package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.widget.ImageView;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;

public final class zzmc extends ImageView {
    private Uri zzakr;
    private int zzaks;
    private int zzakt;
    private zza zzaku;
    private int zzakv;
    private float zzakw;

    public interface zza {
        Path zzl(int i, int i2);
    }

    protected void onDraw(Canvas canvas) {
        if (this.zzaku != null) {
            canvas.clipPath(this.zzaku.zzl(getWidth(), getHeight()));
        }
        super.onDraw(canvas);
        if (this.zzakt != 0) {
            canvas.drawColor(this.zzakt);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight;
        int i;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (this.zzakv) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                measuredHeight = getMeasuredHeight();
                i = (int) (((float) measuredHeight) * this.zzakw);
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                i = getMeasuredWidth();
                measuredHeight = (int) (((float) i) / this.zzakw);
                break;
            default:
                return;
        }
        setMeasuredDimension(i, measuredHeight);
    }

    public void zzbO(int i) {
        this.zzaks = i;
    }

    public void zzm(Uri uri) {
        this.zzakr = uri;
    }

    public int zzqp() {
        return this.zzaks;
    }
}
