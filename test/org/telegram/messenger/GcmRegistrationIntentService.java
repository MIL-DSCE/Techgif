package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GcmRegistrationIntentService extends IntentService {

    /* renamed from: org.telegram.messenger.GcmRegistrationIntentService.1 */
    class C04911 implements Runnable {
        final /* synthetic */ String val$token;

        C04911(String str) {
            this.val$token = str;
        }

        public void run() {
            ApplicationLoader.postInitApplication();
            GcmRegistrationIntentService.this.sendRegistrationToServer(this.val$token);
        }
    }

    /* renamed from: org.telegram.messenger.GcmRegistrationIntentService.2 */
    class C04922 implements Runnable {
        final /* synthetic */ int val$failCount;

        C04922(int i) {
            this.val$failCount = i;
        }

        public void run() {
            try {
                Intent intent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
                intent.putExtra("failCount", this.val$failCount + 1);
                GcmRegistrationIntentService.this.startService(intent);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.GcmRegistrationIntentService.3 */
    class C04943 implements Runnable {
        final /* synthetic */ String val$token;

        /* renamed from: org.telegram.messenger.GcmRegistrationIntentService.3.1 */
        class C04931 implements Runnable {
            C04931() {
            }

            public void run() {
                MessagesController.getInstance().registerForPush(C04943.this.val$token);
            }
        }

        C04943(String str) {
            this.val$token = str;
        }

        public void run() {
            UserConfig.pushString = this.val$token;
            UserConfig.registeredForPush = false;
            UserConfig.saveConfig(false);
            if (UserConfig.getClientUserId() != 0) {
                AndroidUtilities.runOnUIThread(new C04931());
            }
        }
    }

    public GcmRegistrationIntentService() {
        super("GcmRegistrationIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        try {
            String token = InstanceID.getInstance(this).getToken(getString(C0691R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            FileLog.m10d("tmessages", "GCM Registration Token: " + token);
            AndroidUtilities.runOnUIThread(new C04911(token));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            int failCount = intent.getIntExtra("failCount", 0);
            if (failCount < 60) {
                AndroidUtilities.runOnUIThread(new C04922(failCount), failCount < 20 ? 10000 : 1800000);
            }
        }
    }

    private void sendRegistrationToServer(String token) {
        Utilities.stageQueue.postRunnable(new C04943(token));
    }
}
