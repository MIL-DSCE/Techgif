package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class AppleDiskNumberBox extends AppleDataBox {
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    int f47a;
    short f48b;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AppleDiskNumberBox.java", AppleDiskNumberBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getA", "com.googlecode.mp4parser.boxes.apple.AppleDiskNumberBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 16);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setA", "com.googlecode.mp4parser.boxes.apple.AppleDiskNumberBox", "int", "a", TtmlNode.ANONYMOUS_REGION_ID, "void"), 20);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "getB", "com.googlecode.mp4parser.boxes.apple.AppleDiskNumberBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "short"), 24);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig("1", "setB", "com.googlecode.mp4parser.boxes.apple.AppleDiskNumberBox", "short", "b", TtmlNode.ANONYMOUS_REGION_ID, "void"), 28);
    }

    public AppleDiskNumberBox() {
        super("disk", 0);
    }

    public int getA() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.f47a;
    }

    public void setA(int a) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.intObject(a)));
        this.f47a = a;
    }

    public short getB() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.f48b;
    }

    public void setB(short b) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, Conversions.shortObject(b)));
        this.f48b = b;
    }

    protected byte[] writeData() {
        ByteBuffer bb = ByteBuffer.allocate(6);
        bb.putInt(this.f47a);
        bb.putShort(this.f48b);
        return bb.array();
    }

    protected void parseData(ByteBuffer data) {
        this.f47a = data.getInt();
        this.f48b = data.getShort();
    }

    protected int getDataLength() {
        return 6;
    }
}
