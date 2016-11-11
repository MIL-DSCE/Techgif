package com.appsgeyser.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.appsgeyser.sdk.server.StatController;
import com.appsgeyser.sdk.utils.Drawables;
import java.util.HashMap;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class BrowserActivity extends Activity {
    public static final String BANNER_TYPE_FULLSCREEN = "banner_type_fullscreen";
    public static final String BANNER_TYPE_SMALL = "banner_type_small";
    private static final int HTML_SUBSTRING_LENGTH = 1000;
    public static final String KEY_BANNER_TYPE = "banner_type";
    public static final String KEY_BROWSER_URL = "browser_url";
    public static final String KEY_TIMER_DURATION = "timer_duration";
    private static final int MIN_HTML_ALLOWED_LENGTH = 40;
    private static final int REDIRECT_FINISH_TIMEOUT = 1000;
    private Handler _handler;
    private boolean _isFullScreenBanner;
    private long _timerDuration;
    private long _timerStep;
    boolean _toShowTimer;
    ImageButton mBackButton;
    ImageButton mCloseButton;
    ImageButton mForwardButton;
    private Runnable mHtmlCheckRunnable;
    ImageButton mRefreshButton;
    TextView mTimer;
    WebView mWebView;

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.1 */
    class C01201 implements Runnable {
        C01201() {
        }

        public void run() {
            if (BrowserActivity.this.mWebView != null) {
                BrowserActivity.this.mWebView.loadUrl("javascript:window.HtmlViewer.detectHTML(document.documentElement.innerHTML);");
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.2 */
    class C01212 extends WebViewClient {
        C01212() {
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            _handleRedirect(url);
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return _handleRedirect(url);
        }

        private boolean _handleRedirect(String url) {
            BrowserActivity.this._handler.removeCallbacksAndMessages(null);
            if (url == null) {
                return false;
            }
            boolean isHttpUrl = BrowserActivity._isHttpUrl(url);
            boolean isMarketUrl = BrowserActivity._isMarketUrl(url);
            if (isMarketUrl && isHttpUrl) {
                url = BrowserActivity._replaceHttpWithMarketUrl(url);
            }
            if (!isMarketUrl && isHttpUrl) {
                return false;
            }
            HashMap<String, String> urlDetails = new HashMap();
            urlDetails.put(UpdateFragment.FRAGMENT_URL, url);
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            if (BrowserActivity.deviceCanHandleIntent(BrowserActivity.this, intent)) {
                BrowserActivity.this.startActivity(intent);
                if (BrowserActivity.this._isFullScreenBanner) {
                    StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_FINISH_MARKET, urlDetails);
                }
                BrowserActivity.this.finish();
                return true;
            } else if (!BrowserActivity.this._isFullScreenBanner) {
                return false;
            } else {
                StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_NO_MARKET_ON_DEVICE, urlDetails);
                return false;
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (BrowserActivity.this._isFullScreenBanner) {
                HashMap<String, String> details = new HashMap();
                details.put(StatController.KEY_GET_PARAM_DETAILS, BrowserActivity.this._trimSubstring(Integer.toString(errorCode) + " : " + description, BrowserActivity.REDIRECT_FINISH_TIMEOUT));
                details.put(UpdateFragment.FRAGMENT_URL, failingUrl);
                StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_LOADING_ERROR, details);
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        public void onPageFinished(WebView view, String url) {
            if (!BrowserActivity._isMarketUrl(url) && BrowserActivity._isHttpUrl(url)) {
                BrowserActivity.this._handler.postDelayed(BrowserActivity.this.mHtmlCheckRunnable, 1000);
                super.onPageFinished(view, url);
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.3 */
    class C01223 extends WebChromeClient {
        C01223() {
        }

        public void onProgressChanged(WebView webView, int progress) {
            BrowserActivity.this.setTitle("Loading...");
            BrowserActivity.this.setProgress(progress * 100);
            if (progress == 100) {
                BrowserActivity.this.setTitle(webView.getUrl());
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.4 */
    class C01234 implements DownloadListener {
        C01234() {
        }

        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            ResolveInfo ri = BrowserActivity.this.getPackageManager().resolveActivity(intent, 0);
            String resultKey = StatController.KEY_CLICK_CAN_NOT_START_DOWNLOAD;
            if (ri != null) {
                BrowserActivity.this.startActivity(intent);
                resultKey = StatController.KEY_CLICK_FINISH_DOWNLOAD;
            }
            if (BrowserActivity.this._isFullScreenBanner) {
                HashMap<String, String> details = new HashMap();
                details.put(UpdateFragment.FRAGMENT_URL, url);
                StatController.getInstance().sendRequestAsyncByKey(resultKey, details);
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.5 */
    class C01245 implements OnClickListener {
        C01245() {
        }

        public void onClick(View v) {
            if (BrowserActivity.this._isFullScreenBanner) {
                HashMap<String, String> details = new HashMap();
                details.put(UpdateFragment.FRAGMENT_URL, BrowserActivity.this.mWebView.getUrl());
                StatController.getInstance().sendRequestAsyncByKey(StatController.KEY_CLICK_CROSS_MINI_BROWSER, details);
            }
            BrowserActivity.this.finish();
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.6 */
    class C01256 implements OnClickListener {
        C01256() {
        }

        public void onClick(View v) {
            if (BrowserActivity.this.mWebView.canGoBack()) {
                BrowserActivity.this.mWebView.goBack();
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.7 */
    class C01267 implements OnClickListener {
        C01267() {
        }

        public void onClick(View v) {
            if (BrowserActivity.this.mWebView.canGoForward()) {
                BrowserActivity.this.mWebView.goForward();
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.8 */
    class C01278 implements OnClickListener {
        C01278() {
        }

        public void onClick(View v) {
            BrowserActivity.this.mWebView.reload();
        }
    }

    /* renamed from: com.appsgeyser.sdk.BrowserActivity.9 */
    class C01289 extends CountDownTimer {
        C01289(long $anonymous0, long $anonymous1) {
            super($anonymous0, $anonymous1);
        }

        public void onTick(long millisUntilFinished) {
            BrowserActivity.this.mTimer.setText(String.valueOf(millisUntilFinished / BrowserActivity.this._timerStep));
        }

        public void onFinish() {
            BrowserActivity.this._showClose();
        }
    }

    class DetectJSInterface {
        public static final String NAME = "HtmlViewer";

        DetectJSInterface() {
        }

        @JavascriptInterface
        public void detectHTML(String html) {
            String resultKey;
            if (html == null || html.length() < BrowserActivity.MIN_HTML_ALLOWED_LENGTH) {
                resultKey = StatController.KEY_CLICK_FINISH_EMPTY_HTML;
            } else {
                resultKey = StatController.KEY_CLICK_FINISH_HTML;
            }
            if (BrowserActivity.this._isFullScreenBanner) {
                HashMap<String, String> details = new HashMap();
                details.put(StatController.KEY_GET_PARAM_DETAILS, BrowserActivity.this._trimSubstring(html, BrowserActivity.REDIRECT_FINISH_TIMEOUT));
                StatController.getInstance().sendRequestAsyncByKey(resultKey, details);
            }
        }
    }

    public BrowserActivity() {
        this._timerStep = 1000;
        this._timerDuration = -1;
        this._isFullScreenBanner = false;
        this._toShowTimer = false;
        this._handler = new Handler();
        this.mHtmlCheckRunnable = new C01201();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableCookies();
        Intent intent = getIntent();
        String url = intent.getStringExtra(KEY_BROWSER_URL);
        this._isFullScreenBanner = intent.getStringExtra(KEY_BANNER_TYPE).equals(BANNER_TYPE_FULLSCREEN);
        this._timerDuration = intent.getLongExtra(KEY_TIMER_DURATION, -1);
        requestWindowFeature(2);
        getWindow().setFeatureInt(2, -1);
        getWindow().setFlags(MessagesController.UPDATE_MASK_PHONE, MessagesController.UPDATE_MASK_PHONE);
        setContentView(_initBrowserView());
        this.mWebView.resumeTimers();
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.addJavascriptInterface(new DetectJSInterface(), DetectJSInterface.NAME);
        this.mWebView.setWebViewClient(new C01212());
        this.mWebView.setWebChromeClient(new C01223());
        this.mWebView.setDownloadListener(new C01234());
        _initButtons();
        if (this._timerDuration > 0) {
            _showTimer();
            _startTimer();
        } else {
            _showClose();
        }
        this.mWebView.loadUrl(url);
    }

    private void _initButtons() {
        this.mCloseButton.setOnClickListener(new C01245());
        this.mBackButton.setOnClickListener(new C01256());
        this.mForwardButton.setOnClickListener(new C01267());
        this.mRefreshButton.setOnClickListener(new C01278());
    }

    private void enableCookies() {
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
    }

    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    protected void onResume() {
        super.onResume();
        this.mWebView.resumeTimers();
        CookieSyncManager.getInstance().startSync();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mWebView.destroy();
        this.mWebView = null;
    }

    public void onBackPressed() {
    }

    public static boolean deviceCanHandleIntent(Context context, Intent intent) {
        try {
            if (context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                return false;
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static String _replaceHttpWithMarketUrl(String url) {
        if (!_isMarketUrl(url)) {
            return url;
        }
        return "market://details?" + Uri.parse(url).getEncodedQuery();
    }

    private static boolean _isMarketUrl(String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        return uri.getScheme().equals("market") || (host != null && host.equals("play.google.com"));
    }

    private static boolean _isHttpUrl(String url) {
        String scheme = Uri.parse(url).getScheme();
        return scheme.equals("http") || scheme.equals("https");
    }

    private String _trimSubstring(String string, int count) {
        if (string == null) {
            return null;
        }
        if (count > string.length()) {
            count = string.length();
        }
        return string.substring(0, count - 1);
    }

    private void _showTimer() {
        this.mTimer.setVisibility(0);
        this.mCloseButton.setVisibility(8);
    }

    private void _startTimer() {
        new C01289(this._timerDuration, this._timerStep).start();
    }

    private void _showClose() {
        this.mTimer.setVisibility(8);
        this.mCloseButton.setVisibility(0);
    }

    private View _initBrowserView() {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        new LayoutParams(-1, -1).addRule(15, -1);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(-13421773);
        linearLayout.setOrientation(0);
        LayoutParams rllp = new LayoutParams(-1, _pixelsByDp(MIN_HTML_ALLOWED_LENGTH));
        rllp.addRule(12);
        linearLayout.setLayoutParams(rllp);
        this.mBackButton = _getButton(Drawables.LEFT_ARROW.createDrawable(this));
        this.mForwardButton = _getButton(Drawables.RIGHT_ARROW.createDrawable(this));
        this.mRefreshButton = _getButton(Drawables.REFRESH.createDrawable(this));
        this.mCloseButton = _getButton(Drawables.CLOSE.createDrawable(this));
        linearLayout.addView(this.mBackButton);
        linearLayout.addView(this.mForwardButton);
        linearLayout.addView(this.mRefreshButton);
        linearLayout.addView(this.mCloseButton);
        this.mTimer = new TextView(this);
        this.mTimer.setBackgroundColor(0);
        this.mTimer.setPadding(_pixelsByDp(5), _pixelsByDp(5), _pixelsByDp(5), _pixelsByDp(5));
        this.mTimer.setGravity(17);
        this.mTimer.setTextColor(-1);
        this.mTimer.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(_pixelsByDp(30), _pixelsByDp(30), TouchHelperCallback.ALPHA_FULL);
        tlp.gravity = 17;
        this.mTimer.setLayoutParams(tlp);
        linearLayout.addView(this.mTimer);
        this.mWebView = new WebView(this);
        LayoutParams vwlp = new LayoutParams(-1, -2);
        vwlp.addRule(10, -1);
        this.mWebView.setLayoutParams(vwlp);
        relativeLayout.addView(this.mWebView);
        relativeLayout.addView(linearLayout);
        return relativeLayout;
    }

    private ImageButton _getButton(Drawable drawable) {
        ImageButton imageButton = new ImageButton(this);
        imageButton.setBackgroundColor(0);
        imageButton.setPadding(_pixelsByDp(5), _pixelsByDp(5), _pixelsByDp(5), _pixelsByDp(5));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(_pixelsByDp(30), _pixelsByDp(30), TouchHelperCallback.ALPHA_FULL);
        layoutParams.gravity = 17;
        imageButton.setLayoutParams(layoutParams);
        imageButton.setImageDrawable(drawable);
        if (drawable.equals(Drawables.CLOSE)) {
            imageButton.setVisibility(8);
        }
        return imageButton;
    }

    private int _pixelsByDp(int dp) {
        return (int) ((((float) dp) * getResources().getDisplayMetrics().density) + 0.5f);
    }
}
