package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class StickerPreviewViewer {
    private static volatile StickerPreviewViewer Instance;
    private ColorDrawable backgroundDrawable;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private Document currentSticker;
    private View currentStickerPreviewCell;
    private boolean isVisible;
    private int keyboardHeight;
    private long lastUpdateTime;
    private Runnable openStickerPreviewRunnable;
    private Activity parentActivity;
    private float showProgress;
    private int startX;
    private int startY;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    /* renamed from: org.telegram.ui.StickerPreviewViewer.1 */
    class C14511 implements Runnable {
        final /* synthetic */ View val$listView;
        final /* synthetic */ Object val$listener;

        C14511(View view, Object obj) {
            this.val$listView = view;
            this.val$listener = obj;
        }

        public void run() {
            if (this.val$listView instanceof AbsListView) {
                ((AbsListView) this.val$listView).setOnItemClickListener((OnItemClickListener) this.val$listener);
            } else if (this.val$listView instanceof RecyclerListView) {
                ((RecyclerListView) this.val$listView).setOnItemClickListener((RecyclerListView.OnItemClickListener) this.val$listener);
            }
        }
    }

    /* renamed from: org.telegram.ui.StickerPreviewViewer.2 */
    class C14522 implements Runnable {
        final /* synthetic */ int val$height;
        final /* synthetic */ View val$listView;

        C14522(View view, int i) {
            this.val$listView = view;
            this.val$height = i;
        }

        public void run() {
            if (StickerPreviewViewer.this.openStickerPreviewRunnable != null) {
                if (this.val$listView instanceof AbsListView) {
                    ((AbsListView) this.val$listView).setOnItemClickListener(null);
                    ((AbsListView) this.val$listView).requestDisallowInterceptTouchEvent(true);
                } else if (this.val$listView instanceof RecyclerListView) {
                    ((RecyclerListView) this.val$listView).setOnItemClickListener(null);
                    ((RecyclerListView) this.val$listView).requestDisallowInterceptTouchEvent(true);
                }
                StickerPreviewViewer.this.openStickerPreviewRunnable = null;
                StickerPreviewViewer.this.setParentActivity((Activity) this.val$listView.getContext());
                StickerPreviewViewer.this.setKeyboardHeight(this.val$height);
                if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                    StickerPreviewViewer.this.open(((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).getSticker());
                    ((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                } else if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerCell) {
                    StickerPreviewViewer.this.open(((StickerCell) StickerPreviewViewer.this.currentStickerPreviewCell).getSticker());
                    ((StickerCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                } else if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof ContextLinkCell) {
                    StickerPreviewViewer.this.open(((ContextLinkCell) StickerPreviewViewer.this.currentStickerPreviewCell).getDocument());
                    ((ContextLinkCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.StickerPreviewViewer.3 */
    class C14533 implements OnTouchListener {
        C14533() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
                StickerPreviewViewer.this.close();
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.StickerPreviewViewer.4 */
    class C14544 implements Runnable {
        C14544() {
        }

        public void run() {
            StickerPreviewViewer.this.centerImage.setImageBitmap((Bitmap) null);
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onDraw(Canvas canvas) {
            StickerPreviewViewer.getInstance().onDraw(canvas);
        }
    }

    public StickerPreviewViewer() {
        this.backgroundDrawable = new ColorDrawable(1895825408);
        this.centerImage = new ImageReceiver();
        this.isVisible = false;
        this.keyboardHeight = AndroidUtilities.dp(200.0f);
        this.currentSticker = null;
    }

    static {
        Instance = null;
    }

    public static StickerPreviewViewer getInstance() {
        StickerPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        StickerPreviewViewer localInstance2 = new StickerPreviewViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public void reset() {
        if (this.openStickerPreviewRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
            this.openStickerPreviewRunnable = null;
        }
        if (this.currentStickerPreviewCell != null) {
            if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
            } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
            } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
            }
            this.currentStickerPreviewCell = null;
        }
    }

    public boolean onTouch(MotionEvent event, View listView, int height, Object listener) {
        if (this.openStickerPreviewRunnable != null || isVisible()) {
            if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
                AndroidUtilities.runOnUIThread(new C14511(listView, listener), 150);
                if (this.openStickerPreviewRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                    this.openStickerPreviewRunnable = null;
                } else if (isVisible()) {
                    close();
                    if (this.currentStickerPreviewCell != null) {
                        if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                            ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                        } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                            ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                        } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                            ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                        }
                        this.currentStickerPreviewCell = null;
                    }
                }
            } else if (event.getAction() != 0) {
                if (isVisible()) {
                    if (event.getAction() == 2) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        int count = 0;
                        if (listView instanceof AbsListView) {
                            count = ((AbsListView) listView).getChildCount();
                        } else if (listView instanceof RecyclerListView) {
                            count = ((RecyclerListView) listView).getChildCount();
                        }
                        int a = 0;
                        while (a < count) {
                            View view = null;
                            if (listView instanceof AbsListView) {
                                view = ((AbsListView) listView).getChildAt(a);
                            } else if (listView instanceof RecyclerListView) {
                                view = ((RecyclerListView) listView).getChildAt(a);
                            }
                            if (view == null) {
                                return false;
                            }
                            int top = view.getTop();
                            int bottom = view.getBottom();
                            int left = view.getLeft();
                            int right = view.getRight();
                            if (top > y || bottom < y || left > x || right < x) {
                                a++;
                            } else {
                                boolean ok = false;
                                if (view instanceof StickerEmojiCell) {
                                    ok = true;
                                } else if (view instanceof StickerCell) {
                                    ok = true;
                                } else if (view instanceof ContextLinkCell) {
                                    ok = ((ContextLinkCell) view).isSticker();
                                }
                                if (ok && view != this.currentStickerPreviewCell) {
                                    if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                                    } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                                    } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                                    }
                                    this.currentStickerPreviewCell = view;
                                    setKeyboardHeight(height);
                                    if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                        open(((StickerEmojiCell) this.currentStickerPreviewCell).getSticker());
                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(true);
                                    } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                                        open(((StickerCell) this.currentStickerPreviewCell).getSticker());
                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(true);
                                    } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                        open(((ContextLinkCell) this.currentStickerPreviewCell).getDocument());
                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(true);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                } else if (this.openStickerPreviewRunnable != null) {
                    if (event.getAction() == 2) {
                        if (Math.hypot((double) (((float) this.startX) - event.getX()), (double) (((float) this.startY) - event.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
                            AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                            this.openStickerPreviewRunnable = null;
                        }
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                        this.openStickerPreviewRunnable = null;
                    }
                }
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent event, View listView, int height) {
        if (event.getAction() == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = 0;
            if (listView instanceof AbsListView) {
                count = ((AbsListView) listView).getChildCount();
            } else if (listView instanceof RecyclerListView) {
                count = ((RecyclerListView) listView).getChildCount();
            }
            int a = 0;
            while (a < count) {
                View view = null;
                if (listView instanceof AbsListView) {
                    view = ((AbsListView) listView).getChildAt(a);
                } else if (listView instanceof RecyclerListView) {
                    view = ((RecyclerListView) listView).getChildAt(a);
                }
                if (view == null) {
                    return false;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x || right < x) {
                    a++;
                } else {
                    boolean ok = false;
                    if (view instanceof StickerEmojiCell) {
                        ok = ((StickerEmojiCell) view).showingBitmap();
                    } else if (view instanceof StickerCell) {
                        ok = ((StickerCell) view).showingBitmap();
                    } else if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        ok = cell.isSticker() && cell.showingBitmap();
                    }
                    if (!ok) {
                        return false;
                    }
                    this.startX = x;
                    this.startY = y;
                    this.currentStickerPreviewCell = view;
                    this.openStickerPreviewRunnable = new C14522(listView, height);
                    AndroidUtilities.runOnUIThread(this.openStickerPreviewRunnable, 200);
                    return true;
                }
            }
        }
        return false;
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.windowView = new FrameLayout(activity);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (VERSION.SDK_INT >= 23) {
                this.windowView.setFitsSystemWindows(true);
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new C14533());
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147483640;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(Document sticker) {
        if (this.parentActivity != null && sticker != null) {
            this.centerImage.setImage((TLObject) sticker, null, sticker.thumb.location, null, "webp", true);
            this.currentSticker = sticker;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void close() {
        if (this.parentActivity != null) {
            this.showProgress = TouchHelperCallback.ALPHA_FULL;
            this.currentSticker = null;
            this.isVisible = false;
            AndroidUtilities.unlockOrientation(this.parentActivity);
            AndroidUtilities.runOnUIThread(new C14544());
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void destroy() {
        this.isVisible = false;
        this.currentSticker = null;
        if (this.parentActivity != null && this.windowView != null) {
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            Instance = null;
        }
    }

    private void onDraw(Canvas canvas) {
        if (this.containerView != null && this.backgroundDrawable != null) {
            this.backgroundDrawable.setAlpha((int) (BitmapDescriptorFactory.HUE_CYAN * this.showProgress));
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            int size = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight())) / 1.8f);
            canvas.translate((float) (this.containerView.getWidth() / 2), (float) Math.max((size / 2) + AndroidUtilities.statusBarHeight, (this.containerView.getHeight() - this.keyboardHeight) / 2));
            if (this.centerImage.getBitmap() != null) {
                size = (int) (((float) size) * ((this.showProgress * DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) / DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD));
                this.centerImage.setAlpha(this.showProgress);
                this.centerImage.setImageCoords((-size) / 2, (-size) / 2, size, size);
                this.centerImage.draw(canvas);
            }
            canvas.restore();
            if (this.showProgress != TouchHelperCallback.ALPHA_FULL) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                this.showProgress += ((float) dt) / 150.0f;
                this.containerView.invalidate();
                if (this.showProgress > TouchHelperCallback.ALPHA_FULL) {
                    this.showProgress = TouchHelperCallback.ALPHA_FULL;
                }
            }
        }
    }
}
