package org.telegram.messenger.exoplayer.extractor.mp4;

import com.googlecode.mp4parser.boxes.apple.QuicktimeTextSampleEntry;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.util.Util;

public final class Track {
    public static final int TYPE_sbtl;
    public static final int TYPE_soun;
    public static final int TYPE_subt;
    public static final int TYPE_text;
    public static final int TYPE_vide;
    public final long durationUs;
    public final long[] editListDurations;
    public final long[] editListMediaTimes;
    public final int id;
    public final MediaFormat mediaFormat;
    public final long movieTimescale;
    public final int nalUnitLengthFieldLength;
    public final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
    public final long timescale;
    public final int type;

    static {
        TYPE_vide = Util.getIntegerCodeForString("vide");
        TYPE_soun = Util.getIntegerCodeForString("soun");
        TYPE_text = Util.getIntegerCodeForString(QuicktimeTextSampleEntry.TYPE);
        TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
        TYPE_subt = Util.getIntegerCodeForString("subt");
    }

    public Track(int id, int type, long timescale, long movieTimescale, long durationUs, MediaFormat mediaFormat, TrackEncryptionBox[] sampleDescriptionEncryptionBoxes, int nalUnitLengthFieldLength, long[] editListDurations, long[] editListMediaTimes) {
        this.id = id;
        this.type = type;
        this.timescale = timescale;
        this.movieTimescale = movieTimescale;
        this.durationUs = durationUs;
        this.mediaFormat = mediaFormat;
        this.sampleDescriptionEncryptionBoxes = sampleDescriptionEncryptionBoxes;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        this.editListDurations = editListDurations;
        this.editListMediaTimes = editListMediaTimes;
    }
}
