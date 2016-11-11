package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import org.telegram.PhoneFormat.PhoneFormat;

public class CallReceiver extends BroadcastReceiver {

    /* renamed from: org.telegram.messenger.CallReceiver.1 */
    class C04301 extends PhoneStateListener {
        C04301() {
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (state == 1 && incomingNumber != null && incomingNumber.length() > 0) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveCall, PhoneFormat.stripExceptNumbers(incomingNumber));
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        ((TelephonyManager) context.getSystemService("phone")).listen(new C04301(), 32);
    }
}
