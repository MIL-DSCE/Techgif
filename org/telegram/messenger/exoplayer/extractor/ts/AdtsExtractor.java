package org.telegram.messenger.exoplayer.extractor.ts;

import android.support.v4.media.TransportMediator;
import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class AdtsExtractor implements Extractor {
    private static final int ID3_TAG;
    private static final int MAX_PACKET_SIZE = 200;
    private static final int MAX_SNIFF_BYTES = 8192;
    private AdtsReader adtsReader;
    private final long firstSampleTimestampUs;
    private final ParsableByteArray packetBuffer;
    private boolean startedPacket;

    static {
        ID3_TAG = Util.getIntegerCodeForString("ID3");
    }

    public AdtsExtractor() {
        this(0);
    }

    public AdtsExtractor(long firstSampleTimestampUs) {
        this.firstSampleTimestampUs = firstSampleTimestampUs;
        this.packetBuffer = new ParsableByteArray((int) MAX_PACKET_SIZE);
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(10);
        ParsableBitArray scratchBits = new ParsableBitArray(scratch.data);
        int startPosition = ID3_TAG;
        while (true) {
            input.peekFully(scratch.data, ID3_TAG, 10);
            scratch.setPosition(ID3_TAG);
            if (scratch.readUnsignedInt24() != ID3_TAG) {
                break;
            }
            int length = ((((scratch.data[6] & TransportMediator.KEYCODE_MEDIA_PAUSE) << 21) | ((scratch.data[7] & TransportMediator.KEYCODE_MEDIA_PAUSE) << 14)) | ((scratch.data[8] & TransportMediator.KEYCODE_MEDIA_PAUSE) << 7)) | (scratch.data[9] & TransportMediator.KEYCODE_MEDIA_PAUSE);
            startPosition += length + 10;
            input.advancePeekPosition(length);
        }
        input.resetPeekPosition();
        input.advancePeekPosition(startPosition);
        int headerPosition = startPosition;
        int validFramesSize = ID3_TAG;
        int validFramesCount = ID3_TAG;
        while (true) {
            input.peekFully(scratch.data, ID3_TAG, 2);
            scratch.setPosition(ID3_TAG);
            if ((65526 & scratch.readUnsignedShort()) != 65520) {
                validFramesCount = ID3_TAG;
                validFramesSize = ID3_TAG;
                input.resetPeekPosition();
                headerPosition++;
                if (headerPosition - startPosition >= MAX_SNIFF_BYTES) {
                    return false;
                }
                input.advancePeekPosition(headerPosition);
            } else {
                validFramesCount++;
                if (validFramesCount >= 4 && validFramesSize > 188) {
                    return true;
                }
                input.peekFully(scratch.data, ID3_TAG, 4);
                scratchBits.setPosition(14);
                int frameSize = scratchBits.readBits(13);
                if (frameSize <= 6) {
                    return false;
                }
                input.advancePeekPosition(frameSize - 6);
                validFramesSize += frameSize;
            }
        }
    }

    public void init(ExtractorOutput output) {
        this.adtsReader = new AdtsReader(output.track(ID3_TAG), output.track(1));
        output.endTracks();
        output.seekMap(SeekMap.UNSEEKABLE);
    }

    public void seek() {
        this.startedPacket = false;
        this.adtsReader.seek();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int bytesRead = input.read(this.packetBuffer.data, ID3_TAG, MAX_PACKET_SIZE);
        if (bytesRead == -1) {
            return -1;
        }
        this.packetBuffer.setPosition(ID3_TAG);
        this.packetBuffer.setLimit(bytesRead);
        if (!this.startedPacket) {
            this.adtsReader.packetStarted(this.firstSampleTimestampUs, true);
            this.startedPacket = true;
        }
        this.adtsReader.consume(this.packetBuffer);
        return ID3_TAG;
    }
}
