package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzpz.zzd;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

class zzs {
    final boolean zzaWY;
    final int zzaWZ;
    long zzaXa;
    float zzaXb;
    long zzaXc;
    float zzaXd;
    long zzaXe;
    float zzaXf;
    final boolean zzaXg;

    public zzs(zzd com_google_android_gms_internal_zzpz_zzd) {
        boolean z;
        boolean z2 = true;
        zzx.zzz(com_google_android_gms_internal_zzpz_zzd);
        if (com_google_android_gms_internal_zzpz_zzd.zzaZF == null || com_google_android_gms_internal_zzpz_zzd.zzaZF.intValue() == 0) {
            z = false;
        } else {
            if (com_google_android_gms_internal_zzpz_zzd.zzaZF.intValue() != 4) {
                if (com_google_android_gms_internal_zzpz_zzd.zzaZH == null) {
                    z = false;
                }
            } else if (com_google_android_gms_internal_zzpz_zzd.zzaZI == null || com_google_android_gms_internal_zzpz_zzd.zzaZJ == null) {
                z = false;
            }
            z = true;
        }
        if (z) {
            this.zzaWZ = com_google_android_gms_internal_zzpz_zzd.zzaZF.intValue();
            if (com_google_android_gms_internal_zzpz_zzd.zzaZG == null || !com_google_android_gms_internal_zzpz_zzd.zzaZG.booleanValue()) {
                z2 = false;
            }
            this.zzaWY = z2;
            if (com_google_android_gms_internal_zzpz_zzd.zzaZF.intValue() == 4) {
                if (this.zzaWY) {
                    this.zzaXd = Float.parseFloat(com_google_android_gms_internal_zzpz_zzd.zzaZI);
                    this.zzaXf = Float.parseFloat(com_google_android_gms_internal_zzpz_zzd.zzaZJ);
                } else {
                    this.zzaXc = Long.parseLong(com_google_android_gms_internal_zzpz_zzd.zzaZI);
                    this.zzaXe = Long.parseLong(com_google_android_gms_internal_zzpz_zzd.zzaZJ);
                }
            } else if (this.zzaWY) {
                this.zzaXb = Float.parseFloat(com_google_android_gms_internal_zzpz_zzd.zzaZH);
            } else {
                this.zzaXa = Long.parseLong(com_google_android_gms_internal_zzpz_zzd.zzaZH);
            }
        } else {
            this.zzaWZ = 0;
            this.zzaWY = false;
        }
        this.zzaXg = z;
    }

    public Boolean zzac(long j) {
        boolean z = true;
        if (!this.zzaXg) {
            return null;
        }
        if (this.zzaWY) {
            return null;
        }
        switch (this.zzaWZ) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                if (j >= this.zzaXa) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                if (j <= this.zzaXa) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                if (j != this.zzaXa) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case VideoPlayer.STATE_READY /*4*/:
                if (j < this.zzaXc || j > this.zzaXe) {
                    z = false;
                }
                return Boolean.valueOf(z);
            default:
                return null;
        }
    }

    public Boolean zzi(float f) {
        boolean z = true;
        boolean z2 = false;
        if (!this.zzaXg) {
            return null;
        }
        if (!this.zzaWY) {
            return null;
        }
        switch (this.zzaWZ) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                if (f >= this.zzaXb) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                if (f <= this.zzaXb) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                if (f == this.zzaXb || Math.abs(f - this.zzaXb) < 2.0f * Math.max(Math.ulp(f), Math.ulp(this.zzaXb))) {
                    z2 = true;
                }
                return Boolean.valueOf(z2);
            case VideoPlayer.STATE_READY /*4*/:
                if (f < this.zzaXd || f > this.zzaXf) {
                    z = false;
                }
                return Boolean.valueOf(z);
            default:
                return null;
        }
    }
}
