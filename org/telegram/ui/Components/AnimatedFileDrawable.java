package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    private static ScheduledThreadPoolExecutor executor;
    private static final Handler uiHandler;
    private boolean applyTransformation;
    private Bitmap backgroundBitmap;
    private BitmapShader backgroundShader;
    private RectF bitmapRect;
    private boolean decoderCreated;
    private boolean destroyWhenDone;
    private final Rect dstRect;
    private int invalidateAfter;
    private volatile boolean isRecycled;
    private volatile boolean isRunning;
    private long lastFrameTime;
    private int lastTimeStamp;
    private Runnable loadFrameRunnable;
    private Runnable loadFrameTask;
    protected final Runnable mInvalidateTask;
    private final Runnable mStartTask;
    private final int[] metaData;
    private volatile int nativePtr;
    private Bitmap nextRenderingBitmap;
    private BitmapShader nextRenderingShader;
    private View parentView;
    private File path;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private BitmapShader renderingShader;
    private int roundRadius;
    private RectF roundRect;
    private float scaleX;
    private float scaleY;
    private View secondParentView;
    private Matrix shaderMatrix;
    private Runnable uiRunnable;

    /* renamed from: org.telegram.ui.Components.AnimatedFileDrawable.1 */
    class C11041 implements Runnable {
        C11041() {
        }

        public void run() {
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.AnimatedFileDrawable.2 */
    class C11052 implements Runnable {
        C11052() {
        }

        public void run() {
            if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                AnimatedFileDrawable.this.nativePtr = 0;
            }
            if (AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.this.loadFrameTask = null;
                AnimatedFileDrawable.this.nextRenderingBitmap = AnimatedFileDrawable.this.backgroundBitmap;
                AnimatedFileDrawable.this.nextRenderingShader = AnimatedFileDrawable.this.backgroundShader;
                if (AnimatedFileDrawable.this.metaData[2] < AnimatedFileDrawable.this.lastTimeStamp) {
                    AnimatedFileDrawable.this.lastTimeStamp = 0;
                }
                if (AnimatedFileDrawable.this.metaData[2] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                    AnimatedFileDrawable.this.invalidateAfter = AnimatedFileDrawable.this.metaData[2] - AnimatedFileDrawable.this.lastTimeStamp;
                }
                AnimatedFileDrawable.this.lastTimeStamp = AnimatedFileDrawable.this.metaData[2];
                if (AnimatedFileDrawable.this.secondParentView != null) {
                    AnimatedFileDrawable.this.secondParentView.invalidate();
                } else if (AnimatedFileDrawable.this.parentView != null) {
                    AnimatedFileDrawable.this.parentView.invalidate();
                }
            } else if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                AnimatedFileDrawable.this.backgroundBitmap.recycle();
                AnimatedFileDrawable.this.backgroundBitmap = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.AnimatedFileDrawable.3 */
    class C11063 implements Runnable {
        C11063() {
        }

        public void run() {
            if (!AnimatedFileDrawable.this.isRecycled) {
                if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                    AnimatedFileDrawable.this.nativePtr = AnimatedFileDrawable.createDecoder(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData);
                    AnimatedFileDrawable.this.decoderCreated = true;
                }
                try {
                    if (AnimatedFileDrawable.this.backgroundBitmap == null) {
                        AnimatedFileDrawable.this.backgroundBitmap = Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Config.ARGB_8888);
                        if (!(AnimatedFileDrawable.this.backgroundShader != null || AnimatedFileDrawable.this.backgroundBitmap == null || AnimatedFileDrawable.this.roundRadius == 0)) {
                            AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData);
                }
            }
            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
        }
    }

    /* renamed from: org.telegram.ui.Components.AnimatedFileDrawable.4 */
    class C11074 implements Runnable {
        C11074() {
        }

        public void run() {
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
        }
    }

    private static native int createDecoder(String str, int[] iArr);

    private static native void destroyDecoder(int i);

    private static native int getVideoFrame(int i, Bitmap bitmap, int[] iArr);

    static {
        uiHandler = new Handler(Looper.getMainLooper());
        executor = new ScheduledThreadPoolExecutor(2, new DiscardPolicy());
    }

    public AnimatedFileDrawable(File file, boolean createDecoder) {
        this.invalidateAfter = 50;
        this.metaData = new int[3];
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.scaleX = TouchHelperCallback.ALPHA_FULL;
        this.scaleY = TouchHelperCallback.ALPHA_FULL;
        this.dstRect = new Rect();
        this.parentView = null;
        this.secondParentView = null;
        this.mInvalidateTask = new C11041();
        this.uiRunnable = new C11052();
        this.loadFrameRunnable = new C11063();
        this.mStartTask = new C11074();
        this.path = file;
        if (createDecoder) {
            this.nativePtr = createDecoder(file.getAbsolutePath(), this.metaData);
            this.decoderCreated = true;
        }
    }

    protected void postToDecodeQueue(Runnable runnable) {
        executor.execute(runnable);
    }

    public void setParentView(View view) {
        this.parentView = view;
    }

    public void setSecondParentView(View view) {
        this.secondParentView = view;
        if (view == null && this.recycleWithSecond) {
            recycle();
        }
    }

    public void recycle() {
        if (this.secondParentView != null) {
            this.recycleWithSecond = true;
            return;
        }
        this.isRunning = false;
        this.isRecycled = true;
        if (this.loadFrameTask == null) {
            if (this.nativePtr != 0) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = 0;
            }
            if (this.nextRenderingBitmap != null) {
                this.nextRenderingBitmap.recycle();
                this.nextRenderingBitmap = null;
            }
        } else {
            this.destroyWhenDone = true;
        }
        if (this.renderingBitmap != null) {
            this.renderingBitmap.recycle();
            this.renderingBitmap = null;
        }
    }

    protected static void runOnUiThread(Runnable task) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            task.run();
        } else {
            uiHandler.post(task);
        }
    }

    protected void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    public int getOpacity() {
        return -2;
    }

    public void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            if (this.renderingBitmap == null) {
                scheduleNextGetFrame();
            }
            runOnUiThread(this.mStartTask);
        }
    }

    private void scheduleNextGetFrame() {
        if (this.loadFrameTask != null) {
            return;
        }
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            Runnable runnable = this.loadFrameRunnable;
            this.loadFrameTask = runnable;
            postToDecodeQueue(runnable);
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        return this.decoderCreated ? this.metaData[1] : AndroidUtilities.dp(100.0f);
    }

    public int getIntrinsicWidth() {
        return this.decoderCreated ? this.metaData[0] : AndroidUtilities.dp(100.0f);
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (Math.abs(System.currentTimeMillis() - this.lastFrameTime) >= ((long) this.invalidateAfter) && this.nextRenderingBitmap != null) {
                    scheduleNextGetFrame();
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = System.currentTimeMillis();
                }
            }
            if (this.renderingBitmap != null) {
                if (this.applyTransformation) {
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) this.renderingBitmap.getWidth());
                    this.scaleY = ((float) this.dstRect.height()) / ((float) this.renderingBitmap.getHeight());
                    this.applyTransformation = false;
                }
                if (this.roundRadius != 0) {
                    int bitmapW = this.renderingBitmap.getWidth();
                    int bitmapH = this.renderingBitmap.getHeight();
                    float scale = Math.max(this.scaleX, this.scaleY);
                    if (this.renderingShader == null) {
                        this.renderingShader = new BitmapShader(this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                    }
                    getPaint().setShader(this.renderingShader);
                    this.roundRect.set(this.dstRect);
                    this.shaderMatrix.reset();
                    if (Math.abs(this.scaleX - this.scaleY) > 1.0E-5f) {
                        int w = (int) Math.floor((double) (((float) this.dstRect.width()) / scale));
                        int h = (int) Math.floor((double) (((float) this.dstRect.height()) / scale));
                        this.bitmapRect.set((float) ((bitmapW - w) / 2), (float) ((bitmapH - h) / 2), (float) w, (float) h);
                        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, ScaleToFit.START);
                    } else {
                        this.bitmapRect.set(0.0f, 0.0f, (float) this.renderingBitmap.getWidth(), (float) this.renderingBitmap.getHeight());
                        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, ScaleToFit.FILL);
                    }
                    this.renderingShader.setLocalMatrix(this.shaderMatrix);
                    canvas.drawRoundRect(this.roundRect, (float) this.roundRadius, (float) this.roundRadius, getPaint());
                } else {
                    canvas.translate((float) this.dstRect.left, (float) this.dstRect.top);
                    canvas.scale(this.scaleX, this.scaleY);
                    canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                }
                if (this.isRunning) {
                    uiHandler.postDelayed(this.mInvalidateTask, (long) this.invalidateAfter);
                }
            }
        }
    }

    public int getMinimumHeight() {
        return this.decoderCreated ? this.metaData[1] : AndroidUtilities.dp(100.0f);
    }

    public int getMinimumWidth() {
        return this.decoderCreated ? this.metaData[0] : AndroidUtilities.dp(100.0f);
    }

    public Bitmap getAnimatedBitmap() {
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public void setRoundRadius(int value) {
        this.roundRadius = value;
        getPaint().setFlags(1);
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable drawable = new AnimatedFileDrawable(this.path, false);
        drawable.metaData[0] = this.metaData[0];
        drawable.metaData[1] = this.metaData[1];
        return drawable;
    }
}
