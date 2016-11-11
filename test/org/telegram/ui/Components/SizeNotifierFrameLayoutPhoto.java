package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

public class SizeNotifierFrameLayoutPhoto extends FrameLayout {
    private SizeNotifierFrameLayoutPhotoDelegate delegate;
    private int keyboardHeight;
    private Rect rect;
    private WindowManager windowManager;

    /* renamed from: org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto.1 */
    class C11851 implements Runnable {
        final /* synthetic */ boolean val$isWidthGreater;

        C11851(boolean z) {
            this.val$isWidthGreater = z;
        }

        public void run() {
            if (SizeNotifierFrameLayoutPhoto.this.delegate != null) {
                SizeNotifierFrameLayoutPhoto.this.delegate.onSizeChanged(SizeNotifierFrameLayoutPhoto.this.keyboardHeight, this.val$isWidthGreater);
            }
        }
    }

    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayoutPhoto(Context context) {
        super(context);
        this.rect = new Rect();
    }

    public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate delegate) {
        this.delegate = delegate;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        int usableViewHeight = rootView.getHeight() - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        int size = (AndroidUtilities.displaySize.y - this.rect.top) - usableViewHeight;
        if (size <= AndroidUtilities.dp(10.0f)) {
            return 0;
        }
        return size;
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            post(new C11851(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y));
        }
    }
}
