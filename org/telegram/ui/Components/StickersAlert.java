package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.StickerPreviewViewer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class StickersAlert extends BottomSheet implements NotificationCenterDelegate {
    private GridAdapter adapter;
    private StickersAlertDelegate delegate;
    private FrameLayout emptyView;
    private RecyclerListView gridView;
    private boolean ignoreLayout;
    private InputStickerSet inputStickerSet;
    private GridLayoutManager layoutManager;
    private PickerBottomLayout pickerBottomLayout;
    private TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    private int scrollOffsetY;
    private Document selectedSticker;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private TextView stickerEmojiTextView;
    private BackupImageView stickerImageView;
    private FrameLayout stickerPreviewLayout;
    private TL_messages_stickerSet stickerSet;
    private OnItemClickListener stickersOnItemClickListener;
    private TextView titleTextView;

    /* renamed from: org.telegram.ui.Components.StickersAlert.1 */
    class C11891 extends FrameLayout {
        C11891(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(ev);
            }
            StickersAlert.this.dismiss();
            return true;
        }

        public boolean onTouchEvent(MotionEvent e) {
            return !StickersAlert.this.isDismissed() && super.onTouchEvent(e);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int ceil;
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (VERSION.SDK_INT >= 21) {
                height -= AndroidUtilities.statusBarHeight;
            }
            int dp = AndroidUtilities.dp(96.0f);
            if (StickersAlert.this.stickerSet != null) {
                ceil = (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f));
            } else {
                ceil = 0;
            }
            int contentSize = ((Math.max(3, ceil) * AndroidUtilities.dp(82.0f)) + dp) + StickersAlert.backgroundPaddingTop;
            int padding = ((double) contentSize) < ((double) (height / 5)) * 3.2d ? 0 : (height / 5) * 2;
            if (padding != 0 && contentSize < height) {
                padding -= height - contentSize;
            }
            if (padding == 0) {
                padding = StickersAlert.backgroundPaddingTop;
            }
            if (StickersAlert.this.gridView.getPaddingTop() != padding) {
                StickersAlert.this.ignoreLayout = true;
                StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), padding, AndroidUtilities.dp(10.0f), 0);
                StickersAlert.this.emptyView.setPadding(0, padding, 0, 0);
                StickersAlert.this.ignoreLayout = false;
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), C0747C.ENCODING_PCM_32BIT));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            StickersAlert.this.updateLayout();
        }

        public void requestLayout() {
            if (!StickersAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }

        protected void onDraw(Canvas canvas) {
            StickersAlert.this.shadowDrawable.setBounds(0, StickersAlert.this.scrollOffsetY - StickersAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
            StickersAlert.this.shadowDrawable.draw(canvas);
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.2 */
    class C11902 implements OnTouchListener {
        C11902() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.5 */
    class C11915 implements OnTouchListener {
        C11915() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return StickerPreviewViewer.getInstance().onTouch(event, StickersAlert.this.gridView, 0, StickersAlert.this.stickersOnItemClickListener);
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.8 */
    class C11928 extends FrameLayout {
        C11928(Context x0) {
            super(x0);
        }

        public void requestLayout() {
            if (!StickersAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.9 */
    class C11939 implements OnTouchListener {
        C11939() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    public interface StickersAlertDelegate {
        void onStickerSelected(Document document);
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.18 */
    class AnonymousClass18 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$num;
        final /* synthetic */ boolean val$show;

        AnonymousClass18(int i, boolean z) {
            this.val$num = i;
            this.val$show = z;
        }

        public void onAnimationEnd(Animator animation) {
            if (StickersAlert.this.shadowAnimation[this.val$num] != null && StickersAlert.this.shadowAnimation[this.val$num].equals(animation)) {
                if (!this.val$show) {
                    StickersAlert.this.shadow[this.val$num].setVisibility(4);
                }
                StickersAlert.this.shadowAnimation[this.val$num] = null;
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (StickersAlert.this.shadowAnimation[this.val$num] != null && StickersAlert.this.shadowAnimation[this.val$num].equals(animation)) {
                StickersAlert.this.shadowAnimation[this.val$num] = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.4 */
    class C18464 extends ItemDecoration {
        C18464() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.top = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.6 */
    class C18476 extends OnScrollListener {
        C18476() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            StickersAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.7 */
    class C18487 implements OnItemClickListener {
        C18487() {
        }

        public void onItemClick(View view, int position) {
            if (StickersAlert.this.stickerSet != null && position >= 0 && position < StickersAlert.this.stickerSet.documents.size()) {
                LayoutParams layoutParams;
                AnimatorSet animatorSet;
                StickersAlert.this.selectedSticker = (Document) StickersAlert.this.stickerSet.documents.get(position);
                boolean set = false;
                for (int a = 0; a < StickersAlert.this.selectedSticker.attributes.size(); a++) {
                    DocumentAttribute attribute = (DocumentAttribute) StickersAlert.this.selectedSticker.attributes.get(a);
                    if (attribute instanceof TL_documentAttributeSticker) {
                        if (attribute.alt != null && attribute.alt.length() > 0) {
                            StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(attribute.alt, StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                            set = true;
                        }
                        if (!set) {
                            StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                        }
                        StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
                        layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                        layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                        StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                        StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                        animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL})});
                        animatorSet.setDuration(200);
                        animatorSet.start();
                    }
                }
                if (set) {
                    StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                }
                StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
                layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL})});
                animatorSet.setDuration(200);
                animatorSet.start();
            }
        }
    }

    private class GridAdapter extends Adapter {
        Context context;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public GridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return StickersAlert.this.stickerSet != null ? StickersAlert.this.stickerSet.documents.size() : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new StickerEmojiCell(this.context);
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(82.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ((StickerEmojiCell) holder.itemView).setSticker((Document) StickersAlert.this.stickerSet.documents.get(position), true);
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert.3 */
    class C20283 extends RecyclerListView {
        C20283(Context x0) {
            super(x0);
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, StickersAlert.this.gridView, 0);
            if (super.onInterceptTouchEvent(event) || result) {
                return true;
            }
            return false;
        }

        public void requestLayout() {
            if (!StickersAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    public StickersAlert(Context context, InputStickerSet set, TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = set;
        this.stickerSet = loadedSet;
        this.shadowDrawable = context.getResources().getDrawable(C0691R.drawable.sheet_shadow);
        this.containerView = new C11891(context);
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.titleTextView = new TextView(context);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 48));
        this.titleTextView.setOnTouchListener(new C11902());
        this.shadow[0] = new View(context);
        this.shadow[0].setBackgroundResource(C0691R.drawable.header_shadow);
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow[0], LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.gridView = new C20283(context);
        this.gridView.setTag(Integer.valueOf(14));
        RecyclerListView recyclerListView = this.gridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        recyclerListView = this.gridView;
        Adapter gridAdapter = new GridAdapter(context);
        this.adapter = gridAdapter;
        recyclerListView.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new C18464());
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(-657673);
        this.gridView.setOnTouchListener(new C11915());
        this.gridView.setOnScrollListener(new C18476());
        this.stickersOnItemClickListener = new C18487();
        this.gridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new C11928(context);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(new C11939());
        this.emptyView.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        this.shadow[1] = new View(context);
        this.shadow[1].setBackgroundResource(C0691R.drawable.header_shadow_reverse);
        this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.pickerBottomLayout = new PickerBottomLayout(context, false);
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.cancelButton.setTextColor(Theme.STICKERS_SHEET_CLOSE_TEXT_COLOR);
        this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", C0691R.string.Close).toUpperCase());
        this.pickerBottomLayout.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.dismiss();
            }
        });
        this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundResource(C0691R.drawable.stickercounter);
        this.stickerPreviewLayout = new FrameLayout(context);
        this.stickerPreviewLayout.setBackgroundColor(-536870913);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.stickerPreviewLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StickersAlert.this.hidePreview();
            }
        });
        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(C0691R.drawable.delete_reply);
        closeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            closeButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
        }
        this.stickerPreviewLayout.addView(closeButton, LayoutHelper.createFrame(48, 48, 53));
        closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StickersAlert.this.hidePreview();
            }
        });
        this.stickerImageView = new BackupImageView(context);
        this.stickerImageView.setAspectFit(true);
        int size = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        this.stickerPreviewLayout.addView(this.stickerImageView, LayoutHelper.createFrame(size, size, 17));
        this.stickerEmojiTextView = new TextView(context);
        this.stickerEmojiTextView.setTextSize(1, BitmapDescriptorFactory.HUE_ORANGE);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView, LayoutHelper.createFrame(size, size, 17));
        this.previewSendButton = new TextView(context);
        this.previewSendButton.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(-1);
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setText(LocaleController.getString("Close", C0691R.string.Close).toUpperCase());
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.previewSendButton.setVisibility(8);
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StickersAlert.this.delegate.onStickerSelected(StickersAlert.this.selectedSticker);
                StickersAlert.this.dismiss();
            }
        });
        this.previewSendButtonShadow = new View(context);
        this.previewSendButtonShadow.setBackgroundResource(C0691R.drawable.header_shadow_reverse);
        this.previewSendButtonShadow.setVisibility(8);
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.delegate != null) {
            this.previewSendButton.setText(LocaleController.getString("SendSticker", C0691R.string.SendSticker).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, (float) size, 17, 0.0f, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        }
        if (this.stickerSet == null && this.inputStickerSet.short_name != null) {
            this.stickerSet = StickersQuery.getStickerSetByName(this.inputStickerSet.short_name);
        }
        if (this.stickerSet == null) {
            this.stickerSet = StickersQuery.getStickerSetById(Long.valueOf(this.inputStickerSet.id));
        }
        if (this.stickerSet == null) {
            TL_messages_getStickerSet req = new TL_messages_getStickerSet();
            req.stickerset = this.inputStickerSet;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.ui.Components.StickersAlert.14.1 */
                class C11871 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C11871(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        StickersAlert.this.reqId = 0;
                        if (this.val$error == null) {
                            StickersAlert.this.stickerSet = (TL_messages_stickerSet) this.val$response;
                            StickersAlert.this.updateFields();
                            StickersAlert.this.adapter.notifyDataSetChanged();
                            return;
                        }
                        Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersNotFound", C0691R.string.AddStickersNotFound), 0).show();
                        StickersAlert.this.dismiss();
                    }
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C11871(error, response));
                }
            });
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        updateFields();
    }

    private void updateFields() {
        if (this.titleTextView != null) {
            if (this.stickerSet != null) {
                this.titleTextView.setText(this.stickerSet.set.title);
                if (this.stickerSet.set == null || !StickersQuery.isStickerPackInstalled(this.stickerSet.set.id)) {
                    setRightButton(new OnClickListener() {

                        /* renamed from: org.telegram.ui.Components.StickersAlert.15.1 */
                        class C18451 implements RequestDelegate {

                            /* renamed from: org.telegram.ui.Components.StickersAlert.15.1.1 */
                            class C11881 implements Runnable {
                                final /* synthetic */ TL_error val$error;

                                C11881(TL_error tL_error) {
                                    this.val$error = tL_error;
                                }

                                public void run() {
                                    try {
                                        if (this.val$error == null) {
                                            Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersInstalled", C0691R.string.AddStickersInstalled), 0).show();
                                        } else if (this.val$error.text.equals("STICKERSETS_TOO_MUCH")) {
                                            Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("TooMuchStickersets", C0691R.string.TooMuchStickersets), 0).show();
                                        } else {
                                            Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred), 0).show();
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m13e("tmessages", e);
                                    }
                                    StickersQuery.loadStickers(false, true);
                                }
                            }

                            C18451() {
                            }

                            public void run(TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new C11881(error));
                            }
                        }

                        public void onClick(View v) {
                            StickersAlert.this.dismiss();
                            TL_messages_installStickerSet req = new TL_messages_installStickerSet();
                            req.stickerset = StickersAlert.this.inputStickerSet;
                            ConnectionsManager.getInstance().sendRequest(req, new C18451());
                        }
                    }, LocaleController.getString("AddStickers", C0691R.string.AddStickers), Theme.STICKERS_SHEET_ADD_TEXT_COLOR, true);
                } else if (this.stickerSet.set.official) {
                    setRightButton(null, null, Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR, false);
                } else {
                    setRightButton(new OnClickListener() {
                        public void onClick(View v) {
                            StickersAlert.this.dismiss();
                            StickersQuery.removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 0);
                        }
                    }, LocaleController.getString("StickersRemove", C0691R.string.StickersRemove), Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR, false);
                }
                this.adapter.notifyDataSetChanged();
                return;
            }
            setRightButton(null, null, Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR, false);
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = this.gridView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.titleTextView.setTranslationY((float) this.scrollOffsetY);
            this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
            return;
        }
        View child = this.gridView.getChildAt(0);
        Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
        } else {
            newOffset = top;
            runShadowAnimation(0, false);
        }
        if (this.scrollOffsetY != newOffset) {
            recyclerListView = this.gridView;
            this.scrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.titleTextView.setTranslationY((float) this.scrollOffsetY);
            this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
            public void onAnimationEnd(Animator animation) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(int num, boolean show) {
        if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
            this.shadow[num].setTag(show ? null : Integer.valueOf(1));
            if (show) {
                this.shadow[num].setVisibility(0);
            }
            if (this.shadowAnimation[num] != null) {
                this.shadowAnimation[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            AnimatorSet animatorSet = this.shadowAnimation[num];
            Animator[] animatorArr = new Animator[1];
            Object obj = this.shadow[num];
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = show ? TouchHelperCallback.ALPHA_FULL : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(obj, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.shadowAnimation[num].setDuration(150);
            this.shadowAnimation[num].addListener(new AnonymousClass18(num, show));
            this.shadowAnimation[num].start();
        }
    }

    public void dismiss() {
        super.dismiss();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (this.gridView != null) {
                this.gridView.invalidateViews();
            }
            if (StickerPreviewViewer.getInstance().isVisible()) {
                StickerPreviewViewer.getInstance().close();
            }
            StickerPreviewViewer.getInstance().reset();
        }
    }

    private void setRightButton(OnClickListener onClickListener, String title, int color, boolean showCircle) {
        if (title == null) {
            this.pickerBottomLayout.doneButton.setVisibility(8);
            return;
        }
        this.pickerBottomLayout.doneButton.setVisibility(0);
        if (showCircle) {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(0);
            this.pickerBottomLayout.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.stickerSet.documents.size())}));
        } else {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        }
        this.pickerBottomLayout.doneButtonTextView.setTextColor(color);
        this.pickerBottomLayout.doneButtonTextView.setText(title.toUpperCase());
        this.pickerBottomLayout.doneButton.setOnClickListener(onClickListener);
    }
}
