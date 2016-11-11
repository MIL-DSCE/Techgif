package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public abstract class zze {
    public static final zze zzakF;
    public static final zze zzakG;
    public static final zze zzakH;
    public static final zze zzakI;
    public static final zze zzakJ;
    public static final zze zzakK;
    public static final zze zzakL;
    public static final zze zzakM;
    public static final zze zzakN;
    public static final zze zzakO;
    public static final zze zzakP;
    public static final zze zzakQ;
    public static final zze zzakR;
    public static final zze zzakS;
    public static final zze zzakT;

    /* renamed from: com.google.android.gms.common.internal.zze.11 */
    static class AnonymousClass11 extends zze {
        final /* synthetic */ char zzakZ;

        AnonymousClass11(char c) {
            this.zzakZ = c;
        }

        public zze zza(zze com_google_android_gms_common_internal_zze) {
            return com_google_android_gms_common_internal_zze.zzd(this.zzakZ) ? com_google_android_gms_common_internal_zze : super.zza(com_google_android_gms_common_internal_zze);
        }

        public boolean zzd(char c) {
            return c == this.zzakZ;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.1 */
    static class C15581 extends zze {
        C15581() {
        }

        public boolean zzd(char c) {
            return Character.isDigit(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.2 */
    static class C15592 extends zze {
        final /* synthetic */ char zzakU;
        final /* synthetic */ char zzakV;

        C15592(char c, char c2) {
            this.zzakU = c;
            this.zzakV = c2;
        }

        public boolean zzd(char c) {
            return c == this.zzakU || c == this.zzakV;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.3 */
    static class C15603 extends zze {
        final /* synthetic */ char[] zzakW;

        C15603(char[] cArr) {
            this.zzakW = cArr;
        }

        public boolean zzd(char c) {
            return Arrays.binarySearch(this.zzakW, c) >= 0;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.4 */
    static class C15614 extends zze {
        final /* synthetic */ char zzakX;
        final /* synthetic */ char zzakY;

        C15614(char c, char c2) {
            this.zzakX = c;
            this.zzakY = c2;
        }

        public boolean zzd(char c) {
            return this.zzakX <= c && c <= this.zzakY;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.5 */
    static class C15625 extends zze {
        C15625() {
        }

        public boolean zzd(char c) {
            return Character.isLetter(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.6 */
    static class C15636 extends zze {
        C15636() {
        }

        public boolean zzd(char c) {
            return Character.isLetterOrDigit(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.7 */
    static class C15647 extends zze {
        C15647() {
        }

        public boolean zzd(char c) {
            return Character.isUpperCase(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.8 */
    static class C15658 extends zze {
        C15658() {
        }

        public boolean zzd(char c) {
            return Character.isLowerCase(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zze.9 */
    static class C15669 extends zze {
        C15669() {
        }

        public zze zza(zze com_google_android_gms_common_internal_zze) {
            zzx.zzz(com_google_android_gms_common_internal_zze);
            return this;
        }

        public boolean zzb(CharSequence charSequence) {
            zzx.zzz(charSequence);
            return true;
        }

        public boolean zzd(char c) {
            return true;
        }
    }

    private static class zza extends zze {
        List<zze> zzala;

        zza(List<zze> list) {
            this.zzala = list;
        }

        public zze zza(zze com_google_android_gms_common_internal_zze) {
            List arrayList = new ArrayList(this.zzala);
            arrayList.add(zzx.zzz(com_google_android_gms_common_internal_zze));
            return new zza(arrayList);
        }

        public boolean zzd(char c) {
            for (zze zzd : this.zzala) {
                if (zzd.zzd(c)) {
                    return true;
                }
            }
            return false;
        }
    }

    static {
        zzakF = zza((CharSequence) "\t\n\u000b\f\r \u0085\u1680\u2028\u2029\u205f\u3000\u00a0\u180e\u202f").zza(zza('\u2000', '\u200a'));
        zzakG = zza((CharSequence) "\t\n\u000b\f\r \u0085\u1680\u2028\u2029\u205f\u3000").zza(zza('\u2000', '\u2006')).zza(zza('\u2008', '\u200a'));
        zzakH = zza('\u0000', '\u007f');
        zze zza = zza('0', '9');
        zze com_google_android_gms_common_internal_zze = zza;
        for (char c : "\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".toCharArray()) {
            com_google_android_gms_common_internal_zze = com_google_android_gms_common_internal_zze.zza(zza(c, (char) (c + 9)));
        }
        zzakI = com_google_android_gms_common_internal_zze;
        zzakJ = zza('\t', '\r').zza(zza('\u001c', ' ')).zza(zzc('\u1680')).zza(zzc('\u180e')).zza(zza('\u2000', '\u2006')).zza(zza('\u2008', '\u200b')).zza(zza('\u2028', '\u2029')).zza(zzc('\u205f')).zza(zzc('\u3000'));
        zzakK = new C15581();
        zzakL = new C15625();
        zzakM = new C15636();
        zzakN = new C15647();
        zzakO = new C15658();
        zzakP = zza('\u0000', '\u001f').zza(zza('\u007f', '\u009f'));
        zzakQ = zza('\u0000', ' ').zza(zza('\u007f', '\u00a0')).zza(zzc('\u00ad')).zza(zza('\u0600', '\u0603')).zza(zza((CharSequence) "\u06dd\u070f\u1680\u17b4\u17b5\u180e")).zza(zza('\u2000', '\u200f')).zza(zza('\u2028', '\u202f')).zza(zza('\u205f', '\u2064')).zza(zza('\u206a', '\u206f')).zza(zzc('\u3000')).zza(zza('\ud800', '\uf8ff')).zza(zza((CharSequence) "\ufeff\ufff9\ufffa\ufffb"));
        zzakR = zza('\u0000', '\u04f9').zza(zzc('\u05be')).zza(zza('\u05d0', '\u05ea')).zza(zzc('\u05f3')).zza(zzc('\u05f4')).zza(zza('\u0600', '\u06ff')).zza(zza('\u0750', '\u077f')).zza(zza('\u0e00', '\u0e7f')).zza(zza('\u1e00', '\u20af')).zza(zza('\u2100', '\u213a')).zza(zza('\ufb50', '\ufdff')).zza(zza('\ufe70', '\ufeff')).zza(zza('\uff61', '\uffdc'));
        zzakS = new C15669();
        zzakT = new zze() {
            public zze zza(zze com_google_android_gms_common_internal_zze) {
                return (zze) zzx.zzz(com_google_android_gms_common_internal_zze);
            }

            public boolean zzb(CharSequence charSequence) {
                return charSequence.length() == 0;
            }

            public boolean zzd(char c) {
                return false;
            }
        };
    }

    public static zze zza(char c, char c2) {
        zzx.zzac(c2 >= c);
        return new C15614(c, c2);
    }

    public static zze zza(CharSequence charSequence) {
        switch (charSequence.length()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return zzakT;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return zzc(charSequence.charAt(0));
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return new C15592(charSequence.charAt(0), charSequence.charAt(1));
            default:
                char[] toCharArray = charSequence.toString().toCharArray();
                Arrays.sort(toCharArray);
                return new C15603(toCharArray);
        }
    }

    public static zze zzc(char c) {
        return new AnonymousClass11(c);
    }

    public zze zza(zze com_google_android_gms_common_internal_zze) {
        return new zza(Arrays.asList(new zze[]{this, (zze) zzx.zzz(com_google_android_gms_common_internal_zze)}));
    }

    public boolean zzb(CharSequence charSequence) {
        for (int length = charSequence.length() - 1; length >= 0; length--) {
            if (!zzd(charSequence.charAt(length))) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean zzd(char c);
}
