package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.Emoji;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class BotKeyboardView extends LinearLayout {
    private TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<TextView> buttonViews;
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;

    /* renamed from: org.telegram.ui.Components.BotKeyboardView.1 */
    class C11081 implements OnClickListener {
        C11081() {
        }

        public void onClick(View v) {
            BotKeyboardView.this.delegate.didPressedButton((KeyboardButton) v.getTag());
        }
    }

    public interface BotKeyboardViewDelegate {
        void didPressedButton(KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context) {
        super(context);
        this.buttonViews = new ArrayList();
        setOrientation(1);
        ScrollView scrollView = new ScrollView(context);
        addView(scrollView);
        this.container = new LinearLayout(context);
        this.container.setOrientation(1);
        scrollView.addView(this.container);
        setBackgroundColor(-657673);
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int height) {
        this.panelHeight = height;
        if (this.isFullSize && this.botButtons != null && this.botButtons.rows.size() != 0) {
            int max;
            if (this.isFullSize) {
                max = (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            } else {
                max = 42;
            }
            this.buttonHeight = max;
            int count = this.container.getChildCount();
            int newHeight = AndroidUtilities.dp((float) this.buttonHeight);
            for (int a = 0; a < count; a++) {
                View v = this.container.getChildAt(a);
                LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
                if (layoutParams.height != newHeight) {
                    layoutParams.height = newHeight;
                    v.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int a = 0; a < this.buttonViews.size(); a++) {
            ((TextView) this.buttonViews.get(a)).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TL_replyKeyboardMarkup buttons) {
        this.botButtons = buttons;
        this.container.removeAllViews();
        this.buttonViews.clear();
        if (buttons != null && this.botButtons.rows.size() != 0) {
            this.isFullSize = !buttons.resize;
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int a = 0;
            while (a < buttons.rows.size()) {
                float f;
                TL_keyboardButtonRow row = (TL_keyboardButtonRow) buttons.rows.get(a);
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(0);
                LinearLayout linearLayout = this.container;
                int i = this.buttonHeight;
                float f2 = a == 0 ? 15.0f : 10.0f;
                if (a == buttons.rows.size() - 1) {
                    f = 15.0f;
                } else {
                    f = 0.0f;
                }
                linearLayout.addView(layout, LayoutHelper.createLinear(-1, i, 15.0f, f2, 15.0f, f));
                float weight = TouchHelperCallback.ALPHA_FULL / ((float) row.buttons.size());
                int b = 0;
                while (b < row.buttons.size()) {
                    KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                    TextView textView = new TextView(getContext());
                    textView.setTag(button);
                    textView.setTextColor(-13220017);
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundResource(C0691R.drawable.bot_keyboard_states);
                    textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                    textView.setText(Emoji.replaceEmoji(button.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                    layout.addView(textView, LayoutHelper.createLinear(0, -1, weight, 0, 0, b != row.buttons.size() + -1 ? 10 : 0, 0));
                    textView.setOnClickListener(new C11081());
                    this.buttonViews.add(textView);
                    b++;
                }
                a++;
            }
        }
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : ((this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }
}
