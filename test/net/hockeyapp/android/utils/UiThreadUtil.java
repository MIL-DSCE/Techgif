package net.hockeyapp.android.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class UiThreadUtil {

    /* renamed from: net.hockeyapp.android.utils.UiThreadUtil.1 */
    class C04081 implements Runnable {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ int val$errorDialogId;
        final /* synthetic */ ProgressDialog val$progressDialog;

        C04081(ProgressDialog progressDialog, Activity activity, int i) {
            this.val$progressDialog = progressDialog;
            this.val$activity = activity;
            this.val$errorDialogId = i;
        }

        public void run() {
            if (this.val$progressDialog != null && this.val$progressDialog.isShowing()) {
                this.val$progressDialog.dismiss();
            }
            this.val$activity.showDialog(this.val$errorDialogId);
        }
    }

    /* renamed from: net.hockeyapp.android.utils.UiThreadUtil.2 */
    class C04092 implements Runnable {
        final /* synthetic */ ProgressDialog val$progressDialog;

        C04092(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run() {
            if (this.val$progressDialog != null && this.val$progressDialog.isShowing()) {
                this.val$progressDialog.dismiss();
            }
        }
    }

    /* renamed from: net.hockeyapp.android.utils.UiThreadUtil.3 */
    class C04103 implements Runnable {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ int val$flags;
        final /* synthetic */ String val$message;

        C04103(Activity activity, String str, int i) {
            this.val$activity = activity;
            this.val$message = str;
            this.val$flags = i;
        }

        public void run() {
            Toast.makeText(this.val$activity, this.val$message, this.val$flags).show();
        }
    }

    private static class WbUtilHolder {
        public static final UiThreadUtil INSTANCE;

        private WbUtilHolder() {
        }

        static {
            INSTANCE = new UiThreadUtil();
        }
    }

    private UiThreadUtil() {
    }

    public static UiThreadUtil getInstance() {
        return WbUtilHolder.INSTANCE;
    }

    public void dismissLoadingDialogAndDisplayError(WeakReference<Activity> weakActivity, ProgressDialog progressDialog, int errorDialogId) {
        if (weakActivity != null) {
            Activity activity = (Activity) weakActivity.get();
            if (activity != null) {
                activity.runOnUiThread(new C04081(progressDialog, activity, errorDialogId));
            }
        }
    }

    public void dismissLoading(WeakReference<Activity> weakActivity, ProgressDialog progressDialog) {
        if (weakActivity != null) {
            Activity activity = (Activity) weakActivity.get();
            if (activity != null) {
                activity.runOnUiThread(new C04092(progressDialog));
            }
        }
    }

    public void displayToastMessage(WeakReference<Activity> weakActivity, String message, int flags) {
        if (weakActivity != null) {
            Activity activity = (Activity) weakActivity.get();
            if (activity != null) {
                activity.runOnUiThread(new C04103(activity, message, flags));
            }
        }
    }
}
