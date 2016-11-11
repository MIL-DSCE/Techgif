package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseSectionsAdapter;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LetterSectionsListView;

public class ContactsActivity extends BaseFragment implements NotificationCenterDelegate {
    private boolean allowBots;
    private boolean allowUsernameSearch;
    private int chat_id;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private TextView emptyTextView;
    private HashMap<Integer, User> ignoreUsers;
    private LetterSectionsListView listView;
    private BaseSectionsAdapter listViewAdapter;
    private boolean needForwardCount;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private boolean returnAsResult;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;

    /* renamed from: org.telegram.ui.ContactsActivity.3 */
    class C12053 implements OnTouchListener {
        C12053() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity.4 */
    class C12074 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ContactsActivity.4.1 */
        class C12061 implements OnClickListener {
            final /* synthetic */ String val$arg1;

            C12061(String str) {
                this.val$arg1 = str;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", this.val$arg1, null));
                    intent.putExtra("sms_body", LocaleController.getString("InviteText", C0691R.string.InviteText));
                    ContactsActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C12074() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onItemClick(android.widget.AdapterView<?> r23, android.view.View r24, int r25, long r26) {
            /*
            r22 = this;
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.searching;
            if (r18 == 0) goto L_0x0134;
        L_0x000c:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.searchWas;
            if (r18 == 0) goto L_0x0134;
        L_0x0018:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.searchListViewAdapter;
            r0 = r18;
            r1 = r25;
            r16 = r0.getItem(r1);
            r16 = (org.telegram.tgnet.TLRPC.User) r16;
            if (r16 != 0) goto L_0x002f;
        L_0x002e:
            return;
        L_0x002f:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.searchListViewAdapter;
            r0 = r18;
            r1 = r25;
            r18 = r0.isGlobalSearch(r1);
            if (r18 == 0) goto L_0x0075;
        L_0x0043:
            r17 = new java.util.ArrayList;
            r17.<init>();
            r0 = r17;
            r1 = r16;
            r0.add(r1);
            r18 = org.telegram.messenger.MessagesController.getInstance();
            r19 = 0;
            r0 = r18;
            r1 = r17;
            r2 = r19;
            r0.putUsers(r1, r2);
            r18 = org.telegram.messenger.MessagesStorage.getInstance();
            r19 = 0;
            r20 = 0;
            r21 = 1;
            r0 = r18;
            r1 = r17;
            r2 = r19;
            r3 = r20;
            r4 = r21;
            r0.putUsersAndChats(r1, r2, r3, r4);
        L_0x0075:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.returnAsResult;
            if (r18 == 0) goto L_0x00be;
        L_0x0081:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.ignoreUsers;
            if (r18 == 0) goto L_0x00a7;
        L_0x008d:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.ignoreUsers;
            r0 = r16;
            r0 = r0.id;
            r19 = r0;
            r19 = java.lang.Integer.valueOf(r19);
            r18 = r18.containsKey(r19);
            if (r18 != 0) goto L_0x002e;
        L_0x00a7:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = 1;
            r20 = 0;
            r0 = r18;
            r1 = r16;
            r2 = r19;
            r3 = r20;
            r0.didSelectResult(r1, r2, r3);
            goto L_0x002e;
        L_0x00be:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.createSecretChat;
            if (r18 == 0) goto L_0x00fe;
        L_0x00ca:
            r0 = r16;
            r0 = r0.id;
            r18 = r0;
            r19 = org.telegram.messenger.UserConfig.getClientUserId();
            r0 = r18;
            r1 = r19;
            if (r0 == r1) goto L_0x002e;
        L_0x00da:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = 1;
            r18.creatingChat = r19;
            r18 = org.telegram.messenger.SecretChatHelper.getInstance();
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r19 = r0;
            r19 = r19.getParentActivity();
            r0 = r18;
            r1 = r19;
            r2 = r16;
            r0.startSecretChat(r1, r2);
            goto L_0x002e;
        L_0x00fe:
            r6 = new android.os.Bundle;
            r6.<init>();
            r18 = "user_id";
            r0 = r16;
            r0 = r0.id;
            r19 = r0;
            r0 = r18;
            r1 = r19;
            r6.putInt(r0, r1);
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r0 = r18;
            r18 = org.telegram.messenger.MessagesController.checkCanOpenChat(r6, r0);
            if (r18 == 0) goto L_0x002e;
        L_0x0120:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.ChatActivity;
            r0 = r19;
            r0.<init>(r6);
            r20 = 1;
            r18.presentFragment(r19, r20);
            goto L_0x002e;
        L_0x0134:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.listViewAdapter;
            r0 = r18;
            r1 = r25;
            r14 = r0.getSectionForPosition(r1);
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.listViewAdapter;
            r0 = r18;
            r1 = r25;
            r13 = r0.getPositionInSectionForPosition(r1);
            if (r13 < 0) goto L_0x002e;
        L_0x015a:
            if (r14 < 0) goto L_0x002e;
        L_0x015c:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.onlyUsers;
            if (r18 == 0) goto L_0x0174;
        L_0x0168:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.chat_id;
            if (r18 == 0) goto L_0x02d1;
        L_0x0174:
            if (r14 != 0) goto L_0x02d1;
        L_0x0176:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.needPhonebook;
            if (r18 == 0) goto L_0x01cf;
        L_0x0182:
            if (r13 != 0) goto L_0x002e;
        L_0x0184:
            r10 = new android.content.Intent;	 Catch:{ Exception -> 0x01c5 }
            r18 = "android.intent.action.SEND";
            r0 = r18;
            r10.<init>(r0);	 Catch:{ Exception -> 0x01c5 }
            r18 = "text/plain";
            r0 = r18;
            r10.setType(r0);	 Catch:{ Exception -> 0x01c5 }
            r18 = "android.intent.extra.TEXT";
            r19 = org.telegram.messenger.ContactsController.getInstance();	 Catch:{ Exception -> 0x01c5 }
            r19 = r19.getInviteText();	 Catch:{ Exception -> 0x01c5 }
            r0 = r18;
            r1 = r19;
            r10.putExtra(r0, r1);	 Catch:{ Exception -> 0x01c5 }
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;	 Catch:{ Exception -> 0x01c5 }
            r18 = r0;
            r18 = r18.getParentActivity();	 Catch:{ Exception -> 0x01c5 }
            r19 = "InviteFriends";
            r20 = 2131165725; // 0x7f07021d float:1.7945675E38 double:1.0529357703E-314;
            r19 = org.telegram.messenger.LocaleController.getString(r19, r20);	 Catch:{ Exception -> 0x01c5 }
            r0 = r19;
            r19 = android.content.Intent.createChooser(r10, r0);	 Catch:{ Exception -> 0x01c5 }
            r20 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r18.startActivityForResult(r19, r20);	 Catch:{ Exception -> 0x01c5 }
            goto L_0x002e;
        L_0x01c5:
            r9 = move-exception;
            r18 = "tmessages";
            r0 = r18;
            org.telegram.messenger.FileLog.m13e(r0, r9);
            goto L_0x002e;
        L_0x01cf:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.chat_id;
            if (r18 == 0) goto L_0x01f7;
        L_0x01db:
            if (r13 != 0) goto L_0x002e;
        L_0x01dd:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.GroupInviteActivity;
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r20 = r0;
            r20 = r20.chat_id;
            r19.<init>(r20);
            r18.presentFragment(r19);
            goto L_0x002e;
        L_0x01f7:
            if (r13 != 0) goto L_0x0219;
        L_0x01f9:
            r18 = "chat_create";
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r19 = r0;
            r18 = org.telegram.messenger.MessagesController.isFeatureEnabled(r18, r19);
            if (r18 == 0) goto L_0x002e;
        L_0x0207:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.GroupCreateActivity;
            r19.<init>();
            r20 = 0;
            r18.presentFragment(r19, r20);
            goto L_0x002e;
        L_0x0219:
            r18 = 1;
            r0 = r18;
            if (r13 != r0) goto L_0x0264;
        L_0x021f:
            r6 = new android.os.Bundle;
            r6.<init>();
            r18 = "onlyUsers";
            r19 = 1;
            r0 = r18;
            r1 = r19;
            r6.putBoolean(r0, r1);
            r18 = "destroyAfterSelect";
            r19 = 1;
            r0 = r18;
            r1 = r19;
            r6.putBoolean(r0, r1);
            r18 = "createSecretChat";
            r19 = 1;
            r0 = r18;
            r1 = r19;
            r6.putBoolean(r0, r1);
            r18 = "allowBots";
            r19 = 0;
            r0 = r18;
            r1 = r19;
            r6.putBoolean(r0, r1);
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.ContactsActivity;
            r0 = r19;
            r0.<init>(r6);
            r20 = 0;
            r18.presentFragment(r19, r20);
            goto L_0x002e;
        L_0x0264:
            r18 = 2;
            r0 = r18;
            if (r13 != r0) goto L_0x002e;
        L_0x026a:
            r18 = "broadcast_create";
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r19 = r0;
            r18 = org.telegram.messenger.MessagesController.isFeatureEnabled(r18, r19);
            if (r18 == 0) goto L_0x002e;
        L_0x0278:
            r18 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r19 = "mainconfig";
            r20 = 0;
            r12 = r18.getSharedPreferences(r19, r20);
            r18 = "channel_intro";
            r19 = 0;
            r0 = r18;
            r1 = r19;
            r18 = r12.getBoolean(r0, r1);
            if (r18 == 0) goto L_0x02b2;
        L_0x0290:
            r6 = new android.os.Bundle;
            r6.<init>();
            r18 = "step";
            r19 = 0;
            r0 = r18;
            r1 = r19;
            r6.putInt(r0, r1);
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.ChannelCreateActivity;
            r0 = r19;
            r0.<init>(r6);
            r18.presentFragment(r19);
            goto L_0x002e;
        L_0x02b2:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.ChannelIntroActivity;
            r19.<init>();
            r18.presentFragment(r19);
            r18 = r12.edit();
            r19 = "channel_intro";
            r20 = 1;
            r18 = r18.putBoolean(r19, r20);
            r18.commit();
            goto L_0x002e;
        L_0x02d1:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.listViewAdapter;
            r0 = r18;
            r11 = r0.getItem(r14, r13);
            r0 = r11 instanceof org.telegram.tgnet.TLRPC.User;
            r18 = r0;
            if (r18 == 0) goto L_0x039a;
        L_0x02e7:
            r16 = r11;
            r16 = (org.telegram.tgnet.TLRPC.User) r16;
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.returnAsResult;
            if (r18 == 0) goto L_0x0334;
        L_0x02f7:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.ignoreUsers;
            if (r18 == 0) goto L_0x031d;
        L_0x0303:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.ignoreUsers;
            r0 = r16;
            r0 = r0.id;
            r19 = r0;
            r19 = java.lang.Integer.valueOf(r19);
            r18 = r18.containsKey(r19);
            if (r18 != 0) goto L_0x002e;
        L_0x031d:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = 1;
            r20 = 0;
            r0 = r18;
            r1 = r16;
            r2 = r19;
            r3 = r20;
            r0.didSelectResult(r1, r2, r3);
            goto L_0x002e;
        L_0x0334:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.createSecretChat;
            if (r18 == 0) goto L_0x0364;
        L_0x0340:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = 1;
            r18.creatingChat = r19;
            r18 = org.telegram.messenger.SecretChatHelper.getInstance();
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r19 = r0;
            r19 = r19.getParentActivity();
            r0 = r18;
            r1 = r19;
            r2 = r16;
            r0.startSecretChat(r1, r2);
            goto L_0x002e;
        L_0x0364:
            r6 = new android.os.Bundle;
            r6.<init>();
            r18 = "user_id";
            r0 = r16;
            r0 = r0.id;
            r19 = r0;
            r0 = r18;
            r1 = r19;
            r6.putInt(r0, r1);
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r0 = r18;
            r18 = org.telegram.messenger.MessagesController.checkCanOpenChat(r6, r0);
            if (r18 == 0) goto L_0x002e;
        L_0x0386:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = new org.telegram.ui.ChatActivity;
            r0 = r19;
            r0.<init>(r6);
            r20 = 1;
            r18.presentFragment(r19, r20);
            goto L_0x002e;
        L_0x039a:
            r0 = r11 instanceof org.telegram.messenger.ContactsController.Contact;
            r18 = r0;
            if (r18 == 0) goto L_0x002e;
        L_0x03a0:
            r8 = r11;
            r8 = (org.telegram.messenger.ContactsController.Contact) r8;
            r15 = 0;
            r0 = r8.phones;
            r18 = r0;
            r18 = r18.isEmpty();
            if (r18 != 0) goto L_0x03ba;
        L_0x03ae:
            r0 = r8.phones;
            r18 = r0;
            r19 = 0;
            r15 = r18.get(r19);
            r15 = (java.lang.String) r15;
        L_0x03ba:
            if (r15 == 0) goto L_0x002e;
        L_0x03bc:
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.getParentActivity();
            if (r18 == 0) goto L_0x002e;
        L_0x03c8:
            r7 = new android.app.AlertDialog$Builder;
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r18 = r18.getParentActivity();
            r0 = r18;
            r7.<init>(r0);
            r18 = "InviteUser";
            r19 = 2131165731; // 0x7f070223 float:1.7945687E38 double:1.0529357733E-314;
            r18 = org.telegram.messenger.LocaleController.getString(r18, r19);
            r0 = r18;
            r7.setMessage(r0);
            r18 = "AppName";
            r19 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
            r18 = org.telegram.messenger.LocaleController.getString(r18, r19);
            r0 = r18;
            r7.setTitle(r0);
            r5 = r15;
            r18 = "OK";
            r19 = 2131165993; // 0x7f070329 float:1.7946219E38 double:1.0529359027E-314;
            r18 = org.telegram.messenger.LocaleController.getString(r18, r19);
            r19 = new org.telegram.ui.ContactsActivity$4$1;
            r0 = r19;
            r1 = r22;
            r0.<init>(r5);
            r0 = r18;
            r1 = r19;
            r7.setPositiveButton(r0, r1);
            r18 = "Cancel";
            r19 = 2131165374; // 0x7f0700be float:1.7944963E38 double:1.052935597E-314;
            r18 = org.telegram.messenger.LocaleController.getString(r18, r19);
            r19 = 0;
            r0 = r18;
            r1 = r19;
            r7.setNegativeButton(r0, r1);
            r0 = r22;
            r0 = org.telegram.ui.ContactsActivity.this;
            r18 = r0;
            r19 = r7.create();
            r18.showDialog(r19);
            goto L_0x002e;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsActivity.4.onItemClick(android.widget.AdapterView, android.view.View, int, long):void");
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity.5 */
    class C12085 implements OnScrollListener {
        C12085() {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 && ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (absListView.isFastScrollEnabled()) {
                AndroidUtilities.clearDrawableAnimation(absListView);
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity.6 */
    class C12096 implements TextWatcher {
        final /* synthetic */ EditText val$editTextFinal;

        C12096(EditText editText) {
            this.val$editTextFinal = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            try {
                String str = s.toString();
                if (str.length() != 0) {
                    int value = Utilities.parseInt(str).intValue();
                    if (value < 0) {
                        this.val$editTextFinal.setText("0");
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    } else if (value > 300) {
                        this.val$editTextFinal.setText("300");
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    } else if (!str.equals(TtmlNode.ANONYMOUS_REGION_ID + value)) {
                        this.val$editTextFinal.setText(TtmlNode.ANONYMOUS_REGION_ID + value);
                        this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity.7 */
    class C12107 implements OnClickListener {
        final /* synthetic */ EditText val$finalEditText;
        final /* synthetic */ User val$user;

        C12107(User user, EditText editText) {
            this.val$user = user;
            this.val$finalEditText = editText;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ContactsActivity.this.didSelectResult(this.val$user, false, this.val$finalEditText != null ? this.val$finalEditText.getText().toString() : "0");
        }
    }

    public interface ContactsActivityDelegate {
        void didSelectContact(User user, String str);
    }

    /* renamed from: org.telegram.ui.ContactsActivity.1 */
    class C18501 extends ActionBarMenuOnItemClick {
        C18501() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ContactsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ContactsActivity.2 */
    class C18512 extends ActionBarMenuItemSearchListener {
        C18512() {
        }

        public void onSearchExpand() {
            ContactsActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
            ContactsActivity.this.searching = false;
            ContactsActivity.this.searchWas = false;
            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
            ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            ContactsActivity.this.listView.setFastScrollAlwaysVisible(true);
            ContactsActivity.this.listView.setFastScrollEnabled(true);
            ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
            ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0691R.string.NoContacts));
        }

        public void onTextChanged(EditText editText) {
            if (ContactsActivity.this.searchListViewAdapter != null) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ContactsActivity.this.searchWas = true;
                    if (ContactsActivity.this.listView != null) {
                        ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                        ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ContactsActivity.this.listView.setFastScrollAlwaysVisible(false);
                        ContactsActivity.this.listView.setFastScrollEnabled(false);
                        ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                    if (ContactsActivity.this.emptyTextView != null) {
                        ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", C0691R.string.NoResult));
                    }
                }
                ContactsActivity.this.searchListViewAdapter.searchDialogs(text);
            }
        }
    }

    public ContactsActivity(Bundle args) {
        super(args);
        this.creatingChat = false;
        this.allowBots = true;
        this.needForwardCount = true;
        this.selectAlertString = null;
        this.allowUsernameSearch = true;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        if (this.arguments != null) {
            this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.chat_id = this.arguments.getInt("chat_id", 0);
        } else {
            this.needPhonebook = true;
        }
        ContactsController.getInstance().checkInviteText();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
    }

    public View createView(Context context) {
        boolean z;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (!this.destroyAfterSelect) {
            this.actionBar.setTitle(LocaleController.getString("Contacts", C0691R.string.Contacts));
        } else if (this.returnAsResult) {
            this.actionBar.setTitle(LocaleController.getString("SelectContact", C0691R.string.SelectContact));
        } else if (this.createSecretChat) {
            this.actionBar.setTitle(LocaleController.getString("NewSecretChat", C0691R.string.NewSecretChat));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", C0691R.string.NewMessageTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C18501());
        this.actionBar.createMenu().addItem(0, (int) C0691R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18512()).getSearchField().setHint(LocaleController.getString("Search", C0691R.string.Search));
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots);
        int i = this.onlyUsers ? 1 : 0;
        boolean z2 = this.needPhonebook;
        HashMap hashMap = this.ignoreUsers;
        if (this.chat_id != 0) {
            z = true;
        } else {
            z = false;
        }
        this.listViewAdapter = new ContactsAdapter(context, i, z2, hashMap, z);
        this.fragmentView = new FrameLayout(context);
        LinearLayout emptyTextLayout = new LinearLayout(context);
        emptyTextLayout.setVisibility(4);
        emptyTextLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(emptyTextLayout);
        LayoutParams layoutParams = (LayoutParams) emptyTextLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        emptyTextLayout.setLayoutParams(layoutParams);
        emptyTextLayout.setOnTouchListener(new C12053());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(1, 20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", C0691R.string.NoContacts));
        emptyTextLayout.addView(this.emptyTextView);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) this.emptyTextView.getLayoutParams();
        layoutParams1.width = -1;
        layoutParams1.height = -1;
        layoutParams1.weight = 0.5f;
        this.emptyTextView.setLayoutParams(layoutParams1);
        FrameLayout frameLayout = new FrameLayout(context);
        emptyTextLayout.addView(frameLayout);
        layoutParams1 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams1.width = -1;
        layoutParams1.height = -1;
        layoutParams1.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams1);
        this.listView = new LetterSectionsListView(context);
        this.listView.setEmptyView(emptyTextLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C12074());
        this.listView.setOnScrollListener(new C12085());
        return this.fragmentView;
    }

    private void didSelectResult(User user, boolean useAlert, String param) {
        if (!useAlert || this.selectAlertString == null) {
            if (this.delegate != null) {
                this.delegate.didSelectContact(user, param);
                this.delegate = null;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            if (user.bot && user.bot_nochats) {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", C0691R.string.BotCantJoinGroups), 0).show();
                    return;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                    return;
                }
            }
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            String message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            EditText editText = null;
            if (!user.bot && this.needForwardCount) {
                message = String.format("%s\n\n%s", new Object[]{message, LocaleController.getString("AddToTheGroupForwardCount", C0691R.string.AddToTheGroupForwardCount)});
                editText = new EditText(getParentActivity());
                editText.setTextSize(18.0f);
                editText.setText("50");
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                editText.addTextChangedListener(new C12096(editText));
                builder.setView(editText);
            }
            builder.setMessage(message);
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C12107(user, editText));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            showDialog(builder.create());
            if (editText != null) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof LayoutParams) {
                        ((LayoutParams) layoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(10.0f);
                    layoutParams.leftMargin = dp;
                    layoutParams.rightMargin = dp;
                    editText.setLayoutParams(layoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.contactsDidLoaded) {
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                EncryptedChat encryptedChat = args[0];
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2), true);
            }
        } else if (id == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void setDelegate(ContactsActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setIgnoreUsers(HashMap<Integer, User> users) {
        this.ignoreUsers = users;
    }
}
