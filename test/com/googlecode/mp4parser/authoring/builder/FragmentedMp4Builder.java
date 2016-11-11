package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.HintMediaHeaderBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.NullMediaHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.boxes.apple.QuicktimeTextSampleEntry;
import com.googlecode.mp4parser.boxes.dece.SampleEncryptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class FragmentedMp4Builder implements Mp4Builder {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final Logger LOG;
    protected FragmentIntersectionFinder intersectionFinder;

    /* renamed from: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.1 */
    class C03601 implements Comparator<Track> {
        private final /* synthetic */ int val$cycle;
        private final /* synthetic */ Map val$intersectionMap;

        C03601(Map map, int i) {
            this.val$intersectionMap = map;
            this.val$cycle = i;
        }

        public int compare(Track o1, Track o2) {
            int i;
            long startSample1 = ((long[]) this.val$intersectionMap.get(o1))[this.val$cycle];
            long startSample2 = ((long[]) this.val$intersectionMap.get(o2))[this.val$cycle];
            long[] decTimes1 = o1.getSampleDurations();
            long[] decTimes2 = o2.getSampleDurations();
            long startTime1 = 0;
            long startTime2 = 0;
            for (i = 1; ((long) i) < startSample1; i++) {
                startTime1 += decTimes1[i - 1];
            }
            for (i = 1; ((long) i) < startSample2; i++) {
                startTime2 += decTimes2[i - 1];
            }
            return (int) (((((double) startTime1) / ((double) o1.getTrackMetaData().getTimescale())) - (((double) startTime2) / ((double) o2.getTrackMetaData().getTimescale()))) * 100.0d);
        }
    }

    /* renamed from: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.1Mdat */
    class AnonymousClass1Mdat implements Box {
        Container parent;
        long size_;
        private final /* synthetic */ long val$endSample;
        private final /* synthetic */ int val$i;
        private final /* synthetic */ long val$startSample;
        private final /* synthetic */ Track val$track;

        AnonymousClass1Mdat(long j, long j2, Track track, int i) {
            this.val$startSample = j;
            this.val$endSample = j2;
            this.val$track = track;
            this.val$i = i;
            this.size_ = -1;
        }

        public Container getParent() {
            return this.parent;
        }

        public void setParent(Container parent) {
            this.parent = parent;
        }

        public long getOffset() {
            throw new RuntimeException("Doesn't have any meaning for programmatically created boxes");
        }

        public long getSize() {
            if (this.size_ != -1) {
                return this.size_;
            }
            long size = 8;
            for (Sample sample : FragmentedMp4Builder.this.getSamples(this.val$startSample, this.val$endSample, this.val$track, this.val$i)) {
                size += sample.getSize();
            }
            this.size_ = size;
            return size;
        }

        public String getType() {
            return MediaDataBox.TYPE;
        }

        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
            ByteBuffer header = ByteBuffer.allocate(8);
            IsoTypeWriter.writeUInt32(header, (long) CastUtils.l2i(getSize()));
            header.put(IsoFile.fourCCtoBytes(getType()));
            header.rewind();
            writableByteChannel.write(header);
            for (Sample sample : FragmentedMp4Builder.this.getSamples(this.val$startSample, this.val$endSample, this.val$track, this.val$i)) {
                sample.writeTo(writableByteChannel);
            }
        }

        public void parse(DataSource fileChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        }
    }

    static {
        $assertionsDisabled = !FragmentedMp4Builder.class.desiredAssertionStatus();
        LOG = Logger.getLogger(FragmentedMp4Builder.class.getName());
    }

    public Date getDate() {
        return new Date();
    }

    public Box createFtyp(Movie movie) {
        List<String> minorBrands = new LinkedList();
        minorBrands.add("isom");
        minorBrands.add("iso2");
        minorBrands.add(VisualSampleEntry.TYPE3);
        return new FileTypeBox("isom", 0, minorBrands);
    }

    protected List<Track> sortTracksInSequence(List<Track> tracks, int cycle, Map<Track, long[]> intersectionMap) {
        List<Track> tracks2 = new LinkedList(tracks);
        Collections.sort(tracks2, new C03601(intersectionMap, cycle));
        return tracks2;
    }

    protected List<Box> createMoofMdat(Movie movie) {
        List<Box> moofsMdats = new LinkedList();
        HashMap<Track, long[]> intersectionMap = new HashMap();
        int maxNumberOfFragments = 0;
        for (Track track : movie.getTracks()) {
            long[] intersects = this.intersectionFinder.sampleNumbers(track);
            intersectionMap.put(track, intersects);
            maxNumberOfFragments = Math.max(maxNumberOfFragments, intersects.length);
        }
        int sequence = 1;
        for (int cycle = 0; cycle < maxNumberOfFragments; cycle++) {
            for (Track track2 : sortTracksInSequence(movie.getTracks(), cycle, intersectionMap)) {
                sequence = createFragment(moofsMdats, track2, (long[]) intersectionMap.get(track2), cycle, sequence);
            }
        }
        return moofsMdats;
    }

    protected int createFragment(List<Box> moofsMdats, Track track, long[] startSamples, int cycle, int sequence) {
        if (cycle >= startSamples.length) {
            return sequence;
        }
        long startSample = startSamples[cycle];
        long endSample = cycle + 1 < startSamples.length ? startSamples[cycle + 1] : (long) (track.getSamples().size() + 1);
        if (startSample == endSample) {
            return sequence;
        }
        moofsMdats.add(createMoof(startSample, endSample, track, sequence));
        int sequence2 = sequence + 1;
        moofsMdats.add(createMdat(startSample, endSample, track, sequence));
        return sequence2;
    }

    public Container build(Movie movie) {
        LOG.fine("Creating movie " + movie);
        if (this.intersectionFinder == null) {
            Track refTrack = null;
            for (Track track : movie.getTracks()) {
                if (track.getHandler().equals("vide")) {
                    refTrack = track;
                    break;
                }
            }
            this.intersectionFinder = new SyncSampleIntersectFinderImpl(movie, refTrack, -1);
        }
        BasicContainer isoFile = new BasicContainer();
        isoFile.addBox(createFtyp(movie));
        isoFile.addBox(createMoov(movie));
        for (Box box : createMoofMdat(movie)) {
            isoFile.addBox(box);
        }
        isoFile.addBox(createMfra(movie, isoFile));
        return isoFile;
    }

    protected Box createMdat(long startSample, long endSample, Track track, int i) {
        return new AnonymousClass1Mdat(startSample, endSample, track, i);
    }

    protected void createTfhd(long startSample, long endSample, Track track, int sequenceNumber, TrackFragmentBox parent) {
        TrackFragmentHeaderBox tfhd = new TrackFragmentHeaderBox();
        tfhd.setDefaultSampleFlags(new SampleFlags());
        tfhd.setBaseDataOffset(-1);
        tfhd.setTrackId(track.getTrackMetaData().getTrackId());
        tfhd.setDefaultBaseIsMoof(true);
        parent.addBox(tfhd);
    }

    protected void createMfhd(long startSample, long endSample, Track track, int sequenceNumber, MovieFragmentBox parent) {
        MovieFragmentHeaderBox mfhd = new MovieFragmentHeaderBox();
        mfhd.setSequenceNumber((long) sequenceNumber);
        parent.addBox(mfhd);
    }

    protected void createTraf(long startSample, long endSample, Track track, int sequenceNumber, MovieFragmentBox parent) {
        TrackFragmentBox traf = new TrackFragmentBox();
        parent.addBox(traf);
        createTfhd(startSample, endSample, track, sequenceNumber, traf);
        createTfdt(startSample, track, traf);
        createTrun(startSample, endSample, track, sequenceNumber, traf);
        if (track instanceof CencEncryptedTrack) {
            createSaiz(startSample, endSample, (CencEncryptedTrack) track, sequenceNumber, traf);
            createSenc(startSample, endSample, (CencEncryptedTrack) track, sequenceNumber, traf);
            createSaio(startSample, endSample, (CencEncryptedTrack) track, sequenceNumber, traf);
        }
        Map<String, List<GroupEntry>> groupEntryFamilies = new HashMap();
        for (Entry<GroupEntry, long[]> sg : track.getSampleGroups().entrySet()) {
            String type = ((GroupEntry) sg.getKey()).getType();
            List<GroupEntry> groupEntries = (List) groupEntryFamilies.get(type);
            if (groupEntries == null) {
                groupEntries = new ArrayList();
                groupEntryFamilies.put(type, groupEntries);
            }
            groupEntries.add((GroupEntry) sg.getKey());
        }
        for (Entry<String, List<GroupEntry>> sg2 : groupEntryFamilies.entrySet()) {
            SampleGroupDescriptionBox sgpd = new SampleGroupDescriptionBox();
            type = (String) sg2.getKey();
            sgpd.setGroupEntries((List) sg2.getValue());
            SampleToGroupBox sbgp = new SampleToGroupBox();
            sbgp.setGroupingType(type);
            SampleToGroupBox.Entry last = null;
            for (int i = CastUtils.l2i(startSample - 1); i < CastUtils.l2i(endSample - 1); i++) {
                int index = 0;
                for (int j = 0; j < ((List) sg2.getValue()).size(); j++) {
                    if (Arrays.binarySearch((long[]) track.getSampleGroups().get((GroupEntry) ((List) sg2.getValue()).get(j)), (long) i) >= 0) {
                        index = j + 1;
                    }
                }
                if (last == null || last.getGroupDescriptionIndex() != index) {
                    SampleToGroupBox.Entry entry = new SampleToGroupBox.Entry(1, index);
                    sbgp.getEntries().add(entry);
                } else {
                    last.setSampleCount(last.getSampleCount() + 1);
                }
            }
            traf.addBox(sgpd);
            traf.addBox(sbgp);
        }
    }

    protected void createSenc(long startSample, long endSample, CencEncryptedTrack track, int sequenceNumber, TrackFragmentBox parent) {
        SampleEncryptionBox senc = new SampleEncryptionBox();
        senc.setSubSampleEncryption(track.hasSubSampleEncryption());
        senc.setEntries(track.getSampleEncryptionEntries().subList(CastUtils.l2i(startSample - 1), CastUtils.l2i(endSample - 1)));
        parent.addBox(senc);
    }

    protected void createSaio(long startSample, long endSample, CencEncryptedTrack track, int sequenceNumber, TrackFragmentBox parent) {
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath(track.getSampleDescriptionBox(), "enc.[0]/sinf[0]/schm[0]");
        SampleAuxiliaryInformationOffsetsBox saio = new SampleAuxiliaryInformationOffsetsBox();
        parent.addBox(saio);
        if ($assertionsDisabled || parent.getBoxes(TrackRunBox.class).size() == 1) {
            saio.setAuxInfoType("cenc");
            saio.setFlags(1);
            long offset = 0 + 8;
            for (Box box : parent.getBoxes()) {
                if (box instanceof SampleEncryptionBox) {
                    offset += (long) ((SampleEncryptionBox) box).getOffsetToFirstIV();
                    break;
                }
                offset += box.getSize();
            }
            offset += 16;
            for (Object box2 : ((MovieFragmentBox) parent.getParent()).getBoxes()) {
                if (box2 == parent) {
                    break;
                }
                offset += box2.getSize();
            }
            saio.setOffsets(new long[]{offset});
            return;
        }
        throw new AssertionError("Don't know how to deal with multiple Track Run Boxes when encrypting");
    }

    protected void createSaiz(long startSample, long endSample, CencEncryptedTrack track, int sequenceNumber, TrackFragmentBox parent) {
        AbstractContainerBox sampleDescriptionBox = track.getSampleDescriptionBox();
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath(sampleDescriptionBox, "enc.[0]/sinf[0]/schm[0]");
        TrackEncryptionBox tenc = (TrackEncryptionBox) Path.getPath(sampleDescriptionBox, "enc.[0]/sinf[0]/schi[0]/tenc[0]");
        SampleAuxiliaryInformationSizesBox saiz = new SampleAuxiliaryInformationSizesBox();
        saiz.setAuxInfoType("cenc");
        saiz.setFlags(1);
        if (track.hasSubSampleEncryption()) {
            short[] sizes = new short[CastUtils.l2i(endSample - startSample)];
            List<CencSampleAuxiliaryDataFormat> auxs = track.getSampleEncryptionEntries().subList(CastUtils.l2i(startSample - 1), CastUtils.l2i(endSample - 1));
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = (short) ((CencSampleAuxiliaryDataFormat) auxs.get(i)).getSize();
            }
            saiz.setSampleInfoSizes(sizes);
        } else {
            saiz.setDefaultSampleInfoSize(tenc.getDefaultIvSize());
            saiz.setSampleCount(CastUtils.l2i(endSample - startSample));
        }
        parent.addBox(saiz);
    }

    protected List<Sample> getSamples(long startSample, long endSample, Track track, int sequenceNumber) {
        return track.getSamples().subList(CastUtils.l2i(startSample) - 1, CastUtils.l2i(endSample) - 1);
    }

    protected long[] getSampleSizes(long startSample, long endSample, Track track, int sequenceNumber) {
        List<Sample> samples = getSamples(startSample, endSample, track, sequenceNumber);
        long[] sampleSizes = new long[samples.size()];
        for (int i = 0; i < sampleSizes.length; i++) {
            sampleSizes[i] = ((Sample) samples.get(i)).getSize();
        }
        return sampleSizes;
    }

    protected void createTfdt(long startSample, Track track, TrackFragmentBox parent) {
        TrackFragmentBaseMediaDecodeTimeBox tfdt = new TrackFragmentBaseMediaDecodeTimeBox();
        tfdt.setVersion(1);
        long startTime = 0;
        long[] times = track.getSampleDurations();
        for (int i = 1; ((long) i) < startSample; i++) {
            startTime += times[i - 1];
        }
        tfdt.setBaseMediaDecodeTime(startTime);
        parent.addBox(tfdt);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void createTrun(long r26, long r28, com.googlecode.mp4parser.authoring.Track r30, int r31, com.coremedia.iso.boxes.fragment.TrackFragmentBox r32) {
        /*
        r25 = this;
        r17 = new com.coremedia.iso.boxes.fragment.TrackRunBox;
        r17.<init>();
        r18 = 1;
        r17.setVersion(r18);
        r15 = r25.getSampleSizes(r26, r28, r30, r31);
        r18 = 1;
        r17.setSampleDurationPresent(r18);
        r18 = 1;
        r17.setSampleSizePresent(r18);
        r10 = new java.util.ArrayList;
        r18 = r28 - r26;
        r18 = com.googlecode.mp4parser.util.CastUtils.l2i(r18);
        r0 = r18;
        r10.<init>(r0);
        r4 = r30.getCompositionTimeEntries();
        r8 = 0;
        if (r4 == 0) goto L_0x009e;
    L_0x002c:
        r18 = r4.size();
        if (r18 <= 0) goto L_0x009e;
    L_0x0032:
        r18 = r4.size();
        r0 = r18;
        r0 = new com.coremedia.iso.boxes.CompositionTimeToSample.Entry[r0];
        r18 = r0;
        r0 = r18;
        r18 = r4.toArray(r0);
        r18 = (com.coremedia.iso.boxes.CompositionTimeToSample.Entry[]) r18;
        r5 = r18;
    L_0x0046:
        if (r5 == 0) goto L_0x00a0;
    L_0x0048:
        r18 = r5[r8];
        r18 = r18.getCount();
    L_0x004e:
        r0 = r18;
        r6 = (long) r0;
        r18 = 0;
        r18 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r18 <= 0) goto L_0x00a3;
    L_0x0057:
        r18 = 1;
    L_0x0059:
        r17.setSampleCompositionTimeOffsetPresent(r18);
        r12 = 1;
    L_0x005e:
        r18 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1));
        if (r18 < 0) goto L_0x00a6;
    L_0x0062:
        r18 = r30.getSampleDependencies();
        if (r18 == 0) goto L_0x0072;
    L_0x0068:
        r18 = r30.getSampleDependencies();
        r18 = r18.isEmpty();
        if (r18 == 0) goto L_0x00cf;
    L_0x0072:
        r18 = r30.getSyncSamples();
        if (r18 == 0) goto L_0x0083;
    L_0x0078:
        r18 = r30.getSyncSamples();
        r0 = r18;
        r0 = r0.length;
        r18 = r0;
        if (r18 != 0) goto L_0x00cf;
    L_0x0083:
        r14 = 0;
    L_0x0084:
        r0 = r17;
        r0.setSampleFlagsPresent(r14);
        r12 = 0;
    L_0x008a:
        r0 = r15.length;
        r18 = r0;
        r0 = r18;
        if (r12 < r0) goto L_0x00d1;
    L_0x0091:
        r0 = r17;
        r0.setEntries(r10);
        r0 = r32;
        r1 = r17;
        r0.addBox(r1);
        return;
    L_0x009e:
        r5 = 0;
        goto L_0x0046;
    L_0x00a0:
        r18 = -1;
        goto L_0x004e;
    L_0x00a3:
        r18 = 0;
        goto L_0x0059;
    L_0x00a6:
        if (r5 == 0) goto L_0x00ca;
    L_0x00a8:
        r18 = 1;
        r6 = r6 - r18;
        r18 = 0;
        r18 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r18 != 0) goto L_0x00ca;
    L_0x00b2:
        r0 = r5.length;
        r18 = r0;
        r18 = r18 - r8;
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x00ca;
    L_0x00bf:
        r8 = r8 + 1;
        r18 = r5[r8];
        r18 = r18.getCount();
        r0 = r18;
        r6 = (long) r0;
    L_0x00ca:
        r18 = 1;
        r12 = r12 + r18;
        goto L_0x005e;
    L_0x00cf:
        r14 = 1;
        goto L_0x0084;
    L_0x00d1:
        r11 = new com.coremedia.iso.boxes.fragment.TrackRunBox$Entry;
        r11.<init>();
        r18 = r15[r12];
        r0 = r18;
        r11.setSampleSize(r0);
        if (r14 == 0) goto L_0x015c;
    L_0x00df:
        r16 = new com.coremedia.iso.boxes.fragment.SampleFlags;
        r16.<init>();
        r18 = r30.getSampleDependencies();
        if (r18 == 0) goto L_0x0121;
    L_0x00ea:
        r18 = r30.getSampleDependencies();
        r18 = r18.isEmpty();
        if (r18 != 0) goto L_0x0121;
    L_0x00f4:
        r18 = r30.getSampleDependencies();
        r0 = r18;
        r9 = r0.get(r12);
        r9 = (com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry) r9;
        r18 = r9.getSampleDependsOn();
        r0 = r16;
        r1 = r18;
        r0.setSampleDependsOn(r1);
        r18 = r9.getSampleIsDependentOn();
        r0 = r16;
        r1 = r18;
        r0.setSampleIsDependedOn(r1);
        r18 = r9.getSampleHasRedundancy();
        r0 = r16;
        r1 = r18;
        r0.setSampleHasRedundancy(r1);
    L_0x0121:
        r18 = r30.getSyncSamples();
        if (r18 == 0) goto L_0x0157;
    L_0x0127:
        r18 = r30.getSyncSamples();
        r0 = r18;
        r0 = r0.length;
        r18 = r0;
        if (r18 <= 0) goto L_0x0157;
    L_0x0132:
        r18 = r30.getSyncSamples();
        r0 = (long) r12;
        r20 = r0;
        r20 = r20 + r26;
        r0 = r18;
        r1 = r20;
        r18 = java.util.Arrays.binarySearch(r0, r1);
        if (r18 < 0) goto L_0x01aa;
    L_0x0145:
        r18 = 0;
        r0 = r16;
        r1 = r18;
        r0.setSampleIsDifferenceSample(r1);
        r18 = 2;
        r0 = r16;
        r1 = r18;
        r0.setSampleDependsOn(r1);
    L_0x0157:
        r0 = r16;
        r11.setSampleFlags(r0);
    L_0x015c:
        r18 = r30.getSampleDurations();
        r0 = (long) r12;
        r20 = r0;
        r20 = r20 + r26;
        r22 = 1;
        r20 = r20 - r22;
        r19 = com.googlecode.mp4parser.util.CastUtils.l2i(r20);
        r18 = r18[r19];
        r0 = r18;
        r11.setSampleDuration(r0);
        if (r5 == 0) goto L_0x01a3;
    L_0x0176:
        r18 = r5[r8];
        r18 = r18.getOffset();
        r0 = r18;
        r11.setSampleCompositionTimeOffset(r0);
        r18 = 1;
        r6 = r6 - r18;
        r18 = 0;
        r18 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r18 != 0) goto L_0x01a3;
    L_0x018b:
        r0 = r5.length;
        r18 = r0;
        r18 = r18 - r8;
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x01a3;
    L_0x0198:
        r8 = r8 + 1;
        r18 = r5[r8];
        r18 = r18.getCount();
        r0 = r18;
        r6 = (long) r0;
    L_0x01a3:
        r10.add(r11);
        r12 = r12 + 1;
        goto L_0x008a;
    L_0x01aa:
        r18 = 1;
        r0 = r16;
        r1 = r18;
        r0.setSampleIsDifferenceSample(r1);
        r18 = 1;
        r0 = r16;
        r1 = r18;
        r0.setSampleDependsOn(r1);
        goto L_0x0157;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.createTrun(long, long, com.googlecode.mp4parser.authoring.Track, int, com.coremedia.iso.boxes.fragment.TrackFragmentBox):void");
    }

    protected Box createMoof(long startSample, long endSample, Track track, int sequenceNumber) {
        MovieFragmentBox moof = new MovieFragmentBox();
        createMfhd(startSample, endSample, track, sequenceNumber, moof);
        createTraf(startSample, endSample, track, sequenceNumber, moof);
        TrackRunBox firstTrun = (TrackRunBox) moof.getTrackRunBoxes().get(0);
        firstTrun.setDataOffset(1);
        firstTrun.setDataOffset((int) (8 + moof.getSize()));
        return moof;
    }

    protected Box createMvhd(Movie movie) {
        MovieHeaderBox mvhd = new MovieHeaderBox();
        mvhd.setVersion(1);
        mvhd.setCreationTime(getDate());
        mvhd.setModificationTime(getDate());
        mvhd.setDuration(0);
        mvhd.setTimescale(movie.getTimescale());
        long nextTrackId = 0;
        for (Track track : movie.getTracks()) {
            if (nextTrackId < track.getTrackMetaData().getTrackId()) {
                nextTrackId = track.getTrackMetaData().getTrackId();
            }
        }
        mvhd.setNextTrackId(nextTrackId + 1);
        return mvhd;
    }

    protected Box createMoov(Movie movie) {
        MovieBox movieBox = new MovieBox();
        movieBox.addBox(createMvhd(movie));
        for (Track track : movie.getTracks()) {
            movieBox.addBox(createTrak(track, movie));
        }
        movieBox.addBox(createMvex(movie));
        return movieBox;
    }

    protected Box createTfra(Track track, Container isoFile) {
        TrackFragmentRandomAccessBox tfra = new TrackFragmentRandomAccessBox();
        tfra.setVersion(1);
        List<TrackFragmentRandomAccessBox.Entry> offset2timeEntries = new LinkedList();
        TrackExtendsBox trex = null;
        for (TrackExtendsBox innerTrex : Path.getPaths(isoFile, "moov/mvex/trex")) {
            if (innerTrex.getTrackId() == track.getTrackMetaData().getTrackId()) {
                trex = innerTrex;
            }
        }
        long offset = 0;
        long duration = 0;
        for (Box box : isoFile.getBoxes()) {
            if (box instanceof MovieFragmentBox) {
                List<TrackFragmentBox> trafs = ((MovieFragmentBox) box).getBoxes(TrackFragmentBox.class);
                for (int i = 0; i < trafs.size(); i++) {
                    TrackFragmentBox traf = (TrackFragmentBox) trafs.get(i);
                    if (traf.getTrackFragmentHeaderBox().getTrackId() == track.getTrackMetaData().getTrackId()) {
                        List<TrackRunBox> truns = traf.getBoxes(TrackRunBox.class);
                        for (int j = 0; j < truns.size(); j++) {
                            List<TrackFragmentRandomAccessBox.Entry> offset2timeEntriesThisTrun = new LinkedList();
                            TrackRunBox trun = (TrackRunBox) truns.get(j);
                            for (int k = 0; k < trun.getEntries().size(); k++) {
                                SampleFlags sf;
                                TrackRunBox.Entry trunEntry = (TrackRunBox.Entry) trun.getEntries().get(k);
                                if (k == 0 && trun.isFirstSampleFlagsPresent()) {
                                    sf = trun.getFirstSampleFlags();
                                } else if (trun.isSampleFlagsPresent()) {
                                    sf = trunEntry.getSampleFlags();
                                } else {
                                    sf = trex.getDefaultSampleFlags();
                                }
                                if (sf == null && track.getHandler().equals("vide")) {
                                    throw new RuntimeException("Cannot find SampleFlags for video track but it's required to build tfra");
                                }
                                if (sf == null || sf.getSampleDependsOn() == 2) {
                                    offset2timeEntriesThisTrun.add(new TrackFragmentRandomAccessBox.Entry(duration, offset, (long) (i + 1), (long) (j + 1), (long) (k + 1)));
                                }
                                duration += trunEntry.getSampleDuration();
                            }
                            if (offset2timeEntriesThisTrun.size() != trun.getEntries().size() || trun.getEntries().size() <= 0) {
                                offset2timeEntries.addAll(offset2timeEntriesThisTrun);
                            } else {
                                offset2timeEntries.add((TrackFragmentRandomAccessBox.Entry) offset2timeEntriesThisTrun.get(0));
                            }
                        }
                        continue;
                    }
                }
                continue;
            }
            offset += box.getSize();
        }
        tfra.setEntries(offset2timeEntries);
        tfra.setTrackId(track.getTrackMetaData().getTrackId());
        return tfra;
    }

    protected Box createMfra(Movie movie, Container isoFile) {
        MovieFragmentRandomAccessBox mfra = new MovieFragmentRandomAccessBox();
        for (Track track : movie.getTracks()) {
            mfra.addBox(createTfra(track, isoFile));
        }
        MovieFragmentRandomAccessOffsetBox mfro = new MovieFragmentRandomAccessOffsetBox();
        mfra.addBox(mfro);
        mfro.setMfraSize(mfra.getSize());
        return mfra;
    }

    protected Box createTrex(Movie movie, Track track) {
        TrackExtendsBox trex = new TrackExtendsBox();
        trex.setTrackId(track.getTrackMetaData().getTrackId());
        trex.setDefaultSampleDescriptionIndex(1);
        trex.setDefaultSampleDuration(0);
        trex.setDefaultSampleSize(0);
        SampleFlags sf = new SampleFlags();
        if ("soun".equals(track.getHandler()) || "subt".equals(track.getHandler())) {
            sf.setSampleDependsOn(2);
            sf.setSampleIsDependedOn(2);
        }
        trex.setDefaultSampleFlags(sf);
        return trex;
    }

    protected Box createMvex(Movie movie) {
        MovieExtendsBox mvex = new MovieExtendsBox();
        MovieExtendsHeaderBox mved = new MovieExtendsHeaderBox();
        mved.setVersion(1);
        for (Track track : movie.getTracks()) {
            long trackDuration = getTrackDuration(movie, track);
            if (mved.getFragmentDuration() < trackDuration) {
                mved.setFragmentDuration(trackDuration);
            }
        }
        mvex.addBox(mved);
        for (Track track2 : movie.getTracks()) {
            mvex.addBox(createTrex(movie, track2));
        }
        return mvex;
    }

    protected Box createTkhd(Movie movie, Track track) {
        TrackHeaderBox tkhd = new TrackHeaderBox();
        tkhd.setVersion(1);
        tkhd.setFlags(7);
        tkhd.setAlternateGroup(track.getTrackMetaData().getGroup());
        tkhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        tkhd.setDuration(0);
        tkhd.setHeight(track.getTrackMetaData().getHeight());
        tkhd.setWidth(track.getTrackMetaData().getWidth());
        tkhd.setLayer(track.getTrackMetaData().getLayer());
        tkhd.setModificationTime(getDate());
        tkhd.setTrackId(track.getTrackMetaData().getTrackId());
        tkhd.setVolume(track.getTrackMetaData().getVolume());
        return tkhd;
    }

    private long getTrackDuration(Movie movie, Track track) {
        return (track.getDuration() * movie.getTimescale()) / track.getTrackMetaData().getTimescale();
    }

    protected Box createMdhd(Movie movie, Track track) {
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        mdhd.setModificationTime(getDate());
        mdhd.setDuration(0);
        mdhd.setTimescale(track.getTrackMetaData().getTimescale());
        mdhd.setLanguage(track.getTrackMetaData().getLanguage());
        return mdhd;
    }

    protected Box createStbl(Movie movie, Track track) {
        SampleTableBox stbl = new SampleTableBox();
        createStsd(track, stbl);
        stbl.addBox(new TimeToSampleBox());
        stbl.addBox(new SampleToChunkBox());
        stbl.addBox(new SampleSizeBox());
        stbl.addBox(new StaticChunkOffsetBox());
        return stbl;
    }

    protected void createStsd(Track track, SampleTableBox stbl) {
        stbl.addBox(track.getSampleDescriptionBox());
    }

    protected Box createMinf(Track track, Movie movie) {
        MediaInformationBox minf = new MediaInformationBox();
        if (track.getHandler().equals("vide")) {
            minf.addBox(new VideoMediaHeaderBox());
        } else if (track.getHandler().equals("soun")) {
            minf.addBox(new SoundMediaHeaderBox());
        } else if (track.getHandler().equals(QuicktimeTextSampleEntry.TYPE)) {
            minf.addBox(new NullMediaHeaderBox());
        } else if (track.getHandler().equals("subt")) {
            minf.addBox(new SubtitleMediaHeaderBox());
        } else if (track.getHandler().equals(TrackReferenceTypeBox.TYPE1)) {
            minf.addBox(new HintMediaHeaderBox());
        } else if (track.getHandler().equals("sbtl")) {
            minf.addBox(new NullMediaHeaderBox());
        }
        minf.addBox(createDinf(movie, track));
        minf.addBox(createStbl(movie, track));
        return minf;
    }

    protected Box createMdiaHdlr(Track track, Movie movie) {
        HandlerBox hdlr = new HandlerBox();
        hdlr.setHandlerType(track.getHandler());
        return hdlr;
    }

    protected Box createMdia(Track track, Movie movie) {
        MediaBox mdia = new MediaBox();
        mdia.addBox(createMdhd(movie, track));
        mdia.addBox(createMdiaHdlr(track, movie));
        mdia.addBox(createMinf(track, movie));
        return mdia;
    }

    protected Box createTrak(Track track, Movie movie) {
        LOG.fine("Creating Track " + track);
        TrackBox trackBox = new TrackBox();
        trackBox.addBox(createTkhd(movie, track));
        Box edts = createEdts(track, movie);
        if (edts != null) {
            trackBox.addBox(edts);
        }
        trackBox.addBox(createMdia(track, movie));
        return trackBox;
    }

    protected Box createEdts(Track track, Movie movie) {
        if (track.getEdits() == null || track.getEdits().size() <= 0) {
            return null;
        }
        EditListBox elst = new EditListBox();
        elst.setVersion(1);
        List<EditListBox.Entry> entries = new ArrayList();
        for (Edit edit : track.getEdits()) {
            entries.add(new EditListBox.Entry(elst, Math.round(edit.getSegmentDuration() * ((double) movie.getTimescale())), (edit.getMediaTime() * track.getTrackMetaData().getTimescale()) / edit.getTimeScale(), edit.getMediaRate()));
        }
        elst.setEntries(entries);
        EditBox edts = new EditBox();
        edts.addBox(elst);
        return edts;
    }

    protected DataInformationBox createDinf(Movie movie, Track track) {
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        return dinf;
    }

    public FragmentIntersectionFinder getFragmentIntersectionFinder() {
        return this.intersectionFinder;
    }

    public void setIntersectionFinder(FragmentIntersectionFinder intersectionFinder) {
        this.intersectionFinder = intersectionFinder;
    }
}
