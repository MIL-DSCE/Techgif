package com.appsgeyser.sdk.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.appsgeyser.sdk.ads.StatServerClient;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.notifications.AppNotificationManager;
import com.google.android.c2dm.C2DMBaseReceiver;
import net.hockeyapp.android.UpdateFragment;

public class C2DMClientReceiver extends C2DMBaseReceiver {
    public C2DMClientReceiver(Context context) {
        super(Configuration.getInstance().getPushAccount(), context);
    }

    public void onRegistered(Context context, String registration) {
        Configuration.getInstance().loadConfiguration();
        new PushServerClient().sendRegisteredId(registration);
    }

    public void onUnregistered(Context context, String registration) {
        Configuration.getInstance().loadConfiguration();
        new PushServerClient().sendUnregisteredId(registration);
    }

    public void onError(Context context, String errorId) {
        Log.e("push", errorId);
    }

    public void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String url = extras.getString(UpdateFragment.FRAGMENT_URL);
            String message = extras.getString("message");
            String title = extras.getString("title");
            AppNotificationManager.generateNotification(context, message, title, AppNotificationManager.getLaunchIntent(context, title, url));
            String TAG = getClass().getSimpleName();
            Log.i(TAG, "Got incoming push message, url is " + url);
            Log.i(TAG, "Sending feedback to Appsgeyser...");
            new StatServerClient().sendPushReceivedAsync(url);
        }
    }
}
