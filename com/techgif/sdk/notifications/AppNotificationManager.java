package com.appsgeyser.sdk.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.appsgeyser.sdk.MessageViewer;
import net.hockeyapp.android.UpdateFragment;

public class AppNotificationManager {
    public static final int NOTIFICATION_ID = 10001;

    public static Intent getLaunchIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, MessageViewer.class);
        intent.addFlags(67108864);
        intent.addFlags(268435456);
        intent.addFlags(AccessibilityNodeInfoCompat.ACTION_SET_TEXT);
        Bundle extras = new Bundle();
        extras.putString(UpdateFragment.FRAGMENT_URL, url);
        extras.putString("title", title);
        intent.putExtras(extras);
        return intent;
    }

    public static void generateNotification(Context context, String msg, String title, Intent intent) {
        Notification notification = new Notification(context.getApplicationInfo().icon, title, System.currentTimeMillis());
        notification.setLatestEventInfo(context, title, msg, PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 268435455), intent, 0));
        notification.flags |= 16;
        ((NotificationManager) context.getSystemService("notification")).notify((int) System.currentTimeMillis(), notification);
        playNotificationSound(context);
    }

    public static void playNotificationSound(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(2);
        if (uri != null) {
            Ringtone rt = RingtoneManager.getRingtone(context, uri);
            if (rt != null) {
                rt.setStreamType(5);
                rt.play();
            }
        }
    }
}
