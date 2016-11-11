package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Adapters.CountryAdapter.Country;
import org.telegram.ui.Cells.TextSettingsCell;

public class CountrySearchAdapter extends BaseFragmentAdapter {
    private HashMap<String, ArrayList<Country>> countries;
    private Context mContext;
    private ArrayList<Country> searchResult;
    private Timer searchTimer;

    /* renamed from: org.telegram.ui.Adapters.CountrySearchAdapter.1 */
    class C09321 extends TimerTask {
        final /* synthetic */ String val$query;

        C09321(String str) {
            this.val$query = str;
        }

        public void run() {
            try {
                CountrySearchAdapter.this.searchTimer.cancel();
                CountrySearchAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            CountrySearchAdapter.this.processSearch(this.val$query);
        }
    }

    /* renamed from: org.telegram.ui.Adapters.CountrySearchAdapter.2 */
    class C09332 implements Runnable {
        final /* synthetic */ String val$query;

        C09332(String str) {
            this.val$query = str;
        }

        public void run() {
            if (this.val$query.trim().toLowerCase().length() == 0) {
                CountrySearchAdapter.this.updateSearchResults(new ArrayList());
                return;
            }
            ArrayList<Country> resultArray = new ArrayList();
            ArrayList<Country> arr = (ArrayList) CountrySearchAdapter.this.countries.get(this.val$query.substring(0, 1).toUpperCase());
            if (arr != null) {
                Iterator i$ = arr.iterator();
                while (i$.hasNext()) {
                    Country c = (Country) i$.next();
                    if (c.name.toLowerCase().startsWith(this.val$query)) {
                        resultArray.add(c);
                    }
                }
            }
            CountrySearchAdapter.this.updateSearchResults(resultArray);
        }
    }

    /* renamed from: org.telegram.ui.Adapters.CountrySearchAdapter.3 */
    class C09343 implements Runnable {
        final /* synthetic */ ArrayList val$arrCounties;

        C09343(ArrayList arrayList) {
            this.val$arrCounties = arrayList;
        }

        public void run() {
            CountrySearchAdapter.this.searchResult = this.val$arrCounties;
            CountrySearchAdapter.this.notifyDataSetChanged();
        }
    }

    public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> countries) {
        this.mContext = context;
        this.countries = countries;
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
        this.searchTimer.schedule(new C09321(query), 100, 300);
    }

    private void processSearch(String query) {
        Utilities.searchQueue.postRunnable(new C09332(query));
    }

    private void updateSearchResults(ArrayList<Country> arrCounties) {
        AndroidUtilities.runOnUIThread(new C09343(arrCounties));
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int i) {
        return true;
    }

    public int getCount() {
        if (this.searchResult == null) {
            return 0;
        }
        return this.searchResult.size();
    }

    public Country getItem(int i) {
        if (i < 0 || i >= this.searchResult.size()) {
            return null;
        }
        return (Country) this.searchResult.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new TextSettingsCell(this.mContext);
        }
        Country c = (Country) this.searchResult.get(i);
        ((TextSettingsCell) view).setTextAndValue(c.name, "+" + c.code, i != this.searchResult.size() + -1);
        return view;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return this.searchResult == null || this.searchResult.size() == 0;
    }
}
