package com.appsgeyser.sdk;

public class StringUtils {
    public static boolean isNotNullOrEmptyString(String string) {
        return string != null && string.length() > 0;
    }
}
