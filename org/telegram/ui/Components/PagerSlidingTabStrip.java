package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    private int currentPosition;
    private float currentPositionOffset;
    private LayoutParams defaultTabLayoutParams;
    public OnPageChangeListener delegatePageListener;
    private int dividerPadding;
    private int indicatorColor;
    private int indicatorHeight;
    private int lastScrollX;
    private final PageListener pageListener;
    private ViewPager pager;
    private Paint rectPaint;
    private int scrollOffset;
    private boolean shouldExpand;
    private int tabCount;
    private int tabPadding;
    private LinearLayout tabsContainer;
    private int underlineColor;
    private int underlineHeight;

    /* renamed from: org.telegram.ui.Components.PagerSlidingTabStrip.1 */
    class C11361 implements OnGlobalLayoutListener {
        C11361() {
        }

        public void onGlobalLayout() {
            if (VERSION.SDK_INT < 16) {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            PagerSlidingTabStrip.this.currentPosition = PagerSlidingTabStrip.this.pager.getCurrentItem();
            PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.currentPosition, 0);
        }
    }

    /* renamed from: org.telegram.ui.Components.PagerSlidingTabStrip.2 */
    class C11372 implements OnClickListener {
        final /* synthetic */ int val$position;

        C11372(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            PagerSlidingTabStrip.this.pager.setCurrentItem(this.val$position);
        }
    }

    /* renamed from: org.telegram.ui.Components.PagerSlidingTabStrip.3 */
    class C11383 implements Runnable {
        C11383() {
        }

        public void run() {
            PagerSlidingTabStrip.this.notifyDataSetChanged();
        }
    }

    public interface IconTabProvider {
        int getPageIconResId(int i);
    }

    private class PageListener implements OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            PagerSlidingTabStrip.this.currentPosition = position;
            PagerSlidingTabStrip.this.currentPositionOffset = positionOffset;
            PagerSlidingTabStrip.this.scrollToChild(position, (int) (((float) PagerSlidingTabStrip.this.tabsContainer.getChildAt(position).getWidth()) * positionOffset));
            PagerSlidingTabStrip.this.invalidate();
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.pager.getCurrentItem(), 0);
            }
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        public void onPageSelected(int position) {
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageSelected(position);
            }
            int a = 0;
            while (a < PagerSlidingTabStrip.this.tabsContainer.getChildCount()) {
                PagerSlidingTabStrip.this.tabsContainer.getChildAt(a).setSelected(a == position);
                a++;
            }
        }
    }

    public PagerSlidingTabStrip(Context context) {
        super(context);
        this.pageListener = new PageListener();
        this.currentPosition = 0;
        this.currentPositionOffset = 0.0f;
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.shouldExpand = false;
        this.scrollOffset = AndroidUtilities.dp(52.0f);
        this.indicatorHeight = AndroidUtilities.dp(8.0f);
        this.underlineHeight = AndroidUtilities.dp(2.0f);
        this.dividerPadding = AndroidUtilities.dp(12.0f);
        this.tabPadding = AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        setFillViewport(true);
        setWillNotDraw(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.defaultTabLayoutParams = new LayoutParams(-2, -1);
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconResId(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new C11361());
    }

    private void addIconTab(int position, int resId) {
        boolean z = true;
        ImageView tab = new ImageView(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);
        tab.setScaleType(ScaleType.CENTER);
        tab.setOnClickListener(new C11372(position));
        this.tabsContainer.addView(tab);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            v.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                v.setPadding(0, 0, 0, 0);
                v.setLayoutParams(new LayoutParams(-1, -1, TouchHelperCallback.ALPHA_FULL));
            } else {
                v.setPadding(this.tabPadding, 0, this.tabPadding, 0);
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.shouldExpand && MeasureSpec.getMode(widthMeasureSpec) != 0) {
            this.tabsContainer.measure(C0747C.ENCODING_PCM_32BIT | getMeasuredWidth(), heightMeasureSpec);
        }
    }

    private void scrollToChild(int position, int offset) {
        if (this.tabCount != 0) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= this.scrollOffset;
            }
            if (newScrollX != this.lastScrollX) {
                this.lastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            float lineLeft;
            int height = getHeight();
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
            float lineLeft2 = (float) currentTab.getLeft();
            float lineRight = (float) currentTab.getRight();
            if (this.currentPositionOffset <= 0.0f || this.currentPosition >= this.tabCount - 1) {
                lineLeft = lineLeft2;
            } else {
                View nextTab = this.tabsContainer.getChildAt(this.currentPosition + 1);
                lineLeft = (this.currentPositionOffset * ((float) nextTab.getLeft())) + ((TouchHelperCallback.ALPHA_FULL - this.currentPositionOffset) * lineLeft2);
                lineRight = (this.currentPositionOffset * ((float) nextTab.getRight())) + ((TouchHelperCallback.ALPHA_FULL - this.currentPositionOffset) * lineRight);
            }
            this.rectPaint.setColor(this.indicatorColor);
            canvas.drawRect(lineLeft, (float) (height - this.indicatorHeight), lineRight, (float) height, this.rectPaint);
        }
    }

    public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if (!this.shouldExpand) {
            post(new C11383());
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        updateTabStyles();
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }
}
