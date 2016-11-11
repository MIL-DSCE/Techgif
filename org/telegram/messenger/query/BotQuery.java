package org.telegram.messenger.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.Message;

public class BotQuery {
    private static HashMap<Integer, BotInfo> botInfos;
    private static HashMap<Long, Message> botKeyboards;
    private static HashMap<Integer, Long> botKeyboardsByMids;

    /* renamed from: org.telegram.messenger.query.BotQuery.1 */
    static class C07961 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ ArrayList val$messages;

        C07961(ArrayList arrayList, long j) {
            this.val$messages = arrayList;
            this.val$did = j;
        }

        public void run() {
            if (this.val$messages != null) {
                for (int a = 0; a < this.val$messages.size(); a++) {
                    Long did = (Long) BotQuery.botKeyboardsByMids.get(this.val$messages.get(a));
                    if (did != null) {
                        BotQuery.botKeyboards.remove(did);
                        BotQuery.botKeyboardsByMids.remove(this.val$messages.get(a));
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, did);
                    }
                }
                return;
            }
            BotQuery.botKeyboards.remove(Long.valueOf(this.val$did));
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(this.val$did));
        }
    }

    /* renamed from: org.telegram.messenger.query.BotQuery.2 */
    static class C07982 implements Runnable {
        final /* synthetic */ long val$did;

        /* renamed from: org.telegram.messenger.query.BotQuery.2.1 */
        class C07971 implements Runnable {
            final /* synthetic */ Message val$botKeyboardFinal;

            C07971(Message message) {
                this.val$botKeyboardFinal = message;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, this.val$botKeyboardFinal, Long.valueOf(C07982.this.val$did));
            }
        }

        C07982(long j) {
            this.val$did = j;
        }

        public void run() {
            Message botKeyboard = null;
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(this.val$did)}), new Object[0]);
                if (cursor.next() && !cursor.isNull(0)) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        botKeyboard = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                    }
                }
                cursor.dispose();
                if (botKeyboard != null) {
                    AndroidUtilities.runOnUIThread(new C07971(botKeyboard));
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.BotQuery.3 */
    static class C08003 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$uid;

        /* renamed from: org.telegram.messenger.query.BotQuery.3.1 */
        class C07991 implements Runnable {
            final /* synthetic */ BotInfo val$botInfoFinal;

            C07991(BotInfo botInfo) {
                this.val$botInfoFinal = botInfo;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, this.val$botInfoFinal, Integer.valueOf(C08003.this.val$classGuid));
            }
        }

        C08003(int i, int i2) {
            this.val$uid = i;
            this.val$classGuid = i2;
        }

        public void run() {
            BotInfo botInfo = null;
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(this.val$uid)}), new Object[0]);
                if (cursor.next() && !cursor.isNull(0)) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        botInfo = BotInfo.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                    }
                }
                cursor.dispose();
                if (botInfo != null) {
                    AndroidUtilities.runOnUIThread(new C07991(botInfo));
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.BotQuery.4 */
    static class C08014 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ Message val$message;

        C08014(long j, Message message) {
            this.val$did = j;
            this.val$message = message;
        }

        public void run() {
            Message old = (Message) BotQuery.botKeyboards.put(Long.valueOf(this.val$did), this.val$message);
            if (old != null) {
                BotQuery.botKeyboardsByMids.remove(Integer.valueOf(old.id));
            }
            BotQuery.botKeyboardsByMids.put(Integer.valueOf(this.val$message.id), Long.valueOf(this.val$did));
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, this.val$message, Long.valueOf(this.val$did));
        }
    }

    /* renamed from: org.telegram.messenger.query.BotQuery.5 */
    static class C08025 implements Runnable {
        final /* synthetic */ BotInfo val$botInfo;

        C08025(BotInfo botInfo) {
            this.val$botInfo = botInfo;
        }

        public void run() {
            try {
                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(this.val$botInfo.getObjectSize());
                this.val$botInfo.serializeToStream(data);
                state.bindInteger(1, this.val$botInfo.user_id);
                state.bindByteBuffer(2, data);
                state.step();
                data.reuse();
                state.dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    static {
        botInfos = new HashMap();
        botKeyboards = new HashMap();
        botKeyboardsByMids = new HashMap();
    }

    public static void cleanup() {
        botInfos.clear();
        botKeyboards.clear();
        botKeyboardsByMids.clear();
    }

    public static void clearBotKeyboard(long did, ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new C07961(messages, did));
    }

    public static void loadBotKeyboard(long did) {
        if (((Message) botKeyboards.get(Long.valueOf(did))) != null) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, keyboard, Long.valueOf(did));
            return;
        }
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07982(did));
    }

    public static void loadBotInfo(int uid, boolean cache, int classGuid) {
        if (!cache || ((BotInfo) botInfos.get(Integer.valueOf(uid))) == null) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08003(uid, classGuid));
            return;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, botInfo, Integer.valueOf(classGuid));
    }

    public static void putBotKeyboard(long did, Message message) {
        if (message != null) {
            int mid = 0;
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
                if (cursor.next()) {
                    mid = cursor.intValue(0);
                }
                cursor.dispose();
                if (mid < message.id) {
                    SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindLong(1, did);
                    state.bindInteger(2, message.id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                    state.dispose();
                    AndroidUtilities.runOnUIThread(new C08014(did, message));
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static void putBotInfo(BotInfo botInfo) {
        if (botInfo != null) {
            botInfos.put(Integer.valueOf(botInfo.user_id), botInfo);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08025(botInfo));
        }
    }
}
