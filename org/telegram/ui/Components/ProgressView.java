package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ProgressView {
    public float currentProgress;
    public int height;
    private Paint innerPaint;
    private Paint outerPaint;
    public float progressHeight;
    public int width;

    public ProgressView() {
        this.currentProgress = 0.0f;
        this.progressHeight = (float) AndroidUtilities.dp(2.0f);
        this.innerPaint = new Paint();
        this.outerPaint = new Paint();
    }

    public void setProgressColors(int innerColor, int outerColor) {
        this.innerPaint.setColor(innerColor);
        this.outerPaint.setColor(outerColor);
    }

    public void setProgress(float progress) {
        this.currentProgress = progress;
        if (this.currentProgress < 0.0f) {
            this.currentProgress = 0.0f;
        } else if (this.currentProgress > TouchHelperCallback.ALPHA_FULL) {
            this.currentProgress = TouchHelperCallback.ALPHA_FULL;
        }
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, ((float) (this.height / 2)) - (this.progressHeight / 2.0f), (float) this.width, (this.progressHeight / 2.0f) + ((float) (this.height / 2)), this.innerPaint);
        canvas2 = canvas;
        canvas2.drawRect(0.0f, ((float) (this.height / 2)) - (this.progressHeight / 2.0f), this.currentProgress * ((float) this.width), (this.progressHeight / 2.0f) + ((float) (this.height / 2)), this.outerPaint);
    }
}
