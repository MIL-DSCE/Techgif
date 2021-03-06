package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.appsgeyser.sdk.ads.AdView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static boolean dialogsLoaded;
    private String addToGroupAlertString;
    private boolean checkPermission;
    private DialogsActivityDelegate delegate;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private int dialogsType;
    private LinearLayout emptyView;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private boolean onlySelect;
    private long openedDialogId;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private ProgressBar progressView;
    private boolean scrollUpdated;
    private EmptyTextProgressView searchEmptyView;
    private String searchString;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private long selectedDialog;

    /* renamed from: org.telegram.ui.DialogsActivity.13 */
    class AnonymousClass13 implements OnClickListener {
        final /* synthetic */ long val$dialog_id;

        AnonymousClass13(long j) {
            this.val$dialog_id = j;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            DialogsActivity.this.didSelectResult(this.val$dialog_id, false, false);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.1 */
    class C12171 implements Runnable {
        final /* synthetic */ Context val$context;

        C12171(Context context) {
            this.val$context = context;
        }

        public void run() {
            Theme.loadRecources(this.val$context);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.7 */
    class C12247 implements OnTouchListener {
        C12247() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.8 */
    class C12258 extends ViewOutlineProvider {
        C12258() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.9 */
    class C12269 implements View.OnClickListener {
        C12269() {
        }

        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putBoolean("destroyAfterSelect", true);
            DialogsActivity.this.presentFragment(new ContactsActivity(args));
        }
    }

    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z);
    }

    /* renamed from: org.telegram.ui.DialogsActivity.2 */
    class C18552 extends ActionBarMenuItemSearchListener {
        C18552() {
        }

        public void onSearchExpand() {
            DialogsActivity.this.searching = true;
            if (DialogsActivity.this.listView != null) {
                if (DialogsActivity.this.searchString != null) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    DialogsActivity.this.progressView.setVisibility(8);
                    DialogsActivity.this.emptyView.setVisibility(8);
                }
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButton.setVisibility(8);
                }
            }
            DialogsActivity.this.updatePasscodeButton();
        }

        public boolean canCollapseSearch() {
            if (DialogsActivity.this.searchString == null) {
                return true;
            }
            DialogsActivity.this.finishFragment();
            return false;
        }

        public void onSearchCollapse() {
            DialogsActivity.this.searching = false;
            DialogsActivity.this.searchWas = false;
            if (DialogsActivity.this.listView != null) {
                DialogsActivity.this.searchEmptyView.setVisibility(8);
                if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                    DialogsActivity.this.emptyView.setVisibility(8);
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
                } else {
                    DialogsActivity.this.progressView.setVisibility(8);
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.emptyView);
                }
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButton.setVisibility(0);
                    DialogsActivity.this.floatingHidden = true;
                    DialogsActivity.this.floatingButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                    DialogsActivity.this.hideFloatingButton(false);
                }
                if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsAdapter) {
                    DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsAdapter);
                    DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                }
            }
            if (DialogsActivity.this.dialogsSearchAdapter != null) {
                DialogsActivity.this.dialogsSearchAdapter.searchDialogs(null);
            }
            DialogsActivity.this.updatePasscodeButton();
        }

        public void onTextChanged(EditText editText) {
            String text = editText.getText().toString();
            if (text.length() != 0 || (DialogsActivity.this.dialogsSearchAdapter != null && DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())) {
                DialogsActivity.this.searchWas = true;
                if (!(DialogsActivity.this.dialogsSearchAdapter == null || DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter)) {
                    DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsSearchAdapter);
                    DialogsActivity.this.dialogsSearchAdapter.notifyDataSetChanged();
                }
                if (!(DialogsActivity.this.searchEmptyView == null || DialogsActivity.this.listView.getEmptyView() == DialogsActivity.this.searchEmptyView)) {
                    DialogsActivity.this.emptyView.setVisibility(8);
                    DialogsActivity.this.progressView.setVisibility(8);
                    DialogsActivity.this.searchEmptyView.showTextView();
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                }
            }
            if (DialogsActivity.this.dialogsSearchAdapter != null) {
                DialogsActivity.this.dialogsSearchAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.3 */
    class C18563 extends ActionBarMenuOnItemClick {
        C18563() {
        }

        public void onItemClick(int id) {
            boolean z = true;
            if (id == -1) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.finishFragment();
                } else if (DialogsActivity.this.parentLayout != null) {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                }
            } else if (id == 1) {
                if (UserConfig.appLocked) {
                    z = false;
                }
                UserConfig.appLocked = z;
                UserConfig.saveConfig(false);
                DialogsActivity.this.updatePasscodeButton();
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.5 */
    class C18575 implements OnItemClickListener {
        C18575() {
        }

        public void onItemClick(View view, int position) {
            if (DialogsActivity.this.listView != null) {
                if (DialogsActivity.this.listView.getAdapter() != null) {
                    long dialog_id = 0;
                    int message_id = 0;
                    Adapter adapter = DialogsActivity.this.listView.getAdapter();
                    if (adapter == DialogsActivity.this.dialogsAdapter) {
                        TL_dialog dialog = DialogsActivity.this.dialogsAdapter.getItem(position);
                        if (dialog != null) {
                            dialog_id = dialog.id;
                        } else {
                            return;
                        }
                    }
                    if (adapter == DialogsActivity.this.dialogsSearchAdapter) {
                        MessageObject obj = DialogsActivity.this.dialogsSearchAdapter.getItem(position);
                        if (obj instanceof User) {
                            dialog_id = (long) ((User) obj).id;
                            if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(position)) {
                                ArrayList<User> users = new ArrayList();
                                users.add((User) obj);
                                MessagesController.getInstance().putUsers(users, false);
                                MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                            }
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (User) obj);
                            }
                        } else if (obj instanceof Chat) {
                            if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(position)) {
                                ArrayList<Chat> chats = new ArrayList();
                                chats.add((Chat) obj);
                                MessagesController.getInstance().putChats(chats, false);
                                MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                            }
                            if (((Chat) obj).id > 0) {
                                dialog_id = (long) (-((Chat) obj).id);
                            } else {
                                dialog_id = AndroidUtilities.makeBroadcastId(((Chat) obj).id);
                            }
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (Chat) obj);
                            }
                        } else if (obj instanceof EncryptedChat) {
                            dialog_id = ((long) ((EncryptedChat) obj).id) << 32;
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (EncryptedChat) obj);
                            }
                        } else if (obj instanceof MessageObject) {
                            MessageObject messageObject = obj;
                            dialog_id = messageObject.getDialogId();
                            message_id = messageObject.getId();
                            DialogsActivity.this.dialogsSearchAdapter.addHashtagsFromMessage(DialogsActivity.this.dialogsSearchAdapter.getLastSearchString());
                        } else if (obj instanceof String) {
                            DialogsActivity.this.actionBar.openSearchField((String) obj);
                        }
                    }
                    if (dialog_id != 0) {
                        if (DialogsActivity.this.onlySelect) {
                            DialogsActivity.this.didSelectResult(dialog_id, true, false);
                            return;
                        }
                        Bundle args = new Bundle();
                        int lower_part = (int) dialog_id;
                        int high_id = (int) (dialog_id >> 32);
                        if (lower_part == 0) {
                            args.putInt("enc_id", high_id);
                        } else if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else if (lower_part > 0) {
                            args.putInt("user_id", lower_part);
                        } else if (lower_part < 0) {
                            if (message_id != 0) {
                                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_part));
                                if (!(chat == null || chat.migrated_to == null)) {
                                    args.putInt("migrated_to", lower_part);
                                    lower_part = -chat.migrated_to.channel_id;
                                }
                            }
                            String str = "chat_id";
                            args.putInt(r19, -lower_part);
                        }
                        if (message_id != 0) {
                            args.putInt("message_id", message_id);
                        } else {
                            if (DialogsActivity.this.actionBar != null) {
                                DialogsActivity.this.actionBar.closeSearchField();
                            }
                        }
                        if (AndroidUtilities.isTablet()) {
                            if (DialogsActivity.this.openedDialogId == dialog_id) {
                                if (adapter != DialogsActivity.this.dialogsSearchAdapter) {
                                    return;
                                }
                            }
                            if (DialogsActivity.this.dialogsAdapter != null) {
                                DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = dialog_id);
                                DialogsActivity.this.updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                            }
                        }
                        if (DialogsActivity.this.searchString != null) {
                            if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                DialogsActivity.this.presentFragment(new ChatActivity(args));
                                return;
                            }
                            return;
                        }
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            DialogsActivity.this.presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.6 */
    class C18586 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.DialogsActivity.6.1 */
        class C12181 implements OnClickListener {
            C12181() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        }

        /* renamed from: org.telegram.ui.DialogsActivity.6.2 */
        class C12212 implements OnClickListener {
            final /* synthetic */ Chat val$chat;

            /* renamed from: org.telegram.ui.DialogsActivity.6.2.1 */
            class C12191 implements OnClickListener {
                C12191() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 2);
                }
            }

            /* renamed from: org.telegram.ui.DialogsActivity.6.2.2 */
            class C12202 implements OnClickListener {
                C12202() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getCurrentUser(), null);
                    if (AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                    }
                }
            }

            C12212(Chat chat) {
                this.val$chat = chat;
            }

            public void onClick(DialogInterface dialog, int which) {
                Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                if (which == 0) {
                    if (this.val$chat == null || !this.val$chat.megagroup) {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", C0691R.string.AreYouSureClearHistoryChannel));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", C0691R.string.AreYouSureClearHistorySuper));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C12191());
                } else {
                    if (this.val$chat == null || !this.val$chat.megagroup) {
                        if (this.val$chat == null || !this.val$chat.creator) {
                            builder.setMessage(LocaleController.getString("ChannelLeaveAlert", C0691R.string.ChannelLeaveAlert));
                        } else {
                            builder.setMessage(LocaleController.getString("ChannelDeleteAlert", C0691R.string.ChannelDeleteAlert));
                        }
                    } else if (this.val$chat.creator) {
                        builder.setMessage(LocaleController.getString("MegaDeleteAlert", C0691R.string.MegaDeleteAlert));
                    } else {
                        builder.setMessage(LocaleController.getString("MegaLeaveAlert", C0691R.string.MegaLeaveAlert));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C12202());
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder.create());
            }
        }

        /* renamed from: org.telegram.ui.DialogsActivity.6.3 */
        class C12233 implements OnClickListener {
            final /* synthetic */ boolean val$isBot;
            final /* synthetic */ boolean val$isChat;

            /* renamed from: org.telegram.ui.DialogsActivity.6.3.1 */
            class C12221 implements OnClickListener {
                final /* synthetic */ int val$which;

                C12221(int i) {
                    this.val$which = i;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (this.val$which != 0) {
                        if (C12233.this.val$isChat) {
                            Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                            if (currentChat == null || !ChatObject.isNotInChat(currentChat)) {
                                MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                            } else {
                                MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                            }
                        } else {
                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                        }
                        if (C12233.this.val$isBot) {
                            MessagesController.getInstance().blockUser((int) DialogsActivity.this.selectedDialog);
                        }
                        if (AndroidUtilities.isTablet()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                            return;
                        }
                        return;
                    }
                    MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                }
            }

            C12233(boolean z, boolean z2) {
                this.val$isChat = z;
                this.val$isBot = z2;
            }

            public void onClick(DialogInterface dialog, int which) {
                Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                if (which == 0) {
                    builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0691R.string.AreYouSureClearHistory));
                } else if (this.val$isChat) {
                    builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0691R.string.AreYouSureDeleteAndExit));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0691R.string.AreYouSureDeleteThisChat));
                }
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C12221(which));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder.create());
            }
        }

        C18586() {
        }

        public boolean onItemClick(View view, int position) {
            if (!DialogsActivity.this.onlySelect && ((!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) && DialogsActivity.this.getParentActivity() != null)) {
                ArrayList<TL_dialog> dialogs = DialogsActivity.this.getDialogsArray();
                if (position < 0 || position >= dialogs.size()) {
                    return false;
                }
                TL_dialog dialog = (TL_dialog) dialogs.get(position);
                DialogsActivity.this.selectedDialog = dialog.id;
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                int lower_id = (int) DialogsActivity.this.selectedDialog;
                int high_id = (int) (DialogsActivity.this.selectedDialog >> 32);
                String string;
                if (DialogObject.isChannel(dialog)) {
                    CharSequence[] items;
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                    if (chat == null || !chat.megagroup) {
                        items = new CharSequence[2];
                        items[0] = LocaleController.getString("ClearHistoryCache", C0691R.string.ClearHistoryCache);
                        if (chat == null || !chat.creator) {
                            string = LocaleController.getString("LeaveChannelMenu", C0691R.string.LeaveChannelMenu);
                        } else {
                            string = LocaleController.getString("ChannelDeleteMenu", C0691R.string.ChannelDeleteMenu);
                        }
                        items[1] = string;
                    } else {
                        items = new CharSequence[2];
                        items[0] = LocaleController.getString("ClearHistoryCache", C0691R.string.ClearHistoryCache);
                        string = (chat == null || !chat.creator) ? LocaleController.getString("LeaveMegaMenu", C0691R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", C0691R.string.DeleteMegaMenu);
                        items[1] = string;
                    }
                    builder.setItems(items, new C12212(chat));
                    DialogsActivity.this.showDialog(builder.create());
                } else {
                    boolean isChat = lower_id < 0 && high_id != 1;
                    User user = null;
                    if (!(isChat || lower_id <= 0 || high_id == 1)) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                    }
                    boolean isBot = user != null && user.bot;
                    CharSequence[] charSequenceArr = new CharSequence[2];
                    charSequenceArr[0] = LocaleController.getString("ClearHistory", C0691R.string.ClearHistory);
                    string = isChat ? LocaleController.getString("DeleteChat", C0691R.string.DeleteChat) : isBot ? LocaleController.getString("DeleteAndStop", C0691R.string.DeleteAndStop) : LocaleController.getString("Delete", C0691R.string.Delete);
                    charSequenceArr[1] = string;
                    builder.setItems(charSequenceArr, new C12233(isChat, isBot));
                    DialogsActivity.this.showDialog(builder.create());
                }
                return true;
            } else if (((!DialogsActivity.this.searchWas || !DialogsActivity.this.searching) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) || DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter || (!(DialogsActivity.this.dialogsSearchAdapter.getItem(position) instanceof String) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed())) {
                return false;
            } else {
                Builder builder2 = new Builder(DialogsActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder2.setMessage(LocaleController.getString("ClearSearch", C0691R.string.ClearSearch));
                builder2.setPositiveButton(LocaleController.getString("ClearButton", C0691R.string.ClearButton).toUpperCase(), new C12181());
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder2.create());
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity.4 */
    class C20164 extends LinearLayoutManager {
        C20164(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    public DialogsActivity(Bundle args) {
        super(args);
        this.floatingInterpolator = new AccelerateDecelerateInterpolator();
        this.checkPermission = true;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.onlySelect = this.arguments.getBoolean("onlySelect", false);
            this.dialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
        }
        if (this.searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
        }
        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            dialogsLoaded = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
        }
        this.delegate = null;
    }

    public View createView(Context context) {
        float f;
        float f2;
        this.searching = false;
        this.searchWas = false;
        AndroidUtilities.runOnUIThread(new C12171(context));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null) {
            this.passcodeItem = menu.addItem(1, (int) C0691R.drawable.lock_close);
            updatePasscodeButton();
        }
        menu.addItem(0, (int) C0691R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18552()).getSearchField().setHint(LocaleController.getString("Search", C0691R.string.Search));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
            this.actionBar.setTitle(LocaleController.getString("SelectChat", C0691R.string.SelectChat));
        } else {
            if (this.searchString != null) {
                this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
            } else {
                this.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle(LocaleController.getString("AppNameBeta", C0691R.string.AppNameBeta));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C18563());
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-1, -1));
        RelativeLayout adViewContainer = new RelativeLayout(context);
        adViewContainer.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
        linearLayout.addView(adViewContainer);
        AdView adView = new AdView(context, null);
        ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, AndroidUtilities.dp(50.0f));
        layoutParams.addRule(13, -1);
        adView.setLayoutParams(layoutParams);
        adViewContainer.addView(adView);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        this.fragmentView = linearLayout;
        this.fragmentView.setBackgroundColor(this.parentLayout.getContext().getResources().getColor(C0691R.color.app_background));
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setInstantClick(true);
        this.listView.setLayoutAnimation(null);
        this.listView.setTag(Integer.valueOf(4));
        this.layoutManager = new C20164(context);
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.listView.setOnItemClickListener(new C18575());
        this.listView.setOnItemLongClickListener(new C18586());
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", C0691R.string.NoResult));
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.emptyView = new LinearLayout(context);
        this.emptyView.setOrientation(1);
        this.emptyView.setVisibility(8);
        this.emptyView.setGravity(17);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.emptyView.setOnTouchListener(new C12247());
        linearLayout = new TextView(context);
        linearLayout.setText(LocaleController.getString("NoChats", C0691R.string.NoChats));
        linearLayout.setTextColor(-6974059);
        linearLayout.setGravity(17);
        linearLayout.setTextSize(1, 20.0f);
        this.emptyView.addView(linearLayout, LayoutHelper.createLinear(-2, -2));
        linearLayout = new TextView(context);
        String help = LocaleController.getString("NoChatsHelp", C0691R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace('\n', ' ');
        }
        linearLayout.setText(help);
        linearLayout.setTextColor(-6974059);
        linearLayout.setTextSize(1, 15.0f);
        linearLayout.setGravity(17);
        linearLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), 0);
        linearLayout.setLineSpacing((float) AndroidUtilities.dp(2.0f), TouchHelperCallback.ALPHA_FULL);
        this.emptyView.addView(linearLayout, LayoutHelper.createLinear(-2, -2));
        this.progressView = new ProgressBar(context);
        this.progressView.setVisibility(8);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.floatingButton = new ImageView(context);
        this.floatingButton.setVisibility(this.onlySelect ? 8 : 0);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        this.floatingButton.setBackgroundResource(C0691R.drawable.floating_states);
        this.floatingButton.setImageResource(C0691R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new C12258());
        }
        View view = this.floatingButton;
        int i = (LocaleController.isRTL ? 3 : 5) | 80;
        if (LocaleController.isRTL) {
            f = 14.0f;
        } else {
            f = 0.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 0.0f;
        } else {
            f2 = 14.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f, 0.0f, f2, 14.0f));
        this.floatingButton.setOnClickListener(new C12269());
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) {
                    if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10) {
                        MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                    }
                    if (DialogsActivity.this.floatingButton.getVisibility() != 8) {
                        boolean goingDown;
                        View topChild = recyclerView.getChildAt(0);
                        int firstViewTop = 0;
                        if (topChild != null) {
                            firstViewTop = topChild.getTop();
                        }
                        boolean changed = true;
                        if (DialogsActivity.this.prevPosition == firstVisibleItem) {
                            int topDelta = DialogsActivity.this.prevTop - firstViewTop;
                            goingDown = firstViewTop < DialogsActivity.this.prevTop;
                            changed = Math.abs(topDelta) > 1;
                        } else {
                            goingDown = firstVisibleItem > DialogsActivity.this.prevPosition;
                        }
                        if (changed && DialogsActivity.this.scrollUpdated) {
                            DialogsActivity.this.hideFloatingButton(goingDown);
                        }
                        DialogsActivity.this.prevPosition = firstVisibleItem;
                        DialogsActivity.this.prevTop = firstViewTop;
                        DialogsActivity.this.scrollUpdated = true;
                    }
                } else if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }
        });
        if (this.searchString == null) {
            this.dialogsAdapter = new DialogsAdapter(context, this.dialogsType);
            if (AndroidUtilities.isTablet() && this.openedDialogId != 0) {
                this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
            }
            this.listView.setAdapter(this.dialogsAdapter);
        }
        int type = 0;
        if (this.searchString != null) {
            type = 2;
        } else if (!this.onlySelect) {
            type = 1;
        }
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, type, this.dialogsType);
        this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapterDelegate() {

            /* renamed from: org.telegram.ui.DialogsActivity.11.1 */
            class C12161 implements OnClickListener {
                final /* synthetic */ int val$did;

                C12161(int i) {
                    this.val$did = i;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    SearchQuery.removePeer(this.val$did);
                }
            }

            public void searchStateChanged(boolean search) {
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas || DialogsActivity.this.searchEmptyView == null) {
                    return;
                }
                if (search) {
                    DialogsActivity.this.searchEmptyView.showProgress();
                } else {
                    DialogsActivity.this.searchEmptyView.showTextView();
                }
            }

            public void didPressedOnSubDialog(int did) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.didSelectResult((long) did, true, false);
                    return;
                }
                Bundle args = new Bundle();
                if (did > 0) {
                    args.putInt("user_id", did);
                } else {
                    args.putInt("chat_id", -did);
                }
                if (DialogsActivity.this.actionBar != null) {
                    DialogsActivity.this.actionBar.closeSearchField();
                }
                if (AndroidUtilities.isTablet() && DialogsActivity.this.dialogsAdapter != null) {
                    DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = (long) did);
                    DialogsActivity.this.updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                }
                if (DialogsActivity.this.searchString != null) {
                    if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        DialogsActivity.this.presentFragment(new ChatActivity(args));
                    }
                } else if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                    DialogsActivity.this.presentFragment(new ChatActivity(args));
                }
            }

            public void needRemoveHint(int did) {
                if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance().getUser(Integer.valueOf(did)) != null) {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0691R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C12161(did));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }
        });
        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
            this.searchEmptyView.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.progressView);
        } else {
            this.searchEmptyView.setVisibility(8);
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        }
        if (this.searchString != null) {
            this.actionBar.openSearchField(this.searchString);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && !this.onlySelect && VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0 || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    Builder builder;
                    Dialog create;
                    if (activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        builder = new Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", C0691R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        builder = new Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", C0691R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else {
                        askForPermissons();
                    }
                }
            }
        }
    }

    @TargetApi(23)
    private void askForPermissons() {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList();
            if (activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                permissons.add("android.permission.READ_CONTACTS");
                permissons.add("android.permission.WRITE_CONTACTS");
                permissons.add("android.permission.GET_ACCOUNTS");
            }
            if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                permissons.add("android.permission.READ_EXTERNAL_STORAGE");
                permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            activity.requestPermissions((String[]) permissons.toArray(new String[permissons.size()]), 1);
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.onlySelect && this.floatingButton != null) {
            this.floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    DialogsActivity.this.floatingButton.setTranslationY(DialogsActivity.this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f);
                    DialogsActivity.this.floatingButton.setClickable(!DialogsActivity.this.floatingHidden);
                    if (DialogsActivity.this.floatingButton == null) {
                        return;
                    }
                    if (VERSION.SDK_INT < 16) {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            int a = 0;
            while (a < permissions.length) {
                if (grantResults.length > a && grantResults[a] == 0) {
                    String str = permissions[a];
                    Object obj = -1;
                    switch (str.hashCode()) {
                        case 1365911975:
                            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                                int i = 1;
                                break;
                            }
                            break;
                        case 1977429404:
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                obj = null;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                            ContactsController.getInstance().readContacts();
                            break;
                        case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                            ImageLoader.getInstance().checkMediaPaths();
                            break;
                        default:
                            break;
                    }
                }
                a++;
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (this.dialogsAdapter != null) {
                if (this.dialogsAdapter.isDataSetChanged()) {
                    this.dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                }
            }
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (this.listView != null) {
                if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                    this.searchEmptyView.setVisibility(8);
                    this.emptyView.setVisibility(8);
                    this.listView.setEmptyView(this.progressView);
                } else {
                    try {
                        this.progressView.setVisibility(8);
                        if (this.searching && this.searchWas) {
                            this.emptyView.setVisibility(8);
                            this.listView.setEmptyView(this.searchEmptyView);
                        } else {
                            this.searchEmptyView.setVisibility(8);
                            this.listView.setEmptyView(this.emptyView);
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows(((Integer) args[0]).intValue());
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.openedChatChanged) {
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean close = ((Boolean) args[1]).booleanValue();
                long dialog_id = ((Long) args[0]).longValue();
                if (!close) {
                    this.openedDialogId = dialog_id;
                } else if (dialog_id == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                if (this.dialogsAdapter != null) {
                    this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        }
        if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.reloadHints && this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        }
        return null;
    }

    private void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (UserConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            if (UserConfig.appLocked) {
                this.passcodeItem.setIcon(C0691R.drawable.lock_close);
            } else {
                this.passcodeItem.setIcon(C0691R.drawable.lock_open);
            }
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            boolean z;
            this.floatingHidden = hide;
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            animator.setInterpolator(this.floatingInterpolator);
            imageView = this.floatingButton;
            if (hide) {
                z = false;
            } else {
                z = true;
            }
            imageView.setClickable(z);
            animator.start();
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
                        DialogCell cell = (DialogCell) child;
                        if ((mask & MessagesController.UPDATE_MASK_NEW_MESSAGE) != 0) {
                            cell.checkCurrentDialogIndex();
                            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                boolean z;
                                if (cell.getDialogId() == this.openedDialogId) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                cell.setDialogSelected(z);
                            }
                        } else if ((mask & MessagesController.UPDATE_MASK_SELECT_DIALOG) == 0) {
                            cell.update(mask);
                        } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == this.openedDialogId);
                        }
                    }
                } else if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(mask);
                } else if (child instanceof RecyclerListView) {
                    RecyclerListView innerListView = (RecyclerListView) child;
                    int count2 = innerListView.getChildCount();
                    for (int b = 0; b < count2; b++) {
                        View child2 = innerListView.getChildAt(b);
                        if (child2 instanceof HintDialogCell) {
                            ((HintDialogCell) child2).checkUnreadCounter(mask);
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String string) {
        this.searchString = string;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    private void didSelectResult(long dialog_id, boolean useAlert, boolean param) {
        Builder builder;
        if (this.addToGroupAlertString == null && ((int) dialog_id) < 0 && ChatObject.isChannel(-((int) dialog_id)) && !ChatObject.isCanWriteToChannel(-((int) dialog_id))) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setMessage(LocaleController.getString("ChannelCantSendMessage", C0691R.string.ChannelCantSendMessage));
            builder.setNegativeButton(LocaleController.getString("OK", C0691R.string.OK), null);
            showDialog(builder.create());
        } else if (!useAlert || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                this.delegate.didSelectDialog(this, dialog_id, param);
                this.delegate = null;
                return;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part == 0) {
                if (MessagesController.getInstance().getUser(Integer.valueOf(MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id)).user_id)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (high_id == 1) {
                if (MessagesController.getInstance().getChat(Integer.valueOf(lower_part)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                } else {
                    return;
                }
            } else if (lower_part > 0) {
                if (MessagesController.getInstance().getUser(Integer.valueOf(lower_part)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (lower_part < 0) {
                if (MessagesController.getInstance().getChat(Integer.valueOf(-lower_part)) == null) {
                    return;
                }
                if (this.addToGroupAlertString != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, chat.title));
                } else {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new AnonymousClass13(dialog_id));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            showDialog(builder.create());
        }
    }
}
