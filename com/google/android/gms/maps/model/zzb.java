package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public class zzb implements Creator<CircleOptions> {
    static void zza(CircleOptions circleOptions, Parcel parcel, int i) {
        int zzav = com.google.android.gms.common.internal.safeparcel.zzb.zzav(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, circleOptions.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, circleOptions.getCenter(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, circleOptions.getRadius());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, circleOptions.getStrokeWidth());
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 5, circleOptions.getStrokeColor());
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 6, circleOptions.getFillColor());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, circleOptions.getZIndex());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, circleOptions.isVisible());
        com.google.android.gms.common.internal.safeparcel.zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfw(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzik(i);
    }

    public CircleOptions zzfw(Parcel parcel) {
        float f = 0.0f;
        boolean z = false;
        int zzau = zza.zzau(parcel);
        LatLng latLng = null;
        double d = 0.0d;
        int i = 0;
        int i2 = 0;
        float f2 = 0.0f;
        int i3 = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i3 = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    latLng = (LatLng) zza.zza(parcel, zzat, LatLng.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    d = zza.zzn(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    f2 = zza.zzl(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    f = zza.zzl(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new CircleOptions(i3, latLng, d, f2, i2, i, f, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public CircleOptions[] zzik(int i) {
        return new CircleOptions[i];
    }
}
