package org.telegram.messenger.exoplayer.text.tx3g;

import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class Tx3gParser implements SubtitleParser {
    public boolean canParse(String mimeType) {
        return MimeTypes.APPLICATION_TX3G.equals(mimeType);
    }

    public Subtitle parse(byte[] bytes, int offset, int length) {
        return new Tx3gSubtitle(new Cue(new String(bytes, offset, length)));
    }
}
