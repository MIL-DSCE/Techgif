package com.google.android.gms.auth.api.signin;

import android.net.Uri;
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

public class zze implements Creator<SignInAccount> {
    static void zza(SignInAccount signInAccount, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, signInAccount.versionCode);
        zzb.zza(parcel, 2, signInAccount.zzmT(), false);
        zzb.zza(parcel, 3, signInAccount.getIdToken(), false);
        zzb.zza(parcel, 4, signInAccount.getEmail(), false);
        zzb.zza(parcel, 5, signInAccount.getDisplayName(), false);
        zzb.zza(parcel, 6, signInAccount.getPhotoUrl(), i, false);
        zzb.zza(parcel, 7, signInAccount.zzmV(), i, false);
        zzb.zza(parcel, 8, signInAccount.getUserId(), false);
        zzb.zza(parcel, 9, signInAccount.zzmW(), false);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzT(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaO(i);
    }

    public SignInAccount zzT(Parcel parcel) {
        String str = null;
        int zzau = zza.zzau(parcel);
        int i = 0;
        String str2 = TtmlNode.ANONYMOUS_REGION_ID;
        GoogleSignInAccount googleSignInAccount = null;
        Uri uri = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    str6 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    str5 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str4 = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    str3 = zza.zzp(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    uri = (Uri) zza.zza(parcel, zzat, Uri.CREATOR);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    googleSignInAccount = (GoogleSignInAccount) zza.zza(parcel, zzat, GoogleSignInAccount.CREATOR);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new SignInAccount(i, str6, str5, str4, str3, uri, googleSignInAccount, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public SignInAccount[] zzaO(int i) {
        return new SignInAccount[i];
    }
}
