package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(16)
public abstract class MediaCodecTrackRenderer extends SampleSourceTrackRenderer {
    private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000;
    private static final int RECONFIGURATION_STATE_NONE = 0;
    private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
    private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    protected static final int SOURCE_STATE_NOT_READY = 0;
    protected static final int SOURCE_STATE_READY = 1;
    protected static final int SOURCE_STATE_READY_READ_MAY_FAIL = 2;
    private MediaCodec codec;
    public final CodecCounters codecCounters;
    private long codecHotswapTimeMs;
    private boolean codecIsAdaptive;
    private boolean codecNeedsDiscardToSpsWorkaround;
    private boolean codecNeedsEosFlushWorkaround;
    private boolean codecNeedsEosPropagationWorkaround;
    private boolean codecNeedsFlushWorkaround;
    private boolean codecNeedsMonoChannelCountWorkaround;
    private boolean codecReceivedBuffers;
    private boolean codecReceivedEos;
    private int codecReconfigurationState;
    private boolean codecReconfigured;
    private int codecReinitializationState;
    private final List<Long> decodeOnlyPresentationTimestamps;
    private final boolean deviceNeedsAutoFrcWorkaround;
    private DrmInitData drmInitData;
    private final DrmSessionManager drmSessionManager;
    protected final Handler eventHandler;
    private final EventListener eventListener;
    private MediaFormat format;
    private final MediaFormatHolder formatHolder;
    private ByteBuffer[] inputBuffers;
    private int inputIndex;
    private boolean inputStreamEnded;
    private final MediaCodecSelector mediaCodecSelector;
    private boolean openedDrmSession;
    private final BufferInfo outputBufferInfo;
    private ByteBuffer[] outputBuffers;
    private int outputIndex;
    private boolean outputStreamEnded;
    private final boolean playClearSamplesWithoutKeys;
    private final SampleHolder sampleHolder;
    private int sourceState;
    private boolean waitingForFirstSyncFrame;
    private boolean waitingForKeys;

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.1 */
    class C07531 implements Runnable {
        final /* synthetic */ DecoderInitializationException val$e;

        C07531(DecoderInitializationException decoderInitializationException) {
            this.val$e = decoderInitializationException;
        }

        public void run() {
            MediaCodecTrackRenderer.this.eventListener.onDecoderInitializationError(this.val$e);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.2 */
    class C07542 implements Runnable {
        final /* synthetic */ CryptoException val$e;

        C07542(CryptoException cryptoException) {
            this.val$e = cryptoException;
        }

        public void run() {
            MediaCodecTrackRenderer.this.eventListener.onCryptoError(this.val$e);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.3 */
    class C07553 implements Runnable {
        final /* synthetic */ String val$decoderName;
        final /* synthetic */ long val$initializationDuration;
        final /* synthetic */ long val$initializedTimestamp;

        C07553(String str, long j, long j2) {
            this.val$decoderName = str;
            this.val$initializedTimestamp = j;
            this.val$initializationDuration = j2;
        }

        public void run() {
            MediaCodecTrackRenderer.this.eventListener.onDecoderInitialized(this.val$decoderName, this.val$initializedTimestamp, this.val$initializationDuration);
        }
    }

    public static class DecoderInitializationException extends Exception {
        private static final int CUSTOM_ERROR_CODE_BASE = -50000;
        private static final int DECODER_QUERY_ERROR = -49998;
        private static final int NO_SUITABLE_DECODER_ERROR = -49999;
        public final String decoderName;
        public final String diagnosticInfo;
        public final String mimeType;
        public final boolean secureDecoderRequired;

        public DecoderInitializationException(MediaFormat mediaFormat, Throwable cause, boolean secureDecoderRequired, int errorCode) {
            super("Decoder init failed: [" + errorCode + "], " + mediaFormat, cause);
            this.mimeType = mediaFormat.mimeType;
            this.secureDecoderRequired = secureDecoderRequired;
            this.decoderName = null;
            this.diagnosticInfo = buildCustomDiagnosticInfo(errorCode);
        }

        public DecoderInitializationException(MediaFormat mediaFormat, Throwable cause, boolean secureDecoderRequired, String decoderName) {
            super("Decoder init failed: " + decoderName + ", " + mediaFormat, cause);
            this.mimeType = mediaFormat.mimeType;
            this.secureDecoderRequired = secureDecoderRequired;
            this.decoderName = decoderName;
            this.diagnosticInfo = Util.SDK_INT >= 21 ? getDiagnosticInfoV21(cause) : null;
        }

        @TargetApi(21)
        private static String getDiagnosticInfoV21(Throwable cause) {
            if (cause instanceof CodecException) {
                return ((CodecException) cause).getDiagnosticInfo();
            }
            return null;
        }

        private static String buildCustomDiagnosticInfo(int errorCode) {
            return "org.telegram.messenger.exoplayer.MediaCodecTrackRenderer_" + (errorCode < 0 ? "neg_" : TtmlNode.ANONYMOUS_REGION_ID) + Math.abs(errorCode);
        }
    }

    public interface EventListener {
        void onCryptoError(CryptoException cryptoException);

        void onDecoderInitializationError(DecoderInitializationException decoderInitializationException);

        void onDecoderInitialized(String str, long j, long j2);
    }

    protected abstract void configureCodec(MediaCodec mediaCodec, boolean z, MediaFormat mediaFormat, MediaCrypto mediaCrypto);

    protected abstract boolean handlesTrack(MediaCodecSelector mediaCodecSelector, MediaFormat mediaFormat) throws DecoderQueryException;

    protected abstract boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, BufferInfo bufferInfo, int i, boolean z) throws ExoPlaybackException;

    public MediaCodecTrackRenderer(SampleSource source, MediaCodecSelector mediaCodecSelector, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener) {
        SampleSource[] sampleSourceArr = new SampleSource[SOURCE_STATE_READY];
        sampleSourceArr[SOURCE_STATE_NOT_READY] = source;
        this(sampleSourceArr, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener);
    }

    public MediaCodecTrackRenderer(SampleSource[] sources, MediaCodecSelector mediaCodecSelector, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener) {
        super(sources);
        Assertions.checkState(Util.SDK_INT >= 16);
        this.mediaCodecSelector = (MediaCodecSelector) Assertions.checkNotNull(mediaCodecSelector);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
        this.codecCounters = new CodecCounters();
        this.sampleHolder = new SampleHolder(SOURCE_STATE_NOT_READY);
        this.formatHolder = new MediaFormatHolder();
        this.decodeOnlyPresentationTimestamps = new ArrayList();
        this.outputBufferInfo = new BufferInfo();
        this.codecReconfigurationState = SOURCE_STATE_NOT_READY;
        this.codecReinitializationState = SOURCE_STATE_NOT_READY;
    }

    protected final boolean handlesTrack(MediaFormat mediaFormat) throws DecoderQueryException {
        return handlesTrack(this.mediaCodecSelector, mediaFormat);
    }

    protected DecoderInfo getDecoderInfo(MediaCodecSelector mediaCodecSelector, String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
        return mediaCodecSelector.getDecoderInfo(mimeType, requiresSecureDecoder);
    }

    protected final void maybeInitCodec() throws ExoPlaybackException {
        if (shouldInitCodec()) {
            String mimeType = this.format.mimeType;
            MediaCrypto mediaCrypto = null;
            boolean requiresSecureDecoder = false;
            if (this.drmInitData != null) {
                if (this.drmSessionManager == null) {
                    throw new ExoPlaybackException("Media requires a DrmSessionManager");
                }
                if (!this.openedDrmSession) {
                    this.drmSessionManager.open(this.drmInitData);
                    this.openedDrmSession = true;
                }
                int drmSessionState = this.drmSessionManager.getState();
                if (drmSessionState == 0) {
                    throw new ExoPlaybackException(this.drmSessionManager.getError());
                } else if (drmSessionState == 3 || drmSessionState == 4) {
                    mediaCrypto = this.drmSessionManager.getMediaCrypto();
                    requiresSecureDecoder = this.drmSessionManager.requiresSecureDecoderComponent(mimeType);
                } else {
                    return;
                }
            }
            DecoderInfo decoderInfo = null;
            try {
                decoderInfo = getDecoderInfo(this.mediaCodecSelector, mimeType, requiresSecureDecoder);
            } catch (Throwable e) {
                notifyAndThrowDecoderInitError(new DecoderInitializationException(this.format, e, requiresSecureDecoder, -49998));
            }
            if (decoderInfo == null) {
                notifyAndThrowDecoderInitError(new DecoderInitializationException(this.format, null, requiresSecureDecoder, -49999));
            }
            String codecName = decoderInfo.name;
            this.codecIsAdaptive = decoderInfo.adaptive;
            this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround(codecName, this.format);
            this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround(codecName);
            this.codecNeedsEosPropagationWorkaround = codecNeedsEosPropagationWorkaround(codecName);
            this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround(codecName);
            this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround(codecName, this.format);
            try {
                long codecInitializingTimestamp = SystemClock.elapsedRealtime();
                TraceUtil.beginSection("createByCodecName(" + codecName + ")");
                this.codec = MediaCodec.createByCodecName(codecName);
                TraceUtil.endSection();
                TraceUtil.beginSection("configureCodec");
                configureCodec(this.codec, decoderInfo.adaptive, getFrameworkMediaFormat(this.format), mediaCrypto);
                TraceUtil.endSection();
                TraceUtil.beginSection("codec.start()");
                this.codec.start();
                TraceUtil.endSection();
                long codecInitializedTimestamp = SystemClock.elapsedRealtime();
                notifyDecoderInitialized(codecName, codecInitializedTimestamp, codecInitializedTimestamp - codecInitializingTimestamp);
                this.inputBuffers = this.codec.getInputBuffers();
                this.outputBuffers = this.codec.getOutputBuffers();
            } catch (Throwable e2) {
                notifyAndThrowDecoderInitError(new DecoderInitializationException(this.format, e2, requiresSecureDecoder, codecName));
            }
            this.codecHotswapTimeMs = getState() == 3 ? SystemClock.elapsedRealtime() : -1;
            this.inputIndex = -1;
            this.outputIndex = -1;
            this.waitingForFirstSyncFrame = true;
            CodecCounters codecCounters = this.codecCounters;
            codecCounters.codecInitCount += SOURCE_STATE_READY;
        }
    }

    private void notifyAndThrowDecoderInitError(DecoderInitializationException e) throws ExoPlaybackException {
        notifyDecoderInitializationError(e);
        throw new ExoPlaybackException((Throwable) e);
    }

    protected boolean shouldInitCodec() {
        return this.codec == null && this.format != null;
    }

    protected final boolean codecInitialized() {
        return this.codec != null;
    }

    protected final boolean haveFormat() {
        return this.format != null;
    }

    protected void onDisabled() throws ExoPlaybackException {
        this.format = null;
        this.drmInitData = null;
        try {
            releaseCodec();
            try {
                if (this.openedDrmSession) {
                    this.drmSessionManager.close();
                    this.openedDrmSession = false;
                }
                super.onDisabled();
            } catch (Throwable th) {
                super.onDisabled();
            }
        } catch (Throwable th2) {
            super.onDisabled();
        }
    }

    protected void releaseCodec() {
        if (this.codec != null) {
            this.codecHotswapTimeMs = -1;
            this.inputIndex = -1;
            this.outputIndex = -1;
            this.waitingForKeys = false;
            this.decodeOnlyPresentationTimestamps.clear();
            this.inputBuffers = null;
            this.outputBuffers = null;
            this.codecReconfigured = false;
            this.codecReceivedBuffers = false;
            this.codecIsAdaptive = false;
            this.codecNeedsDiscardToSpsWorkaround = false;
            this.codecNeedsFlushWorkaround = false;
            this.codecNeedsEosPropagationWorkaround = false;
            this.codecNeedsEosFlushWorkaround = false;
            this.codecNeedsMonoChannelCountWorkaround = false;
            this.codecReceivedEos = false;
            this.codecReconfigurationState = SOURCE_STATE_NOT_READY;
            this.codecReinitializationState = SOURCE_STATE_NOT_READY;
            CodecCounters codecCounters = this.codecCounters;
            codecCounters.codecReleaseCount += SOURCE_STATE_READY;
            try {
                this.codec.stop();
                try {
                    this.codec.release();
                } finally {
                    this.codec = null;
                }
            } catch (Throwable th) {
                this.codec.release();
            } finally {
                this.codec = null;
            }
        }
    }

    protected void onDiscontinuity(long positionUs) throws ExoPlaybackException {
        this.sourceState = SOURCE_STATE_NOT_READY;
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.codec != null) {
            flushCodec();
        }
    }

    protected void onStarted() {
    }

    protected void onStopped() {
    }

    protected void doSomeWork(long positionUs, long elapsedRealtimeUs, boolean sourceIsReady) throws ExoPlaybackException {
        int i = sourceIsReady ? this.sourceState == 0 ? SOURCE_STATE_READY : this.sourceState : SOURCE_STATE_NOT_READY;
        this.sourceState = i;
        if (this.format == null) {
            readFormat(positionUs);
        }
        maybeInitCodec();
        if (this.codec != null) {
            TraceUtil.beginSection("drainAndFeed");
            do {
            } while (drainOutputBuffer(positionUs, elapsedRealtimeUs));
            if (feedInputBuffer(positionUs, true)) {
                do {
                } while (feedInputBuffer(positionUs, false));
            }
            TraceUtil.endSection();
        }
        this.codecCounters.ensureUpdated();
    }

    private void readFormat(long positionUs) throws ExoPlaybackException {
        if (readSource(positionUs, this.formatHolder, null) == -4) {
            onInputFormatChanged(this.formatHolder);
        }
    }

    protected void flushCodec() throws ExoPlaybackException {
        this.codecHotswapTimeMs = -1;
        this.inputIndex = -1;
        this.outputIndex = -1;
        this.waitingForFirstSyncFrame = true;
        this.waitingForKeys = false;
        this.decodeOnlyPresentationTimestamps.clear();
        if (this.codecNeedsFlushWorkaround || (this.codecNeedsEosFlushWorkaround && this.codecReceivedEos)) {
            releaseCodec();
            maybeInitCodec();
        } else if (this.codecReinitializationState != 0) {
            releaseCodec();
            maybeInitCodec();
        } else {
            this.codec.flush();
            this.codecReceivedBuffers = false;
        }
        if (this.codecReconfigured && this.format != null) {
            this.codecReconfigurationState = SOURCE_STATE_READY;
        }
    }

    private boolean feedInputBuffer(long positionUs, boolean firstFeed) throws ExoPlaybackException {
        if (this.inputStreamEnded || this.codecReinitializationState == SOURCE_STATE_READY_READ_MAY_FAIL) {
            return false;
        }
        if (this.inputIndex < 0) {
            this.inputIndex = this.codec.dequeueInputBuffer(0);
            if (this.inputIndex < 0) {
                return false;
            }
            this.sampleHolder.data = this.inputBuffers[this.inputIndex];
            this.sampleHolder.clearData();
        }
        if (this.codecReinitializationState == SOURCE_STATE_READY) {
            if (!this.codecNeedsEosPropagationWorkaround) {
                this.codecReceivedEos = true;
                this.codec.queueInputBuffer(this.inputIndex, SOURCE_STATE_NOT_READY, SOURCE_STATE_NOT_READY, 0, 4);
                this.inputIndex = -1;
            }
            this.codecReinitializationState = SOURCE_STATE_READY_READ_MAY_FAIL;
            return false;
        }
        int result;
        if (this.waitingForKeys) {
            result = -3;
        } else {
            if (this.codecReconfigurationState == SOURCE_STATE_READY) {
                for (int i = SOURCE_STATE_NOT_READY; i < this.format.initializationData.size(); i += SOURCE_STATE_READY) {
                    this.sampleHolder.data.put((byte[]) this.format.initializationData.get(i));
                }
                this.codecReconfigurationState = SOURCE_STATE_READY_READ_MAY_FAIL;
            }
            result = readSource(positionUs, this.formatHolder, this.sampleHolder);
            if (firstFeed && this.sourceState == SOURCE_STATE_READY && result == -2) {
                this.sourceState = SOURCE_STATE_READY_READ_MAY_FAIL;
            }
        }
        if (result == -2) {
            return false;
        }
        if (result == -4) {
            if (this.codecReconfigurationState == SOURCE_STATE_READY_READ_MAY_FAIL) {
                this.sampleHolder.clearData();
                this.codecReconfigurationState = SOURCE_STATE_READY;
            }
            onInputFormatChanged(this.formatHolder);
            return true;
        } else if (result == -1) {
            if (this.codecReconfigurationState == SOURCE_STATE_READY_READ_MAY_FAIL) {
                this.sampleHolder.clearData();
                this.codecReconfigurationState = SOURCE_STATE_READY;
            }
            this.inputStreamEnded = true;
            if (this.codecReceivedBuffers) {
                try {
                    if (!this.codecNeedsEosPropagationWorkaround) {
                        this.codecReceivedEos = true;
                        this.codec.queueInputBuffer(this.inputIndex, SOURCE_STATE_NOT_READY, SOURCE_STATE_NOT_READY, 0, 4);
                        this.inputIndex = -1;
                    }
                    return false;
                } catch (Throwable e) {
                    notifyCryptoError(e);
                    throw new ExoPlaybackException(e);
                }
            }
            processEndOfStream();
            return false;
        } else {
            if (this.waitingForFirstSyncFrame) {
                if (this.sampleHolder.isSyncFrame()) {
                    this.waitingForFirstSyncFrame = false;
                } else {
                    this.sampleHolder.clearData();
                    if (this.codecReconfigurationState == SOURCE_STATE_READY_READ_MAY_FAIL) {
                        this.codecReconfigurationState = SOURCE_STATE_READY;
                    }
                    return true;
                }
            }
            boolean sampleEncrypted = this.sampleHolder.isEncrypted();
            this.waitingForKeys = shouldWaitForKeys(sampleEncrypted);
            if (this.waitingForKeys) {
                return false;
            }
            if (this.codecNeedsDiscardToSpsWorkaround && !sampleEncrypted) {
                NalUnitUtil.discardToSps(this.sampleHolder.data);
                if (this.sampleHolder.data.position() == 0) {
                    return true;
                }
                this.codecNeedsDiscardToSpsWorkaround = false;
            }
            try {
                int bufferSize = this.sampleHolder.data.position();
                int adaptiveReconfigurationBytes = bufferSize - this.sampleHolder.size;
                long presentationTimeUs = this.sampleHolder.timeUs;
                if (this.sampleHolder.isDecodeOnly()) {
                    this.decodeOnlyPresentationTimestamps.add(Long.valueOf(presentationTimeUs));
                }
                onQueuedInputBuffer(presentationTimeUs, this.sampleHolder.data, bufferSize, sampleEncrypted);
                if (sampleEncrypted) {
                    this.codec.queueSecureInputBuffer(this.inputIndex, SOURCE_STATE_NOT_READY, getFrameworkCryptoInfo(this.sampleHolder, adaptiveReconfigurationBytes), presentationTimeUs, SOURCE_STATE_NOT_READY);
                } else {
                    this.codec.queueInputBuffer(this.inputIndex, SOURCE_STATE_NOT_READY, bufferSize, presentationTimeUs, SOURCE_STATE_NOT_READY);
                }
                this.inputIndex = -1;
                this.codecReceivedBuffers = true;
                this.codecReconfigurationState = SOURCE_STATE_NOT_READY;
                CodecCounters codecCounters = this.codecCounters;
                codecCounters.inputBufferCount += SOURCE_STATE_READY;
                return true;
            } catch (Throwable e2) {
                notifyCryptoError(e2);
                throw new ExoPlaybackException(e2);
            }
        }
    }

    private static CryptoInfo getFrameworkCryptoInfo(SampleHolder sampleHolder, int adaptiveReconfigurationBytes) {
        CryptoInfo cryptoInfo = sampleHolder.cryptoInfo.getFrameworkCryptoInfoV16();
        if (adaptiveReconfigurationBytes != 0) {
            if (cryptoInfo.numBytesOfClearData == null) {
                cryptoInfo.numBytesOfClearData = new int[SOURCE_STATE_READY];
            }
            int[] iArr = cryptoInfo.numBytesOfClearData;
            iArr[SOURCE_STATE_NOT_READY] = iArr[SOURCE_STATE_NOT_READY] + adaptiveReconfigurationBytes;
        }
        return cryptoInfo;
    }

    private MediaFormat getFrameworkMediaFormat(MediaFormat format) {
        MediaFormat mediaFormat = format.getFrameworkMediaFormatV16();
        if (this.deviceNeedsAutoFrcWorkaround) {
            mediaFormat.setInteger("auto-frc", SOURCE_STATE_NOT_READY);
        }
        return mediaFormat;
    }

    private boolean shouldWaitForKeys(boolean sampleEncrypted) throws ExoPlaybackException {
        if (!this.openedDrmSession) {
            return false;
        }
        int drmManagerState = this.drmSessionManager.getState();
        if (drmManagerState == 0) {
            throw new ExoPlaybackException(this.drmSessionManager.getError());
        } else if (drmManagerState == 4) {
            return false;
        } else {
            if (sampleEncrypted || !this.playClearSamplesWithoutKeys) {
                return true;
            }
            return false;
        }
    }

    protected void onInputFormatChanged(MediaFormatHolder formatHolder) throws ExoPlaybackException {
        MediaFormat oldFormat = this.format;
        this.format = formatHolder.format;
        this.drmInitData = formatHolder.drmInitData;
        if (this.codec != null && canReconfigureCodec(this.codec, this.codecIsAdaptive, oldFormat, this.format)) {
            this.codecReconfigured = true;
            this.codecReconfigurationState = SOURCE_STATE_READY;
        } else if (this.codecReceivedBuffers) {
            this.codecReinitializationState = SOURCE_STATE_READY;
        } else {
            releaseCodec();
            maybeInitCodec();
        }
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) throws ExoPlaybackException {
    }

    protected void onOutputStreamEnded() {
    }

    protected void onQueuedInputBuffer(long presentationTimeUs, ByteBuffer buffer, int bufferSize, boolean sampleEncrypted) {
    }

    protected void onProcessedOutputBuffer(long presentationTimeUs) {
    }

    protected boolean canReconfigureCodec(MediaCodec codec, boolean codecIsAdaptive, MediaFormat oldFormat, MediaFormat newFormat) {
        return false;
    }

    protected boolean isEnded() {
        return this.outputStreamEnded;
    }

    protected boolean isReady() {
        return (this.format == null || this.waitingForKeys || (this.sourceState == 0 && this.outputIndex < 0 && !isWithinHotswapPeriod())) ? false : true;
    }

    protected final int getSourceState() {
        return this.sourceState;
    }

    private boolean isWithinHotswapPeriod() {
        return SystemClock.elapsedRealtime() < this.codecHotswapTimeMs + MAX_CODEC_HOTSWAP_TIME_MS;
    }

    protected long getDequeueOutputBufferTimeoutUs() {
        return 0;
    }

    private boolean drainOutputBuffer(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        if (this.outputStreamEnded) {
            return false;
        }
        if (this.outputIndex < 0) {
            this.outputIndex = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
        }
        if (this.outputIndex == -2) {
            processOutputFormat();
            return true;
        } else if (this.outputIndex == -3) {
            this.outputBuffers = this.codec.getOutputBuffers();
            CodecCounters codecCounters = this.codecCounters;
            codecCounters.outputBuffersChangedCount += SOURCE_STATE_READY;
            return true;
        } else if (this.outputIndex < 0) {
            if (!this.codecNeedsEosPropagationWorkaround || (!this.inputStreamEnded && this.codecReinitializationState != SOURCE_STATE_READY_READ_MAY_FAIL)) {
                return false;
            }
            processEndOfStream();
            return true;
        } else if ((this.outputBufferInfo.flags & 4) != 0) {
            processEndOfStream();
            return false;
        } else {
            int decodeOnlyIndex = getDecodeOnlyIndex(this.outputBufferInfo.presentationTimeUs);
            if (!processOutputBuffer(positionUs, elapsedRealtimeUs, this.codec, this.outputBuffers[this.outputIndex], this.outputBufferInfo, this.outputIndex, decodeOnlyIndex != -1)) {
                return false;
            }
            onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
            if (decodeOnlyIndex != -1) {
                this.decodeOnlyPresentationTimestamps.remove(decodeOnlyIndex);
            }
            this.outputIndex = -1;
            return true;
        }
    }

    private void processOutputFormat() throws ExoPlaybackException {
        MediaFormat format = this.codec.getOutputFormat();
        if (this.codecNeedsMonoChannelCountWorkaround) {
            format.setInteger("channel-count", SOURCE_STATE_READY);
        }
        onOutputFormatChanged(this.codec, format);
        CodecCounters codecCounters = this.codecCounters;
        codecCounters.outputFormatChangedCount += SOURCE_STATE_READY;
    }

    private void processEndOfStream() throws ExoPlaybackException {
        if (this.codecReinitializationState == SOURCE_STATE_READY_READ_MAY_FAIL) {
            releaseCodec();
            maybeInitCodec();
            return;
        }
        this.outputStreamEnded = true;
        onOutputStreamEnded();
    }

    private void notifyDecoderInitializationError(DecoderInitializationException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07531(e));
        }
    }

    private void notifyCryptoError(CryptoException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07542(e));
        }
    }

    private void notifyDecoderInitialized(String decoderName, long initializedTimestamp, long initializationDuration) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07553(decoderName, initializedTimestamp, initializationDuration));
        }
    }

    private int getDecodeOnlyIndex(long presentationTimeUs) {
        int size = this.decodeOnlyPresentationTimestamps.size();
        for (int i = SOURCE_STATE_NOT_READY; i < size; i += SOURCE_STATE_READY) {
            if (((Long) this.decodeOnlyPresentationTimestamps.get(i)).longValue() == presentationTimeUs) {
                return i;
            }
        }
        return -1;
    }

    private static boolean codecNeedsFlushWorkaround(String name) {
        return Util.SDK_INT < 18 || ((Util.SDK_INT == 18 && ("OMX.SEC.avc.dec".equals(name) || "OMX.SEC.avc.dec.secure".equals(name))) || (Util.SDK_INT == 19 && Util.MODEL.startsWith("SM-G800") && ("OMX.Exynos.avc.dec".equals(name) || "OMX.Exynos.avc.dec.secure".equals(name))));
    }

    private static boolean codecNeedsDiscardToSpsWorkaround(String name, MediaFormat format) {
        return Util.SDK_INT < 21 && format.initializationData.isEmpty() && "OMX.MTK.VIDEO.DECODER.AVC".equals(name);
    }

    private static boolean codecNeedsEosPropagationWorkaround(String name) {
        return Util.SDK_INT <= 17 && ("OMX.rk.video_decoder.avc".equals(name) || "OMX.allwinner.video.decoder.avc".equals(name));
    }

    private static boolean codecNeedsEosFlushWorkaround(String name) {
        return Util.SDK_INT <= 23 && "OMX.google.vorbis.decoder".equals(name);
    }

    private static boolean codecNeedsMonoChannelCountWorkaround(String name, MediaFormat format) {
        return Util.SDK_INT <= 18 && format.channelCount == SOURCE_STATE_READY && "OMX.MTK.AUDIO.DECODER.MP3".equals(name);
    }

    private static boolean deviceNeedsAutoFrcWorkaround() {
        return Util.SDK_INT <= 22 && "foster".equals(Util.DEVICE) && "NVIDIA".equals(Util.MANUFACTURER);
    }
}
