package org.telegram.messenger.exoplayer.text.webvtt;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class Mp4WebvttParser implements SubtitleParser {
    private static final int BOX_HEADER_SIZE = 8;
    private static final int TYPE_payl;
    private static final int TYPE_sttg;
    private static final int TYPE_vttc;
    private final Builder builder;
    private final ParsableByteArray sampleData;

    static {
        TYPE_payl = Util.getIntegerCodeForString("payl");
        TYPE_sttg = Util.getIntegerCodeForString("sttg");
        TYPE_vttc = Util.getIntegerCodeForString("vttc");
    }

    public Mp4WebvttParser() {
        this.sampleData = new ParsableByteArray();
        this.builder = new Builder();
    }

    public boolean canParse(String mimeType) {
        return MimeTypes.APPLICATION_MP4VTT.equals(mimeType);
    }

    public Mp4WebvttSubtitle parse(byte[] bytes, int offset, int length) throws ParserException {
        this.sampleData.reset(bytes, offset + length);
        this.sampleData.setPosition(offset);
        List<Cue> resultingCueList = new ArrayList();
        while (this.sampleData.bytesLeft() > 0) {
            if (this.sampleData.bytesLeft() < BOX_HEADER_SIZE) {
                throw new ParserException("Incomplete Mp4Webvtt Top Level box header found.");
            }
            int boxSize = this.sampleData.readInt();
            if (this.sampleData.readInt() == TYPE_vttc) {
                resultingCueList.add(parseVttCueBox(this.sampleData, this.builder, boxSize - 8));
            } else {
                this.sampleData.skipBytes(boxSize - 8);
            }
        }
        return new Mp4WebvttSubtitle(resultingCueList);
    }

    private static Cue parseVttCueBox(ParsableByteArray sampleData, Builder builder, int remainingCueBoxBytes) throws ParserException {
        builder.reset();
        while (remainingCueBoxBytes > 0) {
            if (remainingCueBoxBytes < BOX_HEADER_SIZE) {
                throw new ParserException("Incomplete vtt cue box header found.");
            }
            int boxSize = sampleData.readInt();
            int boxType = sampleData.readInt();
            remainingCueBoxBytes -= 8;
            int payloadLength = boxSize - 8;
            String boxPayload = new String(sampleData.data, sampleData.getPosition(), payloadLength);
            sampleData.skipBytes(payloadLength);
            remainingCueBoxBytes -= payloadLength;
            if (boxType == TYPE_sttg) {
                WebvttCueParser.parseCueSettingsList(boxPayload, builder);
            } else if (boxType == TYPE_payl) {
                WebvttCueParser.parseCueText(boxPayload.trim(), builder);
            }
        }
        return builder.build();
    }
}
