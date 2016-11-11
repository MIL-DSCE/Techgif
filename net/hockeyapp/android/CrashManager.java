package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.objects.CrashManagerUserInput;
import net.hockeyapp.android.objects.CrashMetaData;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;
import org.telegram.ui.Components.VideoPlayer;

public class CrashManager {
    private static final String ALWAYS_SEND_KEY = "always_send_crash_reports";
    private static final int STACK_TRACES_FOUND_CONFIRMED = 2;
    private static final int STACK_TRACES_FOUND_NEW = 1;
    private static final int STACK_TRACES_FOUND_NONE = 0;
    private static boolean didCrashInLastSession;
    private static String identifier;
    private static long initializeTimestamp;
    private static boolean submitting;
    private static String urlString;

    /* renamed from: net.hockeyapp.android.CrashManager.1 */
    static class C03701 implements FilenameFilter {
        C03701() {
        }

        public boolean accept(File dir, String filename) {
            return filename.endsWith(".stacktrace");
        }
    }

    /* renamed from: net.hockeyapp.android.CrashManager.2 */
    static class C03712 implements OnClickListener {
        final /* synthetic */ boolean val$ignoreDefaultHandler;
        final /* synthetic */ CrashManagerListener val$listener;
        final /* synthetic */ WeakReference val$weakContext;

        C03712(CrashManagerListener crashManagerListener, WeakReference weakReference, boolean z) {
            this.val$listener = crashManagerListener;
            this.val$weakContext = weakReference;
            this.val$ignoreDefaultHandler = z;
        }

        public void onClick(DialogInterface dialog, int which) {
            CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputDontSend, null, this.val$listener, this.val$weakContext, this.val$ignoreDefaultHandler);
        }
    }

    /* renamed from: net.hockeyapp.android.CrashManager.3 */
    static class C03723 implements OnClickListener {
        final /* synthetic */ boolean val$ignoreDefaultHandler;
        final /* synthetic */ CrashManagerListener val$listener;
        final /* synthetic */ WeakReference val$weakContext;

        C03723(CrashManagerListener crashManagerListener, WeakReference weakReference, boolean z) {
            this.val$listener = crashManagerListener;
            this.val$weakContext = weakReference;
            this.val$ignoreDefaultHandler = z;
        }

        public void onClick(DialogInterface dialog, int which) {
            CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputAlwaysSend, null, this.val$listener, this.val$weakContext, this.val$ignoreDefaultHandler);
        }
    }

    /* renamed from: net.hockeyapp.android.CrashManager.4 */
    static class C03734 implements OnClickListener {
        final /* synthetic */ boolean val$ignoreDefaultHandler;
        final /* synthetic */ CrashManagerListener val$listener;
        final /* synthetic */ WeakReference val$weakContext;

        C03734(CrashManagerListener crashManagerListener, WeakReference weakReference, boolean z) {
            this.val$listener = crashManagerListener;
            this.val$weakContext = weakReference;
            this.val$ignoreDefaultHandler = z;
        }

        public void onClick(DialogInterface dialog, int which) {
            CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputSend, null, this.val$listener, this.val$weakContext, this.val$ignoreDefaultHandler);
        }
    }

    /* renamed from: net.hockeyapp.android.CrashManager.5 */
    static class C03745 extends Thread {
        final /* synthetic */ CrashMetaData val$crashMetaData;
        final /* synthetic */ CrashManagerListener val$listener;
        final /* synthetic */ WeakReference val$weakContext;

        C03745(WeakReference weakReference, CrashManagerListener crashManagerListener, CrashMetaData crashMetaData) {
            this.val$weakContext = weakReference;
            this.val$listener = crashManagerListener;
            this.val$crashMetaData = crashMetaData;
        }

        public void run() {
            CrashManager.submitStackTraces(this.val$weakContext, this.val$listener, this.val$crashMetaData);
            CrashManager.submitting = false;
        }
    }

    /* renamed from: net.hockeyapp.android.CrashManager.6 */
    static class C03756 implements FilenameFilter {
        C03756() {
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(".stacktrace");
        }
    }

    /* renamed from: net.hockeyapp.android.CrashManager.7 */
    static /* synthetic */ class C03767 {
        static final /* synthetic */ int[] $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput;

        static {
            $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput = new int[CrashManagerUserInput.values().length];
            try {
                $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[CrashManagerUserInput.CrashManagerUserInputDontSend.ordinal()] = CrashManager.STACK_TRACES_FOUND_NEW;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[CrashManagerUserInput.CrashManagerUserInputAlwaysSend.ordinal()] = CrashManager.STACK_TRACES_FOUND_CONFIRMED;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[CrashManagerUserInput.CrashManagerUserInputSend.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static {
        identifier = null;
        urlString = null;
        submitting = false;
        didCrashInLastSession = false;
    }

    public static void register(Context context) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier);
    }

    public static void register(Context context, String appIdentifier) {
        register(context, Constants.BASE_URL, appIdentifier, null);
    }

    public static void register(Context context, String appIdentifier, CrashManagerListener listener) {
        register(context, Constants.BASE_URL, appIdentifier, listener);
    }

    public static void register(Context context, String urlString, String appIdentifier, CrashManagerListener listener) {
        initialize(context, urlString, appIdentifier, listener, false);
        execute(context, listener);
    }

    public static void initialize(Context context, String appIdentifier, CrashManagerListener listener) {
        initialize(context, Constants.BASE_URL, appIdentifier, listener, true);
    }

    public static void initialize(Context context, String urlString, String appIdentifier, CrashManagerListener listener) {
        initialize(context, urlString, appIdentifier, listener, true);
    }

    public static void execute(Context context, CrashManagerListener listener) {
        boolean z;
        boolean z2 = true;
        if (listener == null || !listener.ignoreDefaultHandler()) {
            z = false;
        } else {
            z = true;
        }
        Boolean ignoreDefaultHandler = Boolean.valueOf(z);
        WeakReference<Context> weakContext = new WeakReference(context);
        int foundOrSend = hasStackTraces(weakContext);
        if (foundOrSend == STACK_TRACES_FOUND_NEW) {
            didCrashInLastSession = true;
            if (context instanceof Activity) {
                z2 = false;
            }
            Boolean autoSend = Boolean.valueOf(Boolean.valueOf(z2).booleanValue() | PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ALWAYS_SEND_KEY, false));
            if (listener != null) {
                autoSend = Boolean.valueOf(Boolean.valueOf(autoSend.booleanValue() | listener.shouldAutoUploadCrashes()).booleanValue() | listener.onCrashesFound());
                listener.onNewCrashesFound();
            }
            if (autoSend.booleanValue()) {
                sendCrashes(weakContext, listener, ignoreDefaultHandler.booleanValue());
            } else {
                showDialog(weakContext, listener, ignoreDefaultHandler.booleanValue());
            }
        } else if (foundOrSend == STACK_TRACES_FOUND_CONFIRMED) {
            if (listener != null) {
                listener.onConfirmedCrashesFound();
            }
            sendCrashes(weakContext, listener, ignoreDefaultHandler.booleanValue());
        } else {
            registerHandler(weakContext, listener, ignoreDefaultHandler.booleanValue());
        }
    }

    public static int hasStackTraces(WeakReference<Context> weakContext) {
        String[] filenames = searchForStackTraces();
        List<String> confirmedFilenames = null;
        if (filenames == null || filenames.length <= 0) {
            return 0;
        }
        try {
            confirmedFilenames = getConfirmedFilenames(weakContext);
        } catch (Exception e) {
        }
        if (confirmedFilenames == null) {
            return STACK_TRACES_FOUND_NEW;
        }
        int length = filenames.length;
        for (int i = 0; i < length; i += STACK_TRACES_FOUND_NEW) {
            if (!confirmedFilenames.contains(filenames[i])) {
                return STACK_TRACES_FOUND_NEW;
            }
        }
        return STACK_TRACES_FOUND_CONFIRMED;
    }

    public static boolean didCrashInLastSession() {
        return didCrashInLastSession;
    }

    public static CrashDetails getLastCrashDetails() {
        if (Constants.FILES_PATH == null || !didCrashInLastSession()) {
            return null;
        }
        File[] files = new File(Constants.FILES_PATH + "/").listFiles(new C03701());
        long lastModification = 0;
        File lastModifiedFile = null;
        int length = files.length;
        for (int i = 0; i < length; i += STACK_TRACES_FOUND_NEW) {
            File file = files[i];
            if (file.lastModified() > lastModification) {
                lastModification = file.lastModified();
                lastModifiedFile = file;
            }
        }
        if (lastModifiedFile == null || !lastModifiedFile.exists()) {
            return null;
        }
        try {
            return CrashDetails.fromFile(lastModifiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void submitStackTraces(WeakReference<Context> weakContext, CrashManagerListener listener) {
        submitStackTraces(weakContext, listener, null);
    }

    public static void submitStackTraces(WeakReference<Context> weakContext, CrashManagerListener listener, CrashMetaData crashMetaData) {
        String[] list = searchForStackTraces();
        Boolean successful = Boolean.valueOf(false);
        if (list != null && list.length > 0) {
            HockeyLog.debug("Found " + list.length + " stacktrace(s).");
            int index = 0;
            while (true) {
                int length = list.length;
                if (index < r0) {
                    HttpURLConnection urlConnection = null;
                    try {
                        String filename = list[index];
                        String stacktrace = contentsOfFile(weakContext, filename);
                        if (stacktrace.length() > 0) {
                            HockeyLog.debug("Transmitting crash data: \n" + stacktrace);
                            String userID = contentsOfFile(weakContext, filename.replace(".stacktrace", ".user"));
                            String contact = contentsOfFile(weakContext, filename.replace(".stacktrace", ".contact"));
                            if (crashMetaData != null) {
                                String crashMetaDataUserID = crashMetaData.getUserID();
                                if (!TextUtils.isEmpty(crashMetaDataUserID)) {
                                    userID = crashMetaDataUserID;
                                }
                                String crashMetaDataContact = crashMetaData.getUserEmail();
                                if (!TextUtils.isEmpty(crashMetaDataContact)) {
                                    contact = crashMetaDataContact;
                                }
                            }
                            String applicationLog = contentsOfFile(weakContext, filename.replace(".stacktrace", ".description"));
                            String description = crashMetaData != null ? crashMetaData.getUserDescription() : TtmlNode.ANONYMOUS_REGION_ID;
                            if (!TextUtils.isEmpty(applicationLog)) {
                                Object[] objArr;
                                if (TextUtils.isEmpty(description)) {
                                    objArr = new Object[STACK_TRACES_FOUND_NEW];
                                    objArr[0] = applicationLog;
                                    description = String.format("Log:\n%s", objArr);
                                } else {
                                    objArr = new Object[STACK_TRACES_FOUND_CONFIRMED];
                                    objArr[0] = description;
                                    objArr[STACK_TRACES_FOUND_NEW] = applicationLog;
                                    description = String.format("%s\n\nLog:\n%s", objArr);
                                }
                            }
                            Map<String, String> parameters = new HashMap();
                            parameters.put("raw", stacktrace);
                            parameters.put("userID", userID);
                            parameters.put("contact", contact);
                            parameters.put("description", description);
                            parameters.put("sdk", Constants.SDK_NAME);
                            parameters.put("sdk_version", BuildConfig.VERSION_NAME);
                            urlConnection = new HttpURLConnectionBuilder(getURLString()).setRequestMethod("POST").writeFormFields(parameters).build();
                            int responseCode = urlConnection.getResponseCode();
                            boolean z = responseCode == 202 || responseCode == 201;
                            successful = Boolean.valueOf(z);
                        }
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (successful.booleanValue()) {
                            HockeyLog.debug("Transmission succeeded");
                            deleteStackTrace(weakContext, list[index]);
                            if (listener != null) {
                                listener.onCrashesSent();
                                deleteRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                            }
                        } else {
                            HockeyLog.debug("Transmission failed, will retry on next register() call");
                            if (listener != null) {
                                listener.onCrashesNotSent();
                                updateRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (successful.booleanValue()) {
                            HockeyLog.debug("Transmission succeeded");
                            deleteStackTrace(weakContext, list[index]);
                            if (listener != null) {
                                listener.onCrashesSent();
                                deleteRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                            }
                        } else {
                            HockeyLog.debug("Transmission failed, will retry on next register() call");
                            if (listener != null) {
                                listener.onCrashesNotSent();
                                updateRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                            }
                        }
                    } catch (Throwable th) {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (successful.booleanValue()) {
                            HockeyLog.debug("Transmission succeeded");
                            deleteStackTrace(weakContext, list[index]);
                            if (listener != null) {
                                listener.onCrashesSent();
                                deleteRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                            }
                        } else {
                            HockeyLog.debug("Transmission failed, will retry on next register() call");
                            if (listener != null) {
                                listener.onCrashesNotSent();
                                updateRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                            }
                        }
                    }
                    index += STACK_TRACES_FOUND_NEW;
                } else {
                    return;
                }
            }
        }
    }

    public static void deleteStackTraces(WeakReference<Context> weakContext) {
        String[] list = searchForStackTraces();
        if (list != null && list.length > 0) {
            HockeyLog.debug("Found " + list.length + " stacktrace(s).");
            for (int index = 0; index < list.length; index += STACK_TRACES_FOUND_NEW) {
                if (weakContext != null) {
                    try {
                        HockeyLog.debug("Delete stacktrace " + list[index] + ".");
                        deleteStackTrace(weakContext, list[index]);
                        Context context = (Context) weakContext.get();
                        if (context != null) {
                            context.deleteFile(list[index]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean handleUserInput(CrashManagerUserInput userInput, CrashMetaData userProvidedMetaData, CrashManagerListener listener, WeakReference<Context> weakContext, boolean ignoreDefaultHandler) {
        switch (C03767.$SwitchMap$net$hockeyapp$android$objects$CrashManagerUserInput[userInput.ordinal()]) {
            case STACK_TRACES_FOUND_NEW /*1*/:
                if (listener != null) {
                    listener.onUserDeniedCrashes();
                }
                deleteStackTraces(weakContext);
                registerHandler(weakContext, listener, ignoreDefaultHandler);
                return true;
            case STACK_TRACES_FOUND_CONFIRMED /*2*/:
                Context context = null;
                if (weakContext != null) {
                    context = (Context) weakContext.get();
                }
                if (context == null) {
                    return false;
                }
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ALWAYS_SEND_KEY, true).apply();
                sendCrashes(weakContext, listener, ignoreDefaultHandler, userProvidedMetaData);
                return true;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                sendCrashes(weakContext, listener, ignoreDefaultHandler, userProvidedMetaData);
                return true;
            default:
                return false;
        }
    }

    public static void resetAlwaysSend(WeakReference<Context> weakContext) {
        if (weakContext != null) {
            Context context = (Context) weakContext.get();
            if (context != null) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(ALWAYS_SEND_KEY).apply();
            }
        }
    }

    private static void initialize(Context context, String urlString, String appIdentifier, CrashManagerListener listener, boolean registerHandler) {
        boolean z = false;
        if (context != null) {
            if (initializeTimestamp == 0) {
                initializeTimestamp = System.currentTimeMillis();
            }
            urlString = urlString;
            identifier = Util.sanitizeAppIdentifier(appIdentifier);
            didCrashInLastSession = false;
            Constants.loadFromContext(context);
            if (identifier == null) {
                identifier = Constants.APP_PACKAGE;
            }
            if (registerHandler) {
                if (listener != null && listener.ignoreDefaultHandler()) {
                    z = true;
                }
                registerHandler(new WeakReference(context), listener, Boolean.valueOf(z).booleanValue());
            }
        }
    }

    private static void showDialog(WeakReference<Context> weakContext, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        Context context = null;
        if (weakContext != null) {
            context = (Context) weakContext.get();
        }
        if (context != null) {
            if (listener == null || !listener.onHandleAlertView()) {
                Builder builder = new Builder(context);
                builder.setTitle(getAlertTitle(context));
                builder.setMessage(C0388R.string.hockeyapp_crash_dialog_message);
                builder.setNegativeButton(C0388R.string.hockeyapp_crash_dialog_negative_button, new C03712(listener, weakContext, ignoreDefaultHandler));
                builder.setNeutralButton(C0388R.string.hockeyapp_crash_dialog_neutral_button, new C03723(listener, weakContext, ignoreDefaultHandler));
                builder.setPositiveButton(C0388R.string.hockeyapp_crash_dialog_positive_button, new C03734(listener, weakContext, ignoreDefaultHandler));
                builder.create().show();
            }
        }
    }

    private static String getAlertTitle(Context context) {
        String appTitle = Util.getAppName(context);
        String message = context.getString(C0388R.string.hockeyapp_crash_dialog_title);
        Object[] objArr = new Object[STACK_TRACES_FOUND_NEW];
        objArr[0] = appTitle;
        return String.format(message, objArr);
    }

    private static void sendCrashes(WeakReference<Context> weakContext, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        sendCrashes(weakContext, listener, ignoreDefaultHandler, null);
    }

    private static void sendCrashes(WeakReference<Context> weakContext, CrashManagerListener listener, boolean ignoreDefaultHandler, CrashMetaData crashMetaData) {
        saveConfirmedStackTraces(weakContext);
        registerHandler(weakContext, listener, ignoreDefaultHandler);
        Context ctx = (Context) weakContext.get();
        if ((ctx == null || Util.isConnectedToNetwork(ctx)) && !submitting) {
            submitting = true;
            new C03745(weakContext, listener, crashMetaData).start();
        }
    }

    private static void registerHandler(WeakReference<Context> weakReference, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        if (TextUtils.isEmpty(Constants.APP_VERSION) || TextUtils.isEmpty(Constants.APP_PACKAGE)) {
            HockeyLog.debug("Exception handler not set because version or package is null.");
            return;
        }
        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            HockeyLog.debug("Current handler class = " + currentHandler.getClass().getName());
        }
        if (currentHandler instanceof ExceptionHandler) {
            ((ExceptionHandler) currentHandler).setListener(listener);
        } else {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, listener, ignoreDefaultHandler));
        }
    }

    private static String getURLString() {
        return urlString + "api/2/apps/" + identifier + "/crashes/";
    }

    private static void updateRetryCounter(WeakReference<Context> weakContext, String filename, int maxRetryAttempts) {
        if (maxRetryAttempts != -1 && weakContext != null) {
            Context context = (Context) weakContext.get();
            if (context != null) {
                SharedPreferences preferences = context.getSharedPreferences(Constants.SDK_NAME, 0);
                Editor editor = preferences.edit();
                int retryCounter = preferences.getInt("RETRY_COUNT: " + filename, 0);
                if (retryCounter >= maxRetryAttempts) {
                    deleteStackTrace(weakContext, filename);
                    deleteRetryCounter(weakContext, filename, maxRetryAttempts);
                    return;
                }
                editor.putInt("RETRY_COUNT: " + filename, retryCounter + STACK_TRACES_FOUND_NEW);
                editor.apply();
            }
        }
    }

    private static void deleteRetryCounter(WeakReference<Context> weakContext, String filename, int maxRetryAttempts) {
        if (weakContext != null) {
            Context context = (Context) weakContext.get();
            if (context != null) {
                Editor editor = context.getSharedPreferences(Constants.SDK_NAME, 0).edit();
                editor.remove("RETRY_COUNT: " + filename);
                editor.apply();
            }
        }
    }

    private static void deleteStackTrace(WeakReference<Context> weakContext, String filename) {
        if (weakContext != null) {
            Context context = (Context) weakContext.get();
            if (context != null) {
                context.deleteFile(filename);
                context.deleteFile(filename.replace(".stacktrace", ".user"));
                context.deleteFile(filename.replace(".stacktrace", ".contact"));
                context.deleteFile(filename.replace(".stacktrace", ".description"));
            }
        }
    }

    private static String contentsOfFile(WeakReference<Context> weakContext, String filename) {
        IOException e;
        Throwable th;
        if (weakContext != null) {
            Context context = (Context) weakContext.get();
            if (context != null) {
                StringBuilder contents = new StringBuilder();
                BufferedReader reader = null;
                try {
                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
                    while (true) {
                        try {
                            String line = reader2.readLine();
                            if (line == null) {
                                break;
                            }
                            contents.append(line);
                            contents.append(System.getProperty("line.separator"));
                        } catch (FileNotFoundException e2) {
                            reader = reader2;
                        } catch (IOException e3) {
                            e = e3;
                            reader = reader2;
                        } catch (Throwable th2) {
                            th = th2;
                            reader = reader2;
                        }
                    }
                    if (reader2 != null) {
                        try {
                            reader2.close();
                            reader = reader2;
                        } catch (IOException e4) {
                            reader = reader2;
                        }
                    }
                } catch (FileNotFoundException e5) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e6) {
                        }
                    }
                    return contents.toString();
                } catch (IOException e7) {
                    e = e7;
                    try {
                        e.printStackTrace();
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e8) {
                            }
                        }
                        return contents.toString();
                    } catch (Throwable th3) {
                        th = th3;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e9) {
                            }
                        }
                        throw th;
                    }
                }
                return contents.toString();
            }
        }
        return null;
    }

    private static void saveConfirmedStackTraces(WeakReference<Context> weakContext) {
        if (weakContext != null) {
            Context context = (Context) weakContext.get();
            if (context != null) {
                try {
                    String[] filenames = searchForStackTraces();
                    Editor editor = context.getSharedPreferences(Constants.SDK_NAME, 0).edit();
                    editor.putString("ConfirmedFilenames", joinArray(filenames, "|"));
                    editor.apply();
                } catch (Exception e) {
                }
            }
        }
    }

    private static String joinArray(String[] array, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        for (int index = 0; index < array.length; index += STACK_TRACES_FOUND_NEW) {
            buffer.append(array[index]);
            if (index < array.length - 1) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    private static String[] searchForStackTraces() {
        if (Constants.FILES_PATH != null) {
            HockeyLog.debug("Looking for exceptions in: " + Constants.FILES_PATH);
            File dir = new File(Constants.FILES_PATH + "/");
            if (dir.mkdir() || dir.exists()) {
                return dir.list(new C03756());
            }
            return new String[0];
        }
        HockeyLog.debug("Can't search for exception as file path is null.");
        return null;
    }

    private static List<String> getConfirmedFilenames(WeakReference<Context> weakContext) {
        if (weakContext == null) {
            return null;
        }
        Context context = (Context) weakContext.get();
        if (context != null) {
            return Arrays.asList(context.getSharedPreferences(Constants.SDK_NAME, 0).getString("ConfirmedFilenames", TtmlNode.ANONYMOUS_REGION_ID).split("\\|"));
        }
        return null;
    }

    public static long getInitializeTimestamp() {
        return initializeTimestamp;
    }
}
