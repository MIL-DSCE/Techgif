package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class RadioButton extends View {
    private static Paint checkedPaint;
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private int checkedColor;
    private int color;
    private boolean isChecked;
    private float progress;
    private int size;

    public RadioButton(Context context) {
        super(context);
        this.checkedColor = Theme.ACTION_BAR_SUBTITLE_COLOR;
        this.color = Theme.ACTION_BAR_SUBTITLE_COLOR;
        this.size = AndroidUtilities.dp(16.0f);
        if (paint == null) {
            paint = new Paint(1);
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            paint.setStyle(Style.STROKE);
            checkedPaint = new Paint(1);
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        }
        try {
            this.bitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
            this.bitmapCanvas = new Canvas(this.bitmap);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setSize(int value) {
        if (this.size != value) {
            this.size = value;
        }
    }

    public void setColor(int color1, int color2) {
        this.color = color1;
        this.checkedColor = color2;
        invalidate();
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        String str = NotificationCompatApi21.CATEGORY_PROGRESS;
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? TouchHelperCallback.ALPHA_FULL : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(200);
        this.checkAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (this.attachedToWindow && animated) {
                animateToCheckedState(checked);
                return;
            }
            cancelCheckAnimator();
            setProgress(checked ? TouchHelperCallback.ALPHA_FULL : 0.0f);
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        float circleProgress;
        if (this.bitmap == null || this.bitmap.getWidth() != getMeasuredWidth()) {
            if (this.bitmap != null) {
                this.bitmap.recycle();
            }
            try {
                this.bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
                this.bitmapCanvas = new Canvas(this.bitmap);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        if (this.progress <= 0.5f) {
            paint.setColor(this.color);
            checkedPaint.setColor(this.color);
            circleProgress = this.progress / 0.5f;
        } else {
            circleProgress = 2.0f - (this.progress / 0.5f);
            int r1 = Color.red(this.color);
            int rD = (int) (((float) (Color.red(this.checkedColor) - r1)) * (TouchHelperCallback.ALPHA_FULL - circleProgress));
            int g1 = Color.green(this.color);
            int gD = (int) (((float) (Color.green(this.checkedColor) - g1)) * (TouchHelperCallback.ALPHA_FULL - circleProgress));
            int b1 = Color.blue(this.color);
            int c = Color.rgb(r1 + rD, g1 + gD, b1 + ((int) (((float) (Color.blue(this.checkedColor) - b1)) * (TouchHelperCallback.ALPHA_FULL - circleProgress))));
            paint.setColor(c);
            checkedPaint.setColor(c);
        }
        if (this.bitmap != null) {
            this.bitmap.eraseColor(0);
            float rad = ((float) (this.size / 2)) - ((TouchHelperCallback.ALPHA_FULL + circleProgress) * AndroidUtilities.density);
            this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad, paint);
            if (this.progress <= 0.5f) {
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad - ((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)), checkedPaint);
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (rad - ((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL))) * (TouchHelperCallback.ALPHA_FULL - circleProgress), eraser);
            } else {
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), ((float) (this.size / 4)) + (((rad - ((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL))) - ((float) (this.size / 4))) * circleProgress), checkedPaint);
            }
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, null);
        }
    }
}
