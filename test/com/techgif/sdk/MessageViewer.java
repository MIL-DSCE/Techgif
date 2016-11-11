package com.appsgeyser.sdk;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.appsgeyser.sdk.webwiewclient.PushWebViewClient;
import net.hockeyapp.android.UpdateFragment;

public class MessageViewer extends Activity {
    Activity _activity;
    WebView _browser;

    /* renamed from: com.appsgeyser.sdk.MessageViewer.1 */
    class C01291 extends WebChromeClient {
        C01291() {
        }
    }

    /* renamed from: com.appsgeyser.sdk.MessageViewer.2 */
    class C01302 implements OnClickListener {
        C01302() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    public MessageViewer() {
        this._activity = null;
        this._browser = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._activity = this;
        LinearLayout messageViewer = new LinearLayout(this._activity);
        messageViewer.setLayoutParams(new LayoutParams(-1, -1));
        WebView webView = new WebView(this._activity);
        webView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        messageViewer.addView(webView);
        setContentView(messageViewer);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setTitle(extras.getString("title"));
            String url = extras.getString(UpdateFragment.FRAGMENT_URL);
            if (url != null) {
                this._browser = webView;
                this._browser.loadUrl(url);
                this._browser.setWebViewClient(new PushWebViewClient(url, this));
                this._browser.setWebChromeClient(new C01291());
                WebSettings settings = this._browser.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setAllowFileAccess(true);
                settings.setGeolocationEnabled(true);
                settings.setAppCacheMaxSize(5242880);
                this._browser.setVerticalScrollBarEnabled(false);
                this._browser.setHorizontalScrollBarEnabled(false);
                settings.setLoadWithOverviewMode(true);
                settings.setUseWideViewPort(true);
                settings.setBuiltInZoomControls(true);
                this._browser.setInitialScale(0);
            }
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        this._activity.finish();
        return true;
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onPause() {
        super.onPause();
        this._activity.finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        destroyActivity();
    }

    private void destroyActivity() {
        if (this._browser != null) {
            this._browser.destroy();
            this._browser = null;
        }
    }

    public void showMessage(String text) {
        if (this._activity != null) {
            Builder builder = new Builder(this._activity);
            builder.setMessage(text);
            builder.setPositiveButton("ok", new C01302());
            builder.create().show();
        }
    }
}
