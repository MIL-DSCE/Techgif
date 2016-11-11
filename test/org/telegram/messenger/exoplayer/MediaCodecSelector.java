package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT;

    /* renamed from: org.telegram.messenger.exoplayer.MediaCodecSelector.1 */
    static class C16981 implements MediaCodecSelector {
        C16981() {
        }

        public DecoderInfo getDecoderInfo(String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
            return MediaCodecUtil.getDecoderInfo(mimeType, requiresSecureDecoder);
        }

        public DecoderInfo getPassthroughDecoderInfo() throws DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    }

    DecoderInfo getDecoderInfo(String str, boolean z) throws DecoderQueryException;

    DecoderInfo getPassthroughDecoderInfo() throws DecoderQueryException;

    static {
        DEFAULT = new C16981();
    }
}
