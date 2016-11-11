package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import net.hockeyapp.android.Constants;

public class DeviceUtils {

    private static class DeviceUtilsHolder {
        public static final DeviceUtils INSTANCE;

        private DeviceUtilsHolder() {
        }

        static {
            INSTANCE = new DeviceUtils();
        }
    }

    private DeviceUtils() {
    }

    public static DeviceUtils getInstance() {
        return DeviceUtilsHolder.INSTANCE;
    }

    public int getCurrentVersionCode(Context context) {
        return Integer.parseInt(Constants.APP_VERSION);
    }

    public String getAppName(Context context) {
        if (context == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            return pm.getApplicationLabel(pm.getApplicationInfo(context.getPackageName(), 0)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }
}
