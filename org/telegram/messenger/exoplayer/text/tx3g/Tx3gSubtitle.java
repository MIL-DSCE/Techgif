package org.telegram.messenger.exoplayer.text.tx3g;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.util.Assertions;

final class Tx3gSubtitle implements Subtitle {
    private final List<Cue> cues;

    public Tx3gSubtitle(Cue cue) {
        this.cues = Collections.singletonList(cue);
    }

    public int getNextEventTimeIndex(long timeUs) {
        return timeUs < 0 ? 0 : -1;
    }

    public int getEventTimeCount() {
        return 1;
    }

    public long getEventTime(int index) {
        Assertions.checkArgument(index == 0);
        return 0;
    }

    public long getLastEventTime() {
        return 0;
    }

    public List<Cue> getCues(long timeUs) {
        return timeUs >= 0 ? this.cues : Collections.emptyList();
    }
}
