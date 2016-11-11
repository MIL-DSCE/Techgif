package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.PhotoFilterView.CurvesToolValue;
import org.telegram.ui.Components.PhotoFilterView.CurvesValue;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class PhotoFilterCurvesControl extends View {
    private static final int CurvesSegmentBlacks = 1;
    private static final int CurvesSegmentHighlights = 4;
    private static final int CurvesSegmentMidtones = 3;
    private static final int CurvesSegmentNone = 0;
    private static final int CurvesSegmentShadows = 2;
    private static final int CurvesSegmentWhites = 5;
    private static final int GestureStateBegan = 1;
    private static final int GestureStateCancelled = 4;
    private static final int GestureStateChanged = 2;
    private static final int GestureStateEnded = 3;
    private static final int GestureStateFailed = 5;
    private int activeSegment;
    private Rect actualArea;
    private boolean checkForMoving;
    private CurvesToolValue curveValue;
    private PhotoFilterCurvesControlDelegate delegate;
    private boolean isMoving;
    private float lastX;
    private float lastY;
    private Paint paint;
    private Paint paintCurve;
    private Paint paintDash;
    private Path path;
    private TextPaint textPaint;

    public interface PhotoFilterCurvesControlDelegate {
        void valueChanged();
    }

    public PhotoFilterCurvesControl(Context context, CurvesToolValue value) {
        super(context);
        this.activeSegment = CurvesSegmentNone;
        this.checkForMoving = true;
        this.actualArea = new Rect();
        this.paint = new Paint(GestureStateBegan);
        this.paintDash = new Paint(GestureStateBegan);
        this.paintCurve = new Paint(GestureStateBegan);
        this.textPaint = new TextPaint(GestureStateBegan);
        this.path = new Path();
        setWillNotDraw(false);
        this.curveValue = value;
        this.paint.setColor(-1711276033);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL));
        this.paint.setStyle(Style.STROKE);
        this.paintDash.setColor(-1711276033);
        this.paintDash.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintDash.setStyle(Style.STROKE);
        this.paintCurve.setColor(-1);
        this.paintCurve.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintCurve.setStyle(Style.STROKE);
        this.textPaint.setColor(-4210753);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    public void setDelegate(PhotoFilterCurvesControlDelegate photoFilterCurvesControlDelegate) {
        this.delegate = photoFilterCurvesControlDelegate;
    }

    public void setActualArea(float x, float y, float width, float height) {
        this.actualArea.f16x = x;
        this.actualArea.f17y = y;
        this.actualArea.width = width;
        this.actualArea.height = height;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case CurvesSegmentNone /*0*/:
            case GestureStateFailed /*5*/:
                if (event.getPointerCount() != GestureStateBegan) {
                    if (this.isMoving) {
                        handlePan(GestureStateEnded, event);
                        this.checkForMoving = true;
                        this.isMoving = false;
                        break;
                    }
                } else if (this.checkForMoving && !this.isMoving) {
                    float locationX = event.getX();
                    float locationY = event.getY();
                    this.lastX = locationX;
                    this.lastY = locationY;
                    if (locationX >= this.actualArea.f16x && locationX <= this.actualArea.f16x + this.actualArea.width && locationY >= this.actualArea.f17y && locationY <= this.actualArea.f17y + this.actualArea.height) {
                        this.isMoving = true;
                    }
                    this.checkForMoving = false;
                    if (this.isMoving) {
                        handlePan(GestureStateBegan, event);
                        break;
                    }
                }
                break;
            case GestureStateBegan /*1*/:
            case GestureStateEnded /*3*/:
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                if (this.isMoving) {
                    handlePan(GestureStateEnded, event);
                    this.isMoving = false;
                }
                this.checkForMoving = true;
                break;
            case GestureStateChanged /*2*/:
                if (this.isMoving) {
                    handlePan(GestureStateChanged, event);
                    break;
                }
                break;
        }
        return true;
    }

    private void handlePan(int state, MotionEvent event) {
        float locationX = event.getX();
        float locationY = event.getY();
        switch (state) {
            case GestureStateBegan /*1*/:
                selectSegmentWithPoint(locationX);
            case GestureStateChanged /*2*/:
                float delta = Math.min(2.0f, (this.lastY - locationY) / 8.0f);
                CurvesValue curveValue = null;
                switch (this.curveValue.activeType) {
                    case CurvesSegmentNone /*0*/:
                        curveValue = this.curveValue.luminanceCurve;
                        break;
                    case GestureStateBegan /*1*/:
                        curveValue = this.curveValue.redCurve;
                        break;
                    case GestureStateChanged /*2*/:
                        curveValue = this.curveValue.greenCurve;
                        break;
                    case GestureStateEnded /*3*/:
                        curveValue = this.curveValue.blueCurve;
                        break;
                }
                switch (this.activeSegment) {
                    case GestureStateBegan /*1*/:
                        curveValue.blacksLevel = Math.max(0.0f, Math.min(100.0f, curveValue.blacksLevel + delta));
                        break;
                    case GestureStateChanged /*2*/:
                        curveValue.shadowsLevel = Math.max(0.0f, Math.min(100.0f, curveValue.shadowsLevel + delta));
                        break;
                    case GestureStateEnded /*3*/:
                        curveValue.midtonesLevel = Math.max(0.0f, Math.min(100.0f, curveValue.midtonesLevel + delta));
                        break;
                    case GestureStateCancelled /*4*/:
                        curveValue.highlightsLevel = Math.max(0.0f, Math.min(100.0f, curveValue.highlightsLevel + delta));
                        break;
                    case GestureStateFailed /*5*/:
                        curveValue.whitesLevel = Math.max(0.0f, Math.min(100.0f, curveValue.whitesLevel + delta));
                        break;
                }
                invalidate();
                if (this.delegate != null) {
                    this.delegate.valueChanged();
                }
                this.lastX = locationX;
                this.lastY = locationY;
            case GestureStateEnded /*3*/:
            case GestureStateCancelled /*4*/:
            case GestureStateFailed /*5*/:
                unselectSegments();
            default:
        }
    }

    private void selectSegmentWithPoint(float pointx) {
        if (this.activeSegment == 0) {
            this.activeSegment = (int) Math.floor((double) (((pointx - this.actualArea.f16x) / (this.actualArea.width / 5.0f)) + TouchHelperCallback.ALPHA_FULL));
        }
    }

    private void unselectSegments() {
        if (this.activeSegment != 0) {
            this.activeSegment = CurvesSegmentNone;
        }
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        Canvas canvas2;
        int a;
        float segmentWidth = this.actualArea.width / 5.0f;
        for (int i = CurvesSegmentNone; i < GestureStateCancelled; i += GestureStateBegan) {
            canvas2 = canvas;
            canvas2.drawLine((((float) i) * segmentWidth) + (this.actualArea.f16x + segmentWidth), this.actualArea.f17y, (((float) i) * segmentWidth) + (this.actualArea.f16x + segmentWidth), this.actualArea.height + this.actualArea.f17y, this.paint);
        }
        canvas2 = canvas;
        canvas2.drawLine(this.actualArea.f16x, this.actualArea.height + this.actualArea.f17y, this.actualArea.width + this.actualArea.f16x, this.actualArea.f17y, this.paintDash);
        CurvesValue curvesValue = null;
        switch (this.curveValue.activeType) {
            case CurvesSegmentNone /*0*/:
                this.paintCurve.setColor(-1);
                curvesValue = this.curveValue.luminanceCurve;
                break;
            case GestureStateBegan /*1*/:
                this.paintCurve.setColor(-1229492);
                curvesValue = this.curveValue.redCurve;
                break;
            case GestureStateChanged /*2*/:
                this.paintCurve.setColor(-15667555);
                curvesValue = this.curveValue.greenCurve;
                break;
            case GestureStateEnded /*3*/:
                this.paintCurve.setColor(-13404165);
                curvesValue = this.curveValue.blueCurve;
                break;
        }
        for (a = CurvesSegmentNone; a < GestureStateFailed; a += GestureStateBegan) {
            String str;
            Object[] objArr;
            switch (a) {
                case CurvesSegmentNone /*0*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.blacksLevel / 100.0f);
                    str = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateBegan /*1*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.shadowsLevel / 100.0f);
                    str = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateChanged /*2*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.midtonesLevel / 100.0f);
                    str = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateEnded /*3*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.highlightsLevel / 100.0f);
                    str = String.format(Locale.US, "%.2f", objArr);
                    break;
                case GestureStateCancelled /*4*/:
                    objArr = new Object[GestureStateBegan];
                    objArr[CurvesSegmentNone] = Float.valueOf(curvesValue.whitesLevel / 100.0f);
                    str = String.format(Locale.US, "%.2f", objArr);
                    break;
                default:
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    break;
            }
            canvas.drawText(str, (this.actualArea.f16x + ((segmentWidth - this.textPaint.measureText(str)) / 2.0f)) + (((float) a) * segmentWidth), (this.actualArea.f17y + this.actualArea.height) - ((float) AndroidUtilities.dp(4.0f)), this.textPaint);
        }
        float[] points = curvesValue.interpolateCurve();
        invalidate();
        this.path.reset();
        for (a = CurvesSegmentNone; a < points.length / GestureStateChanged; a += GestureStateBegan) {
            if (a == 0) {
                this.path.moveTo(this.actualArea.f16x + (points[a * GestureStateChanged] * this.actualArea.width), this.actualArea.f17y + ((TouchHelperCallback.ALPHA_FULL - points[(a * GestureStateChanged) + GestureStateBegan]) * this.actualArea.height));
            } else {
                this.path.lineTo(this.actualArea.f16x + (points[a * GestureStateChanged] * this.actualArea.width), this.actualArea.f17y + ((TouchHelperCallback.ALPHA_FULL - points[(a * GestureStateChanged) + GestureStateBegan]) * this.actualArea.height));
            }
        }
        canvas.drawPath(this.path, this.paintCurve);
    }
}
