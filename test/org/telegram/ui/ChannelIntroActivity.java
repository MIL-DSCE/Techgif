package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class ChannelIntroActivity extends BaseFragment {
    private TextView createChannelText;
    private TextView descriptionText;
    private ImageView imageView;
    private TextView whatIsChannelText;

    /* renamed from: org.telegram.ui.ChannelIntroActivity.2 */
    class C10612 extends ViewGroup {
        C10612(Context x0) {
            super(x0);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (width > height) {
                ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.45f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.78f), C0747C.ENCODING_PCM_32BIT));
                ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, 0));
                ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.5f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, 0));
                ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C0747C.ENCODING_PCM_32BIT));
            } else {
                ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(width, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.44f), C0747C.ENCODING_PCM_32BIT));
                ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec(width, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, 0));
                ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.9f), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(height, 0));
                ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec(width, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C0747C.ENCODING_PCM_32BIT));
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int width = r - l;
            int height = b - t;
            if (r > b) {
                int y = (int) (((float) height) * 0.05f);
                ChannelIntroActivity.this.imageView.layout(0, y, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + y);
                int x = (int) (((float) width) * 0.4f);
                y = (int) (((float) height) * 0.14f);
                ChannelIntroActivity.this.whatIsChannelText.layout(x, y, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + x, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + y);
                y = (int) (((float) height) * 0.61f);
                ChannelIntroActivity.this.createChannelText.layout(x, y, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + x, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + y);
                x = (int) (((float) width) * 0.45f);
                y = (int) (((float) height) * 0.31f);
                ChannelIntroActivity.this.descriptionText.layout(x, y, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + x, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + y);
                return;
            }
            y = (int) (((float) height) * 0.05f);
            ChannelIntroActivity.this.imageView.layout(0, y, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + y);
            y = (int) (((float) height) * 0.59f);
            ChannelIntroActivity.this.whatIsChannelText.layout(0, y, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + y);
            y = (int) (((float) height) * 0.68f);
            x = (int) (((float) width) * 0.05f);
            ChannelIntroActivity.this.descriptionText.layout(x, y, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + x, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + y);
            y = (int) (((float) height) * 0.86f);
            ChannelIntroActivity.this.createChannelText.layout(0, y, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + y);
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity.3 */
    class C10623 implements OnTouchListener {
        C10623() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity.4 */
    class C10634 implements OnClickListener {
        C10634() {
        }

        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putInt("step", 0);
            ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(args), true);
        }
    }

    /* renamed from: org.telegram.ui.ChannelIntroActivity.1 */
    class C17891 extends ActionBarMenuOnItemClick {
        C17891() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelIntroActivity.this.finishFragment();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_CHANNEL_INTRO_COLOR);
        this.actionBar.setBackButtonImage(C0691R.drawable.pl_back);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new C17891());
        this.fragmentView = new C10612(context);
        this.fragmentView.setBackgroundColor(-1);
        ViewGroup viewGroup = this.fragmentView;
        viewGroup.setOnTouchListener(new C10623());
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(C0691R.drawable.channelintro);
        this.imageView.setScaleType(ScaleType.FIT_CENTER);
        viewGroup.addView(this.imageView);
        this.whatIsChannelText = new TextView(context);
        this.whatIsChannelText.setTextColor(-14606047);
        this.whatIsChannelText.setGravity(1);
        this.whatIsChannelText.setTextSize(1, 24.0f);
        this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", C0691R.string.ChannelAlertTitle));
        viewGroup.addView(this.whatIsChannelText);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(-8882056);
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("ChannelAlertText", C0691R.string.ChannelAlertText));
        viewGroup.addView(this.descriptionText);
        this.createChannelText = new TextView(context);
        this.createChannelText.setTextColor(-11759926);
        this.createChannelText.setGravity(17);
        this.createChannelText.setTextSize(1, 16.0f);
        this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", C0691R.string.ChannelAlertCreate));
        viewGroup.addView(this.createChannelText);
        this.createChannelText.setOnClickListener(new C10634());
        return this.fragmentView;
    }
}
