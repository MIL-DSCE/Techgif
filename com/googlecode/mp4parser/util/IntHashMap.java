package com.googlecode.mp4parser.util;

import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.tgnet.ConnectionsManager;

public class IntHashMap {
    private transient int count;
    private float loadFactor;
    private transient Entry[] table;
    private int threshold;

    private static class Entry {
        int hash;
        int key;
        Entry next;
        Object value;

        protected Entry(int hash, int key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    public IntHashMap() {
        this(20, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION);
    }

    public IntHashMap(int initialCapacity) {
        this(initialCapacity, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION);
    }

    public IntHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        } else if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        } else {
            if (initialCapacity == 0) {
                initialCapacity = 1;
            }
            this.loadFactor = loadFactor;
            this.table = new Entry[initialCapacity];
            this.threshold = (int) (((float) initialCapacity) * loadFactor);
        }
    }

    public int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean contains(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry[] tab = this.table;
        int i = tab.length;
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                return false;
            }
            for (Entry e = tab[i2]; e != null; e = e.next) {
                if (e.value.equals(value)) {
                    return true;
                }
            }
            i = i2;
        }
    }

    public boolean containsValue(Object value) {
        return contains(value);
    }

    public boolean containsKey(int key) {
        Entry[] tab = this.table;
        int hash = key;
        for (Entry e = tab[(ConnectionsManager.DEFAULT_DATACENTER_ID & hash) % tab.length]; e != null; e = e.next) {
            if (e.hash == hash) {
                return true;
            }
        }
        return false;
    }

    public Object get(int key) {
        Entry[] tab = this.table;
        int hash = key;
        for (Entry e = tab[(ConnectionsManager.DEFAULT_DATACENTER_ID & hash) % tab.length]; e != null; e = e.next) {
            if (e.hash == hash) {
                return e.value;
            }
        }
        return null;
    }

    protected void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = (oldCapacity * 2) + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int) (((float) newCapacity) * this.loadFactor);
        this.table = newMap;
        int i = oldCapacity;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                Entry old = oldMap[i2];
                while (old != null) {
                    Entry e = old;
                    old = old.next;
                    int index = (e.hash & ConnectionsManager.DEFAULT_DATACENTER_ID) % newCapacity;
                    e.next = newMap[index];
                    newMap[index] = e;
                }
                i = i2;
            } else {
                return;
            }
        }
    }

    public Object put(int key, Object value) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & ConnectionsManager.DEFAULT_DATACENTER_ID) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash) {
                Object old = e.value;
                e.value = value;
                return old;
            }
        }
        if (this.count >= this.threshold) {
            rehash();
            tab = this.table;
            index = (hash & ConnectionsManager.DEFAULT_DATACENTER_ID) % tab.length;
        }
        tab[index] = new Entry(hash, key, value, tab[index]);
        this.count++;
        return null;
    }

    public Object remove(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (ConnectionsManager.DEFAULT_DATACENTER_ID & hash) % tab.length;
        Entry prev = null;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                this.count--;
                Object oldValue = e.value;
                e.value = null;
                return oldValue;
            }
            prev = e;
        }
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void clear() {
        /*
        r3 = this;
        monitor-enter(r3);
        r1 = r3.table;	 Catch:{ all -> 0x0011 }
        r0 = r1.length;	 Catch:{ all -> 0x0011 }
    L_0x0004:
        r0 = r0 + -1;
        if (r0 >= 0) goto L_0x000d;
    L_0x0008:
        r2 = 0;
        r3.count = r2;	 Catch:{ all -> 0x0011 }
        monitor-exit(r3);
        return;
    L_0x000d:
        r2 = 0;
        r1[r0] = r2;	 Catch:{ all -> 0x0011 }
        goto L_0x0004;
    L_0x0011:
        r2 = move-exception;
        monitor-exit(r3);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.util.IntHashMap.clear():void");
    }
}
