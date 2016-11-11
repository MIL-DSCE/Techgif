package com.google.android.gms.measurement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzrp;
import com.google.android.gms.measurement.internal.zzaj;
import com.google.android.gms.measurement.internal.zzp;
import com.google.android.gms.measurement.internal.zzw;

public final class AppMeasurementService extends Service {
    private static Boolean zzOO;
    private final Handler mHandler;

    /* renamed from: com.google.android.gms.measurement.AppMeasurementService.1 */
    class C03261 implements Runnable {
        final /* synthetic */ int zzOP;
        final /* synthetic */ zzw zzaTW;
        final /* synthetic */ zzp zzaTX;
        final /* synthetic */ AppMeasurementService zzaTY;

        /* renamed from: com.google.android.gms.measurement.AppMeasurementService.1.1 */
        class C03251 implements Runnable {
            final /* synthetic */ C03261 zzaTZ;

            C03251(C03261 c03261) {
                this.zzaTZ = c03261;
            }

            public void run() {
                if (!this.zzaTZ.zzaTY.stopSelfResult(this.zzaTZ.zzOP)) {
                    return;
                }
                if (this.zzaTZ.zzaTW.zzCp().zzkr()) {
                    this.zzaTZ.zzaTX.zzCK().zzfg("Device AppMeasurementService processed last upload request");
                } else {
                    this.zzaTZ.zzaTX.zzCK().zzfg("Local AppMeasurementService processed last upload request");
                }
            }
        }

        C03261(AppMeasurementService appMeasurementService, zzw com_google_android_gms_measurement_internal_zzw, int i, zzp com_google_android_gms_measurement_internal_zzp) {
            this.zzaTY = appMeasurementService;
            this.zzaTW = com_google_android_gms_measurement_internal_zzw;
            this.zzOP = i;
            this.zzaTX = com_google_android_gms_measurement_internal_zzp;
        }

        public void run() {
            this.zzaTW.zzDc();
            this.zzaTY.mHandler.post(new C03251(this));
        }
    }

    public AppMeasurementService() {
        this.mHandler = new Handler();
    }

    private zzp zzAo() {
        return zzw.zzaT(this).zzAo();
    }

    public static boolean zzZ(Context context) {
        zzx.zzz(context);
        if (zzOO != null) {
            return zzOO.booleanValue();
        }
        boolean zza = zzaj.zza(context, AppMeasurementService.class);
        zzOO = Boolean.valueOf(zza);
        return zza;
    }

    private void zziz() {
        try {
            synchronized (AppMeasurementReceiver.zzqy) {
                zzrp com_google_android_gms_internal_zzrp = AppMeasurementReceiver.zzOM;
                if (com_google_android_gms_internal_zzrp != null && com_google_android_gms_internal_zzrp.isHeld()) {
                    com_google_android_gms_internal_zzrp.release();
                }
            }
        } catch (SecurityException e) {
        }
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            zzAo().zzCE().zzfg("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new com.google.android.gms.measurement.internal.zzx(zzw.zzaT(this));
        }
        zzAo().zzCF().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public void onCreate() {
        super.onCreate();
        zzw zzaT = zzw.zzaT(this);
        zzp zzAo = zzaT.zzAo();
        if (zzaT.zzCp().zzkr()) {
            zzAo.zzCK().zzfg("Device AppMeasurementService is starting up");
        } else {
            zzAo.zzCK().zzfg("Local AppMeasurementService is starting up");
        }
    }

    @MainThread
    public void onDestroy() {
        zzw zzaT = zzw.zzaT(this);
        zzp zzAo = zzaT.zzAo();
        if (zzaT.zzCp().zzkr()) {
            zzAo.zzCK().zzfg("Device AppMeasurementService is shutting down");
        } else {
            zzAo.zzCK().zzfg("Local AppMeasurementService is shutting down");
        }
        super.onDestroy();
    }

    @MainThread
    public void onRebind(Intent intent) {
        if (intent == null) {
            zzAo().zzCE().zzfg("onRebind called with null intent");
            return;
        }
        zzAo().zzCK().zzj("onRebind called. action", intent.getAction());
    }

    @MainThread
    public int onStartCommand(Intent intent, int flags, int startId) {
        zziz();
        zzw zzaT = zzw.zzaT(this);
        zzp zzAo = zzaT.zzAo();
        String action = intent.getAction();
        if (zzaT.zzCp().zzkr()) {
            zzAo.zzCK().zze("Device AppMeasurementService called. startId, action", Integer.valueOf(startId), action);
        } else {
            zzAo.zzCK().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(startId), action);
        }
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            zzaT.zzCn().zzg(new C03261(this, zzaT, startId, zzAo));
        }
        return 2;
    }

    @MainThread
    public boolean onUnbind(Intent intent) {
        if (intent == null) {
            zzAo().zzCE().zzfg("onUnbind called with null intent");
        } else {
            zzAo().zzCK().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }
}
