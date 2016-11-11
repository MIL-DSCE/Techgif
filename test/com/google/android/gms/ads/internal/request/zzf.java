package com.google.android.gms.ads.internal.request;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public class zzf implements Creator<AdRequestInfoParcel> {
    static void zza(AdRequestInfoParcel adRequestInfoParcel, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, adRequestInfoParcel.versionCode);
        zzb.zza(parcel, 2, adRequestInfoParcel.zzHs, false);
        zzb.zza(parcel, 3, adRequestInfoParcel.zzHt, i, false);
        zzb.zza(parcel, 4, adRequestInfoParcel.zzrp, i, false);
        zzb.zza(parcel, 5, adRequestInfoParcel.zzrj, false);
        zzb.zza(parcel, 6, adRequestInfoParcel.applicationInfo, i, false);
        zzb.zza(parcel, 7, adRequestInfoParcel.zzHu, i, false);
        zzb.zza(parcel, 8, adRequestInfoParcel.zzHv, false);
        zzb.zza(parcel, 9, adRequestInfoParcel.zzHw, false);
        zzb.zza(parcel, 10, adRequestInfoParcel.zzHx, false);
        zzb.zza(parcel, 11, adRequestInfoParcel.zzrl, i, false);
        zzb.zza(parcel, 12, adRequestInfoParcel.zzHy, false);
        zzb.zzc(parcel, 13, adRequestInfoParcel.zzHz);
        zzb.zzb(parcel, 14, adRequestInfoParcel.zzrH, false);
        zzb.zza(parcel, 15, adRequestInfoParcel.zzHA, false);
        zzb.zza(parcel, 17, adRequestInfoParcel.zzHC, i, false);
        zzb.zza(parcel, 16, adRequestInfoParcel.zzHB);
        zzb.zzc(parcel, 19, adRequestInfoParcel.zzHE);
        zzb.zzc(parcel, 18, adRequestInfoParcel.zzHD);
        zzb.zza(parcel, 21, adRequestInfoParcel.zzHG, false);
        zzb.zza(parcel, 20, adRequestInfoParcel.zzHF);
        zzb.zza(parcel, 25, adRequestInfoParcel.zzHH);
        zzb.zzb(parcel, 27, adRequestInfoParcel.zzHJ, false);
        zzb.zza(parcel, 26, adRequestInfoParcel.zzHI, false);
        zzb.zza(parcel, 29, adRequestInfoParcel.zzrD, i, false);
        zzb.zza(parcel, 28, adRequestInfoParcel.zzri, false);
        zzb.zza(parcel, 31, adRequestInfoParcel.zzHL);
        zzb.zzb(parcel, 30, adRequestInfoParcel.zzHK, false);
        zzb.zza(parcel, 34, adRequestInfoParcel.zzHO);
        zzb.zzc(parcel, 35, adRequestInfoParcel.zzHP);
        zzb.zza(parcel, 32, adRequestInfoParcel.zzHM, i, false);
        zzb.zza(parcel, 33, adRequestInfoParcel.zzHN, false);
        zzb.zzc(parcel, 36, adRequestInfoParcel.zzHQ);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzi(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzG(i);
    }

    public AdRequestInfoParcel[] zzG(int i) {
        return new AdRequestInfoParcel[i];
    }

    public AdRequestInfoParcel zzi(Parcel parcel) {
        int zzau = zza.zzau(parcel);
        int i = 0;
        Bundle bundle = null;
        AdRequestParcel adRequestParcel = null;
        AdSizeParcel adSizeParcel = null;
        String str = null;
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        VersionInfoParcel versionInfoParcel = null;
        Bundle bundle2 = null;
        int i2 = 0;
        List list = null;
        Bundle bundle3 = null;
        boolean z = false;
        Messenger messenger = null;
        int i3 = 0;
        int i4 = 0;
        float f = 0.0f;
        String str5 = null;
        long j = 0;
        String str6 = null;
        List list2 = null;
        String str7 = null;
        NativeAdOptionsParcel nativeAdOptionsParcel = null;
        List list3 = null;
        long j2 = 0;
        CapabilityParcel capabilityParcel = null;
        String str8 = null;
        float f2 = 0.0f;
        int i5 = 0;
        int i6 = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    bundle = zza.zzr(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    adRequestParcel = (AdRequestParcel) zza.zza(parcel, zzat, AdRequestParcel.CREATOR);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    adSizeParcel = (AdSizeParcel) zza.zza(parcel, zzat, AdSizeParcel.CREATOR);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    applicationInfo = (ApplicationInfo) zza.zza(parcel, zzat, ApplicationInfo.CREATOR);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    packageInfo = (PackageInfo) zza.zza(parcel, zzat, PackageInfo.CREATOR);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    str3 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    str4 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    versionInfoParcel = (VersionInfoParcel) zza.zza(parcel, zzat, VersionInfoParcel.CREATOR);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    bundle2 = zza.zzr(parcel, zzat);
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.TIMEOUT /*14*/:
                    list = zza.zzD(parcel, zzat);
                    break;
                case ConnectionResult.INTERRUPTED /*15*/:
                    bundle3 = zza.zzr(parcel, zzat);
                    break;
                case ItemTouchHelper.START /*16*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.SIGN_IN_FAILED /*17*/:
                    messenger = (Messenger) zza.zza(parcel, zzat, Messenger.CREATOR);
                    break;
                case ConnectionResult.SERVICE_UPDATING /*18*/:
                    i3 = zza.zzg(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                    i4 = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                    f = zza.zzl(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    str5 = zza.zzp(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL25 /*25*/:
                    j = zza.zzi(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    str6 = zza.zzp(parcel, zzat);
                    break;
                case OggUtil.PAGE_HEADER_SIZE /*27*/:
                    list2 = zza.zzD(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL28 /*28*/:
                    str7 = zza.zzp(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /*29*/:
                    nativeAdOptionsParcel = (NativeAdOptionsParcel) zza.zza(parcel, zzat, NativeAdOptionsParcel.CREATOR);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL30 /*30*/:
                    list3 = zza.zzD(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL31 /*31*/:
                    j2 = zza.zzi(parcel, zzat);
                    break;
                case ItemTouchHelper.END /*32*/:
                    capabilityParcel = (CapabilityParcel) zza.zza(parcel, zzat, CapabilityParcel.CREATOR);
                    break;
                case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                    str8 = zza.zzp(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                    f2 = zza.zzl(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                    i5 = zza.zzg(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                    i6 = zza.zzg(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new AdRequestInfoParcel(i, bundle, adRequestParcel, adSizeParcel, str, applicationInfo, packageInfo, str2, str3, str4, versionInfoParcel, bundle2, i2, list, bundle3, z, messenger, i3, i4, f, str5, j, str6, list2, str7, nativeAdOptionsParcel, list3, j2, capabilityParcel, str8, f2, i5, i6);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }
}
