package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.MpegAudioHeader;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class MpegAudioReader extends ElementaryStreamReader {
    private static final int HEADER_SIZE = 4;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_FRAME = 2;
    private static final int STATE_READING_HEADER = 1;
    private int frameBytesRead;
    private long frameDurationUs;
    private int frameSize;
    private boolean hasOutputFormat;
    private final MpegAudioHeader header;
    private final ParsableByteArray headerScratch;
    private boolean lastByteWasFF;
    private int state;
    private long timeUs;

    public MpegAudioReader(TrackOutput output) {
        super(output);
        this.state = STATE_FINDING_HEADER;
        this.headerScratch = new ParsableByteArray((int) HEADER_SIZE);
        this.headerScratch.data[STATE_FINDING_HEADER] = (byte) -1;
        this.header = new MpegAudioHeader();
    }

    public void seek() {
        this.state = STATE_FINDING_HEADER;
        this.frameBytesRead = STATE_FINDING_HEADER;
        this.lastByteWasFF = false;
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case STATE_FINDING_HEADER /*0*/:
                    findHeader(data);
                    break;
                case STATE_READING_HEADER /*1*/:
                    readHeaderRemainder(data);
                    break;
                case STATE_READING_FRAME /*2*/:
                    readFrameRemainder(data);
                    break;
                default:
                    break;
            }
        }
    }

    public void packetFinished() {
    }

    private void findHeader(ParsableByteArray source) {
        byte[] data = source.data;
        int startOffset = source.getPosition();
        int endOffset = source.limit();
        int i = startOffset;
        while (i < endOffset) {
            boolean found;
            boolean byteIsFF = (data[i] & NalUnitUtil.EXTENDED_SAR) == NalUnitUtil.EXTENDED_SAR;
            if (this.lastByteWasFF && (data[i] & PsExtractor.VIDEO_STREAM) == PsExtractor.VIDEO_STREAM) {
                found = true;
            } else {
                found = false;
            }
            this.lastByteWasFF = byteIsFF;
            if (found) {
                source.setPosition(i + STATE_READING_HEADER);
                this.lastByteWasFF = false;
                this.headerScratch.data[STATE_READING_HEADER] = data[i];
                this.frameBytesRead = STATE_READING_FRAME;
                this.state = STATE_READING_HEADER;
                return;
            }
            i += STATE_READING_HEADER;
        }
        source.setPosition(endOffset);
    }

    private void readHeaderRemainder(ParsableByteArray source) {
        int bytesToRead = Math.min(source.bytesLeft(), 4 - this.frameBytesRead);
        source.readBytes(this.headerScratch.data, this.frameBytesRead, bytesToRead);
        this.frameBytesRead += bytesToRead;
        if (this.frameBytesRead >= HEADER_SIZE) {
            this.headerScratch.setPosition(STATE_FINDING_HEADER);
            if (MpegAudioHeader.populateHeader(this.headerScratch.readInt(), this.header)) {
                this.frameSize = this.header.frameSize;
                if (!this.hasOutputFormat) {
                    this.frameDurationUs = (C0747C.MICROS_PER_SECOND * ((long) this.header.samplesPerFrame)) / ((long) this.header.sampleRate);
                    this.output.format(MediaFormat.createAudioFormat(null, this.header.mimeType, -1, MessagesController.UPDATE_MASK_SEND_STATE, -1, this.header.channels, this.header.sampleRate, null, null));
                    this.hasOutputFormat = true;
                }
                this.headerScratch.setPosition(STATE_FINDING_HEADER);
                this.output.sampleData(this.headerScratch, HEADER_SIZE);
                this.state = STATE_READING_FRAME;
                return;
            }
            this.frameBytesRead = STATE_FINDING_HEADER;
            this.state = STATE_READING_HEADER;
        }
    }

    private void readFrameRemainder(ParsableByteArray source) {
        int bytesToRead = Math.min(source.bytesLeft(), this.frameSize - this.frameBytesRead);
        this.output.sampleData(source, bytesToRead);
        this.frameBytesRead += bytesToRead;
        if (this.frameBytesRead >= this.frameSize) {
            this.output.sampleMetadata(this.timeUs, STATE_READING_HEADER, this.frameSize, STATE_FINDING_HEADER, null);
            this.timeUs += this.frameDurationUs;
            this.frameBytesRead = STATE_FINDING_HEADER;
            this.state = STATE_FINDING_HEADER;
        }
    }
}
