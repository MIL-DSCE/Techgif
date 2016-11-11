package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.internal.zzx;
import java.util.Map;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;

@zzhb
public class zzeg {
    private final Context mContext;
    private final String zzAY;
    private zzb<zzed> zzAZ;
    private zzb<zzed> zzBa;
    private zze zzBb;
    private int zzBc;
    private final VersionInfoParcel zzpT;
    private final Object zzpV;

    /* renamed from: com.google.android.gms.internal.zzeg.1 */
    class C02541 implements Runnable {
        final /* synthetic */ zze zzBd;
        final /* synthetic */ zzeg zzBe;

        /* renamed from: com.google.android.gms.internal.zzeg.1.4 */
        class C02534 implements Runnable {
            final /* synthetic */ zzed zzBf;
            final /* synthetic */ C02541 zzBg;

            /* renamed from: com.google.android.gms.internal.zzeg.1.4.1 */
            class C02521 implements Runnable {
                final /* synthetic */ C02534 zzBk;

                C02521(C02534 c02534) {
                    this.zzBk = c02534;
                }

                public void run() {
                    this.zzBk.zzBf.destroy();
                }
            }

            C02534(C02541 c02541, zzed com_google_android_gms_internal_zzed) {
                this.zzBg = c02541;
                this.zzBf = com_google_android_gms_internal_zzed;
            }

            public void run() {
                synchronized (this.zzBg.zzBe.zzpV) {
                    if (this.zzBg.zzBd.getStatus() == -1 || this.zzBg.zzBd.getStatus() == 1) {
                        return;
                    }
                    this.zzBg.zzBd.reject();
                    zzir.runOnUiThread(new C02521(this));
                    zzin.m22v("Could not receive loaded message in a timely manner. Rejecting.");
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.1.1 */
        class C16161 implements com.google.android.gms.internal.zzed.zza {
            final /* synthetic */ zzed zzBf;
            final /* synthetic */ C02541 zzBg;

            /* renamed from: com.google.android.gms.internal.zzeg.1.1.1 */
            class C02511 implements Runnable {
                final /* synthetic */ C16161 zzBh;

                /* renamed from: com.google.android.gms.internal.zzeg.1.1.1.1 */
                class C02501 implements Runnable {
                    final /* synthetic */ C02511 zzBi;

                    C02501(C02511 c02511) {
                        this.zzBi = c02511;
                    }

                    public void run() {
                        this.zzBi.zzBh.zzBf.destroy();
                    }
                }

                C02511(C16161 c16161) {
                    this.zzBh = c16161;
                }

                public void run() {
                    synchronized (this.zzBh.zzBg.zzBe.zzpV) {
                        if (this.zzBh.zzBg.zzBd.getStatus() == -1 || this.zzBh.zzBg.zzBd.getStatus() == 1) {
                            return;
                        }
                        this.zzBh.zzBg.zzBd.reject();
                        zzir.runOnUiThread(new C02501(this));
                        zzin.m22v("Could not receive loaded message in a timely manner. Rejecting.");
                    }
                }
            }

            C16161(C02541 c02541, zzed com_google_android_gms_internal_zzed) {
                this.zzBg = c02541;
                this.zzBf = com_google_android_gms_internal_zzed;
            }

            public void zzeo() {
                zzir.zzMc.postDelayed(new C02511(this), (long) zza.zzBn);
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.1.2 */
        class C16172 implements zzdf {
            final /* synthetic */ zzed zzBf;
            final /* synthetic */ C02541 zzBg;

            C16172(C02541 c02541, zzed com_google_android_gms_internal_zzed) {
                this.zzBg = c02541;
                this.zzBf = com_google_android_gms_internal_zzed;
            }

            public void zza(zzjp com_google_android_gms_internal_zzjp, Map<String, String> map) {
                synchronized (this.zzBg.zzBe.zzpV) {
                    if (this.zzBg.zzBd.getStatus() == -1 || this.zzBg.zzBd.getStatus() == 1) {
                        return;
                    }
                    this.zzBg.zzBe.zzBc = 0;
                    this.zzBg.zzBe.zzAZ.zze(this.zzBf);
                    this.zzBg.zzBd.zzh(this.zzBf);
                    this.zzBg.zzBe.zzBb = this.zzBg.zzBd;
                    zzin.m22v("Successfully loaded JS Engine.");
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.1.3 */
        class C16183 implements zzdf {
            final /* synthetic */ zzed zzBf;
            final /* synthetic */ C02541 zzBg;
            final /* synthetic */ zzja zzBj;

            C16183(C02541 c02541, zzed com_google_android_gms_internal_zzed, zzja com_google_android_gms_internal_zzja) {
                this.zzBg = c02541;
                this.zzBf = com_google_android_gms_internal_zzed;
                this.zzBj = com_google_android_gms_internal_zzja;
            }

            public void zza(zzjp com_google_android_gms_internal_zzjp, Map<String, String> map) {
                synchronized (this.zzBg.zzBe.zzpV) {
                    com.google.android.gms.ads.internal.util.client.zzb.zzaJ("JS Engine is requesting an update");
                    if (this.zzBg.zzBe.zzBc == 0) {
                        com.google.android.gms.ads.internal.util.client.zzb.zzaJ("Starting reload.");
                        this.zzBg.zzBe.zzBc = 2;
                        this.zzBg.zzBe.zzeq();
                    }
                    this.zzBf.zzb("/requestReload", (zzdf) this.zzBj.get());
                }
            }
        }

        C02541(zzeg com_google_android_gms_internal_zzeg, zze com_google_android_gms_internal_zzeg_zze) {
            this.zzBe = com_google_android_gms_internal_zzeg;
            this.zzBd = com_google_android_gms_internal_zzeg_zze;
        }

        public void run() {
            zzed zza = this.zzBe.zza(this.zzBe.mContext, this.zzBe.zzpT);
            zza.zza(new C16161(this, zza));
            zza.zza("/jsLoaded", new C16172(this, zza));
            zzja com_google_android_gms_internal_zzja = new zzja();
            zzdf c16183 = new C16183(this, zza, com_google_android_gms_internal_zzja);
            com_google_android_gms_internal_zzja.set(c16183);
            zza.zza("/requestReload", c16183);
            if (this.zzBe.zzAY.endsWith(".js")) {
                zza.zzZ(this.zzBe.zzAY);
            } else if (this.zzBe.zzAY.startsWith("<html>")) {
                zza.zzab(this.zzBe.zzAY);
            } else {
                zza.zzaa(this.zzBe.zzAY);
            }
            zzir.zzMc.postDelayed(new C02534(this, zza), (long) zza.zzBm);
        }
    }

    static class zza {
        static int zzBm;
        static int zzBn;

        static {
            zzBm = 60000;
            zzBn = AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
        }
    }

    public interface zzb<T> {
        void zze(T t);
    }

    /* renamed from: com.google.android.gms.internal.zzeg.2 */
    class C16192 implements com.google.android.gms.internal.zzji.zzc<zzed> {
        final /* synthetic */ zzeg zzBe;
        final /* synthetic */ zze zzBl;

        C16192(zzeg com_google_android_gms_internal_zzeg, zze com_google_android_gms_internal_zzeg_zze) {
            this.zzBe = com_google_android_gms_internal_zzeg;
            this.zzBl = com_google_android_gms_internal_zzeg_zze;
        }

        public void zza(zzed com_google_android_gms_internal_zzed) {
            synchronized (this.zzBe.zzpV) {
                this.zzBe.zzBc = 0;
                if (!(this.zzBe.zzBb == null || this.zzBl == this.zzBe.zzBb)) {
                    zzin.m22v("New JS engine is loaded, marking previous one as destroyable.");
                    this.zzBe.zzBb.zzeu();
                }
                this.zzBe.zzBb = this.zzBl;
            }
        }

        public /* synthetic */ void zze(Object obj) {
            zza((zzed) obj);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzeg.3 */
    class C16203 implements com.google.android.gms.internal.zzji.zza {
        final /* synthetic */ zzeg zzBe;
        final /* synthetic */ zze zzBl;

        C16203(zzeg com_google_android_gms_internal_zzeg, zze com_google_android_gms_internal_zzeg_zze) {
            this.zzBe = com_google_android_gms_internal_zzeg;
            this.zzBl = com_google_android_gms_internal_zzeg_zze;
        }

        public void run() {
            synchronized (this.zzBe.zzpV) {
                this.zzBe.zzBc = 1;
                zzin.m22v("Failed loading new engine. Marking new engine destroyable.");
                this.zzBl.zzeu();
            }
        }
    }

    public static class zzc<T> implements zzb<T> {
        public void zze(T t) {
        }
    }

    public static class zzd extends zzjj<zzeh> {
        private final zze zzBo;
        private boolean zzBp;
        private final Object zzpV;

        /* renamed from: com.google.android.gms.internal.zzeg.zzd.1 */
        class C16211 implements com.google.android.gms.internal.zzji.zzc<zzeh> {
            final /* synthetic */ zzd zzBq;

            C16211(zzd com_google_android_gms_internal_zzeg_zzd) {
                this.zzBq = com_google_android_gms_internal_zzeg_zzd;
            }

            public void zzd(zzeh com_google_android_gms_internal_zzeh) {
                zzin.m22v("Ending javascript session.");
                ((zzei) com_google_android_gms_internal_zzeh).zzew();
            }

            public /* synthetic */ void zze(Object obj) {
                zzd((zzeh) obj);
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.zzd.2 */
        class C16222 implements com.google.android.gms.internal.zzji.zzc<zzeh> {
            final /* synthetic */ zzd zzBq;

            C16222(zzd com_google_android_gms_internal_zzeg_zzd) {
                this.zzBq = com_google_android_gms_internal_zzeg_zzd;
            }

            public void zzd(zzeh com_google_android_gms_internal_zzeh) {
                zzin.m22v("Releasing engine reference.");
                this.zzBq.zzBo.zzet();
            }

            public /* synthetic */ void zze(Object obj) {
                zzd((zzeh) obj);
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.zzd.3 */
        class C16233 implements com.google.android.gms.internal.zzji.zza {
            final /* synthetic */ zzd zzBq;

            C16233(zzd com_google_android_gms_internal_zzeg_zzd) {
                this.zzBq = com_google_android_gms_internal_zzeg_zzd;
            }

            public void run() {
                this.zzBq.zzBo.zzet();
            }
        }

        public zzd(zze com_google_android_gms_internal_zzeg_zze) {
            this.zzpV = new Object();
            this.zzBo = com_google_android_gms_internal_zzeg_zze;
        }

        public void release() {
            synchronized (this.zzpV) {
                if (this.zzBp) {
                    return;
                }
                this.zzBp = true;
                zza(new C16211(this), new com.google.android.gms.internal.zzji.zzb());
                zza(new C16222(this), new C16233(this));
            }
        }
    }

    public static class zze extends zzjj<zzed> {
        private zzb<zzed> zzBa;
        private boolean zzBr;
        private int zzBs;
        private final Object zzpV;

        /* renamed from: com.google.android.gms.internal.zzeg.zze.1 */
        class C16241 implements com.google.android.gms.internal.zzji.zzc<zzed> {
            final /* synthetic */ zzd zzBt;
            final /* synthetic */ zze zzBu;

            C16241(zze com_google_android_gms_internal_zzeg_zze, zzd com_google_android_gms_internal_zzeg_zzd) {
                this.zzBu = com_google_android_gms_internal_zzeg_zze;
                this.zzBt = com_google_android_gms_internal_zzeg_zzd;
            }

            public void zza(zzed com_google_android_gms_internal_zzed) {
                zzin.m22v("Getting a new session for JS Engine.");
                this.zzBt.zzh(com_google_android_gms_internal_zzed.zzen());
            }

            public /* synthetic */ void zze(Object obj) {
                zza((zzed) obj);
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.zze.2 */
        class C16252 implements com.google.android.gms.internal.zzji.zza {
            final /* synthetic */ zzd zzBt;
            final /* synthetic */ zze zzBu;

            C16252(zze com_google_android_gms_internal_zzeg_zze, zzd com_google_android_gms_internal_zzeg_zzd) {
                this.zzBu = com_google_android_gms_internal_zzeg_zze;
                this.zzBt = com_google_android_gms_internal_zzeg_zzd;
            }

            public void run() {
                zzin.m22v("Rejecting reference for JS Engine.");
                this.zzBt.reject();
            }
        }

        /* renamed from: com.google.android.gms.internal.zzeg.zze.3 */
        class C16263 implements com.google.android.gms.internal.zzji.zzc<zzed> {
            final /* synthetic */ zze zzBu;

            /* renamed from: com.google.android.gms.internal.zzeg.zze.3.1 */
            class C02551 implements Runnable {
                final /* synthetic */ zzed zzBv;
                final /* synthetic */ C16263 zzBw;

                C02551(C16263 c16263, zzed com_google_android_gms_internal_zzed) {
                    this.zzBw = c16263;
                    this.zzBv = com_google_android_gms_internal_zzed;
                }

                public void run() {
                    this.zzBw.zzBu.zzBa.zze(this.zzBv);
                    this.zzBv.destroy();
                }
            }

            C16263(zze com_google_android_gms_internal_zzeg_zze) {
                this.zzBu = com_google_android_gms_internal_zzeg_zze;
            }

            public void zza(zzed com_google_android_gms_internal_zzed) {
                zzir.runOnUiThread(new C02551(this, com_google_android_gms_internal_zzed));
            }

            public /* synthetic */ void zze(Object obj) {
                zza((zzed) obj);
            }
        }

        public zze(zzb<zzed> com_google_android_gms_internal_zzeg_zzb_com_google_android_gms_internal_zzed) {
            this.zzpV = new Object();
            this.zzBa = com_google_android_gms_internal_zzeg_zzb_com_google_android_gms_internal_zzed;
            this.zzBr = false;
            this.zzBs = 0;
        }

        public zzd zzes() {
            zzd com_google_android_gms_internal_zzeg_zzd = new zzd(this);
            synchronized (this.zzpV) {
                zza(new C16241(this, com_google_android_gms_internal_zzeg_zzd), new C16252(this, com_google_android_gms_internal_zzeg_zzd));
                zzx.zzab(this.zzBs >= 0);
                this.zzBs++;
            }
            return com_google_android_gms_internal_zzeg_zzd;
        }

        protected void zzet() {
            boolean z = true;
            synchronized (this.zzpV) {
                if (this.zzBs < 1) {
                    z = false;
                }
                zzx.zzab(z);
                zzin.m22v("Releasing 1 reference for JS Engine");
                this.zzBs--;
                zzev();
            }
        }

        public void zzeu() {
            boolean z = true;
            synchronized (this.zzpV) {
                if (this.zzBs < 0) {
                    z = false;
                }
                zzx.zzab(z);
                zzin.m22v("Releasing root reference. JS Engine will be destroyed once other references are released.");
                this.zzBr = true;
                zzev();
            }
        }

        protected void zzev() {
            synchronized (this.zzpV) {
                zzx.zzab(this.zzBs >= 0);
                if (this.zzBr && this.zzBs == 0) {
                    zzin.m22v("No reference is left (including root). Cleaning up engine.");
                    zza(new C16263(this), new com.google.android.gms.internal.zzji.zzb());
                } else {
                    zzin.m22v("There are still references to the engine. Not destroying.");
                }
            }
        }
    }

    public zzeg(Context context, VersionInfoParcel versionInfoParcel, String str) {
        this.zzpV = new Object();
        this.zzBc = 1;
        this.zzAY = str;
        this.mContext = context.getApplicationContext();
        this.zzpT = versionInfoParcel;
        this.zzAZ = new zzc();
        this.zzBa = new zzc();
    }

    public zzeg(Context context, VersionInfoParcel versionInfoParcel, String str, zzb<zzed> com_google_android_gms_internal_zzeg_zzb_com_google_android_gms_internal_zzed, zzb<zzed> com_google_android_gms_internal_zzeg_zzb_com_google_android_gms_internal_zzed2) {
        this(context, versionInfoParcel, str);
        this.zzAZ = com_google_android_gms_internal_zzeg_zzb_com_google_android_gms_internal_zzed;
        this.zzBa = com_google_android_gms_internal_zzeg_zzb_com_google_android_gms_internal_zzed2;
    }

    private zze zzep() {
        zze com_google_android_gms_internal_zzeg_zze = new zze(this.zzBa);
        zzir.runOnUiThread(new C02541(this, com_google_android_gms_internal_zzeg_zze));
        return com_google_android_gms_internal_zzeg_zze;
    }

    protected zzed zza(Context context, VersionInfoParcel versionInfoParcel) {
        return new zzef(context, versionInfoParcel, null);
    }

    protected zze zzeq() {
        zze zzep = zzep();
        zzep.zza(new C16192(this, zzep), new C16203(this, zzep));
        return zzep;
    }

    public zzd zzer() {
        zzd zzes;
        synchronized (this.zzpV) {
            if (this.zzBb == null || this.zzBb.getStatus() == -1) {
                this.zzBc = 2;
                this.zzBb = zzeq();
                zzes = this.zzBb.zzes();
            } else if (this.zzBc == 0) {
                zzes = this.zzBb.zzes();
            } else if (this.zzBc == 1) {
                this.zzBc = 2;
                zzeq();
                zzes = this.zzBb.zzes();
            } else if (this.zzBc == 2) {
                zzes = this.zzBb.zzes();
            } else {
                zzes = this.zzBb.zzes();
            }
        }
        return zzes;
    }
}
