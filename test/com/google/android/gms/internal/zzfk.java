package com.google.android.gms.internal;

import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;
import com.google.ads.mediation.MediationServerParameters;
import com.google.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.internal.client.zzn;
import com.google.android.gms.ads.internal.util.client.zza;
import com.google.android.gms.ads.internal.util.client.zzb;

@zzhb
public final class zzfk<NETWORK_EXTRAS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> implements MediationBannerListener, MediationInterstitialListener {
    private final zzez zzCK;

    /* renamed from: com.google.android.gms.internal.zzfk.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ zzfk zzCQ;
        final /* synthetic */ ErrorCode zzCR;

        AnonymousClass10(zzfk com_google_android_gms_internal_zzfk, ErrorCode errorCode) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
            this.zzCR = errorCode;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdFailedToLoad(zzfl.zza(this.zzCR));
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdFailedToLoad.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.1 */
    class C02601 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02601(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdClicked();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdClicked.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.2 */
    class C02612 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02612(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdOpened();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdOpened.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.3 */
    class C02623 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02623(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdLoaded();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLoaded.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.4 */
    class C02634 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02634(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdClosed();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdClosed.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.5 */
    class C02645 implements Runnable {
        final /* synthetic */ zzfk zzCQ;
        final /* synthetic */ ErrorCode zzCR;

        C02645(zzfk com_google_android_gms_internal_zzfk, ErrorCode errorCode) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
            this.zzCR = errorCode;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdFailedToLoad(zzfl.zza(this.zzCR));
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdFailedToLoad.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.6 */
    class C02656 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02656(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdLeftApplication();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLeftApplication.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.7 */
    class C02667 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02667(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdOpened();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdOpened.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.8 */
    class C02678 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02678(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdLoaded();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLoaded.", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzfk.9 */
    class C02689 implements Runnable {
        final /* synthetic */ zzfk zzCQ;

        C02689(zzfk com_google_android_gms_internal_zzfk) {
            this.zzCQ = com_google_android_gms_internal_zzfk;
        }

        public void run() {
            try {
                this.zzCQ.zzCK.onAdClosed();
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdClosed.", e);
            }
        }
    }

    public zzfk(zzez com_google_android_gms_internal_zzez) {
        this.zzCK = com_google_android_gms_internal_zzez;
    }

    public void onClick(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        zzb.zzaI("Adapter called onClick.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdClicked();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdClicked.", e);
                return;
            }
        }
        zzb.zzaK("onClick must be called on the main UI thread.");
        zza.zzMS.post(new C02601(this));
    }

    public void onDismissScreen(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        zzb.zzaI("Adapter called onDismissScreen.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdClosed();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdClosed.", e);
                return;
            }
        }
        zzb.zzaK("onDismissScreen must be called on the main UI thread.");
        zza.zzMS.post(new C02634(this));
    }

    public void onDismissScreen(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        zzb.zzaI("Adapter called onDismissScreen.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdClosed();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdClosed.", e);
                return;
            }
        }
        zzb.zzaK("onDismissScreen must be called on the main UI thread.");
        zza.zzMS.post(new C02689(this));
    }

    public void onFailedToReceiveAd(MediationBannerAdapter<?, ?> mediationBannerAdapter, ErrorCode errorCode) {
        zzb.zzaI("Adapter called onFailedToReceiveAd with error. " + errorCode);
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdFailedToLoad(zzfl.zza(errorCode));
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdFailedToLoad.", e);
                return;
            }
        }
        zzb.zzaK("onFailedToReceiveAd must be called on the main UI thread.");
        zza.zzMS.post(new C02645(this, errorCode));
    }

    public void onFailedToReceiveAd(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter, ErrorCode errorCode) {
        zzb.zzaI("Adapter called onFailedToReceiveAd with error " + errorCode + ".");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdFailedToLoad(zzfl.zza(errorCode));
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdFailedToLoad.", e);
                return;
            }
        }
        zzb.zzaK("onFailedToReceiveAd must be called on the main UI thread.");
        zza.zzMS.post(new AnonymousClass10(this, errorCode));
    }

    public void onLeaveApplication(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        zzb.zzaI("Adapter called onLeaveApplication.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdLeftApplication();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLeftApplication.", e);
                return;
            }
        }
        zzb.zzaK("onLeaveApplication must be called on the main UI thread.");
        zza.zzMS.post(new C02656(this));
    }

    public void onLeaveApplication(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        zzb.zzaI("Adapter called onLeaveApplication.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdLeftApplication();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLeftApplication.", e);
                return;
            }
        }
        zzb.zzaK("onLeaveApplication must be called on the main UI thread.");
        zza.zzMS.post(new Runnable() {
            final /* synthetic */ zzfk zzCQ;

            {
                this.zzCQ = r1;
            }

            public void run() {
                try {
                    this.zzCQ.zzCK.onAdLeftApplication();
                } catch (Throwable e) {
                    zzb.zzd("Could not call onAdLeftApplication.", e);
                }
            }
        });
    }

    public void onPresentScreen(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        zzb.zzaI("Adapter called onPresentScreen.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdOpened();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdOpened.", e);
                return;
            }
        }
        zzb.zzaK("onPresentScreen must be called on the main UI thread.");
        zza.zzMS.post(new C02667(this));
    }

    public void onPresentScreen(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        zzb.zzaI("Adapter called onPresentScreen.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdOpened();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdOpened.", e);
                return;
            }
        }
        zzb.zzaK("onPresentScreen must be called on the main UI thread.");
        zza.zzMS.post(new C02612(this));
    }

    public void onReceivedAd(MediationBannerAdapter<?, ?> mediationBannerAdapter) {
        zzb.zzaI("Adapter called onReceivedAd.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdLoaded();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLoaded.", e);
                return;
            }
        }
        zzb.zzaK("onReceivedAd must be called on the main UI thread.");
        zza.zzMS.post(new C02678(this));
    }

    public void onReceivedAd(MediationInterstitialAdapter<?, ?> mediationInterstitialAdapter) {
        zzb.zzaI("Adapter called onReceivedAd.");
        if (zzn.zzcS().zzhJ()) {
            try {
                this.zzCK.onAdLoaded();
                return;
            } catch (Throwable e) {
                zzb.zzd("Could not call onAdLoaded.", e);
                return;
            }
        }
        zzb.zzaK("onReceivedAd must be called on the main UI thread.");
        zza.zzMS.post(new C02623(this));
    }
}
