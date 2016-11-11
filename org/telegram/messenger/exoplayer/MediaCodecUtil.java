package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.media.MediaCodecList;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.tgnet.TLRPC;

@TargetApi(16)
public final class MediaCodecUtil {
    private static final DecoderInfo PASSTHROUGH_DECODER_INFO;
    private static final String TAG = "MediaCodecUtil";
    private static final Map<CodecKey, List<DecoderInfo>> decoderInfosCache;
    private static int maxH264DecodableFrameSize;

    private static final class CodecKey {
        public final String mimeType;
        public final boolean secure;

        public CodecKey(String mimeType, boolean secure) {
            this.mimeType = mimeType;
            this.secure = secure;
        }

        public int hashCode() {
            return (((this.mimeType == null ? 0 : this.mimeType.hashCode()) + 31) * 31) + (this.secure ? 1231 : 1237);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != CodecKey.class) {
                return false;
            }
            CodecKey other = (CodecKey) obj;
            if (TextUtils.equals(this.mimeType, other.mimeType) && this.secure == other.secure) {
                return true;
            }
            return false;
        }
    }

    public static class DecoderQueryException extends IOException {
        private DecoderQueryException(Throwable cause) {
            super("Failed to query underlying media codecs", cause);
        }
    }

    private interface MediaCodecListCompat {
        int getCodecCount();

        MediaCodecInfo getCodecInfoAt(int i);

        boolean isSecurePlaybackSupported(String str, CodecCapabilities codecCapabilities);

        boolean secureDecodersExplicit();
    }

    private static final class MediaCodecListCompatV16 implements MediaCodecListCompat {
        private MediaCodecListCompatV16() {
        }

        public int getCodecCount() {
            return MediaCodecList.getCodecCount();
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            return MediaCodecList.getCodecInfoAt(index);
        }

        public boolean secureDecodersExplicit() {
            return false;
        }

        public boolean isSecurePlaybackSupported(String mimeType, CodecCapabilities capabilities) {
            return MediaController.MIME_TYPE.equals(mimeType);
        }
    }

    @TargetApi(21)
    private static final class MediaCodecListCompatV21 implements MediaCodecListCompat {
        private final int codecKind;
        private MediaCodecInfo[] mediaCodecInfos;

        public MediaCodecListCompatV21(boolean includeSecure) {
            this.codecKind = includeSecure ? 1 : 0;
        }

        public int getCodecCount() {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos.length;
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos[index];
        }

        public boolean secureDecodersExplicit() {
            return true;
        }

        public boolean isSecurePlaybackSupported(String mimeType, CodecCapabilities capabilities) {
            return capabilities.isFeatureSupported("secure-playback");
        }

        private void ensureMediaCodecInfosInitialized() {
            if (this.mediaCodecInfos == null) {
                this.mediaCodecInfos = new MediaCodecList(this.codecKind).getCodecInfos();
            }
        }
    }

    static {
        PASSTHROUGH_DECODER_INFO = new DecoderInfo("OMX.google.raw.decoder", null);
        decoderInfosCache = new HashMap();
        maxH264DecodableFrameSize = -1;
    }

    private MediaCodecUtil() {
    }

    public static void warmCodec(String mimeType, boolean secure) {
        try {
            getDecoderInfos(mimeType, secure);
        } catch (DecoderQueryException e) {
            Log.e(TAG, "Codec warming failed", e);
        }
    }

    public static DecoderInfo getPassthroughDecoderInfo() {
        return PASSTHROUGH_DECODER_INFO;
    }

    public static DecoderInfo getDecoderInfo(String mimeType, boolean secure) throws DecoderQueryException {
        List<DecoderInfo> decoderInfos = getDecoderInfos(mimeType, secure);
        return decoderInfos.isEmpty() ? null : (DecoderInfo) decoderInfos.get(0);
    }

    public static synchronized List<DecoderInfo> getDecoderInfos(String mimeType, boolean secure) throws DecoderQueryException {
        List<DecoderInfo> decoderInfos;
        synchronized (MediaCodecUtil.class) {
            CodecKey key = new CodecKey(mimeType, secure);
            List<DecoderInfo> decoderInfos2 = (List) decoderInfosCache.get(key);
            if (decoderInfos2 != null) {
                decoderInfos = decoderInfos2;
            } else {
                MediaCodecListCompat mediaCodecList;
                if (Util.SDK_INT >= 21) {
                    mediaCodecList = new MediaCodecListCompatV21(secure);
                } else {
                    mediaCodecList = new MediaCodecListCompatV16();
                }
                decoderInfos2 = getDecoderInfosInternal(key, mediaCodecList);
                if (secure && decoderInfos2.isEmpty() && 21 <= Util.SDK_INT && Util.SDK_INT <= 23) {
                    decoderInfos2 = getDecoderInfosInternal(key, new MediaCodecListCompatV16());
                    if (!decoderInfos2.isEmpty()) {
                        Log.w(TAG, "MediaCodecList API didn't list secure decoder for: " + mimeType + ". Assuming: " + ((DecoderInfo) decoderInfos2.get(0)).name);
                    }
                }
                decoderInfos2 = Collections.unmodifiableList(decoderInfos2);
                decoderInfosCache.put(key, decoderInfos2);
                decoderInfos = decoderInfos2;
            }
        }
        return decoderInfos;
    }

    private static List<DecoderInfo> getDecoderInfosInternal(CodecKey key, MediaCodecListCompat mediaCodecList) throws DecoderQueryException {
        String codecName;
        try {
            List<DecoderInfo> decoderInfos = new ArrayList();
            String mimeType = key.mimeType;
            int numberOfCodecs = mediaCodecList.getCodecCount();
            boolean secureDecodersExplicit = mediaCodecList.secureDecodersExplicit();
            loop0:
            for (int i = 0; i < numberOfCodecs; i++) {
                MediaCodecInfo codecInfo = mediaCodecList.getCodecInfoAt(i);
                codecName = codecInfo.getName();
                if (isCodecUsableDecoder(codecInfo, codecName, secureDecodersExplicit)) {
                    for (String supportedType : codecInfo.getSupportedTypes()) {
                        if (supportedType.equalsIgnoreCase(mimeType)) {
                            CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(supportedType);
                            boolean secure = mediaCodecList.isSecurePlaybackSupported(mimeType, capabilities);
                            if ((!secureDecodersExplicit || key.secure != secure) && (secureDecodersExplicit || key.secure)) {
                                if (!secureDecodersExplicit && secure) {
                                    decoderInfos.add(new DecoderInfo(codecName + ".secure", capabilities));
                                    break loop0;
                                }
                            }
                            decoderInfos.add(new DecoderInfo(codecName, capabilities));
                        }
                    }
                    continue;
                }
            }
            return decoderInfos;
        } catch (Exception e) {
            if (Util.SDK_INT > 23 || decoderInfos.isEmpty()) {
                throw e;
            } else {
                Log.e(TAG, "Skipping codec " + codecName + " (failed to query capabilities)");
            }
        } catch (Exception e2) {
            throw new DecoderQueryException(null);
        }
    }

    private static boolean isCodecUsableDecoder(MediaCodecInfo info, String name, boolean secureDecodersExplicit) {
        if (info.isEncoder()) {
            return false;
        }
        if (!secureDecodersExplicit && name.endsWith(".secure")) {
            return false;
        }
        if ((Util.SDK_INT < 21 && "CIPAACDecoder".equals(name)) || "CIPMP3Decoder".equals(name) || "CIPVorbisDecoder".equals(name) || "AACDecoder".equals(name) || "MP3Decoder".equals(name)) {
            return false;
        }
        if (Util.SDK_INT < 18 && "OMX.SEC.MP3.Decoder".equals(name)) {
            return false;
        }
        if (Util.SDK_INT < 18 && "OMX.MTK.AUDIO.DECODER.AAC".equals(name) && "a70".equals(Util.DEVICE)) {
            return false;
        }
        if (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.mp3".equals(name) && ("dlxu".equals(Util.DEVICE) || "protou".equals(Util.DEVICE) || "C6602".equals(Util.DEVICE) || "C6603".equals(Util.DEVICE) || "C6606".equals(Util.DEVICE) || "C6616".equals(Util.DEVICE) || "L36h".equals(Util.DEVICE) || "SO-02E".equals(Util.DEVICE))) {
            return false;
        }
        if (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.aac".equals(name) && ("C1504".equals(Util.DEVICE) || "C1505".equals(Util.DEVICE) || "C1604".equals(Util.DEVICE) || "C1605".equals(Util.DEVICE))) {
            return false;
        }
        if (Util.SDK_INT > 19 || Util.DEVICE == null || ((!Util.DEVICE.startsWith("d2") && !Util.DEVICE.startsWith("serrano")) || !"samsung".equals(Util.MANUFACTURER) || !name.equals("OMX.SEC.vp8.dec"))) {
            return true;
        }
        return false;
    }

    @TargetApi(21)
    public static boolean isSizeSupportedV21(String mimeType, boolean secure, int width, int height) throws DecoderQueryException {
        boolean z;
        if (Util.SDK_INT >= 21) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        VideoCapabilities videoCapabilities = getVideoCapabilitiesV21(mimeType, secure);
        return videoCapabilities != null && videoCapabilities.isSizeSupported(width, height);
    }

    @TargetApi(21)
    public static boolean isSizeAndRateSupportedV21(String mimeType, boolean secure, int width, int height, double frameRate) throws DecoderQueryException {
        boolean z;
        if (Util.SDK_INT >= 21) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        VideoCapabilities videoCapabilities = getVideoCapabilitiesV21(mimeType, secure);
        return videoCapabilities != null && videoCapabilities.areSizeAndRateSupported(width, height, frameRate);
    }

    @Deprecated
    public static boolean isH264ProfileSupported(int profile, int level) throws DecoderQueryException {
        DecoderInfo decoderInfo = getDecoderInfo(MediaController.MIME_TYPE, false);
        if (decoderInfo == null) {
            return false;
        }
        for (CodecProfileLevel profileLevel : decoderInfo.capabilities.profileLevels) {
            if (profileLevel.profile == profile && profileLevel.level >= level) {
                return true;
            }
        }
        return false;
    }

    public static int maxH264DecodableFrameSize() throws DecoderQueryException {
        if (maxH264DecodableFrameSize == -1) {
            int result = 0;
            DecoderInfo decoderInfo = getDecoderInfo(MediaController.MIME_TYPE, false);
            if (decoderInfo != null) {
                for (CodecProfileLevel profileLevel : decoderInfo.capabilities.profileLevels) {
                    result = Math.max(avcLevelToMaxFrameSize(profileLevel.level), result);
                }
                result = Math.max(result, 172800);
            }
            maxH264DecodableFrameSize = result;
        }
        return maxH264DecodableFrameSize;
    }

    @TargetApi(21)
    private static VideoCapabilities getVideoCapabilitiesV21(String mimeType, boolean secure) throws DecoderQueryException {
        DecoderInfo decoderInfo = getDecoderInfo(mimeType, secure);
        return decoderInfo == null ? null : decoderInfo.capabilities.getVideoCapabilities();
    }

    private static int avcLevelToMaxFrameSize(int avcLevel) {
        switch (avcLevel) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return 25344;
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                return 101376;
            case ItemTouchHelper.START /*16*/:
                return 101376;
            case ItemTouchHelper.END /*32*/:
                return 101376;
            case MessagesController.UPDATE_MASK_USER_PRINT /*64*/:
                return 202752;
            case MessagesController.UPDATE_MASK_USER_PHONE /*128*/:
                return 414720;
            case MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE /*256*/:
                return 414720;
            case MessagesController.UPDATE_MASK_SELECT_DIALOG /*512*/:
                return 921600;
            case MessagesController.UPDATE_MASK_PHONE /*1024*/:
                return 1310720;
            case MessagesController.UPDATE_MASK_NEW_MESSAGE /*2048*/:
                return AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
            case MessagesController.UPDATE_MASK_SEND_STATE /*4096*/:
                return AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
            case MessagesController.UPDATE_MASK_CHANNEL /*8192*/:
                return 2228224;
            case MessagesController.UPDATE_MASK_CHAT_ADMINS /*16384*/:
                return 5652480;
            case TLRPC.MESSAGE_FLAG_EDITED /*32768*/:
                return 9437184;
            default:
                return -1;
        }
    }
}
