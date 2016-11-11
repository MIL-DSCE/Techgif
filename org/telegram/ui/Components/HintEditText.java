package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.EditText;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class HintEditText extends EditText {
    private String hintText;
    private float numberSize;
    private Paint paint;
    private Rect rect;
    private float spaceSize;
    private float textOffset;

    public HintEditText(Context context) {
        super(context);
        this.paint = new Paint();
        this.rect = new Rect();
        this.paint.setColor(-6842473);
    }

    public String getHintText() {
        return this.hintText;
    }

    public void setHintText(String value) {
        this.hintText = value;
        onTextChange();
        setText(getText());
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onTextChange();
    }

    public void onTextChange() {
        this.textOffset = length() > 0 ? getPaint().measureText(getText(), 0, length()) : 0.0f;
        this.spaceSize = getPaint().measureText(" ");
        this.numberSize = getPaint().measureText("1");
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.hintText != null && length() < this.hintText.length()) {
            int top = getMeasuredHeight() / 2;
            float offsetX = this.textOffset;
            for (int a = length(); a < this.hintText.length(); a++) {
                if (this.hintText.charAt(a) == ' ') {
                    offsetX += this.spaceSize;
                } else {
                    this.rect.set(((int) offsetX) + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), top, ((int) (this.numberSize + offsetX)) - AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), AndroidUtilities.dp(2.0f) + top);
                    canvas.drawRect(this.rect, this.paint);
                    offsetX += this.numberSize;
                }
            }
        }
    }
}
