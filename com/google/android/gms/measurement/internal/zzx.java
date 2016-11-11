package com.google.android.gms.measurement.internal;

import android.os.Binder;
import android.os.Process;
import android.support.annotation.BinderThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.zze;
import com.google.android.gms.measurement.internal.zzm.zza;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class zzx extends zza {
    private final zzw zzaTV;
    private final boolean zzaYw;

    /* renamed from: com.google.android.gms.measurement.internal.zzx.1 */
    class C03501 implements Runnable {
        final /* synthetic */ AppMetadata zzaYx;
        final /* synthetic */ zzx zzaYy;

        C03501(zzx com_google_android_gms_measurement_internal_zzx, AppMetadata appMetadata) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYx = appMetadata;
        }

        public void run() {
            this.zzaYy.zzfl(this.zzaYx.zzaVx);
            this.zzaYy.zzaTV.zzc(this.zzaYx);
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzx.2 */
    class C03512 implements Runnable {
        final /* synthetic */ AppMetadata zzaYx;
        final /* synthetic */ zzx zzaYy;
        final /* synthetic */ EventParcel zzaYz;

        C03512(zzx com_google_android_gms_measurement_internal_zzx, AppMetadata appMetadata, EventParcel eventParcel) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYx = appMetadata;
            this.zzaYz = eventParcel;
        }

        public void run() {
            this.zzaYy.zzfl(this.zzaYx.zzaVx);
            this.zzaYy.zzaTV.zzb(this.zzaYz, this.zzaYx);
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzx.3 */
    class C03523 implements Runnable {
        final /* synthetic */ String zzaHU;
        final /* synthetic */ String zzaYA;
        final /* synthetic */ zzx zzaYy;
        final /* synthetic */ EventParcel zzaYz;

        C03523(zzx com_google_android_gms_measurement_internal_zzx, String str, EventParcel eventParcel, String str2) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYA = str;
            this.zzaYz = eventParcel;
            this.zzaHU = str2;
        }

        public void run() {
            this.zzaYy.zzfl(this.zzaYA);
            this.zzaYy.zzaTV.zza(this.zzaYz, this.zzaHU);
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzx.4 */
    class C03534 implements Runnable {
        final /* synthetic */ UserAttributeParcel zzaYB;
        final /* synthetic */ AppMetadata zzaYx;
        final /* synthetic */ zzx zzaYy;

        C03534(zzx com_google_android_gms_measurement_internal_zzx, AppMetadata appMetadata, UserAttributeParcel userAttributeParcel) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYx = appMetadata;
            this.zzaYB = userAttributeParcel;
        }

        public void run() {
            this.zzaYy.zzfl(this.zzaYx.zzaVx);
            this.zzaYy.zzaTV.zzc(this.zzaYB, this.zzaYx);
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzx.5 */
    class C03545 implements Runnable {
        final /* synthetic */ UserAttributeParcel zzaYB;
        final /* synthetic */ AppMetadata zzaYx;
        final /* synthetic */ zzx zzaYy;

        C03545(zzx com_google_android_gms_measurement_internal_zzx, AppMetadata appMetadata, UserAttributeParcel userAttributeParcel) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYx = appMetadata;
            this.zzaYB = userAttributeParcel;
        }

        public void run() {
            this.zzaYy.zzfl(this.zzaYx.zzaVx);
            this.zzaYy.zzaTV.zzb(this.zzaYB, this.zzaYx);
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzx.6 */
    class C03556 implements Callable<List<zzai>> {
        final /* synthetic */ AppMetadata zzaYx;
        final /* synthetic */ zzx zzaYy;

        C03556(zzx com_google_android_gms_measurement_internal_zzx, AppMetadata appMetadata) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYx = appMetadata;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzDh();
        }

        public List<zzai> zzDh() throws Exception {
            return this.zzaYy.zzaTV.zzCj().zzeX(this.zzaYx.zzaVt);
        }
    }

    /* renamed from: com.google.android.gms.measurement.internal.zzx.7 */
    class C03567 implements Runnable {
        final /* synthetic */ AppMetadata zzaYx;
        final /* synthetic */ zzx zzaYy;

        C03567(zzx com_google_android_gms_measurement_internal_zzx, AppMetadata appMetadata) {
            this.zzaYy = com_google_android_gms_measurement_internal_zzx;
            this.zzaYx = appMetadata;
        }

        public void run() {
            this.zzaYy.zzfl(this.zzaYx.zzaVx);
            this.zzaYy.zzaTV.zzd(this.zzaYx);
        }
    }

    public zzx(zzw com_google_android_gms_measurement_internal_zzw) {
        com.google.android.gms.common.internal.zzx.zzz(com_google_android_gms_measurement_internal_zzw);
        this.zzaTV = com_google_android_gms_measurement_internal_zzw;
        this.zzaYw = false;
    }

    public zzx(zzw com_google_android_gms_measurement_internal_zzw, boolean z) {
        com.google.android.gms.common.internal.zzx.zzz(com_google_android_gms_measurement_internal_zzw);
        this.zzaTV = com_google_android_gms_measurement_internal_zzw;
        this.zzaYw = z;
    }

    @BinderThread
    private void zzfm(String str) throws SecurityException {
        if (TextUtils.isEmpty(str)) {
            this.zzaTV.zzAo().zzCE().zzfg("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        try {
            zzfn(str);
        } catch (SecurityException e) {
            this.zzaTV.zzAo().zzCE().zzj("Measurement Service called with invalid calling package", str);
            throw e;
        }
    }

    private void zzfn(String str) throws SecurityException {
        int myUid = this.zzaYw ? Process.myUid() : Binder.getCallingUid();
        if (!zze.zzb(this.zzaTV.getContext(), myUid, str)) {
            if (!zze.zzf(this.zzaTV.getContext(), myUid) || this.zzaTV.zzCZ()) {
                throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
            }
        }
    }

    @BinderThread
    public List<UserAttributeParcel> zza(AppMetadata appMetadata, boolean z) {
        Object e;
        com.google.android.gms.common.internal.zzx.zzz(appMetadata);
        zzfm(appMetadata.packageName);
        try {
            List<zzai> list = (List) this.zzaTV.zzCn().zzd(new C03556(this, appMetadata)).get();
            List<UserAttributeParcel> arrayList = new ArrayList(list.size());
            for (zzai com_google_android_gms_measurement_internal_zzai : list) {
                if (z || !zzaj.zzfv(com_google_android_gms_measurement_internal_zzai.mName)) {
                    arrayList.add(new UserAttributeParcel(com_google_android_gms_measurement_internal_zzai));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzaTV.zzAo().zzCE().zzj("Failed to get user attributes", e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzaTV.zzAo().zzCE().zzj("Failed to get user attributes", e);
            return null;
        }
    }

    @BinderThread
    public void zza(AppMetadata appMetadata) {
        com.google.android.gms.common.internal.zzx.zzz(appMetadata);
        zzfm(appMetadata.packageName);
        this.zzaTV.zzCn().zzg(new C03567(this, appMetadata));
    }

    @BinderThread
    public void zza(EventParcel eventParcel, AppMetadata appMetadata) {
        com.google.android.gms.common.internal.zzx.zzz(eventParcel);
        com.google.android.gms.common.internal.zzx.zzz(appMetadata);
        zzfm(appMetadata.packageName);
        this.zzaTV.zzCn().zzg(new C03512(this, appMetadata, eventParcel));
    }

    @BinderThread
    public void zza(EventParcel eventParcel, String str, String str2) {
        com.google.android.gms.common.internal.zzx.zzz(eventParcel);
        com.google.android.gms.common.internal.zzx.zzcM(str);
        zzfm(str);
        this.zzaTV.zzCn().zzg(new C03523(this, str2, eventParcel, str));
    }

    @BinderThread
    public void zza(UserAttributeParcel userAttributeParcel, AppMetadata appMetadata) {
        com.google.android.gms.common.internal.zzx.zzz(userAttributeParcel);
        com.google.android.gms.common.internal.zzx.zzz(appMetadata);
        zzfm(appMetadata.packageName);
        if (userAttributeParcel.getValue() == null) {
            this.zzaTV.zzCn().zzg(new C03534(this, appMetadata, userAttributeParcel));
        } else {
            this.zzaTV.zzCn().zzg(new C03545(this, appMetadata, userAttributeParcel));
        }
    }

    @BinderThread
    public void zzb(AppMetadata appMetadata) {
        com.google.android.gms.common.internal.zzx.zzz(appMetadata);
        zzfm(appMetadata.packageName);
        this.zzaTV.zzCn().zzg(new C03501(this, appMetadata));
    }

    @WorkerThread
    void zzfl(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split(":", 2);
            if (split.length == 2) {
                try {
                    long longValue = Long.valueOf(split[0]).longValue();
                    if (longValue > 0) {
                        this.zzaTV.zzCo().zzaXi.zzf(split[1], longValue);
                    } else {
                        this.zzaTV.zzAo().zzCF().zzj("Combining sample with a non-positive weight", Long.valueOf(longValue));
                    }
                } catch (NumberFormatException e) {
                    this.zzaTV.zzAo().zzCF().zzj("Combining sample with a non-number weight", split[0]);
                }
            }
        }
    }
}
