package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class LineProgressView extends View {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;
    private float animatedAlphaValue;
    private float animatedProgressValue;
    private float animationProgressStart;
    private int backColor;
    private float currentProgress;
    private long currentProgressTime;
    private long lastUpdateTime;
    private int progressColor;

    static {
        decelerateInterpolator = null;
        progressPaint = null;
    }

    public LineProgressView(Context context) {
        super(context);
        this.lastUpdateTime = 0;
        this.currentProgress = 0.0f;
        this.animationProgressStart = 0.0f;
        this.currentProgressTime = 0;
        this.animatedProgressValue = 0.0f;
        this.animatedAlphaValue = TouchHelperCallback.ALPHA_FULL;
        this.progressColor = -13196562;
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
            progressPaint = new Paint(1);
            progressPaint.setStyle(Style.STROKE);
            progressPaint.setStrokeCap(Cap.ROUND);
            progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    private void updateAnimation() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (!(this.animatedProgressValue == TouchHelperCallback.ALPHA_FULL || this.animatedProgressValue == this.currentProgress)) {
            float progressDiff = this.currentProgress - this.animationProgressStart;
            if (progressDiff > 0.0f) {
                this.currentProgressTime += dt;
                if (this.currentProgressTime >= 300) {
                    this.animatedProgressValue = this.currentProgress;
                    this.animationProgressStart = this.currentProgress;
                    this.currentProgressTime = 0;
                } else {
                    this.animatedProgressValue = this.animationProgressStart + (decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA) * progressDiff);
                }
            }
            invalidate();
        }
        if (this.animatedProgressValue >= TouchHelperCallback.ALPHA_FULL && this.animatedProgressValue == TouchHelperCallback.ALPHA_FULL && this.animatedAlphaValue != 0.0f) {
            this.animatedAlphaValue -= ((float) dt) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
            }
            invalidate();
        }
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public void setBackColor(int color) {
        this.backColor = color;
    }

    public void setProgress(float value, boolean animated) {
        if (animated) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        }
        if (value != TouchHelperCallback.ALPHA_FULL) {
            this.animatedAlphaValue = TouchHelperCallback.ALPHA_FULL;
        }
        this.currentProgress = value;
        this.currentProgressTime = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        if (!(this.backColor == 0 || this.animatedProgressValue == TouchHelperCallback.ALPHA_FULL)) {
            progressPaint.setColor(this.backColor);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            canvas.drawRect((float) ((int) (((float) getWidth()) * this.animatedProgressValue)), 0.0f, (float) getWidth(), (float) getHeight(), progressPaint);
        }
        progressPaint.setColor(this.progressColor);
        progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
        canvas.drawRect(0.0f, 0.0f, ((float) getWidth()) * this.animatedProgressValue, (float) getHeight(), progressPaint);
        updateAnimation();
    }
}
