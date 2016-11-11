package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public class zzb implements Creator<AppMetadata> {
    static void zza(AppMetadata appMetadata, Parcel parcel, int i) {
        int zzav = com.google.android.gms.common.internal.safeparcel.zzb.zzav(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, appMetadata.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, appMetadata.packageName, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, appMetadata.zzaVt, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, appMetadata.zzaMV, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, appMetadata.zzaVu, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, appMetadata.zzaVv);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, appMetadata.zzaVw);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, appMetadata.zzaVx, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 9, appMetadata.zzaVy);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 10, appMetadata.zzaVz);
        com.google.android.gms.common.internal.safeparcel.zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfL(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zziH(i);
    }

    public AppMetadata zzfL(Parcel parcel) {
        int zzau = zza.zzau(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        long j = 0;
        long j2 = 0;
        String str5 = null;
        boolean z = false;
        boolean z2 = false;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str3 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str4 = zza.zzp(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    j = zza.zzi(parcel, zzat);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    j2 = zza.zzi(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    str5 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new AppMetadata(i, str, str2, str3, str4, j, j2, str5, z, z2);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public AppMetadata[] zziH(int i) {
        return new AppMetadata[i];
    }
}
