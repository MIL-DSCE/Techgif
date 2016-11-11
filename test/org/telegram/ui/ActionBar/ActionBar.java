package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ActionBar extends FrameLayout {
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    private ActionBarMenu actionMode;
    private AnimatorSet actionModeAnimation;
    private View actionModeTop;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private int extraHeight;
    private boolean interceptTouches;
    private boolean isBackOverlayVisible;
    protected boolean isSearchFieldVisible;
    protected int itemsBackgroundColor;
    private CharSequence lastTitle;
    private ActionBarMenu menu;
    private boolean occupyStatusBar;
    protected BaseFragment parentFragment;
    private SimpleTextView subtitleTextView;
    private SimpleTextView titleTextView;

    /* renamed from: org.telegram.ui.ActionBar.ActionBar.1 */
    class C08851 implements OnClickListener {
        C08851() {
        }

        public void onClick(View v) {
            if (ActionBar.this.isSearchFieldVisible) {
                ActionBar.this.closeSearchField();
            } else if (ActionBar.this.actionBarMenuOnItemClick != null) {
                ActionBar.this.actionBarMenuOnItemClick.onItemClick(-1);
            }
        }
    }

    public static class ActionBarMenuOnItemClick {
        public void onItemClick(int id) {
        }

        public boolean canOpenMenu() {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar.2 */
    class C17382 extends AnimatorListenerAdapterProxy {
        C17382() {
        }

        public void onAnimationStart(Animator animation) {
            ActionBar.this.actionMode.setVisibility(0);
            if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null) {
                ActionBar.this.actionModeTop.setVisibility(0);
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                ActionBar.this.actionModeAnimation = null;
                if (ActionBar.this.titleTextView != null) {
                    ActionBar.this.titleTextView.setVisibility(4);
                }
                if (ActionBar.this.subtitleTextView != null) {
                    ActionBar.this.subtitleTextView.setVisibility(4);
                }
                if (ActionBar.this.menu != null) {
                    ActionBar.this.menu.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                ActionBar.this.actionModeAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar.3 */
    class C17393 extends AnimatorListenerAdapterProxy {
        C17393() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                ActionBar.this.actionModeAnimation = null;
                ActionBar.this.actionMode.setVisibility(4);
                if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null) {
                    ActionBar.this.actionModeTop.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                ActionBar.this.actionModeAnimation = null;
            }
        }
    }

    public ActionBar(Context context) {
        super(context);
        this.occupyStatusBar = VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.castShadows = true;
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            this.backButtonImageView = new ImageView(getContext());
            this.backButtonImageView.setScaleType(ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
            this.backButtonImageView.setPadding(AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new C08851());
        }
    }

    public void setBackButtonDrawable(Drawable drawable) {
        int i;
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        ImageView imageView = this.backButtonImageView;
        if (drawable == null) {
            i = 8;
        } else {
            i = 0;
        }
        imageView.setVisibility(i);
        this.backButtonImageView.setImageDrawable(drawable);
        if (drawable instanceof BackDrawable) {
            ((BackDrawable) drawable).setRotation(isActionModeShowed() ? TouchHelperCallback.ALPHA_FULL : 0.0f, false);
        }
    }

    public void setBackButtonImage(int resource) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(resource == 0 ? 8 : 0);
        this.backButtonImageView.setImageResource(resource);
    }

    private void createsubtitleTextView() {
        if (this.subtitleTextView == null) {
            this.subtitleTextView = new SimpleTextView(getContext());
            this.subtitleTextView.setGravity(3);
            this.subtitleTextView.setTextColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
            addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setAddToContainer(boolean value) {
        this.addToContainer = value;
    }

    public boolean getAddToContainer() {
        return this.addToContainer;
    }

    public void setSubtitle(CharSequence value) {
        if (value != null && this.subtitleTextView == null) {
            createsubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            SimpleTextView simpleTextView = this.subtitleTextView;
            int i = (value == null || this.isSearchFieldVisible) ? 4 : 0;
            simpleTextView.setVisibility(i);
            this.subtitleTextView.setText(value);
        }
    }

    private void createTitleTextView() {
        if (this.titleTextView == null) {
            this.titleTextView = new SimpleTextView(getContext());
            this.titleTextView.setGravity(3);
            this.titleTextView.setTextColor(-1);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.titleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setTitle(CharSequence value) {
        if (value != null && this.titleTextView == null) {
            createTitleTextView();
        }
        if (this.titleTextView != null) {
            this.lastTitle = value;
            SimpleTextView simpleTextView = this.titleTextView;
            int i = (value == null || this.isSearchFieldVisible) ? 4 : 0;
            simpleTextView.setVisibility(i);
            this.titleTextView.setText(value);
        }
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView;
    }

    public String getTitle() {
        if (this.titleTextView == null) {
            return null;
        }
        return this.titleTextView.getText().toString();
    }

    public ActionBarMenu createMenu() {
        if (this.menu != null) {
            return this.menu;
        }
        this.menu = new ActionBarMenu(getContext(), this);
        addView(this.menu, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick listener) {
        this.actionBarMenuOnItemClick = listener;
    }

    public ActionBarMenu createActionMode() {
        if (this.actionMode != null) {
            return this.actionMode;
        }
        int i;
        this.actionMode = new ActionBarMenu(getContext(), this);
        this.actionMode.setBackgroundColor(-1);
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        ActionBarMenu actionBarMenu = this.actionMode;
        if (this.occupyStatusBar) {
            i = AndroidUtilities.statusBarHeight;
        } else {
            i = 0;
        }
        actionBarMenu.setPadding(0, i, 0, 0);
        LayoutParams layoutParams = (LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        if (this.occupyStatusBar && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(-1728053248);
            addView(this.actionModeTop);
            layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
            this.actionModeTop.setVisibility(4);
        }
        return this.actionMode;
    }

    public void showActionMode() {
        if (this.actionMode != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            ArrayList<Animator> animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL}));
            if (this.occupyStatusBar && this.actionModeTop != null) {
                animators.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f, TouchHelperCallback.ALPHA_FULL}));
            }
            if (this.actionModeAnimation != null) {
                this.actionModeAnimation.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(animators);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new C17382());
            this.actionModeAnimation.start();
            if (this.backButtonImageView != null) {
                Drawable drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(TouchHelperCallback.ALPHA_FULL, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_MODE_SELECTOR_COLOR));
            }
        }
    }

    public void hideActionMode() {
        if (this.actionMode != null && this.actionModeVisible) {
            this.actionModeVisible = false;
            ArrayList<Animator> animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f}));
            if (this.occupyStatusBar && this.actionModeTop != null) {
                animators.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f}));
            }
            if (this.actionModeAnimation != null) {
                this.actionModeAnimation.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(animators);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new C17393());
            this.actionModeAnimation.start();
            if (this.titleTextView != null) {
                this.titleTextView.setVisibility(0);
            }
            if (this.subtitleTextView != null) {
                this.subtitleTextView.setVisibility(0);
            }
            if (this.menu != null) {
                this.menu.setVisibility(0);
            }
            if (this.backButtonImageView != null) {
                Drawable drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(0.0f, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
            }
        }
    }

    public void showActionModeTop() {
        if (this.occupyStatusBar && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(-1728053248);
            addView(this.actionModeTop);
            LayoutParams layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    protected void onSearchFieldVisibilityChanged(boolean visible) {
        int i = 4;
        this.isSearchFieldVisible = visible;
        if (this.titleTextView != null) {
            this.titleTextView.setVisibility(visible ? 4 : 0);
        }
        if (this.subtitleTextView != null) {
            SimpleTextView simpleTextView = this.subtitleTextView;
            if (!visible) {
                i = 0;
            }
            simpleTextView.setVisibility(i);
        }
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable != null && (drawable instanceof MenuDrawable)) {
            ((MenuDrawable) drawable).setRotation(visible ? TouchHelperCallback.ALPHA_FULL : 0.0f, true);
        }
    }

    public void setInterceptTouches(boolean value) {
        this.interceptTouches = value;
    }

    public void setExtraHeight(int value) {
        this.extraHeight = value;
    }

    public void closeSearchField() {
        if (this.isSearchFieldVisible && this.menu != null) {
            this.menu.closeSearchField();
        }
    }

    public void openSearchField(String text) {
        if (this.menu != null && text != null) {
            this.menu.openSearchField(!this.isSearchFieldVisible, text);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLeft;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int actionBarHeight = getCurrentActionBarHeight();
        int actionBarHeightSpec = MeasureSpec.makeMeasureSpec(actionBarHeight, C0747C.ENCODING_PCM_32BIT);
        setMeasuredDimension(width, ((this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + actionBarHeight) + this.extraHeight);
        if (this.backButtonImageView == null || this.backButtonImageView.getVisibility() == 8) {
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), C0747C.ENCODING_PCM_32BIT), actionBarHeightSpec);
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            int menuWidth;
            if (this.isSearchFieldVisible) {
                menuWidth = MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f), C0747C.ENCODING_PCM_32BIT);
            } else {
                menuWidth = MeasureSpec.makeMeasureSpec(width, LinearLayoutManager.INVALID_OFFSET);
            }
            this.menu.measure(menuWidth, actionBarHeightSpec);
        }
        if (!((this.titleTextView == null || this.titleTextView.getVisibility() == 8) && (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8))) {
            SimpleTextView simpleTextView;
            int i;
            int availableWidth = ((width - (this.menu != null ? this.menu.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - textLeft;
            if (!(this.titleTextView == null || this.titleTextView.getVisibility() == 8)) {
                simpleTextView = this.titleTextView;
                i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18;
                simpleTextView.setTextSize(i);
                this.titleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), LinearLayoutManager.INVALID_OFFSET));
            }
            if (!(this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8)) {
                simpleTextView = this.subtitleTextView;
                i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14;
                simpleTextView.setTextSize(i);
                this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), LinearLayoutManager.INVALID_OFFSET));
            }
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View child = getChildAt(i2);
            if (!(child.getVisibility() == 8 || child == this.titleTextView || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView)) {
                measureChildWithMargins(child, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0747C.ENCODING_PCM_32BIT), 0);
            }
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int textLeft;
        int menuLeft;
        int currentActionBarHeight;
        int i;
        float f;
        int textTop;
        int childCount;
        int i2;
        View child;
        View view;
        LayoutParams lp;
        int width;
        int height;
        int gravity;
        int verticalGravity;
        int childLeft;
        int childTop;
        int additionalTop = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        if (this.backButtonImageView != null) {
            if (this.backButtonImageView.getVisibility() != 8) {
                this.backButtonImageView.layout(0, additionalTop, this.backButtonImageView.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + additionalTop);
                textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
                if (this.menu != null) {
                    if (this.menu.getVisibility() != 8) {
                        if (this.isSearchFieldVisible) {
                            menuLeft = (right - left) - this.menu.getMeasuredWidth();
                        } else {
                            menuLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
                        }
                        this.menu.layout(menuLeft, additionalTop, this.menu.getMeasuredWidth() + menuLeft, this.menu.getMeasuredHeight() + additionalTop);
                    }
                }
                if (this.titleTextView != null) {
                    if (this.titleTextView.getVisibility() != 8) {
                        if (this.subtitleTextView != null) {
                            if (this.subtitleTextView.getVisibility() != 8) {
                                currentActionBarHeight = ((getCurrentActionBarHeight() / 2) - this.titleTextView.getTextHeight()) / 2;
                                if (!AndroidUtilities.isTablet()) {
                                    i = getResources().getConfiguration().orientation;
                                    if (r0 == 2) {
                                        f = 2.0f;
                                        textTop = currentActionBarHeight + AndroidUtilities.dp(f);
                                        this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
                                    }
                                }
                                f = 3.0f;
                                textTop = currentActionBarHeight + AndroidUtilities.dp(f);
                                this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
                            }
                        }
                        textTop = (getCurrentActionBarHeight() - this.titleTextView.getTextHeight()) / 2;
                        this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
                    }
                }
                if (this.subtitleTextView != null) {
                    if (this.subtitleTextView.getVisibility() != 8) {
                        currentActionBarHeight = (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2) + (getCurrentActionBarHeight() / 2);
                        if (!AndroidUtilities.isTablet()) {
                            i = getResources().getConfiguration().orientation;
                            if (r0 == 2) {
                                f = TouchHelperCallback.ALPHA_FULL;
                                textTop = currentActionBarHeight - AndroidUtilities.dp(f);
                                this.subtitleTextView.layout(textLeft, additionalTop + textTop, this.subtitleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.subtitleTextView.getTextHeight());
                            }
                        }
                        f = TouchHelperCallback.ALPHA_FULL;
                        textTop = currentActionBarHeight - AndroidUtilities.dp(f);
                        this.subtitleTextView.layout(textLeft, additionalTop + textTop, this.subtitleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.subtitleTextView.getTextHeight());
                    }
                }
                childCount = getChildCount();
                for (i2 = 0; i2 < childCount; i2++) {
                    child = getChildAt(i2);
                    if (child.getVisibility() != 8) {
                        view = this.titleTextView;
                        if (child != r0) {
                            view = this.subtitleTextView;
                            if (child != r0) {
                                view = this.menu;
                                if (child != r0) {
                                    view = this.backButtonImageView;
                                    if (child == r0) {
                                        lp = (LayoutParams) child.getLayoutParams();
                                        width = child.getMeasuredWidth();
                                        height = child.getMeasuredHeight();
                                        gravity = lp.gravity;
                                        if (gravity == -1) {
                                            gravity = 51;
                                        }
                                        verticalGravity = gravity & 112;
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
                            }
                        }
                    }
                }
            }
        }
        textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        if (this.menu != null) {
            if (this.menu.getVisibility() != 8) {
                if (this.isSearchFieldVisible) {
                    menuLeft = (right - left) - this.menu.getMeasuredWidth();
                } else {
                    if (AndroidUtilities.isTablet()) {
                    }
                    menuLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
                }
                this.menu.layout(menuLeft, additionalTop, this.menu.getMeasuredWidth() + menuLeft, this.menu.getMeasuredHeight() + additionalTop);
            }
        }
        if (this.titleTextView != null) {
            if (this.titleTextView.getVisibility() != 8) {
                if (this.subtitleTextView != null) {
                    if (this.subtitleTextView.getVisibility() != 8) {
                        currentActionBarHeight = ((getCurrentActionBarHeight() / 2) - this.titleTextView.getTextHeight()) / 2;
                        if (AndroidUtilities.isTablet()) {
                            i = getResources().getConfiguration().orientation;
                            if (r0 == 2) {
                                f = 2.0f;
                                textTop = currentActionBarHeight + AndroidUtilities.dp(f);
                                this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
                            }
                        }
                        f = 3.0f;
                        textTop = currentActionBarHeight + AndroidUtilities.dp(f);
                        this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
                    }
                }
                textTop = (getCurrentActionBarHeight() - this.titleTextView.getTextHeight()) / 2;
                this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
            }
        }
        if (this.subtitleTextView != null) {
            if (this.subtitleTextView.getVisibility() != 8) {
                currentActionBarHeight = (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2) + (getCurrentActionBarHeight() / 2);
                if (AndroidUtilities.isTablet()) {
                    i = getResources().getConfiguration().orientation;
                    if (r0 == 2) {
                        f = TouchHelperCallback.ALPHA_FULL;
                        textTop = currentActionBarHeight - AndroidUtilities.dp(f);
                        this.subtitleTextView.layout(textLeft, additionalTop + textTop, this.subtitleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.subtitleTextView.getTextHeight());
                    }
                }
                f = TouchHelperCallback.ALPHA_FULL;
                textTop = currentActionBarHeight - AndroidUtilities.dp(f);
                this.subtitleTextView.layout(textLeft, additionalTop + textTop, this.subtitleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.subtitleTextView.getTextHeight());
            }
        }
        childCount = getChildCount();
        for (i2 = 0; i2 < childCount; i2++) {
            child = getChildAt(i2);
            if (child.getVisibility() != 8) {
                view = this.titleTextView;
                if (child != r0) {
                    view = this.subtitleTextView;
                    if (child != r0) {
                        view = this.menu;
                        if (child != r0) {
                            view = this.backButtonImageView;
                            if (child == r0) {
                                lp = (LayoutParams) child.getLayoutParams();
                                width = child.getMeasuredWidth();
                                height = child.getMeasuredHeight();
                                gravity = lp.gravity;
                                if (gravity == -1) {
                                    gravity = 51;
                                }
                                verticalGravity = gravity & 112;
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
                    }
                }
            }
        }
    }

    public void onMenuButtonPressed() {
        if (this.menu != null) {
            this.menu.onMenuButtonPressed();
        }
    }

    protected void onPause() {
        if (this.menu != null) {
            this.menu.hideAllPopupMenus();
        }
    }

    public void setAllowOverlayTitle(boolean value) {
        this.allowOverlayTitle = value;
    }

    public void setTitleOverlayText(String text) {
        if (this.allowOverlayTitle && this.parentFragment.parentLayout != null) {
            CharSequence textToSet = text != null ? text : this.lastTitle;
            if (textToSet != null && this.titleTextView == null) {
                createTitleTextView();
            }
            if (this.titleTextView != null) {
                SimpleTextView simpleTextView = this.titleTextView;
                int i = (textToSet == null || this.isSearchFieldVisible) ? 4 : 0;
                simpleTextView.setVisibility(i);
                this.titleTextView.setText(textToSet);
            }
        }
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
        if (this.actionMode != null) {
            int i;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (this.occupyStatusBar) {
                i = AndroidUtilities.statusBarHeight;
            } else {
                i = 0;
            }
            actionBarMenu.setPadding(0, i, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int color) {
        this.itemsBackgroundColor = color;
        if (this.backButtonImageView != null) {
            this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
        }
    }

    public void setCastShadows(boolean value) {
        this.castShadows = value;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || this.interceptTouches;
    }

    public static int getCurrentActionBarHeight() {
        if (AndroidUtilities.isTablet()) {
            return AndroidUtilities.dp(64.0f);
        }
        if (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2) {
            return AndroidUtilities.dp(48.0f);
        }
        return AndroidUtilities.dp(56.0f);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
