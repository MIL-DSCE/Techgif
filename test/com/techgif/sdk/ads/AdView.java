package com.appsgeyser.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.appsgeyser.sdk.admobutils.AdMobParameters;
import com.appsgeyser.sdk.admobutils.ParameterizedRequest;
import com.appsgeyser.sdk.ads.behavior.BehaviorAcceptor;
import com.appsgeyser.sdk.ads.behavior.BehaviorVisitor;
import com.appsgeyser.sdk.ads.behavior.bannerBehaviors.AdViewBehavior;
import com.appsgeyser.sdk.deviceidparser.DeviceIdParameters;
import com.appsgeyser.sdk.deviceidparser.DeviceIdParser;
import com.appsgeyser.sdk.deviceidparser.IDeviceIdParserListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class AdView extends RelativeLayout implements BehaviorAcceptor {
    static final int AD_MOB_VIEW_ID = 4660;
    private static final int DEFAULT_HEIGHT = 50;
    private static final int DEFAULT_WIDTH = 320;
    static final float UNSET_DENSITY = -1.0f;
    private Activity _activity;
    private boolean _adMobNow;
    private com.google.android.gms.ads.AdView _admobView;
    private AdsLoader _adsLoader;
    private WebView _browser;
    private AdsBannerWebViewClient _browserClient;
    private Context _context;
    private float _density;
    private DisplayMetrics _displayMetrics;
    private boolean _fisrt;
    private AdsHeaderReceiver _headerReceiver;
    private int _height;
    private int _width;

    /* renamed from: com.appsgeyser.sdk.ads.AdView.2 */
    class C01332 extends WebChromeClient {
        C01332() {
        }

        public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.AdView.1 */
    class C15141 implements IDeviceIdParserListener {
        C15141() {
        }

        public void onDeviceIdParametersObtained(DeviceIdParameters result) {
            AdView.this._initWithDeviceIdParameters(result);
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.AdView.3 */
    class C15153 extends AdListener {
        C15153() {
        }

        public void onAdLoaded() {
            AdView.this._admobView.setVisibility(0);
        }

        public void onAdFailedToLoad(int errorCode) {
        }

        public void onAdOpened() {
        }

        public void onAdClosed() {
        }

        public void onAdLeftApplication() {
        }
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._width = DEFAULT_WIDTH;
        this._height = DEFAULT_HEIGHT;
        this._density = UNSET_DENSITY;
        this._fisrt = true;
        this._adMobNow = false;
        this._context = context;
        _init();
    }

    private void _init() {
        this._activity = (Activity) getContext();
        setVisibility(8);
        if (getContext().checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0 && getContext().checkCallingOrSelfPermission("android.permission.INTERNET") == 0) {
            DeviceIdParser parser = DeviceIdParser.getInstance();
            if (parser.isEmty()) {
                parser.rescan(getContext(), new C15141());
                return;
            } else {
                _initWithDeviceIdParameters(parser.getDeviceIdParameters());
                return;
            }
        }
        Log.e("Appsgeyser SDK", "You have to grant ACCESS_NETWORK_STATE and INTERNET permissions to work properly");
    }

    private void _initWithDeviceIdParameters(DeviceIdParameters deviceIdParameters) {
        this._browser = new WebView(getContext());
        addView(this._browser, new LayoutParams(-1, -1));
        this._adsLoader = new AdsLoader();
        this._adsLoader.init(this, deviceIdParameters);
        this._headerReceiver = new AdsHeaderReceiver(this, this._adsLoader);
        this._adsLoader.setAdsLoadingFinishedListener(this._headerReceiver);
        this._adsLoader.setHeaderReceiver(this._headerReceiver);
        this._browser.addJavascriptInterface(new BannerJavascriptInterface(this, this._adsLoader), BannerJavascriptInterface.JS_INTERFACE_NAME);
        this._browserClient = new AdsBannerWebViewClient();
        this._browserClient.setOnPageFinishedListener(this._adsLoader);
        this._browserClient.setOnPageStartedListener(this._adsLoader);
        this._browser.setWebChromeClient(new C01332());
        this._browser.setWebViewClient(this._browserClient);
        this._displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getMetrics(this._displayMetrics);
        applyDefaultSettings();
        this._adsLoader.reload();
    }

    public void applyDefaultSettings() {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        WebSettings settings = this._browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setGeolocationEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        Context ctx = getContext();
        String appCachePath = ctx.getDir("appcache", 0).getPath();
        String databasePath = ctx.getDir("databases", 0).getPath();
        String geolocationDatabasePath = ctx.getDir("geolocation", 0).getPath();
        settings.setAppCachePath(appCachePath);
        settings.setDatabasePath(databasePath);
        settings.setGeolocationDatabasePath(geolocationDatabasePath);
        _applySize();
    }

    public void setWidth(int width) {
        if (width > 0) {
            this._width = width;
            _applySize();
        }
    }

    public void setHeight(int height) {
        if (height > 0) {
            this._height = height;
            _applySize();
        }
    }

    public WebView getBrowser() {
        return this._browser;
    }

    protected void _applySize() {
        if (this._density == UNSET_DENSITY && this._context != null) {
            this._density = this._context.getResources().getDisplayMetrics().density;
        }
        if (this._density <= 0.0f) {
            this._density = TouchHelperCallback.ALPHA_FULL;
        }
        this._width = (int) ((this._density * ((float) this._width)) + 0.5f);
        this._height = (int) ((this._density * ((float) this._height)) + 0.5f);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            return;
        }
        if (this._displayMetrics.widthPixels < this._width + 6) {
            params.width = this._displayMetrics.widthPixels;
            params.height = (int) (((((float) (this._displayMetrics.widthPixels - 6)) / ((float) this._width)) * ((float) this._height)) + 6.0f);
            return;
        }
        params.width = this._width + 6;
        params.height = this._height + 6;
    }

    public void acceptBehavior(BehaviorVisitor visitor) {
        if (visitor instanceof AdViewBehavior) {
            ((AdViewBehavior) visitor).visit(this);
        }
    }

    public void hide() {
        setVisibility(8);
        this._browser.setWebViewClient(null);
    }

    public void show() {
        setVisibility(0);
        this._browser.setWebViewClient(this._browserClient);
    }

    public void switchToAdMobAd(AdMobParameters adMobParameters) {
        this._adMobNow = true;
        this._browser.setVisibility(8);
        _removeAdMobBanner();
        _createAdMobBanner();
        this._admobView.setAdUnitId(adMobParameters.getPublisherId());
        this._admobView.setAdSize(AdSize.BANNER);
        this._admobView.setAdListener(new C15153());
        this._admobView.loadAd(new ParameterizedRequest(adMobParameters).getRequest());
    }

    private void _createAdMobBanner() {
        _removeAdMobBanner();
        this._admobView = new com.google.android.gms.ads.AdView(this._activity);
        this._admobView.setVisibility(8);
        addView(this._admobView, new LayoutParams(-1, -1));
    }

    private void _removeAdMobBanner() {
        removeView(this._admobView);
    }

    public void switchToHtmlBanner() {
        this._adMobNow = false;
        _removeAdMobBanner();
        this._browser.setVisibility(0);
    }

    public void switchToAdMobBanner() {
        this._browser.setVisibility(8);
        if (this._admobView != null) {
            this._admobView.setVisibility(0);
        }
    }

    public boolean isAdMobNow() {
        return this._adMobNow;
    }
}
