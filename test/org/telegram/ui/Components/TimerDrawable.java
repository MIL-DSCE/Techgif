package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class TimerDrawable extends Drawable {
    private static Drawable emptyTimerDrawable;
    private static TextPaint timePaint;
    private static Drawable timerDrawable;
    private int time;
    private int timeHeight;
    private StaticLayout timeLayout;
    private float timeWidth;

    public TimerDrawable(Context context) {
        this.timeWidth = 0.0f;
        this.timeHeight = 0;
        this.time = 0;
        if (emptyTimerDrawable == null) {
            emptyTimerDrawable = context.getResources().getDrawable(C0691R.drawable.header_timer);
            timerDrawable = context.getResources().getDrawable(C0691R.drawable.header_timer2);
            timePaint = new TextPaint(1);
            timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
            timePaint.setColor(-1);
            timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }
    }

    public void setTime(int value) {
        String timeString;
        this.time = value;
        if (this.time >= 1 && this.time < 60) {
            timeString = TtmlNode.ANONYMOUS_REGION_ID + value;
            if (timeString.length() < 2) {
                timeString = timeString + "s";
            }
        } else if (this.time >= 60 && this.time < 3600) {
            timeString = TtmlNode.ANONYMOUS_REGION_ID + (value / 60);
            if (timeString.length() < 2) {
                timeString = timeString + "m";
            }
        } else if (this.time >= 3600 && this.time < 86400) {
            timeString = TtmlNode.ANONYMOUS_REGION_ID + ((value / 60) / 60);
            if (timeString.length() < 2) {
                timeString = timeString + "h";
            }
        } else if (this.time < 86400 || this.time >= 604800) {
            timeString = TtmlNode.ANONYMOUS_REGION_ID + ((((value / 60) / 60) / 24) / 7);
            if (timeString.length() < 2) {
                timeString = timeString + "w";
            } else if (timeString.length() > 2) {
                timeString = "c";
            }
        } else {
            timeString = TtmlNode.ANONYMOUS_REGION_ID + (((value / 60) / 60) / 24);
            if (timeString.length() < 2) {
                timeString = timeString + "d";
            }
        }
        this.timeWidth = timePaint.measureText(timeString);
        try {
            this.timeLayout = new StaticLayout(timeString, timePaint, (int) Math.ceil((double) this.timeWidth), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            this.timeHeight = this.timeLayout.getHeight();
        } catch (Throwable e) {
            this.timeLayout = null;
            FileLog.m13e("tmessages", e);
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        Drawable drawable;
        int width = timerDrawable.getIntrinsicWidth();
        int height = timerDrawable.getIntrinsicHeight();
        if (this.time == 0) {
            drawable = timerDrawable;
        } else {
            drawable = emptyTimerDrawable;
        }
        int x = (width - drawable.getIntrinsicWidth()) / 2;
        int y = (height - drawable.getIntrinsicHeight()) / 2;
        drawable.setBounds(x, y, drawable.getIntrinsicWidth() + x, drawable.getIntrinsicHeight() + y);
        drawable.draw(canvas);
        if (this.time != 0 && this.timeLayout != null) {
            int xOffxet = 0;
            if (AndroidUtilities.density == 3.0f) {
                xOffxet = -1;
            }
            canvas.translate((float) (((int) (((double) (width / 2)) - Math.ceil((double) (this.timeWidth / 2.0f)))) + xOffxet), (float) ((height - this.timeHeight) / 2));
            this.timeLayout.draw(canvas);
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
        return timerDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return timerDrawable.getIntrinsicHeight();
    }
}
