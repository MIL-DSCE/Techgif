package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class TextDetailSettingsCell extends FrameLayout {
    private static Paint paint;
    private boolean multiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public TextDetailSettingsCell(Context context) {
        int i;
        int i2;
        int i3 = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(TouchHelperCallback.ALPHA_FULL);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, 17.0f, 10.0f, 17.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        view = this.valueTextView;
        if (!LocaleController.isRTL) {
            i3 = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i3 | 48, 17.0f, 35.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = 0;
        if (this.multiline) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, 0));
            return;
        }
        int dp = AndroidUtilities.dp(64.0f);
        if (this.needDivider) {
            i = 1;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(i + dp, C0747C.ENCODING_PCM_32BIT));
    }

    public void setMultilineDetail(boolean value) {
        this.multiline = value;
        if (value) {
            this.valueTextView.setLines(0);
            this.valueTextView.setMaxLines(0);
            this.valueTextView.setSingleLine(false);
            this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
            return;
        }
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }
}
