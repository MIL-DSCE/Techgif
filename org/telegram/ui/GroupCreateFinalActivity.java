package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int chatType;
    private boolean createAfterUpload;
    private boolean donePressed;
    private ListAdapter listAdapter;
    private ListView listView;
    private EditText nameTextView;
    private String nameToSet;
    private ProgressDialog progressDialog;
    private ArrayList<Integer> selectedContacts;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity.1 */
    class C12401 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ ArrayList val$usersToLoad;

        C12401(ArrayList arrayList, ArrayList arrayList2, Semaphore semaphore) {
            this.val$users = arrayList;
            this.val$usersToLoad = arrayList2;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$users.addAll(MessagesStorage.getInstance().getUsers(this.val$usersToLoad));
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity.3 */
    class C12433 implements OnClickListener {

        /* renamed from: org.telegram.ui.GroupCreateFinalActivity.3.1 */
        class C12421 implements DialogInterface.OnClickListener {
            C12421() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    GroupCreateFinalActivity.this.avatarUpdater.openCamera();
                } else if (i == GroupCreateFinalActivity.done_button) {
                    GroupCreateFinalActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    GroupCreateFinalActivity.this.avatar = null;
                    GroupCreateFinalActivity.this.uploadedAvatar = null;
                    GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                }
            }
        }

        C12433() {
        }

        public void onClick(View view) {
            if (GroupCreateFinalActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(GroupCreateFinalActivity.this.getParentActivity());
                builder.setItems(GroupCreateFinalActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0691R.string.FromCamera), LocaleController.getString("FromGalley", C0691R.string.FromGalley), LocaleController.getString("DeletePhoto", C0691R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0691R.string.FromCamera), LocaleController.getString("FromGalley", C0691R.string.FromGalley)}, new C12421());
                GroupCreateFinalActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity.4 */
    class C12444 implements TextWatcher {
        C12444() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String obj;
            AvatarDrawable access$900 = GroupCreateFinalActivity.this.avatarDrawable;
            if (GroupCreateFinalActivity.this.nameTextView.length() > 0) {
                obj = GroupCreateFinalActivity.this.nameTextView.getText().toString();
            } else {
                obj = null;
            }
            access$900.setInfo(5, obj, null, false);
            GroupCreateFinalActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity.5 */
    class C12455 implements Runnable {
        final /* synthetic */ InputFile val$file;
        final /* synthetic */ PhotoSize val$small;

        C12455(InputFile inputFile, PhotoSize photoSize) {
            this.val$file = inputFile;
            this.val$small = photoSize;
        }

        public void run() {
            GroupCreateFinalActivity.this.uploadedAvatar = this.val$file;
            GroupCreateFinalActivity.this.avatar = this.val$small.location;
            GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
            if (GroupCreateFinalActivity.this.createAfterUpload) {
                FileLog.m11e("tmessages", "avatar did uploaded");
                MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity.2 */
    class C18612 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.GroupCreateFinalActivity.2.1 */
        class C12411 implements DialogInterface.OnClickListener {
            final /* synthetic */ int val$reqId;

            C12411(int i) {
                this.val$reqId = i;
            }

            public void onClick(DialogInterface dialog, int which) {
                ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
                GroupCreateFinalActivity.this.donePressed = false;
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C18612() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupCreateFinalActivity.this.finishFragment();
            } else if (id == GroupCreateFinalActivity.done_button && !GroupCreateFinalActivity.this.donePressed && GroupCreateFinalActivity.this.nameTextView.getText().length() != 0) {
                GroupCreateFinalActivity.this.donePressed = true;
                if (GroupCreateFinalActivity.this.chatType == GroupCreateFinalActivity.done_button) {
                    MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
                } else if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != null) {
                    GroupCreateFinalActivity.this.createAfterUpload = true;
                } else {
                    GroupCreateFinalActivity.this.progressDialog = new ProgressDialog(GroupCreateFinalActivity.this.getParentActivity());
                    GroupCreateFinalActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
                    GroupCreateFinalActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    GroupCreateFinalActivity.this.progressDialog.setCancelable(false);
                    GroupCreateFinalActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new C12411(MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this)));
                    GroupCreateFinalActivity.this.progressDialog.show();
                }
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

        public boolean isEnabled(int position) {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = new UserCell(this.mContext, GroupCreateFinalActivity.done_button, 0, false);
            }
            ((UserCell) view).setData(MessagesController.getInstance().getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(i)), null, null, 0);
            return view;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return GroupCreateFinalActivity.done_button;
        }

        public int getCount() {
            return GroupCreateFinalActivity.this.selectedContacts.size();
        }
    }

    public GroupCreateFinalActivity(Bundle args) {
        super(args);
        this.avatarUpdater = new AvatarUpdater();
        this.progressDialog = null;
        this.nameToSet = null;
        this.chatType = 0;
        this.chatType = args.getInt("chatType", 0);
        this.avatarDrawable = new AvatarDrawable();
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        ArrayList<Integer> usersToLoad = new ArrayList();
        Iterator i$ = this.selectedContacts.iterator();
        while (i$.hasNext()) {
            Integer uid = (Integer) i$.next();
            if (MessagesController.getInstance().getUser(uid) == null) {
                usersToLoad.add(uid);
            }
        }
        if (!usersToLoad.isEmpty()) {
            Semaphore semaphore = new Semaphore(0);
            ArrayList<User> users = new ArrayList();
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C12401(users, usersToLoad, semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (usersToLoad.size() != users.size() || users.isEmpty()) {
                return false;
            }
            i$ = users.iterator();
            while (i$.hasNext()) {
                MessagesController.getInstance().putUser((User) i$.next(), true);
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.clear();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == done_button) {
            this.actionBar.setTitle(LocaleController.getString("NewBroadcastList", C0691R.string.NewBroadcastList));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewGroup", C0691R.string.NewGroup));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C18612());
        this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(done_button);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        layoutParams.gravity = 51;
        frameLayout.setLayoutParams(layoutParams);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, this.chatType == done_button);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage);
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams1.width = AndroidUtilities.dp(64.0f);
        layoutParams1.height = AndroidUtilities.dp(64.0f);
        layoutParams1.topMargin = AndroidUtilities.dp(12.0f);
        layoutParams1.bottomMargin = AndroidUtilities.dp(12.0f);
        layoutParams1.leftMargin = LocaleController.isRTL ? 0 : AndroidUtilities.dp(16.0f);
        layoutParams1.rightMargin = LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0;
        layoutParams1.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.avatarImage.setLayoutParams(layoutParams1);
        if (this.chatType != done_button) {
            this.avatarDrawable.setDrawPhoto(true);
            this.avatarImage.setOnClickListener(new C12433());
        }
        this.nameTextView = new EditText(context);
        this.nameTextView.setHint(this.chatType == 0 ? LocaleController.getString("EnterGroupNamePlaceholder", C0691R.string.EnterGroupNamePlaceholder) : LocaleController.getString("EnterListName", C0691R.string.EnterListName));
        if (this.nameToSet != null) {
            this.nameTextView.setText(this.nameToSet);
            this.nameToSet = null;
        }
        this.nameTextView.setMaxLines(4);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameTextView.setTextSize(done_button, 16.0f);
        this.nameTextView.setHintTextColor(-6842473);
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(MessagesController.UPDATE_MASK_CHAT_ADMINS);
        this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        InputFilter[] inputFilters = new InputFilter[done_button];
        inputFilters[0] = new LengthFilter(100);
        this.nameTextView.setFilters(inputFilters);
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.nameTextView.setTextColor(-14606047);
        frameLayout.addView(this.nameTextView);
        layoutParams1 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams1.width = -1;
        layoutParams1.height = -2;
        layoutParams1.leftMargin = LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(96.0f);
        layoutParams1.rightMargin = LocaleController.isRTL ? AndroidUtilities.dp(96.0f) : AndroidUtilities.dp(16.0f);
        layoutParams1.gravity = 16;
        this.nameTextView.setLayoutParams(layoutParams1);
        if (this.chatType != done_button) {
            this.nameTextView.addTextChangedListener(new C12444());
        }
        GreySectionCell sectionCell = new GreySectionCell(context);
        sectionCell.setText(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
        linearLayout.addView(sectionCell);
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        ListView listView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        linearLayout.addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        return this.fragmentView;
    }

    public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
        AndroidUtilities.runOnUIThread(new C12455(file, small));
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
        if (this.nameTextView != null) {
            String text = this.nameTextView.getText().toString();
            if (text != null && text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
        String text = args.getString("nameTextView");
        if (text == null) {
            return;
        }
        if (this.nameTextView != null) {
            this.nameTextView.setText(text);
        } else {
            this.nameToSet = text;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.nameTextView);
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & done_button) != 0 || (mask & 4) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.chatDidFailCreate) {
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            this.donePressed = false;
        } else if (id == NotificationCenter.chatDidCreated) {
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
            int chat_id = ((Integer) args[0]).intValue();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle args2 = new Bundle();
            args2.putInt("chat_id", chat_id);
            presentFragment(new ChatActivity(args2), true);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance().changeChatAvatar(chat_id, this.uploadedAvatar);
            }
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a += done_button) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }
}
