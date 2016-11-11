package net.hockeyapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class LoginManager {
    static final String LOGIN_EXIT_KEY = "net.hockeyapp.android.EXIT";
    public static final int LOGIN_MODE_ANONYMOUS = 0;
    public static final int LOGIN_MODE_EMAIL_ONLY = 1;
    public static final int LOGIN_MODE_EMAIL_PASSWORD = 2;
    public static final int LOGIN_MODE_VALIDATE = 3;
    private static String identifier;
    static LoginManagerListener listener;
    static Class<?> mainActivity;
    private static int mode;
    private static String secret;
    private static String urlString;
    private static Handler validateHandler;

    private static class LoginHandler extends Handler {
        private final WeakReference<Context> mWeakContext;

        public LoginHandler(Context context) {
            this.mWeakContext = new WeakReference(context);
        }

        public void handleMessage(Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.BUNDLE_SUCCESS);
            Context context = (Context) this.mWeakContext.get();
            if (context != null) {
                if (success) {
                    HockeyLog.verbose("HockeyAuth", "We authenticated or verified successfully");
                } else {
                    LoginManager.startLoginActivity(context);
                }
            }
        }
    }

    static {
        identifier = null;
        secret = null;
        validateHandler = null;
        urlString = null;
    }

    public static void register(Context context, String appSecret, int mode) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier, appSecret, mode, (Class) null);
    }

    public static void register(Context context, String appIdentifier, String appSecret, int mode, LoginManagerListener listener) {
        listener = listener;
        register(context, appIdentifier, appSecret, mode, (Class) null);
    }

    public static void register(Context context, String appIdentifier, String appSecret, int mode, Class<?> activity) {
        register(context, appIdentifier, appSecret, Constants.BASE_URL, mode, activity);
    }

    public static void register(Context context, String appIdentifier, String appSecret, String urlString, int mode, Class<?> activity) {
        if (context != null) {
            identifier = Util.sanitizeAppIdentifier(appIdentifier);
            secret = appSecret;
            urlString = urlString;
            mode = mode;
            mainActivity = activity;
            if (validateHandler == null) {
                validateHandler = new LoginHandler(context);
            }
            Constants.loadFromContext(context);
        }
    }

    public static void verifyLogin(Activity context, Intent intent) {
        if (intent != null && intent.getBooleanExtra(LOGIN_EXIT_KEY, false)) {
            context.finish();
        } else if (context != null && mode != 0) {
            SharedPreferences prefs = context.getSharedPreferences("net.hockeyapp.android.login", LOGIN_MODE_ANONYMOUS);
            if (prefs.getInt(LoginActivity.EXTRA_MODE, -1) != mode) {
                HockeyLog.verbose("HockeyAuth", "Mode has changed, require re-auth.");
                prefs.edit().remove("auid").remove("iuid").putInt(LoginActivity.EXTRA_MODE, mode).apply();
            }
            String auid = prefs.getString("auid", null);
            String iuid = prefs.getString("iuid", null);
            boolean notAuthenticated = auid == null && iuid == null;
            boolean auidMissing = auid == null && (mode == LOGIN_MODE_EMAIL_PASSWORD || mode == LOGIN_MODE_VALIDATE);
            boolean iuidMissing = iuid == null && mode == LOGIN_MODE_EMAIL_ONLY;
            if (notAuthenticated || auidMissing || iuidMissing) {
                HockeyLog.verbose("HockeyAuth", "Not authenticated or correct ID missing, re-authenticate.");
                startLoginActivity(context);
            } else if (mode == LOGIN_MODE_VALIDATE) {
                HockeyLog.verbose("HockeyAuth", "LOGIN_MODE_VALIDATE, Validate the user's info!");
                Map<String, String> params = new HashMap();
                if (auid != null) {
                    params.put("type", "auid");
                    params.put(TtmlNode.ATTR_ID, auid);
                } else if (iuid != null) {
                    params.put("type", "iuid");
                    params.put(TtmlNode.ATTR_ID, iuid);
                }
                LoginTask verifyTask = new LoginTask(context, validateHandler, getURLString(LOGIN_MODE_VALIDATE), LOGIN_MODE_VALIDATE, params);
                verifyTask.setShowProgressDialog(false);
                AsyncTaskUtils.execute(verifyTask);
            }
        }
    }

    private static void startLoginActivity(Context context) {
        Intent intent = new Intent();
        int tempMode = Boolean.valueOf(mode == LOGIN_MODE_VALIDATE).booleanValue() ? LOGIN_MODE_EMAIL_PASSWORD : mode;
        intent.setFlags(1342177280);
        intent.setClass(context, LoginActivity.class);
        intent.putExtra(UpdateFragment.FRAGMENT_URL, getURLString(tempMode));
        intent.putExtra(LoginActivity.EXTRA_MODE, tempMode);
        intent.putExtra(LoginActivity.EXTRA_SECRET, secret);
        context.startActivity(intent);
    }

    private static String getURLString(int mode) {
        String suffix = TtmlNode.ANONYMOUS_REGION_ID;
        if (mode == LOGIN_MODE_EMAIL_PASSWORD) {
            suffix = "authorize";
        } else if (mode == LOGIN_MODE_EMAIL_ONLY) {
            suffix = "check";
        } else if (mode == LOGIN_MODE_VALIDATE) {
            suffix = "validate";
        }
        return urlString + "api/3/apps/" + identifier + "/identity/" + suffix;
    }
}
