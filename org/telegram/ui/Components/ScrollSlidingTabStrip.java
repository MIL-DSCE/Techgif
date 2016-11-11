package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC.Document;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private int currentPosition;
    private LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding;
    private int indicatorColor;
    private int lastScrollX;
    private Paint rectPaint;
    private int scrollOffset;
    private int tabCount;
    private int tabPadding;
    private LinearLayout tabsContainer;
    private int underlineColor;
    private int underlineHeight;

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip.1 */
    class C11731 implements OnClickListener {
        final /* synthetic */ int val$position;

        C11731(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            ScrollSlidingTabStrip.this.delegate.onPageSelected(this.val$position);
        }
    }

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip.2 */
    class C11742 implements OnClickListener {
        final /* synthetic */ int val$position;

        C11742(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            ScrollSlidingTabStrip.this.delegate.onPageSelected(this.val$position);
        }
    }

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    public ScrollSlidingTabStrip(Context context) {
        super(context);
        this.currentPosition = 0;
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.scrollOffset = AndroidUtilities.dp(52.0f);
        this.underlineHeight = AndroidUtilities.dp(2.0f);
        this.dividerPadding = AndroidUtilities.dp(12.0f);
        this.tabPadding = AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.defaultTabLayoutParams = new LayoutParams(AndroidUtilities.dp(52.0f), -1);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabCount = 0;
        this.currentPosition = 0;
    }

    public void selectTab(int num) {
        if (num >= 0 && num < this.tabCount) {
            View tab = this.tabsContainer.getChildAt(num);
            if (VERSION.SDK_INT >= 15) {
                tab.callOnClick();
            } else {
                tab.performClick();
            }
        }
    }

    public void addIconTab(int resId) {
        boolean z = true;
        int position = this.tabCount;
        this.tabCount = position + 1;
        ImageView tab = new ImageView(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);
        tab.setScaleType(ScaleType.CENTER);
        tab.setOnClickListener(new C11731(position));
        this.tabsContainer.addView(tab);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
    }

    public void addStickerTab(Document sticker) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setFocusable(true);
        tab.setOnClickListener(new C11742(position));
        this.tabsContainer.addView(tab);
        tab.setSelected(position == this.currentPosition);
        BackupImageView imageView = new BackupImageView(getContext());
        if (!(sticker == null || sticker.thumb == null)) {
            imageView.setImage(sticker.thumb.location, null, "webp", null);
        }
        imageView.setAspectFit(true);
        tab.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            this.tabsContainer.getChildAt(i).setLayoutParams(this.defaultTabLayoutParams);
        }
    }

    private void scrollToChild(int position) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(position) != null) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft();
            if (position > 0) {
                newScrollX -= this.scrollOffset;
            }
            int currentScrollX = getScrollX();
            if (newScrollX == this.lastScrollX) {
                return;
            }
            if (newScrollX < currentScrollX) {
                this.lastScrollX = newScrollX;
                smoothScrollTo(this.lastScrollX, 0);
            } else if (this.scrollOffset + newScrollX > (getWidth() + currentScrollX) - (this.scrollOffset * 2)) {
                this.lastScrollX = (newScrollX - getWidth()) + (this.scrollOffset * 3);
                smoothScrollTo(this.lastScrollX, 0);
            }
        }
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
            float lineLeft = 0.0f;
            float lineRight = 0.0f;
            if (currentTab != null) {
                lineLeft = (float) currentTab.getLeft();
                lineRight = (float) currentTab.getRight();
            }
            this.rectPaint.setColor(this.indicatorColor);
            canvas.drawRect(lineLeft, 0.0f, lineRight, (float) height, this.rectPaint);
        }
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition != position) {
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                int a = 0;
                while (a < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(a).setSelected(a == position);
                    a++;
                }
                if (first != position || position <= 1) {
                    scrollToChild(position);
                } else {
                    scrollToChild(position - 1);
                }
                invalidate();
            }
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }
}
