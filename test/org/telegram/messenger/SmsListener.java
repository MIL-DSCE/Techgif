package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver {
    private SharedPreferences preferences;

    /* renamed from: org.telegram.messenger.SmsListener.1 */
    class C07421 implements Runnable {
        final /* synthetic */ Matcher val$matcher;

        C07421(Matcher matcher) {
            this.val$matcher = matcher;
        }

        public void run() {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.val$matcher.group(0));
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") && AndroidUtilities.isWaitingForSms()) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] msgs = new SmsMessage[pdus.length];
                    String wholeString = TtmlNode.ANONYMOUS_REGION_ID;
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        wholeString = wholeString + msgs[i].getMessageBody();
                    }
                    Matcher matcher = Pattern.compile("[0-9]+").matcher(wholeString);
                    if (matcher.find() && matcher.group(0).length() >= 3) {
                        AndroidUtilities.runOnUIThread(new C07421(matcher));
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }
    }
}
