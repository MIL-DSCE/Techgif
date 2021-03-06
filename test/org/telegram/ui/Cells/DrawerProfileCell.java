package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerProfileCell extends FrameLayout {
    private BackupImageView avatarImageView;
    private int currentColor;
    private Rect destRect;
    private TextView nameTextView;
    private Paint paint;
    private TextView phoneTextView;
    private ImageView shadowView;
    private Rect srcRect;

    public DrawerProfileCell(Context context) {
        super(context);
        this.srcRect = new Rect();
        this.destRect = new Rect();
        this.paint = new Paint();
        setBackgroundColor(Theme.ACTION_BAR_PROFILE_COLOR);
        this.shadowView = new ImageView(context);
        this.shadowView.setVisibility(4);
        this.shadowView.setScaleType(ScaleType.FIT_XY);
        this.shadowView.setImageResource(C0691R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 16.0f, 28.0f));
        this.phoneTextView = new TextView(context);
        this.phoneTextView.setTextColor(-4004353);
        this.phoneTextView.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 16.0f, 9.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (VERSION.SDK_INT >= 21) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, C0747C.ENCODING_PCM_32BIT));
            return;
        }
        try {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), C0747C.ENCODING_PCM_32BIT));
        } catch (Throwable e) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148.0f));
            FileLog.m13e("tmessages", e);
        }
    }

    protected void onDraw(Canvas canvas) {
        Drawable backgroundDrawable = ApplicationLoader.getCachedWallpaper();
        int color = ApplicationLoader.getServiceMessageColor();
        if (this.currentColor != color) {
            this.currentColor = color;
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(ViewCompat.MEASURED_STATE_MASK | color, Mode.MULTIPLY));
        }
        if (!ApplicationLoader.isCustomTheme() || backgroundDrawable == null) {
            this.shadowView.setVisibility(4);
            this.phoneTextView.setTextColor(-4004353);
            super.onDraw(canvas);
            return;
        }
        this.phoneTextView.setTextColor(-1);
        this.shadowView.setVisibility(0);
        if (backgroundDrawable instanceof ColorDrawable) {
            backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            backgroundDrawable.draw(canvas);
        } else if (backgroundDrawable instanceof BitmapDrawable) {
            float scale;
            Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
            float scaleX = ((float) getMeasuredWidth()) / ((float) bitmap.getWidth());
            float scaleY = ((float) getMeasuredHeight()) / ((float) bitmap.getHeight());
            if (scaleX < scaleY) {
                scale = scaleY;
            } else {
                scale = scaleX;
            }
            int width = (int) (((float) getMeasuredWidth()) / scale);
            int height = (int) (((float) getMeasuredHeight()) / scale);
            int x = (bitmap.getWidth() - width) / 2;
            int y = (bitmap.getHeight() - height) / 2;
            this.srcRect.set(x, y, x + width, y + height);
            this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
        }
    }

    public void setUser(User user) {
        if (user != null) {
            TLObject photo = null;
            if (user.photo != null) {
                photo = user.photo.photo_small;
            }
            this.nameTextView.setText(UserObject.getUserName(user));
            this.phoneTextView.setText(PhoneFormat.getInstance().format("+" + user.phone));
            Drawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.ACTION_BAR_MAIN_AVATAR_COLOR);
            this.avatarImageView.setImage(photo, "50_50", avatarDrawable);
        }
    }
}
