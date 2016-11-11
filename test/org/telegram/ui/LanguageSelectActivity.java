package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextSettingsCell;

public class LanguageSelectActivity extends BaseFragment {
    private TextView emptyTextView;
    private BaseFragmentAdapter listAdapter;
    private ListView listView;
    private BaseFragmentAdapter searchListViewAdapter;
    public ArrayList<LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;

    /* renamed from: org.telegram.ui.LanguageSelectActivity.3 */
    class C12553 implements OnTouchListener {
        C12553() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.4 */
    class C12564 implements OnItemClickListener {
        C12564() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            LocaleInfo localeInfo = null;
            if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                if (i >= 0 && i < LanguageSelectActivity.this.searchResult.size()) {
                    localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
                }
            } else if (i >= 0 && i < LocaleController.getInstance().sortedLanguages.size()) {
                localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(i);
            }
            if (localeInfo != null) {
                LocaleController.getInstance().applyLanguage(localeInfo, true);
                LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false);
            }
            LanguageSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.5 */
    class C12585 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.LanguageSelectActivity.5.1 */
        class C12571 implements OnClickListener {
            final /* synthetic */ LocaleInfo val$finalLocaleInfo;

            C12571(LocaleInfo localeInfo) {
                this.val$finalLocaleInfo = localeInfo;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (LocaleController.getInstance().deleteLanguage(this.val$finalLocaleInfo)) {
                    if (LanguageSelectActivity.this.searchResult != null) {
                        LanguageSelectActivity.this.searchResult.remove(this.val$finalLocaleInfo);
                    }
                    if (LanguageSelectActivity.this.listAdapter != null) {
                        LanguageSelectActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    if (LanguageSelectActivity.this.searchListViewAdapter != null) {
                        LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        C12585() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            LocaleInfo localeInfo = null;
            if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                if (i >= 0 && i < LanguageSelectActivity.this.searchResult.size()) {
                    localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
                }
            } else if (i >= 0 && i < LocaleController.getInstance().sortedLanguages.size()) {
                localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(i);
            }
            if (localeInfo == null || localeInfo.pathToFile == null || LanguageSelectActivity.this.getParentActivity() == null) {
                return false;
            }
            LocaleInfo finalLocaleInfo = localeInfo;
            Builder builder = new Builder(LanguageSelectActivity.this.getParentActivity());
            builder.setMessage(LocaleController.getString("DeleteLocalization", C0691R.string.DeleteLocalization));
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("Delete", C0691R.string.Delete), new C12571(finalLocaleInfo));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            LanguageSelectActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.6 */
    class C12596 implements OnScrollListener {
        C12596() {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 && LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.7 */
    class C12607 extends TimerTask {
        final /* synthetic */ String val$query;

        C12607(String str) {
            this.val$query = str;
        }

        public void run() {
            try {
                LanguageSelectActivity.this.searchTimer.cancel();
                LanguageSelectActivity.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            LanguageSelectActivity.this.processSearch(this.val$query);
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.8 */
    class C12618 implements Runnable {
        final /* synthetic */ String val$query;

        C12618(String str) {
            this.val$query = str;
        }

        public void run() {
            if (this.val$query.trim().toLowerCase().length() == 0) {
                LanguageSelectActivity.this.updateSearchResults(new ArrayList());
                return;
            }
            long time = System.currentTimeMillis();
            ArrayList<LocaleInfo> resultArray = new ArrayList();
            Iterator i$ = LocaleController.getInstance().sortedLanguages.iterator();
            while (i$.hasNext()) {
                LocaleInfo c = (LocaleInfo) i$.next();
                if (c.name.toLowerCase().startsWith(this.val$query) || c.nameEnglish.toLowerCase().startsWith(this.val$query)) {
                    resultArray.add(c);
                }
            }
            LanguageSelectActivity.this.updateSearchResults(resultArray);
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.9 */
    class C12629 implements Runnable {
        final /* synthetic */ ArrayList val$arrCounties;

        C12629(ArrayList arrayList) {
            this.val$arrCounties = arrayList;
        }

        public void run() {
            LanguageSelectActivity.this.searchResult = this.val$arrCounties;
            LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.1 */
    class C18661 extends ActionBarMenuOnItemClick {
        C18661() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                LanguageSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity.2 */
    class C18672 extends ActionBarMenuItemSearchListener {
        C18672() {
        }

        public void onSearchExpand() {
            LanguageSelectActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            LanguageSelectActivity.this.search(null);
            LanguageSelectActivity.this.searching = false;
            LanguageSelectActivity.this.searchWas = false;
            if (LanguageSelectActivity.this.listView != null) {
                LanguageSelectActivity.this.emptyTextView.setVisibility(8);
                LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
            }
        }

        public void onTextChanged(EditText editText) {
            String text = editText.getText().toString();
            LanguageSelectActivity.this.search(text);
            if (text.length() != 0) {
                LanguageSelectActivity.this.searchWas = true;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                }
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public int getCount() {
            if (LocaleController.getInstance().sortedLanguages == null) {
                return 0;
            }
            return LocaleController.getInstance().sortedLanguages.size();
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
            if (view == null) {
                view = new TextSettingsCell(this.mContext);
            }
            ((TextSettingsCell) view).setText(((LocaleInfo) LocaleController.getInstance().sortedLanguages.get(i)).name, i != LocaleController.getInstance().sortedLanguages.size() + -1);
            return view;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return LocaleController.getInstance().sortedLanguages == null || LocaleController.getInstance().sortedLanguages.size() == 0;
        }
    }

    private class SearchAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public int getCount() {
            if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            }
            return LanguageSelectActivity.this.searchResult.size();
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
            if (view == null) {
                view = new TextSettingsCell(this.mContext);
            }
            ((TextSettingsCell) view).setText(((LocaleInfo) LanguageSelectActivity.this.searchResult.get(i)).name, i != LanguageSelectActivity.this.searchResult.size() + -1);
            return view;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return LanguageSelectActivity.this.searchResult == null || LanguageSelectActivity.this.searchResult.size() == 0;
        }
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", C0691R.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new C18661());
        this.actionBar.createMenu().addItem(0, (int) C0691R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18672()).getSearchField().setHint(LocaleController.getString("Search", C0691R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchListViewAdapter = new SearchAdapter(context);
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
        emptyTextLayout.setOnTouchListener(new C12553());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoResult", C0691R.string.NoResult));
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
        this.listView = new ListView(context);
        this.listView.setEmptyView(emptyTextLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setAdapter(this.listAdapter);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C12564());
        this.listView.setOnItemLongClickListener(new C12585());
        this.listView.setOnScrollListener(new C12596());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void search(String query) {
        if (query == null) {
            this.searchResult = null;
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new C12607(query), 100, 300);
    }

    private void processSearch(String query) {
        Utilities.searchQueue.postRunnable(new C12618(query));
    }

    private void updateSearchResults(ArrayList<LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new C12629(arrCounties));
    }
}
