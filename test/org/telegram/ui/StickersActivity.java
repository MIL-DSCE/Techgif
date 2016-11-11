package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VideoPlayer;

public class StickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean needReorder;
    private int rowCount;
    private int stickersEndRow;
    private int stickersInfoRow;
    private int stickersStartRow;

    /* renamed from: org.telegram.ui.StickersActivity.1 */
    class C19501 extends ActionBarMenuOnItemClick {
        C19501() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                StickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.StickersActivity.2 */
    class C19512 implements OnItemClickListener {
        C19512() {
        }

        public void onItemClick(View view, int position) {
            if (position >= StickersActivity.this.stickersStartRow && position < StickersActivity.this.stickersEndRow && StickersActivity.this.getParentActivity() != null) {
                StickersActivity.this.sendReorder();
                TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) StickersQuery.getStickerSets().get(position);
                ArrayList<Document> stickers = stickerSet.documents;
                if (stickers != null && !stickers.isEmpty()) {
                    StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), null, stickerSet, null));
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.StickersActivity.3 */
    class C19523 implements RequestDelegate {
        C19523() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.StickersActivity.ListAdapter.1 */
        class C14561 implements OnClickListener {

            /* renamed from: org.telegram.ui.StickersActivity.ListAdapter.1.1 */
            class C14551 implements DialogInterface.OnClickListener {
                final /* synthetic */ int[] val$options;
                final /* synthetic */ TL_messages_stickerSet val$stickerSet;

                C14551(int[] iArr, TL_messages_stickerSet tL_messages_stickerSet) {
                    this.val$options = iArr;
                    this.val$stickerSet = tL_messages_stickerSet;
                }

                public void onClick(DialogInterface dialog, int which) {
                    ListAdapter.this.processSelectionOption(this.val$options[which], this.val$stickerSet);
                }
            }

            C14561() {
            }

            public void onClick(View v) {
                int[] options;
                CharSequence[] items;
                StickersActivity.this.sendReorder();
                TL_messages_stickerSet stickerSet = ((StickerSetCell) v.getParent()).getStickersSet();
                Builder builder = new Builder(StickersActivity.this.getParentActivity());
                builder.setTitle(stickerSet.set.title);
                if (stickerSet.set.official) {
                    String string;
                    options = new int[]{0};
                    items = new CharSequence[1];
                    if (stickerSet.set.disabled) {
                        string = LocaleController.getString("StickersShow", C0691R.string.StickersShow);
                    } else {
                        string = LocaleController.getString("StickersHide", C0691R.string.StickersHide);
                    }
                    items[0] = string;
                } else {
                    options = new int[]{0, 1, 2, 3};
                    items = new CharSequence[4];
                    items[0] = !stickerSet.set.disabled ? LocaleController.getString("StickersHide", C0691R.string.StickersHide) : LocaleController.getString("StickersShow", C0691R.string.StickersShow);
                    items[1] = LocaleController.getString("StickersRemove", C0691R.string.StickersRemove);
                    items[2] = LocaleController.getString("StickersShare", C0691R.string.StickersShare);
                    items[3] = LocaleController.getString("StickersCopy", C0691R.string.StickersCopy);
                }
                builder.setItems(items, new C14551(options, stickerSet));
                StickersActivity.this.showDialog(builder.create());
            }
        }

        /* renamed from: org.telegram.ui.StickersActivity.ListAdapter.2 */
        class C19532 extends URLSpanNoUnderline {
            C19532(String x0) {
                super(x0);
            }

            public void onClick(View widget) {
                MessagesController.openByUserName("stickers", StickersActivity.this, 1);
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

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return ((TL_messages_stickerSet) StickersQuery.getStickerSets().get(i)).set.id;
            }
            if (i == StickersActivity.this.stickersInfoRow) {
                return -2147483648L;
            }
            return (long) i;
        }

        private void processSelectionOption(int which, TL_messages_stickerSet stickerSet) {
            int i = 1;
            if (which == 0) {
                Context parentActivity = StickersActivity.this.getParentActivity();
                StickerSet stickerSet2 = stickerSet.set;
                if (stickerSet.set.disabled) {
                    i = 2;
                }
                StickersQuery.removeStickersSet(parentActivity, stickerSet2, i);
            } else if (which == 1) {
                StickersQuery.removeStickersSet(StickersActivity.this.getParentActivity(), stickerSet.set, 0);
            } else if (which == 2) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{stickerSet.set.short_name}));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", C0691R.string.StickersShare)), 500);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            } else if (which == 3) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{stickerSet.set.short_name})));
                    Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0691R.string.LinkCopied), 0).show();
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ArrayList<TL_messages_stickerSet> arrayList = StickersQuery.getStickerSets();
                ((StickerSetCell) holder.itemView).setStickersSet((TL_messages_stickerSet) arrayList.get(position), position != arrayList.size() + -1);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new StickerSetCell(this.mContext);
                    view.setBackgroundColor(-1);
                    view.setBackgroundResource(C0691R.drawable.list_selector_white);
                    ((StickerSetCell) view).setOnOptionsClick(new C14561());
                    break;
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    view = new TextInfoPrivacyCell(this.mContext);
                    String text = LocaleController.getString("StickersInfo", C0691R.string.StickersInfo);
                    String botName = "@stickers";
                    int index = text.indexOf(botName);
                    if (index != -1) {
                        try {
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                            stringBuilder.setSpan(new C19532("@stickers"), index, botName.length() + index, 18);
                            ((TextInfoPrivacyCell) view).setText(stringBuilder);
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                            ((TextInfoPrivacyCell) view).setText(text);
                        }
                    } else {
                        ((TextInfoPrivacyCell) view).setText(text);
                    }
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int i) {
            if ((i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) && i == StickersActivity.this.stickersInfoRow) {
                return 1;
            }
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            if (fromIndex != toIndex) {
                StickersActivity.this.needReorder = true;
            }
            ArrayList<TL_messages_stickerSet> arrayList = StickersQuery.getStickerSets();
            TL_messages_stickerSet from = (TL_messages_stickerSet) arrayList.get(fromIndex);
            arrayList.set(fromIndex, arrayList.get(toIndex));
            arrayList.set(toIndex, from);
            notifyItemMoved(fromIndex, toIndex);
        }
    }

    public class TouchHelperCallback extends Callback {
        public static final float ALPHA_FULL = 1.0f;

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                return Callback.makeMovementFlags(0, 0);
            }
            return Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder source, ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        StickersQuery.checkStickers();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        sendReorder();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Stickers", C0691R.string.Stickers));
        this.actionBar.setActionBarMenuOnItemClick(new C19501());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(-986896);
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        this.listView.setTag(Integer.valueOf(7));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(1);
        this.listView.setLayoutManager(layoutManager);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C19512());
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.stickersDidLoaded) {
            updateRows();
        }
    }

    private void sendReorder() {
        if (this.needReorder) {
            StickersQuery.calcNewHash();
            this.needReorder = false;
            TL_messages_reorderStickerSets req = new TL_messages_reorderStickerSets();
            ArrayList<TL_messages_stickerSet> arrayList = StickersQuery.getStickerSets();
            for (int a = 0; a < arrayList.size(); a++) {
                req.order.add(Long.valueOf(((TL_messages_stickerSet) arrayList.get(a)).set.id));
            }
            ConnectionsManager.getInstance().sendRequest(req, new C19523());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TL_messages_stickerSet> stickerSets = StickersQuery.getStickerSets();
        if (stickerSets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
        } else {
            this.stickersStartRow = 0;
            this.stickersEndRow = stickerSets.size();
            this.rowCount += stickerSets.size();
        }
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.stickersInfoRow = i;
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
