package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private Paint paint;
    private RectF rect;

    public CloseProgressDrawable2() {
        this.paint = new Paint(1);
        this.interpolator = new DecelerateInterpolator();
        this.rect = new RectF();
        this.paint.setColor(-5395027);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStyle(Style.STROKE);
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }

    public void draw(Canvas canvas) {
        long newTime = System.currentTimeMillis();
        if (this.lastFrameTime != 0) {
            long dt = newTime - this.lastFrameTime;
            if (this.animating || this.angle != 0.0f) {
                this.angle += ((float) (360 * dt)) / 500.0f;
                if (this.animating || this.angle < 720.0f) {
                    this.angle -= (float) (((int) (this.angle / 720.0f)) * 720);
                } else {
                    this.angle = 0.0f;
                }
                invalidateSelf();
            }
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas.rotate(-45.0f);
        float progress1 = TouchHelperCallback.ALPHA_FULL;
        float progress2 = TouchHelperCallback.ALPHA_FULL;
        float progress3 = TouchHelperCallback.ALPHA_FULL;
        float progress4 = 0.0f;
        if (this.angle >= 0.0f && this.angle < 90.0f) {
            progress1 = TouchHelperCallback.ALPHA_FULL - (this.angle / 90.0f);
        } else if (this.angle >= 90.0f && this.angle < BitmapDescriptorFactory.HUE_CYAN) {
            progress1 = 0.0f;
            progress2 = TouchHelperCallback.ALPHA_FULL - ((this.angle - 90.0f) / 90.0f);
        } else if (this.angle >= BitmapDescriptorFactory.HUE_CYAN && this.angle < BitmapDescriptorFactory.HUE_VIOLET) {
            progress2 = 0.0f;
            progress1 = 0.0f;
            progress3 = TouchHelperCallback.ALPHA_FULL - ((this.angle - BitmapDescriptorFactory.HUE_CYAN) / 90.0f);
        } else if (this.angle >= BitmapDescriptorFactory.HUE_VIOLET && this.angle < 360.0f) {
            progress3 = 0.0f;
            progress2 = 0.0f;
            progress1 = 0.0f;
            progress4 = (this.angle - BitmapDescriptorFactory.HUE_VIOLET) / 90.0f;
        } else if (this.angle >= 360.0f && this.angle < 450.0f) {
            progress3 = 0.0f;
            progress2 = 0.0f;
            progress1 = 0.0f;
            progress4 = TouchHelperCallback.ALPHA_FULL - ((this.angle - 360.0f) / 90.0f);
        } else if (this.angle >= 450.0f && this.angle < 540.0f) {
            progress3 = 0.0f;
            progress2 = 0.0f;
            progress1 = (this.angle - 450.0f) / 90.0f;
        } else if (this.angle >= 540.0f && this.angle < 630.0f) {
            progress3 = 0.0f;
            progress2 = (this.angle - 540.0f) / 90.0f;
        } else if (this.angle >= 630.0f && this.angle < 720.0f) {
            progress3 = (this.angle - 630.0f) / 90.0f;
        }
        if (progress1 != 0.0f) {
            canvas.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * progress1, this.paint);
        }
        if (progress2 != 0.0f) {
            canvas.drawLine(((float) (-AndroidUtilities.dp(8.0f))) * progress2, 0.0f, 0.0f, 0.0f, this.paint);
        }
        if (progress3 != 0.0f) {
            canvas.drawLine(0.0f, ((float) (-AndroidUtilities.dp(8.0f))) * progress3, 0.0f, 0.0f, this.paint);
        }
        if (progress4 != TouchHelperCallback.ALPHA_FULL) {
            canvas.drawLine(((float) AndroidUtilities.dp(8.0f)) * progress4, 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        }
        canvas.restore();
        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        this.rect.set((float) (cx - AndroidUtilities.dp(8.0f)), (float) (cy - AndroidUtilities.dp(8.0f)), (float) (AndroidUtilities.dp(8.0f) + cx), (float) (AndroidUtilities.dp(8.0f) + cy));
        canvas.drawArc(this.rect, (this.angle < 360.0f ? 0.0f : this.angle - 360.0f) - 45.0f, this.angle < 360.0f ? this.angle : 720.0f - this.angle, false, this.paint);
        this.lastFrameTime = newTime;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
