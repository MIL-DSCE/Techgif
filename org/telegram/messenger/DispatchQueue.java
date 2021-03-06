package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {
    private volatile Handler handler;
    private CountDownLatch syncLatch;

    public DispatchQueue(String threadName) {
        this.handler = null;
        this.syncLatch = new CountDownLatch(1);
        setName(threadName);
        start();
    }

    private void sendMessage(Message msg, int delay) {
        try {
            this.syncLatch.await();
            if (delay <= 0) {
                this.handler.sendMessage(msg);
            } else {
                this.handler.sendMessageDelayed(msg, (long) delay);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void cancelRunnable(Runnable runnable) {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacks(runnable);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void postRunnable(Runnable runnable) {
        postRunnable(runnable, 0);
    }

    public void postRunnable(Runnable runnable, long delay) {
        try {
            this.syncLatch.await();
            if (delay <= 0) {
                this.handler.post(runnable);
            } else {
                this.handler.postDelayed(runnable, delay);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void cleanupQueue() {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacksAndMessages(null);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void run() {
        Looper.prepare();
        this.handler = new Handler();
        this.syncLatch.countDown();
        Looper.loop();
    }
}
