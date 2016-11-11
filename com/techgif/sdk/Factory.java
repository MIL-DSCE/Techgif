package com.appsgeyser.sdk;

public class Factory {
    private static Factory _instance;

    protected Factory() {
    }

    public static Factory getInstance() {
        if (_instance == null) {
            _instance = new Factory();
        }
        return _instance;
    }
}
