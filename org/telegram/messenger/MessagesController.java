package org.telegram.messenger;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer.ExoPlayer.Factory;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_auth_logOut;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_botInfo;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.telegram.tgnet.TLRPC.TL_channels_editAbout;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editPhoto;
import org.telegram.tgnet.TLRPC.TL_channels_editTitle;
import org.telegram.tgnet.TLRPC.TL_channels_getFullChannel;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.telegram.tgnet.TLRPC.TL_channels_leaveChannel;
import org.telegram.tgnet.TLRPC.TL_channels_readHistory;
import org.telegram.tgnet.TLRPC.TL_channels_toggleInvites;
import org.telegram.tgnet.TLRPC.TL_channels_toggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channels_updatePinnedMessage;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chat;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_contactBlocked;
import org.telegram.tgnet.TLRPC.TL_contactLinkContact;
import org.telegram.tgnet.TLRPC.TL_contacts_block;
import org.telegram.tgnet.TLRPC.TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_contacts_unblock;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_disabledFeature;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_appChangelog;
import org.telegram.tgnet.TLRPC.TL_help_getAppChangelog;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_inputGeoPointEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhotoCropAuto;
import org.telegram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUser;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUserSelf;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_chatFull;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_deleteHistory;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC.TL_messages_editChatPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_editChatTitle;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC.TL_messages_getHistory;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerSettings;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_reportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC.TL_messages_setTyping;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_messages_toggleChatAdmins;
import org.telegram.tgnet.TLRPC.TL_notifyPeer;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_peerSettings;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardHide;
import org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordVideoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageTypingAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadDocumentAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadPhotoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadVideoAction;
import org.telegram.tgnet.TLRPC.TL_updateChannel;
import org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
import org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
import org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
import org.telegram.tgnet.TLRPC.TL_updateChatAdmins;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
import org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateContactLink;
import org.telegram.tgnet.TLRPC.TL_updateContactRegistered;
import org.telegram.tgnet.TLRPC.TL_updateDcOptions;
import org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewAuthorization;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewGeoChatMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
import org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_updatePrivacy;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
import org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updateStickerSets;
import org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.TL_updateUserPhone;
import org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
import org.telegram.tgnet.TLRPC.TL_updateUserStatus;
import org.telegram.tgnet.TLRPC.TL_updateUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateWebPage;
import org.telegram.tgnet.TLRPC.TL_updatesCombined;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_differenceSlice;
import org.telegram.tgnet.TLRPC.TL_updates_getChannelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getState;
import org.telegram.tgnet.TLRPC.TL_updates_state;
import org.telegram.tgnet.TLRPC.TL_userForeign_old2;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.TL_users_getFullUser;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.contacts_Blocked;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;
import org.telegram.tgnet.TLRPC.updates_Difference;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class MessagesController implements NotificationCenterDelegate {
    private static volatile MessagesController Instance = null;
    public static final int UPDATE_MASK_ALL = 1535;
    public static final int UPDATE_MASK_AVATAR = 2;
    public static final int UPDATE_MASK_CHANNEL = 8192;
    public static final int UPDATE_MASK_CHAT_ADMINS = 16384;
    public static final int UPDATE_MASK_CHAT_AVATAR = 8;
    public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static final int UPDATE_MASK_CHAT_NAME = 16;
    public static final int UPDATE_MASK_NAME = 1;
    public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static final int UPDATE_MASK_PHONE = 1024;
    public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static final int UPDATE_MASK_SELECT_DIALOG = 512;
    public static final int UPDATE_MASK_SEND_STATE = 4096;
    public static final int UPDATE_MASK_STATUS = 4;
    public static final int UPDATE_MASK_USER_PHONE = 128;
    public static final int UPDATE_MASK_USER_PRINT = 64;
    public ArrayList<Integer> blockedUsers;
    private SparseArray<ArrayList<Integer>> channelViewsToReload;
    private SparseArray<ArrayList<Integer>> channelViewsToSend;
    private HashMap<Integer, Integer> channelsPts;
    private ConcurrentHashMap<Integer, Chat> chats;
    private HashMap<Integer, Boolean> checkingLastMessagesDialogs;
    private ArrayList<Long> createdDialogIds;
    private Runnable currentDeleteTaskRunnable;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    private final Comparator<TL_dialog> dialogComparator;
    public HashMap<Long, MessageObject> dialogMessage;
    public HashMap<Integer, MessageObject> dialogMessagesByIds;
    public HashMap<Long, MessageObject> dialogMessagesByRandomIds;
    public ArrayList<TL_dialog> dialogs;
    public boolean dialogsEndReached;
    public ArrayList<TL_dialog> dialogsGroupsOnly;
    public ArrayList<TL_dialog> dialogsServerOnly;
    public ConcurrentHashMap<Long, TL_dialog> dialogs_dict;
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max;
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max;
    private ArrayList<TL_disabledFeature> disabledFeatures;
    public boolean enableJoined;
    private ConcurrentHashMap<Integer, EncryptedChat> encryptedChats;
    private HashMap<Integer, ExportedChatInvite> exportedChats;
    public boolean firstGettingTask;
    public int fontSize;
    private HashMap<Integer, String> fullUsersAbout;
    public boolean gettingDifference;
    private HashMap<Integer, Boolean> gettingDifferenceChannels;
    private boolean gettingNewDeleteTask;
    private HashMap<Integer, Boolean> gettingUnknownChannels;
    public int groupBigSize;
    private ArrayList<Integer> joiningToChannels;
    private int lastPrintingStringCount;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    private ArrayList<Integer> loadedFullChats;
    private ArrayList<Integer> loadedFullParticipants;
    private ArrayList<Integer> loadedFullUsers;
    public boolean loadingBlockedUsers;
    public boolean loadingDialogs;
    private ArrayList<Integer> loadingFullChats;
    private ArrayList<Integer> loadingFullParticipants;
    private ArrayList<Integer> loadingFullUsers;
    private HashMap<Long, Boolean> loadingPeerSettings;
    public int maxBroadcastCount;
    public int maxEditTime;
    public int maxGroupCount;
    public int maxMegagroupCount;
    private boolean migratingDialogs;
    public int minGroupConvertSize;
    private SparseIntArray needShortPollChannels;
    public int nextDialogsCacheOffset;
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy;
    public HashMap<Long, CharSequence> printingStrings;
    public HashMap<Long, Integer> printingStringsTypes;
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers;
    public int ratingDecay;
    public boolean registeringForPush;
    private HashMap<Long, ArrayList<Integer>> reloadingMessages;
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages;
    private HashMap<Long, ArrayList<MessageObject>> reloadingWebpagesPending;
    public int secretWebpagePreview;
    public HashMap<Integer, HashMap<Long, Boolean>> sendingTypings;
    private SparseIntArray shortPollChannels;
    private int statusRequest;
    private int statusSettingState;
    private final Comparator<Update> updatesComparator;
    private HashMap<Integer, ArrayList<Updates>> updatesQueueChannels;
    private ArrayList<Updates> updatesQueuePts;
    private ArrayList<Updates> updatesQueueQts;
    private ArrayList<Updates> updatesQueueSeq;
    private HashMap<Integer, Long> updatesStartWaitTimeChannels;
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    private String uploadingAvatar;
    private ConcurrentHashMap<Integer, User> users;
    private ConcurrentHashMap<String, User> usersByUsernames;

    /* renamed from: org.telegram.messenger.MessagesController.101 */
    class AnonymousClass101 implements Runnable {
        final /* synthetic */ ArrayList val$chatsArr;
        final /* synthetic */ ArrayList val$usersArr;

        AnonymousClass101(ArrayList arrayList, ArrayList arrayList2) {
            this.val$usersArr = arrayList;
            this.val$chatsArr = arrayList2;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$usersArr, false);
            MessagesController.this.putChats(this.val$chatsArr, false);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.102 */
    class AnonymousClass102 implements Runnable {
        final /* synthetic */ ArrayList val$chatsArr;
        final /* synthetic */ ArrayList val$usersArr;

        AnonymousClass102(ArrayList arrayList, ArrayList arrayList2) {
            this.val$usersArr = arrayList;
            this.val$chatsArr = arrayList2;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$usersArr, false);
            MessagesController.this.putChats(this.val$chatsArr, false);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.104 */
    class AnonymousClass104 implements Runnable {
        final /* synthetic */ TL_updateUserBlocked val$finalUpdate;

        /* renamed from: org.telegram.messenger.MessagesController.104.1 */
        class C05601 implements Runnable {
            C05601() {
            }

            public void run() {
                if (!AnonymousClass104.this.val$finalUpdate.blocked) {
                    MessagesController.this.blockedUsers.remove(Integer.valueOf(AnonymousClass104.this.val$finalUpdate.user_id));
                } else if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(AnonymousClass104.this.val$finalUpdate.user_id))) {
                    MessagesController.this.blockedUsers.add(Integer.valueOf(AnonymousClass104.this.val$finalUpdate.user_id));
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            }
        }

        AnonymousClass104(TL_updateUserBlocked tL_updateUserBlocked) {
            this.val$finalUpdate = tL_updateUserBlocked;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05601());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.105 */
    class AnonymousClass105 implements Runnable {
        final /* synthetic */ ArrayList val$pushMessages;

        /* renamed from: org.telegram.messenger.MessagesController.105.1 */
        class C05611 implements Runnable {
            C05611() {
            }

            public void run() {
                NotificationsController.getInstance().processNewMessages(AnonymousClass105.this.val$pushMessages, true);
            }
        }

        AnonymousClass105(ArrayList arrayList) {
            this.val$pushMessages = arrayList;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05611());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.106 */
    class AnonymousClass106 implements Runnable {
        final /* synthetic */ SparseArray val$channelViews;
        final /* synthetic */ ArrayList val$chatInfoToUpdate;
        final /* synthetic */ ArrayList val$contactsIds;
        final /* synthetic */ HashMap val$editingMessages;
        final /* synthetic */ int val$interfaceUpdateMaskFinal;
        final /* synthetic */ HashMap val$messages;
        final /* synthetic */ boolean val$printChangedArg;
        final /* synthetic */ ArrayList val$updatesOnMainThread;
        final /* synthetic */ HashMap val$webPages;

        /* renamed from: org.telegram.messenger.MessagesController.106.1 */
        class C05621 implements Runnable {
            final /* synthetic */ User val$currentUser;

            C05621(User user) {
                this.val$currentUser = user;
            }

            public void run() {
                ContactsController.getInstance().addContactToPhoneBook(this.val$currentUser, true);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.106.2 */
        class C05632 implements Runnable {
            final /* synthetic */ Update val$update;

            C05632(Update update) {
                this.val$update = update;
            }

            public void run() {
                MessagesController.this.getChannelDifference(this.val$update.channel_id, MessagesController.UPDATE_MASK_NAME, 0);
            }
        }

        AnonymousClass106(int i, ArrayList arrayList, HashMap hashMap, HashMap hashMap2, HashMap hashMap3, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray) {
            this.val$interfaceUpdateMaskFinal = i;
            this.val$updatesOnMainThread = arrayList;
            this.val$webPages = hashMap;
            this.val$messages = hashMap2;
            this.val$editingMessages = hashMap3;
            this.val$printChangedArg = z;
            this.val$contactsIds = arrayList2;
            this.val$chatInfoToUpdate = arrayList3;
            this.val$channelViews = sparseArray;
        }

        public void run() {
            int a;
            long dialog_id;
            NotificationCenter instance;
            int i;
            Object[] objArr;
            ArrayList<MessageObject> arrayList;
            int updateMask = this.val$interfaceUpdateMaskFinal;
            boolean hasDraftUpdates = false;
            if (!this.val$updatesOnMainThread.isEmpty()) {
                ArrayList<User> dbUsers = new ArrayList();
                ArrayList<User> dbUsersStatus = new ArrayList();
                Editor editor = null;
                for (a = 0; a < this.val$updatesOnMainThread.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Update update = (Update) this.val$updatesOnMainThread.get(a);
                    User toDbUser = new User();
                    toDbUser.id = update.user_id;
                    User currentUser = MessagesController.this.getUser(Integer.valueOf(update.user_id));
                    if (update instanceof TL_updatePrivacy) {
                        if (update.key instanceof TL_privacyKeyStatusTimestamp) {
                            ContactsController.getInstance().setPrivacyRules(update.rules, false);
                        } else if (update.key instanceof TL_privacyKeyChatInvite) {
                            ContactsController.getInstance().setPrivacyRules(update.rules, true);
                        }
                    } else if (update instanceof TL_updateUserStatus) {
                        if (update.status instanceof TL_userStatusRecently) {
                            update.status.expires = -100;
                        } else if (update.status instanceof TL_userStatusLastWeek) {
                            update.status.expires = -101;
                        } else if (update.status instanceof TL_userStatusLastMonth) {
                            update.status.expires = -102;
                        }
                        if (currentUser != null) {
                            currentUser.id = update.user_id;
                            currentUser.status = update.status;
                        }
                        toDbUser.status = update.status;
                        dbUsersStatus.add(toDbUser);
                        if (update.user_id == UserConfig.getClientUserId()) {
                            NotificationsController.getInstance().setLastOnlineFromOtherDevice(update.status.expires);
                        }
                    } else if (update instanceof TL_updateUserName) {
                        if (currentUser != null) {
                            if (!UserObject.isContact(currentUser)) {
                                currentUser.first_name = update.first_name;
                                currentUser.last_name = update.last_name;
                            }
                            if (currentUser.username != null && currentUser.username.length() > 0) {
                                MessagesController.this.usersByUsernames.remove(currentUser.username);
                            }
                            if (update.username != null && update.username.length() > 0) {
                                MessagesController.this.usersByUsernames.put(update.username, currentUser);
                            }
                            currentUser.username = update.username;
                        }
                        toDbUser.first_name = update.first_name;
                        toDbUser.last_name = update.last_name;
                        toDbUser.username = update.username;
                        dbUsers.add(toDbUser);
                    } else if (update instanceof TL_updateUserPhoto) {
                        if (currentUser != null) {
                            currentUser.photo = update.photo;
                        }
                        toDbUser.photo = update.photo;
                        dbUsers.add(toDbUser);
                    } else if (update instanceof TL_updateUserPhone) {
                        if (currentUser != null) {
                            currentUser.phone = update.phone;
                            Utilities.phoneBookQueue.postRunnable(new C05621(currentUser));
                        }
                        toDbUser.phone = update.phone;
                        dbUsers.add(toDbUser);
                    } else if (update instanceof TL_updateNotifySettings) {
                        TL_updateNotifySettings updateNotifySettings = (TL_updateNotifySettings) update;
                        if ((update.notify_settings instanceof TL_peerNotifySettings) && (updateNotifySettings.peer instanceof TL_notifyPeer)) {
                            if (editor == null) {
                                editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                            }
                            if (updateNotifySettings.peer.peer.user_id != 0) {
                                dialog_id = (long) updateNotifySettings.peer.peer.user_id;
                            } else if (updateNotifySettings.peer.peer.chat_id != 0) {
                                dialog_id = (long) (-updateNotifySettings.peer.peer.chat_id);
                            } else {
                                dialog_id = (long) (-updateNotifySettings.peer.peer.channel_id);
                            }
                            dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(dialog_id));
                            if (dialog != null) {
                                dialog.notify_settings = update.notify_settings;
                            }
                            editor.putBoolean("silent_" + dialog_id, update.notify_settings.silent);
                            if (update.notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime()) {
                                int until = 0;
                                if (update.notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                                    editor.putInt("notify2_" + dialog_id, MessagesController.UPDATE_MASK_AVATAR);
                                    if (dialog != null) {
                                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                } else {
                                    until = update.notify_settings.mute_until;
                                    editor.putInt("notify2_" + dialog_id, 3);
                                    editor.putInt("notifyuntil_" + dialog_id, update.notify_settings.mute_until);
                                    if (dialog != null) {
                                        dialog.notify_settings.mute_until = until;
                                    }
                                }
                                MessagesStorage.getInstance().setDialogFlags(dialog_id, (((long) until) << MessagesController.UPDATE_MASK_CHAT_MEMBERS) | 1);
                                NotificationsController.getInstance().removeNotificationsForDialog(dialog_id);
                            } else {
                                if (dialog != null) {
                                    dialog.notify_settings.mute_until = 0;
                                }
                                editor.remove("notify2_" + dialog_id);
                                MessagesStorage.getInstance().setDialogFlags(dialog_id, 0);
                            }
                        }
                    } else if (update instanceof TL_updateChannel) {
                        dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(-((long) update.channel_id)));
                        Chat chat = MessagesController.this.getChat(Integer.valueOf(update.channel_id));
                        if (chat != null) {
                            if (dialog == null && (chat instanceof TL_channel) && !chat.left) {
                                Utilities.stageQueue.postRunnable(new C05632(update));
                            } else if (chat.left && dialog != null) {
                                MessagesController.this.deleteDialog(dialog.id, 0);
                            }
                        }
                        updateMask |= MessagesController.UPDATE_MASK_CHANNEL;
                        MessagesController.this.loadFullChat(update.channel_id, 0, true);
                    } else if (update instanceof TL_updateChatAdmins) {
                        updateMask |= MessagesController.UPDATE_MASK_CHAT_ADMINS;
                    } else if (update instanceof TL_updateStickerSets) {
                        StickersQuery.loadStickers(false, true);
                    } else if (update instanceof TL_updateStickerSetsOrder) {
                        StickersQuery.reorderStickers(update.order);
                    } else if (update instanceof TL_updateNewStickerSet) {
                        StickersQuery.addNewStickerSet(update.stickerset);
                    } else if (update instanceof TL_updateSavedGifs) {
                        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit();
                        editor2.putLong("lastGifLoadTime", 0).commit();
                    } else if (update instanceof TL_updateDraftMessage) {
                        long did;
                        hasDraftUpdates = true;
                        Peer peer = ((TL_updateDraftMessage) update).peer;
                        if (peer.user_id != 0) {
                            did = (long) peer.user_id;
                        } else if (peer.channel_id != 0) {
                            did = (long) (-peer.channel_id);
                        } else {
                            did = (long) (-peer.chat_id);
                        }
                        DraftQuery.saveDraft(did, update.draft, null, true);
                    }
                }
                if (editor != null) {
                    editor.commit();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
                MessagesStorage.getInstance().updateUsers(dbUsersStatus, true, true, true);
                MessagesStorage.getInstance().updateUsers(dbUsers, false, true, true);
            }
            if (!this.val$webPages.isEmpty()) {
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.didReceivedWebpagesInUpdates;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = this.val$webPages;
                instance.postNotificationName(i, objArr);
                for (Entry<Long, WebPage> entry : this.val$webPages.entrySet()) {
                    arrayList = (ArrayList) MessagesController.this.reloadingWebpagesPending.remove(entry.getKey());
                    if (arrayList != null) {
                        WebPage webpage = (WebPage) entry.getValue();
                        ArrayList messagesArr = new ArrayList();
                        dialog_id = 0;
                        if ((webpage instanceof TL_webPage) || (webpage instanceof TL_webPageEmpty)) {
                            for (a = 0; a < arrayList.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = webpage;
                                if (a == 0) {
                                    dialog_id = ((MessageObject) arrayList.get(a)).getDialogId();
                                    ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(a)).messageOwner);
                                }
                                messagesArr.add(((MessageObject) arrayList.get(a)).messageOwner);
                            }
                        } else {
                            MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(webpage.id), arrayList);
                        }
                        if (!messagesArr.isEmpty()) {
                            MessagesStorage.getInstance().putMessages(messagesArr, true, true, false, MediaController.getInstance().getAutodownloadMask());
                            instance = NotificationCenter.getInstance();
                            i = NotificationCenter.replaceMessagesObjects;
                            objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                            objArr[0] = Long.valueOf(dialog_id);
                            objArr[MessagesController.UPDATE_MASK_NAME] = arrayList;
                            instance.postNotificationName(i, objArr);
                        }
                    }
                }
            }
            boolean updateDialogs = false;
            if (!this.val$messages.isEmpty()) {
                for (Entry<Long, ArrayList<MessageObject>> entry2 : this.val$messages.entrySet()) {
                    ArrayList<MessageObject> value = (ArrayList) entry2.getValue();
                    MessagesController.this.updateInterfaceWithMessages(((Long) entry2.getKey()).longValue(), value);
                }
                updateDialogs = true;
            } else if (hasDraftUpdates) {
                MessagesController.this.sortDialogs(null);
                updateDialogs = true;
            }
            if (!this.val$editingMessages.isEmpty()) {
                for (Entry<Long, ArrayList<MessageObject>> pair : this.val$editingMessages.entrySet()) {
                    Long dialog_id2 = (Long) pair.getKey();
                    arrayList = (ArrayList) pair.getValue();
                    MessageObject oldObject = (MessageObject) MessagesController.this.dialogMessage.get(dialog_id2);
                    if (oldObject != null) {
                        a = 0;
                        while (a < arrayList.size()) {
                            MessageObject newMessage = (MessageObject) arrayList.get(a);
                            if (oldObject.getId() == newMessage.getId()) {
                                MessagesController.this.dialogMessage.put(dialog_id2, newMessage);
                                if (newMessage.messageOwner.to_id != null && newMessage.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(newMessage.getId()), newMessage);
                                }
                                updateDialogs = true;
                            } else {
                                a += MessagesController.UPDATE_MASK_NAME;
                            }
                        }
                    }
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.replaceMessagesObjects;
                    objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                    objArr[0] = dialog_id2;
                    objArr[MessagesController.UPDATE_MASK_NAME] = arrayList;
                    instance.postNotificationName(i, objArr);
                }
            }
            if (updateDialogs) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            if (this.val$printChangedArg) {
                updateMask |= MessagesController.UPDATE_MASK_USER_PRINT;
            }
            if (!this.val$contactsIds.isEmpty()) {
                updateMask = (updateMask | MessagesController.UPDATE_MASK_NAME) | MessagesController.UPDATE_MASK_USER_PHONE;
            }
            if (!this.val$chatInfoToUpdate.isEmpty()) {
                for (a = 0; a < this.val$chatInfoToUpdate.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    MessagesStorage.getInstance().updateChatParticipants((ChatParticipants) this.val$chatInfoToUpdate.get(a));
                }
            }
            if (this.val$channelViews.size() != 0) {
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.didUpdatedMessagesViews;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = this.val$channelViews;
                instance.postNotificationName(i, objArr);
            }
            if (updateMask != 0) {
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.updateInterfaces;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(updateMask);
                instance.postNotificationName(i, objArr);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.107 */
    class AnonymousClass107 implements Runnable {
        final /* synthetic */ SparseArray val$deletedMessages;
        final /* synthetic */ HashMap val$markAsReadEncrypted;
        final /* synthetic */ ArrayList val$markAsReadMessages;
        final /* synthetic */ SparseArray val$markAsReadMessagesInbox;
        final /* synthetic */ SparseArray val$markAsReadMessagesOutbox;

        /* renamed from: org.telegram.messenger.MessagesController.107.1 */
        class C05641 implements Runnable {
            C05641() {
            }

            public void run() {
                int b;
                int key;
                MessageObject obj;
                int updateMask = 0;
                if (!(AnonymousClass107.this.val$markAsReadMessagesInbox.size() == 0 && AnonymousClass107.this.val$markAsReadMessagesOutbox.size() == 0)) {
                    int messageId;
                    TL_dialog dialog;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.messagesRead;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                    objArr[0] = AnonymousClass107.this.val$markAsReadMessagesInbox;
                    objArr[MessagesController.UPDATE_MASK_NAME] = AnonymousClass107.this.val$markAsReadMessagesOutbox;
                    instance.postNotificationName(i, objArr);
                    NotificationsController.getInstance().processReadMessages(AnonymousClass107.this.val$markAsReadMessagesInbox, 0, 0, 0, false);
                    for (b = 0; b < AnonymousClass107.this.val$markAsReadMessagesInbox.size(); b += MessagesController.UPDATE_MASK_NAME) {
                        key = AnonymousClass107.this.val$markAsReadMessagesInbox.keyAt(b);
                        messageId = (int) ((Long) AnonymousClass107.this.val$markAsReadMessagesInbox.get(key)).longValue();
                        dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf((long) key));
                        if (dialog != null && dialog.top_message <= messageId) {
                            obj = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(dialog.id));
                            if (!(obj == null || obj.isOut())) {
                                obj.setIsRead();
                                updateMask |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                            }
                        }
                    }
                    for (b = 0; b < AnonymousClass107.this.val$markAsReadMessagesOutbox.size(); b += MessagesController.UPDATE_MASK_NAME) {
                        key = AnonymousClass107.this.val$markAsReadMessagesOutbox.keyAt(b);
                        messageId = (int) ((Long) AnonymousClass107.this.val$markAsReadMessagesOutbox.get(key)).longValue();
                        dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf((long) key));
                        if (dialog != null && dialog.top_message <= messageId) {
                            obj = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(dialog.id));
                            if (obj != null && obj.isOut()) {
                                obj.setIsRead();
                                updateMask |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                            }
                        }
                    }
                }
                if (!AnonymousClass107.this.val$markAsReadEncrypted.isEmpty()) {
                    for (Entry<Integer, Integer> entry : AnonymousClass107.this.val$markAsReadEncrypted.entrySet()) {
                        instance = NotificationCenter.getInstance();
                        i = NotificationCenter.messagesReadEncrypted;
                        objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                        objArr[0] = entry.getKey();
                        objArr[MessagesController.UPDATE_MASK_NAME] = entry.getValue();
                        instance.postNotificationName(i, objArr);
                        long dialog_id = ((long) ((Integer) entry.getKey()).intValue()) << MessagesController.UPDATE_MASK_CHAT_MEMBERS;
                        if (((TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(dialog_id))) != null) {
                            MessageObject message = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(dialog_id));
                            if (message != null && message.messageOwner.date <= ((Integer) entry.getValue()).intValue()) {
                                message.setIsRead();
                                updateMask |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                            }
                        }
                    }
                }
                if (!AnonymousClass107.this.val$markAsReadMessages.isEmpty()) {
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.messagesReadContent;
                    objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = AnonymousClass107.this.val$markAsReadMessages;
                    instance.postNotificationName(i, objArr);
                }
                if (AnonymousClass107.this.val$deletedMessages.size() != 0) {
                    for (int a = 0; a < AnonymousClass107.this.val$deletedMessages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                        key = AnonymousClass107.this.val$deletedMessages.keyAt(a);
                        ArrayList<Integer> arrayList = (ArrayList) AnonymousClass107.this.val$deletedMessages.get(key);
                        if (arrayList != null) {
                            instance = NotificationCenter.getInstance();
                            i = NotificationCenter.messagesDeleted;
                            objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                            objArr[0] = arrayList;
                            objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(key);
                            instance.postNotificationName(i, objArr);
                            if (key == 0) {
                                for (b = 0; b < arrayList.size(); b += MessagesController.UPDATE_MASK_NAME) {
                                    obj = (MessageObject) MessagesController.this.dialogMessagesByIds.get((Integer) arrayList.get(b));
                                    if (obj != null) {
                                        obj.deleted = true;
                                    }
                                }
                            } else {
                                obj = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf((long) (-key)));
                                if (obj != null) {
                                    for (b = 0; b < arrayList.size(); b += MessagesController.UPDATE_MASK_NAME) {
                                        if (obj.getId() == ((Integer) arrayList.get(b)).intValue()) {
                                            obj.deleted = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    NotificationsController.getInstance().removeDeletedMessagesFromNotifications(AnonymousClass107.this.val$deletedMessages);
                }
                if (updateMask != 0) {
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.updateInterfaces;
                    objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(updateMask);
                    instance.postNotificationName(i, objArr);
                }
            }
        }

        AnonymousClass107(SparseArray sparseArray, SparseArray sparseArray2, HashMap hashMap, ArrayList arrayList, SparseArray sparseArray3) {
            this.val$markAsReadMessagesInbox = sparseArray;
            this.val$markAsReadMessagesOutbox = sparseArray2;
            this.val$markAsReadEncrypted = hashMap;
            this.val$markAsReadMessages = arrayList;
            this.val$deletedMessages = sparseArray3;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05641());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.109 */
    static class AnonymousClass109 implements OnClickListener {
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ int val$reqId;

        AnonymousClass109(int i, BaseFragment baseFragment) {
            this.val$reqId = i;
            this.val$fragment = baseFragment;
        }

        public void onClick(DialogInterface dialog, int which) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (this.val$fragment != null) {
                this.val$fragment.setVisibleDialog(null);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ int val$minDate;

        AnonymousClass17(int i) {
            this.val$minDate = i;
        }

        public void run() {
            if ((MessagesController.this.currentDeletingTaskMids == null && !MessagesController.this.gettingNewDeleteTask) || (MessagesController.this.currentDeletingTaskTime != 0 && this.val$minDate < MessagesController.this.currentDeletingTaskTime)) {
                MessagesController.this.getNewDeleteTask(null);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ SparseArray val$mids;

        AnonymousClass18(SparseArray sparseArray) {
            this.val$mids = sparseArray;
        }

        public void run() {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.didCreatedNewDeleteTask;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
            objArr[0] = this.val$mids;
            instance.postNotificationName(i, objArr);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ ArrayList val$oldTask;

        AnonymousClass19(ArrayList arrayList) {
            this.val$oldTask = arrayList;
        }

        public void run() {
            MessagesController.this.gettingNewDeleteTask = true;
            MessagesStorage.getInstance().getNewTask(this.val$oldTask);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.1 */
    class C05711 implements Comparator<TL_dialog> {
        C05711() {
        }

        public int compare(TL_dialog dialog1, TL_dialog dialog2) {
            DraftMessage draftMessage = DraftQuery.getDraft(dialog1.id);
            int date1 = (draftMessage == null || draftMessage.date < dialog1.last_message_date) ? dialog1.last_message_date : draftMessage.date;
            draftMessage = DraftQuery.getDraft(dialog2.id);
            int date2 = (draftMessage == null || draftMessage.date < dialog2.last_message_date) ? dialog2.last_message_date : draftMessage.date;
            if (date1 < date2) {
                return MessagesController.UPDATE_MASK_NAME;
            }
            if (date1 > date2) {
                return -1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.21 */
    class AnonymousClass21 implements Runnable {
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ int val$taskTime;

        /* renamed from: org.telegram.messenger.MessagesController.21.1 */
        class C05731 implements Runnable {
            C05731() {
            }

            public void run() {
                MessagesController.this.checkDeletingTask(true);
            }
        }

        AnonymousClass21(ArrayList arrayList, int i) {
            this.val$messages = arrayList;
            this.val$taskTime = i;
        }

        public void run() {
            MessagesController.this.gettingNewDeleteTask = false;
            if (this.val$messages != null) {
                MessagesController.this.currentDeletingTaskTime = this.val$taskTime;
                MessagesController.this.currentDeletingTaskMids = this.val$messages;
                if (MessagesController.this.currentDeleteTaskRunnable != null) {
                    Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
                    MessagesController.this.currentDeleteTaskRunnable = null;
                }
                if (!MessagesController.this.checkDeletingTask(false)) {
                    MessagesController.this.currentDeleteTaskRunnable = new C05731();
                    Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, ((long) Math.abs(ConnectionsManager.getInstance().getCurrentTime() - MessagesController.this.currentDeletingTaskTime)) * 1000);
                    return;
                }
                return;
            }
            MessagesController.this.currentDeletingTaskTime = 0;
            MessagesController.this.currentDeletingTaskMids = null;
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.27 */
    class AnonymousClass27 implements Runnable {
        final /* synthetic */ boolean val$cache;
        final /* synthetic */ ArrayList val$ids;
        final /* synthetic */ ArrayList val$users;

        AnonymousClass27(ArrayList arrayList, boolean z, ArrayList arrayList2) {
            this.val$users = arrayList;
            this.val$cache = z;
            this.val$ids = arrayList2;
        }

        public void run() {
            if (this.val$users != null) {
                MessagesController.this.putUsers(this.val$users, this.val$cache);
            }
            MessagesController.this.loadingBlockedUsers = false;
            if (this.val$ids.isEmpty() && this.val$cache && !UserConfig.blockedUsersLoaded) {
                MessagesController.this.getBlockedUsers(false);
                return;
            }
            if (!this.val$cache) {
                UserConfig.blockedUsersLoaded = true;
                UserConfig.saveConfig(false);
            }
            MessagesController.this.blockedUsers = this.val$ids;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.2 */
    class C05752 implements Comparator<Update> {
        C05752() {
        }

        public int compare(Update lhs, Update rhs) {
            int ltype = MessagesController.this.getUpdateType(lhs);
            int rtype = MessagesController.this.getUpdateType(rhs);
            if (ltype != rtype) {
                return AndroidUtilities.compare(ltype, rtype);
            }
            if (ltype == 0) {
                return AndroidUtilities.compare(lhs.pts, rhs.pts);
            }
            if (ltype == MessagesController.UPDATE_MASK_NAME) {
                return AndroidUtilities.compare(lhs.qts, rhs.qts);
            }
            if (ltype != MessagesController.UPDATE_MASK_AVATAR) {
                return 0;
            }
            int lChannel = MessagesController.this.getUpdateChannelId(lhs);
            int rChannel = MessagesController.this.getUpdateChannelId(rhs);
            if (lChannel == rChannel) {
                return AndroidUtilities.compare(lhs.pts, rhs.pts);
            }
            return AndroidUtilities.compare(lChannel, rChannel);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.30 */
    class AnonymousClass30 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ int val$offset;
        final /* synthetic */ photos_Photos val$res;

        AnonymousClass30(photos_Photos org_telegram_tgnet_TLRPC_photos_Photos, boolean z, int i, int i2, int i3, int i4) {
            this.val$res = org_telegram_tgnet_TLRPC_photos_Photos;
            this.val$fromCache = z;
            this.val$did = i;
            this.val$offset = i2;
            this.val$count = i3;
            this.val$classGuid = i4;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$res.users, this.val$fromCache);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(this.val$did), Integer.valueOf(this.val$offset), Integer.valueOf(this.val$count), Boolean.valueOf(this.val$fromCache), Integer.valueOf(this.val$classGuid), this.val$res.photos);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.35 */
    class AnonymousClass35 implements Runnable {
        final /* synthetic */ long val$did;

        AnonymousClass35(long j) {
            this.val$did = j;
        }

        public void run() {
            MessagesController.this.channelsPts.remove(Integer.valueOf(-((int) this.val$did)));
            MessagesController.this.shortPollChannels.delete(-((int) this.val$did));
            MessagesController.this.needShortPollChannels.delete(-((int) this.val$did));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.36 */
    class AnonymousClass36 implements Runnable {
        final /* synthetic */ long val$did;

        /* renamed from: org.telegram.messenger.MessagesController.36.1 */
        class C05761 implements Runnable {
            C05761() {
            }

            public void run() {
                NotificationsController.getInstance().removeNotificationsForDialog(AnonymousClass36.this.val$did);
            }
        }

        AnonymousClass36(long j) {
            this.val$did = j;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05761());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.3 */
    class C05783 implements Runnable {
        final /* synthetic */ TL_config val$config;

        C05783(TL_config tL_config) {
            this.val$config = tL_config;
        }

        public void run() {
            MessagesController.this.maxMegagroupCount = this.val$config.megagroup_size_max;
            MessagesController.this.maxGroupCount = this.val$config.chat_size_max;
            MessagesController.this.groupBigSize = this.val$config.chat_big_size;
            MessagesController.this.disabledFeatures = this.val$config.disabled_features;
            MessagesController.this.maxEditTime = this.val$config.edit_time_limit;
            MessagesController.this.ratingDecay = this.val$config.rating_e_decay;
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            editor.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
            editor.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
            editor.putInt("groupBigSize", MessagesController.this.groupBigSize);
            editor.putInt("maxEditTime", MessagesController.this.maxEditTime);
            editor.putInt("ratingDecay", MessagesController.this.ratingDecay);
            try {
                SerializedData data = new SerializedData();
                data.writeInt32(MessagesController.this.disabledFeatures.size());
                Iterator i$ = MessagesController.this.disabledFeatures.iterator();
                while (i$.hasNext()) {
                    ((TL_disabledFeature) i$.next()).serializeToStream(data);
                }
                String string = Base64.encodeToString(data.toByteArray(), 0);
                if (string.length() != 0) {
                    editor.putString("disabledFeatures", string);
                }
            } catch (Throwable e) {
                editor.remove("disabledFeatures");
                FileLog.m13e("tmessages", e);
            }
            editor.commit();
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.40 */
    class AnonymousClass40 implements Runnable {
        final /* synthetic */ boolean val$byChannelUsers;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ ChatFull val$info;
        final /* synthetic */ MessageObject val$pinnedMessageObject;
        final /* synthetic */ ArrayList val$usersArr;

        AnonymousClass40(ArrayList arrayList, boolean z, ChatFull chatFull, boolean z2, MessageObject messageObject) {
            this.val$usersArr = arrayList;
            this.val$fromCache = z;
            this.val$info = chatFull;
            this.val$byChannelUsers = z2;
            this.val$pinnedMessageObject = messageObject;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$usersArr, this.val$fromCache);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.chatInfoDidLoaded;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
            objArr[0] = this.val$info;
            objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(0);
            objArr[MessagesController.UPDATE_MASK_AVATAR] = Boolean.valueOf(this.val$byChannelUsers);
            objArr[3] = this.val$pinnedMessageObject;
            instance.postNotificationName(i, objArr);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.46 */
    class AnonymousClass46 implements Runnable {
        final /* synthetic */ HashMap val$newPrintingStrings;
        final /* synthetic */ HashMap val$newPrintingStringsTypes;

        AnonymousClass46(HashMap hashMap, HashMap hashMap2) {
            this.val$newPrintingStrings = hashMap;
            this.val$newPrintingStringsTypes = hashMap2;
        }

        public void run() {
            MessagesController.this.printingStrings = this.val$newPrintingStrings;
            MessagesController.this.printingStringsTypes = this.val$newPrintingStringsTypes;
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.51 */
    class AnonymousClass51 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$first_unread;
        final /* synthetic */ boolean val$isCache;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ boolean val$isEnd;
        final /* synthetic */ int val$last_date;
        final /* synthetic */ int val$last_message_id;
        final /* synthetic */ int val$loadIndex;
        final /* synthetic */ int val$load_type;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ messages_Messages val$messagesRes;
        final /* synthetic */ boolean val$queryFromServer;
        final /* synthetic */ int val$unread_count;

        /* renamed from: org.telegram.messenger.MessagesController.51.1 */
        class C05841 implements Runnable {
            C05841() {
            }

            public void run() {
                MessagesController messagesController = MessagesController.this;
                long j = AnonymousClass51.this.val$dialog_id;
                int i = AnonymousClass51.this.val$count;
                int i2 = (AnonymousClass51.this.val$load_type == MessagesController.UPDATE_MASK_AVATAR && AnonymousClass51.this.val$queryFromServer) ? AnonymousClass51.this.val$first_unread : AnonymousClass51.this.val$max_id;
                messagesController.loadMessages(j, i, i2, false, 0, AnonymousClass51.this.val$classGuid, AnonymousClass51.this.val$load_type, AnonymousClass51.this.val$last_message_id, AnonymousClass51.this.val$isChannel, AnonymousClass51.this.val$loadIndex, AnonymousClass51.this.val$first_unread, AnonymousClass51.this.val$unread_count, AnonymousClass51.this.val$last_date, AnonymousClass51.this.val$queryFromServer);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.51.2 */
        class C05852 implements Runnable {
            final /* synthetic */ ArrayList val$messagesToReload;
            final /* synthetic */ ArrayList val$objects;
            final /* synthetic */ HashMap val$webpagesToReload;

            C05852(ArrayList arrayList, ArrayList arrayList2, HashMap hashMap) {
                this.val$objects = arrayList;
                this.val$messagesToReload = arrayList2;
                this.val$webpagesToReload = hashMap;
            }

            public void run() {
                MessagesController.this.putUsers(AnonymousClass51.this.val$messagesRes.users, AnonymousClass51.this.val$isCache);
                MessagesController.this.putChats(AnonymousClass51.this.val$messagesRes.chats, AnonymousClass51.this.val$isCache);
                int first_unread_final = ConnectionsManager.DEFAULT_DATACENTER_ID;
                if (AnonymousClass51.this.val$queryFromServer && AnonymousClass51.this.val$load_type == MessagesController.UPDATE_MASK_AVATAR) {
                    for (int a = 0; a < AnonymousClass51.this.val$messagesRes.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                        Message message = (Message) AnonymousClass51.this.val$messagesRes.messages.get(a);
                        if (!message.out && message.id > AnonymousClass51.this.val$first_unread && message.id < first_unread_final) {
                            first_unread_final = message.id;
                        }
                    }
                }
                if (first_unread_final == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    first_unread_final = AnonymousClass51.this.val$first_unread;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDidLoaded, Long.valueOf(AnonymousClass51.this.val$dialog_id), Integer.valueOf(AnonymousClass51.this.val$count), this.val$objects, Boolean.valueOf(AnonymousClass51.this.val$isCache), Integer.valueOf(first_unread_final), Integer.valueOf(AnonymousClass51.this.val$last_message_id), Integer.valueOf(AnonymousClass51.this.val$unread_count), Integer.valueOf(AnonymousClass51.this.val$last_date), Integer.valueOf(AnonymousClass51.this.val$load_type), Boolean.valueOf(AnonymousClass51.this.val$isEnd), Integer.valueOf(AnonymousClass51.this.val$classGuid), Integer.valueOf(AnonymousClass51.this.val$loadIndex));
                if (!this.val$messagesToReload.isEmpty()) {
                    MessagesController.this.reloadMessages(this.val$messagesToReload, AnonymousClass51.this.val$dialog_id);
                }
                if (!this.val$webpagesToReload.isEmpty()) {
                    MessagesController.this.reloadWebPages(AnonymousClass51.this.val$dialog_id, this.val$webpagesToReload);
                }
            }
        }

        AnonymousClass51(messages_Messages org_telegram_tgnet_TLRPC_messages_Messages, long j, boolean z, int i, int i2, boolean z2, int i3, int i4, int i5, int i6, boolean z3, int i7, int i8, int i9, boolean z4) {
            this.val$messagesRes = org_telegram_tgnet_TLRPC_messages_Messages;
            this.val$dialog_id = j;
            this.val$isCache = z;
            this.val$count = i;
            this.val$load_type = i2;
            this.val$queryFromServer = z2;
            this.val$first_unread = i3;
            this.val$max_id = i4;
            this.val$classGuid = i5;
            this.val$last_message_id = i6;
            this.val$isChannel = z3;
            this.val$loadIndex = i7;
            this.val$unread_count = i8;
            this.val$last_date = i9;
            this.val$isEnd = z4;
        }

        public void run() {
            int a;
            boolean createDialog = false;
            boolean isMegagroup = false;
            if (this.val$messagesRes instanceof TL_messages_channelMessages) {
                int channelId = -((int) this.val$dialog_id);
                if (((Integer) MessagesController.this.channelsPts.get(Integer.valueOf(channelId))) == null && Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(channelId)).intValue() == 0) {
                    MessagesController.this.channelsPts.put(Integer.valueOf(channelId), Integer.valueOf(this.val$messagesRes.pts));
                    createDialog = true;
                    if (MessagesController.this.needShortPollChannels.indexOfKey(channelId) < 0 || MessagesController.this.shortPollChannels.indexOfKey(channelId) >= 0) {
                        MessagesController.this.getChannelDifference(channelId);
                    } else {
                        MessagesController.this.getChannelDifference(channelId, MessagesController.UPDATE_MASK_AVATAR, 0);
                    }
                }
                for (a = 0; a < this.val$messagesRes.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Chat chat = (Chat) this.val$messagesRes.chats.get(a);
                    if (chat.id == channelId) {
                        isMegagroup = chat.megagroup;
                        break;
                    }
                }
            }
            int lower_id = (int) this.val$dialog_id;
            int high_id = (int) (this.val$dialog_id >> MessagesController.UPDATE_MASK_CHAT_MEMBERS);
            if (!this.val$isCache) {
                ImageLoader.saveMessagesThumbs(this.val$messagesRes.messages);
            }
            if (high_id == MessagesController.UPDATE_MASK_NAME || lower_id == 0 || !this.val$isCache || this.val$messagesRes.messages.size() != 0) {
                Message message;
                AbstractMap usersDict = new HashMap();
                AbstractMap chatsDict = new HashMap();
                for (a = 0; a < this.val$messagesRes.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    User u = (User) this.val$messagesRes.users.get(a);
                    usersDict.put(Integer.valueOf(u.id), u);
                }
                for (a = 0; a < this.val$messagesRes.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Chat c = (Chat) this.val$messagesRes.chats.get(a);
                    chatsDict.put(Integer.valueOf(c.id), c);
                }
                int size = this.val$messagesRes.messages.size();
                if (!this.val$isCache) {
                    Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
                    if (inboxValue == null) {
                        inboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), inboxValue);
                    }
                    Integer outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
                    if (outboxValue == null) {
                        outboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), outboxValue);
                    }
                    for (a = 0; a < size; a += MessagesController.UPDATE_MASK_NAME) {
                        message = (Message) this.val$messagesRes.messages.get(a);
                        if (!(this.val$isCache || !message.post || message.out)) {
                            message.media_unread = true;
                        }
                        if (isMegagroup) {
                            message.flags |= LinearLayoutManager.INVALID_OFFSET;
                        }
                        if (message.action instanceof TL_messageActionChatDeleteUser) {
                            User user = (User) usersDict.get(Integer.valueOf(message.action.user_id));
                            if (user != null && user.bot) {
                                message.reply_markup = new TL_replyKeyboardHide();
                            }
                        }
                        if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                            message.unread = false;
                            message.media_unread = false;
                        } else {
                            message.unread = (message.out ? outboxValue : inboxValue).intValue() < message.id;
                        }
                    }
                    MessagesStorage.getInstance().putMessages(this.val$messagesRes, this.val$dialog_id, this.val$load_type, this.val$max_id, createDialog);
                }
                ArrayList<MessageObject> objects = new ArrayList();
                ArrayList<Integer> messagesToReload = new ArrayList();
                HashMap<String, ArrayList<MessageObject>> webpagesToReload = new HashMap();
                for (a = 0; a < size; a += MessagesController.UPDATE_MASK_NAME) {
                    message = (Message) this.val$messagesRes.messages.get(a);
                    message.dialog_id = this.val$dialog_id;
                    MessageObject messageObject = new MessageObject(message, usersDict, chatsDict, true);
                    objects.add(messageObject);
                    if (this.val$isCache) {
                        if (message.media instanceof TL_messageMediaUnsupported) {
                            if (message.media.bytes != null && (message.media.bytes.length == 0 || (message.media.bytes.length == MessagesController.UPDATE_MASK_NAME && message.media.bytes[0] < 53))) {
                                messagesToReload.add(Integer.valueOf(message.id));
                            }
                        } else if (message.media instanceof TL_messageMediaWebPage) {
                            if ((message.media.webpage instanceof TL_webPagePending) && message.media.webpage.date <= ConnectionsManager.getInstance().getCurrentTime()) {
                                messagesToReload.add(Integer.valueOf(message.id));
                            } else if (message.media.webpage instanceof TL_webPageUrlPending) {
                                ArrayList<MessageObject> arrayList = (ArrayList) webpagesToReload.get(message.media.webpage.url);
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    webpagesToReload.put(message.media.webpage.url, arrayList);
                                }
                                arrayList.add(messageObject);
                            }
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C05852(objects, messagesToReload, webpagesToReload));
                return;
            }
            AndroidUtilities.runOnUIThread(new C05841());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.54 */
    class AnonymousClass54 implements Runnable {
        final /* synthetic */ int val$count;
        final /* synthetic */ messages_Dialogs val$dialogsRes;
        final /* synthetic */ ArrayList val$encChats;
        final /* synthetic */ int val$loadType;
        final /* synthetic */ boolean val$migrate;
        final /* synthetic */ int val$offset;
        final /* synthetic */ boolean val$resetEnd;

        /* renamed from: org.telegram.messenger.MessagesController.54.1 */
        class C05891 implements Runnable {
            C05891() {
            }

            public void run() {
                MessagesController.this.putUsers(AnonymousClass54.this.val$dialogsRes.users, true);
                MessagesController.this.loadingDialogs = false;
                if (AnonymousClass54.this.val$resetEnd) {
                    MessagesController.this.dialogsEndReached = false;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                MessagesController.this.loadDialogs(0, AnonymousClass54.this.val$count, false);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.54.2 */
        class C05902 implements Runnable {
            final /* synthetic */ HashMap val$chatsDict;
            final /* synthetic */ ArrayList val$dialogsToReload;
            final /* synthetic */ HashMap val$new_dialogMessage;
            final /* synthetic */ HashMap val$new_dialogs_dict;

            C05902(HashMap hashMap, HashMap hashMap2, HashMap hashMap3, ArrayList arrayList) {
                this.val$new_dialogs_dict = hashMap;
                this.val$new_dialogMessage = hashMap2;
                this.val$chatsDict = hashMap3;
                this.val$dialogsToReload = arrayList;
            }

            public void run() {
                if (AnonymousClass54.this.val$loadType != MessagesController.UPDATE_MASK_NAME) {
                    MessagesController.this.applyDialogsNotificationsSettings(AnonymousClass54.this.val$dialogsRes.dialogs);
                    if (!UserConfig.draftsLoaded) {
                        DraftQuery.loadDrafts();
                    }
                }
                MessagesController.this.putUsers(AnonymousClass54.this.val$dialogsRes.users, AnonymousClass54.this.val$loadType == MessagesController.UPDATE_MASK_NAME);
                MessagesController.this.putChats(AnonymousClass54.this.val$dialogsRes.chats, AnonymousClass54.this.val$loadType == MessagesController.UPDATE_MASK_NAME);
                if (AnonymousClass54.this.val$encChats != null) {
                    for (int a = 0; a < AnonymousClass54.this.val$encChats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                        EncryptedChat encryptedChat = (EncryptedChat) AnonymousClass54.this.val$encChats.get(a);
                        if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 46) {
                            SecretChatHelper.getInstance().sendNotifyLayerMessage(encryptedChat, null);
                        }
                        MessagesController.this.putEncryptedChat(encryptedChat, true);
                    }
                }
                if (!AnonymousClass54.this.val$migrate) {
                    MessagesController.this.loadingDialogs = false;
                }
                boolean added = false;
                int lastDialogDate = (!AnonymousClass54.this.val$migrate || MessagesController.this.dialogs.isEmpty()) ? 0 : ((TL_dialog) MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
                for (Entry<Long, TL_dialog> pair : this.val$new_dialogs_dict.entrySet()) {
                    Long key = (Long) pair.getKey();
                    TL_dialog value = (TL_dialog) pair.getValue();
                    if (!AnonymousClass54.this.val$migrate || lastDialogDate == 0 || value.last_message_date >= lastDialogDate) {
                        TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(key);
                        if (AnonymousClass54.this.val$loadType != MessagesController.UPDATE_MASK_NAME && (value.draft instanceof TL_draftMessage)) {
                            DraftQuery.saveDraft(value.id, value.draft, null, false);
                        }
                        MessageObject messageObject;
                        if (currentDialog == null) {
                            added = true;
                            MessagesController.this.dialogs_dict.put(key, value);
                            messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(value.id));
                            MessagesController.this.dialogMessage.put(key, messageObject);
                            if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                if (messageObject.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                }
                            }
                        } else {
                            if (AnonymousClass54.this.val$loadType != MessagesController.UPDATE_MASK_NAME) {
                                currentDialog.notify_settings = value.notify_settings;
                            }
                            MessageObject oldMsg = (MessageObject) MessagesController.this.dialogMessage.get(key);
                            if ((oldMsg == null || !oldMsg.deleted) && oldMsg != null && currentDialog.top_message <= 0) {
                                MessageObject newMsg = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(value.id));
                                if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                                    MessagesController.this.dialogs_dict.put(key, value);
                                    MessagesController.this.dialogMessage.put(key, newMsg);
                                    if (newMsg != null && newMsg.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(newMsg.getId()), newMsg);
                                        if (!(newMsg == null || newMsg.messageOwner.random_id == 0)) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(newMsg.messageOwner.random_id), newMsg);
                                        }
                                    }
                                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(oldMsg.getId()));
                                    if (oldMsg.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(oldMsg.messageOwner.random_id));
                                    }
                                }
                            } else if (value.top_message >= currentDialog.top_message) {
                                MessagesController.this.dialogs_dict.put(key, value);
                                messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(value.id));
                                MessagesController.this.dialogMessage.put(key, messageObject);
                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                    if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                    }
                                }
                                if (oldMsg != null) {
                                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(oldMsg.getId()));
                                    if (oldMsg.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(oldMsg.messageOwner.random_id));
                                    }
                                }
                            }
                        }
                    }
                }
                MessagesController.this.dialogs.clear();
                MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
                MessagesController.this.sortDialogs(AnonymousClass54.this.val$migrate ? this.val$chatsDict : null);
                if (!(AnonymousClass54.this.val$loadType == MessagesController.UPDATE_MASK_AVATAR || AnonymousClass54.this.val$migrate)) {
                    MessagesController messagesController = MessagesController.this;
                    boolean z = (AnonymousClass54.this.val$dialogsRes.dialogs.size() == 0 || AnonymousClass54.this.val$dialogsRes.dialogs.size() != AnonymousClass54.this.val$count) && AnonymousClass54.this.val$loadType == 0;
                    messagesController.dialogsEndReached = z;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (AnonymousClass54.this.val$migrate) {
                    UserConfig.migrateOffsetId = AnonymousClass54.this.val$offset;
                    UserConfig.saveConfig(false);
                    MessagesController.this.migratingDialogs = false;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                } else {
                    MessagesController.this.generateUpdateMessage();
                    if (!added && AnonymousClass54.this.val$loadType == MessagesController.UPDATE_MASK_NAME) {
                        MessagesController.this.loadDialogs(0, AnonymousClass54.this.val$count, false);
                    }
                }
                MessagesController.this.migrateDialogs(UserConfig.migrateOffsetId, UserConfig.migrateOffsetDate, UserConfig.migrateOffsetUserId, UserConfig.migrateOffsetChatId, UserConfig.migrateOffsetChannelId, UserConfig.migrateOffsetAccess);
                if (!this.val$dialogsToReload.isEmpty()) {
                    MessagesController.this.reloadDialogsReadValue(this.val$dialogsToReload, 0);
                }
            }
        }

        AnonymousClass54(int i, messages_Dialogs org_telegram_tgnet_TLRPC_messages_Dialogs, boolean z, int i2, int i3, ArrayList arrayList, boolean z2) {
            this.val$loadType = i;
            this.val$dialogsRes = org_telegram_tgnet_TLRPC_messages_Dialogs;
            this.val$resetEnd = z;
            this.val$count = i2;
            this.val$offset = i3;
            this.val$encChats = arrayList;
            this.val$migrate = z2;
        }

        public void run() {
            int i = this.val$loadType;
            int size = this.val$dialogsRes.dialogs.size();
            FileLog.m11e("tmessages", "loaded loadType " + i + " count " + i);
            if (this.val$loadType == MessagesController.UPDATE_MASK_NAME && this.val$dialogsRes.dialogs.size() == 0) {
                AndroidUtilities.runOnUIThread(new C05891());
                return;
            }
            int a;
            HashMap<Long, TL_dialog> new_dialogs_dict = new HashMap();
            HashMap<Long, MessageObject> new_dialogMessage = new HashMap();
            AbstractMap usersDict = new HashMap();
            HashMap<Integer, Chat> chatsDict = new HashMap();
            for (a = 0; a < this.val$dialogsRes.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                User u = (User) this.val$dialogsRes.users.get(a);
                usersDict.put(Integer.valueOf(u.id), u);
            }
            for (a = 0; a < this.val$dialogsRes.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                Chat c = (Chat) this.val$dialogsRes.chats.get(a);
                chatsDict.put(Integer.valueOf(c.id), c);
            }
            if (this.val$loadType == MessagesController.UPDATE_MASK_NAME) {
                MessagesController.this.nextDialogsCacheOffset = this.val$offset + this.val$count;
            }
            for (a = 0; a < this.val$dialogsRes.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                Chat chat;
                Message message = (Message) this.val$dialogsRes.messages.get(a);
                MessageObject messageObject;
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) chatsDict.get(Integer.valueOf(message.to_id.channel_id));
                    if (chat == null || !chat.left) {
                        if (chat != null && chat.megagroup) {
                            message.flags |= LinearLayoutManager.INVALID_OFFSET;
                        }
                        if (!(this.val$loadType == MessagesController.UPDATE_MASK_NAME || !message.post || message.out)) {
                            message.media_unread = true;
                        }
                        messageObject = new MessageObject(message, usersDict, chatsDict, false);
                        new_dialogMessage.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                    }
                } else {
                    if (message.to_id.chat_id != 0) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(message.to_id.chat_id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    message.media_unread = true;
                    messageObject = new MessageObject(message, usersDict, chatsDict, false);
                    new_dialogMessage.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                }
            }
            ArrayList<TL_dialog> dialogsToReload = new ArrayList();
            for (a = 0; a < this.val$dialogsRes.dialogs.size(); a += MessagesController.UPDATE_MASK_NAME) {
                Integer value;
                TL_dialog d = (TL_dialog) this.val$dialogsRes.dialogs.get(a);
                if (d.id == 0 && d.peer != null) {
                    if (d.peer.user_id != 0) {
                        d.id = (long) d.peer.user_id;
                    } else if (d.peer.chat_id != 0) {
                        d.id = (long) (-d.peer.chat_id);
                    } else if (d.peer.channel_id != 0) {
                        d.id = (long) (-d.peer.channel_id);
                    }
                }
                if (d.id != 0) {
                    if (d.last_message_date == 0) {
                        MessageObject mess = (MessageObject) new_dialogMessage.get(Long.valueOf(d.id));
                        if (mess != null) {
                            d.last_message_date = mess.messageOwner.date;
                        }
                    }
                    boolean allowCheck = true;
                    if (DialogObject.isChannel(d)) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(-((int) d.id)));
                        if (chat != null) {
                            if (!chat.megagroup) {
                                allowCheck = false;
                            }
                            if (chat.left) {
                            }
                        }
                        MessagesController.this.channelsPts.put(Integer.valueOf(-((int) d.id)), Integer.valueOf(d.pts));
                    } else if (((int) d.id) < 0) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(-((int) d.id)));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    new_dialogs_dict.put(Long.valueOf(d.id), d);
                    if (allowCheck && this.val$loadType == MessagesController.UPDATE_MASK_NAME && ((d.read_outbox_max_id == 0 || d.read_inbox_max_id == 0) && d.top_message != 0)) {
                        dialogsToReload.add(d);
                    }
                    value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                    value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                }
            }
            if (this.val$loadType != MessagesController.UPDATE_MASK_NAME) {
                ImageLoader.saveMessagesThumbs(this.val$dialogsRes.messages);
                for (a = 0; a < this.val$dialogsRes.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    message = (Message) this.val$dialogsRes.messages.get(a);
                    if (message.action instanceof TL_messageActionChatDeleteUser) {
                        User user = (User) usersDict.get(Integer.valueOf(message.action.user_id));
                        if (user != null && user.bot) {
                            message.reply_markup = new TL_replyKeyboardHide();
                        }
                    }
                    if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                        message.unread = false;
                        message.media_unread = false;
                    } else {
                        boolean z;
                        ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                        value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                        if (value == null) {
                            value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                            read_max.put(Long.valueOf(message.dialog_id), value);
                        }
                        if (value.intValue() < message.id) {
                            z = true;
                        } else {
                            z = false;
                        }
                        message.unread = z;
                    }
                }
                MessagesStorage.getInstance().putDialogs(this.val$dialogsRes);
            }
            if (this.val$loadType == MessagesController.UPDATE_MASK_AVATAR) {
                chat = (Chat) this.val$dialogsRes.chats.get(0);
                MessagesController.this.getChannelDifference(chat.id);
                MessagesController.this.checkChannelInviter(chat.id);
            }
            AndroidUtilities.runOnUIThread(new C05902(new_dialogs_dict, new_dialogMessage, chatsDict, dialogsToReload));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.55 */
    class AnonymousClass55 implements Runnable {
        final /* synthetic */ HashMap val$dialogsToUpdate;

        AnonymousClass55(HashMap hashMap) {
            this.val$dialogsToUpdate = hashMap;
        }

        public void run() {
            for (Entry<Long, Integer> entry : this.val$dialogsToUpdate.entrySet()) {
                TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(entry.getKey());
                if (currentDialog != null) {
                    currentDialog.unread_count = ((Integer) entry.getValue()).intValue();
                }
            }
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.updateInterfaces;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
            objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
            instance.postNotificationName(i, objArr);
            NotificationsController.getInstance().processDialogsUpdateRead(this.val$dialogsToUpdate);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.57 */
    class AnonymousClass57 implements Runnable {
        final /* synthetic */ messages_Dialogs val$dialogsRes;

        /* renamed from: org.telegram.messenger.MessagesController.57.1 */
        class C05931 implements Runnable {
            final /* synthetic */ HashMap val$dialogsToUpdate;
            final /* synthetic */ HashMap val$new_dialogMessage;
            final /* synthetic */ HashMap val$new_dialogs_dict;

            C05931(HashMap hashMap, HashMap hashMap2, HashMap hashMap3) {
                this.val$new_dialogs_dict = hashMap;
                this.val$new_dialogMessage = hashMap2;
                this.val$dialogsToUpdate = hashMap3;
            }

            public void run() {
                MessagesController.this.putUsers(AnonymousClass57.this.val$dialogsRes.users, true);
                MessagesController.this.putChats(AnonymousClass57.this.val$dialogsRes.chats, true);
                for (Entry<Long, TL_dialog> pair : this.val$new_dialogs_dict.entrySet()) {
                    Long key = (Long) pair.getKey();
                    TL_dialog value = (TL_dialog) pair.getValue();
                    TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(key);
                    MessageObject messageObject;
                    if (currentDialog == null) {
                        MessagesController messagesController = MessagesController.this;
                        messagesController.nextDialogsCacheOffset += MessagesController.UPDATE_MASK_NAME;
                        MessagesController.this.dialogs_dict.put(key, value);
                        messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(value.id));
                        MessagesController.this.dialogMessage.put(key, messageObject);
                        if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                            MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                            if (messageObject.messageOwner.random_id != 0) {
                                MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                            }
                        }
                    } else {
                        currentDialog.unread_count = value.unread_count;
                        MessageObject oldMsg = (MessageObject) MessagesController.this.dialogMessage.get(key);
                        if (oldMsg != null && currentDialog.top_message <= 0) {
                            MessageObject newMsg = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(value.id));
                            if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                                MessagesController.this.dialogs_dict.put(key, value);
                                MessagesController.this.dialogMessage.put(key, newMsg);
                                if (newMsg != null && newMsg.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(newMsg.getId()), newMsg);
                                    if (newMsg.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(newMsg.messageOwner.random_id), newMsg);
                                    }
                                }
                                MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(oldMsg.getId()));
                                if (oldMsg.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(oldMsg.messageOwner.random_id));
                                }
                            }
                        } else if ((oldMsg != null && oldMsg.deleted) || value.top_message > currentDialog.top_message) {
                            MessagesController.this.dialogs_dict.put(key, value);
                            messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(value.id));
                            MessagesController.this.dialogMessage.put(key, messageObject);
                            if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                if (messageObject.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                }
                            }
                            if (oldMsg != null) {
                                MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(oldMsg.getId()));
                                if (oldMsg.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(oldMsg.messageOwner.random_id));
                                }
                            }
                            if (messageObject == null) {
                                MessagesController.this.checkLastDialogMessage(value, null, 0);
                            }
                        }
                    }
                }
                MessagesController.this.dialogs.clear();
                MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
                MessagesController.this.sortDialogs(null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                NotificationsController.getInstance().processDialogsUpdateRead(this.val$dialogsToUpdate);
            }
        }

        AnonymousClass57(messages_Dialogs org_telegram_tgnet_TLRPC_messages_Dialogs) {
            this.val$dialogsRes = org_telegram_tgnet_TLRPC_messages_Dialogs;
        }

        public void run() {
            HashMap<Long, TL_dialog> new_dialogs_dict = new HashMap();
            HashMap<Long, MessageObject> new_dialogMessage = new HashMap();
            HashMap<Integer, User> usersDict = new HashMap();
            HashMap<Integer, Chat> chatsDict = new HashMap();
            HashMap<Long, Integer> dialogsToUpdate = new HashMap();
            int a = 0;
            while (true) {
                if (a >= this.val$dialogsRes.users.size()) {
                    break;
                }
                User u = (User) this.val$dialogsRes.users.get(a);
                usersDict.put(Integer.valueOf(u.id), u);
                a += MessagesController.UPDATE_MASK_NAME;
            }
            a = 0;
            while (true) {
                if (a >= this.val$dialogsRes.chats.size()) {
                    break;
                }
                Chat c = (Chat) this.val$dialogsRes.chats.get(a);
                chatsDict.put(Integer.valueOf(c.id), c);
                a += MessagesController.UPDATE_MASK_NAME;
            }
            a = 0;
            while (true) {
                if (a >= this.val$dialogsRes.messages.size()) {
                    break;
                }
                Chat chat;
                Message message = (Message) this.val$dialogsRes.messages.get(a);
                MessageObject messageObject;
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) chatsDict.get(Integer.valueOf(message.to_id.channel_id));
                    if (chat != null && chat.left) {
                    }
                    messageObject = new MessageObject(message, usersDict, chatsDict, false);
                    new_dialogMessage.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                } else {
                    if (message.to_id.chat_id != 0) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(message.to_id.chat_id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    messageObject = new MessageObject(message, usersDict, chatsDict, false);
                    new_dialogMessage.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                }
                a += MessagesController.UPDATE_MASK_NAME;
            }
            a = 0;
            while (true) {
                if (a < this.val$dialogsRes.dialogs.size()) {
                    TL_dialog d = (TL_dialog) this.val$dialogsRes.dialogs.get(a);
                    if (d.id == 0) {
                        if (d.peer.user_id != 0) {
                            d.id = (long) d.peer.user_id;
                        } else {
                            if (d.peer.chat_id != 0) {
                                d.id = (long) (-d.peer.chat_id);
                            } else {
                                if (d.peer.channel_id != 0) {
                                    d.id = (long) (-d.peer.channel_id);
                                }
                            }
                        }
                    }
                    MessageObject mess;
                    Integer value;
                    if (DialogObject.isChannel(d)) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(-((int) d.id)));
                        if (chat != null && chat.left) {
                        }
                        if (d.last_message_date == 0) {
                            mess = (MessageObject) new_dialogMessage.get(Long.valueOf(d.id));
                            if (mess != null) {
                                d.last_message_date = mess.messageOwner.date;
                            }
                        }
                        new_dialogs_dict.put(Long.valueOf(d.id), d);
                        dialogsToUpdate.put(Long.valueOf(d.id), Integer.valueOf(d.unread_count));
                        value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                        value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                    } else {
                        if (((int) d.id) < 0) {
                            chat = (Chat) chatsDict.get(Integer.valueOf(-((int) d.id)));
                            if (!(chat == null || chat.migrated_to == null)) {
                            }
                        }
                        if (d.last_message_date == 0) {
                            mess = (MessageObject) new_dialogMessage.get(Long.valueOf(d.id));
                            if (mess != null) {
                                d.last_message_date = mess.messageOwner.date;
                            }
                        }
                        new_dialogs_dict.put(Long.valueOf(d.id), d);
                        dialogsToUpdate.put(Long.valueOf(d.id), Integer.valueOf(d.unread_count));
                        value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                        value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                    }
                    a += MessagesController.UPDATE_MASK_NAME;
                } else {
                    AndroidUtilities.runOnUIThread(new C05931(new_dialogs_dict, new_dialogMessage, dialogsToUpdate));
                    return;
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.58 */
    class AnonymousClass58 implements Runnable {
        final /* synthetic */ Message val$message;

        AnonymousClass58(Message message) {
            this.val$message = message;
        }

        public void run() {
            int peer;
            SparseArray<ArrayList<Integer>> array = MessagesController.this.channelViewsToSend;
            if (this.val$message.to_id.channel_id != 0) {
                peer = -this.val$message.to_id.channel_id;
            } else if (this.val$message.to_id.chat_id != 0) {
                peer = -this.val$message.to_id.chat_id;
            } else {
                peer = this.val$message.to_id.user_id;
            }
            ArrayList<Integer> ids = (ArrayList) array.get(peer);
            if (ids == null) {
                ids = new ArrayList();
                array.put(peer, ids);
            }
            if (!ids.contains(Integer.valueOf(this.val$message.id))) {
                ids.add(Integer.valueOf(this.val$message.id));
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.5 */
    class C05945 implements Runnable {
        C05945() {
        }

        public void run() {
            MessagesController.this.updatesQueueSeq.clear();
            MessagesController.this.updatesQueuePts.clear();
            MessagesController.this.updatesQueueQts.clear();
            MessagesController.this.gettingUnknownChannels.clear();
            MessagesController.this.updatesStartWaitTimeSeq = 0;
            MessagesController.this.updatesStartWaitTimePts = 0;
            MessagesController.this.updatesStartWaitTimeQts = 0;
            MessagesController.this.createdDialogIds.clear();
            MessagesController.this.gettingDifference = false;
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.60 */
    class AnonymousClass60 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$max_positive_id;
        final /* synthetic */ boolean val$popup;

        /* renamed from: org.telegram.messenger.MessagesController.60.1 */
        class C05951 implements Runnable {
            C05951() {
            }

            public void run() {
                TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(AnonymousClass60.this.val$dialog_id));
                if (dialog != null) {
                    dialog.unread_count = 0;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                    instance.postNotificationName(i, objArr);
                }
                if (AnonymousClass60.this.val$popup) {
                    NotificationsController.getInstance().processReadMessages(null, AnonymousClass60.this.val$dialog_id, 0, AnonymousClass60.this.val$max_positive_id, true);
                    HashMap<Long, Integer> dialogsToUpdate = new HashMap();
                    dialogsToUpdate.put(Long.valueOf(AnonymousClass60.this.val$dialog_id), Integer.valueOf(-1));
                    NotificationsController.getInstance().processDialogsUpdateRead(dialogsToUpdate);
                    return;
                }
                NotificationsController.getInstance().processReadMessages(null, AnonymousClass60.this.val$dialog_id, 0, AnonymousClass60.this.val$max_positive_id, false);
                dialogsToUpdate = new HashMap();
                dialogsToUpdate.put(Long.valueOf(AnonymousClass60.this.val$dialog_id), Integer.valueOf(0));
                NotificationsController.getInstance().processDialogsUpdateRead(dialogsToUpdate);
            }
        }

        AnonymousClass60(long j, boolean z, int i) {
            this.val$dialog_id = j;
            this.val$popup = z;
            this.val$max_positive_id = i;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05951());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.63 */
    class AnonymousClass63 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$max_date;
        final /* synthetic */ boolean val$popup;

        /* renamed from: org.telegram.messenger.MessagesController.63.1 */
        class C05961 implements Runnable {
            C05961() {
            }

            public void run() {
                NotificationsController.getInstance().processReadMessages(null, AnonymousClass63.this.val$dialog_id, AnonymousClass63.this.val$max_date, 0, AnonymousClass63.this.val$popup);
                TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(AnonymousClass63.this.val$dialog_id));
                if (dialog != null) {
                    dialog.unread_count = 0;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                    instance.postNotificationName(i, objArr);
                }
                HashMap<Long, Integer> dialogsToUpdate = new HashMap();
                dialogsToUpdate.put(Long.valueOf(AnonymousClass63.this.val$dialog_id), Integer.valueOf(0));
                NotificationsController.getInstance().processDialogsUpdateRead(dialogsToUpdate);
            }
        }

        AnonymousClass63(long j, int i, boolean z) {
            this.val$dialog_id = j;
            this.val$max_date = i;
            this.val$popup = z;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05961());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.67 */
    class AnonymousClass67 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        AnonymousClass67(int i) {
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

    /* renamed from: org.telegram.messenger.MessagesController.6 */
    class C06046 implements Runnable {
        C06046() {
        }

        public void run() {
            ConnectionsManager.getInstance().setIsUpdating(false);
            MessagesController.this.updatesQueueChannels.clear();
            MessagesController.this.updatesStartWaitTimeChannels.clear();
            MessagesController.this.gettingDifferenceChannels.clear();
            MessagesController.this.channelsPts.clear();
            MessagesController.this.shortPollChannels.clear();
            MessagesController.this.needShortPollChannels.clear();
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.7 */
    class C06137 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ boolean val$set;

        C06137(boolean z, long j) {
            this.val$set = z;
            this.val$dialog_id = j;
        }

        public void run() {
            if (this.val$set) {
                MessagesController.this.createdDialogIds.add(Long.valueOf(this.val$dialog_id));
            } else {
                MessagesController.this.createdDialogIds.remove(Long.valueOf(this.val$dialog_id));
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.8 */
    class C06158 implements Runnable {
        C06158() {
        }

        public void run() {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.updateInterfaces;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
            objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_STATUS);
            instance.postNotificationName(i, objArr);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.90 */
    class AnonymousClass90 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ boolean val$stop;

        AnonymousClass90(boolean z, int i) {
            this.val$stop = z;
            this.val$channelId = i;
        }

        public void run() {
            if (this.val$stop) {
                MessagesController.this.needShortPollChannels.delete(this.val$channelId);
                return;
            }
            MessagesController.this.needShortPollChannels.put(this.val$channelId, 0);
            if (MessagesController.this.shortPollChannels.indexOfKey(this.val$channelId) < 0) {
                MessagesController.this.getChannelDifference(this.val$channelId, MessagesController.UPDATE_MASK_AVATAR, 0);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.93 */
    class AnonymousClass93 implements Runnable {
        final /* synthetic */ ArrayList val$pushMessages;

        /* renamed from: org.telegram.messenger.MessagesController.93.1 */
        class C06311 implements Runnable {
            C06311() {
            }

            public void run() {
                NotificationsController.getInstance().processNewMessages(AnonymousClass93.this.val$pushMessages, true);
            }
        }

        AnonymousClass93(ArrayList arrayList) {
            this.val$pushMessages = arrayList;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C06311());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.94 */
    class AnonymousClass94 implements Runnable {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ ArrayList val$pushMessages;

        AnonymousClass94(int i, ArrayList arrayList) {
            this.val$chat_id = i;
            this.val$pushMessages = arrayList;
        }

        public void run() {
            MessagesController.this.updateInterfaceWithMessages((long) (-this.val$chat_id), this.val$pushMessages);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.95 */
    class AnonymousClass95 implements Runnable {
        final /* synthetic */ int val$chat_id;

        /* renamed from: org.telegram.messenger.MessagesController.95.1 */
        class C16851 implements RequestDelegate {
            final /* synthetic */ Chat val$chat;

            /* renamed from: org.telegram.messenger.MessagesController.95.1.1 */
            class C06321 implements Runnable {
                final /* synthetic */ TL_channels_channelParticipant val$res;

                C06321(TL_channels_channelParticipant tL_channels_channelParticipant) {
                    this.val$res = tL_channels_channelParticipant;
                }

                public void run() {
                    MessagesController.this.putUsers(this.val$res.users, false);
                }
            }

            /* renamed from: org.telegram.messenger.MessagesController.95.1.2 */
            class C06342 implements Runnable {
                final /* synthetic */ ArrayList val$pushMessages;

                /* renamed from: org.telegram.messenger.MessagesController.95.1.2.1 */
                class C06331 implements Runnable {
                    C06331() {
                    }

                    public void run() {
                        NotificationsController.getInstance().processNewMessages(C06342.this.val$pushMessages, true);
                    }
                }

                C06342(ArrayList arrayList) {
                    this.val$pushMessages = arrayList;
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C06331());
                }
            }

            /* renamed from: org.telegram.messenger.MessagesController.95.1.3 */
            class C06353 implements Runnable {
                final /* synthetic */ ArrayList val$pushMessages;

                C06353(ArrayList arrayList) {
                    this.val$pushMessages = arrayList;
                }

                public void run() {
                    MessagesController.this.updateInterfaceWithMessages((long) (-AnonymousClass95.this.val$chat_id), this.val$pushMessages);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            C16851(Chat chat) {
                this.val$chat = chat;
            }

            public void run(TLObject response, TL_error error) {
                TL_channels_channelParticipant res = (TL_channels_channelParticipant) response;
                if (res != null && (res.participant instanceof TL_channelParticipantSelf) && res.participant.inviter_id != UserConfig.getClientUserId()) {
                    if (!this.val$chat.megagroup || !MessagesStorage.getInstance().isMigratedChat(this.val$chat.id)) {
                        AndroidUtilities.runOnUIThread(new C06321(res));
                        MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                        TL_messageService message = new TL_messageService();
                        message.media_unread = true;
                        message.unread = true;
                        message.flags = MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                        message.post = true;
                        if (this.val$chat.megagroup) {
                            message.flags |= LinearLayoutManager.INVALID_OFFSET;
                        }
                        int newMessageId = UserConfig.getNewMessageId();
                        message.id = newMessageId;
                        message.local_id = newMessageId;
                        message.date = res.participant.date;
                        message.action = new TL_messageActionChatAddUser();
                        message.from_id = res.participant.inviter_id;
                        message.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
                        message.to_id = new TL_peerChannel();
                        message.to_id.channel_id = AnonymousClass95.this.val$chat_id;
                        message.dialog_id = (long) (-AnonymousClass95.this.val$chat_id);
                        UserConfig.saveConfig(false);
                        ArrayList<MessageObject> pushMessages = new ArrayList();
                        ArrayList messagesArr = new ArrayList();
                        ConcurrentHashMap<Integer, User> usersDict = new ConcurrentHashMap();
                        for (int a = 0; a < res.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                            User user = (User) res.users.get(a);
                            usersDict.put(Integer.valueOf(user.id), user);
                        }
                        messagesArr.add(message);
                        pushMessages.add(new MessageObject(message, usersDict, true));
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06342(pushMessages));
                        MessagesStorage.getInstance().putMessages(messagesArr, true, true, false, MediaController.getInstance().getAutodownloadMask());
                        AndroidUtilities.runOnUIThread(new C06353(pushMessages));
                    }
                }
            }
        }

        AnonymousClass95(int i) {
            this.val$chat_id = i;
        }

        public void run() {
            Chat chat = MessagesController.this.getChat(Integer.valueOf(this.val$chat_id));
            if (chat != null && ChatObject.isChannel(this.val$chat_id) && !chat.creator) {
                TL_channels_getParticipant req = new TL_channels_getParticipant();
                req.channel = MessagesController.getInputChannel(this.val$chat_id);
                req.user_id = new TL_inputUserSelf();
                ConnectionsManager.getInstance().sendRequest(req, new C16851(chat));
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.96 */
    class AnonymousClass96 implements Runnable {
        final /* synthetic */ ArrayList val$objArr;
        final /* synthetic */ boolean val$printUpdate;
        final /* synthetic */ int val$user_id;

        AnonymousClass96(boolean z, int i, ArrayList arrayList) {
            this.val$printUpdate = z;
            this.val$user_id = i;
            this.val$objArr = arrayList;
        }

        public void run() {
            if (this.val$printUpdate) {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_USER_PRINT);
                instance.postNotificationName(i, objArr);
            }
            MessagesController.this.updateInterfaceWithMessages((long) this.val$user_id, this.val$objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.97 */
    class AnonymousClass97 implements Runnable {
        final /* synthetic */ ArrayList val$objArr;
        final /* synthetic */ boolean val$printUpdate;
        final /* synthetic */ Updates val$updates;

        AnonymousClass97(boolean z, Updates updates, ArrayList arrayList) {
            this.val$printUpdate = z;
            this.val$updates = updates;
            this.val$objArr = arrayList;
        }

        public void run() {
            if (this.val$printUpdate) {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_USER_PRINT);
                instance.postNotificationName(i, objArr);
            }
            MessagesController.this.updateInterfaceWithMessages((long) (-this.val$updates.chat_id), this.val$objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.98 */
    class AnonymousClass98 implements Runnable {
        final /* synthetic */ ArrayList val$objArr;

        /* renamed from: org.telegram.messenger.MessagesController.98.1 */
        class C06361 implements Runnable {
            C06361() {
            }

            public void run() {
                NotificationsController.getInstance().processNewMessages(AnonymousClass98.this.val$objArr, true);
            }
        }

        AnonymousClass98(ArrayList arrayList) {
            this.val$objArr = arrayList;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C06361());
        }
    }

    public static class PrintingUser {
        public SendMessageAction action;
        public long lastTime;
        public int userId;
    }

    /* renamed from: org.telegram.messenger.MessagesController.108 */
    static class AnonymousClass108 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ int val$type;

        /* renamed from: org.telegram.messenger.MessagesController.108.1 */
        class C05651 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C05651(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                try {
                    AnonymousClass108.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                AnonymousClass108.this.val$fragment.setVisibleDialog(null);
                if (this.val$error == null) {
                    TL_contacts_resolvedPeer res = this.val$response;
                    MessagesController.getInstance().putUsers(res.users, false);
                    MessagesController.getInstance().putChats(res.chats, false);
                    MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, false, true);
                    if (!res.chats.isEmpty()) {
                        MessagesController.openChatOrProfileWith(null, (Chat) res.chats.get(0), AnonymousClass108.this.val$fragment, MessagesController.UPDATE_MASK_NAME);
                    } else if (!res.users.isEmpty()) {
                        MessagesController.openChatOrProfileWith((User) res.users.get(0), null, AnonymousClass108.this.val$fragment, AnonymousClass108.this.val$type);
                    }
                } else if (AnonymousClass108.this.val$fragment != null && AnonymousClass108.this.val$fragment.getParentActivity() != null) {
                    try {
                        Toast.makeText(AnonymousClass108.this.val$fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", C0691R.string.NoUsernameFound), 0).show();
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
            }
        }

        AnonymousClass108(ProgressDialog progressDialog, BaseFragment baseFragment, int i) {
            this.val$progressDialog = progressDialog;
            this.val$fragment = baseFragment;
            this.val$type = i;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05651(error, response));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ Chat val$chat;
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ int val$classGuid;

        /* renamed from: org.telegram.messenger.MessagesController.10.1 */
        class C05581 implements Runnable {
            final /* synthetic */ TL_messages_chatFull val$res;

            C05581(TL_messages_chatFull tL_messages_chatFull) {
                this.val$res = tL_messages_chatFull;
            }

            public void run() {
                MessagesController.this.applyDialogNotificationsSettings((long) (-AnonymousClass10.this.val$chat_id), this.val$res.full_chat.notify_settings);
                for (int a = 0; a < this.val$res.full_chat.bot_info.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    BotQuery.putBotInfo((BotInfo) this.val$res.full_chat.bot_info.get(a));
                }
                MessagesController.this.exportedChats.put(Integer.valueOf(AnonymousClass10.this.val$chat_id), this.val$res.full_chat.exported_invite);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(AnonymousClass10.this.val$chat_id));
                MessagesController.this.loadedFullChats.add(Integer.valueOf(AnonymousClass10.this.val$chat_id));
                if (!this.val$res.chats.isEmpty()) {
                    ((Chat) this.val$res.chats.get(0)).address = this.val$res.full_chat.about;
                }
                MessagesController.this.putUsers(this.val$res.users, false);
                MessagesController.this.putChats(this.val$res.chats, false);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatInfoDidLoaded;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                objArr[0] = this.val$res.full_chat;
                objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(AnonymousClass10.this.val$classGuid);
                objArr[MessagesController.UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                objArr[3] = null;
                instance.postNotificationName(i, objArr);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.10.2 */
        class C05592 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05592(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                MessagesController.this.checkChannelError(this.val$error.text, AnonymousClass10.this.val$chat_id);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(AnonymousClass10.this.val$chat_id));
            }
        }

        AnonymousClass10(Chat chat, int i, int i2) {
            this.val$chat = chat;
            this.val$chat_id = i;
            this.val$classGuid = i2;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                TL_messages_chatFull res = (TL_messages_chatFull) response;
                MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
                MessagesStorage.getInstance().updateChatInfo(res.full_chat, false);
                if (ChatObject.isChannel(this.val$chat)) {
                    ArrayList<Update> arrayList;
                    long dialog_id = (long) (-this.val$chat_id);
                    Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, dialog_id));
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(res.full_chat.read_inbox_max_id, value.intValue())));
                    if (value.intValue() == 0) {
                        arrayList = new ArrayList();
                        TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                        update.channel_id = this.val$chat_id;
                        update.max_id = res.full_chat.read_inbox_max_id;
                        arrayList.add(update);
                        MessagesController.this.processUpdateArray(arrayList, null, null, false);
                    }
                    value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, dialog_id));
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(res.full_chat.read_outbox_max_id, value.intValue())));
                    if (value.intValue() == 0) {
                        arrayList = new ArrayList();
                        TL_updateReadChannelOutbox update2 = new TL_updateReadChannelOutbox();
                        update2.channel_id = this.val$chat_id;
                        update2.max_id = res.full_chat.read_outbox_max_id;
                        arrayList.add(update2);
                        MessagesController.this.processUpdateArray(arrayList, null, null, false);
                    }
                }
                AndroidUtilities.runOnUIThread(new C05581(res));
                return;
            }
            AndroidUtilities.runOnUIThread(new C05592(error));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.11 */
    class AnonymousClass11 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ User val$user;

        /* renamed from: org.telegram.messenger.MessagesController.11.1 */
        class C05661 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C05661(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                NotificationCenter instance;
                int i;
                Object[] objArr;
                TL_userFull userFull = this.val$response;
                MessagesController.this.applyDialogNotificationsSettings((long) AnonymousClass11.this.val$user.id, userFull.notify_settings);
                if (userFull.bot_info instanceof TL_botInfo) {
                    BotQuery.putBotInfo(userFull.bot_info);
                }
                if (userFull.about == null || userFull.about.length() <= 0) {
                    MessagesController.this.fullUsersAbout.remove(Integer.valueOf(AnonymousClass11.this.val$user.id));
                } else {
                    MessagesController.this.fullUsersAbout.put(Integer.valueOf(AnonymousClass11.this.val$user.id), userFull.about);
                }
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(AnonymousClass11.this.val$user.id));
                MessagesController.this.loadedFullUsers.add(Integer.valueOf(AnonymousClass11.this.val$user.id));
                String names = AnonymousClass11.this.val$user.first_name + AnonymousClass11.this.val$user.last_name + AnonymousClass11.this.val$user.username;
                ArrayList<User> users = new ArrayList();
                users.add(userFull.user);
                MessagesController.this.putUsers(users, false);
                MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                if (!(names == null || names.equals(userFull.user.first_name + userFull.user.last_name + userFull.user.username))) {
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.updateInterfaces;
                    objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_NAME);
                    instance.postNotificationName(i, objArr);
                }
                if (userFull.bot_info instanceof TL_botInfo) {
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.botInfoDidLoaded;
                    objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                    objArr[0] = userFull.bot_info;
                    objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(AnonymousClass11.this.val$classGuid);
                    instance.postNotificationName(i, objArr);
                }
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.userInfoDidLoaded;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(AnonymousClass11.this.val$user.id);
                instance.postNotificationName(i, objArr);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.11.2 */
        class C05672 implements Runnable {
            C05672() {
            }

            public void run() {
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(AnonymousClass11.this.val$user.id));
            }
        }

        AnonymousClass11(User user, int i) {
            this.val$user = user;
            this.val$classGuid = i;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                AndroidUtilities.runOnUIThread(new C05661(response));
            } else {
                AndroidUtilities.runOnUIThread(new C05672());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.12 */
    class AnonymousClass12 implements RequestDelegate {
        final /* synthetic */ Chat val$chat;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$result;

        /* renamed from: org.telegram.messenger.MessagesController.12.1 */
        class C05681 implements Runnable {
            final /* synthetic */ ArrayList val$objects;

            C05681(ArrayList arrayList) {
                this.val$objects = arrayList;
            }

            public void run() {
                ArrayList<Integer> arrayList = (ArrayList) MessagesController.this.reloadingMessages.get(Long.valueOf(AnonymousClass12.this.val$dialog_id));
                if (arrayList != null) {
                    arrayList.removeAll(AnonymousClass12.this.val$result);
                    if (arrayList.isEmpty()) {
                        MessagesController.this.reloadingMessages.remove(Long.valueOf(AnonymousClass12.this.val$dialog_id));
                    }
                }
                MessageObject dialogObj = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(AnonymousClass12.this.val$dialog_id));
                if (dialogObj != null) {
                    int a = 0;
                    while (a < this.val$objects.size()) {
                        MessageObject obj = (MessageObject) this.val$objects.get(a);
                        if (dialogObj == null || dialogObj.getId() != obj.getId()) {
                            a += MessagesController.UPDATE_MASK_NAME;
                        } else {
                            MessagesController.this.dialogMessage.put(Long.valueOf(AnonymousClass12.this.val$dialog_id), obj);
                            if (obj.messageOwner.to_id.channel_id == 0) {
                                obj = (MessageObject) MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(obj.getId()));
                                if (obj != null) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(obj.getId()), obj);
                                }
                            }
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                    }
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.replaceMessagesObjects;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                objArr[0] = Long.valueOf(AnonymousClass12.this.val$dialog_id);
                objArr[MessagesController.UPDATE_MASK_NAME] = this.val$objects;
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass12(long j, Chat chat, ArrayList arrayList) {
            this.val$dialog_id = j;
            this.val$chat = chat;
            this.val$result = arrayList;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                int a;
                messages_Messages messagesRes = (messages_Messages) response;
                AbstractMap usersLocal = new HashMap();
                for (a = 0; a < messagesRes.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    User u = (User) messagesRes.users.get(a);
                    usersLocal.put(Integer.valueOf(u.id), u);
                }
                HashMap<Integer, Chat> chatsLocal = new HashMap();
                for (a = 0; a < messagesRes.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Chat c = (Chat) messagesRes.chats.get(a);
                    chatsLocal.put(Integer.valueOf(c.id), c);
                }
                Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
                if (inboxValue == null) {
                    inboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), inboxValue);
                }
                Integer outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
                if (outboxValue == null) {
                    outboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), outboxValue);
                }
                ArrayList<MessageObject> objects = new ArrayList();
                for (a = 0; a < messagesRes.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Integer num;
                    Message message = (Message) messagesRes.messages.get(a);
                    if (this.val$chat != null && this.val$chat.megagroup) {
                        message.flags |= LinearLayoutManager.INVALID_OFFSET;
                    }
                    message.dialog_id = this.val$dialog_id;
                    if (message.out) {
                        num = outboxValue;
                    } else {
                        num = inboxValue;
                    }
                    message.unread = num.intValue() < message.id;
                    objects.add(new MessageObject(message, usersLocal, chatsLocal, true));
                }
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                MessagesStorage.getInstance().putMessages(messagesRes, this.val$dialog_id, -1, 0, false);
                AndroidUtilities.runOnUIThread(new C05681(objects));
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.15 */
    class AnonymousClass15 implements RequestDelegate {
        final /* synthetic */ long val$dialogId;

        /* renamed from: org.telegram.messenger.MessagesController.15.1 */
        class C05691 implements Runnable {
            C05691() {
            }

            public void run() {
                MessagesController.this.loadingPeerSettings.remove(Long.valueOf(AnonymousClass15.this.val$dialogId));
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                editor.remove("spam_" + AnonymousClass15.this.val$dialogId);
                editor.putInt("spam3_" + AnonymousClass15.this.val$dialogId, MessagesController.UPDATE_MASK_NAME);
                editor.commit();
            }
        }

        AnonymousClass15(long j) {
            this.val$dialogId = j;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05691());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.16 */
    class AnonymousClass16 implements RequestDelegate {
        final /* synthetic */ long val$dialogId;

        /* renamed from: org.telegram.messenger.MessagesController.16.1 */
        class C05701 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C05701(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                MessagesController.this.loadingPeerSettings.remove(Long.valueOf(AnonymousClass16.this.val$dialogId));
                if (this.val$response != null) {
                    TL_peerSettings res = this.val$response;
                    Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                    if (res.report_spam) {
                        editor.putInt("spam3_" + AnonymousClass16.this.val$dialogId, MessagesController.UPDATE_MASK_AVATAR);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.peerSettingsDidLoaded;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                        objArr[0] = Long.valueOf(AnonymousClass16.this.val$dialogId);
                        instance.postNotificationName(i, objArr);
                    } else {
                        editor.putInt("spam3_" + AnonymousClass16.this.val$dialogId, MessagesController.UPDATE_MASK_NAME);
                    }
                    editor.commit();
                }
            }
        }

        AnonymousClass16(long j) {
            this.val$dialogId = j;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05701(response));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.22 */
    class AnonymousClass22 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ long val$max_id;
        final /* synthetic */ int val$offset;

        AnonymousClass22(int i, int i2, int i3, long j, int i4) {
            this.val$did = i;
            this.val$offset = i2;
            this.val$count = i3;
            this.val$max_id = j;
            this.val$classGuid = i4;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesController.this.processLoadedUserPhotos((photos_Photos) response, this.val$did, this.val$offset, this.val$count, this.val$max_id, false, this.val$classGuid);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.23 */
    class AnonymousClass23 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ long val$max_id;
        final /* synthetic */ int val$offset;

        AnonymousClass23(int i, int i2, int i3, long j, int i4) {
            this.val$did = i;
            this.val$offset = i2;
            this.val$count = i3;
            this.val$max_id = j;
            this.val$classGuid = i4;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                messages_Messages messages = (messages_Messages) response;
                TL_photos_photos res = new TL_photos_photos();
                res.count = messages.count;
                res.users.addAll(messages.users);
                for (int a = 0; a < messages.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Message message = (Message) messages.messages.get(a);
                    if (!(message.action == null || message.action.photo == null)) {
                        res.photos.add(message.action.photo);
                    }
                }
                MessagesController.this.processLoadedUserPhotos(res, this.val$did, this.val$offset, this.val$count, this.val$max_id, false, this.val$classGuid);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.24 */
    class AnonymousClass24 implements RequestDelegate {
        final /* synthetic */ User val$user;

        AnonymousClass24(User user) {
            this.val$user = user;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                ArrayList<Integer> ids = new ArrayList();
                ids.add(Integer.valueOf(this.val$user.id));
                MessagesStorage.getInstance().putBlockedUsers(ids, false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.25 */
    class AnonymousClass25 implements RequestDelegate {
        final /* synthetic */ User val$user;

        AnonymousClass25(User user) {
            this.val$user = user;
        }

        public void run(TLObject response, TL_error error) {
            MessagesStorage.getInstance().deleteBlockedUser(this.val$user.id);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.31 */
    class AnonymousClass31 implements RequestDelegate {
        final /* synthetic */ int val$channelId;

        AnonymousClass31(int i) {
            this.val$channelId = i;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                MessagesController.this.processNewChannelDifferenceParams(res.pts, res.pts_count, this.val$channelId);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.34 */
    class AnonymousClass34 implements RequestDelegate {
        final /* synthetic */ Chat val$chat;
        final /* synthetic */ User val$user;

        AnonymousClass34(Chat chat, User user) {
            this.val$chat = chat;
            this.val$user = user;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
                if (res.offset > 0) {
                    MessagesController.this.deleteUserChannelHistory(this.val$chat, this.val$user, res.offset);
                }
                MessagesController.this.processNewChannelDifferenceParams(res.pts, res.pts_count, this.val$chat.id);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.37 */
    class AnonymousClass37 implements RequestDelegate {
        final /* synthetic */ long val$did;
        final /* synthetic */ int val$max_id_delete_final;
        final /* synthetic */ int val$onlyHistory;

        AnonymousClass37(long j, int i, int i2) {
            this.val$did = j;
            this.val$onlyHistory = i;
            this.val$max_id_delete_final = i2;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
                if (res.offset > 0) {
                    MessagesController.this.deleteDialog(this.val$did, false, this.val$onlyHistory, this.val$max_id_delete_final);
                }
                MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.39 */
    class AnonymousClass39 implements RequestDelegate {
        final /* synthetic */ Integer val$chat_id;

        /* renamed from: org.telegram.messenger.MessagesController.39.1 */
        class C05771 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C05771(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_channels_channelParticipants res = this.val$response;
                    MessagesController.this.putUsers(res.users, false);
                    MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                    MessagesStorage.getInstance().updateChannelUsers(AnonymousClass39.this.val$chat_id.intValue(), res.participants);
                    MessagesController.this.loadedFullParticipants.add(AnonymousClass39.this.val$chat_id);
                }
                MessagesController.this.loadingFullParticipants.remove(AnonymousClass39.this.val$chat_id);
            }
        }

        AnonymousClass39(Integer num) {
            this.val$chat_id = num;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05771(error, response));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.43 */
    class AnonymousClass43 implements RequestDelegate {
        final /* synthetic */ int val$key;
        final /* synthetic */ TL_messages_getMessagesViews val$req;

        /* renamed from: org.telegram.messenger.MessagesController.43.1 */
        class C05801 implements Runnable {
            final /* synthetic */ SparseArray val$channelViews;

            C05801(SparseArray sparseArray) {
                this.val$channelViews = sparseArray;
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.didUpdatedMessagesViews;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = this.val$channelViews;
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass43(int i, TL_messages_getMessagesViews tL_messages_getMessagesViews) {
            this.val$key = i;
            this.val$req = tL_messages_getMessagesViews;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                Vector vector = (Vector) response;
                SparseArray<SparseIntArray> channelViews = new SparseArray();
                SparseIntArray array = (SparseIntArray) channelViews.get(this.val$key);
                if (array == null) {
                    array = new SparseIntArray();
                    channelViews.put(this.val$key, array);
                }
                int a = 0;
                while (a < this.val$req.id.size() && a < vector.objects.size()) {
                    array.put(((Integer) this.val$req.id.get(a)).intValue(), ((Integer) vector.objects.get(a)).intValue());
                    a += MessagesController.UPDATE_MASK_NAME;
                }
                MessagesStorage.getInstance().putChannelViews(channelViews, this.val$req.peer instanceof TL_inputPeerChannel);
                AndroidUtilities.runOnUIThread(new C05801(channelViews));
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.47 */
    class AnonymousClass47 implements RequestDelegate {
        final /* synthetic */ int val$action;
        final /* synthetic */ long val$dialog_id;

        /* renamed from: org.telegram.messenger.MessagesController.47.1 */
        class C05811 implements Runnable {
            C05811() {
            }

            public void run() {
                HashMap<Long, Boolean> typings = (HashMap) MessagesController.this.sendingTypings.get(Integer.valueOf(AnonymousClass47.this.val$action));
                if (typings != null) {
                    typings.remove(Long.valueOf(AnonymousClass47.this.val$dialog_id));
                }
            }
        }

        AnonymousClass47(int i, long j) {
            this.val$action = i;
            this.val$dialog_id = j;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05811());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.48 */
    class AnonymousClass48 implements RequestDelegate {
        final /* synthetic */ int val$action;
        final /* synthetic */ long val$dialog_id;

        /* renamed from: org.telegram.messenger.MessagesController.48.1 */
        class C05821 implements Runnable {
            C05821() {
            }

            public void run() {
                HashMap<Long, Boolean> typings = (HashMap) MessagesController.this.sendingTypings.get(Integer.valueOf(AnonymousClass48.this.val$action));
                if (typings != null) {
                    typings.remove(Long.valueOf(AnonymousClass48.this.val$dialog_id));
                }
            }
        }

        AnonymousClass48(int i, long j) {
            this.val$action = i;
            this.val$dialog_id = j;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05821());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.49 */
    class AnonymousClass49 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$first_unread;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$last_date;
        final /* synthetic */ int val$last_message_id;
        final /* synthetic */ int val$loadIndex;
        final /* synthetic */ int val$load_type;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ boolean val$queryFromServer;
        final /* synthetic */ int val$unread_count;

        AnonymousClass49(int i, long j, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z, int i9, boolean z2) {
            this.val$count = i;
            this.val$dialog_id = j;
            this.val$max_id = i2;
            this.val$classGuid = i3;
            this.val$first_unread = i4;
            this.val$last_message_id = i5;
            this.val$unread_count = i6;
            this.val$last_date = i7;
            this.val$load_type = i8;
            this.val$isChannel = z;
            this.val$loadIndex = i9;
            this.val$queryFromServer = z2;
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                messages_Messages res = (messages_Messages) response;
                if (res.messages.size() > this.val$count) {
                    res.messages.remove(0);
                }
                MessagesController.this.processLoadedMessages(res, this.val$dialog_id, this.val$count, this.val$max_id, false, this.val$classGuid, this.val$first_unread, this.val$last_message_id, this.val$unread_count, this.val$last_date, this.val$load_type, this.val$isChannel, false, this.val$loadIndex, this.val$queryFromServer);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.4 */
    class C16844 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.MessagesController.4.1 */
        class C05791 implements Runnable {
            C05791() {
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_AVATAR);
                instance.postNotificationName(i, objArr);
                UserConfig.saveConfig(true);
            }
        }

        C16844() {
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user == null) {
                    user = UserConfig.getCurrentUser();
                    MessagesController.this.putUser(user, true);
                } else {
                    UserConfig.setCurrentUser(user);
                }
                if (user != null) {
                    TL_photos_photo photo = (TL_photos_photo) response;
                    ArrayList<PhotoSize> sizes = photo.photo.sizes;
                    PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
                    PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
                    user.photo = new TL_userProfilePhoto();
                    user.photo.photo_id = photo.photo.id;
                    if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    if (bigSize != null) {
                        user.photo.photo_big = bigSize.location;
                    } else if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    MessagesStorage.getInstance().clearUserPhotos(user.id);
                    ArrayList<User> users = new ArrayList();
                    users.add(user);
                    MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                    AndroidUtilities.runOnUIThread(new C05791());
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.50 */
    class AnonymousClass50 implements RequestDelegate {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ String val$url;

        /* renamed from: org.telegram.messenger.MessagesController.50.1 */
        class C05831 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C05831(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                ArrayList<MessageObject> arrayList = (ArrayList) MessagesController.this.reloadingWebpages.remove(AnonymousClass50.this.val$url);
                if (arrayList != null) {
                    messages_Messages messagesRes = new TL_messages_messages();
                    int a;
                    if (this.val$response instanceof TL_messageMediaWebPage) {
                        TL_messageMediaWebPage media = this.val$response;
                        if ((media.webpage instanceof TL_webPage) || (media.webpage instanceof TL_webPageEmpty)) {
                            for (a = 0; a < arrayList.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = media.webpage;
                                if (a == 0) {
                                    ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(a)).messageOwner);
                                }
                                messagesRes.messages.add(((MessageObject) arrayList.get(a)).messageOwner);
                            }
                        } else {
                            MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(media.webpage.id), arrayList);
                        }
                    } else {
                        for (a = 0; a < arrayList.size(); a += MessagesController.UPDATE_MASK_NAME) {
                            ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = new TL_webPageEmpty();
                            messagesRes.messages.add(((MessageObject) arrayList.get(a)).messageOwner);
                        }
                    }
                    if (!messagesRes.messages.isEmpty()) {
                        MessagesStorage.getInstance().putMessages(messagesRes, AnonymousClass50.this.val$dialog_id, -2, 0, false);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.replaceMessagesObjects;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                        objArr[0] = Long.valueOf(AnonymousClass50.this.val$dialog_id);
                        objArr[MessagesController.UPDATE_MASK_NAME] = arrayList;
                        instance.postNotificationName(i, objArr);
                    }
                }
            }
        }

        AnonymousClass50(String str, long j) {
            this.val$url = str;
            this.val$dialog_id = j;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C05831(response));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.52 */
    class AnonymousClass52 implements RequestDelegate {
        final /* synthetic */ int val$count;

        AnonymousClass52(int i) {
            this.val$count = i;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesController.this.processLoadedDialogs((messages_Dialogs) response, null, 0, this.val$count, 0, false, false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.56 */
    class AnonymousClass56 implements RequestDelegate {
        final /* synthetic */ TL_dialog val$dialog;
        final /* synthetic */ int val$lower_id;
        final /* synthetic */ long val$newTaskId;

        /* renamed from: org.telegram.messenger.MessagesController.56.1 */
        class C05911 implements Runnable {
            C05911() {
            }

            public void run() {
                TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(AnonymousClass56.this.val$dialog.id));
                if (currentDialog != null && currentDialog.top_message == 0) {
                    MessagesController.this.deleteDialog(AnonymousClass56.this.val$dialog.id, 3);
                }
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.56.2 */
        class C05922 implements Runnable {
            C05922() {
            }

            public void run() {
                MessagesController.this.checkingLastMessagesDialogs.remove(Integer.valueOf(AnonymousClass56.this.val$lower_id));
            }
        }

        AnonymousClass56(TL_dialog tL_dialog, long j, int i) {
            this.val$dialog = tL_dialog;
            this.val$newTaskId = j;
            this.val$lower_id = i;
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                messages_Messages res = (messages_Messages) response;
                if (res.messages.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C05911());
                } else {
                    TL_messages_dialogs dialogs = new TL_messages_dialogs();
                    Message newMessage = (Message) res.messages.get(0);
                    TL_dialog newDialog = new TL_dialog();
                    newDialog.flags = this.val$dialog.flags;
                    newDialog.top_message = newMessage.id;
                    newDialog.last_message_date = newMessage.date;
                    newDialog.notify_settings = this.val$dialog.notify_settings;
                    newDialog.pts = this.val$dialog.pts;
                    newDialog.unread_count = this.val$dialog.unread_count;
                    newDialog.read_inbox_max_id = this.val$dialog.read_inbox_max_id;
                    newDialog.read_outbox_max_id = this.val$dialog.read_outbox_max_id;
                    long j = this.val$dialog.id;
                    newDialog.id = j;
                    newMessage.dialog_id = j;
                    dialogs.users.addAll(res.users);
                    dialogs.chats.addAll(res.chats);
                    dialogs.dialogs.add(newDialog);
                    dialogs.messages.addAll(res.messages);
                    dialogs.count = MessagesController.UPDATE_MASK_NAME;
                    MessagesController.this.processDialogsUpdate(dialogs, null);
                    MessagesStorage.getInstance().putMessages(res.messages, true, true, false, MediaController.getInstance().getAutodownloadMask(), true);
                }
            }
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
            AndroidUtilities.runOnUIThread(new C05922());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.64 */
    class AnonymousClass64 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: org.telegram.messenger.MessagesController.64.1 */
        class C05971 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05971(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                    AlertsCreator.showFloodWaitAlert(this.val$error.text, AnonymousClass64.this.val$fragment);
                } else {
                    AlertsCreator.showAddUserAlert(this.val$error.text, AnonymousClass64.this.val$fragment, false);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.64.2 */
        class C05982 implements Runnable {
            final /* synthetic */ Updates val$updates;

            C05982(Updates updates) {
                this.val$updates = updates;
            }

            public void run() {
                MessagesController.this.putUsers(this.val$updates.users, false);
                MessagesController.this.putChats(this.val$updates.chats, false);
                if (this.val$updates.chats == null || this.val$updates.chats.isEmpty()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                    return;
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatDidCreated;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(((Chat) this.val$updates.chats.get(0)).id);
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass64(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run(TLObject response, TL_error error) {
            if (error != null) {
                AndroidUtilities.runOnUIThread(new C05971(error));
                return;
            }
            Updates updates = (Updates) response;
            MessagesController.this.processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new C05982(updates));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.65 */
    class AnonymousClass65 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: org.telegram.messenger.MessagesController.65.1 */
        class C05991 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05991(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                    AlertsCreator.showFloodWaitAlert(this.val$error.text, AnonymousClass65.this.val$fragment);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.65.2 */
        class C06002 implements Runnable {
            final /* synthetic */ Updates val$updates;

            C06002(Updates updates) {
                this.val$updates = updates;
            }

            public void run() {
                MessagesController.this.putUsers(this.val$updates.users, false);
                MessagesController.this.putChats(this.val$updates.chats, false);
                if (this.val$updates.chats == null || this.val$updates.chats.isEmpty()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                    return;
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatDidCreated;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(((Chat) this.val$updates.chats.get(0)).id);
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass65(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run(TLObject response, TL_error error) {
            if (error != null) {
                AndroidUtilities.runOnUIThread(new C05991(error));
                return;
            }
            Updates updates = (Updates) response;
            MessagesController.this.processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new C06002(updates));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.66 */
    class AnonymousClass66 implements RequestDelegate {
        final /* synthetic */ Context val$context;
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: org.telegram.messenger.MessagesController.66.1 */
        class C06011 implements Runnable {
            C06011() {
            }

            public void run() {
                if (!((Activity) AnonymousClass66.this.val$context).isFinishing()) {
                    try {
                        AnonymousClass66.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.66.2 */
        class C06022 implements Runnable {
            C06022() {
            }

            public void run() {
                if (!((Activity) AnonymousClass66.this.val$context).isFinishing()) {
                    try {
                        AnonymousClass66.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    Builder builder = new Builder(AnonymousClass66.this.val$context);
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                    builder.show().setCanceledOnTouchOutside(true);
                }
            }
        }

        AnonymousClass66(Context context, ProgressDialog progressDialog) {
            this.val$context = context;
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                AndroidUtilities.runOnUIThread(new C06011());
                Updates updates = (Updates) response;
                MessagesController.this.processUpdates((Updates) response, false);
                return;
            }
            AndroidUtilities.runOnUIThread(new C06022());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.68 */
    class AnonymousClass68 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: org.telegram.messenger.MessagesController.68.1 */
        class C06031 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C06031(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (AnonymousClass68.this.val$fragment != null) {
                    AlertsCreator.showAddUserAlert(this.val$error.text, AnonymousClass68.this.val$fragment, true);
                } else if (this.val$error.text.equals("PEER_FLOOD")) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.needShowAlert;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_NAME);
                    instance.postNotificationName(i, objArr);
                }
            }
        }

        AnonymousClass68(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run(TLObject response, TL_error error) {
            if (error != null) {
                AndroidUtilities.runOnUIThread(new C06031(error));
            } else {
                MessagesController.this.processUpdates((Updates) response, false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.71 */
    class AnonymousClass71 implements RequestDelegate {
        final /* synthetic */ String val$about;
        final /* synthetic */ ChatFull val$info;

        /* renamed from: org.telegram.messenger.MessagesController.71.1 */
        class C06061 implements Runnable {
            C06061() {
            }

            public void run() {
                AnonymousClass71.this.val$info.about = AnonymousClass71.this.val$about;
                MessagesStorage.getInstance().updateChatInfo(AnonymousClass71.this.val$info, false);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatInfoDidLoaded;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                objArr[0] = AnonymousClass71.this.val$info;
                objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(0);
                objArr[MessagesController.UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                objArr[3] = null;
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass71(ChatFull chatFull, String str) {
            this.val$info = chatFull;
            this.val$about = str;
        }

        public void run(TLObject response, TL_error error) {
            if (response instanceof TL_boolTrue) {
                AndroidUtilities.runOnUIThread(new C06061());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.72 */
    class AnonymousClass72 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ String val$userName;

        /* renamed from: org.telegram.messenger.MessagesController.72.1 */
        class C06071 implements Runnable {
            C06071() {
            }

            public void run() {
                Chat chat = MessagesController.this.getChat(Integer.valueOf(AnonymousClass72.this.val$chat_id));
                if (AnonymousClass72.this.val$userName.length() != 0) {
                    chat.flags |= MessagesController.UPDATE_MASK_USER_PRINT;
                } else {
                    chat.flags &= -65;
                }
                chat.username = AnonymousClass72.this.val$userName;
                ArrayList<Chat> arrayList = new ArrayList();
                arrayList.add(chat);
                MessagesStorage.getInstance().putUsersAndChats(null, arrayList, true, true);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL);
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass72(int i, String str) {
            this.val$chat_id = i;
            this.val$userName = str;
        }

        public void run(TLObject response, TL_error error) {
            if (response instanceof TL_boolTrue) {
                AndroidUtilities.runOnUIThread(new C06071());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.74 */
    class AnonymousClass74 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;

        AnonymousClass74(int i) {
            this.val$chat_id = i;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesController.this.processUpdates((Updates) response, false);
                MessagesController.this.loadFullChat(this.val$chat_id, 0, true);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.76 */
    class AnonymousClass76 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ InputUser val$inputUser;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ boolean val$isMegagroup;

        /* renamed from: org.telegram.messenger.MessagesController.76.1 */
        class C06081 implements Runnable {
            C06081() {
            }

            public void run() {
                MessagesController.this.joiningToChannels.remove(Integer.valueOf(AnonymousClass76.this.val$chat_id));
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.76.2 */
        class C06092 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C06092(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                boolean z = true;
                if (AnonymousClass76.this.val$fragment != null) {
                    String str = this.val$error.text;
                    BaseFragment baseFragment = AnonymousClass76.this.val$fragment;
                    if (!AnonymousClass76.this.val$isChannel || AnonymousClass76.this.val$isMegagroup) {
                        z = false;
                    }
                    AlertsCreator.showAddUserAlert(str, baseFragment, z);
                } else if (this.val$error.text.equals("PEER_FLOOD")) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.needShowAlert;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_NAME);
                    instance.postNotificationName(i, objArr);
                }
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.76.3 */
        class C06103 implements Runnable {
            C06103() {
            }

            public void run() {
                MessagesController.this.loadFullChat(AnonymousClass76.this.val$chat_id, 0, true);
            }
        }

        AnonymousClass76(boolean z, InputUser inputUser, int i, BaseFragment baseFragment, boolean z2) {
            this.val$isChannel = z;
            this.val$inputUser = inputUser;
            this.val$chat_id = i;
            this.val$fragment = baseFragment;
            this.val$isMegagroup = z2;
        }

        public void run(TLObject response, TL_error error) {
            if (this.val$isChannel && (this.val$inputUser instanceof TL_inputUserSelf)) {
                AndroidUtilities.runOnUIThread(new C06081());
            }
            if (error != null) {
                AndroidUtilities.runOnUIThread(new C06092(error));
                return;
            }
            boolean hasJoinMessage = false;
            Updates updates = (Updates) response;
            for (int a = 0; a < updates.updates.size(); a += MessagesController.UPDATE_MASK_NAME) {
                Update update = (Update) updates.updates.get(a);
                if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                    hasJoinMessage = true;
                    break;
                }
            }
            MessagesController.this.processUpdates(updates, false);
            if (this.val$isChannel) {
                if (!hasJoinMessage && (this.val$inputUser instanceof TL_inputUserSelf)) {
                    MessagesController.this.generateJoinMessage(this.val$chat_id, true);
                }
                AndroidUtilities.runOnUIThread(new C06103(), 1000);
            }
            if (this.val$isChannel && (this.val$inputUser instanceof TL_inputUserSelf)) {
                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), true, this.val$chat_id);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.77 */
    class AnonymousClass77 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ InputUser val$inputUser;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ User val$user;

        /* renamed from: org.telegram.messenger.MessagesController.77.1 */
        class C06111 implements Runnable {
            C06111() {
            }

            public void run() {
                MessagesController.this.deleteDialog((long) (-AnonymousClass77.this.val$chat_id), 0);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.77.2 */
        class C06122 implements Runnable {
            C06122() {
            }

            public void run() {
                MessagesController.this.loadFullChat(AnonymousClass77.this.val$chat_id, 0, true);
            }
        }

        AnonymousClass77(User user, int i, boolean z, InputUser inputUser) {
            this.val$user = user;
            this.val$chat_id = i;
            this.val$isChannel = z;
            this.val$inputUser = inputUser;
        }

        public void run(TLObject response, TL_error error) {
            if (this.val$user.id == UserConfig.getClientUserId()) {
                AndroidUtilities.runOnUIThread(new C06111());
            }
            if (error == null) {
                MessagesController.this.processUpdates((Updates) response, false);
                if (this.val$isChannel && !(this.val$inputUser instanceof TL_inputUserSelf)) {
                    AndroidUtilities.runOnUIThread(new C06122(), 1000);
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.83 */
    class AnonymousClass83 implements RequestDelegate {
        final /* synthetic */ String val$regid;

        /* renamed from: org.telegram.messenger.MessagesController.83.1 */
        class C06141 implements Runnable {
            C06141() {
            }

            public void run() {
                MessagesController.this.registeringForPush = false;
            }
        }

        AnonymousClass83(String str) {
            this.val$regid = str;
        }

        public void run(TLObject response, TL_error error) {
            if (response instanceof TL_boolTrue) {
                FileLog.m11e("tmessages", "registered for push");
                UserConfig.registeredForPush = true;
                UserConfig.pushString = this.val$regid;
                UserConfig.saveConfig(false);
            }
            AndroidUtilities.runOnUIThread(new C06141());
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.89 */
    class AnonymousClass89 implements RequestDelegate {
        final /* synthetic */ Chat val$channel;
        final /* synthetic */ long val$newTaskId;

        AnonymousClass89(long j, Chat chat) {
            this.val$newTaskId = j;
            this.val$channel = chat;
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                if (!(res.dialogs.isEmpty() || res.chats.isEmpty())) {
                    TL_messages_dialogs dialogs = new TL_messages_dialogs();
                    dialogs.dialogs.addAll(res.dialogs);
                    dialogs.messages.addAll(res.messages);
                    dialogs.users.addAll(res.users);
                    dialogs.chats.addAll(res.chats);
                    MessagesController.this.processLoadedDialogs(dialogs, null, 0, MessagesController.UPDATE_MASK_NAME, MessagesController.UPDATE_MASK_AVATAR, false, false);
                }
            }
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
            MessagesController.this.gettingUnknownChannels.remove(Integer.valueOf(this.val$channel.id));
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.91 */
    class AnonymousClass91 implements RequestDelegate {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$newDialogType;
        final /* synthetic */ long val$newTaskId;

        /* renamed from: org.telegram.messenger.MessagesController.91.1 */
        class C06161 implements Runnable {
            final /* synthetic */ updates_ChannelDifference val$res;

            C06161(updates_ChannelDifference org_telegram_tgnet_TLRPC_updates_ChannelDifference) {
                this.val$res = org_telegram_tgnet_TLRPC_updates_ChannelDifference;
            }

            public void run() {
                MessagesController.this.putUsers(this.val$res.users, false);
                MessagesController.this.putChats(this.val$res.chats, false);
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.91.2 */
        class C06222 implements Runnable {
            final /* synthetic */ Chat val$channelFinal;
            final /* synthetic */ ArrayList val$msgUpdates;
            final /* synthetic */ updates_ChannelDifference val$res;
            final /* synthetic */ HashMap val$usersDict;

            /* renamed from: org.telegram.messenger.MessagesController.91.2.1 */
            class C06171 implements Runnable {
                final /* synthetic */ HashMap val$corrected;

                C06171(HashMap hashMap) {
                    this.val$corrected = hashMap;
                }

                public void run() {
                    for (Entry<Integer, long[]> entry : this.val$corrected.entrySet()) {
                        Integer newId = (Integer) entry.getKey();
                        long[] ids = (long[]) entry.getValue();
                        Integer oldId = Integer.valueOf((int) ids[MessagesController.UPDATE_MASK_NAME]);
                        SendMessagesHelper.getInstance().processSentMessage(oldId.intValue());
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.messageReceivedByServer;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                        objArr[0] = oldId;
                        objArr[MessagesController.UPDATE_MASK_NAME] = newId;
                        objArr[MessagesController.UPDATE_MASK_AVATAR] = null;
                        objArr[3] = Long.valueOf(ids[0]);
                        instance.postNotificationName(i, objArr);
                    }
                }
            }

            /* renamed from: org.telegram.messenger.MessagesController.91.2.2 */
            class C06212 implements Runnable {

                /* renamed from: org.telegram.messenger.MessagesController.91.2.2.1 */
                class C06181 implements Runnable {
                    final /* synthetic */ HashMap val$messages;

                    C06181(HashMap hashMap) {
                        this.val$messages = hashMap;
                    }

                    public void run() {
                        for (Entry<Long, ArrayList<MessageObject>> pair : this.val$messages.entrySet()) {
                            ArrayList<MessageObject> value = (ArrayList) pair.getValue();
                            MessagesController.this.updateInterfaceWithMessages(((Long) pair.getKey()).longValue(), value);
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                }

                /* renamed from: org.telegram.messenger.MessagesController.91.2.2.2 */
                class C06202 implements Runnable {
                    final /* synthetic */ ArrayList val$pushMessages;

                    /* renamed from: org.telegram.messenger.MessagesController.91.2.2.2.1 */
                    class C06191 implements Runnable {
                        C06191() {
                        }

                        public void run() {
                            NotificationsController.getInstance().processNewMessages(C06202.this.val$pushMessages, true);
                        }
                    }

                    C06202(ArrayList arrayList) {
                        this.val$pushMessages = arrayList;
                    }

                    public void run() {
                        if (!this.val$pushMessages.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new C06191());
                        }
                        MessagesStorage.getInstance().putMessages(C06222.this.val$res.new_messages, true, false, false, MediaController.getInstance().getAutodownloadMask());
                    }
                }

                C06212() {
                }

                public void run() {
                    long dialog_id;
                    Integer inboxValue;
                    Integer outboxValue;
                    int a;
                    Message message;
                    Integer num;
                    boolean z;
                    if ((C06222.this.val$res instanceof TL_updates_channelDifference) || (C06222.this.val$res instanceof TL_updates_channelDifferenceEmpty)) {
                        if (!C06222.this.val$res.new_messages.isEmpty()) {
                            HashMap<Long, ArrayList<MessageObject>> messages = new HashMap();
                            ImageLoader.saveMessagesThumbs(C06222.this.val$res.new_messages);
                            ArrayList<MessageObject> pushMessages = new ArrayList();
                            dialog_id = (long) (-AnonymousClass91.this.val$channelId);
                            inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                            if (inboxValue == null) {
                                inboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, dialog_id));
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
                            }
                            outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                            if (outboxValue == null) {
                                outboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, dialog_id));
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
                            }
                            for (a = 0; a < C06222.this.val$res.new_messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                MessageObject obj;
                                long uid;
                                ArrayList<MessageObject> arr;
                                message = (Message) C06222.this.val$res.new_messages.get(a);
                                if (C06222.this.val$channelFinal == null || !C06222.this.val$channelFinal.left) {
                                    if (message.out) {
                                        num = outboxValue;
                                    } else {
                                        num = inboxValue;
                                    }
                                    if (num.intValue() < message.id && !(message.action instanceof TL_messageActionChannelCreate)) {
                                        z = true;
                                        message.unread = z;
                                        if (C06222.this.val$channelFinal != null && C06222.this.val$channelFinal.megagroup) {
                                            message.flags |= LinearLayoutManager.INVALID_OFFSET;
                                        }
                                        obj = new MessageObject(message, C06222.this.val$usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(dialog_id)));
                                        if (!obj.isOut() && obj.isUnread()) {
                                            pushMessages.add(obj);
                                        }
                                        uid = (long) (-AnonymousClass91.this.val$channelId);
                                        arr = (ArrayList) messages.get(Long.valueOf(uid));
                                        if (arr == null) {
                                            arr = new ArrayList();
                                            messages.put(Long.valueOf(uid), arr);
                                        }
                                        arr.add(obj);
                                    }
                                }
                                z = false;
                                message.unread = z;
                                message.flags |= LinearLayoutManager.INVALID_OFFSET;
                                obj = new MessageObject(message, C06222.this.val$usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(dialog_id)));
                                pushMessages.add(obj);
                                uid = (long) (-AnonymousClass91.this.val$channelId);
                                arr = (ArrayList) messages.get(Long.valueOf(uid));
                                if (arr == null) {
                                    arr = new ArrayList();
                                    messages.put(Long.valueOf(uid), arr);
                                }
                                arr.add(obj);
                            }
                            AndroidUtilities.runOnUIThread(new C06181(messages));
                            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06202(pushMessages));
                        }
                        if (!C06222.this.val$res.other_updates.isEmpty()) {
                            MessagesController.this.processUpdateArray(C06222.this.val$res.other_updates, C06222.this.val$res.users, C06222.this.val$res.chats, true);
                        }
                        MessagesController.this.processChannelsUpdatesQueue(AnonymousClass91.this.val$channelId, MessagesController.UPDATE_MASK_NAME);
                        MessagesStorage.getInstance().saveChannelPts(AnonymousClass91.this.val$channelId, C06222.this.val$res.pts);
                    } else if (C06222.this.val$res instanceof TL_updates_channelDifferenceTooLong) {
                        dialog_id = (long) (-AnonymousClass91.this.val$channelId);
                        inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                        if (inboxValue == null) {
                            inboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, dialog_id));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
                        }
                        outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                        if (outboxValue == null) {
                            outboxValue = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, dialog_id));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
                        }
                        for (a = 0; a < C06222.this.val$res.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                            message = (Message) C06222.this.val$res.messages.get(a);
                            message.dialog_id = (long) (-AnonymousClass91.this.val$channelId);
                            if (!(message.action instanceof TL_messageActionChannelCreate) && (C06222.this.val$channelFinal == null || !C06222.this.val$channelFinal.left)) {
                                if (message.out) {
                                    num = outboxValue;
                                } else {
                                    num = inboxValue;
                                }
                                if (num.intValue() < message.id) {
                                    z = true;
                                    message.unread = z;
                                    if (C06222.this.val$channelFinal != null && C06222.this.val$channelFinal.megagroup) {
                                        message.flags |= LinearLayoutManager.INVALID_OFFSET;
                                    }
                                }
                            }
                            z = false;
                            message.unread = z;
                            message.flags |= LinearLayoutManager.INVALID_OFFSET;
                        }
                        MessagesStorage.getInstance().overwriteChannel(AnonymousClass91.this.val$channelId, (TL_updates_channelDifferenceTooLong) C06222.this.val$res, AnonymousClass91.this.val$newDialogType);
                    }
                    MessagesController.this.gettingDifferenceChannels.remove(Integer.valueOf(AnonymousClass91.this.val$channelId));
                    MessagesController.this.channelsPts.put(Integer.valueOf(AnonymousClass91.this.val$channelId), Integer.valueOf(C06222.this.val$res.pts));
                    if ((C06222.this.val$res.flags & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                        MessagesController.this.shortPollChannels.put(AnonymousClass91.this.val$channelId, ((int) (System.currentTimeMillis() / 1000)) + C06222.this.val$res.timeout);
                    }
                    if (!C06222.this.val$res.isFinal) {
                        MessagesController.this.getChannelDifference(AnonymousClass91.this.val$channelId);
                    }
                    int i = C06222.this.val$res.pts;
                    FileLog.m11e("tmessages", "received channel difference with pts = " + r0 + " channelId = " + AnonymousClass91.this.val$channelId);
                    FileLog.m11e("tmessages", "new_messages = " + C06222.this.val$res.new_messages.size() + " messages = " + C06222.this.val$res.messages.size() + " users = " + C06222.this.val$res.users.size() + " chats = " + C06222.this.val$res.chats.size() + " other updates = " + C06222.this.val$res.other_updates.size());
                    if (AnonymousClass91.this.val$newTaskId != 0) {
                        MessagesStorage.getInstance().removePendingTask(AnonymousClass91.this.val$newTaskId);
                    }
                }
            }

            C06222(ArrayList arrayList, updates_ChannelDifference org_telegram_tgnet_TLRPC_updates_ChannelDifference, Chat chat, HashMap hashMap) {
                this.val$msgUpdates = arrayList;
                this.val$res = org_telegram_tgnet_TLRPC_updates_ChannelDifference;
                this.val$channelFinal = chat;
                this.val$usersDict = hashMap;
            }

            public void run() {
                if (!this.val$msgUpdates.isEmpty()) {
                    HashMap<Integer, long[]> corrected = new HashMap();
                    Iterator i$ = this.val$msgUpdates.iterator();
                    while (i$.hasNext()) {
                        TL_updateMessageID update = (TL_updateMessageID) i$.next();
                        long[] ids = MessagesStorage.getInstance().updateMessageStateAndId(update.random_id, null, update.id, 0, false, AnonymousClass91.this.val$channelId);
                        if (ids != null) {
                            corrected.put(Integer.valueOf(update.id), ids);
                        }
                    }
                    if (!corrected.isEmpty()) {
                        AndroidUtilities.runOnUIThread(new C06171(corrected));
                    }
                }
                Utilities.stageQueue.postRunnable(new C06212());
            }
        }

        /* renamed from: org.telegram.messenger.MessagesController.91.3 */
        class C06233 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C06233(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                MessagesController.this.checkChannelError(this.val$error.text, AnonymousClass91.this.val$channelId);
            }
        }

        AnonymousClass91(int i, int i2, long j) {
            this.val$channelId = i;
            this.val$newDialogType = i2;
            this.val$newTaskId = j;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                int a;
                updates_ChannelDifference res = (updates_ChannelDifference) response;
                HashMap<Integer, User> usersDict = new HashMap();
                for (a = 0; a < res.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    User user = (User) res.users.get(a);
                    usersDict.put(Integer.valueOf(user.id), user);
                }
                Chat channel = null;
                for (a = 0; a < res.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    Chat chat = (Chat) res.chats.get(a);
                    if (chat.id == this.val$channelId) {
                        channel = chat;
                        break;
                    }
                }
                Chat channelFinal = channel;
                ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
                if (!res.other_updates.isEmpty()) {
                    a = 0;
                    while (a < res.other_updates.size()) {
                        Update upd = (Update) res.other_updates.get(a);
                        if (upd instanceof TL_updateMessageID) {
                            msgUpdates.add((TL_updateMessageID) upd);
                            res.other_updates.remove(a);
                            a--;
                        }
                        a += MessagesController.UPDATE_MASK_NAME;
                    }
                }
                MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
                AndroidUtilities.runOnUIThread(new C06161(res));
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06222(msgUpdates, res, channelFinal, usersDict));
                return;
            }
            AndroidUtilities.runOnUIThread(new C06233(error));
            MessagesController.this.gettingDifferenceChannels.remove(Integer.valueOf(this.val$channelId));
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController.9 */
    class C16869 implements RequestDelegate {
        C16869() {
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                ArrayList<Update> arrayList = new ArrayList();
                for (int a = 0; a < res.dialogs.size(); a += MessagesController.UPDATE_MASK_NAME) {
                    TL_dialog dialog = (TL_dialog) res.dialogs.get(a);
                    if (dialog.read_inbox_max_id == 0) {
                        dialog.read_inbox_max_id = MessagesController.UPDATE_MASK_NAME;
                    }
                    if (dialog.read_outbox_max_id == 0) {
                        dialog.read_outbox_max_id = MessagesController.UPDATE_MASK_NAME;
                    }
                    if (dialog.id == 0 && dialog.peer != null) {
                        if (dialog.peer.user_id != 0) {
                            dialog.id = (long) dialog.peer.user_id;
                        } else if (dialog.peer.chat_id != 0) {
                            dialog.id = (long) (-dialog.peer.chat_id);
                        } else if (dialog.peer.channel_id != 0) {
                            dialog.id = (long) (-dialog.peer.channel_id);
                        }
                    }
                    Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, value.intValue())));
                    if (value.intValue() == 0) {
                        if (dialog.peer.channel_id != 0) {
                            TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                            update.channel_id = dialog.peer.channel_id;
                            update.max_id = dialog.read_inbox_max_id;
                            arrayList.add(update);
                        } else {
                            TL_updateReadHistoryInbox update2 = new TL_updateReadHistoryInbox();
                            update2.peer = dialog.peer;
                            update2.max_id = dialog.read_inbox_max_id;
                            arrayList.add(update2);
                        }
                    }
                    value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, value.intValue())));
                    if (value.intValue() == 0) {
                        if (dialog.peer.channel_id != 0) {
                            TL_updateReadChannelOutbox update3 = new TL_updateReadChannelOutbox();
                            update3.channel_id = dialog.peer.channel_id;
                            update3.max_id = dialog.read_outbox_max_id;
                            arrayList.add(update3);
                        } else {
                            TL_updateReadHistoryOutbox update4 = new TL_updateReadHistoryOutbox();
                            update4.peer = dialog.peer;
                            update4.max_id = dialog.read_outbox_max_id;
                            arrayList.add(update4);
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    MessagesController.this.processUpdateArray(arrayList, null, null, false);
                }
            }
        }
    }

    private class UserActionUpdatesPts extends Updates {
        private UserActionUpdatesPts() {
        }
    }

    private class UserActionUpdatesSeq extends Updates {
        private UserActionUpdatesSeq() {
        }
    }

    static /* synthetic */ long access$3714(MessagesController x0, long x1) {
        long j = x0.lastStatusUpdateTime + x1;
        x0.lastStatusUpdateTime = j;
        return j;
    }

    static {
        Instance = null;
    }

    public static MessagesController getInstance() {
        MessagesController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        MessagesController localInstance2 = new MessagesController();
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

    public MessagesController() {
        this.chats = new ConcurrentHashMap(100, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.encryptedChats = new ConcurrentHashMap(10, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.users = new ConcurrentHashMap(100, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.usersByUsernames = new ConcurrentHashMap(100, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.joiningToChannels = new ArrayList();
        this.exportedChats = new HashMap();
        this.dialogs = new ArrayList();
        this.dialogsServerOnly = new ArrayList();
        this.dialogsGroupsOnly = new ArrayList();
        this.dialogs_read_inbox_max = new ConcurrentHashMap(100, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.dialogs_read_outbox_max = new ConcurrentHashMap(100, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.dialogs_dict = new ConcurrentHashMap(100, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.dialogMessage = new HashMap();
        this.dialogMessagesByRandomIds = new HashMap();
        this.dialogMessagesByIds = new HashMap();
        this.printingUsers = new ConcurrentHashMap(20, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.printingStrings = new HashMap();
        this.printingStringsTypes = new HashMap();
        this.sendingTypings = new HashMap();
        this.onlinePrivacy = new ConcurrentHashMap(20, TouchHelperCallback.ALPHA_FULL, UPDATE_MASK_AVATAR);
        this.lastPrintingStringCount = 0;
        this.loadingPeerSettings = new HashMap();
        this.createdDialogIds = new ArrayList();
        this.shortPollChannels = new SparseIntArray();
        this.needShortPollChannels = new SparseIntArray();
        this.loadingBlockedUsers = false;
        this.blockedUsers = new ArrayList();
        this.channelViewsToSend = new SparseArray();
        this.channelViewsToReload = new SparseArray();
        this.updatesQueueChannels = new HashMap();
        this.updatesStartWaitTimeChannels = new HashMap();
        this.channelsPts = new HashMap();
        this.gettingDifferenceChannels = new HashMap();
        this.gettingUnknownChannels = new HashMap();
        this.checkingLastMessagesDialogs = new HashMap();
        this.updatesQueueSeq = new ArrayList();
        this.updatesQueuePts = new ArrayList();
        this.updatesQueueQts = new ArrayList();
        this.updatesStartWaitTimeSeq = 0;
        this.updatesStartWaitTimePts = 0;
        this.updatesStartWaitTimeQts = 0;
        this.fullUsersAbout = new HashMap();
        this.loadingFullUsers = new ArrayList();
        this.loadedFullUsers = new ArrayList();
        this.loadingFullChats = new ArrayList();
        this.loadingFullParticipants = new ArrayList();
        this.loadedFullParticipants = new ArrayList();
        this.loadedFullChats = new ArrayList();
        this.reloadingWebpages = new HashMap();
        this.reloadingWebpagesPending = new HashMap();
        this.reloadingMessages = new HashMap();
        this.gettingNewDeleteTask = false;
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeleteTaskRunnable = null;
        this.loadingDialogs = false;
        this.migratingDialogs = false;
        this.dialogsEndReached = false;
        this.gettingDifference = false;
        this.updatingState = false;
        this.firstGettingTask = false;
        this.registeringForPush = false;
        this.secretWebpagePreview = UPDATE_MASK_AVATAR;
        this.lastStatusUpdateTime = 0;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        this.offlineSent = false;
        this.uploadingAvatar = null;
        this.enableJoined = true;
        this.fontSize = AndroidUtilities.dp(16.0f);
        this.maxGroupCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.maxBroadcastCount = 100;
        this.maxMegagroupCount = Factory.DEFAULT_MIN_REBUFFER_MS;
        this.minGroupConvertSize = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.maxEditTime = 172800;
        this.disabledFeatures = new ArrayList();
        this.dialogComparator = new C05711();
        this.updatesComparator = new C05752();
        ImageLoader.getInstance();
        MessagesStorage.getInstance();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        addSupportUser();
        this.enableJoined = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnableContactJoined", true);
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.secretWebpagePreview = preferences.getInt("secretWebpage2", UPDATE_MASK_AVATAR);
        this.maxGroupCount = preferences.getInt("maxGroupCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxMegagroupCount = preferences.getInt("maxMegagroupCount", 1000);
        this.maxEditTime = preferences.getInt("maxEditTime", 3600);
        this.groupBigSize = preferences.getInt("groupBigSize", 10);
        this.ratingDecay = preferences.getInt("ratingDecay", 2419200);
        this.fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : UPDATE_MASK_CHAT_NAME);
        String disabledFeaturesString = preferences.getString("disabledFeatures", null);
        if (disabledFeaturesString != null && disabledFeaturesString.length() != 0) {
            try {
                byte[] bytes = Base64.decode(disabledFeaturesString, 0);
                if (bytes != null) {
                    SerializedData data = new SerializedData(bytes);
                    int count = data.readInt32(false);
                    for (int a = 0; a < count; a += UPDATE_MASK_NAME) {
                        TL_disabledFeature feature = TL_disabledFeature.TLdeserialize(data, data.readInt32(false), false);
                        if (!(feature == null || feature.feature == null || feature.description == null)) {
                            this.disabledFeatures.add(feature);
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void updateConfig(TL_config config) {
        AndroidUtilities.runOnUIThread(new C05783(config));
    }

    public static boolean isFeatureEnabled(String feature, BaseFragment fragment) {
        if (feature == null || feature.length() == 0 || getInstance().disabledFeatures.isEmpty() || fragment == null) {
            return true;
        }
        Iterator i$ = getInstance().disabledFeatures.iterator();
        while (i$.hasNext()) {
            TL_disabledFeature disabledFeature = (TL_disabledFeature) i$.next();
            if (disabledFeature.feature.equals(feature)) {
                if (fragment.getParentActivity() != null) {
                    Builder builder = new Builder(fragment.getParentActivity());
                    builder.setTitle("Oops!");
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                    builder.setMessage(disabledFeature.description);
                    fragment.showDialog(builder.create());
                }
                return false;
            }
        }
        return true;
    }

    public void addSupportUser() {
        TL_userForeign_old2 user = new TL_userForeign_old2();
        user.phone = "333";
        user.id = 333000;
        user.first_name = "Telegram";
        user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
        user.status = null;
        user.photo = new TL_userProfilePhotoEmpty();
        putUser(user, true);
        user = new TL_userForeign_old2();
        user.phone = "42777";
        user.id = 777000;
        user.first_name = "Telegram";
        user.last_name = "Notifications";
        user.status = null;
        user.photo = new TL_userProfilePhotoEmpty();
        putUser(user, true);
    }

    public static InputUser getInputUser(User user) {
        if (user == null) {
            return new TL_inputUserEmpty();
        }
        if (user.id == UserConfig.getClientUserId()) {
            return new TL_inputUserSelf();
        }
        InputUser inputUser = new TL_inputUser();
        inputUser.user_id = user.id;
        inputUser.access_hash = user.access_hash;
        return inputUser;
    }

    public static InputUser getInputUser(int user_id) {
        return getInputUser(getInstance().getUser(Integer.valueOf(user_id)));
    }

    public static InputChannel getInputChannel(Chat chat) {
        if (!(chat instanceof TL_channel) && !(chat instanceof TL_channelForbidden)) {
            return new TL_inputChannelEmpty();
        }
        InputChannel inputChat = new TL_inputChannel();
        inputChat.channel_id = chat.id;
        inputChat.access_hash = chat.access_hash;
        return inputChat;
    }

    public static InputChannel getInputChannel(int chatId) {
        return getInputChannel(getInstance().getChat(Integer.valueOf(chatId)));
    }

    public static InputPeer getInputPeer(int id) {
        InputPeer inputPeer;
        if (id < 0) {
            Chat chat = getInstance().getChat(Integer.valueOf(-id));
            if (ChatObject.isChannel(chat)) {
                inputPeer = new TL_inputPeerChannel();
                inputPeer.channel_id = -id;
                inputPeer.access_hash = chat.access_hash;
                return inputPeer;
            }
            inputPeer = new TL_inputPeerChat();
            inputPeer.chat_id = -id;
            return inputPeer;
        }
        User user = getInstance().getUser(Integer.valueOf(id));
        inputPeer = new TL_inputPeerUser();
        inputPeer.user_id = id;
        if (user == null) {
            return inputPeer;
        }
        inputPeer.access_hash = user.access_hash;
        return inputPeer;
    }

    public static Peer getPeer(int id) {
        Peer inputPeer;
        if (id < 0) {
            Chat chat = getInstance().getChat(Integer.valueOf(-id));
            if ((chat instanceof TL_channel) || (chat instanceof TL_channelForbidden)) {
                inputPeer = new TL_peerChannel();
                inputPeer.channel_id = -id;
                return inputPeer;
            }
            inputPeer = new TL_peerChat();
            inputPeer.chat_id = -id;
            return inputPeer;
        }
        User user = getInstance().getUser(Integer.valueOf(id));
        inputPeer = new TL_peerUser();
        inputPeer.user_id = id;
        return inputPeer;
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        if (id == NotificationCenter.FileDidUpload) {
            location = args[0];
            InputFile file = args[UPDATE_MASK_NAME];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
                req.caption = TtmlNode.ANONYMOUS_REGION_ID;
                req.crop = new TL_inputPhotoCropAuto();
                req.file = file;
                req.geo_point = new TL_inputGeoPointEmpty();
                ConnectionsManager.getInstance().sendRequest(req, new C16844());
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                this.uploadingAvatar = null;
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            Integer newMsgId = args[UPDATE_MASK_NAME];
            Long did = args[3];
            MessageObject obj = (MessageObject) this.dialogMessage.get(did);
            if (obj != null && obj.getId() == msgId.intValue()) {
                obj.messageOwner.id = newMsgId.intValue();
                obj.messageOwner.send_state = 0;
                TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
                if (dialog != null && dialog.top_message == msgId.intValue()) {
                    dialog.top_message = newMsgId.intValue();
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            obj = (MessageObject) this.dialogMessagesByIds.remove(msgId);
            if (obj != null) {
                this.dialogMessagesByIds.put(newMsgId, obj);
            }
        }
    }

    public void cleanup() {
        ContactsController.getInstance().cleanup();
        MediaController.getInstance().cleanup();
        NotificationsController.getInstance().cleanup();
        SendMessagesHelper.getInstance().cleanup();
        SecretChatHelper.getInstance().cleanup();
        StickersQuery.cleanup();
        SearchQuery.cleanup();
        DraftQuery.cleanup();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsersAbout.clear();
        this.dialogs.clear();
        this.joiningToChannels.clear();
        this.channelViewsToSend.clear();
        this.channelViewsToReload.clear();
        this.dialogsServerOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.users.clear();
        this.usersByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.lastPrintingStringCount = 0;
        this.nextDialogsCacheOffset = 0;
        Utilities.stageQueue.postRunnable(new C05945());
        this.blockedUsers.clear();
        this.sendingTypings.clear();
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.gettingNewDeleteTask = false;
        this.loadingDialogs = false;
        this.dialogsEndReached = false;
        this.loadingBlockedUsers = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.uploadingAvatar = null;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new C06046());
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public User getUser(Integer id) {
        return (User) this.users.get(id);
    }

    public User getUser(String username) {
        if (username == null || username.length() == 0) {
            return null;
        }
        return (User) this.usersByUsernames.get(username.toLowerCase());
    }

    public ConcurrentHashMap<Integer, User> getUsers() {
        return this.users;
    }

    public Chat getChat(Integer id) {
        return (Chat) this.chats.get(id);
    }

    public EncryptedChat getEncryptedChat(Integer id) {
        return (EncryptedChat) this.encryptedChats.get(id);
    }

    public EncryptedChat getEncryptedChatDB(int chat_id) {
        EncryptedChat chat = (EncryptedChat) this.encryptedChats.get(Integer.valueOf(chat_id));
        if (chat != null) {
            return chat;
        }
        Semaphore semaphore = new Semaphore(0);
        ArrayList<TLObject> result = new ArrayList();
        MessagesStorage.getInstance().getEncryptedChat(chat_id, semaphore, result);
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        if (result.size() != UPDATE_MASK_AVATAR) {
            return chat;
        }
        chat = (EncryptedChat) result.get(0);
        User user = (User) result.get(UPDATE_MASK_NAME);
        putEncryptedChat(chat, false);
        putUser(user, true);
        return chat;
    }

    public void setLastCreatedDialogId(long dialog_id, boolean set) {
        Utilities.stageQueue.postRunnable(new C06137(set, dialog_id));
    }

    public ExportedChatInvite getExportedInvite(int chat_id) {
        return (ExportedChatInvite) this.exportedChats.get(Integer.valueOf(chat_id));
    }

    public boolean putUser(User user, boolean fromCache) {
        if (user == null) {
            return false;
        }
        if (!fromCache || user.id / 1000 == 333 || user.id == 777000) {
            fromCache = false;
        } else {
            fromCache = true;
        }
        User oldUser = (User) this.users.get(Integer.valueOf(user.id));
        if (!(oldUser == null || oldUser.username == null || oldUser.username.length() <= 0)) {
            this.usersByUsernames.remove(oldUser.username);
        }
        if (user.username != null && user.username.length() > 0) {
            this.usersByUsernames.put(user.username.toLowerCase(), user);
        }
        if (user.min) {
            if (oldUser == null) {
                this.users.put(Integer.valueOf(user.id), user);
                return false;
            } else if (fromCache) {
                return false;
            } else {
                if (user.username != null) {
                    oldUser.username = user.username;
                    oldUser.flags |= UPDATE_MASK_CHAT_AVATAR;
                } else {
                    oldUser.username = null;
                    oldUser.flags &= -9;
                }
                if (user.photo != null) {
                    oldUser.photo = user.photo;
                    oldUser.flags |= UPDATE_MASK_CHAT_MEMBERS;
                    return false;
                }
                oldUser.photo = null;
                oldUser.flags &= -33;
                return false;
            }
        } else if (!fromCache) {
            this.users.put(Integer.valueOf(user.id), user);
            if (user.id == UserConfig.getClientUserId()) {
                UserConfig.setCurrentUser(user);
                UserConfig.saveConfig(true);
            }
            if (oldUser == null || user.status == null || oldUser.status == null || user.status.expires == oldUser.status.expires) {
                return false;
            }
            return true;
        } else if (oldUser == null) {
            this.users.put(Integer.valueOf(user.id), user);
            return false;
        } else if (!oldUser.min) {
            return false;
        } else {
            user.min = false;
            if (oldUser.username != null) {
                user.username = oldUser.username;
                user.flags |= UPDATE_MASK_CHAT_AVATAR;
            } else {
                user.username = null;
                user.flags &= -9;
            }
            if (oldUser.photo != null) {
                user.photo = oldUser.photo;
                user.flags |= UPDATE_MASK_CHAT_MEMBERS;
            } else {
                user.photo = null;
                user.flags &= -33;
            }
            this.users.put(Integer.valueOf(user.id), user);
            return false;
        }
    }

    public void putUsers(ArrayList<User> users, boolean fromCache) {
        if (users != null && !users.isEmpty()) {
            boolean updateStatus = false;
            int count = users.size();
            for (int a = 0; a < count; a += UPDATE_MASK_NAME) {
                if (putUser((User) users.get(a), fromCache)) {
                    updateStatus = true;
                }
            }
            if (updateStatus) {
                AndroidUtilities.runOnUIThread(new C06158());
            }
        }
    }

    public void putChat(Chat chat, boolean fromCache) {
        if (chat != null) {
            Chat oldChat = (Chat) this.chats.get(Integer.valueOf(chat.id));
            if (chat.min) {
                if (oldChat == null) {
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (!fromCache) {
                    oldChat.title = chat.title;
                    oldChat.photo = chat.photo;
                    oldChat.broadcast = chat.broadcast;
                    oldChat.verified = chat.verified;
                    oldChat.megagroup = chat.megagroup;
                    oldChat.democracy = chat.democracy;
                    if (chat.username != null) {
                        oldChat.username = chat.username;
                        oldChat.flags |= UPDATE_MASK_USER_PRINT;
                        return;
                    }
                    oldChat.username = null;
                    oldChat.flags &= -65;
                }
            } else if (!fromCache) {
                if (!(oldChat == null || chat.version == oldChat.version)) {
                    this.loadedFullChats.remove(Integer.valueOf(chat.id));
                }
                this.chats.put(Integer.valueOf(chat.id), chat);
            } else if (oldChat == null) {
                this.chats.put(Integer.valueOf(chat.id), chat);
            } else if (oldChat.min) {
                chat.min = false;
                chat.title = oldChat.title;
                chat.photo = oldChat.photo;
                chat.broadcast = oldChat.broadcast;
                chat.verified = oldChat.verified;
                chat.megagroup = oldChat.megagroup;
                chat.democracy = oldChat.democracy;
                if (oldChat.username != null) {
                    chat.username = oldChat.username;
                    chat.flags |= UPDATE_MASK_USER_PRINT;
                } else {
                    chat.username = null;
                    chat.flags &= -65;
                }
                this.chats.put(Integer.valueOf(chat.id), chat);
            }
        }
    }

    public void putChats(ArrayList<Chat> chats, boolean fromCache) {
        if (chats != null && !chats.isEmpty()) {
            int count = chats.size();
            for (int a = 0; a < count; a += UPDATE_MASK_NAME) {
                putChat((Chat) chats.get(a), fromCache);
            }
        }
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, boolean fromCache) {
        if (encryptedChat != null) {
            if (fromCache) {
                this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
            } else {
                this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            }
        }
    }

    public void putEncryptedChats(ArrayList<EncryptedChat> encryptedChats, boolean fromCache) {
        if (encryptedChats != null && !encryptedChats.isEmpty()) {
            int count = encryptedChats.size();
            for (int a = 0; a < count; a += UPDATE_MASK_NAME) {
                putEncryptedChat((EncryptedChat) encryptedChats.get(a), fromCache);
            }
        }
    }

    public String getUserAbout(int uid) {
        return (String) this.fullUsersAbout.get(Integer.valueOf(uid));
    }

    public void cancelLoadFullUser(int uid) {
        this.loadingFullUsers.remove(Integer.valueOf(uid));
    }

    public void cancelLoadFullChat(int cid) {
        this.loadingFullChats.remove(Integer.valueOf(cid));
    }

    protected void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TL_dialog> dialogs, long did) {
        if (!dialogs.isEmpty()) {
            TL_messages_getPeerDialogs req = new TL_messages_getPeerDialogs();
            if (dialogs != null) {
                for (int a = 0; a < dialogs.size(); a += UPDATE_MASK_NAME) {
                    req.peers.add(getInputPeer((int) ((TL_dialog) dialogs.get(a)).id));
                }
            } else {
                req.peers.add(getInputPeer((int) did));
            }
            ConnectionsManager.getInstance().sendRequest(req, new C16869());
        }
    }

    public void loadFullChat(int chat_id, int classGuid, boolean force) {
        if (!this.loadingFullChats.contains(Integer.valueOf(chat_id))) {
            if (force || !this.loadedFullChats.contains(Integer.valueOf(chat_id))) {
                TLObject request;
                this.loadingFullChats.add(Integer.valueOf(chat_id));
                Chat chat = getChat(Integer.valueOf(chat_id));
                TLObject req;
                if (ChatObject.isChannel(chat_id)) {
                    req = new TL_channels_getFullChannel();
                    req.channel = getInputChannel(chat_id);
                    request = req;
                } else {
                    req = new TL_messages_getFullChat();
                    req.chat_id = chat_id;
                    request = req;
                }
                int reqId = ConnectionsManager.getInstance().sendRequest(request, new AnonymousClass10(chat, chat_id, classGuid));
                if (classGuid != 0) {
                    ConnectionsManager.getInstance().bindRequestToGuid(reqId, classGuid);
                }
            }
        }
    }

    public void loadFullUser(User user, int classGuid, boolean force) {
        if (user != null && !this.loadingFullUsers.contains(Integer.valueOf(user.id))) {
            if (force || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TL_users_getFullUser req = new TL_users_getFullUser();
                req.id = getInputUser(user);
                ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass11(user, classGuid)), classGuid);
            }
        }
    }

    private void reloadMessages(ArrayList<Integer> mids, long dialog_id) {
        if (!mids.isEmpty()) {
            TLObject request;
            ArrayList<Integer> result = new ArrayList();
            Chat chat = ChatObject.getChatByDialog(dialog_id);
            TLObject req;
            if (ChatObject.isChannel(chat)) {
                req = new TL_channels_getMessages();
                req.channel = getInputChannel(chat);
                req.id = result;
                request = req;
            } else {
                req = new TL_messages_getMessages();
                req.id = result;
                request = req;
            }
            ArrayList<Integer> arrayList = (ArrayList) this.reloadingMessages.get(Long.valueOf(dialog_id));
            for (int a = 0; a < mids.size(); a += UPDATE_MASK_NAME) {
                Integer mid = (Integer) mids.get(a);
                if (arrayList == null || !arrayList.contains(mid)) {
                    result.add(mid);
                }
            }
            if (!result.isEmpty()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.reloadingMessages.put(Long.valueOf(dialog_id), arrayList);
                }
                arrayList.addAll(result);
                ConnectionsManager.getInstance().sendRequest(request, new AnonymousClass12(dialog_id, chat, result));
            }
        }
    }

    public void hideReportSpam(long dialogId, User currentUser, Chat currentChat) {
        if (currentUser != null || currentChat != null) {
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            editor.putInt("spam3_" + dialogId, UPDATE_MASK_NAME);
            editor.commit();
            TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
            if (currentUser != null) {
                req.peer = getInputPeer(currentUser.id);
            } else if (currentChat != null) {
                req.peer = getInputPeer(-currentChat.id);
            }
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }

    public void reportSpam(long dialogId, User currentUser, Chat currentChat) {
        if (currentUser != null || currentChat != null) {
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            editor.putInt("spam3_" + dialogId, UPDATE_MASK_NAME);
            editor.commit();
            TL_messages_reportSpam req = new TL_messages_reportSpam();
            if (currentChat != null) {
                req.peer = getInputPeer(-currentChat.id);
            } else if (currentUser != null) {
                req.peer = getInputPeer(currentUser.id);
            }
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            }, UPDATE_MASK_AVATAR);
        }
    }

    public void loadPeerSettings(long dialogId, User currentUser, Chat currentChat) {
        if (!this.loadingPeerSettings.containsKey(Long.valueOf(dialogId))) {
            if (currentUser != null || currentChat != null) {
                this.loadingPeerSettings.put(Long.valueOf(dialogId), Boolean.valueOf(true));
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (preferences.getInt("spam3_" + dialogId, 0) == UPDATE_MASK_NAME) {
                    return;
                }
                if (preferences.getBoolean("spam_" + dialogId, false)) {
                    TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
                    if (currentUser != null) {
                        req.peer = getInputPeer(currentUser.id);
                    } else if (currentChat != null) {
                        req.peer = getInputPeer(-currentChat.id);
                    }
                    ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass15(dialogId));
                    return;
                }
                TL_messages_getPeerSettings req2 = new TL_messages_getPeerSettings();
                if (currentUser != null) {
                    req2.peer = getInputPeer(currentUser.id);
                } else if (currentChat != null) {
                    req2.peer = getInputPeer(-currentChat.id);
                }
                ConnectionsManager.getInstance().sendRequest(req2, new AnonymousClass16(dialogId));
            }
        }
    }

    protected void processNewChannelDifferenceParams(int pts, int pts_count, int channelId) {
        FileLog.m11e("tmessages", "processNewChannelDifferenceParams pts = " + pts + " pts_count = " + pts_count + " channeldId = " + channelId);
        if (DialogObject.isChannel((TL_dialog) this.dialogs_dict.get(Long.valueOf((long) (-channelId))))) {
            Integer channelPts = (Integer) this.channelsPts.get(Integer.valueOf(channelId));
            if (channelPts == null) {
                channelPts = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(channelId));
                if (channelPts.intValue() == 0) {
                    channelPts = Integer.valueOf(UPDATE_MASK_NAME);
                }
                this.channelsPts.put(Integer.valueOf(channelId), channelPts);
            }
            if (channelPts.intValue() + pts_count == pts) {
                FileLog.m11e("tmessages", "APPLY CHANNEL PTS");
                this.channelsPts.put(Integer.valueOf(channelId), Integer.valueOf(pts));
                MessagesStorage.getInstance().saveChannelPts(channelId, pts);
            } else if (channelPts.intValue() != pts) {
                Long updatesStartWaitTime = (Long) this.updatesStartWaitTimeChannels.get(Integer.valueOf(channelId));
                Boolean gettingDifferenceChannel = (Boolean) this.gettingDifferenceChannels.get(Integer.valueOf(channelId));
                if (gettingDifferenceChannel == null) {
                    gettingDifferenceChannel = Boolean.valueOf(false);
                }
                if (gettingDifferenceChannel.booleanValue() || updatesStartWaitTime == null || Math.abs(System.currentTimeMillis() - updatesStartWaitTime.longValue()) <= 1500) {
                    FileLog.m11e("tmessages", "ADD CHANNEL UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                    if (updatesStartWaitTime == null) {
                        this.updatesStartWaitTimeChannels.put(Integer.valueOf(channelId), Long.valueOf(System.currentTimeMillis()));
                    }
                    UserActionUpdatesPts updates = new UserActionUpdatesPts();
                    updates.pts = pts;
                    updates.pts_count = pts_count;
                    updates.chat_id = channelId;
                    ArrayList<Updates> arrayList = (ArrayList) this.updatesQueueChannels.get(Integer.valueOf(channelId));
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        this.updatesQueueChannels.put(Integer.valueOf(channelId), arrayList);
                    }
                    arrayList.add(updates);
                    return;
                }
                getChannelDifference(channelId);
            }
        }
    }

    protected void processNewDifferenceParams(int seq, int pts, int date, int pts_count) {
        FileLog.m11e("tmessages", "processNewDifferenceParams seq = " + seq + " pts = " + pts + " date = " + date + " pts_count = " + pts_count);
        if (pts != -1) {
            if (MessagesStorage.lastPtsValue + pts_count == pts) {
                FileLog.m11e("tmessages", "APPLY PTS");
                MessagesStorage.lastPtsValue = pts;
                MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
            } else if (MessagesStorage.lastPtsValue != pts) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    FileLog.m11e("tmessages", "ADD UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts updates = new UserActionUpdatesPts();
                    updates.pts = pts;
                    updates.pts_count = pts_count;
                    this.updatesQueuePts.add(updates);
                } else {
                    getDifference();
                }
            }
        }
        if (seq == -1) {
            return;
        }
        if (MessagesStorage.lastSeqValue + UPDATE_MASK_NAME == seq) {
            FileLog.m11e("tmessages", "APPLY SEQ");
            MessagesStorage.lastSeqValue = seq;
            if (date != -1) {
                MessagesStorage.lastDateValue = date;
            }
            MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
        } else if (MessagesStorage.lastSeqValue == seq) {
        } else {
            if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                FileLog.m11e("tmessages", "ADD UPDATE TO QUEUE seq = " + seq);
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq updates2 = new UserActionUpdatesSeq();
                updates2.seq = seq;
                this.updatesQueueSeq.add(updates2);
                return;
            }
            getDifference();
        }
    }

    public void didAddedNewTask(int minDate, SparseArray<ArrayList<Integer>> mids) {
        Utilities.stageQueue.postRunnable(new AnonymousClass17(minDate));
        AndroidUtilities.runOnUIThread(new AnonymousClass18(mids));
    }

    public void getNewDeleteTask(ArrayList<Integer> oldTask) {
        Utilities.stageQueue.postRunnable(new AnonymousClass19(oldTask));
    }

    private boolean checkDeletingTask(boolean runnable) {
        int currentServerTime = ConnectionsManager.getInstance().getCurrentTime();
        if (this.currentDeletingTaskMids == null) {
            return false;
        }
        if (!runnable && (this.currentDeletingTaskTime == 0 || this.currentDeletingTaskTime > currentServerTime)) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (!(this.currentDeleteTaskRunnable == null || runnable)) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController.20.1 */
            class C05721 implements Runnable {
                C05721() {
                }

                public void run() {
                    MessagesController.this.getNewDeleteTask(MessagesController.this.currentDeletingTaskMids);
                    MessagesController.this.currentDeletingTaskTime = 0;
                    MessagesController.this.currentDeletingTaskMids = null;
                }
            }

            public void run() {
                MessagesController.this.deleteMessages(MessagesController.this.currentDeletingTaskMids, null, null, 0);
                Utilities.stageQueue.postRunnable(new C05721());
            }
        });
        return true;
    }

    public void processLoadedDeleteTask(int taskTime, ArrayList<Integer> messages) {
        Utilities.stageQueue.postRunnable(new AnonymousClass21(messages, taskTime));
    }

    public void loadDialogPhotos(int did, int offset, int count, long max_id, boolean fromCache, int classGuid) {
        if (fromCache) {
            MessagesStorage.getInstance().getDialogPhotos(did, offset, count, max_id, classGuid);
        } else if (did > 0) {
            User user = getUser(Integer.valueOf(did));
            if (user != null) {
                TL_photos_getUserPhotos req = new TL_photos_getUserPhotos();
                req.limit = count;
                req.offset = offset;
                req.max_id = (long) ((int) max_id);
                req.user_id = getInputUser(user);
                ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass22(did, offset, count, max_id, classGuid)), classGuid);
            }
        } else if (did < 0) {
            TL_messages_search req2 = new TL_messages_search();
            req2.filter = new TL_inputMessagesFilterChatPhotos();
            req2.limit = count;
            req2.offset = offset;
            req2.max_id = (int) max_id;
            req2.f37q = TtmlNode.ANONYMOUS_REGION_ID;
            req2.peer = getInputPeer(did);
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req2, new AnonymousClass23(did, offset, count, max_id, classGuid)), classGuid);
        }
    }

    public void blockUser(int user_id) {
        User user = getUser(Integer.valueOf(user_id));
        if (user != null && !this.blockedUsers.contains(Integer.valueOf(user_id))) {
            this.blockedUsers.add(Integer.valueOf(user_id));
            if (user.bot) {
                SearchQuery.removeInline(user_id);
            } else {
                SearchQuery.removePeer(user_id);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            TL_contacts_block req = new TL_contacts_block();
            req.id = getInputUser(user);
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass24(user));
        }
    }

    public void unblockUser(int user_id) {
        TL_contacts_unblock req = new TL_contacts_unblock();
        User user = getUser(Integer.valueOf(user_id));
        if (user != null) {
            this.blockedUsers.remove(Integer.valueOf(user.id));
            req.id = getInputUser(user);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass25(user));
        }
    }

    public void getBlockedUsers(boolean cache) {
        if (UserConfig.isClientActivated() && !this.loadingBlockedUsers) {
            this.loadingBlockedUsers = true;
            if (cache) {
                MessagesStorage.getInstance().getBlockedUsers();
                return;
            }
            TL_contacts_getBlocked req = new TL_contacts_getBlocked();
            req.offset = 0;
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ArrayList<Integer> blocked = new ArrayList();
                    ArrayList<User> users = null;
                    if (error == null) {
                        contacts_Blocked res = (contacts_Blocked) response;
                        Iterator i$ = res.blocked.iterator();
                        while (i$.hasNext()) {
                            blocked.add(Integer.valueOf(((TL_contactBlocked) i$.next()).user_id));
                        }
                        users = res.users;
                        MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                        MessagesStorage.getInstance().putBlockedUsers(blocked, true);
                    }
                    MessagesController.this.processLoadedBlockedUsers(blocked, users, false);
                }
            });
        }
    }

    public void processLoadedBlockedUsers(ArrayList<Integer> ids, ArrayList<User> users, boolean cache) {
        AndroidUtilities.runOnUIThread(new AnonymousClass27(users, cache, ids));
    }

    public void deleteUserPhoto(InputPhoto photo) {
        if (photo == null) {
            TL_photos_updateProfilePhoto req = new TL_photos_updateProfilePhoto();
            req.id = new TL_inputPhotoEmpty();
            req.crop = new TL_inputPhotoCropAuto();
            UserConfig.getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(UserConfig.getClientUserId()));
            if (user == null) {
                user = UserConfig.getCurrentUser();
            }
            if (user != null) {
                user.photo = UserConfig.getCurrentUser().photo;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(UPDATE_MASK_ALL);
                instance.postNotificationName(i, objArr);
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController.28.1 */
                    class C05741 implements Runnable {
                        C05741() {
                        }

                        public void run() {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                            NotificationCenter instance = NotificationCenter.getInstance();
                            int i = NotificationCenter.updateInterfaces;
                            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                            objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_ALL);
                            instance.postNotificationName(i, objArr);
                            UserConfig.saveConfig(true);
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
                            if (user == null) {
                                user = UserConfig.getCurrentUser();
                                MessagesController.this.putUser(user, false);
                            } else {
                                UserConfig.setCurrentUser(user);
                            }
                            if (user != null) {
                                MessagesStorage.getInstance().clearUserPhotos(user.id);
                                ArrayList<User> users = new ArrayList();
                                users.add(user);
                                MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                                user.photo = (UserProfilePhoto) response;
                                AndroidUtilities.runOnUIThread(new C05741());
                            }
                        }
                    }
                });
                return;
            }
            return;
        }
        TL_photos_deletePhotos req2 = new TL_photos_deletePhotos();
        req2.id.add(photo);
        ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void processLoadedUserPhotos(photos_Photos res, int did, int offset, int count, long max_id, boolean fromCache, int classGuid) {
        if (!fromCache) {
            MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
            MessagesStorage.getInstance().putDialogPhotos(did, res);
        } else if (res == null || res.photos.isEmpty()) {
            loadDialogPhotos(did, offset, count, max_id, false, classGuid);
            return;
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass30(res, fromCache, did, offset, count, classGuid));
    }

    public void uploadAndApplyUserAvatar(PhotoSize bigPhoto) {
        if (bigPhoto != null) {
            this.uploadingAvatar = FileLoader.getInstance().getDirectory(UPDATE_MASK_STATUS) + "/" + bigPhoto.location.volume_id + "_" + bigPhoto.location.local_id + ".jpg";
            FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true);
        }
    }

    public void markChannelDialogMessageAsDeleted(ArrayList<Integer> messages, int channelId) {
        MessageObject obj = (MessageObject) this.dialogMessage.get(Long.valueOf((long) (-channelId)));
        if (obj != null) {
            for (int a = 0; a < messages.size(); a += UPDATE_MASK_NAME) {
                if (obj.getId() == ((Integer) messages.get(a)).intValue()) {
                    obj.deleted = true;
                    return;
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, EncryptedChat encryptedChat, int channelId) {
        if (messages != null && !messages.isEmpty()) {
            int a;
            if (channelId == 0) {
                for (a = 0; a < messages.size(); a += UPDATE_MASK_NAME) {
                    MessageObject obj = (MessageObject) this.dialogMessagesByIds.get((Integer) messages.get(a));
                    if (obj != null) {
                        obj.deleted = true;
                    }
                }
            } else {
                markChannelDialogMessageAsDeleted(messages, channelId);
            }
            ArrayList<Integer> toSend = new ArrayList();
            for (a = 0; a < messages.size(); a += UPDATE_MASK_NAME) {
                Integer mid = (Integer) messages.get(a);
                if (mid.intValue() > 0) {
                    toSend.add(mid);
                }
            }
            MessagesStorage.getInstance().markMessagesAsDeleted(messages, true, channelId);
            MessagesStorage.getInstance().updateDialogsWithDeletedMessages(messages, true, channelId);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.messagesDeleted;
            Object[] objArr = new Object[UPDATE_MASK_AVATAR];
            objArr[0] = messages;
            objArr[UPDATE_MASK_NAME] = Integer.valueOf(channelId);
            instance.postNotificationName(i, objArr);
            if (channelId != 0) {
                TL_channels_deleteMessages req = new TL_channels_deleteMessages();
                req.id = toSend;
                req.channel = getInputChannel(channelId);
                ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass31(channelId));
                return;
            }
            if (!(randoms == null || encryptedChat == null || randoms.isEmpty())) {
                SecretChatHelper.getInstance().sendMessagesDeleteMessage(encryptedChat, randoms, null);
            }
            TL_messages_deleteMessages req2 = new TL_messages_deleteMessages();
            req2.id = toSend;
            ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                        MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                    }
                }
            });
        }
    }

    public void pinChannelMessage(Chat chat, int id, boolean notify) {
        TL_channels_updatePinnedMessage req = new TL_channels_updatePinnedMessage();
        req.channel = getInputChannel(chat);
        req.id = id;
        req.silent = !notify;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        });
    }

    public void deleteUserChannelHistory(Chat chat, User user, int offset) {
        if (offset == 0) {
            MessagesStorage.getInstance().deleteUserChannelHistory(chat.id, user.id);
        }
        TL_channels_deleteUserHistory req = new TL_channels_deleteUserHistory();
        req.channel = getInputChannel(chat);
        req.user_id = getInputUser(user);
        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass34(chat, user));
    }

    public void deleteDialog(long did, int onlyHistory) {
        deleteDialog(did, true, onlyHistory, 0);
    }

    private void deleteDialog(long did, boolean first, int onlyHistory, int max_id) {
        int lower_part = (int) did;
        int high_id = (int) (did >> UPDATE_MASK_CHAT_MEMBERS);
        int max_id_delete = max_id;
        if (onlyHistory == UPDATE_MASK_AVATAR) {
            MessagesStorage.getInstance().deleteDialog(did, onlyHistory);
            return;
        }
        if (onlyHistory == 0 || onlyHistory == 3) {
            AndroidUtilities.uninstallShortcut(did);
        }
        if (first) {
            MessagesStorage.getInstance().deleteDialog(did, onlyHistory);
            TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(Long.valueOf(did));
            if (dialog != null) {
                if (max_id_delete == 0) {
                    max_id_delete = Math.max(0, dialog.top_message);
                }
                if (onlyHistory == 0 || onlyHistory == 3) {
                    this.dialogs.remove(dialog);
                    if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
                        Utilities.stageQueue.postRunnable(new AnonymousClass35(did));
                    }
                    this.dialogsGroupsOnly.remove(dialog);
                    this.dialogs_dict.remove(Long.valueOf(did));
                    this.dialogs_read_inbox_max.remove(Long.valueOf(did));
                    this.dialogs_read_outbox_max.remove(Long.valueOf(did));
                    this.nextDialogsCacheOffset--;
                } else {
                    dialog.unread_count = 0;
                }
                MessageObject object = (MessageObject) this.dialogMessage.remove(Long.valueOf(dialog.id));
                int lastMessageId;
                if (object != null) {
                    lastMessageId = object.getId();
                    this.dialogMessagesByIds.remove(Integer.valueOf(object.getId()));
                } else {
                    lastMessageId = dialog.top_message;
                    object = (MessageObject) this.dialogMessagesByIds.remove(Integer.valueOf(dialog.top_message));
                }
                if (!(object == null || object.messageOwner.random_id == 0)) {
                    this.dialogMessagesByRandomIds.remove(Long.valueOf(object.messageOwner.random_id));
                }
                if (onlyHistory != UPDATE_MASK_NAME || lower_part == 0 || lastMessageId <= 0) {
                    dialog.top_message = 0;
                } else {
                    Message message = new TL_messageService();
                    message.id = dialog.top_message;
                    message.out = false;
                    message.from_id = UserConfig.getClientUserId();
                    message.flags |= UPDATE_MASK_READ_DIALOG_MESSAGE;
                    message.action = new TL_messageActionHistoryClear();
                    message.date = dialog.last_message_date;
                    if (lower_part > 0) {
                        message.to_id = new TL_peerUser();
                        message.to_id.user_id = lower_part;
                    } else if (ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                        message.to_id = new TL_peerChannel();
                        message.to_id.channel_id = -lower_part;
                    } else {
                        message.to_id = new TL_peerChat();
                        message.to_id.chat_id = -lower_part;
                    }
                    MessageObject messageObject = new MessageObject(message, null, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                    ArrayList<MessageObject> objArr = new ArrayList();
                    objArr.add(messageObject);
                    ArrayList arr = new ArrayList();
                    arr.add(message);
                    updateInterfaceWithMessages(did, objArr);
                    MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
                }
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.removeAllMessagesFromDialog;
            Object[] objArr2 = new Object[UPDATE_MASK_AVATAR];
            objArr2[0] = Long.valueOf(did);
            objArr2[UPDATE_MASK_NAME] = Boolean.valueOf(false);
            instance.postNotificationName(i, objArr2);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass36(did));
        }
        if (high_id != UPDATE_MASK_NAME && onlyHistory != 3) {
            if (lower_part != 0) {
                InputPeer peer = getInputPeer(lower_part);
                if (peer != null && !(peer instanceof TL_inputPeerChannel)) {
                    TLObject req = new TL_messages_deleteHistory();
                    req.peer = peer;
                    req.max_id = max_id_delete;
                    req.just_clear = onlyHistory != 0;
                    ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass37(did, onlyHistory, max_id_delete), UPDATE_MASK_USER_PRINT);
                }
            } else if (onlyHistory == UPDATE_MASK_NAME) {
                SecretChatHelper.getInstance().sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(high_id)), null);
            } else {
                SecretChatHelper.getInstance().declineSecretChat(high_id);
            }
        }
    }

    public SearchImage saveGif(Document document) {
        SearchImage searchImage = new SearchImage();
        searchImage.type = UPDATE_MASK_AVATAR;
        searchImage.document = document;
        searchImage.date = (int) (System.currentTimeMillis() / 1000);
        searchImage.id = TtmlNode.ANONYMOUS_REGION_ID + searchImage.document.id;
        ArrayList<SearchImage> arrayList = new ArrayList();
        arrayList.add(searchImage);
        MessagesStorage.getInstance().putWebRecent(arrayList);
        TL_messages_saveGif req = new TL_messages_saveGif();
        req.id = new TL_inputDocument();
        req.id.id = searchImage.document.id;
        req.id.access_hash = searchImage.document.access_hash;
        req.unsave = false;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
        return searchImage;
    }

    public void loadChannelParticipants(Integer chat_id) {
        if (!this.loadingFullParticipants.contains(chat_id) && !this.loadedFullParticipants.contains(chat_id)) {
            this.loadingFullParticipants.add(chat_id);
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = getInputChannel(chat_id.intValue());
            req.filter = new TL_channelParticipantsRecent();
            req.offset = 0;
            req.limit = UPDATE_MASK_CHAT_MEMBERS;
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass39(chat_id));
        }
    }

    public void loadChatInfo(int chat_id, Semaphore semaphore, boolean force) {
        MessagesStorage.getInstance().loadChatInfo(chat_id, semaphore, force, false);
    }

    public void processChatInfo(int chat_id, ChatFull info, ArrayList<User> usersArr, boolean fromCache, boolean force, boolean byChannelUsers, MessageObject pinnedMessageObject) {
        if (fromCache && chat_id > 0 && !byChannelUsers) {
            loadFullChat(chat_id, 0, force);
        }
        if (info != null) {
            AndroidUtilities.runOnUIThread(new AnonymousClass40(usersArr, fromCache, info, byChannelUsers, pinnedMessageObject));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateTimerProc() {
        /*
        r30 = this;
        r10 = java.lang.System.currentTimeMillis();
        r23 = 0;
        r0 = r30;
        r1 = r23;
        r0.checkDeletingTask(r1);
        r23 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r23 == 0) goto L_0x01e5;
    L_0x0013:
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r24 = r23.getPauseTime();
        r26 = 0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 != 0) goto L_0x0125;
    L_0x0021:
        r23 = org.telegram.messenger.ApplicationLoader.isScreenOn;
        if (r23 == 0) goto L_0x0125;
    L_0x0025:
        r23 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;
        if (r23 != 0) goto L_0x0125;
    L_0x0029:
        r0 = r30;
        r0 = r0.statusSettingState;
        r23 = r0;
        r24 = 1;
        r0 = r23;
        r1 = r24;
        if (r0 == r1) goto L_0x00ab;
    L_0x0037:
        r0 = r30;
        r0 = r0.lastStatusUpdateTime;
        r24 = r0;
        r26 = 0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 == 0) goto L_0x0062;
    L_0x0043:
        r24 = java.lang.System.currentTimeMillis();
        r0 = r30;
        r0 = r0.lastStatusUpdateTime;
        r26 = r0;
        r24 = r24 - r26;
        r24 = java.lang.Math.abs(r24);
        r26 = 55000; // 0xd6d8 float:7.7071E-41 double:2.71736E-319;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 >= 0) goto L_0x0062;
    L_0x005a:
        r0 = r30;
        r0 = r0.offlineSent;
        r23 = r0;
        if (r23 == 0) goto L_0x00ab;
    L_0x0062:
        r23 = 1;
        r0 = r23;
        r1 = r30;
        r1.statusSettingState = r0;
        r0 = r30;
        r0 = r0.statusRequest;
        r23 = r0;
        if (r23 == 0) goto L_0x0081;
    L_0x0072:
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r0 = r30;
        r0 = r0.statusRequest;
        r24 = r0;
        r25 = 1;
        r23.cancelRequest(r24, r25);
    L_0x0081:
        r16 = new org.telegram.tgnet.TLRPC$TL_account_updateStatus;
        r16.<init>();
        r23 = 0;
        r0 = r23;
        r1 = r16;
        r1.offline = r0;
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r24 = new org.telegram.messenger.MessagesController$41;
        r0 = r24;
        r1 = r30;
        r0.<init>();
        r0 = r23;
        r1 = r16;
        r2 = r24;
        r23 = r0.sendRequest(r1, r2);
        r0 = r23;
        r1 = r30;
        r1.statusRequest = r0;
    L_0x00ab:
        r0 = r30;
        r0 = r0.updatesQueueChannels;
        r23 = r0;
        r23 = r23.isEmpty();
        if (r23 != 0) goto L_0x019e;
    L_0x00b7:
        r14 = new java.util.ArrayList;
        r0 = r30;
        r0 = r0.updatesQueueChannels;
        r23 = r0;
        r23 = r23.keySet();
        r0 = r23;
        r14.<init>(r0);
        r4 = 0;
    L_0x00c9:
        r23 = r14.size();
        r0 = r23;
        if (r4 >= r0) goto L_0x019e;
    L_0x00d1:
        r23 = r14.get(r4);
        r23 = (java.lang.Integer) r23;
        r13 = r23.intValue();
        r0 = r30;
        r0 = r0.updatesStartWaitTimeChannels;
        r23 = r0;
        r24 = java.lang.Integer.valueOf(r13);
        r21 = r23.get(r24);
        r21 = (java.lang.Long) r21;
        if (r21 == 0) goto L_0x0122;
    L_0x00ed:
        r24 = r21.longValue();
        r26 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r24 = r24 + r26;
        r23 = (r24 > r10 ? 1 : (r24 == r10 ? 0 : -1));
        if (r23 >= 0) goto L_0x0122;
    L_0x00f9:
        r23 = "tmessages";
        r24 = new java.lang.StringBuilder;
        r24.<init>();
        r25 = "QUEUE CHANNEL ";
        r24 = r24.append(r25);
        r0 = r24;
        r24 = r0.append(r13);
        r25 = " UPDATES WAIT TIMEOUT - CHECK QUEUE";
        r24 = r24.append(r25);
        r24 = r24.toString();
        org.telegram.messenger.FileLog.m11e(r23, r24);
        r23 = 0;
        r0 = r30;
        r1 = r23;
        r0.processChannelsUpdatesQueue(r13, r1);
    L_0x0122:
        r4 = r4 + 1;
        goto L_0x00c9;
    L_0x0125:
        r0 = r30;
        r0 = r0.statusSettingState;
        r23 = r0;
        r24 = 2;
        r0 = r23;
        r1 = r24;
        if (r0 == r1) goto L_0x00ab;
    L_0x0133:
        r0 = r30;
        r0 = r0.offlineSent;
        r23 = r0;
        if (r23 != 0) goto L_0x00ab;
    L_0x013b:
        r24 = java.lang.System.currentTimeMillis();
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r26 = r23.getPauseTime();
        r24 = r24 - r26;
        r24 = java.lang.Math.abs(r24);
        r26 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 < 0) goto L_0x00ab;
    L_0x0153:
        r23 = 2;
        r0 = r23;
        r1 = r30;
        r1.statusSettingState = r0;
        r0 = r30;
        r0 = r0.statusRequest;
        r23 = r0;
        if (r23 == 0) goto L_0x0172;
    L_0x0163:
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r0 = r30;
        r0 = r0.statusRequest;
        r24 = r0;
        r25 = 1;
        r23.cancelRequest(r24, r25);
    L_0x0172:
        r16 = new org.telegram.tgnet.TLRPC$TL_account_updateStatus;
        r16.<init>();
        r23 = 1;
        r0 = r23;
        r1 = r16;
        r1.offline = r0;
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r24 = new org.telegram.messenger.MessagesController$42;
        r0 = r24;
        r1 = r30;
        r0.<init>();
        r0 = r23;
        r1 = r16;
        r2 = r24;
        r23 = r0.sendRequest(r1, r2);
        r0 = r23;
        r1 = r30;
        r1.statusRequest = r0;
        goto L_0x00ab;
    L_0x019e:
        r4 = 0;
    L_0x019f:
        r23 = 3;
        r0 = r23;
        if (r4 >= r0) goto L_0x01e5;
    L_0x01a5:
        r0 = r30;
        r24 = r0.getUpdatesStartTime(r4);
        r26 = 0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 == 0) goto L_0x01e2;
    L_0x01b1:
        r0 = r30;
        r24 = r0.getUpdatesStartTime(r4);
        r26 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r24 = r24 + r26;
        r23 = (r24 > r10 ? 1 : (r24 == r10 ? 0 : -1));
        if (r23 >= 0) goto L_0x01e2;
    L_0x01bf:
        r23 = "tmessages";
        r24 = new java.lang.StringBuilder;
        r24.<init>();
        r0 = r24;
        r24 = r0.append(r4);
        r25 = " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE";
        r24 = r24.append(r25);
        r24 = r24.toString();
        org.telegram.messenger.FileLog.m11e(r23, r24);
        r23 = 0;
        r0 = r30;
        r1 = r23;
        r0.processUpdatesQueue(r4, r1);
    L_0x01e2:
        r4 = r4 + 1;
        goto L_0x019f;
    L_0x01e5:
        r0 = r30;
        r0 = r0.channelViewsToSend;
        r23 = r0;
        r23 = r23.size();
        if (r23 != 0) goto L_0x01fd;
    L_0x01f1:
        r0 = r30;
        r0 = r0.channelViewsToReload;
        r23 = r0;
        r23 = r23.size();
        if (r23 == 0) goto L_0x028c;
    L_0x01fd:
        r24 = java.lang.System.currentTimeMillis();
        r0 = r30;
        r0 = r0.lastViewsCheckTime;
        r26 = r0;
        r24 = r24 - r26;
        r24 = java.lang.Math.abs(r24);
        r26 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 < 0) goto L_0x028c;
    L_0x0213:
        r24 = java.lang.System.currentTimeMillis();
        r0 = r24;
        r2 = r30;
        r2.lastViewsCheckTime = r0;
        r7 = 0;
    L_0x021e:
        r23 = 2;
        r0 = r23;
        if (r7 >= r0) goto L_0x028c;
    L_0x0224:
        if (r7 != 0) goto L_0x0233;
    L_0x0226:
        r0 = r30;
        r6 = r0.channelViewsToSend;
    L_0x022a:
        r23 = r6.size();
        if (r23 != 0) goto L_0x0238;
    L_0x0230:
        r7 = r7 + 1;
        goto L_0x021e;
    L_0x0233:
        r0 = r30;
        r6 = r0.channelViewsToReload;
        goto L_0x022a;
    L_0x0238:
        r4 = 0;
    L_0x0239:
        r23 = r6.size();
        r0 = r23;
        if (r4 >= r0) goto L_0x0288;
    L_0x0241:
        r13 = r6.keyAt(r4);
        r16 = new org.telegram.tgnet.TLRPC$TL_messages_getMessagesViews;
        r16.<init>();
        r23 = getInputPeer(r13);
        r0 = r23;
        r1 = r16;
        r1.peer = r0;
        r23 = r6.get(r13);
        r23 = (java.util.ArrayList) r23;
        r0 = r23;
        r1 = r16;
        r1.id = r0;
        if (r4 != 0) goto L_0x0285;
    L_0x0262:
        r23 = 1;
    L_0x0264:
        r0 = r23;
        r1 = r16;
        r1.increment = r0;
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r24 = new org.telegram.messenger.MessagesController$43;
        r0 = r24;
        r1 = r30;
        r2 = r16;
        r0.<init>(r13, r2);
        r0 = r23;
        r1 = r16;
        r2 = r24;
        r0.sendRequest(r1, r2);
        r4 = r4 + 1;
        goto L_0x0239;
    L_0x0285:
        r23 = 0;
        goto L_0x0264;
    L_0x0288:
        r6.clear();
        goto L_0x0230;
    L_0x028c:
        r0 = r30;
        r0 = r0.onlinePrivacy;
        r23 = r0;
        r23 = r23.isEmpty();
        if (r23 != 0) goto L_0x030d;
    L_0x0298:
        r18 = 0;
        r23 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r8 = r23.getCurrentTime();
        r0 = r30;
        r0 = r0.onlinePrivacy;
        r23 = r0;
        r23 = r23.entrySet();
        r12 = r23.iterator();
    L_0x02b0:
        r23 = r12.hasNext();
        if (r23 == 0) goto L_0x02e1;
    L_0x02b6:
        r9 = r12.next();
        r9 = (java.util.Map.Entry) r9;
        r23 = r9.getValue();
        r23 = (java.lang.Integer) r23;
        r23 = r23.intValue();
        r24 = r8 + -30;
        r0 = r23;
        r1 = r24;
        if (r0 >= r1) goto L_0x02b0;
    L_0x02ce:
        if (r18 != 0) goto L_0x02d5;
    L_0x02d0:
        r18 = new java.util.ArrayList;
        r18.<init>();
    L_0x02d5:
        r23 = r9.getKey();
        r0 = r18;
        r1 = r23;
        r0.add(r1);
        goto L_0x02b0;
    L_0x02e1:
        if (r18 == 0) goto L_0x030d;
    L_0x02e3:
        r12 = r18.iterator();
    L_0x02e7:
        r23 = r12.hasNext();
        if (r23 == 0) goto L_0x0301;
    L_0x02ed:
        r19 = r12.next();
        r19 = (java.lang.Integer) r19;
        r0 = r30;
        r0 = r0.onlinePrivacy;
        r23 = r0;
        r0 = r23;
        r1 = r19;
        r0.remove(r1);
        goto L_0x02e7;
    L_0x0301:
        r23 = new org.telegram.messenger.MessagesController$44;
        r0 = r23;
        r1 = r30;
        r0.<init>();
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r23);
    L_0x030d:
        r0 = r30;
        r0 = r0.shortPollChannels;
        r23 = r0;
        r23 = r23.size();
        if (r23 == 0) goto L_0x0372;
    L_0x0319:
        r4 = 0;
    L_0x031a:
        r0 = r30;
        r0 = r0.shortPollChannels;
        r23 = r0;
        r23 = r23.size();
        r0 = r23;
        if (r4 >= r0) goto L_0x0372;
    L_0x0328:
        r0 = r30;
        r0 = r0.shortPollChannels;
        r23 = r0;
        r0 = r23;
        r13 = r0.keyAt(r4);
        r0 = r30;
        r0 = r0.shortPollChannels;
        r23 = r0;
        r0 = r23;
        r17 = r0.get(r13);
        r0 = r17;
        r0 = (long) r0;
        r24 = r0;
        r26 = java.lang.System.currentTimeMillis();
        r28 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r26 = r26 / r28;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 >= 0) goto L_0x036f;
    L_0x0351:
        r0 = r30;
        r0 = r0.shortPollChannels;
        r23 = r0;
        r0 = r23;
        r0.delete(r13);
        r0 = r30;
        r0 = r0.needShortPollChannels;
        r23 = r0;
        r0 = r23;
        r23 = r0.indexOfKey(r13);
        if (r23 < 0) goto L_0x036f;
    L_0x036a:
        r0 = r30;
        r0.getChannelDifference(r13);
    L_0x036f:
        r4 = r4 + 1;
        goto L_0x031a;
    L_0x0372:
        r0 = r30;
        r0 = r0.printingUsers;
        r23 = r0;
        r23 = r23.isEmpty();
        if (r23 == 0) goto L_0x0394;
    L_0x037e:
        r0 = r30;
        r0 = r0.lastPrintingStringCount;
        r23 = r0;
        r0 = r30;
        r0 = r0.printingUsers;
        r24 = r0;
        r24 = r24.size();
        r0 = r23;
        r1 = r24;
        if (r0 == r1) goto L_0x0417;
    L_0x0394:
        r20 = 0;
        r15 = new java.util.ArrayList;
        r0 = r30;
        r0 = r0.printingUsers;
        r23 = r0;
        r23 = r23.keySet();
        r0 = r23;
        r15.<init>(r0);
        r7 = 0;
    L_0x03a8:
        r23 = r15.size();
        r0 = r23;
        if (r7 >= r0) goto L_0x0406;
    L_0x03b0:
        r13 = r15.get(r7);
        r13 = (java.lang.Long) r13;
        r0 = r30;
        r0 = r0.printingUsers;
        r23 = r0;
        r0 = r23;
        r5 = r0.get(r13);
        r5 = (java.util.ArrayList) r5;
        r4 = 0;
    L_0x03c5:
        r23 = r5.size();
        r0 = r23;
        if (r4 >= r0) goto L_0x03ed;
    L_0x03cd:
        r22 = r5.get(r4);
        r22 = (org.telegram.messenger.MessagesController.PrintingUser) r22;
        r0 = r22;
        r0 = r0.lastTime;
        r24 = r0;
        r26 = 5900; // 0x170c float:8.268E-42 double:2.915E-320;
        r24 = r24 + r26;
        r23 = (r24 > r10 ? 1 : (r24 == r10 ? 0 : -1));
        if (r23 >= 0) goto L_0x03ea;
    L_0x03e1:
        r20 = 1;
        r0 = r22;
        r5.remove(r0);
        r4 = r4 + -1;
    L_0x03ea:
        r4 = r4 + 1;
        goto L_0x03c5;
    L_0x03ed:
        r23 = r5.isEmpty();
        if (r23 == 0) goto L_0x0403;
    L_0x03f3:
        r0 = r30;
        r0 = r0.printingUsers;
        r23 = r0;
        r0 = r23;
        r0.remove(r13);
        r15.remove(r7);
        r7 = r7 + -1;
    L_0x0403:
        r7 = r7 + 1;
        goto L_0x03a8;
    L_0x0406:
        r30.updatePrintingStrings();
        if (r20 == 0) goto L_0x0417;
    L_0x040b:
        r23 = new org.telegram.messenger.MessagesController$45;
        r0 = r23;
        r1 = r30;
        r0.<init>();
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r23);
    L_0x0417:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateTimerProc():void");
    }

    private String getUserNameForTyping(User user) {
        if (user == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (user.first_name != null && user.first_name.length() > 0) {
            return user.first_name;
        }
        if (user.last_name == null || user.last_name.length() <= 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        return user.last_name;
    }

    private void updatePrintingStrings() {
        HashMap<Long, CharSequence> newPrintingStrings = new HashMap();
        HashMap<Long, Integer> newPrintingStringsTypes = new HashMap();
        ArrayList<Long> keys = new ArrayList(this.printingUsers.keySet());
        for (Entry<Long, ArrayList<PrintingUser>> entry : this.printingUsers.entrySet()) {
            long key = ((Long) entry.getKey()).longValue();
            ArrayList<PrintingUser> arr = (ArrayList) entry.getValue();
            int lower_id = (int) key;
            User user;
            Long valueOf;
            if (lower_id > 0 || lower_id == 0 || arr.size() == UPDATE_MASK_NAME) {
                PrintingUser pu = (PrintingUser) arr.get(0);
                user = getUser(Integer.valueOf(pu.userId));
                if (user != null) {
                    Object[] objArr;
                    if (pu.action instanceof TL_sendMessageRecordAudioAction) {
                        if (lower_id < 0) {
                            valueOf = Long.valueOf(key);
                            objArr = new Object[UPDATE_MASK_NAME];
                            objArr[0] = getUserNameForTyping(user);
                            newPrintingStrings.put(valueOf, LocaleController.formatString("IsRecordingAudio", C0691R.string.IsRecordingAudio, objArr));
                        } else {
                            newPrintingStrings.put(Long.valueOf(key), LocaleController.getString("RecordingAudio", C0691R.string.RecordingAudio));
                        }
                        newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(UPDATE_MASK_NAME));
                    } else {
                        if (pu.action instanceof TL_sendMessageUploadAudioAction) {
                            if (lower_id < 0) {
                                valueOf = Long.valueOf(key);
                                objArr = new Object[UPDATE_MASK_NAME];
                                objArr[0] = getUserNameForTyping(user);
                                newPrintingStrings.put(valueOf, LocaleController.formatString("IsSendingAudio", C0691R.string.IsSendingAudio, objArr));
                            } else {
                                newPrintingStrings.put(Long.valueOf(key), LocaleController.getString("SendingAudio", C0691R.string.SendingAudio));
                            }
                            newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(UPDATE_MASK_AVATAR));
                        } else {
                            if (!(pu.action instanceof TL_sendMessageUploadVideoAction)) {
                                if (!(pu.action instanceof TL_sendMessageRecordVideoAction)) {
                                    if (pu.action instanceof TL_sendMessageUploadDocumentAction) {
                                        if (lower_id < 0) {
                                            valueOf = Long.valueOf(key);
                                            objArr = new Object[UPDATE_MASK_NAME];
                                            objArr[0] = getUserNameForTyping(user);
                                            newPrintingStrings.put(valueOf, LocaleController.formatString("IsSendingFile", C0691R.string.IsSendingFile, objArr));
                                        } else {
                                            newPrintingStrings.put(Long.valueOf(key), LocaleController.getString("SendingFile", C0691R.string.SendingFile));
                                        }
                                        newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(UPDATE_MASK_AVATAR));
                                    } else {
                                        if (pu.action instanceof TL_sendMessageUploadPhotoAction) {
                                            if (lower_id < 0) {
                                                valueOf = Long.valueOf(key);
                                                objArr = new Object[UPDATE_MASK_NAME];
                                                objArr[0] = getUserNameForTyping(user);
                                                newPrintingStrings.put(valueOf, LocaleController.formatString("IsSendingPhoto", C0691R.string.IsSendingPhoto, objArr));
                                            } else {
                                                newPrintingStrings.put(Long.valueOf(key), LocaleController.getString("SendingPhoto", C0691R.string.SendingPhoto));
                                            }
                                            newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(UPDATE_MASK_AVATAR));
                                        } else {
                                            if (lower_id < 0) {
                                                valueOf = Long.valueOf(key);
                                                Object[] objArr2 = new Object[UPDATE_MASK_AVATAR];
                                                objArr2[0] = getUserNameForTyping(user);
                                                objArr2[UPDATE_MASK_NAME] = LocaleController.getString("IsTyping", C0691R.string.IsTyping);
                                                newPrintingStrings.put(valueOf, String.format("%s %s", objArr2));
                                            } else {
                                                newPrintingStrings.put(Long.valueOf(key), LocaleController.getString("Typing", C0691R.string.Typing));
                                            }
                                            newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(0));
                                        }
                                    }
                                }
                            }
                            if (lower_id < 0) {
                                valueOf = Long.valueOf(key);
                                objArr = new Object[UPDATE_MASK_NAME];
                                objArr[0] = getUserNameForTyping(user);
                                newPrintingStrings.put(valueOf, LocaleController.formatString("IsSendingVideo", C0691R.string.IsSendingVideo, objArr));
                            } else {
                                newPrintingStrings.put(Long.valueOf(key), LocaleController.getString("SendingVideoStatus", C0691R.string.SendingVideoStatus));
                            }
                            newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(UPDATE_MASK_AVATAR));
                        }
                    }
                } else {
                    return;
                }
            }
            int count = 0;
            String label = TtmlNode.ANONYMOUS_REGION_ID;
            Iterator i$ = arr.iterator();
            while (i$.hasNext()) {
                user = getUser(Integer.valueOf(((PrintingUser) i$.next()).userId));
                if (user != null) {
                    if (label.length() != 0) {
                        label = label + ", ";
                    }
                    label = label + getUserNameForTyping(user);
                    count += UPDATE_MASK_NAME;
                }
                if (count == UPDATE_MASK_AVATAR) {
                    break;
                }
            }
            if (label.length() != 0) {
                if (count == UPDATE_MASK_NAME) {
                    valueOf = Long.valueOf(key);
                    objArr2 = new Object[UPDATE_MASK_AVATAR];
                    objArr2[0] = label;
                    objArr2[UPDATE_MASK_NAME] = LocaleController.getString("IsTyping", C0691R.string.IsTyping);
                    newPrintingStrings.put(valueOf, String.format("%s %s", objArr2));
                } else if (arr.size() > UPDATE_MASK_AVATAR) {
                    valueOf = Long.valueOf(key);
                    objArr2 = new Object[UPDATE_MASK_AVATAR];
                    objArr2[0] = label;
                    objArr2[UPDATE_MASK_NAME] = LocaleController.formatPluralString("AndMoreTyping", arr.size() - 2);
                    newPrintingStrings.put(valueOf, String.format("%s %s", objArr2));
                } else {
                    valueOf = Long.valueOf(key);
                    objArr2 = new Object[UPDATE_MASK_AVATAR];
                    objArr2[0] = label;
                    objArr2[UPDATE_MASK_NAME] = LocaleController.getString("AreTyping", C0691R.string.AreTyping);
                    newPrintingStrings.put(valueOf, String.format("%s %s", objArr2));
                }
                newPrintingStringsTypes.put(Long.valueOf(key), Integer.valueOf(0));
            }
        }
        this.lastPrintingStringCount = newPrintingStrings.size();
        AndroidUtilities.runOnUIThread(new AnonymousClass46(newPrintingStrings, newPrintingStringsTypes));
    }

    public void cancelTyping(int action, long dialog_id) {
        HashMap<Long, Boolean> typings = (HashMap) this.sendingTypings.get(Integer.valueOf(action));
        if (typings != null) {
            typings.remove(Long.valueOf(dialog_id));
        }
    }

    public void sendTyping(long dialog_id, int action, int classGuid) {
        if (dialog_id != 0) {
            HashMap<Long, Boolean> typings = (HashMap) this.sendingTypings.get(Integer.valueOf(action));
            if (typings == null || typings.get(Long.valueOf(dialog_id)) == null) {
                if (typings == null) {
                    typings = new HashMap();
                    this.sendingTypings.put(Integer.valueOf(action), typings);
                }
                int lower_part = (int) dialog_id;
                int high_id = (int) (dialog_id >> UPDATE_MASK_CHAT_MEMBERS);
                int reqId;
                if (lower_part != 0) {
                    if (high_id != UPDATE_MASK_NAME) {
                        TL_messages_setTyping req = new TL_messages_setTyping();
                        req.peer = getInputPeer(lower_part);
                        if (req.peer instanceof TL_inputPeerChannel) {
                            Chat chat = getChat(Integer.valueOf(req.peer.channel_id));
                            if (chat == null || !chat.megagroup) {
                                return;
                            }
                        }
                        if (req.peer != null) {
                            if (action == 0) {
                                req.action = new TL_sendMessageTypingAction();
                            } else if (action == UPDATE_MASK_NAME) {
                                req.action = new TL_sendMessageRecordAudioAction();
                            } else if (action == UPDATE_MASK_AVATAR) {
                                req.action = new TL_sendMessageCancelAction();
                            } else if (action == 3) {
                                req.action = new TL_sendMessageUploadDocumentAction();
                            } else if (action == UPDATE_MASK_STATUS) {
                                req.action = new TL_sendMessageUploadPhotoAction();
                            } else if (action == 5) {
                                req.action = new TL_sendMessageUploadVideoAction();
                            }
                            typings.put(Long.valueOf(dialog_id), Boolean.valueOf(true));
                            reqId = ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass47(action, dialog_id), UPDATE_MASK_AVATAR);
                            if (classGuid != 0) {
                                ConnectionsManager.getInstance().bindRequestToGuid(reqId, classGuid);
                            }
                        }
                    }
                } else if (action == 0) {
                    EncryptedChat chat2 = getEncryptedChat(Integer.valueOf(high_id));
                    if (chat2.auth_key != null && chat2.auth_key.length > UPDATE_MASK_NAME && (chat2 instanceof TL_encryptedChat)) {
                        TL_messages_setEncryptedTyping req2 = new TL_messages_setEncryptedTyping();
                        req2.peer = new TL_inputEncryptedChat();
                        req2.peer.chat_id = chat2.id;
                        req2.peer.access_hash = chat2.access_hash;
                        req2.typing = true;
                        typings.put(Long.valueOf(dialog_id), Boolean.valueOf(true));
                        reqId = ConnectionsManager.getInstance().sendRequest(req2, new AnonymousClass48(action, dialog_id), UPDATE_MASK_AVATAR);
                        if (classGuid != 0) {
                            ConnectionsManager.getInstance().bindRequestToGuid(reqId, classGuid);
                        }
                    }
                }
            }
        }
    }

    public void loadMessages(long dialog_id, int count, int max_id, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex) {
        loadMessages(dialog_id, count, max_id, fromCache, midDate, classGuid, load_type, last_message_id, isChannel, loadIndex, 0, 0, 0, false);
    }

    public void loadMessages(long dialog_id, int count, int max_id, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex, int first_unread, int unread_count, int last_date, boolean queryFromServer) {
        FileLog.m11e("tmessages", "load messages in chat " + dialog_id + " count " + count + " max_id " + max_id + " cache " + fromCache + " mindate = " + midDate + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " index " + loadIndex + " firstUnread " + first_unread + " underad count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        int lower_part = (int) dialog_id;
        if (fromCache || lower_part == 0) {
            MessagesStorage.getInstance().getMessages(dialog_id, count, max_id, midDate, classGuid, load_type, isChannel, loadIndex);
            return;
        }
        TLObject req = new TL_messages_getHistory();
        req.peer = getInputPeer(lower_part);
        if (load_type == 3) {
            req.add_offset = (-count) / UPDATE_MASK_AVATAR;
        } else if (load_type == UPDATE_MASK_NAME) {
            req.add_offset = (-count) - 1;
        } else if (load_type == UPDATE_MASK_AVATAR && max_id != 0) {
            req.add_offset = (-count) + 6;
        } else if (lower_part < 0 && max_id != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
            req.add_offset = -1;
            req.limit += UPDATE_MASK_NAME;
        }
        req.limit = count;
        req.offset_id = max_id;
        ConnectionsManager instance = ConnectionsManager.getInstance();
        ConnectionsManager.getInstance().bindRequestToGuid(r21.sendRequest(req, new AnonymousClass49(count, dialog_id, max_id, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, isChannel, loadIndex, queryFromServer)), classGuid);
    }

    public void reloadWebPages(long dialog_id, HashMap<String, ArrayList<MessageObject>> webpagesToReload) {
        for (Entry<String, ArrayList<MessageObject>> entry : webpagesToReload.entrySet()) {
            String url = (String) entry.getKey();
            ArrayList<MessageObject> messages = (ArrayList) entry.getValue();
            ArrayList<MessageObject> arrayList = (ArrayList) this.reloadingWebpages.get(url);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.reloadingWebpages.put(url, arrayList);
            }
            arrayList.addAll(messages);
            TL_messages_getWebPagePreview req = new TL_messages_getWebPagePreview();
            req.message = url;
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass50(url, dialog_id));
        }
    }

    public void processLoadedMessages(messages_Messages messagesRes, long dialog_id, int count, int max_id, boolean isCache, int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, boolean isChannel, boolean isEnd, int loadIndex, boolean queryFromServer) {
        FileLog.m11e("tmessages", "processLoadedMessages size " + messagesRes.messages.size() + " in chat " + dialog_id + " count " + count + " max_id " + max_id + " cache " + isCache + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " isChannel " + isChannel + " index " + loadIndex + " firstUnread " + first_unread + " underad count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        Utilities.stageQueue.postRunnable(new AnonymousClass51(messagesRes, dialog_id, isCache, count, load_type, queryFromServer, first_unread, max_id, classGuid, last_message_id, isChannel, loadIndex, unread_count, last_date, isEnd));
    }

    public void loadDialogs(int offset, int count, boolean fromCache) {
        if (!this.loadingDialogs) {
            this.loadingDialogs = true;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            FileLog.m11e("tmessages", "load cacheOffset = " + offset + " count = " + count + " cache = " + fromCache);
            if (fromCache) {
                MessagesStorage.getInstance().getDialogs(offset == 0 ? 0 : this.nextDialogsCacheOffset, count);
                return;
            }
            TL_messages_getDialogs req = new TL_messages_getDialogs();
            req.limit = count;
            boolean found = false;
            for (int a = this.dialogs.size() - 1; a >= 0; a--) {
                TL_dialog dialog = (TL_dialog) this.dialogs.get(a);
                int high_id = (int) (dialog.id >> UPDATE_MASK_CHAT_MEMBERS);
                if (!(((int) dialog.id) == 0 || high_id == UPDATE_MASK_NAME || dialog.top_message <= 0)) {
                    MessageObject message = (MessageObject) this.dialogMessage.get(Long.valueOf(dialog.id));
                    if (message != null && message.getId() > 0) {
                        int id;
                        req.offset_date = message.messageOwner.date;
                        req.offset_id = message.messageOwner.id;
                        if (message.messageOwner.to_id.channel_id != 0) {
                            id = -message.messageOwner.to_id.channel_id;
                        } else if (message.messageOwner.to_id.chat_id != 0) {
                            id = -message.messageOwner.to_id.chat_id;
                        } else {
                            id = message.messageOwner.to_id.user_id;
                        }
                        req.offset_peer = getInputPeer(id);
                        found = true;
                        if (!found) {
                            req.offset_peer = new TL_inputPeerEmpty();
                        }
                        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass52(count));
                    }
                }
            }
            if (found) {
                req.offset_peer = new TL_inputPeerEmpty();
            }
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass52(count));
        }
    }

    private void migrateDialogs(int offset, int offsetDate, int offsetUser, int offsetChat, int offsetChannel, long accessPeer) {
        if (!this.migratingDialogs && offset != -1) {
            this.migratingDialogs = true;
            TL_messages_getDialogs req = new TL_messages_getDialogs();
            req.limit = 100;
            req.offset_id = offset;
            req.offset_date = offsetDate;
            if (offset == 0) {
                req.offset_peer = new TL_inputPeerEmpty();
            } else {
                if (offsetChannel != 0) {
                    req.offset_peer = new TL_inputPeerChannel();
                    req.offset_peer.channel_id = offsetChannel;
                } else if (offsetUser != 0) {
                    req.offset_peer = new TL_inputPeerUser();
                    req.offset_peer.user_id = offsetUser;
                } else {
                    req.offset_peer = new TL_inputPeerChat();
                    req.offset_peer.chat_id = offsetChat;
                }
                req.offset_peer.access_hash = accessPeer;
            }
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController.53.1 */
                class C05871 implements Runnable {
                    final /* synthetic */ messages_Dialogs val$dialogsRes;

                    /* renamed from: org.telegram.messenger.MessagesController.53.1.1 */
                    class C05861 implements Runnable {
                        C05861() {
                        }

                        public void run() {
                            MessagesController.this.migratingDialogs = false;
                        }
                    }

                    C05871(messages_Dialogs org_telegram_tgnet_TLRPC_messages_Dialogs) {
                        this.val$dialogsRes = org_telegram_tgnet_TLRPC_messages_Dialogs;
                    }

                    public void run() {
                        try {
                            int a;
                            Message message;
                            int offsetId;
                            TL_dialog dialog;
                            if (this.val$dialogsRes.dialogs.size() == 100) {
                                Message lastMessage = null;
                                for (a = 0; a < this.val$dialogsRes.messages.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                    message = (Message) this.val$dialogsRes.messages.get(a);
                                    if (lastMessage == null || message.date < lastMessage.date) {
                                        lastMessage = message;
                                    }
                                }
                                offsetId = lastMessage.id;
                                UserConfig.migrateOffsetDate = lastMessage.date;
                                Chat chat;
                                if (lastMessage.to_id.channel_id != 0) {
                                    UserConfig.migrateOffsetChannelId = lastMessage.to_id.channel_id;
                                    UserConfig.migrateOffsetChatId = 0;
                                    UserConfig.migrateOffsetUserId = 0;
                                    for (a = 0; a < this.val$dialogsRes.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                        chat = (Chat) this.val$dialogsRes.chats.get(a);
                                        if (chat.id == UserConfig.migrateOffsetChannelId) {
                                            UserConfig.migrateOffsetAccess = chat.access_hash;
                                            break;
                                        }
                                    }
                                } else if (lastMessage.to_id.chat_id != 0) {
                                    UserConfig.migrateOffsetChatId = lastMessage.to_id.chat_id;
                                    UserConfig.migrateOffsetChannelId = 0;
                                    UserConfig.migrateOffsetUserId = 0;
                                    for (a = 0; a < this.val$dialogsRes.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                        chat = (Chat) this.val$dialogsRes.chats.get(a);
                                        if (chat.id == UserConfig.migrateOffsetChatId) {
                                            UserConfig.migrateOffsetAccess = chat.access_hash;
                                            break;
                                        }
                                    }
                                } else if (lastMessage.to_id.user_id != 0) {
                                    UserConfig.migrateOffsetUserId = lastMessage.to_id.user_id;
                                    UserConfig.migrateOffsetChatId = 0;
                                    UserConfig.migrateOffsetChannelId = 0;
                                    for (a = 0; a < this.val$dialogsRes.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                        User user = (User) this.val$dialogsRes.users.get(a);
                                        if (user.id == UserConfig.migrateOffsetUserId) {
                                            UserConfig.migrateOffsetAccess = user.access_hash;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                offsetId = -1;
                            }
                            StringBuilder stringBuilder = new StringBuilder(this.val$dialogsRes.dialogs.size() * 12);
                            HashMap<Long, TL_dialog> dialogHashMap = new HashMap();
                            for (a = 0; a < this.val$dialogsRes.dialogs.size(); a += MessagesController.UPDATE_MASK_NAME) {
                                dialog = (TL_dialog) this.val$dialogsRes.dialogs.get(a);
                                if (dialog.peer.channel_id != 0) {
                                    dialog.id = (long) (-dialog.peer.channel_id);
                                } else if (dialog.peer.chat_id != 0) {
                                    dialog.id = (long) (-dialog.peer.chat_id);
                                } else {
                                    dialog.id = (long) dialog.peer.user_id;
                                }
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(",");
                                }
                                stringBuilder.append(dialog.id);
                                dialogHashMap.put(Long.valueOf(dialog.id), dialog);
                            }
                            SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                            objArr[0] = stringBuilder.toString();
                            SQLiteCursor cursor = database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", objArr), new Object[0]);
                            while (cursor.next()) {
                                long did = cursor.longValue(0);
                                dialog = (TL_dialog) dialogHashMap.remove(Long.valueOf(did));
                                if (dialog != null) {
                                    this.val$dialogsRes.dialogs.remove(dialog);
                                    a = 0;
                                    while (a < this.val$dialogsRes.messages.size()) {
                                        message = (Message) this.val$dialogsRes.messages.get(a);
                                        if (MessageObject.getDialogId(message) == did) {
                                            this.val$dialogsRes.messages.remove(a);
                                            a--;
                                            if (message.id == dialog.top_message) {
                                                dialog.top_message = 0;
                                            }
                                            if (dialog.top_message == 0) {
                                                break;
                                            }
                                        }
                                        a += MessagesController.UPDATE_MASK_NAME;
                                    }
                                }
                            }
                            cursor.dispose();
                            cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                            if (cursor.next()) {
                                int date = Math.max(1441062000, cursor.intValue(0));
                                a = 0;
                                while (a < this.val$dialogsRes.messages.size()) {
                                    message = (Message) this.val$dialogsRes.messages.get(a);
                                    if (message.date < date) {
                                        offsetId = -1;
                                        this.val$dialogsRes.messages.remove(a);
                                        a--;
                                        dialog = (TL_dialog) dialogHashMap.remove(Long.valueOf(MessageObject.getDialogId(message)));
                                        if (dialog != null) {
                                            this.val$dialogsRes.dialogs.remove(dialog);
                                        }
                                    }
                                    a += MessagesController.UPDATE_MASK_NAME;
                                }
                            }
                            cursor.dispose();
                            MessagesController.this.processLoadedDialogs(this.val$dialogsRes, null, offsetId, 0, 0, false, true);
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                            AndroidUtilities.runOnUIThread(new C05861());
                        }
                    }
                }

                /* renamed from: org.telegram.messenger.MessagesController.53.2 */
                class C05882 implements Runnable {
                    C05882() {
                    }

                    public void run() {
                        MessagesController.this.migratingDialogs = false;
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05871((messages_Dialogs) response));
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C05882());
                }
            });
        }
    }

    public void processLoadedDialogs(messages_Dialogs dialogsRes, ArrayList<EncryptedChat> encChats, int offset, int count, int loadType, boolean resetEnd, boolean migrate) {
        Utilities.stageQueue.postRunnable(new AnonymousClass54(loadType, dialogsRes, resetEnd, count, offset, encChats, migrate));
    }

    private void applyDialogNotificationsSettings(long dialog_id, PeerNotifySettings notify_settings) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        int currentValue = preferences.getInt("notify2_" + dialog_id, 0);
        int currentValue2 = preferences.getInt("notifyuntil_" + dialog_id, 0);
        Editor editor = preferences.edit();
        boolean updated = false;
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(Long.valueOf(dialog_id));
        if (dialog != null) {
            dialog.notify_settings = notify_settings;
        }
        editor.putBoolean("silent_" + dialog_id, notify_settings.silent);
        if (notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime()) {
            int until = 0;
            if (notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                if (currentValue != UPDATE_MASK_AVATAR) {
                    updated = true;
                    editor.putInt("notify2_" + dialog_id, UPDATE_MASK_AVATAR);
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                }
            } else if (!(currentValue == 3 && currentValue2 == notify_settings.mute_until)) {
                updated = true;
                until = notify_settings.mute_until;
                editor.putInt("notify2_" + dialog_id, 3);
                editor.putInt("notifyuntil_" + dialog_id, notify_settings.mute_until);
                if (dialog != null) {
                    dialog.notify_settings.mute_until = until;
                }
            }
            MessagesStorage.getInstance().setDialogFlags(dialog_id, (((long) until) << UPDATE_MASK_CHAT_MEMBERS) | 1);
            NotificationsController.getInstance().removeNotificationsForDialog(dialog_id);
        } else {
            if (!(currentValue == 0 || currentValue == UPDATE_MASK_NAME)) {
                updated = true;
                if (dialog != null) {
                    dialog.notify_settings.mute_until = 0;
                }
                editor.remove("notify2_" + dialog_id);
            }
            MessagesStorage.getInstance().setDialogFlags(dialog_id, 0);
        }
        editor.commit();
        if (updated) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TL_dialog> dialogs) {
        Editor editor = null;
        for (int a = 0; a < dialogs.size(); a += UPDATE_MASK_NAME) {
            TL_dialog dialog = (TL_dialog) dialogs.get(a);
            if (dialog.peer != null && (dialog.notify_settings instanceof TL_peerNotifySettings)) {
                int dialog_id;
                if (editor == null) {
                    editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                }
                if (dialog.peer.user_id != 0) {
                    dialog_id = dialog.peer.user_id;
                } else if (dialog.peer.chat_id != 0) {
                    dialog_id = -dialog.peer.chat_id;
                } else {
                    dialog_id = -dialog.peer.channel_id;
                }
                editor.putBoolean("silent_" + dialog_id, dialog.notify_settings.silent);
                if (dialog.notify_settings.mute_until == 0) {
                    editor.remove("notify2_" + dialog_id);
                } else if (dialog.notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                    editor.putInt("notify2_" + dialog_id, UPDATE_MASK_AVATAR);
                    dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                } else {
                    editor.putInt("notify2_" + dialog_id, 3);
                    editor.putInt("notifyuntil_" + dialog_id, dialog.notify_settings.mute_until);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public void processDialogsUpdateRead(HashMap<Long, Integer> dialogsToUpdate) {
        AndroidUtilities.runOnUIThread(new AnonymousClass55(dialogsToUpdate));
    }

    protected void checkLastDialogMessage(TL_dialog dialog, InputPeer peer, long taskId) {
        Throwable e;
        long newTaskId;
        int lower_id = (int) dialog.id;
        if (lower_id != 0 && !this.checkingLastMessagesDialogs.containsKey(Integer.valueOf(lower_id))) {
            InputPeer inputPeer;
            TL_messages_getHistory req = new TL_messages_getHistory();
            if (peer == null) {
                inputPeer = getInputPeer(lower_id);
            } else {
                inputPeer = peer;
            }
            req.peer = inputPeer;
            if (req.peer != null) {
                req.limit = UPDATE_MASK_NAME;
                this.checkingLastMessagesDialogs.put(Integer.valueOf(lower_id), Boolean.valueOf(true));
                if (taskId == 0) {
                    NativeByteBuffer data = null;
                    try {
                        NativeByteBuffer data2 = new NativeByteBuffer(peer.getObjectSize() + 40);
                        try {
                            data2.writeInt32(UPDATE_MASK_AVATAR);
                            data2.writeInt64(dialog.id);
                            data2.writeInt32(dialog.top_message);
                            data2.writeInt32(dialog.read_inbox_max_id);
                            data2.writeInt32(dialog.read_outbox_max_id);
                            data2.writeInt32(dialog.unread_count);
                            data2.writeInt32(dialog.last_message_date);
                            data2.writeInt32(dialog.pts);
                            data2.writeInt32(dialog.flags);
                            peer.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.m13e("tmessages", e);
                            newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass56(dialog, newTaskId, lower_id));
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.m13e("tmessages", e);
                        newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass56(dialog, newTaskId, lower_id));
                    }
                    newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass56(dialog, newTaskId, lower_id));
            }
        }
    }

    public void processDialogsUpdate(messages_Dialogs dialogsRes, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new AnonymousClass57(dialogsRes));
    }

    public void addToViewsQueue(Message message, boolean reload) {
        ArrayList<Long> arrayList = new ArrayList();
        long messageId = (long) message.id;
        if (message.to_id.channel_id != 0) {
            messageId |= ((long) message.to_id.channel_id) << UPDATE_MASK_CHAT_MEMBERS;
        }
        arrayList.add(Long.valueOf(messageId));
        MessagesStorage.getInstance().markMessagesContentAsRead(arrayList);
        Utilities.stageQueue.postRunnable(new AnonymousClass58(message));
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        ArrayList<Long> arrayList = new ArrayList();
        long messageId = (long) messageObject.getId();
        if (messageObject.messageOwner.to_id.channel_id != 0) {
            messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << UPDATE_MASK_CHAT_MEMBERS;
        }
        arrayList.add(Long.valueOf(messageId));
        MessagesStorage.getInstance().markMessagesContentAsRead(arrayList);
        NotificationCenter instance = NotificationCenter.getInstance();
        int i = NotificationCenter.messagesReadContent;
        Object[] objArr = new Object[UPDATE_MASK_NAME];
        objArr[0] = arrayList;
        instance.postNotificationName(i, objArr);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, LinearLayoutManager.INVALID_OFFSET);
            return;
        }
        TL_messages_readMessageContents req = new TL_messages_readMessageContents();
        req.id.add(Integer.valueOf(messageObject.getId()));
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                    MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                }
            }
        });
    }

    public void markMessageAsRead(long dialog_id, long random_id, int ttl) {
        if (random_id != 0 && dialog_id != 0) {
            if (ttl > 0 || ttl == LinearLayoutManager.INVALID_OFFSET) {
                int high_id = (int) (dialog_id >> UPDATE_MASK_CHAT_MEMBERS);
                if (((int) dialog_id) == 0) {
                    EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
                    if (chat != null) {
                        ArrayList<Long> random_ids = new ArrayList();
                        random_ids.add(Long.valueOf(random_id));
                        SecretChatHelper.getInstance().sendMessagesReadMessage(chat, random_ids, null);
                        if (ttl > 0) {
                            int time = ConnectionsManager.getInstance().getCurrentTime();
                            MessagesStorage.getInstance().createTaskForSecretChat(chat.id, time, time, 0, random_ids);
                        }
                    }
                }
            }
        }
    }

    public void markDialogAsRead(long dialog_id, int max_id, int max_positive_id, int max_date, boolean was, boolean popup) {
        int lower_part = (int) dialog_id;
        int high_id = (int) (dialog_id >> UPDATE_MASK_CHAT_MEMBERS);
        TLObject req;
        if (lower_part != 0) {
            if (max_positive_id != 0 && high_id != UPDATE_MASK_NAME) {
                InputPeer inputPeer = getInputPeer(lower_part);
                long messageId = (long) max_positive_id;
                TLObject request;
                if (inputPeer instanceof TL_inputPeerChannel) {
                    request = new TL_channels_readHistory();
                    request.channel = getInputChannel(-lower_part);
                    request.max_id = max_positive_id;
                    req = request;
                    messageId |= ((long) (-lower_part)) << UPDATE_MASK_CHAT_MEMBERS;
                } else {
                    request = new TL_messages_readHistory();
                    request.peer = inputPeer;
                    request.max_id = max_positive_id;
                    req = request;
                }
                Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), max_positive_id)));
                MessagesStorage.getInstance().processPendingRead(dialog_id, messageId, max_date);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass60(dialog_id, popup, max_positive_id));
                if (max_positive_id != Integer.MAX_VALUE) {
                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null && (response instanceof TL_messages_affectedMessages)) {
                                TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                                MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                            }
                        }
                    });
                }
            }
        } else if (max_date != 0) {
            EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
            if (chat.auth_key != null && chat.auth_key.length > UPDATE_MASK_NAME && (chat instanceof TL_encryptedChat)) {
                req = new TL_messages_readEncryptedHistory();
                req.peer = new TL_inputEncryptedChat();
                req.peer.chat_id = chat.id;
                req.peer.access_hash = chat.access_hash;
                req.max_date = max_date;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
            MessagesStorage.getInstance().processPendingRead(dialog_id, (long) max_id, max_date);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass63(dialog_id, max_date, popup));
            if (chat.ttl > 0 && was) {
                int serverTime = Math.max(ConnectionsManager.getInstance().getCurrentTime(), max_date);
                MessagesStorage.getInstance().createTaskForSecretChat(chat.id, serverTime, serverTime, 0, null);
            }
        }
    }

    public int createChat(String title, ArrayList<Integer> selectedContacts, String about, int type, BaseFragment fragment) {
        int a;
        if (type == UPDATE_MASK_NAME) {
            TL_chat chat = new TL_chat();
            chat.id = UserConfig.lastBroadcastId;
            chat.title = title;
            chat.photo = new TL_chatPhotoEmpty();
            chat.participants_count = selectedContacts.size();
            chat.date = (int) (System.currentTimeMillis() / 1000);
            chat.version = UPDATE_MASK_NAME;
            UserConfig.lastBroadcastId--;
            putChat(chat, false);
            ArrayList<Chat> chatsArrays = new ArrayList();
            chatsArrays.add(chat);
            MessagesStorage.getInstance().putUsersAndChats(null, chatsArrays, true, true);
            TL_chatFull chatFull = new TL_chatFull();
            chatFull.id = chat.id;
            chatFull.chat_photo = new TL_photoEmpty();
            chatFull.notify_settings = new TL_peerNotifySettingsEmpty();
            chatFull.exported_invite = new TL_chatInviteEmpty();
            chatFull.participants = new TL_chatParticipants();
            chatFull.participants.chat_id = chat.id;
            chatFull.participants.admin_id = UserConfig.getClientUserId();
            chatFull.participants.version = UPDATE_MASK_NAME;
            for (a = 0; a < selectedContacts.size(); a += UPDATE_MASK_NAME) {
                TL_chatParticipant participant = new TL_chatParticipant();
                participant.user_id = ((Integer) selectedContacts.get(a)).intValue();
                participant.inviter_id = UserConfig.getClientUserId();
                participant.date = (int) (System.currentTimeMillis() / 1000);
                chatFull.participants.participants.add(participant);
            }
            MessagesStorage.getInstance().updateChatInfo(chatFull, false);
            TL_messageService newMsg = new TL_messageService();
            newMsg.action = new TL_messageActionCreatedBroadcastList();
            int newMessageId = UserConfig.getNewMessageId();
            newMsg.id = newMessageId;
            newMsg.local_id = newMessageId;
            newMsg.from_id = UserConfig.getClientUserId();
            newMsg.dialog_id = AndroidUtilities.makeBroadcastId(chat.id);
            newMsg.to_id = new TL_peerChat();
            newMsg.to_id.chat_id = chat.id;
            newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
            newMsg.random_id = 0;
            newMsg.flags |= UPDATE_MASK_READ_DIALOG_MESSAGE;
            UserConfig.saveConfig(false);
            MessageObject newMsgObj = new MessageObject(newMsg, this.users, true);
            newMsgObj.messageOwner.send_state = 0;
            ArrayList<MessageObject> objArr = new ArrayList();
            objArr.add(newMsgObj);
            ArrayList arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
            updateInterfaceWithMessages(newMsg.dialog_id, objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.chatDidCreated;
            Object[] objArr2 = new Object[UPDATE_MASK_NAME];
            objArr2[0] = Integer.valueOf(chat.id);
            instance.postNotificationName(i, objArr2);
            return 0;
        } else if (type == 0) {
            req = new TL_messages_createChat();
            req.title = title;
            for (a = 0; a < selectedContacts.size(); a += UPDATE_MASK_NAME) {
                User user = getUser((Integer) selectedContacts.get(a));
                if (user != null) {
                    req.users.add(getInputUser(user));
                }
            }
            return ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass64(fragment), UPDATE_MASK_AVATAR);
        } else if (type != UPDATE_MASK_AVATAR && type != UPDATE_MASK_STATUS) {
            return 0;
        } else {
            req = new TL_channels_createChannel();
            req.title = title;
            req.about = about;
            if (type == UPDATE_MASK_STATUS) {
                req.megagroup = true;
            } else {
                req.broadcast = true;
            }
            return ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass65(fragment), UPDATE_MASK_AVATAR);
        }
    }

    public void convertToMegaGroup(Context context, int chat_id) {
        TL_messages_migrateChat req = new TL_messages_migrateChat();
        req.chat_id = chat_id;
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new AnonymousClass67(ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass66(context, progressDialog))));
        try {
            progressDialog.show();
        } catch (Exception e) {
        }
    }

    public void addUsersToChannel(int chat_id, ArrayList<InputUser> users, BaseFragment fragment) {
        if (users != null && !users.isEmpty()) {
            TL_channels_inviteToChannel req = new TL_channels_inviteToChannel();
            req.channel = getInputChannel(chat_id);
            req.users = users;
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass68(fragment));
        }
    }

    public void toogleChannelInvites(int chat_id, boolean enabled) {
        TL_channels_toggleInvites req = new TL_channels_toggleInvites();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        }, UPDATE_MASK_USER_PRINT);
    }

    public void toogleChannelSignatures(int chat_id, boolean enabled) {
        TL_channels_toggleSignatures req = new TL_channels_toggleSignatures();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController.70.1 */
            class C06051 implements Runnable {
                C06051() {
                }

                public void run() {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL);
                    instance.postNotificationName(i, objArr);
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    AndroidUtilities.runOnUIThread(new C06051());
                }
            }
        }, UPDATE_MASK_USER_PRINT);
    }

    public void updateChannelAbout(int chat_id, String about, ChatFull info) {
        if (info != null) {
            TL_channels_editAbout req = new TL_channels_editAbout();
            req.channel = getInputChannel(chat_id);
            req.about = about;
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass71(info, about), UPDATE_MASK_USER_PRINT);
        }
    }

    public void updateChannelUserName(int chat_id, String userName) {
        TL_channels_updateUsername req = new TL_channels_updateUsername();
        req.channel = getInputChannel(chat_id);
        req.username = userName;
        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass72(chat_id, userName), UPDATE_MASK_USER_PRINT);
    }

    public void sendBotStart(User user, String botHash) {
        if (user != null) {
            TL_messages_startBot req = new TL_messages_startBot();
            req.bot = getInputUser(user);
            req.peer = getInputPeer(user.id);
            req.start_param = botHash;
            req.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            });
        }
    }

    public void toggleAdminMode(int chat_id, boolean enabled) {
        TL_messages_toggleChatAdmins req = new TL_messages_toggleChatAdmins();
        req.chat_id = chat_id;
        req.enabled = enabled;
        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass74(chat_id));
    }

    public void toggleUserAdmin(int chat_id, int user_id, boolean admin) {
        TL_messages_editChatAdmin req = new TL_messages_editChatAdmin();
        req.chat_id = chat_id;
        req.user_id = getInputUser(user_id);
        req.is_admin = admin;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void addUserToChat(int chat_id, User user, ChatFull info, int count_fwd, String botHash, BaseFragment fragment) {
        if (user != null) {
            if (chat_id > 0) {
                TLObject request;
                boolean isChannel = ChatObject.isChannel(chat_id);
                boolean isMegagroup = isChannel && getChat(Integer.valueOf(chat_id)).megagroup;
                InputUser inputUser = getInputUser(user);
                TLObject req;
                if (botHash != null && (!isChannel || isMegagroup)) {
                    req = new TL_messages_startBot();
                    req.bot = inputUser;
                    if (isChannel) {
                        req.peer = getInputPeer(-chat_id);
                    } else {
                        req.peer = new TL_inputPeerChat();
                        req.peer.chat_id = chat_id;
                    }
                    req.start_param = botHash;
                    req.random_id = Utilities.random.nextLong();
                    request = req;
                } else if (!isChannel) {
                    req = new TL_messages_addChatUser();
                    req.chat_id = chat_id;
                    req.fwd_limit = count_fwd;
                    req.user_id = inputUser;
                    request = req;
                } else if (inputUser instanceof TL_inputUserSelf) {
                    if (!this.joiningToChannels.contains(Integer.valueOf(chat_id))) {
                        req = new TL_channels_joinChannel();
                        req.channel = getInputChannel(chat_id);
                        request = req;
                        this.joiningToChannels.add(Integer.valueOf(chat_id));
                    } else {
                        return;
                    }
                } else if (!user.bot || isMegagroup) {
                    req = new TL_channels_inviteToChannel();
                    req.channel = getInputChannel(chat_id);
                    req.users.add(inputUser);
                    request = req;
                } else {
                    req = new TL_channels_editAdmin();
                    req.channel = getInputChannel(chat_id);
                    req.user_id = getInputUser(user);
                    req.role = new TL_channelRoleEditor();
                    request = req;
                }
                ConnectionsManager.getInstance().sendRequest(request, new AnonymousClass76(isChannel, inputUser, chat_id, fragment, isMegagroup));
            } else if (info instanceof TL_chatFull) {
                int a = 0;
                while (a < info.participants.participants.size()) {
                    if (((ChatParticipant) info.participants.participants.get(a)).user_id != user.id) {
                        a += UPDATE_MASK_NAME;
                    } else {
                        return;
                    }
                }
                Chat chat = getChat(Integer.valueOf(chat_id));
                chat.participants_count += UPDATE_MASK_NAME;
                ArrayList<Chat> chatArrayList = new ArrayList();
                chatArrayList.add(chat);
                MessagesStorage.getInstance().putUsersAndChats(null, chatArrayList, true, true);
                TL_chatParticipant newPart = new TL_chatParticipant();
                newPart.user_id = user.id;
                newPart.inviter_id = UserConfig.getClientUserId();
                newPart.date = ConnectionsManager.getInstance().getCurrentTime();
                info.participants.participants.add(0, newPart);
                MessagesStorage.getInstance().updateChatInfo(info, true);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatInfoDidLoaded;
                Object[] objArr = new Object[UPDATE_MASK_STATUS];
                objArr[0] = info;
                objArr[UPDATE_MASK_NAME] = Integer.valueOf(0);
                objArr[UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                objArr[3] = null;
                instance.postNotificationName(i, objArr);
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.updateInterfaces;
                objArr = new Object[UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(UPDATE_MASK_CHAT_MEMBERS);
                instance.postNotificationName(i, objArr);
            }
        }
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info) {
        if (user != null) {
            Chat chat;
            if (chat_id > 0) {
                TLObject request;
                InputUser inputUser = getInputUser(user);
                chat = getChat(Integer.valueOf(chat_id));
                boolean isChannel = ChatObject.isChannel(chat);
                TLObject req;
                if (!isChannel) {
                    req = new TL_messages_deleteChatUser();
                    req.chat_id = chat_id;
                    req.user_id = getInputUser(user);
                    request = req;
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    req = new TL_channels_kickFromChannel();
                    req.channel = getInputChannel(chat);
                    req.user_id = inputUser;
                    req.kicked = true;
                    request = req;
                } else if (chat.creator) {
                    req = new TL_channels_deleteChannel();
                    req.channel = getInputChannel(chat);
                    request = req;
                } else {
                    req = new TL_channels_leaveChannel();
                    req.channel = getInputChannel(chat);
                    request = req;
                }
                ConnectionsManager.getInstance().sendRequest(request, new AnonymousClass77(user, chat_id, isChannel, inputUser), UPDATE_MASK_USER_PRINT);
            } else if (info instanceof TL_chatFull) {
                NotificationCenter instance;
                int i;
                Object[] objArr;
                chat = getChat(Integer.valueOf(chat_id));
                chat.participants_count--;
                ArrayList<Chat> chatArrayList = new ArrayList();
                chatArrayList.add(chat);
                MessagesStorage.getInstance().putUsersAndChats(null, chatArrayList, true, true);
                boolean changed = false;
                for (int a = 0; a < info.participants.participants.size(); a += UPDATE_MASK_NAME) {
                    if (((ChatParticipant) info.participants.participants.get(a)).user_id == user.id) {
                        info.participants.participants.remove(a);
                        changed = true;
                        break;
                    }
                }
                if (changed) {
                    MessagesStorage.getInstance().updateChatInfo(info, true);
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.chatInfoDidLoaded;
                    objArr = new Object[UPDATE_MASK_STATUS];
                    objArr[0] = info;
                    objArr[UPDATE_MASK_NAME] = Integer.valueOf(0);
                    objArr[UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                    objArr[3] = null;
                    instance.postNotificationName(i, objArr);
                }
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.updateInterfaces;
                objArr = new Object[UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(UPDATE_MASK_CHAT_MEMBERS);
                instance.postNotificationName(i, objArr);
            }
        }
    }

    public void changeChatTitle(int chat_id, String title) {
        if (chat_id > 0) {
            TLObject request;
            TLObject req;
            if (ChatObject.isChannel(chat_id)) {
                req = new TL_channels_editTitle();
                req.channel = getInputChannel(chat_id);
                req.title = title;
                request = req;
            } else {
                req = new TL_messages_editChatTitle();
                req.chat_id = chat_id;
                req.title = title;
                request = req;
            }
            ConnectionsManager.getInstance().sendRequest(request, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            }, UPDATE_MASK_USER_PRINT);
            return;
        }
        Chat chat = getChat(Integer.valueOf(chat_id));
        chat.title = title;
        ArrayList<Chat> chatArrayList = new ArrayList();
        chatArrayList.add(chat);
        MessagesStorage.getInstance().putUsersAndChats(null, chatArrayList, true, true);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter instance = NotificationCenter.getInstance();
        int i = NotificationCenter.updateInterfaces;
        Object[] objArr = new Object[UPDATE_MASK_NAME];
        objArr[0] = Integer.valueOf(UPDATE_MASK_CHAT_NAME);
        instance.postNotificationName(i, objArr);
    }

    public void changeChatAvatar(int chat_id, InputFile uploadedAvatar) {
        TLObject request;
        TLObject req;
        if (ChatObject.isChannel(chat_id)) {
            req = new TL_channels_editPhoto();
            req.channel = getInputChannel(chat_id);
            if (uploadedAvatar != null) {
                req.photo = new TL_inputChatUploadedPhoto();
                req.photo.file = uploadedAvatar;
                req.photo.crop = new TL_inputPhotoCropAuto();
            } else {
                req.photo = new TL_inputChatPhotoEmpty();
            }
            request = req;
        } else {
            req = new TL_messages_editChatPhoto();
            req.chat_id = chat_id;
            if (uploadedAvatar != null) {
                req.photo = new TL_inputChatUploadedPhoto();
                req.photo.file = uploadedAvatar;
                req.photo.crop = new TL_inputPhotoCropAuto();
            } else {
                req.photo = new TL_inputChatPhotoEmpty();
            }
            request = req;
        }
        ConnectionsManager.getInstance().sendRequest(request, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        }, UPDATE_MASK_USER_PRINT);
    }

    public void unregistedPush() {
        if (UserConfig.registeredForPush && UserConfig.pushString.length() == 0) {
            TL_account_unregisterDevice req = new TL_account_unregisterDevice();
            req.token = UserConfig.pushString;
            req.token_type = UPDATE_MASK_AVATAR;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }

    public void performLogout(boolean byUser) {
        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().clear().commit();
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0).commit();
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("gifhint").commit();
        if (byUser) {
            unregistedPush();
            ConnectionsManager.getInstance().sendRequest(new TL_auth_logOut(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ConnectionsManager.getInstance().cleanup();
                }
            });
        } else {
            ConnectionsManager.getInstance().cleanup();
        }
        UserConfig.clearConfig();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        MessagesStorage.getInstance().cleanup(false);
        cleanup();
        ContactsController.getInstance().deleteAllAppAccounts();
    }

    public void generateUpdateMessage() {
        if (!BuildVars.DEBUG_VERSION && UserConfig.lastUpdateVersion != null && !UserConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)) {
            ConnectionsManager.getInstance().sendRequest(new TL_help_getAppChangelog(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        UserConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
                        UserConfig.saveConfig(false);
                    }
                    if (response instanceof TL_help_appChangelog) {
                        TL_updateServiceNotification update = new TL_updateServiceNotification();
                        update.message = ((TL_help_appChangelog) response).text;
                        update.media = new TL_messageMediaEmpty();
                        update.type = "update";
                        update.popup = false;
                        ArrayList<Update> updates = new ArrayList();
                        updates.add(update);
                        MessagesController.this.processUpdateArray(updates, null, null, false);
                    }
                }
            });
        }
    }

    public void registerForPush(String regid) {
        if (regid != null && regid.length() != 0 && !this.registeringForPush && UserConfig.getClientUserId() != 0) {
            if (!UserConfig.registeredForPush || !regid.equals(UserConfig.pushString)) {
                this.registeringForPush = true;
                TL_account_registerDevice req = new TL_account_registerDevice();
                req.token_type = UPDATE_MASK_AVATAR;
                req.token = regid;
                ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass83(regid));
            }
        }
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            ConnectionsManager.getInstance().sendRequest(new TL_updates_getState(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    MessagesController.this.updatingState = false;
                    if (error == null) {
                        TL_updates_state res = (TL_updates_state) response;
                        MessagesStorage.lastDateValue = res.date;
                        MessagesStorage.lastPtsValue = res.pts;
                        MessagesStorage.lastSeqValue = res.seq;
                        MessagesStorage.lastQtsValue = res.qts;
                        for (int a = 0; a < 3; a += MessagesController.UPDATE_MASK_NAME) {
                            MessagesController.this.processUpdatesQueue(a, MessagesController.UPDATE_MASK_AVATAR);
                        }
                        MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
                    } else if (error.code != 401) {
                        MessagesController.this.loadCurrentState();
                    }
                }
            });
        }
    }

    private int getUpdateSeq(Updates updates) {
        if (updates instanceof TL_updatesCombined) {
            return updates.seq_start;
        }
        return updates.seq;
    }

    private void setUpdatesStartTime(int type, long time) {
        if (type == 0) {
            this.updatesStartWaitTimeSeq = time;
        } else if (type == UPDATE_MASK_NAME) {
            this.updatesStartWaitTimePts = time;
        } else if (type == UPDATE_MASK_AVATAR) {
            this.updatesStartWaitTimeQts = time;
        }
    }

    public long getUpdatesStartTime(int type) {
        if (type == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (type == UPDATE_MASK_NAME) {
            return this.updatesStartWaitTimePts;
        }
        if (type == UPDATE_MASK_AVATAR) {
            return this.updatesStartWaitTimeQts;
        }
        return 0;
    }

    private int isValidUpdate(Updates updates, int type) {
        if (type == 0) {
            int seq = getUpdateSeq(updates);
            if (MessagesStorage.lastSeqValue + UPDATE_MASK_NAME == seq || MessagesStorage.lastSeqValue == seq) {
                return 0;
            }
            if (MessagesStorage.lastSeqValue >= seq) {
                return UPDATE_MASK_AVATAR;
            }
            return UPDATE_MASK_NAME;
        } else if (type == UPDATE_MASK_NAME) {
            if (updates.pts <= MessagesStorage.lastPtsValue) {
                return UPDATE_MASK_AVATAR;
            }
            if (MessagesStorage.lastPtsValue + updates.pts_count == updates.pts) {
                return 0;
            }
            return UPDATE_MASK_NAME;
        } else if (type != UPDATE_MASK_AVATAR) {
            return 0;
        } else {
            if (updates.pts <= MessagesStorage.lastQtsValue) {
                return UPDATE_MASK_AVATAR;
            }
            if (MessagesStorage.lastQtsValue + updates.updates.size() == updates.pts) {
                return 0;
            }
            return UPDATE_MASK_NAME;
        }
    }

    private void processChannelsUpdatesQueue(int channelId, int state) {
        ArrayList<Updates> updatesQueue = (ArrayList) this.updatesQueueChannels.get(Integer.valueOf(channelId));
        if (updatesQueue != null) {
            Integer channelPts = (Integer) this.channelsPts.get(Integer.valueOf(channelId));
            if (updatesQueue.isEmpty() || channelPts == null) {
                this.updatesQueueChannels.remove(Integer.valueOf(channelId));
                return;
            }
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
            boolean anyProceed = false;
            if (state == UPDATE_MASK_AVATAR) {
                this.channelsPts.put(Integer.valueOf(channelId), Integer.valueOf(((Updates) updatesQueue.get(0)).pts));
            }
            int a = 0;
            while (updatesQueue.size() > 0) {
                int updateState;
                Updates updates = (Updates) updatesQueue.get(a);
                if (updates.pts <= channelPts.intValue()) {
                    updateState = UPDATE_MASK_AVATAR;
                } else if (channelPts.intValue() + updates.pts_count == updates.pts) {
                    updateState = 0;
                } else {
                    updateState = UPDATE_MASK_NAME;
                }
                if (updateState == 0) {
                    processUpdates(updates, true);
                    anyProceed = true;
                    updatesQueue.remove(a);
                    a--;
                } else if (updateState == UPDATE_MASK_NAME) {
                    Long updatesStartWaitTime = (Long) this.updatesStartWaitTimeChannels.get(Integer.valueOf(channelId));
                    if (updatesStartWaitTime == null || (!anyProceed && Math.abs(System.currentTimeMillis() - updatesStartWaitTime.longValue()) > 1500)) {
                        FileLog.m11e("tmessages", "HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - getChannelDifference ");
                        this.updatesStartWaitTimeChannels.remove(Integer.valueOf(channelId));
                        this.updatesQueueChannels.remove(Integer.valueOf(channelId));
                        getChannelDifference(channelId);
                        return;
                    }
                    FileLog.m11e("tmessages", "HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - will wait more time");
                    if (anyProceed) {
                        this.updatesStartWaitTimeChannels.put(Integer.valueOf(channelId), Long.valueOf(System.currentTimeMillis()));
                        return;
                    }
                    return;
                } else {
                    updatesQueue.remove(a);
                    a--;
                }
                a += UPDATE_MASK_NAME;
            }
            this.updatesQueueChannels.remove(Integer.valueOf(channelId));
            this.updatesStartWaitTimeChannels.remove(Integer.valueOf(channelId));
            FileLog.m11e("tmessages", "UPDATES CHANNEL " + channelId + " QUEUE PROCEED - OK");
        }
    }

    private void processUpdatesQueue(int type, int state) {
        ArrayList<Updates> updatesQueue = null;
        if (type == 0) {
            updatesQueue = this.updatesQueueSeq;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(updates), MessagesController.this.getUpdateSeq(updates2));
                }
            });
        } else if (type == UPDATE_MASK_NAME) {
            updatesQueue = this.updatesQueuePts;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
        } else if (type == UPDATE_MASK_AVATAR) {
            updatesQueue = this.updatesQueueQts;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
        }
        if (!(updatesQueue == null || updatesQueue.isEmpty())) {
            Updates updates;
            boolean anyProceed = false;
            if (state == UPDATE_MASK_AVATAR) {
                updates = (Updates) updatesQueue.get(0);
                if (type == 0) {
                    MessagesStorage.lastSeqValue = getUpdateSeq(updates);
                } else if (type == UPDATE_MASK_NAME) {
                    MessagesStorage.lastPtsValue = updates.pts;
                } else {
                    MessagesStorage.lastQtsValue = updates.pts;
                }
            }
            int a = 0;
            while (updatesQueue.size() > 0) {
                updates = (Updates) updatesQueue.get(a);
                int updateState = isValidUpdate(updates, type);
                if (updateState == 0) {
                    processUpdates(updates, true);
                    anyProceed = true;
                    updatesQueue.remove(a);
                    a--;
                } else if (updateState != UPDATE_MASK_NAME) {
                    updatesQueue.remove(a);
                    a--;
                } else if (getUpdatesStartTime(type) == 0 || (!anyProceed && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(type)) > 1500)) {
                    FileLog.m11e("tmessages", "HOLE IN UPDATES QUEUE - getDifference");
                    setUpdatesStartTime(type, 0);
                    updatesQueue.clear();
                    getDifference();
                    return;
                } else {
                    FileLog.m11e("tmessages", "HOLE IN UPDATES QUEUE - will wait more time");
                    if (anyProceed) {
                        setUpdatesStartTime(type, System.currentTimeMillis());
                        return;
                    }
                    return;
                }
                a += UPDATE_MASK_NAME;
            }
            updatesQueue.clear();
            FileLog.m11e("tmessages", "UPDATES QUEUE PROCEED - OK");
        }
        setUpdatesStartTime(type, 0);
    }

    protected void loadUnknownChannel(Chat channel, long taskId) {
        Throwable e;
        long newTaskId;
        if ((channel instanceof TL_channel) && !this.gettingUnknownChannels.containsKey(Integer.valueOf(channel.id))) {
            this.gettingUnknownChannels.put(Integer.valueOf(channel.id), Boolean.valueOf(true));
            TL_inputPeerChannel inputPeer = new TL_inputPeerChannel();
            inputPeer.channel_id = channel.id;
            inputPeer.access_hash = channel.access_hash;
            TL_messages_getPeerDialogs req = new TL_messages_getPeerDialogs();
            req.peers.add(inputPeer);
            if (taskId == 0) {
                NativeByteBuffer data = null;
                try {
                    NativeByteBuffer data2 = new NativeByteBuffer(channel.getObjectSize() + UPDATE_MASK_STATUS);
                    try {
                        data2.writeInt32(0);
                        channel.serializeToStream(data2);
                        data = data2;
                    } catch (Exception e2) {
                        e = e2;
                        data = data2;
                        FileLog.m13e("tmessages", e);
                        newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass89(newTaskId, channel));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.m13e("tmessages", e);
                    newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                    ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass89(newTaskId, channel));
                }
                newTaskId = MessagesStorage.getInstance().createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass89(newTaskId, channel));
        }
    }

    public void startShortPoll(int channelId, boolean stop) {
        Utilities.stageQueue.postRunnable(new AnonymousClass90(stop, channelId));
    }

    private void getChannelDifference(int channelId) {
        getChannelDifference(channelId, 0, 0);
    }

    protected void getChannelDifference(int channelId, int newDialogType, long taskId) {
        Throwable e;
        long newTaskId;
        TL_updates_getChannelDifference req;
        Boolean gettingDifferenceChannel = (Boolean) this.gettingDifferenceChannels.get(Integer.valueOf(channelId));
        if (gettingDifferenceChannel == null) {
            gettingDifferenceChannel = Boolean.valueOf(false);
        }
        if (!gettingDifferenceChannel.booleanValue()) {
            Integer channelPts;
            int limit = 100;
            if (newDialogType != UPDATE_MASK_NAME) {
                channelPts = (Integer) this.channelsPts.get(Integer.valueOf(channelId));
                if (channelPts == null) {
                    channelPts = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(channelId));
                    if (channelPts.intValue() != 0) {
                        this.channelsPts.put(Integer.valueOf(channelId), channelPts);
                    }
                    if (channelPts.intValue() == 0 && newDialogType == UPDATE_MASK_AVATAR) {
                        return;
                    }
                }
                if (channelPts.intValue() == 0) {
                    return;
                }
            } else if (((Integer) this.channelsPts.get(Integer.valueOf(channelId))) == null) {
                channelPts = Integer.valueOf(UPDATE_MASK_NAME);
                limit = UPDATE_MASK_NAME;
            } else {
                return;
            }
            if (taskId == 0) {
                NativeByteBuffer data = null;
                try {
                    NativeByteBuffer data2 = new NativeByteBuffer(12);
                    try {
                        data2.writeInt32(UPDATE_MASK_NAME);
                        data2.writeInt32(channelId);
                        data2.writeInt32(newDialogType);
                        data = data2;
                    } catch (Exception e2) {
                        e = e2;
                        data = data2;
                        FileLog.m13e("tmessages", e);
                        newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                        this.gettingDifferenceChannels.put(Integer.valueOf(channelId), Boolean.valueOf(true));
                        req = new TL_updates_getChannelDifference();
                        req.channel = getInputChannel(channelId);
                        req.filter = new TL_channelMessagesFilterEmpty();
                        req.pts = channelPts.intValue();
                        req.limit = limit;
                        FileLog.m11e("tmessages", "start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
                        ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass91(channelId, newDialogType, newTaskId));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.m13e("tmessages", e);
                    newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                    this.gettingDifferenceChannels.put(Integer.valueOf(channelId), Boolean.valueOf(true));
                    req = new TL_updates_getChannelDifference();
                    req.channel = getInputChannel(channelId);
                    req.filter = new TL_channelMessagesFilterEmpty();
                    req.pts = channelPts.intValue();
                    req.limit = limit;
                    FileLog.m11e("tmessages", "start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
                    ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass91(channelId, newDialogType, newTaskId));
                }
                newTaskId = MessagesStorage.getInstance().createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            this.gettingDifferenceChannels.put(Integer.valueOf(channelId), Boolean.valueOf(true));
            req = new TL_updates_getChannelDifference();
            req.channel = getInputChannel(channelId);
            req.filter = new TL_channelMessagesFilterEmpty();
            req.pts = channelPts.intValue();
            req.limit = limit;
            FileLog.m11e("tmessages", "start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass91(channelId, newDialogType, newTaskId));
        }
    }

    private void checkChannelError(String text, int channelId) {
        int i = -1;
        switch (text.hashCode()) {
            case -1809401834:
                if (text.equals("USER_BANNED_IN_CHANNEL")) {
                    i = UPDATE_MASK_AVATAR;
                    break;
                }
                break;
            case -795226617:
                if (text.equals("CHANNEL_PRIVATE")) {
                    i = 0;
                    break;
                }
                break;
            case -471086771:
                if (text.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                    i = UPDATE_MASK_NAME;
                    break;
                }
                break;
        }
        NotificationCenter instance;
        int i2;
        Object[] objArr;
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.chatInfoCantLoad;
                objArr = new Object[UPDATE_MASK_AVATAR];
                objArr[0] = Integer.valueOf(channelId);
                objArr[UPDATE_MASK_NAME] = Integer.valueOf(0);
                instance.postNotificationName(i2, objArr);
            case UPDATE_MASK_NAME /*1*/:
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.chatInfoCantLoad;
                objArr = new Object[UPDATE_MASK_AVATAR];
                objArr[0] = Integer.valueOf(channelId);
                objArr[UPDATE_MASK_NAME] = Integer.valueOf(UPDATE_MASK_NAME);
                instance.postNotificationName(i2, objArr);
            case UPDATE_MASK_AVATAR /*2*/:
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.chatInfoCantLoad;
                Object[] objArr2 = new Object[UPDATE_MASK_AVATAR];
                objArr2[0] = Integer.valueOf(channelId);
                objArr2[UPDATE_MASK_NAME] = Integer.valueOf(UPDATE_MASK_AVATAR);
                instance.postNotificationName(i2, objArr2);
            default:
        }
    }

    public void getDifference() {
        getDifference(MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue, false);
    }

    public void getDifference(int pts, int date, int qts, boolean slice) {
        registerForPush(UserConfig.pushString);
        if (MessagesStorage.lastPtsValue == 0) {
            loadCurrentState();
        } else if (slice || !this.gettingDifference) {
            if (!this.firstGettingTask) {
                getNewDeleteTask(null);
                this.firstGettingTask = true;
            }
            this.gettingDifference = true;
            TL_updates_getDifference req = new TL_updates_getDifference();
            req.pts = pts;
            req.date = date;
            req.qts = qts;
            if (req.date == 0) {
                req.date = ConnectionsManager.getInstance().getCurrentTime();
            }
            FileLog.m11e("tmessages", "start getDifference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue);
            ConnectionsManager.getInstance().setIsUpdating(true);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController.92.1 */
                class C06241 implements Runnable {
                    final /* synthetic */ updates_Difference val$res;

                    C06241(updates_Difference org_telegram_tgnet_TLRPC_updates_Difference) {
                        this.val$res = org_telegram_tgnet_TLRPC_updates_Difference;
                    }

                    public void run() {
                        MessagesController.this.putUsers(this.val$res.users, false);
                        MessagesController.this.putChats(this.val$res.chats, false);
                    }
                }

                /* renamed from: org.telegram.messenger.MessagesController.92.2 */
                class C06302 implements Runnable {
                    final /* synthetic */ HashMap val$chatsDict;
                    final /* synthetic */ ArrayList val$msgUpdates;
                    final /* synthetic */ updates_Difference val$res;
                    final /* synthetic */ HashMap val$usersDict;

                    /* renamed from: org.telegram.messenger.MessagesController.92.2.1 */
                    class C06251 implements Runnable {
                        final /* synthetic */ HashMap val$corrected;

                        C06251(HashMap hashMap) {
                            this.val$corrected = hashMap;
                        }

                        public void run() {
                            for (Entry<Integer, long[]> entry : this.val$corrected.entrySet()) {
                                Integer newId = (Integer) entry.getKey();
                                long[] ids = (long[]) entry.getValue();
                                Integer oldId = Integer.valueOf((int) ids[MessagesController.UPDATE_MASK_NAME]);
                                SendMessagesHelper.getInstance().processSentMessage(oldId.intValue());
                                NotificationCenter instance = NotificationCenter.getInstance();
                                int i = NotificationCenter.messageReceivedByServer;
                                Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                                objArr[0] = oldId;
                                objArr[MessagesController.UPDATE_MASK_NAME] = newId;
                                objArr[MessagesController.UPDATE_MASK_AVATAR] = null;
                                objArr[3] = Long.valueOf(ids[0]);
                                instance.postNotificationName(i, objArr);
                            }
                        }
                    }

                    /* renamed from: org.telegram.messenger.MessagesController.92.2.2 */
                    class C06292 implements Runnable {

                        /* renamed from: org.telegram.messenger.MessagesController.92.2.2.1 */
                        class C06261 implements Runnable {
                            final /* synthetic */ HashMap val$messages;

                            C06261(HashMap hashMap) {
                                this.val$messages = hashMap;
                            }

                            public void run() {
                                for (Entry<Long, ArrayList<MessageObject>> pair : this.val$messages.entrySet()) {
                                    ArrayList<MessageObject> value = (ArrayList) pair.getValue();
                                    MessagesController.this.updateInterfaceWithMessages(((Long) pair.getKey()).longValue(), value);
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                        }

                        /* renamed from: org.telegram.messenger.MessagesController.92.2.2.2 */
                        class C06282 implements Runnable {
                            final /* synthetic */ ArrayList val$pushMessages;

                            /* renamed from: org.telegram.messenger.MessagesController.92.2.2.2.1 */
                            class C06271 implements Runnable {
                                C06271() {
                                }

                                public void run() {
                                    NotificationsController.getInstance().processNewMessages(C06282.this.val$pushMessages, !(C06302.this.val$res instanceof TL_updates_differenceSlice));
                                }
                            }

                            C06282(ArrayList arrayList) {
                                this.val$pushMessages = arrayList;
                            }

                            public void run() {
                                if (!this.val$pushMessages.isEmpty()) {
                                    AndroidUtilities.runOnUIThread(new C06271());
                                }
                                MessagesStorage.getInstance().putMessages(C06302.this.val$res.new_messages, true, false, false, MediaController.getInstance().getAutodownloadMask());
                            }
                        }

                        C06292() {
                        }

                        /* JADX WARNING: inconsistent code. */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            /*
                            r22 = this;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_messages;
                            r17 = r0;
                            r17 = r17.isEmpty();
                            if (r17 == 0) goto L_0x0030;
                        L_0x0018:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_encrypted_messages;
                            r17 = r0;
                            r17 = r17.isEmpty();
                            if (r17 != 0) goto L_0x02d3;
                        L_0x0030:
                            r11 = new java.util.HashMap;
                            r11.<init>();
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_encrypted_messages;
                            r17 = r0;
                            r8 = r17.iterator();
                        L_0x004b:
                            r17 = r8.hasNext();
                            if (r17 == 0) goto L_0x0091;
                        L_0x0051:
                            r7 = r8.next();
                            r7 = (org.telegram.tgnet.TLRPC.EncryptedMessage) r7;
                            r17 = org.telegram.messenger.SecretChatHelper.getInstance();
                            r0 = r17;
                            r6 = r0.decryptMessage(r7);
                            if (r6 == 0) goto L_0x004b;
                        L_0x0063:
                            r17 = r6.isEmpty();
                            if (r17 != 0) goto L_0x004b;
                        L_0x0069:
                            r9 = r6.iterator();
                        L_0x006d:
                            r17 = r9.hasNext();
                            if (r17 == 0) goto L_0x004b;
                        L_0x0073:
                            r10 = r9.next();
                            r10 = (org.telegram.tgnet.TLRPC.Message) r10;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_messages;
                            r17 = r0;
                            r0 = r17;
                            r0.add(r10);
                            goto L_0x006d;
                        L_0x0091:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_messages;
                            r17 = r0;
                            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r17);
                            r13 = new java.util.ArrayList;
                            r13.<init>();
                            r4 = 0;
                        L_0x00ac:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_messages;
                            r17 = r0;
                            r17 = r17.size();
                            r0 = r17;
                            if (r4 >= r0) goto L_0x02ac;
                        L_0x00c6:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.new_messages;
                            r17 = r0;
                            r0 = r17;
                            r10 = r0.get(r4);
                            r10 = (org.telegram.tgnet.TLRPC.Message) r10;
                            r0 = r10.action;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
                            r17 = r0;
                            if (r17 == 0) goto L_0x011d;
                        L_0x00ec:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$usersDict;
                            r17 = r0;
                            r0 = r10.action;
                            r18 = r0;
                            r0 = r18;
                            r0 = r0.user_id;
                            r18 = r0;
                            r18 = java.lang.Integer.valueOf(r18);
                            r15 = r17.get(r18);
                            r15 = (org.telegram.tgnet.TLRPC.User) r15;
                            if (r15 == 0) goto L_0x011d;
                        L_0x010e:
                            r0 = r15.bot;
                            r17 = r0;
                            if (r17 == 0) goto L_0x011d;
                        L_0x0114:
                            r17 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
                            r17.<init>();
                            r0 = r17;
                            r10.reply_markup = r0;
                        L_0x011d:
                            r0 = r10.dialog_id;
                            r18 = r0;
                            r20 = 0;
                            r17 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
                            if (r17 != 0) goto L_0x014b;
                        L_0x0127:
                            r0 = r10.to_id;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.chat_id;
                            r17 = r0;
                            if (r17 == 0) goto L_0x01ee;
                        L_0x0133:
                            r0 = r10.to_id;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.chat_id;
                            r17 = r0;
                            r0 = r17;
                            r0 = -r0;
                            r17 = r0;
                            r0 = r17;
                            r0 = (long) r0;
                            r18 = r0;
                            r0 = r18;
                            r10.dialog_id = r0;
                        L_0x014b:
                            r0 = r10.action;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
                            r17 = r0;
                            if (r17 != 0) goto L_0x0163;
                        L_0x0157:
                            r0 = r10.action;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
                            r17 = r0;
                            if (r17 == 0) goto L_0x0225;
                        L_0x0163:
                            r17 = 0;
                            r0 = r17;
                            r10.unread = r0;
                            r17 = 0;
                            r0 = r17;
                            r10.media_unread = r0;
                        L_0x016f:
                            r12 = new org.telegram.messenger.MessageObject;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$usersDict;
                            r17 = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r18 = r0;
                            r0 = r18;
                            r0 = r0.val$chatsDict;
                            r18 = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r19 = r0;
                            r19 = r19.createdDialogIds;
                            r0 = r10.dialog_id;
                            r20 = r0;
                            r20 = java.lang.Long.valueOf(r20);
                            r19 = r19.contains(r20);
                            r0 = r17;
                            r1 = r18;
                            r2 = r19;
                            r12.<init>(r10, r0, r1, r2);
                            r17 = r12.isOut();
                            if (r17 != 0) goto L_0x01c3;
                        L_0x01ba:
                            r17 = r12.isUnread();
                            if (r17 == 0) goto L_0x01c3;
                        L_0x01c0:
                            r13.add(r12);
                        L_0x01c3:
                            r0 = r10.dialog_id;
                            r18 = r0;
                            r17 = java.lang.Long.valueOf(r18);
                            r0 = r17;
                            r5 = r11.get(r0);
                            r5 = (java.util.ArrayList) r5;
                            if (r5 != 0) goto L_0x01e7;
                        L_0x01d5:
                            r5 = new java.util.ArrayList;
                            r5.<init>();
                            r0 = r10.dialog_id;
                            r18 = r0;
                            r17 = java.lang.Long.valueOf(r18);
                            r0 = r17;
                            r11.put(r0, r5);
                        L_0x01e7:
                            r5.add(r12);
                            r4 = r4 + 1;
                            goto L_0x00ac;
                        L_0x01ee:
                            r0 = r10.to_id;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.user_id;
                            r17 = r0;
                            r18 = org.telegram.messenger.UserConfig.getClientUserId();
                            r0 = r17;
                            r1 = r18;
                            if (r0 != r1) goto L_0x0210;
                        L_0x0202:
                            r0 = r10.to_id;
                            r17 = r0;
                            r0 = r10.from_id;
                            r18 = r0;
                            r0 = r18;
                            r1 = r17;
                            r1.user_id = r0;
                        L_0x0210:
                            r0 = r10.to_id;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.user_id;
                            r17 = r0;
                            r0 = r17;
                            r0 = (long) r0;
                            r18 = r0;
                            r0 = r18;
                            r10.dialog_id = r0;
                            goto L_0x014b;
                        L_0x0225:
                            r0 = r10.out;
                            r17 = r0;
                            if (r17 == 0) goto L_0x0292;
                        L_0x022b:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r0 = r17;
                            r14 = r0.dialogs_read_outbox_max;
                        L_0x0241:
                            r0 = r10.dialog_id;
                            r18 = r0;
                            r17 = java.lang.Long.valueOf(r18);
                            r0 = r17;
                            r16 = r14.get(r0);
                            r16 = (java.lang.Integer) r16;
                            if (r16 != 0) goto L_0x027c;
                        L_0x0253:
                            r17 = org.telegram.messenger.MessagesStorage.getInstance();
                            r0 = r10.out;
                            r18 = r0;
                            r0 = r10.dialog_id;
                            r20 = r0;
                            r0 = r17;
                            r1 = r18;
                            r2 = r20;
                            r17 = r0.getDialogReadMax(r1, r2);
                            r16 = java.lang.Integer.valueOf(r17);
                            r0 = r10.dialog_id;
                            r18 = r0;
                            r17 = java.lang.Long.valueOf(r18);
                            r0 = r17;
                            r1 = r16;
                            r14.put(r0, r1);
                        L_0x027c:
                            r17 = r16.intValue();
                            r0 = r10.id;
                            r18 = r0;
                            r0 = r17;
                            r1 = r18;
                            if (r0 >= r1) goto L_0x02a9;
                        L_0x028a:
                            r17 = 1;
                        L_0x028c:
                            r0 = r17;
                            r10.unread = r0;
                            goto L_0x016f;
                        L_0x0292:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r0 = r17;
                            r14 = r0.dialogs_read_inbox_max;
                            goto L_0x0241;
                        L_0x02a9:
                            r17 = 0;
                            goto L_0x028c;
                        L_0x02ac:
                            r17 = new org.telegram.messenger.MessagesController$92$2$2$1;
                            r0 = r17;
                            r1 = r22;
                            r0.<init>(r11);
                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17);
                            r17 = org.telegram.messenger.MessagesStorage.getInstance();
                            r17 = r17.getStorageQueue();
                            r18 = new org.telegram.messenger.MessagesController$92$2$2$2;
                            r0 = r18;
                            r1 = r22;
                            r0.<init>(r13);
                            r17.postRunnable(r18);
                            r17 = org.telegram.messenger.SecretChatHelper.getInstance();
                            r17.processPendingEncMessages();
                        L_0x02d3:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.other_updates;
                            r17 = r0;
                            r17 = r17.isEmpty();
                            if (r17 != 0) goto L_0x0338;
                        L_0x02eb:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r18 = r0;
                            r0 = r18;
                            r0 = r0.val$res;
                            r18 = r0;
                            r0 = r18;
                            r0 = r0.other_updates;
                            r18 = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.val$res;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.users;
                            r19 = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r20 = r0;
                            r0 = r20;
                            r0 = r0.val$res;
                            r20 = r0;
                            r0 = r20;
                            r0 = r0.chats;
                            r20 = r0;
                            r21 = 1;
                            r17.processUpdateArray(r18, r19, r20, r21);
                        L_0x0338:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates_difference;
                            r17 = r0;
                            if (r17 == 0) goto L_0x03fc;
                        L_0x034c:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r18 = 0;
                            r0 = r18;
                            r1 = r17;
                            r1.gettingDifference = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.seq;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastSeqValue = r17;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.date;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastDateValue = r17;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.pts;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastPtsValue = r17;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.qts;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastQtsValue = r17;
                            r17 = org.telegram.tgnet.ConnectionsManager.getInstance();
                            r18 = 0;
                            r17.setIsUpdating(r18);
                            r4 = 0;
                        L_0x03d8:
                            r17 = 3;
                            r0 = r17;
                            if (r4 >= r0) goto L_0x045e;
                        L_0x03de:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r18 = 1;
                            r0 = r17;
                            r1 = r18;
                            r0.processUpdatesQueue(r4, r1);
                            r4 = r4 + 1;
                            goto L_0x03d8;
                        L_0x03fc:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates_differenceSlice;
                            r17 = r0;
                            if (r17 == 0) goto L_0x0520;
                        L_0x0410:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.intermediate_state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.date;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastDateValue = r17;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.intermediate_state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.pts;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastPtsValue = r17;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.intermediate_state;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.qts;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastQtsValue = r17;
                        L_0x045e:
                            r17 = org.telegram.messenger.MessagesStorage.getInstance();
                            r18 = org.telegram.messenger.MessagesStorage.lastSeqValue;
                            r19 = org.telegram.messenger.MessagesStorage.lastPtsValue;
                            r20 = org.telegram.messenger.MessagesStorage.lastDateValue;
                            r21 = org.telegram.messenger.MessagesStorage.lastQtsValue;
                            r17.saveDiffParams(r18, r19, r20, r21);
                            r17 = "tmessages";
                            r18 = new java.lang.StringBuilder;
                            r18.<init>();
                            r19 = "received difference with date = ";
                            r18 = r18.append(r19);
                            r19 = org.telegram.messenger.MessagesStorage.lastDateValue;
                            r18 = r18.append(r19);
                            r19 = " pts = ";
                            r18 = r18.append(r19);
                            r19 = org.telegram.messenger.MessagesStorage.lastPtsValue;
                            r18 = r18.append(r19);
                            r19 = " seq = ";
                            r18 = r18.append(r19);
                            r19 = org.telegram.messenger.MessagesStorage.lastSeqValue;
                            r18 = r18.append(r19);
                            r19 = " messages = ";
                            r18 = r18.append(r19);
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.val$res;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.new_messages;
                            r19 = r0;
                            r19 = r19.size();
                            r18 = r18.append(r19);
                            r19 = " users = ";
                            r18 = r18.append(r19);
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.val$res;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.users;
                            r19 = r0;
                            r19 = r19.size();
                            r18 = r18.append(r19);
                            r19 = " chats = ";
                            r18 = r18.append(r19);
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.val$res;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.chats;
                            r19 = r0;
                            r19 = r19.size();
                            r18 = r18.append(r19);
                            r19 = " other updates = ";
                            r18 = r18.append(r19);
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.val$res;
                            r19 = r0;
                            r0 = r19;
                            r0 = r0.other_updates;
                            r19 = r0;
                            r19 = r19.size();
                            r18 = r18.append(r19);
                            r18 = r18.toString();
                            org.telegram.messenger.FileLog.m11e(r17, r18);
                            return;
                        L_0x0520:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates_differenceEmpty;
                            r17 = r0;
                            if (r17 == 0) goto L_0x045e;
                        L_0x0534:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r18 = 0;
                            r0 = r18;
                            r1 = r17;
                            r1.gettingDifference = r0;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.seq;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastSeqValue = r17;
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.val$res;
                            r17 = r0;
                            r0 = r17;
                            r0 = r0.date;
                            r17 = r0;
                            org.telegram.messenger.MessagesStorage.lastDateValue = r17;
                            r17 = org.telegram.tgnet.ConnectionsManager.getInstance();
                            r18 = 0;
                            r17.setIsUpdating(r18);
                            r4 = 0;
                        L_0x0580:
                            r17 = 3;
                            r0 = r17;
                            if (r4 >= r0) goto L_0x045e;
                        L_0x0586:
                            r0 = r22;
                            r0 = org.telegram.messenger.MessagesController.92.C06302.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.AnonymousClass92.this;
                            r17 = r0;
                            r0 = r17;
                            r0 = org.telegram.messenger.MessagesController.this;
                            r17 = r0;
                            r18 = 1;
                            r0 = r17;
                            r1 = r18;
                            r0.processUpdatesQueue(r4, r1);
                            r4 = r4 + 1;
                            goto L_0x0580;
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.92.2.2.run():void");
                        }
                    }

                    C06302(updates_Difference org_telegram_tgnet_TLRPC_updates_Difference, ArrayList arrayList, HashMap hashMap, HashMap hashMap2) {
                        this.val$res = org_telegram_tgnet_TLRPC_updates_Difference;
                        this.val$msgUpdates = arrayList;
                        this.val$usersDict = hashMap;
                        this.val$chatsDict = hashMap2;
                    }

                    public void run() {
                        MessagesStorage.getInstance().putUsersAndChats(this.val$res.users, this.val$res.chats, true, false);
                        if (!this.val$msgUpdates.isEmpty()) {
                            HashMap<Integer, long[]> corrected = new HashMap();
                            Iterator i$ = this.val$msgUpdates.iterator();
                            while (i$.hasNext()) {
                                TL_updateMessageID update = (TL_updateMessageID) i$.next();
                                long[] ids = MessagesStorage.getInstance().updateMessageStateAndId(update.random_id, null, update.id, 0, false, 0);
                                if (ids != null) {
                                    corrected.put(Integer.valueOf(update.id), ids);
                                }
                            }
                            if (!corrected.isEmpty()) {
                                AndroidUtilities.runOnUIThread(new C06251(corrected));
                            }
                        }
                        Utilities.stageQueue.postRunnable(new C06292());
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        int a;
                        updates_Difference res = (updates_Difference) response;
                        if (res instanceof TL_updates_differenceSlice) {
                            MessagesController.this.getDifference(res.intermediate_state.pts, res.intermediate_state.date, res.intermediate_state.qts, true);
                        }
                        HashMap<Integer, User> usersDict = new HashMap();
                        HashMap<Integer, Chat> chatsDict = new HashMap();
                        for (a = 0; a < res.users.size(); a += MessagesController.UPDATE_MASK_NAME) {
                            User user = (User) res.users.get(a);
                            usersDict.put(Integer.valueOf(user.id), user);
                        }
                        for (a = 0; a < res.chats.size(); a += MessagesController.UPDATE_MASK_NAME) {
                            Chat chat = (Chat) res.chats.get(a);
                            chatsDict.put(Integer.valueOf(chat.id), chat);
                        }
                        ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
                        if (!res.other_updates.isEmpty()) {
                            a = 0;
                            while (a < res.other_updates.size()) {
                                Update upd = (Update) res.other_updates.get(a);
                                if (upd instanceof TL_updateMessageID) {
                                    msgUpdates.add((TL_updateMessageID) upd);
                                    res.other_updates.remove(a);
                                    a--;
                                }
                                a += MessagesController.UPDATE_MASK_NAME;
                            }
                        }
                        AndroidUtilities.runOnUIThread(new C06241(res));
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06302(res, msgUpdates, usersDict, chatsDict));
                        return;
                    }
                    MessagesController.this.gettingDifference = false;
                    ConnectionsManager.getInstance().setIsUpdating(false);
                }
            });
        }
    }

    public void generateJoinMessage(int chat_id, boolean ignoreLeft) {
        Chat chat = getChat(Integer.valueOf(chat_id));
        if (chat != null && ChatObject.isChannel(chat_id)) {
            if ((!chat.left && !chat.kicked) || ignoreLeft) {
                TL_messageService message = new TL_messageService();
                message.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                int newMessageId = UserConfig.getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.date = ConnectionsManager.getInstance().getCurrentTime();
                message.from_id = UserConfig.getClientUserId();
                message.to_id = new TL_peerChannel();
                message.to_id.channel_id = chat_id;
                message.dialog_id = (long) (-chat_id);
                message.post = true;
                message.action = new TL_messageActionChatAddUser();
                message.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
                if (chat.megagroup) {
                    message.flags |= LinearLayoutManager.INVALID_OFFSET;
                }
                UserConfig.saveConfig(false);
                ArrayList<MessageObject> pushMessages = new ArrayList();
                ArrayList messagesArr = new ArrayList();
                messagesArr.add(message);
                pushMessages.add(new MessageObject(message, null, true));
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass93(pushMessages));
                MessagesStorage.getInstance().putMessages(messagesArr, true, true, false, MediaController.getInstance().getAutodownloadMask());
                AndroidUtilities.runOnUIThread(new AnonymousClass94(chat_id, pushMessages));
            }
        }
    }

    public void convertGroup() {
    }

    public void checkChannelInviter(int chat_id) {
        AndroidUtilities.runOnUIThread(new AnonymousClass95(chat_id));
    }

    private int getUpdateType(Update update) {
        if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateReadMessagesContents) || (update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox) || (update instanceof TL_updateDeleteMessages) || (update instanceof TL_updateWebPage) || (update instanceof TL_updateEditMessage)) {
            return 0;
        }
        if (update instanceof TL_updateNewEncryptedMessage) {
            return UPDATE_MASK_NAME;
        }
        if ((update instanceof TL_updateNewChannelMessage) || (update instanceof TL_updateDeleteChannelMessages) || (update instanceof TL_updateEditChannelMessage)) {
            return UPDATE_MASK_AVATAR;
        }
        return 3;
    }

    private int getUpdateChannelId(Update update) {
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).message.to_id.channel_id;
        }
        return update.channel_id;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processUpdates(org.telegram.tgnet.TLRPC.Updates r53, boolean r54) {
        /*
        r52 = this;
        r31 = 0;
        r32 = 0;
        r33 = 0;
        r44 = 0;
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShort;
        if (r4 == 0) goto L_0x0061;
    L_0x000e:
        r11 = new java.util.ArrayList;
        r11.<init>();
        r0 = r53;
        r4 = r0.update;
        r11.add(r4);
        r4 = 0;
        r6 = 0;
        r7 = 0;
        r0 = r52;
        r0.processUpdateArray(r11, r4, r6, r7);
    L_0x0022:
        r4 = org.telegram.messenger.SecretChatHelper.getInstance();
        r4.processPendingEncMessages();
        if (r54 != 0) goto L_0x0c4e;
    L_0x002b:
        r25 = new java.util.ArrayList;
        r0 = r52;
        r4 = r0.updatesQueueChannels;
        r4 = r4.keySet();
        r0 = r25;
        r0.<init>(r4);
        r10 = 0;
    L_0x003b:
        r4 = r25.size();
        if (r10 >= r4) goto L_0x0c49;
    L_0x0041:
        r0 = r25;
        r24 = r0.get(r10);
        r24 = (java.lang.Integer) r24;
        if (r31 == 0) goto L_0x0c3d;
    L_0x004b:
        r0 = r31;
        r1 = r24;
        r4 = r0.contains(r1);
        if (r4 == 0) goto L_0x0c3d;
    L_0x0055:
        r4 = r24.intValue();
        r0 = r52;
        r0.getChannelDifference(r4);
    L_0x005e:
        r10 = r10 + 1;
        goto L_0x003b;
    L_0x0061:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
        if (r4 != 0) goto L_0x006d;
    L_0x0067:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x04a9;
    L_0x006d:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
        if (r4 == 0) goto L_0x01e7;
    L_0x0073:
        r0 = r53;
        r0 = r0.from_id;
        r50 = r0;
    L_0x0079:
        r4 = java.lang.Integer.valueOf(r50);
        r0 = r52;
        r47 = r0.getUser(r4);
        r48 = 0;
        r49 = 0;
        r16 = 0;
        if (r47 == 0) goto L_0x0091;
    L_0x008b:
        r0 = r47;
        r4 = r0.min;
        if (r4 == 0) goto L_0x00ad;
    L_0x0091:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r50;
        r47 = r4.getUserSync(r0);
        if (r47 == 0) goto L_0x00a5;
    L_0x009d:
        r0 = r47;
        r4 = r0.min;
        if (r4 == 0) goto L_0x00a5;
    L_0x00a3:
        r47 = 0;
    L_0x00a5:
        r4 = 1;
        r0 = r52;
        r1 = r47;
        r0.putUser(r1, r4);
    L_0x00ad:
        r30 = 0;
        r0 = r53;
        r4 = r0.fwd_from;
        if (r4 == 0) goto L_0x0119;
    L_0x00b5:
        r0 = r53;
        r4 = r0.fwd_from;
        r4 = r4.from_id;
        if (r4 == 0) goto L_0x00e7;
    L_0x00bd:
        r0 = r53;
        r4 = r0.fwd_from;
        r4 = r4.from_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r52;
        r48 = r0.getUser(r4);
        if (r48 != 0) goto L_0x00e5;
    L_0x00cf:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.fwd_from;
        r6 = r6.from_id;
        r48 = r4.getUserSync(r6);
        r4 = 1;
        r0 = r52;
        r1 = r48;
        r0.putUser(r1, r4);
    L_0x00e5:
        r30 = 1;
    L_0x00e7:
        r0 = r53;
        r4 = r0.fwd_from;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x0119;
    L_0x00ef:
        r0 = r53;
        r4 = r0.fwd_from;
        r4 = r4.channel_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r52;
        r16 = r0.getChat(r4);
        if (r16 != 0) goto L_0x0117;
    L_0x0101:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.fwd_from;
        r6 = r6.channel_id;
        r16 = r4.getChatSync(r6);
        r4 = 1;
        r0 = r52;
        r1 = r16;
        r0.putChat(r1, r4);
    L_0x0117:
        r30 = 1;
    L_0x0119:
        r29 = 0;
        r0 = r53;
        r4 = r0.via_bot_id;
        if (r4 == 0) goto L_0x0147;
    L_0x0121:
        r0 = r53;
        r4 = r0.via_bot_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r52;
        r49 = r0.getUser(r4);
        if (r49 != 0) goto L_0x0145;
    L_0x0131:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.via_bot_id;
        r49 = r4.getUserSync(r6);
        r4 = 1;
        r0 = r52;
        r1 = r49;
        r0.putUser(r1, r4);
    L_0x0145:
        r29 = 1;
    L_0x0147:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x01f3;
    L_0x014d:
        if (r47 == 0) goto L_0x0159;
    L_0x014f:
        if (r30 == 0) goto L_0x0155;
    L_0x0151:
        if (r48 != 0) goto L_0x0155;
    L_0x0153:
        if (r16 == 0) goto L_0x0159;
    L_0x0155:
        if (r29 == 0) goto L_0x01ef;
    L_0x0157:
        if (r49 != 0) goto L_0x01ef;
    L_0x0159:
        r28 = 1;
    L_0x015b:
        if (r28 != 0) goto L_0x01b4;
    L_0x015d:
        r0 = r53;
        r4 = r0.entities;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x01b4;
    L_0x0167:
        r10 = 0;
    L_0x0168:
        r0 = r53;
        r4 = r0.entities;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x01b4;
    L_0x0172:
        r0 = r53;
        r4 = r0.entities;
        r20 = r4.get(r10);
        r20 = (org.telegram.tgnet.TLRPC.MessageEntity) r20;
        r0 = r20;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r4 == 0) goto L_0x0234;
    L_0x0182:
        r20 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r20;
        r0 = r20;
        r0 = r0.user_id;
        r41 = r0;
        r4 = java.lang.Integer.valueOf(r41);
        r0 = r52;
        r21 = r0.getUser(r4);
        if (r21 == 0) goto L_0x019c;
    L_0x0196:
        r0 = r21;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0234;
    L_0x019c:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r41;
        r21 = r4.getUserSync(r0);
        if (r21 == 0) goto L_0x01b0;
    L_0x01a8:
        r0 = r21;
        r4 = r0.min;
        if (r4 == 0) goto L_0x01b0;
    L_0x01ae:
        r21 = 0;
    L_0x01b0:
        if (r21 != 0) goto L_0x022c;
    L_0x01b2:
        r28 = 1;
    L_0x01b4:
        if (r47 == 0) goto L_0x01e1;
    L_0x01b6:
        r0 = r47;
        r4 = r0.status;
        if (r4 == 0) goto L_0x01e1;
    L_0x01bc:
        r0 = r47;
        r4 = r0.status;
        r4 = r4.expires;
        if (r4 > 0) goto L_0x01e1;
    L_0x01c4:
        r0 = r52;
        r4 = r0.onlinePrivacy;
        r0 = r47;
        r6 = r0.id;
        r6 = java.lang.Integer.valueOf(r6);
        r7 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r7 = r7.getCurrentTime();
        r7 = java.lang.Integer.valueOf(r7);
        r4.put(r6, r7);
        r44 = 1;
    L_0x01e1:
        if (r28 == 0) goto L_0x0238;
    L_0x01e3:
        r32 = 1;
        goto L_0x0022;
    L_0x01e7:
        r0 = r53;
        r0 = r0.user_id;
        r50 = r0;
        goto L_0x0079;
    L_0x01ef:
        r28 = 0;
        goto L_0x015b;
    L_0x01f3:
        r0 = r53;
        r4 = r0.chat_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r52;
        r19 = r0.getChat(r4);
        if (r19 != 0) goto L_0x0217;
    L_0x0203:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.chat_id;
        r19 = r4.getChatSync(r6);
        r4 = 1;
        r0 = r52;
        r1 = r19;
        r0.putChat(r1, r4);
    L_0x0217:
        if (r19 == 0) goto L_0x0225;
    L_0x0219:
        if (r47 == 0) goto L_0x0225;
    L_0x021b:
        if (r30 == 0) goto L_0x0221;
    L_0x021d:
        if (r48 != 0) goto L_0x0221;
    L_0x021f:
        if (r16 == 0) goto L_0x0225;
    L_0x0221:
        if (r29 == 0) goto L_0x0229;
    L_0x0223:
        if (r49 != 0) goto L_0x0229;
    L_0x0225:
        r28 = 1;
    L_0x0227:
        goto L_0x015b;
    L_0x0229:
        r28 = 0;
        goto L_0x0227;
    L_0x022c:
        r4 = 1;
        r0 = r52;
        r1 = r47;
        r0.putUser(r1, r4);
    L_0x0234:
        r10 = r10 + 1;
        goto L_0x0168;
    L_0x0238:
        r4 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r0 = r53;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r53;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x0420;
    L_0x0245:
        r26 = new org.telegram.tgnet.TLRPC$TL_message;
        r26.<init>();
        r0 = r53;
        r4 = r0.id;
        r0 = r26;
        r0.id = r4;
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x03c8;
    L_0x0258:
        r0 = r53;
        r4 = r0.out;
        if (r4 == 0) goto L_0x03c0;
    L_0x025e:
        r4 = org.telegram.messenger.UserConfig.getClientUserId();
        r0 = r26;
        r0.from_id = r4;
    L_0x0266:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r4.<init>();
        r0 = r26;
        r0.to_id = r4;
        r0 = r26;
        r4 = r0.to_id;
        r0 = r50;
        r4.user_id = r0;
        r0 = r50;
        r6 = (long) r0;
        r0 = r26;
        r0.dialog_id = r6;
    L_0x027e:
        r0 = r53;
        r4 = r0.fwd_from;
        r0 = r26;
        r0.fwd_from = r4;
        r0 = r53;
        r4 = r0.silent;
        r0 = r26;
        r0.silent = r4;
        r0 = r53;
        r4 = r0.out;
        r0 = r26;
        r0.out = r4;
        r0 = r53;
        r4 = r0.mentioned;
        r0 = r26;
        r0.mentioned = r4;
        r0 = r53;
        r4 = r0.media_unread;
        r0 = r26;
        r0.media_unread = r4;
        r0 = r53;
        r4 = r0.entities;
        r0 = r26;
        r0.entities = r4;
        r0 = r53;
        r4 = r0.message;
        r0 = r26;
        r0.message = r4;
        r0 = r53;
        r4 = r0.date;
        r0 = r26;
        r0.date = r4;
        r0 = r53;
        r4 = r0.via_bot_id;
        r0 = r26;
        r0.via_bot_id = r4;
        r0 = r53;
        r4 = r0.flags;
        r4 = r4 | 256;
        r0 = r26;
        r0.flags = r4;
        r0 = r53;
        r4 = r0.reply_to_msg_id;
        r0 = r26;
        r0.reply_to_msg_id = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r4.<init>();
        r0 = r26;
        r0.media = r4;
        r0 = r26;
        r4 = r0.out;
        if (r4 == 0) goto L_0x03ed;
    L_0x02e7:
        r0 = r52;
        r0 = r0.dialogs_read_outbox_max;
        r38 = r0;
    L_0x02ed:
        r0 = r26;
        r6 = r0.dialog_id;
        r4 = java.lang.Long.valueOf(r6);
        r0 = r38;
        r51 = r0.get(r4);
        r51 = (java.lang.Integer) r51;
        if (r51 != 0) goto L_0x0322;
    L_0x02ff:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r26;
        r6 = r0.out;
        r0 = r26;
        r8 = r0.dialog_id;
        r4 = r4.getDialogReadMax(r6, r8);
        r51 = java.lang.Integer.valueOf(r4);
        r0 = r26;
        r6 = r0.dialog_id;
        r4 = java.lang.Long.valueOf(r6);
        r0 = r38;
        r1 = r51;
        r0.put(r4, r1);
    L_0x0322:
        r4 = r51.intValue();
        r0 = r26;
        r6 = r0.id;
        if (r4 >= r6) goto L_0x03f5;
    L_0x032c:
        r4 = 1;
    L_0x032d:
        r0 = r26;
        r0.unread = r4;
        r0 = r53;
        r4 = r0.pts;
        org.telegram.messenger.MessagesStorage.lastPtsValue = r4;
        r34 = new org.telegram.messenger.MessageObject;
        r4 = 0;
        r0 = r52;
        r6 = r0.createdDialogIds;
        r0 = r26;
        r8 = r0.dialog_id;
        r7 = java.lang.Long.valueOf(r8);
        r6 = r6.contains(r7);
        r0 = r34;
        r1 = r26;
        r0.<init>(r1, r4, r6);
        r35 = new java.util.ArrayList;
        r35.<init>();
        r0 = r35;
        r1 = r34;
        r0.add(r1);
        r5 = new java.util.ArrayList;
        r5.<init>();
        r0 = r26;
        r5.add(r0);
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x03fb;
    L_0x036d:
        r0 = r53;
        r4 = r0.out;
        if (r4 != 0) goto L_0x03f8;
    L_0x0373:
        r0 = r53;
        r4 = r0.user_id;
        r6 = (long) r4;
        r0 = r52;
        r1 = r35;
        r4 = r0.updatePrintingUsersWithNewMessages(r6, r1);
        if (r4 == 0) goto L_0x03f8;
    L_0x0382:
        r36 = 1;
    L_0x0384:
        if (r36 == 0) goto L_0x0389;
    L_0x0386:
        r52.updatePrintingStrings();
    L_0x0389:
        r4 = new org.telegram.messenger.MessagesController$96;
        r0 = r52;
        r1 = r36;
        r2 = r50;
        r3 = r35;
        r4.<init>(r1, r2, r3);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x0399:
        r4 = r34.isOut();
        if (r4 != 0) goto L_0x03b3;
    L_0x039f:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r4 = r4.getStorageQueue();
        r6 = new org.telegram.messenger.MessagesController$98;
        r0 = r52;
        r1 = r35;
        r6.<init>(r1);
        r4.postRunnable(r6);
    L_0x03b3:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r6 = 0;
        r7 = 1;
        r8 = 0;
        r9 = 0;
        r4.putMessages(r5, r6, r7, r8, r9);
        goto L_0x0022;
    L_0x03c0:
        r0 = r50;
        r1 = r26;
        r1.from_id = r0;
        goto L_0x0266;
    L_0x03c8:
        r0 = r50;
        r1 = r26;
        r1.from_id = r0;
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r4.<init>();
        r0 = r26;
        r0.to_id = r4;
        r0 = r26;
        r4 = r0.to_id;
        r0 = r53;
        r6 = r0.chat_id;
        r4.chat_id = r6;
        r0 = r53;
        r4 = r0.chat_id;
        r4 = -r4;
        r6 = (long) r4;
        r0 = r26;
        r0.dialog_id = r6;
        goto L_0x027e;
    L_0x03ed:
        r0 = r52;
        r0 = r0.dialogs_read_inbox_max;
        r38 = r0;
        goto L_0x02ed;
    L_0x03f5:
        r4 = 0;
        goto L_0x032d;
    L_0x03f8:
        r36 = 0;
        goto L_0x0384;
    L_0x03fb:
        r0 = r53;
        r4 = r0.chat_id;
        r4 = -r4;
        r6 = (long) r4;
        r0 = r52;
        r1 = r35;
        r36 = r0.updatePrintingUsersWithNewMessages(r6, r1);
        if (r36 == 0) goto L_0x040e;
    L_0x040b:
        r52.updatePrintingStrings();
    L_0x040e:
        r4 = new org.telegram.messenger.MessagesController$97;
        r0 = r52;
        r1 = r36;
        r2 = r53;
        r3 = r35;
        r4.<init>(r1, r2, r3);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x0399;
    L_0x0420:
        r4 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r0 = r53;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x0022;
    L_0x0428:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get diff short message, pts: ";
        r6 = r6.append(r7);
        r7 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r6 = r6.append(r7);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r53;
        r7 = r0.pts;
        r6 = r6.append(r7);
        r7 = " count = ";
        r6 = r6.append(r7);
        r0 = r53;
        r7 = r0.pts_count;
        r6 = r6.append(r7);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x0481;
    L_0x0464:
        r0 = r52;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0481;
    L_0x046e:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r8 = r0.updatesStartWaitTimePts;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x04a5;
    L_0x0481:
        r0 = r52;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0493;
    L_0x048b:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r0.updatesStartWaitTimePts = r6;
    L_0x0493:
        r4 = "tmessages";
        r6 = "add to queue";
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.updatesQueuePts;
        r0 = r53;
        r4.add(r0);
        goto L_0x0022;
    L_0x04a5:
        r32 = 1;
        goto L_0x0022;
    L_0x04a9:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r4 != 0) goto L_0x04b5;
    L_0x04af:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates;
        if (r4 == 0) goto L_0x0be2;
    L_0x04b5:
        r27 = 0;
        r10 = 0;
    L_0x04b8:
        r0 = r53;
        r4 = r0.chats;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x0525;
    L_0x04c2:
        r0 = r53;
        r4 = r0.chats;
        r19 = r4.get(r10);
        r19 = (org.telegram.tgnet.TLRPC.Chat) r19;
        r0 = r19;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channel;
        if (r4 == 0) goto L_0x0522;
    L_0x04d2:
        r0 = r19;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0522;
    L_0x04d8:
        r0 = r19;
        r4 = r0.id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r52;
        r22 = r0.getChat(r4);
        if (r22 == 0) goto L_0x04ee;
    L_0x04e8:
        r0 = r22;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0504;
    L_0x04ee:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.chat_id;
        r15 = r4.getChatSync(r6);
        if (r22 != 0) goto L_0x0502;
    L_0x04fc:
        r4 = 1;
        r0 = r52;
        r0.putChat(r15, r4);
    L_0x0502:
        r22 = r15;
    L_0x0504:
        if (r22 == 0) goto L_0x050c;
    L_0x0506:
        r0 = r22;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0522;
    L_0x050c:
        if (r27 != 0) goto L_0x0513;
    L_0x050e:
        r27 = new java.util.HashMap;
        r27.<init>();
    L_0x0513:
        r0 = r19;
        r4 = r0.id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r27;
        r1 = r19;
        r0.put(r4, r1);
    L_0x0522:
        r10 = r10 + 1;
        goto L_0x04b8;
    L_0x0525:
        if (r27 == 0) goto L_0x0576;
    L_0x0527:
        r10 = 0;
    L_0x0528:
        r0 = r53;
        r4 = r0.updates;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x0576;
    L_0x0532:
        r0 = r53;
        r4 = r0.updates;
        r42 = r4.get(r10);
        r42 = (org.telegram.tgnet.TLRPC.Update) r42;
        r0 = r42;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r4 == 0) goto L_0x062a;
    L_0x0542:
        r42 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r42;
        r0 = r42;
        r4 = r0.message;
        r4 = r4.to_id;
        r0 = r4.channel_id;
        r17 = r0;
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r27;
        r4 = r0.containsKey(r4);
        if (r4 == 0) goto L_0x062a;
    L_0x055a:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get diff because of min channel ";
        r6 = r6.append(r7);
        r0 = r17;
        r6 = r6.append(r0);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r32 = 1;
    L_0x0576:
        if (r32 != 0) goto L_0x0022;
    L_0x0578:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.users;
        r0 = r53;
        r7 = r0.chats;
        r8 = 1;
        r9 = 1;
        r4.putUsersAndChats(r6, r7, r8, r9);
        r0 = r53;
        r4 = r0.updates;
        r0 = r52;
        r6 = r0.updatesComparator;
        java.util.Collections.sort(r4, r6);
        r10 = 0;
    L_0x0595:
        r0 = r53;
        r4 = r0.updates;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x0ad7;
    L_0x059f:
        r0 = r53;
        r4 = r0.updates;
        r42 = r4.get(r10);
        r42 = (org.telegram.tgnet.TLRPC.Update) r42;
        r0 = r52;
        r1 = r42;
        r4 = r0.getUpdateType(r1);
        if (r4 != 0) goto L_0x0727;
    L_0x05b3:
        r45 = new org.telegram.tgnet.TLRPC$TL_updates;
        r45.<init>();
        r0 = r45;
        r4 = r0.updates;
        r0 = r42;
        r4.add(r0);
        r0 = r42;
        r4 = r0.pts;
        r0 = r45;
        r0.pts = r4;
        r0 = r42;
        r4 = r0.pts_count;
        r0 = r45;
        r0.pts_count = r4;
        r13 = r10 + 1;
    L_0x05d3:
        r0 = r53;
        r4 = r0.updates;
        r4 = r4.size();
        if (r13 >= r4) goto L_0x062e;
    L_0x05dd:
        r0 = r53;
        r4 = r0.updates;
        r43 = r4.get(r13);
        r43 = (org.telegram.tgnet.TLRPC.Update) r43;
        r0 = r52;
        r1 = r43;
        r4 = r0.getUpdateType(r1);
        if (r4 != 0) goto L_0x062e;
    L_0x05f1:
        r0 = r45;
        r4 = r0.pts;
        r0 = r43;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r43;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x062e;
    L_0x0600:
        r0 = r45;
        r4 = r0.updates;
        r0 = r43;
        r4.add(r0);
        r0 = r43;
        r4 = r0.pts;
        r0 = r45;
        r0.pts = r4;
        r0 = r45;
        r4 = r0.pts_count;
        r0 = r43;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r45;
        r0.pts_count = r4;
        r0 = r53;
        r4 = r0.updates;
        r4.remove(r13);
        r13 = r13 + -1;
        r13 = r13 + 1;
        goto L_0x05d3;
    L_0x062a:
        r10 = r10 + 1;
        goto L_0x0528;
    L_0x062e:
        r4 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r0 = r45;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r45;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x068e;
    L_0x063b:
        r0 = r45;
        r4 = r0.updates;
        r0 = r53;
        r6 = r0.users;
        r0 = r53;
        r7 = r0.chats;
        r8 = 0;
        r0 = r52;
        r4 = r0.processUpdateArray(r4, r6, r7, r8);
        if (r4 != 0) goto L_0x0687;
    L_0x0650:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get diff inner TL_updates, seq: ";
        r6 = r6.append(r7);
        r7 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r6 = r6.append(r7);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r53;
        r7 = r0.seq;
        r6 = r6.append(r7);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r32 = 1;
    L_0x067a:
        r0 = r53;
        r4 = r0.updates;
        r4.remove(r10);
        r10 = r10 + -1;
        r10 = r10 + 1;
        goto L_0x0595;
    L_0x0687:
        r0 = r45;
        r4 = r0.pts;
        org.telegram.messenger.MessagesStorage.lastPtsValue = r4;
        goto L_0x067a;
    L_0x068e:
        r4 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r0 = r45;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x067a;
    L_0x0696:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r0 = r42;
        r6 = r6.append(r0);
        r7 = " need get diff, pts: ";
        r6 = r6.append(r7);
        r7 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r6 = r6.append(r7);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r45;
        r7 = r0.pts;
        r6 = r6.append(r7);
        r7 = " count = ";
        r6 = r6.append(r7);
        r0 = r45;
        r7 = r0.pts_count;
        r6 = r6.append(r7);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x06ff;
    L_0x06d8:
        r0 = r52;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x06ff;
    L_0x06e2:
        r0 = r52;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0723;
    L_0x06ec:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r8 = r0.updatesStartWaitTimePts;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x0723;
    L_0x06ff:
        r0 = r52;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0711;
    L_0x0709:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r0.updatesStartWaitTimePts = r6;
    L_0x0711:
        r4 = "tmessages";
        r6 = "add to queue";
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.updatesQueuePts;
        r0 = r45;
        r4.add(r0);
        goto L_0x067a;
    L_0x0723:
        r32 = 1;
        goto L_0x067a;
    L_0x0727:
        r0 = r52;
        r1 = r42;
        r4 = r0.getUpdateType(r1);
        r6 = 1;
        if (r4 != r6) goto L_0x084e;
    L_0x0732:
        r45 = new org.telegram.tgnet.TLRPC$TL_updates;
        r45.<init>();
        r0 = r45;
        r4 = r0.updates;
        r0 = r42;
        r4.add(r0);
        r0 = r42;
        r4 = r0.qts;
        r0 = r45;
        r0.pts = r4;
        r13 = r10 + 1;
    L_0x074a:
        r0 = r53;
        r4 = r0.updates;
        r4 = r4.size();
        if (r13 >= r4) goto L_0x0792;
    L_0x0754:
        r0 = r53;
        r4 = r0.updates;
        r43 = r4.get(r13);
        r43 = (org.telegram.tgnet.TLRPC.Update) r43;
        r0 = r52;
        r1 = r43;
        r4 = r0.getUpdateType(r1);
        r6 = 1;
        if (r4 != r6) goto L_0x0792;
    L_0x0769:
        r0 = r45;
        r4 = r0.pts;
        r4 = r4 + 1;
        r0 = r43;
        r6 = r0.qts;
        if (r4 != r6) goto L_0x0792;
    L_0x0775:
        r0 = r45;
        r4 = r0.updates;
        r0 = r43;
        r4.add(r0);
        r0 = r43;
        r4 = r0.qts;
        r0 = r45;
        r0.pts = r4;
        r0 = r53;
        r4 = r0.updates;
        r4.remove(r13);
        r13 = r13 + -1;
        r13 = r13 + 1;
        goto L_0x074a;
    L_0x0792:
        r4 = org.telegram.messenger.MessagesStorage.lastQtsValue;
        if (r4 == 0) goto L_0x07a7;
    L_0x0796:
        r4 = org.telegram.messenger.MessagesStorage.lastQtsValue;
        r0 = r45;
        r6 = r0.updates;
        r6 = r6.size();
        r4 = r4 + r6;
        r0 = r45;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x07c3;
    L_0x07a7:
        r0 = r45;
        r4 = r0.updates;
        r0 = r53;
        r6 = r0.users;
        r0 = r53;
        r7 = r0.chats;
        r8 = 0;
        r0 = r52;
        r0.processUpdateArray(r4, r6, r7, r8);
        r0 = r45;
        r4 = r0.pts;
        org.telegram.messenger.MessagesStorage.lastQtsValue = r4;
        r33 = 1;
        goto L_0x067a;
    L_0x07c3:
        r4 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r0 = r45;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x067a;
    L_0x07cb:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r0 = r42;
        r6 = r6.append(r0);
        r7 = " need get diff, qts: ";
        r6 = r6.append(r7);
        r7 = org.telegram.messenger.MessagesStorage.lastQtsValue;
        r6 = r6.append(r7);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r45;
        r7 = r0.pts;
        r6 = r6.append(r7);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x0826;
    L_0x07ff:
        r0 = r52;
        r6 = r0.updatesStartWaitTimeQts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0826;
    L_0x0809:
        r0 = r52;
        r6 = r0.updatesStartWaitTimeQts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x084a;
    L_0x0813:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r8 = r0.updatesStartWaitTimeQts;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x084a;
    L_0x0826:
        r0 = r52;
        r6 = r0.updatesStartWaitTimeQts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0838;
    L_0x0830:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r0.updatesStartWaitTimeQts = r6;
    L_0x0838:
        r4 = "tmessages";
        r6 = "add to queue";
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.updatesQueueQts;
        r0 = r45;
        r4.add(r0);
        goto L_0x067a;
    L_0x084a:
        r32 = 1;
        goto L_0x067a;
    L_0x084e:
        r0 = r52;
        r1 = r42;
        r4 = r0.getUpdateType(r1);
        r6 = 2;
        if (r4 != r6) goto L_0x0ad7;
    L_0x0859:
        r0 = r52;
        r1 = r42;
        r17 = r0.getUpdateChannelId(r1);
        r40 = 0;
        r0 = r52;
        r4 = r0.channelsPts;
        r6 = java.lang.Integer.valueOf(r17);
        r18 = r4.get(r6);
        r18 = (java.lang.Integer) r18;
        if (r18 != 0) goto L_0x08af;
    L_0x0873:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r17;
        r4 = r4.getChannelPtsSync(r0);
        r18 = java.lang.Integer.valueOf(r4);
        r4 = r18.intValue();
        if (r4 != 0) goto L_0x0937;
    L_0x0887:
        r14 = 0;
    L_0x0888:
        r0 = r53;
        r4 = r0.chats;
        r4 = r4.size();
        if (r14 >= r4) goto L_0x08af;
    L_0x0892:
        r0 = r53;
        r4 = r0.chats;
        r19 = r4.get(r14);
        r19 = (org.telegram.tgnet.TLRPC.Chat) r19;
        r0 = r19;
        r4 = r0.id;
        r0 = r17;
        if (r4 != r0) goto L_0x0933;
    L_0x08a4:
        r6 = 0;
        r0 = r52;
        r1 = r19;
        r0.loadUnknownChannel(r1, r6);
        r40 = 1;
    L_0x08af:
        r45 = new org.telegram.tgnet.TLRPC$TL_updates;
        r45.<init>();
        r0 = r45;
        r4 = r0.updates;
        r0 = r42;
        r4.add(r0);
        r0 = r42;
        r4 = r0.pts;
        r0 = r45;
        r0.pts = r4;
        r0 = r42;
        r4 = r0.pts_count;
        r0 = r45;
        r0.pts_count = r4;
        r13 = r10 + 1;
    L_0x08cf:
        r0 = r53;
        r4 = r0.updates;
        r4 = r4.size();
        if (r13 >= r4) goto L_0x0946;
    L_0x08d9:
        r0 = r53;
        r4 = r0.updates;
        r43 = r4.get(r13);
        r43 = (org.telegram.tgnet.TLRPC.Update) r43;
        r0 = r52;
        r1 = r43;
        r4 = r0.getUpdateType(r1);
        r6 = 2;
        if (r4 != r6) goto L_0x0946;
    L_0x08ee:
        r0 = r52;
        r1 = r43;
        r4 = r0.getUpdateChannelId(r1);
        r0 = r17;
        if (r0 != r4) goto L_0x0946;
    L_0x08fa:
        r0 = r45;
        r4 = r0.pts;
        r0 = r43;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r43;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x0946;
    L_0x0909:
        r0 = r45;
        r4 = r0.updates;
        r0 = r43;
        r4.add(r0);
        r0 = r43;
        r4 = r0.pts;
        r0 = r45;
        r0.pts = r4;
        r0 = r45;
        r4 = r0.pts_count;
        r0 = r43;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r45;
        r0.pts_count = r4;
        r0 = r53;
        r4 = r0.updates;
        r4.remove(r13);
        r13 = r13 + -1;
        r13 = r13 + 1;
        goto L_0x08cf;
    L_0x0933:
        r14 = r14 + 1;
        goto L_0x0888;
    L_0x0937:
        r0 = r52;
        r4 = r0.channelsPts;
        r6 = java.lang.Integer.valueOf(r17);
        r0 = r18;
        r4.put(r6, r0);
        goto L_0x08af;
    L_0x0946:
        if (r40 != 0) goto L_0x0abb;
    L_0x0948:
        r4 = r18.intValue();
        r0 = r45;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r45;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x09c8;
    L_0x0957:
        r0 = r45;
        r4 = r0.updates;
        r0 = r53;
        r6 = r0.users;
        r0 = r53;
        r7 = r0.chats;
        r8 = 0;
        r0 = r52;
        r4 = r0.processUpdateArray(r4, r6, r7, r8);
        if (r4 != 0) goto L_0x09a6;
    L_0x096c:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get channel diff inner TL_updates, channel_id = ";
        r6 = r6.append(r7);
        r0 = r17;
        r6 = r6.append(r0);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        if (r31 != 0) goto L_0x098f;
    L_0x0988:
        r31 = new java.util.ArrayList;
        r31.<init>();
        goto L_0x067a;
    L_0x098f:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r31;
        r4 = r0.contains(r4);
        if (r4 != 0) goto L_0x067a;
    L_0x099b:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r31;
        r0.add(r4);
        goto L_0x067a;
    L_0x09a6:
        r0 = r52;
        r4 = r0.channelsPts;
        r6 = java.lang.Integer.valueOf(r17);
        r0 = r45;
        r7 = r0.pts;
        r7 = java.lang.Integer.valueOf(r7);
        r4.put(r6, r7);
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r45;
        r6 = r0.pts;
        r0 = r17;
        r4.saveChannelPts(r0, r6);
        goto L_0x067a;
    L_0x09c8:
        r4 = r18.intValue();
        r0 = r45;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x067a;
    L_0x09d2:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r0 = r42;
        r6 = r6.append(r0);
        r7 = " need get channel diff, pts: ";
        r6 = r6.append(r7);
        r0 = r18;
        r6 = r6.append(r0);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r45;
        r7 = r0.pts;
        r6 = r6.append(r7);
        r7 = " count = ";
        r6 = r6.append(r7);
        r0 = r45;
        r7 = r0.pts_count;
        r6 = r6.append(r7);
        r7 = " channelId = ";
        r6 = r6.append(r7);
        r0 = r17;
        r6 = r6.append(r0);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.updatesStartWaitTimeChannels;
        r6 = java.lang.Integer.valueOf(r17);
        r46 = r4.get(r6);
        r46 = (java.lang.Long) r46;
        r0 = r52;
        r4 = r0.gettingDifferenceChannels;
        r6 = java.lang.Integer.valueOf(r17);
        r23 = r4.get(r6);
        r23 = (java.lang.Boolean) r23;
        if (r23 != 0) goto L_0x0a3d;
    L_0x0a38:
        r4 = 0;
        r23 = java.lang.Boolean.valueOf(r4);
    L_0x0a3d:
        r4 = r23.booleanValue();
        if (r4 != 0) goto L_0x0a58;
    L_0x0a43:
        if (r46 == 0) goto L_0x0a58;
    L_0x0a45:
        r6 = java.lang.System.currentTimeMillis();
        r8 = r46.longValue();
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x0a9b;
    L_0x0a58:
        if (r46 != 0) goto L_0x0a6d;
    L_0x0a5a:
        r0 = r52;
        r4 = r0.updatesStartWaitTimeChannels;
        r6 = java.lang.Integer.valueOf(r17);
        r8 = java.lang.System.currentTimeMillis();
        r7 = java.lang.Long.valueOf(r8);
        r4.put(r6, r7);
    L_0x0a6d:
        r4 = "tmessages";
        r6 = "add to queue";
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.updatesQueueChannels;
        r6 = java.lang.Integer.valueOf(r17);
        r12 = r4.get(r6);
        r12 = (java.util.ArrayList) r12;
        if (r12 != 0) goto L_0x0a94;
    L_0x0a84:
        r12 = new java.util.ArrayList;
        r12.<init>();
        r0 = r52;
        r4 = r0.updatesQueueChannels;
        r6 = java.lang.Integer.valueOf(r17);
        r4.put(r6, r12);
    L_0x0a94:
        r0 = r45;
        r12.add(r0);
        goto L_0x067a;
    L_0x0a9b:
        if (r31 != 0) goto L_0x0aa4;
    L_0x0a9d:
        r31 = new java.util.ArrayList;
        r31.<init>();
        goto L_0x067a;
    L_0x0aa4:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r31;
        r4 = r0.contains(r4);
        if (r4 != 0) goto L_0x067a;
    L_0x0ab0:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r31;
        r0.add(r4);
        goto L_0x067a;
    L_0x0abb:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need load unknown channel = ";
        r6 = r6.append(r7);
        r0 = r17;
        r6 = r6.append(r0);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        goto L_0x067a;
    L_0x0ad7:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r4 == 0) goto L_0x0b22;
    L_0x0add:
        r4 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r4 = r4 + 1;
        r0 = r53;
        r6 = r0.seq_start;
        if (r4 == r6) goto L_0x0aef;
    L_0x0ae7:
        r4 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r0 = r53;
        r6 = r0.seq_start;
        if (r4 != r6) goto L_0x0b1f;
    L_0x0aef:
        r37 = 1;
    L_0x0af1:
        if (r37 == 0) goto L_0x0b40;
    L_0x0af3:
        r0 = r53;
        r4 = r0.updates;
        r0 = r53;
        r6 = r0.users;
        r0 = r53;
        r7 = r0.chats;
        r8 = 0;
        r0 = r52;
        r0.processUpdateArray(r4, r6, r7, r8);
        r0 = r53;
        r4 = r0.date;
        if (r4 == 0) goto L_0x0b11;
    L_0x0b0b:
        r0 = r53;
        r4 = r0.date;
        org.telegram.messenger.MessagesStorage.lastDateValue = r4;
    L_0x0b11:
        r0 = r53;
        r4 = r0.seq;
        if (r4 == 0) goto L_0x0022;
    L_0x0b17:
        r0 = r53;
        r4 = r0.seq;
        org.telegram.messenger.MessagesStorage.lastSeqValue = r4;
        goto L_0x0022;
    L_0x0b1f:
        r37 = 0;
        goto L_0x0af1;
    L_0x0b22:
        r4 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r4 = r4 + 1;
        r0 = r53;
        r6 = r0.seq;
        if (r4 == r6) goto L_0x0b3a;
    L_0x0b2c:
        r0 = r53;
        r4 = r0.seq;
        if (r4 == 0) goto L_0x0b3a;
    L_0x0b32:
        r0 = r53;
        r4 = r0.seq;
        r6 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        if (r4 != r6) goto L_0x0b3d;
    L_0x0b3a:
        r37 = 1;
    L_0x0b3c:
        goto L_0x0af1;
    L_0x0b3d:
        r37 = 0;
        goto L_0x0b3c;
    L_0x0b40:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r4 == 0) goto L_0x0bb5;
    L_0x0b46:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get diff TL_updatesCombined, seq: ";
        r6 = r6.append(r7);
        r7 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r6 = r6.append(r7);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r53;
        r7 = r0.seq_start;
        r6 = r6.append(r7);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
    L_0x0b6e:
        r0 = r52;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x0b91;
    L_0x0b74:
        r0 = r52;
        r6 = r0.updatesStartWaitTimeSeq;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0b91;
    L_0x0b7e:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r8 = r0.updatesStartWaitTimeSeq;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x0bde;
    L_0x0b91:
        r0 = r52;
        r6 = r0.updatesStartWaitTimeSeq;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0ba3;
    L_0x0b9b:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r52;
        r0.updatesStartWaitTimeSeq = r6;
    L_0x0ba3:
        r4 = "tmessages";
        r6 = "add TL_updates/Combined to queue";
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r0 = r52;
        r4 = r0.updatesQueueSeq;
        r0 = r53;
        r4.add(r0);
        goto L_0x0022;
    L_0x0bb5:
        r4 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "need get diff TL_updates, seq: ";
        r6 = r6.append(r7);
        r7 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r6 = r6.append(r7);
        r7 = " ";
        r6 = r6.append(r7);
        r0 = r53;
        r7 = r0.seq;
        r6 = r6.append(r7);
        r6 = r6.toString();
        org.telegram.messenger.FileLog.m11e(r4, r6);
        goto L_0x0b6e;
    L_0x0bde:
        r32 = 1;
        goto L_0x0022;
    L_0x0be2:
        r0 = r53;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesTooLong;
        if (r4 == 0) goto L_0x0bf3;
    L_0x0be8:
        r4 = "tmessages";
        r6 = "need get diff TL_updatesTooLong";
        org.telegram.messenger.FileLog.m11e(r4, r6);
        r32 = 1;
        goto L_0x0022;
    L_0x0bf3:
        r0 = r53;
        r4 = r0 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesSeq;
        if (r4 == 0) goto L_0x0c01;
    L_0x0bf9:
        r0 = r53;
        r4 = r0.seq;
        org.telegram.messenger.MessagesStorage.lastSeqValue = r4;
        goto L_0x0022;
    L_0x0c01:
        r0 = r53;
        r4 = r0 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesPts;
        if (r4 == 0) goto L_0x0022;
    L_0x0c07:
        r0 = r53;
        r4 = r0.chat_id;
        if (r4 == 0) goto L_0x0c35;
    L_0x0c0d:
        r0 = r52;
        r4 = r0.channelsPts;
        r0 = r53;
        r6 = r0.chat_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r53;
        r7 = r0.pts;
        r7 = java.lang.Integer.valueOf(r7);
        r4.put(r6, r7);
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r0 = r53;
        r6 = r0.chat_id;
        r0 = r53;
        r7 = r0.pts;
        r4.saveChannelPts(r6, r7);
        goto L_0x0022;
    L_0x0c35:
        r0 = r53;
        r4 = r0.pts;
        org.telegram.messenger.MessagesStorage.lastPtsValue = r4;
        goto L_0x0022;
    L_0x0c3d:
        r4 = r24.intValue();
        r6 = 0;
        r0 = r52;
        r0.processChannelsUpdatesQueue(r4, r6);
        goto L_0x005e;
    L_0x0c49:
        if (r32 == 0) goto L_0x0c87;
    L_0x0c4b:
        r52.getDifference();
    L_0x0c4e:
        if (r33 == 0) goto L_0x0c6b;
    L_0x0c50:
        r39 = new org.telegram.tgnet.TLRPC$TL_messages_receivedQueue;
        r39.<init>();
        r4 = org.telegram.messenger.MessagesStorage.lastQtsValue;
        r0 = r39;
        r0.max_qts = r4;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r6 = new org.telegram.messenger.MessagesController$99;
        r0 = r52;
        r6.<init>();
        r0 = r39;
        r4.sendRequest(r0, r6);
    L_0x0c6b:
        if (r44 == 0) goto L_0x0c77;
    L_0x0c6d:
        r4 = new org.telegram.messenger.MessagesController$100;
        r0 = r52;
        r4.<init>();
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x0c77:
        r4 = org.telegram.messenger.MessagesStorage.getInstance();
        r6 = org.telegram.messenger.MessagesStorage.lastSeqValue;
        r7 = org.telegram.messenger.MessagesStorage.lastPtsValue;
        r8 = org.telegram.messenger.MessagesStorage.lastDateValue;
        r9 = org.telegram.messenger.MessagesStorage.lastQtsValue;
        r4.saveDiffParams(r6, r7, r8, r9);
        return;
    L_0x0c87:
        r10 = 0;
    L_0x0c88:
        r4 = 3;
        if (r10 >= r4) goto L_0x0c4e;
    L_0x0c8b:
        r4 = 0;
        r0 = r52;
        r0.processUpdatesQueue(r10, r4);
        r10 = r10 + 1;
        goto L_0x0c88;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    public boolean processUpdateArray(ArrayList<Update> updates, ArrayList<User> usersArr, ArrayList<Chat> chatsArr, boolean fromGetDifference) {
        if (updates.isEmpty()) {
            if (!(usersArr == null && chatsArr == null)) {
                AndroidUtilities.runOnUIThread(new AnonymousClass101(usersArr, chatsArr));
            }
            return true;
        }
        AbstractMap usersDict;
        int a;
        AbstractMap chatsDict;
        long currentTime = System.currentTimeMillis();
        HashMap<Long, ArrayList<MessageObject>> messages = new HashMap();
        HashMap<Long, WebPage> webPages = new HashMap();
        ArrayList<MessageObject> pushMessages = new ArrayList();
        ArrayList<Message> messagesArr = new ArrayList();
        HashMap<Long, ArrayList<MessageObject>> editingMessages = new HashMap();
        SparseArray<SparseIntArray> channelViews = new SparseArray();
        SparseArray<Long> markAsReadMessagesInbox = new SparseArray();
        SparseArray<Long> markAsReadMessagesOutbox = new SparseArray();
        ArrayList<Long> markAsReadMessages = new ArrayList();
        HashMap<Integer, Integer> markAsReadEncrypted = new HashMap();
        SparseArray<ArrayList<Integer>> deletedMessages = new SparseArray();
        boolean printChanged = false;
        ArrayList<ChatParticipants> chatInfoToUpdate = new ArrayList();
        ArrayList<Update> updatesOnMainThread = new ArrayList();
        ArrayList<TL_updateEncryptedMessagesRead> tasks = new ArrayList();
        ArrayList<Integer> contactsIds = new ArrayList();
        boolean checkForUsers = true;
        if (usersArr != null) {
            usersDict = new ConcurrentHashMap();
            for (a = 0; a < usersArr.size(); a += UPDATE_MASK_NAME) {
                User user = (User) usersArr.get(a);
                usersDict.put(Integer.valueOf(user.id), user);
            }
        } else {
            checkForUsers = false;
            usersDict = this.users;
        }
        if (chatsArr != null) {
            chatsDict = new ConcurrentHashMap();
            for (a = 0; a < chatsArr.size(); a += UPDATE_MASK_NAME) {
                Chat chat = (Chat) chatsArr.get(a);
                chatsDict.put(Integer.valueOf(chat.id), chat);
            }
        } else {
            checkForUsers = false;
            chatsDict = this.chats;
        }
        if (fromGetDifference) {
            checkForUsers = false;
        }
        if (!(usersArr == null && chatsArr == null)) {
            AndroidUtilities.runOnUIThread(new AnonymousClass102(usersArr, chatsArr));
        }
        int interfaceUpdateMask = 0;
        for (int c = 0; c < updates.size(); c += UPDATE_MASK_NAME) {
            ArrayList<Integer> arrayList;
            Iterator i$;
            Update update = (Update) updates.get(c);
            Message message;
            int user_id;
            int count;
            MessageEntity entity;
            ConcurrentHashMap<Long, Integer> read_max;
            Integer value;
            MessageObject messageObject;
            ArrayList<MessageObject> arr;
            if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                if (update instanceof TL_updateNewMessage) {
                    message = ((TL_updateNewMessage) update).message;
                } else {
                    message = ((TL_updateNewChannelMessage) update).message;
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m10d("tmessages", update + " channelId = " + message.to_id.channel_id);
                    }
                    if (!message.out && message.from_id == UserConfig.getClientUserId()) {
                        message.out = true;
                    }
                }
                chat = null;
                if (checkForUsers) {
                    int chat_id = 0;
                    user_id = 0;
                    if (message.to_id.channel_id != 0) {
                        chat_id = message.to_id.channel_id;
                    } else if (message.to_id.chat_id != 0) {
                        chat_id = message.to_id.chat_id;
                    } else if (message.to_id.user_id != 0) {
                        user_id = message.to_id.user_id;
                    }
                    if (chat_id != 0) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(chat_id));
                        if (chat == null) {
                            chat = getChat(Integer.valueOf(chat_id));
                        }
                        if (chat == null) {
                            chat = MessagesStorage.getInstance().getChatSync(chat_id);
                            putChat(chat, true);
                        }
                        if (chat == null) {
                            FileLog.m10d("tmessages", "not found chat " + chat_id);
                            return false;
                        } else if (chat.megagroup) {
                            message.flags |= LinearLayoutManager.INVALID_OFFSET;
                        }
                    }
                    count = message.entities.size() + 3;
                    for (a = 0; a < count; a += UPDATE_MASK_NAME) {
                        boolean allowMin = false;
                        if (a != 0) {
                            if (a == UPDATE_MASK_NAME) {
                                user_id = message.from_id;
                                if (message.post) {
                                    allowMin = true;
                                }
                            } else if (a == UPDATE_MASK_AVATAR) {
                                user_id = message.fwd_from != null ? message.fwd_from.from_id : 0;
                            } else {
                                entity = (MessageEntity) message.entities.get(a - 3);
                                user_id = entity instanceof TL_messageEntityMentionName ? ((TL_messageEntityMentionName) entity).user_id : 0;
                            }
                        }
                        if (user_id > 0) {
                            user = (User) usersDict.get(Integer.valueOf(user_id));
                            if (user == null || (!allowMin && user.min)) {
                                user = getUser(Integer.valueOf(user_id));
                            }
                            if (user == null || (!allowMin && user.min)) {
                                user = MessagesStorage.getInstance().getUserSync(user_id);
                                if (!(user == null || allowMin || !user.min)) {
                                    user = null;
                                }
                                putUser(user, true);
                            }
                            if (user == null) {
                                FileLog.m10d("tmessages", "not found user " + user_id);
                                return false;
                            } else if (a == UPDATE_MASK_NAME && user.status != null && user.status.expires <= 0) {
                                this.onlinePrivacy.put(Integer.valueOf(user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                                interfaceUpdateMask |= UPDATE_MASK_STATUS;
                            }
                        }
                    }
                }
                if (message.action instanceof TL_messageActionChatDeleteUser) {
                    user = (User) usersDict.get(Integer.valueOf(message.action.user_id));
                    if (user != null && user.bot) {
                        message.reply_markup = new TL_replyKeyboardHide();
                    } else if (message.from_id == UserConfig.getClientUserId() && message.action.user_id == UserConfig.getClientUserId()) {
                    }
                }
                messagesArr.add(message);
                ImageLoader.saveMessageThumbs(message);
                if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else if (message.to_id.channel_id != 0) {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                } else {
                    if (message.to_id.user_id == UserConfig.getClientUserId()) {
                        message.to_id.user_id = message.from_id;
                    }
                    message.dialog_id = (long) message.to_id.user_id;
                }
                read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                    read_max.put(Long.valueOf(message.dialog_id), value);
                }
                boolean z = value.intValue() < message.id && !((chat != null && ChatObject.isNotInChat(chat)) || (message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate));
                message.unread = z;
                messageObject = new MessageObject(message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if (messageObject.type == 11) {
                    interfaceUpdateMask |= UPDATE_MASK_CHAT_AVATAR;
                } else if (messageObject.type == 10) {
                    interfaceUpdateMask |= UPDATE_MASK_CHAT_NAME;
                }
                arr = (ArrayList) messages.get(Long.valueOf(message.dialog_id));
                if (arr == null) {
                    arr = new ArrayList();
                    messages.put(Long.valueOf(message.dialog_id), arr);
                }
                arr.add(messageObject);
                if (!messageObject.isOut() && messageObject.isUnread()) {
                    pushMessages.add(messageObject);
                }
            } else if (update instanceof TL_updateReadMessagesContents) {
                for (a = 0; a < update.messages.size(); a += UPDATE_MASK_NAME) {
                    markAsReadMessages.add(Long.valueOf((long) ((Integer) update.messages.get(a)).intValue()));
                }
            } else if ((update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox)) {
                Peer peer;
                if (update instanceof TL_updateReadHistoryInbox) {
                    peer = ((TL_updateReadHistoryInbox) update).peer;
                    if (peer.chat_id != 0) {
                        markAsReadMessagesInbox.put(-peer.chat_id, Long.valueOf((long) update.max_id));
                        dialog_id = (long) (-peer.chat_id);
                    } else {
                        markAsReadMessagesInbox.put(peer.user_id, Long.valueOf((long) update.max_id));
                        dialog_id = (long) peer.user_id;
                    }
                    read_max = this.dialogs_read_inbox_max;
                } else {
                    peer = ((TL_updateReadHistoryOutbox) update).peer;
                    if (peer.chat_id != 0) {
                        markAsReadMessagesOutbox.put(-peer.chat_id, Long.valueOf((long) update.max_id));
                        dialog_id = (long) (-peer.chat_id);
                    } else {
                        markAsReadMessagesOutbox.put(peer.user_id, Long.valueOf((long) update.max_id));
                        dialog_id = (long) peer.user_id;
                    }
                    read_max = this.dialogs_read_outbox_max;
                }
                value = (Integer) read_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(update instanceof TL_updateReadHistoryOutbox, dialog_id));
                }
                read_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), update.max_id)));
            } else if (update instanceof TL_updateDeleteMessages) {
                arrayList = (ArrayList) deletedMessages.get(0);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    deletedMessages.put(0, arrayList);
                }
                arrayList.addAll(update.messages);
            } else if ((update instanceof TL_updateUserTyping) || (update instanceof TL_updateChatUserTyping)) {
                if (update.user_id != UserConfig.getClientUserId()) {
                    uid = (long) (-update.chat_id);
                    if (uid == 0) {
                        uid = (long) update.user_id;
                    }
                    arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
                    if (!(update.action instanceof TL_sendMessageCancelAction)) {
                        if (arr == null) {
                            arr = new ArrayList();
                            this.printingUsers.put(Long.valueOf(uid), arr);
                        }
                        exist = false;
                        i$ = arr.iterator();
                        while (i$.hasNext()) {
                            u = (PrintingUser) i$.next();
                            if (u.userId == update.user_id) {
                                exist = true;
                                u.lastTime = currentTime;
                                if (u.action.getClass() != update.action.getClass()) {
                                    printChanged = true;
                                }
                                u.action = update.action;
                                if (!exist) {
                                    newUser = new PrintingUser();
                                    newUser.userId = update.user_id;
                                    newUser.lastTime = currentTime;
                                    newUser.action = update.action;
                                    arr.add(newUser);
                                    printChanged = true;
                                }
                            }
                        }
                        if (exist) {
                            newUser = new PrintingUser();
                            newUser.userId = update.user_id;
                            newUser.lastTime = currentTime;
                            newUser.action = update.action;
                            arr.add(newUser);
                            printChanged = true;
                        }
                    } else if (arr != null) {
                        for (a = 0; a < arr.size(); a += UPDATE_MASK_NAME) {
                            if (((PrintingUser) arr.get(a)).userId == update.user_id) {
                                arr.remove(a);
                                printChanged = true;
                                break;
                            }
                        }
                        if (arr.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(uid));
                        }
                    }
                    this.onlinePrivacy.put(Integer.valueOf(update.user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                }
            } else if (update instanceof TL_updateChatParticipants) {
                interfaceUpdateMask |= UPDATE_MASK_CHAT_MEMBERS;
                chatInfoToUpdate.add(update.participants);
            } else if (update instanceof TL_updateUserStatus) {
                interfaceUpdateMask |= UPDATE_MASK_STATUS;
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateUserName) {
                interfaceUpdateMask |= UPDATE_MASK_NAME;
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateUserPhoto) {
                interfaceUpdateMask |= UPDATE_MASK_AVATAR;
                MessagesStorage.getInstance().clearUserPhotos(update.user_id);
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateUserPhone) {
                interfaceUpdateMask |= UPDATE_MASK_PHONE;
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateContactRegistered) {
                if (this.enableJoined) {
                    if (usersDict.containsKey(Integer.valueOf(update.user_id)) && !MessagesStorage.getInstance().isDialogHasMessages((long) update.user_id)) {
                        newMessage = new TL_messageService();
                        newMessage.action = new TL_messageActionUserJoined();
                        r4 = UserConfig.getNewMessageId();
                        newMessage.id = r4;
                        newMessage.local_id = r4;
                        UserConfig.saveConfig(false);
                        newMessage.unread = false;
                        newMessage.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                        newMessage.date = update.date;
                        newMessage.from_id = update.user_id;
                        newMessage.to_id = new TL_peerUser();
                        newMessage.to_id.user_id = UserConfig.getClientUserId();
                        newMessage.dialog_id = (long) update.user_id;
                        messagesArr.add(newMessage);
                        messageObject = new MessageObject(newMessage, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(newMessage.dialog_id)));
                        arr = (ArrayList) messages.get(Long.valueOf(newMessage.dialog_id));
                        if (arr == null) {
                            arr = new ArrayList();
                            messages.put(Long.valueOf(newMessage.dialog_id), arr);
                        }
                        arr.add(messageObject);
                    }
                }
            } else if (update instanceof TL_updateContactLink) {
                int idx;
                if (update.my_link instanceof TL_contactLinkContact) {
                    idx = contactsIds.indexOf(Integer.valueOf(-update.user_id));
                    if (idx != -1) {
                        contactsIds.remove(idx);
                    }
                    if (!contactsIds.contains(Integer.valueOf(update.user_id))) {
                        contactsIds.add(Integer.valueOf(update.user_id));
                    }
                } else {
                    idx = contactsIds.indexOf(Integer.valueOf(update.user_id));
                    if (idx != -1) {
                        contactsIds.remove(idx);
                    }
                    if (!contactsIds.contains(Integer.valueOf(update.user_id))) {
                        contactsIds.add(Integer.valueOf(-update.user_id));
                    }
                }
            } else if (update instanceof TL_updateNewAuthorization) {
                if (!MessagesStorage.getInstance().hasAuthMessage(update.date)) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.newSessionReceived, new Object[0]);
                        }
                    });
                    newMessage = new TL_messageService();
                    newMessage.action = new TL_messageActionLoginUnknownLocation();
                    newMessage.action.title = update.device;
                    newMessage.action.address = update.location;
                    r4 = UserConfig.getNewMessageId();
                    newMessage.id = r4;
                    newMessage.local_id = r4;
                    UserConfig.saveConfig(false);
                    newMessage.unread = true;
                    newMessage.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                    newMessage.date = update.date;
                    newMessage.from_id = 777000;
                    newMessage.to_id = new TL_peerUser();
                    newMessage.to_id.user_id = UserConfig.getClientUserId();
                    newMessage.dialog_id = 777000;
                    messagesArr.add(newMessage);
                    messageObject = new MessageObject(newMessage, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(newMessage.dialog_id)));
                    arr = (ArrayList) messages.get(Long.valueOf(newMessage.dialog_id));
                    if (arr == null) {
                        arr = new ArrayList();
                        messages.put(Long.valueOf(newMessage.dialog_id), arr);
                    }
                    arr.add(messageObject);
                    pushMessages.add(messageObject);
                }
            } else if (update instanceof TL_updateNewGeoChatMessage) {
                continue;
            } else if (update instanceof TL_updateNewEncryptedMessage) {
                ArrayList<Message> decryptedMessages = SecretChatHelper.getInstance().decryptMessage(((TL_updateNewEncryptedMessage) update).message);
                if (!(decryptedMessages == null || decryptedMessages.isEmpty())) {
                    uid = ((long) ((TL_updateNewEncryptedMessage) update).message.chat_id) << UPDATE_MASK_CHAT_MEMBERS;
                    arr = (ArrayList) messages.get(Long.valueOf(uid));
                    if (arr == null) {
                        arr = new ArrayList();
                        messages.put(Long.valueOf(uid), arr);
                    }
                    for (a = 0; a < decryptedMessages.size(); a += UPDATE_MASK_NAME) {
                        message = (Message) decryptedMessages.get(a);
                        ImageLoader.saveMessageThumbs(message);
                        messagesArr.add(message);
                        messageObject = new MessageObject(message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(uid)));
                        arr.add(messageObject);
                        pushMessages.add(messageObject);
                    }
                }
            } else if (update instanceof TL_updateEncryptedChatTyping) {
                EncryptedChat encryptedChat = getEncryptedChatDB(update.chat_id);
                if (encryptedChat != null) {
                    update.user_id = encryptedChat.user_id;
                    uid = ((long) update.chat_id) << UPDATE_MASK_CHAT_MEMBERS;
                    arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
                    if (arr == null) {
                        arr = new ArrayList();
                        this.printingUsers.put(Long.valueOf(uid), arr);
                    }
                    exist = false;
                    i$ = arr.iterator();
                    while (i$.hasNext()) {
                        u = (PrintingUser) i$.next();
                        if (u.userId == update.user_id) {
                            exist = true;
                            u.lastTime = currentTime;
                            u.action = new TL_sendMessageTypingAction();
                            break;
                        }
                    }
                    if (!exist) {
                        newUser = new PrintingUser();
                        newUser.userId = update.user_id;
                        newUser.lastTime = currentTime;
                        newUser.action = new TL_sendMessageTypingAction();
                        arr.add(newUser);
                        printChanged = true;
                    }
                    this.onlinePrivacy.put(Integer.valueOf(update.user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                }
            } else if (update instanceof TL_updateEncryptedMessagesRead) {
                markAsReadEncrypted.put(Integer.valueOf(update.chat_id), Integer.valueOf(Math.max(update.max_date, update.date)));
                tasks.add((TL_updateEncryptedMessagesRead) update);
            } else if (update instanceof TL_updateChatParticipantAdd) {
                MessagesStorage.getInstance().updateChatInfo(update.chat_id, update.user_id, 0, update.inviter_id, update.version);
            } else if (update instanceof TL_updateChatParticipantDelete) {
                MessagesStorage.getInstance().updateChatInfo(update.chat_id, update.user_id, UPDATE_MASK_NAME, 0, update.version);
            } else if (update instanceof TL_updateDcOptions) {
                ConnectionsManager.getInstance().updateDcSettings();
            } else if (update instanceof TL_updateEncryption) {
                SecretChatHelper.getInstance().processUpdateEncryption((TL_updateEncryption) update, usersDict);
            } else if (update instanceof TL_updateUserBlocked) {
                TL_updateUserBlocked finalUpdate = (TL_updateUserBlocked) update;
                if (finalUpdate.blocked) {
                    ArrayList<Integer> ids = new ArrayList();
                    ids.add(Integer.valueOf(finalUpdate.user_id));
                    MessagesStorage.getInstance().putBlockedUsers(ids, false);
                } else {
                    MessagesStorage.getInstance().deleteBlockedUser(finalUpdate.user_id);
                }
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass104(finalUpdate));
            } else if (update instanceof TL_updateNotifySettings) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateServiceNotification) {
                TL_updateServiceNotification notification = (TL_updateServiceNotification) update;
                if (notification.popup && notification.message != null && notification.message.length() > 0) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.needShowAlert;
                    Object[] objArr = new Object[UPDATE_MASK_AVATAR];
                    objArr[0] = Integer.valueOf(UPDATE_MASK_AVATAR);
                    objArr[UPDATE_MASK_NAME] = notification.message;
                    instance.postNotificationName(i, objArr);
                }
                newMessage = new TL_message();
                r4 = UserConfig.getNewMessageId();
                newMessage.id = r4;
                newMessage.local_id = r4;
                UserConfig.saveConfig(false);
                newMessage.unread = true;
                newMessage.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                newMessage.date = ConnectionsManager.getInstance().getCurrentTime();
                newMessage.from_id = 777000;
                newMessage.to_id = new TL_peerUser();
                newMessage.to_id.user_id = UserConfig.getClientUserId();
                newMessage.dialog_id = 777000;
                newMessage.media = update.media;
                newMessage.flags |= UPDATE_MASK_SELECT_DIALOG;
                newMessage.message = notification.message;
                messagesArr.add(newMessage);
                messageObject = new MessageObject(newMessage, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(newMessage.dialog_id)));
                arr = (ArrayList) messages.get(Long.valueOf(newMessage.dialog_id));
                if (arr == null) {
                    arr = new ArrayList();
                    messages.put(Long.valueOf(newMessage.dialog_id), arr);
                }
                arr.add(messageObject);
                pushMessages.add(messageObject);
            } else if (update instanceof TL_updatePrivacy) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateWebPage) {
                webPages.put(Long.valueOf(update.webpage.id), update.webpage);
            } else if (update instanceof TL_updateChannelTooLong) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m10d("tmessages", update + " channelId = " + update.channel_id);
                }
                Integer channelPts = (Integer) this.channelsPts.get(Integer.valueOf(update.channel_id));
                if (channelPts == null) {
                    channelPts = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(update.channel_id));
                    if (channelPts.intValue() == 0) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(update.channel_id));
                        if (chat == null || chat.min) {
                            chat = getChat(Integer.valueOf(update.channel_id));
                        }
                        if (chat == null || chat.min) {
                            chat = MessagesStorage.getInstance().getChatSync(update.channel_id);
                            putChat(chat, true);
                        }
                        if (!(chat == null || chat.min)) {
                            loadUnknownChannel(chat, 0);
                        }
                    } else {
                        this.channelsPts.put(Integer.valueOf(update.channel_id), channelPts);
                    }
                }
                if (channelPts.intValue() != 0) {
                    if ((update.flags & UPDATE_MASK_NAME) == 0) {
                        getChannelDifference(update.channel_id);
                    } else if (update.pts > channelPts.intValue()) {
                        getChannelDifference(update.channel_id);
                    }
                }
            } else if ((update instanceof TL_updateReadChannelInbox) || (update instanceof TL_updateReadChannelOutbox)) {
                long message_id = ((long) update.max_id) | (((long) update.channel_id) << UPDATE_MASK_CHAT_MEMBERS);
                dialog_id = (long) (-update.channel_id);
                if (update instanceof TL_updateReadChannelInbox) {
                    read_max = this.dialogs_read_inbox_max;
                    markAsReadMessagesInbox.put(-update.channel_id, Long.valueOf(message_id));
                } else {
                    read_max = this.dialogs_read_outbox_max;
                    markAsReadMessagesOutbox.put(-update.channel_id, Long.valueOf(message_id));
                }
                value = (Integer) read_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(update instanceof TL_updateReadChannelOutbox, dialog_id));
                }
                read_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), update.max_id)));
            } else if (update instanceof TL_updateDeleteChannelMessages) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m10d("tmessages", update + " channelId = " + update.channel_id);
                }
                arrayList = (ArrayList) deletedMessages.get(update.channel_id);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    deletedMessages.put(update.channel_id, arrayList);
                }
                arrayList.addAll(update.messages);
            } else if (update instanceof TL_updateChannel) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m10d("tmessages", update + " channelId = " + update.channel_id);
                }
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateChannelMessageViews) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m10d("tmessages", update + " channelId = " + update.channel_id);
                }
                TL_updateChannelMessageViews updateChannelMessageViews = (TL_updateChannelMessageViews) update;
                SparseIntArray array = (SparseIntArray) channelViews.get(update.channel_id);
                if (array == null) {
                    array = new SparseIntArray();
                    channelViews.put(update.channel_id, array);
                }
                array.put(updateChannelMessageViews.id, update.views);
            } else if (update instanceof TL_updateChatParticipantAdmin) {
                MessagesStorage.getInstance().updateChatInfo(update.chat_id, update.user_id, UPDATE_MASK_AVATAR, update.is_admin ? UPDATE_MASK_NAME : 0, update.version);
            } else if (update instanceof TL_updateChatAdmins) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateStickerSets) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateStickerSetsOrder) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateNewStickerSet) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateDraftMessage) {
                updatesOnMainThread.add(update);
            } else if (update instanceof TL_updateSavedGifs) {
                updatesOnMainThread.add(update);
            } else if ((update instanceof TL_updateEditChannelMessage) || (update instanceof TL_updateEditMessage)) {
                if (update instanceof TL_updateEditChannelMessage) {
                    message = ((TL_updateEditChannelMessage) update).message;
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m10d("tmessages", update + " channelId = " + message.to_id.channel_id);
                    }
                    chat = (Chat) chatsDict.get(Integer.valueOf(message.to_id.channel_id));
                    if (chat == null) {
                        chat = getChat(Integer.valueOf(message.to_id.channel_id));
                    }
                    if (chat == null) {
                        chat = MessagesStorage.getInstance().getChatSync(message.to_id.channel_id);
                        putChat(chat, true);
                    }
                    if (chat != null && chat.megagroup) {
                        message.flags |= LinearLayoutManager.INVALID_OFFSET;
                    }
                } else {
                    message = ((TL_updateEditMessage) update).message;
                }
                if (message.out && (message.message == null || message.message.length() == 0)) {
                    message.message = "-1";
                    message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                }
                if (!fromGetDifference) {
                    count = message.entities.size();
                    for (a = 0; a < count; a += UPDATE_MASK_NAME) {
                        entity = (MessageEntity) message.entities.get(a);
                        if (entity instanceof TL_messageEntityMentionName) {
                            user_id = ((TL_messageEntityMentionName) entity).user_id;
                            user = (User) usersDict.get(Integer.valueOf(user_id));
                            if (user == null || user.min) {
                                user = getUser(Integer.valueOf(user_id));
                            }
                            if (user == null || user.min) {
                                user = MessagesStorage.getInstance().getUserSync(user_id);
                                if (user != null && user.min) {
                                    user = null;
                                }
                                putUser(user, true);
                            }
                            if (user == null) {
                                return false;
                            }
                        }
                    }
                }
                if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else if (message.to_id.channel_id != 0) {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                } else {
                    if (message.to_id.user_id == UserConfig.getClientUserId()) {
                        message.to_id.user_id = message.from_id;
                    }
                    message.dialog_id = (long) message.to_id.user_id;
                }
                read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                    read_max.put(Long.valueOf(message.dialog_id), value);
                }
                message.unread = value.intValue() < message.id;
                ImageLoader.saveMessageThumbs(message);
                messageObject = new MessageObject(message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                arr = (ArrayList) editingMessages.get(Long.valueOf(message.dialog_id));
                if (arr == null) {
                    arr = new ArrayList();
                    editingMessages.put(Long.valueOf(message.dialog_id), arr);
                }
                arr.add(messageObject);
            } else if (update instanceof TL_updateChannelPinnedMessage) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m10d("tmessages", update + " channelId = " + update.channel_id);
                }
                MessagesStorage.getInstance().updateChannelPinnedMessage(update.channel_id, ((TL_updateChannelPinnedMessage) update).id);
            }
        }
        if (!messages.isEmpty()) {
            for (Entry<Long, ArrayList<MessageObject>> pair : messages.entrySet()) {
                if (updatePrintingUsersWithNewMessages(((Long) pair.getKey()).longValue(), (ArrayList) pair.getValue())) {
                    printChanged = true;
                }
            }
        }
        if (printChanged) {
            updatePrintingStrings();
        }
        int interfaceUpdateMaskFinal = interfaceUpdateMask;
        boolean printChangedArg = printChanged;
        if (!contactsIds.isEmpty()) {
            ContactsController.getInstance().processContactsUpdates(contactsIds, usersDict);
        }
        if (!pushMessages.isEmpty()) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass105(pushMessages));
        }
        if (!messagesArr.isEmpty()) {
            MessagesStorage.getInstance().putMessages((ArrayList) messagesArr, true, true, false, MediaController.getInstance().getAutodownloadMask());
        }
        if (!editingMessages.isEmpty()) {
            for (Entry<Long, ArrayList<MessageObject>> pair2 : editingMessages.entrySet()) {
                messages_Messages messagesRes = new TL_messages_messages();
                ArrayList<MessageObject> messageObjects = (ArrayList) pair2.getValue();
                for (a = 0; a < messageObjects.size(); a += UPDATE_MASK_NAME) {
                    messagesRes.messages.add(((MessageObject) messageObjects.get(a)).messageOwner);
                }
                MessagesStorage.getInstance().putMessages(messagesRes, ((Long) pair2.getKey()).longValue(), -2, 0, false);
            }
        }
        if (channelViews.size() != 0) {
            MessagesStorage.getInstance().putChannelViews(channelViews, true);
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass106(interfaceUpdateMaskFinal, updatesOnMainThread, webPages, messages, editingMessages, printChangedArg, contactsIds, chatInfoToUpdate, channelViews));
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass107(markAsReadMessagesInbox, markAsReadMessagesOutbox, markAsReadEncrypted, markAsReadMessages, deletedMessages));
        if (!webPages.isEmpty()) {
            MessagesStorage.getInstance().putWebPages(webPages);
        }
        if (!(markAsReadMessagesInbox.size() == 0 && markAsReadMessagesOutbox.size() == 0 && markAsReadEncrypted.isEmpty())) {
            if (markAsReadMessagesInbox.size() != 0) {
                MessagesStorage.getInstance().updateDialogsWithReadMessages(markAsReadMessagesInbox, markAsReadMessagesOutbox, true);
            }
            MessagesStorage.getInstance().markMessagesAsRead(markAsReadMessagesInbox, markAsReadMessagesOutbox, markAsReadEncrypted, true);
        }
        if (!markAsReadMessages.isEmpty()) {
            MessagesStorage.getInstance().markMessagesContentAsRead(markAsReadMessages);
        }
        if (deletedMessages.size() != 0) {
            for (a = 0; a < deletedMessages.size(); a += UPDATE_MASK_NAME) {
                int key = deletedMessages.keyAt(a);
                arrayList = (ArrayList) deletedMessages.get(key);
                MessagesStorage.getInstance().markMessagesAsDeleted(arrayList, true, key);
                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(arrayList, true, key);
            }
        }
        if (!tasks.isEmpty()) {
            for (a = 0; a < tasks.size(); a += UPDATE_MASK_NAME) {
                TL_updateEncryptedMessagesRead update2 = (TL_updateEncryptedMessagesRead) tasks.get(a);
                MessagesStorage.getInstance().createTaskForSecretChat(update2.chat_id, update2.max_date, update2.date, UPDATE_MASK_NAME, null);
            }
        }
        return true;
    }

    private boolean isNotifySettingsMuted(PeerNotifySettings settings) {
        return (settings instanceof TL_peerNotifySettings) && settings.mute_until > ConnectionsManager.getInstance().getCurrentTime();
    }

    public boolean isDialogMuted(long dialog_id) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        int mute_type = preferences.getInt("notify2_" + dialog_id, 0);
        if (mute_type == UPDATE_MASK_AVATAR) {
            return true;
        }
        if (mute_type != 3 || preferences.getInt("notifyuntil_" + dialog_id, 0) < ConnectionsManager.getInstance().getCurrentTime()) {
            return false;
        }
        return true;
    }

    private boolean updatePrintingUsersWithNewMessages(long uid, ArrayList<MessageObject> messages) {
        if (uid > 0) {
            if (((ArrayList) this.printingUsers.get(Long.valueOf(uid))) != null) {
                this.printingUsers.remove(Long.valueOf(uid));
                return true;
            }
        } else if (uid < 0) {
            ArrayList<Integer> messagesUsers = new ArrayList();
            Iterator i$ = messages.iterator();
            while (i$.hasNext()) {
                MessageObject message = (MessageObject) i$.next();
                if (!messagesUsers.contains(Integer.valueOf(message.messageOwner.from_id))) {
                    messagesUsers.add(Integer.valueOf(message.messageOwner.from_id));
                }
            }
            ArrayList<PrintingUser> arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
            boolean changed = false;
            if (arr != null) {
                int a = 0;
                while (a < arr.size()) {
                    if (messagesUsers.contains(Integer.valueOf(((PrintingUser) arr.get(a)).userId))) {
                        arr.remove(a);
                        a--;
                        if (arr.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(uid));
                        }
                        changed = true;
                    }
                    a += UPDATE_MASK_NAME;
                }
            }
            if (changed) {
                return true;
            }
        }
        return false;
    }

    protected void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages) {
        updateInterfaceWithMessages(uid, messages, false);
    }

    protected static void addNewGifToRecent(Document document, int date) {
        ArrayList<SearchImage> arrayList = new ArrayList();
        SearchImage searchImage = new SearchImage();
        searchImage.type = UPDATE_MASK_AVATAR;
        searchImage.document = document;
        searchImage.date = date;
        searchImage.id = TtmlNode.ANONYMOUS_REGION_ID + searchImage.document.id;
        arrayList.add(searchImage);
        MessagesStorage.getInstance().putWebRecent(arrayList);
    }

    protected void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages, boolean isBroadcast) {
        if (messages != null && !messages.isEmpty()) {
            boolean isEncryptedChat = ((int) uid) == 0;
            MessageObject lastMessage = null;
            int channelId = 0;
            boolean updateRating = false;
            for (int a = 0; a < messages.size(); a += UPDATE_MASK_NAME) {
                MessageObject message = (MessageObject) messages.get(a);
                if (lastMessage == null || ((!isEncryptedChat && message.getId() > lastMessage.getId()) || (((isEncryptedChat || (message.getId() < 0 && lastMessage.getId() < 0)) && message.getId() < lastMessage.getId()) || message.messageOwner.date > lastMessage.messageOwner.date))) {
                    lastMessage = message;
                    if (message.messageOwner.to_id.channel_id != 0) {
                        channelId = message.messageOwner.to_id.channel_id;
                    }
                }
                if (message.isOut() && message.isNewGif() && !message.isSending() && !message.isForwarded()) {
                    addNewGifToRecent(message.messageOwner.media.document, message.messageOwner.date);
                }
                if (message.isOut() && message.isSent()) {
                    updateRating = true;
                }
            }
            MessagesQuery.loadReplyMessagesForMessages(messages, uid);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.didReceivedNewMessages;
            Object[] objArr = new Object[UPDATE_MASK_AVATAR];
            objArr[0] = Long.valueOf(uid);
            objArr[UPDATE_MASK_NAME] = messages;
            instance.postNotificationName(i, objArr);
            if (lastMessage != null) {
                TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(Long.valueOf(uid));
                MessageObject object;
                if (!(lastMessage.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                    boolean changed = false;
                    if (dialog == null) {
                        if (!isBroadcast) {
                            Chat chat = getChat(Integer.valueOf(channelId));
                            if (channelId != 0 && chat == null) {
                                return;
                            }
                            if (chat == null || !chat.left) {
                                dialog = new TL_dialog();
                                dialog.id = uid;
                                dialog.unread_count = 0;
                                dialog.top_message = lastMessage.getId();
                                dialog.last_message_date = lastMessage.messageOwner.date;
                                dialog.flags = ChatObject.isChannel(chat) ? UPDATE_MASK_NAME : 0;
                                this.dialogs_dict.put(Long.valueOf(uid), dialog);
                                this.dialogs.add(dialog);
                                this.dialogMessage.put(Long.valueOf(uid), lastMessage);
                                if (lastMessage.messageOwner.to_id.channel_id == 0) {
                                    this.dialogMessagesByIds.put(Integer.valueOf(lastMessage.getId()), lastMessage);
                                    if (lastMessage.messageOwner.random_id != 0) {
                                        this.dialogMessagesByRandomIds.put(Long.valueOf(lastMessage.messageOwner.random_id), lastMessage);
                                    }
                                }
                                this.nextDialogsCacheOffset += UPDATE_MASK_NAME;
                                changed = true;
                            } else {
                                return;
                            }
                        }
                    } else if ((dialog.top_message > 0 && lastMessage.getId() > 0 && lastMessage.getId() > dialog.top_message) || ((dialog.top_message < 0 && lastMessage.getId() < 0 && lastMessage.getId() < dialog.top_message) || !this.dialogMessage.containsKey(Long.valueOf(uid)) || dialog.top_message < 0 || dialog.last_message_date <= lastMessage.messageOwner.date)) {
                        object = (MessageObject) this.dialogMessagesByIds.remove(Integer.valueOf(dialog.top_message));
                        if (!(object == null || object.messageOwner.random_id == 0)) {
                            this.dialogMessagesByRandomIds.remove(Long.valueOf(object.messageOwner.random_id));
                        }
                        dialog.top_message = lastMessage.getId();
                        if (!isBroadcast) {
                            dialog.last_message_date = lastMessage.messageOwner.date;
                            changed = true;
                        }
                        this.dialogMessage.put(Long.valueOf(uid), lastMessage);
                        if (lastMessage.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(Integer.valueOf(lastMessage.getId()), lastMessage);
                            if (lastMessage.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.put(Long.valueOf(lastMessage.messageOwner.random_id), lastMessage);
                            }
                        }
                    }
                    if (changed) {
                        sortDialogs(null);
                    }
                    if (updateRating) {
                        SearchQuery.increasePeerRaiting(uid);
                    }
                } else if (dialog != null) {
                    this.dialogs.remove(dialog);
                    this.dialogsServerOnly.remove(dialog);
                    this.dialogsGroupsOnly.remove(dialog);
                    this.dialogs_dict.remove(Long.valueOf(dialog.id));
                    this.dialogs_read_inbox_max.remove(Long.valueOf(dialog.id));
                    this.dialogs_read_outbox_max.remove(Long.valueOf(dialog.id));
                    this.nextDialogsCacheOffset--;
                    this.dialogMessage.remove(Long.valueOf(dialog.id));
                    object = (MessageObject) this.dialogMessagesByIds.remove(Integer.valueOf(dialog.top_message));
                    if (!(object == null || object.messageOwner.random_id == 0)) {
                        this.dialogMessagesByRandomIds.remove(Long.valueOf(object.messageOwner.random_id));
                    }
                    dialog.top_message = 0;
                    NotificationsController.getInstance().removeNotificationsForDialog(dialog.id);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                }
            }
        }
    }

    public void sortDialogs(HashMap<Integer, Chat> chatsDict) {
        this.dialogsServerOnly.clear();
        this.dialogsGroupsOnly.clear();
        Collections.sort(this.dialogs, this.dialogComparator);
        int a = 0;
        while (a < this.dialogs.size()) {
            TL_dialog d = (TL_dialog) this.dialogs.get(a);
            int high_id = (int) (d.id >> UPDATE_MASK_CHAT_MEMBERS);
            int lower_id = (int) d.id;
            if (!(lower_id == 0 || high_id == UPDATE_MASK_NAME)) {
                this.dialogsServerOnly.add(d);
                Chat chat;
                if (DialogObject.isChannel(d)) {
                    chat = getChat(Integer.valueOf(-lower_id));
                    if (chat != null && ((chat.megagroup && chat.editor) || chat.creator)) {
                        this.dialogsGroupsOnly.add(d);
                    }
                } else if (lower_id < 0) {
                    if (chatsDict != null) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(-lower_id));
                        if (!(chat == null || chat.migrated_to == null)) {
                            this.dialogs.remove(a);
                            a--;
                        }
                    }
                    this.dialogsGroupsOnly.add(d);
                }
            }
            a += UPDATE_MASK_NAME;
        }
    }

    private static String getRestrictionReason(String reason) {
        if (reason == null || reason.length() == 0) {
            return null;
        }
        int index = reason.indexOf(": ");
        if (index <= 0) {
            return null;
        }
        String type = reason.substring(0, index);
        if (type.contains("-all") || type.contains("-android")) {
            return reason.substring(index + UPDATE_MASK_AVATAR);
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment fragment, String reason) {
        Builder builder = new Builder(fragment.getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
        builder.setMessage(reason);
        fragment.showDialog(builder.create());
    }

    public static boolean checkCanOpenChat(Bundle bundle, BaseFragment fragment) {
        if (bundle == null || fragment == null) {
            return true;
        }
        User user = null;
        Chat chat = null;
        int user_id = bundle.getInt("user_id", 0);
        int chat_id = bundle.getInt("chat_id", 0);
        if (user_id != 0) {
            user = getInstance().getUser(Integer.valueOf(user_id));
        } else if (chat_id != 0) {
            chat = getInstance().getChat(Integer.valueOf(chat_id));
        }
        if (user == null && chat == null) {
            return true;
        }
        String reason = null;
        if (chat != null) {
            reason = getRestrictionReason(chat.restriction_reason);
        } else if (user != null) {
            reason = getRestrictionReason(user.restriction_reason);
        }
        if (reason == null) {
            return true;
        }
        showCantOpenAlert(fragment, reason);
        return false;
    }

    public static void openChatOrProfileWith(User user, Chat chat, BaseFragment fragment, int type) {
        if ((user != null || chat != null) && fragment != null) {
            String reason = null;
            boolean closeLast = false;
            if (chat != null) {
                reason = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                reason = getRestrictionReason(user.restriction_reason);
                if (user.bot) {
                    type = UPDATE_MASK_NAME;
                    closeLast = true;
                }
            }
            if (reason != null) {
                showCantOpenAlert(fragment, reason);
                return;
            }
            Bundle args = new Bundle();
            if (chat != null) {
                args.putInt("chat_id", chat.id);
            } else {
                args.putInt("user_id", user.id);
            }
            if (type == 0) {
                fragment.presentFragment(new ProfileActivity(args));
            } else {
                fragment.presentFragment(new ChatActivity(args), closeLast);
            }
        }
    }

    public static void openByUserName(String username, BaseFragment fragment, int type) {
        if (username != null && fragment != null) {
            User user = getInstance().getUser(username);
            if (user != null) {
                openChatOrProfileWith(user, null, fragment, type);
            } else if (fragment.getParentActivity() != null) {
                ProgressDialog progressDialog = new ProgressDialog(fragment.getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                req.username = username;
                progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new AnonymousClass109(ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass108(progressDialog, fragment, type)), fragment));
                fragment.setVisibleDialog(progressDialog);
                progressDialog.show();
            }
        }
    }
}
