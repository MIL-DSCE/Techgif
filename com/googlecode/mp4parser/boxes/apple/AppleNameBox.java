package com.googlecode.mp4parser.boxes.apple;

public class AppleNameBox extends Utf8AppleDataBox {
    public static final String TYPE = "\u00a9nam";

    public AppleNameBox() {
        super(TYPE);
    }
}
