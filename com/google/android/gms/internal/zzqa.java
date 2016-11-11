package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public interface zzqa {

    public static final class zza extends zzsu {
        private static volatile zza[] zzaZR;
        public String name;
        public Boolean zzaZS;

        public zza() {
            zzDL();
        }

        public static zza[] zzDK() {
            if (zzaZR == null) {
                synchronized (zzss.zzbut) {
                    if (zzaZR == null) {
                        zzaZR = new zza[0];
                    }
                }
            }
            return zzaZR;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzqa_zza = (zza) o;
            if (this.name == null) {
                if (com_google_android_gms_internal_zzqa_zza.name != null) {
                    return false;
                }
            } else if (!this.name.equals(com_google_android_gms_internal_zzqa_zza.name)) {
                return false;
            }
            return this.zzaZS == null ? com_google_android_gms_internal_zzqa_zza.zzaZS == null : this.zzaZS.equals(com_google_android_gms_internal_zzqa_zza.zzaZS);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31;
            if (this.zzaZS != null) {
                i = this.zzaZS.hashCode();
            }
            return hashCode + i;
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzz(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.name != null) {
                output.zzn(1, this.name);
            }
            if (this.zzaZS != null) {
                output.zze(2, this.zzaZS.booleanValue());
            }
            super.writeTo(output);
        }

        public zza zzDL() {
            this.name = null;
            this.zzaZS = null;
            this.zzbuu = -1;
            return this;
        }

        protected int zzz() {
            int zzz = super.zzz();
            if (this.name != null) {
                zzz += zzsn.zzo(1, this.name);
            }
            return this.zzaZS != null ? zzz + zzsn.zzf(2, this.zzaZS.booleanValue()) : zzz;
        }

        public zza zzz(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case ConnectionResult.DEVELOPER_ERROR /*10*/:
                        this.name = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case ItemTouchHelper.START /*16*/:
                        this.zzaZS = Boolean.valueOf(com_google_android_gms_internal_zzsm.zzJc());
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
    }

    public static final class zzb extends zzsu {
        public String zzaVt;
        public Long zzaZT;
        public Integer zzaZU;
        public zzc[] zzaZV;
        public zza[] zzaZW;
        public com.google.android.gms.internal.zzpz.zza[] zzaZX;

        public zzb() {
            zzDM();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zzb)) {
                return false;
            }
            zzb com_google_android_gms_internal_zzqa_zzb = (zzb) o;
            if (this.zzaZT == null) {
                if (com_google_android_gms_internal_zzqa_zzb.zzaZT != null) {
                    return false;
                }
            } else if (!this.zzaZT.equals(com_google_android_gms_internal_zzqa_zzb.zzaZT)) {
                return false;
            }
            if (this.zzaVt == null) {
                if (com_google_android_gms_internal_zzqa_zzb.zzaVt != null) {
                    return false;
                }
            } else if (!this.zzaVt.equals(com_google_android_gms_internal_zzqa_zzb.zzaVt)) {
                return false;
            }
            if (this.zzaZU == null) {
                if (com_google_android_gms_internal_zzqa_zzb.zzaZU != null) {
                    return false;
                }
            } else if (!this.zzaZU.equals(com_google_android_gms_internal_zzqa_zzb.zzaZU)) {
                return false;
            }
            if (!zzss.equals(this.zzaZV, com_google_android_gms_internal_zzqa_zzb.zzaZV)) {
                return false;
            }
            if (zzss.equals(this.zzaZW, com_google_android_gms_internal_zzqa_zzb.zzaZW)) {
                return zzss.equals(this.zzaZX, com_google_android_gms_internal_zzqa_zzb.zzaZX);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.zzaVt == null ? 0 : this.zzaVt.hashCode()) + (((this.zzaZT == null ? 0 : this.zzaZT.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
            if (this.zzaZU != null) {
                i = this.zzaZU.hashCode();
            }
            return ((((((hashCode + i) * 31) + zzss.hashCode(this.zzaZV)) * 31) + zzss.hashCode(this.zzaZW)) * 31) + zzss.hashCode(this.zzaZX);
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzA(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            int i = 0;
            if (this.zzaZT != null) {
                output.zzb(1, this.zzaZT.longValue());
            }
            if (this.zzaVt != null) {
                output.zzn(2, this.zzaVt);
            }
            if (this.zzaZU != null) {
                output.zzA(3, this.zzaZU.intValue());
            }
            if (this.zzaZV != null && this.zzaZV.length > 0) {
                for (zzsu com_google_android_gms_internal_zzsu : this.zzaZV) {
                    if (com_google_android_gms_internal_zzsu != null) {
                        output.zza(4, com_google_android_gms_internal_zzsu);
                    }
                }
            }
            if (this.zzaZW != null && this.zzaZW.length > 0) {
                for (zzsu com_google_android_gms_internal_zzsu2 : this.zzaZW) {
                    if (com_google_android_gms_internal_zzsu2 != null) {
                        output.zza(5, com_google_android_gms_internal_zzsu2);
                    }
                }
            }
            if (this.zzaZX != null && this.zzaZX.length > 0) {
                while (i < this.zzaZX.length) {
                    zzsu com_google_android_gms_internal_zzsu3 = this.zzaZX[i];
                    if (com_google_android_gms_internal_zzsu3 != null) {
                        output.zza(6, com_google_android_gms_internal_zzsu3);
                    }
                    i++;
                }
            }
            super.writeTo(output);
        }

        public zzb zzA(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                int zzc;
                Object obj;
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                        this.zzaZT = Long.valueOf(com_google_android_gms_internal_zzsm.zzJa());
                        continue;
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        this.zzaVt = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL24 /*24*/:
                        this.zzaZU = Integer.valueOf(com_google_android_gms_internal_zzsm.zzJb());
                        continue;
                    case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                        zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 34);
                        zzIX = this.zzaZV == null ? 0 : this.zzaZV.length;
                        obj = new zzc[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZV, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = new zzc();
                            com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = new zzc();
                        com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                        this.zzaZV = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                        zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 42);
                        zzIX = this.zzaZW == null ? 0 : this.zzaZW.length;
                        obj = new zza[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZW, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = new zza();
                            com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = new zza();
                        com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                        this.zzaZW = obj;
                        continue;
                    case NalUnitTypes.NAL_TYPE_UNSPEC50 /*50*/:
                        zzc = zzsx.zzc(com_google_android_gms_internal_zzsm, 50);
                        zzIX = this.zzaZX == null ? 0 : this.zzaZX.length;
                        obj = new com.google.android.gms.internal.zzpz.zza[(zzc + zzIX)];
                        if (zzIX != 0) {
                            System.arraycopy(this.zzaZX, 0, obj, 0, zzIX);
                        }
                        while (zzIX < obj.length - 1) {
                            obj[zzIX] = new com.google.android.gms.internal.zzpz.zza();
                            com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                            com_google_android_gms_internal_zzsm.zzIX();
                            zzIX++;
                        }
                        obj[zzIX] = new com.google.android.gms.internal.zzpz.zza();
                        com_google_android_gms_internal_zzsm.zza(obj[zzIX]);
                        this.zzaZX = obj;
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

        public zzb zzDM() {
            this.zzaZT = null;
            this.zzaVt = null;
            this.zzaZU = null;
            this.zzaZV = zzc.zzDN();
            this.zzaZW = zza.zzDK();
            this.zzaZX = com.google.android.gms.internal.zzpz.zza.zzDA();
            this.zzbuu = -1;
            return this;
        }

        protected int zzz() {
            int i;
            int i2 = 0;
            int zzz = super.zzz();
            if (this.zzaZT != null) {
                zzz += zzsn.zzd(1, this.zzaZT.longValue());
            }
            if (this.zzaVt != null) {
                zzz += zzsn.zzo(2, this.zzaVt);
            }
            if (this.zzaZU != null) {
                zzz += zzsn.zzC(3, this.zzaZU.intValue());
            }
            if (this.zzaZV != null && this.zzaZV.length > 0) {
                i = zzz;
                for (zzsu com_google_android_gms_internal_zzsu : this.zzaZV) {
                    if (com_google_android_gms_internal_zzsu != null) {
                        i += zzsn.zzc(4, com_google_android_gms_internal_zzsu);
                    }
                }
                zzz = i;
            }
            if (this.zzaZW != null && this.zzaZW.length > 0) {
                i = zzz;
                for (zzsu com_google_android_gms_internal_zzsu2 : this.zzaZW) {
                    if (com_google_android_gms_internal_zzsu2 != null) {
                        i += zzsn.zzc(5, com_google_android_gms_internal_zzsu2);
                    }
                }
                zzz = i;
            }
            if (this.zzaZX != null && this.zzaZX.length > 0) {
                while (i2 < this.zzaZX.length) {
                    zzsu com_google_android_gms_internal_zzsu3 = this.zzaZX[i2];
                    if (com_google_android_gms_internal_zzsu3 != null) {
                        zzz += zzsn.zzc(6, com_google_android_gms_internal_zzsu3);
                    }
                    i2++;
                }
            }
            return zzz;
        }
    }

    public static final class zzc extends zzsu {
        private static volatile zzc[] zzaZY;
        public String key;
        public String value;

        public zzc() {
            zzDO();
        }

        public static zzc[] zzDN() {
            if (zzaZY == null) {
                synchronized (zzss.zzbut) {
                    if (zzaZY == null) {
                        zzaZY = new zzc[0];
                    }
                }
            }
            return zzaZY;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof zzc)) {
                return false;
            }
            zzc com_google_android_gms_internal_zzqa_zzc = (zzc) o;
            if (this.key == null) {
                if (com_google_android_gms_internal_zzqa_zzc.key != null) {
                    return false;
                }
            } else if (!this.key.equals(com_google_android_gms_internal_zzqa_zzc.key)) {
                return false;
            }
            return this.value == null ? com_google_android_gms_internal_zzqa_zzc.value == null : this.value.equals(com_google_android_gms_internal_zzqa_zzc.value);
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.key == null ? 0 : this.key.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31;
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode + i;
        }

        public /* synthetic */ zzsu mergeFrom(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            return zzB(com_google_android_gms_internal_zzsm);
        }

        public void writeTo(zzsn output) throws IOException {
            if (this.key != null) {
                output.zzn(1, this.key);
            }
            if (this.value != null) {
                output.zzn(2, this.value);
            }
            super.writeTo(output);
        }

        public zzc zzB(zzsm com_google_android_gms_internal_zzsm) throws IOException {
            while (true) {
                int zzIX = com_google_android_gms_internal_zzsm.zzIX();
                switch (zzIX) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        break;
                    case ConnectionResult.DEVELOPER_ERROR /*10*/:
                        this.key = com_google_android_gms_internal_zzsm.readString();
                        continue;
                    case ConnectionResult.SERVICE_UPDATING /*18*/:
                        this.value = com_google_android_gms_internal_zzsm.readString();
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

        public zzc zzDO() {
            this.key = null;
            this.value = null;
            this.zzbuu = -1;
            return this;
        }

        protected int zzz() {
            int zzz = super.zzz();
            if (this.key != null) {
                zzz += zzsn.zzo(1, this.key);
            }
            return this.value != null ? zzz + zzsn.zzo(2, this.value) : zzz;
        }
    }
}
