package org.telegram.messenger.exoplayer.upstream.cache;

import android.os.ConditionVariable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer.upstream.cache.Cache.Listener;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class SimpleCache implements Cache {
    private final File cacheDir;
    private final HashMap<String, TreeSet<CacheSpan>> cachedSpans;
    private final CacheEvictor evictor;
    private final HashMap<String, ArrayList<Listener>> listeners;
    private final HashMap<String, CacheSpan> lockedSpans;
    private long totalSpace;

    /* renamed from: org.telegram.messenger.exoplayer.upstream.cache.SimpleCache.1 */
    class C07881 extends Thread {
        final /* synthetic */ ConditionVariable val$conditionVariable;

        C07881(String x0, ConditionVariable conditionVariable) {
            this.val$conditionVariable = conditionVariable;
            super(x0);
        }

        public void run() {
            synchronized (SimpleCache.this) {
                this.val$conditionVariable.open();
                SimpleCache.this.initialize();
            }
        }
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor) {
        this.totalSpace = 0;
        this.cacheDir = cacheDir;
        this.evictor = evictor;
        this.lockedSpans = new HashMap();
        this.cachedSpans = new HashMap();
        this.listeners = new HashMap();
        ConditionVariable conditionVariable = new ConditionVariable();
        new C07881("SimpleCache.initialize()", conditionVariable).start();
        conditionVariable.block();
    }

    public synchronized NavigableSet<CacheSpan> addListener(String key, Listener listener) {
        ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
        if (listenersForKey == null) {
            listenersForKey = new ArrayList();
            this.listeners.put(key, listenersForKey);
        }
        listenersForKey.add(listener);
        return getCachedSpans(key);
    }

    public synchronized void removeListener(String key, Listener listener) {
        ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
        if (listenersForKey != null) {
            listenersForKey.remove(listener);
            if (listenersForKey.isEmpty()) {
                this.listeners.remove(key);
            }
        }
    }

    public synchronized NavigableSet<CacheSpan> getCachedSpans(String key) {
        TreeSet<CacheSpan> spansForKey;
        spansForKey = (TreeSet) this.cachedSpans.get(key);
        return spansForKey == null ? null : new TreeSet(spansForKey);
    }

    public synchronized Set<String> getKeys() {
        return new HashSet(this.cachedSpans.keySet());
    }

    public synchronized long getCacheSpace() {
        return this.totalSpace;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized org.telegram.messenger.exoplayer.upstream.cache.CacheSpan startReadWrite(java.lang.String r5, long r6) throws java.lang.InterruptedException {
        /*
        r4 = this;
        monitor-enter(r4);
        r0 = org.telegram.messenger.exoplayer.upstream.cache.CacheSpan.createLookup(r5, r6);	 Catch:{ all -> 0x0011 }
    L_0x0005:
        r1 = r4.startReadWriteNonBlocking(r0);	 Catch:{ all -> 0x0011 }
        if (r1 == 0) goto L_0x000d;
    L_0x000b:
        monitor-exit(r4);
        return r1;
    L_0x000d:
        r4.wait();	 Catch:{ all -> 0x0011 }
        goto L_0x0005;
    L_0x0011:
        r2 = move-exception;
        monitor-exit(r4);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.upstream.cache.SimpleCache.startReadWrite(java.lang.String, long):org.telegram.messenger.exoplayer.upstream.cache.CacheSpan");
    }

    public synchronized CacheSpan startReadWriteNonBlocking(String key, long position) {
        return startReadWriteNonBlocking(CacheSpan.createLookup(key, position));
    }

    private synchronized CacheSpan startReadWriteNonBlocking(CacheSpan lookupSpan) {
        CacheSpan cacheSpan;
        CacheSpan spanningRegion = getSpan(lookupSpan);
        if (spanningRegion.isCached) {
            CacheSpan oldCacheSpan = spanningRegion;
            TreeSet<CacheSpan> spansForKey = (TreeSet) this.cachedSpans.get(oldCacheSpan.key);
            Assertions.checkState(spansForKey.remove(oldCacheSpan));
            spanningRegion = oldCacheSpan.touch();
            spansForKey.add(spanningRegion);
            notifySpanTouched(oldCacheSpan, spanningRegion);
            cacheSpan = spanningRegion;
        } else if (this.lockedSpans.containsKey(lookupSpan.key)) {
            cacheSpan = null;
        } else {
            this.lockedSpans.put(lookupSpan.key, spanningRegion);
            cacheSpan = spanningRegion;
        }
        return cacheSpan;
    }

    public synchronized File startFile(String key, long position, long length) {
        Assertions.checkState(this.lockedSpans.containsKey(key));
        if (!this.cacheDir.exists()) {
            removeStaleSpans();
            this.cacheDir.mkdirs();
        }
        this.evictor.onStartFile(this, key, position, length);
        return CacheSpan.getCacheFileName(this.cacheDir, key, position, System.currentTimeMillis());
    }

    public synchronized void commitFile(File file) {
        CacheSpan span = CacheSpan.createCacheEntry(file);
        Assertions.checkState(span != null);
        Assertions.checkState(this.lockedSpans.containsKey(span.key));
        if (file.exists()) {
            if (file.length() == 0) {
                file.delete();
            } else {
                addSpan(span);
                notifyAll();
            }
        }
    }

    public synchronized void releaseHoleSpan(CacheSpan holeSpan) {
        Assertions.checkState(holeSpan == this.lockedSpans.remove(holeSpan.key));
        notifyAll();
    }

    private CacheSpan getSpan(CacheSpan lookupSpan) {
        String key = lookupSpan.key;
        long offset = lookupSpan.position;
        TreeSet<CacheSpan> entries = (TreeSet) this.cachedSpans.get(key);
        if (entries == null) {
            return CacheSpan.createOpenHole(key, lookupSpan.position);
        }
        CacheSpan floorSpan = (CacheSpan) entries.floor(lookupSpan);
        if (floorSpan == null || floorSpan.position > offset || offset >= floorSpan.position + floorSpan.length) {
            CacheSpan ceilEntry = (CacheSpan) entries.ceiling(lookupSpan);
            return ceilEntry == null ? CacheSpan.createOpenHole(key, lookupSpan.position) : CacheSpan.createClosedHole(key, lookupSpan.position, ceilEntry.position - lookupSpan.position);
        } else if (floorSpan.file.exists()) {
            return floorSpan;
        } else {
            removeStaleSpans();
            return getSpan(lookupSpan);
        }
    }

    private void initialize() {
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
        }
        File[] files = this.cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File file2;
                if (file2.length() == 0) {
                    file2.delete();
                } else {
                    file2 = CacheSpan.upgradeIfNeeded(file2);
                    CacheSpan span = CacheSpan.createCacheEntry(file2);
                    if (span == null) {
                        file2.delete();
                    } else {
                        addSpan(span);
                    }
                }
            }
        }
    }

    private void addSpan(CacheSpan span) {
        TreeSet<CacheSpan> spansForKey = (TreeSet) this.cachedSpans.get(span.key);
        if (spansForKey == null) {
            spansForKey = new TreeSet();
            this.cachedSpans.put(span.key, spansForKey);
        }
        spansForKey.add(span);
        this.totalSpace += span.length;
        notifySpanAdded(span);
    }

    public synchronized void removeSpan(CacheSpan span) {
        TreeSet<CacheSpan> spansForKey = (TreeSet) this.cachedSpans.get(span.key);
        this.totalSpace -= span.length;
        Assertions.checkState(spansForKey.remove(span));
        span.file.delete();
        if (spansForKey.isEmpty()) {
            this.cachedSpans.remove(span.key);
        }
        notifySpanRemoved(span);
    }

    private void removeStaleSpans() {
        Iterator<Entry<String, TreeSet<CacheSpan>>> iterator = this.cachedSpans.entrySet().iterator();
        while (iterator.hasNext()) {
            Iterator<CacheSpan> spanIterator = ((TreeSet) ((Entry) iterator.next()).getValue()).iterator();
            boolean isEmpty = true;
            while (spanIterator.hasNext()) {
                CacheSpan span = (CacheSpan) spanIterator.next();
                if (span.file.exists()) {
                    isEmpty = false;
                } else {
                    spanIterator.remove();
                    if (span.isCached) {
                        this.totalSpace -= span.length;
                    }
                    notifySpanRemoved(span);
                }
            }
            if (isEmpty) {
                iterator.remove();
            }
        }
    }

    private void notifySpanRemoved(CacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanRemoved(this, span);
            }
        }
        this.evictor.onSpanRemoved(this, span);
    }

    private void notifySpanAdded(CacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanAdded(this, span);
            }
        }
        this.evictor.onSpanAdded(this, span);
    }

    private void notifySpanTouched(CacheSpan oldSpan, CacheSpan newSpan) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(oldSpan.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanTouched(this, oldSpan, newSpan);
            }
        }
        this.evictor.onSpanTouched(this, oldSpan, newSpan);
    }

    public synchronized boolean isCached(String key, long position, long length) {
        boolean z;
        TreeSet<CacheSpan> entries = (TreeSet) this.cachedSpans.get(key);
        if (entries == null) {
            z = false;
        } else {
            CacheSpan floorSpan = (CacheSpan) entries.floor(CacheSpan.createLookup(key, position));
            if (floorSpan == null || floorSpan.position + floorSpan.length <= position) {
                z = false;
            } else {
                long queryEndPosition = position + length;
                long currentEndPosition = floorSpan.position + floorSpan.length;
                if (currentEndPosition >= queryEndPosition) {
                    z = true;
                } else {
                    for (CacheSpan next : entries.tailSet(floorSpan, false)) {
                        if (next.position <= currentEndPosition) {
                            currentEndPosition = Math.max(currentEndPosition, next.position + next.length);
                            if (currentEndPosition >= queryEndPosition) {
                                z = true;
                                break;
                            }
                        }
                        z = false;
                        break;
                    }
                    z = false;
                }
            }
        }
        return z;
    }
}
