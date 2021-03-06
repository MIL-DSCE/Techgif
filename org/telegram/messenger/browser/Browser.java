package org.telegram.messenger.browser;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.ShareBroadcastReceiver;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsIntent.Builder;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    /* renamed from: org.telegram.messenger.browser.Browser.1 */
    static class C16971 implements ServiceConnectionCallback {
        C16971() {
        }

        public void onServiceConnected(CustomTabsClient client) {
            Browser.customTabsClient = client;
            if (MediaController.getInstance().canCustomTabs() && Browser.customTabsClient != null) {
                try {
                    Browser.customTabsClient.warmup(0);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        public void onServiceDisconnected() {
            Browser.customTabsClient = null;
        }
    }

    private static class NavigationCallback extends CustomTabsCallback {
        private NavigationCallback() {
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            FileLog.m11e("tmessages", "code = " + navigationEvent + " extras " + extras);
        }
    }

    private static CustomTabsSession getCurrentSession() {
        return customTabsCurrentSession == null ? null : (CustomTabsSession) customTabsCurrentSession.get();
    }

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference(session);
    }

    private static CustomTabsSession getSession() {
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(new NavigationCallback());
            setCurrentSession(customTabsSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        Activity currentActivity = null;
        if (VERSION.SDK_INT >= 15) {
            if (currentCustomTabsActivity != null) {
                currentActivity = (Activity) currentCustomTabsActivity.get();
            }
            if (!(currentActivity == null || currentActivity == activity)) {
                unbindCustomTabsService(currentActivity);
            }
            if (customTabsClient == null) {
                currentCustomTabsActivity = new WeakReference(activity);
                try {
                    if (TextUtils.isEmpty(customTabsPackageToBind)) {
                        customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(activity);
                        if (customTabsPackageToBind == null) {
                            return;
                        }
                    }
                    customTabsServiceConnection = new ServiceConnection(new C16971());
                    if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection)) {
                        customTabsServiceConnection = null;
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (VERSION.SDK_INT >= 15 && customTabsServiceConnection != null) {
            if ((currentCustomTabsActivity == null ? null : (Activity) currentCustomTabsActivity.get()) == activity) {
                currentCustomTabsActivity.clear();
            }
            try {
                activity.unbindService(customTabsServiceConnection);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            customTabsClient = null;
            customTabsSession = null;
        }
    }

    public static void openUrl(Context context, String url) {
        openUrl(context, Uri.parse(url), true);
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context != null && url != null) {
            openUrl(context, Uri.parse(url), allowCustom);
        }
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        if (context != null && uri != null) {
            try {
                boolean internalUri = isInternalUri(uri);
                if (VERSION.SDK_INT < 15 || !allowCustom || !MediaController.getInstance().canCustomTabs() || internalUri) {
                    Intent intent = new Intent("android.intent.action.VIEW", uri);
                    if (internalUri) {
                        intent.setComponent(new ComponentName(context.getPackageName(), LaunchActivity.class.getName()));
                    }
                    intent.putExtra("com.android.browser.application_id", context.getPackageName());
                    context.startActivity(intent);
                    return;
                }
                Intent share = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                share.setAction("android.intent.action.SEND");
                Builder builder = new Builder(getSession());
                builder.setToolbarColor(Theme.ACTION_BAR_COLOR);
                builder.setShowTitle(true);
                builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), C0691R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", C0691R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, share, 0), false);
                builder.build().launchUrl((Activity) context, uri);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static boolean isInternalUrl(String url) {
        return isInternalUri(Uri.parse(url));
    }

    public static boolean isInternalUri(Uri uri) {
        String host = uri.getHost();
        host = host != null ? host.toLowerCase() : TtmlNode.ANONYMOUS_REGION_ID;
        return "tg".equals(uri.getScheme()) || "telegram.me".equals(host) || "telegram.dog".equals(host);
    }
}
