package com.google.android.gms.measurement.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzmq;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class zzv extends zzz {
    private zzc zzaXI;
    private zzc zzaXJ;
    private final BlockingQueue<FutureTask<?>> zzaXK;
    private final BlockingQueue<FutureTask<?>> zzaXL;
    private final UncaughtExceptionHandler zzaXM;
    private final UncaughtExceptionHandler zzaXN;
    private final Object zzaXO;
    private final Semaphore zzaXP;
    private volatile boolean zzaXQ;

    private final class zza<V> extends FutureTask<V> {
        private final String zzaXR;
        final /* synthetic */ zzv zzaXS;

        zza(zzv com_google_android_gms_measurement_internal_zzv, Runnable runnable, String str) {
            this.zzaXS = com_google_android_gms_measurement_internal_zzv;
            super(runnable, null);
            zzx.zzz(str);
            this.zzaXR = str;
        }

        zza(zzv com_google_android_gms_measurement_internal_zzv, Callable<V> callable, String str) {
            this.zzaXS = com_google_android_gms_measurement_internal_zzv;
            super(callable);
            zzx.zzz(str);
            this.zzaXR = str;
        }

        protected void setException(Throwable error) {
            this.zzaXS.zzAo().zzCE().zzj(this.zzaXR, error);
            super.setException(error);
        }
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String zzaXR;
        final /* synthetic */ zzv zzaXS;

        public zzb(zzv com_google_android_gms_measurement_internal_zzv, String str) {
            this.zzaXS = com_google_android_gms_measurement_internal_zzv;
            zzx.zzz(str);
            this.zzaXR = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable error) {
            this.zzaXS.zzAo().zzCE().zzj(this.zzaXR, error);
        }
    }

    private final class zzc extends Thread {
        final /* synthetic */ zzv zzaXS;
        private final Object zzaXT;
        private final BlockingQueue<FutureTask<?>> zzaXU;

        public zzc(zzv com_google_android_gms_measurement_internal_zzv, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.zzaXS = com_google_android_gms_measurement_internal_zzv;
            zzx.zzz(str);
            this.zzaXT = new Object();
            this.zzaXU = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.zzaXS.zzAo().zzCF().zzj(getName() + " was interrupted", interruptedException);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r4 = this;
            r0 = 0;
            r1 = r0;
        L_0x0002:
            if (r1 != 0) goto L_0x0015;
        L_0x0004:
            r0 = r4.zzaXS;	 Catch:{ InterruptedException -> 0x0010 }
            r0 = r0.zzaXP;	 Catch:{ InterruptedException -> 0x0010 }
            r0.acquire();	 Catch:{ InterruptedException -> 0x0010 }
            r0 = 1;
            r1 = r0;
            goto L_0x0002;
        L_0x0010:
            r0 = move-exception;
            r4.zza(r0);
            goto L_0x0002;
        L_0x0015:
            r0 = r4.zzaXU;	 Catch:{ all -> 0x0023 }
            r0 = r0.poll();	 Catch:{ all -> 0x0023 }
            r0 = (java.util.concurrent.FutureTask) r0;	 Catch:{ all -> 0x0023 }
            if (r0 == 0) goto L_0x004d;
        L_0x001f:
            r0.run();	 Catch:{ all -> 0x0023 }
            goto L_0x0015;
        L_0x0023:
            r0 = move-exception;
            r1 = r4.zzaXS;
            r1 = r1.zzaXO;
            monitor-enter(r1);
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzaXP;	 Catch:{ all -> 0x00e1 }
            r2.release();	 Catch:{ all -> 0x00e1 }
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzaXO;	 Catch:{ all -> 0x00e1 }
            r2.notifyAll();	 Catch:{ all -> 0x00e1 }
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzaXI;	 Catch:{ all -> 0x00e1 }
            if (r4 != r2) goto L_0x00d1;
        L_0x0045:
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r3 = 0;
            r2.zzaXI = r3;	 Catch:{ all -> 0x00e1 }
        L_0x004b:
            monitor-exit(r1);	 Catch:{ all -> 0x00e1 }
            throw r0;
        L_0x004d:
            r1 = r4.zzaXT;	 Catch:{ all -> 0x0023 }
            monitor-enter(r1);	 Catch:{ all -> 0x0023 }
            r0 = r4.zzaXU;	 Catch:{ all -> 0x00a6 }
            r0 = r0.peek();	 Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x0067;
        L_0x0058:
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00a6 }
            r0 = r0.zzaXQ;	 Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x0067;
        L_0x0060:
            r0 = r4.zzaXT;	 Catch:{ InterruptedException -> 0x00a1 }
            r2 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
            r0.wait(r2);	 Catch:{ InterruptedException -> 0x00a1 }
        L_0x0067:
            monitor-exit(r1);	 Catch:{ all -> 0x00a6 }
            r0 = r4.zzaXS;	 Catch:{ all -> 0x0023 }
            r1 = r0.zzaXO;	 Catch:{ all -> 0x0023 }
            monitor-enter(r1);	 Catch:{ all -> 0x0023 }
            r0 = r4.zzaXU;	 Catch:{ all -> 0x00ce }
            r0 = r0.peek();	 Catch:{ all -> 0x00ce }
            if (r0 != 0) goto L_0x00cb;
        L_0x0077:
            monitor-exit(r1);	 Catch:{ all -> 0x00ce }
            r0 = r4.zzaXS;
            r1 = r0.zzaXO;
            monitor-enter(r1);
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzaXP;	 Catch:{ all -> 0x00b8 }
            r0.release();	 Catch:{ all -> 0x00b8 }
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzaXO;	 Catch:{ all -> 0x00b8 }
            r0.notifyAll();	 Catch:{ all -> 0x00b8 }
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzaXI;	 Catch:{ all -> 0x00b8 }
            if (r4 != r0) goto L_0x00a9;
        L_0x0099:
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r2 = 0;
            r0.zzaXI = r2;	 Catch:{ all -> 0x00b8 }
        L_0x009f:
            monitor-exit(r1);	 Catch:{ all -> 0x00b8 }
            return;
        L_0x00a1:
            r0 = move-exception;
            r4.zza(r0);	 Catch:{ all -> 0x00a6 }
            goto L_0x0067;
        L_0x00a6:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00a6 }
            throw r0;	 Catch:{ all -> 0x0023 }
        L_0x00a9:
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzaXJ;	 Catch:{ all -> 0x00b8 }
            if (r4 != r0) goto L_0x00bb;
        L_0x00b1:
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r2 = 0;
            r0.zzaXJ = r2;	 Catch:{ all -> 0x00b8 }
            goto L_0x009f;
        L_0x00b8:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00b8 }
            throw r0;
        L_0x00bb:
            r0 = r4.zzaXS;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzAo();	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzCE();	 Catch:{ all -> 0x00b8 }
            r2 = "Current scheduler thread is neither worker nor network";
            r0.zzfg(r2);	 Catch:{ all -> 0x00b8 }
            goto L_0x009f;
        L_0x00cb:
            monitor-exit(r1);	 Catch:{ all -> 0x00ce }
            goto L_0x0015;
        L_0x00ce:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00ce }
            throw r0;	 Catch:{ all -> 0x0023 }
        L_0x00d1:
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzaXJ;	 Catch:{ all -> 0x00e1 }
            if (r4 != r2) goto L_0x00e4;
        L_0x00d9:
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r3 = 0;
            r2.zzaXJ = r3;	 Catch:{ all -> 0x00e1 }
            goto L_0x004b;
        L_0x00e1:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00e1 }
            throw r0;
        L_0x00e4:
            r2 = r4.zzaXS;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzAo();	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzCE();	 Catch:{ all -> 0x00e1 }
            r3 = "Current scheduler thread is neither worker nor network";
            r2.zzfg(r3);	 Catch:{ all -> 0x00e1 }
            goto L_0x004b;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzv.zzc.run():void");
        }

        public void zzfb() {
            synchronized (this.zzaXT) {
                this.zzaXT.notifyAll();
            }
        }
    }

    zzv(zzw com_google_android_gms_measurement_internal_zzw) {
        super(com_google_android_gms_measurement_internal_zzw);
        this.zzaXO = new Object();
        this.zzaXP = new Semaphore(2);
        this.zzaXK = new LinkedBlockingQueue();
        this.zzaXL = new LinkedBlockingQueue();
        this.zzaXM = new zzb(this, "Thread death: Uncaught exception on worker thread");
        this.zzaXN = new zzb(this, "Thread death: Uncaught exception on network thread");
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.zzaXO) {
            this.zzaXK.add(futureTask);
            if (this.zzaXI == null) {
                this.zzaXI = new zzc(this, "Measurement Worker", this.zzaXK);
                this.zzaXI.setUncaughtExceptionHandler(this.zzaXM);
                this.zzaXI.start();
            } else {
                this.zzaXI.zzfb();
            }
        }
    }

    private void zzb(FutureTask<?> futureTask) {
        synchronized (this.zzaXO) {
            this.zzaXL.add(futureTask);
            if (this.zzaXJ == null) {
                this.zzaXJ = new zzc(this, "Measurement Network", this.zzaXL);
                this.zzaXJ.setUncaughtExceptionHandler(this.zzaXN);
                this.zzaXJ.start();
            } else {
                this.zzaXJ.zzfb();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ zzp zzAo() {
        return super.zzAo();
    }

    public void zzCd() {
        if (Thread.currentThread() != this.zzaXJ) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzc zzCe() {
        return super.zzCe();
    }

    public /* bridge */ /* synthetic */ zzab zzCf() {
        return super.zzCf();
    }

    public /* bridge */ /* synthetic */ zzn zzCg() {
        return super.zzCg();
    }

    public /* bridge */ /* synthetic */ zzg zzCh() {
        return super.zzCh();
    }

    public /* bridge */ /* synthetic */ zzac zzCi() {
        return super.zzCi();
    }

    public /* bridge */ /* synthetic */ zze zzCj() {
        return super.zzCj();
    }

    public /* bridge */ /* synthetic */ zzaj zzCk() {
        return super.zzCk();
    }

    public /* bridge */ /* synthetic */ zzu zzCl() {
        return super.zzCl();
    }

    public /* bridge */ /* synthetic */ zzad zzCm() {
        return super.zzCm();
    }

    public /* bridge */ /* synthetic */ zzv zzCn() {
        return super.zzCn();
    }

    public /* bridge */ /* synthetic */ zzt zzCo() {
        return super.zzCo();
    }

    public /* bridge */ /* synthetic */ zzd zzCp() {
        return super.zzCp();
    }

    public <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zzjv();
        zzx.zzz(callable);
        FutureTask com_google_android_gms_measurement_internal_zzv_zza = new zza(this, (Callable) callable, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzaXI) {
            com_google_android_gms_measurement_internal_zzv_zza.run();
        } else {
            zza(com_google_android_gms_measurement_internal_zzv_zza);
        }
        return com_google_android_gms_measurement_internal_zzv_zza;
    }

    public void zzg(Runnable runnable) throws IllegalStateException {
        zzjv();
        zzx.zzz(runnable);
        zza(new zza(this, runnable, "Task exception on worker thread"));
    }

    public void zzh(Runnable runnable) throws IllegalStateException {
        zzjv();
        zzx.zzz(runnable);
        zzb(new zza(this, runnable, "Task exception on network thread"));
    }

    protected void zziJ() {
    }

    public /* bridge */ /* synthetic */ void zzjj() {
        super.zzjj();
    }

    public void zzjk() {
        if (Thread.currentThread() != this.zzaXI) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    public /* bridge */ /* synthetic */ zzmq zzjl() {
        return super.zzjl();
    }
}
