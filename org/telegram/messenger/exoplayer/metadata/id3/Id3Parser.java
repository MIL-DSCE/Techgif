package org.telegram.messenger.exoplayer.metadata.id3;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.metadata.MetadataParser;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class Id3Parser implements MetadataParser<List<Id3Frame>> {
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;

    public boolean canParse(String mimeType) {
        return mimeType.equals(MimeTypes.APPLICATION_ID3);
    }

    public List<Id3Frame> parse(byte[] data, int size) throws ParserException {
        List<Id3Frame> id3Frames = new ArrayList();
        ParsableByteArray id3Data = new ParsableByteArray(data, size);
        int id3Size = parseId3Header(id3Data);
        while (id3Size > 0) {
            int frameId0 = id3Data.readUnsignedByte();
            int frameId1 = id3Data.readUnsignedByte();
            int frameId2 = id3Data.readUnsignedByte();
            int frameId3 = id3Data.readUnsignedByte();
            int frameSize = id3Data.readSynchSafeInt();
            if (frameSize <= ID3_TEXT_ENCODING_UTF_16) {
                break;
            }
            Id3Frame frame;
            id3Data.skipBytes(ID3_TEXT_ENCODING_UTF_16BE);
            if (frameId0 == 84 && frameId1 == 88 && frameId2 == 88 && frameId3 == 88) {
                try {
                    frame = parseTxxxFrame(id3Data, frameSize);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            } else if (frameId0 == 80 && frameId1 == 82 && frameId2 == 73 && frameId3 == 86) {
                frame = parsePrivFrame(id3Data, frameSize);
            } else if (frameId0 == 71 && frameId1 == 69 && frameId2 == 79 && frameId3 == 66) {
                frame = parseGeobFrame(id3Data, frameSize);
            } else if (frameId0 == 65 && frameId1 == 80 && frameId2 == 73 && frameId3 == 67) {
                frame = parseApicFrame(id3Data, frameSize);
            } else if (frameId0 == 84) {
                frame = parseTextInformationFrame(id3Data, frameSize, String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)}));
            } else {
                frame = parseBinaryFrame(id3Data, frameSize, String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)}));
            }
            id3Frames.add(frame);
            id3Size -= frameSize + 10;
        }
        return Collections.unmodifiableList(id3Frames);
    }

    private static int indexOfEos(byte[] data, int fromIndex, int encoding) {
        int terminationPos = indexOfZeroByte(data, fromIndex);
        if (encoding == 0 || encoding == ID3_TEXT_ENCODING_UTF_8) {
            return terminationPos;
        }
        while (terminationPos < data.length - 1) {
            if (data[terminationPos + ID3_TEXT_ENCODING_UTF_16] == null) {
                return terminationPos;
            }
            terminationPos = indexOfZeroByte(data, terminationPos + ID3_TEXT_ENCODING_UTF_16);
        }
        return data.length;
    }

    private static int indexOfZeroByte(byte[] data, int fromIndex) {
        for (int i = fromIndex; i < data.length; i += ID3_TEXT_ENCODING_UTF_16) {
            if (data[i] == null) {
                return i;
            }
        }
        return data.length;
    }

    private static int delimiterLength(int encodingByte) {
        return (encodingByte == 0 || encodingByte == ID3_TEXT_ENCODING_UTF_8) ? ID3_TEXT_ENCODING_UTF_16 : ID3_TEXT_ENCODING_UTF_16BE;
    }

    private static int parseId3Header(ParsableByteArray id3Buffer) throws ParserException {
        int id1 = id3Buffer.readUnsignedByte();
        int id2 = id3Buffer.readUnsignedByte();
        int id3 = id3Buffer.readUnsignedByte();
        if (id1 == 73 && id2 == 68 && id3 == 51) {
            id3Buffer.skipBytes(ID3_TEXT_ENCODING_UTF_16BE);
            int flags = id3Buffer.readUnsignedByte();
            int id3Size = id3Buffer.readSynchSafeInt();
            if ((flags & ID3_TEXT_ENCODING_UTF_16BE) != 0) {
                int extendedHeaderSize = id3Buffer.readSynchSafeInt();
                if (extendedHeaderSize > 4) {
                    id3Buffer.skipBytes(extendedHeaderSize - 4);
                }
                id3Size -= extendedHeaderSize;
            }
            if ((flags & 8) != 0) {
                return id3Size - 10;
            }
            return id3Size;
        }
        Object[] objArr = new Object[ID3_TEXT_ENCODING_UTF_8];
        objArr[ID3_TEXT_ENCODING_ISO_8859_1] = Integer.valueOf(id1);
        objArr[ID3_TEXT_ENCODING_UTF_16] = Integer.valueOf(id2);
        objArr[ID3_TEXT_ENCODING_UTF_16BE] = Integer.valueOf(id3);
        throw new ParserException(String.format(Locale.US, "Unexpected ID3 file identifier, expected \"ID3\", actual \"%c%c%c\".", objArr));
    }

    private static TxxxFrame parseTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, ID3_TEXT_ENCODING_ISO_8859_1, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, ID3_TEXT_ENCODING_ISO_8859_1, encoding);
        int valueStartIndex = descriptionEndIndex + delimiterLength(encoding);
        return new TxxxFrame(new String(data, ID3_TEXT_ENCODING_ISO_8859_1, descriptionEndIndex, charset), new String(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding) - valueStartIndex, charset));
    }

    private static PrivFrame parsePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, ID3_TEXT_ENCODING_ISO_8859_1, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, ID3_TEXT_ENCODING_ISO_8859_1);
        return new PrivFrame(new String(data, ID3_TEXT_ENCODING_ISO_8859_1, ownerEndIndex, "ISO-8859-1"), Arrays.copyOfRange(data, ownerEndIndex + ID3_TEXT_ENCODING_UTF_16, data.length));
    }

    private static GeobFrame parseGeobFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, ID3_TEXT_ENCODING_ISO_8859_1, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, ID3_TEXT_ENCODING_ISO_8859_1);
        String mimeType = new String(data, ID3_TEXT_ENCODING_ISO_8859_1, mimeTypeEndIndex, "ISO-8859-1");
        int filenameStartIndex = mimeTypeEndIndex + ID3_TEXT_ENCODING_UTF_16;
        int filenameEndIndex = indexOfEos(data, filenameStartIndex, encoding);
        String filename = new String(data, filenameStartIndex, filenameEndIndex - filenameStartIndex, charset);
        int descriptionStartIndex = filenameEndIndex + delimiterLength(encoding);
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new GeobFrame(mimeType, filename, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), Arrays.copyOfRange(data, descriptionEndIndex + delimiterLength(encoding), data.length));
    }

    private static ApicFrame parseApicFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, ID3_TEXT_ENCODING_ISO_8859_1, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, ID3_TEXT_ENCODING_ISO_8859_1);
        String mimeType = new String(data, ID3_TEXT_ENCODING_ISO_8859_1, mimeTypeEndIndex, "ISO-8859-1");
        int pictureType = data[mimeTypeEndIndex + ID3_TEXT_ENCODING_UTF_16] & NalUnitUtil.EXTENDED_SAR;
        int descriptionStartIndex = mimeTypeEndIndex + ID3_TEXT_ENCODING_UTF_16BE;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new ApicFrame(mimeType, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), pictureType, Arrays.copyOfRange(data, descriptionEndIndex + delimiterLength(encoding), data.length));
    }

    private static TextInformationFrame parseTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, ID3_TEXT_ENCODING_ISO_8859_1, frameSize - 1);
        return new TextInformationFrame(id, new String(data, ID3_TEXT_ENCODING_ISO_8859_1, indexOfEos(data, ID3_TEXT_ENCODING_ISO_8859_1, encoding), charset));
    }

    private static BinaryFrame parseBinaryFrame(ParsableByteArray id3Data, int frameSize, String id) {
        byte[] frame = new byte[frameSize];
        id3Data.readBytes(frame, ID3_TEXT_ENCODING_ISO_8859_1, frameSize);
        return new BinaryFrame(id, frame);
    }

    private static String getCharsetName(int encodingByte) {
        switch (encodingByte) {
            case ID3_TEXT_ENCODING_ISO_8859_1 /*0*/:
                return "ISO-8859-1";
            case ID3_TEXT_ENCODING_UTF_16 /*1*/:
                return "UTF-16";
            case ID3_TEXT_ENCODING_UTF_16BE /*2*/:
                return "UTF-16BE";
            case ID3_TEXT_ENCODING_UTF_8 /*3*/:
                return C0747C.UTF8_NAME;
            default:
                return "ISO-8859-1";
        }
    }
}
