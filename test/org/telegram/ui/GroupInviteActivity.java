package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

public class GroupInviteActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int copyLinkRow;
    private ExportedChatInvite invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    /* renamed from: org.telegram.ui.GroupInviteActivity.2 */
    class C12472 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.GroupInviteActivity.2.1 */
        class C12461 implements OnClickListener {
            C12461() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                GroupInviteActivity.this.generateLink(true);
            }
        }

        C12472() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (GroupInviteActivity.this.getParentActivity() != null) {
                if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.linkRow) {
                    if (GroupInviteActivity.this.invite != null) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", GroupInviteActivity.this.invite.link));
                            Toast.makeText(GroupInviteActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0691R.string.LinkCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    if (GroupInviteActivity.this.invite != null) {
                        try {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            intent.putExtra("android.intent.extra.TEXT", GroupInviteActivity.this.invite.link);
                            GroupInviteActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", C0691R.string.InviteToGroupByLink)), 500);
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                        }
                    }
                } else if (i == GroupInviteActivity.this.revokeLinkRow) {
                    Builder builder = new Builder(GroupInviteActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlert", C0691R.string.RevokeAlert));
                    builder.setTitle(LocaleController.getString("RevokeLink", C0691R.string.RevokeLink));
                    builder.setPositiveButton(LocaleController.getString("RevokeButton", C0691R.string.RevokeButton), new C12461());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    GroupInviteActivity.this.showDialog(builder.create());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupInviteActivity.1 */
    class C18621 extends ActionBarMenuOnItemClick {
        C18621() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupInviteActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupInviteActivity.3 */
    class C18633 implements RequestDelegate {
        final /* synthetic */ boolean val$newRequest;

        /* renamed from: org.telegram.ui.GroupInviteActivity.3.1 */
        class C12481 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C12481(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    GroupInviteActivity.this.invite = (ExportedChatInvite) this.val$response;
                    if (C18633.this.val$newRequest) {
                        if (GroupInviteActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(GroupInviteActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("RevokeAlertNewLink", C0691R.string.RevokeAlertNewLink));
                            builder.setTitle(LocaleController.getString("RevokeLink", C0691R.string.RevokeLink));
                            builder.setNegativeButton(LocaleController.getString("OK", C0691R.string.OK), null);
                            GroupInviteActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    }
                }
                GroupInviteActivity.this.loading = false;
                GroupInviteActivity.this.listAdapter.notifyDataSetChanged();
            }
        }

        C18633(boolean z) {
            this.val$newRequest = z;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C12481(error, response));
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
            return i == GroupInviteActivity.this.revokeLinkRow || i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.linkRow;
        }

        public int getCount() {
            return GroupInviteActivity.this.loading ? 0 : GroupInviteActivity.this.rowCount;
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
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == GroupInviteActivity.this.copyLinkRow) {
                    textCell.setText(LocaleController.getString("CopyLink", C0691R.string.CopyLink), true);
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    textCell.setText(LocaleController.getString("ShareLink", C0691R.string.ShareLink), false);
                } else if (i == GroupInviteActivity.this.revokeLinkRow) {
                    textCell.setText(LocaleController.getString("RevokeLink", C0691R.string.RevokeLink), true);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == GroupInviteActivity.this.shadowRow) {
                    ((TextInfoPrivacyCell) view).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                } else if (i == GroupInviteActivity.this.linkInfoRow) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("LinkInfo", C0691R.string.LinkInfo));
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("ChannelLinkInfo", C0691R.string.ChannelLinkInfo));
                    }
                    view.setBackgroundResource(C0691R.drawable.greydivider);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextBlockCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                ((TextBlockCell) view).setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : C2DMBaseReceiver.EXTRA_ERROR, false);
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (i == GroupInviteActivity.this.shadowRow || i == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            if (i == GroupInviteActivity.this.linkRow) {
                return 2;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isEmpty() {
            return GroupInviteActivity.this.loading;
        }
    }

    public GroupInviteActivity(int cid) {
        this.chat_id = cid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.linkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.linkInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.copyLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.revokeLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shadowRow = i;
        return true;
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", C0691R.string.InviteLink));
        this.actionBar.setActionBarMenuOnItemClick(new C18621());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(-986896);
        FrameLayout progressView = new FrameLayout(context);
        frameLayout.addView(progressView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        progressView.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setEmptyView(progressView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1, 51));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C12472());
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull info = args[0];
            int guid = ((Integer) args[1]).intValue();
            if (info.id == this.chat_id && guid == this.classGuid) {
                this.invite = MessagesController.getInstance().getExportedInvite(this.chat_id);
                if (this.invite instanceof TL_chatInviteExported) {
                    this.loading = false;
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                generateLink(false);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void generateLink(boolean newRequest) {
        TLObject request;
        this.loading = true;
        TLObject req;
        if (ChatObject.isChannel(this.chat_id)) {
            req = new TL_channels_exportInvite();
            req.channel = MessagesController.getInputChannel(this.chat_id);
            request = req;
        } else {
            req = new TL_messages_exportChatInvite();
            req.chat_id = this.chat_id;
            request = req;
        }
        ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(request, new C18633(newRequest)), this.classGuid);
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
