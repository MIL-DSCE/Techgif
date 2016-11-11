package org.telegram.messenger.exoplayer.extractor.webm;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;

final class VarintReader {
    private static final int STATE_BEGIN_READING = 0;
    private static final int STATE_READ_CONTENTS = 1;
    private static final long[] VARINT_LENGTH_MASKS;
    private int length;
    private final byte[] scratch;
    private int state;

    static {
        VARINT_LENGTH_MASKS = new long[]{128, 64, 32, 16, 8, 4, 2, 1};
    }

    public VarintReader() {
        this.scratch = new byte[8];
    }

    public void reset() {
        this.state = STATE_BEGIN_READING;
        this.length = STATE_BEGIN_READING;
    }

    public long readUnsignedVarint(ExtractorInput input, boolean allowEndOfInput, boolean removeLengthMask, int maximumAllowedLength) throws IOException, InterruptedException {
        if (this.state == 0) {
            if (!input.readFully(this.scratch, STATE_BEGIN_READING, STATE_READ_CONTENTS, allowEndOfInput)) {
                return -1;
            }
            this.length = parseUnsignedVarintLength(this.scratch[STATE_BEGIN_READING] & NalUnitUtil.EXTENDED_SAR);
            if (this.length == -1) {
                throw new IllegalStateException("No valid varint length mask found");
            }
            this.state = STATE_READ_CONTENTS;
        }
        if (this.length > maximumAllowedLength) {
            this.state = STATE_BEGIN_READING;
            return -2;
        }
        if (this.length != STATE_READ_CONTENTS) {
            input.readFully(this.scratch, STATE_READ_CONTENTS, this.length - 1);
        }
        this.state = STATE_BEGIN_READING;
        return assembleVarint(this.scratch, this.length, removeLengthMask);
    }

    public int getLastLength() {
        return this.length;
    }

    public static int parseUnsignedVarintLength(int firstByte) {
        for (int i = STATE_BEGIN_READING; i < VARINT_LENGTH_MASKS.length; i += STATE_READ_CONTENTS) {
            if ((VARINT_LENGTH_MASKS[i] & ((long) firstByte)) != 0) {
                return i + STATE_READ_CONTENTS;
            }
        }
        return -1;
    }

    public static long assembleVarint(byte[] varintBytes, int varintLength, boolean removeLengthMask) {
        long varint = ((long) varintBytes[STATE_BEGIN_READING]) & 255;
        if (removeLengthMask) {
            varint &= VARINT_LENGTH_MASKS[varintLength - 1] ^ -1;
        }
        for (int i = STATE_READ_CONTENTS; i < varintLength; i += STATE_READ_CONTENTS) {
            varint = (varint << 8) | (((long) varintBytes[i]) & 255);
        }
        return varint;
    }
}
