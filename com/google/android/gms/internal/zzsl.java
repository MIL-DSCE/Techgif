package com.google.android.gms.internal;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;

public class zzsl {
    private final byte[] zzbtW;
    private int zzbtX;
    private int zzbtY;

    public zzsl(byte[] bArr) {
        int i;
        this.zzbtW = new byte[MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE];
        for (i = 0; i < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE; i++) {
            this.zzbtW[i] = (byte) i;
        }
        i = 0;
        for (int i2 = 0; i2 < MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE; i2++) {
            i = ((i + this.zzbtW[i2]) + bArr[i2 % bArr.length]) & NalUnitUtil.EXTENDED_SAR;
            byte b = this.zzbtW[i2];
            this.zzbtW[i2] = this.zzbtW[i];
            this.zzbtW[i] = b;
        }
        this.zzbtX = 0;
        this.zzbtY = 0;
    }

    public void zzC(byte[] bArr) {
        int i = this.zzbtX;
        int i2 = this.zzbtY;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            i = (i + 1) & NalUnitUtil.EXTENDED_SAR;
            i2 = (i2 + this.zzbtW[i]) & NalUnitUtil.EXTENDED_SAR;
            byte b = this.zzbtW[i];
            this.zzbtW[i] = this.zzbtW[i2];
            this.zzbtW[i2] = b;
            bArr[i3] = (byte) (bArr[i3] ^ this.zzbtW[(this.zzbtW[i] + this.zzbtW[i2]) & NalUnitUtil.EXTENDED_SAR]);
        }
        this.zzbtX = i;
        this.zzbtY = i2;
    }
}
