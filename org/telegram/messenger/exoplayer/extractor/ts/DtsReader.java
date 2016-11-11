package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.DtsUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class DtsReader extends ElementaryStreamReader {
    private static final int HEADER_SIZE = 15;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private static final int SYNC_VALUE = 2147385345;
    private static final int SYNC_VALUE_SIZE = 4;
    private int bytesRead;
    private final ParsableByteArray headerScratchBytes;
    private MediaFormat mediaFormat;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private int syncBytes;
    private long timeUs;

    public DtsReader(TrackOutput output) {
        super(output);
        this.headerScratchBytes = new ParsableByteArray(new byte[HEADER_SIZE]);
        this.headerScratchBytes.data[STATE_FINDING_SYNC] = Byte.MAX_VALUE;
        this.headerScratchBytes.data[STATE_READING_HEADER] = (byte) -2;
        this.headerScratchBytes.data[STATE_READING_SAMPLE] = Byte.MIN_VALUE;
        this.headerScratchBytes.data[3] = (byte) 1;
        this.state = STATE_FINDING_SYNC;
    }

    public void seek() {
        this.state = STATE_FINDING_SYNC;
        this.bytesRead = STATE_FINDING_SYNC;
        this.syncBytes = STATE_FINDING_SYNC;
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case STATE_FINDING_SYNC /*0*/:
                    if (!skipToNextSync(data)) {
                        break;
                    }
                    this.bytesRead = SYNC_VALUE_SIZE;
                    this.state = STATE_READING_HEADER;
                    break;
                case STATE_READING_HEADER /*1*/:
                    if (!continueRead(data, this.headerScratchBytes.data, HEADER_SIZE)) {
                        break;
                    }
                    parseHeader();
                    this.headerScratchBytes.setPosition(STATE_FINDING_SYNC);
                    this.output.sampleData(this.headerScratchBytes, HEADER_SIZE);
                    this.state = STATE_READING_SAMPLE;
                    break;
                case STATE_READING_SAMPLE /*2*/:
                    int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
                    this.output.sampleData(data, bytesToRead);
                    this.bytesRead += bytesToRead;
                    if (this.bytesRead != this.sampleSize) {
                        break;
                    }
                    this.output.sampleMetadata(this.timeUs, STATE_READING_HEADER, this.sampleSize, STATE_FINDING_SYNC, null);
                    this.timeUs += this.sampleDurationUs;
                    this.state = STATE_FINDING_SYNC;
                    break;
                default:
                    break;
            }
        }
    }

    public void packetFinished() {
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        source.readBytes(target, this.bytesRead, bytesToRead);
        this.bytesRead += bytesToRead;
        return this.bytesRead == targetLength;
    }

    private boolean skipToNextSync(ParsableByteArray pesBuffer) {
        while (pesBuffer.bytesLeft() > 0) {
            this.syncBytes <<= 8;
            this.syncBytes |= pesBuffer.readUnsignedByte();
            if (this.syncBytes == SYNC_VALUE) {
                this.syncBytes = STATE_FINDING_SYNC;
                return true;
            }
        }
        return false;
    }

    private void parseHeader() {
        byte[] frameData = this.headerScratchBytes.data;
        if (this.mediaFormat == null) {
            this.mediaFormat = DtsUtil.parseDtsFormat(frameData, null, -1, null);
            this.output.format(this.mediaFormat);
        }
        this.sampleSize = DtsUtil.getDtsFrameSize(frameData);
        this.sampleDurationUs = (long) ((int) ((C0747C.MICROS_PER_SECOND * ((long) DtsUtil.parseDtsAudioSampleCount(frameData))) / ((long) this.mediaFormat.sampleRate)));
    }
}
