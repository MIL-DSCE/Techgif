package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.AboutLinkCell.AboutLinkCellDelegate;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ProfileActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate, PhotoViewerProvider {
    private static final int add_contact = 1;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int convert_to_supergroup = 13;
    private static final int delete_contact = 5;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int edit_name = 8;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int set_admins = 11;
    private static final int share = 10;
    private static final int share_contact = 3;
    private int addMemberRow;
    private boolean allowProfileAnimation;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int blockedUsersRow;
    private BotInfo botInfo;
    private int channelInfoRow;
    private int channelNameRow;
    private int chat_id;
    private int convertHelpRow;
    private int convertRow;
    private boolean creatingChat;
    private Chat currentChat;
    private EncryptedChat currentEncryptedChat;
    private long dialog_id;
    private int emptyRow;
    private int emptyRowChat;
    private int emptyRowChat2;
    private int extraHeight;
    private ChatFull info;
    private int initialAnimationExtraHeight;
    private LinearLayoutManager layoutManager;
    private int leaveChannelRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int loadMoreMembersRow;
    private boolean loadingUsers;
    private int managementRow;
    private int membersEndRow;
    private int membersRow;
    private int membersSectionRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView;
    private int onlineCount;
    private SimpleTextView[] onlineTextView;
    private boolean openAnimationInProgress;
    private HashMap<Integer, ChatParticipant> participantsMap;
    private int phoneRow;
    private boolean playProfileAnimation;
    private int rowCount;
    private int sectionRow;
    private int selectedUser;
    private int settingsKeyRow;
    private int settingsNotificationsRow;
    private int settingsTimerRow;
    private int sharedMediaRow;
    private ArrayList<Integer> sortedUsers;
    private int startSecretChatRow;
    private TopView topView;
    private int totalMediaCount;
    private int totalMediaCountMerge;
    private boolean userBlocked;
    private int userInfoRow;
    private int userSectionRow;
    private int user_id;
    private int usernameRow;
    private boolean usersEndReached;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: org.telegram.ui.ProfileActivity.14 */
    class AnonymousClass14 implements OnClickListener {
        final /* synthetic */ User val$user;

        AnonymousClass14(User user) {
            this.val$user = user;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + this.val$user.username));
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.15 */
    class AnonymousClass15 implements OnClickListener {
        final /* synthetic */ User val$user;

        AnonymousClass15(User user) {
            this.val$user = user;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + this.val$user.phone));
                    intent.addFlags(268435456);
                    ProfileActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            } else if (i == ProfileActivity.add_contact) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + this.val$user.phone));
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.16 */
    class AnonymousClass16 implements OnClickListener {
        final /* synthetic */ int val$position;

        AnonymousClass16(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                String about;
                if (this.val$position == ProfileActivity.this.channelInfoRow) {
                    about = ProfileActivity.this.info.about;
                } else {
                    about = MessagesController.getInstance().getUserAbout(ProfileActivity.this.botInfo.user_id);
                }
                if (about != null) {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", about));
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.1 */
    class C13951 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C13951(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ProfileActivity.this.currentChat = MessagesStorage.getInstance().getChat(ProfileActivity.this.chat_id);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.22 */
    class AnonymousClass22 implements Runnable {
        final /* synthetic */ Object[] val$args;

        AnonymousClass22(Object[] objArr) {
            this.val$args = objArr;
        }

        public void run() {
            NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            EncryptedChat encryptedChat = this.val$args[0];
            Bundle args2 = new Bundle();
            args2.putInt("enc_id", encryptedChat.id);
            ProfileActivity.this.presentFragment(new ChatActivity(args2), true);
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.24 */
    class AnonymousClass24 implements Runnable {
        final /* synthetic */ AnimatorSet val$animatorSet;

        AnonymousClass24(AnimatorSet animatorSet) {
            this.val$animatorSet = animatorSet;
        }

        public void run() {
            this.val$animatorSet.start();
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.5 */
    class C13985 extends FrameLayout {
        C13985(Context x0) {
            super(x0);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ProfileActivity.this.checkListViewScroll();
        }
    }

    private class TopView extends View {
        private int currentColor;
        private Paint paint;

        public TopView(Context context) {
            super(context);
            this.paint = new Paint();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int color) {
            if (color != this.currentColor) {
                this.paint.setColor(color);
                invalidate();
            }
        }

        protected void onDraw(Canvas canvas) {
            int height = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (ProfileActivity.this.extraHeight + height), this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, ProfileActivity.this.extraHeight + height);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.18 */
    class AnonymousClass18 implements RequestDelegate {
        final /* synthetic */ int val$delay;
        final /* synthetic */ TL_channels_getParticipants val$req;

        /* renamed from: org.telegram.ui.ProfileActivity.18.1 */
        class C13941 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C13941(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_channels_channelParticipants res = this.val$response;
                    MessagesController.getInstance().putUsers(res.users, false);
                    if (res.users.size() != Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                        ProfileActivity.this.usersEndReached = true;
                    }
                    if (AnonymousClass18.this.val$req.offset == 0) {
                        ProfileActivity.this.participantsMap.clear();
                        ProfileActivity.this.info.participants = new TL_chatParticipants();
                        MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                        MessagesStorage.getInstance().updateChannelUsers(ProfileActivity.this.chat_id, res.participants);
                    }
                    for (int a = 0; a < res.participants.size(); a += ProfileActivity.add_contact) {
                        TL_chatChannelParticipant participant = new TL_chatChannelParticipant();
                        participant.channelParticipant = (ChannelParticipant) res.participants.get(a);
                        participant.inviter_id = participant.channelParticipant.inviter_id;
                        participant.user_id = participant.channelParticipant.user_id;
                        participant.date = participant.channelParticipant.date;
                        if (!ProfileActivity.this.participantsMap.containsKey(Integer.valueOf(participant.user_id))) {
                            ProfileActivity.this.info.participants.participants.add(participant);
                            ProfileActivity.this.participantsMap.put(Integer.valueOf(participant.user_id), participant);
                        }
                    }
                }
                ProfileActivity.this.updateOnlineCount();
                ProfileActivity.this.loadingUsers = false;
                ProfileActivity.this.updateRowsIds();
                if (ProfileActivity.this.listAdapter != null) {
                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        AnonymousClass18(TL_channels_getParticipants tL_channels_getParticipants, int i) {
            this.val$req = tL_channels_getParticipants;
            this.val$delay = i;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C13941(error, response), (long) this.val$delay);
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.23 */
    class AnonymousClass23 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ Runnable val$callback;

        AnonymousClass23(Runnable runnable) {
            this.val$callback = runnable;
        }

        public void onAnimationEnd(Animator animation) {
            if (VERSION.SDK_INT > 15) {
                ProfileActivity.this.listView.setLayerType(0, null);
            }
            if (ProfileActivity.this.animatingItem != null) {
                ProfileActivity.this.actionBar.createMenu().clearItems();
                ProfileActivity.this.animatingItem = null;
            }
            this.val$callback.run();
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.2 */
    class C19272 implements AvatarUpdaterDelegate {
        C19272() {
        }

        public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
            if (ProfileActivity.this.chat_id != 0) {
                MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, file);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.3 */
    class C19283 extends ActionBar {
        C19283(Context x0) {
            super(x0);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.4 */
    class C19304 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ProfileActivity.4.1 */
        class C13961 implements OnClickListener {
            C13961() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (ProfileActivity.this.userBlocked) {
                    MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                } else {
                    MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                }
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.4.2 */
        class C13972 implements OnClickListener {
            final /* synthetic */ User val$user;

            C13972(User user) {
                this.val$user = user;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<User> arrayList = new ArrayList();
                arrayList.add(this.val$user);
                ContactsController.getInstance().deleteContact(arrayList);
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.4.3 */
        class C19293 implements DialogsActivityDelegate {
            final /* synthetic */ User val$user;

            C19293(User user) {
                this.val$user = user;
            }

            public void didSelectDialog(DialogsActivity fragment, long did, boolean param) {
                Bundle args = new Bundle();
                args.putBoolean("scrollToTopOnResume", true);
                args.putInt("chat_id", -((int) did));
                if (MessagesController.checkCanOpenChat(args, fragment)) {
                    NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    MessagesController.getInstance().addUserToChat(-((int) did), this.val$user, null, 0, null, ProfileActivity.this);
                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                    ProfileActivity.this.removeSelfFromStack();
                }
            }
        }

        C19304() {
        }

        public void onItemClick(int id) {
            if (ProfileActivity.this.getParentActivity() != null) {
                if (id == -1) {
                    ProfileActivity.this.finishFragment();
                } else if (id == ProfileActivity.block_contact) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user == null) {
                        return;
                    }
                    if (!user.bot) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        if (ProfileActivity.this.userBlocked) {
                            builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0691R.string.AreYouSureUnblockContact));
                        } else {
                            builder.setMessage(LocaleController.getString("AreYouSureBlockContact", C0691R.string.AreYouSureBlockContact));
                        }
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C13961());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else if (ProfileActivity.this.userBlocked) {
                        MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                        SendMessagesHelper.getInstance().sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                        ProfileActivity.this.finishFragment();
                    } else {
                        MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                    }
                } else if (id == ProfileActivity.add_contact) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    args = new Bundle();
                    args.putInt("user_id", user.id);
                    args.putBoolean("addContact", true);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (id == ProfileActivity.share_contact) {
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", ProfileActivity.add_contact);
                    args.putString("selectAlertString", LocaleController.getString("SendContactTo", C0691R.string.SendContactTo));
                    args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0691R.string.SendContactToGroup));
                    r0 = new DialogsActivity(args);
                    r0.setDelegate(ProfileActivity.this);
                    ProfileActivity.this.presentFragment(r0);
                } else if (id == ProfileActivity.edit_contact) {
                    args = new Bundle();
                    args.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (id == ProfileActivity.delete_contact) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null && ProfileActivity.this.getParentActivity() != null) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", C0691R.string.AreYouSureDeleteContact));
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C13972(user));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    }
                } else if (id == ProfileActivity.leave_group) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (id == ProfileActivity.edit_name) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ChangeChatNameActivity(args));
                } else if (id == ProfileActivity.edit_channel) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    r0 = new ChannelEditActivity(args);
                    r0.setInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r0);
                } else if (id == ProfileActivity.invite_to_group) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putInt("dialogsType", ProfileActivity.block_contact);
                        Object[] objArr = new Object[ProfileActivity.block_contact];
                        objArr[0] = UserObject.getUserName(user);
                        objArr[ProfileActivity.add_contact] = "%1$s";
                        args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0691R.string.AddToTheGroupTitle, objArr));
                        r0 = new DialogsActivity(args);
                        r0.setDelegate(new C19293(user));
                        ProfileActivity.this.presentFragment(r0);
                    }
                } else if (id == ProfileActivity.share) {
                    try {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user != null) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            String about = MessagesController.getInstance().getUserAbout(ProfileActivity.this.botInfo.user_id);
                            Object[] objArr2;
                            if (ProfileActivity.this.botInfo == null || about == null) {
                                objArr2 = new Object[ProfileActivity.add_contact];
                                objArr2[0] = user.username;
                                intent.putExtra("android.intent.extra.TEXT", String.format("https://telegram.me/%s", objArr2));
                            } else {
                                objArr2 = new Object[ProfileActivity.block_contact];
                                objArr2[0] = about;
                                objArr2[ProfileActivity.add_contact] = user.username;
                                intent.putExtra("android.intent.extra.TEXT", String.format("%s https://telegram.me/%s", objArr2));
                            }
                            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", C0691R.string.BotShare)), 500);
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                } else if (id == ProfileActivity.set_admins) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    r0 = new SetAdminsActivity(args);
                    r0.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r0);
                } else if (id == ProfileActivity.convert_to_supergroup) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ConvertGroupActivity(args));
                } else if (id == ProfileActivity.add_shortcut) {
                    try {
                        long did;
                        if (ProfileActivity.this.currentEncryptedChat != null) {
                            did = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                        } else if (ProfileActivity.this.user_id != 0) {
                            did = (long) ProfileActivity.this.user_id;
                        } else if (ProfileActivity.this.chat_id != 0) {
                            did = (long) (-ProfileActivity.this.chat_id);
                        } else {
                            return;
                        }
                        AndroidUtilities.installShortcut(did);
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.8 */
    class C19318 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ProfileActivity.8.1 */
        class C13991 implements OnClickListener {
            C13991() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.creatingChat = true;
                SecretChatHelper.getInstance().startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id)));
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.8.2 */
        class C14002 implements OnClickListener {
            C14002() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
            }
        }

        C19318() {
        }

        public void onItemClick(View view, int position) {
            if (ProfileActivity.this.getParentActivity() != null) {
                Bundle args;
                if (position == ProfileActivity.this.sharedMediaRow) {
                    args = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        args.putLong("dialog_id", ProfileActivity.this.dialog_id != 0 ? ProfileActivity.this.dialog_id : (long) ProfileActivity.this.user_id);
                    } else {
                        args.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    MediaActivity fragment = new MediaActivity(args);
                    fragment.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (position == ProfileActivity.this.settingsKeyRow) {
                    args = new Bundle();
                    args.putInt("chat_id", (int) (ProfileActivity.this.dialog_id >> 32));
                    ProfileActivity.this.presentFragment(new IdenticonActivity(args));
                } else if (position == ProfileActivity.this.settingsTimerRow) {
                    ProfileActivity.this.showDialog(AndroidUtilities.buildTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
                } else if (position == ProfileActivity.this.settingsNotificationsRow) {
                    args = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        args.putLong("dialog_id", ProfileActivity.this.dialog_id == 0 ? (long) ProfileActivity.this.user_id : ProfileActivity.this.dialog_id);
                    } else if (ProfileActivity.this.chat_id != 0) {
                        args.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(args));
                } else if (position == ProfileActivity.this.startSecretChatRow) {
                    builder = new Builder(ProfileActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureSecretChat", C0691R.string.AreYouSureSecretChat));
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C13991());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    ProfileActivity.this.showDialog(builder.create());
                } else if (position > ProfileActivity.this.emptyRowChat2 && position < ProfileActivity.this.membersEndRow) {
                    int user_id;
                    if (ProfileActivity.this.sortedUsers.isEmpty()) {
                        user_id = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get((position - ProfileActivity.this.emptyRowChat2) - 1)).user_id;
                    } else {
                        user_id = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((position - ProfileActivity.this.emptyRowChat2) - 1)).intValue())).user_id;
                    }
                    if (user_id != UserConfig.getClientUserId()) {
                        args = new Bundle();
                        args.putInt("user_id", user_id);
                        ProfileActivity.this.presentFragment(new ProfileActivity(args));
                    }
                } else if (position == ProfileActivity.this.addMemberRow) {
                    ProfileActivity.this.openAddMember();
                } else if (position == ProfileActivity.this.channelNameRow) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        if (ProfileActivity.this.info.about == null || ProfileActivity.this.info.about.length() <= 0) {
                            intent.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\nhttps://telegram.me/" + ProfileActivity.this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\n" + ProfileActivity.this.info.about + "\nhttps://telegram.me/" + ProfileActivity.this.currentChat.username);
                        }
                        ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", C0691R.string.BotShare)), 500);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                } else if (position == ProfileActivity.this.leaveChannelRow) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (position == ProfileActivity.this.membersRow || position == ProfileActivity.this.blockedUsersRow || position == ProfileActivity.this.managementRow) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    if (position == ProfileActivity.this.blockedUsersRow) {
                        args.putInt("type", 0);
                    } else if (position == ProfileActivity.this.managementRow) {
                        args.putInt("type", ProfileActivity.add_contact);
                    } else if (position == ProfileActivity.this.membersRow) {
                        args.putInt("type", ProfileActivity.block_contact);
                    }
                    ProfileActivity.this.presentFragment(new ChannelUsersActivity(args));
                } else if (position == ProfileActivity.this.convertRow) {
                    builder = new Builder(ProfileActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("ConvertGroupAlert", C0691R.string.ConvertGroupAlert));
                    builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", C0691R.string.ConvertGroupAlertWarning));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14002());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    ProfileActivity.this.showDialog(builder.create());
                } else {
                    ProfileActivity.this.processOnClickOrPress(position);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.9 */
    class C19339 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.ProfileActivity.9.1 */
        class C14031 implements OnClickListener {
            final /* synthetic */ ChatParticipant val$user;

            /* renamed from: org.telegram.ui.ProfileActivity.9.1.1 */
            class C19321 implements RequestDelegate {

                /* renamed from: org.telegram.ui.ProfileActivity.9.1.1.1 */
                class C14011 implements Runnable {
                    C14011() {
                    }

                    public void run() {
                        MessagesController.getInstance().loadFullChat(ProfileActivity.this.chat_id, 0, true);
                    }
                }

                /* renamed from: org.telegram.ui.ProfileActivity.9.1.1.2 */
                class C14022 implements Runnable {
                    final /* synthetic */ TL_error val$error;

                    C14022(TL_error tL_error) {
                        this.val$error = tL_error;
                    }

                    public void run() {
                        AlertsCreator.showAddUserAlert(this.val$error.text, ProfileActivity.this, false);
                    }
                }

                C19321() {
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.getInstance().processUpdates((Updates) response, false);
                        AndroidUtilities.runOnUIThread(new C14011(), 1000);
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C14022(error));
                }
            }

            C14031(ChatParticipant chatParticipant) {
                this.val$user = chatParticipant;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TL_chatChannelParticipant channelParticipant = this.val$user;
                    channelParticipant.channelParticipant = new TL_channelParticipantEditor();
                    channelParticipant.channelParticipant.inviter_id = UserConfig.getClientUserId();
                    channelParticipant.channelParticipant.user_id = this.val$user.user_id;
                    channelParticipant.channelParticipant.date = this.val$user.date;
                    TL_channels_editAdmin req = new TL_channels_editAdmin();
                    req.channel = MessagesController.getInputChannel(ProfileActivity.this.chat_id);
                    req.user_id = MessagesController.getInputUser(ProfileActivity.this.selectedUser);
                    req.role = new TL_channelRoleEditor();
                    ConnectionsManager.getInstance().sendRequest(req, new C19321());
                } else if (i == ProfileActivity.add_contact) {
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                }
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.9.2 */
        class C14042 implements OnClickListener {
            C14042() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                }
            }
        }

        C19339() {
        }

        public boolean onItemClick(View view, int position) {
            if (position <= ProfileActivity.this.emptyRowChat2 || position >= ProfileActivity.this.membersEndRow) {
                return ProfileActivity.this.processOnClickOrPress(position);
            }
            if (ProfileActivity.this.getParentActivity() == null) {
                return false;
            }
            ChatParticipant user;
            boolean allowKick = false;
            boolean allowSetAdmin = false;
            if (ProfileActivity.this.sortedUsers.isEmpty()) {
                user = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((position - ProfileActivity.this.emptyRowChat2) - 1);
            } else {
                user = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((position - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
            }
            ProfileActivity.this.selectedUser = user.user_id;
            if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) user).channelParticipant;
                if (user.user_id != UserConfig.getClientUserId()) {
                    if (ProfileActivity.this.currentChat.creator) {
                        allowKick = true;
                    } else if ((channelParticipant instanceof TL_channelParticipant) && (ProfileActivity.this.currentChat.editor || channelParticipant.inviter_id == UserConfig.getClientUserId())) {
                        allowKick = true;
                    }
                }
                allowSetAdmin = (channelParticipant instanceof TL_channelParticipant) && !MessagesController.getInstance().getUser(Integer.valueOf(user.user_id)).bot;
            } else if (user.user_id != UserConfig.getClientUserId()) {
                if (ProfileActivity.this.currentChat.creator) {
                    allowKick = true;
                } else if ((user instanceof TL_chatParticipant) && ((ProfileActivity.this.currentChat.admin && ProfileActivity.this.currentChat.admins_enabled) || user.inviter_id == UserConfig.getClientUserId())) {
                    allowKick = true;
                }
            }
            if (!allowKick) {
                return false;
            }
            Builder builder = new Builder(ProfileActivity.this.getParentActivity());
            CharSequence[] items;
            if (ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.currentChat.creator && allowSetAdmin) {
                items = new CharSequence[ProfileActivity.block_contact];
                items[0] = LocaleController.getString("SetAsAdmin", C0691R.string.SetAsAdmin);
                items[ProfileActivity.add_contact] = LocaleController.getString("KickFromGroup", C0691R.string.KickFromGroup);
                builder.setItems(items, new C14031(user));
            } else {
                String string;
                items = new CharSequence[ProfileActivity.add_contact];
                if (ProfileActivity.this.chat_id > 0) {
                    string = LocaleController.getString("KickFromGroup", C0691R.string.KickFromGroup);
                } else {
                    string = LocaleController.getString("KickFromBroadcast", C0691R.string.KickFromBroadcast);
                }
                items[0] = string;
                builder.setItems(items, new C14042());
            }
            ProfileActivity.this.showDialog(builder.create());
            return true;
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ProfileActivity.ListAdapter.1 */
        class C19341 extends TextDetailCell {
            C19341(Context x0) {
                super(x0);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == ProfileActivity.block_contact)) {
                    getBackground().setHotspot(event.getX(), event.getY());
                }
                return super.onTouchEvent(event);
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.ListAdapter.2 */
        class C19352 extends TextCell {
            C19352(Context x0) {
                super(x0);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == ProfileActivity.block_contact)) {
                    getBackground().setHotspot(event.getX(), event.getY());
                }
                return super.onTouchEvent(event);
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.ListAdapter.3 */
        class C19363 extends UserCell {
            C19363(Context x0, int x1, int x2, boolean x3) {
                super(x0, x1, x2, x3);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == ProfileActivity.block_contact)) {
                    getBackground().setHotspot(event.getX(), event.getY());
                }
                return super.onTouchEvent(event);
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity.ListAdapter.4 */
        class C19374 implements AboutLinkCellDelegate {
            C19374() {
            }

            public void didPressUrl(String url) {
                if (url.startsWith("@")) {
                    MessagesController.openByUserName(url.substring(ProfileActivity.add_contact), ProfileActivity.this, 0);
                } else if (url.startsWith("#")) {
                    DialogsActivity fragment = new DialogsActivity(null);
                    fragment.setSearchString(url);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (url.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > ProfileActivity.add_contact) {
                    BaseFragment previousFragment = (BaseFragment) ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (previousFragment instanceof ChatActivity) {
                        ProfileActivity.this.finishFragment();
                        ((ChatActivity) previousFragment).chatActivityEnterView.setCommand(null, url, false, false);
                    }
                }
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new EmptyCell(this.mContext);
                    break;
                case ProfileActivity.add_contact /*1*/:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.dp(72.0f), 0, 0, 0);
                    break;
                case ProfileActivity.block_contact /*2*/:
                    view = new C19341(this.mContext);
                    break;
                case ProfileActivity.share_contact /*3*/:
                    view = new C19352(this.mContext);
                    break;
                case ProfileActivity.edit_contact /*4*/:
                    view = new C19363(this.mContext, 61, 0, true);
                    break;
                case ProfileActivity.delete_contact /*5*/:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    view = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) view;
                    cell.setBackgroundResource(C0691R.drawable.greydivider);
                    Object[] objArr = new Object[ProfileActivity.add_contact];
                    objArr[0] = LocaleController.formatPluralString("Members", MessagesController.getInstance().maxMegagroupCount);
                    cell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", C0691R.string.ConvertGroupInfo, objArr)));
                    break;
                case ProfileActivity.leave_group /*7*/:
                    view = new LoadingCell(this.mContext);
                    break;
                case ProfileActivity.edit_name /*8*/:
                    view = new AboutLinkCell(this.mContext);
                    ((AboutLinkCell) view).setDelegate(new C19374());
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(org.telegram.messenger.support.widget.RecyclerView.ViewHolder r27, int r28) {
            /*
            r26 = this;
            r9 = 1;
            r20 = r27.getItemViewType();
            switch(r20) {
                case 0: goto L_0x00b4;
                case 1: goto L_0x0008;
                case 2: goto L_0x00fa;
                case 3: goto L_0x027e;
                case 4: goto L_0x05b0;
                case 5: goto L_0x0008;
                case 6: goto L_0x0008;
                case 7: goto L_0x0008;
                case 8: goto L_0x06f5;
                default: goto L_0x0008;
            };
        L_0x0008:
            r9 = 0;
        L_0x0009:
            if (r9 == 0) goto L_0x00b3;
        L_0x000b:
            r10 = 0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.user_id;
            if (r20 == 0) goto L_0x0769;
        L_0x0018:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.phoneRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0028:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsTimerRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0038:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsKeyRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0048:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsNotificationsRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0058:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.sharedMediaRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0068:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.startSecretChatRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0078:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.usernameRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0098;
        L_0x0088:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.userInfoRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0766;
        L_0x0098:
            r10 = 1;
        L_0x0099:
            if (r10 == 0) goto L_0x083a;
        L_0x009b:
            r0 = r27;
            r0 = r0.itemView;
            r20 = r0;
            r20 = r20.getBackground();
            if (r20 != 0) goto L_0x00b3;
        L_0x00a7:
            r0 = r27;
            r0 = r0.itemView;
            r20 = r0;
            r21 = 2130837790; // 0x7f02011e float:1.7280544E38 double:1.052773749E-314;
            r20.setBackgroundResource(r21);
        L_0x00b3:
            return;
        L_0x00b4:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.emptyRowChat;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x00d4;
        L_0x00c4:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.emptyRowChat2;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x00e7;
        L_0x00d4:
            r0 = r27;
            r0 = r0.itemView;
            r20 = r0;
            r20 = (org.telegram.ui.Cells.EmptyCell) r20;
            r21 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
            r21 = org.telegram.messenger.AndroidUtilities.dp(r21);
            r20.setHeight(r21);
            goto L_0x0009;
        L_0x00e7:
            r0 = r27;
            r0 = r0.itemView;
            r20 = r0;
            r20 = (org.telegram.ui.Cells.EmptyCell) r20;
            r21 = 1108344832; // 0x42100000 float:36.0 double:5.47595105E-315;
            r21 = org.telegram.messenger.AndroidUtilities.dp(r21);
            r20.setHeight(r21);
            goto L_0x0009;
        L_0x00fa:
            r0 = r27;
            r0 = r0.itemView;
            r16 = r0;
            r16 = (org.telegram.ui.Cells.TextDetailCell) r16;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.phoneRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x017e;
        L_0x0112:
            r20 = org.telegram.messenger.MessagesController.getInstance();
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r21 = r21.user_id;
            r21 = java.lang.Integer.valueOf(r21);
            r17 = r20.getUser(r21);
            r0 = r17;
            r0 = r0.phone;
            r20 = r0;
            if (r20 == 0) goto L_0x0174;
        L_0x0130:
            r0 = r17;
            r0 = r0.phone;
            r20 = r0;
            r20 = r20.length();
            if (r20 == 0) goto L_0x0174;
        L_0x013c:
            r20 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
            r21 = new java.lang.StringBuilder;
            r21.<init>();
            r22 = "+";
            r21 = r21.append(r22);
            r0 = r17;
            r0 = r0.phone;
            r22 = r0;
            r21 = r21.append(r22);
            r21 = r21.toString();
            r14 = r20.format(r21);
        L_0x015d:
            r20 = "PhoneMobile";
            r21 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r21 = 2130837866; // 0x7f02016a float:1.7280698E38 double:1.0527737864E-314;
            r0 = r16;
            r1 = r20;
            r2 = r21;
            r0.setTextAndValueAndIcon(r14, r1, r2);
            goto L_0x0009;
        L_0x0174:
            r20 = "NumberUnknown";
            r21 = 2131165992; // 0x7f070328 float:1.7946217E38 double:1.052935902E-314;
            r14 = org.telegram.messenger.LocaleController.getString(r20, r21);
            goto L_0x015d;
        L_0x017e:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.usernameRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x01e8;
        L_0x018e:
            r20 = org.telegram.messenger.MessagesController.getInstance();
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r21 = r21.user_id;
            r21 = java.lang.Integer.valueOf(r21);
            r17 = r20.getUser(r21);
            if (r17 == 0) goto L_0x01e5;
        L_0x01a6:
            r0 = r17;
            r0 = r0.username;
            r20 = r0;
            if (r20 == 0) goto L_0x01e5;
        L_0x01ae:
            r0 = r17;
            r0 = r0.username;
            r20 = r0;
            r20 = r20.length();
            if (r20 == 0) goto L_0x01e5;
        L_0x01ba:
            r20 = new java.lang.StringBuilder;
            r20.<init>();
            r21 = "@";
            r20 = r20.append(r21);
            r0 = r17;
            r0 = r0.username;
            r21 = r0;
            r20 = r20.append(r21);
            r14 = r20.toString();
        L_0x01d3:
            r20 = "Username";
            r21 = 2131166286; // 0x7f07044e float:1.7946813E38 double:1.0529360475E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r16;
            r1 = r20;
            r0.setTextAndValue(r14, r1);
            goto L_0x0009;
        L_0x01e5:
            r14 = "-";
            goto L_0x01d3;
        L_0x01e8:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.channelNameRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0009;
        L_0x01f8:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.currentChat;
            if (r20 == 0) goto L_0x027b;
        L_0x0204:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.currentChat;
            r0 = r20;
            r0 = r0.username;
            r20 = r0;
            if (r20 == 0) goto L_0x027b;
        L_0x0216:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.currentChat;
            r0 = r20;
            r0 = r0.username;
            r20 = r0;
            r20 = r20.length();
            if (r20 == 0) goto L_0x027b;
        L_0x022c:
            r20 = new java.lang.StringBuilder;
            r20.<init>();
            r21 = "@";
            r20 = r20.append(r21);
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r21 = r21.currentChat;
            r0 = r21;
            r0 = r0.username;
            r21 = r0;
            r20 = r20.append(r21);
            r14 = r20.toString();
        L_0x024f:
            r20 = new java.lang.StringBuilder;
            r20.<init>();
            r21 = "telegram.me/";
            r20 = r20.append(r21);
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r21 = r21.currentChat;
            r0 = r21;
            r0 = r0.username;
            r21 = r0;
            r20 = r20.append(r21);
            r20 = r20.toString();
            r0 = r16;
            r1 = r20;
            r0.setTextAndValue(r14, r1);
            goto L_0x0009;
        L_0x027b:
            r14 = "-";
            goto L_0x024f;
        L_0x027e:
            r0 = r27;
            r15 = r0.itemView;
            r15 = (org.telegram.ui.Cells.TextCell) r15;
            r20 = -14606047; // 0xffffffffff212121 float:-2.1417772E38 double:NaN;
            r0 = r20;
            r15.setTextColor(r0);
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.sharedMediaRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x030b;
        L_0x029c:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.totalMediaCount;
            r21 = -1;
            r0 = r20;
            r1 = r21;
            if (r0 != r1) goto L_0x02c9;
        L_0x02ae:
            r20 = "Loading";
            r21 = 2131165797; // 0x7f070265 float:1.7945821E38 double:1.052935806E-314;
            r19 = org.telegram.messenger.LocaleController.getString(r20, r21);
        L_0x02b7:
            r20 = "SharedMedia";
            r21 = 2131166203; // 0x7f0703fb float:1.7946645E38 double:1.0529360065E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r1 = r19;
            r15.setTextAndValue(r0, r1);
            goto L_0x0009;
        L_0x02c9:
            r21 = "%d";
            r20 = 1;
            r0 = r20;
            r0 = new java.lang.Object[r0];
            r22 = r0;
            r23 = 0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r24 = r20.totalMediaCount;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.totalMediaCountMerge;
            r25 = -1;
            r0 = r20;
            r1 = r25;
            if (r0 == r1) goto L_0x0308;
        L_0x02f1:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.totalMediaCountMerge;
        L_0x02fb:
            r20 = r20 + r24;
            r20 = java.lang.Integer.valueOf(r20);
            r22[r23] = r20;
            r19 = java.lang.String.format(r21, r22);
            goto L_0x02b7;
        L_0x0308:
            r20 = 0;
            goto L_0x02fb;
        L_0x030b:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsTimerRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0364;
        L_0x031b:
            r20 = org.telegram.messenger.MessagesController.getInstance();
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r22 = r21.dialog_id;
            r21 = 32;
            r22 = r22 >> r21;
            r0 = r22;
            r0 = (int) r0;
            r21 = r0;
            r21 = java.lang.Integer.valueOf(r21);
            r11 = r20.getEncryptedChat(r21);
            r0 = r11.ttl;
            r20 = r0;
            if (r20 != 0) goto L_0x035b;
        L_0x0340:
            r20 = "ShortMessageLifetimeForever";
            r21 = 2131166207; // 0x7f0703ff float:1.7946653E38 double:1.0529360085E-314;
            r19 = org.telegram.messenger.LocaleController.getString(r20, r21);
        L_0x0349:
            r20 = "MessageLifetime";
            r21 = 2131165832; // 0x7f070288 float:1.7945892E38 double:1.052935823E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r1 = r19;
            r15.setTextAndValue(r0, r1);
            goto L_0x0009;
        L_0x035b:
            r0 = r11.ttl;
            r20 = r0;
            r19 = org.telegram.messenger.AndroidUtilities.formatTTLString(r20);
            goto L_0x0349;
        L_0x0364:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsNotificationsRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0389;
        L_0x0374:
            r20 = "NotificationsAndSounds";
            r21 = 2131165980; // 0x7f07031c float:1.7946192E38 double:1.0529358963E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r21 = 2130837928; // 0x7f0201a8 float:1.7280824E38 double:1.052773817E-314;
            r0 = r20;
            r1 = r21;
            r15.setTextAndIcon(r0, r1);
            goto L_0x0009;
        L_0x0389:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.startSecretChatRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x03b1;
        L_0x0399:
            r20 = "StartEncryptedChat";
            r21 = 2131166225; // 0x7f070411 float:1.794669E38 double:1.0529360173E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            r20 = -13129447; // 0xffffffffff37a919 float:-2.4412673E38 double:NaN;
            r0 = r20;
            r15.setTextColor(r0);
            goto L_0x0009;
        L_0x03b1:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsKeyRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x03f8;
        L_0x03c1:
            r12 = new org.telegram.ui.Components.IdenticonDrawable;
            r12.<init>();
            r20 = org.telegram.messenger.MessagesController.getInstance();
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r22 = r21.dialog_id;
            r21 = 32;
            r22 = r22 >> r21;
            r0 = r22;
            r0 = (int) r0;
            r21 = r0;
            r21 = java.lang.Integer.valueOf(r21);
            r11 = r20.getEncryptedChat(r21);
            r12.setEncryptedChat(r11);
            r20 = "EncryptionKey";
            r21 = 2131165586; // 0x7f070192 float:1.7945393E38 double:1.0529357016E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setTextAndValueDrawable(r0, r12);
            goto L_0x0009;
        L_0x03f8:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.leaveChannelRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0420;
        L_0x0408:
            r20 = -1229511; // 0xffffffffffed3d39 float:NaN double:NaN;
            r0 = r20;
            r15.setTextColor(r0);
            r20 = "LeaveChannel";
            r21 = 2131165778; // 0x7f070252 float:1.7945783E38 double:1.0529357965E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            goto L_0x0009;
        L_0x0420:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.convertRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0448;
        L_0x0430:
            r20 = "UpgradeGroup";
            r21 = 2131166282; // 0x7f07044a float:1.7946805E38 double:1.0529360455E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            r20 = -13129447; // 0xffffffffff37a919 float:-2.4412673E38 double:NaN;
            r0 = r20;
            r15.setTextColor(r0);
            goto L_0x0009;
        L_0x0448:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.membersRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x04ac;
        L_0x0458:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.info;
            if (r20 == 0) goto L_0x049c;
        L_0x0464:
            r20 = "ChannelMembers";
            r21 = 2131165420; // 0x7f0700ec float:1.7945057E38 double:1.0529356196E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r21 = "%d";
            r22 = 1;
            r0 = r22;
            r0 = new java.lang.Object[r0];
            r22 = r0;
            r23 = 0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r24 = r0;
            r24 = r24.info;
            r0 = r24;
            r0 = r0.participants_count;
            r24 = r0;
            r24 = java.lang.Integer.valueOf(r24);
            r22[r23] = r24;
            r21 = java.lang.String.format(r21, r22);
            r0 = r20;
            r1 = r21;
            r15.setTextAndValue(r0, r1);
            goto L_0x0009;
        L_0x049c:
            r20 = "ChannelMembers";
            r21 = 2131165420; // 0x7f0700ec float:1.7945057E38 double:1.0529356196E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            goto L_0x0009;
        L_0x04ac:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.managementRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0510;
        L_0x04bc:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.info;
            if (r20 == 0) goto L_0x0500;
        L_0x04c8:
            r20 = "ChannelAdministrators";
            r21 = 2131165393; // 0x7f0700d1 float:1.7945002E38 double:1.0529356063E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r21 = "%d";
            r22 = 1;
            r0 = r22;
            r0 = new java.lang.Object[r0];
            r22 = r0;
            r23 = 0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r24 = r0;
            r24 = r24.info;
            r0 = r24;
            r0 = r0.admins_count;
            r24 = r0;
            r24 = java.lang.Integer.valueOf(r24);
            r22[r23] = r24;
            r21 = java.lang.String.format(r21, r22);
            r0 = r20;
            r1 = r21;
            r15.setTextAndValue(r0, r1);
            goto L_0x0009;
        L_0x0500:
            r20 = "ChannelAdministrators";
            r21 = 2131165393; // 0x7f0700d1 float:1.7945002E38 double:1.0529356063E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            goto L_0x0009;
        L_0x0510:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.blockedUsersRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0574;
        L_0x0520:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.info;
            if (r20 == 0) goto L_0x0564;
        L_0x052c:
            r20 = "ChannelBlockedUsers";
            r21 = 2131165398; // 0x7f0700d6 float:1.7945012E38 double:1.052935609E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r21 = "%d";
            r22 = 1;
            r0 = r22;
            r0 = new java.lang.Object[r0];
            r22 = r0;
            r23 = 0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r24 = r0;
            r24 = r24.info;
            r0 = r24;
            r0 = r0.kicked_count;
            r24 = r0;
            r24 = java.lang.Integer.valueOf(r24);
            r22[r23] = r24;
            r21 = java.lang.String.format(r21, r22);
            r0 = r20;
            r1 = r21;
            r15.setTextAndValue(r0, r1);
            goto L_0x0009;
        L_0x0564:
            r20 = "ChannelBlockedUsers";
            r21 = 2131165398; // 0x7f0700d6 float:1.7945012E38 double:1.052935609E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            goto L_0x0009;
        L_0x0574:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.addMemberRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0009;
        L_0x0584:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.chat_id;
            if (r20 <= 0) goto L_0x05a0;
        L_0x0590:
            r20 = "AddMember";
            r21 = 2131165264; // 0x7f070050 float:1.794474E38 double:1.0529355426E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            goto L_0x0009;
        L_0x05a0:
            r20 = "AddRecipient";
            r21 = 2131165266; // 0x7f070052 float:1.7944744E38 double:1.0529355435E-314;
            r20 = org.telegram.messenger.LocaleController.getString(r20, r21);
            r0 = r20;
            r15.setText(r0);
            goto L_0x0009;
        L_0x05b0:
            r0 = r27;
            r0 = r0.itemView;
            r18 = r0;
            r18 = (org.telegram.ui.Cells.UserCell) r18;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.sortedUsers;
            r20 = r20.isEmpty();
            if (r20 != 0) goto L_0x0665;
        L_0x05c8:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.info;
            r0 = r20;
            r0 = r0.participants;
            r20 = r0;
            r0 = r20;
            r0 = r0.participants;
            r21 = r0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.sortedUsers;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r22 = r0;
            r22 = r22.emptyRowChat2;
            r22 = r28 - r22;
            r22 = r22 + -1;
            r0 = r20;
            r1 = r22;
            r20 = r0.get(r1);
            r20 = (java.lang.Integer) r20;
            r20 = r20.intValue();
            r0 = r21;
            r1 = r20;
            r13 = r0.get(r1);
            r13 = (org.telegram.tgnet.TLRPC.ChatParticipant) r13;
        L_0x060e:
            if (r13 == 0) goto L_0x0009;
        L_0x0610:
            r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
            r20 = r0;
            if (r20 == 0) goto L_0x06b2;
        L_0x0616:
            r20 = r13;
            r20 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r20;
            r0 = r20;
            r8 = r0.channelParticipant;
            r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
            r20 = r0;
            if (r20 == 0) goto L_0x0691;
        L_0x0624:
            r20 = 1;
            r0 = r18;
            r1 = r20;
            r0.setIsAdmin(r1);
        L_0x062d:
            r20 = org.telegram.messenger.MessagesController.getInstance();
            r0 = r13.user_id;
            r21 = r0;
            r21 = java.lang.Integer.valueOf(r21);
            r21 = r20.getUser(r21);
            r22 = 0;
            r23 = 0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.emptyRowChat2;
            r20 = r20 + 1;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x06f1;
        L_0x0653:
            r20 = 2130837816; // 0x7f020138 float:1.7280597E38 double:1.0527737617E-314;
        L_0x0656:
            r0 = r18;
            r1 = r21;
            r2 = r22;
            r3 = r23;
            r4 = r20;
            r0.setData(r1, r2, r3, r4);
            goto L_0x0009;
        L_0x0665:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.info;
            r0 = r20;
            r0 = r0.participants;
            r20 = r0;
            r0 = r20;
            r0 = r0.participants;
            r20 = r0;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r21 = r21.emptyRowChat2;
            r21 = r28 - r21;
            r21 = r21 + -1;
            r13 = r20.get(r21);
            r13 = (org.telegram.tgnet.TLRPC.ChatParticipant) r13;
            goto L_0x060e;
        L_0x0691:
            r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
            r20 = r0;
            if (r20 != 0) goto L_0x069d;
        L_0x0697:
            r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantModerator;
            r20 = r0;
            if (r20 == 0) goto L_0x06a7;
        L_0x069d:
            r20 = 2;
            r0 = r18;
            r1 = r20;
            r0.setIsAdmin(r1);
            goto L_0x062d;
        L_0x06a7:
            r20 = 0;
            r0 = r18;
            r1 = r20;
            r0.setIsAdmin(r1);
            goto L_0x062d;
        L_0x06b2:
            r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
            r20 = r0;
            if (r20 == 0) goto L_0x06c3;
        L_0x06b8:
            r20 = 1;
            r0 = r18;
            r1 = r20;
            r0.setIsAdmin(r1);
            goto L_0x062d;
        L_0x06c3:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.currentChat;
            r0 = r20;
            r0 = r0.admins_enabled;
            r20 = r0;
            if (r20 == 0) goto L_0x06e6;
        L_0x06d5:
            r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
            r20 = r0;
            if (r20 == 0) goto L_0x06e6;
        L_0x06db:
            r20 = 2;
            r0 = r18;
            r1 = r20;
            r0.setIsAdmin(r1);
            goto L_0x062d;
        L_0x06e6:
            r20 = 0;
            r0 = r18;
            r1 = r20;
            r0.setIsAdmin(r1);
            goto L_0x062d;
        L_0x06f1:
            r20 = 0;
            goto L_0x0656;
        L_0x06f5:
            r0 = r27;
            r7 = r0.itemView;
            r7 = (org.telegram.ui.Cells.AboutLinkCell) r7;
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.userInfoRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0727;
        L_0x070b:
            r20 = org.telegram.messenger.MessagesController.getInstance();
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r21 = r0;
            r21 = r21.user_id;
            r6 = r20.getUserAbout(r21);
            r20 = 2130837560; // 0x7f020038 float:1.7280078E38 double:1.0527736353E-314;
            r0 = r20;
            r7.setTextAndIcon(r6, r0);
            goto L_0x0009;
        L_0x0727:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.channelInfoRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0009;
        L_0x0737:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.info;
            r0 = r20;
            r14 = r0.about;
        L_0x0745:
            r20 = "\n\n\n";
            r0 = r20;
            r20 = r14.contains(r0);
            if (r20 == 0) goto L_0x075c;
        L_0x074f:
            r20 = "\n\n\n";
            r21 = "\n\n";
            r0 = r20;
            r1 = r21;
            r14 = r14.replace(r0, r1);
            goto L_0x0745;
        L_0x075c:
            r20 = 2130837560; // 0x7f020038 float:1.7280078E38 double:1.0527736353E-314;
            r0 = r20;
            r7.setTextAndIcon(r14, r0);
            goto L_0x0009;
        L_0x0766:
            r10 = 0;
            goto L_0x0099;
        L_0x0769:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.chat_id;
            if (r20 == 0) goto L_0x0099;
        L_0x0775:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.convertRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x0785:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.settingsNotificationsRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x0795:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.sharedMediaRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x07a5:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.emptyRowChat2;
            r0 = r28;
            r1 = r20;
            if (r0 <= r1) goto L_0x07c5;
        L_0x07b5:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.membersEndRow;
            r0 = r28;
            r1 = r20;
            if (r0 < r1) goto L_0x0835;
        L_0x07c5:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.addMemberRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x07d5:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.channelNameRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x07e5:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.leaveChannelRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x07f5:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.membersRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x0805:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.managementRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x0815:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.blockedUsersRow;
            r0 = r28;
            r1 = r20;
            if (r0 == r1) goto L_0x0835;
        L_0x0825:
            r0 = r26;
            r0 = org.telegram.ui.ProfileActivity.this;
            r20 = r0;
            r20 = r20.channelInfoRow;
            r0 = r28;
            r1 = r20;
            if (r0 != r1) goto L_0x0838;
        L_0x0835:
            r10 = 1;
        L_0x0836:
            goto L_0x0099;
        L_0x0838:
            r10 = 0;
            goto L_0x0836;
        L_0x083a:
            r0 = r27;
            r0 = r0.itemView;
            r20 = r0;
            r20 = r20.getBackground();
            if (r20 == 0) goto L_0x00b3;
        L_0x0846:
            r0 = r27;
            r0 = r0.itemView;
            r20 = r0;
            r21 = 0;
            r20.setBackgroundDrawable(r21);
            goto L_0x00b3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(org.telegram.messenger.support.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.emptyRow || i == ProfileActivity.this.emptyRowChat || i == ProfileActivity.this.emptyRowChat2) {
                return 0;
            }
            if (i == ProfileActivity.this.sectionRow || i == ProfileActivity.this.userSectionRow) {
                return ProfileActivity.add_contact;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.channelNameRow) {
                return ProfileActivity.block_contact;
            }
            if (i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.sharedMediaRow || i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.membersRow || i == ProfileActivity.this.managementRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.convertRow || i == ProfileActivity.this.addMemberRow) {
                return ProfileActivity.share_contact;
            }
            if (i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) {
                return ProfileActivity.edit_contact;
            }
            if (i == ProfileActivity.this.membersSectionRow) {
                return ProfileActivity.delete_contact;
            }
            if (i == ProfileActivity.this.convertHelpRow) {
                return 6;
            }
            if (i == ProfileActivity.this.loadMoreMembersRow) {
                return ProfileActivity.leave_group;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return ProfileActivity.edit_name;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.7 */
    class C20177 extends LinearLayoutManager {
        C20177(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity.6 */
    class C20296 extends RecyclerListView {
        C20296(Context x0) {
            super(x0);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    public ProfileActivity(Bundle args) {
        super(args);
        this.nameTextView = new SimpleTextView[block_contact];
        this.onlineTextView = new SimpleTextView[block_contact];
        this.participantsMap = new HashMap();
        this.allowProfileAnimation = true;
        this.onlineCount = -1;
        this.totalMediaCount = -1;
        this.totalMediaCountMerge = -1;
        this.rowCount = 0;
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = getArguments().getInt("chat_id", 0);
        if (this.user_id != 0) {
            this.dialog_id = this.arguments.getLong("dialog_id", 0);
            if (this.dialog_id != 0) {
                this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user == null) {
                return false;
            }
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.userInfoDidLoaded);
            if (this.currentEncryptedChat != null) {
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
            }
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
            if (user.bot) {
                BotQuery.loadBotInfo(user.id, true, this.classGuid);
            }
            MessagesController.getInstance().loadFullUser(MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
            if (this.currentChat == null) {
                Semaphore semaphore = new Semaphore(0);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C13951(semaphore));
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
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.sortedUsers = new ArrayList();
            updateOnlineCount();
            this.avatarUpdater = new AvatarUpdater();
            this.avatarUpdater.delegate = new C19272();
            this.avatarUpdater.parentFragment = this;
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
            }
        }
        if (this.dialog_id != 0) {
            SharedMediaQuery.getMediaCount(this.dialog_id, 0, this.classGuid, true);
        } else if (this.user_id != 0) {
            SharedMediaQuery.getMediaCount((long) this.user_id, 0, this.classGuid, true);
        } else if (this.chat_id > 0) {
            SharedMediaQuery.getMediaCount((long) (-this.chat_id), 0, this.classGuid, true);
            if (this.mergeDialogId != 0) {
                SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
            }
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        updateRowsIds();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        if (this.user_id != 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userInfoDidLoaded);
            MessagesController.getInstance().cancelLoadFullUser(this.user_id);
            if (this.currentEncryptedChat != null) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
            }
        } else if (this.chat_id != 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.avatarUpdater.clear();
        }
    }

    protected ActionBar createActionBar(Context context) {
        boolean z;
        ActionBar actionBar = new C19283(context);
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i));
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        if (VERSION.SDK_INT < 21 || AndroidUtilities.isTablet()) {
            z = false;
        } else {
            z = true;
        }
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new C19304());
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setProfile(true);
        this.fragmentView = new C13985(context);
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new C20296(context);
        this.listView.setTag(Integer.valueOf(6));
        this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        this.listView.setBackgroundColor(-1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setClipToPadding(false);
        this.layoutManager = new C20177(context);
        this.layoutManager.setOrientation(add_contact);
        this.listView.setLayoutManager(this.layoutManager);
        RecyclerListView recyclerListView = this.listView;
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C19318());
        this.listView.setOnItemLongClickListener(new C19339());
        this.topView = new TopView(context);
        TopView topView = this.topView;
        i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ProfileActivity.this.user_id != 0) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user.photo != null && user.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(user.photo.photo_big, ProfileActivity.this);
                    }
                } else if (ProfileActivity.this.chat_id != 0) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                    if (chat.photo != null && chat.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, ProfileActivity.this);
                    }
                }
            }
        });
        int a = 0;
        while (a < block_contact) {
            if (this.playProfileAnimation || a != 0) {
                float f;
                this.nameTextView[a] = new SimpleTextView(context);
                this.nameTextView[a].setTextColor(-1);
                this.nameTextView[a].setTextSize(18);
                this.nameTextView[a].setGravity(share_contact);
                this.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[a].setRightDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[a].setPivotX(0.0f);
                this.nameTextView[a].setPivotY(0.0f);
                frameLayout.addView(this.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[a] = new SimpleTextView(context);
                SimpleTextView simpleTextView = this.onlineTextView[a];
                i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i));
                this.onlineTextView[a].setTextSize(add_shortcut);
                this.onlineTextView[a].setGravity(share_contact);
                View view = this.onlineTextView[a];
                if (a == 0) {
                    f = 48.0f;
                } else {
                    f = 8.0f;
                }
                frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, f, 0.0f));
            }
            a += add_contact;
        }
        if (this.user_id != 0 || (this.chat_id >= 0 && !ChatObject.isLeftFromChat(this.currentChat))) {
            this.writeButton = new ImageView(context);
            try {
                this.writeButton.setBackgroundResource(C0691R.drawable.floating_user_states);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            this.writeButton.setScaleType(ScaleType.CENTER);
            if (this.user_id != 0) {
                this.writeButton.setImageResource(C0691R.drawable.floating_message);
                this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
            } else if (this.chat_id != 0) {
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                if ((!isChannel || this.currentChat.creator || (this.currentChat.megagroup && this.currentChat.editor)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                    this.writeButton.setImageResource(C0691R.drawable.floating_camera);
                } else {
                    this.writeButton.setImageResource(C0691R.drawable.floating_message);
                    this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                }
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                int[] iArr = new int[add_contact];
                iArr[0] = 16842919;
                float[] fArr = new float[block_contact];
                fArr[0] = (float) AndroidUtilities.dp(2.0f);
                fArr[add_contact] = (float) AndroidUtilities.dp(4.0f);
                animator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
                iArr = new int[0];
                fArr = new float[block_contact];
                fArr[0] = (float) AndroidUtilities.dp(4.0f);
                fArr[add_contact] = (float) AndroidUtilities.dp(2.0f);
                animator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
                this.writeButton.setStateListAnimator(animator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButton.setOnClickListener(new View.OnClickListener() {

                /* renamed from: org.telegram.ui.ProfileActivity.12.1 */
                class C13931 implements OnClickListener {
                    C13931() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            ProfileActivity.this.avatarUpdater.openCamera();
                        } else if (i == ProfileActivity.add_contact) {
                            ProfileActivity.this.avatarUpdater.openGallery();
                        } else if (i == ProfileActivity.block_contact) {
                            MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, null);
                        }
                    }
                }

                public void onClick(View v) {
                    if (ProfileActivity.this.getParentActivity() != null) {
                        Bundle args;
                        if (ProfileActivity.this.user_id != 0) {
                            if (ProfileActivity.this.playProfileAnimation && (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                                ProfileActivity.this.finishFragment();
                                return;
                            }
                            User user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            if (user != null && !(user instanceof TL_userEmpty)) {
                                args = new Bundle();
                                args.putInt("user_id", ProfileActivity.this.user_id);
                                if (MessagesController.checkCanOpenChat(args, ProfileActivity.this)) {
                                    NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                }
                            }
                        } else if (ProfileActivity.this.chat_id != 0) {
                            boolean isChannel = ChatObject.isChannel(ProfileActivity.this.currentChat);
                            if ((!isChannel || ProfileActivity.this.currentChat.creator || (ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.currentChat.editor)) && (isChannel || ProfileActivity.this.currentChat.admin || ProfileActivity.this.currentChat.creator || !ProfileActivity.this.currentChat.admins_enabled)) {
                                CharSequence[] items;
                                Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                if (chat.photo == null || chat.photo.photo_big == null || (chat.photo instanceof TL_chatPhotoEmpty)) {
                                    items = new CharSequence[ProfileActivity.block_contact];
                                    items[0] = LocaleController.getString("FromCamera", C0691R.string.FromCamera);
                                    items[ProfileActivity.add_contact] = LocaleController.getString("FromGalley", C0691R.string.FromGalley);
                                } else {
                                    items = new CharSequence[ProfileActivity.share_contact];
                                    items[0] = LocaleController.getString("FromCamera", C0691R.string.FromCamera);
                                    items[ProfileActivity.add_contact] = LocaleController.getString("FromGalley", C0691R.string.FromGalley);
                                    items[ProfileActivity.block_contact] = LocaleController.getString("DeletePhoto", C0691R.string.DeletePhoto);
                                }
                                builder.setItems(items, new C13931());
                                ProfileActivity.this.showDialog(builder.create());
                            } else if (ProfileActivity.this.playProfileAnimation && (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                                ProfileActivity.this.finishFragment();
                            } else {
                                args = new Bundle();
                                args.putInt("chat_id", ProfileActivity.this.currentChat.id);
                                if (MessagesController.checkCanOpenChat(args, ProfileActivity.this)) {
                                    NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                }
                            }
                        }
                    }
                }
            });
        }
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ProfileActivity.this.checkListViewScroll();
                if (ProfileActivity.this.participantsMap != null && ProfileActivity.this.loadMoreMembersRow != -1 && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
            }
        });
        return this.fragmentView;
    }

    private boolean processOnClickOrPress(int position) {
        User user;
        Builder builder;
        CharSequence[] charSequenceArr;
        if (position == this.usernameRow) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user == null || user.username == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            charSequenceArr = new CharSequence[add_contact];
            charSequenceArr[0] = LocaleController.getString("Copy", C0691R.string.Copy);
            builder.setItems(charSequenceArr, new AnonymousClass14(user));
            showDialog(builder.create());
            return true;
        } else if (position == this.phoneRow) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user == null || user.phone == null || user.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            charSequenceArr = new CharSequence[block_contact];
            charSequenceArr[0] = LocaleController.getString("Call", C0691R.string.Call);
            charSequenceArr[add_contact] = LocaleController.getString("Copy", C0691R.string.Copy);
            builder.setItems(charSequenceArr, new AnonymousClass15(user));
            showDialog(builder.create());
            return true;
        } else if (position != this.channelInfoRow && position != this.userInfoRow) {
            return false;
        } else {
            builder = new Builder(getParentActivity());
            charSequenceArr = new CharSequence[add_contact];
            charSequenceArr[0] = LocaleController.getString("Copy", C0691R.string.Copy);
            builder.setItems(charSequenceArr, new AnonymousClass16(position));
            showDialog(builder.create());
            return true;
        }
    }

    private void leaveChatPressed() {
        Builder builder = new Builder(getParentActivity());
        if (!ChatObject.isChannel(this.chat_id) || this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0691R.string.AreYouSureDeleteAndExit));
        } else {
            builder.setMessage(ChatObject.isChannel(this.chat_id) ? LocaleController.getString("ChannelLeaveAlert", C0691R.string.ChannelLeaveAlert) : LocaleController.getString("AreYouSureDeleteAndExit", C0691R.string.AreYouSureDeleteAndExit));
        }
        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.kickUser(0);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void saveSelfArgs(Bundle args) {
        if (this.chat_id != 0 && this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.chat_id != 0) {
            MessagesController.getInstance().loadChatInfo(this.chat_id, null, false);
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = args.getString("path");
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (this.chat_id != 0) {
            this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getChannelParticipants(boolean reload) {
        int i = 0;
        if (!this.loadingUsers && this.participantsMap != null && this.info != null) {
            int delay;
            this.loadingUsers = true;
            if (this.participantsMap.isEmpty() || !reload) {
                delay = 0;
            } else {
                delay = 300;
            }
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInputChannel(this.chat_id);
            req.filter = new TL_channelParticipantsRecent();
            if (!reload) {
                i = this.participantsMap.size();
            }
            req.offset = i;
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass18(req, delay)), this.classGuid);
        }
    }

    private void openAddMember() {
        boolean z = true;
        Bundle args = new Bundle();
        args.putBoolean("onlyUsers", true);
        args.putBoolean("destroyAfterSelect", true);
        args.putBoolean("returnAsResult", true);
        String str = "needForwardCount";
        if (ChatObject.isChannel(this.currentChat)) {
            z = false;
        }
        args.putBoolean(str, z);
        if (this.chat_id > 0) {
            if (this.currentChat.creator) {
                args.putInt("chat_id", this.currentChat.id);
            }
            args.putString("selectAlertString", LocaleController.getString("AddToTheGroup", C0691R.string.AddToTheGroup));
        }
        ContactsActivity fragment = new ContactsActivity(args);
        fragment.setDelegate(new ContactsActivityDelegate() {
            public void didSelectContact(User user, String param) {
                MessagesController.getInstance().addUserToChat(ProfileActivity.this.chat_id, user, ProfileActivity.this.info, param != null ? Utilities.parseInt(param).intValue() : 0, null, ProfileActivity.this);
            }
        });
        if (!(this.info == null || this.info.participants == null)) {
            HashMap<Integer, User> users = new HashMap();
            for (int a = 0; a < this.info.participants.participants.size(); a += add_contact) {
                users.put(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id), null);
            }
            fragment.setIgnoreUsers(users);
        }
        presentFragment(fragment);
    }

    private void checkListViewScroll() {
        boolean z = false;
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
            View child = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop();
            int newOffset = 0;
            if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
            }
            if (this.extraHeight != newOffset) {
                this.extraHeight = newOffset;
                this.topView.invalidate();
                if (this.playProfileAnimation) {
                    if (this.extraHeight != 0) {
                        z = true;
                    }
                    this.allowProfileAnimation = z;
                }
                needLayout();
            }
        }
    }

    private void needLayout() {
        FrameLayout.LayoutParams layoutParams;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (!(this.listView == null || this.openAnimationInProgress)) {
            layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            if (this.writeButton != null) {
                this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    boolean setVisible = diff > DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                    if (setVisible != (this.writeButton.getTag() == null)) {
                        if (setVisible) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet old = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            old.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        AnimatorSet animatorSet;
                        Animator[] animatorArr;
                        float[] fArr;
                        if (setVisible) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            animatorSet = this.writeButtonAnimation;
                            animatorArr = new Animator[share_contact];
                            fArr = new float[add_contact];
                            fArr[0] = TouchHelperCallback.ALPHA_FULL;
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                            fArr = new float[add_contact];
                            fArr[0] = TouchHelperCallback.ALPHA_FULL;
                            animatorArr[add_contact] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                            fArr = new float[add_contact];
                            fArr[0] = TouchHelperCallback.ALPHA_FULL;
                            animatorArr[block_contact] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr);
                            animatorSet.playTogether(animatorArr);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            animatorSet = this.writeButtonAnimation;
                            animatorArr = new Animator[share_contact];
                            fArr = new float[add_contact];
                            fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                            fArr = new float[add_contact];
                            fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                            animatorArr[add_contact] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                            fArr = new float[add_contact];
                            fArr[0] = 0.0f;
                            animatorArr[block_contact] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr);
                            animatorSet.playTogether(animatorArr);
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationEnd(Animator animation) {
                                if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animation)) {
                                    ProfileActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (TouchHelperCallback.ALPHA_FULL + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setScaleX((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setScaleY((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            for (int a = 0; a < block_contact; a += add_contact) {
                if (this.nameTextView[a] != null) {
                    this.nameTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.nameTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * diff));
                    this.onlineTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.onlineTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
                    this.nameTextView[a].setScaleX(TouchHelperCallback.ALPHA_FULL + (0.12f * diff));
                    this.nameTextView[a].setScaleY(TouchHelperCallback.ALPHA_FULL + (0.12f * diff));
                    if (a == add_contact && !this.openAnimationInProgress) {
                        int width;
                        if (AndroidUtilities.isTablet()) {
                            width = AndroidUtilities.dp(490.0f);
                        } else {
                            width = AndroidUtilities.displaySize.x;
                        }
                        width = (int) (((float) (width - AndroidUtilities.dp(126.0f + (40.0f * (TouchHelperCallback.ALPHA_FULL - diff))))) - this.nameTextView[a].getTranslationX());
                        layoutParams = (FrameLayout.LayoutParams) this.nameTextView[a].getLayoutParams();
                        if (((float) width) < (this.nameTextView[a].getPaint().measureText(this.nameTextView[a].getText().toString()) * this.nameTextView[a].getScaleX()) + ((float) this.nameTextView[a].getSideDrawablesSize())) {
                            layoutParams.width = (int) Math.ceil((double) (((float) width) / this.nameTextView[a].getScaleX()));
                        } else {
                            layoutParams.width = -2;
                        }
                        this.nameTextView[a].setLayoutParams(layoutParams);
                        layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[a].getLayoutParams();
                        layoutParams.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[a].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (TouchHelperCallback.ALPHA_FULL - diff))));
                        this.onlineTextView[a].setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ProfileActivity.this.fragmentView != null) {
                        ProfileActivity.this.checkListViewScroll();
                        ProfileActivity.this.needLayout();
                        ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, Object... args) {
        ViewHolder holder;
        Chat newChat;
        int count;
        int a;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if (this.user_id != 0) {
                if (!((mask & block_contact) == 0 && (mask & add_contact) == 0 && (mask & edit_contact) == 0)) {
                    updateProfileData();
                }
                if ((mask & MessagesController.UPDATE_MASK_PHONE) != 0 && this.listView != null) {
                    holder = (Holder) this.listView.findViewHolderForPosition(this.phoneRow);
                    if (holder != null) {
                        this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                    }
                }
            } else if (this.chat_id != 0) {
                if ((mask & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                    newChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                    if (newChat != null) {
                        this.currentChat = newChat;
                        createActionBarMenu();
                        updateRowsIds();
                        if (this.listAdapter != null) {
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (!((mask & MessagesController.UPDATE_MASK_CHANNEL) == 0 && (mask & edit_name) == 0 && (mask & 16) == 0 && (mask & 32) == 0 && (mask & edit_contact) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if ((mask & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                    updateRowsIds();
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                if (((mask & block_contact) != 0 || (mask & add_contact) != 0 || (mask & edit_contact) != 0) && this.listView != null) {
                    count = this.listView.getChildCount();
                    for (a = 0; a < count; a += add_contact) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoaded) {
            createActionBarMenu();
        } else if (id == NotificationCenter.mediaCountDidLoaded) {
            long uid = ((Long) args[0]).longValue();
            long did = this.dialog_id;
            if (did == 0) {
                if (this.user_id != 0) {
                    did = (long) this.user_id;
                } else if (this.chat_id != 0) {
                    did = (long) (-this.chat_id);
                }
            }
            if (uid != did) {
                if (uid != this.mergeDialogId) {
                    return;
                }
            }
            if (uid == did) {
                this.totalMediaCount = ((Integer) args[add_contact]).intValue();
            } else {
                this.totalMediaCountMerge = ((Integer) args[add_contact]).intValue();
            }
            if (this.listView != null) {
                count = this.listView.getChildCount();
                for (a = 0; a < count; a += add_contact) {
                    holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
                    if (holder.getAdapterPosition() == this.sharedMediaRow) {
                        this.listAdapter.onBindViewHolder(holder, this.sharedMediaRow);
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new AnonymousClass22(args));
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat = args[0];
            if (this.currentEncryptedChat != null && chat.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.blockedUsersDidLoaded) {
            boolean oldValue = this.userBlocked;
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
            if (oldValue != this.userBlocked) {
                createActionBarMenu();
            }
        } else if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chat_id) {
                boolean byChannelUsers = ((Boolean) args[block_contact]).booleanValue();
                if ((this.info instanceof TL_channelFull) && chatFull.participants == null && this.info != null) {
                    chatFull.participants = this.info.participants;
                }
                boolean loadChannelParticipants = this.info == null && (chatFull instanceof TL_channelFull);
                this.info = chatFull;
                if (this.mergeDialogId == 0) {
                    if (this.info.migrated_from_chat_id != 0) {
                        this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
                        SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                    }
                }
                fetchUsersFromChannelInfo();
                updateOnlineCount();
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
                newChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (newChat != null) {
                    this.currentChat = newChat;
                    createActionBarMenu();
                }
                if (!this.currentChat.megagroup) {
                    return;
                }
                if (loadChannelParticipants || !byChannelUsers) {
                    getChannelParticipants(true);
                }
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.botInfoDidLoaded) {
            BotInfo info = args[0];
            if (info.user_id == this.user_id) {
                this.botInfo = info;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.userInfoDidLoaded) {
            if (((Integer) args[0]).intValue() == this.user_id) {
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                ArrayList<MessageObject> arr = args[add_contact];
                for (a = 0; a < arr.size(); a += add_contact) {
                    MessageObject obj = (MessageObject) arr.get(a);
                    if (this.currentEncryptedChat != null) {
                        if (obj.messageOwner.action != null) {
                            if (obj.messageOwner.action instanceof TL_messageEncryptedAction) {
                                if (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                                    TL_decryptedMessageActionSetMessageTTL action = obj.messageOwner.action.encryptedAction;
                                    if (this.listAdapter != null) {
                                        this.listAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateProfileData();
        fixLayout();
    }

    public void setPlayProfileAnimation(boolean value) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (!AndroidUtilities.isTablet() && preferences.getBoolean("view_animations", true)) {
            this.playProfileAnimation = value;
        }
    }

    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (!backward && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        NotificationCenter instance = NotificationCenter.getInstance();
        int[] iArr = new int[share_contact];
        iArr[0] = NotificationCenter.dialogsNeedReload;
        iArr[add_contact] = NotificationCenter.closeChats;
        iArr[block_contact] = NotificationCenter.mediaCountDidLoaded;
        instance.setAllowedNotificationsDutingAnimation(iArr);
        NotificationCenter.getInstance().setAnimationInProgress(true);
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (!backward && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = false;
        }
        NotificationCenter.getInstance().setAnimationInProgress(false);
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public void setAnimationProgress(float progress) {
        int i = delete_contact;
        this.animationProgress = progress;
        this.listView.setAlpha(progress);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * progress));
        int i2 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        int color = AvatarDrawable.getProfileBackColorForId(i2);
        int r = Color.red(Theme.ACTION_BAR_COLOR);
        int g = Color.green(Theme.ACTION_BAR_COLOR);
        int b = Color.blue(Theme.ACTION_BAR_COLOR);
        this.topView.setBackgroundColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * progress)), g + ((int) (((float) (Color.green(color) - g)) * progress)), b + ((int) (((float) (Color.blue(color) - b)) * progress))));
        if (this.user_id == 0 && (!ChatObject.isChannel(this.chat_id) || this.currentChat.megagroup)) {
            i = this.chat_id;
        }
        color = AvatarDrawable.getProfileTextColorForId(i);
        r = Color.red(Theme.ACTION_BAR_SUBTITLE_COLOR);
        g = Color.green(Theme.ACTION_BAR_SUBTITLE_COLOR);
        b = Color.blue(Theme.ACTION_BAR_SUBTITLE_COLOR);
        int rD = (int) (((float) (Color.red(color) - r)) * progress);
        int gD = (int) (((float) (Color.green(color) - g)) * progress);
        int bD = (int) (((float) (Color.blue(color) - b)) * progress);
        for (int a = 0; a < block_contact; a += add_contact) {
            if (this.onlineTextView[a] != null) {
                this.onlineTextView[a].setTextColor(Color.rgb(r + rD, g + gD, b + bD));
            }
        }
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * progress);
        color = AvatarDrawable.getProfileColorForId(this.user_id != 0 ? this.user_id : this.chat_id);
        int color2 = AvatarDrawable.getColorForId(this.user_id != 0 ? this.user_id : this.chat_id);
        if (color != color2) {
            this.avatarDrawable.setColor(Color.rgb(Color.red(color2) + ((int) (((float) (Color.red(color) - Color.red(color2))) * progress)), Color.green(color2) + ((int) (((float) (Color.green(color) - Color.green(color2))) * progress)), Color.blue(color2) + ((int) (((float) (Color.blue(color) - Color.blue(color2))) * progress))));
            this.avatarImage.invalidate();
        }
        needLayout();
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        if (VERSION.SDK_INT > 15) {
            this.listView.setLayerType(block_contact, null);
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (menu.getItem(share) == null && this.animatingItem == null) {
            this.animatingItem = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
        }
        ArrayList<Animator> animators;
        float[] fArr;
        int a;
        Object obj;
        String str;
        float[] fArr2;
        if (isOpen) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[add_contact].getLayoutParams();
            layoutParams.rightMargin = (int) ((-21.0f * AndroidUtilities.density) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[add_contact].setLayoutParams(layoutParams);
            int width = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (21.0f * AndroidUtilities.density)));
            layoutParams = (FrameLayout.LayoutParams) this.nameTextView[add_contact].getLayoutParams();
            if (((float) width) < (this.nameTextView[add_contact].getPaint().measureText(this.nameTextView[add_contact].getText().toString()) * 1.12f) + ((float) this.nameTextView[add_contact].getSideDrawablesSize())) {
                layoutParams.width = (int) Math.ceil((double) (((float) width) / 1.12f));
            } else {
                layoutParams.width = -2;
            }
            this.nameTextView[add_contact].setLayoutParams(layoutParams);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
            this.fragmentView.setBackgroundColor(0);
            setAnimationProgress(0.0f);
            animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL}));
            if (this.writeButton != null) {
                this.writeButton.setScaleX(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD);
                this.writeButton.setScaleY(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD);
                this.writeButton.setAlpha(0.0f);
                fArr = new float[add_contact];
                fArr[0] = TouchHelperCallback.ALPHA_FULL;
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr));
                fArr = new float[add_contact];
                fArr[0] = TouchHelperCallback.ALPHA_FULL;
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr));
                fArr = new float[add_contact];
                fArr[0] = TouchHelperCallback.ALPHA_FULL;
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr));
            }
            a = 0;
            while (a < block_contact) {
                this.onlineTextView[a].setAlpha(a == 0 ? TouchHelperCallback.ALPHA_FULL : 0.0f);
                this.nameTextView[a].setAlpha(a == 0 ? TouchHelperCallback.ALPHA_FULL : 0.0f);
                obj = this.onlineTextView[a];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = a == 0 ? 0.0f : TouchHelperCallback.ALPHA_FULL;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                obj = this.nameTextView[a];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = a == 0 ? 0.0f : TouchHelperCallback.ALPHA_FULL;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                a += add_contact;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(TouchHelperCallback.ALPHA_FULL);
                fArr = new float[add_contact];
                fArr[0] = 0.0f;
                animators.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", fArr));
            }
            animatorSet.playTogether(animators);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{TouchHelperCallback.ALPHA_FULL, 0.0f}));
            if (this.writeButton != null) {
                fArr = new float[add_contact];
                fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr));
                fArr = new float[add_contact];
                fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr));
                fArr = new float[add_contact];
                fArr[0] = 0.0f;
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr));
            }
            a = 0;
            while (a < block_contact) {
                obj = this.onlineTextView[a];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = a == 0 ? TouchHelperCallback.ALPHA_FULL : 0.0f;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                obj = this.nameTextView[a];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = a == 0 ? TouchHelperCallback.ALPHA_FULL : 0.0f;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                a += add_contact;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(0.0f);
                fArr = new float[add_contact];
                fArr[0] = TouchHelperCallback.ALPHA_FULL;
                animators.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", fArr));
            }
            animatorSet.playTogether(animators);
        }
        animatorSet.addListener(new AnonymousClass23(callback));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        AndroidUtilities.runOnUIThread(new AnonymousClass24(animatorSet), 50);
        return animatorSet;
    }

    public void updatePhotoAtIndex(int index) {
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        PlaceProviderObject placeProviderObject = null;
        if (fileLocation != null) {
            FileLocation photoBig = null;
            if (this.user_id != 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                    photoBig = user.photo.photo_big;
                }
            } else if (this.chat_id != 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (!(chat == null || chat.photo == null || chat.photo.photo_big == null)) {
                    photoBig = chat.photo.photo_big;
                }
            }
            if (photoBig != null && photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                int[] coords = new int[block_contact];
                this.avatarImage.getLocationInWindow(coords);
                placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = coords[0];
                placeProviderObject.viewY = coords[add_contact] - AndroidUtilities.statusBarHeight;
                placeProviderObject.parentView = this.avatarImage;
                placeProviderObject.imageReceiver = this.avatarImage.getImageReceiver();
                if (this.user_id != 0) {
                    placeProviderObject.dialogId = this.user_id;
                } else if (this.chat_id != 0) {
                    placeProviderObject.dialogId = -this.chat_id;
                }
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                placeProviderObject.size = -1;
                placeProviderObject.radius = this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = this.avatarImage.getScaleX();
            }
        }
        return placeProviderObject;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
    }

    public void willHidePhotoViewer() {
        this.avatarImage.getImageReceiver().setVisible(true, true);
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
        return 0;
    }

    private void updateOnlineCount() {
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance().getCurrentTime();
        this.sortedUsers.clear();
        if ((this.info instanceof TL_chatFull) || ((this.info instanceof TL_channelFull) && this.info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.info.participants != null)) {
            for (int a = 0; a < this.info.participants.participants.size(); a += add_contact) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getClientUserId()) || user.status.expires <= AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS))) {
                    this.onlineCount += add_contact;
                }
                this.sortedUsers.add(Integer.valueOf(a));
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator<Integer>() {
                    public int compare(Integer lhs, Integer rhs) {
                        User user1 = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(rhs.intValue())).user_id));
                        User user2 = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(lhs.intValue())).user_id));
                        int status1 = 0;
                        int status2 = 0;
                        if (!(user1 == null || user1.status == null)) {
                            status1 = user1.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user1.status.expires;
                        }
                        if (!(user2 == null || user2.status == null)) {
                            status2 = user2.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user2.status.expires;
                        }
                        if (status1 <= 0 || status2 <= 0) {
                            if (status1 >= 0 || status2 >= 0) {
                                if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
                                    return -1;
                                }
                                if ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) {
                                    return 0;
                                }
                                return ProfileActivity.add_contact;
                            } else if (status1 > status2) {
                                return ProfileActivity.add_contact;
                            } else {
                                if (status1 < status2) {
                                    return -1;
                                }
                                return 0;
                            }
                        } else if (status1 > status2) {
                            return ProfileActivity.add_contact;
                        } else {
                            if (status1 < status2) {
                                return -1;
                            }
                            return 0;
                        }
                    }
                });
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyItemRangeChanged(this.emptyRowChat2 + add_contact, this.sortedUsers.size());
            }
        }
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        if (!(this.info == null || this.info.migrated_from_chat_id == 0)) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
        fetchUsersFromChannelInfo();
    }

    private void fetchUsersFromChannelInfo() {
        if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
            for (int a = 0; a < this.info.participants.participants.size(); a += add_contact) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(a);
                this.participantsMap.put(Integer.valueOf(chatParticipant.user_id), chatParticipant);
            }
        }
    }

    private void kickUser(int uid) {
        if (uid != 0) {
            MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(uid)), this.info);
            if (this.currentChat.megagroup && this.info != null && this.info.participants != null) {
                int a;
                boolean changed = false;
                for (a = 0; a < this.info.participants.participants.size(); a += add_contact) {
                    if (((TL_chatChannelParticipant) this.info.participants.participants.get(a)).channelParticipant.user_id == uid) {
                        if (this.info != null) {
                            ChatFull chatFull = this.info;
                            chatFull.participants_count--;
                        }
                        this.info.participants.participants.remove(a);
                        changed = true;
                        if (!(this.info == null || this.info.participants == null)) {
                            for (a = 0; a < this.info.participants.participants.size(); a += add_contact) {
                                if (((ChatParticipant) this.info.participants.participants.get(a)).user_id != uid) {
                                    this.info.participants.participants.remove(a);
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (!changed) {
                            updateOnlineCount();
                            updateRowsIds();
                            this.listAdapter.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                }
                while (a < this.info.participants.participants.size()) {
                    if (((ChatParticipant) this.info.participants.participants.get(a)).user_id != uid) {
                    } else {
                        this.info.participants.participants.remove(a);
                        changed = true;
                        break;
                        if (!changed) {
                            updateOnlineCount();
                            updateRowsIds();
                            this.listAdapter.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                }
                if (!changed) {
                    updateOnlineCount();
                    updateRowsIds();
                    this.listAdapter.notifyDataSetChanged();
                    return;
                }
                return;
            }
            return;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.closeChats;
            Object[] objArr = new Object[add_contact];
            objArr[0] = Long.valueOf(-((long) this.chat_id));
            instance.postNotificationName(i, objArr);
        } else {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), this.info);
        this.playProfileAnimation = false;
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    private void updateRowsIds() {
        this.emptyRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.userSectionRow = -1;
        this.sectionRow = -1;
        this.sharedMediaRow = -1;
        this.settingsNotificationsRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.startSecretChatRow = -1;
        this.membersEndRow = -1;
        this.emptyRowChat2 = -1;
        this.addMemberRow = -1;
        this.channelInfoRow = -1;
        this.channelNameRow = -1;
        this.convertRow = -1;
        this.convertHelpRow = -1;
        this.emptyRowChat = -1;
        this.membersSectionRow = -1;
        this.membersRow = -1;
        this.managementRow = -1;
        this.leaveChannelRow = -1;
        this.loadMoreMembersRow = -1;
        this.blockedUsersRow = -1;
        this.rowCount = 0;
        int i;
        if (this.user_id != 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            i = this.rowCount;
            this.rowCount = i + add_contact;
            this.emptyRow = i;
            if (user == null || !user.bot) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.phoneRow = i;
            }
            if (!(user == null || user.username == null || user.username.length() <= 0)) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.usernameRow = i;
            }
            if (MessagesController.getInstance().getUserAbout(user.id) != null) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.userSectionRow = i;
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.userInfoRow = i;
            } else {
                this.userSectionRow = -1;
                this.userInfoRow = -1;
            }
            i = this.rowCount;
            this.rowCount = i + add_contact;
            this.sectionRow = i;
            i = this.rowCount;
            this.rowCount = i + add_contact;
            this.settingsNotificationsRow = i;
            i = this.rowCount;
            this.rowCount = i + add_contact;
            this.sharedMediaRow = i;
            if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.settingsTimerRow = i;
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.settingsKeyRow = i;
            }
            if (user != null && !user.bot && this.currentEncryptedChat == null && user.id != UserConfig.getClientUserId()) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.startSecretChatRow = i;
            }
        } else if (this.chat_id == 0) {
        } else {
            if (this.chat_id > 0) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.emptyRow = i;
                if (ChatObject.isChannel(this.currentChat) && (!(this.info == null || this.info.about == null || this.info.about.length() <= 0) || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
                    if (!(this.info == null || this.info.about == null || this.info.about.length() <= 0)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.channelInfoRow = i;
                    }
                    if (this.currentChat.username != null && this.currentChat.username.length() > 0) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.channelNameRow = i;
                    }
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.sectionRow = i;
                }
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.settingsNotificationsRow = i;
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.sharedMediaRow = i;
                if (ChatObject.isChannel(this.currentChat)) {
                    if (!(this.currentChat.megagroup || this.info == null || (!this.currentChat.creator && !this.info.can_view_participants))) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.membersRow = i;
                    }
                    if (!(ChatObject.isNotInChat(this.currentChat) || this.currentChat.megagroup || (!this.currentChat.creator && !this.currentChat.editor && !this.currentChat.moderator))) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.managementRow = i;
                    }
                    if (!ChatObject.isNotInChat(this.currentChat) && this.currentChat.megagroup && (this.currentChat.editor || this.currentChat.creator)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.blockedUsersRow = i;
                    }
                    if (!(this.currentChat.creator || this.currentChat.left || this.currentChat.kicked || this.currentChat.megagroup)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.leaveChannelRow = i;
                    }
                    if (this.currentChat.megagroup && ((this.currentChat.editor || this.currentChat.creator || this.currentChat.democracy) && (this.info == null || this.info.participants_count < MessagesController.getInstance().maxMegagroupCount))) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.addMemberRow = i;
                    }
                    if (this.info != null && this.info.participants != null && !this.info.participants.participants.isEmpty()) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.emptyRowChat = i;
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.membersSectionRow = i;
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.emptyRowChat2 = i;
                        this.rowCount += this.info.participants.participants.size();
                        this.membersEndRow = this.rowCount;
                        if (!this.usersEndReached) {
                            i = this.rowCount;
                            this.rowCount = i + add_contact;
                            this.loadMoreMembersRow = i;
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (this.info != null) {
                    if (!(this.info.participants instanceof TL_chatParticipantsForbidden) && this.info.participants.participants.size() < MessagesController.getInstance().maxGroupCount && (this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.addMemberRow = i;
                    }
                    if (this.currentChat.creator && this.info.participants.participants.size() >= MessagesController.getInstance().minGroupConvertSize) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.convertRow = i;
                    }
                }
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.emptyRowChat = i;
                if (this.convertRow != -1) {
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.convertHelpRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.membersSectionRow = i;
                }
                if (this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.emptyRowChat2 = i;
                    this.rowCount += this.info.participants.participants.size();
                    this.membersEndRow = this.rowCount;
                }
            } else if (!ChatObject.isChannel(this.currentChat) && this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.addMemberRow = i;
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.emptyRowChat2 = i;
                this.rowCount += this.info.participants.participants.size();
                this.membersEndRow = this.rowCount;
            }
        }
    }

    private void updateProfileData() {
        if (this.avatarImage != null && this.nameTextView != null) {
            TLObject photo;
            FileLocation photoBig;
            String newString;
            int a;
            if (this.user_id != 0) {
                String newString2;
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                photo = null;
                photoBig = null;
                if (user.photo != null) {
                    photo = user.photo.photo_small;
                    photoBig = user.photo.photo_big;
                }
                this.avatarDrawable.setInfo(user);
                this.avatarImage.setImage(photo, "50_50", this.avatarDrawable);
                newString = UserObject.getUserName(user);
                if (user.id == 333000 || user.id == 777000) {
                    newString2 = LocaleController.getString("ServiceNotifications", C0691R.string.ServiceNotifications);
                } else if (user.bot) {
                    newString2 = LocaleController.getString("Bot", C0691R.string.Bot);
                } else {
                    newString2 = LocaleController.formatUserStatus(user);
                }
                for (a = 0; a < block_contact; a += add_contact) {
                    if (this.nameTextView[a] != null) {
                        int leftIcon;
                        int rightIcon;
                        if (!(a != 0 || user.phone == null || user.phone.length() == 0 || user.id / 1000 == 777 || user.id / 1000 == 333)) {
                            if (ContactsController.getInstance().contactsDict.get(user.id) == null && !(ContactsController.getInstance().contactsDict.size() == 0 && ContactsController.getInstance().isLoadingContacts())) {
                                String phoneString = PhoneFormat.getInstance().format("+" + user.phone);
                                if (!this.nameTextView[a].getText().equals(phoneString)) {
                                    this.nameTextView[a].setText(phoneString);
                                }
                                if (!this.onlineTextView[a].getText().equals(newString2)) {
                                    this.onlineTextView[a].setText(newString2);
                                }
                                leftIcon = this.currentEncryptedChat == null ? C0691R.drawable.ic_lock_header : 0;
                                rightIcon = 0;
                                if (a == 0) {
                                    rightIcon = MessagesController.getInstance().isDialogMuted(this.dialog_id == 0 ? this.dialog_id : (long) this.user_id) ? C0691R.drawable.mute_fixed : 0;
                                } else if (user.verified) {
                                    rightIcon = C0691R.drawable.check_profile_fixed;
                                }
                                this.nameTextView[a].setLeftDrawable(leftIcon);
                                this.nameTextView[a].setRightDrawable(rightIcon);
                            }
                        }
                        if (!this.nameTextView[a].getText().equals(newString)) {
                            this.nameTextView[a].setText(newString);
                        }
                        if (this.onlineTextView[a].getText().equals(newString2)) {
                            this.onlineTextView[a].setText(newString2);
                        }
                        if (this.currentEncryptedChat == null) {
                        }
                        rightIcon = 0;
                        if (a == 0) {
                            if (this.dialog_id == 0) {
                            }
                            if (MessagesController.getInstance().isDialogMuted(this.dialog_id == 0 ? this.dialog_id : (long) this.user_id)) {
                            }
                        } else if (user.verified) {
                            rightIcon = C0691R.drawable.check_profile_fixed;
                        }
                        this.nameTextView[a].setLeftDrawable(leftIcon);
                        this.nameTextView[a].setRightDrawable(rightIcon);
                    }
                }
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(photoBig), false);
            } else if (this.chat_id != 0) {
                int[] result;
                String shortNumber;
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                } else {
                    chat = this.currentChat;
                }
                Object[] objArr;
                if (!ChatObject.isChannel(chat)) {
                    int count = chat.participants_count;
                    if (this.info != null) {
                        count = this.info.participants.participants.size();
                    }
                    if (count == 0 || this.onlineCount <= add_contact) {
                        newString = LocaleController.formatPluralString("Members", count);
                    } else {
                        objArr = new Object[block_contact];
                        objArr[0] = LocaleController.formatPluralString("Members", count);
                        objArr[add_contact] = LocaleController.formatPluralString("Online", this.onlineCount);
                        newString = String.format("%s, %s", objArr);
                    }
                } else if (this.info == null || (!this.currentChat.megagroup && (this.info.participants_count == 0 || this.currentChat.admin || this.info.can_view_participants))) {
                    if (this.currentChat.megagroup) {
                        newString = LocaleController.getString("Loading", C0691R.string.Loading).toLowerCase();
                    } else if ((chat.flags & 64) != 0) {
                        newString = LocaleController.getString("ChannelPublic", C0691R.string.ChannelPublic).toLowerCase();
                    } else {
                        newString = LocaleController.getString("ChannelPrivate", C0691R.string.ChannelPrivate).toLowerCase();
                    }
                } else if (!this.currentChat.megagroup || this.info.participants_count > 200) {
                    result = new int[add_contact];
                    shortNumber = LocaleController.formatShortNumber(this.info.participants_count, result);
                    String formatPluralString = LocaleController.formatPluralString("Members", result[0]);
                    Object[] objArr2 = new Object[add_contact];
                    objArr2[0] = Integer.valueOf(result[0]);
                    newString = formatPluralString.replace(String.format("%d", objArr2), shortNumber);
                } else if (this.onlineCount <= add_contact || this.info.participants_count == 0) {
                    newString = LocaleController.formatPluralString("Members", this.info.participants_count);
                } else {
                    objArr = new Object[block_contact];
                    objArr[0] = LocaleController.formatPluralString("Members", this.info.participants_count);
                    objArr[add_contact] = LocaleController.formatPluralString("Online", this.onlineCount);
                    newString = String.format("%s, %s", objArr);
                }
                for (a = 0; a < block_contact; a += add_contact) {
                    if (this.nameTextView[a] != null) {
                        if (chat.title != null) {
                            if (!this.nameTextView[a].getText().equals(chat.title)) {
                                this.nameTextView[a].setText(chat.title);
                            }
                        }
                        this.nameTextView[a].setLeftDrawable(null);
                        if (a == 0) {
                            this.nameTextView[a].setRightDrawable(MessagesController.getInstance().isDialogMuted((long) (-this.chat_id)) ? C0691R.drawable.mute_fixed : 0);
                        } else if (chat.verified) {
                            this.nameTextView[a].setRightDrawable((int) C0691R.drawable.check_profile_fixed);
                        } else {
                            this.nameTextView[a].setRightDrawable(null);
                        }
                        if (!this.currentChat.megagroup || this.info == null || this.info.participants_count > 200 || this.onlineCount <= 0) {
                            if (a == 0 && ChatObject.isChannel(this.currentChat) && this.info != null && this.info.participants_count != 0 && (this.currentChat.megagroup || this.currentChat.broadcast)) {
                                result = new int[add_contact];
                                shortNumber = LocaleController.formatShortNumber(this.info.participants_count, result);
                                SimpleTextView simpleTextView = this.onlineTextView[a];
                                String formatPluralString2 = LocaleController.formatPluralString("Members", result[0]);
                                Object[] objArr3 = new Object[add_contact];
                                objArr3[0] = Integer.valueOf(result[0]);
                                simpleTextView.setText(formatPluralString2.replace(String.format("%d", objArr3), shortNumber));
                            } else if (!this.onlineTextView[a].getText().equals(newString)) {
                                this.onlineTextView[a].setText(newString);
                            }
                        } else if (!this.onlineTextView[a].getText().equals(newString)) {
                            this.onlineTextView[a].setText(newString);
                        }
                    }
                }
                photo = null;
                photoBig = null;
                if (chat.photo != null) {
                    photo = chat.photo.photo_small;
                    photoBig = chat.photo.photo_big;
                }
                this.avatarDrawable.setInfo(chat);
                this.avatarImage.setImage(photo, "50_50", this.avatarDrawable);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(photoBig), false);
            }
        }
    }

    private void createActionBarMenu() {
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.clearItems();
        this.animatingItem = null;
        ActionBarMenuItem item = null;
        if (this.user_id != 0) {
            if (ContactsController.getInstance().contactsDict.get(this.user_id) == null) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                if (user != null) {
                    item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
                    if (user.bot) {
                        if (!user.bot_nochats) {
                            item.addSubItem(invite_to_group, LocaleController.getString("BotInvite", C0691R.string.BotInvite), 0);
                        }
                        item.addSubItem(share, LocaleController.getString("BotShare", C0691R.string.BotShare), 0);
                    }
                    if (user.phone != null && user.phone.length() != 0) {
                        String string;
                        item.addSubItem(add_contact, LocaleController.getString("AddContact", C0691R.string.AddContact), 0);
                        item.addSubItem(share_contact, LocaleController.getString("ShareContact", C0691R.string.ShareContact), 0);
                        if (this.userBlocked) {
                            string = LocaleController.getString("Unblock", C0691R.string.Unblock);
                        } else {
                            string = LocaleController.getString("BlockContact", C0691R.string.BlockContact);
                        }
                        item.addSubItem(block_contact, string, 0);
                    } else if (user.bot) {
                        item.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BotStop", C0691R.string.BotStop) : LocaleController.getString("BotRestart", C0691R.string.BotRestart), 0);
                    } else {
                        item.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BlockContact", C0691R.string.BlockContact) : LocaleController.getString("Unblock", C0691R.string.Unblock), 0);
                    }
                } else {
                    return;
                }
            }
            item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
            item.addSubItem(share_contact, LocaleController.getString("ShareContact", C0691R.string.ShareContact), 0);
            item.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BlockContact", C0691R.string.BlockContact) : LocaleController.getString("Unblock", C0691R.string.Unblock), 0);
            item.addSubItem(edit_contact, LocaleController.getString("EditContact", C0691R.string.EditContact), 0);
            item.addSubItem(delete_contact, LocaleController.getString("DeleteContact", C0691R.string.DeleteContact), 0);
        } else if (this.chat_id != 0) {
            if (this.chat_id > 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (this.writeButton != null) {
                    boolean isChannel = ChatObject.isChannel(this.currentChat);
                    if ((!isChannel || this.currentChat.creator || (this.currentChat.megagroup && this.currentChat.editor)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        this.writeButton.setImageResource(C0691R.drawable.floating_camera);
                        this.writeButton.setPadding(0, 0, 0, 0);
                    } else {
                        this.writeButton.setImageResource(C0691R.drawable.floating_message);
                        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                    }
                }
                if (ChatObject.isChannel(chat)) {
                    if (chat.creator || (chat.megagroup && chat.editor)) {
                        item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
                        item.addSubItem(edit_channel, LocaleController.getString("ChannelEdit", C0691R.string.ChannelEdit), 0);
                    }
                    if (!(chat.creator || chat.left || chat.kicked || !chat.megagroup)) {
                        if (item == null) {
                            item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
                        }
                        item.addSubItem(leave_group, LocaleController.getString("LeaveMegaMenu", C0691R.string.LeaveMegaMenu), 0);
                    }
                } else {
                    item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
                    if (chat.creator && this.chat_id > 0) {
                        item.addSubItem(set_admins, LocaleController.getString("SetAdmins", C0691R.string.SetAdmins), 0);
                    }
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        item.addSubItem(edit_name, LocaleController.getString("EditName", C0691R.string.EditName), 0);
                    }
                    if (chat.creator && (this.info == null || this.info.participants.participants.size() > add_contact)) {
                        item.addSubItem(convert_to_supergroup, LocaleController.getString("ConvertGroupMenu", C0691R.string.ConvertGroupMenu), 0);
                    }
                    item.addSubItem(leave_group, LocaleController.getString("DeleteAndExit", C0691R.string.DeleteAndExit), 0);
                }
            } else {
                item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
                item.addSubItem(edit_name, LocaleController.getString("EditName", C0691R.string.EditName), 0);
            }
        }
        if (item == null) {
            item = menu.addItem((int) share, (int) C0691R.drawable.ic_ab_other);
        }
        item.addSubItem(add_shortcut, LocaleController.getString("AddShortcut", C0691R.string.AddShortcut), 0);
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public void didSelectDialog(DialogsActivity fragment, long dialog_id, boolean param) {
        if (dialog_id != 0) {
            Bundle args = new Bundle();
            args.putBoolean("scrollToTopOnResume", true);
            int lower_part = (int) dialog_id;
            if (lower_part == 0) {
                args.putInt("enc_id", (int) (dialog_id >> 32));
            } else if (lower_part > 0) {
                args.putInt("user_id", lower_part);
            } else if (lower_part < 0) {
                args.putInt("chat_id", -lower_part);
            }
            if (MessagesController.checkCanOpenChat(args, fragment)) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args), true);
                removeSelfFromStack();
                SendMessagesHelper.getInstance().sendMessage(MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)), dialog_id, null, null, null);
            }
        }
    }
}
