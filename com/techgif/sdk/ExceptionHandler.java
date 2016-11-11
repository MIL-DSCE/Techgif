package com.appsgeyser.sdk;

public class ExceptionHandler {
    public static void handleException(Exception e) {
        Logger.ErrorLog(e.getMessage());
    }
}
