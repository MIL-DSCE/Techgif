package org.telegram.ui.Adapters;

import android.location.Location;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.volley.RequestQueue;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.toolbox.JsonObjectRequest;
import org.telegram.messenger.volley.toolbox.Volley;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;

public class BaseLocationAdapter extends BaseFragmentAdapter {
    private BaseLocationAdapterDelegate delegate;
    protected ArrayList<String> iconUrls;
    private Location lastSearchLocation;
    protected ArrayList<TL_messageMediaVenue> places;
    private RequestQueue requestQueue;
    private Timer searchTimer;
    protected boolean searching;

    /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter.1 */
    class C09171 extends TimerTask {
        final /* synthetic */ Location val$coordinate;
        final /* synthetic */ String val$query;

        /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter.1.1 */
        class C09161 implements Runnable {
            C09161() {
            }

            public void run() {
                BaseLocationAdapter.this.lastSearchLocation = null;
                BaseLocationAdapter.this.searchGooglePlacesWithQuery(C09171.this.val$query, C09171.this.val$coordinate);
            }
        }

        C09171(String str, Location location) {
            this.val$query = str;
            this.val$coordinate = location;
        }

        public void run() {
            try {
                BaseLocationAdapter.this.searchTimer.cancel();
                BaseLocationAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C09161());
        }
    }

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList);
    }

    /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter.2 */
    class C17492 implements Listener<JSONObject> {
        C17492() {
        }

        public void onResponse(JSONObject response) {
            try {
                BaseLocationAdapter.this.places.clear();
                BaseLocationAdapter.this.iconUrls.clear();
                JSONArray result = response.getJSONObject("response").getJSONArray("venues");
                for (int a = 0; a < result.length(); a++) {
                    try {
                        Object[] objArr;
                        JSONObject object = result.getJSONObject(a);
                        String iconUrl = null;
                        if (object.has("categories")) {
                            JSONArray categories = object.getJSONArray("categories");
                            if (categories.length() > 0) {
                                JSONObject category = categories.getJSONObject(0);
                                if (category.has("icon")) {
                                    JSONObject icon = category.getJSONObject("icon");
                                    objArr = new Object[2];
                                    objArr[0] = icon.getString("prefix");
                                    objArr[1] = icon.getString("suffix");
                                    iconUrl = String.format(Locale.US, "%s64%s", objArr);
                                }
                            }
                        }
                        BaseLocationAdapter.this.iconUrls.add(iconUrl);
                        JSONObject location = object.getJSONObject("location");
                        TL_messageMediaVenue venue = new TL_messageMediaVenue();
                        venue.geo = new TL_geoPoint();
                        venue.geo.lat = location.getDouble("lat");
                        venue.geo._long = location.getDouble("lng");
                        if (location.has("address")) {
                            venue.address = location.getString("address");
                        } else if (location.has("city")) {
                            venue.address = location.getString("city");
                        } else if (location.has("state")) {
                            venue.address = location.getString("state");
                        } else if (location.has("country")) {
                            venue.address = location.getString("country");
                        } else {
                            objArr = new Object[2];
                            objArr[0] = Double.valueOf(venue.geo.lat);
                            objArr[1] = Double.valueOf(venue.geo._long);
                            venue.address = String.format(Locale.US, "%f,%f", objArr);
                        }
                        if (object.has("name")) {
                            venue.title = object.getString("name");
                        }
                        venue.venue_id = object.getString(TtmlNode.ATTR_ID);
                        venue.provider = "foursquare";
                        BaseLocationAdapter.this.places.add(venue);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            BaseLocationAdapter.this.searching = false;
            BaseLocationAdapter.this.notifyDataSetChanged();
            if (BaseLocationAdapter.this.delegate != null) {
                BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter.3 */
    class C17503 implements ErrorListener {
        C17503() {
        }

        public void onErrorResponse(VolleyError error) {
            FileLog.m11e("tmessages", "Error: " + error.getMessage());
            BaseLocationAdapter.this.searching = false;
            BaseLocationAdapter.this.notifyDataSetChanged();
            if (BaseLocationAdapter.this.delegate != null) {
                BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
            }
        }
    }

    public BaseLocationAdapter() {
        this.places = new ArrayList();
        this.iconUrls = new ArrayList();
        this.requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
    }

    public void destroy() {
        if (this.requestQueue != null) {
            this.requestQueue.cancelAll((Object) "search");
            this.requestQueue.stop();
        }
    }

    public void setDelegate(BaseLocationAdapterDelegate delegate) {
        this.delegate = delegate;
    }

    public void searchDelayed(String query, Location coordinate) {
        if (query == null || query.length() == 0) {
            this.places.clear();
            notifyDataSetChanged();
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
        this.searchTimer.schedule(new C09171(query, coordinate), 200, 500);
    }

    public void searchGooglePlacesWithQuery(String query, Location coordinate) {
        if (this.lastSearchLocation != null) {
            if (coordinate.distanceTo(this.lastSearchLocation) < 200.0f) {
                return;
            }
        }
        this.lastSearchLocation = coordinate;
        if (this.searching) {
            this.searching = false;
            this.requestQueue.cancelAll((Object) "search");
        }
        try {
            this.searching = true;
            Object[] objArr = new Object[4];
            objArr[0] = BuildVars.FOURSQUARE_API_VERSION;
            objArr[1] = BuildVars.FOURSQUARE_API_ID;
            objArr[2] = BuildVars.FOURSQUARE_API_KEY;
            objArr[3] = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(coordinate.getLatitude()), Double.valueOf(coordinate.getLongitude())});
            String url = String.format(Locale.US, "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s", objArr);
            if (query != null && query.length() > 0) {
                url = url + "&query=" + URLEncoder.encode(query, C0747C.UTF8_NAME);
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(0, url, null, new C17492(), new C17503());
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setTag("search");
            this.requestQueue.add(jsonObjReq);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            this.searching = false;
            if (this.delegate != null) {
                this.delegate.didLoadedSearchResult(this.places);
            }
        }
        notifyDataSetChanged();
    }
}
