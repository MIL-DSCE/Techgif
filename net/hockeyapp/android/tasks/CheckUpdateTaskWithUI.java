package net.hockeyapp.android.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import net.hockeyapp.android.C0388R;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.UpdateFragment;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionCache;
import org.json.JSONArray;

public class CheckUpdateTaskWithUI extends CheckUpdateTask {
    private Activity mActivity;
    private AlertDialog mDialog;
    protected boolean mIsDialogRequired;

    /* renamed from: net.hockeyapp.android.tasks.CheckUpdateTaskWithUI.1 */
    class C04011 implements OnClickListener {
        C04011() {
        }

        public void onClick(DialogInterface dialog, int which) {
            CheckUpdateTaskWithUI.this.cleanUp();
            if (CheckUpdateTaskWithUI.this.listener != null) {
                CheckUpdateTaskWithUI.this.listener.onCancel();
            }
        }
    }

    /* renamed from: net.hockeyapp.android.tasks.CheckUpdateTaskWithUI.2 */
    class C04022 implements OnClickListener {
        final /* synthetic */ JSONArray val$updateInfo;

        C04022(JSONArray jSONArray) {
            this.val$updateInfo = jSONArray;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (CheckUpdateTaskWithUI.this.getCachingEnabled()) {
                VersionCache.setVersionInfo(CheckUpdateTaskWithUI.this.mActivity, "[]");
            }
            WeakReference<Activity> weakActivity = new WeakReference(CheckUpdateTaskWithUI.this.mActivity);
            if (Util.fragmentsSupported().booleanValue() && Util.runsOnTablet(weakActivity).booleanValue()) {
                CheckUpdateTaskWithUI.this.showUpdateFragment(this.val$updateInfo);
            } else {
                CheckUpdateTaskWithUI.this.startUpdateIntent(this.val$updateInfo, Boolean.valueOf(false));
            }
        }
    }

    public CheckUpdateTaskWithUI(WeakReference<Activity> weakActivity, String urlString, String appIdentifier, UpdateManagerListener listener, boolean isDialogRequired) {
        super(weakActivity, urlString, appIdentifier, listener);
        this.mActivity = null;
        this.mDialog = null;
        this.mIsDialogRequired = false;
        if (weakActivity != null) {
            this.mActivity = (Activity) weakActivity.get();
        }
        this.mIsDialogRequired = isDialogRequired;
    }

    public void detach() {
        super.detach();
        this.mActivity = null;
        if (this.mDialog != null) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    protected void onPostExecute(JSONArray updateInfo) {
        super.onPostExecute(updateInfo);
        if (updateInfo != null && this.mIsDialogRequired) {
            showDialog(updateInfo);
        }
    }

    @TargetApi(11)
    private void showDialog(JSONArray updateInfo) {
        if (getCachingEnabled()) {
            HockeyLog.verbose("HockeyUpdate", "Caching is enabled. Setting version to cached one.");
            VersionCache.setVersionInfo(this.mActivity, updateInfo.toString());
        }
        if (this.mActivity != null && !this.mActivity.isFinishing()) {
            Builder builder = new Builder(this.mActivity);
            builder.setTitle(C0388R.string.hockeyapp_update_dialog_title);
            if (this.mandatory.booleanValue()) {
                String appName = Util.getAppName(this.mActivity);
                Toast.makeText(this.mActivity, String.format(this.mActivity.getString(C0388R.string.hockeyapp_update_mandatory_toast), new Object[]{appName}), 1).show();
                startUpdateIntent(updateInfo, Boolean.valueOf(true));
                return;
            }
            builder.setMessage(C0388R.string.hockeyapp_update_dialog_message);
            builder.setNegativeButton(C0388R.string.hockeyapp_update_dialog_negative_button, new C04011());
            builder.setPositiveButton(C0388R.string.hockeyapp_update_dialog_positive_button, new C04022(updateInfo));
            this.mDialog = builder.create();
            this.mDialog.show();
        }
    }

    @TargetApi(11)
    private void showUpdateFragment(JSONArray updateInfo) {
        if (this.mActivity != null) {
            FragmentTransaction fragmentTransaction = this.mActivity.getFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            Fragment existingFragment = this.mActivity.getFragmentManager().findFragmentByTag("hockey_update_dialog");
            if (existingFragment != null) {
                fragmentTransaction.remove(existingFragment);
            }
            fragmentTransaction.addToBackStack(null);
            Class<? extends UpdateFragment> fragmentClass = UpdateFragment.class;
            if (this.listener != null) {
                fragmentClass = this.listener.getUpdateFragmentClass();
            }
            try {
                ((DialogFragment) fragmentClass.getMethod("newInstance", new Class[]{JSONArray.class, String.class}).invoke(null, new Object[]{updateInfo, getURLString("apk")})).show(fragmentTransaction, "hockey_update_dialog");
            } catch (Exception e) {
                HockeyLog.error("An exception happened while showing the update fragment:");
                e.printStackTrace();
                HockeyLog.error("Showing update activity instead.");
                startUpdateIntent(updateInfo, Boolean.valueOf(false));
            }
        }
    }

    private void startUpdateIntent(JSONArray updateInfo, Boolean finish) {
        Class<?> activityClass = null;
        if (this.listener != null) {
            activityClass = this.listener.getUpdateActivityClass();
        }
        if (activityClass == null) {
            activityClass = UpdateActivity.class;
        }
        if (this.mActivity != null) {
            Intent intent = new Intent();
            intent.setClass(this.mActivity, activityClass);
            intent.putExtra(UpdateActivity.EXTRA_JSON, updateInfo.toString());
            intent.putExtra(UpdateFragment.FRAGMENT_URL, getURLString("apk"));
            this.mActivity.startActivity(intent);
            if (finish.booleanValue()) {
                this.mActivity.finish();
            }
        }
        cleanUp();
    }

    protected void cleanUp() {
        super.cleanUp();
        this.mActivity = null;
        this.mDialog = null;
    }
}
