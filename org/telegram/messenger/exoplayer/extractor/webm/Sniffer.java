package org.telegram.messenger.exoplayer.extractor.webm;

import java.io.IOException;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class Sniffer {
    private static final int ID_EBML = 440786851;
    private static final int SEARCH_LENGTH = 1024;
    private int peekLength;
    private final ParsableByteArray scratch;

    public Sniffer() {
        this.scratch = new ParsableByteArray(8);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean sniff(org.telegram.messenger.exoplayer.extractor.ExtractorInput r23) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r22 = this;
        r12 = r23.getLength();
        r18 = -1;
        r5 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1));
        if (r5 == 0) goto L_0x0010;
    L_0x000a:
        r18 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r5 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1));
        if (r5 <= 0) goto L_0x004a;
    L_0x0010:
        r18 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
    L_0x0012:
        r0 = r18;
        r4 = (int) r0;
        r0 = r22;
        r5 = r0.scratch;
        r5 = r5.data;
        r18 = 0;
        r19 = 4;
        r0 = r23;
        r1 = r18;
        r2 = r19;
        r0.peekFully(r5, r1, r2);
        r0 = r22;
        r5 = r0.scratch;
        r16 = r5.readUnsignedInt();
        r5 = 4;
        r0 = r22;
        r0.peekLength = r5;
    L_0x0035:
        r18 = 440786851; // 0x1a45dfa3 float:4.0919297E-23 double:2.1777764E-315;
        r5 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
        if (r5 == 0) goto L_0x007a;
    L_0x003c:
        r0 = r22;
        r5 = r0.peekLength;
        r5 = r5 + 1;
        r0 = r22;
        r0.peekLength = r5;
        if (r5 != r4) goto L_0x004d;
    L_0x0048:
        r5 = 0;
    L_0x0049:
        return r5;
    L_0x004a:
        r18 = r12;
        goto L_0x0012;
    L_0x004d:
        r0 = r22;
        r5 = r0.scratch;
        r5 = r5.data;
        r18 = 0;
        r19 = 1;
        r0 = r23;
        r1 = r18;
        r2 = r19;
        r0.peekFully(r5, r1, r2);
        r5 = 8;
        r18 = r16 << r5;
        r20 = -256; // 0xffffffffffffff00 float:NaN double:NaN;
        r16 = r18 & r20;
        r0 = r22;
        r5 = r0.scratch;
        r5 = r5.data;
        r18 = 0;
        r5 = r5[r18];
        r5 = r5 & 255;
        r0 = (long) r5;
        r18 = r0;
        r16 = r16 | r18;
        goto L_0x0035;
    L_0x007a:
        r6 = r22.readUint(r23);
        r0 = r22;
        r5 = r0.peekLength;
        r8 = (long) r5;
        r18 = -9223372036854775808;
        r5 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r5 == 0) goto L_0x0095;
    L_0x0089:
        r18 = -1;
        r5 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1));
        if (r5 == 0) goto L_0x00b3;
    L_0x008f:
        r18 = r8 + r6;
        r5 = (r18 > r12 ? 1 : (r18 == r12 ? 0 : -1));
        if (r5 < 0) goto L_0x00b3;
    L_0x0095:
        r5 = 0;
        goto L_0x0049;
    L_0x0097:
        r18 = 0;
        r5 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r5 == 0) goto L_0x00b3;
    L_0x009d:
        r5 = (int) r14;
        r0 = r23;
        r0.advancePeekPosition(r5);
        r0 = r22;
        r5 = r0.peekLength;
        r0 = (long) r5;
        r18 = r0;
        r18 = r18 + r14;
        r0 = r18;
        r5 = (int) r0;
        r0 = r22;
        r0.peekLength = r5;
    L_0x00b3:
        r0 = r22;
        r5 = r0.peekLength;
        r0 = (long) r5;
        r18 = r0;
        r20 = r8 + r6;
        r5 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r5 >= 0) goto L_0x00e1;
    L_0x00c0:
        r10 = r22.readUint(r23);
        r18 = -9223372036854775808;
        r5 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r5 != 0) goto L_0x00cd;
    L_0x00ca:
        r5 = 0;
        goto L_0x0049;
    L_0x00cd:
        r14 = r22.readUint(r23);
        r18 = 0;
        r5 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r5 < 0) goto L_0x00de;
    L_0x00d7:
        r18 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r5 <= 0) goto L_0x0097;
    L_0x00de:
        r5 = 0;
        goto L_0x0049;
    L_0x00e1:
        r0 = r22;
        r5 = r0.peekLength;
        r0 = (long) r5;
        r18 = r0;
        r20 = r8 + r6;
        r5 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r5 != 0) goto L_0x00f1;
    L_0x00ee:
        r5 = 1;
        goto L_0x0049;
    L_0x00f1:
        r5 = 0;
        goto L_0x0049;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.extractor.webm.Sniffer.sniff(org.telegram.messenger.exoplayer.extractor.ExtractorInput):boolean");
    }

    private long readUint(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, 0, 1);
        int value = this.scratch.data[0] & NalUnitUtil.EXTENDED_SAR;
        if (value == 0) {
            return Long.MIN_VALUE;
        }
        int mask = MessagesController.UPDATE_MASK_USER_PHONE;
        int length = 0;
        while ((value & mask) == 0) {
            mask >>= 1;
            length++;
        }
        value &= mask ^ -1;
        input.peekFully(this.scratch.data, 1, length);
        for (int i = 0; i < length; i++) {
            value = (value << 8) + (this.scratch.data[i + 1] & NalUnitUtil.EXTENDED_SAR);
        }
        this.peekLength += length + 1;
        return (long) value;
    }
}
