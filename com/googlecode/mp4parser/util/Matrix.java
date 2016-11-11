package com.googlecode.mp4parser.util;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class Matrix {
    public static final Matrix ROTATE_0;
    public static final Matrix ROTATE_180;
    public static final Matrix ROTATE_270;
    public static final Matrix ROTATE_90;
    double f3a;
    double f4b;
    double f5c;
    double f6d;
    double tx;
    double ty;
    double f7u;
    double f8v;
    double f9w;

    public Matrix(double a, double b, double c, double d, double u, double v, double w, double tx, double ty) {
        this.f7u = u;
        this.f8v = v;
        this.f9w = w;
        this.f3a = a;
        this.f4b = b;
        this.f5c = c;
        this.f6d = d;
        this.tx = tx;
        this.ty = ty;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Matrix matrix = (Matrix) o;
        if (Double.compare(matrix.f3a, this.f3a) != 0) {
            return false;
        }
        if (Double.compare(matrix.f4b, this.f4b) != 0) {
            return false;
        }
        if (Double.compare(matrix.f5c, this.f5c) != 0) {
            return false;
        }
        if (Double.compare(matrix.f6d, this.f6d) != 0) {
            return false;
        }
        if (Double.compare(matrix.tx, this.tx) != 0) {
            return false;
        }
        if (Double.compare(matrix.ty, this.ty) != 0) {
            return false;
        }
        if (Double.compare(matrix.f7u, this.f7u) != 0) {
            return false;
        }
        if (Double.compare(matrix.f8v, this.f8v) != 0) {
            return false;
        }
        if (Double.compare(matrix.f9w, this.f9w) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.f7u);
        int result = (int) ((temp >>> 32) ^ temp);
        temp = Double.doubleToLongBits(this.f8v);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.f9w);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.f3a);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.f4b);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.f5c);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.f6d);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.tx);
        result = (result * 31) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.ty);
        return (result * 31) + ((int) ((temp >>> 32) ^ temp));
    }

    public String toString() {
        if (equals(ROTATE_0)) {
            return "Rotate 0\u00b0";
        }
        if (equals(ROTATE_90)) {
            return "Rotate 90\u00b0";
        }
        if (equals(ROTATE_180)) {
            return "Rotate 180\u00b0";
        }
        if (equals(ROTATE_270)) {
            return "Rotate 270\u00b0";
        }
        return "Matrix{u=" + this.f7u + ", v=" + this.f8v + ", w=" + this.f9w + ", a=" + this.f3a + ", b=" + this.f4b + ", c=" + this.f5c + ", d=" + this.f6d + ", tx=" + this.tx + ", ty=" + this.ty + '}';
    }

    static {
        ROTATE_0 = new Matrix(1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
        ROTATE_90 = new Matrix(0.0d, 1.0d, -1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
        ROTATE_180 = new Matrix(-1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
        ROTATE_270 = new Matrix(0.0d, -1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
    }

    public static Matrix fromFileOrder(double a, double b, double u, double c, double d, double v, double tx, double ty, double w) {
        return new Matrix(a, b, c, d, u, v, w, tx, ty);
    }

    public static Matrix fromByteBuffer(ByteBuffer byteBuffer) {
        return fromFileOrder(IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer));
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f3a);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f4b);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.f7u);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f5c);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f6d);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.f8v);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.tx);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.ty);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.f9w);
    }
}
