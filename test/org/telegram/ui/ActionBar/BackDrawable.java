package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class BackDrawable extends Drawable {
    private boolean alwaysClose;
    private boolean animationInProgress;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private Paint paint;
    private boolean reverseAngle;

    public BackDrawable(boolean close) {
        this.paint = new Paint(1);
        this.reverseAngle = false;
        this.interpolator = new DecelerateInterpolator();
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.alwaysClose = close;
    }

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0;
        if (this.currentRotation == TouchHelperCallback.ALPHA_FULL) {
            this.reverseAngle = true;
        } else if (this.currentRotation == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (animated) {
            if (this.currentRotation < rotation) {
                this.currentAnimationTime = (int) (this.currentRotation * BitmapDescriptorFactory.HUE_MAGENTA);
            } else {
                this.currentAnimationTime = (int) ((TouchHelperCallback.ALPHA_FULL - this.currentRotation) * BitmapDescriptorFactory.HUE_MAGENTA);
            }
            this.lastFrameTime = System.currentTimeMillis();
            this.finalRotation = rotation;
        } else {
            this.currentRotation = rotation;
            this.finalRotation = rotation;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                if (this.currentAnimationTime >= 300) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) this.currentAnimationTime) / BitmapDescriptorFactory.HUE_MAGENTA) * this.finalRotation;
                } else {
                    this.currentRotation = TouchHelperCallback.ALPHA_FULL - this.interpolator.getInterpolation(((float) this.currentAnimationTime) / BitmapDescriptorFactory.HUE_MAGENTA);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int rD = (int) (-138.0f * this.currentRotation);
        this.paint.setColor(Color.rgb(rD + NalUnitUtil.EXTENDED_SAR, rD + NalUnitUtil.EXTENDED_SAR, rD + NalUnitUtil.EXTENDED_SAR));
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        float rotation = this.currentRotation;
        if (this.alwaysClose) {
            canvas.rotate((((float) (this.reverseAngle ? -180 : 180)) * this.currentRotation) + 135.0f);
            rotation = TouchHelperCallback.ALPHA_FULL;
        } else {
            canvas.rotate(((float) (this.reverseAngle ? -225 : 135)) * this.currentRotation);
        }
        canvas.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)) * rotation), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        float startYDiff = (float) (-AndroidUtilities.dp(0.5f));
        float endYDiff = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL)) * rotation);
        float startXDiff = ((float) (-AndroidUtilities.dp(7.0f))) + (((float) AndroidUtilities.dp(7.0f)) * rotation);
        float endXDiff = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * rotation);
        canvas.drawLine(startXDiff, -startYDiff, endXDiff, -endYDiff, this.paint);
        canvas.drawLine(startXDiff, startYDiff, endXDiff, endYDiff, this.paint);
        canvas.restore();
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
