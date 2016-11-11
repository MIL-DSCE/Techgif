package com.appsgeyser.sdk.ads.behavior.loaderBehaviors;

import com.appsgeyser.sdk.ads.AdsLoader;

public class LoaderHideTimeoutBehavior extends LoaderBehavior {
    float _timeOut;

    public LoaderHideTimeoutBehavior(float timeout) {
        this._timeOut = timeout;
    }

    public void visit(AdsLoader loader) {
        loader.setHideTimeout(this._timeOut);
    }
}
