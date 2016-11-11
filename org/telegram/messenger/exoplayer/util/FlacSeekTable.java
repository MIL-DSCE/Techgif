package org.telegram.messenger.exoplayer.util;

import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.extractor.SeekMap;

public final class FlacSeekTable {
    private static final int METADATA_LENGTH_OFFSET = 1;
    private static final int SEEK_POINT_SIZE = 18;
    private final long[] offsets;
    private final long[] sampleNumbers;

    /* renamed from: org.telegram.messenger.exoplayer.util.FlacSeekTable.1 */
    class C17011 implements SeekMap {
        final /* synthetic */ long val$firstFrameOffset;
        final /* synthetic */ long val$sampleRate;

        C17011(long j, long j2) {
            this.val$sampleRate = j;
            this.val$firstFrameOffset = j2;
        }

        public boolean isSeekable() {
            return true;
        }

        public long getPosition(long timeUs) {
            return this.val$firstFrameOffset + FlacSeekTable.this.offsets[Util.binarySearchFloor(FlacSeekTable.this.sampleNumbers, (this.val$sampleRate * timeUs) / C0747C.MICROS_PER_SECOND, true, true)];
        }
    }

    public static FlacSeekTable parseSeekTable(ParsableByteArray data) {
        data.skipBytes(METADATA_LENGTH_OFFSET);
        int numberOfSeekPoints = data.readUnsignedInt24() / SEEK_POINT_SIZE;
        long[] sampleNumbers = new long[numberOfSeekPoints];
        long[] offsets = new long[numberOfSeekPoints];
        for (int i = 0; i < numberOfSeekPoints; i += METADATA_LENGTH_OFFSET) {
            sampleNumbers[i] = data.readLong();
            offsets[i] = data.readLong();
            data.skipBytes(2);
        }
        return new FlacSeekTable(sampleNumbers, offsets);
    }

    private FlacSeekTable(long[] sampleNumbers, long[] offsets) {
        this.sampleNumbers = sampleNumbers;
        this.offsets = offsets;
    }

    public SeekMap createSeekMap(long firstFrameOffset, long sampleRate) {
        return new C17011(sampleRate, firstFrameOffset);
    }
}
