package com.appsgeyser.sdk.server.implementation;

import com.appsgeyser.sdk.ErrorInfo;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.server.ContentHandler;
import com.appsgeyser.sdk.server.ContentRequest;
import com.appsgeyser.sdk.server.ContentRequest.Method;
import com.appsgeyser.sdk.server.Response;

public class AppsgeyserServerClient {
    private static final int OK_RESPONSE = 200;
    private final Configuration _configuration;

    private static class SingletonHolder {
        public static final AppsgeyserServerClient HOLDER_INSTANCE;

        private SingletonHolder() {
        }

        static {
            HOLDER_INSTANCE = new AppsgeyserServerClient();
        }
    }

    /* renamed from: com.appsgeyser.sdk.server.implementation.AppsgeyserServerClient.1 */
    class C19641 extends ContentHandler {
        private final /* synthetic */ RegisterAppInstall val$installWatcher;
        private final /* synthetic */ String val$strGuid;

        C19641(RegisterAppInstall registerAppInstall, String str) {
            this.val$installWatcher = registerAppInstall;
            this.val$strGuid = str;
        }

        public void onContentRequestDone(ContentRequest request, Response response) {
            if (response.status() == AppsgeyserServerClient.OK_RESPONSE) {
                this.val$installWatcher.appGuidRegistered(this.val$strGuid);
            } else {
                this.val$installWatcher.appGuidNotRegistered(new ErrorInfo("Error occurs while registering app guid: " + response.status()));
            }
        }

        public void onContentRequestFailed(ContentRequest request, ErrorInfo error) {
            this.val$installWatcher.appGuidNotRegistered(error);
        }
    }

    /* renamed from: com.appsgeyser.sdk.server.implementation.AppsgeyserServerClient.2 */
    class C19652 extends ContentHandler {
        private final /* synthetic */ RegisterAppUsage val$usageWatcher;

        C19652(RegisterAppUsage registerAppUsage) {
            this.val$usageWatcher = registerAppUsage;
        }

        public void onContentRequestDone(ContentRequest request, Response response) {
            if (response.status() == AppsgeyserServerClient.OK_RESPONSE) {
                this.val$usageWatcher.appUsageRegistered();
            } else {
                this.val$usageWatcher.appUsageNotRegistered(new ErrorInfo("Error occurs while registering app usage: " + response.status()));
            }
        }

        public void onContentRequestFailed(ContentRequest request, ErrorInfo error) {
            this.val$usageWatcher.appUsageNotRegistered(error);
        }
    }

    private AppsgeyserServerClient() {
        this._configuration = Configuration.getInstance();
    }

    public static AppsgeyserServerClient getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public void sendAfterInstallInfo(String strGuid, RegisterAppInstall installWatcher) {
        ContentRequest request = new ContentRequest(new StringBuilder(String.valueOf(this._configuration.getRegisteredUrl())).append("?action=install&name=").append(this._configuration.getApplicationId()).append("&id=").append(strGuid).append("&system=android&sdk=1").toString());
        request.setMethod(Method.GET);
        request.addHeader(ContentRequest.DEFAULT_HEADER_CACHE);
        if (installWatcher != null) {
            request.setContentHandler(new C19641(installWatcher, strGuid));
        }
        request.enqueue();
    }

    public void sendUsageInfo(RegisterAppUsage usageWatcher) {
        ContentRequest request = new ContentRequest(new StringBuilder(String.valueOf(this._configuration.getAddUsageUrl())).append("?action=usage&name=").append(this._configuration.getApplicationId()).append("&id=").append(this._configuration.getAppGuid()).append("&system=android&sdk=1").toString());
        request.setMethod(Method.GET);
        request.addHeader(ContentRequest.DEFAULT_HEADER_CACHE);
        if (usageWatcher != null) {
            request.setContentHandler(new C19652(usageWatcher));
        }
        request.enqueue();
    }

    public void sendRequest(String url) {
        ContentRequest request = new ContentRequest(url);
        request.setMethod(Method.GET);
        request.addHeader(ContentRequest.DEFAULT_HEADER_CACHE);
        request.enqueue();
    }
}
