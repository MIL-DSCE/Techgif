package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class zzz implements Creator<ResolveAccountResponse> {
    static void zza(ResolveAccountResponse resolveAccountResponse, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, resolveAccountResponse.mVersionCode);
        zzb.zza(parcel, 2, resolveAccountResponse.zzakA, false);
        zzb.zza(parcel, 3, resolveAccountResponse.zzqY(), i, false);
        zzb.zza(parcel, 4, resolveAccountResponse.zzqZ());
        zzb.zza(parcel, 5, resolveAccountResponse.zzra());
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaq(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzbX(i);
    }

    public ResolveAccountResponse zzaq(Parcel parcel) {
        ConnectionResult connectionResult = null;
        boolean z = false;
        int zzau = zza.zzau(parcel);
        boolean z2 = false;
        IBinder iBinder = null;
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
                    connectionResult = (ConnectionResult) zza.zza(parcel, zzat, ConnectionResult.CREATOR);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new ResolveAccountResponse(i, iBinder, connectionResult, z2, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public ResolveAccountResponse[] zzbX(int i) {
        return new ResolveAccountResponse[i];
    }
}
