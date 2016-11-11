package org.telegram.messenger.exoplayer.extractor.webm;

import java.io.EOFException;
import java.io.IOException;
import java.util.Stack;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.ui.Components.VideoPlayer;

final class DefaultEbmlReader implements EbmlReader {
    private static final int ELEMENT_STATE_READ_CONTENT = 2;
    private static final int ELEMENT_STATE_READ_CONTENT_SIZE = 1;
    private static final int ELEMENT_STATE_READ_ID = 0;
    private static final int MAX_ID_BYTES = 4;
    private static final int MAX_INTEGER_ELEMENT_SIZE_BYTES = 8;
    private static final int MAX_LENGTH_BYTES = 8;
    private static final int VALID_FLOAT32_ELEMENT_SIZE_BYTES = 4;
    private static final int VALID_FLOAT64_ELEMENT_SIZE_BYTES = 8;
    private long elementContentSize;
    private int elementId;
    private int elementState;
    private final Stack<MasterElement> masterElementsStack;
    private EbmlReaderOutput output;
    private final byte[] scratch;
    private final VarintReader varintReader;

    private static final class MasterElement {
        private final long elementEndPosition;
        private final int elementId;

        private MasterElement(int elementId, long elementEndPosition) {
            this.elementId = elementId;
            this.elementEndPosition = elementEndPosition;
        }
    }

    DefaultEbmlReader() {
        this.scratch = new byte[VALID_FLOAT64_ELEMENT_SIZE_BYTES];
        this.masterElementsStack = new Stack();
        this.varintReader = new VarintReader();
    }

    public void init(EbmlReaderOutput eventHandler) {
        this.output = eventHandler;
    }

    public void reset() {
        this.elementState = ELEMENT_STATE_READ_ID;
        this.masterElementsStack.clear();
        this.varintReader.reset();
    }

    public boolean read(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkState(this.output != null);
        while (true) {
            if (this.masterElementsStack.isEmpty() || input.getPosition() < ((MasterElement) this.masterElementsStack.peek()).elementEndPosition) {
                if (this.elementState == 0) {
                    long result = this.varintReader.readUnsignedVarint(input, true, false, VALID_FLOAT32_ELEMENT_SIZE_BYTES);
                    if (result == -2) {
                        result = maybeResyncToNextLevel1Element(input);
                    }
                    if (result == -1) {
                        return false;
                    }
                    this.elementId = (int) result;
                    this.elementState = ELEMENT_STATE_READ_CONTENT_SIZE;
                }
                if (this.elementState == ELEMENT_STATE_READ_CONTENT_SIZE) {
                    this.elementContentSize = this.varintReader.readUnsignedVarint(input, false, true, VALID_FLOAT64_ELEMENT_SIZE_BYTES);
                    this.elementState = ELEMENT_STATE_READ_CONTENT;
                }
                int type = this.output.getElementType(this.elementId);
                switch (type) {
                    case ELEMENT_STATE_READ_ID /*0*/:
                        input.skipFully((int) this.elementContentSize);
                        this.elementState = ELEMENT_STATE_READ_ID;
                    case ELEMENT_STATE_READ_CONTENT_SIZE /*1*/:
                        long elementContentPosition = input.getPosition();
                        this.masterElementsStack.add(new MasterElement(elementContentPosition + this.elementContentSize, null));
                        this.output.startMasterElement(this.elementId, elementContentPosition, this.elementContentSize);
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case ELEMENT_STATE_READ_CONTENT /*2*/:
                        if (this.elementContentSize > 8) {
                            throw new ParserException("Invalid integer size: " + this.elementContentSize);
                        }
                        this.output.integerElement(this.elementId, readInteger(input, (int) this.elementContentSize));
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        if (this.elementContentSize > 2147483647L) {
                            throw new ParserException("String element size: " + this.elementContentSize);
                        }
                        this.output.stringElement(this.elementId, readString(input, (int) this.elementContentSize));
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case VALID_FLOAT32_ELEMENT_SIZE_BYTES /*4*/:
                        this.output.binaryElement(this.elementId, (int) this.elementContentSize, input);
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case VideoPlayer.STATE_ENDED /*5*/:
                        if (this.elementContentSize == 4 || this.elementContentSize == 8) {
                            this.output.floatElement(this.elementId, readFloat(input, (int) this.elementContentSize));
                            this.elementState = ELEMENT_STATE_READ_ID;
                            return true;
                        }
                        throw new ParserException("Invalid float size: " + this.elementContentSize);
                    default:
                        throw new ParserException("Invalid element type " + type);
                }
            }
            this.output.endMasterElement(((MasterElement) this.masterElementsStack.pop()).elementId);
            return true;
        }
    }

    private long maybeResyncToNextLevel1Element(ExtractorInput input) throws EOFException, IOException, InterruptedException {
        input.resetPeekPosition();
        while (true) {
            input.peekFully(this.scratch, ELEMENT_STATE_READ_ID, VALID_FLOAT32_ELEMENT_SIZE_BYTES);
            int varintLength = VarintReader.parseUnsignedVarintLength(this.scratch[ELEMENT_STATE_READ_ID]);
            if (varintLength != -1 && varintLength <= VALID_FLOAT32_ELEMENT_SIZE_BYTES) {
                int potentialId = (int) VarintReader.assembleVarint(this.scratch, varintLength, false);
                if (this.output.isLevel1Element(potentialId)) {
                    input.skipFully(varintLength);
                    return (long) potentialId;
                }
            }
            input.skipFully(ELEMENT_STATE_READ_CONTENT_SIZE);
        }
    }

    private long readInteger(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        input.readFully(this.scratch, ELEMENT_STATE_READ_ID, byteLength);
        long value = 0;
        for (int i = ELEMENT_STATE_READ_ID; i < byteLength; i += ELEMENT_STATE_READ_CONTENT_SIZE) {
            value = (value << VALID_FLOAT64_ELEMENT_SIZE_BYTES) | ((long) (this.scratch[i] & NalUnitUtil.EXTENDED_SAR));
        }
        return value;
    }

    private double readFloat(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        long integerValue = readInteger(input, byteLength);
        if (byteLength == VALID_FLOAT32_ELEMENT_SIZE_BYTES) {
            return (double) Float.intBitsToFloat((int) integerValue);
        }
        return Double.longBitsToDouble(integerValue);
    }

    private String readString(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        if (byteLength == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        byte[] stringBytes = new byte[byteLength];
        input.readFully(stringBytes, ELEMENT_STATE_READ_ID, byteLength);
        return new String(stringBytes);
    }
}
