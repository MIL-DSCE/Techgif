package com.coremedia.iso;

import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public final class IsoTypeWriterVariable {
    public static void write(long v, ByteBuffer bb, int bytes) {
        switch (bytes) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                IsoTypeWriter.writeUInt8(bb, (int) (255 & v));
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                IsoTypeWriter.writeUInt16(bb, (int) (65535 & v));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                IsoTypeWriter.writeUInt24(bb, (int) (16777215 & v));
            case VideoPlayer.STATE_READY /*4*/:
                IsoTypeWriter.writeUInt32(bb, v);
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                IsoTypeWriter.writeUInt64(bb, v);
            default:
                throw new RuntimeException("I don't know how to read " + bytes + " bytes");
        }
    }
}
