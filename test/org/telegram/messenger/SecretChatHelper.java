package org.telegram.messenger;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.view.InputDeviceCompat;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLClassStore;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.DecryptedMessageAction;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedFile;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaAudio;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService_layer8;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_encryptedFile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_acceptEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_discardEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_requestEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncrypted;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedService;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_DhConfig;
import org.telegram.tgnet.TLRPC.messages_SentEncryptedMessage;

public class SecretChatHelper {
    public static final int CURRENT_SECRET_CHAT_LAYER = 46;
    private static volatile SecretChatHelper Instance;
    private HashMap<Integer, EncryptedChat> acceptingChats;
    public ArrayList<Update> delayedEncryptedChatUpdates;
    private ArrayList<Long> pendingEncMessagesToDelete;
    private HashMap<Integer, ArrayList<TL_decryptedMessageHolder>> secretHolesQueue;
    private ArrayList<Integer> sendingNotifyLayer;
    private boolean startingSecretChat;

    /* renamed from: org.telegram.messenger.SecretChatHelper.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ EncryptedChat val$encryptedChat;

        AnonymousClass10(EncryptedChat encryptedChat) {
            this.val$encryptedChat = encryptedChat;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$encryptedChat, false);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$encryptedChat);
            SecretChatHelper.this.sendNotifyLayerMessage(this.val$encryptedChat, null);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ TL_encryptedChatDiscarded val$newChat;

        AnonymousClass11(TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
            this.val$newChat = tL_encryptedChatDiscarded;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.15 */
    class AnonymousClass15 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        AnonymousClass15(int i) {
            this.val$reqId = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.1 */
    class C06981 implements Runnable {
        final /* synthetic */ ArrayList val$pendingEncMessagesToDeleteCopy;

        C06981(ArrayList arrayList) {
            this.val$pendingEncMessagesToDeleteCopy = arrayList;
        }

        public void run() {
            for (int a = 0; a < this.val$pendingEncMessagesToDeleteCopy.size(); a++) {
                MessageObject messageObject = (MessageObject) MessagesController.getInstance().dialogMessagesByRandomIds.get(this.val$pendingEncMessagesToDeleteCopy.get(a));
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.2 */
    class C06992 implements Runnable {
        final /* synthetic */ TL_dialog val$dialog;
        final /* synthetic */ EncryptedChat val$newChat;

        C06992(TL_dialog tL_dialog, EncryptedChat encryptedChat) {
            this.val$dialog = tL_dialog;
            this.val$newChat = encryptedChat;
        }

        public void run() {
            MessagesController.getInstance().dialogs_dict.put(Long.valueOf(this.val$dialog.id), this.val$dialog);
            MessagesController.getInstance().dialogs.add(this.val$dialog);
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            MessagesController.getInstance().sortDialogs(null);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.3 */
    class C07003 implements Runnable {
        final /* synthetic */ EncryptedChat val$exist;
        final /* synthetic */ EncryptedChat val$newChat;

        C07003(EncryptedChat encryptedChat, EncryptedChat encryptedChat2) {
            this.val$exist = encryptedChat;
            this.val$newChat = encryptedChat2;
        }

        public void run() {
            if (this.val$exist != null) {
                MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            }
            MessagesStorage.getInstance().updateEncryptedChat(this.val$newChat);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.4 */
    class C07044 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;
        final /* synthetic */ InputEncryptedFile val$encryptedFile;
        final /* synthetic */ MessageObject val$newMsg;
        final /* synthetic */ Message val$newMsgObj;
        final /* synthetic */ String val$originalPath;
        final /* synthetic */ DecryptedMessage val$req;

        /* renamed from: org.telegram.messenger.SecretChatHelper.4.1 */
        class C16891 implements RequestDelegate {

            /* renamed from: org.telegram.messenger.SecretChatHelper.4.1.1 */
            class C07021 implements Runnable {
                final /* synthetic */ String val$attachPath;
                final /* synthetic */ messages_SentEncryptedMessage val$res;

                /* renamed from: org.telegram.messenger.SecretChatHelper.4.1.1.1 */
                class C07011 implements Runnable {
                    C07011() {
                    }

                    public void run() {
                        C07044.this.val$newMsgObj.send_state = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(C07044.this.val$newMsgObj.id), Integer.valueOf(C07044.this.val$newMsgObj.id), C07044.this.val$newMsgObj, Long.valueOf(C07044.this.val$newMsgObj.dialog_id));
                        SendMessagesHelper.getInstance().processSentMessage(C07044.this.val$newMsgObj.id);
                        if (MessageObject.isVideoMessage(C07044.this.val$newMsgObj)) {
                            SendMessagesHelper.getInstance().stopVideoService(C07021.this.val$attachPath);
                        }
                        SendMessagesHelper.getInstance().removeFromSendingMessages(C07044.this.val$newMsgObj.id);
                    }
                }

                C07021(messages_SentEncryptedMessage org_telegram_tgnet_TLRPC_messages_SentEncryptedMessage, String str) {
                    this.val$res = org_telegram_tgnet_TLRPC_messages_SentEncryptedMessage;
                    this.val$attachPath = str;
                }

                public void run() {
                    if (SecretChatHelper.isSecretInvisibleMessage(C07044.this.val$newMsgObj)) {
                        this.val$res.date = 0;
                    }
                    MessagesStorage.getInstance().updateMessageStateAndId(C07044.this.val$newMsgObj.random_id, Integer.valueOf(C07044.this.val$newMsgObj.id), C07044.this.val$newMsgObj.id, this.val$res.date, false, 0);
                    AndroidUtilities.runOnUIThread(new C07011());
                }
            }

            /* renamed from: org.telegram.messenger.SecretChatHelper.4.1.2 */
            class C07032 implements Runnable {
                C07032() {
                }

                public void run() {
                    C07044.this.val$newMsgObj.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(C07044.this.val$newMsgObj.id));
                    SendMessagesHelper.getInstance().processSentMessage(C07044.this.val$newMsgObj.id);
                    if (MessageObject.isVideoMessage(C07044.this.val$newMsgObj)) {
                        SendMessagesHelper.getInstance().stopVideoService(C07044.this.val$newMsgObj.attachPath);
                    }
                    SendMessagesHelper.getInstance().removeFromSendingMessages(C07044.this.val$newMsgObj.id);
                }
            }

            C16891() {
            }

            public void run(TLObject response, TL_error error) {
                if (error == null && (C07044.this.val$req.action instanceof TL_decryptedMessageActionNotifyLayer)) {
                    EncryptedChat currentChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(C07044.this.val$chat.id));
                    if (currentChat == null) {
                        currentChat = C07044.this.val$chat;
                    }
                    if (currentChat.key_hash == null) {
                        currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
                    }
                    if (AndroidUtilities.getPeerLayerVersion(currentChat.layer) >= SecretChatHelper.CURRENT_SECRET_CHAT_LAYER && currentChat.key_hash.length == 16) {
                        try {
                            byte[] sha256 = Utilities.computeSHA256(C07044.this.val$chat.auth_key, 0, C07044.this.val$chat.auth_key.length);
                            byte[] key_hash = new byte[36];
                            System.arraycopy(C07044.this.val$chat.key_hash, 0, key_hash, 0, 16);
                            System.arraycopy(sha256, 0, key_hash, 16, 20);
                            currentChat.key_hash = key_hash;
                            MessagesStorage.getInstance().updateEncryptedChat(currentChat);
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                    SecretChatHelper.this.sendingNotifyLayer.remove(Integer.valueOf(currentChat.id));
                    currentChat.layer = AndroidUtilities.setMyLayerVersion(currentChat.layer, SecretChatHelper.CURRENT_SECRET_CHAT_LAYER);
                    MessagesStorage.getInstance().updateEncryptedChatLayer(currentChat);
                }
                if (C07044.this.val$newMsgObj == null) {
                    return;
                }
                if (error == null) {
                    String attachPath = C07044.this.val$newMsgObj.attachPath;
                    messages_SentEncryptedMessage res = (messages_SentEncryptedMessage) response;
                    if (SecretChatHelper.isSecretVisibleMessage(C07044.this.val$newMsgObj)) {
                        C07044.this.val$newMsgObj.date = res.date;
                    }
                    if (C07044.this.val$newMsg != null && (res.file instanceof TL_encryptedFile)) {
                        SecretChatHelper.this.updateMediaPaths(C07044.this.val$newMsg, res.file, C07044.this.val$req, C07044.this.val$originalPath);
                    }
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07021(res, attachPath));
                    return;
                }
                MessagesStorage.getInstance().markMessageAsSendError(C07044.this.val$newMsgObj);
                AndroidUtilities.runOnUIThread(new C07032());
            }
        }

        C07044(EncryptedChat encryptedChat, DecryptedMessage decryptedMessage, Message message, InputEncryptedFile inputEncryptedFile, MessageObject messageObject, String str) {
            this.val$chat = encryptedChat;
            this.val$req = decryptedMessage;
            this.val$newMsgObj = message;
            this.val$encryptedFile = inputEncryptedFile;
            this.val$newMsg = messageObject;
            this.val$originalPath = str;
        }

        public void run() {
            try {
                TLObject toEncryptObject;
                TLObject reqToSend;
                if (AndroidUtilities.getPeerLayerVersion(this.val$chat.layer) >= 17) {
                    TLObject layer = new TL_decryptedMessageLayer();
                    layer.layer = Math.min(Math.max(17, AndroidUtilities.getMyLayerVersion(this.val$chat.layer)), AndroidUtilities.getPeerLayerVersion(this.val$chat.layer));
                    layer.message = this.val$req;
                    layer.random_bytes = new byte[15];
                    Utilities.random.nextBytes(layer.random_bytes);
                    toEncryptObject = layer;
                    if (this.val$chat.seq_in == 0 && this.val$chat.seq_out == 0) {
                        if (this.val$chat.admin_id == UserConfig.getClientUserId()) {
                            this.val$chat.seq_out = 1;
                        } else {
                            this.val$chat.seq_in = 1;
                        }
                    }
                    if (this.val$newMsgObj.seq_in == 0 && this.val$newMsgObj.seq_out == 0) {
                        layer.in_seq_no = this.val$chat.seq_in;
                        layer.out_seq_no = this.val$chat.seq_out;
                        EncryptedChat encryptedChat = this.val$chat;
                        encryptedChat.seq_out += 2;
                        if (AndroidUtilities.getPeerLayerVersion(this.val$chat.layer) >= 20) {
                            if (this.val$chat.key_create_date == 0) {
                                this.val$chat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                            }
                            encryptedChat = this.val$chat;
                            encryptedChat.key_use_count_out = (short) (encryptedChat.key_use_count_out + 1);
                            if ((this.val$chat.key_use_count_out >= (short) 100 || this.val$chat.key_create_date < ConnectionsManager.getInstance().getCurrentTime() - 604800) && this.val$chat.exchange_id == 0 && this.val$chat.future_key_fingerprint == 0) {
                                SecretChatHelper.this.requestNewSecretChatKey(this.val$chat);
                            }
                        }
                        MessagesStorage.getInstance().updateEncryptedChatSeq(this.val$chat);
                        if (this.val$newMsgObj != null) {
                            this.val$newMsgObj.seq_in = layer.in_seq_no;
                            this.val$newMsgObj.seq_out = layer.out_seq_no;
                            MessagesStorage.getInstance().setMessageSeq(this.val$newMsgObj.id, this.val$newMsgObj.seq_in, this.val$newMsgObj.seq_out);
                        }
                    } else {
                        layer.in_seq_no = this.val$newMsgObj.seq_in;
                        layer.out_seq_no = this.val$newMsgObj.seq_out;
                    }
                    FileLog.m11e("tmessages", this.val$req + " send message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
                } else {
                    toEncryptObject = this.val$req;
                }
                int len = toEncryptObject.getObjectSize();
                AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(len + 4);
                nativeByteBuffer.writeInt32(len);
                toEncryptObject.serializeToStream(nativeByteBuffer);
                Object messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer);
                Object messageKey = new byte[16];
                if (messageKeyFull.length != 0) {
                    System.arraycopy(messageKeyFull, messageKeyFull.length - 16, messageKey, 0, 16);
                }
                MessageKeyData keyData = MessageKeyData.generateMessageKeyData(this.val$chat.auth_key, messageKey, false);
                len = nativeByteBuffer.length();
                int extraLen = len % 16 != 0 ? 16 - (len % 16) : 0;
                NativeByteBuffer dataForEncryption = new NativeByteBuffer(len + extraLen);
                nativeByteBuffer.position(0);
                dataForEncryption.writeBytes((NativeByteBuffer) nativeByteBuffer);
                if (extraLen != 0) {
                    byte[] b = new byte[extraLen];
                    Utilities.random.nextBytes(b);
                    dataForEncryption.writeBytes(b);
                }
                nativeByteBuffer.reuse();
                Utilities.aesIgeEncryption(dataForEncryption.buffer, keyData.aesKey, keyData.aesIv, true, false, 0, dataForEncryption.limit());
                NativeByteBuffer data = new NativeByteBuffer((messageKey.length + 8) + dataForEncryption.length());
                dataForEncryption.position(0);
                data.writeInt64(this.val$chat.key_fingerprint);
                data.writeBytes((byte[]) messageKey);
                data.writeBytes(dataForEncryption);
                dataForEncryption.reuse();
                data.position(0);
                TLObject req2;
                if (this.val$encryptedFile != null) {
                    req2 = new TL_messages_sendEncryptedFile();
                    req2.data = data;
                    req2.random_id = this.val$req.random_id;
                    req2.peer = new TL_inputEncryptedChat();
                    req2.peer.chat_id = this.val$chat.id;
                    req2.peer.access_hash = this.val$chat.access_hash;
                    req2.file = this.val$encryptedFile;
                    reqToSend = req2;
                } else if (this.val$req instanceof TL_decryptedMessageService) {
                    req2 = new TL_messages_sendEncryptedService();
                    req2.data = data;
                    req2.random_id = this.val$req.random_id;
                    req2.peer = new TL_inputEncryptedChat();
                    req2.peer.chat_id = this.val$chat.id;
                    req2.peer.access_hash = this.val$chat.access_hash;
                    reqToSend = req2;
                } else {
                    req2 = new TL_messages_sendEncrypted();
                    req2.data = data;
                    req2.random_id = this.val$req.random_id;
                    req2.peer = new TL_inputEncryptedChat();
                    req2.peer.chat_id = this.val$chat.id;
                    req2.peer.access_hash = this.val$chat.access_hash;
                    reqToSend = req2;
                }
                ConnectionsManager.getInstance().sendRequest(reqToSend, new C16891(), 64);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.5 */
    class C07055 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;

        C07055(EncryptedChat encryptedChat) {
            this.val$chat = encryptedChat;
        }

        public void run() {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$chat);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.6 */
    class C07086 implements Runnable {
        final /* synthetic */ long val$did;

        /* renamed from: org.telegram.messenger.SecretChatHelper.6.1 */
        class C07071 implements Runnable {

            /* renamed from: org.telegram.messenger.SecretChatHelper.6.1.1 */
            class C07061 implements Runnable {
                C07061() {
                }

                public void run() {
                    NotificationsController.getInstance().processReadMessages(null, C07086.this.val$did, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
                    HashMap<Long, Integer> dialogsToUpdate = new HashMap();
                    dialogsToUpdate.put(Long.valueOf(C07086.this.val$did), Integer.valueOf(0));
                    NotificationsController.getInstance().processDialogsUpdateRead(dialogsToUpdate);
                }
            }

            C07071() {
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C07061());
            }
        }

        C07086(long j) {
            this.val$did = j;
        }

        public void run() {
            TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$did));
            if (dialog != null) {
                dialog.unread_count = 0;
                MessagesController.getInstance().dialogMessage.remove(Long.valueOf(dialog.id));
            }
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07071());
            MessagesStorage.getInstance().deleteDialog(this.val$did, 1);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(this.val$did), Boolean.valueOf(false));
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.7 */
    class C07097 implements Runnable {
        final /* synthetic */ TL_encryptedChatDiscarded val$newChat;

        C07097(TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
            this.val$newChat = tL_encryptedChatDiscarded;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.8 */
    class C07108 implements Comparator<TL_decryptedMessageHolder> {
        C07108() {
        }

        public int compare(TL_decryptedMessageHolder lhs, TL_decryptedMessageHolder rhs) {
            if (lhs.layer.out_seq_no > rhs.layer.out_seq_no) {
                return 1;
            }
            if (lhs.layer.out_seq_no < rhs.layer.out_seq_no) {
                return -1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.9 */
    class C07119 implements Runnable {
        final /* synthetic */ TL_encryptedChatDiscarded val$newChat;

        C07119(TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
            this.val$newChat = tL_encryptedChatDiscarded;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            MessagesStorage.getInstance().updateEncryptedChat(this.val$newChat);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.13 */
    class AnonymousClass13 implements RequestDelegate {
        final /* synthetic */ EncryptedChat val$encryptedChat;

        /* renamed from: org.telegram.messenger.SecretChatHelper.13.1 */
        class C16871 implements RequestDelegate {

            /* renamed from: org.telegram.messenger.SecretChatHelper.13.1.1 */
            class C06921 implements Runnable {
                final /* synthetic */ EncryptedChat val$newChat;

                C06921(EncryptedChat encryptedChat) {
                    this.val$newChat = encryptedChat;
                }

                public void run() {
                    MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
                    SecretChatHelper.this.sendNotifyLayerMessage(this.val$newChat, null);
                }
            }

            C16871() {
            }

            public void run(TLObject response, TL_error error) {
                SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(AnonymousClass13.this.val$encryptedChat.id));
                if (error == null) {
                    EncryptedChat newChat = (EncryptedChat) response;
                    newChat.auth_key = AnonymousClass13.this.val$encryptedChat.auth_key;
                    newChat.user_id = AnonymousClass13.this.val$encryptedChat.user_id;
                    newChat.seq_in = AnonymousClass13.this.val$encryptedChat.seq_in;
                    newChat.seq_out = AnonymousClass13.this.val$encryptedChat.seq_out;
                    newChat.key_create_date = AnonymousClass13.this.val$encryptedChat.key_create_date;
                    newChat.key_use_count_in = AnonymousClass13.this.val$encryptedChat.key_use_count_in;
                    newChat.key_use_count_out = AnonymousClass13.this.val$encryptedChat.key_use_count_out;
                    MessagesStorage.getInstance().updateEncryptedChat(newChat);
                    AndroidUtilities.runOnUIThread(new C06921(newChat));
                }
            }
        }

        AnonymousClass13(EncryptedChat encryptedChat) {
            this.val$encryptedChat = encryptedChat;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                int a;
                messages_DhConfig res = (messages_DhConfig) response;
                if (response instanceof TL_messages_dhConfig) {
                    if (Utilities.isGoodPrime(res.f43p, res.f42g)) {
                        MessagesStorage.secretPBytes = res.f43p;
                        MessagesStorage.secretG = res.f42g;
                        MessagesStorage.lastSecretVersion = res.version;
                        MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                    } else {
                        SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(this.val$encryptedChat.id));
                        SecretChatHelper.this.declineSecretChat(this.val$encryptedChat.id);
                        return;
                    }
                }
                byte[] salt = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                for (a = 0; a < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE; a++) {
                    salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                }
                this.val$encryptedChat.a_or_b = salt;
                this.val$encryptedChat.seq_in = 1;
                this.val$encryptedChat.seq_out = 0;
                BigInteger p = new BigInteger(1, MessagesStorage.secretPBytes);
                BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), p);
                BigInteger g_a = new BigInteger(1, this.val$encryptedChat.g_a);
                if (Utilities.isGoodGaAndGb(g_a, p)) {
                    byte[] correctedAuth;
                    byte[] g_b_bytes = g_b.toByteArray();
                    if (g_b_bytes.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                        correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                        System.arraycopy(g_b_bytes, 1, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                        g_b_bytes = correctedAuth;
                    }
                    byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
                    if (authKey.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                        correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                        System.arraycopy(authKey, authKey.length + InputDeviceCompat.SOURCE_ANY, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                        authKey = correctedAuth;
                    } else if (authKey.length < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                        correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                        System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                        for (a = 0; a < 256 - authKey.length; a++) {
                            authKey[a] = (byte) 0;
                        }
                        authKey = correctedAuth;
                    }
                    byte[] authKeyHash = Utilities.computeSHA1(authKey);
                    byte[] authKeyId = new byte[8];
                    System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                    this.val$encryptedChat.auth_key = authKey;
                    this.val$encryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                    TL_messages_acceptEncryption req2 = new TL_messages_acceptEncryption();
                    req2.g_b = g_b_bytes;
                    req2.peer = new TL_inputEncryptedChat();
                    req2.peer.chat_id = this.val$encryptedChat.id;
                    TL_inputEncryptedChat tL_inputEncryptedChat = req2.peer;
                    tL_inputEncryptedChat.access_hash = this.val$encryptedChat.access_hash;
                    req2.key_fingerprint = Utilities.bytesToLong(authKeyId);
                    ConnectionsManager.getInstance().sendRequest(req2, new C16871());
                    return;
                }
                SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(this.val$encryptedChat.id));
                SecretChatHelper.this.declineSecretChat(this.val$encryptedChat.id);
                return;
            }
            SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(this.val$encryptedChat.id));
        }
    }

    /* renamed from: org.telegram.messenger.SecretChatHelper.14 */
    class AnonymousClass14 implements RequestDelegate {
        final /* synthetic */ Context val$context;
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ User val$user;

        /* renamed from: org.telegram.messenger.SecretChatHelper.14.1 */
        class C06931 implements Runnable {
            C06931() {
            }

            public void run() {
                try {
                    if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                        AnonymousClass14.this.val$progressDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        /* renamed from: org.telegram.messenger.SecretChatHelper.14.3 */
        class C06973 implements Runnable {
            C06973() {
            }

            public void run() {
                SecretChatHelper.this.startingSecretChat = false;
                if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                    try {
                        AnonymousClass14.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
        }

        /* renamed from: org.telegram.messenger.SecretChatHelper.14.2 */
        class C16882 implements RequestDelegate {
            final /* synthetic */ byte[] val$salt;

            /* renamed from: org.telegram.messenger.SecretChatHelper.14.2.1 */
            class C06951 implements Runnable {
                final /* synthetic */ TLObject val$response;

                /* renamed from: org.telegram.messenger.SecretChatHelper.14.2.1.1 */
                class C06941 implements Runnable {
                    C06941() {
                    }

                    public void run() {
                        if (!SecretChatHelper.this.delayedEncryptedChatUpdates.isEmpty()) {
                            MessagesController.getInstance().processUpdateArray(SecretChatHelper.this.delayedEncryptedChatUpdates, null, null, false);
                            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                        }
                    }
                }

                C06951(TLObject tLObject) {
                    this.val$response = tLObject;
                }

                public void run() {
                    SecretChatHelper.this.startingSecretChat = false;
                    if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                        try {
                            AnonymousClass14.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                    EncryptedChat chat = this.val$response;
                    chat.user_id = chat.participant_id;
                    chat.seq_in = 0;
                    chat.seq_out = 1;
                    chat.a_or_b = C16882.this.val$salt;
                    MessagesController.getInstance().putEncryptedChat(chat, false);
                    TL_dialog dialog = new TL_dialog();
                    dialog.id = ((long) chat.id) << 32;
                    dialog.unread_count = 0;
                    dialog.top_message = 0;
                    dialog.last_message_date = ConnectionsManager.getInstance().getCurrentTime();
                    MessagesController.getInstance().dialogs_dict.put(Long.valueOf(dialog.id), dialog);
                    MessagesController.getInstance().dialogs.add(dialog);
                    MessagesController.getInstance().sortDialogs(null);
                    MessagesStorage.getInstance().putEncryptedChat(chat, AnonymousClass14.this.val$user, dialog);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatCreated, chat);
                    Utilities.stageQueue.postRunnable(new C06941());
                }
            }

            /* renamed from: org.telegram.messenger.SecretChatHelper.14.2.2 */
            class C06962 implements Runnable {
                C06962() {
                }

                public void run() {
                    if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                        SecretChatHelper.this.startingSecretChat = false;
                        try {
                            AnonymousClass14.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                        Builder builder = new Builder(AnonymousClass14.this.val$context);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setMessage(LocaleController.getString("CreateEncryptedChatError", C0691R.string.CreateEncryptedChatError));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        builder.show().setCanceledOnTouchOutside(true);
                    }
                }
            }

            C16882(byte[] bArr) {
                this.val$salt = bArr;
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    AndroidUtilities.runOnUIThread(new C06951(response));
                    return;
                }
                SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                AndroidUtilities.runOnUIThread(new C06962());
            }
        }

        AnonymousClass14(Context context, ProgressDialog progressDialog, User user) {
            this.val$context = context;
            this.val$progressDialog = progressDialog;
            this.val$user = user;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                messages_DhConfig res = (messages_DhConfig) response;
                if (response instanceof TL_messages_dhConfig) {
                    if (Utilities.isGoodPrime(res.f43p, res.f42g)) {
                        MessagesStorage.secretPBytes = res.f43p;
                        MessagesStorage.secretG = res.f42g;
                        MessagesStorage.lastSecretVersion = res.version;
                        MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                    } else {
                        AndroidUtilities.runOnUIThread(new C06931());
                        return;
                    }
                }
                byte[] salt = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                for (int a = 0; a < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE; a++) {
                    salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                }
                byte[] g_a = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
                if (g_a.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                    byte[] correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                    System.arraycopy(g_a, 1, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                    g_a = correctedAuth;
                }
                TL_messages_requestEncryption req2 = new TL_messages_requestEncryption();
                req2.g_a = g_a;
                req2.user_id = MessagesController.getInputUser(this.val$user);
                req2.random_id = Utilities.random.nextInt();
                ConnectionsManager.getInstance().sendRequest(req2, new C16882(salt), 2);
                return;
            }
            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
            AndroidUtilities.runOnUIThread(new C06973());
        }
    }

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor;
        public int date;
        public EncryptedFile file;
        public TL_decryptedMessageLayer layer;
        public boolean new_key_used;
        public long random_id;

        static {
            constructor = 1431655929;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.layer = TL_decryptedMessageLayer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (stream.readBool(exception)) {
                this.file = EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.new_key_used = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            stream.writeInt32(this.date);
            this.layer.serializeToStream(stream);
            stream.writeBool(this.file != null);
            if (this.file != null) {
                this.file.serializeToStream(stream);
            }
            stream.writeBool(this.new_key_used);
        }
    }

    public SecretChatHelper() {
        this.sendingNotifyLayer = new ArrayList();
        this.secretHolesQueue = new HashMap();
        this.acceptingChats = new HashMap();
        this.delayedEncryptedChatUpdates = new ArrayList();
        this.pendingEncMessagesToDelete = new ArrayList();
        this.startingSecretChat = false;
    }

    static {
        Instance = null;
    }

    public static SecretChatHelper getInstance() {
        SecretChatHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (SecretChatHelper.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        SecretChatHelper localInstance2 = new SecretChatHelper();
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

    public void cleanup() {
        this.sendingNotifyLayer.clear();
        this.acceptingChats.clear();
        this.secretHolesQueue.clear();
        this.delayedEncryptedChatUpdates.clear();
        this.pendingEncMessagesToDelete.clear();
        this.startingSecretChat = false;
    }

    protected void processPendingEncMessages() {
        if (!this.pendingEncMessagesToDelete.isEmpty()) {
            AndroidUtilities.runOnUIThread(new C06981(new ArrayList(this.pendingEncMessagesToDelete)));
            MessagesStorage.getInstance().markMessagesAsDeletedByRandoms(new ArrayList(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
        }
    }

    private TL_messageService createServiceSecretMessage(EncryptedChat encryptedChat, DecryptedMessageAction decryptedMessage) {
        TL_messageService newMsg = new TL_messageService();
        newMsg.action = new TL_messageEncryptedAction();
        newMsg.action.encryptedAction = decryptedMessage;
        int newMessageId = UserConfig.getNewMessageId();
        newMsg.id = newMessageId;
        newMsg.local_id = newMessageId;
        newMsg.from_id = UserConfig.getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
        newMsg.dialog_id = ((long) encryptedChat.id) << 32;
        newMsg.to_id = new TL_peerUser();
        newMsg.send_state = 1;
        if (encryptedChat.participant_id == UserConfig.getClientUserId()) {
            newMsg.to_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.to_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessage instanceof TL_decryptedMessageActionScreenshotMessages) || (decryptedMessage instanceof TL_decryptedMessageActionSetMessageTTL)) {
            newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
        } else {
            newMsg.date = 0;
        }
        newMsg.random_id = SendMessagesHelper.getInstance().getNextRandomId();
        UserConfig.saveConfig(false);
        ArrayList arr = new ArrayList();
        arr.add(newMsg);
        MessagesStorage.getInstance().putMessages(arr, false, true, true, 0);
        return newMsg;
    }

    public void sendMessagesReadMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionReadMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    protected void processUpdateEncryption(TL_updateEncryption update, ConcurrentHashMap<Integer, User> usersDict) {
        EncryptedChat newChat = update.chat;
        long dialog_id = ((long) newChat.id) << 32;
        EncryptedChat existingChat = MessagesController.getInstance().getEncryptedChatDB(newChat.id);
        if ((newChat instanceof TL_encryptedChatRequested) && existingChat == null) {
            int user_id = newChat.participant_id;
            if (user_id == UserConfig.getClientUserId()) {
                user_id = newChat.admin_id;
            }
            User user = MessagesController.getInstance().getUser(Integer.valueOf(user_id));
            if (user == null) {
                user = (User) usersDict.get(Integer.valueOf(user_id));
            }
            newChat.user_id = user_id;
            TL_dialog dialog = new TL_dialog();
            dialog.id = dialog_id;
            dialog.unread_count = 0;
            dialog.top_message = 0;
            dialog.last_message_date = update.date;
            AndroidUtilities.runOnUIThread(new C06992(dialog, newChat));
            MessagesStorage.getInstance().putEncryptedChat(newChat, user, dialog);
            getInstance().acceptSecretChat(newChat);
        } else if (!(newChat instanceof TL_encryptedChat)) {
            EncryptedChat exist = existingChat;
            if (exist != null) {
                newChat.user_id = exist.user_id;
                newChat.auth_key = exist.auth_key;
                newChat.key_create_date = exist.key_create_date;
                newChat.key_use_count_in = exist.key_use_count_in;
                newChat.key_use_count_out = exist.key_use_count_out;
                newChat.ttl = exist.ttl;
                newChat.seq_in = exist.seq_in;
                newChat.seq_out = exist.seq_out;
            }
            AndroidUtilities.runOnUIThread(new C07003(exist, newChat));
        } else if (existingChat != null && (existingChat instanceof TL_encryptedChatWaiting) && (existingChat.auth_key == null || existingChat.auth_key.length == 1)) {
            newChat.a_or_b = existingChat.a_or_b;
            newChat.user_id = existingChat.user_id;
            getInstance().processAcceptedSecretChat(newChat);
        } else if (existingChat == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(update);
        }
    }

    public void sendMessagesDeleteMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionDeleteMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendClearHistoryMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendNotifyLayerMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if ((encryptedChat instanceof TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            TL_decryptedMessageService reqSend;
            Message message;
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionNotifyLayer();
                reqSend.action.layer = CURRENT_SECRET_CHAT_LAYER;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendRequestKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionRequestKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendAcceptKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionAcceptKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                reqSend.action.g_b = encryptedChat.g_a_or_b;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendCommitKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionCommitKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendAbortKeyMessage(EncryptedChat encryptedChat, Message resendMessage, long excange_id) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionAbortKey();
                reqSend.action.exchange_id = excange_id;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendNoopMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendTTLMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionSetMessageTTL();
                reqSend.action.ttl_seconds = encryptedChat.ttl;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(message, null, false);
                newMsgObj.messageOwner.send_state = 1;
                ArrayList<MessageObject> objArr = new ArrayList();
                objArr.add(newMsgObj);
                MessagesController.getInstance().updateInterfaceWithMessages(message.dialog_id, objArr);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendScreenshotMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            TL_decryptedMessageService reqSend;
            Message message;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                reqSend = new TL_decryptedMessageService();
            } else {
                reqSend = new TL_decryptedMessageService_layer8();
                reqSend.random_bytes = new byte[15];
                Utilities.random.nextBytes(reqSend.random_bytes);
            }
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionScreenshotMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(message, null, false);
                newMsgObj.messageOwner.send_state = 1;
                ArrayList<MessageObject> objArr = new ArrayList();
                objArr.add(newMsgObj);
                MessagesController.getInstance().updateInterfaceWithMessages(message.dialog_id, objArr);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    private void updateMediaPaths(MessageObject newMsgObj, EncryptedFile file, DecryptedMessage decryptedMessage, String originalPath) {
        Message newMsg = newMsgObj.messageOwner;
        if (file == null) {
            return;
        }
        ArrayList arr;
        if ((newMsg.media instanceof TL_messageMediaPhoto) && newMsg.media.photo != null) {
            PhotoSize size = (PhotoSize) newMsg.media.photo.sizes.get(newMsg.media.photo.sizes.size() - 1);
            String fileName = size.location.volume_id + "_" + size.location.local_id;
            size.location = new TL_fileEncryptedLocation();
            size.location.key = decryptedMessage.media.key;
            size.location.iv = decryptedMessage.media.iv;
            size.location.dc_id = file.dc_id;
            size.location.volume_id = file.id;
            size.location.secret = file.access_hash;
            size.location.local_id = file.key_fingerprint;
            String fileName2 = size.location.volume_id + "_" + size.location.local_id;
            new File(FileLoader.getInstance().getDirectory(4), fileName + ".jpg").renameTo(FileLoader.getPathToAttach(size));
            ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, true);
            arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
        } else if ((newMsg.media instanceof TL_messageMediaDocument) && newMsg.media.document != null) {
            Document document = newMsg.media.document;
            newMsg.media.document = new TL_documentEncrypted();
            newMsg.media.document.id = file.id;
            newMsg.media.document.access_hash = file.access_hash;
            newMsg.media.document.date = document.date;
            newMsg.media.document.attributes = document.attributes;
            newMsg.media.document.mime_type = document.mime_type;
            newMsg.media.document.size = file.size;
            newMsg.media.document.key = decryptedMessage.media.key;
            newMsg.media.document.iv = decryptedMessage.media.iv;
            newMsg.media.document.thumb = document.thumb;
            newMsg.media.document.dc_id = file.dc_id;
            newMsg.media.document.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath()) && new File(newMsg.attachPath).renameTo(FileLoader.getPathToAttach(newMsg.media.document))) {
                newMsgObj.mediaExists = newMsgObj.attachPathExists;
                newMsgObj.attachPathExists = false;
                newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            }
            arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
        }
    }

    public static boolean isSecretVisibleMessage(Message message) {
        return (message.action instanceof TL_messageEncryptedAction) && ((message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL));
    }

    public static boolean isSecretInvisibleMessage(Message message) {
        return (!(message.action instanceof TL_messageEncryptedAction) || (message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) ? false : true;
    }

    protected void performSendEncryptedRequest(DecryptedMessage req, Message newMsgObj, EncryptedChat chat, InputEncryptedFile encryptedFile, String originalPath, MessageObject newMsg) {
        if (req != null && chat.auth_key != null && !(chat instanceof TL_encryptedChatRequested) && !(chat instanceof TL_encryptedChatWaiting)) {
            SendMessagesHelper.getInstance().putToSendingMessages(newMsgObj);
            Utilities.stageQueue.postRunnable(new C07044(chat, req, newMsgObj, encryptedFile, newMsg, originalPath));
        }
    }

    private void applyPeerLayer(EncryptedChat chat, int newPeerLayer) {
        int currentPeerLayer = AndroidUtilities.getPeerLayerVersion(chat.layer);
        if (newPeerLayer > currentPeerLayer) {
            if (chat.key_hash.length == 16 && currentPeerLayer >= CURRENT_SECRET_CHAT_LAYER) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    chat.key_hash = key_hash;
                    MessagesStorage.getInstance().updateEncryptedChat(chat);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            chat.layer = AndroidUtilities.setPeerLayerVersion(chat.layer, newPeerLayer);
            MessagesStorage.getInstance().updateEncryptedChatLayer(chat);
            if (currentPeerLayer < CURRENT_SECRET_CHAT_LAYER) {
                sendNotifyLayerMessage(chat, null);
            }
            AndroidUtilities.runOnUIThread(new C07055(chat));
        }
    }

    public Message processDecryptedObject(EncryptedChat chat, EncryptedFile file, int date, long random_id, TLObject object, boolean new_key_used) {
        if (object != null) {
            int from_id = chat.admin_id;
            if (from_id == UserConfig.getClientUserId()) {
                from_id = chat.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(chat.layer) >= 20 && chat.exchange_id == 0 && chat.future_key_fingerprint == 0 && chat.key_use_count_in >= (short) 120) {
                requestNewSecretChatKey(chat);
            }
            if (chat.exchange_id == 0 && chat.future_key_fingerprint != 0 && !new_key_used) {
                chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                chat.future_key_fingerprint = 0;
                MessagesStorage.getInstance().updateEncryptedChat(chat);
            } else if (chat.exchange_id != 0 && new_key_used) {
                chat.key_fingerprint = chat.future_key_fingerprint;
                chat.auth_key = chat.future_auth_key;
                chat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                chat.future_key_fingerprint = 0;
                chat.key_use_count_in = (short) 0;
                chat.key_use_count_out = (short) 0;
                chat.exchange_id = 0;
                MessagesStorage.getInstance().updateEncryptedChat(chat);
            }
            int newMessageId;
            if (object instanceof TL_decryptedMessage) {
                TL_message newMessage;
                TL_decryptedMessage decryptedMessage = (TL_decryptedMessage) object;
                if (AndroidUtilities.getPeerLayerVersion(chat.layer) >= 17) {
                    newMessage = new TL_message_secret();
                    newMessage.ttl = decryptedMessage.ttl;
                    newMessage.entities = decryptedMessage.entities;
                } else {
                    newMessage = new TL_message();
                    newMessage.ttl = chat.ttl;
                }
                newMessage.message = decryptedMessage.message;
                newMessage.date = date;
                newMessageId = UserConfig.getNewMessageId();
                newMessage.id = newMessageId;
                newMessage.local_id = newMessageId;
                UserConfig.saveConfig(false);
                newMessage.from_id = from_id;
                newMessage.to_id = new TL_peerUser();
                newMessage.random_id = random_id;
                newMessage.to_id.user_id = UserConfig.getClientUserId();
                newMessage.unread = true;
                newMessage.flags = 768;
                if (decryptedMessage.via_bot_name != null && decryptedMessage.via_bot_name.length() > 0) {
                    newMessage.via_bot_name = decryptedMessage.via_bot_name;
                    newMessage.flags |= MessagesController.UPDATE_MASK_NEW_MESSAGE;
                }
                newMessage.dialog_id = ((long) chat.id) << 32;
                if (decryptedMessage.reply_to_random_id != 0) {
                    newMessage.reply_to_random_id = decryptedMessage.reply_to_random_id;
                    newMessage.flags |= 8;
                }
                if (decryptedMessage.media == null || (decryptedMessage.media instanceof TL_decryptedMessageMediaEmpty)) {
                    newMessage.media = new TL_messageMediaEmpty();
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaWebPage) {
                    newMessage.media = new TL_messageMediaWebPage();
                    newMessage.media.webpage = new TL_webPageUrlPending();
                    newMessage.media.webpage.url = decryptedMessage.media.url;
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaContact) {
                    newMessage.media = new TL_messageMediaContact();
                    newMessage.media.last_name = decryptedMessage.media.last_name;
                    newMessage.media.first_name = decryptedMessage.media.first_name;
                    newMessage.media.phone_number = decryptedMessage.media.phone_number;
                    newMessage.media.user_id = decryptedMessage.media.user_id;
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaGeoPoint) {
                    newMessage.media = new TL_messageMediaGeo();
                    newMessage.media.geo = new TL_geoPoint();
                    newMessage.media.geo.lat = decryptedMessage.media.lat;
                    newMessage.media.geo._long = decryptedMessage.media._long;
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaPhoto) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaPhoto();
                    newMessage.media.caption = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.photo = new TL_photo();
                    newMessage.media.photo.date = newMessage.date;
                    thumb = ((TL_decryptedMessageMediaPhoto) decryptedMessage.media).thumb;
                    if (thumb != null && thumb.length != 0 && thumb.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                        TL_photoCachedSize small = new TL_photoCachedSize();
                        small.w = decryptedMessage.media.thumb_w;
                        small.h = decryptedMessage.media.thumb_h;
                        small.bytes = thumb;
                        small.type = "s";
                        small.location = new TL_fileLocationUnavailable();
                        newMessage.media.photo.sizes.add(small);
                    }
                    TL_photoSize big = new TL_photoSize();
                    big.w = decryptedMessage.media.f27w;
                    big.h = decryptedMessage.media.f26h;
                    big.type = "x";
                    big.size = file.size;
                    big.location = new TL_fileEncryptedLocation();
                    big.location.key = decryptedMessage.media.key;
                    big.location.iv = decryptedMessage.media.iv;
                    big.location.dc_id = file.dc_id;
                    big.location.volume_id = file.id;
                    big.location.secret = file.access_hash;
                    big.location.local_id = file.key_fingerprint;
                    newMessage.media.photo.sizes.add(big);
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaVideo) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaDocument();
                    newMessage.media.document = new TL_documentEncrypted();
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    newMessage.media.document.dc_id = file.dc_id;
                    newMessage.media.caption = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.document.date = date;
                    newMessage.media.document.size = file.size;
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = MimeTypes.VIDEO_MP4;
                    }
                    thumb = ((TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                    if (thumb == null || thumb.length == 0 || thumb.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                        newMessage.media.document.thumb = new TL_photoSizeEmpty();
                        newMessage.media.document.thumb.type = "s";
                    } else {
                        newMessage.media.document.thumb = new TL_photoCachedSize();
                        newMessage.media.document.thumb.bytes = thumb;
                        newMessage.media.document.thumb.f34w = decryptedMessage.media.thumb_w;
                        newMessage.media.document.thumb.f33h = decryptedMessage.media.thumb_h;
                        newMessage.media.document.thumb.type = "s";
                        newMessage.media.document.thumb.location = new TL_fileLocationUnavailable();
                    }
                    TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                    attributeVideo.w = decryptedMessage.media.f27w;
                    attributeVideo.h = decryptedMessage.media.f26h;
                    attributeVideo.duration = decryptedMessage.media.duration;
                    newMessage.media.document.attributes.add(attributeVideo);
                    if (newMessage.ttl == 0) {
                        return newMessage;
                    }
                    newMessage.ttl = Math.max(decryptedMessage.media.duration + 2, newMessage.ttl);
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaDocument();
                    newMessage.media.caption = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.document = new TL_documentEncrypted();
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.date = date;
                    if (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument_layer8) {
                        TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                        fileName.file_name = decryptedMessage.media.file_name;
                        newMessage.media.document.attributes.add(fileName);
                    } else {
                        newMessage.media.document.attributes = decryptedMessage.media.attributes;
                    }
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    Document document = newMessage.media.document;
                    if (decryptedMessage.media.size != 0) {
                        newMessageId = Math.min(decryptedMessage.media.size, file.size);
                    } else {
                        newMessageId = file.size;
                    }
                    document.size = newMessageId;
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    thumb = ((TL_decryptedMessageMediaDocument) decryptedMessage.media).thumb;
                    if (thumb == null || thumb.length == 0 || thumb.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                        newMessage.media.document.thumb = new TL_photoSizeEmpty();
                        newMessage.media.document.thumb.type = "s";
                    } else {
                        newMessage.media.document.thumb = new TL_photoCachedSize();
                        newMessage.media.document.thumb.bytes = thumb;
                        newMessage.media.document.thumb.f34w = decryptedMessage.media.thumb_w;
                        newMessage.media.document.thumb.f33h = decryptedMessage.media.thumb_h;
                        newMessage.media.document.thumb.type = "s";
                        newMessage.media.document.thumb.location = new TL_fileLocationUnavailable();
                    }
                    newMessage.media.document.dc_id = file.dc_id;
                    if (!MessageObject.isVoiceMessage(newMessage)) {
                        return newMessage;
                    }
                    newMessage.media_unread = true;
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaExternalDocument) {
                    newMessage.media = new TL_messageMediaDocument();
                    newMessage.media.caption = TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.document = new TL_document();
                    newMessage.media.document.id = decryptedMessage.media.id;
                    newMessage.media.document.access_hash = decryptedMessage.media.access_hash;
                    newMessage.media.document.date = decryptedMessage.media.date;
                    newMessage.media.document.attributes = decryptedMessage.media.attributes;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    newMessage.media.document.dc_id = decryptedMessage.media.dc_id;
                    newMessage.media.document.size = decryptedMessage.media.size;
                    newMessage.media.document.thumb = ((TL_decryptedMessageMediaExternalDocument) decryptedMessage.media).thumb;
                    if (newMessage.media.document.mime_type != null) {
                        return newMessage;
                    }
                    newMessage.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                    return newMessage;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaAudio) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaDocument();
                    newMessage.media.document = new TL_documentEncrypted();
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.date = date;
                    newMessage.media.document.size = file.size;
                    newMessage.media.document.dc_id = file.dc_id;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    newMessage.media.document.thumb = new TL_photoSizeEmpty();
                    newMessage.media.document.thumb.type = "s";
                    newMessage.media.caption = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = "audio/ogg";
                    }
                    TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                    attributeAudio.duration = decryptedMessage.media.duration;
                    attributeAudio.voice = true;
                    newMessage.media.document.attributes.add(attributeAudio);
                    if (newMessage.ttl == 0) {
                        return newMessage;
                    }
                    newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                    return newMessage;
                } else if (!(decryptedMessage.media instanceof TL_decryptedMessageMediaVenue)) {
                    return null;
                } else {
                    newMessage.media = new TL_messageMediaVenue();
                    newMessage.media.geo = new TL_geoPoint();
                    newMessage.media.geo.lat = decryptedMessage.media.lat;
                    newMessage.media.geo._long = decryptedMessage.media._long;
                    newMessage.media.title = decryptedMessage.media.title;
                    newMessage.media.address = decryptedMessage.media.address;
                    newMessage.media.provider = decryptedMessage.media.provider;
                    newMessage.media.venue_id = decryptedMessage.media.venue_id;
                    return newMessage;
                }
            } else if (object instanceof TL_decryptedMessageService) {
                TL_decryptedMessageService serviceMessage = (TL_decryptedMessageService) object;
                if ((serviceMessage.action instanceof TL_decryptedMessageActionSetMessageTTL) || (serviceMessage.action instanceof TL_decryptedMessageActionScreenshotMessages)) {
                    Message newMessage2 = new TL_messageService();
                    if (serviceMessage.action instanceof TL_decryptedMessageActionSetMessageTTL) {
                        newMessage2.action = new TL_messageEncryptedAction();
                        if (serviceMessage.action.ttl_seconds < 0 || serviceMessage.action.ttl_seconds > 31536000) {
                            serviceMessage.action.ttl_seconds = 31536000;
                        }
                        chat.ttl = serviceMessage.action.ttl_seconds;
                        newMessage2.action.encryptedAction = serviceMessage.action;
                        MessagesStorage.getInstance().updateEncryptedChatTTL(chat);
                    } else if (serviceMessage.action instanceof TL_decryptedMessageActionScreenshotMessages) {
                        newMessage2.action = new TL_messageEncryptedAction();
                        newMessage2.action.encryptedAction = serviceMessage.action;
                    }
                    newMessageId = UserConfig.getNewMessageId();
                    newMessage2.id = newMessageId;
                    newMessage2.local_id = newMessageId;
                    UserConfig.saveConfig(false);
                    newMessage2.unread = true;
                    newMessage2.flags = MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                    newMessage2.date = date;
                    newMessage2.from_id = from_id;
                    newMessage2.to_id = new TL_peerUser();
                    newMessage2.to_id.user_id = UserConfig.getClientUserId();
                    newMessage2.dialog_id = ((long) chat.id) << 32;
                    return newMessage2;
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionFlushHistory) {
                    AndroidUtilities.runOnUIThread(new C07086(((long) chat.id) << 32));
                    return null;
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionDeleteMessages) {
                    if (!serviceMessage.action.random_ids.isEmpty()) {
                        this.pendingEncMessagesToDelete.addAll(serviceMessage.action.random_ids);
                    }
                    return null;
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionReadMessages) {
                    if (!serviceMessage.action.random_ids.isEmpty()) {
                        int time = ConnectionsManager.getInstance().getCurrentTime();
                        MessagesStorage.getInstance().createTaskForSecretChat(chat.id, time, time, 1, serviceMessage.action.random_ids);
                    }
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionNotifyLayer) {
                    applyPeerLayer(chat, serviceMessage.action.layer);
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionRequestKey) {
                    if (chat.exchange_id != 0) {
                        if (chat.exchange_id > serviceMessage.action.exchange_id) {
                            FileLog.m11e("tmessages", "we already have request key with higher exchange_id");
                            return null;
                        }
                        sendAbortKeyMessage(chat, null, chat.exchange_id);
                    }
                    byte[] salt = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                    Utilities.random.nextBytes(salt);
                    r0 = new BigInteger(1, MessagesStorage.secretPBytes);
                    BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), r0);
                    r0 = new BigInteger(1, serviceMessage.action.g_a);
                    if (Utilities.isGoodGaAndGb(r0, r0)) {
                        byte[] g_b_bytes = g_b.toByteArray();
                        if (g_b_bytes.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                            correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                            System.arraycopy(g_b_bytes, 1, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                            g_b_bytes = correctedAuth;
                        }
                        authKey = r0.modPow(new BigInteger(1, salt), r0).toByteArray();
                        if (authKey.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                            correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                            System.arraycopy(authKey, authKey.length + InputDeviceCompat.SOURCE_ANY, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                            authKey = correctedAuth;
                        } else if (authKey.length < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                            correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                            System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                            for (a = 0; a < 256 - authKey.length; a++) {
                                authKey[a] = (byte) 0;
                            }
                            authKey = correctedAuth;
                        }
                        authKeyHash = Utilities.computeSHA1(authKey);
                        authKeyId = new byte[8];
                        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                        chat.exchange_id = serviceMessage.action.exchange_id;
                        chat.future_auth_key = authKey;
                        chat.future_key_fingerprint = Utilities.bytesToLong(authKeyId);
                        chat.g_a_or_b = g_b_bytes;
                        MessagesStorage.getInstance().updateEncryptedChat(chat);
                        sendAcceptKeyMessage(chat, null);
                    } else {
                        sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                        return null;
                    }
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionAcceptKey) {
                    if (chat.exchange_id == serviceMessage.action.exchange_id) {
                        r0 = new BigInteger(1, MessagesStorage.secretPBytes);
                        r0 = new BigInteger(1, serviceMessage.action.g_b);
                        if (Utilities.isGoodGaAndGb(r0, r0)) {
                            authKey = r0.modPow(new BigInteger(1, chat.a_or_b), r0).toByteArray();
                            if (authKey.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                                correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                                System.arraycopy(authKey, authKey.length + InputDeviceCompat.SOURCE_ANY, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                                authKey = correctedAuth;
                            } else if (authKey.length < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                                correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                                System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                                for (a = 0; a < 256 - authKey.length; a++) {
                                    authKey[a] = (byte) 0;
                                }
                                authKey = correctedAuth;
                            }
                            authKeyHash = Utilities.computeSHA1(authKey);
                            authKeyId = new byte[8];
                            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                            long fingerprint = Utilities.bytesToLong(authKeyId);
                            if (serviceMessage.action.key_fingerprint == fingerprint) {
                                chat.future_auth_key = authKey;
                                chat.future_key_fingerprint = fingerprint;
                                MessagesStorage.getInstance().updateEncryptedChat(chat);
                                sendCommitKeyMessage(chat, null);
                            } else {
                                chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                                chat.future_key_fingerprint = 0;
                                chat.exchange_id = 0;
                                MessagesStorage.getInstance().updateEncryptedChat(chat);
                                sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                            }
                        } else {
                            chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                            chat.future_key_fingerprint = 0;
                            chat.exchange_id = 0;
                            MessagesStorage.getInstance().updateEncryptedChat(chat);
                            sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                            return null;
                        }
                    }
                    chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                    chat.future_key_fingerprint = 0;
                    chat.exchange_id = 0;
                    MessagesStorage.getInstance().updateEncryptedChat(chat);
                    sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionCommitKey) {
                    if (chat.exchange_id == serviceMessage.action.exchange_id && chat.future_key_fingerprint == serviceMessage.action.key_fingerprint) {
                        long old_fingerpring = chat.key_fingerprint;
                        byte[] old_key = chat.auth_key;
                        chat.key_fingerprint = chat.future_key_fingerprint;
                        chat.auth_key = chat.future_auth_key;
                        chat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                        chat.future_auth_key = old_key;
                        chat.future_key_fingerprint = old_fingerpring;
                        chat.key_use_count_in = (short) 0;
                        chat.key_use_count_out = (short) 0;
                        chat.exchange_id = 0;
                        MessagesStorage.getInstance().updateEncryptedChat(chat);
                        sendNoopMessage(chat, null);
                    } else {
                        chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                        chat.future_key_fingerprint = 0;
                        chat.exchange_id = 0;
                        MessagesStorage.getInstance().updateEncryptedChat(chat);
                        sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                    }
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionAbortKey) {
                    if (chat.exchange_id == serviceMessage.action.exchange_id) {
                        chat.future_auth_key = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                        chat.future_key_fingerprint = 0;
                        chat.exchange_id = 0;
                        MessagesStorage.getInstance().updateEncryptedChat(chat);
                    }
                } else if (!(serviceMessage.action instanceof TL_decryptedMessageActionNoop)) {
                    if (!(serviceMessage.action instanceof TL_decryptedMessageActionResend)) {
                        return null;
                    }
                    EncryptedChat newChat = new TL_encryptedChatDiscarded();
                    newChat.id = chat.id;
                    newChat.user_id = chat.user_id;
                    newChat.auth_key = chat.auth_key;
                    newChat.key_create_date = chat.key_create_date;
                    newChat.key_use_count_in = chat.key_use_count_in;
                    newChat.key_use_count_out = chat.key_use_count_out;
                    newChat.seq_in = chat.seq_in;
                    newChat.seq_out = chat.seq_out;
                    MessagesStorage.getInstance().updateEncryptedChat(newChat);
                    AndroidUtilities.runOnUIThread(new C07097(newChat));
                    declineSecretChat(chat.id);
                }
            } else {
                FileLog.m11e("tmessages", "unknown message " + object);
            }
        } else {
            FileLog.m11e("tmessages", "unknown TLObject");
        }
        return null;
    }

    public void checkSecretHoles(EncryptedChat chat, ArrayList<Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = (ArrayList) this.secretHolesQueue.get(Integer.valueOf(chat.id));
        if (holes != null) {
            Collections.sort(holes, new C07108());
            boolean update = false;
            int a = 0;
            while (holes.size() > 0) {
                TL_decryptedMessageHolder holder = (TL_decryptedMessageHolder) holes.get(a);
                if (holder.layer.out_seq_no != chat.seq_in && chat.seq_in != holder.layer.out_seq_no - 2) {
                    break;
                }
                applyPeerLayer(chat, holder.layer.layer);
                chat.seq_in = holder.layer.out_seq_no;
                holes.remove(a);
                a--;
                update = true;
                Message message = processDecryptedObject(chat, holder.file, holder.date, holder.random_id, holder.layer.message, holder.new_key_used);
                if (message != null) {
                    messages.add(message);
                }
                a++;
            }
            if (holes.isEmpty()) {
                this.secretHolesQueue.remove(Integer.valueOf(chat.id));
            }
            if (update) {
                MessagesStorage.getInstance().updateEncryptedChatSeq(chat);
            }
        }
    }

    protected ArrayList<Message> decryptMessage(EncryptedMessage message) {
        EncryptedChat chat = MessagesController.getInstance().getEncryptedChatDB(message.chat_id);
        if (chat == null || (chat instanceof TL_encryptedChatDiscarded)) {
            return null;
        }
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.bytes.length);
            nativeByteBuffer.writeBytes(message.bytes);
            nativeByteBuffer.position(0);
            long fingerprint = nativeByteBuffer.readInt64(false);
            byte[] keyToDecrypt = null;
            boolean new_key_used = false;
            if (chat.key_fingerprint == fingerprint) {
                keyToDecrypt = chat.auth_key;
            } else if (chat.future_key_fingerprint != 0 && chat.future_key_fingerprint == fingerprint) {
                keyToDecrypt = chat.future_auth_key;
                new_key_used = true;
            }
            if (keyToDecrypt != null) {
                byte[] messageKey = nativeByteBuffer.readData(16, false);
                MessageKeyData keyData = MessageKeyData.generateMessageKeyData(keyToDecrypt, messageKey, false);
                Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, false, false, 24, nativeByteBuffer.limit() - 24);
                int len = nativeByteBuffer.readInt32(false);
                if (len < 0 || len > nativeByteBuffer.limit() - 28) {
                    return null;
                }
                byte[] messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer, 24, Math.min((len + 4) + 24, nativeByteBuffer.buffer.limit()));
                if (!Utilities.arraysEquals(messageKey, 0, messageKeyFull, messageKeyFull.length - 16)) {
                    return null;
                }
                TLObject object = TLClassStore.Instance().TLdeserialize(nativeByteBuffer, nativeByteBuffer.readInt32(false), false);
                nativeByteBuffer.reuse();
                if (!new_key_used && AndroidUtilities.getPeerLayerVersion(chat.layer) >= 20) {
                    chat.key_use_count_in = (short) (chat.key_use_count_in + 1);
                }
                if (object instanceof TL_decryptedMessageLayer) {
                    TL_decryptedMessageLayer layer = (TL_decryptedMessageLayer) object;
                    if (chat.seq_in == 0 && chat.seq_out == 0) {
                        if (chat.admin_id == UserConfig.getClientUserId()) {
                            chat.seq_out = 1;
                        } else {
                            chat.seq_in = 1;
                        }
                    }
                    if (layer.random_bytes.length < 15) {
                        FileLog.m11e("tmessages", "got random bytes less than needed");
                        return null;
                    }
                    FileLog.m11e("tmessages", "current chat in_seq = " + chat.seq_in + " out_seq = " + chat.seq_out);
                    FileLog.m11e("tmessages", "got message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
                    if (layer.out_seq_no < chat.seq_in) {
                        return null;
                    }
                    if (chat.seq_in == layer.out_seq_no || chat.seq_in == layer.out_seq_no - 2) {
                        applyPeerLayer(chat, layer.layer);
                        chat.seq_in = layer.out_seq_no;
                        MessagesStorage.getInstance().updateEncryptedChatSeq(chat);
                        object = layer.message;
                    } else {
                        FileLog.m11e("tmessages", "got hole");
                        ArrayList<TL_decryptedMessageHolder> arr = (ArrayList) this.secretHolesQueue.get(Integer.valueOf(chat.id));
                        if (arr == null) {
                            arr = new ArrayList();
                            this.secretHolesQueue.put(Integer.valueOf(chat.id), arr);
                        }
                        if (arr.size() >= 4) {
                            this.secretHolesQueue.remove(Integer.valueOf(chat.id));
                            TL_encryptedChatDiscarded newChat = new TL_encryptedChatDiscarded();
                            newChat.id = chat.id;
                            newChat.user_id = chat.user_id;
                            newChat.auth_key = chat.auth_key;
                            newChat.key_create_date = chat.key_create_date;
                            newChat.key_use_count_in = chat.key_use_count_in;
                            newChat.key_use_count_out = chat.key_use_count_out;
                            newChat.seq_in = chat.seq_in;
                            newChat.seq_out = chat.seq_out;
                            AndroidUtilities.runOnUIThread(new C07119(newChat));
                            declineSecretChat(chat.id);
                            return null;
                        }
                        TL_decryptedMessageHolder holder = new TL_decryptedMessageHolder();
                        holder.layer = layer;
                        holder.file = message.file;
                        holder.random_id = message.random_id;
                        holder.date = message.date;
                        holder.new_key_used = new_key_used;
                        arr.add(holder);
                        return null;
                    }
                }
                ArrayList<Message> messages = new ArrayList();
                Message decryptedMessage = processDecryptedObject(chat, message.file, message.date, message.random_id, object, new_key_used);
                if (decryptedMessage != null) {
                    messages.add(decryptedMessage);
                }
                checkSecretHoles(chat, messages);
                return messages;
            }
            nativeByteBuffer.reuse();
            FileLog.m11e("tmessages", "fingerprint mismatch " + fingerprint);
            return null;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void requestNewSecretChatKey(EncryptedChat encryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
            byte[] salt = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
            Utilities.random.nextBytes(salt);
            byte[] g_a = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
            if (g_a.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                byte[] correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                System.arraycopy(g_a, 1, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                g_a = correctedAuth;
            }
            encryptedChat.exchange_id = SendMessagesHelper.getInstance().getNextRandomId();
            encryptedChat.a_or_b = salt;
            encryptedChat.g_a = g_a;
            MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
            sendRequestKeyMessage(encryptedChat, null);
        }
    }

    public void processAcceptedSecretChat(EncryptedChat encryptedChat) {
        BigInteger p = new BigInteger(1, MessagesStorage.secretPBytes);
        BigInteger i_authKey = new BigInteger(1, encryptedChat.g_a_or_b);
        if (Utilities.isGoodGaAndGb(i_authKey, p)) {
            byte[] authKey = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), p).toByteArray();
            byte[] correctedAuth;
            if (authKey.length > MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                System.arraycopy(authKey, authKey.length + InputDeviceCompat.SOURCE_ANY, correctedAuth, 0, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                authKey = correctedAuth;
            } else if (authKey.length < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) {
                correctedAuth = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
                System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                for (int a = 0; a < 256 - authKey.length; a++) {
                    authKey[a] = (byte) 0;
                }
                authKey = correctedAuth;
            }
            byte[] authKeyHash = Utilities.computeSHA1(authKey);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            if (encryptedChat.key_fingerprint == Utilities.bytesToLong(authKeyId)) {
                encryptedChat.auth_key = authKey;
                encryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                encryptedChat.seq_in = 0;
                encryptedChat.seq_out = 1;
                MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                AndroidUtilities.runOnUIThread(new AnonymousClass10(encryptedChat));
                return;
            }
            TL_encryptedChatDiscarded newChat = new TL_encryptedChatDiscarded();
            newChat.id = encryptedChat.id;
            newChat.user_id = encryptedChat.user_id;
            newChat.auth_key = encryptedChat.auth_key;
            newChat.key_create_date = encryptedChat.key_create_date;
            newChat.key_use_count_in = encryptedChat.key_use_count_in;
            newChat.key_use_count_out = encryptedChat.key_use_count_out;
            newChat.seq_in = encryptedChat.seq_in;
            newChat.seq_out = encryptedChat.seq_out;
            MessagesStorage.getInstance().updateEncryptedChat(newChat);
            AndroidUtilities.runOnUIThread(new AnonymousClass11(newChat));
            declineSecretChat(encryptedChat.id);
            return;
        }
        declineSecretChat(encryptedChat.id);
    }

    public void declineSecretChat(int chat_id) {
        TL_messages_discardEncryption req = new TL_messages_discardEncryption();
        req.chat_id = chat_id;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void acceptSecretChat(EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(Integer.valueOf(encryptedChat.id)) == null) {
            this.acceptingChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            TL_messages_getDhConfig req = new TL_messages_getDhConfig();
            req.random_length = MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
            req.version = MessagesStorage.lastSecretVersion;
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass13(encryptedChat));
        }
    }

    public void startSecretChat(Context context, User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            TL_messages_getDhConfig req = new TL_messages_getDhConfig();
            req.random_length = MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
            req.version = MessagesStorage.lastSecretVersion;
            progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new AnonymousClass15(ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass14(context, progressDialog, user), 2)));
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }
}
