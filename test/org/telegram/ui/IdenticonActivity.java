package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanReplacement;

public class IdenticonActivity extends BaseFragment {
    private int chat_id;

    /* renamed from: org.telegram.ui.IdenticonActivity.2 */
    class C12492 implements OnTouchListener {
        C12492() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.IdenticonActivity.3 */
    class C12503 implements OnPreDrawListener {
        C12503() {
        }

        public boolean onPreDraw() {
            if (IdenticonActivity.this.fragmentView != null) {
                IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                LinearLayout layout = (LinearLayout) IdenticonActivity.this.fragmentView;
                int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                if (rotation == 3 || rotation == 1) {
                    layout.setOrientation(0);
                } else {
                    layout.setOrientation(1);
                }
                IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
            }
            return true;
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                return false;
            }
        }
    }

    /* renamed from: org.telegram.ui.IdenticonActivity.1 */
    class C18641 extends ActionBarMenuOnItemClick {
        C18641() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                IdenticonActivity.this.finishFragment();
            }
        }
    }

    public IdenticonActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id");
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EncryptionKey", C0691R.string.EncryptionKey));
        this.actionBar.setActionBarMenuOnItemClick(new C18641());
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(1);
        linearLayout.setWeightSum(100.0f);
        linearLayout.setBackgroundColor(-986896);
        this.fragmentView.setOnTouchListener(new C12492());
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        ImageView identiconView = new ImageView(context);
        identiconView.setScaleType(ScaleType.FIT_XY);
        frameLayout.addView(identiconView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        frameLayout.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        TextView textView = new TextView(context);
        textView.setTextColor(-8421505);
        textView.setTextSize(1, 16.0f);
        textView.setLinksClickable(true);
        textView.setClickable(true);
        textView.setMovementMethod(new LinkMovementMethodMy());
        textView.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
        textView.setGravity(17);
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            IdenticonDrawable drawable = new IdenticonDrawable();
            identiconView.setImageDrawable(drawable);
            drawable.setEncryptedChat(encryptedChat);
            User user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
            SpannableStringBuilder hash = new SpannableStringBuilder();
            if (encryptedChat.key_hash.length > 16) {
                String hex = Utilities.bytesToHex(encryptedChat.key_hash);
                for (int a = 0; a < 32; a++) {
                    if (a != 0) {
                        if (a % 8 == 0) {
                            hash.append('\n');
                        } else if (a % 4 == 0) {
                            hash.append(' ');
                        }
                    }
                    hash.append(hex.substring(a * 2, (a * 2) + 2));
                    hash.append(' ');
                }
                hash.append("\n\n");
            }
            r15 = new Object[2];
            r15[0] = user.first_name;
            r15[1] = user.first_name;
            hash.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", C0691R.string.EncryptionKeyDescription, r15)));
            String url = "telegram.org";
            int index = hash.toString().indexOf("telegram.org");
            if (index != -1) {
                hash.setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", C0691R.string.EncryptionKeyLink)), index, "telegram.org".length() + index, 33);
            }
            textView.setText(hash);
        }
        return this.fragmentView;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    private void fixLayout() {
        this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new C12503());
    }
}
