package com.appsgeyser.sdk.ads;

import android.content.Context;
import android.os.Handler;
import com.appsgeyser.sdk.AppsgeyserSDKInternal;
import com.appsgeyser.sdk.admobutils.AdMobParameters;
import com.appsgeyser.sdk.admobutils.ParameterizedRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

public class AdMobFSBannerController extends AdListener {
    private static final long LOADING_TIMEOUT = 5000;
    private Context _context;
    private boolean _error;
    private Handler _handler;
    private InterstitialAd _interstitial;
    private IFullScreenBannerListener _listener;
    private Runnable _timeoutExpiredRunnable;

    /* renamed from: com.appsgeyser.sdk.ads.AdMobFSBannerController.1 */
    class C01311 implements Runnable {
        C01311() {
        }

        public void run() {
            AdMobFSBannerController.this._error = true;
            if (AdMobFSBannerController.this._listener != null) {
                AdMobFSBannerController.this._listener.onAdFailedToLoad();
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.AdMobFSBannerController.2 */
    class C01322 implements Runnable {
        private final /* synthetic */ String val$_adUnitID;
        private final /* synthetic */ String val$birthday;
        private final /* synthetic */ String val$genderString;
        private final /* synthetic */ String val$keywords;
        private final /* synthetic */ String val$latitude;
        private final /* synthetic */ String val$longtitude;

        C01322(String str, String str2, String str3, String str4, String str5, String str6) {
            this.val$_adUnitID = str;
            this.val$keywords = str2;
            this.val$genderString = str3;
            this.val$birthday = str4;
            this.val$latitude = str5;
            this.val$longtitude = str6;
        }

        public void run() {
            AdMobFSBannerController.this._error = false;
            String adUnitID = this.val$_adUnitID;
            if (adUnitID == null) {
                adUnitID = TtmlNode.ANONYMOUS_REGION_ID;
            }
            AdMobFSBannerController.this._interstitial = new InterstitialAd(AdMobFSBannerController.this._context);
            AdMobFSBannerController.this._interstitial.setAdUnitId(adUnitID);
            AdMobFSBannerController.this._interstitial.setAdListener(AdMobFSBannerController.this);
            AdMobFSBannerController.this._interstitial.loadAd(new ParameterizedRequest(new AdMobParameters(adUnitID, this.val$keywords, this.val$genderString, this.val$birthday, this.val$latitude, this.val$longtitude)).getRequest());
            AdMobFSBannerController.this._handler.postDelayed(AdMobFSBannerController.this._timeoutExpiredRunnable, AdMobFSBannerController.LOADING_TIMEOUT);
        }
    }

    AdMobFSBannerController(Context context) {
        this._interstitial = null;
        this._error = false;
        this._listener = null;
        this._handler = new Handler();
        this._timeoutExpiredRunnable = new C01311();
        this._context = context;
    }

    public void load(String _adUnitID, String keywords, String genderString, String birthday, String latitude, String longtitude) {
        AppsgeyserSDKInternal.runOnMainThread(new C01322(_adUnitID, keywords, genderString, birthday, latitude, longtitude));
    }

    public void showBanner() {
        if (this._interstitial.isLoaded() && !this._error) {
            this._interstitial.show();
        }
    }

    public void onAdClosed() {
    }

    public void onAdFailedToLoad(int errorCode) {
        if (!this._error) {
            this._error = true;
            if (this._listener != null) {
                this._listener.onAdFailedToLoad();
            }
            this._handler.removeCallbacksAndMessages(null);
        }
    }

    public void onAdLeftApplication() {
    }

    public void onAdOpened() {
    }

    public void onAdLoaded() {
        if (!(!this._interstitial.isLoaded() || this._error || this._listener == null)) {
            this._listener.onLoadFinished();
        }
        this._handler.removeCallbacksAndMessages(null);
    }

    protected void setListener(IFullScreenBannerListener listener) {
        this._listener = listener;
    }
}
