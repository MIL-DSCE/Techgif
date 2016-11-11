package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.ConnectionsManager;

public final class FragmentedMp4Extractor implements Extractor {
    private static final int FLAG_SIDELOADED = 4;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final Stack<ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private final ParsableByteArray encryptionSignalByte;
    private long endOfMdatPosition;
    private final byte[] extendedTypeScratch;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private final Track sideloadedTrack;
    private final SparseArray<TrackBundle> trackBundles;

    private static final class TrackBundle {
        public int currentSampleIndex;
        public DefaultSampleValues defaultSampleValues;
        public final TrackFragment fragment;
        public final TrackOutput output;
        public Track track;

        public TrackBundle(TrackOutput output) {
            this.fragment = new TrackFragment();
            this.output = output;
        }

        public void init(Track track, DefaultSampleValues defaultSampleValues) {
            this.track = (Track) Assertions.checkNotNull(track);
            this.defaultSampleValues = (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValues);
            this.output.format(track.mediaFormat);
            reset();
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = FragmentedMp4Extractor.STATE_READING_ATOM_HEADER;
        }
    }

    static {
        PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[]{(byte) -94, (byte) 57, (byte) 79, (byte) 82, (byte) 90, (byte) -101, (byte) 79, ClosedCaptionCtrl.MISC_CHAN_1, (byte) -94, (byte) 68, (byte) 108, (byte) 66, (byte) 124, (byte) 100, (byte) -115, (byte) -12};
    }

    public FragmentedMp4Extractor() {
        this(STATE_READING_ATOM_HEADER);
    }

    public FragmentedMp4Extractor(int flags) {
        this(flags, null);
    }

    public FragmentedMp4Extractor(int flags, Track sideloadedTrack) {
        this.sideloadedTrack = sideloadedTrack;
        this.flags = (sideloadedTrack != null ? STATE_READING_SAMPLE_CONTINUE : STATE_READING_ATOM_HEADER) | flags;
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray((int) STATE_READING_SAMPLE_CONTINUE);
        this.encryptionSignalByte = new ParsableByteArray((int) STATE_READING_ATOM_PAYLOAD);
        this.extendedTypeScratch = new byte[16];
        this.containerAtoms = new Stack();
        this.trackBundles = new SparseArray();
        enterReadingAtomHeaderState();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffFragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        if (this.sideloadedTrack != null) {
            TrackBundle bundle = new TrackBundle(output.track(STATE_READING_ATOM_HEADER));
            bundle.init(this.sideloadedTrack, new DefaultSampleValues(STATE_READING_ATOM_HEADER, STATE_READING_ATOM_HEADER, STATE_READING_ATOM_HEADER, STATE_READING_ATOM_HEADER));
            this.trackBundles.put(STATE_READING_ATOM_HEADER, bundle);
            this.extractorOutput.endTracks();
        }
    }

    public void seek() {
        int trackCount = this.trackBundles.size();
        for (int i = STATE_READING_ATOM_HEADER; i < trackCount; i += STATE_READING_ATOM_PAYLOAD) {
            ((TrackBundle) this.trackBundles.valueAt(i)).reset();
        }
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case STATE_READING_ATOM_HEADER /*0*/:
                    if (readAtomHeader(input)) {
                        break;
                    }
                    return -1;
                case STATE_READING_ATOM_PAYLOAD /*1*/:
                    readAtomPayload(input);
                    break;
                case STATE_READING_ENCRYPTION_DATA /*2*/:
                    readEncryptionData(input);
                    break;
                default:
                    if (!readSample(input)) {
                        break;
                    }
                    return STATE_READING_ATOM_HEADER;
            }
        }
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = STATE_READING_ATOM_HEADER;
        this.atomHeaderBytesRead = STATE_READING_ATOM_HEADER;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.data, STATE_READING_ATOM_HEADER, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(STATE_READING_ATOM_HEADER);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        if (this.atomSize == 1) {
            input.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        }
        long atomPosition = input.getPosition() - ((long) this.atomHeaderBytesRead);
        if (this.atomType == Atom.TYPE_moof) {
            int trackCount = this.trackBundles.size();
            for (int i = STATE_READING_ATOM_HEADER; i < trackCount; i += STATE_READING_ATOM_PAYLOAD) {
                TrackFragment fragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
                fragment.auxiliaryDataPosition = atomPosition;
                fragment.dataPosition = atomPosition;
            }
        }
        if (this.atomType == Atom.TYPE_mdat) {
            this.currentTrackBundle = null;
            this.endOfMdatPosition = this.atomSize + atomPosition;
            if (!this.haveOutputSeekMap) {
                this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                this.haveOutputSeekMap = true;
            }
            this.parserState = STATE_READING_ENCRYPTION_DATA;
            return true;
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long endPosition = (input.getPosition() + this.atomSize) - 8;
            this.containerAtoms.add(new ContainerAtom(this.atomType, endPosition));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(endPosition);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            if (this.atomHeaderBytesRead != 8) {
                throw new ParserException("Leaf atom defines extended atom size (unsupported).");
            } else if (this.atomSize > 2147483647L) {
                throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
            } else {
                this.atomData = new ParsableByteArray((int) this.atomSize);
                System.arraycopy(this.atomHeader.data, STATE_READING_ATOM_HEADER, this.atomData.data, STATE_READING_ATOM_HEADER, 8);
                this.parserState = STATE_READING_ATOM_PAYLOAD;
            }
        } else if (this.atomSize > 2147483647L) {
            throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
        } else {
            this.atomData = null;
            this.parserState = STATE_READING_ATOM_PAYLOAD;
        }
        return true;
    }

    private void readAtomPayload(ExtractorInput input) throws IOException, InterruptedException {
        int atomPayloadSize = ((int) this.atomSize) - this.atomHeaderBytesRead;
        if (this.atomData != null) {
            input.readFully(this.atomData.data, 8, atomPayloadSize);
            onLeafAtomRead(new LeafAtom(this.atomType, this.atomData), input.getPosition());
        } else {
            input.skipFully(atomPayloadSize);
        }
        processAtomEnded(input.getPosition());
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == atomEndPosition) {
            onContainerAtomRead((ContainerAtom) this.containerAtoms.pop());
        }
        enterReadingAtomHeaderState();
    }

    private void onLeafAtomRead(LeafAtom leaf, long inputPosition) throws ParserException {
        if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(leaf);
        } else if (leaf.type == Atom.TYPE_sidx) {
            this.extractorOutput.seekMap(parseSidx(leaf.data, inputPosition));
            this.haveOutputSeekMap = true;
        }
    }

    private void onContainerAtomRead(ContainerAtom container) throws ParserException {
        if (container.type == Atom.TYPE_moov) {
            onMoovContainerAtomRead(container);
        } else if (container.type == Atom.TYPE_moof) {
            onMoofContainerAtomRead(container);
        } else if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(container);
        }
    }

    private void onMoovContainerAtomRead(ContainerAtom moov) {
        int i;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        List<LeafAtom> moovLeafChildren = moov.leafChildren;
        int moovLeafChildrenSize = moovLeafChildren.size();
        Mapped drmInitData = null;
        for (i = STATE_READING_ATOM_HEADER; i < moovLeafChildrenSize; i += STATE_READING_ATOM_PAYLOAD) {
            LeafAtom child = (LeafAtom) moovLeafChildren.get(i);
            if (child.type == Atom.TYPE_pssh) {
                if (drmInitData == null) {
                    drmInitData = new Mapped();
                }
                byte[] psshData = child.data.data;
                if (PsshAtomUtil.parseUuid(psshData) == null) {
                    Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    drmInitData.put(PsshAtomUtil.parseUuid(psshData), new SchemeInitData(MimeTypes.VIDEO_MP4, psshData));
                }
            }
        }
        if (drmInitData != null) {
            this.extractorOutput.drmInitData(drmInitData);
        }
        ContainerAtom mvex = moov.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray<DefaultSampleValues> defaultSampleValuesArray = new SparseArray();
        long duration = -1;
        int mvexChildrenSize = mvex.leafChildren.size();
        for (i = STATE_READING_ATOM_HEADER; i < mvexChildrenSize; i += STATE_READING_ATOM_PAYLOAD) {
            LeafAtom atom = (LeafAtom) mvex.leafChildren.get(i);
            if (atom.type == Atom.TYPE_trex) {
                Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
                defaultSampleValuesArray.put(((Integer) trexData.first).intValue(), trexData.second);
            } else {
                if (atom.type == Atom.TYPE_mehd) {
                    duration = parseMehd(atom.data);
                }
            }
        }
        SparseArray<Track> tracks = new SparseArray();
        int moovContainerChildrenSize = moov.containerChildren.size();
        for (i = STATE_READING_ATOM_HEADER; i < moovContainerChildrenSize; i += STATE_READING_ATOM_PAYLOAD) {
            Track track;
            ContainerAtom atom2 = (ContainerAtom) moov.containerChildren.get(i);
            if (atom2.type == Atom.TYPE_trak) {
                track = AtomParsers.parseTrak(atom2, moov.getLeafAtomOfType(Atom.TYPE_mvhd), duration, false);
                if (track != null) {
                    tracks.put(track.id, track);
                }
            }
        }
        int trackCount = tracks.size();
        if (this.trackBundles.size() == 0) {
            for (i = STATE_READING_ATOM_HEADER; i < trackCount; i += STATE_READING_ATOM_PAYLOAD) {
                this.trackBundles.put(((Track) tracks.valueAt(i)).id, new TrackBundle(this.extractorOutput.track(i)));
            }
            this.extractorOutput.endTracks();
        } else {
            Assertions.checkState(this.trackBundles.size() == trackCount);
        }
        for (i = STATE_READING_ATOM_HEADER; i < trackCount; i += STATE_READING_ATOM_PAYLOAD) {
            track = (Track) tracks.valueAt(i);
            ((TrackBundle) this.trackBundles.get(track.id)).init(track, (DefaultSampleValues) defaultSampleValuesArray.get(track.id));
        }
    }

    private void onMoofContainerAtomRead(ContainerAtom moof) throws ParserException {
        parseMoof(moof, this.trackBundles, this.flags, this.extendedTypeScratch);
    }

    private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray trex) {
        trex.setPosition(12);
        return Pair.create(Integer.valueOf(trex.readInt()), new DefaultSampleValues(trex.readUnsignedIntToInt() - 1, trex.readUnsignedIntToInt(), trex.readUnsignedIntToInt(), trex.readInt()));
    }

    private static long parseMehd(ParsableByteArray mehd) {
        mehd.setPosition(8);
        return Atom.parseFullAtomVersion(mehd.readInt()) == 0 ? mehd.readUnsignedInt() : mehd.readUnsignedLongToLong();
    }

    private static void parseMoof(ContainerAtom moof, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        int moofContainerChildrenSize = moof.containerChildren.size();
        for (int i = STATE_READING_ATOM_HEADER; i < moofContainerChildrenSize; i += STATE_READING_ATOM_PAYLOAD) {
            ContainerAtom child = (ContainerAtom) moof.containerChildren.get(i);
            if (child.type == Atom.TYPE_traf) {
                parseTraf(child, trackBundleArray, flags, extendedTypeScratch);
            }
        }
    }

    private static void parseTraf(ContainerAtom traf, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        if (traf.getChildAtomOfTypeCount(Atom.TYPE_trun) != STATE_READING_ATOM_PAYLOAD) {
            throw new ParserException("Trun count in traf != 1 (unsupported).");
        }
        TrackBundle trackBundle = parseTfhd(traf.getLeafAtomOfType(Atom.TYPE_tfhd).data, trackBundleArray, flags);
        if (trackBundle != null) {
            TrackFragment fragment = trackBundle.fragment;
            long decodeTime = fragment.nextFragmentDecodeTime;
            trackBundle.reset();
            if (traf.getLeafAtomOfType(Atom.TYPE_tfdt) != null && (flags & STATE_READING_ENCRYPTION_DATA) == 0) {
                decodeTime = parseTfdt(traf.getLeafAtomOfType(Atom.TYPE_tfdt).data);
            }
            parseTrun(trackBundle, decodeTime, flags, traf.getLeafAtomOfType(Atom.TYPE_trun).data);
            LeafAtom saiz = traf.getLeafAtomOfType(Atom.TYPE_saiz);
            if (saiz != null) {
                parseSaiz(trackBundle.track.sampleDescriptionEncryptionBoxes[fragment.header.sampleDescriptionIndex], saiz.data, fragment);
            }
            LeafAtom saio = traf.getLeafAtomOfType(Atom.TYPE_saio);
            if (saio != null) {
                parseSaio(saio.data, fragment);
            }
            LeafAtom senc = traf.getLeafAtomOfType(Atom.TYPE_senc);
            if (senc != null) {
                parseSenc(senc.data, fragment);
            }
            int childrenSize = traf.leafChildren.size();
            for (int i = STATE_READING_ATOM_HEADER; i < childrenSize; i += STATE_READING_ATOM_PAYLOAD) {
                LeafAtom atom = (LeafAtom) traf.leafChildren.get(i);
                if (atom.type == Atom.TYPE_uuid) {
                    parseUuid(atom.data, fragment, extendedTypeScratch);
                }
            }
        }
    }

    private static void parseSaiz(TrackEncryptionBox encryptionBox, ParsableByteArray saiz, TrackFragment out) throws ParserException {
        int vectorSize = encryptionBox.initializationVectorSize;
        saiz.setPosition(8);
        if ((Atom.parseFullAtomFlags(saiz.readInt()) & STATE_READING_ATOM_PAYLOAD) == STATE_READING_ATOM_PAYLOAD) {
            saiz.skipBytes(8);
        }
        int defaultSampleInfoSize = saiz.readUnsignedByte();
        int sampleCount = saiz.readUnsignedIntToInt();
        if (sampleCount != out.length) {
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.length);
        }
        int totalSize = STATE_READING_ATOM_HEADER;
        if (defaultSampleInfoSize == 0) {
            boolean[] sampleHasSubsampleEncryptionTable = out.sampleHasSubsampleEncryptionTable;
            for (int i = STATE_READING_ATOM_HEADER; i < sampleCount; i += STATE_READING_ATOM_PAYLOAD) {
                int sampleInfoSize = saiz.readUnsignedByte();
                totalSize += sampleInfoSize;
                sampleHasSubsampleEncryptionTable[i] = sampleInfoSize > vectorSize;
            }
        } else {
            totalSize = STATE_READING_ATOM_HEADER + (defaultSampleInfoSize * sampleCount);
            Arrays.fill(out.sampleHasSubsampleEncryptionTable, STATE_READING_ATOM_HEADER, sampleCount, defaultSampleInfoSize > vectorSize);
        }
        out.initEncryptionData(totalSize);
    }

    private static void parseSaio(ParsableByteArray saio, TrackFragment out) throws ParserException {
        saio.setPosition(8);
        int fullAtom = saio.readInt();
        if ((Atom.parseFullAtomFlags(fullAtom) & STATE_READING_ATOM_PAYLOAD) == STATE_READING_ATOM_PAYLOAD) {
            saio.skipBytes(8);
        }
        int entryCount = saio.readUnsignedIntToInt();
        if (entryCount != STATE_READING_ATOM_PAYLOAD) {
            throw new ParserException("Unexpected saio entry count: " + entryCount);
        }
        int version = Atom.parseFullAtomVersion(fullAtom);
        out.auxiliaryDataPosition = (version == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong()) + out.auxiliaryDataPosition;
    }

    private static TrackBundle parseTfhd(ParsableByteArray tfhd, SparseArray<TrackBundle> trackBundles, int flags) {
        tfhd.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(tfhd.readInt());
        int trackId = tfhd.readInt();
        if ((flags & STATE_READING_SAMPLE_CONTINUE) != 0) {
            trackId = STATE_READING_ATOM_HEADER;
        }
        TrackBundle trackBundle = (TrackBundle) trackBundles.get(trackId);
        if (trackBundle == null) {
            return null;
        }
        if ((atomFlags & STATE_READING_ATOM_PAYLOAD) != 0) {
            long baseDataPosition = tfhd.readUnsignedLongToLong();
            trackBundle.fragment.dataPosition = baseDataPosition;
            trackBundle.fragment.auxiliaryDataPosition = baseDataPosition;
        }
        DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
        trackBundle.fragment.header = new DefaultSampleValues((atomFlags & STATE_READING_ENCRYPTION_DATA) != 0 ? tfhd.readUnsignedIntToInt() - 1 : defaultSampleValues.sampleDescriptionIndex, (atomFlags & 8) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.duration, (atomFlags & 16) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.size, (atomFlags & 32) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.flags);
        return trackBundle;
    }

    private static long parseTfdt(ParsableByteArray tfdt) {
        tfdt.setPosition(8);
        return Atom.parseFullAtomVersion(tfdt.readInt()) == STATE_READING_ATOM_PAYLOAD ? tfdt.readUnsignedLongToLong() : tfdt.readUnsignedInt();
    }

    private static void parseTrun(TrackBundle trackBundle, long decodeTime, int flags, ParsableByteArray trun) {
        trun.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(trun.readInt());
        Track track = trackBundle.track;
        TrackFragment fragment = trackBundle.fragment;
        DefaultSampleValues defaultSampleValues = fragment.header;
        int sampleCount = trun.readUnsignedIntToInt();
        if ((atomFlags & STATE_READING_ATOM_PAYLOAD) != 0) {
            fragment.dataPosition += (long) trun.readInt();
        }
        boolean firstSampleFlagsPresent = (atomFlags & STATE_READING_SAMPLE_CONTINUE) != 0;
        int firstSampleFlags = defaultSampleValues.flags;
        if (firstSampleFlagsPresent) {
            firstSampleFlags = trun.readUnsignedIntToInt();
        }
        boolean sampleDurationsPresent = (atomFlags & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) != 0;
        boolean sampleSizesPresent = (atomFlags & MessagesController.UPDATE_MASK_SELECT_DIALOG) != 0;
        boolean sampleFlagsPresent = (atomFlags & MessagesController.UPDATE_MASK_PHONE) != 0;
        boolean sampleCompositionTimeOffsetsPresent = (atomFlags & MessagesController.UPDATE_MASK_NEW_MESSAGE) != 0;
        long edtsOffset = 0;
        if (track.editListDurations != null && track.editListDurations.length == STATE_READING_ATOM_PAYLOAD && track.editListDurations[STATE_READING_ATOM_HEADER] == 0) {
            edtsOffset = Util.scaleLargeTimestamp(track.editListMediaTimes[STATE_READING_ATOM_HEADER], 1000, track.timescale);
        }
        fragment.initTables(sampleCount);
        int[] sampleSizeTable = fragment.sampleSizeTable;
        int[] sampleCompositionTimeOffsetTable = fragment.sampleCompositionTimeOffsetTable;
        long[] sampleDecodingTimeTable = fragment.sampleDecodingTimeTable;
        boolean[] sampleIsSyncFrameTable = fragment.sampleIsSyncFrameTable;
        long timescale = track.timescale;
        long cumulativeTime = decodeTime;
        boolean workaroundEveryVideoFrameIsSyncFrame = track.type == Track.TYPE_vide && (flags & STATE_READING_ATOM_PAYLOAD) != 0;
        int i = STATE_READING_ATOM_HEADER;
        while (i < sampleCount) {
            int sampleDuration = sampleDurationsPresent ? trun.readUnsignedIntToInt() : defaultSampleValues.duration;
            int sampleSize = sampleSizesPresent ? trun.readUnsignedIntToInt() : defaultSampleValues.size;
            int sampleFlags = (i == 0 && firstSampleFlagsPresent) ? firstSampleFlags : sampleFlagsPresent ? trun.readInt() : defaultSampleValues.flags;
            if (sampleCompositionTimeOffsetsPresent) {
                sampleCompositionTimeOffsetTable[i] = (int) (((long) (trun.readInt() * 1000)) / timescale);
            } else {
                sampleCompositionTimeOffsetTable[i] = STATE_READING_ATOM_HEADER;
            }
            sampleDecodingTimeTable[i] = Util.scaleLargeTimestamp(cumulativeTime, 1000, timescale) - edtsOffset;
            sampleSizeTable[i] = sampleSize;
            boolean z = ((sampleFlags >> 16) & STATE_READING_ATOM_PAYLOAD) == 0 && (!workaroundEveryVideoFrameIsSyncFrame || i == 0);
            sampleIsSyncFrameTable[i] = z;
            cumulativeTime += (long) sampleDuration;
            i += STATE_READING_ATOM_PAYLOAD;
        }
        fragment.nextFragmentDecodeTime = cumulativeTime;
    }

    private static void parseUuid(ParsableByteArray uuid, TrackFragment out, byte[] extendedTypeScratch) throws ParserException {
        uuid.setPosition(8);
        uuid.readBytes(extendedTypeScratch, STATE_READING_ATOM_HEADER, 16);
        if (Arrays.equals(extendedTypeScratch, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
            parseSenc(uuid, 16, out);
        }
    }

    private static void parseSenc(ParsableByteArray senc, TrackFragment out) throws ParserException {
        parseSenc(senc, STATE_READING_ATOM_HEADER, out);
    }

    private static void parseSenc(ParsableByteArray senc, int offset, TrackFragment out) throws ParserException {
        senc.setPosition(offset + 8);
        int flags = Atom.parseFullAtomFlags(senc.readInt());
        if ((flags & STATE_READING_ATOM_PAYLOAD) != 0) {
            throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
        }
        boolean subsampleEncryption;
        if ((flags & STATE_READING_ENCRYPTION_DATA) != 0) {
            subsampleEncryption = true;
        } else {
            subsampleEncryption = false;
        }
        int sampleCount = senc.readUnsignedIntToInt();
        if (sampleCount != out.length) {
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.length);
        }
        Arrays.fill(out.sampleHasSubsampleEncryptionTable, STATE_READING_ATOM_HEADER, sampleCount, subsampleEncryption);
        out.initEncryptionData(senc.bytesLeft());
        out.fillEncryptionData(senc);
    }

    private static ChunkIndex parseSidx(ParsableByteArray atom, long inputPosition) throws ParserException {
        long earliestPresentationTime;
        atom.setPosition(8);
        int version = Atom.parseFullAtomVersion(atom.readInt());
        atom.skipBytes(STATE_READING_SAMPLE_CONTINUE);
        long timescale = atom.readUnsignedInt();
        long offset = inputPosition;
        if (version == 0) {
            earliestPresentationTime = atom.readUnsignedInt();
            offset += atom.readUnsignedInt();
        } else {
            earliestPresentationTime = atom.readUnsignedLongToLong();
            offset += atom.readUnsignedLongToLong();
        }
        atom.skipBytes(STATE_READING_ENCRYPTION_DATA);
        int referenceCount = atom.readUnsignedShort();
        int[] sizes = new int[referenceCount];
        long[] offsets = new long[referenceCount];
        long[] durationsUs = new long[referenceCount];
        long[] timesUs = new long[referenceCount];
        long time = earliestPresentationTime;
        long timeUs = Util.scaleLargeTimestamp(time, C0747C.MICROS_PER_SECOND, timescale);
        for (int i = STATE_READING_ATOM_HEADER; i < referenceCount; i += STATE_READING_ATOM_PAYLOAD) {
            int firstInt = atom.readInt();
            if ((LinearLayoutManager.INVALID_OFFSET & firstInt) != 0) {
                throw new ParserException("Unhandled indirect reference");
            }
            long referenceDuration = atom.readUnsignedInt();
            sizes[i] = ConnectionsManager.DEFAULT_DATACENTER_ID & firstInt;
            offsets[i] = offset;
            timesUs[i] = timeUs;
            time += referenceDuration;
            timeUs = Util.scaleLargeTimestamp(time, C0747C.MICROS_PER_SECOND, timescale);
            durationsUs[i] = timeUs - timesUs[i];
            atom.skipBytes(STATE_READING_SAMPLE_CONTINUE);
            offset += (long) sizes[i];
        }
        return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
    }

    private void readEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        TrackBundle nextTrackBundle = null;
        long nextDataOffset = PtsTimestampAdjuster.DO_NOT_OFFSET;
        int trackBundlesSize = this.trackBundles.size();
        for (int i = STATE_READING_ATOM_HEADER; i < trackBundlesSize; i += STATE_READING_ATOM_PAYLOAD) {
            TrackFragment trackFragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
            if (trackFragment.sampleEncryptionDataNeedsFill && trackFragment.auxiliaryDataPosition < nextDataOffset) {
                nextDataOffset = trackFragment.auxiliaryDataPosition;
                nextTrackBundle = (TrackBundle) this.trackBundles.valueAt(i);
            }
        }
        if (nextTrackBundle == null) {
            this.parserState = STATE_READING_SAMPLE_START;
            return;
        }
        int bytesToSkip = (int) (nextDataOffset - input.getPosition());
        if (bytesToSkip < 0) {
            throw new ParserException("Offset to encryption data was negative.");
        }
        input.skipFully(bytesToSkip);
        nextTrackBundle.fragment.fillEncryptionData(input);
    }

    private boolean readSample(ExtractorInput input) throws IOException, InterruptedException {
        if (this.parserState == STATE_READING_SAMPLE_START) {
            if (this.currentTrackBundle == null) {
                this.currentTrackBundle = getNextFragmentRun(this.trackBundles);
                int bytesToSkip;
                if (this.currentTrackBundle == null) {
                    bytesToSkip = (int) (this.endOfMdatPosition - input.getPosition());
                    if (bytesToSkip < 0) {
                        throw new ParserException("Offset to end of mdat was negative.");
                    }
                    input.skipFully(bytesToSkip);
                    enterReadingAtomHeaderState();
                    return false;
                }
                bytesToSkip = (int) (this.currentTrackBundle.fragment.dataPosition - input.getPosition());
                if (bytesToSkip < 0) {
                    throw new ParserException("Offset to sample data was negative.");
                }
                input.skipFully(bytesToSkip);
            }
            this.sampleSize = this.currentTrackBundle.fragment.sampleSizeTable[this.currentTrackBundle.currentSampleIndex];
            if (this.currentTrackBundle.fragment.definesEncryptionData) {
                this.sampleBytesWritten = appendSampleEncryptionData(this.currentTrackBundle);
                this.sampleSize += this.sampleBytesWritten;
            } else {
                this.sampleBytesWritten = STATE_READING_ATOM_HEADER;
            }
            this.parserState = STATE_READING_SAMPLE_CONTINUE;
            this.sampleCurrentNalBytesRemaining = STATE_READING_ATOM_HEADER;
        }
        TrackFragment fragment = this.currentTrackBundle.fragment;
        Track track = this.currentTrackBundle.track;
        TrackOutput output = this.currentTrackBundle.output;
        int sampleIndex = this.currentTrackBundle.currentSampleIndex;
        if (track.nalUnitLengthFieldLength != -1) {
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[STATE_READING_ATOM_HEADER] = (byte) 0;
            nalLengthData[STATE_READING_ATOM_PAYLOAD] = (byte) 0;
            nalLengthData[STATE_READING_ENCRYPTION_DATA] = (byte) 0;
            int nalUnitLengthFieldLength = track.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < this.sampleSize) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    input.readFully(this.nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                    this.nalLength.setPosition(STATE_READING_ATOM_HEADER);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(STATE_READING_ATOM_HEADER);
                    output.sampleData(this.nalStartCode, STATE_READING_SAMPLE_CONTINUE);
                    this.sampleBytesWritten += STATE_READING_SAMPLE_CONTINUE;
                    this.sampleSize += nalUnitLengthFieldLengthDiff;
                } else {
                    int writtenBytes = output.sampleData(input, this.sampleCurrentNalBytesRemaining, false);
                    this.sampleBytesWritten += writtenBytes;
                    this.sampleCurrentNalBytesRemaining -= writtenBytes;
                }
            }
        } else {
            while (this.sampleBytesWritten < this.sampleSize) {
                this.sampleBytesWritten += output.sampleData(input, this.sampleSize - this.sampleBytesWritten, false);
            }
        }
        output.sampleMetadata(fragment.getSamplePresentationTime(sampleIndex) * 1000, (fragment.definesEncryptionData ? STATE_READING_ENCRYPTION_DATA : STATE_READING_ATOM_HEADER) | (fragment.sampleIsSyncFrameTable[sampleIndex] ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER), this.sampleSize, STATE_READING_ATOM_HEADER, fragment.definesEncryptionData ? track.sampleDescriptionEncryptionBoxes[fragment.header.sampleDescriptionIndex].keyId : null);
        TrackBundle trackBundle = this.currentTrackBundle;
        trackBundle.currentSampleIndex += STATE_READING_ATOM_PAYLOAD;
        if (this.currentTrackBundle.currentSampleIndex == fragment.length) {
            this.currentTrackBundle = null;
        }
        this.parserState = STATE_READING_SAMPLE_START;
        return true;
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles) {
        TrackBundle nextTrackBundle = null;
        long nextTrackRunOffset = PtsTimestampAdjuster.DO_NOT_OFFSET;
        int trackBundlesSize = trackBundles.size();
        for (int i = STATE_READING_ATOM_HEADER; i < trackBundlesSize; i += STATE_READING_ATOM_PAYLOAD) {
            TrackBundle trackBundle = (TrackBundle) trackBundles.valueAt(i);
            if (trackBundle.currentSampleIndex != trackBundle.fragment.length) {
                long trunOffset = trackBundle.fragment.dataPosition;
                if (trunOffset < nextTrackRunOffset) {
                    nextTrackBundle = trackBundle;
                    nextTrackRunOffset = trunOffset;
                }
            }
        }
        return nextTrackBundle;
    }

    private int appendSampleEncryptionData(TrackBundle trackBundle) {
        int i;
        TrackFragment trackFragment = trackBundle.fragment;
        ParsableByteArray sampleEncryptionData = trackFragment.sampleEncryptionData;
        int vectorSize = trackBundle.track.sampleDescriptionEncryptionBoxes[trackFragment.header.sampleDescriptionIndex].initializationVectorSize;
        boolean subsampleEncryption = trackFragment.sampleHasSubsampleEncryptionTable[trackBundle.currentSampleIndex];
        byte[] bArr = this.encryptionSignalByte.data;
        if (subsampleEncryption) {
            i = MessagesController.UPDATE_MASK_USER_PHONE;
        } else {
            i = STATE_READING_ATOM_HEADER;
        }
        bArr[STATE_READING_ATOM_HEADER] = (byte) (i | vectorSize);
        this.encryptionSignalByte.setPosition(STATE_READING_ATOM_HEADER);
        TrackOutput output = trackBundle.output;
        output.sampleData(this.encryptionSignalByte, STATE_READING_ATOM_PAYLOAD);
        output.sampleData(sampleEncryptionData, vectorSize);
        if (!subsampleEncryption) {
            return vectorSize + STATE_READING_ATOM_PAYLOAD;
        }
        int subsampleCount = sampleEncryptionData.readUnsignedShort();
        sampleEncryptionData.skipBytes(-2);
        int subsampleDataLength = (subsampleCount * 6) + STATE_READING_ENCRYPTION_DATA;
        output.sampleData(sampleEncryptionData, subsampleDataLength);
        return (vectorSize + STATE_READING_ATOM_PAYLOAD) + subsampleDataLength;
    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == Atom.TYPE_hdlr || atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_sidx || atom == Atom.TYPE_stsd || atom == Atom.TYPE_tfdt || atom == Atom.TYPE_tfhd || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_trex || atom == Atom.TYPE_trun || atom == Atom.TYPE_pssh || atom == Atom.TYPE_saiz || atom == Atom.TYPE_saio || atom == Atom.TYPE_senc || atom == Atom.TYPE_uuid || atom == Atom.TYPE_elst || atom == Atom.TYPE_mehd;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_moof || atom == Atom.TYPE_traf || atom == Atom.TYPE_mvex || atom == Atom.TYPE_edts;
    }
}
