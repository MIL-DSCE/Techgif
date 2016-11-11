package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ProgressCircleView extends View {
    private Paint paint;

    public ProgressCircleView(Context context) {
        super(context);
        this.paint = new Paint(1);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
