package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class TypingDotsDrawable extends Drawable {
    private DecelerateInterpolator decelerateInterpolator;
    private float[] elapsedTimes;
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float[] scales;
    private float[] startTimes;
    private boolean started;

    /* renamed from: org.telegram.ui.Components.TypingDotsDrawable.1 */
    class C11941 implements Runnable {
        C11941() {
        }

        public void run() {
            TypingDotsDrawable.this.checkUpdate();
        }
    }

    public TypingDotsDrawable() {
        this.isChat = false;
        this.paint = new Paint(1);
        this.scales = new float[3];
        this.startTimes = new float[]{0.0f, 150.0f, BitmapDescriptorFactory.HUE_MAGENTA};
        this.elapsedTimes = new float[]{0.0f, 0.0f, 0.0f};
        this.lastUpdateTime = 0;
        this.started = false;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
    }

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 50) {
            dt = 50;
        }
        for (int a = 0; a < 3; a++) {
            float[] fArr = this.elapsedTimes;
            fArr[a] = fArr[a] + ((float) dt);
            float timeSinceStart = this.elapsedTimes[a] - this.startTimes[a];
            if (timeSinceStart <= 0.0f) {
                this.scales[a] = 1.33f;
            } else if (timeSinceStart <= 320.0f) {
                this.scales[a] = 1.33f + this.decelerateInterpolator.getInterpolation(timeSinceStart / 320.0f);
            } else if (timeSinceStart <= 640.0f) {
                this.scales[a] = (TouchHelperCallback.ALPHA_FULL - this.decelerateInterpolator.getInterpolation((timeSinceStart - 320.0f) / 320.0f)) + 1.33f;
            } else if (timeSinceStart >= 800.0f) {
                this.elapsedTimes[a] = 0.0f;
                this.startTimes[a] = 0.0f;
                this.scales[a] = 1.33f;
            } else {
                this.scales[a] = 1.33f;
            }
        }
        invalidateSelf();
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        for (int a = 0; a < 3; a++) {
            this.elapsedTimes[a] = 0.0f;
            this.scales[a] = 1.33f;
        }
        this.startTimes[0] = 0.0f;
        this.startTimes[1] = 150.0f;
        this.startTimes[2] = BitmapDescriptorFactory.HUE_MAGENTA;
        this.started = false;
    }

    public void draw(Canvas canvas) {
        int y;
        if (this.isChat) {
            y = AndroidUtilities.dp(8.5f) + getBounds().top;
        } else {
            y = AndroidUtilities.dp(9.3f) + getBounds().top;
        }
        canvas.drawCircle((float) AndroidUtilities.dp(3.0f), (float) y, this.scales[0] * AndroidUtilities.density, this.paint);
        canvas.drawCircle((float) AndroidUtilities.dp(9.0f), (float) y, this.scales[1] * AndroidUtilities.density, this.paint);
        canvas.drawCircle((float) AndroidUtilities.dp(15.0f), (float) y, this.scales[2] * AndroidUtilities.density, this.paint);
        checkUpdate();
    }

    private void checkUpdate() {
        if (!this.started) {
            return;
        }
        if (NotificationCenter.getInstance().isAnimationInProgress()) {
            AndroidUtilities.runOnUIThread(new C11941(), 100);
        } else {
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
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
    }
}
