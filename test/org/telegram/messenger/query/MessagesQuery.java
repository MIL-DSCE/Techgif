package org.telegram.messenger.query;

import android.text.Spannable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.Components.URLSpanUserMention;

public class MessagesQuery {

    /* renamed from: org.telegram.messenger.query.MessagesQuery.1 */
    static class C08061 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$mid;

        C08061(int i, int i2) {
            this.val$channelId = i;
            this.val$mid = i2;
        }

        public void run() {
            MessagesQuery.loadPinnedMessageInternal(this.val$channelId, this.val$mid, false);
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.3 */
    static class C08073 implements Runnable {
        final /* synthetic */ Message val$result;

        C08073(Message message) {
            this.val$result = message;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                NativeByteBuffer data = new NativeByteBuffer(this.val$result.getObjectSize());
                this.val$result.serializeToStream(data);
                state.requery();
                state.bindInteger(1, this.val$result.to_id.channel_id);
                state.bindInteger(2, this.val$result.id);
                state.bindByteBuffer(3, data);
                state.step();
                data.reuse();
                state.dispose();
                MessagesStorage.getInstance().getDatabase().commitTransaction();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.4 */
    static class C08084 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ HashMap val$chatsDict;
        final /* synthetic */ boolean val$isCache;
        final /* synthetic */ Message val$result;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ HashMap val$usersDict;

        C08084(ArrayList arrayList, boolean z, ArrayList arrayList2, Message message, HashMap hashMap, HashMap hashMap2) {
            this.val$users = arrayList;
            this.val$isCache = z;
            this.val$chats = arrayList2;
            this.val$result = message;
            this.val$usersDict = hashMap;
            this.val$chatsDict = hashMap2;
        }

        public void run() {
            MessagesController.getInstance().putUsers(this.val$users, this.val$isCache);
            MessagesController.getInstance().putChats(this.val$chats, this.val$isCache);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedPinnedMessage, new MessageObject(this.val$result, this.val$usersDict, this.val$chatsDict, false));
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.5 */
    static class C08105 implements Runnable {
        final /* synthetic */ long val$dialogId;
        final /* synthetic */ HashMap val$replyMessageRandomOwners;
        final /* synthetic */ ArrayList val$replyMessages;

        /* renamed from: org.telegram.messenger.query.MessagesQuery.5.1 */
        class C08091 implements Runnable {
            C08091() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(C08105.this.val$dialogId));
            }
        }

        C08105(ArrayList arrayList, long j, HashMap hashMap) {
            this.val$replyMessages = arrayList;
            this.val$dialogId = j;
            this.val$replyMessageRandomOwners = hashMap;
        }

        public void run() {
            try {
                ArrayList<MessageObject> arrayList;
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[1];
                objArr[0] = TextUtils.join(",", this.val$replyMessages);
                SQLiteCursor cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", objArr), new Object[0]);
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        message.id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = this.val$dialogId;
                        arrayList = (ArrayList) this.val$replyMessageRandomOwners.remove(Long.valueOf(cursor.longValue(3)));
                        if (arrayList != null) {
                            MessageObject messageObject = new MessageObject(message, null, null, false);
                            for (int b = 0; b < arrayList.size(); b++) {
                                MessageObject object = (MessageObject) arrayList.get(b);
                                object.replyMessageObject = messageObject;
                                object.messageOwner.reply_to_msg_id = messageObject.getId();
                            }
                        }
                    }
                }
                cursor.dispose();
                if (!this.val$replyMessageRandomOwners.isEmpty()) {
                    for (Entry<Long, ArrayList<MessageObject>> entry : this.val$replyMessageRandomOwners.entrySet()) {
                        arrayList = (ArrayList) entry.getValue();
                        for (int a = 0; a < arrayList.size(); a++) {
                            ((MessageObject) arrayList.get(a)).messageOwner.reply_to_random_id = 0;
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C08091());
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.6 */
    static class C08116 implements Runnable {
        final /* synthetic */ int val$channelIdFinal;
        final /* synthetic */ long val$dialogId;
        final /* synthetic */ HashMap val$replyMessageOwners;
        final /* synthetic */ ArrayList val$replyMessages;
        final /* synthetic */ StringBuilder val$stringBuilder;

        /* renamed from: org.telegram.messenger.query.MessagesQuery.6.1 */
        class C17071 implements RequestDelegate {
            C17071() {
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    messages_Messages messagesRes = (messages_Messages) response;
                    ImageLoader.saveMessagesThumbs(messagesRes.messages);
                    MessagesQuery.broadcastReplyMessages(messagesRes.messages, C08116.this.val$replyMessageOwners, messagesRes.users, messagesRes.chats, C08116.this.val$dialogId, false);
                    MessagesStorage.getInstance().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                    MessagesQuery.saveReplyMessages(C08116.this.val$replyMessageOwners, messagesRes.messages);
                }
            }
        }

        /* renamed from: org.telegram.messenger.query.MessagesQuery.6.2 */
        class C17082 implements RequestDelegate {
            C17082() {
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    messages_Messages messagesRes = (messages_Messages) response;
                    ImageLoader.saveMessagesThumbs(messagesRes.messages);
                    MessagesQuery.broadcastReplyMessages(messagesRes.messages, C08116.this.val$replyMessageOwners, messagesRes.users, messagesRes.chats, C08116.this.val$dialogId, false);
                    MessagesStorage.getInstance().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                    MessagesQuery.saveReplyMessages(C08116.this.val$replyMessageOwners, messagesRes.messages);
                }
            }
        }

        C08116(StringBuilder stringBuilder, long j, ArrayList arrayList, HashMap hashMap, int i) {
            this.val$stringBuilder = stringBuilder;
            this.val$dialogId = j;
            this.val$replyMessages = arrayList;
            this.val$replyMessageOwners = hashMap;
            this.val$channelIdFinal = i;
        }

        public void run() {
            try {
                ArrayList<Message> result = new ArrayList();
                ArrayList<User> users = new ArrayList();
                ArrayList<Chat> chats = new ArrayList();
                ArrayList<Integer> usersToLoad = new ArrayList();
                ArrayList<Integer> chatsToLoad = new ArrayList();
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[1];
                objArr[0] = this.val$stringBuilder.toString();
                SQLiteCursor cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", objArr), new Object[0]);
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        message.id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = this.val$dialogId;
                        MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        result.add(message);
                        this.val$replyMessages.remove(Integer.valueOf(message.id));
                    }
                }
                cursor.dispose();
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
                MessagesQuery.broadcastReplyMessages(result, this.val$replyMessageOwners, users, chats, this.val$dialogId, true);
                if (!this.val$replyMessages.isEmpty()) {
                    if (this.val$channelIdFinal != 0) {
                        TL_channels_getMessages req = new TL_channels_getMessages();
                        req.channel = MessagesController.getInputChannel(this.val$channelIdFinal);
                        req.id = this.val$replyMessages;
                        ConnectionsManager.getInstance().sendRequest(req, new C17071());
                        return;
                    }
                    TL_messages_getMessages req2 = new TL_messages_getMessages();
                    req2.id = this.val$replyMessages;
                    ConnectionsManager.getInstance().sendRequest(req2, new C17082());
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.7 */
    static class C08127 implements Runnable {
        final /* synthetic */ HashMap val$replyMessageOwners;
        final /* synthetic */ ArrayList val$result;

        C08127(ArrayList arrayList, HashMap hashMap) {
            this.val$result = arrayList;
            this.val$replyMessageOwners = hashMap;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
                for (int a = 0; a < this.val$result.size(); a++) {
                    Message message = (Message) this.val$result.get(a);
                    ArrayList<MessageObject> messageObjects = (ArrayList) this.val$replyMessageOwners.get(Integer.valueOf(message.id));
                    if (messageObjects != null) {
                        NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                        message.serializeToStream(data);
                        for (int b = 0; b < messageObjects.size(); b++) {
                            MessageObject messageObject = (MessageObject) messageObjects.get(b);
                            state.requery();
                            long messageId = (long) messageObject.getId();
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            state.bindByteBuffer(1, data);
                            state.bindLong(2, messageId);
                            state.step();
                        }
                        data.reuse();
                    }
                }
                state.dispose();
                MessagesStorage.getInstance().getDatabase().commitTransaction();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.8 */
    static class C08138 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ HashMap val$chatsDict;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ boolean val$isCache;
        final /* synthetic */ HashMap val$replyMessageOwners;
        final /* synthetic */ ArrayList val$result;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ HashMap val$usersDict;

        C08138(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, HashMap hashMap, HashMap hashMap2, HashMap hashMap3, long j) {
            this.val$users = arrayList;
            this.val$isCache = z;
            this.val$chats = arrayList2;
            this.val$result = arrayList3;
            this.val$replyMessageOwners = hashMap;
            this.val$usersDict = hashMap2;
            this.val$chatsDict = hashMap3;
            this.val$dialog_id = j;
        }

        public void run() {
            MessagesController.getInstance().putUsers(this.val$users, this.val$isCache);
            MessagesController.getInstance().putChats(this.val$chats, this.val$isCache);
            boolean changed = false;
            for (int a = 0; a < this.val$result.size(); a++) {
                Message message = (Message) this.val$result.get(a);
                ArrayList<MessageObject> arrayList = (ArrayList) this.val$replyMessageOwners.get(Integer.valueOf(message.id));
                if (arrayList != null) {
                    MessageObject messageObject = new MessageObject(message, this.val$usersDict, this.val$chatsDict, false);
                    for (int b = 0; b < arrayList.size(); b++) {
                        MessageObject m = (MessageObject) arrayList.get(b);
                        m.replyMessageObject = messageObject;
                        if (m.messageOwner.action instanceof TL_messageActionPinMessage) {
                            m.generatePinMessageText(null, null);
                        }
                    }
                    changed = true;
                }
            }
            if (changed) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(this.val$dialog_id));
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesQuery.2 */
    static class C17062 implements RequestDelegate {
        final /* synthetic */ int val$channelId;

        C17062(int i) {
            this.val$channelId = i;
        }

        public void run(TLObject response, TL_error error) {
            boolean ok = false;
            if (error == null) {
                messages_Messages messagesRes = (messages_Messages) response;
                if (!messagesRes.messages.isEmpty()) {
                    ImageLoader.saveMessagesThumbs(messagesRes.messages);
                    MessagesQuery.broadcastPinnedMessage((Message) messagesRes.messages.get(0), messagesRes.users, messagesRes.chats, false, false);
                    MessagesStorage.getInstance().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                    MessagesQuery.savePinnedMessage((Message) messagesRes.messages.get(0));
                    ok = true;
                }
            }
            if (!ok) {
                MessagesStorage.getInstance().updateChannelPinnedMessage(this.val$channelId, 0);
            }
        }
    }

    public static MessageObject loadPinnedMessage(int channelId, int mid, boolean useQueue) {
        if (!useQueue) {
            return loadPinnedMessageInternal(channelId, mid, true);
        }
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08061(channelId, mid));
        return null;
    }

    private static MessageObject loadPinnedMessageInternal(int channelId, int mid, boolean returnValue) {
        long messageId = ((long) mid) | (((long) channelId) << 32);
        Message result = null;
        try {
            NativeByteBuffer data;
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageId)}), new Object[0]);
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    result = Message.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    result.id = cursor.intValue(1);
                    result.date = cursor.intValue(2);
                    result.dialog_id = (long) (-channelId);
                    MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                }
            }
            cursor.dispose();
            if (result == null) {
                cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Integer.valueOf(channelId)}), new Object[0]);
                if (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        result = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (result.id != mid) {
                            result = null;
                        } else {
                            result.dialog_id = (long) (-channelId);
                            MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                        }
                    }
                }
                cursor.dispose();
            }
            if (result == null) {
                TL_channels_getMessages req = new TL_channels_getMessages();
                req.channel = MessagesController.getInputChannel(channelId);
                req.id.add(Integer.valueOf(mid));
                ConnectionsManager.getInstance().sendRequest(req, new C17062(channelId));
            } else if (returnValue) {
                return broadcastPinnedMessage(result, users, chats, true, returnValue);
            } else {
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
                broadcastPinnedMessage(result, users, chats, true, false);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        return null;
    }

    private static void savePinnedMessage(Message result) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08073(result));
    }

    private static MessageObject broadcastPinnedMessage(Message result, ArrayList<User> users, ArrayList<Chat> chats, boolean isCache, boolean returnValue) {
        int a;
        HashMap<Integer, User> usersDict = new HashMap();
        for (a = 0; a < users.size(); a++) {
            User user = (User) users.get(a);
            usersDict.put(Integer.valueOf(user.id), user);
        }
        HashMap<Integer, Chat> chatsDict = new HashMap();
        for (a = 0; a < chats.size(); a++) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(Integer.valueOf(chat.id), chat);
        }
        if (returnValue) {
            return new MessageObject(result, usersDict, chatsDict, false);
        }
        AndroidUtilities.runOnUIThread(new C08084(users, isCache, chats, result, usersDict, chatsDict));
        return null;
    }

    public static void loadReplyMessagesForMessages(ArrayList<MessageObject> messages, long dialogId) {
        StringBuilder stringBuilder;
        int a;
        MessageObject messageObject;
        ArrayList<MessageObject> messageObjects;
        if (((int) dialogId) == 0) {
            ArrayList<Long> replyMessages = new ArrayList();
            HashMap<Long, ArrayList<MessageObject>> replyMessageRandomOwners = new HashMap();
            stringBuilder = new StringBuilder();
            for (a = 0; a < messages.size(); a++) {
                messageObject = (MessageObject) messages.get(a);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    Long id = Long.valueOf(messageObject.messageOwner.reply_to_random_id);
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(',');
                    }
                    stringBuilder.append(id);
                    messageObjects = (ArrayList) replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(id)) {
                        replyMessages.add(id);
                    }
                }
            }
            if (!replyMessages.isEmpty()) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08105(replyMessages, dialogId, replyMessageRandomOwners));
                return;
            }
            return;
        }
        ArrayList<Integer> replyMessages2 = new ArrayList();
        HashMap<Integer, ArrayList<MessageObject>> replyMessageOwners = new HashMap();
        stringBuilder = new StringBuilder();
        int channelId = 0;
        for (a = 0; a < messages.size(); a++) {
            messageObject = (MessageObject) messages.get(a);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                Integer id2 = Integer.valueOf(messageObject.messageOwner.reply_to_msg_id);
                long messageId = (long) id2.intValue();
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                    channelId = messageObject.messageOwner.to_id.channel_id;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(messageId);
                messageObjects = (ArrayList) replyMessageOwners.get(id2);
                if (messageObjects == null) {
                    messageObjects = new ArrayList();
                    replyMessageOwners.put(id2, messageObjects);
                }
                messageObjects.add(messageObject);
                if (!replyMessages2.contains(id2)) {
                    replyMessages2.add(id2);
                }
            }
        }
        if (!replyMessages2.isEmpty()) {
            int channelIdFinal = channelId;
            DispatchQueue storageQueue = MessagesStorage.getInstance().getStorageQueue();
            r20.postRunnable(new C08116(stringBuilder, dialogId, replyMessages2, replyMessageOwners, channelIdFinal));
        }
    }

    private static void saveReplyMessages(HashMap<Integer, ArrayList<MessageObject>> replyMessageOwners, ArrayList<Message> result) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08127(result, replyMessageOwners));
    }

    private static void broadcastReplyMessages(ArrayList<Message> result, HashMap<Integer, ArrayList<MessageObject>> replyMessageOwners, ArrayList<User> users, ArrayList<Chat> chats, long dialog_id, boolean isCache) {
        int a;
        HashMap<Integer, User> usersDict = new HashMap();
        for (a = 0; a < users.size(); a++) {
            User user = (User) users.get(a);
            usersDict.put(Integer.valueOf(user.id), user);
        }
        HashMap<Integer, Chat> chatsDict = new HashMap();
        for (a = 0; a < chats.size(); a++) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(Integer.valueOf(chat.id), chat);
        }
        AndroidUtilities.runOnUIThread(new C08138(users, isCache, chats, result, replyMessageOwners, usersDict, chatsDict, dialog_id));
    }

    public static ArrayList<MessageEntity> getEntities(CharSequence message) {
        if (message == null) {
            return null;
        }
        if (!(message instanceof Spannable)) {
            return null;
        }
        Spannable spannable = (Spannable) message;
        URLSpanUserMention[] spans = (URLSpanUserMention[]) spannable.getSpans(0, message.length(), URLSpanUserMention.class);
        if (spans == null || spans.length <= 0) {
            return null;
        }
        ArrayList<MessageEntity> entities = new ArrayList();
        for (int b = 0; b < spans.length; b++) {
            TL_inputMessageEntityMentionName entity = new TL_inputMessageEntityMentionName();
            entity.user_id = MessagesController.getInputUser(Utilities.parseInt(spans[b].getURL()).intValue());
            if (entity.user_id != null) {
                entity.offset = spannable.getSpanStart(spans[b]);
                entity.length = Math.min(spannable.getSpanEnd(spans[b]), message.length()) - entity.offset;
                if (message.charAt((entity.offset + entity.length) - 1) == ' ') {
                    entity.length--;
                }
                entities.add(entity);
            }
        }
        return entities;
    }
}
