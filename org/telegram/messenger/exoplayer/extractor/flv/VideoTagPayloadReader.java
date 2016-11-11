package org.telegram.messenger.exoplayer.extractor.flv;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.extractor.flv.TagPayloadReader.UnsupportedFormatException;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

final class VideoTagPayloadReader extends TagPayloadReader {
    private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
    private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int VIDEO_CODEC_AVC = 7;
    private static final int VIDEO_FRAME_KEYFRAME = 1;
    private static final int VIDEO_FRAME_VIDEO_INFO = 5;
    private int frameType;
    private boolean hasOutputFormat;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int nalUnitLengthFieldLength;

    private static final class AvcSequenceHeaderData {
        public final int height;
        public final List<byte[]> initializationData;
        public final int nalUnitLengthFieldLength;
        public final float pixelWidthAspectRatio;
        public final int width;

        public AvcSequenceHeaderData(List<byte[]> initializationData, int nalUnitLengthFieldLength, int width, int height, float pixelWidthAspectRatio) {
            this.initializationData = initializationData;
            this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio;
            this.width = width;
            this.height = height;
        }
    }

    public VideoTagPayloadReader(TrackOutput output) {
        super(output);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
    }

    public void seek() {
    }

    protected boolean parseHeader(ParsableByteArray data) throws UnsupportedFormatException {
        int header = data.readUnsignedByte();
        int frameType = (header >> 4) & 15;
        int videoCodec = header & 15;
        if (videoCodec != VIDEO_CODEC_AVC) {
            throw new UnsupportedFormatException("Video format not supported: " + videoCodec);
        }
        this.frameType = frameType;
        return frameType != VIDEO_FRAME_VIDEO_INFO;
    }

    protected void parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        int packetType = data.readUnsignedByte();
        timeUs += ((long) data.readUnsignedInt24()) * 1000;
        if (packetType == 0 && !this.hasOutputFormat) {
            ParsableByteArray parsableByteArray = new ParsableByteArray(new byte[data.bytesLeft()]);
            data.readBytes(parsableByteArray.data, AVC_PACKET_TYPE_SEQUENCE_HEADER, data.bytesLeft());
            AvcSequenceHeaderData avcData = parseAvcCodecPrivate(parsableByteArray);
            this.nalUnitLengthFieldLength = avcData.nalUnitLengthFieldLength;
            this.output.format(MediaFormat.createVideoFormat(null, MediaController.MIME_TYPE, -1, -1, getDurationUs(), avcData.width, avcData.height, avcData.initializationData, -1, avcData.pixelWidthAspectRatio));
            this.hasOutputFormat = true;
        } else if (packetType == VIDEO_FRAME_KEYFRAME) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[AVC_PACKET_TYPE_SEQUENCE_HEADER] = (byte) 0;
            nalLengthData[VIDEO_FRAME_KEYFRAME] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            int nalUnitLengthFieldLengthDiff = 4 - this.nalUnitLengthFieldLength;
            int bytesWritten = AVC_PACKET_TYPE_SEQUENCE_HEADER;
            while (data.bytesLeft() > 0) {
                data.readBytes(this.nalLength.data, nalUnitLengthFieldLengthDiff, this.nalUnitLengthFieldLength);
                this.nalLength.setPosition(AVC_PACKET_TYPE_SEQUENCE_HEADER);
                int bytesToWrite = this.nalLength.readUnsignedIntToInt();
                this.nalStartCode.setPosition(AVC_PACKET_TYPE_SEQUENCE_HEADER);
                this.output.sampleData(this.nalStartCode, 4);
                bytesWritten += 4;
                this.output.sampleData(data, bytesToWrite);
                bytesWritten += bytesToWrite;
            }
            this.output.sampleMetadata(timeUs, this.frameType == VIDEO_FRAME_KEYFRAME ? VIDEO_FRAME_KEYFRAME : AVC_PACKET_TYPE_SEQUENCE_HEADER, bytesWritten, AVC_PACKET_TYPE_SEQUENCE_HEADER, null);
        }
    }

    private AvcSequenceHeaderData parseAvcCodecPrivate(ParsableByteArray buffer) throws ParserException {
        boolean z;
        buffer.setPosition(4);
        int nalUnitLengthFieldLength = (buffer.readUnsignedByte() & 3) + VIDEO_FRAME_KEYFRAME;
        if (nalUnitLengthFieldLength != 3) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        List<byte[]> initializationData = new ArrayList();
        int numSequenceParameterSets = buffer.readUnsignedByte() & 31;
        for (int i = AVC_PACKET_TYPE_SEQUENCE_HEADER; i < numSequenceParameterSets; i += VIDEO_FRAME_KEYFRAME) {
            initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
        }
        int numPictureParameterSets = buffer.readUnsignedByte();
        for (int j = AVC_PACKET_TYPE_SEQUENCE_HEADER; j < numPictureParameterSets; j += VIDEO_FRAME_KEYFRAME) {
            initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
        }
        float pixelWidthAspectRatio = TouchHelperCallback.ALPHA_FULL;
        int width = -1;
        int height = -1;
        if (numSequenceParameterSets > 0) {
            ParsableBitArray spsDataBitArray = new ParsableBitArray((byte[]) initializationData.get(AVC_PACKET_TYPE_SEQUENCE_HEADER));
            spsDataBitArray.setPosition((nalUnitLengthFieldLength + VIDEO_FRAME_KEYFRAME) * 8);
            SpsData sps = NalUnitUtil.parseSpsNalUnit(spsDataBitArray);
            width = sps.width;
            height = sps.height;
            pixelWidthAspectRatio = sps.pixelWidthAspectRatio;
        }
        return new AvcSequenceHeaderData(initializationData, nalUnitLengthFieldLength, width, height, pixelWidthAspectRatio);
    }
}
