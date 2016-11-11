package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public interface zzpz {

    public static final class zza extends zzsu {
        private static volatile zza[] zzaZq;
        public Integer zzaZr;
        public zze[] zzaZs;
        public zzb[] zzaZt;

        public zza() {
            zzDB();
        }

        public static zza[] zzDA() {
            if (zzaZq == null) {
                synchronized (zzss.zzbut) {
                    if (zzaZq == null) {
                        zzaZq = new zza[0];
                    }
                }
            }
            return zzaZq;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzpz_zza = (zza) o;
            if (this.zzaZr == null) {
                if (com_google_android_gms_internal_zzpz_zza.zzaZr != null) {
                    return false;
                }
            } else if (!this.zzaZr.equals(com_google_android_gms_internal_zzpz_zza.zzaZr)) {
                return false;
            }
            if (zzss.equals(this.zzaZs, com_google_android_gms_internal_zzpz_zza.zzaZs)) {
                return zzss.equals(this.zzaZt, com_google_android_gms_internal_zzpz_zza.zzaZt);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return (((((this.zzaZr == null ? 0 : this.zzaZr.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzss.hashCode(this.zzaZs)) * 31) + zzss.hashCode(this.zzaZt);
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzt(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            int i = 0;
            if (this.zzaZr != null) {
                output.zzA(1, this.zzaZr.intValue());
            }
            if (this.zzaZs != null && this.zzaZs.length > 0) {
                for (zzsu com_google_android_gms_internal_zzsu : this.zzaZs) {
                    if (com_google_android_gms_internal_zzsu != null) {
                        output.zza(2, com_google_android_gms_internal_zzsu);
                    }
                }
            }
            if (this.zzaZt != null && this.zzaZt.length > 0) {
                while (i < this.zzaZt.length) {
                    zzsu com_google_android_gms_internal_zzsu2 = this.zzaZt[i];
                    if (com_google_android_gms_internal_zzsu2 != null) {
                        output.zza(3, com_google_android_gms_internal_zzsu2);
                    }
                    i++;
                }
            }
            super.writeTo(output);
        }

        public zza zzDB() {
            this.zzaZr = null;
            this.zzaZs = zze.zzDH();
            this.zzaZt = zzb.zzDC();
            this.zzbuu = -1;
            return this;
        }

        public zza zzt(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                int zzc;
                Object obj;
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        this.zzaZr = Integer.valueOf(com_google_android_gms_internal_zzsm.zzJb());
                        continue;
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 18);
                        zzIX = this.zzaZs == null ? 0 : this.zzaZs.length;
                        obj = new zze[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZs, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = new zze();
                            com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = new zze();
                        com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                        this.zzaZs = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 26);
                        zzIX = this.zzaZt == null ? 0 : this.zzaZt.length;
                        obj = new zzb[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZt, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = new zzb();
                            com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = new zzb();
                        com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                        this.zzaZt = obj;
                        continue;
                    default:
                        if (!zzsx.zzb(com_google_android_gms_internal_zzsm, zzIX)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzz() {
            int i = 0;
            int zzz = super.zzz();
            if (this.zzaZr != null) {
                zzz += zzsn.zzC(1, this.zzaZr.intValue());
            }
            if (this.zzaZs != null && this.zzaZs.length > 0) {
                int i2 = zzz;
                for (zzsu com_google_android_gms_internal_zzsu : this.zzaZs) {
                    if (com_google_android_gms_internal_zzsu != null) {
                        i2 += zzsn.zzc(2, com_google_android_gms_internal_zzsu);
                    }
                }
                zzz = i2;
            }
            if (this.zzaZt != null && this.zzaZt.length > 0) {
                while (i < this.zzaZt.length) {
                    zzsu com_google_android_gms_internal_zzsu2 = this.zzaZt[i];
                    if (com_google_android_gms_internal_zzsu2 != null) {
                        zzz += zzsn.zzc(3, com_google_android_gms_internal_zzsu2);
                    }
                    i++;
                }
            }
            return zzz;
        }
    }

    public static final class zzb extends zzsu {
        private static volatile zzb[] zzaZu;
        public Integer zzaZv;
        public String zzaZw;
        public zzc[] zzaZx;
        public Boolean zzaZy;
        public zzd zzaZz;

        public zzb() {
            zzDD();
        }

        public static zzb[] zzDC() {
            if (zzaZu == null) {
                synchronized (zzss.zzbut) {
                    if (zzaZu == null) {
                        zzaZu = new zzb[0];
                    }
                }
            }
            return zzaZu;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzpz_zzb = (zzb) o;
            if (this.zzaZv == null) {
                if (com_google_android_gms_internal_zzpz_zzb.zzaZv != null) {
                    return false;
                }
            } else if (!this.zzaZv.equals(com_google_android_gms_internal_zzpz_zzb.zzaZv)) {
                return false;
            }
            if (this.zzaZw == null) {
                if (com_google_android_gms_internal_zzpz_zzb.zzaZw != null) {
                    return false;
                }
            } else if (!this.zzaZw.equals(com_google_android_gms_internal_zzpz_zzb.zzaZw)) {
                return false;
            }
            if (!zzss.equals(this.zzaZx, com_google_android_gms_internal_zzpz_zzb.zzaZx)) {
                return false;
            }
            if (this.zzaZy == null) {
                if (com_google_android_gms_internal_zzpz_zzb.zzaZy != null) {
                    return false;
                }
            } else if (!this.zzaZy.equals(com_google_android_gms_internal_zzpz_zzb.zzaZy)) {
                return false;
            }
            return this.zzaZz == null ? com_google_android_gms_internal_zzpz_zzb.zzaZz == null : this.zzaZz.equals(com_google_android_gms_internal_zzpz_zzb.zzaZz);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzaZy == null ? 0 : this.zzaZy.hashCode()) + (((((this.zzaZw == null ? 0 : this.zzaZw.hashCode()) + (((this.zzaZv == null ? 0 : this.zzaZv.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzss.hashCode(this.zzaZx)) * 31)) * 31;
            if (this.zzaZz != null) {
                i = this.zzaZz.hashCode();
            }
            return hashCode + i;
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzu(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.zzaZv != null) {
                output.zzA(1, this.zzaZv.intValue());
            }
            if (this.zzaZw != null) {
                output.zzn(2, this.zzaZw);
            }
            if (this.zzaZx != null && this.zzaZx.length > 0) {
                for (zzsu com_google_android_gms_internal_zzsu : this.zzaZx) {
                    if (com_google_android_gms_internal_zzsu != null) {
                        output.zza(3, com_google_android_gms_internal_zzsu);
                    }
                }
            }
            if (this.zzaZy != null) {
                output.zze(4, this.zzaZy.booleanValue());
            }
            if (this.zzaZz != null) {
                output.zza(5, this.zzaZz);
            }
            super.writeTo(output);
        }

        public zzb zzDD() {
            this.zzaZv = null;
            this.zzaZw = null;
            this.zzaZx = zzc.zzDE();
            this.zzaZy = null;
            this.zzaZz = null;
            this.zzbuu = -1;
            return this;
        }

        public zzb zzu(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        this.zzaZv = Integer.valueOf(com_google_android_gms_internal_zzsm.zzJb());
                        continue;
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        this.zzaZw = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        int zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 26);
                        zzIX = this.zzaZx == null ? 0 : this.zzaZx.length;
                        Object obj = new zzc[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZx, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = new zzc();
                            com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = new zzc();
                        com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                        this.zzaZx = obj;
                        continue;
                    case ItemTouchHelper.END /*32*/:
                        this.zzaZy = Boolean.valueOf(com_google_android_gms_internal_zzsm.zzJc());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                        if (this.zzaZz == null) {
                            this.zzaZz = new zzd();
                        }
                        com_google_android_gms_internal_zzsm.zza(this.zzaZz);
                        continue;
                    default:
                        if (!zzsx.zzb(com_google_android_gms_internal_zzsm, zzIX)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzz() {
            int zzz = super.zzz();
            if (this.zzaZv != null) {
                zzz += zzsn.zzC(1, this.zzaZv.intValue());
            }
            if (this.zzaZw != null) {
                zzz += zzsn.zzo(2, this.zzaZw);
            }
            if (this.zzaZx != null && this.zzaZx.length > 0) {
                int i = zzz;
                for (zzsu com_google_android_gms_internal_zzsu : this.zzaZx) {
                    if (com_google_android_gms_internal_zzsu != null) {
                        i += zzsn.zzc(3, com_google_android_gms_internal_zzsu);
                    }
                }
                zzz = i;
            }
            if (this.zzaZy != null) {
                zzz += zzsn.zzf(4, this.zzaZy.booleanValue());
            }
            return this.zzaZz != null ? zzz + zzsn.zzc(5, this.zzaZz) : zzz;
        }
    }

    public static final class zzc extends zzsu {
        private static volatile zzc[] zzaZA;
        public zzf zzaZB;
        public zzd zzaZC;
        public Boolean zzaZD;
        public String zzaZE;

        public zzc() {
            zzDF();
        }

        public static zzc[] zzDE() {
            if (zzaZA == null) {
                synchronized (zzss.zzbut) {
                    if (zzaZA == null) {
                        zzaZA = new zzc[0];
                    }
                }
            }
            return zzaZA;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzpz_zzc = (zzc) o;
            if (this.zzaZB == null) {
                if (com_google_android_gms_internal_zzpz_zzc.zzaZB != null) {
                    return false;
                }
            } else if (!this.zzaZB.equals(com_google_android_gms_internal_zzpz_zzc.zzaZB)) {
                return false;
            }
            if (this.zzaZC == null) {
                if (com_google_android_gms_internal_zzpz_zzc.zzaZC != null) {
                    return false;
                }
            } else if (!this.zzaZC.equals(com_google_android_gms_internal_zzpz_zzc.zzaZC)) {
                return false;
            }
            if (this.zzaZD == null) {
                if (com_google_android_gms_internal_zzpz_zzc.zzaZD != null) {
                    return false;
                }
            } else if (!this.zzaZD.equals(com_google_android_gms_internal_zzpz_zzc.zzaZD)) {
                return false;
            }
            return this.zzaZE == null ? com_google_android_gms_internal_zzpz_zzc.zzaZE == null : this.zzaZE.equals(com_google_android_gms_internal_zzpz_zzc.zzaZE);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzaZD == null ? 0 : this.zzaZD.hashCode()) + (((this.zzaZC == null ? 0 : this.zzaZC.hashCode()) + (((this.zzaZB == null ? 0 : this.zzaZB.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
            if (this.zzaZE != null) {
                i = this.zzaZE.hashCode();
            }
            return hashCode + i;
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzv(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.zzaZB != null) {
                output.zza(1, this.zzaZB);
            }
            if (this.zzaZC != null) {
                output.zza(2, this.zzaZC);
            }
            if (this.zzaZD != null) {
                output.zze(3, this.zzaZD.booleanValue());
            }
            if (this.zzaZE != null) {
                output.zzn(4, this.zzaZE);
            }
            super.writeTo(output);
        }

        public zzc zzDF() {
            this.zzaZB = null;
            this.zzaZC = null;
            this.zzaZD = null;
            this.zzaZE = null;
            this.zzbuu = -1;
            return this;
        }

        public zzc zzv(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case ConnectionResult.DEVELOPER_ERROR /*10*/:
                        if (this.zzaZB == null) {
                            this.zzaZB = new zzf();
                        }
                        com_google_android_gms_internal_zzsm.zza(this.zzaZB);
                        continue;
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        if (this.zzaZC == null) {
                            this.zzaZC = new zzd();
                        }
                        com_google_android_gms_internal_zzsm.zza(this.zzaZC);
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL24 /*24*/:
                        this.zzaZD = Boolean.valueOf(com_google_android_gms_internal_zzsm.zzJc());
                        continue;
                    case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                        this.zzaZE = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    default:
                        if (!zzsx.zzb(com_google_android_gms_internal_zzsm, zzIX)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzz() {
            int zzz = super.zzz();
            if (this.zzaZB != null) {
                zzz += zzsn.zzc(1, this.zzaZB);
            }
            if (this.zzaZC != null) {
                zzz += zzsn.zzc(2, this.zzaZC);
            }
            if (this.zzaZD != null) {
                zzz += zzsn.zzf(3, this.zzaZD.booleanValue());
            }
            return this.zzaZE != null ? zzz + zzsn.zzo(4, this.zzaZE) : zzz;
        }
    }

    public static final class zzd extends zzsu {
        public Integer zzaZF;
        public Boolean zzaZG;
        public String zzaZH;
        public String zzaZI;
        public String zzaZJ;

        public zzd() {
            zzDG();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zzd)) {
                return false;
            }
            zzd com_google_android_gms_internal_zzpz_zzd = (zzd) o;
            if (this.zzaZF == null) {
                if (com_google_android_gms_internal_zzpz_zzd.zzaZF != null) {
                    return false;
                }
            } else if (!this.zzaZF.equals(com_google_android_gms_internal_zzpz_zzd.zzaZF)) {
                return false;
            }
            if (this.zzaZG == null) {
                if (com_google_android_gms_internal_zzpz_zzd.zzaZG != null) {
                    return false;
                }
            } else if (!this.zzaZG.equals(com_google_android_gms_internal_zzpz_zzd.zzaZG)) {
                return false;
            }
            if (this.zzaZH == null) {
                if (com_google_android_gms_internal_zzpz_zzd.zzaZH != null) {
                    return false;
                }
            } else if (!this.zzaZH.equals(com_google_android_gms_internal_zzpz_zzd.zzaZH)) {
                return false;
            }
            if (this.zzaZI == null) {
                if (com_google_android_gms_internal_zzpz_zzd.zzaZI != null) {
                    return false;
                }
            } else if (!this.zzaZI.equals(com_google_android_gms_internal_zzpz_zzd.zzaZI)) {
                return false;
            }
            return this.zzaZJ == null ? com_google_android_gms_internal_zzpz_zzd.zzaZJ == null : this.zzaZJ.equals(com_google_android_gms_internal_zzpz_zzd.zzaZJ);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzaZI == null ? 0 : this.zzaZI.hashCode()) + (((this.zzaZH == null ? 0 : this.zzaZH.hashCode()) + (((this.zzaZG == null ? 0 : this.zzaZG.hashCode()) + (((this.zzaZF == null ? 0 : this.zzaZF.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
            if (this.zzaZJ != null) {
                i = this.zzaZJ.hashCode();
            }
            return hashCode + i;
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzw(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.zzaZF != null) {
                output.zzA(1, this.zzaZF.intValue());
            }
            if (this.zzaZG != null) {
                output.zze(2, this.zzaZG.booleanValue());
            }
            if (this.zzaZH != null) {
                output.zzn(3, this.zzaZH);
            }
            if (this.zzaZI != null) {
                output.zzn(4, this.zzaZI);
            }
            if (this.zzaZJ != null) {
                output.zzn(5, this.zzaZJ);
            }
            super.writeTo(output);
        }

        public zzd zzDG() {
            this.zzaZF = null;
            this.zzaZG = null;
            this.zzaZH = null;
            this.zzaZI = null;
            this.zzaZJ = null;
            this.zzbuu = -1;
            return this;
        }

        public zzd zzw(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        zzIX = com_google_android_gms_internal_zzsm.zzJb();
                        switch (zzIX) {
                            case VideoPlayer.TRACK_DEFAULT /*0*/:
                            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                            case VideoPlayer.STATE_READY /*4*/:
                                this.zzaZF = Integer.valueOf(zzIX);
                                break;
                            default:
                                continue;
                        }
                    case ItemTouchHelper.START /*16*/:
                        this.zzaZG = Boolean.valueOf(com_google_android_gms_internal_zzsm.zzJc());
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        this.zzaZH = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                        this.zzaZI = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                        this.zzaZJ = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    default:
                        if (!zzsx.zzb(com_google_android_gms_internal_zzsm, zzIX)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzz() {
            int zzz = super.zzz();
            if (this.zzaZF != null) {
                zzz += zzsn.zzC(1, this.zzaZF.intValue());
            }
            if (this.zzaZG != null) {
                zzz += zzsn.zzf(2, this.zzaZG.booleanValue());
            }
            if (this.zzaZH != null) {
                zzz += zzsn.zzo(3, this.zzaZH);
            }
            if (this.zzaZI != null) {
                zzz += zzsn.zzo(4, this.zzaZI);
            }
            return this.zzaZJ != null ? zzz + zzsn.zzo(5, this.zzaZJ) : zzz;
        }
    }

    public static final class zze extends zzsu {
        private static volatile zze[] zzaZK;
        public String zzaZL;
        public zzc zzaZM;
        public Integer zzaZv;

        public zze() {
            zzDI();
        }

        public static zze[] zzDH() {
            if (zzaZK == null) {
                synchronized (zzss.zzbut) {
                    if (zzaZK == null) {
                        zzaZK = new zze[0];
                    }
                }
            }
            return zzaZK;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zze)) {
                return false;
            }
            zze com_google_android_gms_internal_zzpz_zze = (zze) o;
            if (this.zzaZv == null) {
                if (com_google_android_gms_internal_zzpz_zze.zzaZv != null) {
                    return false;
                }
            } else if (!this.zzaZv.equals(com_google_android_gms_internal_zzpz_zze.zzaZv)) {
                return false;
            }
            if (this.zzaZL == null) {
                if (com_google_android_gms_internal_zzpz_zze.zzaZL != null) {
                    return false;
                }
            } else if (!this.zzaZL.equals(com_google_android_gms_internal_zzpz_zze.zzaZL)) {
                return false;
            }
            return this.zzaZM == null ? com_google_android_gms_internal_zzpz_zze.zzaZM == null : this.zzaZM.equals(com_google_android_gms_internal_zzpz_zze.zzaZM);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzaZL == null ? 0 : this.zzaZL.hashCode()) + (((this.zzaZv == null ? 0 : this.zzaZv.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.zzaZM != null) {
                i = this.zzaZM.hashCode();
            }
            return hashCode + i;
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzx(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.zzaZv != null) {
                output.zzA(1, this.zzaZv.intValue());
            }
            if (this.zzaZL != null) {
                output.zzn(2, this.zzaZL);
            }
            if (this.zzaZM != null) {
                output.zza(3, this.zzaZM);
            }
            super.writeTo(output);
        }

        public zze zzDI() {
            this.zzaZv = null;
            this.zzaZL = null;
            this.zzaZM = null;
            this.zzbuu = -1;
            return this;
        }

        public zze zzx(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        this.zzaZv = Integer.valueOf(com_google_android_gms_internal_zzsm.zzJb());
                        continue;
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        this.zzaZL = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                        if (this.zzaZM == null) {
                            this.zzaZM = new zzc();
                        }
                        com_google_android_gms_internal_zzsm.zza(this.zzaZM);
                        continue;
                    default:
                        if (!zzsx.zzb(com_google_android_gms_internal_zzsm, zzIX)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzz() {
            int zzz = super.zzz();
            if (this.zzaZv != null) {
                zzz += zzsn.zzC(1, this.zzaZv.intValue());
            }
            if (this.zzaZL != null) {
                zzz += zzsn.zzo(2, this.zzaZL);
            }
            return this.zzaZM != null ? zzz + zzsn.zzc(3, this.zzaZM) : zzz;
        }
    }

    public static final class zzf extends zzsu {
        public Integer zzaZN;
        public String zzaZO;
        public Boolean zzaZP;
        public String[] zzaZQ;

        public zzf() {
            zzDJ();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zzf)) {
                return false;
            }
            zzf com_google_android_gms_internal_zzpz_zzf = (zzf) o;
            if (this.zzaZN == null) {
                if (com_google_android_gms_internal_zzpz_zzf.zzaZN != null) {
                    return false;
                }
            } else if (!this.zzaZN.equals(com_google_android_gms_internal_zzpz_zzf.zzaZN)) {
                return false;
            }
            if (this.zzaZO == null) {
                if (com_google_android_gms_internal_zzpz_zzf.zzaZO != null) {
                    return false;
                }
            } else if (!this.zzaZO.equals(com_google_android_gms_internal_zzpz_zzf.zzaZO)) {
                return false;
            }
            if (this.zzaZP == null) {
                if (com_google_android_gms_internal_zzpz_zzf.zzaZP != null) {
                    return false;
                }
            } else if (!this.zzaZP.equals(com_google_android_gms_internal_zzpz_zzf.zzaZP)) {
                return false;
            }
            return zzss.equals(this.zzaZQ, com_google_android_gms_internal_zzpz_zzf.zzaZQ);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzaZO == null ? 0 : this.zzaZO.hashCode()) + (((this.zzaZN == null ? 0 : this.zzaZN.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.zzaZP != null) {
                i = this.zzaZP.hashCode();
            }
            return ((hashCode + i) * 31) + zzss.hashCode(this.zzaZQ);
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzy(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.zzaZN != null) {
                output.zzA(1, this.zzaZN.intValue());
            }
            if (this.zzaZO != null) {
                output.zzn(2, this.zzaZO);
            }
            if (this.zzaZP != null) {
                output.zze(3, this.zzaZP.booleanValue());
            }
            if (this.zzaZQ != null && this.zzaZQ.length > 0) {
                for (String str : this.zzaZQ) {
                    if (str != null) {
                        output.zzn(4, str);
                    }
                }
            }
            super.writeTo(output);
        }

        public zzf zzDJ() {
            this.zzaZN = null;
            this.zzaZO = null;
            this.zzaZP = null;
            this.zzaZQ = zzsx.zzbuB;
            this.zzbuu = -1;
            return this;
        }

        public zzf zzy(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        zzIX = com_google_android_gms_internal_zzsm.zzJb();
                        switch (zzIX) {
                            case VideoPlayer.TRACK_DEFAULT /*0*/:
                            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                            case VideoPlayer.STATE_READY /*4*/:
                            case VideoPlayer.STATE_ENDED /*5*/:
                            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                                this.zzaZN = Integer.valueOf(zzIX);
                                break;
                            default:
                                continue;
                        }
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        this.zzaZO = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL24 /*24*/:
                        this.zzaZP = Boolean.valueOf(com_google_android_gms_internal_zzsm.zzJc());
                        continue;
                    case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                        int zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 34);
                        zzIX = this.zzaZQ == null ? 0 : this.zzaZQ.length;
                        Object obj = new String[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZQ, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = com_google_android_gms_internal_zzsm.readString();
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = com_google_android_gms_internal_zzsm.readString();
                        this.zzaZQ = obj;
                        continue;
                    default:
                        if (!zzsx.zzb(com_google_android_gms_internal_zzsm, zzIX)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        protected int zzz() {
            int i = 0;
            int zzz = super.zzz();
            if (this.zzaZN != null) {
                zzz += zzsn.zzC(1, this.zzaZN.intValue());
            }
            if (this.zzaZO != null) {
                zzz += zzsn.zzo(2, this.zzaZO);
            }
            if (this.zzaZP != null) {
                zzz += zzsn.zzf(3, this.zzaZP.booleanValue());
            }
            if (this.zzaZQ == null || this.zzaZQ.length <= 0) {
                return zzz;
            }
            int i2 = 0;
            int i3 = 0;
            while (i < this.zzaZQ.length) {
                String str = this.zzaZQ[i];
                if (str != null) {
                    i3++;
                    i2 += zzsn.zzgO(str);
                }
                i++;
            }
            return (zzz + i2) + (i3 * 1);
        }
    }
}
