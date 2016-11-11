package org.telegram.messenger.exoplayer.extractor.flv;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class FlvExtractor implements Extractor, SeekMap {
    private static final int FLV_HEADER_SIZE = 9;
    private static final int FLV_TAG;
    private static final int FLV_TAG_HEADER_SIZE = 11;
    private static final int STATE_READING_FLV_HEADER = 1;
    private static final int STATE_READING_TAG_DATA = 4;
    private static final int STATE_READING_TAG_HEADER = 3;
    private static final int STATE_SKIPPING_TO_TAG_HEADER = 2;
    private static final int TAG_TYPE_AUDIO = 8;
    private static final int TAG_TYPE_SCRIPT_DATA = 18;
    private static final int TAG_TYPE_VIDEO = 9;
    private AudioTagPayloadReader audioReader;
    private int bytesToNextTagHeader;
    private ExtractorOutput extractorOutput;
    private final ParsableByteArray headerBuffer;
    private ScriptTagPayloadReader metadataReader;
    private int parserState;
    private final ParsableByteArray scratch;
    private final ParsableByteArray tagData;
    public int tagDataSize;
    private final ParsableByteArray tagHeaderBuffer;
    public long tagTimestampUs;
    public int tagType;
    private VideoTagPayloadReader videoReader;

    static {
        FLV_TAG = Util.getIntegerCodeForString("FLV");
    }

    public FlvExtractor() {
        this.scratch = new ParsableByteArray((int) STATE_READING_TAG_DATA);
        this.headerBuffer = new ParsableByteArray((int) TAG_TYPE_VIDEO);
        this.tagHeaderBuffer = new ParsableByteArray((int) FLV_TAG_HEADER_SIZE);
        this.tagData = new ParsableByteArray();
        this.parserState = STATE_READING_FLV_HEADER;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, FLV_TAG, STATE_READING_TAG_HEADER);
        this.scratch.setPosition(FLV_TAG);
        if (this.scratch.readUnsignedInt24() != FLV_TAG) {
            return false;
        }
        input.peekFully(this.scratch.data, FLV_TAG, STATE_SKIPPING_TO_TAG_HEADER);
        this.scratch.setPosition(FLV_TAG);
        if ((this.scratch.readUnsignedShort() & Callback.DEFAULT_SWIPE_ANIMATION_DURATION) != 0) {
            return false;
        }
        input.peekFully(this.scratch.data, FLV_TAG, STATE_READING_TAG_DATA);
        this.scratch.setPosition(FLV_TAG);
        int dataOffset = this.scratch.readInt();
        input.resetPeekPosition();
        input.advancePeekPosition(dataOffset);
        input.peekFully(this.scratch.data, FLV_TAG, STATE_READING_TAG_DATA);
        this.scratch.setPosition(FLV_TAG);
        if (this.scratch.readInt() == 0) {
            return true;
        }
        return false;
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek() {
        this.parserState = STATE_READING_FLV_HEADER;
        this.bytesToNextTagHeader = FLV_TAG;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case STATE_READING_FLV_HEADER /*1*/:
                    if (readFlvHeader(input)) {
                        break;
                    }
                    return -1;
                case STATE_SKIPPING_TO_TAG_HEADER /*2*/:
                    skipToTagHeader(input);
                    break;
                case STATE_READING_TAG_HEADER /*3*/:
                    if (readTagHeader(input)) {
                        break;
                    }
                    return -1;
                case STATE_READING_TAG_DATA /*4*/:
                    if (!readTagData(input)) {
                        break;
                    }
                    return FLV_TAG;
                default:
                    break;
            }
        }
    }

    private boolean readFlvHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (!input.readFully(this.headerBuffer.data, FLV_TAG, TAG_TYPE_VIDEO, true)) {
            return false;
        }
        boolean hasAudio;
        boolean hasVideo;
        this.headerBuffer.setPosition(FLV_TAG);
        this.headerBuffer.skipBytes(STATE_READING_TAG_DATA);
        int flags = this.headerBuffer.readUnsignedByte();
        if ((flags & STATE_READING_TAG_DATA) != 0) {
            hasAudio = true;
        } else {
            hasAudio = false;
        }
        if ((flags & STATE_READING_FLV_HEADER) != 0) {
            hasVideo = true;
        } else {
            hasVideo = false;
        }
        if (hasAudio && this.audioReader == null) {
            this.audioReader = new AudioTagPayloadReader(this.extractorOutput.track(TAG_TYPE_AUDIO));
        }
        if (hasVideo && this.videoReader == null) {
            this.videoReader = new VideoTagPayloadReader(this.extractorOutput.track(TAG_TYPE_VIDEO));
        }
        if (this.metadataReader == null) {
            this.metadataReader = new ScriptTagPayloadReader(null);
        }
        this.extractorOutput.endTracks();
        this.extractorOutput.seekMap(this);
        this.bytesToNextTagHeader = (this.headerBuffer.readInt() - 9) + STATE_READING_TAG_DATA;
        this.parserState = STATE_SKIPPING_TO_TAG_HEADER;
        return true;
    }

    private void skipToTagHeader(ExtractorInput input) throws IOException, InterruptedException {
        input.skipFully(this.bytesToNextTagHeader);
        this.bytesToNextTagHeader = FLV_TAG;
        this.parserState = STATE_READING_TAG_HEADER;
    }

    private boolean readTagHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (!input.readFully(this.tagHeaderBuffer.data, FLV_TAG, FLV_TAG_HEADER_SIZE, true)) {
            return false;
        }
        this.tagHeaderBuffer.setPosition(FLV_TAG);
        this.tagType = this.tagHeaderBuffer.readUnsignedByte();
        this.tagDataSize = this.tagHeaderBuffer.readUnsignedInt24();
        this.tagTimestampUs = (long) this.tagHeaderBuffer.readUnsignedInt24();
        this.tagTimestampUs = (((long) (this.tagHeaderBuffer.readUnsignedByte() << 24)) | this.tagTimestampUs) * 1000;
        this.tagHeaderBuffer.skipBytes(STATE_READING_TAG_HEADER);
        this.parserState = STATE_READING_TAG_DATA;
        return true;
    }

    private boolean readTagData(ExtractorInput input) throws IOException, InterruptedException {
        boolean wasConsumed = true;
        if (this.tagType == TAG_TYPE_AUDIO && this.audioReader != null) {
            this.audioReader.consume(prepareTagData(input), this.tagTimestampUs);
        } else if (this.tagType == TAG_TYPE_VIDEO && this.videoReader != null) {
            this.videoReader.consume(prepareTagData(input), this.tagTimestampUs);
        } else if (this.tagType != TAG_TYPE_SCRIPT_DATA || this.metadataReader == null) {
            input.skipFully(this.tagDataSize);
            wasConsumed = false;
        } else {
            this.metadataReader.consume(prepareTagData(input), this.tagTimestampUs);
            if (this.metadataReader.getDurationUs() != -1) {
                if (this.audioReader != null) {
                    this.audioReader.setDurationUs(this.metadataReader.getDurationUs());
                }
                if (this.videoReader != null) {
                    this.videoReader.setDurationUs(this.metadataReader.getDurationUs());
                }
            }
        }
        this.bytesToNextTagHeader = STATE_READING_TAG_DATA;
        this.parserState = STATE_SKIPPING_TO_TAG_HEADER;
        return wasConsumed;
    }

    private ParsableByteArray prepareTagData(ExtractorInput input) throws IOException, InterruptedException {
        if (this.tagDataSize > this.tagData.capacity()) {
            this.tagData.reset(new byte[Math.max(this.tagData.capacity() * STATE_SKIPPING_TO_TAG_HEADER, this.tagDataSize)], FLV_TAG);
        } else {
            this.tagData.setPosition(FLV_TAG);
        }
        this.tagData.setLimit(this.tagDataSize);
        input.readFully(this.tagData.data, FLV_TAG, this.tagDataSize);
        return this.tagData;
    }

    public boolean isSeekable() {
        return false;
    }

    public long getPosition(long timeUs) {
        return 0;
    }
}
