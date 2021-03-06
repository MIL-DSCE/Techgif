package net.hockeyapp.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import org.telegram.messenger.MessagesController;

public class SimpleMultipartEntity {
    private static final char[] BOUNDARY_CHARS;
    private String mBoundary;
    private boolean mIsSetFirst;
    private boolean mIsSetLast;
    private ByteArrayOutputStream mOut;

    static {
        BOUNDARY_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }

    public SimpleMultipartEntity() {
        this.mIsSetFirst = false;
        this.mIsSetLast = false;
        this.mOut = new ByteArrayOutputStream();
        StringBuffer buffer = new StringBuffer();
        Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buffer.append(BOUNDARY_CHARS[rand.nextInt(BOUNDARY_CHARS.length)]);
        }
        this.mBoundary = buffer.toString();
    }

    public String getBoundary() {
        return this.mBoundary;
    }

    public void writeFirstBoundaryIfNeeds() throws IOException {
        if (!this.mIsSetFirst) {
            this.mOut.write(("--" + this.mBoundary + "\r\n").getBytes());
        }
        this.mIsSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (!this.mIsSetLast) {
            try {
                this.mOut.write(("\r\n--" + this.mBoundary + "--\r\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mIsSetLast = true;
        }
    }

    public void addPart(String key, String value) throws IOException {
        writeFirstBoundaryIfNeeds();
        this.mOut.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
        this.mOut.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
        this.mOut.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
        this.mOut.write(value.getBytes());
        this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
    }

    public void addPart(String key, File value, boolean lastFile) throws IOException {
        addPart(key, value.getName(), new FileInputStream(value), lastFile);
    }

    public void addPart(String key, String fileName, InputStream fin, boolean lastFile) throws IOException {
        addPart(key, fileName, fin, "application/octet-stream", lastFile);
    }

    public void addPart(String key, String fileName, InputStream fin, String type, boolean lastFile) throws IOException {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            this.mOut.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            this.mOut.write(type.getBytes());
            this.mOut.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
            byte[] tmp = new byte[MessagesController.UPDATE_MASK_SEND_STATE];
            while (true) {
                int l = fin.read(tmp);
                if (l == -1) {
                    break;
                }
                this.mOut.write(tmp, 0, l);
            }
            this.mOut.flush();
            if (lastFile) {
                writeLastBoundaryIfNeeds();
            } else {
                this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
            }
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return (long) this.mOut.toByteArray().length;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + getBoundary();
    }

    public ByteArrayOutputStream getOutputStream() {
        writeLastBoundaryIfNeeds();
        return this.mOut;
    }
}
