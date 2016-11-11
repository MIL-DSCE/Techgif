package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ChatUnreadCell extends FrameLayout {
    private TextView textView;

    public ChatUnreadCell(Context context) {
        super(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundResource(C0691R.drawable.newmsg_divider);
        addView(frameLayout, LayoutHelper.createFrame(-1, 27.0f, 51, 0.0f, 7.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(C0691R.drawable.ic_ab_new);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        this.textView = new TextView(context);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.CHAT_UNREAD_TEXT_COLOR);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), C0747C.ENCODING_PCM_32BIT));
    }
}
