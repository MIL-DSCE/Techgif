package org.telegram.tgnet;

public class TLObject {
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator;
    public boolean disableFree;

    /* renamed from: org.telegram.tgnet.TLObject.1 */
    static class C08841 extends ThreadLocal<NativeByteBuffer> {
        C08841() {
        }

        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    }

    static {
        sizeCalculator = new C08841();
    }

    public TLObject() {
        this.disableFree = false;
    }

    public void readParams(AbstractSerializedData stream, boolean exception) {
    }

    public void serializeToStream(AbstractSerializedData stream) {
    }

    public TLObject deserializeResponse(AbstractSerializedData stream, int constructor, boolean exception) {
        return null;
    }

    public void freeResources() {
    }

    public int getObjectSize() {
        NativeByteBuffer byteBuffer = (NativeByteBuffer) sizeCalculator.get();
        byteBuffer.rewind();
        serializeToStream((AbstractSerializedData) sizeCalculator.get());
        return byteBuffer.length();
    }
}
