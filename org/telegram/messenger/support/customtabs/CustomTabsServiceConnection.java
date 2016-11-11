package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import org.telegram.messenger.support.customtabs.ICustomTabsService.Stub;

public abstract class CustomTabsServiceConnection implements ServiceConnection {

    /* renamed from: org.telegram.messenger.support.customtabs.CustomTabsServiceConnection.1 */
    class C17201 extends CustomTabsClient {
        C17201(ICustomTabsService x0, ComponentName x1) {
            super(x0, x1);
        }
    }

    public abstract void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient);

    public final void onServiceConnected(ComponentName name, IBinder service) {
        onCustomTabsServiceConnected(name, new C17201(Stub.asInterface(service), name));
    }
}
