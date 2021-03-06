package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PhotoPickerAlbumsCell extends FrameLayout {
    private AlbumEntry[] albumEntries;
    private AlbumView[] albumViews;
    private int albumsCount;
    private PhotoPickerAlbumsCellDelegate delegate;

    /* renamed from: org.telegram.ui.Cells.PhotoPickerAlbumsCell.1 */
    class C09871 implements OnClickListener {
        C09871() {
        }

        public void onClick(View v) {
            if (PhotoPickerAlbumsCell.this.delegate != null) {
                PhotoPickerAlbumsCell.this.delegate.didSelectAlbum(PhotoPickerAlbumsCell.this.albumEntries[((Integer) v.getTag()).intValue()]);
            }
        }
    }

    private class AlbumView extends FrameLayout {
        private TextView countTextView;
        private BackupImageView imageView;
        private TextView nameTextView;
        private View selector;

        public AlbumView(Context context) {
            super(context);
            this.imageView = new BackupImageView(context);
            addView(this.imageView, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            linearLayout.setBackgroundColor(2130706432);
            addView(linearLayout, LayoutHelper.createFrame(-1, 28, 83));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextSize(1, 13.0f);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TruncateAt.END);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setGravity(16);
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, (float) TouchHelperCallback.ALPHA_FULL, 8, 0, 0, 0));
            this.countTextView = new TextView(context);
            this.countTextView.setTextSize(1, 13.0f);
            this.countTextView.setTextColor(-5592406);
            this.countTextView.setSingleLine(true);
            this.countTextView.setEllipsize(TruncateAt.END);
            this.countTextView.setMaxLines(1);
            this.countTextView.setGravity(16);
            linearLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -1, 4.0f, 0.0f, 4.0f, 0.0f));
            this.selector = new View(context);
            this.selector.setBackgroundResource(C0691R.drawable.list_selector);
            addView(this.selector, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }
    }

    public interface PhotoPickerAlbumsCellDelegate {
        void didSelectAlbum(AlbumEntry albumEntry);
    }

    public PhotoPickerAlbumsCell(Context context) {
        super(context);
        this.albumEntries = new AlbumEntry[4];
        this.albumViews = new AlbumView[4];
        for (int a = 0; a < 4; a++) {
            this.albumViews[a] = new AlbumView(context);
            addView(this.albumViews[a]);
            this.albumViews[a].setVisibility(4);
            this.albumViews[a].setTag(Integer.valueOf(a));
            this.albumViews[a].setOnClickListener(new C09871());
        }
    }

    public void setAlbumsCount(int count) {
        int a = 0;
        while (a < this.albumViews.length) {
            this.albumViews[a].setVisibility(a < count ? 0 : 4);
            a++;
        }
        this.albumsCount = count;
    }

    public void setDelegate(PhotoPickerAlbumsCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setAlbum(int a, AlbumEntry albumEntry) {
        this.albumEntries[a] = albumEntry;
        if (albumEntry != null) {
            AlbumView albumView = this.albumViews[a];
            albumView.imageView.setOrientation(0, true);
            if (albumEntry.coverPhoto == null || albumEntry.coverPhoto.path == null) {
                albumView.imageView.setImageResource(C0691R.drawable.nophotos);
            } else {
                albumView.imageView.setOrientation(albumEntry.coverPhoto.orientation, true);
                if (albumEntry.coverPhoto.isVideo) {
                    albumView.imageView.setImage("vthumb://" + albumEntry.coverPhoto.imageId + ":" + albumEntry.coverPhoto.path, null, getContext().getResources().getDrawable(C0691R.drawable.nophotos));
                } else {
                    albumView.imageView.setImage("thumb://" + albumEntry.coverPhoto.imageId + ":" + albumEntry.coverPhoto.path, null, getContext().getResources().getDrawable(C0691R.drawable.nophotos));
                }
            }
            albumView.nameTextView.setText(albumEntry.bucketName);
            albumView.countTextView.setText(String.format("%d", new Object[]{Integer.valueOf(albumEntry.photos.size())}));
            return;
        }
        this.albumViews[a].setVisibility(4);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        if (AndroidUtilities.isTablet()) {
            itemWidth = (AndroidUtilities.dp(490.0f) - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        } else {
            itemWidth = (AndroidUtilities.displaySize.x - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        }
        for (int a = 0; a < this.albumsCount; a++) {
            LayoutParams layoutParams = (LayoutParams) this.albumViews[a].getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(4.0f) + itemWidth) * a;
            layoutParams.width = itemWidth;
            layoutParams.height = itemWidth;
            layoutParams.gravity = 51;
            this.albumViews[a].setLayoutParams(layoutParams);
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f) + itemWidth, C0747C.ENCODING_PCM_32BIT));
    }
}
