package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.PpsData;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class H264Reader extends ElementaryStreamReader {
    private static final int NAL_UNIT_TYPE_PPS = 8;
    private static final int NAL_UNIT_TYPE_SEI = 6;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private boolean hasOutputFormat;
    private long pesTimeUs;
    private final NalUnitTargetBuffer pps;
    private final boolean[] prefixFlags;
    private final SampleReader sampleReader;
    private final NalUnitTargetBuffer sei;
    private final SeiReader seiReader;
    private final ParsableByteArray seiWrapper;
    private final NalUnitTargetBuffer sps;
    private long totalBytesWritten;

    private static final class SampleReader {
        private static final int DEFAULT_BUFFER_SIZE = 128;
        private static final int NAL_UNIT_TYPE_AUD = 9;
        private static final int NAL_UNIT_TYPE_IDR = 5;
        private static final int NAL_UNIT_TYPE_NON_IDR = 1;
        private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
        private final boolean allowNonIdrKeyframes;
        private byte[] buffer;
        private int bufferLength;
        private final boolean detectAccessUnits;
        private boolean isFilling;
        private long nalUnitStartPosition;
        private long nalUnitTimeUs;
        private int nalUnitType;
        private final TrackOutput output;
        private final SparseArray<PpsData> pps;
        private SliceHeaderData previousSliceHeader;
        private boolean readingSample;
        private boolean sampleIsKeyframe;
        private long samplePosition;
        private long sampleTimeUs;
        private final ParsableBitArray scratch;
        private SliceHeaderData sliceHeader;
        private final SparseArray<SpsData> sps;

        private static final class SliceHeaderData {
            private static final int SLICE_TYPE_ALL_I = 7;
            private static final int SLICE_TYPE_I = 2;
            private boolean bottomFieldFlag;
            private boolean bottomFieldFlagPresent;
            private int deltaPicOrderCnt0;
            private int deltaPicOrderCnt1;
            private int deltaPicOrderCntBottom;
            private boolean fieldPicFlag;
            private int frameNum;
            private boolean hasSliceType;
            private boolean idrPicFlag;
            private int idrPicId;
            private boolean isComplete;
            private int nalRefIdc;
            private int picOrderCntLsb;
            private int picParameterSetId;
            private int sliceType;
            private SpsData spsData;

            private SliceHeaderData() {
            }

            public void clear() {
                this.hasSliceType = false;
                this.isComplete = false;
            }

            public void setSliceType(int sliceType) {
                this.sliceType = sliceType;
                this.hasSliceType = true;
            }

            public void setAll(SpsData spsData, int nalRefIdc, int sliceType, int frameNum, int picParameterSetId, boolean fieldPicFlag, boolean bottomFieldFlagPresent, boolean bottomFieldFlag, boolean idrPicFlag, int idrPicId, int picOrderCntLsb, int deltaPicOrderCntBottom, int deltaPicOrderCnt0, int deltaPicOrderCnt1) {
                this.spsData = spsData;
                this.nalRefIdc = nalRefIdc;
                this.sliceType = sliceType;
                this.frameNum = frameNum;
                this.picParameterSetId = picParameterSetId;
                this.fieldPicFlag = fieldPicFlag;
                this.bottomFieldFlagPresent = bottomFieldFlagPresent;
                this.bottomFieldFlag = bottomFieldFlag;
                this.idrPicFlag = idrPicFlag;
                this.idrPicId = idrPicId;
                this.picOrderCntLsb = picOrderCntLsb;
                this.deltaPicOrderCntBottom = deltaPicOrderCntBottom;
                this.deltaPicOrderCnt0 = deltaPicOrderCnt0;
                this.deltaPicOrderCnt1 = deltaPicOrderCnt1;
                this.isComplete = true;
                this.hasSliceType = true;
            }

            public boolean isISlice() {
                return this.hasSliceType && (this.sliceType == SLICE_TYPE_ALL_I || this.sliceType == SLICE_TYPE_I);
            }

            private boolean isFirstVclNalUnitOfPicture(SliceHeaderData other) {
                if (this.isComplete) {
                    if (!other.isComplete || this.frameNum != other.frameNum || this.picParameterSetId != other.picParameterSetId || this.fieldPicFlag != other.fieldPicFlag) {
                        return true;
                    }
                    if (this.bottomFieldFlagPresent && other.bottomFieldFlagPresent && this.bottomFieldFlag != other.bottomFieldFlag) {
                        return true;
                    }
                    if (this.nalRefIdc != other.nalRefIdc && (this.nalRefIdc == 0 || other.nalRefIdc == 0)) {
                        return true;
                    }
                    if (this.spsData.picOrderCountType == 0 && other.spsData.picOrderCountType == 0 && (this.picOrderCntLsb != other.picOrderCntLsb || this.deltaPicOrderCntBottom != other.deltaPicOrderCntBottom)) {
                        return true;
                    }
                    if ((this.spsData.picOrderCountType == SampleReader.NAL_UNIT_TYPE_NON_IDR && other.spsData.picOrderCountType == SampleReader.NAL_UNIT_TYPE_NON_IDR && (this.deltaPicOrderCnt0 != other.deltaPicOrderCnt0 || this.deltaPicOrderCnt1 != other.deltaPicOrderCnt1)) || this.idrPicFlag != other.idrPicFlag) {
                        return true;
                    }
                    if (this.idrPicFlag && other.idrPicFlag && this.idrPicId != other.idrPicId) {
                        return true;
                    }
                }
                return false;
            }
        }

        public SampleReader(TrackOutput output, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
            this.output = output;
            this.allowNonIdrKeyframes = allowNonIdrKeyframes;
            this.detectAccessUnits = detectAccessUnits;
            this.sps = new SparseArray();
            this.pps = new SparseArray();
            this.previousSliceHeader = new SliceHeaderData();
            this.sliceHeader = new SliceHeaderData();
            this.scratch = new ParsableBitArray();
            this.buffer = new byte[DEFAULT_BUFFER_SIZE];
            reset();
        }

        public boolean needsSpsPps() {
            return this.detectAccessUnits;
        }

        public void putSps(SpsData spsData) {
            this.sps.append(spsData.seqParameterSetId, spsData);
        }

        public void putPps(PpsData ppsData) {
            this.pps.append(ppsData.picParameterSetId, ppsData);
        }

        public void reset() {
            this.isFilling = false;
            this.readingSample = false;
            this.sliceHeader.clear();
        }

        public void startNalUnit(long position, int type, long pesTimeUs) {
            this.nalUnitType = type;
            this.nalUnitTimeUs = pesTimeUs;
            this.nalUnitStartPosition = position;
            if (!(this.allowNonIdrKeyframes && this.nalUnitType == NAL_UNIT_TYPE_NON_IDR)) {
                if (!this.detectAccessUnits) {
                    return;
                }
                if (!(this.nalUnitType == NAL_UNIT_TYPE_IDR || this.nalUnitType == NAL_UNIT_TYPE_NON_IDR || this.nalUnitType == NAL_UNIT_TYPE_PARTITION_A)) {
                    return;
                }
            }
            SliceHeaderData newSliceHeader = this.previousSliceHeader;
            this.previousSliceHeader = this.sliceHeader;
            this.sliceHeader = newSliceHeader;
            this.sliceHeader.clear();
            this.bufferLength = 0;
            this.isFilling = true;
        }

        public void appendToNalUnit(byte[] data, int offset, int limit) {
            if (this.isFilling) {
                int readLength = limit - offset;
                if (this.buffer.length < this.bufferLength + readLength) {
                    this.buffer = Arrays.copyOf(this.buffer, (this.bufferLength + readLength) * NAL_UNIT_TYPE_PARTITION_A);
                }
                System.arraycopy(data, offset, this.buffer, this.bufferLength, readLength);
                this.bufferLength += readLength;
                this.scratch.reset(this.buffer, this.bufferLength);
                if (this.scratch.bitsLeft() >= H264Reader.NAL_UNIT_TYPE_PPS) {
                    this.scratch.skipBits(NAL_UNIT_TYPE_NON_IDR);
                    int nalRefIdc = this.scratch.readBits(NAL_UNIT_TYPE_PARTITION_A);
                    this.scratch.skipBits(NAL_UNIT_TYPE_IDR);
                    if (this.scratch.canReadExpGolombCodedNum()) {
                        this.scratch.readUnsignedExpGolombCodedInt();
                        if (this.scratch.canReadExpGolombCodedNum()) {
                            int sliceType = this.scratch.readUnsignedExpGolombCodedInt();
                            if (!this.detectAccessUnits) {
                                this.isFilling = false;
                                this.sliceHeader.setSliceType(sliceType);
                            } else if (this.scratch.canReadExpGolombCodedNum()) {
                                int picParameterSetId = this.scratch.readUnsignedExpGolombCodedInt();
                                if (this.pps.indexOfKey(picParameterSetId) < 0) {
                                    this.isFilling = false;
                                    return;
                                }
                                PpsData ppsData = (PpsData) this.pps.get(picParameterSetId);
                                SpsData spsData = (SpsData) this.sps.get(ppsData.seqParameterSetId);
                                if (spsData.separateColorPlaneFlag) {
                                    if (this.scratch.bitsLeft() >= NAL_UNIT_TYPE_PARTITION_A) {
                                        this.scratch.skipBits(NAL_UNIT_TYPE_PARTITION_A);
                                    } else {
                                        return;
                                    }
                                }
                                int bitsLeft = this.scratch.bitsLeft();
                                int i = spsData.frameNumLength;
                                if (bitsLeft >= r0) {
                                    boolean fieldPicFlag = false;
                                    boolean bottomFieldFlagPresent = false;
                                    boolean bottomFieldFlag = false;
                                    int frameNum = this.scratch.readBits(spsData.frameNumLength);
                                    if (!spsData.frameMbsOnlyFlag) {
                                        if (this.scratch.bitsLeft() >= NAL_UNIT_TYPE_NON_IDR) {
                                            fieldPicFlag = this.scratch.readBit();
                                            if (fieldPicFlag) {
                                                if (this.scratch.bitsLeft() >= NAL_UNIT_TYPE_NON_IDR) {
                                                    bottomFieldFlag = this.scratch.readBit();
                                                    bottomFieldFlagPresent = true;
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    boolean idrPicFlag = this.nalUnitType == NAL_UNIT_TYPE_IDR;
                                    int idrPicId = 0;
                                    if (idrPicFlag) {
                                        if (this.scratch.canReadExpGolombCodedNum()) {
                                            idrPicId = this.scratch.readUnsignedExpGolombCodedInt();
                                        } else {
                                            return;
                                        }
                                    }
                                    int picOrderCntLsb = 0;
                                    int deltaPicOrderCntBottom = 0;
                                    int deltaPicOrderCnt0 = 0;
                                    int deltaPicOrderCnt1 = 0;
                                    if (spsData.picOrderCountType == 0) {
                                        bitsLeft = this.scratch.bitsLeft();
                                        i = spsData.picOrderCntLsbLength;
                                        if (bitsLeft >= r0) {
                                            picOrderCntLsb = this.scratch.readBits(spsData.picOrderCntLsbLength);
                                            if (ppsData.bottomFieldPicOrderInFramePresentFlag && !fieldPicFlag) {
                                                if (this.scratch.canReadExpGolombCodedNum()) {
                                                    deltaPicOrderCntBottom = this.scratch.readSignedExpGolombCodedInt();
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    } else if (spsData.picOrderCountType == NAL_UNIT_TYPE_NON_IDR && !spsData.deltaPicOrderAlwaysZeroFlag) {
                                        if (this.scratch.canReadExpGolombCodedNum()) {
                                            deltaPicOrderCnt0 = this.scratch.readSignedExpGolombCodedInt();
                                            if (ppsData.bottomFieldPicOrderInFramePresentFlag && !fieldPicFlag) {
                                                if (this.scratch.canReadExpGolombCodedNum()) {
                                                    deltaPicOrderCnt1 = this.scratch.readSignedExpGolombCodedInt();
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    this.sliceHeader.setAll(spsData, nalRefIdc, sliceType, frameNum, picParameterSetId, fieldPicFlag, bottomFieldFlagPresent, bottomFieldFlag, idrPicFlag, idrPicId, picOrderCntLsb, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                                    this.isFilling = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        public void endNalUnit(long position, int offset) {
            int i = 0;
            if (this.nalUnitType == NAL_UNIT_TYPE_AUD || (this.detectAccessUnits && this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))) {
                if (this.readingSample) {
                    outputSample(offset + ((int) (position - this.nalUnitStartPosition)));
                }
                this.samplePosition = this.nalUnitStartPosition;
                this.sampleTimeUs = this.nalUnitTimeUs;
                this.sampleIsKeyframe = false;
                this.readingSample = true;
            }
            boolean z = this.sampleIsKeyframe;
            if (this.nalUnitType == NAL_UNIT_TYPE_IDR || (this.allowNonIdrKeyframes && this.nalUnitType == NAL_UNIT_TYPE_NON_IDR && this.sliceHeader.isISlice())) {
                i = NAL_UNIT_TYPE_NON_IDR;
            }
            this.sampleIsKeyframe = i | z;
        }

        private void outputSample(int offset) {
            this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe ? NAL_UNIT_TYPE_NON_IDR : 0, (int) (this.nalUnitStartPosition - this.samplePosition), offset, null);
        }
    }

    public H264Reader(TrackOutput output, SeiReader seiReader, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
        super(output);
        this.seiReader = seiReader;
        this.prefixFlags = new boolean[3];
        this.sampleReader = new SampleReader(output, allowNonIdrKeyframes, detectAccessUnits);
        this.sps = new NalUnitTargetBuffer(NAL_UNIT_TYPE_SPS, MessagesController.UPDATE_MASK_USER_PHONE);
        this.pps = new NalUnitTargetBuffer(NAL_UNIT_TYPE_PPS, MessagesController.UPDATE_MASK_USER_PHONE);
        this.sei = new NalUnitTargetBuffer(NAL_UNIT_TYPE_SEI, MessagesController.UPDATE_MASK_USER_PHONE);
        this.seiWrapper = new ParsableByteArray();
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.sps.reset();
        this.pps.reset();
        this.sei.reset();
        this.sampleReader.reset();
        this.totalBytesWritten = 0;
    }

    public void packetStarted(long pesTimeUs, boolean dataAlignmentIndicator) {
        this.pesTimeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        if (data.bytesLeft() > 0) {
            int offset = data.getPosition();
            int limit = data.limit();
            byte[] dataArray = data.data;
            this.totalBytesWritten += (long) data.bytesLeft();
            this.output.sampleData(data, data.bytesLeft());
            while (true) {
                int nalUnitOffset = NalUnitUtil.findNalUnit(dataArray, offset, limit, this.prefixFlags);
                if (nalUnitOffset == limit) {
                    nalUnitData(dataArray, offset, limit);
                    return;
                }
                int nalUnitType = NalUnitUtil.getNalUnitType(dataArray, nalUnitOffset);
                int lengthToNalUnit = nalUnitOffset - offset;
                if (lengthToNalUnit > 0) {
                    nalUnitData(dataArray, offset, nalUnitOffset);
                }
                int bytesWrittenPastPosition = limit - nalUnitOffset;
                long absolutePosition = this.totalBytesWritten - ((long) bytesWrittenPastPosition);
                endNalUnit(absolutePosition, bytesWrittenPastPosition, lengthToNalUnit < 0 ? -lengthToNalUnit : 0, this.pesTimeUs);
                startNalUnit(absolutePosition, nalUnitType, this.pesTimeUs);
                offset = nalUnitOffset + 3;
            }
        }
    }

    public void packetFinished() {
    }

    private void startNalUnit(long position, int nalUnitType, long pesTimeUs) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.startNalUnit(nalUnitType);
            this.pps.startNalUnit(nalUnitType);
        }
        this.sei.startNalUnit(nalUnitType);
        this.sampleReader.startNalUnit(position, nalUnitType, pesTimeUs);
    }

    private void nalUnitData(byte[] dataArray, int offset, int limit) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.appendToNalUnit(dataArray, offset, limit);
            this.pps.appendToNalUnit(dataArray, offset, limit);
        }
        this.sei.appendToNalUnit(dataArray, offset, limit);
        this.sampleReader.appendToNalUnit(dataArray, offset, limit);
    }

    private void endNalUnit(long position, int offset, int discardPadding, long pesTimeUs) {
        if (!this.hasOutputFormat || this.sampleReader.needsSpsPps()) {
            this.sps.endNalUnit(discardPadding);
            this.pps.endNalUnit(discardPadding);
            if (this.hasOutputFormat) {
                if (this.sps.isCompleted()) {
                    this.sampleReader.putSps(NalUnitUtil.parseSpsNalUnit(unescape(this.sps)));
                    this.sps.reset();
                } else if (this.pps.isCompleted()) {
                    this.sampleReader.putPps(NalUnitUtil.parsePpsNalUnit(unescape(this.pps)));
                    this.pps.reset();
                }
            } else if (this.sps.isCompleted() && this.pps.isCompleted()) {
                List<byte[]> initializationData = new ArrayList();
                initializationData.add(Arrays.copyOf(this.sps.nalData, this.sps.nalLength));
                initializationData.add(Arrays.copyOf(this.pps.nalData, this.pps.nalLength));
                SpsData spsData = NalUnitUtil.parseSpsNalUnit(unescape(this.sps));
                PpsData ppsData = NalUnitUtil.parsePpsNalUnit(unescape(this.pps));
                TrackOutput trackOutput = this.output;
                r18.format(MediaFormat.createVideoFormat(null, MediaController.MIME_TYPE, -1, -1, -1, spsData.width, spsData.height, initializationData, -1, spsData.pixelWidthAspectRatio));
                this.hasOutputFormat = true;
                this.sampleReader.putSps(spsData);
                this.sampleReader.putPps(ppsData);
                this.sps.reset();
                this.pps.reset();
            }
        }
        if (this.sei.endNalUnit(discardPadding)) {
            this.seiWrapper.reset(this.sei.nalData, NalUnitUtil.unescapeStream(this.sei.nalData, this.sei.nalLength));
            this.seiWrapper.setPosition(4);
            this.seiReader.consume(pesTimeUs, this.seiWrapper);
        }
        this.sampleReader.endNalUnit(position, offset);
    }

    private static ParsableBitArray unescape(NalUnitTargetBuffer buffer) {
        ParsableBitArray bitArray = new ParsableBitArray(buffer.nalData, NalUnitUtil.unescapeStream(buffer.nalData, buffer.nalLength));
        bitArray.skipBits(32);
        return bitArray;
    }
}
