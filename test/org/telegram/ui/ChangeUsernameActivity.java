package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_checkUsername;
import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;

public class ChangeUsernameActivity extends BaseFragment {
    private static final int done_button = 1;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private View doneButton;
    private EditText firstNameField;
    private String lastCheckName;
    private boolean lastNameAvailable;

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.2 */
    class C10212 implements OnTouchListener {
        C10212() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.3 */
    class C10223 implements OnEditorActionListener {
        C10223() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeUsernameActivity.this.doneButton == null) {
                return false;
            }
            ChangeUsernameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.4 */
    class C10234 implements TextWatcher {
        C10234() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChangeUsernameActivity.this.checkUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), false);
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.5 */
    class C10255 implements Runnable {
        final /* synthetic */ String val$name;

        /* renamed from: org.telegram.ui.ChangeUsernameActivity.5.1 */
        class C17801 implements RequestDelegate {

            /* renamed from: org.telegram.ui.ChangeUsernameActivity.5.1.1 */
            class C10241 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10241(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChangeUsernameActivity.this.checkReqId = 0;
                    if (ChangeUsernameActivity.this.lastCheckName != null && ChangeUsernameActivity.this.lastCheckName.equals(C10255.this.val$name)) {
                        if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                            TextView access$600 = ChangeUsernameActivity.this.checkTextView;
                            Object[] objArr = new Object[ChangeUsernameActivity.done_button];
                            objArr[0] = C10255.this.val$name;
                            access$600.setText(LocaleController.formatString("UsernameAvailable", C0691R.string.UsernameAvailable, objArr));
                            ChangeUsernameActivity.this.checkTextView.setTextColor(-14248148);
                            ChangeUsernameActivity.this.lastNameAvailable = true;
                            return;
                        }
                        ChangeUsernameActivity.this.checkTextView.setText(LocaleController.getString("UsernameInUse", C0691R.string.UsernameInUse));
                        ChangeUsernameActivity.this.checkTextView.setTextColor(-3198928);
                        ChangeUsernameActivity.this.lastNameAvailable = false;
                    }
                }
            }

            C17801() {
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C10241(error, response));
            }
        }

        C10255(String str) {
            this.val$name = str;
        }

        public void run() {
            TL_account_checkUsername req = new TL_account_checkUsername();
            req.username = this.val$name;
            ChangeUsernameActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(req, new C17801(), 2);
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.7 */
    class C10287 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        C10287(int i) {
            this.val$reqId = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.1 */
    class C17791 extends ActionBarMenuOnItemClick {
        C17791() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeUsernameActivity.this.finishFragment();
            } else if (id == ChangeUsernameActivity.done_button) {
                ChangeUsernameActivity.this.saveName();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity.6 */
    class C17816 implements RequestDelegate {
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: org.telegram.ui.ChangeUsernameActivity.6.1 */
        class C10261 implements Runnable {
            final /* synthetic */ User val$user;

            C10261(User user) {
                this.val$user = user;
            }

            public void run() {
                try {
                    C17816.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                ArrayList<User> users = new ArrayList();
                users.add(this.val$user);
                MessagesController.getInstance().putUsers(users, false);
                MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                UserConfig.saveConfig(true);
                ChangeUsernameActivity.this.finishFragment();
            }
        }

        /* renamed from: org.telegram.ui.ChangeUsernameActivity.6.2 */
        class C10272 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C10272(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                try {
                    C17816.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                ChangeUsernameActivity.this.showErrorAlert(this.val$error.text);
            }
        }

        C17816(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                AndroidUtilities.runOnUIThread(new C10261((User) response));
            } else {
                AndroidUtilities.runOnUIThread(new C10272(error));
            }
        }
    }

    public ChangeUsernameActivity() {
        this.checkReqId = 0;
        this.lastCheckName = null;
        this.checkRunnable = null;
        this.lastNameAvailable = false;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", C0691R.string.Username));
        this.actionBar.setActionBarMenuOnItemClick(new C17791());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user == null) {
            user = UserConfig.getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context);
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C10212());
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(-6842473);
        this.firstNameField.setTextColor(-14606047);
        this.firstNameField.setMaxLines(done_button);
        this.firstNameField.setLines(done_button);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", C0691R.string.UsernamePlaceholder));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setOnEditorActionListener(new C10223());
        ((LinearLayout) this.fragmentView).addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        if (!(user == null || user.username == null || user.username.length() <= 0)) {
            this.firstNameField.setText(user.username);
            this.firstNameField.setSelection(this.firstNameField.length());
        }
        this.checkTextView = new TextView(context);
        this.checkTextView.setTextSize(done_button, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        ((LinearLayout) this.fragmentView).addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        TextView helpTextView = new TextView(context);
        helpTextView.setTextSize(done_button, 15.0f);
        helpTextView.setTextColor(-9605774);
        helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", C0691R.string.UsernameHelp)));
        ((LinearLayout) this.fragmentView).addView(helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.firstNameField.addTextChangedListener(new C10234());
        this.checkTextView.setVisibility(8);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    private void showErrorAlert(String error) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            Object obj = -1;
            switch (error.hashCode()) {
                case -141887186:
                    if (error.equals("USERNAMES_UNAVAILABLE")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 288843630:
                    if (error.equals("USERNAME_INVALID")) {
                        obj = null;
                        break;
                    }
                    break;
                case 533175271:
                    if (error.equals("USERNAME_OCCUPIED")) {
                        obj = done_button;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("UsernameInvalid", C0691R.string.UsernameInvalid));
                    break;
                case done_button /*1*/:
                    builder.setMessage(LocaleController.getString("UsernameInUse", C0691R.string.UsernameInUse));
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    builder.setMessage(LocaleController.getString("FeatureUnavailable", C0691R.string.FeatureUnavailable));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private boolean checkUserName(String name, boolean alert) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (alert && name.length() == 0) {
            return true;
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", C0691R.string.UsernameInvalid));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        showErrorAlert(LocaleController.getString("UsernameInvalidStartNumber", C0691R.string.UsernameInvalidStartNumber));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", C0691R.string.UsernameInvalidStartNumber));
                        this.checkTextView.setTextColor(-3198928);
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        showErrorAlert(LocaleController.getString("UsernameInvalid", C0691R.string.UsernameInvalid));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", C0691R.string.UsernameInvalid));
                        this.checkTextView.setTextColor(-3198928);
                    }
                    return false;
                } else {
                    a += done_button;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (alert) {
                showErrorAlert(LocaleController.getString("UsernameInvalidShort", C0691R.string.UsernameInvalidShort));
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", C0691R.string.UsernameInvalidShort));
                this.checkTextView.setTextColor(-3198928);
            }
            return false;
        } else if (name.length() > 32) {
            if (alert) {
                showErrorAlert(LocaleController.getString("UsernameInvalidLong", C0691R.string.UsernameInvalidLong));
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", C0691R.string.UsernameInvalidLong));
                this.checkTextView.setTextColor(-3198928);
            }
            return false;
        } else if (alert) {
            return true;
        } else {
            String currentName = UserConfig.getCurrentUser().username;
            if (currentName == null) {
                currentName = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (name.equals(currentName)) {
                TextView textView = this.checkTextView;
                Object[] objArr = new Object[done_button];
                objArr[0] = name;
                textView.setText(LocaleController.formatString("UsernameAvailable", C0691R.string.UsernameAvailable, objArr));
                this.checkTextView.setTextColor(-14248148);
                return true;
            }
            this.checkTextView.setText(LocaleController.getString("UsernameChecking", C0691R.string.UsernameChecking));
            this.checkTextView.setTextColor(-9605774);
            this.lastCheckName = name;
            this.checkRunnable = new C10255(name);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    private void saveName() {
        if (checkUserName(this.firstNameField.getText().toString(), true)) {
            User user = UserConfig.getCurrentUser();
            if (getParentActivity() != null && user != null) {
                String currentName = user.username;
                if (currentName == null) {
                    currentName = TtmlNode.ANONYMOUS_REGION_ID;
                }
                String newName = this.firstNameField.getText().toString();
                if (currentName.equals(newName)) {
                    finishFragment();
                    return;
                }
                ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                TL_account_updateUsername req = new TL_account_updateUsername();
                req.username = newName;
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[done_button];
                objArr[0] = Integer.valueOf(done_button);
                instance.postNotificationName(i, objArr);
                int reqId = ConnectionsManager.getInstance().sendRequest(req, new C17816(progressDialog), 2);
                ConnectionsManager.getInstance().bindRequestToGuid(reqId, this.classGuid);
                progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new C10287(reqId));
                progressDialog.show();
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }
}
