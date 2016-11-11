package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;
import java.util.PriorityQueue;
import org.telegram.tgnet.ConnectionsManager;

public final class NetworkLock {
    public static final int DOWNLOAD_PRIORITY = 10;
    public static final int STREAMING_PRIORITY = 0;
    public static final NetworkLock instance;
    private int highestPriority;
    private final Object lock;
    private final PriorityQueue<Integer> queue;

    public static class PriorityTooLowException extends IOException {
        public PriorityTooLowException(int priority, int highestPriority) {
            super("Priority too low [priority=" + priority + ", highest=" + highestPriority + "]");
        }
    }

    static {
        instance = new NetworkLock();
    }

    private NetworkLock() {
        this.lock = new Object();
        this.queue = new PriorityQueue();
        this.highestPriority = ConnectionsManager.DEFAULT_DATACENTER_ID;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void proceed(int r3) throws java.lang.InterruptedException {
        /*
        r2 = this;
        r1 = r2.lock;
        monitor-enter(r1);
    L_0x0003:
        r0 = r2.highestPriority;	 Catch:{ all -> 0x000d }
        if (r0 >= r3) goto L_0x0010;
    L_0x0007:
        r0 = r2.lock;	 Catch:{ all -> 0x000d }
        r0.wait();	 Catch:{ all -> 0x000d }
        goto L_0x0003;
    L_0x000d:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x000d }
        throw r0;
    L_0x0010:
        monitor-exit(r1);	 Catch:{ all -> 0x000d }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.upstream.NetworkLock.proceed(int):void");
    }

    public boolean proceedNonBlocking(int priority) {
        boolean z;
        synchronized (this.lock) {
            z = this.highestPriority >= priority;
        }
        return z;
    }

    public void proceedOrThrow(int priority) throws PriorityTooLowException {
        synchronized (this.lock) {
            if (this.highestPriority < priority) {
                throw new PriorityTooLowException(priority, this.highestPriority);
            }
        }
    }

    public void add(int priority) {
        synchronized (this.lock) {
            this.queue.add(Integer.valueOf(priority));
            this.highestPriority = Math.min(this.highestPriority, priority);
        }
    }

    public void remove(int priority) {
        synchronized (this.lock) {
            this.queue.remove(Integer.valueOf(priority));
            this.highestPriority = this.queue.isEmpty() ? ConnectionsManager.DEFAULT_DATACENTER_ID : ((Integer) this.queue.peek()).intValue();
            this.lock.notifyAll();
        }
    }
}
