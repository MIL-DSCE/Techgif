package org.telegram.ui.Components;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.ui.StickerPreviewViewer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class EmojiView extends FrameLayout implements NotificationCenterDelegate {
    private static final OnScrollChangedListener NOP;
    private static HashMap<String, String> emojiColor;
    private static final Field superListenerField;
    private ArrayList<EmojiGridAdapter> adapters;
    private ImageView backspaceButton;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private int emojiSize;
    private HashMap<String, Integer> emojiUseHistory;
    private ExtendedGridLayoutManager flowLayoutManager;
    private int gifTabBum;
    private GifsAdapter gifsAdapter;
    private RecyclerListView gifsGridView;
    private int[] icons;
    private long lastGifLoadTime;
    private int lastNotifyWidth;
    private Listener listener;
    private boolean loadingRecent;
    private boolean loadingRecentGifs;
    private int[] location;
    private ArrayList<Long> newRecentStickers;
    private int oldWidth;
    private ViewPager pager;
    private LinearLayout pagerSlidingTabStripContainer;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private ArrayList<String> recentEmoji;
    private ArrayList<SearchImage> recentImages;
    private ArrayList<Document> recentStickers;
    private int recentTabBum;
    private FrameLayout recentsWrap;
    private ScrollSlidingTabStrip scrollSlidingTabStrip;
    private boolean showGifs;
    private boolean showStickers;
    private ArrayList<TL_messages_stickerSet> stickerSets;
    private TextView stickersEmptyView;
    private StickersGridAdapter stickersGridAdapter;
    private GridView stickersGridView;
    private OnItemClickListener stickersOnItemClickListener;
    private int stickersTabOffset;
    private FrameLayout stickersWrap;
    private boolean switchToGifTab;
    private ArrayList<GridView> views;

    /* renamed from: org.telegram.ui.Components.EmojiView.14 */
    class AnonymousClass14 extends LinearLayout {
        AnonymousClass14(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.16 */
    class AnonymousClass16 extends ImageView {
        AnonymousClass16(Context x0) {
            super(x0);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == 0) {
                EmojiView.this.backspacePressed = true;
                EmojiView.this.backspaceOnce = false;
                EmojiView.this.postBackspaceRunnable(350);
            } else if (event.getAction() == 3 || event.getAction() == 1) {
                EmojiView.this.backspacePressed = false;
                if (!(EmojiView.this.backspaceOnce || EmojiView.this.listener == null || !EmojiView.this.listener.onBackspace())) {
                    EmojiView.this.backspaceButton.performHapticFeedback(3);
                }
            }
            super.onTouchEvent(event);
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ int val$time;

        AnonymousClass18(int i) {
            this.val$time = i;
        }

        public void run() {
            if (EmojiView.this.backspacePressed) {
                if (EmojiView.this.listener != null && EmojiView.this.listener.onBackspace()) {
                    EmojiView.this.backspaceButton.performHapticFeedback(3);
                }
                EmojiView.this.backspaceOnce = true;
                EmojiView.this.postBackspaceRunnable(Math.max(50, this.val$time - 100));
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.1 */
    static class C11271 implements OnScrollChangedListener {
        C11271() {
        }

        public void onScrollChanged() {
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.20 */
    class AnonymousClass20 implements Comparator<Long> {
        final /* synthetic */ HashMap val$stickersUseHistory;

        AnonymousClass20(HashMap hashMap) {
            this.val$stickersUseHistory = hashMap;
        }

        public int compare(Long lhs, Long rhs) {
            Integer count1 = (Integer) this.val$stickersUseHistory.get(lhs);
            Integer count2 = (Integer) this.val$stickersUseHistory.get(rhs);
            if (count1 == null) {
                count1 = Integer.valueOf(0);
            }
            if (count2 == null) {
                count2 = Integer.valueOf(0);
            }
            if (count1.intValue() > count2.intValue()) {
                return -1;
            }
            if (count1.intValue() < count2.intValue()) {
                return 1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.2 */
    class C11292 extends GridView {
        C11292(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            return super.onInterceptTouchEvent(event) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight());
        }

        public void setVisibility(int visibility) {
            if (EmojiView.this.gifsGridView == null || EmojiView.this.gifsGridView.getVisibility() != 0) {
                super.setVisibility(visibility);
            } else {
                super.setVisibility(8);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.3 */
    class C11303 implements OnTouchListener {
        C11303() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return StickerPreviewViewer.getInstance().onTouch(event, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickersOnItemClickListener);
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.4 */
    class C11314 implements OnItemClickListener {
        C11314() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long i) {
            if (view instanceof StickerEmojiCell) {
                StickerPreviewViewer.getInstance().reset();
                StickerEmojiCell cell = (StickerEmojiCell) view;
                if (!cell.isDisabled()) {
                    cell.disable();
                    Document document = cell.getSticker();
                    EmojiView.this.addRecentSticker(document);
                    if (EmojiView.this.listener != null) {
                        EmojiView.this.listener.onStickerSelected(document);
                    }
                }
            }
        }
    }

    private class EmojiColorPickerView extends View {
        private Drawable arrowDrawable;
        private int arrowX;
        private Drawable backgroundDrawable;
        private String currentEmoji;
        private RectF rect;
        private Paint rectPaint;
        private int selection;

        public void setEmoji(String emoji, int arrowPosition) {
            this.currentEmoji = emoji;
            this.arrowX = arrowPosition;
            this.rectPaint.setColor(788529152);
            invalidate();
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public void setSelection(int position) {
            if (this.selection != position) {
                this.selection = position;
                invalidate();
            }
        }

        public int getSelection() {
            return this.selection;
        }

        public EmojiColorPickerView(Context context) {
            super(context);
            this.rectPaint = new Paint(1);
            this.rect = new RectF();
            this.backgroundDrawable = getResources().getDrawable(C0691R.drawable.stickers_back_all);
            this.arrowDrawable = getResources().getDrawable(C0691R.drawable.stickers_back_arrow);
        }

        protected void onDraw(Canvas canvas) {
            float f;
            float f2 = 55.5f;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? BitmapDescriptorFactory.HUE_YELLOW : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            if (AndroidUtilities.isTablet()) {
                f = 55.5f;
            } else {
                f = 47.5f;
            }
            int dp2 = AndroidUtilities.dp(f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f2 = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f2 + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                for (int a = 0; a < 6; a++) {
                    int x = (EmojiView.this.emojiSize * a) + AndroidUtilities.dp((float) ((a * 4) + 5));
                    int y = AndroidUtilities.dp(9.0f);
                    if (this.selection == a) {
                        this.rect.set((float) x, (float) (y - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + x), (float) ((EmojiView.this.emojiSize + y) + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String code = this.currentEmoji;
                    if (a != 0) {
                        code = code + "\ud83c";
                        switch (a) {
                            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                                code = code + "\udffb";
                                break;
                            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                                code = code + "\udffc";
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                code = code + "\udffd";
                                break;
                            case VideoPlayer.STATE_READY /*4*/:
                                code = code + "\udffe";
                                break;
                            case VideoPlayer.STATE_ENDED /*5*/:
                                code = code + "\udfff";
                                break;
                        }
                    }
                    Drawable drawable2 = Emoji.getEmojiBigDrawable(code);
                    if (drawable2 != null) {
                        drawable2.setBounds(x, y, EmojiView.this.emojiSize + x, EmojiView.this.emojiSize + y);
                        drawable2.draw(canvas);
                    }
                }
            }
        }
    }

    private class EmojiGridAdapter extends BaseAdapter {
        private int emojiPage;

        public EmojiGridAdapter(int page) {
            this.emojiPage = page;
        }

        public int getCount() {
            if (this.emojiPage == -1) {
                return EmojiView.this.recentEmoji.size();
            }
            return EmojiData.dataColored[this.emojiPage].length;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int i, View view, ViewGroup paramViewGroup) {
            String code;
            String coloredCode;
            ImageViewEmoji imageView = (ImageViewEmoji) view;
            if (imageView == null) {
                imageView = new ImageViewEmoji(EmojiView.this.getContext());
            }
            if (this.emojiPage == -1) {
                code = (String) EmojiView.this.recentEmoji.get(i);
                coloredCode = code;
            } else {
                code = EmojiData.dataColored[this.emojiPage][i];
                coloredCode = code;
                String color = (String) EmojiView.emojiColor.get(code);
                if (color != null) {
                    coloredCode = coloredCode + color;
                }
            }
            imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode));
            imageView.setTag(code);
            return imageView;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    private class EmojiPopupWindow extends PopupWindow {
        private OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow() {
            init();
        }

        public EmojiPopupWindow(Context context) {
            super(context);
            init();
        }

        public EmojiPopupWindow(int width, int height) {
            super(width, height);
            init();
        }

        public EmojiPopupWindow(View contentView) {
            super(contentView);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height) {
            super(contentView, width, height);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception e) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            if (this.mSuperScrollListener != null && this.mViewTreeObserver != null) {
                if (this.mViewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View anchor) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
                if (vto != this.mViewTreeObserver) {
                    if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = vto;
                    if (vto != null) {
                        vto.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View anchor, int xoff, int yoff) {
            try {
                super.showAsDropDown(anchor, xoff, yoff);
                registerListener(anchor);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }

        public void update(View anchor, int xoff, int yoff, int width, int height) {
            super.update(anchor, xoff, yoff, width, height);
            registerListener(anchor);
        }

        public void update(View anchor, int width, int height) {
            super.update(anchor, width, height);
            registerListener(anchor);
        }

        public void showAtLocation(View parent, int gravity, int x, int y) {
            super.showAtLocation(parent, gravity, x, y);
            unregisterListener();
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            unregisterListener();
        }
    }

    private class ImageViewEmoji extends ImageView {
        private float lastX;
        private float lastY;
        private boolean touched;
        private float touchedX;
        private float touchedY;

        /* renamed from: org.telegram.ui.Components.EmojiView.ImageViewEmoji.1 */
        class C11331 implements OnClickListener {
            final /* synthetic */ EmojiView val$this$0;

            C11331(EmojiView emojiView) {
                this.val$this$0 = emojiView;
            }

            public void onClick(View view) {
                ImageViewEmoji.this.sendEmoji(null);
            }
        }

        /* renamed from: org.telegram.ui.Components.EmojiView.ImageViewEmoji.2 */
        class C11342 implements OnLongClickListener {
            final /* synthetic */ EmojiView val$this$0;

            C11342(EmojiView emojiView) {
                this.val$this$0 = emojiView;
            }

            public boolean onLongClick(View view) {
                int yOffset = 0;
                String code = (String) view.getTag();
                if (EmojiData.emojiColoredMap.containsKey(code)) {
                    int i;
                    ImageViewEmoji.this.touched = true;
                    ImageViewEmoji.this.touchedX = ImageViewEmoji.this.lastX;
                    ImageViewEmoji.this.touchedY = ImageViewEmoji.this.lastY;
                    String color = (String) EmojiView.emojiColor.get(code);
                    if (color != null) {
                        i = -1;
                        switch (color.hashCode()) {
                            case 1773375:
                                if (color.equals("\ud83c\udffb")) {
                                    i = 0;
                                    break;
                                }
                                break;
                            case 1773376:
                                if (color.equals("\ud83c\udffc")) {
                                    boolean z = true;
                                    break;
                                }
                                break;
                            case 1773377:
                                if (color.equals("\ud83c\udffd")) {
                                    i = 2;
                                    break;
                                }
                                break;
                            case 1773378:
                                if (color.equals("\ud83c\udffe")) {
                                    i = 3;
                                    break;
                                }
                                break;
                            case 1773379:
                                if (color.equals("\ud83c\udfff")) {
                                    i = 4;
                                    break;
                                }
                                break;
                        }
                        switch (i) {
                            case VideoPlayer.TRACK_DEFAULT /*0*/:
                                EmojiView.this.pickerView.setSelection(1);
                                break;
                            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                                EmojiView.this.pickerView.setSelection(2);
                                break;
                            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                                EmojiView.this.pickerView.setSelection(3);
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                EmojiView.this.pickerView.setSelection(4);
                                break;
                            case VideoPlayer.STATE_READY /*4*/:
                                EmojiView.this.pickerView.setSelection(5);
                                break;
                        }
                    }
                    EmojiView.this.pickerView.setSelection(0);
                    view.getLocationOnScreen(EmojiView.this.location);
                    int selection = EmojiView.this.pickerView.getSelection() * EmojiView.this.emojiSize;
                    int selection2 = EmojiView.this.pickerView.getSelection() * 4;
                    if (AndroidUtilities.isTablet()) {
                        i = 5;
                    } else {
                        i = 1;
                    }
                    int x = selection + AndroidUtilities.dp((float) (selection2 - i));
                    if (EmojiView.this.location[0] - x < AndroidUtilities.dp(5.0f)) {
                        x += (EmojiView.this.location[0] - x) - AndroidUtilities.dp(5.0f);
                    } else if ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                        x += ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                    }
                    int xOffset = -x;
                    if (view.getTop() < 0) {
                        yOffset = view.getTop();
                    }
                    EmojiView.this.pickerView.setEmoji(code, (AndroidUtilities.dp(AndroidUtilities.isTablet() ? BitmapDescriptorFactory.HUE_ORANGE : 22.0f) - xOffset) + ((int) AndroidUtilities.dpf2(0.5f)));
                    EmojiView.this.pickerViewPopup.setFocusable(true);
                    EmojiView.this.pickerViewPopup.showAsDropDown(view, xOffset, (((-view.getMeasuredHeight()) - EmojiView.this.popupHeight) + ((view.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - yOffset);
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                if (EmojiView.this.pager.getCurrentItem() == 0) {
                    EmojiView.this.listener.onClearEmojiRecent();
                }
                return false;
            }
        }

        public ImageViewEmoji(Context context) {
            super(context);
            setOnClickListener(new C11331(EmojiView.this));
            setOnLongClickListener(new C11342(EmojiView.this));
            setBackgroundResource(C0691R.drawable.list_selector);
            setScaleType(ScaleType.CENTER);
        }

        private void sendEmoji(String override) {
            String code;
            if (override != null) {
                code = override;
            } else {
                code = (String) getTag();
            }
            if (override == null) {
                if (EmojiView.this.pager.getCurrentItem() != 0) {
                    String color = (String) EmojiView.emojiColor.get(code);
                    if (color != null) {
                        code = code + color;
                    }
                }
                Integer count = (Integer) EmojiView.this.emojiUseHistory.get(code);
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                if (count.intValue() == 0 && EmojiView.this.emojiUseHistory.size() > 50) {
                    for (int a = EmojiView.this.recentEmoji.size() - 1; a >= 0; a--) {
                        EmojiView.this.emojiUseHistory.remove((String) EmojiView.this.recentEmoji.get(a));
                        EmojiView.this.recentEmoji.remove(a);
                        if (EmojiView.this.emojiUseHistory.size() <= 50) {
                            break;
                        }
                    }
                }
                EmojiView.this.emojiUseHistory.put(code, Integer.valueOf(count.intValue() + 1));
                if (EmojiView.this.pager.getCurrentItem() != 0) {
                    EmojiView.this.sortEmoji();
                }
                EmojiView.this.saveRecentEmoji();
                ((EmojiGridAdapter) EmojiView.this.adapters.get(0)).notifyDataSetChanged();
                if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(code));
                }
            } else if (EmojiView.this.listener != null) {
                EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(override));
            }
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.touched) {
                if (event.getAction() == 1 || event.getAction() == 3) {
                    if (EmojiView.this.pickerViewPopup != null && EmojiView.this.pickerViewPopup.isShowing()) {
                        EmojiView.this.pickerViewPopup.dismiss();
                        String color = null;
                        switch (EmojiView.this.pickerView.getSelection()) {
                            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                                color = "\ud83c\udffb";
                                break;
                            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                                color = "\ud83c\udffc";
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                color = "\ud83c\udffd";
                                break;
                            case VideoPlayer.STATE_READY /*4*/:
                                color = "\ud83c\udffe";
                                break;
                            case VideoPlayer.STATE_ENDED /*5*/:
                                color = "\ud83c\udfff";
                                break;
                        }
                        String code = (String) getTag();
                        if (EmojiView.this.pager.getCurrentItem() != 0) {
                            if (color != null) {
                                EmojiView.emojiColor.put(code, color);
                                code = code + color;
                            } else {
                                EmojiView.emojiColor.remove(code);
                            }
                            setImageDrawable(Emoji.getEmojiBigDrawable(code));
                            sendEmoji(null);
                            EmojiView.this.saveEmojiColors();
                        } else {
                            StringBuilder append = new StringBuilder().append(code);
                            if (color == null) {
                                color = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            sendEmoji(append.append(color).toString());
                        }
                    }
                    this.touched = false;
                    this.touchedX = -10000.0f;
                    this.touchedY = -10000.0f;
                } else if (event.getAction() == 2) {
                    boolean ignore = false;
                    if (this.touchedX != -10000.0f) {
                        if (Math.abs(this.touchedX - event.getX()) > AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, true) || Math.abs(this.touchedY - event.getY()) > AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, false)) {
                            this.touchedX = -10000.0f;
                            this.touchedY = -10000.0f;
                        } else {
                            ignore = true;
                        }
                    }
                    if (!ignore) {
                        getLocationOnScreen(EmojiView.this.location);
                        float x = ((float) EmojiView.this.location[0]) + event.getX();
                        EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
                        int position = (int) ((x - ((float) (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f)))) / ((float) (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0f))));
                        if (position < 0) {
                            position = 0;
                        } else if (position > 5) {
                            position = 5;
                        }
                        EmojiView.this.pickerView.setSelection(position);
                    }
                }
            }
            this.lastX = event.getX();
            this.lastY = event.getY();
            return super.onTouchEvent(event);
        }
    }

    public interface Listener {
        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(Document document);

        void onGifTab(boolean z);

        void onStickerSelected(Document document);

        void onStickersSettingsClick();

        void onStickersTab(boolean z);
    }

    private class StickersGridAdapter extends BaseAdapter {
        private HashMap<Integer, Document> cache;
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow;
        private HashMap<Integer, TL_messages_stickerSet> rowStartPack;
        private int stickersPerRow;
        private int totalItems;

        /* renamed from: org.telegram.ui.Components.EmojiView.StickersGridAdapter.1 */
        class C18231 extends StickerEmojiCell {
            C18231(Context x0) {
                super(x0);
            }

            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C0747C.ENCODING_PCM_32BIT));
            }
        }

        public StickersGridAdapter(Context context) {
            this.rowStartPack = new HashMap();
            this.packStartRow = new HashMap();
            this.cache = new HashMap();
            this.context = context;
        }

        public int getCount() {
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
        }

        public Object getItem(int i) {
            return this.cache.get(Integer.valueOf(i));
        }

        public long getItemId(int i) {
            return -1;
        }

        public int getPositionForPack(TL_messages_stickerSet stickerSet) {
            return ((Integer) this.packStartRow.get(stickerSet)).intValue() * this.stickersPerRow;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int position) {
            return this.cache.get(Integer.valueOf(position)) != null;
        }

        public int getItemViewType(int position) {
            if (this.cache.get(Integer.valueOf(position)) != null) {
                return 0;
            }
            return 1;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public int getTabForPosition(int position) {
            if (this.stickersPerRow == 0) {
                int width = EmojiView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet pack = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf(position / this.stickersPerRow));
            if (pack == null) {
                return EmojiView.this.recentTabBum;
            }
            return EmojiView.this.stickerSets.indexOf(pack) + EmojiView.this.stickersTabOffset;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Document sticker = (Document) this.cache.get(Integer.valueOf(i));
            if (sticker != null) {
                if (view == null) {
                    view = new C18231(this.context);
                }
                ((StickerEmojiCell) view).setSticker(sticker, false);
            } else {
                if (view == null) {
                    view = new EmptyCell(this.context);
                }
                if (i == this.totalItems) {
                    TL_messages_stickerSet pack = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf((i - 1) / this.stickersPerRow));
                    if (pack == null) {
                        ((EmptyCell) view).setHeight(1);
                    } else {
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) pack.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        EmptyCell emptyCell = (EmptyCell) view;
                        if (height <= 0) {
                            height = 1;
                        }
                        emptyCell.setHeight(height);
                    }
                } else {
                    ((EmptyCell) view).setHeight(AndroidUtilities.dp(82.0f));
                }
            }
            return view;
        }

        public void notifyDataSetChanged() {
            int width = EmojiView.this.getMeasuredWidth();
            if (width == 0) {
                width = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            this.rowStartPack.clear();
            this.packStartRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList<TL_messages_stickerSet> packs = EmojiView.this.stickerSets;
            for (int a = -1; a < packs.size(); a++) {
                ArrayList<Document> documents;
                TL_messages_stickerSet pack = null;
                int startRow = this.totalItems / this.stickersPerRow;
                if (a == -1) {
                    documents = EmojiView.this.recentStickers;
                } else {
                    pack = (TL_messages_stickerSet) packs.get(a);
                    documents = pack.documents;
                    this.packStartRow.put(pack, Integer.valueOf(startRow));
                }
                if (!documents.isEmpty()) {
                    int b;
                    int count = (int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)));
                    for (b = 0; b < documents.size(); b++) {
                        this.cache.put(Integer.valueOf(this.totalItems + b), documents.get(b));
                    }
                    this.totalItems += this.stickersPerRow * count;
                    for (b = 0; b < count; b++) {
                        this.rowStartPack.put(Integer.valueOf(startRow + b), pack);
                    }
                }
            }
            super.notifyDataSetChanged();
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.10 */
    class AnonymousClass10 extends ScrollSlidingTabStrip {
        boolean first;
        float lastTranslateX;
        float lastX;
        boolean startedScroll;

        AnonymousClass10(Context x0) {
            super(x0);
            this.first = true;
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(ev);
        }

        public boolean onTouchEvent(MotionEvent ev) {
            if (this.first) {
                this.first = false;
                this.lastX = ev.getX();
            }
            float newTranslationX = EmojiView.this.scrollSlidingTabStrip.getTranslationX();
            if (EmojiView.this.scrollSlidingTabStrip.getScrollX() == 0 && newTranslationX == 0.0f) {
                if (this.startedScroll || this.lastX - ev.getX() >= 0.0f) {
                    if (this.startedScroll && this.lastX - ev.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                } else if (EmojiView.this.pager.beginFakeDrag()) {
                    this.startedScroll = true;
                    this.lastTranslateX = EmojiView.this.scrollSlidingTabStrip.getTranslationX();
                }
            }
            if (this.startedScroll) {
                try {
                    EmojiView.this.pager.fakeDragBy((float) ((int) (((ev.getX() - this.lastX) + newTranslationX) - this.lastTranslateX)));
                    this.lastTranslateX = newTranslationX;
                } catch (Throwable e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception e2) {
                    }
                    this.startedScroll = false;
                    FileLog.m13e("tmessages", e);
                }
            }
            this.lastX = ev.getX();
            if (ev.getAction() == 3 || ev.getAction() == 1) {
                this.first = true;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            if (this.startedScroll || super.onTouchEvent(ev)) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.13 */
    class AnonymousClass13 extends ViewPager {
        AnonymousClass13(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.6 */
    class C18186 extends SpanSizeLookup {
        C18186() {
        }

        public int getSpanSize(int position) {
            return EmojiView.this.flowLayoutManager.getSpanSizeForItem(position);
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.7 */
    class C18197 extends ItemDecoration {
        C18197() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int i = 0;
            outRect.left = 0;
            outRect.top = 0;
            outRect.bottom = 0;
            int position = parent.getChildAdapterPosition(view);
            if (!EmojiView.this.flowLayoutManager.isFirstRow(position)) {
                outRect.top = AndroidUtilities.dp(2.0f);
            }
            if (!EmojiView.this.flowLayoutManager.isLastInRow(position)) {
                i = AndroidUtilities.dp(2.0f);
            }
            outRect.right = i;
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.8 */
    class C18208 implements RecyclerListView.OnItemClickListener {
        C18208() {
        }

        public void onItemClick(View view, int position) {
            if (position >= 0 && position < EmojiView.this.recentImages.size() && EmojiView.this.listener != null) {
                EmojiView.this.listener.onGifSelected(((SearchImage) EmojiView.this.recentImages.get(position)).document);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.9 */
    class C18229 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.Components.EmojiView.9.1 */
        class C11321 implements DialogInterface.OnClickListener {
            final /* synthetic */ SearchImage val$searchImage;

            /* renamed from: org.telegram.ui.Components.EmojiView.9.1.1 */
            class C18211 implements RequestDelegate {
                C18211() {
                }

                public void run(TLObject response, TL_error error) {
                }
            }

            C11321(SearchImage searchImage) {
                this.val$searchImage = searchImage;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                EmojiView.this.recentImages.remove(this.val$searchImage);
                TL_messages_saveGif req = new TL_messages_saveGif();
                req.id = new TL_inputDocument();
                req.id.id = this.val$searchImage.document.id;
                req.id.access_hash = this.val$searchImage.document.access_hash;
                req.unsave = true;
                ConnectionsManager.getInstance().sendRequest(req, new C18211());
                MessagesStorage.getInstance().removeWebRecent(this.val$searchImage);
                if (EmojiView.this.gifsAdapter != null) {
                    EmojiView.this.gifsAdapter.notifyDataSetChanged();
                }
                if (EmojiView.this.recentImages.isEmpty()) {
                    EmojiView.this.updateStickerTabs();
                }
            }
        }

        C18229() {
        }

        public boolean onItemClick(View view, int position) {
            if (position < 0 || position >= EmojiView.this.recentImages.size()) {
                return false;
            }
            SearchImage searchImage = (SearchImage) EmojiView.this.recentImages.get(position);
            Builder builder = new Builder(view.getContext());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setMessage(LocaleController.getString("DeleteGif", C0691R.string.DeleteGif));
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK).toUpperCase(), new C11321(searchImage));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            builder.show().setCanceledOnTouchOutside(true);
            return true;
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements IconTabProvider {
        private EmojiPagesAdapter() {
        }

        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            View view;
            if (position == 0) {
                view = EmojiView.this.recentsWrap;
            } else if (position == 6) {
                view = EmojiView.this.stickersWrap;
            } else {
                view = (View) EmojiView.this.views.get(position);
            }
            viewGroup.removeView(view);
        }

        public int getCount() {
            return EmojiView.this.views.size();
        }

        public int getPageIconResId(int paramInt) {
            return EmojiView.this.icons[paramInt];
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view;
            if (position == 0) {
                view = EmojiView.this.recentsWrap;
            } else if (position == 6) {
                view = EmojiView.this.stickersWrap;
            } else {
                view = (View) EmojiView.this.views.get(position);
            }
            viewGroup.addView(view);
            return view;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    private class GifsAdapter extends Adapter {
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public GifsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return EmojiView.this.recentImages.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new ContextLinkCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            SearchImage photoEntry = (SearchImage) EmojiView.this.recentImages.get(i);
            if (photoEntry.document != null) {
                ((ContextLinkCell) viewHolder.itemView).setGif(photoEntry.document, false);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView.5 */
    class C20305 extends ExtendedGridLayoutManager {
        private Size size;

        C20305(Context x0, int x1) {
            super(x0, x1);
            this.size = new Size();
        }

        protected Size getSizeForItem(int i) {
            float f;
            float f2 = 100.0f;
            Document document = ((SearchImage) EmojiView.this.recentImages.get(i)).document;
            Size size = this.size;
            if (document.thumb == null || document.thumb.f34w == 0) {
                f = 100.0f;
            } else {
                f = (float) document.thumb.f34w;
            }
            size.width = f;
            Size size2 = this.size;
            if (!(document.thumb == null || document.thumb.f33h == 0)) {
                f2 = (float) document.thumb.f33h;
            }
            size2.height = f2;
            for (int b = 0; b < document.attributes.size(); b++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(b);
                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                    this.size.width = (float) attribute.f29w;
                    this.size.height = (float) attribute.f28h;
                    break;
                }
            }
            return this.size;
        }
    }

    static {
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
        NOP = new C11271();
        emojiColor = new HashMap();
    }

    public EmojiView(boolean needStickers, boolean needGif, Context context) {
        float f;
        super(context);
        this.adapters = new ArrayList();
        this.emojiUseHistory = new HashMap();
        this.recentEmoji = new ArrayList();
        this.newRecentStickers = new ArrayList();
        this.recentStickers = new ArrayList();
        this.stickerSets = new ArrayList();
        this.icons = new int[]{C0691R.drawable.ic_emoji_recent, C0691R.drawable.ic_emoji_smile, C0691R.drawable.ic_emoji_flower, C0691R.drawable.ic_emoji_bell, C0691R.drawable.ic_emoji_car, C0691R.drawable.ic_emoji_symbol, C0691R.drawable.ic_emoji_sticker};
        this.views = new ArrayList();
        this.location = new int[2];
        this.recentTabBum = -2;
        this.gifTabBum = -2;
        this.showStickers = needStickers;
        this.showGifs = needGif;
        for (int i = 0; i < EmojiData.dataColored.length + 1; i++) {
            GridView gridView = new GridView(context);
            if (AndroidUtilities.isTablet()) {
                gridView.setColumnWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
            } else {
                gridView.setColumnWidth(AndroidUtilities.dp(45.0f));
            }
            gridView.setNumColumns(-1);
            this.views.add(gridView);
            EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter(i - 1);
            gridView.setAdapter(emojiGridAdapter);
            AndroidUtilities.setListViewEdgeEffectColor(gridView, -657673);
            this.adapters.add(emojiGridAdapter);
        }
        if (this.showStickers) {
            StickersQuery.checkStickers();
            this.stickersGridView = new C11292(context);
            this.stickersGridView.setSelector(C0691R.drawable.transparent);
            this.stickersGridView.setColumnWidth(AndroidUtilities.dp(72.0f));
            this.stickersGridView.setNumColumns(-1);
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersGridView);
            this.stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridView.setAdapter(this.stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new C11303());
            this.stickersOnItemClickListener = new C11314();
            this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
            AndroidUtilities.setListViewEdgeEffectColor(this.stickersGridView, -657673);
            this.stickersWrap = new FrameLayout(context);
            this.stickersWrap.addView(this.stickersGridView);
            if (needGif) {
                this.gifsGridView = new RecyclerListView(context);
                this.gifsGridView.setTag(Integer.valueOf(11));
                RecyclerListView recyclerListView = this.gifsGridView;
                LayoutManager c20305 = new C20305(context, 100);
                this.flowLayoutManager = c20305;
                recyclerListView.setLayoutManager(c20305);
                this.flowLayoutManager.setSpanSizeLookup(new C18186());
                this.gifsGridView.addItemDecoration(new C18197());
                this.gifsGridView.setOverScrollMode(2);
                recyclerListView = this.gifsGridView;
                Adapter gifsAdapter = new GifsAdapter(context);
                this.gifsAdapter = gifsAdapter;
                recyclerListView.setAdapter(gifsAdapter);
                this.gifsGridView.setOnItemClickListener(new C18208());
                this.gifsGridView.setOnItemLongClickListener(new C18229());
                this.gifsGridView.setVisibility(8);
                this.stickersWrap.addView(this.gifsGridView);
            }
            this.stickersEmptyView = new TextView(context);
            this.stickersEmptyView.setText(LocaleController.getString("NoStickers", C0691R.string.NoStickers));
            this.stickersEmptyView.setTextSize(1, 18.0f);
            this.stickersEmptyView.setTextColor(-7829368);
            this.stickersWrap.addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2, 17));
            this.stickersGridView.setEmptyView(this.stickersEmptyView);
            this.scrollSlidingTabStrip = new AnonymousClass10(context);
            this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
            this.scrollSlidingTabStrip.setIndicatorColor(-1907225);
            this.scrollSlidingTabStrip.setUnderlineColor(-1907225);
            this.scrollSlidingTabStrip.setVisibility(4);
            addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
            this.scrollSlidingTabStrip.setTranslationX((float) AndroidUtilities.displaySize.x);
            updateStickerTabs();
            this.scrollSlidingTabStrip.setDelegate(new ScrollSlidingTabStripDelegate() {
                public void onPageSelected(int page) {
                    int i = 8;
                    if (EmojiView.this.gifsGridView != null) {
                        if (page == EmojiView.this.gifTabBum + 1) {
                            if (EmojiView.this.gifsGridView.getVisibility() != 0) {
                                EmojiView.this.listener.onGifTab(true);
                                EmojiView.this.showGifTab();
                            }
                        } else if (EmojiView.this.gifsGridView.getVisibility() == 0) {
                            EmojiView.this.listener.onGifTab(false);
                            EmojiView.this.gifsGridView.setVisibility(8);
                            EmojiView.this.stickersGridView.setVisibility(0);
                            TextView access$3400 = EmojiView.this.stickersEmptyView;
                            if (EmojiView.this.stickersGridAdapter.getCount() == 0) {
                                i = 0;
                            }
                            access$3400.setVisibility(i);
                        }
                    }
                    if (page == 0) {
                        EmojiView.this.pager.setCurrentItem(0);
                    } else if (page == EmojiView.this.gifTabBum + 1) {
                    } else {
                        if (page == EmojiView.this.recentTabBum + 1) {
                            ((GridView) EmojiView.this.views.get(6)).setSelection(0);
                            return;
                        }
                        int index = (page - 1) - EmojiView.this.stickersTabOffset;
                        if (index != EmojiView.this.stickerSets.size()) {
                            if (index >= EmojiView.this.stickerSets.size()) {
                                index = EmojiView.this.stickerSets.size() - 1;
                            }
                            ((GridView) EmojiView.this.views.get(6)).setSelection(EmojiView.this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) EmojiView.this.stickerSets.get(index)));
                        } else if (EmojiView.this.listener != null) {
                            EmojiView.this.listener.onStickersSettingsClick();
                        }
                    }
                }
            });
            this.stickersGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    EmojiView.this.checkStickersScroll(firstVisibleItem);
                }
            });
        }
        setBackgroundColor(-657673);
        this.pager = new AnonymousClass13(context);
        EmojiView emojiView = this;
        this.pager.setAdapter(new EmojiPagesAdapter());
        this.pagerSlidingTabStripContainer = new AnonymousClass14(context);
        this.pagerSlidingTabStripContainer.setOrientation(0);
        this.pagerSlidingTabStripContainer.setBackgroundColor(-657673);
        addView(this.pagerSlidingTabStripContainer, LayoutHelper.createFrame(-1, 48.0f));
        PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(context);
        pagerSlidingTabStrip.setViewPager(this.pager);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0f));
        pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        pagerSlidingTabStrip.setIndicatorColor(-13920542);
        pagerSlidingTabStrip.setUnderlineColor(-1907225);
        this.pagerSlidingTabStripContainer.addView(pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, (float) TouchHelperCallback.ALPHA_FULL));
        pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                EmojiView.this.onPageScrolled(position, EmojiView.this.getMeasuredWidth(), positionOffsetPixels);
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.pagerSlidingTabStripContainer.addView(frameLayout, LayoutHelper.createLinear(52, 48));
        this.backspaceButton = new AnonymousClass16(context);
        this.backspaceButton.setImageResource(C0691R.drawable.ic_smiles_backspace);
        this.backspaceButton.setBackgroundResource(C0691R.drawable.ic_emoji_backspace);
        this.backspaceButton.setScaleType(ScaleType.CENTER);
        frameLayout.addView(this.backspaceButton, LayoutHelper.createFrame(52, 48.0f));
        View view = new View(context);
        view.setBackgroundColor(-1907225);
        frameLayout.addView(view, LayoutHelper.createFrame(52, 1, 83));
        this.recentsWrap = new FrameLayout(context);
        this.recentsWrap.addView((View) this.views.get(0));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("NoRecent", C0691R.string.NoRecent));
        textView.setTextSize(18.0f);
        textView.setTextColor(-7829368);
        textView.setGravity(17);
        this.recentsWrap.addView(textView);
        ((GridView) this.views.get(0)).setEmptyView(textView);
        addView(this.pager, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context);
        View view2 = this.pickerView;
        int dp = AndroidUtilities.dp((float) ((((AndroidUtilities.isTablet() ? 40 : 32) * 6) + 10) + 20));
        this.popupWidth = dp;
        if (AndroidUtilities.isTablet()) {
            f = 64.0f;
        } else {
            f = 56.0f;
        }
        int dp2 = AndroidUtilities.dp(f);
        this.popupHeight = dp2;
        this.pickerViewPopup = new EmojiPopupWindow(view2, dp, dp2);
        this.pickerViewPopup.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || EmojiView.this.pickerViewPopup == null || !EmojiView.this.pickerViewPopup.isShowing()) {
                    return false;
                }
                EmojiView.this.pickerViewPopup.dismiss();
                return true;
            }
        });
        loadRecents();
    }

    public void clearRecentEmoji() {
        getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
        this.emojiUseHistory.clear();
        this.recentEmoji.clear();
        saveRecentEmoji();
        ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
    }

    private void showGifTab() {
        this.gifsGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.scrollSlidingTabStrip.onPageScrolled(this.gifTabBum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
    }

    private void checkStickersScroll(int firstVisibleItem) {
        if (this.stickersGridView != null) {
            if (this.stickersGridView.getVisibility() != 0) {
                this.scrollSlidingTabStrip.onPageScrolled(this.gifTabBum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                return;
            }
            int count = this.stickersGridView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.stickersGridView.getChildAt(a);
                if (child.getHeight() + child.getTop() >= AndroidUtilities.dp(5.0f)) {
                    break;
                }
                firstVisibleItem++;
            }
            this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        }
    }

    private void onPageScrolled(int position, int width, int positionOffsetPixels) {
        boolean z = true;
        int i = 0;
        if (this.scrollSlidingTabStrip != null) {
            if (width == 0) {
                width = AndroidUtilities.displaySize.x;
            }
            int margin = 0;
            if (position == 5) {
                margin = -positionOffsetPixels;
                if (this.listener != null) {
                    Listener listener = this.listener;
                    if (positionOffsetPixels == 0) {
                        z = false;
                    }
                    listener.onStickersTab(z);
                }
            } else if (position == 6) {
                margin = -width;
                if (this.listener != null) {
                    this.listener.onStickersTab(true);
                }
            } else if (this.listener != null) {
                this.listener.onStickersTab(false);
            }
            if (this.pagerSlidingTabStripContainer.getTranslationX() != ((float) margin)) {
                this.pagerSlidingTabStripContainer.setTranslationX((float) margin);
                this.scrollSlidingTabStrip.setTranslationX((float) (width + margin));
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.scrollSlidingTabStrip;
                if (margin >= 0) {
                    i = 4;
                }
                scrollSlidingTabStrip.setVisibility(i);
            }
        }
    }

    private void postBackspaceRunnable(int time) {
        AndroidUtilities.runOnUIThread(new AnonymousClass18(time), (long) time);
    }

    private String convert(long paramLong) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        for (int i = 0; i < 4; i++) {
            int j = (int) (65535 & (paramLong >> ((3 - i) * 16)));
            if (j != 0) {
                str = str + ((char) j);
            }
        }
        return str;
    }

    private void saveRecentEmoji() {
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, Integer> entry : this.emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    private void saveEmojiColors() {
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append((String) entry.getValue());
        }
        preferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }

    private void saveRecentStickers() {
        Editor editor = getContext().getSharedPreferences("emoji", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < this.newRecentStickers.size(); a++) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(this.newRecentStickers.get(a));
        }
        editor.putString("stickers2", stringBuilder.toString());
        editor.commit();
    }

    public void switchToGifRecent() {
        this.pager.setCurrentItem(6);
        if (this.gifTabBum < 0 || this.recentImages.isEmpty()) {
            this.switchToGifTab = true;
        } else {
            this.scrollSlidingTabStrip.selectTab(this.gifTabBum + 1);
        }
    }

    private void sortEmoji() {
        this.recentEmoji.clear();
        for (Entry<String, Integer> entry : this.emojiUseHistory.entrySet()) {
            this.recentEmoji.add(entry.getKey());
        }
        Collections.sort(this.recentEmoji, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                Integer count1 = (Integer) EmojiView.this.emojiUseHistory.get(lhs);
                Integer count2 = (Integer) EmojiView.this.emojiUseHistory.get(rhs);
                if (count1 == null) {
                    count1 = Integer.valueOf(0);
                }
                if (count2 == null) {
                    count2 = Integer.valueOf(0);
                }
                if (count1.intValue() > count2.intValue()) {
                    return -1;
                }
                if (count1.intValue() < count2.intValue()) {
                    return 1;
                }
                return 0;
            }
        });
        while (this.recentEmoji.size() > 50) {
            this.recentEmoji.remove(this.recentEmoji.size() - 1);
        }
    }

    private void sortStickers() {
        if (StickersQuery.getStickerSets().isEmpty()) {
            this.recentStickers.clear();
            return;
        }
        int a;
        this.recentStickers.clear();
        for (a = 0; a < this.newRecentStickers.size(); a++) {
            Document sticker = StickersQuery.getStickerById(((Long) this.newRecentStickers.get(a)).longValue());
            if (sticker != null) {
                this.recentStickers.add(sticker);
            }
        }
        while (this.recentStickers.size() > 20) {
            this.recentStickers.remove(this.recentStickers.size() - 1);
        }
        if (this.newRecentStickers.size() != this.recentStickers.size()) {
            this.newRecentStickers.clear();
            for (a = 0; a < this.recentStickers.size(); a++) {
                this.newRecentStickers.add(Long.valueOf(((Document) this.recentStickers.get(a)).id));
            }
            saveRecentStickers();
        }
    }

    private void updateStickerTabs() {
        if (this.scrollSlidingTabStrip != null) {
            int a;
            this.recentTabBum = -2;
            this.gifTabBum = -2;
            this.stickersTabOffset = 0;
            int lastPosition = this.scrollSlidingTabStrip.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            this.scrollSlidingTabStrip.addIconTab(C0691R.drawable.ic_smiles_smile);
            if (!(!this.showGifs || this.recentImages == null || this.recentImages.isEmpty())) {
                this.scrollSlidingTabStrip.addIconTab(C0691R.drawable.ic_smiles_gif);
                this.gifTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
            }
            if (!this.recentStickers.isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.scrollSlidingTabStrip.addIconTab(C0691R.drawable.ic_smiles_recent);
            }
            this.stickerSets.clear();
            ArrayList<TL_messages_stickerSet> packs = StickersQuery.getStickerSets();
            for (a = 0; a < packs.size(); a++) {
                TL_messages_stickerSet pack = (TL_messages_stickerSet) packs.get(a);
                if (!(pack.set.disabled || pack.documents == null || pack.documents.isEmpty())) {
                    this.stickerSets.add(pack);
                }
            }
            for (a = 0; a < this.stickerSets.size(); a++) {
                this.scrollSlidingTabStrip.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets.get(a)).documents.get(0));
            }
            this.scrollSlidingTabStrip.addIconTab(C0691R.drawable.ic_settings);
            this.scrollSlidingTabStrip.updateTabStyles();
            if (lastPosition != 0) {
                this.scrollSlidingTabStrip.onPageScrolled(lastPosition, lastPosition);
            }
            if (this.switchToGifTab && this.gifTabBum >= 0 && this.gifsGridView.getVisibility() != 0) {
                showGifTab();
                this.switchToGifTab = false;
            }
            if (this.gifTabBum == -2 && this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                this.listener.onGifTab(false);
                this.gifsGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersEmptyView.setVisibility(this.stickersGridAdapter.getCount() != 0 ? 8 : 0);
            } else if (this.gifTabBum == -2) {
            } else {
                if (this.gifsGridView == null || this.gifsGridView.getVisibility() != 0) {
                    this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(this.stickersGridView.getFirstVisiblePosition()) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                } else {
                    this.scrollSlidingTabStrip.onPageScrolled(this.gifTabBum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                }
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            int index = this.newRecentStickers.indexOf(Long.valueOf(document.id));
            if (index == -1) {
                this.newRecentStickers.add(0, Long.valueOf(document.id));
                if (this.newRecentStickers.size() > 20) {
                    this.newRecentStickers.remove(this.newRecentStickers.size() - 1);
                }
            } else if (index != 0) {
                this.newRecentStickers.remove(index);
                this.newRecentStickers.add(0, Long.valueOf(document.id));
            }
            saveRecentStickers();
        }
    }

    public void addRecentGif(SearchImage searchImage) {
        if (searchImage != null && searchImage.document != null && this.recentImages != null) {
            boolean wasEmpty = this.recentImages.isEmpty();
            for (int a = 0; a < this.recentImages.size(); a++) {
                SearchImage image = (SearchImage) this.recentImages.get(a);
                if (image.id.equals(searchImage.id)) {
                    this.recentImages.remove(a);
                    this.recentImages.add(0, image);
                    if (this.gifsAdapter != null) {
                        this.gifsAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
            }
            this.recentImages.add(0, searchImage);
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void loadRecents() {
        String str;
        String[] args2;
        int a;
        String[] args;
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", 0);
        this.lastGifLoadTime = preferences.getLong("lastGifLoadTime", 0);
        try {
            this.emojiUseHistory.clear();
            if (preferences.contains("emojis")) {
                str = preferences.getString("emojis", TtmlNode.ANONYMOUS_REGION_ID);
                if (str != null && str.length() > 0) {
                    for (String arg : str.split(",")) {
                        args2 = arg.split("=");
                        long value = Utilities.parseLong(args2[0]).longValue();
                        String string = TtmlNode.ANONYMOUS_REGION_ID;
                        for (a = 0; a < 4; a++) {
                            string = String.valueOf((char) ((int) value)) + string;
                            value >>= 16;
                            if (value == 0) {
                                break;
                            }
                        }
                        if (string.length() > 0) {
                            this.emojiUseHistory.put(string, Utilities.parseInt(args2[1]));
                        }
                    }
                }
                preferences.edit().remove("emojis").commit();
                saveRecentEmoji();
            } else {
                str = preferences.getString("emojis2", TtmlNode.ANONYMOUS_REGION_ID);
                if (str != null && str.length() > 0) {
                    for (String arg2 : str.split(",")) {
                        args2 = arg2.split("=");
                        this.emojiUseHistory.put(args2[0], Utilities.parseInt(args2[1]));
                    }
                }
            }
            if (this.emojiUseHistory.isEmpty()) {
                if (!preferences.getBoolean("filled_default", false)) {
                    String[] newRecent = new String[]{"\ud83d\ude02", "\ud83d\ude18", "\u2764", "\ud83d\ude0d", "\ud83d\ude0a", "\ud83d\ude01", "\ud83d\udc4d", "\u263a", "\ud83d\ude14", "\ud83d\ude04", "\ud83d\ude2d", "\ud83d\udc8b", "\ud83d\ude12", "\ud83d\ude33", "\ud83d\ude1c", "\ud83d\ude48", "\ud83d\ude09", "\ud83d\ude03", "\ud83d\ude22", "\ud83d\ude1d", "\ud83d\ude31", "\ud83d\ude21", "\ud83d\ude0f", "\ud83d\ude1e", "\ud83d\ude05", "\ud83d\ude1a", "\ud83d\ude4a", "\ud83d\ude0c", "\ud83d\ude00", "\ud83d\ude0b", "\ud83d\ude06", "\ud83d\udc4c", "\ud83d\ude10", "\ud83d\ude15"};
                    int i = 0;
                    while (true) {
                        int length = newRecent.length;
                        if (i >= r0) {
                            break;
                        }
                        this.emojiUseHistory.put(newRecent[i], Integer.valueOf(newRecent.length - i));
                        i++;
                    }
                    preferences.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
            }
            sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            str = preferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
            if (str != null && str.length() > 0) {
                args = str.split(",");
                a = 0;
                while (true) {
                    length = args.length;
                    if (a >= r0) {
                        break;
                    }
                    args2 = args[a].split("=");
                    emojiColor.put(args2[0], args2[1]);
                    a++;
                }
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        if (this.showStickers) {
            try {
                this.newRecentStickers.clear();
                str = preferences.getString("stickers", TtmlNode.ANONYMOUS_REGION_ID);
                if (str == null || str.length() <= 0) {
                    args = preferences.getString("stickers2", TtmlNode.ANONYMOUS_REGION_ID).split(",");
                    a = 0;
                    while (true) {
                        length = args.length;
                        if (a >= r0) {
                            break;
                        }
                        if (args[a].length() != 0) {
                            long id = Utilities.parseLong(args[a]).longValue();
                            if (id != 0) {
                                this.newRecentStickers.add(Long.valueOf(id));
                            }
                        }
                        a++;
                    }
                } else {
                    args = str.split(",");
                    HashMap<Long, Integer> stickersUseHistory = new HashMap();
                    a = 0;
                    while (true) {
                        length = args.length;
                        if (a >= r0) {
                            break;
                        }
                        args2 = args[a].split("=");
                        Long key = Utilities.parseLong(args2[0]);
                        stickersUseHistory.put(key, Utilities.parseInt(args2[1]));
                        this.newRecentStickers.add(key);
                        a++;
                    }
                    Collections.sort(this.newRecentStickers, new AnonymousClass20(stickersUseHistory));
                    preferences.edit().remove("stickers").commit();
                    saveRecentStickers();
                }
                sortStickers();
                updateStickerTabs();
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams layoutParams = (LayoutParams) this.pagerSlidingTabStripContainer.getLayoutParams();
        LayoutParams layoutParams1 = null;
        layoutParams.width = MeasureSpec.getSize(widthMeasureSpec);
        if (this.scrollSlidingTabStrip != null) {
            layoutParams1 = (LayoutParams) this.scrollSlidingTabStrip.getLayoutParams();
            if (layoutParams1 != null) {
                layoutParams1.width = layoutParams.width;
            }
        }
        if (layoutParams.width != this.oldWidth) {
            if (!(this.scrollSlidingTabStrip == null || layoutParams1 == null)) {
                onPageScrolled(this.pager.getCurrentItem(), layoutParams.width, 0);
                this.scrollSlidingTabStrip.setLayoutParams(layoutParams1);
            }
            this.pagerSlidingTabStripContainer.setLayoutParams(layoutParams);
            this.oldWidth = layoutParams.width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(layoutParams.width, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), C0747C.ENCODING_PCM_32BIT));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void reloadStickersAdapter() {
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener value) {
        this.listener = value;
    }

    public void invalidateViews() {
        Iterator i$ = this.views.iterator();
        while (i$.hasNext()) {
            GridView gridView = (GridView) i$.next();
            if (gridView != null) {
                gridView.invalidateViews();
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    EmojiView.this.updateStickerTabs();
                    EmojiView.this.reloadStickersAdapter();
                }
            });
        }
    }

    public void loadGifRecent() {
        if (this.showGifs && this.gifsAdapter != null && !this.loadingRecent) {
            MessagesStorage.getInstance().loadWebRecent(2);
            this.loadingRecent = true;
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
            sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
                sortStickers();
                updateStickerTabs();
                reloadStickersAdapter();
                if (!(this.gifsGridView == null || this.gifsGridView.getVisibility() != 0 || this.listener == null)) {
                    this.listener.onGifTab(true);
                }
            }
            loadGifRecent();
        }
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.pickerViewPopup != null && this.pickerViewPopup.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private int calcGifsHash(ArrayList<SearchImage> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        long acc = 0;
        for (int a = 0; a < Math.min(Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size()); a++) {
            SearchImage searchImage = (SearchImage) arrayList.get(a);
            if (searchImage.document != null) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (searchImage.document.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) searchImage.document.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void loadRecentGif() {
        if (!this.loadingRecentGifs && Math.abs(System.currentTimeMillis() - this.lastGifLoadTime) >= 3600000) {
            this.loadingRecentGifs = true;
            TL_messages_getSavedGifs req = new TL_messages_getSavedGifs();
            req.hash = calcGifsHash(this.recentImages);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.ui.Components.EmojiView.22.1 */
                class C11281 implements Runnable {
                    final /* synthetic */ ArrayList val$arrayListFinal;

                    C11281(ArrayList arrayList) {
                        this.val$arrayListFinal = arrayList;
                    }

                    public void run() {
                        if (this.val$arrayListFinal != null) {
                            boolean wasEmpty = EmojiView.this.recentImages.isEmpty();
                            EmojiView.this.recentImages = this.val$arrayListFinal;
                            if (EmojiView.this.gifsAdapter != null) {
                                EmojiView.this.gifsAdapter.notifyDataSetChanged();
                            }
                            EmojiView.this.lastGifLoadTime = System.currentTimeMillis();
                            EmojiView.this.getContext().getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", EmojiView.this.lastGifLoadTime).commit();
                            if (wasEmpty && !EmojiView.this.recentImages.isEmpty()) {
                                EmojiView.this.updateStickerTabs();
                            }
                        }
                        EmojiView.this.loadingRecentGifs = false;
                    }
                }

                public void run(TLObject response, TL_error error) {
                    ArrayList<SearchImage> arrayList = null;
                    if (response instanceof TL_messages_savedGifs) {
                        arrayList = new ArrayList();
                        TL_messages_savedGifs res = (TL_messages_savedGifs) response;
                        int size = res.gifs.size();
                        for (int a = 0; a < size; a++) {
                            SearchImage searchImage = new SearchImage();
                            searchImage.type = 2;
                            searchImage.document = (Document) res.gifs.get(a);
                            searchImage.date = size - a;
                            searchImage.id = TtmlNode.ANONYMOUS_REGION_ID + searchImage.document.id;
                            arrayList.add(searchImage);
                            MessagesStorage.getInstance().putWebRecent(arrayList);
                        }
                    }
                    AndroidUtilities.runOnUIThread(new C11281(arrayList));
                }
            });
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.stickersDidLoaded) {
            updateStickerTabs();
            reloadStickersAdapter();
        } else if (id == NotificationCenter.recentImagesDidLoaded && ((Integer) args[0]).intValue() == 2) {
            int previousCount;
            if (this.recentImages != null) {
                previousCount = this.recentImages.size();
            } else {
                previousCount = 0;
            }
            this.recentImages = (ArrayList) args[1];
            this.loadingRecent = false;
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (previousCount != this.recentImages.size()) {
                updateStickerTabs();
            }
            loadRecentGif();
        }
    }
}
