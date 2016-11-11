package org.telegram.messenger.exoplayer.audio;

import android.annotation.TargetApi;
import android.media.AudioTimestamp;
import android.media.PlaybackParams;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.exoplayer.util.Ac3Util;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.DtsUtil;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public final class AudioTrack {
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
    public static final long CURRENT_POSITION_NOT_SET = Long.MIN_VALUE;
    private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000;
    private static final long MAX_BUFFER_DURATION_US = 750000;
    private static final long MAX_LATENCY_US = 5000000;
    private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
    private static final long MIN_BUFFER_DURATION_US = 250000;
    private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
    private static final int MIN_TIMESTAMP_SAMPLE_INTERVAL_US = 500000;
    private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000;
    public static final int RESULT_BUFFER_CONSUMED = 2;
    public static final int RESULT_POSITION_DISCONTINUITY = 1;
    public static final int SESSION_ID_NOT_SET = 0;
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final String TAG = "AudioTrack";
    public static boolean enablePreV21AudioSessionWorkaround;
    public static boolean failOnSpuriousAudioTimestamp;
    private final AudioCapabilities audioCapabilities;
    private boolean audioTimestampSet;
    private android.media.AudioTrack audioTrack;
    private final AudioTrackUtil audioTrackUtil;
    private int bufferBytesRemaining;
    private int bufferSize;
    private long bufferSizeUs;
    private int channelConfig;
    private int framesPerEncodedSample;
    private Method getLatencyMethod;
    private android.media.AudioTrack keepSessionIdAudioTrack;
    private long lastPlayheadSampleTimeUs;
    private long lastTimestampSampleTimeUs;
    private long latencyUs;
    private int nextPlayheadOffsetIndex;
    private boolean passthrough;
    private int pcmFrameSize;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private final ConditionVariable releasingConditionVariable;
    private ByteBuffer resampledBuffer;
    private long resumeSystemTimeUs;
    private int sampleRate;
    private long smoothedPlayheadOffsetUs;
    private int sourceEncoding;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private final int streamType;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private int targetEncoding;
    private byte[] temporaryBuffer;
    private int temporaryBufferOffset;
    private boolean useResampledBuffer;
    private float volume;

    /* renamed from: org.telegram.messenger.exoplayer.audio.AudioTrack.1 */
    class C07631 extends Thread {
        final /* synthetic */ android.media.AudioTrack val$toRelease;

        C07631(android.media.AudioTrack audioTrack) {
            this.val$toRelease = audioTrack;
        }

        public void run() {
            try {
                this.val$toRelease.flush();
                this.val$toRelease.release();
            } finally {
                AudioTrack.this.releasingConditionVariable.open();
            }
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.audio.AudioTrack.2 */
    class C07642 extends Thread {
        final /* synthetic */ android.media.AudioTrack val$toRelease;

        C07642(android.media.AudioTrack audioTrack) {
            this.val$toRelease = audioTrack;
        }

        public void run() {
            this.val$toRelease.release();
        }
    }

    private static class AudioTrackUtil {
        protected android.media.AudioTrack audioTrack;
        private long endPlaybackHeadPosition;
        private long lastRawPlaybackHeadPosition;
        private boolean needsPassthroughWorkaround;
        private long passthroughWorkaroundPauseOffset;
        private long rawPlaybackHeadWrapCount;
        private int sampleRate;
        private long stopPlaybackHeadPosition;
        private long stopTimestampUs;

        private AudioTrackUtil() {
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean needsPassthroughWorkaround) {
            this.audioTrack = audioTrack;
            this.needsPassthroughWorkaround = needsPassthroughWorkaround;
            this.stopTimestampUs = -1;
            this.lastRawPlaybackHeadPosition = 0;
            this.rawPlaybackHeadWrapCount = 0;
            this.passthroughWorkaroundPauseOffset = 0;
            if (audioTrack != null) {
                this.sampleRate = audioTrack.getSampleRate();
            }
        }

        public void handleEndOfStream(long submittedFrames) {
            this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
            this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
            this.endPlaybackHeadPosition = submittedFrames;
            this.audioTrack.stop();
        }

        public void pause() {
            if (this.stopTimestampUs == -1) {
                this.audioTrack.pause();
            }
        }

        public long getPlaybackHeadPosition() {
            if (this.stopTimestampUs != -1) {
                return Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + ((((long) this.sampleRate) * ((SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs)) / C0747C.MICROS_PER_SECOND));
            }
            int state = this.audioTrack.getPlayState();
            if (state == AudioTrack.START_IN_SYNC) {
                return 0;
            }
            long rawPlaybackHeadPosition = 4294967295L & ((long) this.audioTrack.getPlaybackHeadPosition());
            if (this.needsPassthroughWorkaround) {
                if (state == AudioTrack.START_NEED_SYNC && rawPlaybackHeadPosition == 0) {
                    this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
                }
                rawPlaybackHeadPosition += this.passthroughWorkaroundPauseOffset;
            }
            if (this.lastRawPlaybackHeadPosition > rawPlaybackHeadPosition) {
                this.rawPlaybackHeadWrapCount++;
            }
            this.lastRawPlaybackHeadPosition = rawPlaybackHeadPosition;
            return (this.rawPlaybackHeadWrapCount << 32) + rawPlaybackHeadPosition;
        }

        public long getPlaybackHeadPositionUs() {
            return (getPlaybackHeadPosition() * C0747C.MICROS_PER_SECOND) / ((long) this.sampleRate);
        }

        public boolean updateTimestamp() {
            return false;
        }

        public long getTimestampNanoTime() {
            throw new UnsupportedOperationException();
        }

        public long getTimestampFramePosition() {
            throw new UnsupportedOperationException();
        }

        public void setPlaybackParameters(PlaybackParams playbackParams) {
            throw new UnsupportedOperationException();
        }

        public float getPlaybackSpeed() {
            return TouchHelperCallback.ALPHA_FULL;
        }
    }

    public static final class InitializationException extends Exception {
        public final int audioTrackState;

        public InitializationException(int audioTrackState, int sampleRate, int channelConfig, int bufferSize) {
            super("AudioTrack init failed: " + audioTrackState + ", Config(" + sampleRate + ", " + channelConfig + ", " + bufferSize + ")");
            this.audioTrackState = audioTrackState;
        }
    }

    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        public InvalidAudioTrackTimestampException(String message) {
            super(message);
        }
    }

    public static final class WriteException extends Exception {
        public final int errorCode;

        public WriteException(int errorCode) {
            super("AudioTrack write failed: " + errorCode);
            this.errorCode = errorCode;
        }
    }

    @TargetApi(19)
    private static class AudioTrackUtilV19 extends AudioTrackUtil {
        private final AudioTimestamp audioTimestamp;
        private long lastRawTimestampFramePosition;
        private long lastTimestampFramePosition;
        private long rawTimestampFramePositionWrapCount;

        public AudioTrackUtilV19() {
            super();
            this.audioTimestamp = new AudioTimestamp();
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean needsPassthroughWorkaround) {
            super.reconfigure(audioTrack, needsPassthroughWorkaround);
            this.rawTimestampFramePositionWrapCount = 0;
            this.lastRawTimestampFramePosition = 0;
            this.lastTimestampFramePosition = 0;
        }

        public boolean updateTimestamp() {
            boolean updated = this.audioTrack.getTimestamp(this.audioTimestamp);
            if (updated) {
                long rawFramePosition = this.audioTimestamp.framePosition;
                if (this.lastRawTimestampFramePosition > rawFramePosition) {
                    this.rawTimestampFramePositionWrapCount++;
                }
                this.lastRawTimestampFramePosition = rawFramePosition;
                this.lastTimestampFramePosition = (this.rawTimestampFramePositionWrapCount << 32) + rawFramePosition;
            }
            return updated;
        }

        public long getTimestampNanoTime() {
            return this.audioTimestamp.nanoTime;
        }

        public long getTimestampFramePosition() {
            return this.lastTimestampFramePosition;
        }
    }

    @TargetApi(23)
    private static class AudioTrackUtilV23 extends AudioTrackUtilV19 {
        private PlaybackParams playbackParams;
        private float playbackSpeed;

        public AudioTrackUtilV23() {
            this.playbackSpeed = TouchHelperCallback.ALPHA_FULL;
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean needsPassthroughWorkaround) {
            super.reconfigure(audioTrack, needsPassthroughWorkaround);
            maybeApplyPlaybackParams();
        }

        public void setPlaybackParameters(PlaybackParams playbackParams) {
            if (playbackParams == null) {
                playbackParams = new PlaybackParams();
            }
            playbackParams = playbackParams.allowDefaults();
            this.playbackParams = playbackParams;
            this.playbackSpeed = playbackParams.getSpeed();
            maybeApplyPlaybackParams();
        }

        public float getPlaybackSpeed() {
            return this.playbackSpeed;
        }

        private void maybeApplyPlaybackParams() {
            if (this.audioTrack != null && this.playbackParams != null) {
                this.audioTrack.setPlaybackParams(this.playbackParams);
            }
        }
    }

    static {
        enablePreV21AudioSessionWorkaround = false;
        failOnSpuriousAudioTimestamp = false;
    }

    public AudioTrack() {
        this(null, 3);
    }

    public AudioTrack(AudioCapabilities audioCapabilities, int streamType) {
        this.audioCapabilities = audioCapabilities;
        this.streamType = streamType;
        this.releasingConditionVariable = new ConditionVariable(true);
        if (Util.SDK_INT >= 18) {
            try {
                this.getLatencyMethod = android.media.AudioTrack.class.getMethod("getLatency", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
        }
        if (Util.SDK_INT >= 23) {
            this.audioTrackUtil = new AudioTrackUtilV23();
        } else if (Util.SDK_INT >= 19) {
            this.audioTrackUtil = new AudioTrackUtilV19();
        } else {
            this.audioTrackUtil = new AudioTrackUtil();
        }
        this.playheadOffsets = new long[MAX_PLAYHEAD_OFFSET_COUNT];
        this.volume = TouchHelperCallback.ALPHA_FULL;
        this.startMediaTimeState = START_NOT_SET;
    }

    public boolean isPassthroughSupported(String mimeType) {
        return this.audioCapabilities != null && this.audioCapabilities.supportsEncoding(getEncodingForMimeType(mimeType));
    }

    public boolean isInitialized() {
        return this.audioTrack != null;
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        if (!hasCurrentPositionUs()) {
            return CURRENT_POSITION_NOT_SET;
        }
        if (this.audioTrack.getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long systemClockUs = System.nanoTime() / 1000;
        if (this.audioTimestampSet) {
            long framesDiff = durationUsToFrames((long) (((float) (systemClockUs - (this.audioTrackUtil.getTimestampNanoTime() / 1000))) * this.audioTrackUtil.getPlaybackSpeed()));
            return framesToDurationUs(this.audioTrackUtil.getTimestampFramePosition() + framesDiff) + this.startMediaTimeUs;
        }
        long currentPositionUs;
        if (this.playheadOffsetCount == 0) {
            currentPositionUs = this.audioTrackUtil.getPlaybackHeadPositionUs() + this.startMediaTimeUs;
        } else {
            currentPositionUs = (this.smoothedPlayheadOffsetUs + systemClockUs) + this.startMediaTimeUs;
        }
        if (sourceEnded) {
            return currentPositionUs;
        }
        return currentPositionUs - this.latencyUs;
    }

    public void configure(String mimeType, int channelCount, int sampleRate, int pcmEncoding) {
        configure(mimeType, channelCount, sampleRate, pcmEncoding, START_NOT_SET);
    }

    public void configure(String mimeType, int channelCount, int sampleRate, int pcmEncoding, int specifiedBufferSize) {
        int channelConfig;
        int sourceEncoding;
        switch (channelCount) {
            case START_IN_SYNC /*1*/:
                channelConfig = BUFFER_MULTIPLICATION_FACTOR;
                break;
            case START_NEED_SYNC /*2*/:
                channelConfig = 12;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                channelConfig = 28;
                break;
            case BUFFER_MULTIPLICATION_FACTOR /*4*/:
                channelConfig = 204;
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                channelConfig = 220;
                break;
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                channelConfig = 252;
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                channelConfig = 1276;
                break;
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                channelConfig = C0747C.CHANNEL_OUT_7POINT1_SURROUND;
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel count: " + channelCount);
        }
        boolean passthrough = !MimeTypes.AUDIO_RAW.equals(mimeType);
        if (passthrough) {
            sourceEncoding = getEncodingForMimeType(mimeType);
        } else if (pcmEncoding == 3 || pcmEncoding == START_NEED_SYNC || pcmEncoding == Integer.MIN_VALUE || pcmEncoding == 1073741824) {
            sourceEncoding = pcmEncoding;
        } else {
            throw new IllegalArgumentException("Unsupported PCM encoding: " + pcmEncoding);
        }
        if (!isInitialized() || this.sourceEncoding != sourceEncoding || this.sampleRate != sampleRate || this.channelConfig != channelConfig) {
            long j;
            reset();
            this.sourceEncoding = sourceEncoding;
            this.passthrough = passthrough;
            this.sampleRate = sampleRate;
            this.channelConfig = channelConfig;
            if (!passthrough) {
                sourceEncoding = START_NEED_SYNC;
            }
            this.targetEncoding = sourceEncoding;
            this.pcmFrameSize = channelCount * START_NEED_SYNC;
            if (specifiedBufferSize != 0) {
                this.bufferSize = specifiedBufferSize;
            } else if (!passthrough) {
                int minBufferSize = android.media.AudioTrack.getMinBufferSize(sampleRate, channelConfig, this.targetEncoding);
                Assertions.checkState(minBufferSize != -2);
                int multipliedBufferSize = minBufferSize * BUFFER_MULTIPLICATION_FACTOR;
                int minAppBufferSize = ((int) durationUsToFrames(PASSTHROUGH_BUFFER_DURATION_US)) * this.pcmFrameSize;
                int maxAppBufferSize = (int) Math.max((long) minBufferSize, durationUsToFrames(MAX_BUFFER_DURATION_US) * ((long) this.pcmFrameSize));
                if (multipliedBufferSize >= minAppBufferSize) {
                    minAppBufferSize = multipliedBufferSize > maxAppBufferSize ? maxAppBufferSize : multipliedBufferSize;
                }
                this.bufferSize = minAppBufferSize;
            } else if (this.targetEncoding == 5 || this.targetEncoding == 6) {
                this.bufferSize = 20480;
            } else {
                this.bufferSize = 49152;
            }
            if (passthrough) {
                j = -1;
            } else {
                j = framesToDurationUs(pcmBytesToFrames((long) this.bufferSize));
            }
            this.bufferSizeUs = j;
        }
    }

    public int initialize() throws InitializationException {
        return initialize(START_NOT_SET);
    }

    public int initialize(int sessionId) throws InitializationException {
        this.releasingConditionVariable.block();
        if (sessionId == 0) {
            this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, START_IN_SYNC);
        } else {
            this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, START_IN_SYNC, sessionId);
        }
        checkAudioTrackInitialized();
        sessionId = this.audioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround && Util.SDK_INT < 21) {
            if (!(this.keepSessionIdAudioTrack == null || sessionId == this.keepSessionIdAudioTrack.getAudioSessionId())) {
                releaseKeepSessionIdAudioTrack();
            }
            if (this.keepSessionIdAudioTrack == null) {
                this.keepSessionIdAudioTrack = new android.media.AudioTrack(this.streamType, 4000, BUFFER_MULTIPLICATION_FACTOR, START_NEED_SYNC, START_NEED_SYNC, START_NOT_SET, sessionId);
            }
        }
        this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
        setAudioTrackVolume();
        return sessionId;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public long getBufferSizeUs() {
        return this.bufferSizeUs;
    }

    public void play() {
        if (isInitialized()) {
            this.resumeSystemTimeUs = System.nanoTime() / 1000;
            this.audioTrack.play();
        }
    }

    public void handleDiscontinuity() {
        if (this.startMediaTimeState == START_IN_SYNC) {
            this.startMediaTimeState = START_NEED_SYNC;
        }
    }

    public int handleBuffer(ByteBuffer buffer, int offset, int size, long presentationTimeUs) throws WriteException {
        if (size == 0) {
            return START_NEED_SYNC;
        }
        if (needsPassthroughWorkarounds()) {
            if (this.audioTrack.getPlayState() == START_NEED_SYNC) {
                return START_NOT_SET;
            }
            if (this.audioTrack.getPlayState() == START_IN_SYNC && this.audioTrackUtil.getPlaybackHeadPosition() != 0) {
                return START_NOT_SET;
            }
        }
        int result = START_NOT_SET;
        if (this.bufferBytesRemaining == 0) {
            this.useResampledBuffer = this.targetEncoding != this.sourceEncoding;
            if (this.useResampledBuffer) {
                Assertions.checkState(this.targetEncoding == START_NEED_SYNC);
                this.resampledBuffer = resampleTo16BitPcm(buffer, offset, size, this.sourceEncoding, this.resampledBuffer);
                buffer = this.resampledBuffer;
                offset = this.resampledBuffer.position();
                size = this.resampledBuffer.limit();
            }
            this.bufferBytesRemaining = size;
            buffer.position(offset);
            if (this.passthrough && this.framesPerEncodedSample == 0) {
                this.framesPerEncodedSample = getFramesPerEncodedSample(this.targetEncoding, buffer);
            }
            if (this.startMediaTimeState == 0) {
                this.startMediaTimeUs = Math.max(0, presentationTimeUs);
                this.startMediaTimeState = START_IN_SYNC;
            } else {
                long expectedBufferStartTime = this.startMediaTimeUs + framesToDurationUs(getSubmittedFrames());
                if (this.startMediaTimeState == START_IN_SYNC && Math.abs(expectedBufferStartTime - presentationTimeUs) > 200000) {
                    Log.e(TAG, "Discontinuity detected [expected " + expectedBufferStartTime + ", got " + presentationTimeUs + "]");
                    this.startMediaTimeState = START_NEED_SYNC;
                }
                if (this.startMediaTimeState == START_NEED_SYNC) {
                    this.startMediaTimeUs += presentationTimeUs - expectedBufferStartTime;
                    this.startMediaTimeState = START_IN_SYNC;
                    result = START_NOT_SET | START_IN_SYNC;
                }
            }
            if (Util.SDK_INT < 21) {
                if (this.temporaryBuffer == null || this.temporaryBuffer.length < size) {
                    this.temporaryBuffer = new byte[size];
                }
                buffer.get(this.temporaryBuffer, START_NOT_SET, size);
                this.temporaryBufferOffset = START_NOT_SET;
            }
        }
        int bytesWritten = START_NOT_SET;
        if (Util.SDK_INT < 21) {
            int bytesToWrite = this.bufferSize - ((int) (this.submittedPcmBytes - (this.audioTrackUtil.getPlaybackHeadPosition() * ((long) this.pcmFrameSize))));
            if (bytesToWrite > 0) {
                bytesWritten = this.audioTrack.write(this.temporaryBuffer, this.temporaryBufferOffset, Math.min(this.bufferBytesRemaining, bytesToWrite));
                if (bytesWritten >= 0) {
                    this.temporaryBufferOffset += bytesWritten;
                }
            }
        } else {
            ByteBuffer data;
            if (this.useResampledBuffer) {
                data = this.resampledBuffer;
            } else {
                data = buffer;
            }
            bytesWritten = writeNonBlockingV21(this.audioTrack, data, this.bufferBytesRemaining);
        }
        if (bytesWritten < 0) {
            throw new WriteException(bytesWritten);
        }
        this.bufferBytesRemaining -= bytesWritten;
        if (!this.passthrough) {
            this.submittedPcmBytes += (long) bytesWritten;
        }
        if (this.bufferBytesRemaining != 0) {
            return result;
        }
        if (this.passthrough) {
            this.submittedEncodedFrames += (long) this.framesPerEncodedSample;
        }
        return result | START_NEED_SYNC;
    }

    public void handleEndOfStream() {
        if (isInitialized()) {
            this.audioTrackUtil.handleEndOfStream(getSubmittedFrames());
        }
    }

    public boolean hasPendingData() {
        return isInitialized() && (getSubmittedFrames() > this.audioTrackUtil.getPlaybackHeadPosition() || overrideHasPendingData());
    }

    public void setPlaybackParams(PlaybackParams playbackParams) {
        this.audioTrackUtil.setPlaybackParameters(playbackParams);
    }

    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.volume = volume;
            setAudioTrackVolume();
        }
    }

    private void setAudioTrackVolume() {
        if (!isInitialized()) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            setAudioTrackVolumeV21(this.audioTrack, this.volume);
        } else {
            setAudioTrackVolumeV3(this.audioTrack, this.volume);
        }
    }

    public void pause() {
        if (isInitialized()) {
            resetSyncParams();
            this.audioTrackUtil.pause();
        }
    }

    public void reset() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0;
            this.submittedEncodedFrames = 0;
            this.framesPerEncodedSample = START_NOT_SET;
            this.bufferBytesRemaining = START_NOT_SET;
            this.startMediaTimeState = START_NOT_SET;
            this.latencyUs = 0;
            resetSyncParams();
            if (this.audioTrack.getPlayState() == 3) {
                this.audioTrack.pause();
            }
            android.media.AudioTrack toRelease = this.audioTrack;
            this.audioTrack = null;
            this.audioTrackUtil.reconfigure(null, false);
            this.releasingConditionVariable.close();
            new C07631(toRelease).start();
        }
    }

    public void release() {
        reset();
        releaseKeepSessionIdAudioTrack();
    }

    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack != null) {
            android.media.AudioTrack toRelease = this.keepSessionIdAudioTrack;
            this.keepSessionIdAudioTrack = null;
            new C07642(toRelease).start();
        }
    }

    private boolean hasCurrentPositionUs() {
        return isInitialized() && this.startMediaTimeState != 0;
    }

    private void maybeSampleSyncParams() {
        long playbackPositionUs = this.audioTrackUtil.getPlaybackHeadPositionUs();
        if (playbackPositionUs != 0) {
            long systemClockUs = System.nanoTime() / 1000;
            if (systemClockUs - this.lastPlayheadSampleTimeUs >= 30000) {
                this.playheadOffsets[this.nextPlayheadOffsetIndex] = playbackPositionUs - systemClockUs;
                this.nextPlayheadOffsetIndex = (this.nextPlayheadOffsetIndex + START_IN_SYNC) % MAX_PLAYHEAD_OFFSET_COUNT;
                if (this.playheadOffsetCount < MAX_PLAYHEAD_OFFSET_COUNT) {
                    this.playheadOffsetCount += START_IN_SYNC;
                }
                this.lastPlayheadSampleTimeUs = systemClockUs;
                this.smoothedPlayheadOffsetUs = 0;
                for (int i = START_NOT_SET; i < this.playheadOffsetCount; i += START_IN_SYNC) {
                    this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / ((long) this.playheadOffsetCount);
                }
            }
            if (!needsPassthroughWorkarounds() && systemClockUs - this.lastTimestampSampleTimeUs >= 500000) {
                this.audioTimestampSet = this.audioTrackUtil.updateTimestamp();
                if (this.audioTimestampSet) {
                    long audioTimestampUs = this.audioTrackUtil.getTimestampNanoTime() / 1000;
                    long audioTimestampFramePosition = this.audioTrackUtil.getTimestampFramePosition();
                    if (audioTimestampUs < this.resumeSystemTimeUs) {
                        this.audioTimestampSet = false;
                    } else if (Math.abs(audioTimestampUs - systemClockUs) > MAX_LATENCY_US) {
                        message = "Spurious audio timestamp (system clock mismatch): " + audioTimestampFramePosition + ", " + audioTimestampUs + ", " + systemClockUs + ", " + playbackPositionUs;
                        if (failOnSpuriousAudioTimestamp) {
                            throw new InvalidAudioTrackTimestampException(message);
                        }
                        Log.w(TAG, message);
                        this.audioTimestampSet = false;
                    } else if (Math.abs(framesToDurationUs(audioTimestampFramePosition) - playbackPositionUs) > MAX_LATENCY_US) {
                        message = "Spurious audio timestamp (frame position mismatch): " + audioTimestampFramePosition + ", " + audioTimestampUs + ", " + systemClockUs + ", " + playbackPositionUs;
                        if (failOnSpuriousAudioTimestamp) {
                            throw new InvalidAudioTrackTimestampException(message);
                        }
                        Log.w(TAG, message);
                        this.audioTimestampSet = false;
                    }
                }
                if (!(this.getLatencyMethod == null || this.passthrough)) {
                    try {
                        this.latencyUs = (((long) ((Integer) this.getLatencyMethod.invoke(this.audioTrack, (Object[]) null)).intValue()) * 1000) - this.bufferSizeUs;
                        this.latencyUs = Math.max(this.latencyUs, 0);
                        if (this.latencyUs > MAX_LATENCY_US) {
                            Log.w(TAG, "Ignoring impossibly large audio latency: " + this.latencyUs);
                            this.latencyUs = 0;
                        }
                    } catch (Exception e) {
                        this.getLatencyMethod = null;
                    }
                }
                this.lastTimestampSampleTimeUs = systemClockUs;
            }
        }
    }

    private void checkAudioTrackInitialized() throws InitializationException {
        int state = this.audioTrack.getState();
        if (state != START_IN_SYNC) {
            try {
                this.audioTrack.release();
            } catch (Exception e) {
            } finally {
                this.audioTrack = null;
            }
            throw new InitializationException(state, this.sampleRate, this.channelConfig, this.bufferSize);
        }
    }

    private long pcmBytesToFrames(long byteCount) {
        return byteCount / ((long) this.pcmFrameSize);
    }

    private long framesToDurationUs(long frameCount) {
        return (C0747C.MICROS_PER_SECOND * frameCount) / ((long) this.sampleRate);
    }

    private long durationUsToFrames(long durationUs) {
        return (((long) this.sampleRate) * durationUs) / C0747C.MICROS_PER_SECOND;
    }

    private long getSubmittedFrames() {
        return this.passthrough ? this.submittedEncodedFrames : pcmBytesToFrames(this.submittedPcmBytes);
    }

    private void resetSyncParams() {
        this.smoothedPlayheadOffsetUs = 0;
        this.playheadOffsetCount = START_NOT_SET;
        this.nextPlayheadOffsetIndex = START_NOT_SET;
        this.lastPlayheadSampleTimeUs = 0;
        this.audioTimestampSet = false;
        this.lastTimestampSampleTimeUs = 0;
    }

    private boolean needsPassthroughWorkarounds() {
        return Util.SDK_INT < 23 && (this.targetEncoding == 5 || this.targetEncoding == 6);
    }

    private boolean overrideHasPendingData() {
        return needsPassthroughWorkarounds() && this.audioTrack.getPlayState() == START_NEED_SYNC && this.audioTrack.getPlaybackHeadPosition() == 0;
    }

    private static ByteBuffer resampleTo16BitPcm(ByteBuffer buffer, int offset, int size, int sourceEncoding, ByteBuffer out) {
        int resampledSize;
        switch (sourceEncoding) {
            case LinearLayoutManager.INVALID_OFFSET /*-2147483648*/:
                resampledSize = (size / 3) * START_NEED_SYNC;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                resampledSize = size * START_NEED_SYNC;
                break;
            case C0747C.ENCODING_PCM_32BIT /*1073741824*/:
                resampledSize = size / START_NEED_SYNC;
                break;
            default:
                throw new IllegalStateException();
        }
        ByteBuffer resampledBuffer = out;
        if (resampledBuffer == null || resampledBuffer.capacity() < resampledSize) {
            resampledBuffer = ByteBuffer.allocateDirect(resampledSize);
        }
        resampledBuffer.position(START_NOT_SET);
        resampledBuffer.limit(resampledSize);
        int limit = offset + size;
        int i;
        switch (sourceEncoding) {
            case LinearLayoutManager.INVALID_OFFSET /*-2147483648*/:
                for (i = offset; i < limit; i += 3) {
                    resampledBuffer.put(buffer.get(i + START_IN_SYNC));
                    resampledBuffer.put(buffer.get(i + START_NEED_SYNC));
                }
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                for (i = offset; i < limit; i += START_IN_SYNC) {
                    resampledBuffer.put((byte) 0);
                    resampledBuffer.put((byte) ((buffer.get(i) & NalUnitUtil.EXTENDED_SAR) - 128));
                }
                break;
            case C0747C.ENCODING_PCM_32BIT /*1073741824*/:
                for (i = offset; i < limit; i += BUFFER_MULTIPLICATION_FACTOR) {
                    resampledBuffer.put(buffer.get(i + START_NEED_SYNC));
                    resampledBuffer.put(buffer.get(i + 3));
                }
                break;
            default:
                throw new IllegalStateException();
        }
        resampledBuffer.position(START_NOT_SET);
        return resampledBuffer;
    }

    private static int getEncodingForMimeType(String mimeType) {
        int i = -1;
        switch (mimeType.hashCode()) {
            case -1095064472:
                if (mimeType.equals(MimeTypes.AUDIO_DTS)) {
                    i = START_NEED_SYNC;
                    break;
                }
                break;
            case 187078296:
                if (mimeType.equals(MimeTypes.AUDIO_AC3)) {
                    i = START_NOT_SET;
                    break;
                }
                break;
            case 1504578661:
                if (mimeType.equals(MimeTypes.AUDIO_E_AC3)) {
                    i = START_IN_SYNC;
                    break;
                }
                break;
            case 1505942594:
                if (mimeType.equals(MimeTypes.AUDIO_DTS_HD)) {
                    i = 3;
                    break;
                }
                break;
        }
        switch (i) {
            case START_NOT_SET /*0*/:
                return 5;
            case START_IN_SYNC /*1*/:
                return 6;
            case START_NEED_SYNC /*2*/:
                return 7;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 8;
            default:
                return START_NOT_SET;
        }
    }

    private static int getFramesPerEncodedSample(int encoding, ByteBuffer buffer) {
        if (encoding == 7 || encoding == 8) {
            return DtsUtil.parseDtsAudioSampleCount(buffer);
        }
        if (encoding == 5) {
            return Ac3Util.getAc3SyncframeAudioSampleCount();
        }
        if (encoding == 6) {
            return Ac3Util.parseEAc3SyncframeAudioSampleCount(buffer);
        }
        throw new IllegalStateException("Unexpected audio encoding: " + encoding);
    }

    @TargetApi(21)
    private static int writeNonBlockingV21(android.media.AudioTrack audioTrack, ByteBuffer buffer, int size) {
        return audioTrack.write(buffer, size, START_IN_SYNC);
    }

    @TargetApi(21)
    private static void setAudioTrackVolumeV21(android.media.AudioTrack audioTrack, float volume) {
        audioTrack.setVolume(volume);
    }

    private static void setAudioTrackVolumeV3(android.media.AudioTrack audioTrack, float volume) {
        audioTrack.setStereoVolume(volume, volume);
    }
}
