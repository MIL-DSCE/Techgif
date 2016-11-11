package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Adapters.BaseSectionsAdapter;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.SectionsListView;
import org.telegram.ui.Components.WebFrameLayout;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class MediaActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int delete = 4;
    private static final int files_item = 2;
    private static final int forward = 3;
    private static final int links_item = 5;
    private static final int music_item = 6;
    private static final int shared_media_item = 1;
    private ArrayList<View> actionModeViews;
    private SharedDocumentsAdapter audioAdapter;
    private MediaSearchAdapter audioSearchAdapter;
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache;
    private int columnsCount;
    private long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private LinearLayout emptyView;
    protected ChatFull info;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private SectionsListView listView;
    private long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private ActionBarPopupWindowLayout popupLayout;
    private LinearLayout progressView;
    private boolean scrolling;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private HashMap<Integer, MessageObject>[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private int selectedMode;
    private SharedMediaData[] sharedMediaData;

    /* renamed from: org.telegram.ui.MediaActivity.3 */
    class C13233 implements OnClickListener {
        C13233() {
        }

        public void onClick(View view) {
            MediaActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.4 */
    class C13244 implements OnTouchListener {
        C13244() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.5 */
    class C13255 implements OnItemClickListener {
        C13255() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if ((MediaActivity.this.selectedMode == MediaActivity.shared_media_item || MediaActivity.this.selectedMode == MediaActivity.delete) && (view instanceof SharedDocumentCell)) {
                MediaActivity.this.onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0);
            } else if (MediaActivity.this.selectedMode == MediaActivity.forward && (view instanceof SharedLinkCell)) {
                MediaActivity.this.onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.6 */
    class C13266 implements OnScrollListener {
        C13266() {
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            boolean z = true;
            if (scrollState == MediaActivity.shared_media_item && MediaActivity.this.searching && MediaActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(MediaActivity.this.getParentActivity().getCurrentFocus());
            }
            MediaActivity mediaActivity = MediaActivity.this;
            if (scrollState == 0) {
                z = false;
            }
            mediaActivity.scrolling = z;
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if ((!MediaActivity.this.searching || !MediaActivity.this.searchWas) && visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading) {
                int type;
                if (MediaActivity.this.selectedMode == 0) {
                    type = 0;
                } else if (MediaActivity.this.selectedMode == MediaActivity.shared_media_item) {
                    type = MediaActivity.shared_media_item;
                } else if (MediaActivity.this.selectedMode == MediaActivity.files_item) {
                    type = MediaActivity.files_item;
                } else if (MediaActivity.this.selectedMode == MediaActivity.delete) {
                    type = MediaActivity.delete;
                } else {
                    type = MediaActivity.forward;
                }
                if (!MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[0]) {
                    MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(MediaActivity.this.dialog_id, 0, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[0], type, true, MediaActivity.this.classGuid);
                } else if (MediaActivity.this.mergeDialogId != 0 && !MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[MediaActivity.shared_media_item]) {
                    MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(MediaActivity.this.mergeDialogId, 0, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[MediaActivity.shared_media_item], type, true, MediaActivity.this.classGuid);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.7 */
    class C13277 implements OnItemLongClickListener {
        C13277() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
            if ((MediaActivity.this.selectedMode == MediaActivity.shared_media_item || MediaActivity.this.selectedMode == MediaActivity.delete) && (view instanceof SharedDocumentCell)) {
                return MediaActivity.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
            } else if (MediaActivity.this.selectedMode != MediaActivity.forward || !(view instanceof SharedLinkCell)) {
                return false;
            } else {
                return MediaActivity.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.8 */
    class C13288 implements OnTouchListener {
        C13288() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.9 */
    class C13299 implements OnPreDrawListener {
        C13299() {
        }

        public boolean onPreDraw() {
            MediaActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            MediaActivity.this.fixLayoutInternal();
            return true;
        }
    }

    private class SharedMediaData {
        private boolean[] endReached;
        private boolean loading;
        private int[] max_id;
        private ArrayList<MessageObject> messages;
        private HashMap<Integer, MessageObject>[] messagesDict;
        private HashMap<String, ArrayList<MessageObject>> sectionArrays;
        private ArrayList<String> sections;
        private int totalCount;

        private SharedMediaData() {
            this.messages = new ArrayList();
            HashMap[] hashMapArr = new HashMap[MediaActivity.files_item];
            hashMapArr[0] = new HashMap();
            hashMapArr[MediaActivity.shared_media_item] = new HashMap();
            this.messagesDict = hashMapArr;
            this.sections = new ArrayList();
            this.sectionArrays = new HashMap();
            this.endReached = new boolean[]{false, true};
            this.max_id = new int[]{0, 0};
        }

        public boolean addMessage(MessageObject messageObject, boolean isNew, boolean enc) {
            int loadIndex;
            if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                loadIndex = 0;
            } else {
                loadIndex = MediaActivity.shared_media_item;
            }
            if (this.messagesDict[loadIndex].containsKey(Integer.valueOf(messageObject.getId()))) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                messageObjects = new ArrayList();
                this.sectionArrays.put(messageObject.monthKey, messageObjects);
                if (isNew) {
                    this.sections.add(0, messageObject.monthKey);
                } else {
                    this.sections.add(messageObject.monthKey);
                }
            }
            if (isNew) {
                messageObjects.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                messageObjects.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[loadIndex].put(Integer.valueOf(messageObject.getId()), messageObject);
            if (enc) {
                this.max_id[loadIndex] = Math.max(messageObject.getId(), this.max_id[loadIndex]);
            } else if (messageObject.getId() > 0) {
                this.max_id[loadIndex] = Math.min(messageObject.getId(), this.max_id[loadIndex]);
            }
            return true;
        }

        public boolean deleteMessage(int mid, int loadIndex) {
            MessageObject messageObject = (MessageObject) this.messagesDict[loadIndex].get(Integer.valueOf(mid));
            if (messageObject == null) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                return false;
            }
            messageObjects.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[loadIndex].remove(Integer.valueOf(messageObject.getId()));
            if (messageObjects.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return true;
        }

        public void replaceMid(int oldMid, int newMid) {
            MessageObject obj = (MessageObject) this.messagesDict[0].get(Integer.valueOf(oldMid));
            if (obj != null) {
                this.messagesDict[0].remove(Integer.valueOf(oldMid));
                this.messagesDict[0].put(Integer.valueOf(newMid), obj);
                obj.messageOwner.id = newMid;
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.1 */
    class C18901 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.MediaActivity.1.1 */
        class C13221 implements DialogInterface.OnClickListener {
            C13221() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                for (int a = MediaActivity.shared_media_item; a >= 0; a--) {
                    MessageObject msg;
                    ArrayList<Integer> ids = new ArrayList(MediaActivity.this.selectedFiles[a].keySet());
                    ArrayList<Long> random_ids = null;
                    EncryptedChat currentEncryptedChat = null;
                    int channelId = 0;
                    if (!ids.isEmpty()) {
                        msg = (MessageObject) MediaActivity.this.selectedFiles[a].get(ids.get(0));
                        if (null == null && msg.messageOwner.to_id.channel_id != 0) {
                            channelId = msg.messageOwner.to_id.channel_id;
                        }
                    }
                    if (((int) MediaActivity.this.dialog_id) == 0) {
                        currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (MediaActivity.this.dialog_id >> 32)));
                    }
                    if (currentEncryptedChat != null) {
                        random_ids = new ArrayList();
                        for (Entry<Integer, MessageObject> entry : MediaActivity.this.selectedFiles[a].entrySet()) {
                            msg = (MessageObject) entry.getValue();
                            if (!(msg.messageOwner.random_id == 0 || msg.type == 10)) {
                                random_ids.add(Long.valueOf(msg.messageOwner.random_id));
                            }
                        }
                    }
                    MessagesController.getInstance().deleteMessages(ids, random_ids, currentEncryptedChat, channelId);
                    MediaActivity.this.selectedFiles[a].clear();
                }
                MediaActivity.this.actionBar.hideActionMode();
                MediaActivity.this.actionBar.closeSearchField();
                MediaActivity.this.cantDeleteMessagesCount = 0;
            }
        }

        /* renamed from: org.telegram.ui.MediaActivity.1.2 */
        class C18892 implements DialogsActivityDelegate {
            C18892() {
            }

            public void didSelectDialog(DialogsActivity fragment, long did, boolean param) {
                int lower_part = (int) did;
                if (lower_part != 0) {
                    Bundle args = new Bundle();
                    args.putBoolean("scrollToTopOnResume", true);
                    if (lower_part > 0) {
                        args.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args.putInt("chat_id", -lower_part);
                    }
                    if (MessagesController.checkCanOpenChat(args, fragment)) {
                        ArrayList<MessageObject> fmessages = new ArrayList();
                        for (int a = MediaActivity.shared_media_item; a >= 0; a--) {
                            ArrayList<Integer> ids = new ArrayList(MediaActivity.this.selectedFiles[a].keySet());
                            Collections.sort(ids);
                            Iterator i$ = ids.iterator();
                            while (i$.hasNext()) {
                                Integer id = (Integer) i$.next();
                                if (id.intValue() > 0) {
                                    fmessages.add(MediaActivity.this.selectedFiles[a].get(id));
                                }
                            }
                            MediaActivity.this.selectedFiles[a].clear();
                        }
                        MediaActivity.this.cantDeleteMessagesCount = 0;
                        MediaActivity.this.actionBar.hideActionMode();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        ChatActivity chatActivity = new ChatActivity(args);
                        MediaActivity.this.presentFragment(chatActivity, true);
                        chatActivity.showReplyPanel(true, null, fmessages, null, false, false);
                        if (!AndroidUtilities.isTablet()) {
                            MediaActivity.this.removeSelfFromStack();
                            return;
                        }
                        return;
                    }
                    return;
                }
                fragment.finishFragment();
            }
        }

        C18901() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    for (int a = MediaActivity.shared_media_item; a >= 0; a--) {
                        MediaActivity.this.selectedFiles[a].clear();
                    }
                    MediaActivity.this.cantDeleteMessagesCount = 0;
                    MediaActivity.this.actionBar.hideActionMode();
                    MediaActivity.this.listView.invalidateViews();
                    return;
                }
                MediaActivity.this.finishFragment();
            } else if (id == MediaActivity.shared_media_item) {
                if (MediaActivity.this.selectedMode != 0) {
                    MediaActivity.this.selectedMode = 0;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (id == MediaActivity.files_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.shared_media_item) {
                    MediaActivity.this.selectedMode = MediaActivity.shared_media_item;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (id == MediaActivity.links_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.forward) {
                    MediaActivity.this.selectedMode = MediaActivity.forward;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (id == MediaActivity.music_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.delete) {
                    MediaActivity.this.selectedMode = MediaActivity.delete;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (id == MediaActivity.delete) {
                if (MediaActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(MediaActivity.this.getParentActivity());
                    Object[] objArr = new Object[MediaActivity.shared_media_item];
                    objArr[0] = LocaleController.formatPluralString("items", MediaActivity.this.selectedFiles[0].size() + MediaActivity.this.selectedFiles[MediaActivity.shared_media_item].size());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0691R.string.AreYouSureDeleteMessages, objArr));
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C13221());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    MediaActivity.this.showDialog(builder.create());
                }
            } else if (id == MediaActivity.forward) {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", MediaActivity.shared_media_item);
                DialogsActivity fragment = new DialogsActivity(args);
                fragment.setDelegate(new C18892());
                MediaActivity.this.presentFragment(fragment);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity.2 */
    class C18912 extends ActionBarMenuItemSearchListener {
        C18912() {
        }

        public void onSearchExpand() {
            MediaActivity.this.dropDownContainer.setVisibility(8);
            MediaActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            MediaActivity.this.dropDownContainer.setVisibility(0);
            if (MediaActivity.this.selectedMode == MediaActivity.shared_media_item) {
                MediaActivity.this.documentsSearchAdapter.search(null);
            } else if (MediaActivity.this.selectedMode == MediaActivity.forward) {
                MediaActivity.this.linksSearchAdapter.search(null);
            } else if (MediaActivity.this.selectedMode == MediaActivity.delete) {
                MediaActivity.this.audioSearchAdapter.search(null);
            }
            MediaActivity.this.searching = false;
            MediaActivity.this.searchWas = false;
            MediaActivity.this.switchToCurrentSelectedMode();
        }

        public void onTextChanged(EditText editText) {
            String text = editText.getText().toString();
            if (text.length() != 0) {
                MediaActivity.this.searchWas = true;
                MediaActivity.this.switchToCurrentSelectedMode();
            }
            if (MediaActivity.this.selectedMode == MediaActivity.shared_media_item) {
                if (MediaActivity.this.documentsSearchAdapter != null) {
                    MediaActivity.this.documentsSearchAdapter.search(text);
                }
            } else if (MediaActivity.this.selectedMode == MediaActivity.forward) {
                if (MediaActivity.this.linksSearchAdapter != null) {
                    MediaActivity.this.linksSearchAdapter.search(text);
                }
            } else if (MediaActivity.this.selectedMode == MediaActivity.delete && MediaActivity.this.audioSearchAdapter != null) {
                MediaActivity.this.audioSearchAdapter.search(text);
            }
        }
    }

    public class MediaSearchAdapter extends BaseFragmentAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch;
        private int lastReqId;
        private Context mContext;
        private int reqId;
        private ArrayList<MessageObject> searchResult;
        private Timer searchTimer;

        /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.2 */
        class C13312 extends TimerTask {
            final /* synthetic */ String val$query;

            C13312(String str) {
                this.val$query = str;
            }

            public void run() {
                try {
                    MediaSearchAdapter.this.searchTimer.cancel();
                    MediaSearchAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                MediaSearchAdapter.this.processSearch(this.val$query);
            }
        }

        /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.3 */
        class C13333 implements Runnable {
            final /* synthetic */ String val$query;

            /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.3.1 */
            class C13321 implements Runnable {
                final /* synthetic */ ArrayList val$copy;

                C13321(ArrayList arrayList) {
                    this.val$copy = arrayList;
                }

                public void run() {
                    String search1 = C13333.this.val$query.trim().toLowerCase();
                    if (search1.length() == 0) {
                        MediaSearchAdapter.this.updateSearchResults(new ArrayList());
                        return;
                    }
                    int i;
                    String search2 = LocaleController.getInstance().getTranslitString(search1);
                    if (search1.equals(search2) || search2.length() == 0) {
                        search2 = null;
                    }
                    if (search2 != null) {
                        i = MediaActivity.shared_media_item;
                    } else {
                        i = 0;
                    }
                    String[] search = new String[(i + MediaActivity.shared_media_item)];
                    search[0] = search1;
                    if (search2 != null) {
                        search[MediaSearchAdapter.this.currentType] = search2;
                    }
                    ArrayList<MessageObject> resultArray = new ArrayList();
                    for (int a = 0; a < this.val$copy.size(); a += MediaActivity.shared_media_item) {
                        MessageObject messageObject = (MessageObject) this.val$copy.get(a);
                        for (int b = 0; b < search.length; b += MediaActivity.shared_media_item) {
                            String q = search[b];
                            String name = messageObject.getDocumentName();
                            if (!(name == null || name.length() == 0)) {
                                if (!name.toLowerCase().contains(q)) {
                                    if (MediaSearchAdapter.this.currentType == MediaActivity.delete) {
                                        Document document;
                                        if (messageObject.type == 0) {
                                            document = messageObject.messageOwner.media.webpage.document;
                                        } else {
                                            document = messageObject.messageOwner.media.document;
                                        }
                                        boolean ok = false;
                                        int c = 0;
                                        while (c < document.attributes.size()) {
                                            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(c);
                                            if (attribute instanceof TL_documentAttributeAudio) {
                                                if (attribute.performer != null) {
                                                    ok = attribute.performer.toLowerCase().contains(q);
                                                }
                                                if (!(ok || attribute.title == null)) {
                                                    ok = attribute.title.toLowerCase().contains(q);
                                                }
                                                if (ok) {
                                                    resultArray.add(messageObject);
                                                    break;
                                                }
                                            } else {
                                                c += MediaActivity.shared_media_item;
                                            }
                                        }
                                        if (ok) {
                                            resultArray.add(messageObject);
                                            break;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    resultArray.add(messageObject);
                                    break;
                                }
                            }
                        }
                    }
                    MediaSearchAdapter.this.updateSearchResults(resultArray);
                }
            }

            C13333(String str) {
                this.val$query = str;
            }

            public void run() {
                if (!MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.isEmpty()) {
                    if (MediaSearchAdapter.this.currentType == MediaActivity.shared_media_item || MediaSearchAdapter.this.currentType == MediaActivity.delete) {
                        MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.get(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.size() - 1);
                        MediaSearchAdapter.this.queryServerSearch(this.val$query, messageObject.getId(), messageObject.getDialogId());
                    } else if (MediaSearchAdapter.this.currentType == MediaActivity.forward) {
                        MediaSearchAdapter.this.queryServerSearch(this.val$query, 0, MediaActivity.this.dialog_id);
                    }
                }
                if (MediaSearchAdapter.this.currentType == MediaActivity.shared_media_item || MediaSearchAdapter.this.currentType == MediaActivity.delete) {
                    ArrayList<MessageObject> copy = new ArrayList();
                    copy.addAll(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages);
                    Utilities.searchQueue.postRunnable(new C13321(copy));
                }
            }
        }

        /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.4 */
        class C13344 implements Runnable {
            final /* synthetic */ ArrayList val$documents;

            C13344(ArrayList arrayList) {
                this.val$documents = arrayList;
            }

            public void run() {
                MediaSearchAdapter.this.searchResult = this.val$documents;
                MediaSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.1 */
        class C18921 implements RequestDelegate {
            final /* synthetic */ int val$currentReqId;
            final /* synthetic */ int val$max_id;

            /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.1.1 */
            class C13301 implements Runnable {
                final /* synthetic */ ArrayList val$messageObjects;

                C13301(ArrayList arrayList) {
                    this.val$messageObjects = arrayList;
                }

                public void run() {
                    if (C18921.this.val$currentReqId == MediaSearchAdapter.this.lastReqId) {
                        MediaSearchAdapter.this.globalSearch = this.val$messageObjects;
                        MediaSearchAdapter.this.notifyDataSetChanged();
                    }
                    MediaSearchAdapter.this.reqId = 0;
                }
            }

            C18921(int i, int i2) {
                this.val$max_id = i;
                this.val$currentReqId = i2;
            }

            public void run(TLObject response, TL_error error) {
                ArrayList<MessageObject> messageObjects = new ArrayList();
                if (error == null) {
                    messages_Messages res = (messages_Messages) response;
                    for (int a = 0; a < res.messages.size(); a += MediaActivity.shared_media_item) {
                        Message message = (Message) res.messages.get(a);
                        if (this.val$max_id == 0 || message.id <= this.val$max_id) {
                            messageObjects.add(new MessageObject(message, null, false));
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C13301(messageObjects));
            }
        }

        /* renamed from: org.telegram.ui.MediaActivity.MediaSearchAdapter.5 */
        class C18935 implements SharedLinkCellDelegate {
            C18935() {
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return !MediaActivity.this.actionBar.isActionModeShowed();
            }
        }

        public MediaSearchAdapter(Context context, int type) {
            this.searchResult = new ArrayList();
            this.globalSearch = new ArrayList();
            this.reqId = 0;
            this.mContext = context;
            this.currentType = type;
        }

        public void queryServerSearch(String query, int max_id, long did) {
            int uid = (int) did;
            if (uid != 0) {
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
                if (query == null || query.length() == 0) {
                    this.globalSearch.clear();
                    this.lastReqId = 0;
                    notifyDataSetChanged();
                    return;
                }
                TL_messages_search req = new TL_messages_search();
                req.offset = 0;
                req.limit = 50;
                req.max_id = max_id;
                if (this.currentType == MediaActivity.shared_media_item) {
                    req.filter = new TL_inputMessagesFilterDocument();
                } else if (this.currentType == MediaActivity.forward) {
                    req.filter = new TL_inputMessagesFilterUrl();
                } else if (this.currentType == MediaActivity.delete) {
                    req.filter = new TL_inputMessagesFilterMusic();
                }
                req.f37q = query;
                req.peer = MessagesController.getInputPeer(uid);
                if (req.peer != null) {
                    int currentReqId = this.lastReqId + MediaActivity.shared_media_item;
                    this.lastReqId = currentReqId;
                    this.reqId = ConnectionsManager.getInstance().sendRequest(req, new C18921(max_id, currentReqId), MediaActivity.files_item);
                    ConnectionsManager.getInstance().bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                }
            }
        }

        public void search(String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (query == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new C13312(query), 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new C13333(query));
        }

        private void updateSearchResults(ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new C13344(documents));
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i != this.searchResult.size() + this.globalSearch.size();
        }

        public int getCount() {
            int count = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if (globalCount != 0) {
                return count + globalCount;
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

        public MessageObject getItem(int i) {
            if (i < this.searchResult.size()) {
                return (MessageObject) this.searchResult.get(i);
            }
            return (MessageObject) this.globalSearch.get(i - this.searchResult.size());
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            MessageObject messageObject;
            boolean z2;
            HashMap[] access$600;
            int i2;
            if (this.currentType == MediaActivity.shared_media_item || this.currentType == MediaActivity.delete) {
                if (view == null) {
                    view = new SharedDocumentCell(this.mContext);
                }
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                messageObject = getItem(i);
                if (i != getCount() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                sharedDocumentCell.setDocument(messageObject, z2);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    access$600 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i2 = 0;
                    } else {
                        i2 = MediaActivity.shared_media_item;
                    }
                    z2 = access$600[i2].containsKey(Integer.valueOf(messageObject.getId()));
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedDocumentCell.setChecked(z2, z);
                } else {
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedDocumentCell.setChecked(false, z);
                }
            } else if (this.currentType == MediaActivity.forward) {
                if (view == null) {
                    view = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) view).setDelegate(new C18935());
                }
                SharedLinkCell sharedLinkCell = (SharedLinkCell) view;
                messageObject = getItem(i);
                if (i != getCount() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                sharedLinkCell.setLink(messageObject, z2);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    access$600 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i2 = 0;
                    } else {
                        i2 = MediaActivity.shared_media_item;
                    }
                    z2 = access$600[i2].containsKey(Integer.valueOf(messageObject.getId()));
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedLinkCell.setChecked(z2, z);
                } else {
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedLinkCell.setChecked(false, z);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return MediaActivity.shared_media_item;
        }

        public boolean isEmpty() {
            return this.searchResult.isEmpty() && this.globalSearch.isEmpty();
        }
    }

    private class SharedDocumentsAdapter extends BaseSectionsAdapter {
        private int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isRowEnabled(int section, int row) {
            return row != 0;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[this.currentType].sections.size();
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.isEmpty() || (MediaActivity.this.sharedMediaData[this.currentType].endReached[0] && MediaActivity.this.sharedMediaData[this.currentType].endReached[MediaActivity.shared_media_item])) {
                i = 0;
            }
            return i + size;
        }

        public int getCountForSection(int section) {
            if (section < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(section))).size() + MediaActivity.shared_media_item;
            }
            return MediaActivity.shared_media_item;
        }

        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new GreySectionCell(this.mContext);
            }
            if (section < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GreySectionCell) convertView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(section))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return convertView;
        }

        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if (section < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(section));
                if (position == 0) {
                    if (convertView == null) {
                        convertView = new GreySectionCell(this.mContext);
                    }
                    ((GreySectionCell) convertView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                    return convertView;
                }
                if (convertView == null) {
                    convertView = new SharedDocumentCell(this.mContext);
                }
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) convertView;
                MessageObject messageObject = (MessageObject) messageObjects.get(position - 1);
                boolean z = position != messageObjects.size() || (section == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading);
                sharedDocumentCell.setDocument(messageObject, z);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    sharedDocumentCell.setChecked(MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : MediaActivity.shared_media_item].containsKey(Integer.valueOf(messageObject.getId())), !MediaActivity.this.scrolling);
                    return convertView;
                }
                sharedDocumentCell.setChecked(false, !MediaActivity.this.scrolling);
                return convertView;
            } else if (convertView == null) {
                return new LoadingCell(this.mContext);
            } else {
                return convertView;
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return MediaActivity.files_item;
            }
            if (position == 0) {
                return 0;
            }
            return MediaActivity.shared_media_item;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }
    }

    private class SharedLinksAdapter extends BaseSectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity.SharedLinksAdapter.1 */
        class C18941 implements SharedLinkCellDelegate {
            C18941() {
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return !MediaActivity.this.actionBar.isActionModeShowed();
            }
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isRowEnabled(int section, int row) {
            return row != 0;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size();
            if (MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.isEmpty() || (MediaActivity.this.sharedMediaData[MediaActivity.forward].endReached[0] && MediaActivity.this.sharedMediaData[MediaActivity.forward].endReached[MediaActivity.shared_media_item])) {
                i = 0;
            }
            return i + size;
        }

        public int getCountForSection(int section) {
            if (section < MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size()) {
                return ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.forward].sectionArrays.get(MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.get(section))).size() + MediaActivity.shared_media_item;
            }
            return MediaActivity.shared_media_item;
        }

        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new GreySectionCell(this.mContext);
            }
            if (section < MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size()) {
                ((GreySectionCell) convertView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.forward].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.get(section))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return convertView;
        }

        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if (section < MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size()) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.forward].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.get(section));
                if (position == 0) {
                    if (convertView == null) {
                        convertView = new GreySectionCell(this.mContext);
                    }
                    ((GreySectionCell) convertView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                    return convertView;
                }
                if (convertView == null) {
                    convertView = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) convertView).setDelegate(new C18941());
                }
                SharedLinkCell sharedLinkCell = (SharedLinkCell) convertView;
                MessageObject messageObject = (MessageObject) messageObjects.get(position - 1);
                boolean z = position != messageObjects.size() || (section == MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size() - 1 && MediaActivity.this.sharedMediaData[MediaActivity.forward].loading);
                sharedLinkCell.setLink(messageObject, z);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    sharedLinkCell.setChecked(MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : MediaActivity.shared_media_item].containsKey(Integer.valueOf(messageObject.getId())), !MediaActivity.this.scrolling);
                    return convertView;
                }
                sharedLinkCell.setChecked(false, !MediaActivity.this.scrolling);
                return convertView;
            } else if (convertView == null) {
                return new LoadingCell(this.mContext);
            } else {
                return convertView;
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size()) {
                return MediaActivity.files_item;
            }
            if (position == 0) {
                return 0;
            }
            return MediaActivity.shared_media_item;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }
    }

    private class SharedPhotoVideoAdapter extends BaseSectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity.SharedPhotoVideoAdapter.1 */
        class C18951 implements SharedPhotoVideoCellDelegate {
            C18951() {
            }

            public void didClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                MediaActivity.this.onItemClick(index, cell, messageObject, a);
            }

            public boolean didLongClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                return MediaActivity.this.onItemLongClick(messageObject, cell, a);
            }
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isRowEnabled(int section, int row) {
            return false;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[0].sections.size();
            if (MediaActivity.this.sharedMediaData[0].sections.isEmpty() || (MediaActivity.this.sharedMediaData[0].endReached[0] && MediaActivity.this.sharedMediaData[0].endReached[MediaActivity.shared_media_item])) {
                i = 0;
            }
            return i + size;
        }

        public int getCountForSection(int section) {
            if (section < MediaActivity.this.sharedMediaData[0].sections.size()) {
                return ((int) Math.ceil((double) (((float) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get(MediaActivity.this.sharedMediaData[0].sections.get(section))).size()) / ((float) MediaActivity.this.columnsCount)))) + MediaActivity.shared_media_item;
            }
            return MediaActivity.shared_media_item;
        }

        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new SharedMediaSectionCell(this.mContext);
                convertView.setBackgroundColor(-1);
            }
            if (section < MediaActivity.this.sharedMediaData[0].sections.size()) {
                ((SharedMediaSectionCell) convertView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(section))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return convertView;
        }

        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if (section < MediaActivity.this.sharedMediaData[0].sections.size()) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(section));
                if (position == 0) {
                    if (convertView == null) {
                        convertView = new SharedMediaSectionCell(this.mContext);
                    }
                    ((SharedMediaSectionCell) convertView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                    return convertView;
                }
                SharedPhotoVideoCell cell;
                if (convertView == null) {
                    if (MediaActivity.this.cellCache.isEmpty()) {
                        convertView = new SharedPhotoVideoCell(this.mContext);
                    } else {
                        convertView = (View) MediaActivity.this.cellCache.get(0);
                        MediaActivity.this.cellCache.remove(0);
                    }
                    cell = (SharedPhotoVideoCell) convertView;
                    cell.setDelegate(new C18951());
                } else {
                    cell = (SharedPhotoVideoCell) convertView;
                }
                cell.setItemsCount(MediaActivity.this.columnsCount);
                for (int a = 0; a < MediaActivity.this.columnsCount; a += MediaActivity.shared_media_item) {
                    int index = ((position - 1) * MediaActivity.this.columnsCount) + a;
                    if (index < messageObjects.size()) {
                        MessageObject messageObject = (MessageObject) messageObjects.get(index);
                        cell.setIsFirst(position == MediaActivity.shared_media_item);
                        cell.setItem(a, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            int i;
                            boolean z;
                            HashMap[] access$600 = MediaActivity.this.selectedFiles;
                            if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                                i = 0;
                            } else {
                                i = MediaActivity.shared_media_item;
                            }
                            boolean containsKey = access$600[i].containsKey(Integer.valueOf(messageObject.getId()));
                            if (MediaActivity.this.scrolling) {
                                z = false;
                            } else {
                                z = true;
                            }
                            cell.setChecked(a, containsKey, z);
                        } else {
                            cell.setChecked(a, false, !MediaActivity.this.scrolling);
                        }
                    } else {
                        cell.setItem(a, index, null);
                    }
                }
                cell.requestLayout();
                return convertView;
            } else if (convertView == null) {
                return new LoadingCell(this.mContext);
            } else {
                return convertView;
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= MediaActivity.this.sharedMediaData[0].sections.size()) {
                return MediaActivity.files_item;
            }
            if (position == 0) {
                return 0;
            }
            return MediaActivity.shared_media_item;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }
    }

    public MediaActivity(Bundle args) {
        super(args);
        this.cellCache = new ArrayList(music_item);
        HashMap[] hashMapArr = new HashMap[files_item];
        hashMapArr[0] = new HashMap();
        hashMapArr[shared_media_item] = new HashMap();
        this.selectedFiles = hashMapArr;
        this.actionModeViews = new ArrayList();
        this.info = null;
        this.columnsCount = delete;
        this.sharedMediaData = new SharedMediaData[links_item];
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.dialog_id = getArguments().getLong("dialog_id", 0);
        for (int a = 0; a < this.sharedMediaData.length; a += shared_media_item) {
            this.sharedMediaData[a] = new SharedMediaData();
            this.sharedMediaData[a].max_id[0] = ((int) this.dialog_id) == 0 ? LinearLayoutManager.INVALID_OFFSET : ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[a].max_id[shared_media_item] = this.info.migrated_from_max_id;
                this.sharedMediaData[a].endReached[shared_media_item] = false;
            }
        }
        this.sharedMediaData[0].loading = true;
        SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, 0, true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
    }

    public View createView(Context context) {
        int a;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setTitle(TtmlNode.ANONYMOUS_REGION_ID);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C18901());
        for (a = shared_media_item; a >= 0; a--) {
            this.selectedFiles[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        ActionBarMenu menu = this.actionBar.createMenu();
        this.searchItem = menu.addItem(0, (int) C0691R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18912());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0691R.string.Search));
        this.searchItem.setVisibility(8);
        this.dropDownContainer = new ActionBarMenuItem(context, menu, 0);
        this.dropDownContainer.setSubMenuOpenSide(shared_media_item);
        this.dropDownContainer.addSubItem(shared_media_item, LocaleController.getString("SharedMediaTitle", C0691R.string.SharedMediaTitle), 0);
        this.dropDownContainer.addSubItem(files_item, LocaleController.getString("DocumentsTitle", C0691R.string.DocumentsTitle), 0);
        if (((int) this.dialog_id) != 0) {
            this.dropDownContainer.addSubItem(links_item, LocaleController.getString("LinksTitle", C0691R.string.LinksTitle), 0);
            this.dropDownContainer.addSubItem(music_item, LocaleController.getString("AudioTitle", C0691R.string.AudioTitle), 0);
        } else {
            EncryptedChat currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            if (currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 46) {
                this.dropDownContainer.addSubItem(music_item, LocaleController.getString("AudioTitle", C0691R.string.AudioTitle), 0);
            }
        }
        this.actionBar.addView(this.dropDownContainer, 0, LayoutHelper.createFrame(-2, GroundOverlayOptions.NO_DIMENSION, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.dropDownContainer.setOnClickListener(new C13233());
        this.dropDown = new TextView(context);
        this.dropDown.setGravity(forward);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(shared_media_item);
        this.dropDown.setMaxLines(shared_media_item);
        this.dropDown.setEllipsize(TruncateAt.END);
        this.dropDown.setTextColor(-1);
        this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0691R.drawable.ic_arrow_drop_down, 0);
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(-9211021);
        this.selectedMessagesCountTextView.setOnTouchListener(new C13244());
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, (float) TouchHelperCallback.ALPHA_FULL, 65, 0, 0, 0));
        if (((int) this.dialog_id) != 0) {
            this.actionModeViews.add(actionMode.addItem(forward, C0691R.drawable.ic_ab_fwd_forward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        }
        this.actionModeViews.add(actionMode.addItem(delete, C0691R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context);
        this.documentsAdapter = new SharedDocumentsAdapter(context, shared_media_item);
        this.audioAdapter = new SharedDocumentsAdapter(context, delete);
        this.documentsSearchAdapter = new MediaSearchAdapter(context, shared_media_item);
        this.audioSearchAdapter = new MediaSearchAdapter(context, delete);
        this.linksSearchAdapter = new MediaSearchAdapter(context, forward);
        this.linksAdapter = new SharedLinksAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.listView = new SectionsListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setDrawSelectorOnTop(true);
        this.listView.setClipToPadding(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.listView.setOnItemClickListener(new C13255());
        this.listView.setOnScrollListener(new C13266());
        this.listView.setOnItemLongClickListener(new C13277());
        for (a = 0; a < music_item; a += shared_media_item) {
            this.cellCache.add(new SharedPhotoVideoCell(context));
        }
        this.emptyView = new LinearLayout(context);
        this.emptyView.setOrientation(shared_media_item);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setBackgroundColor(-986896);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.emptyView.setOnTouchListener(new C13288());
        this.emptyImageView = new ImageView(context);
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-7697782);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setTextSize(shared_media_item, 17.0f);
        this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        this.emptyView.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        this.progressView = new LinearLayout(context);
        this.progressView.setGravity(17);
        this.progressView.setOrientation(shared_media_item);
        this.progressView.setVisibility(8);
        this.progressView.setBackgroundColor(-986896);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.progressView.addView(new ProgressBar(context), LayoutHelper.createLinear(-2, -2));
        switchToCurrentSelectedMode();
        if (!AndroidUtilities.isTablet()) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        int type;
        ArrayList<MessageObject> arr;
        boolean enc;
        int loadIndex;
        ActionBarMenuItem actionBarMenuItem;
        int i;
        if (id == NotificationCenter.mediaDidLoaded) {
            long uid = ((Long) args[0]).longValue();
            if (((Integer) args[forward]).intValue() == this.classGuid) {
                type = ((Integer) args[delete]).intValue();
                this.sharedMediaData[type].loading = false;
                this.sharedMediaData[type].totalCount = ((Integer) args[shared_media_item]).intValue();
                arr = args[files_item];
                enc = ((int) this.dialog_id) == 0;
                loadIndex = uid == this.dialog_id ? 0 : shared_media_item;
                for (int a = 0; a < arr.size(); a += shared_media_item) {
                    this.sharedMediaData[type].addMessage((MessageObject) arr.get(a), false, enc);
                }
                this.sharedMediaData[type].endReached[loadIndex] = ((Boolean) args[links_item]).booleanValue();
                if (loadIndex == 0 && this.sharedMediaData[this.selectedMode].messages.isEmpty() && this.mergeDialogId != 0) {
                    this.sharedMediaData[this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(this.mergeDialogId, 0, 50, this.sharedMediaData[this.selectedMode].max_id[shared_media_item], type, true, this.classGuid);
                }
                if (!this.sharedMediaData[this.selectedMode].loading) {
                    if (this.progressView != null) {
                        this.progressView.setVisibility(8);
                    }
                    if (this.selectedMode == type && this.listView != null && this.listView.getEmptyView() == null) {
                        this.listView.setEmptyView(this.emptyView);
                    }
                }
                this.scrolling = true;
                if (this.selectedMode == 0 && type == 0) {
                    if (this.photoVideoAdapter != null) {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == shared_media_item && type == shared_media_item) {
                    if (this.documentsAdapter != null) {
                        this.documentsAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == forward && type == forward) {
                    if (this.linksAdapter != null) {
                        this.linksAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == delete && type == delete && this.audioAdapter != null) {
                    this.audioAdapter.notifyDataSetChanged();
                }
                if (this.selectedMode == shared_media_item || this.selectedMode == forward || this.selectedMode == delete) {
                    actionBarMenuItem = this.searchItem;
                    if (this.sharedMediaData[this.selectedMode].messages.isEmpty() || this.searching) {
                        i = 8;
                    } else {
                        i = 0;
                    }
                    actionBarMenuItem.setVisibility(i);
                }
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            Chat currentChat = null;
            if (((int) this.dialog_id) < 0) {
                currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
            }
            int channelId = ((Integer) args[shared_media_item]).intValue();
            loadIndex = 0;
            if (ChatObject.isChannel(currentChat)) {
                if (channelId == 0 && this.mergeDialogId != 0) {
                    loadIndex = shared_media_item;
                } else if (channelId == currentChat.id) {
                    loadIndex = 0;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            updated = false;
            r18 = args[0].iterator();
            while (r18.hasNext()) {
                Integer ids = (Integer) r18.next();
                arr$ = this.sharedMediaData;
                len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$ += shared_media_item) {
                    if (arr$[i$].deleteMessage(ids.intValue(), loadIndex)) {
                        updated = true;
                    }
                }
            }
            if (updated) {
                this.scrolling = true;
                if (this.photoVideoAdapter != null) {
                    this.photoVideoAdapter.notifyDataSetChanged();
                }
                if (this.documentsAdapter != null) {
                    this.documentsAdapter.notifyDataSetChanged();
                }
                if (this.linksAdapter != null) {
                    this.linksAdapter.notifyDataSetChanged();
                }
                if (this.audioAdapter != null) {
                    this.audioAdapter.notifyDataSetChanged();
                }
                if (this.selectedMode == shared_media_item || this.selectedMode == forward || this.selectedMode == delete) {
                    actionBarMenuItem = this.searchItem;
                    i = (this.sharedMediaData[this.selectedMode].messages.isEmpty() || this.searching) ? 8 : 0;
                    actionBarMenuItem.setVisibility(i);
                }
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                arr = (ArrayList) args[shared_media_item];
                enc = ((int) this.dialog_id) == 0;
                updated = false;
                r18 = arr.iterator();
                while (r18.hasNext()) {
                    MessageObject obj = (MessageObject) r18.next();
                    if (obj.messageOwner.media != null) {
                        type = SharedMediaQuery.getMediaType(obj.messageOwner);
                        if (type == -1) {
                            return;
                        }
                        if (this.sharedMediaData[type].addMessage(obj, true, enc)) {
                            updated = true;
                        }
                    }
                }
                if (updated) {
                    this.scrolling = true;
                    if (this.photoVideoAdapter != null) {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (this.documentsAdapter != null) {
                        this.documentsAdapter.notifyDataSetChanged();
                    }
                    if (this.linksAdapter != null) {
                        this.linksAdapter.notifyDataSetChanged();
                    }
                    if (this.audioAdapter != null) {
                        this.audioAdapter.notifyDataSetChanged();
                    }
                    if (this.selectedMode == shared_media_item || this.selectedMode == forward || this.selectedMode == delete) {
                        actionBarMenuItem = this.searchItem;
                        i = (this.sharedMediaData[this.selectedMode].messages.isEmpty() || this.searching) ? 8 : 0;
                        actionBarMenuItem.setVisibility(i);
                    }
                }
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            Integer newMsgId = args[shared_media_item];
            arr$ = this.sharedMediaData;
            len$ = arr$.length;
            for (int i$2 = 0; i$2 < len$; i$2 += shared_media_item) {
                arr$[i$2].replaceMid(msgId.intValue(), newMsgId.intValue());
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.dropDownContainer != null) {
            this.dropDownContainer.closeSubMenu();
        }
    }

    public void onResume() {
        super.onResume();
        this.scrolling = true;
        if (this.photoVideoAdapter != null) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
        if (this.documentsAdapter != null) {
            this.documentsAdapter.notifyDataSetChanged();
        }
        if (this.linksAdapter != null) {
            this.linksAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C13299());
        }
    }

    public void updatePhotoAtIndex(int index) {
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        if (messageObject == null || this.listView == null || this.selectedMode != 0) {
            return null;
        }
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a += shared_media_item) {
            View view = this.listView.getChildAt(a);
            if (view instanceof SharedPhotoVideoCell) {
                SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                int i = 0;
                while (i < music_item) {
                    MessageObject message = cell.getMessageObject(i);
                    if (message == null) {
                        break;
                        continue;
                    } else {
                        BackupImageView imageView = cell.getImageView(i);
                        if (message.getId() == messageObject.getId()) {
                            int[] coords = new int[files_item];
                            imageView.getLocationInWindow(coords);
                            PlaceProviderObject object = new PlaceProviderObject();
                            object.viewX = coords[0];
                            object.viewY = coords[shared_media_item] - AndroidUtilities.statusBarHeight;
                            object.parentView = this.listView;
                            object.imageReceiver = imageView.getImageReceiver();
                            object.thumb = object.imageReceiver.getBitmap();
                            object.parentView.getLocationInWindow(coords);
                            object.clipTopAddition = AndroidUtilities.dp(40.0f);
                            return object;
                        }
                        i += shared_media_item;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
    }

    public void willHidePhotoViewer() {
    }

    public boolean isPhotoChecked(int index) {
        return false;
    }

    public void setPhotoChecked(int index) {
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public void sendButtonPressed(int index) {
    }

    public int getSelectedCount() {
        return 0;
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        if (this.info != null && this.info.migrated_from_chat_id != 0) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
    }

    private void switchToCurrentSelectedMode() {
        if (this.searching && this.searchWas) {
            if (this.listView != null) {
                if (this.selectedMode == shared_media_item) {
                    this.listView.setAdapter(this.documentsSearchAdapter);
                    this.documentsSearchAdapter.notifyDataSetChanged();
                } else if (this.selectedMode == forward) {
                    this.listView.setAdapter(this.linksSearchAdapter);
                    this.linksSearchAdapter.notifyDataSetChanged();
                } else if (this.selectedMode == delete) {
                    this.listView.setAdapter(this.audioSearchAdapter);
                    this.audioSearchAdapter.notifyDataSetChanged();
                }
            }
            if (this.emptyTextView != null) {
                this.emptyTextView.setText(LocaleController.getString("NoResult", C0691R.string.NoResult));
                this.emptyTextView.setTextSize(shared_media_item, 20.0f);
                this.emptyImageView.setVisibility(8);
                return;
            }
            return;
        }
        this.emptyTextView.setTextSize(shared_media_item, 17.0f);
        this.emptyImageView.setVisibility(0);
        if (this.selectedMode == 0) {
            this.listView.setAdapter(this.photoVideoAdapter);
            this.dropDown.setText(LocaleController.getString("SharedMediaTitle", C0691R.string.SharedMediaTitle));
            this.emptyImageView.setImageResource(C0691R.drawable.tip1);
            if (((int) this.dialog_id) == 0) {
                this.emptyTextView.setText(LocaleController.getString("NoMediaSecret", C0691R.string.NoMediaSecret));
            } else {
                this.emptyTextView.setText(LocaleController.getString("NoMedia", C0691R.string.NoMedia));
            }
            this.searchItem.setVisibility(8);
            if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(null);
                this.emptyView.setVisibility(8);
            } else {
                this.progressView.setVisibility(8);
                this.listView.setEmptyView(this.emptyView);
            }
            this.listView.setVisibility(0);
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
        } else if (this.selectedMode == shared_media_item || this.selectedMode == delete) {
            if (this.selectedMode == shared_media_item) {
                this.listView.setAdapter(this.documentsAdapter);
                this.dropDown.setText(LocaleController.getString("DocumentsTitle", C0691R.string.DocumentsTitle));
                this.emptyImageView.setImageResource(C0691R.drawable.tip2);
                if (((int) this.dialog_id) == 0) {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", C0691R.string.NoSharedFilesSecret));
                } else {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedFiles", C0691R.string.NoSharedFiles));
                }
            } else if (this.selectedMode == delete) {
                this.listView.setAdapter(this.audioAdapter);
                this.dropDown.setText(LocaleController.getString("AudioTitle", C0691R.string.AudioTitle));
                this.emptyImageView.setImageResource(C0691R.drawable.tip4);
                if (((int) this.dialog_id) == 0) {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", C0691R.string.NoSharedAudioSecret));
                } else {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedAudio", C0691R.string.NoSharedAudio));
                }
            }
            r1 = this.searchItem;
            if (this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                r0 = 8;
            } else {
                r0 = 0;
            }
            r1.setVisibility(r0);
            if (!(this.sharedMediaData[this.selectedMode].loading || this.sharedMediaData[this.selectedMode].endReached[0] || !this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
                int i;
                this.sharedMediaData[this.selectedMode].loading = true;
                long j = this.dialog_id;
                if (this.selectedMode == shared_media_item) {
                    i = shared_media_item;
                } else {
                    i = delete;
                }
                SharedMediaQuery.loadMedia(j, 0, 50, 0, i, true, this.classGuid);
            }
            this.listView.setVisibility(0);
            if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(null);
                this.emptyView.setVisibility(8);
            } else {
                this.progressView.setVisibility(8);
                this.listView.setEmptyView(this.emptyView);
            }
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
        } else if (this.selectedMode == forward) {
            this.listView.setAdapter(this.linksAdapter);
            this.dropDown.setText(LocaleController.getString("LinksTitle", C0691R.string.LinksTitle));
            this.emptyImageView.setImageResource(C0691R.drawable.tip3);
            if (((int) this.dialog_id) == 0) {
                this.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", C0691R.string.NoSharedLinksSecret));
            } else {
                this.emptyTextView.setText(LocaleController.getString("NoSharedLinks", C0691R.string.NoSharedLinks));
            }
            r1 = this.searchItem;
            if (this.sharedMediaData[forward].messages.isEmpty()) {
                r0 = 8;
            } else {
                r0 = 0;
            }
            r1.setVisibility(r0);
            if (!(this.sharedMediaData[this.selectedMode].loading || this.sharedMediaData[this.selectedMode].endReached[0] || !this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
                this.sharedMediaData[this.selectedMode].loading = true;
                SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, forward, true, this.classGuid);
            }
            this.listView.setVisibility(0);
            if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(null);
                this.emptyView.setVisibility(8);
            } else {
                this.progressView.setVisibility(8);
                this.listView.setEmptyView(this.emptyView);
            }
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
        }
    }

    private boolean onItemLongClick(MessageObject item, View view, int a) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        int i;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        HashMap[] hashMapArr = this.selectedFiles;
        if (item.getDialogId() == this.dialog_id) {
            i = 0;
        } else {
            i = shared_media_item;
        }
        hashMapArr[i].put(Integer.valueOf(item.getId()), item);
        if (!item.canDeleteMessage(null)) {
            this.cantDeleteMessagesCount += shared_media_item;
        }
        this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        this.selectedMessagesCountTextView.setNumber(shared_media_item, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList();
        for (int i2 = 0; i2 < this.actionModeViews.size(); i2 += shared_media_item) {
            View view2 = (View) this.actionModeViews.get(i2);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, TouchHelperCallback.ALPHA_FULL}));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(a, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        }
        this.actionBar.showActionMode();
        return true;
    }

    private void onItemClick(int index, View view, MessageObject message, int a) {
        if (message != null) {
            if (this.actionBar.isActionModeShowed()) {
                int loadIndex = message.getDialogId() == this.dialog_id ? 0 : shared_media_item;
                if (this.selectedFiles[loadIndex].containsKey(Integer.valueOf(message.getId()))) {
                    this.selectedFiles[loadIndex].remove(Integer.valueOf(message.getId()));
                    if (!message.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else {
                    this.selectedFiles[loadIndex].put(Integer.valueOf(message.getId()), message);
                    if (!message.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount += shared_media_item;
                    }
                }
                if (this.selectedFiles[0].isEmpty() && this.selectedFiles[shared_media_item].isEmpty()) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[shared_media_item].size(), true);
                }
                this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                this.scrolling = false;
                if (view instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) view).setChecked(this.selectedFiles[loadIndex].containsKey(Integer.valueOf(message.getId())), true);
                } else if (view instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) view).setChecked(a, this.selectedFiles[loadIndex].containsKey(Integer.valueOf(message.getId())), true);
                } else if (view instanceof SharedLinkCell) {
                    ((SharedLinkCell) view).setChecked(this.selectedFiles[loadIndex].containsKey(Integer.valueOf(message.getId())), true);
                }
            } else if (this.selectedMode == 0) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[this.selectedMode].messages, index, this.dialog_id, this.mergeDialogId, this);
            } else if (this.selectedMode == shared_media_item || this.selectedMode == delete) {
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell cell = (SharedDocumentCell) view;
                    if (cell.isLoaded()) {
                        if (!message.isMusic() || !MediaController.getInstance().setPlaylist(this.sharedMediaData[this.selectedMode].messages, message)) {
                            File f = null;
                            String fileName = message.messageOwner.media != null ? FileLoader.getAttachFileName(message.getDocument()) : TtmlNode.ANONYMOUS_REGION_ID;
                            if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                f = new File(message.messageOwner.attachPath);
                            }
                            if (f == null || !(f == null || f.exists())) {
                                f = FileLoader.getPathToMessage(message.messageOwner);
                            }
                            if (f != null && f.exists()) {
                                String realMimeType = null;
                                try {
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                    int idx = fileName.lastIndexOf(46);
                                    if (idx != -1) {
                                        realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + shared_media_item).toLowerCase());
                                        if (realMimeType == null) {
                                            realMimeType = message.getDocument().mime_type;
                                            if (realMimeType == null || realMimeType.length() == 0) {
                                                realMimeType = null;
                                            }
                                        }
                                        if (realMimeType != null) {
                                            intent.setDataAndType(Uri.fromFile(f), realMimeType);
                                        } else {
                                            intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                        }
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                    }
                                    if (realMimeType != null) {
                                        try {
                                            getParentActivity().startActivityForResult(intent, 500);
                                            return;
                                        } catch (Exception e) {
                                            intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                            getParentActivity().startActivityForResult(intent, 500);
                                            return;
                                        }
                                    }
                                    getParentActivity().startActivityForResult(intent, 500);
                                } catch (Exception e2) {
                                    if (getParentActivity() != null) {
                                        Builder builder = new Builder(getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                                        Object[] objArr = new Object[shared_media_item];
                                        objArr[0] = message.getDocument().mime_type;
                                        builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0691R.string.NoHandleAppInstalled, objArr));
                                        showDialog(builder.create());
                                    }
                                }
                            }
                        }
                    } else if (cell.isLoading()) {
                        FileLoader.getInstance().cancelLoadFile(cell.getMessage().getDocument());
                        cell.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance().loadFile(cell.getMessage().getDocument(), false, false);
                        cell.updateFileExistIcon();
                    }
                }
            } else if (this.selectedMode == forward) {
                try {
                    WebPage webPage = message.messageOwner.media.webpage;
                    String link = null;
                    if (!(webPage == null || (webPage instanceof TL_webPageEmpty))) {
                        if (VERSION.SDK_INT < 16 || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                            link = webPage.url;
                        } else {
                            openWebView(webPage);
                            return;
                        }
                    }
                    if (link == null) {
                        link = ((SharedLinkCell) view).getLink(0);
                    }
                    if (link != null) {
                        Browser.openUrl(getParentActivity(), link);
                    }
                } catch (Throwable e3) {
                    FileLog.m13e("tmessages", e3);
                }
            }
        }
    }

    private void openWebView(WebPage webPage) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setCustomView(new WebFrameLayout(getParentActivity(), builder.create(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height));
        builder.setUseFullWidth(true);
        showDialog(builder.create());
    }

    private void fixLayoutInternal() {
        int i = 0;
        if (this.listView != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != files_item) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
            if (AndroidUtilities.isTablet()) {
                this.columnsCount = delete;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else if (rotation == forward || rotation == shared_media_item) {
                this.columnsCount = music_item;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            } else {
                this.columnsCount = delete;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.photoVideoAdapter.notifyDataSetChanged();
            if (this.dropDownContainer != null) {
                if (!AndroidUtilities.isTablet()) {
                    LayoutParams layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                    if (VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    layoutParams.topMargin = i;
                    this.dropDownContainer.setLayoutParams(layoutParams);
                }
                if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != files_item) {
                    this.dropDown.setTextSize(20.0f);
                } else {
                    this.dropDown.setTextSize(18.0f);
                }
            }
        }
    }
}
