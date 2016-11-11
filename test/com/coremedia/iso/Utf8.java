package com.coremedia.iso;

import java.io.UnsupportedEncodingException;
import org.telegram.messenger.exoplayer.C0747C;

public final class Utf8 {
    public static byte[] convert(String s) {
        if (s == null) {
            return null;
        }
        try {
            return s.getBytes(C0747C.UTF8_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    public static String convert(byte[] b) {
        if (b == null) {
            return null;
        }
        try {
            return new String(b, C0747C.UTF8_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    public static int utf8StringLengthInBytes(String utf8) {
        if (utf8 == null) {
            return 0;
        }
        try {
            return utf8.getBytes(C0747C.UTF8_NAME).length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }
}
