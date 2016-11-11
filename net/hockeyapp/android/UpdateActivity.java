package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.objects.ErrorObject;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;
import org.telegram.messenger.MessagesController;

public class UpdateActivity extends Activity implements UpdateActivityInterface, UpdateInfoListener, OnClickListener {
    private static final int DIALOG_ERROR_ID = 0;
    public static final String EXTRA_JSON = "json";
    public static final String EXTRA_URL = "url";
    private Context mContext;
    protected DownloadFileTask mDownloadTask;
    private ErrorObject mError;
    protected VersionHelper mVersionHelper;

    /* renamed from: net.hockeyapp.android.UpdateActivity.1 */
    class C03891 implements DialogInterface.OnClickListener {
        C03891() {
        }

        public void onClick(DialogInterface dialog, int id) {
            UpdateActivity.this.mError = null;
            dialog.cancel();
        }
    }

    /* renamed from: net.hockeyapp.android.UpdateActivity.2 */
    class C03902 implements DialogInterface.OnClickListener {
        final /* synthetic */ UpdateActivity val$updateActivity;

        C03902(UpdateActivity updateActivity) {
            this.val$updateActivity = updateActivity;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$updateActivity.prepareDownload();
        }
    }

    /* renamed from: net.hockeyapp.android.UpdateActivity.5 */
    class C03915 implements Runnable {
        C03915() {
        }

        public void run() {
            UpdateActivity.this.showDialog(UpdateActivity.DIALOG_ERROR_ID);
        }
    }

    /* renamed from: net.hockeyapp.android.UpdateActivity.6 */
    class C03926 implements Runnable {
        C03926() {
        }

        public void run() {
            UpdateActivity.this.showDialog(UpdateActivity.DIALOG_ERROR_ID);
        }
    }

    /* renamed from: net.hockeyapp.android.UpdateActivity.7 */
    class C03937 implements Runnable {
        C03937() {
        }

        public void run() {
            UpdateActivity.this.showDialog(UpdateActivity.DIALOG_ERROR_ID);
        }
    }

    /* renamed from: net.hockeyapp.android.UpdateActivity.3 */
    class C16663 extends DownloadFileListener {
        final /* synthetic */ String val$fileDate;
        final /* synthetic */ TextView val$versionLabel;
        final /* synthetic */ String val$versionString;

        C16663(TextView textView, String str, String str2) {
            this.val$versionLabel = textView;
            this.val$versionString = str;
            this.val$fileDate = str2;
        }

        public void downloadSuccessful(DownloadFileTask task) {
            if (task instanceof GetFileSizeTask) {
                long appSize = ((GetFileSizeTask) task).getSize();
                String appSizeString = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB";
                this.val$versionLabel.setText(UpdateActivity.this.getString(C0388R.string.hockeyapp_update_version_details_label, new Object[]{this.val$versionString, this.val$fileDate, appSizeString}));
            }
        }
    }

    /* renamed from: net.hockeyapp.android.UpdateActivity.4 */
    class C16674 extends DownloadFileListener {
        C16674() {
        }

        public void downloadFailed(DownloadFileTask task, Boolean userWantsRetry) {
            if (userWantsRetry.booleanValue()) {
                UpdateActivity.this.startDownloadTask();
            } else {
                UpdateActivity.this.enableUpdateButton();
            }
        }

        public void downloadSuccessful(DownloadFileTask task) {
            UpdateActivity.this.enableUpdateButton();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("App Update");
        setContentView(getLayoutView());
        this.mContext = this;
        this.mVersionHelper = new VersionHelper(this, getIntent().getStringExtra(EXTRA_JSON), this);
        configureView();
        this.mDownloadTask = (DownloadFileTask) getLastNonConfigurationInstance();
        if (this.mDownloadTask != null) {
            this.mDownloadTask.attach(this);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mDownloadTask != null) {
            this.mDownloadTask.detach();
        }
        return this.mDownloadTask;
    }

    protected Dialog onCreateDialog(int id) {
        return onCreateDialog(id, null);
    }

    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case DIALOG_ERROR_ID /*0*/:
                return new Builder(this).setMessage("An error has occured").setCancelable(false).setTitle("Error").setIcon(17301543).setPositiveButton("OK", new C03891()).create();
            default:
                return null;
        }
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_ERROR_ID /*0*/:
                AlertDialog messageDialogError = (AlertDialog) dialog;
                if (this.mError != null) {
                    messageDialogError.setMessage(this.mError.getMessage());
                } else {
                    messageDialogError.setMessage("An unknown error has occured.");
                }
            default:
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        enableUpdateButton();
        if (permissions.length != 0 && grantResults.length != 0 && requestCode == 1) {
            if (grantResults[DIALOG_ERROR_ID] == 0) {
                prepareDownload();
                return;
            }
            HockeyLog.warn("User denied write permission, can't continue with updater task.");
            UpdateManagerListener listener = UpdateManager.getLastListener();
            if (listener != null) {
                listener.onUpdatePermissionsNotGranted();
            } else {
                new Builder(this.mContext).setTitle(getString(C0388R.string.hockeyapp_permission_update_title)).setMessage(getString(C0388R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(C0388R.string.hockeyapp_permission_dialog_negative_button), null).setPositiveButton(getString(C0388R.string.hockeyapp_permission_dialog_positive_button), new C03902(this)).create().show();
            }
        }
    }

    public int getCurrentVersionCode() {
        int currentVersionCode = -1;
        try {
            return getPackageManager().getPackageInfo(getPackageName(), MessagesController.UPDATE_MASK_USER_PHONE).versionCode;
        } catch (NameNotFoundException e) {
            return currentVersionCode;
        }
    }

    @SuppressLint({"InflateParams"})
    public View getLayoutView() {
        return getLayoutInflater().inflate(C0388R.layout.hockeyapp_activity_update, null);
    }

    public void onClick(View v) {
        prepareDownload();
        v.setEnabled(false);
    }

    protected void configureView() {
        ((TextView) findViewById(C0388R.id.label_title)).setText(getAppName());
        TextView versionLabel = (TextView) findViewById(C0388R.id.label_version);
        String versionString = "Version " + this.mVersionHelper.getVersionString();
        String fileDate = this.mVersionHelper.getFileDateString();
        String appSizeString = "Unknown size";
        long appSize = this.mVersionHelper.getFileSizeBytes();
        if (appSize >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            Object[] objArr = new Object[1];
            objArr[DIALOG_ERROR_ID] = Float.valueOf(((float) appSize) / 1048576.0f);
            appSizeString = stringBuilder.append(String.format(Locale.US, "%.2f", objArr)).append(" MB").toString();
        } else {
            AsyncTaskUtils.execute(new GetFileSizeTask(this, getIntent().getStringExtra(EXTRA_URL), new C16663(versionLabel, versionString, fileDate)));
        }
        versionLabel.setText(getString(C0388R.string.hockeyapp_update_version_details_label, new Object[]{versionString, fileDate, appSizeString}));
        ((Button) findViewById(C0388R.id.button_update)).setOnClickListener(this);
        WebView webView = (WebView) findViewById(C0388R.id.web_update_details);
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.loadDataWithBaseURL(Constants.BASE_URL, getReleaseNotes(), "text/html", "utf-8", null);
    }

    protected String getReleaseNotes() {
        return this.mVersionHelper.getReleaseNotes(false);
    }

    protected void startDownloadTask() {
        startDownloadTask(getIntent().getStringExtra(EXTRA_URL));
    }

    protected void startDownloadTask(String url) {
        createDownloadTask(url, new C16674());
        AsyncTaskUtils.execute(this.mDownloadTask);
    }

    protected void createDownloadTask(String url, DownloadFileListener listener) {
        this.mDownloadTask = new DownloadFileTask(this, url, listener);
    }

    public void enableUpdateButton() {
        findViewById(C0388R.id.button_update).setEnabled(true);
    }

    public String getAppName() {
        try {
            PackageManager pm = getPackageManager();
            return pm.getApplicationLabel(pm.getApplicationInfo(getPackageName(), DIALOG_ERROR_ID)).toString();
        } catch (NameNotFoundException e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    private boolean isWriteExternalStorageSet(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    @SuppressLint({"InlinedApi"})
    private boolean isUnknownSourcesChecked() {
        try {
            if (VERSION.SDK_INT < 17 || VERSION.SDK_INT >= 21) {
                if (Secure.getInt(getContentResolver(), "install_non_market_apps") != 1) {
                    return false;
                }
                return true;
            } else if (Global.getInt(getContentResolver(), "install_non_market_apps") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SettingNotFoundException e) {
            return true;
        }
    }

    protected void prepareDownload() {
        if (!Util.isConnectedToNetwork(this.mContext)) {
            this.mError = new ErrorObject();
            this.mError.setMessage(getString(C0388R.string.hockeyapp_error_no_network_message));
            runOnUiThread(new C03915());
        } else if (isWriteExternalStorageSet(this.mContext)) {
            if (isUnknownSourcesChecked()) {
                startDownloadTask();
                return;
            }
            this.mError = new ErrorObject();
            this.mError.setMessage("The installation from unknown sources is not enabled. Please check the device settings.");
            runOnUiThread(new C03937());
        } else if (VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        } else {
            this.mError = new ErrorObject();
            this.mError.setMessage("The permission to access the external storage permission is not set. Please contact the developer.");
            runOnUiThread(new C03926());
        }
    }
}
