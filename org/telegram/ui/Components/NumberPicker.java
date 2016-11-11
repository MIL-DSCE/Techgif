package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.Locale;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.ConnectionsManager;

public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 0;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private Scroller mAdjustScroller;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private boolean mDecrementVirtualButtonPressed;
    private String[] mDisplayedValues;
    private Scroller mFlingScroller;
    private Formatter mFormatter;
    private boolean mIncrementVirtualButtonPressed;
    private boolean mIngonreMoveEvents;
    private int mInitialScrollOffset;
    private TextView mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private int mMinHeight;
    private int mMinValue;
    private int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private Drawable mSelectionDivider;
    private int mSelectionDividerHeight;
    private int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private Paint mSelectorWheelPaint;
    private int mSolidColor;
    private int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private Drawable mVirtualButtonPressedDrawable;
    private boolean mWrapSelectorWheel;

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        private void setStep(boolean increment) {
            this.mIncrement = increment;
        }

        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker.this.postDelayed(this, NumberPicker.this.mLongPressUpdateInterval);
        }
    }

    public interface Formatter {
        String format(int i);
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS;
        private final int MODE_TAPPED;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
            this.MODE_PRESS = BUTTON_INCREMENT;
            this.MODE_TAPPED = BUTTON_DECREMENT;
        }

        public void cancel() {
            this.mMode = NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID;
            this.mManagedButton = NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            }
            NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        public void buttonPressDelayed(int button) {
            cancel();
            this.mMode = BUTTON_INCREMENT;
            this.mManagedButton = button;
            NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int button) {
            cancel();
            this.mMode = BUTTON_DECREMENT;
            this.mManagedButton = button;
            NumberPicker.this.post(this);
        }

        public void run() {
            switch (this.mMode) {
                case BUTTON_INCREMENT /*1*/:
                    switch (this.mManagedButton) {
                        case BUTTON_INCREMENT /*1*/:
                            NumberPicker.this.mIncrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                        case BUTTON_DECREMENT /*2*/:
                            NumberPicker.this.mDecrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                        default:
                    }
                case BUTTON_DECREMENT /*2*/:
                    switch (this.mManagedButton) {
                        case BUTTON_INCREMENT /*1*/:
                            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.access$180(NumberPicker.this, BUTTON_INCREMENT);
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                        case BUTTON_DECREMENT /*2*/:
                            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.access$380(NumberPicker.this, BUTTON_INCREMENT);
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                        default:
                    }
                default:
            }
        }
    }

    static /* synthetic */ boolean access$180(NumberPicker x0, int x1) {
        boolean z = (byte) (x0.mIncrementVirtualButtonPressed ^ x1);
        x0.mIncrementVirtualButtonPressed = z;
        return z;
    }

    static /* synthetic */ boolean access$380(NumberPicker x0, int x1) {
        boolean z = (byte) (x0.mDecrementVirtualButtonPressed ^ x1);
        x0.mDecrementVirtualButtonPressed = z;
        return z;
    }

    private void init() {
        this.mSolidColor = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mSelectionDivider = getResources().getDrawable(C0691R.drawable.numberpicker_selection_divider);
        this.mSelectionDividerHeight = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, 48.0f, getResources().getDisplayMetrics());
        this.mMinHeight = SIZE_UNSPECIFIED;
        this.mMaxHeight = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, BitmapDescriptorFactory.HUE_CYAN, getResources().getDisplayMetrics());
        if (this.mMinHeight == SIZE_UNSPECIFIED || this.mMaxHeight == SIZE_UNSPECIFIED || this.mMinHeight <= this.mMaxHeight) {
            this.mMinWidth = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, 64.0f, getResources().getDisplayMetrics());
            this.mMaxWidth = SIZE_UNSPECIFIED;
            if (this.mMinWidth == SIZE_UNSPECIFIED || this.mMaxWidth == SIZE_UNSPECIFIED || this.mMinWidth <= this.mMaxWidth) {
                boolean z;
                if (this.mMaxWidth == SIZE_UNSPECIFIED) {
                    z = true;
                } else {
                    z = false;
                }
                this.mComputeMaxWidth = z;
                this.mVirtualButtonPressedDrawable = getResources().getDrawable(C0691R.drawable.item_background_holo_light);
                this.mPressedStateHelper = new PressedStateHelper();
                setWillNotDraw(false);
                this.mInputText = new TextView(getContext());
                addView(this.mInputText);
                this.mInputText.setLayoutParams(new LayoutParams(SIZE_UNSPECIFIED, -2));
                this.mInputText.setGravity(17);
                this.mInputText.setSingleLine(true);
                this.mInputText.setBackgroundResource(DEFAULT_LAYOUT_RESOURCE_ID);
                this.mInputText.setTextSize(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT, 18.0f);
                ViewConfiguration configuration = ViewConfiguration.get(getContext());
                this.mTouchSlop = configuration.getScaledTouchSlop();
                this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
                this.mTextSize = (int) this.mInputText.getTextSize();
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextAlign(Align.CENTER);
                paint.setTextSize((float) this.mTextSize);
                paint.setTypeface(this.mInputText.getTypeface());
                paint.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, SIZE_UNSPECIFIED));
                this.mSelectorWheelPaint = paint;
                this.mFlingScroller = new Scroller(getContext(), null, true);
                this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
                updateInputTextView();
                return;
            }
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        throw new IllegalArgumentException("minHeight > maxHeight");
    }

    public NumberPicker(Context context) {
        super(context);
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
        this.mInitialScrollOffset = LinearLayoutManager.INVALID_OFFSET;
        this.mScrollState = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mLastHandledDownDpadKeyCode = SIZE_UNSPECIFIED;
        init();
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
        this.mInitialScrollOffset = LinearLayoutManager.INVALID_OFFSET;
        this.mScrollState = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mLastHandledDownDpadKeyCode = SIZE_UNSPECIFIED;
        init();
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
        this.mInitialScrollOffset = LinearLayoutManager.INVALID_OFFSET;
        this.mScrollState = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mLastHandledDownDpadKeyCode = SIZE_UNSPECIFIED;
        init();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int msrdWdth = getMeasuredWidth();
        int msrdHght = getMeasuredHeight();
        int inptTxtMsrdWdth = this.mInputText.getMeasuredWidth();
        int inptTxtMsrdHght = this.mInputText.getMeasuredHeight();
        int inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT;
        int inptTxtTop = (msrdHght - inptTxtMsrdHght) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT;
        this.mInputText.layout(inptTxtLeft, inptTxtTop, inptTxtLeft + inptTxtMsrdWdth, inptTxtTop + inptTxtMsrdHght);
        if (changed) {
            initializeSelectorWheel();
            initializeFadingEdges();
            this.mTopSelectionDividerTop = ((getHeight() - this.mSelectionDividersDistance) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT) - this.mSelectionDividerHeight;
            this.mBottomSelectionDividerBottom = (this.mTopSelectionDividerTop + (this.mSelectionDividerHeight * UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT)) + this.mSelectionDividersDistance;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(makeMeasureSpec(widthMeasureSpec, this.mMaxWidth), makeMeasureSpec(heightMeasureSpec, this.mMaxHeight));
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), widthMeasureSpec), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), heightMeasureSpec));
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        int overshootAdjustment = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + amountToScroll) % this.mSelectorElementHeight);
        if (overshootAdjustment == 0) {
            return false;
        }
        if (Math.abs(overshootAdjustment) > this.mSelectorElementHeight / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT) {
            if (overshootAdjustment > 0) {
                overshootAdjustment -= this.mSelectorElementHeight;
            } else {
                overshootAdjustment += this.mSelectorElementHeight;
            }
        }
        scrollBy(DEFAULT_LAYOUT_RESOURCE_ID, amountToScroll + overshootAdjustment);
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case DEFAULT_LAYOUT_RESOURCE_ID /*0*/:
                removeAllCallbacks();
                this.mInputText.setVisibility(4);
                float y = event.getY();
                this.mLastDownEventY = y;
                this.mLastDownOrMoveEventY = y;
                this.mLastDownEventTime = event.getEventTime();
                this.mIngonreMoveEvents = false;
                if (this.mLastDownEventY < ((float) this.mTopSelectionDividerTop)) {
                    if (this.mScrollState == 0) {
                        this.mPressedStateHelper.buttonPressDelayed(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                    }
                } else if (this.mLastDownEventY > ((float) this.mBottomSelectionDividerBottom) && this.mScrollState == 0) {
                    this.mPressedStateHelper.buttonPressDelayed(SELECTOR_MIDDLE_ITEM_INDEX);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                if (!this.mFlingScroller.isFinished()) {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                    onScrollStateChange(DEFAULT_LAYOUT_RESOURCE_ID);
                } else if (!this.mAdjustScroller.isFinished()) {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                } else if (this.mLastDownEventY < ((float) this.mTopSelectionDividerTop)) {
                    postChangeCurrentByOneFromLongPress(false, (long) ViewConfiguration.getLongPressTimeout());
                } else if (this.mLastDownEventY > ((float) this.mBottomSelectionDividerBottom)) {
                    postChangeCurrentByOneFromLongPress(true, (long) ViewConfiguration.getLongPressTimeout());
                }
                return true;
            default:
                return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        switch (event.getActionMasked()) {
            case SELECTOR_MIDDLE_ITEM_INDEX /*1*/:
                removeChangeCurrentByOneFromLongPress();
                this.mPressedStateHelper.cancel();
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > this.mMinimumFlingVelocity) {
                    fling(initialVelocity);
                    onScrollStateChange(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                } else {
                    int eventY = (int) event.getY();
                    long deltaTime = event.getEventTime() - this.mLastDownEventTime;
                    if (((int) Math.abs(((float) eventY) - this.mLastDownEventY)) > this.mTouchSlop || deltaTime >= ((long) ViewConfiguration.getTapTimeout())) {
                        ensureScrollWheelAdjusted();
                    } else {
                        int selectorIndexOffset = (eventY / this.mSelectorElementHeight) + SIZE_UNSPECIFIED;
                        if (selectorIndexOffset > 0) {
                            changeValueByOne(true);
                            this.mPressedStateHelper.buttonTapped(SELECTOR_MIDDLE_ITEM_INDEX);
                        } else if (selectorIndexOffset < 0) {
                            changeValueByOne(false);
                            this.mPressedStateHelper.buttonTapped(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                        }
                    }
                    onScrollStateChange(DEFAULT_LAYOUT_RESOURCE_ID);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                break;
            case UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT /*2*/:
                if (!this.mIngonreMoveEvents) {
                    float currentMoveY = event.getY();
                    if (this.mScrollState == SELECTOR_MIDDLE_ITEM_INDEX) {
                        scrollBy(DEFAULT_LAYOUT_RESOURCE_ID, (int) (currentMoveY - this.mLastDownOrMoveEventY));
                        invalidate();
                    } else if (((int) Math.abs(currentMoveY - this.mLastDownEventY)) > this.mTouchSlop) {
                        removeAllCallbacks();
                        onScrollStateChange(SELECTOR_MIDDLE_ITEM_INDEX);
                    }
                    this.mLastDownOrMoveEventY = currentMoveY;
                    break;
                }
                break;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case SELECTOR_MIDDLE_ITEM_INDEX /*1*/:
            case SELECTOR_WHEEL_ITEM_COUNT /*3*/:
                removeAllCallbacks();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchKeyEvent(android.view.KeyEvent r6) {
        /*
        r5 = this;
        r4 = 20;
        r2 = 1;
        r0 = r6.getKeyCode();
        switch(r0) {
            case 19: goto L_0x0013;
            case 20: goto L_0x0013;
            case 23: goto L_0x000f;
            case 66: goto L_0x000f;
            default: goto L_0x000a;
        };
    L_0x000a:
        r2 = super.dispatchKeyEvent(r6);
    L_0x000e:
        return r2;
    L_0x000f:
        r5.removeAllCallbacks();
        goto L_0x000a;
    L_0x0013:
        r1 = r6.getAction();
        switch(r1) {
            case 0: goto L_0x001b;
            case 1: goto L_0x004f;
            default: goto L_0x001a;
        };
    L_0x001a:
        goto L_0x000a;
    L_0x001b:
        r1 = r5.mWrapSelectorWheel;
        if (r1 != 0) goto L_0x0021;
    L_0x001f:
        if (r0 != r4) goto L_0x0042;
    L_0x0021:
        r1 = r5.getValue();
        r3 = r5.getMaxValue();
        if (r1 >= r3) goto L_0x000a;
    L_0x002b:
        r5.requestFocus();
        r5.mLastHandledDownDpadKeyCode = r0;
        r5.removeAllCallbacks();
        r1 = r5.mFlingScroller;
        r1 = r1.isFinished();
        if (r1 == 0) goto L_0x000e;
    L_0x003b:
        if (r0 != r4) goto L_0x004d;
    L_0x003d:
        r1 = r2;
    L_0x003e:
        r5.changeValueByOne(r1);
        goto L_0x000e;
    L_0x0042:
        r1 = r5.getValue();
        r3 = r5.getMinValue();
        if (r1 <= r3) goto L_0x000a;
    L_0x004c:
        goto L_0x002b;
    L_0x004d:
        r1 = 0;
        goto L_0x003e;
    L_0x004f:
        r1 = r5.mLastHandledDownDpadKeyCode;
        if (r1 != r0) goto L_0x000a;
    L_0x0053:
        r1 = -1;
        r5.mLastHandledDownDpadKeyCode = r1;
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.NumberPicker.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    public boolean dispatchTrackballEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case SELECTOR_MIDDLE_ITEM_INDEX /*1*/:
            case SELECTOR_WHEEL_ITEM_COUNT /*3*/:
                removeAllCallbacks();
                break;
        }
        return super.dispatchTrackballEvent(event);
    }

    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currentScrollerY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(DEFAULT_LAYOUT_RESOURCE_ID, currentScrollerY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currentScrollerY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mInputText.setEnabled(enabled);
    }

    public void scrollBy(int x, int y) {
        int[] selectorIndices = this.mSelectorIndices;
        if (!this.mWrapSelectorWheel && y > 0 && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else if (this.mWrapSelectorWheel || y >= 0 || selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] < this.mMaxValue) {
            this.mCurrentScrollOffset += y;
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight) {
                this.mCurrentScrollOffset -= this.mSelectorElementHeight;
                decrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX], true);
                if (!this.mWrapSelectorWheel && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
                this.mCurrentScrollOffset += this.mSelectorElementHeight;
                incrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX], true);
                if (!this.mWrapSelectorWheel && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= this.mMaxValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
        } else {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        }
    }

    protected int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    protected int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + SELECTOR_MIDDLE_ITEM_INDEX) * this.mSelectorElementHeight;
    }

    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    public int getSolidColor() {
        return this.mSolidColor;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.mOnValueChangeListener = onValueChangedListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != this.mFormatter) {
            this.mFormatter = formatter;
            initializeSelectorWheelIndices();
            updateInputTextView();
        }
    }

    public void setValue(int value) {
        setValueInternal(value, false);
    }

    private void tryComputeMaxWidth() {
        if (this.mComputeMaxWidth) {
            int maxTextWidth = DEFAULT_LAYOUT_RESOURCE_ID;
            if (this.mDisplayedValues == null) {
                float maxDigitWidth = 0.0f;
                for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i <= 9; i += SELECTOR_MIDDLE_ITEM_INDEX) {
                    float digitWidth = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                    if (digitWidth > maxDigitWidth) {
                        maxDigitWidth = digitWidth;
                    }
                }
                int numberOfDigits = DEFAULT_LAYOUT_RESOURCE_ID;
                for (int current = this.mMaxValue; current > 0; current /= 10) {
                    numberOfDigits += SELECTOR_MIDDLE_ITEM_INDEX;
                }
                maxTextWidth = (int) (((float) numberOfDigits) * maxDigitWidth);
            } else {
                String[] arr$ = this.mDisplayedValues;
                int len$ = arr$.length;
                for (int i$ = DEFAULT_LAYOUT_RESOURCE_ID; i$ < len$; i$ += SELECTOR_MIDDLE_ITEM_INDEX) {
                    float textWidth = this.mSelectorWheelPaint.measureText(arr$[i$]);
                    if (textWidth > ((float) maxTextWidth)) {
                        maxTextWidth = (int) textWidth;
                    }
                }
            }
            maxTextWidth += this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight();
            if (this.mMaxWidth != maxTextWidth) {
                if (maxTextWidth > this.mMinWidth) {
                    this.mMaxWidth = maxTextWidth;
                } else {
                    this.mMaxWidth = this.mMinWidth;
                }
                invalidate();
            }
        }
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        boolean wrappingAllowed = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length;
        if ((!wrapSelectorWheel || wrappingAllowed) && wrapSelectorWheel != this.mWrapSelectorWheel) {
            this.mWrapSelectorWheel = wrapSelectorWheel;
        }
    }

    public void setOnLongPressUpdateInterval(long intervalMillis) {
        this.mLongPressUpdateInterval = intervalMillis;
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(int minValue) {
        if (this.mMinValue != minValue) {
            if (minValue < 0) {
                throw new IllegalArgumentException("minValue must be >= 0");
            }
            this.mMinValue = minValue;
            if (this.mMinValue > this.mValue) {
                this.mValue = this.mMinValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
            initializeSelectorWheelIndices();
            updateInputTextView();
            tryComputeMaxWidth();
            invalidate();
        }
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        if (this.mMaxValue != maxValue) {
            if (maxValue < 0) {
                throw new IllegalArgumentException("maxValue must be >= 0");
            }
            this.mMaxValue = maxValue;
            if (this.mMaxValue < this.mValue) {
                this.mValue = this.mMaxValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
            initializeSelectorWheelIndices();
            updateInputTextView();
            tryComputeMaxWidth();
            invalidate();
        }
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setDisplayedValues(String[] displayedValues) {
        if (this.mDisplayedValues != displayedValues) {
            this.mDisplayedValues = displayedValues;
            updateInputTextView();
            initializeSelectorWheelIndices();
            tryComputeMaxWidth();
        }
    }

    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    protected void onDraw(Canvas canvas) {
        float x = (float) ((getRight() - getLeft()) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
        float y = (float) this.mCurrentScrollOffset;
        if (this.mVirtualButtonPressedDrawable != null && this.mScrollState == 0) {
            if (this.mDecrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, getRight(), this.mTopSelectionDividerTop);
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
            if (this.mIncrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, this.mBottomSelectionDividerBottom, getRight(), getBottom());
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
        }
        int[] selectorIndices = this.mSelectorIndices;
        for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i < selectorIndices.length; i += SELECTOR_MIDDLE_ITEM_INDEX) {
            String scrollSelectorValue = (String) this.mSelectorIndexToStringCache.get(selectorIndices[i]);
            if (i != SELECTOR_MIDDLE_ITEM_INDEX || this.mInputText.getVisibility() != 0) {
                canvas.drawText(scrollSelectorValue, x, y, this.mSelectorWheelPaint);
            }
            y += (float) this.mSelectorElementHeight;
        }
        if (this.mSelectionDivider != null) {
            int topOfTopDivider = this.mTopSelectionDividerTop;
            this.mSelectionDivider.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, topOfTopDivider, getRight(), topOfTopDivider + this.mSelectionDividerHeight);
            this.mSelectionDivider.draw(canvas);
            int bottomOfBottomDivider = this.mBottomSelectionDividerBottom;
            this.mSelectionDivider.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, bottomOfBottomDivider - this.mSelectionDividerHeight, getRight(), bottomOfBottomDivider);
            this.mSelectionDivider.draw(canvas);
        }
    }

    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == SIZE_UNSPECIFIED) {
            return measureSpec;
        }
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case LinearLayoutManager.INVALID_OFFSET /*-2147483648*/:
                return MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), C0747C.ENCODING_PCM_32BIT);
            case DEFAULT_LAYOUT_RESOURCE_ID /*0*/:
                return MeasureSpec.makeMeasureSpec(maxSize, C0747C.ENCODING_PCM_32BIT);
            case C0747C.ENCODING_PCM_32BIT /*1073741824*/:
                return measureSpec;
            default:
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
        }
    }

    private int resolveSizeAndStateRespectingMinSize(int minSize, int measuredSize, int measureSpec) {
        if (minSize != SIZE_UNSPECIFIED) {
            return resolveSizeAndState(Math.max(minSize, measuredSize), measureSpec, DEFAULT_LAYOUT_RESOURCE_ID);
        }
        return measuredSize;
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case LinearLayoutManager.INVALID_OFFSET /*-2147483648*/:
                if (specSize >= size) {
                    result = size;
                    break;
                }
                result = specSize | ViewCompat.MEASURED_STATE_TOO_SMALL;
                break;
            case DEFAULT_LAYOUT_RESOURCE_ID /*0*/:
                result = size;
                break;
            case C0747C.ENCODING_PCM_32BIT /*1073741824*/:
                result = specSize;
                break;
        }
        return (ViewCompat.MEASURED_STATE_MASK & childMeasuredState) | result;
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] selectorIndices = this.mSelectorIndices;
        int current = getValue();
        for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i < this.mSelectorIndices.length; i += SELECTOR_MIDDLE_ITEM_INDEX) {
            int selectorIndex = current + (i + SIZE_UNSPECIFIED);
            if (this.mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    private void setValueInternal(int current, boolean notifyChange) {
        if (this.mValue != current) {
            if (this.mWrapSelectorWheel) {
                current = getWrappedSelectorIndex(current);
            } else {
                current = Math.min(Math.max(current, this.mMinValue), this.mMaxValue);
            }
            int previous = this.mValue;
            this.mValue = current;
            updateInputTextView();
            if (notifyChange) {
                notifyChange(previous, current);
            }
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    private void changeValueByOne(boolean increment) {
        this.mInputText.setVisibility(4);
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = DEFAULT_LAYOUT_RESOURCE_ID;
        if (increment) {
            this.mFlingScroller.startScroll(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, -this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            this.mFlingScroller.startScroll(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = this.mSelectorIndices;
        this.mSelectorTextGapHeight = (int) ((((float) ((getBottom() - getTop()) - (selectorIndices.length * this.mTextSize))) / ((float) selectorIndices.length)) + 0.5f);
        this.mSelectorElementHeight = this.mTextSize + this.mSelectorTextGapHeight;
        this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop()) - (this.mSelectorElementHeight * SELECTOR_MIDDLE_ITEM_INDEX);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(DEFAULT_LAYOUT_RESOURCE_ID);
        } else if (this.mScrollState != SELECTOR_MIDDLE_ITEM_INDEX) {
            updateInputTextView();
        }
    }

    private void onScrollStateChange(int scrollState) {
        if (this.mScrollState != scrollState) {
            this.mScrollState = scrollState;
            if (this.mOnScrollListener != null) {
                this.mOnScrollListener.onScrollStateChange(this, scrollState);
            }
        }
    }

    private void fling(int velocityY) {
        this.mPreviousScrollerY = DEFAULT_LAYOUT_RESOURCE_ID;
        if (velocityY > 0) {
            this.mFlingScroller.fling(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, velocityY, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        } else {
            this.mFlingScroller.fling(DEFAULT_LAYOUT_RESOURCE_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, DEFAULT_LAYOUT_RESOURCE_ID, velocityY, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        }
        invalidate();
    }

    private int getWrappedSelectorIndex(int selectorIndex) {
        if (selectorIndex > this.mMaxValue) {
            return (this.mMinValue + ((selectorIndex - this.mMaxValue) % (this.mMaxValue - this.mMinValue))) + SIZE_UNSPECIFIED;
        }
        if (selectorIndex < this.mMinValue) {
            return (this.mMaxValue - ((this.mMinValue - selectorIndex) % (this.mMaxValue - this.mMinValue))) + SELECTOR_MIDDLE_ITEM_INDEX;
        }
        return selectorIndex;
    }

    private void incrementSelectorIndices(int[] selectorIndices) {
        System.arraycopy(selectorIndices, SELECTOR_MIDDLE_ITEM_INDEX, selectorIndices, DEFAULT_LAYOUT_RESOURCE_ID, selectorIndices.length + SIZE_UNSPECIFIED);
        int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + SELECTOR_MIDDLE_ITEM_INDEX;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex > this.mMaxValue) {
            nextScrollSelectorIndex = this.mMinValue;
        }
        selectorIndices[selectorIndices.length + SIZE_UNSPECIFIED] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void decrementSelectorIndices(int[] selectorIndices) {
        System.arraycopy(selectorIndices, DEFAULT_LAYOUT_RESOURCE_ID, selectorIndices, SELECTOR_MIDDLE_ITEM_INDEX, selectorIndices.length + SIZE_UNSPECIFIED);
        int nextScrollSelectorIndex = selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] + SIZE_UNSPECIFIED;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex < this.mMinValue) {
            nextScrollSelectorIndex = this.mMaxValue;
        }
        selectorIndices[DEFAULT_LAYOUT_RESOURCE_ID] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        SparseArray<String> cache = this.mSelectorIndexToStringCache;
        if (((String) cache.get(selectorIndex)) == null) {
            String scrollSelectorValue;
            if (selectorIndex < this.mMinValue || selectorIndex > this.mMaxValue) {
                scrollSelectorValue = TtmlNode.ANONYMOUS_REGION_ID;
            } else if (this.mDisplayedValues != null) {
                scrollSelectorValue = this.mDisplayedValues[selectorIndex - this.mMinValue];
            } else {
                scrollSelectorValue = formatNumber(selectorIndex);
            }
            cache.put(selectorIndex, scrollSelectorValue);
        }
    }

    private String formatNumber(int value) {
        return this.mFormatter != null ? this.mFormatter.format(value) : formatNumberWithLocale(value);
    }

    private boolean updateInputTextView() {
        String text = this.mDisplayedValues == null ? formatNumber(this.mValue) : this.mDisplayedValues[this.mValue - this.mMinValue];
        if (TextUtils.isEmpty(text) || text.equals(this.mInputText.getText().toString())) {
            return false;
        }
        this.mInputText.setText(text);
        return true;
    }

    private void notifyChange(int previous, int current) {
        if (this.mOnValueChangeListener != null) {
            this.mOnValueChangeListener.onValueChange(this, previous, this.mValue);
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean increment, long delayMillis) {
        if (this.mChangeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(increment);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, delayMillis);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private int getSelectedPos(String value) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return this.mMinValue;
            }
        }
        for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i < this.mDisplayedValues.length; i += SELECTOR_MIDDLE_ITEM_INDEX) {
            value = value.toLowerCase();
            if (this.mDisplayedValues[i].toLowerCase().startsWith(value)) {
                return this.mMinValue + i;
            }
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e2) {
            return this.mMinValue;
        }
    }

    private boolean ensureScrollWheelAdjusted() {
        int deltaY = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (deltaY == 0) {
            return false;
        }
        this.mPreviousScrollerY = DEFAULT_LAYOUT_RESOURCE_ID;
        if (Math.abs(deltaY) > this.mSelectorElementHeight / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT) {
            deltaY += deltaY > 0 ? -this.mSelectorElementHeight : this.mSelectorElementHeight;
        }
        this.mAdjustScroller.startScroll(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, deltaY, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
        invalidate();
        return true;
    }

    private static String formatNumberWithLocale(int value) {
        Object[] objArr = new Object[SELECTOR_MIDDLE_ITEM_INDEX];
        objArr[DEFAULT_LAYOUT_RESOURCE_ID] = Integer.valueOf(value);
        return String.format(Locale.getDefault(), "%d", objArr);
    }
}
