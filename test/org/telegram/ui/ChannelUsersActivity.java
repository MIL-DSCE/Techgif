package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChannelParticipantRole;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.telegram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channelRoleEmpty;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class ChannelUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chatId;
    private EmptyTextProgressView emptyView;
    private boolean firstLoaded;
    private boolean isAdmin;
    private boolean isMegagroup;
    private boolean isPublic;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private ArrayList<ChannelParticipant> participants;
    private int participantsStartRow;
    private int type;

    /* renamed from: org.telegram.ui.ChannelUsersActivity.2 */
    class C10642 implements OnItemClickListener {
        final /* synthetic */ ListView val$listView;

        /* renamed from: org.telegram.ui.ChannelUsersActivity.2.1 */
        class C17911 implements ContactsActivityDelegate {
            C17911() {
            }

            public void didSelectContact(User user, String param) {
                MessagesController.getInstance().addUserToChat(ChannelUsersActivity.this.chatId, user, null, param != null ? Utilities.parseInt(param).intValue() : 0, null, ChannelUsersActivity.this);
            }
        }

        /* renamed from: org.telegram.ui.ChannelUsersActivity.2.2 */
        class C17922 implements ContactsActivityDelegate {
            C17922() {
            }

            public void didSelectContact(User user, String param) {
                ChannelUsersActivity.this.setUserChannelRole(user, new TL_channelRoleEditor());
            }
        }

        C10642(ListView listView) {
            this.val$listView = listView;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Bundle args;
            ContactsActivity fragment;
            if (ChannelUsersActivity.this.type == 2) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (i == 0) {
                        args = new Bundle();
                        args.putBoolean("onlyUsers", true);
                        args.putBoolean("destroyAfterSelect", true);
                        args.putBoolean("returnAsResult", true);
                        args.putBoolean("needForwardCount", false);
                        args.putBoolean("allowUsernameSearch", false);
                        args.putString("selectAlertString", LocaleController.getString("ChannelAddTo", C0691R.string.ChannelAddTo));
                        fragment = new ContactsActivity(args);
                        fragment.setDelegate(new C17911());
                        ChannelUsersActivity.this.presentFragment(fragment);
                    } else if (!ChannelUsersActivity.this.isPublic && i == 1) {
                        ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
                    }
                }
            } else if (ChannelUsersActivity.this.type == 1 && ChannelUsersActivity.this.isAdmin) {
                if (ChannelUsersActivity.this.isMegagroup && (i == 1 || i == 2)) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                    if (chat != null) {
                        boolean changed = false;
                        if (i == 1 && !chat.democracy) {
                            chat.democracy = true;
                            changed = true;
                        } else if (i == 2 && chat.democracy) {
                            chat.democracy = false;
                            changed = true;
                        }
                        if (changed) {
                            MessagesController.getInstance().toogleChannelInvites(ChannelUsersActivity.this.chatId, chat.democracy);
                            int count = this.val$listView.getChildCount();
                            for (int a = 0; a < count; a++) {
                                View child = this.val$listView.getChildAt(a);
                                if (child instanceof RadioCell) {
                                    int num = ((Integer) child.getTag()).intValue();
                                    RadioCell radioCell = (RadioCell) child;
                                    boolean z = (num == 0 && chat.democracy) || (num == 1 && !chat.democracy);
                                    radioCell.setChecked(z, true);
                                }
                            }
                            return;
                        }
                        return;
                    }
                    return;
                } else if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                    args = new Bundle();
                    args.putBoolean("onlyUsers", true);
                    args.putBoolean("destroyAfterSelect", true);
                    args.putBoolean("returnAsResult", true);
                    args.putBoolean("needForwardCount", false);
                    args.putBoolean("allowUsernameSearch", true);
                    args.putString("selectAlertString", LocaleController.getString("ChannelAddUserAdminAlert", C0691R.string.ChannelAddUserAdminAlert));
                    fragment = new ContactsActivity(args);
                    fragment.setDelegate(new C17922());
                    ChannelUsersActivity.this.presentFragment(fragment);
                    return;
                }
            }
            ChannelParticipant participant = null;
            if (i >= ChannelUsersActivity.this.participantsStartRow && i < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                participant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
            }
            if (participant != null) {
                args = new Bundle();
                args.putInt("user_id", participant.user_id);
                ChannelUsersActivity.this.presentFragment(new ProfileActivity(args));
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity.3 */
    class C10673 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.ChannelUsersActivity.3.1 */
        class C10661 implements OnClickListener {
            final /* synthetic */ ChannelParticipant val$finalParticipant;

            /* renamed from: org.telegram.ui.ChannelUsersActivity.3.1.1 */
            class C17931 implements RequestDelegate {

                /* renamed from: org.telegram.ui.ChannelUsersActivity.3.1.1.1 */
                class C10651 implements Runnable {
                    final /* synthetic */ Updates val$updates;

                    C10651(Updates updates) {
                        this.val$updates = updates;
                    }

                    public void run() {
                        MessagesController.getInstance().loadFullChat(((Chat) this.val$updates.chats.get(0)).id, 0, true);
                    }
                }

                C17931() {
                }

                public void run(TLObject response, TL_error error) {
                    if (response != null) {
                        Updates updates = (Updates) response;
                        MessagesController.getInstance().processUpdates(updates, false);
                        if (!updates.chats.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new C10651(updates), 1000);
                        }
                    }
                }
            }

            C10661(ChannelParticipant channelParticipant) {
                this.val$finalParticipant = channelParticipant;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i != 0) {
                    return;
                }
                if (ChannelUsersActivity.this.type == 0) {
                    ChannelUsersActivity.this.participants.remove(this.val$finalParticipant);
                    ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    TL_channels_kickFromChannel req = new TL_channels_kickFromChannel();
                    req.kicked = false;
                    req.user_id = MessagesController.getInputUser(this.val$finalParticipant.user_id);
                    req.channel = MessagesController.getInputChannel(ChannelUsersActivity.this.chatId);
                    ConnectionsManager.getInstance().sendRequest(req, new C17931());
                } else if (ChannelUsersActivity.this.type == 1) {
                    ChannelUsersActivity.this.setUserChannelRole(MessagesController.getInstance().getUser(Integer.valueOf(this.val$finalParticipant.user_id)), new TL_channelRoleEmpty());
                } else if (ChannelUsersActivity.this.type == 2) {
                    MessagesController.getInstance().deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(this.val$finalParticipant.user_id)), null);
                }
            }
        }

        C10673() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (ChannelUsersActivity.this.getParentActivity() == null) {
                return false;
            }
            ChannelParticipant participant = null;
            if (i >= ChannelUsersActivity.this.participantsStartRow && i < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                participant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
            }
            if (participant == null) {
                return false;
            }
            ChannelParticipant finalParticipant = participant;
            Builder builder = new Builder(ChannelUsersActivity.this.getParentActivity());
            CharSequence[] items = null;
            if (ChannelUsersActivity.this.type == 0) {
                items = new CharSequence[]{LocaleController.getString("Unblock", C0691R.string.Unblock)};
            } else if (ChannelUsersActivity.this.type == 1) {
                items = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", C0691R.string.ChannelRemoveUserAdmin)};
            } else if (ChannelUsersActivity.this.type == 2) {
                items = new CharSequence[]{LocaleController.getString("ChannelRemoveUser", C0691R.string.ChannelRemoveUser)};
            }
            builder.setItems(items, new C10661(finalParticipant));
            ChannelUsersActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity.5 */
    class C10705 implements Runnable {
        C10705() {
        }

        public void run() {
            ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity.1 */
    class C17901 extends ActionBarMenuOnItemClick {
        C17901() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelUsersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity.4 */
    class C17944 implements RequestDelegate {

        /* renamed from: org.telegram.ui.ChannelUsersActivity.4.1 */
        class C10681 implements Runnable {
            C10681() {
            }

            public void run() {
                MessagesController.getInstance().loadFullChat(ChannelUsersActivity.this.chatId, 0, true);
            }
        }

        /* renamed from: org.telegram.ui.ChannelUsersActivity.4.2 */
        class C10692 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C10692(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                AlertsCreator.showAddUserAlert(this.val$error.text, ChannelUsersActivity.this, !ChannelUsersActivity.this.isMegagroup);
            }
        }

        C17944() {
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesController.getInstance().processUpdates((Updates) response, false);
                AndroidUtilities.runOnUIThread(new C10681(), 1000);
                return;
            }
            AndroidUtilities.runOnUIThread(new C10692(error));
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity.6 */
    class C17956 implements RequestDelegate {

        /* renamed from: org.telegram.ui.ChannelUsersActivity.6.1 */
        class C10731 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.ui.ChannelUsersActivity.6.1.1 */
            class C10711 implements Comparator<ChannelParticipant> {
                C10711() {
                }

                public int compare(ChannelParticipant lhs, ChannelParticipant rhs) {
                    User user1 = MessagesController.getInstance().getUser(Integer.valueOf(rhs.user_id));
                    User user2 = MessagesController.getInstance().getUser(Integer.valueOf(lhs.user_id));
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
                            if (status2 < 0 && status1 > 0) {
                                return 1;
                            }
                            if (status2 != 0 || status1 == 0) {
                                return 0;
                            }
                            return 1;
                        } else if (status1 > status2) {
                            return 1;
                        } else {
                            if (status1 < status2) {
                                return -1;
                            }
                            return 0;
                        }
                    } else if (status1 > status2) {
                        return 1;
                    } else {
                        if (status1 < status2) {
                            return -1;
                        }
                        return 0;
                    }
                }
            }

            /* renamed from: org.telegram.ui.ChannelUsersActivity.6.1.2 */
            class C10722 implements Comparator<ChannelParticipant> {
                C10722() {
                }

                public int compare(ChannelParticipant lhs, ChannelParticipant rhs) {
                    int type1 = ChannelUsersActivity.this.getChannelAdminParticipantType(lhs);
                    int type2 = ChannelUsersActivity.this.getChannelAdminParticipantType(rhs);
                    if (type1 > type2) {
                        return 1;
                    }
                    if (type1 < type2) {
                        return -1;
                    }
                    return 0;
                }
            }

            C10731(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_channels_channelParticipants res = this.val$response;
                    MessagesController.getInstance().putUsers(res.users, false);
                    ChannelUsersActivity.this.participants = res.participants;
                    try {
                        if (ChannelUsersActivity.this.type == 0 || ChannelUsersActivity.this.type == 2) {
                            Collections.sort(ChannelUsersActivity.this.participants, new C10711());
                        } else if (ChannelUsersActivity.this.type == 1) {
                            Collections.sort(res.participants, new C10722());
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
                ChannelUsersActivity.this.loadingUsers = false;
                ChannelUsersActivity.this.firstLoaded = true;
                if (ChannelUsersActivity.this.emptyView != null) {
                    ChannelUsersActivity.this.emptyView.showTextView();
                }
                if (ChannelUsersActivity.this.listViewAdapter != null) {
                    ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                }
            }
        }

        C17956() {
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C10731(error, response));
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            boolean z = true;
            if (ChannelUsersActivity.this.type == 2) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isPublic) {
                        if (i == 0) {
                            return true;
                        }
                        if (i == 1) {
                            return false;
                        }
                    } else if (i == 0 || i == 1) {
                        return true;
                    } else {
                        if (i == 2) {
                            return false;
                        }
                    }
                }
            } else if (ChannelUsersActivity.this.type == 1) {
                if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                    return ChannelUsersActivity.this.isAdmin;
                }
                if (i == (ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) + 1) {
                    return false;
                }
                if (ChannelUsersActivity.this.isMegagroup && ChannelUsersActivity.this.isAdmin && i < 4) {
                    boolean z2 = i == 1 || i == 2;
                    return z2;
                }
            }
            if (i == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow || ((ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow)).user_id == UserConfig.getClientUserId()) {
                z = false;
            }
            return z;
        }

        public int getCount() {
            int i = 1;
            if (ChannelUsersActivity.this.participants.isEmpty() && ChannelUsersActivity.this.type == 0) {
                return 0;
            }
            if (ChannelUsersActivity.this.loadingUsers && !ChannelUsersActivity.this.firstLoaded) {
                return 0;
            }
            if (ChannelUsersActivity.this.type != 1) {
                return (ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) + 1;
            }
            int size = ChannelUsersActivity.this.participants.size();
            if (ChannelUsersActivity.this.isAdmin) {
                i = 2;
            }
            size += i;
            i = (ChannelUsersActivity.this.isAdmin && ChannelUsersActivity.this.isMegagroup) ? 4 : 0;
            return size + i;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int viewType = getItemViewType(i);
            View userCell;
            if (viewType == 0) {
                if (view == null) {
                    userCell = new UserCell(this.mContext, 1, 0, false);
                    userCell.setBackgroundColor(-1);
                }
                UserCell userCell2 = (UserCell) view;
                ChannelParticipant participant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
                User user = MessagesController.getInstance().getUser(Integer.valueOf(participant.user_id));
                if (user == null) {
                    return view;
                }
                if (ChannelUsersActivity.this.type == 0) {
                    CharSequence string;
                    if (user.phone == null || user.phone.length() == 0) {
                        string = LocaleController.getString("NumberUnknown", C0691R.string.NumberUnknown);
                    } else {
                        string = PhoneFormat.getInstance().format("+" + user.phone);
                    }
                    userCell2.setData(user, null, string, 0);
                    return view;
                } else if (ChannelUsersActivity.this.type == 1) {
                    String role = null;
                    if ((participant instanceof TL_channelParticipantCreator) || (participant instanceof TL_channelParticipantSelf)) {
                        role = LocaleController.getString("ChannelCreator", C0691R.string.ChannelCreator);
                    } else if (participant instanceof TL_channelParticipantModerator) {
                        role = LocaleController.getString("ChannelModerator", C0691R.string.ChannelModerator);
                    } else if (participant instanceof TL_channelParticipantEditor) {
                        role = LocaleController.getString("ChannelEditor", C0691R.string.ChannelEditor);
                    }
                    userCell2.setData(user, null, role, 0);
                    return view;
                } else if (ChannelUsersActivity.this.type != 2) {
                    return view;
                } else {
                    userCell2.setData(user, null, null, 0);
                    return view;
                }
            } else if (viewType == 1) {
                if (view == null) {
                    userCell = new TextInfoPrivacyCell(this.mContext);
                }
                if (ChannelUsersActivity.this.type == 0) {
                    ((TextInfoPrivacyCell) view).setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedGroup", C0691R.string.NoBlockedGroup), LocaleController.getString("UnblockText", C0691R.string.UnblockText)}));
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                    return view;
                } else if (ChannelUsersActivity.this.type == 1) {
                    if (!ChannelUsersActivity.this.isAdmin) {
                        ((TextInfoPrivacyCell) view).setText(TtmlNode.ANONYMOUS_REGION_ID);
                        view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                        return view;
                    } else if (ChannelUsersActivity.this.isMegagroup) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("MegaAdminsInfo", C0691R.string.MegaAdminsInfo));
                        view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                        return view;
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("ChannelAdminsInfo", C0691R.string.ChannelAdminsInfo));
                        view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                        return view;
                    }
                } else if (ChannelUsersActivity.this.type != 2) {
                    return view;
                } else {
                    if (((ChannelUsersActivity.this.isPublic || i != 2) && i != 1) || !ChannelUsersActivity.this.isAdmin) {
                        ((TextInfoPrivacyCell) view).setText(TtmlNode.ANONYMOUS_REGION_ID);
                        view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                        return view;
                    }
                    if (ChannelUsersActivity.this.isMegagroup) {
                        ((TextInfoPrivacyCell) view).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("ChannelMembersInfo", C0691R.string.ChannelMembersInfo));
                    }
                    view.setBackgroundResource(C0691R.drawable.greydivider);
                    return view;
                }
            } else if (viewType == 2) {
                if (view == null) {
                    userCell = new TextSettingsCell(this.mContext);
                    userCell.setBackgroundColor(-1);
                }
                TextSettingsCell actionCell = (TextSettingsCell) view;
                if (ChannelUsersActivity.this.type == 2) {
                    if (i == 0) {
                        actionCell.setText(LocaleController.getString("AddMember", C0691R.string.AddMember), true);
                        return view;
                    } else if (i != 1) {
                        return view;
                    } else {
                        actionCell.setText(LocaleController.getString("ChannelInviteViaLink", C0691R.string.ChannelInviteViaLink), false);
                        return view;
                    }
                } else if (ChannelUsersActivity.this.type != 1) {
                    return view;
                } else {
                    actionCell.setTextAndIcon(LocaleController.getString("ChannelAddAdmin", C0691R.string.ChannelAddAdmin), C0691R.drawable.managers, false);
                    return view;
                }
            } else if (viewType == 3) {
                if (view == null) {
                    return new ShadowSectionCell(this.mContext);
                }
                return view;
            } else if (viewType == 4) {
                if (view == null) {
                    userCell = new TextCell(this.mContext);
                    userCell.setBackgroundColor(-1);
                }
                ((TextCell) view).setTextAndIcon(LocaleController.getString("ChannelAddAdmin", C0691R.string.ChannelAddAdmin), C0691R.drawable.managers);
                return view;
            } else if (viewType == 5) {
                if (view == null) {
                    userCell = new HeaderCell(this.mContext);
                    userCell.setBackgroundColor(-1);
                }
                ((HeaderCell) view).setText(LocaleController.getString("WhoCanAddMembers", C0691R.string.WhoCanAddMembers));
                return view;
            } else if (viewType != 6) {
                return view;
            } else {
                if (view == null) {
                    userCell = new RadioCell(this.mContext);
                    userCell.setBackgroundColor(-1);
                }
                RadioCell radioCell = (RadioCell) view;
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                String string2;
                boolean z;
                if (i == 1) {
                    radioCell.setTag(Integer.valueOf(0));
                    string2 = LocaleController.getString("WhoCanAddMembersAllMembers", C0691R.string.WhoCanAddMembersAllMembers);
                    z = chat != null && chat.democracy;
                    radioCell.setText(string2, z, true);
                    return view;
                } else if (i != 2) {
                    return view;
                } else {
                    radioCell.setTag(Integer.valueOf(1));
                    string2 = LocaleController.getString("WhoCanAddMembersAdmins", C0691R.string.WhoCanAddMembersAdmins);
                    z = (chat == null || chat.democracy) ? false : true;
                    radioCell.setText(string2, z, false);
                    return view;
                }
            }
        }

        public int getItemViewType(int i) {
            if (ChannelUsersActivity.this.type == 1) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isMegagroup) {
                        if (i == 0) {
                            return 5;
                        }
                        if (i == 1 || i == 2) {
                            return 6;
                        }
                        if (i == 3) {
                            return 3;
                        }
                    }
                    if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                        return 4;
                    }
                    if (i == (ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) + 1) {
                        return 1;
                    }
                }
            } else if (ChannelUsersActivity.this.type == 2 && ChannelUsersActivity.this.isAdmin) {
                if (ChannelUsersActivity.this.isPublic) {
                    if (i == 0) {
                        return 2;
                    }
                    if (i == 1) {
                        return 1;
                    }
                } else if (i == 0 || i == 1) {
                    return 2;
                } else {
                    if (i == 2) {
                        return 1;
                    }
                }
            }
            if (i == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                return 1;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 7;
        }

        public boolean isEmpty() {
            return getCount() == 0 || (ChannelUsersActivity.this.participants.isEmpty() && ChannelUsersActivity.this.loadingUsers);
        }
    }

    public ChannelUsersActivity(Bundle args) {
        int i = 0;
        super(args);
        this.participants = new ArrayList();
        this.chatId = this.arguments.getInt("chat_id");
        this.type = this.arguments.getInt("type");
        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (chat != null) {
            if (chat.creator) {
                this.isAdmin = true;
                this.isPublic = (chat.flags & 64) != 0;
            }
            this.isMegagroup = chat.megagroup;
        }
        if (this.type == 0) {
            this.participantsStartRow = 0;
        } else if (this.type == 1) {
            if (this.isAdmin && this.isMegagroup) {
                i = 4;
            }
            this.participantsStartRow = i;
        } else if (this.type == 2) {
            if (this.isAdmin) {
                i = this.isPublic ? 2 : 3;
            }
            this.participantsStartRow = i;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlockedUsers", C0691R.string.ChannelBlockedUsers));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", C0691R.string.ChannelAdministrators));
        } else if (this.type == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelMembers", C0691R.string.ChannelMembers));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C17901());
        ActionBarMenu menu = this.actionBar.createMenu();
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(-986896);
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        if (this.type == 0) {
            if (this.isMegagroup) {
                this.emptyView.setText(LocaleController.getString("NoBlockedGroup", C0691R.string.NoBlockedGroup));
            } else {
                this.emptyView.setText(LocaleController.getString("NoBlocked", C0691R.string.NoBlocked));
            }
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        ListView listView = new ListView(context);
        listView.setEmptyView(this.emptyView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setDrawSelectorOnTop(true);
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        if (!LocaleController.isRTL) {
            i = 2;
        }
        listView.setVerticalScrollbarPosition(i);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        listView.setOnItemClickListener(new C10642(listView));
        if (this.isAdmin || (this.isMegagroup && this.type == 0)) {
            listView.setOnItemLongClickListener(new C10673());
        }
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    public void setUserChannelRole(User user, ChannelParticipantRole role) {
        if (user != null && role != null) {
            TL_channels_editAdmin req = new TL_channels_editAdmin();
            req.channel = MessagesController.getInputChannel(this.chatId);
            req.user_id = MessagesController.getInputUser(user);
            req.role = role;
            ConnectionsManager.getInstance().sendRequest(req, new C17944());
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded && args[0].id == this.chatId) {
            AndroidUtilities.runOnUIThread(new C10705());
        }
    }

    private int getChannelAdminParticipantType(ChannelParticipant participant) {
        if ((participant instanceof TL_channelParticipantCreator) || (participant instanceof TL_channelParticipantSelf)) {
            return 0;
        }
        if (participant instanceof TL_channelParticipantEditor) {
            return 1;
        }
        return 2;
    }

    private void getChannelParticipants(int offset, int count) {
        if (!this.loadingUsers) {
            this.loadingUsers = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInputChannel(this.chatId);
            if (this.type == 0) {
                req.filter = new TL_channelParticipantsKicked();
            } else if (this.type == 1) {
                req.filter = new TL_channelParticipantsAdmins();
            } else if (this.type == 2) {
                req.filter = new TL_channelParticipantsRecent();
            }
            req.offset = offset;
            req.limit = count;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new C17956()), this.classGuid);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }
}
