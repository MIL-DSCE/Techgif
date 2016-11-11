package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public class zzc implements Creator<GoogleSignInOptions> {
    static void zza(GoogleSignInOptions googleSignInOptions, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, googleSignInOptions.versionCode);
        zzb.zzc(parcel, 2, googleSignInOptions.zzmN(), false);
        zzb.zza(parcel, 3, googleSignInOptions.getAccount(), i, false);
        zzb.zza(parcel, 4, googleSignInOptions.zzmO());
        zzb.zza(parcel, 5, googleSignInOptions.zzmP());
        zzb.zza(parcel, 6, googleSignInOptions.zzmQ());
        zzb.zza(parcel, 7, googleSignInOptions.zzmR(), false);
        zzb.zza(parcel, 8, googleSignInOptions.zzmS(), false);
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzS(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaN(i);
    }

    public GoogleSignInOptions zzS(Parcel parcel) {
        String str = null;
        boolean z = false;
        int zzau = zza.zzau(parcel);
        String str2 = null;
        boolean z2 = false;
        boolean z3 = false;
        Account account = null;
        ArrayList arrayList = null;
        int i = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    arrayList = zza.zzc(parcel, zzat, Scope.CREATOR);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    account = (Account) zza.zza(parcel, zzat, Account.CREATOR);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    z3 = zza.zzc(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    z2 = zza.zzc(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    z = zza.zzc(parcel, zzat);
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new GoogleSignInOptions(i, arrayList, account, z3, z2, z, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public GoogleSignInOptions[] zzaN(int i) {
        return new GoogleSignInOptions[i];
    }
}
