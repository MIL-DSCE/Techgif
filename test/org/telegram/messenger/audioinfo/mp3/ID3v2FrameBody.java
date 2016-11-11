package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.audioinfo.util.RangeInputStream;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer;
    private final ID3v2DataInput data;
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    /* renamed from: org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody.1 */
    static class C07461 extends ThreadLocal<Buffer> {
        C07461() {
        }

        protected Buffer initialValue() {
            return new Buffer(MessagesController.UPDATE_MASK_SEND_STATE);
        }
    }

    static final class Buffer {
        byte[] bytes;

        Buffer(int initialLength) {
            this.bytes = new byte[initialLength];
        }

        byte[] bytes(int minLength) {
            if (minLength > this.bytes.length) {
                int length = this.bytes.length * 2;
                while (minLength > length) {
                    length *= 2;
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    static {
        textBuffer = new C07461();
    }

    ID3v2FrameBody(InputStream delegate, long position, int dataLength, ID3v2TagHeader tagHeader, ID3v2FrameHeader frameHeader) throws IOException {
        this.input = new RangeInputStream(delegate, position, (long) dataLength);
        this.data = new ID3v2DataInput(this.input);
        this.tagHeader = tagHeader;
        this.frameHeader = frameHeader;
    }

    public ID3v2DataInput getData() {
        return this.data;
    }

    public long getPosition() {
        return this.input.getPosition();
    }

    public long getRemainingLength() {
        return this.input.getRemainingLength();
    }

    public ID3v2TagHeader getTagHeader() {
        return this.tagHeader;
    }

    public ID3v2FrameHeader getFrameHeader() {
        return this.frameHeader;
    }

    private String extractString(byte[] bytes, int offset, int length, ID3v2Encoding encoding, boolean searchZeros) {
        if (searchZeros) {
            int zeros = 0;
            int i = 0;
            while (i < length) {
                if (bytes[offset + i] != null || (encoding == ID3v2Encoding.UTF_16 && zeros == 0 && (offset + i) % 2 != 0)) {
                    zeros = 0;
                } else {
                    zeros++;
                    if (zeros == encoding.getZeroBytes()) {
                        length = (i + 1) - encoding.getZeroBytes();
                        break;
                    }
                }
                i++;
            }
        }
        try {
            String string = new String(bytes, offset, length, encoding.getCharset().name());
            if (string.length() <= 0 || string.charAt(0) != '\ufeff') {
                return string;
            }
            return string.substring(1);
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public String readZeroTerminatedString(int maxLength, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        int zeros = 0;
        int length = Math.min(maxLength, (int) getRemainingLength());
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(length);
        int i = 0;
        while (i < length) {
            byte readByte = this.data.readByte();
            bytes[i] = readByte;
            if (readByte != null || (encoding == ID3v2Encoding.UTF_16 && zeros == 0 && i % 2 != 0)) {
                zeros = 0;
            } else {
                zeros++;
                if (zeros == encoding.getZeroBytes()) {
                    return extractString(bytes, 0, (i + 1) - encoding.getZeroBytes(), encoding, false);
                }
            }
            i++;
        }
        throw new ID3v2Exception("Could not read zero-termiated string");
    }

    public String readFixedLengthString(int length, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        if (((long) length) > getRemainingLength()) {
            throw new ID3v2Exception("Could not read fixed-length string of length: " + length);
        }
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(length);
        this.data.readFully(bytes, 0, length);
        return extractString(bytes, 0, length, encoding, true);
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte value = this.data.readByte();
        switch (value) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return ID3v2Encoding.ISO_8859_1;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return ID3v2Encoding.UTF_16;
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return ID3v2Encoding.UTF_16BE;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return ID3v2Encoding.UTF_8;
            default:
                throw new ID3v2Exception("Invalid encoding: " + value);
        }
    }

    public String toString() {
        return "id3v2frame[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
    }
}
