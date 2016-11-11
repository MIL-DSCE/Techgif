package com.google.android.gms.measurement.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzx;

abstract class zzf {
    private static volatile Handler zzRC;
    private volatile long zzRD;
    private final zzw zzaTV;
    private boolean zzaVI;
    private final Runnable zzx;

    /* renamed from: com.google.android.gms.measurement.internal.zzf.1 */
    class C03441 implements Runnable {
        final /* synthetic */ zzf zzaVJ;

        C03441(zzf com_google_android_gms_measurement_internal_zzf) {
            this.zzaVJ = com_google_android_gms_measurement_internal_zzf;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.zzaVJ.zzaTV.zzCn().zzg(this);
                return;
            }
            boolean zzbw = this.zzaVJ.zzbw();
            this.zzaVJ.zzRD = 0;
            if (zzbw && this.zzaVJ.zzaVI) {
                this.zzaVJ.run();
            }
        }
    }

    zzf(zzw com_google_android_gms_measurement_internal_zzw) {
        zzx.zzz(com_google_android_gms_measurement_internal_zzw);
        this.zzaTV = com_google_android_gms_measurement_internal_zzw;
        this.zzaVI = true;
        this.zzx = new C03441(this);
    }

    private Handler getHandler() {
        if (zzRC != null) {
            return zzRC;
        }
        Handler handler;
        synchronized (zzf.class) {
            if (zzRC == null) {
                zzRC = new Handler(this.zzaTV.getContext().getMainLooper());
            }
            handler = zzRC;
        }
        return handler;
    }

    public void cancel() {
        this.zzRD = 0;
        getHandler().removeCallbacks(this.zzx);
    }

    public abstract void run();

    public boolean zzbw() {
        return this.zzRD != 0;
    }

    public void zzt(long j) {
        cancel();
        if (j >= 0) {
            this.zzRD = this.zzaTV.zzjl().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzx, j)) {
                this.zzaTV.zzAo().zzCE().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
