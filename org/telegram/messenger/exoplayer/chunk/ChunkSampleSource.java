package org.telegram.messenger.exoplayer.chunk;

import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.LoadControl;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;
import org.telegram.messenger.exoplayer.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;

public class ChunkSampleSource implements SampleSource, SampleSourceReader, Callback {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final long NO_RESET_PENDING = Long.MIN_VALUE;
    private static final int STATE_ENABLED = 3;
    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_PREPARED = 2;
    private final int bufferSizeContribution;
    private final ChunkSource chunkSource;
    private long currentLoadStartTimeMs;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private final ChunkOperationHolder currentLoadableHolder;
    private Format downstreamFormat;
    private MediaFormat downstreamMediaFormat;
    private long downstreamPositionUs;
    private int enabledTrackCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private long lastPerformedBufferOperation;
    private long lastSeekPositionUs;
    private final LoadControl loadControl;
    private Loader loader;
    private boolean loadingFinished;
    private final LinkedList<BaseMediaChunk> mediaChunks;
    private final int minLoadableRetryCount;
    private boolean pendingDiscontinuity;
    private long pendingResetPositionUs;
    private final List<BaseMediaChunk> readOnlyMediaChunks;
    protected final DefaultTrackOutput sampleQueue;
    private int state;

    /* renamed from: org.telegram.messenger.exoplayer.chunk.ChunkSampleSource.1 */
    class C07651 implements Runnable {
        final /* synthetic */ Format val$format;
        final /* synthetic */ long val$length;
        final /* synthetic */ long val$mediaEndTimeUs;
        final /* synthetic */ long val$mediaStartTimeUs;
        final /* synthetic */ int val$trigger;
        final /* synthetic */ int val$type;

        C07651(long j, int i, int i2, Format format, long j2, long j3) {
            this.val$length = j;
            this.val$type = i;
            this.val$trigger = i2;
            this.val$format = format;
            this.val$mediaStartTimeUs = j2;
            this.val$mediaEndTimeUs = j3;
        }

        public void run() {
            ChunkSampleSource.this.eventListener.onLoadStarted(ChunkSampleSource.this.eventSourceId, this.val$length, this.val$type, this.val$trigger, this.val$format, ChunkSampleSource.this.usToMs(this.val$mediaStartTimeUs), ChunkSampleSource.this.usToMs(this.val$mediaEndTimeUs));
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.chunk.ChunkSampleSource.2 */
    class C07662 implements Runnable {
        final /* synthetic */ long val$bytesLoaded;
        final /* synthetic */ long val$elapsedRealtimeMs;
        final /* synthetic */ Format val$format;
        final /* synthetic */ long val$loadDurationMs;
        final /* synthetic */ long val$mediaEndTimeUs;
        final /* synthetic */ long val$mediaStartTimeUs;
        final /* synthetic */ int val$trigger;
        final /* synthetic */ int val$type;

        C07662(long j, int i, int i2, Format format, long j2, long j3, long j4, long j5) {
            this.val$bytesLoaded = j;
            this.val$type = i;
            this.val$trigger = i2;
            this.val$format = format;
            this.val$mediaStartTimeUs = j2;
            this.val$mediaEndTimeUs = j3;
            this.val$elapsedRealtimeMs = j4;
            this.val$loadDurationMs = j5;
        }

        public void run() {
            ChunkSampleSource.this.eventListener.onLoadCompleted(ChunkSampleSource.this.eventSourceId, this.val$bytesLoaded, this.val$type, this.val$trigger, this.val$format, ChunkSampleSource.this.usToMs(this.val$mediaStartTimeUs), ChunkSampleSource.this.usToMs(this.val$mediaEndTimeUs), this.val$elapsedRealtimeMs, this.val$loadDurationMs);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.chunk.ChunkSampleSource.3 */
    class C07673 implements Runnable {
        final /* synthetic */ long val$bytesLoaded;

        C07673(long j) {
            this.val$bytesLoaded = j;
        }

        public void run() {
            ChunkSampleSource.this.eventListener.onLoadCanceled(ChunkSampleSource.this.eventSourceId, this.val$bytesLoaded);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.chunk.ChunkSampleSource.4 */
    class C07684 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07684(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            ChunkSampleSource.this.eventListener.onLoadError(ChunkSampleSource.this.eventSourceId, this.val$e);
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.chunk.ChunkSampleSource.5 */
    class C07695 implements Runnable {
        final /* synthetic */ long val$mediaEndTimeUs;
        final /* synthetic */ long val$mediaStartTimeUs;

        C07695(long j, long j2) {
            this.val$mediaStartTimeUs = j;
            this.val$mediaEndTimeUs = j2;
        }

        public void run() {
            ChunkSampleSource.this.eventListener.onUpstreamDiscarded(ChunkSampleSource.this.eventSourceId, ChunkSampleSource.this.usToMs(this.val$mediaStartTimeUs), ChunkSampleSource.this.usToMs(this.val$mediaEndTimeUs));
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer.chunk.ChunkSampleSource.6 */
    class C07706 implements Runnable {
        final /* synthetic */ Format val$format;
        final /* synthetic */ long val$positionUs;
        final /* synthetic */ int val$trigger;

        C07706(Format format, int i, long j) {
            this.val$format = format;
            this.val$trigger = i;
            this.val$positionUs = j;
        }

        public void run() {
            ChunkSampleSource.this.eventListener.onDownstreamFormatChanged(ChunkSampleSource.this.eventSourceId, this.val$format, this.val$trigger, ChunkSampleSource.this.usToMs(this.val$positionUs));
        }
    }

    public interface EventListener extends BaseChunkSampleSourceEventListener {
    }

    public ChunkSampleSource(ChunkSource chunkSource, LoadControl loadControl, int bufferSizeContribution) {
        this(chunkSource, loadControl, bufferSizeContribution, null, null, STATE_IDLE);
    }

    public ChunkSampleSource(ChunkSource chunkSource, LoadControl loadControl, int bufferSizeContribution, Handler eventHandler, EventListener eventListener, int eventSourceId) {
        this(chunkSource, loadControl, bufferSizeContribution, eventHandler, eventListener, eventSourceId, STATE_ENABLED);
    }

    public ChunkSampleSource(ChunkSource chunkSource, LoadControl loadControl, int bufferSizeContribution, Handler eventHandler, EventListener eventListener, int eventSourceId, int minLoadableRetryCount) {
        this.chunkSource = chunkSource;
        this.loadControl = loadControl;
        this.bufferSizeContribution = bufferSizeContribution;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.eventSourceId = eventSourceId;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.currentLoadableHolder = new ChunkOperationHolder();
        this.mediaChunks = new LinkedList();
        this.readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
        this.sampleQueue = new DefaultTrackOutput(loadControl.getAllocator());
        this.state = STATE_IDLE;
        this.pendingResetPositionUs = NO_RESET_PENDING;
    }

    public SampleSourceReader register() {
        Assertions.checkState(this.state == 0);
        this.state = STATE_INITIALIZED;
        return this;
    }

    public boolean prepare(long positionUs) {
        boolean z = this.state == STATE_INITIALIZED || this.state == STATE_PREPARED;
        Assertions.checkState(z);
        if (this.state == STATE_PREPARED) {
            return true;
        }
        if (!this.chunkSource.prepare()) {
            return false;
        }
        if (this.chunkSource.getTrackCount() > 0) {
            this.loader = new Loader("Loader:" + this.chunkSource.getFormat(STATE_IDLE).mimeType);
        }
        this.state = STATE_PREPARED;
        return true;
    }

    public int getTrackCount() {
        boolean z = this.state == STATE_PREPARED || this.state == STATE_ENABLED;
        Assertions.checkState(z);
        return this.chunkSource.getTrackCount();
    }

    public MediaFormat getFormat(int track) {
        boolean z = this.state == STATE_PREPARED || this.state == STATE_ENABLED;
        Assertions.checkState(z);
        return this.chunkSource.getFormat(track);
    }

    public void enable(int track, long positionUs) {
        boolean z = true;
        Assertions.checkState(this.state == STATE_PREPARED);
        int i = this.enabledTrackCount;
        this.enabledTrackCount = i + STATE_INITIALIZED;
        if (i != 0) {
            z = false;
        }
        Assertions.checkState(z);
        this.state = STATE_ENABLED;
        this.chunkSource.enable(track);
        this.loadControl.register(this, this.bufferSizeContribution);
        this.downstreamFormat = null;
        this.downstreamMediaFormat = null;
        this.downstreamPositionUs = positionUs;
        this.lastSeekPositionUs = positionUs;
        this.pendingDiscontinuity = false;
        restartFrom(positionUs);
    }

    public void disable(int track) {
        boolean z = true;
        Assertions.checkState(this.state == STATE_ENABLED);
        int i = this.enabledTrackCount - 1;
        this.enabledTrackCount = i;
        if (i != 0) {
            z = false;
        }
        Assertions.checkState(z);
        this.state = STATE_PREPARED;
        try {
            this.chunkSource.disable(this.mediaChunks);
            this.loadControl.unregister(this);
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
                return;
            }
            this.sampleQueue.clear();
            this.mediaChunks.clear();
            clearCurrentLoadable();
            this.loadControl.trimAllocator();
        } catch (Throwable th) {
            this.loadControl.unregister(this);
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                this.sampleQueue.clear();
                this.mediaChunks.clear();
                clearCurrentLoadable();
                this.loadControl.trimAllocator();
            }
        }
    }

    public boolean continueBuffering(int track, long positionUs) {
        boolean z;
        if (this.state == STATE_ENABLED) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        this.downstreamPositionUs = positionUs;
        this.chunkSource.continueBuffering(positionUs);
        updateLoadControl();
        if (this.loadingFinished || !this.sampleQueue.isEmpty()) {
            return true;
        }
        return false;
    }

    public long readDiscontinuity(int track) {
        if (!this.pendingDiscontinuity) {
            return NO_RESET_PENDING;
        }
        this.pendingDiscontinuity = false;
        return this.lastSeekPositionUs;
    }

    public int readData(int track, long positionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder) {
        Assertions.checkState(this.state == STATE_ENABLED);
        this.downstreamPositionUs = positionUs;
        if (this.pendingDiscontinuity || isPendingReset()) {
            return -2;
        }
        boolean haveSamples = !this.sampleQueue.isEmpty();
        BaseMediaChunk currentChunk = (BaseMediaChunk) this.mediaChunks.getFirst();
        while (haveSamples && this.mediaChunks.size() > STATE_INITIALIZED && ((BaseMediaChunk) this.mediaChunks.get(STATE_INITIALIZED)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()) {
            this.mediaChunks.removeFirst();
            currentChunk = (BaseMediaChunk) this.mediaChunks.getFirst();
        }
        Format format = currentChunk.format;
        if (!format.equals(this.downstreamFormat)) {
            notifyDownstreamFormatChanged(format, currentChunk.trigger, currentChunk.startTimeUs);
        }
        this.downstreamFormat = format;
        if (haveSamples || currentChunk.isMediaFormatFinal) {
            MediaFormat mediaFormat = currentChunk.getMediaFormat();
            if (mediaFormat.equals(this.downstreamMediaFormat)) {
                this.downstreamMediaFormat = mediaFormat;
            } else {
                formatHolder.format = mediaFormat;
                formatHolder.drmInitData = currentChunk.getDrmInitData();
                this.downstreamMediaFormat = mediaFormat;
                return -4;
            }
        }
        if (haveSamples) {
            if (!this.sampleQueue.getSample(sampleHolder)) {
                return -2;
            }
            boolean decodeOnly = sampleHolder.timeUs < this.lastSeekPositionUs;
            sampleHolder.flags = (decodeOnly ? C0747C.SAMPLE_FLAG_DECODE_ONLY : STATE_IDLE) | sampleHolder.flags;
            onSampleRead(currentChunk, sampleHolder);
            return -3;
        } else if (this.loadingFinished) {
            return -1;
        } else {
            return -2;
        }
    }

    public void seekToUs(long positionUs) {
        boolean z;
        if (this.state == STATE_ENABLED) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        long currentPositionUs = isPendingReset() ? this.pendingResetPositionUs : this.downstreamPositionUs;
        this.downstreamPositionUs = positionUs;
        this.lastSeekPositionUs = positionUs;
        if (currentPositionUs != positionUs) {
            boolean seekInsideBuffer;
            if (isPendingReset() || !this.sampleQueue.skipToKeyframeBefore(positionUs)) {
                seekInsideBuffer = false;
            } else {
                seekInsideBuffer = true;
            }
            if (seekInsideBuffer) {
                boolean haveSamples;
                if (this.sampleQueue.isEmpty()) {
                    haveSamples = false;
                } else {
                    haveSamples = true;
                }
                while (haveSamples && this.mediaChunks.size() > STATE_INITIALIZED && ((BaseMediaChunk) this.mediaChunks.get(STATE_INITIALIZED)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()) {
                    this.mediaChunks.removeFirst();
                }
            } else {
                restartFrom(positionUs);
            }
            this.pendingDiscontinuity = true;
        }
    }

    public void maybeThrowError() throws IOException {
        if (this.currentLoadableException != null && this.currentLoadableExceptionCount > this.minLoadableRetryCount) {
            throw this.currentLoadableException;
        } else if (this.currentLoadableHolder.chunk == null) {
            this.chunkSource.maybeThrowError();
        }
    }

    public long getBufferedPositionUs() {
        Assertions.checkState(this.state == STATE_ENABLED);
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        if (this.loadingFinished) {
            return -3;
        }
        long largestParsedTimestampUs = this.sampleQueue.getLargestParsedTimestampUs();
        return largestParsedTimestampUs == NO_RESET_PENDING ? this.downstreamPositionUs : largestParsedTimestampUs;
    }

    public void release() {
        Assertions.checkState(this.state != STATE_ENABLED);
        if (this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
        this.state = STATE_IDLE;
    }

    public void onLoadCompleted(Loadable loadable) {
        long now = SystemClock.elapsedRealtime();
        long loadDurationMs = now - this.currentLoadStartTimeMs;
        Chunk currentLoadable = this.currentLoadableHolder.chunk;
        this.chunkSource.onChunkLoadCompleted(currentLoadable);
        if (isMediaChunk(currentLoadable)) {
            BaseMediaChunk mediaChunk = (BaseMediaChunk) currentLoadable;
            notifyLoadCompleted(currentLoadable.bytesLoaded(), mediaChunk.type, mediaChunk.trigger, mediaChunk.format, mediaChunk.startTimeUs, mediaChunk.endTimeUs, now, loadDurationMs);
        } else {
            notifyLoadCompleted(currentLoadable.bytesLoaded(), currentLoadable.type, currentLoadable.trigger, currentLoadable.format, -1, -1, now, loadDurationMs);
        }
        clearCurrentLoadable();
        updateLoadControl();
    }

    public void onLoadCanceled(Loadable loadable) {
        notifyLoadCanceled(this.currentLoadableHolder.chunk.bytesLoaded());
        clearCurrentLoadable();
        if (this.state == STATE_ENABLED) {
            restartFrom(this.pendingResetPositionUs);
            return;
        }
        this.sampleQueue.clear();
        this.mediaChunks.clear();
        clearCurrentLoadable();
        this.loadControl.trimAllocator();
    }

    public void onLoadError(Loadable loadable, IOException e) {
        this.currentLoadableException = e;
        this.currentLoadableExceptionCount += STATE_INITIALIZED;
        this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        notifyLoadError(e);
        this.chunkSource.onChunkLoadError(this.currentLoadableHolder.chunk, e);
        updateLoadControl();
    }

    protected void onSampleRead(MediaChunk mediaChunk, SampleHolder sampleHolder) {
    }

    private void restartFrom(long positionUs) {
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        this.sampleQueue.clear();
        this.mediaChunks.clear();
        clearCurrentLoadable();
        updateLoadControl();
    }

    private void clearCurrentLoadable() {
        this.currentLoadableHolder.chunk = null;
        clearCurrentLoadableException();
    }

    private void clearCurrentLoadableException() {
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = STATE_IDLE;
    }

    private void updateLoadControl() {
        boolean isBackedOff;
        boolean loadingOrBackedOff;
        long now = SystemClock.elapsedRealtime();
        long nextLoadPositionUs = getNextLoadPositionUs();
        if (this.currentLoadableException != null) {
            isBackedOff = true;
        } else {
            isBackedOff = false;
        }
        if (this.loader.isLoading() || isBackedOff) {
            loadingOrBackedOff = true;
        } else {
            loadingOrBackedOff = false;
        }
        if (!loadingOrBackedOff && ((this.currentLoadableHolder.chunk == null && nextLoadPositionUs != -1) || now - this.lastPerformedBufferOperation > 2000)) {
            this.lastPerformedBufferOperation = now;
            doChunkOperation();
            boolean chunksDiscarded = discardUpstreamMediaChunks(this.currentLoadableHolder.queueSize);
            if (this.currentLoadableHolder.chunk == null) {
                nextLoadPositionUs = -1;
            } else if (chunksDiscarded) {
                nextLoadPositionUs = getNextLoadPositionUs();
            }
        }
        boolean nextLoader = this.loadControl.update(this, this.downstreamPositionUs, nextLoadPositionUs, loadingOrBackedOff);
        if (isBackedOff) {
            if (now - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                resumeFromBackOff();
            }
        } else if (!this.loader.isLoading() && nextLoader) {
            maybeStartLoading();
        }
    }

    private long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        return this.loadingFinished ? -1 : ((BaseMediaChunk) this.mediaChunks.getLast()).endTimeUs;
    }

    private void resumeFromBackOff() {
        this.currentLoadableException = null;
        Chunk backedOffChunk = this.currentLoadableHolder.chunk;
        if (!isMediaChunk(backedOffChunk)) {
            doChunkOperation();
            discardUpstreamMediaChunks(this.currentLoadableHolder.queueSize);
            if (this.currentLoadableHolder.chunk == backedOffChunk) {
                this.loader.startLoading(backedOffChunk, this);
                return;
            }
            notifyLoadCanceled(backedOffChunk.bytesLoaded());
            maybeStartLoading();
        } else if (backedOffChunk == this.mediaChunks.getFirst()) {
            this.loader.startLoading(backedOffChunk, this);
        } else {
            Chunk removedChunk = (BaseMediaChunk) this.mediaChunks.removeLast();
            Assertions.checkState(backedOffChunk == removedChunk);
            doChunkOperation();
            this.mediaChunks.add(removedChunk);
            if (this.currentLoadableHolder.chunk == backedOffChunk) {
                this.loader.startLoading(backedOffChunk, this);
                return;
            }
            notifyLoadCanceled(backedOffChunk.bytesLoaded());
            discardUpstreamMediaChunks(this.currentLoadableHolder.queueSize);
            clearCurrentLoadableException();
            maybeStartLoading();
        }
    }

    private void maybeStartLoading() {
        Chunk currentLoadable = this.currentLoadableHolder.chunk;
        if (currentLoadable != null) {
            this.currentLoadStartTimeMs = SystemClock.elapsedRealtime();
            if (isMediaChunk(currentLoadable)) {
                BaseMediaChunk mediaChunk = (BaseMediaChunk) currentLoadable;
                mediaChunk.init(this.sampleQueue);
                this.mediaChunks.add(mediaChunk);
                if (isPendingReset()) {
                    this.pendingResetPositionUs = NO_RESET_PENDING;
                }
                notifyLoadStarted(mediaChunk.dataSpec.length, mediaChunk.type, mediaChunk.trigger, mediaChunk.format, mediaChunk.startTimeUs, mediaChunk.endTimeUs);
            } else {
                notifyLoadStarted(currentLoadable.dataSpec.length, currentLoadable.type, currentLoadable.trigger, currentLoadable.format, -1, -1);
            }
            this.loader.startLoading(currentLoadable, this);
        }
    }

    private void doChunkOperation() {
        this.currentLoadableHolder.endOfStream = false;
        this.currentLoadableHolder.queueSize = this.readOnlyMediaChunks.size();
        this.chunkSource.getChunkOperation(this.readOnlyMediaChunks, this.pendingResetPositionUs != NO_RESET_PENDING ? this.pendingResetPositionUs : this.downstreamPositionUs, this.currentLoadableHolder);
        this.loadingFinished = this.currentLoadableHolder.endOfStream;
    }

    private boolean discardUpstreamMediaChunks(int queueLength) {
        if (this.mediaChunks.size() <= queueLength) {
            return false;
        }
        long startTimeUs = 0;
        long endTimeUs = ((BaseMediaChunk) this.mediaChunks.getLast()).endTimeUs;
        BaseMediaChunk removed = null;
        while (this.mediaChunks.size() > queueLength) {
            removed = (BaseMediaChunk) this.mediaChunks.removeLast();
            startTimeUs = removed.startTimeUs;
            this.loadingFinished = false;
        }
        this.sampleQueue.discardUpstreamSamples(removed.getFirstSampleIndex());
        notifyUpstreamDiscarded(startTimeUs, endTimeUs);
        return true;
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != NO_RESET_PENDING;
    }

    private long getRetryDelayMillis(long errorCount) {
        return Math.min((errorCount - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    protected final long usToMs(long timeUs) {
        return timeUs / 1000;
    }

    private void notifyLoadStarted(long length, int type, int trigger, Format format, long mediaStartTimeUs, long mediaEndTimeUs) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07651(length, type, trigger, format, mediaStartTimeUs, mediaEndTimeUs));
        }
    }

    private void notifyLoadCompleted(long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07662(bytesLoaded, type, trigger, format, mediaStartTimeUs, mediaEndTimeUs, elapsedRealtimeMs, loadDurationMs));
        }
    }

    private void notifyLoadCanceled(long bytesLoaded) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07673(bytesLoaded));
        }
    }

    private void notifyLoadError(IOException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07684(e));
        }
    }

    private void notifyUpstreamDiscarded(long mediaStartTimeUs, long mediaEndTimeUs) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07695(mediaStartTimeUs, mediaEndTimeUs));
        }
    }

    private void notifyDownstreamFormatChanged(Format format, int trigger, long positionUs) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07706(format, trigger, positionUs));
        }
    }
}
