package com.coremedia.iso;

import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public final class IsoTypeReaderVariable {
    public static long read(ByteBuffer bb, int bytes) {
        switch (bytes) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return (long) IsoTypeReader.readUInt8(bb);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return (long) IsoTypeReader.readUInt16(bb);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return (long) IsoTypeReader.readUInt24(bb);
            case VideoPlayer.STATE_READY /*4*/:
                return IsoTypeReader.readUInt32(bb);
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                return IsoTypeReader.readUInt64(bb);
            default:
                throw new RuntimeException("I don't know how to read " + bytes + " bytes");
        }
    }
}
