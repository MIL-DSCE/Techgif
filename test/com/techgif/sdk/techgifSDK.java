package com.appsgeyser.sdk;

import android.app.Activity;
import android.app.Application;
import com.appsgeyser.sdk.ads.AdView;
import com.appsgeyser.sdk.ads.FullScreenBanner;
import com.appsgeyser.sdk.analytics.Analytics;

public class AppsgeyserSDK {

    private static class SingletonHolder {
        public static final AppsgeyserSDK HOLDER_INSTANCE;

        private SingletonHolder() {
        }

        static {
            HOLDER_INSTANCE = new AppsgeyserSDK();
        }
    }

    public static void takeOff(Application application, String APIkey) {
        AppsgeyserSDKInternal.takeOff(application, APIkey);
    }

    public static void setActivity(Activity activity) {
        AppsgeyserSDKInternal.setActivity(activity);
    }

    public static Analytics getAnalytics() {
        return AppsgeyserSDKInternal.getAnalytics();
    }

    public static void enablePush() {
        AppsgeyserSDKInternal.enablePush();
    }

    public static void setAdView(AdView adView) {
        AppsgeyserSDKInternal.setAdView(adView);
    }

    public static AdView getAdView() {
        return AppsgeyserSDKInternal.getAdView();
    }

    public static FullScreenBanner getFullScreenBanner() {
        return AppsgeyserSDKInternal.getFullScreenBanner();
    }

    private AppsgeyserSDK() {
    }

    protected static AppsgeyserSDK getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }
}
