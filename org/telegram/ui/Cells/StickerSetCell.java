package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class StickerSetCell extends FrameLayout {
    private static Paint paint;
    private BackupImageView imageView;
    private boolean needDivider;
    private ImageView optionsButton;
    private Rect rect;
    private TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public StickerSetCell(Context context) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        this.rect = new Rect();
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 40.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2);
        view = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        View view2 = this.imageView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view2, LayoutHelper.createFrame(48, 48.0f, i | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR));
        this.optionsButton.setImageResource(C0691R.drawable.doc_actions_b);
        this.optionsButton.setScaleType(ScaleType.CENTER);
        View view3 = this.optionsButton;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(view3, LayoutHelper.createFrame(40, 40, i3 | 48));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), C0747C.ENCODING_PCM_32BIT));
    }

    public void setStickersSet(TL_messages_stickerSet set, boolean divider) {
        this.needDivider = divider;
        this.stickersSet = set;
        this.textView.setText(this.stickersSet.set.title);
        if (this.stickersSet.set.disabled) {
            this.textView.setAlpha(0.5f);
            this.valueTextView.setAlpha(0.5f);
            this.imageView.setAlpha(0.5f);
        } else {
            this.textView.setAlpha(TouchHelperCallback.ALPHA_FULL);
            this.valueTextView.setAlpha(TouchHelperCallback.ALPHA_FULL);
            this.imageView.setAlpha(TouchHelperCallback.ALPHA_FULL);
        }
        ArrayList<Document> documents = set.documents;
        if (documents == null || documents.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
            return;
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", documents.size()));
        Document document = (Document) documents.get(0);
        if (document.thumb != null && document.thumb.location != null) {
            this.imageView.setImage(document.thumb.location, null, "webp", null);
        }
    }

    public void setOnOptionsClick(OnClickListener listener) {
        this.optionsButton.setOnClickListener(listener);
    }

    public TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            this.optionsButton.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
            if (event.getAction() == 0 || event.getAction() == 2) {
                getBackground().setHotspot(event.getX(), event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }
}
