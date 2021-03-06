package org.telegram.messenger;

import java.util.Locale;

public class VideoEditedInfo {
    public int bitrate;
    public long endTime;
    public int originalHeight;
    public String originalPath;
    public int originalWidth;
    public int resultHeight;
    public int resultWidth;
    public int rotationValue;
    public long startTime;

    public String getString() {
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), this.originalPath});
    }

    public boolean parseString(String string) {
        if (string.length() < 6) {
            return false;
        }
        try {
            String[] args = string.split("_");
            if (args.length >= 10) {
                this.startTime = Long.parseLong(args[1]);
                this.endTime = Long.parseLong(args[2]);
                this.rotationValue = Integer.parseInt(args[3]);
                this.originalWidth = Integer.parseInt(args[4]);
                this.originalHeight = Integer.parseInt(args[5]);
                this.bitrate = Integer.parseInt(args[6]);
                this.resultWidth = Integer.parseInt(args[7]);
                this.resultHeight = Integer.parseInt(args[8]);
                for (int a = 9; a < args.length; a++) {
                    if (this.originalPath == null) {
                        this.originalPath = args[a];
                    } else {
                        this.originalPath += "_" + args[a];
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return false;
        }
    }
}
