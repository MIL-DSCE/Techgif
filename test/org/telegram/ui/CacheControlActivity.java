package org.telegram.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.io.File;
import java.util.ArrayList;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ClearCacheService;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.query.BotQuery;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

public class CacheControlActivity extends BaseFragment {
    private long audioSize;
    private int cacheInfoRow;
    private int cacheRow;
    private long cacheSize;
    private boolean calculating;
    private volatile boolean canceled;
    private boolean[] clear;
    private int databaseInfoRow;
    private int databaseRow;
    private long databaseSize;
    private long documentsSize;
    private int keepMediaInfoRow;
    private int keepMediaRow;
    private ListAdapter listAdapter;
    private long musicSize;
    private long photoSize;
    private int rowCount;
    private long totalSize;
    private long videoSize;

    /* renamed from: org.telegram.ui.CacheControlActivity.1 */
    class C09741 implements Runnable {

        /* renamed from: org.telegram.ui.CacheControlActivity.1.1 */
        class C09731 implements Runnable {
            C09731() {
            }

            public void run() {
                CacheControlActivity.this.calculating = false;
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        C09741() {
        }

        public void run() {
            CacheControlActivity.this.cacheSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), 0);
            if (!CacheControlActivity.this.canceled) {
                CacheControlActivity.this.photoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), 0);
                if (!CacheControlActivity.this.canceled) {
                    CacheControlActivity.this.videoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), 0);
                    if (!CacheControlActivity.this.canceled) {
                        CacheControlActivity.this.documentsSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 1);
                        if (!CacheControlActivity.this.canceled) {
                            CacheControlActivity.this.musicSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 2);
                            if (!CacheControlActivity.this.canceled) {
                                CacheControlActivity.this.audioSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), 0);
                                CacheControlActivity.this.totalSize = ((((CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize) + CacheControlActivity.this.audioSize) + CacheControlActivity.this.photoSize) + CacheControlActivity.this.documentsSize) + CacheControlActivity.this.musicSize;
                                AndroidUtilities.runOnUIThread(new C09731());
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CacheControlActivity.2 */
    class C09762 implements Runnable {
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: org.telegram.ui.CacheControlActivity.2.1 */
        class C09751 implements Runnable {
            final /* synthetic */ boolean val$imagesClearedFinal;

            C09751(boolean z) {
                this.val$imagesClearedFinal = z;
            }

            public void run() {
                if (this.val$imagesClearedFinal) {
                    ImageLoader.getInstance().clearMemory();
                }
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
                try {
                    C09762.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C09762(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run() {
            boolean imagesCleared = false;
            for (int a = 0; a < 6; a++) {
                if (CacheControlActivity.this.clear[a]) {
                    int type = -1;
                    int documentsMusicType = 0;
                    if (a == 0) {
                        type = 0;
                    } else if (a == 1) {
                        type = 2;
                    } else if (a == 2) {
                        type = 3;
                        documentsMusicType = 1;
                    } else if (a == 3) {
                        type = 3;
                        documentsMusicType = 2;
                    } else if (a == 4) {
                        type = 1;
                    } else if (a == 5) {
                        type = 4;
                    }
                    if (type != -1) {
                        File file = FileLoader.getInstance().checkDirectory(type);
                        if (file != null) {
                            try {
                                File[] array = file.listFiles();
                                if (array != null) {
                                    int b = 0;
                                    while (b < array.length) {
                                        String name = array[b].getName().toLowerCase();
                                        if (documentsMusicType == 1 || documentsMusicType == 2) {
                                            if (name.endsWith(".mp3") || name.endsWith(".m4a")) {
                                                if (documentsMusicType == 1) {
                                                    b++;
                                                }
                                            } else if (documentsMusicType == 2) {
                                                b++;
                                            }
                                        }
                                        if (!name.equals(".nomedia") && array[b].isFile()) {
                                            array[b].delete();
                                        }
                                        b++;
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                        }
                        if (type == 4) {
                            CacheControlActivity.this.cacheSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), documentsMusicType);
                            imagesCleared = true;
                        } else if (type == 1) {
                            CacheControlActivity.this.audioSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), documentsMusicType);
                        } else if (type == 3) {
                            if (documentsMusicType == 1) {
                                CacheControlActivity.this.documentsSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), documentsMusicType);
                            } else {
                                CacheControlActivity.this.musicSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), documentsMusicType);
                            }
                        } else if (type == 0) {
                            imagesCleared = true;
                            CacheControlActivity.this.photoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), documentsMusicType);
                        } else if (type == 2) {
                            CacheControlActivity.this.videoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), documentsMusicType);
                        }
                    }
                }
            }
            boolean imagesClearedFinal = imagesCleared;
            CacheControlActivity.this.totalSize = ((((CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize) + CacheControlActivity.this.audioSize) + CacheControlActivity.this.photoSize) + CacheControlActivity.this.documentsSize) + CacheControlActivity.this.musicSize;
            AndroidUtilities.runOnUIThread(new C09751(imagesClearedFinal));
        }
    }

    /* renamed from: org.telegram.ui.CacheControlActivity.4 */
    class C09834 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.CacheControlActivity.4.1 */
        class C09771 implements OnClickListener {
            C09771() {
            }

            public void onClick(DialogInterface dialog, int which) {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("keep_media", which).commit();
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
                PendingIntent pintent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, ClearCacheService.class), 0);
                AlarmManager alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi21.CATEGORY_ALARM);
                if (which == 2) {
                    alarmManager.cancel(pintent);
                } else {
                    alarmManager.setInexactRepeating(2, 86400000, 86400000, pintent);
                }
            }
        }

        /* renamed from: org.telegram.ui.CacheControlActivity.4.2 */
        class C09802 implements OnClickListener {

            /* renamed from: org.telegram.ui.CacheControlActivity.4.2.1 */
            class C09791 implements Runnable {
                final /* synthetic */ ProgressDialog val$progressDialog;

                /* renamed from: org.telegram.ui.CacheControlActivity.4.2.1.1 */
                class C09781 implements Runnable {
                    C09781() {
                    }

                    public void run() {
                        try {
                            C09791.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                        if (CacheControlActivity.this.listAdapter != null) {
                            CacheControlActivity.this.databaseSize = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").length();
                            CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }

                C09791(ProgressDialog progressDialog) {
                    this.val$progressDialog = progressDialog;
                }

                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                        ArrayList<Long> dialogsToCleanup = new ArrayList();
                        String str = "SELECT did FROM dialogs WHERE 1";
                        SQLiteCursor cursor = database.queryFinalized(r27, new Object[0]);
                        StringBuilder ids = new StringBuilder();
                        while (cursor.next()) {
                            long did = cursor.longValue(0);
                            int high_id = (int) (did >> 32);
                            if (!(((int) did) == 0 || high_id == 1)) {
                                dialogsToCleanup.add(Long.valueOf(did));
                            }
                        }
                        cursor.dispose();
                        SQLitePreparedStatement state5 = database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                        SQLitePreparedStatement state6 = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                        database.beginTransaction();
                        for (int a = 0; a < dialogsToCleanup.size(); a++) {
                            Long did2 = (Long) dialogsToCleanup.get(a);
                            int messagesCount = 0;
                            Object[] objArr = new Object[0];
                            cursor = database.queryFinalized("SELECT COUNT(mid) FROM messages WHERE uid = " + did2, objArr);
                            if (cursor.next()) {
                                messagesCount = cursor.intValue(0);
                            }
                            cursor.dispose();
                            if (messagesCount > 2) {
                                objArr = new Object[0];
                                cursor = database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + did2, objArr);
                                int messageId = -1;
                                if (cursor.next()) {
                                    long last_mid_i = cursor.longValue(0);
                                    long last_mid = cursor.longValue(1);
                                    objArr = new Object[0];
                                    SQLiteCursor cursor2 = database.queryFinalized("SELECT data FROM messages WHERE uid = " + did2 + " AND mid IN (" + last_mid_i + "," + last_mid + ")", objArr);
                                    while (cursor2.next()) {
                                        try {
                                            NativeByteBuffer data = cursor2.byteBufferValue(0);
                                            if (data != null) {
                                                Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                                data.reuse();
                                                if (message != null) {
                                                    messageId = message.id;
                                                }
                                            }
                                        } catch (Throwable e) {
                                            FileLog.m13e("tmessages", e);
                                        }
                                    }
                                    cursor2.dispose();
                                    database.executeFast("DELETE FROM messages WHERE uid = " + did2 + " AND mid != " + last_mid_i + " AND mid != " + last_mid).stepThis().dispose();
                                    database.executeFast("DELETE FROM messages_holes WHERE uid = " + did2).stepThis().dispose();
                                    database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + did2).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + did2).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_v2 WHERE uid = " + did2).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + did2).stepThis().dispose();
                                    BotQuery.clearBotKeyboard(did2.longValue(), null);
                                    if (messageId != -1) {
                                        MessagesStorage.createFirstHoles(did2.longValue(), state5, state6, messageId);
                                    }
                                }
                                cursor.dispose();
                            }
                        }
                        state5.dispose();
                        state6.dispose();
                        database.commitTransaction();
                        database.executeFast("VACUUM").stepThis().dispose();
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    } finally {
                        AndroidUtilities.runOnUIThread(new C09781());
                    }
                }
            }

            C09802() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProgressDialog progressDialog = new ProgressDialog(CacheControlActivity.this.getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09791(progressDialog));
            }
        }

        /* renamed from: org.telegram.ui.CacheControlActivity.4.3 */
        class C09813 implements View.OnClickListener {
            C09813() {
            }

            public void onClick(View v) {
                CheckBoxCell cell = (CheckBoxCell) v;
                int num = ((Integer) cell.getTag()).intValue();
                CacheControlActivity.this.clear[num] = !CacheControlActivity.this.clear[num];
                cell.setChecked(CacheControlActivity.this.clear[num], true);
            }
        }

        /* renamed from: org.telegram.ui.CacheControlActivity.4.4 */
        class C09824 implements View.OnClickListener {
            C09824() {
            }

            public void onClick(View v) {
                try {
                    if (CacheControlActivity.this.visibleDialog != null) {
                        CacheControlActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                CacheControlActivity.this.cleanupFolders();
            }
        }

        C09834() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Builder builder;
            if (i == CacheControlActivity.this.keepMediaRow) {
                builder = new Builder(CacheControlActivity.this.getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("KeepMediaForever", C0691R.string.KeepMediaForever)}, new C09771());
                CacheControlActivity.this.showDialog(builder.create());
            } else if (i == CacheControlActivity.this.databaseRow) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(CacheControlActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                builder2.setMessage(LocaleController.getString("LocalDatabaseClear", C0691R.string.LocalDatabaseClear));
                builder2.setPositiveButton(LocaleController.getString("CacheClear", C0691R.string.CacheClear), new C09802());
                CacheControlActivity.this.showDialog(builder2.create());
            } else if (i == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0 && CacheControlActivity.this.getParentActivity() != null) {
                builder = new Builder(CacheControlActivity.this.getParentActivity());
                builder.setApplyTopPadding(false);
                builder.setApplyBottomPadding(false);
                LinearLayout linearLayout = new LinearLayout(CacheControlActivity.this.getParentActivity());
                linearLayout.setOrientation(1);
                for (int a = 0; a < 6; a++) {
                    long size = 0;
                    String name = null;
                    if (a == 0) {
                        size = CacheControlActivity.this.photoSize;
                        name = LocaleController.getString("LocalPhotoCache", C0691R.string.LocalPhotoCache);
                    } else if (a == 1) {
                        size = CacheControlActivity.this.videoSize;
                        name = LocaleController.getString("LocalVideoCache", C0691R.string.LocalVideoCache);
                    } else if (a == 2) {
                        size = CacheControlActivity.this.documentsSize;
                        name = LocaleController.getString("LocalDocumentCache", C0691R.string.LocalDocumentCache);
                    } else if (a == 3) {
                        size = CacheControlActivity.this.musicSize;
                        name = LocaleController.getString("LocalMusicCache", C0691R.string.LocalMusicCache);
                    } else if (a == 4) {
                        size = CacheControlActivity.this.audioSize;
                        name = LocaleController.getString("LocalAudioCache", C0691R.string.LocalAudioCache);
                    } else if (a == 5) {
                        size = CacheControlActivity.this.cacheSize;
                        name = LocaleController.getString("LocalCache", C0691R.string.LocalCache);
                    }
                    if (size > 0) {
                        CacheControlActivity.this.clear[a] = true;
                        CheckBoxCell checkBoxCell = new CheckBoxCell(CacheControlActivity.this.getParentActivity());
                        checkBoxCell.setTag(Integer.valueOf(a));
                        checkBoxCell.setBackgroundResource(C0691R.drawable.list_selector);
                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                        checkBoxCell.setText(name, AndroidUtilities.formatFileSize(size), true, true);
                        checkBoxCell.setOnClickListener(new C09813());
                    } else {
                        CacheControlActivity.this.clear[a] = false;
                    }
                }
                BottomSheetCell cell = new BottomSheetCell(CacheControlActivity.this.getParentActivity(), 1);
                cell.setBackgroundResource(C0691R.drawable.list_selector);
                cell.setTextAndIcon(LocaleController.getString("ClearMediaCache", C0691R.string.ClearMediaCache).toUpperCase(), 0);
                cell.setTextColor(-3319206);
                cell.setOnClickListener(new C09824());
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                builder.setCustomView(linearLayout);
                CacheControlActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.CacheControlActivity.3 */
    class C17633 extends ActionBarMenuOnItemClick {
        C17633() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                CacheControlActivity.this.finishFragment();
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == CacheControlActivity.this.databaseRow || ((i == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0) || i == CacheControlActivity.this.keepMediaRow);
        }

        public int getCount() {
            return CacheControlActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == CacheControlActivity.this.databaseRow) {
                    textCell.setTextAndValue(LocaleController.getString("LocalDatabase", C0691R.string.LocalDatabase), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                } else if (i == CacheControlActivity.this.cacheRow) {
                    if (CacheControlActivity.this.calculating) {
                        textCell.setTextAndValue(LocaleController.getString("ClearMediaCache", C0691R.string.ClearMediaCache), LocaleController.getString("CalculatingSize", C0691R.string.CalculatingSize), false);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("ClearMediaCache", C0691R.string.ClearMediaCache), CacheControlActivity.this.totalSize == 0 ? LocaleController.getString("CacheEmpty", C0691R.string.CacheEmpty) : AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize), false);
                    }
                } else if (i == CacheControlActivity.this.keepMediaRow) {
                    String value;
                    int keepMedia = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
                    if (keepMedia == 0) {
                        value = LocaleController.formatPluralString("Weeks", 1);
                    } else if (keepMedia == 1) {
                        value = LocaleController.formatPluralString("Months", 1);
                    } else {
                        value = LocaleController.getString("KeepMediaForever", C0691R.string.KeepMediaForever);
                    }
                    textCell.setTextAndValue(LocaleController.getString("KeepMedia", C0691R.string.KeepMedia), value, false);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == CacheControlActivity.this.databaseInfoRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("LocalDatabaseInfo", C0691R.string.LocalDatabaseInfo));
                    view.setBackgroundResource(C0691R.drawable.greydivider_bottom);
                } else if (i == CacheControlActivity.this.cacheInfoRow) {
                    ((TextInfoPrivacyCell) view).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    view.setBackgroundResource(C0691R.drawable.greydivider);
                } else if (i == CacheControlActivity.this.keepMediaInfoRow) {
                    ((TextInfoPrivacyCell) view).setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", C0691R.string.KeepMediaInfo)));
                    view.setBackgroundResource(C0691R.drawable.greydivider);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == CacheControlActivity.this.databaseRow || i == CacheControlActivity.this.cacheRow || i == CacheControlActivity.this.keepMediaRow) {
                return 0;
            }
            if (i == CacheControlActivity.this.databaseInfoRow || i == CacheControlActivity.this.cacheInfoRow || i == CacheControlActivity.this.keepMediaInfoRow) {
                return 1;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public CacheControlActivity() {
        this.databaseSize = -1;
        this.cacheSize = -1;
        this.documentsSize = -1;
        this.audioSize = -1;
        this.musicSize = -1;
        this.photoSize = -1;
        this.videoSize = -1;
        this.totalSize = -1;
        this.clear = new boolean[6];
        this.calculating = true;
        this.canceled = false;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.keepMediaRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.keepMediaInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.databaseRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.databaseInfoRow = i;
        this.databaseSize = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").length();
        Utilities.globalQueue.postRunnable(new C09741());
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.canceled = true;
    }

    private long getDirectorySize(File dir, int documentsMusicType) {
        if (dir == null || this.canceled) {
            return 0;
        }
        long size = 0;
        if (dir.isDirectory()) {
            try {
                File[] array = dir.listFiles();
                if (array == null) {
                    return 0;
                }
                for (File file : array) {
                    if (this.canceled) {
                        return 0;
                    }
                    if (documentsMusicType == 1 || documentsMusicType == 2) {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".mp3") || name.endsWith(".m4a")) {
                            if (documentsMusicType == 1) {
                            }
                        } else if (documentsMusicType == 2) {
                        }
                    }
                    if (file.isDirectory()) {
                        size += getDirectorySize(file, documentsMusicType);
                    } else {
                        size += file.length();
                    }
                }
                return size;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                return 0;
            }
        } else if (dir.isFile()) {
            return 0 + dir.length();
        } else {
            return 0;
        }
    }

    private void cleanupFolders() {
        ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
        progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utilities.globalQueue.postRunnable(new C09762(progressDialog));
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CacheSettings", C0691R.string.CacheSettings));
        this.actionBar.setActionBarMenuOnItemClick(new C17633());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(-986896);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C09834());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
