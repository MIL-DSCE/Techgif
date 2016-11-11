package org.telegram.messenger.exoplayer.extractor;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseArray;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public final class ExtractorSampleSource implements SampleSource, SampleSourceReader, ExtractorOutput, Callback {
    private static final List<Class<? extends Extractor>> DEFAULT_EXTRACTOR_CLASSES;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE = 6;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND = 3;
    private static final int MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA = -1;
    private static final long NO_RESET_PENDING = Long.MIN_VALUE;
    private final Allocator allocator;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private final DataSource dataSource;
    private long downstreamPositionUs;
    private volatile DrmInitData drmInitData;
    private int enabledTrackCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private int extractedSampleCount;
    private int extractedSampleCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private boolean havePendingNextSampleUs;
    private long lastSeekPositionUs;
    private ExtractingLoadable loadable;
    private Loader loader;
    private boolean loadingFinished;
    private long maxTrackDurationUs;
    private MediaFormat[] mediaFormats;
    private final int minLoadableRetryCount;
    private boolean[] pendingDiscontinuities;
    private boolean[] pendingMediaFormat;
    private long pendingNextSampleUs;
    private long pendingResetPositionUs;
    private boolean prepared;
    private int remainingReleaseCount;
    private final int requestedBufferSize;
    private final SparseArray<InternalTrackOutput> sampleQueues;
    private long sampleTimeOffsetUs;
    private volatile SeekMap seekMap;
    private boolean[] trackEnabledStates;
    private volatile boolean tracksBuilt;
    private final Uri uri;

    /* renamed from: org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource.1 */
    class C07731 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07731(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            ExtractorSampleSource.this.eventListener.onLoadError(ExtractorSampleSource.this.eventSourceId, this.val$e);
        }
    }

    public interface EventListener {
        void onLoadError(int i, IOException iOException);
    }

    private static final class ExtractorHolder {
        private Extractor extractor;
        private final ExtractorOutput extractorOutput;
        private final Extractor[] extractors;

        public ExtractorHolder(Extractor[] extractors, ExtractorOutput extractorOutput) {
            this.extractors = extractors;
            this.extractorOutput = extractorOutput;
        }

        public Extractor selectExtractor(ExtractorInput input) throws UnrecognizedInputFormatException, IOException, InterruptedException {
            if (this.extractor != null) {
                return this.extractor;
            }
            Extractor[] arr$ = this.extractors;
            int len$ = arr$.length;
            int i$ = 0;
            loop0:
            while (i$ < len$) {
                Extractor extractor = arr$[i$];
                try {
                    if (extractor.sniff(input)) {
                        this.extractor = extractor;
                        input.resetPeekPosition();
                        break loop0;
                    }
                    i$++;
                } catch (EOFException e) {
                    i$++;
                } finally {
                    input.resetPeekPosition();
                }
            }
            if (this.extractor == null) {
                throw new UnrecognizedInputFormatException(this.extractors);
            }
            this.extractor.init(this.extractorOutput);
            return this.extractor;
        }
    }

    private static class ExtractingLoadable implements Loadable {
        private final Allocator allocator;
        private final DataSource dataSource;
        private final ExtractorHolder extractorHolder;
        private volatile boolean loadCanceled;
        private boolean pendingExtractorSeek;
        private final PositionHolder positionHolder;
        private final int requestedBufferSize;
        private final Uri uri;

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, Allocator allocator, int requestedBufferSize, long position) {
            this.uri = (Uri) Assertions.checkNotNull(uri);
            this.dataSource = (DataSource) Assertions.checkNotNull(dataSource);
            this.extractorHolder = (ExtractorHolder) Assertions.checkNotNull(extractorHolder);
            this.allocator = (Allocator) Assertions.checkNotNull(allocator);
            this.requestedBufferSize = requestedBufferSize;
            this.positionHolder = new PositionHolder();
            this.positionHolder.position = position;
            this.pendingExtractorSeek = true;
        }

        public void cancelLoad() {
            this.loadCanceled = true;
        }

        public boolean isLoadCanceled() {
            return this.loadCanceled;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void load() throws java.io.IOException, java.lang.InterruptedException {
            /*
            r12 = this;
            r9 = 0;
        L_0x0001:
            if (r9 != 0) goto L_0x007a;
        L_0x0003:
            r1 = r12.loadCanceled;
            if (r1 != 0) goto L_0x007a;
        L_0x0007:
            r8 = 0;
            r1 = r12.positionHolder;	 Catch:{ all -> 0x0063 }
            r2 = r1.position;	 Catch:{ all -> 0x0063 }
            r10 = r12.dataSource;	 Catch:{ all -> 0x0063 }
            r0 = new org.telegram.messenger.exoplayer.upstream.DataSpec;	 Catch:{ all -> 0x0063 }
            r1 = r12.uri;	 Catch:{ all -> 0x0063 }
            r4 = -1;
            r6 = 0;
            r0.<init>(r1, r2, r4, r6);	 Catch:{ all -> 0x0063 }
            r4 = r10.open(r0);	 Catch:{ all -> 0x0063 }
            r10 = -1;
            r1 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
            if (r1 == 0) goto L_0x0023;
        L_0x0022:
            r4 = r4 + r2;
        L_0x0023:
            r0 = new org.telegram.messenger.exoplayer.extractor.DefaultExtractorInput;	 Catch:{ all -> 0x0063 }
            r1 = r12.dataSource;	 Catch:{ all -> 0x0063 }
            r0.<init>(r1, r2, r4);	 Catch:{ all -> 0x0063 }
            r1 = r12.extractorHolder;	 Catch:{ all -> 0x007b }
            r7 = r1.selectExtractor(r0);	 Catch:{ all -> 0x007b }
            r1 = r12.pendingExtractorSeek;	 Catch:{ all -> 0x007b }
            if (r1 == 0) goto L_0x003a;
        L_0x0034:
            r7.seek();	 Catch:{ all -> 0x007b }
            r1 = 0;
            r12.pendingExtractorSeek = r1;	 Catch:{ all -> 0x007b }
        L_0x003a:
            if (r9 != 0) goto L_0x004e;
        L_0x003c:
            r1 = r12.loadCanceled;	 Catch:{ all -> 0x007b }
            if (r1 != 0) goto L_0x004e;
        L_0x0040:
            r1 = r12.allocator;	 Catch:{ all -> 0x007b }
            r6 = r12.requestedBufferSize;	 Catch:{ all -> 0x007b }
            r1.blockWhileTotalBytesAllocatedExceeds(r6);	 Catch:{ all -> 0x007b }
            r1 = r12.positionHolder;	 Catch:{ all -> 0x007b }
            r9 = r7.read(r0, r1);	 Catch:{ all -> 0x007b }
            goto L_0x003a;
        L_0x004e:
            r1 = 1;
            if (r9 != r1) goto L_0x0058;
        L_0x0051:
            r9 = 0;
        L_0x0052:
            r1 = r12.dataSource;
            r1.close();
            goto L_0x0001;
        L_0x0058:
            if (r0 == 0) goto L_0x0052;
        L_0x005a:
            r1 = r12.positionHolder;
            r10 = r0.getPosition();
            r1.position = r10;
            goto L_0x0052;
        L_0x0063:
            r1 = move-exception;
            r0 = r8;
        L_0x0065:
            r6 = 1;
            if (r9 != r6) goto L_0x006f;
        L_0x0068:
            r9 = 0;
        L_0x0069:
            r6 = r12.dataSource;
            r6.close();
            throw r1;
        L_0x006f:
            if (r0 == 0) goto L_0x0069;
        L_0x0071:
            r6 = r12.positionHolder;
            r10 = r0.getPosition();
            r6.position = r10;
            goto L_0x0069;
        L_0x007a:
            return;
        L_0x007b:
            r1 = move-exception;
            goto L_0x0065;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource.ExtractingLoadable.load():void");
        }
    }

    public static final class UnrecognizedInputFormatException extends ParserException {
        public UnrecognizedInputFormatException(Extractor[] extractors) {
            super("None of the available extractors (" + Util.getCommaDelimitedSimpleClassNames(extractors) + ") could read the stream.");
        }
    }

    private class InternalTrackOutput extends DefaultTrackOutput {
        public InternalTrackOutput(Allocator allocator) {
            super(allocator);
        }

        public void sampleMetadata(long timeUs, int flags, int size, int offset, byte[] encryptionKey) {
            super.sampleMetadata(timeUs, flags, size, offset, encryptionKey);
            ExtractorSampleSource.this.extractedSampleCount = ExtractorSampleSource.this.extractedSampleCount + 1;
        }
    }

    static {
        DEFAULT_EXTRACTOR_CLASSES = new ArrayList();
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.webm.WebmExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.mp4.FragmentedMp4Extractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e2) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.mp4.Mp4Extractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e3) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.mp3.Mp3Extractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e4) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.ts.AdtsExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e5) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.ts.TsExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e6) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.flv.FlvExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e7) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.ogg.OggExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e8) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.ts.PsExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e9) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.extractor.wav.WavExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e10) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.ext.flac.FlacExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e11) {
        }
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int requestedBufferSize, Extractor... extractors) {
        this(uri, dataSource, allocator, requestedBufferSize, MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA, extractors);
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int requestedBufferSize, Handler eventHandler, EventListener eventListener, int eventSourceId, Extractor... extractors) {
        this(uri, dataSource, allocator, requestedBufferSize, MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA, eventHandler, eventListener, eventSourceId, extractors);
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int requestedBufferSize, int minLoadableRetryCount, Extractor... extractors) {
        this(uri, dataSource, allocator, requestedBufferSize, minLoadableRetryCount, null, null, 0, extractors);
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int requestedBufferSize, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, int eventSourceId, Extractor... extractors) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.eventListener = eventListener;
        this.eventHandler = eventHandler;
        this.eventSourceId = eventSourceId;
        this.allocator = allocator;
        this.requestedBufferSize = requestedBufferSize;
        this.minLoadableRetryCount = minLoadableRetryCount;
        if (extractors == null || extractors.length == 0) {
            extractors = new Extractor[DEFAULT_EXTRACTOR_CLASSES.size()];
            int i = 0;
            while (i < extractors.length) {
                try {
                    extractors[i] = (Extractor) ((Class) DEFAULT_EXTRACTOR_CLASSES.get(i)).newInstance();
                    i++;
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Unexpected error creating default extractor", e);
                } catch (IllegalAccessException e2) {
                    throw new IllegalStateException("Unexpected error creating default extractor", e2);
                }
            }
        }
        this.extractorHolder = new ExtractorHolder(extractors, this);
        this.sampleQueues = new SparseArray();
        this.pendingResetPositionUs = NO_RESET_PENDING;
    }

    public SampleSourceReader register() {
        this.remainingReleaseCount++;
        return this;
    }

    public boolean prepare(long positionUs) {
        if (this.prepared) {
            return true;
        }
        if (this.loader == null) {
            this.loader = new Loader("Loader:ExtractorSampleSource");
        }
        maybeStartLoading();
        if (this.seekMap == null || !this.tracksBuilt || !haveFormatsForAllTracks()) {
            return false;
        }
        int trackCount = this.sampleQueues.size();
        this.trackEnabledStates = new boolean[trackCount];
        this.pendingDiscontinuities = new boolean[trackCount];
        this.pendingMediaFormat = new boolean[trackCount];
        this.mediaFormats = new MediaFormat[trackCount];
        this.maxTrackDurationUs = -1;
        for (int i = 0; i < trackCount; i++) {
            MediaFormat format = ((InternalTrackOutput) this.sampleQueues.valueAt(i)).getFormat();
            this.mediaFormats[i] = format;
            if (format.durationUs != -1 && format.durationUs > this.maxTrackDurationUs) {
                this.maxTrackDurationUs = format.durationUs;
            }
        }
        this.prepared = true;
        return true;
    }

    public int getTrackCount() {
        return this.sampleQueues.size();
    }

    public MediaFormat getFormat(int track) {
        Assertions.checkState(this.prepared);
        return this.mediaFormats[track];
    }

    public void enable(int track, long positionUs) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(!this.trackEnabledStates[track]);
        this.enabledTrackCount++;
        this.trackEnabledStates[track] = true;
        this.pendingMediaFormat[track] = true;
        this.pendingDiscontinuities[track] = false;
        if (this.enabledTrackCount == 1) {
            if (!this.seekMap.isSeekable()) {
                positionUs = 0;
            }
            this.downstreamPositionUs = positionUs;
            this.lastSeekPositionUs = positionUs;
            restartFrom(positionUs);
        }
    }

    public void disable(int track) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackEnabledStates[track]);
        this.enabledTrackCount += MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA;
        this.trackEnabledStates[track] = false;
        if (this.enabledTrackCount == 0) {
            this.downstreamPositionUs = NO_RESET_PENDING;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
                return;
            }
            clearState();
            this.allocator.trim(0);
        }
    }

    public boolean continueBuffering(int track, long playbackPositionUs) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackEnabledStates[track]);
        this.downstreamPositionUs = playbackPositionUs;
        discardSamplesForDisabledTracks(this.downstreamPositionUs);
        if (this.loadingFinished) {
            return true;
        }
        maybeStartLoading();
        if (isPendingReset()) {
            return false;
        }
        return !((InternalTrackOutput) this.sampleQueues.valueAt(track)).isEmpty();
    }

    public long readDiscontinuity(int track) {
        if (!this.pendingDiscontinuities[track]) {
            return NO_RESET_PENDING;
        }
        this.pendingDiscontinuities[track] = false;
        return this.lastSeekPositionUs;
    }

    public int readData(int track, long playbackPositionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder) {
        this.downstreamPositionUs = playbackPositionUs;
        if (this.pendingDiscontinuities[track] || isPendingReset()) {
            return -2;
        }
        InternalTrackOutput sampleQueue = (InternalTrackOutput) this.sampleQueues.valueAt(track);
        if (this.pendingMediaFormat[track]) {
            formatHolder.format = sampleQueue.getFormat();
            formatHolder.drmInitData = this.drmInitData;
            this.pendingMediaFormat[track] = false;
            return -4;
        } else if (!sampleQueue.getSample(sampleHolder)) {
            return this.loadingFinished ? MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA : -2;
        } else {
            boolean decodeOnly;
            int i;
            if (sampleHolder.timeUs < this.lastSeekPositionUs) {
                decodeOnly = true;
            } else {
                decodeOnly = false;
            }
            int i2 = sampleHolder.flags;
            if (decodeOnly) {
                i = C0747C.SAMPLE_FLAG_DECODE_ONLY;
            } else {
                i = 0;
            }
            sampleHolder.flags = i | i2;
            if (this.havePendingNextSampleUs) {
                this.sampleTimeOffsetUs = this.pendingNextSampleUs - sampleHolder.timeUs;
                this.havePendingNextSampleUs = false;
            }
            sampleHolder.timeUs += this.sampleTimeOffsetUs;
            return -3;
        }
    }

    public void maybeThrowError() throws IOException {
        if (this.currentLoadableException != null) {
            if (isCurrentLoadableExceptionFatal()) {
                throw this.currentLoadableException;
            }
            int minLoadableRetryCountForMedia;
            if (this.minLoadableRetryCount != MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA) {
                minLoadableRetryCountForMedia = this.minLoadableRetryCount;
            } else {
                minLoadableRetryCountForMedia = (this.seekMap == null || this.seekMap.isSeekable()) ? DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND : DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE;
            }
            if (this.currentLoadableExceptionCount > minLoadableRetryCountForMedia) {
                throw this.currentLoadableException;
            }
        }
    }

    public void seekToUs(long positionUs) {
        boolean z;
        Assertions.checkState(this.prepared);
        if (this.enabledTrackCount > 0) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (!this.seekMap.isSeekable()) {
            positionUs = 0;
        }
        long currentPositionUs = isPendingReset() ? this.pendingResetPositionUs : this.downstreamPositionUs;
        this.downstreamPositionUs = positionUs;
        this.lastSeekPositionUs = positionUs;
        if (currentPositionUs != positionUs) {
            boolean seekInsideBuffer;
            if (isPendingReset()) {
                seekInsideBuffer = false;
            } else {
                seekInsideBuffer = true;
            }
            int i = 0;
            while (seekInsideBuffer && i < this.sampleQueues.size()) {
                seekInsideBuffer &= ((InternalTrackOutput) this.sampleQueues.valueAt(i)).skipToKeyframeBefore(positionUs);
                i++;
            }
            if (!seekInsideBuffer) {
                restartFrom(positionUs);
            }
            for (i = 0; i < this.pendingDiscontinuities.length; i++) {
                this.pendingDiscontinuities[i] = true;
            }
        }
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return -3;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long largestParsedTimestampUs = NO_RESET_PENDING;
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            largestParsedTimestampUs = Math.max(largestParsedTimestampUs, ((InternalTrackOutput) this.sampleQueues.valueAt(i)).getLargestParsedTimestampUs());
        }
        return largestParsedTimestampUs == NO_RESET_PENDING ? this.downstreamPositionUs : largestParsedTimestampUs;
    }

    public void release() {
        Assertions.checkState(this.remainingReleaseCount > 0);
        int i = this.remainingReleaseCount + MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA;
        this.remainingReleaseCount = i;
        if (i == 0) {
            if (this.loader != null) {
                this.loader.release();
                this.loader = null;
            }
            if (this.extractorHolder.extractor != null) {
                this.extractorHolder.extractor.release();
                this.extractorHolder.extractor = null;
            }
        }
    }

    public void onLoadCompleted(Loadable loadable) {
        this.loadingFinished = true;
    }

    public void onLoadCanceled(Loadable loadable) {
        if (this.enabledTrackCount > 0) {
            restartFrom(this.pendingResetPositionUs);
            return;
        }
        clearState();
        this.allocator.trim(0);
    }

    public void onLoadError(Loadable ignored, IOException e) {
        this.currentLoadableException = e;
        this.currentLoadableExceptionCount = this.extractedSampleCount > this.extractedSampleCountAtStartOfLoad ? 1 : this.currentLoadableExceptionCount + 1;
        this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        notifyLoadError(e);
        maybeStartLoading();
    }

    public TrackOutput track(int id) {
        InternalTrackOutput sampleQueue = (InternalTrackOutput) this.sampleQueues.get(id);
        if (sampleQueue != null) {
            return sampleQueue;
        }
        sampleQueue = new InternalTrackOutput(this.allocator);
        this.sampleQueues.put(id, sampleQueue);
        return sampleQueue;
    }

    public void endTracks() {
        this.tracksBuilt = true;
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
    }

    public void drmInitData(DrmInitData drmInitData) {
        this.drmInitData = drmInitData;
    }

    private void restartFrom(long positionUs) {
        this.pendingResetPositionUs = positionUs;
        this.loadingFinished = false;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        clearState();
        maybeStartLoading();
    }

    private void maybeStartLoading() {
        boolean z = false;
        if (!this.loadingFinished && !this.loader.isLoading()) {
            if (this.currentLoadableException == null) {
                this.sampleTimeOffsetUs = 0;
                this.havePendingNextSampleUs = false;
                if (this.prepared) {
                    Assertions.checkState(isPendingReset());
                    if (this.maxTrackDurationUs == -1 || this.pendingResetPositionUs < this.maxTrackDurationUs) {
                        this.loadable = createLoadableFromPositionUs(this.pendingResetPositionUs);
                        this.pendingResetPositionUs = NO_RESET_PENDING;
                    } else {
                        this.loadingFinished = true;
                        this.pendingResetPositionUs = NO_RESET_PENDING;
                        return;
                    }
                }
                this.loadable = createLoadableFromStart();
                this.extractedSampleCountAtStartOfLoad = this.extractedSampleCount;
                this.loader.startLoading(this.loadable, this);
            } else if (!isCurrentLoadableExceptionFatal()) {
                if (this.loadable != null) {
                    z = true;
                }
                Assertions.checkState(z);
                if (SystemClock.elapsedRealtime() - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                    this.currentLoadableException = null;
                    int i;
                    if (!this.prepared) {
                        for (i = 0; i < this.sampleQueues.size(); i++) {
                            ((InternalTrackOutput) this.sampleQueues.valueAt(i)).clear();
                        }
                        this.loadable = createLoadableFromStart();
                    } else if (!this.seekMap.isSeekable() && this.maxTrackDurationUs == -1) {
                        for (i = 0; i < this.sampleQueues.size(); i++) {
                            ((InternalTrackOutput) this.sampleQueues.valueAt(i)).clear();
                        }
                        this.loadable = createLoadableFromStart();
                        this.pendingNextSampleUs = this.downstreamPositionUs;
                        this.havePendingNextSampleUs = true;
                    }
                    this.extractedSampleCountAtStartOfLoad = this.extractedSampleCount;
                    this.loader.startLoading(this.loadable, this);
                }
            }
        }
    }

    private ExtractingLoadable createLoadableFromStart() {
        return new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.allocator, this.requestedBufferSize, 0);
    }

    private ExtractingLoadable createLoadableFromPositionUs(long positionUs) {
        return new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.allocator, this.requestedBufferSize, this.seekMap.getPosition(positionUs));
    }

    private boolean haveFormatsForAllTracks() {
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            if (!((InternalTrackOutput) this.sampleQueues.valueAt(i)).hasFormat()) {
                return false;
            }
        }
        return true;
    }

    private void discardSamplesForDisabledTracks(long timeUs) {
        for (int i = 0; i < this.trackEnabledStates.length; i++) {
            if (!this.trackEnabledStates[i]) {
                ((InternalTrackOutput) this.sampleQueues.valueAt(i)).discardUntil(timeUs);
            }
        }
    }

    private void clearState() {
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            ((InternalTrackOutput) this.sampleQueues.valueAt(i)).clear();
        }
        this.loadable = null;
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = 0;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != NO_RESET_PENDING;
    }

    private boolean isCurrentLoadableExceptionFatal() {
        return this.currentLoadableException instanceof UnrecognizedInputFormatException;
    }

    private long getRetryDelayMillis(long errorCount) {
        return Math.min((errorCount - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    private void notifyLoadError(IOException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07731(e));
        }
    }
}
