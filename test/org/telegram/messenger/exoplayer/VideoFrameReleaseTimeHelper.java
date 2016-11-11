package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.WindowManager;
import com.google.android.gms.maps.model.GroundOverlayOptions;

@TargetApi(16)
public final class VideoFrameReleaseTimeHelper {
    private static final long CHOREOGRAPHER_SAMPLE_DELAY_MILLIS = 500;
    private static final long MAX_ALLOWED_DRIFT_NS = 20000000;
    private static final int MIN_FRAMES_FOR_ADJUSTMENT = 6;
    private static final long VSYNC_OFFSET_PERCENTAGE = 80;
    private long adjustedLastFrameTimeNs;
    private long frameCount;
    private boolean haveSync;
    private long lastFramePresentationTimeUs;
    private long pendingAdjustedFrameTimeNs;
    private long syncFramePresentationTimeNs;
    private long syncUnadjustedReleaseTimeNs;
    private final boolean useDefaultDisplayVsync;
    private final long vsyncDurationNs;
    private final long vsyncOffsetNs;
    private final VSyncSampler vsyncSampler;

    private static final class VSyncSampler implements FrameCallback, Callback {
        private static final int CREATE_CHOREOGRAPHER = 0;
        private static final VSyncSampler INSTANCE;
        private static final int MSG_ADD_OBSERVER = 1;
        private static final int MSG_REMOVE_OBSERVER = 2;
        private Choreographer choreographer;
        private final HandlerThread choreographerOwnerThread;
        private final Handler handler;
        private int observerCount;
        public volatile long sampledVsyncTimeNs;

        static {
            INSTANCE = new VSyncSampler();
        }

        public static VSyncSampler getInstance() {
            return INSTANCE;
        }

        private VSyncSampler() {
            this.choreographerOwnerThread = new HandlerThread("ChoreographerOwner:Handler");
            this.choreographerOwnerThread.start();
            this.handler = new Handler(this.choreographerOwnerThread.getLooper(), this);
            this.handler.sendEmptyMessage(CREATE_CHOREOGRAPHER);
        }

        public void addObserver() {
            this.handler.sendEmptyMessage(MSG_ADD_OBSERVER);
        }

        public void removeObserver() {
            this.handler.sendEmptyMessage(MSG_REMOVE_OBSERVER);
        }

        public void doFrame(long vsyncTimeNs) {
            this.sampledVsyncTimeNs = vsyncTimeNs;
            this.choreographer.postFrameCallbackDelayed(this, VideoFrameReleaseTimeHelper.CHOREOGRAPHER_SAMPLE_DELAY_MILLIS);
        }

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case CREATE_CHOREOGRAPHER /*0*/:
                    createChoreographerInstanceInternal();
                    return true;
                case MSG_ADD_OBSERVER /*1*/:
                    addObserverInternal();
                    return true;
                case MSG_REMOVE_OBSERVER /*2*/:
                    removeObserverInternal();
                    return true;
                default:
                    return false;
            }
        }

        private void createChoreographerInstanceInternal() {
            this.choreographer = Choreographer.getInstance();
        }

        private void addObserverInternal() {
            this.observerCount += MSG_ADD_OBSERVER;
            if (this.observerCount == MSG_ADD_OBSERVER) {
                this.choreographer.postFrameCallback(this);
            }
        }

        private void removeObserverInternal() {
            this.observerCount--;
            if (this.observerCount == 0) {
                this.choreographer.removeFrameCallback(this);
                this.sampledVsyncTimeNs = 0;
            }
        }
    }

    public VideoFrameReleaseTimeHelper() {
        this(GroundOverlayOptions.NO_DIMENSION, false);
    }

    public VideoFrameReleaseTimeHelper(Context context) {
        this(getDefaultDisplayRefreshRate(context), true);
    }

    private VideoFrameReleaseTimeHelper(float defaultDisplayRefreshRate, boolean useDefaultDisplayVsync) {
        this.useDefaultDisplayVsync = useDefaultDisplayVsync;
        if (useDefaultDisplayVsync) {
            this.vsyncSampler = VSyncSampler.getInstance();
            this.vsyncDurationNs = (long) (1.0E9d / ((double) defaultDisplayRefreshRate));
            this.vsyncOffsetNs = (this.vsyncDurationNs * VSYNC_OFFSET_PERCENTAGE) / 100;
            return;
        }
        this.vsyncSampler = null;
        this.vsyncDurationNs = -1;
        this.vsyncOffsetNs = -1;
    }

    public void enable() {
        this.haveSync = false;
        if (this.useDefaultDisplayVsync) {
            this.vsyncSampler.addObserver();
        }
    }

    public void disable() {
        if (this.useDefaultDisplayVsync) {
            this.vsyncSampler.removeObserver();
        }
    }

    public long adjustReleaseTime(long framePresentationTimeUs, long unadjustedReleaseTimeNs) {
        long framePresentationTimeNs = framePresentationTimeUs * 1000;
        long adjustedFrameTimeNs = framePresentationTimeNs;
        long adjustedReleaseTimeNs = unadjustedReleaseTimeNs;
        if (this.haveSync) {
            if (framePresentationTimeUs != this.lastFramePresentationTimeUs) {
                this.frameCount++;
                this.adjustedLastFrameTimeNs = this.pendingAdjustedFrameTimeNs;
            }
            if (this.frameCount >= 6) {
                long candidateAdjustedFrameTimeNs = this.adjustedLastFrameTimeNs + ((framePresentationTimeNs - this.syncFramePresentationTimeNs) / this.frameCount);
                if (isDriftTooLarge(candidateAdjustedFrameTimeNs, unadjustedReleaseTimeNs)) {
                    this.haveSync = false;
                } else {
                    adjustedFrameTimeNs = candidateAdjustedFrameTimeNs;
                    adjustedReleaseTimeNs = (this.syncUnadjustedReleaseTimeNs + adjustedFrameTimeNs) - this.syncFramePresentationTimeNs;
                }
            } else if (isDriftTooLarge(framePresentationTimeNs, unadjustedReleaseTimeNs)) {
                this.haveSync = false;
            }
        }
        if (!this.haveSync) {
            this.syncFramePresentationTimeNs = framePresentationTimeNs;
            this.syncUnadjustedReleaseTimeNs = unadjustedReleaseTimeNs;
            this.frameCount = 0;
            this.haveSync = true;
            onSynced();
        }
        this.lastFramePresentationTimeUs = framePresentationTimeUs;
        this.pendingAdjustedFrameTimeNs = adjustedFrameTimeNs;
        return (this.vsyncSampler == null || this.vsyncSampler.sampledVsyncTimeNs == 0) ? adjustedReleaseTimeNs : closestVsync(adjustedReleaseTimeNs, this.vsyncSampler.sampledVsyncTimeNs, this.vsyncDurationNs) - this.vsyncOffsetNs;
    }

    protected void onSynced() {
    }

    private boolean isDriftTooLarge(long frameTimeNs, long releaseTimeNs) {
        return Math.abs((releaseTimeNs - this.syncUnadjustedReleaseTimeNs) - (frameTimeNs - this.syncFramePresentationTimeNs)) > MAX_ALLOWED_DRIFT_NS;
    }

    private static long closestVsync(long releaseTime, long sampledVsyncTime, long vsyncDuration) {
        long snappedBeforeNs;
        long snappedAfterNs;
        long snappedTimeNs = sampledVsyncTime + (vsyncDuration * ((releaseTime - sampledVsyncTime) / vsyncDuration));
        if (releaseTime <= snappedTimeNs) {
            snappedBeforeNs = snappedTimeNs - vsyncDuration;
            snappedAfterNs = snappedTimeNs;
        } else {
            snappedBeforeNs = snappedTimeNs;
            snappedAfterNs = snappedTimeNs + vsyncDuration;
        }
        if (snappedAfterNs - releaseTime < releaseTime - snappedBeforeNs) {
            return snappedAfterNs;
        }
        return snappedBeforeNs;
    }

    private static float getDefaultDisplayRefreshRate(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRefreshRate();
    }
}
