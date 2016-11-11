package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;

public class ChannelEditTypeActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private Chat currentChat;
    private boolean donePressed;
    private HeaderCell headerCell;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linkContainer;
    private boolean loadingInvite;
    private EditText nameTextView;
    private TextBlockCell privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private TextInfoPrivacyCell typeInfoCell;

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.1 */
    class C10531 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C10531(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChannelEditTypeActivity.this.currentChat = MessagesStorage.getInstance().getChat(ChannelEditTypeActivity.this.chatId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.3 */
    class C10543 implements OnClickListener {
        C10543() {
        }

        public void onClick(View v) {
            if (ChannelEditTypeActivity.this.isPrivate) {
                ChannelEditTypeActivity.this.isPrivate = false;
                ChannelEditTypeActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.4 */
    class C10554 implements OnClickListener {
        C10554() {
        }

        public void onClick(View v) {
            if (!ChannelEditTypeActivity.this.isPrivate) {
                ChannelEditTypeActivity.this.isPrivate = true;
                ChannelEditTypeActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.5 */
    class C10565 implements TextWatcher {
        C10565() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString(), false);
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.6 */
    class C10576 implements OnClickListener {
        C10576() {
        }

        public void onClick(View v) {
            if (ChannelEditTypeActivity.this.invite != null) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditTypeActivity.this.invite.link));
                    Toast.makeText(ChannelEditTypeActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0691R.string.LinkCopied), 0).show();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.7 */
    class C10597 implements Runnable {
        final /* synthetic */ String val$name;

        /* renamed from: org.telegram.ui.ChannelEditTypeActivity.7.1 */
        class C17871 implements RequestDelegate {

            /* renamed from: org.telegram.ui.ChannelEditTypeActivity.7.1.1 */
            class C10581 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C10581(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChannelEditTypeActivity.this.checkReqId = 0;
                    if (ChannelEditTypeActivity.this.lastCheckName != null && ChannelEditTypeActivity.this.lastCheckName.equals(C10597.this.val$name)) {
                        if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                            TextView access$600 = ChannelEditTypeActivity.this.checkTextView;
                            Object[] objArr = new Object[ChannelEditTypeActivity.done_button];
                            objArr[0] = C10597.this.val$name;
                            access$600.setText(LocaleController.formatString("LinkAvailable", C0691R.string.LinkAvailable, objArr));
                            ChannelEditTypeActivity.this.checkTextView.setTextColor(-14248148);
                            ChannelEditTypeActivity.this.lastNameAvailable = true;
                            return;
                        }
                        if (this.val$error == null || !this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                            ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", C0691R.string.LinkInUse));
                        } else {
                            ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("ChangePublicLimitReached", C0691R.string.ChangePublicLimitReached));
                        }
                        ChannelEditTypeActivity.this.checkTextView.setTextColor(-3198928);
                        ChannelEditTypeActivity.this.lastNameAvailable = false;
                    }
                }
            }

            C17871() {
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C10581(error, response));
            }
        }

        C10597(String str) {
            this.val$name = str;
        }

        public void run() {
            TL_channels_checkUsername req = new TL_channels_checkUsername();
            req.username = this.val$name;
            req.channel = MessagesController.getInputChannel(ChannelEditTypeActivity.this.chatId);
            ChannelEditTypeActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(req, new C17871(), 2);
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.2 */
    class C17862 extends ActionBarMenuOnItemClick {
        C17862() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelEditTypeActivity.this.finishFragment();
            } else if (id == ChannelEditTypeActivity.done_button && !ChannelEditTypeActivity.this.donePressed) {
                if (ChannelEditTypeActivity.this.isPrivate || (((ChannelEditTypeActivity.this.currentChat.username != null || ChannelEditTypeActivity.this.nameTextView.length() == 0) && (ChannelEditTypeActivity.this.currentChat.username == null || ChannelEditTypeActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditTypeActivity.this.nameTextView.getText().toString()))) || ChannelEditTypeActivity.this.nameTextView.length() == 0 || ChannelEditTypeActivity.this.lastNameAvailable)) {
                    ChannelEditTypeActivity.this.donePressed = true;
                    String oldUserName = ChannelEditTypeActivity.this.currentChat.username != null ? ChannelEditTypeActivity.this.currentChat.username : TtmlNode.ANONYMOUS_REGION_ID;
                    String newUserName = ChannelEditTypeActivity.this.isPrivate ? TtmlNode.ANONYMOUS_REGION_ID : ChannelEditTypeActivity.this.nameTextView.getText().toString();
                    if (!oldUserName.equals(newUserName)) {
                        MessagesController.getInstance().updateChannelUserName(ChannelEditTypeActivity.this.chatId, newUserName);
                    }
                    ChannelEditTypeActivity.this.finishFragment();
                    return;
                }
                Vibrator v = (Vibrator) ChannelEditTypeActivity.this.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(ChannelEditTypeActivity.this.checkTextView, 2.0f, 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditTypeActivity.8 */
    class C17888 implements RequestDelegate {

        /* renamed from: org.telegram.ui.ChannelEditTypeActivity.8.1 */
        class C10601 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C10601(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    ChannelEditTypeActivity.this.invite = (ExportedChatInvite) this.val$response;
                }
                ChannelEditTypeActivity.this.loadingInvite = false;
                ChannelEditTypeActivity.this.privateContainer.setText(ChannelEditTypeActivity.this.invite != null ? ChannelEditTypeActivity.this.invite.link : LocaleController.getString("Loading", C0691R.string.Loading), false);
            }
        }

        C17888() {
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C10601(error, response));
        }
    }

    public ChannelEditTypeActivity(Bundle args) {
        super(args);
        this.isPrivate = false;
        this.checkReqId = 0;
        this.lastCheckName = null;
        this.checkRunnable = null;
        this.lastNameAvailable = false;
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        boolean z = false;
        this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10531(semaphore));
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
        if (this.currentChat.username == null || this.currentChat.username.length() == 0) {
            z = true;
        }
        this.isPrivate = z;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C17862());
        this.actionBar.createMenu().addItemWithWidth(done_button, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        this.fragmentView.setBackgroundColor(-986896);
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        LinearLayout linearLayout = new LinearLayout(context);
        scrollView.addView(linearLayout, new LayoutParams(-1, -2));
        linearLayout.setOrientation(done_button);
        if (this.currentChat.megagroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupType", C0691R.string.GroupType));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ChannelType", C0691R.string.ChannelType));
        }
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(done_button);
        linearLayout2.setBackgroundColor(-1);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1 = new RadioButtonCell(context);
        this.radioButtonCell1.setBackgroundResource(C0691R.drawable.list_selector);
        if (this.currentChat.megagroup) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", C0691R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", C0691R.string.MegaPublicInfo), !this.isPrivate, false);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", C0691R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", C0691R.string.ChannelPublicInfo), !this.isPrivate, false);
        }
        linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new C10543());
        this.radioButtonCell2 = new RadioButtonCell(context);
        this.radioButtonCell2.setBackgroundResource(C0691R.drawable.list_selector);
        if (this.currentChat.megagroup) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", C0691R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", C0691R.string.MegaPrivateInfo), this.isPrivate, false);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", C0691R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", C0691R.string.ChannelPrivateInfo), this.isPrivate, false);
        }
        linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new C10554());
        linearLayout.addView(new ShadowSectionCell(context), LayoutHelper.createLinear(-1, -2));
        this.linkContainer = new LinearLayout(context);
        this.linkContainer.setOrientation(done_button);
        this.linkContainer.setBackgroundColor(-1);
        linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context);
        this.linkContainer.addView(this.headerCell);
        this.publicContainer = new LinearLayout(context);
        this.publicContainer.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
        EditText editText = new EditText(context);
        editText.setText("telegram.me/");
        editText.setTextSize(done_button, 18.0f);
        editText.setHintTextColor(-6842473);
        editText.setTextColor(-14606047);
        editText.setMaxLines(done_button);
        editText.setLines(done_button);
        editText.setEnabled(false);
        editText.setBackgroundDrawable(null);
        editText.setPadding(0, 0, 0, 0);
        editText.setSingleLine(true);
        editText.setInputType(163840);
        editText.setImeOptions(6);
        this.publicContainer.addView(editText, LayoutHelper.createLinear(-2, 36));
        this.nameTextView = new EditText(context);
        this.nameTextView.setTextSize(done_button, 18.0f);
        if (!this.isPrivate) {
            this.nameTextView.setText(this.currentChat.username);
        }
        this.nameTextView.setHintTextColor(-6842473);
        this.nameTextView.setTextColor(-14606047);
        this.nameTextView.setMaxLines(done_button);
        this.nameTextView.setLines(done_button);
        this.nameTextView.setBackgroundDrawable(null);
        this.nameTextView.setPadding(0, 0, 0, 0);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setInputType(163872);
        this.nameTextView.setImeOptions(6);
        this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", C0691R.string.ChannelUsernamePlaceholder));
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
        this.nameTextView.addTextChangedListener(new C10565());
        this.privateContainer = new TextBlockCell(context);
        this.privateContainer.setBackgroundResource(C0691R.drawable.list_selector);
        this.linkContainer.addView(this.privateContainer);
        this.privateContainer.setOnClickListener(new C10576());
        this.checkTextView = new TextView(context);
        this.checkTextView.setTextSize(done_button, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.checkTextView.setVisibility(8);
        this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
        this.typeInfoCell = new TextInfoPrivacyCell(context);
        this.typeInfoCell.setBackgroundResource(C0691R.drawable.greydivider_bottom);
        linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
        updatePrivatePublic();
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        if (chatFull == null) {
            return;
        }
        if (chatFull.exported_invite instanceof TL_chatInviteExported) {
            this.invite = chatFull.exported_invite;
        } else {
            generateLink();
        }
    }

    private void updatePrivatePublic() {
        int i;
        int i2 = 0;
        this.radioButtonCell1.setChecked(!this.isPrivate, true);
        this.radioButtonCell2.setChecked(this.isPrivate, true);
        if (this.currentChat.megagroup) {
            this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("MegaPrivateLinkHelp", C0691R.string.MegaPrivateLinkHelp) : LocaleController.getString("MegaUsernameHelp", C0691R.string.MegaUsernameHelp));
            this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", C0691R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", C0691R.string.ChannelLinkTitle));
        } else {
            this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", C0691R.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", C0691R.string.ChannelUsernameHelp));
            this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", C0691R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", C0691R.string.ChannelLinkTitle));
        }
        LinearLayout linearLayout = this.publicContainer;
        if (this.isPrivate) {
            i = 8;
        } else {
            i = 0;
        }
        linearLayout.setVisibility(i);
        TextBlockCell textBlockCell = this.privateContainer;
        if (this.isPrivate) {
            i = 0;
        } else {
            i = 8;
        }
        textBlockCell.setVisibility(i);
        this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
        this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", C0691R.string.Loading), false);
        this.nameTextView.clearFocus();
        TextView textView = this.checkTextView;
        if (this.isPrivate || this.checkTextView.length() == 0) {
            i2 = 8;
        }
        textView.setVisibility(i2);
        AndroidUtilities.hideKeyboard(this.nameTextView);
    }

    private boolean checkUserName(String name, boolean alert) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (alert && name.length() == 0) {
            return true;
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0691R.string.LinkInvalid));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (this.currentChat.megagroup) {
                        if (alert) {
                            showErrorAlert(LocaleController.getString("LinkInvalidStartNumberMega", C0691R.string.LinkInvalidStartNumberMega));
                        } else {
                            this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", C0691R.string.LinkInvalidStartNumberMega));
                            this.checkTextView.setTextColor(-3198928);
                        }
                    } else if (alert) {
                        showErrorAlert(LocaleController.getString("LinkInvalidStartNumber", C0691R.string.LinkInvalidStartNumber));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", C0691R.string.LinkInvalidStartNumber));
                        this.checkTextView.setTextColor(-3198928);
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        showErrorAlert(LocaleController.getString("LinkInvalid", C0691R.string.LinkInvalid));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0691R.string.LinkInvalid));
                        this.checkTextView.setTextColor(-3198928);
                    }
                    return false;
                } else {
                    a += done_button;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (this.currentChat.megagroup) {
                if (alert) {
                    showErrorAlert(LocaleController.getString("LinkInvalidShortMega", C0691R.string.LinkInvalidShortMega));
                } else {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", C0691R.string.LinkInvalidShortMega));
                    this.checkTextView.setTextColor(-3198928);
                }
            } else if (alert) {
                showErrorAlert(LocaleController.getString("LinkInvalidShort", C0691R.string.LinkInvalidShort));
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", C0691R.string.LinkInvalidShort));
                this.checkTextView.setTextColor(-3198928);
            }
            return false;
        } else if (name.length() > 32) {
            if (alert) {
                showErrorAlert(LocaleController.getString("LinkInvalidLong", C0691R.string.LinkInvalidLong));
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", C0691R.string.LinkInvalidLong));
                this.checkTextView.setTextColor(-3198928);
            }
            return false;
        } else if (alert) {
            return true;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", C0691R.string.LinkChecking));
            this.checkTextView.setTextColor(-9605774);
            this.lastCheckName = name;
            this.checkRunnable = new C10597(name);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TL_channels_exportInvite req = new TL_channels_exportInvite();
            req.channel = MessagesController.getInputChannel(this.chatId);
            ConnectionsManager.getInstance().sendRequest(req, new C17888());
        }
    }

    private void showErrorAlert(String error) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            Object obj = -1;
            switch (error.hashCode()) {
                case -141887186:
                    if (error.equals("USERNAMES_UNAVAILABLE")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 288843630:
                    if (error.equals("USERNAME_INVALID")) {
                        obj = null;
                        break;
                    }
                    break;
                case 533175271:
                    if (error.equals("USERNAME_OCCUPIED")) {
                        obj = done_button;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("LinkInvalid", C0691R.string.LinkInvalid));
                    break;
                case done_button /*1*/:
                    builder.setMessage(LocaleController.getString("LinkInUse", C0691R.string.LinkInUse));
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    builder.setMessage(LocaleController.getString("FeatureUnavailable", C0691R.string.FeatureUnavailable));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            showDialog(builder.create());
        }
    }
}
