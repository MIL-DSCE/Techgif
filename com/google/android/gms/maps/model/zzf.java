package com.google.android.gms.maps.model;

import android.os.IBinder;
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
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class zzf implements Creator<MarkerOptions> {
    static void zza(MarkerOptions markerOptions, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, markerOptions.getVersionCode());
        zzb.zza(parcel, 2, markerOptions.getPosition(), i, false);
        zzb.zza(parcel, 3, markerOptions.getTitle(), false);
        zzb.zza(parcel, 4, markerOptions.getSnippet(), false);
        zzb.zza(parcel, 5, markerOptions.zzAk(), false);
        zzb.zza(parcel, 6, markerOptions.getAnchorU());
        zzb.zza(parcel, 7, markerOptions.getAnchorV());
        zzb.zza(parcel, 8, markerOptions.isDraggable());
        zzb.zza(parcel, 9, markerOptions.isVisible());
        zzb.zza(parcel, 10, markerOptions.isFlat());
        zzb.zza(parcel, 11, markerOptions.getRotation());
        zzb.zza(parcel, 12, markerOptions.getInfoWindowAnchorU());
        zzb.zza(parcel, 13, markerOptions.getInfoWindowAnchorV());
        zzb.zza(parcel, 14, markerOptions.getAlpha());
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfA(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzio(i);
    }

    public MarkerOptions zzfA(Parcel parcel) {
        int zzau = zza.zzau(parcel);
        int i = 0;
        LatLng latLng = null;
        String str = null;
        String str2 = null;
        IBinder iBinder = null;
        float f = 0.0f;
        float f2 = 0.0f;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        float f3 = 0.0f;
        float f4 = 0.5f;
        float f5 = 0.0f;
        float f6 = TouchHelperCallback.ALPHA_FULL;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    latLng = (LatLng) zza.zza(parcel, zzat, LatLng.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    iBinder = zza.zzq(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    f = zza.zzl(parcel, zzat);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    f2 = zza.zzl(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    z3 = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    f3 = zza.zzl(parcel, zzat);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    f4 = zza.zzl(parcel, zzat);
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    f5 = zza.zzl(parcel, zzat);
                    break;
                case ConnectionResult.TIMEOUT /*14*/:
                    f6 = zza.zzl(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new MarkerOptions(i, latLng, str, str2, iBinder, f, f2, z, z2, z3, f3, f4, f5, f6);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public MarkerOptions[] zzio(int i) {
        return new MarkerOptions[i];
    }
}
