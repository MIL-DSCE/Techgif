package com.appsgeyser.sdk.ads;

import android.content.Context;
import android.webkit.JavascriptInterface;
import com.appsgeyser.sdk.server.StatController;
import com.appsgeyser.sdk.utils.WebViewScreenShooter;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class FullscreenBannerJsInterface extends BaseSecureJsInterface {
    public static String JS_INTERFACE_NAME;
    private FullScreenBanner _fsBanner;

    static {
        JS_INTERFACE_NAME = "AppsgeyserBanner";
    }

    public FullscreenBannerJsInterface(FullScreenBanner fullScreenBanner, Context context) {
        this._fsBanner = null;
        this._fsBanner = fullScreenBanner;
    }

    @JavascriptInterface
    public void stayAlive() {
        this._fsBanner.stayAlive();
    }

    @JavascriptInterface
    public void showAdMobFullScreenBanner(String adUnitID, String keywords, String genderString, String birthday, String latitude, String longtitude) {
        this._fsBanner.showAdMobFSBanner(adUnitID, keywords, genderString, birthday, latitude, longtitude);
    }

    @JavascriptInterface
    public void dismissAdMobOnTimeout(String receivedTimeout) {
    }

    @JavascriptInterface
    public void close() {
        this._fsBanner.close();
    }

    @JavascriptInterface
    public void setClickUrl(String clickUrl, String hashCode) {
        if (_checkSecurityCode(hashCode)) {
            this._fsBanner.setClickUrl(clickUrl);
        }
    }

    @JavascriptInterface
    public String takeScreenShot() {
        return WebViewScreenShooter.takeScreenShotInBase64(this._fsBanner.getWebView());
    }

    @JavascriptInterface
    public void setStatUrls(String jsonParameters) {
        try {
            JSONObject json = new JSONObject(jsonParameters);
            HashMap<String, String> result = new HashMap();
            Iterator<?> iterator = json.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                result.put(key, json.get(key).toString());
            }
            StatController.getInstance().init(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void forceOpenInNativeBrowser(boolean openInNativeBrowser) {
        this._fsBanner.forceOpenInNativeBrowser(openInNativeBrowser);
    }

    @JavascriptInterface
    public void setBackKeyLocked(boolean locked) {
        this._fsBanner.setBackKeyLocked(locked);
    }

    @JavascriptInterface
    public void trackCrossClick() {
        StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_CROSS_BANNER);
    }

    @JavascriptInterface
    public void trackBannerClick() {
        StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_HTML_TAP_START);
    }

    @JavascriptInterface
    public void trackTimerClick() {
        StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_TIMER_BANNER);
    }

    @JavascriptInterface
    public void showTimer(int seconds) {
        this._fsBanner.setShowTimer((long) (seconds * 1000));
    }
}
