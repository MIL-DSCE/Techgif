package org.telegram.messenger.exoplayer.smoothstreaming;

import android.net.Uri;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.FormatWrapper;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.UriUtil;
import org.telegram.messenger.exoplayer.util.Util;

public class SmoothStreamingManifest {
    public final long durationUs;
    public final long dvrWindowLengthUs;
    public final boolean isLive;
    public final int lookAheadCount;
    public final int majorVersion;
    public final int minorVersion;
    public final ProtectionElement protectionElement;
    public final StreamElement[] streamElements;

    public static class ProtectionElement {
        public final byte[] data;
        public final UUID uuid;

        public ProtectionElement(UUID uuid, byte[] data) {
            this.uuid = uuid;
            this.data = data;
        }
    }

    public static class StreamElement {
        public static final int TYPE_AUDIO = 0;
        public static final int TYPE_TEXT = 2;
        public static final int TYPE_UNKNOWN = -1;
        public static final int TYPE_VIDEO = 1;
        private static final String URL_PLACEHOLDER_BITRATE = "{bitrate}";
        private static final String URL_PLACEHOLDER_START_TIME = "{start time}";
        private final String baseUri;
        public final int chunkCount;
        private final List<Long> chunkStartTimes;
        private final long[] chunkStartTimesUs;
        private final String chunkTemplate;
        public final int displayHeight;
        public final int displayWidth;
        public final String language;
        private final long lastChunkDurationUs;
        public final int maxHeight;
        public final int maxWidth;
        public final String name;
        public final int qualityLevels;
        public final String subType;
        public final long timescale;
        public final TrackElement[] tracks;
        public final int type;

        public StreamElement(String baseUri, String chunkTemplate, int type, String subType, long timescale, String name, int qualityLevels, int maxWidth, int maxHeight, int displayWidth, int displayHeight, String language, TrackElement[] tracks, List<Long> chunkStartTimes, long lastChunkDuration) {
            this.baseUri = baseUri;
            this.chunkTemplate = chunkTemplate;
            this.type = type;
            this.subType = subType;
            this.timescale = timescale;
            this.name = name;
            this.qualityLevels = qualityLevels;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.displayWidth = displayWidth;
            this.displayHeight = displayHeight;
            this.language = language;
            this.tracks = tracks;
            this.chunkCount = chunkStartTimes.size();
            this.chunkStartTimes = chunkStartTimes;
            this.lastChunkDurationUs = Util.scaleLargeTimestamp(lastChunkDuration, C0747C.MICROS_PER_SECOND, timescale);
            this.chunkStartTimesUs = Util.scaleLargeTimestamps(chunkStartTimes, C0747C.MICROS_PER_SECOND, timescale);
        }

        public int getChunkIndex(long timeUs) {
            return Util.binarySearchFloor(this.chunkStartTimesUs, timeUs, true, true);
        }

        public long getStartTimeUs(int chunkIndex) {
            return this.chunkStartTimesUs[chunkIndex];
        }

        public long getChunkDurationUs(int chunkIndex) {
            return chunkIndex == this.chunkCount + TYPE_UNKNOWN ? this.lastChunkDurationUs : this.chunkStartTimesUs[chunkIndex + TYPE_VIDEO] - this.chunkStartTimesUs[chunkIndex];
        }

        public Uri buildRequestUri(int track, int chunkIndex) {
            boolean z;
            boolean z2 = true;
            if (this.tracks != null) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkState(z);
            if (this.chunkStartTimes != null) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkState(z);
            if (chunkIndex >= this.chunkStartTimes.size()) {
                z2 = false;
            }
            Assertions.checkState(z2);
            return UriUtil.resolveToUri(this.baseUri, this.chunkTemplate.replace(URL_PLACEHOLDER_BITRATE, Integer.toString(this.tracks[track].format.bitrate)).replace(URL_PLACEHOLDER_START_TIME, ((Long) this.chunkStartTimes.get(chunkIndex)).toString()));
        }
    }

    public static class TrackElement implements FormatWrapper {
        public final byte[][] csd;
        public final Format format;

        public TrackElement(int index, int bitrate, String mimeType, byte[][] csd, int maxWidth, int maxHeight, int sampleRate, int numChannels, String language) {
            this.csd = csd;
            this.format = new Format(String.valueOf(index), mimeType, maxWidth, maxHeight, GroundOverlayOptions.NO_DIMENSION, numChannels, sampleRate, bitrate, language);
        }

        public Format getFormat() {
            return this.format;
        }
    }

    public SmoothStreamingManifest(int majorVersion, int minorVersion, long timescale, long duration, long dvrWindowLength, int lookAheadCount, boolean isLive, ProtectionElement protectionElement, StreamElement[] streamElements) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.lookAheadCount = lookAheadCount;
        this.isLive = isLive;
        this.protectionElement = protectionElement;
        this.streamElements = streamElements;
        this.dvrWindowLengthUs = dvrWindowLength == 0 ? -1 : Util.scaleLargeTimestamp(dvrWindowLength, C0747C.MICROS_PER_SECOND, timescale);
        this.durationUs = duration == 0 ? -1 : Util.scaleLargeTimestamp(duration, C0747C.MICROS_PER_SECOND, timescale);
    }
}
