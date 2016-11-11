package com.appsgeyser.sdk.notification;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import net.hockeyapp.android.UpdateFragment;

public class MessageViewer extends Activity {
    Activity _activity;
    WebView _browser;

    /* renamed from: com.appsgeyser.sdk.notification.MessageViewer.1 */
    class C01491 extends WebChromeClient {
        C01491() {
        }
    }

    /* renamed from: com.appsgeyser.sdk.notification.MessageViewer.2 */
    class C01502 implements OnClickListener {
        C01502() {
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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setTitle(extras.getString("title"));
            String url = extras.getString(UpdateFragment.FRAGMENT_URL);
            if (url != null) {
                this._browser.loadUrl(url);
                this._browser.setWebChromeClient(new C01491());
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
            builder.setPositiveButton("ok", new C01502());
            builder.create().show();
        }
    }
}
