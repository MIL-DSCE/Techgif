package com.googlecode.mp4parser.authoring.tracks;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.session.PlaybackStateCompat;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.DTSSpecificBox;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.EOFException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer.upstream.DefaultHttpDataSource;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public class DTSTrackImpl extends AbstractTrack {
    private static final int BUFFER = 67108864;
    int bcCoreBitRate;
    int bcCoreChannelMask;
    int bcCoreMaxSampleRate;
    int bitrate;
    int channelCount;
    int channelMask;
    int codecDelayAtMaxFs;
    int coreBitRate;
    int coreChannelMask;
    int coreFramePayloadInBytes;
    int coreMaxSampleRate;
    boolean coreSubStreamPresent;
    private int dataOffset;
    private DataSource dataSource;
    DTSSpecificBox ddts;
    int extAvgBitrate;
    int extFramePayloadInBytes;
    int extPeakBitrate;
    int extSmoothBuffSize;
    boolean extensionSubStreamPresent;
    int frameSize;
    boolean isVBR;
    private String lang;
    int lbrCodingPresent;
    int lsbTrimPercent;
    int maxSampleRate;
    int numExtSubStreams;
    int numFramesTotal;
    int numSamplesOrigAudioAtMaxFs;
    SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    int sampleSize;
    int samplerate;
    private List<Sample> samples;
    int samplesPerFrame;
    int samplesPerFrameAtMaxFs;
    TrackMetaData trackMetaData;
    String type;

    class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        private final int corePresent;
        long dataEnd;
        DataSource dataSource;
        int inBufferPos;
        long start;

        LookAhead(DataSource dataSource, long bufferStartPos, long dataSize, int corePresent) throws IOException {
            this.inBufferPos = 0;
            this.dataSource = dataSource;
            this.bufferStartPos = bufferStartPos;
            this.dataEnd = dataSize + bufferStartPos;
            this.corePresent = corePresent;
            fillBuffer();
        }

        public ByteBuffer findNextStart() throws IOException {
            while (true) {
                try {
                    if (this.corePresent == 1) {
                        if (nextFourEquals0x7FFE8001()) {
                            break;
                        }
                        discardByte();
                    } else if (nextFourEquals0x64582025()) {
                        break;
                    } else {
                        discardByte();
                    }
                } catch (EOFException e) {
                    return null;
                }
            }
            discardNext4AndMarkStart();
            while (true) {
                if (this.corePresent == 1) {
                    if (nextFourEquals0x7FFE8001orEof()) {
                        break;
                    }
                    discardQWord();
                } else if (nextFourEquals0x64582025orEof()) {
                    break;
                } else {
                    discardQWord();
                }
            }
            return getSample();
        }

        private void fillBuffer() throws IOException {
            System.err.println("Fill Buffer");
            this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataEnd - this.bufferStartPos, 67108864));
        }

        private boolean nextFourEquals0x64582025() throws IOException {
            return nextFourEquals((byte) 100, (byte) 88, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.ROLL_UP_CAPTIONS_2_ROWS);
        }

        private boolean nextFourEquals0x7FFE8001() throws IOException {
            return nextFourEquals(Byte.MAX_VALUE, (byte) -2, Byte.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEquals(byte a, byte b, byte c, byte d) throws IOException {
            if (this.buffer.limit() - this.inBufferPos >= 4) {
                if (this.buffer.get(this.inBufferPos) == a && this.buffer.get(this.inBufferPos + 1) == b && this.buffer.get(this.inBufferPos + 2) == c && this.buffer.get(this.inBufferPos + 3) == d) {
                    return true;
                }
                return false;
            } else if ((this.bufferStartPos + ((long) this.inBufferPos)) + 4 < this.dataSource.size()) {
                return false;
            } else {
                throw new EOFException();
            }
        }

        private boolean nextFourEquals0x64582025orEof() throws IOException {
            return nextFourEqualsOrEof((byte) 100, (byte) 88, ClosedCaptionCtrl.RESUME_CAPTION_LOADING, ClosedCaptionCtrl.ROLL_UP_CAPTIONS_2_ROWS);
        }

        private boolean nextFourEquals0x7FFE8001orEof() throws IOException {
            return nextFourEqualsOrEof(Byte.MAX_VALUE, (byte) -2, Byte.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEqualsOrEof(byte a, byte b, byte c, byte d) throws IOException {
            if (this.buffer.limit() - this.inBufferPos >= 4) {
                if ((this.bufferStartPos + ((long) this.inBufferPos)) % 1048576 == 0) {
                    System.err.println((((this.bufferStartPos + ((long) this.inBufferPos)) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
                }
                if (this.buffer.get(this.inBufferPos) == a && this.buffer.get(this.inBufferPos + 1) == b && this.buffer.get(this.inBufferPos + 2) == c && this.buffer.get(this.inBufferPos + 3) == d) {
                    return true;
                }
                return false;
            } else if ((this.bufferStartPos + ((long) this.inBufferPos)) + 4 <= this.dataEnd) {
                this.bufferStartPos = this.start;
                this.inBufferPos = 0;
                fillBuffer();
                return nextFourEquals0x7FFE8001();
            } else if (this.bufferStartPos + ((long) this.inBufferPos) != this.dataEnd) {
                return false;
            } else {
                return true;
            }
        }

        private void discardByte() {
            this.inBufferPos++;
        }

        private void discardQWord() {
            this.inBufferPos += 4;
        }

        private void discardNext4AndMarkStart() {
            this.start = this.bufferStartPos + ((long) this.inBufferPos);
            this.inBufferPos += 4;
        }

        private ByteBuffer getSample() {
            if (this.start >= this.bufferStartPos) {
                this.buffer.position((int) (this.start - this.bufferStartPos));
                Buffer sample = this.buffer.slice();
                sample.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return (ByteBuffer) sample;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }
    }

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.DTSTrackImpl.1 */
    class C16611 implements Sample {
        private final /* synthetic */ ByteBuffer val$finalSample;

        C16611(ByteBuffer byteBuffer) {
            this.val$finalSample = byteBuffer;
        }

        public void writeTo(WritableByteChannel channel) throws IOException {
            channel.write((ByteBuffer) this.val$finalSample.rewind());
        }

        public long getSize() {
            return (long) this.val$finalSample.rewind().remaining();
        }

        public ByteBuffer asByteBuffer() {
            return this.val$finalSample;
        }
    }

    public DTSTrackImpl(DataSource dataSource, String lang) throws IOException {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.frameSize = 0;
        this.dataOffset = 0;
        this.ddts = new DTSSpecificBox();
        this.isVBR = false;
        this.coreSubStreamPresent = false;
        this.extensionSubStreamPresent = false;
        this.numExtSubStreams = 0;
        this.coreMaxSampleRate = 0;
        this.coreBitRate = 0;
        this.coreChannelMask = 0;
        this.coreFramePayloadInBytes = 0;
        this.extAvgBitrate = 0;
        this.extPeakBitrate = 0;
        this.extSmoothBuffSize = 0;
        this.extFramePayloadInBytes = 0;
        this.maxSampleRate = 0;
        this.lbrCodingPresent = 0;
        this.numFramesTotal = 0;
        this.samplesPerFrameAtMaxFs = 0;
        this.numSamplesOrigAudioAtMaxFs = 0;
        this.channelMask = 0;
        this.codecDelayAtMaxFs = 0;
        this.bcCoreMaxSampleRate = 0;
        this.bcCoreBitRate = 0;
        this.bcCoreChannelMask = 0;
        this.lsbTrimPercent = 0;
        this.type = "none";
        this.lang = "eng";
        this.lang = lang;
        this.dataSource = dataSource;
        parse();
    }

    public DTSTrackImpl(DataSource dataSource) throws IOException {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.frameSize = 0;
        this.dataOffset = 0;
        this.ddts = new DTSSpecificBox();
        this.isVBR = false;
        this.coreSubStreamPresent = false;
        this.extensionSubStreamPresent = false;
        this.numExtSubStreams = 0;
        this.coreMaxSampleRate = 0;
        this.coreBitRate = 0;
        this.coreChannelMask = 0;
        this.coreFramePayloadInBytes = 0;
        this.extAvgBitrate = 0;
        this.extPeakBitrate = 0;
        this.extSmoothBuffSize = 0;
        this.extFramePayloadInBytes = 0;
        this.maxSampleRate = 0;
        this.lbrCodingPresent = 0;
        this.numFramesTotal = 0;
        this.samplesPerFrameAtMaxFs = 0;
        this.numSamplesOrigAudioAtMaxFs = 0;
        this.channelMask = 0;
        this.codecDelayAtMaxFs = 0;
        this.bcCoreMaxSampleRate = 0;
        this.bcCoreBitRate = 0;
        this.bcCoreChannelMask = 0;
        this.lsbTrimPercent = 0;
        this.type = "none";
        this.lang = "eng";
        this.dataSource = dataSource;
        parse();
    }

    public void close() throws IOException {
        this.dataSource.close();
    }

    private void parse() throws IOException {
        if (readVariables()) {
            this.sampleDescriptionBox = new SampleDescriptionBox();
            AudioSampleEntry audioSampleEntry = new AudioSampleEntry(this.type);
            audioSampleEntry.setChannelCount(this.channelCount);
            audioSampleEntry.setSampleRate((long) this.samplerate);
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            audioSampleEntry.addBox(this.ddts);
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            this.trackMetaData.setCreationTime(new Date());
            this.trackMetaData.setModificationTime(new Date());
            this.trackMetaData.setLanguage(this.lang);
            this.trackMetaData.setTimescale((long) this.samplerate);
            return;
        }
        throw new IOException();
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    public List<Entry> getCompositionTimeEntries() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public String getHandler() {
        return "soun";
    }

    private void parseDtshdhdr(int size, ByteBuffer bb) {
        bb.getInt();
        bb.get();
        bb.getInt();
        bb.get();
        int bitwStreamMetadata = bb.getShort();
        bb.get();
        this.numExtSubStreams = bb.get();
        if ((bitwStreamMetadata & 1) == 1) {
            this.isVBR = true;
        }
        if ((bitwStreamMetadata & 8) == 8) {
            this.coreSubStreamPresent = true;
        }
        if ((bitwStreamMetadata & 16) == 16) {
            this.extensionSubStreamPresent = true;
            this.numExtSubStreams++;
        } else {
            this.numExtSubStreams = 0;
        }
        for (int i = 14; i < size; i++) {
            bb.get();
        }
    }

    private boolean parseCoressmd(int size, ByteBuffer bb) {
        this.coreMaxSampleRate = (bb.get() << 16) | (SupportMenu.USER_MASK & bb.getShort());
        this.coreBitRate = bb.getShort();
        this.coreChannelMask = bb.getShort();
        this.coreFramePayloadInBytes = bb.getInt();
        for (int i = 11; i < size; i++) {
            bb.get();
        }
        return true;
    }

    private boolean parseAuprhdr(int size, ByteBuffer bb) {
        bb.get();
        int bitwAupresData = bb.getShort();
        this.maxSampleRate = (bb.get() << 16) | (bb.getShort() & SupportMenu.USER_MASK);
        this.numFramesTotal = bb.getInt();
        this.samplesPerFrameAtMaxFs = bb.getShort();
        this.numSamplesOrigAudioAtMaxFs = (bb.get() << 32) | (bb.getInt() & SupportMenu.USER_MASK);
        this.channelMask = bb.getShort();
        this.codecDelayAtMaxFs = bb.getShort();
        int c = 21;
        if ((bitwAupresData & 3) == 3) {
            this.bcCoreMaxSampleRate = (bb.get() << 16) | (bb.getShort() & SupportMenu.USER_MASK);
            this.bcCoreBitRate = bb.getShort();
            this.bcCoreChannelMask = bb.getShort();
            c = 21 + 7;
        }
        if ((bitwAupresData & 4) > 0) {
            this.lsbTrimPercent = bb.get();
            c++;
        }
        if ((bitwAupresData & 8) > 0) {
            this.lbrCodingPresent = 1;
        }
        while (c < size) {
            bb.get();
            c++;
        }
        return true;
    }

    private boolean parseExtssmd(int size, ByteBuffer bb) {
        int i;
        this.extAvgBitrate = (bb.get() << 16) | (bb.getShort() & SupportMenu.USER_MASK);
        if (this.isVBR) {
            this.extPeakBitrate = (bb.get() << 16) | (bb.getShort() & SupportMenu.USER_MASK);
            this.extSmoothBuffSize = bb.getShort();
            i = 3 + 5;
        } else {
            this.extFramePayloadInBytes = bb.getInt();
            i = 3 + 4;
        }
        while (i < size) {
            bb.get();
            i++;
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean readVariables() throws java.io.IOException {
        /*
        r52 = this;
        r0 = r52;
        r3 = r0.dataSource;
        r4 = 0;
        r50 = 25000; // 0x61a8 float:3.5032E-41 double:1.23516E-319;
        r0 = r50;
        r9 = r3.map(r4, r0);
        r46 = r9.getInt();
        r47 = r9.getInt();
        r3 = 1146377032; // 0x44545348 float:849.3013 double:5.663855087E-315;
        r0 = r46;
        if (r0 != r3) goto L_0x0024;
    L_0x001d:
        r3 = 1145586770; // 0x44484452 float:801.0675 double:5.659950674E-315;
        r0 = r47;
        if (r0 == r3) goto L_0x0050;
    L_0x0024:
        r3 = new java.io.IOException;
        r4 = "data does not start with 'DTSHDHDR' as required for a DTS-HD file";
        r3.<init>(r4);
        throw r3;
    L_0x002c:
        r4 = r9.getLong();
        r0 = (int) r4;
        r43 = r0;
        r3 = 1146377032; // 0x44545348 float:849.3013 double:5.663855087E-315;
        r0 = r46;
        if (r0 != r3) goto L_0x009b;
    L_0x003a:
        r3 = 1145586770; // 0x44484452 float:801.0675 double:5.659950674E-315;
        r0 = r47;
        if (r0 != r3) goto L_0x009b;
    L_0x0041:
        r0 = r52;
        r1 = r43;
        r0.parseDtshdhdr(r1, r9);
    L_0x0048:
        r46 = r9.getInt();
        r47 = r9.getInt();
    L_0x0050:
        r3 = 1398035021; // 0x5354524d float:9.1191384E11 double:6.907210756E-315;
        r0 = r46;
        if (r0 != r3) goto L_0x005e;
    L_0x0057:
        r3 = 1145132097; // 0x44415441 float:773.31647 double:5.65770429E-315;
        r0 = r47;
        if (r0 == r3) goto L_0x0066;
    L_0x005e:
        r3 = r9.remaining();
        r4 = 100;
        if (r3 > r4) goto L_0x002c;
    L_0x0066:
        r6 = r9.getLong();
        r3 = r9.position();
        r0 = r52;
        r0.dataOffset = r3;
        r2 = -1;
        r17 = 0;
        r16 = 0;
        r8 = -1;
        r20 = -1;
        r25 = 0;
        r23 = 0;
        r22 = 0;
        r24 = 0;
        r19 = 0;
        r26 = 0;
        r18 = 0;
        r15 = 0;
    L_0x0089:
        if (r15 == 0) goto L_0x00f7;
    L_0x008b:
        r27 = -1;
        r0 = r52;
        r3 = r0.samplesPerFrame;
        switch(r3) {
            case 512: goto L_0x02e9;
            case 1024: goto L_0x02ed;
            case 2048: goto L_0x02f1;
            case 4096: goto L_0x02f5;
            default: goto L_0x0094;
        };
    L_0x0094:
        r3 = -1;
        r0 = r27;
        if (r0 != r3) goto L_0x02f9;
    L_0x0099:
        r3 = 0;
    L_0x009a:
        return r3;
    L_0x009b:
        r3 = 1129271877; // 0x434f5245 float:207.32137 double:5.57934439E-315;
        r0 = r46;
        if (r0 != r3) goto L_0x00b5;
    L_0x00a2:
        r3 = 1397968196; // 0x53534d44 float:9.075344E11 double:6.906880596E-315;
        r0 = r47;
        if (r0 != r3) goto L_0x00b5;
    L_0x00a9:
        r0 = r52;
        r1 = r43;
        r3 = r0.parseCoressmd(r1, r9);
        if (r3 != 0) goto L_0x0048;
    L_0x00b3:
        r3 = 0;
        goto L_0x009a;
    L_0x00b5:
        r3 = 1096110162; // 0x41555052 float:13.332109 double:5.41550375E-315;
        r0 = r46;
        if (r0 != r3) goto L_0x00cf;
    L_0x00bc:
        r3 = 759710802; // 0x2d484452 float:1.1383854E-11 double:3.75347008E-315;
        r0 = r47;
        if (r0 != r3) goto L_0x00cf;
    L_0x00c3:
        r0 = r52;
        r1 = r43;
        r3 = r0.parseAuprhdr(r1, r9);
        if (r3 != 0) goto L_0x0048;
    L_0x00cd:
        r3 = 0;
        goto L_0x009a;
    L_0x00cf:
        r3 = 1163416659; // 0x45585453 float:3461.2703 double:5.74804203E-315;
        r0 = r46;
        if (r0 != r3) goto L_0x00e9;
    L_0x00d6:
        r3 = 1398754628; // 0x535f4d44 float:9.5907401E11 double:6.910766087E-315;
        r0 = r47;
        if (r0 != r3) goto L_0x00e9;
    L_0x00dd:
        r0 = r52;
        r1 = r43;
        r3 = r0.parseExtssmd(r1, r9);
        if (r3 != 0) goto L_0x0048;
    L_0x00e7:
        r3 = 0;
        goto L_0x009a;
    L_0x00e9:
        r32 = 0;
    L_0x00eb:
        r0 = r32;
        r1 = r43;
        if (r0 >= r1) goto L_0x0048;
    L_0x00f1:
        r9.get();
        r32 = r32 + 1;
        goto L_0x00eb;
    L_0x00f7:
        r38 = r9.position();
        r45 = r9.getInt();
        r3 = 2147385345; // 0x7ffe8001 float:NaN double:1.0609493273E-314;
        r0 = r45;
        if (r0 != r3) goto L_0x0203;
    L_0x0106:
        r3 = 1;
        if (r8 != r3) goto L_0x010c;
    L_0x0109:
        r15 = 1;
        goto L_0x0089;
    L_0x010c:
        r8 = 1;
        r11 = new com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
        r11.<init>(r9);
        r3 = 1;
        r30 = r11.readBits(r3);
        r3 = 5;
        r42 = r11.readBits(r3);
        r3 = 1;
        r13 = r11.readBits(r3);
        r3 = 1;
        r0 = r30;
        if (r0 != r3) goto L_0x012e;
    L_0x0126:
        r3 = 31;
        r0 = r42;
        if (r0 != r3) goto L_0x012e;
    L_0x012c:
        if (r13 == 0) goto L_0x0131;
    L_0x012e:
        r3 = 0;
        goto L_0x009a;
    L_0x0131:
        r3 = 7;
        r33 = r11.readBits(r3);
        r3 = r33 + 1;
        r3 = r3 * 32;
        r0 = r52;
        r0.samplesPerFrame = r3;
        r3 = 14;
        r29 = r11.readBits(r3);
        r0 = r52;
        r3 = r0.frameSize;
        r4 = r29 + 1;
        r3 = r3 + r4;
        r0 = r52;
        r0.frameSize = r3;
        r3 = 6;
        r2 = r11.readBits(r3);
        r3 = 4;
        r41 = r11.readBits(r3);
        r0 = r52;
        r1 = r41;
        r3 = r0.getSampleRate(r1);
        r0 = r52;
        r0.samplerate = r3;
        r3 = 5;
        r40 = r11.readBits(r3);
        r0 = r52;
        r1 = r40;
        r3 = r0.getBitRate(r1);
        r0 = r52;
        r0.bitrate = r3;
        r3 = 1;
        r28 = r11.readBits(r3);
        if (r28 == 0) goto L_0x0180;
    L_0x017d:
        r3 = 0;
        goto L_0x009a;
    L_0x0180:
        r3 = 1;
        r11.readBits(r3);
        r3 = 1;
        r11.readBits(r3);
        r3 = 1;
        r11.readBits(r3);
        r3 = 1;
        r11.readBits(r3);
        r3 = 3;
        r17 = r11.readBits(r3);
        r3 = 1;
        r16 = r11.readBits(r3);
        r3 = 1;
        r11.readBits(r3);
        r3 = 2;
        r11.readBits(r3);
        r3 = 1;
        r11.readBits(r3);
        r3 = 1;
        if (r13 != r3) goto L_0x01ae;
    L_0x01a9:
        r3 = 16;
        r11.readBits(r3);
    L_0x01ae:
        r3 = 1;
        r11.readBits(r3);
        r3 = 4;
        r48 = r11.readBits(r3);
        r3 = 2;
        r11.readBits(r3);
        r3 = 3;
        r39 = r11.readBits(r3);
        switch(r39) {
            case 0: goto L_0x01c6;
            case 1: goto L_0x01c6;
            case 2: goto L_0x01e5;
            case 3: goto L_0x01e5;
            case 4: goto L_0x01c3;
            case 5: goto L_0x01ec;
            case 6: goto L_0x01ec;
            default: goto L_0x01c3;
        };
    L_0x01c3:
        r3 = 0;
        goto L_0x009a;
    L_0x01c6:
        r3 = 16;
        r0 = r52;
        r0.sampleSize = r3;
    L_0x01cc:
        r3 = 1;
        r11.readBits(r3);
        r3 = 1;
        r11.readBits(r3);
        r14 = 0;
        switch(r48) {
            case 6: goto L_0x01f3;
            case 7: goto L_0x01fc;
            default: goto L_0x01d8;
        };
    L_0x01d8:
        r3 = 4;
        r11.readBits(r3);
    L_0x01dc:
        r3 = r38 + r29;
        r3 = r3 + 1;
        r9.position(r3);
        goto L_0x0089;
    L_0x01e5:
        r3 = 20;
        r0 = r52;
        r0.sampleSize = r3;
        goto L_0x01cc;
    L_0x01ec:
        r3 = 24;
        r0 = r52;
        r0.sampleSize = r3;
        goto L_0x01cc;
    L_0x01f3:
        r3 = 4;
        r14 = r11.readBits(r3);
        r3 = r14 + 16;
        r3 = -r3;
        goto L_0x01dc;
    L_0x01fc:
        r3 = 4;
        r14 = r11.readBits(r3);
        r3 = -r14;
        goto L_0x01dc;
    L_0x0203:
        r3 = 1683496997; // 0x64582025 float:1.5947252E22 double:8.31758031E-315;
        r0 = r45;
        if (r0 != r3) goto L_0x02d0;
    L_0x020a:
        r3 = -1;
        if (r8 != r3) goto L_0x0216;
    L_0x020d:
        r8 = 0;
        r0 = r52;
        r3 = r0.samplesPerFrameAtMaxFs;
        r0 = r52;
        r0.samplesPerFrame = r3;
    L_0x0216:
        r20 = 1;
        r11 = new com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
        r11.<init>(r9);
        r3 = 8;
        r11.readBits(r3);
        r3 = 2;
        r11.readBits(r3);
        r3 = 1;
        r31 = r11.readBits(r3);
        r35 = 12;
        r34 = 20;
        if (r31 != 0) goto L_0x0235;
    L_0x0231:
        r35 = 8;
        r34 = 16;
    L_0x0235:
        r0 = r35;
        r3 = r11.readBits(r0);
        r37 = r3 + 1;
        r0 = r34;
        r3 = r11.readBits(r0);
        r36 = r3 + 1;
        r3 = r38 + r37;
        r9.position(r3);
        r21 = r9.getInt();
        r3 = 1515870810; // 0x5a5a5a5a float:1.53652219E16 double:7.48939691E-315;
        r0 = r21;
        if (r0 != r3) goto L_0x0270;
    L_0x0255:
        r3 = 1;
        r0 = r25;
        if (r0 != r3) goto L_0x025b;
    L_0x025a:
        r15 = 1;
    L_0x025b:
        r25 = 1;
    L_0x025d:
        if (r15 != 0) goto L_0x0269;
    L_0x025f:
        r0 = r52;
        r3 = r0.frameSize;
        r3 = r3 + r36;
        r0 = r52;
        r0.frameSize = r3;
    L_0x0269:
        r3 = r38 + r36;
        r9.position(r3);
        goto L_0x0089;
    L_0x0270:
        r3 = 1191201283; // 0x47004a03 float:32842.01 double:5.88531631E-315;
        r0 = r21;
        if (r0 != r3) goto L_0x0280;
    L_0x0277:
        r3 = 1;
        r0 = r23;
        if (r0 != r3) goto L_0x027d;
    L_0x027c:
        r15 = 1;
    L_0x027d:
        r23 = 1;
        goto L_0x025d;
    L_0x0280:
        r3 = 496366178; // 0x1d95f262 float:3.969059E-21 double:2.452374763E-315;
        r0 = r21;
        if (r0 != r3) goto L_0x0290;
    L_0x0287:
        r3 = 1;
        r0 = r22;
        if (r0 != r3) goto L_0x028d;
    L_0x028c:
        r15 = 1;
    L_0x028d:
        r22 = 1;
        goto L_0x025d;
    L_0x0290:
        r3 = 1700671838; // 0x655e315e float:6.557975E22 double:8.4024353E-315;
        r0 = r21;
        if (r0 != r3) goto L_0x02a0;
    L_0x0297:
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x029d;
    L_0x029c:
        r15 = 1;
    L_0x029d:
        r24 = 1;
        goto L_0x025d;
    L_0x02a0:
        r3 = 176167201; // 0xa801921 float:1.2335404E-32 double:8.7038162E-316;
        r0 = r21;
        if (r0 != r3) goto L_0x02b0;
    L_0x02a7:
        r3 = 1;
        r0 = r19;
        if (r0 != r3) goto L_0x02ad;
    L_0x02ac:
        r15 = 1;
    L_0x02ad:
        r19 = 1;
        goto L_0x025d;
    L_0x02b0:
        r3 = 1101174087; // 0x41a29547 float:20.32289 double:5.440522865E-315;
        r0 = r21;
        if (r0 != r3) goto L_0x02c0;
    L_0x02b7:
        r3 = 1;
        r0 = r26;
        if (r0 != r3) goto L_0x02bd;
    L_0x02bc:
        r15 = 1;
    L_0x02bd:
        r26 = 1;
        goto L_0x025d;
    L_0x02c0:
        r3 = 45126241; // 0x2b09261 float:2.5944893E-37 double:2.22953254E-316;
        r0 = r21;
        if (r0 != r3) goto L_0x025d;
    L_0x02c7:
        r3 = 1;
        r0 = r18;
        if (r0 != r3) goto L_0x02cd;
    L_0x02cc:
        r15 = 1;
    L_0x02cd:
        r18 = 1;
        goto L_0x025d;
    L_0x02d0:
        r3 = new java.io.IOException;
        r4 = new java.lang.StringBuilder;
        r5 = "No DTS_SYNCWORD_* found at ";
        r4.<init>(r5);
        r5 = r9.position();
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x02e9:
        r27 = 0;
        goto L_0x0094;
    L_0x02ed:
        r27 = 1;
        goto L_0x0094;
    L_0x02f1:
        r27 = 2;
        goto L_0x0094;
    L_0x02f5:
        r27 = 3;
        goto L_0x0094;
    L_0x02f9:
        r12 = 31;
        switch(r2) {
            case 0: goto L_0x0419;
            case 1: goto L_0x02fe;
            case 2: goto L_0x0419;
            case 3: goto L_0x02fe;
            case 4: goto L_0x0419;
            case 5: goto L_0x0419;
            case 6: goto L_0x0419;
            case 7: goto L_0x0419;
            case 8: goto L_0x0419;
            case 9: goto L_0x0419;
            default: goto L_0x02fe;
        };
    L_0x02fe:
        r44 = 0;
        if (r8 != 0) goto L_0x045e;
    L_0x0302:
        r3 = 1;
        r0 = r26;
        if (r0 != r3) goto L_0x0426;
    L_0x0307:
        if (r18 != 0) goto L_0x041c;
    L_0x0309:
        r44 = 17;
        r3 = "dtsl";
        r0 = r52;
        r0.type = r3;
    L_0x0311:
        r0 = r52;
        r3 = r0.maxSampleRate;
        r0 = r52;
        r0.samplerate = r3;
        r3 = 24;
        r0 = r52;
        r0.sampleSize = r3;
    L_0x031f:
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.maxSampleRate;
        r4 = (long) r4;
        r3.setDTSSamplingFrequency(r4);
        r0 = r52;
        r3 = r0.isVBR;
        if (r3 == 0) goto L_0x05a1;
    L_0x0331:
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.coreBitRate;
        r0 = r52;
        r5 = r0.extPeakBitrate;
        r4 = r4 + r5;
        r4 = r4 * 1000;
        r4 = (long) r4;
        r3.setMaxBitRate(r4);
    L_0x0344:
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.coreBitRate;
        r0 = r52;
        r5 = r0.extAvgBitrate;
        r4 = r4 + r5;
        r4 = r4 * 1000;
        r4 = (long) r4;
        r3.setAvgBitRate(r4);
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.sampleSize;
        r3.setPcmSampleDepth(r4);
        r0 = r52;
        r3 = r0.ddts;
        r0 = r27;
        r3.setFrameDuration(r0);
        r0 = r52;
        r3 = r0.ddts;
        r0 = r44;
        r3.setStreamConstruction(r0);
        r0 = r52;
        r3 = r0.coreChannelMask;
        r3 = r3 & 8;
        if (r3 > 0) goto L_0x0384;
    L_0x037c:
        r0 = r52;
        r3 = r0.coreChannelMask;
        r3 = r3 & 4096;
        if (r3 <= 0) goto L_0x05b6;
    L_0x0384:
        r0 = r52;
        r3 = r0.ddts;
        r4 = 1;
        r3.setCoreLFEPresent(r4);
    L_0x038c:
        r0 = r52;
        r3 = r0.ddts;
        r3.setCoreLayout(r12);
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.coreFramePayloadInBytes;
        r3.setCoreSize(r4);
        r0 = r52;
        r3 = r0.ddts;
        r4 = 0;
        r3.setStereoDownmix(r4);
        r0 = r52;
        r3 = r0.ddts;
        r4 = 4;
        r3.setRepresentationType(r4);
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.channelMask;
        r3.setChannelLayout(r4);
        r0 = r52;
        r3 = r0.coreMaxSampleRate;
        if (r3 <= 0) goto L_0x05c0;
    L_0x03bf:
        r0 = r52;
        r3 = r0.extAvgBitrate;
        if (r3 <= 0) goto L_0x05c0;
    L_0x03c5:
        r0 = r52;
        r3 = r0.ddts;
        r4 = 1;
        r3.setMultiAssetFlag(r4);
    L_0x03cd:
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.lbrCodingPresent;
        r3.setLBRDurationMod(r4);
        r0 = r52;
        r3 = r0.ddts;
        r4 = 0;
        r3.setReservedBoxPresent(r4);
        r3 = 0;
        r0 = r52;
        r0.channelCount = r3;
        r10 = 0;
    L_0x03e6:
        r3 = 16;
        if (r10 < r3) goto L_0x05ca;
    L_0x03ea:
        r0 = r52;
        r4 = r0.dataSource;
        r0 = r52;
        r5 = r0.dataOffset;
        r3 = r52;
        r3 = r3.generateSamples(r4, r5, r6, r8);
        r0 = r52;
        r0.samples = r3;
        r0 = r52;
        r3 = r0.samples;
        r3 = r3.size();
        r3 = new long[r3];
        r0 = r52;
        r0.sampleDurations = r3;
        r0 = r52;
        r3 = r0.sampleDurations;
        r0 = r52;
        r4 = r0.samplesPerFrame;
        r4 = (long) r4;
        java.util.Arrays.fill(r3, r4);
        r3 = 1;
        goto L_0x009a;
    L_0x0419:
        r12 = r2;
        goto L_0x02fe;
    L_0x041c:
        r44 = 21;
        r3 = "dtsh";
        r0 = r52;
        r0.type = r3;
        goto L_0x0311;
    L_0x0426:
        r3 = 1;
        r0 = r19;
        if (r0 != r3) goto L_0x0435;
    L_0x042b:
        r44 = 18;
        r3 = "dtse";
        r0 = r52;
        r0.type = r3;
        goto L_0x0311;
    L_0x0435:
        r3 = 1;
        r0 = r18;
        if (r0 != r3) goto L_0x0311;
    L_0x043a:
        r3 = "dtsh";
        r0 = r52;
        r0.type = r3;
        if (r23 != 0) goto L_0x0448;
    L_0x0442:
        if (r26 != 0) goto L_0x0448;
    L_0x0444:
        r44 = 19;
        goto L_0x0311;
    L_0x0448:
        r3 = 1;
        r0 = r23;
        if (r0 != r3) goto L_0x0453;
    L_0x044d:
        if (r26 != 0) goto L_0x0453;
    L_0x044f:
        r44 = 20;
        goto L_0x0311;
    L_0x0453:
        if (r23 != 0) goto L_0x0311;
    L_0x0455:
        r3 = 1;
        r0 = r26;
        if (r0 != r3) goto L_0x0311;
    L_0x045a:
        r44 = 21;
        goto L_0x0311;
    L_0x045e:
        r3 = 1;
        r0 = r20;
        if (r0 >= r3) goto L_0x049a;
    L_0x0463:
        if (r16 <= 0) goto L_0x0490;
    L_0x0465:
        switch(r17) {
            case 0: goto L_0x0472;
            case 2: goto L_0x047c;
            case 6: goto L_0x0486;
            default: goto L_0x0468;
        };
    L_0x0468:
        r44 = 0;
        r3 = "dtsh";
        r0 = r52;
        r0.type = r3;
        goto L_0x031f;
    L_0x0472:
        r44 = 2;
        r3 = "dtsc";
        r0 = r52;
        r0.type = r3;
        goto L_0x031f;
    L_0x047c:
        r44 = 4;
        r3 = "dtsc";
        r0 = r52;
        r0.type = r3;
        goto L_0x031f;
    L_0x0486:
        r44 = 3;
        r3 = "dtsh";
        r0 = r52;
        r0.type = r3;
        goto L_0x031f;
    L_0x0490:
        r44 = 1;
        r3 = "dtsc";
        r0 = r52;
        r0.type = r3;
        goto L_0x031f;
    L_0x049a:
        r3 = "dtsh";
        r0 = r52;
        r0.type = r3;
        if (r16 != 0) goto L_0x051a;
    L_0x04a2:
        if (r18 != 0) goto L_0x04b5;
    L_0x04a4:
        r3 = 1;
        r0 = r23;
        if (r0 != r3) goto L_0x04b5;
    L_0x04a9:
        if (r22 != 0) goto L_0x04b5;
    L_0x04ab:
        if (r24 != 0) goto L_0x04b5;
    L_0x04ad:
        if (r26 != 0) goto L_0x04b5;
    L_0x04af:
        if (r19 != 0) goto L_0x04b5;
    L_0x04b1:
        r44 = 5;
        goto L_0x031f;
    L_0x04b5:
        if (r18 != 0) goto L_0x04c8;
    L_0x04b7:
        if (r23 != 0) goto L_0x04c8;
    L_0x04b9:
        if (r22 != 0) goto L_0x04c8;
    L_0x04bb:
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x04c8;
    L_0x04c0:
        if (r26 != 0) goto L_0x04c8;
    L_0x04c2:
        if (r19 != 0) goto L_0x04c8;
    L_0x04c4:
        r44 = 6;
        goto L_0x031f;
    L_0x04c8:
        if (r18 != 0) goto L_0x04de;
    L_0x04ca:
        r3 = 1;
        r0 = r23;
        if (r0 != r3) goto L_0x04de;
    L_0x04cf:
        if (r22 != 0) goto L_0x04de;
    L_0x04d1:
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x04de;
    L_0x04d6:
        if (r26 != 0) goto L_0x04de;
    L_0x04d8:
        if (r19 != 0) goto L_0x04de;
    L_0x04da:
        r44 = 9;
        goto L_0x031f;
    L_0x04de:
        if (r18 != 0) goto L_0x04f1;
    L_0x04e0:
        if (r23 != 0) goto L_0x04f1;
    L_0x04e2:
        r3 = 1;
        r0 = r22;
        if (r0 != r3) goto L_0x04f1;
    L_0x04e7:
        if (r24 != 0) goto L_0x04f1;
    L_0x04e9:
        if (r26 != 0) goto L_0x04f1;
    L_0x04eb:
        if (r19 != 0) goto L_0x04f1;
    L_0x04ed:
        r44 = 10;
        goto L_0x031f;
    L_0x04f1:
        if (r18 != 0) goto L_0x0507;
    L_0x04f3:
        r3 = 1;
        r0 = r23;
        if (r0 != r3) goto L_0x0507;
    L_0x04f8:
        r3 = 1;
        r0 = r22;
        if (r0 != r3) goto L_0x0507;
    L_0x04fd:
        if (r24 != 0) goto L_0x0507;
    L_0x04ff:
        if (r26 != 0) goto L_0x0507;
    L_0x0501:
        if (r19 != 0) goto L_0x0507;
    L_0x0503:
        r44 = 13;
        goto L_0x031f;
    L_0x0507:
        if (r18 != 0) goto L_0x031f;
    L_0x0509:
        if (r23 != 0) goto L_0x031f;
    L_0x050b:
        if (r22 != 0) goto L_0x031f;
    L_0x050d:
        if (r24 != 0) goto L_0x031f;
    L_0x050f:
        r3 = 1;
        r0 = r26;
        if (r0 != r3) goto L_0x031f;
    L_0x0514:
        if (r19 != 0) goto L_0x031f;
    L_0x0516:
        r44 = 14;
        goto L_0x031f;
    L_0x051a:
        if (r17 != 0) goto L_0x052f;
    L_0x051c:
        if (r18 != 0) goto L_0x052f;
    L_0x051e:
        if (r23 != 0) goto L_0x052f;
    L_0x0520:
        if (r22 != 0) goto L_0x052f;
    L_0x0522:
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x052f;
    L_0x0527:
        if (r26 != 0) goto L_0x052f;
    L_0x0529:
        if (r19 != 0) goto L_0x052f;
    L_0x052b:
        r44 = 7;
        goto L_0x031f;
    L_0x052f:
        r3 = 6;
        r0 = r17;
        if (r0 != r3) goto L_0x0547;
    L_0x0534:
        if (r18 != 0) goto L_0x0547;
    L_0x0536:
        if (r23 != 0) goto L_0x0547;
    L_0x0538:
        if (r22 != 0) goto L_0x0547;
    L_0x053a:
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x0547;
    L_0x053f:
        if (r26 != 0) goto L_0x0547;
    L_0x0541:
        if (r19 != 0) goto L_0x0547;
    L_0x0543:
        r44 = 8;
        goto L_0x031f;
    L_0x0547:
        if (r17 != 0) goto L_0x055c;
    L_0x0549:
        if (r18 != 0) goto L_0x055c;
    L_0x054b:
        if (r23 != 0) goto L_0x055c;
    L_0x054d:
        r3 = 1;
        r0 = r22;
        if (r0 != r3) goto L_0x055c;
    L_0x0552:
        if (r24 != 0) goto L_0x055c;
    L_0x0554:
        if (r26 != 0) goto L_0x055c;
    L_0x0556:
        if (r19 != 0) goto L_0x055c;
    L_0x0558:
        r44 = 11;
        goto L_0x031f;
    L_0x055c:
        r3 = 6;
        r0 = r17;
        if (r0 != r3) goto L_0x0574;
    L_0x0561:
        if (r18 != 0) goto L_0x0574;
    L_0x0563:
        if (r23 != 0) goto L_0x0574;
    L_0x0565:
        r3 = 1;
        r0 = r22;
        if (r0 != r3) goto L_0x0574;
    L_0x056a:
        if (r24 != 0) goto L_0x0574;
    L_0x056c:
        if (r26 != 0) goto L_0x0574;
    L_0x056e:
        if (r19 != 0) goto L_0x0574;
    L_0x0570:
        r44 = 12;
        goto L_0x031f;
    L_0x0574:
        if (r17 != 0) goto L_0x0589;
    L_0x0576:
        if (r18 != 0) goto L_0x0589;
    L_0x0578:
        if (r23 != 0) goto L_0x0589;
    L_0x057a:
        if (r22 != 0) goto L_0x0589;
    L_0x057c:
        if (r24 != 0) goto L_0x0589;
    L_0x057e:
        r3 = 1;
        r0 = r26;
        if (r0 != r3) goto L_0x0589;
    L_0x0583:
        if (r19 != 0) goto L_0x0589;
    L_0x0585:
        r44 = 15;
        goto L_0x031f;
    L_0x0589:
        r3 = 2;
        r0 = r17;
        if (r0 != r3) goto L_0x031f;
    L_0x058e:
        if (r18 != 0) goto L_0x031f;
    L_0x0590:
        if (r23 != 0) goto L_0x031f;
    L_0x0592:
        if (r22 != 0) goto L_0x031f;
    L_0x0594:
        if (r24 != 0) goto L_0x031f;
    L_0x0596:
        r3 = 1;
        r0 = r26;
        if (r0 != r3) goto L_0x031f;
    L_0x059b:
        if (r19 != 0) goto L_0x031f;
    L_0x059d:
        r44 = 16;
        goto L_0x031f;
    L_0x05a1:
        r0 = r52;
        r3 = r0.ddts;
        r0 = r52;
        r4 = r0.coreBitRate;
        r0 = r52;
        r5 = r0.extAvgBitrate;
        r4 = r4 + r5;
        r4 = r4 * 1000;
        r4 = (long) r4;
        r3.setMaxBitRate(r4);
        goto L_0x0344;
    L_0x05b6:
        r0 = r52;
        r3 = r0.ddts;
        r4 = 0;
        r3.setCoreLFEPresent(r4);
        goto L_0x038c;
    L_0x05c0:
        r0 = r52;
        r3 = r0.ddts;
        r4 = 0;
        r3.setMultiAssetFlag(r4);
        goto L_0x03cd;
    L_0x05ca:
        r0 = r52;
        r3 = r0.channelMask;
        r3 = r3 >> r10;
        r3 = r3 & 1;
        r4 = 1;
        if (r3 != r4) goto L_0x05e1;
    L_0x05d4:
        switch(r10) {
            case 0: goto L_0x05e5;
            case 1: goto L_0x05d7;
            case 2: goto L_0x05d7;
            case 3: goto L_0x05e5;
            case 4: goto L_0x05e5;
            case 5: goto L_0x05d7;
            case 6: goto L_0x05d7;
            case 7: goto L_0x05e5;
            case 8: goto L_0x05e5;
            case 9: goto L_0x05d7;
            case 10: goto L_0x05d7;
            case 11: goto L_0x05d7;
            case 12: goto L_0x05e5;
            case 13: goto L_0x05d7;
            case 14: goto L_0x05e5;
            default: goto L_0x05d7;
        };
    L_0x05d7:
        r0 = r52;
        r3 = r0.channelCount;
        r3 = r3 + 2;
        r0 = r52;
        r0.channelCount = r3;
    L_0x05e1:
        r10 = r10 + 1;
        goto L_0x03e6;
    L_0x05e5:
        r0 = r52;
        r3 = r0.channelCount;
        r3 = r3 + 1;
        r0 = r52;
        r0.channelCount = r3;
        goto L_0x05e1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.DTSTrackImpl.readVariables():boolean");
    }

    private List<Sample> generateSamples(DataSource dataSource, int dataOffset, long dataSize, int corePresent) throws IOException {
        LookAhead la = new LookAhead(dataSource, (long) dataOffset, dataSize, corePresent);
        List<Sample> mySamples = new ArrayList();
        while (true) {
            ByteBuffer sample = la.findNextStart();
            if (sample == null) {
                System.err.println("all samples found");
                return mySamples;
            }
            mySamples.add(new C16611(sample));
        }
    }

    private int getBitRate(int rate) throws IOException {
        switch (rate) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return 32;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return 56;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return 64;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 96;
            case VideoPlayer.STATE_READY /*4*/:
                return 112;
            case VideoPlayer.STATE_ENDED /*5*/:
                return MessagesController.UPDATE_MASK_USER_PHONE;
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                return PsExtractor.AUDIO_STREAM;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return PsExtractor.VIDEO_STREAM;
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                return MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return 320;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                return 384;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                return 448;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return MessagesController.UPDATE_MASK_SELECT_DIALOG;
            case ConnectionResult.CANCELED /*13*/:
                return 576;
            case ConnectionResult.TIMEOUT /*14*/:
                return 640;
            case ConnectionResult.INTERRUPTED /*15*/:
                return 768;
            case ItemTouchHelper.START /*16*/:
                return 960;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                return MessagesController.UPDATE_MASK_PHONE;
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return 1152;
            case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                return 1280;
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                return 1344;
            case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
                return 1408;
            case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL22 /*22*/:
                return 1411;
            case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL23 /*23*/:
                return 1472;
            case NalUnitTypes.NAL_TYPE_RSV_VCL24 /*24*/:
                return 1536;
            case NalUnitTypes.NAL_TYPE_RSV_VCL25 /*25*/:
                return -1;
            default:
                throw new IOException("Unknown bitrate value");
        }
    }

    private int getSampleRate(int sfreq) throws IOException {
        switch (sfreq) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return 16000;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 32000;
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                return 11025;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return 22050;
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                return 44100;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                return 12000;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return 24000;
            case ConnectionResult.CANCELED /*13*/:
                return 48000;
            default:
                throw new IOException("Unknown Sample Rate");
        }
    }
}
