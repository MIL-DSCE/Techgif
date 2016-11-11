package org.telegram.messenger.exoplayer.extractor.mp3;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.MpegAudioHeader;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class Mp3Extractor implements Extractor {
    private static final int HEADER_MASK = -128000;
    private static final int INFO_HEADER;
    private static final int MAX_SNIFF_BYTES = 4096;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int VBRI_HEADER;
    private static final int XING_HEADER;
    private long basisTimeUs;
    private ExtractorOutput extractorOutput;
    private final long forcedFirstSampleTimestampUs;
    private GaplessInfo gaplessInfo;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;

    interface Seeker extends SeekMap {
        long getDurationUs();

        long getTimeUs(long j);
    }

    static {
        XING_HEADER = Util.getIntegerCodeForString("Xing");
        INFO_HEADER = Util.getIntegerCodeForString("Info");
        VBRI_HEADER = Util.getIntegerCodeForString("VBRI");
    }

    public Mp3Extractor() {
        this(-1);
    }

    public Mp3Extractor(long forcedFirstSampleTimestampUs) {
        this.forcedFirstSampleTimestampUs = forcedFirstSampleTimestampUs;
        this.scratch = new ParsableByteArray(4);
        this.synchronizedHeader = new MpegAudioHeader();
        this.basisTimeUs = -1;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return synchronize(input, true);
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(INFO_HEADER);
        extractorOutput.endTracks();
    }

    public void seek() {
        this.synchronizedHeaderData = INFO_HEADER;
        this.samplesRead = 0;
        this.basisTimeUs = -1;
        this.sampleBytesRemaining = INFO_HEADER;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.synchronizedHeaderData == 0 && !synchronizeCatchingEndOfInput(input)) {
            return -1;
        }
        if (this.seeker == null) {
            setupSeeker(input);
            this.extractorOutput.seekMap(this.seeker);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(null, this.synchronizedHeader.mimeType, -1, MAX_SNIFF_BYTES, this.seeker.getDurationUs(), this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, null, null);
            if (this.gaplessInfo != null) {
                mediaFormat = mediaFormat.copyWithGaplessInfo(this.gaplessInfo.encoderDelay, this.gaplessInfo.encoderPadding);
            }
            this.trackOutput.format(mediaFormat);
        }
        return readSample(input);
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.sampleBytesRemaining == 0) {
            if (!maybeResynchronize(extractorInput)) {
                return -1;
            }
            if (this.basisTimeUs == -1) {
                this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                if (this.forcedFirstSampleTimestampUs != -1) {
                    this.basisTimeUs += this.forcedFirstSampleTimestampUs - this.seeker.getTimeUs(0);
                }
            }
            this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
        }
        int bytesAppended = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (bytesAppended == -1) {
            return -1;
        }
        this.sampleBytesRemaining -= bytesAppended;
        if (this.sampleBytesRemaining > 0) {
            return INFO_HEADER;
        }
        this.trackOutput.sampleMetadata(this.basisTimeUs + ((this.samplesRead * C0747C.MICROS_PER_SECOND) / ((long) this.synchronizedHeader.sampleRate)), 1, this.synchronizedHeader.frameSize, INFO_HEADER, null);
        this.samplesRead += (long) this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = INFO_HEADER;
        return INFO_HEADER;
    }

    private boolean maybeResynchronize(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.resetPeekPosition();
        if (!extractorInput.peekFully(this.scratch.data, INFO_HEADER, 4, true)) {
            return false;
        }
        this.scratch.setPosition(INFO_HEADER);
        int sampleHeaderData = this.scratch.readInt();
        if ((sampleHeaderData & HEADER_MASK) != (this.synchronizedHeaderData & HEADER_MASK) || MpegAudioHeader.getFrameSize(sampleHeaderData) == -1) {
            this.synchronizedHeaderData = INFO_HEADER;
            extractorInput.skipFully(1);
            return synchronizeCatchingEndOfInput(extractorInput);
        }
        MpegAudioHeader.populateHeader(sampleHeaderData, this.synchronizedHeader);
        return true;
    }

    private boolean synchronizeCatchingEndOfInput(ExtractorInput input) throws IOException, InterruptedException {
        boolean z = false;
        try {
            z = synchronize(input, false);
        } catch (EOFException e) {
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean synchronize(org.telegram.messenger.exoplayer.extractor.ExtractorInput r11, boolean r12) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r10 = this;
        r4 = 0;
        r5 = 0;
        r0 = 0;
        r3 = 0;
        r11.resetPeekPosition();
        r6 = r11.getPosition();
        r8 = 0;
        r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r6 != 0) goto L_0x0021;
    L_0x0011:
        r6 = org.telegram.messenger.exoplayer.extractor.mp3.Id3Util.parseId3(r11);
        r10.gaplessInfo = r6;
        r6 = r11.getPeekPosition();
        r3 = (int) r6;
        if (r12 != 0) goto L_0x0021;
    L_0x001e:
        r11.skipFully(r3);
    L_0x0021:
        if (r12 == 0) goto L_0x0029;
    L_0x0023:
        r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        if (r4 != r6) goto L_0x0029;
    L_0x0027:
        r6 = 0;
    L_0x0028:
        return r6;
    L_0x0029:
        if (r12 != 0) goto L_0x0037;
    L_0x002b:
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        if (r4 != r6) goto L_0x0037;
    L_0x002f:
        r6 = new org.telegram.messenger.exoplayer.ParserException;
        r7 = "Searched too many bytes.";
        r6.<init>(r7);
        throw r6;
    L_0x0037:
        r6 = r10.scratch;
        r6 = r6.data;
        r7 = 0;
        r8 = 4;
        r9 = 1;
        r6 = r11.peekFully(r6, r7, r8, r9);
        if (r6 != 0) goto L_0x0046;
    L_0x0044:
        r6 = 0;
        goto L_0x0028;
    L_0x0046:
        r6 = r10.scratch;
        r7 = 0;
        r6.setPosition(r7);
        r6 = r10.scratch;
        r2 = r6.readInt();
        if (r0 == 0) goto L_0x005e;
    L_0x0054:
        r6 = -128000; // 0xfffffffffffe0c00 float:NaN double:NaN;
        r6 = r6 & r2;
        r7 = -128000; // 0xfffffffffffe0c00 float:NaN double:NaN;
        r7 = r7 & r0;
        if (r6 != r7) goto L_0x0065;
    L_0x005e:
        r1 = org.telegram.messenger.exoplayer.util.MpegAudioHeader.getFrameSize(r2);
        r6 = -1;
        if (r1 != r6) goto L_0x0079;
    L_0x0065:
        r5 = 0;
        r0 = 0;
        r4 = r4 + 1;
        if (r12 == 0) goto L_0x0074;
    L_0x006b:
        r11.resetPeekPosition();
        r6 = r3 + r4;
        r11.advancePeekPosition(r6);
        goto L_0x0021;
    L_0x0074:
        r6 = 1;
        r11.skipFully(r6);
        goto L_0x0021;
    L_0x0079:
        r5 = r5 + 1;
        r6 = 1;
        if (r5 != r6) goto L_0x008a;
    L_0x007e:
        r6 = r10.synchronizedHeader;
        org.telegram.messenger.exoplayer.util.MpegAudioHeader.populateHeader(r2, r6);
        r0 = r2;
    L_0x0084:
        r6 = r1 + -4;
        r11.advancePeekPosition(r6);
        goto L_0x0021;
    L_0x008a:
        r6 = 4;
        if (r5 != r6) goto L_0x0084;
    L_0x008d:
        if (r12 == 0) goto L_0x0098;
    L_0x008f:
        r6 = r3 + r4;
        r11.skipFully(r6);
    L_0x0094:
        r10.synchronizedHeaderData = r0;
        r6 = 1;
        goto L_0x0028;
    L_0x0098:
        r11.resetPeekPosition();
        goto L_0x0094;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.extractor.mp3.Mp3Extractor.synchronize(org.telegram.messenger.exoplayer.extractor.ExtractorInput, boolean):boolean");
    }

    private void setupSeeker(ExtractorInput input) throws IOException, InterruptedException {
        int xingBase = 21;
        ParsableByteArray frame = new ParsableByteArray(this.synchronizedHeader.frameSize);
        input.peekFully(frame.data, INFO_HEADER, this.synchronizedHeader.frameSize);
        long position = input.getPosition();
        long length = input.getLength();
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                xingBase = 36;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            xingBase = 13;
        }
        frame.setPosition(xingBase);
        int headerData = frame.readInt();
        if (headerData == XING_HEADER || headerData == INFO_HEADER) {
            this.seeker = XingSeeker.create(this.synchronizedHeader, frame, position, length);
            if (this.seeker != null && this.gaplessInfo == null) {
                input.resetPeekPosition();
                input.advancePeekPosition(xingBase + 141);
                input.peekFully(this.scratch.data, INFO_HEADER, 3);
                this.scratch.setPosition(INFO_HEADER);
                this.gaplessInfo = GaplessInfo.createFromXingHeaderValue(this.scratch.readUnsignedInt24());
            }
            input.skipFully(this.synchronizedHeader.frameSize);
        } else {
            frame.setPosition(36);
            if (frame.readInt() == VBRI_HEADER) {
                this.seeker = VbriSeeker.create(this.synchronizedHeader, frame, position, length);
                input.skipFully(this.synchronizedHeader.frameSize);
            }
        }
        if (this.seeker == null) {
            input.resetPeekPosition();
            input.peekFully(this.scratch.data, INFO_HEADER, 4);
            this.scratch.setPosition(INFO_HEADER);
            MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
            this.seeker = new ConstantBitrateSeeker(input.getPosition(), this.synchronizedHeader.bitrate, length);
        }
    }
}
