package com.appsgeyser.sdk.ads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.appsgeyser.sdk.AdActivity;
import com.appsgeyser.sdk.BrowserActivity;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.deviceidparser.DeviceIdParameters;
import com.appsgeyser.sdk.deviceidparser.DeviceIdParser;
import com.appsgeyser.sdk.deviceidparser.IDeviceIdParserListener;
import com.appsgeyser.sdk.location.Geolocation;
import com.appsgeyser.sdk.server.StatController;
import com.appsgeyser.sdk.server.implementation.AppsgeyserServerClient;
import java.util.HashMap;
import net.hockeyapp.android.UpdateFragment;

public class FullScreenBanner {
    private static final long LOADING_TIMEOUT = 5000;
    private static final long NO_TIMER = -1;
    public static final String ONE = "one";
    private static final String SPLASH_SERVER_DOMAIN = "http://splash.appsgeyser.com/";
    private static final String TAG;
    private static volatile FullScreenBanner _instance;
    private static volatile boolean inLoadingProcess;
    private AdMobFSBannerController _adMobFSBannerController;
    private boolean _backKeyLocked;
    String _bannerUrl;
    WebView _bannerView;
    private String _clickUrl;
    private Runnable _closeRunnable;
    Context _context;
    private BannerTypes _currentBannerType;
    boolean _errorHappened;
    boolean _errorOnTimeout;
    private Handler _handler;
    private boolean _keepAliveCalled;
    IFullScreenBannerListener _listener;
    private boolean _openInNativeBrowser;
    boolean _ready;
    private boolean _redirect;
    private long _timerDuration;

    /* renamed from: com.appsgeyser.sdk.ads.FullScreenBanner.1 */
    class C01441 implements Runnable {
        C01441() {
        }

        public void run() {
            if (!FullScreenBanner.this._keepAliveCalled && !FullScreenBanner.this._errorHappened) {
                FullScreenBanner.getInstance(FullScreenBanner.this._context).close();
                FullScreenBanner.this._errorHappened = true;
                if (FullScreenBanner.this._listener != null) {
                    FullScreenBanner.this._listener.onAdFailedToLoad();
                    FullScreenBanner.inLoadingProcess = false;
                }
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.FullScreenBanner.3 */
    class C01453 extends WebViewClient {
        C01453() {
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            _handleRedirect(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            if (!(FullScreenBanner.this._errorOnTimeout && FullScreenBanner.this._keepAliveCalled) && FullScreenBanner.this._errorOnTimeout) {
                FullScreenBanner.this._errorHappened = true;
                if (FullScreenBanner.this._listener != null) {
                    FullScreenBanner.this._listener.onAdFailedToLoad();
                }
            } else if (!FullScreenBanner.this._errorHappened) {
                FullScreenBanner.this._ready = true;
                if (!FullScreenBanner.this._currentBannerType.equals(BannerTypes.ADMOB)) {
                    FullScreenBanner.this._currentBannerType = BannerTypes.HTML;
                    if (FullScreenBanner.this._listener != null) {
                        FullScreenBanner.inLoadingProcess = false;
                        FullScreenBanner.this._listener.onLoadFinished();
                    }
                }
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return _handleRedirect(view, url);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            FullScreenBanner.this._errorHappened = true;
            FullScreenBanner.this._handler.removeCallbacksAndMessages(null);
            if (FullScreenBanner.this._listener != null) {
                FullScreenBanner.this._listener.onAdFailedToLoad();
            }
        }

        private boolean _handleRedirect(WebView view, String url) {
            if (url.equalsIgnoreCase(FullScreenBanner.this._bannerUrl) || FullScreenBanner.this._redirect) {
                return false;
            }
            Intent intent;
            view.stopLoading();
            HashMap<String, String> details = new HashMap();
            details.put(UpdateFragment.FRAGMENT_URL, url);
            if (FullScreenBanner.this._openInNativeBrowser) {
                intent = new Intent(FullScreenBanner.this._context, BrowserActivity.class);
                intent.putExtra(BrowserActivity.KEY_BROWSER_URL, url);
                intent.putExtra(BrowserActivity.KEY_BANNER_TYPE, BrowserActivity.BANNER_TYPE_FULLSCREEN);
                intent.putExtra(BrowserActivity.KEY_TIMER_DURATION, FullScreenBanner.this._timerDuration);
                intent.addFlags(268435456);
                StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_REDIRECT_START, details);
            } else {
                intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_EXTERNAL_BROWSER, details);
            }
            FullScreenBanner.this._context.startActivity(intent);
            intent.setFlags(268435456);
            FullScreenBanner.this.close();
            if (FullScreenBanner.this._clickUrl != null && FullScreenBanner.this._clickUrl.length() > 0) {
                AppsgeyserServerClient.getInstance().sendRequest(FullScreenBanner.this._clickUrl);
            }
            return true;
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.FullScreenBanner.4 */
    class C01464 implements Runnable {
        C01464() {
        }

        public void run() {
            FullScreenBanner.this._bannerView.loadUrl(FullScreenBanner.this._bannerUrl);
        }
    }

    private enum BannerTypes {
        HTML,
        ADMOB,
        NO_BANNER,
        PENDING_BANNER
    }

    /* renamed from: com.appsgeyser.sdk.ads.FullScreenBanner.2 */
    class C15162 implements IDeviceIdParserListener {
        C15162() {
        }

        public void onDeviceIdParametersObtained(DeviceIdParameters result) {
            FullScreenBanner.this._initWithDeviceIdParameters(result);
        }
    }

    static {
        TAG = FullScreenBanner.class.getSimpleName();
        inLoadingProcess = false;
    }

    public static FullScreenBanner getInstance(Context context) {
        FullScreenBanner localInstance = _instance;
        if (localInstance == null) {
            synchronized (FullScreenBanner.class) {
                try {
                    localInstance = _instance;
                    if (localInstance == null) {
                        FullScreenBanner localInstance2 = new FullScreenBanner(context);
                        try {
                            _instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    private FullScreenBanner(Context context) {
        this._currentBannerType = BannerTypes.NO_BANNER;
        this._clickUrl = null;
        this._redirect = false;
        this._adMobFSBannerController = null;
        this._openInNativeBrowser = true;
        this._backKeyLocked = true;
        this._timerDuration = NO_TIMER;
        this._context = null;
        this._bannerView = null;
        this._bannerUrl = null;
        this._listener = null;
        this._ready = false;
        this._errorOnTimeout = true;
        this._errorHappened = false;
        this._keepAliveCalled = false;
        this._handler = new Handler();
        this._closeRunnable = new C01441();
        this._context = context;
        this._bannerView = new WebView(this._context);
        this._adMobFSBannerController = new AdMobFSBannerController(this._context);
        DeviceIdParser parser = DeviceIdParser.getInstance();
        if (parser.isEmty()) {
            parser.rescan(this._context, new C15162());
        } else {
            _initWithDeviceIdParameters(parser.getDeviceIdParameters());
        }
        this._bannerView.setWebViewClient(new C01453());
        this._bannerView.addJavascriptInterface(new FullscreenBannerJsInterface(this, this._context), FullscreenBannerJsInterface.JS_INTERFACE_NAME);
        String appCachePath = this._context.getDir("appcache", 0).getPath();
        String geolocationDatabasePath = this._context.getDir("geolocation", 0).getPath();
        WebSettings settings = this._bannerView.getSettings();
        settings.setAppCachePath(appCachePath);
        settings.setGeolocationDatabasePath(geolocationDatabasePath);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setGeolocationEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
    }

    public void load() {
        if (inLoadingProcess) {
            Log.e(TAG, "already loading!");
            return;
        }
        inLoadingProcess = true;
        this._keepAliveCalled = false;
        this._ready = false;
        this._errorHappened = false;
        if (this._bannerUrl != null) {
            this._currentBannerType = BannerTypes.NO_BANNER;
            _loadBanner();
        } else {
            this._currentBannerType = BannerTypes.PENDING_BANNER;
        }
        if (this._listener != null) {
            this._listener.onLoadStarted();
        }
    }

    private void _loadBanner() {
        if (this._bannerUrl != null) {
            this._bannerView.post(new C01464());
            this._handler.postDelayed(this._closeRunnable, LOADING_TIMEOUT);
        } else {
            Log.e(TAG, "initialization error, can't load banner!");
        }
        inLoadingProcess = true;
    }

    public boolean isReady() {
        return this._ready;
    }

    public void show() {
        if (!this._ready || this._errorHappened) {
            Log.e(TAG, "banner is not ready!");
        } else if (this._currentBannerType.equals(BannerTypes.HTML)) {
            Intent intent = new Intent(this._context, AdActivity.class);
            intent.setFlags(268435456);
            this._context.startActivity(intent);
        } else if (this._currentBannerType.equals(BannerTypes.ADMOB)) {
            this._adMobFSBannerController.showBanner();
        }
    }

    protected void stayAlive() {
        this._keepAliveCalled = true;
    }

    public void setListener(IFullScreenBannerListener listener) {
        this._listener = listener;
        this._adMobFSBannerController.setListener(listener);
    }

    public WebView getWebView() {
        return this._bannerView;
    }

    public void clearView() {
        this._bannerView.loadUrl("about:blank");
    }

    private void _initWithDeviceIdParameters(DeviceIdParameters deviceIdParameters) {
        double[] coords = Geolocation.getCoords(this._context);
        Configuration configuration = Configuration.getInstance();
        String version = configuration.getPlatformVersion();
        String deviceIdSection = TtmlNode.ANONYMOUS_REGION_ID;
        if (deviceIdParameters != null) {
            String advId = deviceIdParameters.getAdvid();
            String limitAdTrackingEnabled = deviceIdParameters.getLimitAdTrackingEnabled().toString().toLowerCase();
            String aid = deviceIdParameters.getAid();
            if (advId == null || TtmlNode.ANONYMOUS_REGION_ID == advId) {
                deviceIdSection = "&aid=" + aid;
            } else {
                deviceIdSection = "&advid=" + advId + "&limit_ad_tracking_enabled=" + limitAdTrackingEnabled;
            }
        }
        this._bannerUrl = new StringBuilder(SPLASH_SERVER_DOMAIN).append("?widgetid=" + configuration.getApplicationId() + "&guid=" + configuration.getAppGuid() + "&v=" + version + deviceIdSection + "&tlat=" + coords[0] + "&tlon=" + coords[1] + "&p=android&sdk=1").toString();
        if (this._currentBannerType.equals(BannerTypes.PENDING_BANNER)) {
            _loadBanner();
        }
    }

    protected void setClickUrl(String clickUrl) {
        this._clickUrl = clickUrl;
    }

    protected void showAdMobFSBanner(String adUnitID, String keywords, String genderString, String birthday, String latitude, String longtitude) {
        this._currentBannerType = BannerTypes.ADMOB;
        this._adMobFSBannerController.load(adUnitID, keywords, genderString, birthday, latitude, longtitude);
    }

    public void close() {
        this._redirect = false;
        if (this._currentBannerType.equals(BannerTypes.HTML)) {
            AdActivity activity = AdActivity.getInstance();
            if (activity != null) {
                try {
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isErrorOnTimeout() {
        return this._errorOnTimeout;
    }

    public void setErrorOnTimeout(boolean errorOnTimeout) {
        this._errorOnTimeout = errorOnTimeout;
    }

    public void forceOpenInNativeBrowser(boolean openInNativeBrowser) {
        this._openInNativeBrowser = openInNativeBrowser;
    }

    public void setBackKeyLocked(boolean locked) {
        this._backKeyLocked = locked;
    }

    public void setShowTimer(long ms) {
        if (ms > 0) {
            this._timerDuration = ms;
        }
    }
}
