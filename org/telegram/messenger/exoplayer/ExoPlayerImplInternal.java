package org.telegram.messenger.exoplayer;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer.ExoPlayer.ExoPlayerComponent;
import org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.PriorityHandlerThread;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;

final class ExoPlayerImplInternal implements Callback {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MSG_CUSTOM = 9;
    private static final int MSG_DO_SOME_WORK = 7;
    public static final int MSG_ERROR = 4;
    private static final int MSG_INCREMENTAL_PREPARE = 2;
    private static final int MSG_PREPARE = 1;
    public static final int MSG_PREPARED = 1;
    private static final int MSG_RELEASE = 5;
    private static final int MSG_SEEK_TO = 6;
    private static final int MSG_SET_PLAY_WHEN_READY = 3;
    public static final int MSG_SET_PLAY_WHEN_READY_ACK = 3;
    private static final int MSG_SET_RENDERER_SELECTED_TRACK = 8;
    public static final int MSG_STATE_CHANGED = 2;
    private static final int MSG_STOP = 4;
    private static final int PREPARE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private volatile long bufferedPositionUs;
    private int customMessagesProcessed;
    private int customMessagesSent;
    private volatile long durationUs;
    private long elapsedRealtimeUs;
    private final List<TrackRenderer> enabledRenderers;
    private final Handler eventHandler;
    private final Handler handler;
    private final HandlerThread internalPlaybackThread;
    private long lastSeekPositionMs;
    private final long minBufferUs;
    private final long minRebufferUs;
    private final AtomicInteger pendingSeekCount;
    private boolean playWhenReady;
    private volatile long positionUs;
    private boolean rebuffering;
    private boolean released;
    private MediaClock rendererMediaClock;
    private TrackRenderer rendererMediaClockSource;
    private TrackRenderer[] renderers;
    private final int[] selectedTrackIndices;
    private final StandaloneMediaClock standaloneMediaClock;
    private int state;
    private final MediaFormat[][] trackFormats;

    public ExoPlayerImplInternal(Handler eventHandler, boolean playWhenReady, int[] selectedTrackIndices, int minBufferMs, int minRebufferMs) {
        this.customMessagesSent = 0;
        this.customMessagesProcessed = 0;
        this.eventHandler = eventHandler;
        this.playWhenReady = playWhenReady;
        this.minBufferUs = ((long) minBufferMs) * 1000;
        this.minRebufferUs = ((long) minRebufferMs) * 1000;
        this.selectedTrackIndices = Arrays.copyOf(selectedTrackIndices, selectedTrackIndices.length);
        this.state = MSG_PREPARED;
        this.durationUs = -1;
        this.bufferedPositionUs = -1;
        this.standaloneMediaClock = new StandaloneMediaClock();
        this.pendingSeekCount = new AtomicInteger();
        this.enabledRenderers = new ArrayList(selectedTrackIndices.length);
        this.trackFormats = new MediaFormat[selectedTrackIndices.length][];
        this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
    }

    public Looper getPlaybackLooper() {
        return this.internalPlaybackThread.getLooper();
    }

    public long getCurrentPosition() {
        return this.pendingSeekCount.get() > 0 ? this.lastSeekPositionMs : this.positionUs / 1000;
    }

    public long getBufferedPosition() {
        return this.bufferedPositionUs == -1 ? -1 : this.bufferedPositionUs / 1000;
    }

    public long getDuration() {
        return this.durationUs == -1 ? -1 : this.durationUs / 1000;
    }

    public void prepare(TrackRenderer... renderers) {
        this.handler.obtainMessage(MSG_PREPARED, renderers).sendToTarget();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        int i;
        Handler handler = this.handler;
        if (playWhenReady) {
            i = MSG_PREPARED;
        } else {
            i = 0;
        }
        handler.obtainMessage(MSG_SET_PLAY_WHEN_READY_ACK, i, 0).sendToTarget();
    }

    public void seekTo(long positionMs) {
        this.lastSeekPositionMs = positionMs;
        this.pendingSeekCount.incrementAndGet();
        this.handler.obtainMessage(MSG_SEEK_TO, Util.getTopInt(positionMs), Util.getBottomInt(positionMs)).sendToTarget();
    }

    public void stop() {
        this.handler.sendEmptyMessage(MSG_STOP);
    }

    public void setRendererSelectedTrack(int rendererIndex, int trackIndex) {
        this.handler.obtainMessage(MSG_SET_RENDERER_SELECTED_TRACK, rendererIndex, trackIndex).sendToTarget();
    }

    public void sendMessage(ExoPlayerComponent target, int messageType, Object message) {
        this.customMessagesSent += MSG_PREPARED;
        this.handler.obtainMessage(MSG_CUSTOM, messageType, 0, Pair.create(target, message)).sendToTarget();
    }

    public synchronized void blockingSendMessage(ExoPlayerComponent target, int messageType, Object message) {
        if (this.released) {
            Log.w(TAG, "Sent message(" + messageType + ") after release. Message ignored.");
        } else {
            int messageNumber = this.customMessagesSent;
            this.customMessagesSent = messageNumber + MSG_PREPARED;
            this.handler.obtainMessage(MSG_CUSTOM, messageType, 0, Pair.create(target, message)).sendToTarget();
            while (this.customMessagesProcessed <= messageNumber) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void release() {
        /*
        r3 = this;
        monitor-enter(r3);
        r1 = r3.released;	 Catch:{ all -> 0x001e }
        if (r1 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r3);
        return;
    L_0x0007:
        r1 = r3.handler;	 Catch:{ all -> 0x001e }
        r2 = 5;
        r1.sendEmptyMessage(r2);	 Catch:{ all -> 0x001e }
    L_0x000d:
        r1 = r3.released;	 Catch:{ all -> 0x001e }
        if (r1 != 0) goto L_0x0021;
    L_0x0011:
        r3.wait();	 Catch:{ InterruptedException -> 0x0015 }
        goto L_0x000d;
    L_0x0015:
        r0 = move-exception;
        r1 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x001e }
        r1.interrupt();	 Catch:{ all -> 0x001e }
        goto L_0x000d;
    L_0x001e:
        r1 = move-exception;
        monitor-exit(r3);
        throw r1;
    L_0x0021:
        r1 = r3.internalPlaybackThread;	 Catch:{ all -> 0x001e }
        r1.quit();	 Catch:{ all -> 0x001e }
        goto L_0x0005;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.ExoPlayerImplInternal.release():void");
    }

    public boolean handleMessage(Message msg) {
        boolean z = false;
        try {
            switch (msg.what) {
                case MSG_PREPARED /*1*/:
                    prepareInternal((TrackRenderer[]) msg.obj);
                    return true;
                case MSG_STATE_CHANGED /*2*/:
                    incrementalPrepareInternal();
                    return true;
                case MSG_SET_PLAY_WHEN_READY_ACK /*3*/:
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    setPlayWhenReadyInternal(z);
                    return true;
                case MSG_STOP /*4*/:
                    stopInternal();
                    return true;
                case MSG_RELEASE /*5*/:
                    releaseInternal();
                    return true;
                case MSG_SEEK_TO /*6*/:
                    seekToInternal(Util.getLong(msg.arg1, msg.arg2));
                    return true;
                case MSG_DO_SOME_WORK /*7*/:
                    doSomeWork();
                    return true;
                case MSG_SET_RENDERER_SELECTED_TRACK /*8*/:
                    setRendererSelectedTrackInternal(msg.arg1, msg.arg2);
                    return true;
                case MSG_CUSTOM /*9*/:
                    sendMessageInternal(msg.arg1, msg.obj);
                    return true;
                default:
                    return false;
            }
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Internal track renderer error.", e);
            this.eventHandler.obtainMessage(MSG_STOP, e).sendToTarget();
            stopInternal();
            return true;
        } catch (Throwable e2) {
            Log.e(TAG, "Internal runtime error.", e2);
            this.eventHandler.obtainMessage(MSG_STOP, new ExoPlaybackException(e2, true)).sendToTarget();
            stopInternal();
            return true;
        }
    }

    private void setState(int state) {
        if (this.state != state) {
            this.state = state;
            this.eventHandler.obtainMessage(MSG_STATE_CHANGED, state, 0).sendToTarget();
        }
    }

    private void prepareInternal(TrackRenderer[] renderers) throws ExoPlaybackException {
        resetInternal();
        this.renderers = renderers;
        Arrays.fill(this.trackFormats, null);
        setState(MSG_STATE_CHANGED);
        incrementalPrepareInternal();
    }

    private void incrementalPrepareInternal() throws ExoPlaybackException {
        int rendererIndex;
        long operationStartTimeMs = SystemClock.elapsedRealtime();
        boolean prepared = true;
        for (rendererIndex = 0; rendererIndex < this.renderers.length; rendererIndex += MSG_PREPARED) {
            TrackRenderer renderer = this.renderers[rendererIndex];
            if (renderer.getState() == 0 && renderer.prepare(this.positionUs) == 0) {
                renderer.maybeThrowError();
                prepared = false;
            }
        }
        if (prepared) {
            long durationUs = 0;
            boolean allRenderersEnded = true;
            boolean allRenderersReadyOrEnded = true;
            for (rendererIndex = 0; rendererIndex < this.renderers.length; rendererIndex += MSG_PREPARED) {
                int trackIndex;
                renderer = this.renderers[rendererIndex];
                int rendererTrackCount = renderer.getTrackCount();
                MediaFormat[] rendererTrackFormats = new MediaFormat[rendererTrackCount];
                for (trackIndex = 0; trackIndex < rendererTrackCount; trackIndex += MSG_PREPARED) {
                    rendererTrackFormats[trackIndex] = renderer.getFormat(trackIndex);
                }
                this.trackFormats[rendererIndex] = rendererTrackFormats;
                if (rendererTrackCount > 0) {
                    if (durationUs != -1) {
                        long trackDurationUs = renderer.getDurationUs();
                        if (trackDurationUs == -1) {
                            durationUs = -1;
                        } else if (trackDurationUs != -2) {
                            durationUs = Math.max(durationUs, trackDurationUs);
                        }
                    }
                    trackIndex = this.selectedTrackIndices[rendererIndex];
                    if (trackIndex >= 0 && trackIndex < rendererTrackFormats.length) {
                        enableRenderer(renderer, trackIndex, false);
                        allRenderersEnded = allRenderersEnded && renderer.isEnded();
                        if (allRenderersReadyOrEnded && rendererReadyOrEnded(renderer)) {
                            allRenderersReadyOrEnded = true;
                        } else {
                            allRenderersReadyOrEnded = false;
                        }
                    }
                }
            }
            this.durationUs = durationUs;
            if (!allRenderersEnded || (durationUs != -1 && durationUs > this.positionUs)) {
                this.state = allRenderersReadyOrEnded ? MSG_STOP : MSG_SET_PLAY_WHEN_READY_ACK;
            } else {
                this.state = MSG_RELEASE;
            }
            this.eventHandler.obtainMessage(MSG_PREPARED, this.state, 0, this.trackFormats).sendToTarget();
            if (this.playWhenReady && this.state == MSG_STOP) {
                startRenderers();
            }
            this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            return;
        }
        scheduleNextOperation(MSG_STATE_CHANGED, operationStartTimeMs, 10);
    }

    private void enableRenderer(TrackRenderer renderer, int trackIndex, boolean joining) throws ExoPlaybackException {
        renderer.enable(trackIndex, this.positionUs, joining);
        this.enabledRenderers.add(renderer);
        MediaClock mediaClock = renderer.getMediaClock();
        if (mediaClock != null) {
            Assertions.checkState(this.rendererMediaClock == null);
            this.rendererMediaClock = mediaClock;
            this.rendererMediaClockSource = renderer;
        }
    }

    private boolean rendererReadyOrEnded(TrackRenderer renderer) {
        boolean z = false;
        if (renderer.isEnded()) {
            return true;
        }
        if (!renderer.isReady()) {
            return false;
        }
        if (this.state == MSG_STOP) {
            return true;
        }
        long rendererDurationUs = renderer.getDurationUs();
        long rendererBufferedPositionUs = renderer.getBufferedPositionUs();
        long minBufferDurationUs = this.rebuffering ? this.minRebufferUs : this.minBufferUs;
        if (minBufferDurationUs <= 0 || rendererBufferedPositionUs == -1 || rendererBufferedPositionUs == -3 || rendererBufferedPositionUs >= this.positionUs + minBufferDurationUs || !(rendererDurationUs == -1 || rendererDurationUs == -2 || rendererBufferedPositionUs < rendererDurationUs)) {
            z = true;
        }
        return z;
    }

    private void setPlayWhenReadyInternal(boolean playWhenReady) throws ExoPlaybackException {
        try {
            this.rebuffering = false;
            this.playWhenReady = playWhenReady;
            if (!playWhenReady) {
                stopRenderers();
                updatePositionUs();
            } else if (this.state == MSG_STOP) {
                startRenderers();
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            } else if (this.state == MSG_SET_PLAY_WHEN_READY_ACK) {
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            }
            this.eventHandler.obtainMessage(MSG_SET_PLAY_WHEN_READY_ACK).sendToTarget();
        } catch (Throwable th) {
            this.eventHandler.obtainMessage(MSG_SET_PLAY_WHEN_READY_ACK).sendToTarget();
        }
    }

    private void startRenderers() throws ExoPlaybackException {
        this.rebuffering = false;
        this.standaloneMediaClock.start();
        for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
            ((TrackRenderer) this.enabledRenderers.get(i)).start();
        }
    }

    private void stopRenderers() throws ExoPlaybackException {
        this.standaloneMediaClock.stop();
        for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
            ensureStopped((TrackRenderer) this.enabledRenderers.get(i));
        }
    }

    private void updatePositionUs() {
        if (this.rendererMediaClock == null || !this.enabledRenderers.contains(this.rendererMediaClockSource) || this.rendererMediaClockSource.isEnded()) {
            this.positionUs = this.standaloneMediaClock.getPositionUs();
        } else {
            this.positionUs = this.rendererMediaClock.getPositionUs();
            this.standaloneMediaClock.setPositionUs(this.positionUs);
        }
        this.elapsedRealtimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    private void doSomeWork() throws ExoPlaybackException {
        TraceUtil.beginSection("doSomeWork");
        long operationStartTimeMs = SystemClock.elapsedRealtime();
        long bufferedPositionUs = this.durationUs != -1 ? this.durationUs : PtsTimestampAdjuster.DO_NOT_OFFSET;
        boolean allRenderersEnded = true;
        boolean allRenderersReadyOrEnded = true;
        updatePositionUs();
        for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
            TrackRenderer renderer = (TrackRenderer) this.enabledRenderers.get(i);
            renderer.doSomeWork(this.positionUs, this.elapsedRealtimeUs);
            allRenderersEnded = allRenderersEnded && renderer.isEnded();
            boolean rendererReadyOrEnded = rendererReadyOrEnded(renderer);
            if (!rendererReadyOrEnded) {
                renderer.maybeThrowError();
            }
            allRenderersReadyOrEnded = allRenderersReadyOrEnded && rendererReadyOrEnded;
            if (bufferedPositionUs != -1) {
                long rendererDurationUs = renderer.getDurationUs();
                long rendererBufferedPositionUs = renderer.getBufferedPositionUs();
                if (rendererBufferedPositionUs == -1) {
                    bufferedPositionUs = -1;
                } else if (rendererBufferedPositionUs != -3 && (rendererDurationUs == -1 || rendererDurationUs == -2 || rendererBufferedPositionUs < rendererDurationUs)) {
                    bufferedPositionUs = Math.min(bufferedPositionUs, rendererBufferedPositionUs);
                }
            }
        }
        this.bufferedPositionUs = bufferedPositionUs;
        if (allRenderersEnded && (this.durationUs == -1 || this.durationUs <= this.positionUs)) {
            setState(MSG_RELEASE);
            stopRenderers();
        } else if (this.state == MSG_SET_PLAY_WHEN_READY_ACK && allRenderersReadyOrEnded) {
            setState(MSG_STOP);
            if (this.playWhenReady) {
                startRenderers();
            }
        } else if (this.state == MSG_STOP && !allRenderersReadyOrEnded) {
            this.rebuffering = this.playWhenReady;
            setState(MSG_SET_PLAY_WHEN_READY_ACK);
            stopRenderers();
        }
        this.handler.removeMessages(MSG_DO_SOME_WORK);
        if ((this.playWhenReady && this.state == MSG_STOP) || this.state == MSG_SET_PLAY_WHEN_READY_ACK) {
            scheduleNextOperation(MSG_DO_SOME_WORK, operationStartTimeMs, 10);
        } else if (!this.enabledRenderers.isEmpty()) {
            scheduleNextOperation(MSG_DO_SOME_WORK, operationStartTimeMs, 1000);
        }
        TraceUtil.endSection();
    }

    private void scheduleNextOperation(int operationType, long thisOperationStartTimeMs, long intervalMs) {
        long nextOperationDelayMs = (thisOperationStartTimeMs + intervalMs) - SystemClock.elapsedRealtime();
        if (nextOperationDelayMs <= 0) {
            this.handler.sendEmptyMessage(operationType);
        } else {
            this.handler.sendEmptyMessageDelayed(operationType, nextOperationDelayMs);
        }
    }

    private void seekToInternal(long positionMs) throws ExoPlaybackException {
        try {
            if (positionMs != this.positionUs / 1000) {
                this.rebuffering = false;
                this.positionUs = positionMs * 1000;
                this.standaloneMediaClock.stop();
                this.standaloneMediaClock.setPositionUs(this.positionUs);
                if (this.state == MSG_PREPARED || this.state == MSG_STATE_CHANGED) {
                    this.pendingSeekCount.decrementAndGet();
                    return;
                }
                for (int i = 0; i < this.enabledRenderers.size(); i += MSG_PREPARED) {
                    TrackRenderer renderer = (TrackRenderer) this.enabledRenderers.get(i);
                    ensureStopped(renderer);
                    renderer.seekTo(this.positionUs);
                }
                setState(MSG_SET_PLAY_WHEN_READY_ACK);
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
                this.pendingSeekCount.decrementAndGet();
            }
        } finally {
            this.pendingSeekCount.decrementAndGet();
        }
    }

    private void stopInternal() {
        resetInternal();
        setState(MSG_PREPARED);
    }

    private void releaseInternal() {
        resetInternal();
        setState(MSG_PREPARED);
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private void resetInternal() {
        this.handler.removeMessages(MSG_DO_SOME_WORK);
        this.handler.removeMessages(MSG_STATE_CHANGED);
        this.rebuffering = false;
        this.standaloneMediaClock.stop();
        if (this.renderers != null) {
            for (int i = 0; i < this.renderers.length; i += MSG_PREPARED) {
                TrackRenderer renderer = this.renderers[i];
                stopAndDisable(renderer);
                release(renderer);
            }
            this.renderers = null;
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
            this.enabledRenderers.clear();
        }
    }

    private void stopAndDisable(TrackRenderer renderer) {
        try {
            ensureDisabled(renderer);
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Stop failed.", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "Stop failed.", e2);
        }
    }

    private void release(TrackRenderer renderer) {
        try {
            renderer.release();
        } catch (ExoPlaybackException e) {
            Log.e(TAG, "Release failed.", e);
        } catch (RuntimeException e2) {
            Log.e(TAG, "Release failed.", e2);
        }
    }

    private <T> void sendMessageInternal(int what, Object obj) throws ExoPlaybackException {
        try {
            Pair<ExoPlayerComponent, Object> targetAndMessage = (Pair) obj;
            ((ExoPlayerComponent) targetAndMessage.first).handleMessage(what, targetAndMessage.second);
            if (!(this.state == MSG_PREPARED || this.state == MSG_STATE_CHANGED)) {
                this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
            }
            synchronized (this) {
                this.customMessagesProcessed += MSG_PREPARED;
                notifyAll();
            }
        } catch (Throwable th) {
            synchronized (this) {
            }
            this.customMessagesProcessed += MSG_PREPARED;
            notifyAll();
        }
    }

    private void setRendererSelectedTrackInternal(int rendererIndex, int trackIndex) throws ExoPlaybackException {
        boolean joining = true;
        if (this.selectedTrackIndices[rendererIndex] != trackIndex) {
            this.selectedTrackIndices[rendererIndex] = trackIndex;
            if (this.state != MSG_PREPARED && this.state != MSG_STATE_CHANGED) {
                TrackRenderer renderer = this.renderers[rendererIndex];
                int rendererState = renderer.getState();
                if (rendererState != 0 && rendererState != -1 && renderer.getTrackCount() != 0) {
                    boolean isEnabled;
                    boolean shouldEnable;
                    if (rendererState == MSG_STATE_CHANGED || rendererState == MSG_SET_PLAY_WHEN_READY_ACK) {
                        isEnabled = true;
                    } else {
                        isEnabled = false;
                    }
                    if (trackIndex < 0 || trackIndex >= this.trackFormats[rendererIndex].length) {
                        shouldEnable = false;
                    } else {
                        shouldEnable = true;
                    }
                    if (isEnabled) {
                        if (!shouldEnable && renderer == this.rendererMediaClockSource) {
                            this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
                        }
                        ensureDisabled(renderer);
                        this.enabledRenderers.remove(renderer);
                    }
                    if (shouldEnable) {
                        boolean playing;
                        if (this.playWhenReady && this.state == MSG_STOP) {
                            playing = true;
                        } else {
                            playing = false;
                        }
                        if (isEnabled || !playing) {
                            joining = false;
                        }
                        enableRenderer(renderer, trackIndex, joining);
                        if (playing) {
                            renderer.start();
                        }
                        this.handler.sendEmptyMessage(MSG_DO_SOME_WORK);
                    }
                }
            }
        }
    }

    private void ensureStopped(TrackRenderer renderer) throws ExoPlaybackException {
        if (renderer.getState() == MSG_SET_PLAY_WHEN_READY_ACK) {
            renderer.stop();
        }
    }

    private void ensureDisabled(TrackRenderer renderer) throws ExoPlaybackException {
        ensureStopped(renderer);
        if (renderer.getState() == MSG_STATE_CHANGED) {
            renderer.disable();
            if (renderer == this.rendererMediaClockSource) {
                this.rendererMediaClock = null;
                this.rendererMediaClockSource = null;
            }
        }
    }
}
