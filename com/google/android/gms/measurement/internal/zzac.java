package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.common.stats.zzb;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzmq;
import com.google.android.gms.measurement.AppMeasurementService;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.ui.Components.VideoPlayer;

public class zzac extends zzz {
    private final zza zzaYN;
    private zzm zzaYO;
    private Boolean zzaYP;
    private final zzf zzaYQ;
    private final zzaf zzaYR;
    private final List<Runnable> zzaYS;
    private final zzf zzaYT;

    /* renamed from: com.google.android.gms.measurement.internal.zzac.3 */
    class C03323 implements Runnable {
        final /* synthetic */ zzac zzaYU;

        C03323(zzac com_google_android_gms_measurement_internal_zzac) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
        }

        public void run() {
            zzm zzc = this.zzaYU.zzaYO;
            if (zzc == null) {
                this.zzaYU.zzAo().zzCE().zzfg("Failed to send measurementEnabled to service");
                return;
            }
            try {
                zzc.zzb(this.zzaYU.zzCg().zzfe(this.zzaYU.zzAo().zzCL()));
                this.zzaYU.zzjI();
            } catch (RemoteException e) {
                this.zzaYU.zzAo().zzCE().zzj("Failed to send measurementEnabled to AppMeasurementService", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzac.4 */
    class C03334 implements Runnable {
        final /* synthetic */ String zzaHU;
        final /* synthetic */ zzac zzaYU;
        final /* synthetic */ EventParcel zzaYz;

        C03334(zzac com_google_android_gms_measurement_internal_zzac, String str, EventParcel eventParcel) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
            this.zzaHU = str;
            this.zzaYz = eventParcel;
        }

        public void run() {
            zzm zzc = this.zzaYU.zzaYO;
            if (zzc == null) {
                this.zzaYU.zzAo().zzCE().zzfg("Discarding data. Failed to send event to service");
                return;
            }
            try {
                if (TextUtils.isEmpty(this.zzaHU)) {
                    zzc.zza(this.zzaYz, this.zzaYU.zzCg().zzfe(this.zzaYU.zzAo().zzCL()));
                } else {
                    zzc.zza(this.zzaYz, this.zzaHU, this.zzaYU.zzAo().zzCL());
                }
                this.zzaYU.zzjI();
            } catch (RemoteException e) {
                this.zzaYU.zzAo().zzCE().zzj("Failed to send event to AppMeasurementService", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzac.5 */
    class C03345 implements Runnable {
        final /* synthetic */ UserAttributeParcel zzaYB;
        final /* synthetic */ zzac zzaYU;

        C03345(zzac com_google_android_gms_measurement_internal_zzac, UserAttributeParcel userAttributeParcel) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
            this.zzaYB = userAttributeParcel;
        }

        public void run() {
            zzm zzc = this.zzaYU.zzaYO;
            if (zzc == null) {
                this.zzaYU.zzAo().zzCE().zzfg("Discarding data. Failed to set user attribute");
                return;
            }
            try {
                zzc.zza(this.zzaYB, this.zzaYU.zzCg().zzfe(this.zzaYU.zzAo().zzCL()));
                this.zzaYU.zzjI();
            } catch (RemoteException e) {
                this.zzaYU.zzAo().zzCE().zzj("Failed to send attribute to AppMeasurementService", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzac.6 */
    class C03356 implements Runnable {
        final /* synthetic */ zzac zzaYU;

        C03356(zzac com_google_android_gms_measurement_internal_zzac) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
        }

        public void run() {
            zzm zzc = this.zzaYU.zzaYO;
            if (zzc == null) {
                this.zzaYU.zzAo().zzCE().zzfg("Discarding data. Failed to send app launch");
                return;
            }
            try {
                zzc.zza(this.zzaYU.zzCg().zzfe(this.zzaYU.zzAo().zzCL()));
                this.zzaYU.zzjI();
            } catch (RemoteException e) {
                this.zzaYU.zzAo().zzCE().zzj("Failed to send app launch to AppMeasurementService", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzac.1 */
    class C16511 extends zzf {
        final /* synthetic */ zzac zzaYU;

        C16511(zzac com_google_android_gms_measurement_internal_zzac, zzw com_google_android_gms_measurement_internal_zzw) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
            super(com_google_android_gms_measurement_internal_zzw);
        }

        public void run() {
            this.zzaYU.zzjJ();
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzac.2 */
    class C16522 extends zzf {
        final /* synthetic */ zzac zzaYU;

        C16522(zzac com_google_android_gms_measurement_internal_zzac, zzw com_google_android_gms_measurement_internal_zzw) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
            super(com_google_android_gms_measurement_internal_zzw);
        }

        public void run() {
            this.zzaYU.zzAo().zzCF().zzfg("Tasks have been queued for a long time");
        }
    }

    protected class zza implements ServiceConnection, ConnectionCallbacks, OnConnectionFailedListener {
        final /* synthetic */ zzac zzaYU;
        private volatile boolean zzaYV;
        private volatile zzo zzaYW;

        /* renamed from: com.google.android.gms.measurement.internal.zzac.zza.1 */
        class C03361 implements Runnable {
            final /* synthetic */ zzm zzaYX;
            final /* synthetic */ zza zzaYY;

            C03361(zza com_google_android_gms_measurement_internal_zzac_zza, zzm com_google_android_gms_measurement_internal_zzm) {
                this.zzaYY = com_google_android_gms_measurement_internal_zzac_zza;
                this.zzaYX = com_google_android_gms_measurement_internal_zzm;
            }

            public void run() {
                synchronized (this.zzaYY) {
                    this.zzaYY.zzaYV = false;
                    if (!this.zzaYY.zzaYU.isConnected()) {
                        this.zzaYY.zzaYU.zzAo().zzCK().zzfg("Connected to service");
                        this.zzaYY.zzaYU.zza(this.zzaYX);
                    }
                }
            }
        }

        /* renamed from: com.google.android.gms.measurement.internal.zzac.zza.2 */
        class C03372 implements Runnable {
            final /* synthetic */ ComponentName zzQQ;
            final /* synthetic */ zza zzaYY;

            C03372(zza com_google_android_gms_measurement_internal_zzac_zza, ComponentName componentName) {
                this.zzaYY = com_google_android_gms_measurement_internal_zzac_zza;
                this.zzQQ = componentName;
            }

            public void run() {
                this.zzaYY.zzaYU.onServiceDisconnected(this.zzQQ);
            }
        }

        /* renamed from: com.google.android.gms.measurement.internal.zzac.zza.3 */
        class C03383 implements Runnable {
            final /* synthetic */ zza zzaYY;
            final /* synthetic */ zzm zzaYZ;

            C03383(zza com_google_android_gms_measurement_internal_zzac_zza, zzm com_google_android_gms_measurement_internal_zzm) {
                this.zzaYY = com_google_android_gms_measurement_internal_zzac_zza;
                this.zzaYZ = com_google_android_gms_measurement_internal_zzm;
            }

            public void run() {
                synchronized (this.zzaYY) {
                    this.zzaYY.zzaYV = false;
                    if (!this.zzaYY.zzaYU.isConnected()) {
                        this.zzaYY.zzaYU.zzAo().zzCJ().zzfg("Connected to remote service");
                        this.zzaYY.zzaYU.zza(this.zzaYZ);
                    }
                }
            }
        }

        /* renamed from: com.google.android.gms.measurement.internal.zzac.zza.4 */
        class C03394 implements Runnable {
            final /* synthetic */ zza zzaYY;

            C03394(zza com_google_android_gms_measurement_internal_zzac_zza) {
                this.zzaYY = com_google_android_gms_measurement_internal_zzac_zza;
            }

            public void run() {
                this.zzaYY.zzaYU.onServiceDisconnected(new ComponentName(this.zzaYY.zzaYU.getContext(), AppMeasurementService.class));
            }
        }

        protected zza(zzac com_google_android_gms_measurement_internal_zzac) {
            this.zzaYU = com_google_android_gms_measurement_internal_zzac;
        }

        @MainThread
        public void onConnected(@Nullable Bundle connectionHint) {
            zzx.zzcD("MeasurementServiceConnection.onConnected");
            synchronized (this) {
                try {
                    zzm com_google_android_gms_measurement_internal_zzm = (zzm) this.zzaYW.zzqJ();
                    this.zzaYW = null;
                    this.zzaYU.zzCn().zzg(new C03383(this, com_google_android_gms_measurement_internal_zzm));
                } catch (DeadObjectException e) {
                    this.zzaYW = null;
                    this.zzaYV = false;
                } catch (IllegalStateException e2) {
                    this.zzaYW = null;
                    this.zzaYV = false;
                }
            }
        }

        @MainThread
        public void onConnectionFailed(@NonNull ConnectionResult result) {
            zzx.zzcD("MeasurementServiceConnection.onConnectionFailed");
            zzp zzCT = this.zzaYU.zzaTV.zzCT();
            if (zzCT != null) {
                zzCT.zzCF().zzj("Service connection failed", result);
            }
            synchronized (this) {
                this.zzaYV = false;
                this.zzaYW = null;
            }
        }

        @MainThread
        public void onConnectionSuspended(int cause) {
            zzx.zzcD("MeasurementServiceConnection.onConnectionSuspended");
            this.zzaYU.zzAo().zzCJ().zzfg("Service connection suspended");
            this.zzaYU.zzCn().zzg(new C03394(this));
        }

        @MainThread
        public void onServiceConnected(ComponentName name, IBinder binder) {
            zzx.zzcD("MeasurementServiceConnection.onServiceConnected");
            synchronized (this) {
                if (binder == null) {
                    this.zzaYV = false;
                    this.zzaYU.zzAo().zzCE().zzfg("Service connected with null binder");
                    return;
                }
                zzm com_google_android_gms_measurement_internal_zzm = null;
                try {
                    String interfaceDescriptor = binder.getInterfaceDescriptor();
                    if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                        com_google_android_gms_measurement_internal_zzm = com.google.android.gms.measurement.internal.zzm.zza.zzdn(binder);
                        this.zzaYU.zzAo().zzCK().zzfg("Bound to IMeasurementService interface");
                    } else {
                        this.zzaYU.zzAo().zzCE().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                    }
                } catch (RemoteException e) {
                    this.zzaYU.zzAo().zzCE().zzfg("Service connect failed to get IMeasurementService");
                }
                if (com_google_android_gms_measurement_internal_zzm == null) {
                    this.zzaYV = false;
                    try {
                        zzb.zzrP().zza(this.zzaYU.getContext(), this.zzaYU.zzaYN);
                    } catch (IllegalArgumentException e2) {
                    }
                } else {
                    this.zzaYU.zzCn().zzg(new C03361(this, com_google_android_gms_measurement_internal_zzm));
                }
                return;
            }
        }

        @MainThread
        public void onServiceDisconnected(ComponentName name) {
            zzx.zzcD("MeasurementServiceConnection.onServiceDisconnected");
            this.zzaYU.zzAo().zzCJ().zzfg("Service disconnected");
            this.zzaYU.zzCn().zzg(new C03372(this, name));
        }

        @WorkerThread
        public void zzDt() {
            this.zzaYU.zzjk();
            Context context = this.zzaYU.getContext();
            synchronized (this) {
                if (this.zzaYV) {
                    this.zzaYU.zzAo().zzCK().zzfg("Connection attempt already in progress");
                } else if (this.zzaYW != null) {
                    this.zzaYU.zzAo().zzCK().zzfg("Already awaiting connection attempt");
                } else {
                    this.zzaYW = new zzo(context, Looper.getMainLooper(), zzf.zzat(context), this, this);
                    this.zzaYU.zzAo().zzCK().zzfg("Connecting to remote service");
                    this.zzaYV = true;
                    this.zzaYW.zzqG();
                }
            }
        }

        @WorkerThread
        public void zzz(Intent intent) {
            this.zzaYU.zzjk();
            Context context = this.zzaYU.getContext();
            zzb zzrP = zzb.zzrP();
            synchronized (this) {
                if (this.zzaYV) {
                    this.zzaYU.zzAo().zzCK().zzfg("Connection attempt already in progress");
                    return;
                }
                this.zzaYV = true;
                zzrP.zza(context, intent, this.zzaYU.zzaYN, 129);
            }
        }
    }

    protected zzac(zzw com_google_android_gms_measurement_internal_zzw) {
        super(com_google_android_gms_measurement_internal_zzw);
        this.zzaYS = new ArrayList();
        this.zzaYR = new zzaf(com_google_android_gms_measurement_internal_zzw.zzjl());
        this.zzaYN = new zza(this);
        this.zzaYQ = new C16511(this, com_google_android_gms_measurement_internal_zzw);
        this.zzaYT = new C16522(this, com_google_android_gms_measurement_internal_zzw);
    }

    @WorkerThread
    private void onServiceDisconnected(ComponentName name) {
        zzjk();
        if (this.zzaYO != null) {
            this.zzaYO = null;
            zzAo().zzCK().zzj("Disconnected from device MeasurementService", name);
            zzDr();
        }
    }

    private boolean zzDp() {
        List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent(getContext(), AppMeasurementService.class), AccessibilityNodeInfoCompat.ACTION_CUT);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    @WorkerThread
    private boolean zzDq() {
        zzjk();
        zzjv();
        if (zzCp().zzkr()) {
            return true;
        }
        zzAo().zzCK().zzfg("Checking service availability");
        switch (zzc.zzoK().isGooglePlayServicesAvailable(getContext())) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                zzAo().zzCK().zzfg("Service available");
                return true;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                zzAo().zzCK().zzfg("Service missing");
                return false;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                zzAo().zzCK().zzfg("Service version update required");
                return false;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                zzAo().zzCK().zzfg("Service disabled");
                return false;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                zzAo().zzCK().zzfg("Service invalid");
                return false;
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                zzAo().zzCK().zzfg("Service updating");
                return true;
            default:
                return false;
        }
    }

    @WorkerThread
    private void zzDr() {
        zzjk();
        zzjX();
    }

    @WorkerThread
    private void zzDs() {
        zzjk();
        zzAo().zzCK().zzj("Processing queued up service tasks", Integer.valueOf(this.zzaYS.size()));
        for (Runnable zzg : this.zzaYS) {
            zzCn().zzg(zzg);
        }
        this.zzaYS.clear();
        this.zzaYT.cancel();
    }

    @WorkerThread
    private void zza(zzm com_google_android_gms_measurement_internal_zzm) {
        zzjk();
        zzx.zzz(com_google_android_gms_measurement_internal_zzm);
        this.zzaYO = com_google_android_gms_measurement_internal_zzm;
        zzjI();
        zzDs();
    }

    @WorkerThread
    private void zzi(Runnable runnable) throws IllegalStateException {
        zzjk();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzaYS.size()) >= zzCp().zzBS()) {
            zzAo().zzCE().zzfg("Discarding data. Max runnable queue size reached");
        } else {
            this.zzaYS.add(runnable);
            if (!this.zzaTV.zzCZ()) {
                this.zzaYT.zzt(HlsChunkSource.DEFAULT_PLAYLIST_BLACKLIST_MS);
            }
            zzjX();
        }
    }

    @WorkerThread
    private void zzjI() {
        zzjk();
        this.zzaYR.start();
        if (!this.zzaTV.zzCZ()) {
            this.zzaYQ.zzt(zzCp().zzkM());
        }
    }

    @WorkerThread
    private void zzjJ() {
        zzjk();
        if (isConnected()) {
            zzAo().zzCK().zzfg("Inactivity, disconnecting from AppMeasurementService");
            disconnect();
        }
    }

    @WorkerThread
    private void zzjX() {
        zzjk();
        zzjv();
        if (!isConnected()) {
            if (this.zzaYP == null) {
                this.zzaYP = zzCo().zzCP();
                if (this.zzaYP == null) {
                    zzAo().zzCK().zzfg("State of service unknown");
                    this.zzaYP = Boolean.valueOf(zzDq());
                    zzCo().zzar(this.zzaYP.booleanValue());
                }
            }
            if (this.zzaYP.booleanValue()) {
                zzAo().zzCK().zzfg("Using measurement service");
                this.zzaYN.zzDt();
            } else if (zzDp() && !this.zzaTV.zzCZ()) {
                zzAo().zzCK().zzfg("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                intent.setComponent(new ComponentName(getContext(), AppMeasurementService.class));
                this.zzaYN.zzz(intent);
            } else if (zzCp().zzks()) {
                zzAo().zzCK().zzfg("Using direct local measurement implementation");
                zza(new zzx(this.zzaTV, true));
            } else {
                zzAo().zzCE().zzfg("Not in main process. Unable to use local measurement implementation. Please register the AppMeasurementService service in the app manifest");
            }
        }
    }

    @WorkerThread
    public void disconnect() {
        zzjk();
        zzjv();
        try {
            zzb.zzrP().zza(getContext(), this.zzaYN);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzaYO = null;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public boolean isConnected() {
        zzjk();
        zzjv();
        return this.zzaYO != null;
    }

    public /* bridge */ /* synthetic */ zzp zzAo() {
        return super.zzAo();
    }

    public /* bridge */ /* synthetic */ void zzCd() {
        super.zzCd();
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

    @WorkerThread
    protected void zzDl() {
        zzjk();
        zzjv();
        zzi(new C03356(this));
    }

    @WorkerThread
    protected void zzDo() {
        zzjk();
        zzjv();
        zzi(new C03323(this));
    }

    @WorkerThread
    protected void zza(UserAttributeParcel userAttributeParcel) {
        zzjk();
        zzjv();
        zzi(new C03345(this, userAttributeParcel));
    }

    @WorkerThread
    protected void zzb(EventParcel eventParcel, String str) {
        zzx.zzz(eventParcel);
        zzjk();
        zzjv();
        zzi(new C03334(this, str, eventParcel));
    }

    protected void zziJ() {
    }

    public /* bridge */ /* synthetic */ void zzjj() {
        super.zzjj();
    }

    public /* bridge */ /* synthetic */ void zzjk() {
        super.zzjk();
    }

    public /* bridge */ /* synthetic */ zzmq zzjl() {
        return super.zzjl();
    }
}
