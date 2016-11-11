package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.Log;
import android.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class AdtsReader extends ElementaryStreamReader {
    private static final int CRC_SIZE = 2;
    private static final int HEADER_SIZE = 5;
    private static final int ID3_HEADER_SIZE = 10;
    private static final byte[] ID3_IDENTIFIER;
    private static final int ID3_SIZE_OFFSET = 6;
    private static final int MATCH_STATE_FF = 512;
    private static final int MATCH_STATE_I = 768;
    private static final int MATCH_STATE_ID = 1024;
    private static final int MATCH_STATE_START = 256;
    private static final int MATCH_STATE_VALUE_SHIFT = 8;
    private static final int STATE_FINDING_SAMPLE = 0;
    private static final int STATE_READING_ADTS_HEADER = 2;
    private static final int STATE_READING_ID3_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 3;
    private static final String TAG = "AdtsReader";
    private final ParsableBitArray adtsScratch;
    private int bytesRead;
    private TrackOutput currentOutput;
    private long currentSampleDuration;
    private boolean hasCrc;
    private boolean hasOutputFormat;
    private final ParsableByteArray id3HeaderBuffer;
    private final TrackOutput id3Output;
    private int matchState;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    static {
        ID3_IDENTIFIER = new byte[]{(byte) 73, (byte) 68, (byte) 51};
    }

    public AdtsReader(TrackOutput output, TrackOutput id3Output) {
        super(output);
        this.id3Output = id3Output;
        id3Output.format(MediaFormat.createId3Format());
        this.adtsScratch = new ParsableBitArray(new byte[7]);
        this.id3HeaderBuffer = new ParsableByteArray(Arrays.copyOf(ID3_IDENTIFIER, ID3_HEADER_SIZE));
        setFindingSampleState();
    }

    public void seek() {
        setFindingSampleState();
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case STATE_FINDING_SAMPLE /*0*/:
                    findNextSample(data);
                    break;
                case STATE_READING_ID3_HEADER /*1*/:
                    if (!continueRead(data, this.id3HeaderBuffer.data, ID3_HEADER_SIZE)) {
                        break;
                    }
                    parseId3Header();
                    break;
                case STATE_READING_ADTS_HEADER /*2*/:
                    if (!continueRead(data, this.adtsScratch.data, this.hasCrc ? 7 : HEADER_SIZE)) {
                        break;
                    }
                    parseAdtsHeader();
                    break;
                case STATE_READING_SAMPLE /*3*/:
                    readSample(data);
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

    private void setFindingSampleState() {
        this.state = STATE_FINDING_SAMPLE;
        this.bytesRead = STATE_FINDING_SAMPLE;
        this.matchState = MATCH_STATE_START;
    }

    private void setReadingId3HeaderState() {
        this.state = STATE_READING_ID3_HEADER;
        this.bytesRead = ID3_IDENTIFIER.length;
        this.sampleSize = STATE_FINDING_SAMPLE;
        this.id3HeaderBuffer.setPosition(STATE_FINDING_SAMPLE);
    }

    private void setReadingSampleState(TrackOutput outputToUse, long currentSampleDuration, int priorReadBytes, int sampleSize) {
        this.state = STATE_READING_SAMPLE;
        this.bytesRead = priorReadBytes;
        this.currentOutput = outputToUse;
        this.currentSampleDuration = currentSampleDuration;
        this.sampleSize = sampleSize;
    }

    private void setReadingAdtsHeaderState() {
        this.state = STATE_READING_ADTS_HEADER;
        this.bytesRead = STATE_FINDING_SAMPLE;
    }

    private void findNextSample(ParsableByteArray pesBuffer) {
        byte[] adtsData = pesBuffer.data;
        int position = pesBuffer.getPosition();
        int endOffset = pesBuffer.limit();
        int position2 = position;
        while (position2 < endOffset) {
            position = position2 + STATE_READING_ID3_HEADER;
            int data = adtsData[position2] & NalUnitUtil.EXTENDED_SAR;
            if (this.matchState != MATCH_STATE_FF || data < PsExtractor.VIDEO_STREAM_MASK || data == NalUnitUtil.EXTENDED_SAR) {
                switch (this.matchState | data) {
                    case 329:
                        this.matchState = MATCH_STATE_I;
                        break;
                    case 511:
                        this.matchState = MATCH_STATE_FF;
                        break;
                    case 836:
                        this.matchState = MATCH_STATE_ID;
                        break;
                    case 1075:
                        setReadingId3HeaderState();
                        pesBuffer.setPosition(position);
                        return;
                    default:
                        if (this.matchState == MATCH_STATE_START) {
                            break;
                        }
                        this.matchState = MATCH_STATE_START;
                        position--;
                        break;
                }
                position2 = position;
            } else {
                this.hasCrc = (data & STATE_READING_ID3_HEADER) == 0;
                setReadingAdtsHeaderState();
                pesBuffer.setPosition(position);
                return;
            }
        }
        pesBuffer.setPosition(position2);
        position = position2;
    }

    private void parseId3Header() {
        this.id3Output.sampleData(this.id3HeaderBuffer, ID3_HEADER_SIZE);
        this.id3HeaderBuffer.setPosition(ID3_SIZE_OFFSET);
        setReadingSampleState(this.id3Output, 0, ID3_HEADER_SIZE, this.id3HeaderBuffer.readSynchSafeInt() + ID3_HEADER_SIZE);
    }

    private void parseAdtsHeader() {
        this.adtsScratch.setPosition(STATE_FINDING_SAMPLE);
        if (this.hasOutputFormat) {
            this.adtsScratch.skipBits(ID3_HEADER_SIZE);
        } else {
            int audioObjectType = this.adtsScratch.readBits(STATE_READING_ADTS_HEADER) + STATE_READING_ID3_HEADER;
            if (audioObjectType == STATE_READING_ID3_HEADER) {
                Log.w(TAG, "Detected AAC Main audio, but assuming AAC LC.");
                audioObjectType = STATE_READING_ADTS_HEADER;
            }
            int sampleRateIndex = this.adtsScratch.readBits(4);
            this.adtsScratch.skipBits(STATE_READING_ID3_HEADER);
            byte[] audioSpecificConfig = CodecSpecificDataUtil.buildAacAudioSpecificConfig(audioObjectType, sampleRateIndex, this.adtsScratch.readBits(STATE_READING_SAMPLE));
            Pair<Integer, Integer> audioParams = CodecSpecificDataUtil.parseAacAudioSpecificConfig(audioSpecificConfig);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_AAC, -1, -1, -1, ((Integer) audioParams.second).intValue(), ((Integer) audioParams.first).intValue(), Collections.singletonList(audioSpecificConfig), null);
            this.sampleDurationUs = 1024000000 / ((long) mediaFormat.sampleRate);
            this.output.format(mediaFormat);
            this.hasOutputFormat = true;
        }
        this.adtsScratch.skipBits(4);
        int sampleSize = (this.adtsScratch.readBits(13) - 2) - 5;
        if (this.hasCrc) {
            sampleSize -= 2;
        }
        setReadingSampleState(this.output, this.sampleDurationUs, STATE_FINDING_SAMPLE, sampleSize);
    }

    private void readSample(ParsableByteArray data) {
        int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
        this.currentOutput.sampleData(data, bytesToRead);
        this.bytesRead += bytesToRead;
        if (this.bytesRead == this.sampleSize) {
            this.currentOutput.sampleMetadata(this.timeUs, STATE_READING_ID3_HEADER, this.sampleSize, STATE_FINDING_SAMPLE, null);
            this.timeUs += this.currentSampleDuration;
            setFindingSampleState();
        }
    }
}
