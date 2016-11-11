package org.telegram.customMessenger;

import android.content.Context;
import java.util.ArrayList;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;

public class CustomGroupManager {
    private final String PREFS_TAG;
    private Context _context;

    /* renamed from: org.telegram.customMessenger.CustomGroupManager.1 */
    class C16701 implements RequestDelegate {
        final /* synthetic */ String val$initialUrl;

        C16701(String str) {
            this.val$initialUrl = str;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesController.getInstance().processUpdates((Updates) response, false);
            }
            Updates updates = (Updates) response;
            if (updates != null && updates.chats != null && !updates.chats.isEmpty()) {
                Chat chat = (Chat) updates.chats.get(0);
                CustomGroupManager.this._context.getSharedPreferences("CustomGroupUrls", 0).edit().putBoolean(this.val$initialUrl, true).apply();
                MessagesController.getInstance().addUserToChat(chat.id, UserConfig.getCurrentUser(), null, 0, null, null);
            }
        }
    }

    /* renamed from: org.telegram.customMessenger.CustomGroupManager.2 */
    class C16712 implements RequestDelegate {
        final /* synthetic */ String val$initialUrl;

        C16712(String str) {
            this.val$initialUrl = str;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
                MessagesController.getInstance().putUsers(res.users, false);
                MessagesController.getInstance().putChats(res.chats, false);
                MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, false, true);
                if (!res.chats.isEmpty()) {
                    int chatId = ((Chat) res.chats.get(0)).id;
                    CustomGroupManager.this._context.getSharedPreferences("CustomGroupUrls", 0).edit().putBoolean(this.val$initialUrl, true).apply();
                    MessagesController.getInstance().addUserToChat(chatId, UserConfig.getCurrentUser(), null, 0, null, null);
                }
                if (!res.users.isEmpty()) {
                    User user = (User) res.users.get(0);
                    if (user.bot) {
                        ArrayList<User> users = new ArrayList();
                        users.add(user);
                        MessagesController.getInstance().putUsers(users, false);
                        MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                        SendMessagesHelper.getInstance().sendMessage("/start", (long) user.id, null, null, false, null, null, null);
                    }
                }
            }
        }
    }

    public CustomGroupManager(Context context) {
        this.PREFS_TAG = "CustomGroupUrls";
        this._context = context;
    }

    private boolean checkUrlAddedBefore(String url) {
        return this._context.getSharedPreferences("CustomGroupUrls", 0).getBoolean(url, false);
    }

    public void addGroupsFromResources() {
        for (String url : this._context.getResources().getStringArray(C0691R.array.ChannelUrls)) {
            try {
                if (!checkUrlAddedBefore(url)) {
                    parseGroupUrl(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseGroupUrl(java.lang.String r22) {
        /*
        r21 = this;
        r9 = r22;
        r5 = android.net.Uri.parse(r22);
        r17 = 0;
        r6 = 0;
        r16 = 0;
        r4 = 0;
        r3 = 0;
        r10 = 0;
        r11 = 0;
        r7 = 0;
        if (r5 == 0) goto L_0x0078;
    L_0x0012:
        r14 = r5.getScheme();
        if (r14 == 0) goto L_0x0316;
    L_0x0018:
        r18 = "http";
        r0 = r18;
        r18 = r14.equals(r0);
        if (r18 != 0) goto L_0x002c;
    L_0x0022:
        r18 = "https";
        r0 = r18;
        r18 = r14.equals(r0);
        if (r18 == 0) goto L_0x018c;
    L_0x002c:
        r18 = r5.getHost();
        r8 = r18.toLowerCase();
        r18 = "telegram.me";
        r0 = r18;
        r18 = r8.equals(r0);
        if (r18 != 0) goto L_0x0048;
    L_0x003e:
        r18 = "telegram.dog";
        r0 = r18;
        r18 = r8.equals(r0);
        if (r18 == 0) goto L_0x0078;
    L_0x0048:
        r12 = r5.getPath();
        if (r12 == 0) goto L_0x0078;
    L_0x004e:
        r18 = r12.length();
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x0078;
    L_0x005a:
        r18 = 1;
        r0 = r18;
        r12 = r12.substring(r0);
        r18 = "joinchat/";
        r0 = r18;
        r18 = r12.startsWith(r0);
        if (r18 == 0) goto L_0x00bf;
    L_0x006c:
        r18 = "joinchat/";
        r19 = "";
        r0 = r18;
        r1 = r19;
        r6 = r12.replace(r0, r1);
    L_0x0078:
        if (r6 == 0) goto L_0x009f;
    L_0x007a:
        r18 = r6.length();
        if (r18 == 0) goto L_0x009f;
    L_0x0080:
        r13 = new org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
        r13.<init>();
        r13.hash = r6;
        r18 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r19 = new org.telegram.customMessenger.CustomGroupManager$1;
        r0 = r19;
        r1 = r21;
        r0.<init>(r9);
        r20 = 2;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r0.sendRequest(r13, r1, r2);
    L_0x009f:
        if (r17 == 0) goto L_0x00be;
    L_0x00a1:
        r13 = new org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
        r13.<init>();
        r0 = r17;
        r13.username = r0;
        r18 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r19 = new org.telegram.customMessenger.CustomGroupManager$2;
        r0 = r19;
        r1 = r21;
        r0.<init>(r9);
        r0 = r18;
        r1 = r19;
        r0.sendRequest(r13, r1);
    L_0x00be:
        return;
    L_0x00bf:
        r18 = "addstickers/";
        r0 = r18;
        r18 = r12.startsWith(r0);
        if (r18 == 0) goto L_0x00d6;
    L_0x00c9:
        r18 = "addstickers/";
        r19 = "";
        r0 = r18;
        r1 = r19;
        r16 = r12.replace(r0, r1);
        goto L_0x0078;
    L_0x00d6:
        r18 = "msg/";
        r0 = r18;
        r18 = r12.startsWith(r0);
        if (r18 != 0) goto L_0x00ea;
    L_0x00e0:
        r18 = "share/";
        r0 = r18;
        r18 = r12.startsWith(r0);
        if (r18 == 0) goto L_0x0139;
    L_0x00ea:
        r18 = "url";
        r0 = r18;
        r10 = r5.getQueryParameter(r0);
        if (r10 != 0) goto L_0x00f6;
    L_0x00f4:
        r10 = "";
    L_0x00f6:
        r18 = "text";
        r0 = r18;
        r18 = r5.getQueryParameter(r0);
        if (r18 == 0) goto L_0x0078;
    L_0x0100:
        r18 = r10.length();
        if (r18 <= 0) goto L_0x011c;
    L_0x0106:
        r7 = 1;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r18;
        r18 = r0.append(r10);
        r19 = "\n";
        r18 = r18.append(r19);
        r10 = r18.toString();
    L_0x011c:
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r18;
        r18 = r0.append(r10);
        r19 = "text";
        r0 = r19;
        r19 = r5.getQueryParameter(r0);
        r18 = r18.append(r19);
        r10 = r18.toString();
        goto L_0x0078;
    L_0x0139:
        r18 = r12.length();
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 < r1) goto L_0x0078;
    L_0x0145:
        r15 = r5.getPathSegments();
        r18 = r15.size();
        if (r18 <= 0) goto L_0x017a;
    L_0x014f:
        r18 = 0;
        r0 = r18;
        r17 = r15.get(r0);
        r17 = (java.lang.String) r17;
        r18 = r15.size();
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x017a;
    L_0x0165:
        r18 = 1;
        r0 = r18;
        r18 = r15.get(r0);
        r18 = (java.lang.String) r18;
        r11 = org.telegram.messenger.Utilities.parseInt(r18);
        r18 = r11.intValue();
        if (r18 != 0) goto L_0x017a;
    L_0x0179:
        r11 = 0;
    L_0x017a:
        r18 = "start";
        r0 = r18;
        r4 = r5.getQueryParameter(r0);
        r18 = "startgroup";
        r0 = r18;
        r3 = r5.getQueryParameter(r0);
        goto L_0x0078;
    L_0x018c:
        r18 = "tg";
        r0 = r18;
        r18 = r14.equals(r0);
        if (r18 == 0) goto L_0x0078;
    L_0x0196:
        r18 = "tg:resolve";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 != 0) goto L_0x01ae;
    L_0x01a2:
        r18 = "tg://resolve";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 == 0) goto L_0x01f5;
    L_0x01ae:
        r18 = "tg:resolve";
        r19 = "tg://telegram.org";
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r18 = r0.replace(r1, r2);
        r19 = "tg://resolve";
        r20 = "tg://telegram.org";
        r22 = r18.replace(r19, r20);
        r5 = android.net.Uri.parse(r22);
        r18 = "domain";
        r0 = r18;
        r17 = r5.getQueryParameter(r0);
        r18 = "start";
        r0 = r18;
        r4 = r5.getQueryParameter(r0);
        r18 = "startgroup";
        r0 = r18;
        r3 = r5.getQueryParameter(r0);
        r18 = "post";
        r0 = r18;
        r18 = r5.getQueryParameter(r0);
        r11 = org.telegram.messenger.Utilities.parseInt(r18);
        r18 = r11.intValue();
        if (r18 != 0) goto L_0x0078;
    L_0x01f2:
        r11 = 0;
        goto L_0x0078;
    L_0x01f5:
        r18 = "tg:join";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 != 0) goto L_0x020d;
    L_0x0201:
        r18 = "tg://join";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 == 0) goto L_0x0231;
    L_0x020d:
        r18 = "tg:join";
        r19 = "tg://telegram.org";
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r18 = r0.replace(r1, r2);
        r19 = "tg://join";
        r20 = "tg://telegram.org";
        r22 = r18.replace(r19, r20);
        r5 = android.net.Uri.parse(r22);
        r18 = "invite";
        r0 = r18;
        r6 = r5.getQueryParameter(r0);
        goto L_0x0078;
    L_0x0231:
        r18 = "tg:addstickers";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 != 0) goto L_0x0249;
    L_0x023d:
        r18 = "tg://addstickers";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 == 0) goto L_0x026d;
    L_0x0249:
        r18 = "tg:addstickers";
        r19 = "tg://telegram.org";
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r18 = r0.replace(r1, r2);
        r19 = "tg://addstickers";
        r20 = "tg://telegram.org";
        r22 = r18.replace(r19, r20);
        r5 = android.net.Uri.parse(r22);
        r18 = "set";
        r0 = r18;
        r16 = r5.getQueryParameter(r0);
        goto L_0x0078;
    L_0x026d:
        r18 = "tg:msg";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 != 0) goto L_0x029d;
    L_0x0279:
        r18 = "tg://msg";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 != 0) goto L_0x029d;
    L_0x0285:
        r18 = "tg://share";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 != 0) goto L_0x029d;
    L_0x0291:
        r18 = "tg:share";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 == 0) goto L_0x0078;
    L_0x029d:
        r18 = "tg:msg";
        r19 = "tg://telegram.org";
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r18 = r0.replace(r1, r2);
        r19 = "tg://msg";
        r20 = "tg://telegram.org";
        r18 = r18.replace(r19, r20);
        r19 = "tg://share";
        r20 = "tg://telegram.org";
        r18 = r18.replace(r19, r20);
        r19 = "tg:share";
        r20 = "tg://telegram.org";
        r22 = r18.replace(r19, r20);
        r5 = android.net.Uri.parse(r22);
        r18 = "url";
        r0 = r18;
        r10 = r5.getQueryParameter(r0);
        if (r10 != 0) goto L_0x02d3;
    L_0x02d1:
        r10 = "";
    L_0x02d3:
        r18 = "text";
        r0 = r18;
        r18 = r5.getQueryParameter(r0);
        if (r18 == 0) goto L_0x0078;
    L_0x02dd:
        r18 = r10.length();
        if (r18 <= 0) goto L_0x02f9;
    L_0x02e3:
        r7 = 1;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r18;
        r18 = r0.append(r10);
        r19 = "\n";
        r18 = r18.append(r19);
        r10 = r18.toString();
    L_0x02f9:
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r18;
        r18 = r0.append(r10);
        r19 = "text";
        r0 = r19;
        r19 = r5.getQueryParameter(r0);
        r18 = r18.append(r19);
        r10 = r18.toString();
        goto L_0x0078;
    L_0x0316:
        r18 = "@";
        r0 = r22;
        r1 = r18;
        r18 = r0.startsWith(r1);
        if (r18 == 0) goto L_0x0078;
    L_0x0322:
        r18 = 1;
        r0 = r22;
        r1 = r18;
        r17 = r0.substring(r1);
        goto L_0x0078;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.customMessenger.CustomGroupManager.parseGroupUrl(java.lang.String):void");
    }
}
