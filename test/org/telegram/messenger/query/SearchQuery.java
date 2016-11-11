package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.User;

public class SearchQuery {
    public static ArrayList<TL_topPeer> hints;
    public static ArrayList<TL_topPeer> inlineBots;
    private static HashMap<Integer, Integer> inlineDates;
    private static boolean loaded;
    private static boolean loading;

    /* renamed from: org.telegram.messenger.query.SearchQuery.1 */
    static class C08171 implements Runnable {

        /* renamed from: org.telegram.messenger.query.SearchQuery.1.1 */
        class C08161 implements Runnable {
            final /* synthetic */ ArrayList val$chats;
            final /* synthetic */ ArrayList val$hintsNew;
            final /* synthetic */ ArrayList val$inlineBotsNew;
            final /* synthetic */ HashMap val$inlineDatesNew;
            final /* synthetic */ ArrayList val$users;

            C08161(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, HashMap hashMap) {
                this.val$users = arrayList;
                this.val$chats = arrayList2;
                this.val$hintsNew = arrayList3;
                this.val$inlineBotsNew = arrayList4;
                this.val$inlineDatesNew = hashMap;
            }

            public void run() {
                MessagesController.getInstance().putUsers(this.val$users, true);
                MessagesController.getInstance().putChats(this.val$chats, true);
                SearchQuery.loading = false;
                SearchQuery.loaded = true;
                SearchQuery.hints = this.val$hintsNew;
                SearchQuery.inlineBots = this.val$inlineBotsNew;
                SearchQuery.inlineDates = this.val$inlineDatesNew;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                if (Math.abs(UserConfig.lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
                    SearchQuery.loadHints(false);
                }
            }
        }

        C08171() {
        }

        public void run() {
            ArrayList<TL_topPeer> hintsNew = new ArrayList();
            ArrayList<TL_topPeer> inlineBotsNew = new ArrayList();
            HashMap<Integer, Integer> inlineDatesNew = new HashMap();
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            try {
                ArrayList<Integer> usersToLoad = new ArrayList();
                ArrayList<Integer> chatsToLoad = new ArrayList();
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, type, rating, date FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
                while (cursor.next()) {
                    int did = cursor.intValue(0);
                    int type = cursor.intValue(1);
                    TL_topPeer peer = new TL_topPeer();
                    peer.rating = cursor.doubleValue(2);
                    if (did > 0) {
                        peer.peer = new TL_peerUser();
                        peer.peer.user_id = did;
                        usersToLoad.add(Integer.valueOf(did));
                    } else {
                        peer.peer = new TL_peerChat();
                        peer.peer.chat_id = -did;
                        chatsToLoad.add(Integer.valueOf(-did));
                    }
                    if (type == 0) {
                        hintsNew.add(peer);
                    } else if (type == 1) {
                        inlineBotsNew.add(peer);
                        inlineDatesNew.put(Integer.valueOf(did), Integer.valueOf(cursor.intValue(3)));
                    }
                }
                cursor.dispose();
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
                AndroidUtilities.runOnUIThread(new C08161(users, chats, hintsNew, inlineBotsNew, inlineDatesNew));
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.3 */
    static class C08213 implements Comparator<TL_topPeer> {
        C08213() {
        }

        public int compare(TL_topPeer lhs, TL_topPeer rhs) {
            if (lhs.rating > rhs.rating) {
                return -1;
            }
            if (lhs.rating < rhs.rating) {
                return 1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.6 */
    static class C08246 implements Runnable {
        final /* synthetic */ long val$did;
        final /* synthetic */ int val$lower_id;

        /* renamed from: org.telegram.messenger.query.SearchQuery.6.1 */
        class C08231 implements Runnable {
            final /* synthetic */ double val$dtFinal;

            /* renamed from: org.telegram.messenger.query.SearchQuery.6.1.1 */
            class C08221 implements Comparator<TL_topPeer> {
                C08221() {
                }

                public int compare(TL_topPeer lhs, TL_topPeer rhs) {
                    if (lhs.rating > rhs.rating) {
                        return -1;
                    }
                    if (lhs.rating < rhs.rating) {
                        return 1;
                    }
                    return 0;
                }
            }

            C08231(double d) {
                this.val$dtFinal = d;
            }

            public void run() {
                TL_topPeer peer = null;
                for (int a = 0; a < SearchQuery.hints.size(); a++) {
                    TL_topPeer p = (TL_topPeer) SearchQuery.hints.get(a);
                    if ((C08246.this.val$lower_id < 0 && (p.peer.chat_id == (-C08246.this.val$lower_id) || p.peer.channel_id == (-C08246.this.val$lower_id))) || (C08246.this.val$lower_id > 0 && p.peer.user_id == C08246.this.val$lower_id)) {
                        peer = p;
                        break;
                    }
                }
                if (peer == null) {
                    peer = new TL_topPeer();
                    if (C08246.this.val$lower_id > 0) {
                        peer.peer = new TL_peerUser();
                        peer.peer.user_id = C08246.this.val$lower_id;
                    } else {
                        peer.peer = new TL_peerChat();
                        peer.peer.chat_id = -C08246.this.val$lower_id;
                    }
                    SearchQuery.hints.add(peer);
                }
                peer.rating += Math.exp(this.val$dtFinal / ((double) MessagesController.getInstance().ratingDecay));
                Collections.sort(SearchQuery.hints, new C08221());
                SearchQuery.savePeer((int) C08246.this.val$did, 0, peer.rating);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
            }
        }

        C08246(long j, int i) {
            this.val$did = j;
            this.val$lower_id = i;
        }

        public void run() {
            double dt = 0.0d;
            int lastTime = 0;
            int lastMid = 0;
            try {
                SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                Object[] objArr = new Object[1];
                objArr[0] = Long.valueOf(this.val$did);
                SQLiteCursor cursor = database.queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", objArr), new Object[0]);
                if (cursor.next()) {
                    lastMid = cursor.intValue(0);
                    lastTime = cursor.intValue(1);
                }
                cursor.dispose();
                if (lastMid > 0) {
                    database = MessagesStorage.getInstance().getDatabase();
                    objArr = new Object[2];
                    objArr[0] = Long.valueOf(this.val$did);
                    objArr[1] = Integer.valueOf(lastMid);
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT date FROM messages WHERE uid = %d AND mid < %d AND out = 1 ORDER BY date DESC", objArr), new Object[0]);
                    if (cursor.next()) {
                        dt = (double) (lastTime - cursor.intValue(0));
                    }
                    cursor.dispose();
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C08231(dt));
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.7 */
    static class C08257 implements Runnable {
        final /* synthetic */ int val$did;
        final /* synthetic */ double val$rating;
        final /* synthetic */ int val$type;

        C08257(int i, int i2, double d) {
            this.val$did = i;
            this.val$type = i2;
            this.val$rating = d;
        }

        public void run() {
            try {
                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                state.requery();
                state.bindInteger(1, this.val$did);
                state.bindInteger(2, this.val$type);
                state.bindDouble(3, this.val$rating);
                state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
                state.step();
                state.dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.8 */
    static class C08268 implements Runnable {
        final /* synthetic */ int val$did;
        final /* synthetic */ int val$type;

        C08268(int i, int i2) {
            this.val$did = i;
            this.val$type = i2;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(this.val$did), Integer.valueOf(this.val$type)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.2 */
    static class C17112 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.query.SearchQuery.2.1 */
        class C08201 implements Runnable {
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.messenger.query.SearchQuery.2.1.1 */
            class C08191 implements Runnable {
                final /* synthetic */ HashMap val$inlineDatesCopy;
                final /* synthetic */ TL_contacts_topPeers val$topPeers;

                /* renamed from: org.telegram.messenger.query.SearchQuery.2.1.1.1 */
                class C08181 implements Runnable {
                    C08181() {
                    }

                    public void run() {
                        UserConfig.lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
                        UserConfig.saveConfig(false);
                    }
                }

                C08191(TL_contacts_topPeers tL_contacts_topPeers, HashMap hashMap) {
                    this.val$topPeers = tL_contacts_topPeers;
                    this.val$inlineDatesCopy = hashMap;
                }

                public void run() {
                    try {
                        MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                        MessagesStorage.getInstance().getDatabase().beginTransaction();
                        MessagesStorage.getInstance().putUsersAndChats(this.val$topPeers.users, this.val$topPeers.chats, false, false);
                        SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                        for (int a = 0; a < this.val$topPeers.categories.size(); a++) {
                            int type;
                            TL_topPeerCategoryPeers category = (TL_topPeerCategoryPeers) this.val$topPeers.categories.get(a);
                            if (category.category instanceof TL_topPeerCategoryBotsInline) {
                                type = 1;
                            } else {
                                type = 0;
                            }
                            for (int b = 0; b < category.peers.size(); b++) {
                                int did;
                                int intValue;
                                TL_topPeer peer = (TL_topPeer) category.peers.get(b);
                                if (peer.peer instanceof TL_peerUser) {
                                    did = peer.peer.user_id;
                                } else if (peer.peer instanceof TL_peerChat) {
                                    did = -peer.peer.chat_id;
                                } else {
                                    did = -peer.peer.channel_id;
                                }
                                Integer date = (Integer) this.val$inlineDatesCopy.get(Integer.valueOf(did));
                                state.requery();
                                state.bindInteger(1, did);
                                state.bindInteger(2, type);
                                state.bindDouble(3, peer.rating);
                                if (date != null) {
                                    intValue = date.intValue();
                                } else {
                                    intValue = 0;
                                }
                                state.bindInteger(4, intValue);
                                state.step();
                            }
                        }
                        state.dispose();
                        MessagesStorage.getInstance().getDatabase().commitTransaction();
                        AndroidUtilities.runOnUIThread(new C08181());
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }

            C08201(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                TL_contacts_topPeers topPeers = this.val$response;
                MessagesController.getInstance().putUsers(topPeers.users, false);
                MessagesController.getInstance().putChats(topPeers.chats, false);
                for (int a = 0; a < topPeers.categories.size(); a++) {
                    TL_topPeerCategoryPeers category = (TL_topPeerCategoryPeers) topPeers.categories.get(a);
                    if (category.category instanceof TL_topPeerCategoryBotsInline) {
                        SearchQuery.inlineBots = category.peers;
                    } else {
                        SearchQuery.hints = category.peers;
                    }
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08191(topPeers, new HashMap(SearchQuery.inlineDates)));
            }
        }

        C17112() {
        }

        public void run(TLObject response, TL_error error) {
            if (response instanceof TL_contacts_topPeers) {
                AndroidUtilities.runOnUIThread(new C08201(response));
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.4 */
    static class C17124 implements RequestDelegate {
        C17124() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    /* renamed from: org.telegram.messenger.query.SearchQuery.5 */
    static class C17135 implements RequestDelegate {
        C17135() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    static {
        hints = new ArrayList();
        inlineBots = new ArrayList();
        inlineDates = new HashMap();
    }

    public static void cleanup() {
        loading = false;
        loaded = false;
        hints.clear();
        inlineBots.clear();
        inlineDates.clear();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static void loadHints(boolean cache) {
        if (!loading) {
            if (!cache) {
                loading = true;
                TL_contacts_getTopPeers req = new TL_contacts_getTopPeers();
                req.hash = 0;
                req.bots_pm = false;
                req.correspondents = true;
                req.groups = false;
                req.channels = false;
                req.bots_inline = true;
                req.offset = 0;
                req.limit = 20;
                ConnectionsManager.getInstance().sendRequest(req, new C17112());
            } else if (!loaded) {
                loading = true;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08171());
                loaded = true;
            }
        }
    }

    public static void increaseInlineRaiting(int uid) {
        int dt;
        Integer time = (Integer) inlineDates.get(Integer.valueOf(uid));
        if (time != null) {
            dt = Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - time.intValue());
        } else {
            dt = 60;
        }
        TL_topPeer peer = null;
        for (int a = 0; a < inlineBots.size(); a++) {
            TL_topPeer p = (TL_topPeer) inlineBots.get(a);
            if (p.peer.user_id == uid) {
                peer = p;
                break;
            }
        }
        if (peer == null) {
            peer = new TL_topPeer();
            peer.peer = new TL_peerUser();
            peer.peer.user_id = uid;
            inlineBots.add(peer);
        }
        peer.rating += Math.exp((double) (dt / MessagesController.getInstance().ratingDecay));
        Collections.sort(inlineBots, new C08213());
        if (inlineBots.size() > 20) {
            inlineBots.remove(inlineBots.size() - 1);
        }
        savePeer(uid, 1, peer.rating);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static void removeInline(int uid) {
        for (int a = 0; a < inlineBots.size(); a++) {
            if (((TL_topPeer) inlineBots.get(a)).peer.user_id == uid) {
                inlineBots.remove(a);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryBotsInline();
                req.peer = MessagesController.getInputPeer(uid);
                ConnectionsManager.getInstance().sendRequest(req, new C17124());
                deletePeer(uid, 1);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public static void removePeer(int uid) {
        for (int a = 0; a < hints.size(); a++) {
            if (((TL_topPeer) hints.get(a)).peer.user_id == uid) {
                hints.remove(a);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryCorrespondents();
                req.peer = MessagesController.getInputPeer(uid);
                deletePeer(uid, 0);
                ConnectionsManager.getInstance().sendRequest(req, new C17135());
                return;
            }
        }
    }

    public static void increasePeerRaiting(long did) {
        int lower_id = (int) did;
        if (lower_id > 0) {
            User user = lower_id > 0 ? MessagesController.getInstance().getUser(Integer.valueOf(lower_id)) : null;
            if (user != null && !user.bot) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08246(did, lower_id));
            }
        }
    }

    private static void savePeer(int did, int type, double rating) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08257(did, type, rating));
    }

    private static void deletePeer(int did, int type) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08268(did, type));
    }
}
