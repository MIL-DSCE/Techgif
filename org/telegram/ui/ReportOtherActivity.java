package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonOther;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class ReportOtherActivity extends BaseFragment {
    private static final int done_button = 1;
    private long dialog_id;
    private View doneButton;
    private EditText firstNameField;
    private View headerLabelView;

    /* renamed from: org.telegram.ui.ReportOtherActivity.2 */
    class C14142 implements OnTouchListener {
        C14142() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity.3 */
    class C14153 implements OnEditorActionListener {
        C14153() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ReportOtherActivity.this.doneButton == null) {
                return false;
            }
            ReportOtherActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity.4 */
    class C14164 implements Runnable {
        C14164() {
        }

        public void run() {
            if (ReportOtherActivity.this.firstNameField != null) {
                ReportOtherActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
            }
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity.1 */
    class C19401 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ReportOtherActivity.1.1 */
        class C19391 implements RequestDelegate {
            C19391() {
            }

            public void run(TLObject response, TL_error error) {
            }
        }

        C19401() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ReportOtherActivity.this.finishFragment();
            } else if (id == ReportOtherActivity.done_button && ReportOtherActivity.this.firstNameField.getText().length() != 0) {
                TL_account_reportPeer req = new TL_account_reportPeer();
                req.peer = MessagesController.getInputPeer((int) ReportOtherActivity.this.dialog_id);
                req.reason = new TL_inputReportReasonOther();
                req.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                ConnectionsManager.getInstance().sendRequest(req, new C19391());
                ReportOtherActivity.this.finishFragment();
            }
        }
    }

    public ReportOtherActivity(Bundle args) {
        super(args);
        this.dialog_id = getArguments().getLong("dialog_id", 0);
    }

    public View createView(Context context) {
        int i = 3;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", C0691R.string.ReportChat));
        this.actionBar.setActionBarMenuOnItemClick(new C19401());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C14142());
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(-6842473);
        this.firstNameField.setTextColor(-14606047);
        this.firstNameField.setMaxLines(3);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        EditText editText = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editText.setGravity(i);
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setOnEditorActionListener(new C14153());
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", C0691R.string.ReportChatDescription));
        this.firstNameField.setSelection(this.firstNameField.length());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new C14164(), 100);
        }
    }
}
