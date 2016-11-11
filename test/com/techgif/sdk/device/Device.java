package com.appsgeyser.sdk.device;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import com.appsgeyser.sdk.ExceptionHandler;

public class Device {
    public static String getDeviceId(Context context) {
        String deviceId = TtmlNode.ANONYMOUS_REGION_ID;
        try {
            if (context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
                ExceptionHandler.handleException(new Exception("Invlid permission. You have to grant READ_PHONE_STATE permissions to work properly"));
            } else {
                deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        return deviceId;
    }

    public static String getAndroidId(Context context) {
        String strResult = TtmlNode.ANONYMOUS_REGION_ID;
        try {
            strResult = Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        return strResult;
    }
}
