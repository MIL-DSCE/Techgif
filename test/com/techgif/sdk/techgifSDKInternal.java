package com.appsgeyser.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import com.appsgeyser.sdk.ads.AdView;
import com.appsgeyser.sdk.ads.FullScreenBanner;
import com.appsgeyser.sdk.analytics.Analytics;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.configuration.Constants;
import com.appsgeyser.sdk.configuration.PreferencesCoder;
import com.appsgeyser.sdk.server.PushServerClient;
import org.telegram.messenger.MessagesController;

public class AppsgeyserSDKInternal {
    public static String packageName;
    private Activity _activity;
    protected AdView _adView;
    protected Analytics _analytics;
    protected Application _application;
    protected Configuration _configuration;
    protected boolean _enablePull;
    protected boolean _enablePush;

    private static class SingletonHolder {
        public static final AppsgeyserSDKInternal HOLDER_INSTANCE;

        private SingletonHolder() {
        }

        static {
            HOLDER_INSTANCE = new AppsgeyserSDKInternal();
        }
    }

    static {
        packageName = TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static void takeOff(Application application, String APIkey) {
        getInstance()._takeOff(application, APIkey);
    }

    public static void setAdView(AdView adView) {
        getInstance()._setAdView(adView);
    }

    public static void setActivity(Activity activity) {
        getInstance()._setActivity(activity);
    }

    private Activity _getActivity() {
        return this._activity;
    }

    private void _setActivity(Activity activity) {
        this._activity = activity;
    }

    public static AdView getAdView() {
        return getInstance()._getAdView();
    }

    public static Analytics getAnalytics() {
        return getInstance()._analytics;
    }

    public static void enablePush() {
        getInstance().setPushEnabled(true);
    }

    public static void enablePull() {
        getInstance().setPullEnabled(true);
    }

    public static FullScreenBanner getFullScreenBanner() {
        return FullScreenBanner.getInstance(getInstance()._application);
    }

    public static void runOnMainThread(Runnable blockToExecute) {
        Application application = getApplication();
        if (application != null && blockToExecute != null) {
            runOnMainThread(application, blockToExecute);
        }
    }

    public static void runOnMainThread(Context context, Runnable blockToExecute) {
        if (context != null && blockToExecute != null) {
            new Handler(context.getMainLooper()).post(blockToExecute);
        }
    }

    private AppsgeyserSDKInternal() {
        this._application = null;
        this._configuration = null;
        this._analytics = null;
        this._enablePush = false;
        this._enablePull = false;
        this._adView = null;
        this._activity = null;
    }

    public boolean pushEnabled() {
        return this._enablePush;
    }

    public boolean pullEnabled() {
        return this._enablePull;
    }

    public void setPushEnabled(boolean _enablePush) {
        this._enablePush = _enablePush;
    }

    public void setPullEnabled(boolean _enablePull) {
        this._enablePull = _enablePull;
    }

    protected static AppsgeyserSDKInternal getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public static Application getApplication() {
        return SingletonHolder.HOLDER_INSTANCE._application;
    }

    private void _setAdView(AdView adView) {
        this._adView = adView;
    }

    private AdView _getAdView() {
        return this._adView;
    }

    private Application _getApplication() {
        return this._application;
    }

    private void _takeOff(Application application, String APIkey) {
        packageName = application.getPackageName();
        if (application != null && StringUtils.isNotNullOrEmptyString(APIkey) && _checkPermissions(application)) {
            _init(application);
            this._configuration = Configuration.getInstance();
            if (!(this._configuration.getApplicationId() != null ? this._configuration.getApplicationId() : TtmlNode.ANONYMOUS_REGION_ID).equals(APIkey)) {
                this._configuration.clearApplicationSettings();
                this._configuration.setApplicationId(APIkey);
            }
            if (this._enablePush) {
                new PushServerClient().loadPushAccount();
            }
        }
    }

    private void _init(Application application) {
        this._application = application;
        this._configuration = Configuration.getInstance();
        this._configuration.setSettingsCoder(new PreferencesCoder(this._application, Constants.PREFS_NAME));
        this._configuration.loadConfiguration();
        this._analytics = new Analytics();
    }

    private boolean _checkPermissions(Application application) {
        if (application.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0 && application.checkCallingOrSelfPermission("android.permission.INTERNET") == 0) {
            if (this._enablePush) {
                PackageManager packageManager = application.getPackageManager();
                String packageName = application.getPackageName();
                try {
                    packageManager.getPermissionInfo(new StringBuilder(String.valueOf(packageName)).append(".permission.C2D_MESSAGE").toString(), MessagesController.UPDATE_MASK_SEND_STATE);
                } catch (NameNotFoundException e) {
                    ExceptionHandler.handleException(new Exception("Invalid permission. You have to grant " + packageName + ".permission.C2D_MESSAGE" + " and com.google.android.c2dm.permission.RECEIVE permissions to work properly"));
                    return false;
                }
            }
            return true;
        }
        ExceptionHandler.handleException(new Exception("Invalid permission. You have to grant ACCESS_NETWORK_STATE and INTERNET permissions to work properly"));
        return false;
    }

    private boolean _checkActivityDeclared(Application application, String className) {
        try {
            application.getPackageManager().getActivityInfo(new ComponentName(application, className), 1);
            return true;
        } catch (NameNotFoundException e) {
            ExceptionHandler.handleException(new Exception("Activity " + className + " is non-declared in manifest!"));
            return false;
        }
    }
}
