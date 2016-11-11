package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.Ac3Util;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class Ac3Reader extends ElementaryStreamReader {
    private static final int HEADER_SIZE = 8;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int bytesRead;
    private final ParsableBitArray headerScratchBits;
    private final ParsableByteArray headerScratchBytes;
    private final boolean isEac3;
    private boolean lastByteWas0B;
    private MediaFormat mediaFormat;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    public Ac3Reader(TrackOutput output, boolean isEac3) {
        super(output);
        this.isEac3 = isEac3;
        this.headerScratchBits = new ParsableBitArray(new byte[HEADER_SIZE]);
        this.headerScratchBytes = new ParsableByteArray(this.headerScratchBits.data);
        this.state = STATE_FINDING_SYNC;
    }

    public void seek() {
        this.state = STATE_FINDING_SYNC;
        this.bytesRead = STATE_FINDING_SYNC;
        this.lastByteWas0B = false;
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
                    this.state = STATE_READING_HEADER;
                    this.headerScratchBytes.data[STATE_FINDING_SYNC] = (byte) 11;
                    this.headerScratchBytes.data[STATE_READING_HEADER] = (byte) 119;
                    this.bytesRead = STATE_READING_SAMPLE;
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
            if (this.lastByteWas0B) {
                int secondByte = pesBuffer.readUnsignedByte();
                if (secondByte == 119) {
                    this.lastByteWas0B = false;
                    return true;
                }
                this.lastByteWas0B = secondByte == 11;
            } else {
                this.lastByteWas0B = pesBuffer.readUnsignedByte() == 11;
            }
        }
        return false;
    }

    private void parseHeader() {
        if (this.mediaFormat == null) {
            this.mediaFormat = this.isEac3 ? Ac3Util.parseEac3SyncframeFormat(this.headerScratchBits, null, -1, null) : Ac3Util.parseAc3SyncframeFormat(this.headerScratchBits, null, -1, null);
            this.output.format(this.mediaFormat);
        }
        this.sampleSize = this.isEac3 ? Ac3Util.parseEAc3SyncframeSize(this.headerScratchBits.data) : Ac3Util.parseAc3SyncframeSize(this.headerScratchBits.data);
        this.sampleDurationUs = (long) ((int) ((C0747C.MICROS_PER_SECOND * ((long) (this.isEac3 ? Ac3Util.parseEAc3SyncframeAudioSampleCount(this.headerScratchBits.data) : Ac3Util.getAc3SyncframeAudioSampleCount()))) / ((long) this.mediaFormat.sampleRate)));
    }
}
