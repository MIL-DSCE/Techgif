package org.telegram.messenger.exoplayer.extractor.webm;

import android.util.Pair;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Universal;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.upstream.DefaultHttpDataSource;
import org.telegram.messenger.exoplayer.util.LongArray;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public final class WebmExtractor implements Extractor {
    private static final int BLOCK_STATE_DATA = 2;
    private static final int BLOCK_STATE_HEADER = 1;
    private static final int BLOCK_STATE_START = 0;
    private static final String CODEC_ID_AAC = "A_AAC";
    private static final String CODEC_ID_AC3 = "A_AC3";
    private static final String CODEC_ID_ACM = "A_MS/ACM";
    private static final String CODEC_ID_DTS = "A_DTS";
    private static final String CODEC_ID_DTS_EXPRESS = "A_DTS/EXPRESS";
    private static final String CODEC_ID_DTS_LOSSLESS = "A_DTS/LOSSLESS";
    private static final String CODEC_ID_E_AC3 = "A_EAC3";
    private static final String CODEC_ID_FLAC = "A_FLAC";
    private static final String CODEC_ID_FOURCC = "V_MS/VFW/FOURCC";
    private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
    private static final String CODEC_ID_H265 = "V_MPEGH/ISO/HEVC";
    private static final String CODEC_ID_MP3 = "A_MPEG/L3";
    private static final String CODEC_ID_MPEG2 = "V_MPEG2";
    private static final String CODEC_ID_MPEG4_AP = "V_MPEG4/ISO/AP";
    private static final String CODEC_ID_MPEG4_ASP = "V_MPEG4/ISO/ASP";
    private static final String CODEC_ID_MPEG4_SP = "V_MPEG4/ISO/SP";
    private static final String CODEC_ID_OPUS = "A_OPUS";
    private static final String CODEC_ID_PCM_INT_LIT = "A_PCM/INT/LIT";
    private static final String CODEC_ID_PGS = "S_HDMV/PGS";
    private static final String CODEC_ID_SUBRIP = "S_TEXT/UTF8";
    private static final String CODEC_ID_TRUEHD = "A_TRUEHD";
    private static final String CODEC_ID_VOBSUB = "S_VOBSUB";
    private static final String CODEC_ID_VORBIS = "A_VORBIS";
    private static final String CODEC_ID_VP8 = "V_VP8";
    private static final String CODEC_ID_VP9 = "V_VP9";
    private static final String DOC_TYPE_MATROSKA = "matroska";
    private static final String DOC_TYPE_WEBM = "webm";
    private static final int ENCRYPTION_IV_SIZE = 8;
    private static final int FOURCC_COMPRESSION_VC1 = 826496599;
    private static final int ID_AUDIO = 225;
    private static final int ID_AUDIO_BIT_DEPTH = 25188;
    private static final int ID_BLOCK = 161;
    private static final int ID_BLOCK_DURATION = 155;
    private static final int ID_BLOCK_GROUP = 160;
    private static final int ID_CHANNELS = 159;
    private static final int ID_CLUSTER = 524531317;
    private static final int ID_CODEC_DELAY = 22186;
    private static final int ID_CODEC_ID = 134;
    private static final int ID_CODEC_PRIVATE = 25506;
    private static final int ID_CONTENT_COMPRESSION = 20532;
    private static final int ID_CONTENT_COMPRESSION_ALGORITHM = 16980;
    private static final int ID_CONTENT_COMPRESSION_SETTINGS = 16981;
    private static final int ID_CONTENT_ENCODING = 25152;
    private static final int ID_CONTENT_ENCODINGS = 28032;
    private static final int ID_CONTENT_ENCODING_ORDER = 20529;
    private static final int ID_CONTENT_ENCODING_SCOPE = 20530;
    private static final int ID_CONTENT_ENCRYPTION = 20533;
    private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS = 18407;
    private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE = 18408;
    private static final int ID_CONTENT_ENCRYPTION_ALGORITHM = 18401;
    private static final int ID_CONTENT_ENCRYPTION_KEY_ID = 18402;
    private static final int ID_CUES = 475249515;
    private static final int ID_CUE_CLUSTER_POSITION = 241;
    private static final int ID_CUE_POINT = 187;
    private static final int ID_CUE_TIME = 179;
    private static final int ID_CUE_TRACK_POSITIONS = 183;
    private static final int ID_DEFAULT_DURATION = 2352003;
    private static final int ID_DISPLAY_HEIGHT = 21690;
    private static final int ID_DISPLAY_UNIT = 21682;
    private static final int ID_DISPLAY_WIDTH = 21680;
    private static final int ID_DOC_TYPE = 17026;
    private static final int ID_DOC_TYPE_READ_VERSION = 17029;
    private static final int ID_DURATION = 17545;
    private static final int ID_EBML = 440786851;
    private static final int ID_EBML_READ_VERSION = 17143;
    private static final int ID_INFO = 357149030;
    private static final int ID_LANGUAGE = 2274716;
    private static final int ID_PIXEL_HEIGHT = 186;
    private static final int ID_PIXEL_WIDTH = 176;
    private static final int ID_REFERENCE_BLOCK = 251;
    private static final int ID_SAMPLING_FREQUENCY = 181;
    private static final int ID_SEEK = 19899;
    private static final int ID_SEEK_HEAD = 290298740;
    private static final int ID_SEEK_ID = 21419;
    private static final int ID_SEEK_POSITION = 21420;
    private static final int ID_SEEK_PRE_ROLL = 22203;
    private static final int ID_SEGMENT = 408125543;
    private static final int ID_SEGMENT_INFO = 357149030;
    private static final int ID_SIMPLE_BLOCK = 163;
    private static final int ID_TIMECODE_SCALE = 2807729;
    private static final int ID_TIME_CODE = 231;
    private static final int ID_TRACKS = 374648427;
    private static final int ID_TRACK_ENTRY = 174;
    private static final int ID_TRACK_NUMBER = 215;
    private static final int ID_TRACK_TYPE = 131;
    private static final int ID_VIDEO = 224;
    private static final int LACING_EBML = 3;
    private static final int LACING_FIXED_SIZE = 2;
    private static final int LACING_NONE = 0;
    private static final int LACING_XIPH = 1;
    private static final int MP3_MAX_INPUT_SIZE = 4096;
    private static final int OPUS_MAX_INPUT_SIZE = 5760;
    private static final byte[] SUBRIP_PREFIX;
    private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
    private static final byte[] SUBRIP_TIMECODE_EMPTY;
    private static final int SUBRIP_TIMECODE_LENGTH = 12;
    private static final int TRACK_TYPE_AUDIO = 2;
    private static final int UNKNOWN = -1;
    private static final int VORBIS_MAX_INPUT_SIZE = 8192;
    private static final int WAVE_FORMAT_EXTENSIBLE = 65534;
    private static final int WAVE_FORMAT_PCM = 1;
    private static final int WAVE_FORMAT_SIZE = 18;
    private static final UUID WAVE_SUBFORMAT_PCM;
    private long blockDurationUs;
    private int blockFlags;
    private int blockLacingSampleCount;
    private int blockLacingSampleIndex;
    private int[] blockLacingSampleSizes;
    private int blockState;
    private long blockTimeUs;
    private int blockTrackNumber;
    private int blockTrackNumberLength;
    private long clusterTimecodeUs;
    private LongArray cueClusterPositions;
    private LongArray cueTimesUs;
    private long cuesContentPosition;
    private Track currentTrack;
    private long durationTimecode;
    private long durationUs;
    private ExtractorOutput extractorOutput;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private final EbmlReader reader;
    private int sampleBytesRead;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private boolean sampleEncodingHandled;
    private boolean sampleRead;
    private boolean sampleSeenReferenceBlock;
    private final ParsableByteArray sampleStrippedBytes;
    private final ParsableByteArray scratch;
    private int seekEntryId;
    private final ParsableByteArray seekEntryIdBytes;
    private long seekEntryPosition;
    private boolean seekForCues;
    private long seekPositionAfterBuildingCues;
    private boolean seenClusterPositionForCurrentCuePoint;
    private long segmentContentPosition;
    private long segmentContentSize;
    private boolean sentDrmInitData;
    private boolean sentSeekMap;
    private final ParsableByteArray subripSample;
    private long timecodeScale;
    private final SparseArray<Track> tracks;
    private final VarintReader varintReader;
    private final ParsableByteArray vorbisNumPageSamples;

    private static final class Track {
        private static final int DISPLAY_UNIT_PIXELS = 0;
        public int audioBitDepth;
        public int channelCount;
        public long codecDelayNs;
        public String codecId;
        public byte[] codecPrivate;
        public int defaultSampleDurationNs;
        public int displayHeight;
        public int displayUnit;
        public int displayWidth;
        public byte[] encryptionKeyId;
        public boolean hasContentEncryption;
        public int height;
        private String language;
        public int nalUnitLengthFieldLength;
        public int number;
        public TrackOutput output;
        public int sampleRate;
        public byte[] sampleStrippedBytes;
        public long seekPreRollNs;
        public int type;
        public int width;

        private Track() {
            this.width = WebmExtractor.UNKNOWN;
            this.height = WebmExtractor.UNKNOWN;
            this.displayWidth = WebmExtractor.UNKNOWN;
            this.displayHeight = WebmExtractor.UNKNOWN;
            this.displayUnit = WebmExtractor.LACING_NONE;
            this.channelCount = WebmExtractor.WAVE_FORMAT_PCM;
            this.audioBitDepth = WebmExtractor.UNKNOWN;
            this.sampleRate = DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS;
            this.codecDelayNs = 0;
            this.seekPreRollNs = 0;
            this.language = "eng";
        }

        public void initializeOutput(ExtractorOutput output, int trackId, long durationUs) throws ParserException {
            String mimeType;
            MediaFormat format;
            int maxInputSize = WebmExtractor.UNKNOWN;
            int pcmEncoding = WebmExtractor.UNKNOWN;
            List<byte[]> initializationData = null;
            String str = this.codecId;
            Object obj = WebmExtractor.UNKNOWN;
            switch (str.hashCode()) {
                case -2095576542:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG4_AP)) {
                        obj = 5;
                        break;
                    }
                    break;
                case -2095575984:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG4_SP)) {
                        obj = WebmExtractor.LACING_EBML;
                        break;
                    }
                    break;
                case -1985379776:
                    if (str.equals(WebmExtractor.CODEC_ID_ACM)) {
                        obj = 20;
                        break;
                    }
                    break;
                case -1784763192:
                    if (str.equals(WebmExtractor.CODEC_ID_TRUEHD)) {
                        obj = 15;
                        break;
                    }
                    break;
                case -1730367663:
                    if (str.equals(WebmExtractor.CODEC_ID_VORBIS)) {
                        obj = 9;
                        break;
                    }
                    break;
                case -1482641357:
                    if (str.equals(WebmExtractor.CODEC_ID_MP3)) {
                        obj = WebmExtractor.SUBRIP_TIMECODE_LENGTH;
                        break;
                    }
                    break;
                case -1373388978:
                    if (str.equals(WebmExtractor.CODEC_ID_FOURCC)) {
                        obj = WebmExtractor.ENCRYPTION_IV_SIZE;
                        break;
                    }
                    break;
                case -538363189:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG4_ASP)) {
                        obj = 4;
                        break;
                    }
                    break;
                case -538363109:
                    if (str.equals(WebmExtractor.CODEC_ID_H264)) {
                        obj = 6;
                        break;
                    }
                    break;
                case -425012669:
                    if (str.equals(WebmExtractor.CODEC_ID_VOBSUB)) {
                        obj = 23;
                        break;
                    }
                    break;
                case -356037306:
                    if (str.equals(WebmExtractor.CODEC_ID_DTS_LOSSLESS)) {
                        obj = WebmExtractor.WAVE_FORMAT_SIZE;
                        break;
                    }
                    break;
                case 62923557:
                    if (str.equals(WebmExtractor.CODEC_ID_AAC)) {
                        obj = 11;
                        break;
                    }
                    break;
                case 62923603:
                    if (str.equals(WebmExtractor.CODEC_ID_AC3)) {
                        obj = 13;
                        break;
                    }
                    break;
                case 62927045:
                    if (str.equals(WebmExtractor.CODEC_ID_DTS)) {
                        obj = 16;
                        break;
                    }
                    break;
                case 82338133:
                    if (str.equals(WebmExtractor.CODEC_ID_VP8)) {
                        obj = null;
                        break;
                    }
                    break;
                case 82338134:
                    if (str.equals(WebmExtractor.CODEC_ID_VP9)) {
                        obj = WebmExtractor.WAVE_FORMAT_PCM;
                        break;
                    }
                    break;
                case 99146302:
                    if (str.equals(WebmExtractor.CODEC_ID_PGS)) {
                        obj = 24;
                        break;
                    }
                    break;
                case 542569478:
                    if (str.equals(WebmExtractor.CODEC_ID_DTS_EXPRESS)) {
                        obj = 17;
                        break;
                    }
                    break;
                case 725957860:
                    if (str.equals(WebmExtractor.CODEC_ID_PCM_INT_LIT)) {
                        obj = 21;
                        break;
                    }
                    break;
                case 855502857:
                    if (str.equals(WebmExtractor.CODEC_ID_H265)) {
                        obj = 7;
                        break;
                    }
                    break;
                case 1422270023:
                    if (str.equals(WebmExtractor.CODEC_ID_SUBRIP)) {
                        obj = 22;
                        break;
                    }
                    break;
                case 1809237540:
                    if (str.equals(WebmExtractor.CODEC_ID_MPEG2)) {
                        obj = WebmExtractor.TRACK_TYPE_AUDIO;
                        break;
                    }
                    break;
                case 1950749482:
                    if (str.equals(WebmExtractor.CODEC_ID_E_AC3)) {
                        obj = 14;
                        break;
                    }
                    break;
                case 1950789798:
                    if (str.equals(WebmExtractor.CODEC_ID_FLAC)) {
                        obj = WebmExtractor.SUBRIP_PREFIX_END_TIMECODE_OFFSET;
                        break;
                    }
                    break;
                case 1951062397:
                    if (str.equals(WebmExtractor.CODEC_ID_OPUS)) {
                        obj = 10;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case WebmExtractor.LACING_NONE /*0*/:
                    mimeType = MimeTypes.VIDEO_VP8;
                    break;
                case WebmExtractor.WAVE_FORMAT_PCM /*1*/:
                    mimeType = MimeTypes.VIDEO_VP9;
                    break;
                case WebmExtractor.TRACK_TYPE_AUDIO /*2*/:
                    mimeType = MimeTypes.VIDEO_MPEG2;
                    break;
                case WebmExtractor.LACING_EBML /*3*/:
                case VideoPlayer.STATE_READY /*4*/:
                case VideoPlayer.STATE_ENDED /*5*/:
                    mimeType = MimeTypes.VIDEO_MP4V;
                    initializationData = this.codecPrivate == null ? null : Collections.singletonList(this.codecPrivate);
                    break;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    mimeType = MediaController.MIME_TYPE;
                    Pair<List<byte[]>, Integer> h264Data = parseAvcCodecPrivate(new ParsableByteArray(this.codecPrivate));
                    initializationData = h264Data.first;
                    this.nalUnitLengthFieldLength = ((Integer) h264Data.second).intValue();
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    mimeType = MimeTypes.VIDEO_H265;
                    Pair<List<byte[]>, Integer> hevcData = parseHevcCodecPrivate(new ParsableByteArray(this.codecPrivate));
                    initializationData = hevcData.first;
                    this.nalUnitLengthFieldLength = ((Integer) hevcData.second).intValue();
                    break;
                case WebmExtractor.ENCRYPTION_IV_SIZE /*8*/:
                    mimeType = MimeTypes.VIDEO_VC1;
                    initializationData = parseFourCcVc1Private(new ParsableByteArray(this.codecPrivate));
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    mimeType = MimeTypes.AUDIO_VORBIS;
                    maxInputSize = WebmExtractor.VORBIS_MAX_INPUT_SIZE;
                    initializationData = parseVorbisCodecPrivate(this.codecPrivate);
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    mimeType = MimeTypes.AUDIO_OPUS;
                    maxInputSize = WebmExtractor.OPUS_MAX_INPUT_SIZE;
                    initializationData = new ArrayList(WebmExtractor.LACING_EBML);
                    initializationData.add(this.codecPrivate);
                    initializationData.add(ByteBuffer.allocate(WebmExtractor.ENCRYPTION_IV_SIZE).order(ByteOrder.nativeOrder()).putLong(this.codecDelayNs).array());
                    initializationData.add(ByteBuffer.allocate(WebmExtractor.ENCRYPTION_IV_SIZE).order(ByteOrder.nativeOrder()).putLong(this.seekPreRollNs).array());
                    break;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    mimeType = MimeTypes.AUDIO_AAC;
                    initializationData = Collections.singletonList(this.codecPrivate);
                    break;
                case WebmExtractor.SUBRIP_TIMECODE_LENGTH /*12*/:
                    mimeType = MimeTypes.AUDIO_MPEG;
                    maxInputSize = WebmExtractor.MP3_MAX_INPUT_SIZE;
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    mimeType = MimeTypes.AUDIO_AC3;
                    break;
                case ConnectionResult.TIMEOUT /*14*/:
                    mimeType = MimeTypes.AUDIO_E_AC3;
                    break;
                case ConnectionResult.INTERRUPTED /*15*/:
                    mimeType = MimeTypes.AUDIO_TRUEHD;
                    break;
                case ItemTouchHelper.START /*16*/:
                case ConnectionResult.SIGN_IN_FAILED /*17*/:
                    mimeType = MimeTypes.AUDIO_DTS;
                    break;
                case WebmExtractor.WAVE_FORMAT_SIZE /*18*/:
                    mimeType = MimeTypes.AUDIO_DTS_HD;
                    break;
                case WebmExtractor.SUBRIP_PREFIX_END_TIMECODE_OFFSET /*19*/:
                    mimeType = MimeTypes.AUDIO_FLAC;
                    initializationData = Collections.singletonList(this.codecPrivate);
                    break;
                case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                    mimeType = MimeTypes.AUDIO_RAW;
                    if (parseMsAcmCodecPrivate(new ParsableByteArray(this.codecPrivate))) {
                        pcmEncoding = Util.getPcmEncoding(this.audioBitDepth);
                        if (pcmEncoding == 0) {
                            throw new ParserException("Unsupported PCM bit depth: " + this.audioBitDepth);
                        }
                    }
                    throw new ParserException("Non-PCM MS/ACM is unsupported");
                    break;
                case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                    mimeType = MimeTypes.AUDIO_RAW;
                    pcmEncoding = Util.getPcmEncoding(this.audioBitDepth);
                    if (pcmEncoding == 0) {
                        throw new ParserException("Unsupported PCM bit depth: " + this.audioBitDepth);
                    }
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL22 /*22*/:
                    mimeType = MimeTypes.APPLICATION_SUBRIP;
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL23 /*23*/:
                    mimeType = MimeTypes.APPLICATION_VOBSUB;
                    initializationData = Collections.singletonList(this.codecPrivate);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL24 /*24*/:
                    mimeType = MimeTypes.APPLICATION_PGS;
                    break;
                default:
                    throw new ParserException("Unrecognized codec identifier.");
            }
            if (MimeTypes.isAudio(mimeType)) {
                format = MediaFormat.createAudioFormat(Integer.toString(trackId), mimeType, WebmExtractor.UNKNOWN, maxInputSize, durationUs, this.channelCount, this.sampleRate, initializationData, this.language, pcmEncoding);
            } else if (MimeTypes.isVideo(mimeType)) {
                if (this.displayUnit == 0) {
                    this.displayWidth = this.displayWidth == WebmExtractor.UNKNOWN ? this.width : this.displayWidth;
                    this.displayHeight = this.displayHeight == WebmExtractor.UNKNOWN ? this.height : this.displayHeight;
                }
                float pixelWidthHeightRatio = GroundOverlayOptions.NO_DIMENSION;
                if (!(this.displayWidth == WebmExtractor.UNKNOWN || this.displayHeight == WebmExtractor.UNKNOWN)) {
                    pixelWidthHeightRatio = ((float) (this.height * this.displayWidth)) / ((float) (this.width * this.displayHeight));
                }
                format = MediaFormat.createVideoFormat(Integer.toString(trackId), mimeType, WebmExtractor.UNKNOWN, maxInputSize, durationUs, this.width, this.height, initializationData, WebmExtractor.UNKNOWN, pixelWidthHeightRatio);
            } else if (MimeTypes.APPLICATION_SUBRIP.equals(mimeType)) {
                format = MediaFormat.createTextFormat(Integer.toString(trackId), mimeType, WebmExtractor.UNKNOWN, durationUs, this.language);
            } else if (MimeTypes.APPLICATION_VOBSUB.equals(mimeType) || MimeTypes.APPLICATION_PGS.equals(mimeType)) {
                format = MediaFormat.createImageFormat(Integer.toString(trackId), mimeType, WebmExtractor.UNKNOWN, durationUs, initializationData, this.language);
            } else {
                throw new ParserException("Unexpected MIME type.");
            }
            this.output = output.track(this.number);
            this.output.format(format);
        }

        private static List<byte[]> parseFourCcVc1Private(ParsableByteArray buffer) throws ParserException {
            try {
                buffer.skipBytes(16);
                long compression = buffer.readLittleEndianUnsignedInt();
                if (compression != 826496599) {
                    throw new ParserException("Unsupported FourCC compression type: " + compression);
                }
                int startOffset = buffer.getPosition() + 20;
                byte[] bufferData = buffer.data;
                int offset = startOffset;
                while (offset < bufferData.length - 4) {
                    if (bufferData[offset] == null && bufferData[offset + WebmExtractor.WAVE_FORMAT_PCM] == null && bufferData[offset + WebmExtractor.TRACK_TYPE_AUDIO] == WebmExtractor.WAVE_FORMAT_PCM && bufferData[offset + WebmExtractor.LACING_EBML] == 15) {
                        return Collections.singletonList(Arrays.copyOfRange(bufferData, offset, bufferData.length));
                    }
                    offset += WebmExtractor.WAVE_FORMAT_PCM;
                }
                throw new ParserException("Failed to find FourCC VC1 initialization data");
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing FourCC VC1 codec private");
            }
        }

        private static Pair<List<byte[]>, Integer> parseAvcCodecPrivate(ParsableByteArray buffer) throws ParserException {
            try {
                buffer.setPosition(4);
                int nalUnitLengthFieldLength = (buffer.readUnsignedByte() & WebmExtractor.LACING_EBML) + WebmExtractor.WAVE_FORMAT_PCM;
                if (nalUnitLengthFieldLength == WebmExtractor.LACING_EBML) {
                    throw new ParserException();
                }
                List<byte[]> initializationData = new ArrayList();
                int numSequenceParameterSets = buffer.readUnsignedByte() & 31;
                for (int i = WebmExtractor.LACING_NONE; i < numSequenceParameterSets; i += WebmExtractor.WAVE_FORMAT_PCM) {
                    initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
                }
                int numPictureParameterSets = buffer.readUnsignedByte();
                for (int j = WebmExtractor.LACING_NONE; j < numPictureParameterSets; j += WebmExtractor.WAVE_FORMAT_PCM) {
                    initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
                }
                return Pair.create(initializationData, Integer.valueOf(nalUnitLengthFieldLength));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing AVC codec private");
            }
        }

        private static Pair<List<byte[]>, Integer> parseHevcCodecPrivate(ParsableByteArray parent) throws ParserException {
            try {
                int i;
                int numberOfNalUnits;
                int j;
                int nalUnitLength;
                parent.setPosition(21);
                int lengthSizeMinusOne = parent.readUnsignedByte() & WebmExtractor.LACING_EBML;
                int numberOfArrays = parent.readUnsignedByte();
                int csdLength = WebmExtractor.LACING_NONE;
                int csdStartPosition = parent.getPosition();
                for (i = WebmExtractor.LACING_NONE; i < numberOfArrays; i += WebmExtractor.WAVE_FORMAT_PCM) {
                    parent.skipBytes(WebmExtractor.WAVE_FORMAT_PCM);
                    numberOfNalUnits = parent.readUnsignedShort();
                    for (j = WebmExtractor.LACING_NONE; j < numberOfNalUnits; j += WebmExtractor.WAVE_FORMAT_PCM) {
                        nalUnitLength = parent.readUnsignedShort();
                        csdLength += nalUnitLength + 4;
                        parent.skipBytes(nalUnitLength);
                    }
                }
                parent.setPosition(csdStartPosition);
                byte[] buffer = new byte[csdLength];
                int bufferPosition = WebmExtractor.LACING_NONE;
                for (i = WebmExtractor.LACING_NONE; i < numberOfArrays; i += WebmExtractor.WAVE_FORMAT_PCM) {
                    parent.skipBytes(WebmExtractor.WAVE_FORMAT_PCM);
                    numberOfNalUnits = parent.readUnsignedShort();
                    for (j = WebmExtractor.LACING_NONE; j < numberOfNalUnits; j += WebmExtractor.WAVE_FORMAT_PCM) {
                        nalUnitLength = parent.readUnsignedShort();
                        System.arraycopy(NalUnitUtil.NAL_START_CODE, WebmExtractor.LACING_NONE, buffer, bufferPosition, NalUnitUtil.NAL_START_CODE.length);
                        bufferPosition += NalUnitUtil.NAL_START_CODE.length;
                        System.arraycopy(parent.data, parent.getPosition(), buffer, bufferPosition, nalUnitLength);
                        bufferPosition += nalUnitLength;
                        parent.skipBytes(nalUnitLength);
                    }
                }
                return Pair.create(csdLength == 0 ? null : Collections.singletonList(buffer), Integer.valueOf(lengthSizeMinusOne + WebmExtractor.WAVE_FORMAT_PCM));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing HEVC codec private");
            }
        }

        private static List<byte[]> parseVorbisCodecPrivate(byte[] codecPrivate) throws ParserException {
            try {
                if (codecPrivate[WebmExtractor.LACING_NONE] != WebmExtractor.TRACK_TYPE_AUDIO) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                int vorbisInfoLength = WebmExtractor.LACING_NONE;
                int offset = WebmExtractor.WAVE_FORMAT_PCM;
                while (codecPrivate[offset] == (byte) -1) {
                    vorbisInfoLength += NalUnitUtil.EXTENDED_SAR;
                    offset += WebmExtractor.WAVE_FORMAT_PCM;
                }
                int offset2 = offset + WebmExtractor.WAVE_FORMAT_PCM;
                vorbisInfoLength += codecPrivate[offset];
                int vorbisSkipLength = WebmExtractor.LACING_NONE;
                offset = offset2;
                while (codecPrivate[offset] == (byte) -1) {
                    vorbisSkipLength += NalUnitUtil.EXTENDED_SAR;
                    offset += WebmExtractor.WAVE_FORMAT_PCM;
                }
                offset2 = offset + WebmExtractor.WAVE_FORMAT_PCM;
                vorbisSkipLength += codecPrivate[offset];
                if (codecPrivate[offset2] != WebmExtractor.WAVE_FORMAT_PCM) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                byte[] vorbisInfo = new byte[vorbisInfoLength];
                System.arraycopy(codecPrivate, offset2, vorbisInfo, WebmExtractor.LACING_NONE, vorbisInfoLength);
                offset2 += vorbisInfoLength;
                if (codecPrivate[offset2] != WebmExtractor.LACING_EBML) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                offset2 += vorbisSkipLength;
                if (codecPrivate[offset2] != 5) {
                    throw new ParserException("Error parsing vorbis codec private");
                }
                byte[] vorbisBooks = new byte[(codecPrivate.length - offset2)];
                System.arraycopy(codecPrivate, offset2, vorbisBooks, WebmExtractor.LACING_NONE, codecPrivate.length - offset2);
                List<byte[]> initializationData = new ArrayList(WebmExtractor.TRACK_TYPE_AUDIO);
                initializationData.add(vorbisInfo);
                initializationData.add(vorbisBooks);
                return initializationData;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing vorbis codec private");
            }
        }

        private static boolean parseMsAcmCodecPrivate(ParsableByteArray buffer) throws ParserException {
            try {
                int formatTag = buffer.readLittleEndianUnsignedShort();
                if (formatTag == WebmExtractor.WAVE_FORMAT_PCM) {
                    return true;
                }
                if (formatTag != WebmExtractor.WAVE_FORMAT_EXTENSIBLE) {
                    return false;
                }
                buffer.setPosition(24);
                if (buffer.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits() && buffer.readLong() == WebmExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits()) {
                    return true;
                }
                return false;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing MS/ACM codec private");
            }
        }
    }

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public int getElementType(int id) {
            return WebmExtractor.this.getElementType(id);
        }

        public boolean isLevel1Element(int id) {
            return WebmExtractor.this.isLevel1Element(id);
        }

        public void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
            WebmExtractor.this.startMasterElement(id, contentPosition, contentSize);
        }

        public void endMasterElement(int id) throws ParserException {
            WebmExtractor.this.endMasterElement(id);
        }

        public void integerElement(int id, long value) throws ParserException {
            WebmExtractor.this.integerElement(id, value);
        }

        public void floatElement(int id, double value) throws ParserException {
            WebmExtractor.this.floatElement(id, value);
        }

        public void stringElement(int id, String value) throws ParserException {
            WebmExtractor.this.stringElement(id, value);
        }

        public void binaryElement(int id, int contentsSize, ExtractorInput input) throws IOException, InterruptedException {
            WebmExtractor.this.binaryElement(id, contentsSize, input);
        }
    }

    static {
        SUBRIP_PREFIX = new byte[]{(byte) 49, (byte) 10, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, ClosedCaptionCtrl.ERASE_DISPLAYED_MEMORY, (byte) 48, (byte) 48, (byte) 48, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.CARRIAGE_RETURN, ClosedCaptionCtrl.CARRIAGE_RETURN, (byte) 62, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, ClosedCaptionCtrl.ERASE_DISPLAYED_MEMORY, (byte) 48, (byte) 48, (byte) 48, (byte) 10};
        SUBRIP_TIMECODE_EMPTY = new byte[]{ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.RESUME_CAPTION_LOADING};
        WAVE_SUBFORMAT_PCM = new UUID(72057594037932032L, -9223371306706625679L);
    }

    public WebmExtractor() {
        this(new DefaultEbmlReader());
    }

    WebmExtractor(EbmlReader reader) {
        this.segmentContentPosition = -1;
        this.segmentContentSize = -1;
        this.timecodeScale = -1;
        this.durationTimecode = -1;
        this.durationUs = -1;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = -1;
        this.reader = reader;
        this.reader.init(new InnerEbmlReaderOutput());
        this.varintReader = new VarintReader();
        this.tracks = new SparseArray();
        this.scratch = new ParsableByteArray(4);
        this.vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(UNKNOWN).array());
        this.seekEntryIdBytes = new ParsableByteArray(4);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.sampleStrippedBytes = new ParsableByteArray();
        this.subripSample = new ParsableByteArray();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return new Sniffer().sniff(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek() {
        this.clusterTimecodeUs = -1;
        this.blockState = LACING_NONE;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        this.sampleRead = false;
        boolean continueReading = true;
        while (continueReading && !this.sampleRead) {
            continueReading = this.reader.read(input);
            if (continueReading && maybeSeekForCues(seekPosition, input.getPosition())) {
                return WAVE_FORMAT_PCM;
            }
        }
        if (continueReading) {
            return LACING_NONE;
        }
        return UNKNOWN;
    }

    int getElementType(int id) {
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
            case ID_BLOCK_DURATION /*155*/:
            case ID_CHANNELS /*159*/:
            case ID_PIXEL_WIDTH /*176*/:
            case ID_CUE_TIME /*179*/:
            case ID_PIXEL_HEIGHT /*186*/:
            case ID_TRACK_NUMBER /*215*/:
            case ID_TIME_CODE /*231*/:
            case ID_CUE_CLUSTER_POSITION /*241*/:
            case ID_REFERENCE_BLOCK /*251*/:
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
            case ID_EBML_READ_VERSION /*17143*/:
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
            case ID_SEEK_POSITION /*21420*/:
            case ID_DISPLAY_WIDTH /*21680*/:
            case ID_DISPLAY_UNIT /*21682*/:
            case ID_DISPLAY_HEIGHT /*21690*/:
            case ID_CODEC_DELAY /*22186*/:
            case ID_SEEK_PRE_ROLL /*22203*/:
            case ID_AUDIO_BIT_DEPTH /*25188*/:
            case ID_DEFAULT_DURATION /*2352003*/:
            case ID_TIMECODE_SCALE /*2807729*/:
                return TRACK_TYPE_AUDIO;
            case ID_CODEC_ID /*134*/:
            case ID_DOC_TYPE /*17026*/:
            case ID_LANGUAGE /*2274716*/:
                return LACING_EBML;
            case ID_BLOCK_GROUP /*160*/:
            case ID_TRACK_ENTRY /*174*/:
            case ID_CUE_TRACK_POSITIONS /*183*/:
            case ID_CUE_POINT /*187*/:
            case ID_VIDEO /*224*/:
            case ID_AUDIO /*225*/:
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS /*18407*/:
            case ID_SEEK /*19899*/:
            case ID_CONTENT_COMPRESSION /*20532*/:
            case ID_CONTENT_ENCRYPTION /*20533*/:
            case ID_CONTENT_ENCODING /*25152*/:
            case ID_CONTENT_ENCODINGS /*28032*/:
            case ID_SEEK_HEAD /*290298740*/:
            case ID_SEGMENT_INFO /*357149030*/:
            case ID_TRACKS /*374648427*/:
            case ID_SEGMENT /*408125543*/:
            case ID_EBML /*440786851*/:
            case ID_CUES /*475249515*/:
            case ID_CLUSTER /*524531317*/:
                return WAVE_FORMAT_PCM;
            case ID_BLOCK /*161*/:
            case ID_SIMPLE_BLOCK /*163*/:
            case ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
            case ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
            case ID_SEEK_ID /*21419*/:
            case ID_CODEC_PRIVATE /*25506*/:
                return 4;
            case ID_SAMPLING_FREQUENCY /*181*/:
            case ID_DURATION /*17545*/:
                return 5;
            default:
                return LACING_NONE;
        }
    }

    boolean isLevel1Element(int id) {
        return id == ID_SEGMENT_INFO || id == ID_CLUSTER || id == ID_CUES || id == ID_TRACKS;
    }

    void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
        switch (id) {
            case ID_BLOCK_GROUP /*160*/:
                this.sampleSeenReferenceBlock = false;
            case ID_TRACK_ENTRY /*174*/:
                this.currentTrack = new Track();
            case ID_CUE_POINT /*187*/:
                this.seenClusterPositionForCurrentCuePoint = false;
            case ID_SEEK /*19899*/:
                this.seekEntryId = UNKNOWN;
                this.seekEntryPosition = -1;
            case ID_CONTENT_ENCRYPTION /*20533*/:
                this.currentTrack.hasContentEncryption = true;
            case ID_SEGMENT /*408125543*/:
                if (this.segmentContentPosition == -1 || this.segmentContentPosition == contentPosition) {
                    this.segmentContentPosition = contentPosition;
                    this.segmentContentSize = contentSize;
                    return;
                }
                throw new ParserException("Multiple Segment elements not supported");
            case ID_CUES /*475249515*/:
                this.cueTimesUs = new LongArray();
                this.cueClusterPositions = new LongArray();
            case ID_CLUSTER /*524531317*/:
                if (!this.sentSeekMap) {
                    if (this.cuesContentPosition != -1) {
                        this.seekForCues = true;
                        return;
                    }
                    this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                    this.sentSeekMap = true;
                }
            default:
        }
    }

    void endMasterElement(int id) throws ParserException {
        switch (id) {
            case ID_BLOCK_GROUP /*160*/:
                if (this.blockState == TRACK_TYPE_AUDIO) {
                    if (!this.sampleSeenReferenceBlock) {
                        this.blockFlags |= WAVE_FORMAT_PCM;
                    }
                    commitSampleToOutput((Track) this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
                    this.blockState = LACING_NONE;
                }
            case ID_TRACK_ENTRY /*174*/:
                if (this.tracks.get(this.currentTrack.number) == null && isCodecSupported(this.currentTrack.codecId)) {
                    this.currentTrack.initializeOutput(this.extractorOutput, this.currentTrack.number, this.durationUs);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                }
                this.currentTrack = null;
            case ID_SEEK /*19899*/:
                if (this.seekEntryId == UNKNOWN || this.seekEntryPosition == -1) {
                    throw new ParserException("Mandatory element SeekID or SeekPosition not found");
                } else if (this.seekEntryId == ID_CUES) {
                    this.cuesContentPosition = this.seekEntryPosition;
                }
            case ID_CONTENT_ENCODING /*25152*/:
                if (!this.currentTrack.hasContentEncryption) {
                    return;
                }
                if (this.currentTrack.encryptionKeyId == null) {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                } else if (!this.sentDrmInitData) {
                    this.extractorOutput.drmInitData(new Universal(new SchemeInitData(MimeTypes.VIDEO_WEBM, this.currentTrack.encryptionKeyId)));
                    this.sentDrmInitData = true;
                }
            case ID_CONTENT_ENCODINGS /*28032*/:
                if (this.currentTrack.hasContentEncryption && this.currentTrack.sampleStrippedBytes != null) {
                    throw new ParserException("Combining encryption and compression is not supported");
                }
            case ID_SEGMENT_INFO /*357149030*/:
                if (this.timecodeScale == -1) {
                    this.timecodeScale = C0747C.MICROS_PER_SECOND;
                }
                if (this.durationTimecode != -1) {
                    this.durationUs = scaleTimecodeToUs(this.durationTimecode);
                }
            case ID_TRACKS /*374648427*/:
                if (this.tracks.size() == 0) {
                    throw new ParserException("No valid tracks were found");
                }
                this.extractorOutput.endTracks();
            case ID_CUES /*475249515*/:
                if (!this.sentSeekMap) {
                    this.extractorOutput.seekMap(buildSeekMap());
                    this.sentSeekMap = true;
                }
            default:
        }
    }

    void integerElement(int id, long value) throws ParserException {
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) value;
            case ID_BLOCK_DURATION /*155*/:
                this.blockDurationUs = scaleTimecodeToUs(value);
            case ID_CHANNELS /*159*/:
                this.currentTrack.channelCount = (int) value;
            case ID_PIXEL_WIDTH /*176*/:
                this.currentTrack.width = (int) value;
            case ID_CUE_TIME /*179*/:
                this.cueTimesUs.add(scaleTimecodeToUs(value));
            case ID_PIXEL_HEIGHT /*186*/:
                this.currentTrack.height = (int) value;
            case ID_TRACK_NUMBER /*215*/:
                this.currentTrack.number = (int) value;
            case ID_TIME_CODE /*231*/:
                this.clusterTimecodeUs = scaleTimecodeToUs(value);
            case ID_CUE_CLUSTER_POSITION /*241*/:
                if (!this.seenClusterPositionForCurrentCuePoint) {
                    this.cueClusterPositions.add(value);
                    this.seenClusterPositionForCurrentCuePoint = true;
                }
            case ID_REFERENCE_BLOCK /*251*/:
                this.sampleSeenReferenceBlock = true;
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                if (value != 3) {
                    throw new ParserException("ContentCompAlgo " + value + " not supported");
                }
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
                if (value < 1 || value > 2) {
                    throw new ParserException("DocTypeReadVersion " + value + " not supported");
                }
            case ID_EBML_READ_VERSION /*17143*/:
                if (value != 1) {
                    throw new ParserException("EBMLReadVersion " + value + " not supported");
                }
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                if (value != 5) {
                    throw new ParserException("ContentEncAlgo " + value + " not supported");
                }
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                if (value != 1) {
                    throw new ParserException("AESSettingsCipherMode " + value + " not supported");
                }
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
                if (value != 0) {
                    throw new ParserException("ContentEncodingOrder " + value + " not supported");
                }
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
                if (value != 1) {
                    throw new ParserException("ContentEncodingScope " + value + " not supported");
                }
            case ID_SEEK_POSITION /*21420*/:
                this.seekEntryPosition = this.segmentContentPosition + value;
            case ID_DISPLAY_WIDTH /*21680*/:
                this.currentTrack.displayWidth = (int) value;
            case ID_DISPLAY_UNIT /*21682*/:
                this.currentTrack.displayUnit = (int) value;
            case ID_DISPLAY_HEIGHT /*21690*/:
                this.currentTrack.displayHeight = (int) value;
            case ID_CODEC_DELAY /*22186*/:
                this.currentTrack.codecDelayNs = value;
            case ID_SEEK_PRE_ROLL /*22203*/:
                this.currentTrack.seekPreRollNs = value;
            case ID_AUDIO_BIT_DEPTH /*25188*/:
                this.currentTrack.audioBitDepth = (int) value;
            case ID_DEFAULT_DURATION /*2352003*/:
                this.currentTrack.defaultSampleDurationNs = (int) value;
            case ID_TIMECODE_SCALE /*2807729*/:
                this.timecodeScale = value;
            default:
        }
    }

    void floatElement(int id, double value) {
        switch (id) {
            case ID_SAMPLING_FREQUENCY /*181*/:
                this.currentTrack.sampleRate = (int) value;
            case ID_DURATION /*17545*/:
                this.durationTimecode = (long) value;
            default:
        }
    }

    void stringElement(int id, String value) throws ParserException {
        switch (id) {
            case ID_CODEC_ID /*134*/:
                this.currentTrack.codecId = value;
            case ID_DOC_TYPE /*17026*/:
                if (!DOC_TYPE_WEBM.equals(value) && !DOC_TYPE_MATROSKA.equals(value)) {
                    throw new ParserException("DocType " + value + " not supported");
                }
            case ID_LANGUAGE /*2274716*/:
                this.currentTrack.language = value;
            default:
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void binaryElement(int r31, int r32, org.telegram.messenger.exoplayer.extractor.ExtractorInput r33) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r30 = this;
        switch(r31) {
            case 161: goto L_0x00f0;
            case 163: goto L_0x00f0;
            case 16981: goto L_0x0097;
            case 18402: goto L_0x00c3;
            case 21419: goto L_0x0020;
            case 25506: goto L_0x006b;
            default: goto L_0x0003;
        };
    L_0x0003:
        r25 = new org.telegram.messenger.exoplayer.ParserException;
        r26 = new java.lang.StringBuilder;
        r26.<init>();
        r27 = "Unexpected id: ";
        r26 = r26.append(r27);
        r0 = r26;
        r1 = r31;
        r26 = r0.append(r1);
        r26 = r26.toString();
        r25.<init>(r26);
        throw r25;
    L_0x0020:
        r0 = r30;
        r0 = r0.seekEntryIdBytes;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 0;
        java.util.Arrays.fill(r25, r26);
        r0 = r30;
        r0 = r0.seekEntryIdBytes;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 4 - r32;
        r0 = r33;
        r1 = r25;
        r2 = r26;
        r3 = r32;
        r0.readFully(r1, r2, r3);
        r0 = r30;
        r0 = r0.seekEntryIdBytes;
        r25 = r0;
        r26 = 0;
        r25.setPosition(r26);
        r0 = r30;
        r0 = r0.seekEntryIdBytes;
        r25 = r0;
        r26 = r25.readUnsignedInt();
        r0 = r26;
        r0 = (int) r0;
        r25 = r0;
        r0 = r25;
        r1 = r30;
        r1.seekEntryId = r0;
    L_0x006a:
        return;
    L_0x006b:
        r0 = r30;
        r0 = r0.currentTrack;
        r25 = r0;
        r0 = r32;
        r0 = new byte[r0];
        r26 = r0;
        r0 = r26;
        r1 = r25;
        r1.codecPrivate = r0;
        r0 = r30;
        r0 = r0.currentTrack;
        r25 = r0;
        r0 = r25;
        r0 = r0.codecPrivate;
        r25 = r0;
        r26 = 0;
        r0 = r33;
        r1 = r25;
        r2 = r26;
        r3 = r32;
        r0.readFully(r1, r2, r3);
        goto L_0x006a;
    L_0x0097:
        r0 = r30;
        r0 = r0.currentTrack;
        r25 = r0;
        r0 = r32;
        r0 = new byte[r0];
        r26 = r0;
        r0 = r26;
        r1 = r25;
        r1.sampleStrippedBytes = r0;
        r0 = r30;
        r0 = r0.currentTrack;
        r25 = r0;
        r0 = r25;
        r0 = r0.sampleStrippedBytes;
        r25 = r0;
        r26 = 0;
        r0 = r33;
        r1 = r25;
        r2 = r26;
        r3 = r32;
        r0.readFully(r1, r2, r3);
        goto L_0x006a;
    L_0x00c3:
        r0 = r30;
        r0 = r0.currentTrack;
        r25 = r0;
        r0 = r32;
        r0 = new byte[r0];
        r26 = r0;
        r0 = r26;
        r1 = r25;
        r1.encryptionKeyId = r0;
        r0 = r30;
        r0 = r0.currentTrack;
        r25 = r0;
        r0 = r25;
        r0 = r0.encryptionKeyId;
        r25 = r0;
        r26 = 0;
        r0 = r33;
        r1 = r25;
        r2 = r26;
        r3 = r32;
        r0.readFully(r1, r2, r3);
        goto L_0x006a;
    L_0x00f0:
        r0 = r30;
        r0 = r0.blockState;
        r25 = r0;
        if (r25 != 0) goto L_0x0146;
    L_0x00f8:
        r0 = r30;
        r0 = r0.varintReader;
        r25 = r0;
        r26 = 0;
        r27 = 1;
        r28 = 8;
        r0 = r25;
        r1 = r33;
        r2 = r26;
        r3 = r27;
        r4 = r28;
        r26 = r0.readUnsignedVarint(r1, r2, r3, r4);
        r0 = r26;
        r0 = (int) r0;
        r25 = r0;
        r0 = r25;
        r1 = r30;
        r1.blockTrackNumber = r0;
        r0 = r30;
        r0 = r0.varintReader;
        r25 = r0;
        r25 = r25.getLastLength();
        r0 = r25;
        r1 = r30;
        r1.blockTrackNumberLength = r0;
        r26 = -1;
        r0 = r26;
        r2 = r30;
        r2.blockDurationUs = r0;
        r25 = 1;
        r0 = r25;
        r1 = r30;
        r1.blockState = r0;
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r25.reset();
    L_0x0146:
        r0 = r30;
        r0 = r0.tracks;
        r25 = r0;
        r0 = r30;
        r0 = r0.blockTrackNumber;
        r26 = r0;
        r24 = r25.get(r26);
        r24 = (org.telegram.messenger.exoplayer.extractor.webm.WebmExtractor.Track) r24;
        if (r24 != 0) goto L_0x0173;
    L_0x015a:
        r0 = r30;
        r0 = r0.blockTrackNumberLength;
        r25 = r0;
        r25 = r32 - r25;
        r0 = r33;
        r1 = r25;
        r0.skipFully(r1);
        r25 = 0;
        r0 = r25;
        r1 = r30;
        r1.blockState = r0;
        goto L_0x006a;
    L_0x0173:
        r0 = r30;
        r0 = r0.blockState;
        r25 = r0;
        r26 = 1;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x0287;
    L_0x0181:
        r25 = 3;
        r0 = r30;
        r1 = r33;
        r2 = r25;
        r0.readScratch(r1, r2);
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 2;
        r25 = r25[r26];
        r25 = r25 & 6;
        r13 = r25 >> 1;
        if (r13 != 0) goto L_0x02f3;
    L_0x01a2:
        r25 = 1;
        r0 = r25;
        r1 = r30;
        r1.blockLacingSampleCount = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = 1;
        r25 = ensureArrayCapacity(r25, r26);
        r0 = r25;
        r1 = r30;
        r1.blockLacingSampleSizes = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = 0;
        r0 = r30;
        r0 = r0.blockTrackNumberLength;
        r27 = r0;
        r27 = r32 - r27;
        r27 = r27 + -3;
        r25[r26] = r27;
    L_0x01d0:
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 0;
        r25 = r25[r26];
        r25 = r25 << 8;
        r0 = r30;
        r0 = r0.scratch;
        r26 = r0;
        r0 = r26;
        r0 = r0.data;
        r26 = r0;
        r27 = 1;
        r26 = r26[r27];
        r0 = r26;
        r0 = r0 & 255;
        r26 = r0;
        r22 = r25 | r26;
        r0 = r30;
        r0 = r0.clusterTimecodeUs;
        r26 = r0;
        r0 = r22;
        r0 = (long) r0;
        r28 = r0;
        r0 = r30;
        r1 = r28;
        r28 = r0.scaleTimecodeToUs(r1);
        r26 = r26 + r28;
        r0 = r26;
        r2 = r30;
        r2.blockTimeUs = r0;
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 2;
        r25 = r25[r26];
        r25 = r25 & 8;
        r26 = 8;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x0535;
    L_0x022f:
        r11 = 1;
    L_0x0230:
        r0 = r24;
        r0 = r0.type;
        r25 = r0;
        r26 = 2;
        r0 = r25;
        r1 = r26;
        if (r0 == r1) goto L_0x0264;
    L_0x023e:
        r25 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        r0 = r31;
        r1 = r25;
        if (r0 != r1) goto L_0x0538;
    L_0x0246:
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 2;
        r25 = r25[r26];
        r0 = r25;
        r0 = r0 & 128;
        r25 = r0;
        r26 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x0538;
    L_0x0264:
        r12 = 1;
    L_0x0265:
        if (r12 == 0) goto L_0x053b;
    L_0x0267:
        r25 = 1;
        r26 = r25;
    L_0x026b:
        if (r11 == 0) goto L_0x0541;
    L_0x026d:
        r25 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
    L_0x026f:
        r25 = r25 | r26;
        r0 = r25;
        r1 = r30;
        r1.blockFlags = r0;
        r25 = 2;
        r0 = r25;
        r1 = r30;
        r1.blockState = r0;
        r25 = 0;
        r0 = r25;
        r1 = r30;
        r1.blockLacingSampleIndex = r0;
    L_0x0287:
        r25 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        r0 = r31;
        r1 = r25;
        if (r0 != r1) goto L_0x054f;
    L_0x028f:
        r0 = r30;
        r0 = r0.blockLacingSampleIndex;
        r25 = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        if (r0 >= r1) goto L_0x0545;
    L_0x02a1:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleIndex;
        r26 = r0;
        r25 = r25[r26];
        r0 = r30;
        r1 = r33;
        r2 = r24;
        r3 = r25;
        r0.writeSampleData(r1, r2, r3);
        r0 = r30;
        r0 = r0.blockTimeUs;
        r26 = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleIndex;
        r25 = r0;
        r0 = r24;
        r0 = r0.defaultSampleDurationNs;
        r28 = r0;
        r25 = r25 * r28;
        r0 = r25;
        r0 = r0 / 1000;
        r25 = r0;
        r0 = r25;
        r0 = (long) r0;
        r28 = r0;
        r20 = r26 + r28;
        r0 = r30;
        r1 = r24;
        r2 = r20;
        r0.commitSampleToOutput(r1, r2);
        r0 = r30;
        r0 = r0.blockLacingSampleIndex;
        r25 = r0;
        r25 = r25 + 1;
        r0 = r25;
        r1 = r30;
        r1.blockLacingSampleIndex = r0;
        goto L_0x028f;
    L_0x02f3:
        r25 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        r0 = r31;
        r1 = r25;
        if (r0 == r1) goto L_0x0303;
    L_0x02fb:
        r25 = new org.telegram.messenger.exoplayer.ParserException;
        r26 = "Lacing only supported in SimpleBlocks.";
        r25.<init>(r26);
        throw r25;
    L_0x0303:
        r25 = 4;
        r0 = r30;
        r1 = r33;
        r2 = r25;
        r0.readScratch(r1, r2);
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = 3;
        r25 = r25[r26];
        r0 = r25;
        r0 = r0 & 255;
        r25 = r0;
        r25 = r25 + 1;
        r0 = r25;
        r1 = r30;
        r1.blockLacingSampleCount = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r26 = r0;
        r25 = ensureArrayCapacity(r25, r26);
        r0 = r25;
        r1 = r30;
        r1.blockLacingSampleSizes = r0;
        r25 = 2;
        r0 = r25;
        if (r13 != r0) goto L_0x0373;
    L_0x0348:
        r0 = r30;
        r0 = r0.blockTrackNumberLength;
        r25 = r0;
        r25 = r32 - r25;
        r25 = r25 + -4;
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r26 = r0;
        r6 = r25 / r26;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = 0;
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r27 = r0;
        r0 = r25;
        r1 = r26;
        r2 = r27;
        java.util.Arrays.fill(r0, r1, r2, r6);
        goto L_0x01d0;
    L_0x0373:
        r25 = 1;
        r0 = r25;
        if (r13 != r0) goto L_0x03f0;
    L_0x0379:
        r23 = 0;
        r8 = 4;
        r17 = 0;
    L_0x037e:
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r25 = r0;
        r25 = r25 + -1;
        r0 = r17;
        r1 = r25;
        if (r0 >= r1) goto L_0x03d2;
    L_0x038c:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = 0;
        r25[r17] = r26;
    L_0x0396:
        r8 = r8 + 1;
        r0 = r30;
        r1 = r33;
        r0.readScratch(r1, r8);
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = r8 + -1;
        r25 = r25[r26];
        r0 = r25;
        r7 = r0 & 255;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = r25[r17];
        r26 = r26 + r7;
        r25[r17] = r26;
        r25 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r0 = r25;
        if (r7 == r0) goto L_0x0396;
    L_0x03c5:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r25 = r25[r17];
        r23 = r23 + r25;
        r17 = r17 + 1;
        goto L_0x037e;
    L_0x03d2:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r26 = r0;
        r26 = r26 + -1;
        r0 = r30;
        r0 = r0.blockTrackNumberLength;
        r27 = r0;
        r27 = r32 - r27;
        r27 = r27 - r8;
        r27 = r27 - r23;
        r25[r26] = r27;
        goto L_0x01d0;
    L_0x03f0:
        r25 = 3;
        r0 = r25;
        if (r13 != r0) goto L_0x051a;
    L_0x03f6:
        r23 = 0;
        r8 = 4;
        r17 = 0;
    L_0x03fb:
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r25 = r0;
        r25 = r25 + -1;
        r0 = r17;
        r1 = r25;
        if (r0 >= r1) goto L_0x04fc;
    L_0x0409:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = 0;
        r25[r17] = r26;
        r8 = r8 + 1;
        r0 = r30;
        r1 = r33;
        r0.readScratch(r1, r8);
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = r8 + -1;
        r25 = r25[r26];
        if (r25 != 0) goto L_0x0436;
    L_0x042e:
        r25 = new org.telegram.messenger.exoplayer.ParserException;
        r26 = "No valid varint length mask found";
        r25.<init>(r26);
        throw r25;
    L_0x0436:
        r18 = 0;
        r9 = 0;
    L_0x0439:
        r25 = 8;
        r0 = r25;
        if (r9 >= r0) goto L_0x04ba;
    L_0x043f:
        r25 = 1;
        r26 = 7 - r9;
        r14 = r25 << r26;
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r26 = r8 + -1;
        r25 = r25[r26];
        r25 = r25 & r14;
        if (r25 == 0) goto L_0x04d0;
    L_0x0459:
        r15 = r8 + -1;
        r8 = r8 + r9;
        r0 = r30;
        r1 = r33;
        r0.readScratch(r1, r8);
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r16 = r15 + 1;
        r25 = r25[r15];
        r0 = r25;
        r0 = r0 & 255;
        r25 = r0;
        r26 = r14 ^ -1;
        r25 = r25 & r26;
        r0 = r25;
        r0 = (long) r0;
        r18 = r0;
    L_0x0482:
        r0 = r16;
        if (r0 >= r8) goto L_0x04aa;
    L_0x0486:
        r25 = 8;
        r18 = r18 << r25;
        r0 = r30;
        r0 = r0.scratch;
        r25 = r0;
        r0 = r25;
        r0 = r0.data;
        r25 = r0;
        r15 = r16 + 1;
        r25 = r25[r16];
        r0 = r25;
        r0 = r0 & 255;
        r25 = r0;
        r0 = r25;
        r0 = (long) r0;
        r26 = r0;
        r18 = r18 | r26;
        r16 = r15;
        goto L_0x0482;
    L_0x04aa:
        if (r17 <= 0) goto L_0x04ba;
    L_0x04ac:
        r26 = 1;
        r25 = r9 * 7;
        r25 = r25 + 6;
        r26 = r26 << r25;
        r28 = 1;
        r26 = r26 - r28;
        r18 = r18 - r26;
    L_0x04ba:
        r26 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r25 = (r18 > r26 ? 1 : (r18 == r26 ? 0 : -1));
        if (r25 < 0) goto L_0x04c8;
    L_0x04c1:
        r26 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r25 = (r18 > r26 ? 1 : (r18 == r26 ? 0 : -1));
        if (r25 <= 0) goto L_0x04d4;
    L_0x04c8:
        r25 = new org.telegram.messenger.exoplayer.ParserException;
        r26 = "EBML lacing sample size out of range.";
        r25.<init>(r26);
        throw r25;
    L_0x04d0:
        r9 = r9 + 1;
        goto L_0x0439;
    L_0x04d4:
        r0 = r18;
        r10 = (int) r0;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        if (r17 != 0) goto L_0x04ef;
    L_0x04df:
        r25[r17] = r10;
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r25 = r25[r17];
        r23 = r23 + r25;
        r17 = r17 + 1;
        goto L_0x03fb;
    L_0x04ef:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r26 = r0;
        r27 = r17 + -1;
        r26 = r26[r27];
        r10 = r10 + r26;
        goto L_0x04df;
    L_0x04fc:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r0 = r30;
        r0 = r0.blockLacingSampleCount;
        r26 = r0;
        r26 = r26 + -1;
        r0 = r30;
        r0 = r0.blockTrackNumberLength;
        r27 = r0;
        r27 = r32 - r27;
        r27 = r27 - r8;
        r27 = r27 - r23;
        r25[r26] = r27;
        goto L_0x01d0;
    L_0x051a:
        r25 = new org.telegram.messenger.exoplayer.ParserException;
        r26 = new java.lang.StringBuilder;
        r26.<init>();
        r27 = "Unexpected lacing value: ";
        r26 = r26.append(r27);
        r0 = r26;
        r26 = r0.append(r13);
        r26 = r26.toString();
        r25.<init>(r26);
        throw r25;
    L_0x0535:
        r11 = 0;
        goto L_0x0230;
    L_0x0538:
        r12 = 0;
        goto L_0x0265;
    L_0x053b:
        r25 = 0;
        r26 = r25;
        goto L_0x026b;
    L_0x0541:
        r25 = 0;
        goto L_0x026f;
    L_0x0545:
        r25 = 0;
        r0 = r25;
        r1 = r30;
        r1.blockState = r0;
        goto L_0x006a;
    L_0x054f:
        r0 = r30;
        r0 = r0.blockLacingSampleSizes;
        r25 = r0;
        r26 = 0;
        r25 = r25[r26];
        r0 = r30;
        r1 = r33;
        r2 = r24;
        r3 = r25;
        r0.writeSampleData(r1, r2, r3);
        goto L_0x006a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.extractor.webm.WebmExtractor.binaryElement(int, int, org.telegram.messenger.exoplayer.extractor.ExtractorInput):void");
    }

    private void commitSampleToOutput(Track track, long timeUs) {
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            writeSubripSample(track);
        }
        track.output.sampleMetadata(timeUs, this.blockFlags, this.sampleBytesWritten, LACING_NONE, track.encryptionKeyId);
        this.sampleRead = true;
        resetSample();
    }

    private void resetSample() {
        this.sampleBytesRead = LACING_NONE;
        this.sampleBytesWritten = LACING_NONE;
        this.sampleCurrentNalBytesRemaining = LACING_NONE;
        this.sampleEncodingHandled = false;
        this.sampleStrippedBytes.reset();
    }

    private void readScratch(ExtractorInput input, int requiredLength) throws IOException, InterruptedException {
        if (this.scratch.limit() < requiredLength) {
            if (this.scratch.capacity() < requiredLength) {
                this.scratch.reset(Arrays.copyOf(this.scratch.data, Math.max(this.scratch.data.length * TRACK_TYPE_AUDIO, requiredLength)), this.scratch.limit());
            }
            input.readFully(this.scratch.data, this.scratch.limit(), requiredLength - this.scratch.limit());
            this.scratch.setLimit(requiredLength);
        }
    }

    private void writeSampleData(ExtractorInput input, Track track, int size) throws IOException, InterruptedException {
        if (CODEC_ID_SUBRIP.equals(track.codecId)) {
            int sizeWithPrefix = SUBRIP_PREFIX.length + size;
            if (this.subripSample.capacity() < sizeWithPrefix) {
                this.subripSample.data = Arrays.copyOf(SUBRIP_PREFIX, sizeWithPrefix + size);
            }
            input.readFully(this.subripSample.data, SUBRIP_PREFIX.length, size);
            this.subripSample.setPosition(LACING_NONE);
            this.subripSample.setLimit(sizeWithPrefix);
            return;
        }
        TrackOutput output = track.output;
        if (!this.sampleEncodingHandled) {
            if (track.hasContentEncryption) {
                this.blockFlags &= -3;
                input.readFully(this.scratch.data, LACING_NONE, WAVE_FORMAT_PCM);
                this.sampleBytesRead += WAVE_FORMAT_PCM;
                if ((this.scratch.data[LACING_NONE] & MessagesController.UPDATE_MASK_USER_PHONE) == MessagesController.UPDATE_MASK_USER_PHONE) {
                    throw new ParserException("Extension bit is set in signal byte");
                } else if ((this.scratch.data[LACING_NONE] & WAVE_FORMAT_PCM) == WAVE_FORMAT_PCM) {
                    this.scratch.data[LACING_NONE] = (byte) 8;
                    this.scratch.setPosition(LACING_NONE);
                    output.sampleData(this.scratch, WAVE_FORMAT_PCM);
                    this.sampleBytesWritten += WAVE_FORMAT_PCM;
                    this.blockFlags |= TRACK_TYPE_AUDIO;
                }
            } else if (track.sampleStrippedBytes != null) {
                this.sampleStrippedBytes.reset(track.sampleStrippedBytes, track.sampleStrippedBytes.length);
            }
            this.sampleEncodingHandled = true;
        }
        size += this.sampleStrippedBytes.limit();
        if (CODEC_ID_H264.equals(track.codecId) || CODEC_ID_H265.equals(track.codecId)) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[LACING_NONE] = (byte) 0;
            nalLengthData[WAVE_FORMAT_PCM] = (byte) 0;
            nalLengthData[TRACK_TYPE_AUDIO] = (byte) 0;
            int nalUnitLengthFieldLength = track.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesRead < size) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    readToTarget(input, nalLengthData, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                    this.nalLength.setPosition(LACING_NONE);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(LACING_NONE);
                    output.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                } else {
                    this.sampleCurrentNalBytesRemaining -= readToOutput(input, output, this.sampleCurrentNalBytesRemaining);
                }
            }
        } else {
            while (this.sampleBytesRead < size) {
                readToOutput(input, output, size - this.sampleBytesRead);
            }
        }
        if (CODEC_ID_VORBIS.equals(track.codecId)) {
            this.vorbisNumPageSamples.setPosition(LACING_NONE);
            output.sampleData(this.vorbisNumPageSamples, 4);
            this.sampleBytesWritten += 4;
        }
    }

    private void writeSubripSample(Track track) {
        setSubripSampleEndTimecode(this.subripSample.data, this.blockDurationUs);
        track.output.sampleData(this.subripSample, this.subripSample.limit());
        this.sampleBytesWritten += this.subripSample.limit();
    }

    private static void setSubripSampleEndTimecode(byte[] subripSampleData, long timeUs) {
        byte[] timeCodeData;
        if (timeUs == -1) {
            timeCodeData = SUBRIP_TIMECODE_EMPTY;
        } else {
            timeUs -= ((long) ((int) (timeUs / 3600000000L))) * 3600000000L;
            timeUs -= (long) (60000000 * ((int) (timeUs / 60000000)));
            int milliseconds = (int) ((timeUs - ((long) (1000000 * ((int) (timeUs / C0747C.MICROS_PER_SECOND))))) / 1000);
            timeCodeData = String.format(Locale.US, "%02d:%02d:%02d,%03d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf((int) (timeUs / C0747C.MICROS_PER_SECOND)), Integer.valueOf(milliseconds)}).getBytes();
        }
        System.arraycopy(timeCodeData, LACING_NONE, subripSampleData, SUBRIP_PREFIX_END_TIMECODE_OFFSET, SUBRIP_TIMECODE_LENGTH);
    }

    private void readToTarget(ExtractorInput input, byte[] target, int offset, int length) throws IOException, InterruptedException {
        int pendingStrippedBytes = Math.min(length, this.sampleStrippedBytes.bytesLeft());
        input.readFully(target, offset + pendingStrippedBytes, length - pendingStrippedBytes);
        if (pendingStrippedBytes > 0) {
            this.sampleStrippedBytes.readBytes(target, offset, pendingStrippedBytes);
        }
        this.sampleBytesRead += length;
    }

    private int readToOutput(ExtractorInput input, TrackOutput output, int length) throws IOException, InterruptedException {
        int bytesRead;
        int strippedBytesLeft = this.sampleStrippedBytes.bytesLeft();
        if (strippedBytesLeft > 0) {
            bytesRead = Math.min(length, strippedBytesLeft);
            output.sampleData(this.sampleStrippedBytes, bytesRead);
        } else {
            bytesRead = output.sampleData(input, length, false);
        }
        this.sampleBytesRead += bytesRead;
        this.sampleBytesWritten += bytesRead;
        return bytesRead;
    }

    private SeekMap buildSeekMap() {
        if (this.segmentContentPosition == -1 || this.durationUs == -1 || this.cueTimesUs == null || this.cueTimesUs.size() == 0 || this.cueClusterPositions == null || this.cueClusterPositions.size() != this.cueTimesUs.size()) {
            this.cueTimesUs = null;
            this.cueClusterPositions = null;
            return SeekMap.UNSEEKABLE;
        }
        int i;
        int cuePointsSize = this.cueTimesUs.size();
        int[] sizes = new int[cuePointsSize];
        long[] offsets = new long[cuePointsSize];
        long[] durationsUs = new long[cuePointsSize];
        long[] timesUs = new long[cuePointsSize];
        for (i = LACING_NONE; i < cuePointsSize; i += WAVE_FORMAT_PCM) {
            timesUs[i] = this.cueTimesUs.get(i);
            offsets[i] = this.segmentContentPosition + this.cueClusterPositions.get(i);
        }
        for (i = LACING_NONE; i < cuePointsSize + UNKNOWN; i += WAVE_FORMAT_PCM) {
            sizes[i] = (int) (offsets[i + WAVE_FORMAT_PCM] - offsets[i]);
            durationsUs[i] = timesUs[i + WAVE_FORMAT_PCM] - timesUs[i];
        }
        sizes[cuePointsSize + UNKNOWN] = (int) ((this.segmentContentPosition + this.segmentContentSize) - offsets[cuePointsSize + UNKNOWN]);
        durationsUs[cuePointsSize + UNKNOWN] = this.durationUs - timesUs[cuePointsSize + UNKNOWN];
        this.cueTimesUs = null;
        this.cueClusterPositions = null;
        return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
    }

    private boolean maybeSeekForCues(PositionHolder seekPosition, long currentPosition) {
        if (this.seekForCues) {
            this.seekPositionAfterBuildingCues = currentPosition;
            seekPosition.position = this.cuesContentPosition;
            this.seekForCues = false;
            return true;
        } else if (!this.sentSeekMap || this.seekPositionAfterBuildingCues == -1) {
            return false;
        } else {
            seekPosition.position = this.seekPositionAfterBuildingCues;
            this.seekPositionAfterBuildingCues = -1;
            return true;
        }
    }

    private long scaleTimecodeToUs(long unscaledTimecode) throws ParserException {
        if (this.timecodeScale == -1) {
            throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
        }
        return Util.scaleLargeTimestamp(unscaledTimecode, this.timecodeScale, 1000);
    }

    private static boolean isCodecSupported(String codecId) {
        return CODEC_ID_VP8.equals(codecId) || CODEC_ID_VP9.equals(codecId) || CODEC_ID_MPEG2.equals(codecId) || CODEC_ID_MPEG4_SP.equals(codecId) || CODEC_ID_MPEG4_ASP.equals(codecId) || CODEC_ID_MPEG4_AP.equals(codecId) || CODEC_ID_H264.equals(codecId) || CODEC_ID_H265.equals(codecId) || CODEC_ID_FOURCC.equals(codecId) || CODEC_ID_OPUS.equals(codecId) || CODEC_ID_VORBIS.equals(codecId) || CODEC_ID_AAC.equals(codecId) || CODEC_ID_MP3.equals(codecId) || CODEC_ID_AC3.equals(codecId) || CODEC_ID_E_AC3.equals(codecId) || CODEC_ID_TRUEHD.equals(codecId) || CODEC_ID_DTS.equals(codecId) || CODEC_ID_DTS_EXPRESS.equals(codecId) || CODEC_ID_DTS_LOSSLESS.equals(codecId) || CODEC_ID_FLAC.equals(codecId) || CODEC_ID_ACM.equals(codecId) || CODEC_ID_PCM_INT_LIT.equals(codecId) || CODEC_ID_SUBRIP.equals(codecId) || CODEC_ID_VOBSUB.equals(codecId) || CODEC_ID_PGS.equals(codecId);
    }

    private static int[] ensureArrayCapacity(int[] array, int length) {
        if (array == null) {
            return new int[length];
        }
        return array.length < length ? new int[Math.max(array.length * TRACK_TYPE_AUDIO, length)] : array;
    }
}
