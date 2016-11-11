package com.appsgeyser.sdk.hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;

public class Hasher {
    public static final String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(b & NalUnitUtil.EXTENDED_SAR);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }
}
