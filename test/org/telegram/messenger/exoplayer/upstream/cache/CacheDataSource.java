package org.telegram.messenger.exoplayer.upstream.cache;

import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InterruptedIOException;
import org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import org.telegram.messenger.exoplayer.upstream.DataSink;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.upstream.FileDataSource;
import org.telegram.messenger.exoplayer.upstream.TeeDataSource;
import org.telegram.messenger.exoplayer.upstream.cache.CacheDataSink.CacheDataSinkException;

public final class CacheDataSource implements DataSource {
    private static final String TAG = "CacheDataSource";
    private final boolean blockOnCache;
    private long bytesRemaining;
    private final Cache cache;
    private final DataSource cacheReadDataSource;
    private final DataSource cacheWriteDataSource;
    private DataSource currentDataSource;
    private final EventListener eventListener;
    private int flags;
    private boolean ignoreCache;
    private final boolean ignoreCacheOnError;
    private String key;
    private CacheSpan lockedSpan;
    private long readPosition;
    private long totalCachedBytesRead;
    private final DataSource upstreamDataSource;
    private Uri uri;

    public interface EventListener {
        void onCachedBytesRead(long j, long j2);
    }

    public CacheDataSource(Cache cache, DataSource upstream, boolean blockOnCache, boolean ignoreCacheOnError) {
        this(cache, upstream, blockOnCache, ignoreCacheOnError, PtsTimestampAdjuster.DO_NOT_OFFSET);
    }

    public CacheDataSource(Cache cache, DataSource upstream, boolean blockOnCache, boolean ignoreCacheOnError, long maxCacheFileSize) {
        this(cache, upstream, new FileDataSource(), new CacheDataSink(cache, maxCacheFileSize), blockOnCache, ignoreCacheOnError, null);
    }

    public CacheDataSource(Cache cache, DataSource upstream, DataSource cacheReadDataSource, DataSink cacheWriteDataSink, boolean blockOnCache, boolean ignoreCacheOnError, EventListener eventListener) {
        this.cache = cache;
        this.cacheReadDataSource = cacheReadDataSource;
        this.blockOnCache = blockOnCache;
        this.ignoreCacheOnError = ignoreCacheOnError;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        try {
            this.uri = dataSpec.uri;
            this.flags = dataSpec.flags;
            this.key = dataSpec.key;
            this.readPosition = dataSpec.position;
            this.bytesRemaining = dataSpec.length;
            openNextSource();
            return dataSpec.length;
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    public int read(byte[] buffer, int offset, int max) throws IOException {
        try {
            int bytesRead = this.currentDataSource.read(buffer, offset, max);
            if (bytesRead >= 0) {
                if (this.currentDataSource == this.cacheReadDataSource) {
                    this.totalCachedBytesRead += (long) bytesRead;
                }
                this.readPosition += (long) bytesRead;
                if (this.bytesRemaining != -1) {
                    this.bytesRemaining -= (long) bytesRead;
                }
            } else {
                closeCurrentSource();
                if (this.bytesRemaining > 0 && this.bytesRemaining != -1) {
                    openNextSource();
                    bytesRead = read(buffer, offset, max);
                }
            }
            return bytesRead;
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    public void close() throws IOException {
        notifyBytesRead();
        try {
            closeCurrentSource();
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    private void openNextSource() throws IOException {
        CacheSpan span;
        DataSpec dataSpec;
        if (this.ignoreCache) {
            span = null;
        } else if (this.bytesRemaining == -1) {
            Log.w(TAG, "Cache bypassed due to unbounded length.");
            span = null;
        } else if (this.blockOnCache) {
            try {
                span = this.cache.startReadWrite(this.key, this.readPosition);
            } catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } else {
            span = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
        }
        if (span == null) {
            this.currentDataSource = this.upstreamDataSource;
            dataSpec = new DataSpec(this.uri, this.readPosition, this.bytesRemaining, this.key, this.flags);
        } else if (span.isCached) {
            long filePosition = this.readPosition - span.position;
            dataSpec = new DataSpec(Uri.fromFile(span.file), this.readPosition, filePosition, Math.min(span.length - filePosition, this.bytesRemaining), this.key, this.flags);
            this.currentDataSource = this.cacheReadDataSource;
        } else {
            this.lockedSpan = span;
            DataSpec dataSpec2 = new DataSpec(this.uri, this.readPosition, span.isOpenEnded() ? this.bytesRemaining : Math.min(span.length, this.bytesRemaining), this.key, this.flags);
            this.currentDataSource = this.cacheWriteDataSource != null ? this.cacheWriteDataSource : this.upstreamDataSource;
        }
        this.currentDataSource.open(dataSpec);
    }

    private void closeCurrentSource() throws IOException {
        if (this.currentDataSource != null) {
            try {
                this.currentDataSource.close();
                this.currentDataSource = null;
                if (this.lockedSpan != null) {
                    this.cache.releaseHoleSpan(this.lockedSpan);
                    this.lockedSpan = null;
                }
            } catch (Throwable th) {
                if (this.lockedSpan != null) {
                    this.cache.releaseHoleSpan(this.lockedSpan);
                    this.lockedSpan = null;
                }
            }
        }
    }

    private void handleBeforeThrow(IOException exception) {
        if (!this.ignoreCacheOnError) {
            return;
        }
        if (this.currentDataSource == this.cacheReadDataSource || (exception instanceof CacheDataSinkException)) {
            this.ignoreCache = true;
        }
    }

    private void notifyBytesRead() {
        if (this.eventListener != null && this.totalCachedBytesRead > 0) {
            this.eventListener.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
            this.totalCachedBytesRead = 0;
        }
    }
}
