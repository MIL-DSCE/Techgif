package com.appsgeyser.sdk.ads;

import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.hasher.Hasher;

public abstract class BaseSecureJsInterface {
    protected boolean _checkSecurityCode(String hashCode) {
        Configuration config = Configuration.getInstance();
        return hashCode.equalsIgnoreCase(Hasher.md5(config.getAppGuid() + config.getApplicationId()));
    }
}
