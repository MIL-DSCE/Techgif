package com.google.android.gms.internal;

import android.os.Handler;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.ads.internal.zzk;
import com.google.android.gms.ads.internal.zzr;
import java.util.LinkedList;
import java.util.List;

@zzhb
class zzdw {
    private final List<zza> zzpH;

    /* renamed from: com.google.android.gms.internal.zzdw.7 */
    class C02427 implements Runnable {
        final /* synthetic */ zzdw zzAc;
        final /* synthetic */ zza zzAo;
        final /* synthetic */ zzdx zzAp;

        C02427(zzdw com_google_android_gms_internal_zzdw, zza com_google_android_gms_internal_zzdw_zza, zzdx com_google_android_gms_internal_zzdx) {
            this.zzAc = com_google_android_gms_internal_zzdw;
            this.zzAo = com_google_android_gms_internal_zzdw_zza;
            this.zzAp = com_google_android_gms_internal_zzdx;
        }

        public void run() {
            try {
                this.zzAo.zzb(this.zzAp);
            } catch (Throwable e) {
                zzb.zzd("Could not propagate interstitial ad event.", e);
            }
        }
    }

    interface zza {
        void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException;
    }

    /* renamed from: com.google.android.gms.internal.zzdw.1 */
    class C19681 extends com.google.android.gms.ads.internal.client.zzq.zza {
        final /* synthetic */ zzdw zzAc;

        /* renamed from: com.google.android.gms.internal.zzdw.1.1 */
        class C15981 implements zza {
            final /* synthetic */ C19681 zzAd;

            C15981(C19681 c19681) {
                this.zzAd = c19681;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzpK != null) {
                    com_google_android_gms_internal_zzdx.zzpK.onAdClosed();
                }
                zzr.zzbN().zzee();
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.1.2 */
        class C15992 implements zza {
            final /* synthetic */ C19681 zzAd;
            final /* synthetic */ int zzAe;

            C15992(C19681 c19681, int i) {
                this.zzAd = c19681;
                this.zzAe = i;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzpK != null) {
                    com_google_android_gms_internal_zzdx.zzpK.onAdFailedToLoad(this.zzAe);
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.1.3 */
        class C16003 implements zza {
            final /* synthetic */ C19681 zzAd;

            C16003(C19681 c19681) {
                this.zzAd = c19681;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzpK != null) {
                    com_google_android_gms_internal_zzdx.zzpK.onAdLeftApplication();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.1.4 */
        class C16014 implements zza {
            final /* synthetic */ C19681 zzAd;

            C16014(C19681 c19681) {
                this.zzAd = c19681;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzpK != null) {
                    com_google_android_gms_internal_zzdx.zzpK.onAdLoaded();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.1.5 */
        class C16025 implements zza {
            final /* synthetic */ C19681 zzAd;

            C16025(C19681 c19681) {
                this.zzAd = c19681;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzpK != null) {
                    com_google_android_gms_internal_zzdx.zzpK.onAdOpened();
                }
            }
        }

        C19681(zzdw com_google_android_gms_internal_zzdw) {
            this.zzAc = com_google_android_gms_internal_zzdw;
        }

        public void onAdClosed() throws RemoteException {
            this.zzAc.zzpH.add(new C15981(this));
        }

        public void onAdFailedToLoad(int errorCode) throws RemoteException {
            this.zzAc.zzpH.add(new C15992(this, errorCode));
            zzin.m22v("Pooled interstitial failed to load.");
        }

        public void onAdLeftApplication() throws RemoteException {
            this.zzAc.zzpH.add(new C16003(this));
        }

        public void onAdLoaded() throws RemoteException {
            this.zzAc.zzpH.add(new C16014(this));
            zzin.m22v("Pooled interstitial loaded.");
        }

        public void onAdOpened() throws RemoteException {
            this.zzAc.zzpH.add(new C16025(this));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzdw.2 */
    class C19692 extends com.google.android.gms.ads.internal.client.zzw.zza {
        final /* synthetic */ zzdw zzAc;

        /* renamed from: com.google.android.gms.internal.zzdw.2.1 */
        class C16031 implements zza {
            final /* synthetic */ String val$name;
            final /* synthetic */ String zzAf;
            final /* synthetic */ C19692 zzAg;

            C16031(C19692 c19692, String str, String str2) {
                this.zzAg = c19692;
                this.val$name = str;
                this.zzAf = str2;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAq != null) {
                    com_google_android_gms_internal_zzdx.zzAq.onAppEvent(this.val$name, this.zzAf);
                }
            }
        }

        C19692(zzdw com_google_android_gms_internal_zzdw) {
            this.zzAc = com_google_android_gms_internal_zzdw;
        }

        public void onAppEvent(String name, String info) throws RemoteException {
            this.zzAc.zzpH.add(new C16031(this, name, info));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzdw.3 */
    class C19703 extends com.google.android.gms.internal.zzgd.zza {
        final /* synthetic */ zzdw zzAc;

        /* renamed from: com.google.android.gms.internal.zzdw.3.1 */
        class C16041 implements zza {
            final /* synthetic */ zzgc zzAh;
            final /* synthetic */ C19703 zzAi;

            C16041(C19703 c19703, zzgc com_google_android_gms_internal_zzgc) {
                this.zzAi = c19703;
                this.zzAh = com_google_android_gms_internal_zzgc;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAr != null) {
                    com_google_android_gms_internal_zzdx.zzAr.zza(this.zzAh);
                }
            }
        }

        C19703(zzdw com_google_android_gms_internal_zzdw) {
            this.zzAc = com_google_android_gms_internal_zzdw;
        }

        public void zza(zzgc com_google_android_gms_internal_zzgc) throws RemoteException {
            this.zzAc.zzpH.add(new C16041(this, com_google_android_gms_internal_zzgc));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzdw.4 */
    class C19714 extends com.google.android.gms.internal.zzcf.zza {
        final /* synthetic */ zzdw zzAc;

        /* renamed from: com.google.android.gms.internal.zzdw.4.1 */
        class C16051 implements zza {
            final /* synthetic */ zzce zzAj;
            final /* synthetic */ C19714 zzAk;

            C16051(C19714 c19714, zzce com_google_android_gms_internal_zzce) {
                this.zzAk = c19714;
                this.zzAj = com_google_android_gms_internal_zzce;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAs != null) {
                    com_google_android_gms_internal_zzdx.zzAs.zza(this.zzAj);
                }
            }
        }

        C19714(zzdw com_google_android_gms_internal_zzdw) {
            this.zzAc = com_google_android_gms_internal_zzdw;
        }

        public void zza(zzce com_google_android_gms_internal_zzce) throws RemoteException {
            this.zzAc.zzpH.add(new C16051(this, com_google_android_gms_internal_zzce));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzdw.5 */
    class C19725 extends com.google.android.gms.ads.internal.client.zzp.zza {
        final /* synthetic */ zzdw zzAc;

        /* renamed from: com.google.android.gms.internal.zzdw.5.1 */
        class C16061 implements zza {
            final /* synthetic */ C19725 zzAl;

            C16061(C19725 c19725) {
                this.zzAl = c19725;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAt != null) {
                    com_google_android_gms_internal_zzdx.zzAt.onAdClicked();
                }
            }
        }

        C19725(zzdw com_google_android_gms_internal_zzdw) {
            this.zzAc = com_google_android_gms_internal_zzdw;
        }

        public void onAdClicked() throws RemoteException {
            this.zzAc.zzpH.add(new C16061(this));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzdw.6 */
    class C19736 extends com.google.android.gms.ads.internal.reward.client.zzd.zza {
        final /* synthetic */ zzdw zzAc;

        /* renamed from: com.google.android.gms.internal.zzdw.6.1 */
        class C16071 implements zza {
            final /* synthetic */ C19736 zzAm;

            C16071(C19736 c19736) {
                this.zzAm = c19736;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.onRewardedVideoAdLoaded();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.6.2 */
        class C16082 implements zza {
            final /* synthetic */ C19736 zzAm;

            C16082(C19736 c19736) {
                this.zzAm = c19736;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.onRewardedVideoAdOpened();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.6.3 */
        class C16093 implements zza {
            final /* synthetic */ C19736 zzAm;

            C16093(C19736 c19736) {
                this.zzAm = c19736;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.onRewardedVideoStarted();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.6.4 */
        class C16104 implements zza {
            final /* synthetic */ C19736 zzAm;

            C16104(C19736 c19736) {
                this.zzAm = c19736;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.onRewardedVideoAdClosed();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.6.5 */
        class C16115 implements zza {
            final /* synthetic */ C19736 zzAm;
            final /* synthetic */ com.google.android.gms.ads.internal.reward.client.zza zzAn;

            C16115(C19736 c19736, com.google.android.gms.ads.internal.reward.client.zza com_google_android_gms_ads_internal_reward_client_zza) {
                this.zzAm = c19736;
                this.zzAn = com_google_android_gms_ads_internal_reward_client_zza;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.zza(this.zzAn);
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.6.6 */
        class C16126 implements zza {
            final /* synthetic */ C19736 zzAm;

            C16126(C19736 c19736) {
                this.zzAm = c19736;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.onRewardedVideoAdLeftApplication();
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzdw.6.7 */
        class C16137 implements zza {
            final /* synthetic */ int zzAe;
            final /* synthetic */ C19736 zzAm;

            C16137(C19736 c19736, int i) {
                this.zzAm = c19736;
                this.zzAe = i;
            }

            public void zzb(zzdx com_google_android_gms_internal_zzdx) throws RemoteException {
                if (com_google_android_gms_internal_zzdx.zzAu != null) {
                    com_google_android_gms_internal_zzdx.zzAu.onRewardedVideoAdFailedToLoad(this.zzAe);
                }
            }
        }

        C19736(zzdw com_google_android_gms_internal_zzdw) {
            this.zzAc = com_google_android_gms_internal_zzdw;
        }

        public void onRewardedVideoAdClosed() throws RemoteException {
            this.zzAc.zzpH.add(new C16104(this));
        }

        public void onRewardedVideoAdFailedToLoad(int errorCode) throws RemoteException {
            this.zzAc.zzpH.add(new C16137(this, errorCode));
        }

        public void onRewardedVideoAdLeftApplication() throws RemoteException {
            this.zzAc.zzpH.add(new C16126(this));
        }

        public void onRewardedVideoAdLoaded() throws RemoteException {
            this.zzAc.zzpH.add(new C16071(this));
        }

        public void onRewardedVideoAdOpened() throws RemoteException {
            this.zzAc.zzpH.add(new C16082(this));
        }

        public void onRewardedVideoStarted() throws RemoteException {
            this.zzAc.zzpH.add(new C16093(this));
        }

        public void zza(com.google.android.gms.ads.internal.reward.client.zza com_google_android_gms_ads_internal_reward_client_zza) throws RemoteException {
            this.zzAc.zzpH.add(new C16115(this, com_google_android_gms_ads_internal_reward_client_zza));
        }
    }

    zzdw() {
        this.zzpH = new LinkedList();
    }

    void zza(zzdx com_google_android_gms_internal_zzdx) {
        Handler handler = zzir.zzMc;
        for (zza c02427 : this.zzpH) {
            handler.post(new C02427(this, c02427, com_google_android_gms_internal_zzdx));
        }
    }

    void zzc(zzk com_google_android_gms_ads_internal_zzk) {
        com_google_android_gms_ads_internal_zzk.zza(new C19681(this));
        com_google_android_gms_ads_internal_zzk.zza(new C19692(this));
        com_google_android_gms_ads_internal_zzk.zza(new C19703(this));
        com_google_android_gms_ads_internal_zzk.zza(new C19714(this));
        com_google_android_gms_ads_internal_zzk.zza(new C19725(this));
        com_google_android_gms_ads_internal_zzk.zza(new C19736(this));
    }
}
