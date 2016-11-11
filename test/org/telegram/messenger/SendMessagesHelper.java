package org.telegram.messenger;

import android.app.AlertDialog.Builder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageText;
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
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio_old;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.VideoPlayer;

public class SendMessagesHelper implements NotificationCenterDelegate {
    private static volatile SendMessagesHelper Instance;
    private ChatFull currentChatInfo;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages;
    private LocationProvider locationProvider;
    private HashMap<Integer, Message> sendingMessages;
    private HashMap<Integer, MessageObject> unsentMessages;
    private HashMap<String, MessageObject> waitingForCallback;
    private HashMap<String, MessageObject> waitingForLocation;

    /* renamed from: org.telegram.messenger.SendMessagesHelper.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ ArrayList val$encryptedChats;
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ ArrayList val$users;

        AnonymousClass10(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
            this.val$users = arrayList;
            this.val$chats = arrayList2;
            this.val$encryptedChats = arrayList3;
            this.val$messages = arrayList4;
        }

        public void run() {
            MessagesController.getInstance().putUsers(this.val$users, true);
            MessagesController.getInstance().putChats(this.val$chats, true);
            MessagesController.getInstance().putEncryptedChats(this.val$encryptedChats, true);
            for (int a = 0; a < this.val$messages.size(); a++) {
                SendMessagesHelper.this.retrySendMessage(new MessageObject((Message) this.val$messages.get(a), null, false), true);
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.11 */
    static class AnonymousClass11 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ TL_document val$documentFinal;
        final /* synthetic */ HashMap val$params;
        final /* synthetic */ String val$pathFinal;
        final /* synthetic */ MessageObject val$reply_to_msg;

        AnonymousClass11(TL_document tL_document, String str, long j, MessageObject messageObject, HashMap hashMap) {
            this.val$documentFinal = tL_document;
            this.val$pathFinal = str;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
            this.val$params = hashMap;
        }

        public void run() {
            SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$pathFinal, this.val$dialog_id, this.val$reply_to_msg, null, this.val$params);
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.12 */
    static class AnonymousClass12 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$messageObjects;
        final /* synthetic */ MessageObject val$reply_to_msg;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.12.1 */
        class C07121 implements Runnable {
            final /* synthetic */ TL_document val$documentFinal;
            final /* synthetic */ MessageObject val$messageObject;
            final /* synthetic */ HashMap val$params;

            C07121(TL_document tL_document, MessageObject messageObject, HashMap hashMap) {
                this.val$documentFinal = tL_document;
                this.val$messageObject = messageObject;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$messageObject.messageOwner.attachPath, AnonymousClass12.this.val$dialog_id, AnonymousClass12.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass12(ArrayList arrayList, long j, MessageObject messageObject) {
            this.val$messageObjects = arrayList;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            int size = this.val$messageObjects.size();
            for (int a = 0; a < size; a++) {
                MessageObject messageObject = (MessageObject) this.val$messageObjects.get(a);
                String originalPath = messageObject.messageOwner.attachPath;
                File f = new File(originalPath);
                boolean isEncrypted = ((int) this.val$dialog_id) == 0;
                if (originalPath != null) {
                    originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + f.length();
                }
                TL_document tL_document = null;
                if (!isEncrypted) {
                    tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 1 : 4);
                }
                if (tL_document == null) {
                    tL_document = messageObject.messageOwner.media.document;
                }
                if (isEncrypted) {
                    EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.val$dialog_id >> 32)));
                    if (encryptedChat != null) {
                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) < 46) {
                            for (int b = 0; b < tL_document.attributes.size(); b++) {
                                if (tL_document.attributes.get(b) instanceof TL_documentAttributeAudio) {
                                    TL_documentAttributeAudio_old old = new TL_documentAttributeAudio_old();
                                    old.duration = ((DocumentAttribute) tL_document.attributes.get(b)).duration;
                                    tL_document.attributes.remove(b);
                                    tL_document.attributes.add(old);
                                    break;
                                }
                            }
                        }
                    } else {
                        return;
                    }
                }
                HashMap<String, String> params = new HashMap();
                if (originalPath != null) {
                    params.put("originalPath", originalPath);
                }
                AndroidUtilities.runOnUIThread(new C07121(tL_document, messageObject, params));
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.13 */
    static class AnonymousClass13 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ String val$mime;
        final /* synthetic */ ArrayList val$originalPaths;
        final /* synthetic */ ArrayList val$paths;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ ArrayList val$uris;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.13.1 */
        class C07131 implements Runnable {
            C07131() {
            }

            public void run() {
                try {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", C0691R.string.UnsupportedAttachment), 0).show();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        AnonymousClass13(ArrayList arrayList, ArrayList arrayList2, String str, long j, MessageObject messageObject, ArrayList arrayList3) {
            this.val$paths = arrayList;
            this.val$originalPaths = arrayList2;
            this.val$mime = str;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
            this.val$uris = arrayList3;
        }

        public void run() {
            int a;
            boolean error = false;
            if (this.val$paths != null) {
                for (a = 0; a < this.val$paths.size(); a++) {
                    if (!SendMessagesHelper.prepareSendingDocumentInternal((String) this.val$paths.get(a), (String) this.val$originalPaths.get(a), null, this.val$mime, this.val$dialog_id, this.val$reply_to_msg, null)) {
                        error = true;
                    }
                }
            }
            if (this.val$uris != null) {
                for (a = 0; a < this.val$uris.size(); a++) {
                    if (!SendMessagesHelper.prepareSendingDocumentInternal(null, null, (Uri) this.val$uris.get(a), this.val$mime, this.val$dialog_id, this.val$reply_to_msg, null)) {
                        error = true;
                    }
                }
            }
            if (error) {
                AndroidUtilities.runOnUIThread(new C07131());
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.14 */
    static class AnonymousClass14 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ HashMap val$params;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ BotInlineResult val$result;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.14.1 */
        class C07141 implements Runnable {
            final /* synthetic */ TL_document val$finalDocument;
            final /* synthetic */ String val$finalPathFinal;
            final /* synthetic */ TL_photo val$finalPhoto;

            C07141(TL_document tL_document, String str, TL_photo tL_photo) {
                this.val$finalDocument = tL_document;
                this.val$finalPathFinal = str;
                this.val$finalPhoto = tL_photo;
            }

            public void run() {
                if (this.val$finalDocument != null) {
                    this.val$finalDocument.caption = AnonymousClass14.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalDocument, null, this.val$finalPathFinal, AnonymousClass14.this.val$dialog_id, AnonymousClass14.this.val$reply_to_msg, AnonymousClass14.this.val$result.send_message.reply_markup, AnonymousClass14.this.val$params);
                } else if (this.val$finalPhoto != null) {
                    this.val$finalPhoto.caption = AnonymousClass14.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalPhoto, AnonymousClass14.this.val$result.content_url, AnonymousClass14.this.val$dialog_id, AnonymousClass14.this.val$reply_to_msg, AnonymousClass14.this.val$result.send_message.reply_markup, AnonymousClass14.this.val$params);
                }
            }
        }

        AnonymousClass14(BotInlineResult botInlineResult, HashMap hashMap, long j, MessageObject messageObject) {
            this.val$result = botInlineResult;
            this.val$params = hashMap;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            String finalPath = null;
            TL_document document = null;
            TL_photo photo = null;
            if (!(this.val$result instanceof TL_botInlineMediaResult)) {
                if (this.val$result.content_url != null) {
                    File f = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.content_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.content_url, "file"));
                    if (f.exists()) {
                        finalPath = f.getAbsolutePath();
                    } else {
                        finalPath = this.val$result.content_url;
                    }
                    String str = this.val$result.type;
                    Object obj = -1;
                    switch (str.hashCode()) {
                        case -1890252483:
                            if (str.equals("sticker")) {
                                obj = 4;
                                break;
                            }
                            break;
                        case 102340:
                            if (str.equals("gif")) {
                                obj = 5;
                                break;
                            }
                            break;
                        case 3143036:
                            if (str.equals("file")) {
                                obj = 2;
                                break;
                            }
                            break;
                        case 93166550:
                            if (str.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                                obj = null;
                                break;
                            }
                            break;
                        case 106642994:
                            if (str.equals("photo")) {
                                obj = 6;
                                break;
                            }
                            break;
                        case 112202875:
                            if (str.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                                obj = 3;
                                break;
                            }
                            break;
                        case 112386354:
                            if (str.equals("voice")) {
                                obj = 1;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                        case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                        case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                        case VideoPlayer.STATE_BUFFERING /*3*/:
                        case VideoPlayer.STATE_READY /*4*/:
                        case VideoPlayer.STATE_ENDED /*5*/:
                            String str2;
                            document = new TL_document();
                            document.id = 0;
                            document.size = 0;
                            document.dc_id = 0;
                            document.mime_type = this.val$result.content_type;
                            document.date = ConnectionsManager.getInstance().getCurrentTime();
                            TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                            document.attributes.add(fileName);
                            str = this.val$result.type;
                            obj = -1;
                            switch (str.hashCode()) {
                                case -1890252483:
                                    if (str.equals("sticker")) {
                                        obj = 5;
                                        break;
                                    }
                                    break;
                                case 102340:
                                    if (str.equals("gif")) {
                                        obj = null;
                                        break;
                                    }
                                    break;
                                case 3143036:
                                    if (str.equals("file")) {
                                        obj = 3;
                                        break;
                                    }
                                    break;
                                case 93166550:
                                    if (str.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                                        obj = 2;
                                        break;
                                    }
                                    break;
                                case 112202875:
                                    if (str.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                                        obj = 4;
                                        break;
                                    }
                                    break;
                                case 112386354:
                                    if (str.equals("voice")) {
                                        obj = 1;
                                        break;
                                    }
                                    break;
                            }
                            Bitmap bitmap;
                            TL_documentAttributeAudio audio;
                            switch (obj) {
                                case VideoPlayer.TRACK_DEFAULT /*0*/:
                                    fileName.file_name = "animation.gif";
                                    if (finalPath.endsWith("mp4")) {
                                        document.mime_type = MimeTypes.VIDEO_MP4;
                                        document.attributes.add(new TL_documentAttributeAnimated());
                                    } else {
                                        document.mime_type = "image/gif";
                                    }
                                    try {
                                        if (finalPath.endsWith("mp4")) {
                                            bitmap = ThumbnailUtils.createVideoThumbnail(finalPath, 1);
                                        } else {
                                            bitmap = ImageLoader.loadBitmap(finalPath, null, 90.0f, 90.0f, true);
                                        }
                                        if (bitmap != null) {
                                            document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            bitmap.recycle();
                                            break;
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m13e("tmessages", e);
                                        break;
                                    }
                                    break;
                                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                                    audio = new TL_documentAttributeAudio();
                                    audio.duration = this.val$result.duration;
                                    audio.voice = true;
                                    fileName.file_name = "audio.ogg";
                                    document.attributes.add(audio);
                                    document.thumb = new TL_photoSizeEmpty();
                                    str2 = "s";
                                    document.thumb.type = str;
                                    break;
                                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                                    audio = new TL_documentAttributeAudio();
                                    audio.duration = this.val$result.duration;
                                    audio.title = this.val$result.title;
                                    audio.flags |= 1;
                                    if (this.val$result.description != null) {
                                        audio.performer = this.val$result.description;
                                        audio.flags |= 2;
                                    }
                                    fileName.file_name = "audio.mp3";
                                    document.attributes.add(audio);
                                    document.thumb = new TL_photoSizeEmpty();
                                    str2 = "s";
                                    document.thumb.type = str;
                                    break;
                                case VideoPlayer.STATE_BUFFERING /*3*/:
                                    int idx = this.val$result.content_type.indexOf(47);
                                    if (idx == -1) {
                                        fileName.file_name = "file";
                                        break;
                                    }
                                    fileName.file_name = "file." + this.val$result.content_type.substring(idx + 1);
                                    break;
                                case VideoPlayer.STATE_READY /*4*/:
                                    fileName.file_name = "video.mp4";
                                    TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                                    attributeVideo.w = this.val$result.f25w;
                                    attributeVideo.h = this.val$result.f24h;
                                    attributeVideo.duration = this.val$result.duration;
                                    document.attributes.add(attributeVideo);
                                    try {
                                        bitmap = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "jpg")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (bitmap != null) {
                                            document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            bitmap.recycle();
                                            break;
                                        }
                                    } catch (Throwable e2) {
                                        FileLog.m13e("tmessages", e2);
                                        break;
                                    }
                                    break;
                                case VideoPlayer.STATE_ENDED /*5*/:
                                    TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                                    attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                                    attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                    document.attributes.add(attributeSticker);
                                    TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
                                    attributeImageSize.w = this.val$result.f25w;
                                    attributeImageSize.h = this.val$result.f24h;
                                    document.attributes.add(attributeImageSize);
                                    fileName.file_name = "sticker.webp";
                                    try {
                                        bitmap = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "webp")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (bitmap != null) {
                                            document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            bitmap.recycle();
                                            break;
                                        }
                                    } catch (Throwable e22) {
                                        FileLog.m13e("tmessages", e22);
                                        break;
                                    }
                                    break;
                            }
                            if (fileName.file_name == null) {
                                fileName.file_name = "file";
                            }
                            if (document.mime_type == null) {
                                document.mime_type = "application/octet-stream";
                            }
                            if (document.thumb == null) {
                                document.thumb = new TL_photoSize();
                                PhotoSize photoSize = document.thumb;
                                photoSize.f34w = this.val$result.f25w;
                                photoSize = document.thumb;
                                photoSize.f33h = this.val$result.f24h;
                                document.thumb.size = 0;
                                photoSize = document.thumb;
                                photoSize.location = new TL_fileLocationUnavailable();
                                str2 = "x";
                                document.thumb.type = str;
                                break;
                            }
                            break;
                        case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                            if (f.exists()) {
                                photo = SendMessagesHelper.getInstance().generatePhotoSizes(finalPath, null);
                            }
                            if (photo == null) {
                                photo = new TL_photo();
                                photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                TL_photoSize photoSize2 = new TL_photoSize();
                                photoSize2.w = this.val$result.f25w;
                                photoSize2.h = this.val$result.f24h;
                                photoSize2.size = 1;
                                photoSize2.location = new TL_fileLocationUnavailable();
                                photoSize2.type = "x";
                                photo.sizes.add(photoSize2);
                                break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            if (this.val$result.document != null) {
                if (this.val$result.document instanceof TL_document) {
                    document = this.val$result.document;
                }
            } else {
                if (this.val$result.photo != null) {
                    if (this.val$result.photo instanceof TL_photo) {
                        photo = (TL_photo) this.val$result.photo;
                    }
                }
            }
            String finalPathFinal = finalPath;
            TL_document finalDocument = document;
            TL_photo finalPhoto = photo;
            if (this.val$params != null) {
                if (this.val$result.content_url != null) {
                    this.val$params.put("originalPath", this.val$result.content_url);
                }
            }
            AndroidUtilities.runOnUIThread(new C07141(finalDocument, finalPathFinal, finalPhoto));
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.15 */
    static class AnonymousClass15 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$photos;
        final /* synthetic */ MessageObject val$reply_to_msg;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.15.1 */
        class C07151 implements Runnable {
            final /* synthetic */ TL_document val$documentFinal;
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ String val$pathFinal;

            C07151(TL_document tL_document, String str, HashMap hashMap) {
                this.val$documentFinal = tL_document;
                this.val$pathFinal = str;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$pathFinal, AnonymousClass15.this.val$dialog_id, AnonymousClass15.this.val$reply_to_msg, null, this.val$params);
            }
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper.15.2 */
        class C07162 implements Runnable {
            final /* synthetic */ boolean val$needDownloadHttpFinal;
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ TL_photo val$photoFinal;
            final /* synthetic */ SearchImage val$searchImage;

            C07162(TL_photo tL_photo, boolean z, SearchImage searchImage, HashMap hashMap) {
                this.val$photoFinal = tL_photo;
                this.val$needDownloadHttpFinal = z;
                this.val$searchImage = searchImage;
                this.val$params = hashMap;
            }

            public void run() {
                String str;
                SendMessagesHelper instance = SendMessagesHelper.getInstance();
                TL_photo tL_photo = this.val$photoFinal;
                if (this.val$needDownloadHttpFinal) {
                    str = this.val$searchImage.imageUrl;
                } else {
                    str = null;
                }
                instance.sendMessage(tL_photo, str, AnonymousClass15.this.val$dialog_id, AnonymousClass15.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass15(long j, ArrayList arrayList, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$photos = arrayList;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            boolean isEncrypted = ((int) this.val$dialog_id) == 0;
            for (int a = 0; a < this.val$photos.size(); a++) {
                SearchImage searchImage = (SearchImage) this.val$photos.get(a);
                HashMap<String, String> params;
                File cacheFile;
                if (searchImage.type == 1) {
                    params = new HashMap();
                    TL_document tL_document = null;
                    if (searchImage.document instanceof TL_document) {
                        tL_document = searchImage.document;
                        cacheFile = FileLoader.getPathToAttach(tL_document, true);
                    } else {
                        if (!isEncrypted) {
                            Document doc = (Document) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !isEncrypted ? 1 : 4);
                            if (doc instanceof TL_document) {
                                tL_document = (TL_document) doc;
                            }
                        }
                        cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                    }
                    if (tL_document == null) {
                        if (searchImage.localUrl != null) {
                            params.put(UpdateFragment.FRAGMENT_URL, searchImage.localUrl);
                        }
                        File thumbFile = null;
                        tL_document = new TL_document();
                        tL_document.id = 0;
                        tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
                        TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                        fileName.file_name = "animation.gif";
                        tL_document.attributes.add(fileName);
                        tL_document.size = searchImage.size;
                        tL_document.dc_id = 0;
                        if (cacheFile.toString().endsWith("mp4")) {
                            tL_document.mime_type = MimeTypes.VIDEO_MP4;
                            tL_document.attributes.add(new TL_documentAttributeAnimated());
                        } else {
                            tL_document.mime_type = "image/gif";
                        }
                        if (cacheFile.exists()) {
                            thumbFile = cacheFile;
                        } else {
                            cacheFile = null;
                        }
                        if (thumbFile == null) {
                            File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg"));
                            if (!file.exists()) {
                                thumbFile = null;
                            }
                        }
                        if (thumbFile != null) {
                            try {
                                Bitmap bitmap;
                                if (thumbFile.getAbsolutePath().endsWith("mp4")) {
                                    bitmap = ThumbnailUtils.createVideoThumbnail(thumbFile.getAbsolutePath(), 1);
                                } else {
                                    bitmap = ImageLoader.loadBitmap(thumbFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                                }
                                if (bitmap != null) {
                                    tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                                    bitmap.recycle();
                                }
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                        }
                        if (tL_document.thumb == null) {
                            tL_document.thumb = new TL_photoSize();
                            tL_document.thumb.f34w = searchImage.width;
                            tL_document.thumb.f33h = searchImage.height;
                            tL_document.thumb.size = 0;
                            tL_document.thumb.location = new TL_fileLocationUnavailable();
                            tL_document.thumb.type = "x";
                        }
                    }
                    if (searchImage.caption != null) {
                        tL_document.caption = searchImage.caption.toString();
                    }
                    TL_document documentFinal = tL_document;
                    String originalPathFinal = searchImage.imageUrl;
                    String pathFinal = cacheFile == null ? searchImage.imageUrl : cacheFile.toString();
                    if (!(params == null || searchImage.imageUrl == null)) {
                        params.put("originalPath", searchImage.imageUrl);
                    }
                    AndroidUtilities.runOnUIThread(new C07151(documentFinal, pathFinal, params));
                } else {
                    boolean needDownloadHttp = true;
                    TL_photo tL_photo = null;
                    if (!isEncrypted) {
                        tL_photo = (TL_photo) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !isEncrypted ? 0 : 3);
                    }
                    if (tL_photo == null) {
                        cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                        if (cacheFile.exists() && cacheFile.length() != 0) {
                            tL_photo = SendMessagesHelper.getInstance().generatePhotoSizes(cacheFile.toString(), null);
                            if (tL_photo != null) {
                                needDownloadHttp = false;
                            }
                        }
                        if (tL_photo == null) {
                            cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg"));
                            if (cacheFile.exists()) {
                                tL_photo = SendMessagesHelper.getInstance().generatePhotoSizes(cacheFile.toString(), null);
                            }
                            if (tL_photo == null) {
                                tL_photo = new TL_photo();
                                tL_photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                TL_photoSize photoSize = new TL_photoSize();
                                photoSize.w = searchImage.width;
                                photoSize.h = searchImage.height;
                                photoSize.size = 0;
                                photoSize.location = new TL_fileLocationUnavailable();
                                photoSize.type = "x";
                                tL_photo.sizes.add(photoSize);
                            }
                        }
                    }
                    if (tL_photo != null) {
                        if (searchImage.caption != null) {
                            tL_photo.caption = searchImage.caption.toString();
                        }
                        TL_photo photoFinal = tL_photo;
                        boolean needDownloadHttpFinal = needDownloadHttp;
                        params = new HashMap();
                        if (searchImage.imageUrl != null) {
                            params.put("originalPath", searchImage.imageUrl);
                        }
                        AndroidUtilities.runOnUIThread(new C07162(photoFinal, needDownloadHttpFinal, searchImage, params));
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.16 */
    static class AnonymousClass16 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ String val$text;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.16.1 */
        class C07181 implements Runnable {

            /* renamed from: org.telegram.messenger.SendMessagesHelper.16.1.1 */
            class C07171 implements Runnable {
                C07171() {
                }

                public void run() {
                    String textFinal = SendMessagesHelper.getTrimmedString(AnonymousClass16.this.val$text);
                    if (textFinal.length() != 0) {
                        int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
                        for (int a = 0; a < count; a++) {
                            SendMessagesHelper.getInstance().sendMessage(textFinal.substring(a * MessagesController.UPDATE_MASK_SEND_STATE, Math.min((a + 1) * MessagesController.UPDATE_MASK_SEND_STATE, textFinal.length())), AnonymousClass16.this.val$dialog_id, null, null, true, null, null, null);
                        }
                    }
                }
            }

            C07181() {
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C07171());
            }
        }

        AnonymousClass16(String str, long j) {
            this.val$text = str;
            this.val$dialog_id = j;
        }

        public void run() {
            Utilities.stageQueue.postRunnable(new C07181());
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.17 */
    static class AnonymousClass17 implements Runnable {
        final /* synthetic */ ArrayList val$captions;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$pathsCopy;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ ArrayList val$urisCopy;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.17.1 */
        class C07191 implements Runnable {
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ TL_photo val$photoFinal;

            C07191(TL_photo tL_photo, HashMap hashMap) {
                this.val$photoFinal = tL_photo;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$photoFinal, null, AnonymousClass17.this.val$dialog_id, AnonymousClass17.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass17(long j, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$pathsCopy = arrayList;
            this.val$urisCopy = arrayList2;
            this.val$captions = arrayList3;
            this.val$reply_to_msg = messageObject;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r25 = this;
            r0 = r25;
            r2 = r0.val$dialog_id;
            r2 = (int) r2;
            if (r2 != 0) goto L_0x00a7;
        L_0x0007:
            r13 = 1;
        L_0x0008:
            r19 = 0;
            r21 = 0;
            r20 = 0;
            r0 = r25;
            r2 = r0.val$pathsCopy;
            r2 = r2.isEmpty();
            if (r2 != 0) goto L_0x00aa;
        L_0x0018:
            r0 = r25;
            r2 = r0.val$pathsCopy;
            r11 = r2.size();
        L_0x0020:
            r16 = 0;
            r24 = 0;
            r5 = 0;
            r10 = 0;
        L_0x0026:
            if (r10 >= r11) goto L_0x0196;
        L_0x0028:
            r0 = r25;
            r2 = r0.val$pathsCopy;
            r2 = r2.isEmpty();
            if (r2 != 0) goto L_0x00b4;
        L_0x0032:
            r0 = r25;
            r2 = r0.val$pathsCopy;
            r16 = r2.get(r10);
            r16 = (java.lang.String) r16;
        L_0x003c:
            r14 = r16;
            r23 = r16;
            if (r23 != 0) goto L_0x004c;
        L_0x0042:
            if (r24 == 0) goto L_0x004c;
        L_0x0044:
            r23 = org.telegram.messenger.AndroidUtilities.getPath(r24);
            r14 = r24.toString();
        L_0x004c:
            r12 = 0;
            if (r23 == 0) goto L_0x00cd;
        L_0x004f:
            r2 = ".gif";
            r0 = r23;
            r2 = r0.endsWith(r2);
            if (r2 != 0) goto L_0x0063;
        L_0x0059:
            r2 = ".webp";
            r0 = r23;
            r2 = r0.endsWith(r2);
            if (r2 == 0) goto L_0x00cd;
        L_0x0063:
            r2 = ".gif";
            r0 = r23;
            r2 = r0.endsWith(r2);
            if (r2 == 0) goto L_0x00ca;
        L_0x006d:
            r5 = "gif";
        L_0x006f:
            r12 = 1;
        L_0x0070:
            if (r12 == 0) goto L_0x0100;
        L_0x0072:
            if (r19 != 0) goto L_0x0083;
        L_0x0074:
            r19 = new java.util.ArrayList;
            r19.<init>();
            r21 = new java.util.ArrayList;
            r21.<init>();
            r20 = new java.util.ArrayList;
            r20.<init>();
        L_0x0083:
            r0 = r19;
            r1 = r23;
            r0.add(r1);
            r0 = r21;
            r0.add(r14);
            r0 = r25;
            r2 = r0.val$captions;
            if (r2 == 0) goto L_0x00fe;
        L_0x0095:
            r0 = r25;
            r2 = r0.val$captions;
            r2 = r2.get(r10);
            r2 = (java.lang.String) r2;
        L_0x009f:
            r0 = r20;
            r0.add(r2);
        L_0x00a4:
            r10 = r10 + 1;
            goto L_0x0026;
        L_0x00a7:
            r13 = 0;
            goto L_0x0008;
        L_0x00aa:
            r0 = r25;
            r2 = r0.val$urisCopy;
            r11 = r2.size();
            goto L_0x0020;
        L_0x00b4:
            r0 = r25;
            r2 = r0.val$urisCopy;
            r2 = r2.isEmpty();
            if (r2 != 0) goto L_0x003c;
        L_0x00be:
            r0 = r25;
            r2 = r0.val$urisCopy;
            r24 = r2.get(r10);
            r24 = (android.net.Uri) r24;
            goto L_0x003c;
        L_0x00ca:
            r5 = "webp";
            goto L_0x006f;
        L_0x00cd:
            if (r23 != 0) goto L_0x0070;
        L_0x00cf:
            if (r24 == 0) goto L_0x0070;
        L_0x00d1:
            r2 = org.telegram.messenger.MediaController.isGif(r24);
            if (r2 == 0) goto L_0x00e7;
        L_0x00d7:
            r12 = 1;
            r14 = r24.toString();
            r2 = "gif";
            r0 = r24;
            r23 = org.telegram.messenger.MediaController.copyFileToCache(r0, r2);
            r5 = "gif";
            goto L_0x0070;
        L_0x00e7:
            r2 = org.telegram.messenger.MediaController.isWebp(r24);
            if (r2 == 0) goto L_0x0070;
        L_0x00ed:
            r12 = 1;
            r14 = r24.toString();
            r2 = "webp";
            r0 = r24;
            r23 = org.telegram.messenger.MediaController.copyFileToCache(r0, r2);
            r5 = "webp";
            goto L_0x0070;
        L_0x00fe:
            r2 = 0;
            goto L_0x009f;
        L_0x0100:
            if (r23 == 0) goto L_0x0190;
        L_0x0102:
            r22 = new java.io.File;
            r22.<init>(r23);
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r2 = r2.append(r14);
            r6 = r22.length();
            r2 = r2.append(r6);
            r3 = "_";
            r2 = r2.append(r3);
            r6 = r22.lastModified();
            r2 = r2.append(r6);
            r14 = r2.toString();
        L_0x012a:
            r17 = 0;
            if (r13 != 0) goto L_0x0150;
        L_0x012e:
            r3 = org.telegram.messenger.MessagesStorage.getInstance();
            if (r13 != 0) goto L_0x0192;
        L_0x0134:
            r2 = 0;
        L_0x0135:
            r17 = r3.getSentFile(r14, r2);
            r17 = (org.telegram.tgnet.TLRPC.TL_photo) r17;
            if (r17 != 0) goto L_0x0150;
        L_0x013d:
            if (r24 == 0) goto L_0x0150;
        L_0x013f:
            r3 = org.telegram.messenger.MessagesStorage.getInstance();
            r4 = org.telegram.messenger.AndroidUtilities.getPath(r24);
            if (r13 != 0) goto L_0x0194;
        L_0x0149:
            r2 = 0;
        L_0x014a:
            r17 = r3.getSentFile(r4, r2);
            r17 = (org.telegram.tgnet.TLRPC.TL_photo) r17;
        L_0x0150:
            if (r17 != 0) goto L_0x015e;
        L_0x0152:
            r2 = org.telegram.messenger.SendMessagesHelper.getInstance();
            r0 = r16;
            r1 = r24;
            r17 = r2.generatePhotoSizes(r0, r1);
        L_0x015e:
            if (r17 == 0) goto L_0x00a4;
        L_0x0160:
            r0 = r25;
            r2 = r0.val$captions;
            if (r2 == 0) goto L_0x0174;
        L_0x0166:
            r0 = r25;
            r2 = r0.val$captions;
            r2 = r2.get(r10);
            r2 = (java.lang.String) r2;
            r0 = r17;
            r0.caption = r2;
        L_0x0174:
            r18 = r17;
            r15 = new java.util.HashMap;
            r15.<init>();
            if (r14 == 0) goto L_0x0182;
        L_0x017d:
            r2 = "originalPath";
            r15.put(r2, r14);
        L_0x0182:
            r2 = new org.telegram.messenger.SendMessagesHelper$17$1;
            r0 = r25;
            r1 = r18;
            r2.<init>(r1, r15);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
            goto L_0x00a4;
        L_0x0190:
            r14 = 0;
            goto L_0x012a;
        L_0x0192:
            r2 = 3;
            goto L_0x0135;
        L_0x0194:
            r2 = 3;
            goto L_0x014a;
        L_0x0196:
            if (r19 == 0) goto L_0x01cc;
        L_0x0198:
            r2 = r19.isEmpty();
            if (r2 != 0) goto L_0x01cc;
        L_0x019e:
            r10 = 0;
        L_0x019f:
            r2 = r19.size();
            if (r10 >= r2) goto L_0x01cc;
        L_0x01a5:
            r0 = r19;
            r2 = r0.get(r10);
            r2 = (java.lang.String) r2;
            r0 = r21;
            r3 = r0.get(r10);
            r3 = (java.lang.String) r3;
            r4 = 0;
            r0 = r25;
            r6 = r0.val$dialog_id;
            r0 = r25;
            r8 = r0.val$reply_to_msg;
            r0 = r20;
            r9 = r0.get(r10);
            r9 = (java.lang.String) r9;
            org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r8, r9);
            r10 = r10 + 1;
            goto L_0x019f;
        L_0x01cc:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.17.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.18 */
    static class AnonymousClass18 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ long val$duration;
        final /* synthetic */ long val$estimatedSize;
        final /* synthetic */ int val$height;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ VideoEditedInfo val$videoEditedInfo;
        final /* synthetic */ String val$videoPath;
        final /* synthetic */ int val$width;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.18.1 */
        class C07201 implements Runnable {
            final /* synthetic */ String val$finalPath;
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ TL_document val$videoFinal;

            C07201(TL_document tL_document, String str, HashMap hashMap) {
                this.val$videoFinal = tL_document;
                this.val$finalPath = str;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$videoFinal, AnonymousClass18.this.val$videoEditedInfo, this.val$finalPath, AnonymousClass18.this.val$dialog_id, AnonymousClass18.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass18(long j, VideoEditedInfo videoEditedInfo, String str, long j2, int i, int i2, long j3, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$videoEditedInfo = videoEditedInfo;
            this.val$videoPath = str;
            this.val$duration = j2;
            this.val$height = i;
            this.val$width = i2;
            this.val$estimatedSize = j3;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            MediaMetadataRetriever mediaMetadataRetriever;
            MediaMetadataRetriever mediaMetadataRetriever2;
            MediaPlayer mp;
            Throwable e;
            Throwable th;
            boolean isEncrypted = ((int) this.val$dialog_id) == 0;
            if (this.val$videoEditedInfo != null || this.val$videoPath.endsWith("mp4")) {
                String path = this.val$videoPath;
                String originalPath = this.val$videoPath;
                File file = new File(originalPath);
                originalPath = originalPath + file.length() + "_" + file.lastModified();
                if (this.val$videoEditedInfo != null) {
                    originalPath = originalPath + this.val$duration + "_" + this.val$videoEditedInfo.startTime + "_" + this.val$videoEditedInfo.endTime;
                    if (this.val$videoEditedInfo.resultWidth == this.val$videoEditedInfo.originalWidth) {
                        originalPath = originalPath + "_" + this.val$videoEditedInfo.resultWidth;
                    }
                }
                TL_document document = null;
                PhotoSize size;
                TL_documentAttributeVideo attributeVideo;
                boolean infoObtained;
                String width;
                String height;
                String duration;
                String fileName;
                File cacheFile;
                TL_document videoFinal;
                String originalPathFinal;
                String finalPath;
                HashMap<String, String> params;
                if (isEncrypted) {
                    if (null == null) {
                        size = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(this.val$videoPath, 1), 90.0f, 90.0f, 55, isEncrypted);
                        document = new TL_document();
                        document.thumb = size;
                        if (document.thumb != null) {
                            document.thumb.type = "s";
                        } else {
                            document.thumb = new TL_photoSizeEmpty();
                            document.thumb.type = "s";
                        }
                        document.mime_type = MimeTypes.VIDEO_MP4;
                        UserConfig.saveConfig(false);
                        attributeVideo = new TL_documentAttributeVideo();
                        document.attributes.add(attributeVideo);
                        if (this.val$videoEditedInfo == null) {
                            if (file.exists()) {
                                document.size = (int) file.length();
                            }
                            infoObtained = false;
                            mediaMetadataRetriever = null;
                            mediaMetadataRetriever2 = new MediaMetadataRetriever();
                            mediaMetadataRetriever2.setDataSource(this.val$videoPath);
                            width = mediaMetadataRetriever2.extractMetadata(18);
                            if (width != null) {
                                attributeVideo.w = Integer.parseInt(width);
                            }
                            height = mediaMetadataRetriever2.extractMetadata(19);
                            if (height != null) {
                                attributeVideo.h = Integer.parseInt(height);
                            }
                            duration = mediaMetadataRetriever2.extractMetadata(9);
                            if (duration != null) {
                                attributeVideo.duration = (int) Math.ceil((double) (((float) Long.parseLong(duration)) / 1000.0f));
                            }
                            infoObtained = true;
                            if (mediaMetadataRetriever2 != null) {
                                mediaMetadataRetriever2.release();
                            }
                            mediaMetadataRetriever = mediaMetadataRetriever2;
                            if (!infoObtained) {
                                mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(this.val$videoPath)));
                                if (mp != null) {
                                    attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                                    attributeVideo.w = mp.getVideoWidth();
                                    attributeVideo.h = mp.getVideoHeight();
                                    mp.release();
                                }
                            }
                        } else {
                            attributeVideo.duration = (int) (this.val$duration / 1000);
                            if (this.val$videoEditedInfo.rotationValue != 90) {
                            }
                            attributeVideo.w = this.val$height;
                            attributeVideo.h = this.val$width;
                            document.size = (int) this.val$estimatedSize;
                            fileName = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                            UserConfig.lastLocalId--;
                            cacheFile = new File(FileLoader.getInstance().getDirectory(4), fileName);
                            UserConfig.saveConfig(false);
                            path = cacheFile.getAbsolutePath();
                        }
                    }
                    videoFinal = document;
                    originalPathFinal = originalPath;
                    finalPath = path;
                    params = new HashMap();
                    if (originalPath != null) {
                        params.put("originalPath", originalPath);
                    }
                    AndroidUtilities.runOnUIThread(new C07201(videoFinal, finalPath, params));
                    return;
                }
                if (null == null) {
                    size = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(this.val$videoPath, 1), 90.0f, 90.0f, 55, isEncrypted);
                    document = new TL_document();
                    document.thumb = size;
                    if (document.thumb != null) {
                        document.thumb = new TL_photoSizeEmpty();
                        document.thumb.type = "s";
                    } else {
                        document.thumb.type = "s";
                    }
                    document.mime_type = MimeTypes.VIDEO_MP4;
                    UserConfig.saveConfig(false);
                    attributeVideo = new TL_documentAttributeVideo();
                    document.attributes.add(attributeVideo);
                    if (this.val$videoEditedInfo == null) {
                        attributeVideo.duration = (int) (this.val$duration / 1000);
                        if (this.val$videoEditedInfo.rotationValue != 90 || this.val$videoEditedInfo.rotationValue == 270) {
                            attributeVideo.w = this.val$height;
                            attributeVideo.h = this.val$width;
                        } else {
                            attributeVideo.w = this.val$width;
                            attributeVideo.h = this.val$height;
                        }
                        document.size = (int) this.val$estimatedSize;
                        fileName = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                        UserConfig.lastLocalId--;
                        cacheFile = new File(FileLoader.getInstance().getDirectory(4), fileName);
                        UserConfig.saveConfig(false);
                        path = cacheFile.getAbsolutePath();
                    } else {
                        if (file.exists()) {
                            document.size = (int) file.length();
                        }
                        infoObtained = false;
                        mediaMetadataRetriever = null;
                        try {
                            mediaMetadataRetriever2 = new MediaMetadataRetriever();
                            try {
                                mediaMetadataRetriever2.setDataSource(this.val$videoPath);
                                width = mediaMetadataRetriever2.extractMetadata(18);
                                if (width != null) {
                                    attributeVideo.w = Integer.parseInt(width);
                                }
                                height = mediaMetadataRetriever2.extractMetadata(19);
                                if (height != null) {
                                    attributeVideo.h = Integer.parseInt(height);
                                }
                                duration = mediaMetadataRetriever2.extractMetadata(9);
                                if (duration != null) {
                                    attributeVideo.duration = (int) Math.ceil((double) (((float) Long.parseLong(duration)) / 1000.0f));
                                }
                                infoObtained = true;
                                if (mediaMetadataRetriever2 != null) {
                                    try {
                                        mediaMetadataRetriever2.release();
                                    } catch (Throwable e2) {
                                        FileLog.m13e("tmessages", e2);
                                        mediaMetadataRetriever = mediaMetadataRetriever2;
                                    }
                                }
                                mediaMetadataRetriever = mediaMetadataRetriever2;
                            } catch (Exception e3) {
                                e2 = e3;
                                mediaMetadataRetriever = mediaMetadataRetriever2;
                                try {
                                    FileLog.m13e("tmessages", e2);
                                    if (mediaMetadataRetriever != null) {
                                        try {
                                            mediaMetadataRetriever.release();
                                        } catch (Throwable e22) {
                                            FileLog.m13e("tmessages", e22);
                                        }
                                    }
                                    if (infoObtained) {
                                        try {
                                            mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(this.val$videoPath)));
                                            if (mp != null) {
                                                attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                                                attributeVideo.w = mp.getVideoWidth();
                                                attributeVideo.h = mp.getVideoHeight();
                                                mp.release();
                                            }
                                        } catch (Throwable e222) {
                                            FileLog.m13e("tmessages", e222);
                                        }
                                    }
                                    videoFinal = document;
                                    originalPathFinal = originalPath;
                                    finalPath = path;
                                    params = new HashMap();
                                    if (originalPath != null) {
                                        params.put("originalPath", originalPath);
                                    }
                                    AndroidUtilities.runOnUIThread(new C07201(videoFinal, finalPath, params));
                                    return;
                                } catch (Throwable th2) {
                                    th = th2;
                                    if (mediaMetadataRetriever != null) {
                                        try {
                                            mediaMetadataRetriever.release();
                                        } catch (Throwable e2222) {
                                            FileLog.m13e("tmessages", e2222);
                                        }
                                    }
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                mediaMetadataRetriever = mediaMetadataRetriever2;
                                if (mediaMetadataRetriever != null) {
                                    mediaMetadataRetriever.release();
                                }
                                throw th;
                            }
                        } catch (Exception e4) {
                            e2222 = e4;
                            FileLog.m13e("tmessages", e2222);
                            if (mediaMetadataRetriever != null) {
                                mediaMetadataRetriever.release();
                            }
                            if (infoObtained) {
                                mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(this.val$videoPath)));
                                if (mp != null) {
                                    attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                                    attributeVideo.w = mp.getVideoWidth();
                                    attributeVideo.h = mp.getVideoHeight();
                                    mp.release();
                                }
                            }
                            videoFinal = document;
                            originalPathFinal = originalPath;
                            finalPath = path;
                            params = new HashMap();
                            if (originalPath != null) {
                                params.put("originalPath", originalPath);
                            }
                            AndroidUtilities.runOnUIThread(new C07201(videoFinal, finalPath, params));
                            return;
                        }
                        if (infoObtained) {
                            mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(this.val$videoPath)));
                            if (mp != null) {
                                attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                                attributeVideo.w = mp.getVideoWidth();
                                attributeVideo.h = mp.getVideoHeight();
                                mp.release();
                            }
                        }
                    }
                }
                videoFinal = document;
                originalPathFinal = originalPath;
                finalPath = path;
                params = new HashMap();
                if (originalPath != null) {
                    params.put("originalPath", originalPath);
                }
                AndroidUtilities.runOnUIThread(new C07201(videoFinal, finalPath, params));
                return;
            }
            SendMessagesHelper.prepareSendingDocumentInternal(this.val$videoPath, this.val$videoPath, null, null, this.val$dialog_id, this.val$reply_to_msg, null);
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.2 */
    class C07222 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ DelayedMessage val$message;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.2.1 */
        class C07211 implements Runnable {
            final /* synthetic */ TL_photo val$photo;

            C07211(TL_photo tL_photo) {
                this.val$photo = tL_photo;
            }

            public void run() {
                if (this.val$photo != null) {
                    C07222.this.val$message.httpLocation = null;
                    C07222.this.val$message.obj.messageOwner.media.photo = this.val$photo;
                    C07222.this.val$message.obj.messageOwner.attachPath = C07222.this.val$cacheFile.toString();
                    C07222.this.val$message.location = ((PhotoSize) this.val$photo.sizes.get(this.val$photo.sizes.size() - 1)).location;
                    ArrayList messages = new ArrayList();
                    messages.add(C07222.this.val$message.obj.messageOwner);
                    MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                    SendMessagesHelper.this.performSendDelayedMessage(C07222.this.val$message);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, C07222.this.val$message.obj);
                    return;
                }
                FileLog.m11e("tmessages", "can't load image " + C07222.this.val$message.httpLocation + " to file " + C07222.this.val$cacheFile.toString());
                MessagesStorage.getInstance().markMessageAsSendError(C07222.this.val$message.obj.messageOwner);
                C07222.this.val$message.obj.messageOwner.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(C07222.this.val$message.obj.getId()));
                SendMessagesHelper.this.processSentMessage(C07222.this.val$message.obj.getId());
            }
        }

        C07222(File file, DelayedMessage delayedMessage) {
            this.val$cacheFile = file;
            this.val$message = delayedMessage;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C07211(SendMessagesHelper.getInstance().generatePhotoSizes(this.val$cacheFile.toString(), null)));
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.3 */
    class C07243 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ DelayedMessage val$message;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.3.1 */
        class C07231 implements Runnable {
            C07231() {
            }

            public void run() {
                C07243.this.val$message.httpLocation = null;
                C07243.this.val$message.obj.messageOwner.attachPath = C07243.this.val$cacheFile.toString();
                C07243.this.val$message.location = C07243.this.val$message.documentLocation.thumb.location;
                ArrayList messages = new ArrayList();
                messages.add(C07243.this.val$message.obj.messageOwner);
                MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                SendMessagesHelper.this.performSendDelayedMessage(C07243.this.val$message);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, C07243.this.val$message.obj);
            }
        }

        C07243(DelayedMessage delayedMessage, File file) {
            this.val$message = delayedMessage;
            this.val$cacheFile = file;
        }

        public void run() {
            boolean z = true;
            if (this.val$message.documentLocation.thumb.location instanceof TL_fileLocationUnavailable) {
                try {
                    Bitmap bitmap = ImageLoader.loadBitmap(this.val$cacheFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                    if (bitmap != null) {
                        TL_document tL_document = this.val$message.documentLocation;
                        if (this.val$message.sendEncryptedRequest == null) {
                            z = false;
                        }
                        tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z);
                        bitmap.recycle();
                    }
                } catch (Throwable e) {
                    this.val$message.documentLocation.thumb = null;
                    FileLog.m13e("tmessages", e);
                }
                if (this.val$message.documentLocation.thumb == null) {
                    this.val$message.documentLocation.thumb = new TL_photoSizeEmpty();
                    this.val$message.documentLocation.thumb.type = "s";
                }
            }
            AndroidUtilities.runOnUIThread(new C07231());
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.7 */
    class C07337 implements Runnable {
        final /* synthetic */ String val$path;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.7.1 */
        class C07321 implements Runnable {
            C07321() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, C07337.this.val$path);
            }
        }

        C07337(String str) {
            this.val$path = str;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C07321());
        }
    }

    protected class DelayedMessage {
        public TL_document documentLocation;
        public EncryptedChat encryptedChat;
        public String httpLocation;
        public FileLocation location;
        public MessageObject obj;
        public String originalPath;
        public TL_decryptedMessage sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;

        protected DelayedMessage() {
        }
    }

    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener;
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.LocationProvider.1 */
        class C07411 implements Runnable {
            C07411() {
            }

            public void run() {
                if (LocationProvider.this.locationQueryCancelRunnable == this) {
                    if (LocationProvider.this.delegate != null) {
                        if (LocationProvider.this.lastKnownLocation != null) {
                            LocationProvider.this.delegate.onLocationAcquired(LocationProvider.this.lastKnownLocation);
                        } else {
                            LocationProvider.this.delegate.onUnableLocationAcquire();
                        }
                    }
                    LocationProvider.this.cleanup();
                }
            }
        }

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    FileLog.m11e("tmessages", "found location " + location);
                    LocationProvider.this.lastKnownLocation = location;
                    if (location.getAccuracy() < 100.0f) {
                        if (LocationProvider.this.delegate != null) {
                            LocationProvider.this.delegate.onLocationAcquired(location);
                        }
                        if (LocationProvider.this.locationQueryCancelRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        }

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        public LocationProvider() {
            this.gpsLocationListener = new GpsLocationListener();
            this.networkLocationListener = new GpsLocationListener();
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.gpsLocationListener = new GpsLocationListener();
            this.networkLocationListener = new GpsLocationListener();
            this.delegate = locationProviderDelegate;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        private void cleanup() {
            this.locationManager.removeUpdates(this.gpsLocationListener);
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.lastKnownLocation = null;
            this.locationQueryCancelRunnable = null;
        }

        public void start() {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            }
            try {
                this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
            if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
            }
            this.locationQueryCancelRunnable = new C07411();
            AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
        }

        public void stop() {
            if (this.locationManager != null) {
                if (this.locationQueryCancelRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
                }
                cleanup();
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.1 */
    class C16901 implements LocationProviderDelegate {
        C16901() {
        }

        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.4 */
    class C16914 implements RequestDelegate {
        final /* synthetic */ boolean val$isMegagroupFinal;
        final /* synthetic */ HashMap val$messagesByRandomIdsFinal;
        final /* synthetic */ ArrayList val$newMsgArr;
        final /* synthetic */ ArrayList val$newMsgObjArr;
        final /* synthetic */ long val$peer;
        final /* synthetic */ Peer val$to_id;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.4.1 */
        class C07261 implements Runnable {
            final /* synthetic */ Message val$newMsgObj;
            final /* synthetic */ int val$oldId;
            final /* synthetic */ ArrayList val$sentMessages;

            /* renamed from: org.telegram.messenger.SendMessagesHelper.4.1.1 */
            class C07251 implements Runnable {
                C07251() {
                }

                public void run() {
                    C07261.this.val$newMsgObj.send_state = 0;
                    SearchQuery.increasePeerRaiting(C16914.this.val$peer);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(C07261.this.val$oldId), Integer.valueOf(C07261.this.val$newMsgObj.id), C07261.this.val$newMsgObj, Long.valueOf(C16914.this.val$peer));
                    SendMessagesHelper.this.processSentMessage(C07261.this.val$oldId);
                    SendMessagesHelper.this.removeFromSendingMessages(C07261.this.val$oldId);
                }
            }

            C07261(Message message, int i, ArrayList arrayList) {
                this.val$newMsgObj = message;
                this.val$oldId = i;
                this.val$sentMessages = arrayList;
            }

            public void run() {
                MessagesStorage.getInstance().updateMessageStateAndId(this.val$newMsgObj.random_id, Integer.valueOf(this.val$oldId), this.val$newMsgObj.id, 0, false, C16914.this.val$to_id.channel_id);
                MessagesStorage.getInstance().putMessages(this.val$sentMessages, true, false, false, 0);
                AndroidUtilities.runOnUIThread(new C07251());
                if (MessageObject.isVideoMessage(this.val$newMsgObj)) {
                    SendMessagesHelper.this.stopVideoService(this.val$newMsgObj.attachPath);
                }
            }
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper.4.2 */
        class C07272 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C07272(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (this.val$error.text.equals("PEER_FLOOD")) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                }
            }
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper.4.3 */
        class C07283 implements Runnable {
            final /* synthetic */ Message val$newMsgObj;

            C07283(Message message) {
                this.val$newMsgObj = message;
            }

            public void run() {
                this.val$newMsgObj.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.val$newMsgObj.id));
                SendMessagesHelper.this.processSentMessage(this.val$newMsgObj.id);
                if (MessageObject.isVideoMessage(this.val$newMsgObj)) {
                    SendMessagesHelper.this.stopVideoService(this.val$newMsgObj.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(this.val$newMsgObj.id);
            }
        }

        C16914(long j, boolean z, HashMap hashMap, ArrayList arrayList, ArrayList arrayList2, Peer peer) {
            this.val$peer = j;
            this.val$isMegagroupFinal = z;
            this.val$messagesByRandomIdsFinal = hashMap;
            this.val$newMsgArr = arrayList;
            this.val$newMsgObjArr = arrayList2;
            this.val$to_id = peer;
        }

        public void run(TLObject response, TL_error error) {
            int a;
            Message newMsgObj;
            if (error == null) {
                Update update;
                HashMap<Integer, Long> newMessagesByIds = new HashMap();
                Updates updates = (Updates) response;
                a = 0;
                while (a < updates.updates.size()) {
                    update = (Update) updates.updates.get(a);
                    if (update instanceof TL_updateMessageID) {
                        TL_updateMessageID updateMessageID = (TL_updateMessageID) update;
                        newMessagesByIds.put(Integer.valueOf(updateMessageID.id), Long.valueOf(updateMessageID.random_id));
                        updates.updates.remove(a);
                        a--;
                    }
                    a++;
                }
                Integer value = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(this.val$peer));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$peer));
                    MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(this.val$peer), value);
                }
                for (a = 0; a < updates.updates.size(); a++) {
                    update = (Update) updates.updates.get(a);
                    if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                        Message message;
                        if (update instanceof TL_updateNewMessage) {
                            message = ((TL_updateNewMessage) update).message;
                            MessagesController.getInstance().processNewDifferenceParams(-1, update.pts, -1, update.pts_count);
                        } else {
                            message = ((TL_updateNewChannelMessage) update).message;
                            MessagesController.getInstance().processNewChannelDifferenceParams(update.pts, update.pts_count, message.to_id.channel_id);
                            if (this.val$isMegagroupFinal) {
                                message.flags |= LinearLayoutManager.INVALID_OFFSET;
                            }
                        }
                        message.unread = value.intValue() < message.id;
                        Long random_id = (Long) newMessagesByIds.get(Integer.valueOf(message.id));
                        if (random_id != null) {
                            newMsgObj = (Message) this.val$messagesByRandomIdsFinal.get(random_id);
                            if (newMsgObj != null) {
                                MessageObject msgObj = (MessageObject) this.val$newMsgArr.get(this.val$newMsgObjArr.indexOf(newMsgObj));
                                this.val$newMsgObjArr.remove(newMsgObj);
                                int oldId = newMsgObj.id;
                                ArrayList<Message> sentMessages = new ArrayList();
                                sentMessages.add(message);
                                newMsgObj.id = message.id;
                                SendMessagesHelper.this.updateMediaPaths(msgObj, message, null, true);
                                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07261(newMsgObj, oldId, sentMessages));
                            }
                        }
                    }
                }
            } else {
                AndroidUtilities.runOnUIThread(new C07272(error));
            }
            for (a = 0; a < this.val$newMsgObjArr.size(); a++) {
                newMsgObj = (Message) this.val$newMsgObjArr.get(a);
                MessagesStorage.getInstance().markMessageAsSendError(newMsgObj);
                AndroidUtilities.runOnUIThread(new C07283(newMsgObj));
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.5 */
    class C16925 implements RequestDelegate {
        final /* synthetic */ Runnable val$callback;
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.5.1 */
        class C07291 implements Runnable {
            C07291() {
            }

            public void run() {
                C16925.this.val$callback.run();
            }
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper.5.2 */
        class C07302 implements Runnable {
            C07302() {
            }

            public void run() {
                Builder builder = new Builder(C16925.this.val$fragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setMessage(LocaleController.getString("EditMessageError", C0691R.string.EditMessageError));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                C16925.this.val$fragment.showDialog(builder.create());
            }
        }

        C16925(Runnable runnable, BaseFragment baseFragment) {
            this.val$callback = runnable;
            this.val$fragment = baseFragment;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C07291());
            if (error == null) {
                MessagesController.getInstance().processUpdates((Updates) response, false);
            } else if (!error.text.equals("MESSAGE_NOT_MODIFIED")) {
                AndroidUtilities.runOnUIThread(new C07302());
            }
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.6 */
    class C16936 implements RequestDelegate {
        final /* synthetic */ String val$key;
        final /* synthetic */ MessageObject val$messageObject;
        final /* synthetic */ ChatActivity val$parentFragment;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.6.1 */
        class C07311 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C07311(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response != null) {
                    TL_messages_botCallbackAnswer res = this.val$response;
                    if (res.message != null) {
                        if (!res.alert) {
                            int uid = C16936.this.val$messageObject.messageOwner.from_id;
                            if (C16936.this.val$messageObject.messageOwner.via_bot_id != 0) {
                                uid = C16936.this.val$messageObject.messageOwner.via_bot_id;
                            }
                            User user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                            if (user != null) {
                                C16936.this.val$parentFragment.showAlert(user, res.message);
                            } else {
                                return;
                            }
                        } else if (C16936.this.val$parentFragment.getParentActivity() != null) {
                            Builder builder = new Builder(C16936.this.val$parentFragment.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                            builder.setMessage(res.message);
                            C16936.this.val$parentFragment.showDialog(builder.create());
                        } else {
                            return;
                        }
                    }
                }
                SendMessagesHelper.this.waitingForCallback.remove(C16936.this.val$key);
            }
        }

        C16936(ChatActivity chatActivity, MessageObject messageObject, String str) {
            this.val$parentFragment = chatActivity;
            this.val$messageObject = messageObject;
            this.val$key = str;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C07311(response));
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.8 */
    class C16948 implements RequestDelegate {
        final /* synthetic */ MessageObject val$msgObj;
        final /* synthetic */ Message val$newMsgObj;
        final /* synthetic */ String val$originalPath;
        final /* synthetic */ TLObject val$req;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.8.1 */
        class C07391 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.messenger.SendMessagesHelper.8.1.1 */
            class C07341 implements Runnable {
                final /* synthetic */ TL_updateShortSentMessage val$res;

                C07341(TL_updateShortSentMessage tL_updateShortSentMessage) {
                    this.val$res = tL_updateShortSentMessage;
                }

                public void run() {
                    MessagesController.getInstance().processNewDifferenceParams(-1, this.val$res.pts, this.val$res.date, this.val$res.pts_count);
                }
            }

            /* renamed from: org.telegram.messenger.SendMessagesHelper.8.1.2 */
            class C07352 implements Runnable {
                final /* synthetic */ TL_updateNewMessage val$newMessage;

                C07352(TL_updateNewMessage tL_updateNewMessage) {
                    this.val$newMessage = tL_updateNewMessage;
                }

                public void run() {
                    MessagesController.getInstance().processNewDifferenceParams(-1, this.val$newMessage.pts, -1, this.val$newMessage.pts_count);
                }
            }

            /* renamed from: org.telegram.messenger.SendMessagesHelper.8.1.3 */
            class C07363 implements Runnable {
                final /* synthetic */ TL_updateNewChannelMessage val$newMessage;

                C07363(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
                    this.val$newMessage = tL_updateNewChannelMessage;
                }

                public void run() {
                    MessagesController.getInstance().processNewChannelDifferenceParams(this.val$newMessage.pts, this.val$newMessage.pts_count, this.val$newMessage.message.to_id.channel_id);
                }
            }

            /* renamed from: org.telegram.messenger.SendMessagesHelper.8.1.4 */
            class C07384 implements Runnable {
                final /* synthetic */ String val$attachPath;
                final /* synthetic */ boolean val$isBroadcast;
                final /* synthetic */ int val$oldId;
                final /* synthetic */ ArrayList val$sentMessages;

                /* renamed from: org.telegram.messenger.SendMessagesHelper.8.1.4.1 */
                class C07371 implements Runnable {
                    C07371() {
                    }

                    public void run() {
                        if (C07384.this.val$isBroadcast) {
                            for (int a = 0; a < C07384.this.val$sentMessages.size(); a++) {
                                Message message = (Message) C07384.this.val$sentMessages.get(a);
                                ArrayList<MessageObject> arr = new ArrayList();
                                MessageObject messageObject = new MessageObject(message, null, false);
                                arr.add(messageObject);
                                MessagesController.getInstance().updateInterfaceWithMessages(messageObject.getDialogId(), arr, true);
                            }
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                        SearchQuery.increasePeerRaiting(C16948.this.val$newMsgObj.dialog_id);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.messageReceivedByServer;
                        Object[] objArr = new Object[4];
                        objArr[0] = Integer.valueOf(C07384.this.val$oldId);
                        objArr[1] = Integer.valueOf(C07384.this.val$isBroadcast ? C07384.this.val$oldId : C16948.this.val$newMsgObj.id);
                        objArr[2] = C16948.this.val$newMsgObj;
                        objArr[3] = Long.valueOf(C16948.this.val$newMsgObj.dialog_id);
                        instance.postNotificationName(i, objArr);
                        SendMessagesHelper.this.processSentMessage(C07384.this.val$oldId);
                        SendMessagesHelper.this.removeFromSendingMessages(C07384.this.val$oldId);
                    }
                }

                C07384(int i, boolean z, ArrayList arrayList, String str) {
                    this.val$oldId = i;
                    this.val$isBroadcast = z;
                    this.val$sentMessages = arrayList;
                    this.val$attachPath = str;
                }

                public void run() {
                    MessagesStorage.getInstance().updateMessageStateAndId(C16948.this.val$newMsgObj.random_id, Integer.valueOf(this.val$oldId), this.val$isBroadcast ? this.val$oldId : C16948.this.val$newMsgObj.id, 0, false, C16948.this.val$newMsgObj.to_id.channel_id);
                    MessagesStorage.getInstance().putMessages(this.val$sentMessages, true, false, this.val$isBroadcast, 0);
                    if (this.val$isBroadcast) {
                        ArrayList currentMessage = new ArrayList();
                        currentMessage.add(C16948.this.val$newMsgObj);
                        MessagesStorage.getInstance().putMessages(currentMessage, true, false, false, 0);
                    }
                    AndroidUtilities.runOnUIThread(new C07371());
                    if (MessageObject.isVideoMessage(C16948.this.val$newMsgObj)) {
                        SendMessagesHelper.this.stopVideoService(this.val$attachPath);
                    }
                }
            }

            C07391(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean isSentError = false;
                if (this.val$error == null) {
                    int i;
                    int oldId = C16948.this.val$newMsgObj.id;
                    boolean isBroadcast = C16948.this.val$req instanceof TL_messages_sendBroadcast;
                    ArrayList<Message> sentMessages = new ArrayList();
                    String attachPath = C16948.this.val$newMsgObj.attachPath;
                    Message message;
                    if (this.val$response instanceof TL_updateShortSentMessage) {
                        TL_updateShortSentMessage res = this.val$response;
                        message = C16948.this.val$newMsgObj;
                        Message message2 = C16948.this.val$newMsgObj;
                        i = res.id;
                        message2.id = i;
                        message.local_id = i;
                        C16948.this.val$newMsgObj.date = res.date;
                        C16948.this.val$newMsgObj.entities = res.entities;
                        C16948.this.val$newMsgObj.out = res.out;
                        if (res.media != null) {
                            C16948.this.val$newMsgObj.media = res.media;
                            message = C16948.this.val$newMsgObj;
                            message.flags |= MessagesController.UPDATE_MASK_SELECT_DIALOG;
                        }
                        if (!C16948.this.val$newMsgObj.entities.isEmpty()) {
                            message = C16948.this.val$newMsgObj;
                            message.flags |= MessagesController.UPDATE_MASK_USER_PHONE;
                        }
                        Utilities.stageQueue.postRunnable(new C07341(res));
                        sentMessages.add(C16948.this.val$newMsgObj);
                    } else if (this.val$response instanceof Updates) {
                        ArrayList<Update> updates = ((Updates) this.val$response).updates;
                        Message message3 = null;
                        int a = 0;
                        while (a < updates.size()) {
                            Update update = (Update) updates.get(a);
                            if (update instanceof TL_updateNewMessage) {
                                TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                                message3 = newMessage.message;
                                sentMessages.add(message3);
                                C16948.this.val$newMsgObj.id = newMessage.message.id;
                                Utilities.stageQueue.postRunnable(new C07352(newMessage));
                                break;
                            } else if (update instanceof TL_updateNewChannelMessage) {
                                TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                                message3 = newMessage2.message;
                                sentMessages.add(message3);
                                if ((C16948.this.val$newMsgObj.flags & LinearLayoutManager.INVALID_OFFSET) != 0) {
                                    message = newMessage2.message;
                                    message.flags |= LinearLayoutManager.INVALID_OFFSET;
                                }
                                Utilities.stageQueue.postRunnable(new C07363(newMessage2));
                            } else {
                                a++;
                            }
                        }
                        if (message3 != null) {
                            Integer value = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(message3.dialog_id));
                            if (value == null) {
                                value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message3.out, message3.dialog_id));
                                MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(message3.dialog_id), value);
                            }
                            message3.unread = value.intValue() < message3.id;
                            C16948.this.val$newMsgObj.id = message3.id;
                            SendMessagesHelper.this.updateMediaPaths(C16948.this.val$msgObj, message3, C16948.this.val$originalPath, false);
                        } else {
                            isSentError = true;
                        }
                    }
                    if (!isSentError) {
                        int i2;
                        C16948.this.val$newMsgObj.send_state = 0;
                        NotificationCenter instance = NotificationCenter.getInstance();
                        i = NotificationCenter.messageReceivedByServer;
                        Integer[] numArr = new Object[4];
                        numArr[0] = Integer.valueOf(oldId);
                        if (isBroadcast) {
                            i2 = oldId;
                        } else {
                            i2 = C16948.this.val$newMsgObj.id;
                        }
                        numArr[1] = Integer.valueOf(i2);
                        numArr[2] = C16948.this.val$newMsgObj;
                        numArr[3] = Long.valueOf(C16948.this.val$newMsgObj.dialog_id);
                        instance.postNotificationName(i, numArr);
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07384(oldId, isBroadcast, sentMessages, attachPath));
                    }
                } else {
                    if (this.val$error.text.equals("PEER_FLOOD")) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                    }
                    isSentError = true;
                }
                if (isSentError) {
                    MessagesStorage.getInstance().markMessageAsSendError(C16948.this.val$newMsgObj);
                    C16948.this.val$newMsgObj.send_state = 2;
                    NotificationCenter instance2 = NotificationCenter.getInstance();
                    int i3 = NotificationCenter.messageSendError;
                    Integer[] numArr2 = new Object[1];
                    numArr2[0] = Integer.valueOf(C16948.this.val$newMsgObj.id);
                    instance2.postNotificationName(i3, numArr2);
                    SendMessagesHelper.this.processSentMessage(C16948.this.val$newMsgObj.id);
                    if (MessageObject.isVideoMessage(C16948.this.val$newMsgObj)) {
                        SendMessagesHelper.this.stopVideoService(C16948.this.val$newMsgObj.attachPath);
                    }
                    SendMessagesHelper.this.removeFromSendingMessages(C16948.this.val$newMsgObj.id);
                }
            }
        }

        C16948(Message message, TLObject tLObject, MessageObject messageObject, String str) {
            this.val$newMsgObj = message;
            this.val$req = tLObject;
            this.val$msgObj = messageObject;
            this.val$originalPath = str;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C07391(error, response));
        }
    }

    /* renamed from: org.telegram.messenger.SendMessagesHelper.9 */
    class C16959 implements QuickAckDelegate {
        final /* synthetic */ Message val$newMsgObj;

        /* renamed from: org.telegram.messenger.SendMessagesHelper.9.1 */
        class C07401 implements Runnable {
            final /* synthetic */ int val$msg_id;

            C07401(int i) {
                this.val$msg_id = i;
            }

            public void run() {
                C16959.this.val$newMsgObj.send_state = 0;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(this.val$msg_id));
            }
        }

        C16959(Message message) {
            this.val$newMsgObj = message;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C07401(this.val$newMsgObj.id));
        }
    }

    static {
        Instance = null;
    }

    public static SendMessagesHelper getInstance() {
        SendMessagesHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (SendMessagesHelper.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        SendMessagesHelper localInstance2 = new SendMessagesHelper();
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

    public SendMessagesHelper() {
        this.currentChatInfo = null;
        this.delayedMessages = new HashMap();
        this.unsentMessages = new HashMap();
        this.sendingMessages = new HashMap();
        this.waitingForLocation = new HashMap();
        this.waitingForCallback = new HashMap();
        this.locationProvider = new LocationProvider(new C16901());
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.currentChatInfo = null;
        this.locationProvider.stop();
    }

    public void setCurrentChatInfo(ChatFull info) {
        this.currentChatInfo = info;
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        ArrayList<DelayedMessage> arr;
        int a;
        DelayedMessage message;
        if (id == NotificationCenter.FileDidUpload) {
            location = args[0];
            InputFile file = args[1];
            InputEncryptedFile encryptedFile = args[2];
            arr = (ArrayList) this.delayedMessages.get(location);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    message = (DelayedMessage) arr.get(a);
                    InputMedia media = null;
                    if (message.sendRequest instanceof TL_messages_sendMedia) {
                        media = ((TL_messages_sendMedia) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TL_messages_sendBroadcast) {
                        media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                    }
                    if (file != null && media != null) {
                        if (message.type == 0) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                        } else if (message.type == 1) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 2) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 3) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                        }
                        arr.remove(a);
                        a--;
                    } else if (!(encryptedFile == null || message.sendEncryptedRequest == null)) {
                        if ((message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaVideo) || (message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaPhoto)) {
                            message.sendEncryptedRequest.media.size = (int) ((Long) args[5]).longValue();
                        }
                        message.sendEncryptedRequest.media.key = (byte[]) args[3];
                        message.sendEncryptedRequest.media.iv = (byte[]) args[4];
                        SecretChatHelper.getInstance().performSendEncryptedRequest(message.sendEncryptedRequest, message.obj.messageOwner, message.encryptedChat, encryptedFile, message.originalPath, message.obj);
                        arr.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            boolean enc = ((Boolean) args[1]).booleanValue();
            arr = (ArrayList) this.delayedMessages.get(location);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    DelayedMessage obj = (DelayedMessage) arr.get(a);
                    if ((enc && obj.sendEncryptedRequest != null) || !(enc || obj.sendRequest == null)) {
                        MessagesStorage.getInstance().markMessageAsSendError(obj.obj.messageOwner);
                        obj.obj.messageOwner.send_state = 2;
                        arr.remove(a);
                        a--;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(obj.obj.getId()));
                        processSentMessage(obj.obj.getId());
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.FilePreparingStarted) {
            messageObject = args[0];
            finalPath = args[1];
            arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    message = (DelayedMessage) arr.get(a);
                    if (message.obj == messageObject) {
                        message.videoEditedInfo = null;
                        performSendDelayedMessage(message);
                        arr.remove(a);
                        break;
                    }
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                }
            }
        } else if (id == NotificationCenter.FileNewChunkAvailable) {
            messageObject = (MessageObject) args[0];
            finalPath = (String) args[1];
            long finalSize = ((Long) args[2]).longValue();
            FileLoader.getInstance().checkUploadNewDataAvailable(finalPath, ((int) messageObject.getDialogId()) == 0, finalSize);
            if (finalSize != 0) {
                arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arr != null) {
                    for (a = 0; a < arr.size(); a++) {
                        message = (DelayedMessage) arr.get(a);
                        if (message.obj == messageObject) {
                            message.obj.videoEditedInfo = null;
                            message.obj.messageOwner.message = "-1";
                            message.obj.messageOwner.media.document.size = (int) finalSize;
                            ArrayList messages = new ArrayList();
                            messages.add(message.obj.messageOwner);
                            MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                            break;
                        }
                    }
                    if (arr.isEmpty()) {
                        this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                    }
                }
            }
        } else if (id == NotificationCenter.FilePreparingFailed) {
            messageObject = (MessageObject) args[0];
            finalPath = (String) args[1];
            stopVideoService(messageObject.messageOwner.attachPath);
            arr = (ArrayList) this.delayedMessages.get(finalPath);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    message = (DelayedMessage) arr.get(a);
                    if (message.obj == messageObject) {
                        MessagesStorage.getInstance().markMessageAsSendError(message.obj.messageOwner);
                        message.obj.messageOwner.send_state = 2;
                        arr.remove(a);
                        a--;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.obj.getId()));
                        processSentMessage(message.obj.getId());
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(finalPath);
                }
            }
        } else if (id == NotificationCenter.httpFileDidLoaded) {
            path = args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    message = (DelayedMessage) arr.get(a);
                    if (message.type == 0) {
                        Utilities.globalQueue.postRunnable(new C07222(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(message.httpLocation) + "." + ImageLoader.getHttpUrlExtension(message.httpLocation, "file")), message));
                    } else if (message.type == 2) {
                        Utilities.globalQueue.postRunnable(new C07243(message, new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(message.httpLocation) + ".gif")));
                    }
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.FileDidLoaded) {
            path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    performSendDelayedMessage((DelayedMessage) arr.get(a));
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.httpFileDidFailedLoad || id == NotificationCenter.FileDidFailedLoad) {
            path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                Iterator i$ = arr.iterator();
                while (i$.hasNext()) {
                    message = (DelayedMessage) i$.next();
                    MessagesStorage.getInstance().markMessageAsSendError(message.obj.messageOwner);
                    message.obj.messageOwner.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.obj.getId()));
                    processSentMessage(message.obj.getId());
                }
                this.delayedMessages.remove(path);
            }
        }
    }

    public void cancelSendingMessage(MessageObject object) {
        String keyToRemvoe = null;
        boolean enc = false;
        for (Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> messages = (ArrayList) entry.getValue();
            int a = 0;
            while (a < messages.size()) {
                DelayedMessage message = (DelayedMessage) messages.get(a);
                if (message.obj.getId() == object.getId()) {
                    messages.remove(a);
                    MediaController.getInstance().cancelVideoConvert(message.obj);
                    if (messages.size() == 0) {
                        keyToRemvoe = (String) entry.getKey();
                        if (message.sendEncryptedRequest != null) {
                            enc = true;
                        }
                    }
                } else {
                    a++;
                }
            }
        }
        if (keyToRemvoe != null) {
            if (keyToRemvoe.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(keyToRemvoe);
            } else {
                FileLoader.getInstance().cancelUploadFile(keyToRemvoe, enc);
            }
            stopVideoService(keyToRemvoe);
        }
        ArrayList<Integer> messages2 = new ArrayList();
        messages2.add(Integer.valueOf(object.getId()));
        MessagesController.getInstance().deleteMessages(messages2, null, null, object.messageOwner.to_id.channel_id);
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            return false;
        }
        if (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) {
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                MessagesStorage.getInstance().markMessageAsSendError(messageObject.messageOwner);
                messageObject.messageOwner.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                SecretChatHelper.getInstance().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionDeleteMessages) {
                SecretChatHelper.getInstance().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionFlushHistory) {
                SecretChatHelper.getInstance().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNotifyLayer) {
                SecretChatHelper.getInstance().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionReadMessages) {
                SecretChatHelper.getInstance().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                SecretChatHelper.getInstance().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!((messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionTyping) || (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionResend))) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionCommitKey) {
                    SecretChatHelper.getInstance().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAbortKey) {
                    SecretChatHelper.getInstance().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionRequestKey) {
                    SecretChatHelper.getInstance().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAcceptKey) {
                    SecretChatHelper.getInstance().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNoop) {
                    SecretChatHelper.getInstance().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (unsent) {
            this.unsentMessages.put(Integer.valueOf(messageObject.getId()), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    protected void processSentMessage(int id) {
        int prevSize = this.unsentMessages.size();
        this.unsentMessages.remove(Integer.valueOf(id));
        if (prevSize != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long did) {
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                if (messageObject.messageOwner.message != null) {
                    WebPage webPage = null;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                        webPage = messageObject.messageOwner.media.webpage;
                    }
                    sendMessage(messageObject.messageOwner.message, did, messageObject.replyMessageObject, webPage, true, messageObject.messageOwner.entities, null, null);
                    return;
                }
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, did);
            } else if (messageObject.messageOwner.media.photo instanceof TL_photo) {
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, null, null);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, did, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                User user = new TL_userContact_old2();
                user.phone = messageObject.messageOwner.media.phone_number;
                user.first_name = messageObject.messageOwner.media.first_name;
                user.last_name = messageObject.messageOwner.media.last_name;
                user.id = messageObject.messageOwner.media.user_id;
                sendMessage(user, did, messageObject.replyMessageObject, null, null);
            } else {
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, did);
            }
        }
    }

    public void sendSticker(Document document, long peer, MessageObject replyingMessageObject) {
        if (document != null) {
            if (((int) peer) == 0) {
                if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (peer >> 32))) == null) {
                    return;
                }
                if (document.thumb instanceof TL_photoSize) {
                    File file = FileLoader.getPathToAttach(document.thumb, true);
                    if (file.exists()) {
                        try {
                            int len = (int) file.length();
                            byte[] arr = new byte[((int) file.length())];
                            new RandomAccessFile(file, "r").readFully(arr);
                            Document newDocument = new TL_document();
                            newDocument.thumb = new TL_photoCachedSize();
                            newDocument.thumb.location = document.thumb.location;
                            newDocument.thumb.size = document.thumb.size;
                            newDocument.thumb.f34w = document.thumb.f34w;
                            newDocument.thumb.f33h = document.thumb.f33h;
                            newDocument.thumb.type = document.thumb.type;
                            newDocument.thumb.bytes = arr;
                            newDocument.id = document.id;
                            newDocument.access_hash = document.access_hash;
                            newDocument.date = document.date;
                            newDocument.mime_type = document.mime_type;
                            newDocument.size = document.size;
                            newDocument.dc_id = document.dc_id;
                            newDocument.attributes = document.attributes;
                            if (newDocument.mime_type == null) {
                                newDocument.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            document = newDocument;
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                }
            }
            getInstance().sendMessage((TL_document) document, null, null, peer, replyingMessageObject, null, null);
        }
    }

    public void sendMessage(ArrayList<MessageObject> messages, long peer) {
        if (((int) peer) != 0 && messages != null && !messages.isEmpty()) {
            Chat chat;
            int lower_id = (int) peer;
            Peer to_id = MessagesController.getPeer((int) peer);
            boolean isMegagroup = false;
            boolean isSignature = false;
            if (lower_id <= 0) {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                if (ChatObject.isChannel(chat)) {
                    isMegagroup = chat.megagroup;
                    isSignature = chat.signatures;
                }
            } else if (MessagesController.getInstance().getUser(Integer.valueOf(lower_id)) == null) {
                return;
            }
            ArrayList<MessageObject> objArr = new ArrayList();
            ArrayList<Message> arr = new ArrayList();
            ArrayList<Long> randomIds = new ArrayList();
            ArrayList<Integer> ids = new ArrayList();
            HashMap<Long, Message> messagesByRandomIds = new HashMap();
            InputPeer inputPeer = MessagesController.getInputPeer(lower_id);
            int a = 0;
            while (a < messages.size()) {
                MessageObject msgObj = (MessageObject) messages.get(a);
                if (msgObj.getId() > 0) {
                    Message newMsg = new TL_message();
                    if (msgObj.isForwarded()) {
                        newMsg.fwd_from = msgObj.messageOwner.fwd_from;
                    } else {
                        newMsg.fwd_from = new TL_messageFwdHeader();
                        TL_messageFwdHeader tL_messageFwdHeader;
                        if (msgObj.isFromUser()) {
                            newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                            tL_messageFwdHeader = newMsg.fwd_from;
                            tL_messageFwdHeader.flags |= 1;
                        } else {
                            newMsg.fwd_from.channel_id = msgObj.messageOwner.to_id.channel_id;
                            tL_messageFwdHeader = newMsg.fwd_from;
                            tL_messageFwdHeader.flags |= 2;
                            if (msgObj.messageOwner.post) {
                                newMsg.fwd_from.channel_post = msgObj.getId();
                                tL_messageFwdHeader = newMsg.fwd_from;
                                tL_messageFwdHeader.flags |= 4;
                                if (msgObj.messageOwner.from_id > 0) {
                                    newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                    tL_messageFwdHeader = newMsg.fwd_from;
                                    tL_messageFwdHeader.flags |= 1;
                                }
                            }
                        }
                        newMsg.date = msgObj.messageOwner.date;
                    }
                    newMsg.media = msgObj.messageOwner.media;
                    newMsg.flags = 4;
                    if (newMsg.media != null) {
                        newMsg.flags |= MessagesController.UPDATE_MASK_SELECT_DIALOG;
                    }
                    if (isMegagroup) {
                        newMsg.flags |= LinearLayoutManager.INVALID_OFFSET;
                    }
                    if (msgObj.messageOwner.via_bot_id != 0) {
                        newMsg.via_bot_id = msgObj.messageOwner.via_bot_id;
                        newMsg.flags |= MessagesController.UPDATE_MASK_NEW_MESSAGE;
                    }
                    newMsg.message = msgObj.messageOwner.message;
                    newMsg.fwd_msg_id = msgObj.getId();
                    newMsg.attachPath = msgObj.messageOwner.attachPath;
                    newMsg.entities = msgObj.messageOwner.entities;
                    if (!newMsg.entities.isEmpty()) {
                        newMsg.flags |= MessagesController.UPDATE_MASK_USER_PHONE;
                    }
                    if (newMsg.attachPath == null) {
                        newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    int newMessageId = UserConfig.getNewMessageId();
                    newMsg.id = newMessageId;
                    newMsg.local_id = newMessageId;
                    newMsg.out = true;
                    if (to_id.channel_id == 0 || isMegagroup) {
                        newMsg.from_id = UserConfig.getClientUserId();
                        newMsg.flags |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                    } else {
                        newMsg.from_id = isSignature ? UserConfig.getClientUserId() : -to_id.channel_id;
                        newMsg.post = true;
                    }
                    if (newMsg.random_id == 0) {
                        newMsg.random_id = getNextRandomId();
                    }
                    randomIds.add(Long.valueOf(newMsg.random_id));
                    messagesByRandomIds.put(Long.valueOf(newMsg.random_id), newMsg);
                    ids.add(Integer.valueOf(newMsg.fwd_msg_id));
                    newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
                    if (!(inputPeer instanceof TL_inputPeerChannel)) {
                        if ((msgObj.messageOwner.flags & MessagesController.UPDATE_MASK_PHONE) != 0) {
                            newMsg.views = msgObj.messageOwner.views;
                            newMsg.flags |= MessagesController.UPDATE_MASK_PHONE;
                        }
                        newMsg.unread = true;
                    } else if (isMegagroup) {
                        newMsg.unread = true;
                    } else {
                        newMsg.views = 1;
                        newMsg.flags |= MessagesController.UPDATE_MASK_PHONE;
                    }
                    newMsg.dialog_id = peer;
                    newMsg.to_id = to_id;
                    if (MessageObject.isVoiceMessage(newMsg) && newMsg.to_id.channel_id == 0) {
                        newMsg.media_unread = true;
                    }
                    if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                        newMsg.ttl = -msgObj.messageOwner.to_id.channel_id;
                    }
                    MessageObject messageObject = new MessageObject(newMsg, null, true);
                    messageObject.messageOwner.send_state = 1;
                    objArr.add(messageObject);
                    arr.add(newMsg);
                    putToSendingMessages(newMsg);
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m11e("tmessages", "forward message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
                    }
                    if (!(arr.size() == 100 || a == messages.size() - 1)) {
                        if (a != messages.size() - 1) {
                            if (((MessageObject) messages.get(a + 1)).getDialogId() == msgObj.getDialogId()) {
                            }
                        }
                    }
                    MessagesStorage.getInstance().putMessages(new ArrayList(arr), false, true, false, 0);
                    MessagesController.getInstance().updateInterfaceWithMessages(peer, objArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    UserConfig.saveConfig(false);
                    TLObject req = new TL_messages_forwardMessages();
                    req.to_peer = inputPeer;
                    if (req.to_peer instanceof TL_inputPeerChannel) {
                        req.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
                    }
                    if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(msgObj.messageOwner.to_id.channel_id));
                        req.from_peer = new TL_inputPeerChannel();
                        req.from_peer.channel_id = msgObj.messageOwner.to_id.channel_id;
                        if (chat != null) {
                            req.from_peer.access_hash = chat.access_hash;
                        }
                    } else {
                        req.from_peer = new TL_inputPeerEmpty();
                    }
                    req.random_id = randomIds;
                    req.id = ids;
                    ArrayList<Message> newMsgObjArr = arr;
                    ArrayList<MessageObject> newMsgArr = objArr;
                    HashMap<Long, Message> messagesByRandomIdsFinal = messagesByRandomIds;
                    boolean isMegagroupFinal = isMegagroup;
                    ConnectionsManager.getInstance().sendRequest(req, new C16914(peer, isMegagroupFinal, messagesByRandomIdsFinal, newMsgArr, newMsgObjArr, to_id), 68);
                    if (a != messages.size() - 1) {
                        objArr = new ArrayList();
                        arr = new ArrayList();
                        randomIds = new ArrayList();
                        ids = new ArrayList();
                        messagesByRandomIds = new HashMap();
                    }
                }
                a++;
            }
        }
    }

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, BaseFragment fragment, ArrayList<MessageEntity> entities, Runnable callback) {
        boolean z = false;
        if (fragment == null || fragment.getParentActivity() == null || callback == null) {
            return 0;
        }
        TL_messages_editMessage req = new TL_messages_editMessage();
        req.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
        req.message = message;
        req.flags |= MessagesController.UPDATE_MASK_NEW_MESSAGE;
        req.id = messageObject.getId();
        if (!searchLinks) {
            z = true;
        }
        req.no_webpage = z;
        if (entities != null) {
            req.entities = entities;
            req.flags |= 8;
        }
        return ConnectionsManager.getInstance().sendRequest(req, new C16925(callback, fragment));
    }

    private void sendLocation(Location location) {
        MessageMedia mediaGeo = new TL_messageMediaGeo();
        mediaGeo.geo = new TL_geoPoint();
        mediaGeo.geo.lat = location.getLatitude();
        mediaGeo.geo._long = location.getLongitude();
        for (Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) entry.getValue();
            getInstance().sendMessage(mediaGeo, messageObject.getDialogId(), messageObject, null, null);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, KeyboardButton button) {
        this.waitingForLocation.put(messageObject.getId() + "_" + Utilities.bytesToHex(button.data), messageObject);
        this.locationProvider.start();
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, KeyboardButton button) {
        return (messageObject == null || button == null || !this.waitingForLocation.containsKey(messageObject.getId() + "_" + Utilities.bytesToHex(button.data))) ? false : true;
    }

    public void sendCallback(MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment) {
        if (messageObject != null && button != null && parentFragment != null) {
            String key = messageObject.getId() + "_" + Utilities.bytesToHex(button.data);
            this.waitingForCallback.put(key, messageObject);
            TL_messages_getBotCallbackAnswer req = new TL_messages_getBotCallbackAnswer();
            req.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
            req.msg_id = messageObject.getId();
            req.data = button.data;
            ConnectionsManager.getInstance().sendRequest(req, new C16936(parentFragment, messageObject, key), 2);
        }
    }

    public boolean isSendingCallback(MessageObject messageObject, KeyboardButton button) {
        return (messageObject == null || button == null || !this.waitingForCallback.containsKey(messageObject.getId() + "_" + Utilities.bytesToHex(button.data))) ? false : true;
    }

    public void sendMessage(MessageObject retryMessageObject) {
        MessageObject messageObject = retryMessageObject;
        sendMessage(null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, true, messageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params);
    }

    public void sendMessage(User user, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, user, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, videoEditedInfo, null, document, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(message, null, null, null, null, null, peer, null, reply_to_msg, webPage, searchLinks, null, entities, replyMarkup, params);
    }

    public void sendMessage(MessageMedia location, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, location, null, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(TL_photo photo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, photo, null, null, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMessage(java.lang.String r54, org.telegram.tgnet.TLRPC.MessageMedia r55, org.telegram.tgnet.TLRPC.TL_photo r56, org.telegram.messenger.VideoEditedInfo r57, org.telegram.tgnet.TLRPC.User r58, org.telegram.tgnet.TLRPC.TL_document r59, long r60, java.lang.String r62, org.telegram.messenger.MessageObject r63, org.telegram.tgnet.TLRPC.WebPage r64, boolean r65, org.telegram.messenger.MessageObject r66, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r67, org.telegram.tgnet.TLRPC.ReplyMarkup r68, java.util.HashMap<java.lang.String, java.lang.String> r69) {
        /*
        r53 = this;
        r8 = 0;
        r4 = (r60 > r8 ? 1 : (r60 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r41 = 0;
        if (r69 == 0) goto L_0x001f;
    L_0x000b:
        r4 = "originalPath";
        r0 = r69;
        r4 = r0.containsKey(r4);
        if (r4 == 0) goto L_0x001f;
    L_0x0015:
        r4 = "originalPath";
        r0 = r69;
        r41 = r0.get(r4);
        r41 = (java.lang.String) r41;
    L_0x001f:
        r36 = 0;
        r38 = 0;
        r51 = -1;
        r0 = r60;
        r0 = (int) r0;
        r33 = r0;
        r4 = 32;
        r8 = r60 >> r4;
        r0 = (int) r8;
        r28 = r0;
        r31 = 0;
        r16 = 0;
        if (r33 == 0) goto L_0x0082;
    L_0x0037:
        r46 = org.telegram.messenger.MessagesController.getInputPeer(r33);
    L_0x003b:
        r47 = 0;
        if (r33 != 0) goto L_0x0085;
    L_0x003f:
        r4 = org.telegram.messenger.MessagesController.getInstance();
        r6 = java.lang.Integer.valueOf(r28);
        r16 = r4.getEncryptedChat(r6);
        if (r16 != 0) goto L_0x00a5;
    L_0x004d:
        if (r66 == 0) goto L_0x0006;
    L_0x004f:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r66;
        r6 = r0.messageOwner;
        r4.markMessageAsSendError(r6);
        r0 = r66;
        r4 = r0.messageOwner;
        r6 = 2;
        r4.send_state = r6;
        r4 = org.telegram.messenger.NotificationCenter.getInstance();
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r11 = r66.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r8[r9] = r11;
        r4.postNotificationName(r6, r8);
        r4 = r66.getId();
        r0 = r53;
        r0.processSentMessage(r4);
        goto L_0x0006;
    L_0x0082:
        r46 = 0;
        goto L_0x003b;
    L_0x0085:
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r4 == 0) goto L_0x00a5;
    L_0x008b:
        r4 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r46;
        r6 = r0.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r24 = r4.getChat(r6);
        if (r24 == 0) goto L_0x020e;
    L_0x009d:
        r0 = r24;
        r4 = r0.megagroup;
        if (r4 != 0) goto L_0x020e;
    L_0x00a3:
        r31 = 1;
    L_0x00a5:
        if (r66 == 0) goto L_0x02ea;
    L_0x00a7:
        r0 = r66;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x01d2 }
        r36 = r0;
        r4 = r66.isForwarded();	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0212;
    L_0x00b3:
        r51 = 4;
    L_0x00b5:
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x01d2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x00c7;
    L_0x00bf:
        r8 = r53.getNextRandomId();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.random_id = r8;	 Catch:{ Exception -> 0x01d2 }
    L_0x00c7:
        if (r69 == 0) goto L_0x00f9;
    L_0x00c9:
        r4 = "bot";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x00f9;
    L_0x00d3:
        if (r16 == 0) goto L_0x064c;
    L_0x00d5:
        r4 = "bot_name";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x01d2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.via_bot_name = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.via_bot_name;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x00ef;
    L_0x00e9:
        r4 = "";
        r0 = r36;
        r0.via_bot_name = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x00ef:
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 2048;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x00f9:
        r0 = r69;
        r1 = r36;
        r1.params = r0;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance();	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.getCurrentTime();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.date = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 512;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r46;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0679;
    L_0x011b:
        if (r31 == 0) goto L_0x012c;
    L_0x011d:
        r4 = 1;
        r0 = r36;
        r0.views = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 1024;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x012c:
        r4 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x01d2 }
        r0 = r46;
        r6 = r0.channel_id;	 Catch:{ Exception -> 0x01d2 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x01d2 }
        r24 = r4.getChat(r6);	 Catch:{ Exception -> 0x01d2 }
        if (r24 == 0) goto L_0x0154;
    L_0x013e:
        r0 = r24;
        r4 = r0.megagroup;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0664;
    L_0x0144:
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r6 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r4 = r4 | r6;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
        r4 = 1;
        r0 = r36;
        r0.unread = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0154:
        r0 = r60;
        r2 = r36;
        r2.dialog_id = r0;	 Catch:{ Exception -> 0x01d2 }
        if (r63 == 0) goto L_0x0186;
    L_0x015c:
        if (r16 == 0) goto L_0x0680;
    L_0x015e:
        r0 = r63;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x01d2 }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x01d2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x0680;
    L_0x016a:
        r0 = r63;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x01d2 }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.reply_to_random_id = r8;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 8;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x017e:
        r4 = r63.getId();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0186:
        if (r68 == 0) goto L_0x019a;
    L_0x0188:
        if (r16 != 0) goto L_0x019a;
    L_0x018a:
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 64;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r68;
        r1 = r36;
        r1.reply_markup = r0;	 Catch:{ Exception -> 0x01d2 }
    L_0x019a:
        if (r33 == 0) goto L_0x080f;
    L_0x019c:
        r4 = 1;
        r0 = r28;
        if (r0 != r4) goto L_0x07df;
    L_0x01a1:
        r0 = r53;
        r4 = r0.currentChatInfo;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x068c;
    L_0x01a7:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4.markMessageAsSendError(r0);	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance();	 Catch:{ Exception -> 0x01d2 }
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;	 Catch:{ Exception -> 0x01d2 }
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x01d2 }
        r9 = 0;
        r0 = r36;
        r11 = r0.id;	 Catch:{ Exception -> 0x01d2 }
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x01d2 }
        r8[r9] = r11;	 Catch:{ Exception -> 0x01d2 }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.id;	 Catch:{ Exception -> 0x01d2 }
        r0 = r53;
        r0.processSentMessage(r4);	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0006;
    L_0x01d2:
        r27 = move-exception;
        r12 = r38;
    L_0x01d5:
        r4 = "tmessages";
        r0 = r27;
        org.telegram.messenger.FileLog.m13e(r4, r0);
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r36;
        r4.markMessageAsSendError(r0);
        if (r12 == 0) goto L_0x01ec;
    L_0x01e7:
        r4 = r12.messageOwner;
        r6 = 2;
        r4.send_state = r6;
    L_0x01ec:
        r4 = org.telegram.messenger.NotificationCenter.getInstance();
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r36;
        r11 = r0.id;
        r11 = java.lang.Integer.valueOf(r11);
        r8[r9] = r11;
        r4.postNotificationName(r6, r8);
        r0 = r36;
        r4 = r0.id;
        r0 = r53;
        r0.processSentMessage(r4);
        goto L_0x0006;
    L_0x020e:
        r31 = 0;
        goto L_0x00a5;
    L_0x0212:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x0230;
    L_0x0218:
        r0 = r36;
        r0 = r0.message;	 Catch:{ Exception -> 0x01d2 }
        r54 = r0;
        r51 = 0;
    L_0x0220:
        if (r69 == 0) goto L_0x00b5;
    L_0x0222:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x00b5;
    L_0x022c:
        r51 = 9;
        goto L_0x00b5;
    L_0x0230:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 4;
        if (r4 != r6) goto L_0x0240;
    L_0x0237:
        r0 = r36;
        r0 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r55 = r0;
        r51 = 1;
        goto L_0x0220;
    L_0x0240:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 1;
        if (r4 != r6) goto L_0x0255;
    L_0x0247:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.photo;	 Catch:{ Exception -> 0x01d2 }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x01d2 }
        r56 = r0;
        r51 = 2;
        goto L_0x0220;
    L_0x0255:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 3;
        if (r4 != r6) goto L_0x026a;
    L_0x025c:
        r51 = 3;
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x01d2 }
        r59 = r0;
        goto L_0x0220;
    L_0x026a:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 12;
        if (r4 != r6) goto L_0x02a5;
    L_0x0272:
        r52 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x01d2 }
        r52.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x176c }
        r4 = r4.phone_number;	 Catch:{ Exception -> 0x176c }
        r0 = r52;
        r0.phone = r4;	 Catch:{ Exception -> 0x176c }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x176c }
        r4 = r4.first_name;	 Catch:{ Exception -> 0x176c }
        r0 = r52;
        r0.first_name = r4;	 Catch:{ Exception -> 0x176c }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x176c }
        r4 = r4.last_name;	 Catch:{ Exception -> 0x176c }
        r0 = r52;
        r0.last_name = r4;	 Catch:{ Exception -> 0x176c }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x176c }
        r4 = r4.user_id;	 Catch:{ Exception -> 0x176c }
        r0 = r52;
        r0.id = r4;	 Catch:{ Exception -> 0x176c }
        r51 = 6;
        r58 = r52;
        goto L_0x0220;
    L_0x02a5:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 8;
        if (r4 == r6) goto L_0x02c5;
    L_0x02ad:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 9;
        if (r4 == r6) goto L_0x02c5;
    L_0x02b5:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 13;
        if (r4 == r6) goto L_0x02c5;
    L_0x02bd:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 14;
        if (r4 != r6) goto L_0x02d4;
    L_0x02c5:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x01d2 }
        r59 = r0;
        r51 = 7;
        goto L_0x0220;
    L_0x02d4:
        r0 = r66;
        r4 = r0.type;	 Catch:{ Exception -> 0x01d2 }
        r6 = 2;
        if (r4 != r6) goto L_0x0220;
    L_0x02db:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x01d2 }
        r59 = r0;
        r51 = 8;
        goto L_0x0220;
    L_0x02ea:
        if (r54 == 0) goto L_0x039c;
    L_0x02ec:
        if (r16 == 0) goto L_0x037b;
    L_0x02ee:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 17;
        if (r4 < r6) goto L_0x037b;
    L_0x02fa:
        r37 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
    L_0x0301:
        if (r67 == 0) goto L_0x030f;
    L_0x0303:
        r4 = r67.isEmpty();	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x030f;
    L_0x0309:
        r0 = r67;
        r1 = r36;
        r1.entities = r0;	 Catch:{ Exception -> 0x01d2 }
    L_0x030f:
        if (r16 == 0) goto L_0x032c;
    L_0x0311:
        r0 = r64;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x032c;
    L_0x0317:
        r0 = r64;
        r4 = r0.url;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0384;
    L_0x031d:
        r39 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x01d2 }
        r39.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r64;
        r4 = r0.url;	 Catch:{ Exception -> 0x01d2 }
        r0 = r39;
        r0.url = r4;	 Catch:{ Exception -> 0x01d2 }
        r64 = r39;
    L_0x032c:
        if (r64 != 0) goto L_0x0387;
    L_0x032e:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.media = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0337:
        if (r69 == 0) goto L_0x0399;
    L_0x0339:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0399;
    L_0x0343:
        r51 = 9;
    L_0x0345:
        r0 = r54;
        r1 = r36;
        r1.message = r0;	 Catch:{ Exception -> 0x01d2 }
    L_0x034b:
        r0 = r36;
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x0357;
    L_0x0351:
        r4 = "";
        r0 = r36;
        r0.attachPath = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0357:
        r4 = org.telegram.messenger.UserConfig.getNewMessageId();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.id = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.local_id = r4;	 Catch:{ Exception -> 0x01d2 }
        r4 = 1;
        r0 = r36;
        r0.out = r4;	 Catch:{ Exception -> 0x01d2 }
        if (r31 == 0) goto L_0x0638;
    L_0x036a:
        if (r46 == 0) goto L_0x0638;
    L_0x036c:
        r0 = r46;
        r4 = r0.channel_id;	 Catch:{ Exception -> 0x01d2 }
        r4 = -r4;
        r0 = r36;
        r0.from_id = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0375:
        r4 = 0;
        org.telegram.messenger.UserConfig.saveConfig(r4);	 Catch:{ Exception -> 0x01d2 }
        goto L_0x00b5;
    L_0x037b:
        r37 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
        goto L_0x0301;
    L_0x0384:
        r64 = 0;
        goto L_0x032c;
    L_0x0387:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.media = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r64;
        r4.webpage = r0;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0337;
    L_0x0399:
        r51 = 0;
        goto L_0x0345;
    L_0x039c:
        if (r55 == 0) goto L_0x03db;
    L_0x039e:
        if (r16 == 0) goto L_0x03cf;
    L_0x03a0:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 17;
        if (r4 < r6) goto L_0x03cf;
    L_0x03ac:
        r37 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
    L_0x03b3:
        r0 = r55;
        r1 = r36;
        r1.media = r0;	 Catch:{ Exception -> 0x01d2 }
        r4 = "";
        r0 = r36;
        r0.message = r4;	 Catch:{ Exception -> 0x01d2 }
        if (r69 == 0) goto L_0x03d7;
    L_0x03c1:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x03d7;
    L_0x03cb:
        r51 = 9;
        goto L_0x034b;
    L_0x03cf:
        r37 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
        goto L_0x03b3;
    L_0x03d7:
        r51 = 1;
        goto L_0x034b;
    L_0x03db:
        if (r56 == 0) goto L_0x0478;
    L_0x03dd:
        if (r16 == 0) goto L_0x0441;
    L_0x03df:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 17;
        if (r4 < r6) goto L_0x0441;
    L_0x03eb:
        r37 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
    L_0x03f2:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.media = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r6 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0449;
    L_0x0405:
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x01d2 }
    L_0x0409:
        r6.caption = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r56;
        r4.photo = r0;	 Catch:{ Exception -> 0x01d2 }
        if (r69 == 0) goto L_0x044c;
    L_0x0415:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x044c;
    L_0x041f:
        r51 = 9;
    L_0x0421:
        r4 = "-1";
        r0 = r36;
        r0.message = r4;	 Catch:{ Exception -> 0x01d2 }
        if (r62 == 0) goto L_0x044f;
    L_0x0429:
        r4 = r62.length();	 Catch:{ Exception -> 0x01d2 }
        if (r4 <= 0) goto L_0x044f;
    L_0x042f:
        r4 = "http";
        r0 = r62;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x044f;
    L_0x0439:
        r0 = r62;
        r1 = r36;
        r1.attachPath = r0;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x034b;
    L_0x0441:
        r37 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
        goto L_0x03f2;
    L_0x0449:
        r4 = "";
        goto L_0x0409;
    L_0x044c:
        r51 = 2;
        goto L_0x0421;
    L_0x044f:
        r0 = r56;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x01d2 }
        r0 = r56;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x01d2 }
        r6 = r6.size();	 Catch:{ Exception -> 0x01d2 }
        r6 = r6 + -1;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x01d2 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r4.location;	 Catch:{ Exception -> 0x01d2 }
        r32 = r0;
        r4 = 1;
        r0 = r32;
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4);	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.attachPath = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x034b;
    L_0x0478:
        if (r58 == 0) goto L_0x050a;
    L_0x047a:
        if (r16 == 0) goto L_0x04fe;
    L_0x047c:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 17;
        if (r4 < r6) goto L_0x04fe;
    L_0x0488:
        r37 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
    L_0x048f:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.media = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r58;
        r6 = r0.phone;	 Catch:{ Exception -> 0x01d2 }
        r4.phone_number = r6;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r58;
        r6 = r0.first_name;	 Catch:{ Exception -> 0x01d2 }
        r4.first_name = r6;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r58;
        r6 = r0.last_name;	 Catch:{ Exception -> 0x01d2 }
        r4.last_name = r6;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r58;
        r6 = r0.id;	 Catch:{ Exception -> 0x01d2 }
        r4.user_id = r6;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.first_name;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x04d4;
    L_0x04c8:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r6 = "";
        r4.first_name = r6;	 Catch:{ Exception -> 0x01d2 }
        r0 = r58;
        r0.first_name = r6;	 Catch:{ Exception -> 0x01d2 }
    L_0x04d4:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.last_name;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x04e8;
    L_0x04dc:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r6 = "";
        r4.last_name = r6;	 Catch:{ Exception -> 0x01d2 }
        r0 = r58;
        r0.last_name = r6;	 Catch:{ Exception -> 0x01d2 }
    L_0x04e8:
        r4 = "";
        r0 = r36;
        r0.message = r4;	 Catch:{ Exception -> 0x01d2 }
        if (r69 == 0) goto L_0x0506;
    L_0x04f0:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0506;
    L_0x04fa:
        r51 = 9;
        goto L_0x034b;
    L_0x04fe:
        r37 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
        goto L_0x048f;
    L_0x0506:
        r51 = 6;
        goto L_0x034b;
    L_0x050a:
        if (r59 == 0) goto L_0x034b;
    L_0x050c:
        if (r16 == 0) goto L_0x05bd;
    L_0x050e:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 17;
        if (r4 < r6) goto L_0x05bd;
    L_0x051a:
        r37 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
    L_0x0521:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.media = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r6 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x05c6;
    L_0x0534:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x01d2 }
    L_0x0538:
        r6.caption = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r0 = r59;
        r4.document = r0;	 Catch:{ Exception -> 0x01d2 }
        if (r69 == 0) goto L_0x05ca;
    L_0x0544:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x05ca;
    L_0x054e:
        r51 = 9;
    L_0x0550:
        if (r57 != 0) goto L_0x05e2;
    L_0x0552:
        r4 = "-1";
        r0 = r36;
        r0.message = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0558:
        if (r16 == 0) goto L_0x05ec;
    L_0x055a:
        r0 = r59;
        r4 = r0.dc_id;	 Catch:{ Exception -> 0x01d2 }
        if (r4 <= 0) goto L_0x05ec;
    L_0x0560:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r59);	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x05ec;
    L_0x0566:
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r59);	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.attachPath = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0572:
        if (r16 == 0) goto L_0x034b;
    L_0x0574:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r59);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x034b;
    L_0x057a:
        r20 = 0;
    L_0x057c:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.size();	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x034b;
    L_0x0588:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        r22 = r4.get(r0);	 Catch:{ Exception -> 0x01d2 }
        r22 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r22;	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0634;
    L_0x059a:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 46;
        if (r4 >= r6) goto L_0x05f3;
    L_0x05a6:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        r4.remove(r0);	 Catch:{ Exception -> 0x01d2 }
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x01d2 }
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_old;	 Catch:{ Exception -> 0x01d2 }
        r6.<init>();	 Catch:{ Exception -> 0x01d2 }
        r4.add(r6);	 Catch:{ Exception -> 0x01d2 }
        goto L_0x034b;
    L_0x05bd:
        r37 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01d2 }
        r37.<init>();	 Catch:{ Exception -> 0x01d2 }
        r36 = r37;
        goto L_0x0521;
    L_0x05c6:
        r4 = "";
        goto L_0x0538;
    L_0x05ca:
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r59);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x05d4;
    L_0x05d0:
        r51 = 3;
        goto L_0x0550;
    L_0x05d4:
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r59);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x05de;
    L_0x05da:
        r51 = 8;
        goto L_0x0550;
    L_0x05de:
        r51 = 7;
        goto L_0x0550;
    L_0x05e2:
        r4 = r57.getString();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.message = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0558;
    L_0x05ec:
        r0 = r62;
        r1 = r36;
        r1.attachPath = r0;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0572;
    L_0x05f3:
        r0 = r22;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0629;
    L_0x05f9:
        r0 = r22;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x01d2 }
        r8 = r4.id;	 Catch:{ Exception -> 0x01d2 }
        r35 = org.telegram.messenger.query.StickersQuery.getStickerSetName(r8);	 Catch:{ Exception -> 0x01d2 }
        if (r35 == 0) goto L_0x061e;
    L_0x0605:
        r4 = r35.length();	 Catch:{ Exception -> 0x01d2 }
        if (r4 <= 0) goto L_0x061e;
    L_0x060b:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r0.stickerset = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x01d2 }
        r0 = r35;
        r4.short_name = r0;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x034b;
    L_0x061e:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r0.stickerset = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x034b;
    L_0x0629:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r0.stickerset = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x034b;
    L_0x0634:
        r20 = r20 + 1;
        goto L_0x057c;
    L_0x0638:
        r4 = org.telegram.messenger.UserConfig.getClientUserId();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.from_id = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 256;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0375;
    L_0x064c:
        r4 = "bot";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x01d2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.via_bot_id = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x00ef;
    L_0x0664:
        r4 = 1;
        r0 = r36;
        r0.post = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r24;
        r4 = r0.signatures;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0154;
    L_0x066f:
        r4 = org.telegram.messenger.UserConfig.getClientUserId();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.from_id = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0154;
    L_0x0679:
        r4 = 1;
        r0 = r36;
        r0.unread = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0154;
    L_0x0680:
        r0 = r36;
        r4 = r0.flags;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4 | 8;
        r0 = r36;
        r0.flags = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x017e;
    L_0x068c:
        r48 = new java.util.ArrayList;	 Catch:{ Exception -> 0x01d2 }
        r48.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r53;
        r4 = r0.currentChatInfo;	 Catch:{ Exception -> 0x06c7 }
        r4 = r4.participants;	 Catch:{ Exception -> 0x06c7 }
        r4 = r4.participants;	 Catch:{ Exception -> 0x06c7 }
        r29 = r4.iterator();	 Catch:{ Exception -> 0x06c7 }
    L_0x069d:
        r4 = r29.hasNext();	 Catch:{ Exception -> 0x06c7 }
        if (r4 == 0) goto L_0x06ce;
    L_0x06a3:
        r42 = r29.next();	 Catch:{ Exception -> 0x06c7 }
        r42 = (org.telegram.tgnet.TLRPC.ChatParticipant) r42;	 Catch:{ Exception -> 0x06c7 }
        r4 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x06c7 }
        r0 = r42;
        r6 = r0.user_id;	 Catch:{ Exception -> 0x06c7 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x06c7 }
        r49 = r4.getUser(r6);	 Catch:{ Exception -> 0x06c7 }
        r43 = org.telegram.messenger.MessagesController.getInputUser(r49);	 Catch:{ Exception -> 0x06c7 }
        if (r43 == 0) goto L_0x069d;
    L_0x06bf:
        r0 = r48;
        r1 = r43;
        r0.add(r1);	 Catch:{ Exception -> 0x06c7 }
        goto L_0x069d;
    L_0x06c7:
        r27 = move-exception;
        r47 = r48;
        r12 = r38;
        goto L_0x01d5;
    L_0x06ce:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Exception -> 0x06c7 }
        r4.<init>();	 Catch:{ Exception -> 0x06c7 }
        r0 = r36;
        r0.to_id = r4;	 Catch:{ Exception -> 0x06c7 }
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x06c7 }
        r0 = r33;
        r4.chat_id = r0;	 Catch:{ Exception -> 0x06c7 }
        r47 = r48;
    L_0x06e1:
        if (r16 == 0) goto L_0x06ef;
    L_0x06e3:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x01d2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x01d2 }
        r6 = 46;
        if (r4 < r6) goto L_0x0707;
    L_0x06ef:
        r4 = 1;
        r0 = r28;
        if (r0 == r4) goto L_0x0707;
    L_0x06f4:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r36);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x0707;
    L_0x06fa:
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x01d2 }
        if (r4 != 0) goto L_0x0707;
    L_0x0702:
        r4 = 1;
        r0 = r36;
        r0.media_unread = r4;	 Catch:{ Exception -> 0x01d2 }
    L_0x0707:
        r4 = 1;
        r0 = r36;
        r0.send_state = r4;	 Catch:{ Exception -> 0x01d2 }
        r12 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x01d2 }
        r4 = 0;
        r6 = 1;
        r0 = r36;
        r12.<init>(r0, r4, r6);	 Catch:{ Exception -> 0x01d2 }
        r0 = r63;
        r12.replyMessageObject = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = r12.isForwarded();	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x0727;
    L_0x071f:
        r4 = r12.type;	 Catch:{ Exception -> 0x08f2 }
        r6 = 3;
        if (r4 != r6) goto L_0x0727;
    L_0x0724:
        r4 = 1;
        r12.attachPathExists = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0727:
        r40 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08f2 }
        r40.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r40;
        r0.add(r12);	 Catch:{ Exception -> 0x08f2 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08f2 }
        r5.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r5.add(r0);	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r7 = 1;
        r8 = 0;
        r9 = 0;
        r4.putMessages(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r0 = r60;
        r2 = r40;
        r4.updateInterfaceWithMessages(r0, r2);	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x08f2 }
        r8 = 0;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x08f2 }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x07a9;
    L_0x0761:
        if (r46 == 0) goto L_0x07a9;
    L_0x0763:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f2 }
        r6.<init>();	 Catch:{ Exception -> 0x08f2 }
        r8 = "send message user_id = ";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r8 = r0.user_id;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r8 = " chat_id = ";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r8 = r0.chat_id;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r8 = " channel_id = ";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r8 = r0.channel_id;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r8 = " access_hash = ";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x08f2 }
        org.telegram.messenger.FileLog.m11e(r4, r6);	 Catch:{ Exception -> 0x08f2 }
    L_0x07a9:
        if (r51 == 0) goto L_0x07b5;
    L_0x07ab:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x0a46;
    L_0x07b1:
        if (r54 == 0) goto L_0x0a46;
    L_0x07b3:
        if (r16 == 0) goto L_0x0a46;
    L_0x07b5:
        if (r16 != 0) goto L_0x0971;
    L_0x07b7:
        if (r47 == 0) goto L_0x08f5;
    L_0x07b9:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r44 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08f2 }
        r44.<init>();	 Catch:{ Exception -> 0x08f2 }
        r20 = 0;
    L_0x07c5:
        r4 = r47.size();	 Catch:{ Exception -> 0x08f2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x08d7;
    L_0x07cd:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.nextLong();	 Catch:{ Exception -> 0x08f2 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r44;
        r0.add(r4);	 Catch:{ Exception -> 0x08f2 }
        r20 = r20 + 1;
        goto L_0x07c5;
    L_0x07df:
        r4 = org.telegram.messenger.MessagesController.getPeer(r33);	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.to_id = r4;	 Catch:{ Exception -> 0x01d2 }
        if (r33 <= 0) goto L_0x06e1;
    L_0x07e9:
        r4 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x01d2 }
        r6 = java.lang.Integer.valueOf(r33);	 Catch:{ Exception -> 0x01d2 }
        r49 = r4.getUser(r6);	 Catch:{ Exception -> 0x01d2 }
        if (r49 != 0) goto L_0x0802;
    L_0x07f7:
        r0 = r36;
        r4 = r0.id;	 Catch:{ Exception -> 0x01d2 }
        r0 = r53;
        r0.processSentMessage(r4);	 Catch:{ Exception -> 0x01d2 }
        goto L_0x0006;
    L_0x0802:
        r0 = r49;
        r4 = r0.bot;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x06e1;
    L_0x0808:
        r4 = 0;
        r0 = r36;
        r0.unread = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x06e1;
    L_0x080f:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x01d2 }
        r4.<init>();	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.to_id = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r16;
        r4 = r0.participant_id;	 Catch:{ Exception -> 0x01d2 }
        r6 = org.telegram.messenger.UserConfig.getClientUserId();	 Catch:{ Exception -> 0x01d2 }
        if (r4 != r6) goto L_0x0880;
    L_0x0822:
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x01d2 }
        r0 = r16;
        r6 = r0.admin_id;	 Catch:{ Exception -> 0x01d2 }
        r4.user_id = r6;	 Catch:{ Exception -> 0x01d2 }
    L_0x082c:
        r0 = r16;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.ttl = r4;	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x06e1;
    L_0x083a:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r36);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x088e;
    L_0x0840:
        r26 = 0;
        r20 = 0;
    L_0x0844:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.size();	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x0870;
    L_0x0854:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        r22 = r4.get(r0);	 Catch:{ Exception -> 0x01d2 }
        r22 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r22;	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x088b;
    L_0x086a:
        r0 = r22;
        r0 = r0.duration;	 Catch:{ Exception -> 0x01d2 }
        r26 = r0;
    L_0x0870:
        r0 = r16;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x01d2 }
        r6 = r26 + 1;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.ttl = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x06e1;
    L_0x0880:
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x01d2 }
        r0 = r16;
        r6 = r0.participant_id;	 Catch:{ Exception -> 0x01d2 }
        r4.user_id = r6;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x082c;
    L_0x088b:
        r20 = r20 + 1;
        goto L_0x0844;
    L_0x088e:
        r4 = org.telegram.messenger.MessageObject.isVideoMessage(r36);	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x06e1;
    L_0x0894:
        r26 = 0;
        r20 = 0;
    L_0x0898:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.size();	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x08c4;
    L_0x08a8:
        r0 = r36;
        r4 = r0.media;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.document;	 Catch:{ Exception -> 0x01d2 }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x01d2 }
        r0 = r20;
        r22 = r4.get(r0);	 Catch:{ Exception -> 0x01d2 }
        r22 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r22;	 Catch:{ Exception -> 0x01d2 }
        r0 = r22;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x01d2 }
        if (r4 == 0) goto L_0x08d4;
    L_0x08be:
        r0 = r22;
        r0 = r0.duration;	 Catch:{ Exception -> 0x01d2 }
        r26 = r0;
    L_0x08c4:
        r0 = r16;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x01d2 }
        r6 = r26 + 1;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x01d2 }
        r0 = r36;
        r0.ttl = r4;	 Catch:{ Exception -> 0x01d2 }
        goto L_0x06e1;
    L_0x08d4:
        r20 = r20 + 1;
        goto L_0x0898;
    L_0x08d7:
        r0 = r54;
        r7.message = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r47;
        r7.contacts = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaEmpty;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r44;
        r7.random_id = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x08f2:
        r27 = move-exception;
        goto L_0x01d5;
    L_0x08f5:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r54;
        r7.message = r0;	 Catch:{ Exception -> 0x08f2 }
        if (r66 != 0) goto L_0x096f;
    L_0x0900:
        r4 = 1;
    L_0x0901:
        r7.clear_draft = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0930;
    L_0x090b:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f2 }
        r6 = "Notifications";
        r8 = 0;
        r4 = r4.getSharedPreferences(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f2 }
        r6.<init>();	 Catch:{ Exception -> 0x08f2 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r60;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x08f2 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r7.silent = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0930:
        r0 = r46;
        r7.peer = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x08f2 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        if (r63 == 0) goto L_0x0948;
    L_0x093c:
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 1;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r63.getId();	 Catch:{ Exception -> 0x08f2 }
        r7.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0948:
        if (r65 != 0) goto L_0x094d;
    L_0x094a:
        r4 = 1;
        r7.no_webpage = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x094d:
        if (r67 == 0) goto L_0x095f;
    L_0x094f:
        r4 = r67.isEmpty();	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x095f;
    L_0x0955:
        r0 = r67;
        r7.entities = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 8;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x095f:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        if (r66 != 0) goto L_0x0006;
    L_0x0967:
        r4 = 0;
        r0 = r60;
        org.telegram.messenger.query.DraftQuery.cleanDraft(r0, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x096f:
        r4 = 0;
        goto L_0x0901;
    L_0x0971:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x0a12;
    L_0x097d:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x08f2 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x08f2 }
        if (r67 == 0) goto L_0x099a;
    L_0x098a:
        r4 = r67.isEmpty();	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x099a;
    L_0x0990:
        r0 = r67;
        r7.entities = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 128;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x099a:
        if (r63 == 0) goto L_0x09b6;
    L_0x099c:
        r0 = r63;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x09b6;
    L_0x09a8:
        r0 = r63;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x08f2 }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 8;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x09b6:
        if (r69 == 0) goto L_0x09d4;
    L_0x09b8:
        r4 = "bot_name";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x09d4;
    L_0x09c2:
        r4 = "bot_name";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x08f2 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 2048;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x09d4:
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x08f2 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r54;
        r7.message = r0;	 Catch:{ Exception -> 0x08f2 }
        if (r64 == 0) goto L_0x0a3e;
    L_0x09e0:
        r0 = r64;
        r4 = r0.url;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0a3e;
    L_0x09e6:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r64;
        r6 = r0.url;	 Catch:{ Exception -> 0x08f2 }
        r4.url = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 512;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x09fb:
        r6 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r10 = 0;
        r11 = 0;
        r9 = r16;
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x08f2 }
        if (r66 != 0) goto L_0x0006;
    L_0x0a0a:
        r4 = 0;
        r0 = r60;
        org.telegram.messenger.query.DraftQuery.cleanDraft(r0, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0a12:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 17;
        if (r4 < r6) goto L_0x0a2a;
    L_0x0a1e:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer17;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x08f2 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x09b6;
    L_0x0a2a:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer8;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = 15;
        r4 = new byte[r4];	 Catch:{ Exception -> 0x08f2 }
        r7.random_bytes = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x08f2 }
        r6 = r7.random_bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.nextBytes(r6);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x09b6;
    L_0x0a3e:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x09fb;
    L_0x0a46:
        r4 = 1;
        r0 = r51;
        if (r0 < r4) goto L_0x0a50;
    L_0x0a4b:
        r4 = 3;
        r0 = r51;
        if (r0 <= r4) goto L_0x0a63;
    L_0x0a50:
        r4 = 5;
        r0 = r51;
        if (r0 < r4) goto L_0x0a5b;
    L_0x0a55:
        r4 = 8;
        r0 = r51;
        if (r0 <= r4) goto L_0x0a63;
    L_0x0a5b:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x1635;
    L_0x0a61:
        if (r16 == 0) goto L_0x1635;
    L_0x0a63:
        if (r16 != 0) goto L_0x0f12;
    L_0x0a65:
        r30 = 0;
        r25 = 0;
        r4 = 1;
        r0 = r51;
        if (r0 != r4) goto L_0x0ae8;
    L_0x0a6e:
        r0 = r55;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0ae2;
    L_0x0a74:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r4 = r0.address;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.address = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r4 = r0.title;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.title = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r4 = r0.provider;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.provider = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r4 = r0.venue_id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.venue_id = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0a99:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.geo_point = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r4 = r0.geo_point;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.geo;	 Catch:{ Exception -> 0x08f2 }
        r8 = r6.lat;	 Catch:{ Exception -> 0x08f2 }
        r4.lat = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r4 = r0.geo_point;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.geo;	 Catch:{ Exception -> 0x08f2 }
        r8 = r6._long;	 Catch:{ Exception -> 0x08f2 }
        r4._long = r8;	 Catch:{ Exception -> 0x08f2 }
    L_0x0aba:
        if (r47 == 0) goto L_0x0e1a;
    L_0x0abc:
        r45 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x08f2 }
        r45.<init>();	 Catch:{ Exception -> 0x08f2 }
        r44 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08f2 }
        r44.<init>();	 Catch:{ Exception -> 0x08f2 }
        r20 = 0;
    L_0x0ac8:
        r4 = r47.size();	 Catch:{ Exception -> 0x08f2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x0de3;
    L_0x0ad0:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.nextLong();	 Catch:{ Exception -> 0x08f2 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r44;
        r0.add(r4);	 Catch:{ Exception -> 0x08f2 }
        r20 = r20 + 1;
        goto L_0x0ac8;
    L_0x0ae2:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0a99;
    L_0x0ae8:
        r4 = 2;
        r0 = r51;
        if (r0 == r4) goto L_0x0af5;
    L_0x0aed:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x0b9a;
    L_0x0af3:
        if (r56 == 0) goto L_0x0b9a;
    L_0x0af5:
        r0 = r56;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0b63;
    L_0x0aff:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0b44;
    L_0x0b0a:
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0b0e:
        r0 = r30;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = 0;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        if (r62 == 0) goto L_0x0b47;
    L_0x0b2c:
        r4 = r62.length();	 Catch:{ Exception -> 0x08f2 }
        if (r4 <= 0) goto L_0x0b47;
    L_0x0b32:
        r4 = "http";
        r0 = r62;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0b47;
    L_0x0b3c:
        r0 = r62;
        r1 = r25;
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0aba;
    L_0x0b44:
        r4 = "";
        goto L_0x0b0e;
    L_0x0b47:
        r0 = r56;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.size();	 Catch:{ Exception -> 0x08f2 }
        r6 = r6 + -1;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.location = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0aba;
    L_0x0b63:
        r34 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x08f2 }
        r34.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r0.id = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0b97;
    L_0x0b77:
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0b7b:
        r0 = r34;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r4.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r30 = r34;
        goto L_0x0aba;
    L_0x0b97:
        r4 = "";
        goto L_0x0b7b;
    L_0x0b9a:
        r4 = 3;
        r0 = r51;
        if (r0 != r4) goto L_0x0c44;
    L_0x0b9f:
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0c0d;
    L_0x0ba9:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0c04;
    L_0x0bb1:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedThumbDocument;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
    L_0x0bb6:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0c0a;
    L_0x0bbc:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0bc0:
        r0 = r30;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.mime_type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.attributes = r4;	 Catch:{ Exception -> 0x08f2 }
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = 1;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.location = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r1 = r25;
        r1.documentLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r57;
        r1 = r25;
        r1.videoEditedInfo = r0;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0aba;
    L_0x0c04:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0bb6;
    L_0x0c0a:
        r4 = "";
        goto L_0x0bc0;
    L_0x0c0d:
        r34 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x08f2 }
        r34.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r0.id = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0c41;
    L_0x0c21:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0c25:
        r0 = r34;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r4.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r30 = r34;
        goto L_0x0aba;
    L_0x0c41:
        r4 = "";
        goto L_0x0c25;
    L_0x0c44:
        r4 = 6;
        r0 = r51;
        if (r0 != r4) goto L_0x0c68;
    L_0x0c49:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r4 = r0.phone;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.phone_number = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r4 = r0.first_name;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.first_name = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r4 = r0.last_name;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.last_name = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0aba;
    L_0x0c68:
        r4 = 7;
        r0 = r51;
        if (r0 == r4) goto L_0x0c73;
    L_0x0c6d:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x0d5c;
    L_0x0c73:
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0d25;
    L_0x0c7d:
        if (r16 != 0) goto L_0x0cdc;
    L_0x0c7f:
        if (r41 == 0) goto L_0x0cdc;
    L_0x0c81:
        r4 = r41.length();	 Catch:{ Exception -> 0x08f2 }
        if (r4 <= 0) goto L_0x0cdc;
    L_0x0c87:
        r4 = "http";
        r0 = r41;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0cdc;
    L_0x0c91:
        if (r69 == 0) goto L_0x0cdc;
    L_0x0c93:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = "url";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = "\\|";
        r21 = r4.split(r6);	 Catch:{ Exception -> 0x08f2 }
        r0 = r21;
        r4 = r0.length;	 Catch:{ Exception -> 0x08f2 }
        r6 = 2;
        if (r4 != r6) goto L_0x0cbc;
    L_0x0cae:
        r4 = 0;
        r4 = r21[r4];	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.url = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = 1;
        r4 = r21[r4];	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.f32q = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0cbc:
        r0 = r59;
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.mime_type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.attributes = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0d22;
    L_0x0cd2:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0cd6:
        r0 = r30;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0aba;
    L_0x0cdc:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0d1c;
    L_0x0ce4:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_fileLocation;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0d1c;
    L_0x0cee:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedThumbDocument;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
    L_0x0cf3:
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = 2;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r1 = r25;
        r1.documentLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.location = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0cbc;
    L_0x0d1c:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0cf3;
    L_0x0d22:
        r4 = "";
        goto L_0x0cd6;
    L_0x0d25:
        r34 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x08f2 }
        r34.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r0.id = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r4.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0d59;
    L_0x0d4d:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0d51:
        r0 = r34;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r30 = r34;
        goto L_0x0aba;
    L_0x0d59:
        r4 = "";
        goto L_0x0d51;
    L_0x0d5c:
        r4 = 8;
        r0 = r51;
        if (r0 != r4) goto L_0x0aba;
    L_0x0d62:
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0dac;
    L_0x0d6c:
        r30 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x08f2 }
        r30.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.mime_type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r0.attributes = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0da9;
    L_0x0d87:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0d8b:
        r0 = r30;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = 3;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r1 = r25;
        r1.documentLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0aba;
    L_0x0da9:
        r4 = "";
        goto L_0x0d8b;
    L_0x0dac:
        r34 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x08f2 }
        r34.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r0.id = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0de0;
    L_0x0dc0:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x0dc4:
        r0 = r34;
        r0.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r4.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r34;
        r4 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r30 = r34;
        goto L_0x0aba;
    L_0x0de0:
        r4 = "";
        goto L_0x0dc4;
    L_0x0de3:
        r0 = r47;
        r1 = r45;
        r1.contacts = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r1 = r45;
        r1.media = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r44;
        r1 = r45;
        r1.random_id = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = "";
        r0 = r45;
        r0.message = r4;	 Catch:{ Exception -> 0x08f2 }
        if (r25 == 0) goto L_0x0e03;
    L_0x0dfd:
        r0 = r45;
        r1 = r25;
        r1.sendRequest = r0;	 Catch:{ Exception -> 0x08f2 }
    L_0x0e03:
        r7 = r45;
        if (r66 != 0) goto L_0x0e0d;
    L_0x0e07:
        r4 = 0;
        r0 = r60;
        org.telegram.messenger.query.DraftQuery.cleanDraft(r0, r4);	 Catch:{ Exception -> 0x08f2 }
    L_0x0e0d:
        r4 = 1;
        r0 = r51;
        if (r0 != r4) goto L_0x0e81;
    L_0x0e12:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0e1a:
        r45 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x08f2 }
        r45.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r1 = r45;
        r1.peer = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0e54;
    L_0x0e2d:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f2 }
        r6 = "Notifications";
        r8 = 0;
        r4 = r4.getSharedPreferences(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f2 }
        r6.<init>();	 Catch:{ Exception -> 0x08f2 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r60;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x08f2 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r45;
        r0.silent = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0e54:
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r45;
        r0.random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r30;
        r1 = r45;
        r1.media = r0;	 Catch:{ Exception -> 0x08f2 }
        if (r63 == 0) goto L_0x0e76;
    L_0x0e64:
        r0 = r45;
        r4 = r0.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 1;
        r0 = r45;
        r0.flags = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r63.getId();	 Catch:{ Exception -> 0x08f2 }
        r0 = r45;
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0e76:
        if (r25 == 0) goto L_0x0e7e;
    L_0x0e78:
        r0 = r45;
        r1 = r25;
        r1.sendRequest = r0;	 Catch:{ Exception -> 0x08f2 }
    L_0x0e7e:
        r7 = r45;
        goto L_0x0e0d;
    L_0x0e81:
        r4 = 2;
        r0 = r51;
        if (r0 != r4) goto L_0x0ea1;
    L_0x0e86:
        r0 = r56;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0e99;
    L_0x0e90:
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0e99:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0ea1:
        r4 = 3;
        r0 = r51;
        if (r0 != r4) goto L_0x0ec1;
    L_0x0ea6:
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0eb9;
    L_0x0eb0:
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0eb9:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0ec1:
        r4 = 6;
        r0 = r51;
        if (r0 != r4) goto L_0x0ece;
    L_0x0ec6:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0ece:
        r4 = 7;
        r0 = r51;
        if (r0 != r4) goto L_0x0ef1;
    L_0x0ed3:
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0ee8;
    L_0x0edd:
        if (r25 == 0) goto L_0x0ee8;
    L_0x0edf:
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0ee8:
        r0 = r53;
        r1 = r41;
        r0.performSendMessageRequest(r7, r12, r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0ef1:
        r4 = 8;
        r0 = r51;
        if (r0 != r4) goto L_0x0006;
    L_0x0ef7:
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0f0a;
    L_0x0f01:
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0f0a:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0f12:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x0fee;
    L_0x0f1e:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x08f2 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x08f2 }
        if (r67 == 0) goto L_0x0f3b;
    L_0x0f2b:
        r4 = r67.isEmpty();	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x0f3b;
    L_0x0f31:
        r0 = r67;
        r7.entities = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 128;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0f3b:
        if (r63 == 0) goto L_0x0f57;
    L_0x0f3d:
        r0 = r63;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x0f57;
    L_0x0f49:
        r0 = r63;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x08f2 }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 8;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0f57:
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 512;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0f5d:
        if (r69 == 0) goto L_0x0f7b;
    L_0x0f5f:
        r4 = "bot_name";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x0f7b;
    L_0x0f69:
        r4 = "bot_name";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x08f2 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 2048;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x0f7b:
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x08f2 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = "";
        r7.message = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = 1;
        r0 = r51;
        if (r0 != r4) goto L_0x1023;
    L_0x0f8a:
        r0 = r55;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x101b;
    L_0x0f90:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x101b;
    L_0x0f9c:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.address;	 Catch:{ Exception -> 0x08f2 }
        r4.address = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.title;	 Catch:{ Exception -> 0x08f2 }
        r4.title = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.provider;	 Catch:{ Exception -> 0x08f2 }
        r4.provider = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.venue_id;	 Catch:{ Exception -> 0x08f2 }
        r4.venue_id = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x0fc3:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.geo;	 Catch:{ Exception -> 0x08f2 }
        r8 = r6.lat;	 Catch:{ Exception -> 0x08f2 }
        r4.lat = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r55;
        r6 = r0.geo;	 Catch:{ Exception -> 0x08f2 }
        r8 = r6._long;	 Catch:{ Exception -> 0x08f2 }
        r4._long = r8;	 Catch:{ Exception -> 0x08f2 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r10 = 0;
        r11 = 0;
        r9 = r16;
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x08f2 }
    L_0x0fe4:
        if (r66 != 0) goto L_0x0006;
    L_0x0fe6:
        r4 = 0;
        r0 = r60;
        org.telegram.messenger.query.DraftQuery.cleanDraft(r0, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x0fee:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 17;
        if (r4 < r6) goto L_0x1007;
    L_0x0ffa:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer17;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x08f2 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0f5d;
    L_0x1007:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer8;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4 = 15;
        r4 = new byte[r4];	 Catch:{ Exception -> 0x08f2 }
        r7.random_bytes = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x08f2 }
        r6 = r7.random_bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.nextBytes(r6);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0f5d;
    L_0x101b:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fc3;
    L_0x1023:
        r4 = 2;
        r0 = r51;
        if (r0 == r4) goto L_0x1030;
    L_0x1028:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x117a;
    L_0x102e:
        if (r56 == 0) goto L_0x117a;
    L_0x1030:
        r0 = r56;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r50 = r4.get(r6);	 Catch:{ Exception -> 0x08f2 }
        r50 = (org.telegram.tgnet.TLRPC.PhotoSize) r50;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.size();	 Catch:{ Exception -> 0x08f2 }
        r6 = r6 + -1;
        r23 = r4.get(r6);	 Catch:{ Exception -> 0x08f2 }
        r23 = (org.telegram.tgnet.TLRPC.PhotoSize) r23;	 Catch:{ Exception -> 0x08f2 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r50);	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x1104;
    L_0x105e:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x10f6;
    L_0x106d:
        r0 = r56;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x1071:
        r6.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r50;
        r4 = r0.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x10fa;
    L_0x1079:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r50;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x1083:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r50;
        r6 = r0.f33h;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r50;
        r6 = r0.f34w;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r6 = r0.f34w;	 Catch:{ Exception -> 0x08f2 }
        r4.f27w = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r6 = r0.f33h;	 Catch:{ Exception -> 0x08f2 }
        r4.f26h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r6 = r0.size;	 Catch:{ Exception -> 0x08f2 }
        r4.size = r6;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r4 = r0.location;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.key;	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x1143;
    L_0x10b3:
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x08f2 }
        r4 = 0;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r1 = r25;
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x08f2 }
        if (r62 == 0) goto L_0x1128;
    L_0x10d7:
        r4 = r62.length();	 Catch:{ Exception -> 0x08f2 }
        if (r4 <= 0) goto L_0x1128;
    L_0x10dd:
        r4 = "http";
        r0 = r62;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x1128;
    L_0x10e7:
        r0 = r62;
        r1 = r25;
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x08f2 }
    L_0x10ed:
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x10f6:
        r4 = "";
        goto L_0x1071;
    L_0x10fa:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x1083;
    L_0x1104:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto_layer8;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r50;
        r4 = r0.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x111d;
    L_0x1111:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto_layer8) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r50;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x1083;
    L_0x111d:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto_layer8) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x1083;
    L_0x1128:
        r0 = r56;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r56;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.size();	 Catch:{ Exception -> 0x08f2 }
        r6 = r6 + -1;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.location;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.location = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x10ed;
    L_0x1143:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x08f2 }
        r10.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r4 = r0.location;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.volume_id;	 Catch:{ Exception -> 0x08f2 }
        r10.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r4 = r0.location;	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.secret;	 Catch:{ Exception -> 0x08f2 }
        r10.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r6 = r0.location;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.key;	 Catch:{ Exception -> 0x08f2 }
        r4.key = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r23;
        r6 = r0.location;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.iv;	 Catch:{ Exception -> 0x08f2 }
        r4.iv = r6;	 Catch:{ Exception -> 0x08f2 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r11 = 0;
        r9 = r16;
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x117a:
        r4 = 3;
        r0 = r51;
        if (r0 != r4) goto L_0x1306;
    L_0x117f:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r4);	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x1267;
    L_0x1192:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x125c;
    L_0x119f:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x125c;
    L_0x11a7:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x11b3:
        r6 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x12cf;
    L_0x11bb:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x11bf:
        r6.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = "video/mp4";
        r4.mime_type = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.size;	 Catch:{ Exception -> 0x08f2 }
        r4.size = r6;	 Catch:{ Exception -> 0x08f2 }
        r20 = 0;
    L_0x11d1:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.size();	 Catch:{ Exception -> 0x08f2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x1207;
    L_0x11dd:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r20;
        r22 = r4.get(r0);	 Catch:{ Exception -> 0x08f2 }
        r22 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r22;	 Catch:{ Exception -> 0x08f2 }
        r0 = r22;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x12d3;
    L_0x11ef:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r22;
        r6 = r0.f29w;	 Catch:{ Exception -> 0x08f2 }
        r4.f27w = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r22;
        r6 = r0.f28h;	 Catch:{ Exception -> 0x08f2 }
        r4.f26h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r22;
        r6 = r0.duration;	 Catch:{ Exception -> 0x08f2 }
        r4.duration = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x1207:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x12d7;
    L_0x1225:
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x08f2 }
        r4 = 1;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r1 = r25;
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r1 = r25;
        r1.documentLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r57;
        r1 = r25;
        r1.videoEditedInfo = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x125c:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x11b3;
    L_0x1267:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 17;
        if (r4 < r6) goto L_0x12a1;
    L_0x1273:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo_layer17;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x1296;
    L_0x1280:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x1296;
    L_0x1288:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo_layer17) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x11b3;
    L_0x1296:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo_layer17) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x11b3;
    L_0x12a1:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo_layer8;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x12c4;
    L_0x12ae:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x12c4;
    L_0x12b6:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo_layer8) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x11b3;
    L_0x12c4:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo_layer8) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x11b3;
    L_0x12cf:
        r4 = "";
        goto L_0x11bf;
    L_0x12d3:
        r20 = r20 + 1;
        goto L_0x11d1;
    L_0x12d7:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x08f2 }
        r10.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r10.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r10.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.key;	 Catch:{ Exception -> 0x08f2 }
        r4.key = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.iv;	 Catch:{ Exception -> 0x08f2 }
        r4.iv = r6;	 Catch:{ Exception -> 0x08f2 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r11 = 0;
        r9 = r16;
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x1306:
        r4 = 6;
        r0 = r51;
        if (r0 != r4) goto L_0x1344;
    L_0x130b:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r6 = r0.phone;	 Catch:{ Exception -> 0x08f2 }
        r4.phone_number = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r6 = r0.first_name;	 Catch:{ Exception -> 0x08f2 }
        r4.first_name = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r6 = r0.last_name;	 Catch:{ Exception -> 0x08f2 }
        r4.last_name = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r58;
        r6 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r4.user_id = r6;	 Catch:{ Exception -> 0x08f2 }
        r13 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r15 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r17 = 0;
        r18 = 0;
        r14 = r7;
        r19 = r12;
        r13.performSendEncryptedRequest(r14, r15, r16, r17, r18, r19);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x1344:
        r4 = 7;
        r0 = r51;
        if (r0 == r4) goto L_0x1351;
    L_0x1349:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x1526;
    L_0x134f:
        if (r59 == 0) goto L_0x1526;
    L_0x1351:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r59);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x13ce;
    L_0x1357:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r4.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.date;	 Catch:{ Exception -> 0x08f2 }
        r4.date = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x08f2 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.size;	 Catch:{ Exception -> 0x08f2 }
        r4.size = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.dc_id;	 Catch:{ Exception -> 0x08f2 }
        r4.dc_id = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x13c3;
    L_0x139c:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x08f2 }
        r6.<init>();	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = "s";
        r4.type = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x13b1:
        r13 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r15 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r17 = 0;
        r18 = 0;
        r14 = r7;
        r19 = r12;
        r13.performSendEncryptedRequest(r14, r15, r16, r17, r18, r19);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x13c3:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x13b1;
    L_0x13ce:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r4);	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x14a3;
    L_0x13e1:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x08f2 }
        r6 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x148b;
    L_0x13f8:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x13fc:
        r6.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x148f;
    L_0x1404:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x148f;
    L_0x140c:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x142c:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.size;	 Catch:{ Exception -> 0x08f2 }
        r4.size = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x08f2 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.key;	 Catch:{ Exception -> 0x08f2 }
        if (r4 != 0) goto L_0x14f7;
    L_0x1442:
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x08f2 }
        r4 = 2;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r1 = r25;
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x08f2 }
        if (r62 == 0) goto L_0x147c;
    L_0x1466:
        r4 = r62.length();	 Catch:{ Exception -> 0x08f2 }
        if (r4 <= 0) goto L_0x147c;
    L_0x146c:
        r4 = "http";
        r0 = r62;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x147c;
    L_0x1476:
        r0 = r62;
        r1 = r25;
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x08f2 }
    L_0x147c:
        r0 = r59;
        r1 = r25;
        r1.documentLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x148b:
        r4 = "";
        goto L_0x13fc;
    L_0x148f:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x142c;
    L_0x14a3:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument_layer8;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r59);	 Catch:{ Exception -> 0x08f2 }
        r4.file_name = r6;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x14e2;
    L_0x14b8:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x14e2;
    L_0x14c0:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x142c;
    L_0x14e2:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x142c;
    L_0x14f7:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x08f2 }
        r10.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.id;	 Catch:{ Exception -> 0x08f2 }
        r10.id = r8;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r10.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.key;	 Catch:{ Exception -> 0x08f2 }
        r4.key = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.iv;	 Catch:{ Exception -> 0x08f2 }
        r4.iv = r6;	 Catch:{ Exception -> 0x08f2 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r11 = 0;
        r9 = r16;
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x1526:
        r4 = 8;
        r0 = r51;
        if (r0 != r4) goto L_0x0fe4;
    L_0x152c:
        r25 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r1 = r53;
        r0.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r1 = r25;
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x08f2 }
        r0 = r25;
        r0.obj = r12;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r1 = r25;
        r1.documentLocation = r0;	 Catch:{ Exception -> 0x08f2 }
        r4 = 3;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 46;
        if (r4 < r6) goto L_0x15db;
    L_0x155a:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x08f2 }
        r6 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x15c4;
    L_0x1571:
        r0 = r59;
        r4 = r0.caption;	 Catch:{ Exception -> 0x08f2 }
    L_0x1575:
        r6.caption = r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x15c7;
    L_0x157d:
        r0 = r59;
        r4 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x15c7;
    L_0x1585:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.thumb;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x08f2 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x15a5:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x08f2 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.size;	 Catch:{ Exception -> 0x08f2 }
        r4.size = r6;	 Catch:{ Exception -> 0x08f2 }
        r0 = r41;
        r1 = r25;
        r1.originalPath = r0;	 Catch:{ Exception -> 0x08f2 }
    L_0x15bb:
        r0 = r53;
        r1 = r25;
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0fe4;
    L_0x15c4:
        r4 = "";
        goto L_0x1575;
    L_0x15c7:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x08f2 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = 0;
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x15a5;
    L_0x15db:
        r0 = r16;
        r4 = r0.layer;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x08f2 }
        r6 = 17;
        if (r4 < r6) goto L_0x162a;
    L_0x15e7:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaAudio;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x15ee:
        r20 = 0;
    L_0x15f0:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.size();	 Catch:{ Exception -> 0x08f2 }
        r0 = r20;
        if (r0 >= r4) goto L_0x1616;
    L_0x15fc:
        r0 = r59;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x08f2 }
        r0 = r20;
        r22 = r4.get(r0);	 Catch:{ Exception -> 0x08f2 }
        r22 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r22;	 Catch:{ Exception -> 0x08f2 }
        r0 = r22;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x1632;
    L_0x160e:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r22;
        r6 = r0.duration;	 Catch:{ Exception -> 0x08f2 }
        r4.duration = r6;	 Catch:{ Exception -> 0x08f2 }
    L_0x1616:
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r6 = "audio/ogg";
        r4.mime_type = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.media;	 Catch:{ Exception -> 0x08f2 }
        r0 = r59;
        r6 = r0.size;	 Catch:{ Exception -> 0x08f2 }
        r4.size = r6;	 Catch:{ Exception -> 0x08f2 }
        r4 = 3;
        r0 = r25;
        r0.type = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x15bb;
    L_0x162a:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaAudio_layer8;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.media = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x15ee;
    L_0x1632:
        r20 = r20 + 1;
        goto L_0x15f0;
    L_0x1635:
        r4 = 4;
        r0 = r51;
        if (r0 != r4) goto L_0x16e9;
    L_0x163a:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r7.to_peer = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r66;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.ttl;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x16d1;
    L_0x164b:
        r4 = org.telegram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x08f2 }
        r0 = r66;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x08f2 }
        r6 = -r6;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x08f2 }
        r24 = r4.getChat(r6);	 Catch:{ Exception -> 0x08f2 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.from_peer = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r7.from_peer;	 Catch:{ Exception -> 0x08f2 }
        r0 = r66;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x08f2 }
        r6 = -r6;
        r4.channel_id = r6;	 Catch:{ Exception -> 0x08f2 }
        if (r24 == 0) goto L_0x167a;
    L_0x1672:
        r4 = r7.from_peer;	 Catch:{ Exception -> 0x08f2 }
        r0 = r24;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x08f2 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x08f2 }
    L_0x167a:
        r0 = r66;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4.to_id;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x16a9;
    L_0x1684:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f2 }
        r6 = "Notifications";
        r8 = 0;
        r4 = r4.getSharedPreferences(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f2 }
        r6.<init>();	 Catch:{ Exception -> 0x08f2 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r60;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x08f2 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r7.silent = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x16a9:
        r4 = r7.random_id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x08f2 }
        r6 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x08f2 }
        r4.add(r6);	 Catch:{ Exception -> 0x08f2 }
        r4 = r66.getId();	 Catch:{ Exception -> 0x08f2 }
        if (r4 < 0) goto L_0x16d9;
    L_0x16bc:
        r4 = r7.id;	 Catch:{ Exception -> 0x08f2 }
        r6 = r66.getId();	 Catch:{ Exception -> 0x08f2 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x08f2 }
        r4.add(r6);	 Catch:{ Exception -> 0x08f2 }
    L_0x16c9:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x16d1:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x08f2 }
        r4.<init>();	 Catch:{ Exception -> 0x08f2 }
        r7.from_peer = r4;	 Catch:{ Exception -> 0x08f2 }
        goto L_0x167a;
    L_0x16d9:
        r4 = r7.id;	 Catch:{ Exception -> 0x08f2 }
        r0 = r66;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.fwd_msg_id;	 Catch:{ Exception -> 0x08f2 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x08f2 }
        r4.add(r6);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x16c9;
    L_0x16e9:
        r4 = 9;
        r0 = r51;
        if (r0 != r4) goto L_0x0006;
    L_0x16ef:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x08f2 }
        r7.<init>();	 Catch:{ Exception -> 0x08f2 }
        r0 = r46;
        r7.peer = r0;	 Catch:{ Exception -> 0x08f2 }
        r0 = r36;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x08f2 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x08f2 }
        if (r63 == 0) goto L_0x170c;
    L_0x1700:
        r4 = r7.flags;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 | 1;
        r7.flags = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = r63.getId();	 Catch:{ Exception -> 0x08f2 }
        r7.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x170c:
        r0 = r36;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x08f2 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x08f2 }
        if (r4 == 0) goto L_0x1739;
    L_0x1714:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x08f2 }
        r6 = "Notifications";
        r8 = 0;
        r4 = r4.getSharedPreferences(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f2 }
        r6.<init>();	 Catch:{ Exception -> 0x08f2 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x08f2 }
        r0 = r60;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x08f2 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x08f2 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x08f2 }
        r7.silent = r4;	 Catch:{ Exception -> 0x08f2 }
    L_0x1739:
        r4 = "query_id";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = org.telegram.messenger.Utilities.parseLong(r4);	 Catch:{ Exception -> 0x08f2 }
        r8 = r4.longValue();	 Catch:{ Exception -> 0x08f2 }
        r7.query_id = r8;	 Catch:{ Exception -> 0x08f2 }
        r4 = "id";
        r0 = r69;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x08f2 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x08f2 }
        r7.id = r4;	 Catch:{ Exception -> 0x08f2 }
        if (r66 != 0) goto L_0x1764;
    L_0x175b:
        r4 = 1;
        r7.clear_draft = r4;	 Catch:{ Exception -> 0x08f2 }
        r4 = 0;
        r0 = r60;
        org.telegram.messenger.query.DraftQuery.cleanDraft(r0, r4);	 Catch:{ Exception -> 0x08f2 }
    L_0x1764:
        r4 = 0;
        r0 = r53;
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x08f2 }
        goto L_0x0006;
    L_0x176c:
        r27 = move-exception;
        r12 = r38;
        r58 = r52;
        goto L_0x01d5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap):void");
    }

    private void performSendDelayedMessage(DelayedMessage message) {
        String location;
        if (message.type == 0) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file");
            } else if (message.sendRequest != null) {
                location = FileLoader.getPathToAttach(message.location).toString();
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, false, true);
            } else {
                location = FileLoader.getPathToAttach(message.location).toString();
                if (message.sendEncryptedRequest == null || message.location.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, true, true);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(message.location), message);
                FileLoader.getInstance().loadFile(message.location, "jpg", 0, false);
            }
        } else if (message.type == 1) {
            if (message.videoEditedInfo != null) {
                location = message.obj.messageOwner.attachPath;
                if (location == null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                }
                putToDelayedMessages(location, message);
                MediaController.getInstance().scheduleVideoConvert(message.obj);
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TL_messages_sendMedia) {
                    media = ((TL_messages_sendMedia) message.sendRequest).media;
                } else {
                    media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                }
                if (media.file == null) {
                    location = message.obj.messageOwner.attachPath;
                    if (location == null) {
                        location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                    }
                    putToDelayedMessages(location, message);
                    if (message.obj.videoEditedInfo != null) {
                        FileLoader.getInstance().uploadFile(location, false, false, message.documentLocation.size);
                        return;
                    } else {
                        FileLoader.getInstance().uploadFile(location, false, false);
                        return;
                    }
                }
                location = FileLoader.getInstance().getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, false, true);
            } else {
                location = message.obj.messageOwner.attachPath;
                if (location == null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                }
                putToDelayedMessages(location, message);
                if (message.obj.videoEditedInfo != null) {
                    FileLoader.getInstance().uploadFile(location, true, false, message.documentLocation.size);
                } else {
                    FileLoader.getInstance().uploadFile(location, true, false);
                }
            }
        } else if (message.type == 2) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "gif");
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TL_messages_sendMedia) {
                    media = ((TL_messages_sendMedia) message.sendRequest).media;
                } else {
                    media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                }
                if (media.file == null) {
                    location = message.obj.messageOwner.attachPath;
                    putToDelayedMessages(location, message);
                    if (message.sendRequest != null) {
                        FileLoader.getInstance().uploadFile(location, false, false);
                    } else {
                        FileLoader.getInstance().uploadFile(location, true, false);
                    }
                } else if (media.thumb == null && message.location != null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, false, true);
                }
            } else {
                location = message.obj.messageOwner.attachPath;
                if (message.sendEncryptedRequest == null || message.documentLocation.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, true, false);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(message.documentLocation), message);
                FileLoader.getInstance().loadFile(message.documentLocation, true, false);
            }
        } else if (message.type == 3) {
            location = message.obj.messageOwner.attachPath;
            putToDelayedMessages(location, message);
            if (message.sendRequest != null) {
                FileLoader.getInstance().uploadFile(location, false, true);
            } else {
                FileLoader.getInstance().uploadFile(location, true, true);
            }
        }
    }

    protected void stopVideoService(String path) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07337(path));
    }

    protected void putToSendingMessages(Message message) {
        this.sendingMessages.put(Integer.valueOf(message.id), message);
    }

    protected void removeFromSendingMessages(int mid) {
        this.sendingMessages.remove(Integer.valueOf(mid));
    }

    public boolean isSendingMessage(int mid) {
        return this.sendingMessages.containsKey(Integer.valueOf(mid));
    }

    private void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath) {
        int i;
        Message newMsgObj = msgObj.messageOwner;
        putToSendingMessages(newMsgObj);
        ConnectionsManager instance = ConnectionsManager.getInstance();
        RequestDelegate c16948 = new C16948(newMsgObj, req, msgObj, originalPath);
        QuickAckDelegate c16959 = new C16959(newMsgObj);
        if (req instanceof TL_messages_sendMessage) {
            i = MessagesController.UPDATE_MASK_USER_PHONE;
        } else {
            i = 0;
        }
        instance.sendRequest(req, c16948, c16959, i | 68);
    }

    private void updateMediaPaths(MessageObject newMsgObj, Message sentMessage, String originalPath, boolean post) {
        Message newMsg = newMsgObj.messageOwner;
        if (sentMessage != null) {
            int a;
            PhotoSize size;
            PhotoSize size2;
            String fileName;
            String fileName2;
            File cacheFile;
            File cacheFile2;
            if ((sentMessage.media instanceof TL_messageMediaPhoto) && sentMessage.media.photo != null && (newMsg.media instanceof TL_messageMediaPhoto) && newMsg.media.photo != null) {
                MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.photo, 0);
                if (newMsg.media.photo.sizes.size() == 1 && (((PhotoSize) newMsg.media.photo.sizes.get(0)).location instanceof TL_fileLocationUnavailable)) {
                    newMsg.media.photo.sizes = sentMessage.media.photo.sizes;
                } else {
                    for (a = 0; a < sentMessage.media.photo.sizes.size(); a++) {
                        size = (PhotoSize) sentMessage.media.photo.sizes.get(a);
                        if (!(size == null || size.location == null || (size instanceof TL_photoSizeEmpty) || size.type == null)) {
                            int b = 0;
                            while (b < newMsg.media.photo.sizes.size()) {
                                size2 = (PhotoSize) newMsg.media.photo.sizes.get(b);
                                if (size2 == null || size2.location == null || size2.type == null || !((size2.location.volume_id == -2147483648L && size.type.equals(size2.type)) || (size.f34w == size2.f34w && size.f33h == size2.f33h))) {
                                    b++;
                                } else {
                                    fileName = size2.location.volume_id + "_" + size2.location.local_id;
                                    fileName2 = size.location.volume_id + "_" + size.location.local_id;
                                    if (!fileName.equals(fileName2)) {
                                        cacheFile = new File(FileLoader.getInstance().getDirectory(4), fileName + ".jpg");
                                        if (sentMessage.media.photo.sizes.size() == 1 || size.f34w > 90 || size.f33h > 90) {
                                            cacheFile2 = FileLoader.getPathToAttach(size);
                                        } else {
                                            cacheFile2 = new File(FileLoader.getInstance().getDirectory(4), fileName2 + ".jpg");
                                        }
                                        cacheFile.renameTo(cacheFile2);
                                        ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, post);
                                        size2.location = size.location;
                                        size2.size = size.size;
                                    }
                                }
                            }
                        }
                    }
                }
                sentMessage.message = newMsg.message;
                sentMessage.attachPath = newMsg.attachPath;
                newMsg.media.photo.id = sentMessage.media.photo.id;
                newMsg.media.photo.access_hash = sentMessage.media.photo.access_hash;
            } else if ((sentMessage.media instanceof TL_messageMediaDocument) && sentMessage.media.document != null && (newMsg.media instanceof TL_messageMediaDocument) && newMsg.media.document != null) {
                DocumentAttribute attribute;
                if (MessageObject.isVideoMessage(sentMessage)) {
                    MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.document, 2);
                    sentMessage.attachPath = newMsg.attachPath;
                } else if (!MessageObject.isVoiceMessage(sentMessage)) {
                    MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.document, 1);
                }
                size2 = newMsg.media.document.thumb;
                size = sentMessage.media.document.thumb;
                if (size2 != null && size2.location != null && size2.location.volume_id == -2147483648L && size != null && size.location != null && !(size instanceof TL_photoSizeEmpty) && !(size2 instanceof TL_photoSizeEmpty)) {
                    fileName = size2.location.volume_id + "_" + size2.location.local_id;
                    fileName2 = size.location.volume_id + "_" + size.location.local_id;
                    if (!fileName.equals(fileName2)) {
                        new File(FileLoader.getInstance().getDirectory(4), fileName + ".jpg").renameTo(new File(FileLoader.getInstance().getDirectory(4), fileName2 + ".jpg"));
                        ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, post);
                        size2.location = size.location;
                        size2.size = size.size;
                    }
                } else if (size2 != null && MessageObject.isStickerMessage(sentMessage) && size2.location != null) {
                    size.location = size2.location;
                } else if ((size2 != null && (size2.location instanceof TL_fileLocationUnavailable)) || (size2 instanceof TL_photoSizeEmpty)) {
                    newMsg.media.document.thumb = sentMessage.media.document.thumb;
                }
                newMsg.media.document.dc_id = sentMessage.media.document.dc_id;
                newMsg.media.document.id = sentMessage.media.document.id;
                newMsg.media.document.access_hash = sentMessage.media.document.access_hash;
                byte[] oldWaveform = null;
                for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                    attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                    if (attribute instanceof TL_documentAttributeAudio) {
                        oldWaveform = attribute.waveform;
                        break;
                    }
                }
                newMsg.media.document.attributes = sentMessage.media.document.attributes;
                if (oldWaveform != null) {
                    for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                        attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeAudio) {
                            attribute.waveform = oldWaveform;
                            attribute.flags |= 4;
                        }
                    }
                }
                newMsg.media.document.size = sentMessage.media.document.size;
                newMsg.media.document.mime_type = sentMessage.media.document.mime_type;
                if ((sentMessage.flags & 4) == 0 && MessageObject.isOut(sentMessage) && MessageObject.isNewGifDocument(sentMessage.media.document)) {
                    MessagesController.addNewGifToRecent(sentMessage.media.document, sentMessage.date);
                }
                if (newMsg.attachPath == null || !newMsg.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath())) {
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                    return;
                }
                cacheFile = new File(newMsg.attachPath);
                cacheFile2 = FileLoader.getPathToAttach(sentMessage.media.document);
                if (!cacheFile.renameTo(cacheFile2)) {
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                } else if (MessageObject.isVideoMessage(sentMessage)) {
                    newMsgObj.attachPathExists = true;
                } else {
                    newMsgObj.mediaExists = newMsgObj.attachPathExists;
                    newMsgObj.attachPathExists = false;
                    newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                    if (originalPath != null) {
                        if (originalPath.startsWith("http")) {
                            MessagesStorage.getInstance().addRecentLocalFile(originalPath, cacheFile2.toString(), newMsg.media.document);
                        }
                    }
                }
            } else if ((sentMessage.media instanceof TL_messageMediaContact) && (newMsg.media instanceof TL_messageMediaContact)) {
                newMsg.media = sentMessage.media;
            } else if (sentMessage.media instanceof TL_messageMediaWebPage) {
                newMsg.media = sentMessage.media;
            }
        }
    }

    private void putToDelayedMessages(String location, DelayedMessage message) {
        ArrayList<DelayedMessage> arrayList = (ArrayList) this.delayedMessages.get(location);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.delayedMessages.put(location, arrayList);
        }
        arrayList.add(message);
    }

    protected ArrayList<DelayedMessage> getDelayedMessages(String location) {
        return (ArrayList) this.delayedMessages.get(location);
    }

    protected long getNextRandomId() {
        long val = 0;
        while (val == 0) {
            val = Utilities.random.nextLong();
        }
        return val;
    }

    public void checkUnsentMessages() {
        MessagesStorage.getInstance().getUnsentMessages(1000);
    }

    protected void processUnsentMessages(ArrayList<Message> messages, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        AndroidUtilities.runOnUIThread(new AnonymousClass10(users, chats, encryptedChats, messages));
    }

    public TL_photo generatePhotoSizes(String path, Uri imageUri) {
        Bitmap bitmap = ImageLoader.loadBitmap(path, imageUri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (bitmap == null && AndroidUtilities.getPhotoSize() != 800) {
            bitmap = ImageLoader.loadBitmap(path, imageUri, 800.0f, 800.0f, true);
        }
        ArrayList<PhotoSize> sizes = new ArrayList();
        PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, true);
        if (size != null) {
            sizes.add(size);
        }
        size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (size != null) {
            sizes.add(size);
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (sizes.isEmpty()) {
            return null;
        }
        UserConfig.saveConfig(false);
        TL_photo photo = new TL_photo();
        photo.date = ConnectionsManager.getInstance().getCurrentTime();
        photo.sizes = sizes;
        return photo;
    }

    private static boolean prepareSendingDocumentInternal(String path, String originalPath, Uri uri, String mime, long dialog_id, MessageObject reply_to_msg, String caption) {
        if ((path == null || path.length() == 0) && uri == null) {
            return false;
        }
        if (uri != null && AndroidUtilities.isInternalUri(uri)) {
            return false;
        }
        if (path != null && AndroidUtilities.isInternalUri(Uri.fromFile(new File(path)))) {
            return false;
        }
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        TL_documentAttributeAudio attributeAudio = null;
        if (uri != null) {
            String extension = null;
            if (mime != null) {
                extension = myMime.getExtensionFromMimeType(mime);
            }
            if (extension == null) {
                extension = "txt";
            }
            path = MediaController.copyFileToCache(uri, extension);
            if (path == null) {
                return false;
            }
        }
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            return false;
        }
        boolean isEncrypted = ((int) dialog_id) == 0;
        boolean allowSticker = !isEncrypted;
        String name = file.getName();
        String ext = TtmlNode.ANONYMOUS_REGION_ID;
        int idx = path.lastIndexOf(46);
        if (idx != -1) {
            ext = path.substring(idx + 1);
        }
        if (ext.toLowerCase().equals("mp3") || ext.toLowerCase().equals("m4a")) {
            AudioInfo audioInfo = AudioInfo.getAudioInfo(file);
            if (!(audioInfo == null || audioInfo.getDuration() == 0)) {
                if (isEncrypted) {
                    EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32)));
                    if (encryptedChat == null) {
                        return false;
                    }
                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 46) {
                        attributeAudio = new TL_documentAttributeAudio();
                    } else {
                        attributeAudio = new TL_documentAttributeAudio_old();
                    }
                } else {
                    attributeAudio = new TL_documentAttributeAudio();
                }
                attributeAudio.duration = (int) (audioInfo.getDuration() / 1000);
                attributeAudio.title = audioInfo.getTitle();
                attributeAudio.performer = audioInfo.getArtist();
                if (attributeAudio.title == null) {
                    attributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
                    attributeAudio.flags |= 1;
                }
                if (attributeAudio.performer == null) {
                    attributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
                    attributeAudio.flags |= 2;
                }
            }
        }
        if (originalPath != null) {
            if (attributeAudio != null) {
                originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
            } else {
                originalPath = originalPath + TtmlNode.ANONYMOUS_REGION_ID + file.length();
            }
        }
        TL_document tL_document = null;
        if (!isEncrypted) {
            tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 1 : 4);
            if (!(tL_document != null || path.equals(originalPath) || isEncrypted)) {
                tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(path + file.length(), !isEncrypted ? 1 : 4);
            }
        }
        if (tL_document == null) {
            tL_document = new TL_document();
            tL_document.id = 0;
            tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
            TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
            fileName.file_name = name;
            tL_document.attributes.add(fileName);
            tL_document.size = (int) file.length();
            tL_document.dc_id = 0;
            if (attributeAudio != null) {
                tL_document.attributes.add(attributeAudio);
            }
            if (ext.length() == 0) {
                tL_document.mime_type = "application/octet-stream";
            } else if (ext.toLowerCase().equals("webp")) {
                tL_document.mime_type = "image/webp";
            } else {
                String mimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                if (mimeType != null) {
                    tL_document.mime_type = mimeType;
                } else {
                    tL_document.mime_type = "application/octet-stream";
                }
            }
            if (tL_document.mime_type.equals("image/gif")) {
                try {
                    Bitmap bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                    if (bitmap != null) {
                        fileName.file_name = "animation.gif";
                        tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                        bitmap.recycle();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            if (tL_document.mime_type.equals("image/webp") && allowSticker) {
                Options bmOptions = new Options();
                try {
                    bmOptions.inJustDecodeBounds = true;
                    RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
                    ByteBuffer buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
                    Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                    randomAccessFile.close();
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                if (bmOptions.outWidth != 0 && bmOptions.outHeight != 0 && bmOptions.outWidth <= 800 && bmOptions.outHeight <= 800) {
                    TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                    attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                    attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                    tL_document.attributes.add(attributeSticker);
                    TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
                    attributeImageSize.w = bmOptions.outWidth;
                    attributeImageSize.h = bmOptions.outHeight;
                    tL_document.attributes.add(attributeImageSize);
                }
            }
            if (tL_document.thumb == null) {
                tL_document.thumb = new TL_photoSizeEmpty();
                tL_document.thumb.type = "s";
            }
        }
        tL_document.caption = caption;
        HashMap<String, String> params = new HashMap();
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass11(tL_document, path, dialog_id, reply_to_msg, params));
        return true;
    }

    public static void prepareSendingDocument(String path, String originalPath, Uri uri, String mine, long dialog_id, MessageObject reply_to_msg) {
        if ((path != null && originalPath != null) || uri != null) {
            ArrayList<String> paths = new ArrayList();
            ArrayList<String> originalPaths = new ArrayList();
            ArrayList<Uri> uris = null;
            if (uri != null) {
                uris = new ArrayList();
            }
            paths.add(path);
            originalPaths.add(originalPath);
            prepareSendingDocuments(paths, originalPaths, uris, mine, dialog_id, reply_to_msg);
        }
    }

    public static void prepareSendingAudioDocuments(ArrayList<MessageObject> messageObjects, long dialog_id, MessageObject reply_to_msg) {
        new Thread(new AnonymousClass12(messageObjects, dialog_id, reply_to_msg)).start();
    }

    public static void prepareSendingDocuments(ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String mime, long dialog_id, MessageObject reply_to_msg) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
                new Thread(new AnonymousClass13(paths, originalPaths, mime, dialog_id, reply_to_msg, uris)).start();
            }
        }
    }

    public static void prepareSendingPhoto(String imageFilePath, Uri imageUri, long dialog_id, MessageObject reply_to_msg, CharSequence caption) {
        ArrayList<String> paths = null;
        ArrayList<Uri> uris = null;
        ArrayList<String> captions = null;
        if (!(imageFilePath == null || imageFilePath.length() == 0)) {
            paths = new ArrayList();
            paths.add(imageFilePath);
        }
        if (imageUri != null) {
            uris = new ArrayList();
            uris.add(imageUri);
        }
        if (caption != null) {
            captions = new ArrayList();
            captions.add(caption.toString());
        }
        prepareSendingPhotos(paths, uris, dialog_id, reply_to_msg, captions);
    }

    public static void prepareSendingBotContextResult(BotInlineResult result, HashMap<String, String> params, long dialog_id, MessageObject reply_to_msg) {
        if (result != null) {
            if (result.send_message instanceof TL_botInlineMessageMediaAuto) {
                new Thread(new AnonymousClass14(result, params, dialog_id, reply_to_msg)).run();
            } else if (result.send_message instanceof TL_botInlineMessageText) {
                getInstance().sendMessage(result.send_message.message, dialog_id, reply_to_msg, null, !result.send_message.no_webpage, result.send_message.entities, result.send_message.reply_markup, params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaVenue) {
                MessageMedia venue = new TL_messageMediaVenue();
                venue.geo = result.send_message.geo;
                venue.address = result.send_message.address;
                venue.title = result.send_message.title;
                venue.provider = result.send_message.provider;
                venue.venue_id = result.send_message.venue_id;
                getInstance().sendMessage(venue, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaGeo) {
                MessageMedia location = new TL_messageMediaGeo();
                location.geo = result.send_message.geo;
                getInstance().sendMessage(location, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaContact) {
                User user = new TL_user();
                user.phone = result.send_message.phone_number;
                user.first_name = result.send_message.first_name;
                user.last_name = result.send_message.last_name;
                getInstance().sendMessage(user, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            }
        }
    }

    public static void prepareSendingPhotosSearch(ArrayList<SearchImage> photos, long dialog_id, MessageObject reply_to_msg) {
        if (photos != null && !photos.isEmpty()) {
            new Thread(new AnonymousClass15(dialog_id, photos, reply_to_msg)).start();
        }
    }

    private static String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static void prepareSendingText(String text, long dialog_id) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass16(text, dialog_id));
    }

    public static void prepareSendingPhotos(ArrayList<String> paths, ArrayList<Uri> uris, long dialog_id, MessageObject reply_to_msg, ArrayList<String> captions) {
        if (paths != null || uris != null) {
            if (paths != null && paths.isEmpty()) {
                return;
            }
            if (uris == null || !uris.isEmpty()) {
                ArrayList<String> pathsCopy = new ArrayList();
                ArrayList<Uri> urisCopy = new ArrayList();
                if (paths != null) {
                    pathsCopy.addAll(paths);
                }
                if (uris != null) {
                    urisCopy.addAll(uris);
                }
                new Thread(new AnonymousClass17(dialog_id, pathsCopy, urisCopy, captions, reply_to_msg)).start();
            }
        }
    }

    public static void prepareSendingVideo(String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo videoEditedInfo, long dialog_id, MessageObject reply_to_msg) {
        if (videoPath != null && videoPath.length() != 0) {
            new Thread(new AnonymousClass18(dialog_id, videoEditedInfo, videoPath, duration, height, width, estimatedSize, reply_to_msg)).start();
        }
    }
}
