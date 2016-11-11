package org.telegram.messenger.audioinfo.mp3;

import android.support.v4.media.TransportMediator;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;

public class ID3v1Info extends AudioInfo {
    public static boolean isID3v1StartPosition(InputStream input) throws IOException {
        input.mark(3);
        try {
            boolean z = input.read() == 84 && input.read() == 65 && input.read() == 71;
            input.reset();
            return z;
        } catch (Throwable th) {
            input.reset();
        }
    }

    public ID3v1Info(InputStream input) throws IOException {
        if (isID3v1StartPosition(input)) {
            this.brand = "ID3";
            this.version = "1.0";
            byte[] bytes = readBytes(input, MessagesController.UPDATE_MASK_USER_PHONE);
            this.title = extractString(bytes, 3, 30);
            this.artist = extractString(bytes, 33, 30);
            this.album = extractString(bytes, 63, 30);
            try {
                this.year = Short.parseShort(extractString(bytes, 93, 4));
            } catch (NumberFormatException e) {
                this.year = (short) 0;
            }
            this.comment = extractString(bytes, 97, 30);
            ID3v1Genre id3v1Genre = ID3v1Genre.getGenre(bytes[TransportMediator.KEYCODE_MEDIA_PAUSE]);
            if (id3v1Genre != null) {
                this.genre = id3v1Genre.getDescription();
            }
            if (bytes[125] == null && bytes[TransportMediator.KEYCODE_MEDIA_PLAY] != null) {
                this.version = "1.1";
                this.track = (short) (bytes[TransportMediator.KEYCODE_MEDIA_PLAY] & NalUnitUtil.EXTENDED_SAR);
            }
        }
    }

    byte[] readBytes(InputStream input, int len) throws IOException {
        int total = 0;
        byte[] bytes = new byte[len];
        while (total < len) {
            int current = input.read(bytes, total, len - total);
            if (current > 0) {
                total += current;
            } else {
                throw new EOFException();
            }
        }
        return bytes;
    }

    String extractString(byte[] bytes, int offset, int length) {
        try {
            String text = new String(bytes, offset, length, "ISO-8859-1");
            int zeroIndex = text.indexOf(0);
            if (zeroIndex < 0) {
                return text;
            }
            return text.substring(0, zeroIndex);
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }
}
