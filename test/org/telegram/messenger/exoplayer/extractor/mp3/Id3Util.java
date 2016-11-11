package org.telegram.messenger.exoplayer.extractor.mp3;

import android.support.v4.media.TransportMediator;
import android.util.Pair;
import java.io.IOException;
import java.nio.charset.Charset;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

final class Id3Util {
    private static final Charset[] CHARSET_BY_ENCODING;
    private static final int ID3_TAG;
    private static final int MAXIMUM_METADATA_SIZE = 3145728;

    static {
        ID3_TAG = Util.getIntegerCodeForString("ID3");
        CHARSET_BY_ENCODING = new Charset[]{Charset.forName("ISO-8859-1"), Charset.forName("UTF-16LE"), Charset.forName("UTF-16BE"), Charset.forName(C0747C.UTF8_NAME)};
    }

    public static GaplessInfo parseId3(ExtractorInput input) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(10);
        int peekedId3Bytes = ID3_TAG;
        GaplessInfo metadata = null;
        while (true) {
            input.peekFully(scratch.data, ID3_TAG, 10);
            scratch.setPosition(ID3_TAG);
            if (scratch.readUnsignedInt24() != ID3_TAG) {
                input.resetPeekPosition();
                input.advancePeekPosition(peekedId3Bytes);
                return metadata;
            }
            int majorVersion = scratch.readUnsignedByte();
            int minorVersion = scratch.readUnsignedByte();
            int flags = scratch.readUnsignedByte();
            int length = scratch.readSynchSafeInt();
            if (metadata == null && canParseMetadata(majorVersion, minorVersion, flags, length)) {
                byte[] frame = new byte[length];
                input.peekFully(frame, ID3_TAG, length);
                metadata = parseGaplessInfo(new ParsableByteArray(frame), majorVersion, flags);
            } else {
                input.advancePeekPosition(length);
            }
            peekedId3Bytes += length + 10;
        }
    }

    private static boolean canParseMetadata(int majorVersion, int minorVersion, int flags, int length) {
        return minorVersion != NalUnitUtil.EXTENDED_SAR && majorVersion >= 2 && majorVersion <= 4 && length <= MAXIMUM_METADATA_SIZE && ((majorVersion != 2 || ((flags & 63) == 0 && (flags & 64) == 0)) && ((majorVersion != 3 || (flags & 31) == 0) && (majorVersion != 4 || (flags & 15) == 0)));
    }

    private static GaplessInfo parseGaplessInfo(ParsableByteArray frame, int version, int flags) {
        unescape(frame, version, flags);
        frame.setPosition(ID3_TAG);
        int extendedHeaderSize;
        if (version != 3 || (flags & 64) == 0) {
            if (version == 4 && (flags & 64) != 0) {
                if (frame.bytesLeft() < 4) {
                    return null;
                }
                extendedHeaderSize = frame.readSynchSafeInt();
                if (extendedHeaderSize < 6 || extendedHeaderSize > frame.bytesLeft() + 4) {
                    return null;
                }
                frame.setPosition(extendedHeaderSize);
            }
        } else if (frame.bytesLeft() < 4) {
            return null;
        } else {
            extendedHeaderSize = frame.readUnsignedIntToInt();
            if (extendedHeaderSize > frame.bytesLeft()) {
                return null;
            }
            if (extendedHeaderSize >= 6) {
                frame.skipBytes(2);
                int paddingSize = frame.readUnsignedIntToInt();
                frame.setPosition(4);
                frame.setLimit(frame.limit() - paddingSize);
                if (frame.bytesLeft() < extendedHeaderSize) {
                    return null;
                }
            }
            frame.skipBytes(extendedHeaderSize);
        }
        while (true) {
            Pair<String, String> comment = findNextComment(version, frame);
            if (comment == null) {
                return null;
            }
            if (((String) comment.first).length() > 3) {
                GaplessInfo gaplessInfo = GaplessInfo.createFromComment(((String) comment.first).substring(3), (String) comment.second);
                if (gaplessInfo != null) {
                    return gaplessInfo;
                }
            }
        }
    }

    private static Pair<String, String> findNextComment(int majorVersion, ParsableByteArray data) {
        while (true) {
            int frameSize;
            String id;
            if (majorVersion == 2) {
                if (data.bytesLeft() < 6) {
                    return null;
                }
                id = data.readString(3, Charset.forName("US-ASCII"));
                if (id.equals("\u0000\u0000\u0000")) {
                    return null;
                }
                frameSize = data.readUnsignedInt24();
                if (frameSize == 0 || frameSize > data.bytesLeft()) {
                    return null;
                }
                if (id.equals("COM")) {
                    break;
                }
                data.skipBytes(frameSize);
            } else if (data.bytesLeft() < 10) {
                return null;
            } else {
                id = data.readString(4, Charset.forName("US-ASCII"));
                if (id.equals("\u0000\u0000\u0000\u0000")) {
                    return null;
                }
                frameSize = majorVersion == 4 ? data.readSynchSafeInt() : data.readUnsignedIntToInt();
                if (frameSize == 0 || frameSize > data.bytesLeft() - 2) {
                    return null;
                }
                boolean compressedOrEncrypted;
                int flags = data.readUnsignedShort();
                if ((majorVersion != 4 || (flags & 12) == 0) && (majorVersion != 3 || (flags & PsExtractor.AUDIO_STREAM) == 0)) {
                    compressedOrEncrypted = false;
                } else {
                    compressedOrEncrypted = true;
                }
                if (!compressedOrEncrypted && id.equals("COMM")) {
                    break;
                }
                data.skipBytes(frameSize);
            }
        }
        int encoding = data.readUnsignedByte();
        if (encoding < 0 || encoding >= CHARSET_BY_ENCODING.length) {
            return null;
        }
        String[] commentFields = data.readString(frameSize - 1, CHARSET_BY_ENCODING[encoding]).split("\u0000");
        if (commentFields.length == 2) {
            return Pair.create(commentFields[ID3_TAG], commentFields[1]);
        }
        return null;
    }

    private static boolean unescape(ParsableByteArray frame, int version, int flags) {
        if (version != 4) {
            if ((flags & MessagesController.UPDATE_MASK_USER_PHONE) != 0) {
                byte[] bytes = frame.data;
                int newLength = bytes.length;
                int i = ID3_TAG;
                while (i + 1 < newLength) {
                    if ((bytes[i] & NalUnitUtil.EXTENDED_SAR) == NalUnitUtil.EXTENDED_SAR && bytes[i + 1] == null) {
                        System.arraycopy(bytes, i + 2, bytes, i + 1, (newLength - i) - 2);
                        newLength--;
                    }
                    i++;
                }
                frame.setLimit(newLength);
            }
        } else if (canUnescapeVersion4(frame, false)) {
            unescapeVersion4(frame, false);
        } else if (!canUnescapeVersion4(frame, true)) {
            return false;
        } else {
            unescapeVersion4(frame, true);
        }
        return true;
    }

    private static boolean canUnescapeVersion4(ParsableByteArray frame, boolean unsignedIntDataSizeHack) {
        frame.setPosition(ID3_TAG);
        while (frame.bytesLeft() >= 10 && frame.readInt() != 0) {
            long dataSize = frame.readUnsignedInt();
            if (!unsignedIntDataSizeHack) {
                if ((8421504 & dataSize) != 0) {
                    return false;
                }
                dataSize = (((dataSize & 127) | (((dataSize >> 8) & 127) << 7)) | (((dataSize >> 16) & 127) << 14)) | (((dataSize >> 24) & 127) << 21);
            }
            if (dataSize > ((long) (frame.bytesLeft() - 2))) {
                return false;
            }
            if ((frame.readUnsignedShort() & 1) != 0 && frame.bytesLeft() < 4) {
                return false;
            }
            frame.skipBytes((int) dataSize);
        }
        return true;
    }

    private static void unescapeVersion4(ParsableByteArray frame, boolean unsignedIntDataSizeHack) {
        frame.setPosition(ID3_TAG);
        byte[] bytes = frame.data;
        while (frame.bytesLeft() >= 10 && frame.readInt() != 0) {
            int dataSize = unsignedIntDataSizeHack ? frame.readUnsignedIntToInt() : frame.readSynchSafeInt();
            int flags = frame.readUnsignedShort();
            int previousFlags = flags;
            if ((flags & 1) != 0) {
                int offset = frame.getPosition();
                System.arraycopy(bytes, offset + 4, bytes, offset, frame.bytesLeft() - 4);
                dataSize -= 4;
                flags &= -2;
                frame.setLimit(frame.limit() - 4);
            }
            if ((flags & 2) != 0) {
                int readOffset = frame.getPosition() + 1;
                int writeOffset = readOffset;
                int i = ID3_TAG;
                int writeOffset2 = writeOffset;
                while (i + 1 < dataSize) {
                    if ((bytes[readOffset - 1] & NalUnitUtil.EXTENDED_SAR) == NalUnitUtil.EXTENDED_SAR && bytes[readOffset] == null) {
                        readOffset++;
                        dataSize--;
                    }
                    writeOffset = writeOffset2 + 1;
                    int readOffset2 = readOffset + 1;
                    bytes[writeOffset2] = bytes[readOffset];
                    i++;
                    writeOffset2 = writeOffset;
                    readOffset = readOffset2;
                }
                frame.setLimit(frame.limit() - (readOffset - writeOffset2));
                System.arraycopy(bytes, readOffset, bytes, writeOffset2, frame.bytesLeft() - readOffset);
                flags &= -3;
            }
            if (flags != previousFlags || unsignedIntDataSizeHack) {
                int dataSizeOffset = frame.getPosition() - 6;
                writeSyncSafeInteger(bytes, dataSizeOffset, dataSize);
                bytes[dataSizeOffset + 4] = (byte) (flags >> 8);
                bytes[dataSizeOffset + 5] = (byte) (flags & NalUnitUtil.EXTENDED_SAR);
            }
            frame.skipBytes(dataSize);
        }
    }

    private static void writeSyncSafeInteger(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte) ((value >> 21) & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bytes[offset + 1] = (byte) ((value >> 14) & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bytes[offset + 2] = (byte) ((value >> 7) & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bytes[offset + 3] = (byte) (value & TransportMediator.KEYCODE_MEDIA_PAUSE);
    }

    private Id3Util() {
    }
}
