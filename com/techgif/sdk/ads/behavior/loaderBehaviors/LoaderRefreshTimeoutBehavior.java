package com.appsgeyser.sdk.ads.behavior.loaderBehaviors;

import com.appsgeyser.sdk.ads.AdsLoader;

public class LoaderRefreshTimeoutBehavior extends LoaderBehavior {
    float _timeOut;

    public LoaderRefreshTimeoutBehavior(float timeout) {
        this._timeOut = timeout;
    }

    public void visit(AdsLoader loader) {
        loader.setRefreshTimeout(this._timeOut);
    }
}
