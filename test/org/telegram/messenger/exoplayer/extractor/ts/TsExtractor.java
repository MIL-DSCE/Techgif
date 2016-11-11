package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.DummyTrackOutput;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class TsExtractor implements Extractor {
    private static final long AC3_FORMAT_IDENTIFIER;
    private static final long E_AC3_FORMAT_IDENTIFIER;
    private static final long HEVC_FORMAT_IDENTIFIER;
    private static final String TAG = "TsExtractor";
    private static final int TS_PACKET_SIZE = 188;
    private static final int TS_PAT_PID = 0;
    private static final int TS_STREAM_TYPE_AAC = 15;
    private static final int TS_STREAM_TYPE_AC3 = 129;
    private static final int TS_STREAM_TYPE_DTS = 138;
    private static final int TS_STREAM_TYPE_EIA608 = 256;
    private static final int TS_STREAM_TYPE_E_AC3 = 135;
    private static final int TS_STREAM_TYPE_H262 = 2;
    private static final int TS_STREAM_TYPE_H264 = 27;
    private static final int TS_STREAM_TYPE_H265 = 36;
    private static final int TS_STREAM_TYPE_HDMV_DTS = 130;
    private static final int TS_STREAM_TYPE_ID3 = 21;
    private static final int TS_STREAM_TYPE_MPA = 3;
    private static final int TS_STREAM_TYPE_MPA_LSF = 4;
    private static final int TS_SYNC_BYTE = 71;
    public static final int WORKAROUND_ALLOW_NON_IDR_KEYFRAMES = 1;
    public static final int WORKAROUND_DETECT_ACCESS_UNITS = 8;
    public static final int WORKAROUND_IGNORE_AAC_STREAM = 2;
    public static final int WORKAROUND_IGNORE_H264_STREAM = 4;
    Id3Reader id3Reader;
    private ExtractorOutput output;
    private final PtsTimestampAdjuster ptsTimestampAdjuster;
    final SparseBooleanArray streamTypes;
    private final ParsableByteArray tsPacketBuffer;
    final SparseArray<TsPayloadReader> tsPayloadReaders;
    private final ParsableBitArray tsScratch;
    private final int workaroundFlags;

    private static abstract class TsPayloadReader {
        public abstract void consume(ParsableByteArray parsableByteArray, boolean z, ExtractorOutput extractorOutput);

        public abstract void seek();

        private TsPayloadReader() {
        }
    }

    private class PatReader extends TsPayloadReader {
        private final ParsableBitArray patScratch;

        public PatReader() {
            super();
            this.patScratch = new ParsableBitArray(new byte[TsExtractor.WORKAROUND_IGNORE_H264_STREAM]);
        }

        public void seek() {
        }

        public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator, ExtractorOutput output) {
            if (payloadUnitStartIndicator) {
                data.skipBytes(data.readUnsignedByte());
            }
            data.readBytes(this.patScratch, (int) TsExtractor.TS_STREAM_TYPE_MPA);
            this.patScratch.skipBits(12);
            int sectionLength = this.patScratch.readBits(12);
            data.skipBytes(5);
            int programCount = (sectionLength - 9) / TsExtractor.WORKAROUND_IGNORE_H264_STREAM;
            for (int i = TsExtractor.TS_PAT_PID; i < programCount; i += TsExtractor.WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) {
                data.readBytes(this.patScratch, (int) TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                int programNumber = this.patScratch.readBits(16);
                this.patScratch.skipBits(TsExtractor.TS_STREAM_TYPE_MPA);
                if (programNumber == 0) {
                    this.patScratch.skipBits(13);
                } else {
                    TsExtractor.this.tsPayloadReaders.put(this.patScratch.readBits(13), new PmtReader());
                }
            }
        }
    }

    private static final class PesReader extends TsPayloadReader {
        private static final int HEADER_SIZE = 9;
        private static final int MAX_HEADER_EXTENSION_SIZE = 10;
        private static final int PES_SCRATCH_SIZE = 10;
        private static final int STATE_FINDING_HEADER = 0;
        private static final int STATE_READING_BODY = 3;
        private static final int STATE_READING_HEADER = 1;
        private static final int STATE_READING_HEADER_EXTENSION = 2;
        private int bytesRead;
        private boolean dataAlignmentIndicator;
        private boolean dtsFlag;
        private int extendedHeaderLength;
        private int payloadSize;
        private final ElementaryStreamReader pesPayloadReader;
        private final ParsableBitArray pesScratch;
        private boolean ptsFlag;
        private final PtsTimestampAdjuster ptsTimestampAdjuster;
        private boolean seenFirstDts;
        private int state;
        private long timeUs;

        public PesReader(ElementaryStreamReader pesPayloadReader, PtsTimestampAdjuster ptsTimestampAdjuster) {
            super();
            this.pesPayloadReader = pesPayloadReader;
            this.ptsTimestampAdjuster = ptsTimestampAdjuster;
            this.pesScratch = new ParsableBitArray(new byte[PES_SCRATCH_SIZE]);
            this.state = STATE_FINDING_HEADER;
        }

        public void seek() {
            this.state = STATE_FINDING_HEADER;
            this.bytesRead = STATE_FINDING_HEADER;
            this.seenFirstDts = false;
            this.pesPayloadReader.seek();
        }

        public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator, ExtractorOutput output) {
            if (payloadUnitStartIndicator) {
                switch (this.state) {
                    case STATE_READING_HEADER_EXTENSION /*2*/:
                        Log.w(TsExtractor.TAG, "Unexpected start indicator reading extended header");
                        break;
                    case STATE_READING_BODY /*3*/:
                        if (this.payloadSize != -1) {
                            Log.w(TsExtractor.TAG, "Unexpected start indicator: expected " + this.payloadSize + " more bytes");
                        }
                        this.pesPayloadReader.packetFinished();
                        break;
                }
                setState(STATE_READING_HEADER);
            }
            while (data.bytesLeft() > 0) {
                switch (this.state) {
                    case STATE_FINDING_HEADER /*0*/:
                        data.skipBytes(data.bytesLeft());
                        break;
                    case STATE_READING_HEADER /*1*/:
                        if (!continueRead(data, this.pesScratch.data, HEADER_SIZE)) {
                            break;
                        }
                        setState(parseHeader() ? STATE_READING_HEADER_EXTENSION : STATE_FINDING_HEADER);
                        break;
                    case STATE_READING_HEADER_EXTENSION /*2*/:
                        if (continueRead(data, this.pesScratch.data, Math.min(PES_SCRATCH_SIZE, this.extendedHeaderLength)) && continueRead(data, null, this.extendedHeaderLength)) {
                            parseHeaderExtension();
                            this.pesPayloadReader.packetStarted(this.timeUs, this.dataAlignmentIndicator);
                            setState(STATE_READING_BODY);
                            break;
                        }
                    case STATE_READING_BODY /*3*/:
                        int padding;
                        int readLength = data.bytesLeft();
                        if (this.payloadSize == -1) {
                            padding = STATE_FINDING_HEADER;
                        } else {
                            padding = readLength - this.payloadSize;
                        }
                        if (padding > 0) {
                            readLength -= padding;
                            data.setLimit(data.getPosition() + readLength);
                        }
                        this.pesPayloadReader.consume(data);
                        if (this.payloadSize == -1) {
                            break;
                        }
                        this.payloadSize -= readLength;
                        if (this.payloadSize != 0) {
                            break;
                        }
                        this.pesPayloadReader.packetFinished();
                        setState(STATE_READING_HEADER);
                        break;
                    default:
                        break;
                }
            }
        }

        private void setState(int state) {
            this.state = state;
            this.bytesRead = STATE_FINDING_HEADER;
        }

        private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
            int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
            if (bytesToRead <= 0) {
                return true;
            }
            if (target == null) {
                source.skipBytes(bytesToRead);
            } else {
                source.readBytes(target, this.bytesRead, bytesToRead);
            }
            this.bytesRead += bytesToRead;
            if (this.bytesRead != targetLength) {
                return false;
            }
            return true;
        }

        private boolean parseHeader() {
            this.pesScratch.setPosition(STATE_FINDING_HEADER);
            int startCodePrefix = this.pesScratch.readBits(24);
            if (startCodePrefix != STATE_READING_HEADER) {
                Log.w(TsExtractor.TAG, "Unexpected start code prefix: " + startCodePrefix);
                this.payloadSize = -1;
                return false;
            }
            this.pesScratch.skipBits(TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS);
            int packetLength = this.pesScratch.readBits(16);
            this.pesScratch.skipBits(5);
            this.dataAlignmentIndicator = this.pesScratch.readBit();
            this.pesScratch.skipBits(STATE_READING_HEADER_EXTENSION);
            this.ptsFlag = this.pesScratch.readBit();
            this.dtsFlag = this.pesScratch.readBit();
            this.pesScratch.skipBits(6);
            this.extendedHeaderLength = this.pesScratch.readBits(TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS);
            if (packetLength == 0) {
                this.payloadSize = -1;
            } else {
                this.payloadSize = ((packetLength + 6) - 9) - this.extendedHeaderLength;
            }
            return true;
        }

        private void parseHeaderExtension() {
            this.pesScratch.setPosition(STATE_FINDING_HEADER);
            this.timeUs = -1;
            if (this.ptsFlag) {
                this.pesScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                long pts = ((long) this.pesScratch.readBits(STATE_READING_BODY)) << 30;
                this.pesScratch.skipBits(STATE_READING_HEADER);
                pts |= (long) (this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC) << TsExtractor.TS_STREAM_TYPE_AAC);
                this.pesScratch.skipBits(STATE_READING_HEADER);
                pts |= (long) this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC);
                this.pesScratch.skipBits(STATE_READING_HEADER);
                if (!this.seenFirstDts && this.dtsFlag) {
                    this.pesScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                    long dts = ((long) this.pesScratch.readBits(STATE_READING_BODY)) << 30;
                    this.pesScratch.skipBits(STATE_READING_HEADER);
                    dts |= (long) (this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC) << TsExtractor.TS_STREAM_TYPE_AAC);
                    this.pesScratch.skipBits(STATE_READING_HEADER);
                    dts |= (long) this.pesScratch.readBits(TsExtractor.TS_STREAM_TYPE_AAC);
                    this.pesScratch.skipBits(STATE_READING_HEADER);
                    this.ptsTimestampAdjuster.adjustTimestamp(dts);
                    this.seenFirstDts = true;
                }
                this.timeUs = this.ptsTimestampAdjuster.adjustTimestamp(pts);
            }
        }
    }

    private class PmtReader extends TsPayloadReader {
        private final ParsableBitArray pmtScratch;
        private int sectionBytesRead;
        private final ParsableByteArray sectionData;
        private int sectionLength;

        public PmtReader() {
            super();
            this.pmtScratch = new ParsableBitArray(new byte[5]);
            this.sectionData = new ParsableByteArray();
        }

        public void seek() {
        }

        public void consume(ParsableByteArray data, boolean payloadUnitStartIndicator, ExtractorOutput output) {
            if (payloadUnitStartIndicator) {
                data.skipBytes(data.readUnsignedByte());
                data.readBytes(this.pmtScratch, (int) TsExtractor.TS_STREAM_TYPE_MPA);
                this.pmtScratch.skipBits(12);
                this.sectionLength = this.pmtScratch.readBits(12);
                if (this.sectionData.capacity() < this.sectionLength) {
                    this.sectionData.reset(new byte[this.sectionLength], this.sectionLength);
                } else {
                    this.sectionData.reset();
                    this.sectionData.setLimit(this.sectionLength);
                }
            }
            int bytesToRead = Math.min(data.bytesLeft(), this.sectionLength - this.sectionBytesRead);
            data.readBytes(this.sectionData.data, this.sectionBytesRead, bytesToRead);
            this.sectionBytesRead += bytesToRead;
            if (this.sectionBytesRead >= this.sectionLength) {
                this.sectionData.skipBytes(7);
                this.sectionData.readBytes(this.pmtScratch, (int) TsExtractor.WORKAROUND_IGNORE_AAC_STREAM);
                this.pmtScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                int programInfoLength = this.pmtScratch.readBits(12);
                this.sectionData.skipBytes(programInfoLength);
                if (TsExtractor.this.id3Reader == null) {
                    TsExtractor.this.id3Reader = new Id3Reader(output.track(TsExtractor.TS_STREAM_TYPE_ID3));
                }
                int remainingEntriesLength = ((this.sectionLength - 9) - programInfoLength) - 4;
                while (remainingEntriesLength > 0) {
                    this.sectionData.readBytes(this.pmtScratch, 5);
                    int streamType = this.pmtScratch.readBits(TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS);
                    this.pmtScratch.skipBits(TsExtractor.TS_STREAM_TYPE_MPA);
                    int elementaryPid = this.pmtScratch.readBits(13);
                    this.pmtScratch.skipBits(TsExtractor.WORKAROUND_IGNORE_H264_STREAM);
                    int esInfoLength = this.pmtScratch.readBits(12);
                    if (streamType == 6) {
                        streamType = readPrivateDataStreamType(this.sectionData, esInfoLength);
                    } else {
                        this.sectionData.skipBytes(esInfoLength);
                    }
                    remainingEntriesLength -= esInfoLength + 5;
                    if (!TsExtractor.this.streamTypes.get(streamType)) {
                        ElementaryStreamReader pesPayloadReader;
                        switch (streamType) {
                            case TsExtractor.WORKAROUND_IGNORE_AAC_STREAM /*2*/:
                                pesPayloadReader = new H262Reader(output.track(TsExtractor.WORKAROUND_IGNORE_AAC_STREAM));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_MPA /*3*/:
                                pesPayloadReader = new MpegAudioReader(output.track(TsExtractor.TS_STREAM_TYPE_MPA));
                                break;
                            case TsExtractor.WORKAROUND_IGNORE_H264_STREAM /*4*/:
                                pesPayloadReader = new MpegAudioReader(output.track(TsExtractor.WORKAROUND_IGNORE_H264_STREAM));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_AAC /*15*/:
                                pesPayloadReader = (TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_IGNORE_AAC_STREAM) != 0 ? null : new AdtsReader(output.track(TsExtractor.TS_STREAM_TYPE_AAC), new DummyTrackOutput());
                                break;
                            case TsExtractor.TS_STREAM_TYPE_ID3 /*21*/:
                                pesPayloadReader = TsExtractor.this.id3Reader;
                                break;
                            case TsExtractor.TS_STREAM_TYPE_H264 /*27*/:
                                if ((TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_IGNORE_H264_STREAM) != 0) {
                                    pesPayloadReader = null;
                                } else {
                                    pesPayloadReader = new H264Reader(output.track(TsExtractor.TS_STREAM_TYPE_H264), new SeiReader(output.track(TsExtractor.TS_STREAM_TYPE_EIA608)), (TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) != 0, (TsExtractor.this.workaroundFlags & TsExtractor.WORKAROUND_DETECT_ACCESS_UNITS) != 0);
                                }
                                break;
                            case TsExtractor.TS_STREAM_TYPE_H265 /*36*/:
                                pesPayloadReader = new H265Reader(output.track(TsExtractor.TS_STREAM_TYPE_H265), new SeiReader(output.track(TsExtractor.TS_STREAM_TYPE_EIA608)));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
                                pesPayloadReader = new Ac3Reader(output.track(TsExtractor.TS_STREAM_TYPE_AC3), false);
                                break;
                            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                            case TsExtractor.TS_STREAM_TYPE_DTS /*138*/:
                                pesPayloadReader = new DtsReader(output.track(TsExtractor.TS_STREAM_TYPE_DTS));
                                break;
                            case TsExtractor.TS_STREAM_TYPE_E_AC3 /*135*/:
                                pesPayloadReader = new Ac3Reader(output.track(TsExtractor.TS_STREAM_TYPE_E_AC3), true);
                                break;
                            default:
                                pesPayloadReader = null;
                                break;
                        }
                        if (pesPayloadReader != null) {
                            TsExtractor.this.streamTypes.put(streamType, true);
                            TsExtractor.this.tsPayloadReaders.put(elementaryPid, new PesReader(pesPayloadReader, TsExtractor.this.ptsTimestampAdjuster));
                        }
                    }
                }
                output.endTracks();
            }
        }

        private int readPrivateDataStreamType(ParsableByteArray data, int length) {
            int streamType = -1;
            int descriptorsEndPosition = data.getPosition() + length;
            while (data.getPosition() < descriptorsEndPosition) {
                int descriptorTag = data.readUnsignedByte();
                int descriptorLength = data.readUnsignedByte();
                if (descriptorTag == 5) {
                    long formatIdentifier = data.readUnsignedInt();
                    if (formatIdentifier == TsExtractor.AC3_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_AC3;
                    } else if (formatIdentifier == TsExtractor.E_AC3_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    } else if (formatIdentifier == TsExtractor.HEVC_FORMAT_IDENTIFIER) {
                        streamType = TsExtractor.TS_STREAM_TYPE_H265;
                    }
                    data.setPosition(descriptorsEndPosition);
                    return streamType;
                }
                if (descriptorTag == 106) {
                    streamType = TsExtractor.TS_STREAM_TYPE_AC3;
                } else if (descriptorTag == 122) {
                    streamType = TsExtractor.TS_STREAM_TYPE_E_AC3;
                } else if (descriptorTag == 123) {
                    streamType = TsExtractor.TS_STREAM_TYPE_DTS;
                }
                data.skipBytes(descriptorLength);
            }
            data.setPosition(descriptorsEndPosition);
            return streamType;
        }
    }

    static {
        AC3_FORMAT_IDENTIFIER = (long) Util.getIntegerCodeForString("AC-3");
        E_AC3_FORMAT_IDENTIFIER = (long) Util.getIntegerCodeForString("EAC3");
        HEVC_FORMAT_IDENTIFIER = (long) Util.getIntegerCodeForString("HEVC");
    }

    public TsExtractor() {
        this(new PtsTimestampAdjuster(HEVC_FORMAT_IDENTIFIER));
    }

    public TsExtractor(PtsTimestampAdjuster ptsTimestampAdjuster) {
        this(ptsTimestampAdjuster, TS_PAT_PID);
    }

    public TsExtractor(PtsTimestampAdjuster ptsTimestampAdjuster, int workaroundFlags) {
        this.ptsTimestampAdjuster = ptsTimestampAdjuster;
        this.workaroundFlags = workaroundFlags;
        this.tsPacketBuffer = new ParsableByteArray((int) TS_PACKET_SIZE);
        this.tsScratch = new ParsableBitArray(new byte[TS_STREAM_TYPE_MPA]);
        this.tsPayloadReaders = new SparseArray();
        this.tsPayloadReaders.put(TS_PAT_PID, new PatReader());
        this.streamTypes = new SparseBooleanArray();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        byte[] scratch = new byte[WORKAROUND_ALLOW_NON_IDR_KEYFRAMES];
        for (int i = TS_PAT_PID; i < 5; i += WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) {
            input.peekFully(scratch, TS_PAT_PID, WORKAROUND_ALLOW_NON_IDR_KEYFRAMES);
            if ((scratch[TS_PAT_PID] & NalUnitUtil.EXTENDED_SAR) != TS_SYNC_BYTE) {
                return false;
            }
            input.advancePeekPosition(187);
        }
        return true;
    }

    public void init(ExtractorOutput output) {
        this.output = output;
        output.seekMap(SeekMap.UNSEEKABLE);
    }

    public void seek() {
        this.ptsTimestampAdjuster.reset();
        for (int i = TS_PAT_PID; i < this.tsPayloadReaders.size(); i += WORKAROUND_ALLOW_NON_IDR_KEYFRAMES) {
            ((TsPayloadReader) this.tsPayloadReaders.valueAt(i)).seek();
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (!input.readFully(this.tsPacketBuffer.data, TS_PAT_PID, TS_PACKET_SIZE, true)) {
            return -1;
        }
        this.tsPacketBuffer.setPosition(TS_PAT_PID);
        this.tsPacketBuffer.setLimit(TS_PACKET_SIZE);
        if (this.tsPacketBuffer.readUnsignedByte() != TS_SYNC_BYTE) {
            return TS_PAT_PID;
        }
        this.tsPacketBuffer.readBytes(this.tsScratch, (int) TS_STREAM_TYPE_MPA);
        this.tsScratch.skipBits(WORKAROUND_ALLOW_NON_IDR_KEYFRAMES);
        boolean payloadUnitStartIndicator = this.tsScratch.readBit();
        this.tsScratch.skipBits(WORKAROUND_ALLOW_NON_IDR_KEYFRAMES);
        int pid = this.tsScratch.readBits(13);
        this.tsScratch.skipBits(WORKAROUND_IGNORE_AAC_STREAM);
        boolean adaptationFieldExists = this.tsScratch.readBit();
        boolean payloadExists = this.tsScratch.readBit();
        if (adaptationFieldExists) {
            this.tsPacketBuffer.skipBytes(this.tsPacketBuffer.readUnsignedByte());
        }
        if (!payloadExists) {
            return TS_PAT_PID;
        }
        TsPayloadReader payloadReader = (TsPayloadReader) this.tsPayloadReaders.get(pid);
        if (payloadReader == null) {
            return TS_PAT_PID;
        }
        payloadReader.consume(this.tsPacketBuffer, payloadUnitStartIndicator, this.output);
        return TS_PAT_PID;
    }
}
