package org.telegram.messenger.exoplayer.extractor.mp4;

import android.support.v4.view.ViewCompat;
import com.coremedia.iso.boxes.ChunkOffset64BitBox;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.ProtectionSchemeInformationBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SchemeInformationBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.UserBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleWaveBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.AC3SpecificBox;
import com.googlecode.mp4parser.boxes.DTSSpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.googlecode.mp4parser.boxes.apple.PixelAspectRationAtom;
import com.googlecode.mp4parser.boxes.dece.SampleEncryptionBox;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.threegpp26244.SegmentIndexBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso14496.part30.WebVTTSampleEntry;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

abstract class Atom {
    public static final int FULL_HEADER_SIZE = 12;
    public static final int HEADER_SIZE = 8;
    public static final int LONG_HEADER_SIZE = 16;
    public static final int LONG_SIZE_PREFIX = 1;
    public static final int TYPE_DASHES;
    public static final int TYPE_TTML;
    public static final int TYPE_ac_3;
    public static final int TYPE_avc1;
    public static final int TYPE_avc3;
    public static final int TYPE_avcC;
    public static final int TYPE_co64;
    public static final int TYPE_ctts;
    public static final int TYPE_d263;
    public static final int TYPE_dac3;
    public static final int TYPE_data;
    public static final int TYPE_ddts;
    public static final int TYPE_dec3;
    public static final int TYPE_dtsc;
    public static final int TYPE_dtse;
    public static final int TYPE_dtsh;
    public static final int TYPE_dtsl;
    public static final int TYPE_ec_3;
    public static final int TYPE_edts;
    public static final int TYPE_elst;
    public static final int TYPE_enca;
    public static final int TYPE_encv;
    public static final int TYPE_esds;
    public static final int TYPE_frma;
    public static final int TYPE_ftyp;
    public static final int TYPE_hdlr;
    public static final int TYPE_hev1;
    public static final int TYPE_hvc1;
    public static final int TYPE_hvcC;
    public static final int TYPE_ilst;
    public static final int TYPE_lpcm;
    public static final int TYPE_mdat;
    public static final int TYPE_mdhd;
    public static final int TYPE_mdia;
    public static final int TYPE_mean;
    public static final int TYPE_mehd;
    public static final int TYPE_meta;
    public static final int TYPE_minf;
    public static final int TYPE_moof;
    public static final int TYPE_moov;
    public static final int TYPE_mp4a;
    public static final int TYPE_mp4v;
    public static final int TYPE_mvex;
    public static final int TYPE_mvhd;
    public static final int TYPE_name;
    public static final int TYPE_pasp;
    public static final int TYPE_pssh;
    public static final int TYPE_s263;
    public static final int TYPE_saio;
    public static final int TYPE_saiz;
    public static final int TYPE_samr;
    public static final int TYPE_sawb;
    public static final int TYPE_schi;
    public static final int TYPE_schm;
    public static final int TYPE_senc;
    public static final int TYPE_sidx;
    public static final int TYPE_sinf;
    public static final int TYPE_sowt;
    public static final int TYPE_stbl;
    public static final int TYPE_stco;
    public static final int TYPE_stpp;
    public static final int TYPE_stsc;
    public static final int TYPE_stsd;
    public static final int TYPE_stss;
    public static final int TYPE_stsz;
    public static final int TYPE_stts;
    public static final int TYPE_tenc;
    public static final int TYPE_tfdt;
    public static final int TYPE_tfhd;
    public static final int TYPE_tkhd;
    public static final int TYPE_traf;
    public static final int TYPE_trak;
    public static final int TYPE_trex;
    public static final int TYPE_trun;
    public static final int TYPE_tx3g;
    public static final int TYPE_udta;
    public static final int TYPE_uuid;
    public static final int TYPE_vmhd;
    public static final int TYPE_wave;
    public static final int TYPE_wvtt;
    public final int type;

    static final class ContainerAtom extends Atom {
        public final List<ContainerAtom> containerChildren;
        public final long endPosition;
        public final List<LeafAtom> leafChildren;

        public ContainerAtom(int type, long endPosition) {
            super(type);
            this.endPosition = endPosition;
            this.leafChildren = new ArrayList();
            this.containerChildren = new ArrayList();
        }

        public void add(LeafAtom atom) {
            this.leafChildren.add(atom);
        }

        public void add(ContainerAtom atom) {
            this.containerChildren.add(atom);
        }

        public LeafAtom getLeafAtomOfType(int type) {
            int childrenSize = this.leafChildren.size();
            for (int i = 0; i < childrenSize; i += Atom.LONG_SIZE_PREFIX) {
                LeafAtom atom = (LeafAtom) this.leafChildren.get(i);
                if (atom.type == type) {
                    return atom;
                }
            }
            return null;
        }

        public ContainerAtom getContainerAtomOfType(int type) {
            int childrenSize = this.containerChildren.size();
            for (int i = 0; i < childrenSize; i += Atom.LONG_SIZE_PREFIX) {
                ContainerAtom atom = (ContainerAtom) this.containerChildren.get(i);
                if (atom.type == type) {
                    return atom;
                }
            }
            return null;
        }

        public int getChildAtomOfTypeCount(int type) {
            int i;
            int count = 0;
            int size = this.leafChildren.size();
            for (i = 0; i < size; i += Atom.LONG_SIZE_PREFIX) {
                if (((LeafAtom) this.leafChildren.get(i)).type == type) {
                    count += Atom.LONG_SIZE_PREFIX;
                }
            }
            size = this.containerChildren.size();
            for (i = 0; i < size; i += Atom.LONG_SIZE_PREFIX) {
                if (((ContainerAtom) this.containerChildren.get(i)).type == type) {
                    count += Atom.LONG_SIZE_PREFIX;
                }
            }
            return count;
        }

        public String toString() {
            return Atom.getAtomTypeString(this.type) + " leaves: " + Arrays.toString(this.leafChildren.toArray(new LeafAtom[0])) + " containers: " + Arrays.toString(this.containerChildren.toArray(new ContainerAtom[0]));
        }
    }

    static final class LeafAtom extends Atom {
        public final ParsableByteArray data;

        public LeafAtom(int type, ParsableByteArray data) {
            super(type);
            this.data = data;
        }
    }

    static {
        TYPE_ftyp = Util.getIntegerCodeForString(FileTypeBox.TYPE);
        TYPE_avc1 = Util.getIntegerCodeForString(VisualSampleEntry.TYPE3);
        TYPE_avc3 = Util.getIntegerCodeForString(VisualSampleEntry.TYPE4);
        TYPE_hvc1 = Util.getIntegerCodeForString(VisualSampleEntry.TYPE6);
        TYPE_hev1 = Util.getIntegerCodeForString(VisualSampleEntry.TYPE7);
        TYPE_s263 = Util.getIntegerCodeForString(VisualSampleEntry.TYPE2);
        TYPE_d263 = Util.getIntegerCodeForString("d263");
        TYPE_mdat = Util.getIntegerCodeForString(MediaDataBox.TYPE);
        TYPE_mp4a = Util.getIntegerCodeForString(AudioSampleEntry.TYPE3);
        TYPE_wave = Util.getIntegerCodeForString(AppleWaveBox.TYPE);
        TYPE_lpcm = Util.getIntegerCodeForString("lpcm");
        TYPE_sowt = Util.getIntegerCodeForString("sowt");
        TYPE_ac_3 = Util.getIntegerCodeForString(AudioSampleEntry.TYPE8);
        TYPE_dac3 = Util.getIntegerCodeForString(AC3SpecificBox.TYPE);
        TYPE_ec_3 = Util.getIntegerCodeForString(AudioSampleEntry.TYPE9);
        TYPE_dec3 = Util.getIntegerCodeForString(EC3SpecificBox.TYPE);
        TYPE_dtsc = Util.getIntegerCodeForString("dtsc");
        TYPE_dtsh = Util.getIntegerCodeForString(AudioSampleEntry.TYPE12);
        TYPE_dtsl = Util.getIntegerCodeForString(AudioSampleEntry.TYPE11);
        TYPE_dtse = Util.getIntegerCodeForString(AudioSampleEntry.TYPE13);
        TYPE_ddts = Util.getIntegerCodeForString(DTSSpecificBox.TYPE);
        TYPE_tfdt = Util.getIntegerCodeForString(TrackFragmentBaseMediaDecodeTimeBox.TYPE);
        TYPE_tfhd = Util.getIntegerCodeForString(TrackFragmentHeaderBox.TYPE);
        TYPE_trex = Util.getIntegerCodeForString(TrackExtendsBox.TYPE);
        TYPE_trun = Util.getIntegerCodeForString(TrackRunBox.TYPE);
        TYPE_sidx = Util.getIntegerCodeForString(SegmentIndexBox.TYPE);
        TYPE_moov = Util.getIntegerCodeForString(MovieBox.TYPE);
        TYPE_mvhd = Util.getIntegerCodeForString(MovieHeaderBox.TYPE);
        TYPE_trak = Util.getIntegerCodeForString(TrackBox.TYPE);
        TYPE_mdia = Util.getIntegerCodeForString(MediaBox.TYPE);
        TYPE_minf = Util.getIntegerCodeForString(MediaInformationBox.TYPE);
        TYPE_stbl = Util.getIntegerCodeForString(SampleTableBox.TYPE);
        TYPE_avcC = Util.getIntegerCodeForString(AvcConfigurationBox.TYPE);
        TYPE_hvcC = Util.getIntegerCodeForString(HevcConfigurationBox.TYPE);
        TYPE_esds = Util.getIntegerCodeForString(ESDescriptorBox.TYPE);
        TYPE_moof = Util.getIntegerCodeForString(MovieFragmentBox.TYPE);
        TYPE_traf = Util.getIntegerCodeForString(TrackFragmentBox.TYPE);
        TYPE_mvex = Util.getIntegerCodeForString(MovieExtendsBox.TYPE);
        TYPE_mehd = Util.getIntegerCodeForString(MovieExtendsHeaderBox.TYPE);
        TYPE_tkhd = Util.getIntegerCodeForString(TrackHeaderBox.TYPE);
        TYPE_edts = Util.getIntegerCodeForString(EditBox.TYPE);
        TYPE_elst = Util.getIntegerCodeForString(EditListBox.TYPE);
        TYPE_mdhd = Util.getIntegerCodeForString(MediaHeaderBox.TYPE);
        TYPE_hdlr = Util.getIntegerCodeForString(HandlerBox.TYPE);
        TYPE_stsd = Util.getIntegerCodeForString(SampleDescriptionBox.TYPE);
        TYPE_pssh = Util.getIntegerCodeForString(ProtectionSystemSpecificHeaderBox.TYPE);
        TYPE_sinf = Util.getIntegerCodeForString(ProtectionSchemeInformationBox.TYPE);
        TYPE_schm = Util.getIntegerCodeForString(SchemeTypeBox.TYPE);
        TYPE_schi = Util.getIntegerCodeForString(SchemeInformationBox.TYPE);
        TYPE_tenc = Util.getIntegerCodeForString(TrackEncryptionBox.TYPE);
        TYPE_encv = Util.getIntegerCodeForString(VisualSampleEntry.TYPE_ENCRYPTED);
        TYPE_enca = Util.getIntegerCodeForString(AudioSampleEntry.TYPE_ENCRYPTED);
        TYPE_frma = Util.getIntegerCodeForString(OriginalFormatBox.TYPE);
        TYPE_saiz = Util.getIntegerCodeForString(SampleAuxiliaryInformationSizesBox.TYPE);
        TYPE_saio = Util.getIntegerCodeForString(SampleAuxiliaryInformationOffsetsBox.TYPE);
        TYPE_uuid = Util.getIntegerCodeForString(UserBox.TYPE);
        TYPE_senc = Util.getIntegerCodeForString(SampleEncryptionBox.TYPE);
        TYPE_pasp = Util.getIntegerCodeForString(PixelAspectRationAtom.TYPE);
        TYPE_TTML = Util.getIntegerCodeForString("TTML");
        TYPE_vmhd = Util.getIntegerCodeForString(VideoMediaHeaderBox.TYPE);
        TYPE_mp4v = Util.getIntegerCodeForString(VisualSampleEntry.TYPE1);
        TYPE_stts = Util.getIntegerCodeForString(TimeToSampleBox.TYPE);
        TYPE_stss = Util.getIntegerCodeForString(SyncSampleBox.TYPE);
        TYPE_ctts = Util.getIntegerCodeForString(CompositionTimeToSample.TYPE);
        TYPE_stsc = Util.getIntegerCodeForString(SampleToChunkBox.TYPE);
        TYPE_stsz = Util.getIntegerCodeForString(SampleSizeBox.TYPE);
        TYPE_stco = Util.getIntegerCodeForString(StaticChunkOffsetBox.TYPE);
        TYPE_co64 = Util.getIntegerCodeForString(ChunkOffset64BitBox.TYPE);
        TYPE_tx3g = Util.getIntegerCodeForString(TextSampleEntry.TYPE1);
        TYPE_wvtt = Util.getIntegerCodeForString(WebVTTSampleEntry.TYPE);
        TYPE_stpp = Util.getIntegerCodeForString(XMLSubtitleSampleEntry.TYPE);
        TYPE_samr = Util.getIntegerCodeForString(AudioSampleEntry.TYPE1);
        TYPE_sawb = Util.getIntegerCodeForString(AudioSampleEntry.TYPE2);
        TYPE_udta = Util.getIntegerCodeForString(UserDataBox.TYPE);
        TYPE_meta = Util.getIntegerCodeForString(MetaBox.TYPE);
        TYPE_ilst = Util.getIntegerCodeForString(AppleItemListBox.TYPE);
        TYPE_mean = Util.getIntegerCodeForString("mean");
        TYPE_name = Util.getIntegerCodeForString("name");
        TYPE_data = Util.getIntegerCodeForString("data");
        TYPE_DASHES = Util.getIntegerCodeForString("----");
    }

    public Atom(int type) {
        this.type = type;
    }

    public String toString() {
        return getAtomTypeString(this.type);
    }

    public static int parseFullAtomVersion(int fullAtomInt) {
        return (fullAtomInt >> 24) & NalUnitUtil.EXTENDED_SAR;
    }

    public static int parseFullAtomFlags(int fullAtomInt) {
        return ViewCompat.MEASURED_SIZE_MASK & fullAtomInt;
    }

    public static String getAtomTypeString(int type) {
        return TtmlNode.ANONYMOUS_REGION_ID + ((char) (type >> 24)) + ((char) ((type >> LONG_HEADER_SIZE) & NalUnitUtil.EXTENDED_SAR)) + ((char) ((type >> HEADER_SIZE) & NalUnitUtil.EXTENDED_SAR)) + ((char) (type & NalUnitUtil.EXTENDED_SAR));
    }
}
