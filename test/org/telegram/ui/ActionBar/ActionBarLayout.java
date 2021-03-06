package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ActionBarLayout extends FrameLayout {
    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator;
    protected boolean animationInProgress;
    private float animationProgress;
    private Runnable animationRunnable;
    private View backgroundView;
    private boolean beginTrackingSent;
    private LinearLayoutContainer containerView;
    private LinearLayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    private DecelerateInterpolator decelerateInterpolator;
    private Runnable delayedOpenAnimationRunnable;
    private ActionBarLayoutDelegate delegate;
    private DrawerLayoutContainer drawerLayoutContainer;
    public ArrayList<BaseFragment> fragmentsStack;
    private boolean inActionMode;
    public float innerTranslationX;
    private long lastFrameTime;
    private boolean maybeStartTracking;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onOpenAnimationEndRunnable;
    protected Activity parentActivity;
    private boolean removeActionBarExtraHeight;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private String titleOverlayText;
    private boolean transitionAnimationInProgress;
    private long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    private Runnable waitingForKeyboardCloseRunnable;

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ BaseFragment val$currentFragment;

        AnonymousClass12(BaseFragment baseFragment) {
            this.val$currentFragment = baseFragment;
        }

        public void run() {
            ActionBarLayout.this.removeFragmentFromStackInternal(this.val$currentFragment);
            ActionBarLayout.this.setVisibility(8);
            if (ActionBarLayout.this.backgroundView != null) {
                ActionBarLayout.this.backgroundView.setVisibility(8);
            }
            if (ActionBarLayout.this.drawerLayoutContainer != null) {
                ActionBarLayout.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.2 */
    class C08862 implements Runnable {
        final /* synthetic */ boolean val$first;
        final /* synthetic */ boolean val$open;

        C08862(boolean z, boolean z2) {
            this.val$first = z;
            this.val$open = z2;
        }

        public void run() {
            if (ActionBarLayout.this.animationRunnable == this) {
                ActionBarLayout.this.animationRunnable = null;
                if (this.val$first) {
                    ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                }
                long newTime = System.nanoTime() / C0747C.MICROS_PER_SECOND;
                long dt = newTime - ActionBarLayout.this.lastFrameTime;
                if (dt > 18) {
                    dt = 18;
                }
                ActionBarLayout.this.lastFrameTime = newTime;
                ActionBarLayout.access$816(ActionBarLayout.this, ((float) dt) / 150.0f);
                if (ActionBarLayout.this.animationProgress > TouchHelperCallback.ALPHA_FULL) {
                    ActionBarLayout.this.animationProgress = TouchHelperCallback.ALPHA_FULL;
                }
                float interpolated = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                if (this.val$open) {
                    ActionBarLayout.this.containerView.setAlpha(interpolated);
                    ActionBarLayout.this.containerView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * (TouchHelperCallback.ALPHA_FULL - interpolated));
                } else {
                    ActionBarLayout.this.containerViewBack.setAlpha(TouchHelperCallback.ALPHA_FULL - interpolated);
                    ActionBarLayout.this.containerViewBack.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * interpolated);
                }
                if (ActionBarLayout.this.animationProgress < TouchHelperCallback.ALPHA_FULL) {
                    ActionBarLayout.this.startLayoutAnimation(this.val$open, false);
                } else {
                    ActionBarLayout.this.onAnimationEndCheck(false);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.3 */
    class C08873 implements Runnable {
        final /* synthetic */ BaseFragment val$fragment;

        C08873(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run() {
            this.val$fragment.onTransitionAnimationEnd(true, false);
            this.val$fragment.onBecomeFullyVisible();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.5 */
    class C08885 implements Runnable {
        final /* synthetic */ BaseFragment val$currentFragment;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ boolean val$removeLast;

        C08885(boolean z, BaseFragment baseFragment, BaseFragment baseFragment2) {
            this.val$removeLast = z;
            this.val$currentFragment = baseFragment;
            this.val$fragment = baseFragment2;
        }

        public void run() {
            if (VERSION.SDK_INT > 15) {
                ActionBarLayout.this.containerView.setLayerType(0, null);
                ActionBarLayout.this.containerViewBack.setLayerType(0, null);
            }
            ActionBarLayout.this.presentFragmentInternalRemoveOld(this.val$removeLast, this.val$currentFragment);
            this.val$fragment.onTransitionAnimationEnd(true, false);
            this.val$fragment.onBecomeFullyVisible();
            ActionBarLayout.this.containerView.setTranslationX(0.0f);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.6 */
    class C08896 implements Runnable {
        C08896() {
        }

        public void run() {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.7 */
    class C08907 implements Runnable {
        C08907() {
        }

        public void run() {
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.8 */
    class C08918 implements Runnable {
        C08918() {
        }

        public void run() {
            if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.9 */
    class C08929 implements Runnable {
        final /* synthetic */ BaseFragment val$currentFragment;
        final /* synthetic */ BaseFragment val$previousFragmentFinal;

        C08929(BaseFragment baseFragment, BaseFragment baseFragment2) {
            this.val$currentFragment = baseFragment;
            this.val$previousFragmentFinal = baseFragment2;
        }

        public void run() {
            if (VERSION.SDK_INT > 15) {
                ActionBarLayout.this.containerView.setLayerType(0, null);
                ActionBarLayout.this.containerViewBack.setLayerType(0, null);
            }
            ActionBarLayout.this.closeLastFragmentInternalRemoveOld(this.val$currentFragment);
            ActionBarLayout.this.containerViewBack.setTranslationX(0.0f);
            this.val$currentFragment.onTransitionAnimationEnd(false, false);
            this.val$previousFragmentFinal.onTransitionAnimationEnd(true, true);
            this.val$previousFragmentFinal.onBecomeFullyVisible();
        }
    }

    public interface ActionBarLayoutDelegate {
        boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout);

        boolean needCloseLastFragment(ActionBarLayout actionBarLayout);

        boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout);

        boolean onPreIme();

        void onRebuildAllFragments(ActionBarLayout actionBarLayout);
    }

    public class LinearLayoutContainer extends LinearLayout {
        private boolean isKeyboardVisible;
        private Rect rect;

        public LinearLayoutContainer(Context context) {
            super(context);
            this.rect = new Rect();
            setOrientation(1);
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof ActionBar) {
                return super.drawChild(canvas, child, drawingTime);
            }
            boolean result;
            int actionBarHeight = 0;
            int childCount = getChildCount();
            for (int a = 0; a < childCount; a++) {
                View view = getChildAt(a);
                if (view != child && (view instanceof ActionBar) && view.getVisibility() == 0) {
                    if (((ActionBar) view).getCastShadows()) {
                        actionBarHeight = view.getMeasuredHeight();
                    }
                    result = super.drawChild(canvas, child, drawingTime);
                    if (actionBarHeight == 0 && ActionBarLayout.headerShadowDrawable != null) {
                        ActionBarLayout.headerShadowDrawable.setBounds(0, actionBarHeight, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + actionBarHeight);
                        ActionBarLayout.headerShadowDrawable.draw(canvas);
                        return result;
                    }
                }
            }
            result = super.drawChild(canvas, child, drawingTime);
            return actionBarHeight == 0 ? result : result;
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int i;
            boolean z = false;
            super.onLayout(changed, l, t, r, b);
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            int height = rootView.getHeight();
            if (this.rect.top != 0) {
                i = AndroidUtilities.statusBarHeight;
            } else {
                i = 0;
            }
            if (((height - i) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top) > 0) {
                z = true;
            }
            this.isKeyboardVisible = z;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.1 */
    class C17401 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$backAnimation;

        C17401(boolean z) {
            this.val$backAnimation = z;
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarLayout.this.onSlideAnimationEnd(this.val$backAnimation);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarLayout.4 */
    class C17414 extends AnimatorListenerAdapterProxy {
        C17414() {
        }

        public void onAnimationEnd(Animator animation) {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    static /* synthetic */ float access$816(ActionBarLayout x0, float x1) {
        float f = x0.animationProgress + x1;
        x0.animationProgress = f;
        return f;
    }

    public ActionBarLayout(Context context) {
        super(context);
        this.decelerateInterpolator = new DecelerateInterpolator(1.5f);
        this.accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        this.animationProgress = 0.0f;
        this.delegate = null;
        this.parentActivity = null;
        this.fragmentsStack = null;
        this.parentActivity = (Activity) context;
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(C0691R.drawable.layer_shadow);
            headerShadowDrawable = getResources().getDrawable(C0691R.drawable.header_shadow);
            scrimPaint = new Paint();
        }
    }

    public void init(ArrayList<BaseFragment> stack) {
        this.fragmentsStack = stack;
        this.containerViewBack = new LinearLayoutContainer(this.parentActivity);
        addView(this.containerViewBack);
        LayoutParams layoutParams = (LayoutParams) this.containerViewBack.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerViewBack.setLayoutParams(layoutParams);
        this.containerView = new LinearLayoutContainer(this.parentActivity);
        addView(this.containerView);
        layoutParams = (LayoutParams) this.containerView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerView.setLayoutParams(layoutParams);
        Iterator i$ = this.fragmentsStack.iterator();
        while (i$.hasNext()) {
            ((BaseFragment) i$.next()).setParentLayout(this);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onConfigurationChanged(newConfig);
        }
    }

    public void drawHeaderShadow(Canvas canvas, int y) {
        if (headerShadowDrawable != null) {
            headerShadowDrawable.setBounds(0, y, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + y);
            headerShadowDrawable.draw(canvas);
        }
    }

    public void setInnerTranslationX(float value) {
        this.innerTranslationX = value;
        invalidate();
    }

    public float getInnerTranslationX() {
        return this.innerTranslationX;
    }

    public void onResume() {
        if (this.transitionAnimationInProgress) {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd(false);
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd(false);
            }
        }
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onResume();
        }
    }

    public void onPause() {
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onPause();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.animationInProgress || checkTransitionAnimation() || onTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event == null || event.getKeyCode() != 4 || event.getAction() != 1) {
            return super.dispatchKeyEventPreIme(event);
        }
        if ((this.delegate == null || !this.delegate.onPreIme()) && !super.dispatchKeyEventPreIme(event)) {
            return false;
        }
        return true;
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int translationX = ((int) this.innerTranslationX) + getPaddingRight();
        int clipLeft = getPaddingLeft();
        int clipRight = width + getPaddingLeft();
        if (child == this.containerViewBack) {
            clipRight = translationX;
        } else if (child == this.containerView) {
            clipLeft = translationX;
        }
        int restoreCount = canvas.save();
        if (!this.transitionAnimationInProgress) {
            canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);
        if (translationX != 0) {
            if (child == this.containerView) {
                float alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), TouchHelperCallback.ALPHA_FULL));
                layerShadowDrawable.setBounds(translationX - layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                layerShadowDrawable.draw(canvas);
            } else if (child == this.containerViewBack) {
                float opacity = Math.min(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, ((float) (width - translationX)) / ((float) width));
                if (opacity < 0.0f) {
                    opacity = 0.0f;
                }
                scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), scrimPaint);
            }
        }
        return result;
    }

    public void setDelegate(ActionBarLayoutDelegate delegate) {
        this.delegate = delegate;
    }

    private void onSlideAnimationEnd(boolean backAnimation) {
        BaseFragment lastFragment;
        if (backAnimation) {
            ViewGroup parent;
            lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
            lastFragment.onPause();
            if (lastFragment.fragmentView != null) {
                parent = (ViewGroup) lastFragment.fragmentView.getParent();
                if (parent != null) {
                    parent.removeView(lastFragment.fragmentView);
                }
            }
            if (lastFragment.actionBar != null && lastFragment.actionBar.getAddToContainer()) {
                parent = (ViewGroup) lastFragment.actionBar.getParent();
                if (parent != null) {
                    parent.removeView(lastFragment.actionBar);
                }
            }
        } else {
            lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            lastFragment.onPause();
            lastFragment.onFragmentDestroy();
            lastFragment.setParentLayout(null);
            this.fragmentsStack.remove(this.fragmentsStack.size() - 1);
            LinearLayoutContainer temp = this.containerView;
            this.containerView = this.containerViewBack;
            this.containerViewBack = temp;
            bringChildToFront(this.containerView);
            lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            this.currentActionBar = lastFragment.actionBar;
            lastFragment.onResume();
            lastFragment.onBecomeFullyVisible();
        }
        this.containerViewBack.setVisibility(8);
        this.startedTracking = false;
        this.animationInProgress = false;
        this.containerView.setTranslationX(0.0f);
        this.containerViewBack.setTranslationX(0.0f);
        setInnerTranslationX(0.0f);
    }

    private void prepareForMoving(MotionEvent ev) {
        ViewGroup parent;
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) ev.getX();
        this.containerViewBack.setVisibility(0);
        this.beginTrackingSent = false;
        BaseFragment lastFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
        View fragmentView = lastFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(this.parentActivity);
        } else {
            parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            parent.removeView(fragmentView);
        }
        if (lastFragment.actionBar != null && lastFragment.actionBar.getAddToContainer()) {
            parent = (ViewGroup) lastFragment.actionBar.getParent();
            if (parent != null) {
                parent.removeView(lastFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                lastFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(lastFragment.actionBar);
            lastFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
        }
        this.containerViewBack.addView(fragmentView);
        ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        fragmentView.setLayoutParams(layoutParams);
        if (!lastFragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(-1);
        }
        lastFragment.onResume();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            if (ev == null || ev.getAction() != 0 || this.startedTracking || this.maybeStartTracking) {
                if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int dx = Math.max(0, (int) (ev.getX() - ((float) this.startedTrackingX)));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.maybeStartTracking && !this.startedTracking && ((float) dx) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                        prepareForMoving(ev);
                    } else if (this.startedTracking) {
                        if (!this.beginTrackingSent) {
                            if (this.parentActivity.getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                            }
                            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBeginSlide();
                            this.beginTrackingSent = true;
                        }
                        this.containerView.setTranslationX((float) dx);
                        setInnerTranslationX((float) dx);
                    }
                } else if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                    float velX;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (!this.startedTracking && ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                        velX = this.velocityTracker.getXVelocity();
                        float velY = this.velocityTracker.getYVelocity();
                        if (velX >= 3500.0f && velX > Math.abs(velY)) {
                            prepareForMoving(ev);
                            if (!this.beginTrackingSent) {
                                if (((Activity) getContext()).getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                }
                                this.beginTrackingSent = true;
                            }
                        }
                    }
                    if (this.startedTracking) {
                        float distToMove;
                        float x = this.containerView.getX();
                        AnimatorSet animatorSet = new AnimatorSet();
                        velX = this.velocityTracker.getXVelocity();
                        boolean backAnimation = x < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (velX < 3500.0f || velX < this.velocityTracker.getYVelocity());
                        Animator[] animatorArr;
                        if (backAnimation) {
                            distToMove = x;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            distToMove = ((float) this.containerView.getMeasuredWidth()) - x;
                            animatorArr = new Animator[2];
                            float[] fArr = new float[1];
                            fArr[0] = (float) this.containerView.getMeasuredWidth();
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", fArr);
                            float[] fArr2 = new float[1];
                            fArr2[0] = (float) this.containerView.getMeasuredWidth();
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", fArr2);
                            animatorSet.playTogether(animatorArr);
                        }
                        animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.containerView.getMeasuredWidth())) * distToMove), 50));
                        animatorSet.addListener(new C17401(backAnimation));
                        animatorSet.start();
                        this.animationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                    }
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                } else if (ev == null) {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
            } else if (!((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                return false;
            } else {
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                if (this.velocityTracker != null) {
                    this.velocityTracker.clear();
                }
            }
        }
        return this.startedTracking;
    }

    public void onBackPressed() {
        if (!this.startedTracking && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            if (this.currentActionBar != null && this.currentActionBar.isSearchFieldVisible) {
                this.currentActionBar.closeSearchField();
            } else if (((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBackPressed() && !this.fragmentsStack.isEmpty()) {
                closeLastFragment(true);
            }
        }
    }

    public void onLowMemory() {
        Iterator i$ = this.fragmentsStack.iterator();
        while (i$.hasNext()) {
            ((BaseFragment) i$.next()).onLowMemory();
        }
    }

    private void onAnimationEndCheck(boolean byCheck) {
        onCloseAnimationEnd(false);
        onOpenAnimationEnd(false);
        if (this.waitingForKeyboardCloseRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.waitingForKeyboardCloseRunnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        if (this.currentAnimation != null) {
            if (byCheck) {
                this.currentAnimation.cancel();
            }
            this.currentAnimation = null;
        }
        if (this.animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animationRunnable = null;
        }
        setAlpha(TouchHelperCallback.ALPHA_FULL);
        this.containerView.setAlpha(TouchHelperCallback.ALPHA_FULL);
        this.containerView.setScaleX(TouchHelperCallback.ALPHA_FULL);
        this.containerView.setScaleY(TouchHelperCallback.ALPHA_FULL);
        this.containerViewBack.setAlpha(TouchHelperCallback.ALPHA_FULL);
        this.containerViewBack.setScaleX(TouchHelperCallback.ALPHA_FULL);
        this.containerViewBack.setScaleY(TouchHelperCallback.ALPHA_FULL);
    }

    public boolean checkTransitionAnimation() {
        if (this.transitionAnimationInProgress && this.transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return this.transitionAnimationInProgress;
    }

    private void presentFragmentInternalRemoveOld(boolean removeLast, BaseFragment fragment) {
        if (fragment != null) {
            fragment.onPause();
            if (removeLast) {
                fragment.onFragmentDestroy();
                fragment.setParentLayout(null);
                this.fragmentsStack.remove(fragment);
            } else {
                ViewGroup parent;
                if (fragment.fragmentView != null) {
                    parent = (ViewGroup) fragment.fragmentView.getParent();
                    if (parent != null) {
                        parent.removeView(fragment.fragmentView);
                    }
                }
                if (fragment.actionBar != null && fragment.actionBar.getAddToContainer()) {
                    parent = (ViewGroup) fragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(fragment.actionBar);
                    }
                }
            }
            this.containerViewBack.setVisibility(8);
        }
    }

    public boolean presentFragment(BaseFragment fragment) {
        return presentFragment(fragment, false, false, true);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        return presentFragment(fragment, removeLast, false, true);
    }

    private void startLayoutAnimation(boolean open, boolean first) {
        if (first) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / C0747C.MICROS_PER_SECOND;
            if (VERSION.SDK_INT > 15) {
                this.containerView.setLayerType(2, null);
                this.containerViewBack.setLayerType(2, null);
            }
        }
        Runnable c08862 = new C08862(first, open);
        this.animationRunnable = c08862;
        AndroidUtilities.runOnUIThread(c08862);
    }

    public void resumeDelayedFragmentAnimation() {
        if (this.delayedOpenAnimationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.delayedOpenAnimationRunnable);
            this.delayedOpenAnimationRunnable.run();
            this.delayedOpenAnimationRunnable = null;
        }
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, boolean check) {
        if (checkTransitionAnimation() || ((this.delegate != null && check && !this.delegate.needPresentFragment(fragment, removeLast, forceWithoutAnimation, this)) || !fragment.onFragmentCreate())) {
            return false;
        }
        ViewGroup parent;
        if (this.parentActivity.getCurrentFocus() != null) {
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
        boolean needAnimation = !forceWithoutAnimation && this.parentActivity.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true);
        BaseFragment currentFragment = !this.fragmentsStack.isEmpty() ? (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1) : null;
        fragment.setParentLayout(this);
        View fragmentView = fragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = fragment.createView(this.parentActivity);
        } else {
            parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        if (fragment.actionBar != null && fragment.actionBar.getAddToContainer()) {
            if (this.removeActionBarExtraHeight) {
                fragment.actionBar.setOccupyStatusBar(false);
            }
            parent = (ViewGroup) fragment.actionBar.getParent();
            if (parent != null) {
                parent.removeView(fragment.actionBar);
            }
            this.containerViewBack.addView(fragment.actionBar);
            fragment.actionBar.setTitleOverlayText(this.titleOverlayText);
        }
        this.containerViewBack.addView(fragmentView);
        ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        fragmentView.setLayoutParams(layoutParams);
        this.fragmentsStack.add(fragment);
        fragment.onResume();
        this.currentActionBar = fragment.actionBar;
        if (!fragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(-1);
        }
        LinearLayoutContainer temp = this.containerView;
        this.containerView = this.containerViewBack;
        this.containerViewBack = temp;
        this.containerView.setVisibility(0);
        setInnerTranslationX(0.0f);
        bringChildToFront(this.containerView);
        if (!needAnimation) {
            presentFragmentInternalRemoveOld(removeLast, currentFragment);
            if (this.backgroundView != null) {
                this.backgroundView.setVisibility(0);
            }
        }
        if (!needAnimation) {
            if (this.backgroundView != null) {
                this.backgroundView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                this.backgroundView.setVisibility(0);
            }
            fragment.onTransitionAnimationStart(true, false);
            fragment.onTransitionAnimationEnd(true, false);
            fragment.onBecomeFullyVisible();
        } else if (this.useAlphaAnimations && this.fragmentsStack.size() == 1) {
            presentFragmentInternalRemoveOld(removeLast, currentFragment);
            this.transitionAnimationStartTime = System.currentTimeMillis();
            this.transitionAnimationInProgress = true;
            this.onOpenAnimationEndRunnable = new C08873(fragment);
            ArrayList<Animator> animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL}));
            if (this.backgroundView != null) {
                this.backgroundView.setVisibility(0);
                animators.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL}));
            }
            fragment.onTransitionAnimationStart(true, false);
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.playTogether(animators);
            this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
            this.currentAnimation.setDuration(200);
            this.currentAnimation.addListener(new C17414());
            this.currentAnimation.start();
        } else {
            this.transitionAnimationStartTime = System.currentTimeMillis();
            this.transitionAnimationInProgress = true;
            this.onOpenAnimationEndRunnable = new C08885(removeLast, currentFragment, fragment);
            fragment.onTransitionAnimationStart(true, false);
            AnimatorSet animation = fragment.onCustomTransitionAnimation(true, new C08896());
            if (animation == null) {
                this.containerView.setAlpha(0.0f);
                this.containerView.setTranslationX(48.0f);
                if (this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) {
                    this.waitingForKeyboardCloseRunnable = new C08907();
                    AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                } else if (fragment.needDelayOpenAnimation()) {
                    this.delayedOpenAnimationRunnable = new C08918();
                    AndroidUtilities.runOnUIThread(this.delayedOpenAnimationRunnable, 200);
                } else {
                    startLayoutAnimation(true, true);
                }
            } else if (VERSION.SDK_INT > 15) {
                this.containerView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                this.containerView.setTranslationX(0.0f);
                this.currentAnimation = animation;
            } else {
                this.containerView.setAlpha(TouchHelperCallback.ALPHA_FULL);
                this.containerView.setTranslationX(0.0f);
                this.currentAnimation = animation;
            }
        }
        return true;
    }

    public boolean addFragmentToStack(BaseFragment fragment) {
        return addFragmentToStack(fragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment fragment, int position) {
        if ((this.delegate != null && !this.delegate.needAddFragmentToStack(fragment, this)) || !fragment.onFragmentCreate()) {
            return false;
        }
        fragment.setParentLayout(this);
        if (position == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ViewGroup parent;
                BaseFragment previousFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
                previousFragment.onPause();
                if (previousFragment.actionBar != null) {
                    parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                }
                if (previousFragment.fragmentView != null) {
                    parent = (ViewGroup) previousFragment.fragmentView.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.fragmentView);
                    }
                }
            }
            this.fragmentsStack.add(fragment);
        } else {
            this.fragmentsStack.add(position, fragment);
        }
        return true;
    }

    private void closeLastFragmentInternalRemoveOld(BaseFragment fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        this.fragmentsStack.remove(fragment);
        this.containerViewBack.setVisibility(8);
        bringChildToFront(this.containerView);
    }

    public void closeLastFragment(boolean animated) {
        if ((this.delegate == null || this.delegate.needCloseLastFragment(this)) && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            if (this.parentActivity.getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
            }
            setInnerTranslationX(0.0f);
            boolean needAnimation = animated && this.parentActivity.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true);
            BaseFragment currentFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            BaseFragment previousFragment = null;
            if (this.fragmentsStack.size() > 1) {
                previousFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
            }
            if (previousFragment != null) {
                ViewGroup parent;
                LinearLayoutContainer temp = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = temp;
                this.containerView.setVisibility(0);
                previousFragment.setParentLayout(this);
                View fragmentView = previousFragment.fragmentView;
                if (fragmentView == null) {
                    fragmentView = previousFragment.createView(this.parentActivity);
                } else {
                    parent = (ViewGroup) fragmentView.getParent();
                    if (parent != null) {
                        parent.removeView(fragmentView);
                    }
                }
                if (previousFragment.actionBar != null && previousFragment.actionBar.getAddToContainer()) {
                    if (this.removeActionBarExtraHeight) {
                        previousFragment.actionBar.setOccupyStatusBar(false);
                    }
                    parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                    this.containerView.addView(previousFragment.actionBar);
                    previousFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
                }
                this.containerView.addView(fragmentView);
                ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                fragmentView.setLayoutParams(layoutParams);
                previousFragment.onTransitionAnimationStart(true, true);
                currentFragment.onTransitionAnimationStart(false, false);
                previousFragment.onResume();
                this.currentActionBar = previousFragment.actionBar;
                if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                    fragmentView.setBackgroundColor(-1);
                }
                if (!needAnimation) {
                    closeLastFragmentInternalRemoveOld(currentFragment);
                }
                if (needAnimation) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onCloseAnimationEndRunnable = new C08929(currentFragment, previousFragment);
                    AnimatorSet animation = currentFragment.onCustomTransitionAnimation(false, new Runnable() {
                        public void run() {
                            ActionBarLayout.this.onAnimationEndCheck(false);
                        }
                    });
                    if (animation != null) {
                        if (VERSION.SDK_INT > 15) {
                            this.currentAnimation = animation;
                        } else {
                            this.currentAnimation = animation;
                        }
                        return;
                    } else if (this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) {
                        this.waitingForKeyboardCloseRunnable = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    ActionBarLayout.this.startLayoutAnimation(false, true);
                                }
                            }
                        };
                        AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                        return;
                    } else {
                        startLayoutAnimation(false, true);
                        return;
                    }
                }
                currentFragment.onTransitionAnimationEnd(false, false);
                previousFragment.onTransitionAnimationEnd(true, true);
                previousFragment.onBecomeFullyVisible();
            } else if (this.useAlphaAnimations) {
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.onCloseAnimationEndRunnable = new AnonymousClass12(currentFragment);
                ArrayList<Animator> animators = new ArrayList();
                animators.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL, 0.0f}));
                if (this.backgroundView != null) {
                    animators.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL, 0.0f}));
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                this.currentAnimation.setDuration(200);
                this.currentAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationStart(Animator animation) {
                        ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }

                    public void onAnimationEnd(Animator animation) {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }
                });
                this.currentAnimation.start();
            } else {
                removeFragmentFromStackInternal(currentFragment);
                setVisibility(8);
                if (this.backgroundView != null) {
                    this.backgroundView.setVisibility(8);
                }
            }
        }
    }

    public void showLastFragment() {
        if (!this.fragmentsStack.isEmpty()) {
            BaseFragment previousFragment;
            ViewGroup parent;
            for (int a = 0; a < this.fragmentsStack.size() - 1; a++) {
                previousFragment = (BaseFragment) this.fragmentsStack.get(a);
                if (previousFragment.actionBar != null) {
                    parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                }
                if (previousFragment.fragmentView != null) {
                    parent = (ViewGroup) previousFragment.fragmentView.getParent();
                    if (parent != null) {
                        previousFragment.onPause();
                        parent.removeView(previousFragment.fragmentView);
                    }
                }
            }
            previousFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            previousFragment.setParentLayout(this);
            View fragmentView = previousFragment.fragmentView;
            if (fragmentView == null) {
                fragmentView = previousFragment.createView(this.parentActivity);
            } else {
                parent = (ViewGroup) fragmentView.getParent();
                if (parent != null) {
                    parent.removeView(fragmentView);
                }
            }
            if (previousFragment.actionBar != null && previousFragment.actionBar.getAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    previousFragment.actionBar.setOccupyStatusBar(false);
                }
                parent = (ViewGroup) previousFragment.actionBar.getParent();
                if (parent != null) {
                    parent.removeView(previousFragment.actionBar);
                }
                this.containerView.addView(previousFragment.actionBar);
                previousFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
            }
            this.containerView.addView(fragmentView);
            ViewGroup.LayoutParams layoutParams = fragmentView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            fragmentView.setLayoutParams(layoutParams);
            previousFragment.onResume();
            this.currentActionBar = previousFragment.actionBar;
            if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                fragmentView.setBackgroundColor(-1);
            }
        }
    }

    private void removeFragmentFromStackInternal(BaseFragment fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        this.fragmentsStack.remove(fragment);
    }

    public void removeFragmentFromStack(BaseFragment fragment) {
        if (this.useAlphaAnimations && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
            closeLastFragment(true);
        } else {
            removeFragmentFromStackInternal(fragment);
        }
    }

    public void removeAllFragments() {
        int a = 0;
        while (this.fragmentsStack.size() > 0) {
            removeFragmentFromStackInternal((BaseFragment) this.fragmentsStack.get(a));
            a = (a - 1) + 1;
        }
    }

    public void rebuildAllFragmentViews(boolean last) {
        int a = 0;
        while (true) {
            if (a >= this.fragmentsStack.size() - (last ? 0 : 1)) {
                break;
            }
            ((BaseFragment) this.fragmentsStack.get(a)).clearViews();
            ((BaseFragment) this.fragmentsStack.get(a)).setParentLayout(this);
            a++;
        }
        if (this.delegate != null) {
            this.delegate.onRebuildAllFragments(this);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!(keyCode != 82 || checkTransitionAnimation() || this.startedTracking || this.currentActionBar == null)) {
            this.currentActionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onActionModeStarted(Object mode) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onActionModeFinished(Object mode) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(0);
        }
        this.inActionMode = false;
    }

    private void onCloseAnimationEnd(boolean post) {
        if (this.transitionAnimationInProgress && this.onCloseAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationStartTime = 0;
            if (post) {
                new Handler().post(new Runnable() {
                    public void run() {
                        ActionBarLayout.this.onCloseAnimationEndRunnable.run();
                        ActionBarLayout.this.onCloseAnimationEndRunnable = null;
                    }
                });
                return;
            }
            this.onCloseAnimationEndRunnable.run();
            this.onCloseAnimationEndRunnable = null;
        }
    }

    private void onOpenAnimationEnd(boolean post) {
        if (this.transitionAnimationInProgress && this.onOpenAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationStartTime = 0;
            if (post) {
                new Handler().post(new Runnable() {
                    public void run() {
                        ActionBarLayout.this.onOpenAnimationEndRunnable.run();
                        ActionBarLayout.this.onOpenAnimationEndRunnable = null;
                    }
                });
                return;
            }
            this.onOpenAnimationEndRunnable.run();
            this.onOpenAnimationEndRunnable = null;
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (this.parentActivity != null) {
            if (this.transitionAnimationInProgress) {
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                    this.currentAnimation = null;
                }
                if (this.onCloseAnimationEndRunnable != null) {
                    onCloseAnimationEnd(false);
                } else if (this.onOpenAnimationEndRunnable != null) {
                    onOpenAnimationEnd(false);
                }
                this.containerView.invalidate();
                if (intent != null) {
                    this.parentActivity.startActivityForResult(intent, requestCode);
                }
            } else if (intent != null) {
                this.parentActivity.startActivityForResult(intent, requestCode);
            }
        }
    }

    public void setUseAlphaAnimations(boolean value) {
        this.useAlphaAnimations = value;
    }

    public void setBackgroundView(View view) {
        this.backgroundView = view;
    }

    public void setDrawerLayoutContainer(DrawerLayoutContainer layout) {
        this.drawerLayoutContainer = layout;
    }

    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return this.drawerLayoutContainer;
    }

    public void setRemoveActionBarExtraHeight(boolean value) {
        this.removeActionBarExtraHeight = value;
    }

    public void setTitleOverlayText(String text) {
        this.titleOverlayText = text;
        Iterator i$ = this.fragmentsStack.iterator();
        while (i$.hasNext()) {
            BaseFragment fragment = (BaseFragment) i$.next();
            if (fragment.actionBar != null) {
                fragment.actionBar.setTitleOverlayText(this.titleOverlayText);
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
