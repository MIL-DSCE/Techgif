package com.appsgeyser.sdk.ads;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import com.appsgeyser.sdk.StringUtils;
import com.appsgeyser.sdk.admobutils.AdMobParameters;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.device.Device;
import com.appsgeyser.sdk.hasher.Hasher;

public class BannerJavascriptInterface {
    public static String JS_INTERFACE_NAME;
    private AdView _adView;
    private AdsLoader _adsLoader;
    private String _androidId;

    /* renamed from: com.appsgeyser.sdk.ads.BannerJavascriptInterface.1 */
    class C01391 implements Runnable {
        C01391() {
        }

        public void run() {
            try {
                BannerJavascriptInterface.this._androidId = Device.getAndroidId(BannerJavascriptInterface.this._adView.getContext());
            } catch (Exception e) {
                BannerJavascriptInterface.this._androidId = null;
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.BannerJavascriptInterface.2 */
    class C01402 implements Runnable {
        C01402() {
        }

        public void run() {
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.BannerJavascriptInterface.3 */
    class C01413 implements Runnable {
        private final /* synthetic */ String val$hash;
        private final /* synthetic */ String val$url;

        C01413(String str, String str2) {
            this.val$hash = str;
            this.val$url = str2;
        }

        public void run() {
            if (BannerJavascriptInterface.this._checkSecurityCode(this.val$hash)) {
                BannerJavascriptInterface.this._adsLoader.setClickUrl(this.val$url);
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.BannerJavascriptInterface.4 */
    class C01424 implements Runnable {
        private final /* synthetic */ String val$hash;

        C01424(String str) {
            this.val$hash = str;
        }

        public void run() {
            if (BannerJavascriptInterface.this._checkSecurityCode(this.val$hash)) {
                BannerJavascriptInterface.this._adsLoader.reload();
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.BannerJavascriptInterface.5 */
    class C01435 implements Runnable {
        private final /* synthetic */ String val$birthday;
        private final /* synthetic */ String val$genderString;
        private final /* synthetic */ String val$keywords;
        private final /* synthetic */ String val$latitude;
        private final /* synthetic */ String val$longtitude;
        private final /* synthetic */ String val$publisherId;

        C01435(String str, String str2, String str3, String str4, String str5, String str6) {
            this.val$publisherId = str;
            this.val$keywords = str2;
            this.val$genderString = str3;
            this.val$birthday = str4;
            this.val$latitude = str5;
            this.val$longtitude = str6;
        }

        public void run() {
            BannerJavascriptInterface.this._adView.switchToAdMobAd(new AdMobParameters(this.val$publisherId, this.val$keywords, this.val$genderString, this.val$birthday, this.val$latitude, this.val$longtitude));
        }
    }

    static {
        JS_INTERFACE_NAME = "AppsgeyserBanner";
    }

    public BannerJavascriptInterface(AdView adView, AdsLoader loader) {
        this._adView = adView;
        this._adsLoader = loader;
        this._adView.post(new C01391());
    }

    @JavascriptInterface
    public void close() {
        this._adView.post(new C01402());
    }

    @JavascriptInterface
    public void setClickUrl(String url, String hash) {
        this._adView.post(new C01413(hash, url));
    }

    @JavascriptInterface
    public String getAndroidId(String hash) {
        if (_checkSecurityCode(hash)) {
            return this._androidId;
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    @JavascriptInterface
    public void reload(String hash) {
        this._adView.post(new C01424(hash));
    }

    @JavascriptInterface
    protected boolean _checkSecurityCode(String hashCode) {
        Configuration config = Configuration.getInstance();
        String appId = config.getApplicationId();
        String guid = config.getAppGuid();
        if (StringUtils.isNotNullOrEmptyString(appId) && StringUtils.isNotNullOrEmptyString(guid)) {
            return hashCode.equalsIgnoreCase(Hasher.md5(new StringBuilder(String.valueOf(guid)).append(appId).toString()));
        }
        return false;
    }

    @JavascriptInterface
    public void forceOpenInNativeBrowser(boolean openInNativeBrowser) {
        this._adsLoader.forceOpenInNativeBrowser(openInNativeBrowser);
    }

    @JavascriptInterface
    public void showAdMobAd(String publisherId, String keywords, String genderString, String birthday, String latitude, String longtitude) {
        ((Activity) this._adView.getContext()).runOnUiThread(new C01435(publisherId, keywords, genderString, birthday, latitude, longtitude));
    }
}
