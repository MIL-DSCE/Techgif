package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate;

public class PrivacyUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int block_user = 1;
    private PrivacyActivityDelegate delegate;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private ListView listView;
    private ListAdapter listViewAdapter;
    private int selectedUserId;
    private ArrayList<Integer> uidArray;

    /* renamed from: org.telegram.ui.PrivacyUsersActivity.2 */
    class C13892 implements OnTouchListener {
        C13892() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity.3 */
    class C13903 implements OnItemClickListener {
        C13903() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i < PrivacyUsersActivity.this.uidArray.size()) {
                Bundle args = new Bundle();
                args.putInt("user_id", ((Integer) PrivacyUsersActivity.this.uidArray.get(i)).intValue());
                PrivacyUsersActivity.this.presentFragment(new ProfileActivity(args));
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity.4 */
    class C13924 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.PrivacyUsersActivity.4.1 */
        class C13911 implements OnClickListener {
            C13911() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    PrivacyUsersActivity.this.uidArray.remove(Integer.valueOf(PrivacyUsersActivity.this.selectedUserId));
                    PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    if (PrivacyUsersActivity.this.delegate != null) {
                        PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, false);
                    }
                }
            }
        }

        C13924() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i >= 0 && i < PrivacyUsersActivity.this.uidArray.size() && PrivacyUsersActivity.this.getParentActivity() != null) {
                PrivacyUsersActivity.this.selectedUserId = ((Integer) PrivacyUsersActivity.this.uidArray.get(i)).intValue();
                Builder builder = new Builder(PrivacyUsersActivity.this.getParentActivity());
                CharSequence[] items = new CharSequence[PrivacyUsersActivity.block_user];
                items[0] = LocaleController.getString("Delete", C0691R.string.Delete);
                builder.setItems(items, new C13911());
                PrivacyUsersActivity.this.showDialog(builder.create());
            }
            return true;
        }
    }

    public interface PrivacyActivityDelegate {
        void didUpdatedUserList(ArrayList<Integer> arrayList, boolean z);
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity.1 */
    class C19261 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.PrivacyUsersActivity.1.1 */
        class C19251 implements GroupCreateActivityDelegate {
            C19251() {
            }

            public void didSelectUsers(ArrayList<Integer> ids) {
                Iterator i$ = ids.iterator();
                while (i$.hasNext()) {
                    Integer id = (Integer) i$.next();
                    if (!PrivacyUsersActivity.this.uidArray.contains(id)) {
                        PrivacyUsersActivity.this.uidArray.add(id);
                    }
                }
                PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                if (PrivacyUsersActivity.this.delegate != null) {
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
                }
            }
        }

        C19261() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PrivacyUsersActivity.this.finishFragment();
            } else if (id == PrivacyUsersActivity.block_user) {
                Bundle args = new Bundle();
                args.putBoolean(PrivacyUsersActivity.this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
                args.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
                GroupCreateActivity fragment = new GroupCreateActivity(args);
                fragment.setDelegate(new C19251());
                PrivacyUsersActivity.this.presentFragment(fragment);
            }
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
            return i != PrivacyUsersActivity.this.uidArray.size();
        }

        public int getCount() {
            if (PrivacyUsersActivity.this.uidArray.isEmpty()) {
                return 0;
            }
            return PrivacyUsersActivity.this.uidArray.size() + PrivacyUsersActivity.block_user;
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
                    view = new UserCell(this.mContext, PrivacyUsersActivity.block_user, 0, false);
                }
                User user = MessagesController.getInstance().getUser((Integer) PrivacyUsersActivity.this.uidArray.get(i));
                UserCell userCell = (UserCell) view;
                CharSequence string = (user.phone == null || user.phone.length() == 0) ? LocaleController.getString("NumberUnknown", C0691R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + user.phone);
                userCell.setData(user, null, string, 0);
                return view;
            } else if (type != PrivacyUsersActivity.block_user || view != null) {
                return view;
            } else {
                view = new TextInfoCell(this.mContext);
                ((TextInfoCell) view).setText(LocaleController.getString("RemoveFromListText", C0691R.string.RemoveFromListText));
                return view;
            }
        }

        public int getItemViewType(int i) {
            if (i == PrivacyUsersActivity.this.uidArray.size()) {
                return PrivacyUsersActivity.block_user;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEmpty() {
            return PrivacyUsersActivity.this.uidArray.isEmpty();
        }
    }

    public PrivacyUsersActivity(ArrayList<Integer> users, boolean group, boolean always) {
        this.uidArray = users;
        this.isAlwaysShare = always;
        this.isGroup = group;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        int i = block_user;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", C0691R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", C0691R.string.NeverAllow));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", C0691R.string.AlwaysShareWithTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", C0691R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19261());
        this.actionBar.createMenu().addItem((int) block_user, (int) C0691R.drawable.plus);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        TextView emptyTextView = new TextView(context);
        emptyTextView.setTextColor(-8355712);
        emptyTextView.setTextSize(20.0f);
        emptyTextView.setGravity(17);
        emptyTextView.setVisibility(4);
        emptyTextView.setText(LocaleController.getString("NoContacts", C0691R.string.NoContacts));
        frameLayout.addView(emptyTextView);
        LayoutParams layoutParams = (LayoutParams) emptyTextView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        emptyTextView.setLayoutParams(layoutParams);
        emptyTextView.setOnTouchListener(new C13892());
        this.listView = new ListView(context);
        this.listView.setEmptyView(emptyTextView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        ListView listView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        listView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        listView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C13903());
        this.listView.setOnItemLongClickListener(new C13924());
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & block_user) != 0) {
                updateVisibleRows(mask);
            }
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a += block_user) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }
}
