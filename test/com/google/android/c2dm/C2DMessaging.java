package com.google.android.c2dm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

public class C2DMessaging {
    public static final String BACKOFF = "backoff";
    private static final long DEFAULT_BACKOFF = 30000;
    public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
    public static final String EXTRA_SENDER = "sender";
    public static final String GSF_PACKAGE = "com.example.temp";
    public static final String LAST_REGISTRATION_CHANGE = "last_registration_change";
    public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
    public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";

    public static void register(Context context, String senderId) {
        Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
        registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        registrationIntent.putExtra(EXTRA_SENDER, senderId);
        context.startService(registrationIntent);
    }

    public static void unregister(Context context) {
        Intent regIntent = new Intent(REQUEST_UNREGISTRATION_INTENT);
        regIntent.setPackage(GSF_PACKAGE);
        regIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        context.startService(regIntent);
    }

    public static String getRegistrationId(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString("dm_registration", TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static long getLastRegistrationChange(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getLong(LAST_REGISTRATION_CHANGE, 0);
    }

    static long getBackoff(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getLong(BACKOFF, DEFAULT_BACKOFF);
    }

    static void setBackoff(Context context, long backoff) {
        Editor editor = context.getSharedPreferences(context.getPackageName(), 0).edit();
        editor.putLong(BACKOFF, backoff);
        editor.commit();
    }

    public static void clearRegistrationId(Context context) {
        Editor editor = context.getSharedPreferences(context.getPackageName(), 0).edit();
        editor.putString("dm_registration", TtmlNode.ANONYMOUS_REGION_ID);
        editor.putLong(LAST_REGISTRATION_CHANGE, System.currentTimeMillis());
        editor.commit();
    }

    static void setRegistrationId(Context context, String registrationId) {
        Editor editor = context.getSharedPreferences(context.getPackageName(), 0).edit();
        editor.putString("dm_registration", registrationId);
        editor.commit();
    }
}
