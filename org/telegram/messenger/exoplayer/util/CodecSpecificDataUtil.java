package org.telegram.messenger.exoplayer.util;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.upstream.DefaultHttpDataSource;

public final class CodecSpecificDataUtil {
    private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
    private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
    private static final int AUDIO_OBJECT_TYPE_PS = 29;
    private static final int AUDIO_OBJECT_TYPE_SBR = 5;
    private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
    private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE;
    private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
    private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
    private static final byte[] NAL_START_CODE;

    static {
        NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};
        AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[]{96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, 7350};
        AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = new int[]{0, 1, AUDIO_OBJECT_TYPE_AAC_LC, 3, 4, AUDIO_OBJECT_TYPE_SBR, 6, 8, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, 7, 8, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID, 8, AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID};
    }

    private CodecSpecificDataUtil() {
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] audioSpecificConfig) {
        int sampleRate;
        boolean z;
        boolean z2 = true;
        ParsableBitArray bitArray = new ParsableBitArray(audioSpecificConfig);
        int audioObjectType = bitArray.readBits(AUDIO_OBJECT_TYPE_SBR);
        int frequencyIndex = bitArray.readBits(4);
        if (frequencyIndex == AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY) {
            sampleRate = bitArray.readBits(24);
        } else {
            if (frequencyIndex < 13) {
                z = true;
            } else {
                z = false;
            }
            Assertions.checkArgument(z);
            sampleRate = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[frequencyIndex];
        }
        int channelConfiguration = bitArray.readBits(4);
        if (audioObjectType == AUDIO_OBJECT_TYPE_SBR || audioObjectType == AUDIO_OBJECT_TYPE_PS) {
            frequencyIndex = bitArray.readBits(4);
            if (frequencyIndex == AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY) {
                sampleRate = bitArray.readBits(24);
            } else {
                if (frequencyIndex < 13) {
                    z = true;
                } else {
                    z = false;
                }
                Assertions.checkArgument(z);
                sampleRate = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[frequencyIndex];
            }
            if (bitArray.readBits(AUDIO_OBJECT_TYPE_SBR) == AUDIO_OBJECT_TYPE_ER_BSAC) {
                channelConfiguration = bitArray.readBits(4);
            }
        }
        int channelCount = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[channelConfiguration];
        if (channelCount == AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID) {
            z2 = false;
        }
        Assertions.checkArgument(z2);
        return Pair.create(Integer.valueOf(sampleRate), Integer.valueOf(channelCount));
    }

    public static byte[] buildAacAudioSpecificConfig(int audioObjectType, int sampleRateIndex, int channelConfig) {
        byte[] audioSpecificConfig = new byte[AUDIO_OBJECT_TYPE_AAC_LC];
        audioSpecificConfig[0] = (byte) (((audioObjectType << 3) & 248) | ((sampleRateIndex >> 1) & 7));
        audioSpecificConfig[1] = (byte) (((sampleRateIndex << 7) & MessagesController.UPDATE_MASK_USER_PHONE) | ((channelConfig << 3) & 120));
        return audioSpecificConfig;
    }

    public static byte[] buildAacAudioSpecificConfig(int sampleRate, int numChannels) {
        int i;
        int sampleRateIndex = AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID;
        for (i = 0; i < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length; i++) {
            if (sampleRate == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i]) {
                sampleRateIndex = i;
            }
        }
        int channelConfig = AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID;
        for (i = 0; i < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length; i++) {
            if (numChannels == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[i]) {
                channelConfig = i;
            }
        }
        byte[] csd = new byte[AUDIO_OBJECT_TYPE_AAC_LC];
        csd[0] = (byte) ((sampleRateIndex >> 1) | 16);
        csd[1] = (byte) (((sampleRateIndex & 1) << 7) | (channelConfig << 3));
        return csd;
    }

    public static byte[] buildNalUnit(byte[] data, int offset, int length) {
        byte[] nalUnit = new byte[(NAL_START_CODE.length + length)];
        System.arraycopy(NAL_START_CODE, 0, nalUnit, 0, NAL_START_CODE.length);
        System.arraycopy(data, offset, nalUnit, NAL_START_CODE.length, length);
        return nalUnit;
    }

    public static byte[][] splitNalUnits(byte[] data) {
        if (!isNalStartCode(data, 0)) {
            return (byte[][]) null;
        }
        List<Integer> starts = new ArrayList();
        int nalUnitIndex = 0;
        do {
            starts.add(Integer.valueOf(nalUnitIndex));
            nalUnitIndex = findNalStartCode(data, NAL_START_CODE.length + nalUnitIndex);
        } while (nalUnitIndex != AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID);
        byte[][] split = new byte[starts.size()][];
        int i = 0;
        while (i < starts.size()) {
            int startIndex = ((Integer) starts.get(i)).intValue();
            byte[] nal = new byte[((i < starts.size() + AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID ? ((Integer) starts.get(i + 1)).intValue() : data.length) - startIndex)];
            System.arraycopy(data, startIndex, nal, 0, nal.length);
            split[i] = nal;
            i++;
        }
        return split;
    }

    private static int findNalStartCode(byte[] data, int index) {
        int endIndex = data.length - NAL_START_CODE.length;
        for (int i = index; i <= endIndex; i++) {
            if (isNalStartCode(data, i)) {
                return i;
            }
        }
        return AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID;
    }

    private static boolean isNalStartCode(byte[] data, int index) {
        if (data.length - index <= NAL_START_CODE.length) {
            return false;
        }
        for (int j = 0; j < NAL_START_CODE.length; j++) {
            if (data[index + j] != NAL_START_CODE[j]) {
                return false;
            }
        }
        return true;
    }
}
