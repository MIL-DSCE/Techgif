package com.googlecode.mp4parser.authoring.tracks.h265;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack.LookAhead;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord.Array;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.tgnet.TLRPC;

public class H265TrackImpl extends AbstractH26XTrack implements NalUnitTypes {
    ArrayList<ByteBuffer> pps;
    ArrayList<Sample> samples;
    ArrayList<ByteBuffer> sps;
    SampleDescriptionBox stsd;
    ArrayList<ByteBuffer> vps;

    public H265TrackImpl(DataSource dataSource) throws IOException {
        super(dataSource);
        this.sps = new ArrayList();
        this.pps = new ArrayList();
        this.vps = new ArrayList();
        this.samples = new ArrayList();
        ArrayList<ByteBuffer> nals = new ArrayList();
        LookAhead la = new LookAhead(dataSource);
        boolean[] vclNalUnitSeenInAU = new boolean[1];
        boolean[] isIdr = new boolean[]{true};
        while (true) {
            ByteBuffer nal = findNextNal(la);
            if (nal == null) {
                this.stsd = createSampleDescriptionBox();
                this.decodingTimes = new long[this.samples.size()];
                getTrackMetaData().setTimescale(25);
                Arrays.fill(this.decodingTimes, 1);
                return;
            }
            NalUnitHeader unitHeader = getNalUnitHeader(nal);
            if (vclNalUnitSeenInAU[0]) {
                if (!isVcl(unitHeader)) {
                    switch (unitHeader.nalUnitType) {
                        case ItemTouchHelper.END /*32*/:
                        case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                        case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                        case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                        case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                        case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                        case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL41 /*41*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL43 /*43*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC49 /*49*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC50 /*50*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC51 /*51*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC52 /*52*/:
                        case TLRPC.LAYER /*53*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC54 /*54*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC55 /*55*/:
                            wrapUp(nals, vclNalUnitSeenInAU, isIdr);
                            break;
                        default:
                            break;
                    }
                } else if ((nal.get(2) & -128) != 0) {
                    wrapUp(nals, vclNalUnitSeenInAU, isIdr);
                }
            }
            switch (unitHeader.nalUnitType) {
                case ItemTouchHelper.END /*32*/:
                    nal.position(2);
                    this.vps.add(nal.slice());
                    System.err.println("Stored VPS");
                    break;
                case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                    nal.position(2);
                    this.sps.add(nal.slice());
                    nal.position(1);
                    SequenceParameterSetRbsp sequenceParameterSetRbsp = new SequenceParameterSetRbsp(Channels.newInputStream(new ByteBufferByteChannel(nal.slice())));
                    System.err.println("Stored SPS");
                    break;
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                    nal.position(2);
                    this.pps.add(nal.slice());
                    System.err.println("Stored PPS");
                    break;
                case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                    SEIMessage sEIMessage = new SEIMessage(new BitReaderBuffer(nal.slice()));
                    break;
            }
            switch (unitHeader.nalUnitType) {
                case ItemTouchHelper.END /*32*/:
                case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                case NalUnitTypes.NAL_TYPE_FD_NUT /*38*/:
                    break;
                default:
                    System.err.println("Adding " + unitHeader.nalUnitType);
                    nals.add(nal);
                    break;
            }
            if (isVcl(unitHeader)) {
                switch (unitHeader.nalUnitType) {
                    case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
                    case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                        isIdr[0] = isIdr[0] & 1;
                        break;
                    default:
                        isIdr[0] = false;
                        break;
                }
            }
            vclNalUnitSeenInAU[0] = vclNalUnitSeenInAU[0] | isVcl(unitHeader);
        }
    }

    private SampleDescriptionBox createSampleDescriptionBox() {
        this.stsd = new SampleDescriptionBox();
        VisualSampleEntry visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE6);
        visualSampleEntry.setDataReferenceIndex(1);
        visualSampleEntry.setDepth(24);
        visualSampleEntry.setFrameCount(1);
        visualSampleEntry.setHorizresolution(72.0d);
        visualSampleEntry.setVertresolution(72.0d);
        visualSampleEntry.setWidth(640);
        visualSampleEntry.setHeight(480);
        visualSampleEntry.setCompressorname("HEVC Coding");
        HevcConfigurationBox hevcConfigurationBox = new HevcConfigurationBox();
        Array spsArray = new Array();
        spsArray.array_completeness = true;
        spsArray.nal_unit_type = 33;
        spsArray.nalUnits = new ArrayList();
        Iterator it = this.sps.iterator();
        while (it.hasNext()) {
            spsArray.nalUnits.add(AbstractH26XTrack.toArray((ByteBuffer) it.next()));
        }
        Array ppsArray = new Array();
        ppsArray.array_completeness = true;
        ppsArray.nal_unit_type = 34;
        ppsArray.nalUnits = new ArrayList();
        it = this.pps.iterator();
        while (it.hasNext()) {
            ppsArray.nalUnits.add(AbstractH26XTrack.toArray((ByteBuffer) it.next()));
        }
        Array vpsArray = new Array();
        vpsArray.array_completeness = true;
        vpsArray.nal_unit_type = 34;
        vpsArray.nalUnits = new ArrayList();
        it = this.vps.iterator();
        while (it.hasNext()) {
            vpsArray.nalUnits.add(AbstractH26XTrack.toArray((ByteBuffer) it.next()));
        }
        hevcConfigurationBox.getArrays().addAll(Arrays.asList(new Array[]{spsArray, vpsArray, ppsArray}));
        visualSampleEntry.addBox(hevcConfigurationBox);
        this.stsd.addBox(visualSampleEntry);
        return this.stsd;
    }

    public void wrapUp(List<ByteBuffer> nals, boolean[] vclNalUnitSeenInAU, boolean[] isIdr) {
        this.samples.add(createSampleObject(nals));
        System.err.print("Create AU from " + nals.size() + " NALs");
        if (isIdr[0]) {
            System.err.println("  IDR");
        } else {
            System.err.println();
        }
        vclNalUnitSeenInAU[0] = false;
        isIdr[0] = true;
        nals.clear();
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return null;
    }

    public String getHandler() {
        return "vide";
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    boolean isVcl(NalUnitHeader nalUnitHeader) {
        return nalUnitHeader.nalUnitType >= 0 && nalUnitHeader.nalUnitType <= 31;
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer nal) {
        nal.position(0);
        int nal_unit_header = IsoTypeReader.readUInt16(nal);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (TLRPC.MESSAGE_FLAG_EDITED & nal_unit_header) >> 15;
        nalUnitHeader.nalUnitType = (nal_unit_header & 32256) >> 9;
        nalUnitHeader.nuhLayerId = (nal_unit_header & 504) >> 3;
        nalUnitHeader.nuhTemporalIdPlusOne = nal_unit_header & 7;
        return nalUnitHeader;
    }

    public static void main(String[] args) throws IOException {
        Track track = new H265TrackImpl(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
        Movie movie = new Movie();
        movie.addTrack(track);
        new DefaultMp4Builder().build(movie).writeContainer(new FileOutputStream("output.mp4").getChannel());
    }
}
