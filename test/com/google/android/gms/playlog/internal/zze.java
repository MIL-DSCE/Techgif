package com.google.android.gms.playlog.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public class zze implements Creator<PlayLoggerContext> {
    static void zza(PlayLoggerContext playLoggerContext, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, playLoggerContext.versionCode);
        zzb.zza(parcel, 2, playLoggerContext.packageName, false);
        zzb.zzc(parcel, 3, playLoggerContext.zzbdL);
        zzb.zzc(parcel, 4, playLoggerContext.zzbdM);
        zzb.zza(parcel, 5, playLoggerContext.zzbdN, false);
        zzb.zza(parcel, 6, playLoggerContext.zzbdO, false);
        zzb.zza(parcel, 7, playLoggerContext.zzbdP);
        zzb.zza(parcel, 8, playLoggerContext.zzbdQ, false);
        zzb.zza(parcel, 9, playLoggerContext.zzbdR);
        zzb.zzc(parcel, 10, playLoggerContext.zzbdS);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzgz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzjF(i);
    }

    public PlayLoggerContext zzgz(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzau = zza.zzau(parcel);
        boolean z = true;
        boolean z2 = false;
        String str2 = null;
        String str3 = null;
        int i2 = 0;
        int i3 = 0;
        String str4 = null;
        int i4 = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i4 = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    str4 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    i3 = zza.zzg(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str3 = zza.zzp(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new PlayLoggerContext(i4, str4, i3, i2, str3, str2, z, str, z2, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public PlayLoggerContext[] zzjF(int i) {
        return new PlayLoggerContext[i];
    }
}
