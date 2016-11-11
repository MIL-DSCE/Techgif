package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class RadialProgress {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;
    private boolean alphaForPrevious;
    private float animatedAlphaValue;
    private float animatedProgressValue;
    private float animationProgressStart;
    private RectF cicleRect;
    private Drawable currentDrawable;
    private float currentProgress;
    private long currentProgressTime;
    private boolean currentWithRound;
    private boolean hideCurrentDrawable;
    private long lastUpdateTime;
    private View parent;
    private Drawable previousDrawable;
    private boolean previousWithRound;
    private int progressColor;
    private RectF progressRect;
    private float radOffset;

    public RadialProgress(View parentView) {
        this.lastUpdateTime = 0;
        this.radOffset = 0.0f;
        this.currentProgress = 0.0f;
        this.animationProgressStart = 0.0f;
        this.currentProgressTime = 0;
        this.animatedProgressValue = 0.0f;
        this.progressRect = new RectF();
        this.cicleRect = new RectF();
        this.animatedAlphaValue = TouchHelperCallback.ALPHA_FULL;
        this.progressColor = -1;
        this.alphaForPrevious = true;
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
            progressPaint = new Paint(1);
            progressPaint.setStyle(Style.STROKE);
            progressPaint.setStrokeCap(Cap.ROUND);
            progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        }
        this.parent = parentView;
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        this.progressRect.set((float) left, (float) top, (float) right, (float) bottom);
    }

    public void setAlphaForPrevious(boolean value) {
        this.alphaForPrevious = value;
    }

    private void updateAnimation(boolean progress) {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (progress) {
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
                        this.animatedProgressValue = this.animationProgressStart + (decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA) * progressDiff);
                    }
                }
                invalidateParent();
            }
            if (this.animatedProgressValue >= TouchHelperCallback.ALPHA_FULL && this.previousDrawable != null) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousDrawable = null;
                }
                invalidateParent();
            }
        } else if (this.previousDrawable != null) {
            this.animatedAlphaValue -= ((float) dt) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
                this.previousDrawable = null;
            }
            invalidateParent();
        }
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public void setHideCurrentDrawable(boolean value) {
        this.hideCurrentDrawable = value;
    }

    public void setProgress(float value, boolean animated) {
        if (!(value == TouchHelperCallback.ALPHA_FULL || this.animatedAlphaValue == 0.0f || this.previousDrawable == null)) {
            this.animatedAlphaValue = 0.0f;
            this.previousDrawable = null;
        }
        if (animated) {
            if (this.animatedProgressValue > value) {
                this.animatedProgressValue = value;
            }
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        }
        this.currentProgress = value;
        this.currentProgressTime = 0;
        invalidateParent();
    }

    private void invalidateParent() {
        int offset = AndroidUtilities.dp(2.0f);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public void setBackground(Drawable drawable, boolean withRound, boolean animated) {
        this.lastUpdateTime = System.currentTimeMillis();
        if (!animated || this.currentDrawable == drawable) {
            this.previousDrawable = null;
            this.previousWithRound = false;
        } else {
            this.previousDrawable = this.currentDrawable;
            this.previousWithRound = this.currentWithRound;
            this.animatedAlphaValue = TouchHelperCallback.ALPHA_FULL;
            setProgress(TouchHelperCallback.ALPHA_FULL, animated);
        }
        this.currentWithRound = withRound;
        this.currentDrawable = drawable;
        if (animated) {
            invalidateParent();
        } else {
            this.parent.invalidate();
        }
    }

    public boolean swapBackground(Drawable drawable) {
        if (this.currentDrawable == drawable) {
            return false;
        }
        this.currentDrawable = drawable;
        return true;
    }

    public float getAlpha() {
        return (this.previousDrawable == null && this.currentDrawable == null) ? 0.0f : this.animatedAlphaValue;
    }

    public void draw(Canvas canvas) {
        if (this.previousDrawable != null) {
            if (this.alphaForPrevious) {
                this.previousDrawable.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            } else {
                this.previousDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            this.previousDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
            this.previousDrawable.draw(canvas);
        }
        if (!(this.hideCurrentDrawable || this.currentDrawable == null)) {
            if (this.previousDrawable != null) {
                this.currentDrawable.setAlpha((int) ((TouchHelperCallback.ALPHA_FULL - this.animatedAlphaValue) * 255.0f));
            } else {
                this.currentDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            this.currentDrawable.setBounds((int) this.progressRect.left, (int) this.progressRect.top, (int) this.progressRect.right, (int) this.progressRect.bottom);
            this.currentDrawable.draw(canvas);
        }
        if (this.currentWithRound || this.previousWithRound) {
            int diff = AndroidUtilities.dp(4.0f);
            progressPaint.setColor(this.progressColor);
            if (this.previousWithRound) {
                progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            } else {
                progressPaint.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            this.cicleRect.set(this.progressRect.left + ((float) diff), this.progressRect.top + ((float) diff), this.progressRect.right - ((float) diff), this.progressRect.bottom - ((float) diff));
            canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, progressPaint);
            updateAnimation(true);
            return;
        }
        updateAnimation(false);
    }
}
