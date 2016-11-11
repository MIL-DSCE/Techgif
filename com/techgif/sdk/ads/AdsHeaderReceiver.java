package com.appsgeyser.sdk.ads;

import com.appsgeyser.sdk.ads.AdsLoader.AdsLoadingFinishedListener;
import com.appsgeyser.sdk.ads.AdsLoader.HeadersReceiver;
import com.appsgeyser.sdk.ads.behavior.BehaviorFactory;
import com.appsgeyser.sdk.ads.behavior.BehaviorVisitor;
import java.util.List;
import java.util.Map;

public class AdsHeaderReceiver implements AdsLoadingFinishedListener, HeadersReceiver {
    private AdView _adView;
    private AdsLoader _adsLoader;
    private Map<String, List<String>> _lastResponseHeaders;

    public AdsHeaderReceiver(AdView view, AdsLoader loader) {
        this._adsLoader = loader;
        this._adView = view;
    }

    public boolean onAdHeadersReceived(Map<String, List<String>> headers) {
        this._lastResponseHeaders = headers;
        return true;
    }

    public void onAdLoadFinished() {
        _applyBehaviors(new BehaviorFactory().createPostloadBehaviors(this._lastResponseHeaders));
    }

    private void _applyBehaviors(List<BehaviorVisitor> behaviors) {
        for (BehaviorVisitor visitor : behaviors) {
            this._adsLoader.acceptBehavior(visitor);
            this._adView.acceptBehavior(visitor);
        }
    }
}
