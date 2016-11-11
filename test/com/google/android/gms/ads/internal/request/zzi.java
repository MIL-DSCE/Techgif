package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class zzi implements Creator<CapabilityParcel> {
    static void zza(CapabilityParcel capabilityParcel, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, capabilityParcel.versionCode);
        zzb.zza(parcel, 2, capabilityParcel.zzIn);
        zzb.zza(parcel, 3, capabilityParcel.zzIo);
        zzb.zza(parcel, 4, capabilityParcel.zzIp);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzk(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzI(i);
    }

    public CapabilityParcel[] zzI(int i) {
        return new CapabilityParcel[i];
    }

    public CapabilityParcel zzk(Parcel parcel) {
        boolean z = false;
        int zzau = zza.zzau(parcel);
        boolean z2 = false;
        boolean z3 = false;
        int i = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    z3 = zza.zzc(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new CapabilityParcel(i, z3, z2, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }
}
