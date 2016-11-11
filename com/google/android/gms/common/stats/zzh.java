package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public class zzh implements Creator<WakeLockEvent> {
    static void zza(WakeLockEvent wakeLockEvent, Parcel parcel, int i) {
        int zzav = zzb.zzav(parcel);
        zzb.zzc(parcel, 1, wakeLockEvent.mVersionCode);
        zzb.zza(parcel, 2, wakeLockEvent.getTimeMillis());
        zzb.zza(parcel, 4, wakeLockEvent.zzrR(), false);
        zzb.zzc(parcel, 5, wakeLockEvent.zzrT());
        zzb.zzb(parcel, 6, wakeLockEvent.zzrU(), false);
        zzb.zza(parcel, 8, wakeLockEvent.zzrN());
        zzb.zza(parcel, 10, wakeLockEvent.zzrS(), false);
        zzb.zzc(parcel, 11, wakeLockEvent.getEventType());
        zzb.zza(parcel, 12, wakeLockEvent.zzrK(), false);
        zzb.zza(parcel, 13, wakeLockEvent.zzrW(), false);
        zzb.zzc(parcel, 14, wakeLockEvent.zzrV());
        zzb.zza(parcel, 15, wakeLockEvent.zzrX());
        zzb.zza(parcel, 16, wakeLockEvent.zzrY());
        zzb.zzI(parcel, zzav);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaG(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcm(i);
    }

    public WakeLockEvent zzaG(Parcel parcel) {
        int zzau = zza.zzau(parcel);
        int i = 0;
        long j = 0;
        int i2 = 0;
        String str = null;
        int i3 = 0;
        List list = null;
        String str2 = null;
        long j2 = 0;
        int i4 = 0;
        String str3 = null;
        String str4 = null;
        float f = 0.0f;
        long j3 = 0;
        while (parcel.dataPosition() < zzau) {
            int zzat = zza.zzat(parcel);
            switch (zza.zzca(zzat)) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    i = zza.zzg(parcel, zzat);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    j = zza.zzi(parcel, zzat);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    str = zza.zzp(parcel, zzat);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    i3 = zza.zzg(parcel, zzat);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    list = zza.zzD(parcel, zzat);
                    break;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    j2 = zza.zzi(parcel, zzat);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    str3 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    i2 = zza.zzg(parcel, zzat);
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    str2 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    str4 = zza.zzp(parcel, zzat);
                    break;
                case ConnectionResult.TIMEOUT /*14*/:
                    i4 = zza.zzg(parcel, zzat);
                    break;
                case ConnectionResult.INTERRUPTED /*15*/:
                    f = zza.zzl(parcel, zzat);
                    break;
                case ItemTouchHelper.START /*16*/:
                    j3 = zza.zzi(parcel, zzat);
                    break;
                default:
                    zza.zzb(parcel, zzat);
                    break;
            }
        }
        if (parcel.dataPosition() == zzau) {
            return new WakeLockEvent(i, j, i2, str, i3, list, str2, j2, i4, str3, str4, f, j3);
        }
        throw new zza.zza("Overread allowed size end=" + zzau, parcel);
    }

    public WakeLockEvent[] zzcm(int i) {
        return new WakeLockEvent[i];
    }
}
