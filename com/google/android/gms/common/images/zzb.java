package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class zzb implements Creator<WebImage> {
    static void zza(WebImage webImage, Parcel parcel, int i) {
        int zzav = com.google.android.gms.common.internal.safeparcel.zzb.zzav(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, webImage.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, webImage.getUrl(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 3, webImage.getWidth());
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 4, webImage.getHeight());
        com.google.android.gms.common.internal.safeparcel.zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzal(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzbN(i);
    }

    public WebImage zzal(Parcel parcel) {
        int i = 0;
        int zzau = zza.zzau(parcel);
        Uri uri = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzau) {
            Uri uri2;
            int zzg;
            int zzat = zza.zzat(parcel);
            int i4;
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i4 = i;
                    i = i2;
                    uri2 = uri;
                    zzg = zza.zzg(parcel, zzat);
                    zzat = i4;
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    zzg = i3;
                    i4 = i2;
                    uri2 = (Uri) zza.zza(parcel, zzat, Uri.CREATOR);
                    zzat = i;
                    i = i4;
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    uri2 = uri;
                    zzg = i3;
                    i4 = i;
                    i = zza.zzg(parcel, zzat);
                    zzat = i4;
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    zzat = zza.zzg(parcel, zzat);
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    zzat = i;
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
            }
            i3 = zzg;
            uri = uri2;
            i2 = i;
            i = zzat;
        }
        if (parcel.dataPosition() == zzau) {
            return new WebImage(i3, uri, i2, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public WebImage[] zzbN(int i) {
        return new WebImage[i];
    }
}
