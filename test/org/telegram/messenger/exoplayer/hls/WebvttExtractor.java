package org.telegram.messenger.exoplayer.hls;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class WebvttExtractor implements Extractor {
    private static final Pattern LOCAL_TIMESTAMP;
    private static final Pattern MEDIA_TIMESTAMP;
    private ExtractorOutput output;
    private final PtsTimestampAdjuster ptsTimestampAdjuster;
    private byte[] sampleData;
    private final ParsableByteArray sampleDataWrapper;
    private int sampleSize;

    static {
        LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
        MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");
    }

    public WebvttExtractor(PtsTimestampAdjuster ptsTimestampAdjuster) {
        this.ptsTimestampAdjuster = ptsTimestampAdjuster;
        this.sampleDataWrapper = new ParsableByteArray();
        this.sampleData = new byte[MessagesController.UPDATE_MASK_PHONE];
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        throw new IllegalStateException();
    }

    public void init(ExtractorOutput output) {
        this.output = output;
        output.seekMap(SeekMap.UNSEEKABLE);
    }

    public void seek() {
        throw new IllegalStateException();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int currentFileSize = (int) input.getLength();
        if (this.sampleSize == this.sampleData.length) {
            int i;
            byte[] bArr = this.sampleData;
            if (currentFileSize != -1) {
                i = currentFileSize;
            } else {
                i = this.sampleData.length;
            }
            this.sampleData = Arrays.copyOf(bArr, (i * 3) / 2);
        }
        int bytesRead = input.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
        if (bytesRead != -1) {
            this.sampleSize += bytesRead;
            if (currentFileSize == -1 || this.sampleSize != currentFileSize) {
                return 0;
            }
        }
        processSample();
        return -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processSample() throws org.telegram.messenger.exoplayer.ParserException {
        /*
        r24 = this;
        r17 = new org.telegram.messenger.exoplayer.util.ParsableByteArray;
        r0 = r24;
        r8 = r0.sampleData;
        r0 = r17;
        r0.<init>(r8);
        org.telegram.messenger.exoplayer.text.webvtt.WebvttParserUtil.validateWebvttHeaderLine(r17);
        r22 = 0;
        r20 = 0;
    L_0x0012:
        r14 = r17.readLine();
        r8 = android.text.TextUtils.isEmpty(r14);
        if (r8 != 0) goto L_0x0087;
    L_0x001c:
        r8 = "X-TIMESTAMP-MAP";
        r8 = r14.startsWith(r8);
        if (r8 == 0) goto L_0x0012;
    L_0x0024:
        r8 = LOCAL_TIMESTAMP;
        r15 = r8.matcher(r14);
        r8 = r15.find();
        if (r8 != 0) goto L_0x0049;
    L_0x0030:
        r8 = new org.telegram.messenger.exoplayer.ParserException;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "X-TIMESTAMP-MAP doesn't contain local timestamp: ";
        r9 = r9.append(r10);
        r9 = r9.append(r14);
        r9 = r9.toString();
        r8.<init>(r9);
        throw r8;
    L_0x0049:
        r8 = MEDIA_TIMESTAMP;
        r16 = r8.matcher(r14);
        r8 = r16.find();
        if (r8 != 0) goto L_0x006e;
    L_0x0055:
        r8 = new org.telegram.messenger.exoplayer.ParserException;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "X-TIMESTAMP-MAP doesn't contain media timestamp: ";
        r9 = r9.append(r10);
        r9 = r9.append(r14);
        r9 = r9.toString();
        r8.<init>(r9);
        throw r8;
    L_0x006e:
        r8 = 1;
        r8 = r15.group(r8);
        r22 = org.telegram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r8);
        r8 = 1;
        r0 = r16;
        r8 = r0.group(r8);
        r8 = java.lang.Long.parseLong(r8);
        r20 = org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster.ptsToUs(r8);
        goto L_0x0012;
    L_0x0087:
        r4 = org.telegram.messenger.exoplayer.text.webvtt.WebvttCueParser.findNextCueHeader(r17);
        if (r4 != 0) goto L_0x0095;
    L_0x008d:
        r8 = 0;
        r0 = r24;
        r0.buildTrackOutput(r8);
    L_0x0094:
        return;
    L_0x0095:
        r8 = 1;
        r8 = r4.group(r8);
        r12 = org.telegram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r8);
        r0 = r24;
        r8 = r0.ptsTimestampAdjuster;
        r10 = r12 + r20;
        r10 = r10 - r22;
        r10 = org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster.usToPts(r10);
        r6 = r8.adjustTimestamp(r10);
        r18 = r6 - r12;
        r0 = r24;
        r1 = r18;
        r5 = r0.buildTrackOutput(r1);
        r0 = r24;
        r8 = r0.sampleDataWrapper;
        r0 = r24;
        r9 = r0.sampleData;
        r0 = r24;
        r10 = r0.sampleSize;
        r8.reset(r9, r10);
        r0 = r24;
        r8 = r0.sampleDataWrapper;
        r0 = r24;
        r9 = r0.sampleSize;
        r5.sampleData(r8, r9);
        r8 = 1;
        r0 = r24;
        r9 = r0.sampleSize;
        r10 = 0;
        r11 = 0;
        r5.sampleMetadata(r6, r8, r9, r10, r11);
        goto L_0x0094;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.hls.WebvttExtractor.processSample():void");
    }

    private TrackOutput buildTrackOutput(long subsampleOffsetUs) {
        TrackOutput trackOutput = this.output.track(0);
        trackOutput.format(MediaFormat.createTextFormat(TtmlNode.ATTR_ID, MimeTypes.TEXT_VTT, -1, -1, "en", subsampleOffsetUs));
        this.output.endTracks();
        return trackOutput;
    }
}
