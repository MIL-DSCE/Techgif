package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditActivity extends BaseFragment implements AvatarUpdaterDelegate, NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextSettingsCell adminCell;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int chatId;
    private boolean createAfterUpload;
    private Chat currentChat;
    private EditText descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private ChatFull info;
    private EditText nameTextView;
    private ProgressDialog progressDialog;
    private boolean signMessages;
    private TextSettingsCell typeCell;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.ChannelEditActivity.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ InputFile val$file;
        final /* synthetic */ PhotoSize val$small;

        AnonymousClass10(InputFile inputFile, PhotoSize photoSize) {
            this.val$file = inputFile;
            this.val$small = photoSize;
        }

        public void run() {
            ChannelEditActivity.this.uploadedAvatar = this.val$file;
            ChannelEditActivity.this.avatar = this.val$small.location;
            ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
            if (ChannelEditActivity.this.createAfterUpload) {
                try {
                    if (ChannelEditActivity.this.progressDialog != null && ChannelEditActivity.this.progressDialog.isShowing()) {
                        ChannelEditActivity.this.progressDialog.dismiss();
                        ChannelEditActivity.this.progressDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                ChannelEditActivity.this.doneButton.performClick();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.1 */
    class C10421 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C10421(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChannelEditActivity.this.currentChat = MessagesStorage.getInstance().getChat(ChannelEditActivity.this.chatId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.3 */
    class C10453 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChannelEditActivity.3.1 */
        class C10441 implements DialogInterface.OnClickListener {
            C10441() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChannelEditActivity.this.avatarUpdater.openCamera();
                } else if (i == ChannelEditActivity.done_button) {
                    ChannelEditActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    ChannelEditActivity.this.avatar = null;
                    ChannelEditActivity.this.uploadedAvatar = null;
                    ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
                }
            }
        }

        C10453() {
        }

        public void onClick(View view) {
            if (ChannelEditActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChannelEditActivity.this.getParentActivity());
                builder.setItems(ChannelEditActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0691R.string.FromCamera), LocaleController.getString("FromGalley", C0691R.string.FromGalley), LocaleController.getString("DeletePhoto", C0691R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0691R.string.FromCamera), LocaleController.getString("FromGalley", C0691R.string.FromGalley)}, new C10441());
                ChannelEditActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.4 */
    class C10464 implements TextWatcher {
        C10464() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String obj;
            AvatarDrawable access$1200 = ChannelEditActivity.this.avatarDrawable;
            if (ChannelEditActivity.this.nameTextView.length() > 0) {
                obj = ChannelEditActivity.this.nameTextView.getText().toString();
            } else {
                obj = null;
            }
            access$1200.setInfo(5, obj, null, false);
            ChannelEditActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.5 */
    class C10475 implements OnEditorActionListener {
        C10475() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChannelEditActivity.this.doneButton == null) {
                return false;
            }
            ChannelEditActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.6 */
    class C10486 implements TextWatcher {
        C10486() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.7 */
    class C10497 implements OnClickListener {
        C10497() {
        }

        public void onClick(View v) {
            ChannelEditActivity.this.signMessages = !ChannelEditActivity.this.signMessages;
            ((TextCheckCell) v).setChecked(ChannelEditActivity.this.signMessages);
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.8 */
    class C10508 implements OnClickListener {
        C10508() {
        }

        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putInt("chat_id", ChannelEditActivity.this.chatId);
            args.putInt("type", ChannelEditActivity.done_button);
            ChannelEditActivity.this.presentFragment(new ChannelUsersActivity(args));
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.9 */
    class C10529 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChannelEditActivity.9.1 */
        class C10511 implements DialogInterface.OnClickListener {
            C10511() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i2 = NotificationCenter.closeChats;
                    Object[] objArr = new Object[ChannelEditActivity.done_button];
                    objArr[0] = Long.valueOf(-((long) ChannelEditActivity.this.chatId));
                    instance.postNotificationName(i2, objArr);
                } else {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                MessagesController.getInstance().deleteUserFromChat(ChannelEditActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), ChannelEditActivity.this.info);
                ChannelEditActivity.this.finishFragment();
            }
        }

        C10529() {
        }

        public void onClick(View v) {
            Builder builder = new Builder(ChannelEditActivity.this.getParentActivity());
            if (ChannelEditActivity.this.currentChat.megagroup) {
                builder.setMessage(LocaleController.getString("MegaDeleteAlert", C0691R.string.MegaDeleteAlert));
            } else {
                builder.setMessage(LocaleController.getString("ChannelDeleteAlert", C0691R.string.ChannelDeleteAlert));
            }
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C10511());
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            ChannelEditActivity.this.showDialog(builder.create());
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity.2 */
    class C17852 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ChannelEditActivity.2.1 */
        class C10431 implements DialogInterface.OnClickListener {
            C10431() {
            }

            public void onClick(DialogInterface dialog, int which) {
                ChannelEditActivity.this.createAfterUpload = false;
                ChannelEditActivity.this.progressDialog = null;
                ChannelEditActivity.this.donePressed = false;
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C17852() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelEditActivity.this.finishFragment();
            } else if (id == ChannelEditActivity.done_button && !ChannelEditActivity.this.donePressed) {
                if (ChannelEditActivity.this.nameTextView.length() == 0) {
                    Vibrator v = (Vibrator) ChannelEditActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChannelEditActivity.this.nameTextView, 2.0f, 0);
                    return;
                }
                ChannelEditActivity.this.donePressed = true;
                if (ChannelEditActivity.this.avatarUpdater.uploadingAvatar != null) {
                    ChannelEditActivity.this.createAfterUpload = true;
                    ChannelEditActivity.this.progressDialog = new ProgressDialog(ChannelEditActivity.this.getParentActivity());
                    ChannelEditActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
                    ChannelEditActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    ChannelEditActivity.this.progressDialog.setCancelable(false);
                    ChannelEditActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0691R.string.Cancel), new C10431());
                    ChannelEditActivity.this.progressDialog.show();
                    return;
                }
                if (!ChannelEditActivity.this.currentChat.title.equals(ChannelEditActivity.this.nameTextView.getText().toString())) {
                    MessagesController.getInstance().changeChatTitle(ChannelEditActivity.this.chatId, ChannelEditActivity.this.nameTextView.getText().toString());
                }
                if (!(ChannelEditActivity.this.info == null || ChannelEditActivity.this.info.about.equals(ChannelEditActivity.this.descriptionTextView.getText().toString()))) {
                    MessagesController.getInstance().updateChannelAbout(ChannelEditActivity.this.chatId, ChannelEditActivity.this.descriptionTextView.getText().toString(), ChannelEditActivity.this.info);
                }
                if (ChannelEditActivity.this.signMessages != ChannelEditActivity.this.currentChat.signatures) {
                    ChannelEditActivity.this.currentChat.signatures = true;
                    MessagesController.getInstance().toogleChannelSignatures(ChannelEditActivity.this.chatId, ChannelEditActivity.this.signMessages);
                }
                if (ChannelEditActivity.this.uploadedAvatar != null) {
                    MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, ChannelEditActivity.this.uploadedAvatar);
                } else if (ChannelEditActivity.this.avatar == null && (ChannelEditActivity.this.currentChat.photo instanceof TL_chatPhoto)) {
                    MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, null);
                }
                ChannelEditActivity.this.finishFragment();
            }
        }
    }

    public ChannelEditActivity(Bundle args) {
        super(args);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarUpdater = new AvatarUpdater();
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10421(semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (this.currentChat == null) {
                return false;
            }
            MessagesController.getInstance().putChat(this.currentChat, true);
            if (this.info == null) {
                MessagesStorage.getInstance().loadChatInfo(this.chatId, semaphore, false, false);
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                if (this.info == null) {
                    return false;
                }
            }
        }
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarUpdater != null) {
            this.avatarUpdater.clear();
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        float f;
        float f2;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C17852());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        this.fragmentView.setBackgroundColor(-986896);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        LinearLayout linearLayout = new LinearLayout(context);
        scrollView.addView(linearLayout, new LayoutParams(-1, -2));
        linearLayout.setOrientation(done_button);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", C0691R.string.ChannelEdit));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(done_button);
        linearLayout2.setBackgroundColor(-1);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
        this.avatarDrawable.setDrawPhoto(true);
        View view = this.avatarImage;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 16.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 16.0f;
        } else {
            f2 = 0.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
        this.avatarImage.setOnClickListener(new C10453());
        this.nameTextView = new EditText(context);
        if (this.currentChat.megagroup) {
            this.nameTextView.setHint(LocaleController.getString("GroupName", C0691R.string.GroupName));
        } else {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", C0691R.string.EnterChannelName));
        }
        this.nameTextView.setMaxLines(4);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameTextView.setTextSize(done_button, 16.0f);
        this.nameTextView.setHintTextColor(-6842473);
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(16385);
        this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        InputFilter[] inputFilters = new InputFilter[done_button];
        inputFilters[0] = new LengthFilter(100);
        this.nameTextView.setFilters(inputFilters);
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.nameTextView.setTextColor(-14606047);
        view = this.nameTextView;
        f = LocaleController.isRTL ? 16.0f : 96.0f;
        if (LocaleController.isRTL) {
            f2 = 96.0f;
        } else {
            f2 = 16.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 16, f, 0.0f, f2, 0.0f));
        this.nameTextView.addTextChangedListener(new C10464());
        View lineView = new View(context);
        lineView.setBackgroundColor(-3158065);
        linearLayout.addView(lineView, new LinearLayout.LayoutParams(-1, done_button));
        linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(done_button);
        linearLayout2.setBackgroundColor(-1);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditText(context);
        this.descriptionTextView.setTextSize(done_button, 16.0f);
        this.descriptionTextView.setHintTextColor(-6842473);
        this.descriptionTextView.setTextColor(-14606047);
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        inputFilters = new InputFilter[done_button];
        inputFilters[0] = new LengthFilter(NalUnitUtil.EXTENDED_SAR);
        this.descriptionTextView.setFilters(inputFilters);
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", C0691R.string.DescriptionOptionalPlaceholder));
        AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
        linearLayout2.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0f, 12.0f, 17.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new C10475());
        this.descriptionTextView.addTextChangedListener(new C10486());
        View shadowSectionCell = new ShadowSectionCell(context);
        shadowSectionCell.setSize(20);
        linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup || !this.currentChat.megagroup) {
            frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundColor(-1);
            linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.typeCell = new TextSettingsCell(context);
            updateTypeCell();
            this.typeCell.setBackgroundResource(C0691R.drawable.list_selector);
            frameLayout.addView(this.typeCell, LayoutHelper.createFrame(-1, -2.0f));
            lineView = new View(context);
            lineView.setBackgroundColor(-3158065);
            linearLayout.addView(lineView, new LinearLayout.LayoutParams(-1, done_button));
            frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundColor(-1);
            linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            if (this.currentChat.megagroup) {
                this.adminCell = new TextSettingsCell(context);
                updateAdminCell();
                this.adminCell.setBackgroundResource(C0691R.drawable.list_selector);
                frameLayout.addView(this.adminCell, LayoutHelper.createFrame(-1, -2.0f));
                this.adminCell.setOnClickListener(new C10508());
                shadowSectionCell = new ShadowSectionCell(context);
                shadowSectionCell.setSize(20);
                linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
                if (!this.currentChat.creator) {
                    shadowSectionCell.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                }
            } else {
                shadowSectionCell = new TextCheckCell(context);
                shadowSectionCell.setBackgroundResource(C0691R.drawable.list_selector);
                shadowSectionCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", C0691R.string.ChannelSignMessages), this.signMessages, false);
                frameLayout.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -2.0f));
                shadowSectionCell.setOnClickListener(new C10497());
                TextInfoPrivacyCell infoCell = new TextInfoPrivacyCell(context);
                infoCell.setBackgroundResource(C0691R.drawable.greydivider);
                infoCell.setText(LocaleController.getString("ChannelSignMessagesInfo", C0691R.string.ChannelSignMessagesInfo));
                linearLayout.addView(infoCell, LayoutHelper.createLinear(-1, -2));
            }
        }
        if (this.currentChat.creator) {
            frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundColor(-1);
            linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            shadowSectionCell = new TextSettingsCell(context);
            shadowSectionCell.setTextColor(-1229511);
            shadowSectionCell.setBackgroundResource(C0691R.drawable.list_selector);
            if (this.currentChat.megagroup) {
                shadowSectionCell.setText(LocaleController.getString("DeleteMega", C0691R.string.DeleteMega), false);
            } else {
                shadowSectionCell.setText(LocaleController.getString("ChannelDelete", C0691R.string.ChannelDelete), false);
            }
            frameLayout.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -2.0f));
            shadowSectionCell.setOnClickListener(new C10529());
            infoCell = new TextInfoPrivacyCell(context);
            infoCell.setBackgroundResource(C0691R.drawable.greydivider_bottom);
            if (this.currentChat.megagroup) {
                infoCell.setText(LocaleController.getString("MegaDeleteInfo", C0691R.string.MegaDeleteInfo));
            } else {
                infoCell.setText(LocaleController.getString("ChannelDeleteInfo", C0691R.string.ChannelDeleteInfo));
            }
            linearLayout.addView(infoCell, LayoutHelper.createLinear(-1, -2));
        }
        this.nameTextView.setText(this.currentChat.title);
        this.nameTextView.setSelection(this.nameTextView.length());
        if (this.info != null) {
            this.descriptionTextView.setText(this.info.about);
        }
        if (this.currentChat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null) {
                    this.descriptionTextView.setText(chatFull.about);
                }
                this.info = chatFull;
                updateAdminCell();
                updateTypeCell();
            }
        } else if (id == NotificationCenter.updateInterfaces && (((Integer) args[0]).intValue() & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
            updateTypeCell();
        }
    }

    public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
        AndroidUtilities.runOnUIThread(new AnonymousClass10(file, small));
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
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
    }

    private void updateTypeCell() {
        String type = (this.currentChat.username == null || this.currentChat.username.length() == 0) ? LocaleController.getString("ChannelTypePrivate", C0691R.string.ChannelTypePrivate) : LocaleController.getString("ChannelTypePublic", C0691R.string.ChannelTypePublic);
        if (this.currentChat.megagroup) {
            this.typeCell.setTextAndValue(LocaleController.getString("GroupType", C0691R.string.GroupType), type, false);
        } else {
            this.typeCell.setTextAndValue(LocaleController.getString("ChannelType", C0691R.string.ChannelType), type, false);
        }
        if (this.currentChat.creator && (this.info == null || this.info.can_set_username)) {
            this.typeCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", ChannelEditActivity.this.chatId);
                    ChannelEditTypeActivity fragment = new ChannelEditTypeActivity(args);
                    fragment.setInfo(ChannelEditActivity.this.info);
                    ChannelEditActivity.this.presentFragment(fragment);
                }
            });
            this.typeCell.setTextColor(-14606047);
            this.typeCell.setTextValueColor(-13660983);
            return;
        }
        this.typeCell.setOnClickListener(null);
        this.typeCell.setTextColor(-5723992);
        this.typeCell.setTextValueColor(-5723992);
    }

    private void updateAdminCell() {
        if (this.adminCell != null) {
            if (this.info != null) {
                TextSettingsCell textSettingsCell = this.adminCell;
                String string = LocaleController.getString("ChannelAdministrators", C0691R.string.ChannelAdministrators);
                Object[] objArr = new Object[done_button];
                objArr[0] = Integer.valueOf(this.info.admins_count);
                textSettingsCell.setTextAndValue(string, String.format("%d", objArr), false);
                return;
            }
            this.adminCell.setText(LocaleController.getString("ChannelAdministrators", C0691R.string.ChannelAdministrators), false);
        }
    }
}
