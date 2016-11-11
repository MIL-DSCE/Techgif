package org.telegram.messenger.exoplayer;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class SingleSampleSource implements SampleSource, SampleSourceReader, Callback, Loadable {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final int INITIAL_SAMPLE_SIZE = 1;
    private static final int STATE_END_OF_STREAM = 2;
    private static final int STATE_SEND_FORMAT = 0;
    private static final int STATE_SEND_SAMPLE = 1;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private final DataSource dataSource;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private final MediaFormat format;
    private Loader loader;
    private boolean loadingFinished;
    private final int minLoadableRetryCount;
    private long pendingDiscontinuityPositionUs;
    private byte[] sampleData;
    private int sampleSize;
    private int state;
    private final Uri uri;

    /* renamed from: org.telegram.messenger.exoplayer.SingleSampleSource.1 */
    class C07611 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07611(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            SingleSampleSource.this.eventListener.onLoadError(SingleSampleSource.this.eventSourceId, this.val$e);
        }
    }

    public interface EventListener {
        void onLoadError(int i, IOException iOException);
    }

    public SingleSampleSource(Uri uri, DataSource dataSource, MediaFormat format) {
        this(uri, dataSource, format, DEFAULT_MIN_LOADABLE_RETRY_COUNT);
    }

    public SingleSampleSource(Uri uri, DataSource dataSource, MediaFormat format, int minLoadableRetryCount) {
        this(uri, dataSource, format, minLoadableRetryCount, null, null, STATE_SEND_FORMAT);
    }

    public SingleSampleSource(Uri uri, DataSource dataSource, MediaFormat format, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, int eventSourceId) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.format = format;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.eventSourceId = eventSourceId;
        this.sampleData = new byte[STATE_SEND_SAMPLE];
    }

    public SampleSourceReader register() {
        return this;
    }

    public boolean prepare(long positionUs) {
        if (this.loader == null) {
            this.loader = new Loader("Loader:" + this.format.mimeType);
        }
        return true;
    }

    public int getTrackCount() {
        return STATE_SEND_SAMPLE;
    }

    public MediaFormat getFormat(int track) {
        return this.format;
    }

    public void enable(int track, long positionUs) {
        this.state = STATE_SEND_FORMAT;
        this.pendingDiscontinuityPositionUs = Long.MIN_VALUE;
        clearCurrentLoadableException();
        maybeStartLoading();
    }

    public boolean continueBuffering(int track, long positionUs) {
        maybeStartLoading();
        return this.loadingFinished;
    }

    public void maybeThrowError() throws IOException {
        if (this.currentLoadableException != null && this.currentLoadableExceptionCount > this.minLoadableRetryCount) {
            throw this.currentLoadableException;
        }
    }

    public long readDiscontinuity(int track) {
        long discontinuityPositionUs = this.pendingDiscontinuityPositionUs;
        this.pendingDiscontinuityPositionUs = Long.MIN_VALUE;
        return discontinuityPositionUs;
    }

    public int readData(int track, long positionUs, MediaFormatHolder formatHolder, SampleHolder sampleHolder) {
        if (this.state == STATE_END_OF_STREAM) {
            return -1;
        }
        if (this.state == 0) {
            formatHolder.format = this.format;
            this.state = STATE_SEND_SAMPLE;
            return -4;
        }
        boolean z;
        if (this.state == STATE_SEND_SAMPLE) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (!this.loadingFinished) {
            return -2;
        }
        sampleHolder.timeUs = 0;
        sampleHolder.size = this.sampleSize;
        sampleHolder.flags = STATE_SEND_SAMPLE;
        sampleHolder.ensureSpaceForWrite(sampleHolder.size);
        sampleHolder.data.put(this.sampleData, STATE_SEND_FORMAT, this.sampleSize);
        this.state = STATE_END_OF_STREAM;
        return -3;
    }

    public void seekToUs(long positionUs) {
        if (this.state == STATE_END_OF_STREAM) {
            this.pendingDiscontinuityPositionUs = positionUs;
            this.state = STATE_SEND_SAMPLE;
        }
    }

    public long getBufferedPositionUs() {
        return this.loadingFinished ? -3 : 0;
    }

    public void disable(int track) {
        this.state = STATE_END_OF_STREAM;
    }

    public void release() {
        if (this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
    }

    private void maybeStartLoading() {
        if (!this.loadingFinished && this.state != STATE_END_OF_STREAM && !this.loader.isLoading()) {
            if (this.currentLoadableException != null) {
                if (SystemClock.elapsedRealtime() - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                    this.currentLoadableException = null;
                } else {
                    return;
                }
            }
            this.loader.startLoading(this, this);
        }
    }

    private void clearCurrentLoadableException() {
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = STATE_SEND_FORMAT;
    }

    private long getRetryDelayMillis(long errorCount) {
        return Math.min((errorCount - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    public void onLoadCompleted(Loadable loadable) {
        this.loadingFinished = true;
        clearCurrentLoadableException();
    }

    public void onLoadCanceled(Loadable loadable) {
    }

    public void onLoadError(Loadable loadable, IOException e) {
        this.currentLoadableException = e;
        this.currentLoadableExceptionCount += STATE_SEND_SAMPLE;
        this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        notifyLoadError(e);
        maybeStartLoading();
    }

    public void cancelLoad() {
    }

    public boolean isLoadCanceled() {
        return false;
    }

    public void load() throws IOException, InterruptedException {
        this.sampleSize = STATE_SEND_FORMAT;
        try {
            this.dataSource.open(new DataSpec(this.uri));
            int result = STATE_SEND_FORMAT;
            while (result != -1) {
                this.sampleSize += result;
                if (this.sampleSize == this.sampleData.length) {
                    this.sampleData = Arrays.copyOf(this.sampleData, this.sampleData.length * STATE_END_OF_STREAM);
                }
                result = this.dataSource.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
            }
        } finally {
            this.dataSource.close();
        }
    }

    private void notifyLoadError(IOException e) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07611(e));
        }
    }
}
