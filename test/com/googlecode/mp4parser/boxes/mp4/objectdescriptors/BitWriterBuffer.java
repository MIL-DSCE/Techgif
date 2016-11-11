package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.InputDeviceCompat;
import java.nio.ByteBuffer;
import org.telegram.messenger.MessagesController;

public class BitWriterBuffer {
    static final /* synthetic */ boolean $assertionsDisabled;
    private ByteBuffer buffer;
    int initialPos;
    int position;

    static {
        $assertionsDisabled = !BitWriterBuffer.class.desiredAssertionStatus();
    }

    public BitWriterBuffer(ByteBuffer buffer) {
        this.position = 0;
        this.buffer = buffer;
        this.initialPos = buffer.position();
    }

    public void writeBits(int i, int numBits) {
        int i2 = 1;
        if ($assertionsDisabled || i <= (1 << numBits) - 1) {
            ByteBuffer byteBuffer;
            int i3;
            int left = 8 - (this.position % 8);
            if (numBits <= left) {
                int current = this.buffer.get(this.initialPos + (this.position / 8));
                if (current < 0) {
                    current += MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                }
                current += i << (left - numBits);
                byteBuffer = this.buffer;
                i3 = this.initialPos + (this.position / 8);
                if (current > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                    current += InputDeviceCompat.SOURCE_ANY;
                }
                byteBuffer.put(i3, (byte) current);
                this.position += numBits;
            } else {
                int bitsSecondWrite = numBits - left;
                writeBits(i >> bitsSecondWrite, left);
                writeBits(((1 << bitsSecondWrite) - 1) & i, bitsSecondWrite);
            }
            byteBuffer = this.buffer;
            i3 = this.initialPos + (this.position / 8);
            if (this.position % 8 <= 0) {
                i2 = 0;
            }
            byteBuffer.position(i2 + i3);
            return;
        }
        throw new AssertionError(String.format("Trying to write a value bigger (%s) than the number bits (%s) allows. Please mask the value before writing it and make your code is really working as intended.", new Object[]{Integer.valueOf(i), Integer.valueOf((1 << numBits) - 1)}));
    }
}
