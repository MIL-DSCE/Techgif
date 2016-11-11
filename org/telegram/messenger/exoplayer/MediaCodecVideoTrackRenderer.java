package org.telegram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.nio.ByteBuffer;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

@TargetApi(16)
public class MediaCodecVideoTrackRenderer extends MediaCodecTrackRenderer {
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    public static final int MSG_SET_SURFACE = 1;
    private final long allowedJoiningTimeUs;
    private int consecutiveDroppedFrameCount;
    private int currentHeight;
    private float currentPixelWidthHeightRatio;
    private int currentUnappliedRotationDegrees;
    private int currentWidth;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrameCount;
    private final EventListener eventListener;
    private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
    private long joiningDeadlineUs;
    private int lastReportedHeight;
    private float lastReportedPixelWidthHeightRatio;
    private int lastReportedUnappliedRotationDegrees;
    private int lastReportedWidth;
    private final int maxDroppedFrameCountToNotify;
    private float pendingPixelWidthHeightRatio;
    private int pendingRotationDegrees;
    private boolean renderedFirstFrame;
    private boolean reportedDrawnToSurface;
    private Surface surface;
    private final int videoScalingMode;

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer.1 */
    class C07571 implements Runnable {
        final /* synthetic */ int val$currentHeight;
        final /* synthetic */ float val$currentPixelWidthHeightRatio;
        final /* synthetic */ int val$currentUnappliedRotationDegrees;
        final /* synthetic */ int val$currentWidth;

        C07571(int i, int i2, int i3, float f) {
            this.val$currentWidth = i;
            this.val$currentHeight = i2;
            this.val$currentUnappliedRotationDegrees = i3;
            this.val$currentPixelWidthHeightRatio = f;
        }

        public void run() {
            MediaCodecVideoTrackRenderer.this.eventListener.onVideoSizeChanged(this.val$currentWidth, this.val$currentHeight, this.val$currentUnappliedRotationDegrees, this.val$currentPixelWidthHeightRatio);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer.2 */
    class C07582 implements Runnable {
        final /* synthetic */ Surface val$surface;

        C07582(Surface surface) {
            this.val$surface = surface;
        }

        public void run() {
            MediaCodecVideoTrackRenderer.this.eventListener.onDrawnToSurface(this.val$surface);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer.3 */
    class C07593 implements Runnable {
        final /* synthetic */ int val$countToNotify;
        final /* synthetic */ long val$elapsedToNotify;

        C07593(int i, long j) {
            this.val$countToNotify = i;
            this.val$elapsedToNotify = j;
        }

        public void run() {
            MediaCodecVideoTrackRenderer.this.eventListener.onDroppedFrames(this.val$countToNotify, this.val$elapsedToNotify);
        }
    }

    public interface EventListener extends org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.EventListener {
        void onDrawnToSurface(Surface surface);

        void onDroppedFrames(int i, long j);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public MediaCodecVideoTrackRenderer(Context context, SampleSource source, MediaCodecSelector mediaCodecSelector, int videoScalingMode) {
        this(context, source, mediaCodecSelector, videoScalingMode, 0);
    }

    public MediaCodecVideoTrackRenderer(Context context, SampleSource source, MediaCodecSelector mediaCodecSelector, int videoScalingMode, long allowedJoiningTimeMs) {
        this(context, source, mediaCodecSelector, videoScalingMode, allowedJoiningTimeMs, null, null, -1);
    }

    public MediaCodecVideoTrackRenderer(Context context, SampleSource source, MediaCodecSelector mediaCodecSelector, int videoScalingMode, long allowedJoiningTimeMs, Handler eventHandler, EventListener eventListener, int maxDroppedFrameCountToNotify) {
        this(context, source, mediaCodecSelector, videoScalingMode, allowedJoiningTimeMs, null, false, eventHandler, eventListener, maxDroppedFrameCountToNotify);
    }

    public MediaCodecVideoTrackRenderer(Context context, SampleSource source, MediaCodecSelector mediaCodecSelector, int videoScalingMode, long allowedJoiningTimeMs, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener, int maxDroppedFrameCountToNotify) {
        super(source, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, (org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.EventListener) eventListener);
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(context);
        this.videoScalingMode = videoScalingMode;
        this.allowedJoiningTimeUs = 1000 * allowedJoiningTimeMs;
        this.eventListener = eventListener;
        this.maxDroppedFrameCountToNotify = maxDroppedFrameCountToNotify;
        this.joiningDeadlineUs = -1;
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
        this.pendingPixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
        this.lastReportedWidth = -1;
        this.lastReportedHeight = -1;
        this.lastReportedPixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
    }

    protected boolean handlesTrack(MediaCodecSelector mediaCodecSelector, MediaFormat mediaFormat) throws DecoderQueryException {
        String mimeType = mediaFormat.mimeType;
        if (!MimeTypes.isVideo(mimeType)) {
            return false;
        }
        if (MimeTypes.VIDEO_UNKNOWN.equals(mimeType) || mediaCodecSelector.getDecoderInfo(mimeType, false) != null) {
            return true;
        }
        return false;
    }

    protected void onEnabled(int track, long positionUs, boolean joining) throws ExoPlaybackException {
        super.onEnabled(track, positionUs, joining);
        if (joining && this.allowedJoiningTimeUs > 0) {
            this.joiningDeadlineUs = (SystemClock.elapsedRealtime() * 1000) + this.allowedJoiningTimeUs;
        }
        this.frameReleaseTimeHelper.enable();
    }

    protected void onDiscontinuity(long positionUs) throws ExoPlaybackException {
        super.onDiscontinuity(positionUs);
        this.renderedFirstFrame = false;
        this.consecutiveDroppedFrameCount = 0;
        this.joiningDeadlineUs = -1;
    }

    protected boolean isReady() {
        if (super.isReady() && (this.renderedFirstFrame || !codecInitialized() || getSourceState() == 2)) {
            this.joiningDeadlineUs = -1;
            return true;
        } else if (this.joiningDeadlineUs == -1) {
            return false;
        } else {
            if (SystemClock.elapsedRealtime() * 1000 < this.joiningDeadlineUs) {
                return true;
            }
            this.joiningDeadlineUs = -1;
            return false;
        }
    }

    protected void onStarted() {
        super.onStarted();
        this.droppedFrameCount = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
    }

    protected void onStopped() {
        this.joiningDeadlineUs = -1;
        maybeNotifyDroppedFrameCount();
        super.onStopped();
    }

    protected void onDisabled() throws ExoPlaybackException {
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
        this.pendingPixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
        this.lastReportedWidth = -1;
        this.lastReportedHeight = -1;
        this.lastReportedPixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
        this.frameReleaseTimeHelper.disable();
        super.onDisabled();
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        if (messageType == MSG_SET_SURFACE) {
            setSurface((Surface) message);
        } else {
            super.handleMessage(messageType, message);
        }
    }

    private void setSurface(Surface surface) throws ExoPlaybackException {
        if (this.surface != surface) {
            this.surface = surface;
            this.reportedDrawnToSurface = false;
            int state = getState();
            if (state == 2 || state == 3) {
                releaseCodec();
                maybeInitCodec();
            }
        }
    }

    protected boolean shouldInitCodec() {
        return super.shouldInitCodec() && this.surface != null && this.surface.isValid();
    }

    protected void configureCodec(MediaCodec codec, boolean codecIsAdaptive, MediaFormat format, MediaCrypto crypto) {
        maybeSetMaxInputSize(format, codecIsAdaptive);
        codec.configure(format, this.surface, crypto, 0);
    }

    protected void onInputFormatChanged(MediaFormatHolder holder) throws ExoPlaybackException {
        super.onInputFormatChanged(holder);
        this.pendingPixelWidthHeightRatio = holder.format.pixelWidthHeightRatio == GroundOverlayOptions.NO_DIMENSION ? TouchHelperCallback.ALPHA_FULL : holder.format.pixelWidthHeightRatio;
        this.pendingRotationDegrees = holder.format.rotationDegrees == -1 ? 0 : holder.format.rotationDegrees;
    }

    protected final boolean haveRenderedFirstFrame() {
        return this.renderedFirstFrame;
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {
        int integer;
        boolean hasCrop = outputFormat.containsKey(KEY_CROP_RIGHT) && outputFormat.containsKey(KEY_CROP_LEFT) && outputFormat.containsKey(KEY_CROP_BOTTOM) && outputFormat.containsKey(KEY_CROP_TOP);
        this.currentWidth = hasCrop ? (outputFormat.getInteger(KEY_CROP_RIGHT) - outputFormat.getInteger(KEY_CROP_LEFT)) + MSG_SET_SURFACE : outputFormat.getInteger("width");
        if (hasCrop) {
            integer = (outputFormat.getInteger(KEY_CROP_BOTTOM) - outputFormat.getInteger(KEY_CROP_TOP)) + MSG_SET_SURFACE;
        } else {
            integer = outputFormat.getInteger("height");
        }
        this.currentHeight = integer;
        this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
        if (Util.SDK_INT < 21) {
            this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
        } else if (this.pendingRotationDegrees == 90 || this.pendingRotationDegrees == 270) {
            int rotatedHeight = this.currentWidth;
            this.currentWidth = this.currentHeight;
            this.currentHeight = rotatedHeight;
            this.currentPixelWidthHeightRatio = TouchHelperCallback.ALPHA_FULL / this.currentPixelWidthHeightRatio;
        }
        codec.setVideoScalingMode(this.videoScalingMode);
    }

    protected boolean canReconfigureCodec(MediaCodec codec, boolean codecIsAdaptive, MediaFormat oldFormat, MediaFormat newFormat) {
        return newFormat.mimeType.equals(oldFormat.mimeType) && (codecIsAdaptive || (oldFormat.width == newFormat.width && oldFormat.height == newFormat.height));
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, BufferInfo bufferInfo, int bufferIndex, boolean shouldSkip) {
        if (shouldSkip) {
            skipOutputBuffer(codec, bufferIndex);
            this.consecutiveDroppedFrameCount = 0;
            return true;
        } else if (!this.renderedFirstFrame) {
            if (Util.SDK_INT >= 21) {
                renderOutputBufferV21(codec, bufferIndex, System.nanoTime());
            } else {
                renderOutputBuffer(codec, bufferIndex);
            }
            this.consecutiveDroppedFrameCount = 0;
            return true;
        } else if (getState() != 3) {
            return false;
        } else {
            long earlyUs = (bufferInfo.presentationTimeUs - positionUs) - ((SystemClock.elapsedRealtime() * 1000) - elapsedRealtimeUs);
            long systemTimeNs = System.nanoTime();
            long unadjustedFrameReleaseTimeNs = systemTimeNs + (1000 * earlyUs);
            long adjustedReleaseTimeNs = this.frameReleaseTimeHelper.adjustReleaseTime(bufferInfo.presentationTimeUs, unadjustedFrameReleaseTimeNs);
            earlyUs = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
            if (earlyUs < -30000) {
                dropOutputBuffer(codec, bufferIndex);
                return true;
            }
            if (Util.SDK_INT >= 21) {
                if (earlyUs < 50000) {
                    renderOutputBufferV21(codec, bufferIndex, adjustedReleaseTimeNs);
                    this.consecutiveDroppedFrameCount = 0;
                    return true;
                }
            } else if (earlyUs < 30000) {
                if (earlyUs > 11000) {
                    try {
                        Thread.sleep((earlyUs - 10000) / 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                renderOutputBuffer(codec, bufferIndex);
                this.consecutiveDroppedFrameCount = 0;
                return true;
            }
            return false;
        }
    }

    protected void skipOutputBuffer(MediaCodec codec, int bufferIndex) {
        TraceUtil.beginSection("skipVideoBuffer");
        codec.releaseOutputBuffer(bufferIndex, false);
        TraceUtil.endSection();
        CodecCounters codecCounters = this.codecCounters;
        codecCounters.skippedOutputBufferCount += MSG_SET_SURFACE;
    }

    protected void dropOutputBuffer(MediaCodec codec, int bufferIndex) {
        TraceUtil.beginSection("dropVideoBuffer");
        codec.releaseOutputBuffer(bufferIndex, false);
        TraceUtil.endSection();
        CodecCounters codecCounters = this.codecCounters;
        codecCounters.droppedOutputBufferCount += MSG_SET_SURFACE;
        this.droppedFrameCount += MSG_SET_SURFACE;
        this.consecutiveDroppedFrameCount += MSG_SET_SURFACE;
        this.codecCounters.maxConsecutiveDroppedOutputBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.codecCounters.maxConsecutiveDroppedOutputBufferCount);
        if (this.droppedFrameCount == this.maxDroppedFrameCountToNotify) {
            maybeNotifyDroppedFrameCount();
        }
    }

    protected void renderOutputBuffer(MediaCodec codec, int bufferIndex) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(bufferIndex, true);
        TraceUtil.endSection();
        CodecCounters codecCounters = this.codecCounters;
        codecCounters.renderedOutputBufferCount += MSG_SET_SURFACE;
        this.renderedFirstFrame = true;
        maybeNotifyDrawnToSurface();
    }

    @TargetApi(21)
    protected void renderOutputBufferV21(MediaCodec codec, int bufferIndex, long releaseTimeNs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(bufferIndex, releaseTimeNs);
        TraceUtil.endSection();
        CodecCounters codecCounters = this.codecCounters;
        codecCounters.renderedOutputBufferCount += MSG_SET_SURFACE;
        this.renderedFirstFrame = true;
        maybeNotifyDrawnToSurface();
    }

    @SuppressLint({"InlinedApi"})
    private void maybeSetMaxInputSize(MediaFormat format, boolean codecIsAdaptive) {
        if (!format.containsKey("max-input-size")) {
            int maxPixels;
            int minCompressionRatio;
            int maxHeight = format.getInteger("height");
            if (codecIsAdaptive && format.containsKey("max-height")) {
                maxHeight = Math.max(maxHeight, format.getInteger("max-height"));
            }
            int maxWidth = format.getInteger("width");
            if (codecIsAdaptive && format.containsKey("max-width")) {
                maxWidth = Math.max(maxHeight, format.getInteger("max-width"));
            }
            String string = format.getString("mime");
            Object obj = -1;
            switch (string.hashCode()) {
                case -1664118616:
                    if (string.equals(MimeTypes.VIDEO_H263)) {
                        obj = null;
                        break;
                    }
                    break;
                case -1662541442:
                    if (string.equals(MimeTypes.VIDEO_H265)) {
                        obj = 4;
                        break;
                    }
                    break;
                case 1187890754:
                    if (string.equals(MimeTypes.VIDEO_MP4V)) {
                        obj = MSG_SET_SURFACE;
                        break;
                    }
                    break;
                case 1331836730:
                    if (string.equals(MediaController.MIME_TYPE)) {
                        obj = 2;
                        break;
                    }
                    break;
                case 1599127256:
                    if (string.equals(MimeTypes.VIDEO_VP8)) {
                        obj = 3;
                        break;
                    }
                    break;
                case 1599127257:
                    if (string.equals(MimeTypes.VIDEO_VP9)) {
                        obj = 5;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                case MSG_SET_SURFACE /*1*/:
                    maxPixels = maxWidth * maxHeight;
                    minCompressionRatio = 2;
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    if (!"BRAVIA 4K 2015".equals(Util.MODEL)) {
                        maxPixels = ((((maxWidth + 15) / 16) * ((maxHeight + 15) / 16)) * 16) * 16;
                        minCompressionRatio = 2;
                        break;
                    }
                    return;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    maxPixels = maxWidth * maxHeight;
                    minCompressionRatio = 2;
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                case VideoPlayer.STATE_ENDED /*5*/:
                    maxPixels = maxWidth * maxHeight;
                    minCompressionRatio = 4;
                    break;
                default:
                    return;
            }
            format.setInteger("max-input-size", (maxPixels * 3) / (minCompressionRatio * 2));
        }
    }

    private void maybeNotifyVideoSizeChanged() {
        if (this.eventHandler != null && this.eventListener != null) {
            if (this.lastReportedWidth != this.currentWidth || this.lastReportedHeight != this.currentHeight || this.lastReportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees || this.lastReportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio) {
                int currentWidth = this.currentWidth;
                int currentHeight = this.currentHeight;
                int currentUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
                float currentPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
                this.eventHandler.post(new C07571(currentWidth, currentHeight, currentUnappliedRotationDegrees, currentPixelWidthHeightRatio));
                this.lastReportedWidth = currentWidth;
                this.lastReportedHeight = currentHeight;
                this.lastReportedUnappliedRotationDegrees = currentUnappliedRotationDegrees;
                this.lastReportedPixelWidthHeightRatio = currentPixelWidthHeightRatio;
            }
        }
    }

    private void maybeNotifyDrawnToSurface() {
        if (this.eventHandler != null && this.eventListener != null && !this.reportedDrawnToSurface) {
            this.eventHandler.post(new C07582(this.surface));
            this.reportedDrawnToSurface = true;
        }
    }

    private void maybeNotifyDroppedFrameCount() {
        if (this.eventHandler != null && this.eventListener != null && this.droppedFrameCount != 0) {
            long now = SystemClock.elapsedRealtime();
            this.eventHandler.post(new C07593(this.droppedFrameCount, now - this.droppedFrameAccumulationStartTimeMs));
            this.droppedFrameCount = 0;
            this.droppedFrameAccumulationStartTimeMs = now;
        }
    }
}
