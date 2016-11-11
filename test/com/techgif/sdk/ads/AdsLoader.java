package com.appsgeyser.sdk.ads;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import com.appsgeyser.sdk.BrowserActivity;
import com.appsgeyser.sdk.ads.AdsBannerWebViewClient.OnPageFinishedListener;
import com.appsgeyser.sdk.ads.AdsBannerWebViewClient.OnPageStartedListener;
import com.appsgeyser.sdk.ads.behavior.BehaviorAcceptor;
import com.appsgeyser.sdk.ads.behavior.BehaviorFactory.ClickBehavior;
import com.appsgeyser.sdk.ads.behavior.BehaviorVisitor;
import com.appsgeyser.sdk.ads.behavior.loaderBehaviors.LoaderBehavior;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.device.Device;
import com.appsgeyser.sdk.deviceidparser.DeviceIdParameters;
import com.appsgeyser.sdk.location.Geolocation;
import com.appsgeyser.sdk.server.ContentRequest;
import com.appsgeyser.sdk.server.ContentRequest.Method;
import com.appsgeyser.sdk.server.Response;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.ui.Components.VideoPlayer;

public class AdsLoader implements BehaviorAcceptor, OnPageFinishedListener, OnPageStartedListener {
    private static final String AD_SERVER_DOMAIN = "http://ads.appsgeyser.com/";
    final float DEFAULT_HIDE_TIMEOUT;
    private AdView _adView;
    private String _bannerUrl;
    private ClickBehavior _clickBehavior;
    private String _clickUrl;
    Thread _closeBannerThread;
    private HeadersReceiver _headersReceiver;
    private AdsLoadingFinishedListener _loadingFinishedListener;
    private boolean _openInNativeBrowser;
    Timer _refreshTimer;
    private StatServerClient _serverClient;

    /* renamed from: com.appsgeyser.sdk.ads.AdsLoader.1 */
    class C01341 implements OnTouchListener {
        C01341() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    if (!v.hasFocus()) {
                        v.requestFocus();
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.AdsLoader.2 */
    class C01352 extends Thread {
        private final /* synthetic */ AdView val$adView;

        C01352(AdView adView) {
            this.val$adView = adView;
        }

        public void run() {
            AdsLoader.this._refreshTimer.cancel();
            this.val$adView.getBrowser().stopLoading();
            AdsLoader.this._adView.hide();
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.AdsLoader.3 */
    class C01373 extends Thread {

        /* renamed from: com.appsgeyser.sdk.ads.AdsLoader.3.1 */
        class C01361 implements Runnable {
            C01361() {
            }

            public void run() {
                AdsLoader.this._adView.getBrowser().loadUrl(AdsLoader.this._bannerUrl);
            }
        }

        C01373() {
        }

        public void run() {
            Map<String, List<String>> headers = AdsLoader.this._loadHeaders(AdsLoader.this._bannerUrl);
            if (headers == null) {
                return;
            }
            if (AdsLoader.this._headersReceiver == null || AdsLoader.this._headersReceiver.onAdHeadersReceived(headers)) {
                AdsLoader.this._adView.getBrowser().post(new C01361());
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.AdsLoader.4 */
    class C01384 extends TimerTask {
        C01384() {
        }

        public void run() {
            AdsLoader.this.reload();
            AdsLoader.this._refreshTimer.cancel();
        }
    }

    public interface AdsLoadingFinishedListener {
        void onAdLoadFinished();
    }

    public interface HeadersReceiver {
        boolean onAdHeadersReceived(Map<String, List<String>> map);
    }

    public AdsLoader() {
        this.DEFAULT_HIDE_TIMEOUT = 60000.0f;
        this._closeBannerThread = null;
        this._refreshTimer = new Timer();
        this._openInNativeBrowser = true;
    }

    public void init(AdView adView, DeviceIdParameters deviceIdParameters) {
        this._adView = adView;
        adView.setOnTouchListener(new C01341());
        this._serverClient = new StatServerClient();
        this._bannerUrl = _createBannerUrl(deviceIdParameters);
        this._clickBehavior = ClickBehavior.HIDE;
        this._closeBannerThread = new C01352(adView);
    }

    private String _createBannerUrl(DeviceIdParameters deviceIdParameters) {
        double[] coords = Geolocation.getCoords(this._adView.getContext());
        String deviceId = TtmlNode.ANONYMOUS_REGION_ID;
        String androidId = TtmlNode.ANONYMOUS_REGION_ID;
        try {
            deviceId = Device.getDeviceId(this._adView.getContext());
            androidId = Device.getAndroidId(this._adView.getContext());
        } catch (Exception e) {
        }
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
        } else {
            deviceIdSection = "&hid=" + deviceId + "&aid=" + androidId;
        }
        return new StringBuilder(AD_SERVER_DOMAIN).append("?widgetid=" + configuration.getApplicationId() + "&guid=" + configuration.getAppGuid() + "&v=" + version + deviceIdSection + "&tlat=" + coords[0] + "&tlon=" + coords[1] + "&p=android&sdk=1").toString();
    }

    public String getClickUrl() {
        return this._clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this._clickUrl = clickUrl;
    }

    public void setHeaderReceiver(HeadersReceiver receiver) {
        this._headersReceiver = receiver;
    }

    public void setAdsLoadingFinishedListener(AdsLoadingFinishedListener listener) {
        this._loadingFinishedListener = listener;
    }

    public void reload() {
        try {
            new C01373().start();
        } catch (Exception e) {
            Log.e("AdsLoader", e.getMessage());
        }
    }

    private Map<String, List<String>> _loadHeaders(String url) {
        ContentRequest request = new ContentRequest(new StringBuilder(String.valueOf(url)).append("&test=1").toString());
        request.setMethod(Method.HEAD);
        request.addHeader(ContentRequest.DEFAULT_HEADER_CACHE);
        Response response = request.execute();
        if (response == null || response.status() != Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
            return null;
        }
        return response.getHeaders();
    }

    public void changeClickBehavior(ClickBehavior clickBehavior) {
        this._clickBehavior = clickBehavior;
    }

    public void setRefreshTimeout(float seconds) {
        if (((double) seconds) > 0.0d) {
            this._refreshTimer.cancel();
            this._refreshTimer = new Timer();
            this._refreshTimer.scheduleAtFixedRate(new C01384(), (long) ((int) (1000.0f * seconds)), 100);
        }
    }

    public void setHideTimeout(float seconds) {
        if (((double) seconds) <= 0.0d) {
            seconds = 60000.0f;
        }
        this._adView.removeCallbacks(this._closeBannerThread);
        this._adView.postDelayed(this._closeBannerThread, (long) (1000.0f * seconds));
    }

    public void acceptBehavior(BehaviorVisitor visitor) {
        if (visitor instanceof LoaderBehavior) {
            ((LoaderBehavior) visitor).visit((BehaviorAcceptor) this);
        }
    }

    private void _setDefaults() {
        this._adView.show();
        this._refreshTimer.cancel();
        setHideTimeout(60000.0f);
        this._adView.applyDefaultSettings();
    }

    public boolean loadStarted(WebView view, String url, Bitmap favicon) {
        if (url.equals(this._bannerUrl)) {
            this._adView.switchToHtmlBanner();
            return true;
        }
        Intent intent;
        if (this._clickBehavior == ClickBehavior.HIDE) {
            this._adView.hide();
            this._refreshTimer.cancel();
        } else if (this._clickBehavior == ClickBehavior.REMAIN_ON_SCREEN) {
            reload();
        }
        view.stopLoading();
        url = url.replaceAll("&nostat=1", TtmlNode.ANONYMOUS_REGION_ID);
        if (this._openInNativeBrowser) {
            intent = new Intent(this._adView.getContext(), BrowserActivity.class);
            intent.putExtra(BrowserActivity.KEY_BROWSER_URL, url);
            intent.putExtra(BrowserActivity.KEY_BANNER_TYPE, BrowserActivity.BANNER_TYPE_SMALL);
            intent.addFlags(268435456);
        } else {
            intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        }
        this._adView.getContext().startActivity(intent);
        if (this._clickUrl != null && this._clickUrl.length() > 0) {
            this._serverClient.sendClickInfo(this._clickUrl);
        }
        return false;
    }

    public void loadFinished(WebView view, String url) {
        this._adView.setVisibility(0);
        if (this._adView.isAdMobNow()) {
            this._adView.switchToAdMobBanner();
        } else {
            this._adView.switchToHtmlBanner();
        }
        if (url.equalsIgnoreCase(this._bannerUrl)) {
            _setDefaults();
            if (this._loadingFinishedListener != null) {
                this._loadingFinishedListener.onAdLoadFinished();
            }
        }
    }

    public void forceOpenInNativeBrowser(boolean openInNativeBrowser) {
        this._openInNativeBrowser = openInNativeBrowser;
    }
}
