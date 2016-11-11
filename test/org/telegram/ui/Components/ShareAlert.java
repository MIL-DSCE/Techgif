package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ShareAlert extends BottomSheet {
    private boolean copyLinkOnEnd;
    private LinearLayout doneButton;
    private TextView doneButtonBadgeTextView;
    private TextView doneButtonTextView;
    private TL_exportedMessageLink exportedMessageLink;
    private FrameLayout frameLayout;
    private RecyclerListView gridView;
    private boolean isPublicChannel;
    private GridLayoutManager layoutManager;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    private EditText nameTextView;
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private HashMap<Long, TL_dialog> selectedDialogs;
    private MessageObject sendingMessageObject;
    private View shadow;
    private Drawable shadowDrawable;
    private int topBeforeSwitch;

    /* renamed from: org.telegram.ui.Components.ShareAlert.2 */
    class C11762 extends FrameLayout {
        private boolean ignoreLayout;

        C11762(Context x0) {
            super(x0);
            this.ignoreLayout = false;
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) ShareAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(ev);
            }
            ShareAlert.this.dismiss();
            return true;
        }

        public boolean onTouchEvent(MotionEvent e) {
            return !ShareAlert.this.isDismissed() && super.onTouchEvent(e);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (VERSION.SDK_INT >= 21) {
                height -= AndroidUtilities.statusBarHeight;
            }
            int contentSize = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / 4.0f))) * AndroidUtilities.dp(100.0f))) + ShareAlert.backgroundPaddingTop;
            int padding = contentSize < height ? 0 : (height - ((height / 5) * 3)) + AndroidUtilities.dp(8.0f);
            if (ShareAlert.this.gridView.getPaddingTop() != padding) {
                this.ignoreLayout = true;
                ShareAlert.this.gridView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
                this.ignoreLayout = false;
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), C0747C.ENCODING_PCM_32BIT));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ShareAlert.this.updateLayout();
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        protected void onDraw(Canvas canvas) {
            ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
            ShareAlert.this.shadowDrawable.draw(canvas);
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.3 */
    class C11773 implements OnTouchListener {
        C11773() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.4 */
    class C11784 implements OnClickListener {
        C11784() {
        }

        public void onClick(View v) {
            if (ShareAlert.this.selectedDialogs.isEmpty() && ShareAlert.this.isPublicChannel) {
                if (ShareAlert.this.loadingLink) {
                    ShareAlert.this.copyLinkOnEnd = true;
                    Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", C0691R.string.Loading), 0).show();
                } else {
                    ShareAlert.this.copyLink(ShareAlert.this.getContext());
                }
                ShareAlert.this.dismiss();
                return;
            }
            ArrayList<MessageObject> arrayList = new ArrayList();
            arrayList.add(ShareAlert.this.sendingMessageObject);
            for (Entry<Long, TL_dialog> entry : ShareAlert.this.selectedDialogs.entrySet()) {
                SendMessagesHelper.getInstance().sendMessage(arrayList, ((Long) entry.getKey()).longValue());
            }
            ShareAlert.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.5 */
    class C11795 implements TextWatcher {
        C11795() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String text = ShareAlert.this.nameTextView.getText().toString();
            if (text.length() != 0) {
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                if (ShareAlert.this.searchEmptyView != null) {
                    ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", C0691R.string.NoResult));
                }
            } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                int top = ShareAlert.this.getCurrentTop();
                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", C0691R.string.NoChats));
                ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                ShareAlert.this.listAdapter.notifyDataSetChanged();
                if (top > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                }
            }
            if (ShareAlert.this.searchAdapter != null) {
                ShareAlert.this.searchAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.1 */
    class C18411 implements RequestDelegate {
        final /* synthetic */ Context val$context;

        /* renamed from: org.telegram.ui.Components.ShareAlert.1.1 */
        class C11751 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C11751(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response != null) {
                    ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) this.val$response;
                    if (ShareAlert.this.copyLinkOnEnd) {
                        ShareAlert.this.copyLink(C18411.this.val$context);
                    }
                }
                ShareAlert.this.loadingLink = false;
            }
        }

        C18411(Context context) {
            this.val$context = context;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C11751(response));
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.6 */
    class C18426 extends ItemDecoration {
        C18426() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int i = 0;
            Holder holder = (Holder) parent.getChildViewHolder(view);
            if (holder != null) {
                int pos = holder.getAdapterPosition();
                outRect.left = pos % 4 == 0 ? 0 : AndroidUtilities.dp(4.0f);
                if (pos % 4 != 3) {
                    i = AndroidUtilities.dp(4.0f);
                }
                outRect.right = i;
                return;
            }
            outRect.left = AndroidUtilities.dp(4.0f);
            outRect.right = AndroidUtilities.dp(4.0f);
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.7 */
    class C18437 implements OnItemClickListener {
        C18437() {
        }

        public void onItemClick(View view, int position) {
            TL_dialog dialog;
            if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {
                dialog = ShareAlert.this.listAdapter.getItem(position);
            } else {
                dialog = ShareAlert.this.searchAdapter.getItem(position);
            }
            if (dialog != null) {
                ShareDialogCell cell = (ShareDialogCell) view;
                if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(dialog.id))) {
                    ShareAlert.this.selectedDialogs.remove(Long.valueOf(dialog.id));
                    cell.setChecked(false, true);
                } else {
                    ShareAlert.this.selectedDialogs.put(Long.valueOf(dialog.id), dialog);
                    cell.setChecked(true, true);
                }
                ShareAlert.this.updateSelectedCount();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert.8 */
    class C18448 extends OnScrollListener {
        C18448() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ShareAlert.this.updateLayout();
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    private class ShareDialogsAdapter extends Adapter {
        private Context context;
        private int currentCount;
        private ArrayList<TL_dialog> dialogs;

        public ShareDialogsAdapter(Context context) {
            this.dialogs = new ArrayList();
            this.context = context;
            for (int a = 0; a < MessagesController.getInstance().dialogsServerOnly.size(); a++) {
                TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogsServerOnly.get(a);
                int lower_id = (int) dialog.id;
                int high_id = (int) (dialog.id >> 32);
                if (!(lower_id == 0 || high_id == 1)) {
                    if (lower_id > 0) {
                        this.dialogs.add(dialog);
                    } else {
                        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                        if (!(chat == null || ChatObject.isNotInChat(chat) || (ChatObject.isChannel(chat) && !chat.creator && !chat.editor && !chat.megagroup))) {
                            this.dialogs.add(dialog);
                        }
                    }
                }
            }
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public TL_dialog getItem(int i) {
            if (i < 0 || i >= this.dialogs.size()) {
                return null;
            }
            return (TL_dialog) this.dialogs.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ShareDialogCell(this.context);
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ShareDialogCell cell = holder.itemView;
            TL_dialog dialog = getItem(position);
            cell.setDialog((int) dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(dialog.id)), null);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public class ShareSearchAdapter extends Adapter {
        private Context context;
        private int lastReqId;
        private int lastSearchId;
        private String lastSearchText;
        private int reqId;
        private ArrayList<DialogSearchResult> searchResult;
        private Timer searchTimer;

        /* renamed from: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.1 */
        class C11811 implements Runnable {
            final /* synthetic */ String val$query;
            final /* synthetic */ int val$searchId;

            /* renamed from: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.1.1 */
            class C11801 implements Comparator<DialogSearchResult> {
                C11801() {
                }

                public int compare(DialogSearchResult lhs, DialogSearchResult rhs) {
                    if (lhs.date < rhs.date) {
                        return 1;
                    }
                    if (lhs.date > rhs.date) {
                        return -1;
                    }
                    return 0;
                }
            }

            C11811(String str, int i) {
                this.val$query = str;
                this.val$searchId = i;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r39 = this;
                r0 = r39;
                r0 = r0.val$query;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r33 = r33.trim();	 Catch:{ Exception -> 0x00eb }
                r24 = r33.toLowerCase();	 Catch:{ Exception -> 0x00eb }
                r33 = r24.length();	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0038;
            L_0x0014:
                r0 = r39;
                r0 = org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.this;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = -1;
                r33.lastSearchId = r34;	 Catch:{ Exception -> 0x00eb }
                r0 = r39;
                r0 = org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.this;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00eb }
                r34.<init>();	 Catch:{ Exception -> 0x00eb }
                r0 = r39;
                r0 = org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.this;	 Catch:{ Exception -> 0x00eb }
                r35 = r0;
                r35 = r35.lastSearchId;	 Catch:{ Exception -> 0x00eb }
                r33.updateSearchResults(r34, r35);	 Catch:{ Exception -> 0x00eb }
            L_0x0037:
                return;
            L_0x0038:
                r33 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r24;
                r25 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r24.equals(r25);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0050;
            L_0x004a:
                r33 = r25.length();	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0052;
            L_0x0050:
                r25 = 0;
            L_0x0052:
                if (r25 == 0) goto L_0x00f5;
            L_0x0054:
                r33 = 1;
            L_0x0056:
                r33 = r33 + 1;
                r0 = r33;
                r0 = new java.lang.String[r0];	 Catch:{ Exception -> 0x00eb }
                r23 = r0;
                r33 = 0;
                r23[r33] = r24;	 Catch:{ Exception -> 0x00eb }
                if (r25 == 0) goto L_0x0068;
            L_0x0064:
                r33 = 1;
                r23[r33] = r25;	 Catch:{ Exception -> 0x00eb }
            L_0x0068:
                r32 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00eb }
                r32.<init>();	 Catch:{ Exception -> 0x00eb }
                r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00eb }
                r7.<init>();	 Catch:{ Exception -> 0x00eb }
                r22 = 0;
                r11 = new java.util.HashMap;	 Catch:{ Exception -> 0x00eb }
                r11.<init>();	 Catch:{ Exception -> 0x00eb }
                r33 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00eb }
                r33 = r33.getDatabase();	 Catch:{ Exception -> 0x00eb }
                r34 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400";
                r35 = 0;
                r0 = r35;
                r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x00eb }
                r35 = r0;
                r8 = r33.queryFinalized(r34, r35);	 Catch:{ Exception -> 0x00eb }
            L_0x008f:
                r33 = r8.next();	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x011a;
            L_0x0095:
                r33 = 0;
                r0 = r33;
                r16 = r8.longValue(r0);	 Catch:{ Exception -> 0x00eb }
                r10 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x00eb }
                r0 = r39;
                r0 = org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.this;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = 0;
                r0 = r33;
                r1 = r34;
                r10.<init>(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = 1;
                r0 = r33;
                r33 = r8.intValue(r0);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10.date = r0;	 Catch:{ Exception -> 0x00eb }
                r33 = java.lang.Long.valueOf(r16);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r11.put(r0, r10);	 Catch:{ Exception -> 0x00eb }
                r0 = r16;
                r0 = (int) r0;	 Catch:{ Exception -> 0x00eb }
                r19 = r0;
                r33 = 32;
                r34 = r16 >> r33;
                r0 = r34;
                r14 = (int) r0;	 Catch:{ Exception -> 0x00eb }
                if (r19 == 0) goto L_0x008f;
            L_0x00d1:
                r33 = 1;
                r0 = r33;
                if (r14 == r0) goto L_0x008f;
            L_0x00d7:
                if (r19 <= 0) goto L_0x00f9;
            L_0x00d9:
                r33 = java.lang.Integer.valueOf(r19);	 Catch:{ Exception -> 0x00eb }
                r33 = r32.contains(r33);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x008f;
            L_0x00e3:
                r33 = java.lang.Integer.valueOf(r19);	 Catch:{ Exception -> 0x00eb }
                r32.add(r33);	 Catch:{ Exception -> 0x00eb }
                goto L_0x008f;
            L_0x00eb:
                r12 = move-exception;
                r33 = "tmessages";
                r0 = r33;
                org.telegram.messenger.FileLog.m13e(r0, r12);
                goto L_0x0037;
            L_0x00f5:
                r33 = 0;
                goto L_0x0056;
            L_0x00f9:
                r0 = r19;
                r0 = -r0;
                r33 = r0;
                r33 = java.lang.Integer.valueOf(r33);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r33 = r7.contains(r0);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x008f;
            L_0x010a:
                r0 = r19;
                r0 = -r0;
                r33 = r0;
                r33 = java.lang.Integer.valueOf(r33);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r7.add(r0);	 Catch:{ Exception -> 0x00eb }
                goto L_0x008f;
            L_0x011a:
                r8.dispose();	 Catch:{ Exception -> 0x00eb }
                r33 = r32.isEmpty();	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x02dd;
            L_0x0123:
                r33 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00eb }
                r33 = r33.getDatabase();	 Catch:{ Exception -> 0x00eb }
                r34 = java.util.Locale.US;	 Catch:{ Exception -> 0x00eb }
                r35 = "SELECT data, status, name FROM users WHERE uid IN(%s)";
                r36 = 1;
                r0 = r36;
                r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x00eb }
                r36 = r0;
                r37 = 0;
                r38 = ",";
                r0 = r38;
                r1 = r32;
                r38 = android.text.TextUtils.join(r0, r1);	 Catch:{ Exception -> 0x00eb }
                r36[r37] = r38;	 Catch:{ Exception -> 0x00eb }
                r34 = java.lang.String.format(r34, r35, r36);	 Catch:{ Exception -> 0x00eb }
                r35 = 0;
                r0 = r35;
                r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x00eb }
                r35 = r0;
                r8 = r33.queryFinalized(r34, r35);	 Catch:{ Exception -> 0x00eb }
            L_0x0155:
                r33 = r8.next();	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x02da;
            L_0x015b:
                r33 = 2;
                r0 = r33;
                r20 = r8.stringValue(r0);	 Catch:{ Exception -> 0x00eb }
                r33 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r20;
                r27 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x00eb }
                r0 = r20;
                r1 = r27;
                r33 = r0.equals(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x017b;
            L_0x0179:
                r27 = 0;
            L_0x017b:
                r30 = 0;
                r33 = ";;;";
                r0 = r20;
                r1 = r33;
                r31 = r0.lastIndexOf(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = -1;
                r0 = r31;
                r1 = r33;
                if (r0 == r1) goto L_0x0199;
            L_0x018f:
                r33 = r31 + 3;
                r0 = r20;
                r1 = r33;
                r30 = r0.substring(r1);	 Catch:{ Exception -> 0x00eb }
            L_0x0199:
                r13 = 0;
                r5 = r23;
                r0 = r5.length;	 Catch:{ Exception -> 0x00eb }
                r18 = r0;
                r15 = 0;
            L_0x01a0:
                r0 = r18;
                if (r15 >= r0) goto L_0x0155;
            L_0x01a4:
                r21 = r5[r15];	 Catch:{ Exception -> 0x00eb }
                r33 = r20.startsWith(r21);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x01fa;
            L_0x01ac:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = " ";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r21;
                r33 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r0 = r20;
                r1 = r33;
                r33 = r0.contains(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x01fa;
            L_0x01cd:
                if (r27 == 0) goto L_0x028c;
            L_0x01cf:
                r0 = r27;
                r1 = r21;
                r33 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x01fa;
            L_0x01d9:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = " ";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r21;
                r33 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r0 = r27;
                r1 = r33;
                r33 = r0.contains(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x028c;
            L_0x01fa:
                r13 = 1;
            L_0x01fb:
                if (r13 == 0) goto L_0x02d6;
            L_0x01fd:
                r33 = 0;
                r0 = r33;
                r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x00eb }
                if (r9 == 0) goto L_0x0155;
            L_0x0207:
                r33 = 0;
                r0 = r33;
                r33 = r9.readInt32(r0);	 Catch:{ Exception -> 0x00eb }
                r34 = 0;
                r0 = r33;
                r1 = r34;
                r29 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x00eb }
                r9.reuse();	 Catch:{ Exception -> 0x00eb }
                r0 = r29;
                r0 = r0.id;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r33;
                r0 = (long) r0;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r33 = java.lang.Long.valueOf(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10 = r11.get(r0);	 Catch:{ Exception -> 0x00eb }
                r10 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x00eb }
                r0 = r29;
                r0 = r0.status;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 == 0) goto L_0x024f;
            L_0x023b:
                r0 = r29;
                r0 = r0.status;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = 1;
                r0 = r34;
                r34 = r8.intValue(r0);	 Catch:{ Exception -> 0x00eb }
                r0 = r34;
                r1 = r33;
                r1.expires = r0;	 Catch:{ Exception -> 0x00eb }
            L_0x024f:
                r33 = 1;
                r0 = r33;
                if (r13 != r0) goto L_0x029b;
            L_0x0255:
                r0 = r29;
                r0 = r0.first_name;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r29;
                r0 = r0.last_name;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r33;
                r1 = r34;
                r2 = r21;
                r33 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r2);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10.name = r0;	 Catch:{ Exception -> 0x00eb }
            L_0x026f:
                r0 = r29;
                r10.object = r0;	 Catch:{ Exception -> 0x00eb }
                r0 = r10.dialog;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r29;
                r0 = r0.id;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r0 = (long) r0;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r2 = r33;
                r2.id = r0;	 Catch:{ Exception -> 0x00eb }
                r22 = r22 + 1;
                goto L_0x0155;
            L_0x028c:
                if (r30 == 0) goto L_0x01fb;
            L_0x028e:
                r0 = r30;
                r1 = r21;
                r33 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x01fb;
            L_0x0298:
                r13 = 2;
                goto L_0x01fb;
            L_0x029b:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = "@";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r29;
                r0 = r0.username;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r34 = 0;
                r35 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r35.<init>();	 Catch:{ Exception -> 0x00eb }
                r36 = "@";
                r35 = r35.append(r36);	 Catch:{ Exception -> 0x00eb }
                r0 = r35;
                r1 = r21;
                r35 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r35 = r35.toString();	 Catch:{ Exception -> 0x00eb }
                r33 = org.telegram.messenger.AndroidUtilities.generateSearchName(r33, r34, r35);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10.name = r0;	 Catch:{ Exception -> 0x00eb }
                goto L_0x026f;
            L_0x02d6:
                r15 = r15 + 1;
                goto L_0x01a0;
            L_0x02da:
                r8.dispose();	 Catch:{ Exception -> 0x00eb }
            L_0x02dd:
                r33 = r7.isEmpty();	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x042b;
            L_0x02e3:
                r33 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00eb }
                r33 = r33.getDatabase();	 Catch:{ Exception -> 0x00eb }
                r34 = java.util.Locale.US;	 Catch:{ Exception -> 0x00eb }
                r35 = "SELECT data, name FROM chats WHERE uid IN(%s)";
                r36 = 1;
                r0 = r36;
                r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x00eb }
                r36 = r0;
                r37 = 0;
                r38 = ",";
                r0 = r38;
                r38 = android.text.TextUtils.join(r0, r7);	 Catch:{ Exception -> 0x00eb }
                r36[r37] = r38;	 Catch:{ Exception -> 0x00eb }
                r34 = java.lang.String.format(r34, r35, r36);	 Catch:{ Exception -> 0x00eb }
                r35 = 0;
                r0 = r35;
                r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x00eb }
                r35 = r0;
                r8 = r33.queryFinalized(r34, r35);	 Catch:{ Exception -> 0x00eb }
            L_0x0313:
                r33 = r8.next();	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x0428;
            L_0x0319:
                r33 = 1;
                r0 = r33;
                r20 = r8.stringValue(r0);	 Catch:{ Exception -> 0x00eb }
                r33 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r20;
                r27 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x00eb }
                r0 = r20;
                r1 = r27;
                r33 = r0.equals(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x0339;
            L_0x0337:
                r27 = 0;
            L_0x0339:
                r4 = 0;
            L_0x033a:
                r0 = r23;
                r0 = r0.length;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r33;
                if (r4 >= r0) goto L_0x0313;
            L_0x0343:
                r21 = r23[r4];	 Catch:{ Exception -> 0x00eb }
                r33 = r20.startsWith(r21);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0399;
            L_0x034b:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = " ";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r21;
                r33 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r0 = r20;
                r1 = r33;
                r33 = r0.contains(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0399;
            L_0x036c:
                if (r27 == 0) goto L_0x0424;
            L_0x036e:
                r0 = r27;
                r1 = r21;
                r33 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0399;
            L_0x0378:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = " ";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r21;
                r33 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r0 = r27;
                r1 = r33;
                r33 = r0.contains(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x0424;
            L_0x0399:
                r33 = 0;
                r0 = r33;
                r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x00eb }
                if (r9 == 0) goto L_0x0313;
            L_0x03a3:
                r33 = 0;
                r0 = r33;
                r33 = r9.readInt32(r0);	 Catch:{ Exception -> 0x00eb }
                r34 = 0;
                r0 = r33;
                r1 = r34;
                r6 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x00eb }
                r9.reuse();	 Catch:{ Exception -> 0x00eb }
                if (r6 == 0) goto L_0x0313;
            L_0x03ba:
                r33 = org.telegram.messenger.ChatObject.isNotInChat(r6);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0313;
            L_0x03c0:
                r33 = org.telegram.messenger.ChatObject.isChannel(r6);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x03d8;
            L_0x03c6:
                r0 = r6.creator;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 != 0) goto L_0x03d8;
            L_0x03cc:
                r0 = r6.editor;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 != 0) goto L_0x03d8;
            L_0x03d2:
                r0 = r6.megagroup;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 == 0) goto L_0x0313;
            L_0x03d8:
                r0 = r6.id;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r33;
                r0 = (long) r0;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r0 = -r0;
                r34 = r0;
                r33 = java.lang.Long.valueOf(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10 = r11.get(r0);	 Catch:{ Exception -> 0x00eb }
                r10 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x00eb }
                r0 = r6.title;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = 0;
                r0 = r33;
                r1 = r34;
                r2 = r21;
                r33 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r2);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10.name = r0;	 Catch:{ Exception -> 0x00eb }
                r10.object = r6;	 Catch:{ Exception -> 0x00eb }
                r0 = r10.dialog;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r6.id;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r0 = -r0;
                r34 = r0;
                r0 = r34;
                r0 = (long) r0;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r2 = r33;
                r2.id = r0;	 Catch:{ Exception -> 0x00eb }
                r22 = r22 + 1;
                goto L_0x0313;
            L_0x0424:
                r4 = r4 + 1;
                goto L_0x033a;
            L_0x0428:
                r8.dispose();	 Catch:{ Exception -> 0x00eb }
            L_0x042b:
                r26 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00eb }
                r0 = r26;
                r1 = r22;
                r0.<init>(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r11.values();	 Catch:{ Exception -> 0x00eb }
                r15 = r33.iterator();	 Catch:{ Exception -> 0x00eb }
            L_0x043c:
                r33 = r15.hasNext();	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x045a;
            L_0x0442:
                r10 = r15.next();	 Catch:{ Exception -> 0x00eb }
                r10 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x00eb }
                r0 = r10.object;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 == 0) goto L_0x043c;
            L_0x044e:
                r0 = r10.name;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 == 0) goto L_0x043c;
            L_0x0454:
                r0 = r26;
                r0.add(r10);	 Catch:{ Exception -> 0x00eb }
                goto L_0x043c;
            L_0x045a:
                r33 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x00eb }
                r33 = r33.getDatabase();	 Catch:{ Exception -> 0x00eb }
                r34 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid";
                r35 = 0;
                r0 = r35;
                r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x00eb }
                r35 = r0;
                r8 = r33.queryFinalized(r34, r35);	 Catch:{ Exception -> 0x00eb }
            L_0x0470:
                r33 = r8.next();	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x060b;
            L_0x0476:
                r33 = 3;
                r0 = r33;
                r28 = r8.intValue(r0);	 Catch:{ Exception -> 0x00eb }
                r0 = r28;
                r0 = (long) r0;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r33 = java.lang.Long.valueOf(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r33 = r11.containsKey(r0);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x0470;
            L_0x048f:
                r33 = 2;
                r0 = r33;
                r20 = r8.stringValue(r0);	 Catch:{ Exception -> 0x00eb }
                r33 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r20;
                r27 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x00eb }
                r0 = r20;
                r1 = r27;
                r33 = r0.equals(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x04af;
            L_0x04ad:
                r27 = 0;
            L_0x04af:
                r30 = 0;
                r33 = ";;;";
                r0 = r20;
                r1 = r33;
                r31 = r0.lastIndexOf(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = -1;
                r0 = r31;
                r1 = r33;
                if (r0 == r1) goto L_0x04cd;
            L_0x04c3:
                r33 = r31 + 3;
                r0 = r20;
                r1 = r33;
                r30 = r0.substring(r1);	 Catch:{ Exception -> 0x00eb }
            L_0x04cd:
                r13 = 0;
                r5 = r23;
                r0 = r5.length;	 Catch:{ Exception -> 0x00eb }
                r18 = r0;
                r15 = 0;
            L_0x04d4:
                r0 = r18;
                if (r15 >= r0) goto L_0x0470;
            L_0x04d8:
                r21 = r5[r15];	 Catch:{ Exception -> 0x00eb }
                r33 = r20.startsWith(r21);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x052e;
            L_0x04e0:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = " ";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r21;
                r33 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r0 = r20;
                r1 = r33;
                r33 = r0.contains(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x052e;
            L_0x0501:
                if (r27 == 0) goto L_0x05bd;
            L_0x0503:
                r0 = r27;
                r1 = r21;
                r33 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 != 0) goto L_0x052e;
            L_0x050d:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = " ";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r21;
                r33 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r0 = r27;
                r1 = r33;
                r33 = r0.contains(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x05bd;
            L_0x052e:
                r13 = 1;
            L_0x052f:
                if (r13 == 0) goto L_0x0607;
            L_0x0531:
                r33 = 0;
                r0 = r33;
                r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x00eb }
                if (r9 == 0) goto L_0x0470;
            L_0x053b:
                r33 = 0;
                r0 = r33;
                r33 = r9.readInt32(r0);	 Catch:{ Exception -> 0x00eb }
                r34 = 0;
                r0 = r33;
                r1 = r34;
                r29 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x00eb }
                r9.reuse();	 Catch:{ Exception -> 0x00eb }
                r10 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x00eb }
                r0 = r39;
                r0 = org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.this;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = 0;
                r0 = r33;
                r1 = r34;
                r10.<init>(r1);	 Catch:{ Exception -> 0x00eb }
                r0 = r29;
                r0 = r0.status;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                if (r33 == 0) goto L_0x057d;
            L_0x0569:
                r0 = r29;
                r0 = r0.status;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r34 = 1;
                r0 = r34;
                r34 = r8.intValue(r0);	 Catch:{ Exception -> 0x00eb }
                r0 = r34;
                r1 = r33;
                r1.expires = r0;	 Catch:{ Exception -> 0x00eb }
            L_0x057d:
                r0 = r10.dialog;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r29;
                r0 = r0.id;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r0 = (long) r0;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r34;
                r2 = r33;
                r2.id = r0;	 Catch:{ Exception -> 0x00eb }
                r0 = r29;
                r10.object = r0;	 Catch:{ Exception -> 0x00eb }
                r33 = 1;
                r0 = r33;
                if (r13 != r0) goto L_0x05cc;
            L_0x059c:
                r0 = r29;
                r0 = r0.first_name;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r29;
                r0 = r0.last_name;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r33;
                r1 = r34;
                r2 = r21;
                r33 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r2);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10.name = r0;	 Catch:{ Exception -> 0x00eb }
            L_0x05b6:
                r0 = r26;
                r0.add(r10);	 Catch:{ Exception -> 0x00eb }
                goto L_0x0470;
            L_0x05bd:
                if (r30 == 0) goto L_0x052f;
            L_0x05bf:
                r0 = r30;
                r1 = r21;
                r33 = r0.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
                if (r33 == 0) goto L_0x052f;
            L_0x05c9:
                r13 = 2;
                goto L_0x052f;
            L_0x05cc:
                r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r33.<init>();	 Catch:{ Exception -> 0x00eb }
                r34 = "@";
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r0 = r29;
                r0 = r0.username;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r33 = r33.append(r34);	 Catch:{ Exception -> 0x00eb }
                r33 = r33.toString();	 Catch:{ Exception -> 0x00eb }
                r34 = 0;
                r35 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00eb }
                r35.<init>();	 Catch:{ Exception -> 0x00eb }
                r36 = "@";
                r35 = r35.append(r36);	 Catch:{ Exception -> 0x00eb }
                r0 = r35;
                r1 = r21;
                r35 = r0.append(r1);	 Catch:{ Exception -> 0x00eb }
                r35 = r35.toString();	 Catch:{ Exception -> 0x00eb }
                r33 = org.telegram.messenger.AndroidUtilities.generateSearchName(r33, r34, r35);	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r10.name = r0;	 Catch:{ Exception -> 0x00eb }
                goto L_0x05b6;
            L_0x0607:
                r15 = r15 + 1;
                goto L_0x04d4;
            L_0x060b:
                r8.dispose();	 Catch:{ Exception -> 0x00eb }
                r33 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$1$1;	 Catch:{ Exception -> 0x00eb }
                r0 = r33;
                r1 = r39;
                r0.<init>();	 Catch:{ Exception -> 0x00eb }
                r0 = r26;
                r1 = r33;
                java.util.Collections.sort(r0, r1);	 Catch:{ Exception -> 0x00eb }
                r0 = r39;
                r0 = org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.this;	 Catch:{ Exception -> 0x00eb }
                r33 = r0;
                r0 = r39;
                r0 = r0.val$searchId;	 Catch:{ Exception -> 0x00eb }
                r34 = r0;
                r0 = r33;
                r1 = r26;
                r2 = r34;
                r0.updateSearchResults(r1, r2);	 Catch:{ Exception -> 0x00eb }
                goto L_0x0037;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.1.run():void");
            }
        }

        /* renamed from: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.2 */
        class C11822 implements Runnable {
            final /* synthetic */ ArrayList val$result;
            final /* synthetic */ int val$searchId;

            C11822(int i, ArrayList arrayList) {
                this.val$searchId = i;
                this.val$result = arrayList;
            }

            public void run() {
                if (this.val$searchId == ShareSearchAdapter.this.lastSearchId) {
                    boolean becomeEmpty;
                    boolean isEmpty;
                    for (int a = 0; a < this.val$result.size(); a++) {
                        DialogSearchResult obj = (DialogSearchResult) this.val$result.get(a);
                        if (obj.object instanceof User) {
                            MessagesController.getInstance().putUser(obj.object, true);
                        } else if (obj.object instanceof Chat) {
                            MessagesController.getInstance().putChat(obj.object, true);
                        }
                    }
                    if (ShareSearchAdapter.this.searchResult.isEmpty() || !this.val$result.isEmpty()) {
                        becomeEmpty = false;
                    } else {
                        becomeEmpty = true;
                    }
                    if (ShareSearchAdapter.this.searchResult.isEmpty() && this.val$result.isEmpty()) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                    }
                    if (becomeEmpty) {
                        ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    }
                    ShareSearchAdapter.this.searchResult = this.val$result;
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    if (!isEmpty && !becomeEmpty && ShareAlert.this.topBeforeSwitch > 0) {
                        ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                        ShareAlert.this.topBeforeSwitch = -1000;
                    }
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.3 */
        class C11833 extends TimerTask {
            final /* synthetic */ String val$query;
            final /* synthetic */ int val$searchId;

            C11833(String str, int i) {
                this.val$query = str;
                this.val$searchId = i;
            }

            public void run() {
                try {
                    cancel();
                    ShareSearchAdapter.this.searchTimer.cancel();
                    ShareSearchAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                ShareSearchAdapter.this.searchDialogsInternal(this.val$query, this.val$searchId);
            }
        }

        private class DialogSearchResult {
            public int date;
            public TL_dialog dialog;
            public CharSequence name;
            public TLObject object;

            private DialogSearchResult() {
                this.dialog = new TL_dialog();
            }
        }

        public ShareSearchAdapter(Context context) {
            this.searchResult = new ArrayList();
            this.reqId = 0;
            this.lastSearchId = 0;
            this.context = context;
        }

        private void searchDialogsInternal(String query, int searchId) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C11811(query, searchId));
        }

        private void updateSearchResults(ArrayList<DialogSearchResult> result, int searchId) {
            AndroidUtilities.runOnUIThread(new C11822(searchId, result));
        }

        public void searchDialogs(String query) {
            if (query == null || this.lastSearchText == null || !query.equals(this.lastSearchText)) {
                this.lastSearchText = query;
                try {
                    if (this.searchTimer != null) {
                        this.searchTimer.cancel();
                        this.searchTimer = null;
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                if (query == null || query.length() == 0) {
                    this.searchResult.clear();
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    notifyDataSetChanged();
                    return;
                }
                int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                this.searchTimer = new Timer();
                this.searchTimer.schedule(new C11833(query, searchId), 200, 300);
            }
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public TL_dialog getItem(int i) {
            return ((DialogSearchResult) this.searchResult.get(i)).dialog;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ShareDialogCell(this.context);
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            DialogSearchResult result = (DialogSearchResult) this.searchResult.get(position);
            holder.itemView.setDialog((int) result.dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(result.dialog.id)), result.name);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ShareAlert(Context context, MessageObject messageObject, boolean publicChannel) {
        super(context, true);
        this.selectedDialogs = new HashMap();
        this.shadowDrawable = context.getResources().getDrawable(C0691R.drawable.sheet_shadow);
        this.sendingMessageObject = messageObject;
        this.searchAdapter = new ShareSearchAdapter(context);
        this.isPublicChannel = publicChannel;
        if (publicChannel) {
            this.loadingLink = true;
            TL_channels_exportMessageLink req = new TL_channels_exportMessageLink();
            req.id = messageObject.getId();
            req.channel = MessagesController.getInputChannel(messageObject.messageOwner.to_id.channel_id);
            ConnectionsManager.getInstance().sendRequest(req, new C18411(context));
        }
        this.containerView = new C11762(context);
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setBackgroundColor(-1);
        this.frameLayout.setOnTouchListener(new C11773());
        this.doneButton = new LinearLayout(context);
        this.doneButton.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false));
        this.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButton.setOnClickListener(new C11784());
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(Theme.SHARE_SHEET_BADGE_TEXT_COLOR);
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundResource(C0691R.drawable.bluecounter);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(C0691R.drawable.search_share);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 19));
        this.nameTextView = new EditText(context);
        this.nameTextView.setHint(LocaleController.getString("ShareSendTo", C0691R.string.ShareSendTo));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(19);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setBackgroundDrawable(null);
        this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(16385);
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.nameTextView.setTextColor(Theme.SHARE_SHEET_EDIT_TEXT_COLOR);
        this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 48.0f, 2.0f, 96.0f, 0.0f));
        this.nameTextView.addTextChangedListener(new C11795());
        this.gridView = new RecyclerListView(context);
        this.gridView.setTag(Integer.valueOf(13));
        this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        this.gridView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.gridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new C18426());
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        recyclerListView = this.gridView;
        Adapter shareDialogsAdapter = new ShareDialogsAdapter(context);
        this.listAdapter = shareDialogsAdapter;
        recyclerListView.setAdapter(shareDialogsAdapter);
        this.gridView.setGlowColor(-657673);
        this.gridView.setOnItemClickListener(new C18437());
        this.gridView.setOnScrollListener(new C18448());
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.showTextView();
        this.searchEmptyView.setText(LocaleController.getString("NoChats", C0691R.string.NoChats));
        this.gridView.setEmptyView(this.searchEmptyView);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
        this.shadow = new View(context);
        this.shadow.setBackgroundResource(C0691R.drawable.header_shadow);
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        updateSelectedCount();
    }

    private int getCurrentTop() {
        int i = 0;
        if (this.gridView.getChildCount() != 0) {
            View child = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.gridView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
        }
        return -1000;
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        int newOffset = 0;
        if (this.gridView.getChildCount() > 0) {
            View child = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
            int top = child.getTop() - AndroidUtilities.dp(8.0f);
            if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
            }
            if (this.scrollOffsetY != newOffset) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = newOffset;
                recyclerListView.setTopGlowOffset(newOffset);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.shadow.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void copyLink(Context context) {
        if (this.exportedMessageLink != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.exportedMessageLink.link));
                Toast.makeText(context, LocaleController.getString("LinkCopied", C0691R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void updateSelectedCount() {
        if (this.selectedDialogs.isEmpty()) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (this.isPublicChannel) {
                this.doneButtonTextView.setTextColor(Theme.SHARE_SHEET_COPY_TEXT_COLOR);
                this.doneButton.setEnabled(true);
                this.doneButtonTextView.setText(LocaleController.getString("CopyLink", C0691R.string.CopyLink).toUpperCase());
                return;
            }
            this.doneButtonTextView.setTextColor(Theme.SHARE_SHEET_SEND_DISABLED_TEXT_COLOR);
            this.doneButton.setEnabled(false);
            this.doneButtonTextView.setText(LocaleController.getString("Send", C0691R.string.Send).toUpperCase());
            return;
        }
        this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedDialogs.size())}));
        this.doneButtonTextView.setTextColor(Theme.SHARE_SHEET_SEND_TEXT_COLOR);
        this.doneButton.setEnabled(true);
        this.doneButtonTextView.setText(LocaleController.getString("Send", C0691R.string.Send).toUpperCase());
    }
}
