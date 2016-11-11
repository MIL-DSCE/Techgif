package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class zze implements Creator<SafeParcelResponse> {
    static void zza(SafeParcelResponse safeParcelResponse, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, safeParcelResponse.getVersionCode());
        zzb.zza(parcel, 2, safeParcelResponse.zzrD(), false);
        zzb.zza(parcel, 3, safeParcelResponse.zzrE(), i, false);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaE(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzck(i);
    }

    public SafeParcelResponse zzaE(Parcel parcel) {
        FieldMappingDictionary fieldMappingDictionary = null;
        int zzau = zza.zzau(parcel);
        int i = 0;
        Parcel parcel2 = null;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    parcel2 = zza.zzE(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    fieldMappingDictionary = (FieldMappingDictionary) zza.zza(parcel, zzat, FieldMappingDictionary.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new SafeParcelResponse(i, parcel2, fieldMappingDictionary);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public SafeParcelResponse[] zzck(int i) {
        return new SafeParcelResponse[i];
    }
}
