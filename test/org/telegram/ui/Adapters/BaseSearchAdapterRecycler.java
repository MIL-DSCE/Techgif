package org.telegram.ui.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_found;
import org.telegram.tgnet.TLRPC.TL_contacts_search;
import org.telegram.tgnet.TLRPC.TL_error;

public abstract class BaseSearchAdapterRecycler extends Adapter {
    protected ArrayList<TLObject> globalSearch;
    protected ArrayList<HashtagObject> hashtags;
    protected HashMap<String, HashtagObject> hashtagsByText;
    protected boolean hashtagsLoadedFromDb;
    protected String lastFoundUsername;
    private int lastReqId;
    private int reqId;

    /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.2 */
    class C09272 implements Runnable {

        /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.2.1 */
        class C09251 implements Comparator<HashtagObject> {
            C09251() {
            }

            public int compare(HashtagObject lhs, HashtagObject rhs) {
                if (lhs.date < rhs.date) {
                    return 1;
                }
                if (lhs.date > rhs.date) {
                    return -1;
                }
                return 0;
            }
        }

        /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.2.2 */
        class C09262 implements Runnable {
            final /* synthetic */ ArrayList val$arrayList;
            final /* synthetic */ HashMap val$hashMap;

            C09262(ArrayList arrayList, HashMap hashMap) {
                this.val$arrayList = arrayList;
                this.val$hashMap = hashMap;
            }

            public void run() {
                BaseSearchAdapterRecycler.this.setHashtags(this.val$arrayList, this.val$hashMap);
            }
        }

        C09272() {
        }

        public void run() {
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
                ArrayList<HashtagObject> arrayList = new ArrayList();
                HashMap<String, HashtagObject> hashMap = new HashMap();
                while (cursor.next()) {
                    HashtagObject hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = cursor.stringValue(0);
                    hashtagObject.date = cursor.intValue(1);
                    arrayList.add(hashtagObject);
                    hashMap.put(hashtagObject.hashtag, hashtagObject);
                }
                cursor.dispose();
                Collections.sort(arrayList, new C09251());
                AndroidUtilities.runOnUIThread(new C09262(arrayList, hashMap));
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.3 */
    class C09283 implements Runnable {
        final /* synthetic */ ArrayList val$arrayList;

        C09283(ArrayList arrayList) {
            this.val$arrayList = arrayList;
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().beginTransaction();
                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
                int a = 0;
                while (a < this.val$arrayList.size() && a != 100) {
                    HashtagObject hashtagObject = (HashtagObject) this.val$arrayList.get(a);
                    state.requery();
                    state.bindString(1, hashtagObject.hashtag);
                    state.bindInteger(2, hashtagObject.date);
                    state.step();
                    a++;
                }
                state.dispose();
                MessagesStorage.getInstance().getDatabase().commitTransaction();
                if (this.val$arrayList.size() >= 100) {
                    MessagesStorage.getInstance().getDatabase().beginTransaction();
                    for (a = 100; a < this.val$arrayList.size(); a++) {
                        MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) this.val$arrayList.get(a)).hashtag + "'").stepThis().dispose();
                    }
                    MessagesStorage.getInstance().getDatabase().commitTransaction();
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.4 */
    class C09294 implements Runnable {
        C09294() {
        }

        public void run() {
            try {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    protected static class HashtagObject {
        int date;
        String hashtag;

        protected HashtagObject() {
        }
    }

    /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.1 */
    class C17521 implements RequestDelegate {
        final /* synthetic */ boolean val$allowChats;
        final /* synthetic */ int val$currentReqId;
        final /* synthetic */ String val$query;

        /* renamed from: org.telegram.ui.Adapters.BaseSearchAdapterRecycler.1.1 */
        class C09241 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C09241(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (C17521.this.val$currentReqId == BaseSearchAdapterRecycler.this.lastReqId && this.val$error == null) {
                    int a;
                    TL_contacts_found res = this.val$response;
                    BaseSearchAdapterRecycler.this.globalSearch.clear();
                    if (C17521.this.val$allowChats) {
                        for (a = 0; a < res.chats.size(); a++) {
                            BaseSearchAdapterRecycler.this.globalSearch.add(res.chats.get(a));
                        }
                    }
                    for (a = 0; a < res.users.size(); a++) {
                        BaseSearchAdapterRecycler.this.globalSearch.add(res.users.get(a));
                    }
                    BaseSearchAdapterRecycler.this.lastFoundUsername = C17521.this.val$query;
                    BaseSearchAdapterRecycler.this.notifyDataSetChanged();
                }
                BaseSearchAdapterRecycler.this.reqId = 0;
            }
        }

        C17521(int i, boolean z, String str) {
            this.val$currentReqId = i;
            this.val$allowChats = z;
            this.val$query = str;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C09241(error, response));
        }
    }

    public BaseSearchAdapterRecycler() {
        this.globalSearch = new ArrayList();
        this.reqId = 0;
        this.lastFoundUsername = null;
        this.hashtagsLoadedFromDb = false;
    }

    public void queryServerSearch(String query, boolean allowChats) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (query == null || query.length() < 5) {
            this.globalSearch.clear();
            this.lastReqId = 0;
            notifyDataSetChanged();
            return;
        }
        TL_contacts_search req = new TL_contacts_search();
        req.f35q = query;
        req.limit = 50;
        int currentReqId = this.lastReqId + 1;
        this.lastReqId = currentReqId;
        this.reqId = ConnectionsManager.getInstance().sendRequest(req, new C17521(currentReqId, allowChats, query), 2);
    }

    public void loadRecentHashtags() {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09272());
    }

    public void addHashtagsFromMessage(CharSequence message) {
        if (message != null) {
            boolean changed = false;
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@\\.]+").matcher(message);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(message.charAt(start) == '@' || message.charAt(start) == '#')) {
                    start++;
                }
                String hashtag = message.subSequence(start, end).toString();
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap();
                    this.hashtags = new ArrayList();
                }
                HashtagObject hashtagObject = (HashtagObject) this.hashtagsByText.get(hashtag);
                if (hashtagObject == null) {
                    hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = hashtag;
                    this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
                } else {
                    this.hashtags.remove(hashtagObject);
                }
                hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
                this.hashtags.add(0, hashtagObject);
                changed = true;
            }
            if (changed) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09283(arrayList));
    }

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList();
        this.hashtagsByText = new HashMap();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09294());
    }

    protected void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
    }
}
