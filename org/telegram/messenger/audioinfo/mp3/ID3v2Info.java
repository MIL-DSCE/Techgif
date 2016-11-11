package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.ui.Components.VideoPlayer;

public class ID3v2Info extends AudioInfo {
    static final Logger LOGGER;
    private byte coverPictureType;
    private final Level debugLevel;

    static class AttachedPicture {
        static final byte TYPE_COVER_FRONT = (byte) 3;
        static final byte TYPE_OTHER = (byte) 0;
        final String description;
        final byte[] imageData;
        final String imageType;
        final byte type;

        public AttachedPicture(byte type, String description, String imageType, byte[] imageData) {
            this.type = type;
            this.description = description;
            this.imageType = imageType;
            this.imageData = imageData;
        }
    }

    static class CommentOrUnsynchronizedLyrics {
        final String description;
        final String language;
        final String text;

        public CommentOrUnsynchronizedLyrics(String language, String description, String text) {
            this.language = language;
            this.description = description;
            this.text = text;
        }
    }

    public ID3v2Info(java.io.InputStream r11, java.util.logging.Level r12) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x0085 in {18, 22, 38, 40, 42, 45, 47, 48, 50, 51, 52, 53, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r10 = this;
        r6 = 2;
        r9 = 1;
        r8 = 0;
        r10.<init>();
        r10.debugLevel = r12;
        r5 = isID3v2StartPosition(r11);
        if (r5 == 0) goto L_0x0067;
    L_0x000e:
        r4 = new org.telegram.messenger.audioinfo.mp3.ID3v2TagHeader;
        r4.<init>(r11);
        r5 = "ID3";
        r10.brand = r5;
        r5 = "2.%d.%d";
        r6 = new java.lang.Object[r6];
        r7 = r4.getVersion();
        r7 = java.lang.Integer.valueOf(r7);
        r6[r8] = r7;
        r7 = r4.getRevision();
        r7 = java.lang.Integer.valueOf(r7);
        r6[r9] = r7;
        r5 = java.lang.String.format(r5, r6);
        r10.version = r5;
        r3 = r4.tagBody(r11);
    L_0x0039:
        r6 = r3.getRemainingLength();	 Catch:{ ID3v2Exception -> 0x0085 }
        r8 = 10;	 Catch:{ ID3v2Exception -> 0x0085 }
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ ID3v2Exception -> 0x0085 }
        if (r5 <= 0) goto L_0x004e;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x0043:
        r2 = new org.telegram.messenger.audioinfo.mp3.ID3v2FrameHeader;	 Catch:{ ID3v2Exception -> 0x0085 }
        r2.<init>(r3);	 Catch:{ ID3v2Exception -> 0x0085 }
        r5 = r2.isPadding();	 Catch:{ ID3v2Exception -> 0x0085 }
        if (r5 == 0) goto L_0x0068;
    L_0x004e:
        r5 = r3.getData();
        r6 = r3.getRemainingLength();
        r5.skipFully(r6);
        r5 = r4.getFooterSize();
        if (r5 <= 0) goto L_0x0067;
    L_0x005f:
        r5 = r4.getFooterSize();
        r6 = (long) r5;
        r11.skip(r6);
    L_0x0067:
        return;
    L_0x0068:
        r5 = r2.getBodySize();	 Catch:{ ID3v2Exception -> 0x0085 }
        r6 = (long) r5;	 Catch:{ ID3v2Exception -> 0x0085 }
        r8 = r3.getRemainingLength();	 Catch:{ ID3v2Exception -> 0x0085 }
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ ID3v2Exception -> 0x0085 }
        if (r5 <= 0) goto L_0x00ab;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x0075:
        r5 = LOGGER;	 Catch:{ ID3v2Exception -> 0x0085 }
        r5 = r5.isLoggable(r12);	 Catch:{ ID3v2Exception -> 0x0085 }
        if (r5 == 0) goto L_0x004e;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x007d:
        r5 = LOGGER;	 Catch:{ ID3v2Exception -> 0x0085 }
        r6 = "ID3 frame claims to extend frames area";	 Catch:{ ID3v2Exception -> 0x0085 }
        r5.log(r12, r6);	 Catch:{ ID3v2Exception -> 0x0085 }
        goto L_0x004e;
    L_0x0085:
        r0 = move-exception;
        r5 = LOGGER;
        r5 = r5.isLoggable(r12);
        if (r5 == 0) goto L_0x004e;
    L_0x008e:
        r5 = LOGGER;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "ID3 exception occured: ";
        r6 = r6.append(r7);
        r7 = r0.getMessage();
        r6 = r6.append(r7);
        r6 = r6.toString();
        r5.log(r12, r6);
        goto L_0x004e;
    L_0x00ab:
        r5 = r2.isValid();	 Catch:{ ID3v2Exception -> 0x0085 }
        if (r5 == 0) goto L_0x010a;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x00b1:
        r5 = r2.isEncryption();	 Catch:{ ID3v2Exception -> 0x0085 }
        if (r5 != 0) goto L_0x010a;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x00b7:
        r1 = r3.frameBody(r2);	 Catch:{ ID3v2Exception -> 0x0085 }
        r10.parseFrame(r1);	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r5 = r1.getData();
        r6 = r1.getRemainingLength();
        r5.skipFully(r6);
        goto L_0x0039;
    L_0x00cb:
        r0 = move-exception;
        r5 = LOGGER;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r5 = r5.isLoggable(r12);	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        if (r5 == 0) goto L_0x00f0;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
    L_0x00d4:
        r5 = LOGGER;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r6 = "ID3 exception occured in frame %s: %s";	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r7 = 2;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r7 = new java.lang.Object[r7];	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r8 = 0;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r9 = r2.getFrameId();	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r7[r8] = r9;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r8 = 1;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r9 = r0.getMessage();	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r7[r8] = r9;	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r6 = java.lang.String.format(r6, r7);	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
        r5.log(r12, r6);	 Catch:{ ID3v2Exception -> 0x00cb, all -> 0x00fd }
    L_0x00f0:
        r5 = r1.getData();
        r6 = r1.getRemainingLength();
        r5.skipFully(r6);
        goto L_0x0039;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x00fd:
        r5 = move-exception;	 Catch:{ ID3v2Exception -> 0x0085 }
        r6 = r1.getData();	 Catch:{ ID3v2Exception -> 0x0085 }
        r8 = r1.getRemainingLength();	 Catch:{ ID3v2Exception -> 0x0085 }
        r6.skipFully(r8);	 Catch:{ ID3v2Exception -> 0x0085 }
        throw r5;	 Catch:{ ID3v2Exception -> 0x0085 }
    L_0x010a:
        r5 = r3.getData();	 Catch:{ ID3v2Exception -> 0x0085 }
        r6 = r2.getBodySize();	 Catch:{ ID3v2Exception -> 0x0085 }
        r6 = (long) r6;	 Catch:{ ID3v2Exception -> 0x0085 }
        r5.skipFully(r6);	 Catch:{ ID3v2Exception -> 0x0085 }
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2Info.<init>(java.io.InputStream, java.util.logging.Level):void");
    }

    static {
        LOGGER = Logger.getLogger(ID3v2Info.class.getName());
    }

    public static boolean isID3v2StartPosition(InputStream input) throws IOException {
        input.mark(3);
        try {
            boolean z = input.read() == 73 && input.read() == 68 && input.read() == 51;
            input.reset();
            return z;
        } catch (Throwable th) {
            input.reset();
        }
    }

    public ID3v2Info(InputStream input) throws IOException, ID3v2Exception {
        this(input, Level.FINEST);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void parseFrame(org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody r25) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /*
        r24 = this;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0034;
    L_0x000e:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Parsing frame: ";
        r22 = r22.append(r23);
        r23 = r25.getFrameHeader();
        r23 = r23.getFrameId();
        r22 = r22.append(r23);
        r22 = r22.toString();
        r20.log(r21, r22);
    L_0x0034:
        r20 = r25.getFrameHeader();
        r21 = r20.getFrameId();
        r20 = -1;
        r22 = r21.hashCode();
        switch(r22) {
            case 66913: goto L_0x005f;
            case 79210: goto L_0x0049;
            case 82815: goto L_0x0075;
            case 82878: goto L_0x00a1;
            case 82880: goto L_0x00b7;
            case 82881: goto L_0x008b;
            case 82883: goto L_0x00ce;
            case 83149: goto L_0x00f2;
            case 83253: goto L_0x010a;
            case 83254: goto L_0x0122;
            case 83269: goto L_0x013a;
            case 83341: goto L_0x0152;
            case 83377: goto L_0x016a;
            case 83378: goto L_0x0182;
            case 83552: goto L_0x019a;
            case 84125: goto L_0x01b2;
            case 2015625: goto L_0x0054;
            case 2074380: goto L_0x006a;
            case 2567331: goto L_0x0080;
            case 2569298: goto L_0x0096;
            case 2569357: goto L_0x00ac;
            case 2569358: goto L_0x00c2;
            case 2569360: goto L_0x00da;
            case 2570401: goto L_0x00e6;
            case 2575250: goto L_0x0176;
            case 2575251: goto L_0x018e;
            case 2577697: goto L_0x00fe;
            case 2581512: goto L_0x0116;
            case 2581513: goto L_0x012e;
            case 2581856: goto L_0x0146;
            case 2583398: goto L_0x015e;
            case 2590194: goto L_0x01a6;
            case 2614438: goto L_0x01be;
            default: goto L_0x0045;
        };
    L_0x0045:
        switch(r20) {
            case 0: goto L_0x01ca;
            case 1: goto L_0x01ca;
            case 2: goto L_0x030c;
            case 3: goto L_0x030c;
            case 4: goto L_0x0336;
            case 5: goto L_0x0336;
            case 6: goto L_0x0342;
            case 7: goto L_0x0342;
            case 8: goto L_0x0354;
            case 9: goto L_0x0354;
            case 10: goto L_0x0360;
            case 11: goto L_0x0360;
            case 12: goto L_0x03d3;
            case 13: goto L_0x03d3;
            case 14: goto L_0x03df;
            case 15: goto L_0x043c;
            case 16: goto L_0x043c;
            case 17: goto L_0x0483;
            case 18: goto L_0x0483;
            case 19: goto L_0x048f;
            case 20: goto L_0x048f;
            case 21: goto L_0x049b;
            case 22: goto L_0x049b;
            case 23: goto L_0x058b;
            case 24: goto L_0x058b;
            case 25: goto L_0x067b;
            case 26: goto L_0x067b;
            case 27: goto L_0x0687;
            case 28: goto L_0x0687;
            case 29: goto L_0x0693;
            case 30: goto L_0x0693;
            case 31: goto L_0x06e0;
            case 32: goto L_0x06e0;
            default: goto L_0x0048;
        };
    L_0x0048:
        return;
    L_0x0049:
        r22 = "PIC";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0051:
        r20 = 0;
        goto L_0x0045;
    L_0x0054:
        r22 = "APIC";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x005c:
        r20 = 1;
        goto L_0x0045;
    L_0x005f:
        r22 = "COM";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0067:
        r20 = 2;
        goto L_0x0045;
    L_0x006a:
        r22 = "COMM";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0072:
        r20 = 3;
        goto L_0x0045;
    L_0x0075:
        r22 = "TAL";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x007d:
        r20 = 4;
        goto L_0x0045;
    L_0x0080:
        r22 = "TALB";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0088:
        r20 = 5;
        goto L_0x0045;
    L_0x008b:
        r22 = "TCP";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0093:
        r20 = 6;
        goto L_0x0045;
    L_0x0096:
        r22 = "TCMP";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x009e:
        r20 = 7;
        goto L_0x0045;
    L_0x00a1:
        r22 = "TCM";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00a9:
        r20 = 8;
        goto L_0x0045;
    L_0x00ac:
        r22 = "TCOM";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00b4:
        r20 = 9;
        goto L_0x0045;
    L_0x00b7:
        r22 = "TCO";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00bf:
        r20 = 10;
        goto L_0x0045;
    L_0x00c2:
        r22 = "TCON";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00ca:
        r20 = 11;
        goto L_0x0045;
    L_0x00ce:
        r22 = "TCR";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00d6:
        r20 = 12;
        goto L_0x0045;
    L_0x00da:
        r22 = "TCOP";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00e2:
        r20 = 13;
        goto L_0x0045;
    L_0x00e6:
        r22 = "TDRC";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00ee:
        r20 = 14;
        goto L_0x0045;
    L_0x00f2:
        r22 = "TLE";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x00fa:
        r20 = 15;
        goto L_0x0045;
    L_0x00fe:
        r22 = "TLEN";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0106:
        r20 = 16;
        goto L_0x0045;
    L_0x010a:
        r22 = "TP1";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0112:
        r20 = 17;
        goto L_0x0045;
    L_0x0116:
        r22 = "TPE1";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x011e:
        r20 = 18;
        goto L_0x0045;
    L_0x0122:
        r22 = "TP2";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x012a:
        r20 = 19;
        goto L_0x0045;
    L_0x012e:
        r22 = "TPE2";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0136:
        r20 = 20;
        goto L_0x0045;
    L_0x013a:
        r22 = "TPA";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0142:
        r20 = 21;
        goto L_0x0045;
    L_0x0146:
        r22 = "TPOS";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x014e:
        r20 = 22;
        goto L_0x0045;
    L_0x0152:
        r22 = "TRK";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x015a:
        r20 = 23;
        goto L_0x0045;
    L_0x015e:
        r22 = "TRCK";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0166:
        r20 = 24;
        goto L_0x0045;
    L_0x016a:
        r22 = "TT1";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0172:
        r20 = 25;
        goto L_0x0045;
    L_0x0176:
        r22 = "TIT1";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x017e:
        r20 = 26;
        goto L_0x0045;
    L_0x0182:
        r22 = "TT2";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x018a:
        r20 = 27;
        goto L_0x0045;
    L_0x018e:
        r22 = "TIT2";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x0196:
        r20 = 28;
        goto L_0x0045;
    L_0x019a:
        r22 = "TYE";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x01a2:
        r20 = 29;
        goto L_0x0045;
    L_0x01a6:
        r22 = "TYER";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x01ae:
        r20 = 30;
        goto L_0x0045;
    L_0x01b2:
        r22 = "ULT";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x01ba:
        r20 = 31;
        goto L_0x0045;
    L_0x01be:
        r22 = "USLT";
        r21 = r21.equals(r22);
        if (r21 == 0) goto L_0x0045;
    L_0x01c6:
        r20 = 32;
        goto L_0x0045;
    L_0x01ca:
        r0 = r24;
        r0 = r0.cover;
        r20 = r0;
        if (r20 == 0) goto L_0x01e0;
    L_0x01d2:
        r0 = r24;
        r0 = r0.coverPictureType;
        r20 = r0;
        r21 = 3;
        r0 = r20;
        r1 = r21;
        if (r0 == r1) goto L_0x0048;
    L_0x01e0:
        r10 = r24.parseAttachedPictureFrame(r25);
        r0 = r24;
        r0 = r0.cover;
        r20 = r0;
        if (r20 == 0) goto L_0x01fe;
    L_0x01ec:
        r0 = r10.type;
        r20 = r0;
        r21 = 3;
        r0 = r20;
        r1 = r21;
        if (r0 == r1) goto L_0x01fe;
    L_0x01f8:
        r0 = r10.type;
        r20 = r0;
        if (r20 != 0) goto L_0x0048;
    L_0x01fe:
        r4 = r10.imageData;	 Catch:{ Throwable -> 0x0307 }
        r9 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0307 }
        r9.<init>();	 Catch:{ Throwable -> 0x0307 }
        r20 = 1;
        r0 = r20;
        r9.inJustDecodeBounds = r0;	 Catch:{ Throwable -> 0x0307 }
        r20 = 1;
        r0 = r20;
        r9.inSampleSize = r0;	 Catch:{ Throwable -> 0x0307 }
        r20 = 0;
        r0 = r4.length;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r0 = r20;
        r1 = r21;
        android.graphics.BitmapFactory.decodeByteArray(r4, r0, r1, r9);	 Catch:{ Throwable -> 0x0307 }
        r0 = r9.outWidth;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r21 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        r0 = r20;
        r1 = r21;
        if (r0 > r1) goto L_0x0235;
    L_0x0229:
        r0 = r9.outHeight;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r21 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        r0 = r20;
        r1 = r21;
        if (r0 <= r1) goto L_0x0254;
    L_0x0235:
        r0 = r9.outWidth;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r0 = r9.outHeight;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r13 = java.lang.Math.max(r20, r21);	 Catch:{ Throwable -> 0x0307 }
    L_0x0241:
        r20 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        r0 = r20;
        if (r13 <= r0) goto L_0x0254;
    L_0x0247:
        r0 = r9.inSampleSize;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r20 = r20 * 2;
        r0 = r20;
        r9.inSampleSize = r0;	 Catch:{ Throwable -> 0x0307 }
        r13 = r13 / 2;
        goto L_0x0241;
    L_0x0254:
        r20 = 0;
        r0 = r20;
        r9.inJustDecodeBounds = r0;	 Catch:{ Throwable -> 0x0307 }
        r20 = 0;
        r0 = r4.length;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r0 = r20;
        r1 = r21;
        r20 = android.graphics.BitmapFactory.decodeByteArray(r4, r0, r1, r9);	 Catch:{ Throwable -> 0x0307 }
        r0 = r20;
        r1 = r24;
        r1.cover = r0;	 Catch:{ Throwable -> 0x0307 }
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        if (r20 == 0) goto L_0x02ee;
    L_0x0275:
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r20 = r20.getWidth();	 Catch:{ Throwable -> 0x0307 }
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r21 = r21.getHeight();	 Catch:{ Throwable -> 0x0307 }
        r20 = java.lang.Math.max(r20, r21);	 Catch:{ Throwable -> 0x0307 }
        r0 = r20;
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r21 = 1123024896; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r12 = r20 / r21;
        r20 = 0;
        r20 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1));
        if (r20 <= 0) goto L_0x02fa;
    L_0x029c:
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r21 = r21.getWidth();	 Catch:{ Throwable -> 0x0307 }
        r0 = r21;
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r21 = r21 / r12;
        r0 = r21;
        r0 = (int) r0;	 Catch:{ Throwable -> 0x0307 }
        r21 = r0;
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r22 = r0;
        r22 = r22.getHeight();	 Catch:{ Throwable -> 0x0307 }
        r0 = r22;
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0307 }
        r22 = r0;
        r22 = r22 / r12;
        r0 = r22;
        r0 = (int) r0;	 Catch:{ Throwable -> 0x0307 }
        r22 = r0;
        r23 = 1;
        r20 = android.graphics.Bitmap.createScaledBitmap(r20, r21, r22, r23);	 Catch:{ Throwable -> 0x0307 }
        r0 = r20;
        r1 = r24;
        r1.smallCover = r0;	 Catch:{ Throwable -> 0x0307 }
    L_0x02da:
        r0 = r24;
        r0 = r0.smallCover;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        if (r20 != 0) goto L_0x02ee;
    L_0x02e2:
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r0 = r20;
        r1 = r24;
        r1.smallCover = r0;	 Catch:{ Throwable -> 0x0307 }
    L_0x02ee:
        r0 = r10.type;
        r20 = r0;
        r0 = r20;
        r1 = r24;
        r1.coverPictureType = r0;
        goto L_0x0048;
    L_0x02fa:
        r0 = r24;
        r0 = r0.cover;	 Catch:{ Throwable -> 0x0307 }
        r20 = r0;
        r0 = r20;
        r1 = r24;
        r1.smallCover = r0;	 Catch:{ Throwable -> 0x0307 }
        goto L_0x02da;
    L_0x0307:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x02ee;
    L_0x030c:
        r5 = r24.parseCommentOrUnsynchronizedLyricsFrame(r25);
        r0 = r24;
        r0 = r0.comment;
        r20 = r0;
        if (r20 == 0) goto L_0x032a;
    L_0x0318:
        r0 = r5.description;
        r20 = r0;
        if (r20 == 0) goto L_0x032a;
    L_0x031e:
        r20 = "";
        r0 = r5.description;
        r21 = r0;
        r20 = r20.equals(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x032a:
        r0 = r5.text;
        r20 = r0;
        r0 = r20;
        r1 = r24;
        r1.comment = r0;
        goto L_0x0048;
    L_0x0336:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.album = r0;
        goto L_0x0048;
    L_0x0342:
        r20 = "1";
        r21 = r24.parseTextFrame(r25);
        r20 = r20.equals(r21);
        r0 = r20;
        r1 = r24;
        r1.compilation = r0;
        goto L_0x0048;
    L_0x0354:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.composer = r0;
        goto L_0x0048;
    L_0x0360:
        r14 = r24.parseTextFrame(r25);
        r20 = r14.length();
        if (r20 <= 0) goto L_0x0048;
    L_0x036a:
        r0 = r24;
        r0.genre = r14;
        r7 = 0;
        r20 = 0;
        r0 = r20;
        r20 = r14.charAt(r0);	 Catch:{ NumberFormatException -> 0x03c7 }
        r21 = 40;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x03ca;
    L_0x037f:
        r20 = 41;
        r0 = r20;
        r11 = r14.indexOf(r0);	 Catch:{ NumberFormatException -> 0x03c7 }
        r20 = 1;
        r0 = r20;
        if (r11 <= r0) goto L_0x03b9;
    L_0x038d:
        r20 = 1;
        r0 = r20;
        r20 = r14.substring(r0, r11);	 Catch:{ NumberFormatException -> 0x03c7 }
        r20 = java.lang.Integer.parseInt(r20);	 Catch:{ NumberFormatException -> 0x03c7 }
        r7 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r20);	 Catch:{ NumberFormatException -> 0x03c7 }
        if (r7 != 0) goto L_0x03b9;
    L_0x039f:
        r20 = r14.length();	 Catch:{ NumberFormatException -> 0x03c7 }
        r21 = r11 + 1;
        r0 = r20;
        r1 = r21;
        if (r0 <= r1) goto L_0x03b9;
    L_0x03ab:
        r20 = r11 + 1;
        r0 = r20;
        r20 = r14.substring(r0);	 Catch:{ NumberFormatException -> 0x03c7 }
        r0 = r20;
        r1 = r24;
        r1.genre = r0;	 Catch:{ NumberFormatException -> 0x03c7 }
    L_0x03b9:
        if (r7 == 0) goto L_0x0048;
    L_0x03bb:
        r20 = r7.getDescription();	 Catch:{ NumberFormatException -> 0x03c7 }
        r0 = r20;
        r1 = r24;
        r1.genre = r0;	 Catch:{ NumberFormatException -> 0x03c7 }
        goto L_0x0048;
    L_0x03c7:
        r20 = move-exception;
        goto L_0x0048;
    L_0x03ca:
        r20 = java.lang.Integer.parseInt(r14);	 Catch:{ NumberFormatException -> 0x03c7 }
        r7 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r20);	 Catch:{ NumberFormatException -> 0x03c7 }
        goto L_0x03b9;
    L_0x03d3:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.copyright = r0;
        goto L_0x0048;
    L_0x03df:
        r15 = r24.parseTextFrame(r25);
        r20 = r15.length();
        r21 = 4;
        r0 = r20;
        r1 = r21;
        if (r0 < r1) goto L_0x0048;
    L_0x03ef:
        r20 = 0;
        r21 = 4;
        r0 = r20;
        r1 = r21;
        r20 = r15.substring(r0, r1);	 Catch:{ NumberFormatException -> 0x040b }
        r20 = java.lang.Short.valueOf(r20);	 Catch:{ NumberFormatException -> 0x040b }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x040b }
        r0 = r20;
        r1 = r24;
        r1.year = r0;	 Catch:{ NumberFormatException -> 0x040b }
        goto L_0x0048;
    L_0x040b:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x041a:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse year from: ";
        r22 = r22.append(r23);
        r0 = r22;
        r22 = r0.append(r15);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x043c:
        r16 = r24.parseTextFrame(r25);
        r20 = java.lang.Long.valueOf(r16);	 Catch:{ NumberFormatException -> 0x0450 }
        r20 = r20.longValue();	 Catch:{ NumberFormatException -> 0x0450 }
        r0 = r20;
        r2 = r24;
        r2.duration = r0;	 Catch:{ NumberFormatException -> 0x0450 }
        goto L_0x0048;
    L_0x0450:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x045f:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse track duration: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r16;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x0483:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.artist = r0;
        goto L_0x0048;
    L_0x048f:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.albumArtist = r0;
        goto L_0x0048;
    L_0x049b:
        r17 = r24.parseTextFrame(r25);
        r20 = r17.length();
        if (r20 <= 0) goto L_0x0048;
    L_0x04a5:
        r20 = 47;
        r0 = r17;
        r1 = r20;
        r8 = r0.indexOf(r1);
        if (r8 >= 0) goto L_0x04f4;
    L_0x04b1:
        r20 = java.lang.Short.valueOf(r17);	 Catch:{ NumberFormatException -> 0x04c1 }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x04c1 }
        r0 = r20;
        r1 = r24;
        r1.disc = r0;	 Catch:{ NumberFormatException -> 0x04c1 }
        goto L_0x0048;
    L_0x04c1:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x04d0:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse disc number: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r17;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x04f4:
        r20 = 0;
        r0 = r17;
        r1 = r20;
        r20 = r0.substring(r1, r8);	 Catch:{ NumberFormatException -> 0x0559 }
        r20 = java.lang.Short.valueOf(r20);	 Catch:{ NumberFormatException -> 0x0559 }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x0559 }
        r0 = r20;
        r1 = r24;
        r1.disc = r0;	 Catch:{ NumberFormatException -> 0x0559 }
    L_0x050c:
        r20 = r8 + 1;
        r0 = r17;
        r1 = r20;
        r20 = r0.substring(r1);	 Catch:{ NumberFormatException -> 0x0526 }
        r20 = java.lang.Short.valueOf(r20);	 Catch:{ NumberFormatException -> 0x0526 }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x0526 }
        r0 = r20;
        r1 = r24;
        r1.discs = r0;	 Catch:{ NumberFormatException -> 0x0526 }
        goto L_0x0048;
    L_0x0526:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x0535:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse number of discs: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r17;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x0559:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x050c;
    L_0x0568:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse disc number: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r17;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x050c;
    L_0x058b:
        r18 = r24.parseTextFrame(r25);
        r20 = r18.length();
        if (r20 <= 0) goto L_0x0048;
    L_0x0595:
        r20 = 47;
        r0 = r18;
        r1 = r20;
        r8 = r0.indexOf(r1);
        if (r8 >= 0) goto L_0x05e4;
    L_0x05a1:
        r20 = java.lang.Short.valueOf(r18);	 Catch:{ NumberFormatException -> 0x05b1 }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x05b1 }
        r0 = r20;
        r1 = r24;
        r1.track = r0;	 Catch:{ NumberFormatException -> 0x05b1 }
        goto L_0x0048;
    L_0x05b1:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x05c0:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse track number: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r18;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x05e4:
        r20 = 0;
        r0 = r18;
        r1 = r20;
        r20 = r0.substring(r1, r8);	 Catch:{ NumberFormatException -> 0x0649 }
        r20 = java.lang.Short.valueOf(r20);	 Catch:{ NumberFormatException -> 0x0649 }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x0649 }
        r0 = r20;
        r1 = r24;
        r1.track = r0;	 Catch:{ NumberFormatException -> 0x0649 }
    L_0x05fc:
        r20 = r8 + 1;
        r0 = r18;
        r1 = r20;
        r20 = r0.substring(r1);	 Catch:{ NumberFormatException -> 0x0616 }
        r20 = java.lang.Short.valueOf(r20);	 Catch:{ NumberFormatException -> 0x0616 }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x0616 }
        r0 = r20;
        r1 = r24;
        r1.tracks = r0;	 Catch:{ NumberFormatException -> 0x0616 }
        goto L_0x0048;
    L_0x0616:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x0625:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse number of tracks: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r18;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x0649:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x05fc;
    L_0x0658:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse track number: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r18;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x05fc;
    L_0x067b:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.grouping = r0;
        goto L_0x0048;
    L_0x0687:
        r20 = r24.parseTextFrame(r25);
        r0 = r20;
        r1 = r24;
        r1.title = r0;
        goto L_0x0048;
    L_0x0693:
        r19 = r24.parseTextFrame(r25);
        r20 = r19.length();
        if (r20 <= 0) goto L_0x0048;
    L_0x069d:
        r20 = java.lang.Short.valueOf(r19);	 Catch:{ NumberFormatException -> 0x06ad }
        r20 = r20.shortValue();	 Catch:{ NumberFormatException -> 0x06ad }
        r0 = r20;
        r1 = r24;
        r1.year = r0;	 Catch:{ NumberFormatException -> 0x06ad }
        goto L_0x0048;
    L_0x06ad:
        r6 = move-exception;
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r20 = r20.isLoggable(r21);
        if (r20 == 0) goto L_0x0048;
    L_0x06bc:
        r20 = LOGGER;
        r0 = r24;
        r0 = r0.debugLevel;
        r21 = r0;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "Could not parse year: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r19;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r20.log(r21, r22);
        goto L_0x0048;
    L_0x06e0:
        r0 = r24;
        r0 = r0.lyrics;
        r20 = r0;
        if (r20 != 0) goto L_0x0048;
    L_0x06e8:
        r20 = r24.parseCommentOrUnsynchronizedLyricsFrame(r25);
        r0 = r20;
        r0 = r0.text;
        r20 = r0;
        r0 = r20;
        r1 = r24;
        r1.lyrics = r0;
        goto L_0x0048;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2Info.parseFrame(org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody):void");
    }

    String parseTextFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        return frame.readFixedLengthString((int) frame.getRemainingLength(), frame.readEncoding());
    }

    CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        ID3v2Encoding encoding = data.readEncoding();
        return new CommentOrUnsynchronizedLyrics(data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), data.readZeroTerminatedString(Callback.DEFAULT_DRAG_ANIMATION_DURATION, encoding), data.readFixedLengthString((int) data.getRemainingLength(), encoding));
    }

    AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        String imageType;
        ID3v2Encoding encoding = data.readEncoding();
        if (data.getTagHeader().getVersion() == 2) {
            String toUpperCase = data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
            Object obj = -1;
            switch (toUpperCase.hashCode()) {
                case 73665:
                    if (toUpperCase.equals("JPG")) {
                        obj = 1;
                        break;
                    }
                    break;
                case 79369:
                    if (toUpperCase.equals("PNG")) {
                        obj = null;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    imageType = "image/png";
                    break;
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    imageType = "image/jpeg";
                    break;
                default:
                    imageType = "image/unknown";
                    break;
            }
        }
        imageType = data.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        return new AttachedPicture(data.getData().readByte(), data.readZeroTerminatedString(Callback.DEFAULT_DRAG_ANIMATION_DURATION, encoding), imageType, data.getData().readFully((int) data.getRemainingLength()));
    }
}
