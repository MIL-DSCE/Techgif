package com.appsgeyser.sdk.ads.behavior.loaderBehaviors;

import com.appsgeyser.sdk.ads.AdsLoader;
import com.appsgeyser.sdk.ads.behavior.BehaviorFactory.ClickBehavior;

public class LoaderClickBehavior extends LoaderBehavior {
    ClickBehavior _clickBehavior;

    public LoaderClickBehavior(ClickBehavior behavior) {
        this._clickBehavior = behavior;
    }

    public void visit(AdsLoader loader) {
        loader.changeClickBehavior(this._clickBehavior);
    }
}
