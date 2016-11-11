package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ChatAttachAlert extends BottomSheet implements NotificationCenterDelegate, PhotoViewerProvider, BottomSheetDelegateInterface {
    private ListAdapter adapter;
    private LinearLayoutManager attachPhotoLayoutManager;
    private RecyclerListView attachPhotoRecyclerView;
    private ViewGroup attachView;
    private ChatActivity baseFragment;
    private AnimatorSet currentHintAnimation;
    private DecelerateInterpolator decelerateInterpolator;
    private ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private Runnable hideHintRunnable;
    private boolean hintShowed;
    private TextView hintTextView;
    private boolean ignoreLayout;
    private ArrayList<InnerAnimator> innerAnimators;
    private LinearLayoutManager layoutManager;
    private View lineView;
    private RecyclerListView listView;
    private boolean loading;
    private PhotoAttachAdapter photoAttachAdapter;
    private EmptyTextProgressView progressView;
    private boolean revealAnimationInProgress;
    private float revealRadius;
    private int revealX;
    private int revealY;
    private int scrollOffsetY;
    private AttachButton sendPhotosButton;
    private Drawable shadowDrawable;
    private boolean useRevealAnimation;
    private View[] views;
    private ArrayList<Holder> viewsCache;

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.11 */
    class AnonymousClass11 extends AnimatorListenerAdapter {
        final /* synthetic */ AnimatorSet val$animatorSet;
        final /* synthetic */ boolean val$open;

        AnonymousClass11(boolean z, AnimatorSet animatorSet) {
            this.val$open = z;
            this.val$animatorSet = animatorSet;
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animation)) {
                ChatAttachAlert.this.currentSheetAnimation = null;
                ChatAttachAlert.this.onRevealAnimationEnd(this.val$open);
                ChatAttachAlert.this.containerView.invalidate();
                ChatAttachAlert.this.containerView.setLayerType(0, null);
                if (!this.val$open) {
                    ChatAttachAlert.this.containerView.setVisibility(4);
                    try {
                        ChatAttachAlert.this.dismissInternal();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatAttachAlert.this.currentSheetAnimation != null && this.val$animatorSet.equals(animation)) {
                ChatAttachAlert.this.currentSheetAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.12 */
    class AnonymousClass12 extends AnimatorListenerAdapter {
        final /* synthetic */ AnimatorSet val$animatorSetInner;

        AnonymousClass12(AnimatorSet animatorSet) {
            this.val$animatorSetInner = animatorSet;
        }

        public void onAnimationEnd(Animator animation) {
            if (this.val$animatorSetInner != null) {
                this.val$animatorSetInner.start();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.4 */
    class C11204 extends FrameLayout {
        C11204(Context x0) {
            super(x0);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294.0f), C0747C.ENCODING_PCM_32BIT));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int width = right - left;
            int height = bottom - top;
            int t = AndroidUtilities.dp(8.0f);
            ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, t, width, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + t);
            ChatAttachAlert.this.progressView.layout(0, t, width, ChatAttachAlert.this.progressView.getMeasuredHeight() + t);
            ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0f), width, AndroidUtilities.dp(96.0f) + ChatAttachAlert.this.lineView.getMeasuredHeight());
            ChatAttachAlert.this.hintTextView.layout((width - ChatAttachAlert.this.hintTextView.getMeasuredWidth()) - AndroidUtilities.dp(5.0f), (height - ChatAttachAlert.this.hintTextView.getMeasuredHeight()) - AndroidUtilities.dp(5.0f), width - AndroidUtilities.dp(5.0f), height - AndroidUtilities.dp(5.0f));
            int diff = (width - AndroidUtilities.dp(360.0f)) / 3;
            for (int a = 0; a < 8; a++) {
                int y = AndroidUtilities.dp((float) (((a / 4) * 95) + 105));
                int x = AndroidUtilities.dp(10.0f) + ((a % 4) * (AndroidUtilities.dp(85.0f) + diff));
                ChatAttachAlert.this.views[a].layout(x, y, ChatAttachAlert.this.views[a].getMeasuredWidth() + x, ChatAttachAlert.this.views[a].getMeasuredHeight() + y);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.7 */
    class C11217 extends View {
        C11217(Context x0) {
            super(x0);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.8 */
    class C11228 implements OnClickListener {
        C11228() {
        }

        public void onClick(View v) {
            ChatAttachAlert.this.delegate.didPressedButton(((Integer) v.getTag()).intValue());
        }
    }

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable;
        private boolean checkingForLongPress;
        private User currentUser;
        private BackupImageView imageView;
        private TextView nameTextView;
        private CheckForLongPress pendingCheckForLongPress;
        private CheckForTap pendingCheckForTap;
        private int pressCount;
        private boolean pressed;

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert.AttachBotButton.1 */
        class C11231 implements DialogInterface.OnClickListener {
            C11231() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SearchQuery.removeInline(AttachBotButton.this.currentUser.id);
            }
        }

        class CheckForLongPress implements Runnable {
            public int currentPressCount;

            CheckForLongPress() {
            }

            public void run() {
                if (AttachBotButton.this.checkingForLongPress && AttachBotButton.this.getParent() != null && this.currentPressCount == AttachBotButton.this.pressCount) {
                    AttachBotButton.this.checkingForLongPress = false;
                    AttachBotButton.this.performHapticFeedback(0);
                    AttachBotButton.this.onLongPress();
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    AttachBotButton.this.onTouchEvent(event);
                    event.recycle();
                }
            }
        }

        private final class CheckForTap implements Runnable {
            private CheckForTap() {
            }

            public void run() {
                if (AttachBotButton.this.pendingCheckForLongPress == null) {
                    AttachBotButton.this.pendingCheckForLongPress = new CheckForLongPress();
                }
                AttachBotButton.this.pendingCheckForLongPress.currentPressCount = AttachBotButton.access$104(AttachBotButton.this);
                AttachBotButton.this.postDelayed(AttachBotButton.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }

        static /* synthetic */ int access$104(AttachBotButton x0) {
            int i = x0.pressCount + 1;
            x0.pressCount = i;
            return i;
        }

        public AttachBotButton(Context context) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            this.checkingForLongPress = false;
            this.pendingCheckForLongPress = null;
            this.pressCount = 0;
            this.pendingCheckForTap = null;
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
            addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.nameTextView.setTextSize(1, 12.0f);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(2);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 65.0f, 6.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0747C.ENCODING_PCM_32BIT));
        }

        private void onLongPress() {
            if (ChatAttachAlert.this.baseFragment != null && this.currentUser != null) {
                Builder builder = new Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0691R.string.ChatHintsDelete, ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C11231());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                builder.show();
            }
        }

        public void setUser(User user) {
            if (user != null) {
                this.currentUser = user;
                TLObject photo = null;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                if (!(user == null || user.photo == null)) {
                    photo = user.photo.photo_small;
                }
                this.imageView.setImage(photo, "50_50", this.avatarDrawable);
                requestLayout();
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean result = false;
            if (event.getAction() == 0) {
                this.pressed = true;
                invalidate();
                result = true;
            } else if (this.pressed) {
                if (event.getAction() == 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    this.pressed = false;
                    playSoundEffect(0);
                    ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance().getUser(Integer.valueOf(((TL_topPeer) SearchQuery.inlineBots.get(((Integer) getTag()).intValue())).peer.user_id)));
                    ChatAttachAlert.this.setUseRevealAnimation(false);
                    ChatAttachAlert.this.dismiss();
                    ChatAttachAlert.this.setUseRevealAnimation(true);
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.pressed = false;
                    invalidate();
                }
            }
            if (!result) {
                result = super.onTouchEvent(event);
            } else if (event.getAction() == 0) {
                startCheckLongPress();
            }
            if (!(event.getAction() == 0 || event.getAction() == 2)) {
                cancelCheckLongPress();
            }
            return result;
        }

        protected void startCheckLongPress() {
            if (!this.checkingForLongPress) {
                this.checkingForLongPress = true;
                if (this.pendingCheckForTap == null) {
                    this.pendingCheckForTap = new CheckForTap();
                }
                postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
            }
        }

        protected void cancelCheckLongPress() {
            this.checkingForLongPress = false;
            if (this.pendingCheckForLongPress != null) {
                removeCallbacks(this.pendingCheckForLongPress);
            }
            if (this.pendingCheckForTap != null) {
                removeCallbacks(this.pendingCheckForTap);
            }
        }
    }

    private class AttachButton extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public AttachButton(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(64, 64, 49));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.textView.setTextSize(1, 12.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 64.0f, 0.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), C0747C.ENCODING_PCM_32BIT));
        }

        public void setTextAndIcon(CharSequence text, Drawable drawable) {
            this.textView.setText(text);
            this.imageView.setBackgroundDrawable(drawable);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i);

        void didSelectBot(User user);

        View getRevealView();
    }

    private class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.2 */
    class C18132 extends ItemDecoration {
        C18132() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.3 */
    class C18143 extends OnScrollListener {
        C18143() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (ChatAttachAlert.this.listView.getChildCount() > 0) {
                if (ChatAttachAlert.this.hintShowed && ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1) {
                    ChatAttachAlert.this.hideHint();
                    ChatAttachAlert.this.hintShowed = false;
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("bothint", true).commit();
                }
                ChatAttachAlert.this.updateLayout();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.6 */
    class C18156 implements OnItemClickListener {
        C18156() {
        }

        public void onItemClick(View view, int position) {
            if (ChatAttachAlert.this.baseFragment != null && ChatAttachAlert.this.baseFragment.getParentActivity() != null) {
                if (ChatAttachAlert.this.deviceHasGoodCamera && position == 0) {
                    AndroidUtilities.generatePicturePath();
                    return;
                }
                if (ChatAttachAlert.this.deviceHasGoodCamera) {
                    position--;
                }
                ArrayList<Object> arrayList = MediaController.allPhotosAlbumEntry.photos;
                if (position >= 0 && position < arrayList.size()) {
                    PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, 0, ChatAttachAlert.this, ChatAttachAlert.this.baseFragment);
                    AndroidUtilities.hideKeyboard(ChatAttachAlert.this.baseFragment.getFragmentView().findFocus());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.9 */
    class C18169 extends AnimatorListenerAdapterProxy {
        C18169() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                ChatAttachAlert.this.currentHintAnimation = null;
                if (ChatAttachAlert.this.hintTextView != null) {
                    ChatAttachAlert.this.hintTextView.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                ChatAttachAlert.this.currentHintAnimation = null;
            }
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert.ListAdapter.1 */
        class C11241 extends FrameLayout {
            C11241(Context x0) {
                super(x0);
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int diff = ((right - left) - AndroidUtilities.dp(360.0f)) / 3;
                for (int a = 0; a < 4; a++) {
                    int x = AndroidUtilities.dp(10.0f) + ((a % 4) * (AndroidUtilities.dp(85.0f) + diff));
                    View child = getChildAt(a);
                    child.layout(x, 0, child.getMeasuredWidth() + x, child.getMeasuredHeight());
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = ChatAttachAlert.this.attachView;
                    break;
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    View frameLayout = new C11241(this.mContext);
                    for (int a = 0; a < 4; a++) {
                        frameLayout.addView(new AttachBotButton(this.mContext));
                    }
                    view = frameLayout;
                    frameLayout.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position > 1) {
                position = (position - 2) * 4;
                FrameLayout frameLayout = holder.itemView;
                for (int a = 0; a < 4; a++) {
                    AttachBotButton child = (AttachBotButton) frameLayout.getChildAt(a);
                    if (position + a >= SearchQuery.inlineBots.size()) {
                        child.setVisibility(4);
                    } else {
                        child.setVisibility(0);
                        child.setTag(Integer.valueOf(position + a));
                        child.setUser(MessagesController.getInstance().getUser(Integer.valueOf(((TL_topPeer) SearchQuery.inlineBots.get(position + a)).peer.user_id)));
                    }
                }
            }
        }

        public int getItemCount() {
            return (!SearchQuery.inlineBots.isEmpty() ? ((int) Math.ceil((double) (((float) SearchQuery.inlineBots.size()) / 4.0f))) + 1 : 0) + 1;
        }

        public int getItemViewType(int position) {
            switch (position) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    return 0;
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    return 1;
                default:
                    return 2;
            }
        }
    }

    private class PhotoAttachAdapter extends Adapter {
        private Context mContext;
        private HashMap<Integer, PhotoEntry> selectedPhotos;

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert.PhotoAttachAdapter.1 */
        class C18171 implements PhotoAttachPhotoCellDelegate {
            C18171() {
            }

            public void onCheckClick(PhotoAttachPhotoCell v) {
                PhotoEntry photoEntry = v.getPhotoEntry();
                if (PhotoAttachAdapter.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                    PhotoAttachAdapter.this.selectedPhotos.remove(Integer.valueOf(photoEntry.imageId));
                    v.setChecked(false, true);
                    photoEntry.imagePath = null;
                    photoEntry.thumbPath = null;
                    v.setPhotoEntry(photoEntry, ((Integer) v.getTag()).intValue() == MediaController.allPhotosAlbumEntry.photos.size() + -1);
                } else {
                    PhotoAttachAdapter.this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                    v.setChecked(true, true);
                }
                ChatAttachAlert.this.updatePhotosButton();
            }
        }

        public PhotoAttachAdapter(Context context) {
            this.selectedPhotos = new HashMap();
            this.mContext = context;
        }

        public void clearSelectedPhotos() {
            if (!this.selectedPhotos.isEmpty()) {
                for (Entry<Integer, PhotoEntry> entry : this.selectedPhotos.entrySet()) {
                    PhotoEntry photoEntry = (PhotoEntry) entry.getValue();
                    photoEntry.imagePath = null;
                    photoEntry.thumbPath = null;
                    photoEntry.caption = null;
                }
                this.selectedPhotos.clear();
                ChatAttachAlert.this.updatePhotosButton();
                notifyDataSetChanged();
            }
        }

        public Holder createHolder() {
            PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext);
            cell.setDelegate(new C18171());
            return new Holder(cell);
        }

        public HashMap<Integer, PhotoEntry> getSelectedPhotos() {
            return this.selectedPhotos;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (!ChatAttachAlert.this.deviceHasGoodCamera || position != 0) {
                boolean z;
                if (ChatAttachAlert.this.deviceHasGoodCamera) {
                    position--;
                }
                PhotoAttachPhotoCell cell = holder.itemView;
                PhotoEntry photoEntry = (PhotoEntry) MediaController.allPhotosAlbumEntry.photos.get(position);
                if (position == MediaController.allPhotosAlbumEntry.photos.size() - 1) {
                    z = true;
                } else {
                    z = false;
                }
                cell.setPhotoEntry(photoEntry, z);
                cell.setChecked(this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                cell.getImageView().setTag(Integer.valueOf(position));
                cell.setTag(Integer.valueOf(position));
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    return new Holder(new PhotoAttachCameraCell(this.mContext));
                default:
                    if (ChatAttachAlert.this.viewsCache.isEmpty()) {
                        return createHolder();
                    }
                    Holder holder = (Holder) ChatAttachAlert.this.viewsCache.get(0);
                    ChatAttachAlert.this.viewsCache.remove(0);
                    return holder;
            }
        }

        public int getItemCount() {
            int count = 0;
            if (ChatAttachAlert.this.deviceHasGoodCamera) {
                count = 0 + 1;
            }
            if (MediaController.allPhotosAlbumEntry != null) {
                return count + MediaController.allPhotosAlbumEntry.photos.size();
            }
            return count;
        }

        public int getItemViewType(int position) {
            if (ChatAttachAlert.this.deviceHasGoodCamera && position == 0) {
                return 1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.5 */
    class C20145 extends LinearLayoutManager {
        C20145(Context x0) {
            super(x0);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert.1 */
    class C20271 extends RecyclerListView {
        C20271(Context x0) {
            super(x0);
        }

        public void requestLayout() {
            if (!ChatAttachAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0 || ChatAttachAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) ChatAttachAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(ev);
            }
            ChatAttachAlert.this.dismiss();
            return true;
        }

        public boolean onTouchEvent(MotionEvent e) {
            return !ChatAttachAlert.this.isDismissed() && super.onTouchEvent(e);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (VERSION.SDK_INT >= 21) {
                height -= AndroidUtilities.statusBarHeight;
            }
            int contentSize = (AndroidUtilities.dp(294.0f) + ChatAttachAlert.backgroundPaddingTop) + (SearchQuery.inlineBots.isEmpty() ? 0 : (((int) Math.ceil((double) (((float) SearchQuery.inlineBots.size()) / 4.0f))) * AndroidUtilities.dp(100.0f)) + AndroidUtilities.dp(12.0f));
            int padding = contentSize == AndroidUtilities.dp(294.0f) ? 0 : height - AndroidUtilities.dp(294.0f);
            if (padding != 0 && contentSize < height) {
                padding -= height - contentSize;
            }
            if (padding == 0) {
                padding = ChatAttachAlert.backgroundPaddingTop;
            }
            if (getPaddingTop() != padding) {
                ChatAttachAlert.this.ignoreLayout = true;
                setPadding(ChatAttachAlert.backgroundPaddingLeft, padding, ChatAttachAlert.backgroundPaddingLeft, 0);
                ChatAttachAlert.this.ignoreLayout = false;
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), C0747C.ENCODING_PCM_32BIT));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ChatAttachAlert.this.updateLayout();
        }

        public void onDraw(Canvas canvas) {
            if (!ChatAttachAlert.this.useRevealAnimation || VERSION.SDK_INT > 19) {
                ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                ChatAttachAlert.this.shadowDrawable.draw(canvas);
                return;
            }
            canvas.save();
            canvas.clipRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight());
            if (ChatAttachAlert.this.revealAnimationInProgress) {
                canvas.drawCircle((float) ChatAttachAlert.this.revealX, (float) ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
            } else {
                canvas.drawRect((float) ChatAttachAlert.backgroundPaddingLeft, (float) ChatAttachAlert.this.scrollOffsetY, (float) (getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft), (float) getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
            }
            canvas.restore();
        }
    }

    public ChatAttachAlert(Context context) {
        View[] viewArr;
        int a;
        super(context, false);
        this.views = new View[20];
        this.viewsCache = new ArrayList(8);
        this.innerAnimators = new ArrayList();
        this.deviceHasGoodCamera = false;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.loading = true;
        setDelegate(this);
        setUseRevealAnimation(true);
        ViewGroup c20271;
        RecyclerListView recyclerListView;
        LayoutManager linearLayoutManager;
        Adapter listAdapter;
        RecyclerListView recyclerListView2;
        EmptyTextProgressView emptyTextProgressView;
        if (this.deviceHasGoodCamera) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadInlineHints);
            this.shadowDrawable = context.getResources().getDrawable(C0691R.drawable.sheet_shadow);
            c20271 = new C20271(context);
            this.listView = c20271;
            this.containerView = c20271;
            this.listView.setTag(Integer.valueOf(10));
            this.containerView.setWillNotDraw(false);
            this.listView.setClipToPadding(false);
            recyclerListView = this.listView;
            linearLayoutManager = new LinearLayoutManager(getContext());
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.layoutManager.setOrientation(1);
            recyclerListView = this.listView;
            listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setEnabled(true);
            this.listView.setGlowColor(-657673);
            this.listView.addItemDecoration(new C18132());
            this.listView.setOnScrollListener(new C18143());
            this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
            this.attachView = new C11204(context);
            viewArr = this.views;
            recyclerListView2 = new RecyclerListView(context);
            this.attachPhotoRecyclerView = recyclerListView2;
            viewArr[8] = recyclerListView2;
            this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
            recyclerListView = this.attachPhotoRecyclerView;
            listAdapter = new PhotoAttachAdapter(context);
            this.photoAttachAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.attachPhotoRecyclerView.setClipToPadding(false);
            this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
            this.attachPhotoRecyclerView.setItemAnimator(null);
            this.attachPhotoRecyclerView.setLayoutAnimation(null);
            this.attachPhotoRecyclerView.setOverScrollMode(2);
            this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
            this.attachPhotoLayoutManager = new C20145(context);
            this.attachPhotoLayoutManager.setOrientation(0);
            this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
            this.attachPhotoRecyclerView.setOnItemClickListener(new C18156());
            viewArr = this.views;
            emptyTextProgressView = new EmptyTextProgressView(context);
            this.progressView = emptyTextProgressView;
            viewArr[9] = emptyTextProgressView;
        } else {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadInlineHints);
            this.shadowDrawable = context.getResources().getDrawable(C0691R.drawable.sheet_shadow);
            c20271 = new C20271(context);
            this.listView = c20271;
            this.containerView = c20271;
            this.listView.setTag(Integer.valueOf(10));
            this.containerView.setWillNotDraw(false);
            this.listView.setClipToPadding(false);
            recyclerListView = this.listView;
            linearLayoutManager = new LinearLayoutManager(getContext());
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.layoutManager.setOrientation(1);
            recyclerListView = this.listView;
            listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setEnabled(true);
            this.listView.setGlowColor(-657673);
            this.listView.addItemDecoration(new C18132());
            this.listView.setOnScrollListener(new C18143());
            this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
            this.attachView = new C11204(context);
            viewArr = this.views;
            recyclerListView2 = new RecyclerListView(context);
            this.attachPhotoRecyclerView = recyclerListView2;
            viewArr[8] = recyclerListView2;
            this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
            recyclerListView = this.attachPhotoRecyclerView;
            listAdapter = new PhotoAttachAdapter(context);
            this.photoAttachAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.attachPhotoRecyclerView.setClipToPadding(false);
            this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
            this.attachPhotoRecyclerView.setItemAnimator(null);
            this.attachPhotoRecyclerView.setLayoutAnimation(null);
            this.attachPhotoRecyclerView.setOverScrollMode(2);
            this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
            this.attachPhotoLayoutManager = new C20145(context);
            this.attachPhotoLayoutManager.setOrientation(0);
            this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
            this.attachPhotoRecyclerView.setOnItemClickListener(new C18156());
            viewArr = this.views;
            emptyTextProgressView = new EmptyTextProgressView(context);
            this.progressView = emptyTextProgressView;
            viewArr[9] = emptyTextProgressView;
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", C0691R.string.NoPhotos));
            this.progressView.setTextSize(20);
        } else {
            this.progressView.setText(LocaleController.getString("PermissionStorage", C0691R.string.PermissionStorage));
            this.progressView.setTextSize(16);
        }
        this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoRecyclerView.setEmptyView(this.progressView);
        viewArr = this.views;
        View c11217 = new C11217(getContext());
        this.lineView = c11217;
        viewArr[10] = c11217;
        this.lineView.setBackgroundColor(-2960686);
        this.attachView.addView(this.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
        CharSequence[] items = new CharSequence[]{LocaleController.getString("ChatCamera", C0691R.string.ChatCamera), LocaleController.getString("ChatGallery", C0691R.string.ChatGallery), LocaleController.getString("ChatVideo", C0691R.string.ChatVideo), LocaleController.getString("AttachMusic", C0691R.string.AttachMusic), LocaleController.getString("ChatDocument", C0691R.string.ChatDocument), LocaleController.getString("AttachContact", C0691R.string.AttachContact), LocaleController.getString("ChatLocation", C0691R.string.ChatLocation), TtmlNode.ANONYMOUS_REGION_ID};
        for (a = 0; a < 8; a++) {
            AttachButton attachButton = new AttachButton(context);
            attachButton.setTextAndIcon(items[a], Theme.attachButtonDrawables[a]);
            this.attachView.addView(attachButton, LayoutHelper.createFrame(85, 90, 51));
            attachButton.setTag(Integer.valueOf(a));
            this.views[a] = attachButton;
            if (a == 7) {
                this.sendPhotosButton = attachButton;
                this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            }
            attachButton.setOnClickListener(new C11228());
        }
        this.hintTextView = new TextView(context);
        this.hintTextView.setBackgroundResource(C0691R.drawable.tooltip);
        this.hintTextView.setTextColor(Theme.CHAT_GIF_HINT_TEXT_COLOR);
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", C0691R.string.AttachBotsHelp));
        this.hintTextView.setGravity(16);
        this.hintTextView.setVisibility(4);
        this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(C0691R.drawable.scroll_tip, 0, 0, 0);
        this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0f, 85, 5.0f, 0.0f, 5.0f, 5.0f));
        for (a = 0; a < 8; a++) {
            this.viewsCache.add(this.photoAttachAdapter.createHolder());
        }
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
    }

    private void hideHint() {
        if (this.hideHintRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideHintRunnable);
            this.hideHintRunnable = null;
        }
        if (this.hintTextView != null) {
            this.currentHintAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentHintAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new C18169());
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    private void showHint() {
        if (!SearchQuery.inlineBots.isEmpty() && !ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("bothint", false)) {
            this.hintShowed = true;
            this.hintTextView.setVisibility(0);
            this.currentHintAnimation = new AnimatorSet();
            this.currentHintAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL})});
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy() {

                /* renamed from: org.telegram.ui.Components.ChatAttachAlert.10.1 */
                class C11191 implements Runnable {
                    C11191() {
                    }

                    public void run() {
                        if (ChatAttachAlert.this.hideHintRunnable == this) {
                            ChatAttachAlert.this.hideHintRunnable = null;
                            ChatAttachAlert.this.hideHint();
                        }
                    }
                }

                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                        AndroidUtilities.runOnUIThread(ChatAttachAlert.this.hideHintRunnable = new C11191(), 2000);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.albumsDidLoaded) {
            if (this.photoAttachAdapter != null) {
                this.loading = false;
                this.progressView.showTextView();
                this.photoAttachAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.reloadInlineHints && this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = this.listView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            recyclerListView = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updatePhotosButton() {
        if (this.photoAttachAdapter.getSelectedPhotos().size() == 0) {
            this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(C0691R.drawable.attach_hide_states);
            this.sendPhotosButton.imageView.setImageResource(C0691R.drawable.attach_hide2);
            this.sendPhotosButton.textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
        } else {
            this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(C0691R.drawable.attach_send_states);
            this.sendPhotosButton.imageView.setImageResource(C0691R.drawable.attach_send2);
            TextView access$4200 = this.sendPhotosButton.textView;
            Object[] objArr = new Object[1];
            objArr[0] = String.format("(%d)", new Object[]{Integer.valueOf(count)});
            access$4200.setText(LocaleController.formatString("SendItems", C0691R.string.SendItems, objArr));
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", C0691R.string.NoPhotos));
            this.progressView.setTextSize(20);
            return;
        }
        this.progressView.setText(LocaleController.getString("PermissionStorage", C0691R.string.PermissionStorage));
        this.progressView.setTextSize(16);
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void loadGalleryPhotos() {
        if (MediaController.allPhotosAlbumEntry == null && VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void init(ChatActivity parentFragment) {
        if (MediaController.allPhotosAlbumEntry != null) {
            for (int a = 0; a < Math.min(100, MediaController.allPhotosAlbumEntry.photos.size()); a++) {
                PhotoEntry photoEntry = (PhotoEntry) MediaController.allPhotosAlbumEntry.photos.get(a);
                photoEntry.caption = null;
                photoEntry.imagePath = null;
                photoEntry.thumbPath = null;
            }
        }
        if (this.currentHintAnimation != null) {
            this.currentHintAnimation.cancel();
            this.currentHintAnimation = null;
        }
        this.hintTextView.setAlpha(0.0f);
        this.hintTextView.setVisibility(4);
        this.attachPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
        this.photoAttachAdapter.clearSelectedPhotos();
        this.baseFragment = parentFragment;
        this.layoutManager.scrollToPositionWithOffset(0, 1000000);
        updatePhotosButton();
    }

    public HashMap<Integer, PhotoEntry> getSelectedPhotos() {
        return this.photoAttachAdapter.getSelectedPhotos();
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadInlineHints);
        this.baseFragment = null;
    }

    private PhotoAttachPhotoCell getCellForIndex(int index) {
        if (MediaController.allPhotosAlbumEntry == null) {
            return null;
        }
        int count = this.attachPhotoRecyclerView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.attachPhotoRecyclerView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                int num = ((Integer) cell.getImageView().getTag()).intValue();
                if (num >= 0 && num < MediaController.allPhotosAlbumEntry.photos.size() && num == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        int i = 0;
        PhotoAttachPhotoCell cell = getCellForIndex(index);
        if (cell == null) {
            return null;
        }
        int i2;
        int[] coords = new int[2];
        cell.getImageView().getLocationInWindow(coords);
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = coords[0];
        int i3 = coords[1];
        if (VERSION.SDK_INT >= 21) {
            i2 = AndroidUtilities.statusBarHeight;
        } else {
            i2 = 0;
        }
        object.viewY = i3 - i2;
        object.parentView = this.attachPhotoRecyclerView;
        object.imageReceiver = cell.getImageView().getImageReceiver();
        object.thumb = object.imageReceiver.getBitmap();
        object.scale = cell.getImageView().getScaleX();
        if (VERSION.SDK_INT < 21) {
            i = -AndroidUtilities.statusBarHeight;
        }
        object.clipBottomAddition = i;
        cell.getCheckBox().setVisibility(8);
        return object;
    }

    public void updatePhotoAtIndex(int index) {
        PhotoAttachPhotoCell cell = getCellForIndex(index);
        if (cell != null) {
            cell.getImageView().setOrientation(0, true);
            PhotoEntry photoEntry = (PhotoEntry) MediaController.allPhotosAlbumEntry.photos.get(index);
            if (photoEntry.thumbPath != null) {
                cell.getImageView().setImage(photoEntry.thumbPath, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
            } else if (photoEntry.path != null) {
                cell.getImageView().setOrientation(photoEntry.orientation, true);
                cell.getImageView().setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(C0691R.drawable.nophotos));
            } else {
                cell.getImageView().setImageResource(C0691R.drawable.nophotos);
            }
        }
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        PhotoAttachPhotoCell cell = getCellForIndex(index);
        if (cell != null) {
            return cell.getImageView().getImageReceiver().getBitmap();
        }
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        PhotoAttachPhotoCell cell = getCellForIndex(index);
        if (cell != null) {
            cell.getCheckBox().setVisibility(0);
        }
    }

    public void willHidePhotoViewer() {
        int count = this.attachPhotoRecyclerView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.attachPhotoRecyclerView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                if (cell.getCheckBox().getVisibility() != 0) {
                    cell.getCheckBox().setVisibility(0);
                }
            }
        }
    }

    public boolean isPhotoChecked(int index) {
        return index >= 0 && index < MediaController.allPhotosAlbumEntry.photos.size() && this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((PhotoEntry) MediaController.allPhotosAlbumEntry.photos.get(index)).imageId));
    }

    public void setPhotoChecked(int index) {
        boolean add = true;
        if (index >= 0 && index < MediaController.allPhotosAlbumEntry.photos.size()) {
            PhotoEntry photoEntry = (PhotoEntry) MediaController.allPhotosAlbumEntry.photos.get(index);
            if (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(photoEntry.imageId))) {
                this.photoAttachAdapter.getSelectedPhotos().remove(Integer.valueOf(photoEntry.imageId));
                add = false;
            } else {
                this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(photoEntry.imageId), photoEntry);
            }
            int count = this.attachPhotoRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.attachPhotoRecyclerView.getChildAt(a);
                if (((Integer) view.getTag()).intValue() == index) {
                    ((PhotoAttachPhotoCell) view).setChecked(add, false);
                    break;
                }
            }
            updatePhotosButton();
        }
    }

    public boolean cancelButtonPressed() {
        return false;
    }

    public void sendButtonPressed(int index) {
        if (this.photoAttachAdapter.getSelectedPhotos().isEmpty()) {
            if (index >= 0 && index < MediaController.allPhotosAlbumEntry.photos.size()) {
                PhotoEntry photoEntry = (PhotoEntry) MediaController.allPhotosAlbumEntry.photos.get(index);
                this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(photoEntry.imageId), photoEntry);
            } else {
                return;
            }
        }
        this.delegate.didPressedButton(7);
    }

    public int getSelectedCount() {
        return this.photoAttachAdapter.getSelectedPhotos().size();
    }

    private void onRevealAnimationEnd(boolean open) {
        NotificationCenter.getInstance().setAnimationInProgress(false);
        this.revealAnimationInProgress = false;
        if (open && VERSION.SDK_INT <= 19 && MediaController.allPhotosAlbumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        if (open) {
            showHint();
        }
    }

    public void onOpenAnimationEnd() {
        onRevealAnimationEnd(true);
    }

    public void onOpenAnimationStart() {
    }

    private void setUseRevealAnimation(boolean value) {
        if (!value || (value && VERSION.SDK_INT >= 18 && !AndroidUtilities.isTablet())) {
            this.useRevealAnimation = value;
        }
    }

    @SuppressLint({"NewApi"})
    protected void setRevealRadius(float radius) {
        this.revealRadius = radius;
        if (VERSION.SDK_INT <= 19) {
            this.containerView.invalidate();
        }
        if (!isDismissed()) {
            int a = 0;
            while (a < this.innerAnimators.size()) {
                InnerAnimator innerAnimator = (InnerAnimator) this.innerAnimators.get(a);
                if (innerAnimator.startRadius <= radius) {
                    innerAnimator.animatorSet.start();
                    this.innerAnimators.remove(a);
                    a--;
                }
                a++;
            }
        }
    }

    protected float getRevealRadius() {
        return this.revealRadius;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    private void startRevealAnimation(boolean r33) {
        /*
        r32 = this;
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = 0;
        r25.setTranslationY(r26);
        r7 = new android.animation.AnimatorSet;
        r7.<init>();
        r0 = r32;
        r0 = r0.delegate;
        r25 = r0;
        r23 = r25.getRevealView();
        r25 = r23.getVisibility();
        if (r25 != 0) goto L_0x0193;
    L_0x0020:
        r25 = r23.getParent();
        r25 = (android.view.ViewGroup) r25;
        r25 = r25.getVisibility();
        if (r25 != 0) goto L_0x0193;
    L_0x002c:
        r25 = 2;
        r0 = r25;
        r12 = new int[r0];
        r0 = r23;
        r0.getLocationInWindow(r12);
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 19;
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x0187;
    L_0x0041:
        r25 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r25;
        r0 = r0.y;
        r25 = r0;
        r0 = r32;
        r0 = r0.containerView;
        r26 = r0;
        r26 = r26.getMeasuredHeight();
        r25 = r25 - r26;
        r26 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r25 = r25 - r26;
        r0 = r25;
        r0 = (float) r0;
        r20 = r0;
    L_0x005e:
        r25 = 0;
        r25 = r12[r25];
        r26 = r23.getMeasuredWidth();
        r26 = r26 / 2;
        r25 = r25 + r26;
        r0 = r25;
        r1 = r32;
        r1.revealX = r0;
        r25 = 1;
        r25 = r12[r25];
        r26 = r23.getMeasuredHeight();
        r26 = r26 / 2;
        r25 = r25 + r26;
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
        r25 = r25 - r20;
        r0 = r25;
        r0 = (int) r0;
        r25 = r0;
        r0 = r25;
        r1 = r32;
        r1.revealY = r0;
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 19;
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x00a8;
    L_0x0098:
        r0 = r32;
        r0 = r0.revealY;
        r25 = r0;
        r26 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r25 = r25 - r26;
        r0 = r25;
        r1 = r32;
        r1.revealY = r0;
    L_0x00a8:
        r25 = 4;
        r0 = r25;
        r13 = new int[r0][];
        r25 = 0;
        r26 = 2;
        r0 = r26;
        r0 = new int[r0];
        r26 = r0;
        r26 = {0, 0};
        r13[r25] = r26;
        r25 = 1;
        r26 = 2;
        r0 = r26;
        r0 = new int[r0];
        r26 = r0;
        r27 = 0;
        r28 = 0;
        r26[r27] = r28;
        r27 = 1;
        r28 = 1134034944; // 0x43980000 float:304.0 double:5.60287707E-315;
        r28 = org.telegram.messenger.AndroidUtilities.dp(r28);
        r26[r27] = r28;
        r13[r25] = r26;
        r25 = 2;
        r26 = 2;
        r0 = r26;
        r0 = new int[r0];
        r26 = r0;
        r27 = 0;
        r0 = r32;
        r0 = r0.containerView;
        r28 = r0;
        r28 = r28.getMeasuredWidth();
        r26[r27] = r28;
        r27 = 1;
        r28 = 0;
        r26[r27] = r28;
        r13[r25] = r26;
        r25 = 3;
        r26 = 2;
        r0 = r26;
        r0 = new int[r0];
        r26 = r0;
        r27 = 0;
        r0 = r32;
        r0 = r0.containerView;
        r28 = r0;
        r28 = r28.getMeasuredWidth();
        r26[r27] = r28;
        r27 = 1;
        r28 = 1134034944; // 0x43980000 float:304.0 double:5.60287707E-315;
        r28 = org.telegram.messenger.AndroidUtilities.dp(r28);
        r26[r27] = r28;
        r13[r25] = r26;
        r17 = 0;
        r0 = r32;
        r0 = r0.revealY;
        r25 = r0;
        r0 = r32;
        r0 = r0.scrollOffsetY;
        r26 = r0;
        r25 = r25 - r26;
        r26 = backgroundPaddingTop;
        r24 = r25 + r26;
        r6 = 0;
    L_0x0132:
        r25 = 4;
        r0 = r25;
        if (r6 >= r0) goto L_0x01cd;
    L_0x0138:
        r0 = r32;
        r0 = r0.revealX;
        r25 = r0;
        r26 = r13[r6];
        r27 = 0;
        r26 = r26[r27];
        r25 = r25 - r26;
        r0 = r32;
        r0 = r0.revealX;
        r26 = r0;
        r27 = r13[r6];
        r28 = 0;
        r27 = r27[r28];
        r26 = r26 - r27;
        r25 = r25 * r26;
        r26 = r13[r6];
        r27 = 1;
        r26 = r26[r27];
        r26 = r24 - r26;
        r27 = r13[r6];
        r28 = 1;
        r27 = r27[r28];
        r27 = r24 - r27;
        r26 = r26 * r27;
        r25 = r25 + r26;
        r0 = r25;
        r0 = (double) r0;
        r26 = r0;
        r26 = java.lang.Math.sqrt(r26);
        r26 = java.lang.Math.ceil(r26);
        r0 = r26;
        r0 = (int) r0;
        r25 = r0;
        r0 = r17;
        r1 = r25;
        r17 = java.lang.Math.max(r0, r1);
        r6 = r6 + 1;
        goto L_0x0132;
    L_0x0187:
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r20 = r25.getY();
        goto L_0x005e;
    L_0x0193:
        r25 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r25;
        r0 = r0.x;
        r25 = r0;
        r25 = r25 / 2;
        r26 = backgroundPaddingLeft;
        r25 = r25 + r26;
        r0 = r25;
        r1 = r32;
        r1.revealX = r0;
        r25 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r25;
        r0 = r0.y;
        r25 = r0;
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
        r0 = r32;
        r0 = r0.containerView;
        r26 = r0;
        r26 = r26.getY();
        r25 = r25 - r26;
        r0 = r25;
        r0 = (int) r0;
        r25 = r0;
        r0 = r25;
        r1 = r32;
        r1.revealY = r0;
        goto L_0x00a8;
    L_0x01cd:
        r0 = r32;
        r0 = r0.revealX;
        r25 = r0;
        r0 = r32;
        r0 = r0.containerView;
        r26 = r0;
        r26 = r26.getMeasuredWidth();
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x0561;
    L_0x01e3:
        r0 = r32;
        r0 = r0.revealX;
        r18 = r0;
    L_0x01e9:
        r9 = new java.util.ArrayList;
        r25 = 3;
        r0 = r25;
        r9.<init>(r0);
        r26 = "revealRadius";
        r25 = 2;
        r0 = r25;
        r0 = new float[r0];
        r27 = r0;
        r28 = 0;
        if (r33 == 0) goto L_0x056d;
    L_0x0200:
        r25 = 0;
    L_0x0202:
        r27[r28] = r25;
        r28 = 1;
        if (r33 == 0) goto L_0x0574;
    L_0x0208:
        r0 = r17;
        r0 = (float) r0;
        r25 = r0;
    L_0x020d:
        r27[r28] = r25;
        r0 = r32;
        r1 = r26;
        r2 = r27;
        r25 = android.animation.ObjectAnimator.ofFloat(r0, r1, r2);
        r0 = r25;
        r9.add(r0);
        r0 = r32;
        r0 = r0.backDrawable;
        r26 = r0;
        r27 = "alpha";
        r25 = 1;
        r0 = r25;
        r0 = new int[r0];
        r28 = r0;
        r29 = 0;
        if (r33 == 0) goto L_0x0578;
    L_0x0232:
        r25 = 51;
    L_0x0234:
        r28[r29] = r25;
        r25 = android.animation.ObjectAnimator.ofInt(r26, r27, r28);
        r0 = r25;
        r9.add(r0);
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 21;
        r0 = r25;
        r1 = r26;
        if (r0 < r1) goto L_0x0595;
    L_0x0249:
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r26 = org.telegram.messenger.AndroidUtilities.dp(r26);
        r0 = r26;
        r0 = (float) r0;
        r26 = r0;
        r25.setElevation(r26);
        r0 = r32;
        r0 = r0.containerView;	 Catch:{ Exception -> 0x0589 }
        r27 = r0;
        r0 = r32;
        r0 = r0.revealY;	 Catch:{ Exception -> 0x0589 }
        r28 = r0;
        if (r33 == 0) goto L_0x057c;
    L_0x026b:
        r25 = 0;
        r26 = r25;
    L_0x026f:
        if (r33 == 0) goto L_0x0585;
    L_0x0271:
        r0 = r17;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0589 }
        r25 = r0;
    L_0x0276:
        r0 = r27;
        r1 = r18;
        r2 = r28;
        r3 = r26;
        r4 = r25;
        r25 = android.view.ViewAnimationUtils.createCircularReveal(r0, r1, r2, r3, r4);	 Catch:{ Exception -> 0x0589 }
        r0 = r25;
        r9.add(r0);	 Catch:{ Exception -> 0x0589 }
    L_0x0289:
        r26 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r0 = r26;
        r7.setDuration(r0);
    L_0x0290:
        r7.playTogether(r9);
        r25 = new org.telegram.ui.Components.ChatAttachAlert$11;
        r0 = r25;
        r1 = r32;
        r2 = r33;
        r0.<init>(r2, r7);
        r0 = r25;
        r7.addListener(r0);
        if (r33 == 0) goto L_0x06b0;
    L_0x02a5:
        r0 = r32;
        r0 = r0.innerAnimators;
        r25 = r0;
        r25.clear();
        r25 = org.telegram.messenger.NotificationCenter.getInstance();
        r26 = 1;
        r0 = r26;
        r0 = new int[r0];
        r26 = r0;
        r27 = 0;
        r28 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r26[r27] = r28;
        r25.setAllowedNotificationsDutingAnimation(r26);
        r25 = org.telegram.messenger.NotificationCenter.getInstance();
        r26 = 1;
        r25.setAnimationInProgress(r26);
        r25 = 1;
        r0 = r25;
        r1 = r32;
        r1.revealAnimationInProgress = r0;
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 19;
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x068b;
    L_0x02de:
        r14 = 11;
    L_0x02e0:
        r6 = 0;
    L_0x02e1:
        if (r6 >= r14) goto L_0x06b0;
    L_0x02e3:
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 19;
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x068f;
    L_0x02ed:
        r25 = 8;
        r0 = r25;
        if (r6 >= r0) goto L_0x030f;
    L_0x02f3:
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = 1036831949; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r25.setScaleX(r26);
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = 1036831949; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r25.setScaleY(r26);
    L_0x030f:
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = 0;
        r25.setAlpha(r26);
    L_0x031c:
        r19 = new org.telegram.ui.Components.ChatAttachAlert$InnerAnimator;
        r25 = 0;
        r0 = r19;
        r1 = r32;
        r2 = r25;
        r0.<init>(r2);
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r25 = r25.getLeft();
        r0 = r32;
        r0 = r0.views;
        r26 = r0;
        r26 = r26[r6];
        r26 = r26.getMeasuredWidth();
        r26 = r26 / 2;
        r10 = r25 + r26;
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r25 = r25.getTop();
        r0 = r32;
        r0 = r0.attachView;
        r26 = r0;
        r26 = r26.getTop();
        r25 = r25 + r26;
        r0 = r32;
        r0 = r0.views;
        r26 = r0;
        r26 = r26[r6];
        r26 = r26.getMeasuredHeight();
        r26 = r26 / 2;
        r11 = r25 + r26;
        r0 = r32;
        r0 = r0.revealX;
        r25 = r0;
        r25 = r25 - r10;
        r0 = r32;
        r0 = r0.revealX;
        r26 = r0;
        r26 = r26 - r10;
        r25 = r25 * r26;
        r0 = r32;
        r0 = r0.revealY;
        r26 = r0;
        r26 = r26 - r11;
        r0 = r32;
        r0 = r0.revealY;
        r27 = r0;
        r27 = r27 - r11;
        r26 = r26 * r27;
        r25 = r25 + r26;
        r0 = r25;
        r0 = (double) r0;
        r26 = r0;
        r26 = java.lang.Math.sqrt(r26);
        r0 = r26;
        r15 = (float) r0;
        r0 = r32;
        r0 = r0.revealX;
        r25 = r0;
        r25 = r25 - r10;
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
        r21 = r25 / r15;
        r0 = r32;
        r0 = r0.revealY;
        r25 = r0;
        r25 = r25 - r11;
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
        r22 = r25 / r15;
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r0 = r32;
        r0 = r0.views;
        r26 = r0;
        r26 = r26[r6];
        r26 = r26.getMeasuredWidth();
        r26 = r26 / 2;
        r0 = r26;
        r0 = (float) r0;
        r26 = r0;
        r27 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r27;
        r0 = (float) r0;
        r27 = r0;
        r27 = r27 * r21;
        r26 = r26 + r27;
        r25.setPivotX(r26);
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r0 = r32;
        r0 = r0.views;
        r26 = r0;
        r26 = r26[r6];
        r26 = r26.getMeasuredHeight();
        r26 = r26 / 2;
        r0 = r26;
        r0 = (float) r0;
        r26 = r0;
        r27 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r27 = org.telegram.messenger.AndroidUtilities.dp(r27);
        r0 = r27;
        r0 = (float) r0;
        r27 = r0;
        r27 = r27 * r22;
        r26 = r26 + r27;
        r25.setPivotY(r26);
        r25 = 1117913088; // 0x42a20000 float:81.0 double:5.52322452E-315;
        r25 = org.telegram.messenger.AndroidUtilities.dp(r25);
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
        r25 = r15 - r25;
        r0 = r19;
        r1 = r25;
        r0.startRadius = r1;
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r27 = 1;
        r27 = java.lang.Integer.valueOf(r27);
        r25.setTag(r26, r27);
        r9 = new java.util.ArrayList;
        r9.<init>();
        r25 = 8;
        r0 = r25;
        if (r6 >= r0) goto L_0x06ad;
    L_0x044a:
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = "scaleX";
        r27 = 2;
        r0 = r27;
        r0 = new float[r0];
        r27 = r0;
        r27 = {1060320051, 1065772646};
        r25 = android.animation.ObjectAnimator.ofFloat(r25, r26, r27);
        r0 = r25;
        r9.add(r0);
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = "scaleY";
        r27 = 2;
        r0 = r27;
        r0 = new float[r0];
        r27 = r0;
        r27 = {1060320051, 1065772646};
        r25 = android.animation.ObjectAnimator.ofFloat(r25, r26, r27);
        r0 = r25;
        r9.add(r0);
        r8 = new android.animation.AnimatorSet;
        r8.<init>();
        r25 = 2;
        r0 = r25;
        r0 = new android.animation.Animator[r0];
        r25 = r0;
        r26 = 0;
        r0 = r32;
        r0 = r0.views;
        r27 = r0;
        r27 = r27[r6];
        r28 = "scaleX";
        r29 = 1;
        r0 = r29;
        r0 = new float[r0];
        r29 = r0;
        r30 = 0;
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r29[r30] = r31;
        r27 = android.animation.ObjectAnimator.ofFloat(r27, r28, r29);
        r25[r26] = r27;
        r26 = 1;
        r0 = r32;
        r0 = r0.views;
        r27 = r0;
        r27 = r27[r6];
        r28 = "scaleY";
        r29 = 1;
        r0 = r29;
        r0 = new float[r0];
        r29 = r0;
        r30 = 0;
        r31 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r29[r30] = r31;
        r27 = android.animation.ObjectAnimator.ofFloat(r27, r28, r29);
        r25[r26] = r27;
        r0 = r25;
        r8.playTogether(r0);
        r26 = 100;
        r0 = r26;
        r8.setDuration(r0);
        r0 = r32;
        r0 = r0.decelerateInterpolator;
        r25 = r0;
        r0 = r25;
        r8.setInterpolator(r0);
    L_0x04ea:
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 19;
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x0515;
    L_0x04f4:
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = "alpha";
        r27 = 1;
        r0 = r27;
        r0 = new float[r0];
        r27 = r0;
        r28 = 0;
        r29 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r27[r28] = r29;
        r25 = android.animation.ObjectAnimator.ofFloat(r25, r26, r27);
        r0 = r25;
        r9.add(r0);
    L_0x0515:
        r25 = new android.animation.AnimatorSet;
        r25.<init>();
        r0 = r19;
        r1 = r25;
        r0.animatorSet = r1;
        r25 = r19.animatorSet;
        r0 = r25;
        r0.playTogether(r9);
        r25 = r19.animatorSet;
        r26 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r25.setDuration(r26);
        r25 = r19.animatorSet;
        r0 = r32;
        r0 = r0.decelerateInterpolator;
        r26 = r0;
        r25.setInterpolator(r26);
        r25 = r19.animatorSet;
        r26 = new org.telegram.ui.Components.ChatAttachAlert$12;
        r0 = r26;
        r1 = r32;
        r0.<init>(r8);
        r25.addListener(r26);
        r0 = r32;
        r0 = r0.innerAnimators;
        r25 = r0;
        r0 = r25;
        r1 = r19;
        r0.add(r1);
        r6 = r6 + 1;
        goto L_0x02e1;
    L_0x0561:
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r18 = r25.getMeasuredWidth();
        goto L_0x01e9;
    L_0x056d:
        r0 = r17;
        r0 = (float) r0;
        r25 = r0;
        goto L_0x0202;
    L_0x0574:
        r25 = 0;
        goto L_0x020d;
    L_0x0578:
        r25 = 0;
        goto L_0x0234;
    L_0x057c:
        r0 = r17;
        r0 = (float) r0;
        r25 = r0;
        r26 = r25;
        goto L_0x026f;
    L_0x0585:
        r25 = 0;
        goto L_0x0276;
    L_0x0589:
        r16 = move-exception;
        r25 = "tmessages";
        r0 = r25;
        r1 = r16;
        org.telegram.messenger.FileLog.m13e(r0, r1);
        goto L_0x0289;
    L_0x0595:
        if (r33 != 0) goto L_0x0650;
    L_0x0597:
        r26 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r26;
        r7.setDuration(r0);
        r0 = r32;
        r0 = r0.containerView;
        r26 = r0;
        r0 = r32;
        r0 = r0.revealX;
        r25 = r0;
        r0 = r32;
        r0 = r0.containerView;
        r27 = r0;
        r27 = r27.getMeasuredWidth();
        r0 = r25;
        r1 = r27;
        if (r0 > r1) goto L_0x063f;
    L_0x05ba:
        r0 = r32;
        r0 = r0.revealX;
        r25 = r0;
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
    L_0x05c5:
        r0 = r26;
        r1 = r25;
        r0.setPivotX(r1);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r0 = r32;
        r0 = r0.revealY;
        r26 = r0;
        r0 = r26;
        r0 = (float) r0;
        r26 = r0;
        r25.setPivotY(r26);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = "scaleX";
        r27 = 1;
        r0 = r27;
        r0 = new float[r0];
        r27 = r0;
        r28 = 0;
        r29 = 0;
        r27[r28] = r29;
        r25 = android.animation.ObjectAnimator.ofFloat(r25, r26, r27);
        r0 = r25;
        r9.add(r0);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = "scaleY";
        r27 = 1;
        r0 = r27;
        r0 = new float[r0];
        r27 = r0;
        r28 = 0;
        r29 = 0;
        r27[r28] = r29;
        r25 = android.animation.ObjectAnimator.ofFloat(r25, r26, r27);
        r0 = r25;
        r9.add(r0);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = "alpha";
        r27 = 1;
        r0 = r27;
        r0 = new float[r0];
        r27 = r0;
        r28 = 0;
        r29 = 0;
        r27[r28] = r29;
        r25 = android.animation.ObjectAnimator.ofFloat(r25, r26, r27);
        r0 = r25;
        r9.add(r0);
        goto L_0x0290;
    L_0x063f:
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r25 = r25.getMeasuredWidth();
        r0 = r25;
        r0 = (float) r0;
        r25 = r0;
        goto L_0x05c5;
    L_0x0650:
        r26 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        r0 = r26;
        r7.setDuration(r0);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r25.setScaleX(r26);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r25.setScaleY(r26);
        r0 = r32;
        r0 = r0.containerView;
        r25 = r0;
        r26 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r25.setAlpha(r26);
        r25 = android.os.Build.VERSION.SDK_INT;
        r26 = 19;
        r0 = r25;
        r1 = r26;
        if (r0 > r1) goto L_0x0290;
    L_0x0682:
        r26 = 20;
        r0 = r26;
        r7.setStartDelay(r0);
        goto L_0x0290;
    L_0x068b:
        r14 = 8;
        goto L_0x02e0;
    L_0x068f:
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = 1060320051; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r25.setScaleX(r26);
        r0 = r32;
        r0 = r0.views;
        r25 = r0;
        r25 = r25[r6];
        r26 = 1060320051; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r25.setScaleY(r26);
        goto L_0x031c;
    L_0x06ad:
        r8 = 0;
        goto L_0x04ea;
    L_0x06b0:
        r0 = r32;
        r0.currentSheetAnimation = r7;
        r7.start();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.startRevealAnimation(boolean):void");
    }

    protected boolean onCustomOpenAnimation() {
        if (!this.useRevealAnimation) {
            return false;
        }
        startRevealAnimation(true);
        return true;
    }

    protected boolean onCustomCloseAnimation() {
        if (!this.useRevealAnimation) {
            return false;
        }
        this.backDrawable.setAlpha(51);
        startRevealAnimation(false);
        return true;
    }
}
