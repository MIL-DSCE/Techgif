package com.googlecode.mp4parser.h264.model;

import org.telegram.messenger.exoplayer.util.NalUnitUtil;

public class AspectRatio {
    public static final AspectRatio Extended_SAR;
    private int value;

    static {
        Extended_SAR = new AspectRatio(NalUnitUtil.EXTENDED_SAR);
    }

    private AspectRatio(int value) {
        this.value = value;
    }

    public static AspectRatio fromValue(int value) {
        if (value == Extended_SAR.value) {
            return Extended_SAR;
        }
        return new AspectRatio(value);
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("AspectRatio{");
        sb.append("value=").append(this.value);
        sb.append('}');
        return sb.toString();
    }
}
