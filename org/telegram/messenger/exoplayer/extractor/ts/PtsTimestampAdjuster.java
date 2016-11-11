package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.C0747C;

public final class PtsTimestampAdjuster {
    public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
    private static final long MAX_PTS_PLUS_ONE = 8589934592L;
    private final long firstSampleTimestampUs;
    private volatile long lastPts;
    private long timestampOffsetUs;

    public PtsTimestampAdjuster(long firstSampleTimestampUs) {
        this.firstSampleTimestampUs = firstSampleTimestampUs;
        this.lastPts = Long.MIN_VALUE;
    }

    public void reset() {
        this.lastPts = Long.MIN_VALUE;
    }

    public boolean isInitialized() {
        return this.lastPts != Long.MIN_VALUE;
    }

    public long adjustTimestamp(long pts) {
        if (this.lastPts != Long.MIN_VALUE) {
            long closestWrapCount = (this.lastPts + 4294967296L) / MAX_PTS_PLUS_ONE;
            long ptsWrapBelow = pts + (MAX_PTS_PLUS_ONE * (closestWrapCount - 1));
            long ptsWrapAbove = pts + (MAX_PTS_PLUS_ONE * closestWrapCount);
            if (Math.abs(ptsWrapBelow - this.lastPts) < Math.abs(ptsWrapAbove - this.lastPts)) {
                pts = ptsWrapBelow;
            } else {
                pts = ptsWrapAbove;
            }
        }
        long timeUs = ptsToUs(pts);
        if (this.firstSampleTimestampUs != DO_NOT_OFFSET && this.lastPts == Long.MIN_VALUE) {
            this.timestampOffsetUs = this.firstSampleTimestampUs - timeUs;
        }
        this.lastPts = pts;
        return this.timestampOffsetUs + timeUs;
    }

    public static long ptsToUs(long pts) {
        return (C0747C.MICROS_PER_SECOND * pts) / 90000;
    }

    public static long usToPts(long us) {
        return (90000 * us) / C0747C.MICROS_PER_SECOND;
    }
}
