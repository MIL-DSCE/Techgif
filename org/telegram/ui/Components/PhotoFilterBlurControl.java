package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PhotoFilterBlurControl extends FrameLayout {
    private static final float BlurInsetProximity;
    private static final float BlurMinimumDifference = 0.02f;
    private static final float BlurMinimumFalloff = 0.1f;
    private static final float BlurViewCenterInset;
    private static final float BlurViewRadiusInset;
    private final int GestureStateBegan;
    private final int GestureStateCancelled;
    private final int GestureStateChanged;
    private final int GestureStateEnded;
    private final int GestureStateFailed;
    private BlurViewActiveControl activeControl;
    private Size actualAreaSize;
    private float angle;
    private Paint arcPaint;
    private RectF arcRect;
    private Point centerPoint;
    private boolean checkForMoving;
    private boolean checkForZooming;
    private PhotoFilterLinearBlurControlDelegate delegate;
    private float falloff;
    private boolean isMoving;
    private boolean isZooming;
    private Paint paint;
    private float pointerScale;
    private float pointerStartX;
    private float pointerStartY;
    private float size;
    private Point startCenterPoint;
    private float startDistance;
    private float startPointerDistance;
    private float startRadius;
    private int type;

    /* renamed from: org.telegram.ui.Components.PhotoFilterBlurControl.1 */
    static /* synthetic */ class C11481 {
        static final /* synthetic */ int[] f13xcde84254;

        static {
            f13xcde84254 = new int[BlurViewActiveControl.values().length];
            try {
                f13xcde84254[BlurViewActiveControl.BlurViewActiveControlCenter.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f13xcde84254[BlurViewActiveControl.BlurViewActiveControlInnerRadius.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f13xcde84254[BlurViewActiveControl.BlurViewActiveControlOuterRadius.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f13xcde84254[BlurViewActiveControl.BlurViewActiveControlRotation.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private enum BlurViewActiveControl {
        BlurViewActiveControlNone,
        BlurViewActiveControlCenter,
        BlurViewActiveControlInnerRadius,
        BlurViewActiveControlOuterRadius,
        BlurViewActiveControlWholeArea,
        BlurViewActiveControlRotation
    }

    public interface PhotoFilterLinearBlurControlDelegate {
        void valueChanged(Point point, float f, float f2, float f3);
    }

    static {
        BlurInsetProximity = (float) AndroidUtilities.dp(20.0f);
        BlurViewCenterInset = (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        BlurViewRadiusInset = (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
    }

    public PhotoFilterBlurControl(Context context) {
        super(context);
        this.GestureStateBegan = 1;
        this.GestureStateChanged = 2;
        this.GestureStateEnded = 3;
        this.GestureStateCancelled = 4;
        this.GestureStateFailed = 5;
        this.startCenterPoint = new Point();
        this.actualAreaSize = new Size();
        this.centerPoint = new Point(0.5f, 0.5f);
        this.falloff = 0.15f;
        this.size = 0.35f;
        this.arcRect = new RectF();
        this.pointerScale = TouchHelperCallback.ALPHA_FULL;
        this.checkForMoving = true;
        this.paint = new Paint(1);
        this.arcPaint = new Paint(1);
        setWillNotDraw(false);
        this.paint.setColor(-1);
        this.arcPaint.setColor(-1);
        this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.arcPaint.setStyle(Style.STROKE);
    }

    public void setType(int blurType) {
        this.type = blurType;
    }

    public void setDelegate(PhotoFilterLinearBlurControlDelegate delegate) {
        this.delegate = delegate;
    }

    private float getDistance(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return BlurInsetProximity;
        }
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        return (float) Math.sqrt((double) (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }

    private float degreesToRadians(float degrees) {
        return (3.1415927f * degrees) / BitmapDescriptorFactory.HUE_CYAN;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                if (event.getPointerCount() == 1) {
                    if (this.checkForMoving && !this.isMoving) {
                        float locationX = event.getX();
                        float locationY = event.getY();
                        Point centerPoint = getActualCenterPoint();
                        Point delta = new Point(locationX - centerPoint.f14x, locationY - centerPoint.f15y);
                        float radialDistance = (float) Math.sqrt((double) ((delta.f14x * delta.f14x) + (delta.f15y * delta.f15y)));
                        float innerRadius = getActualInnerRadius();
                        float outerRadius = getActualOuterRadius();
                        boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                        float innerRadiusOuterInset = close ? BlurInsetProximity : BlurViewRadiusInset;
                        float outerRadiusInnerInset = close ? BlurInsetProximity : BlurViewRadiusInset;
                        if (this.type == 0) {
                            float distance = (float) Math.abs((((double) delta.f14x) * Math.cos(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)) + (((double) delta.f15y) * Math.sin(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)));
                            if (radialDistance < BlurViewCenterInset) {
                                this.isMoving = true;
                            } else if (distance > innerRadius - BlurViewRadiusInset && distance < innerRadius + innerRadiusOuterInset) {
                                this.isMoving = true;
                            } else if (distance > outerRadius - outerRadiusInnerInset && distance < BlurViewRadiusInset + outerRadius) {
                                this.isMoving = true;
                            } else if (distance <= innerRadius - BlurViewRadiusInset || distance >= BlurViewRadiusInset + outerRadius) {
                                this.isMoving = true;
                            }
                        } else if (this.type == 1) {
                            if (radialDistance < BlurViewCenterInset) {
                                this.isMoving = true;
                            } else if (radialDistance > innerRadius - BlurViewRadiusInset && radialDistance < innerRadius + innerRadiusOuterInset) {
                                this.isMoving = true;
                            } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < BlurViewRadiusInset + outerRadius) {
                                this.isMoving = true;
                            }
                        }
                        this.checkForMoving = false;
                        if (this.isMoving) {
                            handlePan(1, event);
                            break;
                        }
                    }
                }
                if (this.isMoving) {
                    handlePan(3, event);
                    this.checkForMoving = true;
                    this.isMoving = false;
                }
                if (event.getPointerCount() == 2) {
                    if (this.checkForZooming && !this.isZooming) {
                        handlePinch(1, event);
                        this.isZooming = true;
                        break;
                    }
                }
                handlePinch(3, event);
                this.checkForZooming = true;
                this.isZooming = false;
                break;
                break;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                if (this.isMoving) {
                    handlePan(3, event);
                    this.isMoving = false;
                } else if (this.isZooming) {
                    handlePinch(3, event);
                    this.isZooming = false;
                }
                this.checkForMoving = true;
                this.checkForZooming = true;
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                if (!this.isMoving) {
                    if (this.isZooming) {
                        handlePinch(2, event);
                        break;
                    }
                }
                handlePan(2, event);
                break;
                break;
        }
        return true;
    }

    private void handlePan(int state, MotionEvent event) {
        float locationX = event.getX();
        float locationY = event.getY();
        Point actualCenterPoint = getActualCenterPoint();
        Point delta = new Point(locationX - actualCenterPoint.f14x, locationY - actualCenterPoint.f15y);
        float radialDistance = (float) Math.sqrt((double) ((delta.f14x * delta.f14x) + (delta.f15y * delta.f15y)));
        float shorterSide = this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width;
        float innerRadius = shorterSide * this.falloff;
        float outerRadius = shorterSide * this.size;
        float distance = (float) Math.abs((((double) delta.f14x) * Math.cos(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)) + (((double) delta.f15y) * Math.sin(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)));
        int i;
        switch (state) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                this.pointerStartX = event.getX();
                this.pointerStartY = event.getY();
                boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                float innerRadiusOuterInset = close ? BlurInsetProximity : BlurViewRadiusInset;
                float outerRadiusInnerInset = close ? BlurInsetProximity : BlurViewRadiusInset;
                if (this.type != 0) {
                    i = this.type;
                    if (r0 == 1) {
                        if (radialDistance < BlurViewCenterInset) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                            this.startCenterPoint = actualCenterPoint;
                        } else if (radialDistance > innerRadius - BlurViewRadiusInset && radialDistance < innerRadius + innerRadiusOuterInset) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            this.startDistance = radialDistance;
                            this.startRadius = innerRadius;
                        } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < BlurViewRadiusInset + outerRadius) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            this.startDistance = radialDistance;
                            this.startRadius = outerRadius;
                        }
                    }
                } else if (radialDistance < BlurViewCenterInset) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                    this.startCenterPoint = actualCenterPoint;
                } else if (distance > innerRadius - BlurViewRadiusInset && distance < innerRadius + innerRadiusOuterInset) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                    this.startDistance = distance;
                    this.startRadius = innerRadius;
                } else if (distance > outerRadius - outerRadiusInnerInset && distance < BlurViewRadiusInset + outerRadius) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                    this.startDistance = distance;
                    this.startRadius = outerRadius;
                } else if (distance <= innerRadius - BlurViewRadiusInset || distance >= BlurViewRadiusInset + outerRadius) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                }
                setSelected(true, true);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                float translationX;
                float translationY;
                Rect actualArea;
                Point point;
                if (this.type != 0) {
                    i = this.type;
                    if (r0 == 1) {
                        switch (C11481.f13xcde84254[this.activeControl.ordinal()]) {
                            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                                translationX = locationX - this.pointerStartX;
                                translationY = locationY - this.pointerStartY;
                                actualArea = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, (((float) getHeight()) - this.actualAreaSize.height) / 2.0f, this.actualAreaSize.width, this.actualAreaSize.height);
                                point = new Point(Math.max(actualArea.f16x, Math.min(actualArea.f16x + actualArea.width, this.startCenterPoint.f14x + translationX)), Math.max(actualArea.f17y, Math.min(actualArea.f17y + actualArea.height, this.startCenterPoint.f15y + translationY)));
                                this.centerPoint = new Point((point.f14x - actualArea.f16x) / this.actualAreaSize.width, ((point.f15y - actualArea.f17y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                                break;
                            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                                this.falloff = Math.min(Math.max(BlurMinimumFalloff, (this.startRadius + (radialDistance - this.startDistance)) / shorterSide), this.size - BlurMinimumDifference);
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                this.size = Math.max(this.falloff + BlurMinimumDifference, (this.startRadius + (radialDistance - this.startDistance)) / shorterSide);
                                break;
                            default:
                                break;
                        }
                    }
                }
                switch (C11481.f13xcde84254[this.activeControl.ordinal()]) {
                    case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                        translationX = locationX - this.pointerStartX;
                        translationY = locationY - this.pointerStartY;
                        actualArea = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, (((float) getHeight()) - this.actualAreaSize.height) / 2.0f, this.actualAreaSize.width, this.actualAreaSize.height);
                        point = new Point(Math.max(actualArea.f16x, Math.min(actualArea.f16x + actualArea.width, this.startCenterPoint.f14x + translationX)), Math.max(actualArea.f17y, Math.min(actualArea.f17y + actualArea.height, this.startCenterPoint.f15y + translationY)));
                        this.centerPoint = new Point((point.f14x - actualArea.f16x) / this.actualAreaSize.width, ((point.f15y - actualArea.f17y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                        break;
                    case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                        this.falloff = Math.min(Math.max(BlurMinimumFalloff, (this.startRadius + (distance - this.startDistance)) / shorterSide), this.size - BlurMinimumDifference);
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        this.size = Math.max(this.falloff + BlurMinimumDifference, (this.startRadius + (distance - this.startDistance)) / shorterSide);
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        translationX = locationX - this.pointerStartX;
                        translationY = locationY - this.pointerStartY;
                        boolean clockwise = false;
                        boolean right = locationX > actualCenterPoint.f14x;
                        boolean bottom = locationY > actualCenterPoint.f15y;
                        if (right || bottom) {
                            if (!right || bottom) {
                                if (right && bottom) {
                                    if (Math.abs(translationY) > Math.abs(translationX)) {
                                        if (translationY > BlurInsetProximity) {
                                            clockwise = true;
                                        }
                                    } else if (translationX < BlurInsetProximity) {
                                        clockwise = true;
                                    }
                                } else if (Math.abs(translationY) > Math.abs(translationX)) {
                                    if (translationY < BlurInsetProximity) {
                                        clockwise = true;
                                    }
                                } else if (translationX < BlurInsetProximity) {
                                    clockwise = true;
                                }
                            } else if (Math.abs(translationY) > Math.abs(translationX)) {
                                if (translationY > BlurInsetProximity) {
                                    clockwise = true;
                                }
                            } else if (translationX > BlurInsetProximity) {
                                clockwise = true;
                            }
                        } else if (Math.abs(translationY) > Math.abs(translationX)) {
                            if (translationY < BlurInsetProximity) {
                                clockwise = true;
                            }
                        } else if (translationX > BlurInsetProximity) {
                            clockwise = true;
                        }
                        this.angle = (((((float) (((clockwise ? 1 : 0) * 2) - 1)) * ((float) Math.sqrt((double) ((translationX * translationX) + (translationY * translationY))))) / 3.1415927f) / 1.15f) + this.angle;
                        this.pointerStartX = locationX;
                        this.pointerStartY = locationY;
                        break;
                }
                invalidate();
                if (this.delegate != null) {
                    this.delegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
                }
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
            default:
        }
    }

    private void handlePinch(int state, MotionEvent event) {
        switch (state) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                this.startPointerDistance = getDistance(event);
                this.pointerScale = TouchHelperCallback.ALPHA_FULL;
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlWholeArea;
                setSelected(true, true);
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            default:
                return;
        }
        float newDistance = getDistance(event);
        this.pointerScale += ((newDistance - this.startPointerDistance) / AndroidUtilities.density) * 0.01f;
        this.falloff = Math.max(BlurMinimumFalloff, this.falloff * this.pointerScale);
        this.size = Math.max(this.falloff + BlurMinimumDifference, this.size * this.pointerScale);
        this.pointerScale = TouchHelperCallback.ALPHA_FULL;
        this.startPointerDistance = newDistance;
        invalidate();
        if (this.delegate != null) {
            this.delegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    private void setSelected(boolean selected, boolean animated) {
    }

    public void setActualAreaSize(float width, float height) {
        this.actualAreaSize.width = width;
        this.actualAreaSize.height = height;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point centerPoint = getActualCenterPoint();
        float innerRadius = getActualInnerRadius();
        float outerRadius = getActualOuterRadius();
        canvas.translate(centerPoint.f14x, centerPoint.f15y);
        int i;
        Canvas canvas2;
        if (this.type == 0) {
            float f;
            canvas.rotate(this.angle);
            float space = (float) AndroidUtilities.dp(6.0f);
            float length = (float) AndroidUtilities.dp(12.0f);
            float thickness = (float) AndroidUtilities.dp(1.5f);
            for (i = 0; i < 30; i++) {
                canvas2 = canvas;
                canvas2.drawRect((length + space) * ((float) i), -innerRadius, (((float) i) * (length + space)) + length, thickness - innerRadius, this.paint);
                canvas.drawRect(((((float) (-i)) * (length + space)) - space) - length, -innerRadius, (((float) (-i)) * (length + space)) - space, thickness - innerRadius, this.paint);
                canvas2 = canvas;
                f = innerRadius;
                canvas2.drawRect((length + space) * ((float) i), f, length + (((float) i) * (length + space)), thickness + innerRadius, this.paint);
                canvas.drawRect(((((float) (-i)) * (length + space)) - space) - length, innerRadius, (((float) (-i)) * (length + space)) - space, thickness + innerRadius, this.paint);
            }
            length = (float) AndroidUtilities.dp(6.0f);
            for (i = 0; i < 64; i++) {
                canvas2 = canvas;
                canvas2.drawRect((length + space) * ((float) i), -outerRadius, length + (((float) i) * (length + space)), thickness - outerRadius, this.paint);
                canvas.drawRect(((((float) (-i)) * (length + space)) - space) - length, -outerRadius, (((float) (-i)) * (length + space)) - space, thickness - outerRadius, this.paint);
                canvas2 = canvas;
                f = outerRadius;
                canvas2.drawRect((length + space) * ((float) i), f, length + (((float) i) * (length + space)), thickness + outerRadius, this.paint);
                canvas.drawRect(((((float) (-i)) * (length + space)) - space) - length, outerRadius, (((float) (-i)) * (length + space)) - space, thickness + outerRadius, this.paint);
            }
        } else if (this.type == 1) {
            this.arcRect.set(-innerRadius, -innerRadius, innerRadius, innerRadius);
            for (i = 0; i < 22; i++) {
                canvas2 = canvas;
                canvas2.drawArc(this.arcRect, (6.15f + 10.2f) * ((float) i), 10.2f, false, this.arcPaint);
            }
            this.arcRect.set(-outerRadius, -outerRadius, outerRadius, outerRadius);
            for (i = 0; i < 64; i++) {
                canvas2 = canvas;
                canvas2.drawArc(this.arcRect, (2.02f + 3.6f) * ((float) i), 3.6f, false, this.arcPaint);
            }
        }
        canvas.drawCircle(BlurInsetProximity, BlurInsetProximity, (float) AndroidUtilities.dp(8.0f), this.paint);
    }

    private Point getActualCenterPoint() {
        return new Point(((((float) getWidth()) - this.actualAreaSize.width) / 2.0f) + (this.centerPoint.f14x * this.actualAreaSize.width), (((((float) getHeight()) - this.actualAreaSize.height) / 2.0f) - ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) + (this.centerPoint.f15y * this.actualAreaSize.width));
    }

    private float getActualInnerRadius() {
        return (this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width) * this.falloff;
    }

    private float getActualOuterRadius() {
        return (this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width) * this.size;
    }
}
