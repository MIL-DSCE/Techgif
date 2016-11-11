package com.appsgeyser.sdk.ads.behavior.bannerBehaviors;

import com.appsgeyser.sdk.ads.AdView;

public class BannerWidthBehavior extends AdViewBehavior {
    private int _width;

    public BannerWidthBehavior(int w) {
        this._width = w;
    }

    public void visit(AdView view) {
        view.setWidth(this._width);
    }
}
