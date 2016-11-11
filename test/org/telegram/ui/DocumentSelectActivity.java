package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class DocumentSelectActivity extends BaseFragment {
    private static final int done = 3;
    private ArrayList<View> actionModeViews;
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private TextView emptyView;
    private ArrayList<HistoryEntry> history;
    private ArrayList<ListItem> items;
    private ListAdapter listAdapter;
    private ListView listView;
    private BroadcastReceiver receiver;
    private boolean receiverRegistered;
    private boolean scrolling;
    private HashMap<String, ListItem> selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private long sizeLimit;

    /* renamed from: org.telegram.ui.DocumentSelectActivity.1 */
    class C12281 extends BroadcastReceiver {

        /* renamed from: org.telegram.ui.DocumentSelectActivity.1.1 */
        class C12271 implements Runnable {
            C12271() {
            }

            public void run() {
                try {
                    if (DocumentSelectActivity.this.currentDir == null) {
                        DocumentSelectActivity.this.listRoots();
                    } else {
                        DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C12281() {
        }

        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new C12271();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.3 */
    class C12293 implements OnTouchListener {
        C12293() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.4 */
    class C12304 implements OnTouchListener {
        C12304() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.5 */
    class C12315 implements OnScrollListener {
        C12315() {
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            DocumentSelectActivity.this.scrolling = scrollState != 0;
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.6 */
    class C12326 implements OnItemLongClickListener {
        C12326() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
            if (DocumentSelectActivity.this.actionBar.isActionModeShowed() || i < 0 || i >= DocumentSelectActivity.this.items.size()) {
                return false;
            }
            ListItem item = (ListItem) DocumentSelectActivity.this.items.get(i);
            File file = item.file;
            if (!(file == null || file.isDirectory())) {
                if (!file.canRead()) {
                    DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", C0691R.string.AccessError));
                    return false;
                } else if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                    DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", C0691R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                    return false;
                } else if (file.length() == 0) {
                    return false;
                } else {
                    DocumentSelectActivity.this.selectedFiles.put(file.toString(), item);
                    DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList<Animator> animators = new ArrayList();
                    for (int a = 0; a < DocumentSelectActivity.this.actionModeViews.size(); a++) {
                        View view2 = (View) DocumentSelectActivity.this.actionModeViews.get(a);
                        AndroidUtilities.clearDrawableAnimation(view2);
                        animators.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, TouchHelperCallback.ALPHA_FULL}));
                    }
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    DocumentSelectActivity.this.scrolling = false;
                    if (view instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) view).setChecked(true, true);
                    }
                    DocumentSelectActivity.this.actionBar.showActionMode();
                }
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.7 */
    class C12337 implements OnItemClickListener {
        C12337() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i >= 0 && i < DocumentSelectActivity.this.items.size()) {
                ListItem item = (ListItem) DocumentSelectActivity.this.items.get(i);
                File file = item.file;
                HistoryEntry he;
                if (file == null) {
                    if (item.icon == C0691R.drawable.ic_storage_gallery) {
                        if (DocumentSelectActivity.this.delegate != null) {
                            DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                        }
                        DocumentSelectActivity.this.finishFragment(false);
                        return;
                    }
                    he = (HistoryEntry) DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
                    DocumentSelectActivity.this.actionBar.setTitle(he.title);
                    if (he.dir != null) {
                        DocumentSelectActivity.this.listFiles(he.dir);
                    } else {
                        DocumentSelectActivity.this.listRoots();
                    }
                    DocumentSelectActivity.this.listView.setSelectionFromTop(he.scrollItem, he.scrollOffset);
                } else if (file.isDirectory()) {
                    he = new HistoryEntry(null);
                    he.scrollItem = DocumentSelectActivity.this.listView.getFirstVisiblePosition();
                    he.scrollOffset = DocumentSelectActivity.this.listView.getChildAt(0).getTop();
                    he.dir = DocumentSelectActivity.this.currentDir;
                    he.title = DocumentSelectActivity.this.actionBar.getTitle();
                    DocumentSelectActivity.this.history.add(he);
                    if (DocumentSelectActivity.this.listFiles(file)) {
                        DocumentSelectActivity.this.actionBar.setTitle(item.title);
                        DocumentSelectActivity.this.listView.setSelection(0);
                        return;
                    }
                    DocumentSelectActivity.this.history.remove(he);
                } else {
                    if (!file.canRead()) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", C0691R.string.AccessError));
                        file = new File("/mnt/sdcard");
                    }
                    if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", C0691R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                    } else if (file.length() == 0) {
                    } else {
                        if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                            if (DocumentSelectActivity.this.selectedFiles.containsKey(file.toString())) {
                                DocumentSelectActivity.this.selectedFiles.remove(file.toString());
                            } else {
                                DocumentSelectActivity.this.selectedFiles.put(file.toString(), item);
                            }
                            if (DocumentSelectActivity.this.selectedFiles.isEmpty()) {
                                DocumentSelectActivity.this.actionBar.hideActionMode();
                            } else {
                                DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
                            }
                            DocumentSelectActivity.this.scrolling = false;
                            if (view instanceof SharedDocumentCell) {
                                ((SharedDocumentCell) view).setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), true);
                            }
                        } else if (DocumentSelectActivity.this.delegate != null) {
                            ArrayList<String> files = new ArrayList();
                            files.add(file.getAbsolutePath());
                            DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, files);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.8 */
    class C12348 implements OnPreDrawListener {
        C12348() {
        }

        public boolean onPreDraw() {
            DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            DocumentSelectActivity.this.fixLayoutInternal();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.9 */
    class C12359 implements Comparator<File> {
        C12359() {
        }

        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() != rhs.isDirectory()) {
                return lhs.isDirectory() ? -1 : 1;
            } else {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        }
    }

    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList);

        void startDocumentSelectActivity();
    }

    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }
    }

    private class ListItem {
        String ext;
        File file;
        int icon;
        String subtitle;
        String thumb;
        String title;

        private ListItem() {
            this.subtitle = TtmlNode.ANONYMOUS_REGION_ID;
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity.2 */
    class C18592 extends ActionBarMenuOnItemClick {
        C18592() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    DocumentSelectActivity.this.selectedFiles.clear();
                    DocumentSelectActivity.this.actionBar.hideActionMode();
                    DocumentSelectActivity.this.listView.invalidateViews();
                    return;
                }
                DocumentSelectActivity.this.finishFragment();
            } else if (id == DocumentSelectActivity.done && DocumentSelectActivity.this.delegate != null) {
                ArrayList<String> files = new ArrayList();
                files.addAll(DocumentSelectActivity.this.selectedFiles.keySet());
                DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, files);
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return DocumentSelectActivity.this.items.size();
        }

        public Object getItem(int position) {
            return DocumentSelectActivity.this.items.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public int getItemViewType(int pos) {
            return ((ListItem) DocumentSelectActivity.this.items.get(pos)).subtitle.length() > 0 ? 0 : 1;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            boolean z = true;
            if (convertView == null) {
                convertView = new SharedDocumentCell(this.mContext);
            }
            SharedDocumentCell textDetailCell = (SharedDocumentCell) convertView;
            ListItem item = (ListItem) DocumentSelectActivity.this.items.get(position);
            if (item.icon != 0) {
                ((SharedDocumentCell) convertView).setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, item.icon);
            } else {
                ((SharedDocumentCell) convertView).setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0);
            }
            if (item.file == null || !DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                if (DocumentSelectActivity.this.scrolling) {
                    z = false;
                }
                textDetailCell.setChecked(false, z);
            } else {
                boolean z2;
                boolean containsKey = DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString());
                if (DocumentSelectActivity.this.scrolling) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                textDetailCell.setChecked(containsKey, z2);
            }
            return convertView;
        }
    }

    public DocumentSelectActivity() {
        this.items = new ArrayList();
        this.receiverRegistered = false;
        this.history = new ArrayList();
        this.sizeLimit = 1610612736;
        this.selectedFiles = new HashMap();
        this.actionModeViews = new ArrayList();
        this.receiver = new C12281();
    }

    public void onFragmentDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            filter.addAction("android.intent.action.MEDIA_CHECKING");
            filter.addAction("android.intent.action.MEDIA_EJECT");
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_NOFS");
            filter.addAction("android.intent.action.MEDIA_REMOVED");
            filter.addAction("android.intent.action.MEDIA_SHARED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.receiver, filter);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", C0691R.string.SelectFile));
        this.actionBar.setActionBarMenuOnItemClick(new C18592());
        this.selectedFiles.clear();
        this.actionModeViews.clear();
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(-9211021);
        this.selectedMessagesCountTextView.setOnTouchListener(new C12293());
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, (float) TouchHelperCallback.ALPHA_FULL, 65, 0, 0, 0));
        this.actionModeViews.add(actionMode.addItem(done, C0691R.drawable.ic_ab_done_gray, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(C0691R.layout.document_select_layout, null, false);
        this.listAdapter = new ListAdapter(context);
        this.emptyView = (TextView) this.fragmentView.findViewById(C0691R.id.searchEmptyView);
        this.emptyView.setOnTouchListener(new C12304());
        this.listView = (ListView) this.fragmentView.findViewById(C0691R.id.listView);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnScrollListener(new C12315());
        this.listView.setOnItemLongClickListener(new C12326());
        this.listView.setOnItemClickListener(new C12337());
        listRoots();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C12348());
        }
    }

    private void fixLayoutInternal() {
        if (this.selectedMessagesCountTextView != null) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
    }

    public boolean onBackPressed() {
        if (this.history.size() <= 0) {
            return super.onBackPressed();
        }
        HistoryEntry he = (HistoryEntry) this.history.remove(this.history.size() - 1);
        this.actionBar.setTitle(he.title);
        if (he.dir != null) {
            listFiles(he.dir);
        } else {
            listRoots();
        }
        this.listView.setSelectionFromTop(he.scrollItem, he.scrollOffset);
        return false;
    }

    public void setDelegate(DocumentSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private boolean listFiles(File dir) {
        if (dir.canRead()) {
            this.emptyView.setText(LocaleController.getString("NoFiles", C0691R.string.NoFiles));
            try {
                File[] files = dir.listFiles();
                if (files == null) {
                    showErrorBox(LocaleController.getString("UnknownError", C0691R.string.UnknownError));
                    return false;
                }
                ListItem item;
                this.currentDir = dir;
                this.items.clear();
                Arrays.sort(files, new C12359());
                for (File file : files) {
                    if (file.getName().indexOf(46) != 0) {
                        item = new ListItem();
                        item.title = file.getName();
                        item.file = file;
                        if (file.isDirectory()) {
                            item.icon = C0691R.drawable.ic_directory;
                            item.subtitle = LocaleController.getString("Folder", C0691R.string.Folder);
                        } else {
                            String fname = file.getName();
                            String[] sp = fname.split("\\.");
                            item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                            item.subtitle = AndroidUtilities.formatFileSize(file.length());
                            fname = fname.toLowerCase();
                            if (fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                                item.thumb = file.getAbsolutePath();
                            }
                        }
                        this.items.add(item);
                    }
                }
                item = new ListItem();
                item.title = "..";
                if (this.history.size() > 0) {
                    HistoryEntry entry = (HistoryEntry) this.history.get(this.history.size() - 1);
                    if (entry.dir == null) {
                        item.subtitle = LocaleController.getString("Folder", C0691R.string.Folder);
                    } else {
                        item.subtitle = entry.dir.toString();
                    }
                } else {
                    item.subtitle = LocaleController.getString("Folder", C0691R.string.Folder);
                }
                item.icon = C0691R.drawable.ic_directory;
                item.file = null;
                this.items.add(0, item);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            } catch (Exception e) {
                showErrorBox(e.getLocalizedMessage());
                return false;
            }
        } else if ((!dir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) && !dir.getAbsolutePath().startsWith("/sdcard") && !dir.getAbsolutePath().startsWith("/mnt/sdcard")) || Environment.getExternalStorageState().equals("mounted") || Environment.getExternalStorageState().equals("mounted_ro")) {
            showErrorBox(LocaleController.getString("AccessError", C0691R.string.AccessError));
            return false;
        } else {
            this.currentDir = dir;
            this.items.clear();
            if ("shared".equals(Environment.getExternalStorageState())) {
                this.emptyView.setText(LocaleController.getString("UsbActive", C0691R.string.UsbActive));
            } else {
                this.emptyView.setText(LocaleController.getString("NotMounted", C0691R.string.NotMounted));
            }
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        }
    }

    private void showErrorBox(String error) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", C0691R.string.AppName)).setMessage(error).setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null).show();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    private void listRoots() {
        /*
        r24 = this;
        r21 = 0;
        r0 = r21;
        r1 = r24;
        r1.currentDir = r0;
        r0 = r24;
        r0 = r0.items;
        r21 = r0;
        r21.clear();
        r17 = new java.util.HashSet;
        r17.<init>();
        r21 = android.os.Environment.getExternalStorageDirectory();
        r5 = r21.getPath();
        r12 = android.os.Environment.isExternalStorageRemovable();
        r6 = android.os.Environment.getExternalStorageState();
        r21 = "mounted";
        r0 = r21;
        r21 = r6.equals(r0);
        if (r21 != 0) goto L_0x003a;
    L_0x0030:
        r21 = "mounted_ro";
        r0 = r21;
        r21 = r6.equals(r0);
        if (r21 == 0) goto L_0x0081;
    L_0x003a:
        r8 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r21 = 0;
        r0 = r24;
        r1 = r21;
        r8.<init>(r1);
        r21 = android.os.Environment.isExternalStorageRemovable();
        if (r21 == 0) goto L_0x02ad;
    L_0x004b:
        r21 = "SdCard";
        r22 = 2131166132; // 0x7f0703b4 float:1.79465E38 double:1.0529359714E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);
        r0 = r21;
        r8.title = r0;
        r21 = 2130837735; // 0x7f0200e7 float:1.7280432E38 double:1.0527737217E-314;
        r0 = r21;
        r8.icon = r0;
    L_0x005f:
        r0 = r24;
        r21 = r0.getRootSubtitle(r5);
        r0 = r21;
        r8.subtitle = r0;
        r21 = android.os.Environment.getExternalStorageDirectory();
        r0 = r21;
        r8.file = r0;
        r0 = r24;
        r0 = r0.items;
        r21 = r0;
        r0 = r21;
        r0.add(r8);
        r0 = r17;
        r0.add(r5);
    L_0x0081:
        r3 = 0;
        r4 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x030f }
        r21 = new java.io.FileReader;	 Catch:{ Exception -> 0x030f }
        r22 = "/proc/mounts";
        r21.<init>(r22);	 Catch:{ Exception -> 0x030f }
        r0 = r21;
        r4.<init>(r0);	 Catch:{ Exception -> 0x030f }
    L_0x0090:
        r14 = r4.readLine();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r14 == 0) goto L_0x02da;
    L_0x0096:
        r21 = "vfat";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x00aa;
    L_0x00a0:
        r21 = "/mnt";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 == 0) goto L_0x0090;
    L_0x00aa:
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m11e(r0, r14);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r19 = new java.util.StringTokenizer;	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21 = " ";
        r0 = r19;
        r1 = r21;
        r0.<init>(r14, r1);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r20 = r19.nextToken();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r16 = r19.nextToken();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r0 = r17;
        r1 = r16;
        r21 = r0.contains(r1);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0090;
    L_0x00ce:
        r21 = "/dev/block/vold";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 == 0) goto L_0x0090;
    L_0x00d8:
        r21 = "/mnt/secure";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0090;
    L_0x00e2:
        r21 = "/mnt/asec";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0090;
    L_0x00ec:
        r21 = "/mnt/obb";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0090;
    L_0x00f6:
        r21 = "/dev/mapper";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0090;
    L_0x0100:
        r21 = "tmpfs";
        r0 = r21;
        r21 = r14.contains(r0);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0090;
    L_0x010a:
        r21 = new java.io.File;	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r0 = r21;
        r1 = r16;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21 = r21.isDirectory();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 != 0) goto L_0x0155;
    L_0x0119:
        r21 = 47;
        r0 = r16;
        r1 = r21;
        r11 = r0.lastIndexOf(r1);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21 = -1;
        r0 = r21;
        if (r11 == r0) goto L_0x0155;
    L_0x0129:
        r21 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21.<init>();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r22 = "/storage/";
        r21 = r21.append(r22);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r22 = r11 + 1;
        r0 = r16;
        r1 = r22;
        r22 = r0.substring(r1);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21 = r21.append(r22);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r15 = r21.toString();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21 = new java.io.File;	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r0 = r21;
        r0.<init>(r15);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r21 = r21.isDirectory();	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        if (r21 == 0) goto L_0x0155;
    L_0x0153:
        r16 = r15;
    L_0x0155:
        r0 = r17;
        r1 = r16;
        r0.add(r1);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        r13 = new org.telegram.ui.DocumentSelectActivity$ListItem;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r21 = 0;
        r0 = r24;
        r1 = r21;
        r13.<init>(r1);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r21 = r16.toLowerCase();	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r22 = "sd";
        r21 = r21.contains(r22);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        if (r21 == 0) goto L_0x02c3;
    L_0x0173:
        r21 = "SdCard";
        r22 = 2131166132; // 0x7f0703b4 float:1.79465E38 double:1.0529359714E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r21;
        r13.title = r0;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
    L_0x0180:
        r21 = 2130837735; // 0x7f0200e7 float:1.7280432E38 double:1.0527737217E-314;
        r0 = r21;
        r13.icon = r0;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r24;
        r1 = r16;
        r21 = r0.getRootSubtitle(r1);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r21;
        r13.subtitle = r0;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r21 = new java.io.File;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r21;
        r1 = r16;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r21;
        r13.file = r0;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r24;
        r0 = r0.items;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r21 = r0;
        r0 = r21;
        r0.add(r13);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        goto L_0x0090;
    L_0x01ad:
        r7 = move-exception;
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m13e(r0, r7);	 Catch:{ Exception -> 0x01b7, all -> 0x02d2 }
        goto L_0x0090;
    L_0x01b7:
        r7 = move-exception;
        r3 = r4;
    L_0x01b9:
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m13e(r0, r7);	 Catch:{ all -> 0x030d }
        if (r3 == 0) goto L_0x01c5;
    L_0x01c2:
        r3.close();	 Catch:{ Exception -> 0x02ed }
    L_0x01c5:
        r9 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r21 = 0;
        r0 = r24;
        r1 = r21;
        r9.<init>(r1);
        r21 = "/";
        r0 = r21;
        r9.title = r0;
        r21 = "SystemRoot";
        r22 = 2131166244; // 0x7f070424 float:1.7946728E38 double:1.0529360267E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);
        r0 = r21;
        r9.subtitle = r0;
        r21 = 2130837725; // 0x7f0200dd float:1.7280412E38 double:1.052773717E-314;
        r0 = r21;
        r9.icon = r0;
        r21 = new java.io.File;
        r22 = "/";
        r21.<init>(r22);
        r0 = r21;
        r9.file = r0;
        r0 = r24;
        r0 = r0.items;
        r21 = r0;
        r0 = r21;
        r0.add(r9);
        r18 = new java.io.File;	 Catch:{ Exception -> 0x0300 }
        r21 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x0300 }
        r22 = "AppName";
        r23 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r22 = org.telegram.messenger.LocaleController.getString(r22, r23);	 Catch:{ Exception -> 0x0300 }
        r0 = r18;
        r1 = r21;
        r2 = r22;
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0300 }
        r21 = r18.exists();	 Catch:{ Exception -> 0x0300 }
        if (r21 == 0) goto L_0x0255;
    L_0x021e:
        r10 = new org.telegram.ui.DocumentSelectActivity$ListItem;	 Catch:{ Exception -> 0x0300 }
        r21 = 0;
        r0 = r24;
        r1 = r21;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0300 }
        r21 = "AppName";
        r22 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);	 Catch:{ Exception -> 0x030a }
        r0 = r21;
        r10.title = r0;	 Catch:{ Exception -> 0x030a }
        r21 = r18.toString();	 Catch:{ Exception -> 0x030a }
        r0 = r21;
        r10.subtitle = r0;	 Catch:{ Exception -> 0x030a }
        r21 = 2130837725; // 0x7f0200dd float:1.7280412E38 double:1.052773717E-314;
        r0 = r21;
        r10.icon = r0;	 Catch:{ Exception -> 0x030a }
        r0 = r18;
        r10.file = r0;	 Catch:{ Exception -> 0x030a }
        r0 = r24;
        r0 = r0.items;	 Catch:{ Exception -> 0x030a }
        r21 = r0;
        r0 = r21;
        r0.add(r10);	 Catch:{ Exception -> 0x030a }
        r9 = r10;
    L_0x0255:
        r9 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r21 = 0;
        r0 = r24;
        r1 = r21;
        r9.<init>(r1);
        r21 = "Gallery";
        r22 = 2131165683; // 0x7f0701f3 float:1.794559E38 double:1.0529357496E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);
        r0 = r21;
        r9.title = r0;
        r21 = "GalleryInfo";
        r22 = 2131165684; // 0x7f0701f4 float:1.7945592E38 double:1.05293575E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);
        r0 = r21;
        r9.subtitle = r0;
        r21 = 2130837768; // 0x7f020108 float:1.72805E38 double:1.052773738E-314;
        r0 = r21;
        r9.icon = r0;
        r21 = 0;
        r0 = r21;
        r9.file = r0;
        r0 = r24;
        r0 = r0.items;
        r21 = r0;
        r0 = r21;
        r0.add(r9);
        r0 = r24;
        r0 = r0.listView;
        r21 = r0;
        org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r21);
        r21 = 1;
        r0 = r21;
        r1 = r24;
        r1.scrolling = r0;
        r0 = r24;
        r0 = r0.listAdapter;
        r21 = r0;
        r21.notifyDataSetChanged();
        return;
    L_0x02ad:
        r21 = "InternalStorage";
        r22 = 2131165718; // 0x7f070216 float:1.7945661E38 double:1.052935767E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);
        r0 = r21;
        r8.title = r0;
        r21 = 2130837767; // 0x7f020107 float:1.7280497E38 double:1.0527737375E-314;
        r0 = r21;
        r8.icon = r0;
        goto L_0x005f;
    L_0x02c3:
        r21 = "ExternalStorage";
        r22 = 2131165602; // 0x7f0701a2 float:1.7945426E38 double:1.0529357095E-314;
        r21 = org.telegram.messenger.LocaleController.getString(r21, r22);	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        r0 = r21;
        r13.title = r0;	 Catch:{ Exception -> 0x01ad, all -> 0x02d2 }
        goto L_0x0180;
    L_0x02d2:
        r21 = move-exception;
        r3 = r4;
    L_0x02d4:
        if (r3 == 0) goto L_0x02d9;
    L_0x02d6:
        r3.close();	 Catch:{ Exception -> 0x02f7 }
    L_0x02d9:
        throw r21;
    L_0x02da:
        if (r4 == 0) goto L_0x0312;
    L_0x02dc:
        r4.close();	 Catch:{ Exception -> 0x02e2 }
        r3 = r4;
        goto L_0x01c5;
    L_0x02e2:
        r7 = move-exception;
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m13e(r0, r7);
        r3 = r4;
        goto L_0x01c5;
    L_0x02ed:
        r7 = move-exception;
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m13e(r0, r7);
        goto L_0x01c5;
    L_0x02f7:
        r7 = move-exception;
        r22 = "tmessages";
        r0 = r22;
        org.telegram.messenger.FileLog.m13e(r0, r7);
        goto L_0x02d9;
    L_0x0300:
        r7 = move-exception;
    L_0x0301:
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m13e(r0, r7);
        goto L_0x0255;
    L_0x030a:
        r7 = move-exception;
        r9 = r10;
        goto L_0x0301;
    L_0x030d:
        r21 = move-exception;
        goto L_0x02d4;
    L_0x030f:
        r7 = move-exception;
        goto L_0x01b9;
    L_0x0312:
        r3 = r4;
        goto L_0x01c5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DocumentSelectActivity.listRoots():void");
    }

    private String getRootSubtitle(String path) {
        try {
            StatFs stat = new StatFs(path);
            long free = ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
            if (((long) stat.getBlockCount()) * ((long) stat.getBlockSize()) == 0) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            return LocaleController.formatString("FreeOfTotal", C0691R.string.FreeOfTotal, AndroidUtilities.formatFileSize(free), AndroidUtilities.formatFileSize(((long) stat.getBlockCount()) * ((long) stat.getBlockSize())));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return path;
        }
    }
}
