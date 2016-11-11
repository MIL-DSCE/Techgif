package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public class zzh implements Creator<PolygonOptions> {
    static void zza(PolygonOptions polygonOptions, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, polygonOptions.getVersionCode());
        zzb.zzc(parcel, 2, polygonOptions.getPoints(), false);
        zzb.zzd(parcel, 3, polygonOptions.zzAl(), false);
        zzb.zza(parcel, 4, polygonOptions.getStrokeWidth());
        zzb.zzc(parcel, 5, polygonOptions.getStrokeColor());
        zzb.zzc(parcel, 6, polygonOptions.getFillColor());
        zzb.zza(parcel, 7, polygonOptions.getZIndex());
        zzb.zza(parcel, 8, polygonOptions.isVisible());
        zzb.zza(parcel, 9, polygonOptions.isGeodesic());
        zzb.zza(parcel, 10, polygonOptions.isClickable());
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfC(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zziq(i);
    }

    public PolygonOptions zzfC(Parcel parcel) {
        float f = 0.0f;
        boolean z = false;
        int zzau = zza.zzau(parcel);
        List list = null;
        List arrayList = new ArrayList();
        boolean z2 = false;
        boolean z3 = false;
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
                    list = zza.zzc(parcel, zzat, LatLng.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    zza.zza(parcel, zzat, arrayList, getClass().getClassLoader());
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
                    z3 = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new PolygonOptions(i3, list, arrayList, f2, i2, i, f, z3, z2, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public PolygonOptions[] zziq(int i) {
        return new PolygonOptions[i];
    }
}
