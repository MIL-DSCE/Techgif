package com.google.android.gms.ads.internal.request;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.ads.internal.reward.mediation.client.RewardItemParcel;
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

public class zzh implements Creator<AdResponseParcel> {
    static void zza(AdResponseParcel adResponseParcel, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, adResponseParcel.versionCode);
        zzb.zza(parcel, 2, adResponseParcel.zzEF, false);
        zzb.zza(parcel, 3, adResponseParcel.body, false);
        zzb.zzb(parcel, 4, adResponseParcel.zzBQ, false);
        zzb.zzc(parcel, 5, adResponseParcel.errorCode);
        zzb.zzb(parcel, 6, adResponseParcel.zzBR, false);
        zzb.zza(parcel, 7, adResponseParcel.zzHS);
        zzb.zza(parcel, 8, adResponseParcel.zzHT);
        zzb.zza(parcel, 9, adResponseParcel.zzHU);
        zzb.zzb(parcel, 10, adResponseParcel.zzHV, false);
        zzb.zza(parcel, 11, adResponseParcel.zzBU);
        zzb.zzc(parcel, 12, adResponseParcel.orientation);
        zzb.zza(parcel, 13, adResponseParcel.zzHW, false);
        zzb.zza(parcel, 14, adResponseParcel.zzHX);
        zzb.zza(parcel, 15, adResponseParcel.zzHY, false);
        zzb.zza(parcel, 19, adResponseParcel.zzIa, false);
        zzb.zza(parcel, 18, adResponseParcel.zzHZ);
        zzb.zza(parcel, 21, adResponseParcel.zzIb, false);
        zzb.zza(parcel, 23, adResponseParcel.zzuk);
        zzb.zza(parcel, 22, adResponseParcel.zzIc);
        zzb.zza(parcel, 25, adResponseParcel.zzId);
        zzb.zza(parcel, 24, adResponseParcel.zzHB);
        zzb.zzc(parcel, 27, adResponseParcel.zzIf);
        zzb.zza(parcel, 26, adResponseParcel.zzIe);
        zzb.zza(parcel, 29, adResponseParcel.zzIh, false);
        zzb.zza(parcel, 28, adResponseParcel.zzIg, i, false);
        zzb.zza(parcel, 31, adResponseParcel.zzul);
        zzb.zza(parcel, 30, adResponseParcel.zzIi, false);
        zzb.zzb(parcel, 34, adResponseParcel.zzIk, false);
        zzb.zzb(parcel, 35, adResponseParcel.zzIl, false);
        zzb.zza(parcel, 32, adResponseParcel.zzum);
        zzb.zza(parcel, 33, adResponseParcel.zzIj, i, false);
        zzb.zza(parcel, 36, adResponseParcel.zzIm);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzj(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzH(i);
    }

    public AdResponseParcel[] zzH(int i) {
        return new AdResponseParcel[i];
    }

    public AdResponseParcel zzj(Parcel parcel) {
        int zzau = zza.zzau(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        List list = null;
        int i2 = 0;
        List list2 = null;
        long j = 0;
        boolean z = false;
        long j2 = 0;
        List list3 = null;
        long j3 = 0;
        int i3 = 0;
        String str3 = null;
        long j4 = 0;
        String str4 = null;
        boolean z2 = false;
        String str5 = null;
        String str6 = null;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        int i4 = 0;
        LargeParcelTeleporter largeParcelTeleporter = null;
        String str7 = null;
        String str8 = null;
        boolean z8 = false;
        boolean z9 = false;
        RewardItemParcel rewardItemParcel = null;
        List list4 = null;
        List list5 = null;
        boolean z10 = false;
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
                    list = zza.zzD(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    list2 = zza.zzD(parcel, zzat);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    j = zza.zzi(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    j2 = zza.zzi(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    list3 = zza.zzD(parcel, zzat);
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    j3 = zza.zzi(parcel, zzat);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    i3 = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    str3 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.TIMEOUT /*14*/:
                    j4 = zza.zzi(parcel, zzat);
                    break;
                case ConnectionResult.INTERRUPTED /*15*/:
                    str4 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_UPDATING /*18*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                    str5 = zza.zzp(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    str6 = zza.zzp(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL22 /*22*/:
                    z3 = zza.zzc(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL23 /*23*/:
                    z4 = zza.zzc(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL24 /*24*/:
                    z5 = zza.zzc(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL25 /*25*/:
                    z6 = zza.zzc(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    z7 = zza.zzc(parcel, zzat);
                    break;
                case OggUtil.PAGE_HEADER_SIZE /*27*/:
                    i4 = zza.zzg(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL28 /*28*/:
                    largeParcelTeleporter = (LargeParcelTeleporter) zza.zza(parcel, zzat, LargeParcelTeleporter.CREATOR);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /*29*/:
                    str7 = zza.zzp(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL30 /*30*/:
                    str8 = zza.zzp(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL31 /*31*/:
                    z8 = zza.zzc(parcel, zzat);
                    break;
                case ItemTouchHelper.END /*32*/:
                    z9 = zza.zzc(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                    rewardItemParcel = (RewardItemParcel) zza.zza(parcel, zzat, RewardItemParcel.CREATOR);
                    break;
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                    list4 = zza.zzD(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                    list5 = zza.zzD(parcel, zzat);
                    break;
                case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                    z10 = zza.zzc(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new AdResponseParcel(i, str, str2, list, i2, list2, j, z, j2, list3, j3, i3, str3, j4, str4, z2, str5, str6, z3, z4, z5, z6, z7, i4, largeParcelTeleporter, str7, str8, z8, z9, rewardItemParcel, list4, list5, z10);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }
}
