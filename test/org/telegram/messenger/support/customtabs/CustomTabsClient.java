package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import org.telegram.messenger.support.customtabs.ICustomTabsCallback.Stub;

public class CustomTabsClient {
    private final ICustomTabsService mService;
    private final ComponentName mServiceComponentName;

    /* renamed from: org.telegram.messenger.support.customtabs.CustomTabsClient.1 */
    class C19981 extends Stub {
        final /* synthetic */ CustomTabsCallback val$callback;

        C19981(CustomTabsCallback customTabsCallback) {
            this.val$callback = customTabsCallback;
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            if (this.val$callback != null) {
                this.val$callback.onNavigationEvent(navigationEvent, extras);
            }
        }

        public void extraCallback(String callbackName, Bundle args) throws RemoteException {
            if (this.val$callback != null) {
                this.val$callback.extraCallback(callbackName, args);
            }
        }
    }

    CustomTabsClient(ICustomTabsService service, ComponentName componentName) {
        this.mService = service;
        this.mServiceComponentName = componentName;
    }

    public static boolean bindCustomTabsService(Context context, String packageName, CustomTabsServiceConnection connection) {
        Intent intent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);
        }
        return context.bindService(intent, connection, 33);
    }

    public boolean warmup(long flags) {
        try {
            return this.mService.warmup(flags);
        } catch (RemoteException e) {
            return false;
        }
    }

    public CustomTabsSession newSession(CustomTabsCallback callback) {
        Stub wrapper = new C19981(callback);
        try {
            if (this.mService.newSession(wrapper)) {
                return new CustomTabsSession(this.mService, wrapper, this.mServiceComponentName);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public Bundle extraCommand(String commandName, Bundle args) {
        try {
            return this.mService.extraCommand(commandName, args);
        } catch (RemoteException e) {
            return null;
        }
    }
}
