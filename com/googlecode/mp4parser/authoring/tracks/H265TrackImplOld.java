package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoTypeReader;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public class H265TrackImplOld {
    public static final int AUD_NUT = 35;
    private static final int BLA_N_LP = 18;
    private static final int BLA_W_LP = 16;
    private static final int BLA_W_RADL = 17;
    private static final long BUFFER = 1048576;
    private static final int CRA_NUT = 21;
    private static final int IDR_N_LP = 20;
    private static final int IDR_W_RADL = 19;
    public static final int PPS_NUT = 34;
    public static final int PREFIX_SEI_NUT = 39;
    private static final int RADL_N = 6;
    private static final int RADL_R = 7;
    private static final int RASL_N = 8;
    private static final int RASL_R = 9;
    public static final int RSV_NVCL41 = 41;
    public static final int RSV_NVCL42 = 42;
    public static final int RSV_NVCL43 = 43;
    public static final int RSV_NVCL44 = 44;
    public static final int SPS_NUT = 33;
    private static final int STSA_N = 4;
    private static final int STSA_R = 5;
    private static final int TRAIL_N = 0;
    private static final int TRAIL_R = 1;
    private static final int TSA_N = 2;
    private static final int TSA_R = 3;
    public static final int UNSPEC48 = 48;
    public static final int UNSPEC49 = 49;
    public static final int UNSPEC50 = 50;
    public static final int UNSPEC51 = 51;
    public static final int UNSPEC52 = 52;
    public static final int UNSPEC53 = 53;
    public static final int UNSPEC54 = 54;
    public static final int UNSPEC55 = 55;
    public static final int VPS_NUT = 32;
    LinkedHashMap<Long, ByteBuffer> pictureParamterSets;
    List<Sample> samples;
    LinkedHashMap<Long, ByteBuffer> sequenceParamterSets;
    List<Long> syncSamples;
    LinkedHashMap<Long, ByteBuffer> videoParamterSets;

    class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        DataSource dataSource;
        int inBufferPos;
        long start;

        LookAhead(DataSource dataSource) throws IOException {
            this.bufferStartPos = 0;
            this.inBufferPos = H265TrackImplOld.TRAIL_N;
            this.dataSource = dataSource;
            fillBuffer();
        }

        public void fillBuffer() throws IOException {
            this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataSource.size() - this.bufferStartPos, H265TrackImplOld.BUFFER));
        }

        boolean nextThreeEquals001() throws IOException {
            if (this.buffer.limit() - this.inBufferPos >= H265TrackImplOld.TSA_R) {
                if (this.buffer.get(this.inBufferPos) == null && this.buffer.get(this.inBufferPos + H265TrackImplOld.TRAIL_R) == null && this.buffer.get(this.inBufferPos + H265TrackImplOld.TSA_N) == (byte) 1) {
                    return true;
                }
                return false;
            } else if (this.bufferStartPos + ((long) this.inBufferPos) == this.dataSource.size()) {
                throw new EOFException();
            } else {
                throw new RuntimeException("buffer repositioning require");
            }
        }

        boolean nextThreeEquals000or001orEof() throws IOException {
            if (this.buffer.limit() - this.inBufferPos >= H265TrackImplOld.TSA_R) {
                if (this.buffer.get(this.inBufferPos) == null && this.buffer.get(this.inBufferPos + H265TrackImplOld.TRAIL_R) == null && (this.buffer.get(this.inBufferPos + H265TrackImplOld.TSA_N) == null || this.buffer.get(this.inBufferPos + H265TrackImplOld.TSA_N) == (byte) 1)) {
                    return true;
                }
                return false;
            } else if ((this.bufferStartPos + ((long) this.inBufferPos)) + 3 <= this.dataSource.size()) {
                this.bufferStartPos = this.start;
                this.inBufferPos = H265TrackImplOld.TRAIL_N;
                fillBuffer();
                return nextThreeEquals000or001orEof();
            } else if (this.bufferStartPos + ((long) this.inBufferPos) != this.dataSource.size()) {
                return false;
            } else {
                return true;
            }
        }

        void discardByte() {
            this.inBufferPos += H265TrackImplOld.TRAIL_R;
        }

        void discardNext3AndMarkStart() {
            this.inBufferPos += H265TrackImplOld.TSA_R;
            this.start = this.bufferStartPos + ((long) this.inBufferPos);
        }

        public ByteBuffer getNal() {
            if (this.start >= this.bufferStartPos) {
                this.buffer.position((int) (this.start - this.bufferStartPos));
                Buffer sample = this.buffer.slice();
                sample.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return (ByteBuffer) sample;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }
    }

    public static class NalUnitHeader {
        int forbiddenZeroFlag;
        int nalUnitType;
        int nuhLayerId;
        int nuhTemporalIdPlusOne;
    }

    public enum PARSE_STATE {
        AUD_SEI_SLICE,
        SEI_SLICE,
        SLICE_OES_EOB
    }

    public H265TrackImplOld(DataSource ds) throws IOException {
        this.videoParamterSets = new LinkedHashMap();
        this.sequenceParamterSets = new LinkedHashMap();
        this.pictureParamterSets = new LinkedHashMap();
        this.syncSamples = new ArrayList();
        this.samples = new ArrayList();
        LookAhead la = new LookAhead(ds);
        long sampleNo = 1;
        List<ByteBuffer> accessUnit = new ArrayList();
        int accessUnitNalType = TRAIL_N;
        while (true) {
            ByteBuffer nal = findNextNal(la);
            if (nal == null) {
                System.err.println(TtmlNode.ANONYMOUS_REGION_ID);
                HevcDecoderConfigurationRecord hvcC = new HevcDecoderConfigurationRecord();
                hvcC.setArrays(getArrays());
                hvcC.setAvgFrameRate(TRAIL_N);
                return;
            }
            NalUnitHeader nalUnitHeader = getNalUnitHeader(nal);
            switch (nalUnitHeader.nalUnitType) {
                case VPS_NUT /*32*/:
                    this.videoParamterSets.put(Long.valueOf(sampleNo), nal);
                    break;
                case SPS_NUT /*33*/:
                    this.sequenceParamterSets.put(Long.valueOf(sampleNo), nal);
                    break;
                case PPS_NUT /*34*/:
                    this.pictureParamterSets.put(Long.valueOf(sampleNo), nal);
                    break;
            }
            if (nalUnitHeader.nalUnitType < VPS_NUT) {
                accessUnitNalType = nalUnitHeader.nalUnitType;
            }
            if (isFirstOfAU(nalUnitHeader.nalUnitType, nal, accessUnit) && !accessUnit.isEmpty()) {
                System.err.println("##########################");
                for (ByteBuffer byteBuffer : accessUnit) {
                    NalUnitHeader _nalUnitHeader = getNalUnitHeader(byteBuffer);
                    PrintStream printStream = System.err;
                    Object[] objArr = new Object[STSA_N];
                    objArr[TRAIL_N] = Integer.valueOf(_nalUnitHeader.nalUnitType);
                    objArr[TRAIL_R] = Integer.valueOf(_nalUnitHeader.nuhLayerId);
                    objArr[TSA_N] = Integer.valueOf(_nalUnitHeader.nuhTemporalIdPlusOne);
                    objArr[TSA_R] = Integer.valueOf(byteBuffer.limit());
                    printStream.println(String.format("type: %3d - layer: %3d - tempId: %3d - size: %3d", objArr));
                }
                System.err.println("                          ##########################");
                this.samples.add(createSample(accessUnit));
                accessUnit.clear();
                sampleNo++;
            }
            accessUnit.add(nal);
            if (accessUnitNalType >= BLA_W_LP && accessUnitNalType <= CRA_NUT) {
                this.syncSamples.add(Long.valueOf(sampleNo));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        H265TrackImplOld h265TrackImplOld = new H265TrackImplOld(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
    }

    private ByteBuffer findNextNal(LookAhead la) throws IOException {
        while (!la.nextThreeEquals001()) {
            try {
                la.discardByte();
            } catch (EOFException e) {
                return null;
            }
        }
        la.discardNext3AndMarkStart();
        while (!la.nextThreeEquals000or001orEof()) {
            la.discardByte();
        }
        return la.getNal();
    }

    public void profile_tier_level(int maxNumSubLayersMinus1, CAVLCReader r) throws IOException {
        int j;
        int i;
        r.readU(TSA_N, "general_profile_space ");
        r.readBool("general_tier_flag");
        r.readU(STSA_R, "general_profile_idc");
        boolean[] general_profile_compatibility_flag = new boolean[VPS_NUT];
        for (j = TRAIL_N; j < VPS_NUT; j += TRAIL_R) {
            general_profile_compatibility_flag[j] = r.readBool("general_profile_compatibility_flag[" + j + "]");
        }
        r.readBool("general_progressive_source_flag");
        r.readBool("general_interlaced_source_flag");
        r.readBool("general_non_packed_constraint_flag");
        r.readBool("general_frame_only_constraint_flag");
        long readU = (long) r.readU(RSV_NVCL44, "general_reserved_zero_44bits");
        r.readU(RASL_N, "general_level_idc");
        boolean[] sub_layer_profile_present_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] sub_layer_level_present_flag = new boolean[maxNumSubLayersMinus1];
        for (i = TRAIL_N; i < maxNumSubLayersMinus1; i += TRAIL_R) {
            sub_layer_profile_present_flag[i] = r.readBool("sub_layer_profile_present_flag[" + i + "]");
            sub_layer_level_present_flag[i] = r.readBool("sub_layer_level_present_flag[" + i + "]");
        }
        if (maxNumSubLayersMinus1 > 0) {
            for (i = maxNumSubLayersMinus1; i < RASL_N; i += TRAIL_R) {
                r.readU(TSA_N, "reserved_zero_2bits");
            }
        }
        int[] sub_layer_profile_space = new int[maxNumSubLayersMinus1];
        boolean[] sub_layer_tier_flag = new boolean[maxNumSubLayersMinus1];
        int[] sub_layer_profile_idc = new int[maxNumSubLayersMinus1];
        int[] iArr = new int[]{maxNumSubLayersMinus1, VPS_NUT};
        boolean[][] sub_layer_profile_compatibility_flag = (boolean[][]) Array.newInstance(Boolean.TYPE, iArr);
        boolean[] sub_layer_progressive_source_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] sub_layer_interlaced_source_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] sub_layer_non_packed_constraint_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] sub_layer_frame_only_constraint_flag = new boolean[maxNumSubLayersMinus1];
        int[] sub_layer_level_idc = new int[maxNumSubLayersMinus1];
        for (i = TRAIL_N; i < maxNumSubLayersMinus1; i += TRAIL_R) {
            if (sub_layer_profile_present_flag[i]) {
                sub_layer_profile_space[i] = r.readU(TSA_N, "sub_layer_profile_space[" + i + "]");
                sub_layer_tier_flag[i] = r.readBool("sub_layer_tier_flag[" + i + "]");
                sub_layer_profile_idc[i] = r.readU(STSA_R, "sub_layer_profile_idc[" + i + "]");
                for (j = TRAIL_N; j < VPS_NUT; j += TRAIL_R) {
                    sub_layer_profile_compatibility_flag[i][j] = r.readBool("sub_layer_profile_compatibility_flag[" + i + "][" + j + "]");
                }
                sub_layer_progressive_source_flag[i] = r.readBool("sub_layer_progressive_source_flag[" + i + "]");
                sub_layer_interlaced_source_flag[i] = r.readBool("sub_layer_interlaced_source_flag[" + i + "]");
                sub_layer_non_packed_constraint_flag[i] = r.readBool("sub_layer_non_packed_constraint_flag[" + i + "]");
                sub_layer_frame_only_constraint_flag[i] = r.readBool("sub_layer_frame_only_constraint_flag[" + i + "]");
                r.readNBit(RSV_NVCL44, "reserved");
            }
            if (sub_layer_level_present_flag[i]) {
                sub_layer_level_idc[i] = r.readU(RASL_N, "sub_layer_level_idc");
            }
        }
    }

    public int getFrameRate(ByteBuffer vps) throws IOException {
        int i;
        CAVLCReader r = new CAVLCReader(Channels.newInputStream(new ByteBufferByteChannel((ByteBuffer) vps.position(TRAIL_N))));
        r.readU(STSA_N, "vps_parameter_set_id");
        r.readU(TSA_N, "vps_reserved_three_2bits");
        r.readU(RADL_N, "vps_max_layers_minus1");
        int vps_max_sub_layers_minus1 = r.readU(TSA_R, "vps_max_sub_layers_minus1");
        r.readBool("vps_temporal_id_nesting_flag");
        r.readU(BLA_W_LP, "vps_reserved_0xffff_16bits");
        profile_tier_level(vps_max_sub_layers_minus1, r);
        boolean vps_sub_layer_ordering_info_present_flag = r.readBool("vps_sub_layer_ordering_info_present_flag");
        if (vps_sub_layer_ordering_info_present_flag) {
            i = TRAIL_N;
        } else {
            i = vps_max_sub_layers_minus1;
        }
        int[] vps_max_dec_pic_buffering_minus1 = new int[i];
        if (vps_sub_layer_ordering_info_present_flag) {
            i = TRAIL_N;
        } else {
            i = vps_max_sub_layers_minus1;
        }
        int[] vps_max_num_reorder_pics = new int[i];
        if (vps_sub_layer_ordering_info_present_flag) {
            i = TRAIL_N;
        } else {
            i = vps_max_sub_layers_minus1;
        }
        int[] vps_max_latency_increase_plus1 = new int[i];
        int i2 = vps_sub_layer_ordering_info_present_flag ? TRAIL_N : vps_max_sub_layers_minus1;
        while (i2 <= vps_max_sub_layers_minus1) {
            vps_max_dec_pic_buffering_minus1[i2] = r.readUE("vps_max_dec_pic_buffering_minus1[" + i2 + "]");
            vps_max_num_reorder_pics[i2] = r.readUE("vps_max_dec_pic_buffering_minus1[" + i2 + "]");
            vps_max_latency_increase_plus1[i2] = r.readUE("vps_max_dec_pic_buffering_minus1[" + i2 + "]");
            i2 += TRAIL_R;
        }
        int vps_max_layer_id = r.readU(RADL_N, "vps_max_layer_id");
        int vps_num_layer_sets_minus1 = r.readUE("vps_num_layer_sets_minus1");
        boolean[][] layer_id_included_flag = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{vps_num_layer_sets_minus1, vps_max_layer_id});
        for (i2 = TRAIL_R; i2 <= vps_num_layer_sets_minus1; i2 += TRAIL_R) {
            for (int j = TRAIL_N; j <= vps_max_layer_id; j += TRAIL_R) {
                layer_id_included_flag[i2][j] = r.readBool("layer_id_included_flag[" + i2 + "][" + j + "]");
            }
        }
        if (r.readBool("vps_timing_info_present_flag")) {
            long readU = (long) r.readU(VPS_NUT, "vps_num_units_in_tick");
            readU = (long) r.readU(VPS_NUT, "vps_time_scale");
            if (r.readBool("vps_poc_proportional_to_timing_flag")) {
                r.readUE("vps_num_ticks_poc_diff_one_minus1");
            }
            int vps_num_hrd_parameters = r.readUE("vps_num_hrd_parameters");
            int[] hrd_layer_set_idx = new int[vps_num_hrd_parameters];
            boolean[] cprms_present_flag = new boolean[vps_num_hrd_parameters];
            for (i2 = TRAIL_N; i2 < vps_num_hrd_parameters; i2 += TRAIL_R) {
                hrd_layer_set_idx[i2] = r.readUE("hrd_layer_set_idx[" + i2 + "]");
                if (i2 > 0) {
                    cprms_present_flag[i2] = r.readBool("cprms_present_flag[" + i2 + "]");
                } else {
                    cprms_present_flag[TRAIL_N] = true;
                }
                hrd_parameters(cprms_present_flag[i2], vps_max_sub_layers_minus1, r);
            }
        }
        if (r.readBool("vps_extension_flag")) {
            while (r.moreRBSPData()) {
                r.readBool("vps_extension_data_flag");
            }
        }
        r.readTrailingBits();
        return TRAIL_N;
    }

    private void hrd_parameters(boolean commonInfPresentFlag, int maxNumSubLayersMinus1, CAVLCReader r) throws IOException {
        boolean nal_hrd_parameters_present_flag = false;
        boolean vcl_hrd_parameters_present_flag = false;
        boolean sub_pic_hrd_params_present_flag = false;
        if (commonInfPresentFlag) {
            nal_hrd_parameters_present_flag = r.readBool("nal_hrd_parameters_present_flag");
            vcl_hrd_parameters_present_flag = r.readBool("vcl_hrd_parameters_present_flag");
            if (nal_hrd_parameters_present_flag || vcl_hrd_parameters_present_flag) {
                sub_pic_hrd_params_present_flag = r.readBool("sub_pic_hrd_params_present_flag");
                if (sub_pic_hrd_params_present_flag) {
                    r.readU(RASL_N, "tick_divisor_minus2");
                    r.readU(STSA_R, "du_cpb_removal_delay_increment_length_minus1");
                    r.readBool("sub_pic_cpb_params_in_pic_timing_sei_flag");
                    r.readU(STSA_R, "dpb_output_delay_du_length_minus1");
                }
                r.readU(STSA_N, "bit_rate_scale");
                r.readU(STSA_N, "cpb_size_scale");
                if (sub_pic_hrd_params_present_flag) {
                    r.readU(STSA_N, "cpb_size_du_scale");
                }
                r.readU(STSA_R, "initial_cpb_removal_delay_length_minus1");
                r.readU(STSA_R, "au_cpb_removal_delay_length_minus1");
                r.readU(STSA_R, "dpb_output_delay_length_minus1");
            }
        }
        boolean[] fixed_pic_rate_general_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] fixed_pic_rate_within_cvs_flag = new boolean[maxNumSubLayersMinus1];
        boolean[] low_delay_hrd_flag = new boolean[maxNumSubLayersMinus1];
        int[] cpb_cnt_minus1 = new int[maxNumSubLayersMinus1];
        int[] elemental_duration_in_tc_minus1 = new int[maxNumSubLayersMinus1];
        for (int i = TRAIL_N; i <= maxNumSubLayersMinus1; i += TRAIL_R) {
            fixed_pic_rate_general_flag[i] = r.readBool("fixed_pic_rate_general_flag[" + i + "]");
            if (!fixed_pic_rate_general_flag[i]) {
                fixed_pic_rate_within_cvs_flag[i] = r.readBool("fixed_pic_rate_within_cvs_flag[" + i + "]");
            }
            if (fixed_pic_rate_within_cvs_flag[i]) {
                elemental_duration_in_tc_minus1[i] = r.readUE("elemental_duration_in_tc_minus1[" + i + "]");
            } else {
                low_delay_hrd_flag[i] = r.readBool("low_delay_hrd_flag[" + i + "]");
            }
            if (!low_delay_hrd_flag[i]) {
                cpb_cnt_minus1[i] = r.readUE("cpb_cnt_minus1[" + i + "]");
            }
            if (nal_hrd_parameters_present_flag) {
                sub_layer_hrd_parameters(i, cpb_cnt_minus1[i], sub_pic_hrd_params_present_flag, r);
            }
            if (vcl_hrd_parameters_present_flag) {
                sub_layer_hrd_parameters(i, cpb_cnt_minus1[i], sub_pic_hrd_params_present_flag, r);
            }
        }
    }

    void sub_layer_hrd_parameters(int subLayerId, int cpbCnt, boolean sub_pic_hrd_params_present_flag, CAVLCReader r) throws IOException {
        int[] bit_rate_value_minus1 = new int[cpbCnt];
        int[] cpb_size_value_minus1 = new int[cpbCnt];
        int[] cpb_size_du_value_minus1 = new int[cpbCnt];
        int[] bit_rate_du_value_minus1 = new int[cpbCnt];
        boolean[] cbr_flag = new boolean[cpbCnt];
        for (int i = TRAIL_N; i <= cpbCnt; i += TRAIL_R) {
            bit_rate_value_minus1[i] = r.readUE("bit_rate_value_minus1[" + i + "]");
            cpb_size_value_minus1[i] = r.readUE("cpb_size_value_minus1[" + i + "]");
            if (sub_pic_hrd_params_present_flag) {
                cpb_size_du_value_minus1[i] = r.readUE("cpb_size_du_value_minus1[" + i + "]");
                bit_rate_du_value_minus1[i] = r.readUE("bit_rate_du_value_minus1[" + i + "]");
            }
            cbr_flag[i] = r.readBool("cbr_flag[" + i + "]");
        }
    }

    private List<HevcDecoderConfigurationRecord.Array> getArrays() {
        HevcDecoderConfigurationRecord.Array vpsArray = new HevcDecoderConfigurationRecord.Array();
        vpsArray.array_completeness = true;
        vpsArray.nal_unit_type = VPS_NUT;
        vpsArray.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer : this.videoParamterSets.values()) {
            byte[] ps = new byte[byteBuffer.limit()];
            byteBuffer.position(TRAIL_N);
            byteBuffer.get(ps);
            vpsArray.nalUnits.add(ps);
        }
        HevcDecoderConfigurationRecord.Array spsArray = new HevcDecoderConfigurationRecord.Array();
        spsArray.array_completeness = true;
        spsArray.nal_unit_type = SPS_NUT;
        spsArray.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer2 : this.sequenceParamterSets.values()) {
            ps = new byte[byteBuffer2.limit()];
            byteBuffer2.position(TRAIL_N);
            byteBuffer2.get(ps);
            spsArray.nalUnits.add(ps);
        }
        HevcDecoderConfigurationRecord.Array ppsArray = new HevcDecoderConfigurationRecord.Array();
        ppsArray.array_completeness = true;
        ppsArray.nal_unit_type = SPS_NUT;
        ppsArray.nalUnits = new ArrayList();
        for (ByteBuffer byteBuffer22 : this.pictureParamterSets.values()) {
            ps = new byte[byteBuffer22.limit()];
            byteBuffer22.position(TRAIL_N);
            byteBuffer22.get(ps);
            ppsArray.nalUnits.add(ps);
        }
        HevcDecoderConfigurationRecord.Array[] arrayArr = new HevcDecoderConfigurationRecord.Array[TSA_R];
        arrayArr[TRAIL_N] = vpsArray;
        arrayArr[TRAIL_R] = spsArray;
        arrayArr[TSA_N] = ppsArray;
        return Arrays.asList(arrayArr);
    }

    boolean isFirstOfAU(int nalUnitType, ByteBuffer nalUnit, List<ByteBuffer> accessUnit) {
        if (accessUnit.isEmpty()) {
            return true;
        }
        boolean vclPresentInCurrentAU;
        if (getNalUnitHeader((ByteBuffer) accessUnit.get(accessUnit.size() - 1)).nalUnitType <= 31) {
            vclPresentInCurrentAU = true;
        } else {
            vclPresentInCurrentAU = false;
        }
        switch (nalUnitType) {
            case VPS_NUT /*32*/:
            case SPS_NUT /*33*/:
            case PPS_NUT /*34*/:
            case AUD_NUT /*35*/:
            case PREFIX_SEI_NUT /*39*/:
            case RSV_NVCL41 /*41*/:
            case RSV_NVCL42 /*42*/:
            case RSV_NVCL43 /*43*/:
            case RSV_NVCL44 /*44*/:
            case UNSPEC48 /*48*/:
            case UNSPEC49 /*49*/:
            case UNSPEC50 /*50*/:
            case UNSPEC51 /*51*/:
            case UNSPEC52 /*52*/:
            case UNSPEC53 /*53*/:
            case UNSPEC54 /*54*/:
            case UNSPEC55 /*55*/:
                if (vclPresentInCurrentAU) {
                    return true;
                }
                break;
        }
        switch (nalUnitType) {
            case TRAIL_N /*0*/:
            case TRAIL_R /*1*/:
            case TSA_N /*2*/:
            case TSA_R /*3*/:
            case STSA_N /*4*/:
            case STSA_R /*5*/:
            case RADL_N /*6*/:
            case RADL_R /*7*/:
            case RASL_N /*8*/:
            case RASL_R /*9*/:
            case BLA_W_LP /*16*/:
            case BLA_W_RADL /*17*/:
            case BLA_N_LP /*18*/:
            case IDR_W_RADL /*19*/:
            case IDR_N_LP /*20*/:
            case CRA_NUT /*21*/:
                byte[] b = new byte[UNSPEC50];
                nalUnit.position(TRAIL_N);
                nalUnit.get(b);
                nalUnit.position(TSA_N);
                return vclPresentInCurrentAU && (IsoTypeReader.readUInt8(nalUnit) & MessagesController.UPDATE_MASK_USER_PHONE) > 0;
            default:
                return false;
        }
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer nal) {
        nal.position(TRAIL_N);
        int nal_unit_header = IsoTypeReader.readUInt16(nal);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (TLRPC.MESSAGE_FLAG_EDITED & nal_unit_header) >> 15;
        nalUnitHeader.nalUnitType = (nal_unit_header & 32256) >> RASL_R;
        nalUnitHeader.nuhLayerId = (nal_unit_header & 504) >> TSA_R;
        nalUnitHeader.nuhTemporalIdPlusOne = nal_unit_header & RADL_R;
        return nalUnitHeader;
    }

    protected Sample createSample(List<ByteBuffer> nals) {
        byte[] sizeInfo = new byte[(nals.size() * STSA_N)];
        ByteBuffer sizeBuf = ByteBuffer.wrap(sizeInfo);
        for (ByteBuffer b : nals) {
            sizeBuf.putInt(b.remaining());
        }
        ByteBuffer[] data = new ByteBuffer[(nals.size() * TSA_N)];
        for (int i = TRAIL_N; i < nals.size(); i += TRAIL_R) {
            data[i * TSA_N] = ByteBuffer.wrap(sizeInfo, i * STSA_N, STSA_N);
            data[(i * TSA_N) + TRAIL_R] = (ByteBuffer) nals.get(i);
        }
        return new SampleImpl(data);
    }
}
