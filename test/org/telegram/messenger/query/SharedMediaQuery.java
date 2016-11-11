package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterVoice;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class SharedMediaQuery {
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 5;
    public static final int MEDIA_URL = 3;

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.3 */
    static class C08283 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ ArrayList val$objects;
        final /* synthetic */ messages_Messages val$res;
        final /* synthetic */ boolean val$topReached;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08283(messages_Messages org_telegram_tgnet_TLRPC_messages_Messages, boolean z, long j, ArrayList arrayList, int i, int i2, boolean z2) {
            this.val$res = org_telegram_tgnet_TLRPC_messages_Messages;
            this.val$fromCache = z;
            this.val$uid = j;
            this.val$objects = arrayList;
            this.val$classGuid = i;
            this.val$type = i2;
            this.val$topReached = z2;
        }

        public void run() {
            int totalCount = this.val$res.count;
            MessagesController.getInstance().putUsers(this.val$res.users, this.val$fromCache);
            MessagesController.getInstance().putChats(this.val$res.chats, this.val$fromCache);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mediaDidLoaded, Long.valueOf(this.val$uid), Integer.valueOf(totalCount), this.val$objects, Integer.valueOf(this.val$classGuid), Integer.valueOf(this.val$type), Boolean.valueOf(this.val$topReached));
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.4 */
    static class C08294 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08294(long j, boolean z, int i, int i2, int i3) {
            this.val$uid = j;
            this.val$fromCache = z;
            this.val$count = i;
            this.val$type = i2;
            this.val$classGuid = i3;
        }

        public void run() {
            int i = SharedMediaQuery.MEDIA_PHOTOVIDEO;
            int lower_part = (int) this.val$uid;
            if (this.val$fromCache && this.val$count == -1 && lower_part != 0) {
                SharedMediaQuery.getMediaCount(this.val$uid, this.val$type, this.val$classGuid, false);
                return;
            }
            if (!this.val$fromCache) {
                SharedMediaQuery.putMediaCountDatabase(this.val$uid, this.val$type, this.val$count);
            }
            NotificationCenter instance = NotificationCenter.getInstance();
            int i2 = NotificationCenter.mediaCountDidLoaded;
            Object[] objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
            if (!(this.val$fromCache && this.val$count == -1)) {
                i = this.val$count;
            }
            objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(i);
            objArr[SharedMediaQuery.MEDIA_AUDIO] = Boolean.valueOf(this.val$fromCache);
            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(this.val$type);
            instance.postNotificationName(i2, objArr);
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.5 */
    static class C08305 implements Runnable {
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08305(long j, int i, int i2) {
            this.val$uid = j;
            this.val$type = i;
            this.val$count = i2;
        }

        public void run() {
            try {
                SQLitePreparedStatement state2 = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
                state2.requery();
                state2.bindLong(SharedMediaQuery.MEDIA_FILE, this.val$uid);
                state2.bindInteger(SharedMediaQuery.MEDIA_AUDIO, this.val$type);
                state2.bindInteger(SharedMediaQuery.MEDIA_URL, this.val$count);
                state2.step();
                state2.dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.6 */
    static class C08316 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08316(long j, int i, int i2) {
            this.val$uid = j;
            this.val$type = i;
            this.val$classGuid = i2;
        }

        public void run() {
            int count = -1;
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                SQLiteCursor cursor = database.queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                if (cursor.next()) {
                    count = cursor.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                }
                cursor.dispose();
                int lower_part = (int) this.val$uid;
                if (count == -1 && lower_part == 0) {
                    database = MessagesStorage.getInstance().getDatabase();
                    objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                    objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                    objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    if (cursor.next()) {
                        count = cursor.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                    }
                    cursor.dispose();
                    if (count != -1) {
                        SharedMediaQuery.putMediaCountDatabase(this.val$uid, this.val$type, count);
                    }
                }
                SharedMediaQuery.processLoadedMediaCount(count, this.val$uid, this.val$type, this.val$classGuid, true);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.7 */
    static class C08327 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ int val$offset;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08327(int i, long j, int i2, boolean z, int i3, int i4, int i5) {
            this.val$count = i;
            this.val$uid = j;
            this.val$max_id = i2;
            this.val$isChannel = z;
            this.val$type = i3;
            this.val$offset = i4;
            this.val$classGuid = i5;
        }

        public void run() {
            TL_messages_messages res = new TL_messages_messages();
            try {
                SQLiteCursor cursor;
                boolean topReached;
                ArrayList<Integer> usersToLoad = new ArrayList();
                ArrayList<Integer> chatsToLoad = new ArrayList();
                int countToLoad = this.val$count + SharedMediaQuery.MEDIA_FILE;
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                boolean isEnd = false;
                Object[] objArr;
                if (((int) this.val$uid) != 0) {
                    int channelId = SharedMediaQuery.MEDIA_PHOTOVIDEO;
                    long messageMaxId = (long) this.val$max_id;
                    if (this.val$isChannel) {
                        channelId = -((int) this.val$uid);
                    }
                    if (!(messageMaxId == 0 || channelId == 0)) {
                        messageMaxId |= ((long) channelId) << 32;
                    }
                    objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                    objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                    objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    if (cursor.next()) {
                        isEnd = cursor.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO) == SharedMediaQuery.MEDIA_FILE;
                        cursor.dispose();
                    } else {
                        cursor.dispose();
                        objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        if (cursor.next()) {
                            int mid = cursor.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                            if (mid != 0) {
                                SQLitePreparedStatement state = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                state.requery();
                                state.bindLong(SharedMediaQuery.MEDIA_FILE, this.val$uid);
                                state.bindInteger(SharedMediaQuery.MEDIA_AUDIO, this.val$type);
                                state.bindInteger(SharedMediaQuery.MEDIA_URL, SharedMediaQuery.MEDIA_PHOTOVIDEO);
                                state.bindInteger(SharedMediaQuery.MEDIA_MUSIC, mid);
                                state.step();
                                state.dispose();
                            }
                        }
                        cursor.dispose();
                    }
                    long holeMessageId;
                    if (messageMaxId != 0) {
                        holeMessageId = 0;
                        objArr = new Object[SharedMediaQuery.MEDIA_URL];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$max_id);
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        if (cursor.next()) {
                            holeMessageId = (long) cursor.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                            if (channelId != 0) {
                                holeMessageId |= ((long) channelId) << 32;
                            }
                        }
                        cursor.dispose();
                        if (holeMessageId > 1) {
                            objArr = new Object[SharedMediaQuery.MEDIA_TYPES_COUNT];
                            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr[SharedMediaQuery.MEDIA_FILE] = Long.valueOf(messageMaxId);
                            objArr[SharedMediaQuery.MEDIA_AUDIO] = Long.valueOf(holeMessageId);
                            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(this.val$type);
                            objArr[SharedMediaQuery.MEDIA_MUSIC] = Integer.valueOf(countToLoad);
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        } else {
                            objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr[SharedMediaQuery.MEDIA_FILE] = Long.valueOf(messageMaxId);
                            objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$type);
                            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(countToLoad);
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        }
                    } else {
                        holeMessageId = 0;
                        objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        if (cursor.next()) {
                            holeMessageId = (long) cursor.intValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                            if (channelId != 0) {
                                holeMessageId |= ((long) channelId) << 32;
                            }
                        }
                        cursor.dispose();
                        if (holeMessageId > 1) {
                            objArr = new Object[SharedMediaQuery.MEDIA_TYPES_COUNT];
                            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr[SharedMediaQuery.MEDIA_FILE] = Long.valueOf(holeMessageId);
                            objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$type);
                            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(this.val$offset);
                            objArr[SharedMediaQuery.MEDIA_MUSIC] = Integer.valueOf(countToLoad);
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d,%d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        } else {
                            objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                            objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                            objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                            objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$offset);
                            objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(countToLoad);
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d,%d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                        }
                    }
                } else {
                    isEnd = true;
                    if (this.val$max_id != 0) {
                        objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$max_id);
                        objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$type);
                        objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(countToLoad);
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    } else {
                        objArr = new Object[SharedMediaQuery.MEDIA_MUSIC];
                        objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                        objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$type);
                        objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(this.val$offset);
                        objArr[SharedMediaQuery.MEDIA_URL] = Integer.valueOf(countToLoad);
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d,%d", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                    }
                }
                while (cursor.next()) {
                    AbstractSerializedData data = cursor.byteBufferValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                    if (data != null) {
                        Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        message.id = cursor.intValue(SharedMediaQuery.MEDIA_FILE);
                        message.dialog_id = this.val$uid;
                        if (((int) this.val$uid) == 0) {
                            message.random_id = cursor.longValue(SharedMediaQuery.MEDIA_AUDIO);
                        }
                        res.messages.add(message);
                        if (message.from_id > 0) {
                            if (!usersToLoad.contains(Integer.valueOf(message.from_id))) {
                                usersToLoad.add(Integer.valueOf(message.from_id));
                            }
                        } else if (!chatsToLoad.contains(Integer.valueOf(-message.from_id))) {
                            chatsToLoad.add(Integer.valueOf(-message.from_id));
                        }
                    }
                }
                cursor.dispose();
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), res.users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), res.chats);
                }
                if (res.messages.size() > this.val$count) {
                    topReached = false;
                    res.messages.remove(res.messages.size() - 1);
                } else {
                    topReached = isEnd;
                }
                SharedMediaQuery.processLoadedMedia(res, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, topReached);
            } catch (Throwable e) {
                res.messages.clear();
                res.chats.clear();
                res.users.clear();
                FileLog.m13e("tmessages", e);
                SharedMediaQuery.processLoadedMedia(res, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, false);
            } catch (Throwable th) {
                Throwable th2 = th;
                SharedMediaQuery.processLoadedMedia(res, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, true, this.val$classGuid, this.val$isChannel, false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.8 */
    static class C08338 implements Runnable {
        final /* synthetic */ int val$max_id;
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ boolean val$topReached;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C08338(ArrayList arrayList, boolean z, long j, int i, int i2) {
            this.val$messages = arrayList;
            this.val$topReached = z;
            this.val$uid = j;
            this.val$max_id = i;
            this.val$type = i2;
        }

        public void run() {
            int minId = SharedMediaQuery.MEDIA_FILE;
            try {
                if (this.val$messages.isEmpty() || this.val$topReached) {
                    MessagesStorage.getInstance().doneHolesInMedia(this.val$uid, this.val$max_id, this.val$type);
                    if (this.val$messages.isEmpty()) {
                        return;
                    }
                }
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement state2 = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                Iterator i$ = this.val$messages.iterator();
                while (i$.hasNext()) {
                    Message message = (Message) i$.next();
                    if (SharedMediaQuery.canAddMessageToMedia(message)) {
                        long messageId = (long) message.id;
                        if (message.to_id.channel_id != 0) {
                            messageId |= ((long) message.to_id.channel_id) << 32;
                        }
                        state2.requery();
                        NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                        message.serializeToStream(data);
                        state2.bindLong(SharedMediaQuery.MEDIA_FILE, messageId);
                        state2.bindLong(SharedMediaQuery.MEDIA_AUDIO, this.val$uid);
                        state2.bindInteger(SharedMediaQuery.MEDIA_URL, message.date);
                        state2.bindInteger(SharedMediaQuery.MEDIA_MUSIC, this.val$type);
                        state2.bindByteBuffer((int) SharedMediaQuery.MEDIA_TYPES_COUNT, data);
                        state2.step();
                        data.reuse();
                    }
                }
                state2.dispose();
                if (!(this.val$topReached && this.val$max_id == 0)) {
                    if (!this.val$topReached) {
                        minId = ((Message) this.val$messages.get(this.val$messages.size() - 1)).id;
                    }
                    if (this.val$max_id != 0) {
                        MessagesStorage.getInstance().closeHolesInMedia(this.val$uid, minId, this.val$max_id, this.val$type);
                    } else {
                        MessagesStorage.getInstance().closeHolesInMedia(this.val$uid, minId, ConnectionsManager.DEFAULT_DATACENTER_ID, this.val$type);
                    }
                }
                MessagesStorage.getInstance().getDatabase().commitTransaction();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.9 */
    static class C08359 implements Runnable {
        final /* synthetic */ int val$max_id;
        final /* synthetic */ long val$uid;

        /* renamed from: org.telegram.messenger.query.SharedMediaQuery.9.1 */
        class C08341 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;

            C08341(ArrayList arrayList) {
                this.val$arrayList = arrayList;
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.musicDidLoaded;
                Object[] objArr = new Object[SharedMediaQuery.MEDIA_AUDIO];
                objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(C08359.this.val$uid);
                objArr[SharedMediaQuery.MEDIA_FILE] = this.val$arrayList;
                instance.postNotificationName(i, objArr);
            }
        }

        C08359(long j, int i) {
            this.val$uid = j;
            this.val$max_id = i;
        }

        public void run() {
            ArrayList<MessageObject> arrayList = new ArrayList();
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[SharedMediaQuery.MEDIA_URL];
                objArr[SharedMediaQuery.MEDIA_PHOTOVIDEO] = Long.valueOf(this.val$uid);
                objArr[SharedMediaQuery.MEDIA_FILE] = Integer.valueOf(this.val$max_id);
                objArr[SharedMediaQuery.MEDIA_AUDIO] = Integer.valueOf(SharedMediaQuery.MEDIA_MUSIC);
                SQLiteCursor cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", objArr), new Object[SharedMediaQuery.MEDIA_PHOTOVIDEO]);
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(SharedMediaQuery.MEDIA_PHOTOVIDEO);
                    if (data != null) {
                        Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (MessageObject.isMusicMessage(message)) {
                            message.id = cursor.intValue(SharedMediaQuery.MEDIA_FILE);
                            message.dialog_id = this.val$uid;
                            arrayList.add(SharedMediaQuery.MEDIA_PHOTOVIDEO, new MessageObject(message, null, false));
                        }
                    }
                }
                cursor.dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C08341(arrayList));
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.1 */
    static class C17141 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ int val$offset;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        C17141(int i, long j, int i2, int i3, int i4, int i5, boolean z) {
            this.val$count = i;
            this.val$uid = j;
            this.val$offset = i2;
            this.val$max_id = i3;
            this.val$type = i4;
            this.val$classGuid = i5;
            this.val$isChannel = z;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                boolean topReached;
                messages_Messages res = (messages_Messages) response;
                if (res.messages.size() > this.val$count) {
                    topReached = false;
                    res.messages.remove(res.messages.size() - 1);
                } else {
                    topReached = true;
                }
                SharedMediaQuery.processLoadedMedia(res, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, false, this.val$classGuid, this.val$isChannel, topReached);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SharedMediaQuery.2 */
    static class C17152 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        /* renamed from: org.telegram.messenger.query.SharedMediaQuery.2.1 */
        class C08271 implements Runnable {
            final /* synthetic */ messages_Messages val$res;

            C08271(messages_Messages org_telegram_tgnet_TLRPC_messages_Messages) {
                this.val$res = org_telegram_tgnet_TLRPC_messages_Messages;
            }

            public void run() {
                MessagesController.getInstance().putUsers(this.val$res.users, false);
                MessagesController.getInstance().putChats(this.val$res.chats, false);
            }
        }

        C17152(long j, int i, int i2) {
            this.val$uid = j;
            this.val$type = i;
            this.val$classGuid = i2;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                int count;
                messages_Messages res = (messages_Messages) response;
                MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
                if (res instanceof TL_messages_messages) {
                    count = res.messages.size();
                } else {
                    count = res.count;
                }
                AndroidUtilities.runOnUIThread(new C08271(res));
                SharedMediaQuery.processLoadedMediaCount(count, this.val$uid, this.val$type, this.val$classGuid, false);
            }
        }
    }

    public static void loadMedia(long uid, int offset, int count, int max_id, int type, boolean fromCache, int classGuid) {
        boolean isChannel = ((int) uid) < 0 && ChatObject.isChannel(-((int) uid));
        int lower_part = (int) uid;
        if (fromCache || lower_part == 0) {
            loadMediaDatabase(uid, offset, count, max_id, type, classGuid, isChannel);
            return;
        }
        TLObject req = new TL_messages_search();
        req.offset = offset;
        req.limit = count + MEDIA_FILE;
        req.max_id = max_id;
        if (type == 0) {
            req.filter = new TL_inputMessagesFilterPhotoVideo();
        } else if (type == MEDIA_FILE) {
            req.filter = new TL_inputMessagesFilterDocument();
        } else if (type == MEDIA_AUDIO) {
            req.filter = new TL_inputMessagesFilterVoice();
        } else if (type == MEDIA_URL) {
            req.filter = new TL_inputMessagesFilterUrl();
        } else if (type == MEDIA_MUSIC) {
            req.filter = new TL_inputMessagesFilterMusic();
        }
        req.f37q = TtmlNode.ANONYMOUS_REGION_ID;
        req.peer = MessagesController.getInputPeer(lower_part);
        if (req.peer != null) {
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new C17141(count, uid, offset, max_id, type, classGuid, isChannel)), classGuid);
        }
    }

    public static void getMediaCount(long uid, int type, int classGuid, boolean fromCache) {
        int lower_part = (int) uid;
        if (fromCache || lower_part == 0) {
            getMediaCountDatabase(uid, type, classGuid);
            return;
        }
        TL_messages_search req = new TL_messages_search();
        req.offset = MEDIA_PHOTOVIDEO;
        req.limit = MEDIA_FILE;
        req.max_id = MEDIA_PHOTOVIDEO;
        if (type == 0) {
            req.filter = new TL_inputMessagesFilterPhotoVideo();
        } else if (type == MEDIA_FILE) {
            req.filter = new TL_inputMessagesFilterDocument();
        } else if (type == MEDIA_AUDIO) {
            req.filter = new TL_inputMessagesFilterVoice();
        } else if (type == MEDIA_URL) {
            req.filter = new TL_inputMessagesFilterUrl();
        } else if (type == MEDIA_MUSIC) {
            req.filter = new TL_inputMessagesFilterMusic();
        }
        req.f37q = TtmlNode.ANONYMOUS_REGION_ID;
        req.peer = MessagesController.getInputPeer(lower_part);
        if (req.peer != null) {
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new C17152(uid, type, classGuid)), classGuid);
        }
    }

    public static int getMediaType(Message message) {
        if (message == null) {
            return -1;
        }
        if (message.media instanceof TL_messageMediaPhoto) {
            return MEDIA_PHOTOVIDEO;
        }
        if (message.media instanceof TL_messageMediaDocument) {
            if (MessageObject.isVoiceMessage(message)) {
                return MEDIA_AUDIO;
            }
            if (MessageObject.isVideoMessage(message)) {
                return MEDIA_PHOTOVIDEO;
            }
            if (MessageObject.isStickerMessage(message)) {
                return -1;
            }
            if (MessageObject.isMusicMessage(message)) {
                return MEDIA_MUSIC;
            }
            return MEDIA_FILE;
        } else if (message.entities.isEmpty()) {
            return -1;
        } else {
            for (int a = MEDIA_PHOTOVIDEO; a < message.entities.size(); a += MEDIA_FILE) {
                MessageEntity entity = (MessageEntity) message.entities.get(a);
                if ((entity instanceof TL_messageEntityUrl) || (entity instanceof TL_messageEntityTextUrl) || (entity instanceof TL_messageEntityEmail)) {
                    return MEDIA_URL;
                }
            }
            return -1;
        }
    }

    public static boolean canAddMessageToMedia(Message message) {
        if ((message instanceof TL_message_secret) && (message.media instanceof TL_messageMediaPhoto) && message.ttl != 0 && message.ttl <= 60) {
            return false;
        }
        if ((message.media instanceof TL_messageMediaPhoto) || ((message.media instanceof TL_messageMediaDocument) && !MessageObject.isGifDocument(message.media.document))) {
            return true;
        }
        if (message.entities.isEmpty()) {
            return false;
        }
        for (int a = MEDIA_PHOTOVIDEO; a < message.entities.size(); a += MEDIA_FILE) {
            MessageEntity entity = (MessageEntity) message.entities.get(a);
            if ((entity instanceof TL_messageEntityUrl) || (entity instanceof TL_messageEntityTextUrl) || (entity instanceof TL_messageEntityEmail)) {
                return true;
            }
        }
        return false;
    }

    private static void processLoadedMedia(messages_Messages res, long uid, int offset, int count, int max_id, int type, boolean fromCache, int classGuid, boolean isChannel, boolean topReached) {
        int lower_part = (int) uid;
        if (fromCache && res.messages.isEmpty() && lower_part != 0) {
            loadMedia(uid, offset, count, max_id, type, false, classGuid);
            return;
        }
        int a;
        if (!fromCache) {
            ImageLoader.saveMessagesThumbs(res.messages);
            MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
            putMediaDatabase(uid, type, res.messages, max_id, topReached);
        }
        AbstractMap usersDict = new HashMap();
        for (a = MEDIA_PHOTOVIDEO; a < res.users.size(); a += MEDIA_FILE) {
            User u = (User) res.users.get(a);
            usersDict.put(Integer.valueOf(u.id), u);
        }
        ArrayList<MessageObject> objects = new ArrayList();
        for (a = MEDIA_PHOTOVIDEO; a < res.messages.size(); a += MEDIA_FILE) {
            objects.add(new MessageObject((Message) res.messages.get(a), usersDict, true));
        }
        AndroidUtilities.runOnUIThread(new C08283(res, fromCache, uid, objects, classGuid, type, topReached));
    }

    private static void processLoadedMediaCount(int count, long uid, int type, int classGuid, boolean fromCache) {
        AndroidUtilities.runOnUIThread(new C08294(uid, fromCache, count, type, classGuid));
    }

    private static void putMediaCountDatabase(long uid, int type, int count) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08305(uid, type, count));
    }

    private static void getMediaCountDatabase(long uid, int type, int classGuid) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08316(uid, type, classGuid));
    }

    private static void loadMediaDatabase(long uid, int offset, int count, int max_id, int type, int classGuid, boolean isChannel) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08327(count, uid, max_id, isChannel, type, offset, classGuid));
    }

    private static void putMediaDatabase(long uid, int type, ArrayList<Message> messages, int max_id, boolean topReached) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08338(messages, topReached, uid, max_id, type));
    }

    public static void loadMusic(long uid, int max_id) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08359(uid, max_id));
    }
}
