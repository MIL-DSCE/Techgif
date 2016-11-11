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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class SendingFileDrawable extends Drawable {
    private static DecelerateInterpolator decelerateInterpolator;
    private float animatedProgressValue;
    private float animationProgressStart;
    private RectF cicleRect;
    private float currentProgress;
    private long currentProgressTime;
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float radOffset;
    private boolean started;

    static {
        decelerateInterpolator = null;
    }

    public SendingFileDrawable() {
        this.radOffset = 0.0f;
        this.currentProgress = 0.0f;
        this.animationProgressStart = 0.0f;
        this.currentProgressTime = 0;
        this.animatedProgressValue = 0.0f;
        this.cicleRect = new RectF();
        this.isChat = false;
        this.paint = new Paint(1);
        this.lastUpdateTime = 0;
        this.started = false;
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        decelerateInterpolator = new DecelerateInterpolator();
    }

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    public void setProgress(float value, boolean animated) {
        if (animated) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        }
        this.currentProgress = value;
        this.currentProgressTime = 0;
        invalidateSelf();
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (this.animatedProgressValue != TouchHelperCallback.ALPHA_FULL) {
            this.radOffset += ((float) (360 * dt)) / 1000.0f;
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
            invalidateSelf();
        }
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        this.started = false;
    }

    public void draw(Canvas canvas) {
        this.cicleRect.set((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), (float) AndroidUtilities.dp(this.isChat ? 3.0f : 4.0f), (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(this.isChat ? 11.0f : 12.0f));
        canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(BitmapDescriptorFactory.HUE_YELLOW, 360.0f * this.animatedProgressValue), false, this.paint);
        if (this.started) {
            update();
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(14.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}
