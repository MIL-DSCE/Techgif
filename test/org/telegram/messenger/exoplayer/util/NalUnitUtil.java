package org.telegram.messenger.exoplayer.util;

import android.support.v4.media.TransportMediator;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public final class NalUnitUtil {
    public static final float[] ASPECT_RATIO_IDC_VALUES;
    public static final int EXTENDED_SAR = 255;
    public static final byte[] NAL_START_CODE;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private static final String TAG = "NalUnitUtil";
    private static int[] scratchEscapePositions;
    private static final Object scratchEscapePositionsLock;

    public static final class PpsData {
        public final boolean bottomFieldPicOrderInFramePresentFlag;
        public final int picParameterSetId;
        public final int seqParameterSetId;

        public PpsData(int picParameterSetId, int seqParameterSetId, boolean bottomFieldPicOrderInFramePresentFlag) {
            this.picParameterSetId = picParameterSetId;
            this.seqParameterSetId = seqParameterSetId;
            this.bottomFieldPicOrderInFramePresentFlag = bottomFieldPicOrderInFramePresentFlag;
        }
    }

    public static final class SpsData {
        public final boolean deltaPicOrderAlwaysZeroFlag;
        public final boolean frameMbsOnlyFlag;
        public final int frameNumLength;
        public final int height;
        public final int picOrderCntLsbLength;
        public final int picOrderCountType;
        public final float pixelWidthAspectRatio;
        public final boolean separateColorPlaneFlag;
        public final int seqParameterSetId;
        public final int width;

        public SpsData(int seqParameterSetId, int width, int height, float pixelWidthAspectRatio, boolean separateColorPlaneFlag, boolean frameMbsOnlyFlag, int frameNumLength, int picOrderCountType, int picOrderCntLsbLength, boolean deltaPicOrderAlwaysZeroFlag) {
            this.seqParameterSetId = seqParameterSetId;
            this.width = width;
            this.height = height;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio;
            this.separateColorPlaneFlag = separateColorPlaneFlag;
            this.frameMbsOnlyFlag = frameMbsOnlyFlag;
            this.frameNumLength = frameNumLength;
            this.picOrderCountType = picOrderCountType;
            this.picOrderCntLsbLength = picOrderCntLsbLength;
            this.deltaPicOrderAlwaysZeroFlag = deltaPicOrderAlwaysZeroFlag;
        }
    }

    static {
        NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};
        ASPECT_RATIO_IDC_VALUES = new float[]{TouchHelperCallback.ALPHA_FULL, TouchHelperCallback.ALPHA_FULL, 1.0909091f, 0.90909094f, 1.4545455f, 1.2121212f, 2.1818182f, 1.8181819f, 2.909091f, 2.4242425f, 1.6363636f, 1.3636364f, 1.939394f, 1.6161616f, 1.3333334f, 1.5f, 2.0f};
        scratchEscapePositionsLock = new Object();
        scratchEscapePositions = new int[10];
    }

    public static int unescapeStream(byte[] data, int limit) {
        synchronized (scratchEscapePositionsLock) {
            int position = 0;
            int scratchEscapeCount = 0;
            while (position < limit) {
                try {
                    position = findNextUnescapeIndex(data, position, limit);
                    if (position < limit) {
                        if (scratchEscapePositions.length <= scratchEscapeCount) {
                            scratchEscapePositions = Arrays.copyOf(scratchEscapePositions, scratchEscapePositions.length * 2);
                        }
                        int scratchEscapeCount2 = scratchEscapeCount + 1;
                        try {
                            scratchEscapePositions[scratchEscapeCount] = position;
                            position += 3;
                            scratchEscapeCount = scratchEscapeCount2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    scratchEscapeCount2 = scratchEscapeCount;
                }
            }
            int unescapedLength = limit - scratchEscapeCount;
            int escapedPosition = 0;
            int unescapedPosition = 0;
            for (int i = 0; i < scratchEscapeCount; i++) {
                int copyLength = scratchEscapePositions[i] - escapedPosition;
                System.arraycopy(data, escapedPosition, data, unescapedPosition, copyLength);
                unescapedPosition += copyLength;
                int i2 = unescapedPosition + 1;
                data[unescapedPosition] = (byte) 0;
                unescapedPosition = i2 + 1;
                data[i2] = (byte) 0;
                escapedPosition += copyLength + 3;
            }
            System.arraycopy(data, escapedPosition, data, unescapedPosition, unescapedLength - unescapedPosition);
            return unescapedLength;
        }
    }

    public static void discardToSps(ByteBuffer data) {
        int length = data.position();
        int consecutiveZeros = 0;
        int offset = 0;
        while (offset + 1 < length) {
            int value = data.get(offset) & EXTENDED_SAR;
            if (consecutiveZeros == 3) {
                if (value == 1 && (data.get(offset + 1) & 31) == NAL_UNIT_TYPE_SPS) {
                    ByteBuffer offsetData = data.duplicate();
                    offsetData.position(offset - 3);
                    offsetData.limit(length);
                    data.position(0);
                    data.put(offsetData);
                    return;
                }
            } else if (value == 0) {
                consecutiveZeros++;
            }
            if (value != 0) {
                consecutiveZeros = 0;
            }
            offset++;
        }
        data.clear();
    }

    public static byte[] parseChildNalUnit(ParsableByteArray atom) {
        int length = atom.readUnsignedShort();
        int offset = atom.getPosition();
        atom.skipBytes(length);
        return CodecSpecificDataUtil.buildNalUnit(atom.data, offset, length);
    }

    public static int getNalUnitType(byte[] data, int offset) {
        return data[offset + 3] & 31;
    }

    public static int getH265NalUnitType(byte[] data, int offset) {
        return (data[offset + 3] & TransportMediator.KEYCODE_MEDIA_PLAY) >> 1;
    }

    public static SpsData parseSpsNalUnit(ParsableBitArray data) {
        int i;
        int profileIdc = data.readBits(8);
        data.skipBits(16);
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        int chromaFormatIdc = 1;
        boolean separateColorPlaneFlag = false;
        if (profileIdc == 100 || profileIdc == 110 || profileIdc == 122 || profileIdc == 244 || profileIdc == 44 || profileIdc == 83 || profileIdc == 86 || profileIdc == 118 || profileIdc == 128 || profileIdc == 138) {
            chromaFormatIdc = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc == 3) {
                separateColorPlaneFlag = data.readBit();
            }
            data.readUnsignedExpGolombCodedInt();
            data.readUnsignedExpGolombCodedInt();
            data.skipBits(1);
            if (data.readBit()) {
                int limit = chromaFormatIdc != 3 ? 8 : 12;
                i = 0;
                while (i < limit) {
                    if (data.readBit()) {
                        skipScalingList(data, i < 6 ? 16 : 64);
                    }
                    i++;
                }
            }
        }
        int frameNumLength = data.readUnsignedExpGolombCodedInt() + 4;
        int picOrderCntType = data.readUnsignedExpGolombCodedInt();
        int picOrderCntLsbLength = 0;
        boolean deltaPicOrderAlwaysZeroFlag = false;
        if (picOrderCntType != 0) {
            if (picOrderCntType == 1) {
                deltaPicOrderAlwaysZeroFlag = data.readBit();
                data.readSignedExpGolombCodedInt();
                data.readSignedExpGolombCodedInt();
                long numRefFramesInPicOrderCntCycle = (long) data.readUnsignedExpGolombCodedInt();
                i = 0;
                while (true) {
                    if (((long) i) >= numRefFramesInPicOrderCntCycle) {
                        break;
                    }
                    data.readUnsignedExpGolombCodedInt();
                    i++;
                }
            }
        } else {
            picOrderCntLsbLength = data.readUnsignedExpGolombCodedInt() + 4;
        }
        data.readUnsignedExpGolombCodedInt();
        data.skipBits(1);
        int picWidthInMbs = data.readUnsignedExpGolombCodedInt() + 1;
        int picHeightInMapUnits = data.readUnsignedExpGolombCodedInt() + 1;
        boolean frameMbsOnlyFlag = data.readBit();
        int frameHeightInMbs = (2 - (frameMbsOnlyFlag ? 1 : 0)) * picHeightInMapUnits;
        if (!frameMbsOnlyFlag) {
            data.skipBits(1);
        }
        data.skipBits(1);
        int frameWidth = picWidthInMbs * 16;
        int frameHeight = frameHeightInMbs * 16;
        if (data.readBit()) {
            int cropUnitX;
            int cropUnitY;
            int frameCropLeftOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropRightOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropTopOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropBottomOffset = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc == 0) {
                cropUnitX = 1;
                cropUnitY = 2 - (frameMbsOnlyFlag ? 1 : 0);
            } else {
                cropUnitX = chromaFormatIdc == 3 ? 1 : 2;
                cropUnitY = (chromaFormatIdc == 1 ? 2 : 1) * (2 - (frameMbsOnlyFlag ? 1 : 0));
            }
            frameWidth -= (frameCropLeftOffset + frameCropRightOffset) * cropUnitX;
            frameHeight -= (frameCropTopOffset + frameCropBottomOffset) * cropUnitY;
        }
        float pixelWidthHeightRatio = TouchHelperCallback.ALPHA_FULL;
        if (data.readBit() && data.readBit()) {
            int aspectRatioIdc = data.readBits(8);
            if (aspectRatioIdc == EXTENDED_SAR) {
                int sarWidth = data.readBits(16);
                int sarHeight = data.readBits(16);
                if (!(sarWidth == 0 || sarHeight == 0)) {
                    pixelWidthHeightRatio = ((float) sarWidth) / ((float) sarHeight);
                }
            } else if (aspectRatioIdc < ASPECT_RATIO_IDC_VALUES.length) {
                pixelWidthHeightRatio = ASPECT_RATIO_IDC_VALUES[aspectRatioIdc];
            } else {
                Log.w(TAG, "Unexpected aspect_ratio_idc value: " + aspectRatioIdc);
            }
        }
        return new SpsData(seqParameterSetId, frameWidth, frameHeight, pixelWidthHeightRatio, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength, deltaPicOrderAlwaysZeroFlag);
    }

    public static PpsData parsePpsNalUnit(ParsableBitArray data) {
        int picParameterSetId = data.readUnsignedExpGolombCodedInt();
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        data.skipBits(1);
        return new PpsData(picParameterSetId, seqParameterSetId, data.readBit());
    }

    public static int findNalUnit(byte[] data, int startOffset, int endOffset, boolean[] prefixFlags) {
        boolean z;
        boolean z2 = true;
        int length = endOffset - startOffset;
        if (length >= 0) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (length == 0) {
            return endOffset;
        }
        if (prefixFlags != null) {
            if (prefixFlags[0]) {
                clearPrefixFlags(prefixFlags);
                return startOffset - 3;
            } else if (length > 1 && prefixFlags[1] && data[startOffset] == (byte) 1) {
                clearPrefixFlags(prefixFlags);
                return startOffset - 2;
            } else if (length > 2 && prefixFlags[2] && data[startOffset] == null && data[startOffset + 1] == (byte) 1) {
                clearPrefixFlags(prefixFlags);
                return startOffset - 1;
            }
        }
        int limit = endOffset - 1;
        int i = startOffset + 2;
        while (i < limit) {
            if ((data[i] & 254) == 0) {
                if (data[i - 2] == null && data[i - 1] == null && data[i] == (byte) 1) {
                    if (prefixFlags != null) {
                        clearPrefixFlags(prefixFlags);
                    }
                    return i - 2;
                }
                i -= 2;
            }
            i += 3;
        }
        if (prefixFlags == null) {
            return endOffset;
        }
        z = length > 2 ? data[endOffset + -3] == null && data[endOffset - 2] == null && data[endOffset - 1] == (byte) 1 : length == 2 ? prefixFlags[2] && data[endOffset - 2] == null && data[endOffset - 1] == (byte) 1 : prefixFlags[1] && data[endOffset - 1] == (byte) 1;
        prefixFlags[0] = z;
        z = length > 1 ? data[endOffset + -2] == null && data[endOffset - 1] == null : prefixFlags[2] && data[endOffset - 1] == null;
        prefixFlags[1] = z;
        if (data[endOffset - 1] != null) {
            z2 = false;
        }
        prefixFlags[2] = z2;
        return endOffset;
    }

    public static void clearPrefixFlags(boolean[] prefixFlags) {
        prefixFlags[0] = false;
        prefixFlags[1] = false;
        prefixFlags[2] = false;
    }

    private static int findNextUnescapeIndex(byte[] bytes, int offset, int limit) {
        int i = offset;
        while (i < limit - 2) {
            if (bytes[i] == null && bytes[i + 1] == null && bytes[i + 2] == 3) {
                return i;
            }
            i++;
        }
        return limit;
    }

    private static void skipScalingList(ParsableBitArray bitArray, int size) {
        int lastScale = 8;
        int nextScale = 8;
        for (int i = 0; i < size; i++) {
            if (nextScale != 0) {
                nextScale = ((lastScale + bitArray.readSignedExpGolombCodedInt()) + MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) % MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
            }
            if (nextScale != 0) {
                lastScale = nextScale;
            }
        }
    }

    private NalUnitUtil() {
    }
}
