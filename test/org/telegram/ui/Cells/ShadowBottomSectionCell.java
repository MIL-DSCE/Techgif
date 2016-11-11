package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.exoplayer.C0747C;

public class ShadowBottomSectionCell extends View {
    public ShadowBottomSectionCell(Context context) {
        super(context);
        setBackgroundResource(C0691R.drawable.greydivider_bottom);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(6.0f), C0747C.ENCODING_PCM_32BIT));
    }
}
