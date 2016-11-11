package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class SearchAdapter extends BaseSearchAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowUsernameSearch;
    private HashMap<Integer, ?> checkedMap;
    private HashMap<Integer, User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private ArrayList<User> searchResult;
    private ArrayList<CharSequence> searchResultNames;
    private Timer searchTimer;
    private boolean useUserCell;

    /* renamed from: org.telegram.ui.Adapters.SearchAdapter.1 */
    class C09521 extends TimerTask {
        final /* synthetic */ String val$query;

        C09521(String str) {
            this.val$query = str;
        }

        public void run() {
            try {
                SearchAdapter.this.searchTimer.cancel();
                SearchAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            SearchAdapter.this.processSearch(this.val$query);
        }
    }

    /* renamed from: org.telegram.ui.Adapters.SearchAdapter.2 */
    class C09542 implements Runnable {
        final /* synthetic */ String val$query;

        /* renamed from: org.telegram.ui.Adapters.SearchAdapter.2.1 */
        class C09531 implements Runnable {
            final /* synthetic */ ArrayList val$contactsCopy;

            C09531(ArrayList arrayList) {
                this.val$contactsCopy = arrayList;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r21 = this;
                r0 = r21;
                r0 = org.telegram.ui.Adapters.SearchAdapter.C09542.this;
                r17 = r0;
                r0 = r17;
                r0 = r0.val$query;
                r17 = r0;
                r17 = r17.trim();
                r13 = r17.toLowerCase();
                r17 = r13.length();
                if (r17 != 0) goto L_0x0034;
            L_0x001a:
                r0 = r21;
                r0 = org.telegram.ui.Adapters.SearchAdapter.C09542.this;
                r17 = r0;
                r0 = r17;
                r0 = org.telegram.ui.Adapters.SearchAdapter.this;
                r17 = r0;
                r18 = new java.util.ArrayList;
                r18.<init>();
                r19 = new java.util.ArrayList;
                r19.<init>();
                r17.updateSearchResults(r18, r19);
            L_0x0033:
                return;
            L_0x0034:
                r17 = org.telegram.messenger.LocaleController.getInstance();
                r0 = r17;
                r14 = r0.getTranslitString(r13);
                r17 = r13.equals(r14);
                if (r17 != 0) goto L_0x004a;
            L_0x0044:
                r17 = r14.length();
                if (r17 != 0) goto L_0x004b;
            L_0x004a:
                r14 = 0;
            L_0x004b:
                if (r14 == 0) goto L_0x00c3;
            L_0x004d:
                r17 = 1;
            L_0x004f:
                r17 = r17 + 1;
                r0 = r17;
                r12 = new java.lang.String[r0];
                r17 = 0;
                r12[r17] = r13;
                if (r14 == 0) goto L_0x005f;
            L_0x005b:
                r17 = 1;
                r12[r17] = r14;
            L_0x005f:
                r10 = new java.util.ArrayList;
                r10.<init>();
                r11 = new java.util.ArrayList;
                r11.<init>();
                r2 = 0;
            L_0x006a:
                r0 = r21;
                r0 = r0.val$contactsCopy;
                r17 = r0;
                r17 = r17.size();
                r0 = r17;
                if (r2 >= r0) goto L_0x01ba;
            L_0x0078:
                r0 = r21;
                r0 = r0.val$contactsCopy;
                r17 = r0;
                r0 = r17;
                r4 = r0.get(r2);
                r4 = (org.telegram.tgnet.TLRPC.TL_contact) r4;
                r17 = org.telegram.messenger.MessagesController.getInstance();
                r0 = r4.user_id;
                r18 = r0;
                r18 = java.lang.Integer.valueOf(r18);
                r16 = r17.getUser(r18);
                r0 = r16;
                r0 = r0.id;
                r17 = r0;
                r18 = org.telegram.messenger.UserConfig.getClientUserId();
                r0 = r17;
                r1 = r18;
                if (r0 == r1) goto L_0x00c0;
            L_0x00a6:
                r0 = r21;
                r0 = org.telegram.ui.Adapters.SearchAdapter.C09542.this;
                r17 = r0;
                r0 = r17;
                r0 = org.telegram.ui.Adapters.SearchAdapter.this;
                r17 = r0;
                r17 = r17.onlyMutual;
                if (r17 == 0) goto L_0x00c6;
            L_0x00b8:
                r0 = r16;
                r0 = r0.mutual_contact;
                r17 = r0;
                if (r17 != 0) goto L_0x00c6;
            L_0x00c0:
                r2 = r2 + 1;
                goto L_0x006a;
            L_0x00c3:
                r17 = 0;
                goto L_0x004f;
            L_0x00c6:
                r0 = r16;
                r0 = r0.first_name;
                r17 = r0;
                r0 = r16;
                r0 = r0.last_name;
                r18 = r0;
                r17 = org.telegram.messenger.ContactsController.formatName(r17, r18);
                r8 = r17.toLowerCase();
                r17 = org.telegram.messenger.LocaleController.getInstance();
                r0 = r17;
                r15 = r0.getTranslitString(r8);
                r17 = r8.equals(r15);
                if (r17 == 0) goto L_0x00eb;
            L_0x00ea:
                r15 = 0;
            L_0x00eb:
                r5 = 0;
                r3 = r12;
                r7 = r3.length;
                r6 = 0;
            L_0x00ef:
                if (r6 >= r7) goto L_0x00c0;
            L_0x00f1:
                r9 = r3[r6];
                r17 = r8.startsWith(r9);
                if (r17 != 0) goto L_0x013b;
            L_0x00f9:
                r17 = new java.lang.StringBuilder;
                r17.<init>();
                r18 = " ";
                r17 = r17.append(r18);
                r0 = r17;
                r17 = r0.append(r9);
                r17 = r17.toString();
                r0 = r17;
                r17 = r8.contains(r0);
                if (r17 != 0) goto L_0x013b;
            L_0x0116:
                if (r15 == 0) goto L_0x0164;
            L_0x0118:
                r17 = r15.startsWith(r9);
                if (r17 != 0) goto L_0x013b;
            L_0x011e:
                r17 = new java.lang.StringBuilder;
                r17.<init>();
                r18 = " ";
                r17 = r17.append(r18);
                r0 = r17;
                r17 = r0.append(r9);
                r17 = r17.toString();
                r0 = r17;
                r17 = r15.contains(r0);
                if (r17 == 0) goto L_0x0164;
            L_0x013b:
                r5 = 1;
            L_0x013c:
                if (r5 == 0) goto L_0x01b6;
            L_0x013e:
                r17 = 1;
                r0 = r17;
                if (r5 != r0) goto L_0x017c;
            L_0x0144:
                r0 = r16;
                r0 = r0.first_name;
                r17 = r0;
                r0 = r16;
                r0 = r0.last_name;
                r18 = r0;
                r0 = r17;
                r1 = r18;
                r17 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r1, r9);
                r0 = r17;
                r11.add(r0);
            L_0x015d:
                r0 = r16;
                r10.add(r0);
                goto L_0x00c0;
            L_0x0164:
                r0 = r16;
                r0 = r0.username;
                r17 = r0;
                if (r17 == 0) goto L_0x013c;
            L_0x016c:
                r0 = r16;
                r0 = r0.username;
                r17 = r0;
                r0 = r17;
                r17 = r0.startsWith(r9);
                if (r17 == 0) goto L_0x013c;
            L_0x017a:
                r5 = 2;
                goto L_0x013c;
            L_0x017c:
                r17 = new java.lang.StringBuilder;
                r17.<init>();
                r18 = "@";
                r17 = r17.append(r18);
                r0 = r16;
                r0 = r0.username;
                r18 = r0;
                r17 = r17.append(r18);
                r17 = r17.toString();
                r18 = 0;
                r19 = new java.lang.StringBuilder;
                r19.<init>();
                r20 = "@";
                r19 = r19.append(r20);
                r0 = r19;
                r19 = r0.append(r9);
                r19 = r19.toString();
                r17 = org.telegram.messenger.AndroidUtilities.generateSearchName(r17, r18, r19);
                r0 = r17;
                r11.add(r0);
                goto L_0x015d;
            L_0x01b6:
                r6 = r6 + 1;
                goto L_0x00ef;
            L_0x01ba:
                r0 = r21;
                r0 = org.telegram.ui.Adapters.SearchAdapter.C09542.this;
                r17 = r0;
                r0 = r17;
                r0 = org.telegram.ui.Adapters.SearchAdapter.this;
                r17 = r0;
                r0 = r17;
                r0.updateSearchResults(r10, r11);
                goto L_0x0033;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapter.2.1.run():void");
            }
        }

        C09542(String str) {
            this.val$query = str;
        }

        public void run() {
            if (SearchAdapter.this.allowUsernameSearch) {
                SearchAdapter.this.queryServerSearch(this.val$query, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots);
            }
            ArrayList<TL_contact> contactsCopy = new ArrayList();
            contactsCopy.addAll(ContactsController.getInstance().contacts);
            Utilities.searchQueue.postRunnable(new C09531(contactsCopy));
        }
    }

    /* renamed from: org.telegram.ui.Adapters.SearchAdapter.3 */
    class C09553 implements Runnable {
        final /* synthetic */ ArrayList val$names;
        final /* synthetic */ ArrayList val$users;

        C09553(ArrayList arrayList, ArrayList arrayList2) {
            this.val$users = arrayList;
            this.val$names = arrayList2;
        }

        public void run() {
            SearchAdapter.this.searchResult = this.val$users;
            SearchAdapter.this.searchResultNames = this.val$names;
            SearchAdapter.this.notifyDataSetChanged();
        }
    }

    public SearchAdapter(Context context, HashMap<Integer, User> arg1, boolean usernameSearch, boolean mutual, boolean chats, boolean bots) {
        this.searchResult = new ArrayList();
        this.searchResultNames = new ArrayList();
        this.mContext = context;
        this.ignoreUsers = arg1;
        this.onlyMutual = mutual;
        this.allowUsernameSearch = usernameSearch;
        this.allowChats = chats;
        this.allowBots = bots;
    }

    public void setCheckedMap(HashMap<Integer, ?> map) {
        this.checkedMap = map;
    }

    public void setUseUserCell(boolean value) {
        this.useUserCell = value;
    }

    public void searchDialogs(String query) {
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        if (query == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                queryServerSearch(null, this.allowChats, this.allowBots);
            }
            notifyDataSetChanged();
            return;
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new C09521(query), 200, 300);
    }

    private void processSearch(String query) {
        AndroidUtilities.runOnUIThread(new C09542(query));
    }

    private void updateSearchResults(ArrayList<User> users, ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new C09553(users, names));
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int i) {
        return i != this.searchResult.size();
    }

    public int getCount() {
        int count = this.searchResult.size();
        int globalCount = this.globalSearch.size();
        if (globalCount != 0) {
            return count + (globalCount + 1);
        }
        return count;
    }

    public boolean isGlobalSearch(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.globalSearch.size();
        if ((i < 0 || i >= localCount) && i > localCount && i <= globalCount + localCount) {
            return true;
        }
        return false;
    }

    public TLObject getItem(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.globalSearch.size();
        if (i >= 0 && i < localCount) {
            return (TLObject) this.searchResult.get(i);
        }
        if (i <= localCount || i > globalCount + localCount) {
            return null;
        }
        return (TLObject) this.globalSearch.get((i - localCount) - 1);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i != this.searchResult.size()) {
            if (view == null) {
                if (this.useUserCell) {
                    view = new UserCell(this.mContext, 1, 1, false);
                    if (this.checkedMap != null) {
                        ((UserCell) view).setChecked(false, false);
                    }
                } else {
                    view = new ProfileSearchCell(this.mContext);
                }
            }
            TLObject object = getItem(i);
            if (object == null) {
                return view;
            }
            int id = 0;
            String un = null;
            if (object instanceof User) {
                un = ((User) object).username;
                id = ((User) object).id;
            } else if (object instanceof Chat) {
                un = ((Chat) object).username;
                id = ((Chat) object).id;
            }
            CharSequence username = null;
            CharSequence name = null;
            if (i < this.searchResult.size()) {
                name = (CharSequence) this.searchResultNames.get(i);
                if (name != null && un != null && un.length() > 0 && name.toString().startsWith("@" + un)) {
                    username = name;
                    name = null;
                }
            } else if (i > this.searchResult.size() && un != null) {
                String foundUserName = this.lastFoundUsername;
                if (foundUserName.startsWith("@")) {
                    foundUserName = foundUserName.substring(1);
                }
                try {
                    username = AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", new Object[]{un.substring(0, foundUserName.length()), un.substring(foundUserName.length())}));
                } catch (Throwable e) {
                    Object username2 = un;
                    FileLog.m13e("tmessages", e);
                }
            }
            if (this.useUserCell) {
                ((UserCell) view).setData(object, name, username, 0);
                if (this.checkedMap == null) {
                    return view;
                }
                ((UserCell) view).setChecked(this.checkedMap.containsKey(Integer.valueOf(id)), false);
                return view;
            }
            ((ProfileSearchCell) view).setData(object, null, name, username, false);
            ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
            boolean z = (i == getCount() + -1 || i == this.searchResult.size() - 1) ? false : true;
            profileSearchCell.useSeparator = z;
            if (this.ignoreUsers == null) {
                return view;
            }
            if (this.ignoreUsers.containsKey(Integer.valueOf(id))) {
                ((ProfileSearchCell) view).drawAlpha = 0.5f;
                return view;
            }
            ((ProfileSearchCell) view).drawAlpha = TouchHelperCallback.ALPHA_FULL;
            return view;
        } else if (view != null) {
            return view;
        } else {
            view = new GreySectionCell(this.mContext);
            ((GreySectionCell) view).setText(LocaleController.getString("GlobalSearch", C0691R.string.GlobalSearch));
            return view;
        }
    }

    public int getItemViewType(int i) {
        if (i == this.searchResult.size()) {
            return 1;
        }
        return 0;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean isEmpty() {
        return this.searchResult.isEmpty() && this.globalSearch.isEmpty();
    }
}
