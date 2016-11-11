package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer.audio.AudioTrack;
import org.telegram.messenger.exoplayer.audio.AudioTrack.InitializationException;
import org.telegram.messenger.exoplayer.audio.AudioTrack.WriteException;
import org.telegram.messenger.exoplayer.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer.util.MimeTypes;

@TargetApi(16)
public class MediaCodecAudioTrackRenderer extends MediaCodecTrackRenderer implements MediaClock {
    public static final int MSG_SET_PLAYBACK_PARAMS = 2;
    public static final int MSG_SET_VOLUME = 1;
    private boolean allowPositionDiscontinuity;
    private int audioSessionId;
    private final AudioTrack audioTrack;
    private boolean audioTrackHasData;
    private long currentPositionUs;
    private final EventListener eventListener;
    private long lastFeedElapsedRealtimeMs;
    private boolean passthroughEnabled;
    private MediaFormat passthroughMediaFormat;
    private int pcmEncoding;

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer.1 */
    class C07501 implements Runnable {
        final /* synthetic */ InitializationException val$e;

        C07501(InitializationException initializationException) {
            this.val$e = initializationException;
        }

        public void run() {
            MediaCodecAudioTrackRenderer.this.eventListener.onAudioTrackInitializationError(this.val$e);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer.2 */
    class C07512 implements Runnable {
        final /* synthetic */ WriteException val$e;

        C07512(WriteException writeException) {
            this.val$e = writeException;
        }

        public void run() {
            MediaCodecAudioTrackRenderer.this.eventListener.onAudioTrackWriteError(this.val$e);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer.3 */
    class C07523 implements Runnable {
        final /* synthetic */ int val$bufferSize;
        final /* synthetic */ long val$bufferSizeMs;
        final /* synthetic */ long val$elapsedSinceLastFeedMs;

        C07523(int i, long j, long j2) {
            this.val$bufferSize = i;
            this.val$bufferSizeMs = j;
            this.val$elapsedSinceLastFeedMs = j2;
        }

        public void run() {
            MediaCodecAudioTrackRenderer.this.eventListener.onAudioTrackUnderrun(this.val$bufferSize, this.val$bufferSizeMs, this.val$elapsedSinceLastFeedMs);
        }
    }

    public interface EventListener extends org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.EventListener {
        void onAudioTrackInitializationError(InitializationException initializationException);

        void onAudioTrackUnderrun(int i, long j, long j2);

        void onAudioTrackWriteError(WriteException writeException);
    }

    public MediaCodecAudioTrackRenderer(SampleSource source, MediaCodecSelector mediaCodecSelector) {
        this(source, mediaCodecSelector, null, true);
    }

    public MediaCodecAudioTrackRenderer(SampleSource source, MediaCodecSelector mediaCodecSelector, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys) {
        this(source, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, null, null);
    }

    public MediaCodecAudioTrackRenderer(SampleSource source, MediaCodecSelector mediaCodecSelector, Handler eventHandler, EventListener eventListener) {
        this(source, mediaCodecSelector, null, true, eventHandler, eventListener);
    }

    public MediaCodecAudioTrackRenderer(SampleSource source, MediaCodecSelector mediaCodecSelector, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener) {
        this(source, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, null, 3);
    }

    public MediaCodecAudioTrackRenderer(SampleSource source, MediaCodecSelector mediaCodecSelector, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener, AudioCapabilities audioCapabilities, int streamType) {
        SampleSource[] sampleSourceArr = new SampleSource[MSG_SET_VOLUME];
        sampleSourceArr[0] = source;
        this(sampleSourceArr, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, audioCapabilities, streamType);
    }

    public MediaCodecAudioTrackRenderer(SampleSource[] sources, MediaCodecSelector mediaCodecSelector, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener, AudioCapabilities audioCapabilities, int streamType) {
        super(sources, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, (org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.EventListener) eventListener);
        this.eventListener = eventListener;
        this.audioSessionId = 0;
        this.audioTrack = new AudioTrack(audioCapabilities, streamType);
    }

    protected boolean handlesTrack(MediaCodecSelector mediaCodecSelector, MediaFormat mediaFormat) throws DecoderQueryException {
        String mimeType = mediaFormat.mimeType;
        if (!MimeTypes.isAudio(mimeType)) {
            return false;
        }
        if (MimeTypes.AUDIO_UNKNOWN.equals(mimeType) || ((allowPassthrough(mimeType) && mediaCodecSelector.getPassthroughDecoderInfo() != null) || mediaCodecSelector.getDecoderInfo(mimeType, false) != null)) {
            return true;
        }
        return false;
    }

    protected DecoderInfo getDecoderInfo(MediaCodecSelector mediaCodecSelector, String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
        if (allowPassthrough(mimeType)) {
            DecoderInfo passthroughDecoderInfo = mediaCodecSelector.getPassthroughDecoderInfo();
            if (passthroughDecoderInfo != null) {
                this.passthroughEnabled = true;
                return passthroughDecoderInfo;
            }
        }
        this.passthroughEnabled = false;
        return super.getDecoderInfo(mediaCodecSelector, mimeType, requiresSecureDecoder);
    }

    protected boolean allowPassthrough(String mimeType) {
        return this.audioTrack.isPassthroughSupported(mimeType);
    }

    protected void configureCodec(MediaCodec codec, boolean codecIsAdaptive, MediaFormat format, MediaCrypto crypto) {
        String mimeType = format.getString("mime");
        if (this.passthroughEnabled) {
            format.setString("mime", MimeTypes.AUDIO_RAW);
            codec.configure(format, null, crypto, 0);
            format.setString("mime", mimeType);
            this.passthroughMediaFormat = format;
            return;
        }
        codec.configure(format, null, crypto, 0);
        this.passthroughMediaFormat = null;
    }

    protected MediaClock getMediaClock() {
        return this;
    }

    protected void onInputFormatChanged(MediaFormatHolder holder) throws ExoPlaybackException {
        super.onInputFormatChanged(holder);
        this.pcmEncoding = MimeTypes.AUDIO_RAW.equals(holder.format.mimeType) ? holder.format.pcmEncoding : MSG_SET_PLAYBACK_PARAMS;
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {
        MediaFormat format;
        boolean passthrough = this.passthroughMediaFormat != null;
        String mimeType = passthrough ? this.passthroughMediaFormat.getString("mime") : MimeTypes.AUDIO_RAW;
        if (passthrough) {
            format = this.passthroughMediaFormat;
        } else {
            format = outputFormat;
        }
        this.audioTrack.configure(mimeType, format.getInteger("channel-count"), format.getInteger("sample-rate"), this.pcmEncoding);
    }

    protected void onAudioSessionId(int audioSessionId) {
    }

    protected void onStarted() {
        super.onStarted();
        this.audioTrack.play();
    }

    protected void onStopped() {
        this.audioTrack.pause();
        super.onStopped();
    }

    protected boolean isEnded() {
        return super.isEnded() && !this.audioTrack.hasPendingData();
    }

    protected boolean isReady() {
        return this.audioTrack.hasPendingData() || super.isReady();
    }

    public long getPositionUs() {
        long newCurrentPositionUs = this.audioTrack.getCurrentPositionUs(isEnded());
        if (newCurrentPositionUs != Long.MIN_VALUE) {
            if (!this.allowPositionDiscontinuity) {
                newCurrentPositionUs = Math.max(this.currentPositionUs, newCurrentPositionUs);
            }
            this.currentPositionUs = newCurrentPositionUs;
            this.allowPositionDiscontinuity = false;
        }
        return this.currentPositionUs;
    }

    protected void onDisabled() throws ExoPlaybackException {
        this.audioSessionId = 0;
        try {
            this.audioTrack.release();
        } finally {
            super.onDisabled();
        }
    }

    protected void onDiscontinuity(long positionUs) throws ExoPlaybackException {
        super.onDiscontinuity(positionUs);
        this.audioTrack.reset();
        this.currentPositionUs = positionUs;
        this.allowPositionDiscontinuity = true;
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, BufferInfo bufferInfo, int bufferIndex, boolean shouldSkip) throws ExoPlaybackException {
        if (this.passthroughEnabled && (bufferInfo.flags & MSG_SET_PLAYBACK_PARAMS) != 0) {
            codec.releaseOutputBuffer(bufferIndex, false);
            return true;
        } else if (shouldSkip) {
            codec.releaseOutputBuffer(bufferIndex, false);
            r2 = this.codecCounters;
            r2.skippedOutputBufferCount += MSG_SET_VOLUME;
            this.audioTrack.handleDiscontinuity();
            return true;
        } else {
            if (this.audioTrack.isInitialized()) {
                boolean audioTrackHadData = this.audioTrackHasData;
                this.audioTrackHasData = this.audioTrack.hasPendingData();
                if (audioTrackHadData && !this.audioTrackHasData && getState() == 3) {
                    long elapsedSinceLastFeedMs = SystemClock.elapsedRealtime() - this.lastFeedElapsedRealtimeMs;
                    long bufferSizeUs = this.audioTrack.getBufferSizeUs();
                    notifyAudioTrackUnderrun(this.audioTrack.getBufferSize(), bufferSizeUs == -1 ? -1 : bufferSizeUs / 1000, elapsedSinceLastFeedMs);
                }
            } else {
                try {
                    if (this.audioSessionId != 0) {
                        this.audioTrack.initialize(this.audioSessionId);
                    } else {
                        this.audioSessionId = this.audioTrack.initialize();
                        onAudioSessionId(this.audioSessionId);
                    }
                    this.audioTrackHasData = false;
                    if (getState() == 3) {
                        this.audioTrack.play();
                    }
                } catch (Throwable e) {
                    notifyAudioTrackInitializationError(e);
                    throw new ExoPlaybackException(e);
                }
            }
            try {
                int handleBufferResult = this.audioTrack.handleBuffer(buffer, bufferInfo.offset, bufferInfo.size, bufferInfo.presentationTimeUs);
                this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
                if ((handleBufferResult & MSG_SET_VOLUME) != 0) {
                    handleAudioTrackDiscontinuity();
                    this.allowPositionDiscontinuity = true;
                }
                if ((handleBufferResult & MSG_SET_PLAYBACK_PARAMS) == 0) {
                    return false;
                }
                codec.releaseOutputBuffer(bufferIndex, false);
                r2 = this.codecCounters;
                r2.renderedOutputBufferCount += MSG_SET_VOLUME;
                return true;
            } catch (Throwable e2) {
                notifyAudioTrackWriteError(e2);
                throw new ExoPlaybackException(e2);
            }
        }
    }

    protected void onOutputStreamEnded() {
        this.audioTrack.handleEndOfStream();
    }

    protected void handleAudioTrackDiscontinuity() {
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        switch (messageType) {
            case MSG_SET_VOLUME /*1*/:
                this.audioTrack.setVolume(((Float) message).floatValue());
            case MSG_SET_PLAYBACK_PARAMS /*2*/:
                this.audioTrack.setPlaybackParams((PlaybackParams) message);
            default:
                super.handleMessage(messageType, message);
        }
    }

    private void notifyAudioTrackInitializationError(InitializationException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07501(e));
        }
    }

    private void notifyAudioTrackWriteError(WriteException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07512(e));
        }
    }

    private void notifyAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07523(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs));
        }
    }
}
