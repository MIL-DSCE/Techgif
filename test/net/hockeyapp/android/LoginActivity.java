package net.hockeyapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.Scopes;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.Util;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;

public class LoginActivity extends Activity {
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_SECRET = "secret";
    public static final String EXTRA_URL = "url";
    private Button mButtonLogin;
    private Handler mLoginHandler;
    private LoginTask mLoginTask;
    private int mMode;
    private String mSecret;
    private String mUrl;

    /* renamed from: net.hockeyapp.android.LoginActivity.1 */
    class C03851 implements OnClickListener {
        C03851() {
        }

        public void onClick(View v) {
            LoginActivity.this.performAuthentication();
        }
    }

    private static class LoginHandler extends Handler {
        private final WeakReference<Activity> mWeakActivity;

        public LoginHandler(Activity activity) {
            this.mWeakActivity = new WeakReference(activity);
        }

        public void handleMessage(Message msg) {
            Activity activity = (Activity) this.mWeakActivity.get();
            if (activity != null) {
                if (msg.getData().getBoolean(LoginTask.BUNDLE_SUCCESS)) {
                    activity.finish();
                    if (LoginManager.listener != null) {
                        LoginManager.listener.onSuccess();
                        return;
                    }
                    return;
                }
                Toast.makeText(activity, "Login failed. Check your credentials.", 1).show();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0388R.layout.hockeyapp_activity_login);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mUrl = extras.getString(EXTRA_URL);
            this.mSecret = extras.getString(EXTRA_SECRET);
            this.mMode = extras.getInt(EXTRA_MODE);
        }
        configureView();
        initLoginHandler();
        Object object = getLastNonConfigurationInstance();
        if (object != null) {
            this.mLoginTask = (LoginTask) object;
            this.mLoginTask.attach(this, this.mLoginHandler);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mLoginTask != null) {
            this.mLoginTask.detach();
        }
        return this.mLoginTask;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (LoginManager.listener != null) {
                LoginManager.listener.onBack();
            } else {
                Intent intent = new Intent(this, LoginManager.mainActivity);
                intent.setFlags(67108864);
                intent.putExtra("net.hockeyapp.android.EXIT", true);
                startActivity(intent);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void configureView() {
        if (this.mMode == 1) {
            ((EditText) findViewById(C0388R.id.input_password)).setVisibility(4);
        }
        ((TextView) findViewById(C0388R.id.text_headline)).setText(this.mMode == 1 ? C0388R.string.hockeyapp_login_headline_text_email_only : C0388R.string.hockeyapp_login_headline_text);
        this.mButtonLogin = (Button) findViewById(C0388R.id.button_login);
        this.mButtonLogin.setOnClickListener(new C03851());
    }

    private void initLoginHandler() {
        this.mLoginHandler = new LoginHandler(this);
    }

    private void performAuthentication() {
        if (Util.isConnectedToNetwork(this)) {
            String email = ((EditText) findViewById(C0388R.id.input_email)).getText().toString();
            String password = ((EditText) findViewById(C0388R.id.input_password)).getText().toString();
            boolean ready = false;
            Map<String, String> params = new HashMap();
            if (this.mMode == 1) {
                ready = !TextUtils.isEmpty(email);
                params.put(Scopes.EMAIL, email);
                params.put("authcode", md5(this.mSecret + email));
            } else if (this.mMode == 2) {
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    ready = false;
                } else {
                    ready = true;
                }
                params.put(Scopes.EMAIL, email);
                params.put("password", password);
            }
            if (ready) {
                this.mLoginTask = new LoginTask(this, this.mLoginHandler, this.mUrl, this.mMode, params);
                AsyncTaskUtils.execute(this.mLoginTask);
                return;
            }
            Toast.makeText(this, getString(C0388R.string.hockeyapp_login_missing_credentials_toast), 1).show();
            return;
        }
        Toast.makeText(this, C0388R.string.hockeyapp_error_no_network_message, 1).show();
    }

    public String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(aMessageDigest & NalUnitUtil.EXTENDED_SAR);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }
}
