package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PhotoAttachPhotoCell extends FrameLayout {
    private static Rect rect;
    private CheckBox checkBox;
    private FrameLayout checkFrame;
    private PhotoAttachPhotoCellDelegate delegate;
    private BackupImageView imageView;
    private boolean isLast;
    private PhotoEntry photoEntry;
    private boolean pressed;

    public interface PhotoAttachPhotoCellDelegate {
        void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell);
    }

    static {
        rect = new Rect();
    }

    public PhotoAttachPhotoCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        addView(this.imageView, LayoutHelper.createFrame(80, 80.0f));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42.0f, 51, 38.0f, 0.0f, 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0691R.drawable.checkbig);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105);
        addView(this.checkBox, LayoutHelper.createFrame(30, BitmapDescriptorFactory.HUE_ORANGE, 51, 46.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.isLast ? 0 : 6) + 80)), C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C0747C.ENCODING_PCM_32BIT));
    }

    public PhotoEntry getPhotoEntry() {
        return this.photoEntry;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public void setPhotoEntry(PhotoEntry entry, boolean last) {
        boolean z;
        int i = 0;
        this.pressed = false;
        this.photoEntry = entry;
        this.isLast = last;
        if (this.photoEntry.thumbPath != null) {
            this.imageView.setImage(this.photoEntry.thumbPath, null, getResources().getDrawable(C0691R.drawable.nophotos));
        } else if (this.photoEntry.path != null) {
            this.imageView.setOrientation(this.photoEntry.orientation, true);
            this.imageView.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, null, getResources().getDrawable(C0691R.drawable.nophotos));
        } else {
            this.imageView.setImageResource(C0691R.drawable.nophotos);
        }
        boolean showing = PhotoViewer.getInstance().isShowingImage(this.photoEntry.path);
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (showing) {
            z = false;
        } else {
            z = true;
        }
        imageReceiver.setVisible(z, true);
        CheckBox checkBox = this.checkBox;
        if (showing) {
            i = 4;
        }
        checkBox.setVisibility(i);
        requestLayout();
    }

    public void setChecked(boolean value, boolean animated) {
        this.checkBox.setChecked(value, animated);
    }

    public void setOnCheckClickLisnener(OnClickListener onCheckClickLisnener) {
        this.checkFrame.setOnClickListener(onCheckClickLisnener);
    }

    public void setDelegate(PhotoAttachPhotoCellDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        this.checkFrame.getHitRect(rect);
        if (event.getAction() == 0) {
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                this.pressed = true;
                invalidate();
                result = true;
            }
        } else if (this.pressed) {
            if (event.getAction() == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
                this.pressed = false;
                playSoundEffect(0);
                this.delegate.onCheckClick(this);
                invalidate();
            } else if (event.getAction() == 3) {
                this.pressed = false;
                invalidate();
            } else if (event.getAction() == 2 && !rect.contains((int) event.getX(), (int) event.getY())) {
                this.pressed = false;
                invalidate();
            }
        }
        if (result) {
            return result;
        }
        return super.onTouchEvent(event);
    }
}
