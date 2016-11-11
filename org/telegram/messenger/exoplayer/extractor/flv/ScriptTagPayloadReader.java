package org.telegram.messenger.exoplayer.extractor.flv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class ScriptTagPayloadReader extends TagPayloadReader {
    private static final int AMF_TYPE_BOOLEAN = 1;
    private static final int AMF_TYPE_DATE = 11;
    private static final int AMF_TYPE_ECMA_ARRAY = 8;
    private static final int AMF_TYPE_END_MARKER = 9;
    private static final int AMF_TYPE_NUMBER = 0;
    private static final int AMF_TYPE_OBJECT = 3;
    private static final int AMF_TYPE_STRICT_ARRAY = 10;
    private static final int AMF_TYPE_STRING = 2;
    private static final String KEY_DURATION = "duration";
    private static final String NAME_METADATA = "onMetaData";

    public ScriptTagPayloadReader(TrackOutput output) {
        super(output);
    }

    public void seek() {
    }

    protected boolean parseHeader(ParsableByteArray data) {
        return true;
    }

    protected void parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        if (readAmfType(data) != AMF_TYPE_STRING) {
            throw new ParserException();
        }
        if (!NAME_METADATA.equals(readAmfString(data))) {
            return;
        }
        if (readAmfType(data) != AMF_TYPE_ECMA_ARRAY) {
            throw new ParserException();
        }
        Map<String, Object> metadata = readAmfEcmaArray(data);
        if (metadata.containsKey(KEY_DURATION)) {
            double durationSeconds = ((Double) metadata.get(KEY_DURATION)).doubleValue();
            if (durationSeconds > 0.0d) {
                setDurationUs((long) (1000000.0d * durationSeconds));
            }
        }
    }

    private static int readAmfType(ParsableByteArray data) {
        return data.readUnsignedByte();
    }

    private static Boolean readAmfBoolean(ParsableByteArray data) {
        boolean z = true;
        if (data.readUnsignedByte() != AMF_TYPE_BOOLEAN) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    private static Double readAmfDouble(ParsableByteArray data) {
        return Double.valueOf(Double.longBitsToDouble(data.readLong()));
    }

    private static String readAmfString(ParsableByteArray data) {
        int size = data.readUnsignedShort();
        int position = data.getPosition();
        data.skipBytes(size);
        return new String(data.data, position, size);
    }

    private static ArrayList<Object> readAmfStrictArray(ParsableByteArray data) {
        int count = data.readUnsignedIntToInt();
        ArrayList<Object> list = new ArrayList(count);
        for (int i = AMF_TYPE_NUMBER; i < count; i += AMF_TYPE_BOOLEAN) {
            list.add(readAmfData(data, readAmfType(data)));
        }
        return list;
    }

    private static HashMap<String, Object> readAmfObject(ParsableByteArray data) {
        HashMap<String, Object> array = new HashMap();
        while (true) {
            String key = readAmfString(data);
            int type = readAmfType(data);
            if (type == AMF_TYPE_END_MARKER) {
                return array;
            }
            array.put(key, readAmfData(data, type));
        }
    }

    private static HashMap<String, Object> readAmfEcmaArray(ParsableByteArray data) {
        int count = data.readUnsignedIntToInt();
        HashMap<String, Object> array = new HashMap(count);
        for (int i = AMF_TYPE_NUMBER; i < count; i += AMF_TYPE_BOOLEAN) {
            array.put(readAmfString(data), readAmfData(data, readAmfType(data)));
        }
        return array;
    }

    private static Date readAmfDate(ParsableByteArray data) {
        Date date = new Date((long) readAmfDouble(data).doubleValue());
        data.skipBytes(AMF_TYPE_STRING);
        return date;
    }

    private static Object readAmfData(ParsableByteArray data, int type) {
        switch (type) {
            case AMF_TYPE_NUMBER /*0*/:
                return readAmfDouble(data);
            case AMF_TYPE_BOOLEAN /*1*/:
                return readAmfBoolean(data);
            case AMF_TYPE_STRING /*2*/:
                return readAmfString(data);
            case AMF_TYPE_OBJECT /*3*/:
                return readAmfObject(data);
            case AMF_TYPE_ECMA_ARRAY /*8*/:
                return readAmfEcmaArray(data);
            case AMF_TYPE_STRICT_ARRAY /*10*/:
                return readAmfStrictArray(data);
            case AMF_TYPE_DATE /*11*/:
                return readAmfDate(data);
            default:
                return null;
        }
    }
}
