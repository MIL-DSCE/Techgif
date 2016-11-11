package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;

public class BottomSheet extends Dialog {
    protected static int backgroundPaddingLeft;
    protected static int backgroundPaddingTop;
    private AccelerateInterpolator accelerateInterpolator;
    private boolean allowCustomAnimation;
    private boolean applyBottomPadding;
    private boolean applyTopPadding;
    protected ColorDrawable backDrawable;
    protected Paint ciclePaint;
    private ContainerView container;
    protected ViewGroup containerView;
    protected AnimatorSet currentSheetAnimation;
    private View customView;
    private DecelerateInterpolator decelerateInterpolator;
    private BottomSheetDelegateInterface delegate;
    private boolean dismissed;
    private boolean focusable;
    private boolean fullWidth;
    private int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews;
    private CharSequence[] items;
    private WindowInsets lastInsets;
    private int layoutCount;
    private OnClickListener onClickListener;
    private Drawable shadowDrawable;
    private Runnable startAnimationRunnable;
    private int tag;
    private CharSequence title;
    private int touchSlop;
    private boolean useFastDismiss;

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.1 */
    class C09071 implements OnApplyWindowInsetsListener {
        C09071() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            BottomSheet.this.lastInsets = insets;
            v.requestLayout();
            return insets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.2 */
    class C09082 extends FrameLayout {
        C09082(Context x0) {
            super(x0);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.3 */
    class C09093 implements OnTouchListener {
        C09093() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.4 */
    class C09104 implements View.OnClickListener {
        C09104() {
        }

        public void onClick(View v) {
            BottomSheet.this.dismissWithButtonClick(((Integer) v.getTag()).intValue());
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.5 */
    class C09115 implements Runnable {
        C09115() {
        }

        public void run() {
            if (BottomSheet.this.startAnimationRunnable == this) {
                BottomSheet.this.startAnimationRunnable = null;
                BottomSheet.this.startOpenAnimation();
            }
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public BottomSheetCell(Context context, int type) {
            super(context);
            setBackgroundResource(C0691R.drawable.list_selector);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(24, 24, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            if (type == 0) {
                this.textView.setTextColor(-14606047);
                this.textView.setTextSize(1, 16.0f);
                addView(this.textView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
            } else if (type == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(-14606047);
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C0747C.ENCODING_PCM_32BIT));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            this.textView.setText(text);
            if (icon != 0) {
                this.imageView.setImageResource(icon);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
        }
    }

    public interface BottomSheetDelegateInterface {
        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this.bottomSheet = new BottomSheet(context, false);
        }

        public Builder(Context context, boolean needFocus) {
            this.bottomSheet = new BottomSheet(context, needFocus);
        }

        public Builder setItems(CharSequence[] items, OnClickListener onClickListener) {
            this.bottomSheet.items = items;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, OnClickListener onClickListener) {
            this.bottomSheet.items = items;
            this.bottomSheet.itemIcons = icons;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.bottomSheet.title = title;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int tag) {
            this.bottomSheet.tag = tag;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate delegate) {
            this.bottomSheet.setDelegate(delegate);
            return this;
        }

        public Builder setApplyTopPadding(boolean value) {
            this.bottomSheet.applyTopPadding = value;
            return this;
        }

        public Builder setApplyBottomPadding(boolean value) {
            this.bottomSheet.applyBottomPadding = value;
            return this;
        }

        public BottomSheet setUseFullWidth(boolean value) {
            this.bottomSheet.fullWidth = value;
            return this.bottomSheet;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.6 */
    class C17436 extends AnimatorListenerAdapterProxy {
        C17436() {
        }

        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                if (BottomSheet.this.delegate != null) {
                    BottomSheet.this.delegate.onOpenAnimationEnd();
                }
                BottomSheet.this.container.setLayerType(0, null);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.7 */
    class C17447 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$item;

        /* renamed from: org.telegram.ui.ActionBar.BottomSheet.7.1 */
        class C09121 implements Runnable {
            C09121() {
            }

            public void run() {
                try {
                    super.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C17447(int i) {
            this.val$item = i;
        }

        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                if (BottomSheet.this.onClickListener != null) {
                    BottomSheet.this.onClickListener.onClick(BottomSheet.this, this.val$item);
                }
                AndroidUtilities.runOnUIThread(new C09121());
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.BottomSheet.8 */
    class C17458 extends AnimatorListenerAdapterProxy {

        /* renamed from: org.telegram.ui.ActionBar.BottomSheet.8.1 */
        class C09131 implements Runnable {
            C09131() {
            }

            public void run() {
                try {
                    BottomSheet.this.dismissInternal();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        C17458() {
        }

        public void onAnimationEnd(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
                AndroidUtilities.runOnUIThread(new C09131());
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public void onOpenAnimationStart() {
        }

        public void onOpenAnimationEnd() {
        }
    }

    private class ContainerView extends FrameLayout implements NestedScrollingParent {
        private AnimatorSet currentAnimation;
        private boolean maybeStartTracking;
        private NestedScrollingParentHelper nestedScrollingParentHelper;
        private boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker;

        /* renamed from: org.telegram.ui.ActionBar.BottomSheet.ContainerView.1 */
        class C17461 extends AnimatorListenerAdapterProxy {
            C17461() {
            }

            public void onAnimationEnd(Animator animation) {
                if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animation)) {
                    ContainerView.this.currentAnimation = null;
                }
            }
        }

        public ContainerView(Context context) {
            super(context);
            this.velocityTracker = null;
            this.maybeStartTracking = false;
            this.startedTracking = false;
            this.currentAnimation = null;
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            return (BottomSheet.this.dismissed || nestedScrollAxes != 2 || BottomSheet.this.canDismissWithSwipe()) ? false : true;
        }

        public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
            if (!BottomSheet.this.dismissed) {
                cancelCurrentAnimation();
            }
        }

        public void onStopNestedScroll(View target) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
            if (!BottomSheet.this.dismissed) {
                float currentTranslation = BottomSheet.this.containerView.getTranslationY();
                checkDismiss(0.0f, 0.0f);
            }
        }

        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (!BottomSheet.this.dismissed) {
                cancelCurrentAnimation();
                if (dyUnconsumed != 0) {
                    float currentTranslation = BottomSheet.this.containerView.getTranslationY() - ((float) dyUnconsumed);
                    if (currentTranslation < 0.0f) {
                        currentTranslation = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(currentTranslation);
                }
            }
        }

        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            if (!BottomSheet.this.dismissed) {
                cancelCurrentAnimation();
                float currentTranslation = BottomSheet.this.containerView.getTranslationY();
                if (currentTranslation > 0.0f && dy > 0) {
                    currentTranslation -= (float) dy;
                    consumed[1] = dy;
                    if (currentTranslation < 0.0f) {
                        currentTranslation = 0.0f;
                        consumed[1] = (int) (((float) consumed[1]) + 0.0f);
                    }
                    BottomSheet.this.containerView.setTranslationY(currentTranslation);
                }
            }
        }

        public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
            return false;
        }

        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return false;
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float velX, float velY) {
            boolean backAnimation;
            float translationY = BottomSheet.this.containerView.getTranslationY();
            if ((translationY >= AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, false) || (velY >= 3500.0f && Math.abs(velY) >= Math.abs(velX))) && (velY >= 0.0f || Math.abs(velY) < 3500.0f)) {
                backAnimation = false;
            } else {
                backAnimation = true;
            }
            if (backAnimation) {
                this.currentAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.currentAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.currentAnimation.setDuration((long) ((int) ((translationY / AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, false)) * 150.0f)));
                this.currentAnimation.setInterpolator(new DecelerateInterpolator());
                this.currentAnimation.addListener(new C17461());
                this.currentAnimation.start();
                return;
            }
            boolean allowOld = BottomSheet.this.allowCustomAnimation;
            BottomSheet.this.allowCustomAnimation = false;
            BottomSheet.this.useFastDismiss = true;
            BottomSheet.this.dismiss();
            BottomSheet.this.allowCustomAnimation = allowOld;
        }

        private void cancelCurrentAnimation() {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
        }

        public boolean onTouchEvent(MotionEvent ev) {
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking)) {
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                if (this.startedTrackingY < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                if (this.velocityTracker != null) {
                    this.velocityTracker.clear();
                }
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float dx = (float) Math.abs((int) (ev.getX() - ((float) this.startedTrackingX)));
                float dy = (float) (((int) ev.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(ev);
                if (this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= ((float) BottomSheet.this.touchSlop)) {
                    this.startedTrackingY = (int) ev.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    translationY = BottomSheet.this.containerView.getTranslationY() + dy;
                    if (translationY < 0.0f) {
                        translationY = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(translationY);
                    this.startedTrackingY = (int) ev.getY();
                }
            } else if (ev == null || (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                translationY = BottomSheet.this.containerView.getTranslationY();
                if (this.startedTracking || translationY != 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                    this.startedTracking = false;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                }
                if (this.velocityTracker != null) {
                    this.velocityTracker.recycle();
                    this.velocityTracker = null;
                }
            }
            return this.startedTracking;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            boolean isPortrait;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                width -= BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                height -= BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
            }
            setMeasuredDimension(width, height);
            if (width < height) {
                isPortrait = true;
            } else {
                isPortrait = false;
            }
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.fullWidth) {
                    BottomSheet.this.containerView.measure(MeasureSpec.makeMeasureSpec((BottomSheet.backgroundPaddingLeft * 2) + width, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, LinearLayoutManager.INVALID_OFFSET));
                } else {
                    int widthSpec;
                    if (AndroidUtilities.isTablet()) {
                        widthSpec = MeasureSpec.makeMeasureSpec(((int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD)) + (BottomSheet.backgroundPaddingLeft * 2), C0747C.ENCODING_PCM_32BIT);
                    } else {
                        widthSpec = MeasureSpec.makeMeasureSpec(isPortrait ? (BottomSheet.backgroundPaddingLeft * 2) + width : ((int) Math.max(((float) width) * DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, (float) Math.min(AndroidUtilities.dp(480.0f), width))) + (BottomSheet.backgroundPaddingLeft * 2), C0747C.ENCODING_PCM_32BIT);
                    }
                    BottomSheet.this.containerView.measure(widthSpec, MeasureSpec.makeMeasureSpec(height, LinearLayoutManager.INVALID_OFFSET));
                }
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child.getVisibility() == 8 || child == BottomSheet.this.containerView)) {
                    measureChildWithMargins(child, MeasureSpec.makeMeasureSpec(width, C0747C.ENCODING_PCM_32BIT), 0, MeasureSpec.makeMeasureSpec(height, C0747C.ENCODING_PCM_32BIT), 0);
                }
            }
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            BottomSheet.this.layoutCount = BottomSheet.this.layoutCount - 1;
            if (BottomSheet.this.containerView != null) {
                int t = (bottom - top) - BottomSheet.this.containerView.getMeasuredHeight();
                if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                    left += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                    right += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                }
                int l = ((right - left) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                BottomSheet.this.containerView.layout(l, t, BottomSheet.this.containerView.getMeasuredWidth() + l, BottomSheet.this.containerView.getMeasuredHeight() + t);
            }
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (!(child.getVisibility() == 8 || child == BottomSheet.this.containerView)) {
                    int childLeft;
                    int childTop;
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    switch ((gravity & 7) & 7) {
                        case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                            childLeft = ((((right - left) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            childLeft = (right - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case ItemTouchHelper.START /*16*/:
                            childTop = ((((bottom - top) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            childTop = lp.topMargin;
                            break;
                        case 80:
                            childTop = ((bottom - top) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                BottomSheet.this.startAnimationRunnable = null;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return onTouchEvent(event);
            }
            return super.onInterceptTouchEvent(event);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public BottomSheet(Context context, boolean needFocus) {
        super(context, C0691R.style.TransparentDialog);
        this.backDrawable = new ColorDrawable(ViewCompat.MEASURED_STATE_MASK);
        this.allowCustomAnimation = true;
        this.ciclePaint = new Paint(1);
        this.applyTopPadding = true;
        this.applyBottomPadding = true;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.accelerateInterpolator = new AccelerateInterpolator();
        this.itemViews = new ArrayList();
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-2147417856);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect padding = new Rect();
        this.shadowDrawable = context.getResources().getDrawable(C0691R.drawable.sheet_shadow);
        this.shadowDrawable.getPadding(padding);
        backgroundPaddingLeft = padding.left;
        backgroundPaddingTop = padding.top;
        this.container = new ContainerView(getContext());
        this.container.setBackgroundDrawable(this.backDrawable);
        this.focusable = needFocus;
        if (VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new C09071());
            this.container.setSystemUiVisibility(1280);
        }
        this.ciclePaint.setColor(-1);
        this.backDrawable.setAlpha(0);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setWindowAnimations(C0691R.style.DialogNoAnimation);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        if (this.containerView == null) {
            int dp;
            this.containerView = new C09082(getContext());
            this.containerView.setBackgroundDrawable(this.shadowDrawable);
            ViewGroup viewGroup = this.containerView;
            int i = backgroundPaddingLeft;
            int dp2 = backgroundPaddingTop + (this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0);
            int i2 = backgroundPaddingLeft;
            if (this.applyBottomPadding) {
                dp = AndroidUtilities.dp(8.0f);
            } else {
                dp = 0;
            }
            viewGroup.setPadding(i, dp2, i2, dp);
        }
        if (VERSION.SDK_INT >= 21) {
            this.containerView.setFitsSystemWindows(true);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        if (this.customView != null) {
            if (this.customView.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
        } else {
            int topOffset = 0;
            if (this.title != null) {
                TextView titleView = new TextView(getContext());
                titleView.setLines(1);
                titleView.setSingleLine(true);
                titleView.setText(this.title);
                titleView.setTextColor(-9079435);
                titleView.setTextSize(1, 16.0f);
                titleView.setEllipsize(TruncateAt.MIDDLE);
                titleView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
                titleView.setGravity(16);
                this.containerView.addView(titleView, LayoutHelper.createFrame(-1, 48.0f));
                titleView.setOnTouchListener(new C09093());
                topOffset = 0 + 48;
            }
            if (this.items != null) {
                int a = 0;
                while (a < this.items.length) {
                    BottomSheetCell cell = new BottomSheetCell(getContext(), 0);
                    cell.setTextAndIcon(this.items[a], this.itemIcons != null ? this.itemIcons[a] : 0);
                    this.containerView.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) topOffset, 0.0f, 0.0f));
                    topOffset += 48;
                    cell.setTag(Integer.valueOf(a));
                    cell.setOnClickListener(new C09104());
                    this.itemViews.add(cell);
                    a++;
                }
            }
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        if (!this.focusable) {
            params.flags |= AccessibilityNodeInfoCompat.ACTION_SET_SELECTION;
        }
        params.height = -1;
        window.setAttributes(params);
    }

    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        this.dismissed = false;
        cancelSheetAnimation();
        if (this.containerView.getMeasuredHeight() == 0) {
            this.containerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, LinearLayoutManager.INVALID_OFFSET));
        }
        this.backDrawable.setAlpha(0);
        if (VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            Runnable c09115 = new C09115();
            this.startAnimationRunnable = c09115;
            AndroidUtilities.runOnUIThread(c09115, 150);
            return;
        }
        startOpenAnimation();
    }

    protected boolean canDismissWithSwipe() {
        return true;
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence value) {
        this.title = value;
    }

    public void setApplyTopPadding(boolean value) {
        this.applyTopPadding = value;
    }

    public void setApplyBottomPadding(boolean value) {
        this.applyBottomPadding = value;
    }

    private void cancelSheetAnimation() {
        if (this.currentSheetAnimation != null) {
            this.currentSheetAnimation.cancel();
            this.currentSheetAnimation = null;
        }
    }

    private void startOpenAnimation() {
        this.containerView.setVisibility(0);
        if (!onCustomOpenAnimation()) {
            if (VERSION.SDK_INT >= 20) {
                this.container.setLayerType(2, null);
            }
            this.containerView.setTranslationY((float) this.containerView.getMeasuredHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{0.0f});
            r1[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{51});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(200);
            animatorSet.setStartDelay(20);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new C17436());
            animatorSet.start();
            this.currentSheetAnimation = animatorSet;
        }
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegate) {
        this.delegate = bottomSheetDelegate;
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getTag() {
        return this.tag;
    }

    public void setItemText(int item, CharSequence text) {
        if (item >= 0 && item < this.itemViews.size()) {
            ((BottomSheetCell) this.itemViews.get(item)).textView.setText(text);
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(int item) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
            r1[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.addListener(new C17447(item));
            animatorSet.start();
            this.currentSheetAnimation = animatorSet;
        }
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                AnimatorSet animatorSet = new AnimatorSet();
                r2 = new Animator[2];
                r2[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                r2[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
                animatorSet.playTogether(r2);
                if (this.useFastDismiss) {
                    int height = this.containerView.getMeasuredHeight();
                    animatorSet.setDuration((long) Math.max(60, (int) ((BitmapDescriptorFactory.HUE_CYAN * (((float) height) - this.containerView.getTranslationY())) / ((float) height))));
                    this.useFastDismiss = false;
                } else {
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new AccelerateInterpolator());
                animatorSet.addListener(new C17458());
                animatorSet.start();
                this.currentSheetAnimation = animatorSet;
            }
        }
    }

    protected void dismissInternal() {
        super.dismiss();
    }

    protected boolean onCustomCloseAnimation() {
        return false;
    }

    protected boolean onCustomOpenAnimation() {
        return false;
    }
}
