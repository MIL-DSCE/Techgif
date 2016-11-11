package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Scroller;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.PlayerControl;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.ExtractorRendererBuilder;
import org.telegram.ui.Components.VideoPlayer.Listener;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PhotoViewer implements NotificationCenterDelegate, OnGestureListener, OnDoubleTapListener {
    private static volatile PhotoViewer Instance = null;
    private static final int PAGE_SPACING;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_caption = 8;
    private static final int gallery_menu_caption_done = 9;
    private static final int gallery_menu_crop = 4;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_showall = 2;
    private static final int gallery_menu_tune = 7;
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private ActionBar actionBar;
    private Context actvityContext;
    private boolean allowMentions;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ArrayList<Photo> avatarsArr;
    private int avatarsDialogId;
    private BackgroundDrawable backgroundDrawable;
    private FrameLayout bottomLayout;
    private boolean canDragDown;
    private boolean canShowBottom;
    private boolean canZoom;
    private ActionBarMenuItem captionDoneItem;
    private PhotoViewerCaptionEnterView captionEditText;
    private ActionBarMenuItem captionItem;
    private TextView captionTextView;
    private TextView captionTextViewNew;
    private TextView captionTextViewOld;
    private ImageReceiver centerImage;
    private AnimatorSet changeModeAnimation;
    private boolean changingPage;
    private CheckBox checkImageView;
    private int classGuid;
    private FrameLayoutDrawer containerView;
    private ActionBarMenuItem cropItem;
    private AnimatorSet currentActionBarAnimation;
    private AnimatedFileDrawable currentAnimation;
    private long currentDialogId;
    private int currentEditMode;
    private FileLocation currentFileLocation;
    private String[] currentFileNames;
    private int currentIndex;
    private MessageObject currentMessageObject;
    private String currentPathObject;
    private PlaceProviderObject currentPlaceObject;
    private int currentRotation;
    private Bitmap currentThumb;
    private FileLocation currentUserAvatarLocation;
    private TextView dateTextView;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private PickerBottomLayout editorDoneLayout;
    private boolean[] endReached;
    private GestureDetector gestureDetector;
    private PlaceProviderObject hideAfterAnimation;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<MessageObject> imagesArr;
    private ArrayList<Object> imagesArrLocals;
    private ArrayList<FileLocation> imagesArrLocations;
    private ArrayList<Integer> imagesArrLocationsSizes;
    private ArrayList<MessageObject> imagesArrTemp;
    private HashMap<Integer, MessageObject>[] imagesByIds;
    private HashMap<Integer, MessageObject>[] imagesByIdsTemp;
    private DecelerateInterpolator interpolator;
    private boolean invalidCoords;
    private boolean isActionBarVisible;
    private boolean isFirstLoading;
    private boolean isPlaying;
    private boolean isVisible;
    private String lastTitle;
    private ImageReceiver leftImage;
    private boolean loadingMoreImages;
    private float maxX;
    private float maxY;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private MentionsAdapter mentionsAdapter;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private TextView nameTextView;
    private boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    private boolean opennedFromMedia;
    private Activity parentActivity;
    private ChatActivity parentChatActivity;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private PickerBottomLayout pickerView;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale;
    private float pinchStartX;
    private float pinchStartY;
    private PhotoViewerProvider placeProvider;
    private boolean playerNeedsPrepare;
    private RadialProgressView[] radialProgressViews;
    private ImageReceiver rightImage;
    private float scale;
    private Scroller scroller;
    private int sendPhotoType;
    private ImageView shareButton;
    private PlaceProviderObject showAfterAnimation;
    private int switchImageAfterAnimation;
    private boolean textureUploaded;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    private ActionBarMenuItem tuneItem;
    private Runnable updateProgressRunnable;
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private ImageView videoPlayButton;
    private VideoPlayer videoPlayer;
    private FrameLayout videoPlayerControlFrameLayout;
    private SeekBar videoPlayerSeekbar;
    private TextView videoPlayerTime;
    private TextureView videoTextureView;
    private AlertDialog visibleDialog;
    private LayoutParams windowLayoutParams;
    private FrameLayoutTouchListener windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    /* renamed from: org.telegram.ui.PhotoViewer.1 */
    class C13671 implements Runnable {
        C13671() {
        }

        public void run() {
            if (!(PhotoViewer.this.videoPlayer == null || PhotoViewer.this.videoPlayerSeekbar == null || PhotoViewer.this.videoPlayerSeekbar.isDragging())) {
                PlayerControl playerControl = PhotoViewer.this.videoPlayer.getPlayerControl();
                PhotoViewer.this.videoPlayerSeekbar.setProgress(((float) playerControl.getCurrentPosition()) / ((float) playerControl.getDuration()));
                PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                PhotoViewer.this.updateVideoPlayerTime();
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 100);
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.30 */
    class AnonymousClass30 implements Runnable {
        final /* synthetic */ ArrayList val$photos;

        AnonymousClass30(ArrayList arrayList) {
            this.val$photos = arrayList;
        }

        public void run() {
            if (PhotoViewer.this.containerView != null && PhotoViewer.this.windowView != null) {
                if (VERSION.SDK_INT >= 18) {
                    PhotoViewer.this.containerView.setLayerType(PhotoViewer.PAGE_SPACING, null);
                }
                PhotoViewer.this.animationInProgress = PhotoViewer.PAGE_SPACING;
                PhotoViewer.this.transitionAnimationStartTime = 0;
                PhotoViewer.this.setImages();
                PhotoViewer.this.containerView.invalidate();
                PhotoViewer.this.animatingImageView.setVisibility(PhotoViewer.gallery_menu_caption);
                if (PhotoViewer.this.showAfterAnimation != null) {
                    PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                }
                if (PhotoViewer.this.hideAfterAnimation != null) {
                    PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                }
                if (this.val$photos != null) {
                    PhotoViewer.this.windowLayoutParams.flags = PhotoViewer.PAGE_SPACING;
                    PhotoViewer.this.windowLayoutParams.softInputMode = 32;
                    ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).updateViewLayout(PhotoViewer.this.windowView, PhotoViewer.this.windowLayoutParams);
                    PhotoViewer.this.windowView.setFocusable(true);
                    PhotoViewer.this.containerView.setFocusable(true);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.32 */
    class AnonymousClass32 implements Runnable {
        final /* synthetic */ AnimatorSet val$animatorSet;

        AnonymousClass32(AnimatorSet animatorSet) {
            this.val$animatorSet = animatorSet;
        }

        public void run() {
            NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded});
            NotificationCenter.getInstance().setAnimationInProgress(true);
            this.val$animatorSet.start();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.33 */
    class AnonymousClass33 implements Runnable {
        final /* synthetic */ PlaceProviderObject val$object;

        AnonymousClass33(PlaceProviderObject placeProviderObject) {
            this.val$object = placeProviderObject;
        }

        public void run() {
            PhotoViewer.this.disableShowCheck = false;
            this.val$object.imageReceiver.setVisible(false, true);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.34 */
    class AnonymousClass34 implements Runnable {
        final /* synthetic */ PlaceProviderObject val$object;

        AnonymousClass34(PlaceProviderObject placeProviderObject) {
            this.val$object = placeProviderObject;
        }

        public void run() {
            if (VERSION.SDK_INT >= 18) {
                PhotoViewer.this.containerView.setLayerType(PhotoViewer.PAGE_SPACING, null);
            }
            PhotoViewer.this.animationInProgress = PhotoViewer.PAGE_SPACING;
            PhotoViewer.this.onPhotoClosed(this.val$object);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.36 */
    class AnonymousClass36 implements Runnable {
        final /* synthetic */ PlaceProviderObject val$object;

        AnonymousClass36(PlaceProviderObject placeProviderObject) {
            this.val$object = placeProviderObject;
        }

        public void run() {
            if (PhotoViewer.this.containerView != null) {
                if (VERSION.SDK_INT >= 18) {
                    PhotoViewer.this.containerView.setLayerType(PhotoViewer.PAGE_SPACING, null);
                }
                PhotoViewer.this.animationInProgress = PhotoViewer.PAGE_SPACING;
                PhotoViewer.this.onPhotoClosed(this.val$object);
                PhotoViewer.this.containerView.setScaleX(TouchHelperCallback.ALPHA_FULL);
                PhotoViewer.this.containerView.setScaleY(TouchHelperCallback.ALPHA_FULL);
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.39 */
    class AnonymousClass39 implements Runnable {
        final /* synthetic */ int val$count;

        AnonymousClass39(int i) {
            this.val$count = i;
        }

        public void run() {
            PhotoViewer.this.redraw(this.val$count + PhotoViewer.gallery_menu_save);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.4 */
    class C13724 implements OnClickListener {
        C13724() {
        }

        public void onClick(View v) {
            PhotoViewer.this.onSharePressed();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.6 */
    class C13736 extends FrameLayout {
        C13736(Context x0) {
            super(x0);
        }

        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (!PhotoViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                return super.onTouchEvent(event);
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            invalidate();
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            long duration;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (PhotoViewer.this.videoPlayer != null) {
                duration = PhotoViewer.this.videoPlayer.getDuration();
                if (duration == -1) {
                    duration = 0;
                }
            } else {
                duration = 0;
            }
            duration /= 1000;
            TextPaint paint = PhotoViewer.this.videoPlayerTime.getPaint();
            Object[] objArr = new Object[PhotoViewer.gallery_menu_crop];
            objArr[PhotoViewer.PAGE_SPACING] = Long.valueOf(duration / 60);
            objArr[PhotoViewer.gallery_menu_save] = Long.valueOf(duration % 60);
            objArr[PhotoViewer.gallery_menu_showall] = Long.valueOf(duration / 60);
            objArr[PhotoViewer.gallery_menu_send] = Long.valueOf(duration % 60);
            PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) paint.measureText(String.format("%02d:%02d / %02d:%02d", objArr)))), getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
            PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
            canvas.restore();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.7 */
    class C13747 implements OnClickListener {
        C13747() {
        }

        public void onClick(View v) {
            if (PhotoViewer.this.videoPlayer == null) {
                return;
            }
            if (PhotoViewer.this.isPlaying) {
                PhotoViewer.this.videoPlayer.getPlayerControl().pause();
            } else {
                PhotoViewer.this.videoPlayer.getPlayerControl().start();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.8 */
    class C13758 implements OnClickListener {
        C13758() {
        }

        public void onClick(View view) {
            if (PhotoViewer.this.placeProvider != null) {
                boolean z;
                PhotoViewer photoViewer = PhotoViewer.this;
                if (PhotoViewer.this.placeProvider.cancelButtonPressed()) {
                    z = false;
                } else {
                    z = true;
                }
                photoViewer.closePhoto(z, false);
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.9 */
    class C13769 implements OnClickListener {
        C13769() {
        }

        public void onClick(View view) {
            if (PhotoViewer.this.placeProvider != null) {
                PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex);
                PhotoViewer.this.closePhoto(false, false);
            }
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private Runnable drawRunnable;

        public BackgroundDrawable(int color) {
            super(color);
        }

        public void setAlpha(int alpha) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (PhotoViewer.this.isVisible && alpha == NalUnitUtil.EXTENDED_SAR) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(alpha);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0 && this.drawRunnable != null) {
                this.drawRunnable.run();
                this.drawRunnable = null;
            }
        }
    }

    private class FrameLayoutTouchListener extends FrameLayout {
        private Runnable attachRunnable;
        private boolean attachedToWindow;

        public FrameLayoutTouchListener(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return PhotoViewer.getInstance().onTouchEvent(event);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            PhotoViewer.getInstance().onLayout(changed, left, top, right, bottom);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attachedToWindow = true;
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attachedToWindow = false;
        }
    }

    public interface PhotoViewerProvider {
        boolean cancelButtonPressed();

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        int getSelectedCount();

        Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        boolean isPhotoChecked(int i);

        void sendButtonPressed(int i);

        void setPhotoChecked(int i);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i);
    }

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public int dialogId;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int radius;
        public float scale;
        public int size;
        public Bitmap thumb;
        public int viewX;
        public int viewY;

        public PlaceProviderObject() {
            this.scale = TouchHelperCallback.ALPHA_FULL;
        }
    }

    private class RadialProgressView {
        private float alpha;
        private float animatedAlphaValue;
        private float animatedProgressValue;
        private float animationProgressStart;
        private int backgroundState;
        private float currentProgress;
        private long currentProgressTime;
        private long lastUpdateTime;
        private View parent;
        private int previousBackgroundState;
        private RectF progressRect;
        private float radOffset;
        private float scale;
        private int size;

        public RadialProgressView(Context context, View parentView) {
            this.lastUpdateTime = 0;
            this.radOffset = 0.0f;
            this.currentProgress = 0.0f;
            this.animationProgressStart = 0.0f;
            this.currentProgressTime = 0;
            this.animatedProgressValue = 0.0f;
            this.progressRect = new RectF();
            this.backgroundState = -1;
            this.parent = null;
            this.size = AndroidUtilities.dp(64.0f);
            this.previousBackgroundState = -2;
            this.animatedAlphaValue = TouchHelperCallback.ALPHA_FULL;
            this.alpha = TouchHelperCallback.ALPHA_FULL;
            this.scale = TouchHelperCallback.ALPHA_FULL;
            if (PhotoViewer.decelerateInterpolator == null) {
                PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                PhotoViewer.progressPaint = new Paint(PhotoViewer.gallery_menu_save);
                PhotoViewer.progressPaint.setStyle(Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.animatedProgressValue != TouchHelperCallback.ALPHA_FULL) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = this.currentProgress - this.animationProgressStart;
                if (progressDiff > 0.0f) {
                    this.currentProgressTime += dt;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA) * progressDiff);
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= TouchHelperCallback.ALPHA_FULL && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (animated) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = value;
                this.animationProgressStart = value;
            }
            this.currentProgress = value;
            this.currentProgressTime = 0;
        }

        public void setBackgroundState(int state, boolean animated) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (!animated || this.backgroundState == state) {
                this.previousBackgroundState = -2;
            } else {
                this.previousBackgroundState = this.backgroundState;
                this.animatedAlphaValue = TouchHelperCallback.ALPHA_FULL;
            }
            this.backgroundState = state;
            this.parent.invalidate();
        }

        public void setAlpha(float value) {
            this.alpha = value;
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int sizeScaled = (int) (((float) this.size) * this.scale);
            int x = (PhotoViewer.this.getContainerViewWidth() - sizeScaled) / PhotoViewer.gallery_menu_showall;
            int y = (PhotoViewer.this.getContainerViewHeight() - sizeScaled) / PhotoViewer.gallery_menu_showall;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < PhotoViewer.gallery_menu_crop) {
                drawable = PhotoViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState >= 0 && this.backgroundState < PhotoViewer.gallery_menu_crop) {
                drawable = PhotoViewer.progressDrawables[this.backgroundState];
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) (((TouchHelperCallback.ALPHA_FULL - this.animatedAlphaValue) * 255.0f) * this.alpha));
                    } else {
                        drawable.setAlpha((int) (this.alpha * 255.0f));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == PhotoViewer.gallery_menu_save || this.previousBackgroundState == 0 || this.previousBackgroundState == PhotoViewer.gallery_menu_save) {
                int diff = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, PhotoViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.22 */
    class AnonymousClass22 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: org.telegram.ui.PhotoViewer.22.1 */
        class C19101 extends AnimatorListenerAdapterProxy {
            C19101() {
            }

            public void onAnimationStart(Animator animation) {
                PhotoViewer.this.pickerView.setVisibility(PhotoViewer.PAGE_SPACING);
                PhotoViewer.this.actionBar.setVisibility(PhotoViewer.PAGE_SPACING);
                if (PhotoViewer.this.needCaptionLayout) {
                    PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.this.captionTextView.getTag() != null ? PhotoViewer.PAGE_SPACING : PhotoViewer.gallery_menu_crop);
                }
                if (PhotoViewer.this.sendPhotoType == 0) {
                    PhotoViewer.this.checkImageView.setVisibility(PhotoViewer.PAGE_SPACING);
                }
            }
        }

        AnonymousClass22(int i) {
            this.val$mode = i;
        }

        public void onAnimationEnd(Animator animation) {
            if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_save) {
                PhotoViewer.this.editorDoneLayout.setVisibility(PhotoViewer.gallery_menu_caption);
                PhotoViewer.this.photoCropView.setVisibility(PhotoViewer.gallery_menu_caption);
            } else if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_showall) {
                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                PhotoViewer.this.photoFilterView = null;
            }
            PhotoViewer.this.imageMoveAnimation = null;
            PhotoViewer.this.currentEditMode = this.val$mode;
            PhotoViewer.this.animateToScale = TouchHelperCallback.ALPHA_FULL;
            PhotoViewer.this.animateToX = 0.0f;
            PhotoViewer.this.animateToY = 0.0f;
            PhotoViewer.this.scale = TouchHelperCallback.ALPHA_FULL;
            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
            PhotoViewer.this.containerView.invalidate();
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> arrayList = new ArrayList();
            float[] fArr = new float[PhotoViewer.gallery_menu_save];
            fArr[PhotoViewer.PAGE_SPACING] = 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", fArr));
            fArr = new float[PhotoViewer.gallery_menu_save];
            fArr[PhotoViewer.PAGE_SPACING] = 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", fArr));
            if (PhotoViewer.this.needCaptionLayout) {
                fArr = new float[PhotoViewer.gallery_menu_save];
                fArr[PhotoViewer.PAGE_SPACING] = 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", fArr));
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
                fArr = new float[PhotoViewer.gallery_menu_save];
                fArr[PhotoViewer.PAGE_SPACING] = TouchHelperCallback.ALPHA_FULL;
                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", fArr));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200);
            animatorSet.addListener(new C19101());
            animatorSet.start();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.24 */
    class AnonymousClass24 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: org.telegram.ui.PhotoViewer.24.1 */
        class C19111 extends AnimatorListenerAdapterProxy {
            C19111() {
            }

            public void onAnimationStart(Animator animation) {
                PhotoViewer.this.editorDoneLayout.setVisibility(PhotoViewer.PAGE_SPACING);
                PhotoViewer.this.photoCropView.setVisibility(PhotoViewer.PAGE_SPACING);
            }

            public void onAnimationEnd(Animator animation) {
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = AnonymousClass24.this.val$mode;
                PhotoViewer.this.animateToScale = TouchHelperCallback.ALPHA_FULL;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer.this.scale = TouchHelperCallback.ALPHA_FULL;
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
            }
        }

        AnonymousClass24(int i) {
            this.val$mode = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onAnimationEnd(android.animation.Animator r20) {
            /*
            r19 = this;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 0;
            r11.changeModeAnimation = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.pickerView;
            r12 = 8;
            r11.setVisibility(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.actionBar;
            r12 = 8;
            r11.setVisibility(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.needCaptionLayout;
            if (r11 == 0) goto L_0x0038;
        L_0x002c:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.captionTextView;
            r12 = 4;
            r11.setVisibility(r12);
        L_0x0038:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.sendPhotoType;
            if (r11 != 0) goto L_0x004f;
        L_0x0042:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.checkImageView;
            r12 = 8;
            r11.setVisibility(r12);
        L_0x004f:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r2 = r11.getBitmap();
            if (r2 == 0) goto L_0x0107;
        L_0x005d:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = r11.photoCropView;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r13 = r11.getOrientation();
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.sendPhotoType;
            r14 = 1;
            if (r11 == r14) goto L_0x01ae;
        L_0x007c:
            r11 = 1;
        L_0x007d:
            r12.setBitmap(r2, r13, r11);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r4 = r11.getBitmapWidth();
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r3 = r11.getBitmapHeight();
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.getContainerViewWidth();
            r11 = (float) r11;
            r12 = (float) r4;
            r9 = r11 / r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.getContainerViewHeight();
            r11 = (float) r11;
            r12 = (float) r3;
            r10 = r11 / r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 1;
            r11 = r11.getContainerViewWidth(r12);
            r11 = (float) r11;
            r12 = (float) r4;
            r6 = r11 / r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 1;
            r11 = r11.getContainerViewHeight(r12);
            r11 = (float) r11;
            r12 = (float) r3;
            r7 = r11 / r12;
            r11 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1));
            if (r11 <= 0) goto L_0x01b1;
        L_0x00ce:
            r8 = r10;
        L_0x00cf:
            r11 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
            if (r11 <= 0) goto L_0x01b4;
        L_0x00d3:
            r5 = r7;
        L_0x00d4:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = r5 / r8;
            r11.animateToScale = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 0;
            r11.animateToX = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 1103101952; // 0x41c00000 float:24.0 double:5.450047783E-315;
            r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r12 = -r12;
            r12 = (float) r12;
            r11.animateToY = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = java.lang.System.currentTimeMillis();
            r11.animationStartTime = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 1;
            r11.zoomAnimation = r12;
        L_0x0107:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = new android.animation.AnimatorSet;
            r12.<init>();
            r11.imageMoveAnimation = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r12 = 3;
            r12 = new android.animation.Animator[r12];
            r13 = 0;
            r0 = r19;
            r14 = org.telegram.ui.PhotoViewer.this;
            r14 = r14.editorDoneLayout;
            r15 = "translationY";
            r16 = 2;
            r0 = r16;
            r0 = new float[r0];
            r16 = r0;
            r17 = 0;
            r18 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
            r18 = org.telegram.messenger.AndroidUtilities.dp(r18);
            r0 = r18;
            r0 = (float) r0;
            r18 = r0;
            r16[r17] = r18;
            r17 = 1;
            r18 = 0;
            r16[r17] = r18;
            r14 = android.animation.ObjectAnimator.ofFloat(r14, r15, r16);
            r12[r13] = r14;
            r13 = 1;
            r0 = r19;
            r14 = org.telegram.ui.PhotoViewer.this;
            r15 = "animationValue";
            r16 = 2;
            r0 = r16;
            r0 = new float[r0];
            r16 = r0;
            r16 = {0, 1065353216};
            r14 = android.animation.ObjectAnimator.ofFloat(r14, r15, r16);
            r12[r13] = r14;
            r13 = 2;
            r0 = r19;
            r14 = org.telegram.ui.PhotoViewer.this;
            r14 = r14.photoCropView;
            r15 = "alpha";
            r16 = 2;
            r0 = r16;
            r0 = new float[r0];
            r16 = r0;
            r16 = {0, 1065353216};
            r14 = android.animation.ObjectAnimator.ofFloat(r14, r15, r16);
            r12[r13] = r14;
            r11.playTogether(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r12 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            r11.setDuration(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r12 = new org.telegram.ui.PhotoViewer$24$1;
            r0 = r19;
            r12.<init>();
            r11.addListener(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r11.start();
            return;
        L_0x01ae:
            r11 = 0;
            goto L_0x007d;
        L_0x01b1:
            r8 = r9;
            goto L_0x00cf;
        L_0x01b4:
            r5 = r6;
            goto L_0x00d4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.24.onAnimationEnd(android.animation.Animator):void");
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.27 */
    class AnonymousClass27 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: org.telegram.ui.PhotoViewer.27.1 */
        class C19121 extends AnimatorListenerAdapterProxy {
            C19121() {
            }

            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                PhotoViewer.this.photoFilterView.init();
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = AnonymousClass27.this.val$mode;
                PhotoViewer.this.animateToScale = TouchHelperCallback.ALPHA_FULL;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer.this.scale = TouchHelperCallback.ALPHA_FULL;
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
            }
        }

        AnonymousClass27(int i) {
            this.val$mode = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onAnimationEnd(android.animation.Animator r20) {
            /*
            r19 = this;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 0;
            r11.changeModeAnimation = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.pickerView;
            r12 = 8;
            r11.setVisibility(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.actionBar;
            r12 = 8;
            r11.setVisibility(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.needCaptionLayout;
            if (r11 == 0) goto L_0x0038;
        L_0x002c:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.captionTextView;
            r12 = 4;
            r11.setVisibility(r12);
        L_0x0038:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.sendPhotoType;
            if (r11 != 0) goto L_0x004f;
        L_0x0042:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.checkImageView;
            r12 = 8;
            r11.setVisibility(r12);
        L_0x004f:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r2 = r11.getBitmap();
            if (r2 == 0) goto L_0x00e4;
        L_0x005d:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r4 = r11.getBitmapWidth();
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.centerImage;
            r3 = r11.getBitmapHeight();
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.getContainerViewWidth();
            r11 = (float) r11;
            r12 = (float) r4;
            r9 = r11 / r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.getContainerViewHeight();
            r11 = (float) r11;
            r12 = (float) r3;
            r10 = r11 / r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 2;
            r11 = r11.getContainerViewWidth(r12);
            r11 = (float) r11;
            r12 = (float) r4;
            r6 = r11 / r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 2;
            r11 = r11.getContainerViewHeight(r12);
            r11 = (float) r11;
            r12 = (float) r3;
            r7 = r11 / r12;
            r11 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1));
            if (r11 <= 0) goto L_0x0173;
        L_0x00ab:
            r8 = r10;
        L_0x00ac:
            r11 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
            if (r11 <= 0) goto L_0x0176;
        L_0x00b0:
            r5 = r7;
        L_0x00b1:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = r5 / r8;
            r11.animateToScale = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 0;
            r11.animateToX = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 1115160576; // 0x42780000 float:62.0 double:5.5096253E-315;
            r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r12 = -r12;
            r12 = (float) r12;
            r11.animateToY = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = java.lang.System.currentTimeMillis();
            r11.animationStartTime = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = 1;
            r11.zoomAnimation = r12;
        L_0x00e4:
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r12 = new android.animation.AnimatorSet;
            r12.<init>();
            r11.imageMoveAnimation = r12;
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r12 = 2;
            r12 = new android.animation.Animator[r12];
            r13 = 0;
            r0 = r19;
            r14 = org.telegram.ui.PhotoViewer.this;
            r15 = "animationValue";
            r16 = 2;
            r0 = r16;
            r0 = new float[r0];
            r16 = r0;
            r16 = {0, 1065353216};
            r14 = android.animation.ObjectAnimator.ofFloat(r14, r15, r16);
            r12[r13] = r14;
            r13 = 1;
            r0 = r19;
            r14 = org.telegram.ui.PhotoViewer.this;
            r14 = r14.photoFilterView;
            r14 = r14.getToolsView();
            r15 = "translationY";
            r16 = 2;
            r0 = r16;
            r0 = new float[r0];
            r16 = r0;
            r17 = 0;
            r18 = 1123811328; // 0x42fc0000 float:126.0 double:5.552365696E-315;
            r18 = org.telegram.messenger.AndroidUtilities.dp(r18);
            r0 = r18;
            r0 = (float) r0;
            r18 = r0;
            r16[r17] = r18;
            r17 = 1;
            r18 = 0;
            r16[r17] = r18;
            r14 = android.animation.ObjectAnimator.ofFloat(r14, r15, r16);
            r12[r13] = r14;
            r11.playTogether(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r12 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            r11.setDuration(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r12 = new org.telegram.ui.PhotoViewer$27$1;
            r0 = r19;
            r12.<init>();
            r11.addListener(r12);
            r0 = r19;
            r11 = org.telegram.ui.PhotoViewer.this;
            r11 = r11.imageMoveAnimation;
            r11.start();
            return;
        L_0x0173:
            r8 = r9;
            goto L_0x00ac;
        L_0x0176:
            r5 = r6;
            goto L_0x00b1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.27.onAnimationEnd(android.animation.Animator):void");
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.2 */
    class C19132 extends FrameLayoutTouchListener {
        C19132(Context x0) {
            super(x0);
        }

        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (event == null || event.getKeyCode() != PhotoViewer.gallery_menu_crop || event.getAction() != PhotoViewer.gallery_menu_save) {
                return super.dispatchKeyEventPreIme(event);
            }
            if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                PhotoViewer.this.closeCaptionEnter(false);
                return false;
            }
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        }

        public ActionMode startActionModeForChild(View originalView, Callback callback, int type) {
            if (VERSION.SDK_INT >= 23) {
                View view = PhotoViewer.this.parentActivity.findViewById(16908290);
                if (view instanceof ViewGroup) {
                    try {
                        return ((ViewGroup) view).startActionModeForChild(originalView, callback, type);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
            return super.startActionModeForChild(originalView, callback, type);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.3 */
    class C19143 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.PhotoViewer.3.1 */
        class C13691 implements DialogInterface.OnClickListener {
            C13691() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (PhotoViewer.this.imagesArr.isEmpty()) {
                    if (!PhotoViewer.this.avatarsArr.isEmpty() && PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
                        Photo photo = (Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                        FileLocation currentLocation = (FileLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                        if (photo instanceof TL_photoEmpty) {
                            photo = null;
                        }
                        boolean current = false;
                        if (PhotoViewer.this.currentUserAvatarLocation != null) {
                            if (photo != null) {
                                Iterator i$ = photo.sizes.iterator();
                                while (i$.hasNext()) {
                                    PhotoSize size = (PhotoSize) i$.next();
                                    if (size.location.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id) {
                                        if (size.location.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                            current = true;
                                            break;
                                        }
                                    }
                                }
                            } else if (currentLocation.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id) {
                                if (currentLocation.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                    current = true;
                                }
                            }
                        }
                        if (current) {
                            MessagesController.getInstance().deleteUserPhoto(null);
                            PhotoViewer.this.closePhoto(false, false);
                        } else if (photo != null) {
                            TL_inputPhoto inputPhoto = new TL_inputPhoto();
                            inputPhoto.id = photo.id;
                            inputPhoto.access_hash = photo.access_hash;
                            MessagesController.getInstance().deleteUserPhoto(inputPhoto);
                            MessagesStorage.getInstance().clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo.id);
                            PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                            PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                            PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                            if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                PhotoViewer.this.closePhoto(false, false);
                                return;
                            }
                            int index = PhotoViewer.this.currentIndex;
                            if (index >= PhotoViewer.this.avatarsArr.size()) {
                                index = PhotoViewer.this.avatarsArr.size() - 1;
                            }
                            PhotoViewer.this.currentIndex = -1;
                            PhotoViewer.this.setImageIndex(index, true);
                        }
                    }
                } else if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                    MessageObject obj = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                    if (obj.isSent()) {
                        PhotoViewer.this.closePhoto(false, false);
                        ArrayList<Integer> arr = new ArrayList();
                        arr.add(Integer.valueOf(obj.getId()));
                        ArrayList<Long> random_ids = null;
                        EncryptedChat encryptedChat = null;
                        if (((int) obj.getDialogId()) == 0 && obj.messageOwner.random_id != 0) {
                            random_ids = new ArrayList();
                            random_ids.add(Long.valueOf(obj.messageOwner.random_id));
                            encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (obj.getDialogId() >> 32)));
                        }
                        MessagesController.getInstance().deleteMessages(arr, random_ids, encryptedChat, obj.messageOwner.to_id.channel_id);
                    }
                }
            }
        }

        C19143() {
        }

        public void onItemClick(int id) {
            int i = PhotoViewer.gallery_menu_save;
            if (id == -1) {
                if (PhotoViewer.this.needCaptionLayout && (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                    PhotoViewer.this.closeCaptionEnter(false);
                } else {
                    PhotoViewer.this.closePhoto(true, false);
                }
            } else if (id == PhotoViewer.gallery_menu_save) {
                if (VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                    File f = null;
                    if (PhotoViewer.this.currentMessageObject != null) {
                        f = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                    } else if (PhotoViewer.this.currentFileLocation != null) {
                        f = FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0);
                    }
                    if (f == null || !f.exists()) {
                        builder = new Builder(PhotoViewer.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                        builder.setMessage(LocaleController.getString("PleaseDownload", C0691R.string.PleaseDownload));
                        PhotoViewer.this.showAlertDialog(builder);
                        return;
                    }
                    String file = f.toString();
                    Context access$600 = PhotoViewer.this.parentActivity;
                    if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                        i = PhotoViewer.PAGE_SPACING;
                    }
                    MediaController.saveFile(file, access$600, i, null, null);
                    return;
                }
                Activity access$6002 = PhotoViewer.this.parentActivity;
                String[] strArr = new String[PhotoViewer.gallery_menu_save];
                strArr[PhotoViewer.PAGE_SPACING] = "android.permission.WRITE_EXTERNAL_STORAGE";
                access$6002.requestPermissions(strArr, PhotoViewer.gallery_menu_crop);
            } else if (id == PhotoViewer.gallery_menu_showall) {
                if (PhotoViewer.this.opennedFromMedia) {
                    PhotoViewer.this.closePhoto(true, false);
                } else if (PhotoViewer.this.currentDialogId != 0) {
                    PhotoViewer.this.disableShowCheck = true;
                    PhotoViewer.this.closePhoto(false, false);
                    Bundle args2 = new Bundle();
                    args2.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                    ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(new MediaActivity(args2), false, true);
                }
            } else if (id == PhotoViewer.gallery_menu_send) {
            } else {
                if (id == PhotoViewer.gallery_menu_crop) {
                    PhotoViewer.this.switchToEditMode(PhotoViewer.gallery_menu_save);
                } else if (id == PhotoViewer.gallery_menu_tune) {
                    PhotoViewer.this.switchToEditMode(PhotoViewer.gallery_menu_showall);
                } else if (id == PhotoViewer.gallery_menu_delete) {
                    if (PhotoViewer.this.parentActivity != null) {
                        builder = new Builder(PhotoViewer.this.parentActivity);
                        if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", C0691R.string.AreYouSureDeletePhoto, new Object[PhotoViewer.PAGE_SPACING]));
                        } else {
                            builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", C0691R.string.AreYouSureDeleteVideo, new Object[PhotoViewer.PAGE_SPACING]));
                        }
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C13691());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        PhotoViewer.this.showAlertDialog(builder);
                    }
                } else if (id == PhotoViewer.gallery_menu_caption) {
                    if (PhotoViewer.this.imageMoveAnimation == null && PhotoViewer.this.changeModeAnimation == null) {
                        PhotoViewer.this.cropItem.setVisibility(PhotoViewer.gallery_menu_caption);
                        PhotoViewer.this.tuneItem.setVisibility(PhotoViewer.gallery_menu_caption);
                        PhotoViewer.this.captionItem.setVisibility(PhotoViewer.gallery_menu_caption);
                        PhotoViewer.this.checkImageView.setVisibility(PhotoViewer.gallery_menu_caption);
                        PhotoViewer.this.captionDoneItem.setVisibility(PhotoViewer.PAGE_SPACING);
                        PhotoViewer.this.pickerView.setVisibility(PhotoViewer.gallery_menu_caption);
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.captionEditText.getLayoutParams();
                        layoutParams.bottomMargin = PhotoViewer.PAGE_SPACING;
                        PhotoViewer.this.captionEditText.setLayoutParams(layoutParams);
                        layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        layoutParams.bottomMargin = PhotoViewer.PAGE_SPACING;
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams);
                        PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.gallery_menu_crop);
                        PhotoViewer.this.captionEditText.openKeyboard();
                        PhotoViewer.this.lastTitle = PhotoViewer.this.actionBar.getTitle();
                        PhotoViewer.this.actionBar.setTitle(LocaleController.getString("PhotoCaption", C0691R.string.PhotoCaption));
                    }
                } else if (id == PhotoViewer.gallery_menu_caption_done) {
                    PhotoViewer.this.closeCaptionEnter(true);
                } else if (id == PhotoViewer.gallery_menu_share) {
                    PhotoViewer.this.onSharePressed();
                } else if (id == PhotoViewer.gallery_menu_openin) {
                    try {
                        AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                        PhotoViewer.this.closePhoto(false, false);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
        }

        public boolean canOpenMenu() {
            if (PhotoViewer.this.currentMessageObject != null) {
                if (FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists()) {
                    return true;
                }
            } else if (PhotoViewer.this.currentFileLocation != null) {
                if (FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0).exists()) {
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.5 */
    class C19155 implements SeekBarDelegate {
        C19155() {
        }

        public void onSeekBarDrag(float progress) {
            if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.getPlayerControl().seekTo((int) (((float) PhotoViewer.this.videoPlayer.getDuration()) * progress));
            }
        }
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
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
            return PhotoViewer.PAGE_SPACING;
        }

        public void updatePhotoAtIndex(int index) {
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (heightSize > AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight) {
                heightSize = AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight;
            }
            setMeasuredDimension(widthSize, heightSize);
            int childCount = getChildCount();
            for (int i = PhotoViewer.PAGE_SPACING; i < childCount; i += PhotoViewer.gallery_menu_save) {
                View child = getChildAt(i);
                if (child.getVisibility() != PhotoViewer.gallery_menu_caption) {
                    if (PhotoViewer.this.captionEditText.isPopupView(child)) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C0747C.ENCODING_PCM_32BIT));
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, PhotoViewer.PAGE_SPACING, heightMeasureSpec, PhotoViewer.PAGE_SPACING);
                    }
                }
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            int paddingBottom = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PhotoViewer.this.captionEditText.getEmojiPadding() : PhotoViewer.PAGE_SPACING;
            for (int i = PhotoViewer.PAGE_SPACING; i < count; i += PhotoViewer.gallery_menu_save) {
                View child = getChildAt(i);
                if (child.getVisibility() != PhotoViewer.gallery_menu_caption) {
                    int childLeft;
                    int childTop;
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    switch ((gravity & PhotoViewer.gallery_menu_tune) & PhotoViewer.gallery_menu_tune) {
                        case PhotoViewer.gallery_menu_save /*1*/:
                            childLeft = ((((r - l) - width) / PhotoViewer.gallery_menu_showall) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            childLeft = (r - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case ItemTouchHelper.START /*16*/:
                            childTop = (((((b - paddingBottom) - t) - height) / PhotoViewer.gallery_menu_showall) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            childTop = lp.topMargin;
                            break;
                        case 80:
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    if (child == PhotoViewer.this.mentionListView) {
                        if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible() || PhotoViewer.this.captionEditText.getEmojiPadding() != 0) {
                            childTop -= PhotoViewer.this.captionEditText.getMeasuredHeight();
                        } else {
                            childTop += AndroidUtilities.dp(400.0f);
                        }
                    } else if (child == PhotoViewer.this.captionEditText) {
                        if (!(PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible() || PhotoViewer.this.captionEditText.getEmojiPadding() != 0)) {
                            childTop += AndroidUtilities.dp(400.0f);
                        }
                    } else if (child == PhotoViewer.this.pickerView || child == PhotoViewer.this.captionTextViewNew || child == PhotoViewer.this.captionTextViewOld) {
                        if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                            childTop += AndroidUtilities.dp(400.0f);
                        }
                    } else if (PhotoViewer.this.captionEditText.isPopupView(child)) {
                        childTop = PhotoViewer.this.captionEditText.getBottom();
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            notifyHeightChanged();
        }

        protected void onDraw(Canvas canvas) {
            PhotoViewer.getInstance().onDraw(canvas);
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return child != PhotoViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, child, drawingTime);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer.15 */
    class AnonymousClass15 extends LinearLayoutManager {
        AnonymousClass15(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    public PhotoViewer() {
        this.isActionBarVisible = true;
        this.backgroundDrawable = new BackgroundDrawable(ViewCompat.MEASURED_STATE_MASK);
        this.radialProgressViews = new RadialProgressView[gallery_menu_send];
        this.canShowBottom = true;
        this.sendPhotoType = PAGE_SPACING;
        this.updateProgressRunnable = new C13671();
        this.animationValues = (float[][]) Array.newInstance(Float.TYPE, new int[]{gallery_menu_showall, gallery_menu_caption});
        this.animationInProgress = PAGE_SPACING;
        this.transitionAnimationStartTime = 0;
        this.animationEndRunnable = null;
        this.disableShowCheck = false;
        this.leftImage = new ImageReceiver();
        this.centerImage = new ImageReceiver();
        this.rightImage = new ImageReceiver();
        this.currentFileNames = new String[gallery_menu_send];
        this.currentThumb = null;
        this.endReached = new boolean[]{false, true};
        this.draggingDown = false;
        this.scale = TouchHelperCallback.ALPHA_FULL;
        this.interpolator = new DecelerateInterpolator(1.5f);
        this.pinchStartScale = TouchHelperCallback.ALPHA_FULL;
        this.canZoom = true;
        this.changingPage = false;
        this.zooming = false;
        this.moving = false;
        this.doubleTap = false;
        this.invalidCoords = false;
        this.canDragDown = true;
        this.zoomAnimation = false;
        this.discardTap = false;
        this.switchImageAfterAnimation = PAGE_SPACING;
        this.velocityTracker = null;
        this.scroller = null;
        this.imagesArrTemp = new ArrayList();
        HashMap[] hashMapArr = new HashMap[gallery_menu_showall];
        hashMapArr[PAGE_SPACING] = new HashMap();
        hashMapArr[gallery_menu_save] = new HashMap();
        this.imagesByIdsTemp = hashMapArr;
        this.imagesArr = new ArrayList();
        hashMapArr = new HashMap[gallery_menu_showall];
        hashMapArr[PAGE_SPACING] = new HashMap();
        hashMapArr[gallery_menu_save] = new HashMap();
        this.imagesByIds = hashMapArr;
        this.imagesArrLocations = new ArrayList();
        this.avatarsArr = new ArrayList();
        this.imagesArrLocationsSizes = new ArrayList();
        this.imagesArrLocals = new ArrayList();
        this.currentUserAvatarLocation = null;
    }

    static {
        PAGE_SPACING = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        decelerateInterpolator = null;
        progressPaint = null;
        Instance = null;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        PhotoViewer localInstance2 = new PhotoViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        int a;
        if (id == NotificationCenter.FileDidFailedLoad) {
            location = args[PAGE_SPACING];
            a = PAGE_SPACING;
            while (a < gallery_menu_send) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a += gallery_menu_save;
                } else {
                    this.radialProgressViews[a].setProgress(TouchHelperCallback.ALPHA_FULL, true);
                    checkProgress(a, true);
                    return;
                }
            }
        } else if (id == NotificationCenter.FileDidLoaded) {
            location = (String) args[PAGE_SPACING];
            a = PAGE_SPACING;
            while (a < gallery_menu_send) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a += gallery_menu_save;
                } else {
                    this.radialProgressViews[a].setProgress(TouchHelperCallback.ALPHA_FULL, true);
                    checkProgress(a, true);
                    if (VERSION.SDK_INT >= 16 && a == 0 && this.currentMessageObject != null && this.currentMessageObject.isVideo()) {
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            location = (String) args[PAGE_SPACING];
            a = PAGE_SPACING;
            while (a < gallery_menu_send) {
                if (this.currentFileNames[a] != null && this.currentFileNames[a].equals(location)) {
                    this.radialProgressViews[a].setProgress(args[gallery_menu_save].floatValue(), true);
                }
                a += gallery_menu_save;
            }
        } else if (id == NotificationCenter.dialogPhotosLoaded) {
            guid = ((Integer) args[gallery_menu_crop]).intValue();
            if (this.avatarsDialogId == ((Integer) args[PAGE_SPACING]).intValue() && this.classGuid == guid) {
                boolean fromCache = ((Boolean) args[gallery_menu_send]).booleanValue();
                int setToImage = -1;
                ArrayList<Photo> photos = args[5];
                if (!photos.isEmpty()) {
                    this.imagesArrLocations.clear();
                    this.imagesArrLocationsSizes.clear();
                    this.avatarsArr.clear();
                    for (a = PAGE_SPACING; a < photos.size(); a += gallery_menu_save) {
                        Photo photo = (Photo) photos.get(a);
                        if (!(photo == null || (photo instanceof TL_photoEmpty) || photo.sizes == null)) {
                            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                            if (sizeFull != null) {
                                if (setToImage == -1 && this.currentFileLocation != null) {
                                    for (int b = PAGE_SPACING; b < photo.sizes.size(); b += gallery_menu_save) {
                                        PhotoSize size = (PhotoSize) photo.sizes.get(b);
                                        if (size.location.local_id == this.currentFileLocation.local_id && size.location.volume_id == this.currentFileLocation.volume_id) {
                                            setToImage = this.imagesArrLocations.size();
                                            break;
                                        }
                                    }
                                }
                                this.imagesArrLocations.add(sizeFull.location);
                                this.imagesArrLocationsSizes.add(Integer.valueOf(sizeFull.size));
                                this.avatarsArr.add(photo);
                            }
                        }
                    }
                    if (this.avatarsArr.isEmpty()) {
                        this.menuItem.hideSubItem(gallery_menu_delete);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_delete);
                    }
                    this.needSearchImageInArr = false;
                    this.currentIndex = -1;
                    if (setToImage != -1) {
                        setImageIndex(setToImage, true);
                    } else {
                        this.avatarsArr.add(PAGE_SPACING, new TL_photoEmpty());
                        this.imagesArrLocations.add(PAGE_SPACING, this.currentFileLocation);
                        this.imagesArrLocationsSizes.add(PAGE_SPACING, Integer.valueOf(PAGE_SPACING));
                        setImageIndex(PAGE_SPACING, true);
                    }
                    if (fromCache) {
                        MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, PAGE_SPACING, 80, 0, false, this.classGuid);
                    }
                }
            }
        } else if (id == NotificationCenter.mediaCountDidLoaded) {
            uid = ((Long) args[PAGE_SPACING]).longValue();
            if (uid == this.currentDialogId || uid == this.mergeDialogId) {
                if (uid == this.currentDialogId) {
                    this.totalImagesCount = ((Integer) args[gallery_menu_save]).intValue();
                } else if (uid == this.mergeDialogId) {
                    this.totalImagesCountMerge = ((Integer) args[gallery_menu_save]).intValue();
                }
                if (this.needSearchImageInArr && this.isFirstLoading) {
                    this.isFirstLoading = false;
                    this.loadingMoreImages = true;
                    SharedMediaQuery.loadMedia(this.currentDialogId, PAGE_SPACING, 80, PAGE_SPACING, PAGE_SPACING, true, this.classGuid);
                } else if (!this.imagesArr.isEmpty()) {
                    ActionBar actionBar;
                    Object[] objArr;
                    if (this.opennedFromMedia) {
                        actionBar = this.actionBar;
                        objArr = new Object[gallery_menu_showall];
                        objArr[PAGE_SPACING] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                        objArr[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                        actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                        return;
                    }
                    actionBar = this.actionBar;
                    objArr = new Object[gallery_menu_showall];
                    objArr[PAGE_SPACING] = Integer.valueOf((((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex) + gallery_menu_save);
                    objArr[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                    actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                }
            }
        } else if (id == NotificationCenter.mediaDidLoaded) {
            uid = ((Long) args[PAGE_SPACING]).longValue();
            guid = ((Integer) args[gallery_menu_send]).intValue();
            if ((uid == this.currentDialogId || uid == this.mergeDialogId) && guid == this.classGuid) {
                this.loadingMoreImages = false;
                int loadIndex = uid == this.currentDialogId ? PAGE_SPACING : gallery_menu_save;
                ArrayList<MessageObject> arr = args[gallery_menu_showall];
                this.endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                int added;
                MessageObject message;
                if (!this.needSearchImageInArr) {
                    added = PAGE_SPACING;
                    Iterator i$ = arr.iterator();
                    while (i$.hasNext()) {
                        message = (MessageObject) i$.next();
                        if (!this.imagesByIds[loadIndex].containsKey(Integer.valueOf(message.getId()))) {
                            added += gallery_menu_save;
                            if (this.opennedFromMedia) {
                                this.imagesArr.add(message);
                            } else {
                                this.imagesArr.add(PAGE_SPACING, message);
                            }
                            this.imagesByIds[loadIndex].put(Integer.valueOf(message.getId()), message);
                        }
                    }
                    if (this.opennedFromMedia) {
                        if (added == 0) {
                            this.totalImagesCount = this.imagesArr.size();
                            this.totalImagesCountMerge = PAGE_SPACING;
                        }
                    } else if (added != 0) {
                        int index = this.currentIndex;
                        this.currentIndex = -1;
                        setImageIndex(index + added, true);
                    } else {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = PAGE_SPACING;
                    }
                } else if (!arr.isEmpty() || (loadIndex == 0 && this.mergeDialogId != 0)) {
                    int foundIndex = -1;
                    MessageObject currentMessage = (MessageObject) this.imagesArr.get(this.currentIndex);
                    added = PAGE_SPACING;
                    for (a = PAGE_SPACING; a < arr.size(); a += gallery_menu_save) {
                        message = (MessageObject) arr.get(a);
                        if (!this.imagesByIdsTemp[loadIndex].containsKey(Integer.valueOf(message.getId()))) {
                            this.imagesByIdsTemp[loadIndex].put(Integer.valueOf(message.getId()), message);
                            if (this.opennedFromMedia) {
                                this.imagesArrTemp.add(message);
                                if (message.getId() == currentMessage.getId()) {
                                    foundIndex = added;
                                }
                                added += gallery_menu_save;
                            } else {
                                added += gallery_menu_save;
                                this.imagesArrTemp.add(PAGE_SPACING, message);
                                if (message.getId() == currentMessage.getId()) {
                                    foundIndex = arr.size() - added;
                                }
                            }
                        }
                    }
                    if (added == 0 && (loadIndex != 0 || this.mergeDialogId == 0)) {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = PAGE_SPACING;
                    }
                    if (foundIndex != -1) {
                        this.imagesArr.clear();
                        this.imagesArr.addAll(this.imagesArrTemp);
                        for (a = PAGE_SPACING; a < gallery_menu_showall; a += gallery_menu_save) {
                            this.imagesByIds[a].clear();
                            this.imagesByIds[a].putAll(this.imagesByIdsTemp[a]);
                            this.imagesByIdsTemp[a].clear();
                        }
                        this.imagesArrTemp.clear();
                        this.needSearchImageInArr = false;
                        this.currentIndex = -1;
                        if (foundIndex >= this.imagesArr.size()) {
                            foundIndex = this.imagesArr.size() - 1;
                        }
                        setImageIndex(foundIndex, true);
                        return;
                    }
                    int loadFromMaxId;
                    if (this.opennedFromMedia) {
                        loadFromMaxId = this.imagesArrTemp.isEmpty() ? PAGE_SPACING : ((MessageObject) this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getId();
                        if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                            loadIndex = gallery_menu_save;
                            if (!(this.imagesArrTemp.isEmpty() || ((MessageObject) this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getDialogId() == this.mergeDialogId)) {
                                loadFromMaxId = PAGE_SPACING;
                            }
                        }
                    } else {
                        if (this.imagesArrTemp.isEmpty()) {
                            loadFromMaxId = PAGE_SPACING;
                        } else {
                            loadFromMaxId = ((MessageObject) this.imagesArrTemp.get(PAGE_SPACING)).getId();
                        }
                        if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                            loadIndex = gallery_menu_save;
                            if (!(this.imagesArrTemp.isEmpty() || ((MessageObject) this.imagesArrTemp.get(PAGE_SPACING)).getDialogId() == this.mergeDialogId)) {
                                loadFromMaxId = PAGE_SPACING;
                            }
                        }
                    }
                    if (!this.endReached[loadIndex]) {
                        this.loadingMoreImages = true;
                        if (this.opennedFromMedia) {
                            long j;
                            if (loadIndex == 0) {
                                j = this.currentDialogId;
                            } else {
                                j = this.mergeDialogId;
                            }
                            SharedMediaQuery.loadMedia(j, PAGE_SPACING, 80, loadFromMaxId, PAGE_SPACING, true, this.classGuid);
                            return;
                        }
                        SharedMediaQuery.loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, PAGE_SPACING, 80, loadFromMaxId, PAGE_SPACING, true, this.classGuid);
                    }
                } else {
                    this.needSearchImageInArr = false;
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded && this.captionTextView != null) {
            this.captionTextView.invalidate();
        }
    }

    private void onSharePressed() {
        if (this.parentActivity != null) {
            File f = null;
            boolean isVideo = false;
            try {
                if (this.currentMessageObject != null) {
                    isVideo = this.currentMessageObject.isVideo();
                    f = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                } else if (this.currentFileLocation != null) {
                    f = FileLoader.getPathToAttach(this.currentFileLocation, this.avatarsDialogId != 0);
                }
                if (f.exists()) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    if (isVideo) {
                        intent.setType(MimeTypes.VIDEO_MP4);
                    } else {
                        intent.setType("image/jpeg");
                    }
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", C0691R.string.ShareFile)), 500);
                    return;
                }
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
                builder.setMessage(LocaleController.getString("PleaseDownload", C0691R.string.PleaseDownload));
                showAlertDialog(builder);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.actvityContext = new ContextThemeWrapper(this.parentActivity, C0691R.style.Theme_TMessages);
            if (progressDrawables == null) {
                progressDrawables = new Drawable[gallery_menu_crop];
                progressDrawables[PAGE_SPACING] = this.parentActivity.getResources().getDrawable(C0691R.drawable.circle_big);
                progressDrawables[gallery_menu_save] = this.parentActivity.getResources().getDrawable(C0691R.drawable.cancel_big);
                progressDrawables[gallery_menu_showall] = this.parentActivity.getResources().getDrawable(C0691R.drawable.load_big);
                progressDrawables[gallery_menu_send] = this.parentActivity.getResources().getDrawable(C0691R.drawable.play_big);
            }
            this.scroller = new Scroller(activity);
            this.windowView = new C19132(activity);
            this.windowView.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setFocusable(false);
            if (VERSION.SDK_INT >= 23) {
                this.windowView.setFitsSystemWindows(true);
            }
            this.animatingImageView = new ClippingImageView(activity);
            this.animatingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = gallery_menu_caption;
            this.actionBar = new ActionBar(activity);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(false);
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR);
            this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
            ActionBar actionBar = this.actionBar;
            Object[] objArr = new Object[gallery_menu_showall];
            objArr[PAGE_SPACING] = Integer.valueOf(gallery_menu_save);
            objArr[gallery_menu_save] = Integer.valueOf(gallery_menu_save);
            actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new C19143());
            ActionBarMenu menu = this.actionBar.createMenu();
            this.menuItem = menu.addItem((int) PAGE_SPACING, (int) C0691R.drawable.ic_ab_other);
            String str = LocaleController.getString("OpenInBrowser", C0691R.string.OpenInBrowser);
            if (!TextUtils.isEmpty(str)) {
                str = str.substring(PAGE_SPACING, gallery_menu_save).toUpperCase() + str.substring(gallery_menu_save).toLowerCase();
            }
            this.menuItem.addSubItem(gallery_menu_openin, str, PAGE_SPACING);
            this.menuItem.addSubItem(gallery_menu_showall, LocaleController.getString("ShowAllMedia", C0691R.string.ShowAllMedia), PAGE_SPACING);
            this.menuItem.addSubItem(gallery_menu_share, LocaleController.getString("ShareFile", C0691R.string.ShareFile), PAGE_SPACING);
            this.menuItem.addSubItem(gallery_menu_save, LocaleController.getString("SaveToGallery", C0691R.string.SaveToGallery), PAGE_SPACING);
            this.menuItem.addSubItem(gallery_menu_delete, LocaleController.getString("Delete", C0691R.string.Delete), PAGE_SPACING);
            this.captionDoneItem = menu.addItemWithWidth(gallery_menu_caption_done, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            this.captionItem = menu.addItemWithWidth(gallery_menu_caption, C0691R.drawable.photo_text, AndroidUtilities.dp(56.0f));
            this.cropItem = menu.addItemWithWidth(gallery_menu_crop, C0691R.drawable.photo_crop, AndroidUtilities.dp(56.0f));
            this.tuneItem = menu.addItemWithWidth(gallery_menu_tune, C0691R.drawable.photo_tools, AndroidUtilities.dp(56.0f));
            this.bottomLayout = new FrameLayout(this.actvityContext);
            this.bottomLayout.setBackgroundColor(2130706432);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.captionTextViewOld = new TextView(this.actvityContext);
            this.captionTextViewOld.setMaxLines(gallery_menu_share);
            this.captionTextViewOld.setBackgroundColor(2130706432);
            this.captionTextViewOld.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            this.captionTextViewOld.setLinkTextColor(-1);
            this.captionTextViewOld.setTextColor(-1);
            this.captionTextViewOld.setGravity(19);
            this.captionTextViewOld.setTextSize(gallery_menu_save, 16.0f);
            this.captionTextViewOld.setVisibility(gallery_menu_crop);
            this.containerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            TextView textView = new TextView(this.actvityContext);
            this.captionTextViewNew = textView;
            this.captionTextView = textView;
            this.captionTextViewNew.setMaxLines(gallery_menu_share);
            this.captionTextViewNew.setBackgroundColor(2130706432);
            this.captionTextViewNew.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            this.captionTextViewNew.setLinkTextColor(-1);
            this.captionTextViewNew.setTextColor(-1);
            this.captionTextViewNew.setGravity(19);
            this.captionTextViewNew.setTextSize(gallery_menu_save, 16.0f);
            this.captionTextViewNew.setVisibility(gallery_menu_crop);
            this.containerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.radialProgressViews[PAGE_SPACING] = new RadialProgressView(this.containerView.getContext(), this.containerView);
            this.radialProgressViews[PAGE_SPACING].setBackgroundState(PAGE_SPACING, false);
            this.radialProgressViews[gallery_menu_save] = new RadialProgressView(this.containerView.getContext(), this.containerView);
            this.radialProgressViews[gallery_menu_save].setBackgroundState(PAGE_SPACING, false);
            this.radialProgressViews[gallery_menu_showall] = new RadialProgressView(this.containerView.getContext(), this.containerView);
            this.radialProgressViews[gallery_menu_showall].setBackgroundState(PAGE_SPACING, false);
            this.shareButton = new ImageView(this.containerView.getContext());
            this.shareButton.setImageResource(C0691R.drawable.share);
            this.shareButton.setScaleType(ScaleType.CENTER);
            this.shareButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
            this.shareButton.setOnClickListener(new C13724());
            this.nameTextView = new TextView(this.containerView.getContext());
            this.nameTextView.setTextSize(gallery_menu_save, 14.0f);
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setMaxLines(gallery_menu_save);
            this.nameTextView.setEllipsize(TruncateAt.END);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setGravity(gallery_menu_send);
            this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, BitmapDescriptorFactory.HUE_YELLOW, 0.0f));
            this.dateTextView = new TextView(this.containerView.getContext());
            this.dateTextView.setTextSize(gallery_menu_save, 13.0f);
            this.dateTextView.setSingleLine(true);
            this.dateTextView.setMaxLines(gallery_menu_save);
            this.dateTextView.setEllipsize(TruncateAt.END);
            this.dateTextView.setTextColor(-1);
            this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.dateTextView.setGravity(gallery_menu_send);
            this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            if (VERSION.SDK_INT >= 16) {
                this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
                this.videoPlayerSeekbar.setColors(1728053247, -1, -1);
                this.videoPlayerSeekbar.setDelegate(new C19155());
                this.videoPlayerControlFrameLayout = new C13736(this.containerView.getContext());
                this.videoPlayerControlFrameLayout.setWillNotDraw(false);
                this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
                this.videoPlayButton = new ImageView(this.containerView.getContext());
                this.videoPlayButton.setScaleType(ScaleType.CENTER);
                this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
                this.videoPlayButton.setOnClickListener(new C13747());
                this.videoPlayerTime = new TextView(this.containerView.getContext());
                this.videoPlayerTime.setTextColor(-1);
                this.videoPlayerTime.setGravity(16);
                this.videoPlayerTime.setTextSize(gallery_menu_save, 13.0f);
                this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, GroundOverlayOptions.NO_DIMENSION, 53, 0.0f, 0.0f, 8.0f, 0.0f));
            }
            this.pickerView = new PickerBottomLayout(this.actvityContext);
            this.pickerView.setBackgroundColor(2130706432);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
            this.pickerView.cancelButton.setOnClickListener(new C13758());
            this.pickerView.doneButton.setOnClickListener(new C13769());
            this.editorDoneLayout = new PickerBottomLayout(this.actvityContext);
            this.editorDoneLayout.setBackgroundColor(2130706432);
            this.editorDoneLayout.updateSelectedCount(PAGE_SPACING, false);
            this.editorDoneLayout.setVisibility(gallery_menu_caption);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_save) {
                        PhotoViewer.this.photoCropView.cancelAnimationRunnable();
                    }
                    PhotoViewer.this.switchToEditMode(PhotoViewer.PAGE_SPACING);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_save) {
                        PhotoViewer.this.photoCropView.cancelAnimationRunnable();
                        if (PhotoViewer.this.imageMoveAnimation != null) {
                            return;
                        }
                    }
                    PhotoViewer.this.applyCurrentEditMode();
                    PhotoViewer.this.switchToEditMode(PhotoViewer.PAGE_SPACING);
                }
            });
            ImageView rotateButton = new ImageView(this.actvityContext);
            rotateButton.setScaleType(ScaleType.CENTER);
            rotateButton.setImageResource(C0691R.drawable.tool_rotate);
            rotateButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.editorDoneLayout.addView(rotateButton, LayoutHelper.createFrame(48, 48, 17));
            rotateButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PhotoViewer.this.centerImage.setOrientation(PhotoViewer.this.centerImage.getOrientation() - 90, false);
                    PhotoViewer.this.photoCropView.setOrientation(PhotoViewer.this.centerImage.getOrientation());
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
            this.gestureDetector.setOnDoubleTapListener(this);
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.leftImage.setParentView(this.containerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.rightImage.setParentView(this.containerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.checkImageView = new CheckBox(this.containerView.getContext(), C0691R.drawable.selectphoto_large);
            this.checkImageView.setDrawBackground(true);
            this.checkImageView.setSize(45);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
            this.checkImageView.setColor(-12793105);
            this.checkImageView.setVisibility(gallery_menu_caption);
            FrameLayoutDrawer frameLayoutDrawer = this.containerView;
            View view = this.checkImageView;
            float f = (rotation == gallery_menu_send || rotation == gallery_menu_save) ? 58.0f : 68.0f;
            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(45, 45.0f, 53, 0.0f, f, 10.0f, 0.0f));
            this.checkImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.placeProvider.setPhotoChecked(PhotoViewer.this.currentIndex);
                        PhotoViewer.this.checkImageView.setChecked(PhotoViewer.this.placeProvider.isPhotoChecked(PhotoViewer.this.currentIndex), true);
                        PhotoViewer.this.updateSelectedCount();
                    }
                }
            });
            this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView);
            this.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
                public void onCaptionEnter() {
                    PhotoViewer.this.closeCaptionEnter(true);
                }

                public void onTextChanged(CharSequence text) {
                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && text != null) {
                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages);
                    }
                }

                public void onWindowSizeChanged(int size) {
                    int i;
                    int min = Math.min(PhotoViewer.gallery_menu_send, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36;
                    if (PhotoViewer.this.mentionsAdapter.getItemCount() > PhotoViewer.gallery_menu_send) {
                        i = 18;
                    } else {
                        i = PhotoViewer.PAGE_SPACING;
                    }
                    if (size - (ActionBar.getCurrentActionBarHeight() * PhotoViewer.gallery_menu_showall) < AndroidUtilities.dp((float) (i + min))) {
                        PhotoViewer.this.allowMentions = false;
                        if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setVisibility(PhotoViewer.gallery_menu_crop);
                            return;
                        }
                        return;
                    }
                    PhotoViewer.this.allowMentions = true;
                    if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == PhotoViewer.gallery_menu_crop) {
                        PhotoViewer.this.mentionListView.setVisibility(PhotoViewer.PAGE_SPACING);
                    }
                }
            });
            this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, -400.0f));
            this.mentionListView = new RecyclerListView(this.actvityContext);
            this.mentionListView.setTag(Integer.valueOf(5));
            this.mentionLayoutManager = new AnonymousClass15(this.actvityContext);
            this.mentionLayoutManager.setOrientation(gallery_menu_save);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(2130706432);
            this.mentionListView.setVisibility(gallery_menu_caption);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(gallery_menu_showall);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView = this.mentionListView;
            Adapter mentionsAdapter = new MentionsAdapter(this.actvityContext, true, 0, new MentionsAdapterDelegate() {

                /* renamed from: org.telegram.ui.PhotoViewer.16.1 */
                class C19081 extends AnimatorListenerAdapterProxy {
                    C19081() {
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                    }
                }

                /* renamed from: org.telegram.ui.PhotoViewer.16.2 */
                class C19092 extends AnimatorListenerAdapterProxy {
                    C19092() {
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                            PhotoViewer.this.mentionListView.setVisibility(PhotoViewer.gallery_menu_caption);
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                    }
                }

                public void needChangePanelVisibility(boolean show) {
                    if (show) {
                        int i;
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        int min = Math.min(PhotoViewer.gallery_menu_send, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36;
                        if (PhotoViewer.this.mentionsAdapter.getItemCount() > PhotoViewer.gallery_menu_send) {
                            i = 18;
                        } else {
                            i = PhotoViewer.PAGE_SPACING;
                        }
                        int height = min + i;
                        layoutParams3.height = AndroidUtilities.dp((float) height);
                        layoutParams3.topMargin = -AndroidUtilities.dp((float) height);
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams3);
                        if (PhotoViewer.this.mentionListAnimation != null) {
                            PhotoViewer.this.mentionListAnimation.cancel();
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                        if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                            return;
                        }
                        PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(PhotoViewer.PAGE_SPACING, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListView.setVisibility(PhotoViewer.PAGE_SPACING);
                            PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            AnimatorSet access$6100 = PhotoViewer.this.mentionListAnimation;
                            Animator[] animatorArr = new Animator[PhotoViewer.gallery_menu_save];
                            animatorArr[PhotoViewer.PAGE_SPACING] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                            access$6100.playTogether(animatorArr);
                            PhotoViewer.this.mentionListAnimation.addListener(new C19081());
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                        PhotoViewer.this.mentionListView.setVisibility(PhotoViewer.gallery_menu_crop);
                        return;
                    }
                    if (PhotoViewer.this.mentionListAnimation != null) {
                        PhotoViewer.this.mentionListAnimation.cancel();
                        PhotoViewer.this.mentionListAnimation = null;
                    }
                    if (PhotoViewer.this.mentionListView.getVisibility() == PhotoViewer.gallery_menu_caption) {
                        return;
                    }
                    if (PhotoViewer.this.allowMentions) {
                        PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                        access$6100 = PhotoViewer.this.mentionListAnimation;
                        animatorArr = new Animator[PhotoViewer.gallery_menu_save];
                        float[] fArr = new float[PhotoViewer.gallery_menu_save];
                        fArr[PhotoViewer.PAGE_SPACING] = 0.0f;
                        animatorArr[PhotoViewer.PAGE_SPACING] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", fArr);
                        access$6100.playTogether(animatorArr);
                        PhotoViewer.this.mentionListAnimation.addListener(new C19092());
                        PhotoViewer.this.mentionListAnimation.setDuration(200);
                        PhotoViewer.this.mentionListAnimation.start();
                        return;
                    }
                    PhotoViewer.this.mentionListView.setVisibility(PhotoViewer.gallery_menu_caption);
                }

                public void onContextSearch(boolean searching) {
                }

                public void onContextClick(BotInlineResult result) {
                }
            });
            this.mentionsAdapter = mentionsAdapter;
            recyclerListView.setAdapter(mentionsAdapter);
            this.mentionsAdapter.setAllowNewMentions(false);
            this.mentionListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    User object = PhotoViewer.this.mentionsAdapter.getItem(position);
                    int start = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
                    int len = PhotoViewer.this.mentionsAdapter.getResultLength();
                    if (object instanceof User) {
                        User user = object;
                        if (user != null) {
                            PhotoViewer.this.captionEditText.replaceWithText(start, len, "@" + user.username + " ");
                        }
                    } else if (object instanceof String) {
                        PhotoViewer.this.captionEditText.replaceWithText(start, len, object + " ");
                    }
                }
            });
            this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                /* renamed from: org.telegram.ui.PhotoViewer.18.1 */
                class C13661 implements DialogInterface.OnClickListener {
                    C13661() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
                    }
                }

                public boolean onItemClick(View view, int position) {
                    if (!(PhotoViewer.this.mentionsAdapter.getItem(position) instanceof String)) {
                        return false;
                    }
                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setMessage(LocaleController.getString("ClearSearch", C0691R.string.ClearSearch));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", C0691R.string.ClearButton).toUpperCase(), new C13661());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    PhotoViewer.this.showAlertDialog(builder);
                    return true;
                }
            });
        }
    }

    private void updateVideoPlayerTime() {
        String newText;
        if (this.videoPlayer == null) {
            newText = "00:00 / 00:00";
        } else {
            long current = this.videoPlayer.getCurrentPosition() / 1000;
            long total = this.videoPlayer.getDuration() / 1000;
            if (total == -1 || current == -1) {
                newText = "00:00 / 00:00";
            } else {
                Object[] objArr = new Object[gallery_menu_crop];
                objArr[PAGE_SPACING] = Long.valueOf(current / 60);
                objArr[gallery_menu_save] = Long.valueOf(current % 60);
                objArr[gallery_menu_showall] = Long.valueOf(total / 60);
                objArr[gallery_menu_send] = Long.valueOf(total % 60);
                newText = String.format("%02d:%02d / %02d:%02d", objArr);
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), newText)) {
            this.videoPlayerTime.setText(newText);
        }
    }

    @SuppressLint({"NewApi"})
    private void preparePlayer(File file, boolean playWhenReady) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout.setVisibility(gallery_menu_crop);
                this.containerView.addView(this.aspectRatioFrameLayout, PAGE_SPACING, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setOpaque(false);
                this.videoTextureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            PhotoViewer.this.videoPlayer.setSurface(new Surface(PhotoViewer.this.videoTextureView.getSurfaceTexture()));
                        }
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            PhotoViewer.this.videoPlayer.blockingClearSurface();
                        }
                        return true;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                        if (!PhotoViewer.this.textureUploaded) {
                            PhotoViewer.this.textureUploaded = true;
                            PhotoViewer.this.containerView.invalidate();
                        }
                    }
                });
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            if (this.videoPlayer == null) {
                long duration;
                this.videoPlayer = new VideoPlayer(new ExtractorRendererBuilder(this.parentActivity, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36", Uri.fromFile(file)));
                this.videoPlayer.addListener(new Listener() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            if (playbackState == PhotoViewer.gallery_menu_crop && PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                PhotoViewer.this.aspectRatioFrameLayout.setVisibility(PhotoViewer.PAGE_SPACING);
                            }
                            if (!PhotoViewer.this.videoPlayer.getPlayerControl().isPlaying() || playbackState == 5) {
                                if (PhotoViewer.this.isPlaying) {
                                    PhotoViewer.this.isPlaying = false;
                                    PhotoViewer.this.videoPlayButton.setImageResource(C0691R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
                                    if (playbackState == 5 && !PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                        PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        PhotoViewer.this.videoPlayer.seekTo(0);
                                        PhotoViewer.this.videoPlayer.getPlayerControl().pause();
                                    }
                                }
                            } else if (!PhotoViewer.this.isPlaying) {
                                PhotoViewer.this.isPlaying = true;
                                PhotoViewer.this.videoPlayButton.setImageResource(C0691R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                            }
                            PhotoViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.m13e("tmessages", (Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                            PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? TouchHelperCallback.ALPHA_FULL : (((float) width) * pixelWidthHeightRatio) / ((float) height));
                        }
                    }
                });
                if (this.videoPlayer != null) {
                    duration = this.videoPlayer.getDuration();
                    if (duration == -1) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                TextPaint paint = this.videoPlayerTime.getPaint();
                Object[] objArr = new Object[gallery_menu_crop];
                objArr[PAGE_SPACING] = Long.valueOf(duration / 60);
                objArr[gallery_menu_save] = Long.valueOf(duration % 60);
                objArr[gallery_menu_showall] = Long.valueOf(duration / 60);
                objArr[gallery_menu_send] = Long.valueOf(duration % 60);
                int size = (int) Math.ceil((double) paint.measureText(String.format("%02d:%02d / %02d:%02d", objArr)));
                this.playerNeedsPrepare = true;
            }
            if (this.playerNeedsPrepare) {
                this.videoPlayer.prepare();
                this.playerNeedsPrepare = false;
            }
            if (this.videoPlayerControlFrameLayout != null) {
                this.videoPlayerControlFrameLayout.setVisibility(PAGE_SPACING);
                this.dateTextView.setVisibility(gallery_menu_caption);
                this.nameTextView.setVisibility(gallery_menu_caption);
                this.shareButton.setVisibility(gallery_menu_caption);
                this.menuItem.showSubItem(gallery_menu_share);
            }
            if (this.videoTextureView.getSurfaceTexture() != null) {
                this.videoPlayer.setSurface(new Surface(this.videoTextureView.getSurfaceTexture()));
            }
            this.videoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.release();
            this.videoPlayer = null;
        }
        if (this.aspectRatioFrameLayout != null) {
            this.containerView.removeView(this.aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(C0691R.drawable.inline_video_play);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (this.videoPlayerControlFrameLayout != null) {
            this.videoPlayerControlFrameLayout.setVisibility(gallery_menu_caption);
            this.dateTextView.setVisibility(PAGE_SPACING);
            this.nameTextView.setVisibility(PAGE_SPACING);
            this.shareButton.setVisibility(PAGE_SPACING);
            this.menuItem.hideSubItem(gallery_menu_share);
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object object) {
        CharSequence caption = null;
        if (object instanceof PhotoEntry) {
            caption = ((PhotoEntry) object).caption;
        } else if (object instanceof SearchImage) {
            caption = ((SearchImage) object).caption;
        }
        if (caption == null || caption.length() == 0) {
            this.captionEditText.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
        } else {
            this.captionEditText.setFieldText(caption);
        }
    }

    private void closeCaptionEnter(boolean apply) {
        Object object = this.imagesArrLocals.get(this.currentIndex);
        if (apply) {
            if (object instanceof PhotoEntry) {
                ((PhotoEntry) object).caption = this.captionEditText.getFieldCharSequence();
            } else if (object instanceof SearchImage) {
                ((SearchImage) object).caption = this.captionEditText.getFieldCharSequence();
            }
            if (!(this.captionEditText.getFieldCharSequence().length() == 0 || this.placeProvider.isPhotoChecked(this.currentIndex))) {
                this.placeProvider.setPhotoChecked(this.currentIndex);
                this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
                updateSelectedCount();
            }
        }
        this.cropItem.setVisibility(PAGE_SPACING);
        this.captionItem.setVisibility(PAGE_SPACING);
        if (VERSION.SDK_INT >= 16) {
            this.tuneItem.setVisibility(PAGE_SPACING);
        }
        if (this.sendPhotoType == 0) {
            this.checkImageView.setVisibility(PAGE_SPACING);
        }
        this.captionDoneItem.setVisibility(gallery_menu_caption);
        this.pickerView.setVisibility(PAGE_SPACING);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.captionEditText.getLayoutParams();
        layoutParams.bottomMargin = -AndroidUtilities.dp(400.0f);
        this.captionEditText.setLayoutParams(layoutParams);
        layoutParams = (FrameLayout.LayoutParams) this.mentionListView.getLayoutParams();
        layoutParams.bottomMargin = -AndroidUtilities.dp(400.0f);
        this.mentionListView.setLayoutParams(layoutParams);
        if (this.lastTitle != null) {
            this.actionBar.setTitle(this.lastTitle);
            this.lastTitle = null;
        }
        updateCaptionTextForCurrentPhoto(object);
        setCurrentCaption(this.captionEditText.getFieldCharSequence());
        if (this.captionEditText.isPopupShowing()) {
            this.captionEditText.hidePopup();
        } else {
            this.captionEditText.closeKeyboard();
        }
    }

    private void showAlertDialog(Builder builder) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            try {
                this.visibleDialog = builder.show();
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        PhotoViewer.this.visibleDialog = null;
                    }
                });
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
        }
    }

    private void applyCurrentEditMode() {
        Bitmap bitmap = null;
        if (this.currentEditMode == gallery_menu_save) {
            bitmap = this.photoCropView.getBitmap();
        } else if (this.currentEditMode == gallery_menu_showall) {
            bitmap = this.photoFilterView.getBitmap();
        }
        if (bitmap != null) {
            PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
            if (size != null) {
                PhotoEntry object = this.imagesArrLocals.get(this.currentIndex);
                if (object instanceof PhotoEntry) {
                    PhotoEntry entry = object;
                    entry.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), 70, false, 101, 101);
                    if (size != null) {
                        entry.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                    }
                } else if (object instanceof SearchImage) {
                    SearchImage entry2 = (SearchImage) object;
                    entry2.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), 70, false, 101, 101);
                    if (size != null) {
                        entry2.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                    }
                }
                if (this.sendPhotoType == 0 && this.placeProvider != null) {
                    this.placeProvider.updatePhotoAtIndex(this.currentIndex);
                    if (!this.placeProvider.isPhotoChecked(this.currentIndex)) {
                        this.placeProvider.setPhotoChecked(this.currentIndex);
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
                        updateSelectedCount();
                    }
                }
                if (this.currentEditMode == gallery_menu_save) {
                    float scaleX = this.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                    float scaleY = this.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                    if (scaleX <= scaleY) {
                        scaleX = scaleY;
                    }
                    this.scale = scaleX;
                    this.translationX = (this.photoCropView.getRectX() + (this.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / gallery_menu_showall));
                    this.translationY = (this.photoCropView.getRectY() + (this.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / gallery_menu_showall));
                    this.zoomAnimation = true;
                }
                this.centerImage.setParentView(null);
                this.centerImage.setOrientation(PAGE_SPACING, true);
                this.centerImage.setImageBitmap(bitmap);
                this.centerImage.setParentView(this.containerView);
            }
        }
    }

    private void switchToEditMode(int mode) {
        if (this.currentEditMode == mode || this.centerImage.getBitmap() == null || this.changeModeAnimation != null || this.imageMoveAnimation != null || this.radialProgressViews[PAGE_SPACING].backgroundState != -1) {
            return;
        }
        if (mode == 0) {
            if (this.currentEditMode != gallery_menu_showall || this.photoFilterView.getToolsView().getVisibility() == 0) {
                if (this.centerImage.getBitmap() != null) {
                    float scale;
                    float newScale;
                    int bitmapWidth = this.centerImage.getBitmapWidth();
                    int bitmapHeight = this.centerImage.getBitmapHeight();
                    float scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    float scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    float newScaleX = ((float) getContainerViewWidth(PAGE_SPACING)) / ((float) bitmapWidth);
                    float newScaleY = ((float) getContainerViewHeight(PAGE_SPACING)) / ((float) bitmapHeight);
                    if (scaleX > scaleY) {
                        scale = scaleY;
                    } else {
                        scale = scaleX;
                    }
                    if (newScaleX > newScaleY) {
                        newScale = newScaleY;
                    } else {
                        newScale = newScaleX;
                    }
                    this.animateToScale = newScale / scale;
                    this.animateToX = 0.0f;
                    if (this.currentEditMode == gallery_menu_save) {
                        this.animateToY = (float) AndroidUtilities.dp(24.0f);
                    } else if (this.currentEditMode == gallery_menu_showall) {
                        this.animateToY = (float) AndroidUtilities.dp(62.0f);
                    }
                    this.animationStartTime = System.currentTimeMillis();
                    this.zoomAnimation = true;
                }
                this.imageMoveAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                float[] fArr;
                if (this.currentEditMode == gallery_menu_save) {
                    animatorSet = this.imageMoveAnimation;
                    animatorArr = new Animator[gallery_menu_send];
                    fArr = new float[gallery_menu_save];
                    fArr[PAGE_SPACING] = (float) AndroidUtilities.dp(48.0f);
                    animatorArr[PAGE_SPACING] = ObjectAnimator.ofFloat(this.editorDoneLayout, "translationY", fArr);
                    animatorArr[gallery_menu_save] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                    fArr = new float[gallery_menu_save];
                    fArr[PAGE_SPACING] = 0.0f;
                    animatorArr[gallery_menu_showall] = ObjectAnimator.ofFloat(this.photoCropView, "alpha", fArr);
                    animatorSet.playTogether(animatorArr);
                } else if (this.currentEditMode == gallery_menu_showall) {
                    this.photoFilterView.shutdown();
                    animatorSet = this.imageMoveAnimation;
                    animatorArr = new Animator[gallery_menu_showall];
                    fArr = new float[gallery_menu_save];
                    fArr[PAGE_SPACING] = (float) AndroidUtilities.dp(126.0f);
                    animatorArr[PAGE_SPACING] = ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), "translationY", fArr);
                    animatorArr[gallery_menu_save] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                    animatorSet.playTogether(animatorArr);
                }
                this.imageMoveAnimation.setDuration(200);
                this.imageMoveAnimation.addListener(new AnonymousClass22(mode));
                this.imageMoveAnimation.start();
                return;
            }
            this.photoFilterView.switchToOrFromEditMode();
        } else if (mode == gallery_menu_save) {
            if (this.photoCropView == null) {
                this.photoCropView = new PhotoCropView(this.actvityContext);
                this.photoCropView.setVisibility(gallery_menu_caption);
                FrameLayoutDrawer frameLayoutDrawer = this.containerView;
                View view = this.photoCropView;
                frameLayoutDrawer.addView(r20, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.photoCropView.setDelegate(new PhotoCropViewDelegate() {
                    public void needMoveImageTo(float x, float y, float s, boolean animated) {
                        if (animated) {
                            PhotoViewer.this.animateTo(s, x, y, true);
                            return;
                        }
                        PhotoViewer.this.translationX = x;
                        PhotoViewer.this.translationY = y;
                        PhotoViewer.this.scale = s;
                        PhotoViewer.this.containerView.invalidate();
                    }

                    public Bitmap getBitmap() {
                        return PhotoViewer.this.centerImage.getBitmap();
                    }
                });
            }
            this.editorDoneLayout.doneButtonTextView.setText(LocaleController.getString("Crop", C0691R.string.Crop));
            this.changeModeAnimation = new AnimatorSet();
            arrayList = new ArrayList();
            r4 = new float[gallery_menu_showall];
            r4[PAGE_SPACING] = 0.0f;
            r4[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
            arrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", r4));
            r4 = new float[gallery_menu_showall];
            r4[PAGE_SPACING] = 0.0f;
            r4[gallery_menu_save] = (float) (-this.actionBar.getHeight());
            arrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", r4));
            if (this.needCaptionLayout) {
                r4 = new float[gallery_menu_showall];
                r4[PAGE_SPACING] = 0.0f;
                r4[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", r4));
            }
            if (this.sendPhotoType == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnonymousClass24(mode));
            this.changeModeAnimation.start();
        } else if (mode == gallery_menu_showall) {
            if (this.photoFilterView == null) {
                this.photoFilterView = new PhotoFilterView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
                this.containerView.addView(this.photoFilterView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
                this.photoFilterView.getDoneTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(PhotoViewer.PAGE_SPACING);
                    }
                });
                this.photoFilterView.getCancelTextView().setOnClickListener(new OnClickListener() {

                    /* renamed from: org.telegram.ui.PhotoViewer.26.1 */
                    class C13681 implements DialogInterface.OnClickListener {
                        C13681() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            PhotoViewer.this.switchToEditMode(PhotoViewer.PAGE_SPACING);
                        }
                    }

                    public void onClick(View v) {
                        if (!PhotoViewer.this.photoFilterView.hasChanges()) {
                            PhotoViewer.this.switchToEditMode(PhotoViewer.PAGE_SPACING);
                        } else if (PhotoViewer.this.parentActivity != null) {
                            Builder builder = new Builder(PhotoViewer.this.parentActivity);
                            builder.setMessage(LocaleController.getString("DiscardChanges", C0691R.string.DiscardChanges));
                            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C13681());
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                            PhotoViewer.this.showAlertDialog(builder);
                        }
                    }
                });
                this.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
            }
            this.changeModeAnimation = new AnimatorSet();
            arrayList = new ArrayList();
            r4 = new float[gallery_menu_showall];
            r4[PAGE_SPACING] = 0.0f;
            r4[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
            arrayList.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", r4));
            r4 = new float[gallery_menu_showall];
            r4[PAGE_SPACING] = 0.0f;
            r4[gallery_menu_save] = (float) (-this.actionBar.getHeight());
            arrayList.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", r4));
            if (this.needCaptionLayout) {
                r4 = new float[gallery_menu_showall];
                r4[PAGE_SPACING] = 0.0f;
                r4[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", r4));
            }
            if (this.sendPhotoType == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnonymousClass27(mode));
            this.changeModeAnimation.start();
        }
    }

    private void toggleCheckImageView(boolean show) {
        float f = TouchHelperCallback.ALPHA_FULL;
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> arrayList = new ArrayList();
        PickerBottomLayout pickerBottomLayout = this.pickerView;
        String str = "alpha";
        float[] fArr = new float[gallery_menu_save];
        fArr[PAGE_SPACING] = show ? 1.0f : PAGE_SPACING;
        arrayList.add(ObjectAnimator.ofFloat(pickerBottomLayout, str, fArr));
        if (this.needCaptionLayout) {
            float f2;
            TextView textView = this.captionTextView;
            str = "alpha";
            fArr = new float[gallery_menu_save];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = PAGE_SPACING;
            }
            fArr[PAGE_SPACING] = f2;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
        }
        if (this.sendPhotoType == 0) {
            CheckBox checkBox = this.checkImageView;
            String str2 = "alpha";
            float[] fArr2 = new float[gallery_menu_save];
            if (!show) {
                f = PAGE_SPACING;
            }
            fArr2[PAGE_SPACING] = f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, str2, fArr2));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleActionBar(boolean show, boolean animated) {
        float f = TouchHelperCallback.ALPHA_FULL;
        if (show) {
            this.actionBar.setVisibility(PAGE_SPACING);
            if (this.canShowBottom) {
                this.bottomLayout.setVisibility(PAGE_SPACING);
                if (this.captionTextView.getTag() != null) {
                    this.captionTextView.setVisibility(PAGE_SPACING);
                }
            }
        }
        this.isActionBarVisible = show;
        this.actionBar.setEnabled(show);
        this.bottomLayout.setEnabled(show);
        float f2;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[gallery_menu_save];
            if (show) {
                f2 = TouchHelperCallback.ALPHA_FULL;
            } else {
                f2 = PAGE_SPACING;
            }
            fArr[PAGE_SPACING] = f2;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            str = "alpha";
            fArr = new float[gallery_menu_save];
            if (show) {
                f2 = TouchHelperCallback.ALPHA_FULL;
            } else {
                f2 = PAGE_SPACING;
            }
            fArr[PAGE_SPACING] = f2;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                String str2 = "alpha";
                float[] fArr2 = new float[gallery_menu_save];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[PAGE_SPACING] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.currentActionBarAnimation != null && PhotoViewer.this.currentActionBarAnimation.equals(animation)) {
                            PhotoViewer.this.actionBar.setVisibility(PhotoViewer.gallery_menu_caption);
                            if (PhotoViewer.this.canShowBottom) {
                                PhotoViewer.this.bottomLayout.setVisibility(PhotoViewer.gallery_menu_caption);
                                if (PhotoViewer.this.captionTextView.getTag() != null) {
                                    PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.gallery_menu_crop);
                                }
                            }
                            PhotoViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        this.actionBar.setAlpha(show ? TouchHelperCallback.ALPHA_FULL : 0.0f);
        frameLayout = this.bottomLayout;
        if (show) {
            f2 = TouchHelperCallback.ALPHA_FULL;
        } else {
            f2 = 0.0f;
        }
        frameLayout.setAlpha(f2);
        if (this.captionTextView.getTag() != null) {
            textView = this.captionTextView;
            if (!show) {
                f = 0.0f;
            }
            textView.setAlpha(f);
        }
        if (!show) {
            this.actionBar.setVisibility(gallery_menu_caption);
            if (this.canShowBottom) {
                this.bottomLayout.setVisibility(gallery_menu_caption);
                if (this.captionTextView.getTag() != null) {
                    this.captionTextView.setVisibility(gallery_menu_crop);
                }
            }
        }
    }

    private String getFileName(int index) {
        if (index < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty() && this.imagesArr.isEmpty()) {
            if (this.imagesArrLocals.isEmpty() || index >= this.imagesArrLocals.size()) {
                return null;
            }
            SearchImage object = this.imagesArrLocals.get(index);
            if (!(object instanceof SearchImage)) {
                return null;
            }
            SearchImage searchImage = object;
            if (searchImage.document != null) {
                return FileLoader.getAttachFileName(searchImage.document);
            }
            if (!(searchImage.type == gallery_menu_save || searchImage.localUrl == null || searchImage.localUrl.length() <= 0)) {
                File file = new File(searchImage.localUrl);
                if (file.exists()) {
                    return file.getName();
                }
                searchImage.localUrl = TtmlNode.ANONYMOUS_REGION_ID;
            }
            return Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg");
        } else if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
                return null;
            }
            return FileLoader.getMessageFileName(((MessageObject) this.imagesArr.get(index)).messageOwner);
        } else if (index >= this.imagesArrLocations.size()) {
            return null;
        } else {
            FileLocation location = (FileLocation) this.imagesArrLocations.get(index);
            return location.volume_id + "_" + location.local_id + ".jpg";
        }
    }

    private FileLocation getFileLocation(int index, int[] size) {
        if (index < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
                return null;
            }
            MessageObject message = (MessageObject) this.imagesArr.get(index);
            PhotoSize sizeFull;
            if (message.messageOwner instanceof TL_messageService) {
                if (message.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    return message.messageOwner.action.newUserPhoto.photo_big;
                }
                sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    size[PAGE_SPACING] = sizeFull.size;
                    if (size[PAGE_SPACING] == 0) {
                        size[PAGE_SPACING] = -1;
                    }
                    return sizeFull.location;
                }
                size[PAGE_SPACING] = -1;
                return null;
            } else if (((message.messageOwner.media instanceof TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
                sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    size[PAGE_SPACING] = sizeFull.size;
                    if (size[PAGE_SPACING] == 0) {
                        size[PAGE_SPACING] = -1;
                    }
                    return sizeFull.location;
                }
                size[PAGE_SPACING] = -1;
                return null;
            } else if (message.getDocument() == null || message.getDocument().thumb == null) {
                return null;
            } else {
                size[PAGE_SPACING] = message.getDocument().thumb.size;
                if (size[PAGE_SPACING] == 0) {
                    size[PAGE_SPACING] = -1;
                }
                return message.getDocument().thumb.location;
            }
        } else if (index >= this.imagesArrLocations.size()) {
            return null;
        } else {
            size[PAGE_SPACING] = ((Integer) this.imagesArrLocationsSizes.get(index)).intValue();
            return (FileLocation) this.imagesArrLocations.get(index);
        }
    }

    private void updateSelectedCount() {
        if (this.placeProvider != null) {
            this.pickerView.updateSelectedCount(this.placeProvider.getSelectedCount(), false);
        }
    }

    private void onPhotoShow(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> messages, ArrayList<Object> photos, int index, PlaceProviderObject object) {
        int a;
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.currentIndex = -1;
        this.currentFileNames[PAGE_SPACING] = null;
        this.currentFileNames[gallery_menu_save] = null;
        this.currentFileNames[gallery_menu_showall] = null;
        this.avatarsDialogId = PAGE_SPACING;
        this.totalImagesCount = PAGE_SPACING;
        this.totalImagesCountMerge = PAGE_SPACING;
        this.currentEditMode = PAGE_SPACING;
        this.isFirstLoading = true;
        this.needSearchImageInArr = false;
        this.loadingMoreImages = false;
        this.endReached[PAGE_SPACING] = false;
        this.endReached[gallery_menu_save] = this.mergeDialogId == 0;
        this.opennedFromMedia = false;
        this.needCaptionLayout = false;
        this.canShowBottom = true;
        this.imagesArr.clear();
        this.imagesArrLocations.clear();
        this.imagesArrLocationsSizes.clear();
        this.avatarsArr.clear();
        this.imagesArrLocals.clear();
        for (a = PAGE_SPACING; a < gallery_menu_showall; a += gallery_menu_save) {
            this.imagesByIds[a].clear();
            this.imagesByIdsTemp[a].clear();
        }
        this.imagesArrTemp.clear();
        this.currentUserAvatarLocation = null;
        this.containerView.setPadding(PAGE_SPACING, PAGE_SPACING, PAGE_SPACING, PAGE_SPACING);
        this.currentThumb = object != null ? object.thumb : null;
        this.menuItem.setVisibility(PAGE_SPACING);
        this.bottomLayout.setVisibility(PAGE_SPACING);
        this.shareButton.setVisibility(gallery_menu_caption);
        this.menuItem.hideSubItem(gallery_menu_showall);
        this.menuItem.hideSubItem(gallery_menu_share);
        this.menuItem.hideSubItem(gallery_menu_openin);
        this.actionBar.setTranslationY(0.0f);
        this.pickerView.setTranslationY(0.0f);
        this.checkImageView.setAlpha(TouchHelperCallback.ALPHA_FULL);
        this.pickerView.setAlpha(TouchHelperCallback.ALPHA_FULL);
        this.checkImageView.setVisibility(gallery_menu_caption);
        this.pickerView.setVisibility(gallery_menu_caption);
        this.cropItem.setVisibility(gallery_menu_caption);
        this.tuneItem.setVisibility(gallery_menu_caption);
        this.captionItem.setVisibility(gallery_menu_caption);
        this.captionDoneItem.setVisibility(gallery_menu_caption);
        this.captionEditText.setVisibility(gallery_menu_caption);
        this.mentionListView.setVisibility(gallery_menu_caption);
        this.editorDoneLayout.setVisibility(gallery_menu_caption);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(gallery_menu_crop);
        if (this.photoCropView != null) {
            this.photoCropView.setVisibility(gallery_menu_caption);
        }
        if (this.photoFilterView != null) {
            this.photoFilterView.setVisibility(gallery_menu_caption);
        }
        for (a = PAGE_SPACING; a < gallery_menu_send; a += gallery_menu_save) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        if (messageObject != null && messages == null) {
            this.imagesArr.add(messageObject);
            if (this.currentAnimation != null) {
                this.needSearchImageInArr = false;
            } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty))) {
                this.needSearchImageInArr = true;
                this.imagesByIds[PAGE_SPACING].put(Integer.valueOf(messageObject.getId()), messageObject);
                this.menuItem.showSubItem(gallery_menu_showall);
            }
            setImageIndex(PAGE_SPACING, true);
        } else if (fileLocation != null) {
            this.avatarsDialogId = object.dialogId;
            this.imagesArrLocations.add(fileLocation);
            this.imagesArrLocationsSizes.add(Integer.valueOf(object.size));
            this.avatarsArr.add(new TL_photoEmpty());
            ImageView imageView = this.shareButton;
            r2 = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? PAGE_SPACING : gallery_menu_caption;
            imageView.setVisibility(r2);
            this.menuItem.hideSubItem(gallery_menu_showall);
            if (this.shareButton.getVisibility() == 0) {
                this.menuItem.hideSubItem(gallery_menu_share);
            } else {
                this.menuItem.showSubItem(gallery_menu_share);
            }
            setImageIndex(PAGE_SPACING, true);
            this.currentUserAvatarLocation = fileLocation;
        } else if (messages != null) {
            this.menuItem.showSubItem(gallery_menu_showall);
            this.opennedFromMedia = true;
            this.imagesArr.addAll(messages);
            if (!this.opennedFromMedia) {
                Collections.reverse(this.imagesArr);
                index = (this.imagesArr.size() - index) - 1;
            }
            for (a = PAGE_SPACING; a < this.imagesArr.size(); a += gallery_menu_save) {
                MessageObject message = (MessageObject) this.imagesArr.get(a);
                this.imagesByIds[message.getDialogId() == this.currentDialogId ? PAGE_SPACING : gallery_menu_save].put(Integer.valueOf(message.getId()), message);
            }
            setImageIndex(index, true);
        } else if (photos != null) {
            if (this.sendPhotoType == 0) {
                this.checkImageView.setVisibility(PAGE_SPACING);
            }
            this.menuItem.setVisibility(gallery_menu_caption);
            this.imagesArrLocals.addAll(photos);
            setImageIndex(index, true);
            this.pickerView.setVisibility(PAGE_SPACING);
            this.bottomLayout.setVisibility(gallery_menu_caption);
            this.canShowBottom = false;
            Object obj = this.imagesArrLocals.get(index);
            ActionBarMenuItem actionBarMenuItem = this.cropItem;
            r2 = ((obj instanceof PhotoEntry) || ((obj instanceof SearchImage) && ((SearchImage) obj).type == 0)) ? PAGE_SPACING : gallery_menu_caption;
            actionBarMenuItem.setVisibility(r2);
            if (this.parentChatActivity != null && (this.parentChatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 46)) {
                this.mentionsAdapter.setChatInfo(this.parentChatActivity.info);
                this.mentionsAdapter.setNeedUsernames(this.parentChatActivity.currentChat != null);
                this.mentionsAdapter.setNeedBotContext(false);
                this.captionItem.setVisibility(this.cropItem.getVisibility());
                this.captionEditText.setVisibility(this.cropItem.getVisibility());
                this.needCaptionLayout = this.captionItem.getVisibility() == 0;
                if (this.needCaptionLayout) {
                    this.captionEditText.onCreate();
                }
            }
            if (VERSION.SDK_INT >= 16) {
                this.tuneItem.setVisibility(this.cropItem.getVisibility());
            }
            updateSelectedCount();
        }
        if (this.currentAnimation == null) {
            if (this.currentDialogId != 0 && this.totalImagesCount == 0) {
                SharedMediaQuery.getMediaCount(this.currentDialogId, PAGE_SPACING, this.classGuid, true);
                if (this.mergeDialogId != 0) {
                    SharedMediaQuery.getMediaCount(this.mergeDialogId, PAGE_SPACING, this.classGuid, true);
                }
            } else if (this.avatarsDialogId != 0) {
                MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, PAGE_SPACING, 80, 0, true, this.classGuid);
            }
        }
        if (this.currentMessageObject != null && this.currentMessageObject.isVideo()) {
            onActionClick(false);
        }
    }

    private void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + gallery_menu_save);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    private void setImageIndex(int index, boolean init) {
        if (this.currentIndex != index && this.placeProvider != null) {
            if (!init) {
                this.currentThumb = null;
            }
            this.currentFileNames[PAGE_SPACING] = getFileName(index);
            this.currentFileNames[gallery_menu_save] = getFileName(index + gallery_menu_save);
            this.currentFileNames[gallery_menu_showall] = getFileName(index - 1);
            this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            int prevIndex = this.currentIndex;
            this.currentIndex = index;
            boolean isVideo = false;
            boolean sameImage = false;
            ActionBar actionBar;
            Object[] objArr;
            ImageView imageView;
            int i;
            if (this.imagesArr.isEmpty()) {
                if (!this.imagesArrLocations.isEmpty()) {
                    this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    this.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    if (this.avatarsDialogId != UserConfig.getClientUserId() || this.avatarsArr.isEmpty()) {
                        this.menuItem.hideSubItem(gallery_menu_delete);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_delete);
                    }
                    FileLocation old = this.currentFileLocation;
                    if (index < 0 || index >= this.imagesArrLocations.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    this.currentFileLocation = (FileLocation) this.imagesArrLocations.get(index);
                    if (old != null && this.currentFileLocation != null && old.local_id == this.currentFileLocation.local_id && old.volume_id == this.currentFileLocation.volume_id) {
                        sameImage = true;
                    }
                    actionBar = this.actionBar;
                    objArr = new Object[gallery_menu_showall];
                    objArr[PAGE_SPACING] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                    objArr[gallery_menu_save] = Integer.valueOf(this.imagesArrLocations.size());
                    actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                    this.menuItem.showSubItem(gallery_menu_save);
                    imageView = this.shareButton;
                    i = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? PAGE_SPACING : gallery_menu_caption;
                    imageView.setVisibility(i);
                    if (this.shareButton.getVisibility() == 0) {
                        this.menuItem.hideSubItem(gallery_menu_share);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_share);
                    }
                } else if (!this.imagesArrLocals.isEmpty()) {
                    SearchImage object = this.imagesArrLocals.get(index);
                    if (index < 0 || index >= this.imagesArrLocals.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    boolean fromCamera = false;
                    CharSequence caption = null;
                    if (object instanceof PhotoEntry) {
                        this.currentPathObject = ((PhotoEntry) object).path;
                        fromCamera = ((PhotoEntry) object).bucketId == 0 && ((PhotoEntry) object).dateTaken == 0 && this.imagesArrLocals.size() == gallery_menu_save;
                        caption = ((PhotoEntry) object).caption;
                    } else if (object instanceof SearchImage) {
                        SearchImage searchImage = object;
                        if (searchImage.document != null) {
                            this.currentPathObject = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                        } else {
                            this.currentPathObject = searchImage.imageUrl;
                        }
                        caption = searchImage.caption;
                    }
                    if (fromCamera) {
                        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0691R.string.AttachPhoto));
                    } else {
                        actionBar = this.actionBar;
                        objArr = new Object[gallery_menu_showall];
                        objArr[PAGE_SPACING] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                        objArr[gallery_menu_save] = Integer.valueOf(this.imagesArrLocals.size());
                        actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                    }
                    if (this.sendPhotoType == 0) {
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), false);
                    }
                    setCurrentCaption(caption);
                    updateCaptionTextForCurrentPhoto(object);
                }
            } else if (this.currentIndex < 0 || this.currentIndex >= this.imagesArr.size()) {
                closePhoto(false, false);
                return;
            } else {
                MessageObject newMessageObject = (MessageObject) this.imagesArr.get(this.currentIndex);
                sameImage = this.currentMessageObject != null && this.currentMessageObject.getId() == newMessageObject.getId();
                this.currentMessageObject = newMessageObject;
                isVideo = this.currentMessageObject.isVideo();
                if (this.currentMessageObject.canDeleteMessage(null)) {
                    this.menuItem.showSubItem(gallery_menu_delete);
                } else {
                    this.menuItem.hideSubItem(gallery_menu_delete);
                }
                if (!isVideo || VERSION.SDK_INT < 16) {
                    this.menuItem.hideSubItem(gallery_menu_openin);
                } else {
                    this.menuItem.showSubItem(gallery_menu_openin);
                }
                if (this.currentMessageObject.isFromUser()) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                    if (user != null) {
                        this.nameTextView.setText(UserObject.getUserName(user));
                    } else {
                        this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                } else {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
                    if (chat != null) {
                        this.nameTextView.setText(chat.title);
                    } else {
                        this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                }
                long date = ((long) this.currentMessageObject.messageOwner.date) * 1000;
                Object[] objArr2 = new Object[gallery_menu_showall];
                objArr2[PAGE_SPACING] = LocaleController.getInstance().formatterYear.format(new Date(date));
                objArr2[gallery_menu_save] = LocaleController.getInstance().formatterDay.format(new Date(date));
                String dateString = LocaleController.formatString("formatDateAtTime", C0691R.string.formatDateAtTime, objArr2);
                if (this.currentFileNames[PAGE_SPACING] == null || !isVideo) {
                    this.dateTextView.setText(dateString);
                } else {
                    TextView textView = this.dateTextView;
                    objArr2 = new Object[gallery_menu_showall];
                    objArr2[PAGE_SPACING] = dateString;
                    objArr2[gallery_menu_save] = AndroidUtilities.formatFileSize((long) this.currentMessageObject.getDocument().size);
                    textView.setText(String.format("%s (%s)", objArr2));
                }
                setCurrentCaption(this.currentMessageObject.caption);
                if (this.currentAnimation != null) {
                    this.menuItem.hideSubItem(gallery_menu_save);
                    this.menuItem.hideSubItem(gallery_menu_share);
                    if (!this.currentMessageObject.canDeleteMessage(null)) {
                        this.menuItem.setVisibility(gallery_menu_caption);
                    }
                    this.shareButton.setVisibility(PAGE_SPACING);
                    this.actionBar.setTitle(LocaleController.getString("AttachGif", C0691R.string.AttachGif));
                } else {
                    if (this.totalImagesCount + this.totalImagesCountMerge == 0 || this.needSearchImageInArr) {
                        if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                            if (this.currentMessageObject.isVideo()) {
                                this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0691R.string.AttachVideo));
                            } else {
                                this.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0691R.string.AttachPhoto));
                            }
                        }
                    } else if (this.opennedFromMedia) {
                        if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.currentIndex > this.imagesArr.size() - 5) {
                            loadFromMaxId = this.imagesArr.isEmpty() ? PAGE_SPACING : ((MessageObject) this.imagesArr.get(this.imagesArr.size() - 1)).getId();
                            loadIndex = PAGE_SPACING;
                            if (this.endReached[PAGE_SPACING] && this.mergeDialogId != 0) {
                                loadIndex = gallery_menu_save;
                                if (!(this.imagesArr.isEmpty() || ((MessageObject) this.imagesArr.get(this.imagesArr.size() - 1)).getDialogId() == this.mergeDialogId)) {
                                    loadFromMaxId = PAGE_SPACING;
                                }
                            }
                            SharedMediaQuery.loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, PAGE_SPACING, 80, loadFromMaxId, PAGE_SPACING, true, this.classGuid);
                            this.loadingMoreImages = true;
                        }
                        actionBar = this.actionBar;
                        objArr = new Object[gallery_menu_showall];
                        objArr[PAGE_SPACING] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                        objArr[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                        actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                    } else {
                        if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.currentIndex < 5) {
                            loadFromMaxId = this.imagesArr.isEmpty() ? PAGE_SPACING : ((MessageObject) this.imagesArr.get(PAGE_SPACING)).getId();
                            loadIndex = PAGE_SPACING;
                            if (this.endReached[PAGE_SPACING] && this.mergeDialogId != 0) {
                                loadIndex = gallery_menu_save;
                                if (!(this.imagesArr.isEmpty() || ((MessageObject) this.imagesArr.get(PAGE_SPACING)).getDialogId() == this.mergeDialogId)) {
                                    loadFromMaxId = PAGE_SPACING;
                                }
                            }
                            SharedMediaQuery.loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, PAGE_SPACING, 80, loadFromMaxId, PAGE_SPACING, true, this.classGuid);
                            this.loadingMoreImages = true;
                        }
                        actionBar = this.actionBar;
                        objArr = new Object[gallery_menu_showall];
                        objArr[PAGE_SPACING] = Integer.valueOf((((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex) + gallery_menu_save);
                        objArr[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                        actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                    }
                    if (this.currentMessageObject.messageOwner.ttl != 0) {
                        this.menuItem.hideSubItem(gallery_menu_save);
                        this.shareButton.setVisibility(gallery_menu_caption);
                        this.menuItem.hideSubItem(gallery_menu_share);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_save);
                        imageView = this.shareButton;
                        i = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? PAGE_SPACING : gallery_menu_caption;
                        imageView.setVisibility(i);
                        if (this.shareButton.getVisibility() == 0) {
                            this.menuItem.hideSubItem(gallery_menu_share);
                        } else {
                            this.menuItem.showSubItem(gallery_menu_share);
                        }
                    }
                }
            }
            if (this.currentPlaceObject != null) {
                if (this.animationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                } else {
                    this.showAfterAnimation = this.currentPlaceObject;
                }
            }
            this.currentPlaceObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            if (this.currentPlaceObject != null) {
                if (this.animationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, true);
                } else {
                    this.hideAfterAnimation = this.currentPlaceObject;
                }
            }
            if (!sameImage) {
                this.draggingDown = false;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
                this.scale = TouchHelperCallback.ALPHA_FULL;
                this.animateToX = 0.0f;
                this.animateToY = 0.0f;
                this.animateToScale = TouchHelperCallback.ALPHA_FULL;
                this.animationStartTime = 0;
                this.imageMoveAnimation = null;
                this.changeModeAnimation = null;
                if (this.aspectRatioFrameLayout != null) {
                    this.aspectRatioFrameLayout.setVisibility(gallery_menu_crop);
                }
                releasePlayer();
                this.pinchStartDistance = 0.0f;
                this.pinchStartScale = TouchHelperCallback.ALPHA_FULL;
                this.pinchCenterX = 0.0f;
                this.pinchCenterY = 0.0f;
                this.pinchStartX = 0.0f;
                this.pinchStartY = 0.0f;
                this.moveStartX = 0.0f;
                this.moveStartY = 0.0f;
                this.zooming = false;
                this.moving = false;
                this.doubleTap = false;
                this.invalidCoords = false;
                this.canDragDown = true;
                this.changingPage = false;
                this.switchImageAfterAnimation = PAGE_SPACING;
                boolean z = (this.imagesArrLocals.isEmpty() && (this.currentFileNames[PAGE_SPACING] == null || isVideo || this.radialProgressViews[PAGE_SPACING].backgroundState == 0)) ? false : true;
                this.canZoom = z;
                updateMinMax(this.scale);
            }
            if (prevIndex == -1) {
                setImages();
                for (int a = PAGE_SPACING; a < gallery_menu_send; a += gallery_menu_save) {
                    checkProgress(a, false);
                }
                return;
            }
            checkProgress(PAGE_SPACING, false);
            ImageReceiver temp;
            RadialProgressView tempProgress;
            if (prevIndex > this.currentIndex) {
                temp = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = temp;
                tempProgress = this.radialProgressViews[PAGE_SPACING];
                this.radialProgressViews[PAGE_SPACING] = this.radialProgressViews[gallery_menu_showall];
                this.radialProgressViews[gallery_menu_showall] = tempProgress;
                setIndexToImage(this.leftImage, this.currentIndex - 1);
                checkProgress(gallery_menu_save, false);
                checkProgress(gallery_menu_showall, false);
            } else if (prevIndex < this.currentIndex) {
                temp = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = temp;
                tempProgress = this.radialProgressViews[PAGE_SPACING];
                this.radialProgressViews[PAGE_SPACING] = this.radialProgressViews[gallery_menu_save];
                this.radialProgressViews[gallery_menu_save] = tempProgress;
                setIndexToImage(this.rightImage, this.currentIndex + gallery_menu_save);
                checkProgress(gallery_menu_save, false);
                checkProgress(gallery_menu_showall, false);
            }
        }
    }

    private void setCurrentCaption(CharSequence caption) {
        if (caption == null || caption.length() <= 0) {
            this.captionItem.setIcon(C0691R.drawable.photo_text);
            this.captionTextView.setTag(null);
            this.captionTextView.setVisibility(gallery_menu_crop);
            return;
        }
        this.captionTextView = this.captionTextViewOld;
        this.captionTextViewOld = this.captionTextViewNew;
        this.captionTextViewNew = this.captionTextView;
        this.captionItem.setIcon(C0691R.drawable.photo_text2);
        CharSequence str = Emoji.replaceEmoji(new SpannableStringBuilder(caption.toString()), MessageObject.getTextPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        this.captionTextView.setTag(str);
        this.captionTextView.setText(str);
        TextView textView = this.captionTextView;
        float f = (this.bottomLayout.getVisibility() == 0 || this.pickerView.getVisibility() == 0) ? TouchHelperCallback.ALPHA_FULL : 0.0f;
        textView.setAlpha(f);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = PhotoViewer.gallery_menu_crop;
                PhotoViewer.this.captionTextViewOld.setTag(null);
                PhotoViewer.this.captionTextViewOld.setVisibility(PhotoViewer.gallery_menu_crop);
                TextView access$1800 = PhotoViewer.this.captionTextViewNew;
                if (PhotoViewer.this.bottomLayout.getVisibility() == 0 || PhotoViewer.this.pickerView.getVisibility() == 0) {
                    i = PhotoViewer.PAGE_SPACING;
                }
                access$1800.setVisibility(i);
            }
        });
    }

    private void checkProgress(int a, boolean animated) {
        boolean z = false;
        if (this.currentFileNames[a] != null) {
            int index = this.currentIndex;
            if (a == gallery_menu_save) {
                index += gallery_menu_save;
            } else if (a == gallery_menu_showall) {
                index--;
            }
            File f = null;
            boolean isVideo = false;
            if (this.currentMessageObject != null) {
                MessageObject messageObject = (MessageObject) this.imagesArr.get(index);
                if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                    f = new File(messageObject.messageOwner.attachPath);
                    if (!f.exists()) {
                        f = null;
                    }
                }
                if (f == null) {
                    f = FileLoader.getPathToMessage(messageObject.messageOwner);
                }
                isVideo = messageObject.isVideo();
            } else if (this.currentFileLocation != null) {
                boolean z2;
                FileLocation location = (FileLocation) this.imagesArrLocations.get(index);
                if (this.avatarsDialogId != 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                f = FileLoader.getPathToAttach(location, z2);
            } else if (this.currentPathObject != null) {
                f = new File(FileLoader.getInstance().getDirectory(gallery_menu_send), this.currentFileNames[a]);
                if (!f.exists()) {
                    f = new File(FileLoader.getInstance().getDirectory(gallery_menu_crop), this.currentFileNames[a]);
                }
            }
            if (f == null || !f.exists()) {
                if (!isVideo) {
                    this.radialProgressViews[a].setBackgroundState(PAGE_SPACING, animated);
                } else if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[a])) {
                    this.radialProgressViews[a].setBackgroundState(gallery_menu_save, false);
                } else {
                    this.radialProgressViews[a].setBackgroundState(gallery_menu_showall, false);
                }
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[a].setProgress(progress.floatValue(), false);
            } else if (isVideo) {
                this.radialProgressViews[a].setBackgroundState(gallery_menu_send, animated);
            } else {
                this.radialProgressViews[a].setBackgroundState(-1, animated);
            }
            if (a == 0) {
                if (!(this.imagesArrLocals.isEmpty() && (this.currentFileNames[PAGE_SPACING] == null || isVideo || this.radialProgressViews[PAGE_SPACING].backgroundState == 0))) {
                    z = true;
                }
                this.canZoom = z;
                return;
            }
            return;
        }
        this.radialProgressViews[a].setBackgroundState(-1, animated);
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        imageReceiver.setOrientation(PAGE_SPACING, false);
        Bitmap placeHolder;
        if (this.imagesArrLocals.isEmpty()) {
            int[] size = new int[gallery_menu_save];
            FileLocation fileLocation = getFileLocation(index, size);
            if (fileLocation != null) {
                MessageObject messageObject = null;
                if (!this.imagesArr.isEmpty()) {
                    messageObject = (MessageObject) this.imagesArr.get(index);
                }
                imageReceiver.setParentMessageObject(messageObject);
                if (messageObject != null) {
                    imageReceiver.setShouldGenerateQualityThumb(true);
                }
                if (messageObject != null && messageObject.isVideo()) {
                    imageReceiver.setNeedsQualityThumb(true);
                    if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
                        imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(C0691R.drawable.photoview_placeholder));
                        return;
                    }
                    placeHolder = null;
                    if (this.currentThumb != null && imageReceiver == this.centerImage) {
                        placeHolder = this.currentThumb;
                    }
                    imageReceiver.setImage(null, null, null, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null, FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100).location, "b", PAGE_SPACING, null, true);
                    return;
                } else if (messageObject == null || this.currentAnimation == null) {
                    imageReceiver.setNeedsQualityThumb(false);
                    placeHolder = null;
                    if (this.currentThumb != null && imageReceiver == this.centerImage) {
                        placeHolder = this.currentThumb;
                    }
                    if (size[PAGE_SPACING] == 0) {
                        size[PAGE_SPACING] = -1;
                    }
                    PhotoSize thumbLocation = messageObject != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100) : null;
                    imageReceiver.setImage(fileLocation, null, null, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null, thumbLocation != null ? thumbLocation.location : null, "b", size[PAGE_SPACING], null, this.avatarsDialogId != 0);
                    return;
                } else {
                    imageReceiver.setImageBitmap(this.currentAnimation);
                    this.currentAnimation.setSecondParentView(this.containerView);
                    return;
                }
            }
            imageReceiver.setNeedsQualityThumb(false);
            imageReceiver.setParentMessageObject(null);
            if (size[PAGE_SPACING] == 0) {
                imageReceiver.setImageBitmap((Bitmap) null);
                return;
            } else {
                imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(C0691R.drawable.photoview_placeholder));
                return;
            }
        }
        imageReceiver.setParentMessageObject(null);
        if (index < 0 || index >= this.imagesArrLocals.size()) {
            imageReceiver.setImageBitmap((Bitmap) null);
            return;
        }
        PhotoEntry object = this.imagesArrLocals.get(index);
        int size2 = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        placeHolder = null;
        if (this.currentThumb != null && imageReceiver == this.centerImage) {
            placeHolder = this.currentThumb;
        }
        if (placeHolder == null) {
            placeHolder = this.placeProvider.getThumbForPhoto(null, null, index);
        }
        String path = null;
        Document document = null;
        int imageSize = PAGE_SPACING;
        String filter = null;
        if (object instanceof PhotoEntry) {
            PhotoEntry photoEntry = object;
            if (photoEntry.imagePath != null) {
                path = photoEntry.imagePath;
            } else {
                imageReceiver.setOrientation(photoEntry.orientation, false);
                path = photoEntry.path;
            }
            Object[] objArr = new Object[gallery_menu_showall];
            objArr[PAGE_SPACING] = Integer.valueOf(size2);
            objArr[gallery_menu_save] = Integer.valueOf(size2);
            filter = String.format(Locale.US, "%d_%d", objArr);
        } else if (object instanceof SearchImage) {
            SearchImage photoEntry2 = (SearchImage) object;
            if (photoEntry2.imagePath != null) {
                path = photoEntry2.imagePath;
            } else if (photoEntry2.document != null) {
                document = photoEntry2.document;
                imageSize = photoEntry2.document.size;
            } else {
                path = photoEntry2.imageUrl;
                imageSize = photoEntry2.size;
            }
            filter = "d";
        }
        if (document != null) {
            Drawable bitmapDrawable;
            FileLocation fileLocation2;
            path = "d";
            if (placeHolder != null) {
                bitmapDrawable = new BitmapDrawable(null, placeHolder);
            } else {
                bitmapDrawable = null;
            }
            if (placeHolder == null) {
                fileLocation2 = document.thumb.location;
            } else {
                fileLocation2 = null;
            }
            Object[] objArr2 = new Object[gallery_menu_showall];
            objArr2[PAGE_SPACING] = Integer.valueOf(size2);
            objArr2[gallery_menu_save] = Integer.valueOf(size2);
            imageReceiver.setImage(document, null, path, bitmapDrawable, fileLocation2, String.format(Locale.US, "%d_%d", objArr2), imageSize, null, false);
            return;
        }
        imageReceiver.setImage(path, filter, placeHolder != null ? new BitmapDrawable(null, placeHolder) : null, null, imageSize);
    }

    public boolean isShowingImage(MessageObject object) {
        return (!this.isVisible || this.disableShowCheck || object == null || this.currentMessageObject == null || this.currentMessageObject.getId() != object.getId()) ? false : true;
    }

    public boolean isShowingImage(FileLocation object) {
        return this.isVisible && !this.disableShowCheck && object != null && this.currentFileLocation != null && object.local_id == this.currentFileLocation.local_id && object.volume_id == this.currentFileLocation.volume_id && object.dc_id == this.currentFileLocation.dc_id;
    }

    public boolean isShowingImage(String object) {
        return (!this.isVisible || this.disableShowCheck || object == null || this.currentPathObject == null || !object.equals(this.currentPathObject)) ? false : true;
    }

    public void openPhoto(MessageObject messageObject, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        openPhoto(messageObject, null, null, null, PAGE_SPACING, provider, null, dialogId, mergeDialogId);
    }

    public void openPhoto(FileLocation fileLocation, PhotoViewerProvider provider) {
        openPhoto(null, fileLocation, null, null, PAGE_SPACING, provider, null, 0, 0);
    }

    public void openPhoto(ArrayList<MessageObject> messages, int index, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        openPhoto((MessageObject) messages.get(index), null, messages, null, index, provider, null, dialogId, mergeDialogId);
    }

    public void openPhotoForSelect(ArrayList<Object> photos, int index, int type, PhotoViewerProvider provider, ChatActivity chatActivity) {
        this.sendPhotoType = type;
        if (this.pickerView != null) {
            this.pickerView.doneButtonTextView.setText(this.sendPhotoType == gallery_menu_save ? LocaleController.getString("Set", C0691R.string.Set).toUpperCase() : LocaleController.getString("Send", C0691R.string.Send).toUpperCase());
        }
        openPhoto(null, null, null, photos, index, provider, chatActivity, 0, 0);
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.animationEndRunnable != null) {
                this.animationEndRunnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = PAGE_SPACING;
        }
        if (this.animationInProgress != 0) {
            return true;
        }
        return false;
    }

    public void openPhoto(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> messages, ArrayList<Object> photos, int index, PhotoViewerProvider provider, ChatActivity chatActivity, long dialogId, long mDialogId) {
        if (this.parentActivity != null && !this.isVisible) {
            if (provider != null || !checkAnimation()) {
                if (messageObject != null || fileLocation != null || messages != null || photos != null) {
                    PlaceProviderObject object = provider.getPlaceForPhoto(messageObject, fileLocation, index);
                    if (object != null || photos != null) {
                        WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                        if (this.windowView.attachedToWindow) {
                            try {
                                wm.removeView(this.windowView);
                            } catch (Exception e) {
                            }
                        }
                        try {
                            this.windowLayoutParams.type = 99;
                            this.windowLayoutParams.flags = gallery_menu_caption;
                            this.windowLayoutParams.softInputMode = PAGE_SPACING;
                            this.windowView.setFocusable(false);
                            this.containerView.setFocusable(false);
                            wm.addView(this.windowView, this.windowLayoutParams);
                            this.parentChatActivity = chatActivity;
                            ActionBar actionBar = this.actionBar;
                            Object[] objArr = new Object[gallery_menu_showall];
                            objArr[PAGE_SPACING] = Integer.valueOf(gallery_menu_save);
                            objArr[gallery_menu_save] = Integer.valueOf(gallery_menu_save);
                            actionBar.setTitle(LocaleController.formatString("Of", C0691R.string.Of, objArr));
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogPhotosLoaded);
                            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
                            this.placeProvider = provider;
                            this.mergeDialogId = mDialogId;
                            this.currentDialogId = dialogId;
                            if (this.velocityTracker == null) {
                                this.velocityTracker = VelocityTracker.obtain();
                            }
                            this.isVisible = true;
                            toggleActionBar(true, false);
                            if (object != null) {
                                float scale;
                                this.disableShowCheck = true;
                                this.animationInProgress = gallery_menu_save;
                                if (messageObject != null) {
                                    this.currentAnimation = object.imageReceiver.getAnimation();
                                }
                                onPhotoShow(messageObject, fileLocation, messages, photos, index, object);
                                Rect drawRegion = object.imageReceiver.getDrawRegion();
                                int orientation = object.imageReceiver.getOrientation();
                                this.animatingImageView.setVisibility(PAGE_SPACING);
                                this.animatingImageView.setRadius(object.radius);
                                this.animatingImageView.setOrientation(orientation);
                                this.animatingImageView.setNeedRadius(object.radius != 0);
                                this.animatingImageView.setImageBitmap(object.thumb);
                                this.animatingImageView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                                this.animatingImageView.setPivotX(0.0f);
                                this.animatingImageView.setPivotY(0.0f);
                                this.animatingImageView.setScaleX(object.scale);
                                this.animatingImageView.setScaleY(object.scale);
                                this.animatingImageView.setTranslationX(((float) object.viewX) + (((float) drawRegion.left) * object.scale));
                                this.animatingImageView.setTranslationY(((float) object.viewY) + (((float) drawRegion.top) * object.scale));
                                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                                layoutParams.width = drawRegion.right - drawRegion.left;
                                layoutParams.height = drawRegion.bottom - drawRegion.top;
                                this.animatingImageView.setLayoutParams(layoutParams);
                                float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                                float scaleY = ((float) (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
                                if (scaleX > scaleY) {
                                    scale = scaleY;
                                } else {
                                    scale = scaleX;
                                }
                                float xPos = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * scale)) / 2.0f;
                                float yPos = (((float) (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight)) - (((float) layoutParams.height) * scale)) / 2.0f;
                                int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                                int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                                int[] coords2 = new int[gallery_menu_showall];
                                object.parentView.getLocationInWindow(coords2);
                                int clipTop = ((coords2[gallery_menu_save] - AndroidUtilities.statusBarHeight) - (object.viewY + drawRegion.top)) + object.clipTopAddition;
                                if (clipTop < 0) {
                                    clipTop = PAGE_SPACING;
                                }
                                int clipBottom = (((object.viewY + drawRegion.top) + layoutParams.height) - ((coords2[gallery_menu_save] + object.parentView.getHeight()) - AndroidUtilities.statusBarHeight)) + object.clipBottomAddition;
                                if (clipBottom < 0) {
                                    clipBottom = PAGE_SPACING;
                                }
                                clipTop = Math.max(clipTop, clipVertical);
                                clipBottom = Math.max(clipBottom, clipVertical);
                                this.animationValues[PAGE_SPACING][PAGE_SPACING] = this.animatingImageView.getScaleX();
                                this.animationValues[PAGE_SPACING][gallery_menu_save] = this.animatingImageView.getScaleY();
                                this.animationValues[PAGE_SPACING][gallery_menu_showall] = this.animatingImageView.getTranslationX();
                                this.animationValues[PAGE_SPACING][gallery_menu_send] = this.animatingImageView.getTranslationY();
                                this.animationValues[PAGE_SPACING][gallery_menu_crop] = ((float) clipHorizontal) * object.scale;
                                this.animationValues[PAGE_SPACING][5] = ((float) clipTop) * object.scale;
                                this.animationValues[PAGE_SPACING][gallery_menu_delete] = ((float) clipBottom) * object.scale;
                                this.animationValues[PAGE_SPACING][gallery_menu_tune] = (float) this.animatingImageView.getRadius();
                                this.animationValues[gallery_menu_save][PAGE_SPACING] = scale;
                                this.animationValues[gallery_menu_save][gallery_menu_save] = scale;
                                this.animationValues[gallery_menu_save][gallery_menu_showall] = xPos;
                                this.animationValues[gallery_menu_save][gallery_menu_send] = yPos;
                                this.animationValues[gallery_menu_save][gallery_menu_crop] = 0.0f;
                                this.animationValues[gallery_menu_save][5] = 0.0f;
                                this.animationValues[gallery_menu_save][gallery_menu_delete] = 0.0f;
                                this.animationValues[gallery_menu_save][gallery_menu_tune] = 0.0f;
                                this.animatingImageView.setAnimationProgress(0.0f);
                                this.backgroundDrawable.setAlpha(PAGE_SPACING);
                                this.containerView.setAlpha(0.0f);
                                AnimatorSet animatorSet = new AnimatorSet();
                                Animator[] animatorArr = new Animator[gallery_menu_send];
                                animatorArr[PAGE_SPACING] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                                animatorArr[gallery_menu_save] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{PAGE_SPACING, NalUnitUtil.EXTENDED_SAR});
                                animatorArr[gallery_menu_showall] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
                                animatorSet.playTogether(animatorArr);
                                this.animationEndRunnable = new AnonymousClass30(photos);
                                animatorSet.setDuration(200);
                                animatorSet.addListener(new AnimatorListenerAdapterProxy() {

                                    /* renamed from: org.telegram.ui.PhotoViewer.31.1 */
                                    class C13701 implements Runnable {
                                        C13701() {
                                        }

                                        public void run() {
                                            NotificationCenter.getInstance().setAnimationInProgress(false);
                                            if (PhotoViewer.this.animationEndRunnable != null) {
                                                PhotoViewer.this.animationEndRunnable.run();
                                                PhotoViewer.this.animationEndRunnable = null;
                                            }
                                        }
                                    }

                                    public void onAnimationEnd(Animator animation) {
                                        AndroidUtilities.runOnUIThread(new C13701());
                                    }
                                });
                                this.transitionAnimationStartTime = System.currentTimeMillis();
                                AndroidUtilities.runOnUIThread(new AnonymousClass32(animatorSet));
                                if (VERSION.SDK_INT >= 18) {
                                    this.containerView.setLayerType(gallery_menu_showall, null);
                                }
                                this.backgroundDrawable.drawRunnable = new AnonymousClass33(object);
                                return;
                            }
                            if (photos != null) {
                                this.windowLayoutParams.flags = PAGE_SPACING;
                                this.windowLayoutParams.softInputMode = 32;
                                wm.updateViewLayout(this.windowView, this.windowLayoutParams);
                                this.windowView.setFocusable(true);
                                this.containerView.setFocusable(true);
                            }
                            this.backgroundDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
                            this.containerView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                            onPhotoShow(messageObject, fileLocation, messages, photos, index, object);
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                        }
                    }
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void closePhoto(boolean r30, boolean r31) {
        /*
        r29 = this;
        if (r31 != 0) goto L_0x002b;
    L_0x0002:
        r0 = r29;
        r0 = r0.currentEditMode;
        r21 = r0;
        if (r21 == 0) goto L_0x002b;
    L_0x000a:
        r0 = r29;
        r0 = r0.currentEditMode;
        r21 = r0;
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x0021;
    L_0x0018:
        r0 = r29;
        r0 = r0.photoCropView;
        r21 = r0;
        r21.cancelAnimationRunnable();
    L_0x0021:
        r21 = 0;
        r0 = r29;
        r1 = r21;
        r0.switchToEditMode(r1);
    L_0x002a:
        return;
    L_0x002b:
        r0 = r29;
        r0 = r0.visibleDialog;	 Catch:{ Exception -> 0x05c6 }
        r21 = r0;
        if (r21 == 0) goto L_0x0044;
    L_0x0033:
        r0 = r29;
        r0 = r0.visibleDialog;	 Catch:{ Exception -> 0x05c6 }
        r21 = r0;
        r21.dismiss();	 Catch:{ Exception -> 0x05c6 }
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.visibleDialog = r0;	 Catch:{ Exception -> 0x05c6 }
    L_0x0044:
        r0 = r29;
        r0 = r0.currentEditMode;
        r21 = r0;
        if (r21 == 0) goto L_0x0082;
    L_0x004c:
        r0 = r29;
        r0 = r0.currentEditMode;
        r21 = r0;
        r22 = 2;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x05d0;
    L_0x005a:
        r0 = r29;
        r0 = r0.photoFilterView;
        r21 = r0;
        r21.shutdown();
        r0 = r29;
        r0 = r0.containerView;
        r21 = r0;
        r0 = r29;
        r0 = r0.photoFilterView;
        r22 = r0;
        r21.removeView(r22);
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.photoFilterView = r0;
    L_0x007a:
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.currentEditMode = r0;
    L_0x0082:
        r0 = r29;
        r0 = r0.parentActivity;
        r21 = r0;
        if (r21 == 0) goto L_0x002a;
    L_0x008a:
        r0 = r29;
        r0 = r0.isVisible;
        r21 = r0;
        if (r21 == 0) goto L_0x002a;
    L_0x0092:
        r21 = r29.checkAnimation();
        if (r21 != 0) goto L_0x002a;
    L_0x0098:
        r0 = r29;
        r0 = r0.placeProvider;
        r21 = r0;
        if (r21 == 0) goto L_0x002a;
    L_0x00a0:
        r0 = r29;
        r0 = r0.captionEditText;
        r21 = r0;
        r21 = r21.hideActionMode();
        if (r21 == 0) goto L_0x00ae;
    L_0x00ac:
        if (r31 == 0) goto L_0x002a;
    L_0x00ae:
        r29.releasePlayer();
        r0 = r29;
        r0 = r0.captionEditText;
        r21 = r0;
        r21.onDestroy();
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.parentChatActivity = r0;
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.FileDidFailedLoad;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.FileDidLoaded;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.mediaCountDidLoaded;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.mediaDidLoaded;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.dialogPhotosLoaded;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.messenger.NotificationCenter.getInstance();
        r22 = org.telegram.messenger.NotificationCenter.emojiDidLoaded;
        r0 = r21;
        r1 = r29;
        r2 = r22;
        r0.removeObserver(r1, r2);
        r21 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r0 = r29;
        r0 = r0.classGuid;
        r22 = r0;
        r21.cancelRequestsForGuid(r22);
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.isActionBarVisible = r0;
        r0 = r29;
        r0 = r0.velocityTracker;
        r21 = r0;
        if (r21 == 0) goto L_0x0159;
    L_0x0148:
        r0 = r29;
        r0 = r0.velocityTracker;
        r21 = r0;
        r21.recycle();
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.velocityTracker = r0;
    L_0x0159:
        r21 = org.telegram.tgnet.ConnectionsManager.getInstance();
        r0 = r29;
        r0 = r0.classGuid;
        r22 = r0;
        r21.cancelRequestsForGuid(r22);
        r0 = r29;
        r0 = r0.placeProvider;
        r21 = r0;
        r0 = r29;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r29;
        r0 = r0.currentFileLocation;
        r23 = r0;
        r0 = r29;
        r0 = r0.currentIndex;
        r24 = r0;
        r14 = r21.getPlaceForPhoto(r22, r23, r24);
        if (r30 == 0) goto L_0x06eb;
    L_0x0184:
        r21 = 1;
        r0 = r21;
        r1 = r29;
        r1.animationInProgress = r0;
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r22 = 0;
        r21.setVisibility(r22);
        r0 = r29;
        r0 = r0.containerView;
        r21 = r0;
        r21.invalidate();
        r4 = new android.animation.AnimatorSet;
        r4.<init>();
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r13 = r21.getLayoutParams();
        r10 = 0;
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r29;
        r0 = r0.centerImage;
        r22 = r0;
        r22 = r22.getOrientation();
        r21.setOrientation(r22);
        if (r14 == 0) goto L_0x05fa;
    L_0x01c5:
        r0 = r29;
        r0 = r0.animatingImageView;
        r22 = r0;
        r0 = r14.radius;
        r21 = r0;
        if (r21 == 0) goto L_0x05f6;
    L_0x01d1:
        r21 = 1;
    L_0x01d3:
        r0 = r22;
        r1 = r21;
        r0.setNeedRadius(r1);
        r0 = r14.imageReceiver;
        r21 = r0;
        r10 = r21.getDrawRegion();
        r0 = r10.right;
        r21 = r0;
        r0 = r10.left;
        r22 = r0;
        r21 = r21 - r22;
        r0 = r21;
        r13.width = r0;
        r0 = r10.bottom;
        r21 = r0;
        r0 = r10.top;
        r22 = r0;
        r21 = r21 - r22;
        r0 = r21;
        r13.height = r0;
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r14.thumb;
        r22 = r0;
        r21.setImageBitmap(r22);
    L_0x020b:
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r21;
        r0.setLayoutParams(r13);
        r21 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r21;
        r0 = r0.x;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r0 = r13.width;
        r22 = r0;
        r0 = r22;
        r0 = (float) r0;
        r22 = r0;
        r16 = r21 / r22;
        r21 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r21;
        r0 = r0.y;
        r21 = r0;
        r22 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r21 = r21 - r22;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r0 = r13.height;
        r22 = r0;
        r0 = r22;
        r0 = (float) r0;
        r22 = r0;
        r17 = r21 / r22;
        r21 = (r16 > r17 ? 1 : (r16 == r17 ? 0 : -1));
        if (r21 <= 0) goto L_0x0636;
    L_0x024e:
        r15 = r17;
    L_0x0250:
        r0 = r13.width;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r0 = r29;
        r0 = r0.scale;
        r22 = r0;
        r21 = r21 * r22;
        r18 = r21 * r15;
        r0 = r13.height;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r0 = r29;
        r0 = r0.scale;
        r22 = r0;
        r21 = r21 * r22;
        r12 = r21 * r15;
        r21 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r21;
        r0 = r0.x;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r21 = r21 - r18;
        r22 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r19 = r21 / r22;
        r21 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r21;
        r0 = r0.y;
        r21 = r0;
        r22 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r21 = r21 - r22;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r21 = r21 - r12;
        r22 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r20 = r21 / r22;
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r29;
        r0 = r0.translationX;
        r22 = r0;
        r22 = r22 + r19;
        r21.setTranslationX(r22);
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r29;
        r0 = r0.translationY;
        r22 = r0;
        r22 = r22 + r20;
        r21.setTranslationY(r22);
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r29;
        r0 = r0.scale;
        r22 = r0;
        r22 = r22 * r15;
        r21.setScaleX(r22);
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r29;
        r0 = r0.scale;
        r22 = r0;
        r22 = r22 * r15;
        r21.setScaleY(r22);
        if (r14 == 0) goto L_0x063a;
    L_0x02e6:
        r0 = r14.imageReceiver;
        r21 = r0;
        r22 = 0;
        r23 = 1;
        r21.setVisible(r22, r23);
        r0 = r10.left;
        r21 = r0;
        r0 = r14.imageReceiver;
        r22 = r0;
        r22 = r22.getImageX();
        r21 = r21 - r22;
        r6 = java.lang.Math.abs(r21);
        r0 = r10.top;
        r21 = r0;
        r0 = r14.imageReceiver;
        r22 = r0;
        r22 = r22.getImageY();
        r21 = r21 - r22;
        r8 = java.lang.Math.abs(r21);
        r21 = 2;
        r0 = r21;
        r9 = new int[r0];
        r0 = r14.parentView;
        r21 = r0;
        r0 = r21;
        r0.getLocationInWindow(r9);
        r21 = 1;
        r21 = r9[r21];
        r22 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r21 = r21 - r22;
        r0 = r14.viewY;
        r22 = r0;
        r0 = r10.top;
        r23 = r0;
        r22 = r22 + r23;
        r21 = r21 - r22;
        r0 = r14.clipTopAddition;
        r22 = r0;
        r7 = r21 + r22;
        if (r7 >= 0) goto L_0x0341;
    L_0x0340:
        r7 = 0;
    L_0x0341:
        r0 = r14.viewY;
        r21 = r0;
        r0 = r10.top;
        r22 = r0;
        r21 = r21 + r22;
        r0 = r10.bottom;
        r22 = r0;
        r0 = r10.top;
        r23 = r0;
        r22 = r22 - r23;
        r21 = r21 + r22;
        r22 = 1;
        r22 = r9[r22];
        r0 = r14.parentView;
        r23 = r0;
        r23 = r23.getHeight();
        r22 = r22 + r23;
        r23 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r22 = r22 - r23;
        r21 = r21 - r22;
        r0 = r14.clipBottomAddition;
        r22 = r0;
        r5 = r21 + r22;
        if (r5 >= 0) goto L_0x0374;
    L_0x0373:
        r5 = 0;
    L_0x0374:
        r7 = java.lang.Math.max(r7, r8);
        r5 = java.lang.Math.max(r5, r8);
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 0;
        r0 = r29;
        r0 = r0.animatingImageView;
        r23 = r0;
        r23 = r23.getScaleX();
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 1;
        r0 = r29;
        r0 = r0.animatingImageView;
        r23 = r0;
        r23 = r23.getScaleY();
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 2;
        r0 = r29;
        r0 = r0.animatingImageView;
        r23 = r0;
        r23 = r23.getTranslationX();
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 3;
        r0 = r29;
        r0 = r0.animatingImageView;
        r23 = r0;
        r23 = r23.getTranslationY();
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 4;
        r23 = 0;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 5;
        r23 = 0;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 6;
        r23 = 0;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 7;
        r23 = 0;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 0;
        r0 = r14.scale;
        r23 = r0;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 1;
        r0 = r14.scale;
        r23 = r0;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 2;
        r0 = r14.viewX;
        r23 = r0;
        r0 = r23;
        r0 = (float) r0;
        r23 = r0;
        r0 = r10.left;
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;
        r24 = r0;
        r0 = r14.scale;
        r25 = r0;
        r24 = r24 * r25;
        r23 = r23 + r24;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 3;
        r0 = r14.viewY;
        r23 = r0;
        r0 = r23;
        r0 = (float) r0;
        r23 = r0;
        r0 = r10.top;
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;
        r24 = r0;
        r0 = r14.scale;
        r25 = r0;
        r24 = r24 * r25;
        r23 = r23 + r24;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 4;
        r0 = (float) r6;
        r23 = r0;
        r0 = r14.scale;
        r24 = r0;
        r23 = r23 * r24;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 5;
        r0 = (float) r7;
        r23 = r0;
        r0 = r14.scale;
        r24 = r0;
        r23 = r23 * r24;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 6;
        r0 = (float) r5;
        r23 = r0;
        r0 = r14.scale;
        r24 = r0;
        r23 = r23 * r24;
        r21[r22] = r23;
        r0 = r29;
        r0 = r0.animationValues;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 7;
        r0 = r14.radius;
        r23 = r0;
        r0 = r23;
        r0 = (float) r0;
        r23 = r0;
        r21[r22] = r23;
        r21 = 3;
        r0 = r21;
        r0 = new android.animation.Animator[r0];
        r21 = r0;
        r22 = 0;
        r0 = r29;
        r0 = r0.animatingImageView;
        r23 = r0;
        r24 = "animationProgress";
        r25 = 2;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r25 = {0, 1065353216};
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r21[r22] = r23;
        r22 = 1;
        r0 = r29;
        r0 = r0.backgroundDrawable;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new int[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofInt(r23, r24, r25);
        r21[r22] = r23;
        r22 = 2;
        r0 = r29;
        r0 = r0.containerView;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r21[r22] = r23;
        r0 = r21;
        r4.playTogether(r0);
    L_0x0550:
        r21 = new org.telegram.ui.PhotoViewer$34;
        r0 = r21;
        r1 = r29;
        r0.<init>(r14);
        r0 = r21;
        r1 = r29;
        r1.animationEndRunnable = r0;
        r22 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r22;
        r4.setDuration(r0);
        r21 = new org.telegram.ui.PhotoViewer$35;
        r0 = r21;
        r1 = r29;
        r0.<init>();
        r0 = r21;
        r4.addListener(r0);
        r22 = java.lang.System.currentTimeMillis();
        r0 = r22;
        r2 = r29;
        r2.transitionAnimationStartTime = r0;
        r21 = android.os.Build.VERSION.SDK_INT;
        r22 = 18;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x0595;
    L_0x0588:
        r0 = r29;
        r0 = r0.containerView;
        r21 = r0;
        r22 = 2;
        r23 = 0;
        r21.setLayerType(r22, r23);
    L_0x0595:
        r4.start();
    L_0x0598:
        r0 = r29;
        r0 = r0.currentAnimation;
        r21 = r0;
        if (r21 == 0) goto L_0x002a;
    L_0x05a0:
        r0 = r29;
        r0 = r0.currentAnimation;
        r21 = r0;
        r22 = 0;
        r21.setSecondParentView(r22);
        r21 = 0;
        r0 = r21;
        r1 = r29;
        r1.currentAnimation = r0;
        r0 = r29;
        r0 = r0.centerImage;
        r22 = r0;
        r21 = 0;
        r21 = (android.graphics.drawable.Drawable) r21;
        r0 = r22;
        r1 = r21;
        r0.setImageBitmap(r1);
        goto L_0x002a;
    L_0x05c6:
        r11 = move-exception;
        r21 = "tmessages";
        r0 = r21;
        org.telegram.messenger.FileLog.m13e(r0, r11);
        goto L_0x0044;
    L_0x05d0:
        r0 = r29;
        r0 = r0.currentEditMode;
        r21 = r0;
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x007a;
    L_0x05de:
        r0 = r29;
        r0 = r0.editorDoneLayout;
        r21 = r0;
        r22 = 8;
        r21.setVisibility(r22);
        r0 = r29;
        r0 = r0.photoCropView;
        r21 = r0;
        r22 = 8;
        r21.setVisibility(r22);
        goto L_0x007a;
    L_0x05f6:
        r21 = 0;
        goto L_0x01d3;
    L_0x05fa:
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r22 = 0;
        r21.setNeedRadius(r22);
        r0 = r29;
        r0 = r0.centerImage;
        r21 = r0;
        r21 = r21.getImageWidth();
        r0 = r21;
        r13.width = r0;
        r0 = r29;
        r0 = r0.centerImage;
        r21 = r0;
        r21 = r21.getImageHeight();
        r0 = r21;
        r13.height = r0;
        r0 = r29;
        r0 = r0.animatingImageView;
        r21 = r0;
        r0 = r29;
        r0 = r0.centerImage;
        r22 = r0;
        r22 = r22.getBitmap();
        r21.setImageBitmap(r22);
        goto L_0x020b;
    L_0x0636:
        r15 = r16;
        goto L_0x0250;
    L_0x063a:
        r21 = 4;
        r0 = r21;
        r0 = new android.animation.Animator[r0];
        r22 = r0;
        r21 = 0;
        r0 = r29;
        r0 = r0.backgroundDrawable;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new int[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofInt(r23, r24, r25);
        r22[r21] = r23;
        r21 = 1;
        r0 = r29;
        r0 = r0.animatingImageView;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r22[r21] = r23;
        r23 = 2;
        r0 = r29;
        r0 = r0.animatingImageView;
        r24 = r0;
        r25 = "translationY";
        r21 = 1;
        r0 = r21;
        r0 = new float[r0];
        r26 = r0;
        r27 = 0;
        r0 = r29;
        r0 = r0.translationY;
        r21 = r0;
        r28 = 0;
        r21 = (r21 > r28 ? 1 : (r21 == r28 ? 0 : -1));
        if (r21 < 0) goto L_0x06d8;
    L_0x069e:
        r21 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r21;
        r0 = r0.y;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
    L_0x06ab:
        r26[r27] = r21;
        r21 = android.animation.ObjectAnimator.ofFloat(r24, r25, r26);
        r22[r23] = r21;
        r21 = 3;
        r0 = r29;
        r0 = r0.containerView;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r22[r21] = r23;
        r0 = r22;
        r4.playTogether(r0);
        goto L_0x0550;
    L_0x06d8:
        r21 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r21;
        r0 = r0.y;
        r21 = r0;
        r0 = r21;
        r0 = -r0;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        goto L_0x06ab;
    L_0x06eb:
        r4 = new android.animation.AnimatorSet;
        r4.<init>();
        r21 = 4;
        r0 = r21;
        r0 = new android.animation.Animator[r0];
        r21 = r0;
        r22 = 0;
        r0 = r29;
        r0 = r0.containerView;
        r23 = r0;
        r24 = "scaleX";
        r25 = 1;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r26 = 0;
        r27 = 1063675494; // 0x3f666666 float:0.9 double:5.2552552E-315;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r21[r22] = r23;
        r22 = 1;
        r0 = r29;
        r0 = r0.containerView;
        r23 = r0;
        r24 = "scaleY";
        r25 = 1;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r26 = 0;
        r27 = 1063675494; // 0x3f666666 float:0.9 double:5.2552552E-315;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r21[r22] = r23;
        r22 = 2;
        r0 = r29;
        r0 = r0.backgroundDrawable;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new int[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofInt(r23, r24, r25);
        r21[r22] = r23;
        r22 = 3;
        r0 = r29;
        r0 = r0.containerView;
        r23 = r0;
        r24 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new float[r0];
        r25 = r0;
        r26 = 0;
        r27 = 0;
        r25[r26] = r27;
        r23 = android.animation.ObjectAnimator.ofFloat(r23, r24, r25);
        r21[r22] = r23;
        r0 = r21;
        r4.playTogether(r0);
        r21 = 2;
        r0 = r21;
        r1 = r29;
        r1.animationInProgress = r0;
        r21 = new org.telegram.ui.PhotoViewer$36;
        r0 = r21;
        r1 = r29;
        r0.<init>(r14);
        r0 = r21;
        r1 = r29;
        r1.animationEndRunnable = r0;
        r22 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r22;
        r4.setDuration(r0);
        r21 = new org.telegram.ui.PhotoViewer$37;
        r0 = r21;
        r1 = r29;
        r0.<init>();
        r0 = r21;
        r4.addListener(r0);
        r22 = java.lang.System.currentTimeMillis();
        r0 = r22;
        r2 = r29;
        r2.transitionAnimationStartTime = r0;
        r21 = android.os.Build.VERSION.SDK_INT;
        r22 = 18;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x07c4;
    L_0x07b7:
        r0 = r29;
        r0 = r0.containerView;
        r21 = r0;
        r22 = 2;
        r23 = 0;
        r21.setLayerType(r22, r23);
    L_0x07c4:
        r4.start();
        goto L_0x0598;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.closePhoto(boolean, boolean):void");
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (this.captionEditText != null) {
                this.captionEditText.onDestroy();
            }
            Instance = null;
        }
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isVisible = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.currentThumb = null;
        if (this.currentAnimation != null) {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int a = PAGE_SPACING; a < gallery_menu_send; a += gallery_menu_save) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.containerView.post(new Runnable() {
            public void run() {
                PhotoViewer.this.animatingImageView.setImageBitmap(null);
                try {
                    if (PhotoViewer.this.windowView.getParent() != null) {
                        ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).removeView(PhotoViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        });
        if (this.placeProvider != null) {
            this.placeProvider.willHidePhotoViewer();
        }
        this.placeProvider = null;
        this.disableShowCheck = false;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
    }

    private void redraw(int count) {
        if (count < gallery_menu_delete && this.containerView != null) {
            this.containerView.invalidate();
            AndroidUtilities.runOnUIThread(new AnonymousClass39(count), 100);
        }
    }

    public void onResume() {
        redraw(PAGE_SPACING);
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
        } else if (this.captionDoneItem.getVisibility() != gallery_menu_caption) {
            closeCaptionEnter(true);
        }
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    private void updateMinMax(float scale) {
        int maxW = ((int) ((((float) this.centerImage.getImageWidth()) * scale) - ((float) getContainerViewWidth()))) / gallery_menu_showall;
        int maxH = ((int) ((((float) this.centerImage.getImageHeight()) * scale) - ((float) getContainerViewHeight()))) / gallery_menu_showall;
        if (maxW > 0) {
            this.minX = (float) (-maxW);
            this.maxX = (float) maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = (float) (-maxH);
            this.maxY = (float) maxH;
        } else {
            this.maxY = 0.0f;
            this.minY = 0.0f;
        }
        if (this.currentEditMode == gallery_menu_save) {
            this.maxX += this.photoCropView.getLimitX();
            this.maxY += this.photoCropView.getLimitY();
            this.minX -= this.photoCropView.getLimitWidth();
            this.minY -= this.photoCropView.getLimitHeight();
        }
    }

    private int getAdditionX() {
        if (this.currentEditMode != 0) {
            return AndroidUtilities.dp(14.0f);
        }
        return PAGE_SPACING;
    }

    private int getAdditionY() {
        if (this.currentEditMode != 0) {
            return AndroidUtilities.dp(14.0f);
        }
        return PAGE_SPACING;
    }

    private int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    private int getContainerViewWidth(int mode) {
        int width = this.containerView.getWidth();
        if (mode != 0) {
            return width - AndroidUtilities.dp(28.0f);
        }
        return width;
    }

    private int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    private int getContainerViewHeight(int mode) {
        int height = AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight;
        if (mode == gallery_menu_save) {
            return height - AndroidUtilities.dp(76.0f);
        }
        if (mode == gallery_menu_showall) {
            return height - AndroidUtilities.dp(154.0f);
        }
        return height;
    }

    private boolean onTouchEvent(MotionEvent ev) {
        if (this.animationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        if (this.currentEditMode == gallery_menu_showall) {
            this.photoFilterView.onTouch(ev);
            return true;
        }
        if (this.currentEditMode == gallery_menu_save) {
            if (ev.getPointerCount() != gallery_menu_save) {
                this.photoCropView.onTouch(null);
            } else if (this.photoCropView.onTouch(ev)) {
                updateMinMax(this.scale);
                return true;
            }
        }
        if (this.captionEditText.isPopupShowing() || this.captionEditText.isKeyboardVisible()) {
            return true;
        }
        if (this.currentEditMode == 0 && ev.getPointerCount() == gallery_menu_save && this.gestureDetector.onTouchEvent(ev) && this.doubleTap) {
            this.doubleTap = false;
            this.moving = false;
            this.zooming = false;
            checkMinMax(false);
            return true;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (this.currentEditMode == gallery_menu_save) {
                this.photoCropView.cancelAnimationRunnable();
            }
            this.discardTap = false;
            if (!this.scroller.isFinished()) {
                this.scroller.abortAnimation();
            }
            if (!(this.draggingDown || this.changingPage)) {
                if (this.canZoom && ev.getPointerCount() == gallery_menu_showall) {
                    this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(gallery_menu_save) - ev.getX(PAGE_SPACING)), (double) (ev.getY(gallery_menu_save) - ev.getY(PAGE_SPACING)));
                    this.pinchStartScale = this.scale;
                    this.pinchCenterX = (ev.getX(PAGE_SPACING) + ev.getX(gallery_menu_save)) / 2.0f;
                    this.pinchCenterY = (ev.getY(PAGE_SPACING) + ev.getY(gallery_menu_save)) / 2.0f;
                    this.pinchStartX = this.translationX;
                    this.pinchStartY = this.translationY;
                    this.zooming = true;
                    this.moving = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                } else if (ev.getPointerCount() == gallery_menu_save) {
                    this.moveStartX = ev.getX();
                    float y = ev.getY();
                    this.moveStartY = y;
                    this.dragY = y;
                    this.draggingDown = false;
                    this.canDragDown = true;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                }
            }
        } else if (ev.getActionMasked() == gallery_menu_showall) {
            if (this.currentEditMode == gallery_menu_save) {
                this.photoCropView.cancelAnimationRunnable();
            }
            if (this.canZoom && ev.getPointerCount() == gallery_menu_showall && !this.draggingDown && this.zooming && !this.changingPage) {
                this.discardTap = true;
                this.scale = (((float) Math.hypot((double) (ev.getX(gallery_menu_save) - ev.getX(PAGE_SPACING)), (double) (ev.getY(gallery_menu_save) - ev.getY(PAGE_SPACING)))) / this.pinchStartDistance) * this.pinchStartScale;
                this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                updateMinMax(this.scale);
                this.containerView.invalidate();
            } else if (ev.getPointerCount() == gallery_menu_save) {
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(ev);
                }
                float dx = Math.abs(ev.getX() - this.moveStartX);
                float dy = Math.abs(ev.getY() - this.dragY);
                if (dx > ((float) AndroidUtilities.dp(3.0f)) || dy > ((float) AndroidUtilities.dp(3.0f))) {
                    this.discardTap = true;
                }
                if (!(this.placeProvider instanceof EmptyPhotoViewerProvider) && this.currentEditMode == 0 && this.canDragDown && !this.draggingDown && this.scale == TouchHelperCallback.ALPHA_FULL && dy >= ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) && dy / 2.0f > dx) {
                    this.draggingDown = true;
                    this.moving = false;
                    this.dragY = ev.getY();
                    if (this.isActionBarVisible && this.canShowBottom) {
                        toggleActionBar(false, true);
                    } else if (this.pickerView.getVisibility() == 0) {
                        toggleActionBar(false, true);
                        toggleCheckImageView(false);
                    }
                    return true;
                } else if (this.draggingDown) {
                    this.translationY = ev.getY() - this.dragY;
                    this.containerView.invalidate();
                } else if (this.invalidCoords || this.animationStartTime != 0) {
                    this.invalidCoords = false;
                    this.moveStartX = ev.getX();
                    this.moveStartY = ev.getY();
                } else {
                    float moveDx = this.moveStartX - ev.getX();
                    float moveDy = this.moveStartY - ev.getY();
                    if (this.moving || this.currentEditMode != 0 || ((this.scale == TouchHelperCallback.ALPHA_FULL && Math.abs(moveDy) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(moveDx)) || this.scale != TouchHelperCallback.ALPHA_FULL)) {
                        if (!this.moving) {
                            moveDx = 0.0f;
                            moveDy = 0.0f;
                            this.moving = true;
                            this.canDragDown = false;
                        }
                        this.moveStartX = ev.getX();
                        this.moveStartY = ev.getY();
                        updateMinMax(this.scale);
                        if ((this.translationX < this.minX && !(this.currentEditMode == 0 && this.rightImage.hasImage())) || (this.translationX > this.maxX && !(this.currentEditMode == 0 && this.leftImage.hasImage()))) {
                            moveDx /= 3.0f;
                        }
                        if (this.maxY == 0.0f && this.minY == 0.0f && this.currentEditMode == 0) {
                            if (this.translationY - moveDy < this.minY) {
                                this.translationY = this.minY;
                                moveDy = 0.0f;
                            } else if (this.translationY - moveDy > this.maxY) {
                                this.translationY = this.maxY;
                                moveDy = 0.0f;
                            }
                        } else if (this.translationY < this.minY || this.translationY > this.maxY) {
                            moveDy /= 3.0f;
                        }
                        this.translationX -= moveDx;
                        if (!(this.scale == TouchHelperCallback.ALPHA_FULL && this.currentEditMode == 0)) {
                            this.translationY -= moveDy;
                        }
                        this.containerView.invalidate();
                    }
                }
            }
        } else if (ev.getActionMasked() == gallery_menu_send || ev.getActionMasked() == gallery_menu_save || ev.getActionMasked() == gallery_menu_delete) {
            if (this.currentEditMode == gallery_menu_save) {
                this.photoCropView.startAnimationRunnable();
            }
            if (this.zooming) {
                this.invalidCoords = true;
                if (this.scale < TouchHelperCallback.ALPHA_FULL) {
                    updateMinMax(TouchHelperCallback.ALPHA_FULL);
                    animateTo(TouchHelperCallback.ALPHA_FULL, 0.0f, 0.0f, true);
                } else if (this.scale > 3.0f) {
                    float atx = (this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                    float aty = (this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                    updateMinMax(3.0f);
                    if (atx < this.minX) {
                        atx = this.minX;
                    } else if (atx > this.maxX) {
                        atx = this.maxX;
                    }
                    if (aty < this.minY) {
                        aty = this.minY;
                    } else if (aty > this.maxY) {
                        aty = this.maxY;
                    }
                    animateTo(3.0f, atx, aty, true);
                } else {
                    checkMinMax(true);
                }
                this.zooming = false;
            } else if (this.draggingDown) {
                if (Math.abs(this.dragY - ev.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                    closePhoto(true, false);
                } else {
                    if (this.pickerView.getVisibility() == 0) {
                        toggleActionBar(true, true);
                        toggleCheckImageView(true);
                    }
                    animateTo(TouchHelperCallback.ALPHA_FULL, 0.0f, 0.0f, false);
                }
                this.draggingDown = false;
            } else if (this.moving) {
                float moveToX = this.translationX;
                float moveToY = this.translationY;
                updateMinMax(this.scale);
                this.moving = false;
                this.canDragDown = true;
                float velocity = 0.0f;
                if (this.velocityTracker != null && this.scale == TouchHelperCallback.ALPHA_FULL) {
                    this.velocityTracker.computeCurrentVelocity(1000);
                    velocity = this.velocityTracker.getXVelocity();
                }
                if (this.currentEditMode == 0) {
                    if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / gallery_menu_send)) || velocity < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImage()) {
                        goToNext();
                        return true;
                    } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / gallery_menu_send)) || velocity > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImage()) {
                        goToPrev();
                        return true;
                    }
                }
                if (this.translationX < this.minX) {
                    moveToX = this.minX;
                } else if (this.translationX > this.maxX) {
                    moveToX = this.maxX;
                }
                if (this.translationY < this.minY) {
                    moveToY = this.minY;
                } else if (this.translationY > this.maxY) {
                    moveToY = this.maxY;
                }
                animateTo(this.scale, moveToX, moveToY, false);
            }
        }
        return false;
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            moveToX = this.minX;
        } else if (this.translationX > this.maxX) {
            moveToX = this.maxX;
        }
        if (this.translationY < this.minY) {
            moveToY = this.minY;
        } else if (this.translationY > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0.0f;
        if (this.scale != TouchHelperCallback.ALPHA_FULL) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / gallery_menu_showall)) * this.scale;
        }
        this.switchImageAfterAnimation = gallery_menu_save;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (PAGE_SPACING / gallery_menu_showall)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != TouchHelperCallback.ALPHA_FULL) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / gallery_menu_showall)) * this.scale;
        }
        this.switchImageAfterAnimation = gallery_menu_showall;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + extra) + ((float) (PAGE_SPACING / gallery_menu_showall)), this.translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale != newScale || this.translationX != newTx || this.translationY != newTy) {
            this.zoomAnimation = isZoom;
            this.animateToScale = newScale;
            this.animateToX = newTx;
            this.animateToY = newTy;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.imageMoveAnimation;
            Animator[] animatorArr = new Animator[gallery_menu_save];
            animatorArr[PAGE_SPACING] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL});
            animatorSet.playTogether(animatorArr);
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.containerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    private void onDraw(android.graphics.Canvas r37) {
        /*
        r36 = this;
        r0 = r36;
        r0 = r0.animationInProgress;
        r31 = r0;
        r32 = 1;
        r0 = r31;
        r1 = r32;
        if (r0 == r1) goto L_0x0024;
    L_0x000e:
        r0 = r36;
        r0 = r0.isVisible;
        r31 = r0;
        if (r31 != 0) goto L_0x0025;
    L_0x0016:
        r0 = r36;
        r0 = r0.animationInProgress;
        r31 = r0;
        r32 = 2;
        r0 = r31;
        r1 = r32;
        if (r0 == r1) goto L_0x0025;
    L_0x0024:
        return;
    L_0x0025:
        r5 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r0 = r36;
        r0 = r0.imageMoveAnimation;
        r31 = r0;
        if (r31 == 0) goto L_0x0734;
    L_0x002f:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.isFinished();
        if (r31 != 0) goto L_0x0044;
    L_0x003b:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31.abortAnimation();
    L_0x0044:
        r0 = r36;
        r0 = r0.scale;
        r31 = r0;
        r0 = r36;
        r0 = r0.animateToScale;
        r32 = r0;
        r0 = r36;
        r0 = r0.scale;
        r33 = r0;
        r32 = r32 - r33;
        r0 = r36;
        r0 = r0.animationValue;
        r33 = r0;
        r32 = r32 * r33;
        r27 = r31 + r32;
        r0 = r36;
        r0 = r0.translationX;
        r31 = r0;
        r0 = r36;
        r0 = r0.animateToX;
        r32 = r0;
        r0 = r36;
        r0 = r0.translationX;
        r33 = r0;
        r32 = r32 - r33;
        r0 = r36;
        r0 = r0.animationValue;
        r33 = r0;
        r32 = r32 * r33;
        r28 = r31 + r32;
        r0 = r36;
        r0 = r0.translationY;
        r31 = r0;
        r0 = r36;
        r0 = r0.animateToY;
        r32 = r0;
        r0 = r36;
        r0 = r0.translationY;
        r33 = r0;
        r32 = r32 - r33;
        r0 = r36;
        r0 = r0.animationValue;
        r33 = r0;
        r32 = r32 * r33;
        r29 = r31 + r32;
        r0 = r36;
        r0 = r0.currentEditMode;
        r31 = r0;
        r32 = 1;
        r0 = r31;
        r1 = r32;
        if (r0 != r1) goto L_0x00bb;
    L_0x00ac:
        r0 = r36;
        r0 = r0.photoCropView;
        r31 = r0;
        r0 = r36;
        r0 = r0.animationValue;
        r32 = r0;
        r31.setAnimationProgress(r32);
    L_0x00bb:
        r0 = r36;
        r0 = r0.animateToScale;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 != 0) goto L_0x00e1;
    L_0x00c7:
        r0 = r36;
        r0 = r0.scale;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 != 0) goto L_0x00e1;
    L_0x00d3:
        r0 = r36;
        r0 = r0.translationX;
        r31 = r0;
        r32 = 0;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 != 0) goto L_0x00e1;
    L_0x00df:
        r5 = r29;
    L_0x00e1:
        r8 = r27;
        r10 = r29;
        r9 = r28;
        r0 = r36;
        r0 = r0.containerView;
        r31 = r0;
        r31.invalidate();
    L_0x00f0:
        r0 = r36;
        r0 = r0.currentEditMode;
        r31 = r0;
        if (r31 != 0) goto L_0x08b6;
    L_0x00f8:
        r0 = r36;
        r0 = r0.scale;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 != 0) goto L_0x08b6;
    L_0x0104:
        r31 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r31 = (r5 > r31 ? 1 : (r5 == r31 ? 0 : -1));
        if (r31 == 0) goto L_0x08b6;
    L_0x010a:
        r0 = r36;
        r0 = r0.zoomAnimation;
        r31 = r0;
        if (r31 != 0) goto L_0x08b6;
    L_0x0112:
        r31 = r36.getContainerViewHeight();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r32 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = r31 / r32;
        r0 = r36;
        r0 = r0.backgroundDrawable;
        r31 = r0;
        r32 = 1123942400; // 0x42fe0000 float:127.0 double:5.553013277E-315;
        r33 = 1132396544; // 0x437f0000 float:255.0 double:5.5947823E-315;
        r34 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r35 = java.lang.Math.abs(r5);
        r0 = r35;
        r35 = java.lang.Math.min(r0, r15);
        r35 = r35 / r15;
        r34 = r34 - r35;
        r33 = r33 * r34;
        r32 = java.lang.Math.max(r32, r33);
        r0 = r32;
        r0 = (int) r0;
        r32 = r0;
        r31.setAlpha(r32);
    L_0x0147:
        r24 = 0;
        r0 = r36;
        r0 = r0.currentEditMode;
        r31 = r0;
        if (r31 != 0) goto L_0x0194;
    L_0x0151:
        r0 = r36;
        r0 = r0.scale;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 < 0) goto L_0x018a;
    L_0x015d:
        r0 = r36;
        r0 = r0.zoomAnimation;
        r31 = r0;
        if (r31 != 0) goto L_0x018a;
    L_0x0165:
        r0 = r36;
        r0 = r0.zooming;
        r31 = r0;
        if (r31 != 0) goto L_0x018a;
    L_0x016d:
        r0 = r36;
        r0 = r0.maxX;
        r31 = r0;
        r32 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r32 = org.telegram.messenger.AndroidUtilities.dp(r32);
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r31 = r31 + r32;
        r31 = (r9 > r31 ? 1 : (r9 == r31 ? 0 : -1));
        if (r31 <= 0) goto L_0x08c3;
    L_0x0184:
        r0 = r36;
        r0 = r0.leftImage;
        r24 = r0;
    L_0x018a:
        if (r24 == 0) goto L_0x08e2;
    L_0x018c:
        r31 = 1;
    L_0x018e:
        r0 = r31;
        r1 = r36;
        r1.changingPage = r0;
    L_0x0194:
        r0 = r36;
        r0 = r0.rightImage;
        r31 = r0;
        r0 = r24;
        r1 = r31;
        if (r0 != r1) goto L_0x0317;
    L_0x01a0:
        r25 = r9;
        r21 = 0;
        r4 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r36;
        r0 = r0.zoomAnimation;
        r31 = r0;
        if (r31 != 0) goto L_0x01ee;
    L_0x01ae:
        r0 = r36;
        r0 = r0.minX;
        r31 = r0;
        r31 = (r25 > r31 ? 1 : (r25 == r31 ? 0 : -1));
        if (r31 >= 0) goto L_0x01ee;
    L_0x01b8:
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r36;
        r0 = r0.minX;
        r32 = r0;
        r32 = r32 - r25;
        r33 = r37.getWidth();
        r0 = r33;
        r0 = (float) r0;
        r33 = r0;
        r32 = r32 / r33;
        r4 = java.lang.Math.min(r31, r32);
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = r31 - r4;
        r32 = 1050253722; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r21 = r31 * r32;
        r31 = r37.getWidth();
        r0 = r31;
        r0 = -r0;
        r31 = r0;
        r32 = PAGE_SPACING;
        r32 = r32 / 2;
        r31 = r31 - r32;
        r0 = r31;
        r0 = (float) r0;
        r25 = r0;
    L_0x01ee:
        r31 = r24.hasBitmapImage();
        if (r31 == 0) goto L_0x02a5;
    L_0x01f4:
        r37.save();
        r31 = r36.getContainerViewWidth();
        r31 = r31 / 2;
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r32 = r36.getContainerViewHeight();
        r32 = r32 / 2;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r31 = r37.getWidth();
        r32 = PAGE_SPACING;
        r32 = r32 / 2;
        r31 = r31 + r32;
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r31 = r31 + r25;
        r32 = 0;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = r31 - r21;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r32 = r32 - r21;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.scale(r1, r2);
        r7 = r24.getBitmapWidth();
        r6 = r24.getBitmapHeight();
        r31 = r36.getContainerViewWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = (float) r7;
        r32 = r0;
        r22 = r31 / r32;
        r31 = r36.getContainerViewHeight();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = (float) r6;
        r32 = r0;
        r23 = r31 / r32;
        r31 = (r22 > r23 ? 1 : (r22 == r23 ? 0 : -1));
        if (r31 <= 0) goto L_0x08e6;
    L_0x026b:
        r18 = r23;
    L_0x026d:
        r0 = (float) r7;
        r31 = r0;
        r31 = r31 * r18;
        r0 = r31;
        r0 = (int) r0;
        r30 = r0;
        r0 = (float) r6;
        r31 = r0;
        r31 = r31 * r18;
        r0 = r31;
        r14 = (int) r0;
        r0 = r24;
        r0.setAlpha(r4);
        r0 = r30;
        r0 = -r0;
        r31 = r0;
        r31 = r31 / 2;
        r0 = -r14;
        r32 = r0;
        r32 = r32 / 2;
        r0 = r24;
        r1 = r31;
        r2 = r32;
        r3 = r30;
        r0.setImageCoords(r1, r2, r3, r14);
        r0 = r24;
        r1 = r37;
        r0.draw(r1);
        r37.restore();
    L_0x02a5:
        r37.save();
        r31 = r10 / r8;
        r0 = r37;
        r1 = r25;
        r2 = r31;
        r0.translate(r1, r2);
        r31 = r37.getWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.scale;
        r32 = r0;
        r33 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r32 = r32 + r33;
        r31 = r31 * r32;
        r32 = PAGE_SPACING;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r31 = r31 + r32;
        r32 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r31 = r31 / r32;
        r0 = -r10;
        r32 = r0;
        r32 = r32 / r8;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 1;
        r31 = r31[r32];
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r32 = r32 - r21;
        r31.setScale(r32);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 1;
        r31 = r31[r32];
        r0 = r31;
        r0.setAlpha(r4);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 1;
        r31 = r31[r32];
        r0 = r31;
        r1 = r37;
        r0.onDraw(r1);
        r37.restore();
    L_0x0317:
        r26 = r9;
        r21 = 0;
        r4 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r36;
        r0 = r0.zoomAnimation;
        r31 = r0;
        if (r31 != 0) goto L_0x035f;
    L_0x0325:
        r0 = r36;
        r0 = r0.maxX;
        r31 = r0;
        r31 = (r26 > r31 ? 1 : (r26 == r31 ? 0 : -1));
        if (r31 <= 0) goto L_0x035f;
    L_0x032f:
        r0 = r36;
        r0 = r0.currentEditMode;
        r31 = r0;
        if (r31 != 0) goto L_0x035f;
    L_0x0337:
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r36;
        r0 = r0.maxX;
        r32 = r0;
        r32 = r26 - r32;
        r33 = r37.getWidth();
        r0 = r33;
        r0 = (float) r0;
        r33 = r0;
        r32 = r32 / r33;
        r4 = java.lang.Math.min(r31, r32);
        r31 = 1050253722; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r21 = r4 * r31;
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = r31 - r4;
        r0 = r36;
        r0 = r0.maxX;
        r26 = r0;
    L_0x035f:
        r31 = android.os.Build.VERSION.SDK_INT;
        r32 = 16;
        r0 = r31;
        r1 = r32;
        if (r0 < r1) goto L_0x08ea;
    L_0x0369:
        r0 = r36;
        r0 = r0.aspectRatioFrameLayout;
        r31 = r0;
        if (r31 == 0) goto L_0x08ea;
    L_0x0371:
        r0 = r36;
        r0 = r0.aspectRatioFrameLayout;
        r31 = r0;
        r31 = r31.getVisibility();
        if (r31 != 0) goto L_0x08ea;
    L_0x037d:
        r11 = 1;
    L_0x037e:
        r0 = r36;
        r0 = r0.centerImage;
        r31 = r0;
        r31 = r31.hasBitmapImage();
        if (r31 == 0) goto L_0x0599;
    L_0x038a:
        r37.save();
        r31 = r36.getContainerViewWidth();
        r31 = r31 / 2;
        r32 = r36.getAdditionX();
        r31 = r31 + r32;
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r32 = r36.getContainerViewHeight();
        r32 = r32 / 2;
        r33 = r36.getAdditionY();
        r32 = r32 + r33;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r0 = r37;
        r1 = r26;
        r0.translate(r1, r10);
        r31 = r8 - r21;
        r32 = r8 - r21;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.scale(r1, r2);
        r0 = r36;
        r0 = r0.currentEditMode;
        r31 = r0;
        r32 = 1;
        r0 = r31;
        r1 = r32;
        if (r0 != r1) goto L_0x03e7;
    L_0x03da:
        r0 = r36;
        r0 = r0.photoCropView;
        r31 = r0;
        r0 = r31;
        r1 = r26;
        r0.setBitmapParams(r8, r1, r10);
    L_0x03e7:
        r0 = r36;
        r0 = r0.centerImage;
        r31 = r0;
        r7 = r31.getBitmapWidth();
        r0 = r36;
        r0 = r0.centerImage;
        r31 = r0;
        r6 = r31.getBitmapHeight();
        if (r11 == 0) goto L_0x044e;
    L_0x03fd:
        r0 = r36;
        r0 = r0.textureUploaded;
        r31 = r0;
        if (r31 == 0) goto L_0x044e;
    L_0x0405:
        r0 = (float) r7;
        r31 = r0;
        r0 = (float) r6;
        r32 = r0;
        r19 = r31 / r32;
        r0 = r36;
        r0 = r0.videoTextureView;
        r31 = r0;
        r31 = r31.getMeasuredWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.videoTextureView;
        r32 = r0;
        r32 = r32.getMeasuredHeight();
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r20 = r31 / r32;
        r31 = r19 - r20;
        r31 = java.lang.Math.abs(r31);
        r32 = 1008981770; // 0x3c23d70a float:0.01 double:4.9850323E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 <= 0) goto L_0x044e;
    L_0x043a:
        r0 = r36;
        r0 = r0.videoTextureView;
        r31 = r0;
        r7 = r31.getMeasuredWidth();
        r0 = r36;
        r0 = r0.videoTextureView;
        r31 = r0;
        r6 = r31.getMeasuredHeight();
    L_0x044e:
        r31 = r36.getContainerViewWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = (float) r7;
        r32 = r0;
        r22 = r31 / r32;
        r31 = r36.getContainerViewHeight();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = (float) r6;
        r32 = r0;
        r23 = r31 / r32;
        r31 = (r22 > r23 ? 1 : (r22 == r23 ? 0 : -1));
        if (r31 <= 0) goto L_0x08ed;
    L_0x046e:
        r18 = r23;
    L_0x0470:
        r0 = (float) r7;
        r31 = r0;
        r31 = r31 * r18;
        r0 = r31;
        r0 = (int) r0;
        r30 = r0;
        r0 = (float) r6;
        r31 = r0;
        r31 = r31 * r18;
        r0 = r31;
        r14 = (int) r0;
        if (r11 == 0) goto L_0x04a0;
    L_0x0484:
        r0 = r36;
        r0 = r0.textureUploaded;
        r31 = r0;
        if (r31 == 0) goto L_0x04a0;
    L_0x048c:
        r0 = r36;
        r0 = r0.videoCrossfadeStarted;
        r31 = r0;
        if (r31 == 0) goto L_0x04a0;
    L_0x0494:
        r0 = r36;
        r0 = r0.videoCrossfadeAlpha;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 == 0) goto L_0x04d5;
    L_0x04a0:
        r0 = r36;
        r0 = r0.centerImage;
        r31 = r0;
        r0 = r31;
        r0.setAlpha(r4);
        r0 = r36;
        r0 = r0.centerImage;
        r31 = r0;
        r0 = r30;
        r0 = -r0;
        r32 = r0;
        r32 = r32 / 2;
        r0 = -r14;
        r33 = r0;
        r33 = r33 / 2;
        r0 = r31;
        r1 = r32;
        r2 = r33;
        r3 = r30;
        r0.setImageCoords(r1, r2, r3, r14);
        r0 = r36;
        r0 = r0.centerImage;
        r31 = r0;
        r0 = r31;
        r1 = r37;
        r0.draw(r1);
    L_0x04d5:
        if (r11 == 0) goto L_0x0596;
    L_0x04d7:
        r0 = r36;
        r0 = r0.videoCrossfadeStarted;
        r31 = r0;
        if (r31 != 0) goto L_0x0501;
    L_0x04df:
        r0 = r36;
        r0 = r0.textureUploaded;
        r31 = r0;
        if (r31 == 0) goto L_0x0501;
    L_0x04e7:
        r31 = 1;
        r0 = r31;
        r1 = r36;
        r1.videoCrossfadeStarted = r0;
        r31 = 0;
        r0 = r31;
        r1 = r36;
        r1.videoCrossfadeAlpha = r0;
        r32 = java.lang.System.currentTimeMillis();
        r0 = r32;
        r2 = r36;
        r2.videoCrossfadeAlphaLastTime = r0;
    L_0x0501:
        r0 = r30;
        r0 = -r0;
        r31 = r0;
        r31 = r31 / 2;
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = -r14;
        r32 = r0;
        r32 = r32 / 2;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r0 = r36;
        r0 = r0.videoTextureView;
        r31 = r0;
        r0 = r36;
        r0 = r0.videoCrossfadeAlpha;
        r32 = r0;
        r32 = r32 * r4;
        r31.setAlpha(r32);
        r0 = r36;
        r0 = r0.aspectRatioFrameLayout;
        r31 = r0;
        r0 = r31;
        r1 = r37;
        r0.draw(r1);
        r0 = r36;
        r0 = r0.videoCrossfadeStarted;
        r31 = r0;
        if (r31 == 0) goto L_0x0596;
    L_0x0546:
        r0 = r36;
        r0 = r0.videoCrossfadeAlpha;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 >= 0) goto L_0x0596;
    L_0x0552:
        r16 = java.lang.System.currentTimeMillis();
        r0 = r36;
        r0 = r0.videoCrossfadeAlphaLastTime;
        r32 = r0;
        r12 = r16 - r32;
        r0 = r16;
        r2 = r36;
        r2.videoCrossfadeAlphaLastTime = r0;
        r0 = r36;
        r0 = r0.videoCrossfadeAlpha;
        r31 = r0;
        r0 = (float) r12;
        r32 = r0;
        r33 = 1133903872; // 0x43960000 float:300.0 double:5.60222949E-315;
        r32 = r32 / r33;
        r31 = r31 + r32;
        r0 = r31;
        r1 = r36;
        r1.videoCrossfadeAlpha = r0;
        r0 = r36;
        r0 = r0.containerView;
        r31 = r0;
        r31.invalidate();
        r0 = r36;
        r0 = r0.videoCrossfadeAlpha;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 <= 0) goto L_0x0596;
    L_0x058e:
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r31;
        r1 = r36;
        r1.videoCrossfadeAlpha = r0;
    L_0x0596:
        r37.restore();
    L_0x0599:
        if (r11 != 0) goto L_0x05f1;
    L_0x059b:
        r0 = r36;
        r0 = r0.videoPlayerControlFrameLayout;
        r31 = r0;
        if (r31 == 0) goto L_0x05af;
    L_0x05a3:
        r0 = r36;
        r0 = r0.videoPlayerControlFrameLayout;
        r31 = r0;
        r31 = r31.getVisibility();
        if (r31 == 0) goto L_0x05f1;
    L_0x05af:
        r37.save();
        r31 = r10 / r8;
        r0 = r37;
        r1 = r26;
        r2 = r31;
        r0.translate(r1, r2);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 0;
        r31 = r31[r32];
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r32 = r32 - r21;
        r31.setScale(r32);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 0;
        r31 = r31[r32];
        r0 = r31;
        r0.setAlpha(r4);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 0;
        r31 = r31[r32];
        r0 = r31;
        r1 = r37;
        r0.onDraw(r1);
        r37.restore();
    L_0x05f1:
        r0 = r36;
        r0 = r0.leftImage;
        r31 = r0;
        r0 = r24;
        r1 = r31;
        if (r0 != r1) goto L_0x0024;
    L_0x05fd:
        r31 = r24.hasBitmapImage();
        if (r31 == 0) goto L_0x06bf;
    L_0x0603:
        r37.save();
        r31 = r36.getContainerViewWidth();
        r31 = r31 / 2;
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r32 = r36.getContainerViewHeight();
        r32 = r32 / 2;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r31 = r37.getWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.scale;
        r32 = r0;
        r33 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r32 = r32 + r33;
        r31 = r31 * r32;
        r32 = PAGE_SPACING;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r31 = r31 + r32;
        r0 = r31;
        r0 = -r0;
        r31 = r0;
        r32 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r31 = r31 / r32;
        r31 = r31 + r9;
        r32 = 0;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r7 = r24.getBitmapWidth();
        r6 = r24.getBitmapHeight();
        r31 = r36.getContainerViewWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = (float) r7;
        r32 = r0;
        r22 = r31 / r32;
        r31 = r36.getContainerViewHeight();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = (float) r6;
        r32 = r0;
        r23 = r31 / r32;
        r31 = (r22 > r23 ? 1 : (r22 == r23 ? 0 : -1));
        if (r31 <= 0) goto L_0x08f1;
    L_0x0681:
        r18 = r23;
    L_0x0683:
        r0 = (float) r7;
        r31 = r0;
        r31 = r31 * r18;
        r0 = r31;
        r0 = (int) r0;
        r30 = r0;
        r0 = (float) r6;
        r31 = r0;
        r31 = r31 * r18;
        r0 = r31;
        r14 = (int) r0;
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r24;
        r1 = r31;
        r0.setAlpha(r1);
        r0 = r30;
        r0 = -r0;
        r31 = r0;
        r31 = r31 / 2;
        r0 = -r14;
        r32 = r0;
        r32 = r32 / 2;
        r0 = r24;
        r1 = r31;
        r2 = r32;
        r3 = r30;
        r0.setImageCoords(r1, r2, r3, r14);
        r0 = r24;
        r1 = r37;
        r0.draw(r1);
        r37.restore();
    L_0x06bf:
        r37.save();
        r31 = r10 / r8;
        r0 = r37;
        r1 = r31;
        r0.translate(r9, r1);
        r31 = r37.getWidth();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.scale;
        r32 = r0;
        r33 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r32 = r32 + r33;
        r31 = r31 * r32;
        r32 = PAGE_SPACING;
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r31 = r31 + r32;
        r0 = r31;
        r0 = -r0;
        r31 = r0;
        r32 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r31 = r31 / r32;
        r0 = -r10;
        r32 = r0;
        r32 = r32 / r8;
        r0 = r37;
        r1 = r31;
        r2 = r32;
        r0.translate(r1, r2);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 2;
        r31 = r31[r32];
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31.setScale(r32);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 2;
        r31 = r31[r32];
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31.setAlpha(r32);
        r0 = r36;
        r0 = r0.radialProgressViews;
        r31 = r0;
        r32 = 2;
        r31 = r31[r32];
        r0 = r31;
        r1 = r37;
        r0.onDraw(r1);
        r37.restore();
        goto L_0x0024;
    L_0x0734:
        r0 = r36;
        r0 = r0.animationStartTime;
        r32 = r0;
        r34 = 0;
        r31 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1));
        if (r31 == 0) goto L_0x079a;
    L_0x0740:
        r0 = r36;
        r0 = r0.animateToX;
        r31 = r0;
        r0 = r31;
        r1 = r36;
        r1.translationX = r0;
        r0 = r36;
        r0 = r0.animateToY;
        r31 = r0;
        r0 = r31;
        r1 = r36;
        r1.translationY = r0;
        r0 = r36;
        r0 = r0.animateToScale;
        r31 = r0;
        r0 = r31;
        r1 = r36;
        r1.scale = r0;
        r32 = 0;
        r0 = r32;
        r2 = r36;
        r2.animationStartTime = r0;
        r0 = r36;
        r0 = r0.currentEditMode;
        r31 = r0;
        r32 = 1;
        r0 = r31;
        r1 = r32;
        if (r0 != r1) goto L_0x0785;
    L_0x077a:
        r0 = r36;
        r0 = r0.photoCropView;
        r31 = r0;
        r32 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31.setAnimationProgress(r32);
    L_0x0785:
        r0 = r36;
        r0 = r0.scale;
        r31 = r0;
        r0 = r36;
        r1 = r31;
        r0.updateMinMax(r1);
        r31 = 0;
        r0 = r31;
        r1 = r36;
        r1.zoomAnimation = r0;
    L_0x079a:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.isFinished();
        if (r31 != 0) goto L_0x0849;
    L_0x07a6:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.computeScrollOffset();
        if (r31 == 0) goto L_0x0849;
    L_0x07b2:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.getStartX();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.maxX;
        r32 = r0;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 >= 0) goto L_0x07f9;
    L_0x07cb:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.getStartX();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.minX;
        r32 = r0;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 <= 0) goto L_0x07f9;
    L_0x07e4:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.getCurrX();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r31;
        r1 = r36;
        r1.translationX = r0;
    L_0x07f9:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.getStartY();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.maxY;
        r32 = r0;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 >= 0) goto L_0x0840;
    L_0x0812:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.getStartY();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r36;
        r0 = r0.minY;
        r32 = r0;
        r31 = (r31 > r32 ? 1 : (r31 == r32 ? 0 : -1));
        if (r31 <= 0) goto L_0x0840;
    L_0x082b:
        r0 = r36;
        r0 = r0.scroller;
        r31 = r0;
        r31 = r31.getCurrY();
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r0 = r31;
        r1 = r36;
        r1.translationY = r0;
    L_0x0840:
        r0 = r36;
        r0 = r0.containerView;
        r31 = r0;
        r31.invalidate();
    L_0x0849:
        r0 = r36;
        r0 = r0.switchImageAfterAnimation;
        r31 = r0;
        if (r31 == 0) goto L_0x087a;
    L_0x0851:
        r0 = r36;
        r0 = r0.switchImageAfterAnimation;
        r31 = r0;
        r32 = 1;
        r0 = r31;
        r1 = r32;
        if (r0 != r1) goto L_0x0894;
    L_0x085f:
        r0 = r36;
        r0 = r0.currentIndex;
        r31 = r0;
        r31 = r31 + 1;
        r32 = 0;
        r0 = r36;
        r1 = r31;
        r2 = r32;
        r0.setImageIndex(r1, r2);
    L_0x0872:
        r31 = 0;
        r0 = r31;
        r1 = r36;
        r1.switchImageAfterAnimation = r0;
    L_0x087a:
        r0 = r36;
        r8 = r0.scale;
        r0 = r36;
        r10 = r0.translationY;
        r0 = r36;
        r9 = r0.translationX;
        r0 = r36;
        r0 = r0.moving;
        r31 = r0;
        if (r31 != 0) goto L_0x00f0;
    L_0x088e:
        r0 = r36;
        r5 = r0.translationY;
        goto L_0x00f0;
    L_0x0894:
        r0 = r36;
        r0 = r0.switchImageAfterAnimation;
        r31 = r0;
        r32 = 2;
        r0 = r31;
        r1 = r32;
        if (r0 != r1) goto L_0x0872;
    L_0x08a2:
        r0 = r36;
        r0 = r0.currentIndex;
        r31 = r0;
        r31 = r31 + -1;
        r32 = 0;
        r0 = r36;
        r1 = r31;
        r2 = r32;
        r0.setImageIndex(r1, r2);
        goto L_0x0872;
    L_0x08b6:
        r0 = r36;
        r0 = r0.backgroundDrawable;
        r31 = r0;
        r32 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r31.setAlpha(r32);
        goto L_0x0147;
    L_0x08c3:
        r0 = r36;
        r0 = r0.minX;
        r31 = r0;
        r32 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r32 = org.telegram.messenger.AndroidUtilities.dp(r32);
        r0 = r32;
        r0 = (float) r0;
        r32 = r0;
        r31 = r31 - r32;
        r31 = (r9 > r31 ? 1 : (r9 == r31 ? 0 : -1));
        if (r31 >= 0) goto L_0x018a;
    L_0x08da:
        r0 = r36;
        r0 = r0.rightImage;
        r24 = r0;
        goto L_0x018a;
    L_0x08e2:
        r31 = 0;
        goto L_0x018e;
    L_0x08e6:
        r18 = r22;
        goto L_0x026d;
    L_0x08ea:
        r11 = 0;
        goto L_0x037e;
    L_0x08ed:
        r18 = r22;
        goto L_0x0470;
    L_0x08f1:
        r18 = r22;
        goto L_0x0683;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    @SuppressLint({"DrawAllocation"})
    private void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            this.scale = TouchHelperCallback.ALPHA_FULL;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
            updateMinMax(this.scale);
            if (this.checkImageView != null) {
                this.checkImageView.post(new Runnable() {
                    public void run() {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                        float f = (rotation == PhotoViewer.gallery_menu_send || rotation == PhotoViewer.gallery_menu_save) ? 58.0f : 68.0f;
                        layoutParams.topMargin = AndroidUtilities.dp(f);
                        PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
                    }
                });
            }
        }
    }

    private void onActionClick(boolean download) {
        if (this.currentMessageObject != null && this.currentFileNames[PAGE_SPACING] != null) {
            File file = null;
            if (!(this.currentMessageObject.messageOwner.attachPath == null || this.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                file = new File(this.currentMessageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file == null) {
                file = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file == null) {
                if (!download) {
                    return;
                }
                if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[PAGE_SPACING])) {
                    FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
                } else {
                    FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
                }
            } else if (VERSION.SDK_INT >= 16) {
                preparePlayer(file, true);
            } else {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(Uri.fromFile(file), MimeTypes.VIDEO_MP4);
                this.parentActivity.startActivityForResult(intent, 500);
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale != TouchHelperCallback.ALPHA_FULL) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        boolean z = false;
        if (this.discardTap) {
            return false;
        }
        if (this.canShowBottom) {
            boolean drawTextureView;
            if (VERSION.SDK_INT < 16 || this.aspectRatioFrameLayout == null || this.aspectRatioFrameLayout.getVisibility() != 0) {
                drawTextureView = false;
            } else {
                drawTextureView = true;
            }
            if (!(this.radialProgressViews[PAGE_SPACING] == null || this.containerView == null || drawTextureView)) {
                int state = this.radialProgressViews[PAGE_SPACING].backgroundState;
                if (state > 0 && state <= gallery_menu_send) {
                    float x = e.getX();
                    float y = e.getY();
                    if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                        onActionClick(true);
                        checkProgress(PAGE_SPACING, true);
                        return true;
                    }
                }
            }
            if (!this.isActionBarVisible) {
                z = true;
            }
            toggleActionBar(z, true);
            return true;
        } else if (this.sendPhotoType != 0) {
            return true;
        } else {
            this.checkImageView.performClick();
            return true;
        }
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (!this.canZoom || (this.scale == TouchHelperCallback.ALPHA_FULL && (this.translationY != 0.0f || this.translationX != 0.0f))) {
            return false;
        }
        if (this.animationStartTime != 0 || this.animationInProgress != 0) {
            return false;
        }
        if (this.scale == TouchHelperCallback.ALPHA_FULL) {
            float atx = (e.getX() - ((float) (getContainerViewWidth() / gallery_menu_showall))) - (((e.getX() - ((float) (getContainerViewWidth() / gallery_menu_showall))) - this.translationX) * (3.0f / this.scale));
            float aty = (e.getY() - ((float) (getContainerViewHeight() / gallery_menu_showall))) - (((e.getY() - ((float) (getContainerViewHeight() / gallery_menu_showall))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (atx < this.minX) {
                atx = this.minX;
            } else if (atx > this.maxX) {
                atx = this.maxX;
            }
            if (aty < this.minY) {
                aty = this.minY;
            } else if (aty > this.maxY) {
                aty = this.maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(TouchHelperCallback.ALPHA_FULL, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
