package org.telegram.ui.Adapters;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper.LocationProvider;
import org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import org.telegram.ui.Cells.MentionCell;

public class MentionsAdapter extends BaseSearchAdapterRecycler {
    private boolean allowNewMentions;
    private HashMap<Integer, BotInfo> botInfo;
    private int botsCount;
    private boolean contextMedia;
    private int contextQueryReqid;
    private Runnable contextQueryRunnable;
    private int contextUsernameReqid;
    private MentionsAdapterDelegate delegate;
    private long dialog_id;
    private User foundContextBot;
    private ChatFull info;
    private boolean isDarkTheme;
    private Location lastKnownLocation;
    private int lastPosition;
    private String lastText;
    private LocationProvider locationProvider;
    private Context mContext;
    private ArrayList<MessageObject> messages;
    private boolean needBotContext;
    private boolean needUsernames;
    private String nextQueryOffset;
    private boolean noUserName;
    private BaseFragment parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private ArrayList<BotInlineResult> searchResultBotContext;
    private HashMap<String, BotInlineResult> searchResultBotContextById;
    private TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<User> searchResultUsernames;
    private String searchingContextQuery;
    private String searchingContextUsername;

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.3 */
    class C09493 implements Runnable {
        final /* synthetic */ String val$query;
        final /* synthetic */ String val$username;

        /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.3.1 */
        class C17561 implements RequestDelegate {

            /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.3.1.1 */
            class C09481 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.3.1.1.1 */
                class C09461 implements OnClickListener {
                    final /* synthetic */ User val$foundContextBotFinal;

                    C09461(User user) {
                        this.val$foundContextBotFinal = user;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (this.val$foundContextBotFinal != null) {
                            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("inlinegeo_" + this.val$foundContextBotFinal.id, true).commit();
                            MentionsAdapter.this.checkLocationPermissionsOrStart();
                        }
                    }
                }

                /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.3.1.1.2 */
                class C09472 implements OnClickListener {
                    C09472() {
                    }

                    public void onClick(DialogInterface dialog, int which) {
                        MentionsAdapter.this.onLocationUnavailable();
                    }
                }

                C09481(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(C09493.this.val$username)) {
                        MentionsAdapter.this.contextUsernameReqid = 0;
                        MentionsAdapter.this.foundContextBot = null;
                        MentionsAdapter.this.locationProvider.stop();
                        if (this.val$error == null) {
                            TL_contacts_resolvedPeer res = this.val$response;
                            if (!res.users.isEmpty()) {
                                User user = (User) res.users.get(0);
                                if (user.bot && user.bot_inline_placeholder != null) {
                                    MessagesController.getInstance().putUser(user, false);
                                    MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                                    MentionsAdapter.this.foundContextBot = user;
                                    if (MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                                        if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("inlinegeo_" + MentionsAdapter.this.foundContextBot.id, false) || MentionsAdapter.this.parentFragment == null || MentionsAdapter.this.parentFragment.getParentActivity() == null) {
                                            MentionsAdapter.this.checkLocationPermissionsOrStart();
                                        } else {
                                            User foundContextBotFinal = MentionsAdapter.this.foundContextBot;
                                            Builder builder = new Builder(MentionsAdapter.this.parentFragment.getParentActivity());
                                            builder.setTitle(LocaleController.getString("ShareYouLocationTitle", C0691R.string.ShareYouLocationTitle));
                                            builder.setMessage(LocaleController.getString("ShareYouLocationInline", C0691R.string.ShareYouLocationInline));
                                            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C09461(foundContextBotFinal));
                                            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), new C09472());
                                            MentionsAdapter.this.parentFragment.showDialog(builder.create());
                                        }
                                    }
                                }
                            }
                        }
                        if (MentionsAdapter.this.foundContextBot == null) {
                            MentionsAdapter.this.noUserName = true;
                            return;
                        }
                        if (MentionsAdapter.this.delegate != null) {
                            MentionsAdapter.this.delegate.onContextSearch(true);
                        }
                        MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
                    }
                }
            }

            C17561() {
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C09481(error, response));
            }
        }

        C09493(String str, String str2) {
            this.val$query = str;
            this.val$username = str2;
        }

        public void run() {
            if (MentionsAdapter.this.contextQueryRunnable == this) {
                MentionsAdapter.this.contextQueryRunnable = null;
                if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                    TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                    req.username = MentionsAdapter.this.searchingContextUsername = this.val$username;
                    MentionsAdapter.this.contextUsernameReqid = ConnectionsManager.getInstance().sendRequest(req, new C17561());
                } else if (!MentionsAdapter.this.noUserName) {
                    MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, this.val$query, TtmlNode.ANONYMOUS_REGION_ID);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.5 */
    class C09515 implements Comparator<User> {
        final /* synthetic */ HashMap val$newResultsHashMap;
        final /* synthetic */ ArrayList val$users;

        C09515(HashMap hashMap, ArrayList arrayList) {
            this.val$newResultsHashMap = hashMap;
            this.val$users = arrayList;
        }

        public int compare(User lhs, User rhs) {
            if (this.val$newResultsHashMap.containsKey(Integer.valueOf(lhs.id)) && this.val$newResultsHashMap.containsKey(Integer.valueOf(rhs.id))) {
                return 0;
            }
            if (this.val$newResultsHashMap.containsKey(Integer.valueOf(lhs.id))) {
                return -1;
            }
            if (this.val$newResultsHashMap.containsKey(Integer.valueOf(rhs.id))) {
                return 1;
            }
            int lhsNum = this.val$users.indexOf(Integer.valueOf(lhs.id));
            int rhsNum = this.val$users.indexOf(Integer.valueOf(rhs.id));
            if (lhsNum == -1 || rhsNum == -1) {
                if (lhsNum != -1 && rhsNum == -1) {
                    return -1;
                }
                if (lhsNum != -1 || rhsNum == -1) {
                    return 0;
                }
                return 1;
            } else if (lhsNum >= rhsNum) {
                return lhsNum == rhsNum ? 0 : 1;
            } else {
                return -1;
            }
        }
    }

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(BotInlineResult botInlineResult);

        void onContextSearch(boolean z);
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.1 */
    class C17541 implements LocationProviderDelegate {
        C17541() {
        }

        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.2 */
    class C17552 extends LocationProvider {
        C17552(LocationProviderDelegate x0) {
            super(x0);
        }

        public void stop() {
            super.stop();
            MentionsAdapter.this.lastKnownLocation = null;
        }
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.4 */
    class C17574 implements RequestDelegate {
        final /* synthetic */ String val$offset;
        final /* synthetic */ String val$query;

        /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.4.1 */
        class C09501 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C09501(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = false;
                if (MentionsAdapter.this.searchingContextQuery != null && C17574.this.val$query.equals(MentionsAdapter.this.searchingContextQuery)) {
                    if (MentionsAdapter.this.delegate != null) {
                        MentionsAdapter.this.delegate.onContextSearch(false);
                    }
                    MentionsAdapter.this.contextQueryReqid = 0;
                    if (this.val$error == null) {
                        TL_messages_botResults res = this.val$response;
                        MentionsAdapter.this.nextQueryOffset = res.next_offset;
                        if (MentionsAdapter.this.searchResultBotContextById == null) {
                            MentionsAdapter.this.searchResultBotContextById = new HashMap();
                            MentionsAdapter.this.searchResultBotContextSwitch = res.switch_pm;
                        }
                        int a = 0;
                        while (a < res.results.size()) {
                            BotInlineResult result = (BotInlineResult) res.results.get(a);
                            if (MentionsAdapter.this.searchResultBotContextById.containsKey(result.id) || (!(result.document instanceof TL_document) && !(result.photo instanceof TL_photo) && result.content_url == null && (result.send_message instanceof TL_botInlineMessageMediaAuto))) {
                                res.results.remove(a);
                                a--;
                            }
                            result.query_id = res.query_id;
                            MentionsAdapter.this.searchResultBotContextById.put(result.id, result);
                            a++;
                        }
                        boolean added = false;
                        if (MentionsAdapter.this.searchResultBotContext == null || C17574.this.val$offset.length() == 0) {
                            MentionsAdapter.this.searchResultBotContext = res.results;
                            MentionsAdapter.this.contextMedia = res.gallery;
                        } else {
                            added = true;
                            MentionsAdapter.this.searchResultBotContext.addAll(res.results);
                            if (res.results.isEmpty()) {
                                MentionsAdapter.this.nextQueryOffset = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                        }
                        MentionsAdapter.this.searchResultHashtags = null;
                        MentionsAdapter.this.searchResultUsernames = null;
                        MentionsAdapter.this.searchResultCommands = null;
                        MentionsAdapter.this.searchResultCommandsHelp = null;
                        MentionsAdapter.this.searchResultCommandsUsers = null;
                        if (added) {
                            boolean hasTop;
                            int i;
                            if (MentionsAdapter.this.searchResultBotContextSwitch != null) {
                                hasTop = true;
                            } else {
                                hasTop = false;
                            }
                            MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                            int size = MentionsAdapter.this.searchResultBotContext.size() - res.results.size();
                            if (hasTop) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            mentionsAdapter.notifyItemChanged((i + size) - 1);
                            mentionsAdapter = MentionsAdapter.this;
                            size = MentionsAdapter.this.searchResultBotContext.size() - res.results.size();
                            if (hasTop) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            mentionsAdapter.notifyItemRangeInserted(i + size, res.results.size());
                        } else {
                            MentionsAdapter.this.notifyDataSetChanged();
                        }
                        MentionsAdapterDelegate access$1200 = MentionsAdapter.this.delegate;
                        if (!(MentionsAdapter.this.searchResultBotContext.isEmpty() && MentionsAdapter.this.searchResultBotContextSwitch == null)) {
                            z = true;
                        }
                        access$1200.needChangePanelVisibility(z);
                    }
                }
            }
        }

        C17574(String str, String str2) {
            this.val$query = str;
            this.val$offset = str2;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C09501(error, response));
        }
    }

    /* renamed from: org.telegram.ui.Adapters.MentionsAdapter.6 */
    class C17586 implements ContextLinkCellDelegate {
        C17586() {
        }

        public void didPressedImage(ContextLinkCell cell) {
            MentionsAdapter.this.delegate.onContextClick(cell.getResult());
        }
    }

    public class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public MentionsAdapter(Context context, boolean darkTheme, long did, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.allowNewMentions = true;
        this.needUsernames = true;
        this.needBotContext = true;
        this.locationProvider = new C17552(new C17541());
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = darkTheme;
        this.dialog_id = did;
    }

    public void onDestroy() {
        if (this.locationProvider != null) {
            this.locationProvider.stop();
        }
        if (this.contextQueryRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
            this.contextQueryRunnable = null;
        }
        if (this.contextUsernameReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
    }

    public void setAllowNewMentions(boolean value) {
        this.allowNewMentions = value;
    }

    public void setParentFragment(BaseFragment fragment) {
        this.parentFragment = fragment;
    }

    public void setChatInfo(ChatFull chatParticipants) {
        this.info = chatParticipants;
        if (this.lastText != null) {
            searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
        }
    }

    public void setNeedUsernames(boolean value) {
        this.needUsernames = value;
    }

    public void setNeedBotContext(boolean value) {
        this.needBotContext = value;
    }

    public void setBotInfo(HashMap<Integer, BotInfo> info) {
        this.botInfo = info;
    }

    public void setBotsCount(int count) {
        this.botsCount = count;
    }

    public void clearRecentHashtags() {
        super.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        if (this.delegate != null) {
            this.delegate.needChangePanelVisibility(false);
        }
    }

    protected void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        super.setHashtags(arrayList, hashMap);
        if (this.lastText != null) {
            searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
        }
    }

    public TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        return this.foundContextBot != null ? this.foundContextBot.id : 0;
    }

    public User getContextBotUser() {
        return this.foundContextBot != null ? this.foundContextBot : null;
    }

    public String getContextBotName() {
        return this.foundContextBot != null ? this.foundContextBot.username : TtmlNode.ANONYMOUS_REGION_ID;
    }

    private void searchForContextBot(String username, String query) {
        this.searchResultBotContext = null;
        this.searchResultBotContextById = null;
        this.searchResultBotContextSwitch = null;
        notifyDataSetChanged();
        if (this.foundContextBot != null) {
            this.delegate.needChangePanelVisibility(false);
        }
        if (this.contextQueryRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
            this.contextQueryRunnable = null;
        }
        if (username == null || username.length() == 0 || !(this.searchingContextUsername == null || this.searchingContextUsername.equals(username))) {
            if (this.contextUsernameReqid != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
                this.contextUsernameReqid = 0;
            }
            if (this.contextQueryReqid != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
                this.contextQueryReqid = 0;
            }
            this.foundContextBot = null;
            this.searchingContextUsername = null;
            this.searchingContextQuery = null;
            this.locationProvider.stop();
            this.noUserName = false;
            if (this.delegate != null) {
                this.delegate.onContextSearch(false);
            }
            if (username == null || username.length() == 0) {
                return;
            }
        }
        if (query == null) {
            if (this.contextQueryReqid != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
                this.contextQueryReqid = 0;
            }
            this.searchingContextQuery = null;
            if (this.delegate != null) {
                this.delegate.onContextSearch(false);
                return;
            }
            return;
        }
        if (this.delegate != null) {
            if (this.foundContextBot != null) {
                this.delegate.onContextSearch(true);
            } else if (username.equals("gif")) {
                this.searchingContextUsername = "gif";
                this.delegate.onContextSearch(false);
            }
        }
        this.searchingContextQuery = query;
        this.contextQueryRunnable = new C09493(query, username);
        AndroidUtilities.runOnUIThread(this.contextQueryRunnable, 400);
    }

    private void onLocationUnavailable() {
        if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
            this.lastKnownLocation = new Location("network");
            this.lastKnownLocation.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void checkLocationPermissionsOrStart() {
        if (this.parentFragment != null && this.parentFragment.getParentActivity() != null) {
            if (VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
                this.locationProvider.start();
            }
        }
    }

    public String getBotCaption() {
        if (this.foundContextBot != null) {
            return this.foundContextBot.bot_inline_placeholder;
        }
        if (this.searchingContextUsername == null || !this.searchingContextUsername.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        if (this.contextQueryReqid == 0 && this.nextQueryOffset != null && this.nextQueryOffset.length() != 0 && this.foundContextBot != null && this.searchingContextQuery != null) {
            searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
        }
    }

    private void searchForContextBotResults(User user, String query, String offset) {
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        if (query == null || user == null) {
            this.searchingContextQuery = null;
        } else if (!user.bot_inline_geo || this.lastKnownLocation != null) {
            TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
            req.bot = MessagesController.getInputUser(user);
            req.query = query;
            req.offset = offset;
            if (!(!user.bot_inline_geo || this.lastKnownLocation == null || this.lastKnownLocation.getLatitude() == -1000.0d)) {
                req.flags |= 1;
                req.geo_point = new TL_inputGeoPoint();
                req.geo_point.lat = this.lastKnownLocation.getLatitude();
                req.geo_point._long = this.lastKnownLocation.getLongitude();
            }
            int lower_id = (int) this.dialog_id;
            int high_id = (int) (this.dialog_id >> 32);
            if (lower_id != 0) {
                req.peer = MessagesController.getInputPeer(lower_id);
            } else {
                req.peer = new TL_inputPeerEmpty();
            }
            this.contextQueryReqid = ConnectionsManager.getInstance().sendRequest(req, new C17574(query, offset), 2);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r35, int r36, java.util.ArrayList<org.telegram.messenger.MessageObject> r37) {
        /*
        r34 = this;
        if (r35 == 0) goto L_0x0008;
    L_0x0002:
        r32 = r35.length();
        if (r32 != 0) goto L_0x0029;
    L_0x0008:
        r32 = 0;
        r33 = 0;
        r0 = r34;
        r1 = r32;
        r2 = r33;
        r0.searchForContextBot(r1, r2);
        r0 = r34;
        r0 = r0.delegate;
        r32 = r0;
        r33 = 0;
        r32.needChangePanelVisibility(r33);
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.lastText = r0;
    L_0x0028:
        return;
    L_0x0029:
        r27 = r36;
        r32 = r35.length();
        if (r32 <= 0) goto L_0x0033;
    L_0x0031:
        r27 = r27 + -1;
    L_0x0033:
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.lastText = r0;
        r26 = new java.lang.StringBuilder;
        r26.<init>();
        r13 = -1;
        r15 = 0;
        r0 = r34;
        r0 = r0.needBotContext;
        r32 = r0;
        if (r32 == 0) goto L_0x010a;
    L_0x004a:
        r32 = 0;
        r0 = r35;
        r1 = r32;
        r32 = r0.charAt(r1);
        r33 = 64;
        r0 = r32;
        r1 = r33;
        if (r0 != r1) goto L_0x010a;
    L_0x005c:
        r32 = 32;
        r0 = r35;
        r1 = r32;
        r19 = r0.indexOf(r1);
        if (r19 <= 0) goto L_0x00fc;
    L_0x0068:
        r32 = 1;
        r0 = r35;
        r1 = r32;
        r2 = r19;
        r29 = r0.substring(r1, r2);
        r32 = r29.length();
        r33 = 1;
        r0 = r32;
        r1 = r33;
        if (r0 < r1) goto L_0x00ed;
    L_0x0080:
        r4 = 1;
    L_0x0081:
        r32 = r29.length();
        r0 = r32;
        if (r4 >= r0) goto L_0x00bb;
    L_0x0089:
        r0 = r29;
        r7 = r0.charAt(r4);
        r32 = 48;
        r0 = r32;
        if (r7 < r0) goto L_0x009b;
    L_0x0095:
        r32 = 57;
        r0 = r32;
        if (r7 <= r0) goto L_0x00ea;
    L_0x009b:
        r32 = 97;
        r0 = r32;
        if (r7 < r0) goto L_0x00a7;
    L_0x00a1:
        r32 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        r0 = r32;
        if (r7 <= r0) goto L_0x00ea;
    L_0x00a7:
        r32 = 65;
        r0 = r32;
        if (r7 < r0) goto L_0x00b3;
    L_0x00ad:
        r32 = 90;
        r0 = r32;
        if (r7 <= r0) goto L_0x00ea;
    L_0x00b3:
        r32 = 95;
        r0 = r32;
        if (r7 == r0) goto L_0x00ea;
    L_0x00b9:
        r29 = "";
    L_0x00bb:
        r32 = r19 + 1;
        r0 = r35;
        r1 = r32;
        r25 = r0.substring(r1);
        r32 = r29.length();
        if (r32 <= 0) goto L_0x00f0;
    L_0x00cb:
        r32 = r25.length();
        if (r32 < 0) goto L_0x00f0;
    L_0x00d1:
        r0 = r34;
        r1 = r29;
        r2 = r25;
        r0.searchForContextBot(r1, r2);
    L_0x00da:
        r11 = -1;
        r4 = r27;
    L_0x00dd:
        if (r4 < 0) goto L_0x01a6;
    L_0x00df:
        r32 = r35.length();
        r0 = r32;
        if (r4 < r0) goto L_0x0118;
    L_0x00e7:
        r4 = r4 + -1;
        goto L_0x00dd;
    L_0x00ea:
        r4 = r4 + 1;
        goto L_0x0081;
    L_0x00ed:
        r29 = "";
        goto L_0x00bb;
    L_0x00f0:
        r32 = 0;
        r0 = r34;
        r1 = r29;
        r2 = r32;
        r0.searchForContextBot(r1, r2);
        goto L_0x00da;
    L_0x00fc:
        r32 = 0;
        r33 = 0;
        r0 = r34;
        r1 = r32;
        r2 = r33;
        r0.searchForContextBot(r1, r2);
        goto L_0x00da;
    L_0x010a:
        r32 = 0;
        r33 = 0;
        r0 = r34;
        r1 = r32;
        r2 = r33;
        r0.searchForContextBot(r1, r2);
        goto L_0x00da;
    L_0x0118:
        r0 = r35;
        r7 = r0.charAt(r4);
        if (r4 == 0) goto L_0x0144;
    L_0x0120:
        r32 = r4 + -1;
        r0 = r35;
        r1 = r32;
        r32 = r0.charAt(r1);
        r33 = 32;
        r0 = r32;
        r1 = r33;
        if (r0 == r1) goto L_0x0144;
    L_0x0132:
        r32 = r4 + -1;
        r0 = r35;
        r1 = r32;
        r32 = r0.charAt(r1);
        r33 = 10;
        r0 = r32;
        r1 = r33;
        if (r0 != r1) goto L_0x0226;
    L_0x0144:
        r32 = 64;
        r0 = r32;
        if (r7 != r0) goto L_0x01b9;
    L_0x014a:
        r0 = r34;
        r0 = r0.needUsernames;
        r32 = r0;
        if (r32 != 0) goto L_0x015c;
    L_0x0152:
        r0 = r34;
        r0 = r0.needBotContext;
        r32 = r0;
        if (r32 == 0) goto L_0x0226;
    L_0x015a:
        if (r4 != 0) goto L_0x0226;
    L_0x015c:
        if (r15 == 0) goto L_0x016b;
    L_0x015e:
        r0 = r34;
        r0 = r0.delegate;
        r32 = r0;
        r33 = 0;
        r32.needChangePanelVisibility(r33);
        goto L_0x0028;
    L_0x016b:
        r0 = r34;
        r0 = r0.info;
        r32 = r0;
        if (r32 != 0) goto L_0x0194;
    L_0x0173:
        if (r4 == 0) goto L_0x0194;
    L_0x0175:
        r0 = r35;
        r1 = r34;
        r1.lastText = r0;
        r0 = r36;
        r1 = r34;
        r1.lastPosition = r0;
        r0 = r37;
        r1 = r34;
        r1.messages = r0;
        r0 = r34;
        r0 = r0.delegate;
        r32 = r0;
        r33 = 0;
        r32.needChangePanelVisibility(r33);
        goto L_0x0028;
    L_0x0194:
        r11 = r4;
        r13 = 0;
        r0 = r34;
        r0.resultStartPosition = r4;
        r32 = r26.length();
        r32 = r32 + 1;
        r0 = r32;
        r1 = r34;
        r1.resultLength = r0;
    L_0x01a6:
        r32 = -1;
        r0 = r32;
        if (r13 != r0) goto L_0x025c;
    L_0x01ac:
        r0 = r34;
        r0 = r0.delegate;
        r32 = r0;
        r33 = 0;
        r32.needChangePanelVisibility(r33);
        goto L_0x0028;
    L_0x01b9:
        r32 = 35;
        r0 = r32;
        if (r7 != r0) goto L_0x0204;
    L_0x01bf:
        r0 = r34;
        r0 = r0.hashtagsLoadedFromDb;
        r32 = r0;
        if (r32 != 0) goto L_0x01e9;
    L_0x01c7:
        r34.loadRecentHashtags();
        r0 = r35;
        r1 = r34;
        r1.lastText = r0;
        r0 = r36;
        r1 = r34;
        r1.lastPosition = r0;
        r0 = r37;
        r1 = r34;
        r1.messages = r0;
        r0 = r34;
        r0 = r0.delegate;
        r32 = r0;
        r33 = 0;
        r32.needChangePanelVisibility(r33);
        goto L_0x0028;
    L_0x01e9:
        r13 = 1;
        r0 = r34;
        r0.resultStartPosition = r4;
        r32 = r26.length();
        r32 = r32 + 1;
        r0 = r32;
        r1 = r34;
        r1.resultLength = r0;
        r32 = 0;
        r0 = r26;
        r1 = r32;
        r0.insert(r1, r7);
        goto L_0x01a6;
    L_0x0204:
        if (r4 != 0) goto L_0x0226;
    L_0x0206:
        r0 = r34;
        r0 = r0.botInfo;
        r32 = r0;
        if (r32 == 0) goto L_0x0226;
    L_0x020e:
        r32 = 47;
        r0 = r32;
        if (r7 != r0) goto L_0x0226;
    L_0x0214:
        r13 = 2;
        r0 = r34;
        r0.resultStartPosition = r4;
        r32 = r26.length();
        r32 = r32 + 1;
        r0 = r32;
        r1 = r34;
        r1.resultLength = r0;
        goto L_0x01a6;
    L_0x0226:
        r32 = 48;
        r0 = r32;
        if (r7 < r0) goto L_0x0232;
    L_0x022c:
        r32 = 57;
        r0 = r32;
        if (r7 <= r0) goto L_0x0251;
    L_0x0232:
        r32 = 97;
        r0 = r32;
        if (r7 < r0) goto L_0x023e;
    L_0x0238:
        r32 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        r0 = r32;
        if (r7 <= r0) goto L_0x0251;
    L_0x023e:
        r32 = 65;
        r0 = r32;
        if (r7 < r0) goto L_0x024a;
    L_0x0244:
        r32 = 90;
        r0 = r32;
        if (r7 <= r0) goto L_0x0251;
    L_0x024a:
        r32 = 95;
        r0 = r32;
        if (r7 == r0) goto L_0x0251;
    L_0x0250:
        r15 = 1;
    L_0x0251:
        r32 = 0;
        r0 = r26;
        r1 = r32;
        r0.insert(r1, r7);
        goto L_0x00e7;
    L_0x025c:
        if (r13 != 0) goto L_0x0504;
    L_0x025e:
        r31 = new java.util.ArrayList;
        r31.<init>();
        r4 = 0;
    L_0x0264:
        r32 = 100;
        r33 = r37.size();
        r32 = java.lang.Math.min(r32, r33);
        r0 = r32;
        if (r4 >= r0) goto L_0x0298;
    L_0x0272:
        r0 = r37;
        r32 = r0.get(r4);
        r32 = (org.telegram.messenger.MessageObject) r32;
        r0 = r32;
        r0 = r0.messageOwner;
        r32 = r0;
        r0 = r32;
        r14 = r0.from_id;
        r32 = java.lang.Integer.valueOf(r14);
        r32 = r31.contains(r32);
        if (r32 != 0) goto L_0x0295;
    L_0x028e:
        r32 = java.lang.Integer.valueOf(r14);
        r31.add(r32);
    L_0x0295:
        r4 = r4 + 1;
        goto L_0x0264;
    L_0x0298:
        r32 = r26.toString();
        r30 = r32.toLowerCase();
        r21 = new java.util.ArrayList;
        r21.<init>();
        r24 = new java.util.HashMap;
        r24.<init>();
        r0 = r34;
        r0 = r0.needBotContext;
        r32 = r0;
        if (r32 == 0) goto L_0x0349;
    L_0x02b2:
        if (r11 != 0) goto L_0x0349;
    L_0x02b4:
        r32 = org.telegram.messenger.query.SearchQuery.inlineBots;
        r32 = r32.isEmpty();
        if (r32 != 0) goto L_0x0349;
    L_0x02bc:
        r10 = 0;
        r4 = 0;
    L_0x02be:
        r32 = org.telegram.messenger.query.SearchQuery.inlineBots;
        r32 = r32.size();
        r0 = r32;
        if (r4 >= r0) goto L_0x0349;
    L_0x02c8:
        r33 = org.telegram.messenger.MessagesController.getInstance();
        r32 = org.telegram.messenger.query.SearchQuery.inlineBots;
        r0 = r32;
        r32 = r0.get(r4);
        r32 = (org.telegram.tgnet.TLRPC.TL_topPeer) r32;
        r0 = r32;
        r0 = r0.peer;
        r32 = r0;
        r0 = r32;
        r0 = r0.user_id;
        r32 = r0;
        r32 = java.lang.Integer.valueOf(r32);
        r0 = r33;
        r1 = r32;
        r28 = r0.getUser(r1);
        if (r28 != 0) goto L_0x02f3;
    L_0x02f0:
        r4 = r4 + 1;
        goto L_0x02be;
    L_0x02f3:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        if (r32 == 0) goto L_0x0343;
    L_0x02fb:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        r32 = r32.length();
        if (r32 <= 0) goto L_0x0343;
    L_0x0307:
        r32 = r30.length();
        if (r32 <= 0) goto L_0x0321;
    L_0x030d:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        r32 = r32.toLowerCase();
        r0 = r32;
        r1 = r30;
        r32 = r0.startsWith(r1);
        if (r32 != 0) goto L_0x0327;
    L_0x0321:
        r32 = r30.length();
        if (r32 != 0) goto L_0x0343;
    L_0x0327:
        r0 = r21;
        r1 = r28;
        r0.add(r1);
        r0 = r28;
        r0 = r0.id;
        r32 = r0;
        r32 = java.lang.Integer.valueOf(r32);
        r0 = r24;
        r1 = r32;
        r2 = r28;
        r0.put(r1, r2);
        r10 = r10 + 1;
    L_0x0343:
        r32 = 5;
        r0 = r32;
        if (r10 != r0) goto L_0x02f0;
    L_0x0349:
        r0 = r34;
        r0 = r0.info;
        r32 = r0;
        if (r32 == 0) goto L_0x04ab;
    L_0x0351:
        r0 = r34;
        r0 = r0.info;
        r32 = r0;
        r0 = r32;
        r0 = r0.participants;
        r32 = r0;
        if (r32 == 0) goto L_0x04ab;
    L_0x035f:
        r4 = 0;
    L_0x0360:
        r0 = r34;
        r0 = r0.info;
        r32 = r0;
        r0 = r32;
        r0 = r0.participants;
        r32 = r0;
        r0 = r32;
        r0 = r0.participants;
        r32 = r0;
        r32 = r32.size();
        r0 = r32;
        if (r4 >= r0) goto L_0x04ab;
    L_0x037a:
        r0 = r34;
        r0 = r0.info;
        r32 = r0;
        r0 = r32;
        r0 = r0.participants;
        r32 = r0;
        r0 = r32;
        r0 = r0.participants;
        r32 = r0;
        r0 = r32;
        r8 = r0.get(r4);
        r8 = (org.telegram.tgnet.TLRPC.ChatParticipant) r8;
        r32 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r8.user_id;
        r33 = r0;
        r33 = java.lang.Integer.valueOf(r33);
        r28 = r32.getUser(r33);
        if (r28 == 0) goto L_0x03c0;
    L_0x03a6:
        r32 = org.telegram.messenger.UserObject.isUserSelf(r28);
        if (r32 != 0) goto L_0x03c0;
    L_0x03ac:
        r0 = r28;
        r0 = r0.id;
        r32 = r0;
        r32 = java.lang.Integer.valueOf(r32);
        r0 = r24;
        r1 = r32;
        r32 = r0.containsKey(r1);
        if (r32 == 0) goto L_0x03c3;
    L_0x03c0:
        r4 = r4 + 1;
        goto L_0x0360;
    L_0x03c3:
        r32 = r30.length();
        if (r32 != 0) goto L_0x03fd;
    L_0x03c9:
        r0 = r28;
        r0 = r0.deleted;
        r32 = r0;
        if (r32 != 0) goto L_0x03c0;
    L_0x03d1:
        r0 = r34;
        r0 = r0.allowNewMentions;
        r32 = r0;
        if (r32 != 0) goto L_0x03f5;
    L_0x03d9:
        r0 = r34;
        r0 = r0.allowNewMentions;
        r32 = r0;
        if (r32 != 0) goto L_0x03c0;
    L_0x03e1:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        if (r32 == 0) goto L_0x03c0;
    L_0x03e9:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        r32 = r32.length();
        if (r32 == 0) goto L_0x03c0;
    L_0x03f5:
        r0 = r21;
        r1 = r28;
        r0.add(r1);
        goto L_0x03c0;
    L_0x03fd:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        if (r32 == 0) goto L_0x042d;
    L_0x0405:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        r32 = r32.length();
        if (r32 <= 0) goto L_0x042d;
    L_0x0411:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        r32 = r32.toLowerCase();
        r0 = r32;
        r1 = r30;
        r32 = r0.startsWith(r1);
        if (r32 == 0) goto L_0x042d;
    L_0x0425:
        r0 = r21;
        r1 = r28;
        r0.add(r1);
        goto L_0x03c0;
    L_0x042d:
        r0 = r34;
        r0 = r0.allowNewMentions;
        r32 = r0;
        if (r32 != 0) goto L_0x0449;
    L_0x0435:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        if (r32 == 0) goto L_0x03c0;
    L_0x043d:
        r0 = r28;
        r0 = r0.username;
        r32 = r0;
        r32 = r32.length();
        if (r32 == 0) goto L_0x03c0;
    L_0x0449:
        r0 = r28;
        r0 = r0.first_name;
        r32 = r0;
        if (r32 == 0) goto L_0x047a;
    L_0x0451:
        r0 = r28;
        r0 = r0.first_name;
        r32 = r0;
        r32 = r32.length();
        if (r32 <= 0) goto L_0x047a;
    L_0x045d:
        r0 = r28;
        r0 = r0.first_name;
        r32 = r0;
        r32 = r32.toLowerCase();
        r0 = r32;
        r1 = r30;
        r32 = r0.startsWith(r1);
        if (r32 == 0) goto L_0x047a;
    L_0x0471:
        r0 = r21;
        r1 = r28;
        r0.add(r1);
        goto L_0x03c0;
    L_0x047a:
        r0 = r28;
        r0 = r0.last_name;
        r32 = r0;
        if (r32 == 0) goto L_0x03c0;
    L_0x0482:
        r0 = r28;
        r0 = r0.last_name;
        r32 = r0;
        r32 = r32.length();
        if (r32 <= 0) goto L_0x03c0;
    L_0x048e:
        r0 = r28;
        r0 = r0.last_name;
        r32 = r0;
        r32 = r32.toLowerCase();
        r0 = r32;
        r1 = r30;
        r32 = r0.startsWith(r1);
        if (r32 == 0) goto L_0x03c0;
    L_0x04a2:
        r0 = r21;
        r1 = r28;
        r0.add(r1);
        goto L_0x03c0;
    L_0x04ab:
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultHashtags = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultCommands = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultCommandsHelp = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultCommandsUsers = r0;
        r0 = r21;
        r1 = r34;
        r1.searchResultUsernames = r0;
        r0 = r34;
        r0 = r0.searchResultUsernames;
        r32 = r0;
        r33 = new org.telegram.ui.Adapters.MentionsAdapter$5;
        r0 = r33;
        r1 = r34;
        r2 = r24;
        r3 = r31;
        r0.<init>(r2, r3);
        java.util.Collections.sort(r32, r33);
        r34.notifyDataSetChanged();
        r0 = r34;
        r0 = r0.delegate;
        r33 = r0;
        r32 = r21.isEmpty();
        if (r32 != 0) goto L_0x0501;
    L_0x04f6:
        r32 = 1;
    L_0x04f8:
        r0 = r33;
        r1 = r32;
        r0.needChangePanelVisibility(r1);
        goto L_0x0028;
    L_0x0501:
        r32 = 0;
        goto L_0x04f8;
    L_0x0504:
        r32 = 1;
        r0 = r32;
        if (r13 != r0) goto L_0x05a1;
    L_0x050a:
        r20 = new java.util.ArrayList;
        r20.<init>();
        r32 = r26.toString();
        r17 = r32.toLowerCase();
        r4 = 0;
    L_0x0518:
        r0 = r34;
        r0 = r0.hashtags;
        r32 = r0;
        r32 = r32.size();
        r0 = r32;
        if (r4 >= r0) goto L_0x055e;
    L_0x0526:
        r0 = r34;
        r0 = r0.hashtags;
        r32 = r0;
        r0 = r32;
        r16 = r0.get(r4);
        r16 = (org.telegram.ui.Adapters.BaseSearchAdapterRecycler.HashtagObject) r16;
        if (r16 == 0) goto L_0x055b;
    L_0x0536:
        r0 = r16;
        r0 = r0.hashtag;
        r32 = r0;
        if (r32 == 0) goto L_0x055b;
    L_0x053e:
        r0 = r16;
        r0 = r0.hashtag;
        r32 = r0;
        r0 = r32;
        r1 = r17;
        r32 = r0.startsWith(r1);
        if (r32 == 0) goto L_0x055b;
    L_0x054e:
        r0 = r16;
        r0 = r0.hashtag;
        r32 = r0;
        r0 = r20;
        r1 = r32;
        r0.add(r1);
    L_0x055b:
        r4 = r4 + 1;
        goto L_0x0518;
    L_0x055e:
        r0 = r20;
        r1 = r34;
        r1.searchResultHashtags = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultUsernames = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultCommands = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultCommandsHelp = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultCommandsUsers = r0;
        r34.notifyDataSetChanged();
        r0 = r34;
        r0 = r0.delegate;
        r33 = r0;
        r32 = r20.isEmpty();
        if (r32 != 0) goto L_0x059e;
    L_0x0593:
        r32 = 1;
    L_0x0595:
        r0 = r33;
        r1 = r32;
        r0.needChangePanelVisibility(r1);
        goto L_0x0028;
    L_0x059e:
        r32 = 0;
        goto L_0x0595;
    L_0x05a1:
        r32 = 2;
        r0 = r32;
        if (r13 != r0) goto L_0x0028;
    L_0x05a7:
        r20 = new java.util.ArrayList;
        r20.<init>();
        r22 = new java.util.ArrayList;
        r22.<init>();
        r23 = new java.util.ArrayList;
        r23.<init>();
        r32 = r26.toString();
        r9 = r32.toLowerCase();
        r0 = r34;
        r0 = r0.botInfo;
        r32 = r0;
        r32 = r32.entrySet();
        r18 = r32.iterator();
    L_0x05cc:
        r32 = r18.hasNext();
        if (r32 == 0) goto L_0x064e;
    L_0x05d2:
        r12 = r18.next();
        r12 = (java.util.Map.Entry) r12;
        r6 = r12.getValue();
        r6 = (org.telegram.tgnet.TLRPC.BotInfo) r6;
        r4 = 0;
    L_0x05df:
        r0 = r6.commands;
        r32 = r0;
        r32 = r32.size();
        r0 = r32;
        if (r4 >= r0) goto L_0x05cc;
    L_0x05eb:
        r0 = r6.commands;
        r32 = r0;
        r0 = r32;
        r5 = r0.get(r4);
        r5 = (org.telegram.tgnet.TLRPC.TL_botCommand) r5;
        if (r5 == 0) goto L_0x064b;
    L_0x05f9:
        r0 = r5.command;
        r32 = r0;
        if (r32 == 0) goto L_0x064b;
    L_0x05ff:
        r0 = r5.command;
        r32 = r0;
        r0 = r32;
        r32 = r0.startsWith(r9);
        if (r32 == 0) goto L_0x064b;
    L_0x060b:
        r32 = new java.lang.StringBuilder;
        r32.<init>();
        r33 = "/";
        r32 = r32.append(r33);
        r0 = r5.command;
        r33 = r0;
        r32 = r32.append(r33);
        r32 = r32.toString();
        r0 = r20;
        r1 = r32;
        r0.add(r1);
        r0 = r5.description;
        r32 = r0;
        r0 = r22;
        r1 = r32;
        r0.add(r1);
        r32 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r6.user_id;
        r33 = r0;
        r33 = java.lang.Integer.valueOf(r33);
        r32 = r32.getUser(r33);
        r0 = r23;
        r1 = r32;
        r0.add(r1);
    L_0x064b:
        r4 = r4 + 1;
        goto L_0x05df;
    L_0x064e:
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultHashtags = r0;
        r32 = 0;
        r0 = r32;
        r1 = r34;
        r1.searchResultUsernames = r0;
        r0 = r20;
        r1 = r34;
        r1.searchResultCommands = r0;
        r0 = r22;
        r1 = r34;
        r1.searchResultCommandsHelp = r0;
        r0 = r23;
        r1 = r34;
        r1.searchResultCommandsUsers = r0;
        r34.notifyDataSetChanged();
        r0 = r34;
        r0 = r0.delegate;
        r33 = r0;
        r32 = r20.isEmpty();
        if (r32 != 0) goto L_0x068a;
    L_0x067f:
        r32 = 1;
    L_0x0681:
        r0 = r33;
        r1 = r32;
        r0.needChangePanelVisibility(r1);
        goto L_0x0028;
    L_0x068a:
        r32 = 0;
        goto L_0x0681;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList):void");
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public int getItemCount() {
        int i = 0;
        if (this.searchResultBotContext != null) {
            int size = this.searchResultBotContext.size();
            if (this.searchResultBotContextSwitch != null) {
                i = 1;
            }
            return i + size;
        } else if (this.searchResultUsernames != null) {
            return this.searchResultUsernames.size();
        } else {
            if (this.searchResultHashtags != null) {
                return this.searchResultHashtags.size();
            }
            if (this.searchResultCommands != null) {
                return this.searchResultCommands.size();
            }
            return 0;
        }
    }

    public int getItemViewType(int position) {
        if (this.searchResultBotContext == null) {
            return 0;
        }
        if (position != 0 || this.searchResultBotContextSwitch == null) {
            return 1;
        }
        return 2;
    }

    public Object getItem(int i) {
        if (this.searchResultBotContext != null) {
            if (this.searchResultBotContextSwitch != null) {
                if (i == 0) {
                    return this.searchResultBotContextSwitch;
                }
                i--;
            }
            if (i < 0 || i >= this.searchResultBotContext.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        } else if (this.searchResultUsernames != null) {
            if (i < 0 || i >= this.searchResultUsernames.size()) {
                return null;
            }
            return this.searchResultUsernames.get(i);
        } else if (this.searchResultHashtags != null) {
            if (i < 0 || i >= this.searchResultHashtags.size()) {
                return null;
            }
            return this.searchResultHashtags.get(i);
        } else if (this.searchResultCommands == null || i < 0 || i >= this.searchResultCommands.size()) {
            return null;
        } else {
            if (this.searchResultCommandsUsers == null || (this.botsCount == 1 && !(this.info instanceof TL_channelFull))) {
                return this.searchResultCommands.get(i);
            }
            if (this.searchResultCommandsUsers.get(i) != null) {
                String str = "%s@%s";
                Object[] objArr = new Object[2];
                objArr[0] = this.searchResultCommands.get(i);
                objArr[1] = this.searchResultCommandsUsers.get(i) != null ? ((User) this.searchResultCommandsUsers.get(i)).username : TtmlNode.ANONYMOUS_REGION_ID;
                return String.format(str, objArr);
            }
            return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
        }
    }

    public boolean isLongClickEnabled() {
        return (this.searchResultHashtags == null && this.searchResultCommands == null) ? false : true;
    }

    public boolean isBotCommands() {
        return this.searchResultCommands != null;
    }

    public boolean isBotContext() {
        return this.searchResultBotContext != null;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = new ContextLinkCell(this.mContext);
            ((ContextLinkCell) view).setDelegate(new C17586());
        } else if (viewType == 2) {
            view = new BotSwitchCell(this.mContext);
        } else {
            view = new MentionCell(this.mContext);
            ((MentionCell) view).setIsDarkTheme(this.isDarkTheme);
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean z = true;
        if (this.searchResultBotContext != null) {
            boolean hasTop = this.searchResultBotContextSwitch != null;
            if (holder.getItemViewType() != 2) {
                if (hasTop) {
                    position--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) holder.itemView;
                BotInlineResult botInlineResult = (BotInlineResult) this.searchResultBotContext.get(position);
                boolean z2 = this.contextMedia;
                boolean z3 = position != this.searchResultBotContext.size() + -1;
                if (!(hasTop && position == 0)) {
                    z = false;
                }
                contextLinkCell.setLink(botInlineResult, z2, z3, z);
            } else if (hasTop) {
                ((BotSwitchCell) holder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else if (this.searchResultUsernames != null) {
            ((MentionCell) holder.itemView).setUser((User) this.searchResultUsernames.get(position));
        } else if (this.searchResultHashtags != null) {
            ((MentionCell) holder.itemView).setText((String) this.searchResultHashtags.get(position));
        } else if (this.searchResultCommands != null) {
            ((MentionCell) holder.itemView).setBotCommand((String) this.searchResultCommands.get(position), (String) this.searchResultCommandsHelp.get(position), this.searchResultCommandsUsers != null ? (User) this.searchResultCommandsUsers.get(position) : null);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 2 || this.foundContextBot == null || !this.foundContextBot.bot_inline_geo) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            onLocationUnavailable();
        } else {
            this.locationProvider.start();
        }
    }
}
