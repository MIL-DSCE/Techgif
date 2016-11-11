package com.appsgeyser.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import org.telegram.messenger.MessagesController;

public class AdActivity extends Activity {
    static AdActivity _activity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activity = this;
        requestWindowFeature(1);
        getWindow().setFlags(MessagesController.UPDATE_MASK_PHONE, MessagesController.UPDATE_MASK_PHONE);
        WebView bannerView = AppsgeyserSDKInternal.getFullScreenBanner().getWebView();
        _removeView(bannerView);
        setContentView(bannerView);
    }

    public void onBackPressed() {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void _removeView(View bannerView) {
        ViewParent parent = bannerView.getParent();
        if (parent != null) {
            ViewGroup vg = (ViewGroup) parent;
            if (vg != null) {
                vg.removeView(bannerView);
            }
        }
    }

    public static AdActivity getInstance() {
        return _activity;
    }
}
