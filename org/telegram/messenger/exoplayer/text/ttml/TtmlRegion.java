package org.telegram.messenger.exoplayer.text.ttml;

import org.telegram.messenger.exoplayer.text.Cue;

final class TtmlRegion {
    public final float line;
    public final float position;
    public final float width;

    public TtmlRegion() {
        this(Cue.DIMEN_UNSET, Cue.DIMEN_UNSET, Cue.DIMEN_UNSET);
    }

    public TtmlRegion(float position, float line, float width) {
        this.position = position;
        this.line = line;
        this.width = width;
    }
}
