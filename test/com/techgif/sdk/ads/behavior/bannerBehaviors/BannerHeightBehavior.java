package com.appsgeyser.sdk.ads.behavior.bannerBehaviors;

import com.appsgeyser.sdk.ads.AdView;

public class BannerHeightBehavior extends AdViewBehavior {
    private int _height;

    public BannerHeightBehavior(int h) {
        this._height = h;
    }

    public void visit(AdView view) {
        view.setHeight(this._height);
    }
}
