package org.telegram.messenger;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;

public class GcmPushListenerService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;

    /* renamed from: org.telegram.messenger.GcmPushListenerService.1 */
    class C04901 implements Runnable {
        final /* synthetic */ Bundle val$bundle;

        C04901(Bundle bundle) {
            this.val$bundle = bundle;
        }

        public void run() {
            ApplicationLoader.postInitApplication();
            try {
                if ("DC_UPDATE".equals(this.val$bundle.getString("loc_key"))) {
                    JSONObject object = new JSONObject(this.val$bundle.getString("custom"));
                    int dc = object.getInt("dc");
                    String[] parts = object.getString("addr").split(":");
                    if (parts.length == 2) {
                        ConnectionsManager.getInstance().applyDatacenterAddress(dc, parts[0], Integer.parseInt(parts[GcmPushListenerService.NOTIFICATION_ID]));
                    } else {
                        return;
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            ConnectionsManager.onInternalPushReceived();
            ConnectionsManager.getInstance().resumeNetworkMaybe();
        }
    }

    public void onMessageReceived(String from, Bundle bundle) {
        FileLog.m10d("tmessages", "GCM received bundle: " + bundle + " from: " + from);
        AndroidUtilities.runOnUIThread(new C04901(bundle));
    }
}
