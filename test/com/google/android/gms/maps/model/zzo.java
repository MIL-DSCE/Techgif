package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class zzo implements Creator<TileOverlayOptions> {
    static void zza(TileOverlayOptions tileOverlayOptions, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, tileOverlayOptions.getVersionCode());
        zzb.zza(parcel, 2, tileOverlayOptions.zzAm(), false);
        zzb.zza(parcel, 3, tileOverlayOptions.isVisible());
        zzb.zza(parcel, 4, tileOverlayOptions.getZIndex());
        zzb.zza(parcel, 5, tileOverlayOptions.getFadeIn());
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfJ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzix(i);
    }

    public TileOverlayOptions zzfJ(Parcel parcel) {
        boolean z = false;
        int zzau = zza.zzau(parcel);
        IBinder iBinder = null;
        float f = 0.0f;
        boolean z2 = true;
        int i = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    iBinder = zza.zzq(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    f = zza.zzl(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new TileOverlayOptions(i, iBinder, z, f, z2);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public TileOverlayOptions[] zzix(int i) {
        return new TileOverlayOptions[i];
    }
}
