package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract.Contacts;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.query.MessagesSearchQuery;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botCommand;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channels_reportSpam;
import org.telegram.tgnet.TLRPC.TL_chatForbidden;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_getMessageEditData;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.Holder;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Adapters.StickersAdapter;
import org.telegram.ui.Adapters.StickersAdapter.StickersAdapterDelegate;
import org.telegram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.WebFrameLayout;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.StickersActivity.TouchHelperCallback;
import org.telegram.ui.VideoEditorActivity.VideoEditorActivityDelegate;

public class ChatActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate, PhotoViewerProvider {
    private static final int attach_audio = 3;
    private static final int attach_contact = 5;
    private static final int attach_document = 4;
    private static final int attach_gallery = 1;
    private static final int attach_location = 6;
    private static final int attach_photo = 0;
    private static final int attach_video = 2;
    private static final int bot_help = 30;
    private static final int bot_settings = 31;
    private static final int chat_enc_timer = 13;
    private static final int chat_menu_attach = 14;
    private static final int clear_history = 15;
    private static final int copy = 10;
    private static final int delete = 12;
    private static final int delete_chat = 16;
    private static final int edit_done = 20;
    private static final int forward = 11;
    private static final int id_chat_compose_panel = 1000;
    private static final int mute = 18;
    private static final int reply = 19;
    private static final int report = 21;
    private static final int search = 40;
    private static final int share_contact = 17;
    private SimpleTextView actionModeSubTextView;
    private SimpleTextView actionModeTextView;
    private FrameLayout actionModeTitleContainer;
    private ArrayList<View> actionModeViews;
    private TextView addContactItem;
    private TextView addToContactsButton;
    private TextView alertNameTextView;
    private TextView alertTextView;
    private FrameLayout alertView;
    private AnimatorSet alertViewAnimator;
    private boolean allowContextBotPanel;
    private boolean allowContextBotPanelSecond;
    private boolean allowStickersPanel;
    private ActionBarMenuItem attachItem;
    private ChatAvatarContainer avatarContainer;
    private MessageObject botButtons;
    private HashMap<Integer, BotInfo> botInfo;
    private MessageObject botReplyButtons;
    private String botUser;
    private int botsCount;
    private FrameLayout bottomOverlay;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private TextView bottomOverlayText;
    private boolean[] cacheEndReached;
    private int cantDeleteMessagesCount;
    protected ChatActivityEnterView chatActivityEnterView;
    private ChatActivityAdapter chatAdapter;
    private ChatAttachAlert chatAttachAlert;
    private long chatEnterTime;
    private LinearLayoutManager chatLayoutManager;
    private long chatLeaveTime;
    private RecyclerListView chatListView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache;
    private Dialog closeChatDialog;
    protected Chat currentChat;
    protected EncryptedChat currentEncryptedChat;
    private String currentPicturePath;
    protected User currentUser;
    private long dialog_id;
    private ActionBarMenuItem editDoneItem;
    private AnimatorSet editDoneItemAnimation;
    private ContextProgressView editDoneItemProgress;
    private int editingMessageObjectReqId;
    private View emojiButtonRed;
    private FrameLayout emptyViewContainer;
    private boolean[] endReached;
    private boolean first;
    private boolean firstLoading;
    private int first_unread_id;
    private boolean forceScrollToTop;
    private boolean[] forwardEndReached;
    private ArrayList<MessageObject> forwardingMessages;
    private MessageObject forwaringMessage;
    private ArrayList<CharSequence> foundUrls;
    private WebPage foundWebPage;
    private TextView gifHintTextView;
    private boolean hasBotsCommands;
    private ActionBarMenuItem headerItem;
    private Runnable hideAlertViewRunnable;
    private int highlightMessageId;
    protected ChatFull info;
    private long inlineReturn;
    private boolean isBroadcast;
    private int lastLoadIndex;
    private int last_message_id;
    private int linkSearchRequestId;
    private boolean loading;
    private boolean loadingForward;
    private int loadingPinnedMessage;
    private int loadsCount;
    private int[] maxDate;
    private int[] maxMessageId;
    private FrameLayout mentionContainer;
    private ExtendedGridLayoutManager mentionGridLayoutManager;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private boolean mentionListViewIgnoreLayout;
    private boolean mentionListViewIsScrolling;
    private int mentionListViewLastViewPosition;
    private int mentionListViewLastViewTop;
    private int mentionListViewScrollOffsetY;
    private MentionsAdapter mentionsAdapter;
    private OnItemClickListener mentionsOnItemClickListener;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    protected ArrayList<MessageObject> messages;
    private HashMap<String, ArrayList<MessageObject>> messagesByDays;
    private HashMap<Integer, MessageObject>[] messagesDict;
    private int[] minDate;
    private int[] minMessageId;
    private TextView muteItem;
    private boolean needSelectFromMessageId;
    private int newUnreadMessageCount;
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;
    private boolean openAnimationEnded;
    private boolean openSearchKeyboard;
    private Runnable openSecretPhotoRunnable;
    private FrameLayout pagedownButton;
    private ObjectAnimator pagedownButtonAnimation;
    private TextView pagedownButtonCounter;
    private boolean pagedownButtonShowedByScroll;
    private boolean paused;
    private String pendingLinkSearchString;
    private Runnable pendingWebPageTimeoutRunnable;
    private SimpleTextView pinnedMessageNameTextView;
    private MessageObject pinnedMessageObject;
    private SimpleTextView pinnedMessageTextView;
    private FrameLayout pinnedMessageView;
    private AnimatorSet pinnedMessageViewAnimator;
    private PlayerView playerView;
    private FrameLayout progressView;
    private boolean readWhenResume;
    private int readWithDate;
    private int readWithMid;
    private AnimatorSet replyButtonAnimation;
    private ImageView replyIconImageView;
    private FileLocation replyImageLocation;
    private BackupImageView replyImageView;
    private SimpleTextView replyNameTextView;
    private SimpleTextView replyObjectTextView;
    private MessageObject replyingMessageObject;
    private TextView reportSpamButton;
    private FrameLayout reportSpamContainer;
    private LinearLayout reportSpamView;
    private AnimatorSet reportSpamViewAnimator;
    private int returnToLoadIndex;
    private int returnToMessageId;
    private AnimatorSet runningAnimation;
    private MessageObject scrollToMessage;
    private int scrollToMessagePosition;
    private boolean scrollToTopOnResume;
    private boolean scrollToTopUnReadOnResume;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    private ImageView searchUpButton;
    private TextView secretViewStatusTextView;
    private HashMap<Integer, MessageObject>[] selectedMessagesCanCopyIds;
    private NumberTextView selectedMessagesCountTextView;
    private HashMap<Integer, MessageObject>[] selectedMessagesIds;
    private MessageObject selectedObject;
    private int startLoadFromMessageId;
    private String startVideoEdit;
    private float startX;
    private float startY;
    private StickersAdapter stickersAdapter;
    private RecyclerListView stickersListView;
    private OnItemClickListener stickersOnItemClickListener;
    private FrameLayout stickersPanel;
    private View timeItem2;
    private MessageObject unreadMessageObject;
    private int unread_to_load;
    private boolean userBlocked;
    private Runnable waitingForCharaterEnterRunnable;
    private ArrayList<Integer> waitingForLoad;
    private boolean waitingForReplyMessageLoad;
    private boolean wasPaused;

    /* renamed from: org.telegram.ui.ChatActivity.10 */
    class AnonymousClass10 extends FrameLayout {
        AnonymousClass10(Context x0) {
            super(x0);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
            SimpleTextView access$3300 = ChatActivity.this.actionModeTextView;
            int i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != ChatActivity.attach_video) ? ChatActivity.edit_done : ChatActivity.mute;
            access$3300.setTextSize(i);
            ChatActivity.this.actionModeTextView.measure(MeasureSpec.makeMeasureSpec(width, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), LinearLayoutManager.INVALID_OFFSET));
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                access$3300 = ChatActivity.this.actionModeSubTextView;
                i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != ChatActivity.attach_video) ? ChatActivity.delete_chat : ChatActivity.chat_menu_attach;
                access$3300.setTextSize(i);
                ChatActivity.this.actionModeSubTextView.measure(MeasureSpec.makeMeasureSpec(width, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), LinearLayoutManager.INVALID_OFFSET));
            }
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int textTop;
            int height = bottom - top;
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                int textHeight = ((height / ChatActivity.attach_video) - ChatActivity.this.actionModeTextView.getTextHeight()) / ChatActivity.attach_video;
                float f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != ChatActivity.attach_video) ? 3.0f : 2.0f;
                textTop = textHeight + AndroidUtilities.dp(f);
            } else {
                textTop = (height - ChatActivity.this.actionModeTextView.getTextHeight()) / ChatActivity.attach_video;
            }
            ChatActivity.this.actionModeTextView.layout(ChatActivity.attach_photo, textTop, ChatActivity.this.actionModeTextView.getMeasuredWidth(), ChatActivity.this.actionModeTextView.getTextHeight() + textTop);
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                int textHeight2 = (height / ChatActivity.attach_video) + (((height / ChatActivity.attach_video) - ChatActivity.this.actionModeSubTextView.getTextHeight()) / ChatActivity.attach_video);
                if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == ChatActivity.attach_video) {
                    textTop = textHeight2 - AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    ChatActivity.this.actionModeSubTextView.layout(ChatActivity.attach_photo, textTop, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + textTop);
                } else {
                    textTop = textHeight2 - AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    ChatActivity.this.actionModeSubTextView.layout(ChatActivity.attach_photo, textTop, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + textTop);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.24 */
    class AnonymousClass24 extends FrameLayout {
        private Drawable background;

        AnonymousClass24(Context x0) {
            super(x0);
        }

        public void onDraw(Canvas canvas) {
            if (ChatActivity.this.mentionListView.getChildCount() > 0) {
                if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                    this.background.setBounds(ChatActivity.attach_photo, ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(4.0f), getMeasuredWidth(), getMeasuredHeight());
                } else {
                    this.background.setBounds(ChatActivity.attach_photo, ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
                }
                this.background.draw(canvas);
            }
        }

        public void setBackgroundResource(int resid) {
            this.background = getContext().getResources().getDrawable(resid);
        }

        public void requestLayout() {
            if (!ChatActivity.this.mentionListViewIgnoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.37 */
    class AnonymousClass37 extends FrameLayout {
        AnonymousClass37(Context x0) {
            super(x0);
        }

        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            if (ChatActivity.this.chatActivityEnterView != null) {
                ChatActivity.this.chatActivityEnterView.invalidate();
            }
            if (getVisibility() != 8) {
                int height = getLayoutParams().height;
                if (ChatActivity.this.chatListView != null) {
                    ChatActivity.this.chatListView.setTranslationY(translationY);
                }
                if (ChatActivity.this.progressView != null) {
                    ChatActivity.this.progressView.setTranslationY(translationY);
                }
                if (ChatActivity.this.mentionContainer != null) {
                    ChatActivity.this.mentionContainer.setTranslationY(translationY);
                }
                if (ChatActivity.this.pagedownButton != null) {
                    ChatActivity.this.pagedownButton.setTranslationY(translationY);
                }
            }
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        public void setVisibility(int visibility) {
            float f = 0.0f;
            super.setVisibility(visibility);
            if (visibility == 8) {
                if (ChatActivity.this.chatListView != null) {
                    ChatActivity.this.chatListView.setTranslationY(0.0f);
                }
                if (ChatActivity.this.progressView != null) {
                    ChatActivity.this.progressView.setTranslationY(0.0f);
                }
                if (ChatActivity.this.mentionContainer != null) {
                    ChatActivity.this.mentionContainer.setTranslationY(0.0f);
                }
                if (ChatActivity.this.pagedownButton != null) {
                    FrameLayout access$4300 = ChatActivity.this.pagedownButton;
                    if (ChatActivity.this.pagedownButton.getTag() == null) {
                        f = (float) AndroidUtilities.dp(100.0f);
                    }
                    access$4300.setTranslationY(f);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.3 */
    class C10813 implements Runnable {
        final /* synthetic */ int val$chatId;
        final /* synthetic */ Semaphore val$semaphore;

        C10813(int i, Semaphore semaphore) {
            this.val$chatId = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentChat = MessagesStorage.getInstance().getChat(this.val$chatId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.47 */
    class AnonymousClass47 implements OnClickListener {
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass47(MessageObject messageObject) {
            this.val$messageObject = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            SendMessagesHelper.getInstance().sendMessage(UserConfig.getCurrentUser(), ChatActivity.this.dialog_id, this.val$messageObject, null, null);
            ChatActivity.this.moveScrollToLastMessage();
            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.4 */
    class C10854 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ int val$userId;

        C10854(int i, Semaphore semaphore) {
            this.val$userId = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentUser = MessagesStorage.getInstance().getUser(this.val$userId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.55 */
    class AnonymousClass55 implements Runnable {
        final /* synthetic */ CharSequence val$charSequence;
        final /* synthetic */ boolean val$force;

        /* renamed from: org.telegram.ui.ChatActivity.55.1 */
        class C10861 implements Runnable {
            C10861() {
            }

            public void run() {
                if (ChatActivity.this.foundWebPage != null) {
                    ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                    ChatActivity.this.foundWebPage = null;
                }
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity.55.2 */
        class C10872 implements Runnable {
            C10872() {
            }

            public void run() {
                if (ChatActivity.this.foundWebPage != null) {
                    ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                    ChatActivity.this.foundWebPage = null;
                }
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity.55.3 */
        class C10893 implements Runnable {

            /* renamed from: org.telegram.ui.ChatActivity.55.3.1 */
            class C10881 implements OnClickListener {
                C10881() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    MessagesController.getInstance().secretWebpagePreview = ChatActivity.attach_gallery;
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ChatActivity.attach_photo).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                    ChatActivity.this.foundUrls = null;
                    ChatActivity.this.searchLinks(AnonymousClass55.this.val$charSequence, AnonymousClass55.this.val$force);
                }
            }

            C10893() {
            }

            public void run() {
                Builder builder = new Builder(ChatActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10881());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                builder.setMessage(LocaleController.getString("SecretLinkPreviewAlert", C0691R.string.SecretLinkPreviewAlert));
                ChatActivity.this.showDialog(builder.create());
                MessagesController.getInstance().secretWebpagePreview = ChatActivity.attach_photo;
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ChatActivity.attach_photo).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity.55.4 */
        class C18024 implements RequestDelegate {
            final /* synthetic */ TL_messages_getWebPagePreview val$req;

            /* renamed from: org.telegram.ui.ChatActivity.55.4.1 */
            class C10901 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10901(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChatActivity.this.linkSearchRequestId = ChatActivity.attach_photo;
                    if (this.val$error != null) {
                        return;
                    }
                    if (this.val$response instanceof TL_messageMediaWebPage) {
                        ChatActivity.this.foundWebPage = ((TL_messageMediaWebPage) this.val$response).webpage;
                        if ((ChatActivity.this.foundWebPage instanceof TL_webPage) || (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                            if (ChatActivity.this.foundWebPage instanceof TL_webPagePending) {
                                ChatActivity.this.pendingLinkSearchString = C18024.this.val$req.message;
                            }
                            if (ChatActivity.this.currentEncryptedChat != null && (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                                ChatActivity.this.foundWebPage.url = C18024.this.val$req.message;
                            }
                            ChatActivity.this.showReplyPanel(true, null, null, ChatActivity.this.foundWebPage, false, true);
                        } else if (ChatActivity.this.foundWebPage != null) {
                            ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                            ChatActivity.this.foundWebPage = null;
                        }
                    } else if (ChatActivity.this.foundWebPage != null) {
                        ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                        ChatActivity.this.foundWebPage = null;
                    }
                }
            }

            C18024(TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
                this.val$req = tL_messages_getWebPagePreview;
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C10901(error, response));
            }
        }

        AnonymousClass55(CharSequence charSequence, boolean z) {
            this.val$charSequence = charSequence;
            this.val$force = z;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r12 = this;
            r9 = org.telegram.ui.ChatActivity.this;
            r9 = r9.linkSearchRequestId;
            if (r9 == 0) goto L_0x001c;
        L_0x0008:
            r9 = org.telegram.tgnet.ConnectionsManager.getInstance();
            r10 = org.telegram.ui.ChatActivity.this;
            r10 = r10.linkSearchRequestId;
            r11 = 1;
            r9.cancelRequest(r10, r11);
            r9 = org.telegram.ui.ChatActivity.this;
            r10 = 0;
            r9.linkSearchRequestId = r10;
        L_0x001c:
            r7 = 0;
            r9 = org.telegram.messenger.AndroidUtilities.WEB_URL;	 Catch:{ Exception -> 0x00ce }
            r10 = r12.val$charSequence;	 Catch:{ Exception -> 0x00ce }
            r3 = r9.matcher(r10);	 Catch:{ Exception -> 0x00ce }
            r8 = r7;
        L_0x0026:
            r9 = r3.find();	 Catch:{ Exception -> 0x013f }
            if (r9 == 0) goto L_0x005c;
        L_0x002c:
            r9 = r3.start();	 Catch:{ Exception -> 0x013f }
            if (r9 <= 0) goto L_0x0042;
        L_0x0032:
            r9 = r12.val$charSequence;	 Catch:{ Exception -> 0x013f }
            r10 = r3.start();	 Catch:{ Exception -> 0x013f }
            r10 = r10 + -1;
            r9 = r9.charAt(r10);	 Catch:{ Exception -> 0x013f }
            r10 = 64;
            if (r9 == r10) goto L_0x0026;
        L_0x0042:
            if (r8 != 0) goto L_0x0142;
        L_0x0044:
            r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x013f }
            r7.<init>();	 Catch:{ Exception -> 0x013f }
        L_0x0049:
            r9 = r12.val$charSequence;	 Catch:{ Exception -> 0x00ce }
            r10 = r3.start();	 Catch:{ Exception -> 0x00ce }
            r11 = r3.end();	 Catch:{ Exception -> 0x00ce }
            r9 = r9.subSequence(r10, r11);	 Catch:{ Exception -> 0x00ce }
            r7.add(r9);	 Catch:{ Exception -> 0x00ce }
            r8 = r7;
            goto L_0x0026;
        L_0x005c:
            if (r8 == 0) goto L_0x009e;
        L_0x005e:
            r9 = org.telegram.ui.ChatActivity.this;	 Catch:{ Exception -> 0x013f }
            r9 = r9.foundUrls;	 Catch:{ Exception -> 0x013f }
            if (r9 == 0) goto L_0x009e;
        L_0x0066:
            r9 = r8.size();	 Catch:{ Exception -> 0x013f }
            r10 = org.telegram.ui.ChatActivity.this;	 Catch:{ Exception -> 0x013f }
            r10 = r10.foundUrls;	 Catch:{ Exception -> 0x013f }
            r10 = r10.size();	 Catch:{ Exception -> 0x013f }
            if (r9 != r10) goto L_0x009e;
        L_0x0076:
            r1 = 1;
            r0 = 0;
        L_0x0078:
            r9 = r8.size();	 Catch:{ Exception -> 0x013f }
            if (r0 >= r9) goto L_0x009a;
        L_0x007e:
            r9 = r8.get(r0);	 Catch:{ Exception -> 0x013f }
            r9 = (java.lang.CharSequence) r9;	 Catch:{ Exception -> 0x013f }
            r10 = org.telegram.ui.ChatActivity.this;	 Catch:{ Exception -> 0x013f }
            r10 = r10.foundUrls;	 Catch:{ Exception -> 0x013f }
            r10 = r10.get(r0);	 Catch:{ Exception -> 0x013f }
            r10 = (java.lang.CharSequence) r10;	 Catch:{ Exception -> 0x013f }
            r9 = android.text.TextUtils.equals(r9, r10);	 Catch:{ Exception -> 0x013f }
            if (r9 != 0) goto L_0x0097;
        L_0x0096:
            r1 = 0;
        L_0x0097:
            r0 = r0 + 1;
            goto L_0x0078;
        L_0x009a:
            if (r1 == 0) goto L_0x009e;
        L_0x009c:
            r7 = r8;
        L_0x009d:
            return;
        L_0x009e:
            r9 = org.telegram.ui.ChatActivity.this;	 Catch:{ Exception -> 0x013f }
            r9.foundUrls = r8;	 Catch:{ Exception -> 0x013f }
            if (r8 != 0) goto L_0x00af;
        L_0x00a5:
            r9 = new org.telegram.ui.ChatActivity$55$1;	 Catch:{ Exception -> 0x013f }
            r9.<init>();	 Catch:{ Exception -> 0x013f }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ Exception -> 0x013f }
            r7 = r8;
            goto L_0x009d;
        L_0x00af:
            r9 = " ";
            r6 = android.text.TextUtils.join(r9, r8);	 Catch:{ Exception -> 0x013f }
            r7 = r8;
        L_0x00b6:
            r9 = org.telegram.ui.ChatActivity.this;
            r9 = r9.currentEncryptedChat;
            if (r9 == 0) goto L_0x0104;
        L_0x00bc:
            r9 = org.telegram.messenger.MessagesController.getInstance();
            r9 = r9.secretWebpagePreview;
            r10 = 2;
            if (r9 != r10) goto L_0x0104;
        L_0x00c5:
            r9 = new org.telegram.ui.ChatActivity$55$3;
            r9.<init>();
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);
            goto L_0x009d;
        L_0x00ce:
            r2 = move-exception;
        L_0x00cf:
            r9 = "tmessages";
            org.telegram.messenger.FileLog.m13e(r9, r2);
            r9 = r12.val$charSequence;
            r9 = r9.toString();
            r5 = r9.toLowerCase();
            r9 = r12.val$charSequence;
            r9 = r9.length();
            r10 = 13;
            if (r9 < r10) goto L_0x00f8;
        L_0x00e8:
            r9 = "http://";
            r9 = r5.contains(r9);
            if (r9 != 0) goto L_0x0101;
        L_0x00f0:
            r9 = "https://";
            r9 = r5.contains(r9);
            if (r9 != 0) goto L_0x0101;
        L_0x00f8:
            r9 = new org.telegram.ui.ChatActivity$55$2;
            r9.<init>();
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);
            goto L_0x009d;
        L_0x0101:
            r6 = r12.val$charSequence;
            goto L_0x00b6;
        L_0x0104:
            r4 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview;
            r4.<init>();
            r9 = r6 instanceof java.lang.String;
            if (r9 == 0) goto L_0x0138;
        L_0x010d:
            r6 = (java.lang.String) r6;
            r4.message = r6;
        L_0x0111:
            r9 = org.telegram.ui.ChatActivity.this;
            r10 = org.telegram.tgnet.ConnectionsManager.getInstance();
            r11 = new org.telegram.ui.ChatActivity$55$4;
            r11.<init>(r4);
            r10 = r10.sendRequest(r4, r11);
            r9.linkSearchRequestId = r10;
            r9 = org.telegram.tgnet.ConnectionsManager.getInstance();
            r10 = org.telegram.ui.ChatActivity.this;
            r10 = r10.linkSearchRequestId;
            r11 = org.telegram.ui.ChatActivity.this;
            r11 = r11.classGuid;
            r9.bindRequestToGuid(r10, r11);
            goto L_0x009d;
        L_0x0138:
            r9 = r6.toString();
            r4.message = r9;
            goto L_0x0111;
        L_0x013f:
            r2 = move-exception;
            r7 = r8;
            goto L_0x00cf;
        L_0x0142:
            r7 = r8;
            goto L_0x0049;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatActivity.55.run():void");
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.5 */
    class C10915 implements Runnable {
        final /* synthetic */ int val$encId;
        final /* synthetic */ Semaphore val$semaphore;

        C10915(int i, Semaphore semaphore) {
            this.val$encId = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentEncryptedChat = MessagesStorage.getInstance().getEncryptedChat(this.val$encId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.61 */
    class AnonymousClass61 implements Runnable {
        final /* synthetic */ int val$last_unread_date_final;
        final /* synthetic */ int val$lastid;
        final /* synthetic */ boolean val$wasUnreadFinal;

        AnonymousClass61(int i, int i2, boolean z) {
            this.val$lastid = i;
            this.val$last_unread_date_final = i2;
            this.val$wasUnreadFinal = z;
        }

        public void run() {
            if (ChatActivity.this.last_message_id != 0) {
                MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, this.val$lastid, ChatActivity.this.last_message_id, this.val$last_unread_date_final, this.val$wasUnreadFinal, false);
            } else {
                MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, this.val$lastid, ChatActivity.this.minMessageId[ChatActivity.attach_photo], ChatActivity.this.maxDate[ChatActivity.attach_photo], this.val$wasUnreadFinal, false);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.62 */
    class AnonymousClass62 implements Runnable {
        final /* synthetic */ Bundle val$bundle;
        final /* synthetic */ int val$channel_id;
        final /* synthetic */ BaseFragment val$lastFragment;

        /* renamed from: org.telegram.ui.ChatActivity.62.1 */
        class C10921 implements Runnable {
            C10921() {
            }

            public void run() {
                MessagesController.getInstance().loadFullChat(AnonymousClass62.this.val$channel_id, ChatActivity.attach_photo, true);
            }
        }

        AnonymousClass62(BaseFragment baseFragment, Bundle bundle, int i) {
            this.val$lastFragment = baseFragment;
            this.val$bundle = bundle;
            this.val$channel_id = i;
        }

        public void run() {
            ActionBarLayout parentLayout = ChatActivity.this.parentLayout;
            if (this.val$lastFragment != null) {
                NotificationCenter.getInstance().removeObserver(this.val$lastFragment, NotificationCenter.closeChats);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[ChatActivity.attach_photo]);
            parentLayout.presentFragment(new ChatActivity(this.val$bundle), true);
            AndroidUtilities.runOnUIThread(new C10921(), 1000);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.63 */
    class AnonymousClass63 implements Runnable {
        final /* synthetic */ Bundle val$bundle;
        final /* synthetic */ int val$channel_id;
        final /* synthetic */ BaseFragment val$lastFragment;

        /* renamed from: org.telegram.ui.ChatActivity.63.1 */
        class C10931 implements Runnable {
            C10931() {
            }

            public void run() {
                MessagesController.getInstance().loadFullChat(AnonymousClass63.this.val$channel_id, ChatActivity.attach_photo, true);
            }
        }

        AnonymousClass63(BaseFragment baseFragment, Bundle bundle, int i) {
            this.val$lastFragment = baseFragment;
            this.val$bundle = bundle;
            this.val$channel_id = i;
        }

        public void run() {
            ActionBarLayout parentLayout = ChatActivity.this.parentLayout;
            if (this.val$lastFragment != null) {
                NotificationCenter.getInstance().removeObserver(this.val$lastFragment, NotificationCenter.closeChats);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[ChatActivity.attach_photo]);
            parentLayout.presentFragment(new ChatActivity(this.val$bundle), true);
            AndroidUtilities.runOnUIThread(new C10931(), 1000);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.6 */
    class C10946 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C10946(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentUser = MessagesStorage.getInstance().getUser(ChatActivity.this.currentEncryptedChat.user_id);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.74 */
    class AnonymousClass74 implements View.OnClickListener {
        final /* synthetic */ boolean[] val$checks;

        AnonymousClass74(boolean[] zArr) {
            this.val$checks = zArr;
        }

        public void onClick(View v) {
            CheckBoxCell cell = (CheckBoxCell) v;
            Integer num = (Integer) cell.getTag();
            this.val$checks[num.intValue()] = !this.val$checks[num.intValue()];
            cell.setChecked(this.val$checks[num.intValue()], true);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.75 */
    class AnonymousClass75 implements OnClickListener {
        final /* synthetic */ boolean[] val$checks;
        final /* synthetic */ MessageObject val$finalSelectedObject;
        final /* synthetic */ User val$userFinal;

        /* renamed from: org.telegram.ui.ChatActivity.75.1 */
        class C18041 implements RequestDelegate {
            C18041() {
            }

            public void run(TLObject response, TL_error error) {
            }
        }

        AnonymousClass75(MessageObject messageObject, User user, boolean[] zArr) {
            this.val$finalSelectedObject = messageObject;
            this.val$userFinal = user;
            this.val$checks = zArr;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ArrayList<Integer> ids = null;
            ArrayList<Long> random_ids;
            if (this.val$finalSelectedObject != null) {
                ids = new ArrayList();
                ids.add(Integer.valueOf(this.val$finalSelectedObject.getId()));
                random_ids = null;
                if (!(ChatActivity.this.currentEncryptedChat == null || this.val$finalSelectedObject.messageOwner.random_id == 0 || this.val$finalSelectedObject.type == ChatActivity.copy)) {
                    random_ids = new ArrayList();
                    random_ids.add(Long.valueOf(this.val$finalSelectedObject.messageOwner.random_id));
                }
                MessagesController.getInstance().deleteMessages(ids, random_ids, ChatActivity.this.currentEncryptedChat, this.val$finalSelectedObject.messageOwner.to_id.channel_id);
            } else {
                for (int a = ChatActivity.attach_gallery; a >= 0; a--) {
                    MessageObject msg;
                    ids = new ArrayList(ChatActivity.this.selectedMessagesIds[a].keySet());
                    random_ids = null;
                    int channelId = ChatActivity.attach_photo;
                    if (!ids.isEmpty()) {
                        msg = (MessageObject) ChatActivity.this.selectedMessagesIds[a].get(ids.get(ChatActivity.attach_photo));
                        if (ChatActivity.attach_photo == null && msg.messageOwner.to_id.channel_id != 0) {
                            channelId = msg.messageOwner.to_id.channel_id;
                        }
                    }
                    if (ChatActivity.this.currentEncryptedChat != null) {
                        random_ids = new ArrayList();
                        for (Entry<Integer, MessageObject> entry : ChatActivity.this.selectedMessagesIds[a].entrySet()) {
                            msg = (MessageObject) entry.getValue();
                            if (!(msg.messageOwner.random_id == 0 || msg.type == ChatActivity.copy)) {
                                random_ids.add(Long.valueOf(msg.messageOwner.random_id));
                            }
                        }
                    }
                    MessagesController.getInstance().deleteMessages(ids, random_ids, ChatActivity.this.currentEncryptedChat, channelId);
                }
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
            }
            if (this.val$userFinal != null) {
                if (this.val$checks[ChatActivity.attach_photo]) {
                    MessagesController.getInstance().deleteUserFromChat(ChatActivity.this.currentChat.id, this.val$userFinal, ChatActivity.this.info);
                }
                if (this.val$checks[ChatActivity.attach_gallery]) {
                    TL_channels_reportSpam req = new TL_channels_reportSpam();
                    req.channel = MessagesController.getInputChannel(ChatActivity.this.currentChat);
                    req.user_id = MessagesController.getInputUser(this.val$userFinal);
                    req.id = ids;
                    ConnectionsManager.getInstance().sendRequest(req, new C18041());
                }
                if (this.val$checks[ChatActivity.attach_video]) {
                    MessagesController.getInstance().deleteUserChannelHistory(ChatActivity.this.currentChat, this.val$userFinal, ChatActivity.attach_photo);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.76 */
    class AnonymousClass76 implements OnClickListener {
        final /* synthetic */ ArrayList val$options;

        AnonymousClass76(ArrayList arrayList) {
            this.val$options = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ChatActivity.this.selectedObject != null && i >= 0 && i < this.val$options.size()) {
                ChatActivity.this.processSelectedOption(((Integer) this.val$options.get(i)).intValue());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.79 */
    class AnonymousClass79 implements View.OnClickListener {
        final /* synthetic */ boolean[] val$checks;

        AnonymousClass79(boolean[] zArr) {
            this.val$checks = zArr;
        }

        public void onClick(View v) {
            boolean z;
            CheckBoxCell cell = (CheckBoxCell) v;
            boolean[] zArr = this.val$checks;
            if (this.val$checks[ChatActivity.attach_photo]) {
                z = false;
            } else {
                z = true;
            }
            zArr[ChatActivity.attach_photo] = z;
            cell.setChecked(this.val$checks[ChatActivity.attach_photo], true);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.80 */
    class AnonymousClass80 implements OnClickListener {
        final /* synthetic */ boolean[] val$checks;
        final /* synthetic */ int val$mid;

        AnonymousClass80(int i, boolean[] zArr) {
            this.val$mid = i;
            this.val$checks = zArr;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, this.val$mid, this.val$checks[ChatActivity.attach_photo]);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.83 */
    class AnonymousClass83 implements OnClickListener {
        final /* synthetic */ String val$url;

        AnonymousClass83(String str) {
            this.val$url = str;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Browser.openUrl(ChatActivity.this.getParentActivity(), this.val$url, ChatActivity.this.inlineReturn == 0);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.9 */
    class C10989 implements OnTouchListener {
        C10989() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.12 */
    class AnonymousClass12 extends SizeNotifierFrameLayout {
        int inputFieldHeight;

        AnonymousClass12(Context x0) {
            super(x0);
            this.inputFieldHeight = ChatActivity.attach_photo;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            heightSize -= getPaddingTop();
            if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                heightSize -= ChatActivity.this.chatActivityEnterView.getEmojiPadding();
            }
            int childCount = getChildCount();
            measureChildWithMargins(ChatActivity.this.chatActivityEnterView, widthMeasureSpec, ChatActivity.attach_photo, heightMeasureSpec, ChatActivity.attach_photo);
            this.inputFieldHeight = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
            for (int i = ChatActivity.attach_photo; i < childCount; i += ChatActivity.attach_gallery) {
                View child = getChildAt(i);
                if (!(child == null || child.getVisibility() == 8 || child == ChatActivity.this.chatActivityEnterView)) {
                    if (child == ChatActivity.this.chatListView || child == ChatActivity.this.progressView) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp((float) ((ChatActivity.this.chatActivityEnterView.isTopViewVisible() ? 48 : ChatActivity.attach_photo) + ChatActivity.attach_video)) + (heightSize - this.inputFieldHeight)), C0747C.ENCODING_PCM_32BIT));
                    } else if (child == ChatActivity.this.emptyViewContainer) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(heightSize, C0747C.ENCODING_PCM_32BIT));
                    } else if (ChatActivity.this.chatActivityEnterView.isPopupView(child)) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C0747C.ENCODING_PCM_32BIT));
                    } else if (child == ChatActivity.this.mentionContainer) {
                        int height;
                        LayoutParams layoutParams = (LayoutParams) ChatActivity.this.mentionContainer.getLayoutParams();
                        ChatActivity.this.mentionListViewIgnoreLayout = true;
                        int maxHeight;
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                            maxHeight = ChatActivity.this.mentionGridLayoutManager.getRowsCount(widthSize) * 102;
                            if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                maxHeight += 34;
                            }
                            height = (heightSize - ChatActivity.this.chatActivityEnterView.getMeasuredHeight()) + (maxHeight != 0 ? AndroidUtilities.dp(2.0f) : ChatActivity.attach_photo);
                            ChatActivity.this.mentionListView.setPadding(ChatActivity.attach_photo, Math.max(ChatActivity.attach_photo, height - AndroidUtilities.dp(Math.min((float) maxHeight, 122.399994f))), ChatActivity.attach_photo, ChatActivity.attach_photo);
                        } else {
                            int size = ChatActivity.this.mentionsAdapter.getItemCount();
                            maxHeight = ChatActivity.attach_photo;
                            if (ChatActivity.this.mentionsAdapter.isBotContext()) {
                                if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                    maxHeight = ChatActivity.attach_photo + 36;
                                    size--;
                                }
                                maxHeight += size * 68;
                            } else {
                                maxHeight = ChatActivity.attach_photo + (size * 36);
                            }
                            height = (heightSize - ChatActivity.this.chatActivityEnterView.getMeasuredHeight()) + (maxHeight != 0 ? AndroidUtilities.dp(2.0f) : ChatActivity.attach_photo);
                            ChatActivity.this.mentionListView.setPadding(ChatActivity.attach_photo, Math.max(ChatActivity.attach_photo, height - AndroidUtilities.dp(Math.min((float) maxHeight, 122.399994f))), ChatActivity.attach_photo, ChatActivity.attach_photo);
                        }
                        layoutParams.height = height;
                        layoutParams.topMargin = ChatActivity.attach_photo;
                        ChatActivity.this.mentionListViewIgnoreLayout = false;
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(layoutParams.height, C0747C.ENCODING_PCM_32BIT));
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, ChatActivity.attach_photo, heightMeasureSpec, ChatActivity.attach_photo);
                    }
                }
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            int paddingBottom = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? ChatActivity.this.chatActivityEnterView.getEmojiPadding() : ChatActivity.attach_photo;
            setBottomClip(paddingBottom);
            for (int i = ChatActivity.attach_photo; i < count; i += ChatActivity.attach_gallery) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    int childLeft;
                    int childTop;
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    switch ((gravity & 7) & 7) {
                        case ChatActivity.attach_gallery /*1*/:
                            childLeft = ((((r - l) - width) / ChatActivity.attach_video) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case ChatActivity.attach_contact /*5*/:
                            childLeft = (r - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case ChatActivity.delete_chat /*16*/:
                            childTop = (((((b - paddingBottom) - t) - height) / ChatActivity.attach_video) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            childTop = lp.topMargin + getPaddingTop();
                            break;
                        case 80:
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    if (child == ChatActivity.this.mentionContainer) {
                        childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight() - AndroidUtilities.dp(2.0f);
                    } else if (child == ChatActivity.this.pagedownButton) {
                        childTop -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                    } else if (child == ChatActivity.this.emptyViewContainer) {
                        childTop -= this.inputFieldHeight / ChatActivity.attach_video;
                    } else if (ChatActivity.this.chatActivityEnterView.isPopupView(child)) {
                        childTop = ChatActivity.this.chatActivityEnterView.getBottom();
                    } else if (child == ChatActivity.this.gifHintTextView) {
                        childTop -= this.inputFieldHeight;
                    } else if ((child == ChatActivity.this.chatListView || child == ChatActivity.this.progressView) && ChatActivity.this.chatActivityEnterView.isTopViewVisible()) {
                        childTop -= AndroidUtilities.dp(48.0f);
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            ChatActivity.this.updateMessagesVisisblePart();
            notifyHeightChanged();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.1 */
    class C17961 implements OnItemLongClickListener {
        C17961() {
        }

        public boolean onItemClick(View view, int position) {
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                return false;
            }
            ChatActivity.this.createMenu(view, false);
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.2 */
    class C17972 implements OnItemClickListener {
        C17972() {
        }

        public void onItemClick(View view, int position) {
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                ChatActivity.this.processRowSelect(view);
            } else {
                ChatActivity.this.createMenu(view, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.57 */
    class AnonymousClass57 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$newVisibility;
        final /* synthetic */ ActionBarMenuItem val$replyItem;

        AnonymousClass57(int i, ActionBarMenuItem actionBarMenuItem) {
            this.val$newVisibility = i;
            this.val$replyItem = actionBarMenuItem;
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animation) && this.val$newVisibility == 8) {
                this.val$replyItem.setVisibility(8);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animation)) {
                ChatActivity.this.replyButtonAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.77 */
    class AnonymousClass77 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$show;

        AnonymousClass77(boolean z) {
            this.val$show = z;
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatActivity.this.editDoneItemAnimation != null && ChatActivity.this.editDoneItemAnimation.equals(animation)) {
                if (this.val$show) {
                    ChatActivity.this.editDoneItem.getImageView().setVisibility(ChatActivity.attach_document);
                } else {
                    ChatActivity.this.editDoneItemProgress.setVisibility(ChatActivity.attach_document);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatActivity.this.editDoneItemAnimation != null && ChatActivity.this.editDoneItemAnimation.equals(animation)) {
                ChatActivity.this.editDoneItemAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.7 */
    class C18057 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ChatActivity.7.1 */
        class C10951 implements OnClickListener {
            final /* synthetic */ int val$id;
            final /* synthetic */ boolean val$isChat;

            C10951(int i, boolean z) {
                this.val$id = i;
                this.val$isChat = z;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (this.val$id != ChatActivity.clear_history) {
                    if (!this.val$isChat) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else {
                        MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                    }
                    ChatActivity.this.finishFragment();
                    return;
                }
                MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_gallery);
            }
        }

        C18057() {
        }

        public void onItemClick(int id) {
            int a;
            if (id == -1) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    for (a = ChatActivity.attach_gallery; a >= 0; a--) {
                        ChatActivity.this.selectedMessagesIds[a].clear();
                        ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    }
                    ChatActivity.this.cantDeleteMessagesCount = ChatActivity.attach_photo;
                    if (ChatActivity.this.chatActivityEnterView.isEditingMessage()) {
                        ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                    } else {
                        ChatActivity.this.actionBar.hideActionMode();
                        ChatActivity.this.updatePinnedMessageView(true);
                    }
                    ChatActivity.this.updateVisibleRows();
                    return;
                }
                ChatActivity.this.finishFragment();
            } else if (id == ChatActivity.copy) {
                String str = TtmlNode.ANONYMOUS_REGION_ID;
                int previousUid = ChatActivity.attach_photo;
                for (a = ChatActivity.attach_gallery; a >= 0; a--) {
                    ArrayList<Integer> arrayList = new ArrayList(ChatActivity.this.selectedMessagesCanCopyIds[a].keySet());
                    if (ChatActivity.this.currentEncryptedChat == null) {
                        Collections.sort(arrayList);
                    } else {
                        Collections.sort(arrayList, Collections.reverseOrder());
                    }
                    for (int b = ChatActivity.attach_photo; b < arrayList.size(); b += ChatActivity.attach_gallery) {
                        messageObject = (MessageObject) ChatActivity.this.selectedMessagesCanCopyIds[a].get((Integer) arrayList.get(b));
                        if (str.length() != 0) {
                            str = str + "\n\n";
                        }
                        str = str + ChatActivity.this.getMessageContent(messageObject, previousUid, true);
                        previousUid = messageObject.messageOwner.from_id;
                    }
                }
                if (str.length() != 0) {
                    AndroidUtilities.addToClipboard(str);
                }
                for (a = ChatActivity.attach_gallery; a >= 0; a--) {
                    ChatActivity.this.selectedMessagesIds[a].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                }
                ChatActivity.this.cantDeleteMessagesCount = ChatActivity.attach_photo;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (id == ChatActivity.edit_done) {
                if (ChatActivity.this.chatActivityEnterView == null) {
                    return;
                }
                if (ChatActivity.this.chatActivityEnterView.isEditingCaption() || ChatActivity.this.chatActivityEnterView.hasText()) {
                    ChatActivity.this.chatActivityEnterView.doneEditingMessage();
                }
            } else if (id == ChatActivity.delete) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.createDeleteMessagesAlert(null);
                }
            } else if (id == ChatActivity.forward) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", ChatActivity.attach_gallery);
                BaseFragment dialogsActivity = new DialogsActivity(args);
                dialogsActivity.setDelegate(ChatActivity.this);
                ChatActivity.this.presentFragment(dialogsActivity);
            } else if (id == ChatActivity.chat_enc_timer) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.showDialog(AndroidUtilities.buildTTLAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.currentEncryptedChat).create());
                }
            } else if (id == ChatActivity.clear_history || id == ChatActivity.delete_chat) {
                if (ChatActivity.this.getParentActivity() != null) {
                    boolean isChat = ((int) ChatActivity.this.dialog_id) < 0 && ((int) (ChatActivity.this.dialog_id >> 32)) != ChatActivity.attach_gallery;
                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    if (id == ChatActivity.clear_history) {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0691R.string.AreYouSureClearHistory));
                    } else if (isChat) {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0691R.string.AreYouSureDeleteAndExit));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0691R.string.AreYouSureDeleteThisChat));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10951(id, isChat));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    ChatActivity.this.showDialog(builder.create());
                }
            } else if (id == ChatActivity.share_contact) {
                if (ChatActivity.this.currentUser != null && ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.currentUser.phone == null || ChatActivity.this.currentUser.phone.length() == 0) {
                        ChatActivity.this.shareMyContact(ChatActivity.this.replyingMessageObject);
                        return;
                    }
                    args = new Bundle();
                    args.putInt("user_id", ChatActivity.this.currentUser.id);
                    args.putBoolean("addContact", true);
                    ChatActivity.this.presentFragment(new ContactAddActivity(args));
                }
            } else if (id == ChatActivity.mute) {
                ChatActivity.this.toggleMute(false);
            } else if (id == ChatActivity.report) {
                ChatActivity.this.showDialog(AlertsCreator.createReportAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.dialog_id, ChatActivity.this));
            } else if (id == ChatActivity.reply) {
                messageObject = null;
                a = ChatActivity.attach_gallery;
                while (a >= 0) {
                    if (messageObject == null && ChatActivity.this.selectedMessagesIds[a].size() == ChatActivity.attach_gallery) {
                        messageObject = (MessageObject) ChatActivity.this.messagesDict[a].get(new ArrayList(ChatActivity.this.selectedMessagesIds[a].keySet()).get(ChatActivity.attach_photo));
                    }
                    ChatActivity.this.selectedMessagesIds[a].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[a].clear();
                    a--;
                }
                if (messageObject != null && (messageObject.messageOwner.id > 0 || (messageObject.messageOwner.id < 0 && ChatActivity.this.currentEncryptedChat != null))) {
                    ChatActivity.this.showReplyPanel(true, messageObject, null, null, false, true);
                }
                ChatActivity.this.cantDeleteMessagesCount = ChatActivity.attach_photo;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (id == ChatActivity.chat_menu_attach) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.createChatAttachView();
                    ChatActivity.this.chatAttachAlert.loadGalleryPhotos();
                    if (VERSION.SDK_INT == ChatActivity.report || VERSION.SDK_INT == 22) {
                        ChatActivity.this.chatActivityEnterView.closeKeyboard();
                    }
                    ChatActivity.this.chatAttachAlert.init(ChatActivity.this);
                    ChatActivity.this.showDialog(ChatActivity.this.chatAttachAlert);
                }
            } else if (id == ChatActivity.bot_help) {
                SendMessagesHelper.getInstance().sendMessage("/help", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            } else if (id == ChatActivity.bot_settings) {
                SendMessagesHelper.getInstance().sendMessage("/settings", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            } else if (id == ChatActivity.search) {
                ChatActivity.this.openSearchWithText(null);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.8 */
    class C18068 extends ActionBarMenuItemSearchListener {

        /* renamed from: org.telegram.ui.ChatActivity.8.1 */
        class C10971 implements Runnable {
            C10971() {
            }

            public void run() {
                ChatActivity.this.searchItem.getSearchField().requestFocus();
                AndroidUtilities.showKeyboard(ChatActivity.this.searchItem.getSearchField());
            }
        }

        C18068() {
        }

        public void onSearchCollapse() {
            ChatActivity.this.avatarContainer.setVisibility(ChatActivity.attach_photo);
            if (ChatActivity.this.chatActivityEnterView.hasText()) {
                if (ChatActivity.this.headerItem != null) {
                    ChatActivity.this.headerItem.setVisibility(8);
                }
                if (ChatActivity.this.attachItem != null) {
                    ChatActivity.this.attachItem.setVisibility(ChatActivity.attach_photo);
                }
            } else {
                if (ChatActivity.this.headerItem != null) {
                    ChatActivity.this.headerItem.setVisibility(ChatActivity.attach_photo);
                }
                if (ChatActivity.this.attachItem != null) {
                    ChatActivity.this.attachItem.setVisibility(8);
                }
            }
            ChatActivity.this.searchItem.setVisibility(8);
            ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            ChatActivity.this.updateVisibleRows();
            ChatActivity.this.scrollToLastMessage(false);
            ChatActivity.this.updateBottomOverlay();
        }

        public void onSearchExpand() {
            if (ChatActivity.this.openSearchKeyboard) {
                AndroidUtilities.runOnUIThread(new C10971(), 300);
            }
        }

        public void onSearchPressed(EditText editText) {
            ChatActivity.this.updateSearchButtons(ChatActivity.attach_photo, ChatActivity.attach_photo, ChatActivity.attach_photo);
            MessagesSearchQuery.searchMessagesInChat(editText.getText().toString(), ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, ChatActivity.attach_photo);
        }
    }

    public class ChatActivityAdapter extends Adapter {
        private int botInfoRow;
        private boolean isBot;
        private int loadingDownRow;
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        /* renamed from: org.telegram.ui.ChatActivity.ChatActivityAdapter.4 */
        class C11004 implements OnPreDrawListener {
            final /* synthetic */ ChatMessageCell val$messageCell;

            C11004(ChatMessageCell chatMessageCell) {
                this.val$messageCell = chatMessageCell;
            }

            public boolean onPreDraw() {
                this.val$messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = ChatActivity.this.chatListView.getMeasuredHeight();
                int top = this.val$messageCell.getTop();
                int bottom = this.val$messageCell.getBottom();
                int viewTop = top >= 0 ? ChatActivity.attach_photo : -top;
                int viewBottom = this.val$messageCell.getMeasuredHeight();
                if (viewBottom > height) {
                    viewBottom = viewTop + height;
                }
                this.val$messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                return true;
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity.ChatActivityAdapter.1 */
        class C18071 implements ChatMessageCellDelegate {

            /* renamed from: org.telegram.ui.ChatActivity.ChatActivityAdapter.1.1 */
            class C10991 implements OnClickListener {
                final /* synthetic */ String val$urlFinal;

                C10991(String str) {
                    this.val$urlFinal = str;
                }

                public void onClick(DialogInterface dialog, int which) {
                    boolean z = true;
                    if (which == 0) {
                        Context parentActivity = ChatActivity.this.getParentActivity();
                        String str = this.val$urlFinal;
                        if (ChatActivity.this.inlineReturn != 0) {
                            z = false;
                        }
                        Browser.openUrl(parentActivity, str, z);
                    } else if (which == ChatActivity.attach_gallery) {
                        AndroidUtilities.addToClipboard(this.val$urlFinal);
                    }
                }
            }

            C18071() {
            }

            public void didPressedShare(ChatMessageCell cell) {
                if (ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.closeKeyboard();
                    }
                    ChatActivity chatActivity = ChatActivity.this;
                    Context access$13600 = ChatActivityAdapter.this.mContext;
                    MessageObject messageObject = cell.getMessageObject();
                    boolean z = ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup && ChatActivity.this.currentChat.username != null && ChatActivity.this.currentChat.username.length() > 0;
                    chatActivity.showDialog(new ShareAlert(access$13600, messageObject, z));
                }
            }

            public boolean needPlayAudio(MessageObject messageObject) {
                if (!messageObject.isVoice()) {
                    return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(ChatActivity.this.messages, messageObject) : false;
                } else {
                    boolean result = MediaController.getInstance().playAudio(messageObject);
                    MediaController.getInstance().setVoiceMessagesPlaylist(result ? ChatActivity.this.createVoiceMessagesPlaylist(messageObject, false) : null, false);
                    return result;
                }
            }

            public void didPressedChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(cell);
                } else if (chat != null && chat != ChatActivity.this.currentChat) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (postId != 0) {
                        args.putInt("message_id", postId);
                    }
                    if (MessagesController.checkCanOpenChat(args, ChatActivity.this)) {
                        ChatActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                }
            }

            public void didPressedOther(ChatMessageCell cell) {
                ChatActivity.this.createMenu(cell, true);
            }

            public void didPressedUserAvatar(ChatMessageCell cell, User user) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(cell);
                } else if (user != null && user.id != UserConfig.getClientUserId()) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", user.id);
                    ProfileActivity fragment = new ProfileActivity(args);
                    boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.id == user.id;
                    fragment.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(fragment);
                }
            }

            public void didPressedBotButton(ChatMessageCell cell, KeyboardButton button) {
                if (ChatActivity.this.getParentActivity() == null) {
                    return;
                }
                if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (button instanceof TL_keyboardButtonCallback) || (button instanceof TL_keyboardButtonUrl)) {
                    ChatActivity.this.chatActivityEnterView.didPressedBotButton(button, cell.getMessageObject(), cell.getMessageObject());
                }
            }

            public void didPressedCancelSendButton(ChatMessageCell cell) {
                MessageObject message = cell.getMessageObject();
                if (message.messageOwner.send_state != 0) {
                    SendMessagesHelper.getInstance().cancelSendingMessage(message);
                }
            }

            public void didLongPressed(ChatMessageCell cell) {
                ChatActivity.this.createMenu(cell, false);
            }

            public boolean canPerformActions() {
                return (ChatActivity.this.actionBar == null || ChatActivity.this.actionBar.isActionModeShowed()) ? false : true;
            }

            public void didPressedUrl(MessageObject messageObject, ClickableSpan url, boolean longPress) {
                boolean z = true;
                if (url != null) {
                    if (url instanceof URLSpanUserMention) {
                        User user = MessagesController.getInstance().getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
                        if (user != null) {
                            MessagesController.openChatOrProfileWith(user, null, ChatActivity.this, ChatActivity.attach_photo);
                        }
                    } else if (url instanceof URLSpanNoUnderline) {
                        String str = ((URLSpanNoUnderline) url).getURL();
                        if (str.startsWith("@")) {
                            MessagesController.openByUserName(str.substring(ChatActivity.attach_gallery), ChatActivity.this, ChatActivity.attach_photo);
                        } else if (str.startsWith("#")) {
                            if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                                ChatActivity.this.openSearchWithText(str);
                                return;
                            }
                            DialogsActivity fragment = new DialogsActivity(null);
                            fragment.setSearchString(str);
                            ChatActivity.this.presentFragment(fragment);
                        } else if (str.startsWith("/") && URLSpanBotCommand.enabled) {
                            ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                            boolean z2 = ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup;
                            chatActivityEnterView.setCommand(messageObject, str, longPress, z2);
                        }
                    } else {
                        String urlFinal = ((URLSpan) url).getURL();
                        if (longPress) {
                            BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                            builder.setTitle(urlFinal);
                            CharSequence[] charSequenceArr = new CharSequence[ChatActivity.attach_video];
                            charSequenceArr[ChatActivity.attach_photo] = LocaleController.getString("Open", C0691R.string.Open);
                            charSequenceArr[ChatActivity.attach_gallery] = LocaleController.getString("Copy", C0691R.string.Copy);
                            builder.setItems(charSequenceArr, new C10991(urlFinal));
                            ChatActivity.this.showDialog(builder.create());
                        } else if (url instanceof URLSpanReplacement) {
                            ChatActivity.this.showOpenUrlAlert(((URLSpanReplacement) url).getURL());
                        } else if (url instanceof URLSpan) {
                            Context parentActivity = ChatActivity.this.getParentActivity();
                            if (ChatActivity.this.inlineReturn != 0) {
                                z = false;
                            }
                            Browser.openUrl(parentActivity, urlFinal, z);
                        } else {
                            url.onClick(ChatActivity.this.fragmentView);
                        }
                    }
                }
            }

            public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivityAdapter.this.mContext);
                builder.setCustomView(new WebFrameLayout(ChatActivityAdapter.this.mContext, builder.create(), title, description, originalUrl, url, w, h));
                builder.setUseFullWidth(true);
                ChatActivity.this.showDialog(builder.create());
            }

            public void didPressedReplyMessage(ChatMessageCell cell, int id) {
                MessageObject messageObject = cell.getMessageObject();
                ChatActivity.this.scrollToMessageId(id, messageObject.getId(), true, messageObject.getDialogId() == ChatActivity.this.mergeDialogId ? ChatActivity.attach_gallery : ChatActivity.attach_photo);
            }

            public void didPressedViaBot(ChatMessageCell cell, String username) {
                if (ChatActivity.this.bottomOverlayChat != null && ChatActivity.this.bottomOverlayChat.getVisibility() == 0) {
                    return;
                }
                if ((ChatActivity.this.bottomOverlay == null || ChatActivity.this.bottomOverlay.getVisibility() != 0) && ChatActivity.this.chatActivityEnterView != null && username != null && username.length() > 0) {
                    ChatActivity.this.chatActivityEnterView.setFieldText("@" + username + " ");
                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                }
            }

            public void didPressedImage(ChatMessageCell cell) {
                long j = 0;
                MessageObject message = cell.getMessageObject();
                if (message.isSendError()) {
                    ChatActivity.this.createMenu(cell, false);
                } else if (!message.isSending()) {
                    if (message.type == ChatActivity.chat_enc_timer) {
                        ChatActivity.this.showDialog(new StickersAlert(ChatActivity.this.getParentActivity(), message.getInputStickerSet(), null, ChatActivity.this.bottomOverlayChat.getVisibility() != 0 ? ChatActivity.this.chatActivityEnterView : null));
                    } else if ((VERSION.SDK_INT >= ChatActivity.delete_chat && message.isVideo()) || message.type == ChatActivity.attach_gallery || ((message.type == 0 && !message.isWebpageDocument()) || message.isGif())) {
                        PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        PhotoViewer instance = PhotoViewer.getInstance();
                        long access$1400 = message.type != 0 ? ChatActivity.this.dialog_id : 0;
                        if (message.type != 0) {
                            j = ChatActivity.this.mergeDialogId;
                        }
                        instance.openPhoto(message, access$1400, j, ChatActivity.this);
                    } else if (message.type == ChatActivity.attach_audio) {
                        ChatActivity.this.sendSecretMessageRead(message);
                        File f = null;
                        try {
                            if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                f = new File(message.messageOwner.attachPath);
                            }
                            if (f == null || !f.exists()) {
                                f = FileLoader.getPathToMessage(message.messageOwner);
                            }
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.setDataAndType(Uri.fromFile(f), MimeTypes.VIDEO_MP4);
                            ChatActivity.this.getParentActivity().startActivityForResult(intent, 500);
                        } catch (Exception e) {
                            ChatActivity.this.alertUserOpenError(message);
                        }
                    } else if (message.type == ChatActivity.attach_document) {
                        if (AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this)) {
                            LocationActivity fragment = new LocationActivity();
                            fragment.setMessageObject(message);
                            ChatActivity.this.presentFragment(fragment);
                        }
                    } else if (message.type == 9 || message.type == 0) {
                        try {
                            AndroidUtilities.openForView(message, ChatActivity.this.getParentActivity());
                        } catch (Exception e2) {
                            ChatActivity.this.alertUserOpenError(message);
                        }
                    }
                }
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity.ChatActivityAdapter.2 */
        class C18082 implements ChatActionCellDelegate {
            C18082() {
            }

            public void didClickedImage(ChatActionCell cell) {
                MessageObject message = cell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                if (photoSize != null) {
                    PhotoViewer.getInstance().openPhoto(photoSize.location, ChatActivity.this);
                    return;
                }
                PhotoViewer.getInstance().openPhoto(message, 0, 0, ChatActivity.this);
            }

            public void didLongPressed(ChatActionCell cell) {
                ChatActivity.this.createMenu(cell, false);
            }

            public void needOpenUserProfile(int uid) {
                boolean z = true;
                Bundle args;
                if (uid < 0) {
                    args = new Bundle();
                    args.putInt("chat_id", -uid);
                    if (MessagesController.checkCanOpenChat(args, ChatActivity.this)) {
                        ChatActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                } else if (uid != UserConfig.getClientUserId()) {
                    args = new Bundle();
                    args.putInt("user_id", uid);
                    if (ChatActivity.this.currentEncryptedChat != null && uid == ChatActivity.this.currentUser.id) {
                        args.putLong("dialog_id", ChatActivity.this.dialog_id);
                    }
                    ProfileActivity fragment = new ProfileActivity(args);
                    if (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.id != uid) {
                        z = false;
                    }
                    fragment.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(fragment);
                }
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity.ChatActivityAdapter.3 */
        class C18093 implements BotHelpCellDelegate {
            C18093() {
            }

            public void didPressUrl(String url) {
                if (url.startsWith("@")) {
                    MessagesController.openByUserName(url.substring(ChatActivity.attach_gallery), ChatActivity.this, ChatActivity.attach_photo);
                } else if (url.startsWith("#")) {
                    DialogsActivity fragment = new DialogsActivity(null);
                    fragment.setSearchString(url);
                    ChatActivity.this.presentFragment(fragment);
                } else if (url.startsWith("/")) {
                    ChatActivity.this.chatActivityEnterView.setCommand(null, url, false, false);
                }
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public ChatActivityAdapter(Context context) {
            this.botInfoRow = -1;
            this.mContext = context;
            boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot;
            this.isBot = z;
        }

        public void updateRows() {
            this.rowCount = ChatActivity.attach_photo;
            if (ChatActivity.this.currentUser == null || !ChatActivity.this.currentUser.bot) {
                this.botInfoRow = -1;
            } else {
                int i = this.rowCount;
                this.rowCount = i + ChatActivity.attach_gallery;
                this.botInfoRow = i;
            }
            if (ChatActivity.this.messages.isEmpty()) {
                this.loadingUpRow = -1;
                this.loadingDownRow = -1;
                this.messagesStartRow = -1;
                this.messagesEndRow = -1;
                return;
            }
            if (ChatActivity.this.endReached[ChatActivity.attach_photo] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.endReached[ChatActivity.attach_gallery])) {
                this.loadingUpRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + ChatActivity.attach_gallery;
                this.loadingUpRow = i;
            }
            this.messagesStartRow = this.rowCount;
            this.rowCount += ChatActivity.this.messages.size();
            this.messagesEndRow = this.rowCount;
            if (ChatActivity.this.forwardEndReached[ChatActivity.attach_photo] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.forwardEndReached[ChatActivity.attach_gallery])) {
                this.loadingDownRow = -1;
                return;
            }
            i = this.rowCount;
            this.rowCount = i + ChatActivity.attach_gallery;
            this.loadingDownRow = i;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public long getItemId(int i) {
            return -1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                if (ChatActivity.this.chatMessageCellsCache.isEmpty()) {
                    view = new ChatMessageCell(this.mContext);
                } else {
                    view = (View) ChatActivity.this.chatMessageCellsCache.get(ChatActivity.attach_photo);
                    ChatActivity.this.chatMessageCellsCache.remove(ChatActivity.attach_photo);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new C18071());
                if (ChatActivity.this.currentEncryptedChat == null) {
                    chatMessageCell.setAllowAssistant(true);
                }
            } else if (viewType == ChatActivity.attach_gallery) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new C18082());
            } else if (viewType == ChatActivity.attach_video) {
                view = new ChatUnreadCell(this.mContext);
            } else if (viewType == ChatActivity.attach_audio) {
                view = new BotHelpCell(this.mContext);
                ((BotHelpCell) view).setDelegate(new C18093());
            } else if (viewType == ChatActivity.attach_document) {
                view = new ChatLoadingCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == this.botInfoRow) {
                String str;
                BotHelpCell helpView = holder.itemView;
                if (ChatActivity.this.botInfo.isEmpty()) {
                    str = null;
                } else {
                    str = ((BotInfo) ChatActivity.this.botInfo.get(Integer.valueOf(ChatActivity.this.currentUser.id))).description;
                }
                helpView.setText(str);
            } else if (position == this.loadingDownRow || position == this.loadingUpRow) {
                holder.itemView.setProgressVisible(ChatActivity.this.loadsCount > ChatActivity.attach_gallery);
            } else if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = (MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - (position - this.messagesStartRow)) - 1);
                View view = holder.itemView;
                boolean selected = false;
                boolean disableSelection = false;
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    if ((ChatActivity.this.chatActivityEnterView != null ? ChatActivity.this.chatActivityEnterView.getEditingMessageObject() : null) != message) {
                        if (!ChatActivity.this.selectedMessagesIds[message.getDialogId() == ChatActivity.this.dialog_id ? ChatActivity.attach_photo : ChatActivity.attach_gallery].containsKey(Integer.valueOf(message.getId()))) {
                            view.setBackgroundColor(ChatActivity.attach_photo);
                            disableSelection = true;
                        }
                    }
                    view.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                    selected = true;
                    disableSelection = true;
                } else {
                    view.setBackgroundColor(ChatActivity.attach_photo);
                }
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = ChatActivity.this.currentChat != null;
                    messageCell.setMessageObject(message);
                    boolean z = !disableSelection;
                    boolean z2 = disableSelection && selected;
                    messageCell.setCheckPressed(z, z2);
                    if ((view instanceof ChatMessageCell) && MediaController.getInstance().canDownloadMedia(ChatActivity.attach_video)) {
                        ((ChatMessageCell) view).downloadAudioIfNeed();
                    }
                    z2 = ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID && message.getId() == ChatActivity.this.highlightMessageId;
                    messageCell.setHighlighted(z2);
                    if (ChatActivity.this.searchContainer == null || ChatActivity.this.searchContainer.getVisibility() != 0 || MessagesSearchQuery.getLastSearchQuery() == null) {
                        messageCell.setHighlightedText(null);
                    } else {
                        messageCell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
                    }
                } else if (view instanceof ChatActionCell) {
                    ((ChatActionCell) view).setMessageObject(message);
                } else if (view instanceof ChatUnreadCell) {
                    ((ChatUnreadCell) view).setText(LocaleController.formatPluralString("NewMessages", ChatActivity.this.unread_to_load));
                }
            }
        }

        public int getItemViewType(int position) {
            if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                return ((MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - (position - this.messagesStartRow)) - 1)).contentType;
            }
            if (position == this.botInfoRow) {
                return ChatActivity.attach_audio;
            }
            return ChatActivity.attach_document;
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder.itemView instanceof ChatMessageCell) {
                ChatMessageCell messageCell = holder.itemView;
                messageCell.getViewTreeObserver().addOnPreDrawListener(new C11004(messageCell));
                boolean z = ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID && messageCell.getMessageObject().getId() == ChatActivity.this.highlightMessageId;
                messageCell.setHighlighted(z);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = ChatActivity.this.messages.indexOf(messageObject);
            if (index != -1) {
                notifyItemChanged(((this.messagesStartRow + ChatActivity.this.messages.size()) - index) - 1);
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.15 */
    class AnonymousClass15 extends LinearLayoutManager {
        AnonymousClass15(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.27 */
    class AnonymousClass27 extends LinearLayoutManager {
        AnonymousClass27(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.59 */
    class AnonymousClass59 extends EmptyPhotoViewerProvider {
        final /* synthetic */ ArrayList val$arrayList;

        AnonymousClass59(ArrayList arrayList) {
            this.val$arrayList = arrayList;
        }

        public void sendButtonPressed(int index) {
            ChatActivity.this.sendPhoto((PhotoEntry) this.val$arrayList.get(ChatActivity.attach_photo));
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.14 */
    class AnonymousClass14 extends RecyclerListView {
        AnonymousClass14(Context x0) {
            super(x0);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            ChatActivity.this.forceScrollToTop = false;
            if (ChatActivity.this.chatAdapter.isBot) {
                int childCount = getChildCount();
                for (int a = ChatActivity.attach_photo; a < childCount; a += ChatActivity.attach_gallery) {
                    View child = getChildAt(a);
                    if (child instanceof BotHelpCell) {
                        int top = ((b - t) / ChatActivity.attach_video) - (child.getMeasuredHeight() / ChatActivity.attach_video);
                        if (child.getTop() > top) {
                            child.layout(ChatActivity.attach_photo, top, r - l, child.getMeasuredHeight() + top);
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.25 */
    class AnonymousClass25 extends RecyclerListView {
        private int lastHeight;
        private int lastWidth;

        AnonymousClass25(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (!ChatActivity.this.mentionListViewIsScrolling && ChatActivity.this.mentionListViewScrollOffsetY != 0 && event.getY() < ((float) ChatActivity.this.mentionListViewScrollOffsetY)) {
                return false;
            }
            boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, ChatActivity.this.mentionListView, ChatActivity.attach_photo);
            if (super.onInterceptTouchEvent(event) || result) {
                return true;
            }
            return false;
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || event.getY() >= ((float) ChatActivity.this.mentionListViewScrollOffsetY)) {
                return super.onTouchEvent(event);
            }
            return false;
        }

        public void requestLayout() {
            if (!ChatActivity.this.mentionListViewIgnoreLayout) {
                super.requestLayout();
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int width = r - l;
            int height = b - t;
            int newPosition = -1;
            int newTop = ChatActivity.attach_photo;
            if (ChatActivity.this.mentionListView != null && ChatActivity.this.mentionListViewLastViewPosition >= 0 && width == this.lastWidth && height - this.lastHeight != 0) {
                newPosition = ChatActivity.this.mentionListViewLastViewPosition;
                newTop = ((ChatActivity.this.mentionListViewLastViewTop + height) - this.lastHeight) - getPaddingTop();
            }
            super.onLayout(changed, l, t, r, b);
            if (newPosition != -1) {
                ChatActivity.this.mentionListViewIgnoreLayout = true;
                if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                    ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(newPosition, newTop);
                } else {
                    ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(newPosition, newTop);
                }
                super.onLayout(false, l, t, r, b);
                ChatActivity.this.mentionListViewIgnoreLayout = false;
            }
            this.lastHeight = height;
            this.lastWidth = width;
            ChatActivity.this.mentionListViewUpdateLayout();
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.39 */
    class AnonymousClass39 extends RecyclerListView {
        AnonymousClass39(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, ChatActivity.this.stickersListView, ChatActivity.attach_photo);
            if (super.onInterceptTouchEvent(event) || result) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity.28 */
    class AnonymousClass28 extends ExtendedGridLayoutManager {
        private Size size;

        AnonymousClass28(Context x0, int x1) {
            super(x0, x1);
            this.size = new Size();
        }

        protected Size getSizeForItem(int i) {
            float f = 100.0f;
            if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                i += ChatActivity.attach_gallery;
            }
            BotInlineResult object = ChatActivity.this.mentionsAdapter.getItem(i);
            if (object instanceof BotInlineResult) {
                BotInlineResult inlineResult = object;
                if (inlineResult.document != null) {
                    float f2;
                    Size size = this.size;
                    if (inlineResult.document.thumb != null) {
                        f2 = (float) inlineResult.document.thumb.f34w;
                    } else {
                        f2 = 100.0f;
                    }
                    size.width = f2;
                    Size size2 = this.size;
                    if (inlineResult.document.thumb != null) {
                        f = (float) inlineResult.document.thumb.f33h;
                    }
                    size2.height = f;
                    for (int b = ChatActivity.attach_photo; b < inlineResult.document.attributes.size(); b += ChatActivity.attach_gallery) {
                        DocumentAttribute attribute = (DocumentAttribute) inlineResult.document.attributes.get(b);
                        if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                            this.size.width = (float) attribute.f29w;
                            this.size.height = (float) attribute.f28h;
                            break;
                        }
                    }
                } else {
                    this.size.width = (float) inlineResult.f25w;
                    this.size.height = (float) inlineResult.f24h;
                }
            }
            return this.size;
        }

        protected int getFlowItemCount() {
            if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                return getItemCount() - 1;
            }
            return super.getFlowItemCount();
        }
    }

    public ChatActivity(Bundle args) {
        super(args);
        this.userBlocked = false;
        this.chatMessageCellsCache = new ArrayList();
        this.actionModeViews = new ArrayList();
        this.allowContextBotPanelSecond = true;
        this.paused = true;
        this.wasPaused = false;
        this.readWhenResume = false;
        HashMap[] hashMapArr = new HashMap[attach_video];
        hashMapArr[attach_photo] = new HashMap();
        hashMapArr[attach_gallery] = new HashMap();
        this.selectedMessagesIds = hashMapArr;
        hashMapArr = new HashMap[attach_video];
        hashMapArr[attach_photo] = new HashMap();
        hashMapArr[attach_gallery] = new HashMap();
        this.selectedMessagesCanCopyIds = hashMapArr;
        this.waitingForLoad = new ArrayList();
        hashMapArr = new HashMap[attach_video];
        hashMapArr[attach_photo] = new HashMap();
        hashMapArr[attach_gallery] = new HashMap();
        this.messagesDict = hashMapArr;
        this.messagesByDays = new HashMap();
        this.messages = new ArrayList();
        this.maxMessageId = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
        this.minMessageId = new int[]{LinearLayoutManager.INVALID_OFFSET, LinearLayoutManager.INVALID_OFFSET};
        this.maxDate = new int[]{LinearLayoutManager.INVALID_OFFSET, LinearLayoutManager.INVALID_OFFSET};
        this.minDate = new int[attach_video];
        this.endReached = new boolean[attach_video];
        this.cacheEndReached = new boolean[attach_video];
        this.forwardEndReached = new boolean[]{true, true};
        this.firstLoading = true;
        this.last_message_id = attach_photo;
        this.first = true;
        this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
        this.scrollToMessagePosition = -10000;
        this.info = null;
        this.botInfo = new HashMap();
        this.chatEnterTime = 0;
        this.chatLeaveTime = 0;
        this.startVideoEdit = null;
        this.openSecretPhotoRunnable = null;
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.onItemLongClickListener = new C17961();
        this.onItemClickListener = new C17972();
    }

    public boolean onFragmentCreate() {
        Semaphore semaphore;
        int chatId = this.arguments.getInt("chat_id", attach_photo);
        int userId = this.arguments.getInt("user_id", attach_photo);
        int encId = this.arguments.getInt("enc_id", attach_photo);
        this.inlineReturn = this.arguments.getLong("inline_return", 0);
        String inlineQuery = this.arguments.getString("inline_query");
        this.startLoadFromMessageId = this.arguments.getInt("message_id", attach_photo);
        int migrated_to = this.arguments.getInt("migrated_to", attach_photo);
        this.scrollToTopOnResume = this.arguments.getBoolean("scrollToTopOnResume", false);
        if (chatId != 0) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(chatId));
            if (this.currentChat == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10813(chatId, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance().putChat(this.currentChat, true);
            }
            if (chatId > 0) {
                this.dialog_id = (long) (-chatId);
            } else {
                this.isBroadcast = true;
                this.dialog_id = AndroidUtilities.makeBroadcastId(chatId);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance().startShortPoll(chatId, false);
            }
        } else if (userId != 0) {
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(userId));
            if (this.currentUser == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10854(userId, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance().putUser(this.currentUser, true);
            }
            this.dialog_id = (long) userId;
            this.botUser = this.arguments.getString("botUser");
            if (inlineQuery != null) {
                MessagesController.getInstance().sendBotStart(this.currentUser, inlineQuery);
            }
        } else if (encId == 0) {
            return false;
        } else {
            this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(encId));
            if (this.currentEncryptedChat == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10915(encId, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                }
                if (this.currentEncryptedChat == null) {
                    return false;
                }
                MessagesController.getInstance().putEncryptedChat(this.currentEncryptedChat, true);
            }
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentEncryptedChat.user_id));
            if (this.currentUser == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10946(semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e222) {
                    FileLog.m13e("tmessages", e222);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance().putUser(this.currentUser, true);
            }
            this.dialog_id = ((long) encId) << 32;
            int[] iArr = this.maxMessageId;
            this.maxMessageId[attach_gallery] = LinearLayoutManager.INVALID_OFFSET;
            iArr[attach_photo] = LinearLayoutManager.INVALID_OFFSET;
            iArr = this.minMessageId;
            this.minMessageId[attach_gallery] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            iArr[attach_photo] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            MediaController.getInstance().startMediaObserver();
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.botKeyboardDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedPinnedMessage);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.peerSettingsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.newDraftReceived);
        super.onFragmentCreate();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            BotQuery.loadBotKeyboard(this.dialog_id);
        }
        this.loading = true;
        MessagesController.getInstance().loadPeerSettings(this.dialog_id, this.currentUser, this.currentChat);
        MessagesController.getInstance().setLastCreatedDialogId(this.dialog_id, true);
        MessagesController instance;
        long j;
        int i;
        int i2;
        boolean isChannel;
        int i3;
        if (this.startLoadFromMessageId != 0) {
            this.needSelectFromMessageId = true;
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            int i4;
            if (migrated_to != 0) {
                this.mergeDialogId = (long) migrated_to;
                instance = MessagesController.getInstance();
                j = this.mergeDialogId;
                i = AndroidUtilities.isTablet() ? bot_help : edit_done;
                i4 = this.startLoadFromMessageId;
                i2 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i3 = this.lastLoadIndex;
                this.lastLoadIndex = i3 + attach_gallery;
                instance.loadMessages(j, i, i4, true, attach_photo, i2, attach_audio, attach_photo, isChannel, i3);
            } else {
                instance = MessagesController.getInstance();
                j = this.dialog_id;
                i = AndroidUtilities.isTablet() ? bot_help : edit_done;
                i4 = this.startLoadFromMessageId;
                i2 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i3 = this.lastLoadIndex;
                this.lastLoadIndex = i3 + attach_gallery;
                instance.loadMessages(j, i, i4, true, attach_photo, i2, attach_audio, attach_photo, isChannel, i3);
            }
        } else {
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            instance = MessagesController.getInstance();
            j = this.dialog_id;
            i = AndroidUtilities.isTablet() ? bot_help : edit_done;
            i2 = this.classGuid;
            isChannel = ChatObject.isChannel(this.currentChat);
            i3 = this.lastLoadIndex;
            this.lastLoadIndex = i3 + attach_gallery;
            instance.loadMessages(j, i, attach_photo, true, attach_photo, i2, attach_video, attach_photo, isChannel, i3);
        }
        if (this.currentChat != null) {
            Semaphore semaphore2 = null;
            if (this.isBroadcast) {
                semaphore = new Semaphore(attach_photo);
            }
            MessagesController.getInstance().loadChatInfo(this.currentChat.id, semaphore2, ChatObject.isChannel(this.currentChat));
            if (this.isBroadcast && semaphore2 != null) {
                try {
                    semaphore2.acquire();
                } catch (Throwable e2222) {
                    FileLog.m13e("tmessages", e2222);
                }
            }
        }
        if (userId != 0 && this.currentUser.bot) {
            BotQuery.loadBotInfo(userId, true, this.classGuid);
        } else if (this.info instanceof TL_chatFull) {
            for (int a = attach_photo; a < this.info.participants.participants.size(); a += attach_gallery) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (user != null && user.bot) {
                    BotQuery.loadBotInfo(user.id, true, this.classGuid);
                }
            }
        }
        if (this.currentUser != null) {
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.currentUser.id));
        }
        if (AndroidUtilities.isTablet()) {
            NotificationCenter instance2 = NotificationCenter.getInstance();
            int i5 = NotificationCenter.openedChatChanged;
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = Long.valueOf(this.dialog_id);
            objArr[attach_gallery] = Boolean.valueOf(false);
            instance2.postNotificationName(i5, objArr);
        }
        if (!(this.currentEncryptedChat == null || AndroidUtilities.getMyLayerVersion(this.currentEncryptedChat.layer) == 46)) {
            SecretChatHelper.getInstance().sendNotifyLayerMessage(this.currentEncryptedChat, null);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        MessagesController.getInstance().setLastCreatedDialogId(this.dialog_id, false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botKeyboardDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedPinnedMessage);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.peerSettingsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newDraftReceived);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.openedChatChanged;
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = Long.valueOf(this.dialog_id);
            objArr[attach_gallery] = Boolean.valueOf(true);
            instance.postNotificationName(i, objArr);
        }
        if (this.currentEncryptedChat != null) {
            MediaController.getInstance().stopMediaObserver();
            try {
                if (VERSION.SDK_INT >= 23) {
                    getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        if (this.currentUser != null) {
            MessagesController.getInstance().cancelLoadFullUser(this.currentUser.id);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.stickersAdapter != null) {
            this.stickersAdapter.onDestroy();
        }
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onDestroy();
        }
        AndroidUtilities.unlockOrientation(getParentActivity());
        if (ChatObject.isChannel(this.currentChat)) {
            MessagesController.getInstance().startShortPoll(this.currentChat.id, true);
        }
    }

    public View createView(Context context) {
        int a;
        View textView;
        boolean z;
        boolean z2;
        MessageObject messageObject;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (a = attach_photo; a < 8; a += attach_gallery) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        for (a = attach_gallery; a >= 0; a--) {
            this.selectedMessagesIds[a].clear();
            this.selectedMessagesCanCopyIds[a].clear();
        }
        this.cantDeleteMessagesCount = attach_photo;
        this.hasOwnBackground = true;
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onDestroy();
            this.chatAttachAlert = null;
        }
        Theme.loadRecources(context);
        Theme.loadChatResources(context);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new C18057());
        this.avatarContainer = new ChatAvatarContainer(context, this, this.currentEncryptedChat != null);
        this.actionBar.addView(this.avatarContainer, attach_photo, LayoutHelper.createFrame(-2, GroundOverlayOptions.NO_DIMENSION, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        if (!(this.currentChat == null || ChatObject.isChannel(this.currentChat))) {
            int count = this.currentChat.participants_count;
            if (this.info != null) {
                count = this.info.participants.participants.size();
            }
            if (count == 0 || this.currentChat.deactivated || this.currentChat.left || (this.currentChat instanceof TL_chatForbidden) || (this.info != null && (this.info.participants instanceof TL_chatParticipantsForbidden))) {
                this.avatarContainer.setEnabled(false);
            }
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            this.searchItem = menu.addItem((int) attach_photo, (int) C0691R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18068());
            this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0691R.string.Search));
            this.searchItem.setVisibility(8);
        }
        this.headerItem = menu.addItem((int) attach_photo, (int) C0691R.drawable.ic_ab_other);
        if (this.searchItem != null) {
            this.headerItem.addSubItem(search, LocaleController.getString("Search", C0691R.string.Search), attach_photo);
        }
        if (ChatObject.isChannel(this.currentChat) && !this.currentChat.creator && (!this.currentChat.megagroup || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
            this.headerItem.addSubItem(report, LocaleController.getString("ReportChat", C0691R.string.ReportChat), attach_photo);
        }
        if (this.currentUser != null) {
            this.addContactItem = this.headerItem.addSubItem(share_contact, TtmlNode.ANONYMOUS_REGION_ID, attach_photo);
        }
        if (this.currentEncryptedChat != null) {
            this.timeItem2 = this.headerItem.addSubItem(chat_enc_timer, LocaleController.getString("SetTimer", C0691R.string.SetTimer), attach_photo);
        }
        if (!ChatObject.isChannel(this.currentChat)) {
            this.headerItem.addSubItem(clear_history, LocaleController.getString("ClearHistory", C0691R.string.ClearHistory), attach_photo);
            if (this.currentChat == null || this.isBroadcast) {
                this.headerItem.addSubItem(delete_chat, LocaleController.getString("DeleteChatUser", C0691R.string.DeleteChatUser), attach_photo);
            } else {
                this.headerItem.addSubItem(delete_chat, LocaleController.getString("DeleteAndExit", C0691R.string.DeleteAndExit), attach_photo);
            }
        }
        this.muteItem = this.headerItem.addSubItem(mute, null, attach_photo);
        if (this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            this.headerItem.addSubItem(bot_settings, LocaleController.getString("BotSettings", C0691R.string.BotSettings), attach_photo);
            this.headerItem.addSubItem(bot_help, LocaleController.getString("BotHelp", C0691R.string.BotHelp), attach_photo);
            updateBotButtons();
        }
        updateTitle();
        this.avatarContainer.updateOnlineCount();
        this.avatarContainer.updateSubtitle();
        updateTitleIcons();
        this.attachItem = menu.addItem((int) chat_menu_attach, (int) C0691R.drawable.ic_ab_other).setOverrideMenuClick(true).setAllowCloseAnimation(false);
        this.attachItem.setVisibility(8);
        this.menuItem = menu.addItem((int) chat_menu_attach, (int) C0691R.drawable.ic_ab_attach).setAllowCloseAnimation(false);
        this.menuItem.setBackgroundDrawable(null);
        this.actionModeViews.clear();
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(mute);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear((int) attach_photo, -1, (float) TouchHelperCallback.ALPHA_FULL, 65, (int) attach_photo, (int) attach_photo, (int) attach_photo));
        this.selectedMessagesCountTextView.setOnTouchListener(new C10989());
        this.actionModeTitleContainer = new AnonymousClass10(context);
        actionMode.addView(this.actionModeTitleContainer, LayoutHelper.createLinear((int) attach_photo, -1, (float) TouchHelperCallback.ALPHA_FULL, 65, (int) attach_photo, (int) attach_photo, (int) attach_photo));
        this.actionModeTitleContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.actionModeTitleContainer.setVisibility(8);
        this.actionModeTextView = new SimpleTextView(context);
        this.actionModeTextView.setTextSize(mute);
        this.actionModeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.actionModeTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.actionModeTextView.setText(LocaleController.getString("Edit", C0691R.string.Edit));
        this.actionModeTitleContainer.addView(this.actionModeTextView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.actionModeSubTextView = new SimpleTextView(context);
        this.actionModeSubTextView.setGravity(attach_audio);
        this.actionModeSubTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.actionModeTitleContainer.addView(this.actionModeSubTextView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        if (this.currentEncryptedChat == null) {
            if (!this.isBroadcast) {
                this.actionModeViews.add(actionMode.addItem(reply, C0691R.drawable.ic_ab_reply, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            }
            this.actionModeViews.add(actionMode.addItem(copy, C0691R.drawable.ic_ab_fwd_copy, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItem(forward, C0691R.drawable.ic_ab_fwd_forward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItem(delete, C0691R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            ArrayList arrayList = this.actionModeViews;
            ActionBarMenuItem addItem = actionMode.addItem(edit_done, C0691R.drawable.check_blue, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f));
            this.editDoneItem = addItem;
            arrayList.add(addItem);
            this.editDoneItem.setVisibility(8);
            this.editDoneItemProgress = new ContextProgressView(context);
            this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
            this.editDoneItemProgress.setVisibility(attach_document);
        } else {
            this.actionModeViews.add(actionMode.addItem(reply, C0691R.drawable.ic_ab_reply, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItem(copy, C0691R.drawable.ic_ab_fwd_copy, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItem(delete, C0691R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        }
        actionMode.getItem(copy).setVisibility(this.selectedMessagesCanCopyIds[attach_photo].size() + this.selectedMessagesCanCopyIds[attach_gallery].size() != 0 ? attach_photo : 8);
        actionMode.getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? attach_photo : 8);
        checkActionBarMenu();
        this.fragmentView = new AnonymousClass12(context);
        SizeNotifierFrameLayout contentView = (SizeNotifierFrameLayout) this.fragmentView;
        contentView.setBackgroundImage(ApplicationLoader.getCachedWallpaper());
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setVisibility(attach_document);
        contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, share_contact));
        this.emptyViewContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (this.currentEncryptedChat == null) {
            textView = new TextView(context);
            if (this.currentUser == null || this.currentUser.id == 777000 || this.currentUser.id == 429000 || !(this.currentUser.id / id_chat_compose_panel == 333 || this.currentUser.id % id_chat_compose_panel == 0)) {
                textView.setText(LocaleController.getString("NoMessages", C0691R.string.NoMessages));
            } else {
                textView.setText(LocaleController.getString("GotAQuestion", C0691R.string.GotAQuestion));
            }
            textView.setTextSize(attach_gallery, 14.0f);
            textView.setGravity(share_contact);
            textView.setTextColor(Theme.CHAT_EMPTY_VIEW_TEXT_COLOR);
            textView.setBackgroundResource(C0691R.drawable.system);
            textView.getBackground().setColorFilter(Theme.colorFilter);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(3.0f));
            this.emptyViewContainer.addView(textView, new LayoutParams(-2, -2, share_contact));
        } else {
            textView = new LinearLayout(context);
            textView.setBackgroundResource(C0691R.drawable.system);
            textView.getBackground().setColorFilter(Theme.colorFilter);
            textView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            textView.setOrientation(attach_gallery);
            this.emptyViewContainer.addView(textView, new LayoutParams(-2, -2, share_contact));
            this.secretViewStatusTextView = new TextView(context);
            this.secretViewStatusTextView.setTextSize(attach_gallery, 15.0f);
            this.secretViewStatusTextView.setTextColor(Theme.SECRET_CHAT_INFO_TEXT_COLOR);
            this.secretViewStatusTextView.setGravity(attach_gallery);
            this.secretViewStatusTextView.setMaxWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_AZURE));
            TextView textView2;
            Object[] objArr;
            if (this.currentEncryptedChat.admin_id == UserConfig.getClientUserId()) {
                textView2 = this.secretViewStatusTextView;
                objArr = new Object[attach_gallery];
                objArr[attach_photo] = UserObject.getFirstName(this.currentUser);
                textView2.setText(LocaleController.formatString("EncryptedPlaceholderTitleOutgoing", C0691R.string.EncryptedPlaceholderTitleOutgoing, objArr));
            } else {
                textView2 = this.secretViewStatusTextView;
                objArr = new Object[attach_gallery];
                objArr[attach_photo] = UserObject.getFirstName(this.currentUser);
                textView2.setText(LocaleController.formatString("EncryptedPlaceholderTitleIncoming", C0691R.string.EncryptedPlaceholderTitleIncoming, objArr));
            }
            textView.addView(this.secretViewStatusTextView, LayoutHelper.createLinear(-2, -2, 49));
            textView = new TextView(context);
            textView.setText(LocaleController.getString("EncryptedDescriptionTitle", C0691R.string.EncryptedDescriptionTitle));
            textView.setTextSize(attach_gallery, 15.0f);
            textView.setTextColor(Theme.SECRET_CHAT_INFO_TEXT_COLOR);
            textView.setGravity(attach_gallery);
            textView.setMaxWidth(AndroidUtilities.dp(260.0f));
            textView.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? attach_contact : attach_audio) | 48, (int) attach_photo, 8, (int) attach_photo, (int) attach_photo));
            for (a = attach_photo; a < attach_document; a += attach_gallery) {
                textView = new LinearLayout(context);
                textView.setOrientation(attach_photo);
                textView.addView(textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? attach_contact : attach_audio, (int) attach_photo, 8, (int) attach_photo, (int) attach_photo));
                textView = new ImageView(context);
                textView.setImageResource(C0691R.drawable.ic_lock_white);
                textView = new TextView(context);
                textView.setTextSize(attach_gallery, 15.0f);
                textView.setTextColor(Theme.SECRET_CHAT_INFO_TEXT_COLOR);
                textView.setGravity((LocaleController.isRTL ? attach_contact : attach_audio) | delete_chat);
                textView.setMaxWidth(AndroidUtilities.dp(260.0f));
                switch (a) {
                    case attach_photo /*0*/:
                        textView.setText(LocaleController.getString("EncryptedDescription1", C0691R.string.EncryptedDescription1));
                        break;
                    case attach_gallery /*1*/:
                        textView.setText(LocaleController.getString("EncryptedDescription2", C0691R.string.EncryptedDescription2));
                        break;
                    case attach_video /*2*/:
                        textView.setText(LocaleController.getString("EncryptedDescription3", C0691R.string.EncryptedDescription3));
                        break;
                    case attach_audio /*3*/:
                        textView.setText(LocaleController.getString("EncryptedDescription4", C0691R.string.EncryptedDescription4));
                        break;
                }
                if (LocaleController.isRTL) {
                    textView.addView(textView, LayoutHelper.createLinear(-2, -2));
                    textView.addView(textView, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else {
                    textView.addView(textView, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                    textView.addView(textView, LayoutHelper.createLinear(-2, -2));
                }
            }
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        this.chatListView = new AnonymousClass14(context);
        this.chatListView.setTag(Integer.valueOf(attach_gallery));
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        Adapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(attach_photo, AndroidUtilities.dp(4.0f), attach_photo, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new AnonymousClass15(context);
        this.chatLayoutManager.setOrientation(attach_gallery);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.chatListView.setOnItemClickListener(this.onItemClickListener);
        this.chatListView.setOnScrollListener(new OnScrollListener() {
            private final int scrollValue;
            private float totalDy;

            {
                this.totalDy = 0.0f;
                this.scrollValue = AndroidUtilities.dp(100.0f);
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == ChatActivity.attach_gallery && ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    ChatActivity.this.updateVisibleRows();
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatActivity.this.checkScrollForLoad(true);
                int firstVisibleItem = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? ChatActivity.attach_photo : Math.abs(ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + ChatActivity.attach_gallery;
                if (visibleItemCount > 0) {
                    if (firstVisibleItem + visibleItemCount == ChatActivity.this.chatAdapter.getItemCount() && ChatActivity.this.forwardEndReached[ChatActivity.attach_photo]) {
                        ChatActivity.this.showPagedownButton(false, true);
                    } else if (dy > 0) {
                        if (ChatActivity.this.pagedownButton.getTag() == null) {
                            this.totalDy += (float) dy;
                            if (this.totalDy > ((float) this.scrollValue)) {
                                this.totalDy = 0.0f;
                                ChatActivity.this.showPagedownButton(true, true);
                                ChatActivity.this.pagedownButtonShowedByScroll = true;
                            }
                        }
                    } else if (ChatActivity.this.pagedownButtonShowedByScroll && ChatActivity.this.pagedownButton.getTag() != null) {
                        this.totalDy += (float) dy;
                        if (this.totalDy < ((float) (-this.scrollValue))) {
                            ChatActivity.this.showPagedownButton(false, true);
                            this.totalDy = 0.0f;
                        }
                    }
                }
                ChatActivity.this.updateMessagesVisisblePart();
            }
        });
        this.chatListView.setOnTouchListener(new OnTouchListener() {

            /* renamed from: org.telegram.ui.ChatActivity.17.1 */
            class C10741 implements Runnable {
                C10741() {
                }

                public void run() {
                    ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                }
            }

            /* renamed from: org.telegram.ui.ChatActivity.17.2 */
            class C10752 implements Runnable {
                C10752() {
                }

                public void run() {
                    ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                    ChatActivity.this.chatListView.setLongClickable(true);
                }
            }

            public boolean onTouch(View v, MotionEvent event) {
                if (ChatActivity.this.openSecretPhotoRunnable != null || SecretPhotoViewer.getInstance().isVisible()) {
                    if (event.getAction() == ChatActivity.attach_gallery || event.getAction() == ChatActivity.attach_audio || event.getAction() == ChatActivity.attach_location) {
                        AndroidUtilities.runOnUIThread(new C10741(), 150);
                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                            ChatActivity.this.openSecretPhotoRunnable = null;
                            try {
                                Toast.makeText(v.getContext(), LocaleController.getString("PhotoTip", C0691R.string.PhotoTip), ChatActivity.attach_photo).show();
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                        } else if (SecretPhotoViewer.getInstance().isVisible()) {
                            AndroidUtilities.runOnUIThread(new C10752());
                            SecretPhotoViewer.getInstance().closePhoto();
                        }
                    } else if (event.getAction() != 0) {
                        if (SecretPhotoViewer.getInstance().isVisible()) {
                            return true;
                        }
                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                            if (event.getAction() != ChatActivity.attach_video) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                                ChatActivity.this.openSecretPhotoRunnable = null;
                            } else if (Math.hypot((double) (ChatActivity.this.startX - event.getX()), (double) (ChatActivity.this.startY - event.getY())) > ((double) AndroidUtilities.dp(5.0f))) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                                ChatActivity.this.openSecretPhotoRunnable = null;
                            }
                            ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                            ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                            ChatActivity.this.chatListView.setLongClickable(true);
                        }
                    }
                }
                return false;
            }
        });
        this.chatListView.setOnInterceptTouchListener(new OnInterceptTouchListener() {

            /* renamed from: org.telegram.ui.ChatActivity.18.1 */
            class C10761 implements Runnable {
                final /* synthetic */ ChatMessageCell val$cell;
                final /* synthetic */ MessageObject val$messageObject;

                C10761(MessageObject messageObject, ChatMessageCell chatMessageCell) {
                    this.val$messageObject = messageObject;
                    this.val$cell = chatMessageCell;
                }

                public void run() {
                    if (ChatActivity.this.openSecretPhotoRunnable != null) {
                        ChatActivity.this.chatListView.requestDisallowInterceptTouchEvent(true);
                        ChatActivity.this.chatListView.setOnItemLongClickListener(null);
                        ChatActivity.this.chatListView.setLongClickable(false);
                        ChatActivity.this.openSecretPhotoRunnable = null;
                        if (ChatActivity.this.sendSecretMessageRead(this.val$messageObject)) {
                            this.val$cell.invalidate();
                        }
                        SecretPhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        SecretPhotoViewer.getInstance().openPhoto(this.val$messageObject);
                    }
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (ChatActivity.this.chatActivityEnterView != null && ChatActivity.this.chatActivityEnterView.isEditingMessage()) {
                    return true;
                }
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    return false;
                }
                if (event.getAction() == 0) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int count = ChatActivity.this.chatListView.getChildCount();
                    int a = ChatActivity.attach_photo;
                    while (a < count) {
                        View view = ChatActivity.this.chatListView.getChildAt(a);
                        int top = view.getTop();
                        int bottom = view.getBottom();
                        if (top > y || bottom < y) {
                            a += ChatActivity.attach_gallery;
                        } else if (view instanceof ChatMessageCell) {
                            ChatMessageCell cell = (ChatMessageCell) view;
                            MessageObject messageObject = cell.getMessageObject();
                            if (messageObject != null && !messageObject.isSending() && messageObject.isSecretPhoto() && cell.getPhotoImage().isInsideImage((float) x, (float) (y - top)) && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                                ChatActivity.this.startX = (float) x;
                                ChatActivity.this.startY = (float) y;
                                ChatActivity.this.chatListView.setOnItemClickListener(null);
                                ChatActivity.this.openSecretPhotoRunnable = new C10761(messageObject, cell);
                                AndroidUtilities.runOnUIThread(ChatActivity.this.openSecretPhotoRunnable, 100);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(attach_document);
        contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        textView = new View(context);
        textView.setBackgroundResource(C0691R.drawable.system_loader);
        textView.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(textView, LayoutHelper.createFrame(36, 36, share_contact));
        textView = new ProgressBar(context);
        try {
            textView.setIndeterminateDrawable(context.getResources().getDrawable(C0691R.drawable.loading_animation));
        } catch (Exception e) {
        }
        textView.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(textView, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.progressView.addView(textView, LayoutHelper.createFrame(32, 32, share_contact));
        if (ChatObject.isChannel(this.currentChat)) {
            this.pinnedMessageView = new FrameLayout(context);
            this.pinnedMessageView.setTag(Integer.valueOf(attach_gallery));
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
            this.pinnedMessageView.setBackgroundResource(C0691R.drawable.blockpanel);
            contentView.addView(this.pinnedMessageView, LayoutHelper.createFrame(-1, 50, 51));
            this.pinnedMessageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.info.pinned_msg_id, ChatActivity.attach_photo, true, ChatActivity.attach_photo);
                }
            });
            textView = new View(context);
            textView.setBackgroundColor(-9658414);
            this.pinnedMessageView.addView(textView, LayoutHelper.createFrame(attach_video, 32.0f, 51, 8.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageNameTextView = new SimpleTextView(context);
            this.pinnedMessageNameTextView.setTextSize(chat_menu_attach);
            this.pinnedMessageNameTextView.setTextColor(Theme.PINNED_PANEL_NAME_TEXT_COLOR);
            this.pinnedMessageNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.pinnedMessageView.addView(this.pinnedMessageNameTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(18.0f), 51, 18.0f, 7.3f, 52.0f, 0.0f));
            this.pinnedMessageTextView = new SimpleTextView(context);
            this.pinnedMessageTextView.setTextSize(chat_menu_attach);
            this.pinnedMessageTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            this.pinnedMessageView.addView(this.pinnedMessageTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(18.0f), 51, 18.0f, 25.3f, 52.0f, 0.0f));
            ImageView closePinned = new ImageView(context);
            closePinned.setImageResource(C0691R.drawable.miniplayer_close);
            closePinned.setScaleType(ScaleType.CENTER);
            this.pinnedMessageView.addView(closePinned, LayoutHelper.createFrame(48, 48, 53));
            closePinned.setOnClickListener(new View.OnClickListener() {

                /* renamed from: org.telegram.ui.ChatActivity.20.1 */
                class C10771 implements OnClickListener {
                    C10771() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, ChatActivity.attach_photo, false);
                    }
                }

                public void onClick(View v) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (ChatActivity.this.currentChat.creator || ChatActivity.this.currentChat.editor) {
                            Builder builder = new Builder(ChatActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("UnpinMessageAlert", C0691R.string.UnpinMessageAlert));
                            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10771());
                            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                            ChatActivity.this.showDialog(builder.create());
                            return;
                        }
                        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", ChatActivity.attach_photo).edit().putInt("pin_" + ChatActivity.this.dialog_id, ChatActivity.this.info.pinned_msg_id).commit();
                        ChatActivity.this.updatePinnedMessageView(true);
                    }
                }
            });
        }
        this.reportSpamView = new LinearLayout(context);
        this.reportSpamView.setTag(Integer.valueOf(attach_gallery));
        this.reportSpamView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
        this.reportSpamView.setVisibility(8);
        this.reportSpamView.setBackgroundResource(C0691R.drawable.blockpanel);
        contentView.addView(this.reportSpamView, LayoutHelper.createFrame(-1, 50, 51));
        this.addToContactsButton = new TextView(context);
        this.addToContactsButton.setTextColor(Theme.CHAT_ADD_CONTACT_TEXT_COLOR);
        this.addToContactsButton.setVisibility(8);
        this.addToContactsButton.setTextSize(attach_gallery, 14.0f);
        this.addToContactsButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addToContactsButton.setSingleLine(true);
        this.addToContactsButton.setMaxLines(attach_gallery);
        this.addToContactsButton.setPadding(AndroidUtilities.dp(4.0f), attach_photo, AndroidUtilities.dp(4.0f), attach_photo);
        this.addToContactsButton.setGravity(share_contact);
        this.addToContactsButton.setText(LocaleController.getString("AddContactChat", C0691R.string.AddContactChat));
        this.reportSpamView.addView(this.addToContactsButton, LayoutHelper.createLinear(-1, -1, 0.5f, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)));
        this.addToContactsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("user_id", ChatActivity.this.currentUser.id);
                args.putBoolean("addContact", true);
                ChatActivity.this.presentFragment(new ContactAddActivity(args));
            }
        });
        this.reportSpamContainer = new FrameLayout(context);
        this.reportSpamView.addView(this.reportSpamContainer, LayoutHelper.createLinear(-1, -1, TouchHelperCallback.ALPHA_FULL, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)));
        this.reportSpamButton = new TextView(context);
        this.reportSpamButton.setTextColor(Theme.CHAT_REPORT_SPAM_TEXT_COLOR);
        this.reportSpamButton.setTextSize(attach_gallery, 14.0f);
        this.reportSpamButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.reportSpamButton.setSingleLine(true);
        this.reportSpamButton.setMaxLines(attach_gallery);
        if (this.currentChat != null) {
            this.reportSpamButton.setText(LocaleController.getString("ReportSpamAndLeave", C0691R.string.ReportSpamAndLeave));
        } else {
            this.reportSpamButton.setText(LocaleController.getString("ReportSpam", C0691R.string.ReportSpam));
        }
        this.reportSpamButton.setGravity(share_contact);
        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), attach_photo, AndroidUtilities.dp(50.0f), attach_photo);
        this.reportSpamContainer.addView(this.reportSpamButton, LayoutHelper.createFrame(-1, -1, 51));
        this.reportSpamButton.setOnClickListener(new View.OnClickListener() {

            /* renamed from: org.telegram.ui.ChatActivity.22.1 */
            class C10781 implements OnClickListener {
                C10781() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (ChatActivity.this.currentUser != null) {
                        MessagesController.getInstance().blockUser(ChatActivity.this.currentUser.id);
                    }
                    MessagesController.getInstance().reportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
                    ChatActivity.this.updateSpamView();
                    if (ChatActivity.this.currentChat == null) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else {
                        MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                    }
                    ChatActivity.this.finishFragment();
                }
            }

            public void onClick(View v) {
                if (ChatActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                    if (ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertChannel", C0691R.string.ReportSpamAlertChannel));
                    } else if (ChatActivity.this.currentChat != null) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertGroup", C0691R.string.ReportSpamAlertGroup));
                    } else {
                        builder.setMessage(LocaleController.getString("ReportSpamAlert", C0691R.string.ReportSpamAlert));
                    }
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10781());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    ChatActivity.this.showDialog(builder.create());
                }
            }
        });
        ImageView closeReportSpam = new ImageView(context);
        closeReportSpam.setImageResource(C0691R.drawable.miniplayer_close);
        closeReportSpam.setScaleType(ScaleType.CENTER);
        this.reportSpamContainer.addView(closeReportSpam, LayoutHelper.createFrame(48, 48, 53));
        closeReportSpam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MessagesController.getInstance().hideReportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
                ChatActivity.this.updateSpamView();
            }
        });
        this.alertView = new FrameLayout(context);
        this.alertView.setTag(Integer.valueOf(attach_gallery));
        this.alertView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
        this.alertView.setVisibility(8);
        this.alertView.setBackgroundResource(C0691R.drawable.blockpanel);
        contentView.addView(this.alertView, LayoutHelper.createFrame(-1, 50, 51));
        this.alertNameTextView = new TextView(context);
        this.alertNameTextView.setTextSize(attach_gallery, 14.0f);
        this.alertNameTextView.setTextColor(Theme.ALERT_PANEL_NAME_TEXT_COLOR);
        this.alertNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.alertNameTextView.setSingleLine(true);
        this.alertNameTextView.setEllipsize(TruncateAt.END);
        this.alertNameTextView.setMaxLines(attach_gallery);
        this.alertView.addView(this.alertNameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 8.0f, 5.0f, 8.0f, 0.0f));
        this.alertTextView = new TextView(context);
        this.alertTextView.setTextSize(attach_gallery, 14.0f);
        this.alertTextView.setTextColor(Theme.ALERT_PANEL_MESSAGE_TEXT_COLOR);
        this.alertTextView.setSingleLine(true);
        this.alertTextView.setEllipsize(TruncateAt.END);
        this.alertTextView.setMaxLines(attach_gallery);
        this.alertView.addView(this.alertTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 8.0f, 23.0f, 8.0f, 0.0f));
        if (!this.isBroadcast) {
            this.mentionContainer = new AnonymousClass24(context);
            this.mentionContainer.setBackgroundResource(C0691R.drawable.compose_panel);
            this.mentionContainer.setVisibility(8);
            this.mentionContainer.setWillNotDraw(false);
            contentView.addView(this.mentionContainer, LayoutHelper.createFrame(-1, 110, 83));
            this.mentionListView = new AnonymousClass25(context);
            this.mentionListView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return StickerPreviewViewer.getInstance().onTouch(event, ChatActivity.this.mentionListView, ChatActivity.attach_photo, ChatActivity.this.mentionsOnItemClickListener);
                }
            });
            this.mentionListView.setTag(Integer.valueOf(attach_video));
            this.mentionLayoutManager = new AnonymousClass27(context);
            this.mentionLayoutManager.setOrientation(attach_gallery);
            this.mentionGridLayoutManager = new AnonymousClass28(context, 100);
            this.mentionGridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if (ChatActivity.this.mentionsAdapter.getItem(position) instanceof TL_inlineBotSwitchPM) {
                        return 100;
                    }
                    if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                        position--;
                    }
                    return ChatActivity.this.mentionGridLayoutManager.getSpanSizeForItem(position);
                }
            });
            this.mentionListView.addItemDecoration(new ItemDecoration() {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    int i = ChatActivity.attach_photo;
                    outRect.left = ChatActivity.attach_photo;
                    outRect.right = ChatActivity.attach_photo;
                    outRect.top = ChatActivity.attach_photo;
                    outRect.bottom = ChatActivity.attach_photo;
                    if (parent.getLayoutManager() == ChatActivity.this.mentionGridLayoutManager) {
                        int position = parent.getChildAdapterPosition(view);
                        if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                            outRect.top = AndroidUtilities.dp(2.0f);
                        } else if (position != 0) {
                            position--;
                            if (!ChatActivity.this.mentionGridLayoutManager.isFirstRow(position)) {
                                outRect.top = AndroidUtilities.dp(2.0f);
                            }
                        } else {
                            return;
                        }
                        if (!ChatActivity.this.mentionGridLayoutManager.isLastInRow(position)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        outRect.right = i;
                    }
                }
            });
            this.mentionListView.setItemAnimator(null);
            this.mentionListView.setLayoutAnimation(null);
            this.mentionListView.setClipToPadding(false);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setOverScrollMode(attach_video);
            this.mentionContainer.addView(this.mentionListView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
            recyclerListView = this.mentionListView;
            chatActivityAdapter = new MentionsAdapter(context, false, this.dialog_id, new MentionsAdapterDelegate() {

                /* renamed from: org.telegram.ui.ChatActivity.31.1 */
                class C17981 extends AnimatorListenerAdapterProxy {
                    C17981() {
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                }

                /* renamed from: org.telegram.ui.ChatActivity.31.2 */
                class C17992 extends AnimatorListenerAdapterProxy {
                    C17992() {
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionContainer.setVisibility(8);
                            ChatActivity.this.mentionContainer.setTag(null);
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                }

                public void needChangePanelVisibility(boolean show) {
                    if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                        ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionGridLayoutManager);
                    } else {
                        ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionLayoutManager);
                    }
                    if (show) {
                        if (ChatActivity.this.mentionListAnimation != null) {
                            ChatActivity.this.mentionListAnimation.cancel();
                            ChatActivity.this.mentionListAnimation = null;
                        }
                        if (ChatActivity.this.mentionContainer.getVisibility() == 0) {
                            ChatActivity.this.mentionContainer.setAlpha(TouchHelperCallback.ALPHA_FULL);
                            return;
                        }
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                            ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(ChatActivity.attach_photo, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                        } else {
                            ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(ChatActivity.attach_photo, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                        }
                        if (ChatActivity.this.allowStickersPanel && (!ChatActivity.this.mentionsAdapter.isBotContext() || ChatActivity.this.allowContextBotPanel || ChatActivity.this.allowContextBotPanelSecond)) {
                            if (ChatActivity.this.currentEncryptedChat != null && ChatActivity.this.mentionsAdapter.isBotContext()) {
                                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ChatActivity.attach_photo);
                                if (!preferences.getBoolean("secretbot", false)) {
                                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                                    builder.setMessage(LocaleController.getString("SecretChatContextBotAlert", C0691R.string.SecretChatContextBotAlert));
                                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                                    ChatActivity.this.showDialog(builder.create());
                                    preferences.edit().putBoolean("secretbot", true).commit();
                                }
                            }
                            ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_photo);
                            ChatActivity.this.mentionContainer.setTag(null);
                            ChatActivity.this.mentionListAnimation = new AnimatorSet();
                            AnimatorSet access$6800 = ChatActivity.this.mentionListAnimation;
                            Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                            animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                            access$6800.playTogether(animatorArr);
                            ChatActivity.this.mentionListAnimation.addListener(new C17981());
                            ChatActivity.this.mentionListAnimation.setDuration(200);
                            ChatActivity.this.mentionListAnimation.start();
                            return;
                        }
                        ChatActivity.this.mentionContainer.setAlpha(TouchHelperCallback.ALPHA_FULL);
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_document);
                        return;
                    }
                    if (ChatActivity.this.mentionListAnimation != null) {
                        ChatActivity.this.mentionListAnimation.cancel();
                        ChatActivity.this.mentionListAnimation = null;
                    }
                    if (ChatActivity.this.mentionContainer.getVisibility() == 8) {
                        return;
                    }
                    if (ChatActivity.this.allowStickersPanel) {
                        ChatActivity.this.mentionListAnimation = new AnimatorSet();
                        access$6800 = ChatActivity.this.mentionListAnimation;
                        animatorArr = new Animator[ChatActivity.attach_gallery];
                        float[] fArr = new float[ChatActivity.attach_gallery];
                        fArr[ChatActivity.attach_photo] = 0.0f;
                        animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", fArr);
                        access$6800.playTogether(animatorArr);
                        ChatActivity.this.mentionListAnimation.addListener(new C17992());
                        ChatActivity.this.mentionListAnimation.setDuration(200);
                        ChatActivity.this.mentionListAnimation.start();
                        return;
                    }
                    ChatActivity.this.mentionContainer.setTag(null);
                    ChatActivity.this.mentionContainer.setVisibility(8);
                }

                public void onContextSearch(boolean searching) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.setCaption(ChatActivity.this.mentionsAdapter.getBotCaption());
                        ChatActivity.this.chatActivityEnterView.showContextProgress(searching);
                    }
                }

                public void onContextClick(BotInlineResult result) {
                    if (ChatActivity.this.getParentActivity() != null && result.content_url != null) {
                        if (result.type.equals(MimeTypes.BASE_TYPE_VIDEO) || result.type.equals("web_player_video")) {
                            BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                            builder.setCustomView(new WebFrameLayout(ChatActivity.this.getParentActivity(), builder.create(), result.title != null ? result.title : TtmlNode.ANONYMOUS_REGION_ID, result.description, result.content_url, result.content_url, result.f25w, result.f24h));
                            builder.setUseFullWidth(true);
                            ChatActivity.this.showDialog(builder.create());
                            return;
                        }
                        Browser.openUrl(ChatActivity.this.getParentActivity(), result.content_url);
                    }
                }
            });
            this.mentionsAdapter = chatActivityAdapter;
            recyclerListView.setAdapter(chatActivityAdapter);
            if (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup)) {
                this.mentionsAdapter.setBotInfo(this.botInfo);
            }
            this.mentionsAdapter.setParentFragment(this);
            this.mentionsAdapter.setChatInfo(this.info);
            this.mentionsAdapter.setNeedUsernames(this.currentChat != null);
            MentionsAdapter mentionsAdapter = this.mentionsAdapter;
            z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
            mentionsAdapter.setNeedBotContext(z);
            this.mentionsAdapter.setBotsCount(this.currentChat != null ? this.botsCount : attach_gallery);
            recyclerListView = this.mentionListView;
            OnItemClickListener anonymousClass32 = new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    TLObject object = ChatActivity.this.mentionsAdapter.getItem(position);
                    int start = ChatActivity.this.mentionsAdapter.getResultStartPosition();
                    int len = ChatActivity.this.mentionsAdapter.getResultLength();
                    if (object instanceof User) {
                        User user = (User) object;
                        if (user == null) {
                            return;
                        }
                        if (user.username != null) {
                            ChatActivity.this.chatActivityEnterView.replaceWithText(start, len, "@" + user.username + " ");
                            return;
                        }
                        String name = user.first_name;
                        if (name == null || name.length() == 0) {
                            name = user.last_name;
                        }
                        Spannable spannableString = new SpannableString(name + " ");
                        spannableString.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + user.id), ChatActivity.attach_photo, spannableString.length(), 33);
                        ChatActivity.this.chatActivityEnterView.replaceWithText(start, len, spannableString);
                    } else if (object instanceof String) {
                        if (ChatActivity.this.mentionsAdapter.isBotCommands()) {
                            SendMessagesHelper.getInstance().sendMessage((String) object, ChatActivity.this.dialog_id, null, null, false, null, null, null);
                            ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                            return;
                        }
                        ChatActivity.this.chatActivityEnterView.replaceWithText(start, len, object + " ");
                    } else if (object instanceof BotInlineResult) {
                        if (ChatActivity.this.chatActivityEnterView.getFieldText() != null) {
                            int uid = ChatActivity.this.mentionsAdapter.getContextBotId();
                            BotInlineResult result = (BotInlineResult) object;
                            HashMap<String, String> params = new HashMap();
                            params.put(TtmlNode.ATTR_ID, result.id);
                            params.put("query_id", TtmlNode.ANONYMOUS_REGION_ID + result.query_id);
                            params.put("bot", TtmlNode.ANONYMOUS_REGION_ID + uid);
                            params.put("bot_name", ChatActivity.this.mentionsAdapter.getContextBotName());
                            SendMessagesHelper.prepareSendingBotContextResult(result, params, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                            ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                            SearchQuery.increaseInlineRaiting(uid);
                        }
                    } else if (object instanceof TL_inlineBotSwitchPM) {
                        ChatActivity.this.processInlineBotContextPM((TL_inlineBotSwitchPM) object);
                    }
                }
            };
            this.mentionsOnItemClickListener = anonymousClass32;
            recyclerListView.setOnItemClickListener(anonymousClass32);
            this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                /* renamed from: org.telegram.ui.ChatActivity.33.1 */
                class C10791 implements OnClickListener {
                    C10791() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivity.this.mentionsAdapter.clearRecentHashtags();
                    }
                }

                public boolean onItemClick(View view, int position) {
                    boolean z = false;
                    if (ChatActivity.this.getParentActivity() == null || !ChatActivity.this.mentionsAdapter.isLongClickEnabled()) {
                        return false;
                    }
                    Object object = ChatActivity.this.mentionsAdapter.getItem(position);
                    if (!(object instanceof String)) {
                        return false;
                    }
                    if (!ChatActivity.this.mentionsAdapter.isBotCommands()) {
                        Builder builder = new Builder(ChatActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearSearch", C0691R.string.ClearSearch));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", C0691R.string.ClearButton).toUpperCase(), new C10791());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                        return true;
                    } else if (!URLSpanBotCommand.enabled) {
                        return false;
                    } else {
                        ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                        ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                        String str = (String) object;
                        if (ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup) {
                            z = true;
                        }
                        chatActivityEnterView.setCommand(null, str, true, z);
                        return true;
                    }
                }
            });
            this.mentionListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    boolean z = true;
                    ChatActivity chatActivity = ChatActivity.this;
                    if (newState != ChatActivity.attach_gallery) {
                        z = false;
                    }
                    chatActivity.mentionListViewIsScrolling = z;
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int lastVisibleItem;
                    int visibleItemCount;
                    if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                        lastVisibleItem = ChatActivity.this.mentionGridLayoutManager.findLastVisibleItemPosition();
                    } else {
                        lastVisibleItem = ChatActivity.this.mentionLayoutManager.findLastVisibleItemPosition();
                    }
                    if (lastVisibleItem == -1) {
                        visibleItemCount = ChatActivity.attach_photo;
                    } else {
                        visibleItemCount = lastVisibleItem;
                    }
                    if (visibleItemCount > 0 && lastVisibleItem > ChatActivity.this.mentionsAdapter.getItemCount() - 5) {
                        ChatActivity.this.mentionsAdapter.searchForContextBotForNextOffset();
                    }
                    ChatActivity.this.mentionListViewUpdateLayout();
                }
            });
        }
        this.pagedownButton = new FrameLayout(context);
        this.pagedownButton.setVisibility(attach_document);
        contentView.addView(this.pagedownButton, LayoutHelper.createFrame(46, 59.0f, 85, 0.0f, 0.0f, 7.0f, 5.0f));
        this.pagedownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.returnToMessageId > 0) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.returnToMessageId, ChatActivity.attach_photo, true, ChatActivity.this.returnToLoadIndex);
                } else {
                    ChatActivity.this.scrollToLastMessage(true);
                }
            }
        });
        textView = new ImageView(context);
        textView.setImageResource(C0691R.drawable.pagedown);
        this.pagedownButton.addView(textView, LayoutHelper.createFrame(46, 46, 83));
        this.pagedownButtonCounter = new TextView(context);
        this.pagedownButtonCounter.setVisibility(attach_document);
        this.pagedownButtonCounter.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pagedownButtonCounter.setTextSize(attach_gallery, 13.0f);
        this.pagedownButtonCounter.setTextColor(-1);
        this.pagedownButtonCounter.setGravity(share_contact);
        this.pagedownButtonCounter.setBackgroundResource(C0691R.drawable.chat_badge);
        this.pagedownButtonCounter.setMinWidth(AndroidUtilities.dp(23.0f));
        this.pagedownButtonCounter.setPadding(AndroidUtilities.dp(8.0f), attach_photo, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        this.pagedownButton.addView(this.pagedownButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
        this.chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), contentView, this, true);
        this.chatActivityEnterView.setDialogId(this.dialog_id);
        this.chatActivityEnterView.addToAttachLayout(this.menuItem);
        this.chatActivityEnterView.setId(id_chat_compose_panel);
        this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
        if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46) {
            z2 = true;
        } else {
            z2 = false;
        }
        chatActivityEnterView.setAllowStickersAndGifs(z, z2);
        contentView.addView(this.chatActivityEnterView, contentView.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterViewDelegate() {

            /* renamed from: org.telegram.ui.ChatActivity.36.1 */
            class C10801 implements Runnable {
                final /* synthetic */ CharSequence val$text;

                C10801(CharSequence charSequence) {
                    this.val$text = charSequence;
                }

                public void run() {
                    if (this == ChatActivity.this.waitingForCharaterEnterRunnable) {
                        ChatActivity.this.searchLinks(this.val$text, false);
                        ChatActivity.this.waitingForCharaterEnterRunnable = null;
                    }
                }
            }

            public void onMessageSend(CharSequence message) {
                ChatActivity.this.moveScrollToLastMessage();
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                if (ChatActivity.this.mentionsAdapter != null) {
                    ChatActivity.this.mentionsAdapter.addHashtagsFromMessage(message);
                }
            }

            public void onTextChanged(CharSequence text, boolean bigChange) {
                MediaController instance = MediaController.getInstance();
                boolean z = !(text == null || text.length() == 0) || ChatActivity.this.chatActivityEnterView.isEditingMessage();
                instance.setInputFieldHasText(z);
                if (!(ChatActivity.this.stickersAdapter == null || ChatActivity.this.chatActivityEnterView.isEditingMessage())) {
                    ChatActivity.this.stickersAdapter.loadStikersForEmoji(text);
                }
                if (ChatActivity.this.mentionsAdapter != null) {
                    ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), ChatActivity.this.chatActivityEnterView.getCursorPosition(), ChatActivity.this.messages);
                }
                if (ChatActivity.this.waitingForCharaterEnterRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable);
                    ChatActivity.this.waitingForCharaterEnterRunnable = null;
                }
                if (!ChatActivity.this.chatActivityEnterView.isMessageWebPageSearchEnabled()) {
                    return;
                }
                if (!ChatActivity.this.chatActivityEnterView.isEditingMessage() || !ChatActivity.this.chatActivityEnterView.isEditingCaption()) {
                    if (bigChange) {
                        ChatActivity.this.searchLinks(text, true);
                        return;
                    }
                    ChatActivity.this.waitingForCharaterEnterRunnable = new C10801(text);
                    AndroidUtilities.runOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable, AndroidUtilities.WEB_URL == null ? 3000 : 1000);
                }
            }

            public void needSendTyping() {
                MessagesController.getInstance().sendTyping(ChatActivity.this.dialog_id, ChatActivity.attach_photo, ChatActivity.this.classGuid);
            }

            public void onAttachButtonHidden() {
                if (!ChatActivity.this.actionBar.isSearchFieldVisible()) {
                    if (ChatActivity.this.attachItem != null) {
                        ChatActivity.this.attachItem.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.headerItem != null) {
                        ChatActivity.this.headerItem.setVisibility(8);
                    }
                }
            }

            public void onAttachButtonShow() {
                if (!ChatActivity.this.actionBar.isSearchFieldVisible()) {
                    if (ChatActivity.this.attachItem != null) {
                        ChatActivity.this.attachItem.setVisibility(8);
                    }
                    if (ChatActivity.this.headerItem != null) {
                        ChatActivity.this.headerItem.setVisibility(ChatActivity.attach_photo);
                    }
                }
            }

            public void onMessageEditEnd(boolean loading) {
                if (loading) {
                    ChatActivity.this.showEditDoneProgress(true, true);
                    return;
                }
                MentionsAdapter access$4000 = ChatActivity.this.mentionsAdapter;
                boolean z = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                access$4000.setNeedBotContext(z);
                ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                ChatActivity.this.chatListView.setClickable(true);
                ChatActivity.this.chatListView.setLongClickable(true);
                ChatActivity.this.mentionsAdapter.setAllowNewMentions(true);
                ChatActivity.this.actionModeTitleContainer.setVisibility(8);
                ChatActivity.this.selectedMessagesCountTextView.setVisibility(ChatActivity.attach_photo);
                ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                if (ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 23) {
                    z = true;
                } else {
                    z = false;
                }
                boolean z2 = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                chatActivityEnterView.setAllowStickersAndGifs(z, z2);
                if (ChatActivity.this.editingMessageObjectReqId != 0) {
                    ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.editingMessageObjectReqId, true);
                    ChatActivity.this.editingMessageObjectReqId = ChatActivity.attach_photo;
                }
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            }

            public void onWindowSizeChanged(int size) {
                boolean z = true;
                if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
                    ChatActivity.this.allowStickersPanel = false;
                    if (ChatActivity.this.stickersPanel.getVisibility() == 0) {
                        ChatActivity.this.stickersPanel.setVisibility(ChatActivity.attach_document);
                    }
                    if (ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getVisibility() == 0) {
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_document);
                    }
                } else {
                    ChatActivity.this.allowStickersPanel = true;
                    if (ChatActivity.this.stickersPanel.getVisibility() == ChatActivity.attach_document) {
                        ChatActivity.this.stickersPanel.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getVisibility() == ChatActivity.attach_document && (!ChatActivity.this.mentionsAdapter.isBotContext() || ChatActivity.this.allowContextBotPanel || ChatActivity.this.allowContextBotPanelSecond)) {
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_photo);
                        ChatActivity.this.mentionContainer.setTag(null);
                    }
                }
                ChatActivity chatActivity = ChatActivity.this;
                if (ChatActivity.this.chatActivityEnterView.isPopupShowing()) {
                    z = false;
                }
                chatActivity.allowContextBotPanel = z;
                ChatActivity.this.checkContextBotPanel();
            }

            public void onStickersTab(boolean opened) {
                if (ChatActivity.this.emojiButtonRed != null) {
                    ChatActivity.this.emojiButtonRed.setVisibility(8);
                }
                ChatActivity.this.allowContextBotPanelSecond = !opened;
                ChatActivity.this.checkContextBotPanel();
            }
        });
        textView = new AnonymousClass37(context);
        textView.setClickable(true);
        this.chatActivityEnterView.addTopView(textView, 48);
        textView = new View(context);
        textView.setBackgroundColor(-1513240);
        textView.addView(textView, LayoutHelper.createFrame(-1, attach_gallery, 83));
        this.replyIconImageView = new ImageView(context);
        this.replyIconImageView.setScaleType(ScaleType.CENTER);
        textView.addView(this.replyIconImageView, LayoutHelper.createFrame(52, 46, 51));
        textView = new ImageView(context);
        textView.setImageResource(C0691R.drawable.delete_reply);
        textView.setScaleType(ScaleType.CENTER);
        textView.addView(textView, LayoutHelper.createFrame(52, 46.0f, 53, 0.0f, 0.5f, 0.0f, 0.0f));
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ChatActivity.this.forwardingMessages != null) {
                    ChatActivity.this.forwardingMessages.clear();
                }
                ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, true, true);
            }
        });
        this.replyNameTextView = new SimpleTextView(context);
        this.replyNameTextView.setTextSize(chat_menu_attach);
        this.replyNameTextView.setTextColor(Theme.REPLY_PANEL_NAME_TEXT_COLOR);
        this.replyNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.addView(this.replyNameTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 6.0f, 52.0f, 0.0f));
        this.replyObjectTextView = new SimpleTextView(context);
        this.replyObjectTextView.setTextSize(chat_menu_attach);
        this.replyObjectTextView.setTextColor(Theme.REPLY_PANEL_MESSAGE_TEXT_COLOR);
        textView.addView(this.replyObjectTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 24.0f, 52.0f, 0.0f));
        this.replyImageView = new BackupImageView(context);
        textView.addView(this.replyImageView, LayoutHelper.createFrame(34, 34.0f, 51, 52.0f, 6.0f, 0.0f, 0.0f));
        this.stickersPanel = new FrameLayout(context);
        this.stickersPanel.setVisibility(8);
        contentView.addView(this.stickersPanel, LayoutHelper.createFrame(-2, 81.5f, 83, 0.0f, 0.0f, 0.0f, 38.0f));
        this.stickersListView = new AnonymousClass39(context);
        this.stickersListView.setTag(Integer.valueOf(attach_audio));
        this.stickersListView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return StickerPreviewViewer.getInstance().onTouch(event, ChatActivity.this.stickersListView, ChatActivity.attach_photo, ChatActivity.this.stickersOnItemClickListener);
            }
        });
        this.stickersListView.setDisallowInterceptTouchEvents(true);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(attach_photo);
        this.stickersListView.setLayoutManager(linearLayoutManager);
        this.stickersListView.setClipToPadding(false);
        this.stickersListView.setOverScrollMode(attach_video);
        this.stickersPanel.addView(this.stickersListView, LayoutHelper.createFrame(-1, 78.0f));
        initStickers();
        textView = new ImageView(context);
        textView.setImageResource(C0691R.drawable.stickers_back_arrow);
        this.stickersPanel.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 83, 53.0f, 0.0f, 0.0f, 0.0f));
        this.searchContainer = new FrameLayout(context);
        this.searchContainer.setBackgroundResource(C0691R.drawable.compose_panel);
        this.searchContainer.setVisibility(attach_document);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setBackgroundResource(C0691R.drawable.compose_panel);
        this.searchContainer.setPadding(attach_photo, AndroidUtilities.dp(3.0f), attach_photo, attach_photo);
        contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.searchUpButton = new ImageView(context);
        this.searchUpButton.setScaleType(ScaleType.CENTER);
        this.searchUpButton.setImageResource(C0691R.drawable.search_up);
        this.searchContainer.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f));
        this.searchUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, ChatActivity.attach_gallery);
            }
        });
        this.searchDownButton = new ImageView(context);
        this.searchDownButton.setScaleType(ScaleType.CENTER);
        this.searchDownButton.setImageResource(C0691R.drawable.search_down);
        this.searchContainer.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 51, 48.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, ChatActivity.attach_video);
            }
        });
        this.searchCountText = new SimpleTextView(context);
        this.searchCountText.setTextColor(Theme.CHAT_SEARCH_COUNT_TEXT_COLOR);
        this.searchCountText.setTextSize(clear_history);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, reply, 108.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlay = new FrameLayout(context);
        this.bottomOverlay.setVisibility(attach_document);
        this.bottomOverlay.setFocusable(true);
        this.bottomOverlay.setFocusableInTouchMode(true);
        this.bottomOverlay.setClickable(true);
        this.bottomOverlay.setBackgroundResource(C0691R.drawable.compose_panel);
        this.bottomOverlay.setPadding(attach_photo, AndroidUtilities.dp(3.0f), attach_photo, attach_photo);
        contentView.addView(this.bottomOverlay, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayText = new TextView(context);
        this.bottomOverlayText.setTextSize(attach_gallery, 16.0f);
        this.bottomOverlayText.setTextColor(Theme.CHAT_BOTTOM_OVERLAY_TEXT_COLOR);
        this.bottomOverlay.addView(this.bottomOverlayText, LayoutHelper.createFrame(-2, -2, share_contact));
        this.bottomOverlayChat = new FrameLayout(context);
        this.bottomOverlayChat.setBackgroundResource(C0691R.drawable.compose_panel);
        this.bottomOverlayChat.setPadding(attach_photo, AndroidUtilities.dp(3.0f), attach_photo, attach_photo);
        this.bottomOverlayChat.setVisibility(attach_document);
        contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {

            /* renamed from: org.telegram.ui.ChatActivity.43.1 */
            class C10821 implements OnClickListener {
                C10821() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                }
            }

            /* renamed from: org.telegram.ui.ChatActivity.43.2 */
            class C10832 implements OnClickListener {
                C10832() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    ChatActivity.this.finishFragment();
                }
            }

            public void onClick(View view) {
                if (ChatActivity.this.getParentActivity() != null) {
                    Builder builder = null;
                    if (ChatActivity.this.currentUser == null || !ChatActivity.this.userBlocked) {
                        if (ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot && ChatActivity.this.botUser != null) {
                            if (ChatActivity.this.botUser.length() != 0) {
                                MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, ChatActivity.this.botUser);
                            } else {
                                SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                            }
                            ChatActivity.this.botUser = null;
                            ChatActivity.this.updateBottomOverlay();
                        } else if (!ChatObject.isChannel(ChatActivity.this.currentChat) || (ChatActivity.this.currentChat instanceof TL_channelForbidden)) {
                            builder = new Builder(ChatActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0691R.string.AreYouSureDeleteThisChat));
                            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10832());
                        } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                            MessagesController.getInstance().addUserToChat(ChatActivity.this.currentChat.id, UserConfig.getCurrentUser(), null, ChatActivity.attach_photo, null, null);
                        } else {
                            ChatActivity.this.toggleMute(true);
                        }
                    } else if (ChatActivity.this.currentUser.bot) {
                        String botUserLast = ChatActivity.this.botUser;
                        ChatActivity.this.botUser = null;
                        MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                        if (botUserLast == null || botUserLast.length() == 0) {
                            SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                        } else {
                            MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, botUserLast);
                        }
                    } else {
                        builder = new Builder(ChatActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0691R.string.AreYouSureUnblockContact));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10821());
                    }
                    if (builder != null) {
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                    }
                }
            }
        });
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(attach_gallery, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.CHAT_BOTTOM_CHAT_OVERLAY_TEXT_COLOR);
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, share_contact));
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? attach_photo : attach_document);
            this.chatListView.setEmptyView(null);
        } else {
            this.progressView.setVisibility(attach_document);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (this.userBlocked) {
            messageObject = null;
        } else {
            messageObject = this.botButtons;
        }
        chatActivityEnterView2.setButtons(messageObject);
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isSmallTablet()) {
            View playerView = new PlayerView(context, this);
            this.playerView = playerView;
            contentView.addView(playerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        updateContactStatus();
        updateBottomOverlay();
        updateSecretStatus();
        updateSpamView();
        updatePinnedMessageView(true);
        try {
            if (this.currentEncryptedChat != null && VERSION.SDK_INT >= 23) {
                getParentActivity().getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        fixLayoutInternal();
        return this.fragmentView;
    }

    private void mentionListViewUpdateLayout() {
        int newOffset = attach_photo;
        if (this.mentionListView.getChildCount() <= 0) {
            this.mentionListViewScrollOffsetY = attach_photo;
            this.mentionListViewLastViewPosition = -1;
            return;
        }
        View child = this.mentionListView.getChildAt(this.mentionListView.getChildCount() - 1);
        Holder holder = (Holder) this.mentionListView.findContainingViewHolder(child);
        if (holder != null) {
            this.mentionListViewLastViewPosition = holder.getAdapterPosition();
            this.mentionListViewLastViewTop = child.getTop();
        } else {
            this.mentionListViewLastViewPosition = -1;
        }
        child = this.mentionListView.getChildAt(attach_photo);
        holder = (Holder) this.mentionListView.findContainingViewHolder(child);
        if (child.getTop() > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = child.getTop();
        }
        if (this.mentionListViewScrollOffsetY != newOffset) {
            RecyclerListView recyclerListView = this.mentionListView;
            this.mentionListViewScrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.mentionListView.invalidate();
            this.mentionContainer.invalidate();
        }
    }

    private void checkBotCommands() {
        boolean z = true;
        URLSpanBotCommand.enabled = false;
        if (this.currentUser != null && this.currentUser.bot) {
            URLSpanBotCommand.enabled = true;
        } else if (this.info instanceof TL_chatFull) {
            int a = attach_photo;
            while (a < this.info.participants.participants.size()) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (user == null || !user.bot) {
                    a += attach_gallery;
                } else {
                    URLSpanBotCommand.enabled = true;
                    return;
                }
            }
        } else if (this.info instanceof TL_channelFull) {
            if (this.info.bot_info.isEmpty()) {
                z = false;
            }
            URLSpanBotCommand.enabled = z;
        }
    }

    public void processInlineBotContextPM(TL_inlineBotSwitchPM object) {
        if (object != null) {
            User user = this.mentionsAdapter.getContextBotUser();
            if (user != null) {
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                if (this.dialog_id == ((long) user.id)) {
                    this.inlineReturn = this.dialog_id;
                    MessagesController.getInstance().sendBotStart(this.currentUser, object.start_param);
                    return;
                }
                Bundle args = new Bundle();
                args.putInt("user_id", user.id);
                args.putString("inline_query", object.start_param);
                args.putLong("inline_return", this.dialog_id);
                if (MessagesController.checkCanOpenChat(args, this)) {
                    presentFragment(new ChatActivity(args));
                }
            }
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            this.chatAttachAlert = new ChatAttachAlert(getParentActivity());
            this.chatAttachAlert.setDelegate(new ChatAttachViewDelegate() {
                public void didPressedButton(int button) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (button == 7) {
                            ChatActivity.this.chatAttachAlert.dismiss();
                            HashMap<Integer, PhotoEntry> selectedPhotos = ChatActivity.this.chatAttachAlert.getSelectedPhotos();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList<String> photos = new ArrayList();
                                ArrayList<String> captions = new ArrayList();
                                for (Entry<Integer, PhotoEntry> entry : selectedPhotos.entrySet()) {
                                    PhotoEntry photoEntry = (PhotoEntry) entry.getValue();
                                    if (photoEntry.imagePath != null) {
                                        photos.add(photoEntry.imagePath);
                                        captions.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                                    } else if (photoEntry.path != null) {
                                        photos.add(photoEntry.path);
                                        captions.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                                    }
                                    photoEntry.imagePath = null;
                                    photoEntry.thumbPath = null;
                                    photoEntry.caption = null;
                                }
                                SendMessagesHelper.prepareSendingPhotos(photos, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, captions);
                                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                                return;
                            }
                            return;
                        }
                        if (ChatActivity.this.chatAttachAlert != null) {
                            ChatActivity.this.chatAttachAlert.dismissWithButtonClick(button);
                        }
                        ChatActivity.this.processSelectedAttach(button);
                    }
                }

                public View getRevealView() {
                    return ChatActivity.this.menuItem;
                }

                public void didSelectBot(User user) {
                    if (ChatActivity.this.chatActivityEnterView != null && user.username != null && user.username.length() != 0) {
                        ChatActivity.this.chatActivityEnterView.setFieldText("@" + user.username + " ");
                        ChatActivity.this.chatActivityEnterView.openKeyboard();
                    }
                }
            });
        }
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setBotUser(String value) {
        if (this.inlineReturn != 0) {
            MessagesController.getInstance().sendBotStart(this.currentUser, value);
            return;
        }
        this.botUser = value;
        updateBottomOverlay();
    }

    public boolean playFirstUnreadVoiceMessage() {
        for (int a = this.messages.size() - 1; a >= 0; a--) {
            MessageObject messageObject = (MessageObject) this.messages.get(a);
            if (messageObject.isVoice() && messageObject.isContentUnread() && !messageObject.isOut() && messageObject.messageOwner.to_id.channel_id == 0) {
                MediaController.getInstance().setVoiceMessagesPlaylist(MediaController.getInstance().playAudio(messageObject) ? createVoiceMessagesPlaylist(messageObject, true) : null, true);
                return true;
            }
        }
        if (VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            return false;
        }
        Activity parentActivity = getParentActivity();
        String[] strArr = new String[attach_gallery];
        strArr[attach_photo] = "android.permission.RECORD_AUDIO";
        parentActivity.requestPermissions(strArr, attach_audio);
        return true;
    }

    private void initStickers() {
        if (this.chatActivityEnterView != null && getParentActivity() != null && this.stickersAdapter == null) {
            if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23) {
                if (this.stickersAdapter != null) {
                    this.stickersAdapter.onDestroy();
                }
                this.stickersListView.setPadding(AndroidUtilities.dp(18.0f), attach_photo, AndroidUtilities.dp(18.0f), attach_photo);
                RecyclerListView recyclerListView = this.stickersListView;
                Adapter stickersAdapter = new StickersAdapter(getParentActivity(), new StickersAdapterDelegate() {

                    /* renamed from: org.telegram.ui.ChatActivity.45.1 */
                    class C18001 extends AnimatorListenerAdapterProxy {
                        final /* synthetic */ boolean val$show;

                        C18001(boolean z) {
                            this.val$show = z;
                        }

                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animation)) {
                                if (!this.val$show) {
                                    ChatActivity.this.stickersAdapter.clearStickers();
                                    ChatActivity.this.stickersPanel.setVisibility(8);
                                    if (StickerPreviewViewer.getInstance().isVisible()) {
                                        StickerPreviewViewer.getInstance().close();
                                    }
                                    StickerPreviewViewer.getInstance().reset();
                                }
                                ChatActivity.this.runningAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animation)) {
                                ChatActivity.this.runningAnimation = null;
                            }
                        }
                    }

                    public void needChangePanelVisibility(boolean show) {
                        float f = TouchHelperCallback.ALPHA_FULL;
                        if (!show || ChatActivity.this.stickersPanel.getVisibility() != 0) {
                            if (show || ChatActivity.this.stickersPanel.getVisibility() != 8) {
                                if (show) {
                                    ChatActivity.this.stickersListView.scrollToPosition(ChatActivity.attach_photo);
                                    ChatActivity.this.stickersPanel.setVisibility(ChatActivity.this.allowStickersPanel ? ChatActivity.attach_photo : ChatActivity.attach_document);
                                }
                                if (ChatActivity.this.runningAnimation != null) {
                                    ChatActivity.this.runningAnimation.cancel();
                                    ChatActivity.this.runningAnimation = null;
                                }
                                if (ChatActivity.this.stickersPanel.getVisibility() != ChatActivity.attach_document) {
                                    float f2;
                                    ChatActivity.this.runningAnimation = new AnimatorSet();
                                    AnimatorSet access$10000 = ChatActivity.this.runningAnimation;
                                    Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                                    FrameLayout access$8600 = ChatActivity.this.stickersPanel;
                                    String str = "alpha";
                                    float[] fArr = new float[ChatActivity.attach_video];
                                    if (show) {
                                        f2 = ChatActivity.attach_photo;
                                    } else {
                                        f2 = 1.0f;
                                    }
                                    fArr[ChatActivity.attach_photo] = f2;
                                    if (!show) {
                                        f = ChatActivity.attach_photo;
                                    }
                                    fArr[ChatActivity.attach_gallery] = f;
                                    animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(access$8600, str, fArr);
                                    access$10000.playTogether(animatorArr);
                                    ChatActivity.this.runningAnimation.setDuration(150);
                                    ChatActivity.this.runningAnimation.addListener(new C18001(show));
                                    ChatActivity.this.runningAnimation.start();
                                } else if (!show) {
                                    ChatActivity.this.stickersPanel.setVisibility(8);
                                }
                            }
                        }
                    }
                });
                this.stickersAdapter = stickersAdapter;
                recyclerListView.setAdapter(stickersAdapter);
                recyclerListView = this.stickersListView;
                OnItemClickListener anonymousClass46 = new OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        Document document = ChatActivity.this.stickersAdapter.getItem(position);
                        if (document instanceof TL_document) {
                            SendMessagesHelper.getInstance().sendSticker(document, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                            ChatActivity.this.chatActivityEnterView.addStickerToRecent(document);
                        }
                        ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                };
                this.stickersOnItemClickListener = anonymousClass46;
                recyclerListView.setOnItemClickListener(anonymousClass46);
            }
        }
    }

    public void shareMyContact(MessageObject messageObject) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ShareYouPhoneNumberTitle", C0691R.string.ShareYouPhoneNumberTitle));
        if (this.currentUser == null) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfo", C0691R.string.AreYouSureShareMyContactInfo));
        } else if (this.currentUser.bot) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfoBot", C0691R.string.AreYouSureShareMyContactInfoBot));
        } else {
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = PhoneFormat.getInstance().format("+" + UserConfig.getCurrentUser().phone);
            objArr[attach_gallery] = ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureShareMyContactInfoUser", C0691R.string.AreYouSureShareMyContactInfoUser, objArr)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new AnonymousClass47(messageObject));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void showGifHint() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", attach_photo);
        if (!preferences.getBoolean("gifhint", false)) {
            preferences.edit().putBoolean("gifhint", true).commit();
            if (getParentActivity() != null && this.fragmentView != null && this.gifHintTextView == null) {
                if (this.allowContextBotPanelSecond) {
                    SizeNotifierFrameLayout frameLayout = this.fragmentView;
                    int index = frameLayout.indexOfChild(this.chatActivityEnterView);
                    if (index != -1) {
                        this.chatActivityEnterView.setOpenGifsTabFirst();
                        this.emojiButtonRed = new View(getParentActivity());
                        this.emojiButtonRed.setBackgroundResource(C0691R.drawable.redcircle);
                        frameLayout.addView(this.emojiButtonRed, index + attach_gallery, LayoutHelper.createFrame(copy, 10.0f, 83, BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 27.0f));
                        this.gifHintTextView = new TextView(getParentActivity());
                        this.gifHintTextView.setBackgroundResource(C0691R.drawable.tooltip);
                        this.gifHintTextView.setTextColor(Theme.CHAT_GIF_HINT_TEXT_COLOR);
                        this.gifHintTextView.setTextSize(attach_gallery, 14.0f);
                        this.gifHintTextView.setPadding(AndroidUtilities.dp(10.0f), attach_photo, AndroidUtilities.dp(10.0f), attach_photo);
                        this.gifHintTextView.setText(LocaleController.getString("TapHereGifs", C0691R.string.TapHereGifs));
                        this.gifHintTextView.setGravity(delete_chat);
                        frameLayout.addView(this.gifHintTextView, index + attach_gallery, LayoutHelper.createFrame(-2, 32.0f, 83, 5.0f, 0.0f, 0.0f, 3.0f));
                        AnimatorSet AnimatorSet = new AnimatorSet();
                        Animator[] animatorArr = new Animator[attach_video];
                        animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.gifHintTextView, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                        animatorArr[attach_gallery] = ObjectAnimator.ofFloat(this.emojiButtonRed, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                        AnimatorSet.playTogether(animatorArr);
                        AnimatorSet.addListener(new AnimatorListenerAdapterProxy() {

                            /* renamed from: org.telegram.ui.ChatActivity.48.1 */
                            class C10841 implements Runnable {

                                /* renamed from: org.telegram.ui.ChatActivity.48.1.1 */
                                class C18011 extends AnimatorListenerAdapterProxy {
                                    C18011() {
                                    }

                                    public void onAnimationEnd(Animator animation) {
                                        if (ChatActivity.this.gifHintTextView != null) {
                                            ChatActivity.this.gifHintTextView.setVisibility(8);
                                        }
                                    }
                                }

                                C10841() {
                                }

                                public void run() {
                                    if (ChatActivity.this.gifHintTextView != null) {
                                        AnimatorSet AnimatorSet = new AnimatorSet();
                                        Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                                        float[] fArr = new float[ChatActivity.attach_gallery];
                                        fArr[ChatActivity.attach_photo] = 0.0f;
                                        animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, "alpha", fArr);
                                        AnimatorSet.playTogether(animatorArr);
                                        AnimatorSet.addListener(new C18011());
                                        AnimatorSet.setDuration(300);
                                        AnimatorSet.start();
                                    }
                                }
                            }

                            public void onAnimationEnd(Animator animation) {
                                AndroidUtilities.runOnUIThread(new C10841(), 2000);
                            }
                        });
                        AnimatorSet.setDuration(300);
                        AnimatorSet.start();
                    }
                } else if (this.chatActivityEnterView != null) {
                    this.chatActivityEnterView.setOpenGifsTabFirst();
                }
            }
        }
    }

    private void checkContextBotPanel() {
        if (!this.allowStickersPanel || this.mentionsAdapter == null || !this.mentionsAdapter.isBotContext()) {
            return;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.allowContextBotPanel || this.allowContextBotPanelSecond) {
            if (this.mentionContainer.getVisibility() == attach_document || this.mentionContainer.getTag() != null) {
                if (this.mentionListAnimation != null) {
                    this.mentionListAnimation.cancel();
                }
                this.mentionContainer.setTag(null);
                this.mentionContainer.setVisibility(attach_photo);
                this.mentionListAnimation = new AnimatorSet();
                animatorSet = this.mentionListAnimation;
                animatorArr = new Animator[attach_gallery];
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.mentionContainer, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                animatorSet.playTogether(animatorArr);
                this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                });
                this.mentionListAnimation.setDuration(200);
                this.mentionListAnimation.start();
            }
        } else if (this.mentionContainer.getVisibility() == 0 && this.mentionContainer.getTag() == null) {
            if (this.mentionListAnimation != null) {
                this.mentionListAnimation.cancel();
            }
            this.mentionContainer.setTag(Integer.valueOf(attach_gallery));
            this.mentionListAnimation = new AnimatorSet();
            animatorSet = this.mentionListAnimation;
            animatorArr = new Animator[attach_gallery];
            float[] fArr = new float[attach_gallery];
            fArr[attach_photo] = 0.0f;
            animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.mentionContainer, "alpha", fArr);
            animatorSet.playTogether(animatorArr);
            this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_document);
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animation)) {
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }
            });
            this.mentionListAnimation.setDuration(200);
            this.mentionListAnimation.start();
        }
    }

    private void checkScrollForLoad(boolean scroll) {
        if (this.chatLayoutManager != null && !this.paused) {
            int firstVisibleItem = this.chatLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = firstVisibleItem == -1 ? attach_photo : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + attach_gallery;
            if (visibleItemCount > 0) {
                int checkLoadCount;
                MessagesController instance;
                long j;
                int i;
                int i2;
                int i3;
                boolean isChannel;
                int i4;
                int totalItemCount = this.chatAdapter.getItemCount();
                if (scroll) {
                    checkLoadCount = 25;
                } else {
                    checkLoadCount = attach_contact;
                }
                if (firstVisibleItem <= checkLoadCount && !this.loading) {
                    boolean z;
                    if (!this.endReached[attach_photo]) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        if (this.messagesByDays.size() != 0) {
                            instance = MessagesController.getInstance();
                            j = this.dialog_id;
                            i = this.maxMessageId[attach_photo];
                            z = !this.cacheEndReached[attach_photo];
                            i2 = this.minDate[attach_photo];
                            i3 = this.classGuid;
                            isChannel = ChatObject.isChannel(this.currentChat);
                            i4 = this.lastLoadIndex;
                            this.lastLoadIndex = i4 + attach_gallery;
                            instance.loadMessages(j, 50, i, z, i2, i3, attach_photo, attach_photo, isChannel, i4);
                        } else {
                            instance = MessagesController.getInstance();
                            j = this.dialog_id;
                            z = !this.cacheEndReached[attach_photo];
                            i2 = this.minDate[attach_photo];
                            i3 = this.classGuid;
                            isChannel = ChatObject.isChannel(this.currentChat);
                            i4 = this.lastLoadIndex;
                            this.lastLoadIndex = i4 + attach_gallery;
                            instance.loadMessages(j, 50, attach_photo, z, i2, i3, attach_photo, attach_photo, isChannel, i4);
                        }
                    } else if (!(this.mergeDialogId == 0 || this.endReached[attach_gallery])) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.mergeDialogId;
                        i = this.maxMessageId[attach_gallery];
                        z = !this.cacheEndReached[attach_gallery];
                        i2 = this.minDate[attach_gallery];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + attach_gallery;
                        instance.loadMessages(j, 50, i, z, i2, i3, attach_photo, attach_photo, isChannel, i4);
                    }
                }
                if (!this.loadingForward && firstVisibleItem + visibleItemCount >= totalItemCount - 10) {
                    if (this.mergeDialogId != 0 && !this.forwardEndReached[attach_gallery]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.mergeDialogId;
                        i = this.minMessageId[attach_gallery];
                        i2 = this.maxDate[attach_gallery];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + attach_gallery;
                        instance.loadMessages(j, 50, i, true, i2, i3, attach_gallery, attach_photo, isChannel, i4);
                        this.loadingForward = true;
                    } else if (!this.forwardEndReached[attach_photo]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.dialog_id;
                        i = this.minMessageId[attach_photo];
                        i2 = this.maxDate[attach_photo];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + attach_gallery;
                        instance.loadMessages(j, 50, i, true, i2, i3, attach_gallery, attach_photo, isChannel, i4);
                        this.loadingForward = true;
                    }
                }
            }
        }
    }

    private void processSelectedAttach(int which) {
        if (which == 0 || which == attach_gallery || which == attach_document || which == attach_video) {
            String action;
            if (this.currentChat != null) {
                if (this.currentChat.participants_count > MessagesController.getInstance().groupBigSize) {
                    if (which == 0 || which == attach_gallery) {
                        action = "bigchat_upload_photo";
                    } else {
                        action = "bigchat_upload_document";
                    }
                } else if (which == 0 || which == attach_gallery) {
                    action = "chat_upload_photo";
                } else {
                    action = "chat_upload_document";
                }
            } else if (which == 0 || which == attach_gallery) {
                action = "pm_upload_photo";
            } else {
                action = "pm_upload_document";
            }
            if (!MessagesController.isFeatureEnabled(action, this)) {
                return;
            }
        }
        if (which == 0) {
            try {
                Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    takePictureIntent.putExtra("output", Uri.fromFile(image));
                    this.currentPicturePath = image.getAbsolutePath();
                }
                startActivityForResult(takePictureIntent, attach_photo);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        } else if (which == attach_gallery) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                boolean z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(false, z, this);
                fragment.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<String> photos, ArrayList<String> captions, ArrayList<SearchImage> webPhotos) {
                        SendMessagesHelper.prepareSendingPhotos(photos, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, captions);
                        SendMessagesHelper.prepareSendingPhotosSearch(webPhotos, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent videoPickerIntent = new Intent();
                            videoPickerIntent.setType("video/*");
                            videoPickerIntent.setAction("android.intent.action.GET_CONTENT");
                            videoPickerIntent.putExtra("android.intent.extra.sizeLimit", 1610612736);
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("image/*");
                            Intent chooserIntent = Intent.createChooser(photoPickerIntent, null);
                            Parcelable[] parcelableArr = new Intent[ChatActivity.attach_gallery];
                            parcelableArr[ChatActivity.attach_photo] = videoPickerIntent;
                            chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", parcelableArr);
                            ChatActivity.this.startActivityForResult(chooserIntent, ChatActivity.attach_gallery);
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }

                    public boolean didSelectVideo(String path) {
                        if (VERSION.SDK_INT < ChatActivity.delete_chat) {
                            SendMessagesHelper.prepareSendingVideo(path, 0, 0, ChatActivity.attach_photo, ChatActivity.attach_photo, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                            DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                            return true;
                        } else if (ChatActivity.this.openVideoEditor(path, true, true)) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                });
                presentFragment(fragment);
                return;
            }
            r8 = getParentActivity();
            r9 = new String[attach_gallery];
            r9[attach_photo] = "android.permission.READ_EXTERNAL_STORAGE";
            r8.requestPermissions(r9, attach_document);
        } else if (which == attach_video) {
            try {
                Intent takeVideoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
                File video = AndroidUtilities.generateVideoPath();
                if (video != null) {
                    if (VERSION.SDK_INT >= mute) {
                        takeVideoIntent.putExtra("output", Uri.fromFile(video));
                    }
                    takeVideoIntent.putExtra("android.intent.extra.sizeLimit", 1610612736);
                    this.currentPicturePath = video.getAbsolutePath();
                }
                startActivityForResult(takeVideoIntent, attach_video);
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
        } else if (which == attach_location) {
            if (AndroidUtilities.isGoogleMapsInstalled(this)) {
                LocationActivity fragment2 = new LocationActivity();
                fragment2.setDelegate(new LocationActivityDelegate() {
                    public void didSelectLocation(MessageMedia location) {
                        SendMessagesHelper.getInstance().sendMessage(location, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, null);
                        ChatActivity.this.moveScrollToLastMessage();
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                        if (ChatActivity.this.paused) {
                            ChatActivity.this.scrollToTopOnResume = true;
                        }
                    }
                });
                presentFragment(fragment2);
            }
        } else if (which == attach_document) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                DocumentSelectActivity fragment3 = new DocumentSelectActivity();
                fragment3.setDelegate(new DocumentSelectActivityDelegate() {
                    public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
                        activity.finishFragment();
                        SendMessagesHelper.prepareSendingDocuments(files, files, null, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public void startDocumentSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("*/*");
                            ChatActivity.this.startActivityForResult(photoPickerIntent, ChatActivity.report);
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                });
                presentFragment(fragment3);
                return;
            }
            r8 = getParentActivity();
            r9 = new String[attach_gallery];
            r9[attach_photo] = "android.permission.READ_EXTERNAL_STORAGE";
            r8.requestPermissions(r9, attach_document);
        } else if (which == attach_audio) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                AudioSelectActivity fragment4 = new AudioSelectActivity();
                fragment4.setDelegate(new AudioSelectActivityDelegate() {
                    public void didSelectAudio(ArrayList<MessageObject> audios) {
                        SendMessagesHelper.prepareSendingAudioDocuments(audios, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }
                });
                presentFragment(fragment4);
                return;
            }
            r8 = getParentActivity();
            r9 = new String[attach_gallery];
            r9[attach_photo] = "android.permission.READ_EXTERNAL_STORAGE";
            r8.requestPermissions(r9, attach_document);
        } else if (which != attach_contact) {
        } else {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
                    intent.setType("vnd.android.cursor.dir/phone_v2");
                    startActivityForResult(intent, bot_settings);
                    return;
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                    return;
                }
            }
            r8 = getParentActivity();
            r9 = new String[attach_gallery];
            r9[attach_photo] = "android.permission.READ_CONTACTS";
            r8.requestPermissions(r9, attach_contact);
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return !(dialog == this.chatAttachAlert && PhotoViewer.getInstance().isVisible()) && super.dismissDialogOnPause(dialog);
    }

    private void searchLinks(CharSequence charSequence, boolean force) {
        if (this.currentEncryptedChat == null || (MessagesController.getInstance().secretWebpagePreview != 0 && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) {
            if (force && this.foundWebPage != null) {
                if (this.foundWebPage.url != null) {
                    int index = TextUtils.indexOf(charSequence, this.foundWebPage.url);
                    char lastChar = '\u0000';
                    boolean lenEqual = false;
                    if (index != -1) {
                        if (this.foundWebPage.url.length() + index == charSequence.length()) {
                            lenEqual = true;
                        } else {
                            lenEqual = false;
                        }
                        if (lenEqual) {
                            lastChar = '\u0000';
                        } else {
                            lastChar = charSequence.charAt(this.foundWebPage.url.length() + index);
                        }
                    } else if (this.foundWebPage.display_url != null) {
                        index = TextUtils.indexOf(charSequence, this.foundWebPage.display_url);
                        if (index == -1 || this.foundWebPage.display_url.length() + index != charSequence.length()) {
                            lenEqual = false;
                        } else {
                            lenEqual = true;
                        }
                        if (index == -1 || lenEqual) {
                            lastChar = '\u0000';
                        } else {
                            lastChar = charSequence.charAt(this.foundWebPage.display_url.length() + index);
                        }
                    }
                    if (index != -1 && (lenEqual || lastChar == ' ' || lastChar == ',' || lastChar == '.' || lastChar == '!' || lastChar == '/')) {
                        return;
                    }
                }
                this.pendingLinkSearchString = null;
                showReplyPanel(false, null, null, this.foundWebPage, false, true);
            }
            Utilities.searchQueue.postRunnable(new AnonymousClass55(charSequence, force));
        }
    }

    private void forwardMessages(ArrayList<MessageObject> arrayList, boolean fromMyName) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (fromMyName) {
                Iterator i$ = arrayList.iterator();
                while (i$.hasNext()) {
                    SendMessagesHelper.getInstance().processForwardFromMyName((MessageObject) i$.next(), this.dialog_id);
                }
                return;
            }
            SendMessagesHelper.getInstance().sendMessage(arrayList, this.dialog_id);
        }
    }

    public void showReplyPanel(boolean show, MessageObject messageObjectToReply, ArrayList<MessageObject> messageObjectsToForward, WebPage webPage, boolean cancel, boolean animated) {
        if (this.chatActivityEnterView != null) {
            if (show) {
                if (messageObjectToReply != null || messageObjectsToForward != null || webPage != null) {
                    if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                        this.actionBar.closeSearchField();
                        this.chatActivityEnterView.setFieldFocused();
                    }
                    boolean openKeyboard = false;
                    if (!(messageObjectToReply == null || messageObjectToReply.getDialogId() == this.dialog_id)) {
                        messageObjectsToForward = new ArrayList();
                        messageObjectsToForward.add(messageObjectToReply);
                        messageObjectToReply = null;
                        openKeyboard = true;
                    }
                    User user;
                    String name;
                    Chat chat;
                    String mess;
                    if (messageObjectToReply != null) {
                        this.forwardingMessages = null;
                        this.replyingMessageObject = messageObjectToReply;
                        this.chatActivityEnterView.setReplyingMessageObject(messageObjectToReply);
                        if (this.foundWebPage == null) {
                            if (messageObjectToReply.isFromUser()) {
                                user = MessagesController.getInstance().getUser(Integer.valueOf(messageObjectToReply.messageOwner.from_id));
                                if (user != null) {
                                    name = UserObject.getUserName(user);
                                } else {
                                    return;
                                }
                            }
                            chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObjectToReply.messageOwner.to_id.channel_id));
                            if (chat != null) {
                                name = chat.title;
                            } else {
                                return;
                            }
                            this.replyIconImageView.setImageResource(C0691R.drawable.reply);
                            this.replyNameTextView.setText(name);
                            if (messageObjectToReply.messageText != null) {
                                mess = messageObjectToReply.messageText.toString();
                                if (mess.length() > 150) {
                                    mess = mess.substring(attach_photo, 150);
                                }
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace('\n', ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            }
                        } else {
                            return;
                        }
                    } else if (messageObjectsToForward == null) {
                        this.replyIconImageView.setImageResource(C0691R.drawable.link);
                        if (webPage instanceof TL_webPagePending) {
                            this.replyNameTextView.setText(LocaleController.getString("GettingLinkInfo", C0691R.string.GettingLinkInfo));
                            this.replyObjectTextView.setText(this.pendingLinkSearchString);
                        } else {
                            if (webPage.site_name != null) {
                                this.replyNameTextView.setText(webPage.site_name);
                            } else if (webPage.title != null) {
                                this.replyNameTextView.setText(webPage.title);
                            } else {
                                this.replyNameTextView.setText(LocaleController.getString("LinkPreview", C0691R.string.LinkPreview));
                            }
                            if (webPage.description != null) {
                                this.replyObjectTextView.setText(webPage.description);
                            } else if (webPage.title != null && webPage.site_name != null) {
                                this.replyObjectTextView.setText(webPage.title);
                            } else if (webPage.author != null) {
                                this.replyObjectTextView.setText(webPage.author);
                            } else {
                                this.replyObjectTextView.setText(webPage.display_url);
                            }
                            this.chatActivityEnterView.setWebPage(webPage, true);
                        }
                    } else if (!messageObjectsToForward.isEmpty()) {
                        this.replyingMessageObject = null;
                        this.chatActivityEnterView.setReplyingMessageObject(null);
                        this.forwardingMessages = messageObjectsToForward;
                        if (this.foundWebPage == null) {
                            int a;
                            Integer uid;
                            this.chatActivityEnterView.setForceShowSendButton(true, animated);
                            ArrayList<Integer> uids = new ArrayList();
                            this.replyIconImageView.setImageResource(C0691R.drawable.forward_blue);
                            MessageObject object = (MessageObject) messageObjectsToForward.get(attach_photo);
                            if (object.isFromUser()) {
                                uids.add(Integer.valueOf(object.messageOwner.from_id));
                            } else {
                                uids.add(Integer.valueOf(-object.messageOwner.to_id.channel_id));
                            }
                            int type = ((MessageObject) messageObjectsToForward.get(attach_photo)).type;
                            for (a = attach_gallery; a < messageObjectsToForward.size(); a += attach_gallery) {
                                object = (MessageObject) messageObjectsToForward.get(a);
                                if (object.isFromUser()) {
                                    uid = Integer.valueOf(object.messageOwner.from_id);
                                } else {
                                    uid = Integer.valueOf(-object.messageOwner.to_id.channel_id);
                                }
                                if (!uids.contains(uid)) {
                                    uids.add(uid);
                                }
                                if (((MessageObject) messageObjectsToForward.get(a)).type != type) {
                                    type = -1;
                                }
                            }
                            StringBuilder userNames = new StringBuilder();
                            for (a = attach_photo; a < uids.size(); a += attach_gallery) {
                                uid = (Integer) uids.get(a);
                                chat = null;
                                user = null;
                                if (uid.intValue() > 0) {
                                    user = MessagesController.getInstance().getUser(uid);
                                } else {
                                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-uid.intValue()));
                                }
                                if (user != null || chat != null) {
                                    if (uids.size() != attach_gallery) {
                                        if (uids.size() != attach_video && userNames.length() != 0) {
                                            userNames.append(" ");
                                            userNames.append(LocaleController.formatPluralString("AndOther", uids.size() - 1));
                                            break;
                                        }
                                        if (userNames.length() > 0) {
                                            userNames.append(", ");
                                        }
                                        if (user == null) {
                                            userNames.append(chat.title);
                                        } else if (user.first_name != null && user.first_name.length() > 0) {
                                            userNames.append(user.first_name);
                                        } else if (user.last_name == null || user.last_name.length() <= 0) {
                                            userNames.append(" ");
                                        } else {
                                            userNames.append(user.last_name);
                                        }
                                    } else if (user != null) {
                                        userNames.append(UserObject.getUserName(user));
                                    } else {
                                        userNames.append(chat.title);
                                    }
                                }
                            }
                            this.replyNameTextView.setText(userNames);
                            if (type == -1 || type == 0 || type == copy || type == forward) {
                                if (messageObjectsToForward.size() != attach_gallery || ((MessageObject) messageObjectsToForward.get(attach_photo)).messageText == null) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMessage", messageObjectsToForward.size()));
                                } else {
                                    mess = ((MessageObject) messageObjectsToForward.get(attach_photo)).messageText.toString();
                                    if (mess.length() > 150) {
                                        mess = mess.substring(attach_photo, 150);
                                    }
                                    this.replyObjectTextView.setText(Emoji.replaceEmoji(mess.replace('\n', ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                }
                            } else if (type == attach_gallery) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedPhoto", messageObjectsToForward.size()));
                                if (messageObjectsToForward.size() == attach_gallery) {
                                    messageObjectToReply = (MessageObject) messageObjectsToForward.get(attach_photo);
                                }
                            } else if (type == attach_document) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedLocation", messageObjectsToForward.size()));
                            } else if (type == attach_audio) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedVideo", messageObjectsToForward.size()));
                                if (messageObjectsToForward.size() == attach_gallery) {
                                    messageObjectToReply = (MessageObject) messageObjectsToForward.get(attach_photo);
                                }
                            } else if (type == delete) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedContact", messageObjectsToForward.size()));
                            } else if (type == attach_video) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedAudio", messageObjectsToForward.size()));
                            } else if (type == chat_menu_attach) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMusic", messageObjectsToForward.size()));
                            } else if (type == chat_enc_timer) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedSticker", messageObjectsToForward.size()));
                            } else if (type == 8 || type == 9) {
                                if (messageObjectsToForward.size() != attach_gallery) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedFile", messageObjectsToForward.size()));
                                } else if (type == 8) {
                                    this.replyObjectTextView.setText(LocaleController.getString("AttachGif", C0691R.string.AttachGif));
                                } else {
                                    name = FileLoader.getDocumentFileName(((MessageObject) messageObjectsToForward.get(attach_photo)).getDocument());
                                    if (name.length() != 0) {
                                        this.replyObjectTextView.setText(name);
                                    }
                                    messageObjectToReply = (MessageObject) messageObjectsToForward.get(attach_photo);
                                }
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                    LayoutParams layoutParams1 = (LayoutParams) this.replyNameTextView.getLayoutParams();
                    LayoutParams layoutParams2 = (LayoutParams) this.replyObjectTextView.getLayoutParams();
                    PhotoSize photoSize = messageObjectToReply != null ? FileLoader.getClosestPhotoSizeWithSize(messageObjectToReply.photoThumbs, 80) : null;
                    int dp;
                    if (photoSize == null || (photoSize instanceof TL_photoSizeEmpty) || (photoSize.location instanceof TL_fileLocationUnavailable) || messageObjectToReply.type == chat_enc_timer || (messageObjectToReply != null && messageObjectToReply.isSecretMedia())) {
                        this.replyImageView.setImageBitmap(null);
                        this.replyImageLocation = null;
                        this.replyImageView.setVisibility(attach_document);
                        dp = AndroidUtilities.dp(52.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams1.leftMargin = dp;
                    } else {
                        this.replyImageLocation = photoSize.location;
                        this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null);
                        this.replyImageView.setVisibility(attach_photo);
                        dp = AndroidUtilities.dp(96.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams1.leftMargin = dp;
                    }
                    this.replyNameTextView.setLayoutParams(layoutParams1);
                    this.replyObjectTextView.setLayoutParams(layoutParams2);
                    this.chatActivityEnterView.showTopView(animated, openKeyboard);
                }
            } else if (this.replyingMessageObject != null || this.forwardingMessages != null || this.foundWebPage != null) {
                if (this.replyingMessageObject != null && (this.replyingMessageObject.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", attach_photo).edit().putInt("answered_" + this.dialog_id, this.replyingMessageObject.getId()).commit();
                }
                if (this.foundWebPage != null) {
                    this.foundWebPage = null;
                    this.chatActivityEnterView.setWebPage(null, !cancel);
                    if (!(webPage == null || (this.replyingMessageObject == null && this.forwardingMessages == null))) {
                        showReplyPanel(true, this.replyingMessageObject, this.forwardingMessages, null, false, true);
                        return;
                    }
                }
                if (this.forwardingMessages != null) {
                    forwardMessages(this.forwardingMessages, false);
                }
                this.chatActivityEnterView.setForceShowSendButton(false, animated);
                this.chatActivityEnterView.hideTopView(animated);
                this.chatActivityEnterView.setReplyingMessageObject(null);
                this.replyingMessageObject = null;
                this.forwardingMessages = null;
                this.replyImageLocation = null;
            }
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private boolean sendSecretMessageRead(MessageObject messageObject) {
        if (messageObject == null || messageObject.isOut() || !messageObject.isSecretMedia() || messageObject.messageOwner.destroyTime != 0 || messageObject.messageOwner.ttl <= 0) {
            return false;
        }
        MessagesController.getInstance().markMessageAsRead(this.dialog_id, messageObject.messageOwner.random_id, messageObject.messageOwner.ttl);
        messageObject.messageOwner.destroyTime = messageObject.messageOwner.ttl + ConnectionsManager.getInstance().getCurrentTime();
        return true;
    }

    private void clearChatData() {
        this.messages.clear();
        this.messagesByDays.clear();
        this.waitingForLoad.clear();
        this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? attach_photo : attach_document);
        this.chatListView.setEmptyView(null);
        for (int a = attach_photo; a < attach_video; a += attach_gallery) {
            this.messagesDict[a].clear();
            if (this.currentEncryptedChat == null) {
                this.maxMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.minMessageId[a] = LinearLayoutManager.INVALID_OFFSET;
            } else {
                this.maxMessageId[a] = LinearLayoutManager.INVALID_OFFSET;
                this.minMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            this.maxDate[a] = LinearLayoutManager.INVALID_OFFSET;
            this.minDate[a] = attach_photo;
            this.endReached[a] = false;
            this.cacheEndReached[a] = false;
            this.forwardEndReached[a] = true;
        }
        this.first = true;
        this.firstLoading = true;
        this.loading = true;
        this.loadingForward = false;
        this.waitingForReplyMessageLoad = false;
        this.startLoadFromMessageId = attach_photo;
        this.last_message_id = attach_photo;
        this.needSelectFromMessageId = false;
        this.chatAdapter.notifyDataSetChanged();
    }

    private void scrollToLastMessage(boolean pagedown) {
        if (!this.forwardEndReached[attach_photo] || this.first_unread_id != 0 || this.startLoadFromMessageId != 0) {
            clearChatData();
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            MessagesController instance = MessagesController.getInstance();
            long j = this.dialog_id;
            int i = this.classGuid;
            boolean isChannel = ChatObject.isChannel(this.currentChat);
            int i2 = this.lastLoadIndex;
            this.lastLoadIndex = i2 + attach_gallery;
            instance.loadMessages(j, bot_help, attach_photo, true, attach_photo, i, attach_photo, attach_photo, isChannel, i2);
        } else if (pagedown && this.chatLayoutManager.findLastCompletelyVisibleItemPosition() == this.chatAdapter.getItemCount() - 1) {
            showPagedownButton(false, true);
            this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            updateVisibleRows();
        } else {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            int count = this.chatListView.getChildCount();
            int additionalTop;
            if (this.chatActivityEnterView.isTopViewVisible()) {
                additionalTop = AndroidUtilities.dp(48.0f);
            } else {
                additionalTop = attach_photo;
            }
            int height = this.chatListView.getMeasuredHeight();
            for (int a = attach_photo; a < count; a += attach_gallery) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    int top = messageCell.getTop();
                    int bottom = messageCell.getBottom();
                    int viewTop = top >= 0 ? attach_photo : -top;
                    int viewBottom = messageCell.getMeasuredHeight();
                    if (viewBottom > height) {
                        viewBottom = viewTop + height;
                    }
                    messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                }
            }
        }
    }

    private void toggleMute(boolean instant) {
        Editor editor;
        TL_dialog dialog;
        if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
            editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo).edit();
            editor.putInt("notify2_" + this.dialog_id, attach_photo);
            MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 0);
            editor.commit();
            dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
            }
            NotificationsController.updateServerNotificationsSettings(this.dialog_id);
        } else if (instant) {
            editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo).edit();
            editor.putInt("notify2_" + this.dialog_id, attach_video);
            MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 1);
            editor.commit();
            dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
                dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            NotificationsController.updateServerNotificationsSettings(this.dialog_id);
            NotificationsController.getInstance().removeNotificationsForDialog(this.dialog_id);
        } else {
            showDialog(AlertsCreator.createMuteAlert(getParentActivity(), this.dialog_id));
        }
    }

    private void scrollToMessageId(int id, int fromMessageId, boolean select, int loadIndex) {
        MessageObject object = (MessageObject) this.messagesDict[loadIndex].get(Integer.valueOf(id));
        boolean query = false;
        if (object == null) {
            query = true;
        } else if (this.messages.indexOf(object) != -1) {
            if (select) {
                this.highlightMessageId = id;
            } else {
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            int yOffset = Math.max(attach_photo, (this.chatListView.getHeight() - object.getApproximateHeight()) / attach_video);
            if (this.messages.get(this.messages.size() - 1) == object) {
                this.chatLayoutManager.scrollToPositionWithOffset(attach_photo, ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
            } else {
                this.chatLayoutManager.scrollToPositionWithOffset(((this.chatAdapter.messagesStartRow + this.messages.size()) - this.messages.indexOf(object)) - 1, ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
            }
            updateVisibleRows();
            boolean found = false;
            int count = this.chatListView.getChildCount();
            for (int a = attach_photo; a < count; a += attach_gallery) {
                View view = this.chatListView.getChildAt(a);
                MessageObject messageObject;
                if (view instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) view).getMessageObject();
                    if (messageObject != null && messageObject.getId() == object.getId()) {
                        found = true;
                        break;
                    }
                } else if (view instanceof ChatActionCell) {
                    messageObject = ((ChatActionCell) view).getMessageObject();
                    if (messageObject != null && messageObject.getId() == object.getId()) {
                        found = true;
                        break;
                    }
                } else {
                    continue;
                }
            }
            if (!found) {
                showPagedownButton(true, true);
            }
        } else {
            query = true;
        }
        if (query) {
            if (this.currentEncryptedChat == null || MessagesStorage.getInstance().checkMessageId(this.dialog_id, this.startLoadFromMessageId)) {
                this.waitingForLoad.clear();
                this.waitingForReplyMessageLoad = true;
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.scrollToMessagePosition = -10000;
                this.startLoadFromMessageId = id;
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance();
                long j = loadIndex == 0 ? this.dialog_id : this.mergeDialogId;
                int i = AndroidUtilities.isTablet() ? bot_help : edit_done;
                int i2 = this.startLoadFromMessageId;
                int i3 = this.classGuid;
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                int i4 = this.lastLoadIndex;
                this.lastLoadIndex = i4 + attach_gallery;
                instance.loadMessages(j, i, i2, true, attach_photo, i3, attach_audio, attach_photo, isChannel, i4);
            } else {
                return;
            }
        }
        this.returnToMessageId = fromMessageId;
        this.returnToLoadIndex = loadIndex;
        this.needSelectFromMessageId = select;
    }

    private void showPagedownButton(boolean show, boolean animated) {
        if (this.pagedownButton != null) {
            float[] fArr;
            if (show) {
                this.pagedownButtonShowedByScroll = false;
                if (this.pagedownButton.getTag() == null) {
                    if (this.pagedownButtonAnimation != null) {
                        this.pagedownButtonAnimation.cancel();
                        this.pagedownButtonAnimation = null;
                    }
                    if (animated) {
                        if (this.pagedownButton.getTranslationY() == 0.0f) {
                            this.pagedownButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                        }
                        this.pagedownButton.setVisibility(attach_photo);
                        this.pagedownButton.setTag(Integer.valueOf(attach_gallery));
                        fArr = new float[attach_gallery];
                        fArr[attach_photo] = 0.0f;
                        this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", fArr).setDuration(200);
                        this.pagedownButtonAnimation.start();
                        return;
                    }
                    this.pagedownButton.setVisibility(attach_photo);
                    return;
                }
                return;
            }
            this.returnToMessageId = attach_photo;
            this.newUnreadMessageCount = attach_photo;
            if (this.pagedownButton.getTag() != null) {
                this.pagedownButton.setTag(null);
                if (this.pagedownButtonAnimation != null) {
                    this.pagedownButtonAnimation.cancel();
                    this.pagedownButtonAnimation = null;
                }
                if (animated) {
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = (float) AndroidUtilities.dp(100.0f);
                    this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", fArr).setDuration(200);
                    this.pagedownButtonAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationEnd(Animator animation) {
                            ChatActivity.this.pagedownButtonCounter.setVisibility(ChatActivity.attach_document);
                            ChatActivity.this.pagedownButton.setVisibility(ChatActivity.attach_document);
                        }
                    });
                    this.pagedownButtonAnimation.start();
                    return;
                }
                this.pagedownButton.setVisibility(attach_document);
            }
        }
    }

    private void updateSecretStatus() {
        if (this.bottomOverlay != null) {
            if (this.currentEncryptedChat == null || this.secretViewStatusTextView == null) {
                this.bottomOverlay.setVisibility(attach_document);
                return;
            }
            boolean hideKeyboard = false;
            if (this.currentEncryptedChat instanceof TL_encryptedChatRequested) {
                this.bottomOverlayText.setText(LocaleController.getString("EncryptionProcessing", C0691R.string.EncryptionProcessing));
                this.bottomOverlay.setVisibility(attach_photo);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat instanceof TL_encryptedChatWaiting) {
                TextView textView = this.bottomOverlayText;
                Object[] objArr = new Object[attach_gallery];
                objArr[attach_photo] = "<b>" + this.currentUser.first_name + "</b>";
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AwaitingEncryption", C0691R.string.AwaitingEncryption, objArr)));
                this.bottomOverlay.setVisibility(attach_photo);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat instanceof TL_encryptedChatDiscarded) {
                this.bottomOverlayText.setText(LocaleController.getString("EncryptionRejected", C0691R.string.EncryptionRejected));
                this.bottomOverlay.setVisibility(attach_photo);
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                DraftQuery.cleanDraft(this.dialog_id, false);
                hideKeyboard = true;
            } else if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                this.bottomOverlay.setVisibility(attach_document);
            }
            checkRaiseSensors();
            if (hideKeyboard) {
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
            checkActionBarMenu();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
    }

    private void checkActionBarMenu() {
        if ((this.currentEncryptedChat == null || (this.currentEncryptedChat instanceof TL_encryptedChat)) && ((this.currentChat == null || !ChatObject.isNotInChat(this.currentChat)) && (this.currentUser == null || !UserObject.isDeleted(this.currentUser)))) {
            if (this.menuItem != null) {
                this.menuItem.setVisibility(attach_photo);
            }
            if (this.timeItem2 != null) {
                this.timeItem2.setVisibility(attach_photo);
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.showTimeItem();
            }
        } else {
            if (this.menuItem != null) {
                this.menuItem.setVisibility(8);
            }
            if (this.timeItem2 != null) {
                this.timeItem2.setVisibility(8);
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.hideTimeItem();
            }
        }
        if (!(this.avatarContainer == null || this.currentEncryptedChat == null)) {
            this.avatarContainer.setTime(this.currentEncryptedChat.ttl);
        }
        checkAndUpdateAvatar();
    }

    private int getMessageType(MessageObject messageObject) {
        if (messageObject == null) {
            return -1;
        }
        InputStickerSet inputStickerSet;
        boolean canSave;
        String mime;
        if (this.currentEncryptedChat == null) {
            boolean isBroadcastError;
            if (this.isBroadcast && messageObject.getId() <= 0 && messageObject.isSendError()) {
                isBroadcastError = true;
            } else {
                isBroadcastError = false;
            }
            if ((this.isBroadcast || messageObject.getId() > 0 || !messageObject.isOut()) && !isBroadcastError) {
                if (messageObject.type == attach_location) {
                    return -1;
                }
                if (messageObject.type == copy || messageObject.type == forward) {
                    if (messageObject.getId() == 0) {
                        return -1;
                    }
                    return attach_gallery;
                } else if (messageObject.isVoice()) {
                    return attach_video;
                } else {
                    if (messageObject.isSticker()) {
                        inputStickerSet = messageObject.getInputStickerSet();
                        if (inputStickerSet instanceof TL_inputStickerSetID) {
                            if (!StickersQuery.isStickerPackInstalled(inputStickerSet.id)) {
                                return 7;
                            }
                        } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                            return 7;
                        }
                    } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                        canSave = false;
                        if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                            canSave = true;
                        }
                        if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                            canSave = true;
                        }
                        if (canSave) {
                            if (messageObject.getDocument() != null) {
                                mime = messageObject.getDocument().mime_type;
                                if (mime != null) {
                                    if (mime.endsWith("/xml")) {
                                        return attach_contact;
                                    }
                                    if (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) {
                                        return attach_location;
                                    }
                                }
                            }
                            return attach_document;
                        }
                    } else if (messageObject.type == delete) {
                        return 8;
                    } else {
                        if (messageObject.isMediaEmpty()) {
                            return attach_audio;
                        }
                    }
                    return attach_video;
                }
            } else if (!messageObject.isSendError()) {
                return -1;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return edit_done;
                }
                return attach_photo;
            }
        } else if (messageObject.isSending()) {
            return -1;
        } else {
            if (messageObject.type == attach_location) {
                return -1;
            }
            if (messageObject.isSendError()) {
                if (messageObject.isMediaEmpty()) {
                    return edit_done;
                }
                return attach_photo;
            } else if (messageObject.type == copy || messageObject.type == forward) {
                if (messageObject.getId() == 0 || messageObject.isSending()) {
                    return -1;
                }
                return attach_gallery;
            } else if (messageObject.isVoice()) {
                return attach_video;
            } else {
                if (messageObject.isSticker()) {
                    inputStickerSet = messageObject.getInputStickerSet();
                    if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                        return 7;
                    }
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                    canSave = false;
                    if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                        canSave = true;
                    }
                    if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        canSave = true;
                    }
                    if (canSave) {
                        if (messageObject.getDocument() != null) {
                            mime = messageObject.getDocument().mime_type;
                            if (mime != null && mime.endsWith("text/xml")) {
                                return attach_contact;
                            }
                        }
                        if (messageObject.messageOwner.ttl <= 0) {
                            return attach_document;
                        }
                    }
                } else if (messageObject.type == delete) {
                    return 8;
                } else {
                    if (messageObject.isMediaEmpty()) {
                        return attach_audio;
                    }
                }
                return attach_video;
            }
        }
    }

    private void addToSelectedMessages(MessageObject messageObject) {
        int index = messageObject.getDialogId() == this.dialog_id ? attach_photo : attach_gallery;
        if (this.selectedMessagesIds[index].containsKey(Integer.valueOf(messageObject.getId()))) {
            this.selectedMessagesIds[index].remove(Integer.valueOf(messageObject.getId()));
            if (messageObject.type == 0 || messageObject.caption != null) {
                this.selectedMessagesCanCopyIds[index].remove(Integer.valueOf(messageObject.getId()));
            }
            if (!messageObject.canDeleteMessage(this.currentChat)) {
                this.cantDeleteMessagesCount--;
            }
        } else {
            this.selectedMessagesIds[index].put(Integer.valueOf(messageObject.getId()), messageObject);
            if (messageObject.type == 0 || messageObject.caption != null) {
                this.selectedMessagesCanCopyIds[index].put(Integer.valueOf(messageObject.getId()), messageObject);
            }
            if (!messageObject.canDeleteMessage(this.currentChat)) {
                this.cantDeleteMessagesCount += attach_gallery;
            }
        }
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (this.selectedMessagesIds[attach_photo].isEmpty() && this.selectedMessagesIds[attach_gallery].isEmpty()) {
            this.actionBar.hideActionMode();
            updatePinnedMessageView(true);
            return;
        }
        int copyVisible = this.actionBar.createActionMode().getItem(copy).getVisibility();
        this.actionBar.createActionMode().getItem(copy).setVisibility(this.selectedMessagesCanCopyIds[attach_photo].size() + this.selectedMessagesCanCopyIds[attach_gallery].size() != 0 ? attach_photo : 8);
        int newCopyVisible = this.actionBar.createActionMode().getItem(copy).getVisibility();
        this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? attach_photo : 8);
        ActionBarMenuItem replyItem = this.actionBar.createActionMode().getItem(reply);
        if (replyItem != null) {
            boolean allowChatActions = true;
            if ((this.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) || this.isBroadcast || (this.currentChat != null && (ChatObject.isNotInChat(this.currentChat) || !(!ChatObject.isChannel(this.currentChat) || this.currentChat.creator || this.currentChat.editor || this.currentChat.megagroup)))) {
                allowChatActions = false;
            }
            int newVisibility = (allowChatActions && this.selectedMessagesIds[attach_photo].size() + this.selectedMessagesIds[attach_gallery].size() == attach_gallery) ? attach_photo : 8;
            if (replyItem.getVisibility() != newVisibility) {
                if (this.replyButtonAnimation != null) {
                    this.replyButtonAnimation.cancel();
                }
                if (copyVisible != newCopyVisible) {
                    if (newVisibility == 0) {
                        replyItem.setAlpha(TouchHelperCallback.ALPHA_FULL);
                        replyItem.setScaleX(TouchHelperCallback.ALPHA_FULL);
                    } else {
                        replyItem.setAlpha(0.0f);
                        replyItem.setScaleX(0.0f);
                    }
                    replyItem.setVisibility(newVisibility);
                    return;
                }
                this.replyButtonAnimation = new AnimatorSet();
                replyItem.setPivotX((float) AndroidUtilities.dp(54.0f));
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                float[] fArr;
                if (newVisibility == 0) {
                    replyItem.setVisibility(newVisibility);
                    animatorSet = this.replyButtonAnimation;
                    animatorArr = new Animator[attach_video];
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                    animatorArr[attach_photo] = ObjectAnimator.ofFloat(replyItem, "alpha", fArr);
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                    animatorArr[attach_gallery] = ObjectAnimator.ofFloat(replyItem, "scaleX", fArr);
                    animatorSet.playTogether(animatorArr);
                } else {
                    animatorSet = this.replyButtonAnimation;
                    animatorArr = new Animator[attach_video];
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = 0.0f;
                    animatorArr[attach_photo] = ObjectAnimator.ofFloat(replyItem, "alpha", fArr);
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = 0.0f;
                    animatorArr[attach_gallery] = ObjectAnimator.ofFloat(replyItem, "scaleX", fArr);
                    animatorSet.playTogether(animatorArr);
                }
                this.replyButtonAnimation.setDuration(100);
                this.replyButtonAnimation.addListener(new AnonymousClass57(newVisibility, replyItem));
                this.replyButtonAnimation.start();
            }
        }
    }

    private void processRowSelect(View view) {
        MessageObject message = null;
        if (view instanceof ChatMessageCell) {
            message = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            message = ((ChatActionCell) view).getMessageObject();
        }
        int type = getMessageType(message);
        if (type >= attach_video && type != edit_done) {
            addToSelectedMessages(message);
            updateActionModeTitle();
            updateVisibleRows();
        }
    }

    private void updateActionModeTitle() {
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (!this.selectedMessagesIds[attach_photo].isEmpty() || !this.selectedMessagesIds[attach_gallery].isEmpty()) {
            this.selectedMessagesCountTextView.setNumber(this.selectedMessagesIds[attach_photo].size() + this.selectedMessagesIds[attach_gallery].size(), true);
        }
    }

    private void updateTitle() {
        if (this.avatarContainer != null) {
            if (this.currentChat != null) {
                this.avatarContainer.setTitle(this.currentChat.title);
            } else if (this.currentUser == null) {
            } else {
                if (this.currentUser.id / id_chat_compose_panel == 777 || this.currentUser.id / id_chat_compose_panel == 333 || ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null || (ContactsController.getInstance().contactsDict.size() == 0 && ContactsController.getInstance().isLoadingContacts())) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else {
                    this.avatarContainer.setTitle(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
                }
            }
        }
    }

    private void updateBotButtons() {
        if (this.headerItem != null && this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            boolean hasHelp = false;
            boolean hasSettings = false;
            if (!this.botInfo.isEmpty()) {
                for (Entry<Integer, BotInfo> entry : this.botInfo.entrySet()) {
                    BotInfo info = (BotInfo) entry.getValue();
                    for (int a = attach_photo; a < info.commands.size(); a += attach_gallery) {
                        TL_botCommand command = (TL_botCommand) info.commands.get(a);
                        if (command.command.toLowerCase().equals("help")) {
                            hasHelp = true;
                        } else if (command.command.toLowerCase().equals("settings")) {
                            hasSettings = true;
                        }
                        if (hasSettings && hasHelp) {
                            break;
                        }
                    }
                }
            }
            if (hasHelp) {
                this.headerItem.showSubItem(bot_help);
            } else {
                this.headerItem.hideSubItem(bot_help);
            }
            if (hasSettings) {
                this.headerItem.showSubItem(bot_settings);
            } else {
                this.headerItem.hideSubItem(bot_settings);
            }
        }
    }

    private void updateTitleIcons() {
        int i = attach_photo;
        if (this.avatarContainer != null) {
            int rightIcon;
            if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                rightIcon = C0691R.drawable.mute_fixed;
            } else {
                rightIcon = attach_photo;
            }
            ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
            if (this.currentEncryptedChat != null) {
                i = C0691R.drawable.ic_lock_header;
            }
            chatAvatarContainer.setTitleIcons(i, rightIcon);
            if (rightIcon != 0) {
                this.muteItem.setText(LocaleController.getString("UnmuteNotifications", C0691R.string.UnmuteNotifications));
            } else {
                this.muteItem.setText(LocaleController.getString("MuteNotifications", C0691R.string.MuteNotifications));
            }
        }
    }

    private void checkAndUpdateAvatar() {
        if (this.currentUser != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
            } else {
                return;
            }
        } else if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
            if (chat != null) {
                this.currentChat = chat;
            } else {
                return;
            }
        }
        if (this.avatarContainer != null) {
            this.avatarContainer.checkAndUpdateAvatar();
        }
    }

    public boolean openVideoEditor(String videoPath, boolean removeLast, boolean animated) {
        Bundle args = new Bundle();
        args.putString("videoPath", videoPath);
        VideoEditorActivity fragment = new VideoEditorActivity(args);
        fragment.setDelegate(new VideoEditorActivityDelegate() {
            public void didFinishEditVideo(String videoPath, long startTime, long endTime, int resultWidth, int resultHeight, int rotationValue, int originalWidth, int originalHeight, int bitrate, long estimatedSize, long estimatedDuration) {
                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.startTime = startTime;
                videoEditedInfo.endTime = endTime;
                videoEditedInfo.rotationValue = rotationValue;
                videoEditedInfo.originalWidth = originalWidth;
                videoEditedInfo.originalHeight = originalHeight;
                videoEditedInfo.bitrate = bitrate;
                videoEditedInfo.resultWidth = resultWidth;
                videoEditedInfo.resultHeight = resultHeight;
                videoEditedInfo.originalPath = videoPath;
                SendMessagesHelper.prepareSendingVideo(videoPath, estimatedSize, estimatedDuration, resultWidth, resultHeight, videoEditedInfo, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
            }
        });
        if (this.parentLayout == null || !fragment.onFragmentCreate()) {
            SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, attach_photo, attach_photo, null, this.dialog_id, this.replyingMessageObject);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
            return false;
        }
        this.parentLayout.presentFragment(fragment, removeLast, !animated, true);
        return true;
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", C0691R.string.UnsupportedAttachment), attach_photo).show();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 0) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            ArrayList<Object> arrayList = new ArrayList();
            int orientation = attach_photo;
            try {
                switch (new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", attach_gallery)) {
                    case attach_audio /*3*/:
                        orientation = 180;
                        break;
                    case attach_location /*6*/:
                        orientation = 90;
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        orientation = 270;
                        break;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            arrayList.add(new PhotoEntry(attach_photo, attach_photo, 0, this.currentPicturePath, orientation, false));
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, attach_photo, attach_video, new AnonymousClass59(arrayList), this);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (requestCode == attach_gallery) {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            uri = data.getData();
            if (uri.toString().contains(MimeTypes.BASE_TYPE_VIDEO)) {
                videoPath = null;
                try {
                    videoPath = AndroidUtilities.getPath(uri);
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                if (videoPath == null) {
                    showAttachmentError();
                }
                if (VERSION.SDK_INT < delete_chat) {
                    SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, attach_photo, attach_photo, null, this.dialog_id, this.replyingMessageObject);
                } else if (this.paused) {
                    this.startVideoEdit = videoPath;
                } else {
                    openVideoEditor(videoPath, false, false);
                }
            } else {
                SendMessagesHelper.prepareSendingPhoto(null, uri, this.dialog_id, this.replyingMessageObject, null);
            }
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (requestCode == attach_video) {
            videoPath = null;
            FileLog.m10d("tmessages", "pic path " + this.currentPicturePath);
            if (!(data == null || this.currentPicturePath == null || !new File(this.currentPicturePath).exists())) {
                data = null;
            }
            if (data != null) {
                uri = data.getData();
                if (uri != null) {
                    FileLog.m10d("tmessages", "video record uri " + uri.toString());
                    videoPath = AndroidUtilities.getPath(uri);
                    FileLog.m10d("tmessages", "resolved path = " + videoPath);
                    if (!new File(videoPath).exists()) {
                        videoPath = this.currentPicturePath;
                    }
                } else {
                    videoPath = this.currentPicturePath;
                }
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                this.currentPicturePath = null;
            }
            if (videoPath == null && this.currentPicturePath != null) {
                if (new File(this.currentPicturePath).exists()) {
                    videoPath = this.currentPicturePath;
                }
                this.currentPicturePath = null;
            }
            if (VERSION.SDK_INT < delete_chat) {
                SendMessagesHelper.prepareSendingVideo(videoPath, 0, 0, attach_photo, attach_photo, null, this.dialog_id, this.replyingMessageObject);
                showReplyPanel(false, null, null, null, false, true);
                DraftQuery.cleanDraft(this.dialog_id, true);
            } else if (this.paused) {
                this.startVideoEdit = videoPath;
            } else {
                openVideoEditor(videoPath, false, false);
            }
        } else if (requestCode == report) {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            uri = data.getData();
            String extractUriFrom = uri.toString();
            if (extractUriFrom.contains("com.google.android.apps.photos.contentprovider")) {
                try {
                    String firstExtraction = extractUriFrom.split("/1/")[attach_gallery];
                    int index = firstExtraction.indexOf("/ACTUAL");
                    if (index != -1) {
                        String substring = firstExtraction.substring(attach_photo, index);
                        uri = Uri.parse(URLDecoder.decode(firstExtraction, C0747C.UTF8_NAME));
                    }
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                }
            }
            String tempPath = AndroidUtilities.getPath(uri);
            String originalPath = tempPath;
            if (tempPath == null) {
                originalPath = data.toString();
                tempPath = MediaController.copyFileToCache(data.getData(), "file");
            }
            if (tempPath == null) {
                showAttachmentError();
                return;
            }
            SendMessagesHelper.prepareSendingDocument(tempPath, originalPath, null, null, this.dialog_id, this.replyingMessageObject);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (requestCode != bot_settings) {
        } else {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            uri = data.getData();
            Cursor c = null;
            try {
                ContentResolver contentResolver = getParentActivity().getContentResolver();
                String[] strArr = new String[attach_video];
                strArr[attach_photo] = "display_name";
                strArr[attach_gallery] = "data1";
                c = contentResolver.query(uri, strArr, null, null, null);
                if (c != null) {
                    boolean sent = false;
                    while (c.moveToNext()) {
                        sent = true;
                        String name = c.getString(attach_photo);
                        String number = c.getString(attach_gallery);
                        User user = new User();
                        user.first_name = name;
                        user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                        user.phone = number;
                        SendMessagesHelper.getInstance().sendMessage(user, this.dialog_id, this.replyingMessageObject, null, null);
                    }
                    if (sent) {
                        showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(this.dialog_id, true);
                    }
                }
                if (c != null) {
                    try {
                        if (!c.isClosed()) {
                            c.close();
                        }
                    } catch (Throwable e222) {
                        FileLog.m13e("tmessages", e222);
                    }
                }
            } catch (Throwable th) {
                if (c != null) {
                    try {
                        if (!c.isClosed()) {
                            c.close();
                        }
                    } catch (Throwable e2222) {
                        FileLog.m13e("tmessages", e2222);
                    }
                }
            }
        }
    }

    public void saveSelfArgs(Bundle args) {
        if (this.currentPicturePath != null) {
            args.putString("path", this.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.currentPicturePath = args.getString("path");
    }

    private void removeUnreadPlane() {
        if (this.unreadMessageObject != null) {
            boolean[] zArr = this.forwardEndReached;
            this.forwardEndReached[attach_gallery] = true;
            zArr[attach_photo] = true;
            this.first_unread_id = attach_photo;
            this.last_message_id = attach_photo;
            this.unread_to_load = attach_photo;
            removeMessageObject(this.unreadMessageObject);
            this.unreadMessageObject = null;
        }
    }

    public boolean processSendingText(String text) {
        return this.chatActivityEnterView.processSendingText(text);
    }

    public void didReceivedNotification(int id, Object... args) {
        int index;
        ArrayList<MessageObject> messArr;
        int a;
        int loadIndex;
        int count;
        boolean z;
        MessageObject obj;
        ArrayList<MessageObject> dayArray;
        Message dateMsg;
        MessageObject messageObject;
        if (id == NotificationCenter.messagesDidLoaded) {
            if (((Integer) args[copy]).intValue() == this.classGuid) {
                if (!this.openAnimationEnded) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int[] iArr = new int[attach_document];
                    iArr[attach_photo] = NotificationCenter.chatInfoDidLoaded;
                    iArr[attach_gallery] = NotificationCenter.dialogsNeedReload;
                    iArr[attach_video] = NotificationCenter.closeChats;
                    iArr[attach_audio] = NotificationCenter.botKeyboardDidLoaded;
                    instance.setAllowedNotificationsDutingAnimation(iArr);
                }
                index = this.waitingForLoad.indexOf(Integer.valueOf(((Integer) args[forward]).intValue()));
                if (index != -1) {
                    this.waitingForLoad.remove(index);
                    messArr = args[attach_video];
                    if (this.waitingForReplyMessageLoad) {
                        boolean found = false;
                        for (a = attach_photo; a < messArr.size(); a += attach_gallery) {
                            if (((MessageObject) messArr.get(a)).getId() == this.startLoadFromMessageId) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            int startLoadFrom = this.startLoadFromMessageId;
                            boolean needSelect = this.needSelectFromMessageId;
                            clearChatData();
                            this.startLoadFromMessageId = startLoadFrom;
                            this.needSelectFromMessageId = needSelect;
                        } else {
                            this.startLoadFromMessageId = attach_photo;
                            return;
                        }
                    }
                    this.loadsCount += attach_gallery;
                    loadIndex = ((Long) args[attach_photo]).longValue() == this.dialog_id ? attach_photo : attach_gallery;
                    count = ((Integer) args[attach_gallery]).intValue();
                    boolean isCache = ((Boolean) args[attach_audio]).booleanValue();
                    int fnid = ((Integer) args[attach_document]).intValue();
                    int last_unread_date = ((Integer) args[7]).intValue();
                    int load_type = ((Integer) args[8]).intValue();
                    boolean wasUnread = false;
                    if (fnid != 0) {
                        this.first_unread_id = fnid;
                        this.last_message_id = ((Integer) args[attach_contact]).intValue();
                        this.unread_to_load = ((Integer) args[attach_location]).intValue();
                    } else if (this.startLoadFromMessageId != 0 && load_type == attach_audio) {
                        this.last_message_id = ((Integer) args[attach_contact]).intValue();
                    }
                    int newRowsCount = attach_photo;
                    boolean[] zArr = this.forwardEndReached;
                    z = this.startLoadFromMessageId == 0 && this.last_message_id == 0;
                    zArr[loadIndex] = z;
                    if ((load_type == attach_gallery || load_type == attach_audio) && loadIndex == attach_gallery) {
                        boolean[] zArr2 = this.endReached;
                        this.cacheEndReached[attach_photo] = true;
                        zArr2[attach_photo] = true;
                        this.forwardEndReached[attach_photo] = false;
                        this.minMessageId[attach_photo] = attach_photo;
                    }
                    if (this.loadsCount == attach_gallery && messArr.size() > edit_done) {
                        this.loadsCount += attach_gallery;
                    }
                    if (this.firstLoading) {
                        if (!this.forwardEndReached[loadIndex]) {
                            this.messages.clear();
                            this.messagesByDays.clear();
                            for (a = attach_photo; a < attach_video; a += attach_gallery) {
                                this.messagesDict[a].clear();
                                if (this.currentEncryptedChat == null) {
                                    this.maxMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    this.minMessageId[a] = LinearLayoutManager.INVALID_OFFSET;
                                } else {
                                    this.maxMessageId[a] = LinearLayoutManager.INVALID_OFFSET;
                                    this.minMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                }
                                this.maxDate[a] = LinearLayoutManager.INVALID_OFFSET;
                                this.minDate[a] = attach_photo;
                            }
                        }
                        this.firstLoading = false;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.parentLayout != null) {
                                    ChatActivity.this.parentLayout.resumeDelayedFragmentAnimation();
                                }
                            }
                        });
                    }
                    if (load_type == attach_gallery) {
                        Collections.reverse(messArr);
                    }
                    if (this.currentEncryptedChat == null) {
                        MessagesQuery.loadReplyMessagesForMessages(messArr, this.dialog_id);
                    }
                    int approximateHeightSum = attach_photo;
                    for (a = attach_photo; a < messArr.size(); a += attach_gallery) {
                        obj = (MessageObject) messArr.get(a);
                        approximateHeightSum += obj.getApproximateHeight();
                        if (this.currentUser != null && this.currentUser.bot && obj.isOut()) {
                            obj.setIsRead();
                        }
                        if (!this.messagesDict[loadIndex].containsKey(Integer.valueOf(obj.getId()))) {
                            if (loadIndex == attach_gallery) {
                                obj.setIsRead();
                            }
                            if (loadIndex == 0 && ChatObject.isChannel(this.currentChat) && obj.getId() == attach_gallery) {
                                this.endReached[loadIndex] = true;
                                this.cacheEndReached[loadIndex] = true;
                            }
                            if (obj.getId() > 0) {
                                this.maxMessageId[loadIndex] = Math.min(obj.getId(), this.maxMessageId[loadIndex]);
                                this.minMessageId[loadIndex] = Math.max(obj.getId(), this.minMessageId[loadIndex]);
                            } else if (this.currentEncryptedChat != null) {
                                this.maxMessageId[loadIndex] = Math.max(obj.getId(), this.maxMessageId[loadIndex]);
                                this.minMessageId[loadIndex] = Math.min(obj.getId(), this.minMessageId[loadIndex]);
                            }
                            if (obj.messageOwner.date != 0) {
                                this.maxDate[loadIndex] = Math.max(this.maxDate[loadIndex], obj.messageOwner.date);
                                if (this.minDate[loadIndex] == 0 || obj.messageOwner.date < this.minDate[loadIndex]) {
                                    this.minDate[loadIndex] = obj.messageOwner.date;
                                }
                            }
                            if (obj.type >= 0 && !(loadIndex == attach_gallery && (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo))) {
                                if (!obj.isOut() && obj.isUnread()) {
                                    wasUnread = true;
                                }
                                this.messagesDict[loadIndex].put(Integer.valueOf(obj.getId()), obj);
                                dayArray = (ArrayList) this.messagesByDays.get(obj.dateKey);
                                if (dayArray == null) {
                                    dayArray = new ArrayList();
                                    this.messagesByDays.put(obj.dateKey, dayArray);
                                    dateMsg = new Message();
                                    dateMsg.message = LocaleController.formatDateChat((long) obj.messageOwner.date);
                                    dateMsg.id = attach_photo;
                                    dateMsg.date = obj.messageOwner.date;
                                    messageObject = new MessageObject(dateMsg, null, false);
                                    messageObject.type = copy;
                                    messageObject.contentType = attach_gallery;
                                    if (load_type == attach_gallery) {
                                        this.messages.add(attach_photo, messageObject);
                                    } else {
                                        this.messages.add(messageObject);
                                    }
                                    newRowsCount += attach_gallery;
                                }
                                newRowsCount += attach_gallery;
                                if (load_type == attach_gallery) {
                                    dayArray.add(obj);
                                    this.messages.add(attach_photo, obj);
                                }
                                if (load_type != attach_gallery) {
                                    dayArray.add(obj);
                                    this.messages.add(this.messages.size() - 1, obj);
                                }
                                if (obj.getId() == this.last_message_id) {
                                    this.forwardEndReached[loadIndex] = true;
                                }
                                if (load_type == attach_video && obj.getId() == this.first_unread_id) {
                                    if (approximateHeightSum > AndroidUtilities.displaySize.y / attach_video || !this.forwardEndReached[attach_photo]) {
                                        dateMsg = new Message();
                                        dateMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                                        dateMsg.id = attach_photo;
                                        messageObject = new MessageObject(dateMsg, null, false);
                                        messageObject.type = attach_location;
                                        messageObject.contentType = attach_video;
                                        this.messages.add(this.messages.size() - 1, messageObject);
                                        this.unreadMessageObject = messageObject;
                                        this.scrollToMessage = this.unreadMessageObject;
                                        this.scrollToMessagePosition = -10000;
                                        newRowsCount += attach_gallery;
                                    }
                                } else if (load_type == attach_audio && obj.getId() == this.startLoadFromMessageId) {
                                    if (this.needSelectFromMessageId) {
                                        this.highlightMessageId = obj.getId();
                                    } else {
                                        this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                    this.scrollToMessage = obj;
                                    this.startLoadFromMessageId = attach_photo;
                                    if (this.scrollToMessagePosition == -10000) {
                                        this.scrollToMessagePosition = -9000;
                                    }
                                }
                            }
                        }
                    }
                    if (load_type == 0 && newRowsCount == 0) {
                        this.loadsCount--;
                    }
                    if (this.forwardEndReached[loadIndex] && loadIndex != attach_gallery) {
                        this.first_unread_id = attach_photo;
                        this.last_message_id = attach_photo;
                    }
                    if (this.loadsCount <= attach_video && !isCache) {
                        updateSpamView();
                    }
                    int firstVisPos;
                    int top;
                    View firstVisView;
                    if (load_type == attach_gallery) {
                        if (!(messArr.size() == count || isCache)) {
                            this.forwardEndReached[loadIndex] = true;
                            if (loadIndex != attach_gallery) {
                                this.first_unread_id = attach_photo;
                                this.last_message_id = attach_photo;
                                this.chatAdapter.notifyItemRemoved(this.chatAdapter.getItemCount() - 1);
                                newRowsCount--;
                            }
                            this.startLoadFromMessageId = attach_photo;
                        }
                        if (newRowsCount > 0) {
                            firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
                            top = attach_photo;
                            if (firstVisPos != this.chatLayoutManager.getItemCount() - 1) {
                                firstVisPos = -1;
                            } else {
                                firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                                top = (firstVisView == null ? attach_photo : firstVisView.getTop()) - this.chatListView.getPaddingTop();
                            }
                            this.chatAdapter.notifyItemRangeInserted(this.chatAdapter.getItemCount() - 1, newRowsCount);
                            if (firstVisPos != -1) {
                                this.chatLayoutManager.scrollToPositionWithOffset(firstVisPos, top);
                            }
                        }
                        this.loadingForward = false;
                    } else {
                        if (messArr.size() < count && load_type != attach_audio) {
                            if (isCache) {
                                if (this.currentEncryptedChat != null || this.isBroadcast) {
                                    this.endReached[loadIndex] = true;
                                }
                                if (load_type != attach_video) {
                                    this.cacheEndReached[loadIndex] = true;
                                }
                            } else if (load_type != attach_video) {
                                this.endReached[loadIndex] = true;
                            }
                        }
                        this.loading = false;
                        if (this.chatListView != null) {
                            if (this.first || this.scrollToTopOnResume || this.forceScrollToTop) {
                                this.forceScrollToTop = false;
                                this.chatAdapter.notifyDataSetChanged();
                                if (this.scrollToMessage != null) {
                                    int yOffset;
                                    if (this.scrollToMessagePosition == -9000) {
                                        yOffset = Math.max(attach_photo, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / attach_video);
                                    } else if (this.scrollToMessagePosition == -10000) {
                                        yOffset = attach_photo;
                                    } else {
                                        yOffset = this.scrollToMessagePosition;
                                    }
                                    if (!this.messages.isEmpty()) {
                                        if (this.messages.get(this.messages.size() - 1) == this.scrollToMessage || this.messages.get(this.messages.size() - 2) == this.scrollToMessage) {
                                            this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.isBot ? attach_gallery : attach_photo, ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
                                        } else {
                                            this.chatLayoutManager.scrollToPositionWithOffset(((this.chatAdapter.messagesStartRow + this.messages.size()) - this.messages.indexOf(this.scrollToMessage)) - 1, ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
                                        }
                                    }
                                    this.chatListView.invalidate();
                                    if (this.scrollToMessagePosition == -10000 || this.scrollToMessagePosition == -9000) {
                                        showPagedownButton(true, true);
                                    }
                                    this.scrollToMessagePosition = -10000;
                                    this.scrollToMessage = null;
                                } else {
                                    moveScrollToLastMessage();
                                }
                            } else if (newRowsCount != 0) {
                                boolean end = false;
                                if (this.endReached[loadIndex] && ((loadIndex == 0 && this.mergeDialogId == 0) || loadIndex == attach_gallery)) {
                                    end = true;
                                    this.chatAdapter.notifyItemRangeChanged(this.chatAdapter.isBot ? attach_gallery : attach_photo, attach_video);
                                }
                                firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
                                firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
                                top = (firstVisView == null ? attach_photo : firstVisView.getTop()) - this.chatListView.getPaddingTop();
                                if (newRowsCount - (end ? attach_gallery : attach_photo) > 0) {
                                    this.chatAdapter.notifyItemRangeInserted((this.chatAdapter.isBot ? attach_video : attach_gallery) + (end ? attach_photo : attach_gallery), newRowsCount - (end ? attach_gallery : attach_photo));
                                }
                                if (firstVisPos != -1) {
                                    int i;
                                    LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                                    int i2 = firstVisPos + newRowsCount;
                                    if (end) {
                                        i = attach_gallery;
                                    } else {
                                        i = attach_photo;
                                    }
                                    linearLayoutManager.scrollToPositionWithOffset(i2 - i, top);
                                }
                            } else if (this.endReached[loadIndex] && ((loadIndex == 0 && this.mergeDialogId == 0) || loadIndex == attach_gallery)) {
                                this.chatAdapter.notifyItemRemoved(this.chatAdapter.isBot ? attach_gallery : attach_photo);
                            }
                            if (this.paused) {
                                this.scrollToTopOnResume = true;
                                if (this.scrollToMessage != null) {
                                    this.scrollToTopUnReadOnResume = true;
                                }
                            }
                            if (this.first && this.chatListView != null) {
                                this.chatListView.setEmptyView(this.emptyViewContainer);
                            }
                        } else {
                            this.scrollToTopOnResume = true;
                            if (this.scrollToMessage != null) {
                                this.scrollToTopUnReadOnResume = true;
                            }
                        }
                    }
                    if (this.first && this.messages.size() > 0) {
                        if (loadIndex == 0) {
                            AndroidUtilities.runOnUIThread(new AnonymousClass61(((MessageObject) this.messages.get(attach_photo)).getId(), last_unread_date, wasUnread), 700);
                        }
                        this.first = false;
                    }
                    if (this.messages.isEmpty() && this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                        this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                        updateBottomOverlay();
                    }
                    if (newRowsCount == 0 && this.currentEncryptedChat != null && !this.endReached[attach_photo]) {
                        this.first = true;
                        if (this.chatListView != null) {
                            this.chatListView.setEmptyView(null);
                        }
                        if (this.emptyViewContainer != null) {
                            this.emptyViewContainer.setVisibility(attach_document);
                        }
                    } else if (this.progressView != null) {
                        this.progressView.setVisibility(attach_document);
                    }
                    checkScrollForLoad(false);
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            if (this.chatListView != null) {
                this.chatListView.invalidateViews();
            }
            if (this.replyObjectTextView != null) {
                this.replyObjectTextView.invalidate();
            }
            if (this.alertTextView != null) {
                this.alertTextView.invalidate();
            }
            if (this.pinnedMessageTextView != null) {
                this.pinnedMessageTextView.invalidate();
            }
            if (this.mentionListView != null) {
                this.mentionListView.invalidateViews();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            Chat chat;
            int updateMask = ((Integer) args[attach_photo]).intValue();
            if (!((updateMask & attach_gallery) == 0 && (updateMask & delete_chat) == 0)) {
                if (this.currentChat != null) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                    if (chat != null) {
                        this.currentChat = chat;
                    }
                } else if (this.currentUser != null) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                    if (user != null) {
                        this.currentUser = user;
                    }
                }
                updateTitle();
            }
            boolean updateSubtitle = false;
            if (!((updateMask & 32) == 0 && (updateMask & attach_document) == 0)) {
                if (!(this.currentChat == null || this.avatarContainer == null)) {
                    this.avatarContainer.updateOnlineCount();
                }
                updateSubtitle = true;
            }
            if (!((updateMask & attach_video) == 0 && (updateMask & 8) == 0 && (updateMask & attach_gallery) == 0)) {
                checkAndUpdateAvatar();
                updateVisibleRows();
            }
            if ((updateMask & 64) != 0) {
                updateSubtitle = true;
            }
            if ((updateMask & MessagesController.UPDATE_MASK_CHANNEL) != 0 && ChatObject.isChannel(this.currentChat)) {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                if (chat != null) {
                    this.currentChat = chat;
                    updateSubtitle = true;
                    updateBottomOverlay();
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setDialogId(this.dialog_id);
                    }
                } else {
                    return;
                }
            }
            if (this.avatarContainer != null && updateSubtitle) {
                this.avatarContainer.updateSubtitle();
            }
            if ((updateMask & MessagesController.UPDATE_MASK_USER_PHONE) != 0) {
                updateContactStatus();
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[attach_photo]).longValue() == this.dialog_id) {
                Object[] objArr;
                boolean updateChat = false;
                boolean hasFromMe = false;
                ArrayList<MessageObject> arr = args[attach_gallery];
                if (this.currentEncryptedChat != null && arr.size() == attach_gallery) {
                    obj = (MessageObject) arr.get(attach_photo);
                    if (this.currentEncryptedChat != null && obj.isOut() && obj.messageOwner.action != null && (obj.messageOwner.action instanceof TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) && getParentActivity() != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < share_contact && this.currentEncryptedChat.ttl > 0 && this.currentEncryptedChat.ttl <= 60) {
                        r0 = new Builder(getParentActivity());
                        r0.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        r0.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        objArr = new Object[attach_video];
                        objArr[attach_photo] = this.currentUser.first_name;
                        objArr[attach_gallery] = this.currentUser.first_name;
                        r0.setMessage(LocaleController.formatString("CompatibilityChat", C0691R.string.CompatibilityChat, objArr));
                        showDialog(r0.create());
                    }
                }
                if (!(this.currentChat == null && this.inlineReturn == 0)) {
                    for (a = attach_photo; a < arr.size(); a += attach_gallery) {
                        messageObject = (MessageObject) arr.get(a);
                        if (this.currentChat != null) {
                            if (((messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) && messageObject.messageOwner.action.user_id == UserConfig.getClientUserId()) || ((messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) && messageObject.messageOwner.action.users.contains(Integer.valueOf(UserConfig.getClientUserId())))) {
                                Chat newChat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
                                if (newChat != null) {
                                    this.currentChat = newChat;
                                    checkActionBarMenu();
                                    updateBottomOverlay();
                                    if (this.avatarContainer != null) {
                                        this.avatarContainer.updateSubtitle();
                                    }
                                }
                            } else if (messageObject.messageOwner.reply_to_msg_id != 0 && messageObject.replyMessageObject == null) {
                                messageObject.replyMessageObject = (MessageObject) this.messagesDict[attach_photo].get(Integer.valueOf(messageObject.messageOwner.reply_to_msg_id));
                                if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                                    messageObject.generatePinMessageText(null, null);
                                }
                            }
                        } else if (!(this.inlineReturn == 0 || messageObject.messageOwner.reply_markup == null)) {
                            for (b = attach_photo; b < messageObject.messageOwner.reply_markup.rows.size(); b += attach_gallery) {
                                TL_keyboardButtonRow row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(b);
                                for (int c = attach_photo; c < row.buttons.size(); c += attach_gallery) {
                                    KeyboardButton button = (KeyboardButton) row.buttons.get(c);
                                    if (button instanceof TL_keyboardButtonSwitchInline) {
                                        processSwitchButton((TL_keyboardButtonSwitchInline) button);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                boolean reloadMegagroup = false;
                Bundle bundle;
                if (this.forwardEndReached[attach_photo]) {
                    boolean markAsRead = false;
                    boolean unreadUpdated = true;
                    int oldCount = this.messages.size();
                    int addedCount = attach_photo;
                    HashMap<String, ArrayList<MessageObject>> webpagesToReload = null;
                    int placeToPaste = -1;
                    for (a = attach_photo; a < arr.size(); a += attach_gallery) {
                        obj = (MessageObject) arr.get(a);
                        if (a == 0) {
                            if (obj.messageOwner.id < 0) {
                                placeToPaste = attach_photo;
                            } else if (this.messages.isEmpty()) {
                                placeToPaste = attach_photo;
                            } else {
                                int size = this.messages.size();
                                for (b = attach_photo; b < size; b += attach_gallery) {
                                    MessageObject lastMessage = (MessageObject) this.messages.get(b);
                                    if (lastMessage.type >= 0 && lastMessage.messageOwner.date > 0) {
                                        if (lastMessage.messageOwner.id <= 0 || obj.messageOwner.id <= 0) {
                                            if (lastMessage.messageOwner.date < obj.messageOwner.date) {
                                                placeToPaste = b;
                                                break;
                                            }
                                        } else if (lastMessage.messageOwner.id < obj.messageOwner.id) {
                                            placeToPaste = b;
                                            break;
                                        }
                                    }
                                }
                                if (placeToPaste == -1 || placeToPaste > this.messages.size()) {
                                    placeToPaste = this.messages.size();
                                }
                            }
                        }
                        if (this.currentUser != null && this.currentUser.bot && obj.isOut()) {
                            obj.setIsRead();
                        }
                        if (!(this.avatarContainer == null || this.currentEncryptedChat == null || obj.messageOwner.action == null || !(obj.messageOwner.action instanceof TL_messageEncryptedAction) || !(obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            this.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (obj.type >= 0 && !this.messagesDict[attach_photo].containsKey(Integer.valueOf(obj.getId()))) {
                            if (this.currentEncryptedChat != null && (obj.messageOwner.media instanceof TL_messageMediaWebPage) && (obj.messageOwner.media.webpage instanceof TL_webPageUrlPending)) {
                                if (webpagesToReload == null) {
                                    webpagesToReload = new HashMap();
                                }
                                ArrayList<MessageObject> arrayList = (ArrayList) webpagesToReload.get(obj.messageOwner.media.webpage.url);
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    webpagesToReload.put(obj.messageOwner.media.webpage.url, arrayList);
                                }
                                arrayList.add(obj);
                            }
                            obj.checkLayout();
                            if (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                                bundle = new Bundle();
                                bundle.putInt("chat_id", obj.messageOwner.action.channel_id);
                                AndroidUtilities.runOnUIThread(new AnonymousClass63(this.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) : null, bundle, obj.messageOwner.action.channel_id));
                                return;
                            }
                            if (this.currentChat != null && this.currentChat.megagroup && ((obj.messageOwner.action instanceof TL_messageActionChatAddUser) || (obj.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                                reloadMegagroup = true;
                            }
                            if (this.minDate[attach_photo] == 0 || obj.messageOwner.date < this.minDate[attach_photo]) {
                                this.minDate[attach_photo] = obj.messageOwner.date;
                            }
                            if (obj.isOut()) {
                                removeUnreadPlane();
                                hasFromMe = true;
                            }
                            if (obj.getId() > 0) {
                                this.maxMessageId[attach_photo] = Math.min(obj.getId(), this.maxMessageId[attach_photo]);
                                this.minMessageId[attach_photo] = Math.max(obj.getId(), this.minMessageId[attach_photo]);
                            } else if (this.currentEncryptedChat != null) {
                                this.maxMessageId[attach_photo] = Math.max(obj.getId(), this.maxMessageId[attach_photo]);
                                this.minMessageId[attach_photo] = Math.min(obj.getId(), this.minMessageId[attach_photo]);
                            }
                            this.maxDate[attach_photo] = Math.max(this.maxDate[attach_photo], obj.messageOwner.date);
                            this.messagesDict[attach_photo].put(Integer.valueOf(obj.getId()), obj);
                            dayArray = (ArrayList) this.messagesByDays.get(obj.dateKey);
                            if (dayArray == null) {
                                dayArray = new ArrayList();
                                this.messagesByDays.put(obj.dateKey, dayArray);
                                dateMsg = new Message();
                                dateMsg.message = LocaleController.formatDateChat((long) obj.messageOwner.date);
                                dateMsg.id = attach_photo;
                                dateMsg.date = obj.messageOwner.date;
                                messageObject = new MessageObject(dateMsg, null, false);
                                messageObject.type = copy;
                                messageObject.contentType = attach_gallery;
                                this.messages.add(placeToPaste, messageObject);
                                addedCount += attach_gallery;
                            }
                            if (!obj.isOut()) {
                                if (this.paused && placeToPaste == 0) {
                                    if (!(this.scrollToTopUnReadOnResume || this.unreadMessageObject == null)) {
                                        removeMessageObject(this.unreadMessageObject);
                                        this.unreadMessageObject = null;
                                    }
                                    if (this.unreadMessageObject == null) {
                                        dateMsg = new Message();
                                        dateMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                                        dateMsg.id = attach_photo;
                                        messageObject = new MessageObject(dateMsg, null, false);
                                        messageObject.type = attach_location;
                                        messageObject.contentType = attach_video;
                                        this.messages.add(attach_photo, messageObject);
                                        this.unreadMessageObject = messageObject;
                                        this.scrollToMessage = this.unreadMessageObject;
                                        this.scrollToMessagePosition = -10000;
                                        unreadUpdated = false;
                                        this.unread_to_load = attach_photo;
                                        this.scrollToTopUnReadOnResume = true;
                                        addedCount += attach_gallery;
                                    }
                                }
                                if (this.unreadMessageObject != null) {
                                    this.unread_to_load += attach_gallery;
                                    unreadUpdated = true;
                                }
                                if (obj.isUnread()) {
                                    if (!this.paused) {
                                        obj.setIsRead();
                                    }
                                    markAsRead = true;
                                }
                            }
                            dayArray.add(attach_photo, obj);
                            this.messages.add(placeToPaste, obj);
                            addedCount += attach_gallery;
                            this.newUnreadMessageCount += attach_gallery;
                            if (obj.type == copy || obj.type == forward) {
                                updateChat = true;
                            }
                        }
                    }
                    if (webpagesToReload != null) {
                        MessagesController.getInstance().reloadWebPages(this.dialog_id, webpagesToReload);
                    }
                    if (this.progressView != null) {
                        this.progressView.setVisibility(attach_document);
                    }
                    if (this.chatAdapter != null) {
                        if (unreadUpdated) {
                            this.chatAdapter.updateRowWithMessageObject(this.unreadMessageObject);
                        }
                        if (addedCount != 0) {
                            this.chatAdapter.notifyItemRangeInserted(this.chatAdapter.getItemCount() - placeToPaste, addedCount);
                        }
                    } else {
                        this.scrollToTopOnResume = true;
                    }
                    if (this.chatListView == null || this.chatAdapter == null) {
                        this.scrollToTopOnResume = true;
                    } else {
                        int lastVisible = this.chatLayoutManager.findLastVisibleItemPosition();
                        if (lastVisible == -1) {
                            lastVisible = attach_photo;
                        }
                        if (this.endReached[attach_photo]) {
                            lastVisible += attach_gallery;
                        }
                        if (this.chatAdapter.isBot) {
                            oldCount += attach_gallery;
                        }
                        if (lastVisible >= oldCount || hasFromMe) {
                            this.newUnreadMessageCount = attach_photo;
                            if (!this.firstLoading) {
                                if (this.paused) {
                                    this.scrollToTopOnResume = true;
                                } else {
                                    this.forceScrollToTop = true;
                                    moveScrollToLastMessage();
                                }
                            }
                        } else {
                            if (!(this.newUnreadMessageCount == 0 || this.pagedownButtonCounter == null)) {
                                this.pagedownButtonCounter.setVisibility(attach_photo);
                                TextView textView = this.pagedownButtonCounter;
                                objArr = new Object[attach_gallery];
                                objArr[attach_photo] = Integer.valueOf(this.newUnreadMessageCount);
                                textView.setText(String.format("%d", objArr));
                            }
                            showPagedownButton(true, true);
                        }
                    }
                    if (markAsRead) {
                        if (this.paused) {
                            this.readWhenResume = true;
                            this.readWithDate = this.maxDate[attach_photo];
                            this.readWithMid = this.minMessageId[attach_photo];
                        } else {
                            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(attach_photo)).getId(), this.minMessageId[attach_photo], this.maxDate[attach_photo], true, false);
                        }
                    }
                } else {
                    int currentMaxDate = LinearLayoutManager.INVALID_OFFSET;
                    int currentMinMsgId = LinearLayoutManager.INVALID_OFFSET;
                    if (this.currentEncryptedChat != null) {
                        currentMinMsgId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    boolean currentMarkAsRead = false;
                    for (a = attach_photo; a < arr.size(); a += attach_gallery) {
                        obj = (MessageObject) arr.get(a);
                        if (this.currentUser != null && this.currentUser.bot && obj.isOut()) {
                            obj.setIsRead();
                        }
                        if (!(this.avatarContainer == null || this.currentEncryptedChat == null || obj.messageOwner.action == null || !(obj.messageOwner.action instanceof TL_messageEncryptedAction) || !(obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            this.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (obj.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                            bundle = new Bundle();
                            bundle.putInt("chat_id", obj.messageOwner.action.channel_id);
                            AndroidUtilities.runOnUIThread(new AnonymousClass62(this.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) : null, bundle, obj.messageOwner.action.channel_id));
                            return;
                        }
                        if (this.currentChat != null && this.currentChat.megagroup && ((obj.messageOwner.action instanceof TL_messageActionChatAddUser) || (obj.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                            reloadMegagroup = true;
                        }
                        if (obj.isOut() && obj.isSending()) {
                            scrollToLastMessage(false);
                            return;
                        }
                        if (obj.type >= 0 && !this.messagesDict[attach_photo].containsKey(Integer.valueOf(obj.getId()))) {
                            obj.checkLayout();
                            currentMaxDate = Math.max(currentMaxDate, obj.messageOwner.date);
                            if (obj.getId() > 0) {
                                currentMinMsgId = Math.max(obj.getId(), currentMinMsgId);
                                this.last_message_id = Math.max(this.last_message_id, obj.getId());
                            } else if (this.currentEncryptedChat != null) {
                                currentMinMsgId = Math.min(obj.getId(), currentMinMsgId);
                                this.last_message_id = Math.min(this.last_message_id, obj.getId());
                            }
                            if (!obj.isOut() && obj.isUnread()) {
                                this.unread_to_load += attach_gallery;
                                currentMarkAsRead = true;
                            }
                            if (obj.type == copy || obj.type == forward) {
                                updateChat = true;
                            }
                        }
                    }
                    if (currentMarkAsRead) {
                        if (this.paused) {
                            this.readWhenResume = true;
                            this.readWithDate = currentMaxDate;
                            this.readWithMid = currentMinMsgId;
                        } else if (this.messages.size() > 0) {
                            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(attach_photo)).getId(), currentMinMsgId, currentMaxDate, true, false);
                        }
                    }
                    updateVisibleRows();
                }
                if (!(this.messages.isEmpty() || this.botUser == null || this.botUser.length() != 0)) {
                    this.botUser = null;
                    updateBottomOverlay();
                }
                if (updateChat) {
                    updateTitle();
                    checkAndUpdateAvatar();
                }
                if (reloadMegagroup) {
                    MessagesController.getInstance().loadFullChat(this.currentChat.id, attach_photo, true);
                }
            }
        } else if (id == NotificationCenter.closeChats) {
            if (args == null || args.length <= 0) {
                removeSelfFromStack();
            } else if (((Long) args[attach_photo]).longValue() == this.dialog_id) {
                finishFragment();
            }
        } else if (id == NotificationCenter.messagesRead) {
            SparseArray<Long> inbox = args[attach_photo];
            SparseArray<Long> outbox = args[attach_gallery];
            updated = false;
            b = attach_photo;
            while (b < inbox.size()) {
                key = inbox.keyAt(b);
                long messageId = ((Long) inbox.get(key)).longValue();
                if (((long) key) != this.dialog_id) {
                    b += attach_gallery;
                } else {
                    for (a = attach_photo; a < this.messages.size(); a += attach_gallery) {
                        obj = (MessageObject) this.messages.get(a);
                        if (!obj.isOut() && obj.getId() > 0 && obj.getId() <= ((int) messageId)) {
                            if (!obj.isUnread()) {
                                break;
                            }
                            obj.setIsRead();
                            updated = true;
                        }
                    }
                    while (b < outbox.size()) {
                        key = outbox.keyAt(b);
                        messageId = (int) ((Long) outbox.get(key)).longValue();
                        if (((long) key) == this.dialog_id) {
                        } else {
                            for (a = attach_photo; a < this.messages.size(); a += attach_gallery) {
                                obj = (MessageObject) this.messages.get(a);
                                if (obj.isOut() && obj.getId() > 0 && obj.getId() <= messageId) {
                                    if (obj.isUnread()) {
                                        break;
                                    }
                                    obj.setIsRead();
                                    updated = true;
                                }
                            }
                            if (updated) {
                                updateVisibleRows();
                            }
                        }
                    }
                    if (updated) {
                        updateVisibleRows();
                    }
                }
            }
            for (b = attach_photo; b < outbox.size(); b += attach_gallery) {
                key = outbox.keyAt(b);
                messageId = (int) ((Long) outbox.get(key)).longValue();
                if (((long) key) == this.dialog_id) {
                    while (a < this.messages.size()) {
                        obj = (MessageObject) this.messages.get(a);
                        if (obj.isUnread()) {
                            obj.setIsRead();
                            updated = true;
                        } else {
                            break;
                            if (updated) {
                                updateVisibleRows();
                            }
                        }
                    }
                    if (updated) {
                        updateVisibleRows();
                    }
                }
            }
            if (updated) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            ArrayList<Integer> markAsDeletedMessages = args[attach_photo];
            int channelId = ((Integer) args[attach_gallery]).intValue();
            loadIndex = attach_photo;
            if (ChatObject.isChannel(this.currentChat)) {
                if (channelId == 0 && this.mergeDialogId != 0) {
                    loadIndex = attach_gallery;
                } else if (channelId == this.currentChat.id) {
                    loadIndex = attach_photo;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            updated = false;
            for (a = attach_photo; a < markAsDeletedMessages.size(); a += attach_gallery) {
                Integer ids = (Integer) markAsDeletedMessages.get(a);
                obj = (MessageObject) this.messagesDict[loadIndex].get(ids);
                if (loadIndex == 0 && this.info != null && this.info.pinned_msg_id == ids.intValue()) {
                    this.pinnedMessageObject = null;
                    this.info.pinned_msg_id = attach_photo;
                    MessagesStorage.getInstance().updateChannelPinnedMessage(channelId, attach_photo);
                    updatePinnedMessageView(true);
                }
                if (obj != null) {
                    index = this.messages.indexOf(obj);
                    if (index != -1) {
                        this.messages.remove(index);
                        this.messagesDict[loadIndex].remove(ids);
                        dayArr = (ArrayList) this.messagesByDays.get(obj.dateKey);
                        if (dayArr != null) {
                            dayArr.remove(obj);
                            if (dayArr.isEmpty()) {
                                this.messagesByDays.remove(obj.dateKey);
                                if (index >= 0 && index < this.messages.size()) {
                                    this.messages.remove(index);
                                }
                            }
                        }
                        updated = true;
                    }
                }
            }
            if (this.messages.isEmpty()) {
                if (this.endReached[attach_photo] || this.loading) {
                    if (this.botButtons != null) {
                        this.botButtons = null;
                        if (this.chatActivityEnterView != null) {
                            this.chatActivityEnterView.setButtons(null, false);
                        }
                    }
                    if (this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                        this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                        updateBottomOverlay();
                    }
                } else {
                    int[] iArr2;
                    if (this.progressView != null) {
                        this.progressView.setVisibility(attach_document);
                    }
                    if (this.chatListView != null) {
                        this.chatListView.setEmptyView(null);
                    }
                    if (this.currentEncryptedChat == null) {
                        iArr2 = this.maxMessageId;
                        this.maxMessageId[attach_gallery] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        iArr2[attach_photo] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        iArr2 = this.minMessageId;
                        this.minMessageId[attach_gallery] = LinearLayoutManager.INVALID_OFFSET;
                        iArr2[attach_photo] = LinearLayoutManager.INVALID_OFFSET;
                    } else {
                        iArr2 = this.maxMessageId;
                        this.maxMessageId[attach_gallery] = LinearLayoutManager.INVALID_OFFSET;
                        iArr2[attach_photo] = LinearLayoutManager.INVALID_OFFSET;
                        iArr2 = this.minMessageId;
                        this.minMessageId[attach_gallery] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        iArr2[attach_photo] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    iArr2 = this.maxDate;
                    this.maxDate[attach_gallery] = LinearLayoutManager.INVALID_OFFSET;
                    iArr2[attach_photo] = LinearLayoutManager.INVALID_OFFSET;
                    iArr2 = this.minDate;
                    this.minDate[attach_gallery] = attach_photo;
                    iArr2[attach_photo] = attach_photo;
                    this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                    r11 = MessagesController.getInstance();
                    r12 = this.dialog_id;
                    boolean z2 = !this.cacheEndReached[attach_photo];
                    int i3 = this.minDate[attach_photo];
                    r18 = this.classGuid;
                    r21 = ChatObject.isChannel(this.currentChat);
                    r22 = this.lastLoadIndex;
                    this.lastLoadIndex = r22 + attach_gallery;
                    r11.loadMessages(r12, bot_help, attach_photo, z2, i3, r18, attach_photo, attach_photo, r21, r22);
                    this.loading = true;
                }
            }
            if (updated && this.chatAdapter != null) {
                removeUnreadPlane();
                this.chatAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[attach_photo];
            obj = (MessageObject) this.messagesDict[attach_photo].get(msgId);
            if (obj != null) {
                Integer newMsgId = args[attach_gallery];
                if (newMsgId.equals(msgId) || !this.messagesDict[attach_photo].containsKey(newMsgId)) {
                    Message newMsgObj = args[attach_video];
                    mediaUpdated = false;
                    try {
                        mediaUpdated = (obj.messageOwner.params != null && obj.messageOwner.params.containsKey("query_id")) || !(newMsgObj.media == null || obj.messageOwner.media == null || newMsgObj.media.getClass().equals(obj.messageOwner.media.getClass()));
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    if (newMsgObj != null) {
                        obj.messageOwner = newMsgObj;
                        obj.generateThumbs(true);
                        obj.setType();
                    }
                    this.messagesDict[attach_photo].remove(msgId);
                    this.messagesDict[attach_photo].put(newMsgId, obj);
                    obj.messageOwner.id = newMsgId.intValue();
                    obj.messageOwner.send_state = attach_photo;
                    obj.forceUpdate = mediaUpdated;
                    messArr = new ArrayList();
                    messArr.add(obj);
                    if (this.currentEncryptedChat == null) {
                        MessagesQuery.loadReplyMessagesForMessages(messArr, this.dialog_id);
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.updateRowWithMessageObject(obj);
                    }
                    if (this.chatLayoutManager != null && mediaUpdated && this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - 1) {
                        moveScrollToLastMessage();
                    }
                    NotificationsController.getInstance().playOutChatSound();
                    return;
                }
                MessageObject removed = (MessageObject) this.messagesDict[attach_photo].remove(msgId);
                if (removed != null) {
                    index = this.messages.indexOf(removed);
                    this.messages.remove(index);
                    dayArr = (ArrayList) this.messagesByDays.get(removed.dateKey);
                    dayArr.remove(obj);
                    if (dayArr.isEmpty()) {
                        this.messagesByDays.remove(obj.dateKey);
                        if (index >= 0 && index < this.messages.size()) {
                            this.messages.remove(index);
                        }
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.messageReceivedByAck) {
            obj = (MessageObject) this.messagesDict[attach_photo].get((Integer) args[attach_photo]);
            if (obj != null) {
                obj.messageOwner.send_state = attach_photo;
                if (this.chatAdapter != null) {
                    this.chatAdapter.updateRowWithMessageObject(obj);
                }
            }
        } else if (id == NotificationCenter.messageSendError) {
            obj = (MessageObject) this.messagesDict[attach_photo].get((Integer) args[attach_photo]);
            if (obj != null) {
                obj.messageOwner.send_state = attach_video;
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[attach_photo];
            if (this.currentChat != null && chatFull.id == this.currentChat.id) {
                if (chatFull instanceof TL_channelFull) {
                    if (this.currentChat.megagroup) {
                        int lastDate = attach_photo;
                        if (chatFull.participants != null) {
                            for (a = attach_photo; a < chatFull.participants.participants.size(); a += attach_gallery) {
                                lastDate = Math.max(((ChatParticipant) chatFull.participants.participants.get(a)).date, lastDate);
                            }
                        }
                        if (lastDate == 0 || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastDate)) > 3600) {
                            MessagesController.getInstance().loadChannelParticipants(Integer.valueOf(this.currentChat.id));
                        }
                    }
                    if (chatFull.participants == null && this.info != null) {
                        chatFull.participants = this.info.participants;
                    }
                }
                this.info = chatFull;
                if (this.mentionsAdapter != null) {
                    this.mentionsAdapter.setChatInfo(this.info);
                }
                if (args[attach_audio] instanceof MessageObject) {
                    this.pinnedMessageObject = (MessageObject) args[attach_audio];
                    updatePinnedMessageView(false);
                } else {
                    updatePinnedMessageView(true);
                }
                if (this.avatarContainer != null) {
                    this.avatarContainer.updateOnlineCount();
                    this.avatarContainer.updateSubtitle();
                }
                if (this.isBroadcast) {
                    SendMessagesHelper.getInstance().setCurrentChatInfo(this.info);
                }
                if (this.info instanceof TL_chatFull) {
                    this.hasBotsCommands = false;
                    this.botInfo.clear();
                    this.botsCount = attach_photo;
                    URLSpanBotCommand.enabled = false;
                    for (a = attach_photo; a < this.info.participants.participants.size(); a += attach_gallery) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                        if (user != null && user.bot) {
                            URLSpanBotCommand.enabled = true;
                            this.botsCount += attach_gallery;
                            BotQuery.loadBotInfo(user.id, true, this.classGuid);
                        }
                    }
                    if (this.chatListView != null) {
                        this.chatListView.invalidateViews();
                    }
                } else if (this.info instanceof TL_channelFull) {
                    this.hasBotsCommands = false;
                    this.botInfo.clear();
                    this.botsCount = attach_photo;
                    URLSpanBotCommand.enabled = !this.info.bot_info.isEmpty();
                    this.botsCount = this.info.bot_info.size();
                    for (a = attach_photo; a < this.info.bot_info.size(); a += attach_gallery) {
                        BotInfo bot = (BotInfo) this.info.bot_info.get(a);
                        if (!bot.commands.isEmpty() && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                            this.hasBotsCommands = true;
                        }
                        this.botInfo.put(Integer.valueOf(bot.user_id), bot);
                    }
                    if (this.chatListView != null) {
                        this.chatListView.invalidateViews();
                    }
                    if (this.mentionsAdapter != null && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                        this.mentionsAdapter.setBotInfo(this.botInfo);
                    }
                }
                if (this.chatActivityEnterView != null) {
                    this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
                }
                if (this.mentionsAdapter != null) {
                    this.mentionsAdapter.setBotsCount(this.botsCount);
                }
                if (ChatObject.isChannel(this.currentChat) && this.mergeDialogId == 0 && this.info.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
                    this.maxMessageId[attach_gallery] = this.info.migrated_from_max_id;
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.chatInfoCantLoad) {
            int chatId = ((Integer) args[attach_photo]).intValue();
            if (this.currentChat != null && this.currentChat.id == chatId) {
                int reason = ((Integer) args[attach_gallery]).intValue();
                if (getParentActivity() != null && this.closeChatDialog == null) {
                    r0 = new Builder(getParentActivity());
                    r0.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    if (reason == 0) {
                        r0.setMessage(LocaleController.getString("ChannelCantOpenPrivate", C0691R.string.ChannelCantOpenPrivate));
                    } else if (reason == attach_gallery) {
                        r0.setMessage(LocaleController.getString("ChannelCantOpenNa", C0691R.string.ChannelCantOpenNa));
                    } else if (reason == attach_video) {
                        r0.setMessage(LocaleController.getString("ChannelCantOpenBanned", C0691R.string.ChannelCantOpenBanned));
                    }
                    r0.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                    Dialog create = r0.create();
                    this.closeChatDialog = create;
                    showDialog(create);
                    this.loading = false;
                    if (this.progressView != null) {
                        this.progressView.setVisibility(attach_document);
                    }
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateContactStatus();
            if (this.avatarContainer != null) {
                this.avatarContainer.updateSubtitle();
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat2 = args[attach_photo];
            if (this.currentEncryptedChat != null && chat2.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat2;
                updateContactStatus();
                updateSecretStatus();
                initStickers();
                if (this.chatActivityEnterView != null) {
                    ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
                    z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
                    boolean z3 = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                    chatActivityEnterView.setAllowStickersAndGifs(z, z3);
                }
                if (this.mentionsAdapter != null) {
                    MentionsAdapter mentionsAdapter = this.mentionsAdapter;
                    z = !this.chatActivityEnterView.isEditingMessage() && (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46);
                    mentionsAdapter.setNeedBotContext(z);
                }
            }
        } else if (id == NotificationCenter.messagesReadEncrypted) {
            int encId = ((Integer) args[attach_photo]).intValue();
            if (this.currentEncryptedChat != null && this.currentEncryptedChat.id == encId) {
                int date = ((Integer) args[attach_gallery]).intValue();
                i$ = this.messages.iterator();
                while (i$.hasNext()) {
                    obj = (MessageObject) i$.next();
                    if (obj.isOut()) {
                        if (obj.isOut() && !obj.isUnread()) {
                            break;
                        } else if (obj.messageOwner.date - 1 <= date) {
                            obj.setIsRead();
                        }
                    }
                }
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.audioDidReset || id == NotificationCenter.audioPlayStateChanged) {
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = attach_photo; a < count; a += attach_gallery) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        messageObject = cell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            cell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.audioProgressDidChanged) {
            Integer mid = args[attach_photo];
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = attach_photo; a < count; a += attach_gallery) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        if (cell.getMessageObject() != null && cell.getMessageObject().getId() == mid.intValue()) {
                            MessageObject playing = cell.getMessageObject();
                            MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                            if (player != null) {
                                playing.audioProgress = player.audioProgress;
                                playing.audioProgressSec = player.audioProgressSec;
                                cell.updateAudioProgress();
                                return;
                            }
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            if (this.dialog_id == ((Long) args[attach_photo]).longValue()) {
                this.messages.clear();
                this.waitingForLoad.clear();
                this.messagesByDays.clear();
                for (a = attach_gallery; a >= 0; a--) {
                    this.messagesDict[a].clear();
                    if (this.currentEncryptedChat == null) {
                        this.maxMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        this.minMessageId[a] = LinearLayoutManager.INVALID_OFFSET;
                    } else {
                        this.maxMessageId[a] = LinearLayoutManager.INVALID_OFFSET;
                        this.minMessageId[a] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    this.maxDate[a] = LinearLayoutManager.INVALID_OFFSET;
                    this.minDate[a] = attach_photo;
                    this.selectedMessagesIds[a].clear();
                    this.selectedMessagesCanCopyIds[a].clear();
                }
                this.cantDeleteMessagesCount = attach_photo;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                if (this.botButtons != null) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setButtons(null, false);
                    }
                }
                if (this.currentEncryptedChat == null && this.currentUser != null && this.currentUser.bot && this.botUser == null) {
                    this.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                    updateBottomOverlay();
                }
                if (((Boolean) args[attach_gallery]).booleanValue()) {
                    if (this.chatAdapter != null) {
                        this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? attach_photo : attach_document);
                        this.chatListView.setEmptyView(null);
                    }
                    for (a = attach_photo; a < attach_video; a += attach_gallery) {
                        this.endReached[a] = false;
                        this.cacheEndReached[a] = false;
                        this.forwardEndReached[a] = true;
                    }
                    this.first = true;
                    this.firstLoading = true;
                    this.loading = true;
                    this.startLoadFromMessageId = attach_photo;
                    this.needSelectFromMessageId = false;
                    this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                    r11 = MessagesController.getInstance();
                    r12 = this.dialog_id;
                    int i4 = AndroidUtilities.isTablet() ? bot_help : edit_done;
                    r18 = this.classGuid;
                    r21 = ChatObject.isChannel(this.currentChat);
                    r22 = this.lastLoadIndex;
                    this.lastLoadIndex = r22 + attach_gallery;
                    r11.loadMessages(r12, i4, attach_photo, true, attach_photo, r18, attach_video, attach_photo, r21, r22);
                } else if (this.progressView != null) {
                    this.progressView.setVisibility(attach_document);
                    this.chatListView.setEmptyView(this.emptyViewContainer);
                }
                if (this.chatAdapter != null) {
                    this.chatAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.screenshotTook) {
            updateInformationForScreenshotDetector();
        } else if (id == NotificationCenter.blockedUsersDidLoaded) {
            if (this.currentUser != null) {
                boolean oldValue = this.userBlocked;
                this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.currentUser.id));
                if (oldValue != this.userBlocked) {
                    updateBottomOverlay();
                }
            }
        } else if (id == NotificationCenter.FileNewChunkAvailable) {
            messageObject = (MessageObject) args[attach_photo];
            long finalSize = ((Long) args[attach_video]).longValue();
            if (finalSize != 0 && this.dialog_id == messageObject.getDialogId()) {
                MessageObject currentObject = (MessageObject) this.messagesDict[attach_photo].get(Integer.valueOf(messageObject.getId()));
                if (currentObject != null) {
                    currentObject.messageOwner.media.document.size = (int) finalSize;
                    updateVisibleRows();
                }
            }
        } else if (id == NotificationCenter.didCreatedNewDeleteTask) {
            SparseArray<ArrayList<Integer>> mids = args[attach_photo];
            changed = false;
            for (int i5 = attach_photo; i5 < mids.size(); i5 += attach_gallery) {
                key = mids.keyAt(i5);
                i$ = ((ArrayList) mids.get(key)).iterator();
                while (i$.hasNext()) {
                    messageObject = (MessageObject) this.messagesDict[attach_photo].get((Integer) i$.next());
                    if (messageObject != null) {
                        messageObject.messageOwner.destroyTime = key;
                        changed = true;
                    }
                }
            }
            if (changed) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.audioDidStarted) {
            sendSecretMessageRead((MessageObject) args[attach_photo]);
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = attach_photo; a < count; a += attach_gallery) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        MessageObject messageObject1 = cell.getMessageObject();
                        if (messageObject1 != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                            cell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.updateMessageMedia) {
            messageObject = (MessageObject) args[attach_photo];
            MessageObject existMessageObject = (MessageObject) this.messagesDict[attach_photo].get(Integer.valueOf(messageObject.getId()));
            if (existMessageObject != null) {
                existMessageObject.messageOwner.media = messageObject.messageOwner.media;
                existMessageObject.messageOwner.attachPath = messageObject.messageOwner.attachPath;
                existMessageObject.generateThumbs(false);
            }
            updateVisibleRows();
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            did = ((Long) args[attach_photo]).longValue();
            if (did == this.dialog_id || did == this.mergeDialogId) {
                loadIndex = did == this.dialog_id ? attach_photo : attach_gallery;
                changed = false;
                mediaUpdated = false;
                ArrayList<MessageObject> messageObjects = args[attach_gallery];
                for (a = attach_photo; a < messageObjects.size(); a += attach_gallery) {
                    messageObject = (MessageObject) messageObjects.get(a);
                    MessageObject old = (MessageObject) this.messagesDict[loadIndex].get(Integer.valueOf(messageObject.getId()));
                    if (this.pinnedMessageObject != null && this.pinnedMessageObject.getId() == messageObject.getId()) {
                        this.pinnedMessageObject = messageObject;
                        updatePinnedMessageView(true);
                    }
                    if (old != null) {
                        if (!mediaUpdated && (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                            mediaUpdated = true;
                        }
                        if (old.replyMessageObject != null) {
                            messageObject.replyMessageObject = old.replyMessageObject;
                        }
                        messageObject.messageOwner.attachPath = old.messageOwner.attachPath;
                        messageObject.attachPathExists = old.attachPathExists;
                        messageObject.mediaExists = old.mediaExists;
                        this.messagesDict[loadIndex].put(Integer.valueOf(old.getId()), messageObject);
                        index = this.messages.indexOf(old);
                        if (index >= 0) {
                            this.messages.set(index, messageObject);
                            if (this.chatAdapter != null) {
                                this.chatAdapter.notifyItemChanged(((this.chatAdapter.messagesStartRow + this.messages.size()) - index) - 1);
                            }
                            changed = true;
                        }
                    }
                }
                if (changed && this.chatLayoutManager != null && mediaUpdated) {
                    if (this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - (this.chatAdapter.isBot ? attach_video : attach_gallery)) {
                        moveScrollToLastMessage();
                    }
                }
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateTitleIcons();
            if (ChatObject.isChannel(this.currentChat)) {
                updateBottomOverlay();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            if (((Long) args[attach_photo]).longValue() == this.dialog_id) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.didLoadedPinnedMessage) {
            MessageObject message = args[attach_photo];
            if (message.getDialogId() == this.dialog_id && this.info != null && this.info.pinned_msg_id == message.getId()) {
                this.pinnedMessageObject = message;
                this.loadingPinnedMessage = attach_photo;
                updatePinnedMessageView(true);
            }
        } else if (id == NotificationCenter.didReceivedWebpages) {
            ArrayList<Message> arrayList2 = args[attach_photo];
            updated = false;
            for (a = attach_photo; a < arrayList2.size(); a += attach_gallery) {
                message = (Message) arrayList2.get(a);
                did = MessageObject.getDialogId(message);
                if (did == this.dialog_id || did == this.mergeDialogId) {
                    currentMessage = (MessageObject) this.messagesDict[did == this.dialog_id ? attach_photo : attach_gallery].get(Integer.valueOf(message.id));
                    if (currentMessage != null) {
                        currentMessage.messageOwner.media = new TL_messageMediaWebPage();
                        currentMessage.messageOwner.media.webpage = message.media.webpage;
                        currentMessage.generateThumbs(true);
                        updated = true;
                    }
                }
            }
            if (updated) {
                updateVisibleRows();
                if (this.chatLayoutManager != null && this.chatLayoutManager.findLastVisibleItemPosition() >= this.messages.size() - 1) {
                    moveScrollToLastMessage();
                }
            }
        } else if (id == NotificationCenter.didReceivedWebpagesInUpdates) {
            if (this.foundWebPage != null) {
                for (WebPage webPage : args[attach_photo].values()) {
                    if (webPage.id == this.foundWebPage.id) {
                        showReplyPanel(!(webPage instanceof TL_webPageEmpty), null, null, webPage, false, true);
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagesReadContent) {
            ArrayList<Long> arrayList3 = args[attach_photo];
            updated = false;
            for (a = attach_photo; a < arrayList3.size(); a += attach_gallery) {
                currentMessage = (MessageObject) this.messagesDict[this.mergeDialogId == 0 ? attach_photo : attach_gallery].get(Integer.valueOf((int) ((Long) arrayList3.get(a)).longValue()));
                if (currentMessage != null) {
                    currentMessage.setContentIsRead();
                    updated = true;
                }
            }
            if (updated) {
                updateVisibleRows();
            }
        } else if (id == NotificationCenter.botInfoDidLoaded) {
            if (this.classGuid == ((Integer) args[attach_gallery]).intValue()) {
                BotInfo info = args[attach_photo];
                if (this.currentEncryptedChat == null) {
                    if (!(info.commands.isEmpty() || ChatObject.isChannel(this.currentChat))) {
                        this.hasBotsCommands = true;
                    }
                    this.botInfo.put(Integer.valueOf(info.user_id), info);
                    if (this.chatAdapter != null) {
                        this.chatAdapter.notifyItemChanged(attach_photo);
                    }
                    if (this.mentionsAdapter != null && (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup))) {
                        this.mentionsAdapter.setBotInfo(this.botInfo);
                    }
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
                    }
                }
                updateBotButtons();
            }
        } else if (id == NotificationCenter.botKeyboardDidLoaded) {
            if (this.dialog_id == ((Long) args[attach_gallery]).longValue()) {
                message = (Message) args[attach_photo];
                if (message == null || this.userBlocked) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                            this.botReplyButtons = null;
                            showReplyPanel(false, null, null, null, false, true);
                        }
                        this.chatActivityEnterView.setButtons(this.botButtons);
                        return;
                    }
                    return;
                }
                this.botButtons = new MessageObject(message, null, false);
                if (this.chatActivityEnterView == null) {
                    return;
                }
                if (this.botButtons.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply) {
                    if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", attach_photo).getInt("answered_" + this.dialog_id, attach_photo) == this.botButtons.getId()) {
                        return;
                    }
                    if (this.replyingMessageObject == null || this.chatActivityEnterView.getFieldText() == null) {
                        this.botReplyButtons = this.botButtons;
                        this.chatActivityEnterView.setButtons(this.botButtons);
                        showReplyPanel(true, this.botButtons, null, null, false, true);
                        return;
                    }
                    return;
                }
                if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                    this.botReplyButtons = null;
                    showReplyPanel(false, null, null, null, false, true);
                }
                this.chatActivityEnterView.setButtons(this.botButtons);
            }
        } else if (id == NotificationCenter.chatSearchResultsAvailable) {
            if (this.classGuid == ((Integer) args[attach_photo]).intValue()) {
                messageId = ((Integer) args[attach_gallery]).intValue();
                did = ((Long) args[attach_audio]).longValue();
                if (messageId != 0) {
                    scrollToMessageId(messageId, attach_photo, true, did == this.dialog_id ? attach_photo : attach_gallery);
                }
                updateSearchButtons(((Integer) args[attach_video]).intValue(), ((Integer) args[attach_document]).intValue(), ((Integer) args[attach_contact]).intValue());
            }
        } else if (id == NotificationCenter.didUpdatedMessagesViews) {
            SparseIntArray array = (SparseIntArray) args[attach_photo].get((int) this.dialog_id);
            if (array != null) {
                updated = false;
                for (a = attach_photo; a < array.size(); a += attach_gallery) {
                    messageId = array.keyAt(a);
                    messageObject = (MessageObject) this.messagesDict[attach_photo].get(Integer.valueOf(messageId));
                    if (messageObject != null) {
                        int newValue = array.get(messageId);
                        if (newValue > messageObject.messageOwner.views) {
                            messageObject.messageOwner.views = newValue;
                            updated = true;
                        }
                    }
                }
                if (updated) {
                    updateVisibleRows();
                }
            }
        } else if (id == NotificationCenter.peerSettingsDidLoaded) {
            if (((Long) args[attach_photo]).longValue() == this.dialog_id) {
                updateSpamView();
            }
        } else if (id == NotificationCenter.newDraftReceived && ((Long) args[attach_photo]).longValue() == this.dialog_id) {
            applyDraftMaybe();
        }
    }

    public boolean processSwitchButton(TL_keyboardButtonSwitchInline button) {
        if (this.inlineReturn == 0) {
            return false;
        }
        String query = "@" + this.currentUser.username + " " + button.query;
        if (this.inlineReturn == this.dialog_id) {
            this.inlineReturn = 0;
            this.chatActivityEnterView.setFieldText(query);
        } else {
            DraftQuery.saveDraft(this.inlineReturn, query, null, null, false);
            if (this.parentLayout.fragmentsStack.size() > attach_gallery) {
                BaseFragment prevFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
                if ((prevFragment instanceof ChatActivity) && ((ChatActivity) prevFragment).dialog_id == this.inlineReturn) {
                    finishFragment();
                } else {
                    Bundle bundle = new Bundle();
                    int lower_part = (int) this.inlineReturn;
                    int high_part = (int) (this.inlineReturn >> 32);
                    if (lower_part == 0) {
                        bundle.putInt("enc_id", high_part);
                    } else if (lower_part > 0) {
                        bundle.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        bundle.putInt("chat_id", -lower_part);
                    }
                    presentFragment(new ChatActivity(bundle), true);
                }
            }
        }
        return true;
    }

    private void updateSearchButtons(int mask, int num, int count) {
        float f = TouchHelperCallback.ALPHA_FULL;
        if (this.searchUpButton != null) {
            boolean z;
            float f2;
            this.searchUpButton.setEnabled((mask & attach_gallery) != 0);
            ImageView imageView = this.searchDownButton;
            if ((mask & attach_video) != 0) {
                z = true;
            } else {
                z = false;
            }
            imageView.setEnabled(z);
            imageView = this.searchUpButton;
            if (this.searchUpButton.isEnabled()) {
                f2 = TouchHelperCallback.ALPHA_FULL;
            } else {
                f2 = 0.5f;
            }
            imageView.setAlpha(f2);
            ImageView imageView2 = this.searchDownButton;
            if (!this.searchDownButton.isEnabled()) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
            if (count == 0) {
                this.searchCountText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                return;
            }
            SimpleTextView simpleTextView = this.searchCountText;
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = Integer.valueOf(num + attach_gallery);
            objArr[attach_gallery] = Integer.valueOf(count);
            simpleTextView.setText(LocaleController.formatString("Of", C0691R.string.Of, objArr));
        }
    }

    public boolean needDelayOpenAnimation() {
        return this.firstLoading;
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        NotificationCenter instance = NotificationCenter.getInstance();
        int[] iArr = new int[attach_contact];
        iArr[attach_photo] = NotificationCenter.chatInfoDidLoaded;
        iArr[attach_gallery] = NotificationCenter.dialogsNeedReload;
        iArr[attach_video] = NotificationCenter.closeChats;
        iArr[attach_audio] = NotificationCenter.messagesDidLoaded;
        iArr[attach_document] = NotificationCenter.botKeyboardDidLoaded;
        instance.setAllowedNotificationsDutingAnimation(iArr);
        NotificationCenter.getInstance().setAnimationInProgress(true);
        if (isOpen) {
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance().setAnimationInProgress(false);
        if (isOpen) {
            this.openAnimationEnded = true;
            if (this.currentUser != null) {
                MessagesController.getInstance().loadFullUser(this.currentUser, this.classGuid, false);
            }
            if (VERSION.SDK_INT >= report) {
                createChatAttachView();
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.closeChatDialog != null && dialog == this.closeChatDialog) {
            MessagesController.getInstance().deleteDialog(this.dialog_id, attach_photo);
            if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
                finishFragment();
                return;
            }
            BaseFragment fragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);
            removeSelfFromStack();
            fragment.finishFragment();
        }
    }

    private void updateBottomOverlay() {
        if (this.bottomOverlayChatText != null) {
            if (this.currentChat != null) {
                if (!ChatObject.isChannel(this.currentChat) || (this.currentChat instanceof TL_channelForbidden)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", C0691R.string.DeleteThisGroup));
                } else if (ChatObject.isNotInChat(this.currentChat)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelJoin", C0691R.string.ChannelJoin));
                } else if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelUnmute", C0691R.string.ChannelUnmute));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelMute", C0691R.string.ChannelMute));
                }
            } else if (this.userBlocked) {
                if (this.currentUser.bot) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("BotUnblock", C0691R.string.BotUnblock));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("Unblock", C0691R.string.Unblock));
                }
                if (this.botButtons != null) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                            this.botReplyButtons = null;
                            showReplyPanel(false, null, null, null, false, true);
                        }
                        this.chatActivityEnterView.setButtons(this.botButtons, false);
                    }
                }
            } else if (this.botUser == null || !this.currentUser.bot) {
                this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisChat", C0691R.string.DeleteThisChat));
            } else {
                this.bottomOverlayChatText.setText(LocaleController.getString("BotStart", C0691R.string.BotStart));
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
            if (this.searchItem == null || this.searchItem.getVisibility() != 0) {
                this.searchContainer.setVisibility(attach_document);
                if ((this.currentChat == null || (!ChatObject.isNotInChat(this.currentChat) && ChatObject.canWriteToChat(this.currentChat))) && (this.currentUser == null || !(UserObject.isDeleted(this.currentUser) || this.userBlocked))) {
                    if (this.botUser == null || !this.currentUser.bot) {
                        this.chatActivityEnterView.setVisibility(attach_photo);
                        this.bottomOverlayChat.setVisibility(attach_document);
                    } else {
                        this.bottomOverlayChat.setVisibility(attach_photo);
                        this.chatActivityEnterView.setVisibility(attach_document);
                    }
                    this.muteItem.setVisibility(attach_photo);
                } else {
                    this.bottomOverlayChat.setVisibility(attach_photo);
                    this.muteItem.setVisibility(8);
                    this.chatActivityEnterView.setFieldFocused(false);
                    this.chatActivityEnterView.setVisibility(attach_document);
                }
            } else {
                this.searchContainer.setVisibility(attach_photo);
                this.bottomOverlayChat.setVisibility(attach_document);
                this.chatActivityEnterView.setFieldFocused(false);
                this.chatActivityEnterView.setVisibility(attach_document);
            }
            checkRaiseSensors();
        }
    }

    public void showAlert(User user, String message) {
        if (this.alertView != null && user != null && message != null) {
            if (this.alertView.getTag() != null) {
                this.alertView.setTag(null);
                if (this.alertViewAnimator != null) {
                    this.alertViewAnimator.cancel();
                    this.alertViewAnimator = null;
                }
                this.alertView.setVisibility(attach_photo);
                this.alertViewAnimator = new AnimatorSet();
                AnimatorSet animatorSet = this.alertViewAnimator;
                Animator[] animatorArr = new Animator[attach_gallery];
                float[] fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.0f;
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.alertView, "translationY", fArr);
                animatorSet.playTogether(animatorArr);
                this.alertViewAnimator.setDuration(200);
                this.alertViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }
                });
                this.alertViewAnimator.start();
            }
            this.alertNameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            this.alertTextView.setText(Emoji.replaceEmoji(message.replace('\n', ' '), this.alertTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            if (this.hideAlertViewRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.hideAlertViewRunnable);
            }
            Runnable anonymousClass65 = new Runnable() {

                /* renamed from: org.telegram.ui.ChatActivity.65.1 */
                class C18031 extends AnimatorListenerAdapterProxy {
                    C18031() {
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                            ChatActivity.this.alertView.setVisibility(8);
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animation)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }
                }

                public void run() {
                    if (ChatActivity.this.hideAlertViewRunnable == this && ChatActivity.this.alertView.getTag() == null) {
                        ChatActivity.this.alertView.setTag(Integer.valueOf(ChatActivity.attach_gallery));
                        if (ChatActivity.this.alertViewAnimator != null) {
                            ChatActivity.this.alertViewAnimator.cancel();
                            ChatActivity.this.alertViewAnimator = null;
                        }
                        ChatActivity.this.alertViewAnimator = new AnimatorSet();
                        AnimatorSet access$11700 = ChatActivity.this.alertViewAnimator;
                        Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                        float[] fArr = new float[ChatActivity.attach_gallery];
                        fArr[ChatActivity.attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                        animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.alertView, "translationY", fArr);
                        access$11700.playTogether(animatorArr);
                        ChatActivity.this.alertViewAnimator.setDuration(200);
                        ChatActivity.this.alertViewAnimator.addListener(new C18031());
                        ChatActivity.this.alertViewAnimator.start();
                    }
                }
            };
            this.hideAlertViewRunnable = anonymousClass65;
            AndroidUtilities.runOnUIThread(anonymousClass65, 3000);
        }
    }

    private void hidePinnedMessageView(boolean animated) {
        if (this.pinnedMessageView.getTag() == null) {
            this.pinnedMessageView.setTag(Integer.valueOf(attach_gallery));
            if (this.pinnedMessageViewAnimator != null) {
                this.pinnedMessageViewAnimator.cancel();
                this.pinnedMessageViewAnimator = null;
            }
            if (animated) {
                this.pinnedMessageViewAnimator = new AnimatorSet();
                AnimatorSet animatorSet = this.pinnedMessageViewAnimator;
                Animator[] animatorArr = new Animator[attach_gallery];
                float[] fArr = new float[attach_gallery];
                fArr[attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", fArr);
                animatorSet.playTogether(animatorArr);
                this.pinnedMessageViewAnimator.setDuration(200);
                this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                            ChatActivity.this.pinnedMessageView.setVisibility(8);
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }
                });
                this.pinnedMessageViewAnimator.start();
                return;
            }
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
        }
    }

    private void updatePinnedMessageView(boolean animated) {
        if (this.pinnedMessageView != null) {
            if (this.info != null) {
                if (!(this.pinnedMessageObject == null || this.info.pinned_msg_id == this.pinnedMessageObject.getId())) {
                    this.pinnedMessageObject = null;
                }
                if (this.info.pinned_msg_id != 0 && this.pinnedMessageObject == null) {
                    this.pinnedMessageObject = (MessageObject) this.messagesDict[attach_photo].get(Integer.valueOf(this.info.pinned_msg_id));
                }
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo);
            if (this.info == null || this.info.pinned_msg_id == 0 || this.info.pinned_msg_id == preferences.getInt("pin_" + this.dialog_id, attach_photo) || (this.actionBar != null && this.actionBar.isActionModeShowed())) {
                hidePinnedMessageView(animated);
            } else if (this.pinnedMessageObject != null) {
                if (this.pinnedMessageView.getTag() != null) {
                    this.pinnedMessageView.setTag(null);
                    if (this.pinnedMessageViewAnimator != null) {
                        this.pinnedMessageViewAnimator.cancel();
                        this.pinnedMessageViewAnimator = null;
                    }
                    if (animated) {
                        this.pinnedMessageView.setVisibility(attach_photo);
                        this.pinnedMessageViewAnimator = new AnimatorSet();
                        AnimatorSet animatorSet = this.pinnedMessageViewAnimator;
                        Animator[] animatorArr = new Animator[attach_gallery];
                        float[] fArr = new float[attach_gallery];
                        fArr[attach_photo] = 0.0f;
                        animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", fArr);
                        animatorSet.playTogether(animatorArr);
                        this.pinnedMessageViewAnimator.setDuration(200);
                        this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                                    ChatActivity.this.pinnedMessageViewAnimator = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animation)) {
                                    ChatActivity.this.pinnedMessageViewAnimator = null;
                                }
                            }
                        });
                        this.pinnedMessageViewAnimator.start();
                    } else {
                        this.pinnedMessageView.setTranslationY(0.0f);
                        this.pinnedMessageView.setVisibility(attach_photo);
                    }
                }
                this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", C0691R.string.PinnedMessage));
                if (this.pinnedMessageObject.messageText != null) {
                    String mess = this.pinnedMessageObject.messageText.toString();
                    if (mess.length() > 150) {
                        mess = mess.substring(attach_photo, 150);
                    }
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(mess.replace('\n', ' '), this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                }
            } else {
                hidePinnedMessageView(animated);
                if (this.loadingPinnedMessage != this.info.pinned_msg_id) {
                    this.loadingPinnedMessage = this.info.pinned_msg_id;
                    MessagesQuery.loadPinnedMessage(this.currentChat.id, this.info.pinned_msg_id, true);
                }
            }
            checkListViewPaddings();
        }
    }

    private void updateSpamView() {
        if (this.reportSpamView != null) {
            boolean show;
            if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo).getInt("spam3_" + this.dialog_id, attach_photo) == attach_video) {
                show = true;
            } else {
                show = false;
            }
            if (show) {
                if (this.messages.isEmpty()) {
                    show = false;
                } else {
                    int count = this.messages.size() - 1;
                    for (int a = count; a >= Math.max(count - 50, attach_photo); a--) {
                        if (((MessageObject) this.messages.get(a)).isOut()) {
                            show = false;
                            break;
                        }
                    }
                }
            }
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            float[] fArr;
            if (show) {
                if (this.reportSpamView.getTag() != null) {
                    this.reportSpamView.setTag(null);
                    this.reportSpamView.setVisibility(attach_photo);
                    if (this.reportSpamViewAnimator != null) {
                        this.reportSpamViewAnimator.cancel();
                    }
                    this.reportSpamViewAnimator = new AnimatorSet();
                    animatorSet = this.reportSpamViewAnimator;
                    animatorArr = new Animator[attach_gallery];
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = 0.0f;
                    animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", fArr);
                    animatorSet.playTogether(animatorArr);
                    this.reportSpamViewAnimator.setDuration(200);
                    this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }
                    });
                    this.reportSpamViewAnimator.start();
                }
            } else if (this.reportSpamView.getTag() == null) {
                this.reportSpamView.setTag(Integer.valueOf(attach_gallery));
                if (this.reportSpamViewAnimator != null) {
                    this.reportSpamViewAnimator.cancel();
                }
                this.reportSpamViewAnimator = new AnimatorSet();
                animatorSet = this.reportSpamViewAnimator;
                animatorArr = new Animator[attach_gallery];
                fArr = new float[attach_gallery];
                fArr[attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", fArr);
                animatorSet.playTogether(animatorArr);
                this.reportSpamViewAnimator.setDuration(200);
                this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                            ChatActivity.this.reportSpamView.setVisibility(8);
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animation)) {
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }
                });
                this.reportSpamViewAnimator.start();
            }
            checkListViewPaddings();
        }
    }

    private void updateContactStatus() {
        if (this.addContactItem != null) {
            if (this.currentUser == null) {
                this.addContactItem.setVisibility(8);
            } else {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                }
                if ((this.currentEncryptedChat != null && !(this.currentEncryptedChat instanceof TL_encryptedChat)) || this.currentUser.id / id_chat_compose_panel == 333 || this.currentUser.id / id_chat_compose_panel == 777 || UserObject.isDeleted(this.currentUser) || ContactsController.getInstance().isLoadingContacts() || (this.currentUser.phone != null && this.currentUser.phone.length() != 0 && ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null && (ContactsController.getInstance().contactsDict.size() != 0 || !ContactsController.getInstance().isLoadingContacts()))) {
                    this.addContactItem.setVisibility(8);
                } else {
                    this.addContactItem.setVisibility(attach_photo);
                    if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                        this.addContactItem.setText(LocaleController.getString("ShareMyContactInfo", C0691R.string.ShareMyContactInfo));
                        this.addToContactsButton.setVisibility(8);
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), attach_photo, AndroidUtilities.dp(50.0f), attach_photo);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, TouchHelperCallback.ALPHA_FULL, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)));
                    } else {
                        this.addContactItem.setText(LocaleController.getString("AddToContacts", C0691R.string.AddToContacts));
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(4.0f), attach_photo, AndroidUtilities.dp(50.0f), attach_photo);
                        this.addToContactsButton.setVisibility(attach_photo);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 0.5f, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)));
                    }
                }
            }
            checkListViewPaddings();
        }
    }

    private void checkListViewPaddings() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = ChatActivity.attach_photo;
                try {
                    int firstVisPos = ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                    int top = ChatActivity.attach_photo;
                    if (firstVisPos != -1) {
                        View firstVisView = ChatActivity.this.chatLayoutManager.findViewByPosition(firstVisPos);
                        if (firstVisView != null) {
                            i = firstVisView.getTop();
                        }
                        top = i - ChatActivity.this.chatListView.getPaddingTop();
                    }
                    if (ChatActivity.this.chatListView.getPaddingTop() != AndroidUtilities.dp(52.0f) && ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null))) {
                        ChatActivity.this.chatListView.setPadding(ChatActivity.attach_photo, AndroidUtilities.dp(52.0f), ChatActivity.attach_photo, AndroidUtilities.dp(3.0f));
                        ChatActivity.this.chatListView.setTopGlowOffset(AndroidUtilities.dp(48.0f));
                        top -= AndroidUtilities.dp(48.0f);
                    } else if (ChatActivity.this.chatListView.getPaddingTop() == AndroidUtilities.dp(4.0f) || ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null))) {
                        firstVisPos = -1;
                    } else {
                        ChatActivity.this.chatListView.setPadding(ChatActivity.attach_photo, AndroidUtilities.dp(4.0f), ChatActivity.attach_photo, AndroidUtilities.dp(3.0f));
                        ChatActivity.this.chatListView.setTopGlowOffset(ChatActivity.attach_photo);
                        top += AndroidUtilities.dp(48.0f);
                    }
                    if (firstVisPos != -1) {
                        ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(firstVisPos, top);
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        });
    }

    private void checkRaiseSensors() {
        if (ApplicationLoader.mainInterfacePaused || ((this.bottomOverlayChat != null && this.bottomOverlayChat.getVisibility() == 0) || ((this.bottomOverlay != null && this.bottomOverlay.getVisibility() == 0) || (this.searchContainer != null && this.searchContainer.getVisibility() == 0)))) {
            MediaController.getInstance().setAllowStartRecord(false);
        } else {
            MediaController.getInstance().setAllowStartRecord(true);
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        MediaController.getInstance().startRaiseToEarSensors(this);
        checkRaiseSensors();
        checkActionBarMenu();
        if (!(this.replyImageLocation == null || this.replyImageView == null)) {
            this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null);
        }
        NotificationsController.getInstance().setOpenedDialogId(this.dialog_id);
        if (this.scrollToTopOnResume) {
            if (!this.scrollToTopUnReadOnResume || this.scrollToMessage == null) {
                moveScrollToLastMessage();
            } else if (this.chatListView != null) {
                int yOffset;
                if (this.scrollToMessagePosition == -9000) {
                    yOffset = Math.max(attach_photo, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / attach_video);
                } else if (this.scrollToMessagePosition == -10000) {
                    yOffset = attach_photo;
                } else {
                    yOffset = this.scrollToMessagePosition;
                }
                this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - this.messages.indexOf(this.scrollToMessage), ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)) + yOffset);
            }
            this.scrollToTopUnReadOnResume = false;
            this.scrollToTopOnResume = false;
            this.scrollToMessage = null;
        }
        this.paused = false;
        if (this.readWhenResume && !this.messages.isEmpty()) {
            Iterator i$ = this.messages.iterator();
            while (i$.hasNext()) {
                MessageObject messageObject = (MessageObject) i$.next();
                if (!messageObject.isUnread() && !messageObject.isOut()) {
                    break;
                } else if (!messageObject.isOut()) {
                    messageObject.setIsRead();
                }
            }
            this.readWhenResume = false;
            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(attach_photo)).getId(), this.readWithMid, this.readWithDate, true, false);
        }
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
        fixLayout();
        applyDraftMaybe();
        if (this.bottomOverlayChat.getVisibility() != 0) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        this.chatActivityEnterView.onResume();
        if (this.currentEncryptedChat != null) {
            this.chatEnterTime = System.currentTimeMillis();
            this.chatLeaveTime = 0;
        }
        if (this.startVideoEdit != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChatActivity.this.openVideoEditor(ChatActivity.this.startVideoEdit, false, false);
                    ChatActivity.this.startVideoEdit = null;
                }
            });
        }
        if (this.chatActivityEnterView == null || !this.chatActivityEnterView.isEditingMessage()) {
            this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
            this.chatListView.setOnItemClickListener(this.onItemClickListener);
            this.chatListView.setLongClickable(true);
        }
        checkBotCommands();
    }

    public void onPause() {
        boolean z = true;
        super.onPause();
        MediaController.getInstance().stopRaiseToEarSensors(this);
        if (this.menuItem != null) {
            this.menuItem.closeSubMenu();
        }
        this.paused = true;
        this.wasPaused = true;
        NotificationsController.getInstance().setOpenedDialogId(0);
        CharSequence draftMessage = null;
        boolean searchWebpage = true;
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onPause();
            if (!this.chatActivityEnterView.isEditingMessage()) {
                CharSequence text = AndroidUtilities.getTrimmedString(this.chatActivityEnterView.getFieldText());
                if (!(TextUtils.isEmpty(text) || TextUtils.equals(text, "@gif"))) {
                    draftMessage = text;
                }
            }
            searchWebpage = this.chatActivityEnterView.isMessageWebPageSearchEnabled();
            this.chatActivityEnterView.setFieldFocused(false);
        }
        long j = this.dialog_id;
        ArrayList entities = MessagesQuery.getEntities(draftMessage);
        Message message = this.replyingMessageObject != null ? this.replyingMessageObject.messageOwner : null;
        if (searchWebpage) {
            z = false;
        }
        DraftQuery.saveDraft(j, draftMessage, entities, message, z);
        MessagesController.getInstance().cancelTyping(attach_photo, this.dialog_id);
        if (this.currentEncryptedChat != null) {
            this.chatLeaveTime = System.currentTimeMillis();
            updateInformationForScreenshotDetector();
        }
    }

    private void applyDraftMaybe() {
        if (this.chatActivityEnterView != null) {
            DraftMessage draftMessage = DraftQuery.getDraft(this.dialog_id);
            Message draftReplyMessage = (draftMessage == null || draftMessage.reply_to_msg_id == 0) ? null : DraftQuery.getDraftMessage(this.dialog_id);
            if (this.chatActivityEnterView.getFieldText() == null && draftMessage != null) {
                CharSequence message;
                this.chatActivityEnterView.setWebPage(null, !draftMessage.no_webpage);
                if (draftMessage.entities.isEmpty()) {
                    message = draftMessage.message;
                } else {
                    SpannableStringBuilder stringBuilder = SpannableStringBuilder.valueOf(draftMessage.message);
                    for (int a = attach_photo; a < draftMessage.entities.size(); a += attach_gallery) {
                        MessageEntity entity = (MessageEntity) draftMessage.entities.get(a);
                        if ((entity instanceof TL_inputMessageEntityMentionName) || (entity instanceof TL_messageEntityMentionName)) {
                            int user_id;
                            if (entity instanceof TL_inputMessageEntityMentionName) {
                                user_id = ((TL_inputMessageEntityMentionName) entity).user_id.user_id;
                            } else {
                                user_id = ((TL_messageEntityMentionName) entity).user_id;
                            }
                            if (entity.offset + entity.length < stringBuilder.length() && stringBuilder.charAt(entity.offset + entity.length) == ' ') {
                                entity.length += attach_gallery;
                            }
                            stringBuilder.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + user_id), entity.offset, entity.offset + entity.length, 33);
                        }
                    }
                    message = stringBuilder;
                }
                this.chatActivityEnterView.setFieldText(message);
                if (getArguments().getBoolean("hasUrl", false)) {
                    this.chatActivityEnterView.setSelection(draftMessage.message.indexOf(copy) + attach_gallery);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (ChatActivity.this.chatActivityEnterView != null) {
                                ChatActivity.this.chatActivityEnterView.setFieldFocused(true);
                                ChatActivity.this.chatActivityEnterView.openKeyboard();
                            }
                        }
                    }, 700);
                }
            }
            if (this.replyingMessageObject == null && draftReplyMessage != null) {
                this.replyingMessageObject = new MessageObject(draftReplyMessage, MessagesController.getInstance().getUsers(), false);
                showReplyPanel(true, this.replyingMessageObject, null, null, false, false);
            }
        }
    }

    private void updateInformationForScreenshotDetector() {
        if (this.currentEncryptedChat != null) {
            ArrayList<Long> visibleMessages = new ArrayList();
            if (this.chatListView != null) {
                int count = this.chatListView.getChildCount();
                for (int a = attach_photo; a < count; a += attach_gallery) {
                    View view = this.chatListView.getChildAt(a);
                    MessageObject object = null;
                    if (view instanceof ChatMessageCell) {
                        object = ((ChatMessageCell) view).getMessageObject();
                    }
                    if (!(object == null || object.getId() >= 0 || object.messageOwner.random_id == 0)) {
                        visibleMessages.add(Long.valueOf(object.messageOwner.random_id));
                    }
                }
            }
            MediaController.getInstance().setLastEncryptedChatParams(this.chatEnterTime, this.chatLeaveTime, this.currentEncryptedChat, visibleMessages);
        }
    }

    private boolean fixLayoutInternal() {
        boolean z = true;
        if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != attach_video) {
            this.selectedMessagesCountTextView.setTextSize(edit_done);
        } else {
            this.selectedMessagesCountTextView.setTextSize(mute);
        }
        if (!AndroidUtilities.isTablet()) {
            return true;
        }
        if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == attach_gallery) {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
            if (this.playerView == null || this.playerView.getParent() != null) {
                return false;
            }
            ((ViewGroup) this.fragmentView).addView(this.playerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            return false;
        }
        ActionBar actionBar = this.actionBar;
        if (!(this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(attach_photo) == this || this.parentLayout.fragmentsStack.size() == attach_gallery)) {
            z = false;
        }
        actionBar.setBackButtonDrawable(new BackDrawable(z));
        if (this.playerView == null || this.playerView.getParent() == null) {
            return false;
        }
        this.fragmentView.setPadding(attach_photo, attach_photo, attach_photo, attach_photo);
        ((ViewGroup) this.fragmentView).removeView(this.playerView);
        return false;
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ChatActivity.this.avatarContainer != null) {
                        ChatActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return ChatActivity.this.fixLayoutInternal();
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        fixLayout();
    }

    private void createDeleteMessagesAlert(MessageObject finalSelectedObject) {
        int i;
        Builder builder = new Builder(getParentActivity());
        String str = "AreYouSureDeleteMessages";
        Object[] objArr = new Object[attach_gallery];
        String str2 = "messages";
        if (finalSelectedObject != null) {
            i = attach_gallery;
        } else {
            i = this.selectedMessagesIds[attach_photo].size() + this.selectedMessagesIds[attach_gallery].size();
        }
        objArr[attach_photo] = LocaleController.formatPluralString(str2, i);
        builder.setMessage(LocaleController.formatString(str, C0691R.string.AreYouSureDeleteMessages, objArr));
        builder.setTitle(LocaleController.getString("Message", C0691R.string.Message));
        boolean[] checks = new boolean[attach_audio];
        User user = null;
        if (this.currentChat != null && this.currentChat.megagroup) {
            int a;
            if (finalSelectedObject == null) {
                int from_id = -1;
                for (a = attach_gallery; a >= 0; a--) {
                    for (Entry<Integer, MessageObject> entry : this.selectedMessagesIds[a].entrySet()) {
                        MessageObject msg = (MessageObject) entry.getValue();
                        if (from_id == -1) {
                            from_id = msg.messageOwner.from_id;
                        }
                        if (from_id >= 0) {
                            if (from_id != msg.messageOwner.from_id) {
                            }
                        }
                        from_id = -2;
                        break;
                    }
                    if (from_id == -2) {
                        break;
                    }
                }
                if (from_id != -1) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(from_id));
                }
            } else if (finalSelectedObject.messageOwner.action == null || (finalSelectedObject.messageOwner.action instanceof TL_messageActionEmpty)) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(finalSelectedObject.messageOwner.from_id));
            }
            if (user == null || user.id == UserConfig.getClientUserId()) {
                user = null;
            } else {
                View frameLayout = new FrameLayout(getParentActivity());
                if (VERSION.SDK_INT >= report) {
                    frameLayout.setPadding(attach_photo, AndroidUtilities.dp(8.0f), attach_photo, attach_photo);
                }
                for (a = attach_photo; a < attach_audio; a += attach_gallery) {
                    int i2;
                    CheckBoxCell cell = new CheckBoxCell(getParentActivity());
                    cell.setBackgroundResource(C0691R.drawable.list_selector);
                    cell.setTag(Integer.valueOf(a));
                    if (a == 0) {
                        cell.setText(LocaleController.getString("DeleteBanUser", C0691R.string.DeleteBanUser), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    } else if (a == attach_gallery) {
                        cell.setText(LocaleController.getString("DeleteReportSpam", C0691R.string.DeleteReportSpam), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    } else if (a == attach_video) {
                        Object[] objArr2 = new Object[attach_gallery];
                        objArr2[attach_photo] = ContactsController.formatName(user.first_name, user.last_name);
                        cell.setText(LocaleController.formatString("DeleteAllFrom", C0691R.string.DeleteAllFrom, objArr2), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    }
                    i = LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : attach_photo;
                    if (LocaleController.isRTL) {
                        i2 = attach_photo;
                    } else {
                        i2 = AndroidUtilities.dp(8.0f);
                    }
                    cell.setPadding(i, attach_photo, i2, attach_photo);
                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, (float) (a * 48), 8.0f, 0.0f));
                    cell.setOnClickListener(new AnonymousClass74(checks));
                }
                builder.setView(frameLayout);
            }
        }
        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new AnonymousClass75(finalSelectedObject, user, checks));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createMenu(android.view.View r29, boolean r30) {
        /*
        r28 = this;
        r0 = r28;
        r0 = r0.actionBar;
        r23 = r0;
        r23 = r23.isActionModeShowed();
        if (r23 == 0) goto L_0x000d;
    L_0x000c:
        return;
    L_0x000d:
        r18 = 0;
        r0 = r29;
        r0 = r0 instanceof org.telegram.ui.Cells.ChatMessageCell;
        r23 = r0;
        if (r23 == 0) goto L_0x005d;
    L_0x0017:
        r29 = (org.telegram.ui.Cells.ChatMessageCell) r29;
        r18 = r29.getMessageObject();
    L_0x001d:
        if (r18 == 0) goto L_0x000c;
    L_0x001f:
        r0 = r28;
        r1 = r18;
        r20 = r0.getMessageType(r1);
        if (r30 == 0) goto L_0x006c;
    L_0x0029:
        r0 = r18;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.action;
        r23 = r0;
        r0 = r23;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
        r23 = r0;
        if (r23 == 0) goto L_0x006c;
    L_0x003d:
        r0 = r18;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.reply_to_msg_id;
        r23 = r0;
        r24 = 0;
        r25 = 1;
        r26 = 0;
        r0 = r28;
        r1 = r23;
        r2 = r24;
        r3 = r25;
        r4 = r26;
        r0.scrollToMessageId(r1, r2, r3, r4);
        goto L_0x000c;
    L_0x005d:
        r0 = r29;
        r0 = r0 instanceof org.telegram.ui.Cells.ChatActionCell;
        r23 = r0;
        if (r23 == 0) goto L_0x001d;
    L_0x0065:
        r29 = (org.telegram.ui.Cells.ChatActionCell) r29;
        r18 = r29.getMessageObject();
        goto L_0x001d;
    L_0x006c:
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.selectedObject = r0;
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.forwaringMessage = r0;
        r6 = 1;
    L_0x007d:
        if (r6 < 0) goto L_0x0098;
    L_0x007f:
        r0 = r28;
        r0 = r0.selectedMessagesCanCopyIds;
        r23 = r0;
        r23 = r23[r6];
        r23.clear();
        r0 = r28;
        r0 = r0.selectedMessagesIds;
        r23 = r0;
        r23 = r23[r6];
        r23.clear();
        r6 = r6 + -1;
        goto L_0x007d;
    L_0x0098:
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.cantDeleteMessagesCount = r0;
        r0 = r28;
        r0 = r0.actionBar;
        r23 = r0;
        r23.hideActionMode();
        r23 = 1;
        r0 = r28;
        r1 = r23;
        r0.updatePinnedMessageView(r1);
        r8 = 1;
        r24 = r18.getDialogId();
        r0 = r28;
        r0 = r0.mergeDialogId;
        r26 = r0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 == 0) goto L_0x02e1;
    L_0x00c1:
        r23 = r18.getId();
        if (r23 <= 0) goto L_0x02e1;
    L_0x00c7:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r23 = org.telegram.messenger.ChatObject.isChannel(r23);
        if (r23 == 0) goto L_0x02e1;
    L_0x00d3:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.megagroup;
        r23 = r0;
        if (r23 == 0) goto L_0x02e1;
    L_0x00e1:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.creator;
        r23 = r0;
        if (r23 != 0) goto L_0x00fd;
    L_0x00ef:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.editor;
        r23 = r0;
        if (r23 == 0) goto L_0x02e1;
    L_0x00fd:
        r0 = r18;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.action;
        r23 = r0;
        if (r23 == 0) goto L_0x011f;
    L_0x010b:
        r0 = r18;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.action;
        r23 = r0;
        r0 = r23;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        r23 = r0;
        if (r23 == 0) goto L_0x02e1;
    L_0x011f:
        r10 = 1;
    L_0x0120:
        r24 = r18.getDialogId();
        r0 = r28;
        r0 = r0.mergeDialogId;
        r26 = r0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 == 0) goto L_0x02e4;
    L_0x012e:
        r0 = r28;
        r0 = r0.info;
        r23 = r0;
        if (r23 == 0) goto L_0x02e4;
    L_0x0136:
        r0 = r28;
        r0 = r0.info;
        r23 = r0;
        r0 = r23;
        r0 = r0.pinned_msg_id;
        r23 = r0;
        r24 = r18.getId();
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x02e4;
    L_0x014c:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.creator;
        r23 = r0;
        if (r23 != 0) goto L_0x0168;
    L_0x015a:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.editor;
        r23 = r0;
        if (r23 == 0) goto L_0x02e4;
    L_0x0168:
        r11 = 1;
    L_0x0169:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r18;
        r1 = r23;
        r23 = r0.canEditMessage(r1);
        if (r23 == 0) goto L_0x02e7;
    L_0x0179:
        r0 = r28;
        r0 = r0.chatActivityEnterView;
        r23 = r0;
        r23 = r23.hasAudioToSend();
        if (r23 != 0) goto L_0x02e7;
    L_0x0185:
        r24 = r18.getDialogId();
        r0 = r28;
        r0 = r0.mergeDialogId;
        r26 = r0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 == 0) goto L_0x02e7;
    L_0x0193:
        r9 = 1;
    L_0x0194:
        r0 = r28;
        r0 = r0.currentEncryptedChat;
        r23 = r0;
        if (r23 == 0) goto L_0x01b4;
    L_0x019c:
        r0 = r28;
        r0 = r0.currentEncryptedChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.layer;
        r23 = r0;
        r23 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r23);
        r24 = 46;
        r0 = r23;
        r1 = r24;
        if (r0 < r1) goto L_0x022a;
    L_0x01b4:
        r23 = 1;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x01ca;
    L_0x01bc:
        r24 = r18.getDialogId();
        r0 = r28;
        r0 = r0.mergeDialogId;
        r26 = r0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 == 0) goto L_0x022a;
    L_0x01ca:
        r0 = r28;
        r0 = r0.currentEncryptedChat;
        r23 = r0;
        if (r23 != 0) goto L_0x01d8;
    L_0x01d2:
        r23 = r18.getId();
        if (r23 < 0) goto L_0x022a;
    L_0x01d8:
        r0 = r28;
        r0 = r0.isBroadcast;
        r23 = r0;
        if (r23 != 0) goto L_0x022a;
    L_0x01e0:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        if (r23 == 0) goto L_0x022b;
    L_0x01e8:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r23 = org.telegram.messenger.ChatObject.isNotInChat(r23);
        if (r23 != 0) goto L_0x022a;
    L_0x01f4:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r23 = org.telegram.messenger.ChatObject.isChannel(r23);
        if (r23 == 0) goto L_0x022b;
    L_0x0200:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.creator;
        r23 = r0;
        if (r23 != 0) goto L_0x022b;
    L_0x020e:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.editor;
        r23 = r0;
        if (r23 != 0) goto L_0x022b;
    L_0x021c:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r23;
        r0 = r0.megagroup;
        r23 = r0;
        if (r23 != 0) goto L_0x022b;
    L_0x022a:
        r8 = 0;
    L_0x022b:
        if (r30 != 0) goto L_0x023d;
    L_0x022d:
        r23 = 2;
        r0 = r20;
        r1 = r23;
        if (r0 < r1) goto L_0x023d;
    L_0x0235:
        r23 = 20;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x0a56;
    L_0x023d:
        if (r20 < 0) goto L_0x000c;
    L_0x023f:
        r0 = r18;
        r1 = r28;
        r1.selectedObject = r0;
        r23 = r28.getParentActivity();
        if (r23 == 0) goto L_0x000c;
    L_0x024b:
        r14 = new android.app.AlertDialog$Builder;
        r23 = r28.getParentActivity();
        r0 = r23;
        r14.<init>(r0);
        r17 = new java.util.ArrayList;
        r17.<init>();
        r19 = new java.util.ArrayList;
        r19.<init>();
        if (r20 != 0) goto L_0x02ea;
    L_0x0262:
        r23 = "Retry";
        r24 = 2131166118; // 0x7f0703a6 float:1.7946472E38 double:1.0529359645E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 0;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "Delete";
        r24 = 2131165532; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x029c:
        r23 = r19.isEmpty();
        if (r23 != 0) goto L_0x000c;
    L_0x02a2:
        r23 = r17.size();
        r0 = r23;
        r0 = new java.lang.CharSequence[r0];
        r23 = r0;
        r0 = r17;
        r1 = r23;
        r15 = r0.toArray(r1);
        r15 = (java.lang.CharSequence[]) r15;
        r23 = new org.telegram.ui.ChatActivity$76;
        r0 = r23;
        r1 = r28;
        r2 = r19;
        r0.<init>(r2);
        r0 = r23;
        r14.setItems(r15, r0);
        r23 = "Message";
        r24 = 2131165831; // 0x7f070287 float:1.794589E38 double:1.0529358227E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r23;
        r14.setTitle(r0);
        r23 = r14.create();
        r0 = r28;
        r1 = r23;
        r0.showDialog(r1);
        goto L_0x000c;
    L_0x02e1:
        r10 = 0;
        goto L_0x0120;
    L_0x02e4:
        r11 = 0;
        goto L_0x0169;
    L_0x02e7:
        r9 = 0;
        goto L_0x0194;
    L_0x02ea:
        r23 = 1;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x03dd;
    L_0x02f2:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        if (r23 == 0) goto L_0x03ae;
    L_0x02fa:
        r0 = r28;
        r0 = r0.isBroadcast;
        r23 = r0;
        if (r23 != 0) goto L_0x03ae;
    L_0x0302:
        if (r8 == 0) goto L_0x0321;
    L_0x0304:
        r23 = "Reply";
        r24 = 2131166090; // 0x7f07038a float:1.7946416E38 double:1.0529359507E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 8;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x0321:
        if (r11 == 0) goto L_0x038e;
    L_0x0323:
        r23 = "UnpinMessage";
        r24 = 2131166277; // 0x7f070445 float:1.7946795E38 double:1.052936043E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 14;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x0340:
        if (r9 == 0) goto L_0x035f;
    L_0x0342:
        r23 = "Edit";
        r24 = 2131165564; // 0x7f07017c float:1.7945349E38 double:1.052935691E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 12;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x035f:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r18;
        r1 = r23;
        r23 = r0.canDeleteMessage(r1);
        if (r23 == 0) goto L_0x029c;
    L_0x036f:
        r23 = "Delete";
        r24 = 2131165532; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x029c;
    L_0x038e:
        if (r10 == 0) goto L_0x0340;
    L_0x0390:
        r23 = "PinMessage";
        r24 = 2131166062; // 0x7f07036e float:1.7946359E38 double:1.052935937E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 13;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0340;
    L_0x03ae:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r18;
        r1 = r23;
        r23 = r0.canDeleteMessage(r1);
        if (r23 == 0) goto L_0x029c;
    L_0x03be:
        r23 = "Delete";
        r24 = 2131165532; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x029c;
    L_0x03dd:
        r23 = 20;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x043e;
    L_0x03e5:
        r23 = "Retry";
        r24 = 2131166118; // 0x7f0703a6 float:1.7946472E38 double:1.0529359645E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 0;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "Copy";
        r24 = 2131165509; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 3;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "Delete";
        r24 = 2131165532; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x029c;
    L_0x043e:
        r0 = r28;
        r0 = r0.currentEncryptedChat;
        r23 = r0;
        if (r23 != 0) goto L_0x0889;
    L_0x0446:
        if (r8 == 0) goto L_0x0465;
    L_0x0448:
        r23 = "Reply";
        r24 = 2131166090; // 0x7f07038a float:1.7946416E38 double:1.0529359507E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 8;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x0465:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.type;
        r23 = r0;
        if (r23 == 0) goto L_0x0481;
    L_0x0473:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.caption;
        r23 = r0;
        if (r23 == 0) goto L_0x049e;
    L_0x0481:
        r23 = "Copy";
        r24 = 2131165509; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 3;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x049e:
        r23 = 3;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x058b;
    L_0x04a6:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.media;
        r23 = r0;
        r0 = r23;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        r23 = r0;
        if (r23 == 0) goto L_0x0501;
    L_0x04c0:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.media;
        r23 = r0;
        r0 = r23;
        r0 = r0.webpage;
        r23 = r0;
        r0 = r23;
        r0 = r0.document;
        r23 = r0;
        r23 = org.telegram.messenger.MessageObject.isNewGifDocument(r23);
        if (r23 == 0) goto L_0x0501;
    L_0x04e4:
        r23 = "SaveToGIFs";
        r24 = 2131166128; // 0x7f0703b0 float:1.7946493E38 double:1.0529359694E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 11;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x0501:
        r23 = "Forward";
        r24 = 2131165614; // 0x7f0701ae float:1.794545E38 double:1.0529357155E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 2;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        if (r11 == 0) goto L_0x0868;
    L_0x0520:
        r23 = "UnpinMessage";
        r24 = 2131166277; // 0x7f070445 float:1.7946795E38 double:1.052936043E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 14;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x053d:
        if (r9 == 0) goto L_0x055c;
    L_0x053f:
        r23 = "Edit";
        r24 = 2131165564; // 0x7f07017c float:1.7945349E38 double:1.052935691E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 12;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x055c:
        r0 = r28;
        r0 = r0.currentChat;
        r23 = r0;
        r0 = r18;
        r1 = r23;
        r23 = r0.canDeleteMessage(r1);
        if (r23 == 0) goto L_0x029c;
    L_0x056c:
        r23 = "Delete";
        r24 = 2131165532; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x029c;
    L_0x058b:
        r23 = 4;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x06b7;
    L_0x0593:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.isVideo();
        if (r23 == 0) goto L_0x05db;
    L_0x059f:
        r23 = "SaveToGallery";
        r24 = 2131166129; // 0x7f0703b1 float:1.7946495E38 double:1.05293597E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 4;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x05db:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.isMusic();
        if (r23 == 0) goto L_0x0623;
    L_0x05e7:
        r23 = "SaveToMusic";
        r24 = 2131166131; // 0x7f0703b3 float:1.7946499E38 double:1.052935971E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 10;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x0623:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.getDocument();
        if (r23 == 0) goto L_0x0698;
    L_0x062f:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.getDocument();
        r23 = org.telegram.messenger.MessageObject.isNewGifDocument(r23);
        if (r23 == 0) goto L_0x065c;
    L_0x063f:
        r23 = "SaveToGIFs";
        r24 = 2131166128; // 0x7f0703b0 float:1.7946493E38 double:1.0529359694E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 11;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x065c:
        r23 = "SaveToDownloads";
        r24 = 2131166127; // 0x7f0703af float:1.794649E38 double:1.052935969E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 10;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x0698:
        r23 = "SaveToGallery";
        r24 = 2131166129; // 0x7f0703b1 float:1.7946495E38 double:1.05293597E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 4;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x06b7:
        r23 = 5;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x06fb;
    L_0x06bf:
        r23 = "ApplyLocalizationFile";
        r24 = 2131165302; // 0x7f070076 float:1.7944817E38 double:1.0529355613E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 5;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x06fb:
        r23 = 6;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x075c;
    L_0x0703:
        r23 = "SaveToGallery";
        r24 = 2131166129; // 0x7f0703b1 float:1.7946495E38 double:1.05293597E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 7;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "SaveToDownloads";
        r24 = 2131166127; // 0x7f0703af float:1.794649E38 double:1.052935969E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 10;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x075c:
        r23 = 7;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x0783;
    L_0x0764:
        r23 = "AddToStickers";
        r24 = 2131165272; // 0x7f070058 float:1.7944756E38 double:1.0529355465E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 9;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x0783:
        r23 = 8;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x0501;
    L_0x078b:
        r23 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r28;
        r0 = r0.selectedObject;
        r24 = r0;
        r0 = r24;
        r0 = r0.messageOwner;
        r24 = r0;
        r0 = r24;
        r0 = r0.media;
        r24 = r0;
        r0 = r24;
        r0 = r0.user_id;
        r24 = r0;
        r24 = java.lang.Integer.valueOf(r24);
        r21 = r23.getUser(r24);
        if (r21 == 0) goto L_0x07f4;
    L_0x07b1:
        r0 = r21;
        r0 = r0.id;
        r23 = r0;
        r24 = org.telegram.messenger.UserConfig.getClientUserId();
        r0 = r23;
        r1 = r24;
        if (r0 == r1) goto L_0x07f4;
    L_0x07c1:
        r23 = org.telegram.messenger.ContactsController.getInstance();
        r0 = r23;
        r0 = r0.contactsDict;
        r23 = r0;
        r0 = r21;
        r0 = r0.id;
        r24 = r0;
        r23 = r23.get(r24);
        if (r23 != 0) goto L_0x07f4;
    L_0x07d7:
        r23 = "AddContactTitle";
        r24 = 2131165262; // 0x7f07004e float:1.7944736E38 double:1.0529355416E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 15;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x07f4:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.media;
        r23 = r0;
        r0 = r23;
        r0 = r0.phone_number;
        r23 = r0;
        if (r23 != 0) goto L_0x082c;
    L_0x080e:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.media;
        r23 = r0;
        r0 = r23;
        r0 = r0.phone_number;
        r23 = r0;
        r23 = r23.length();
        if (r23 == 0) goto L_0x0501;
    L_0x082c:
        r23 = "Copy";
        r24 = 2131165509; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 16;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "Call";
        r24 = 2131165371; // 0x7f0700bb float:1.7944957E38 double:1.0529355954E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 17;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x0501;
    L_0x0868:
        if (r10 == 0) goto L_0x053d;
    L_0x086a:
        r23 = "PinMessage";
        r24 = 2131166062; // 0x7f07036e float:1.7946359E38 double:1.052935937E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 13;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x053d;
    L_0x0889:
        if (r8 == 0) goto L_0x08a8;
    L_0x088b:
        r23 = "Reply";
        r24 = 2131166090; // 0x7f07038a float:1.7946416E38 double:1.0529359507E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 8;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x08a8:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.type;
        r23 = r0;
        if (r23 == 0) goto L_0x08c4;
    L_0x08b6:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.caption;
        r23 = r0;
        if (r23 == 0) goto L_0x08e1;
    L_0x08c4:
        r23 = "Copy";
        r24 = 2131165509; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 3;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x08e1:
        r23 = 4;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x0a08;
    L_0x08e9:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.isVideo();
        if (r23 == 0) goto L_0x094e;
    L_0x08f5:
        r23 = "SaveToGallery";
        r24 = 2131166129; // 0x7f0703b1 float:1.7946495E38 double:1.05293597E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 4;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
    L_0x092f:
        r23 = "Delete";
        r24 = 2131165532; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x029c;
    L_0x094e:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.isMusic();
        if (r23 == 0) goto L_0x0995;
    L_0x095a:
        r23 = "SaveToMusic";
        r24 = 2131166131; // 0x7f0703b3 float:1.7946499E38 double:1.052935971E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 10;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x092f;
    L_0x0995:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.isVideo();
        if (r23 != 0) goto L_0x09e9;
    L_0x09a1:
        r0 = r28;
        r0 = r0.selectedObject;
        r23 = r0;
        r23 = r23.getDocument();
        if (r23 == 0) goto L_0x09e9;
    L_0x09ad:
        r23 = "SaveToDownloads";
        r24 = 2131166127; // 0x7f0703af float:1.794649E38 double:1.052935969E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 10;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        r23 = "ShareFile";
        r24 = 2131166192; // 0x7f0703f0 float:1.7946622E38 double:1.052936001E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 6;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x092f;
    L_0x09e9:
        r23 = "SaveToGallery";
        r24 = 2131166129; // 0x7f0703b1 float:1.7946495E38 double:1.05293597E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 4;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x092f;
    L_0x0a08:
        r23 = 5;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x0a2f;
    L_0x0a10:
        r23 = "ApplyLocalizationFile";
        r24 = 2131165302; // 0x7f070076 float:1.7944817E38 double:1.0529355613E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 5;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x092f;
    L_0x0a2f:
        r23 = 7;
        r0 = r20;
        r1 = r23;
        if (r0 != r1) goto L_0x092f;
    L_0x0a37:
        r23 = "AddToStickers";
        r24 = 2131165272; // 0x7f070058 float:1.7944756E38 double:1.0529355465E-314;
        r23 = org.telegram.messenger.LocaleController.getString(r23, r24);
        r0 = r17;
        r1 = r23;
        r0.add(r1);
        r23 = 9;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r19;
        r1 = r23;
        r0.add(r1);
        goto L_0x092f;
    L_0x0a56:
        r0 = r28;
        r0 = r0.actionBar;
        r23 = r0;
        r7 = r23.createActionMode();
        r23 = 11;
        r0 = r23;
        r16 = r7.getItem(r0);
        if (r16 == 0) goto L_0x0a73;
    L_0x0a6a:
        r23 = 0;
        r0 = r16;
        r1 = r23;
        r0.setVisibility(r1);
    L_0x0a73:
        r23 = 12;
        r0 = r23;
        r16 = r7.getItem(r0);
        if (r16 == 0) goto L_0x0a86;
    L_0x0a7d:
        r23 = 0;
        r0 = r16;
        r1 = r23;
        r0.setVisibility(r1);
    L_0x0a86:
        r0 = r28;
        r0 = r0.editDoneItem;
        r23 = r0;
        if (r23 == 0) goto L_0x0a99;
    L_0x0a8e:
        r0 = r28;
        r0 = r0.editDoneItem;
        r23 = r0;
        r24 = 8;
        r23.setVisibility(r24);
    L_0x0a99:
        r0 = r28;
        r0 = r0.actionBar;
        r23 = r0;
        r23.showActionMode();
        r23 = 1;
        r0 = r28;
        r1 = r23;
        r0.updatePinnedMessageView(r1);
        r12 = new android.animation.AnimatorSet;
        r12.<init>();
        r13 = new java.util.ArrayList;
        r13.<init>();
        r6 = 0;
    L_0x0ab6:
        r0 = r28;
        r0 = r0.actionModeViews;
        r23 = r0;
        r23 = r23.size();
        r0 = r23;
        if (r6 >= r0) goto L_0x0aee;
    L_0x0ac4:
        r0 = r28;
        r0 = r0.actionModeViews;
        r23 = r0;
        r0 = r23;
        r22 = r0.get(r6);
        r22 = (android.view.View) r22;
        org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r22);
        r23 = "scaleY";
        r24 = 2;
        r0 = r24;
        r0 = new float[r0];
        r24 = r0;
        r24 = {1036831949, 1065353216};
        r23 = android.animation.ObjectAnimator.ofFloat(r22, r23, r24);
        r0 = r23;
        r13.add(r0);
        r6 = r6 + 1;
        goto L_0x0ab6;
    L_0x0aee:
        r12.playTogether(r13);
        r24 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        r0 = r24;
        r12.setDuration(r0);
        r12.start();
        r0 = r28;
        r1 = r18;
        r0.addToSelectedMessages(r1);
        r0 = r28;
        r0 = r0.selectedMessagesCountTextView;
        r23 = r0;
        r24 = 1;
        r25 = 0;
        r23.setNumber(r24, r25);
        r28.updateVisibleRows();
        goto L_0x000c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatActivity.createMenu(android.view.View, boolean):void");
    }

    private void showEditDoneProgress(boolean show, boolean animated) {
        if (this.editDoneItemAnimation != null) {
            this.editDoneItemAnimation.cancel();
        }
        if (animated) {
            this.editDoneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            float[] fArr;
            float[] fArr2;
            if (show) {
                this.editDoneItemProgress.setVisibility(attach_photo);
                this.editDoneItem.setEnabled(false);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[attach_location];
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", fArr);
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_gallery] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", fArr);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = 0.0f;
                animatorArr[attach_video] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                animatorArr[attach_audio] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                animatorArr[attach_document] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                animatorArr[attach_contact] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", fArr2);
                animatorSet.playTogether(animatorArr);
            } else {
                this.editDoneItem.getImageView().setVisibility(attach_photo);
                this.editDoneItem.setEnabled(true);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[attach_location];
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", fArr);
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_gallery] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", fArr);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = 0.0f;
                animatorArr[attach_video] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                animatorArr[attach_audio] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                animatorArr[attach_document] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = TouchHelperCallback.ALPHA_FULL;
                animatorArr[attach_contact] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", fArr2);
                animatorSet.playTogether(animatorArr);
            }
            this.editDoneItemAnimation.addListener(new AnonymousClass77(show));
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (show) {
            this.editDoneItem.getImageView().setScaleX(0.1f);
            this.editDoneItem.getImageView().setScaleY(0.1f);
            this.editDoneItem.getImageView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(TouchHelperCallback.ALPHA_FULL);
            this.editDoneItemProgress.setScaleY(TouchHelperCallback.ALPHA_FULL);
            this.editDoneItemProgress.setAlpha(TouchHelperCallback.ALPHA_FULL);
            this.editDoneItem.getImageView().setVisibility(attach_document);
            this.editDoneItemProgress.setVisibility(attach_photo);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getImageView().setScaleX(TouchHelperCallback.ALPHA_FULL);
            this.editDoneItem.getImageView().setScaleY(TouchHelperCallback.ALPHA_FULL);
            this.editDoneItem.getImageView().setAlpha(TouchHelperCallback.ALPHA_FULL);
            this.editDoneItem.getImageView().setVisibility(attach_photo);
            this.editDoneItemProgress.setVisibility(attach_document);
            this.editDoneItem.setEnabled(true);
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (name && previousUid != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (chat != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        }
        if (messageObject.messageOwner.media == null || messageObject.messageOwner.media.caption == null) {
            return str + messageObject.messageText;
        }
        return str + messageObject.messageOwner.media.caption;
    }

    private void processSelectedOption(int option) {
        if (this.selectedObject != null) {
            Bundle args;
            String path;
            Activity parentActivity;
            String[] strArr;
            Builder builder;
            Intent intent;
            switch (option) {
                case attach_photo /*0*/:
                    if (SendMessagesHelper.getInstance().retrySendMessage(this.selectedObject, false)) {
                        moveScrollToLastMessage();
                        break;
                    }
                    break;
                case attach_gallery /*1*/:
                    if (getParentActivity() != null) {
                        createDeleteMessagesAlert(this.selectedObject);
                        break;
                    } else {
                        this.selectedObject = null;
                        return;
                    }
                case attach_video /*2*/:
                    this.forwaringMessage = this.selectedObject;
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", attach_gallery);
                    BaseFragment dialogsActivity = new DialogsActivity(args);
                    dialogsActivity.setDelegate(this);
                    presentFragment(dialogsActivity);
                    break;
                case attach_audio /*3*/:
                    AndroidUtilities.addToClipboard(getMessageContent(this.selectedObject, attach_photo, false));
                    break;
                case attach_document /*4*/:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (this.selectedObject.type == attach_audio || this.selectedObject.type == attach_gallery) {
                        if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            MediaController.saveFile(path, getParentActivity(), this.selectedObject.type == attach_audio ? attach_gallery : attach_photo, null, null);
                            break;
                        }
                        parentActivity = getParentActivity();
                        strArr = new String[attach_gallery];
                        strArr[attach_photo] = "android.permission.WRITE_EXTERNAL_STORAGE";
                        parentActivity.requestPermissions(strArr, attach_document);
                        this.selectedObject = null;
                        return;
                    }
                    break;
                case attach_contact /*5*/:
                    File f;
                    File locFile = null;
                    if (!(this.selectedObject.messageOwner.attachPath == null || this.selectedObject.messageOwner.attachPath.length() == 0)) {
                        f = new File(this.selectedObject.messageOwner.attachPath);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile == null) {
                        f = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile != null) {
                        if (!LocaleController.getInstance().applyLanguageFile(locFile)) {
                            if (getParentActivity() != null) {
                                builder = new Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                                builder.setMessage(LocaleController.getString("IncorrectLocalization", C0691R.string.IncorrectLocalization));
                                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                                showDialog(builder.create());
                                break;
                            }
                            this.selectedObject = null;
                            return;
                        }
                        presentFragment(new LanguageSelectActivity());
                        break;
                    }
                    break;
                case attach_location /*6*/:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    intent = new Intent("android.intent.action.SEND");
                    intent.setType(this.selectedObject.getDocument().mime_type);
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", C0691R.string.ShareFile)), 500);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        MediaController.saveFile(path, getParentActivity(), attach_photo, null, null);
                        break;
                    }
                    parentActivity = getParentActivity();
                    strArr = new String[attach_gallery];
                    strArr[attach_photo] = "android.permission.WRITE_EXTERNAL_STORAGE";
                    parentActivity.requestPermissions(strArr, attach_document);
                    this.selectedObject = null;
                    return;
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    showReplyPanel(true, this.selectedObject, null, null, false, true);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    showDialog(new StickersAlert(getParentActivity(), this.selectedObject.getInputStickerSet(), null, this.bottomOverlayChat.getVisibility() != 0 ? this.chatActivityEnterView : null));
                    break;
                case copy /*10*/:
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        String fileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                        if (fileName == null || fileName.length() == 0) {
                            fileName = this.selectedObject.getFileName();
                        }
                        path = this.selectedObject.messageOwner.attachPath;
                        if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                            path = null;
                        }
                        if (path == null || path.length() == 0) {
                            path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        MediaController.saveFile(path, getParentActivity(), this.selectedObject.isMusic() ? attach_audio : attach_video, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : TtmlNode.ANONYMOUS_REGION_ID);
                        break;
                    }
                    parentActivity = getParentActivity();
                    strArr = new String[attach_gallery];
                    strArr[attach_photo] = "android.permission.WRITE_EXTERNAL_STORAGE";
                    parentActivity.requestPermissions(strArr, attach_document);
                    this.selectedObject = null;
                    return;
                case forward /*11*/:
                    SearchImage searchImage = MessagesController.getInstance().saveGif(this.selectedObject.getDocument());
                    showGifHint();
                    this.chatActivityEnterView.addRecentGif(searchImage);
                    break;
                case delete /*12*/:
                    if (getParentActivity() != null) {
                        if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                            this.actionBar.closeSearchField();
                            this.chatActivityEnterView.setFieldFocused();
                        }
                        this.mentionsAdapter.setNeedBotContext(false);
                        this.chatListView.setOnItemLongClickListener(null);
                        this.chatListView.setOnItemClickListener(null);
                        this.chatListView.setClickable(false);
                        this.chatListView.setLongClickable(false);
                        this.chatActivityEnterView.setEditingMessageObject(this.selectedObject, !this.selectedObject.isMediaEmpty());
                        if (this.chatActivityEnterView.isEditingCaption()) {
                            this.mentionsAdapter.setAllowNewMentions(false);
                        }
                        this.actionModeTitleContainer.setVisibility(attach_photo);
                        this.selectedMessagesCountTextView.setVisibility(8);
                        checkEditTimer();
                        this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
                        ActionBarMenu actionMode = this.actionBar.createActionMode();
                        actionMode.getItem(reply).setVisibility(8);
                        actionMode.getItem(copy).setVisibility(8);
                        actionMode.getItem(forward).setVisibility(8);
                        actionMode.getItem(delete).setVisibility(8);
                        if (this.editDoneItemAnimation != null) {
                            this.editDoneItemAnimation.cancel();
                            this.editDoneItemAnimation = null;
                        }
                        this.editDoneItem.setVisibility(attach_photo);
                        showEditDoneProgress(true, false);
                        this.actionBar.showActionMode();
                        updatePinnedMessageView(true);
                        updateVisibleRows();
                        TLObject req = new TL_messages_getMessageEditData();
                        req.peer = MessagesController.getInputPeer((int) this.dialog_id);
                        req.id = this.selectedObject.getId();
                        this.editingMessageObjectReqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                            /* renamed from: org.telegram.ui.ChatActivity.78.1 */
                            class C10961 implements Runnable {
                                final /* synthetic */ TLObject val$response;

                                C10961(TLObject tLObject) {
                                    this.val$response = tLObject;
                                }

                                public void run() {
                                    ChatActivity.this.editingMessageObjectReqId = ChatActivity.attach_photo;
                                    if (this.val$response == null) {
                                        Builder builder = new Builder(ChatActivity.this.getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                                        builder.setMessage(LocaleController.getString("EditMessageError", C0691R.string.EditMessageError));
                                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                                        ChatActivity.this.showDialog(builder.create());
                                        if (ChatActivity.this.chatActivityEnterView != null) {
                                            ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                                            return;
                                        }
                                        return;
                                    }
                                    ChatActivity.this.showEditDoneProgress(false, true);
                                }
                            }

                            public void run(TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new C10961(response));
                            }
                        });
                        break;
                    }
                    this.selectedObject = null;
                    return;
                case chat_enc_timer /*13*/:
                    int i;
                    int mid = this.selectedObject.getId();
                    builder = new Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("PinMessageAlert", C0691R.string.PinMessageAlert));
                    boolean[] checks = new boolean[attach_gallery];
                    checks[attach_photo] = true;
                    View frameLayout = new FrameLayout(getParentActivity());
                    if (VERSION.SDK_INT >= report) {
                        frameLayout.setPadding(attach_photo, AndroidUtilities.dp(8.0f), attach_photo, attach_photo);
                    }
                    CheckBoxCell cell = new CheckBoxCell(getParentActivity());
                    cell.setBackgroundResource(C0691R.drawable.list_selector);
                    cell.setText(LocaleController.getString("PinNotify", C0691R.string.PinNotify), TtmlNode.ANONYMOUS_REGION_ID, true, false);
                    int dp = LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : attach_photo;
                    if (LocaleController.isRTL) {
                        i = attach_photo;
                    } else {
                        i = AndroidUtilities.dp(8.0f);
                    }
                    cell.setPadding(dp, attach_photo, i, attach_photo);
                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                    cell.setOnClickListener(new AnonymousClass79(checks));
                    builder.setView(frameLayout);
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new AnonymousClass80(mid, checks));
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    showDialog(builder.create());
                    break;
                case chat_menu_attach /*14*/:
                    builder = new Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("UnpinMessageAlert", C0691R.string.UnpinMessageAlert));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, ChatActivity.attach_photo, false);
                        }
                    });
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    showDialog(builder.create());
                    break;
                case clear_history /*15*/:
                    args = new Bundle();
                    args.putInt("user_id", this.selectedObject.messageOwner.media.user_id);
                    args.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                    args.putBoolean("addContact", true);
                    presentFragment(new ContactAddActivity(args));
                    break;
                case delete_chat /*16*/:
                    AndroidUtilities.addToClipboard(this.selectedObject.messageOwner.media.phone_number);
                    break;
                case share_contact /*17*/:
                    try {
                        intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                        intent.addFlags(268435456);
                        getParentActivity().startActivityForResult(intent, 500);
                        break;
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                        break;
                    }
            }
            this.selectedObject = null;
        }
    }

    public void didSelectDialog(DialogsActivity activity, long did, boolean param) {
        if (this.dialog_id == 0) {
            return;
        }
        if (this.forwaringMessage != null || !this.selectedMessagesIds[attach_photo].isEmpty() || !this.selectedMessagesIds[attach_gallery].isEmpty()) {
            ArrayList<MessageObject> fmessages = new ArrayList();
            if (this.forwaringMessage != null) {
                fmessages.add(this.forwaringMessage);
                this.forwaringMessage = null;
            } else {
                for (int a = attach_gallery; a >= 0; a--) {
                    ArrayList<Integer> arrayList = new ArrayList(this.selectedMessagesIds[a].keySet());
                    Collections.sort(arrayList);
                    for (int b = attach_photo; b < arrayList.size(); b += attach_gallery) {
                        Integer id = (Integer) arrayList.get(b);
                        MessageObject message = (MessageObject) this.selectedMessagesIds[a].get(id);
                        if (message != null && id.intValue() > 0) {
                            fmessages.add(message);
                        }
                    }
                    this.selectedMessagesCanCopyIds[a].clear();
                    this.selectedMessagesIds[a].clear();
                }
                this.cantDeleteMessagesCount = attach_photo;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            if (did != this.dialog_id) {
                int lower_part = (int) did;
                if (lower_part != 0) {
                    Bundle args = new Bundle();
                    args.putBoolean("scrollToTopOnResume", this.scrollToTopOnResume);
                    if (lower_part > 0) {
                        args.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args.putInt("chat_id", -lower_part);
                    }
                    if (MessagesController.checkCanOpenChat(args, activity)) {
                        ChatActivity chatActivity = new ChatActivity(args);
                        if (presentFragment(chatActivity, true)) {
                            chatActivity.showReplyPanel(true, null, fmessages, null, false, false);
                            if (!AndroidUtilities.isTablet()) {
                                removeSelfFromStack();
                                return;
                            }
                            return;
                        }
                        activity.finishFragment();
                        return;
                    }
                    return;
                }
                activity.finishFragment();
                return;
            }
            activity.finishFragment();
            moveScrollToLastMessage();
            showReplyPanel(true, null, fmessages, null, false, AndroidUtilities.isTablet());
            if (AndroidUtilities.isTablet()) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            updateVisibleRows();
        }
    }

    public boolean onBackPressed() {
        if (this.actionBar.isActionModeShowed()) {
            for (int a = attach_gallery; a >= 0; a--) {
                this.selectedMessagesIds[a].clear();
                this.selectedMessagesCanCopyIds[a].clear();
            }
            this.chatActivityEnterView.setEditingMessageObject(null, false);
            this.actionBar.hideActionMode();
            updatePinnedMessageView(true);
            this.cantDeleteMessagesCount = attach_photo;
            updateVisibleRows();
            return false;
        } else if (!this.chatActivityEnterView.isPopupShowing()) {
            return true;
        } else {
            this.chatActivityEnterView.hidePopup(true);
            return false;
        }
    }

    private void updateVisibleRows() {
        if (this.chatListView != null) {
            int count = this.chatListView.getChildCount();
            MessageObject editingMessageObject = this.chatActivityEnterView != null ? this.chatActivityEnterView.getEditingMessageObject() : null;
            for (int a = attach_photo; a < count; a += attach_gallery) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    boolean disableSelection = false;
                    boolean selected = false;
                    if (this.actionBar.isActionModeShowed()) {
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject != editingMessageObject) {
                            if (!this.selectedMessagesIds[messageObject.getDialogId() == this.dialog_id ? attach_photo : attach_gallery].containsKey(Integer.valueOf(messageObject.getId()))) {
                                view.setBackgroundColor(attach_photo);
                                disableSelection = true;
                            }
                        }
                        view.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                        selected = true;
                        disableSelection = true;
                    } else {
                        view.setBackgroundColor(attach_photo);
                    }
                    cell.setMessageObject(cell.getMessageObject());
                    boolean z = !disableSelection;
                    boolean z2 = disableSelection && selected;
                    cell.setCheckPressed(z, z2);
                    z2 = (this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || cell.getMessageObject() == null || cell.getMessageObject().getId() != this.highlightMessageId) ? false : true;
                    cell.setHighlighted(z2);
                    if (this.searchContainer == null || this.searchContainer.getVisibility() != 0 || MessagesSearchQuery.getLastSearchQuery() == null) {
                        cell.setHighlightedText(null);
                    } else {
                        cell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
                    }
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell cell2 = (ChatActionCell) view;
                    cell2.setMessageObject(cell2.getMessageObject());
                }
            }
        }
    }

    private void checkEditTimer() {
        if (this.chatActivityEnterView != null) {
            MessageObject messageObject = this.chatActivityEnterView.getEditingMessageObject();
            if (messageObject != null) {
                int dt = (MessagesController.getInstance().maxEditTime + 300) - Math.abs(ConnectionsManager.getInstance().getCurrentTime() - messageObject.messageOwner.date);
                if (dt > 0) {
                    if (dt <= 300) {
                        if (this.actionModeSubTextView.getVisibility() != 0) {
                            this.actionModeSubTextView.setVisibility(attach_photo);
                        }
                        SimpleTextView simpleTextView = this.actionModeSubTextView;
                        Object[] objArr = new Object[attach_gallery];
                        Object[] objArr2 = new Object[attach_video];
                        objArr2[attach_photo] = Integer.valueOf(dt / 60);
                        objArr2[attach_gallery] = Integer.valueOf(dt % 60);
                        objArr[attach_photo] = String.format("%d:%02d", objArr2);
                        simpleTextView.setText(LocaleController.formatString("TimeToEdit", C0691R.string.TimeToEdit, objArr));
                    } else if (this.actionModeSubTextView.getVisibility() != 8) {
                        this.actionModeSubTextView.setVisibility(8);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ChatActivity.this.checkEditTimer();
                        }
                    }, 1000);
                    return;
                }
                this.editDoneItem.setVisibility(8);
                this.actionModeSubTextView.setText(LocaleController.formatString("TimeToEditExpired", C0691R.string.TimeToEditExpired, new Object[attach_photo]));
            }
        }
    }

    private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject startMessageObject, boolean playingUnreadMedia) {
        ArrayList<MessageObject> messageObjects = new ArrayList();
        messageObjects.add(startMessageObject);
        int messageId = startMessageObject.getId();
        if (messageId != 0) {
            for (int a = this.messages.size() - 1; a >= 0; a--) {
                MessageObject messageObject = (MessageObject) this.messages.get(a);
                if (((this.currentEncryptedChat == null && messageObject.getId() > messageId) || (this.currentEncryptedChat != null && messageObject.getId() < messageId)) && messageObject.isVoice() && (!playingUnreadMedia || (messageObject.isContentUnread() && !messageObject.isOut()))) {
                    messageObjects.add(messageObject);
                }
            }
        }
        return messageObjects;
    }

    private void alertUserOpenError(MessageObject message) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            if (message.type == attach_audio) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", C0691R.string.NoPlayerInstalled));
            } else {
                Object[] objArr = new Object[attach_gallery];
                objArr[attach_photo] = message.getDocument().mime_type;
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0691R.string.NoHandleAppInstalled, objArr));
            }
            showDialog(builder.create());
        }
    }

    private void openSearchWithText(String text) {
        this.avatarContainer.setVisibility(8);
        this.headerItem.setVisibility(8);
        this.attachItem.setVisibility(8);
        this.searchItem.setVisibility(attach_photo);
        updateSearchButtons(attach_photo, attach_photo, attach_photo);
        updateBottomOverlay();
        this.openSearchKeyboard = text == null;
        this.searchItem.openSearch(this.openSearchKeyboard);
        if (text != null) {
            this.searchItem.getSearchField().setText(text);
            this.searchItem.getSearchField().setSelection(this.searchItem.getSearchField().length());
            MessagesSearchQuery.searchMessagesInChat(text, this.dialog_id, this.mergeDialogId, this.classGuid, attach_photo);
        }
    }

    public void updatePhotoAtIndex(int index) {
    }

    public boolean isSecretChat() {
        return this.currentEncryptedChat != null;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    public EncryptedChat getCurrentEncryptedChat() {
        return this.currentEncryptedChat;
    }

    public ChatFull getCurrentChatInfo() {
        return this.info;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        int count = this.chatListView.getChildCount();
        for (int a = attach_photo; a < count; a += attach_gallery) {
            ImageReceiver imageReceiver = null;
            View view = this.chatListView.getChildAt(a);
            MessageObject message;
            if (!(view instanceof ChatMessageCell)) {
                if (view instanceof ChatActionCell) {
                    ChatActionCell cell = (ChatActionCell) view;
                    message = cell.getMessageObject();
                    if (message != null) {
                        if (messageObject == null) {
                            if (!(fileLocation == null || message.photoThumbs == null)) {
                                for (int b = attach_photo; b < message.photoThumbs.size(); b += attach_gallery) {
                                    PhotoSize photoSize = (PhotoSize) message.photoThumbs.get(b);
                                    if (photoSize.location.volume_id == fileLocation.volume_id && photoSize.location.local_id == fileLocation.local_id) {
                                        imageReceiver = cell.getPhotoImage();
                                        break;
                                    }
                                }
                            }
                        } else if (message.getId() == messageObject.getId()) {
                            imageReceiver = cell.getPhotoImage();
                        }
                    }
                }
            } else if (messageObject != null) {
                ChatMessageCell cell2 = (ChatMessageCell) view;
                message = cell2.getMessageObject();
                if (message != null && message.getId() == messageObject.getId()) {
                    imageReceiver = cell2.getPhotoImage();
                }
            }
            if (imageReceiver != null) {
                int[] coords = new int[attach_video];
                view.getLocationInWindow(coords);
                PlaceProviderObject object = new PlaceProviderObject();
                object.viewX = coords[attach_photo];
                object.viewY = coords[attach_gallery] - AndroidUtilities.statusBarHeight;
                object.parentView = this.chatListView;
                object.imageReceiver = imageReceiver;
                object.thumb = imageReceiver.getBitmap();
                object.radius = imageReceiver.getRoundRadius();
                if ((view instanceof ChatActionCell) && this.currentChat != null) {
                    object.dialogId = -this.currentChat.id;
                }
                if ((this.pinnedMessageView == null || this.pinnedMessageView.getTag() != null) && (this.reportSpamView == null || this.reportSpamView.getTag() != null)) {
                    return object;
                }
                object.clipTopAddition = AndroidUtilities.dp(48.0f);
                return object;
            }
        }
        return null;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
    }

    public void willHidePhotoViewer() {
    }

    public boolean isPhotoChecked(int index) {
        return false;
    }

    public void setPhotoChecked(int index) {
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public void sendButtonPressed(int index) {
    }

    public int getSelectedCount() {
        return attach_photo;
    }

    public void sendPhoto(PhotoEntry photoEntry) {
        if (photoEntry.imagePath != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry.imagePath, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (photoEntry.path != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry.path, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        }
    }

    public void showOpenUrlAlert(String url) {
        boolean z = true;
        if (Browser.isInternalUrl(url)) {
            Context parentActivity = getParentActivity();
            if (this.inlineReturn != 0) {
                z = false;
            }
            Browser.openUrl(parentActivity, url, z);
            return;
        }
        Builder builder = new Builder(getParentActivity());
        Object[] objArr = new Object[attach_gallery];
        objArr[attach_photo] = url;
        builder.setMessage(LocaleController.formatString("OpenUrlAlert", C0691R.string.OpenUrlAlert, objArr));
        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("Open", C0691R.string.Open), new AnonymousClass83(url));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index != -1) {
            this.messages.remove(index);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(((this.chatAdapter.messagesStartRow + this.messages.size()) - index) - 1);
            }
        }
    }
}
