package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v4.view.ViewCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_noPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.account_Password;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int abortPasswordRow;
    private TextView bottomButton;
    private TextView bottomTextView;
    private int changePasswordRow;
    private int changeRecoveryEmailRow;
    private account_Password currentPassword;
    private byte[] currentPasswordHash;
    private boolean destroyed;
    private ActionBarMenuItem doneItem;
    private String email;
    private boolean emailOnly;
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private ListView listView;
    private boolean loading;
    private EditText passwordEditText;
    private int passwordEmailVerifyDetailRow;
    private int passwordEnabledDetailRow;
    private boolean passwordEntered;
    private int passwordSetState;
    private int passwordSetupDetailRow;
    private ProgressDialog progressDialog;
    private FrameLayout progressView;
    private int rowCount;
    private ScrollView scrollView;
    private int setPasswordDetailRow;
    private int setPasswordRow;
    private int setRecoveryEmailRow;
    private int shadowRow;
    private Runnable shortPollRunnable;
    private TextView titleTextView;
    private int turnPasswordOffRow;
    private int type;
    private boolean waitingForEmail;

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.2 */
    class C14632 implements OnEditorActionListener {
        C14632() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5 && i != 6) {
                return false;
            }
            TwoStepVerificationActivity.this.processDone();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.3 */
    class C14643 implements Callback {
        C14643() {
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.4 */
    class C14684 implements OnClickListener {

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.4.2 */
        class C14672 implements DialogInterface.OnClickListener {
            C14672() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.email = TtmlNode.ANONYMOUS_REGION_ID;
                TwoStepVerificationActivity.this.setNewPassword(false);
            }
        }

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.4.1 */
        class C19551 implements RequestDelegate {

            /* renamed from: org.telegram.ui.TwoStepVerificationActivity.4.1.1 */
            class C14661 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                /* renamed from: org.telegram.ui.TwoStepVerificationActivity.4.1.1.1 */
                class C14651 implements DialogInterface.OnClickListener {
                    final /* synthetic */ TL_auth_passwordRecovery val$res;

                    C14651(TL_auth_passwordRecovery tL_auth_passwordRecovery) {
                        this.val$res = tL_auth_passwordRecovery;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(TwoStepVerificationActivity.done_button);
                        fragment.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                        fragment.currentPassword.email_unconfirmed_pattern = this.val$res.email_pattern;
                        fragment.passwordSetState = 4;
                        TwoStepVerificationActivity.this.presentFragment(fragment);
                    }
                }

                C14661(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    TwoStepVerificationActivity.this.needHideProgress();
                    if (this.val$error == null) {
                        TL_auth_passwordRecovery res = this.val$response;
                        Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                        objArr[0] = res.email_pattern;
                        builder.setMessage(LocaleController.formatString("RestoreEmailSent", C0691R.string.RestoreEmailSent, objArr));
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14651(res));
                        Dialog dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                        if (dialog != null) {
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                        }
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        String timeString;
                        int time = Utilities.parseInt(this.val$error.text).intValue();
                        if (time < 60) {
                            timeString = LocaleController.formatPluralString("Seconds", time);
                        } else {
                            timeString = LocaleController.formatPluralString("Minutes", time / 60);
                        }
                        TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                        String string = LocaleController.getString("AppName", C0691R.string.AppName);
                        Object[] objArr2 = new Object[TwoStepVerificationActivity.done_button];
                        objArr2[0] = timeString;
                        twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0691R.string.FloodWaitTime, objArr2));
                    } else {
                        TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0691R.string.AppName), this.val$error.text);
                    }
                }
            }

            C19551() {
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C14661(error, response));
            }
        }

        C14684() {
        }

        public void onClick(View v) {
            if (TwoStepVerificationActivity.this.type == 0) {
                if (TwoStepVerificationActivity.this.currentPassword.has_recovery) {
                    TwoStepVerificationActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(new TL_auth_requestPasswordRecovery(), new C19551(), 10);
                    return;
                }
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", C0691R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", C0691R.string.RestorePasswordNoEmailText));
            } else if (TwoStepVerificationActivity.this.passwordSetState == 4) {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", C0691R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestoreEmailTroubleText", C0691R.string.RestoreEmailTroubleText));
            } else {
                Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", C0691R.string.YourEmailSkipWarningText));
                builder.setTitle(LocaleController.getString("YourEmailSkipWarning", C0691R.string.YourEmailSkipWarning));
                builder.setPositiveButton(LocaleController.getString("YourEmailSkip", C0691R.string.YourEmailSkip), new C14672());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                TwoStepVerificationActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.5 */
    class C14695 implements OnTouchListener {
        C14695() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.6 */
    class C14716 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.6.1 */
        class C14701 implements DialogInterface.OnClickListener {
            C14701() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.setNewPassword(true);
            }
        }

        C14716() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TwoStepVerificationActivity fragment;
            if (i == TwoStepVerificationActivity.this.setPasswordRow || i == TwoStepVerificationActivity.this.changePasswordRow) {
                fragment = new TwoStepVerificationActivity(TwoStepVerificationActivity.done_button);
                fragment.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                fragment.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                TwoStepVerificationActivity.this.presentFragment(fragment);
            } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow || i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                fragment = new TwoStepVerificationActivity(TwoStepVerificationActivity.done_button);
                fragment.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                fragment.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                fragment.emailOnly = true;
                fragment.passwordSetState = 3;
                TwoStepVerificationActivity.this.presentFragment(fragment);
            } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow || i == TwoStepVerificationActivity.this.abortPasswordRow) {
                Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("TurnPasswordOffQuestion", C0691R.string.TurnPasswordOffQuestion));
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14701());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                TwoStepVerificationActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.7 */
    class C14727 implements Runnable {
        C14727() {
        }

        public void run() {
            if (TwoStepVerificationActivity.this.passwordEditText != null) {
                TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.9 */
    class C14759 implements Runnable {
        C14759() {
        }

        public void run() {
            if (TwoStepVerificationActivity.this.passwordEditText != null) {
                TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ boolean val$clear;
        final /* synthetic */ TL_account_updatePasswordSettings val$req;

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.10.1 */
        class C14591 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.ui.TwoStepVerificationActivity.10.1.1 */
            class C14571 implements DialogInterface.OnClickListener {
                C14571() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i2 = NotificationCenter.didSetTwoStepPassword;
                    Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                    objArr[0] = AnonymousClass10.this.val$req.new_settings.new_password_hash;
                    instance.postNotificationName(i2, objArr);
                    TwoStepVerificationActivity.this.finishFragment();
                }
            }

            /* renamed from: org.telegram.ui.TwoStepVerificationActivity.10.1.2 */
            class C14582 implements DialogInterface.OnClickListener {
                C14582() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i2 = NotificationCenter.didSetTwoStepPassword;
                    Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                    objArr[0] = AnonymousClass10.this.val$req.new_settings.new_password_hash;
                    instance.postNotificationName(i2, objArr);
                    TwoStepVerificationActivity.this.finishFragment();
                }
            }

            C14591(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                TwoStepVerificationActivity.this.needHideProgress();
                Builder builder;
                Dialog dialog;
                if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                    if (AnonymousClass10.this.val$clear) {
                        TwoStepVerificationActivity.this.currentPassword = null;
                        TwoStepVerificationActivity.this.currentPasswordHash = new byte[0];
                        TwoStepVerificationActivity.this.loadPasswordInfo(false);
                        TwoStepVerificationActivity.this.updateRows();
                    } else if (TwoStepVerificationActivity.this.getParentActivity() != null) {
                        builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14571());
                        builder.setMessage(LocaleController.getString("YourPasswordSuccessText", C0691R.string.YourPasswordSuccessText));
                        builder.setTitle(LocaleController.getString("YourPasswordSuccess", C0691R.string.YourPasswordSuccess));
                        dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                        if (dialog != null) {
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                        }
                    }
                } else if (this.val$error == null) {
                } else {
                    if (this.val$error.text.equals("EMAIL_UNCONFIRMED")) {
                        builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14582());
                        builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", C0691R.string.YourEmailAlmostThereText));
                        builder.setTitle(LocaleController.getString("YourEmailAlmostThere", C0691R.string.YourEmailAlmostThere));
                        dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                        if (dialog != null) {
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                        }
                    } else if (this.val$error.text.equals("EMAIL_INVALID")) {
                        TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0691R.string.AppName), LocaleController.getString("PasswordEmailInvalid", C0691R.string.PasswordEmailInvalid));
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        String timeString;
                        int time = Utilities.parseInt(this.val$error.text).intValue();
                        if (time < 60) {
                            timeString = LocaleController.formatPluralString("Seconds", time);
                        } else {
                            timeString = LocaleController.formatPluralString("Minutes", time / 60);
                        }
                        TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                        String string = LocaleController.getString("AppName", C0691R.string.AppName);
                        Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                        objArr[0] = timeString;
                        twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0691R.string.FloodWaitTime, objArr));
                    } else {
                        TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0691R.string.AppName), this.val$error.text);
                    }
                }
            }
        }

        AnonymousClass10(boolean z, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
            this.val$clear = z;
            this.val$req = tL_account_updatePasswordSettings;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C14591(error, response));
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.11 */
    class AnonymousClass11 implements RequestDelegate {
        final /* synthetic */ TL_account_getPasswordSettings val$req;

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.11.1 */
        class C14601 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C14601(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                TwoStepVerificationActivity.this.needHideProgress();
                if (this.val$error == null) {
                    TwoStepVerificationActivity.this.currentPasswordHash = AnonymousClass11.this.val$req.current_password_hash;
                    TwoStepVerificationActivity.this.passwordEntered = true;
                    AndroidUtilities.hideKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                    TwoStepVerificationActivity.this.updateRows();
                } else if (this.val$error.text.equals("PASSWORD_HASH_INVALID")) {
                    TwoStepVerificationActivity.this.onPasscodeError(true);
                } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(this.val$error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                    String string = LocaleController.getString("AppName", C0691R.string.AppName);
                    Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                    objArr[0] = timeString;
                    twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0691R.string.FloodWaitTime, objArr));
                } else {
                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0691R.string.AppName), this.val$error.text);
                }
            }
        }

        AnonymousClass11(TL_account_getPasswordSettings tL_account_getPasswordSettings) {
            this.val$req = tL_account_getPasswordSettings;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C14601(error));
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.1 */
    class C19541 extends ActionBarMenuOnItemClick {
        C19541() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                TwoStepVerificationActivity.this.finishFragment();
            } else if (id == TwoStepVerificationActivity.done_button) {
                TwoStepVerificationActivity.this.processDone();
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.8 */
    class C19568 implements RequestDelegate {
        final /* synthetic */ boolean val$silent;

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.8.1 */
        class C14741 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.ui.TwoStepVerificationActivity.8.1.1 */
            class C14731 implements Runnable {
                C14731() {
                }

                public void run() {
                    if (TwoStepVerificationActivity.this.shortPollRunnable != null) {
                        TwoStepVerificationActivity.this.loadPasswordInfo(true);
                        TwoStepVerificationActivity.this.shortPollRunnable = null;
                    }
                }
            }

            C14741(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = true;
                TwoStepVerificationActivity.this.loading = false;
                if (this.val$error == null) {
                    if (!C19568.this.val$silent) {
                        TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                        boolean z2 = TwoStepVerificationActivity.this.currentPassword != null || (this.val$response instanceof TL_account_noPassword);
                        twoStepVerificationActivity.passwordEntered = z2;
                    }
                    TwoStepVerificationActivity.this.currentPassword = (account_Password) this.val$response;
                    TwoStepVerificationActivity twoStepVerificationActivity2 = TwoStepVerificationActivity.this;
                    if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern.length() <= 0) {
                        z = false;
                    }
                    twoStepVerificationActivity2.waitingForEmail = z;
                    byte[] salt = new byte[(TwoStepVerificationActivity.this.currentPassword.new_salt.length + 8)];
                    Utilities.random.nextBytes(salt);
                    System.arraycopy(TwoStepVerificationActivity.this.currentPassword.new_salt, 0, salt, 0, TwoStepVerificationActivity.this.currentPassword.new_salt.length);
                    TwoStepVerificationActivity.this.currentPassword.new_salt = salt;
                }
                if (TwoStepVerificationActivity.this.type == 0 && !TwoStepVerificationActivity.this.destroyed && TwoStepVerificationActivity.this.shortPollRunnable == null) {
                    TwoStepVerificationActivity.this.shortPollRunnable = new C14731();
                    AndroidUtilities.runOnUIThread(TwoStepVerificationActivity.this.shortPollRunnable, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
                }
                TwoStepVerificationActivity.this.updateRows();
            }
        }

        C19568(boolean z) {
            this.val$silent = z;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C14741(error, response));
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? false : true;
        }

        public int getCount() {
            return (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) ? 0 : TwoStepVerificationActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                textCell.setTextColor(-14606047);
                if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                    textCell.setText(LocaleController.getString("ChangePassword", C0691R.string.ChangePassword), true);
                } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                    textCell.setText(LocaleController.getString("SetAdditionalPassword", C0691R.string.SetAdditionalPassword), true);
                } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textCell.setText(LocaleController.getString("TurnPasswordOff", C0691R.string.TurnPasswordOff), true);
                } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    textCell.setText(LocaleController.getString("ChangeRecoveryEmail", C0691R.string.ChangeRecoveryEmail), TwoStepVerificationActivity.this.abortPasswordRow != -1);
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textCell.setText(LocaleController.getString("SetRecoveryEmail", C0691R.string.SetRecoveryEmail), false);
                } else if (i == TwoStepVerificationActivity.this.abortPasswordRow) {
                    textCell.setTextColor(-2995895);
                    textCell.setText(LocaleController.getString("AbortPassword", C0691R.string.AbortPassword), false);
                }
            } else if (viewType == TwoStepVerificationActivity.done_button) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("SetAdditionalPasswordInfo", C0691R.string.SetAdditionalPasswordInfo));
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                } else if (i == TwoStepVerificationActivity.this.shadowRow) {
                    ((TextInfoPrivacyCell) view).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                } else if (i == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                    r2 = (TextInfoPrivacyCell) view;
                    r3 = new Object[TwoStepVerificationActivity.done_button];
                    r3[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                    r2.setText(LocaleController.formatString("EmailPasswordConfirmText", C0691R.string.EmailPasswordConfirmText, r3));
                    view.setBackgroundResource(C0691R.drawable.greydivider_top);
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("EnabledPasswordText", C0691R.string.EnabledPasswordText));
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                } else if (i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                    r2 = (TextInfoPrivacyCell) view;
                    r3 = new Object[TwoStepVerificationActivity.done_button];
                    r3[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                    r2.setText(LocaleController.formatString("PendingEmailText", C0691R.string.PendingEmailText, r3));
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow || i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                return TwoStepVerificationActivity.done_button;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEmpty() {
            return TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null;
        }
    }

    public TwoStepVerificationActivity(int type) {
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.type = type;
        if (type == 0) {
            loadPasswordInfo(false);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
            if (this.shortPollRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
                this.shortPollRunnable = null;
            }
            this.destroyed = true;
        }
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C19541());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(-986896);
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView);
        LayoutParams layoutParams = (LayoutParams) this.scrollView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.scrollView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(done_button);
        this.scrollView.addView(linearLayout);
        LayoutParams layoutParams2 = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        linearLayout.setLayoutParams(layoutParams2);
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(-9079435);
        this.titleTextView.setTextSize(done_button, 18.0f);
        this.titleTextView.setGravity(done_button);
        linearLayout.addView(this.titleTextView);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.titleTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = done_button;
        layoutParams3.topMargin = AndroidUtilities.dp(38.0f);
        this.titleTextView.setLayoutParams(layoutParams3);
        this.passwordEditText = new EditText(context);
        this.passwordEditText.setTextSize(done_button, 20.0f);
        this.passwordEditText.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        this.passwordEditText.setMaxLines(done_button);
        this.passwordEditText.setLines(done_button);
        this.passwordEditText.setGravity(done_button);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(129);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        AndroidUtilities.clearCursorDrawable(this.passwordEditText);
        linearLayout.addView(this.passwordEditText);
        layoutParams3 = (LinearLayout.LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams3.topMargin = AndroidUtilities.dp(32.0f);
        layoutParams3.height = AndroidUtilities.dp(36.0f);
        layoutParams3.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.gravity = 51;
        layoutParams3.width = -1;
        this.passwordEditText.setLayoutParams(layoutParams3);
        this.passwordEditText.setOnEditorActionListener(new C14632());
        this.passwordEditText.setCustomSelectionActionModeCallback(new C14643());
        this.bottomTextView = new TextView(context);
        this.bottomTextView.setTextColor(-9079435);
        this.bottomTextView.setTextSize(done_button, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", C0691R.string.YourEmailInfo));
        linearLayout.addView(this.bottomTextView);
        layoutParams3 = (LinearLayout.LayoutParams) this.bottomTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        layoutParams3.topMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        layoutParams3.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(40.0f);
        this.bottomTextView.setLayoutParams(layoutParams3);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2);
        layoutParams3 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -1;
        linearLayout2.setLayoutParams(layoutParams3);
        this.bottomButton = new TextView(context);
        this.bottomButton.setTextColor(-11697229);
        this.bottomButton.setTextSize(done_button, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", C0691R.string.YourEmailSkip));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout2.addView(this.bottomButton);
        layoutParams3 = (LinearLayout.LayoutParams) this.bottomButton.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 80;
        layoutParams3.bottomMargin = AndroidUtilities.dp(14.0f);
        layoutParams3.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(40.0f);
        this.bottomButton.setLayoutParams(layoutParams3);
        this.bottomButton.setOnClickListener(new C14684());
        if (this.type == 0) {
            this.progressView = new FrameLayout(context);
            frameLayout.addView(this.progressView);
            layoutParams = (LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            this.progressView.setLayoutParams(layoutParams);
            this.progressView.setOnTouchListener(new C14695());
            this.progressView.addView(new ProgressBar(context));
            layoutParams = (LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.gravity = 17;
            this.progressView.setLayoutParams(layoutParams);
            this.listView = new ListView(context);
            this.listView.setDivider(null);
            this.listView.setEmptyView(this.progressView);
            this.listView.setDividerHeight(0);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDrawSelectorOnTop(true);
            frameLayout.addView(this.listView);
            layoutParams = (LayoutParams) this.listView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 48;
            this.listView.setLayoutParams(layoutParams);
            ListView listView = this.listView;
            android.widget.ListAdapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            listView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new C14716());
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerification", C0691R.string.TwoStepVerification));
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", C0691R.string.PleaseEnterCurrentPassword));
        } else if (this.type == done_button) {
            setPasswordSetState(this.passwordSetState);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            if (!(args == null || args.length <= 0 || args[0] == null)) {
                this.currentPasswordHash = (byte[]) args[0];
            }
            loadPasswordInfo(false);
            updateRows();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.type == done_button) {
            AndroidUtilities.runOnUIThread(new C14727(), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type == done_button) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void loadPasswordInfo(boolean silent) {
        if (!silent) {
            this.loading = true;
        }
        ConnectionsManager.getInstance().sendRequest(new TL_account_getPassword(), new C19568(silent), 10);
    }

    private void setPasswordSetState(int state) {
        int i = 4;
        if (this.passwordEditText != null) {
            this.passwordSetState = state;
            if (this.passwordSetState == 0) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", C0691R.string.YourPassword));
                if (this.currentPassword instanceof TL_account_noPassword) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", C0691R.string.PleaseEnterFirstPassword));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", C0691R.string.PleaseEnterPassword));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == done_button) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", C0691R.string.YourPassword));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", C0691R.string.PleaseReEnterPassword));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", C0691R.string.PasswordHint));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", C0691R.string.PasswordHintText));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", C0691R.string.RecoveryEmail));
                this.titleTextView.setText(LocaleController.getString("YourEmail", C0691R.string.YourEmail));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                TextView textView = this.bottomButton;
                if (!this.emailOnly) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (this.passwordSetState == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", C0691R.string.PasswordRecovery));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", C0691R.string.PasswordCode));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", C0691R.string.RestoreEmailSentInfo));
                TextView textView2 = this.bottomButton;
                Object[] objArr = new Object[done_button];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern;
                textView2.setText(LocaleController.formatString("RestoreEmailTrouble", C0691R.string.RestoreEmailTrouble, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.abortPasswordRow = -1;
        this.passwordSetupDetailRow = -1;
        this.passwordEnabledDetailRow = -1;
        this.passwordEmailVerifyDetailRow = -1;
        this.shadowRow = -1;
        if (!(this.loading || this.currentPassword == null)) {
            int i;
            if (this.currentPassword instanceof TL_account_noPassword) {
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.passwordSetupDetailRow = i;
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.abortPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.shadowRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.setPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.setPasswordDetailRow = i;
                }
            } else if (this.currentPassword instanceof TL_account_password) {
                i = this.rowCount;
                this.rowCount = i + done_button;
                this.changePasswordRow = i;
                i = this.rowCount;
                this.rowCount = i + done_button;
                this.turnPasswordOffRow = i;
                if (this.currentPassword.has_recovery) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.changeRecoveryEmailRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.setRecoveryEmailRow = i;
                }
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.passwordEmailVerifyDetailRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.passwordEnabledDetailRow = i;
                }
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.passwordEntered) {
            if (this.listView != null) {
                this.listView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(this.progressView);
            }
            if (this.passwordEditText != null) {
                this.doneItem.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
                return;
            }
            return;
        }
        if (this.listView != null) {
            this.listView.setEmptyView(null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.progressView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.doneItem.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", C0691R.string.ForgotPassword));
            if (this.currentPassword.hint == null || this.currentPassword.hint.length() <= 0) {
                this.passwordEditText.setHint(TtmlNode.ANONYMOUS_REGION_ID);
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new C14759(), 200);
        }
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(getParentActivity());
            this.progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    private void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    private boolean isValidEmail(String text) {
        if (text == null || text.length() < 3) {
            return false;
        }
        int dot = text.lastIndexOf(46);
        int dog = text.lastIndexOf(64);
        if (dot < 0 || dog < 0 || dot < dog) {
            return false;
        }
        return true;
    }

    private void showAlertWithText(String title, String text) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void setNewPassword(boolean clear) {
        TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
        req.current_password_hash = this.currentPasswordHash;
        req.new_settings = new TL_account_passwordInputSettings();
        if (!clear) {
            TL_account_passwordInputSettings tL_account_passwordInputSettings;
            if (this.firstPassword != null && this.firstPassword.length() > 0) {
                byte[] newPasswordBytes = null;
                try {
                    newPasswordBytes = this.firstPassword.getBytes(C0747C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                byte[] new_salt = this.currentPassword.new_salt;
                byte[] hash = new byte[((new_salt.length * 2) + newPasswordBytes.length)];
                System.arraycopy(new_salt, 0, hash, 0, new_salt.length);
                System.arraycopy(newPasswordBytes, 0, hash, new_salt.length, newPasswordBytes.length);
                System.arraycopy(new_salt, 0, hash, hash.length - new_salt.length, new_salt.length);
                tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= done_button;
                req.new_settings.hint = this.hint;
                req.new_settings.new_password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                req.new_settings.new_salt = new_salt;
            }
            if (this.email.length() > 0) {
                tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 2;
                req.new_settings.email = this.email;
            }
        } else if (this.waitingForEmail && (this.currentPassword instanceof TL_account_noPassword)) {
            req.new_settings.flags = 2;
            req.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
            req.current_password_hash = new byte[0];
        } else {
            req.new_settings.flags = 3;
            req.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
            req.new_settings.new_password_hash = new byte[0];
            req.new_settings.new_salt = new byte[0];
            req.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
        }
        needShowProgress();
        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass10(clear, req), 10);
    }

    private void processDone() {
        if (this.type == 0) {
            if (!this.passwordEntered) {
                String oldPassword = this.passwordEditText.getText().toString();
                if (oldPassword.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                byte[] oldPasswordBytes = null;
                try {
                    oldPasswordBytes = oldPassword.getBytes(C0747C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                needShowProgress();
                byte[] hash = new byte[((this.currentPassword.current_salt.length * 2) + oldPasswordBytes.length)];
                System.arraycopy(this.currentPassword.current_salt, 0, hash, 0, this.currentPassword.current_salt.length);
                System.arraycopy(oldPasswordBytes, 0, hash, this.currentPassword.current_salt.length, oldPasswordBytes.length);
                System.arraycopy(this.currentPassword.current_salt, 0, hash, hash.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
                TL_account_getPasswordSettings req = new TL_account_getPasswordSettings();
                req.current_password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass11(req), 10);
            }
        } else if (this.type != done_button) {
        } else {
            if (this.passwordSetState == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", C0691R.string.ReEnterYourPasscode));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(done_button);
            } else if (this.passwordSetState == done_button) {
                if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    setPasswordSetState(2);
                    return;
                }
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", C0691R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                onPasscodeError(true);
            } else if (this.passwordSetState == 2) {
                this.hint = this.passwordEditText.getText().toString();
                if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", C0691R.string.PasswordAsHintError), 0).show();
                    } catch (Throwable e22) {
                        FileLog.m13e("tmessages", e22);
                    }
                    onPasscodeError(false);
                } else if (this.currentPassword.has_recovery) {
                    this.email = TtmlNode.ANONYMOUS_REGION_ID;
                    setNewPassword(false);
                } else {
                    setPasswordSetState(3);
                }
            } else if (this.passwordSetState == 3) {
                this.email = this.passwordEditText.getText().toString();
                if (isValidEmail(this.email)) {
                    setNewPassword(false);
                } else {
                    onPasscodeError(false);
                }
            } else if (this.passwordSetState == 4) {
                String code = this.passwordEditText.getText().toString();
                if (code.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                TL_auth_recoverPassword req2 = new TL_auth_recoverPassword();
                req2.code = code;
                ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {

                    /* renamed from: org.telegram.ui.TwoStepVerificationActivity.12.1 */
                    class C14621 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        /* renamed from: org.telegram.ui.TwoStepVerificationActivity.12.1.1 */
                        class C14611 implements DialogInterface.OnClickListener {
                            C14611() {
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                                TwoStepVerificationActivity.this.finishFragment();
                            }
                        }

                        C14621(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            if (this.val$error == null) {
                                Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14611());
                                builder.setMessage(LocaleController.getString("PasswordReset", C0691R.string.PasswordReset));
                                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                                Dialog dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            } else if (this.val$error.text.startsWith("CODE_INVALID")) {
                                TwoStepVerificationActivity.this.onPasscodeError(true);
                            } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                                String timeString;
                                int time = Utilities.parseInt(this.val$error.text).intValue();
                                if (time < 60) {
                                    timeString = LocaleController.formatPluralString("Seconds", time);
                                } else {
                                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                }
                                TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                                String string = LocaleController.getString("AppName", C0691R.string.AppName);
                                Object[] objArr = new Object[TwoStepVerificationActivity.done_button];
                                objArr[0] = timeString;
                                twoStepVerificationActivity.showAlertWithText(string, LocaleController.formatString("FloodWaitTime", C0691R.string.FloodWaitTime, objArr));
                            } else {
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0691R.string.AppName), this.val$error.text);
                            }
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new C14621(error));
                    }
                }, 10);
            }
        }
    }

    private void onPasscodeError(boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }
}
