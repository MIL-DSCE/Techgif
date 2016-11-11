package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions;
    private int currentViewNum;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private ProgressDialog progressDialog;
    private SlideView[] views;

    /* renamed from: org.telegram.ui.ChangePhoneActivity.1 */
    class C17701 extends ActionBarMenuOnItemClick {
        C17701() {
        }

        public void onItemClick(int id) {
            if (id == ChangePhoneActivity.done_button) {
                ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
            } else if (id == -1) {
                ChangePhoneActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneActivity.2 */
    class C17712 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ SlideView val$newView;
        final /* synthetic */ SlideView val$outView;

        C17712(SlideView slideView, SlideView slideView2) {
            this.val$newView = slideView;
            this.val$outView = slideView2;
        }

        public void onAnimationStart(Animator animation) {
            this.val$newView.setVisibility(0);
        }

        public void onAnimationEnd(Animator animation) {
            this.val$outView.setVisibility(8);
            this.val$outView.setX(0.0f);
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private EditText codeField;
        private volatile int codeTime;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private String emailPhone;
        private boolean ignoreOnTextChange;
        private double lastCodeTime;
        private double lastCurrentTime;
        private String lastError;
        private int length;
        private boolean nextPressed;
        private int nextType;
        private int openTime;
        private String pattern;
        private String phone;
        private String phoneHash;
        private TextView problemText;
        private ProgressView progressView;
        private String requestPhone;
        private volatile int time;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync;
        private boolean waitingForEvent;

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.1 */
        class C09991 implements TextWatcher {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C09991(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!LoginActivitySmsView.this.ignoreOnTextChange && LoginActivitySmsView.this.length != 0 && LoginActivitySmsView.this.codeField.length() == LoginActivitySmsView.this.length) {
                    LoginActivitySmsView.this.onNextPressed();
                }
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.2 */
        class C10002 implements OnEditorActionListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10002(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                LoginActivitySmsView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.3 */
        class C10013 implements OnClickListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10013(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void onClick(View v) {
                if (!LoginActivitySmsView.this.nextPressed) {
                    if (LoginActivitySmsView.this.nextType == 0 || LoginActivitySmsView.this.nextType == 4) {
                        try {
                            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                            Intent mailer = new Intent("android.intent.action.SEND");
                            mailer.setType("message/rfc822");
                            String[] strArr = new String[ChangePhoneActivity.done_button];
                            strArr[0] = "sms@stel.com";
                            mailer.putExtra("android.intent.extra.EMAIL", strArr);
                            mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + LoginActivitySmsView.this.emailPhone);
                            mailer.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivitySmsView.this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivitySmsView.this.lastError);
                            LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                            return;
                        } catch (Exception e) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("NoMailInstalled", C0691R.string.NoMailInstalled));
                            return;
                        }
                    }
                    LoginActivitySmsView.this.resendCode();
                }
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.4 */
        class C10024 implements OnClickListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.4.1 */
            class C17721 implements RequestDelegate {
                C17721() {
                }

                public void run(TLObject response, TL_error error) {
                }
            }

            C10024(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void onClick(View view) {
                TL_auth_cancelCode req = new TL_auth_cancelCode();
                req.phone_number = LoginActivitySmsView.this.requestPhone;
                req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                ConnectionsManager.getInstance().sendRequest(req, new C17721(), 2);
                LoginActivitySmsView.this.onBackPressed();
                ChangePhoneActivity.this.setPage(0, true, null, true);
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.6 */
        class C10056 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.6.1 */
            class C10041 implements Runnable {
                C10041() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= 1000) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C10056() {
            }

            public void run() {
                double currentTime = (double) System.currentTimeMillis();
                LoginActivitySmsView.access$2826(LoginActivitySmsView.this, currentTime - LoginActivitySmsView.this.lastCodeTime);
                LoginActivitySmsView.this.lastCodeTime = currentTime;
                AndroidUtilities.runOnUIThread(new C10041());
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.7 */
        class C10087 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.7.1 */
            class C10071 implements Runnable {

                /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.7.1.1 */
                class C17741 implements RequestDelegate {

                    /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.7.1.1.1 */
                    class C10061 implements Runnable {
                        final /* synthetic */ TL_error val$error;

                        C10061(TL_error tL_error) {
                            this.val$error = tL_error;
                        }

                        public void run() {
                            LoginActivitySmsView.this.lastError = this.val$error.text;
                        }
                    }

                    C17741() {
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new C10061(error));
                        }
                    }
                }

                C10071() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= 1000) {
                        int seconds = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0691R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                        } else if (LoginActivitySmsView.this.nextType == 2) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0691R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                        }
                        if (LoginActivitySmsView.this.progressView != null) {
                            LoginActivitySmsView.this.progressView.setProgress(TouchHelperCallback.ALPHA_FULL - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                            return;
                        }
                        return;
                    }
                    if (LoginActivitySmsView.this.progressView != null) {
                        LoginActivitySmsView.this.progressView.setProgress(TouchHelperCallback.ALPHA_FULL);
                    }
                    LoginActivitySmsView.this.destroyTimer();
                    if (LoginActivitySmsView.this.currentType == 3) {
                        AndroidUtilities.setWaitingForCall(false);
                        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                        LoginActivitySmsView.this.waitingForEvent = false;
                        LoginActivitySmsView.this.destroyCodeTimer();
                        LoginActivitySmsView.this.resendCode();
                    } else if (LoginActivitySmsView.this.currentType != 2) {
                    } else {
                        if (LoginActivitySmsView.this.nextType == 4) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", C0691R.string.Calling));
                            LoginActivitySmsView.this.createCodeTimer();
                            TL_auth_resendCode req = new TL_auth_resendCode();
                            req.phone_number = LoginActivitySmsView.this.requestPhone;
                            req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance().sendRequest(req, new C17741(), 2);
                        } else if (LoginActivitySmsView.this.nextType == 3) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            LoginActivitySmsView.this.waitingForEvent = false;
                            LoginActivitySmsView.this.destroyCodeTimer();
                            LoginActivitySmsView.this.resendCode();
                        }
                    }
                }
            }

            C10087() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTime = (double) System.currentTimeMillis();
                    LoginActivitySmsView.access$3326(LoginActivitySmsView.this, currentTime - LoginActivitySmsView.this.lastCurrentTime);
                    LoginActivitySmsView.this.lastCurrentTime = currentTime;
                    AndroidUtilities.runOnUIThread(new C10071());
                }
            }
        }

        private class ProgressView extends View {
            private Paint paint;
            private Paint paint2;
            private float progress;

            public ProgressView(Context context) {
                super(context);
                this.paint = new Paint();
                this.paint2 = new Paint();
                this.paint.setColor(-1971470);
                this.paint2.setColor(-10313520);
            }

            public void setProgress(float value) {
                this.progress = value;
                invalidate();
            }

            protected void onDraw(Canvas canvas) {
                int start = (int) (((float) getMeasuredWidth()) * this.progress);
                canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
                canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.5 */
        class C17735 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.5.1 */
            class C10031 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10031(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        ChangePhoneActivity.this.fillNextCodeParams(C17735.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0691R.string.InvalidPhoneNumber));
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0691R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            LoginActivitySmsView.this.onBackPressed();
                            ChangePhoneActivity.this.setPage(0, true, null, true);
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0691R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0691R.string.FloodWait));
                        } else if (this.val$error.code != -1000) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    }
                    ChangePhoneActivity.this.needHideProgress();
                }
            }

            C17735(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C10031(error, response));
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.8 */
        class C17758 implements RequestDelegate {

            /* renamed from: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.8.1 */
            class C10091 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10091(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChangePhoneActivity.this.needHideProgress();
                    LoginActivitySmsView.this.nextPressed = false;
                    if (this.val$error == null) {
                        User user = this.val$response;
                        LoginActivitySmsView.this.destroyTimer();
                        LoginActivitySmsView.this.destroyCodeTimer();
                        UserConfig.setCurrentUser(user);
                        UserConfig.saveConfig(true);
                        ArrayList<User> users = new ArrayList();
                        users.add(user);
                        MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                        MessagesController.getInstance().putUser(user, false);
                        ChangePhoneActivity.this.finishFragment();
                        return;
                    }
                    LoginActivitySmsView.this.lastError = this.val$error.text;
                    if ((LoginActivitySmsView.this.currentType == 3 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2)) || (LoginActivitySmsView.this.currentType == 2 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3))) {
                        LoginActivitySmsView.this.createTimer();
                    }
                    if (LoginActivitySmsView.this.currentType == 2) {
                        AndroidUtilities.setWaitingForSms(true);
                        NotificationCenter.getInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                    } else if (LoginActivitySmsView.this.currentType == 3) {
                        AndroidUtilities.setWaitingForCall(true);
                        NotificationCenter.getInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                    }
                    LoginActivitySmsView.this.waitingForEvent = true;
                    if (LoginActivitySmsView.this.currentType == 3) {
                        return;
                    }
                    if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0691R.string.InvalidPhoneNumber));
                    } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0691R.string.InvalidCode));
                    } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0691R.string.CodeExpired));
                    } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                        ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0691R.string.FloodWait));
                    } else {
                        ChangePhoneActivity.this.needShowAlert(this.val$error.text);
                    }
                }
            }

            C17758() {
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C10091(error, response));
            }
        }

        static /* synthetic */ int access$2826(LoginActivitySmsView x0, double x1) {
            int i = (int) (((double) x0.codeTime) - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$3326(LoginActivitySmsView x0, double x1) {
            int i = (int) (((double) x0.time) - x1);
            x0.time = i;
            return i;
        }

        public LoginActivitySmsView(Context context, int type) {
            super(context);
            this.timerSync = new Object();
            this.time = 60000;
            this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
            this.lastError = TtmlNode.ANONYMOUS_REGION_ID;
            this.pattern = "*";
            this.currentType = type;
            setOrientation(ChangePhoneActivity.done_button);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(-9079435);
            this.confirmTextView.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), TouchHelperCallback.ALPHA_FULL);
            if (this.currentType == 3) {
                FrameLayout frameLayout = new FrameLayout(context);
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(C0691R.drawable.phone_activate);
                if (LocaleController.isRTL) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            } else {
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            }
            this.codeField = new EditText(context);
            this.codeField.setTextColor(-14606047);
            this.codeField.setHint(LocaleController.getString("Code", C0691R.string.Code));
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setHintTextColor(-6842473);
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.codeField.setInputType(3);
            this.codeField.setMaxLines(ChangePhoneActivity.done_button);
            this.codeField.setPadding(0, 0, 0, 0);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, (int) ChangePhoneActivity.done_button, 0, 20, 0, 0));
            this.codeField.addTextChangedListener(new C09991(ChangePhoneActivity.this));
            this.codeField.setOnEditorActionListener(new C10002(ChangePhoneActivity.this));
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            this.timeText.setTextColor(-9079435);
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), TouchHelperCallback.ALPHA_FULL);
            this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 30, 0, 0));
            if (this.currentType == 3) {
                this.progressView = new ProgressView(context);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            this.problemText = new TextView(context);
            this.problemText.setText(LocaleController.getString("DidNotGetTheCode", C0691R.string.DidNotGetTheCode));
            this.problemText.setGravity(LocaleController.isRTL ? 5 : 3);
            this.problemText.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            this.problemText.setTextColor(-11697229);
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), TouchHelperCallback.ALPHA_FULL);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            this.problemText.setOnClickListener(new C10013(ChangePhoneActivity.this));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(linearLayout, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            TextView wrongNumber = new TextView(context);
            wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | ChangePhoneActivity.done_button);
            wrongNumber.setTextColor(-11697229);
            wrongNumber.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), TouchHelperCallback.ALPHA_FULL);
            wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            linearLayout.addView(wrongNumber, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            wrongNumber.setText(LocaleController.getString("WrongNumber", C0691R.string.WrongNumber));
            wrongNumber.setOnClickListener(new C10024(ChangePhoneActivity.this));
        }

        private void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            ChangePhoneActivity.this.needShowProgress();
            TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(req, new C17735(params), 2);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourCode", C0691R.string.YourCode);
        }

        public void setParams(Bundle params) {
            int i = 0;
            if (params != null) {
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.waitingForEvent = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = params;
                this.phone = params.getString("phone");
                this.emailPhone = params.getString("ephone");
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                int i2 = params.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = params.getInt("nextType");
                this.pattern = params.getString("pattern");
                this.length = params.getInt("length");
                if (this.length != 0) {
                    InputFilter[] inputFilters = new InputFilter[ChangePhoneActivity.done_button];
                    inputFilters[0] = new LengthFilter(this.length);
                    this.codeField.setFilters(inputFilters);
                } else {
                    this.codeField.setFilters(new InputFilter[0]);
                }
                if (this.progressView != null) {
                    ProgressView progressView = this.progressView;
                    if (this.nextType != 0) {
                        i2 = 0;
                    } else {
                        i2 = 8;
                    }
                    progressView.setVisibility(i2);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (this.currentType == ChangePhoneActivity.done_button) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", C0691R.string.SentAppCode));
                    } else if (this.currentType == 2) {
                        r7 = new Object[ChangePhoneActivity.done_button];
                        r7[0] = number;
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", C0691R.string.SentSmsCode, r7));
                    } else if (this.currentType == 3) {
                        r7 = new Object[ChangePhoneActivity.done_button];
                        r7[0] = number;
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", C0691R.string.SentCallCode, r7));
                    } else if (this.currentType == 4) {
                        r7 = new Object[ChangePhoneActivity.done_button];
                        r7[0] = number;
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", C0691R.string.SentCallOnly, r7));
                    }
                    this.confirmTextView.setText(str);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField);
                        this.codeField.requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.currentType == ChangePhoneActivity.done_button) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (this.currentType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", C0691R.string.CallText, Integer.valueOf(ChangePhoneActivity.done_button), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", C0691R.string.SmsText, Integer.valueOf(ChangePhoneActivity.done_button), Integer.valueOf(0)));
                        }
                        createTimer();
                    } else if (this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setVisibility(0);
                        this.timeText.setText(LocaleController.formatString("CallText", C0691R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        TextView textView = this.problemText;
                        if (this.time >= 1000) {
                            i = 8;
                        }
                        textView.setVisibility(i);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = DefaultLoadControl.DEFAULT_LOW_WATERMARK_MS;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new C10056(), 0, 1000);
            }
        }

        private void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.codeTimer != null) {
                        this.codeTimer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new C10087(), 0, 1000);
            }
        }

        private void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.timeTimer != null) {
                        this.timeTimer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                TL_account_changePhone req = new TL_account_changePhone();
                req.phone_number = this.requestPhone;
                req.phone_code = this.codeField.getText().toString();
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                ChangePhoneActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(req, new C17758(), 2);
            }
        }

        public void onBackPressed() {
            destroyTimer();
            destroyCodeTimer();
            this.currentParams = null;
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        public void onShow() {
            super.onShow();
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
            }
        }

        public void didReceivedNotification(int id, Object... args) {
            if (this.waitingForEvent && this.codeField != null) {
                if (id == NotificationCenter.didReceiveSmsCode) {
                    this.ignoreOnTextChange = true;
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID + args[0]);
                    this.ignoreOnTextChange = false;
                    onNextPressed();
                } else if (id == NotificationCenter.didReceiveCall) {
                    String num = TtmlNode.ANONYMOUS_REGION_ID + args[0];
                    if (this.pattern.equals("*") || num.contains(this.pattern.replace("*", TtmlNode.ANONYMOUS_REGION_ID))) {
                        this.ignoreOnTextChange = true;
                        this.codeField.setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    }
                }
            }
        }
    }

    public class PhoneView extends SlideView implements OnItemSelectedListener {
        private EditText codeField;
        private HashMap<String, String> codesMap;
        private ArrayList<String> countriesArray;
        private HashMap<String, String> countriesMap;
        private TextView countryButton;
        private int countryState;
        private boolean ignoreOnPhoneChange;
        private boolean ignoreOnTextChange;
        private boolean ignoreSelection;
        private boolean nextPressed;
        private HintEditText phoneField;
        private HashMap<String, String> phoneFormatMap;

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.1 */
        class C10111 implements OnClickListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.1.1 */
            class C17761 implements CountrySelectActivityDelegate {

                /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.1.1.1 */
                class C10101 implements Runnable {
                    C10101() {
                    }

                    public void run() {
                        AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                    }
                }

                C17761() {
                }

                public void didSelectCountry(String name) {
                    PhoneView.this.selectCountry(name);
                    AndroidUtilities.runOnUIThread(new C10101(), 300);
                    PhoneView.this.phoneField.requestFocus();
                    PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                }
            }

            C10111(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void onClick(View view) {
                CountrySelectActivity fragment = new CountrySelectActivity();
                fragment.setCountrySelectActivityDelegate(new C17761());
                ChangePhoneActivity.this.presentFragment(fragment);
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.2 */
        class C10122 implements TextWatcher {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10122(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (!PhoneView.this.ignoreOnTextChange) {
                    PhoneView.this.ignoreOnTextChange = true;
                    String text = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                    PhoneView.this.codeField.setText(text);
                    if (text.length() == 0) {
                        PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", C0691R.string.ChooseCountry));
                        PhoneView.this.phoneField.setHintText(null);
                        PhoneView.this.countryState = ChangePhoneActivity.done_button;
                    } else {
                        boolean ok = false;
                        String textToSet = null;
                        if (text.length() > 4) {
                            PhoneView.this.ignoreOnTextChange = true;
                            for (int a = 4; a >= ChangePhoneActivity.done_button; a--) {
                                String sub = text.substring(0, a);
                                if (((String) PhoneView.this.codesMap.get(sub)) != null) {
                                    ok = true;
                                    textToSet = text.substring(a, text.length()) + PhoneView.this.phoneField.getText().toString();
                                    text = sub;
                                    PhoneView.this.codeField.setText(sub);
                                    break;
                                }
                            }
                            if (!ok) {
                                PhoneView.this.ignoreOnTextChange = true;
                                textToSet = text.substring(ChangePhoneActivity.done_button, text.length()) + PhoneView.this.phoneField.getText().toString();
                                EditText access$400 = PhoneView.this.codeField;
                                text = text.substring(0, ChangePhoneActivity.done_button);
                                access$400.setText(text);
                            }
                        }
                        String country = (String) PhoneView.this.codesMap.get(text);
                        if (country != null) {
                            int index = PhoneView.this.countriesArray.indexOf(country);
                            if (index != -1) {
                                PhoneView.this.ignoreSelection = true;
                                PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(index));
                                String hint = (String) PhoneView.this.phoneFormatMap.get(text);
                                PhoneView.this.phoneField.setHintText(hint != null ? hint.replace('X', '\u2013') : null);
                                PhoneView.this.countryState = 0;
                            } else {
                                PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", C0691R.string.WrongCountry));
                                PhoneView.this.phoneField.setHintText(null);
                                PhoneView.this.countryState = 2;
                            }
                        } else {
                            PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", C0691R.string.WrongCountry));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 2;
                        }
                        if (!ok) {
                            PhoneView.this.codeField.setSelection(PhoneView.this.codeField.getText().length());
                        }
                        if (textToSet != null) {
                            PhoneView.this.phoneField.requestFocus();
                            PhoneView.this.phoneField.setText(textToSet);
                            PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                        }
                    }
                    PhoneView.this.ignoreOnTextChange = false;
                }
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.3 */
        class C10133 implements OnEditorActionListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10133(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                PhoneView.this.phoneField.requestFocus();
                PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                return true;
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.4 */
        class C10144 implements TextWatcher {
            private int actionPosition;
            private int characterAction;
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10144(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
                this.characterAction = -1;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 0 && after == ChangePhoneActivity.done_button) {
                    this.characterAction = ChangePhoneActivity.done_button;
                } else if (count != ChangePhoneActivity.done_button || after != 0) {
                    this.characterAction = -1;
                } else if (s.charAt(start) != ' ' || start <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = start - 1;
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!PhoneView.this.ignoreOnPhoneChange) {
                    int a;
                    int start = PhoneView.this.phoneField.getSelectionStart();
                    String phoneChars = "0123456789";
                    String str = PhoneView.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + ChangePhoneActivity.done_button, str.length());
                        start--;
                    }
                    StringBuilder builder = new StringBuilder(str.length());
                    for (a = 0; a < str.length(); a += ChangePhoneActivity.done_button) {
                        String ch = str.substring(a, a + ChangePhoneActivity.done_button);
                        if (phoneChars.contains(ch)) {
                            builder.append(ch);
                        }
                    }
                    PhoneView.this.ignoreOnPhoneChange = true;
                    String hint = PhoneView.this.phoneField.getHintText();
                    if (hint != null) {
                        a = 0;
                        while (a < builder.length()) {
                            if (a < hint.length()) {
                                if (hint.charAt(a) == ' ') {
                                    builder.insert(a, ' ');
                                    a += ChangePhoneActivity.done_button;
                                    if (!(start != a || this.characterAction == 2 || this.characterAction == 3)) {
                                        start += ChangePhoneActivity.done_button;
                                    }
                                }
                                a += ChangePhoneActivity.done_button;
                            } else {
                                builder.insert(a, ' ');
                                if (!(start != a + ChangePhoneActivity.done_button || this.characterAction == 2 || this.characterAction == 3)) {
                                    start += ChangePhoneActivity.done_button;
                                }
                            }
                        }
                    }
                    PhoneView.this.phoneField.setText(builder);
                    if (start >= 0) {
                        HintEditText access$200 = PhoneView.this.phoneField;
                        if (start > PhoneView.this.phoneField.length()) {
                            start = PhoneView.this.phoneField.length();
                        }
                        access$200.setSelection(start);
                    }
                    PhoneView.this.phoneField.onTextChange();
                    PhoneView.this.ignoreOnPhoneChange = false;
                }
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.5 */
        class C10155 implements OnEditorActionListener {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10155(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5) {
                    return false;
                }
                PhoneView.this.onNextPressed();
                return true;
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.6 */
        class C10166 implements Comparator<String> {
            final /* synthetic */ ChangePhoneActivity val$this$0;

            C10166(ChangePhoneActivity changePhoneActivity) {
                this.val$this$0 = changePhoneActivity;
            }

            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.7 */
        class C17777 implements RequestDelegate {
            final /* synthetic */ Bundle val$params;

            /* renamed from: org.telegram.ui.ChangePhoneActivity.PhoneView.7.1 */
            class C10171 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10171(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    PhoneView.this.nextPressed = false;
                    if (this.val$error == null) {
                        ChangePhoneActivity.this.fillNextCodeParams(C17777.this.val$params, (TL_auth_sentCode) this.val$response);
                    } else if (this.val$error.text != null) {
                        if (this.val$error.text.contains("PHONE_NUMBER_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0691R.string.InvalidPhoneNumber));
                        } else if (this.val$error.text.contains("PHONE_CODE_EMPTY") || this.val$error.text.contains("PHONE_CODE_INVALID")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidCode", C0691R.string.InvalidCode));
                        } else if (this.val$error.text.contains("PHONE_CODE_EXPIRED")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("CodeExpired", C0691R.string.CodeExpired));
                        } else if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("FloodWait", C0691R.string.FloodWait));
                        } else if (this.val$error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                            ChangePhoneActivity changePhoneActivity = ChangePhoneActivity.this;
                            Object[] objArr = new Object[ChangePhoneActivity.done_button];
                            objArr[0] = C17777.this.val$params.getString("phone");
                            changePhoneActivity.needShowAlert(LocaleController.formatString("ChangePhoneNumberOccupied", C0691R.string.ChangePhoneNumberOccupied, objArr));
                        } else {
                            ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred));
                        }
                    }
                    ChangePhoneActivity.this.needHideProgress();
                }
            }

            C17777(Bundle bundle) {
                this.val$params = bundle;
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C10171(error, response));
            }
        }

        public PhoneView(Context context) {
            super(context);
            this.countryState = 0;
            this.countriesArray = new ArrayList();
            this.countriesMap = new HashMap();
            this.codesMap = new HashMap();
            this.phoneFormatMap = new HashMap();
            this.ignoreSelection = false;
            this.ignoreOnTextChange = false;
            this.ignoreOnPhoneChange = false;
            this.nextPressed = false;
            setOrientation(ChangePhoneActivity.done_button);
            this.countryButton = new TextView(context);
            this.countryButton.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), 0);
            this.countryButton.setTextColor(-14606047);
            this.countryButton.setMaxLines(ChangePhoneActivity.done_button);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | ChangePhoneActivity.done_button);
            this.countryButton.setBackgroundResource(C0691R.drawable.spinner_states);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new C10111(ChangePhoneActivity.this));
            View view = new View(context);
            view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            view.setBackgroundColor(-2368549);
            addView(view, LayoutHelper.createLinear(-1, ChangePhoneActivity.done_button, 4.0f, -17.5f, 4.0f, 0.0f));
            view = new LinearLayout(context);
            view.setOrientation(0);
            addView(view, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            view = new TextView(context);
            view.setText("+");
            view.setTextColor(-14606047);
            view.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            view.addView(view, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditText(context);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(-14606047);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.codeField.setMaxLines(ChangePhoneActivity.done_button);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(268435461);
            InputFilter[] inputFilters = new InputFilter[ChangePhoneActivity.done_button];
            inputFilters[0] = new LengthFilter(5);
            this.codeField.setFilters(inputFilters);
            view.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            this.codeField.addTextChangedListener(new C10122(ChangePhoneActivity.this));
            this.codeField.setOnEditorActionListener(new C10133(ChangePhoneActivity.this));
            this.phoneField = new HintEditText(context);
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(-14606047);
            this.phoneField.setHintTextColor(-6842473);
            this.phoneField.setPadding(0, 0, 0, 0);
            AndroidUtilities.clearCursorDrawable(this.phoneField);
            this.phoneField.setTextSize(ChangePhoneActivity.done_button, 18.0f);
            this.phoneField.setMaxLines(ChangePhoneActivity.done_button);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(268435461);
            view.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            this.phoneField.addTextChangedListener(new C10144(ChangePhoneActivity.this));
            this.phoneField.setOnEditorActionListener(new C10155(ChangePhoneActivity.this));
            view = new TextView(context);
            view.setText(LocaleController.getString("ChangePhoneHelp", C0691R.string.ChangePhoneHelp));
            view.setTextColor(-9079435);
            view.setTextSize(ChangePhoneActivity.done_button, 14.0f);
            view.setGravity(LocaleController.isRTL ? 5 : 3);
            view.setLineSpacing((float) AndroidUtilities.dp(2.0f), TouchHelperCallback.ALPHA_FULL);
            addView(view, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            HashMap<String, String> languageMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    this.countriesArray.add(0, args[2]);
                    this.countriesMap.put(args[2], args[0]);
                    this.codesMap.put(args[0], args[2]);
                    if (args.length > 3) {
                        this.phoneFormatMap.put(args[0], args[3]);
                    }
                    languageMap.put(args[ChangePhoneActivity.done_button], args[2]);
                }
                bufferedReader.close();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            Collections.sort(this.countriesArray, new C10166(ChangePhoneActivity.this));
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    country = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            if (country != null) {
                String countryName = (String) languageMap.get(country);
                if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                    this.codeField.setText((CharSequence) this.countriesMap.get(countryName));
                    this.countryState = 0;
                }
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", C0691R.string.ChooseCountry));
                this.phoneField.setHintText(null);
                this.countryState = ChangePhoneActivity.done_button;
            }
            if (this.codeField.length() != 0) {
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        public void selectCountry(String name) {
            if (this.countriesArray.indexOf(name) != -1) {
                this.ignoreOnTextChange = true;
                String code = (String) this.countriesMap.get(name);
                this.codeField.setText(code);
                this.countryButton.setText(name);
                String hint = (String) this.phoneFormatMap.get(code);
                this.phoneField.setHintText(hint != null ? hint.replace('X', '\u2013') : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed() {
            if (ChangePhoneActivity.this.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean simcardAvailable = (tm.getSimState() == ChangePhoneActivity.done_button || tm.getPhoneType() == 0) ? false : true;
                boolean allowCall = true;
                if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean allowSms = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                    if (ChangePhoneActivity.this.checkPermissions) {
                        ChangePhoneActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowSms) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
                        }
                        if (!ChangePhoneActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            if (preferences.getBoolean("firstlogin", true) || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(ChangePhoneActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                                if (ChangePhoneActivity.this.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", C0691R.string.AllowReadCallAndSms));
                                } else if (allowSms) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", C0691R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", C0691R.string.AllowReadSms));
                                }
                                ChangePhoneActivity.this.permissionsDialog = ChangePhoneActivity.this.showDialog(builder.create());
                                return;
                            }
                            ChangePhoneActivity.this.getParentActivity().requestPermissions((String[]) ChangePhoneActivity.this.permissionsItems.toArray(new String[ChangePhoneActivity.this.permissionsItems.size()]), 6);
                            return;
                        }
                    }
                }
                if (this.countryState == ChangePhoneActivity.done_button) {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("ChooseCountry", C0691R.string.ChooseCountry));
                } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION) {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("WrongCountry", C0691R.string.WrongCountry));
                } else if (this.codeField.length() == 0) {
                    ChangePhoneActivity.this.needShowAlert(LocaleController.getString("InvalidPhoneNumber", C0691R.string.InvalidPhoneNumber));
                } else {
                    TL_account_sendChangePhoneCode req = new TL_account_sendChangePhoneCode();
                    String phone = PhoneFormat.stripExceptNumbers(TtmlNode.ANONYMOUS_REGION_ID + this.codeField.getText() + this.phoneField.getText());
                    req.phone_number = phone;
                    boolean z = simcardAvailable && allowCall;
                    req.allow_flashcall = z;
                    if (req.allow_flashcall) {
                        String number = tm.getLine1Number();
                        z = (number == null || number.length() == 0 || (!phone.contains(number) && !number.contains(phone))) ? false : true;
                        req.current_number = z;
                    }
                    Bundle params = new Bundle();
                    params.putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    ChangePhoneActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(req, new C17777(params), 2);
                }
            }
        }

        public void onShow() {
            super.onShow();
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        public String getHeaderName() {
            return LocaleController.getString("ChangePhoneNewNumber", C0691R.string.ChangePhoneNewNumber);
        }
    }

    public ChangePhoneActivity() {
        this.currentViewNum = 0;
        this.views = new SlideView[5];
        this.permissionsItems = new ArrayList();
        this.checkPermissions = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        for (int a = 0; a < this.views.length; a += done_button) {
            if (this.views[a] != null) {
                this.views[a].onDestroyActivity();
            }
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
        this.actionBar.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C17701());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(context);
        this.views[done_button] = new LoginActivitySmsView(context, done_button);
        this.views[2] = new LoginActivitySmsView(context, 2);
        this.views[3] = new LoginActivitySmsView(context, 3);
        this.views[4] = new LoginActivitySmsView(context, 4);
        int a = 0;
        while (a < this.views.length) {
            this.views[a].setVisibility(a == 0 ? 0 : 8);
            frameLayout.addView(this.views[a], LayoutHelper.createFrame(-1, a == 0 ? -2.0f : GroundOverlayOptions.NO_DIMENSION, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, BitmapDescriptorFactory.HUE_ORANGE, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            a += done_button;
        }
        this.actionBar.setTitle(this.views[0].getHeaderName());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            if (this.currentViewNum == 0) {
                this.views[this.currentViewNum].onNextPressed();
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
    }

    public boolean onBackPressed() {
        if (this.currentViewNum == 0) {
            for (int a = 0; a < this.views.length; a += done_button) {
                if (this.views[a] != null) {
                    this.views[a].onDestroyActivity();
                }
            }
            return true;
        }
        this.views[this.currentViewNum].onBackPressed();
        setPage(0, true, null, true);
        return false;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void needShowAlert(String text) {
        if (text != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(getParentActivity());
            this.progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (page == 3) {
            this.doneButton.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        SlideView outView = this.views[this.currentViewNum];
        SlideView newView = this.views[page];
        this.currentViewNum = page;
        newView.setParams(params);
        this.actionBar.setTitle(newView.getHeaderName());
        newView.onShow();
        newView.setX(back ? (float) (-AndroidUtilities.displaySize.x) : (float) AndroidUtilities.displaySize.x);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        String str = "translationX";
        float[] fArr = new float[done_button];
        fArr[0] = back ? (float) AndroidUtilities.displaySize.x : (float) (-AndroidUtilities.displaySize.x);
        animatorArr[0] = ObjectAnimator.ofFloat(outView, str, fArr);
        float[] fArr2 = new float[done_button];
        fArr2[0] = 0.0f;
        animatorArr[done_button] = ObjectAnimator.ofFloat(newView, "translationX", fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new C17712(newView, outView));
        animatorSet.start();
    }

    private void fillNextCodeParams(Bundle params, TL_auth_sentCode res) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.type instanceof TL_auth_sentCodeTypeApp) {
            params.putInt("type", done_button);
            params.putInt("length", res.type.length);
            setPage(done_button, true, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(4, true, params, false);
        } else if (res.type instanceof TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, true, params, false);
        } else if (res.type instanceof TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(2, true, params, false);
        }
    }
}
