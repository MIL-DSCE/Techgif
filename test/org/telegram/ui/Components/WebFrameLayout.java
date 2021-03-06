package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.Theme;

public class WebFrameLayout extends FrameLayout {
    static final Pattern youtubeIdRegex;
    private View customView;
    private CustomViewCallback customViewCallback;
    private BottomSheet dialog;
    private String embedUrl;
    private FrameLayout fullscreenVideoContainer;
    private boolean hasDescription;
    private int height;
    private String openUrl;
    private ProgressBar progressBar;
    private WebView webView;
    private int width;
    private final String youtubeFrame;

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.1 */
    class C11961 extends WebChromeClient {
        C11961() {
        }

        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);
        }

        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (WebFrameLayout.this.customView != null) {
                callback.onCustomViewHidden();
                return;
            }
            WebFrameLayout.this.customView = view;
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.getSheetContainer().setVisibility(4);
                WebFrameLayout.this.fullscreenVideoContainer.setVisibility(0);
                WebFrameLayout.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
            }
            WebFrameLayout.this.customViewCallback = callback;
        }

        public void onHideCustomView() {
            super.onHideCustomView();
            if (WebFrameLayout.this.customView != null) {
                if (WebFrameLayout.this.dialog != null) {
                    WebFrameLayout.this.dialog.getSheetContainer().setVisibility(0);
                    WebFrameLayout.this.fullscreenVideoContainer.setVisibility(4);
                    WebFrameLayout.this.fullscreenVideoContainer.removeView(WebFrameLayout.this.customView);
                }
                if (!(WebFrameLayout.this.customViewCallback == null || WebFrameLayout.this.customViewCallback.getClass().getName().contains(".chromium."))) {
                    WebFrameLayout.this.customViewCallback.onCustomViewHidden();
                }
                WebFrameLayout.this.customView = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.2 */
    class C11972 extends WebViewClient {
        C11972() {
        }

        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WebFrameLayout.this.progressBar.setVisibility(4);
        }
    }

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.3 */
    class C11983 implements OnClickListener {
        C11983() {
        }

        public void onClick(View v) {
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.dismiss();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.4 */
    class C11994 implements OnClickListener {
        C11994() {
        }

        public void onClick(View v) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", WebFrameLayout.this.openUrl));
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            Toast.makeText(WebFrameLayout.this.getContext(), LocaleController.getString("LinkCopied", C0691R.string.LinkCopied), 0).show();
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.dismiss();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.5 */
    class C12005 implements OnClickListener {
        C12005() {
        }

        public void onClick(View v) {
            Browser.openUrl(WebFrameLayout.this.getContext(), WebFrameLayout.this.openUrl);
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.dismiss();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.6 */
    class C12016 implements OnTouchListener {
        C12016() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.WebFrameLayout.7 */
    class C20157 extends BottomSheetDelegate {
        C20157() {
        }

        public void onOpenAnimationEnd() {
            HashMap<String, String> args = new HashMap();
            args.put("Referer", "http://youtube.com");
            boolean ok = false;
            try {
                String host = Uri.parse(WebFrameLayout.this.openUrl).getHost().toLowerCase();
                if ((host != null && host.endsWith("youtube.com")) || host.endsWith("youtu.be")) {
                    Matcher matcher = WebFrameLayout.youtubeIdRegex.matcher(WebFrameLayout.this.openUrl);
                    String id = null;
                    if (matcher.find()) {
                        id = matcher.group(1);
                    }
                    if (id != null) {
                        ok = true;
                        WebFrameLayout.this.webView.loadDataWithBaseURL("http://youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,    .embed-container embed {        position: absolute;        top: 0;        left: 0;        width: 100%% !important;        height: 100%% !important;    }    </style></head><body>    <div class=\"embed-container\">        <div id=\"player\"></div>    </div>    <script src=\"https://www.youtube.com/iframe_api\"></script>    <script>    var player;    YT.ready(function() {         player = new YT.Player(\"player\", {                                \"width\" : \"100%%\",                                \"events\" : {                                \"onReady\" : \"onReady\",                                },                                \"videoId\" : \"%1$s\",                                \"height\" : \"100%%\",                                \"playerVars\" : {                                \"start\" : 0,                                \"rel\" : 0,                                \"showinfo\" : 0,                                \"modestbranding\" : 1,                                \"iv_load_policy\" : 3,                                \"autohide\" : 1,                                \"cc_load_policy\" : 1,                                \"playsinline\" : 1,                                \"controls\" : 1                                }                                });        player.setSize(window.innerWidth, window.innerHeight);    });    function onReady(event) {        player.playVideo();    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{id}), "text/html", C0747C.UTF8_NAME, "http://youtube.com");
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (!ok) {
                try {
                    WebFrameLayout.this.webView.loadUrl(WebFrameLayout.this.embedUrl, args);
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
        }
    }

    static {
        youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})");
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public WebFrameLayout(Context context, BottomSheet parentDialog, String title, String descripton, String originalUrl, String url, int w, int h) {
        TextView textView;
        super(context);
        this.youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,    .embed-container embed {        position: absolute;        top: 0;        left: 0;        width: 100%% !important;        height: 100%% !important;    }    </style></head><body>    <div class=\"embed-container\">        <div id=\"player\"></div>    </div>    <script src=\"https://www.youtube.com/iframe_api\"></script>    <script>    var player;    YT.ready(function() {         player = new YT.Player(\"player\", {                                \"width\" : \"100%%\",                                \"events\" : {                                \"onReady\" : \"onReady\",                                },                                \"videoId\" : \"%1$s\",                                \"height\" : \"100%%\",                                \"playerVars\" : {                                \"start\" : 0,                                \"rel\" : 0,                                \"showinfo\" : 0,                                \"modestbranding\" : 1,                                \"iv_load_policy\" : 3,                                \"autohide\" : 1,                                \"cc_load_policy\" : 1,                                \"playsinline\" : 1,                                \"controls\" : 1                                }                                });        player.setSize(window.innerWidth, window.innerHeight);    });    function onReady(event) {        player.playVideo();    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";
        this.embedUrl = url;
        boolean z = descripton != null && descripton.length() > 0;
        this.hasDescription = z;
        this.openUrl = originalUrl;
        this.width = w;
        this.height = h;
        if (this.width == 0 || this.height == 0) {
            this.width = AndroidUtilities.displaySize.x;
            this.height = AndroidUtilities.displaySize.y / 2;
        }
        this.dialog = parentDialog;
        this.fullscreenVideoContainer = new FrameLayout(context);
        this.fullscreenVideoContainer.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        if (VERSION.SDK_INT >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        parentDialog.setApplyTopPadding(false);
        parentDialog.setApplyBottomPadding(false);
        this.dialog.getContainer().addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.fullscreenVideoContainer.setVisibility(4);
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        if (VERSION.SDK_INT >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        String userAgent = this.webView.getSettings().getUserAgentString();
        if (userAgent != null) {
            this.webView.getSettings().setUserAgentString(userAgent.replace("Android", TtmlNode.ANONYMOUS_REGION_ID));
        }
        if (VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebChromeClient(new C11961());
        this.webView.setWebViewClient(new C11972());
        addView(this.webView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
        this.progressBar = new ProgressBar(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) (((this.hasDescription ? 22 : 0) + 84) / 2)));
        if (this.hasDescription) {
            textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(-14540254);
            textView.setText(descripton);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setEllipsize(TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-7697782);
        textView.setText(title);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View lineView = new View(context);
        lineView.setBackgroundColor(-2368549);
        addView(lineView, new LayoutParams(-1, 1, 83));
        ((LayoutParams) lineView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        addView(frameLayout, LayoutHelper.createFrame(-1, 48, 83));
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("Close", C0691R.string.Close).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new C11983());
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("Copy", C0691R.string.Copy).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new C11994());
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView.setText(LocaleController.getString("OpenInBrowser", C0691R.string.OpenInBrowser).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new C12005());
        setOnTouchListener(new C12016());
        parentDialog.setDelegate(new C20157());
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((AndroidUtilities.dp((float) ((this.hasDescription ? 22 : 0) + 84)) + ((int) Math.min(((float) this.height) / ((float) (this.width / MeasureSpec.getSize(widthMeasureSpec))), (float) (AndroidUtilities.displaySize.y / 2)))) + 1, C0747C.ENCODING_PCM_32BIT));
    }
}
