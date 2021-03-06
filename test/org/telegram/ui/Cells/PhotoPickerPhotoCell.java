package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PhotoPickerPhotoCell extends FrameLayout {
    private AnimatorSet animator;
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public int itemWidth;
    public BackupImageView photoImage;

    /* renamed from: org.telegram.ui.Cells.PhotoPickerPhotoCell.1 */
    class C17641 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$checked;

        C17641(boolean z) {
            this.val$checked = z;
        }

        public void onAnimationEnd(Animator animation) {
            if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animation)) {
                PhotoPickerPhotoCell.this.animator = null;
                if (!this.val$checked) {
                    PhotoPickerPhotoCell.this.setBackgroundColor(0);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animation)) {
                PhotoPickerPhotoCell.this.animator = null;
            }
        }
    }

    public PhotoPickerPhotoCell(Context context) {
        super(context);
        this.photoImage = new BackupImageView(context);
        addView(this.photoImage, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
        this.checkBox = new CheckBox(context, C0691R.drawable.checkbig);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105);
        addView(this.checkBox, LayoutHelper.createFrame(30, BitmapDescriptorFactory.HUE_ORANGE, 53, 0.0f, 4.0f, 4.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.itemWidth, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(this.itemWidth, C0747C.ENCODING_PCM_32BIT));
    }

    public void setChecked(boolean checked, boolean animated) {
        int i = -16119286;
        float f = 0.85f;
        this.checkBox.setChecked(checked, animated);
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
        if (animated) {
            if (checked) {
                setBackgroundColor(-16119286);
            }
            this.animator = new AnimatorSet();
            AnimatorSet animatorSet = this.animator;
            Animator[] animatorArr = new Animator[2];
            BackupImageView backupImageView = this.photoImage;
            String str = "scaleX";
            float[] fArr = new float[1];
            fArr[0] = checked ? 0.85f : 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, str, fArr);
            BackupImageView backupImageView2 = this.photoImage;
            String str2 = "scaleY";
            float[] fArr2 = new float[1];
            if (!checked) {
                f = TouchHelperCallback.ALPHA_FULL;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, str2, fArr2);
            animatorSet.playTogether(animatorArr);
            this.animator.setDuration(200);
            this.animator.addListener(new C17641(checked));
            this.animator.start();
            return;
        }
        float f2;
        if (!checked) {
            i = 0;
        }
        setBackgroundColor(i);
        BackupImageView backupImageView3 = this.photoImage;
        if (checked) {
            f2 = 0.85f;
        } else {
            f2 = TouchHelperCallback.ALPHA_FULL;
        }
        backupImageView3.setScaleX(f2);
        backupImageView2 = this.photoImage;
        if (!checked) {
            f = TouchHelperCallback.ALPHA_FULL;
        }
        backupImageView2.setScaleY(f);
    }
}
