package com.appsgeyser.sdk.analytics;

import com.appsgeyser.sdk.ErrorInfo;
import com.appsgeyser.sdk.Logger;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.server.implementation.AppsgeyserServerClient;
import com.appsgeyser.sdk.server.implementation.RegisterAppInstall;

public class Analytics {
    private Configuration _configuration;
    private AppsgeyserServerClient _serverClient;

    /* renamed from: com.appsgeyser.sdk.analytics.Analytics.1 */
    class C15171 extends RegisterAppInstall {
        C15171() {
        }

        public void appGuidRegistered(String strGuid) {
            Analytics.this._serverClient.sendUsageInfo(null);
            Logger.DebugLog("App guid was sent");
        }

        public void appGuidNotRegistered(ErrorInfo err) {
            Logger.ErrorLog("App was not registered: " + err.getMessage());
        }
    }

    public Analytics() {
        this._configuration = Configuration.getInstance();
        this._serverClient = AppsgeyserServerClient.getInstance();
    }

    public void ActivityStarted() {
        _sendActivityStartedInfo();
    }

    private void _sendActivityStartedInfo() {
        if (this._configuration.isRegistered()) {
            this._serverClient.sendUsageInfo(null);
            Logger.DebugLog("App usage was sent");
            return;
        }
        this._configuration.registerNew();
        this._serverClient.sendAfterInstallInfo(this._configuration.getAppGuid(), new C15171());
    }
}
