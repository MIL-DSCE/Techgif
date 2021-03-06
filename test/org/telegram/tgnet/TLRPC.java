package org.telegram.tgnet;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public class TLRPC {
    public static final int CHAT_FLAG_IS_PUBLIC = 64;
    public static final int LAYER = 53;
    public static final int MESSAGE_FLAG_EDITED = 32768;
    public static final int MESSAGE_FLAG_FWD = 4;
    public static final int MESSAGE_FLAG_HAS_BOT_ID = 2048;
    public static final int MESSAGE_FLAG_HAS_ENTITIES = 128;
    public static final int MESSAGE_FLAG_HAS_FROM_ID = 256;
    public static final int MESSAGE_FLAG_HAS_MARKUP = 64;
    public static final int MESSAGE_FLAG_HAS_MEDIA = 512;
    public static final int MESSAGE_FLAG_HAS_VIEWS = 1024;
    public static final int MESSAGE_FLAG_MEGAGROUP = Integer.MIN_VALUE;
    public static final int MESSAGE_FLAG_REPLY = 8;
    public static final int USER_FLAG_ACCESS_HASH = 1;
    public static final int USER_FLAG_FIRST_NAME = 2;
    public static final int USER_FLAG_LAST_NAME = 4;
    public static final int USER_FLAG_PHONE = 16;
    public static final int USER_FLAG_PHOTO = 32;
    public static final int USER_FLAG_STATUS = 64;
    public static final int USER_FLAG_UNUSED = 128;
    public static final int USER_FLAG_UNUSED2 = 256;
    public static final int USER_FLAG_UNUSED3 = 512;
    public static final int USER_FLAG_USERNAME = 8;

    public static class Audio extends TLObject {
        public long access_hash;
        public int date;
        public int dc_id;
        public int duration;
        public long id;
        public byte[] iv;
        public byte[] key;
        public String mime_type;
        public int size;
        public int user_id;

        public static Audio TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Audio result = null;
            switch (constructor) {
                case -945003370:
                    result = new TL_audio_old2();
                    break;
                case -102543275:
                    result = new TL_audio_layer45();
                    break;
                case 1114908135:
                    result = new TL_audio_old();
                    break;
                case 1431655926:
                    result = new TL_audioEncrypted();
                    break;
                case 1483311320:
                    result = new TL_audioEmpty_layer45();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Audio", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Bool extends TLObject {
        public static Bool TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Bool result = null;
            switch (constructor) {
                case -1720552011:
                    result = new TL_boolTrue();
                    break;
                case -1132882121:
                    result = new TL_boolFalse();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Bool", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class BotInfo extends TLObject {
        public ArrayList<TL_botCommand> commands;
        public String description;
        public int user_id;
        public int version;

        public BotInfo() {
            this.commands = new ArrayList();
        }

        public static BotInfo TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            BotInfo result = null;
            switch (constructor) {
                case -1729618630:
                    result = new TL_botInfo();
                    break;
                case -1154598962:
                    result = new TL_botInfoEmpty_layer48();
                    break;
                case 164583517:
                    result = new TL_botInfo_layer48();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in BotInfo", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class BotInlineMessage extends TLObject {
        public String address;
        public String caption;
        public ArrayList<MessageEntity> entities;
        public String first_name;
        public int flags;
        public GeoPoint geo;
        public String last_name;
        public String message;
        public boolean no_webpage;
        public String phone_number;
        public String provider;
        public ReplyMarkup reply_markup;
        public String title;
        public String venue_id;

        public BotInlineMessage() {
            this.entities = new ArrayList();
        }

        public static BotInlineMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            BotInlineMessage result = null;
            switch (constructor) {
                case -1937807902:
                    result = new TL_botInlineMessageText();
                    break;
                case 175419739:
                    result = new TL_botInlineMessageMediaAuto();
                    break;
                case 904770772:
                    result = new TL_botInlineMessageMediaContact();
                    break;
                case 982505656:
                    result = new TL_botInlineMessageMediaGeo();
                    break;
                case 1130767150:
                    result = new TL_botInlineMessageMediaVenue();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in BotInlineMessage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class BotInlineResult extends TLObject {
        public String content_type;
        public String content_url;
        public String description;
        public Document document;
        public int duration;
        public int flags;
        public int f24h;
        public String id;
        public Photo photo;
        public long query_id;
        public BotInlineMessage send_message;
        public String thumb_url;
        public String title;
        public String type;
        public String url;
        public int f25w;

        public static BotInlineResult TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            BotInlineResult result = null;
            switch (constructor) {
                case -1679053127:
                    result = new TL_botInlineResult();
                    break;
                case 400266251:
                    result = new TL_botInlineMediaResult();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in BotInlineResult", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChannelMessagesFilter extends TLObject {
        public boolean exclude_new_messages;
        public int flags;
        public ArrayList<TL_messageRange> ranges;

        public ChannelMessagesFilter() {
            this.ranges = new ArrayList();
        }

        public static ChannelMessagesFilter TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChannelMessagesFilter result = null;
            switch (constructor) {
                case -1798033689:
                    result = new TL_channelMessagesFilterEmpty();
                    break;
                case -847783593:
                    result = new TL_channelMessagesFilter();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChannelMessagesFilter", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChannelParticipant extends TLObject {
        public int date;
        public int inviter_id;
        public int kicked_by;
        public int user_id;

        public static ChannelParticipant TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChannelParticipant result = null;
            switch (constructor) {
                case -1933187430:
                    result = new TL_channelParticipantKicked();
                    break;
                case -1861910545:
                    result = new TL_channelParticipantModerator();
                    break;
                case -1743180447:
                    result = new TL_channelParticipantEditor();
                    break;
                case -1557620115:
                    result = new TL_channelParticipantSelf();
                    break;
                case -471670279:
                    result = new TL_channelParticipantCreator();
                    break;
                case 367766557:
                    result = new TL_channelParticipant();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChannelParticipant", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChannelParticipantRole extends TLObject {
        public static ChannelParticipantRole TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChannelParticipantRole result = null;
            switch (constructor) {
                case -2113143156:
                    result = new TL_channelRoleEditor();
                    break;
                case -1776756363:
                    result = new TL_channelRoleModerator();
                    break;
                case -1299865402:
                    result = new TL_channelRoleEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChannelParticipantRole", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChannelParticipantsFilter extends TLObject {
        public static ChannelParticipantsFilter TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChannelParticipantsFilter result = null;
            switch (constructor) {
                case -1328445861:
                    result = new TL_channelParticipantsBots();
                    break;
                case -1268741783:
                    result = new TL_channelParticipantsAdmins();
                    break;
                case -566281095:
                    result = new TL_channelParticipantsRecent();
                    break;
                case 1010285434:
                    result = new TL_channelParticipantsKicked();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChannelParticipantsFilter", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Chat extends TLObject {
        public long access_hash;
        public String address;
        public boolean admin;
        public boolean admins_enabled;
        public boolean broadcast;
        public boolean checked_in;
        public boolean creator;
        public int date;
        public boolean deactivated;
        public boolean democracy;
        public boolean editor;
        public boolean explicit_content;
        public int flags;
        public GeoPoint geo;
        public int id;
        public boolean kicked;
        public boolean left;
        public boolean megagroup;
        public InputChannel migrated_to;
        public boolean min;
        public boolean moderator;
        public int participants_count;
        public ChatPhoto photo;
        public boolean restricted;
        public String restriction_reason;
        public boolean signatures;
        public String title;
        public String username;
        public String venue;
        public boolean verified;
        public int version;

        public static Chat TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Chat result = null;
            switch (constructor) {
                case -2059962289:
                    result = new TL_channelForbidden();
                    break;
                case -1683826688:
                    result = new TL_chatEmpty();
                    break;
                case -1588737454:
                    result = new TL_channel();
                    break;
                case -652419756:
                    result = new TL_chat();
                    break;
                case -83047359:
                    result = new TL_chatForbidden_old();
                    break;
                case 120753115:
                    result = new TL_chatForbidden();
                    break;
                case 763724588:
                    result = new TL_channelForbidden_layer52();
                    break;
                case 1260090630:
                    result = new TL_channel_layer48();
                    break;
                case 1737397639:
                    result = new TL_channel_old();
                    break;
                case 1855757255:
                    result = new TL_chat_old();
                    break;
                case 1930607688:
                    result = new TL_chat_old2();
                    break;
                case 1978329690:
                    result = new TL_geoChat();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Chat", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChatFull extends TLObject {
        public String about;
        public int admins_count;
        public ArrayList<BotInfo> bot_info;
        public boolean can_set_username;
        public boolean can_view_participants;
        public Photo chat_photo;
        public ExportedChatInvite exported_invite;
        public int flags;
        public int id;
        public int kicked_count;
        public int migrated_from_chat_id;
        public int migrated_from_max_id;
        public PeerNotifySettings notify_settings;
        public ChatParticipants participants;
        public int participants_count;
        public int pinned_msg_id;
        public int read_inbox_max_id;
        public int read_outbox_max_id;
        public int unread_count;

        public ChatFull() {
            this.bot_info = new ArrayList();
        }

        public static ChatFull TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChatFull result = null;
            switch (constructor) {
                case -1749097118:
                    result = new TL_channelFull_layer52();
                    break;
                case -1640751649:
                    result = new TL_channelFull_layer48();
                    break;
                case -1009430225:
                    result = new TL_channelFull();
                    break;
                case -88925533:
                    result = new TL_channelFull_old();
                    break;
                case 771925524:
                    result = new TL_chatFull();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChatFull", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChatInvite extends TLObject {
        public boolean broadcast;
        public boolean channel;
        public Chat chat;
        public int flags;
        public boolean isPublic;
        public boolean megagroup;
        public String title;

        public static ChatInvite TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChatInvite result = null;
            switch (constructor) {
                case -1813406880:
                    result = new TL_chatInvite();
                    break;
                case 1516793212:
                    result = new TL_chatInviteAlready();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChatInvite", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChatParticipant extends TLObject {
        public int date;
        public int inviter_id;
        public int user_id;

        public static ChatParticipant TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChatParticipant result = null;
            switch (constructor) {
                case -925415106:
                    result = new TL_chatParticipant();
                    break;
                case -636267638:
                    result = new TL_chatParticipantCreator();
                    break;
                case -489233354:
                    result = new TL_chatParticipantAdmin();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChatParticipant", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChatParticipants extends TLObject {
        public int admin_id;
        public int chat_id;
        public int flags;
        public ArrayList<ChatParticipant> participants;
        public ChatParticipant self_participant;
        public int version;

        public ChatParticipants() {
            this.participants = new ArrayList();
        }

        public static ChatParticipants TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChatParticipants result = null;
            switch (constructor) {
                case -57668565:
                    result = new TL_chatParticipantsForbidden();
                    break;
                case 265468810:
                    result = new TL_chatParticipantsForbidden_old();
                    break;
                case 1061556205:
                    result = new TL_chatParticipants();
                    break;
                case 2017571861:
                    result = new TL_chatParticipants_old();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChatParticipants", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ChatPhoto extends TLObject {
        public FileLocation photo_big;
        public FileLocation photo_small;

        public static ChatPhoto TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ChatPhoto result = null;
            switch (constructor) {
                case 935395612:
                    result = new TL_chatPhotoEmpty();
                    break;
                case 1632839530:
                    result = new TL_chatPhoto();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ChatPhoto", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ContactLink extends TLObject {
        public static ContactLink TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ContactLink result = null;
            switch (constructor) {
                case -721239344:
                    result = new TL_contactLinkContact();
                    break;
                case -17968211:
                    result = new TL_contactLinkNone();
                    break;
                case 646922073:
                    result = new TL_contactLinkHasPhone();
                    break;
                case 1599050311:
                    result = new TL_contactLinkUnknown();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ContactLink", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class DecryptedMessage extends TLObject {
        public DecryptedMessageAction action;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public DecryptedMessageMedia media;
        public String message;
        public byte[] random_bytes;
        public long random_id;
        public long reply_to_random_id;
        public int ttl;
        public String via_bot_name;

        public DecryptedMessage() {
            this.entities = new ArrayList();
        }

        public static DecryptedMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            DecryptedMessage result = null;
            switch (constructor) {
                case -1438109059:
                    result = new TL_decryptedMessageService_layer8();
                    break;
                case 528568095:
                    result = new TL_decryptedMessage_layer8();
                    break;
                case 541931640:
                    result = new TL_decryptedMessage_layer17();
                    break;
                case 917541342:
                    result = new TL_decryptedMessage();
                    break;
                case 1930838368:
                    result = new TL_decryptedMessageService();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in DecryptedMessage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class DecryptedMessageAction extends TLObject {
        public SendMessageAction action;
        public int end_seq_no;
        public long exchange_id;
        public byte[] g_a;
        public byte[] g_b;
        public long key_fingerprint;
        public int layer;
        public ArrayList<Long> random_ids;
        public int start_seq_no;
        public int ttl_seconds;

        public DecryptedMessageAction() {
            this.random_ids = new ArrayList();
        }

        public static DecryptedMessageAction TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            DecryptedMessageAction result = null;
            switch (constructor) {
                case -1967000459:
                    result = new TL_decryptedMessageActionScreenshotMessages();
                    break;
                case -1586283796:
                    result = new TL_decryptedMessageActionSetMessageTTL();
                    break;
                case -1473258141:
                    result = new TL_decryptedMessageActionNoop();
                    break;
                case -860719551:
                    result = new TL_decryptedMessageActionTyping();
                    break;
                case -586814357:
                    result = new TL_decryptedMessageActionAbortKey();
                    break;
                case -332526693:
                    result = new TL_decryptedMessageActionCommitKey();
                    break;
                case -217806717:
                    result = new TL_decryptedMessageActionNotifyLayer();
                    break;
                case -204906213:
                    result = new TL_decryptedMessageActionRequestKey();
                    break;
                case 206520510:
                    result = new TL_decryptedMessageActionReadMessages();
                    break;
                case 1360072880:
                    result = new TL_decryptedMessageActionResend();
                    break;
                case 1700872964:
                    result = new TL_decryptedMessageActionDeleteMessages();
                    break;
                case 1729750108:
                    result = new TL_decryptedMessageActionFlushHistory();
                    break;
                case 1877046107:
                    result = new TL_decryptedMessageActionAcceptKey();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in DecryptedMessageAction", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class DecryptedMessageMedia extends TLObject {
        public double _long;
        public long access_hash;
        public String address;
        public ArrayList<DocumentAttribute> attributes;
        public String caption;
        public int date;
        public int dc_id;
        public int duration;
        public String file_name;
        public String first_name;
        public int f26h;
        public long id;
        public byte[] iv;
        public byte[] key;
        public String last_name;
        public double lat;
        public String mime_type;
        public String phone_number;
        public String provider;
        public int size;
        public int thumb_h;
        public int thumb_w;
        public String title;
        public String url;
        public int user_id;
        public String venue_id;
        public int f27w;

        public DecryptedMessageMedia() {
            this.attributes = new ArrayList();
        }

        public static DecryptedMessageMedia TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            DecryptedMessageMedia result = null;
            switch (constructor) {
                case -1978796689:
                    result = new TL_decryptedMessageMediaVenue();
                    break;
                case -1760785394:
                    result = new TL_decryptedMessageMediaVideo();
                    break;
                case -1332395189:
                    result = new TL_decryptedMessageMediaDocument_layer8();
                    break;
                case -452652584:
                    result = new TL_decryptedMessageMediaWebPage();
                    break;
                case -235238024:
                    result = new TL_decryptedMessageMediaPhoto();
                    break;
                case -90853155:
                    result = new TL_decryptedMessageMediaExternalDocument();
                    break;
                case 144661578:
                    result = new TL_decryptedMessageMediaEmpty();
                    break;
                case 846826124:
                    result = new TL_decryptedMessageMediaPhoto_layer8();
                    break;
                case 893913689:
                    result = new TL_decryptedMessageMediaGeoPoint();
                    break;
                case 1290694387:
                    result = new TL_decryptedMessageMediaVideo_layer8();
                    break;
                case 1380598109:
                    result = new TL_decryptedMessageMediaVideo_layer17();
                    break;
                case 1474341323:
                    result = new TL_decryptedMessageMediaAudio();
                    break;
                case 1485441687:
                    result = new TL_decryptedMessageMediaContact();
                    break;
                case 1619031439:
                    result = new TL_decryptedMessageMediaAudio_layer8();
                    break;
                case 2063502050:
                    result = new TL_decryptedMessageMediaDocument();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in DecryptedMessageMedia", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Document extends TLObject {
        public long access_hash;
        public ArrayList<DocumentAttribute> attributes;
        public String caption;
        public int date;
        public int dc_id;
        public String file_name;
        public long id;
        public byte[] iv;
        public byte[] key;
        public String mime_type;
        public int size;
        public PhotoSize thumb;
        public int user_id;

        public Document() {
            this.attributes = new ArrayList();
        }

        public static Document TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Document result = null;
            switch (constructor) {
                case -1627626714:
                    result = new TL_document_old();
                    break;
                case -106717361:
                    result = new TL_document();
                    break;
                case 922273905:
                    result = new TL_documentEmpty();
                    break;
                case 1431655766:
                    result = new TL_documentEncrypted_old();
                    break;
                case 1431655768:
                    result = new TL_documentEncrypted();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Document", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class DocumentAttribute extends TLObject {
        public String alt;
        public int duration;
        public String file_name;
        public int flags;
        public int f28h;
        public String performer;
        public InputStickerSet stickerset;
        public String title;
        public boolean voice;
        public int f29w;
        public byte[] waveform;

        public static DocumentAttribute TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            DocumentAttribute result = null;
            switch (constructor) {
                case -1739392570:
                    result = new TL_documentAttributeAudio();
                    break;
                case -1723033470:
                    result = new TL_documentAttributeSticker_old2();
                    break;
                case -556656416:
                    result = new TL_documentAttributeAudio_layer45();
                    break;
                case -83208409:
                    result = new TL_documentAttributeSticker_old();
                    break;
                case 85215461:
                    result = new TL_documentAttributeAudio_old();
                    break;
                case 297109817:
                    result = new TL_documentAttributeAnimated();
                    break;
                case 358154344:
                    result = new TL_documentAttributeFilename();
                    break;
                case 978674434:
                    result = new TL_documentAttributeSticker();
                    break;
                case 1494273227:
                    result = new TL_documentAttributeVideo();
                    break;
                case 1815593308:
                    result = new TL_documentAttributeImageSize();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in DocumentAttribute", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class DraftMessage extends TLObject {
        public int date;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public String message;
        public boolean no_webpage;
        public int reply_to_msg_id;

        public DraftMessage() {
            this.entities = new ArrayList();
        }

        public static DraftMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            DraftMessage result = null;
            switch (constructor) {
                case -1169445179:
                    result = new TL_draftMessageEmpty();
                    break;
                case -40996577:
                    result = new TL_draftMessage();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in DraftMessage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class EncryptedChat extends TLObject {
        public byte[] a_or_b;
        public long access_hash;
        public int admin_id;
        public byte[] auth_key;
        public int date;
        public long exchange_id;
        public byte[] future_auth_key;
        public long future_key_fingerprint;
        public byte[] g_a;
        public byte[] g_a_or_b;
        public int id;
        public int key_create_date;
        public long key_fingerprint;
        public byte[] key_hash;
        public short key_use_count_in;
        public short key_use_count_out;
        public int layer;
        public byte[] nonce;
        public int participant_id;
        public int seq_in;
        public int seq_out;
        public int ttl;
        public int user_id;

        public static EncryptedChat TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            EncryptedChat result = null;
            switch (constructor) {
                case -1417756512:
                    result = new TL_encryptedChatEmpty();
                    break;
                case -931638658:
                    result = new TL_encryptedChatRequested();
                    break;
                case -94974410:
                    result = new TL_encryptedChat();
                    break;
                case -39213129:
                    result = new TL_encryptedChatRequested_old();
                    break;
                case 332848423:
                    result = new TL_encryptedChatDiscarded();
                    break;
                case 1006044124:
                    result = new TL_encryptedChatWaiting();
                    break;
                case 1711395151:
                    result = new TL_encryptedChat_old();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in EncryptedChat", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class EncryptedFile extends TLObject {
        public long access_hash;
        public int dc_id;
        public long id;
        public int key_fingerprint;
        public int size;

        public static EncryptedFile TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            EncryptedFile result = null;
            switch (constructor) {
                case -1038136962:
                    result = new TL_encryptedFileEmpty();
                    break;
                case 1248893260:
                    result = new TL_encryptedFile();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in EncryptedFile", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class EncryptedMessage extends TLObject {
        public byte[] bytes;
        public int chat_id;
        public int date;
        public EncryptedFile file;
        public long random_id;

        public static EncryptedMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            EncryptedMessage result = null;
            switch (constructor) {
                case -317144808:
                    result = new TL_encryptedMessage();
                    break;
                case 594758406:
                    result = new TL_encryptedMessageService();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in EncryptedMessage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ExportedChatInvite extends TLObject {
        public String link;

        public static ExportedChatInvite TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ExportedChatInvite result = null;
            switch (constructor) {
                case -64092740:
                    result = new TL_chatInviteExported();
                    break;
                case 1776236393:
                    result = new TL_chatInviteEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ExportedChatInvite", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class FileLocation extends TLObject {
        public int dc_id;
        public byte[] iv;
        public byte[] key;
        public int local_id;
        public long secret;
        public long volume_id;

        public static FileLocation TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            FileLocation result = null;
            switch (constructor) {
                case 1406570614:
                    result = new TL_fileLocation();
                    break;
                case 1431655764:
                    result = new TL_fileEncryptedLocation();
                    break;
                case 2086234950:
                    result = new TL_fileLocationUnavailable();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in FileLocation", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class FoundGif extends TLObject {
        public String content_type;
        public String content_url;
        public Document document;
        public int f30h;
        public Photo photo;
        public String thumb_url;
        public String url;
        public int f31w;

        public static FoundGif TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            FoundGif result = null;
            switch (constructor) {
                case -1670052855:
                    result = new TL_foundGifCached();
                    break;
                case 372165663:
                    result = new TL_foundGif();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in FoundGif", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class GeoChatMessage extends TLObject {
        public MessageAction action;
        public int chat_id;
        public int date;
        public int from_id;
        public int id;
        public MessageMedia media;
        public String message;

        public static GeoChatMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            GeoChatMessage result = null;
            switch (constructor) {
                case -749755826:
                    result = new TL_geoChatMessageService();
                    break;
                case 1158019297:
                    result = new TL_geoChatMessage();
                    break;
                case 1613830811:
                    result = new TL_geoChatMessageEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in GeoChatMessage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class GeoPoint extends TLObject {
        public double _long;
        public double lat;

        public static GeoPoint TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            GeoPoint result = null;
            switch (constructor) {
                case 286776671:
                    result = new TL_geoPointEmpty();
                    break;
                case 541710092:
                    result = new TL_geoPoint();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in GeoPoint", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputChannel extends TLObject {
        public long access_hash;
        public int channel_id;

        public static InputChannel TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputChannel result = null;
            switch (constructor) {
                case -1343524562:
                    result = new TL_inputChannel();
                    break;
                case -292807034:
                    result = new TL_inputChannelEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputChannel", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputChatPhoto extends TLObject {
        public InputPhotoCrop crop;
        public InputFile file;
        public InputPhoto id;

        public static InputChatPhoto TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputChatPhoto result = null;
            switch (constructor) {
                case -1809496270:
                    result = new TL_inputChatUploadedPhoto();
                    break;
                case -1293828344:
                    result = new TL_inputChatPhoto();
                    break;
                case 480546647:
                    result = new TL_inputChatPhotoEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputChatPhoto", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputDocument extends TLObject {
        public long access_hash;
        public long id;

        public static InputDocument TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputDocument result = null;
            switch (constructor) {
                case 410618194:
                    result = new TL_inputDocument();
                    break;
                case 1928391342:
                    result = new TL_inputDocumentEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputDocument", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputEncryptedFile extends TLObject {
        public long access_hash;
        public long id;
        public int key_fingerprint;
        public String md5_checksum;
        public int parts;

        public static InputEncryptedFile TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputEncryptedFile result = null;
            switch (constructor) {
                case 406307684:
                    result = new TL_inputEncryptedFileEmpty();
                    break;
                case 767652808:
                    result = new TL_inputEncryptedFileBigUploaded();
                    break;
                case 1511503333:
                    result = new TL_inputEncryptedFile();
                    break;
                case 1690108678:
                    result = new TL_inputEncryptedFileUploaded();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputEncryptedFile", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputFile extends TLObject {
        public long id;
        public String md5_checksum;
        public String name;
        public int parts;

        public static InputFile TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputFile result = null;
            switch (constructor) {
                case -181407105:
                    result = new TL_inputFile();
                    break;
                case -95482955:
                    result = new TL_inputFileBig();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputFile", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputFileLocation extends TLObject {
        public long access_hash;
        public long id;
        public int local_id;
        public long secret;
        public long volume_id;

        public static InputFileLocation TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputFileLocation result = null;
            switch (constructor) {
                case -182231723:
                    result = new TL_inputEncryptedFileLocation();
                    break;
                case 342061462:
                    result = new TL_inputFileLocation();
                    break;
                case 1313188841:
                    result = new TL_inputDocumentFileLocation();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputFileLocation", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputGeoPoint extends TLObject {
        public double _long;
        public double lat;

        public static InputGeoPoint TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputGeoPoint result = null;
            switch (constructor) {
                case -457104426:
                    result = new TL_inputGeoPointEmpty();
                    break;
                case -206066487:
                    result = new TL_inputGeoPoint();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputGeoPoint", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputMedia extends TLObject {
        public String address;
        public ArrayList<DocumentAttribute> attributes;
        public String caption;
        public InputFile file;
        public String first_name;
        public InputGeoPoint geo_point;
        public String last_name;
        public String mime_type;
        public String phone_number;
        public String provider;
        public String f32q;
        public InputFile thumb;
        public String title;
        public String url;
        public String venue_id;

        public InputMedia() {
            this.attributes = new ArrayList();
        }

        public static InputMedia TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputMedia result = null;
            switch (constructor) {
                case -1771768449:
                    result = new TL_inputMediaEmpty();
                    break;
                case -1494984313:
                    result = new TL_inputMediaContact();
                    break;
                case -1386138479:
                    result = new TL_inputMediaUploadedThumbDocument();
                    break;
                case -373312269:
                    result = new TL_inputMediaPhoto();
                    break;
                case -139464256:
                    result = new TL_inputMediaUploadedPhoto();
                    break;
                case -104578748:
                    result = new TL_inputMediaGeoPoint();
                    break;
                case 444068508:
                    result = new TL_inputMediaDocument();
                    break;
                case 495530093:
                    result = new TL_inputMediaUploadedDocument();
                    break;
                case 673687578:
                    result = new TL_inputMediaVenue();
                    break;
                case 1212395773:
                    result = new TL_inputMediaGifExternal();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputMedia", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputNotifyPeer extends TLObject {
        public static InputNotifyPeer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputNotifyPeer result = null;
            switch (constructor) {
                case -1540769658:
                    result = new TL_inputNotifyAll();
                    break;
                case -1195615476:
                    result = new TL_inputNotifyPeer();
                    break;
                case 423314455:
                    result = new TL_inputNotifyUsers();
                    break;
                case 1251338318:
                    result = new TL_inputNotifyChats();
                    break;
                case 1301143240:
                    result = new TL_inputNotifyGeoChatPeer();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputNotifyPeer", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputPeer extends TLObject {
        public long access_hash;
        public int channel_id;
        public int chat_id;
        public int user_id;

        public static InputPeer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputPeer result = null;
            switch (constructor) {
                case 396093539:
                    result = new TL_inputPeerChat();
                    break;
                case 548253432:
                    result = new TL_inputPeerChannel();
                    break;
                case 2072935910:
                    result = new TL_inputPeerUser();
                    break;
                case 2107670217:
                    result = new TL_inputPeerSelf();
                    break;
                case 2134579434:
                    result = new TL_inputPeerEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputPeer", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputPeerNotifyEvents extends TLObject {
        public static InputPeerNotifyEvents TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputPeerNotifyEvents result = null;
            switch (constructor) {
                case -395694988:
                    result = new TL_inputPeerNotifyEventsAll();
                    break;
                case -265263912:
                    result = new TL_inputPeerNotifyEventsEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputPeerNotifyEvents", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputPhoto extends TLObject {
        public long access_hash;
        public long id;

        public static InputPhoto TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputPhoto result = null;
            switch (constructor) {
                case -74070332:
                    result = new TL_inputPhoto();
                    break;
                case 483901197:
                    result = new TL_inputPhotoEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputPhoto", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputPhotoCrop extends TLObject {
        public double crop_left;
        public double crop_top;
        public double crop_width;

        public static InputPhotoCrop TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputPhotoCrop result = null;
            switch (constructor) {
                case -1377390588:
                    result = new TL_inputPhotoCropAuto();
                    break;
                case -644787419:
                    result = new TL_inputPhotoCrop();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputPhotoCrop", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputPrivacyKey extends TLObject {
        public static InputPrivacyKey TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputPrivacyKey result = null;
            switch (constructor) {
                case -1107622874:
                    result = new TL_inputPrivacyKeyChatInvite();
                    break;
                case 1335282456:
                    result = new TL_inputPrivacyKeyStatusTimestamp();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputPrivacyKey", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputPrivacyRule extends TLObject {
        public ArrayList<InputUser> users;

        public InputPrivacyRule() {
            this.users = new ArrayList();
        }

        public static InputPrivacyRule TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputPrivacyRule result = null;
            switch (constructor) {
                case -1877932953:
                    result = new TL_inputPrivacyValueDisallowUsers();
                    break;
                case -697604407:
                    result = new TL_inputPrivacyValueDisallowAll();
                    break;
                case 195371015:
                    result = new TL_inputPrivacyValueDisallowContacts();
                    break;
                case 218751099:
                    result = new TL_inputPrivacyValueAllowContacts();
                    break;
                case 320652927:
                    result = new TL_inputPrivacyValueAllowUsers();
                    break;
                case 407582158:
                    result = new TL_inputPrivacyValueAllowAll();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputPrivacyRule", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputStickerSet extends TLObject {
        public long access_hash;
        public long id;
        public String short_name;

        public static InputStickerSet TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputStickerSet result = null;
            switch (constructor) {
                case -2044933984:
                    result = new TL_inputStickerSetShortName();
                    break;
                case -1645763991:
                    result = new TL_inputStickerSetID();
                    break;
                case -4838507:
                    result = new TL_inputStickerSetEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputStickerSet", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class InputUser extends TLObject {
        public long access_hash;
        public int user_id;

        public static InputUser TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            InputUser result = null;
            switch (constructor) {
                case -1182234929:
                    result = new TL_inputUserEmpty();
                    break;
                case -668391402:
                    result = new TL_inputUser();
                    break;
                case -138301121:
                    result = new TL_inputUserSelf();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in InputUser", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class KeyboardButton extends TLObject {
        public byte[] data;
        public String query;
        public String text;
        public String url;

        public static KeyboardButton TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            KeyboardButton result = null;
            switch (constructor) {
                case -1560655744:
                    result = new TL_keyboardButton();
                    break;
                case -1318425559:
                    result = new TL_keyboardButtonRequestPhone();
                    break;
                case -367298028:
                    result = new TL_keyboardButtonSwitchInline();
                    break;
                case -59151553:
                    result = new TL_keyboardButtonRequestGeoLocation();
                    break;
                case 629866245:
                    result = new TL_keyboardButtonUrl();
                    break;
                case 1748655686:
                    result = new TL_keyboardButtonCallback();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in KeyboardButton", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Message extends TLObject {
        public MessageAction action;
        public String attachPath;
        public int date;
        public int destroyTime;
        public long dialog_id;
        public int edit_date;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public int from_id;
        public TL_messageFwdHeader fwd_from;
        public int fwd_msg_id;
        public int id;
        public int layer;
        public int local_id;
        public MessageMedia media;
        public boolean media_unread;
        public boolean mentioned;
        public String message;
        public boolean out;
        public HashMap<String, String> params;
        public boolean post;
        public long random_id;
        public Message replyMessage;
        public ReplyMarkup reply_markup;
        public int reply_to_msg_id;
        public long reply_to_random_id;
        public int send_state;
        public int seq_in;
        public int seq_out;
        public boolean silent;
        public Peer to_id;
        public int ttl;
        public boolean unread;
        public int via_bot_id;
        public String via_bot_name;
        public int views;

        public Message() {
            this.entities = new ArrayList();
            this.send_state = 0;
            this.fwd_msg_id = 0;
            this.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            this.local_id = 0;
        }

        public static Message TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Message result = null;
            switch (constructor) {
                case -2082087340:
                    result = new TL_messageEmpty();
                    break;
                case -1642487306:
                    result = new TL_messageService();
                    break;
                case -1618124613:
                    result = new TL_messageService_old();
                    break;
                case -1553471722:
                    result = new TL_messageForwarded_old2();
                    break;
                case -1481959023:
                    result = new TL_message_old3();
                    break;
                case -1066691065:
                    result = new TL_messageService_layer48();
                    break;
                case -1063525281:
                    result = new TL_message();
                    break;
                case -1023016155:
                    result = new TL_message_old4();
                    break;
                case -913120932:
                    result = new TL_message_layer47();
                    break;
                case -260565816:
                    result = new TL_message_old5();
                    break;
                case 99903492:
                    result = new TL_messageForwarded_old();
                    break;
                case 495384334:
                    result = new TL_messageService_old2();
                    break;
                case 585853626:
                    result = new TL_message_old();
                    break;
                case 736885382:
                    result = new TL_message_old6();
                    break;
                case 1431655928:
                    result = new TL_message_secret_old();
                    break;
                case 1431655929:
                    result = new TL_message_secret();
                    break;
                case 1450613171:
                    result = new TL_message_old2();
                    break;
                case 1537633299:
                    result = new TL_message_old7();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Message", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class MessageAction extends TLObject {
        public String address;
        public int channel_id;
        public int chat_id;
        public DecryptedMessageAction encryptedAction;
        public int inviter_id;
        public UserProfilePhoto newUserPhoto;
        public Photo photo;
        public String title;
        public int ttl;
        public int user_id;
        public ArrayList<Integer> users;

        public MessageAction() {
            this.users = new ArrayList();
        }

        public static MessageAction TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            MessageAction result = null;
            switch (constructor) {
                case -1799538451:
                    result = new TL_messageActionPinMessage();
                    break;
                case -1781355374:
                    result = new TL_messageActionChannelCreate();
                    break;
                case -1780220945:
                    result = new TL_messageActionChatDeletePhoto();
                    break;
                case -1615153660:
                    result = new TL_messageActionHistoryClear();
                    break;
                case -1503425638:
                    result = new TL_messageActionChatCreate();
                    break;
                case -1336546578:
                    result = new TL_messageActionChannelMigrateFrom();
                    break;
                case -1297179892:
                    result = new TL_messageActionChatDeleteUser();
                    break;
                case -1247687078:
                    result = new TL_messageActionChatEditTitle();
                    break;
                case -1230047312:
                    result = new TL_messageActionEmpty();
                    break;
                case -123931160:
                    result = new TL_messageActionChatJoinedByLink();
                    break;
                case 209540062:
                    result = new TL_messageActionGeoChatCheckin();
                    break;
                case 1217033015:
                    result = new TL_messageActionChatAddUser();
                    break;
                case 1371385889:
                    result = new TL_messageActionChatMigrateTo();
                    break;
                case 1431655760:
                    result = new TL_messageActionUserJoined();
                    break;
                case 1431655761:
                    result = new TL_messageActionUserUpdatedPhoto();
                    break;
                case 1431655762:
                    result = new TL_messageActionTTLChange();
                    break;
                case 1431655767:
                    result = new TL_messageActionCreatedBroadcastList();
                    break;
                case 1431655925:
                    result = new TL_messageActionLoginUnknownLocation();
                    break;
                case 1431655927:
                    result = new TL_messageEncryptedAction();
                    break;
                case 1581055051:
                    result = new TL_messageActionChatAddUser_old();
                    break;
                case 1862504124:
                    result = new TL_messageActionGeoChatCreate();
                    break;
                case 2144015272:
                    result = new TL_messageActionChatEditPhoto();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in MessageAction", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class MessageEntity extends TLObject {
        public String language;
        public int length;
        public int offset;
        public String url;

        public static MessageEntity TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            MessageEntity result = null;
            switch (constructor) {
                case -2106619040:
                    result = new TL_messageEntityItalic();
                    break;
                case -1148011883:
                    result = new TL_messageEntityUnknown();
                    break;
                case -1117713463:
                    result = new TL_messageEntityBold();
                    break;
                case -100378723:
                    result = new TL_messageEntityMention();
                    break;
                case 546203849:
                    result = new TL_inputMessageEntityMentionName();
                    break;
                case 681706865:
                    result = new TL_messageEntityCode();
                    break;
                case 892193368:
                    result = new TL_messageEntityMentionName();
                    break;
                case 1692693954:
                    result = new TL_messageEntityEmail();
                    break;
                case 1827637959:
                    result = new TL_messageEntityBotCommand();
                    break;
                case 1859134776:
                    result = new TL_messageEntityUrl();
                    break;
                case 1868782349:
                    result = new TL_messageEntityHashtag();
                    break;
                case 1938967520:
                    result = new TL_messageEntityPre();
                    break;
                case 1990644519:
                    result = new TL_messageEntityTextUrl();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in MessageEntity", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class MessageMedia extends TLObject {
        public String address;
        public Audio audio_unused;
        public byte[] bytes;
        public String caption;
        public Document document;
        public String first_name;
        public GeoPoint geo;
        public String last_name;
        public String phone_number;
        public Photo photo;
        public String provider;
        public String title;
        public int user_id;
        public String venue_id;
        public Video video_unused;
        public WebPage webpage;

        public static MessageMedia TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            MessageMedia result = null;
            switch (constructor) {
                case -1618676578:
                    result = new TL_messageMediaUnsupported();
                    break;
                case -1563278704:
                    result = new TL_messageMediaVideo_old();
                    break;
                case -1557277184:
                    result = new TL_messageMediaWebPage();
                    break;
                case -961117440:
                    result = new TL_messageMediaAudio_layer45();
                    break;
                case -926655958:
                    result = new TL_messageMediaPhoto_old();
                    break;
                case -203411800:
                    result = new TL_messageMediaDocument();
                    break;
                case 694364726:
                    result = new TL_messageMediaUnsupported_old();
                    break;
                case 802824708:
                    result = new TL_messageMediaDocument_old();
                    break;
                case 1032643901:
                    result = new TL_messageMediaPhoto();
                    break;
                case 1038967584:
                    result = new TL_messageMediaEmpty();
                    break;
                case 1457575028:
                    result = new TL_messageMediaGeo();
                    break;
                case 1540298357:
                    result = new TL_messageMediaVideo_layer45();
                    break;
                case 1585262393:
                    result = new TL_messageMediaContact();
                    break;
                case 2031269663:
                    result = new TL_messageMediaVenue();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in MessageMedia", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
                MessageMedia mediaDocument;
                if (result.video_unused != null) {
                    mediaDocument = new TL_messageMediaDocument();
                    if (result.video_unused instanceof TL_videoEncrypted) {
                        mediaDocument.document = new TL_documentEncrypted();
                        mediaDocument.document.key = result.video_unused.key;
                        mediaDocument.document.iv = result.video_unused.iv;
                    } else {
                        mediaDocument.document = new TL_document();
                    }
                    mediaDocument.document.id = result.video_unused.id;
                    mediaDocument.document.access_hash = result.video_unused.access_hash;
                    mediaDocument.document.date = result.video_unused.date;
                    if (result.video_unused.mime_type != null) {
                        mediaDocument.document.mime_type = result.video_unused.mime_type;
                    } else {
                        mediaDocument.document.mime_type = MimeTypes.VIDEO_MP4;
                    }
                    mediaDocument.document.size = result.video_unused.size;
                    mediaDocument.document.thumb = result.video_unused.thumb;
                    mediaDocument.document.dc_id = result.video_unused.dc_id;
                    mediaDocument.caption = result.caption;
                    TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                    attributeVideo.w = result.video_unused.f41w;
                    attributeVideo.h = result.video_unused.f40h;
                    attributeVideo.duration = result.video_unused.duration;
                    mediaDocument.document.attributes.add(attributeVideo);
                    result = mediaDocument;
                    if (mediaDocument.caption == null) {
                        mediaDocument.caption = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                } else if (result.audio_unused != null) {
                    mediaDocument = new TL_messageMediaDocument();
                    if (result.audio_unused instanceof TL_audioEncrypted) {
                        mediaDocument.document = new TL_documentEncrypted();
                        mediaDocument.document.key = result.audio_unused.key;
                        mediaDocument.document.iv = result.audio_unused.iv;
                    } else {
                        mediaDocument.document = new TL_document();
                    }
                    mediaDocument.document.id = result.audio_unused.id;
                    mediaDocument.document.access_hash = result.audio_unused.access_hash;
                    mediaDocument.document.date = result.audio_unused.date;
                    if (result.audio_unused.mime_type != null) {
                        mediaDocument.document.mime_type = result.audio_unused.mime_type;
                    } else {
                        mediaDocument.document.mime_type = "audio/ogg";
                    }
                    mediaDocument.document.size = result.audio_unused.size;
                    mediaDocument.document.thumb = new TL_photoSizeEmpty();
                    mediaDocument.document.thumb.type = "s";
                    mediaDocument.document.dc_id = result.audio_unused.dc_id;
                    mediaDocument.caption = result.caption;
                    TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                    attributeAudio.duration = result.audio_unused.duration;
                    attributeAudio.voice = true;
                    mediaDocument.document.attributes.add(attributeAudio);
                    result = mediaDocument;
                    if (mediaDocument.caption == null) {
                        mediaDocument.caption = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                }
            }
            return result;
        }
    }

    public static class MessagesFilter extends TLObject {
        public static MessagesFilter TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            MessagesFilter result = null;
            switch (constructor) {
                case -1777752804:
                    result = new TL_inputMessagesFilterPhotos();
                    break;
                case -1629621880:
                    result = new TL_inputMessagesFilterDocument();
                    break;
                case -1614803355:
                    result = new TL_inputMessagesFilterVideo();
                    break;
                case -648121413:
                    result = new TL_inputMessagesFilterPhotoVideoDocuments();
                    break;
                case -3644025:
                    result = new TL_inputMessagesFilterGif();
                    break;
                case 928101534:
                    result = new TL_inputMessagesFilterMusic();
                    break;
                case 975236280:
                    result = new TL_inputMessagesFilterChatPhotos();
                    break;
                case 1358283666:
                    result = new TL_inputMessagesFilterVoice();
                    break;
                case 1458172132:
                    result = new TL_inputMessagesFilterPhotoVideo();
                    break;
                case 1474492012:
                    result = new TL_inputMessagesFilterEmpty();
                    break;
                case 2129714567:
                    result = new TL_inputMessagesFilterUrl();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in MessagesFilter", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class NotifyPeer extends TLObject {
        public Peer peer;

        public static NotifyPeer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            NotifyPeer result = null;
            switch (constructor) {
                case -1613493288:
                    result = new TL_notifyPeer();
                    break;
                case -1261946036:
                    result = new TL_notifyUsers();
                    break;
                case -1073230141:
                    result = new TL_notifyChats();
                    break;
                case 1959820384:
                    result = new TL_notifyAll();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in NotifyPeer", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Peer extends TLObject {
        public int channel_id;
        public int chat_id;
        public int user_id;

        public static Peer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Peer result = null;
            switch (constructor) {
                case -1649296275:
                    result = new TL_peerUser();
                    break;
                case -1160714821:
                    result = new TL_peerChat();
                    break;
                case -1109531342:
                    result = new TL_peerChannel();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Peer", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class PeerNotifyEvents extends TLObject {
        public static PeerNotifyEvents TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            PeerNotifyEvents result = null;
            switch (constructor) {
                case -1378534221:
                    result = new TL_peerNotifyEventsEmpty();
                    break;
                case 1830677896:
                    result = new TL_peerNotifyEventsAll();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in PeerNotifyEvents", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class PeerNotifySettings extends TLObject {
        public int events_mask;
        public int flags;
        public int mute_until;
        public boolean silent;
        public String sound;

        public static PeerNotifySettings TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            PeerNotifySettings result = null;
            switch (constructor) {
                case -1923214866:
                    result = new TL_peerNotifySettings_layer47();
                    break;
                case -1697798976:
                    result = new TL_peerNotifySettings();
                    break;
                case 1889961234:
                    result = new TL_peerNotifySettingsEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in PeerNotifySettings", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Photo extends TLObject {
        public long access_hash;
        public String caption;
        public int date;
        public GeoPoint geo;
        public long id;
        public ArrayList<PhotoSize> sizes;
        public int user_id;

        public Photo() {
            this.sizes = new ArrayList();
        }

        public static Photo TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Photo result = null;
            switch (constructor) {
                case -1014792074:
                    result = new TL_photo_old2();
                    break;
                case -840088834:
                    result = new TL_photo();
                    break;
                case 582313809:
                    result = new TL_photo_old();
                    break;
                case 590459437:
                    result = new TL_photoEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Photo", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class PhotoSize extends TLObject {
        public byte[] bytes;
        public int f33h;
        public FileLocation location;
        public int size;
        public String type;
        public int f34w;

        public static PhotoSize TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            PhotoSize result = null;
            switch (constructor) {
                case -374917894:
                    result = new TL_photoCachedSize();
                    break;
                case 236446268:
                    result = new TL_photoSizeEmpty();
                    break;
                case 2009052699:
                    result = new TL_photoSize();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in PhotoSize", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class PrivacyKey extends TLObject {
        public static PrivacyKey TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            PrivacyKey result = null;
            switch (constructor) {
                case -1137792208:
                    result = new TL_privacyKeyStatusTimestamp();
                    break;
                case 1343122938:
                    result = new TL_privacyKeyChatInvite();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in PrivacyKey", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class PrivacyRule extends TLObject {
        public ArrayList<Integer> users;

        public PrivacyRule() {
            this.users = new ArrayList();
        }

        public static PrivacyRule TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            PrivacyRule result = null;
            switch (constructor) {
                case -1955338397:
                    result = new TL_privacyValueDisallowAll();
                    break;
                case -125240806:
                    result = new TL_privacyValueDisallowContacts();
                    break;
                case -123988:
                    result = new TL_privacyValueAllowContacts();
                    break;
                case 209668535:
                    result = new TL_privacyValueDisallowUsers();
                    break;
                case 1297858060:
                    result = new TL_privacyValueAllowUsers();
                    break;
                case 1698855810:
                    result = new TL_privacyValueAllowAll();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in PrivacyRule", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ReplyMarkup extends TLObject {
        public int flags;
        public boolean resize;
        public ArrayList<TL_keyboardButtonRow> rows;
        public boolean selective;
        public boolean single_use;

        public ReplyMarkup() {
            this.rows = new ArrayList();
        }

        public static ReplyMarkup TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ReplyMarkup result = null;
            switch (constructor) {
                case -1606526075:
                    result = new TL_replyKeyboardHide();
                    break;
                case -200242528:
                    result = new TL_replyKeyboardForceReply();
                    break;
                case 889353612:
                    result = new TL_replyKeyboardMarkup();
                    break;
                case 1218642516:
                    result = new TL_replyInlineMarkup();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ReplyMarkup", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class ReportReason extends TLObject {
        public String text;

        public static ReportReason TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            ReportReason result = null;
            switch (constructor) {
                case -512463606:
                    result = new TL_inputReportReasonOther();
                    break;
                case 505595789:
                    result = new TL_inputReportReasonViolence();
                    break;
                case 777640226:
                    result = new TL_inputReportReasonPornography();
                    break;
                case 1490799288:
                    result = new TL_inputReportReasonSpam();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in ReportReason", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class SendMessageAction extends TLObject {
        public int progress;

        public static SendMessageAction TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            SendMessageAction result = null;
            switch (constructor) {
                case -1884362354:
                    result = new TL_sendMessageUploadDocumentAction_old();
                    break;
                case -1845219337:
                    result = new TL_sendMessageUploadVideoAction_old();
                    break;
                case -1727382502:
                    result = new TL_sendMessageUploadPhotoAction_old();
                    break;
                case -1584933265:
                    result = new TL_sendMessageRecordVideoAction();
                    break;
                case -1441998364:
                    result = new TL_sendMessageUploadDocumentAction();
                    break;
                case -774682074:
                    result = new TL_sendMessageUploadPhotoAction();
                    break;
                case -718310409:
                    result = new TL_sendMessageRecordAudioAction();
                    break;
                case -424899985:
                    result = new TL_sendMessageUploadAudioAction_old();
                    break;
                case -378127636:
                    result = new TL_sendMessageUploadVideoAction();
                    break;
                case -212740181:
                    result = new TL_sendMessageUploadAudioAction();
                    break;
                case -44119819:
                    result = new TL_sendMessageCancelAction();
                    break;
                case 381645902:
                    result = new TL_sendMessageTypingAction();
                    break;
                case 393186209:
                    result = new TL_sendMessageGeoLocationAction();
                    break;
                case 1653390447:
                    result = new TL_sendMessageChooseContactAction();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in SendMessageAction", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class StickerSet extends TLObject {
        public long access_hash;
        public int count;
        public boolean disabled;
        public int flags;
        public int hash;
        public long id;
        public boolean installed;
        public boolean official;
        public String short_name;
        public String title;

        public static StickerSet TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            StickerSet result = null;
            switch (constructor) {
                case -1482409193:
                    result = new TL_stickerSet_old();
                    break;
                case -852477119:
                    result = new TL_stickerSet();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in StickerSet", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class TL_accountDaysTTL extends TLObject {
        public static int constructor;
        public int days;

        static {
            constructor = -1194283041;
        }

        public static TL_accountDaysTTL TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_accountDaysTTL result = new TL_accountDaysTTL();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_accountDaysTTL", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.days = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.days);
        }
    }

    public static class TL_account_authorizations extends TLObject {
        public static int constructor;
        public ArrayList<TL_authorization> authorizations;

        public TL_account_authorizations() {
            this.authorizations = new ArrayList();
        }

        static {
            constructor = 307276766;
        }

        public static TL_account_authorizations TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_account_authorizations result = new TL_account_authorizations();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_account_authorizations", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_authorization object = TL_authorization.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.authorizations.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.authorizations.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_authorization) this.authorizations.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_account_changePhone extends TLObject {
        public static int constructor;
        public String phone_code;
        public String phone_code_hash;
        public String phone_number;

        static {
            constructor = 1891839707;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return User.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.phone_code_hash);
            stream.writeString(this.phone_code);
        }
    }

    public static class TL_account_checkUsername extends TLObject {
        public static int constructor;
        public String username;

        static {
            constructor = 655677548;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.username);
        }
    }

    public static class TL_account_deleteAccount extends TLObject {
        public static int constructor;
        public String reason;

        static {
            constructor = 1099779595;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.reason);
        }
    }

    public static class TL_account_getAccountTTL extends TLObject {
        public static int constructor;

        static {
            constructor = 150761757;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_accountDaysTTL.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_account_getAuthorizations extends TLObject {
        public static int constructor;

        static {
            constructor = -484392616;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_account_authorizations.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_account_getNotifySettings extends TLObject {
        public static int constructor;
        public InputNotifyPeer peer;

        static {
            constructor = 313765169;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return PeerNotifySettings.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_account_getPassword extends TLObject {
        public static int constructor;

        static {
            constructor = 1418342645;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return account_Password.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_account_getPasswordSettings extends TLObject {
        public static int constructor;
        public byte[] current_password_hash;

        static {
            constructor = -1131605573;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_account_passwordSettings.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.current_password_hash);
        }
    }

    public static class TL_account_getPrivacy extends TLObject {
        public static int constructor;
        public InputPrivacyKey key;

        static {
            constructor = -623130288;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_account_privacyRules.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.key.serializeToStream(stream);
        }
    }

    public static class TL_account_getWallPapers extends TLObject {
        public static int constructor;

        static {
            constructor = -1068696894;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                WallPaper object = WallPaper.TLdeserialize(stream, stream.readInt32(exception), exception);
                if (object == null) {
                    break;
                }
                vector.objects.add(object);
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_account_passwordInputSettings extends TLObject {
        public static int constructor;
        public String email;
        public int flags;
        public String hint;
        public byte[] new_password_hash;
        public byte[] new_salt;

        static {
            constructor = -2037289493;
        }

        public static TL_account_passwordInputSettings TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_account_passwordInputSettings result = new TL_account_passwordInputSettings();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_account_passwordInputSettings", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.new_salt = stream.readByteArray(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.new_password_hash = stream.readByteArray(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.hint = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.email = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeByteArray(this.new_salt);
            }
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeByteArray(this.new_password_hash);
            }
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.hint);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.email);
            }
        }
    }

    public static class TL_account_passwordSettings extends TLObject {
        public static int constructor;
        public String email;

        static {
            constructor = -1212732749;
        }

        public static TL_account_passwordSettings TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_account_passwordSettings result = new TL_account_passwordSettings();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_account_passwordSettings", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.email = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.email);
        }
    }

    public static class TL_account_privacyRules extends TLObject {
        public static int constructor;
        public ArrayList<PrivacyRule> rules;
        public ArrayList<User> users;

        public TL_account_privacyRules() {
            this.rules = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = 1430961007;
        }

        public static TL_account_privacyRules TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_account_privacyRules result = new TL_account_privacyRules();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_account_privacyRules", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    PrivacyRule object = PrivacyRule.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.rules.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.rules.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((PrivacyRule) this.rules.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_account_registerDevice extends TLObject {
        public static int constructor;
        public String token;
        public int token_type;

        static {
            constructor = 1669245048;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.token_type);
            stream.writeString(this.token);
        }
    }

    public static class TL_account_reportPeer extends TLObject {
        public static int constructor;
        public InputPeer peer;
        public ReportReason reason;

        static {
            constructor = -1374118561;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.reason.serializeToStream(stream);
        }
    }

    public static class TL_account_resetAuthorization extends TLObject {
        public static int constructor;
        public long hash;

        static {
            constructor = -545786948;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.hash);
        }
    }

    public static class TL_account_resetNotifySettings extends TLObject {
        public static int constructor;

        static {
            constructor = -612493497;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_account_sendChangePhoneCode extends TLObject {
        public static int constructor;
        public boolean allow_flashcall;
        public boolean current_number;
        public int flags;
        public String phone_number;

        static {
            constructor = 149257707;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_sentCode.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.allow_flashcall ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            stream.writeString(this.phone_number);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeBool(this.current_number);
            }
        }
    }

    public static class TL_account_setAccountTTL extends TLObject {
        public static int constructor;
        public TL_accountDaysTTL ttl;

        static {
            constructor = 608323678;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.ttl.serializeToStream(stream);
        }
    }

    public static class TL_account_setPrivacy extends TLObject {
        public static int constructor;
        public InputPrivacyKey key;
        public ArrayList<InputPrivacyRule> rules;

        public TL_account_setPrivacy() {
            this.rules = new ArrayList();
        }

        static {
            constructor = -906486552;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_account_privacyRules.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.key.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.rules.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputPrivacyRule) this.rules.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_account_unregisterDevice extends TLObject {
        public static int constructor;
        public String token;
        public int token_type;

        static {
            constructor = 1707432768;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.token_type);
            stream.writeString(this.token);
        }
    }

    public static class TL_account_updateDeviceLocked extends TLObject {
        public static int constructor;
        public int period;

        static {
            constructor = 954152242;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.period);
        }
    }

    public static class TL_account_updateNotifySettings extends TLObject {
        public static int constructor;
        public InputNotifyPeer peer;
        public TL_inputPeerNotifySettings settings;

        static {
            constructor = -2067899501;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.settings.serializeToStream(stream);
        }
    }

    public static class TL_account_updatePasswordSettings extends TLObject {
        public static int constructor;
        public byte[] current_password_hash;
        public TL_account_passwordInputSettings new_settings;

        static {
            constructor = -92517498;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.current_password_hash);
            this.new_settings.serializeToStream(stream);
        }
    }

    public static class TL_account_updateProfile extends TLObject {
        public static int constructor;
        public String about;
        public String first_name;
        public int flags;
        public String last_name;

        static {
            constructor = 2018596725;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return User.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.first_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.last_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.about);
            }
        }
    }

    public static class TL_account_updateStatus extends TLObject {
        public static int constructor;
        public boolean offline;

        static {
            constructor = 1713919532;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeBool(this.offline);
        }
    }

    public static class TL_account_updateUsername extends TLObject {
        public static int constructor;
        public String username;

        static {
            constructor = 1040964988;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return User.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.username);
        }
    }

    public static class TL_auth_authorization extends TLObject {
        public static int constructor;
        public User user;

        static {
            constructor = -16553231;
        }

        public static TL_auth_authorization TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_auth_authorization result = new TL_auth_authorization();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_authorization", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user = User.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.user.serializeToStream(stream);
        }
    }

    public static class TL_auth_bindTempAuthKey extends TLObject {
        public static int constructor;
        public byte[] encrypted_message;
        public int expires_at;
        public long nonce;
        public long perm_auth_key_id;

        static {
            constructor = -841733627;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.perm_auth_key_id);
            stream.writeInt64(this.nonce);
            stream.writeInt32(this.expires_at);
            stream.writeByteArray(this.encrypted_message);
        }
    }

    public static class TL_auth_cancelCode extends TLObject {
        public static int constructor;
        public String phone_code_hash;
        public String phone_number;

        static {
            constructor = 520357240;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.phone_code_hash);
        }
    }

    public static class TL_auth_checkPassword extends TLObject {
        public static int constructor;
        public byte[] password_hash;

        static {
            constructor = 174260510;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_authorization.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.password_hash);
        }
    }

    public static class TL_auth_checkPhone extends TLObject {
        public static int constructor;
        public String phone_number;

        static {
            constructor = 1877286395;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_checkedPhone.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
        }
    }

    public static class TL_auth_checkedPhone extends TLObject {
        public static int constructor;
        public boolean phone_registered;

        static {
            constructor = -2128698738;
        }

        public static TL_auth_checkedPhone TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_auth_checkedPhone result = new TL_auth_checkedPhone();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_checkedPhone", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.phone_registered = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeBool(this.phone_registered);
        }
    }

    public static class TL_auth_exportAuthorization extends TLObject {
        public static int constructor;
        public int dc_id;

        static {
            constructor = -440401971;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_exportedAuthorization.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.dc_id);
        }
    }

    public static class TL_auth_exportedAuthorization extends TLObject {
        public static int constructor;
        public byte[] bytes;
        public int id;

        static {
            constructor = -543777747;
        }

        public static TL_auth_exportedAuthorization TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_auth_exportedAuthorization result = new TL_auth_exportedAuthorization();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_exportedAuthorization", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.bytes = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeByteArray(this.bytes);
        }
    }

    public static class TL_auth_importAuthorization extends TLObject {
        public static int constructor;
        public byte[] bytes;
        public int id;

        static {
            constructor = -470837741;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_authorization.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeByteArray(this.bytes);
        }
    }

    public static class TL_auth_logOut extends TLObject {
        public static int constructor;

        static {
            constructor = 1461180992;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_auth_passwordRecovery extends TLObject {
        public static int constructor;
        public String email_pattern;

        static {
            constructor = 326715557;
        }

        public static TL_auth_passwordRecovery TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_auth_passwordRecovery result = new TL_auth_passwordRecovery();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_passwordRecovery", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.email_pattern = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.email_pattern);
        }
    }

    public static class TL_auth_recoverPassword extends TLObject {
        public static int constructor;
        public String code;

        static {
            constructor = 1319464594;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_authorization.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.code);
        }
    }

    public static class TL_auth_requestPasswordRecovery extends TLObject {
        public static int constructor;

        static {
            constructor = -661144474;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_passwordRecovery.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_auth_resendCode extends TLObject {
        public static int constructor;
        public String phone_code_hash;
        public String phone_number;

        static {
            constructor = 1056025023;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_sentCode.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.phone_code_hash);
        }
    }

    public static class TL_auth_resetAuthorizations extends TLObject {
        public static int constructor;

        static {
            constructor = -1616179942;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_auth_sendCode extends TLObject {
        public static int constructor;
        public boolean allow_flashcall;
        public String api_hash;
        public int api_id;
        public boolean current_number;
        public int flags;
        public String phone_number;

        static {
            constructor = -2035355412;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_sentCode.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.allow_flashcall ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            stream.writeString(this.phone_number);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeBool(this.current_number);
            }
            stream.writeInt32(this.api_id);
            stream.writeString(this.api_hash);
        }
    }

    public static class TL_auth_sendInvites extends TLObject {
        public static int constructor;
        public String message;
        public ArrayList<String> phone_numbers;

        public TL_auth_sendInvites() {
            this.phone_numbers = new ArrayList();
        }

        static {
            constructor = 1998331287;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.phone_numbers.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeString((String) this.phone_numbers.get(a));
            }
            stream.writeString(this.message);
        }
    }

    public static class TL_auth_sentCode extends TLObject {
        public static int constructor;
        public int flags;
        public auth_CodeType next_type;
        public String phone_code_hash;
        public boolean phone_registered;
        public int timeout;
        public auth_SentCodeType type;

        static {
            constructor = 1577067778;
        }

        public static TL_auth_sentCode TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_auth_sentCode result = new TL_auth_sentCode();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_auth_sentCode", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.phone_registered = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            this.type = auth_SentCodeType.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.phone_code_hash = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.next_type = auth_CodeType.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.timeout = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.phone_registered ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            this.type.serializeToStream(stream);
            stream.writeString(this.phone_code_hash);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.next_type.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.timeout);
            }
        }
    }

    public static class TL_auth_signIn extends TLObject {
        public static int constructor;
        public String phone_code;
        public String phone_code_hash;
        public String phone_number;

        static {
            constructor = -1126886015;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_authorization.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.phone_code_hash);
            stream.writeString(this.phone_code);
        }
    }

    public static class TL_auth_signUp extends TLObject {
        public static int constructor;
        public String first_name;
        public String last_name;
        public String phone_code;
        public String phone_code_hash;
        public String phone_number;

        static {
            constructor = 453408308;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_auth_authorization.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.phone_code_hash);
            stream.writeString(this.phone_code);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
        }
    }

    public static class TL_authorization extends TLObject {
        public static int constructor;
        public int api_id;
        public String app_name;
        public String app_version;
        public String country;
        public int date_active;
        public int date_created;
        public String device_model;
        public int flags;
        public long hash;
        public String ip;
        public String platform;
        public String region;
        public String system_version;

        static {
            constructor = 2079516406;
        }

        public static TL_authorization TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_authorization result = new TL_authorization();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_authorization", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.hash = stream.readInt64(exception);
            this.flags = stream.readInt32(exception);
            this.device_model = stream.readString(exception);
            this.platform = stream.readString(exception);
            this.system_version = stream.readString(exception);
            this.api_id = stream.readInt32(exception);
            this.app_name = stream.readString(exception);
            this.app_version = stream.readString(exception);
            this.date_created = stream.readInt32(exception);
            this.date_active = stream.readInt32(exception);
            this.ip = stream.readString(exception);
            this.country = stream.readString(exception);
            this.region = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.hash);
            stream.writeInt32(this.flags);
            stream.writeString(this.device_model);
            stream.writeString(this.platform);
            stream.writeString(this.system_version);
            stream.writeInt32(this.api_id);
            stream.writeString(this.app_name);
            stream.writeString(this.app_version);
            stream.writeInt32(this.date_created);
            stream.writeInt32(this.date_active);
            stream.writeString(this.ip);
            stream.writeString(this.country);
            stream.writeString(this.region);
        }
    }

    public static class TL_botCommand extends TLObject {
        public static int constructor;
        public String command;
        public String description;

        static {
            constructor = -1032140601;
        }

        public static TL_botCommand TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_botCommand result = new TL_botCommand();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_botCommand", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.command = stream.readString(exception);
            this.description = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.command);
            stream.writeString(this.description);
        }
    }

    public static class TL_channels_channelParticipant extends TLObject {
        public static int constructor;
        public ChannelParticipant participant;
        public ArrayList<User> users;

        public TL_channels_channelParticipant() {
            this.users = new ArrayList();
        }

        static {
            constructor = -791039645;
        }

        public static TL_channels_channelParticipant TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_channels_channelParticipant result = new TL_channels_channelParticipant();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_channels_channelParticipant", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.participant = ChannelParticipant.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    User object = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.users.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.participant.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_channels_channelParticipants extends TLObject {
        public static int constructor;
        public int count;
        public ArrayList<ChannelParticipant> participants;
        public ArrayList<User> users;

        public TL_channels_channelParticipants() {
            this.participants = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = -177282392;
        }

        public static TL_channels_channelParticipants TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_channels_channelParticipants result = new TL_channels_channelParticipants();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_channels_channelParticipants", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    ChannelParticipant object = ChannelParticipant.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.participants.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.participants.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((ChannelParticipant) this.participants.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_channels_checkUsername extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public String username;

        static {
            constructor = 283557164;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeString(this.username);
        }
    }

    public static class TL_channels_createChannel extends TLObject {
        public static int constructor;
        public String about;
        public boolean broadcast;
        public int flags;
        public boolean megagroup;
        public String title;

        static {
            constructor = -192332417;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.broadcast ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.megagroup ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            stream.writeInt32(this.flags);
            stream.writeString(this.title);
            stream.writeString(this.about);
        }
    }

    public static class TL_channels_deleteChannel extends TLObject {
        public static int constructor;
        public InputChannel channel;

        static {
            constructor = -1072619549;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
        }
    }

    public static class TL_channels_deleteMessages extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ArrayList<Integer> id;

        public TL_channels_deleteMessages() {
            this.id = new ArrayList();
        }

        static {
            constructor = -2067661490;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_affectedMessages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_channels_deleteUserHistory extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public InputUser user_id;

        static {
            constructor = -787622117;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_affectedHistory.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.user_id.serializeToStream(stream);
        }
    }

    public static class TL_channels_editAbout extends TLObject {
        public static int constructor;
        public String about;
        public InputChannel channel;

        static {
            constructor = 333610782;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeString(this.about);
        }
    }

    public static class TL_channels_editAdmin extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ChannelParticipantRole role;
        public InputUser user_id;

        static {
            constructor = -344583728;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.user_id.serializeToStream(stream);
            this.role.serializeToStream(stream);
        }
    }

    public static class TL_channels_editPhoto extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public InputChatPhoto photo;

        static {
            constructor = -248621111;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.photo.serializeToStream(stream);
        }
    }

    public static class TL_channels_editTitle extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public String title;

        static {
            constructor = 1450044624;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeString(this.title);
        }
    }

    public static class TL_channels_exportInvite extends TLObject {
        public static int constructor;
        public InputChannel channel;

        static {
            constructor = -950663035;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return ExportedChatInvite.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
        }
    }

    public static class TL_channels_exportMessageLink extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public int id;

        static {
            constructor = -934882771;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_exportedMessageLink.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_channels_getChannels extends TLObject {
        public static int constructor;
        public ArrayList<InputChannel> id;

        public TL_channels_getChannels() {
            this.id = new ArrayList();
        }

        static {
            constructor = 176122811;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_chats.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputChannel) this.id.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_channels_getFullChannel extends TLObject {
        public static int constructor;
        public InputChannel channel;

        static {
            constructor = 141781513;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_chatFull.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
        }
    }

    public static class TL_channels_getMessages extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ArrayList<Integer> id;

        public TL_channels_getMessages() {
            this.id = new ArrayList();
        }

        static {
            constructor = -1814580409;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_channels_getParticipant extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public InputUser user_id;

        static {
            constructor = 1416484774;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_channels_channelParticipant.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.user_id.serializeToStream(stream);
        }
    }

    public static class TL_channels_getParticipants extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ChannelParticipantsFilter filter;
        public int limit;
        public int offset;

        static {
            constructor = 618237842;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_channels_channelParticipants.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.filter.serializeToStream(stream);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_channels_inviteToChannel extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ArrayList<InputUser> users;

        public TL_channels_inviteToChannel() {
            this.users = new ArrayList();
        }

        static {
            constructor = 429865580;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_channels_joinChannel extends TLObject {
        public static int constructor;
        public InputChannel channel;

        static {
            constructor = 615851205;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
        }
    }

    public static class TL_channels_kickFromChannel extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public boolean kicked;
        public InputUser user_id;

        static {
            constructor = -1502421484;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.user_id.serializeToStream(stream);
            stream.writeBool(this.kicked);
        }
    }

    public static class TL_channels_leaveChannel extends TLObject {
        public static int constructor;
        public InputChannel channel;

        static {
            constructor = -130635115;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
        }
    }

    public static class TL_channels_readHistory extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public int max_id;

        static {
            constructor = -871347913;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_channels_reportSpam extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ArrayList<Integer> id;
        public InputUser user_id;

        public TL_channels_reportSpam() {
            this.id = new ArrayList();
        }

        static {
            constructor = -32999408;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.user_id.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_channels_toggleInvites extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public boolean enabled;

        static {
            constructor = 1231065863;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeBool(this.enabled);
        }
    }

    public static class TL_channels_toggleSignatures extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public boolean enabled;

        static {
            constructor = 527021574;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeBool(this.enabled);
        }
    }

    public static class TL_channels_updatePinnedMessage extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public int flags;
        public int id;
        public boolean silent;

        static {
            constructor = -1490162350;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            this.channel.serializeToStream(stream);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_channels_updateUsername extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public String username;

        static {
            constructor = 890549214;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            stream.writeString(this.username);
        }
    }

    public static class TL_chatLocated extends TLObject {
        public static int constructor;
        public int chat_id;
        public int distance;

        static {
            constructor = 909233996;
        }

        public static TL_chatLocated TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_chatLocated result = new TL_chatLocated();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_chatLocated", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.distance = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.distance);
        }
    }

    public static class TL_config extends TLObject {
        public static int constructor;
        public int chat_big_size;
        public int chat_size_max;
        public int date;
        public ArrayList<TL_dcOption> dc_options;
        public ArrayList<TL_disabledFeature> disabled_features;
        public int edit_time_limit;
        public int expires;
        public int forwarded_count_max;
        public int megagroup_size_max;
        public int notify_cloud_delay_ms;
        public int notify_default_delay_ms;
        public int offline_blur_timeout_ms;
        public int offline_idle_timeout_ms;
        public int online_cloud_timeout_ms;
        public int online_update_period_ms;
        public int push_chat_limit;
        public int push_chat_period_ms;
        public int rating_e_decay;
        public int saved_gifs_limit;
        public boolean test_mode;
        public int this_dc;

        public TL_config() {
            this.dc_options = new ArrayList();
            this.disabled_features = new ArrayList();
        }

        static {
            constructor = -918482040;
        }

        public static TL_config TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_config result = new TL_config();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_config", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.date = stream.readInt32(exception);
            this.expires = stream.readInt32(exception);
            this.test_mode = stream.readBool(exception);
            this.this_dc = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_dcOption object = TL_dcOption.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.dc_options.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.chat_size_max = stream.readInt32(exception);
                this.megagroup_size_max = stream.readInt32(exception);
                this.forwarded_count_max = stream.readInt32(exception);
                this.online_update_period_ms = stream.readInt32(exception);
                this.offline_blur_timeout_ms = stream.readInt32(exception);
                this.offline_idle_timeout_ms = stream.readInt32(exception);
                this.online_cloud_timeout_ms = stream.readInt32(exception);
                this.notify_cloud_delay_ms = stream.readInt32(exception);
                this.notify_default_delay_ms = stream.readInt32(exception);
                this.chat_big_size = stream.readInt32(exception);
                this.push_chat_period_ms = stream.readInt32(exception);
                this.push_chat_limit = stream.readInt32(exception);
                this.saved_gifs_limit = stream.readInt32(exception);
                this.edit_time_limit = stream.readInt32(exception);
                this.rating_e_decay = stream.readInt32(exception);
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        TL_disabledFeature object2 = TL_disabledFeature.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.disabled_features.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.date);
            stream.writeInt32(this.expires);
            stream.writeBool(this.test_mode);
            stream.writeInt32(this.this_dc);
            stream.writeInt32(481674261);
            int count = this.dc_options.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_dcOption) this.dc_options.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(this.chat_size_max);
            stream.writeInt32(this.megagroup_size_max);
            stream.writeInt32(this.forwarded_count_max);
            stream.writeInt32(this.online_update_period_ms);
            stream.writeInt32(this.offline_blur_timeout_ms);
            stream.writeInt32(this.offline_idle_timeout_ms);
            stream.writeInt32(this.online_cloud_timeout_ms);
            stream.writeInt32(this.notify_cloud_delay_ms);
            stream.writeInt32(this.notify_default_delay_ms);
            stream.writeInt32(this.chat_big_size);
            stream.writeInt32(this.push_chat_period_ms);
            stream.writeInt32(this.push_chat_limit);
            stream.writeInt32(this.saved_gifs_limit);
            stream.writeInt32(this.edit_time_limit);
            stream.writeInt32(this.rating_e_decay);
            stream.writeInt32(481674261);
            count = this.disabled_features.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_disabledFeature) this.disabled_features.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contact extends TLObject {
        public static int constructor;
        public boolean mutual;
        public int user_id;

        static {
            constructor = -116274796;
        }

        public static TL_contact TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contact result = new TL_contact();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contact", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.mutual = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeBool(this.mutual);
        }
    }

    public static class TL_contactBlocked extends TLObject {
        public static int constructor;
        public int date;
        public int user_id;

        static {
            constructor = 1444661369;
        }

        public static TL_contactBlocked TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contactBlocked result = new TL_contactBlocked();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contactBlocked", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_contactFound extends TLObject {
        public static int constructor;
        public int user_id;

        static {
            constructor = -360210539;
        }

        public static TL_contactFound TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contactFound result = new TL_contactFound();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contactFound", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_contactStatus extends TLObject {
        public static int constructor;
        public UserStatus status;
        public int user_id;

        static {
            constructor = -748155807;
        }

        public static TL_contactStatus TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contactStatus result = new TL_contactStatus();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contactStatus", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_contacts_block extends TLObject {
        public static int constructor;
        public InputUser id;

        static {
            constructor = 858475004;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
        }
    }

    public static class TL_contacts_deleteContact extends TLObject {
        public static int constructor;
        public InputUser id;

        static {
            constructor = -1902823612;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_contacts_link.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
        }
    }

    public static class TL_contacts_deleteContacts extends TLObject {
        public static int constructor;
        public ArrayList<InputUser> id;

        public TL_contacts_deleteContacts() {
            this.id = new ArrayList();
        }

        static {
            constructor = 1504393374;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.id.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_exportCard extends TLObject {
        public static int constructor;

        static {
            constructor = -2065352905;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                vector.objects.add(Integer.valueOf(stream.readInt32(exception)));
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contacts_found extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;
        public ArrayList<Peer> results;
        public ArrayList<User> users;

        public TL_contacts_found() {
            this.results = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = 446822276;
        }

        public static TL_contacts_found TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contacts_found result = new TL_contacts_found();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contacts_found", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Peer object = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.results.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.results.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Peer) this.results.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_getBlocked extends TLObject {
        public static int constructor;
        public int limit;
        public int offset;

        static {
            constructor = -176409329;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return contacts_Blocked.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_contacts_getContacts extends TLObject {
        public static int constructor;
        public String hash;

        static {
            constructor = 583445000;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return contacts_Contacts.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.hash);
        }
    }

    public static class TL_contacts_getStatuses extends TLObject {
        public static int constructor;

        static {
            constructor = -995929106;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                TL_contactStatus object = TL_contactStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
                if (object == null) {
                    break;
                }
                vector.objects.add(object);
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contacts_getTopPeers extends TLObject {
        public static int constructor;
        public boolean bots_inline;
        public boolean bots_pm;
        public boolean channels;
        public boolean correspondents;
        public int flags;
        public boolean groups;
        public int hash;
        public int limit;
        public int offset;

        static {
            constructor = -728224331;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return contacts_TopPeers.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.correspondents ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.bots_pm ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.bots_inline ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.groups ? this.flags | TLRPC.MESSAGE_FLAG_HAS_VIEWS : this.flags & -1025;
            this.flags = this.channels ? this.flags | TLRPC.MESSAGE_FLAG_EDITED : this.flags & -32769;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.limit);
            stream.writeInt32(this.hash);
        }
    }

    public static class TL_contacts_importCard extends TLObject {
        public static int constructor;
        public ArrayList<Integer> export_card;

        public TL_contacts_importCard() {
            this.export_card = new ArrayList();
        }

        static {
            constructor = 1340184318;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return User.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.export_card.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.export_card.get(a)).intValue());
            }
        }
    }

    public static class TL_contacts_importContacts extends TLObject {
        public static int constructor;
        public ArrayList<TL_inputPhoneContact> contacts;
        public boolean replace;

        public TL_contacts_importContacts() {
            this.contacts = new ArrayList();
        }

        static {
            constructor = -634342611;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_contacts_importedContacts.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.contacts.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_inputPhoneContact) this.contacts.get(a)).serializeToStream(stream);
            }
            stream.writeBool(this.replace);
        }
    }

    public static class TL_contacts_importedContacts extends TLObject {
        public static int constructor;
        public ArrayList<TL_importedContact> imported;
        public ArrayList<Long> retry_contacts;
        public ArrayList<User> users;

        public TL_contacts_importedContacts() {
            this.imported = new ArrayList();
            this.retry_contacts = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = -1387117803;
        }

        public static TL_contacts_importedContacts TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contacts_importedContacts result = new TL_contacts_importedContacts();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contacts_importedContacts", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_importedContact object = TL_importedContact.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.imported.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                        this.retry_contacts.add(Long.valueOf(stream.readInt64(exception)));
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object2 != null) {
                                this.users.add(object2);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.imported.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_importedContact) this.imported.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.retry_contacts.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.retry_contacts.get(a)).longValue());
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_link extends TLObject {
        public static int constructor;
        public ContactLink foreign_link;
        public ContactLink my_link;
        public User user;

        static {
            constructor = 986597452;
        }

        public static TL_contacts_link TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contacts_link result = new TL_contacts_link();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contacts_link", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.my_link = ContactLink.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.foreign_link = ContactLink.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.user = User.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.my_link.serializeToStream(stream);
            this.foreign_link.serializeToStream(stream);
            this.user.serializeToStream(stream);
        }
    }

    public static class TL_contacts_resetTopPeerRating extends TLObject {
        public static int constructor;
        public TopPeerCategory category;
        public InputPeer peer;

        static {
            constructor = 451113900;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.category.serializeToStream(stream);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_contacts_resolveUsername extends TLObject {
        public static int constructor;
        public String username;

        static {
            constructor = -113456221;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_contacts_resolvedPeer.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.username);
        }
    }

    public static class TL_contacts_resolvedPeer extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;
        public Peer peer;
        public ArrayList<User> users;

        public TL_contacts_resolvedPeer() {
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = 2131196633;
        }

        public static TL_contacts_resolvedPeer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_contacts_resolvedPeer result = new TL_contacts_resolvedPeer();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_contacts_resolvedPeer", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Chat object = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.chats.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_search extends TLObject {
        public static int constructor;
        public int limit;
        public String f35q;

        static {
            constructor = 301470424;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_contacts_found.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.f35q);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_contacts_unblock extends TLObject {
        public static int constructor;
        public InputUser id;

        static {
            constructor = -448724803;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
        }
    }

    public static class TL_dcOption extends TLObject {
        public static int constructor;
        public int flags;
        public int id;
        public String ip_address;
        public boolean ipv6;
        public boolean media_only;
        public int port;
        public boolean tcpo_only;

        static {
            constructor = 98092748;
        }

        public static TL_dcOption TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_dcOption result = new TL_dcOption();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_dcOption", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.ipv6 = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_only = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) == 0) {
                z2 = false;
            }
            this.tcpo_only = z2;
            this.id = stream.readInt32(exception);
            this.ip_address = stream.readString(exception);
            this.port = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.ipv6 ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.media_only ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.tcpo_only ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.ip_address);
            stream.writeInt32(this.port);
        }
    }

    public static class TL_decryptedMessageLayer extends TLObject {
        public static int constructor;
        public int in_seq_no;
        public int layer;
        public DecryptedMessage message;
        public int out_seq_no;
        public byte[] random_bytes;

        static {
            constructor = 467867529;
        }

        public static TL_decryptedMessageLayer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_decryptedMessageLayer result = new TL_decryptedMessageLayer();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_decryptedMessageLayer", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_bytes = stream.readByteArray(exception);
            this.layer = stream.readInt32(exception);
            this.in_seq_no = stream.readInt32(exception);
            this.out_seq_no = stream.readInt32(exception);
            this.message = DecryptedMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.random_bytes);
            stream.writeInt32(this.layer);
            stream.writeInt32(this.in_seq_no);
            stream.writeInt32(this.out_seq_no);
            this.message.serializeToStream(stream);
        }
    }

    public static class TL_dialog extends TLObject {
        public static int constructor;
        public DraftMessage draft;
        public int flags;
        public long id;
        public int last_message_date;
        public PeerNotifySettings notify_settings;
        public Peer peer;
        public int pts;
        public int read_inbox_max_id;
        public int read_outbox_max_id;
        public int top_message;
        public int unread_count;

        static {
            constructor = 1728035348;
        }

        public static TL_dialog TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_dialog result = new TL_dialog();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_dialog", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.top_message = stream.readInt32(exception);
            this.read_inbox_max_id = stream.readInt32(exception);
            this.read_outbox_max_id = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.pts = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.draft = DraftMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.top_message);
            stream.writeInt32(this.read_inbox_max_id);
            stream.writeInt32(this.read_outbox_max_id);
            stream.writeInt32(this.unread_count);
            this.notify_settings.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.pts);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.draft.serializeToStream(stream);
            }
        }
    }

    public static class TL_disabledFeature extends TLObject {
        public static int constructor;
        public String description;
        public String feature;

        static {
            constructor = -1369215196;
        }

        public static TL_disabledFeature TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_disabledFeature result = new TL_disabledFeature();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_disabledFeature", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.feature = stream.readString(exception);
            this.description = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.feature);
            stream.writeString(this.description);
        }
    }

    public static class TL_error extends TLObject {
        public static int constructor;
        public int code;
        public String text;

        static {
            constructor = -994444869;
        }

        public static TL_error TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_error result = new TL_error();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_error", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.code = stream.readInt32(exception);
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.code);
            stream.writeString(this.text);
        }
    }

    public static class TL_exportedMessageLink extends TLObject {
        public static int constructor;
        public String link;

        static {
            constructor = 524838915;
        }

        public static TL_exportedMessageLink TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_exportedMessageLink result = new TL_exportedMessageLink();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_exportedMessageLink", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.link = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.link);
        }
    }

    public static class TL_geochats_checkin extends TLObject {
        public static int constructor;
        public TL_inputGeoChat peer;

        static {
            constructor = 1437853947;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_statedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_geochats_createGeoChat extends TLObject {
        public static int constructor;
        public String address;
        public InputGeoPoint geo_point;
        public String title;
        public String venue;

        static {
            constructor = 235482646;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_statedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
            this.geo_point.serializeToStream(stream);
            stream.writeString(this.address);
            stream.writeString(this.venue);
        }
    }

    public static class TL_geochats_editChatPhoto extends TLObject {
        public static int constructor;
        public TL_inputGeoChat peer;
        public InputChatPhoto photo;

        static {
            constructor = 903355029;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_statedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.photo.serializeToStream(stream);
        }
    }

    public static class TL_geochats_editChatTitle extends TLObject {
        public static int constructor;
        public String address;
        public TL_inputGeoChat peer;
        public String title;

        static {
            constructor = 1284383347;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_statedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeString(this.title);
            stream.writeString(this.address);
        }
    }

    public static class TL_geochats_getFullChat extends TLObject {
        public static int constructor;
        public TL_inputGeoChat peer;

        static {
            constructor = 1730338159;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_chatFull.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_geochats_getHistory extends TLObject {
        public static int constructor;
        public int limit;
        public int max_id;
        public int offset;
        public TL_inputGeoChat peer;

        static {
            constructor = -1254131096;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return geochats_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_geochats_getLocated extends TLObject {
        public static int constructor;
        public InputGeoPoint geo_point;
        public int limit;
        public int radius;

        static {
            constructor = 2132356495;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_located.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.geo_point.serializeToStream(stream);
            stream.writeInt32(this.radius);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_geochats_getRecents extends TLObject {
        public static int constructor;
        public int limit;
        public int offset;

        static {
            constructor = -515735953;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return geochats_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_geochats_located extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;
        public ArrayList<GeoChatMessage> messages;
        public ArrayList<TL_chatLocated> results;
        public ArrayList<User> users;

        public TL_geochats_located() {
            this.results = new ArrayList();
            this.messages = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = 1224651367;
        }

        public static TL_geochats_located TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_geochats_located result = new TL_geochats_located();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_geochats_located", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_chatLocated object = TL_chatLocated.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.results.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        GeoChatMessage object2 = GeoChatMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.messages.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                User object4 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.users.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.results.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_chatLocated) this.results.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((GeoChatMessage) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_geochats_search extends TLObject {
        public static int constructor;
        public MessagesFilter filter;
        public int limit;
        public int max_date;
        public int max_id;
        public int min_date;
        public int offset;
        public TL_inputGeoChat peer;
        public String f36q;

        static {
            constructor = -808598451;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return geochats_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeString(this.f36q);
            this.filter.serializeToStream(stream);
            stream.writeInt32(this.min_date);
            stream.writeInt32(this.max_date);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_geochats_sendMedia extends TLObject {
        public static int constructor;
        public InputMedia media;
        public TL_inputGeoChat peer;
        public long random_id;

        static {
            constructor = -1192173825;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_statedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.media.serializeToStream(stream);
            stream.writeInt64(this.random_id);
        }
    }

    public static class TL_geochats_sendMessage extends TLObject {
        public static int constructor;
        public String message;
        public TL_inputGeoChat peer;
        public long random_id;

        static {
            constructor = 102432836;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_geochats_statedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeString(this.message);
            stream.writeInt64(this.random_id);
        }
    }

    public static class TL_geochats_setTyping extends TLObject {
        public static int constructor;
        public TL_inputGeoChat peer;
        public boolean typing;

        static {
            constructor = 146319145;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeBool(this.typing);
        }
    }

    public static class TL_geochats_statedMessage extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;
        public GeoChatMessage message;
        public int seq;
        public ArrayList<User> users;

        public TL_geochats_statedMessage() {
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = 397498251;
        }

        public static TL_geochats_statedMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_geochats_statedMessage result = new TL_geochats_statedMessage();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_geochats_statedMessage", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = GeoChatMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Chat object = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.chats.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    this.seq = stream.readInt32(exception);
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(this.seq);
        }
    }

    public static class TL_help_getAppChangelog extends TLObject {
        public static int constructor;

        static {
            constructor = -1189013126;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return help_AppChangelog.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_getAppUpdate extends TLObject {
        public static int constructor;

        static {
            constructor = -1372724842;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return help_AppUpdate.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_getConfig extends TLObject {
        public static int constructor;

        static {
            constructor = -990308245;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_config.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_getInviteText extends TLObject {
        public static int constructor;

        static {
            constructor = 1295590211;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_help_inviteText.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_getNearestDc extends TLObject {
        public static int constructor;

        static {
            constructor = 531836966;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_nearestDc.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_getSupport extends TLObject {
        public static int constructor;

        static {
            constructor = -1663104819;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_help_support.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_getTermsOfService extends TLObject {
        public static int constructor;

        static {
            constructor = 889286899;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_help_termsOfService.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_inviteText extends TLObject {
        public static int constructor;
        public String message;

        static {
            constructor = 415997816;
        }

        public static TL_help_inviteText TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_help_inviteText result = new TL_help_inviteText();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_help_inviteText", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.message);
        }
    }

    public static class TL_help_saveAppLog extends TLObject {
        public static int constructor;
        public ArrayList<TL_inputAppEvent> events;

        public TL_help_saveAppLog() {
            this.events = new ArrayList();
        }

        static {
            constructor = 1862465352;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.events.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_inputAppEvent) this.events.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_help_support extends TLObject {
        public static int constructor;
        public String phone_number;
        public User user;

        static {
            constructor = 398898678;
        }

        public static TL_help_support TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_help_support result = new TL_help_support();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_help_support", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.phone_number = stream.readString(exception);
            this.user = User.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            this.user.serializeToStream(stream);
        }
    }

    public static class TL_help_termsOfService extends TLObject {
        public static int constructor;
        public String text;

        static {
            constructor = -236044656;
        }

        public static TL_help_termsOfService TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_help_termsOfService result = new TL_help_termsOfService();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_help_termsOfService", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
        }
    }

    public static class TL_importedContact extends TLObject {
        public static int constructor;
        public long client_id;
        public int user_id;

        static {
            constructor = -805141448;
        }

        public static TL_importedContact TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_importedContact result = new TL_importedContact();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_importedContact", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.client_id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt64(this.client_id);
        }
    }

    public static class TL_inlineBotSwitchPM extends TLObject {
        public static int constructor;
        public String start_param;
        public String text;

        static {
            constructor = 1008755359;
        }

        public static TL_inlineBotSwitchPM TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inlineBotSwitchPM result = new TL_inlineBotSwitchPM();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inlineBotSwitchPM", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
            this.start_param = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
            stream.writeString(this.start_param);
        }
    }

    public static class TL_inputAppEvent extends TLObject {
        public static int constructor;
        public String data;
        public long peer;
        public double time;
        public String type;

        static {
            constructor = 1996904104;
        }

        public static TL_inputAppEvent TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inputAppEvent result = new TL_inputAppEvent();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputAppEvent", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.time = stream.readDouble(exception);
            this.type = stream.readString(exception);
            this.peer = stream.readInt64(exception);
            this.data = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeDouble(this.time);
            stream.writeString(this.type);
            stream.writeInt64(this.peer);
            stream.writeString(this.data);
        }
    }

    public static class TL_inputBotInlineMessageID extends TLObject {
        public static int constructor;
        public long access_hash;
        public int dc_id;
        public long id;

        static {
            constructor = -1995686519;
        }

        public static TL_inputBotInlineMessageID TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inputBotInlineMessageID result = new TL_inputBotInlineMessageID();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputBotInlineMessageID", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.dc_id = stream.readInt32(exception);
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.dc_id);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputEncryptedChat extends TLObject {
        public static int constructor;
        public long access_hash;
        public int chat_id;

        static {
            constructor = -247351839;
        }

        public static TL_inputEncryptedChat TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inputEncryptedChat result = new TL_inputEncryptedChat();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputEncryptedChat", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputGeoChat extends TLObject {
        public static int constructor;
        public long access_hash;
        public int chat_id;

        static {
            constructor = 1960072954;
        }

        public static TL_inputGeoChat TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inputGeoChat result = new TL_inputGeoChat();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputGeoChat", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputPeerNotifySettings extends TLObject {
        public static int constructor;
        public int flags;
        public int mute_until;
        public boolean show_previews;
        public boolean silent;
        public String sound;

        static {
            constructor = 949182130;
        }

        public static TL_inputPeerNotifySettings TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inputPeerNotifySettings result = new TL_inputPeerNotifySettings();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputPeerNotifySettings", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.show_previews = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) == 0) {
                z2 = false;
            }
            this.silent = z2;
            this.mute_until = stream.readInt32(exception);
            this.sound = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.show_previews ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.mute_until);
            stream.writeString(this.sound);
        }
    }

    public static class TL_inputPhoneContact extends TLObject {
        public static int constructor;
        public long client_id;
        public String first_name;
        public String last_name;
        public String phone;

        static {
            constructor = -208488460;
        }

        public static TL_inputPhoneContact TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_inputPhoneContact result = new TL_inputPhoneContact();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_inputPhoneContact", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.client_id = stream.readInt64(exception);
            this.phone = stream.readString(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.client_id);
            stream.writeString(this.phone);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
        }
    }

    public static class TL_keyboardButtonRow extends TLObject {
        public static int constructor;
        public ArrayList<KeyboardButton> buttons;

        public TL_keyboardButtonRow() {
            this.buttons = new ArrayList();
        }

        static {
            constructor = 2002815875;
        }

        public static TL_keyboardButtonRow TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_keyboardButtonRow result = new TL_keyboardButtonRow();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_keyboardButtonRow", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    KeyboardButton object = KeyboardButton.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.buttons.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.buttons.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((KeyboardButton) this.buttons.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messageFwdHeader extends TLObject {
        public static int constructor;
        public int channel_id;
        public int channel_post;
        public int date;
        public int flags;
        public int from_id;

        static {
            constructor = -947462709;
        }

        public static TL_messageFwdHeader TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messageFwdHeader result = new TL_messageFwdHeader();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messageFwdHeader", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.from_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.channel_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.channel_post = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.from_id);
            }
            stream.writeInt32(this.date);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.channel_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.channel_post);
            }
        }
    }

    public static class TL_messageRange extends TLObject {
        public static int constructor;
        public int max_id;
        public int min_id;

        static {
            constructor = 182649427;
        }

        public static TL_messageRange TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messageRange result = new TL_messageRange();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messageRange", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.min_id = stream.readInt32(exception);
            this.max_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.min_id);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_messages_acceptEncryption extends TLObject {
        public static int constructor;
        public byte[] g_b;
        public long key_fingerprint;
        public TL_inputEncryptedChat peer;

        static {
            constructor = 1035731989;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return EncryptedChat.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeByteArray(this.g_b);
            stream.writeInt64(this.key_fingerprint);
        }
    }

    public static class TL_messages_addChatUser extends TLObject {
        public static int constructor;
        public int chat_id;
        public int fwd_limit;
        public InputUser user_id;

        static {
            constructor = -106911223;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            this.user_id.serializeToStream(stream);
            stream.writeInt32(this.fwd_limit);
        }
    }

    public static class TL_messages_affectedHistory extends TLObject {
        public static int constructor;
        public int offset;
        public int pts;
        public int pts_count;

        static {
            constructor = -1269012015;
        }

        public static TL_messages_affectedHistory TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_affectedHistory result = new TL_messages_affectedHistory();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_affectedHistory", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
            this.offset = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
            stream.writeInt32(this.offset);
        }
    }

    public static class TL_messages_affectedMessages extends TLObject {
        public static int constructor;
        public int pts;
        public int pts_count;

        static {
            constructor = -2066640507;
        }

        public static TL_messages_affectedMessages TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_affectedMessages result = new TL_messages_affectedMessages();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_affectedMessages", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_messages_botCallbackAnswer extends TLObject {
        public static int constructor;
        public boolean alert;
        public int flags;
        public String message;

        static {
            constructor = 308605382;
        }

        public static TL_messages_botCallbackAnswer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_botCallbackAnswer result = new TL_messages_botCallbackAnswer();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_botCallbackAnswer", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.alert = (this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0;
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.message = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.alert ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            stream.writeInt32(this.flags);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.message);
            }
        }
    }

    public static class TL_messages_botResults extends TLObject {
        public static int constructor;
        public int flags;
        public boolean gallery;
        public String next_offset;
        public long query_id;
        public ArrayList<BotInlineResult> results;
        public TL_inlineBotSwitchPM switch_pm;

        public TL_messages_botResults() {
            this.results = new ArrayList();
        }

        static {
            constructor = 627509670;
        }

        public static TL_messages_botResults TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_botResults result = new TL_messages_botResults();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_botResults", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.gallery = z;
            this.query_id = stream.readInt64(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.next_offset = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.switch_pm = TL_inlineBotSwitchPM.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    BotInlineResult object = BotInlineResult.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.results.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.gallery) {
                i = this.flags | TLRPC.USER_FLAG_ACCESS_HASH;
            } else {
                i = this.flags & -2;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt64(this.query_id);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.next_offset);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.switch_pm.serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            int count = this.results.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((BotInlineResult) this.results.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_chatFull extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;
        public ChatFull full_chat;
        public ArrayList<User> users;

        public TL_messages_chatFull() {
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = -438840932;
        }

        public static TL_messages_chatFull TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_chatFull result = new TL_messages_chatFull();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_chatFull", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.full_chat = ChatFull.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Chat object = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.chats.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            this.full_chat.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_chats extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;

        public TL_messages_chats() {
            this.chats = new ArrayList();
        }

        static {
            constructor = 1694474197;
        }

        public static TL_messages_chats TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_chats result = new TL_messages_chats();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_chats", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Chat object = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.chats.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.chats.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_checkChatInvite extends TLObject {
        public static int constructor;
        public String hash;

        static {
            constructor = 1051570619;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return ChatInvite.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.hash);
        }
    }

    public static class TL_messages_createChat extends TLObject {
        public static int constructor;
        public String title;
        public ArrayList<InputUser> users;

        public TL_messages_createChat() {
            this.users = new ArrayList();
        }

        static {
            constructor = 164303470;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.users.get(a)).serializeToStream(stream);
            }
            stream.writeString(this.title);
        }
    }

    public static class TL_messages_deleteChatUser extends TLObject {
        public static int constructor;
        public int chat_id;
        public InputUser user_id;

        static {
            constructor = -530505962;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            this.user_id.serializeToStream(stream);
        }
    }

    public static class TL_messages_deleteHistory extends TLObject {
        public static int constructor;
        public int flags;
        public boolean just_clear;
        public int max_id;
        public InputPeer peer;

        static {
            constructor = 469850889;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_affectedHistory.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.just_clear ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_messages_deleteMessages extends TLObject {
        public static int constructor;
        public ArrayList<Integer> id;

        public TL_messages_deleteMessages() {
            this.id = new ArrayList();
        }

        static {
            constructor = -1510897371;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_affectedMessages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_messages_discardEncryption extends TLObject {
        public static int constructor;
        public int chat_id;

        static {
            constructor = -304536635;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_messages_editChatAdmin extends TLObject {
        public static int constructor;
        public int chat_id;
        public boolean is_admin;
        public InputUser user_id;

        static {
            constructor = -1444503762;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            this.user_id.serializeToStream(stream);
            stream.writeBool(this.is_admin);
        }
    }

    public static class TL_messages_editChatPhoto extends TLObject {
        public static int constructor;
        public int chat_id;
        public InputChatPhoto photo;

        static {
            constructor = -900957736;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            this.photo.serializeToStream(stream);
        }
    }

    public static class TL_messages_editChatTitle extends TLObject {
        public static int constructor;
        public int chat_id;
        public String title;

        static {
            constructor = -599447467;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeString(this.title);
        }
    }

    public static class TL_messages_editInlineBotMessage extends TLObject {
        public static int constructor;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public TL_inputBotInlineMessageID id;
        public String message;
        public boolean no_webpage;
        public ReplyMarkup reply_markup;

        public TL_messages_editInlineBotMessage() {
            this.entities = new ArrayList();
        }

        static {
            constructor = 319564933;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.no_webpage) {
                i = this.flags | TLRPC.USER_FLAG_FIRST_NAME;
            } else {
                i = this.flags & -3;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            this.id.serializeToStream(stream);
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                stream.writeString(this.message);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
        }
    }

    public static class TL_messages_editMessage extends TLObject {
        public static int constructor;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public int id;
        public String message;
        public boolean no_webpage;
        public InputPeer peer;
        public ReplyMarkup reply_markup;

        public TL_messages_editMessage() {
            this.entities = new ArrayList();
        }

        static {
            constructor = -829299510;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.no_webpage) {
                i = this.flags | TLRPC.USER_FLAG_FIRST_NAME;
            } else {
                i = this.flags & -3;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                stream.writeString(this.message);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
        }
    }

    public static class TL_messages_exportChatInvite extends TLObject {
        public static int constructor;
        public int chat_id;

        static {
            constructor = 2106086025;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return ExportedChatInvite.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_messages_forwardMessage extends TLObject {
        public static int constructor;
        public int id;
        public InputPeer peer;
        public long random_id;

        static {
            constructor = 865483769;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.id);
            stream.writeInt64(this.random_id);
        }
    }

    public static class TL_messages_forwardMessages extends TLObject {
        public static int constructor;
        public boolean background;
        public int flags;
        public InputPeer from_peer;
        public ArrayList<Integer> id;
        public ArrayList<Long> random_id;
        public boolean silent;
        public InputPeer to_peer;

        public TL_messages_forwardMessages() {
            this.id = new ArrayList();
            this.random_id = new ArrayList();
        }

        static {
            constructor = 1888354709;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.background ? this.flags | TLRPC.USER_FLAG_STATUS : this.flags & -65;
            stream.writeInt32(this.flags);
            this.from_peer.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
            stream.writeInt32(481674261);
            count = this.random_id.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.random_id.get(a)).longValue());
            }
            this.to_peer.serializeToStream(stream);
        }
    }

    public static class TL_messages_foundGifs extends TLObject {
        public static int constructor;
        public int next_offset;
        public ArrayList<FoundGif> results;

        public TL_messages_foundGifs() {
            this.results = new ArrayList();
        }

        static {
            constructor = 1158290442;
        }

        public static TL_messages_foundGifs TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_foundGifs result = new TL_messages_foundGifs();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_foundGifs", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.next_offset = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    FoundGif object = FoundGif.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.results.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.next_offset);
            stream.writeInt32(481674261);
            int count = this.results.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((FoundGif) this.results.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_getAllDrafts extends TLObject {
        public static int constructor;

        static {
            constructor = 1782549861;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messages_getAllStickers extends TLObject {
        public static int constructor;
        public int hash;

        static {
            constructor = 479598769;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_AllStickers.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.hash);
        }
    }

    public static class TL_messages_getBotCallbackAnswer extends TLObject {
        public static int constructor;
        public byte[] data;
        public int msg_id;
        public InputPeer peer;

        static {
            constructor = -1494659324;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_botCallbackAnswer.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.msg_id);
            stream.writeByteArray(this.data);
        }
    }

    public static class TL_messages_getChats extends TLObject {
        public static int constructor;
        public ArrayList<Integer> id;

        public TL_messages_getChats() {
            this.id = new ArrayList();
        }

        static {
            constructor = 1013621127;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_chats.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_messages_getDhConfig extends TLObject {
        public static int constructor;
        public int random_length;
        public int version;

        static {
            constructor = 651135312;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_DhConfig.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.version);
            stream.writeInt32(this.random_length);
        }
    }

    public static class TL_messages_getDialogs extends TLObject {
        public static int constructor;
        public int limit;
        public int offset_date;
        public int offset_id;
        public InputPeer offset_peer;

        static {
            constructor = 1799878989;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Dialogs.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset_date);
            stream.writeInt32(this.offset_id);
            this.offset_peer.serializeToStream(stream);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_messages_getDocumentByHash extends TLObject {
        public static int constructor;
        public String mime_type;
        public byte[] sha256;
        public int size;

        static {
            constructor = 864953444;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Document.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.sha256);
            stream.writeInt32(this.size);
            stream.writeString(this.mime_type);
        }
    }

    public static class TL_messages_getFullChat extends TLObject {
        public static int constructor;
        public int chat_id;

        static {
            constructor = 998448230;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_chatFull.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_messages_getHistory extends TLObject {
        public static int constructor;
        public int add_offset;
        public int limit;
        public int max_id;
        public int min_id;
        public int offset_date;
        public int offset_id;
        public InputPeer peer;

        static {
            constructor = -1347868602;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.offset_id);
            stream.writeInt32(this.offset_date);
            stream.writeInt32(this.add_offset);
            stream.writeInt32(this.limit);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.min_id);
        }
    }

    public static class TL_messages_getInlineBotResults extends TLObject {
        public static int constructor;
        public InputUser bot;
        public int flags;
        public InputGeoPoint geo_point;
        public String offset;
        public InputPeer peer;
        public String query;

        static {
            constructor = 1364105629;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_botResults.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            this.bot.serializeToStream(stream);
            this.peer.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.geo_point.serializeToStream(stream);
            }
            stream.writeString(this.query);
            stream.writeString(this.offset);
        }
    }

    public static class TL_messages_getMessageEditData extends TLObject {
        public static int constructor;
        public int id;
        public InputPeer peer;

        static {
            constructor = -39416522;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_messageEditData.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_messages_getMessages extends TLObject {
        public static int constructor;
        public ArrayList<Integer> id;

        public TL_messages_getMessages() {
            this.id = new ArrayList();
        }

        static {
            constructor = 1109588596;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_messages_getMessagesViews extends TLObject {
        public static int constructor;
        public ArrayList<Integer> id;
        public boolean increment;
        public InputPeer peer;

        public TL_messages_getMessagesViews() {
            this.id = new ArrayList();
        }

        static {
            constructor = -993483427;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                vector.objects.add(Integer.valueOf(stream.readInt32(exception)));
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
            stream.writeBool(this.increment);
        }
    }

    public static class TL_messages_getPeerDialogs extends TLObject {
        public static int constructor;
        public ArrayList<InputPeer> peers;

        public TL_messages_getPeerDialogs() {
            this.peers = new ArrayList();
        }

        static {
            constructor = 764901049;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_peerDialogs.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.peers.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputPeer) this.peers.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_getPeerSettings extends TLObject {
        public static int constructor;
        public InputPeer peer;

        static {
            constructor = 913498268;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_peerSettings.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_messages_getSavedGifs extends TLObject {
        public static int constructor;
        public int hash;

        static {
            constructor = -2084618926;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_SavedGifs.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.hash);
        }
    }

    public static class TL_messages_getStickerSet extends TLObject {
        public static int constructor;
        public InputStickerSet stickerset;

        static {
            constructor = 639215886;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_stickerSet.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.stickerset.serializeToStream(stream);
        }
    }

    public static class TL_messages_getStickers extends TLObject {
        public static int constructor;
        public String emoticon;
        public String hash;

        static {
            constructor = -1373446075;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Stickers.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.emoticon);
            stream.writeString(this.hash);
        }
    }

    public static class TL_messages_getWebPagePreview extends TLObject {
        public static int constructor;
        public String message;

        static {
            constructor = 623001124;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return MessageMedia.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.message);
        }
    }

    public static class TL_messages_hideReportSpam extends TLObject {
        public static int constructor;
        public InputPeer peer;

        static {
            constructor = -1460572005;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_messages_importChatInvite extends TLObject {
        public static int constructor;
        public String hash;

        static {
            constructor = 1817183516;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.hash);
        }
    }

    public static class TL_messages_installStickerSet extends TLObject {
        public static int constructor;
        public boolean disabled;
        public InputStickerSet stickerset;

        static {
            constructor = 2066793382;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.stickerset.serializeToStream(stream);
            stream.writeBool(this.disabled);
        }
    }

    public static class TL_messages_messageEditData extends TLObject {
        public static int constructor;
        public boolean caption;
        public int flags;

        static {
            constructor = 649453030;
        }

        public static TL_messages_messageEditData TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_messageEditData result = new TL_messages_messageEditData();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_messageEditData", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.caption = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.caption ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
        }
    }

    public static class TL_messages_messageEmpty extends TLObject {
        public static int constructor;

        static {
            constructor = 1062078024;
        }

        public static TL_messages_messageEmpty TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_messageEmpty result = new TL_messages_messageEmpty();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_messageEmpty", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messages_migrateChat extends TLObject {
        public static int constructor;
        public int chat_id;

        static {
            constructor = 363051235;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_messages_peerDialogs extends TLObject {
        public static int constructor;
        public ArrayList<Chat> chats;
        public ArrayList<TL_dialog> dialogs;
        public ArrayList<Message> messages;
        public TL_updates_state state;
        public ArrayList<User> users;

        public TL_messages_peerDialogs() {
            this.dialogs = new ArrayList();
            this.messages = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        static {
            constructor = 863093588;
        }

        public static TL_messages_peerDialogs TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_peerDialogs result = new TL_messages_peerDialogs();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_peerDialogs", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_dialog object = TL_dialog.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.dialogs.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Message object2 = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.messages.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                User object4 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.users.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                            this.state = TL_updates_state.TLdeserialize(stream, stream.readInt32(exception), exception);
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.dialogs.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_dialog) this.dialogs.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
            this.state.serializeToStream(stream);
        }
    }

    public static class TL_messages_readEncryptedHistory extends TLObject {
        public static int constructor;
        public int max_date;
        public TL_inputEncryptedChat peer;

        static {
            constructor = 2135648522;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.max_date);
        }
    }

    public static class TL_messages_readHistory extends TLObject {
        public static int constructor;
        public int max_id;
        public InputPeer peer;

        static {
            constructor = 238054714;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_affectedMessages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_messages_readMessageContents extends TLObject {
        public static int constructor;
        public ArrayList<Integer> id;

        public TL_messages_readMessageContents() {
            this.id = new ArrayList();
        }

        static {
            constructor = 916930423;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_affectedMessages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.id.get(a)).intValue());
            }
        }
    }

    public static class TL_messages_receivedMessages extends TLObject {
        public static int constructor;
        public int max_id;

        static {
            constructor = 94983360;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                TL_receivedNotifyMessage object = TL_receivedNotifyMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
                if (object == null) {
                    break;
                }
                vector.objects.add(object);
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_messages_receivedQueue extends TLObject {
        public static int constructor;
        public int max_qts;

        static {
            constructor = 1436924774;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                vector.objects.add(Long.valueOf(stream.readInt64(exception)));
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.max_qts);
        }
    }

    public static class TL_messages_reorderStickerSets extends TLObject {
        public static int constructor;
        public ArrayList<Long> order;

        public TL_messages_reorderStickerSets() {
            this.order = new ArrayList();
        }

        static {
            constructor = -1613775824;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.order.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.order.get(a)).longValue());
            }
        }
    }

    public static class TL_messages_reportSpam extends TLObject {
        public static int constructor;
        public InputPeer peer;

        static {
            constructor = -820669733;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_messages_requestEncryption extends TLObject {
        public static int constructor;
        public byte[] g_a;
        public int random_id;
        public InputUser user_id;

        static {
            constructor = -162681021;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return EncryptedChat.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.user_id.serializeToStream(stream);
            stream.writeInt32(this.random_id);
            stream.writeByteArray(this.g_a);
        }
    }

    public static class TL_messages_saveDraft extends TLObject {
        public static int constructor;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public String message;
        public boolean no_webpage;
        public InputPeer peer;
        public int reply_to_msg_id;

        public TL_messages_saveDraft() {
            this.entities = new ArrayList();
        }

        static {
            constructor = -1137057461;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.no_webpage) {
                i = this.flags | TLRPC.USER_FLAG_FIRST_NAME;
            } else {
                i = this.flags & -3;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            this.peer.serializeToStream(stream);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
        }
    }

    public static class TL_messages_saveGif extends TLObject {
        public static int constructor;
        public InputDocument id;
        public boolean unsave;

        static {
            constructor = 846868683;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
            stream.writeBool(this.unsave);
        }
    }

    public static class TL_messages_search extends TLObject {
        public static int constructor;
        public MessagesFilter filter;
        public int flags;
        public int limit;
        public int max_date;
        public int max_id;
        public int min_date;
        public int offset;
        public InputPeer peer;
        public String f37q;

        static {
            constructor = -732523960;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            stream.writeString(this.f37q);
            this.filter.serializeToStream(stream);
            stream.writeInt32(this.min_date);
            stream.writeInt32(this.max_date);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_messages_searchGifs extends TLObject {
        public static int constructor;
        public int offset;
        public String f38q;

        static {
            constructor = -1080395925;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_messages_foundGifs.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.f38q);
            stream.writeInt32(this.offset);
        }
    }

    public static class TL_messages_searchGlobal extends TLObject {
        public static int constructor;
        public int limit;
        public int offset_date;
        public int offset_id;
        public InputPeer offset_peer;
        public String f39q;

        static {
            constructor = -1640190800;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_Messages.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.f39q);
            stream.writeInt32(this.offset_date);
            this.offset_peer.serializeToStream(stream);
            stream.writeInt32(this.offset_id);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_messages_sendBroadcast extends TLObject {
        public static int constructor;
        public ArrayList<InputUser> contacts;
        public InputMedia media;
        public String message;
        public ArrayList<Long> random_id;

        public TL_messages_sendBroadcast() {
            this.contacts = new ArrayList();
            this.random_id = new ArrayList();
        }

        static {
            constructor = -1082919718;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.contacts.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.contacts.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.random_id.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.random_id.get(a)).longValue());
            }
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
        }
    }

    public static class TL_messages_sendEncrypted extends TLObject {
        public static int constructor;
        public NativeByteBuffer data;
        public TL_inputEncryptedChat peer;
        public long random_id;

        static {
            constructor = -1451792525;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_SentEncryptedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt64(this.random_id);
            stream.writeByteBuffer(this.data);
        }

        public void freeResources() {
            if (this.data != null) {
                this.data.reuse();
                this.data = null;
            }
        }
    }

    public static class TL_messages_sendEncryptedFile extends TLObject {
        public static int constructor;
        public NativeByteBuffer data;
        public InputEncryptedFile file;
        public TL_inputEncryptedChat peer;
        public long random_id;

        static {
            constructor = -1701831834;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_SentEncryptedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt64(this.random_id);
            stream.writeByteBuffer(this.data);
            this.file.serializeToStream(stream);
        }

        public void freeResources() {
            if (this.data != null) {
                this.data.reuse();
                this.data = null;
            }
        }
    }

    public static class TL_messages_sendEncryptedService extends TLObject {
        public static int constructor;
        public NativeByteBuffer data;
        public TL_inputEncryptedChat peer;
        public long random_id;

        static {
            constructor = 852769188;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return messages_SentEncryptedMessage.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt64(this.random_id);
            stream.writeByteBuffer(this.data);
        }

        public void freeResources() {
            if (this.data != null) {
                this.data.reuse();
                this.data = null;
            }
        }
    }

    public static class TL_messages_sendInlineBotResult extends TLObject {
        public static int constructor;
        public boolean background;
        public boolean clear_draft;
        public int flags;
        public String id;
        public InputPeer peer;
        public long query_id;
        public long random_id;
        public int reply_to_msg_id;
        public boolean silent;

        static {
            constructor = -1318189314;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.background ? this.flags | TLRPC.USER_FLAG_STATUS : this.flags & -65;
            this.flags = this.clear_draft ? this.flags | TLRPC.USER_FLAG_UNUSED : this.flags & -129;
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt64(this.random_id);
            stream.writeInt64(this.query_id);
            stream.writeString(this.id);
        }
    }

    public static class TL_messages_sendMedia extends TLObject {
        public static int constructor;
        public boolean background;
        public boolean clear_draft;
        public int flags;
        public InputMedia media;
        public InputPeer peer;
        public long random_id;
        public ReplyMarkup reply_markup;
        public int reply_to_msg_id;
        public boolean silent;

        static {
            constructor = -923703407;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.background ? this.flags | TLRPC.USER_FLAG_STATUS : this.flags & -65;
            this.flags = this.clear_draft ? this.flags | TLRPC.USER_FLAG_UNUSED : this.flags & -129;
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            this.media.serializeToStream(stream);
            stream.writeInt64(this.random_id);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_sendMessage extends TLObject {
        public static int constructor;
        public boolean background;
        public boolean clear_draft;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public String message;
        public boolean no_webpage;
        public InputPeer peer;
        public long random_id;
        public ReplyMarkup reply_markup;
        public int reply_to_msg_id;
        public boolean silent;

        public TL_messages_sendMessage() {
            this.entities = new ArrayList();
        }

        static {
            constructor = -91733382;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.no_webpage ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.background ? this.flags | TLRPC.USER_FLAG_STATUS : this.flags & -65;
            if (this.clear_draft) {
                i = this.flags | TLRPC.USER_FLAG_UNUSED;
            } else {
                i = this.flags & -129;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            this.peer.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeString(this.message);
            stream.writeInt64(this.random_id);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
        }
    }

    public static class TL_messages_setBotCallbackAnswer extends TLObject {
        public static int constructor;
        public boolean alert;
        public int flags;
        public String message;
        public long query_id;

        static {
            constructor = 1209817370;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.alert ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            stream.writeInt32(this.flags);
            stream.writeInt64(this.query_id);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.message);
            }
        }
    }

    public static class TL_messages_setEncryptedTyping extends TLObject {
        public static int constructor;
        public TL_inputEncryptedChat peer;
        public boolean typing;

        static {
            constructor = 2031374829;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeBool(this.typing);
        }
    }

    public static class TL_messages_setTyping extends TLObject {
        public static int constructor;
        public SendMessageAction action;
        public InputPeer peer;

        static {
            constructor = -1551737264;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_messages_startBot extends TLObject {
        public static int constructor;
        public InputUser bot;
        public InputPeer peer;
        public long random_id;
        public String start_param;

        static {
            constructor = -421563528;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.bot.serializeToStream(stream);
            this.peer.serializeToStream(stream);
            stream.writeInt64(this.random_id);
            stream.writeString(this.start_param);
        }
    }

    public static class TL_messages_stickerSet extends TLObject {
        public static int constructor;
        public ArrayList<Document> documents;
        public ArrayList<TL_stickerPack> packs;
        public StickerSet set;

        public TL_messages_stickerSet() {
            this.packs = new ArrayList();
            this.documents = new ArrayList();
        }

        static {
            constructor = -1240849242;
        }

        public static TL_messages_stickerSet TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_messages_stickerSet result = new TL_messages_stickerSet();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_stickerSet", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.set = StickerSet.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_stickerPack object = TL_stickerPack.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.packs.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Document object2 = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.documents.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            this.set.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.packs.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_stickerPack) this.packs.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.documents.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Document) this.documents.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_toggleChatAdmins extends TLObject {
        public static int constructor;
        public int chat_id;
        public boolean enabled;

        static {
            constructor = -326379039;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Updates.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeBool(this.enabled);
        }
    }

    public static class TL_messages_uninstallStickerSet extends TLObject {
        public static int constructor;
        public InputStickerSet stickerset;

        static {
            constructor = -110209570;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.stickerset.serializeToStream(stream);
        }
    }

    public static class TL_nearestDc extends TLObject {
        public static int constructor;
        public String country;
        public int nearest_dc;
        public int this_dc;

        static {
            constructor = -1910892683;
        }

        public static TL_nearestDc TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_nearestDc result = new TL_nearestDc();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_nearestDc", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.country = stream.readString(exception);
            this.this_dc = stream.readInt32(exception);
            this.nearest_dc = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.country);
            stream.writeInt32(this.this_dc);
            stream.writeInt32(this.nearest_dc);
        }
    }

    public static class TL_null extends TLObject {
        public static int constructor;

        static {
            constructor = 1450380236;
        }

        public static TL_null TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_null result = new TL_null();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_null", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_peerSettings extends TLObject {
        public static int constructor;
        public int flags;
        public boolean report_spam;

        static {
            constructor = -2122045747;
        }

        public static TL_peerSettings TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_peerSettings result = new TL_peerSettings();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_peerSettings", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.report_spam = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.report_spam ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
        }
    }

    public static class TL_photos_deletePhotos extends TLObject {
        public static int constructor;
        public ArrayList<InputPhoto> id;

        public TL_photos_deletePhotos() {
            this.id = new ArrayList();
        }

        static {
            constructor = -2016444625;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                vector.objects.add(Long.valueOf(stream.readInt64(exception)));
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputPhoto) this.id.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_photos_getUserPhotos extends TLObject {
        public static int constructor;
        public int limit;
        public long max_id;
        public int offset;
        public InputUser user_id;

        static {
            constructor = -1848823128;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return photos_Photos.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.user_id.serializeToStream(stream);
            stream.writeInt32(this.offset);
            stream.writeInt64(this.max_id);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_photos_photo extends TLObject {
        public static int constructor;
        public Photo photo;
        public ArrayList<User> users;

        public TL_photos_photo() {
            this.users = new ArrayList();
        }

        static {
            constructor = 539045032;
        }

        public static TL_photos_photo TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_photos_photo result = new TL_photos_photo();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_photos_photo", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    User object = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.users.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.photo.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_photos_updateProfilePhoto extends TLObject {
        public static int constructor;
        public InputPhotoCrop crop;
        public InputPhoto id;

        static {
            constructor = -285902432;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return UserProfilePhoto.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
            this.crop.serializeToStream(stream);
        }
    }

    public static class TL_photos_uploadProfilePhoto extends TLObject {
        public static int constructor;
        public String caption;
        public InputPhotoCrop crop;
        public InputFile file;
        public InputGeoPoint geo_point;

        static {
            constructor = -720397176;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_photos_photo.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.file.serializeToStream(stream);
            stream.writeString(this.caption);
            this.geo_point.serializeToStream(stream);
            this.crop.serializeToStream(stream);
        }
    }

    public static class TL_receivedNotifyMessage extends TLObject {
        public static int constructor;
        public int flags;
        public int id;

        static {
            constructor = -1551583367;
        }

        public static TL_receivedNotifyMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_receivedNotifyMessage result = new TL_receivedNotifyMessage();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_receivedNotifyMessage", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.flags = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt32(this.flags);
        }
    }

    public static class TL_stickerPack extends TLObject {
        public static int constructor;
        public ArrayList<Long> documents;
        public String emoticon;

        public TL_stickerPack() {
            this.documents = new ArrayList();
        }

        static {
            constructor = 313694676;
        }

        public static TL_stickerPack TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_stickerPack result = new TL_stickerPack();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_stickerPack", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.emoticon = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.documents.add(Long.valueOf(stream.readInt64(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.emoticon);
            stream.writeInt32(481674261);
            int count = this.documents.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.documents.get(a)).longValue());
            }
        }
    }

    public static class TL_topPeer extends TLObject {
        public static int constructor;
        public Peer peer;
        public double rating;

        static {
            constructor = -305282981;
        }

        public static TL_topPeer TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_topPeer result = new TL_topPeer();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_topPeer", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.rating = stream.readDouble(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeDouble(this.rating);
        }
    }

    public static class TL_topPeerCategoryPeers extends TLObject {
        public static int constructor;
        public TopPeerCategory category;
        public int count;
        public ArrayList<TL_topPeer> peers;

        public TL_topPeerCategoryPeers() {
            this.peers = new ArrayList();
        }

        static {
            constructor = -75283823;
        }

        public static TL_topPeerCategoryPeers TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_topPeerCategoryPeers result = new TL_topPeerCategoryPeers();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_topPeerCategoryPeers", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.category = TopPeerCategory.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_topPeer object = TL_topPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.peers.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.category.serializeToStream(stream);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.peers.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_topPeer) this.peers.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_updates_getChannelDifference extends TLObject {
        public static int constructor;
        public InputChannel channel;
        public ChannelMessagesFilter filter;
        public int limit;
        public int pts;

        static {
            constructor = -1154295872;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return updates_ChannelDifference.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.channel.serializeToStream(stream);
            this.filter.serializeToStream(stream);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_updates_getDifference extends TLObject {
        public static int constructor;
        public int date;
        public int pts;
        public int qts;

        static {
            constructor = 168039573;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return updates_Difference.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.date);
            stream.writeInt32(this.qts);
        }
    }

    public static class TL_updates_getState extends TLObject {
        public static int constructor;

        static {
            constructor = -304838614;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_updates_state.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_updates_state extends TLObject {
        public static int constructor;
        public int date;
        public int pts;
        public int qts;
        public int seq;
        public int unread_count;

        static {
            constructor = -1519637954;
        }

        public static TL_updates_state TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_updates_state result = new TL_updates_state();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_updates_state", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.pts = stream.readInt32(exception);
            this.qts = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.seq = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.qts);
            stream.writeInt32(this.date);
            stream.writeInt32(this.seq);
            stream.writeInt32(this.unread_count);
        }
    }

    public static class TL_upload_file extends TLObject {
        public static int constructor;
        public NativeByteBuffer bytes;
        public int mtime;
        public storage_FileType type;

        static {
            constructor = 157948117;
        }

        public static TL_upload_file TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_upload_file result = new TL_upload_file();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_upload_file", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.type = storage_FileType.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.mtime = stream.readInt32(exception);
            this.bytes = stream.readByteBuffer(exception);
        }

        public void freeResources() {
            if (!this.disableFree && this.bytes != null) {
                this.bytes.reuse();
                this.bytes = null;
            }
        }
    }

    public static class TL_upload_getFile extends TLObject {
        public static int constructor;
        public int limit;
        public InputFileLocation location;
        public int offset;

        static {
            constructor = -475607115;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_upload_file.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.location.serializeToStream(stream);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.limit);
        }
    }

    public static class TL_upload_saveBigFilePart extends TLObject {
        public static int constructor;
        public NativeByteBuffer bytes;
        public long file_id;
        public int file_part;
        public int file_total_parts;

        static {
            constructor = -562337987;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.file_id);
            stream.writeInt32(this.file_part);
            stream.writeInt32(this.file_total_parts);
            stream.writeByteBuffer(this.bytes);
        }

        public void freeResources() {
            if (!this.disableFree && this.bytes != null) {
                this.bytes.reuse();
                this.bytes = null;
            }
        }
    }

    public static class TL_upload_saveFilePart extends TLObject {
        public static int constructor;
        public NativeByteBuffer bytes;
        public long file_id;
        public int file_part;

        static {
            constructor = -1291540959;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return Bool.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.file_id);
            stream.writeInt32(this.file_part);
            stream.writeByteBuffer(this.bytes);
        }

        public void freeResources() {
            if (!this.disableFree && this.bytes != null) {
                this.bytes.reuse();
                this.bytes = null;
            }
        }
    }

    public static class TL_userFull extends TLObject {
        public static int constructor;
        public String about;
        public boolean blocked;
        public BotInfo bot_info;
        public int flags;
        public TL_contacts_link link;
        public PeerNotifySettings notify_settings;
        public Photo profile_photo;
        public User user;

        static {
            constructor = 1496513539;
        }

        public static TL_userFull TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            if (constructor == constructor) {
                TL_userFull result = new TL_userFull();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TL_userFull", objArr));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.blocked = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            this.user = User.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.about = stream.readString(exception);
            }
            this.link = TL_contacts_link.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.profile_photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.bot_info = BotInfo.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.blocked ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            this.user.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.about);
            }
            this.link.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.profile_photo.serializeToStream(stream);
            }
            this.notify_settings.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.bot_info.serializeToStream(stream);
            }
        }
    }

    public static class TL_users_getFullUser extends TLObject {
        public static int constructor;
        public InputUser id;

        static {
            constructor = -902781519;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            return TL_userFull.TLdeserialize(stream, constructor, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
        }
    }

    public static class TL_users_getUsers extends TLObject {
        public static int constructor;
        public ArrayList<InputUser> id;

        public TL_users_getUsers() {
            this.id = new ArrayList();
        }

        static {
            constructor = 227648840;
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
            Vector vector = new Vector();
            int size = stream.readInt32(exception);
            for (int a = 0; a < size; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                User object = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                if (object == null) {
                    break;
                }
                vector.objects.add(object);
            }
            return vector;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.id.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.id.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TopPeerCategory extends TLObject {
        public static TopPeerCategory TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            TopPeerCategory result = null;
            switch (constructor) {
                case -1419371685:
                    result = new TL_topPeerCategoryBotsPM();
                    break;
                case -1122524854:
                    result = new TL_topPeerCategoryGroups();
                    break;
                case 104314861:
                    result = new TL_topPeerCategoryCorrespondents();
                    break;
                case 344356834:
                    result = new TL_topPeerCategoryBotsInline();
                    break;
                case 371037736:
                    result = new TL_topPeerCategoryChannels();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in TopPeerCategory", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Update extends TLObject {
        public SendMessageAction action;
        public long auth_key_id;
        public boolean blocked;
        public int channel_id;
        public EncryptedChat chat;
        public int chat_id;
        public byte[] data;
        public int date;
        public ArrayList<TL_dcOption> dc_options;
        public String device;
        public DraftMessage draft;
        public boolean enabled;
        public String first_name;
        public int flags;
        public ContactLink foreign_link;
        public GeoPoint geo;
        public int inviter_id;
        public boolean is_admin;
        public PrivacyKey key;
        public String last_name;
        public String location;
        public int max_date;
        public int max_id;
        public MessageMedia media;
        public ArrayList<Integer> messages;
        public ContactLink my_link;
        public PeerNotifySettings notify_settings;
        public String offset;
        public ArrayList<Long> order;
        public ChatParticipants participants;
        public String phone;
        public UserProfilePhoto photo;
        public boolean popup;
        public boolean previous;
        public int pts;
        public int pts_count;
        public int qts;
        public String query;
        public long query_id;
        public long random_id;
        public ArrayList<PrivacyRule> rules;
        public UserStatus status;
        public TL_messages_stickerSet stickerset;
        public String type;
        public int user_id;
        public String username;
        public int version;
        public int views;
        public WebPage webpage;

        public Update() {
            this.dc_options = new ArrayList();
            this.rules = new ArrayList();
            this.messages = new ArrayList();
            this.order = new ArrayList();
        }

        public static Update TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Update result = null;
            switch (constructor) {
                case -2131957734:
                    result = new TL_updateUserBlocked();
                    break;
                case -1906403213:
                    result = new TL_updateDcOptions();
                    break;
                case -1895411046:
                    result = new TL_updateNewAuthorization();
                    break;
                case -1821035490:
                    result = new TL_updateSavedGifs();
                    break;
                case -1791935732:
                    result = new TL_updateUserPhoto();
                    break;
                case -1738988427:
                    result = new TL_updateChannelPinnedMessage();
                    break;
                case -1734268085:
                    result = new TL_updateChannelMessageViews();
                    break;
                case -1721631396:
                    result = new TL_updateReadHistoryInbox();
                    break;
                case -1704596961:
                    result = new TL_updateChatUserTyping();
                    break;
                case -1657903163:
                    result = new TL_updateContactLink();
                    break;
                case -1576161051:
                    result = new TL_updateDeleteMessages();
                    break;
                case -1500747636:
                    result = new TL_updateBotCallbackQuery();
                    break;
                case -1489818765:
                    result = new TL_updateUserName();
                    break;
                case -1264392051:
                    result = new TL_updateEncryption();
                    break;
                case -1232070311:
                    result = new TL_updateChatParticipantAdmin();
                    break;
                case -1227598250:
                    result = new TL_updateChannel();
                    break;
                case -1094555409:
                    result = new TL_updateNotifySettings();
                    break;
                case -1015733815:
                    result = new TL_updateDeleteChannelMessages();
                    break;
                case -469536605:
                    result = new TL_updateEditMessage();
                    break;
                case -364179876:
                    result = new TL_updateChatParticipantAdd();
                    break;
                case -352032773:
                    result = new TL_updateChannelTooLong();
                    break;
                case -299124375:
                    result = new TL_updateDraftMessage();
                    break;
                case -298113238:
                    result = new TL_updatePrivacy();
                    break;
                case -253774767:
                    result = new TL_updateStickerSetsOrder();
                    break;
                case 125178264:
                    result = new TL_updateChatParticipants();
                    break;
                case 239663460:
                    result = new TL_updateBotInlineSend();
                    break;
                case 314130811:
                    result = new TL_updateUserPhone();
                    break;
                case 314359194:
                    result = new TL_updateNewEncryptedMessage();
                    break;
                case 386986326:
                    result = new TL_updateEncryptedChatTyping();
                    break;
                case 457133559:
                    result = new TL_updateEditChannelMessage();
                    break;
                case 469489699:
                    result = new TL_updateUserStatus();
                    break;
                case 522914557:
                    result = new TL_updateNewMessage();
                    break;
                case 628472761:
                    result = new TL_updateContactRegistered();
                    break;
                case 634833351:
                    result = new TL_updateReadChannelOutbox();
                    break;
                case 750622127:
                    result = new TL_updateInlineBotCallbackQuery();
                    break;
                case 791617983:
                    result = new TL_updateReadHistoryOutbox();
                    break;
                case 942527460:
                    result = new TL_updateServiceNotification();
                    break;
                case 956179895:
                    result = new TL_updateEncryptedMessagesRead();
                    break;
                case 1108669311:
                    result = new TL_updateReadChannelInbox();
                    break;
                case 1135492588:
                    result = new TL_updateStickerSets();
                    break;
                case 1318109142:
                    result = new TL_updateMessageID();
                    break;
                case 1417832080:
                    result = new TL_updateBotInlineQuery();
                    break;
                case 1516823543:
                    result = new TL_updateNewGeoChatMessage();
                    break;
                case 1548249383:
                    result = new TL_updateUserTyping();
                    break;
                case 1656358105:
                    result = new TL_updateNewChannelMessage();
                    break;
                case 1753886890:
                    result = new TL_updateNewStickerSet();
                    break;
                case 1757493555:
                    result = new TL_updateReadMessagesContents();
                    break;
                case 1851755554:
                    result = new TL_updateChatParticipantDelete();
                    break;
                case 1855224129:
                    result = new TL_updateChatAdmins();
                    break;
                case 2139689491:
                    result = new TL_updateWebPage();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Update", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Updates extends TLObject {
        public int chat_id;
        public ArrayList<Chat> chats;
        public int date;
        public ArrayList<MessageEntity> entities;
        public int flags;
        public int from_id;
        public TL_messageFwdHeader fwd_from;
        public int id;
        public MessageMedia media;
        public boolean media_unread;
        public boolean mentioned;
        public String message;
        public boolean out;
        public int pts;
        public int pts_count;
        public int reply_to_msg_id;
        public int seq;
        public int seq_start;
        public boolean silent;
        public Update update;
        public ArrayList<Update> updates;
        public int user_id;
        public ArrayList<User> users;
        public int via_bot_id;

        public Updates() {
            this.updates = new ArrayList();
            this.users = new ArrayList();
            this.chats = new ArrayList();
            this.entities = new ArrayList();
        }

        public static Updates TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Updates result = null;
            switch (constructor) {
                case -1857044719:
                    result = new TL_updateShortMessage();
                    break;
                case -484987010:
                    result = new TL_updatesTooLong();
                    break;
                case 301019932:
                    result = new TL_updateShortSentMessage();
                    break;
                case 377562760:
                    result = new TL_updateShortChatMessage();
                    break;
                case 1918567619:
                    result = new TL_updatesCombined();
                    break;
                case 1957577280:
                    result = new TL_updates();
                    break;
                case 2027216577:
                    result = new TL_updateShort();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Updates", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class User extends TLObject {
        public long access_hash;
        public boolean bot;
        public boolean bot_chat_history;
        public int bot_info_version;
        public boolean bot_inline_geo;
        public String bot_inline_placeholder;
        public boolean bot_nochats;
        public boolean contact;
        public boolean deleted;
        public boolean explicit_content;
        public String first_name;
        public int flags;
        public int id;
        public boolean inactive;
        public String last_name;
        public boolean min;
        public boolean mutual_contact;
        public String phone;
        public UserProfilePhoto photo;
        public boolean restricted;
        public String restriction_reason;
        public boolean self;
        public UserStatus status;
        public String username;
        public boolean verified;

        public static User TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            User result = null;
            switch (constructor) {
                case -1298475060:
                    result = new TL_userDeleted_old();
                    break;
                case -894214632:
                    result = new TL_userContact_old2();
                    break;
                case -787638374:
                    result = new TL_user();
                    break;
                case -704549510:
                    result = new TL_userDeleted_old2();
                    break;
                case -640891665:
                    result = new TL_userRequest_old2();
                    break;
                case -218397927:
                    result = new TL_userContact_old();
                    break;
                case 123533224:
                    result = new TL_userForeign_old2();
                    break;
                case 476112392:
                    result = new TL_userSelf_old3();
                    break;
                case 537022650:
                    result = new TL_userEmpty();
                    break;
                case 585404530:
                    result = new TL_user_old();
                    break;
                case 585682608:
                    result = new TL_userRequest_old();
                    break;
                case 1377093789:
                    result = new TL_userForeign_old();
                    break;
                case 1879553105:
                    result = new TL_userSelf_old2();
                    break;
                case 1912944108:
                    result = new TL_userSelf_old();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in User", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class UserProfilePhoto extends TLObject {
        public FileLocation photo_big;
        public long photo_id;
        public FileLocation photo_small;

        public static UserProfilePhoto TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            UserProfilePhoto result = null;
            switch (constructor) {
                case -1727196013:
                    result = new TL_userProfilePhoto_old();
                    break;
                case -715532088:
                    result = new TL_userProfilePhoto();
                    break;
                case 1326562017:
                    result = new TL_userProfilePhotoEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in UserProfilePhoto", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class UserStatus extends TLObject {
        public int expires;

        public static UserStatus TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            UserStatus result = null;
            switch (constructor) {
                case -496024847:
                    result = new TL_userStatusRecently();
                    break;
                case -306628279:
                    result = new TL_userStatusOnline();
                    break;
                case 9203775:
                    result = new TL_userStatusOffline();
                    break;
                case 129960444:
                    result = new TL_userStatusLastWeek();
                    break;
                case 164646985:
                    result = new TL_userStatusEmpty();
                    break;
                case 2011940674:
                    result = new TL_userStatusLastMonth();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in UserStatus", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class Vector extends TLObject {
        public static int constructor;
        public ArrayList<Object> objects;

        public Vector() {
            this.objects = new ArrayList();
        }

        static {
            constructor = 481674261;
        }
    }

    public static class Video extends TLObject {
        public long access_hash;
        public String caption;
        public int date;
        public int dc_id;
        public int duration;
        public int f40h;
        public long id;
        public byte[] iv;
        public byte[] key;
        public String mime_type;
        public int size;
        public PhotoSize thumb;
        public int user_id;
        public int f41w;

        public static Video TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            Video result = null;
            switch (constructor) {
                case -1056548696:
                    result = new TL_videoEmpty_layer45();
                    break;
                case -291550643:
                    result = new TL_video_old3();
                    break;
                case -148338733:
                    result = new TL_video_layer45();
                    break;
                case 948937617:
                    result = new TL_video_old2();
                    break;
                case 1431655763:
                    result = new TL_videoEncrypted();
                    break;
                case 1510253727:
                    result = new TL_video_old();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in Video", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class WallPaper extends TLObject {
        public int bg_color;
        public int color;
        public int id;
        public ArrayList<PhotoSize> sizes;
        public String title;

        public WallPaper() {
            this.sizes = new ArrayList();
        }

        public static WallPaper TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            WallPaper result = null;
            switch (constructor) {
                case -860866985:
                    result = new TL_wallPaper();
                    break;
                case 1662091044:
                    result = new TL_wallPaperSolid();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in WallPaper", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class WebPage extends TLObject {
        public String author;
        public int date;
        public String description;
        public String display_url;
        public Document document;
        public int duration;
        public int embed_height;
        public String embed_type;
        public String embed_url;
        public int embed_width;
        public int flags;
        public long id;
        public Photo photo;
        public String site_name;
        public String title;
        public String type;
        public String url;

        public static WebPage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            WebPage result = null;
            switch (constructor) {
                case -1558273867:
                    result = new TL_webPage_old();
                    break;
                case -981018084:
                    result = new TL_webPagePending();
                    break;
                case -897446185:
                    result = new TL_webPage();
                    break;
                case -736472729:
                    result = new TL_webPageUrlPending();
                    break;
                case -350980120:
                    result = new TL_webPageEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in WebPage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class account_Password extends TLObject {
        public byte[] current_salt;
        public String email_unconfirmed_pattern;
        public boolean has_recovery;
        public String hint;
        public byte[] new_salt;

        public static account_Password TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            account_Password result = null;
            switch (constructor) {
                case -1764049896:
                    result = new TL_account_noPassword();
                    break;
                case 2081952796:
                    result = new TL_account_password();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in account_Password", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class auth_CodeType extends TLObject {
        public static auth_CodeType TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            auth_CodeType result = null;
            switch (constructor) {
                case 577556219:
                    result = new TL_auth_codeTypeFlashCall();
                    break;
                case 1923290508:
                    result = new TL_auth_codeTypeSms();
                    break;
                case 1948046307:
                    result = new TL_auth_codeTypeCall();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in auth_CodeType", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class auth_SentCodeType extends TLObject {
        public int length;
        public String pattern;

        public static auth_SentCodeType TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            auth_SentCodeType result = null;
            switch (constructor) {
                case -1425815847:
                    result = new TL_auth_sentCodeTypeFlashCall();
                    break;
                case -1073693790:
                    result = new TL_auth_sentCodeTypeSms();
                    break;
                case 1035688326:
                    result = new TL_auth_sentCodeTypeApp();
                    break;
                case 1398007207:
                    result = new TL_auth_sentCodeTypeCall();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in auth_SentCodeType", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class contacts_Blocked extends TLObject {
        public ArrayList<TL_contactBlocked> blocked;
        public int count;
        public ArrayList<User> users;

        public contacts_Blocked() {
            this.blocked = new ArrayList();
            this.users = new ArrayList();
        }

        public static contacts_Blocked TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            contacts_Blocked result = null;
            switch (constructor) {
                case -1878523231:
                    result = new TL_contacts_blockedSlice();
                    break;
                case 471043349:
                    result = new TL_contacts_blocked();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in contacts_Blocked", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class contacts_Contacts extends TLObject {
        public ArrayList<TL_contact> contacts;
        public ArrayList<User> users;

        public contacts_Contacts() {
            this.contacts = new ArrayList();
            this.users = new ArrayList();
        }

        public static contacts_Contacts TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            contacts_Contacts result = null;
            switch (constructor) {
                case -1219778094:
                    result = new TL_contacts_contactsNotModified();
                    break;
                case 1871416498:
                    result = new TL_contacts_contacts();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in contacts_Contacts", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class contacts_TopPeers extends TLObject {
        public ArrayList<TL_topPeerCategoryPeers> categories;
        public ArrayList<Chat> chats;
        public ArrayList<User> users;

        public contacts_TopPeers() {
            this.categories = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        public static contacts_TopPeers TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            contacts_TopPeers result = null;
            switch (constructor) {
                case -567906571:
                    result = new TL_contacts_topPeersNotModified();
                    break;
                case 1891070632:
                    result = new TL_contacts_topPeers();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in contacts_TopPeers", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class geochats_Messages extends TLObject {
        public ArrayList<Chat> chats;
        public int count;
        public ArrayList<GeoChatMessage> messages;
        public ArrayList<User> users;

        public geochats_Messages() {
            this.messages = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        public static geochats_Messages TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            geochats_Messages result = null;
            switch (constructor) {
                case -1135057944:
                    result = new TL_geochats_messagesSlice();
                    break;
                case -783127119:
                    result = new TL_geochats_messages();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in geochats_Messages", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class help_AppChangelog extends TLObject {
        public String text;

        public static help_AppChangelog TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            help_AppChangelog result = null;
            switch (constructor) {
                case -1350696044:
                    result = new TL_help_appChangelogEmpty();
                    break;
                case 1181279933:
                    result = new TL_help_appChangelog();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in help_AppChangelog", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class help_AppUpdate extends TLObject {
        public boolean critical;
        public int id;
        public String text;
        public String url;

        public static help_AppUpdate TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            help_AppUpdate result = null;
            switch (constructor) {
                case -1987579119:
                    result = new TL_help_appUpdate();
                    break;
                case -1000708810:
                    result = new TL_help_noAppUpdate();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in help_AppUpdate", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_AllStickers extends TLObject {
        public ArrayList<Document> documents;
        public String hash;
        public ArrayList<TL_stickerPack> packs;
        public ArrayList<StickerSet> sets;

        public messages_AllStickers() {
            this.sets = new ArrayList();
            this.packs = new ArrayList();
            this.documents = new ArrayList();
        }

        public static messages_AllStickers TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_AllStickers result = null;
            switch (constructor) {
                case -395967805:
                    result = new TL_messages_allStickersNotModified();
                    break;
                case -302170017:
                    result = new TL_messages_allStickers();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_AllStickers", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_DhConfig extends TLObject {
        public int f42g;
        public byte[] f43p;
        public byte[] random;
        public int version;

        public static messages_DhConfig TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_DhConfig result = null;
            switch (constructor) {
                case -1058912715:
                    result = new TL_messages_dhConfigNotModified();
                    break;
                case 740433629:
                    result = new TL_messages_dhConfig();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_DhConfig", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_Dialogs extends TLObject {
        public ArrayList<Chat> chats;
        public int count;
        public ArrayList<TL_dialog> dialogs;
        public ArrayList<Message> messages;
        public ArrayList<User> users;

        public messages_Dialogs() {
            this.dialogs = new ArrayList();
            this.messages = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        public static messages_Dialogs TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_Dialogs result = null;
            switch (constructor) {
                case 364538944:
                    result = new TL_messages_dialogs();
                    break;
                case 1910543603:
                    result = new TL_messages_dialogsSlice();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_Dialogs", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_Messages extends TLObject {
        public ArrayList<Chat> chats;
        public int count;
        public int flags;
        public ArrayList<Message> messages;
        public int pts;
        public ArrayList<User> users;

        public messages_Messages() {
            this.messages = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        public static messages_Messages TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_Messages result = null;
            switch (constructor) {
                case -1938715001:
                    result = new TL_messages_messages();
                    break;
                case -1725551049:
                    result = new TL_messages_channelMessages();
                    break;
                case 189033187:
                    result = new TL_messages_messagesSlice();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_Messages", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_SavedGifs extends TLObject {
        public ArrayList<Document> gifs;
        public int hash;

        public messages_SavedGifs() {
            this.gifs = new ArrayList();
        }

        public static messages_SavedGifs TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_SavedGifs result = null;
            switch (constructor) {
                case -402498398:
                    result = new TL_messages_savedGifsNotModified();
                    break;
                case 772213157:
                    result = new TL_messages_savedGifs();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_SavedGifs", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_SentEncryptedMessage extends TLObject {
        public int date;
        public EncryptedFile file;

        public static messages_SentEncryptedMessage TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_SentEncryptedMessage result = null;
            switch (constructor) {
                case -1802240206:
                    result = new TL_messages_sentEncryptedFile();
                    break;
                case 1443858741:
                    result = new TL_messages_sentEncryptedMessage();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_SentEncryptedMessage", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class messages_Stickers extends TLObject {
        public String hash;
        public ArrayList<Document> stickers;

        public messages_Stickers() {
            this.stickers = new ArrayList();
        }

        public static messages_Stickers TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            messages_Stickers result = null;
            switch (constructor) {
                case -1970352846:
                    result = new TL_messages_stickers();
                    break;
                case -244016606:
                    result = new TL_messages_stickersNotModified();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in messages_Stickers", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class photos_Photos extends TLObject {
        public int count;
        public ArrayList<Photo> photos;
        public ArrayList<User> users;

        public photos_Photos() {
            this.photos = new ArrayList();
            this.users = new ArrayList();
        }

        public static photos_Photos TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            photos_Photos result = null;
            switch (constructor) {
                case -1916114267:
                    result = new TL_photos_photos();
                    break;
                case 352657236:
                    result = new TL_photos_photosSlice();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in photos_Photos", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class storage_FileType extends TLObject {
        public static storage_FileType TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            storage_FileType result = null;
            switch (constructor) {
                case -1432995067:
                    result = new TL_storage_fileUnknown();
                    break;
                case -1373745011:
                    result = new TL_storage_filePdf();
                    break;
                case -1278304028:
                    result = new TL_storage_fileMp4();
                    break;
                case -891180321:
                    result = new TL_storage_fileGif();
                    break;
                case 8322574:
                    result = new TL_storage_fileJpeg();
                    break;
                case 172975040:
                    result = new TL_storage_filePng();
                    break;
                case 276907596:
                    result = new TL_storage_fileWebp();
                    break;
                case 1086091090:
                    result = new TL_storage_filePartial();
                    break;
                case 1258941372:
                    result = new TL_storage_fileMov();
                    break;
                case 1384777335:
                    result = new TL_storage_fileMp3();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in storage_FileType", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class updates_ChannelDifference extends TLObject {
        public ArrayList<Chat> chats;
        public int flags;
        public boolean isFinal;
        public ArrayList<Message> messages;
        public ArrayList<Message> new_messages;
        public ArrayList<Update> other_updates;
        public int pts;
        public int read_inbox_max_id;
        public int read_outbox_max_id;
        public int timeout;
        public int top_message;
        public int unread_count;
        public ArrayList<User> users;

        public updates_ChannelDifference() {
            this.new_messages = new ArrayList();
            this.other_updates = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
            this.messages = new ArrayList();
        }

        public static updates_ChannelDifference TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            updates_ChannelDifference result = null;
            switch (constructor) {
                case 543450958:
                    result = new TL_updates_channelDifference();
                    break;
                case 1041346555:
                    result = new TL_updates_channelDifferenceEmpty();
                    break;
                case 1091431943:
                    result = new TL_updates_channelDifferenceTooLong();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in updates_ChannelDifference", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class updates_Difference extends TLObject {
        public ArrayList<Chat> chats;
        public int date;
        public TL_updates_state intermediate_state;
        public ArrayList<EncryptedMessage> new_encrypted_messages;
        public ArrayList<Message> new_messages;
        public ArrayList<Update> other_updates;
        public int seq;
        public TL_updates_state state;
        public ArrayList<User> users;

        public updates_Difference() {
            this.new_messages = new ArrayList();
            this.new_encrypted_messages = new ArrayList();
            this.other_updates = new ArrayList();
            this.chats = new ArrayList();
            this.users = new ArrayList();
        }

        public static updates_Difference TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            updates_Difference result = null;
            switch (constructor) {
                case -1459938943:
                    result = new TL_updates_differenceSlice();
                    break;
                case 16030880:
                    result = new TL_updates_difference();
                    break;
                case 1567990072:
                    result = new TL_updates_differenceEmpty();
                    break;
            }
            if (result == null && exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(constructor);
                throw new RuntimeException(String.format("can't parse magic %x in updates_Difference", objArr));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class TL_account_noPassword extends account_Password {
        public static int constructor;

        static {
            constructor = -1764049896;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.new_salt = stream.readByteArray(exception);
            this.email_unconfirmed_pattern = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.new_salt);
            stream.writeString(this.email_unconfirmed_pattern);
        }
    }

    public static class TL_account_password extends account_Password {
        public static int constructor;

        static {
            constructor = 2081952796;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.current_salt = stream.readByteArray(exception);
            this.new_salt = stream.readByteArray(exception);
            this.hint = stream.readString(exception);
            this.has_recovery = stream.readBool(exception);
            this.email_unconfirmed_pattern = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.current_salt);
            stream.writeByteArray(this.new_salt);
            stream.writeString(this.hint);
            stream.writeBool(this.has_recovery);
            stream.writeString(this.email_unconfirmed_pattern);
        }
    }

    public static class TL_audioEmpty_layer45 extends Audio {
        public static int constructor;

        static {
            constructor = 1483311320;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
        }
    }

    public static class TL_audio_layer45 extends Audio {
        public static int constructor;

        static {
            constructor = -102543275;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.dc_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            stream.writeInt32(this.dc_id);
        }
    }

    public static class TL_auth_codeTypeCall extends auth_CodeType {
        public static int constructor;

        static {
            constructor = 1948046307;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_auth_codeTypeFlashCall extends auth_CodeType {
        public static int constructor;

        static {
            constructor = 577556219;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_auth_codeTypeSms extends auth_CodeType {
        public static int constructor;

        static {
            constructor = 1923290508;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_auth_sentCodeTypeApp extends auth_SentCodeType {
        public static int constructor;

        static {
            constructor = 1035688326;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_auth_sentCodeTypeCall extends auth_SentCodeType {
        public static int constructor;

        static {
            constructor = 1398007207;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_auth_sentCodeTypeFlashCall extends auth_SentCodeType {
        public static int constructor;

        static {
            constructor = -1425815847;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.pattern = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.pattern);
        }
    }

    public static class TL_auth_sentCodeTypeSms extends auth_SentCodeType {
        public static int constructor;

        static {
            constructor = -1073693790;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_boolFalse extends Bool {
        public static int constructor;

        static {
            constructor = -1132882121;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_boolTrue extends Bool {
        public static int constructor;

        static {
            constructor = -1720552011;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_botInfo extends BotInfo {
        public static int constructor;

        static {
            constructor = -1729618630;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.description = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_botCommand object = TL_botCommand.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.commands.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeString(this.description);
            stream.writeInt32(481674261);
            int count = this.commands.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_botCommand) this.commands.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_botInlineMediaResult extends BotInlineResult {
        public static int constructor;

        static {
            constructor = 400266251;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.id = stream.readString(exception);
            this.type = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.document = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.title = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.description = stream.readString(exception);
            }
            this.send_message = BotInlineMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeString(this.id);
            stream.writeString(this.type);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.photo.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.document.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.title);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeString(this.description);
            }
            this.send_message.serializeToStream(stream);
        }
    }

    public static class TL_botInlineMessageMediaAuto extends BotInlineMessage {
        public static int constructor;

        static {
            constructor = 175419739;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.caption = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeString(this.caption);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
        }
    }

    public static class TL_botInlineMessageMediaContact extends BotInlineMessage {
        public static int constructor;

        static {
            constructor = 904770772;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.phone_number = stream.readString(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeString(this.phone_number);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
        }
    }

    public static class TL_botInlineMessageMediaGeo extends BotInlineMessage {
        public static int constructor;

        static {
            constructor = 982505656;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            this.geo.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
        }
    }

    public static class TL_botInlineMessageMediaVenue extends BotInlineMessage {
        public static int constructor;

        static {
            constructor = 1130767150;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
            this.provider = stream.readString(exception);
            this.venue_id = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            this.geo.serializeToStream(stream);
            stream.writeString(this.title);
            stream.writeString(this.address);
            stream.writeString(this.provider);
            stream.writeString(this.venue_id);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
        }
    }

    public static class TL_botInlineMessageText extends BotInlineMessage {
        public static int constructor;

        static {
            constructor = -1937807902;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.no_webpage = z;
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.no_webpage) {
                i = this.flags | TLRPC.USER_FLAG_ACCESS_HASH;
            } else {
                i = this.flags & -2;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
        }
    }

    public static class TL_botInlineResult extends BotInlineResult {
        public static int constructor;

        static {
            constructor = -1679053127;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.id = stream.readString(exception);
            this.type = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.title = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.description = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.url = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.thumb_url = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.content_url = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.content_type = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.w = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.h = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                this.duration = stream.readInt32(exception);
            }
            this.send_message = BotInlineMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeString(this.id);
            stream.writeString(this.type);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.title);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.description);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeString(this.url);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeString(this.thumb_url);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeString(this.content_url);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeString(this.content_type);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeInt32(this.w);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeInt32(this.h);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(this.duration);
            }
            this.send_message.serializeToStream(stream);
        }
    }

    public static class TL_channel extends Chat {
        public static int constructor;

        static {
            constructor = -1588737454;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.creator = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.kicked = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.left = z;
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.editor = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.moderator = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.broadcast = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.verified = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.megagroup = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.restricted = z;
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.democracy = z;
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.signatures = z;
            if ((this.flags & MessagesController.UPDATE_MASK_SEND_STATE) == 0) {
                z2 = false;
            }
            this.min = z2;
            this.id = stream.readInt32(exception);
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                this.access_hash = stream.readInt64(exception);
            }
            this.title = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.username = stream.readString(exception);
            }
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.restriction_reason = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.creator ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.kicked ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.left ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.editor ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            this.flags = this.moderator ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.broadcast ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.verified ? this.flags | TLRPC.USER_FLAG_UNUSED : this.flags & -129;
            this.flags = this.megagroup ? this.flags | TLRPC.USER_FLAG_UNUSED2 : this.flags & -257;
            this.flags = this.restricted ? this.flags | TLRPC.USER_FLAG_UNUSED3 : this.flags & -513;
            this.flags = this.democracy ? this.flags | TLRPC.MESSAGE_FLAG_HAS_VIEWS : this.flags & -1025;
            this.flags = this.signatures ? this.flags | TLRPC.MESSAGE_FLAG_HAS_BOT_ID : this.flags & -2049;
            this.flags = this.min ? this.flags | MessagesController.UPDATE_MASK_SEND_STATE : this.flags & -4097;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                stream.writeInt64(this.access_hash);
            }
            stream.writeString(this.title);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeString(this.username);
            }
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeInt32(this.version);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                stream.writeString(this.restriction_reason);
            }
        }
    }

    public static class TL_channelForbidden extends Chat {
        public static int constructor;

        static {
            constructor = -2059962289;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z = true;
            this.flags = stream.readInt32(exception);
            this.broadcast = (this.flags & TLRPC.USER_FLAG_PHOTO) != 0;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) == 0) {
                z = false;
            }
            this.megagroup = z;
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.broadcast ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.megagroup ? this.flags | TLRPC.USER_FLAG_UNUSED2 : this.flags & -257;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
        }
    }

    public static class TL_channelForbidden_layer52 extends Chat {
        public static int constructor;

        static {
            constructor = 763724588;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
        }
    }

    public static class TL_channelFull extends ChatFull {
        public static int constructor;

        static {
            constructor = -1009430225;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.can_view_participants = z;
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.can_set_username = z;
            this.id = stream.readInt32(exception);
            this.about = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.participants_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.admins_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.kicked_count = stream.readInt32(exception);
            }
            this.read_inbox_max_id = stream.readInt32(exception);
            this.read_outbox_max_id = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
            this.chat_photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.exported_invite = ExportedChatInvite.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    BotInfo object = BotInfo.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.bot_info.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                    this.migrated_from_chat_id = stream.readInt32(exception);
                }
                if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                    this.migrated_from_max_id = stream.readInt32(exception);
                }
                if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                    this.pinned_msg_id = stream.readInt32(exception);
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.can_view_participants ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            if (this.can_set_username) {
                i = this.flags | TLRPC.USER_FLAG_STATUS;
            } else {
                i = this.flags & -65;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.about);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.participants_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.admins_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.kicked_count);
            }
            stream.writeInt32(this.read_inbox_max_id);
            stream.writeInt32(this.read_outbox_max_id);
            stream.writeInt32(this.unread_count);
            this.chat_photo.serializeToStream(stream);
            this.notify_settings.serializeToStream(stream);
            this.exported_invite.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.bot_info.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((BotInfo) this.bot_info.get(a)).serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeInt32(this.migrated_from_chat_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeInt32(this.migrated_from_max_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeInt32(this.pinned_msg_id);
            }
        }
    }

    public static class TL_channelMessagesFilter extends ChannelMessagesFilter {
        public static int constructor;

        static {
            constructor = -847783593;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.exclude_new_messages = z;
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_messageRange object = TL_messageRange.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.ranges.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.exclude_new_messages ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            stream.writeInt32(this.flags);
            stream.writeInt32(481674261);
            int count = this.ranges.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_messageRange) this.ranges.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_channelMessagesFilterEmpty extends ChannelMessagesFilter {
        public static int constructor;

        static {
            constructor = -1798033689;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelParticipant extends ChannelParticipant {
        public static int constructor;

        static {
            constructor = 367766557;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_channelParticipantCreator extends ChannelParticipant {
        public static int constructor;

        static {
            constructor = -471670279;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_channelParticipantEditor extends ChannelParticipant {
        public static int constructor;

        static {
            constructor = -1743180447;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.inviter_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.inviter_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_channelParticipantKicked extends ChannelParticipant {
        public static int constructor;

        static {
            constructor = -1933187430;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.kicked_by = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.kicked_by);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_channelParticipantModerator extends ChannelParticipant {
        public static int constructor;

        static {
            constructor = -1861910545;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.inviter_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.inviter_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_channelParticipantSelf extends ChannelParticipant {
        public static int constructor;

        static {
            constructor = -1557620115;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.inviter_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.inviter_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_channelParticipantsAdmins extends ChannelParticipantsFilter {
        public static int constructor;

        static {
            constructor = -1268741783;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelParticipantsBots extends ChannelParticipantsFilter {
        public static int constructor;

        static {
            constructor = -1328445861;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelParticipantsKicked extends ChannelParticipantsFilter {
        public static int constructor;

        static {
            constructor = 1010285434;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelParticipantsRecent extends ChannelParticipantsFilter {
        public static int constructor;

        static {
            constructor = -566281095;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelRoleEditor extends ChannelParticipantRole {
        public static int constructor;

        static {
            constructor = -2113143156;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelRoleEmpty extends ChannelParticipantRole {
        public static int constructor;

        static {
            constructor = -1299865402;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_channelRoleModerator extends ChannelParticipantRole {
        public static int constructor;

        static {
            constructor = -1776756363;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_chat extends Chat {
        public static int constructor;

        static {
            constructor = -652419756;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.creator = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.kicked = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.left = z;
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.admins_enabled = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.admin = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.deactivated = z2;
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.participants_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.migrated_to = InputChannel.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.creator ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.kicked ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.left ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.admins_enabled ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            this.flags = this.admin ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.deactivated ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.participants_count);
            stream.writeInt32(this.date);
            stream.writeInt32(this.version);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.migrated_to.serializeToStream(stream);
            }
        }
    }

    public static class TL_chatChannelParticipant extends ChatParticipant {
        public static int constructor;
        public ChannelParticipant channelParticipant;

        static {
            constructor = -925415106;
        }
    }

    public static class TL_chatEmpty extends Chat {
        public static int constructor;

        static {
            constructor = -1683826688;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.title = "DELETED";
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_chatForbidden extends Chat {
        public static int constructor;

        static {
            constructor = 120753115;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
        }
    }

    public static class TL_chatFull extends ChatFull {
        public static int constructor;

        static {
            constructor = 771925524;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.participants = ChatParticipants.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.chat_photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.exported_invite = ExportedChatInvite.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    BotInfo object = BotInfo.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.bot_info.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            this.participants.serializeToStream(stream);
            this.chat_photo.serializeToStream(stream);
            this.notify_settings.serializeToStream(stream);
            this.exported_invite.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.bot_info.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((BotInfo) this.bot_info.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_chatInvite extends ChatInvite {
        public static int constructor;

        static {
            constructor = -1813406880;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.channel = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.broadcast = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.isPublic = z;
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) == 0) {
                z2 = false;
            }
            this.megagroup = z2;
            this.title = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.channel ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.broadcast ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.isPublic ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.megagroup ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            stream.writeInt32(this.flags);
            stream.writeString(this.title);
        }
    }

    public static class TL_chatInviteAlready extends ChatInvite {
        public static int constructor;

        static {
            constructor = 1516793212;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.chat.serializeToStream(stream);
        }
    }

    public static class TL_chatInviteEmpty extends ExportedChatInvite {
        public static int constructor;

        static {
            constructor = 1776236393;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_chatInviteExported extends ExportedChatInvite {
        public static int constructor;

        static {
            constructor = -64092740;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.link = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.link);
        }
    }

    public static class TL_chatParticipant extends ChatParticipant {
        public static int constructor;

        static {
            constructor = -925415106;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.inviter_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.inviter_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_chatParticipantAdmin extends ChatParticipant {
        public static int constructor;

        static {
            constructor = -489233354;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.inviter_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.inviter_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_chatParticipantCreator extends ChatParticipant {
        public static int constructor;

        static {
            constructor = -636267638;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_chatParticipants extends ChatParticipants {
        public static int constructor;

        static {
            constructor = 1061556205;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    ChatParticipant object = ChatParticipant.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.participants.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.version = stream.readInt32(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(481674261);
            int count = this.participants.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((ChatParticipant) this.participants.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(this.version);
        }
    }

    public static class TL_chatParticipantsForbidden extends ChatParticipants {
        public static int constructor;

        static {
            constructor = -57668565;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.chat_id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.self_participant = ChatParticipant.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt32(this.chat_id);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.self_participant.serializeToStream(stream);
            }
        }
    }

    public static class TL_chatPhoto extends ChatPhoto {
        public static int constructor;

        static {
            constructor = 1632839530;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo_small = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.photo_big = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.photo_small.serializeToStream(stream);
            this.photo_big.serializeToStream(stream);
        }
    }

    public static class TL_chatPhotoEmpty extends ChatPhoto {
        public static int constructor;

        static {
            constructor = 935395612;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contactLinkContact extends ContactLink {
        public static int constructor;

        static {
            constructor = -721239344;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contactLinkHasPhone extends ContactLink {
        public static int constructor;

        static {
            constructor = 646922073;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contactLinkNone extends ContactLink {
        public static int constructor;

        static {
            constructor = -17968211;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contactLinkUnknown extends ContactLink {
        public static int constructor;

        static {
            constructor = 1599050311;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contacts_blocked extends contacts_Blocked {
        public static int constructor;

        static {
            constructor = 471043349;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_contactBlocked object = TL_contactBlocked.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.blocked.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.blocked.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_contactBlocked) this.blocked.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_blockedSlice extends contacts_Blocked {
        public static int constructor;

        static {
            constructor = -1878523231;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_contactBlocked object = TL_contactBlocked.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.blocked.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.blocked.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_contactBlocked) this.blocked.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_contacts extends contacts_Contacts {
        public static int constructor;

        static {
            constructor = 1871416498;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_contact object = TL_contact.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.contacts.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.contacts.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_contact) this.contacts.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_contactsNotModified extends contacts_Contacts {
        public static int constructor;

        static {
            constructor = -1219778094;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_contacts_topPeers extends contacts_TopPeers {
        public static int constructor;

        static {
            constructor = 1891070632;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_topPeerCategoryPeers object = TL_topPeerCategoryPeers.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.categories.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.categories.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_topPeerCategoryPeers) this.categories.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_contacts_topPeersNotModified extends contacts_TopPeers {
        public static int constructor;

        static {
            constructor = -567906571;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_decryptedMessage extends DecryptedMessage {
        public static int constructor;

        static {
            constructor = 917541342;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.random_id = stream.readInt64(exception);
            this.ttl = stream.readInt32(exception);
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media = DecryptedMessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                this.via_bot_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_random_id = stream.readInt64(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt64(this.random_id);
            stream.writeInt32(this.ttl);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                stream.writeString(this.via_bot_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt64(this.reply_to_random_id);
            }
        }
    }

    public static class TL_decryptedMessageActionAbortKey extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -586814357;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.exchange_id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.exchange_id);
        }
    }

    public static class TL_decryptedMessageActionAcceptKey extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = 1877046107;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.exchange_id = stream.readInt64(exception);
            this.g_b = stream.readByteArray(exception);
            this.key_fingerprint = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.exchange_id);
            stream.writeByteArray(this.g_b);
            stream.writeInt64(this.key_fingerprint);
        }
    }

    public static class TL_decryptedMessageActionCommitKey extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -332526693;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.exchange_id = stream.readInt64(exception);
            this.key_fingerprint = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.exchange_id);
            stream.writeInt64(this.key_fingerprint);
        }
    }

    public static class TL_decryptedMessageActionDeleteMessages extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = 1700872964;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.random_ids.add(Long.valueOf(stream.readInt64(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.random_ids.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.random_ids.get(a)).longValue());
            }
        }
    }

    public static class TL_decryptedMessageActionFlushHistory extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = 1729750108;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_decryptedMessageActionNoop extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -1473258141;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_decryptedMessageActionNotifyLayer extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -217806717;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.layer = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.layer);
        }
    }

    public static class TL_decryptedMessageActionReadMessages extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = 206520510;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.random_ids.add(Long.valueOf(stream.readInt64(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.random_ids.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.random_ids.get(a)).longValue());
            }
        }
    }

    public static class TL_decryptedMessageActionRequestKey extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -204906213;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.exchange_id = stream.readInt64(exception);
            this.g_a = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.exchange_id);
            stream.writeByteArray(this.g_a);
        }
    }

    public static class TL_decryptedMessageActionResend extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = 1360072880;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.start_seq_no = stream.readInt32(exception);
            this.end_seq_no = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.start_seq_no);
            stream.writeInt32(this.end_seq_no);
        }
    }

    public static class TL_decryptedMessageActionScreenshotMessages extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -1967000459;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.random_ids.add(Long.valueOf(stream.readInt64(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.random_ids.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.random_ids.get(a)).longValue());
            }
        }
    }

    public static class TL_decryptedMessageActionSetMessageTTL extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -1586283796;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.ttl_seconds = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.ttl_seconds);
        }
    }

    public static class TL_decryptedMessageActionTyping extends DecryptedMessageAction {
        public static int constructor;

        static {
            constructor = -860719551;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.action = SendMessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_decryptedMessageMediaAudio extends DecryptedMessageMedia {
        public static int constructor;

        static {
            constructor = 1474341323;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_decryptedMessageMediaContact extends DecryptedMessageMedia {
        public static int constructor;

        static {
            constructor = 1485441687;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.phone_number = stream.readString(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_decryptedMessageMediaDocument extends DecryptedMessageMedia {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = 2063502050;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    DocumentAttribute object = DocumentAttribute.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.attributes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.caption = stream.readString(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
            stream.writeInt32(481674261);
            int count = this.attributes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((DocumentAttribute) this.attributes.get(a)).serializeToStream(stream);
            }
            stream.writeString(this.caption);
        }
    }

    public static class TL_decryptedMessageMediaEmpty extends DecryptedMessageMedia {
        public static int constructor;

        static {
            constructor = 144661578;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_decryptedMessageMediaExternalDocument extends DecryptedMessageMedia {
        public static int constructor;
        public PhotoSize thumb;

        static {
            constructor = -90853155;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    DocumentAttribute object = DocumentAttribute.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.attributes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(481674261);
            int count = this.attributes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((DocumentAttribute) this.attributes.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_decryptedMessageMediaGeoPoint extends DecryptedMessageMedia {
        public static int constructor;

        static {
            constructor = 893913689;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.lat = stream.readDouble(exception);
            this._long = stream.readDouble(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeDouble(this.lat);
            stream.writeDouble(this._long);
        }
    }

    public static class TL_decryptedMessageMediaPhoto extends DecryptedMessageMedia {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = -235238024;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
            stream.writeString(this.caption);
        }
    }

    public static class TL_decryptedMessageMediaVenue extends DecryptedMessageMedia {
        public static int constructor;

        static {
            constructor = -1978796689;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.lat = stream.readDouble(exception);
            this._long = stream.readDouble(exception);
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
            this.provider = stream.readString(exception);
            this.venue_id = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeDouble(this.lat);
            stream.writeDouble(this._long);
            stream.writeString(this.title);
            stream.writeString(this.address);
            stream.writeString(this.provider);
            stream.writeString(this.venue_id);
        }
    }

    public static class TL_decryptedMessageMediaVideo extends DecryptedMessageMedia {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = -1760785394;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
            stream.writeString(this.caption);
        }
    }

    public static class TL_decryptedMessageMediaWebPage extends DecryptedMessageMedia {
        public static int constructor;

        static {
            constructor = -452652584;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.url);
        }
    }

    public static class TL_decryptedMessageService extends DecryptedMessage {
        public static int constructor;

        static {
            constructor = 1930838368;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.action = DecryptedMessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_document extends Document {
        public static int constructor;

        static {
            constructor = -106717361;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    DocumentAttribute object = DocumentAttribute.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.attributes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(481674261);
            int count = this.attributes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((DocumentAttribute) this.attributes.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_documentAttributeAnimated extends DocumentAttribute {
        public static int constructor;

        static {
            constructor = 297109817;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_documentAttributeAudio extends DocumentAttribute {
        public static int constructor;

        static {
            constructor = -1739392570;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.voice = (this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0;
            this.duration = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.title = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.performer = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.waveform = stream.readByteArray(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.voice ? this.flags | TLRPC.MESSAGE_FLAG_HAS_VIEWS : this.flags & -1025;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.duration);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.title);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.performer);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeByteArray(this.waveform);
            }
        }
    }

    public static class TL_documentAttributeFilename extends DocumentAttribute {
        public static int constructor;

        static {
            constructor = 358154344;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.file_name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.file_name);
        }
    }

    public static class TL_documentAttributeImageSize extends DocumentAttribute {
        public static int constructor;

        static {
            constructor = 1815593308;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_documentAttributeSticker extends DocumentAttribute {
        public static int constructor;

        static {
            constructor = 978674434;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.alt = stream.readString(exception);
            this.stickerset = InputStickerSet.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.alt);
            this.stickerset.serializeToStream(stream);
        }
    }

    public static class TL_documentAttributeVideo extends DocumentAttribute {
        public static int constructor;

        static {
            constructor = 1494273227;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.duration = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_documentEmpty extends Document {
        public static int constructor;

        static {
            constructor = 922273905;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
        }
    }

    public static class TL_documentEncrypted extends Document {
        public static int constructor;

        static {
            constructor = 1431655768;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            int startReadPosiition = stream.getPosition();
            try {
                this.mime_type = stream.readString(true);
            } catch (Exception e) {
                this.mime_type = "audio/ogg";
                if (stream instanceof NativeByteBuffer) {
                    ((NativeByteBuffer) stream).position(startReadPosiition);
                }
            }
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    DocumentAttribute object = DocumentAttribute.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.attributes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.key = stream.readByteArray(exception);
                this.iv = stream.readByteArray(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(481674261);
            int count = this.attributes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((DocumentAttribute) this.attributes.get(a)).serializeToStream(stream);
            }
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_draftMessage extends DraftMessage {
        public static int constructor;

        static {
            constructor = -40996577;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.no_webpage = z;
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.no_webpage) {
                i = this.flags | TLRPC.USER_FLAG_FIRST_NAME;
            } else {
                i = this.flags & -3;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            stream.writeInt32(this.date);
        }
    }

    public static class TL_draftMessageEmpty extends DraftMessage {
        public static int constructor;

        static {
            constructor = -1169445179;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_encryptedChat extends EncryptedChat {
        public static int constructor;

        static {
            constructor = -94974410;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            this.participant_id = stream.readInt32(exception);
            this.g_a_or_b = stream.readByteArray(exception);
            this.key_fingerprint = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(this.participant_id);
            stream.writeByteArray(this.g_a_or_b);
            stream.writeInt64(this.key_fingerprint);
        }
    }

    public static class TL_encryptedChatDiscarded extends EncryptedChat {
        public static int constructor;

        static {
            constructor = 332848423;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_encryptedChatEmpty extends EncryptedChat {
        public static int constructor;

        static {
            constructor = -1417756512;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_encryptedChatRequested extends EncryptedChat {
        public static int constructor;

        static {
            constructor = -931638658;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            this.participant_id = stream.readInt32(exception);
            this.g_a = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(this.participant_id);
            stream.writeByteArray(this.g_a);
        }
    }

    public static class TL_encryptedChatWaiting extends EncryptedChat {
        public static int constructor;

        static {
            constructor = 1006044124;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            this.participant_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(this.participant_id);
        }
    }

    public static class TL_encryptedFile extends EncryptedFile {
        public static int constructor;

        static {
            constructor = 1248893260;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.size = stream.readInt32(exception);
            this.dc_id = stream.readInt32(exception);
            this.key_fingerprint = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.size);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.key_fingerprint);
        }
    }

    public static class TL_encryptedFileEmpty extends EncryptedFile {
        public static int constructor;

        static {
            constructor = -1038136962;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_encryptedMessage extends EncryptedMessage {
        public static int constructor;

        static {
            constructor = -317144808;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.chat_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.bytes = stream.readByteArray(exception);
            this.file = EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.date);
            stream.writeByteArray(this.bytes);
            this.file.serializeToStream(stream);
        }
    }

    public static class TL_encryptedMessageService extends EncryptedMessage {
        public static int constructor;

        static {
            constructor = 594758406;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.chat_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.bytes = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.date);
            stream.writeByteArray(this.bytes);
        }
    }

    public static class TL_fileEncryptedLocation extends FileLocation {
        public static int constructor;

        static {
            constructor = 1431655764;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.dc_id = stream.readInt32(exception);
            this.volume_id = stream.readInt64(exception);
            this.local_id = stream.readInt32(exception);
            this.secret = stream.readInt64(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.dc_id);
            stream.writeInt64(this.volume_id);
            stream.writeInt32(this.local_id);
            stream.writeInt64(this.secret);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_fileLocation extends FileLocation {
        public static int constructor;

        static {
            constructor = 1406570614;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.dc_id = stream.readInt32(exception);
            this.volume_id = stream.readInt64(exception);
            this.local_id = stream.readInt32(exception);
            this.secret = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.dc_id);
            stream.writeInt64(this.volume_id);
            stream.writeInt32(this.local_id);
            stream.writeInt64(this.secret);
        }
    }

    public static class TL_fileLocationUnavailable extends FileLocation {
        public static int constructor;

        static {
            constructor = 2086234950;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.volume_id = stream.readInt64(exception);
            this.local_id = stream.readInt32(exception);
            this.secret = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.volume_id);
            stream.writeInt32(this.local_id);
            stream.writeInt64(this.secret);
        }
    }

    public static class TL_foundGif extends FoundGif {
        public static int constructor;

        static {
            constructor = 372165663;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
            this.thumb_url = stream.readString(exception);
            this.content_url = stream.readString(exception);
            this.content_type = stream.readString(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.url);
            stream.writeString(this.thumb_url);
            stream.writeString(this.content_url);
            stream.writeString(this.content_type);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_foundGifCached extends FoundGif {
        public static int constructor;

        static {
            constructor = -1670052855;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
            this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.document = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.url);
            this.photo.serializeToStream(stream);
            this.document.serializeToStream(stream);
        }
    }

    public static class TL_geoChat extends Chat {
        public static int constructor;

        static {
            constructor = 1978329690;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
            this.venue = stream.readString(exception);
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.participants_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.checked_in = stream.readBool(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
            stream.writeString(this.address);
            stream.writeString(this.venue);
            this.geo.serializeToStream(stream);
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.participants_count);
            stream.writeInt32(this.date);
            stream.writeBool(this.checked_in);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_geoChatMessage extends GeoChatMessage {
        public static int constructor;

        static {
            constructor = 1158019297;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
        }
    }

    public static class TL_geoChatMessageEmpty extends GeoChatMessage {
        public static int constructor;

        static {
            constructor = 1613830811;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_geoChatMessageService extends GeoChatMessage {
        public static int constructor;

        static {
            constructor = -749755826;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.action = MessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            stream.writeInt32(this.date);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_geoPoint extends GeoPoint {
        public static int constructor;

        static {
            constructor = 541710092;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this._long = stream.readDouble(exception);
            this.lat = stream.readDouble(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeDouble(this._long);
            stream.writeDouble(this.lat);
        }
    }

    public static class TL_geoPointEmpty extends GeoPoint {
        public static int constructor;

        static {
            constructor = 286776671;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_geochats_messages extends geochats_Messages {
        public static int constructor;

        static {
            constructor = -783127119;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    GeoChatMessage object = GeoChatMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((GeoChatMessage) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_geochats_messagesSlice extends geochats_Messages {
        public static int constructor;

        static {
            constructor = -1135057944;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    GeoChatMessage object = GeoChatMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((GeoChatMessage) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_help_appChangelog extends help_AppChangelog {
        public static int constructor;

        static {
            constructor = 1181279933;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
        }
    }

    public static class TL_help_appChangelogEmpty extends help_AppChangelog {
        public static int constructor;

        static {
            constructor = -1350696044;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_help_appUpdate extends help_AppUpdate {
        public static int constructor;

        static {
            constructor = -1987579119;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.critical = stream.readBool(exception);
            this.url = stream.readString(exception);
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeBool(this.critical);
            stream.writeString(this.url);
            stream.writeString(this.text);
        }
    }

    public static class TL_help_noAppUpdate extends help_AppUpdate {
        public static int constructor;

        static {
            constructor = -1000708810;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputChannel extends InputChannel {
        public static int constructor;

        static {
            constructor = -1343524562;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputChannelEmpty extends InputChannel {
        public static int constructor;

        static {
            constructor = -292807034;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputChatPhoto extends InputChatPhoto {
        public static int constructor;

        static {
            constructor = -1293828344;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = InputPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.crop = InputPhotoCrop.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
            this.crop.serializeToStream(stream);
        }
    }

    public static class TL_inputChatPhotoEmpty extends InputChatPhoto {
        public static int constructor;

        static {
            constructor = 480546647;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputChatUploadedPhoto extends InputChatPhoto {
        public static int constructor;

        static {
            constructor = -1809496270;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.file = InputFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.crop = InputPhotoCrop.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.file.serializeToStream(stream);
            this.crop.serializeToStream(stream);
        }
    }

    public static class TL_inputDocument extends InputDocument {
        public static int constructor;

        static {
            constructor = 410618194;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputDocumentEmpty extends InputDocument {
        public static int constructor;

        static {
            constructor = 1928391342;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputDocumentFileLocation extends InputFileLocation {
        public static int constructor;

        static {
            constructor = 1313188841;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputEncryptedFile extends InputEncryptedFile {
        public static int constructor;

        static {
            constructor = 1511503333;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputEncryptedFileBigUploaded extends InputEncryptedFile {
        public static int constructor;

        static {
            constructor = 767652808;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.parts = stream.readInt32(exception);
            this.key_fingerprint = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt32(this.parts);
            stream.writeInt32(this.key_fingerprint);
        }
    }

    public static class TL_inputEncryptedFileEmpty extends InputEncryptedFile {
        public static int constructor;

        static {
            constructor = 406307684;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputEncryptedFileLocation extends InputFileLocation {
        public static int constructor;

        static {
            constructor = -182231723;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputEncryptedFileUploaded extends InputEncryptedFile {
        public static int constructor;

        static {
            constructor = 1690108678;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.parts = stream.readInt32(exception);
            this.md5_checksum = stream.readString(exception);
            this.key_fingerprint = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt32(this.parts);
            stream.writeString(this.md5_checksum);
            stream.writeInt32(this.key_fingerprint);
        }
    }

    public static class TL_inputFile extends InputFile {
        public static int constructor;

        static {
            constructor = -181407105;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.parts = stream.readInt32(exception);
            this.name = stream.readString(exception);
            this.md5_checksum = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt32(this.parts);
            stream.writeString(this.name);
            stream.writeString(this.md5_checksum);
        }
    }

    public static class TL_inputFileBig extends InputFile {
        public static int constructor;

        static {
            constructor = -95482955;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.parts = stream.readInt32(exception);
            this.name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt32(this.parts);
            stream.writeString(this.name);
        }
    }

    public static class TL_inputFileLocation extends InputFileLocation {
        public static int constructor;

        static {
            constructor = 342061462;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.volume_id = stream.readInt64(exception);
            this.local_id = stream.readInt32(exception);
            this.secret = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.volume_id);
            stream.writeInt32(this.local_id);
            stream.writeInt64(this.secret);
        }
    }

    public static class TL_inputGeoPoint extends InputGeoPoint {
        public static int constructor;

        static {
            constructor = -206066487;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.lat = stream.readDouble(exception);
            this._long = stream.readDouble(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeDouble(this.lat);
            stream.writeDouble(this._long);
        }
    }

    public static class TL_inputGeoPointEmpty extends InputGeoPoint {
        public static int constructor;

        static {
            constructor = -457104426;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMediaContact extends InputMedia {
        public static int constructor;

        static {
            constructor = -1494984313;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.phone_number = stream.readString(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
        }
    }

    public static class TL_inputMediaDocument extends InputMedia {
        public static int constructor;
        public InputDocument id;

        static {
            constructor = 444068508;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = InputDocument.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
            stream.writeString(this.caption);
        }
    }

    public static class TL_inputMediaEmpty extends InputMedia {
        public static int constructor;

        static {
            constructor = -1771768449;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMediaGeoPoint extends InputMedia {
        public static int constructor;

        static {
            constructor = -104578748;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.geo_point = InputGeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.geo_point.serializeToStream(stream);
        }
    }

    public static class TL_inputMediaGifExternal extends InputMedia {
        public static int constructor;

        static {
            constructor = 1212395773;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
            this.q = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.url);
            stream.writeString(this.q);
        }
    }

    public static class TL_inputMediaPhoto extends InputMedia {
        public static int constructor;
        public InputPhoto id;

        static {
            constructor = -373312269;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = InputPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.id.serializeToStream(stream);
            stream.writeString(this.caption);
        }
    }

    public static class TL_inputMediaUploadedDocument extends InputMedia {
        public static int constructor;

        static {
            constructor = 495530093;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.file = InputFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.mime_type = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    DocumentAttribute object = DocumentAttribute.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.attributes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.caption = stream.readString(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.file.serializeToStream(stream);
            stream.writeString(this.mime_type);
            stream.writeInt32(481674261);
            int count = this.attributes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((DocumentAttribute) this.attributes.get(a)).serializeToStream(stream);
            }
            stream.writeString(this.caption);
        }
    }

    public static class TL_inputMediaUploadedPhoto extends InputMedia {
        public static int constructor;

        static {
            constructor = -139464256;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.file = InputFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.file.serializeToStream(stream);
            stream.writeString(this.caption);
        }
    }

    public static class TL_inputMediaUploadedThumbDocument extends InputMedia {
        public static int constructor;

        static {
            constructor = -1386138479;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.file = InputFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.thumb = InputFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.mime_type = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    DocumentAttribute object = DocumentAttribute.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.attributes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.caption = stream.readString(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.file.serializeToStream(stream);
            this.thumb.serializeToStream(stream);
            stream.writeString(this.mime_type);
            stream.writeInt32(481674261);
            int count = this.attributes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((DocumentAttribute) this.attributes.get(a)).serializeToStream(stream);
            }
            stream.writeString(this.caption);
        }
    }

    public static class TL_inputMediaVenue extends InputMedia {
        public static int constructor;

        static {
            constructor = 673687578;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.geo_point = InputGeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
            this.provider = stream.readString(exception);
            this.venue_id = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.geo_point.serializeToStream(stream);
            stream.writeString(this.title);
            stream.writeString(this.address);
            stream.writeString(this.provider);
            stream.writeString(this.venue_id);
        }
    }

    public static class TL_inputMessageEntityMentionName extends MessageEntity {
        public static int constructor;
        public InputUser user_id;

        static {
            constructor = 546203849;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
            this.user_id = InputUser.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
            this.user_id.serializeToStream(stream);
        }
    }

    public static class TL_inputMessagesFilterChatPhotos extends MessagesFilter {
        public static int constructor;

        static {
            constructor = 975236280;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterDocument extends MessagesFilter {
        public static int constructor;

        static {
            constructor = -1629621880;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterEmpty extends MessagesFilter {
        public static int constructor;

        static {
            constructor = 1474492012;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterGif extends MessagesFilter {
        public static int constructor;

        static {
            constructor = -3644025;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterMusic extends MessagesFilter {
        public static int constructor;

        static {
            constructor = 928101534;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterPhotoVideo extends MessagesFilter {
        public static int constructor;

        static {
            constructor = 1458172132;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterPhotoVideoDocuments extends MessagesFilter {
        public static int constructor;

        static {
            constructor = -648121413;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterPhotos extends MessagesFilter {
        public static int constructor;

        static {
            constructor = -1777752804;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterUrl extends MessagesFilter {
        public static int constructor;

        static {
            constructor = 2129714567;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterVideo extends MessagesFilter {
        public static int constructor;

        static {
            constructor = -1614803355;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputMessagesFilterVoice extends MessagesFilter {
        public static int constructor;

        static {
            constructor = 1358283666;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputNotifyAll extends InputNotifyPeer {
        public static int constructor;

        static {
            constructor = -1540769658;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputNotifyChats extends InputNotifyPeer {
        public static int constructor;

        static {
            constructor = 1251338318;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputNotifyGeoChatPeer extends InputNotifyPeer {
        public static int constructor;
        public TL_inputGeoChat peer;

        static {
            constructor = 1301143240;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = TL_inputGeoChat.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_inputNotifyPeer extends InputNotifyPeer {
        public static int constructor;
        public InputPeer peer;

        static {
            constructor = -1195615476;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = InputPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_inputNotifyUsers extends InputNotifyPeer {
        public static int constructor;

        static {
            constructor = 423314455;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPeerChannel extends InputPeer {
        public static int constructor;

        static {
            constructor = 548253432;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputPeerChat extends InputPeer {
        public static int constructor;

        static {
            constructor = 396093539;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_inputPeerEmpty extends InputPeer {
        public static int constructor;

        static {
            constructor = 2134579434;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPeerNotifyEventsAll extends InputPeerNotifyEvents {
        public static int constructor;

        static {
            constructor = -395694988;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPeerNotifyEventsEmpty extends InputPeerNotifyEvents {
        public static int constructor;

        static {
            constructor = -265263912;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPeerSelf extends InputPeer {
        public static int constructor;

        static {
            constructor = 2107670217;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPeerUser extends InputPeer {
        public static int constructor;

        static {
            constructor = 2072935910;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputPhoto extends InputPhoto {
        public static int constructor;

        static {
            constructor = -74070332;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputPhotoCrop extends InputPhotoCrop {
        public static int constructor;

        static {
            constructor = -644787419;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.crop_left = stream.readDouble(exception);
            this.crop_top = stream.readDouble(exception);
            this.crop_width = stream.readDouble(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeDouble(this.crop_left);
            stream.writeDouble(this.crop_top);
            stream.writeDouble(this.crop_width);
        }
    }

    public static class TL_inputPhotoCropAuto extends InputPhotoCrop {
        public static int constructor;

        static {
            constructor = -1377390588;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPhotoEmpty extends InputPhoto {
        public static int constructor;

        static {
            constructor = 483901197;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyKeyChatInvite extends InputPrivacyKey {
        public static int constructor;

        static {
            constructor = -1107622874;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyKeyStatusTimestamp extends InputPrivacyKey {
        public static int constructor;

        static {
            constructor = 1335282456;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyValueAllowAll extends InputPrivacyRule {
        public static int constructor;

        static {
            constructor = 407582158;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyValueAllowContacts extends InputPrivacyRule {
        public static int constructor;

        static {
            constructor = 218751099;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyValueAllowUsers extends InputPrivacyRule {
        public static int constructor;

        static {
            constructor = 320652927;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    InputUser object = InputUser.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.users.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_inputPrivacyValueDisallowAll extends InputPrivacyRule {
        public static int constructor;

        static {
            constructor = -697604407;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyValueDisallowContacts extends InputPrivacyRule {
        public static int constructor;

        static {
            constructor = 195371015;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputPrivacyValueDisallowUsers extends InputPrivacyRule {
        public static int constructor;

        static {
            constructor = -1877932953;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    InputUser object = InputUser.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.users.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((InputUser) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_inputReportReasonOther extends ReportReason {
        public static int constructor;

        static {
            constructor = -512463606;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
        }
    }

    public static class TL_inputReportReasonPornography extends ReportReason {
        public static int constructor;

        static {
            constructor = 777640226;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputReportReasonSpam extends ReportReason {
        public static int constructor;

        static {
            constructor = 1490799288;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputReportReasonViolence extends ReportReason {
        public static int constructor;

        static {
            constructor = 505595789;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputStickerSetEmpty extends InputStickerSet {
        public static int constructor;

        static {
            constructor = -4838507;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputStickerSetID extends InputStickerSet {
        public static int constructor;

        static {
            constructor = -1645763991;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputStickerSetShortName extends InputStickerSet {
        public static int constructor;

        static {
            constructor = -2044933984;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.short_name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.short_name);
        }
    }

    public static class TL_inputUser extends InputUser {
        public static int constructor;

        static {
            constructor = -668391402;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt64(this.access_hash);
        }
    }

    public static class TL_inputUserEmpty extends InputUser {
        public static int constructor;

        static {
            constructor = -1182234929;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_inputUserSelf extends InputUser {
        public static int constructor;

        static {
            constructor = -138301121;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_keyboardButton extends KeyboardButton {
        public static int constructor;

        static {
            constructor = -1560655744;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
        }
    }

    public static class TL_keyboardButtonCallback extends KeyboardButton {
        public static int constructor;

        static {
            constructor = 1748655686;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
            this.data = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
            stream.writeByteArray(this.data);
        }
    }

    public static class TL_keyboardButtonRequestGeoLocation extends KeyboardButton {
        public static int constructor;

        static {
            constructor = -59151553;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
        }
    }

    public static class TL_keyboardButtonRequestPhone extends KeyboardButton {
        public static int constructor;

        static {
            constructor = -1318425559;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
        }
    }

    public static class TL_keyboardButtonSwitchInline extends KeyboardButton {
        public static int constructor;

        static {
            constructor = -367298028;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
            this.query = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
            stream.writeString(this.query);
        }
    }

    public static class TL_keyboardButtonUrl extends KeyboardButton {
        public static int constructor;

        static {
            constructor = 629866245;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.text = stream.readString(exception);
            this.url = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.text);
            stream.writeString(this.url);
        }
    }

    public static class TL_message extends Message {
        public static int constructor;

        static {
            constructor = -1063525281;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            int a;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.unread = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.silent = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.post = z;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.from_id = stream.readInt32(exception);
            }
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.from_id == 0) {
                if (this.to_id.user_id != 0) {
                    this.from_id = this.to_id.user_id;
                } else {
                    this.from_id = -this.to_id.channel_id;
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = TL_messageFwdHeader.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                this.via_bot_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            } else {
                this.media = new TL_messageMediaEmpty();
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                this.views = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0) {
                this.edit_date = stream.readInt32(exception);
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
                if (this.id < 0 && this.attachPath.startsWith("||")) {
                    String[] args = this.attachPath.split("\\|\\|");
                    if (args.length > 0) {
                        this.params = new HashMap();
                        for (a = TLRPC.USER_FLAG_ACCESS_HASH; a < args.length - 1; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                            String[] args2 = args[a].split("\\|=\\|");
                            if (args2.length == TLRPC.USER_FLAG_FIRST_NAME) {
                                this.params.put(args2[0], args2[TLRPC.USER_FLAG_ACCESS_HASH]);
                            }
                        }
                        this.attachPath = args[args.length - 1];
                    }
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.silent ? this.flags | MessagesController.UPDATE_MASK_CHANNEL : this.flags & -8193;
            if (this.post) {
                i = this.flags | MessagesController.UPDATE_MASK_CHAT_ADMINS;
            } else {
                i = this.flags & -16385;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeInt32(this.from_id);
            }
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                stream.writeInt32(this.via_bot_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                stream.writeInt32(this.views);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0) {
                stream.writeInt32(this.edit_date);
            }
            String path = this.attachPath;
            if (this.id < 0 && this.params != null && this.params.size() > 0) {
                for (Entry<String, String> entry : this.params.entrySet()) {
                    path = ((String) entry.getKey()) + "|=|" + ((String) entry.getValue()) + "||" + path;
                }
                path = "||" + path;
            }
            stream.writeString(path);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_messageActionChannelCreate extends MessageAction {
        public static int constructor;

        static {
            constructor = -1781355374;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.title = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
        }
    }

    public static class TL_messageActionChannelMigrateFrom extends MessageAction {
        public static int constructor;

        static {
            constructor = -1336546578;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.title = stream.readString(exception);
            this.chat_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_messageActionChatAddUser extends MessageAction {
        public static int constructor;

        static {
            constructor = 1217033015;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.users.add(Integer.valueOf(stream.readInt32(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.users.get(a)).intValue());
            }
        }
    }

    public static class TL_messageActionChatCreate extends MessageAction {
        public static int constructor;

        static {
            constructor = -1503425638;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.title = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.users.add(Integer.valueOf(stream.readInt32(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.users.get(a)).intValue());
            }
        }
    }

    public static class TL_messageActionChatDeletePhoto extends MessageAction {
        public static int constructor;

        static {
            constructor = -1780220945;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionChatDeleteUser extends MessageAction {
        public static int constructor;

        static {
            constructor = -1297179892;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_messageActionChatEditPhoto extends MessageAction {
        public static int constructor;

        static {
            constructor = 2144015272;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.photo.serializeToStream(stream);
        }
    }

    public static class TL_messageActionChatEditTitle extends MessageAction {
        public static int constructor;

        static {
            constructor = -1247687078;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.title = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
        }
    }

    public static class TL_messageActionChatJoinedByLink extends MessageAction {
        public static int constructor;

        static {
            constructor = -123931160;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.inviter_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.inviter_id);
        }
    }

    public static class TL_messageActionChatMigrateTo extends MessageAction {
        public static int constructor;

        static {
            constructor = 1371385889;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
        }
    }

    public static class TL_messageActionCreatedBroadcastList extends MessageAction {
        public static int constructor;

        static {
            constructor = 1431655767;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionEmpty extends MessageAction {
        public static int constructor;

        static {
            constructor = -1230047312;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionGeoChatCheckin extends MessageAction {
        public static int constructor;

        static {
            constructor = 209540062;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionGeoChatCreate extends MessageAction {
        public static int constructor;

        static {
            constructor = 1862504124;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
            stream.writeString(this.address);
        }
    }

    public static class TL_messageActionHistoryClear extends MessageAction {
        public static int constructor;

        static {
            constructor = -1615153660;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionLoginUnknownLocation extends MessageAction {
        public static int constructor;

        static {
            constructor = 1431655925;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.title);
            stream.writeString(this.address);
        }
    }

    public static class TL_messageActionPinMessage extends MessageAction {
        public static int constructor;

        static {
            constructor = -1799538451;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionTTLChange extends MessageAction {
        public static int constructor;

        static {
            constructor = 1431655762;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.ttl = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.ttl);
        }
    }

    public static class TL_messageActionUserJoined extends MessageAction {
        public static int constructor;

        static {
            constructor = 1431655760;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageActionUserUpdatedPhoto extends MessageAction {
        public static int constructor;

        static {
            constructor = 1431655761;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.newUserPhoto = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.newUserPhoto.serializeToStream(stream);
        }
    }

    public static class TL_messageEmpty extends Message {
        public static int constructor;

        static {
            constructor = -2082087340;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.to_id = new TL_peerUser();
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_messageEncryptedAction extends MessageAction {
        public static int constructor;

        static {
            constructor = 1431655927;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.encryptedAction = DecryptedMessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.encryptedAction.serializeToStream(stream);
        }
    }

    public static class TL_messageEntityBold extends MessageEntity {
        public static int constructor;

        static {
            constructor = -1117713463;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityBotCommand extends MessageEntity {
        public static int constructor;

        static {
            constructor = 1827637959;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityCode extends MessageEntity {
        public static int constructor;

        static {
            constructor = 681706865;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityEmail extends MessageEntity {
        public static int constructor;

        static {
            constructor = 1692693954;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityHashtag extends MessageEntity {
        public static int constructor;

        static {
            constructor = 1868782349;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityItalic extends MessageEntity {
        public static int constructor;

        static {
            constructor = -2106619040;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityMention extends MessageEntity {
        public static int constructor;

        static {
            constructor = -100378723;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityMentionName extends MessageEntity {
        public static int constructor;
        public int user_id;

        static {
            constructor = 892193368;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_messageEntityPre extends MessageEntity {
        public static int constructor;

        static {
            constructor = 1938967520;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
            this.language = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
            stream.writeString(this.language);
        }
    }

    public static class TL_messageEntityTextUrl extends MessageEntity {
        public static int constructor;

        static {
            constructor = 1990644519;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
            this.url = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
            stream.writeString(this.url);
        }
    }

    public static class TL_messageEntityUnknown extends MessageEntity {
        public static int constructor;

        static {
            constructor = -1148011883;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageEntityUrl extends MessageEntity {
        public static int constructor;

        static {
            constructor = 1859134776;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.offset = stream.readInt32(exception);
            this.length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.offset);
            stream.writeInt32(this.length);
        }
    }

    public static class TL_messageForwarded_old2 extends Message {
        public static int constructor;

        static {
            constructor = -1553471722;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.media_unread = z2;
            this.id = stream.readInt32(exception);
            this.fwd_from = new TL_messageFwdHeader();
            this.fwd_from.from_id = stream.readInt32(exception);
            TL_messageFwdHeader tL_messageFwdHeader = this.fwd_from;
            tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
            this.fwd_from.date = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.flags |= 772;
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.fwd_from.from_id);
            stream.writeInt32(this.fwd_from.date);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            if (this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
            stream.writeString(this.attachPath);
        }
    }

    public static class TL_messageMediaAudio_layer45 extends MessageMedia {
        public static int constructor;

        static {
            constructor = -961117440;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.audio_unused = Audio.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.audio_unused.serializeToStream(stream);
        }
    }

    public static class TL_messageMediaContact extends MessageMedia {
        public static int constructor;

        static {
            constructor = 1585262393;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.phone_number = stream.readString(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_number);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_messageMediaDocument extends MessageMedia {
        public static int constructor;

        static {
            constructor = -203411800;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.document = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.document.serializeToStream(stream);
            stream.writeString(this.caption);
        }
    }

    public static class TL_messageMediaEmpty extends MessageMedia {
        public static int constructor;

        static {
            constructor = 1038967584;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageMediaGeo extends MessageMedia {
        public static int constructor;

        static {
            constructor = 1457575028;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.geo.serializeToStream(stream);
        }
    }

    public static class TL_messageMediaPhoto extends MessageMedia {
        public static int constructor;

        static {
            constructor = 1032643901;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.photo.serializeToStream(stream);
            stream.writeString(this.caption);
        }
    }

    public static class TL_messageMediaUnsupported extends MessageMedia {
        public static int constructor;

        static {
            constructor = -1618676578;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messageMediaVenue extends MessageMedia {
        public static int constructor;

        static {
            constructor = 2031269663;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.title = stream.readString(exception);
            this.address = stream.readString(exception);
            this.provider = stream.readString(exception);
            this.venue_id = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.geo.serializeToStream(stream);
            stream.writeString(this.title);
            stream.writeString(this.address);
            stream.writeString(this.provider);
            stream.writeString(this.venue_id);
        }
    }

    public static class TL_messageMediaVideo_layer45 extends MessageMedia {
        public static int constructor;

        static {
            constructor = 1540298357;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.video_unused = Video.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.caption = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.video_unused.serializeToStream(stream);
            stream.writeString(this.caption);
        }
    }

    public static class TL_messageMediaWebPage extends MessageMedia {
        public static int constructor;

        static {
            constructor = -1557277184;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.webpage = WebPage.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.webpage.serializeToStream(stream);
        }
    }

    public static class TL_messageService extends Message {
        public static int constructor;

        static {
            constructor = -1642487306;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.silent = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) == 0) {
                z2 = false;
            }
            this.post = z2;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.from_id = stream.readInt32(exception);
            }
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.action = MessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.silent ? this.flags | MessagesController.UPDATE_MASK_CHANNEL : this.flags & -8193;
            this.flags = this.post ? this.flags | MessagesController.UPDATE_MASK_CHAT_ADMINS : this.flags & -16385;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeInt32(this.from_id);
            }
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_messages_allStickers extends messages_AllStickers {
        public static int constructor;
        public int hash;

        static {
            constructor = -302170017;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.hash = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    StickerSet object = StickerSet.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.sets.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.hash);
            stream.writeInt32(481674261);
            int count = this.sets.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((StickerSet) this.sets.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_allStickersNotModified extends messages_AllStickers {
        public static int constructor;

        static {
            constructor = -395967805;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messages_channelMessages extends messages_Messages {
        public static int constructor;

        static {
            constructor = -1725551049;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.pts = stream.readInt32(exception);
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_dhConfig extends messages_DhConfig {
        public static int constructor;

        static {
            constructor = 740433629;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.g = stream.readInt32(exception);
            this.p = stream.readByteArray(exception);
            this.version = stream.readInt32(exception);
            this.random = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.g);
            stream.writeByteArray(this.p);
            stream.writeInt32(this.version);
            stream.writeByteArray(this.random);
        }
    }

    public static class TL_messages_dhConfigNotModified extends messages_DhConfig {
        public static int constructor;

        static {
            constructor = -1058912715;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.random);
        }
    }

    public static class TL_messages_dialogs extends messages_Dialogs {
        public static int constructor;

        static {
            constructor = 364538944;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_dialog object = TL_dialog.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.dialogs.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Message object2 = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.messages.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                User object4 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.users.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.dialogs.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_dialog) this.dialogs.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_dialogsSlice extends messages_Dialogs {
        public static int constructor;

        static {
            constructor = 1910543603;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_dialog object = TL_dialog.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.dialogs.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Message object2 = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.messages.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                User object4 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.users.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.dialogs.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_dialog) this.dialogs.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_messages extends messages_Messages {
        public static int constructor;

        static {
            constructor = -1938715001;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_messagesSlice extends messages_Messages {
        public static int constructor;

        static {
            constructor = 189033187;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_savedGifs extends messages_SavedGifs {
        public static int constructor;

        static {
            constructor = 772213157;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.hash = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Document object = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.gifs.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.hash);
            stream.writeInt32(481674261);
            int count = this.gifs.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Document) this.gifs.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_savedGifsNotModified extends messages_SavedGifs {
        public static int constructor;

        static {
            constructor = -402498398;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_messages_sentEncryptedFile extends messages_SentEncryptedMessage {
        public static int constructor;

        static {
            constructor = -1802240206;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.date = stream.readInt32(exception);
            this.file = EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.date);
            this.file.serializeToStream(stream);
        }
    }

    public static class TL_messages_sentEncryptedMessage extends messages_SentEncryptedMessage {
        public static int constructor;

        static {
            constructor = 1443858741;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_messages_stickers extends messages_Stickers {
        public static int constructor;

        static {
            constructor = -1970352846;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.hash = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Document object = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.stickers.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.hash);
            stream.writeInt32(481674261);
            int count = this.stickers.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Document) this.stickers.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_messages_stickersNotModified extends messages_Stickers {
        public static int constructor;

        static {
            constructor = -244016606;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_notifyAll extends NotifyPeer {
        public static int constructor;

        static {
            constructor = 1959820384;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_notifyChats extends NotifyPeer {
        public static int constructor;

        static {
            constructor = -1073230141;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_notifyPeer extends NotifyPeer {
        public static int constructor;

        static {
            constructor = -1613493288;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_notifyUsers extends NotifyPeer {
        public static int constructor;

        static {
            constructor = -1261946036;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_peerChannel extends Peer {
        public static int constructor;

        static {
            constructor = -1109531342;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
        }
    }

    public static class TL_peerChat extends Peer {
        public static int constructor;

        static {
            constructor = -1160714821;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_peerNotifyEventsAll extends PeerNotifyEvents {
        public static int constructor;

        static {
            constructor = 1830677896;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_peerNotifyEventsEmpty extends PeerNotifyEvents {
        public static int constructor;

        static {
            constructor = -1378534221;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_peerNotifySettings extends PeerNotifySettings {
        public static int constructor;
        public boolean show_previews;

        static {
            constructor = -1697798976;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.show_previews = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) == 0) {
                z2 = false;
            }
            this.silent = z2;
            this.mute_until = stream.readInt32(exception);
            this.sound = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.show_previews ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.silent ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.mute_until);
            stream.writeString(this.sound);
        }
    }

    public static class TL_peerNotifySettingsEmpty extends PeerNotifySettings {
        public static int constructor;

        static {
            constructor = 1889961234;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_peerUser extends Peer {
        public static int constructor;

        static {
            constructor = -1649296275;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_photo extends Photo {
        public static int constructor;

        static {
            constructor = -840088834;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    PhotoSize object = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.sizes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(481674261);
            int count = this.sizes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((PhotoSize) this.sizes.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_photoCachedSize extends PhotoSize {
        public static int constructor;

        static {
            constructor = -374917894;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.type = stream.readString(exception);
            this.location = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.bytes = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.type);
            this.location.serializeToStream(stream);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeByteArray(this.bytes);
        }
    }

    public static class TL_photoEmpty extends Photo {
        public static int constructor;

        static {
            constructor = 590459437;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
        }
    }

    public static class TL_photoSize extends PhotoSize {
        public static int constructor;

        static {
            constructor = 2009052699;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.type = stream.readString(exception);
            this.location = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.type);
            this.location.serializeToStream(stream);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeInt32(this.size);
        }
    }

    public static class TL_photoSizeEmpty extends PhotoSize {
        public static int constructor;

        static {
            constructor = 236446268;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int startReadPosiition = stream.getPosition();
            try {
                this.type = stream.readString(true);
                if (this.type.length() > TLRPC.USER_FLAG_ACCESS_HASH || !(this.type.equals(TtmlNode.ANONYMOUS_REGION_ID) || this.type.equals("s") || this.type.equals("x") || this.type.equals("m") || this.type.equals("y") || this.type.equals("w"))) {
                    this.type = "s";
                    if (stream instanceof NativeByteBuffer) {
                        ((NativeByteBuffer) stream).position(startReadPosiition);
                    }
                }
            } catch (Exception e) {
                this.type = "s";
                if (stream instanceof NativeByteBuffer) {
                    ((NativeByteBuffer) stream).position(startReadPosiition);
                }
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.type);
        }
    }

    public static class TL_photos_photos extends photos_Photos {
        public static int constructor;

        static {
            constructor = -1916114267;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Photo object = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.photos.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.photos.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Photo) this.photos.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_photos_photosSlice extends photos_Photos {
        public static int constructor;

        static {
            constructor = 352657236;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Photo object = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.photos.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(this.count);
            stream.writeInt32(481674261);
            int count = this.photos.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Photo) this.photos.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_privacyKeyChatInvite extends PrivacyKey {
        public static int constructor;

        static {
            constructor = 1343122938;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_privacyKeyStatusTimestamp extends PrivacyKey {
        public static int constructor;

        static {
            constructor = -1137792208;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_privacyValueAllowAll extends PrivacyRule {
        public static int constructor;

        static {
            constructor = 1698855810;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_privacyValueAllowContacts extends PrivacyRule {
        public static int constructor;

        static {
            constructor = -123988;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_privacyValueAllowUsers extends PrivacyRule {
        public static int constructor;

        static {
            constructor = 1297858060;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.users.add(Integer.valueOf(stream.readInt32(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.users.get(a)).intValue());
            }
        }
    }

    public static class TL_privacyValueDisallowAll extends PrivacyRule {
        public static int constructor;

        static {
            constructor = -1955338397;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_privacyValueDisallowContacts extends PrivacyRule {
        public static int constructor;

        static {
            constructor = -125240806;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_privacyValueDisallowUsers extends PrivacyRule {
        public static int constructor;

        static {
            constructor = 209668535;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.users.add(Integer.valueOf(stream.readInt32(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.users.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.users.get(a)).intValue());
            }
        }
    }

    public static class TL_replyInlineMarkup extends ReplyMarkup {
        public static int constructor;

        static {
            constructor = 1218642516;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_keyboardButtonRow object = TL_keyboardButtonRow.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.rows.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.rows.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_keyboardButtonRow) this.rows.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_replyKeyboardForceReply extends ReplyMarkup {
        public static int constructor;

        static {
            constructor = -200242528;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.single_use = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) == 0) {
                z2 = false;
            }
            this.selective = z2;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.single_use ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.selective ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            stream.writeInt32(this.flags);
        }
    }

    public static class TL_replyKeyboardHide extends ReplyMarkup {
        public static int constructor;

        static {
            constructor = -1606526075;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.selective = (this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.selective ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            stream.writeInt32(this.flags);
        }
    }

    public static class TL_replyKeyboardMarkup extends ReplyMarkup {
        public static int constructor;

        static {
            constructor = 889353612;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.resize = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.single_use = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.selective = z;
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_keyboardButtonRow object = TL_keyboardButtonRow.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.rows.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.resize ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.single_use ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.selective ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            stream.writeInt32(this.flags);
            stream.writeInt32(481674261);
            int count = this.rows.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_keyboardButtonRow) this.rows.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_sendMessageCancelAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -44119819;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageChooseContactAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = 1653390447;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageGeoLocationAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = 393186209;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageRecordAudioAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -718310409;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageRecordVideoAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -1584933265;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageTypingAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = 381645902;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageUploadAudioAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -212740181;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.progress = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.progress);
        }
    }

    public static class TL_sendMessageUploadDocumentAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -1441998364;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.progress = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.progress);
        }
    }

    public static class TL_sendMessageUploadPhotoAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -774682074;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.progress = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.progress);
        }
    }

    public static class TL_sendMessageUploadVideoAction extends SendMessageAction {
        public static int constructor;

        static {
            constructor = -378127636;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.progress = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.progress);
        }
    }

    public static class TL_stickerSet extends StickerSet {
        public static int constructor;

        static {
            constructor = -852477119;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.installed = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.disabled = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) == 0) {
                z2 = false;
            }
            this.official = z2;
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
            this.short_name = stream.readString(exception);
            this.count = stream.readInt32(exception);
            this.hash = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.installed ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.disabled ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.official ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            stream.writeInt32(this.flags);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
            stream.writeString(this.short_name);
            stream.writeInt32(this.count);
            stream.writeInt32(this.hash);
        }
    }

    public static class TL_storage_fileGif extends storage_FileType {
        public static int constructor;

        static {
            constructor = -891180321;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_fileJpeg extends storage_FileType {
        public static int constructor;

        static {
            constructor = 8322574;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_fileMov extends storage_FileType {
        public static int constructor;

        static {
            constructor = 1258941372;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_fileMp3 extends storage_FileType {
        public static int constructor;

        static {
            constructor = 1384777335;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_fileMp4 extends storage_FileType {
        public static int constructor;

        static {
            constructor = -1278304028;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_filePartial extends storage_FileType {
        public static int constructor;

        static {
            constructor = 1086091090;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_filePdf extends storage_FileType {
        public static int constructor;

        static {
            constructor = -1373745011;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_filePng extends storage_FileType {
        public static int constructor;

        static {
            constructor = 172975040;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_fileUnknown extends storage_FileType {
        public static int constructor;

        static {
            constructor = -1432995067;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_storage_fileWebp extends storage_FileType {
        public static int constructor;

        static {
            constructor = 276907596;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_topPeerCategoryBotsInline extends TopPeerCategory {
        public static int constructor;

        static {
            constructor = 344356834;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_topPeerCategoryBotsPM extends TopPeerCategory {
        public static int constructor;

        static {
            constructor = -1419371685;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_topPeerCategoryChannels extends TopPeerCategory {
        public static int constructor;

        static {
            constructor = 371037736;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_topPeerCategoryCorrespondents extends TopPeerCategory {
        public static int constructor;

        static {
            constructor = 104314861;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_topPeerCategoryGroups extends TopPeerCategory {
        public static int constructor;

        static {
            constructor = -1122524854;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_updateBotCallbackQuery extends Update {
        public static int constructor;
        public int msg_id;
        public Peer peer;

        static {
            constructor = -1500747636;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.query_id = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.msg_id = stream.readInt32(exception);
            this.data = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.query_id);
            stream.writeInt32(this.user_id);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.msg_id);
            stream.writeByteArray(this.data);
        }
    }

    public static class TL_updateBotInlineQuery extends Update {
        public static int constructor;

        static {
            constructor = 1417832080;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.query_id = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.query = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.offset = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt64(this.query_id);
            stream.writeInt32(this.user_id);
            stream.writeString(this.query);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.geo.serializeToStream(stream);
            }
            stream.writeString(this.offset);
        }
    }

    public static class TL_updateBotInlineSend extends Update {
        public static int constructor;
        public String id;
        public TL_inputBotInlineMessageID msg_id;

        static {
            constructor = 239663460;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
            this.query = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.id = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.msg_id = TL_inputBotInlineMessageID.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt32(this.user_id);
            stream.writeString(this.query);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.geo.serializeToStream(stream);
            }
            stream.writeString(this.id);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.msg_id.serializeToStream(stream);
            }
        }
    }

    public static class TL_updateChannel extends Update {
        public static int constructor;

        static {
            constructor = -1227598250;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
        }
    }

    public static class TL_updateChannelMessageViews extends Update {
        public static int constructor;
        public int id;

        static {
            constructor = -1734268085;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            this.id = stream.readInt32(exception);
            this.views = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt32(this.id);
            stream.writeInt32(this.views);
        }
    }

    public static class TL_updateChannelPinnedMessage extends Update {
        public static int constructor;
        public int id;

        static {
            constructor = -1738988427;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            this.id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_updateChannelTooLong extends Update {
        public static int constructor;

        static {
            constructor = -352032773;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.channel_id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.pts = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt32(this.channel_id);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.pts);
            }
        }
    }

    public static class TL_updateChatAdmins extends Update {
        public static int constructor;

        static {
            constructor = 1855224129;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.enabled = stream.readBool(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeBool(this.enabled);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_updateChatParticipantAdd extends Update {
        public static int constructor;

        static {
            constructor = -364179876;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
            this.inviter_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.inviter_id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_updateChatParticipantAdmin extends Update {
        public static int constructor;

        static {
            constructor = -1232070311;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
            this.is_admin = stream.readBool(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.user_id);
            stream.writeBool(this.is_admin);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_updateChatParticipantDelete extends Update {
        public static int constructor;

        static {
            constructor = 1851755554;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_updateChatParticipants extends Update {
        public static int constructor;

        static {
            constructor = 125178264;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.participants = ChatParticipants.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.participants.serializeToStream(stream);
        }
    }

    public static class TL_updateChatUserTyping extends Update {
        public static int constructor;

        static {
            constructor = -1704596961;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
            this.action = SendMessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.user_id);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_updateContactLink extends Update {
        public static int constructor;

        static {
            constructor = -1657903163;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.my_link = ContactLink.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.foreign_link = ContactLink.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            this.my_link.serializeToStream(stream);
            this.foreign_link.serializeToStream(stream);
        }
    }

    public static class TL_updateContactRegistered extends Update {
        public static int constructor;

        static {
            constructor = 628472761;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_updateDcOptions extends Update {
        public static int constructor;

        static {
            constructor = -1906403213;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_dcOption object = TL_dcOption.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.dc_options.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.dc_options.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_dcOption) this.dc_options.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_updateDeleteChannelMessages extends Update {
        public static int constructor;

        static {
            constructor = -1015733815;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.messages.add(Integer.valueOf(stream.readInt32(exception)));
                }
                this.pts = stream.readInt32(exception);
                this.pts_count = stream.readInt32(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.messages.get(a)).intValue());
            }
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateDeleteMessages extends Update {
        public static int constructor;

        static {
            constructor = -1576161051;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.messages.add(Integer.valueOf(stream.readInt32(exception)));
                }
                this.pts = stream.readInt32(exception);
                this.pts_count = stream.readInt32(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.messages.get(a)).intValue());
            }
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateDraftMessage extends Update {
        public static int constructor;
        public Peer peer;

        static {
            constructor = -299124375;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.draft = DraftMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.draft.serializeToStream(stream);
        }
    }

    public static class TL_updateEditChannelMessage extends Update {
        public static int constructor;
        public Message message;

        static {
            constructor = 457133559;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateEditMessage extends Update {
        public static int constructor;
        public Message message;

        static {
            constructor = -469536605;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateEncryptedChatTyping extends Update {
        public static int constructor;

        static {
            constructor = 386986326;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_updateEncryptedMessagesRead extends Update {
        public static int constructor;

        static {
            constructor = 956179895;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.max_date = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.max_date);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_updateEncryption extends Update {
        public static int constructor;

        static {
            constructor = -1264392051;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat = EncryptedChat.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.chat.serializeToStream(stream);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_updateInlineBotCallbackQuery extends Update {
        public static int constructor;
        public TL_inputBotInlineMessageID msg_id;

        static {
            constructor = 750622127;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.query_id = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.msg_id = TL_inputBotInlineMessageID.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.data = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.query_id);
            stream.writeInt32(this.user_id);
            this.msg_id.serializeToStream(stream);
            stream.writeByteArray(this.data);
        }
    }

    public static class TL_updateMessageID extends Update {
        public static int constructor;
        public int id;

        static {
            constructor = 1318109142;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.random_id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.random_id);
        }
    }

    public static class TL_updateNewAuthorization extends Update {
        public static int constructor;

        static {
            constructor = -1895411046;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.auth_key_id = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.device = stream.readString(exception);
            this.location = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.auth_key_id);
            stream.writeInt32(this.date);
            stream.writeString(this.device);
            stream.writeString(this.location);
        }
    }

    public static class TL_updateNewChannelMessage extends Update {
        public static int constructor;
        public Message message;

        static {
            constructor = 1656358105;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateNewEncryptedMessage extends Update {
        public static int constructor;
        public EncryptedMessage message;

        static {
            constructor = 314359194;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = EncryptedMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.qts = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
            stream.writeInt32(this.qts);
        }
    }

    public static class TL_updateNewGeoChatMessage extends Update {
        public static int constructor;
        public GeoChatMessage message;

        static {
            constructor = 1516823543;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = GeoChatMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
        }
    }

    public static class TL_updateNewMessage extends Update {
        public static int constructor;
        public Message message;

        static {
            constructor = 522914557;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.message = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.message.serializeToStream(stream);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateNewStickerSet extends Update {
        public static int constructor;

        static {
            constructor = 1753886890;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.stickerset = TL_messages_stickerSet.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.stickerset.serializeToStream(stream);
        }
    }

    public static class TL_updateNotifySettings extends Update {
        public static int constructor;
        public NotifyPeer peer;

        static {
            constructor = -1094555409;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = NotifyPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            this.notify_settings.serializeToStream(stream);
        }
    }

    public static class TL_updatePrivacy extends Update {
        public static int constructor;

        static {
            constructor = -298113238;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.key = PrivacyKey.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    PrivacyRule object = PrivacyRule.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.rules.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.key.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.rules.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((PrivacyRule) this.rules.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_updateReadChannelInbox extends Update {
        public static int constructor;

        static {
            constructor = 1108669311;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            this.max_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_updateReadChannelOutbox extends Update {
        public static int constructor;

        static {
            constructor = 634833351;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channel_id = stream.readInt32(exception);
            this.max_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.channel_id);
            stream.writeInt32(this.max_id);
        }
    }

    public static class TL_updateReadHistoryInbox extends Update {
        public static int constructor;
        public Peer peer;

        static {
            constructor = -1721631396;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.max_id = stream.readInt32(exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateReadHistoryOutbox extends Update {
        public static int constructor;
        public Peer peer;

        static {
            constructor = 791617983;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.max_id = stream.readInt32(exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateReadMessagesContents extends Update {
        public static int constructor;

        static {
            constructor = 1757493555;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.messages.add(Integer.valueOf(stream.readInt32(exception)));
                }
                this.pts = stream.readInt32(exception);
                this.pts_count = stream.readInt32(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt32(((Integer) this.messages.get(a)).intValue());
            }
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updateSavedGifs extends Update {
        public static int constructor;

        static {
            constructor = -1821035490;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_updateServiceNotification extends Update {
        public static int constructor;
        public String message;

        static {
            constructor = 942527460;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.type = stream.readString(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.popup = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.type);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            stream.writeBool(this.popup);
        }
    }

    public static class TL_updateShort extends Updates {
        public static int constructor;

        static {
            constructor = 2027216577;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.update = Update.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
        }
    }

    public static class TL_updateShortChatMessage extends Updates {
        public static int constructor;

        static {
            constructor = 377562760;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.silent = z;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.chat_id = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = TL_messageFwdHeader.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                this.via_bot_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            }
        }
    }

    public static class TL_updateShortMessage extends Updates {
        public static int constructor;

        static {
            constructor = -1857044719;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.silent = z;
            this.id = stream.readInt32(exception);
            this.user_id = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = TL_messageFwdHeader.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                this.via_bot_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            }
        }
    }

    public static class TL_updateShortSentMessage extends Updates {
        public static int constructor;

        static {
            constructor = 301019932;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            this.id = stream.readInt32(exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            }
        }
    }

    public static class TL_updateStickerSets extends Update {
        public static int constructor;

        static {
            constructor = 1135492588;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_updateStickerSetsOrder extends Update {
        public static int constructor;

        static {
            constructor = -253774767;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    this.order.add(Long.valueOf(stream.readInt64(exception)));
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.order.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                stream.writeInt64(((Long) this.order.get(a)).longValue());
            }
        }
    }

    public static class TL_updateUserBlocked extends Update {
        public static int constructor;

        static {
            constructor = -2131957734;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.blocked = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeBool(this.blocked);
        }
    }

    public static class TL_updateUserName extends Update {
        public static int constructor;

        static {
            constructor = -1489818765;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
        }
    }

    public static class TL_updateUserPhone extends Update {
        public static int constructor;

        static {
            constructor = 314130811;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.phone = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeString(this.phone);
        }
    }

    public static class TL_updateUserPhoto extends Update {
        public static int constructor;

        static {
            constructor = -1791935732;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.previous = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            this.photo.serializeToStream(stream);
            stream.writeBool(this.previous);
        }
    }

    public static class TL_updateUserStatus extends Update {
        public static int constructor;

        static {
            constructor = 469489699;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_updateUserTyping extends Update {
        public static int constructor;

        static {
            constructor = 1548249383;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.action = SendMessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_updateWebPage extends Update {
        public static int constructor;

        static {
            constructor = 2139689491;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.webpage = WebPage.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.pts = stream.readInt32(exception);
            this.pts_count = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.webpage.serializeToStream(stream);
            stream.writeInt32(this.pts);
            stream.writeInt32(this.pts_count);
        }
    }

    public static class TL_updates extends Updates {
        public static int constructor;

        static {
            constructor = 1957577280;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Update object = Update.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.updates.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        this.date = stream.readInt32(exception);
                        this.seq = stream.readInt32(exception);
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }
    }

    public static class TL_updatesCombined extends Updates {
        public static int constructor;

        static {
            constructor = 1918567619;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Update object = Update.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.updates.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        User object2 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.users.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        this.date = stream.readInt32(exception);
                        this.seq_start = stream.readInt32(exception);
                        this.seq = stream.readInt32(exception);
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }
    }

    public static class TL_updatesTooLong extends Updates {
        public static int constructor;

        static {
            constructor = -484987010;
        }
    }

    public static class TL_updates_channelDifference extends updates_ChannelDifference {
        public static int constructor;

        static {
            constructor = 543450958;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.isFinal = z;
            this.pts = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.timeout = stream.readInt32(exception);
            }
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.new_messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Update object2 = Update.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.other_updates.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Chat object3 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.chats.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                User object4 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.users.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            int a;
            stream.writeInt32(constructor);
            if (this.isFinal) {
                i = this.flags | TLRPC.USER_FLAG_ACCESS_HASH;
            } else {
                i = this.flags & -2;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.pts);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.timeout);
            }
            stream.writeInt32(481674261);
            int count = this.new_messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.new_messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.other_updates.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Update) this.other_updates.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_updates_channelDifferenceEmpty extends updates_ChannelDifference {
        public static int constructor;

        static {
            constructor = 1041346555;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.isFinal = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            this.pts = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.timeout = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.isFinal ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.pts);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.timeout);
            }
        }
    }

    public static class TL_updates_channelDifferenceTooLong extends updates_ChannelDifference {
        public static int constructor;

        static {
            constructor = 1091431943;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.isFinal = z;
            this.pts = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.timeout = stream.readInt32(exception);
            }
            this.top_message = stream.readInt32(exception);
            this.read_inbox_max_id = stream.readInt32(exception);
            this.read_outbox_max_id = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        Chat object2 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.chats.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            User object3 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.users.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            int a;
            stream.writeInt32(constructor);
            if (this.isFinal) {
                i = this.flags | TLRPC.USER_FLAG_ACCESS_HASH;
            } else {
                i = this.flags & -2;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.pts);
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.timeout);
            }
            stream.writeInt32(this.top_message);
            stream.writeInt32(this.read_inbox_max_id);
            stream.writeInt32(this.read_outbox_max_id);
            stream.writeInt32(this.unread_count);
            stream.writeInt32(481674261);
            int count = this.messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_updates_difference extends updates_Difference {
        public static int constructor;

        static {
            constructor = 16030880;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.new_messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        EncryptedMessage object2 = EncryptedMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.new_encrypted_messages.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Update object3 = Update.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.other_updates.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                Chat object4 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.chats.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                            magic = stream.readInt32(exception);
                            if (magic == 481674261) {
                                count = stream.readInt32(exception);
                                a = 0;
                                while (a < count) {
                                    User object5 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                    if (object5 != null) {
                                        this.users.add(object5);
                                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                                    } else {
                                        return;
                                    }
                                }
                                this.state = TL_updates_state.TLdeserialize(stream, stream.readInt32(exception), exception);
                            } else if (exception) {
                                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                                objArr[0] = Integer.valueOf(magic);
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                            }
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.new_messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.new_messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.new_encrypted_messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((EncryptedMessage) this.new_encrypted_messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.other_updates.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Update) this.other_updates.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
            this.state.serializeToStream(stream);
        }
    }

    public static class TL_updates_differenceEmpty extends updates_Difference {
        public static int constructor;

        static {
            constructor = 1567990072;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.date = stream.readInt32(exception);
            this.seq = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.date);
            stream.writeInt32(this.seq);
        }
    }

    public static class TL_updates_differenceSlice extends updates_Difference {
        public static int constructor;

        static {
            constructor = -1459938943;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            Object[] objArr;
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    Message object = Message.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.new_messages.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        EncryptedMessage object2 = EncryptedMessage.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            this.new_encrypted_messages.add(object2);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                    magic = stream.readInt32(exception);
                    if (magic == 481674261) {
                        count = stream.readInt32(exception);
                        a = 0;
                        while (a < count) {
                            Update object3 = Update.TLdeserialize(stream, stream.readInt32(exception), exception);
                            if (object3 != null) {
                                this.other_updates.add(object3);
                                a += TLRPC.USER_FLAG_ACCESS_HASH;
                            } else {
                                return;
                            }
                        }
                        magic = stream.readInt32(exception);
                        if (magic == 481674261) {
                            count = stream.readInt32(exception);
                            a = 0;
                            while (a < count) {
                                Chat object4 = Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                                if (object4 != null) {
                                    this.chats.add(object4);
                                    a += TLRPC.USER_FLAG_ACCESS_HASH;
                                } else {
                                    return;
                                }
                            }
                            magic = stream.readInt32(exception);
                            if (magic == 481674261) {
                                count = stream.readInt32(exception);
                                a = 0;
                                while (a < count) {
                                    User object5 = User.TLdeserialize(stream, stream.readInt32(exception), exception);
                                    if (object5 != null) {
                                        this.users.add(object5);
                                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                                    } else {
                                        return;
                                    }
                                }
                                this.intermediate_state = TL_updates_state.TLdeserialize(stream, stream.readInt32(exception), exception);
                            } else if (exception) {
                                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                                objArr[0] = Integer.valueOf(magic);
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                            }
                        } else if (exception) {
                            objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                            objArr[0] = Integer.valueOf(magic);
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                        }
                    } else if (exception) {
                        objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                        objArr[0] = Integer.valueOf(magic);
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                    }
                } else if (exception) {
                    objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                }
            } else if (exception) {
                objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int a;
            stream.writeInt32(constructor);
            stream.writeInt32(481674261);
            int count = this.new_messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Message) this.new_messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.new_encrypted_messages.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((EncryptedMessage) this.new_encrypted_messages.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.other_updates.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Update) this.other_updates.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.chats.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((Chat) this.chats.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(481674261);
            count = this.users.size();
            stream.writeInt32(count);
            for (a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((User) this.users.get(a)).serializeToStream(stream);
            }
            this.intermediate_state.serializeToStream(stream);
        }
    }

    public static class TL_user extends User {
        public static int constructor;

        static {
            constructor = -787638374;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.self = (this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0;
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.contact = z;
            if ((this.flags & MessagesController.UPDATE_MASK_SEND_STATE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mutual_contact = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.deleted = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.bot = z;
            if ((this.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.bot_chat_history = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_CUT) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.bot_nochats = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_SET_SELECTION) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.verified = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_EXPAND) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.restricted = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_DISMISS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.min = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_SET_TEXT) == 0) {
                z2 = false;
            }
            this.bot_inline_geo = z2;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.access_hash = stream.readInt64(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.first_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.last_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.username = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.phone = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                this.bot_info_version = stream.readInt32(exception);
            }
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_EXPAND) != 0) {
                this.restriction_reason = stream.readString(exception);
            }
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_COLLAPSE) != 0) {
                this.bot_inline_placeholder = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.self ? this.flags | TLRPC.MESSAGE_FLAG_HAS_VIEWS : this.flags & -1025;
            this.flags = this.contact ? this.flags | TLRPC.MESSAGE_FLAG_HAS_BOT_ID : this.flags & -2049;
            this.flags = this.mutual_contact ? this.flags | MessagesController.UPDATE_MASK_SEND_STATE : this.flags & -4097;
            this.flags = this.deleted ? this.flags | MessagesController.UPDATE_MASK_CHANNEL : this.flags & -8193;
            this.flags = this.bot ? this.flags | MessagesController.UPDATE_MASK_CHAT_ADMINS : this.flags & -16385;
            this.flags = this.bot_chat_history ? this.flags | TLRPC.MESSAGE_FLAG_EDITED : this.flags & -32769;
            this.flags = this.bot_nochats ? this.flags | AccessibilityNodeInfoCompat.ACTION_CUT : this.flags & -65537;
            this.flags = this.verified ? this.flags | AccessibilityNodeInfoCompat.ACTION_SET_SELECTION : this.flags & -131073;
            this.flags = this.restricted ? this.flags | AccessibilityNodeInfoCompat.ACTION_EXPAND : this.flags & -262145;
            this.flags = this.min ? this.flags | AccessibilityNodeInfoCompat.ACTION_DISMISS : this.flags & -1048577;
            this.flags = this.bot_inline_geo ? this.flags | AccessibilityNodeInfoCompat.ACTION_SET_TEXT : this.flags & -2097153;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt64(this.access_hash);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.first_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.last_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeString(this.username);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeString(this.phone);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.photo.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.status.serializeToStream(stream);
            }
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                stream.writeInt32(this.bot_info_version);
            }
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_EXPAND) != 0) {
                stream.writeString(this.restriction_reason);
            }
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_COLLAPSE) != 0) {
                stream.writeString(this.bot_inline_placeholder);
            }
        }
    }

    public static class TL_userContact_old2 extends User {
        public static int constructor;

        static {
            constructor = -894214632;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
            this.access_hash = stream.readInt64(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userDeleted_old2 extends User {
        public static int constructor;

        static {
            constructor = -704549510;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
        }
    }

    public static class TL_userEmpty extends User {
        public static int constructor;

        static {
            constructor = 537022650;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
        }
    }

    public static class TL_userForeign_old2 extends User {
        public static int constructor;

        static {
            constructor = 123533224;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
            this.access_hash = stream.readInt64(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
            stream.writeInt64(this.access_hash);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userProfilePhoto extends UserProfilePhoto {
        public static int constructor;

        static {
            constructor = -715532088;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo_id = stream.readInt64(exception);
            this.photo_small = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.photo_big = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.photo_id);
            this.photo_small.serializeToStream(stream);
            this.photo_big.serializeToStream(stream);
        }
    }

    public static class TL_userProfilePhotoEmpty extends UserProfilePhoto {
        public static int constructor;

        static {
            constructor = 1326562017;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_userRequest_old2 extends User {
        public static int constructor;

        static {
            constructor = -640891665;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
            this.access_hash = stream.readInt64(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userSelf_old3 extends User {
        public static int constructor;

        static {
            constructor = 476112392;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userStatusEmpty extends UserStatus {
        public static int constructor;

        static {
            constructor = 164646985;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_userStatusLastMonth extends UserStatus {
        public static int constructor;

        static {
            constructor = 2011940674;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_userStatusLastWeek extends UserStatus {
        public static int constructor;

        static {
            constructor = 129960444;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_userStatusOffline extends UserStatus {
        public static int constructor;

        static {
            constructor = 9203775;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.expires = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.expires);
        }
    }

    public static class TL_userStatusOnline extends UserStatus {
        public static int constructor;

        static {
            constructor = -306628279;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.expires = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.expires);
        }
    }

    public static class TL_userStatusRecently extends UserStatus {
        public static int constructor;

        static {
            constructor = -496024847;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_videoEmpty_layer45 extends Video {
        public static int constructor;

        static {
            constructor = -1056548696;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
        }
    }

    public static class TL_video_layer45 extends Video {
        public static int constructor;

        static {
            constructor = -148338733;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_wallPaper extends WallPaper {
        public static int constructor;

        static {
            constructor = -860866985;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    PhotoSize object = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.sizes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.color = stream.readInt32(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
            stream.writeInt32(481674261);
            int count = this.sizes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((PhotoSize) this.sizes.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(this.color);
        }
    }

    public static class TL_wallPaperSolid extends WallPaper {
        public static int constructor;

        static {
            constructor = 1662091044;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.bg_color = stream.readInt32(exception);
            this.color = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
            stream.writeInt32(this.bg_color);
            stream.writeInt32(this.color);
        }
    }

    public static class TL_webPage extends WebPage {
        public static int constructor;

        static {
            constructor = -897446185;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.id = stream.readInt64(exception);
            this.url = stream.readString(exception);
            this.display_url = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.type = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.site_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.title = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.description = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.embed_url = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.embed_type = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.embed_width = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.embed_height = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                this.duration = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.author = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.document = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt64(this.id);
            stream.writeString(this.url);
            stream.writeString(this.display_url);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.type);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.site_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.title);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeString(this.description);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.photo.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeString(this.embed_url);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeString(this.embed_type);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeInt32(this.embed_width);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeInt32(this.embed_height);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(this.duration);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeString(this.author);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.document.serializeToStream(stream);
            }
        }
    }

    public static class TL_webPageEmpty extends WebPage {
        public static int constructor;

        static {
            constructor = -350980120;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
        }
    }

    public static class TL_webPagePending extends WebPage {
        public static int constructor;

        static {
            constructor = -981018084;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_webPageUrlPending extends WebPage {
        public static int constructor;

        static {
            constructor = -736472729;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.url);
        }
    }

    public static class TL_webPage_old extends WebPage {
        public static int constructor;

        static {
            constructor = -1558273867;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.id = stream.readInt64(exception);
            this.url = stream.readString(exception);
            this.display_url = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.type = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.site_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.title = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.description = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.embed_url = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.embed_type = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.embed_width = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.embed_height = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                this.duration = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.author = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            stream.writeInt64(this.id);
            stream.writeString(this.url);
            stream.writeString(this.display_url);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeString(this.type);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.site_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.title);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeString(this.description);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.photo.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeString(this.embed_url);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeString(this.embed_type);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeInt32(this.embed_width);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeInt32(this.embed_height);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(this.duration);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeString(this.author);
            }
        }
    }

    public static class TL_audioEncrypted extends TL_audio_layer45 {
        public static int constructor;

        static {
            constructor = 1431655926;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.dc_id = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.size);
            stream.writeInt32(this.dc_id);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_audio_old2 extends TL_audio_layer45 {
        public static int constructor;

        static {
            constructor = -945003370;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.dc_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            stream.writeInt32(this.dc_id);
        }
    }

    public static class TL_audio_old extends TL_audio_layer45 {
        public static int constructor;

        static {
            constructor = 1114908135;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.dc_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.size);
            stream.writeInt32(this.dc_id);
        }
    }

    public static class TL_botInfoEmpty_layer48 extends TL_botInfo {
        public static int constructor;

        static {
            constructor = -1154598962;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_botInfo_layer48 extends TL_botInfo {
        public static int constructor;

        static {
            constructor = 164583517;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
            stream.readString(exception);
            this.description = stream.readString(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_botCommand object = TL_botCommand.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.commands.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.version);
            stream.writeString(TtmlNode.ANONYMOUS_REGION_ID);
            stream.writeString(this.description);
            stream.writeInt32(481674261);
            int count = this.commands.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((TL_botCommand) this.commands.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_channelFull_layer48 extends TL_channelFull {
        public static int constructor;

        static {
            constructor = -1640751649;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.can_view_participants = z;
            this.id = stream.readInt32(exception);
            this.about = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.participants_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.admins_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.kicked_count = stream.readInt32(exception);
            }
            this.read_inbox_max_id = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
            stream.readInt32(exception);
            this.chat_photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.exported_invite = ExportedChatInvite.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    BotInfo object = BotInfo.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.bot_info.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                    this.migrated_from_chat_id = stream.readInt32(exception);
                }
                if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                    this.migrated_from_max_id = stream.readInt32(exception);
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            if (this.can_view_participants) {
                i = this.flags | TLRPC.USER_FLAG_USERNAME;
            } else {
                i = this.flags & -9;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.about);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.participants_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.admins_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.kicked_count);
            }
            stream.writeInt32(this.read_inbox_max_id);
            stream.writeInt32(this.unread_count);
            stream.writeInt32(0);
            this.chat_photo.serializeToStream(stream);
            this.notify_settings.serializeToStream(stream);
            this.exported_invite.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.bot_info.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((BotInfo) this.bot_info.get(a)).serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeInt32(this.migrated_from_chat_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeInt32(this.migrated_from_max_id);
            }
        }
    }

    public static class TL_channelFull_layer52 extends TL_channelFull {
        public static int constructor;

        static {
            constructor = -1749097118;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.can_view_participants = z;
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.can_set_username = z;
            this.id = stream.readInt32(exception);
            this.about = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.participants_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.admins_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.kicked_count = stream.readInt32(exception);
            }
            this.read_inbox_max_id = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
            stream.readInt32(exception);
            this.chat_photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.exported_invite = ExportedChatInvite.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    BotInfo object = BotInfo.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.bot_info.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                    this.migrated_from_chat_id = stream.readInt32(exception);
                }
                if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                    this.migrated_from_max_id = stream.readInt32(exception);
                }
                if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                    this.pinned_msg_id = stream.readInt32(exception);
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.can_view_participants ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            if (this.can_set_username) {
                i = this.flags | TLRPC.USER_FLAG_STATUS;
            } else {
                i = this.flags & -65;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.about);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.participants_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.admins_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.kicked_count);
            }
            stream.writeInt32(this.read_inbox_max_id);
            stream.writeInt32(this.unread_count);
            stream.writeInt32(0);
            this.chat_photo.serializeToStream(stream);
            this.notify_settings.serializeToStream(stream);
            this.exported_invite.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.bot_info.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((BotInfo) this.bot_info.get(a)).serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeInt32(this.migrated_from_chat_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeInt32(this.migrated_from_max_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                stream.writeInt32(this.pinned_msg_id);
            }
        }
    }

    public static class TL_channelFull_old extends TL_channelFull {
        public static int constructor;

        static {
            constructor = -88925533;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.can_view_participants = (this.flags & TLRPC.USER_FLAG_USERNAME) != 0;
            this.id = stream.readInt32(exception);
            this.about = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.participants_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.admins_count = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.kicked_count = stream.readInt32(exception);
            }
            this.read_inbox_max_id = stream.readInt32(exception);
            this.unread_count = stream.readInt32(exception);
            stream.readInt32(exception);
            this.chat_photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.notify_settings = PeerNotifySettings.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.exported_invite = ExportedChatInvite.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.can_view_participants ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.about);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt32(this.participants_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeInt32(this.admins_count);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.kicked_count);
            }
            stream.writeInt32(this.read_inbox_max_id);
            stream.writeInt32(this.unread_count);
            stream.writeInt32(0);
            this.chat_photo.serializeToStream(stream);
            this.notify_settings.serializeToStream(stream);
            this.exported_invite.serializeToStream(stream);
        }
    }

    public static class TL_channel_layer48 extends TL_channel {
        public static int constructor;

        static {
            constructor = 1260090630;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.creator = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.kicked = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.left = z;
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.editor = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.moderator = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.broadcast = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.verified = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.megagroup = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.restricted = z;
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.democracy = z;
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) == 0) {
                z2 = false;
            }
            this.signatures = z2;
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.username = stream.readString(exception);
            }
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.restriction_reason = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.creator ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.kicked ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.left ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.editor ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            this.flags = this.moderator ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.broadcast ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.verified ? this.flags | TLRPC.USER_FLAG_UNUSED : this.flags & -129;
            this.flags = this.megagroup ? this.flags | TLRPC.USER_FLAG_UNUSED2 : this.flags & -257;
            this.flags = this.restricted ? this.flags | TLRPC.USER_FLAG_UNUSED3 : this.flags & -513;
            this.flags = this.democracy ? this.flags | TLRPC.MESSAGE_FLAG_HAS_VIEWS : this.flags & -1025;
            this.flags = this.signatures ? this.flags | TLRPC.MESSAGE_FLAG_HAS_BOT_ID : this.flags & -2049;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeString(this.username);
            }
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeInt32(this.version);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                stream.writeString(this.restriction_reason);
            }
        }
    }

    public static class TL_channel_old extends TL_channel {
        public static int constructor;

        static {
            constructor = 1737397639;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.creator = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.kicked = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.left = z;
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.editor = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.moderator = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.broadcast = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.verified = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.megagroup = z;
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) == 0) {
                z2 = false;
            }
            this.explicit_content = z2;
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.username = stream.readString(exception);
            }
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.creator ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.kicked ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.left ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.editor ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            this.flags = this.moderator ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.broadcast ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.verified ? this.flags | TLRPC.USER_FLAG_UNUSED : this.flags & -129;
            this.flags = this.megagroup ? this.flags | TLRPC.USER_FLAG_UNUSED2 : this.flags & -257;
            this.flags = this.explicit_content ? this.flags | TLRPC.USER_FLAG_UNUSED3 : this.flags & -513;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                stream.writeString(this.username);
            }
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_chatForbidden_old extends TL_chatForbidden {
        public static int constructor;

        static {
            constructor = -83047359;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.date = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
            stream.writeInt32(this.date);
        }
    }

    public static class TL_chatParticipantsForbidden_old extends TL_chatParticipantsForbidden {
        public static int constructor;

        static {
            constructor = 265468810;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
        }
    }

    public static class TL_chatParticipants_old extends TL_chatParticipants {
        public static int constructor;

        static {
            constructor = 2017571861;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.chat_id = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    ChatParticipant object = ChatParticipant.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.participants.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                this.version = stream.readInt32(exception);
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.chat_id);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(481674261);
            int count = this.participants.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((ChatParticipant) this.participants.get(a)).serializeToStream(stream);
            }
            stream.writeInt32(this.version);
        }
    }

    public static class TL_chat_old2 extends TL_chat {
        public static int constructor;

        static {
            constructor = 1930607688;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.creator = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.kicked = z;
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.left = z;
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.admins_enabled = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.admin = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.deactivated = z2;
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.participants_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.creator ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.kicked ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.left ? this.flags | TLRPC.USER_FLAG_LAST_NAME : this.flags & -5;
            this.flags = this.admins_enabled ? this.flags | TLRPC.USER_FLAG_USERNAME : this.flags & -9;
            this.flags = this.admin ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.deactivated ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.participants_count);
            stream.writeInt32(this.date);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_chat_old extends TL_chat {
        public static int constructor;

        static {
            constructor = 1855757255;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.photo = ChatPhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.participants_count = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.left = stream.readBool(exception);
            this.version = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.title);
            this.photo.serializeToStream(stream);
            stream.writeInt32(this.participants_count);
            stream.writeInt32(this.date);
            stream.writeBool(this.left);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_decryptedMessageMediaAudio_layer8 extends TL_decryptedMessageMediaAudio {
        public static int constructor;

        static {
            constructor = 1619031439;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.duration = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_decryptedMessageMediaDocument_layer8 extends TL_decryptedMessageMediaDocument {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = -1332395189;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.file_name = stream.readString(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeString(this.file_name);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_decryptedMessageMediaPhoto_layer8 extends TL_decryptedMessageMediaPhoto {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = 846826124;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_decryptedMessageMediaVideo_layer17 extends TL_decryptedMessageMediaVideo {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = 1380598109;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_decryptedMessageMediaVideo_layer8 extends TL_decryptedMessageMediaVideo {
        public static int constructor;
        public byte[] thumb;

        static {
            constructor = 1290694387;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.thumb = stream.readByteArray(exception);
            this.thumb_w = stream.readInt32(exception);
            this.thumb_h = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.thumb);
            stream.writeInt32(this.thumb_w);
            stream.writeInt32(this.thumb_h);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeInt32(this.size);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_decryptedMessageService_layer8 extends TL_decryptedMessageService {
        public static int constructor;

        static {
            constructor = -1438109059;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.random_bytes = stream.readByteArray(exception);
            this.action = DecryptedMessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            stream.writeByteArray(this.random_bytes);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_decryptedMessage_layer17 extends TL_decryptedMessage {
        public static int constructor;

        static {
            constructor = 541931640;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.ttl = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = DecryptedMessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            stream.writeInt32(this.ttl);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
        }
    }

    public static class TL_decryptedMessage_layer8 extends TL_decryptedMessage {
        public static int constructor;

        static {
            constructor = 528568095;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.random_id = stream.readInt64(exception);
            this.random_bytes = stream.readByteArray(exception);
            this.message = stream.readString(exception);
            this.media = DecryptedMessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.random_id);
            stream.writeByteArray(this.random_bytes);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
        }
    }

    public static class TL_documentAttributeAudio_layer45 extends TL_documentAttributeAudio {
        public static int constructor;

        static {
            constructor = -556656416;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.duration = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.performer = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.duration);
            stream.writeString(this.title);
            stream.writeString(this.performer);
        }
    }

    public static class TL_documentAttributeAudio_old extends TL_documentAttributeAudio {
        public static int constructor;

        static {
            constructor = 85215461;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.duration = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.duration);
        }
    }

    public static class TL_documentAttributeSticker_old2 extends TL_documentAttributeSticker {
        public static int constructor;

        static {
            constructor = -1723033470;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.alt = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.alt);
        }
    }

    public static class TL_documentAttributeSticker_old extends TL_documentAttributeSticker {
        public static int constructor;

        static {
            constructor = -83208409;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_documentEncrypted_old extends TL_document {
        public static int constructor;

        static {
            constructor = 1431655766;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.file_name = stream.readString(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeString(this.file_name);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_document_old extends TL_document {
        public static int constructor;

        static {
            constructor = -1627626714;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.file_name = stream.readString(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeString(this.file_name);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
        }
    }

    public static class TL_encryptedChatRequested_old extends TL_encryptedChatRequested {
        public static int constructor;

        static {
            constructor = -39213129;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            this.participant_id = stream.readInt32(exception);
            this.g_a = stream.readByteArray(exception);
            this.nonce = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(this.participant_id);
            stream.writeByteArray(this.g_a);
            stream.writeByteArray(this.nonce);
        }
    }

    public static class TL_encryptedChat_old extends TL_encryptedChat {
        public static int constructor;

        static {
            constructor = 1711395151;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.access_hash = stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            this.participant_id = stream.readInt32(exception);
            this.g_a_or_b = stream.readByteArray(exception);
            this.nonce = stream.readByteArray(exception);
            this.key_fingerprint = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(this.participant_id);
            stream.writeByteArray(this.g_a_or_b);
            stream.writeByteArray(this.nonce);
            stream.writeInt64(this.key_fingerprint);
        }
    }

    public static class TL_messageActionChatAddUser_old extends TL_messageActionChatAddUser {
        public static int constructor;

        static {
            constructor = 1581055051;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.user_id = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.user_id);
        }
    }

    public static class TL_messageForwarded_old extends TL_messageForwarded_old2 {
        public static int constructor;

        static {
            constructor = 99903492;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.fwd_from = new TL_messageFwdHeader();
            this.fwd_from.from_id = stream.readInt32(exception);
            TL_messageFwdHeader tL_messageFwdHeader = this.fwd_from;
            tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
            this.fwd_from.date = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.out = stream.readBool(exception);
            this.unread = stream.readBool(exception);
            this.flags |= 772;
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt32(this.fwd_from.from_id);
            stream.writeInt32(this.fwd_from.date);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeBool(this.out);
            stream.writeBool(this.unread);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            if (this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
            stream.writeString(this.attachPath);
        }
    }

    public static class TL_messageMediaDocument_old extends TL_messageMediaDocument {
        public static int constructor;

        static {
            constructor = 802824708;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.document = Document.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.document.serializeToStream(stream);
        }
    }

    public static class TL_messageMediaPhoto_old extends TL_messageMediaPhoto {
        public static int constructor;

        static {
            constructor = -926655958;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo = Photo.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.photo.serializeToStream(stream);
        }
    }

    public static class TL_messageMediaUnsupported_old extends TL_messageMediaUnsupported {
        public static int constructor;

        static {
            constructor = 694364726;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.bytes = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeByteArray(this.bytes);
        }
    }

    public static class TL_messageMediaVideo_old extends TL_messageMediaVideo_layer45 {
        public static int constructor;

        static {
            constructor = -1563278704;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.video_unused = Video.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.video_unused.serializeToStream(stream);
        }
    }

    public static class TL_messageService_layer48 extends TL_messageService {
        public static int constructor;

        static {
            constructor = -1066691065;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.silent = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) == 0) {
                z2 = false;
            }
            this.post = z2;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.from_id = stream.readInt32(exception);
            }
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.from_id == 0) {
                if (this.to_id.user_id != 0) {
                    this.from_id = this.to_id.user_id;
                } else {
                    this.from_id = -this.to_id.channel_id;
                }
            }
            this.date = stream.readInt32(exception);
            this.action = MessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            this.flags = this.silent ? this.flags | MessagesController.UPDATE_MASK_CHANNEL : this.flags & -8193;
            this.flags = this.post ? this.flags | MessagesController.UPDATE_MASK_CHAT_ADMINS : this.flags & -16385;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeInt32(this.from_id);
            }
            this.to_id.serializeToStream(stream);
            stream.writeInt32(this.date);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_messageService_old2 extends TL_messageService {
        public static int constructor;

        static {
            constructor = 495384334;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.media_unread = z2;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.action = MessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.flags |= TLRPC.USER_FLAG_UNUSED2;
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeInt32(this.date);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_messageService_old extends TL_messageService {
        public static int constructor;

        static {
            constructor = -1618124613;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.out = stream.readBool(exception);
            this.unread = stream.readBool(exception);
            this.flags |= TLRPC.USER_FLAG_UNUSED2;
            this.date = stream.readInt32(exception);
            this.action = MessageAction.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeBool(this.out);
            stream.writeBool(this.unread);
            stream.writeInt32(this.date);
            this.action.serializeToStream(stream);
        }
    }

    public static class TL_message_layer47 extends TL_message {
        public static int constructor;

        static {
            constructor = -913120932;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            int a;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.unread = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.from_id = stream.readInt32(exception);
            }
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.from_id == 0) {
                if (this.to_id.user_id != 0) {
                    this.from_id = this.to_id.user_id;
                } else {
                    this.from_id = -this.to_id.channel_id;
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = new TL_messageFwdHeader();
                Peer peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
                TL_messageFwdHeader tL_messageFwdHeader;
                if (peer instanceof TL_peerChannel) {
                    this.fwd_from.channel_id = peer.channel_id;
                    tL_messageFwdHeader = this.fwd_from;
                    tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_FIRST_NAME;
                } else if (peer instanceof TL_peerUser) {
                    this.fwd_from.from_id = peer.user_id;
                    tL_messageFwdHeader = this.fwd_from;
                    tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
                }
                this.fwd_from.date = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                this.via_bot_id = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            } else {
                this.media = new TL_messageMediaEmpty();
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                this.views = stream.readInt32(exception);
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
                if (this.id < 0 && this.attachPath.startsWith("||")) {
                    String[] args = this.attachPath.split("\\|\\|");
                    if (args.length > 0) {
                        this.params = new HashMap();
                        for (a = TLRPC.USER_FLAG_ACCESS_HASH; a < args.length - 1; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                            String[] args2 = args[a].split("\\|=\\|");
                            if (args2.length == TLRPC.USER_FLAG_FIRST_NAME) {
                                this.params.put(args2[0], args2[TLRPC.USER_FLAG_ACCESS_HASH]);
                            }
                        }
                        this.attachPath = args[args.length - 1];
                    }
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            if (this.media_unread) {
                i = this.flags | TLRPC.USER_FLAG_PHOTO;
            } else {
                i = this.flags & -33;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeInt32(this.from_id);
            }
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                if (this.fwd_from.from_id != 0) {
                    TL_peerUser peer = new TL_peerUser();
                    peer.user_id = this.fwd_from.from_id;
                    peer.serializeToStream(stream);
                } else {
                    TL_peerChannel peer2 = new TL_peerChannel();
                    peer2.channel_id = this.fwd_from.channel_id;
                    peer2.serializeToStream(stream);
                }
                stream.writeInt32(this.fwd_from.date);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                stream.writeInt32(this.via_bot_id);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                stream.writeInt32(this.views);
            }
            String path = this.attachPath;
            if (this.id < 0 && this.params != null && this.params.size() > 0) {
                for (Entry<String, String> entry : this.params.entrySet()) {
                    path = ((String) entry.getKey()) + "|=|" + ((String) entry.getValue()) + "||" + path;
                }
                path = "||" + path;
            }
            stream.writeString(path);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_message_old2 extends TL_message {
        public static int constructor;

        static {
            constructor = 1450613171;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = (stream.readInt32(exception) | TLRPC.USER_FLAG_UNUSED2) | TLRPC.USER_FLAG_UNUSED3;
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.media_unread = z2;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            stream.writeString(this.attachPath);
        }
    }

    public static class TL_message_old3 extends TL_message {
        public static int constructor;

        static {
            constructor = -1481959023;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = (stream.readInt32(exception) | TLRPC.USER_FLAG_UNUSED2) | TLRPC.USER_FLAG_UNUSED3;
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.media_unread = z2;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = new TL_messageFwdHeader();
                this.fwd_from.from_id = stream.readInt32(exception);
                TL_messageFwdHeader tL_messageFwdHeader = this.fwd_from;
                tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
                this.fwd_from.date = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.fwd_from.from_id);
                stream.writeInt32(this.fwd_from.date);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            stream.writeString(this.attachPath);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_message_old4 extends TL_message {
        public static int constructor;

        static {
            constructor = -1023016155;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = (stream.readInt32(exception) | TLRPC.USER_FLAG_UNUSED2) | TLRPC.USER_FLAG_UNUSED3;
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.media_unread = z2;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = new TL_messageFwdHeader();
                this.fwd_from.from_id = stream.readInt32(exception);
                TL_messageFwdHeader tL_messageFwdHeader = this.fwd_from;
                tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
                this.fwd_from.date = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.fwd_from.from_id);
                stream.writeInt32(this.fwd_from.date);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            stream.writeString(this.attachPath);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_message_old5 extends TL_message {
        public static int constructor;

        static {
            constructor = -260565816;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = (stream.readInt32(exception) | TLRPC.USER_FLAG_UNUSED2) | TLRPC.USER_FLAG_UNUSED3;
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.unread = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = new TL_messageFwdHeader();
                this.fwd_from.from_id = stream.readInt32(exception);
                TL_messageFwdHeader tL_messageFwdHeader = this.fwd_from;
                tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
                this.fwd_from.date = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            if (this.media_unread) {
                i = this.flags | TLRPC.USER_FLAG_PHOTO;
            } else {
                i = this.flags & -33;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.fwd_from.from_id);
                stream.writeInt32(this.fwd_from.date);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            stream.writeString(this.attachPath);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_message_old6 extends TL_message {
        public static int constructor;

        static {
            constructor = 736885382;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception) | TLRPC.USER_FLAG_UNUSED2;
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.unread = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = new TL_messageFwdHeader();
                this.fwd_from.from_id = stream.readInt32(exception);
                TL_messageFwdHeader tL_messageFwdHeader = this.fwd_from;
                tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
                this.fwd_from.date = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            } else {
                this.media = new TL_messageMediaEmpty();
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            if (this.media_unread) {
                i = this.flags | TLRPC.USER_FLAG_PHOTO;
            } else {
                i = this.flags & -33;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeInt32(this.fwd_from.from_id);
                stream.writeInt32(this.fwd_from.date);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            stream.writeString(this.attachPath);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_message_old7 extends TL_message {
        public static int constructor;

        static {
            constructor = 1537633299;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.unread = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                this.from_id = stream.readInt32(exception);
            }
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.from_id == 0) {
                if (this.to_id.user_id != 0) {
                    this.from_id = this.to_id.user_id;
                } else {
                    this.from_id = -this.to_id.channel_id;
                }
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.fwd_from = new TL_messageFwdHeader();
                Peer peer = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
                TL_messageFwdHeader tL_messageFwdHeader;
                if (peer instanceof TL_peerChannel) {
                    this.fwd_from.channel_id = peer.channel_id;
                    tL_messageFwdHeader = this.fwd_from;
                    tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_FIRST_NAME;
                } else if (peer instanceof TL_peerUser) {
                    this.fwd_from.from_id = peer.user_id;
                    tL_messageFwdHeader = this.fwd_from;
                    tL_messageFwdHeader.flags |= TLRPC.USER_FLAG_ACCESS_HASH;
                }
                this.fwd_from.date = stream.readInt32(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.reply_to_msg_id = stream.readInt32(exception);
            }
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            } else {
                this.media = new TL_messageMediaEmpty();
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup = ReplyMarkup.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                int magic = stream.readInt32(exception);
                if (magic == 481674261) {
                    int count = stream.readInt32(exception);
                    int a = 0;
                    while (a < count) {
                        MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object != null) {
                            this.entities.add(object);
                            a += TLRPC.USER_FLAG_ACCESS_HASH;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                    objArr[0] = Integer.valueOf(magic);
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
                } else {
                    return;
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                this.views = stream.readInt32(exception);
            }
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                this.fwd_msg_id = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            if (this.media_unread) {
                i = this.flags | TLRPC.USER_FLAG_PHOTO;
            } else {
                i = this.flags & -33;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED2) != 0) {
                stream.writeInt32(this.from_id);
            }
            this.to_id.serializeToStream(stream);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                if (this.fwd_from.from_id != 0) {
                    TL_peerUser peer = new TL_peerUser();
                    peer.user_id = this.fwd_from.from_id;
                    peer.serializeToStream(stream);
                } else {
                    TL_peerChannel peer2 = new TL_peerChannel();
                    peer2.channel_id = this.fwd_from.channel_id;
                    peer2.serializeToStream(stream);
                }
                stream.writeInt32(this.fwd_from.date);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt32(this.reply_to_msg_id);
            }
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            if ((this.flags & TLRPC.USER_FLAG_UNUSED3) != 0) {
                this.media.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.reply_markup.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_UNUSED) != 0) {
                stream.writeInt32(481674261);
                int count = this.entities.size();
                stream.writeInt32(count);
                for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                    ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
                }
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                stream.writeInt32(this.views);
            }
            stream.writeString(this.attachPath);
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0 && this.id < 0) {
                stream.writeInt32(this.fwd_msg_id);
            }
        }
    }

    public static class TL_message_old extends TL_message {
        public static int constructor;

        static {
            constructor = 585853626;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.out = stream.readBool(exception);
            this.unread = stream.readBool(exception);
            this.flags |= 768;
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeBool(this.out);
            stream.writeBool(this.unread);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            stream.writeString(this.attachPath);
        }
    }

    public static class TL_message_secret extends TL_message {
        public static int constructor;

        static {
            constructor = 1431655929;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            this.flags = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.unread = z;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.media_unread = z;
            this.id = stream.readInt32(exception);
            this.ttl = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    MessageEntity object = MessageEntity.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.entities.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
                if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                    this.via_bot_name = stream.readString(exception);
                }
                if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                    this.reply_to_random_id = stream.readInt64(exception);
                }
                if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                    this.attachPath = stream.readString(exception);
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            int i;
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            if (this.media_unread) {
                i = this.flags | TLRPC.USER_FLAG_PHOTO;
            } else {
                i = this.flags & -33;
            }
            this.flags = i;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.ttl);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.entities.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((MessageEntity) this.entities.get(a)).serializeToStream(stream);
            }
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                stream.writeString(this.via_bot_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeInt64(this.reply_to_random_id);
            }
            stream.writeString(this.attachPath);
        }
    }

    public static class TL_peerNotifySettings_layer47 extends TL_peerNotifySettings {
        public static int constructor;
        public boolean show_previews;

        static {
            constructor = -1923214866;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.mute_until = stream.readInt32(exception);
            this.sound = stream.readString(exception);
            this.show_previews = stream.readBool(exception);
            this.events_mask = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.mute_until);
            stream.writeString(this.sound);
            stream.writeBool(this.show_previews);
            stream.writeInt32(this.events_mask);
        }
    }

    public static class TL_photo_old2 extends TL_photo {
        public static int constructor;

        static {
            constructor = -1014792074;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    PhotoSize object = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.sizes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            this.geo.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.sizes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((PhotoSize) this.sizes.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_photo_old extends TL_photo {
        public static int constructor;

        static {
            constructor = 582313809;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.caption = stream.readString(exception);
            this.geo = GeoPoint.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    PhotoSize object = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.sizes.add(object);
                        a += TLRPC.USER_FLAG_ACCESS_HASH;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                Object[] objArr = new Object[TLRPC.USER_FLAG_ACCESS_HASH];
                objArr[0] = Integer.valueOf(magic);
                throw new RuntimeException(String.format("wrong Vector magic, got %x", objArr));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeString(this.caption);
            this.geo.serializeToStream(stream);
            stream.writeInt32(481674261);
            int count = this.sizes.size();
            stream.writeInt32(count);
            for (int a = 0; a < count; a += TLRPC.USER_FLAG_ACCESS_HASH) {
                ((PhotoSize) this.sizes.get(a)).serializeToStream(stream);
            }
        }
    }

    public static class TL_sendMessageUploadAudioAction_old extends TL_sendMessageUploadAudioAction {
        public static int constructor;

        static {
            constructor = -424899985;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageUploadDocumentAction_old extends TL_sendMessageUploadDocumentAction {
        public static int constructor;

        static {
            constructor = -1884362354;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageUploadPhotoAction_old extends TL_sendMessageUploadPhotoAction {
        public static int constructor;

        static {
            constructor = -1727382502;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_sendMessageUploadVideoAction_old extends TL_sendMessageUploadVideoAction {
        public static int constructor;

        static {
            constructor = -1845219337;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_stickerSet_old extends TL_stickerSet {
        public static int constructor;

        static {
            constructor = -1482409193;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.title = stream.readString(exception);
            this.short_name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.title);
            stream.writeString(this.short_name);
        }
    }

    public static class TL_userContact_old extends TL_userContact_old2 {
        public static int constructor;

        static {
            constructor = -218397927;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.access_hash = stream.readInt64(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userDeleted_old extends TL_userDeleted_old2 {
        public static int constructor;

        static {
            constructor = -1298475060;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
        }
    }

    public static class TL_userForeign_old extends TL_userForeign_old2 {
        public static int constructor;

        static {
            constructor = 1377093789;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.access_hash = stream.readInt64(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeInt64(this.access_hash);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userProfilePhoto_old extends TL_userProfilePhoto {
        public static int constructor;

        static {
            constructor = -1727196013;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.photo_small = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.photo_big = FileLocation.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.photo_small.serializeToStream(stream);
            this.photo_big.serializeToStream(stream);
        }
    }

    public static class TL_userRequest_old extends TL_userRequest_old2 {
        public static int constructor;

        static {
            constructor = 585682608;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.access_hash = stream.readInt64(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeInt64(this.access_hash);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
        }
    }

    public static class TL_userSelf_old2 extends TL_userSelf_old3 {
        public static int constructor;

        static {
            constructor = 1879553105;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.username = stream.readString(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.inactive = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.username);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
            stream.writeBool(this.inactive);
        }
    }

    public static class TL_userSelf_old extends TL_userSelf_old3 {
        public static int constructor;

        static {
            constructor = 1912944108;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt32(exception);
            this.first_name = stream.readString(exception);
            this.last_name = stream.readString(exception);
            this.phone = stream.readString(exception);
            this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.inactive = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.id);
            stream.writeString(this.first_name);
            stream.writeString(this.last_name);
            stream.writeString(this.phone);
            this.photo.serializeToStream(stream);
            this.status.serializeToStream(stream);
            stream.writeBool(this.inactive);
        }
    }

    public static class TL_user_old extends TL_user {
        public static int constructor;

        static {
            constructor = 585404530;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = stream.readInt32(exception);
            this.self = (this.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0;
            if ((this.flags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.contact = z;
            if ((this.flags & MessagesController.UPDATE_MASK_SEND_STATE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mutual_contact = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.deleted = z;
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.bot = z;
            if ((this.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.bot_chat_history = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_CUT) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.bot_nochats = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_SET_SELECTION) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.verified = z;
            if ((this.flags & AccessibilityNodeInfoCompat.ACTION_EXPAND) == 0) {
                z2 = false;
            }
            this.explicit_content = z2;
            this.id = stream.readInt32(exception);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                this.access_hash = stream.readInt64(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                this.first_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                this.last_name = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                this.username = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                this.phone = stream.readString(exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.photo = UserProfilePhoto.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.status = UserStatus.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                this.bot_info_version = stream.readInt32(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.self ? this.flags | TLRPC.MESSAGE_FLAG_HAS_VIEWS : this.flags & -1025;
            this.flags = this.contact ? this.flags | TLRPC.MESSAGE_FLAG_HAS_BOT_ID : this.flags & -2049;
            this.flags = this.mutual_contact ? this.flags | MessagesController.UPDATE_MASK_SEND_STATE : this.flags & -4097;
            this.flags = this.deleted ? this.flags | MessagesController.UPDATE_MASK_CHANNEL : this.flags & -8193;
            this.flags = this.bot ? this.flags | MessagesController.UPDATE_MASK_CHAT_ADMINS : this.flags & -16385;
            this.flags = this.bot_chat_history ? this.flags | TLRPC.MESSAGE_FLAG_EDITED : this.flags & -32769;
            this.flags = this.bot_nochats ? this.flags | AccessibilityNodeInfoCompat.ACTION_CUT : this.flags & -65537;
            this.flags = this.verified ? this.flags | AccessibilityNodeInfoCompat.ACTION_SET_SELECTION : this.flags & -131073;
            this.flags = this.explicit_content ? this.flags | AccessibilityNodeInfoCompat.ACTION_EXPAND : this.flags & -262145;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            if ((this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0) {
                stream.writeInt64(this.access_hash);
            }
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                stream.writeString(this.first_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_LAST_NAME) != 0) {
                stream.writeString(this.last_name);
            }
            if ((this.flags & TLRPC.USER_FLAG_USERNAME) != 0) {
                stream.writeString(this.username);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                stream.writeString(this.phone);
            }
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) != 0) {
                this.photo.serializeToStream(stream);
            }
            if ((this.flags & TLRPC.USER_FLAG_STATUS) != 0) {
                this.status.serializeToStream(stream);
            }
            if ((this.flags & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                stream.writeInt32(this.bot_info_version);
            }
        }
    }

    public static class TL_videoEncrypted extends TL_video_layer45 {
        public static int constructor;

        static {
            constructor = 1431655763;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.caption = stream.readString(exception);
            this.duration = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
            this.key = stream.readByteArray(exception);
            this.iv = stream.readByteArray(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeString(this.caption);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
            stream.writeByteArray(this.key);
            stream.writeByteArray(this.iv);
        }
    }

    public static class TL_video_old2 extends TL_video_layer45 {
        public static int constructor;

        static {
            constructor = 948937617;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.caption = stream.readString(exception);
            this.duration = stream.readInt32(exception);
            this.mime_type = stream.readString(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeString(this.caption);
            stream.writeInt32(this.duration);
            stream.writeString(this.mime_type);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_video_old3 extends TL_video_layer45 {
        public static int constructor;

        static {
            constructor = -291550643;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.duration = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_video_old extends TL_video_layer45 {
        public static int constructor;

        static {
            constructor = 1510253727;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readInt64(exception);
            this.access_hash = stream.readInt64(exception);
            this.user_id = stream.readInt32(exception);
            this.date = stream.readInt32(exception);
            this.caption = stream.readString(exception);
            this.duration = stream.readInt32(exception);
            this.size = stream.readInt32(exception);
            this.thumb = PhotoSize.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.dc_id = stream.readInt32(exception);
            this.w = stream.readInt32(exception);
            this.h = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(this.id);
            stream.writeInt64(this.access_hash);
            stream.writeInt32(this.user_id);
            stream.writeInt32(this.date);
            stream.writeString(this.caption);
            stream.writeInt32(this.duration);
            stream.writeInt32(this.size);
            this.thumb.serializeToStream(stream);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.w);
            stream.writeInt32(this.h);
        }
    }

    public static class TL_message_secret_old extends TL_message_secret {
        public static int constructor;

        static {
            constructor = 1431655928;
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            boolean z;
            boolean z2 = true;
            this.flags = (stream.readInt32(exception) | TLRPC.USER_FLAG_UNUSED2) | TLRPC.USER_FLAG_UNUSED3;
            this.unread = (this.flags & TLRPC.USER_FLAG_ACCESS_HASH) != 0;
            if ((this.flags & TLRPC.USER_FLAG_FIRST_NAME) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.out = z;
            if ((this.flags & TLRPC.USER_FLAG_PHONE) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mentioned = z;
            if ((this.flags & TLRPC.USER_FLAG_PHOTO) == 0) {
                z2 = false;
            }
            this.media_unread = z2;
            this.id = stream.readInt32(exception);
            this.ttl = stream.readInt32(exception);
            this.from_id = stream.readInt32(exception);
            this.to_id = Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.date = stream.readInt32(exception);
            this.message = stream.readString(exception);
            this.media = MessageMedia.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (this.id < 0 || !(this.media == null || (this.media instanceof TL_messageMediaEmpty) || (this.media instanceof TL_messageMediaWebPage) || this.message == null || this.message.length() == 0 || !this.message.startsWith("-1"))) {
                this.attachPath = stream.readString(exception);
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.flags = this.unread ? this.flags | TLRPC.USER_FLAG_ACCESS_HASH : this.flags & -2;
            this.flags = this.out ? this.flags | TLRPC.USER_FLAG_FIRST_NAME : this.flags & -3;
            this.flags = this.mentioned ? this.flags | TLRPC.USER_FLAG_PHONE : this.flags & -17;
            this.flags = this.media_unread ? this.flags | TLRPC.USER_FLAG_PHOTO : this.flags & -33;
            stream.writeInt32(this.flags);
            stream.writeInt32(this.id);
            stream.writeInt32(this.ttl);
            stream.writeInt32(this.from_id);
            this.to_id.serializeToStream(stream);
            stream.writeInt32(this.date);
            stream.writeString(this.message);
            this.media.serializeToStream(stream);
            stream.writeString(this.attachPath);
        }
    }
}
