package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzmq;
import com.google.android.gms.measurement.AppMeasurement;
import org.telegram.messenger.MessagesController;

public class zzp extends zzz {
    private final long zzaVj;
    private final char zzaWB;
    private final zza zzaWC;
    private final zza zzaWD;
    private final zza zzaWE;
    private final zza zzaWF;
    private final zza zzaWG;
    private final zza zzaWH;
    private final zza zzaWI;
    private final zza zzaWJ;
    private final zza zzaWK;
    private final String zzamn;

    /* renamed from: com.google.android.gms.measurement.internal.zzp.1 */
    class C03451 implements Runnable {
        final /* synthetic */ String zzaWL;
        final /* synthetic */ zzp zzaWM;

        C03451(zzp com_google_android_gms_measurement_internal_zzp, String str) {
            this.zzaWM = com_google_android_gms_measurement_internal_zzp;
            this.zzaWL = str;
        }

        public void run() {
            zzt zzCo = this.zzaWM.zzaTV.zzCo();
            if (!zzCo.isInitialized() || zzCo.zzDi()) {
                this.zzaWM.zzl(6, "Persisted config not initialized . Not logging error/warn.");
            } else {
                zzCo.zzaXi.zzbq(this.zzaWL);
            }
        }
    }

    public class zza {
        private final int mPriority;
        final /* synthetic */ zzp zzaWM;
        private final boolean zzaWN;
        private final boolean zzaWO;

        zza(zzp com_google_android_gms_measurement_internal_zzp, int i, boolean z, boolean z2) {
            this.zzaWM = com_google_android_gms_measurement_internal_zzp;
            this.mPriority = i;
            this.zzaWN = z;
            this.zzaWO = z2;
        }

        public void zzd(String str, Object obj, Object obj2, Object obj3) {
            this.zzaWM.zza(this.mPriority, this.zzaWN, this.zzaWO, str, obj, obj2, obj3);
        }

        public void zze(String str, Object obj, Object obj2) {
            this.zzaWM.zza(this.mPriority, this.zzaWN, this.zzaWO, str, obj, obj2, null);
        }

        public void zzfg(String str) {
            this.zzaWM.zza(this.mPriority, this.zzaWN, this.zzaWO, str, null, null, null);
        }

        public void zzj(String str, Object obj) {
            this.zzaWM.zza(this.mPriority, this.zzaWN, this.zzaWO, str, obj, null, null);
        }
    }

    zzp(zzw com_google_android_gms_measurement_internal_zzw) {
        super(com_google_android_gms_measurement_internal_zzw);
        this.zzamn = zzCp().zzBz();
        this.zzaVj = zzCp().zzBp();
        if (zzCp().zzks()) {
            this.zzaWB = zzCp().zzkr() ? 'P' : 'C';
        } else {
            this.zzaWB = zzCp().zzkr() ? 'p' : 'c';
        }
        this.zzaWC = new zza(this, 6, false, false);
        this.zzaWD = new zza(this, 6, true, false);
        this.zzaWE = new zza(this, 6, false, true);
        this.zzaWF = new zza(this, 5, false, false);
        this.zzaWG = new zza(this, 5, true, false);
        this.zzaWH = new zza(this, 5, false, true);
        this.zzaWI = new zza(this, 4, false, false);
        this.zzaWJ = new zza(this, 3, false, false);
        this.zzaWK = new zza(this, 2, false, false);
    }

    static String zza(boolean z, String str, Object obj, Object obj2, Object obj3) {
        if (str == null) {
            Object obj4 = TtmlNode.ANONYMOUS_REGION_ID;
        }
        Object zzc = zzc(z, obj);
        Object zzc2 = zzc(z, obj2);
        Object zzc3 = zzc(z, obj3);
        StringBuilder stringBuilder = new StringBuilder();
        String str2 = TtmlNode.ANONYMOUS_REGION_ID;
        if (!TextUtils.isEmpty(obj4)) {
            stringBuilder.append(obj4);
            str2 = ": ";
        }
        if (!TextUtils.isEmpty(zzc)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzc);
            str2 = ", ";
        }
        if (!TextUtils.isEmpty(zzc2)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzc2);
            str2 = ", ";
        }
        if (!TextUtils.isEmpty(zzc3)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzc3);
        }
        return stringBuilder.toString();
    }

    static String zzc(boolean z, Object obj) {
        if (obj == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        Object valueOf = obj instanceof Integer ? Long.valueOf((long) ((Integer) obj).intValue()) : obj;
        if (valueOf instanceof Long) {
            if (!z) {
                return String.valueOf(valueOf);
            }
            if (Math.abs(((Long) valueOf).longValue()) < 100) {
                return String.valueOf(valueOf);
            }
            String str = String.valueOf(valueOf).charAt(0) == '-' ? "-" : TtmlNode.ANONYMOUS_REGION_ID;
            String valueOf2 = String.valueOf(Math.abs(((Long) valueOf).longValue()));
            return str + Math.round(Math.pow(10.0d, (double) (valueOf2.length() - 1))) + "..." + str + Math.round(Math.pow(10.0d, (double) valueOf2.length()) - 1.0d);
        } else if (valueOf instanceof Boolean) {
            return String.valueOf(valueOf);
        } else {
            if (!(valueOf instanceof Throwable)) {
                return z ? "-" : String.valueOf(valueOf);
            } else {
                Throwable th = (Throwable) valueOf;
                StringBuilder stringBuilder = new StringBuilder(th.toString());
                String zzff = zzff(AppMeasurement.class.getCanonicalName());
                String zzff2 = zzff(zzw.class.getCanonicalName());
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    if (!stackTraceElement.isNativeMethod()) {
                        String className = stackTraceElement.getClassName();
                        if (className != null) {
                            className = zzff(className);
                            if (className.equals(zzff) || className.equals(zzff2)) {
                                stringBuilder.append(": ");
                                stringBuilder.append(stackTraceElement);
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }
                return stringBuilder.toString();
            }
        }
    }

    private static String zzff(String str) {
        if (TextUtils.isEmpty(str)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf != -1 ? str.substring(0, lastIndexOf) : str;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ zzp zzAo() {
        return super.zzAo();
    }

    public zza zzCE() {
        return this.zzaWC;
    }

    public zza zzCF() {
        return this.zzaWF;
    }

    public zza zzCG() {
        return this.zzaWG;
    }

    public zza zzCH() {
        return this.zzaWH;
    }

    public zza zzCI() {
        return this.zzaWI;
    }

    public zza zzCJ() {
        return this.zzaWJ;
    }

    public zza zzCK() {
        return this.zzaWK;
    }

    public String zzCL() {
        Pair zzlN = zzCo().zzaXi.zzlN();
        return zzlN == null ? null : String.valueOf(zzlN.second) + ":" + ((String) zzlN.first);
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

    protected boolean zzQ(int i) {
        return Log.isLoggable(this.zzamn, i);
    }

    protected void zza(int i, boolean z, boolean z2, String str, Object obj, Object obj2, Object obj3) {
        if (!z && zzQ(i)) {
            zzl(i, zza(false, str, obj, obj2, obj3));
        }
        if (!z2 && i >= 5) {
            zzb(i, str, obj, obj2, obj3);
        }
    }

    public void zzb(int i, String str, Object obj, Object obj2, Object obj3) {
        zzx.zzz(str);
        zzv zzCU = this.zzaTV.zzCU();
        if (zzCU == null) {
            zzl(6, "Scheduler not set. Not logging error/warn.");
        } else if (!zzCU.isInitialized()) {
            zzl(6, "Scheduler not initialized. Not logging error/warn.");
        } else if (zzCU.zzDi()) {
            zzl(6, "Scheduler shutdown. Not logging error/warn.");
        } else {
            if (i < 0) {
                i = 0;
            }
            if (i >= "01VDIWEA?".length()) {
                i = "01VDIWEA?".length() - 1;
            }
            String str2 = "1" + "01VDIWEA?".charAt(i) + this.zzaWB + this.zzaVj + ":" + zza(true, str, obj, obj2, obj3);
            zzCU.zzg(new C03451(this, str2.length() > MessagesController.UPDATE_MASK_PHONE ? str.substring(0, MessagesController.UPDATE_MASK_PHONE) : str2));
        }
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

    protected void zzl(int i, String str) {
        Log.println(i, this.zzamn, str);
    }
}
