package org.telegram.messenger.exoplayer.text.eia608;

final class ClosedCaptionCtrl extends ClosedCaption {
    public static final byte BACKSPACE = (byte) 33;
    public static final byte CARRIAGE_RETURN = (byte) 45;
    public static final byte END_OF_CAPTION = (byte) 47;
    public static final byte ERASE_DISPLAYED_MEMORY = (byte) 44;
    public static final byte ERASE_NON_DISPLAYED_MEMORY = (byte) 46;
    public static final byte MID_ROW_CHAN_1 = (byte) 17;
    public static final byte MID_ROW_CHAN_2 = (byte) 25;
    public static final byte MISC_CHAN_1 = (byte) 20;
    public static final byte MISC_CHAN_2 = (byte) 28;
    public static final byte RESUME_CAPTION_LOADING = (byte) 32;
    public static final byte RESUME_DIRECT_CAPTIONING = (byte) 41;
    public static final byte ROLL_UP_CAPTIONS_2_ROWS = (byte) 37;
    public static final byte ROLL_UP_CAPTIONS_3_ROWS = (byte) 38;
    public static final byte ROLL_UP_CAPTIONS_4_ROWS = (byte) 39;
    public static final byte TAB_OFFSET_CHAN_1 = (byte) 23;
    public static final byte TAB_OFFSET_CHAN_2 = (byte) 31;
    public final byte cc1;
    public final byte cc2;

    protected ClosedCaptionCtrl(byte cc1, byte cc2) {
        super(0);
        this.cc1 = cc1;
        this.cc2 = cc2;
    }

    public boolean isMidRowCode() {
        return (this.cc1 == 17 || this.cc1 == 25) && this.cc2 >= 32 && this.cc2 <= 47;
    }

    public boolean isMiscCode() {
        return (this.cc1 == 20 || this.cc1 == 28) && this.cc2 >= 32 && this.cc2 <= 47;
    }

    public boolean isTabOffsetCode() {
        return (this.cc1 == 23 || this.cc1 == 31) && this.cc2 >= 33 && this.cc2 <= 35;
    }

    public boolean isPreambleAddressCode() {
        return this.cc1 >= 16 && this.cc1 <= 31 && this.cc2 >= 64 && this.cc2 <= 127;
    }

    public boolean isRepeatable() {
        return this.cc1 >= 16 && this.cc1 <= 31;
    }
}
