package org.telegram.messenger.audioinfo.mp3;

import android.support.v4.media.TransportMediator;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;

public class ID3v2DataInput {
    private final InputStream input;

    public ID3v2DataInput(InputStream in) {
        this.input = in;
    }

    public final void readFully(byte[] b, int off, int len) throws IOException {
        int total = 0;
        while (total < len) {
            int current = this.input.read(b, off + total, len - total);
            if (current > 0) {
                total += current;
            } else {
                throw new EOFException();
            }
        }
    }

    public byte[] readFully(int len) throws IOException {
        byte[] bytes = new byte[len];
        readFully(bytes, 0, len);
        return bytes;
    }

    public void skipFully(long len) throws IOException {
        long total = 0;
        while (total < len) {
            long current = this.input.skip(len - total);
            if (current > 0) {
                total += current;
            } else {
                throw new EOFException();
            }
        }
    }

    public byte readByte() throws IOException {
        int b = this.input.read();
        if (b >= 0) {
            return (byte) b;
        }
        throw new EOFException();
    }

    public int readInt() throws IOException {
        return ((((readByte() & NalUnitUtil.EXTENDED_SAR) << 24) | ((readByte() & NalUnitUtil.EXTENDED_SAR) << 16)) | ((readByte() & NalUnitUtil.EXTENDED_SAR) << 8)) | (readByte() & NalUnitUtil.EXTENDED_SAR);
    }

    public int readSyncsafeInt() throws IOException {
        return ((((readByte() & TransportMediator.KEYCODE_MEDIA_PAUSE) << 21) | ((readByte() & TransportMediator.KEYCODE_MEDIA_PAUSE) << 14)) | ((readByte() & TransportMediator.KEYCODE_MEDIA_PAUSE) << 7)) | (readByte() & TransportMediator.KEYCODE_MEDIA_PAUSE);
    }
}
