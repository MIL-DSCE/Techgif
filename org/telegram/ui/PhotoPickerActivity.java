package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.hockeyapp.android.UpdateActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.volley.AuthFailureError;
import org.telegram.messenger.volley.RequestQueue;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.toolbox.JsonObjectRequest;
import org.telegram.messenger.volley.toolbox.Volley;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.FoundGif;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_foundGifs;
import org.telegram.tgnet.TLRPC.TL_messages_searchGifs;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private ChatActivity chatActivity;
    private PhotoPickerActivityDelegate delegate;
    private TextView emptyView;
    private int giphyReqId;
    private boolean giphySearchEndReached;
    private int itemWidth;
    private String lastSearchString;
    private int lastSearchToken;
    private ListAdapter listAdapter;
    private GridView listView;
    private boolean loadingRecent;
    private int nextGiphySearchOffset;
    private String nextSearchBingString;
    private PickerBottomLayout pickerBottomLayout;
    private FrameLayout progressView;
    private ArrayList<SearchImage> recentImages;
    private RequestQueue requestQueue;
    private ActionBarMenuItem searchItem;
    private ArrayList<SearchImage> searchResult;
    private HashMap<String, SearchImage> searchResultKeys;
    private HashMap<String, SearchImage> searchResultUrls;
    private boolean searching;
    private AlbumEntry selectedAlbum;
    private HashMap<Integer, PhotoEntry> selectedPhotos;
    private HashMap<String, SearchImage> selectedWebPhotos;
    private boolean sendPressed;
    private boolean singlePhoto;
    private int type;

    /* renamed from: org.telegram.ui.PhotoPickerActivity.3 */
    class C13573 implements OnItemClickListener {
        C13573() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (PhotoPickerActivity.this.selectedAlbum == null || !PhotoPickerActivity.this.selectedAlbum.isVideo) {
                ArrayList<Object> arrayList;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    arrayList = PhotoPickerActivity.this.selectedAlbum.photos;
                } else if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    arrayList = PhotoPickerActivity.this.recentImages;
                } else {
                    arrayList = PhotoPickerActivity.this.searchResult;
                }
                if (i >= 0 && i < arrayList.size()) {
                    int i2;
                    if (PhotoPickerActivity.this.searchItem != null) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
                    }
                    PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
                    PhotoViewer instance = PhotoViewer.getInstance();
                    if (PhotoPickerActivity.this.singlePhoto) {
                        i2 = 1;
                    } else {
                        i2 = 0;
                    }
                    instance.openPhotoForSelect(arrayList, i, i2, PhotoPickerActivity.this, PhotoPickerActivity.this.chatActivity);
                }
            } else if (i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.delegate.didSelectVideo(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i)).path)) {
                PhotoPickerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.4 */
    class C13594 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.PhotoPickerActivity.4.1 */
        class C13581 implements OnClickListener {
            C13581() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                PhotoPickerActivity.this.recentImages.clear();
                if (PhotoPickerActivity.this.listAdapter != null) {
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                }
                MessagesStorage.getInstance().clearWebRecent(PhotoPickerActivity.this.type);
            }
        }

        C13594() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                return false;
            }
            Builder builder = new Builder(PhotoPickerActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearSearch", C0691R.string.ClearSearch));
            builder.setPositiveButton(LocaleController.getString("ClearButton", C0691R.string.ClearButton).toUpperCase(), new C13581());
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            PhotoPickerActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.5 */
    class C13605 implements OnTouchListener {
        C13605() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.6 */
    class C13616 implements OnScrollListener {
        C13616() {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !PhotoPickerActivity.this.searching) {
                if (PhotoPickerActivity.this.type == 0 && PhotoPickerActivity.this.nextSearchBingString != null) {
                    PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
                } else if (PhotoPickerActivity.this.type == 1 && !PhotoPickerActivity.this.giphySearchEndReached) {
                    PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.7 */
    class C13627 implements View.OnClickListener {
        C13627() {
        }

        public void onClick(View view) {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true);
            PhotoPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.8 */
    class C13638 implements View.OnClickListener {
        C13638() {
        }

        public void onClick(View view) {
            PhotoPickerActivity.this.sendSelectedPhotos();
        }
    }

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z);

        boolean didSelectVideo(String str);

        void selectedPhotosChanged();
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.1 */
    class C19051 extends ActionBarMenuOnItemClick {
        C19051() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PhotoPickerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.2 */
    class C19062 extends ActionBarMenuItemSearchListener {
        C19062() {
        }

        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            PhotoPickerActivity.this.finishFragment();
            return false;
        }

        public void onTextChanged(EditText editText) {
            if (editText.getText().length() == 0) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.lastSearchString = null;
                PhotoPickerActivity.this.nextSearchBingString = null;
                PhotoPickerActivity.this.giphySearchEndReached = true;
                PhotoPickerActivity.this.searching = false;
                PhotoPickerActivity.this.requestQueue.cancelAll((Object) "search");
                if (PhotoPickerActivity.this.type == 0) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0691R.string.NoRecentPhotos));
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0691R.string.NoRecentGIFs));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }

        public void onSearchPressed(EditText editText) {
            if (editText.getText().toString().length() != 0) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.nextSearchBingString = null;
                PhotoPickerActivity.this.giphySearchEndReached = true;
                if (PhotoPickerActivity.this.type == 0) {
                    PhotoPickerActivity.this.searchBingImages(editText.getText().toString(), 0, 53);
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.nextGiphySearchOffset = 0;
                    PhotoPickerActivity.this.searchGiphyImages(editText.getText().toString(), 0);
                }
                PhotoPickerActivity.this.lastSearchString = editText.getText().toString();
                if (PhotoPickerActivity.this.lastSearchString.length() == 0) {
                    PhotoPickerActivity.this.lastSearchString = null;
                    if (PhotoPickerActivity.this.type == 0) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0691R.string.NoRecentPhotos));
                    } else if (PhotoPickerActivity.this.type == 1) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0691R.string.NoRecentGIFs));
                    }
                } else {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0691R.string.NoResult));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.9 */
    class C19079 implements RequestDelegate {
        final /* synthetic */ String val$query;
        final /* synthetic */ int val$token;

        /* renamed from: org.telegram.ui.PhotoPickerActivity.9.1 */
        class C13641 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C13641(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = true;
                if (C19079.this.val$token == PhotoPickerActivity.this.lastSearchToken) {
                    if (this.val$response != null) {
                        boolean added = false;
                        TL_messages_foundGifs res = this.val$response;
                        PhotoPickerActivity.this.nextGiphySearchOffset = res.next_offset;
                        for (int a = 0; a < res.results.size(); a++) {
                            FoundGif gif = (FoundGif) res.results.get(a);
                            if (!PhotoPickerActivity.this.searchResultKeys.containsKey(gif.url)) {
                                added = true;
                                SearchImage bingImage = new SearchImage();
                                bingImage.id = gif.url;
                                if (gif.document != null) {
                                    for (int b = 0; b < gif.document.attributes.size(); b++) {
                                        DocumentAttribute attribute = (DocumentAttribute) gif.document.attributes.get(b);
                                        if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                            bingImage.width = attribute.f29w;
                                            bingImage.height = attribute.f28h;
                                            break;
                                        }
                                    }
                                } else {
                                    bingImage.width = gif.f31w;
                                    bingImage.height = gif.f30h;
                                }
                                bingImage.size = 0;
                                bingImage.imageUrl = gif.content_url;
                                bingImage.thumbUrl = gif.thumb_url;
                                bingImage.localUrl = gif.url + "|" + C19079.this.val$query;
                                bingImage.document = gif.document;
                                if (!(gif.photo == null || gif.document == null)) {
                                    PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(gif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                                    if (size != null) {
                                        gif.document.thumb = size;
                                    }
                                }
                                bingImage.type = 1;
                                PhotoPickerActivity.this.searchResult.add(bingImage);
                                PhotoPickerActivity.this.searchResultKeys.put(bingImage.id, bingImage);
                            }
                        }
                        PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                        if (added) {
                            z = false;
                        }
                        photoPickerActivity.giphySearchEndReached = z;
                    }
                    PhotoPickerActivity.this.searching = false;
                    PhotoPickerActivity.this.updateSearchInterface();
                }
            }
        }

        C19079(int i, String str) {
            this.val$token = i;
            this.val$query = str;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C13641(response));
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.PhotoPickerActivity.ListAdapter.1 */
        class C13651 implements View.OnClickListener {
            C13651() {
            }

            public void onClick(View v) {
                int index = ((Integer) ((View) v.getParent()).getTag()).intValue();
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                    if (PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                        PhotoPickerActivity.this.selectedPhotos.remove(Integer.valueOf(photoEntry.imageId));
                        photoEntry.imagePath = null;
                        photoEntry.thumbPath = null;
                        PhotoPickerActivity.this.updatePhotoAtIndex(index);
                    } else {
                        PhotoPickerActivity.this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                    }
                    ((PhotoPickerPhotoCell) v.getParent()).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), true);
                } else {
                    SearchImage photoEntry2;
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(((Integer) ((View) v.getParent()).getTag()).intValue());
                    } else {
                        photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(((Integer) ((View) v.getParent()).getTag()).intValue());
                    }
                    if (PhotoPickerActivity.this.selectedWebPhotos.containsKey(photoEntry2.id)) {
                        PhotoPickerActivity.this.selectedWebPhotos.remove(photoEntry2.id);
                        photoEntry2.imagePath = null;
                        photoEntry2.thumbPath = null;
                        PhotoPickerActivity.this.updatePhotoAtIndex(index);
                    } else {
                        PhotoPickerActivity.this.selectedWebPhotos.put(photoEntry2.id, photoEntry2);
                    }
                    ((PhotoPickerPhotoCell) v.getParent()).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(photoEntry2.id), true);
                }
                PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size() + PhotoPickerActivity.this.selectedWebPhotos.size(), true);
                PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return PhotoPickerActivity.this.selectedAlbum != null;
        }

        public boolean isEnabled(int i) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                if (i < PhotoPickerActivity.this.recentImages.size()) {
                    return true;
                }
                return false;
            } else if (i >= PhotoPickerActivity.this.searchResult.size()) {
                return false;
            } else {
                return true;
            }
        }

        public int getCount() {
            int i = 0;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    return PhotoPickerActivity.this.recentImages.size();
                }
                int size;
                if (PhotoPickerActivity.this.type == 0) {
                    size = PhotoPickerActivity.this.searchResult.size();
                    if (PhotoPickerActivity.this.nextSearchBingString != null) {
                        i = 1;
                    }
                    return i + size;
                } else if (PhotoPickerActivity.this.type == 1) {
                    size = PhotoPickerActivity.this.searchResult.size();
                    if (!PhotoPickerActivity.this.giphySearchEndReached) {
                        i = 1;
                    }
                    return i + size;
                }
            }
            return PhotoPickerActivity.this.selectedAlbum.photos.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                boolean showing;
                int i2;
                PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                if (view == null) {
                    view = new PhotoPickerPhotoCell(this.mContext);
                    cell = (PhotoPickerPhotoCell) view;
                    cell.checkFrame.setOnClickListener(new C13651());
                    cell.checkFrame.setVisibility(PhotoPickerActivity.this.singlePhoto ? 8 : 0);
                }
                cell.itemWidth = PhotoPickerActivity.this.itemWidth;
                BackupImageView imageView = ((PhotoPickerPhotoCell) view).photoImage;
                imageView.setTag(Integer.valueOf(i));
                view.setTag(Integer.valueOf(i));
                imageView.setOrientation(0, true);
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    if (photoEntry.thumbPath != null) {
                        imageView.setImage(photoEntry.thumbPath, null, this.mContext.getResources().getDrawable(C0691R.drawable.nophotos));
                    } else if (photoEntry.path != null) {
                        imageView.setOrientation(photoEntry.orientation, true);
                        if (photoEntry.isVideo) {
                            imageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(C0691R.drawable.nophotos));
                        } else {
                            imageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(C0691R.drawable.nophotos));
                        }
                    } else {
                        imageView.setImageResource(C0691R.drawable.nophotos);
                    }
                    cell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    showing = PhotoViewer.getInstance().isShowingImage(photoEntry.path);
                } else {
                    SearchImage photoEntry2;
                    if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                        photoEntry2 = (SearchImage) PhotoPickerActivity.this.recentImages.get(i);
                    } else {
                        photoEntry2 = (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    }
                    if (photoEntry2.thumbPath != null) {
                        imageView.setImage(photoEntry2.thumbPath, null, this.mContext.getResources().getDrawable(C0691R.drawable.nophotos));
                    } else if (photoEntry2.thumbUrl != null && photoEntry2.thumbUrl.length() > 0) {
                        imageView.setImage(photoEntry2.thumbUrl, null, this.mContext.getResources().getDrawable(C0691R.drawable.nophotos));
                    } else if (photoEntry2.document == null || photoEntry2.document.thumb == null) {
                        imageView.setImageResource(C0691R.drawable.nophotos);
                    } else {
                        imageView.setImage(photoEntry2.document.thumb.location, null, this.mContext.getResources().getDrawable(C0691R.drawable.nophotos));
                    }
                    cell.setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(photoEntry2.id), false);
                    if (photoEntry2.document != null) {
                        showing = PhotoViewer.getInstance().isShowingImage(FileLoader.getPathToAttach(photoEntry2.document, true).getAbsolutePath());
                    } else {
                        showing = PhotoViewer.getInstance().isShowingImage(photoEntry2.imageUrl);
                    }
                }
                imageView.getImageReceiver().setVisible(!showing, true);
                CheckBox checkBox = cell.checkBox;
                if (PhotoPickerActivity.this.singlePhoto || showing) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                checkBox.setVisibility(i2);
            } else if (viewType == 1) {
                if (view == null) {
                    view = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0691R.layout.media_loading_layout, viewGroup, false);
                }
                LayoutParams params = view.getLayoutParams();
                params.width = PhotoPickerActivity.this.itemWidth;
                params.height = PhotoPickerActivity.this.itemWidth;
                view.setLayoutParams(params);
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (PhotoPickerActivity.this.selectedAlbum != null || ((PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null && i < PhotoPickerActivity.this.recentImages.size()) || i < PhotoPickerActivity.this.searchResult.size())) {
                return 0;
            }
            return 1;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEmpty() {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return PhotoPickerActivity.this.selectedAlbum.photos.isEmpty();
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                return PhotoPickerActivity.this.recentImages.isEmpty();
            }
            return PhotoPickerActivity.this.searchResult.isEmpty();
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity.12 */
    class AnonymousClass12 extends JsonObjectRequest {
        AnonymousClass12(int x0, String x1, JSONObject x2, Listener x3, ErrorListener x4) {
            super(x0, x1, x2, x3, x4);
        }

        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap();
            headers.put("Authorization", "Basic " + Base64.encodeToString((BuildVars.BING_SEARCH_KEY + ":" + BuildVars.BING_SEARCH_KEY).getBytes(), 2));
            return headers;
        }
    }

    static /* synthetic */ String access$384(PhotoPickerActivity x0, Object x1) {
        String str = x0.nextSearchBingString + x1;
        x0.nextSearchBingString = str;
        return str;
    }

    public PhotoPickerActivity(int type, AlbumEntry selectedAlbum, HashMap<Integer, PhotoEntry> selectedPhotos, HashMap<String, SearchImage> selectedWebPhotos, ArrayList<SearchImage> recentImages, boolean onlyOnePhoto, ChatActivity chatActivity) {
        this.searchResult = new ArrayList();
        this.searchResultKeys = new HashMap();
        this.searchResultUrls = new HashMap();
        this.giphySearchEndReached = true;
        this.itemWidth = 100;
        this.selectedAlbum = selectedAlbum;
        this.selectedPhotos = selectedPhotos;
        this.selectedWebPhotos = selectedWebPhotos;
        this.type = type;
        this.recentImages = recentImages;
        this.singlePhoto = onlyOnePhoto;
        this.chatActivity = chatActivity;
        if (selectedAlbum != null && selectedAlbum.isVideo) {
            this.singlePhoto = true;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.selectedAlbum == null) {
            this.requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
            if (this.recentImages.isEmpty()) {
                MessagesStorage.getInstance().loadWebRecent(this.type);
                this.loadingRecent = true;
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.requestQueue != null) {
            this.requestQueue.cancelAll((Object) "search");
            this.requestQueue.stop();
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        int i = 0;
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        if (this.selectedAlbum != null) {
            this.actionBar.setTitle(this.selectedAlbum.bucketName);
        } else if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", C0691R.string.SearchImagesTitle));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", C0691R.string.SearchGifsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19051());
        if (this.selectedAlbum == null) {
            this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0691R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19062());
        }
        if (this.selectedAlbum == null) {
            if (this.type == 0) {
                this.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", C0691R.string.SearchImagesTitle));
            } else if (this.type == 1) {
                this.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", C0691R.string.SearchGifsTitle));
            }
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.listView = new GridView(context);
        this.listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.listView.setClipToPadding(false);
        this.listView.setDrawSelectorOnTop(true);
        this.listView.setStretchMode(2);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setNumColumns(-1);
        this.listView.setVerticalSpacing(AndroidUtilities.dp(4.0f));
        this.listView.setHorizontalSpacing(AndroidUtilities.dp(4.0f));
        this.listView.setSelector(C0691R.drawable.list_selector);
        frameLayout.addView(this.listView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = this.singlePhoto ? 0 : AndroidUtilities.dp(48.0f);
        this.listView.setLayoutParams(layoutParams);
        GridView gridView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        gridView.setAdapter(listAdapter);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, -13421773);
        this.listView.setOnItemClickListener(new C13573());
        if (this.selectedAlbum == null) {
            this.listView.setOnItemLongClickListener(new C13594());
        }
        this.emptyView = new TextView(context);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        if (this.selectedAlbum != null) {
            this.emptyView.setText(LocaleController.getString("NoPhotos", C0691R.string.NoPhotos));
        } else if (this.type == 0) {
            this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0691R.string.NoRecentPhotos));
        } else if (this.type == 1) {
            this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0691R.string.NoRecentGIFs));
        }
        frameLayout.addView(this.emptyView);
        layoutParams = (FrameLayout.LayoutParams) this.emptyView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = this.singlePhoto ? 0 : AndroidUtilities.dp(48.0f);
        this.emptyView.setLayoutParams(layoutParams);
        this.emptyView.setOnTouchListener(new C13605());
        if (this.selectedAlbum == null) {
            this.listView.setOnScrollListener(new C13616());
            this.progressView = new FrameLayout(context);
            this.progressView.setVisibility(8);
            frameLayout.addView(this.progressView);
            layoutParams = (FrameLayout.LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            if (!this.singlePhoto) {
                i = AndroidUtilities.dp(48.0f);
            }
            layoutParams.bottomMargin = i;
            this.progressView.setLayoutParams(layoutParams);
            ProgressBar progressBar = new ProgressBar(context);
            this.progressView.addView(progressBar);
            layoutParams = (FrameLayout.LayoutParams) progressBar.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.gravity = 17;
            progressBar.setLayoutParams(layoutParams);
            updateSearchInterface();
        }
        this.pickerBottomLayout = new PickerBottomLayout(context);
        frameLayout.addView(this.pickerBottomLayout);
        layoutParams = (FrameLayout.LayoutParams) this.pickerBottomLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        layoutParams.gravity = 80;
        this.pickerBottomLayout.setLayoutParams(layoutParams);
        this.pickerBottomLayout.cancelButton.setOnClickListener(new C13627());
        this.pickerBottomLayout.doneButton.setOnClickListener(new C13638());
        if (this.singlePhoto) {
            this.pickerBottomLayout.setVisibility(8);
        }
        this.listView.setEmptyView(this.emptyView);
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.searchItem != null) {
            this.searchItem.openSearch(true);
            getParentActivity().getWindow().setSoftInputMode(32);
        }
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.recentImagesDidLoaded && this.selectedAlbum == null && this.type == ((Integer) args[0]).intValue()) {
            this.recentImages = (ArrayList) args[1];
            this.loadingRecent = false;
            updateSearchInterface();
        }
    }

    private PhotoPickerPhotoCell getCellForIndex(int index) {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoPickerPhotoCell) {
                PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                int num = ((Integer) cell.photoImage.getTag()).intValue();
                if (this.selectedAlbum == null) {
                    ArrayList<SearchImage> array;
                    if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                        array = this.recentImages;
                    } else {
                        array = this.searchResult;
                    }
                    if (num < 0) {
                        continue;
                    } else if (num >= array.size()) {
                        continue;
                    }
                } else if (num < 0) {
                    continue;
                } else if (num >= this.selectedAlbum.photos.size()) {
                    continue;
                }
                if (num == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        PhotoPickerPhotoCell cell = getCellForIndex(index);
        if (cell == null) {
            return null;
        }
        int[] coords = new int[2];
        cell.photoImage.getLocationInWindow(coords);
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = coords[0];
        object.viewY = coords[1] - AndroidUtilities.statusBarHeight;
        object.parentView = this.listView;
        object.imageReceiver = cell.photoImage.getImageReceiver();
        object.thumb = object.imageReceiver.getBitmap();
        object.scale = cell.photoImage.getScaleX();
        cell.checkBox.setVisibility(8);
        return object;
    }

    public void updatePhotoAtIndex(int index) {
        PhotoPickerPhotoCell cell = getCellForIndex(index);
        if (cell == null) {
            return;
        }
        if (this.selectedAlbum != null) {
            cell.photoImage.setOrientation(0, true);
            PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(index);
            if (photoEntry.thumbPath != null) {
                cell.photoImage.setImage(photoEntry.thumbPath, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
                return;
            } else if (photoEntry.path != null) {
                cell.photoImage.setOrientation(photoEntry.orientation, true);
                if (photoEntry.isVideo) {
                    cell.photoImage.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
                    return;
                } else {
                    cell.photoImage.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
                    return;
                }
            } else {
                cell.photoImage.setImageResource(C0691R.drawable.nophotos);
                return;
            }
        }
        ArrayList<SearchImage> array;
        if (this.searchResult.isEmpty() && this.lastSearchString == null) {
            array = this.recentImages;
        } else {
            array = this.searchResult;
        }
        SearchImage photoEntry2 = (SearchImage) array.get(index);
        if (photoEntry2.document != null && photoEntry2.document.thumb != null) {
            cell.photoImage.setImage(photoEntry2.document.thumb.location, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
        } else if (photoEntry2.thumbPath != null) {
            cell.photoImage.setImage(photoEntry2.thumbPath, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
        } else if (photoEntry2.thumbUrl == null || photoEntry2.thumbUrl.length() <= 0) {
            cell.photoImage.setImageResource(C0691R.drawable.nophotos);
        } else {
            cell.photoImage.setImage(photoEntry2.thumbUrl, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
        }
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        PhotoPickerPhotoCell cell = getCellForIndex(index);
        if (cell != null) {
            return cell.photoImage.getImageReceiver().getBitmap();
        }
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view.getTag() != null) {
                PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) view;
                int num = ((Integer) view.getTag()).intValue();
                if (this.selectedAlbum == null) {
                    ArrayList<SearchImage> array;
                    if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                        array = this.recentImages;
                    } else {
                        array = this.searchResult;
                    }
                    if (num < 0) {
                        continue;
                    } else if (num >= array.size()) {
                    }
                } else if (num < 0) {
                    continue;
                } else if (num >= this.selectedAlbum.photos.size()) {
                    continue;
                }
                if (num == index) {
                    cell.checkBox.setVisibility(0);
                    return;
                }
            }
        }
    }

    public void willHidePhotoViewer() {
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public boolean isPhotoChecked(int index) {
        boolean z = true;
        if (this.selectedAlbum != null) {
            return index >= 0 && index < this.selectedAlbum.photos.size() && this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) this.selectedAlbum.photos.get(index)).imageId));
        } else {
            ArrayList<SearchImage> array;
            if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                array = this.recentImages;
            } else {
                array = this.searchResult;
            }
            if (index < 0 || index >= array.size() || !this.selectedWebPhotos.containsKey(((SearchImage) array.get(index)).id)) {
                z = false;
            }
            return z;
        }
    }

    public void setPhotoChecked(int index) {
        boolean add = true;
        if (this.selectedAlbum == null) {
            ArrayList<SearchImage> array;
            if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                array = this.recentImages;
            } else {
                array = this.searchResult;
            }
            if (index >= 0 && index < array.size()) {
                SearchImage photoEntry = (SearchImage) array.get(index);
                if (this.selectedWebPhotos.containsKey(photoEntry.id)) {
                    this.selectedWebPhotos.remove(photoEntry.id);
                    add = false;
                } else {
                    this.selectedWebPhotos.put(photoEntry.id, photoEntry);
                }
            } else {
                return;
            }
        } else if (index >= 0 && index < this.selectedAlbum.photos.size()) {
            PhotoEntry photoEntry2 = (PhotoEntry) this.selectedAlbum.photos.get(index);
            if (this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId))) {
                this.selectedPhotos.remove(Integer.valueOf(photoEntry2.imageId));
                add = false;
            } else {
                this.selectedPhotos.put(Integer.valueOf(photoEntry2.imageId), photoEntry2);
            }
        } else {
            return;
        }
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (((Integer) view.getTag()).intValue() == index) {
                ((PhotoPickerPhotoCell) view).setChecked(add, false);
                break;
            }
        }
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        this.delegate.selectedPhotosChanged();
    }

    public boolean cancelButtonPressed() {
        this.delegate.actionButtonPressed(true);
        finishFragment();
        return true;
    }

    public void sendButtonPressed(int index) {
        if (this.selectedAlbum != null) {
            if (this.selectedPhotos.isEmpty()) {
                if (index >= 0 && index < this.selectedAlbum.photos.size()) {
                    PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(index);
                    this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                } else {
                    return;
                }
            }
        } else if (this.selectedPhotos.isEmpty()) {
            ArrayList<SearchImage> array;
            if (this.searchResult.isEmpty() && this.lastSearchString == null) {
                array = this.recentImages;
            } else {
                array = this.searchResult;
            }
            if (index >= 0 && index < array.size()) {
                SearchImage photoEntry2 = (SearchImage) array.get(index);
                this.selectedWebPhotos.put(photoEntry2.id, photoEntry2);
            } else {
                return;
            }
        }
        sendSelectedPhotos();
    }

    public int getSelectedCount() {
        return this.selectedPhotos.size() + this.selectedWebPhotos.size();
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.searchItem != null) {
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
        }
    }

    private void updateSearchInterface() {
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView(null);
            this.emptyView.setVisibility(8);
            return;
        }
        this.progressView.setVisibility(8);
        this.emptyView.setVisibility(0);
        this.listView.setEmptyView(this.emptyView);
    }

    private void searchGiphyImages(String query, int offset) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            this.requestQueue.cancelAll((Object) "search");
        }
        this.searching = true;
        TL_messages_searchGifs req = new TL_messages_searchGifs();
        req.f38q = query;
        req.offset = offset;
        int token = this.lastSearchToken + 1;
        this.lastSearchToken = token;
        this.giphyReqId = ConnectionsManager.getInstance().sendRequest(req, new C19079(token, query));
        ConnectionsManager.getInstance().bindRequestToGuid(this.giphyReqId, this.classGuid);
    }

    private void searchBingImages(String query, int offset, int count) {
        boolean adult = true;
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            this.requestQueue.cancelAll((Object) "search");
        }
        try {
            String url;
            this.searching = true;
            if (this.nextSearchBingString != null) {
                url = this.nextSearchBingString;
            } else {
                String phone = UserConfig.getCurrentUser().phone;
                if (!(phone.startsWith("44") || phone.startsWith("49") || phone.startsWith("43") || phone.startsWith("31") || phone.startsWith("1"))) {
                    adult = false;
                }
                Locale locale = Locale.US;
                String str = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query='%s'&$skip=%d&$top=%d&$format=json%s";
                Object[] objArr = new Object[4];
                objArr[0] = URLEncoder.encode(query, C0747C.UTF8_NAME);
                objArr[1] = Integer.valueOf(offset);
                objArr[2] = Integer.valueOf(count);
                objArr[3] = adult ? TtmlNode.ANONYMOUS_REGION_ID : "&Adult='Off'";
                url = String.format(locale, str, objArr);
            }
            JsonObjectRequest jsonObjReq = new AnonymousClass12(0, url, null, new Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    PhotoPickerActivity.this.nextSearchBingString = null;
                    JSONObject d = response.getJSONObject("d");
                    JSONArray result = d.getJSONArray("results");
                    try {
                        PhotoPickerActivity.this.nextSearchBingString = d.getString("__next");
                    } catch (Throwable e) {
                        PhotoPickerActivity.this.nextSearchBingString = null;
                        FileLog.m13e("tmessages", e);
                    }
                    for (int a = 0; a < result.length(); a++) {
                        try {
                            JSONObject object = result.getJSONObject(a);
                            String id = Utilities.MD5(object.getString("MediaUrl"));
                            if (!PhotoPickerActivity.this.searchResultKeys.containsKey(id)) {
                                SearchImage bingImage = new SearchImage();
                                bingImage.id = id;
                                bingImage.width = object.getInt("Width");
                                bingImage.height = object.getInt("Height");
                                bingImage.size = object.getInt("FileSize");
                                bingImage.imageUrl = object.getString("MediaUrl");
                                bingImage.thumbUrl = object.getJSONObject("Thumbnail").getString("MediaUrl");
                                PhotoPickerActivity.this.searchResult.add(bingImage);
                                PhotoPickerActivity.this.searchResultKeys.put(id, bingImage);
                            }
                        } catch (Throwable e2) {
                            try {
                                FileLog.m13e("tmessages", e2);
                            } catch (Throwable e22) {
                                FileLog.m13e("tmessages", e22);
                            }
                        }
                    }
                    PhotoPickerActivity.this.searching = false;
                    if (!(PhotoPickerActivity.this.nextSearchBingString == null || PhotoPickerActivity.this.nextSearchBingString.contains(UpdateActivity.EXTRA_JSON))) {
                        PhotoPickerActivity.access$384(PhotoPickerActivity.this, "&$format=json");
                    }
                    PhotoPickerActivity.this.updateSearchInterface();
                }
            }, new ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    FileLog.m11e("tmessages", "Error: " + error.getMessage());
                    PhotoPickerActivity.this.nextSearchBingString = null;
                    PhotoPickerActivity.this.searching = false;
                    PhotoPickerActivity.this.updateSearchInterface();
                }
            });
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setTag("search");
            this.requestQueue.add(jsonObjReq);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            this.nextSearchBingString = null;
            this.searching = false;
            updateSearchInterface();
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private void sendSelectedPhotos() {
        if ((!this.selectedPhotos.isEmpty() || !this.selectedWebPhotos.isEmpty()) && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false);
            finishFragment();
        }
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoPickerActivity.this.fixLayoutInternal();
                    if (PhotoPickerActivity.this.listView != null) {
                        PhotoPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int columnsCount;
            int position = this.listView.getFirstVisiblePosition();
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet()) {
                columnsCount = 3;
            } else if (rotation == 3 || rotation == 1) {
                columnsCount = 5;
            } else {
                columnsCount = 3;
            }
            this.listView.setNumColumns(columnsCount);
            if (AndroidUtilities.isTablet()) {
                this.itemWidth = (AndroidUtilities.dp(490.0f) - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
            } else {
                this.itemWidth = (AndroidUtilities.displaySize.x - ((columnsCount + 1) * AndroidUtilities.dp(4.0f))) / columnsCount;
            }
            this.listView.setColumnWidth(this.itemWidth);
            this.listAdapter.notifyDataSetChanged();
            this.listView.setSelection(position);
            if (this.selectedAlbum == null) {
                this.emptyView.setPadding(0, 0, 0, (int) (((float) (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight())) * 0.4f));
            }
        }
    }
}
