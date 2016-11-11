package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import android.support.v4.media.TransportMediator;
import java.nio.ByteBuffer;
import org.telegram.messenger.MessagesController;

public class VisualRandomAccessEntry extends GroupEntry {
    public static final String TYPE = "rap ";
    private short numLeadingSamples;
    private boolean numLeadingSamplesKnown;

    public String getType() {
        return TYPE;
    }

    public boolean isNumLeadingSamplesKnown() {
        return this.numLeadingSamplesKnown;
    }

    public void setNumLeadingSamplesKnown(boolean numLeadingSamplesKnown) {
        this.numLeadingSamplesKnown = numLeadingSamplesKnown;
    }

    public short getNumLeadingSamples() {
        return this.numLeadingSamples;
    }

    public void setNumLeadingSamples(short numLeadingSamples) {
        this.numLeadingSamples = numLeadingSamples;
    }

    public void parse(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        this.numLeadingSamplesKnown = (b & MessagesController.UPDATE_MASK_USER_PHONE) == MessagesController.UPDATE_MASK_USER_PHONE;
        this.numLeadingSamples = (short) (b & TransportMediator.KEYCODE_MEDIA_PAUSE);
    }

    public ByteBuffer get() {
        ByteBuffer content = ByteBuffer.allocate(1);
        content.put((byte) ((this.numLeadingSamplesKnown ? MessagesController.UPDATE_MASK_USER_PHONE : 0) | (this.numLeadingSamples & TransportMediator.KEYCODE_MEDIA_PAUSE)));
        content.rewind();
        return content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VisualRandomAccessEntry that = (VisualRandomAccessEntry) o;
        if (this.numLeadingSamples != that.numLeadingSamples) {
            return false;
        }
        if (this.numLeadingSamplesKnown != that.numLeadingSamplesKnown) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((this.numLeadingSamplesKnown ? 1 : 0) * 31) + this.numLeadingSamples;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VisualRandomAccessEntry");
        sb.append("{numLeadingSamplesKnown=").append(this.numLeadingSamplesKnown);
        sb.append(", numLeadingSamples=").append(this.numLeadingSamples);
        sb.append('}');
        return sb.toString();
    }
}
