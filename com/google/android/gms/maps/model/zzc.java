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

public class zzc implements Creator<GroundOverlayOptions> {
    static void zza(GroundOverlayOptions groundOverlayOptions, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, groundOverlayOptions.getVersionCode());
        zzb.zza(parcel, 2, groundOverlayOptions.zzAj(), false);
        zzb.zza(parcel, 3, groundOverlayOptions.getLocation(), i, false);
        zzb.zza(parcel, 4, groundOverlayOptions.getWidth());
        zzb.zza(parcel, 5, groundOverlayOptions.getHeight());
        zzb.zza(parcel, 6, groundOverlayOptions.getBounds(), i, false);
        zzb.zza(parcel, 7, groundOverlayOptions.getBearing());
        zzb.zza(parcel, 8, groundOverlayOptions.getZIndex());
        zzb.zza(parcel, 9, groundOverlayOptions.isVisible());
        zzb.zza(parcel, 10, groundOverlayOptions.getTransparency());
        zzb.zza(parcel, 11, groundOverlayOptions.getAnchorU());
        zzb.zza(parcel, 12, groundOverlayOptions.getAnchorV());
        zzb.zza(parcel, 13, groundOverlayOptions.isClickable());
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzfx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzil(i);
    }

    public GroundOverlayOptions zzfx(Parcel parcel) {
        int zzau = zza.zzau(parcel);
        int i = 0;
        IBinder iBinder = null;
        LatLng latLng = null;
        float f = 0.0f;
        float f2 = 0.0f;
        LatLngBounds latLngBounds = null;
        float f3 = 0.0f;
        float f4 = 0.0f;
        boolean z = false;
        float f5 = 0.0f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        boolean z2 = false;
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
                    latLng = (LatLng) zza.zza(parcel, zzat, LatLng.CREATOR);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    f = zza.zzl(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    f2 = zza.zzl(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    latLngBounds = (LatLngBounds) zza.zza(parcel, zzat, LatLngBounds.CREATOR);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    f3 = zza.zzl(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    f4 = zza.zzl(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    f5 = zza.zzl(parcel, zzat);
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    f6 = zza.zzl(parcel, zzat);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    f7 = zza.zzl(parcel, zzat);
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new GroundOverlayOptions(i, iBinder, latLng, f, f2, latLngBounds, f3, f4, z, f5, f6, f7, z2);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public GroundOverlayOptions[] zzil(int i) {
        return new GroundOverlayOptions[i];
    }
}
