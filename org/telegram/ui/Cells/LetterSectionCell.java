package org.telegram.ui.Cells;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class LetterSectionCell extends FrameLayout {
    private TextView textView;

    public LetterSectionCell(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(AndroidUtilities.dp(54.0f), AndroidUtilities.dp(64.0f)));
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 22.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(-8355712);
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
    }

    public void setLetter(String letter) {
        this.textView.setText(letter.toUpperCase());
    }

    public void setCellHeight(int height) {
        setLayoutParams(new LayoutParams(AndroidUtilities.dp(54.0f), height));
    }
}
