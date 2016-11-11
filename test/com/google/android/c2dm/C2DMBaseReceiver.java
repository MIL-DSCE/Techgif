package com.google.android.c2dm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.appsgeyser.sdk.AppsgeyserSDKInternal;
import com.appsgeyser.sdk.server.C2DMClientReceiver;
import java.io.IOException;

public abstract class C2DMBaseReceiver extends IntentService {
    private static final String C2DM_INTENT = "com.google.android.c2dm.intent.RECEIVE";
    private static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";
    public static final String ERR_ACCOUNT_MISSING = "ACCOUNT_MISSING";
    public static final String ERR_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String ERR_INVALID_PARAMETERS = "INVALID_PARAMETERS";
    public static final String ERR_INVALID_SENDER = "INVALID_SENDER";
    public static final String ERR_PHONE_REGISTRATION_ERROR = "PHONE_REGISTRATION_ERROR";
    public static final String ERR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
    public static final String ERR_TOO_MANY_REGISTRATIONS = "TOO_MANY_REGISTRATIONS";
    public static final String EXTRA_ERROR = "error";
    public static final String EXTRA_REGISTRATION_ID = "registration_id";
    public static final String EXTRA_UNREGISTERED = "unregistered";
    public static final String REGISTRATION_CALLBACK_INTENT = "com.google.android.c2dm.intent.REGISTRATION";
    private static final String TAG = "C2DM";
    private Context _context;
    private final String senderId;

    public abstract void onError(Context context, String str);

    protected abstract void onMessage(Context context, Intent intent);

    public C2DMBaseReceiver(String senderId, Context context) {
        super(senderId);
        this._context = null;
        this.senderId = senderId;
        this._context = context;
    }

    public void onRegistered(Context context, String registrationId) throws IOException {
    }

    public void onUnregistered(Context context, String registration) {
    }

    public final void onHandleIntent(Intent intent) {
        if (this._context == null) {
            this._context = AppsgeyserSDKInternal.getApplication();
        }
        if (intent.getAction().equals(REGISTRATION_CALLBACK_INTENT)) {
            handleRegistration(this._context, intent);
        } else if (intent.getAction().equals(C2DM_INTENT)) {
            onMessage(this._context, intent);
        } else if (intent.getAction().equals(C2DM_RETRY)) {
            C2DMessaging.register(this._context, this.senderId);
        }
    }

    static void runIntentInService(Context context, Intent intent) {
        new C2DMClientReceiver(context).onHandleIntent(intent);
    }

    private void handleRegistration(Context context, Intent intent) {
        String registrationId = intent.getStringExtra(EXTRA_REGISTRATION_ID);
        String error = intent.getStringExtra(EXTRA_ERROR);
        String removed = intent.getStringExtra(EXTRA_UNREGISTERED);
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "dmControl: registrationId = " + registrationId + ", error = " + error + ", removed = " + removed);
        }
        if (removed != null) {
            onUnregistered(context, C2DMessaging.getRegistrationId(context));
            C2DMessaging.clearRegistrationId(context);
        } else if (error != null) {
            C2DMessaging.clearRegistrationId(context);
            Log.e(TAG, "Registration error " + error);
            onError(context, error);
            if (ERR_SERVICE_NOT_AVAILABLE.equals(error)) {
                long backoffTimeMs = C2DMessaging.getBackoff(context);
                Log.d(TAG, "Scheduling registration retry, backoff = " + backoffTimeMs);
                ((AlarmManager) context.getSystemService(NotificationCompatApi21.CATEGORY_ALARM)).set(3, backoffTimeMs, PendingIntent.getBroadcast(context, 0, new Intent(C2DM_RETRY), 0));
                C2DMessaging.setBackoff(context, backoffTimeMs * 2);
            }
        } else {
            try {
                onRegistered(context, registrationId);
                C2DMessaging.setRegistrationId(context, registrationId);
            } catch (IOException ex) {
                Log.e(TAG, "Registration error " + ex.getMessage());
            }
        }
    }
}
