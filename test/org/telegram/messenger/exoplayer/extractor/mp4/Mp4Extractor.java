package org.telegram.messenger.exoplayer.extractor.mp4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class Mp4Extractor implements Extractor, SeekMap {
    private static final int BRAND_QUICKTIME;
    private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144;
    private static final int STATE_AFTER_SEEK = 0;
    private static final int STATE_READING_ATOM_HEADER = 1;
    private static final int STATE_READING_ATOM_PAYLOAD = 2;
    private static final int STATE_READING_SAMPLE = 3;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final Stack<ContainerAtom> containerAtoms;
    private ExtractorOutput extractorOutput;
    private boolean isQuickTime;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private Mp4Track[] tracks;

    private static final class Mp4Track {
        public int sampleIndex;
        public final TrackSampleTable sampleTable;
        public final Track track;
        public final TrackOutput trackOutput;

        public Mp4Track(Track track, TrackSampleTable sampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = sampleTable;
            this.trackOutput = trackOutput;
        }
    }

    static {
        BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
    }

    public Mp4Extractor() {
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new Stack();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        enterReadingAtomHeaderState();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffUnfragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek() {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = STATE_AFTER_SEEK;
        this.sampleBytesWritten = STATE_AFTER_SEEK;
        this.sampleCurrentNalBytesRemaining = STATE_AFTER_SEEK;
        this.parserState = STATE_AFTER_SEEK;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case STATE_AFTER_SEEK /*0*/:
                    if (input.getPosition() != 0) {
                        this.parserState = STATE_READING_SAMPLE;
                        break;
                    }
                    enterReadingAtomHeaderState();
                    break;
                case STATE_READING_ATOM_HEADER /*1*/:
                    if (readAtomHeader(input)) {
                        break;
                    }
                    return -1;
                case STATE_READING_ATOM_PAYLOAD /*2*/:
                    if (!readAtomPayload(input, seekPosition)) {
                        break;
                    }
                    return STATE_READING_ATOM_HEADER;
                default:
                    return readSample(input, seekPosition);
            }
        }
    }

    public boolean isSeekable() {
        return true;
    }

    public long getPosition(long timeUs) {
        long earliestSamplePosition = PtsTimestampAdjuster.DO_NOT_OFFSET;
        for (int trackIndex = STATE_AFTER_SEEK; trackIndex < this.tracks.length; trackIndex += STATE_READING_ATOM_HEADER) {
            TrackSampleTable sampleTable = this.tracks[trackIndex].sampleTable;
            int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
            if (sampleIndex == -1) {
                sampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
            }
            this.tracks[trackIndex].sampleIndex = sampleIndex;
            long offset = sampleTable.offsets[sampleIndex];
            if (offset < earliestSamplePosition) {
                earliestSamplePosition = offset;
            }
        }
        return earliestSamplePosition;
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = STATE_READING_ATOM_HEADER;
        this.atomHeaderBytesRead = STATE_AFTER_SEEK;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.data, STATE_AFTER_SEEK, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(STATE_AFTER_SEEK);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        if (this.atomSize == 1) {
            input.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long endPosition = (input.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
            this.containerAtoms.add(new ContainerAtom(this.atomType, endPosition));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(endPosition);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            boolean z;
            if (this.atomHeaderBytesRead == 8) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkState(z);
            if (this.atomSize <= 2147483647L) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkState(z);
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, STATE_AFTER_SEEK, this.atomData.data, STATE_AFTER_SEEK, 8);
            this.parserState = STATE_READING_ATOM_PAYLOAD;
        } else {
            this.atomData = null;
            this.parserState = STATE_READING_ATOM_PAYLOAD;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long atomPayloadSize = this.atomSize - ((long) this.atomHeaderBytesRead);
        long atomEndPosition = input.getPosition() + atomPayloadSize;
        boolean seekRequired = false;
        if (this.atomData != null) {
            input.readFully(this.atomData.data, this.atomHeaderBytesRead, (int) atomPayloadSize);
            if (this.atomType == Atom.TYPE_ftyp) {
                this.isQuickTime = processFtypAtom(this.atomData);
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(new LeafAtom(this.atomType, this.atomData));
            }
        } else if (atomPayloadSize < RELOAD_MINIMUM_SEEK_DISTANCE) {
            input.skipFully((int) atomPayloadSize);
        } else {
            positionHolder.position = input.getPosition() + atomPayloadSize;
            seekRequired = true;
        }
        processAtomEnded(atomEndPosition);
        if (!seekRequired || this.parserState == STATE_READING_SAMPLE) {
            return false;
        }
        return true;
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == atomEndPosition) {
            ContainerAtom containerAtom = (ContainerAtom) this.containerAtoms.pop();
            if (containerAtom.type == Atom.TYPE_moov) {
                processMoovAtom(containerAtom);
                this.containerAtoms.clear();
                this.parserState = STATE_READING_SAMPLE;
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(containerAtom);
            }
        }
        if (this.parserState != STATE_READING_SAMPLE) {
            enterReadingAtomHeaderState();
        }
    }

    private static boolean processFtypAtom(ParsableByteArray atomData) {
        atomData.setPosition(8);
        if (atomData.readInt() == BRAND_QUICKTIME) {
            return true;
        }
        atomData.skipBytes(4);
        while (atomData.bytesLeft() > 0) {
            if (atomData.readInt() == BRAND_QUICKTIME) {
                return true;
            }
        }
        return false;
    }

    private void processMoovAtom(ContainerAtom moov) throws ParserException {
        List<Mp4Track> tracks = new ArrayList();
        long earliestSampleOffset = PtsTimestampAdjuster.DO_NOT_OFFSET;
        GaplessInfo gaplessInfo = null;
        LeafAtom udta = moov.getLeafAtomOfType(Atom.TYPE_udta);
        if (udta != null) {
            gaplessInfo = AtomParsers.parseUdta(udta, this.isQuickTime);
        }
        int i = STATE_AFTER_SEEK;
        while (true) {
            if (i < moov.containerChildren.size()) {
                ContainerAtom atom = (ContainerAtom) moov.containerChildren.get(i);
                if (atom.type == Atom.TYPE_trak) {
                    Track track = AtomParsers.parseTrak(atom, moov.getLeafAtomOfType(Atom.TYPE_mvhd), -1, this.isQuickTime);
                    if (track != null) {
                        TrackSampleTable trackSampleTable = AtomParsers.parseStbl(track, atom.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl));
                        if (trackSampleTable.sampleCount != 0) {
                            Mp4Track mp4Track = new Mp4Track(track, trackSampleTable, this.extractorOutput.track(i));
                            int maxInputSize = trackSampleTable.maximumSize + 30;
                            MediaFormat mediaFormat = track.mediaFormat.copyWithMaxInputSize(maxInputSize);
                            if (gaplessInfo != null) {
                                mediaFormat = mediaFormat.copyWithGaplessInfo(gaplessInfo.encoderDelay, gaplessInfo.encoderPadding);
                            }
                            mp4Track.trackOutput.format(mediaFormat);
                            tracks.add(mp4Track);
                            long firstSampleOffset = trackSampleTable.offsets[STATE_AFTER_SEEK];
                            if (firstSampleOffset < earliestSampleOffset) {
                                earliestSampleOffset = firstSampleOffset;
                            }
                        }
                    }
                }
                i += STATE_READING_ATOM_HEADER;
            } else {
                this.tracks = (Mp4Track[]) tracks.toArray(new Mp4Track[STATE_AFTER_SEEK]);
                this.extractorOutput.endTracks();
                this.extractorOutput.seekMap(this);
                return;
            }
        }
    }

    private int readSample(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        int trackIndex = getTrackIndexOfEarliestCurrentSample();
        if (trackIndex == -1) {
            return -1;
        }
        Mp4Track track = this.tracks[trackIndex];
        TrackOutput trackOutput = track.trackOutput;
        int sampleIndex = track.sampleIndex;
        long position = track.sampleTable.offsets[sampleIndex];
        long skipAmount = (position - input.getPosition()) + ((long) this.sampleBytesWritten);
        if (skipAmount < 0 || skipAmount >= RELOAD_MINIMUM_SEEK_DISTANCE) {
            positionHolder.position = position;
            return STATE_READING_ATOM_HEADER;
        }
        input.skipFully((int) skipAmount);
        this.sampleSize = track.sampleTable.sizes[sampleIndex];
        int writtenBytes;
        if (track.track.nalUnitLengthFieldLength != -1) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[STATE_AFTER_SEEK] = (byte) 0;
            nalLengthData[STATE_READING_ATOM_HEADER] = (byte) 0;
            nalLengthData[STATE_READING_ATOM_PAYLOAD] = (byte) 0;
            int nalUnitLengthFieldLength = track.track.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track.track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < this.sampleSize) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    input.readFully(this.nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                    this.nalLength.setPosition(STATE_AFTER_SEEK);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(STATE_AFTER_SEEK);
                    trackOutput.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                    this.sampleSize += nalUnitLengthFieldLengthDiff;
                } else {
                    writtenBytes = trackOutput.sampleData(input, this.sampleCurrentNalBytesRemaining, false);
                    this.sampleBytesWritten += writtenBytes;
                    this.sampleCurrentNalBytesRemaining -= writtenBytes;
                }
            }
        } else {
            while (this.sampleBytesWritten < this.sampleSize) {
                writtenBytes = trackOutput.sampleData(input, this.sampleSize - this.sampleBytesWritten, false);
                this.sampleBytesWritten += writtenBytes;
                this.sampleCurrentNalBytesRemaining -= writtenBytes;
            }
        }
        trackOutput.sampleMetadata(track.sampleTable.timestampsUs[sampleIndex], track.sampleTable.flags[sampleIndex], this.sampleSize, STATE_AFTER_SEEK, null);
        track.sampleIndex += STATE_READING_ATOM_HEADER;
        this.sampleBytesWritten = STATE_AFTER_SEEK;
        this.sampleCurrentNalBytesRemaining = STATE_AFTER_SEEK;
        return STATE_AFTER_SEEK;
    }

    private int getTrackIndexOfEarliestCurrentSample() {
        int earliestSampleTrackIndex = -1;
        long earliestSampleOffset = PtsTimestampAdjuster.DO_NOT_OFFSET;
        for (int trackIndex = STATE_AFTER_SEEK; trackIndex < this.tracks.length; trackIndex += STATE_READING_ATOM_HEADER) {
            Mp4Track track = this.tracks[trackIndex];
            int sampleIndex = track.sampleIndex;
            if (sampleIndex != track.sampleTable.sampleCount) {
                long trackSampleOffset = track.sampleTable.offsets[sampleIndex];
                if (trackSampleOffset < earliestSampleOffset) {
                    earliestSampleOffset = trackSampleOffset;
                    earliestSampleTrackIndex = trackIndex;
                }
            }
        }
        return earliestSampleTrackIndex;
    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_hdlr || atom == Atom.TYPE_stsd || atom == Atom.TYPE_stts || atom == Atom.TYPE_stss || atom == Atom.TYPE_ctts || atom == Atom.TYPE_elst || atom == Atom.TYPE_stsc || atom == Atom.TYPE_stsz || atom == Atom.TYPE_stco || atom == Atom.TYPE_co64 || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_ftyp || atom == Atom.TYPE_udta;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_edts;
    }
}
