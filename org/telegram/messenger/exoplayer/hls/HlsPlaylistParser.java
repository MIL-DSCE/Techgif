package org.telegram.messenger.exoplayer.hls;

import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.hls.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer.upstream.UriLoadable.Parser;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class HlsPlaylistParser implements Parser<HlsPlaylist> {
    private static final String AUDIO_TYPE = "AUDIO";
    private static final String BANDWIDTH_ATTR = "BANDWIDTH";
    private static final Pattern BANDWIDTH_ATTR_REGEX;
    private static final Pattern BYTERANGE_REGEX;
    private static final String BYTERANGE_TAG = "#EXT-X-BYTERANGE";
    private static final String CLOSED_CAPTIONS_TYPE = "CLOSED-CAPTIONS";
    private static final String CODECS_ATTR = "CODECS";
    private static final Pattern CODECS_ATTR_REGEX;
    private static final String DISCONTINUITY_SEQUENCE_TAG = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String DISCONTINUITY_TAG = "#EXT-X-DISCONTINUITY";
    private static final String ENDLIST_TAG = "#EXT-X-ENDLIST";
    private static final String INSTREAM_ID_ATTR = "INSTREAM-ID";
    private static final Pattern INSTREAM_ID_ATTR_REGEX;
    private static final String IV_ATTR = "IV";
    private static final Pattern IV_ATTR_REGEX;
    private static final String KEY_TAG = "#EXT-X-KEY";
    private static final String LANGUAGE_ATTR = "LANGUAGE";
    private static final Pattern LANGUAGE_ATTR_REGEX;
    private static final Pattern MEDIA_DURATION_REGEX;
    private static final String MEDIA_DURATION_TAG = "#EXTINF";
    private static final Pattern MEDIA_SEQUENCE_REGEX;
    private static final String MEDIA_SEQUENCE_TAG = "#EXT-X-MEDIA-SEQUENCE";
    private static final String MEDIA_TAG = "#EXT-X-MEDIA";
    private static final String METHOD_AES128 = "AES-128";
    private static final String METHOD_ATTR = "METHOD";
    private static final Pattern METHOD_ATTR_REGEX;
    private static final String METHOD_NONE = "NONE";
    private static final String NAME_ATTR = "NAME";
    private static final Pattern NAME_ATTR_REGEX;
    private static final String RESOLUTION_ATTR = "RESOLUTION";
    private static final Pattern RESOLUTION_ATTR_REGEX;
    private static final String STREAM_INF_TAG = "#EXT-X-STREAM-INF";
    private static final String SUBTITLES_TYPE = "SUBTITLES";
    private static final Pattern TARGET_DURATION_REGEX;
    private static final String TARGET_DURATION_TAG = "#EXT-X-TARGETDURATION";
    private static final String TYPE_ATTR = "TYPE";
    private static final Pattern TYPE_ATTR_REGEX;
    private static final String URI_ATTR = "URI";
    private static final Pattern URI_ATTR_REGEX;
    private static final Pattern VERSION_REGEX;
    private static final String VERSION_TAG = "#EXT-X-VERSION";
    private static final String VIDEO_TYPE = "VIDEO";

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> extraLines, BufferedReader reader) {
            this.extraLines = extraLines;
            this.reader = reader;
        }

        public boolean hasNext() throws IOException {
            if (this.next != null) {
                return true;
            }
            if (this.extraLines.isEmpty()) {
                do {
                    String readLine = this.reader.readLine();
                    this.next = readLine;
                    if (readLine == null) {
                        return false;
                    }
                    this.next = this.next.trim();
                } while (this.next.isEmpty());
                return true;
            }
            this.next = (String) this.extraLines.poll();
            return true;
        }

        public String next() throws IOException {
            if (!hasNext()) {
                return null;
            }
            String result = this.next;
            this.next = null;
            return result;
        }
    }

    static {
        BANDWIDTH_ATTR_REGEX = Pattern.compile("BANDWIDTH=(\\d+)\\b");
        CODECS_ATTR_REGEX = Pattern.compile("CODECS=\"(.+?)\"");
        RESOLUTION_ATTR_REGEX = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
        MEDIA_DURATION_REGEX = Pattern.compile("#EXTINF:([\\d.]+)\\b");
        MEDIA_SEQUENCE_REGEX = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
        TARGET_DURATION_REGEX = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
        VERSION_REGEX = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
        BYTERANGE_REGEX = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
        METHOD_ATTR_REGEX = Pattern.compile("METHOD=(NONE|AES-128)");
        URI_ATTR_REGEX = Pattern.compile("URI=\"(.+?)\"");
        IV_ATTR_REGEX = Pattern.compile("IV=([^,.*]+)");
        TYPE_ATTR_REGEX = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
        LANGUAGE_ATTR_REGEX = Pattern.compile("LANGUAGE=\"(.+?)\"");
        NAME_ATTR_REGEX = Pattern.compile("NAME=\"(.+?)\"");
        INSTREAM_ID_ATTR_REGEX = Pattern.compile("INSTREAM-ID=\"(.+?)\"");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.messenger.exoplayer.hls.HlsPlaylist parse(java.lang.String r6, java.io.InputStream r7) throws java.io.IOException, org.telegram.messenger.exoplayer.ParserException {
        /*
        r5 = this;
        r2 = new java.io.BufferedReader;
        r3 = new java.io.InputStreamReader;
        r3.<init>(r7);
        r2.<init>(r3);
        r0 = new java.util.LinkedList;
        r0.<init>();
    L_0x000f:
        r1 = r2.readLine();	 Catch:{ all -> 0x008b }
        if (r1 == 0) goto L_0x0090;
    L_0x0015:
        r1 = r1.trim();	 Catch:{ all -> 0x008b }
        r3 = r1.isEmpty();	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x000f;
    L_0x001f:
        r3 = "#EXT-X-STREAM-INF";
        r3 = r1.startsWith(r3);	 Catch:{ all -> 0x008b }
        if (r3 == 0) goto L_0x0037;
    L_0x0027:
        r0.add(r1);	 Catch:{ all -> 0x008b }
        r3 = new org.telegram.messenger.exoplayer.hls.HlsPlaylistParser$LineIterator;	 Catch:{ all -> 0x008b }
        r3.<init>(r0, r2);	 Catch:{ all -> 0x008b }
        r3 = parseMasterPlaylist(r3, r6);	 Catch:{ all -> 0x008b }
        r2.close();
    L_0x0036:
        return r3;
    L_0x0037:
        r3 = "#EXT-X-TARGETDURATION";
        r3 = r1.startsWith(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x003f:
        r3 = "#EXT-X-MEDIA-SEQUENCE";
        r3 = r1.startsWith(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x0047:
        r3 = "#EXTINF";
        r3 = r1.startsWith(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x004f:
        r3 = "#EXT-X-KEY";
        r3 = r1.startsWith(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x0057:
        r3 = "#EXT-X-BYTERANGE";
        r3 = r1.startsWith(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x005f:
        r3 = "#EXT-X-DISCONTINUITY";
        r3 = r1.equals(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x0067:
        r3 = "#EXT-X-DISCONTINUITY-SEQUENCE";
        r3 = r1.equals(r3);	 Catch:{ all -> 0x008b }
        if (r3 != 0) goto L_0x0077;
    L_0x006f:
        r3 = "#EXT-X-ENDLIST";
        r3 = r1.equals(r3);	 Catch:{ all -> 0x008b }
        if (r3 == 0) goto L_0x0087;
    L_0x0077:
        r0.add(r1);	 Catch:{ all -> 0x008b }
        r3 = new org.telegram.messenger.exoplayer.hls.HlsPlaylistParser$LineIterator;	 Catch:{ all -> 0x008b }
        r3.<init>(r0, r2);	 Catch:{ all -> 0x008b }
        r3 = parseMediaPlaylist(r3, r6);	 Catch:{ all -> 0x008b }
        r2.close();
        goto L_0x0036;
    L_0x0087:
        r0.add(r1);	 Catch:{ all -> 0x008b }
        goto L_0x000f;
    L_0x008b:
        r3 = move-exception;
        r2.close();
        throw r3;
    L_0x0090:
        r2.close();
        r3 = new org.telegram.messenger.exoplayer.ParserException;
        r4 = "Failed to parse the playlist, could not identify any tags.";
        r3.<init>(r4);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.hls.HlsPlaylistParser.parse(java.lang.String, java.io.InputStream):org.telegram.messenger.exoplayer.hls.HlsPlaylist");
    }

    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator iterator, String baseUri) throws IOException {
        ArrayList<Variant> variants = new ArrayList();
        ArrayList<Variant> audios = new ArrayList();
        ArrayList<Variant> subtitles = new ArrayList();
        int bitrate = 0;
        String codecs = null;
        int width = -1;
        int height = -1;
        String muxedAudioLanguage = null;
        String muxedCaptionLanguage = null;
        boolean expectingStreamInfUrl = false;
        String name = null;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(MEDIA_TAG)) {
                String type = HlsParserUtil.parseStringAttr(line, TYPE_ATTR_REGEX, TYPE_ATTR);
                if (CLOSED_CAPTIONS_TYPE.equals(type)) {
                    if ("CC1".equals(HlsParserUtil.parseStringAttr(line, INSTREAM_ID_ATTR_REGEX, INSTREAM_ID_ATTR))) {
                        muxedCaptionLanguage = HlsParserUtil.parseOptionalStringAttr(line, LANGUAGE_ATTR_REGEX);
                    }
                } else if (SUBTITLES_TYPE.equals(type)) {
                    String subtitleName = HlsParserUtil.parseStringAttr(line, NAME_ATTR_REGEX, NAME_ATTR);
                    uri = HlsParserUtil.parseStringAttr(line, URI_ATTR_REGEX, URI_ATTR);
                    language = HlsParserUtil.parseOptionalStringAttr(line, LANGUAGE_ATTR_REGEX);
                    subtitles.add(new Variant(uri, new Format(subtitleName, MimeTypes.APPLICATION_M3U8, -1, -1, GroundOverlayOptions.NO_DIMENSION, -1, -1, -1, language, codecs)));
                } else if (AUDIO_TYPE.equals(type)) {
                    language = HlsParserUtil.parseOptionalStringAttr(line, LANGUAGE_ATTR_REGEX);
                    uri = HlsParserUtil.parseOptionalStringAttr(line, URI_ATTR_REGEX);
                    if (uri != null) {
                        String str = uri;
                        audios.add(new Variant(str, new Format(HlsParserUtil.parseStringAttr(line, NAME_ATTR_REGEX, NAME_ATTR), MimeTypes.APPLICATION_M3U8, -1, -1, GroundOverlayOptions.NO_DIMENSION, -1, -1, -1, language, codecs)));
                    } else {
                        muxedAudioLanguage = language;
                    }
                }
            } else {
                String name2;
                if (line.startsWith(STREAM_INF_TAG)) {
                    bitrate = HlsParserUtil.parseIntAttr(line, BANDWIDTH_ATTR_REGEX, BANDWIDTH_ATTR);
                    codecs = HlsParserUtil.parseOptionalStringAttr(line, CODECS_ATTR_REGEX);
                    name2 = HlsParserUtil.parseOptionalStringAttr(line, NAME_ATTR_REGEX);
                    String resolutionString = HlsParserUtil.parseOptionalStringAttr(line, RESOLUTION_ATTR_REGEX);
                    if (resolutionString != null) {
                        String[] widthAndHeight = resolutionString.split("x");
                        width = Integer.parseInt(widthAndHeight[0]);
                        if (width <= 0) {
                            width = -1;
                        }
                        height = Integer.parseInt(widthAndHeight[1]);
                        if (height <= 0) {
                            height = -1;
                        }
                    } else {
                        width = -1;
                        height = -1;
                    }
                    expectingStreamInfUrl = true;
                    name = name2;
                } else {
                    if (!line.startsWith("#") && expectingStreamInfUrl) {
                        if (name == null) {
                            name2 = Integer.toString(variants.size());
                        } else {
                            name2 = name;
                        }
                        variants.add(new Variant(line, new Format(name2, MimeTypes.APPLICATION_M3U8, width, height, GroundOverlayOptions.NO_DIMENSION, -1, -1, bitrate, null, codecs)));
                        bitrate = 0;
                        codecs = null;
                        width = -1;
                        height = -1;
                        expectingStreamInfUrl = false;
                        name = null;
                    }
                }
            }
        }
        return new HlsMasterPlaylist(baseUri, variants, audios, subtitles, muxedAudioLanguage, muxedCaptionLanguage);
    }

    private static HlsMediaPlaylist parseMediaPlaylist(LineIterator iterator, String baseUri) throws IOException {
        int mediaSequence = 0;
        int targetDurationSecs = 0;
        int version = 1;
        boolean live = true;
        List<Segment> segments = new ArrayList();
        double segmentDurationSecs = 0.0d;
        int discontinuitySequenceNumber = 0;
        long segmentStartTimeUs = 0;
        long segmentByterangeOffset = 0;
        long segmentByterangeLength = -1;
        int segmentMediaSequence = 0;
        boolean isEncrypted = false;
        String encryptionKeyUri = null;
        String encryptionIV = null;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(TARGET_DURATION_TAG)) {
                targetDurationSecs = HlsParserUtil.parseIntAttr(line, TARGET_DURATION_REGEX, TARGET_DURATION_TAG);
            } else if (line.startsWith(MEDIA_SEQUENCE_TAG)) {
                mediaSequence = HlsParserUtil.parseIntAttr(line, MEDIA_SEQUENCE_REGEX, MEDIA_SEQUENCE_TAG);
                segmentMediaSequence = mediaSequence;
            } else if (line.startsWith(VERSION_TAG)) {
                version = HlsParserUtil.parseIntAttr(line, VERSION_REGEX, VERSION_TAG);
            } else if (line.startsWith(MEDIA_DURATION_TAG)) {
                segmentDurationSecs = HlsParserUtil.parseDoubleAttr(line, MEDIA_DURATION_REGEX, MEDIA_DURATION_TAG);
            } else if (line.startsWith(KEY_TAG)) {
                isEncrypted = METHOD_AES128.equals(HlsParserUtil.parseStringAttr(line, METHOD_ATTR_REGEX, METHOD_ATTR));
                if (isEncrypted) {
                    encryptionKeyUri = HlsParserUtil.parseStringAttr(line, URI_ATTR_REGEX, URI_ATTR);
                    encryptionIV = HlsParserUtil.parseOptionalStringAttr(line, IV_ATTR_REGEX);
                } else {
                    encryptionKeyUri = null;
                    encryptionIV = null;
                }
            } else if (line.startsWith(BYTERANGE_TAG)) {
                String parseStringAttr = HlsParserUtil.parseStringAttr(line, BYTERANGE_REGEX, BYTERANGE_TAG);
                String[] splitByteRange = byteRange.split("@");
                segmentByterangeLength = Long.parseLong(splitByteRange[0]);
                if (splitByteRange.length > 1) {
                    segmentByterangeOffset = Long.parseLong(splitByteRange[1]);
                }
            } else if (line.startsWith(DISCONTINUITY_SEQUENCE_TAG)) {
                discontinuitySequenceNumber = Integer.parseInt(line.substring(line.indexOf(58) + 1));
            } else if (line.equals(DISCONTINUITY_TAG)) {
                discontinuitySequenceNumber++;
            } else if (!line.startsWith("#")) {
                String segmentEncryptionIV;
                if (!isEncrypted) {
                    segmentEncryptionIV = null;
                } else if (encryptionIV != null) {
                    segmentEncryptionIV = encryptionIV;
                } else {
                    segmentEncryptionIV = Integer.toHexString(segmentMediaSequence);
                }
                segmentMediaSequence++;
                if (segmentByterangeLength == -1) {
                    segmentByterangeOffset = 0;
                }
                segments.add(new Segment(line, segmentDurationSecs, discontinuitySequenceNumber, segmentStartTimeUs, isEncrypted, encryptionKeyUri, segmentEncryptionIV, segmentByterangeOffset, segmentByterangeLength));
                segmentStartTimeUs += (long) (1000000.0d * segmentDurationSecs);
                segmentDurationSecs = 0.0d;
                if (segmentByterangeLength != -1) {
                    segmentByterangeOffset += segmentByterangeLength;
                }
                segmentByterangeLength = -1;
            } else if (line.equals(ENDLIST_TAG)) {
                live = false;
            }
        }
        return new HlsMediaPlaylist(baseUri, mediaSequence, targetDurationSecs, version, live, Collections.unmodifiableList(segments));
    }
}
