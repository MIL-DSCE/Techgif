package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.VideoPlayer;

public class DialogsSearchAdapter extends BaseSearchAdapterRecycler {
    private DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private String lastMessagesSearchString;
    private int lastReqId;
    private int lastSearchId;
    private String lastSearchText;
    private Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private ArrayList<RecentSearchObject> recentSearchObjects;
    private HashMap<Long, RecentSearchObject> recentSearchObjectsById;
    private int reqId;
    private ArrayList<TLObject> searchResult;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MessageObject> searchResultMessages;
    private ArrayList<CharSequence> searchResultNames;
    private Timer searchTimer;

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.2 */
    class C09382 implements Runnable {

        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.2.1 */
        class C09361 implements Comparator<RecentSearchObject> {
            C09361() {
            }

            public int compare(RecentSearchObject lhs, RecentSearchObject rhs) {
                if (lhs.date < rhs.date) {
                    return 1;
                }
                if (lhs.date > rhs.date) {
                    return -1;
                }
                return 0;
            }
        }

        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.2.2 */
        class C09372 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;
            final /* synthetic */ HashMap val$hashMap;

            C09372(ArrayList arrayList, HashMap hashMap) {
                this.val$arrayList = arrayList;
                this.val$hashMap = hashMap;
            }

            public void run() {
                DialogsSearchAdapter.this.setRecentSearch(this.val$arrayList, this.val$hashMap);
            }
        }

        C09382() {
        }

        public void run() {
            try {
                long did;
                RecentSearchObject recentSearchObject;
                int a;
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
                ArrayList<Integer> usersToLoad = new ArrayList();
                ArrayList<Integer> chatsToLoad = new ArrayList();
                ArrayList<Integer> encryptedToLoad = new ArrayList();
                ArrayList<User> encUsers = new ArrayList();
                ArrayList<RecentSearchObject> arrayList = new ArrayList();
                HashMap<Long, RecentSearchObject> hashMap = new HashMap();
                while (cursor.next()) {
                    did = cursor.longValue(0);
                    boolean add = false;
                    int lower_id = (int) did;
                    int high_id = (int) (did >> 32);
                    if (lower_id == 0) {
                        if (DialogsSearchAdapter.this.dialogsType == 0) {
                            if (!encryptedToLoad.contains(Integer.valueOf(high_id))) {
                                encryptedToLoad.add(Integer.valueOf(high_id));
                                add = true;
                            }
                        }
                    } else if (high_id == 1) {
                        if (DialogsSearchAdapter.this.dialogsType == 0) {
                            if (!chatsToLoad.contains(Integer.valueOf(lower_id))) {
                                chatsToLoad.add(Integer.valueOf(lower_id));
                                add = true;
                            }
                        }
                    } else if (lower_id > 0) {
                        if (!(DialogsSearchAdapter.this.dialogsType == 2 || usersToLoad.contains(Integer.valueOf(lower_id)))) {
                            usersToLoad.add(Integer.valueOf(lower_id));
                            add = true;
                        }
                    } else {
                        if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                            chatsToLoad.add(Integer.valueOf(-lower_id));
                            add = true;
                        }
                    }
                    if (add) {
                        recentSearchObject = new RecentSearchObject();
                        recentSearchObject.did = did;
                        recentSearchObject.date = cursor.intValue(1);
                        arrayList.add(recentSearchObject);
                        hashMap.put(Long.valueOf(recentSearchObject.did), recentSearchObject);
                    }
                }
                cursor.dispose();
                ArrayList<User> users = new ArrayList();
                if (!encryptedToLoad.isEmpty()) {
                    ArrayList<EncryptedChat> encryptedChats = new ArrayList();
                    MessagesStorage.getInstance().getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
                    for (a = 0; a < encryptedChats.size(); a++) {
                        TLObject tLObject = (TLObject) encryptedChats.get(a);
                        ((RecentSearchObject) hashMap.get(Long.valueOf(((long) ((EncryptedChat) encryptedChats.get(a)).id) << 32))).object = r25;
                    }
                }
                if (!chatsToLoad.isEmpty()) {
                    ArrayList<Chat> chats = new ArrayList();
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                    for (a = 0; a < chats.size(); a++) {
                        Chat chat = (Chat) chats.get(a);
                        if (chat.id > 0) {
                            did = (long) (-chat.id);
                        } else {
                            did = AndroidUtilities.makeBroadcastId(chat.id);
                        }
                        if (chat.migrated_to != null) {
                            recentSearchObject = (RecentSearchObject) hashMap.remove(Long.valueOf(did));
                            if (recentSearchObject != null) {
                                arrayList.remove(recentSearchObject);
                            }
                        } else {
                            ((RecentSearchObject) hashMap.get(Long.valueOf(did))).object = chat;
                        }
                    }
                }
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                    for (a = 0; a < users.size(); a++) {
                        TLObject user = (User) users.get(a);
                        recentSearchObject = (RecentSearchObject) hashMap.get(Long.valueOf((long) user.id));
                        if (recentSearchObject != null) {
                            recentSearchObject.object = user;
                        }
                    }
                }
                Collections.sort(arrayList, new C09361());
                AndroidUtilities.runOnUIThread(new C09372(arrayList, hashMap));
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.3 */
    class C09393 implements Runnable {
        final /* synthetic */ long val$did;

        C09393(long j) {
            this.val$did = j;
        }

        public void run() {
            try {
                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
                state.requery();
                state.bindLong(1, this.val$did);
                state.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
                state.step();
                state.dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.4 */
    class C09404 implements Runnable {
        C09404() {
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.5 */
    class C09425 implements Runnable {
        final /* synthetic */ String val$query;
        final /* synthetic */ int val$searchId;

        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.5.1 */
        class C09411 implements Comparator<DialogSearchResult> {
            C09411() {
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

        C09425(String str, int i) {
            this.val$query = str;
            this.val$searchId = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r46 = this;
            r0 = r46;
            r0 = r0.val$query;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = r40.trim();	 Catch:{ Exception -> 0x0111 }
            r30 = r40.toLowerCase();	 Catch:{ Exception -> 0x0111 }
            r40 = r30.length();	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0042;
        L_0x0014:
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = -1;
            r40.lastSearchId = r41;	 Catch:{ Exception -> 0x0111 }
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r41.<init>();	 Catch:{ Exception -> 0x0111 }
            r42 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r42.<init>();	 Catch:{ Exception -> 0x0111 }
            r43 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r43.<init>();	 Catch:{ Exception -> 0x0111 }
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r44 = r0;
            r44 = r44.lastSearchId;	 Catch:{ Exception -> 0x0111 }
            r40.updateSearchResults(r41, r42, r43, r44);	 Catch:{ Exception -> 0x0111 }
        L_0x0041:
            return;
        L_0x0042:
            r40 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r30;
            r31 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r30.equals(r31);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x005a;
        L_0x0054:
            r40 = r31.length();	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x005c;
        L_0x005a:
            r31 = 0;
        L_0x005c:
            if (r31 == 0) goto L_0x011b;
        L_0x005e:
            r40 = 1;
        L_0x0060:
            r40 = r40 + 1;
            r0 = r40;
            r0 = new java.lang.String[r0];	 Catch:{ Exception -> 0x0111 }
            r29 = r0;
            r40 = 0;
            r29[r40] = r30;	 Catch:{ Exception -> 0x0111 }
            if (r31 == 0) goto L_0x0072;
        L_0x006e:
            r40 = 1;
            r29[r40] = r31;	 Catch:{ Exception -> 0x0111 }
        L_0x0072:
            r39 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r39.<init>();	 Catch:{ Exception -> 0x0111 }
            r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r7.<init>();	 Catch:{ Exception -> 0x0111 }
            r16 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r16.<init>();	 Catch:{ Exception -> 0x0111 }
            r15 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r15.<init>();	 Catch:{ Exception -> 0x0111 }
            r28 = 0;
            r11 = new java.util.HashMap;	 Catch:{ Exception -> 0x0111 }
            r11.<init>();	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x0111 }
            r40 = r40.getDatabase();	 Catch:{ Exception -> 0x0111 }
            r41 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400";
            r42 = 0;
            r0 = r42;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r42 = r0;
            r8 = r40.queryFinalized(r41, r42);	 Catch:{ Exception -> 0x0111 }
        L_0x00a3:
            r40 = r8.next();	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x018e;
        L_0x00a9:
            r40 = 0;
            r0 = r40;
            r20 = r8.longValue(r0);	 Catch:{ Exception -> 0x0111 }
            r10 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x0111 }
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r10.<init>(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = 1;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10.date = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = java.lang.Long.valueOf(r20);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r11.put(r0, r10);	 Catch:{ Exception -> 0x0111 }
            r0 = r20;
            r0 = (int) r0;	 Catch:{ Exception -> 0x0111 }
            r23 = r0;
            r40 = 32;
            r40 = r20 >> r40;
            r0 = r40;
            r0 = (int) r0;	 Catch:{ Exception -> 0x0111 }
            r18 = r0;
            if (r23 == 0) goto L_0x0167;
        L_0x00e7:
            r40 = 1;
            r0 = r18;
            r1 = r40;
            if (r0 != r1) goto L_0x011f;
        L_0x00ef:
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = r40.dialogsType;	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x00a3;
        L_0x00fb:
            r40 = java.lang.Integer.valueOf(r23);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r40 = r7.contains(r0);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x00a3;
        L_0x0107:
            r40 = java.lang.Integer.valueOf(r23);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r7.add(r0);	 Catch:{ Exception -> 0x0111 }
            goto L_0x00a3;
        L_0x0111:
            r14 = move-exception;
            r40 = "tmessages";
            r0 = r40;
            org.telegram.messenger.FileLog.m13e(r0, r14);
            goto L_0x0041;
        L_0x011b:
            r40 = 0;
            goto L_0x0060;
        L_0x011f:
            if (r23 <= 0) goto L_0x0146;
        L_0x0121:
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = r40.dialogsType;	 Catch:{ Exception -> 0x0111 }
            r41 = 2;
            r0 = r40;
            r1 = r41;
            if (r0 == r1) goto L_0x00a3;
        L_0x0133:
            r40 = java.lang.Integer.valueOf(r23);	 Catch:{ Exception -> 0x0111 }
            r40 = r39.contains(r40);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x00a3;
        L_0x013d:
            r40 = java.lang.Integer.valueOf(r23);	 Catch:{ Exception -> 0x0111 }
            r39.add(r40);	 Catch:{ Exception -> 0x0111 }
            goto L_0x00a3;
        L_0x0146:
            r0 = r23;
            r0 = -r0;
            r40 = r0;
            r40 = java.lang.Integer.valueOf(r40);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r40 = r7.contains(r0);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x00a3;
        L_0x0157:
            r0 = r23;
            r0 = -r0;
            r40 = r0;
            r40 = java.lang.Integer.valueOf(r40);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r7.add(r0);	 Catch:{ Exception -> 0x0111 }
            goto L_0x00a3;
        L_0x0167:
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = r40.dialogsType;	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x00a3;
        L_0x0173:
            r40 = java.lang.Integer.valueOf(r18);	 Catch:{ Exception -> 0x0111 }
            r0 = r16;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x00a3;
        L_0x0181:
            r40 = java.lang.Integer.valueOf(r18);	 Catch:{ Exception -> 0x0111 }
            r0 = r16;
            r1 = r40;
            r0.add(r1);	 Catch:{ Exception -> 0x0111 }
            goto L_0x00a3;
        L_0x018e:
            r8.dispose();	 Catch:{ Exception -> 0x0111 }
            r40 = r39.isEmpty();	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0344;
        L_0x0197:
            r40 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x0111 }
            r40 = r40.getDatabase();	 Catch:{ Exception -> 0x0111 }
            r41 = java.util.Locale.US;	 Catch:{ Exception -> 0x0111 }
            r42 = "SELECT data, status, name FROM users WHERE uid IN(%s)";
            r43 = 1;
            r0 = r43;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r43 = r0;
            r44 = 0;
            r45 = ",";
            r0 = r45;
            r1 = r39;
            r45 = android.text.TextUtils.join(r0, r1);	 Catch:{ Exception -> 0x0111 }
            r43[r44] = r45;	 Catch:{ Exception -> 0x0111 }
            r41 = java.lang.String.format(r41, r42, r43);	 Catch:{ Exception -> 0x0111 }
            r42 = 0;
            r0 = r42;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r42 = r0;
            r8 = r40.queryFinalized(r41, r42);	 Catch:{ Exception -> 0x0111 }
        L_0x01c9:
            r40 = r8.next();	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x0341;
        L_0x01cf:
            r40 = 2;
            r0 = r40;
            r24 = r8.stringValue(r0);	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r24;
            r33 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r33;
            r40 = r0.equals(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x01ef;
        L_0x01ed:
            r33 = 0;
        L_0x01ef:
            r37 = 0;
            r40 = ";;;";
            r0 = r24;
            r1 = r40;
            r38 = r0.lastIndexOf(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = -1;
            r0 = r38;
            r1 = r40;
            if (r0 == r1) goto L_0x020d;
        L_0x0203:
            r40 = r38 + 3;
            r0 = r24;
            r1 = r40;
            r37 = r0.substring(r1);	 Catch:{ Exception -> 0x0111 }
        L_0x020d:
            r17 = 0;
            r5 = r29;
            r0 = r5.length;	 Catch:{ Exception -> 0x0111 }
            r22 = r0;
            r19 = 0;
        L_0x0216:
            r0 = r19;
            r1 = r22;
            if (r0 >= r1) goto L_0x01c9;
        L_0x021c:
            r25 = r5[r19];	 Catch:{ Exception -> 0x0111 }
            r40 = r24.startsWith(r25);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0272;
        L_0x0224:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0272;
        L_0x0245:
            if (r33 == 0) goto L_0x02f2;
        L_0x0247:
            r0 = r33;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0272;
        L_0x0251:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r33;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x02f2;
        L_0x0272:
            r17 = 1;
        L_0x0274:
            if (r17 == 0) goto L_0x033d;
        L_0x0276:
            r40 = 0;
            r0 = r40;
            r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x0111 }
            if (r9 == 0) goto L_0x01c9;
        L_0x0280:
            r40 = 0;
            r0 = r40;
            r40 = r9.readInt32(r0);	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r36 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x0111 }
            r9.reuse();	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.id;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r40;
            r0 = (long) r0;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = java.lang.Long.valueOf(r40);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10 = r11.get(r0);	 Catch:{ Exception -> 0x0111 }
            r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.status;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 == 0) goto L_0x02c8;
        L_0x02b4:
            r0 = r36;
            r0 = r0.status;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = 1;
            r0 = r41;
            r41 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r41;
            r1 = r40;
            r1.expires = r0;	 Catch:{ Exception -> 0x0111 }
        L_0x02c8:
            r40 = 1;
            r0 = r17;
            r1 = r40;
            if (r0 != r1) goto L_0x0302;
        L_0x02d0:
            r0 = r36;
            r0 = r0.first_name;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r36;
            r0 = r0.last_name;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r0 = r40;
            r1 = r41;
            r2 = r25;
            r40 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r2);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10.name = r0;	 Catch:{ Exception -> 0x0111 }
        L_0x02ea:
            r0 = r36;
            r10.object = r0;	 Catch:{ Exception -> 0x0111 }
            r28 = r28 + 1;
            goto L_0x01c9;
        L_0x02f2:
            if (r37 == 0) goto L_0x0274;
        L_0x02f4:
            r0 = r37;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x0274;
        L_0x02fe:
            r17 = 2;
            goto L_0x0274;
        L_0x0302:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = "@";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.username;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r42 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r42.<init>();	 Catch:{ Exception -> 0x0111 }
            r43 = "@";
            r42 = r42.append(r43);	 Catch:{ Exception -> 0x0111 }
            r0 = r42;
            r1 = r25;
            r42 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r42 = r42.toString();	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.AndroidUtilities.generateSearchName(r40, r41, r42);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10.name = r0;	 Catch:{ Exception -> 0x0111 }
            goto L_0x02ea;
        L_0x033d:
            r19 = r19 + 1;
            goto L_0x0216;
        L_0x0341:
            r8.dispose();	 Catch:{ Exception -> 0x0111 }
        L_0x0344:
            r40 = r7.isEmpty();	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x047e;
        L_0x034a:
            r40 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x0111 }
            r40 = r40.getDatabase();	 Catch:{ Exception -> 0x0111 }
            r41 = java.util.Locale.US;	 Catch:{ Exception -> 0x0111 }
            r42 = "SELECT data, name FROM chats WHERE uid IN(%s)";
            r43 = 1;
            r0 = r43;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r43 = r0;
            r44 = 0;
            r45 = ",";
            r0 = r45;
            r45 = android.text.TextUtils.join(r0, r7);	 Catch:{ Exception -> 0x0111 }
            r43[r44] = r45;	 Catch:{ Exception -> 0x0111 }
            r41 = java.lang.String.format(r41, r42, r43);	 Catch:{ Exception -> 0x0111 }
            r42 = 0;
            r0 = r42;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r42 = r0;
            r8 = r40.queryFinalized(r41, r42);	 Catch:{ Exception -> 0x0111 }
        L_0x037a:
            r40 = r8.next();	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x047b;
        L_0x0380:
            r40 = 1;
            r0 = r40;
            r24 = r8.stringValue(r0);	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r24;
            r33 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r33;
            r40 = r0.equals(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x03a0;
        L_0x039e:
            r33 = 0;
        L_0x03a0:
            r5 = r29;
            r0 = r5.length;	 Catch:{ Exception -> 0x0111 }
            r22 = r0;
            r19 = 0;
        L_0x03a7:
            r0 = r19;
            r1 = r22;
            if (r0 >= r1) goto L_0x037a;
        L_0x03ad:
            r25 = r5[r19];	 Catch:{ Exception -> 0x0111 }
            r40 = r24.startsWith(r25);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0403;
        L_0x03b5:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0403;
        L_0x03d6:
            if (r33 == 0) goto L_0x0477;
        L_0x03d8:
            r0 = r33;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0403;
        L_0x03e2:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r33;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x0477;
        L_0x0403:
            r40 = 0;
            r0 = r40;
            r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x0111 }
            if (r9 == 0) goto L_0x037a;
        L_0x040d:
            r40 = 0;
            r0 = r40;
            r40 = r9.readInt32(r0);	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r6 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x0111 }
            r9.reuse();	 Catch:{ Exception -> 0x0111 }
            if (r6 == 0) goto L_0x037a;
        L_0x0424:
            r0 = r6.deactivated;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 != 0) goto L_0x037a;
        L_0x042a:
            r40 = org.telegram.messenger.ChatObject.isChannel(r6);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x0436;
        L_0x0430:
            r40 = org.telegram.messenger.ChatObject.isNotInChat(r6);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x037a;
        L_0x0436:
            r0 = r6.id;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 <= 0) goto L_0x046e;
        L_0x043c:
            r0 = r6.id;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r40;
            r0 = -r0;
            r40 = r0;
            r0 = r40;
            r12 = (long) r0;	 Catch:{ Exception -> 0x0111 }
        L_0x0448:
            r40 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10 = r11.get(r0);	 Catch:{ Exception -> 0x0111 }
            r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x0111 }
            r0 = r6.title;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r2 = r25;
            r40 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r2);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10.name = r0;	 Catch:{ Exception -> 0x0111 }
            r10.object = r6;	 Catch:{ Exception -> 0x0111 }
            r28 = r28 + 1;
            goto L_0x037a;
        L_0x046e:
            r0 = r6.id;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r12 = org.telegram.messenger.AndroidUtilities.makeBroadcastId(r40);	 Catch:{ Exception -> 0x0111 }
            goto L_0x0448;
        L_0x0477:
            r19 = r19 + 1;
            goto L_0x03a7;
        L_0x047b:
            r8.dispose();	 Catch:{ Exception -> 0x0111 }
        L_0x047e:
            r40 = r16.isEmpty();	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x071c;
        L_0x0484:
            r40 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x0111 }
            r40 = r40.getDatabase();	 Catch:{ Exception -> 0x0111 }
            r41 = java.util.Locale.US;	 Catch:{ Exception -> 0x0111 }
            r42 = "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)";
            r43 = 1;
            r0 = r43;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r43 = r0;
            r44 = 0;
            r45 = ",";
            r0 = r45;
            r1 = r16;
            r45 = android.text.TextUtils.join(r0, r1);	 Catch:{ Exception -> 0x0111 }
            r43[r44] = r45;	 Catch:{ Exception -> 0x0111 }
            r41 = java.lang.String.format(r41, r42, r43);	 Catch:{ Exception -> 0x0111 }
            r42 = 0;
            r0 = r42;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r42 = r0;
            r8 = r40.queryFinalized(r41, r42);	 Catch:{ Exception -> 0x0111 }
        L_0x04b6:
            r40 = r8.next();	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x0719;
        L_0x04bc:
            r40 = 1;
            r0 = r40;
            r24 = r8.stringValue(r0);	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r24;
            r33 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r33;
            r40 = r0.equals(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x04dc;
        L_0x04da:
            r33 = 0;
        L_0x04dc:
            r37 = 0;
            r40 = ";;;";
            r0 = r24;
            r1 = r40;
            r38 = r0.lastIndexOf(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = -1;
            r0 = r38;
            r1 = r40;
            if (r0 == r1) goto L_0x04fa;
        L_0x04f0:
            r40 = r38 + 2;
            r0 = r24;
            r1 = r40;
            r37 = r0.substring(r1);	 Catch:{ Exception -> 0x0111 }
        L_0x04fa:
            r17 = 0;
            r4 = 0;
        L_0x04fd:
            r0 = r29;
            r0 = r0.length;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r40;
            if (r4 >= r0) goto L_0x04b6;
        L_0x0506:
            r25 = r29[r4];	 Catch:{ Exception -> 0x0111 }
            r40 = r24.startsWith(r25);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x055c;
        L_0x050e:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x055c;
        L_0x052f:
            if (r33 == 0) goto L_0x06ca;
        L_0x0531:
            r0 = r33;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x055c;
        L_0x053b:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r33;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x06ca;
        L_0x055c:
            r17 = 1;
        L_0x055e:
            if (r17 == 0) goto L_0x0715;
        L_0x0560:
            r6 = 0;
            r36 = 0;
            r40 = 0;
            r0 = r40;
            r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x0111 }
            if (r9 == 0) goto L_0x0582;
        L_0x056d:
            r40 = 0;
            r0 = r40;
            r40 = r9.readInt32(r0);	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r6 = org.telegram.tgnet.TLRPC.EncryptedChat.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x0111 }
            r9.reuse();	 Catch:{ Exception -> 0x0111 }
        L_0x0582:
            r40 = 6;
            r0 = r40;
            r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x0111 }
            if (r9 == 0) goto L_0x05a1;
        L_0x058c:
            r40 = 0;
            r0 = r40;
            r40 = r9.readInt32(r0);	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r36 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x0111 }
            r9.reuse();	 Catch:{ Exception -> 0x0111 }
        L_0x05a1:
            if (r6 == 0) goto L_0x04b6;
        L_0x05a3:
            if (r36 == 0) goto L_0x04b6;
        L_0x05a5:
            r0 = r6.id;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r40;
            r0 = (long) r0;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r42 = 32;
            r40 = r40 << r42;
            r40 = java.lang.Long.valueOf(r40);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10 = r11.get(r0);	 Catch:{ Exception -> 0x0111 }
            r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x0111 }
            r40 = 2;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.user_id = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 3;
            r0 = r40;
            r40 = r8.byteArrayValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.a_or_b = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 4;
            r0 = r40;
            r40 = r8.byteArrayValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.auth_key = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 5;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.ttl = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 8;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.layer = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 9;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.seq_in = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 10;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.seq_out = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 11;
            r0 = r40;
            r35 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r40 = r35 >> 16;
            r0 = r40;
            r0 = (short) r0;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r40;
            r6.key_use_count_in = r0;	 Catch:{ Exception -> 0x0111 }
            r0 = r35;
            r0 = (short) r0;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r40;
            r6.key_use_count_out = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 12;
            r0 = r40;
            r40 = r8.longValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.exchange_id = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 13;
            r0 = r40;
            r40 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.key_create_date = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 14;
            r0 = r40;
            r40 = r8.longValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.future_key_fingerprint = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 15;
            r0 = r40;
            r40 = r8.byteArrayValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.future_auth_key = r0;	 Catch:{ Exception -> 0x0111 }
            r40 = 16;
            r0 = r40;
            r40 = r8.byteArrayValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r6.key_hash = r0;	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.status;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 == 0) goto L_0x0686;
        L_0x0672:
            r0 = r36;
            r0 = r0.status;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = 7;
            r0 = r41;
            r41 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r41;
            r1 = r40;
            r1.expires = r0;	 Catch:{ Exception -> 0x0111 }
        L_0x0686:
            r40 = 1;
            r0 = r17;
            r1 = r40;
            if (r0 != r1) goto L_0x06da;
        L_0x068e:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = "<c#ff00a60e>";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.first_name;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r0 = r36;
            r0 = r0.last_name;	 Catch:{ Exception -> 0x0111 }
            r42 = r0;
            r41 = org.telegram.messenger.ContactsController.formatName(r41, r42);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r41 = "</c>";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.AndroidUtilities.replaceTags(r40);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10.name = r0;	 Catch:{ Exception -> 0x0111 }
        L_0x06bf:
            r10.object = r6;	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r15.add(r0);	 Catch:{ Exception -> 0x0111 }
            r28 = r28 + 1;
            goto L_0x04b6;
        L_0x06ca:
            if (r37 == 0) goto L_0x055e;
        L_0x06cc:
            r0 = r37;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x055e;
        L_0x06d6:
            r17 = 2;
            goto L_0x055e;
        L_0x06da:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = "@";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.username;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r42 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r42.<init>();	 Catch:{ Exception -> 0x0111 }
            r43 = "@";
            r42 = r42.append(r43);	 Catch:{ Exception -> 0x0111 }
            r0 = r42;
            r1 = r25;
            r42 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r42 = r42.toString();	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.AndroidUtilities.generateSearchName(r40, r41, r42);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r10.name = r0;	 Catch:{ Exception -> 0x0111 }
            goto L_0x06bf;
        L_0x0715:
            r4 = r4 + 1;
            goto L_0x04fd;
        L_0x0719:
            r8.dispose();	 Catch:{ Exception -> 0x0111 }
        L_0x071c:
            r32 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r0 = r32;
            r1 = r28;
            r0.<init>(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r11.values();	 Catch:{ Exception -> 0x0111 }
            r19 = r40.iterator();	 Catch:{ Exception -> 0x0111 }
        L_0x072d:
            r40 = r19.hasNext();	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x074b;
        L_0x0733:
            r10 = r19.next();	 Catch:{ Exception -> 0x0111 }
            r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x0111 }
            r0 = r10.object;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 == 0) goto L_0x072d;
        L_0x073f:
            r0 = r10.name;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 == 0) goto L_0x072d;
        L_0x0745:
            r0 = r32;
            r0.add(r10);	 Catch:{ Exception -> 0x0111 }
            goto L_0x072d;
        L_0x074b:
            r40 = new org.telegram.ui.Adapters.DialogsSearchAdapter$5$1;	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r46;
            r0.<init>();	 Catch:{ Exception -> 0x0111 }
            r0 = r32;
            r1 = r40;
            java.util.Collections.sort(r0, r1);	 Catch:{ Exception -> 0x0111 }
            r26 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r26.<init>();	 Catch:{ Exception -> 0x0111 }
            r27 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0111 }
            r27.<init>();	 Catch:{ Exception -> 0x0111 }
            r4 = 0;
        L_0x0766:
            r40 = r32.size();	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            if (r4 >= r0) goto L_0x078f;
        L_0x076e:
            r0 = r32;
            r10 = r0.get(r4);	 Catch:{ Exception -> 0x0111 }
            r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r10;	 Catch:{ Exception -> 0x0111 }
            r0 = r10.object;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r26;
            r1 = r40;
            r0.add(r1);	 Catch:{ Exception -> 0x0111 }
            r0 = r10.name;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r27;
            r1 = r40;
            r0.add(r1);	 Catch:{ Exception -> 0x0111 }
            r4 = r4 + 1;
            goto L_0x0766;
        L_0x078f:
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = r40.dialogsType;	 Catch:{ Exception -> 0x0111 }
            r41 = 2;
            r0 = r40;
            r1 = r41;
            if (r0 == r1) goto L_0x093a;
        L_0x07a1:
            r40 = org.telegram.messenger.MessagesStorage.getInstance();	 Catch:{ Exception -> 0x0111 }
            r40 = r40.getDatabase();	 Catch:{ Exception -> 0x0111 }
            r41 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid";
            r42 = 0;
            r0 = r42;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0111 }
            r42 = r0;
            r8 = r40.queryFinalized(r41, r42);	 Catch:{ Exception -> 0x0111 }
        L_0x07b7:
            r40 = r8.next();	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x0937;
        L_0x07bd:
            r40 = 3;
            r0 = r40;
            r34 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r34;
            r0 = (long) r0;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r40 = java.lang.Long.valueOf(r40);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r40 = r11.containsKey(r0);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x07b7;
        L_0x07d6:
            r40 = 2;
            r0 = r40;
            r24 = r8.stringValue(r0);	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r24;
            r33 = r0.getTranslitString(r1);	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r33;
            r40 = r0.equals(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x07f6;
        L_0x07f4:
            r33 = 0;
        L_0x07f6:
            r37 = 0;
            r40 = ";;;";
            r0 = r24;
            r1 = r40;
            r38 = r0.lastIndexOf(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = -1;
            r0 = r38;
            r1 = r40;
            if (r0 == r1) goto L_0x0814;
        L_0x080a:
            r40 = r38 + 3;
            r0 = r24;
            r1 = r40;
            r37 = r0.substring(r1);	 Catch:{ Exception -> 0x0111 }
        L_0x0814:
            r17 = 0;
            r5 = r29;
            r0 = r5.length;	 Catch:{ Exception -> 0x0111 }
            r22 = r0;
            r19 = 0;
        L_0x081d:
            r0 = r19;
            r1 = r22;
            if (r0 >= r1) goto L_0x07b7;
        L_0x0823:
            r25 = r5[r19];	 Catch:{ Exception -> 0x0111 }
            r40 = r24.startsWith(r25);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0879;
        L_0x082b:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r24;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0879;
        L_0x084c:
            if (r33 == 0) goto L_0x08e6;
        L_0x084e:
            r0 = r33;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 != 0) goto L_0x0879;
        L_0x0858:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = " ";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r40;
            r1 = r25;
            r40 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r0 = r33;
            r1 = r40;
            r40 = r0.contains(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x08e6;
        L_0x0879:
            r17 = 1;
        L_0x087b:
            if (r17 == 0) goto L_0x0933;
        L_0x087d:
            r40 = 0;
            r0 = r40;
            r9 = r8.byteBufferValue(r0);	 Catch:{ Exception -> 0x0111 }
            if (r9 == 0) goto L_0x07b7;
        L_0x0887:
            r40 = 0;
            r0 = r40;
            r40 = r9.readInt32(r0);	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r0 = r40;
            r1 = r41;
            r36 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r0, r1);	 Catch:{ Exception -> 0x0111 }
            r9.reuse();	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.status;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            if (r40 == 0) goto L_0x08b8;
        L_0x08a4:
            r0 = r36;
            r0 = r0.status;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r41 = 1;
            r0 = r41;
            r41 = r8.intValue(r0);	 Catch:{ Exception -> 0x0111 }
            r0 = r41;
            r1 = r40;
            r1.expires = r0;	 Catch:{ Exception -> 0x0111 }
        L_0x08b8:
            r40 = 1;
            r0 = r17;
            r1 = r40;
            if (r0 != r1) goto L_0x08f5;
        L_0x08c0:
            r0 = r36;
            r0 = r0.first_name;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r36;
            r0 = r0.last_name;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r0 = r40;
            r1 = r41;
            r2 = r25;
            r40 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r2);	 Catch:{ Exception -> 0x0111 }
            r0 = r27;
            r1 = r40;
            r0.add(r1);	 Catch:{ Exception -> 0x0111 }
        L_0x08dd:
            r0 = r26;
            r1 = r36;
            r0.add(r1);	 Catch:{ Exception -> 0x0111 }
            goto L_0x07b7;
        L_0x08e6:
            if (r37 == 0) goto L_0x087b;
        L_0x08e8:
            r0 = r37;
            r1 = r25;
            r40 = r0.startsWith(r1);	 Catch:{ Exception -> 0x0111 }
            if (r40 == 0) goto L_0x087b;
        L_0x08f2:
            r17 = 2;
            goto L_0x087b;
        L_0x08f5:
            r40 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r40.<init>();	 Catch:{ Exception -> 0x0111 }
            r41 = "@";
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r0 = r36;
            r0 = r0.username;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r40 = r40.append(r41);	 Catch:{ Exception -> 0x0111 }
            r40 = r40.toString();	 Catch:{ Exception -> 0x0111 }
            r41 = 0;
            r42 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0111 }
            r42.<init>();	 Catch:{ Exception -> 0x0111 }
            r43 = "@";
            r42 = r42.append(r43);	 Catch:{ Exception -> 0x0111 }
            r0 = r42;
            r1 = r25;
            r42 = r0.append(r1);	 Catch:{ Exception -> 0x0111 }
            r42 = r42.toString();	 Catch:{ Exception -> 0x0111 }
            r40 = org.telegram.messenger.AndroidUtilities.generateSearchName(r40, r41, r42);	 Catch:{ Exception -> 0x0111 }
            r0 = r27;
            r1 = r40;
            r0.add(r1);	 Catch:{ Exception -> 0x0111 }
            goto L_0x08dd;
        L_0x0933:
            r19 = r19 + 1;
            goto L_0x081d;
        L_0x0937:
            r8.dispose();	 Catch:{ Exception -> 0x0111 }
        L_0x093a:
            r0 = r46;
            r0 = org.telegram.ui.Adapters.DialogsSearchAdapter.this;	 Catch:{ Exception -> 0x0111 }
            r40 = r0;
            r0 = r46;
            r0 = r0.val$searchId;	 Catch:{ Exception -> 0x0111 }
            r41 = r0;
            r0 = r40;
            r1 = r26;
            r2 = r27;
            r3 = r41;
            r0.updateSearchResults(r1, r2, r15, r3);	 Catch:{ Exception -> 0x0111 }
            goto L_0x0041;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.5.run():void");
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.6 */
    class C09436 implements Runnable {
        final /* synthetic */ ArrayList val$encUsers;
        final /* synthetic */ ArrayList val$names;
        final /* synthetic */ ArrayList val$result;
        final /* synthetic */ int val$searchId;

        C09436(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            this.val$searchId = i;
            this.val$result = arrayList;
            this.val$encUsers = arrayList2;
            this.val$names = arrayList3;
        }

        public void run() {
            if (this.val$searchId == DialogsSearchAdapter.this.lastSearchId) {
                for (int a = 0; a < this.val$result.size(); a++) {
                    TLObject obj = (TLObject) this.val$result.get(a);
                    if (obj instanceof User) {
                        MessagesController.getInstance().putUser((User) obj, true);
                    } else if (obj instanceof Chat) {
                        MessagesController.getInstance().putChat((Chat) obj, true);
                    } else if (obj instanceof EncryptedChat) {
                        MessagesController.getInstance().putEncryptedChat((EncryptedChat) obj, true);
                    }
                }
                MessagesController.getInstance().putUsers(this.val$encUsers, true);
                DialogsSearchAdapter.this.searchResult = this.val$result;
                DialogsSearchAdapter.this.searchResultNames = this.val$names;
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.7 */
    class C09457 extends TimerTask {
        final /* synthetic */ String val$query;
        final /* synthetic */ int val$searchId;

        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.7.1 */
        class C09441 implements Runnable {
            C09441() {
            }

            public void run() {
                if (DialogsSearchAdapter.this.needMessagesSearch != 2) {
                    DialogsSearchAdapter.this.queryServerSearch(C09457.this.val$query, true);
                }
                DialogsSearchAdapter.this.searchMessagesInternal(C09457.this.val$query);
            }
        }

        C09457(String str, int i) {
            this.val$query = str;
            this.val$searchId = i;
        }

        public void run() {
            try {
                cancel();
                DialogsSearchAdapter.this.searchTimer.cancel();
                DialogsSearchAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            DialogsSearchAdapter.this.searchDialogsInternal(this.val$query, this.val$searchId);
            AndroidUtilities.runOnUIThread(new C09441());
        }
    }

    private class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        private DialogSearchResult() {
        }
    }

    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(int i);

        void needRemoveHint(int i);

        void searchStateChanged(boolean z);
    }

    protected static class RecentSearchObject {
        int date;
        long did;
        TLObject object;

        protected RecentSearchObject() {
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.1 */
    class C17531 implements RequestDelegate {
        final /* synthetic */ int val$currentReqId;
        final /* synthetic */ TL_messages_searchGlobal val$req;

        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.1.1 */
        class C09351 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C09351(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = true;
                if (C17531.this.val$currentReqId == DialogsSearchAdapter.this.lastReqId && this.val$error == null) {
                    messages_Messages res = this.val$response;
                    MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
                    MessagesController.getInstance().putUsers(res.users, false);
                    MessagesController.getInstance().putChats(res.chats, false);
                    if (C17531.this.val$req.offset_id == 0) {
                        DialogsSearchAdapter.this.searchResultMessages.clear();
                    }
                    for (int a = 0; a < res.messages.size(); a++) {
                        boolean z2;
                        Message message = (Message) res.messages.get(a);
                        DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(message, null, false));
                        long dialog_id = MessageObject.getDialogId(message);
                        ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.getInstance().dialogs_read_outbox_max : MessagesController.getInstance().dialogs_read_inbox_max;
                        Integer value = (Integer) read_max.get(Long.valueOf(dialog_id));
                        if (value == null) {
                            value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, dialog_id));
                            read_max.put(Long.valueOf(dialog_id), value);
                        }
                        if (value.intValue() < message.id) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        message.unread = z2;
                    }
                    DialogsSearchAdapter dialogsSearchAdapter = DialogsSearchAdapter.this;
                    if (res.messages.size() == 20) {
                        z = false;
                    }
                    dialogsSearchAdapter.messagesSearchEndReached = z;
                    DialogsSearchAdapter.this.notifyDataSetChanged();
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.reqId = 0;
            }
        }

        C17531(int i, TL_messages_searchGlobal tL_messages_searchGlobal) {
            this.val$currentReqId = i;
            this.val$req = tL_messages_searchGlobal;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C09351(error, response));
        }
    }

    private class CategoryAdapterRecycler extends Adapter {
        private CategoryAdapterRecycler() {
        }

        public void setIndex(int value) {
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            view.setLayoutParams(new LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(100.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            HintDialogCell cell = holder.itemView;
            TL_topPeer peer = (TL_topPeer) SearchQuery.hints.get(position);
            TL_dialog dialog = new TL_dialog();
            Chat chat = null;
            User user = null;
            int did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
                user = MessagesController.getInstance().getUser(Integer.valueOf(peer.peer.user_id));
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
                chat = MessagesController.getInstance().getChat(Integer.valueOf(peer.peer.channel_id));
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
                chat = MessagesController.getInstance().getChat(Integer.valueOf(peer.peer.chat_id));
            }
            cell.setTag(Integer.valueOf(did));
            String name = TtmlNode.ANONYMOUS_REGION_ID;
            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else if (chat != null) {
                name = chat.title;
            }
            cell.setDialog(did, false, name);
        }

        public int getItemCount() {
            return SearchQuery.hints.size();
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.9 */
    class C20129 extends LinearLayoutManager {
        C20129(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter.8 */
    class C20268 extends RecyclerListView {
        C20268(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent e) {
            if (!(getParent() == null || getParent().getParent() == null)) {
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(e);
        }
    }

    public DialogsSearchAdapter(Context context, int messagesSearch, int type) {
        this.searchResult = new ArrayList();
        this.searchResultNames = new ArrayList();
        this.searchResultMessages = new ArrayList();
        this.searchResultHashtags = new ArrayList();
        this.reqId = 0;
        this.lastSearchId = 0;
        this.recentSearchObjects = new ArrayList();
        this.recentSearchObjectsById = new HashMap();
        this.mContext = context;
        this.needMessagesSearch = messagesSearch;
        this.dialogsType = type;
        loadRecentSearch();
        SearchQuery.loadHints(true);
    }

    public void setDelegate(DialogsSearchAdapterDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean isMessagesSearchEndReached() {
        return this.messagesSearchEndReached;
    }

    public void loadMoreSearchMessages() {
        searchMessagesInternal(this.lastMessagesSearchString);
    }

    public String getLastSearchString() {
        return this.lastMessagesSearchString;
    }

    private void searchMessagesInternal(String query) {
        if (this.needMessagesSearch == 0) {
            return;
        }
        if ((this.lastMessagesSearchString != null && this.lastMessagesSearchString.length() != 0) || (query != null && query.length() != 0)) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (query == null || query.length() == 0) {
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                notifyDataSetChanged();
                if (this.delegate != null) {
                    this.delegate.searchStateChanged(false);
                    return;
                }
                return;
            }
            TL_messages_searchGlobal req = new TL_messages_searchGlobal();
            req.limit = 20;
            req.f39q = query;
            if (this.lastMessagesSearchString == null || !query.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                req.offset_date = 0;
                req.offset_id = 0;
                req.offset_peer = new TL_inputPeerEmpty();
            } else {
                int id;
                MessageObject lastMessage = (MessageObject) this.searchResultMessages.get(this.searchResultMessages.size() - 1);
                req.offset_id = lastMessage.getId();
                req.offset_date = lastMessage.messageOwner.date;
                if (lastMessage.messageOwner.to_id.channel_id != 0) {
                    id = -lastMessage.messageOwner.to_id.channel_id;
                } else if (lastMessage.messageOwner.to_id.chat_id != 0) {
                    id = -lastMessage.messageOwner.to_id.chat_id;
                } else {
                    id = lastMessage.messageOwner.to_id.user_id;
                }
                req.offset_peer = MessagesController.getInputPeer(id);
            }
            this.lastMessagesSearchString = query;
            int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            if (this.delegate != null) {
                this.delegate.searchStateChanged(true);
            }
            this.reqId = ConnectionsManager.getInstance().sendRequest(req, new C17531(currentReqId, req), 2);
        }
    }

    public boolean hasRecentRearch() {
        return (this.recentSearchObjects.isEmpty() && SearchQuery.hints.isEmpty()) ? false : true;
    }

    public boolean isRecentSearchDisplayed() {
        return this.needMessagesSearch != 2 && ((this.lastSearchText == null || this.lastSearchText.length() == 0) && !(this.recentSearchObjects.isEmpty() && SearchQuery.hints.isEmpty()));
    }

    public void loadRecentSearch() {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09382());
    }

    public void putRecentSearch(long did, TLObject object) {
        RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjectsById.get(Long.valueOf(did));
        if (recentSearchObject == null) {
            recentSearchObject = new RecentSearchObject();
            this.recentSearchObjectsById.put(Long.valueOf(did), recentSearchObject);
        } else {
            this.recentSearchObjects.remove(recentSearchObject);
        }
        this.recentSearchObjects.add(0, recentSearchObject);
        recentSearchObject.did = did;
        recentSearchObject.object = object;
        recentSearchObject.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09393(did));
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new HashMap();
        this.recentSearchObjects = new ArrayList();
        notifyDataSetChanged();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09404());
    }

    private void setRecentSearch(ArrayList<RecentSearchObject> arrayList, HashMap<Long, RecentSearchObject> hashMap) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = hashMap;
        for (int a = 0; a < this.recentSearchObjects.size(); a++) {
            RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjects.get(a);
            if (recentSearchObject.object instanceof User) {
                MessagesController.getInstance().putUser((User) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof Chat) {
                MessagesController.getInstance().putChat((Chat) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof EncryptedChat) {
                MessagesController.getInstance().putEncryptedChat((EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(String query, int searchId) {
        if (this.needMessagesSearch != 2) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09425(query, searchId));
        }
    }

    private void updateSearchResults(ArrayList<TLObject> result, ArrayList<CharSequence> names, ArrayList<User> encUsers, int searchId) {
        AndroidUtilities.runOnUIThread(new C09436(searchId, result, encUsers, names));
    }

    public boolean isGlobalSearch(int i) {
        return i > this.searchResult.size() && i <= this.globalSearch.size() + this.searchResult.size();
    }

    public void clearRecentHashtags() {
        super.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    protected void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        super.setHashtags(arrayList, hashMap);
        Iterator i$ = arrayList.iterator();
        while (i$.hasNext()) {
            this.searchResultHashtags.add(((HashtagObject) i$.next()).hashtag);
        }
        if (this.delegate != null) {
            this.delegate.searchStateChanged(false);
        }
        notifyDataSetChanged();
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
                this.hashtagsLoadedFromDb = false;
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                if (this.needMessagesSearch != 2) {
                    queryServerSearch(null, true);
                }
                searchMessagesInternal(null);
                notifyDataSetChanged();
            } else if (this.needMessagesSearch != 2 && query.startsWith("#") && query.length() == 1) {
                this.messagesSearchEndReached = true;
                if (this.hashtagsLoadedFromDb) {
                    this.searchResultMessages.clear();
                    this.searchResultHashtags.clear();
                    Iterator i$ = this.hashtags.iterator();
                    while (i$.hasNext()) {
                        this.searchResultHashtags.add(((HashtagObject) i$.next()).hashtag);
                    }
                    if (this.delegate != null) {
                        this.delegate.searchStateChanged(false);
                    }
                    notifyDataSetChanged();
                    return;
                }
                loadRecentHashtags();
                if (this.delegate != null) {
                    this.delegate.searchStateChanged(true);
                }
                notifyDataSetChanged();
            } else {
                this.searchResultHashtags.clear();
                int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                this.searchTimer = new Timer();
                this.searchTimer.schedule(new C09457(query, searchId), 200, 300);
            }
        }
    }

    public int getItemCount() {
        int i = 0;
        int i2;
        if (isRecentSearchDisplayed()) {
            if (this.recentSearchObjects.isEmpty()) {
                i2 = 0;
            } else {
                i2 = this.recentSearchObjects.size() + 1;
            }
            if (!SearchQuery.hints.isEmpty()) {
                i = 2;
            }
            return i2 + i;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1;
        } else {
            int count = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            int messagesCount = this.searchResultMessages.size();
            if (globalCount != 0) {
                count += globalCount + 1;
            }
            if (messagesCount == 0) {
                return count;
            }
            i2 = messagesCount + 1;
            if (!this.messagesSearchEndReached) {
                i = 1;
            }
            return count + (i2 + i);
        }
    }

    public Object getItem(int i) {
        if (isRecentSearchDisplayed()) {
            int offset = !SearchQuery.hints.isEmpty() ? 2 : 0;
            if (i <= offset || (i - 1) - offset >= this.recentSearchObjects.size()) {
                return null;
            }
            TLObject object = ((RecentSearchObject) this.recentSearchObjects.get((i - 1) - offset)).object;
            if (object instanceof User) {
                TLObject user = MessagesController.getInstance().getUser(Integer.valueOf(((User) object).id));
                if (user != null) {
                    return user;
                }
                return object;
            } else if (!(object instanceof Chat)) {
                return object;
            } else {
                TLObject chat = MessagesController.getInstance().getChat(Integer.valueOf(((Chat) object).id));
                if (chat != null) {
                    return chat;
                }
                return object;
            }
        } else if (this.searchResultHashtags.isEmpty()) {
            int localCount = this.searchResult.size();
            int globalCount = this.globalSearch.isEmpty() ? 0 : this.globalSearch.size() + 1;
            int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if (i >= 0 && i < localCount) {
                return this.searchResult.get(i);
            }
            if (i > localCount && i < globalCount + localCount) {
                return this.globalSearch.get((i - localCount) - 1);
            }
            if (i <= globalCount + localCount || i >= (globalCount + localCount) + messagesCount) {
                return null;
            }
            return this.searchResultMessages.get(((i - localCount) - globalCount) - 1);
        } else if (i > 0) {
            return this.searchResultHashtags.get(i - 1);
        } else {
            return null;
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                view = new ProfileSearchCell(this.mContext);
                view.setBackgroundResource(C0691R.drawable.list_selector);
                break;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                view = new GreySectionCell(this.mContext);
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                view = new DialogCell(this.mContext);
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                view = new LoadingCell(this.mContext);
                break;
            case VideoPlayer.STATE_READY /*4*/:
                view = new HashtagSearchCell(this.mContext);
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                View horizontalListView = new C20268(this.mContext);
                horizontalListView.setTag(Integer.valueOf(9));
                horizontalListView.setItemAnimator(null);
                horizontalListView.setLayoutAnimation(null);
                LinearLayoutManager layoutManager = new C20129(this.mContext);
                layoutManager.setOrientation(0);
                horizontalListView.setLayoutManager(layoutManager);
                horizontalListView.setAdapter(new CategoryAdapterRecycler());
                horizontalListView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        if (DialogsSearchAdapter.this.delegate != null) {
                            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog(((Integer) view.getTag()).intValue());
                        }
                    }
                });
                horizontalListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int position) {
                        if (DialogsSearchAdapter.this.delegate != null) {
                            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer) view.getTag()).intValue());
                        }
                        return true;
                    }
                });
                view = horizontalListView;
                break;
        }
        if (viewType == 5) {
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
        } else {
            view.setLayoutParams(new LayoutParams(-1, -2));
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                TLObject tLObject;
                ProfileSearchCell cell = holder.itemView;
                TLObject user = null;
                TLObject chat = null;
                EncryptedChat encryptedChat = null;
                CharSequence username = null;
                CharSequence name = null;
                boolean isRecent = false;
                String un = null;
                TLObject obj = getItem(position);
                if (obj instanceof User) {
                    user = (User) obj;
                    un = user.username;
                } else if (obj instanceof Chat) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(((Chat) obj).id));
                    if (chat == null) {
                        chat = (Chat) obj;
                    }
                    un = chat.username;
                } else if (obj instanceof EncryptedChat) {
                    encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(((EncryptedChat) obj).id));
                    user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
                }
                boolean z;
                if (isRecentSearchDisplayed()) {
                    isRecent = true;
                    if (position != getItemCount() - 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    cell.useSeparator = z;
                } else {
                    int localCount = this.searchResult.size();
                    z = (position == getItemCount() + -1 || position == localCount - 1 || position == (localCount + (this.globalSearch.isEmpty() ? 0 : this.globalSearch.size() + 1)) - 1) ? false : true;
                    cell.useSeparator = z;
                    if (position < this.searchResult.size()) {
                        name = (CharSequence) this.searchResultNames.get(position);
                        if (!(name == null || user == null || user.username == null || user.username.length() <= 0)) {
                            if (name.toString().startsWith("@" + user.username)) {
                                username = name;
                                name = null;
                            }
                        }
                    } else if (position > this.searchResult.size() && un != null) {
                        String foundUserName = this.lastFoundUsername;
                        if (foundUserName.startsWith("@")) {
                            foundUserName = foundUserName.substring(1);
                        }
                        try {
                            r21 = new Object[2];
                            r21[0] = un.substring(0, foundUserName.length());
                            r21[1] = un.substring(foundUserName.length());
                            username = AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", r21));
                        } catch (Throwable e) {
                            Object username2 = un;
                            FileLog.m13e("tmessages", e);
                        }
                    }
                }
                if (user != null) {
                    tLObject = user;
                } else {
                    tLObject = chat;
                }
                cell.setData(tLObject, encryptedChat, name, username, isRecent);
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                GreySectionCell cell2 = holder.itemView;
                if (isRecentSearchDisplayed()) {
                    if (position < (!SearchQuery.hints.isEmpty() ? 2 : 0)) {
                        cell2.setText(LocaleController.getString("ChatHints", C0691R.string.ChatHints).toUpperCase());
                    } else {
                        cell2.setText(LocaleController.getString("Recent", C0691R.string.Recent).toUpperCase());
                    }
                } else if (!this.searchResultHashtags.isEmpty()) {
                    cell2.setText(LocaleController.getString("Hashtags", C0691R.string.Hashtags).toUpperCase());
                } else if (this.globalSearch.isEmpty() || position != this.searchResult.size()) {
                    cell2.setText(LocaleController.getString("SearchMessages", C0691R.string.SearchMessages));
                } else {
                    cell2.setText(LocaleController.getString("GlobalSearch", C0691R.string.GlobalSearch));
                }
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                DialogCell cell3 = holder.itemView;
                cell3.useSeparator = position != getItemCount() + -1;
                MessageObject messageObject = (MessageObject) getItem(position);
                cell3.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date);
            case VideoPlayer.STATE_READY /*4*/:
                HashtagSearchCell cell4 = holder.itemView;
                cell4.setText((CharSequence) this.searchResultHashtags.get(position - 1));
                cell4.setNeedDivider(position != this.searchResultHashtags.size());
            case VideoPlayer.STATE_ENDED /*5*/:
                ((CategoryAdapterRecycler) ((RecyclerListView) holder.itemView).getAdapter()).setIndex(position / 2);
            default:
        }
    }

    public int getItemViewType(int i) {
        if (isRecentSearchDisplayed()) {
            int offset = !SearchQuery.hints.isEmpty() ? 2 : 0;
            if (i > offset) {
                return 0;
            }
            if (i == offset || i % 2 == 0) {
                return 1;
            }
            return 5;
        } else if (this.searchResultHashtags.isEmpty()) {
            int localCount = this.searchResult.size();
            int globalCount = this.globalSearch.isEmpty() ? 0 : this.globalSearch.size() + 1;
            int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if ((i >= 0 && i < localCount) || (i > localCount && i < globalCount + localCount)) {
                return 0;
            }
            if (i > globalCount + localCount && i < (globalCount + localCount) + messagesCount) {
                return 2;
            }
            if (messagesCount == 0 || i != (globalCount + localCount) + messagesCount) {
                return 1;
            }
            return 3;
        } else if (i != 0) {
            return 4;
        } else {
            return 1;
        }
    }
}
