package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.appsgeyser.sdk.ads.FullScreenBanner;
import com.appsgeyser.sdk.ads.IFullScreenBannerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

public class LaunchActivity extends Activity implements ActionBarLayoutDelegate, NotificationCenterDelegate, DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack;
    private static ArrayList<BaseFragment> mainFragmentsStack;
    private static ArrayList<BaseFragment> rightFragmentsStack;
    private FullScreenBanner _banner;
    private boolean _bannerLoading;
    private ActionBarLayout actionBarLayout;
    private ImageView backgroundTablet;
    private ArrayList<User> contactsToSend;
    private int currentConnectionState;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    private ActionBarLayout layersActionBarLayout;
    private Runnable lockRunnable;
    private OnGlobalLayoutListener onGlobalLayoutListener;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    private ArrayList<Uri> photoPathsArray;
    private ProgressDialog progressDialog;
    private ActionBarLayout rightActionBarLayout;
    private String sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private boolean tabletFullSize;
    private String videoPath;
    private AlertDialog visibleDialog;

    /* renamed from: org.telegram.ui.LaunchActivity.12 */
    class AnonymousClass12 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        AnonymousClass12(int i) {
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

    /* renamed from: org.telegram.ui.LaunchActivity.17 */
    class AnonymousClass17 implements OnClickListener {
        final /* synthetic */ HashMap val$waitingForLocation;

        /* renamed from: org.telegram.ui.LaunchActivity.17.1 */
        class C18681 implements LocationActivityDelegate {
            C18681() {
            }

            public void didSelectLocation(MessageMedia location) {
                for (Entry<String, MessageObject> entry : AnonymousClass17.this.val$waitingForLocation.entrySet()) {
                    MessageObject messageObject = (MessageObject) entry.getValue();
                    SendMessagesHelper.getInstance().sendMessage(location, messageObject.getDialogId(), messageObject, null, null);
                }
            }
        }

        AnonymousClass17(HashMap hashMap) {
            this.val$waitingForLocation = hashMap;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (!LaunchActivity.mainFragmentsStack.isEmpty() && AndroidUtilities.isGoogleMapsInstalled((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                LocationActivity fragment = new LocationActivity();
                fragment.setDelegate(new C18681());
                LaunchActivity.this.presentFragment(fragment);
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.2 */
    class C12652 implements OnTouchListener {
        C12652() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
                return false;
            }
            float x = event.getX();
            float y = event.getY();
            int[] location = new int[2];
            LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(location);
            int viewX = location[0];
            int viewY = location[1];
            if (LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) viewX) && x < ((float) (LaunchActivity.this.layersActionBarLayout.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (LaunchActivity.this.layersActionBarLayout.getHeight() + viewY)))) {
                return false;
            }
            if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                int a = 0;
                while (LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                    LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                    a = (a - 1) + 1;
                }
                LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.3 */
    class C12663 implements View.OnClickListener {
        C12663() {
        }

        public void onClick(View v) {
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.4 */
    class C12674 extends ListView {
        C12674(Context x0) {
            super(x0);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.5 */
    class C12685 implements OnItemClickListener {
        C12685() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (position == 2) {
                if (MessagesController.isFeatureEnabled("chat_create", (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1))) {
                    LaunchActivity.this.presentFragment(new GroupCreateActivity());
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                }
            } else if (position == 3) {
                args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                LaunchActivity.this.presentFragment(new ContactsActivity(args));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (position == 4) {
                if (MessagesController.isFeatureEnabled("broadcast_create", (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1))) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (preferences.getBoolean("channel_intro", false)) {
                        args = new Bundle();
                        args.putInt("step", 0);
                        LaunchActivity.this.presentFragment(new ChannelCreateActivity(args));
                    } else {
                        LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                        preferences.edit().putBoolean("channel_intro", true).commit();
                    }
                    LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                }
            } else if (position == 6) {
                LaunchActivity.this.presentFragment(new ContactsActivity(null));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (position == 7) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                    LaunchActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteFriends", C0691R.string.InviteFriends)), 500);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (position == 8) {
                LaunchActivity.this.presentFragment(new SettingsActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (position == 9) {
                Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", C0691R.string.TelegramFaqUrl));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.6 */
    class C12696 implements OnGlobalLayoutListener {
        final /* synthetic */ View val$view;

        C12696(View view) {
            this.val$view = view;
        }

        public void onGlobalLayout() {
            int height = this.val$view.getMeasuredHeight();
            if (height > AndroidUtilities.dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + height > AndroidUtilities.displaySize.y) {
                AndroidUtilities.displaySize.y = height;
                FileLog.m11e("tmessages", "fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    private class VcardData {
        String name;
        ArrayList<String> phones;

        private VcardData() {
            this.phones = new ArrayList();
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: org.telegram.ui.LaunchActivity.10.1 */
        class C12631 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C12631(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (!LaunchActivity.this.isFinishing()) {
                    try {
                        AnonymousClass10.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    if (this.val$error != null) {
                        Builder builder = new Builder(LaunchActivity.this);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            builder.setMessage(LocaleController.getString("FloodWait", C0691R.string.FloodWait));
                        } else if (this.val$error.text.equals("USERS_TOO_MUCH")) {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", C0691R.string.JoinToGroupErrorFull));
                        } else {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", C0691R.string.JoinToGroupErrorNotExist));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        LaunchActivity.this.showAlertDialog(builder);
                    } else if (LaunchActivity.this.actionBarLayout != null) {
                        Updates updates = this.val$response;
                        if (!updates.chats.isEmpty()) {
                            Chat chat = (Chat) updates.chats.get(0);
                            chat.left = false;
                            chat.kicked = false;
                            MessagesController.getInstance().putUsers(updates.users, false);
                            MessagesController.getInstance().putChats(updates.chats, false);
                            Bundle args = new Bundle();
                            args.putInt("chat_id", chat.id);
                            if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                ChatActivity fragment = new ChatActivity(args);
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                LaunchActivity.this.actionBarLayout.presentFragment(fragment, false, true, true);
                            }
                        }
                    }
                }
            }
        }

        AnonymousClass10(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesController.getInstance().processUpdates((Updates) response, false);
            }
            AndroidUtilities.runOnUIThread(new C12631(error, response));
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.11 */
    class AnonymousClass11 implements DialogsActivityDelegate {
        final /* synthetic */ boolean val$hasUrl;
        final /* synthetic */ String val$message;

        AnonymousClass11(boolean z, String str) {
            this.val$hasUrl = z;
            this.val$message = str;
        }

        public void didSelectDialog(DialogsActivity fragment, long did, boolean param) {
            Bundle args = new Bundle();
            args.putBoolean("scrollToTopOnResume", true);
            args.putBoolean("hasUrl", this.val$hasUrl);
            int lower_part = (int) did;
            int high_id = (int) (did >> 32);
            if (lower_part == 0) {
                args.putInt("enc_id", high_id);
            } else if (high_id == 1) {
                args.putInt("chat_id", lower_part);
            } else if (lower_part > 0) {
                args.putInt("user_id", lower_part);
            } else if (lower_part < 0) {
                args.putInt("chat_id", -lower_part);
            }
            if (MessagesController.checkCanOpenChat(args, fragment)) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                DraftQuery.saveDraft(did, this.val$message, null, null, true);
                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.1 */
    class C18691 implements IFullScreenBannerListener {
        C18691() {
        }

        public void onLoadStarted() {
            LaunchActivity.this._bannerLoading = true;
            LaunchActivity.this.showProgress();
        }

        public void onLoadFinished() {
            LaunchActivity.this._bannerLoading = false;
            LaunchActivity.this.hideProgress();
            LaunchActivity.this._banner.show();
        }

        public void onAdFailedToLoad() {
            LaunchActivity.this._bannerLoading = false;
            LaunchActivity.this.hideProgress();
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.7 */
    class C18707 implements PasscodeViewDelegate {
        C18707() {
        }

        public void didAcceptedPassword() {
            UserConfig.isWaitingForPasscodeEnter = false;
            if (LaunchActivity.this.passcodeSaveIntent != null) {
                LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
                LaunchActivity.this.passcodeSaveIntent = null;
            }
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            LaunchActivity.this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                LaunchActivity.this.layersActionBarLayout.showLastFragment();
                LaunchActivity.this.rightActionBarLayout.showLastFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.8 */
    class C18728 implements RequestDelegate {
        final /* synthetic */ String val$botChat;
        final /* synthetic */ String val$botUser;
        final /* synthetic */ Integer val$messageId;
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: org.telegram.ui.LaunchActivity.8.1 */
        class C12701 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.ui.LaunchActivity.8.1.1 */
            class C18711 implements DialogsActivityDelegate {
                final /* synthetic */ User val$user;

                C18711(User user) {
                    this.val$user = user;
                }

                public void didSelectDialog(DialogsActivity fragment, long did, boolean param) {
                    Bundle args = new Bundle();
                    args.putBoolean("scrollToTopOnResume", true);
                    args.putInt("chat_id", -((int) did));
                    if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        MessagesController.getInstance().addUserToChat(-((int) did), this.val$user, null, 0, C18728.this.val$botChat, null);
                        LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(args), true, false, true);
                    }
                }
            }

            C12701(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (!LaunchActivity.this.isFinishing()) {
                    try {
                        C18728.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    if (this.val$error != null || LaunchActivity.this.actionBarLayout == null) {
                        try {
                            Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", C0691R.string.NoUsernameFound), 0).show();
                            return;
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                            return;
                        }
                    }
                    TL_contacts_resolvedPeer res = this.val$response;
                    MessagesController.getInstance().putUsers(res.users, false);
                    MessagesController.getInstance().putChats(res.chats, false);
                    MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, false, true);
                    Bundle args;
                    if (C18728.this.val$botChat != null) {
                        User user = !res.users.isEmpty() ? (User) res.users.get(0) : null;
                        if (user == null || (user.bot && user.bot_nochats)) {
                            try {
                                Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", C0691R.string.BotCantJoinGroups), 0).show();
                                return;
                            } catch (Throwable e22) {
                                FileLog.m13e("tmessages", e22);
                                return;
                            }
                        }
                        args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putInt("dialogsType", 2);
                        args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0691R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                        DialogsActivity fragment = new DialogsActivity(args);
                        fragment.setDelegate(new C18711(user));
                        LaunchActivity.this.presentFragment(fragment);
                        return;
                    }
                    boolean isBot = false;
                    args = new Bundle();
                    long dialog_id;
                    if (res.chats.isEmpty()) {
                        args.putInt("user_id", ((User) res.users.get(0)).id);
                        dialog_id = (long) ((User) res.users.get(0)).id;
                    } else {
                        args.putInt("chat_id", ((Chat) res.chats.get(0)).id);
                        dialog_id = (long) (-((Chat) res.chats.get(0)).id);
                    }
                    if (C18728.this.val$botUser != null && res.users.size() > 0 && ((User) res.users.get(0)).bot) {
                        args.putString("botUser", C18728.this.val$botUser);
                        isBot = true;
                    }
                    if (C18728.this.val$messageId != null) {
                        args.putInt("message_id", C18728.this.val$messageId.intValue());
                    }
                    BaseFragment lastFragment = !LaunchActivity.mainFragmentsStack.isEmpty() ? (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1) : null;
                    if (lastFragment != null && !MessagesController.checkCanOpenChat(args, lastFragment)) {
                        return;
                    }
                    if (isBot && lastFragment != null && (lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).getDialogId() == dialog_id) {
                        ((ChatActivity) lastFragment).setBotUser(C18728.this.val$botUser);
                        return;
                    }
                    ChatActivity fragment2 = new ChatActivity(args);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    LaunchActivity.this.actionBarLayout.presentFragment(fragment2, false, true, true);
                }
            }
        }

        C18728(ProgressDialog progressDialog, String str, String str2, Integer num) {
            this.val$progressDialog = progressDialog;
            this.val$botChat = str;
            this.val$botUser = str2;
            this.val$messageId = num;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C12701(error, response));
        }
    }

    /* renamed from: org.telegram.ui.LaunchActivity.9 */
    class C18739 implements RequestDelegate {
        final /* synthetic */ String val$botChat;
        final /* synthetic */ String val$botUser;
        final /* synthetic */ String val$group;
        final /* synthetic */ boolean val$hasUrl;
        final /* synthetic */ String val$message;
        final /* synthetic */ Integer val$messageId;
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ String val$sticker;
        final /* synthetic */ String val$username;

        /* renamed from: org.telegram.ui.LaunchActivity.9.1 */
        class C12721 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.ui.LaunchActivity.9.1.1 */
            class C12711 implements OnClickListener {
                C12711() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.runLinkRequest(C18739.this.val$username, C18739.this.val$group, C18739.this.val$sticker, C18739.this.val$botUser, C18739.this.val$botChat, C18739.this.val$message, C18739.this.val$hasUrl, C18739.this.val$messageId, 1);
                }
            }

            C12721(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (!LaunchActivity.this.isFinishing()) {
                    try {
                        C18739.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    Builder builder;
                    if (this.val$error != null || LaunchActivity.this.actionBarLayout == null) {
                        builder = new Builder(LaunchActivity.this);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            builder.setMessage(LocaleController.getString("FloodWait", C0691R.string.FloodWait));
                        } else {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", C0691R.string.JoinToGroupErrorNotExist));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        LaunchActivity.this.showAlertDialog(builder);
                        return;
                    }
                    ChatInvite invite = this.val$response;
                    if (invite.chat == null || ChatObject.isLeftFromChat(invite.chat)) {
                        builder = new Builder(LaunchActivity.this);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        String str;
                        Object[] objArr;
                        if ((invite.megagroup || !invite.channel) && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup)) {
                            str = "JoinToGroup";
                            objArr = new Object[1];
                            objArr[0] = invite.chat != null ? invite.chat.title : invite.title;
                            builder.setMessage(LocaleController.formatString(str, C0691R.string.JoinToGroup, objArr));
                        } else {
                            str = "ChannelJoinTo";
                            objArr = new Object[1];
                            objArr[0] = invite.chat != null ? invite.chat.title : invite.title;
                            builder.setMessage(LocaleController.formatString(str, C0691R.string.ChannelJoinTo, objArr));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C12711());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        LaunchActivity.this.showAlertDialog(builder);
                        return;
                    }
                    MessagesController.getInstance().putChat(invite.chat, false);
                    ArrayList<Chat> chats = new ArrayList();
                    chats.add(invite.chat);
                    MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", invite.chat.id);
                    if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(args, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                        ChatActivity fragment = new ChatActivity(args);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        LaunchActivity.this.actionBarLayout.presentFragment(fragment, false, true, true);
                    }
                }
            }
        }

        C18739(ProgressDialog progressDialog, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num) {
            this.val$progressDialog = progressDialog;
            this.val$username = str;
            this.val$group = str2;
            this.val$sticker = str3;
            this.val$botUser = str4;
            this.val$botChat = str5;
            this.val$message = str6;
            this.val$hasUrl = z;
            this.val$messageId = num;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C12721(error, response));
        }
    }

    public LaunchActivity() {
        this._banner = null;
        this._bannerLoading = false;
    }

    static {
        mainFragmentsStack = new ArrayList();
        layerFragmentsStack = new ArrayList();
        rightFragmentsStack = new ArrayList();
    }

    public void showProgress() {
        if (!isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    public void hideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    public void loadBanner() {
        if (!this._bannerLoading) {
            this._banner.load();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(android.os.Bundle r33) {
        /*
        r32 = this;
        org.telegram.messenger.ApplicationLoader.postInitApplication();
        org.telegram.messenger.NativeCrashManager.handleDumpFiles(r32);
        r27 = r32.getApplication();
        r28 = 2131166514; // 0x7f070532 float:1.7947276E38 double:1.05293616E-314;
        r0 = r32;
        r1 = r28;
        r28 = r0.getString(r1);
        com.appsgeyser.sdk.AppsgeyserSDK.takeOff(r27, r28);
        r6 = com.appsgeyser.sdk.AppsgeyserSDK.getAnalytics();
        if (r6 == 0) goto L_0x0021;
    L_0x001e:
        r6.ActivityStarted();
    L_0x0021:
        r27 = com.appsgeyser.sdk.AppsgeyserSDK.getFullScreenBanner();
        r0 = r27;
        r1 = r32;
        r1._banner = r0;
        r0 = r32;
        r0 = r0._banner;
        r27 = r0;
        r28 = new org.telegram.ui.LaunchActivity$1;
        r0 = r28;
        r1 = r32;
        r0.<init>();
        r27.setListener(r28);
        r27 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r27 != 0) goto L_0x00a9;
    L_0x0043:
        r13 = r32.getIntent();
        if (r13 == 0) goto L_0x006e;
    L_0x0049:
        r27 = r13.getAction();
        if (r27 == 0) goto L_0x006e;
    L_0x004f:
        r27 = "android.intent.action.SEND";
        r28 = r13.getAction();
        r27 = r27.equals(r28);
        if (r27 != 0) goto L_0x0067;
    L_0x005b:
        r27 = r13.getAction();
        r28 = "android.intent.action.SEND_MULTIPLE";
        r27 = r27.equals(r28);
        if (r27 == 0) goto L_0x006e;
    L_0x0067:
        super.onCreate(r33);
        r32.finish();
    L_0x006d:
        return;
    L_0x006e:
        if (r13 == 0) goto L_0x00a9;
    L_0x0070:
        r27 = "fromIntro";
        r28 = 0;
        r0 = r27;
        r1 = r28;
        r27 = r13.getBooleanExtra(r0, r1);
        if (r27 != 0) goto L_0x00a9;
    L_0x007e:
        r27 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r28 = "logininfo2";
        r29 = 0;
        r19 = r27.getSharedPreferences(r28, r29);
        r25 = r19.getAll();
        r27 = r25.isEmpty();
        if (r27 == 0) goto L_0x00a9;
    L_0x0092:
        r14 = new android.content.Intent;
        r27 = org.telegram.ui.IntroActivity.class;
        r0 = r32;
        r1 = r27;
        r14.<init>(r0, r1);
        r0 = r32;
        r0.startActivity(r14);
        super.onCreate(r33);
        r32.finish();
        goto L_0x006d;
    L_0x00a9:
        r27 = 1;
        r0 = r32;
        r1 = r27;
        r0.requestWindowFeature(r1);
        r27 = 2131296262; // 0x7f090006 float:1.8210436E38 double:1.053000264E-314;
        r0 = r32;
        r1 = r27;
        r0.setTheme(r1);
        r27 = r32.getWindow();
        r28 = 2130837998; // 0x7f0201ee float:1.7280966E38 double:1.0527738517E-314;
        r27.setBackgroundDrawableResource(r28);
        super.onCreate(r33);
        org.telegram.ui.ActionBar.Theme.loadRecources(r32);
        r27 = org.telegram.messenger.UserConfig.passcodeHash;
        r27 = r27.length();
        if (r27 == 0) goto L_0x00e2;
    L_0x00d4:
        r27 = org.telegram.messenger.UserConfig.appLocked;
        if (r27 == 0) goto L_0x00e2;
    L_0x00d8:
        r27 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r27 = r27.getCurrentTime();
        org.telegram.messenger.UserConfig.lastPauseTime = r27;
    L_0x00e2:
        r27 = r32.getResources();
        r28 = "status_bar_height";
        r29 = "dimen";
        r30 = "android";
        r22 = r27.getIdentifier(r28, r29, r30);
        if (r22 <= 0) goto L_0x0100;
    L_0x00f2:
        r27 = r32.getResources();
        r0 = r27;
        r1 = r22;
        r27 = r0.getDimensionPixelSize(r1);
        org.telegram.messenger.AndroidUtilities.statusBarHeight = r27;
    L_0x0100:
        r27 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.actionBarLayout = r0;
        r27 = new org.telegram.ui.ActionBar.DrawerLayoutContainer;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.drawerLayoutContainer = r0;
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r28 = new android.view.ViewGroup$LayoutParams;
        r29 = -1;
        r30 = -1;
        r28.<init>(r29, r30);
        r0 = r32;
        r1 = r27;
        r2 = r28;
        r0.setContentView(r1, r2);
        r27 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r27 == 0) goto L_0x0612;
    L_0x013c:
        r27 = r32.getWindow();
        r28 = 16;
        r27.setSoftInputMode(r28);
        r15 = new android.widget.RelativeLayout;
        r0 = r32;
        r15.<init>(r0);
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r0 = r27;
        r0.addView(r15);
        r17 = r15.getLayoutParams();
        r17 = (android.widget.FrameLayout.LayoutParams) r17;
        r27 = -1;
        r0 = r27;
        r1 = r17;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r17;
        r1.height = r0;
        r0 = r17;
        r15.setLayoutParams(r0);
        r27 = new android.widget.ImageView;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.backgroundTablet = r0;
        r0 = r32;
        r0 = r0.backgroundTablet;
        r27 = r0;
        r28 = android.widget.ImageView.ScaleType.CENTER_CROP;
        r27.setScaleType(r28);
        r0 = r32;
        r0 = r0.backgroundTablet;
        r27 = r0;
        r28 = 2130837582; // 0x7f02004e float:1.7280122E38 double:1.052773646E-314;
        r27.setImageResource(r28);
        r0 = r32;
        r0 = r0.backgroundTablet;
        r27 = r0;
        r0 = r27;
        r15.addView(r0);
        r0 = r32;
        r0 = r0.backgroundTablet;
        r27 = r0;
        r21 = r27.getLayoutParams();
        r21 = (android.widget.RelativeLayout.LayoutParams) r21;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.height = r0;
        r0 = r32;
        r0 = r0.backgroundTablet;
        r27 = r0;
        r0 = r27;
        r1 = r21;
        r0.setLayoutParams(r1);
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r15.addView(r0);
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r21 = r27.getLayoutParams();
        r21 = (android.widget.RelativeLayout.LayoutParams) r21;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.height = r0;
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r1 = r21;
        r0.setLayoutParams(r1);
        r27 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.rightActionBarLayout = r0;
        r0 = r32;
        r0 = r0.rightActionBarLayout;
        r27 = r0;
        r0 = r27;
        r15.addView(r0);
        r0 = r32;
        r0 = r0.rightActionBarLayout;
        r27 = r0;
        r21 = r27.getLayoutParams();
        r21 = (android.widget.RelativeLayout.LayoutParams) r21;
        r27 = 1134559232; // 0x43a00000 float:320.0 double:5.605467397E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r27;
        r1 = r21;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.height = r0;
        r0 = r32;
        r0 = r0.rightActionBarLayout;
        r27 = r0;
        r0 = r27;
        r1 = r21;
        r0.setLayoutParams(r1);
        r0 = r32;
        r0 = r0.rightActionBarLayout;
        r27 = r0;
        r28 = rightFragmentsStack;
        r27.init(r28);
        r0 = r32;
        r0 = r0.rightActionBarLayout;
        r27 = r0;
        r0 = r27;
        r1 = r32;
        r0.setDelegate(r1);
        r27 = new android.widget.FrameLayout;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.shadowTabletSide = r0;
        r0 = r32;
        r0 = r0.shadowTabletSide;
        r27 = r0;
        r28 = 1076449908; // 0x40295274 float:2.6456575 double:5.31836919E-315;
        r27.setBackgroundColor(r28);
        r0 = r32;
        r0 = r0.shadowTabletSide;
        r27 = r0;
        r0 = r27;
        r15.addView(r0);
        r0 = r32;
        r0 = r0.shadowTabletSide;
        r27 = r0;
        r21 = r27.getLayoutParams();
        r21 = (android.widget.RelativeLayout.LayoutParams) r21;
        r27 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r27;
        r1 = r21;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.height = r0;
        r0 = r32;
        r0 = r0.shadowTabletSide;
        r27 = r0;
        r0 = r27;
        r1 = r21;
        r0.setLayoutParams(r1);
        r27 = new android.widget.FrameLayout;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.shadowTablet = r0;
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r28 = 8;
        r27.setVisibility(r28);
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r28 = 2130706432; // 0x7f000000 float:1.7014118E38 double:1.0527088494E-314;
        r27.setBackgroundColor(r28);
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r0 = r27;
        r15.addView(r0);
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r21 = r27.getLayoutParams();
        r21 = (android.widget.RelativeLayout.LayoutParams) r21;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r21;
        r1.height = r0;
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r0 = r27;
        r1 = r21;
        r0.setLayoutParams(r1);
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r28 = new org.telegram.ui.LaunchActivity$2;
        r0 = r28;
        r1 = r32;
        r0.<init>();
        r27.setOnTouchListener(r28);
        r0 = r32;
        r0 = r0.shadowTablet;
        r27 = r0;
        r28 = new org.telegram.ui.LaunchActivity$3;
        r0 = r28;
        r1 = r32;
        r0.<init>();
        r27.setOnClickListener(r28);
        r27 = new org.telegram.ui.ActionBar.ActionBarLayout;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.layersActionBarLayout = r0;
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r28 = 1;
        r27.setRemoveActionBarExtraHeight(r28);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r32;
        r0 = r0.shadowTablet;
        r28 = r0;
        r27.setBackgroundView(r28);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r28 = 1;
        r27.setUseAlphaAnimations(r28);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r28 = 2130837572; // 0x7f020044 float:1.7280102E38 double:1.052773641E-314;
        r27.setBackgroundResource(r28);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r27;
        r15.addView(r0);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r21 = r27.getLayoutParams();
        r21 = (android.widget.RelativeLayout.LayoutParams) r21;
        r27 = 1141145600; // 0x44048000 float:530.0 double:5.63800838E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r27;
        r1 = r21;
        r1.width = r0;
        r27 = 1141112832; // 0x44040000 float:528.0 double:5.637846483E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r27;
        r1 = r21;
        r1.height = r0;
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r27;
        r1 = r21;
        r0.setLayoutParams(r1);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r28 = layerFragmentsStack;
        r27.init(r28);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r27;
        r1 = r32;
        r0.setDelegate(r1);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r28 = r0;
        r27.setDrawerLayoutContainer(r28);
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r28 = 8;
        r27.setVisibility(r28);
    L_0x03de:
        r18 = new org.telegram.ui.LaunchActivity$4;
        r0 = r18;
        r1 = r32;
        r2 = r32;
        r0.<init>(r2);
        r27 = r32.getResources();
        r28 = 2131427328; // 0x7f0b0000 float:1.847627E38 double:1.0530650194E-314;
        r27 = r27.getColor(r28);
        r0 = r18;
        r1 = r27;
        r0.setBackgroundColor(r1);
        r27 = new org.telegram.ui.Adapters.DrawerLayoutAdapter;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.drawerLayoutAdapter = r0;
        r0 = r18;
        r1 = r27;
        r0.setAdapter(r1);
        r27 = 1;
        r0 = r18;
        r1 = r27;
        r0.setChoiceMode(r1);
        r27 = 0;
        r0 = r18;
        r1 = r27;
        r0.setDivider(r1);
        r27 = 0;
        r0 = r18;
        r1 = r27;
        r0.setDividerHeight(r1);
        r27 = 0;
        r0 = r18;
        r1 = r27;
        r0.setVerticalScrollBarEnabled(r1);
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r0 = r27;
        r1 = r18;
        r0.setDrawerLayout(r1);
        r16 = r18.getLayoutParams();
        r16 = (android.widget.FrameLayout.LayoutParams) r16;
        r23 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();
        r27 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r27 == 0) goto L_0x062c;
    L_0x0451:
        r27 = 1134559232; // 0x43a00000 float:320.0 double:5.605467397E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
    L_0x0457:
        r0 = r27;
        r1 = r16;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r16;
        r1.height = r0;
        r0 = r18;
        r1 = r16;
        r0.setLayoutParams(r1);
        r27 = new org.telegram.ui.LaunchActivity$5;
        r0 = r27;
        r1 = r32;
        r0.<init>();
        r0 = r18;
        r1 = r27;
        r0.setOnItemClickListener(r1);
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r0 = r32;
        r0 = r0.actionBarLayout;
        r28 = r0;
        r27.setParentActionBarLayout(r28);
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r28 = r0;
        r27.setDrawerLayoutContainer(r28);
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r28 = mainFragmentsStack;
        r27.init(r28);
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r1 = r32;
        r0.setDelegate(r1);
        org.telegram.messenger.ApplicationLoader.loadWallpaper();
        r27 = new org.telegram.ui.Components.PasscodeView;
        r0 = r27;
        r1 = r32;
        r0.<init>(r1);
        r0 = r27;
        r1 = r32;
        r1.passcodeView = r0;
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r0 = r32;
        r0 = r0.passcodeView;
        r28 = r0;
        r27.addView(r28);
        r0 = r32;
        r0 = r0.passcodeView;
        r27 = r0;
        r17 = r27.getLayoutParams();
        r17 = (android.widget.FrameLayout.LayoutParams) r17;
        r27 = -1;
        r0 = r27;
        r1 = r17;
        r1.width = r0;
        r27 = -1;
        r0 = r27;
        r1 = r17;
        r1.height = r0;
        r0 = r32;
        r0 = r0.passcodeView;
        r27 = r0;
        r0 = r27;
        r1 = r17;
        r0.setLayoutParams(r1);
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r29 = 1;
        r0 = r29;
        r0 = new java.lang.Object[r0];
        r29 = r0;
        r30 = 0;
        r29[r30] = r32;
        r27.postNotificationName(r28, r29);
        r27 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r27 = r27.getConnectionState();
        r0 = r27;
        r1 = r32;
        r1.currentConnectionState = r0;
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.appDidLogout;
        r0 = r27;
        r1 = r32;
        r2 = r28;
        r0.addObserver(r1, r2);
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged;
        r0 = r27;
        r1 = r32;
        r2 = r28;
        r0.addObserver(r1, r2);
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.closeOtherAppActivities;
        r0 = r27;
        r1 = r32;
        r2 = r28;
        r0.addObserver(r1, r2);
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.didUpdatedConnectionState;
        r0 = r27;
        r1 = r32;
        r2 = r28;
        r0.addObserver(r1, r2);
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r0 = r27;
        r1 = r32;
        r2 = r28;
        r0.addObserver(r1, r2);
        r27 = org.telegram.messenger.NotificationCenter.getInstance();
        r28 = org.telegram.messenger.NotificationCenter.wasUnableToFindCurrentLocation;
        r0 = r27;
        r1 = r32;
        r2 = r28;
        r0.addObserver(r1, r2);
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r27 = r27.isEmpty();
        if (r27 == 0) goto L_0x07a6;
    L_0x058b:
        r27 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r27 != 0) goto L_0x0650;
    L_0x0591:
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r28 = new org.telegram.ui.LoginActivity;
        r28.<init>();
        r27.addFragmentToStack(r28);
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r28 = 0;
        r29 = 0;
        r27.setAllowOpenDrawer(r28, r29);
    L_0x05ac:
        if (r33 == 0) goto L_0x05d0;
    L_0x05ae:
        r27 = "fragment";
        r0 = r33;
        r1 = r27;
        r11 = r0.getString(r1);	 Catch:{ Exception -> 0x06f0 }
        if (r11 == 0) goto L_0x05d0;
    L_0x05ba:
        r27 = "args";
        r0 = r33;
        r1 = r27;
        r7 = r0.getBundle(r1);	 Catch:{ Exception -> 0x06f0 }
        r27 = -1;
        r28 = r11.hashCode();	 Catch:{ Exception -> 0x06f0 }
        switch(r28) {
            case -1529105743: goto L_0x06c6;
            case -1349522494: goto L_0x06b8;
            case 3052376: goto L_0x0672;
            case 3108362: goto L_0x06aa;
            case 98629247: goto L_0x068e;
            case 738950403: goto L_0x069c;
            case 1434631203: goto L_0x0680;
            default: goto L_0x05cd;
        };
    L_0x05cd:
        switch(r27) {
            case 0: goto L_0x06d4;
            case 1: goto L_0x06fa;
            case 2: goto L_0x0715;
            case 3: goto L_0x0731;
            case 4: goto L_0x074d;
            case 5: goto L_0x0769;
            case 6: goto L_0x078b;
            default: goto L_0x05d0;
        };
    L_0x05d0:
        r28 = r32.getIntent();
        r29 = 0;
        if (r33 == 0) goto L_0x0851;
    L_0x05d8:
        r27 = 1;
    L_0x05da:
        r30 = 0;
        r0 = r32;
        r1 = r28;
        r2 = r29;
        r3 = r27;
        r4 = r30;
        r0.handleIntent(r1, r2, r3, r4);
        r32.needLayout();
        r27 = r32.getWindow();
        r27 = r27.getDecorView();
        r26 = r27.getRootView();
        r27 = r26.getViewTreeObserver();
        r28 = new org.telegram.ui.LaunchActivity$6;
        r0 = r28;
        r1 = r32;
        r2 = r26;
        r0.<init>(r2);
        r0 = r28;
        r1 = r32;
        r1.onGlobalLayoutListener = r0;
        r27.addOnGlobalLayoutListener(r28);
        goto L_0x006d;
    L_0x0612:
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r0 = r32;
        r0 = r0.actionBarLayout;
        r28 = r0;
        r29 = new android.view.ViewGroup$LayoutParams;
        r30 = -1;
        r31 = -1;
        r29.<init>(r30, r31);
        r27.addView(r28, r29);
        goto L_0x03de;
    L_0x062c:
        r27 = 1134559232; // 0x43a00000 float:320.0 double:5.605467397E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r23;
        r0 = r0.x;
        r28 = r0;
        r0 = r23;
        r0 = r0.y;
        r29 = r0;
        r28 = java.lang.Math.min(r28, r29);
        r29 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r29 = org.telegram.messenger.AndroidUtilities.dp(r29);
        r28 = r28 - r29;
        r27 = java.lang.Math.min(r27, r28);
        goto L_0x0457;
    L_0x0650:
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r28 = new org.telegram.ui.DialogsActivity;
        r29 = 0;
        r28.<init>(r29);
        r27.addFragmentToStack(r28);
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r28 = 1;
        r29 = 0;
        r27.setAllowOpenDrawer(r28, r29);
        r32.loadBanner();
        goto L_0x05ac;
    L_0x0672:
        r28 = "chat";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x067c:
        r27 = 0;
        goto L_0x05cd;
    L_0x0680:
        r28 = "settings";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x068a:
        r27 = 1;
        goto L_0x05cd;
    L_0x068e:
        r28 = "group";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x0698:
        r27 = 2;
        goto L_0x05cd;
    L_0x069c:
        r28 = "channel";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x06a6:
        r27 = 3;
        goto L_0x05cd;
    L_0x06aa:
        r28 = "edit";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x06b4:
        r27 = 4;
        goto L_0x05cd;
    L_0x06b8:
        r28 = "chat_profile";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x06c2:
        r27 = 5;
        goto L_0x05cd;
    L_0x06c6:
        r28 = "wallpapers";
        r0 = r28;
        r28 = r11.equals(r0);	 Catch:{ Exception -> 0x06f0 }
        if (r28 == 0) goto L_0x05cd;
    L_0x06d0:
        r27 = 6;
        goto L_0x05cd;
    L_0x06d4:
        if (r7 == 0) goto L_0x05d0;
    L_0x06d6:
        r9 = new org.telegram.ui.ChatActivity;	 Catch:{ Exception -> 0x06f0 }
        r9.<init>(r7);	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r27 = r0.addFragmentToStack(r9);	 Catch:{ Exception -> 0x06f0 }
        if (r27 == 0) goto L_0x05d0;
    L_0x06e9:
        r0 = r33;
        r9.restoreSelfArgs(r0);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x06f0:
        r10 = move-exception;
        r27 = "tmessages";
        r0 = r27;
        org.telegram.messenger.FileLog.m13e(r0, r10);
        goto L_0x05d0;
    L_0x06fa:
        r24 = new org.telegram.ui.SettingsActivity;	 Catch:{ Exception -> 0x06f0 }
        r24.<init>();	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r1 = r24;
        r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x06f0 }
        r0 = r24;
        r1 = r33;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x0715:
        if (r7 == 0) goto L_0x05d0;
    L_0x0717:
        r12 = new org.telegram.ui.GroupCreateFinalActivity;	 Catch:{ Exception -> 0x06f0 }
        r12.<init>(r7);	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r27 = r0.addFragmentToStack(r12);	 Catch:{ Exception -> 0x06f0 }
        if (r27 == 0) goto L_0x05d0;
    L_0x072a:
        r0 = r33;
        r12.restoreSelfArgs(r0);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x0731:
        if (r7 == 0) goto L_0x05d0;
    L_0x0733:
        r8 = new org.telegram.ui.ChannelCreateActivity;	 Catch:{ Exception -> 0x06f0 }
        r8.<init>(r7);	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r27 = r0.addFragmentToStack(r8);	 Catch:{ Exception -> 0x06f0 }
        if (r27 == 0) goto L_0x05d0;
    L_0x0746:
        r0 = r33;
        r8.restoreSelfArgs(r0);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x074d:
        if (r7 == 0) goto L_0x05d0;
    L_0x074f:
        r8 = new org.telegram.ui.ChannelEditActivity;	 Catch:{ Exception -> 0x06f0 }
        r8.<init>(r7);	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r27 = r0.addFragmentToStack(r8);	 Catch:{ Exception -> 0x06f0 }
        if (r27 == 0) goto L_0x05d0;
    L_0x0762:
        r0 = r33;
        r8.restoreSelfArgs(r0);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x0769:
        if (r7 == 0) goto L_0x05d0;
    L_0x076b:
        r20 = new org.telegram.ui.ProfileActivity;	 Catch:{ Exception -> 0x06f0 }
        r0 = r20;
        r0.<init>(r7);	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r1 = r20;
        r27 = r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x06f0 }
        if (r27 == 0) goto L_0x05d0;
    L_0x0782:
        r0 = r20;
        r1 = r33;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x078b:
        r24 = new org.telegram.ui.WallpapersActivity;	 Catch:{ Exception -> 0x06f0 }
        r24.<init>();	 Catch:{ Exception -> 0x06f0 }
        r0 = r32;
        r0 = r0.actionBarLayout;	 Catch:{ Exception -> 0x06f0 }
        r27 = r0;
        r0 = r27;
        r1 = r24;
        r0.addFragmentToStack(r1);	 Catch:{ Exception -> 0x06f0 }
        r0 = r24;
        r1 = r33;
        r0.restoreSelfArgs(r1);	 Catch:{ Exception -> 0x06f0 }
        goto L_0x05d0;
    L_0x07a6:
        r5 = 1;
        r27 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r27 == 0) goto L_0x080b;
    L_0x07ad:
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r27 = r27.size();
        r28 = 1;
        r0 = r27;
        r1 = r28;
        if (r0 > r1) goto L_0x084f;
    L_0x07c5:
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r27 = r27.isEmpty();
        if (r27 == 0) goto L_0x084f;
    L_0x07d7:
        r5 = 1;
    L_0x07d8:
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r27 = r27.size();
        r28 = 1;
        r0 = r27;
        r1 = r28;
        if (r0 != r1) goto L_0x080b;
    L_0x07f0:
        r0 = r32;
        r0 = r0.layersActionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r28 = 0;
        r27 = r27.get(r28);
        r0 = r27;
        r0 = r0 instanceof org.telegram.ui.LoginActivity;
        r27 = r0;
        if (r27 == 0) goto L_0x080b;
    L_0x080a:
        r5 = 0;
    L_0x080b:
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r27 = r27.size();
        r28 = 1;
        r0 = r27;
        r1 = r28;
        if (r0 != r1) goto L_0x083e;
    L_0x0823:
        r0 = r32;
        r0 = r0.actionBarLayout;
        r27 = r0;
        r0 = r27;
        r0 = r0.fragmentsStack;
        r27 = r0;
        r28 = 0;
        r27 = r27.get(r28);
        r0 = r27;
        r0 = r0 instanceof org.telegram.ui.LoginActivity;
        r27 = r0;
        if (r27 == 0) goto L_0x083e;
    L_0x083d:
        r5 = 0;
    L_0x083e:
        r0 = r32;
        r0 = r0.drawerLayoutContainer;
        r27 = r0;
        r28 = 0;
        r0 = r27;
        r1 = r28;
        r0.setAllowOpenDrawer(r5, r1);
        goto L_0x05d0;
    L_0x084f:
        r5 = 0;
        goto L_0x07d8;
    L_0x0851:
        r27 = 0;
        goto L_0x05da;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    private void showPasscodeActivity() {
        if (this.passcodeView != null) {
            UserConfig.appLocked = true;
            if (PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            }
            this.passcodeView.onShow();
            UserConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new C18707());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r75, boolean r76, boolean r77, boolean r78) {
        /*
        r74 = this;
        r37 = r75.getFlags();
        if (r78 != 0) goto L_0x002d;
    L_0x0006:
        r4 = 1;
        r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r4);
        if (r4 != 0) goto L_0x0011;
    L_0x000d:
        r4 = org.telegram.messenger.UserConfig.isWaitingForPasscodeEnter;
        if (r4 == 0) goto L_0x002d;
    L_0x0011:
        r74.showPasscodeActivity();
        r0 = r75;
        r1 = r74;
        r1.passcodeSaveIntent = r0;
        r0 = r76;
        r1 = r74;
        r1.passcodeSaveIntentIsNew = r0;
        r0 = r77;
        r1 = r74;
        r1.passcodeSaveIntentIsRestore = r0;
        r4 = 0;
        org.telegram.messenger.UserConfig.saveConfig(r4);
        r53 = 0;
    L_0x002c:
        return r53;
    L_0x002d:
        r53 = 0;
        r4 = 0;
        r56 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r54 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r55 = java.lang.Integer.valueOf(r4);
        r4 = 0;
        r46 = java.lang.Integer.valueOf(r4);
        if (r75 == 0) goto L_0x01a4;
    L_0x0045:
        r4 = r75.getExtras();
        if (r4 == 0) goto L_0x01a4;
    L_0x004b:
        r4 = r75.getExtras();
        r13 = "dialogId";
        r14 = 0;
        r32 = r4.getLong(r13, r14);
    L_0x0057:
        r60 = 0;
        r61 = 0;
        r4 = 0;
        r0 = r74;
        r0.photoPathsArray = r4;
        r4 = 0;
        r0 = r74;
        r0.videoPath = r4;
        r4 = 0;
        r0 = r74;
        r0.sendingText = r4;
        r4 = 0;
        r0 = r74;
        r0.documentsPathsArray = r4;
        r4 = 0;
        r0 = r74;
        r0.documentsOriginalPathsArray = r4;
        r4 = 0;
        r0 = r74;
        r0.documentsMimeType = r4;
        r4 = 0;
        r0 = r74;
        r0.documentsUrisArray = r4;
        r4 = 0;
        r0 = r74;
        r0.contactsToSend = r4;
        r4 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r4 == 0) goto L_0x00ac;
    L_0x0089:
        r39 = new org.telegram.customMessenger.CustomGroupManager;
        r0 = r39;
        r1 = r74;
        r0.<init>(r1);
        r39.addGroupsFromResources();
        if (r75 == 0) goto L_0x00a9;
    L_0x0097:
        r4 = r75.getAction();
        if (r4 == 0) goto L_0x00a9;
    L_0x009d:
        r4 = r75.getAction();
        r13 = "com.tmessages.openchat";
        r4 = r4.startsWith(r13);
        if (r4 != 0) goto L_0x00ac;
    L_0x00a9:
        r74.loadBanner();
    L_0x00ac:
        r4 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r4 == 0) goto L_0x025d;
    L_0x00b2:
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r4 = r4 & r37;
        if (r4 != 0) goto L_0x025d;
    L_0x00b8:
        if (r75 == 0) goto L_0x025d;
    L_0x00ba:
        r4 = r75.getAction();
        if (r4 == 0) goto L_0x025d;
    L_0x00c0:
        if (r77 != 0) goto L_0x025d;
    L_0x00c2:
        r4 = "android.intent.action.SEND";
        r13 = r75.getAction();
        r4 = r4.equals(r13);
        if (r4 == 0) goto L_0x0529;
    L_0x00ce:
        r36 = 0;
        r66 = r75.getType();
        if (r66 == 0) goto L_0x03cf;
    L_0x00d6:
        r4 = "text/x-vcard";
        r0 = r66;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x03cf;
    L_0x00e0:
        r4 = r75.getExtras();	 Catch:{ Exception -> 0x0245 }
        r13 = "android.intent.extra.STREAM";
        r67 = r4.get(r13);	 Catch:{ Exception -> 0x0245 }
        r67 = (android.net.Uri) r67;	 Catch:{ Exception -> 0x0245 }
        if (r67 == 0) goto L_0x03cb;
    L_0x00ee:
        r27 = r74.getContentResolver();	 Catch:{ Exception -> 0x0245 }
        r0 = r27;
        r1 = r67;
        r62 = r0.openInputStream(r1);	 Catch:{ Exception -> 0x0245 }
        r73 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0245 }
        r73.<init>();	 Catch:{ Exception -> 0x0245 }
        r28 = 0;
        r24 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0245 }
        r4 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x0245 }
        r13 = "UTF-8";
        r0 = r62;
        r4.<init>(r0, r13);	 Catch:{ Exception -> 0x0245 }
        r0 = r24;
        r0.<init>(r4);	 Catch:{ Exception -> 0x0245 }
    L_0x0111:
        r43 = r24.readLine();	 Catch:{ Exception -> 0x0245 }
        if (r43 == 0) goto L_0x0342;
    L_0x0117:
        r4 = "tmessages";
        r0 = r43;
        org.telegram.messenger.FileLog.m11e(r4, r0);	 Catch:{ Exception -> 0x0245 }
        r4 = ":";
        r0 = r43;
        r20 = r0.split(r4);	 Catch:{ Exception -> 0x0245 }
        r0 = r20;
        r4 = r0.length;	 Catch:{ Exception -> 0x0245 }
        r13 = 2;
        if (r4 != r13) goto L_0x0111;
    L_0x012c:
        r4 = 0;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "BEGIN";
        r4 = r4.equals(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x01a8;
    L_0x0137:
        r4 = 1;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "VCARD";
        r4 = r4.equals(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x01a8;
    L_0x0142:
        r28 = new org.telegram.ui.LaunchActivity$VcardData;	 Catch:{ Exception -> 0x0245 }
        r4 = 0;
        r0 = r28;
        r1 = r74;
        r0.<init>(r4);	 Catch:{ Exception -> 0x0245 }
        r0 = r73;
        r1 = r28;
        r0.add(r1);	 Catch:{ Exception -> 0x0245 }
    L_0x0153:
        if (r28 == 0) goto L_0x0111;
    L_0x0155:
        r4 = 0;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "FN";
        r4 = r4.startsWith(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 != 0) goto L_0x0175;
    L_0x0160:
        r4 = 0;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "ORG";
        r4 = r4.startsWith(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x031e;
    L_0x016b:
        r0 = r28;
        r4 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x031e;
    L_0x0175:
        r45 = 0;
        r44 = 0;
        r4 = 0;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = ";";
        r49 = r4.split(r13);	 Catch:{ Exception -> 0x0245 }
        r22 = r49;
        r0 = r22;
        r0 = r0.length;	 Catch:{ Exception -> 0x0245 }
        r42 = r0;
        r41 = 0;
    L_0x018b:
        r0 = r41;
        r1 = r42;
        if (r0 >= r1) goto L_0x01df;
    L_0x0191:
        r48 = r22[r41];	 Catch:{ Exception -> 0x0245 }
        r4 = "=";
        r0 = r48;
        r21 = r0.split(r4);	 Catch:{ Exception -> 0x0245 }
        r0 = r21;
        r4 = r0.length;	 Catch:{ Exception -> 0x0245 }
        r13 = 2;
        if (r4 == r13) goto L_0x01c1;
    L_0x01a1:
        r41 = r41 + 1;
        goto L_0x018b;
    L_0x01a4:
        r32 = 0;
        goto L_0x0057;
    L_0x01a8:
        r4 = 0;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "END";
        r4 = r4.equals(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x0153;
    L_0x01b3:
        r4 = 1;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "VCARD";
        r4 = r4.equals(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x0153;
    L_0x01be:
        r28 = 0;
        goto L_0x0153;
    L_0x01c1:
        r4 = 0;
        r4 = r21[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "CHARSET";
        r4 = r4.equals(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x01d0;
    L_0x01cc:
        r4 = 1;
        r44 = r21[r4];	 Catch:{ Exception -> 0x0245 }
        goto L_0x01a1;
    L_0x01d0:
        r4 = 0;
        r4 = r21[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "ENCODING";
        r4 = r4.equals(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x01a1;
    L_0x01db:
        r4 = 1;
        r45 = r21[r4];	 Catch:{ Exception -> 0x0245 }
        goto L_0x01a1;
    L_0x01df:
        r4 = 1;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r0 = r28;
        r0.name = r4;	 Catch:{ Exception -> 0x0245 }
        if (r45 == 0) goto L_0x0111;
    L_0x01e8:
        r4 = "QUOTED-PRINTABLE";
        r0 = r45;
        r4 = r0.equalsIgnoreCase(r4);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x0111;
    L_0x01f2:
        r0 = r28;
        r4 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r13 = "=";
        r4 = r4.endsWith(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x021d;
    L_0x01fe:
        if (r45 == 0) goto L_0x021d;
    L_0x0200:
        r0 = r28;
        r4 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r13 = 0;
        r0 = r28;
        r14 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r14 = r14.length();	 Catch:{ Exception -> 0x0245 }
        r14 = r14 + -1;
        r4 = r4.substring(r13, r14);	 Catch:{ Exception -> 0x0245 }
        r0 = r28;
        r0.name = r4;	 Catch:{ Exception -> 0x0245 }
        r43 = r24.readLine();	 Catch:{ Exception -> 0x0245 }
        if (r43 != 0) goto L_0x0301;
    L_0x021d:
        r0 = r28;
        r4 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r4 = r4.getBytes();	 Catch:{ Exception -> 0x0245 }
        r25 = org.telegram.messenger.AndroidUtilities.decodeQuotedPrintable(r4);	 Catch:{ Exception -> 0x0245 }
        if (r25 == 0) goto L_0x0111;
    L_0x022b:
        r0 = r25;
        r4 = r0.length;	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x0111;
    L_0x0230:
        r31 = new java.lang.String;	 Catch:{ Exception -> 0x0245 }
        r0 = r31;
        r1 = r25;
        r2 = r44;
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0245 }
        if (r31 == 0) goto L_0x0111;
    L_0x023d:
        r0 = r31;
        r1 = r28;
        r1.name = r0;	 Catch:{ Exception -> 0x0245 }
        goto L_0x0111;
    L_0x0245:
        r34 = move-exception;
        r4 = "tmessages";
        r0 = r34;
        org.telegram.messenger.FileLog.m13e(r4, r0);
        r36 = 1;
    L_0x024f:
        if (r36 == 0) goto L_0x025d;
    L_0x0251:
        r4 = "Unsupported content";
        r13 = 0;
        r0 = r74;
        r4 = android.widget.Toast.makeText(r0, r4, r13);
        r4.show();
    L_0x025d:
        r4 = r56.intValue();
        if (r4 == 0) goto L_0x09e8;
    L_0x0263:
        r20 = new android.os.Bundle;
        r20.<init>();
        r4 = "user_id";
        r13 = r56.intValue();
        r0 = r20;
        r0.putInt(r4, r13);
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0293;
    L_0x027b:
        r4 = mainFragmentsStack;
        r13 = mainFragmentsStack;
        r13 = r13.size();
        r13 = r13 + -1;
        r4 = r4.get(r13);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r20;
        r4 = org.telegram.messenger.MessagesController.checkCanOpenChat(r0, r4);
        if (r4 == 0) goto L_0x02ad;
    L_0x0293:
        r38 = new org.telegram.ui.ChatActivity;
        r0 = r38;
        r1 = r20;
        r0.<init>(r1);
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = 0;
        r14 = 1;
        r15 = 1;
        r0 = r38;
        r4 = r4.presentFragment(r0, r13, r14, r15);
        if (r4 == 0) goto L_0x02ad;
    L_0x02ab:
        r53 = 1;
    L_0x02ad:
        if (r53 != 0) goto L_0x02f9;
    L_0x02af:
        if (r76 != 0) goto L_0x02f9;
    L_0x02b1:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0d22;
    L_0x02b7:
        r4 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r4 != 0) goto L_0x0cfb;
    L_0x02bd:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x02de;
    L_0x02c9:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r13 = new org.telegram.ui.LoginActivity;
        r13.<init>();
        r4.addFragmentToStack(r13);
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 0;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
    L_0x02de:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x02f9;
    L_0x02eb:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4.showLastFragment();
        r0 = r74;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
    L_0x02f9:
        r4 = 0;
        r0 = r75;
        r0.setAction(r4);
        goto L_0x002c;
    L_0x0301:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0245 }
        r4.<init>();	 Catch:{ Exception -> 0x0245 }
        r0 = r28;
        r13 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r4 = r4.append(r13);	 Catch:{ Exception -> 0x0245 }
        r0 = r43;
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x0245 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0245 }
        r0 = r28;
        r0.name = r4;	 Catch:{ Exception -> 0x0245 }
        goto L_0x01f2;
    L_0x031e:
        r4 = 0;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = "TEL";
        r4 = r4.startsWith(r13);	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x0111;
    L_0x0329:
        r4 = 1;
        r4 = r20[r4];	 Catch:{ Exception -> 0x0245 }
        r13 = 1;
        r52 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4, r13);	 Catch:{ Exception -> 0x0245 }
        r4 = r52.length();	 Catch:{ Exception -> 0x0245 }
        if (r4 <= 0) goto L_0x0111;
    L_0x0337:
        r0 = r28;
        r4 = r0.phones;	 Catch:{ Exception -> 0x0245 }
        r0 = r52;
        r4.add(r0);	 Catch:{ Exception -> 0x0245 }
        goto L_0x0111;
    L_0x0342:
        r24.close();	 Catch:{ Exception -> 0x03bf }
        r62.close();	 Catch:{ Exception -> 0x03bf }
    L_0x0348:
        r19 = 0;
    L_0x034a:
        r4 = r73.size();	 Catch:{ Exception -> 0x0245 }
        r0 = r19;
        if (r0 >= r4) goto L_0x024f;
    L_0x0352:
        r0 = r73;
        r1 = r19;
        r72 = r0.get(r1);	 Catch:{ Exception -> 0x0245 }
        r72 = (org.telegram.ui.LaunchActivity.VcardData) r72;	 Catch:{ Exception -> 0x0245 }
        r0 = r72;
        r4 = r0.name;	 Catch:{ Exception -> 0x0245 }
        if (r4 == 0) goto L_0x03c8;
    L_0x0362:
        r0 = r72;
        r4 = r0.phones;	 Catch:{ Exception -> 0x0245 }
        r4 = r4.isEmpty();	 Catch:{ Exception -> 0x0245 }
        if (r4 != 0) goto L_0x03c8;
    L_0x036c:
        r0 = r74;
        r4 = r0.contactsToSend;	 Catch:{ Exception -> 0x0245 }
        if (r4 != 0) goto L_0x037b;
    L_0x0372:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0245 }
        r4.<init>();	 Catch:{ Exception -> 0x0245 }
        r0 = r74;
        r0.contactsToSend = r4;	 Catch:{ Exception -> 0x0245 }
    L_0x037b:
        r23 = 0;
    L_0x037d:
        r0 = r72;
        r4 = r0.phones;	 Catch:{ Exception -> 0x0245 }
        r4 = r4.size();	 Catch:{ Exception -> 0x0245 }
        r0 = r23;
        if (r0 >= r4) goto L_0x03c8;
    L_0x0389:
        r0 = r72;
        r4 = r0.phones;	 Catch:{ Exception -> 0x0245 }
        r0 = r23;
        r52 = r4.get(r0);	 Catch:{ Exception -> 0x0245 }
        r52 = (java.lang.String) r52;	 Catch:{ Exception -> 0x0245 }
        r70 = new org.telegram.tgnet.TLRPC$TL_userContact_old2;	 Catch:{ Exception -> 0x0245 }
        r70.<init>();	 Catch:{ Exception -> 0x0245 }
        r0 = r52;
        r1 = r70;
        r1.phone = r0;	 Catch:{ Exception -> 0x0245 }
        r0 = r72;
        r4 = r0.name;	 Catch:{ Exception -> 0x0245 }
        r0 = r70;
        r0.first_name = r4;	 Catch:{ Exception -> 0x0245 }
        r4 = "";
        r0 = r70;
        r0.last_name = r4;	 Catch:{ Exception -> 0x0245 }
        r4 = 0;
        r0 = r70;
        r0.id = r4;	 Catch:{ Exception -> 0x0245 }
        r0 = r74;
        r4 = r0.contactsToSend;	 Catch:{ Exception -> 0x0245 }
        r0 = r70;
        r4.add(r0);	 Catch:{ Exception -> 0x0245 }
        r23 = r23 + 1;
        goto L_0x037d;
    L_0x03bf:
        r34 = move-exception;
        r4 = "tmessages";
        r0 = r34;
        org.telegram.messenger.FileLog.m13e(r4, r0);	 Catch:{ Exception -> 0x0245 }
        goto L_0x0348;
    L_0x03c8:
        r19 = r19 + 1;
        goto L_0x034a;
    L_0x03cb:
        r36 = 1;
        goto L_0x024f;
    L_0x03cf:
        r4 = "android.intent.extra.TEXT";
        r0 = r75;
        r64 = r0.getStringExtra(r4);
        if (r64 != 0) goto L_0x03e7;
    L_0x03d9:
        r4 = "android.intent.extra.TEXT";
        r0 = r75;
        r65 = r0.getCharSequenceExtra(r4);
        if (r65 == 0) goto L_0x03e7;
    L_0x03e3:
        r64 = r65.toString();
    L_0x03e7:
        r4 = "android.intent.extra.SUBJECT";
        r0 = r75;
        r63 = r0.getStringExtra(r4);
        if (r64 == 0) goto L_0x0494;
    L_0x03f1:
        r4 = r64.length();
        if (r4 == 0) goto L_0x0494;
    L_0x03f7:
        r4 = "http://";
        r0 = r64;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x040b;
    L_0x0401:
        r4 = "https://";
        r0 = r64;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x042e;
    L_0x040b:
        if (r63 == 0) goto L_0x042e;
    L_0x040d:
        r4 = r63.length();
        if (r4 == 0) goto L_0x042e;
    L_0x0413:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r63;
        r4 = r4.append(r0);
        r13 = "\n";
        r4 = r4.append(r13);
        r0 = r64;
        r4 = r4.append(r0);
        r64 = r4.toString();
    L_0x042e:
        r0 = r64;
        r1 = r74;
        r1.sendingText = r0;
    L_0x0434:
        r4 = "android.intent.extra.STREAM";
        r0 = r75;
        r50 = r0.getParcelableExtra(r4);
        if (r50 == 0) goto L_0x051f;
    L_0x043e:
        r0 = r50;
        r4 = r0 instanceof android.net.Uri;
        if (r4 != 0) goto L_0x044c;
    L_0x0444:
        r4 = r50.toString();
        r50 = android.net.Uri.parse(r4);
    L_0x044c:
        r67 = r50;
        r67 = (android.net.Uri) r67;
        if (r67 == 0) goto L_0x045a;
    L_0x0452:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r67);
        if (r4 == 0) goto L_0x045a;
    L_0x0458:
        r36 = 1;
    L_0x045a:
        if (r36 != 0) goto L_0x024f;
    L_0x045c:
        if (r67 == 0) goto L_0x04a3;
    L_0x045e:
        if (r66 == 0) goto L_0x046a;
    L_0x0460:
        r4 = "image/";
        r0 = r66;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x047a;
    L_0x046a:
        r4 = r67.toString();
        r4 = r4.toLowerCase();
        r13 = ".jpg";
        r4 = r4.endsWith(r13);
        if (r4 == 0) goto L_0x04a3;
    L_0x047a:
        r0 = r74;
        r4 = r0.photoPathsArray;
        if (r4 != 0) goto L_0x0489;
    L_0x0480:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r74;
        r0.photoPathsArray = r4;
    L_0x0489:
        r0 = r74;
        r4 = r0.photoPathsArray;
        r0 = r67;
        r4.add(r0);
        goto L_0x024f;
    L_0x0494:
        if (r63 == 0) goto L_0x0434;
    L_0x0496:
        r4 = r63.length();
        if (r4 <= 0) goto L_0x0434;
    L_0x049c:
        r0 = r63;
        r1 = r74;
        r1.sendingText = r0;
        goto L_0x0434;
    L_0x04a3:
        r51 = org.telegram.messenger.AndroidUtilities.getPath(r67);
        if (r51 == 0) goto L_0x04ff;
    L_0x04a9:
        r4 = "file:";
        r0 = r51;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x04bd;
    L_0x04b3:
        r4 = "file://";
        r13 = "";
        r0 = r51;
        r51 = r0.replace(r4, r13);
    L_0x04bd:
        if (r66 == 0) goto L_0x04d1;
    L_0x04bf:
        r4 = "video/";
        r0 = r66;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x04d1;
    L_0x04c9:
        r0 = r51;
        r1 = r74;
        r1.videoPath = r0;
        goto L_0x024f;
    L_0x04d1:
        r0 = r74;
        r4 = r0.documentsPathsArray;
        if (r4 != 0) goto L_0x04e9;
    L_0x04d7:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r74;
        r0.documentsPathsArray = r4;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r74;
        r0.documentsOriginalPathsArray = r4;
    L_0x04e9:
        r0 = r74;
        r4 = r0.documentsPathsArray;
        r0 = r51;
        r4.add(r0);
        r0 = r74;
        r4 = r0.documentsOriginalPathsArray;
        r13 = r67.toString();
        r4.add(r13);
        goto L_0x024f;
    L_0x04ff:
        r0 = r74;
        r4 = r0.documentsUrisArray;
        if (r4 != 0) goto L_0x050e;
    L_0x0505:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0 = r74;
        r0.documentsUrisArray = r4;
    L_0x050e:
        r0 = r74;
        r4 = r0.documentsUrisArray;
        r0 = r67;
        r4.add(r0);
        r0 = r66;
        r1 = r74;
        r1.documentsMimeType = r0;
        goto L_0x024f;
    L_0x051f:
        r0 = r74;
        r4 = r0.sendingText;
        if (r4 != 0) goto L_0x024f;
    L_0x0525:
        r36 = 1;
        goto L_0x024f;
    L_0x0529:
        r4 = r75.getAction();
        r13 = "android.intent.action.SEND_MULTIPLE";
        r4 = r4.equals(r13);
        if (r4 == 0) goto L_0x066d;
    L_0x0535:
        r36 = 0;
        r4 = "android.intent.extra.STREAM";
        r0 = r75;
        r68 = r0.getParcelableArrayListExtra(r4);	 Catch:{ Exception -> 0x0662 }
        r66 = r75.getType();	 Catch:{ Exception -> 0x0662 }
        if (r68 == 0) goto L_0x0589;
    L_0x0545:
        r19 = 0;
    L_0x0547:
        r4 = r68.size();	 Catch:{ Exception -> 0x0662 }
        r0 = r19;
        if (r0 >= r4) goto L_0x0581;
    L_0x054f:
        r0 = r68;
        r1 = r19;
        r50 = r0.get(r1);	 Catch:{ Exception -> 0x0662 }
        r50 = (android.os.Parcelable) r50;	 Catch:{ Exception -> 0x0662 }
        r0 = r50;
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0662 }
        if (r4 != 0) goto L_0x0567;
    L_0x055f:
        r4 = r50.toString();	 Catch:{ Exception -> 0x0662 }
        r50 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0662 }
    L_0x0567:
        r0 = r50;
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0662 }
        r67 = r0;
        if (r67 == 0) goto L_0x057e;
    L_0x056f:
        r4 = org.telegram.messenger.AndroidUtilities.isInternalUri(r67);	 Catch:{ Exception -> 0x0662 }
        if (r4 == 0) goto L_0x057e;
    L_0x0575:
        r0 = r68;
        r1 = r19;
        r0.remove(r1);	 Catch:{ Exception -> 0x0662 }
        r19 = r19 + -1;
    L_0x057e:
        r19 = r19 + 1;
        goto L_0x0547;
    L_0x0581:
        r4 = r68.isEmpty();	 Catch:{ Exception -> 0x0662 }
        if (r4 == 0) goto L_0x0589;
    L_0x0587:
        r68 = 0;
    L_0x0589:
        if (r68 == 0) goto L_0x0650;
    L_0x058b:
        if (r66 == 0) goto L_0x05da;
    L_0x058d:
        r4 = "image/";
        r0 = r66;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0662 }
        if (r4 == 0) goto L_0x05da;
    L_0x0597:
        r19 = 0;
    L_0x0599:
        r4 = r68.size();	 Catch:{ Exception -> 0x0662 }
        r0 = r19;
        if (r0 >= r4) goto L_0x0652;
    L_0x05a1:
        r0 = r68;
        r1 = r19;
        r50 = r0.get(r1);	 Catch:{ Exception -> 0x0662 }
        r50 = (android.os.Parcelable) r50;	 Catch:{ Exception -> 0x0662 }
        r0 = r50;
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0662 }
        if (r4 != 0) goto L_0x05b9;
    L_0x05b1:
        r4 = r50.toString();	 Catch:{ Exception -> 0x0662 }
        r50 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0662 }
    L_0x05b9:
        r0 = r50;
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0662 }
        r67 = r0;
        r0 = r74;
        r4 = r0.photoPathsArray;	 Catch:{ Exception -> 0x0662 }
        if (r4 != 0) goto L_0x05ce;
    L_0x05c5:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0662 }
        r4.<init>();	 Catch:{ Exception -> 0x0662 }
        r0 = r74;
        r0.photoPathsArray = r4;	 Catch:{ Exception -> 0x0662 }
    L_0x05ce:
        r0 = r74;
        r4 = r0.photoPathsArray;	 Catch:{ Exception -> 0x0662 }
        r0 = r67;
        r4.add(r0);	 Catch:{ Exception -> 0x0662 }
        r19 = r19 + 1;
        goto L_0x0599;
    L_0x05da:
        r19 = 0;
    L_0x05dc:
        r4 = r68.size();	 Catch:{ Exception -> 0x0662 }
        r0 = r19;
        if (r0 >= r4) goto L_0x0652;
    L_0x05e4:
        r0 = r68;
        r1 = r19;
        r50 = r0.get(r1);	 Catch:{ Exception -> 0x0662 }
        r50 = (android.os.Parcelable) r50;	 Catch:{ Exception -> 0x0662 }
        r0 = r50;
        r4 = r0 instanceof android.net.Uri;	 Catch:{ Exception -> 0x0662 }
        if (r4 != 0) goto L_0x05fc;
    L_0x05f4:
        r4 = r50.toString();	 Catch:{ Exception -> 0x0662 }
        r50 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x0662 }
    L_0x05fc:
        r0 = r50;
        r0 = (android.net.Uri) r0;	 Catch:{ Exception -> 0x0662 }
        r4 = r0;
        r51 = org.telegram.messenger.AndroidUtilities.getPath(r4);	 Catch:{ Exception -> 0x0662 }
        r47 = r50.toString();	 Catch:{ Exception -> 0x0662 }
        if (r47 != 0) goto L_0x060d;
    L_0x060b:
        r47 = r51;
    L_0x060d:
        if (r51 == 0) goto L_0x064d;
    L_0x060f:
        r4 = "file:";
        r0 = r51;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0662 }
        if (r4 == 0) goto L_0x0623;
    L_0x0619:
        r4 = "file://";
        r13 = "";
        r0 = r51;
        r51 = r0.replace(r4, r13);	 Catch:{ Exception -> 0x0662 }
    L_0x0623:
        r0 = r74;
        r4 = r0.documentsPathsArray;	 Catch:{ Exception -> 0x0662 }
        if (r4 != 0) goto L_0x063b;
    L_0x0629:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0662 }
        r4.<init>();	 Catch:{ Exception -> 0x0662 }
        r0 = r74;
        r0.documentsPathsArray = r4;	 Catch:{ Exception -> 0x0662 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0662 }
        r4.<init>();	 Catch:{ Exception -> 0x0662 }
        r0 = r74;
        r0.documentsOriginalPathsArray = r4;	 Catch:{ Exception -> 0x0662 }
    L_0x063b:
        r0 = r74;
        r4 = r0.documentsPathsArray;	 Catch:{ Exception -> 0x0662 }
        r0 = r51;
        r4.add(r0);	 Catch:{ Exception -> 0x0662 }
        r0 = r74;
        r4 = r0.documentsOriginalPathsArray;	 Catch:{ Exception -> 0x0662 }
        r0 = r47;
        r4.add(r0);	 Catch:{ Exception -> 0x0662 }
    L_0x064d:
        r19 = r19 + 1;
        goto L_0x05dc;
    L_0x0650:
        r36 = 1;
    L_0x0652:
        if (r36 == 0) goto L_0x025d;
    L_0x0654:
        r4 = "Unsupported content";
        r13 = 0;
        r0 = r74;
        r4 = android.widget.Toast.makeText(r0, r4, r13);
        r4.show();
        goto L_0x025d;
    L_0x0662:
        r34 = move-exception;
        r4 = "tmessages";
        r0 = r34;
        org.telegram.messenger.FileLog.m13e(r4, r0);
        r36 = 1;
        goto L_0x0652;
    L_0x066d:
        r4 = "android.intent.action.VIEW";
        r13 = r75.getAction();
        r4 = r4.equals(r13);
        if (r4 == 0) goto L_0x095e;
    L_0x0679:
        r30 = r75.getData();
        if (r30 == 0) goto L_0x025d;
    L_0x067f:
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r11 = 0;
        r58 = r30.getScheme();
        if (r58 == 0) goto L_0x06e5;
    L_0x068d:
        r4 = "http";
        r0 = r58;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x06a1;
    L_0x0697:
        r4 = "https";
        r0 = r58;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x07b0;
    L_0x06a1:
        r4 = r30.getHost();
        r40 = r4.toLowerCase();
        r4 = "telegram.me";
        r0 = r40;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x06bd;
    L_0x06b3:
        r4 = "telegram.dog";
        r0 = r40;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x06e5;
    L_0x06bd:
        r51 = r30.getPath();
        if (r51 == 0) goto L_0x06e5;
    L_0x06c3:
        r4 = r51.length();
        r13 = 1;
        if (r4 <= r13) goto L_0x06e5;
    L_0x06ca:
        r4 = 1;
        r0 = r51;
        r51 = r0.substring(r4);
        r4 = "joinchat/";
        r0 = r51;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x06f5;
    L_0x06db:
        r4 = "joinchat/";
        r13 = "";
        r0 = r51;
        r6 = r0.replace(r4, r13);
    L_0x06e5:
        if (r5 != 0) goto L_0x06ed;
    L_0x06e7:
        if (r6 != 0) goto L_0x06ed;
    L_0x06e9:
        if (r7 != 0) goto L_0x06ed;
    L_0x06eb:
        if (r10 == 0) goto L_0x0916;
    L_0x06ed:
        r13 = 0;
        r4 = r74;
        r4.runLinkRequest(r5, r6, r7, r8, r9, r10, r11, r12, r13);
        goto L_0x025d;
    L_0x06f5:
        r4 = "addstickers/";
        r0 = r51;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x070a;
    L_0x06ff:
        r4 = "addstickers/";
        r13 = "";
        r0 = r51;
        r7 = r0.replace(r4, r13);
        goto L_0x06e5;
    L_0x070a:
        r4 = "msg/";
        r0 = r51;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x071e;
    L_0x0714:
        r4 = "share/";
        r0 = r51;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0769;
    L_0x071e:
        r4 = "url";
        r0 = r30;
        r10 = r0.getQueryParameter(r4);
        if (r10 != 0) goto L_0x072a;
    L_0x0728:
        r10 = "";
    L_0x072a:
        r4 = "text";
        r0 = r30;
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x06e5;
    L_0x0734:
        r4 = r10.length();
        if (r4 <= 0) goto L_0x074e;
    L_0x073a:
        r11 = 1;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r10);
        r13 = "\n";
        r4 = r4.append(r13);
        r10 = r4.toString();
    L_0x074e:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r10);
        r13 = "text";
        r0 = r30;
        r13 = r0.getQueryParameter(r13);
        r4 = r4.append(r13);
        r10 = r4.toString();
        goto L_0x06e5;
    L_0x0769:
        r4 = r51.length();
        r13 = 1;
        if (r4 < r13) goto L_0x06e5;
    L_0x0770:
        r59 = r30.getPathSegments();
        r4 = r59.size();
        if (r4 <= 0) goto L_0x079e;
    L_0x077a:
        r4 = 0;
        r0 = r59;
        r5 = r0.get(r4);
        r5 = (java.lang.String) r5;
        r4 = r59.size();
        r13 = 1;
        if (r4 <= r13) goto L_0x079e;
    L_0x078a:
        r4 = 1;
        r0 = r59;
        r4 = r0.get(r4);
        r4 = (java.lang.String) r4;
        r12 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r12.intValue();
        if (r4 != 0) goto L_0x079e;
    L_0x079d:
        r12 = 0;
    L_0x079e:
        r4 = "start";
        r0 = r30;
        r8 = r0.getQueryParameter(r4);
        r4 = "startgroup";
        r0 = r30;
        r9 = r0.getQueryParameter(r4);
        goto L_0x06e5;
    L_0x07b0:
        r4 = "tg";
        r0 = r58;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x06e5;
    L_0x07ba:
        r69 = r30.toString();
        r4 = "tg:resolve";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x07d2;
    L_0x07c8:
        r4 = "tg://resolve";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0815;
    L_0x07d2:
        r4 = "tg:resolve";
        r13 = "tg://telegram.org";
        r0 = r69;
        r4 = r0.replace(r4, r13);
        r13 = "tg://resolve";
        r14 = "tg://telegram.org";
        r69 = r4.replace(r13, r14);
        r30 = android.net.Uri.parse(r69);
        r4 = "domain";
        r0 = r30;
        r5 = r0.getQueryParameter(r4);
        r4 = "start";
        r0 = r30;
        r8 = r0.getQueryParameter(r4);
        r4 = "startgroup";
        r0 = r30;
        r9 = r0.getQueryParameter(r4);
        r4 = "post";
        r0 = r30;
        r4 = r0.getQueryParameter(r4);
        r12 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r12.intValue();
        if (r4 != 0) goto L_0x06e5;
    L_0x0812:
        r12 = 0;
        goto L_0x06e5;
    L_0x0815:
        r4 = "tg:join";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x0829;
    L_0x081f:
        r4 = "tg://join";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x0849;
    L_0x0829:
        r4 = "tg:join";
        r13 = "tg://telegram.org";
        r0 = r69;
        r4 = r0.replace(r4, r13);
        r13 = "tg://join";
        r14 = "tg://telegram.org";
        r69 = r4.replace(r13, r14);
        r30 = android.net.Uri.parse(r69);
        r4 = "invite";
        r0 = r30;
        r6 = r0.getQueryParameter(r4);
        goto L_0x06e5;
    L_0x0849:
        r4 = "tg:addstickers";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x085d;
    L_0x0853:
        r4 = "tg://addstickers";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x087d;
    L_0x085d:
        r4 = "tg:addstickers";
        r13 = "tg://telegram.org";
        r0 = r69;
        r4 = r0.replace(r4, r13);
        r13 = "tg://addstickers";
        r14 = "tg://telegram.org";
        r69 = r4.replace(r13, r14);
        r30 = android.net.Uri.parse(r69);
        r4 = "set";
        r0 = r30;
        r7 = r0.getQueryParameter(r4);
        goto L_0x06e5;
    L_0x087d:
        r4 = "tg:msg";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x08a5;
    L_0x0887:
        r4 = "tg://msg";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x08a5;
    L_0x0891:
        r4 = "tg://share";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x08a5;
    L_0x089b:
        r4 = "tg:share";
        r0 = r69;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x06e5;
    L_0x08a5:
        r4 = "tg:msg";
        r13 = "tg://telegram.org";
        r0 = r69;
        r4 = r0.replace(r4, r13);
        r13 = "tg://msg";
        r14 = "tg://telegram.org";
        r4 = r4.replace(r13, r14);
        r13 = "tg://share";
        r14 = "tg://telegram.org";
        r4 = r4.replace(r13, r14);
        r13 = "tg:share";
        r14 = "tg://telegram.org";
        r69 = r4.replace(r13, r14);
        r30 = android.net.Uri.parse(r69);
        r4 = "url";
        r0 = r30;
        r10 = r0.getQueryParameter(r4);
        if (r10 != 0) goto L_0x08d7;
    L_0x08d5:
        r10 = "";
    L_0x08d7:
        r4 = "text";
        r0 = r30;
        r4 = r0.getQueryParameter(r4);
        if (r4 == 0) goto L_0x06e5;
    L_0x08e1:
        r4 = r10.length();
        if (r4 <= 0) goto L_0x08fb;
    L_0x08e7:
        r11 = 1;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r10);
        r13 = "\n";
        r4 = r4.append(r13);
        r10 = r4.toString();
    L_0x08fb:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r10);
        r13 = "text";
        r0 = r30;
        r13 = r0.getQueryParameter(r13);
        r4 = r4.append(r13);
        r10 = r4.toString();
        goto L_0x06e5;
    L_0x0916:
        r13 = r74.getContentResolver();	 Catch:{ Exception -> 0x0954 }
        r14 = r75.getData();	 Catch:{ Exception -> 0x0954 }
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r29 = r13.query(r14, r15, r16, r17, r18);	 Catch:{ Exception -> 0x0954 }
        if (r29 == 0) goto L_0x025d;
    L_0x092b:
        r4 = r29.moveToFirst();	 Catch:{ Exception -> 0x0954 }
        if (r4 == 0) goto L_0x094f;
    L_0x0931:
        r4 = "DATA4";
        r0 = r29;
        r4 = r0.getColumnIndex(r4);	 Catch:{ Exception -> 0x0954 }
        r0 = r29;
        r71 = r0.getInt(r4);	 Catch:{ Exception -> 0x0954 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance();	 Catch:{ Exception -> 0x0954 }
        r13 = org.telegram.messenger.NotificationCenter.closeChats;	 Catch:{ Exception -> 0x0954 }
        r14 = 0;
        r14 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0954 }
        r4.postNotificationName(r13, r14);	 Catch:{ Exception -> 0x0954 }
        r56 = java.lang.Integer.valueOf(r71);	 Catch:{ Exception -> 0x0954 }
    L_0x094f:
        r29.close();	 Catch:{ Exception -> 0x0954 }
        goto L_0x025d;
    L_0x0954:
        r34 = move-exception;
        r4 = "tmessages";
        r0 = r34;
        org.telegram.messenger.FileLog.m13e(r4, r0);
        goto L_0x025d;
    L_0x095e:
        r4 = r75.getAction();
        r13 = "org.telegram.messenger.OPEN_ACCOUNT";
        r4 = r4.equals(r13);
        if (r4 == 0) goto L_0x0971;
    L_0x096a:
        r4 = 1;
        r46 = java.lang.Integer.valueOf(r4);
        goto L_0x025d;
    L_0x0971:
        r4 = r75.getAction();
        r13 = "com.tmessages.openchat";
        r4 = r4.startsWith(r13);
        if (r4 == 0) goto L_0x09d8;
    L_0x097d:
        r4 = "chatId";
        r13 = 0;
        r0 = r75;
        r26 = r0.getIntExtra(r4, r13);
        r4 = "userId";
        r13 = 0;
        r0 = r75;
        r71 = r0.getIntExtra(r4, r13);
        r4 = "encId";
        r13 = 0;
        r0 = r75;
        r35 = r0.getIntExtra(r4, r13);
        if (r26 == 0) goto L_0x09ac;
    L_0x099a:
        r4 = org.telegram.messenger.NotificationCenter.getInstance();
        r13 = org.telegram.messenger.NotificationCenter.closeChats;
        r14 = 0;
        r14 = new java.lang.Object[r14];
        r4.postNotificationName(r13, r14);
        r54 = java.lang.Integer.valueOf(r26);
        goto L_0x025d;
    L_0x09ac:
        if (r71 == 0) goto L_0x09c0;
    L_0x09ae:
        r4 = org.telegram.messenger.NotificationCenter.getInstance();
        r13 = org.telegram.messenger.NotificationCenter.closeChats;
        r14 = 0;
        r14 = new java.lang.Object[r14];
        r4.postNotificationName(r13, r14);
        r56 = java.lang.Integer.valueOf(r71);
        goto L_0x025d;
    L_0x09c0:
        if (r35 == 0) goto L_0x09d4;
    L_0x09c2:
        r4 = org.telegram.messenger.NotificationCenter.getInstance();
        r13 = org.telegram.messenger.NotificationCenter.closeChats;
        r14 = 0;
        r14 = new java.lang.Object[r14];
        r4.postNotificationName(r13, r14);
        r55 = java.lang.Integer.valueOf(r35);
        goto L_0x025d;
    L_0x09d4:
        r60 = 1;
        goto L_0x025d;
    L_0x09d8:
        r4 = r75.getAction();
        r13 = "com.tmessages.openplayer";
        r4 = r4.equals(r13);
        if (r4 == 0) goto L_0x025d;
    L_0x09e4:
        r61 = 1;
        goto L_0x025d;
    L_0x09e8:
        r4 = r54.intValue();
        if (r4 == 0) goto L_0x0a3a;
    L_0x09ee:
        r20 = new android.os.Bundle;
        r20.<init>();
        r4 = "chat_id";
        r13 = r54.intValue();
        r0 = r20;
        r0.putInt(r4, r13);
        r4 = mainFragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0a1e;
    L_0x0a06:
        r4 = mainFragmentsStack;
        r13 = mainFragmentsStack;
        r13 = r13.size();
        r13 = r13 + -1;
        r4 = r4.get(r13);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r0 = r20;
        r4 = org.telegram.messenger.MessagesController.checkCanOpenChat(r0, r4);
        if (r4 == 0) goto L_0x02ad;
    L_0x0a1e:
        r38 = new org.telegram.ui.ChatActivity;
        r0 = r38;
        r1 = r20;
        r0.<init>(r1);
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = 0;
        r14 = 1;
        r15 = 1;
        r0 = r38;
        r4 = r4.presentFragment(r0, r13, r14, r15);
        if (r4 == 0) goto L_0x02ad;
    L_0x0a36:
        r53 = 1;
        goto L_0x02ad;
    L_0x0a3a:
        r4 = r55.intValue();
        if (r4 == 0) goto L_0x0a6c;
    L_0x0a40:
        r20 = new android.os.Bundle;
        r20.<init>();
        r4 = "enc_id";
        r13 = r55.intValue();
        r0 = r20;
        r0.putInt(r4, r13);
        r38 = new org.telegram.ui.ChatActivity;
        r0 = r38;
        r1 = r20;
        r0.<init>(r1);
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = 0;
        r14 = 1;
        r15 = 1;
        r0 = r38;
        r4 = r4.presentFragment(r0, r13, r14, r15);
        if (r4 == 0) goto L_0x02ad;
    L_0x0a68:
        r53 = 1;
        goto L_0x02ad;
    L_0x0a6c:
        if (r60 == 0) goto L_0x0abf;
    L_0x0a6e:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 != 0) goto L_0x0a81;
    L_0x0a74:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4.removeAllFragments();
    L_0x0a7b:
        r53 = 0;
        r76 = 0;
        goto L_0x02ad;
    L_0x0a81:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0a7b;
    L_0x0a8d:
        r19 = 0;
    L_0x0a8f:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r4 = r4 + -1;
        if (r4 <= 0) goto L_0x0ab6;
    L_0x0a9d:
        r0 = r74;
        r13 = r0.layersActionBarLayout;
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r14 = 0;
        r4 = r4.get(r14);
        r4 = (org.telegram.ui.ActionBar.BaseFragment) r4;
        r13.removeFragmentFromStack(r4);
        r19 = r19 + -1;
        r19 = r19 + 1;
        goto L_0x0a8f;
    L_0x0ab6:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r13 = 0;
        r4.closeLastFragment(r13);
        goto L_0x0a7b;
    L_0x0abf:
        if (r61 == 0) goto L_0x0b5e;
    L_0x0ac1:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0b24;
    L_0x0ac7:
        r19 = 0;
    L_0x0ac9:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r0 = r19;
        if (r0 >= r4) goto L_0x0af4;
    L_0x0ad7:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r19;
        r38 = r4.get(r0);
        r38 = (org.telegram.ui.ActionBar.BaseFragment) r38;
        r0 = r38;
        r4 = r0 instanceof org.telegram.ui.AudioPlayerActivity;
        if (r4 == 0) goto L_0x0b21;
    L_0x0aeb:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r0 = r38;
        r4.removeFragmentFromStack(r0);
    L_0x0af4:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r74;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 0;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
    L_0x0b0b:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = new org.telegram.ui.AudioPlayerActivity;
        r13.<init>();
        r14 = 0;
        r15 = 1;
        r16 = 1;
        r0 = r16;
        r4.presentFragment(r13, r14, r15, r0);
        r53 = 1;
        goto L_0x02ad;
    L_0x0b21:
        r19 = r19 + 1;
        goto L_0x0ac9;
    L_0x0b24:
        r19 = 0;
    L_0x0b26:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r0 = r19;
        if (r0 >= r4) goto L_0x0b51;
    L_0x0b34:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r19;
        r38 = r4.get(r0);
        r38 = (org.telegram.ui.ActionBar.BaseFragment) r38;
        r0 = r38;
        r4 = r0 instanceof org.telegram.ui.AudioPlayerActivity;
        if (r4 == 0) goto L_0x0b5b;
    L_0x0b48:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r0 = r38;
        r4.removeFragmentFromStack(r0);
    L_0x0b51:
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 1;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        goto L_0x0b0b;
    L_0x0b5b:
        r19 = r19 + 1;
        goto L_0x0b26;
    L_0x0b5e:
        r0 = r74;
        r4 = r0.videoPath;
        if (r4 != 0) goto L_0x0b82;
    L_0x0b64:
        r0 = r74;
        r4 = r0.photoPathsArray;
        if (r4 != 0) goto L_0x0b82;
    L_0x0b6a:
        r0 = r74;
        r4 = r0.sendingText;
        if (r4 != 0) goto L_0x0b82;
    L_0x0b70:
        r0 = r74;
        r4 = r0.documentsPathsArray;
        if (r4 != 0) goto L_0x0b82;
    L_0x0b76:
        r0 = r74;
        r4 = r0.contactsToSend;
        if (r4 != 0) goto L_0x0b82;
    L_0x0b7c:
        r0 = r74;
        r4 = r0.documentsUrisArray;
        if (r4 == 0) goto L_0x0cb8;
    L_0x0b82:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 != 0) goto L_0x0b94;
    L_0x0b88:
        r4 = org.telegram.messenger.NotificationCenter.getInstance();
        r13 = org.telegram.messenger.NotificationCenter.closeChats;
        r14 = 0;
        r14 = new java.lang.Object[r14];
        r4.postNotificationName(r13, r14);
    L_0x0b94:
        r14 = 0;
        r4 = (r32 > r14 ? 1 : (r32 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0cad;
    L_0x0b9a:
        r20 = new android.os.Bundle;
        r20.<init>();
        r4 = "onlySelect";
        r13 = 1;
        r0 = r20;
        r0.putBoolean(r4, r13);
        r0 = r74;
        r4 = r0.contactsToSend;
        if (r4 == 0) goto L_0x0c4f;
    L_0x0bad:
        r4 = "selectAlertString";
        r13 = "SendContactTo";
        r14 = 2131166161; // 0x7f0703d1 float:1.794656E38 double:1.0529359857E-314;
        r13 = org.telegram.messenger.LocaleController.getString(r13, r14);
        r0 = r20;
        r0.putString(r4, r13);
        r4 = "selectAlertStringGroup";
        r13 = "SendContactToGroup";
        r14 = 2131166157; // 0x7f0703cd float:1.7946551E38 double:1.052935984E-314;
        r13 = org.telegram.messenger.LocaleController.getString(r13, r14);
        r0 = r20;
        r0.putString(r4, r13);
    L_0x0bcd:
        r38 = new org.telegram.ui.DialogsActivity;
        r0 = r38;
        r1 = r20;
        r0.<init>(r1);
        r0 = r38;
        r1 = r74;
        r0.setDelegate(r1);
        r74.loadBanner();
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0c74;
    L_0x0be6:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x0c71;
    L_0x0bf2:
        r0 = r74;
        r4 = r0.layersActionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r74;
        r13 = r0.layersActionBarLayout;
        r13 = r13.fragmentsStack;
        r13 = r13.size();
        r13 = r13 + -1;
        r4 = r4.get(r13);
        r4 = r4 instanceof org.telegram.ui.DialogsActivity;
        if (r4 == 0) goto L_0x0c71;
    L_0x0c0c:
        r57 = 1;
    L_0x0c0e:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = 1;
        r14 = 1;
        r0 = r38;
        r1 = r57;
        r4.presentFragment(r0, r1, r13, r14);
        r53 = 1;
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r4 = r4.isVisible();
        if (r4 == 0) goto L_0x0c30;
    L_0x0c27:
        r4 = org.telegram.ui.PhotoViewer.getInstance();
        r13 = 0;
        r14 = 1;
        r4.closePhoto(r13, r14);
    L_0x0c30:
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 0;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0ca2;
    L_0x0c3f:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r74;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        goto L_0x02ad;
    L_0x0c4f:
        r4 = "selectAlertString";
        r13 = "SendMessagesTo";
        r14 = 2131166161; // 0x7f0703d1 float:1.794656E38 double:1.0529359857E-314;
        r13 = org.telegram.messenger.LocaleController.getString(r13, r14);
        r0 = r20;
        r0.putString(r4, r13);
        r4 = "selectAlertStringGroup";
        r13 = "SendMessagesToGroup";
        r14 = 2131166162; // 0x7f0703d2 float:1.7946562E38 double:1.052935986E-314;
        r13 = org.telegram.messenger.LocaleController.getString(r13, r14);
        r0 = r20;
        r0.putString(r4, r13);
        goto L_0x0bcd;
    L_0x0c71:
        r57 = 0;
        goto L_0x0c0e;
    L_0x0c74:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        r13 = 1;
        if (r4 <= r13) goto L_0x0c9f;
    L_0x0c81:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r0 = r74;
        r13 = r0.actionBarLayout;
        r13 = r13.fragmentsStack;
        r13 = r13.size();
        r13 = r13 + -1;
        r4 = r4.get(r13);
        r4 = r4 instanceof org.telegram.ui.DialogsActivity;
        if (r4 == 0) goto L_0x0c9f;
    L_0x0c9b:
        r57 = 1;
    L_0x0c9d:
        goto L_0x0c0e;
    L_0x0c9f:
        r57 = 0;
        goto L_0x0c9d;
    L_0x0ca2:
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 1;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        goto L_0x02ad;
    L_0x0cad:
        r4 = 0;
        r13 = 0;
        r0 = r74;
        r1 = r32;
        r0.didSelectDialog(r4, r1, r13);
        goto L_0x02ad;
    L_0x0cb8:
        r4 = r46.intValue();
        if (r4 == 0) goto L_0x02ad;
    L_0x0cbe:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = new org.telegram.ui.SettingsActivity;
        r13.<init>();
        r14 = 0;
        r15 = 1;
        r16 = 1;
        r0 = r16;
        r4.presentFragment(r13, r14, r15, r0);
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x0cf1;
    L_0x0cd6:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4.showLastFragment();
        r0 = r74;
        r4 = r0.rightActionBarLayout;
        r4.showLastFragment();
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 0;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
    L_0x0ced:
        r53 = 1;
        goto L_0x02ad;
    L_0x0cf1:
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 1;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        goto L_0x0ced;
    L_0x0cfb:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x02de;
    L_0x0d07:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = new org.telegram.ui.DialogsActivity;
        r14 = 0;
        r13.<init>(r14);
        r4.addFragmentToStack(r13);
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 1;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        r74.loadBanner();
        goto L_0x02de;
    L_0x0d22:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x02de;
    L_0x0d2e:
        r4 = org.telegram.messenger.UserConfig.isClientActivated();
        if (r4 != 0) goto L_0x0d4b;
    L_0x0d34:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = new org.telegram.ui.LoginActivity;
        r13.<init>();
        r4.addFragmentToStack(r13);
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 0;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        goto L_0x02de;
    L_0x0d4b:
        r0 = r74;
        r4 = r0.actionBarLayout;
        r13 = new org.telegram.ui.DialogsActivity;
        r14 = 0;
        r13.<init>(r14);
        r4.addFragmentToStack(r13);
        r0 = r74;
        r4 = r0.drawerLayoutContainer;
        r13 = 1;
        r14 = 0;
        r4.setAllowOpenDrawer(r13, r14);
        r74.loadBanner();
        goto L_0x02de;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    private void runLinkRequest(String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, int state) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        int requestId = 0;
        TLObject req;
        if (username != null) {
            req = new TL_contacts_resolveUsername();
            req.username = username;
            requestId = ConnectionsManager.getInstance().sendRequest(req, new C18728(progressDialog, botChat, botUser, messageId));
        } else if (group != null) {
            if (state == 0) {
                req = new TL_messages_checkChatInvite();
                req.hash = group;
                ConnectionsManager instance = ConnectionsManager.getInstance();
                requestId = r20.sendRequest(req, new C18739(progressDialog, username, group, sticker, botUser, botChat, message, hasUrl, messageId), 2);
            } else if (state == 1) {
                req = new TL_messages_importChatInvite();
                req.hash = group;
                ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass10(progressDialog), 2);
            }
        } else if (sticker != null) {
            if (!mainFragmentsStack.isEmpty()) {
                InputStickerSet stickerset = new TL_inputStickerSetShortName();
                stickerset.short_name = sticker;
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(new StickersAlert(this, stickerset, null, null));
                return;
            }
            return;
        } else if (message != null) {
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new AnonymousClass11(hasUrl, message));
            presentFragment(fragment, false, true);
        }
        if (requestId != 0) {
            progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new AnonymousClass12(requestId));
            progressDialog.show();
        }
    }

    public AlertDialog showAlertDialog(Builder builder) {
        AlertDialog alertDialog = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    LaunchActivity.this.visibleDialog = null;
                }
            });
            return this.visibleDialog;
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
            return alertDialog;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialog(DialogsActivity dialogsFragment, long dialog_id, boolean param) {
        if (dialog_id != 0) {
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            Bundle args = new Bundle();
            args.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            if (lower_part == 0) {
                args.putInt("enc_id", high_id);
            } else if (high_id == 1) {
                args.putInt("chat_id", lower_part);
            } else if (lower_part > 0) {
                args.putInt("user_id", lower_part);
            } else if (lower_part < 0) {
                args.putInt("chat_id", -lower_part);
            }
            if (MessagesController.checkCanOpenChat(args, dialogsFragment)) {
                BaseFragment chatActivity = new ChatActivity(args);
                if (this.videoPath == null) {
                    this.actionBarLayout.presentFragment(chatActivity, dialogsFragment != null, dialogsFragment == null, true);
                    if (this.photoPathsArray != null) {
                        ArrayList<String> captions = null;
                        if (this.sendingText != null && this.photoPathsArray.size() == 1) {
                            captions = new ArrayList();
                            captions.add(this.sendingText);
                            this.sendingText = null;
                        }
                        SendMessagesHelper.prepareSendingPhotos(null, this.photoPathsArray, dialog_id, null, captions);
                    }
                    if (this.sendingText != null) {
                        SendMessagesHelper.prepareSendingText(this.sendingText, dialog_id);
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, dialog_id, null);
                    }
                    if (!(this.contactsToSend == null || this.contactsToSend.isEmpty())) {
                        Iterator i$ = this.contactsToSend.iterator();
                        while (i$.hasNext()) {
                            SendMessagesHelper.getInstance().sendMessage((User) i$.next(), dialog_id, null, null, null);
                        }
                    }
                } else if (VERSION.SDK_INT >= 16) {
                    if (AndroidUtilities.isTablet()) {
                        this.actionBarLayout.presentFragment(chatActivity, false, true, true);
                    } else {
                        this.actionBarLayout.addFragmentToStack(chatActivity, this.actionBarLayout.fragmentsStack.size() - 1);
                    }
                    if (!(chatActivity.openVideoEditor(this.videoPath, dialogsFragment != null, false) || dialogsFragment == null || AndroidUtilities.isTablet())) {
                        dialogsFragment.finishFragment(true);
                    }
                } else {
                    this.actionBarLayout.presentFragment(chatActivity, dialogsFragment != null, dialogsFragment == null, true);
                    SendMessagesHelper.prepareSendingVideo(this.videoPath, 0, 0, 0, 0, null, dialog_id, null);
                }
                this.photoPathsArray = null;
                this.videoPath = null;
                this.sendingText = null;
                this.documentsPathsArray = null;
                this.documentsOriginalPathsArray = null;
                this.contactsToSend = null;
            }
        }
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
            if (this.lockRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
                this.lockRunnable = null;
            }
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedConnectionState);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        }
    }

    public void presentFragment(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true);
    }

    public void needLayout() {
        int i = 8;
        int i2 = 0;
        if (AndroidUtilities.isTablet()) {
            int y;
            LayoutParams relativeLayoutParams = (LayoutParams) this.layersActionBarLayout.getLayoutParams();
            relativeLayoutParams.leftMargin = (AndroidUtilities.displaySize.x - relativeLayoutParams.width) / 2;
            if (VERSION.SDK_INT >= 21) {
                y = AndroidUtilities.statusBarHeight;
            } else {
                y = 0;
            }
            relativeLayoutParams.topMargin = (((AndroidUtilities.displaySize.y - relativeLayoutParams.height) - y) / 2) + y;
            this.layersActionBarLayout.setLayoutParams(relativeLayoutParams);
            int a;
            BaseFragment chatFragment;
            if (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2) {
                int i3;
                this.tabletFullSize = false;
                int leftWidth = (AndroidUtilities.displaySize.x / 100) * 35;
                if (leftWidth < AndroidUtilities.dp(320.0f)) {
                    leftWidth = AndroidUtilities.dp(320.0f);
                }
                relativeLayoutParams = (LayoutParams) this.actionBarLayout.getLayoutParams();
                relativeLayoutParams.width = leftWidth;
                relativeLayoutParams.height = -1;
                this.actionBarLayout.setLayoutParams(relativeLayoutParams);
                relativeLayoutParams = (LayoutParams) this.shadowTabletSide.getLayoutParams();
                relativeLayoutParams.leftMargin = leftWidth;
                this.shadowTabletSide.setLayoutParams(relativeLayoutParams);
                relativeLayoutParams = (LayoutParams) this.rightActionBarLayout.getLayoutParams();
                relativeLayoutParams.width = AndroidUtilities.displaySize.x - leftWidth;
                relativeLayoutParams.height = -1;
                relativeLayoutParams.leftMargin = leftWidth;
                this.rightActionBarLayout.setLayoutParams(relativeLayoutParams);
                if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() >= 2) {
                    for (a = 1; a < this.actionBarLayout.fragmentsStack.size(); a = (a - 1) + 1) {
                        chatFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(a);
                        chatFragment.onPause();
                        this.actionBarLayout.fragmentsStack.remove(a);
                        this.rightActionBarLayout.fragmentsStack.add(chatFragment);
                    }
                    if (this.passcodeView.getVisibility() != 0) {
                        this.actionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                    }
                }
                ActionBarLayout actionBarLayout = this.rightActionBarLayout;
                if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    i3 = 8;
                } else {
                    i3 = 0;
                }
                actionBarLayout.setVisibility(i3);
                ImageView imageView = this.backgroundTablet;
                if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    i3 = 0;
                } else {
                    i3 = 8;
                }
                imageView.setVisibility(i3);
                FrameLayout frameLayout = this.shadowTabletSide;
                if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    i2 = 8;
                }
                frameLayout.setVisibility(i2);
                return;
            }
            this.tabletFullSize = true;
            relativeLayoutParams = (LayoutParams) this.actionBarLayout.getLayoutParams();
            relativeLayoutParams.width = -1;
            relativeLayoutParams.height = -1;
            this.actionBarLayout.setLayoutParams(relativeLayoutParams);
            this.shadowTabletSide.setVisibility(8);
            this.rightActionBarLayout.setVisibility(8);
            ImageView imageView2 = this.backgroundTablet;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 0;
            }
            imageView2.setVisibility(i);
            if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                a = 0;
                while (this.rightActionBarLayout.fragmentsStack.size() > 0) {
                    chatFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(a);
                    chatFragment.onPause();
                    this.rightActionBarLayout.fragmentsStack.remove(a);
                    this.actionBarLayout.fragmentsStack.add(chatFragment);
                    a = (a - 1) + 1;
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                }
            }
        }
    }

    public void fixLayout() {
        if (AndroidUtilities.isTablet() && this.actionBarLayout != null) {
            this.actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                /* renamed from: org.telegram.ui.LaunchActivity.14.1 */
                class C12641 implements Runnable {
                    C12641() {
                    }

                    public void run() {
                        LaunchActivity.this.needLayout();
                    }
                }

                public void onGlobalLayout() {
                    AndroidUtilities.runOnUIThread(new C12641());
                    if (LaunchActivity.this.actionBarLayout == null) {
                        return;
                    }
                    if (VERSION.SDK_INT < 16) {
                        LaunchActivity.this.actionBarLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        LaunchActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!(UserConfig.passcodeHash.length() == 0 || UserConfig.lastPauseTime == 0)) {
            UserConfig.lastPauseTime = 0;
            UserConfig.saveConfig(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(requestCode, resultCode, data);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 3 && requestCode != 4 && requestCode != 5) {
            if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == 0) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
            }
            if (this.actionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                    ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
                }
                if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                    ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
                }
            }
        } else if (grantResults.length <= 0 || grantResults[0] != 0) {
            Builder builder = new Builder(this);
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            if (requestCode == 3) {
                builder.setMessage(LocaleController.getString("PermissionNoAudio", C0691R.string.PermissionNoAudio));
            } else if (requestCode == 4) {
                builder.setMessage(LocaleController.getString("PermissionStorage", C0691R.string.PermissionStorage));
            } else if (requestCode == 5) {
                builder.setMessage(LocaleController.getString("PermissionContacts", C0691R.string.PermissionContacts));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0691R.string.PermissionOpenSettings), new OnClickListener() {
                @TargetApi(9)
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                        LaunchActivity.this.startActivity(intent);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            builder.show();
        } else if (requestCode == 4) {
            ImageLoader.getInstance().checkMediaPaths();
        } else if (requestCode == 5) {
            ContactsController.getInstance().readContacts();
        }
    }

    protected void onPause() {
        super.onPause();
        ApplicationLoader.mainInterfacePaused = true;
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onPause();
            this.layersActionBarLayout.onPause();
        }
        if (this.passcodeView != null) {
            this.passcodeView.onPause();
        }
        ConnectionsManager.getInstance().setAppPaused(true, false);
        AndroidUtilities.unregisterUpdates();
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
    }

    protected void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    protected void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    protected void onDestroy() {
        PhotoViewer.getInstance().destroyPhotoViewer();
        SecretPhotoViewer.getInstance().destroyPhotoViewer();
        StickerPreviewViewer.getInstance().destroy();
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                View view = getWindow().getDecorView().getRootView();
                if (VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this.onGlobalLayoutListener);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
                }
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        super.onDestroy();
        onFinish();
    }

    protected void onResume() {
        super.onResume();
        ApplicationLoader.mainInterfacePaused = false;
        onPasscodeResume();
        if (this.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onResume();
                this.layersActionBarLayout.onResume();
            }
        } else {
            this.passcodeView.onResume();
        }
        AndroidUtilities.checkForCrashes(this);
        AndroidUtilities.checkForUpdates(this);
        ConnectionsManager.getInstance().setAppPaused(false, false);
        updateCurrentConnectionState();
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onResume();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize();
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            if (this.drawerLayoutAdapter != null) {
                this.drawerLayoutAdapter.notifyDataSetChanged();
            }
            Iterator i$ = this.actionBarLayout.fragmentsStack.iterator();
            while (i$.hasNext()) {
                ((BaseFragment) i$.next()).onFragmentDestroy();
            }
            this.actionBarLayout.fragmentsStack.clear();
            if (AndroidUtilities.isTablet()) {
                i$ = this.layersActionBarLayout.fragmentsStack.iterator();
                while (i$.hasNext()) {
                    ((BaseFragment) i$.next()).onFragmentDestroy();
                }
                this.layersActionBarLayout.fragmentsStack.clear();
                i$ = this.rightActionBarLayout.fragmentsStack.iterator();
                while (i$.hasNext()) {
                    ((BaseFragment) i$.next()).onFragmentDestroy();
                }
                this.rightActionBarLayout.fragmentsStack.clear();
            }
            startActivity(new Intent(this, IntroActivity.class));
            onFinish();
            finish();
        } else if (id == NotificationCenter.closeOtherAppActivities) {
            if (args[0] != this) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.didUpdatedConnectionState) {
            int state = ConnectionsManager.getInstance().getConnectionState();
            if (this.currentConnectionState != state) {
                FileLog.m10d("tmessages", "switch to state " + state);
                this.currentConnectionState = state;
                updateCurrentConnectionState();
            }
        } else if (id == NotificationCenter.mainUserInfoChanged) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.needShowAlert) {
            Integer reason = args[0];
            builder = new Builder(this);
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            if (reason.intValue() != 2) {
                builder.setNegativeButton(LocaleController.getString("MoreInfo", C0691R.string.MoreInfo), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                            MessagesController.openByUserName("spambot", (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
                        }
                    }
                });
            }
            if (reason.intValue() == 0) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam1", C0691R.string.NobodyLikesSpam1));
            } else if (reason.intValue() == 1) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam2", C0691R.string.NobodyLikesSpam2));
            } else if (reason.intValue() == 2) {
                builder.setMessage((String) args[1]);
            }
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        } else if (id == NotificationCenter.wasUnableToFindCurrentLocation) {
            HashMap<String, MessageObject> waitingForLocation = args[0];
            builder = new Builder(this);
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            builder.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", C0691R.string.ShareYouLocationUnableManually), new AnonymousClass17(waitingForLocation));
            builder.setMessage(LocaleController.getString("ShareYouLocationUnable", C0691R.string.ShareYouLocationUnable));
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(builder.create());
            }
        }
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (UserConfig.passcodeHash.length() != 0) {
            UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
            this.lockRunnable = new Runnable() {
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            FileLog.m11e("tmessages", "lock app");
                            LaunchActivity.this.showPasscodeActivity();
                        } else {
                            FileLog.m11e("tmessages", "didn't pass lock check");
                        }
                        LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            if (UserConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000);
            } else if (UserConfig.autoLockIn != 0) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) UserConfig.autoLockIn) * 1000) + 1000);
            }
        } else {
            UserConfig.lastPauseTime = 0;
        }
        UserConfig.saveConfig(false);
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity();
        }
        if (UserConfig.lastPauseTime != 0) {
            UserConfig.lastPauseTime = 0;
            UserConfig.saveConfig(false);
        }
    }

    private void updateCurrentConnectionState() {
        String text = null;
        if (this.currentConnectionState == 2) {
            text = LocaleController.getString("WaitingForNetwork", C0691R.string.WaitingForNetwork);
        } else if (this.currentConnectionState == 1) {
            text = LocaleController.getString("Connecting", C0691R.string.Connecting);
        } else if (this.currentConnectionState == 4) {
            text = LocaleController.getString("Updating", C0691R.string.Updating);
        }
        this.actionBarLayout.setTitleOverlayText(text);
    }

    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            BaseFragment lastFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                lastFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (lastFragment != null) {
                Bundle args = lastFragment.getArguments();
                if ((lastFragment instanceof ChatActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat");
                } else if (lastFragment instanceof SettingsActivity) {
                    outState.putString("fragment", "settings");
                } else if ((lastFragment instanceof GroupCreateFinalActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "group");
                } else if (lastFragment instanceof WallpapersActivity) {
                    outState.putString("fragment", "wallpapers");
                } else {
                    if (lastFragment instanceof ProfileActivity) {
                        if (((ProfileActivity) lastFragment).isChat() && args != null) {
                            outState.putBundle("args", args);
                            outState.putString("fragment", "chat_profile");
                        }
                    }
                    if ((lastFragment instanceof ChannelCreateActivity) && args != null && args.getInt("step") == 0) {
                        outState.putBundle("args", args);
                        outState.putString("fragment", "channel");
                    } else if ((lastFragment instanceof ChannelEditActivity) && args != null) {
                        outState.putBundle("args", args);
                        outState.putString("fragment", "edit");
                    }
                }
                lastFragment.saveSelfArgs(outState);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            boolean cancel = false;
            if (this.rightActionBarLayout.getVisibility() == 0 && !this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                cancel = !((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() + -1)).onBackPressed();
            }
            if (!cancel) {
                this.actionBarLayout.onBackPressed();
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onLowMemory();
            this.layersActionBarLayout.onLowMemory();
        }
    }

    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        if (VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(mode);
                this.layersActionBarLayout.onActionModeStarted(mode);
            }
        }
    }

    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        if (VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeFinished(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeFinished(mode);
                this.layersActionBarLayout.onActionModeFinished(mode);
            }
        }
    }

    public boolean onPreIme() {
        if (!PhotoViewer.getInstance().isVisible()) {
            return false;
        }
        PhotoViewer.getInstance().closePhoto(true, false);
        return true;
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == 82 && !UserConfig.isWaitingForPasscodeEnter) {
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0 && !this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.onKeyUp(keyCode, event);
                } else if (this.rightActionBarLayout.getVisibility() != 0 || this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.onKeyUp(keyCode, event);
                } else {
                    this.rightActionBarLayout.onKeyUp(keyCode, event);
                }
            } else if (this.actionBarLayout.fragmentsStack.size() != 1) {
                this.actionBarLayout.onKeyUp(keyCode, event);
            } else if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
            } else {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        boolean z = true;
        boolean z2;
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            z2 = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z2, true);
            if ((fragment instanceof DialogsActivity) && ((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                this.actionBarLayout.removeAllFragments();
                this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false);
                this.layersActionBarLayout.removeAllFragments();
                this.layersActionBarLayout.setVisibility(8);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                if (this.tabletFullSize) {
                    return false;
                }
                this.shadowTabletSide.setVisibility(0);
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    return false;
                }
                this.backgroundTablet.setVisibility(0);
                return false;
            } else if (fragment instanceof ChatActivity) {
                int a;
                ActionBarLayout actionBarLayout;
                if ((!this.tabletFullSize && layout == this.rightActionBarLayout) || (this.tabletFullSize && layout == this.actionBarLayout)) {
                    boolean result;
                    if (this.tabletFullSize && layout == this.actionBarLayout && this.actionBarLayout.fragmentsStack.size() == 1) {
                        result = false;
                    } else {
                        result = true;
                    }
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        actionBarLayout = this.layersActionBarLayout;
                        if (forceWithoutAnimation) {
                            z = false;
                        }
                        actionBarLayout.closeLastFragment(z);
                    }
                    if (!result) {
                        this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false);
                    }
                    return result;
                } else if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    actionBarLayout = this.layersActionBarLayout;
                    if (forceWithoutAnimation) {
                        z = false;
                    }
                    actionBarLayout.closeLastFragment(z);
                    return false;
                } else if (!this.tabletFullSize || layout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        a = 0;
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                            a = (a - 1) + 1;
                        }
                        r6 = this.layersActionBarLayout;
                        if (forceWithoutAnimation) {
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                        r6.closeLastFragment(z2);
                    }
                    actionBarLayout = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                        z = false;
                    }
                    actionBarLayout.presentFragment(fragment, z, forceWithoutAnimation, false);
                    return false;
                } else {
                    r6 = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() > 1) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    r6.presentFragment(fragment, z2, forceWithoutAnimation, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    actionBarLayout = this.layersActionBarLayout;
                    if (forceWithoutAnimation) {
                        z = false;
                    }
                    actionBarLayout.closeLastFragment(z);
                    return false;
                }
            } else if (layout == this.layersActionBarLayout) {
                return true;
            } else {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof LoginActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(2130706432);
                }
                this.layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false);
                return false;
            }
        }
        drawerLayoutContainer = this.drawerLayoutContainer;
        if ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity)) {
            z2 = false;
        } else {
            z2 = true;
        }
        drawerLayoutContainer.setAllowOpenDrawer(z2, false);
        return true;
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z, true);
            if (fragment instanceof DialogsActivity) {
                if (((DialogsActivity) fragment).isMainDialogList() && layout != this.actionBarLayout) {
                    this.actionBarLayout.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(fragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (this.tabletFullSize) {
                        return false;
                    }
                    this.shadowTabletSide.setVisibility(0);
                    if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    this.backgroundTablet.setVisibility(0);
                    return false;
                }
            } else if (fragment instanceof ChatActivity) {
                int a;
                if (!this.tabletFullSize && layout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(fragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                } else if (this.tabletFullSize && layout != this.actionBarLayout) {
                    this.actionBarLayout.addFragmentToStack(fragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    a = 0;
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        a = (a - 1) + 1;
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                }
            } else if (layout != this.layersActionBarLayout) {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof LoginActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(2130706432);
                }
                this.layersActionBarLayout.addFragmentToStack(fragment);
                return false;
            }
            return true;
        }
        drawerLayoutContainer = this.drawerLayoutContainer;
        if ((fragment instanceof LoginActivity) || (fragment instanceof CountrySelectActivity)) {
            z = false;
        } else {
            z = true;
        }
        drawerLayoutContainer.setAllowOpenDrawer(z, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == this.actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (layout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public void onRebuildAllFragments(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet() && layout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(true);
            this.rightActionBarLayout.showLastFragment();
            this.actionBarLayout.rebuildAllFragmentViews(true);
            this.actionBarLayout.showLastFragment();
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }
}
