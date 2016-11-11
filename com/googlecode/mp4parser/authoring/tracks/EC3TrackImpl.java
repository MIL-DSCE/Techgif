package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox.Entry;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class EC3TrackImpl extends AbstractTrack {
    private static final long MAX_FRAMES_PER_MMAP = 20;
    private List<BitStreamInfo> bitStreamInfos;
    private int bitrate;
    private final DataSource dataSource;
    private long[] decodingTimes;
    private int frameSize;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    TrackMetaData trackMetaData;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.EC3TrackImpl.1 */
    class C16621 implements Sample {
        private final /* synthetic */ int val$start;

        C16621(int i) {
            this.val$start = i;
        }

        public void writeTo(WritableByteChannel channel) throws IOException {
            EC3TrackImpl.this.dataSource.transferTo((long) this.val$start, (long) EC3TrackImpl.this.frameSize, channel);
        }

        public long getSize() {
            return (long) EC3TrackImpl.this.frameSize;
        }

        public ByteBuffer asByteBuffer() {
            try {
                return EC3TrackImpl.this.dataSource.map((long) this.val$start, (long) EC3TrackImpl.this.frameSize);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BitStreamInfo extends Entry {
        public int bitrate;
        public int chanmap;
        public int frameSize;
        public int samplerate;
        public int strmtyp;
        public int substreamid;

        public String toString() {
            return "BitStreamInfo{frameSize=" + this.frameSize + ", substreamid=" + this.substreamid + ", bitrate=" + this.bitrate + ", samplerate=" + this.samplerate + ", strmtyp=" + this.strmtyp + ", chanmap=" + this.chanmap + '}';
        }
    }

    public EC3TrackImpl(DataSource dataSource) throws IOException {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.bitStreamInfos = new LinkedList();
        this.dataSource = dataSource;
        boolean done = false;
        while (!done) {
            BitStreamInfo bsi = readVariables();
            if (bsi == null) {
                throw new IOException();
            }
            for (BitStreamInfo entry : this.bitStreamInfos) {
                if (bsi.strmtyp != 1 && entry.substreamid == bsi.substreamid) {
                    done = true;
                }
            }
            if (!done) {
                this.bitStreamInfos.add(bsi);
            }
        }
        if (this.bitStreamInfos.size() == 0) {
            throw new IOException();
        }
        int samplerate = ((BitStreamInfo) this.bitStreamInfos.get(0)).samplerate;
        this.sampleDescriptionBox = new SampleDescriptionBox();
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE9);
        audioSampleEntry.setChannelCount(2);
        audioSampleEntry.setSampleRate((long) samplerate);
        audioSampleEntry.setDataReferenceIndex(1);
        audioSampleEntry.setSampleSize(16);
        EC3SpecificBox ec3 = new EC3SpecificBox();
        int[] deps = new int[this.bitStreamInfos.size()];
        int[] chan_locs = new int[this.bitStreamInfos.size()];
        for (BitStreamInfo bsi2 : this.bitStreamInfos) {
            if (bsi2.strmtyp == 1) {
                int i = bsi2.substreamid;
                deps[i] = deps[i] + 1;
                chan_locs[bsi2.substreamid] = ((bsi2.chanmap >> 6) & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) | ((bsi2.chanmap >> 5) & NalUnitUtil.EXTENDED_SAR);
            }
        }
        for (BitStreamInfo bsi22 : this.bitStreamInfos) {
            if (bsi22.strmtyp != 1) {
                Entry e = new Entry();
                e.fscod = bsi22.fscod;
                e.bsid = bsi22.bsid;
                e.bsmod = bsi22.bsmod;
                e.acmod = bsi22.acmod;
                e.lfeon = bsi22.lfeon;
                e.reserved = 0;
                e.num_dep_sub = deps[bsi22.substreamid];
                e.chan_loc = chan_locs[bsi22.substreamid];
                e.reserved2 = 0;
                ec3.addEntry(e);
            }
            this.bitrate += bsi22.bitrate;
            this.frameSize += bsi22.frameSize;
        }
        ec3.setDataRate(this.bitrate / 1000);
        audioSampleEntry.addBox(ec3);
        this.sampleDescriptionBox.addBox(audioSampleEntry);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale((long) samplerate);
        this.trackMetaData.setVolume(TouchHelperCallback.ALPHA_FULL);
        dataSource.position(0);
        this.samples = readSamples();
        this.decodingTimes = new long[this.samples.size()];
        Arrays.fill(this.decodingTimes, 1536);
    }

    public void close() throws IOException {
        this.dataSource.close();
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public long[] getSampleDurations() {
        return this.decodingTimes;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public String getHandler() {
        return "soun";
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    private BitStreamInfo readVariables() throws IOException {
        long startPosition = this.dataSource.position();
        ByteBuffer bb = ByteBuffer.allocate(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.dataSource.read(bb);
        bb.rewind();
        BitReaderBuffer brb = new BitReaderBuffer(bb);
        if (brb.readBits(16) != 2935) {
            return null;
        }
        int numblkscod;
        BitStreamInfo entry = new BitStreamInfo();
        entry.strmtyp = brb.readBits(2);
        entry.substreamid = brb.readBits(3);
        entry.frameSize = (brb.readBits(11) + 1) * 2;
        entry.fscod = brb.readBits(2);
        int fscod2 = -1;
        if (entry.fscod == 3) {
            fscod2 = brb.readBits(2);
            numblkscod = 3;
        } else {
            numblkscod = brb.readBits(2);
        }
        int numberOfBlocksPerSyncFrame = 0;
        switch (numblkscod) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                numberOfBlocksPerSyncFrame = 1;
                break;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                numberOfBlocksPerSyncFrame = 2;
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                numberOfBlocksPerSyncFrame = 3;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                numberOfBlocksPerSyncFrame = 6;
                break;
        }
        entry.frameSize *= 6 / numberOfBlocksPerSyncFrame;
        entry.acmod = brb.readBits(3);
        entry.lfeon = brb.readBits(1);
        entry.bsid = brb.readBits(5);
        brb.readBits(5);
        if (1 == brb.readBits(1)) {
            brb.readBits(8);
        }
        if (entry.acmod == 0) {
            brb.readBits(5);
            if (1 == brb.readBits(1)) {
                brb.readBits(8);
            }
        }
        int i = entry.strmtyp;
        if (1 == r0 && 1 == brb.readBits(1)) {
            entry.chanmap = brb.readBits(16);
        }
        if (1 == brb.readBits(1)) {
            if (entry.acmod > 2) {
                brb.readBits(2);
            }
            if (1 == (entry.acmod & 1) && entry.acmod > 2) {
                brb.readBits(3);
                brb.readBits(3);
            }
            if ((entry.acmod & 4) > 0) {
                brb.readBits(3);
                brb.readBits(3);
            }
            i = entry.lfeon;
            if (1 == r0 && 1 == brb.readBits(1)) {
                brb.readBits(5);
            }
            if (entry.strmtyp == 0) {
                int i2;
                if (1 == brb.readBits(1)) {
                    brb.readBits(6);
                }
                if (entry.acmod == 0 && 1 == brb.readBits(1)) {
                    brb.readBits(6);
                }
                if (1 == brb.readBits(1)) {
                    brb.readBits(6);
                }
                int mixdef = brb.readBits(2);
                if (1 == mixdef) {
                    brb.readBits(5);
                } else if (2 == mixdef) {
                    brb.readBits(12);
                } else if (3 == mixdef) {
                    int mixdeflen = brb.readBits(5);
                    if (1 == brb.readBits(1)) {
                        brb.readBits(5);
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(4);
                        }
                        if (1 == brb.readBits(1)) {
                            if (1 == brb.readBits(1)) {
                                brb.readBits(4);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(4);
                            }
                        }
                    }
                    if (1 == brb.readBits(1)) {
                        brb.readBits(5);
                        if (1 == brb.readBits(1)) {
                            brb.readBits(7);
                            if (1 == brb.readBits(1)) {
                                brb.readBits(8);
                            }
                        }
                    }
                    for (i2 = 0; i2 < mixdeflen + 2; i2++) {
                        brb.readBits(8);
                    }
                    brb.byteSync();
                }
                if (entry.acmod < 2) {
                    if (1 == brb.readBits(1)) {
                        brb.readBits(14);
                    }
                    if (entry.acmod == 0 && 1 == brb.readBits(1)) {
                        brb.readBits(14);
                    }
                    if (1 == brb.readBits(1)) {
                        if (numblkscod == 0) {
                            brb.readBits(5);
                        } else {
                            for (i2 = 0; i2 < numberOfBlocksPerSyncFrame; i2++) {
                                if (1 == brb.readBits(1)) {
                                    brb.readBits(5);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (1 == brb.readBits(1)) {
            entry.bsmod = brb.readBits(3);
        }
        switch (entry.fscod) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                entry.samplerate = 48000;
                break;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                entry.samplerate = 44100;
                break;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                entry.samplerate = 32000;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                switch (fscod2) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        entry.samplerate = 24000;
                        break;
                    case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                        entry.samplerate = 22050;
                        break;
                    case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                        entry.samplerate = 16000;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        entry.samplerate = 0;
                        break;
                    default:
                        break;
                }
        }
        if (entry.samplerate == 0) {
            return null;
        }
        double d = (double) entry.frameSize;
        entry.bitrate = (int) (((((double) entry.samplerate) / 1536.0d) * r0) * 8.0d);
        this.dataSource.position(((long) entry.frameSize) + startPosition);
        return entry;
    }

    private List<Sample> readSamples() throws IOException {
        int framesLeft = CastUtils.l2i((this.dataSource.size() - this.dataSource.position()) / ((long) this.frameSize));
        List<Sample> mySamples = new ArrayList(framesLeft);
        for (int i = 0; i < framesLeft; i++) {
            mySamples.add(new C16621(i * this.frameSize));
        }
        return mySamples;
    }

    public String toString() {
        return "EC3TrackImpl{bitrate=" + this.bitrate + ", bitStreamInfos=" + this.bitStreamInfos + '}';
    }
}
