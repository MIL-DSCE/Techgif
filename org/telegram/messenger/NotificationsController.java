package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.CarExtender;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.SparseArray;
import com.google.android.gms.common.api.CommonStatusCodes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import org.aspectj.lang.JoinPoint;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class NotificationsController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController Instance;
    private AlarmManager alarmManager;
    protected AudioManager audioManager;
    private int autoNotificationId;
    private HashMap<Long, Integer> autoNotificationsIds;
    private ArrayList<MessageObject> delayedPushMessages;
    private boolean inChatSoundEnabled;
    private int lastBadgeCount;
    private int lastOnlineFromOtherDevice;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private WakeLock notificationDelayWakelock;
    private NotificationManagerCompat notificationManager;
    private DispatchQueue notificationsQueue;
    private boolean notifyCheck;
    private long opened_dialog_id;
    private int personal_count;
    public ArrayList<MessageObject> popupMessages;
    private HashMap<Long, Integer> pushDialogs;
    private HashMap<Long, Integer> pushDialogsOverrideMention;
    private ArrayList<MessageObject> pushMessages;
    private HashMap<Long, MessageObject> pushMessagesDict;
    private HashMap<Long, Point> smartNotificationsDialogs;
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    private int total_unread_count;
    private int wearNotificationId;
    private HashMap<Long, Integer> wearNotificationsIds;

    /* renamed from: org.telegram.messenger.NotificationsController.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ int val$count;

        /* renamed from: org.telegram.messenger.NotificationsController.10.1 */
        class C06741 implements Runnable {
            C06741() {
            }

            public void run() {
                try {
                    Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    intent.putExtra("badge_count", AnonymousClass10.this.val$count);
                    intent.putExtra("badge_count_package_name", ApplicationLoader.applicationContext.getPackageName());
                    intent.putExtra("badge_count_class_name", NotificationsController.this.launcherClassName);
                    ApplicationLoader.applicationContext.sendBroadcast(intent);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        AnonymousClass10(int i) {
            this.val$count = i;
        }

        public void run() {
            if (NotificationsController.this.lastBadgeCount != this.val$count) {
                NotificationsController.this.lastBadgeCount = this.val$count;
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("tag", "org.telegram.messenger/org.telegram.ui.LaunchActivity");
                    cv.put("count", Integer.valueOf(this.val$count));
                    ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv);
                } catch (Throwable th) {
                }
                try {
                    if (NotificationsController.this.launcherClassName == null) {
                        NotificationsController.this.launcherClassName = NotificationsController.getLauncherClassName(ApplicationLoader.applicationContext);
                    }
                    if (NotificationsController.this.launcherClassName != null) {
                        AndroidUtilities.runOnUIThread(new C06741());
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.1 */
    class C06771 implements Runnable {
        C06771() {
        }

        public void run() {
            FileLog.m11e("tmessages", "delay reached");
            if (!NotificationsController.this.delayedPushMessages.isEmpty()) {
                NotificationsController.this.showOrUpdateNotification(true);
                NotificationsController.this.delayedPushMessages.clear();
            }
            try {
                if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.2 */
    class C06782 implements Runnable {
        C06782() {
        }

        public void run() {
            NotificationsController.this.opened_dialog_id = 0;
            NotificationsController.this.total_unread_count = 0;
            NotificationsController.this.personal_count = 0;
            NotificationsController.this.pushMessages.clear();
            NotificationsController.this.pushMessagesDict.clear();
            NotificationsController.this.pushDialogs.clear();
            NotificationsController.this.wearNotificationsIds.clear();
            NotificationsController.this.autoNotificationsIds.clear();
            NotificationsController.this.delayedPushMessages.clear();
            NotificationsController.this.notifyCheck = false;
            NotificationsController.this.lastBadgeCount = 0;
            try {
                if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            NotificationsController.this.setBadge(0);
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            editor.clear();
            editor.commit();
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.3 */
    class C06793 implements Runnable {
        final /* synthetic */ long val$dialog_id;

        C06793(long j) {
            this.val$dialog_id = j;
        }

        public void run() {
            NotificationsController.this.opened_dialog_id = this.val$dialog_id;
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.4 */
    class C06804 implements Runnable {
        final /* synthetic */ int val$time;

        C06804(int i) {
            this.val$time = i;
        }

        public void run() {
            FileLog.m11e("tmessages", "set last online from other device = " + this.val$time);
            NotificationsController.this.lastOnlineFromOtherDevice = this.val$time;
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.5 */
    class C06825 implements Runnable {
        final /* synthetic */ SparseArray val$deletedMessages;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: org.telegram.messenger.NotificationsController.5.1 */
        class C06811 implements Runnable {
            C06811() {
            }

            public void run() {
                NotificationsController.this.popupMessages = C06825.this.val$popupArray;
            }
        }

        C06825(SparseArray sparseArray, ArrayList arrayList) {
            this.val$deletedMessages = sparseArray;
            this.val$popupArray = arrayList;
        }

        public void run() {
            int old_unread_count = NotificationsController.this.total_unread_count;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            for (int a = 0; a < this.val$deletedMessages.size(); a++) {
                int key = this.val$deletedMessages.keyAt(a);
                long dialog_id = (long) (-key);
                ArrayList<Integer> mids = (ArrayList) this.val$deletedMessages.get(key);
                Integer currentCount = (Integer) NotificationsController.this.pushDialogs.get(Long.valueOf(dialog_id));
                if (currentCount == null) {
                    currentCount = Integer.valueOf(0);
                }
                Integer newCount = currentCount;
                for (int b = 0; b < mids.size(); b++) {
                    long mid = ((long) ((Integer) mids.get(b)).intValue()) | (((long) key) << 32);
                    MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessagesDict.get(Long.valueOf(mid));
                    if (messageObject != null) {
                        NotificationsController.this.pushMessagesDict.remove(Long.valueOf(mid));
                        NotificationsController.this.delayedPushMessages.remove(messageObject);
                        NotificationsController.this.pushMessages.remove(messageObject);
                        if (NotificationsController.this.isPersonalMessage(messageObject)) {
                            NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                        }
                        if (this.val$popupArray != null) {
                            this.val$popupArray.remove(messageObject);
                        }
                        newCount = Integer.valueOf(newCount.intValue() - 1);
                    }
                }
                if (newCount.intValue() <= 0) {
                    newCount = Integer.valueOf(0);
                    NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(dialog_id));
                }
                if (!newCount.equals(currentCount)) {
                    NotificationsController.access$420(NotificationsController.this, currentCount.intValue());
                    NotificationsController.access$412(NotificationsController.this, newCount.intValue());
                    NotificationsController.this.pushDialogs.put(Long.valueOf(dialog_id), newCount);
                }
                if (newCount.intValue() == 0) {
                    NotificationsController.this.pushDialogs.remove(Long.valueOf(dialog_id));
                    NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(dialog_id));
                    if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                        this.val$popupArray.clear();
                    }
                }
            }
            if (this.val$popupArray != null) {
                AndroidUtilities.runOnUIThread(new C06811());
            }
            if (old_unread_count != NotificationsController.this.total_unread_count) {
                if (NotificationsController.this.notifyCheck) {
                    NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime());
                } else {
                    NotificationsController.this.delayedPushMessages.clear();
                    NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                }
            }
            NotificationsController.this.notifyCheck = false;
            if (preferences.getBoolean("badgeNumber", true)) {
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.6 */
    class C06846 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ SparseArray val$inbox;
        final /* synthetic */ boolean val$isPopup;
        final /* synthetic */ int val$max_date;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: org.telegram.messenger.NotificationsController.6.1 */
        class C06831 implements Runnable {
            C06831() {
            }

            public void run() {
                NotificationsController.this.popupMessages = C06846.this.val$popupArray;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
        }

        C06846(ArrayList arrayList, SparseArray sparseArray, long j, int i, int i2, boolean z) {
            this.val$popupArray = arrayList;
            this.val$inbox = sparseArray;
            this.val$dialog_id = j;
            this.val$max_id = i;
            this.val$max_date = i2;
            this.val$isPopup = z;
        }

        public void run() {
            int a;
            MessageObject messageObject;
            long mid;
            int oldCount = this.val$popupArray != null ? this.val$popupArray.size() : 0;
            if (this.val$inbox != null) {
                for (int b = 0; b < this.val$inbox.size(); b++) {
                    int key = this.val$inbox.keyAt(b);
                    long messageId = ((Long) this.val$inbox.get(key)).longValue();
                    a = 0;
                    while (a < NotificationsController.this.pushMessages.size()) {
                        messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                        if (messageObject.getDialogId() == ((long) key) && messageObject.getId() <= ((int) messageId)) {
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            if (this.val$popupArray != null) {
                                this.val$popupArray.remove(messageObject);
                            }
                            mid = (long) messageObject.messageOwner.id;
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            NotificationsController.this.pushMessagesDict.remove(Long.valueOf(mid));
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            NotificationsController.this.pushMessages.remove(a);
                            a--;
                        }
                        a++;
                    }
                }
                if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                    this.val$popupArray.clear();
                }
            }
            if (!(this.val$dialog_id == 0 || (this.val$max_id == 0 && this.val$max_date == 0))) {
                a = 0;
                while (a < NotificationsController.this.pushMessages.size()) {
                    messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                    if (messageObject.getDialogId() == this.val$dialog_id) {
                        boolean remove = false;
                        if (this.val$max_date != 0) {
                            if (messageObject.messageOwner.date <= this.val$max_date) {
                                remove = true;
                            }
                        } else if (this.val$isPopup) {
                            if (messageObject.getId() == this.val$max_id || this.val$max_id < 0) {
                                remove = true;
                            }
                        } else if (messageObject.getId() <= this.val$max_id || this.val$max_id < 0) {
                            remove = true;
                        }
                        if (remove) {
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            NotificationsController.this.pushMessages.remove(a);
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            if (this.val$popupArray != null) {
                                this.val$popupArray.remove(messageObject);
                            }
                            mid = (long) messageObject.messageOwner.id;
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            NotificationsController.this.pushMessagesDict.remove(Long.valueOf(mid));
                            a--;
                        }
                    }
                    a++;
                }
                if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                    this.val$popupArray.clear();
                }
            }
            if (this.val$popupArray != null && oldCount != this.val$popupArray.size()) {
                AndroidUtilities.runOnUIThread(new C06831());
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.7 */
    class C06867 implements Runnable {
        final /* synthetic */ boolean val$isLast;
        final /* synthetic */ ArrayList val$messageObjects;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: org.telegram.messenger.NotificationsController.7.1 */
        class C06851 implements Runnable {
            final /* synthetic */ int val$popupFinal;

            C06851(int i) {
                this.val$popupFinal = i;
            }

            public void run() {
                NotificationsController.this.popupMessages = C06867.this.val$popupArray;
                if (ApplicationLoader.mainInterfacePaused || !(ApplicationLoader.isScreenOn || UserConfig.isWaitingForPasscodeEnter)) {
                    MessageObject messageObject = (MessageObject) C06867.this.val$messageObjects.get(0);
                    if (this.val$popupFinal == 3 || ((this.val$popupFinal == 1 && ApplicationLoader.isScreenOn) || (this.val$popupFinal == 2 && !ApplicationLoader.isScreenOn))) {
                        Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                        popupIntent.setFlags(268763140);
                        ApplicationLoader.applicationContext.startActivity(popupIntent);
                    }
                }
            }
        }

        C06867(ArrayList arrayList, ArrayList arrayList2, boolean z) {
            this.val$popupArray = arrayList;
            this.val$messageObjects = arrayList2;
            this.val$isLast = z;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r25 = this;
            r5 = 0;
            r0 = r25;
            r0 = r0.val$popupArray;
            r22 = r0;
            r14 = r22.size();
            r20 = new java.util.HashMap;
            r20.<init>();
            r22 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r23 = "Notifications";
            r24 = 0;
            r19 = r22.getSharedPreferences(r23, r24);
            r22 = "PinnedMessages";
            r23 = 1;
            r0 = r19;
            r1 = r22;
            r2 = r23;
            r6 = r0.getBoolean(r1, r2);
            r15 = 0;
            r4 = 0;
        L_0x002a:
            r0 = r25;
            r0 = r0.val$messageObjects;
            r22 = r0;
            r22 = r22.size();
            r0 = r22;
            if (r4 >= r0) goto L_0x0207;
        L_0x0038:
            r0 = r25;
            r0 = r0.val$messageObjects;
            r22 = r0;
            r0 = r22;
            r10 = r0.get(r4);
            r10 = (org.telegram.messenger.MessageObject) r10;
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.id;
            r22 = r0;
            r0 = r22;
            r12 = (long) r0;
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.to_id;
            r22 = r0;
            r0 = r22;
            r0 = r0.channel_id;
            r22 = r0;
            if (r22 == 0) goto L_0x0080;
        L_0x0065:
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.to_id;
            r22 = r0;
            r0 = r22;
            r0 = r0.channel_id;
            r22 = r0;
            r0 = r22;
            r0 = (long) r0;
            r22 = r0;
            r24 = 32;
            r22 = r22 << r24;
            r12 = r12 | r22;
        L_0x0080:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22 = r22.pushMessagesDict;
            r23 = java.lang.Long.valueOf(r12);
            r22 = r22.containsKey(r23);
            if (r22 == 0) goto L_0x0097;
        L_0x0094:
            r4 = r4 + 1;
            goto L_0x002a;
        L_0x0097:
            r8 = r10.getDialogId();
            r16 = r8;
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22 = r22.opened_dialog_id;
            r22 = (r8 > r22 ? 1 : (r8 == r22 ? 0 : -1));
            if (r22 != 0) goto L_0x00b9;
        L_0x00ab:
            r22 = org.telegram.messenger.ApplicationLoader.isScreenOn;
            if (r22 == 0) goto L_0x00b9;
        L_0x00af:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22.playInChatSound();
            goto L_0x0094;
        L_0x00b9:
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.mentioned;
            r22 = r0;
            if (r22 == 0) goto L_0x00e6;
        L_0x00c5:
            if (r6 != 0) goto L_0x00d9;
        L_0x00c7:
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.action;
            r22 = r0;
            r0 = r22;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
            r22 = r0;
            if (r22 != 0) goto L_0x0094;
        L_0x00d9:
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.from_id;
            r22 = r0;
            r0 = r22;
            r8 = (long) r0;
        L_0x00e6:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r0 = r22;
            r22 = r0.isPersonalMessage(r10);
            if (r22 == 0) goto L_0x00fd;
        L_0x00f4:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22.personal_count = r22.personal_count + 1;
        L_0x00fd:
            r5 = 1;
            r22 = java.lang.Long.valueOf(r8);
            r0 = r20;
            r1 = r22;
            r21 = r0.get(r1);
            r21 = (java.lang.Boolean) r21;
            r0 = (int) r8;
            r22 = r0;
            if (r22 >= 0) goto L_0x01eb;
        L_0x0111:
            r7 = 1;
        L_0x0112:
            r0 = (int) r8;
            r22 = r0;
            if (r22 != 0) goto L_0x01ee;
        L_0x0117:
            r15 = 0;
        L_0x0118:
            if (r21 != 0) goto L_0x0165;
        L_0x011a:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r0 = r22;
            r1 = r19;
            r11 = r0.getNotifyOverride(r1, r8);
            r22 = 2;
            r0 = r22;
            if (r11 == r0) goto L_0x0203;
        L_0x012e:
            r22 = "EnableAll";
            r23 = 1;
            r0 = r19;
            r1 = r22;
            r2 = r23;
            r22 = r0.getBoolean(r1, r2);
            if (r22 == 0) goto L_0x0150;
        L_0x013e:
            if (r7 == 0) goto L_0x0152;
        L_0x0140:
            r22 = "EnableGroup";
            r23 = 1;
            r0 = r19;
            r1 = r22;
            r2 = r23;
            r22 = r0.getBoolean(r1, r2);
            if (r22 != 0) goto L_0x0152;
        L_0x0150:
            if (r11 == 0) goto L_0x0203;
        L_0x0152:
            r22 = 1;
        L_0x0154:
            r21 = java.lang.Boolean.valueOf(r22);
            r22 = java.lang.Long.valueOf(r8);
            r0 = r20;
            r1 = r22;
            r2 = r21;
            r0.put(r1, r2);
        L_0x0165:
            if (r15 == 0) goto L_0x0180;
        L_0x0167:
            r0 = r10.messageOwner;
            r22 = r0;
            r0 = r22;
            r0 = r0.to_id;
            r22 = r0;
            r0 = r22;
            r0 = r0.channel_id;
            r22 = r0;
            if (r22 == 0) goto L_0x0180;
        L_0x0179:
            r22 = r10.isMegagroup();
            if (r22 != 0) goto L_0x0180;
        L_0x017f:
            r15 = 0;
        L_0x0180:
            r22 = r21.booleanValue();
            if (r22 == 0) goto L_0x0094;
        L_0x0186:
            if (r15 == 0) goto L_0x0197;
        L_0x0188:
            r0 = r25;
            r0 = r0.val$popupArray;
            r22 = r0;
            r23 = 0;
            r0 = r22;
            r1 = r23;
            r0.add(r1, r10);
        L_0x0197:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22 = r22.delayedPushMessages;
            r0 = r22;
            r0.add(r10);
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22 = r22.pushMessages;
            r23 = 0;
            r0 = r22;
            r1 = r23;
            r0.add(r1, r10);
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22 = r22.pushMessagesDict;
            r23 = java.lang.Long.valueOf(r12);
            r0 = r22;
            r1 = r23;
            r0.put(r1, r10);
            r22 = (r16 > r8 ? 1 : (r16 == r8 ? 0 : -1));
            if (r22 == 0) goto L_0x0094;
        L_0x01d2:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r22 = r22.pushDialogsOverrideMention;
            r23 = java.lang.Long.valueOf(r16);
            r24 = 1;
            r24 = java.lang.Integer.valueOf(r24);
            r22.put(r23, r24);
            goto L_0x0094;
        L_0x01eb:
            r7 = 0;
            goto L_0x0112;
        L_0x01ee:
            if (r7 == 0) goto L_0x0200;
        L_0x01f0:
            r22 = "popupGroup";
        L_0x01f2:
            r23 = 0;
            r0 = r19;
            r1 = r22;
            r2 = r23;
            r15 = r0.getInt(r1, r2);
            goto L_0x0118;
        L_0x0200:
            r22 = "popupAll";
            goto L_0x01f2;
        L_0x0203:
            r22 = 0;
            goto L_0x0154;
        L_0x0207:
            if (r5 == 0) goto L_0x0218;
        L_0x0209:
            r0 = r25;
            r0 = org.telegram.messenger.NotificationsController.this;
            r22 = r0;
            r0 = r25;
            r0 = r0.val$isLast;
            r23 = r0;
            r22.notifyCheck = r23;
        L_0x0218:
            r0 = r25;
            r0 = r0.val$popupArray;
            r22 = r0;
            r22 = r22.isEmpty();
            if (r22 != 0) goto L_0x024a;
        L_0x0224:
            r0 = r25;
            r0 = r0.val$popupArray;
            r22 = r0;
            r22 = r22.size();
            r0 = r22;
            if (r14 == r0) goto L_0x024a;
        L_0x0232:
            r22 = 0;
            r22 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r22);
            if (r22 != 0) goto L_0x024a;
        L_0x023a:
            r18 = r15;
            r22 = new org.telegram.messenger.NotificationsController$7$1;
            r0 = r22;
            r1 = r25;
            r2 = r18;
            r0.<init>(r2);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r22);
        L_0x024a:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.7.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.8 */
    class C06888 implements Runnable {
        final /* synthetic */ HashMap val$dialogsToUpdate;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: org.telegram.messenger.NotificationsController.8.1 */
        class C06871 implements Runnable {
            C06871() {
            }

            public void run() {
                NotificationsController.this.popupMessages = C06888.this.val$popupArray;
            }
        }

        C06888(HashMap hashMap, ArrayList arrayList) {
            this.val$dialogsToUpdate = hashMap;
            this.val$popupArray = arrayList;
        }

        public void run() {
            int old_unread_count = NotificationsController.this.total_unread_count;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            for (Entry<Long, Integer> entry : this.val$dialogsToUpdate.entrySet()) {
                long dialog_id = ((Long) entry.getKey()).longValue();
                int notifyOverride = NotificationsController.this.getNotifyOverride(preferences, dialog_id);
                if (NotificationsController.this.notifyCheck) {
                    Integer override = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(dialog_id));
                    if (override != null && override.intValue() == 1) {
                        NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(dialog_id), Integer.valueOf(0));
                        notifyOverride = 1;
                    }
                }
                boolean canAddValue = notifyOverride != 2 && ((preferences.getBoolean("EnableAll", true) && (((int) dialog_id) >= 0 || preferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                Integer currentCount = (Integer) NotificationsController.this.pushDialogs.get(Long.valueOf(dialog_id));
                Integer newCount = (Integer) entry.getValue();
                if (newCount.intValue() == 0) {
                    NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(dialog_id));
                }
                if (newCount.intValue() < 0) {
                    if (currentCount != null) {
                        newCount = Integer.valueOf(currentCount.intValue() + newCount.intValue());
                    }
                }
                if ((canAddValue || newCount.intValue() == 0) && currentCount != null) {
                    NotificationsController.access$420(NotificationsController.this, currentCount.intValue());
                }
                if (newCount.intValue() == 0) {
                    NotificationsController.this.pushDialogs.remove(Long.valueOf(dialog_id));
                    NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(dialog_id));
                    int a = 0;
                    while (true) {
                        if (a >= NotificationsController.this.pushMessages.size()) {
                            break;
                        }
                        MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                        if (messageObject.getDialogId() == dialog_id) {
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            NotificationsController.this.pushMessages.remove(a);
                            a--;
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            long mid = (long) messageObject.messageOwner.id;
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            NotificationsController.this.pushMessagesDict.remove(Long.valueOf(mid));
                            if (this.val$popupArray != null) {
                                this.val$popupArray.remove(messageObject);
                            }
                        }
                        a++;
                    }
                    if (this.val$popupArray != null) {
                        if (NotificationsController.this.pushMessages.isEmpty()) {
                            if (!this.val$popupArray.isEmpty()) {
                                this.val$popupArray.clear();
                            }
                        }
                    }
                } else if (canAddValue) {
                    NotificationsController.access$412(NotificationsController.this, newCount.intValue());
                    NotificationsController.this.pushDialogs.put(Long.valueOf(dialog_id), newCount);
                }
            }
            if (this.val$popupArray != null) {
                AndroidUtilities.runOnUIThread(new C06871());
            }
            if (old_unread_count != NotificationsController.this.total_unread_count) {
                if (NotificationsController.this.notifyCheck) {
                    NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime());
                } else {
                    NotificationsController.this.delayedPushMessages.clear();
                    NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                }
            }
            NotificationsController.this.notifyCheck = false;
            if (preferences.getBoolean("badgeNumber", true)) {
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController.9 */
    class C06909 implements Runnable {
        final /* synthetic */ HashMap val$dialogs;
        final /* synthetic */ ArrayList val$messages;

        /* renamed from: org.telegram.messenger.NotificationsController.9.1 */
        class C06891 implements Runnable {
            C06891() {
            }

            public void run() {
                NotificationsController.this.popupMessages.clear();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
        }

        C06909(ArrayList arrayList, HashMap hashMap) {
            this.val$messages = arrayList;
            this.val$dialogs = hashMap;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r26 = this;
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushDialogs;
            r20.clear();
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushMessages;
            r20.clear();
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushMessagesDict;
            r20.clear();
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r21 = 0;
            r20.total_unread_count = r21;
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r21 = 0;
            r20.personal_count = r21;
            r20 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r21 = "Notifications";
            r22 = 0;
            r17 = r20.getSharedPreferences(r21, r22);
            r18 = new java.util.HashMap;
            r18.<init>();
            r0 = r26;
            r0 = r0.val$messages;
            r20 = r0;
            if (r20 == 0) goto L_0x01a6;
        L_0x0054:
            r0 = r26;
            r0 = r0.val$messages;
            r20 = r0;
            r8 = r20.iterator();
        L_0x005e:
            r20 = r8.hasNext();
            if (r20 == 0) goto L_0x01a6;
        L_0x0064:
            r9 = r8.next();
            r9 = (org.telegram.tgnet.TLRPC.Message) r9;
            r0 = r9.id;
            r20 = r0;
            r0 = r20;
            r12 = (long) r0;
            r0 = r9.to_id;
            r20 = r0;
            r0 = r20;
            r0 = r0.channel_id;
            r20 = r0;
            if (r20 == 0) goto L_0x0092;
        L_0x007d:
            r0 = r9.to_id;
            r20 = r0;
            r0 = r20;
            r0 = r0.channel_id;
            r20 = r0;
            r0 = r20;
            r0 = (long) r0;
            r20 = r0;
            r22 = 32;
            r20 = r20 << r22;
            r12 = r12 | r20;
        L_0x0092:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushMessagesDict;
            r21 = java.lang.Long.valueOf(r12);
            r20 = r20.containsKey(r21);
            if (r20 != 0) goto L_0x005e;
        L_0x00a6:
            r10 = new org.telegram.messenger.MessageObject;
            r20 = 0;
            r21 = 0;
            r0 = r20;
            r1 = r21;
            r10.<init>(r9, r0, r1);
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r0 = r20;
            r20 = r0.isPersonalMessage(r10);
            if (r20 == 0) goto L_0x00ca;
        L_0x00c1:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r22.personal_count = r20.personal_count + 1;
        L_0x00ca:
            r6 = r10.getDialogId();
            r14 = r6;
            r0 = r10.messageOwner;
            r20 = r0;
            r0 = r20;
            r0 = r0.mentioned;
            r20 = r0;
            if (r20 == 0) goto L_0x00e8;
        L_0x00db:
            r0 = r10.messageOwner;
            r20 = r0;
            r0 = r20;
            r0 = r0.from_id;
            r20 = r0;
            r0 = r20;
            r6 = (long) r0;
        L_0x00e8:
            r20 = java.lang.Long.valueOf(r6);
            r0 = r18;
            r1 = r20;
            r19 = r0.get(r1);
            r19 = (java.lang.Boolean) r19;
            if (r19 != 0) goto L_0x0146;
        L_0x00f8:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r0 = r20;
            r1 = r17;
            r11 = r0.getNotifyOverride(r1, r6);
            r20 = 2;
            r0 = r20;
            if (r11 == r0) goto L_0x01a3;
        L_0x010c:
            r20 = "EnableAll";
            r21 = 1;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r20 = r0.getBoolean(r1, r2);
            if (r20 == 0) goto L_0x0131;
        L_0x011c:
            r0 = (int) r6;
            r20 = r0;
            if (r20 >= 0) goto L_0x0133;
        L_0x0121:
            r20 = "EnableGroup";
            r21 = 1;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r20 = r0.getBoolean(r1, r2);
            if (r20 != 0) goto L_0x0133;
        L_0x0131:
            if (r11 == 0) goto L_0x01a3;
        L_0x0133:
            r20 = 1;
        L_0x0135:
            r19 = java.lang.Boolean.valueOf(r20);
            r20 = java.lang.Long.valueOf(r6);
            r0 = r18;
            r1 = r20;
            r2 = r19;
            r0.put(r1, r2);
        L_0x0146:
            r20 = r19.booleanValue();
            if (r20 == 0) goto L_0x005e;
        L_0x014c:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.opened_dialog_id;
            r20 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1));
            if (r20 != 0) goto L_0x015e;
        L_0x015a:
            r20 = org.telegram.messenger.ApplicationLoader.isScreenOn;
            if (r20 != 0) goto L_0x005e;
        L_0x015e:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushMessagesDict;
            r21 = java.lang.Long.valueOf(r12);
            r0 = r20;
            r1 = r21;
            r0.put(r1, r10);
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushMessages;
            r21 = 0;
            r0 = r20;
            r1 = r21;
            r0.add(r1, r10);
            r20 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1));
            if (r20 == 0) goto L_0x005e;
        L_0x018a:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushDialogsOverrideMention;
            r21 = java.lang.Long.valueOf(r14);
            r22 = 1;
            r22 = java.lang.Integer.valueOf(r22);
            r20.put(r21, r22);
            goto L_0x005e;
        L_0x01a3:
            r20 = 0;
            goto L_0x0135;
        L_0x01a6:
            r0 = r26;
            r0 = r0.val$dialogs;
            r20 = r0;
            r20 = r20.entrySet();
            r8 = r20.iterator();
        L_0x01b4:
            r20 = r8.hasNext();
            if (r20 == 0) goto L_0x0297;
        L_0x01ba:
            r5 = r8.next();
            r5 = (java.util.Map.Entry) r5;
            r20 = r5.getKey();
            r20 = (java.lang.Long) r20;
            r6 = r20.longValue();
            r20 = java.lang.Long.valueOf(r6);
            r0 = r18;
            r1 = r20;
            r19 = r0.get(r1);
            r19 = (java.lang.Boolean) r19;
            if (r19 != 0) goto L_0x0262;
        L_0x01da:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r0 = r20;
            r1 = r17;
            r11 = r0.getNotifyOverride(r1, r6);
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushDialogsOverrideMention;
            r21 = java.lang.Long.valueOf(r6);
            r16 = r20.get(r21);
            r16 = (java.lang.Integer) r16;
            if (r16 == 0) goto L_0x0222;
        L_0x01fe:
            r20 = r16.intValue();
            r21 = 1;
            r0 = r20;
            r1 = r21;
            if (r0 != r1) goto L_0x0222;
        L_0x020a:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushDialogsOverrideMention;
            r21 = java.lang.Long.valueOf(r6);
            r22 = 0;
            r22 = java.lang.Integer.valueOf(r22);
            r20.put(r21, r22);
            r11 = 1;
        L_0x0222:
            r20 = 2;
            r0 = r20;
            if (r11 == r0) goto L_0x0294;
        L_0x0228:
            r20 = "EnableAll";
            r21 = 1;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r20 = r0.getBoolean(r1, r2);
            if (r20 == 0) goto L_0x024d;
        L_0x0238:
            r0 = (int) r6;
            r20 = r0;
            if (r20 >= 0) goto L_0x024f;
        L_0x023d:
            r20 = "EnableGroup";
            r21 = 1;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r20 = r0.getBoolean(r1, r2);
            if (r20 != 0) goto L_0x024f;
        L_0x024d:
            if (r11 == 0) goto L_0x0294;
        L_0x024f:
            r20 = 1;
        L_0x0251:
            r19 = java.lang.Boolean.valueOf(r20);
            r20 = java.lang.Long.valueOf(r6);
            r0 = r18;
            r1 = r20;
            r2 = r19;
            r0.put(r1, r2);
        L_0x0262:
            r20 = r19.booleanValue();
            if (r20 == 0) goto L_0x01b4;
        L_0x0268:
            r20 = r5.getValue();
            r20 = (java.lang.Integer) r20;
            r4 = r20.intValue();
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.pushDialogs;
            r21 = java.lang.Long.valueOf(r6);
            r22 = java.lang.Integer.valueOf(r4);
            r20.put(r21, r22);
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r0 = r20;
            org.telegram.messenger.NotificationsController.access$412(r0, r4);
            goto L_0x01b4;
        L_0x0294:
            r20 = 0;
            goto L_0x0251;
        L_0x0297:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r20 = r20.total_unread_count;
            if (r20 != 0) goto L_0x02af;
        L_0x02a3:
            r20 = new org.telegram.messenger.NotificationsController$9$1;
            r0 = r20;
            r1 = r26;
            r0.<init>();
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r20);
        L_0x02af:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r21 = r0;
            r22 = android.os.SystemClock.uptimeMillis();
            r24 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r22 = r22 / r24;
            r24 = 60;
            r20 = (r22 > r24 ? 1 : (r22 == r24 ? 0 : -1));
            if (r20 >= 0) goto L_0x02f0;
        L_0x02c3:
            r20 = 1;
        L_0x02c5:
            r0 = r21;
            r1 = r20;
            r0.showOrUpdateNotification(r1);
            r20 = "badgeNumber";
            r21 = 1;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r20 = r0.getBoolean(r1, r2);
            if (r20 == 0) goto L_0x02ef;
        L_0x02dc:
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r20 = r0;
            r0 = r26;
            r0 = org.telegram.messenger.NotificationsController.this;
            r21 = r0;
            r21 = r21.total_unread_count;
            r20.setBadge(r21);
        L_0x02ef:
            return;
        L_0x02f0:
            r20 = 0;
            goto L_0x02c5;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.9.run():void");
        }
    }

    static /* synthetic */ int access$412(NotificationsController x0, int x1) {
        int i = x0.total_unread_count + x1;
        x0.total_unread_count = i;
        return i;
    }

    static /* synthetic */ int access$420(NotificationsController x0, int x1) {
        int i = x0.total_unread_count - x1;
        x0.total_unread_count = i;
        return i;
    }

    static {
        Instance = null;
    }

    public static NotificationsController getInstance() {
        NotificationsController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        NotificationsController localInstance2 = new NotificationsController();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public NotificationsController() {
        this.notificationsQueue = new DispatchQueue("notificationsQueue");
        this.pushMessages = new ArrayList();
        this.delayedPushMessages = new ArrayList();
        this.pushMessagesDict = new HashMap();
        this.smartNotificationsDialogs = new HashMap();
        this.notificationManager = null;
        this.pushDialogs = new HashMap();
        this.wearNotificationsIds = new HashMap();
        this.autoNotificationsIds = new HashMap();
        this.pushDialogsOverrideMention = new HashMap();
        this.wearNotificationId = AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
        this.autoNotificationId = 20000;
        this.popupMessages = new ArrayList();
        this.opened_dialog_id = 0;
        this.total_unread_count = 0;
        this.personal_count = 0;
        this.notifyCheck = false;
        this.lastOnlineFromOtherDevice = 0;
        this.inChatSoundEnabled = true;
        this.notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        this.inChatSoundEnabled = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnableInChatSound", true);
        try {
            this.audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi21.CATEGORY_ALARM);
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        try {
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, JoinPoint.SYNCHRONIZATION_LOCK);
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Throwable e22) {
            FileLog.m13e("tmessages", e22);
        }
        this.notificationDelayRunnable = new C06771();
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.notificationsQueue.postRunnable(new C06782());
    }

    public void setInChatSoundEnabled(boolean value) {
        this.inChatSoundEnabled = value;
    }

    public void setOpenedDialogId(long dialog_id) {
        this.notificationsQueue.postRunnable(new C06793(dialog_id));
    }

    public void setLastOnlineFromOtherDevice(int time) {
        this.notificationsQueue.postRunnable(new C06804(time));
    }

    public void removeNotificationsForDialog(long did) {
        getInstance().processReadMessages(null, did, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
        HashMap<Long, Integer> dialogsToUpdate = new HashMap();
        dialogsToUpdate.put(Long.valueOf(did), Integer.valueOf(0));
        getInstance().processDialogsUpdateRead(dialogsToUpdate);
    }

    public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> deletedMessages) {
        this.notificationsQueue.postRunnable(new C06825(deletedMessages, this.popupMessages.isEmpty() ? null : new ArrayList(this.popupMessages)));
    }

    public void processReadMessages(SparseArray<Long> inbox, long dialog_id, int max_date, int max_id, boolean isPopup) {
        this.notificationsQueue.postRunnable(new C06846(this.popupMessages.isEmpty() ? null : new ArrayList(this.popupMessages), inbox, dialog_id, max_id, max_date, isPopup));
    }

    public void processNewMessages(ArrayList<MessageObject> messageObjects, boolean isLast) {
        if (!messageObjects.isEmpty()) {
            this.notificationsQueue.postRunnable(new C06867(new ArrayList(this.popupMessages), messageObjects, isLast));
        }
    }

    public void processDialogsUpdateRead(HashMap<Long, Integer> dialogsToUpdate) {
        this.notificationsQueue.postRunnable(new C06888(dialogsToUpdate, this.popupMessages.isEmpty() ? null : new ArrayList(this.popupMessages)));
    }

    public void processLoadedUnreadMessages(HashMap<Long, Integer> dialogs, ArrayList<Message> messages, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        MessagesController.getInstance().putUsers(users, true);
        MessagesController.getInstance().putChats(chats, true);
        MessagesController.getInstance().putEncryptedChats(encryptedChats, true);
        this.notificationsQueue.postRunnable(new C06909(messages, dialogs));
    }

    public void setBadgeEnabled(boolean enabled) {
        setBadge(enabled ? this.total_unread_count : 0);
    }

    private void setBadge(int count) {
        this.notificationsQueue.postRunnable(new AnonymousClass10(count));
    }

    private String getStringForMessage(MessageObject messageObject, boolean shortMessage) {
        int chat_id;
        User user;
        Chat chat;
        long dialog_id = messageObject.messageOwner.dialog_id;
        if (messageObject.messageOwner.to_id.chat_id != 0) {
            chat_id = messageObject.messageOwner.to_id.chat_id;
        } else {
            chat_id = messageObject.messageOwner.to_id.channel_id;
        }
        int from_id = messageObject.messageOwner.to_id.user_id;
        if (from_id == 0) {
            if (messageObject.isFromUser() || messageObject.getId() < 0) {
                from_id = messageObject.messageOwner.from_id;
            } else {
                from_id = -chat_id;
            }
        } else if (from_id == UserConfig.getClientUserId()) {
            from_id = messageObject.messageOwner.from_id;
        }
        if (dialog_id == 0) {
            if (chat_id != 0) {
                dialog_id = (long) (-chat_id);
            } else if (from_id != 0) {
                dialog_id = (long) from_id;
            }
        }
        String name = null;
        if (from_id > 0) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(from_id));
            if (user != null) {
                name = UserObject.getUserName(user);
            }
        } else {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(-from_id));
            if (chat != null) {
                name = chat.title;
            }
        }
        if (name == null) {
            return null;
        }
        chat = null;
        if (chat_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(chat_id));
            if (chat == null) {
                return null;
            }
        }
        if (((int) dialog_id) == 0 || AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter) {
            return LocaleController.getString("YouHaveNewMessage", C0691R.string.YouHaveNewMessage);
        }
        Object[] objArr;
        if (chat_id == 0 && from_id != 0) {
            if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewAll", true)) {
                if (messageObject.messageOwner instanceof TL_messageService) {
                    if (messageObject.messageOwner.action instanceof TL_messageActionUserJoined) {
                        return LocaleController.formatString("NotificationContactJoined", C0691R.string.NotificationContactJoined, name);
                    }
                    if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                        return LocaleController.formatString("NotificationContactNewPhoto", C0691R.string.NotificationContactNewPhoto, name);
                    }
                    if (!(messageObject.messageOwner.action instanceof TL_messageActionLoginUnknownLocation)) {
                        return null;
                    }
                    objArr = new Object[2];
                    objArr[0] = LocaleController.getInstance().formatterYear.format(((long) messageObject.messageOwner.date) * 1000);
                    objArr[1] = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
                    String date = LocaleController.formatString("formatDateAtTime", C0691R.string.formatDateAtTime, objArr);
                    objArr = new Object[4];
                    objArr[0] = UserConfig.getCurrentUser().first_name;
                    objArr[1] = date;
                    objArr[2] = messageObject.messageOwner.action.title;
                    objArr[3] = messageObject.messageOwner.action.address;
                    return LocaleController.formatString("NotificationUnrecognizedDevice", C0691R.string.NotificationUnrecognizedDevice, objArr);
                } else if (!messageObject.isMediaEmpty()) {
                    if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                        return LocaleController.formatString("NotificationMessagePhoto", C0691R.string.NotificationMessagePhoto, name);
                    } else if (messageObject.isVideo()) {
                        return LocaleController.formatString("NotificationMessageVideo", C0691R.string.NotificationMessageVideo, name);
                    } else if (messageObject.isVoice()) {
                        return LocaleController.formatString("NotificationMessageAudio", C0691R.string.NotificationMessageAudio, name);
                    } else if (messageObject.isMusic()) {
                        return LocaleController.formatString("NotificationMessageMusic", C0691R.string.NotificationMessageMusic, name);
                    } else {
                        if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                            return LocaleController.formatString("NotificationMessageContact", C0691R.string.NotificationMessageContact, name);
                        }
                        if (!(messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                            if (!(messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                                    return null;
                                }
                                if (messageObject.isSticker()) {
                                    if (messageObject.getStickerEmoji() != null) {
                                        return LocaleController.formatString("NotificationMessageStickerEmoji", C0691R.string.NotificationMessageStickerEmoji, name, messageObject.getStickerEmoji());
                                    }
                                    return LocaleController.formatString("NotificationMessageSticker", C0691R.string.NotificationMessageSticker, name);
                                } else if (messageObject.isGif()) {
                                    return LocaleController.formatString("NotificationMessageGif", C0691R.string.NotificationMessageGif, name);
                                } else {
                                    return LocaleController.formatString("NotificationMessageDocument", C0691R.string.NotificationMessageDocument, name);
                                }
                            }
                        }
                        return LocaleController.formatString("NotificationMessageMap", C0691R.string.NotificationMessageMap, name);
                    }
                } else if (shortMessage) {
                    return LocaleController.formatString("NotificationMessageNoText", C0691R.string.NotificationMessageNoText, name);
                } else {
                    if (messageObject.messageOwner.message != null) {
                        if (messageObject.messageOwner.message.length() != 0) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = messageObject.messageOwner.message;
                            return LocaleController.formatString("NotificationMessageText", C0691R.string.NotificationMessageText, objArr);
                        }
                    }
                    return LocaleController.formatString("NotificationMessageNoText", C0691R.string.NotificationMessageNoText, name);
                }
            }
            return LocaleController.formatString("NotificationMessageNoText", C0691R.string.NotificationMessageNoText, name);
        } else if (chat_id == 0) {
            return null;
        } else {
            if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewGroup", true)) {
                String emoji;
                if (messageObject.messageOwner instanceof TL_messageService) {
                    User u2;
                    int i;
                    if (messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) {
                        int singleUserId = messageObject.messageOwner.action.user_id;
                        if (singleUserId == 0) {
                            if (messageObject.messageOwner.action.users.size() == 1) {
                                singleUserId = ((Integer) messageObject.messageOwner.action.users.get(0)).intValue();
                            }
                        }
                        if (singleUserId != 0) {
                            if (messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isMegagroup()) {
                                objArr = new Object[2];
                                objArr[0] = name;
                                objArr[1] = chat.title;
                                return LocaleController.formatString("ChannelAddedByNotification", C0691R.string.ChannelAddedByNotification, objArr);
                            } else if (singleUserId == UserConfig.getClientUserId()) {
                                objArr = new Object[2];
                                objArr[0] = name;
                                objArr[1] = chat.title;
                                return LocaleController.formatString("NotificationInvitedToGroup", C0691R.string.NotificationInvitedToGroup, objArr);
                            } else {
                                u2 = MessagesController.getInstance().getUser(Integer.valueOf(singleUserId));
                                if (u2 == null) {
                                    return null;
                                }
                                i = u2.id;
                                if (from_id != r0) {
                                    objArr = new Object[3];
                                    objArr[1] = chat.title;
                                    objArr[2] = UserObject.getUserName(u2);
                                    return LocaleController.formatString("NotificationGroupAddMember", C0691R.string.NotificationGroupAddMember, objArr);
                                } else if (messageObject.isMegagroup()) {
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationGroupAddSelfMega", C0691R.string.NotificationGroupAddSelfMega, objArr);
                                } else {
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationGroupAddSelf", C0691R.string.NotificationGroupAddSelf, objArr);
                                }
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                        int a = 0;
                        while (true) {
                            if (a < messageObject.messageOwner.action.users.size()) {
                                user = MessagesController.getInstance().getUser((Integer) messageObject.messageOwner.action.users.get(a));
                                if (user != null) {
                                    String name2 = UserObject.getUserName(user);
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(", ");
                                    }
                                    stringBuilder.append(name2);
                                }
                                a++;
                            } else {
                                objArr = new Object[3];
                                objArr[1] = chat.title;
                                objArr[2] = stringBuilder.toString();
                                return LocaleController.formatString("NotificationGroupAddMember", C0691R.string.NotificationGroupAddMember, objArr);
                            }
                        }
                    }
                    if (messageObject.messageOwner.action instanceof TL_messageActionChatJoinedByLink) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationInvitedToGroupByLink", C0691R.string.NotificationInvitedToGroupByLink, objArr);
                    }
                    if (messageObject.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = messageObject.messageOwner.action.title;
                        return LocaleController.formatString("NotificationEditedGroupName", C0691R.string.NotificationEditedGroupName, objArr);
                    }
                    if (!(messageObject.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                        if (!(messageObject.messageOwner.action instanceof TL_messageActionChatDeletePhoto)) {
                            if (messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                                if (messageObject.messageOwner.action.user_id == UserConfig.getClientUserId()) {
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationGroupKickYou", C0691R.string.NotificationGroupKickYou, objArr);
                                }
                                i = messageObject.messageOwner.action.user_id;
                                if (r0 == from_id) {
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationGroupLeftMember", C0691R.string.NotificationGroupLeftMember, objArr);
                                }
                                u2 = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.action.user_id));
                                if (u2 == null) {
                                    return null;
                                }
                                objArr = new Object[3];
                                objArr[1] = chat.title;
                                objArr[2] = UserObject.getUserName(u2);
                                return LocaleController.formatString("NotificationGroupKickMember", C0691R.string.NotificationGroupKickMember, objArr);
                            }
                            if (messageObject.messageOwner.action instanceof TL_messageActionChatCreate) {
                                return messageObject.messageText.toString();
                            }
                            if (messageObject.messageOwner.action instanceof TL_messageActionChannelCreate) {
                                return messageObject.messageText.toString();
                            }
                            if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                                objArr = new Object[1];
                                objArr[0] = chat.title;
                                return LocaleController.formatString("ActionMigrateFromGroupNotify", C0691R.string.ActionMigrateFromGroupNotify, objArr);
                            }
                            if (messageObject.messageOwner.action instanceof TL_messageActionChannelMigrateFrom) {
                                objArr = new Object[1];
                                objArr[0] = messageObject.messageOwner.action.title;
                                return LocaleController.formatString("ActionMigrateFromGroupNotify", C0691R.string.ActionMigrateFromGroupNotify, objArr);
                            }
                            if (!(messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                return null;
                            }
                            if (messageObject.replyMessageObject != null) {
                                MessageObject object = messageObject.replyMessageObject;
                                if (object.isMusic()) {
                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedMusic", C0691R.string.NotificationActionPinnedMusic, objArr);
                                    }
                                    objArr = new Object[1];
                                    objArr[0] = chat.title;
                                    return LocaleController.formatString("NotificationActionPinnedMusicChannel", C0691R.string.NotificationActionPinnedMusicChannel, objArr);
                                } else if (object.isVideo()) {
                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedVideo", C0691R.string.NotificationActionPinnedVideo, objArr);
                                    }
                                    objArr = new Object[1];
                                    objArr[0] = chat.title;
                                    return LocaleController.formatString("NotificationActionPinnedVideoChannel", C0691R.string.NotificationActionPinnedVideoChannel, objArr);
                                } else if (object.isGif()) {
                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedGif", C0691R.string.NotificationActionPinnedGif, objArr);
                                    }
                                    objArr = new Object[1];
                                    objArr[0] = chat.title;
                                    return LocaleController.formatString("NotificationActionPinnedGifChannel", C0691R.string.NotificationActionPinnedGifChannel, objArr);
                                } else if (object.isVoice()) {
                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedVoice", C0691R.string.NotificationActionPinnedVoice, objArr);
                                    }
                                    objArr = new Object[1];
                                    objArr[0] = chat.title;
                                    return LocaleController.formatString("NotificationActionPinnedVoiceChannel", C0691R.string.NotificationActionPinnedVoiceChannel, objArr);
                                } else if (object.isSticker()) {
                                    emoji = messageObject.getStickerEmoji();
                                    if (emoji != null) {
                                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                            objArr = new Object[3];
                                            objArr[1] = chat.title;
                                            objArr[2] = emoji;
                                            return LocaleController.formatString("NotificationActionPinnedStickerEmoji", C0691R.string.NotificationActionPinnedStickerEmoji, objArr);
                                        }
                                        objArr = new Object[2];
                                        objArr[0] = chat.title;
                                        objArr[1] = emoji;
                                        return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", C0691R.string.NotificationActionPinnedStickerEmojiChannel, objArr);
                                    } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedSticker", C0691R.string.NotificationActionPinnedSticker, objArr);
                                    } else {
                                        objArr = new Object[1];
                                        objArr[0] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedStickerChannel", C0691R.string.NotificationActionPinnedStickerChannel, objArr);
                                    }
                                } else {
                                    if (!(object.messageOwner.media instanceof TL_messageMediaDocument)) {
                                        if (!(object.messageOwner.media instanceof TL_messageMediaGeo)) {
                                            if (!(object.messageOwner.media instanceof TL_messageMediaContact)) {
                                                if (!(object.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                                    if (object.messageText != null) {
                                                        if (object.messageText.length() > 0) {
                                                            CharSequence message = object.messageText;
                                                            if (message.length() > 20) {
                                                                message = message.subSequence(0, 20) + "...";
                                                            }
                                                            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                                objArr = new Object[3];
                                                                objArr[0] = name;
                                                                objArr[1] = message;
                                                                objArr[2] = chat.title;
                                                                return LocaleController.formatString("NotificationActionPinnedText", C0691R.string.NotificationActionPinnedText, objArr);
                                                            }
                                                            objArr = new Object[2];
                                                            objArr[0] = chat.title;
                                                            objArr[1] = message;
                                                            return LocaleController.formatString("NotificationActionPinnedTextChannel", C0691R.string.NotificationActionPinnedTextChannel, objArr);
                                                        }
                                                    }
                                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                        objArr = new Object[2];
                                                        objArr[0] = name;
                                                        objArr[1] = chat.title;
                                                        return LocaleController.formatString("NotificationActionPinnedNoText", C0691R.string.NotificationActionPinnedNoText, objArr);
                                                    }
                                                    objArr = new Object[1];
                                                    objArr[0] = chat.title;
                                                    return LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0691R.string.NotificationActionPinnedNoTextChannel, objArr);
                                                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    objArr = new Object[2];
                                                    objArr[0] = name;
                                                    objArr[1] = chat.title;
                                                    return LocaleController.formatString("NotificationActionPinnedPhoto", C0691R.string.NotificationActionPinnedPhoto, objArr);
                                                } else {
                                                    objArr = new Object[1];
                                                    objArr[0] = chat.title;
                                                    return LocaleController.formatString("NotificationActionPinnedPhotoChannel", C0691R.string.NotificationActionPinnedPhotoChannel, objArr);
                                                }
                                            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                objArr = new Object[2];
                                                objArr[0] = name;
                                                objArr[1] = chat.title;
                                                return LocaleController.formatString("NotificationActionPinnedContact", C0691R.string.NotificationActionPinnedContact, objArr);
                                            } else {
                                                objArr = new Object[1];
                                                objArr[0] = chat.title;
                                                return LocaleController.formatString("NotificationActionPinnedContactChannel", C0691R.string.NotificationActionPinnedContactChannel, objArr);
                                            }
                                        } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                            objArr = new Object[2];
                                            objArr[0] = name;
                                            objArr[1] = chat.title;
                                            return LocaleController.formatString("NotificationActionPinnedGeo", C0691R.string.NotificationActionPinnedGeo, objArr);
                                        } else {
                                            objArr = new Object[1];
                                            objArr[0] = chat.title;
                                            return LocaleController.formatString("NotificationActionPinnedGeoChannel", C0691R.string.NotificationActionPinnedGeoChannel, objArr);
                                        }
                                    } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedFile", C0691R.string.NotificationActionPinnedFile, objArr);
                                    } else {
                                        objArr = new Object[1];
                                        objArr[0] = chat.title;
                                        return LocaleController.formatString("NotificationActionPinnedFileChannel", C0691R.string.NotificationActionPinnedFileChannel, objArr);
                                    }
                                }
                            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                objArr = new Object[2];
                                objArr[0] = name;
                                objArr[1] = chat.title;
                                return LocaleController.formatString("NotificationActionPinnedNoText", C0691R.string.NotificationActionPinnedNoText, objArr);
                            } else {
                                objArr = new Object[2];
                                objArr[0] = name;
                                objArr[1] = chat.title;
                                return LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0691R.string.NotificationActionPinnedNoTextChannel, objArr);
                            }
                        }
                    }
                    if (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup()) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationEditedGroupPhoto", C0691R.string.NotificationEditedGroupPhoto, objArr);
                    }
                    objArr = new Object[1];
                    objArr[0] = chat.title;
                    return LocaleController.formatString("ChannelPhotoEditNotification", C0691R.string.ChannelPhotoEditNotification, objArr);
                } else if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    if (messageObject.messageOwner.post) {
                        if (messageObject.isMediaEmpty()) {
                            if (!shortMessage) {
                                if (messageObject.messageOwner.message != null) {
                                    if (messageObject.messageOwner.message.length() != 0) {
                                        objArr = new Object[3];
                                        objArr[1] = chat.title;
                                        objArr[2] = messageObject.messageOwner.message;
                                        return LocaleController.formatString("NotificationMessageGroupText", C0691R.string.NotificationMessageGroupText, objArr);
                                    }
                                }
                            }
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageNoText", C0691R.string.ChannelMessageNoText, objArr);
                        }
                        if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessagePhoto", C0691R.string.ChannelMessagePhoto, objArr);
                        } else if (messageObject.isVideo()) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageVideo", C0691R.string.ChannelMessageVideo, objArr);
                        } else if (messageObject.isVoice()) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageAudio", C0691R.string.ChannelMessageAudio, objArr);
                        } else if (messageObject.isMusic()) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageMusic", C0691R.string.ChannelMessageMusic, objArr);
                        } else {
                            if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                                objArr = new Object[2];
                                objArr[0] = name;
                                objArr[1] = chat.title;
                                return LocaleController.formatString("ChannelMessageContact", C0691R.string.ChannelMessageContact, objArr);
                            }
                            if (!(messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                                if (!(messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                    if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                                        return null;
                                    }
                                    if (messageObject.isSticker()) {
                                        emoji = messageObject.getStickerEmoji();
                                        if (emoji != null) {
                                            objArr = new Object[3];
                                            objArr[1] = chat.title;
                                            objArr[2] = emoji;
                                            return LocaleController.formatString("ChannelMessageStickerEmoji", C0691R.string.ChannelMessageStickerEmoji, objArr);
                                        }
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("ChannelMessageSticker", C0691R.string.ChannelMessageSticker, objArr);
                                    } else if (messageObject.isGif()) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("ChannelMessageGIF", C0691R.string.ChannelMessageGIF, objArr);
                                    } else {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("ChannelMessageDocument", C0691R.string.ChannelMessageDocument, objArr);
                                    }
                                }
                            }
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageMap", C0691R.string.ChannelMessageMap, objArr);
                        }
                    } else if (messageObject.isMediaEmpty()) {
                        if (!shortMessage) {
                            if (messageObject.messageOwner.message != null) {
                                if (messageObject.messageOwner.message.length() != 0) {
                                    objArr = new Object[3];
                                    objArr[1] = chat.title;
                                    objArr[2] = messageObject.messageOwner.message;
                                    return LocaleController.formatString("NotificationMessageGroupText", C0691R.string.NotificationMessageGroupText, objArr);
                                }
                            }
                        }
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("ChannelMessageGroupNoText", C0691R.string.ChannelMessageGroupNoText, objArr);
                    } else {
                        if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageGroupPhoto", C0691R.string.ChannelMessageGroupPhoto, objArr);
                        } else if (messageObject.isVideo()) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageGroupVideo", C0691R.string.ChannelMessageGroupVideo, objArr);
                        } else if (messageObject.isVoice()) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageGroupAudio", C0691R.string.ChannelMessageGroupAudio, objArr);
                        } else if (messageObject.isMusic()) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageGroupMusic", C0691R.string.ChannelMessageGroupMusic, objArr);
                        } else {
                            if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                                objArr = new Object[2];
                                objArr[0] = name;
                                objArr[1] = chat.title;
                                return LocaleController.formatString("ChannelMessageGroupContact", C0691R.string.ChannelMessageGroupContact, objArr);
                            }
                            if (!(messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                                if (!(messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                    if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                                        return null;
                                    }
                                    if (messageObject.isSticker()) {
                                        emoji = messageObject.getStickerEmoji();
                                        if (emoji != null) {
                                            objArr = new Object[3];
                                            objArr[1] = chat.title;
                                            objArr[2] = emoji;
                                            return LocaleController.formatString("ChannelMessageGroupStickerEmoji", C0691R.string.ChannelMessageGroupStickerEmoji, objArr);
                                        }
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("ChannelMessageGroupSticker", C0691R.string.ChannelMessageGroupSticker, objArr);
                                    } else if (messageObject.isGif()) {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("ChannelMessageGroupGif", C0691R.string.ChannelMessageGroupGif, objArr);
                                    } else {
                                        objArr = new Object[2];
                                        objArr[0] = name;
                                        objArr[1] = chat.title;
                                        return LocaleController.formatString("ChannelMessageGroupDocument", C0691R.string.ChannelMessageGroupDocument, objArr);
                                    }
                                }
                            }
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("ChannelMessageGroupMap", C0691R.string.ChannelMessageGroupMap, objArr);
                        }
                    }
                } else if (messageObject.isMediaEmpty()) {
                    if (!shortMessage) {
                        if (messageObject.messageOwner.message != null) {
                            if (messageObject.messageOwner.message.length() != 0) {
                                objArr = new Object[3];
                                objArr[1] = chat.title;
                                objArr[2] = messageObject.messageOwner.message;
                                return LocaleController.formatString("NotificationMessageGroupText", C0691R.string.NotificationMessageGroupText, objArr);
                            }
                        }
                    }
                    objArr = new Object[2];
                    objArr[0] = name;
                    objArr[1] = chat.title;
                    return LocaleController.formatString("NotificationMessageGroupNoText", C0691R.string.NotificationMessageGroupNoText, objArr);
                } else {
                    if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationMessageGroupPhoto", C0691R.string.NotificationMessageGroupPhoto, objArr);
                    } else if (messageObject.isVideo()) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationMessageGroupVideo", C0691R.string.NotificationMessageGroupVideo, objArr);
                    } else if (messageObject.isVoice()) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationMessageGroupAudio", C0691R.string.NotificationMessageGroupAudio, objArr);
                    } else if (messageObject.isMusic()) {
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationMessageGroupMusic", C0691R.string.NotificationMessageGroupMusic, objArr);
                    } else {
                        if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                            objArr = new Object[2];
                            objArr[0] = name;
                            objArr[1] = chat.title;
                            return LocaleController.formatString("NotificationMessageGroupContact", C0691R.string.NotificationMessageGroupContact, objArr);
                        }
                        if (!(messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                            if (!(messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                                    return null;
                                }
                                if (messageObject.isSticker()) {
                                    emoji = messageObject.getStickerEmoji();
                                    if (emoji != null) {
                                        objArr = new Object[3];
                                        objArr[1] = chat.title;
                                        objArr[2] = emoji;
                                        return LocaleController.formatString("NotificationMessageGroupStickerEmoji", C0691R.string.NotificationMessageGroupStickerEmoji, objArr);
                                    }
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationMessageGroupSticker", C0691R.string.NotificationMessageGroupSticker, objArr);
                                } else if (messageObject.isGif()) {
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationMessageGroupGif", C0691R.string.NotificationMessageGroupGif, objArr);
                                } else {
                                    objArr = new Object[2];
                                    objArr[0] = name;
                                    objArr[1] = chat.title;
                                    return LocaleController.formatString("NotificationMessageGroupDocument", C0691R.string.NotificationMessageGroupDocument, objArr);
                                }
                            }
                        }
                        objArr = new Object[2];
                        objArr[0] = name;
                        objArr[1] = chat.title;
                        return LocaleController.formatString("NotificationMessageGroupMap", C0691R.string.NotificationMessageGroupMap, objArr);
                    }
                }
            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                objArr = new Object[2];
                objArr[0] = name;
                objArr[1] = chat.title;
                return LocaleController.formatString("NotificationMessageGroupNoText", C0691R.string.NotificationMessageGroupNoText, objArr);
            } else {
                objArr = new Object[2];
                objArr[0] = name;
                objArr[1] = chat.title;
                return LocaleController.formatString("ChannelMessageNoText", C0691R.string.ChannelMessageNoText, objArr);
            }
        }
    }

    private void scheduleNotificationRepeat() {
        try {
            PendingIntent pintent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class), 0);
            int minutes = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("repeat_messages", 60);
            if (minutes <= 0 || this.personal_count <= 0) {
                this.alarmManager.cancel(pintent);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) ((minutes * 60) * 1000)), pintent);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private static String getLauncherClassName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
                if (resolveInfo.activityInfo.applicationInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                    return resolveInfo.activityInfo.name;
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        return null;
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.to_id != null && messageObject.messageOwner.to_id.chat_id == 0 && messageObject.messageOwner.to_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences preferences, long dialog_id) {
        int notifyOverride = preferences.getInt("notify2_" + dialog_id, 0);
        if (notifyOverride != 3 || preferences.getInt("notifyuntil_" + dialog_id, 0) < ConnectionsManager.getInstance().getCurrentTime()) {
            return notifyOverride;
        }
        return 2;
    }

    private void dismissNotification() {
        try {
            this.notificationManager.cancel(1);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            for (Entry<Long, Integer> entry : this.autoNotificationsIds.entrySet()) {
                this.notificationManager.cancel(((Integer) entry.getValue()).intValue());
            }
            this.autoNotificationsIds.clear();
            for (Entry<Long, Integer> entry2 : this.wearNotificationsIds.entrySet()) {
                this.notificationManager.cancel(((Integer) entry2.getValue()).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                }
            });
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (this.audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            try {
                if (getNotifyOverride(ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0), this.opened_dialog_id) != 2) {
                    this.notificationsQueue.postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.NotificationsController.12.1 */
                        class C06751 implements OnLoadCompleteListener {
                            C06751() {
                            }

                            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                if (status == 0) {
                                    soundPool.play(sampleId, TouchHelperCallback.ALPHA_FULL, TouchHelperCallback.ALPHA_FULL, 1, 0, TouchHelperCallback.ALPHA_FULL);
                                }
                            }
                        }

                        public void run() {
                            if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) > 500) {
                                try {
                                    if (NotificationsController.this.soundPool == null) {
                                        NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                        NotificationsController.this.soundPool.setOnLoadCompleteListener(new C06751());
                                    }
                                    if (NotificationsController.this.soundIn == 0 && !NotificationsController.this.soundInLoaded) {
                                        NotificationsController.this.soundInLoaded = true;
                                        NotificationsController.this.soundIn = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0691R.raw.sound_in, 1);
                                    }
                                    if (NotificationsController.this.soundIn != 0) {
                                        NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, TouchHelperCallback.ALPHA_FULL, TouchHelperCallback.ALPHA_FULL, 1, 0, TouchHelperCallback.ALPHA_FULL);
                                    }
                                } catch (Throwable e) {
                                    FileLog.m13e("tmessages", e);
                                }
                            }
                        }
                    });
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
        }
    }

    private void scheduleNotificationDelay(boolean onlineReason) {
        try {
            FileLog.m11e("tmessages", "delay notification start, onlineReason = " + onlineReason);
            this.notificationDelayWakelock.acquire(10000);
            AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
            AndroidUtilities.runOnUIThread(this.notificationDelayRunnable, (long) (onlineReason ? CommonStatusCodes.AUTH_API_INVALID_CREDENTIALS : 1000));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    protected void repeatNotificationMaybe() {
        this.notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                int hour = Calendar.getInstance().get(11);
                if (hour < 11 || hour > 22) {
                    NotificationsController.this.scheduleNotificationRepeat();
                    return;
                }
                NotificationsController.this.notificationManager.cancel(1);
                NotificationsController.this.showOrUpdateNotification(true);
            }
        });
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r63) {
        /*
        r62 = this;
        r56 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r56 == 0) goto L_0x0012;
    L_0x0006:
        r0 = r62;
        r0 = r0.pushMessages;
        r56 = r0;
        r56 = r56.isEmpty();
        if (r56 == 0) goto L_0x0016;
    L_0x0012:
        r62.dismissNotification();
    L_0x0015:
        return;
    L_0x0016:
        r56 = org.telegram.tgnet.ConnectionsManager.getInstance();	 Catch:{ Exception -> 0x0057 }
        r56.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0057 }
        r0 = r62;
        r0 = r0.pushMessages;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r57 = 0;
        r28 = r56.get(r57);	 Catch:{ Exception -> 0x0057 }
        r28 = (org.telegram.messenger.MessageObject) r28;	 Catch:{ Exception -> 0x0057 }
        r56 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0057 }
        r57 = "Notifications";
        r58 = 0;
        r46 = r56.getSharedPreferences(r57, r58);	 Catch:{ Exception -> 0x0057 }
        r56 = "dismissDate";
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r13 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.date;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        if (r0 > r13) goto L_0x0062;
    L_0x0053:
        r62.dismissNotification();	 Catch:{ Exception -> 0x0057 }
        goto L_0x0015;
    L_0x0057:
        r17 = move-exception;
        r56 = "tmessages";
        r0 = r56;
        r1 = r17;
        org.telegram.messenger.FileLog.m13e(r0, r1);
        goto L_0x0015;
    L_0x0062:
        r14 = r28.getDialogId();	 Catch:{ Exception -> 0x0057 }
        r44 = r14;
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.mentioned;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x0087;
    L_0x0076:
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.from_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0057 }
        r44 = r0;
    L_0x0087:
        r34 = r28.getId();	 Catch:{ Exception -> 0x0057 }
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.to_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.chat_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x0663;
    L_0x009f:
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.to_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r6 = r0.chat_id;	 Catch:{ Exception -> 0x0057 }
    L_0x00af:
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.to_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.user_id;	 Catch:{ Exception -> 0x0057 }
        r53 = r0;
        if (r53 != 0) goto L_0x0675;
    L_0x00c3:
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.from_id;	 Catch:{ Exception -> 0x0057 }
        r53 = r0;
    L_0x00cf:
        r56 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x0057 }
        r57 = java.lang.Integer.valueOf(r53);	 Catch:{ Exception -> 0x0057 }
        r52 = r56.getUser(r57);	 Catch:{ Exception -> 0x0057 }
        r5 = 0;
        if (r6 == 0) goto L_0x00ea;
    L_0x00de:
        r56 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x0057 }
        r57 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0057 }
        r5 = r56.getChat(r57);	 Catch:{ Exception -> 0x0057 }
    L_0x00ea:
        r43 = 0;
        r39 = 0;
        r37 = 0;
        r7 = 0;
        r30 = -16711936; // 0xffffffffff00ff00 float:-1.7146522E38 double:NaN;
        r21 = 0;
        r47 = 0;
        r0 = r62;
        r1 = r46;
        r2 = r44;
        r41 = r0.getNotifyOverride(r1, r2);	 Catch:{ Exception -> 0x0057 }
        if (r63 == 0) goto L_0x0130;
    L_0x0104:
        r56 = 2;
        r0 = r41;
        r1 = r56;
        if (r0 == r1) goto L_0x0130;
    L_0x010c:
        r56 = "EnableAll";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r56 = r0.getBoolean(r1, r2);	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x012e;
    L_0x011c:
        if (r6 == 0) goto L_0x0132;
    L_0x011e:
        r56 = "EnableGroup";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r56 = r0.getBoolean(r1, r2);	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x0132;
    L_0x012e:
        if (r41 != 0) goto L_0x0132;
    L_0x0130:
        r39 = 1;
    L_0x0132:
        if (r39 != 0) goto L_0x01b9;
    L_0x0134:
        r56 = (r14 > r44 ? 1 : (r14 == r44 ? 0 : -1));
        if (r56 != 0) goto L_0x01b9;
    L_0x0138:
        if (r5 == 0) goto L_0x01b9;
    L_0x013a:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "smart_max_count_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = 2;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r40 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "smart_delay_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r38 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        if (r40 == 0) goto L_0x01b9;
    L_0x017e:
        r0 = r62;
        r0 = r0.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r57 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0057 }
        r12 = r56.get(r57);	 Catch:{ Exception -> 0x0057 }
        r12 = (android.graphics.Point) r12;	 Catch:{ Exception -> 0x0057 }
        if (r12 != 0) goto L_0x068d;
    L_0x0190:
        r12 = new android.graphics.Point;	 Catch:{ Exception -> 0x0057 }
        r56 = 1;
        r58 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0057 }
        r60 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r58 = r58 / r60;
        r0 = r58;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r0 = r56;
        r1 = r57;
        r12.<init>(r0, r1);	 Catch:{ Exception -> 0x0057 }
        r0 = r62;
        r0 = r0.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r57 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r57;
        r0.put(r1, r12);	 Catch:{ Exception -> 0x0057 }
    L_0x01b9:
        r56 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0057 }
        r10 = r56.getPath();	 Catch:{ Exception -> 0x0057 }
        if (r39 != 0) goto L_0x034d;
    L_0x01c1:
        r56 = "EnableInAppSounds";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r23 = r0.getBoolean(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "EnableInAppVibrate";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r24 = r0.getBoolean(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "EnableInAppPreview";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r21 = r0.getBoolean(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "EnableInAppPriority";
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r22 = r0.getBoolean(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "vibrate_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r55 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "priority_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = 3;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r48 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r54 = 0;
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "sound_path_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r7 = r0.getString(r1, r2);	 Catch:{ Exception -> 0x0057 }
        if (r6 == 0) goto L_0x06ec;
    L_0x0260:
        if (r7 == 0) goto L_0x06de;
    L_0x0262:
        r56 = r7.equals(r10);	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x06de;
    L_0x0268:
        r7 = 0;
    L_0x0269:
        r56 = "vibrate_group";
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r37 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "priority_group";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r47 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "GroupLed";
        r57 = -16711936; // 0xffffffffff00ff00 float:-1.7146522E38 double:NaN;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r30 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
    L_0x0294:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "color_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r0 = r46;
        r1 = r56;
        r56 = r0.contains(r1);	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x02d4;
    L_0x02b3:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "color_";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.append(r14);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r30 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
    L_0x02d4:
        r56 = 3;
        r0 = r48;
        r1 = r56;
        if (r0 == r1) goto L_0x02de;
    L_0x02dc:
        r47 = r48;
    L_0x02de:
        r56 = 4;
        r0 = r37;
        r1 = r56;
        if (r0 != r1) goto L_0x02ea;
    L_0x02e6:
        r54 = 1;
        r37 = 0;
    L_0x02ea:
        r56 = 2;
        r0 = r37;
        r1 = r56;
        if (r0 != r1) goto L_0x030a;
    L_0x02f2:
        r56 = 1;
        r0 = r55;
        r1 = r56;
        if (r0 == r1) goto L_0x031c;
    L_0x02fa:
        r56 = 3;
        r0 = r55;
        r1 = r56;
        if (r0 == r1) goto L_0x031c;
    L_0x0302:
        r56 = 5;
        r0 = r55;
        r1 = r56;
        if (r0 == r1) goto L_0x031c;
    L_0x030a:
        r56 = 2;
        r0 = r37;
        r1 = r56;
        if (r0 == r1) goto L_0x031a;
    L_0x0312:
        r56 = 2;
        r0 = r55;
        r1 = r56;
        if (r0 == r1) goto L_0x031c;
    L_0x031a:
        if (r55 == 0) goto L_0x031e;
    L_0x031c:
        r37 = r55;
    L_0x031e:
        r56 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x032d;
    L_0x0322:
        if (r23 != 0) goto L_0x0325;
    L_0x0324:
        r7 = 0;
    L_0x0325:
        if (r24 != 0) goto L_0x0329;
    L_0x0327:
        r37 = 2;
    L_0x0329:
        if (r22 != 0) goto L_0x0731;
    L_0x032b:
        r47 = 0;
    L_0x032d:
        if (r54 == 0) goto L_0x034d;
    L_0x032f:
        r56 = 2;
        r0 = r37;
        r1 = r56;
        if (r0 == r1) goto L_0x034d;
    L_0x0337:
        r0 = r62;
        r0 = r0.audioManager;	 Catch:{ Exception -> 0x073d }
        r56 = r0;
        r35 = r56.getRingerMode();	 Catch:{ Exception -> 0x073d }
        if (r35 == 0) goto L_0x034d;
    L_0x0343:
        r56 = 1;
        r0 = r35;
        r1 = r56;
        if (r0 == r1) goto L_0x034d;
    L_0x034b:
        r37 = 2;
    L_0x034d:
        r26 = new android.content.Intent;	 Catch:{ Exception -> 0x0057 }
        r56 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0057 }
        r57 = org.telegram.ui.LaunchActivity.class;
        r0 = r26;
        r1 = r56;
        r2 = r57;
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "com.tmessages.openchat";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r58 = java.lang.Math.random();	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r58;
        r56 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r57 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r0 = r26;
        r1 = r56;
        r0.setAction(r1);	 Catch:{ Exception -> 0x0057 }
        r56 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r0 = r26;
        r1 = r56;
        r0.setFlags(r1);	 Catch:{ Exception -> 0x0057 }
        r0 = (int) r14;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x0804;
    L_0x0394:
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 != r1) goto L_0x03b1;
    L_0x03a6:
        if (r6 == 0) goto L_0x0749;
    L_0x03a8:
        r56 = "chatId";
        r0 = r26;
        r1 = r56;
        r0.putExtra(r1, r6);	 Catch:{ Exception -> 0x0057 }
    L_0x03b1:
        r56 = 0;
        r56 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r56);	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x03bd;
    L_0x03b9:
        r56 = org.telegram.messenger.UserConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x0758;
    L_0x03bd:
        r43 = 0;
    L_0x03bf:
        r56 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0057 }
        r57 = 0;
        r58 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r0 = r56;
        r1 = r57;
        r2 = r26;
        r3 = r58;
        r8 = android.app.PendingIntent.getActivity(r0, r1, r2, r3);	 Catch:{ Exception -> 0x0057 }
        r49 = 1;
        r0 = (int) r14;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03f6;
    L_0x03d8:
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 > r1) goto L_0x03f6;
    L_0x03ea:
        r56 = 0;
        r56 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r56);	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x03f6;
    L_0x03f2:
        r56 = org.telegram.messenger.UserConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x082c;
    L_0x03f6:
        r56 = "AppName";
        r57 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r36 = org.telegram.messenger.LocaleController.getString(r56, r57);	 Catch:{ Exception -> 0x0057 }
        r49 = 0;
    L_0x0401:
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 != r1) goto L_0x083a;
    L_0x0413:
        r56 = "NewMessages";
        r0 = r62;
        r0 = r0.total_unread_count;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r56, r57);	 Catch:{ Exception -> 0x0057 }
    L_0x041f:
        r56 = new android.support.v4.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0057 }
        r57 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0057 }
        r56.<init>(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r36;
        r56 = r0.setContentTitle(r1);	 Catch:{ Exception -> 0x0057 }
        r57 = 2130837852; // 0x7f02015c float:1.728067E38 double:1.0527737795E-314;
        r56 = r56.setSmallIcon(r57);	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r56 = r56.setAutoCancel(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r62;
        r0 = r0.total_unread_count;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r56 = r56.setNumber(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r56 = r0.setContentIntent(r8);	 Catch:{ Exception -> 0x0057 }
        r57 = "messages";
        r56 = r56.setGroup(r57);	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r56 = r56.setGroupSummary(r57);	 Catch:{ Exception -> 0x0057 }
        r57 = -13851168; // 0xffffffffff2ca5e0 float:-2.2948849E38 double:NaN;
        r31 = r56.setColor(r57);	 Catch:{ Exception -> 0x0057 }
        r56 = "msg";
        r0 = r31;
        r1 = r56;
        r0.setCategory(r1);	 Catch:{ Exception -> 0x0057 }
        if (r5 != 0) goto L_0x049f;
    L_0x0469:
        if (r52 == 0) goto L_0x049f;
    L_0x046b:
        r0 = r52;
        r0 = r0.phone;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x049f;
    L_0x0473:
        r0 = r52;
        r0 = r0.phone;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.length();	 Catch:{ Exception -> 0x0057 }
        if (r56 <= 0) goto L_0x049f;
    L_0x047f:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = "tel:+";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r52;
        r0 = r0.phone;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.addPerson(r1);	 Catch:{ Exception -> 0x0057 }
    L_0x049f:
        r51 = 2;
        r27 = 0;
        r18 = 0;
        r0 = r62;
        r0 = r0.pushMessages;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 != r1) goto L_0x08b7;
    L_0x04b7:
        r0 = r62;
        r0 = r0.pushMessages;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r57 = 0;
        r33 = r56.get(r57);	 Catch:{ Exception -> 0x0057 }
        r33 = (org.telegram.messenger.MessageObject) r33;	 Catch:{ Exception -> 0x0057 }
        r56 = 0;
        r0 = r62;
        r1 = r33;
        r2 = r56;
        r27 = r0.getStringForMessage(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r32 = r27;
        r0 = r33;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.silent;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x0871;
    L_0x04e1:
        r51 = 1;
    L_0x04e3:
        if (r32 == 0) goto L_0x0015;
    L_0x04e5:
        if (r49 == 0) goto L_0x050c;
    L_0x04e7:
        if (r5 == 0) goto L_0x0875;
    L_0x04e9:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = " @ ";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r36;
        r56 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = "";
        r0 = r32;
        r1 = r56;
        r2 = r57;
        r32 = r0.replace(r1, r2);	 Catch:{ Exception -> 0x0057 }
    L_0x050c:
        r31.setContentText(r32);	 Catch:{ Exception -> 0x0057 }
        r56 = new android.support.v4.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r32;
        r56 = r0.bigText(r1);	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setStyle(r1);	 Catch:{ Exception -> 0x0057 }
    L_0x0523:
        r16 = new android.content.Intent;	 Catch:{ Exception -> 0x0057 }
        r56 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0057 }
        r57 = org.telegram.messenger.NotificationDismissReceiver.class;
        r0 = r16;
        r1 = r56;
        r2 = r57;
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "messageDate";
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r0 = r57;
        r0 = r0.date;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r0 = r16;
        r1 = r56;
        r2 = r57;
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r58 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r0 = r56;
        r1 = r57;
        r2 = r16;
        r3 = r58;
        r56 = android.app.PendingIntent.getBroadcast(r0, r1, r2, r3);	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setDeleteIntent(r1);	 Catch:{ Exception -> 0x0057 }
        if (r43 == 0) goto L_0x0585;
    L_0x0564:
        r56 = org.telegram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0057 }
        r57 = 0;
        r58 = "50_50";
        r0 = r56;
        r1 = r43;
        r2 = r57;
        r3 = r58;
        r20 = r0.getImageFromMemory(r1, r2, r3);	 Catch:{ Exception -> 0x0057 }
        if (r20 == 0) goto L_0x09bc;
    L_0x057a:
        r56 = r20.getBitmap();	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setLargeIcon(r1);	 Catch:{ Exception -> 0x0057 }
    L_0x0585:
        if (r63 == 0) goto L_0x058f;
    L_0x0587:
        r56 = 1;
        r0 = r51;
        r1 = r56;
        if (r0 != r1) goto L_0x0a06;
    L_0x058f:
        r56 = -1;
        r0 = r31;
        r1 = r56;
        r0.setPriority(r1);	 Catch:{ Exception -> 0x0057 }
    L_0x0598:
        r56 = 1;
        r0 = r51;
        r1 = r56;
        if (r0 == r1) goto L_0x0a97;
    L_0x05a0:
        if (r39 != 0) goto L_0x0a97;
    L_0x05a2:
        r56 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x05a8;
    L_0x05a6:
        if (r21 == 0) goto L_0x05e8;
    L_0x05a8:
        r56 = r27.length();	 Catch:{ Exception -> 0x0057 }
        r57 = 100;
        r0 = r56;
        r1 = r57;
        if (r0 <= r1) goto L_0x05e1;
    L_0x05b4:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = 0;
        r58 = 100;
        r0 = r27;
        r1 = r57;
        r2 = r58;
        r57 = r0.substring(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r58 = 10;
        r59 = 32;
        r57 = r57.replace(r58, r59);	 Catch:{ Exception -> 0x0057 }
        r57 = r57.trim();	 Catch:{ Exception -> 0x0057 }
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r57 = "...";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r27 = r56.toString();	 Catch:{ Exception -> 0x0057 }
    L_0x05e1:
        r0 = r31;
        r1 = r27;
        r0.setTicker(r1);	 Catch:{ Exception -> 0x0057 }
    L_0x05e8:
        r56 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0057 }
        r56 = r56.isRecordingAudio();	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x0611;
    L_0x05f2:
        if (r7 == 0) goto L_0x0611;
    L_0x05f4:
        r56 = "NoSound";
        r0 = r56;
        r56 = r7.equals(r0);	 Catch:{ Exception -> 0x0057 }
        if (r56 != 0) goto L_0x0611;
    L_0x05fe:
        r56 = r7.equals(r10);	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x0a39;
    L_0x0604:
        r56 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0057 }
        r57 = 5;
        r0 = r31;
        r1 = r56;
        r2 = r57;
        r0.setSound(r1, r2);	 Catch:{ Exception -> 0x0057 }
    L_0x0611:
        if (r30 == 0) goto L_0x0622;
    L_0x0613:
        r56 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r57 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r31;
        r1 = r30;
        r2 = r56;
        r3 = r57;
        r0.setLights(r1, r2, r3);	 Catch:{ Exception -> 0x0057 }
    L_0x0622:
        r56 = 2;
        r0 = r37;
        r1 = r56;
        if (r0 == r1) goto L_0x0634;
    L_0x062a:
        r56 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0057 }
        r56 = r56.isRecordingAudio();	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x0a4a;
    L_0x0634:
        r56 = 2;
        r0 = r56;
        r0 = new long[r0];	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = {0, 0};	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setVibrate(r1);	 Catch:{ Exception -> 0x0057 }
    L_0x0646:
        r0 = r62;
        r1 = r31;
        r2 = r63;
        r0.showExtraNotifications(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r0 = r62;
        r0 = r0.notificationManager;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r57 = 1;
        r58 = r31.build();	 Catch:{ Exception -> 0x0057 }
        r56.notify(r57, r58);	 Catch:{ Exception -> 0x0057 }
        r62.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0057 }
        goto L_0x0015;
    L_0x0663:
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.to_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r6 = r0.channel_id;	 Catch:{ Exception -> 0x0057 }
        goto L_0x00af;
    L_0x0675:
        r56 = org.telegram.messenger.UserConfig.getClientUserId();	 Catch:{ Exception -> 0x0057 }
        r0 = r53;
        r1 = r56;
        if (r0 != r1) goto L_0x00cf;
    L_0x067f:
        r0 = r28;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.from_id;	 Catch:{ Exception -> 0x0057 }
        r53 = r0;
        goto L_0x00cf;
    L_0x068d:
        r0 = r12.y;	 Catch:{ Exception -> 0x0057 }
        r29 = r0;
        r56 = r29 + r38;
        r0 = r56;
        r0 = (long) r0;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r58 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0057 }
        r60 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r58 = r58 / r60;
        r56 = (r56 > r58 ? 1 : (r56 == r58 ? 0 : -1));
        if (r56 >= 0) goto L_0x06bc;
    L_0x06a4:
        r56 = 1;
        r58 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0057 }
        r60 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r58 = r58 / r60;
        r0 = r58;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r0 = r56;
        r1 = r57;
        r12.set(r0, r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x01b9;
    L_0x06bc:
        r9 = r12.x;	 Catch:{ Exception -> 0x0057 }
        r0 = r40;
        if (r9 >= r0) goto L_0x06da;
    L_0x06c2:
        r56 = r9 + 1;
        r58 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0057 }
        r60 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r58 = r58 / r60;
        r0 = r58;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r0 = r56;
        r1 = r57;
        r12.set(r0, r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x01b9;
    L_0x06da:
        r39 = 1;
        goto L_0x01b9;
    L_0x06de:
        if (r7 != 0) goto L_0x0269;
    L_0x06e0:
        r56 = "GroupSoundPath";
        r0 = r46;
        r1 = r56;
        r7 = r0.getString(r1, r10);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0269;
    L_0x06ec:
        if (r53 == 0) goto L_0x0294;
    L_0x06ee:
        if (r7 == 0) goto L_0x0724;
    L_0x06f0:
        r56 = r7.equals(r10);	 Catch:{ Exception -> 0x0057 }
        if (r56 == 0) goto L_0x0724;
    L_0x06f6:
        r7 = 0;
    L_0x06f7:
        r56 = "vibrate_messages";
        r57 = 0;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r37 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "priority_group";
        r57 = 1;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r47 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r56 = "MessagesLed";
        r57 = -16711936; // 0xffffffffff00ff00 float:-1.7146522E38 double:NaN;
        r0 = r46;
        r1 = r56;
        r2 = r57;
        r30 = r0.getInt(r1, r2);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0294;
    L_0x0724:
        if (r7 != 0) goto L_0x06f7;
    L_0x0726:
        r56 = "GlobalSoundPath";
        r0 = r46;
        r1 = r56;
        r7 = r0.getString(r1, r10);	 Catch:{ Exception -> 0x0057 }
        goto L_0x06f7;
    L_0x0731:
        r56 = 2;
        r0 = r47;
        r1 = r56;
        if (r0 != r1) goto L_0x032d;
    L_0x0739:
        r47 = 1;
        goto L_0x032d;
    L_0x073d:
        r17 = move-exception;
        r56 = "tmessages";
        r0 = r56;
        r1 = r17;
        org.telegram.messenger.FileLog.m13e(r0, r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x034d;
    L_0x0749:
        if (r53 == 0) goto L_0x03b1;
    L_0x074b:
        r56 = "userId";
        r0 = r26;
        r1 = r56;
        r2 = r53;
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x0057 }
        goto L_0x03b1;
    L_0x0758:
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 != r1) goto L_0x03bf;
    L_0x076a:
        if (r5 == 0) goto L_0x07b2;
    L_0x076c:
        r0 = r5.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03bf;
    L_0x0772:
        r0 = r5.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03bf;
    L_0x077e:
        r0 = r5.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.volume_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r58 = 0;
        r56 = (r56 > r58 ? 1 : (r56 == r58 ? 0 : -1));
        if (r56 == 0) goto L_0x03bf;
    L_0x0794:
        r0 = r5.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.local_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03bf;
    L_0x07a6:
        r0 = r5.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r43 = r0;
        goto L_0x03bf;
    L_0x07b2:
        if (r52 == 0) goto L_0x03bf;
    L_0x07b4:
        r0 = r52;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03bf;
    L_0x07bc:
        r0 = r52;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03bf;
    L_0x07ca:
        r0 = r52;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.volume_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r58 = 0;
        r56 = (r56 > r58 ? 1 : (r56 == r58 ? 0 : -1));
        if (r56 == 0) goto L_0x03bf;
    L_0x07e2:
        r0 = r52;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.local_id;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x03bf;
    L_0x07f6:
        r0 = r52;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0057 }
        r43 = r0;
        goto L_0x03bf;
    L_0x0804:
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 != r1) goto L_0x03bf;
    L_0x0816:
        r56 = "encId";
        r57 = 32;
        r58 = r14 >> r57;
        r0 = r58;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r0 = r26;
        r1 = r56;
        r2 = r57;
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x0057 }
        goto L_0x03bf;
    L_0x082c:
        if (r5 == 0) goto L_0x0834;
    L_0x082e:
        r0 = r5.title;	 Catch:{ Exception -> 0x0057 }
        r36 = r0;
        goto L_0x0401;
    L_0x0834:
        r36 = org.telegram.messenger.UserObject.getUserName(r52);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0401;
    L_0x083a:
        r56 = "NotificationMessagesPeopleDisplayOrder";
        r57 = 2131165977; // 0x7f070319 float:1.7946186E38 double:1.052935895E-314;
        r58 = 2;
        r0 = r58;
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0057 }
        r58 = r0;
        r59 = 0;
        r60 = "NewMessages";
        r0 = r62;
        r0 = r0.total_unread_count;	 Catch:{ Exception -> 0x0057 }
        r61 = r0;
        r60 = org.telegram.messenger.LocaleController.formatPluralString(r60, r61);	 Catch:{ Exception -> 0x0057 }
        r58[r59] = r60;	 Catch:{ Exception -> 0x0057 }
        r59 = 1;
        r60 = "FromChats";
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r61 = r0;
        r61 = r61.size();	 Catch:{ Exception -> 0x0057 }
        r60 = org.telegram.messenger.LocaleController.formatPluralString(r60, r61);	 Catch:{ Exception -> 0x0057 }
        r58[r59] = r60;	 Catch:{ Exception -> 0x0057 }
        r11 = org.telegram.messenger.LocaleController.formatString(r56, r57, r58);	 Catch:{ Exception -> 0x0057 }
        goto L_0x041f;
    L_0x0871:
        r51 = 0;
        goto L_0x04e3;
    L_0x0875:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r36;
        r56 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r57 = ": ";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = "";
        r0 = r32;
        r1 = r56;
        r2 = r57;
        r56 = r0.replace(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r57 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r57.<init>();	 Catch:{ Exception -> 0x0057 }
        r0 = r57;
        r1 = r36;
        r57 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r58 = " ";
        r57 = r57.append(r58);	 Catch:{ Exception -> 0x0057 }
        r57 = r57.toString();	 Catch:{ Exception -> 0x0057 }
        r58 = "";
        r32 = r56.replace(r57, r58);	 Catch:{ Exception -> 0x0057 }
        goto L_0x050c;
    L_0x08b7:
        r0 = r31;
        r0.setContentText(r11);	 Catch:{ Exception -> 0x0057 }
        r25 = new android.support.v4.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0057 }
        r25.<init>();	 Catch:{ Exception -> 0x0057 }
        r0 = r25;
        r1 = r36;
        r0.setBigContentTitle(r1);	 Catch:{ Exception -> 0x0057 }
        r56 = 10;
        r0 = r62;
        r0 = r0.pushMessages;	 Catch:{ Exception -> 0x0057 }
        r57 = r0;
        r57 = r57.size();	 Catch:{ Exception -> 0x0057 }
        r9 = java.lang.Math.min(r56, r57);	 Catch:{ Exception -> 0x0057 }
        r19 = 0;
    L_0x08da:
        r0 = r19;
        if (r0 >= r9) goto L_0x09ae;
    L_0x08de:
        r0 = r62;
        r0 = r0.pushMessages;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r1 = r19;
        r33 = r0.get(r1);	 Catch:{ Exception -> 0x0057 }
        r33 = (org.telegram.messenger.MessageObject) r33;	 Catch:{ Exception -> 0x0057 }
        r56 = 0;
        r0 = r62;
        r1 = r33;
        r2 = r56;
        r32 = r0.getStringForMessage(r1, r2);	 Catch:{ Exception -> 0x0057 }
        if (r32 == 0) goto L_0x090c;
    L_0x08fc:
        r0 = r33;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.date;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        if (r0 > r13) goto L_0x090f;
    L_0x090c:
        r19 = r19 + 1;
        goto L_0x08da;
    L_0x090f:
        r56 = 2;
        r0 = r51;
        r1 = r56;
        if (r0 != r1) goto L_0x0929;
    L_0x0917:
        r27 = r32;
        r0 = r33;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r0 = r56;
        r0 = r0.silent;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        if (r56 == 0) goto L_0x096a;
    L_0x0927:
        r51 = 1;
    L_0x0929:
        r0 = r62;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = r56.size();	 Catch:{ Exception -> 0x0057 }
        r57 = 1;
        r0 = r56;
        r1 = r57;
        if (r0 != r1) goto L_0x0962;
    L_0x093b:
        if (r49 == 0) goto L_0x0962;
    L_0x093d:
        if (r5 == 0) goto L_0x096d;
    L_0x093f:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r57 = " @ ";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r36;
        r56 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = "";
        r0 = r32;
        r1 = r56;
        r2 = r57;
        r32 = r0.replace(r1, r2);	 Catch:{ Exception -> 0x0057 }
    L_0x0962:
        r0 = r25;
        r1 = r32;
        r0.addLine(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x090c;
    L_0x096a:
        r51 = 0;
        goto L_0x0929;
    L_0x096d:
        r56 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r56.<init>();	 Catch:{ Exception -> 0x0057 }
        r0 = r56;
        r1 = r36;
        r56 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r57 = ": ";
        r56 = r56.append(r57);	 Catch:{ Exception -> 0x0057 }
        r56 = r56.toString();	 Catch:{ Exception -> 0x0057 }
        r57 = "";
        r0 = r32;
        r1 = r56;
        r2 = r57;
        r56 = r0.replace(r1, r2);	 Catch:{ Exception -> 0x0057 }
        r57 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0057 }
        r57.<init>();	 Catch:{ Exception -> 0x0057 }
        r0 = r57;
        r1 = r36;
        r57 = r0.append(r1);	 Catch:{ Exception -> 0x0057 }
        r58 = " ";
        r57 = r57.append(r58);	 Catch:{ Exception -> 0x0057 }
        r57 = r57.toString();	 Catch:{ Exception -> 0x0057 }
        r58 = "";
        r32 = r56.replace(r57, r58);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0962;
    L_0x09ae:
        r0 = r25;
        r0.setSummaryText(r11);	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r25;
        r0.setStyle(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0523;
    L_0x09bc:
        r56 = 1126170624; // 0x43200000 float:160.0 double:5.564022167E-315;
        r57 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        r57 = org.telegram.messenger.AndroidUtilities.dp(r57);	 Catch:{ Throwable -> 0x09fd }
        r0 = r57;
        r0 = (float) r0;	 Catch:{ Throwable -> 0x09fd }
        r57 = r0;
        r50 = r56 / r57;
        r42 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x09fd }
        r42.<init>();	 Catch:{ Throwable -> 0x09fd }
        r56 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r56 = (r50 > r56 ? 1 : (r50 == r56 ? 0 : -1));
        if (r56 >= 0) goto L_0x0a00;
    L_0x09d6:
        r56 = 1;
    L_0x09d8:
        r0 = r56;
        r1 = r42;
        r1.inSampleSize = r0;	 Catch:{ Throwable -> 0x09fd }
        r56 = 1;
        r0 = r43;
        r1 = r56;
        r56 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r1);	 Catch:{ Throwable -> 0x09fd }
        r56 = r56.toString();	 Catch:{ Throwable -> 0x09fd }
        r0 = r56;
        r1 = r42;
        r4 = android.graphics.BitmapFactory.decodeFile(r0, r1);	 Catch:{ Throwable -> 0x09fd }
        if (r4 == 0) goto L_0x0585;
    L_0x09f6:
        r0 = r31;
        r0.setLargeIcon(r4);	 Catch:{ Throwable -> 0x09fd }
        goto L_0x0585;
    L_0x09fd:
        r56 = move-exception;
        goto L_0x0585;
    L_0x0a00:
        r0 = r50;
        r0 = (int) r0;
        r56 = r0;
        goto L_0x09d8;
    L_0x0a06:
        if (r47 != 0) goto L_0x0a13;
    L_0x0a08:
        r56 = 0;
        r0 = r31;
        r1 = r56;
        r0.setPriority(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0598;
    L_0x0a13:
        r56 = 1;
        r0 = r47;
        r1 = r56;
        if (r0 != r1) goto L_0x0a26;
    L_0x0a1b:
        r56 = 1;
        r0 = r31;
        r1 = r56;
        r0.setPriority(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0598;
    L_0x0a26:
        r56 = 2;
        r0 = r47;
        r1 = r56;
        if (r0 != r1) goto L_0x0598;
    L_0x0a2e:
        r56 = 2;
        r0 = r31;
        r1 = r56;
        r0.setPriority(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0598;
    L_0x0a39:
        r56 = android.net.Uri.parse(r7);	 Catch:{ Exception -> 0x0057 }
        r57 = 5;
        r0 = r31;
        r1 = r56;
        r2 = r57;
        r0.setSound(r1, r2);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0611;
    L_0x0a4a:
        r56 = 1;
        r0 = r37;
        r1 = r56;
        if (r0 != r1) goto L_0x0a66;
    L_0x0a52:
        r56 = 4;
        r0 = r56;
        r0 = new long[r0];	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = {0, 100, 0, 100};	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setVibrate(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0646;
    L_0x0a66:
        if (r37 == 0) goto L_0x0a70;
    L_0x0a68:
        r56 = 4;
        r0 = r37;
        r1 = r56;
        if (r0 != r1) goto L_0x0a7b;
    L_0x0a70:
        r56 = 2;
        r0 = r31;
        r1 = r56;
        r0.setDefaults(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0646;
    L_0x0a7b:
        r56 = 3;
        r0 = r37;
        r1 = r56;
        if (r0 != r1) goto L_0x0646;
    L_0x0a83:
        r56 = 2;
        r0 = r56;
        r0 = new long[r0];	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = {0, 1000};	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setVibrate(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0646;
    L_0x0a97:
        r56 = 2;
        r0 = r56;
        r0 = new long[r0];	 Catch:{ Exception -> 0x0057 }
        r56 = r0;
        r56 = {0, 0};	 Catch:{ Exception -> 0x0057 }
        r0 = r31;
        r1 = r56;
        r0.setVibrate(r1);	 Catch:{ Exception -> 0x0057 }
        goto L_0x0646;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    @SuppressLint({"InlinedApi"})
    private void showExtraNotifications(Builder notificationBuilder, boolean notifyAboutLast) {
        if (VERSION.SDK_INT >= 18) {
            long dialog_id;
            ArrayList<Long> sortedDialogs = new ArrayList();
            HashMap<Long, ArrayList<MessageObject>> messagesByDialogs = new HashMap();
            int a = 0;
            while (true) {
                if (a >= this.pushMessages.size()) {
                    break;
                }
                MessageObject messageObject = (MessageObject) this.pushMessages.get(a);
                dialog_id = messageObject.getDialogId();
                if (((int) dialog_id) != 0) {
                    ArrayList<MessageObject> arrayList = (ArrayList) messagesByDialogs.get(Long.valueOf(dialog_id));
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        messagesByDialogs.put(Long.valueOf(dialog_id), arrayList);
                        sortedDialogs.add(0, Long.valueOf(dialog_id));
                    }
                    arrayList.add(messageObject);
                }
                a++;
            }
            HashMap<Long, Integer> oldIdsWear = new HashMap();
            oldIdsWear.putAll(this.wearNotificationsIds);
            this.wearNotificationsIds.clear();
            HashMap<Long, Integer> oldIdsAuto = new HashMap();
            oldIdsAuto.putAll(this.autoNotificationsIds);
            this.autoNotificationsIds.clear();
            for (int b = 0; b < sortedDialogs.size(); b++) {
                dialog_id = ((Long) sortedDialogs.get(b)).longValue();
                ArrayList<MessageObject> messageObjects = (ArrayList) messagesByDialogs.get(Long.valueOf(dialog_id));
                int max_id = ((MessageObject) messageObjects.get(0)).getId();
                int max_date = ((MessageObject) messageObjects.get(0)).messageOwner.date;
                Chat chat = null;
                User user = null;
                TLObject photoPath;
                String name;
                Integer notificationIdWear;
                int i;
                Integer notificationIdAuto;
                UnreadConversation.Builder unreadConvBuilder;
                Intent msgHeardIntent;
                Action wearReplyAction;
                Intent msgReplyIntent;
                Intent intent;
                PendingIntent replyPendingIntent;
                RemoteInput remoteInputWear;
                String replyToString;
                String text;
                String message;
                Intent intent2;
                String str;
                PendingIntent contentIntent;
                WearableExtender wearableExtender;
                Builder builder;
                BitmapDrawable img;
                if (dialog_id > 0) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf((int) dialog_id));
                    if (user == null) {
                    }
                    photoPath = null;
                    if (!AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter) {
                        name = LocaleController.getString("AppName", C0691R.string.AppName);
                    } else {
                        if (chat != null) {
                            name = chat.title;
                        } else {
                            name = UserObject.getUserName(user);
                        }
                        if (chat != null) {
                            if (chat.photo != null) {
                                if (chat.photo.photo_small != null) {
                                    if (chat.photo.photo_small.volume_id != 0) {
                                        if (chat.photo.photo_small.local_id != 0) {
                                            photoPath = chat.photo.photo_small;
                                        }
                                    }
                                }
                            }
                        } else if (user.photo != null) {
                            if (user.photo.photo_small != null) {
                                if (user.photo.photo_small.volume_id != 0) {
                                    if (user.photo.photo_small.local_id != 0) {
                                        photoPath = user.photo.photo_small;
                                    }
                                }
                            }
                        }
                    }
                    notificationIdWear = (Integer) oldIdsWear.get(Long.valueOf(dialog_id));
                    if (notificationIdWear != null) {
                        i = this.wearNotificationId;
                        this.wearNotificationId = i + 1;
                        notificationIdWear = Integer.valueOf(i);
                    } else {
                        oldIdsWear.remove(Long.valueOf(dialog_id));
                    }
                    notificationIdAuto = (Integer) oldIdsAuto.get(Long.valueOf(dialog_id));
                    if (notificationIdAuto != null) {
                        i = this.autoNotificationId;
                        this.autoNotificationId = i + 1;
                        notificationIdAuto = Integer.valueOf(i);
                    } else {
                        oldIdsAuto.remove(Long.valueOf(dialog_id));
                    }
                    unreadConvBuilder = new UnreadConversation.Builder(name).setLatestTimestamp(((long) max_date) * 1000);
                    msgHeardIntent = new Intent();
                    msgHeardIntent.addFlags(32);
                    msgHeardIntent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
                    msgHeardIntent.putExtra("dialog_id", dialog_id);
                    msgHeardIntent.putExtra("max_id", max_id);
                    unreadConvBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, notificationIdAuto.intValue(), msgHeardIntent, C0747C.SAMPLE_FLAG_DECODE_ONLY));
                    wearReplyAction = null;
                    if (!(ChatObject.isChannel(chat) || AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter)) {
                        msgReplyIntent = new Intent();
                        msgReplyIntent.addFlags(32);
                        msgReplyIntent.setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                        msgReplyIntent.putExtra("dialog_id", dialog_id);
                        msgReplyIntent.putExtra("max_id", max_id);
                        unreadConvBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, notificationIdAuto.intValue(), msgReplyIntent, C0747C.SAMPLE_FLAG_DECODE_ONLY), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0691R.string.Reply)).build());
                        intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, notificationIdWear.intValue(), intent, C0747C.SAMPLE_FLAG_DECODE_ONLY);
                        remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0691R.string.Reply)).build();
                        if (chat == null) {
                            replyToString = LocaleController.formatString("ReplyToGroup", C0691R.string.ReplyToGroup, name);
                        } else {
                            replyToString = LocaleController.formatString("ReplyToUser", C0691R.string.ReplyToUser, name);
                        }
                        wearReplyAction = new Action.Builder(C0691R.drawable.ic_reply_icon, replyToString, replyPendingIntent).addRemoteInput(remoteInputWear).build();
                    }
                    text = TtmlNode.ANONYMOUS_REGION_ID;
                    for (a = messageObjects.size() - 1; a >= 0; a--) {
                        message = getStringForMessage((MessageObject) messageObjects.get(a), false);
                        if (message != null) {
                            if (chat == null) {
                                message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                            } else {
                                message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID).replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                            }
                            if (text.length() > 0) {
                                text = text + "\n\n";
                            }
                            text = text + message;
                            unreadConvBuilder.addMessage(message);
                        }
                    }
                    intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                    intent2.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                    intent2.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
                    if (chat != null) {
                        str = "chatId";
                        intent2.putExtra(r43, chat.id);
                    } else if (user != null) {
                        str = "userId";
                        intent2.putExtra(r43, user.id);
                    }
                    contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, C0747C.ENCODING_PCM_32BIT);
                    wearableExtender = new WearableExtender();
                    if (wearReplyAction != null) {
                        wearableExtender.addAction(wearReplyAction);
                    }
                    builder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(C0691R.drawable.notification).setGroup("messages").setContentText(text).setAutoCancel(true).setColor(-13851168).setGroupSummary(false).setContentIntent(contentIntent).extend(wearableExtender).extend(new CarExtender().setUnreadConversation(unreadConvBuilder.build())).setCategory(NotificationCompatApi21.CATEGORY_MESSAGE);
                    if (photoPath != null) {
                        img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                        if (img != null) {
                            builder.setLargeIcon(img.getBitmap());
                        }
                    }
                    if (!(chat != null || user == null || user.phone == null)) {
                        if (user.phone.length() > 0) {
                            builder.addPerson("tel:+" + user.phone);
                        }
                    }
                    this.notificationManager.notify(notificationIdWear.intValue(), builder.build());
                    this.wearNotificationsIds.put(Long.valueOf(dialog_id), notificationIdWear);
                } else {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) dialog_id)));
                    if (chat == null) {
                    }
                    photoPath = null;
                    if (AndroidUtilities.needShowPasscode(false)) {
                    }
                    name = LocaleController.getString("AppName", C0691R.string.AppName);
                    notificationIdWear = (Integer) oldIdsWear.get(Long.valueOf(dialog_id));
                    if (notificationIdWear != null) {
                        oldIdsWear.remove(Long.valueOf(dialog_id));
                    } else {
                        i = this.wearNotificationId;
                        this.wearNotificationId = i + 1;
                        notificationIdWear = Integer.valueOf(i);
                    }
                    notificationIdAuto = (Integer) oldIdsAuto.get(Long.valueOf(dialog_id));
                    if (notificationIdAuto != null) {
                        oldIdsAuto.remove(Long.valueOf(dialog_id));
                    } else {
                        i = this.autoNotificationId;
                        this.autoNotificationId = i + 1;
                        notificationIdAuto = Integer.valueOf(i);
                    }
                    unreadConvBuilder = new UnreadConversation.Builder(name).setLatestTimestamp(((long) max_date) * 1000);
                    msgHeardIntent = new Intent();
                    msgHeardIntent.addFlags(32);
                    msgHeardIntent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
                    msgHeardIntent.putExtra("dialog_id", dialog_id);
                    msgHeardIntent.putExtra("max_id", max_id);
                    unreadConvBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, notificationIdAuto.intValue(), msgHeardIntent, C0747C.SAMPLE_FLAG_DECODE_ONLY));
                    wearReplyAction = null;
                    msgReplyIntent = new Intent();
                    msgReplyIntent.addFlags(32);
                    msgReplyIntent.setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                    msgReplyIntent.putExtra("dialog_id", dialog_id);
                    msgReplyIntent.putExtra("max_id", max_id);
                    unreadConvBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, notificationIdAuto.intValue(), msgReplyIntent, C0747C.SAMPLE_FLAG_DECODE_ONLY), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0691R.string.Reply)).build());
                    intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                    intent.putExtra("dialog_id", dialog_id);
                    intent.putExtra("max_id", max_id);
                    replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, notificationIdWear.intValue(), intent, C0747C.SAMPLE_FLAG_DECODE_ONLY);
                    remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0691R.string.Reply)).build();
                    if (chat == null) {
                        replyToString = LocaleController.formatString("ReplyToUser", C0691R.string.ReplyToUser, name);
                    } else {
                        replyToString = LocaleController.formatString("ReplyToGroup", C0691R.string.ReplyToGroup, name);
                    }
                    wearReplyAction = new Action.Builder(C0691R.drawable.ic_reply_icon, replyToString, replyPendingIntent).addRemoteInput(remoteInputWear).build();
                    text = TtmlNode.ANONYMOUS_REGION_ID;
                    for (a = messageObjects.size() - 1; a >= 0; a--) {
                        message = getStringForMessage((MessageObject) messageObjects.get(a), false);
                        if (message != null) {
                            if (chat == null) {
                                message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID).replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                            } else {
                                message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                            }
                            if (text.length() > 0) {
                                text = text + "\n\n";
                            }
                            text = text + message;
                            unreadConvBuilder.addMessage(message);
                        }
                    }
                    intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                    intent2.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                    intent2.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
                    if (chat != null) {
                        str = "chatId";
                        intent2.putExtra(r43, chat.id);
                    } else if (user != null) {
                        str = "userId";
                        intent2.putExtra(r43, user.id);
                    }
                    contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, C0747C.ENCODING_PCM_32BIT);
                    wearableExtender = new WearableExtender();
                    if (wearReplyAction != null) {
                        wearableExtender.addAction(wearReplyAction);
                    }
                    builder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(C0691R.drawable.notification).setGroup("messages").setContentText(text).setAutoCancel(true).setColor(-13851168).setGroupSummary(false).setContentIntent(contentIntent).extend(wearableExtender).extend(new CarExtender().setUnreadConversation(unreadConvBuilder.build())).setCategory(NotificationCompatApi21.CATEGORY_MESSAGE);
                    if (photoPath != null) {
                        img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                        if (img != null) {
                            builder.setLargeIcon(img.getBitmap());
                        }
                    }
                    if (user.phone.length() > 0) {
                        builder.addPerson("tel:+" + user.phone);
                    }
                    this.notificationManager.notify(notificationIdWear.intValue(), builder.build());
                    this.wearNotificationsIds.put(Long.valueOf(dialog_id), notificationIdWear);
                }
            }
            for (Entry<Long, Integer> entry : oldIdsWear.entrySet()) {
                this.notificationManager.cancel(((Integer) entry.getValue()).intValue());
            }
        }
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (this.audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            this.notificationsQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.NotificationsController.14.1 */
                class C06761 implements OnLoadCompleteListener {
                    C06761() {
                    }

                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (status == 0) {
                            soundPool.play(sampleId, TouchHelperCallback.ALPHA_FULL, TouchHelperCallback.ALPHA_FULL, 1, 0, TouchHelperCallback.ALPHA_FULL);
                        }
                    }
                }

                public void run() {
                    try {
                        if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) > 100) {
                            NotificationsController.this.lastSoundOutPlay = System.currentTimeMillis();
                            if (NotificationsController.this.soundPool == null) {
                                NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                NotificationsController.this.soundPool.setOnLoadCompleteListener(new C06761());
                            }
                            if (NotificationsController.this.soundOut == 0 && !NotificationsController.this.soundOutLoaded) {
                                NotificationsController.this.soundOutLoaded = true;
                                NotificationsController.this.soundOut = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0691R.raw.sound_out, 1);
                            }
                            if (NotificationsController.this.soundOut != 0) {
                                NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, TouchHelperCallback.ALPHA_FULL, TouchHelperCallback.ALPHA_FULL, 1, 0, TouchHelperCallback.ALPHA_FULL);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            });
        }
    }

    public static void updateServerNotificationsSettings(long dialog_id) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        if (((int) dialog_id) != 0) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            TL_account_updateNotifySettings req = new TL_account_updateNotifySettings();
            req.settings = new TL_inputPeerNotifySettings();
            req.settings.sound = "default";
            int mute_type = preferences.getInt("notify2_" + dialog_id, 0);
            if (mute_type == 3) {
                req.settings.mute_until = preferences.getInt("notifyuntil_" + dialog_id, 0);
            } else {
                req.settings.mute_until = mute_type != 2 ? 0 : ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            req.settings.show_previews = preferences.getBoolean("preview_" + dialog_id, true);
            req.settings.silent = preferences.getBoolean("silent_" + dialog_id, false);
            req.peer = new TL_inputNotifyPeer();
            ((TL_inputNotifyPeer) req.peer).peer = MessagesController.getInputPeer((int) dialog_id);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }
}
