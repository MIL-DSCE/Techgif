package org.telegram.ui;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeNameActivity extends BaseFragment {
    private static final int done_button = 1;
    private View doneButton;
    private EditText firstNameField;
    private View headerLabelView;
    private EditText lastNameField;

    /* renamed from: org.telegram.ui.ChangeNameActivity.2 */
    class C09952 implements OnTouchListener {
        C09952() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity.3 */
    class C09963 implements OnEditorActionListener {
        C09963() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ChangeNameActivity.this.lastNameField.requestFocus();
            ChangeNameActivity.this.lastNameField.setSelection(ChangeNameActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity.4 */
    class C09974 implements OnEditorActionListener {
        C09974() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            ChangeNameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity.6 */
    class C09986 implements Runnable {
        C09986() {
        }

        public void run() {
            if (ChangeNameActivity.this.firstNameField != null) {
                ChangeNameActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ChangeNameActivity.this.firstNameField);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity.1 */
    class C17681 extends ActionBarMenuOnItemClick {
        C17681() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeNameActivity.this.finishFragment();
            } else if (id == ChangeNameActivity.done_button && ChangeNameActivity.this.firstNameField.getText().length() != 0) {
                ChangeNameActivity.this.saveName();
                ChangeNameActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity.5 */
    class C17695 implements RequestDelegate {
        C17695() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    public View createView(Context context) {
        int i = 5;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EditName", C0691R.string.EditName));
        this.actionBar.setActionBarMenuOnItemClick(new C17681());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user == null) {
            user = UserConfig.getCurrentUser();
        }
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C09952());
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(-6842473);
        this.firstNameField.setTextColor(-14606047);
        this.firstNameField.setMaxLines(done_button);
        this.firstNameField.setLines(done_button);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", C0691R.string.FirstName));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new C09963());
        this.lastNameField = new EditText(context);
        this.lastNameField.setTextSize(done_button, 18.0f);
        this.lastNameField.setHintTextColor(-6842473);
        this.lastNameField.setTextColor(-14606047);
        this.lastNameField.setMaxLines(done_button);
        this.lastNameField.setLines(done_button);
        this.lastNameField.setSingleLine(true);
        EditText editText = this.lastNameField;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        editText.setGravity(i);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", C0691R.string.LastName));
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        linearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new C09974());
        if (user != null) {
            this.firstNameField.setText(user.first_name);
            this.firstNameField.setSelection(this.firstNameField.length());
            this.lastNameField.setText(user.last_name);
        }
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    private void saveName() {
        User currentUser = UserConfig.getCurrentUser();
        if (currentUser != null && this.lastNameField.getText() != null && this.firstNameField.getText() != null) {
            String newFirst = this.firstNameField.getText().toString();
            String newLast = this.lastNameField.getText().toString();
            if (currentUser.first_name == null || !currentUser.first_name.equals(newFirst) || currentUser.last_name == null || !currentUser.last_name.equals(newLast)) {
                TL_account_updateProfile req = new TL_account_updateProfile();
                req.flags = 3;
                req.first_name = newFirst;
                currentUser.first_name = newFirst;
                req.last_name = newLast;
                currentUser.last_name = newLast;
                User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user != null) {
                    user.first_name = req.first_name;
                    user.last_name = req.last_name;
                }
                UserConfig.saveConfig(true);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[done_button];
                objArr[0] = Integer.valueOf(done_button);
                instance.postNotificationName(i, objArr);
                ConnectionsManager.getInstance().sendRequest(req, new C17695());
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new C09986(), 100);
        }
    }
}
