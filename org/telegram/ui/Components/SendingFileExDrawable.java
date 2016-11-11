package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class SendingFileExDrawable extends Drawable {
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float progress;
    private boolean started;

    public SendingFileExDrawable() {
        this.isChat = false;
        this.paint = new Paint(1);
        this.lastUpdateTime = 0;
        this.started = false;
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
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
        this.progress += ((float) dt) / 500.0f;
        while (this.progress > TouchHelperCallback.ALPHA_FULL) {
            this.progress -= TouchHelperCallback.ALPHA_FULL;
        }
        invalidateSelf();
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
        for (int a = 0; a < 3; a++) {
            float f;
            if (a == 0) {
                this.paint.setAlpha((int) (this.progress * 255.0f));
            } else if (a == 2) {
                this.paint.setAlpha((int) ((TouchHelperCallback.ALPHA_FULL - this.progress) * 255.0f));
            } else {
                this.paint.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            float side = ((float) (AndroidUtilities.dp(5.0f) * a)) + (((float) AndroidUtilities.dp(5.0f)) * this.progress);
            if (this.isChat) {
                f = 3.0f;
            } else {
                f = 4.0f;
            }
            canvas.drawLine(side, (float) AndroidUtilities.dp(f), side + ((float) AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(this.isChat ? 7.0f : 8.0f), this.paint);
            if (this.isChat) {
                f = 11.0f;
            } else {
                f = 12.0f;
            }
            float dp = (float) AndroidUtilities.dp(f);
            float dp2 = side + ((float) AndroidUtilities.dp(4.0f));
            if (this.isChat) {
                f = 7.0f;
            } else {
                f = 8.0f;
            }
            canvas.drawLine(side, dp, dp2, (float) AndroidUtilities.dp(f), this.paint);
        }
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
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}
