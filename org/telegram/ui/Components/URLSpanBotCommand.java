package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanBotCommand extends URLSpanNoUnderline {
    public static boolean enabled;

    static {
        enabled = true;
    }

    public URLSpanBotCommand(String url) {
        super(url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(enabled ? Theme.MSG_LINK_TEXT_COLOR : Theme.MSG_TEXT_COLOR);
        ds.setUnderlineText(false);
    }
}
