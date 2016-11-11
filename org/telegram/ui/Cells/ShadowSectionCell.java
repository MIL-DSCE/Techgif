package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.exoplayer.C0747C;

public class ShadowSectionCell extends View {
    private int size;

    public ShadowSectionCell(Context context) {
        super(context);
        this.size = 12;
        setBackgroundResource(C0691R.drawable.greydivider);
    }

    public void setSize(int value) {
        this.size = value;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.size), C0747C.ENCODING_PCM_32BIT));
    }
}
