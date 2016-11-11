package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class Id3Reader extends ElementaryStreamReader {
    private static final int ID3_HEADER_SIZE = 10;
    private final ParsableByteArray id3Header;
    private int sampleBytesRead;
    private int sampleSize;
    private long sampleTimeUs;
    private boolean writingSample;

    public Id3Reader(TrackOutput output) {
        super(output);
        output.format(MediaFormat.createId3Format());
        this.id3Header = new ParsableByteArray((int) ID3_HEADER_SIZE);
    }

    public void seek() {
        this.writingSample = false;
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        if (dataAlignmentIndicator) {
            this.writingSample = true;
            this.sampleTimeUs = pesTimeUs;
            this.sampleSize = 0;
            this.sampleBytesRead = 0;
        }
    }

    public void consume(ParsableByteArray data) {
        if (this.writingSample) {
            int bytesAvailable = data.bytesLeft();
            if (this.sampleBytesRead < ID3_HEADER_SIZE) {
                int headerBytesAvailable = Math.min(bytesAvailable, 10 - this.sampleBytesRead);
                System.arraycopy(data.data, data.getPosition(), this.id3Header.data, this.sampleBytesRead, headerBytesAvailable);
                if (this.sampleBytesRead + headerBytesAvailable == ID3_HEADER_SIZE) {
                    this.id3Header.setPosition(6);
                    this.sampleSize = this.id3Header.readSynchSafeInt() + ID3_HEADER_SIZE;
                }
            }
            int bytesToWrite = Math.min(bytesAvailable, this.sampleSize - this.sampleBytesRead);
            this.output.sampleData(data, bytesToWrite);
            this.sampleBytesRead += bytesToWrite;
        }
    }

    public void packetFinished() {
        if (this.writingSample && this.sampleSize != 0 && this.sampleBytesRead == this.sampleSize) {
            this.output.sampleMetadata(this.sampleTimeUs, 1, this.sampleSize, 0, null);
            this.writingSample = false;
        }
    }
}
