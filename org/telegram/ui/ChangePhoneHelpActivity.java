package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;

public class ChangePhoneHelpActivity extends BaseFragment {

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity.2 */
    class C10182 implements OnTouchListener {
        C10182() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity.3 */
    class C10203 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangePhoneHelpActivity.3.1 */
        class C10191 implements DialogInterface.OnClickListener {
            C10191() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
            }
        }

        C10203() {
        }

        public void onClick(View v) {
            if (ChangePhoneHelpActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChangePhoneHelpActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setMessage(LocaleController.getString("PhoneNumberAlert", C0691R.string.PhoneNumberAlert));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10191());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                ChangePhoneHelpActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity.1 */
    class C17781 extends ActionBarMenuOnItemClick {
        C17781() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangePhoneHelpActivity.this.finishFragment();
            }
        }
    }

    public View createView(Context context) {
        String value;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        User user = UserConfig.getCurrentUser();
        if (user == null || user.phone == null || user.phone.length() == 0) {
            value = LocaleController.getString("NumberUnknown", C0691R.string.NumberUnknown);
        } else {
            value = PhoneFormat.getInstance().format("+" + user.phone);
        }
        this.actionBar.setTitle(value);
        this.actionBar.setActionBarMenuOnItemClick(new C17781());
        this.fragmentView = new RelativeLayout(context);
        this.fragmentView.setOnTouchListener(new C10182());
        RelativeLayout relativeLayout = this.fragmentView;
        ScrollView scrollView = new ScrollView(context);
        relativeLayout.addView(scrollView);
        LayoutParams layoutParams3 = (LayoutParams) scrollView.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -2;
        layoutParams3.addRule(15, -1);
        scrollView.setLayoutParams(layoutParams3);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
        scrollView.addView(linearLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(C0691R.drawable.phone_change);
        linearLayout.addView(imageView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 1;
        imageView.setLayoutParams(layoutParams2);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setGravity(1);
        textView.setTextColor(-14606047);
        try {
            textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", C0691R.string.PhoneNumberHelp)));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            textView.setText(LocaleController.getString("PhoneNumberHelp", C0691R.string.PhoneNumberHelp));
        }
        linearLayout.addView(textView);
        layoutParams2 = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 1;
        layoutParams2.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(20.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(56.0f);
        textView.setLayoutParams(layoutParams2);
        textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setGravity(1);
        textView.setTextColor(-11697229);
        textView.setText(LocaleController.getString("PhoneNumberChange", C0691R.string.PhoneNumberChange));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(textView);
        layoutParams2 = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 1;
        layoutParams2.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(20.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(46.0f);
        textView.setLayoutParams(layoutParams2);
        textView.setOnClickListener(new C10203());
        return this.fragmentView;
    }
}
