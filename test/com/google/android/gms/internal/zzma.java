package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.SystemClock;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public final class zzma extends Drawable implements Callback {
    private int mFrom;
    private long zzRD;
    private boolean zzajT;
    private int zzaka;
    private int zzakb;
    private int zzakc;
    private int zzakd;
    private int zzake;
    private boolean zzakf;
    private zzb zzakg;
    private Drawable zzakh;
    private Drawable zzaki;
    private boolean zzakj;
    private boolean zzakk;
    private boolean zzakl;
    private int zzakm;

    private static final class zza extends Drawable {
        private static final zza zzakn;
        private static final zza zzako;

        private static final class zza extends ConstantState {
            private zza() {
            }

            public int getChangingConfigurations() {
                return 0;
            }

            public Drawable newDrawable() {
                return zza.zzakn;
            }
        }

        static {
            zzakn = new zza();
            zzako = new zza();
        }

        private zza() {
        }

        public void draw(Canvas canvas) {
        }

        public ConstantState getConstantState() {
            return zzako;
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter cf) {
        }
    }

    static final class zzb extends ConstantState {
        int zzakp;
        int zzakq;

        zzb(zzb com_google_android_gms_internal_zzma_zzb) {
            if (com_google_android_gms_internal_zzma_zzb != null) {
                this.zzakp = com_google_android_gms_internal_zzma_zzb.zzakp;
                this.zzakq = com_google_android_gms_internal_zzma_zzb.zzakq;
            }
        }

        public int getChangingConfigurations() {
            return this.zzakp;
        }

        public Drawable newDrawable() {
            return new zzma(this);
        }
    }

    public zzma(Drawable drawable, Drawable drawable2) {
        this(null);
        if (drawable == null) {
            drawable = zza.zzakn;
        }
        this.zzakh = drawable;
        drawable.setCallback(this);
        zzb com_google_android_gms_internal_zzma_zzb = this.zzakg;
        com_google_android_gms_internal_zzma_zzb.zzakq |= drawable.getChangingConfigurations();
        if (drawable2 == null) {
            drawable2 = zza.zzakn;
        }
        this.zzaki = drawable2;
        drawable2.setCallback(this);
        com_google_android_gms_internal_zzma_zzb = this.zzakg;
        com_google_android_gms_internal_zzma_zzb.zzakq |= drawable2.getChangingConfigurations();
    }

    zzma(zzb com_google_android_gms_internal_zzma_zzb) {
        this.zzaka = 0;
        this.zzakc = NalUnitUtil.EXTENDED_SAR;
        this.zzake = 0;
        this.zzajT = true;
        this.zzakg = new zzb(com_google_android_gms_internal_zzma_zzb);
    }

    public boolean canConstantState() {
        if (!this.zzakj) {
            boolean z = (this.zzakh.getConstantState() == null || this.zzaki.getConstantState() == null) ? false : true;
            this.zzakk = z;
            this.zzakj = true;
        }
        return this.zzakk;
    }

    public void draw(Canvas canvas) {
        int i = 1;
        int i2 = 0;
        switch (this.zzaka) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                this.zzRD = SystemClock.uptimeMillis();
                this.zzaka = 2;
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                if (this.zzRD >= 0) {
                    float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.zzRD)) / ((float) this.zzakd);
                    if (uptimeMillis < TouchHelperCallback.ALPHA_FULL) {
                        i = 0;
                    }
                    if (i != 0) {
                        this.zzaka = 0;
                    }
                    float min = Math.min(uptimeMillis, TouchHelperCallback.ALPHA_FULL);
                    this.zzake = (int) ((min * ((float) (this.zzakb - this.mFrom))) + ((float) this.mFrom));
                    break;
                }
                break;
        }
        i2 = i;
        i = this.zzake;
        boolean z = this.zzajT;
        Drawable drawable = this.zzakh;
        Drawable drawable2 = this.zzaki;
        if (i2 != 0) {
            if (!z || i == 0) {
                drawable.draw(canvas);
            }
            if (i == this.zzakc) {
                drawable2.setAlpha(this.zzakc);
                drawable2.draw(canvas);
                return;
            }
            return;
        }
        if (z) {
            drawable.setAlpha(this.zzakc - i);
        }
        drawable.draw(canvas);
        if (z) {
            drawable.setAlpha(this.zzakc);
        }
        if (i > 0) {
            drawable2.setAlpha(i);
            drawable2.draw(canvas);
            drawable2.setAlpha(this.zzakc);
        }
        invalidateSelf();
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.zzakg.zzakp) | this.zzakg.zzakq;
    }

    public ConstantState getConstantState() {
        if (!canConstantState()) {
            return null;
        }
        this.zzakg.zzakp = getChangingConfigurations();
        return this.zzakg;
    }

    public int getIntrinsicHeight() {
        return Math.max(this.zzakh.getIntrinsicHeight(), this.zzaki.getIntrinsicHeight());
    }

    public int getIntrinsicWidth() {
        return Math.max(this.zzakh.getIntrinsicWidth(), this.zzaki.getIntrinsicWidth());
    }

    public int getOpacity() {
        if (!this.zzakl) {
            this.zzakm = Drawable.resolveOpacity(this.zzakh.getOpacity(), this.zzaki.getOpacity());
            this.zzakl = true;
        }
        return this.zzakm;
    }

    @TargetApi(11)
    public void invalidateDrawable(Drawable who) {
        if (zzne.zzsd()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.invalidateDrawable(this);
            }
        }
    }

    public Drawable mutate() {
        if (!this.zzakf && super.mutate() == this) {
            if (canConstantState()) {
                this.zzakh.mutate();
                this.zzaki.mutate();
                this.zzakf = true;
            } else {
                throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
            }
        }
        return this;
    }

    protected void onBoundsChange(Rect bounds) {
        this.zzakh.setBounds(bounds);
        this.zzaki.setBounds(bounds);
    }

    @TargetApi(11)
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (zzne.zzsd()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.scheduleDrawable(this, what, when);
            }
        }
    }

    public void setAlpha(int alpha) {
        if (this.zzake == this.zzakc) {
            this.zzake = alpha;
        }
        this.zzakc = alpha;
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter cf) {
        this.zzakh.setColorFilter(cf);
        this.zzaki.setColorFilter(cf);
    }

    public void startTransition(int durationMillis) {
        this.mFrom = 0;
        this.zzakb = this.zzakc;
        this.zzake = 0;
        this.zzakd = durationMillis;
        this.zzaka = 1;
        invalidateSelf();
    }

    @TargetApi(11)
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (zzne.zzsd()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.unscheduleDrawable(this, what);
            }
        }
    }

    public Drawable zzqn() {
        return this.zzaki;
    }
}
