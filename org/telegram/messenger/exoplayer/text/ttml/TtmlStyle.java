package org.telegram.messenger.exoplayer.text.ttml;

import android.text.Layout.Alignment;
import org.telegram.messenger.exoplayer.util.Assertions;

final class TtmlStyle {
    public static final int FONT_SIZE_UNIT_EM = 2;
    public static final int FONT_SIZE_UNIT_PERCENT = 3;
    public static final int FONT_SIZE_UNIT_PIXEL = 1;
    private static final int OFF = 0;
    private static final int ON = 1;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_BOLD_ITALIC = 3;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_NORMAL = 0;
    public static final int UNSPECIFIED = -1;
    private int backgroundColor;
    private int bold;
    private int fontColor;
    private String fontFamily;
    private float fontSize;
    private int fontSizeUnit;
    private boolean hasBackgroundColor;
    private boolean hasFontColor;
    private String id;
    private TtmlStyle inheritableStyle;
    private int italic;
    private int linethrough;
    private Alignment textAlign;
    private int underline;

    public TtmlStyle() {
        this.linethrough = UNSPECIFIED;
        this.underline = UNSPECIFIED;
        this.bold = UNSPECIFIED;
        this.italic = UNSPECIFIED;
        this.fontSizeUnit = UNSPECIFIED;
    }

    public int getStyle() {
        int i = STYLE_NORMAL;
        if (this.bold == UNSPECIFIED && this.italic == UNSPECIFIED) {
            return UNSPECIFIED;
        }
        int i2 = this.bold != UNSPECIFIED ? this.bold : STYLE_NORMAL;
        if (this.italic != UNSPECIFIED) {
            i = this.italic;
        }
        return i2 | i;
    }

    public boolean isLinethrough() {
        return this.linethrough == STYLE_BOLD;
    }

    public TtmlStyle setLinethrough(boolean linethrough) {
        boolean z;
        int i = STYLE_BOLD;
        if (this.inheritableStyle == null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (!linethrough) {
            i = STYLE_NORMAL;
        }
        this.linethrough = i;
        return this;
    }

    public boolean isUnderline() {
        return this.underline == STYLE_BOLD;
    }

    public TtmlStyle setUnderline(boolean underline) {
        boolean z;
        int i = STYLE_BOLD;
        if (this.inheritableStyle == null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (!underline) {
            i = STYLE_NORMAL;
        }
        this.underline = i;
        return this;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public TtmlStyle setFontFamily(String fontFamily) {
        Assertions.checkState(this.inheritableStyle == null);
        this.fontFamily = fontFamily;
        return this;
    }

    public int getFontColor() {
        if (this.hasFontColor) {
            return this.fontColor;
        }
        throw new IllegalStateException("Font color has not been defined.");
    }

    public TtmlStyle setFontColor(int fontColor) {
        Assertions.checkState(this.inheritableStyle == null);
        this.fontColor = fontColor;
        this.hasFontColor = true;
        return this;
    }

    public boolean hasFontColor() {
        return this.hasFontColor;
    }

    public int getBackgroundColor() {
        if (this.hasBackgroundColor) {
            return this.backgroundColor;
        }
        throw new IllegalStateException("Background color has not been defined.");
    }

    public TtmlStyle setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.hasBackgroundColor = true;
        return this;
    }

    public boolean hasBackgroundColor() {
        return this.hasBackgroundColor;
    }

    public TtmlStyle setBold(boolean isBold) {
        boolean z;
        int i = STYLE_BOLD;
        if (this.inheritableStyle == null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (!isBold) {
            i = STYLE_NORMAL;
        }
        this.bold = i;
        return this;
    }

    public TtmlStyle setItalic(boolean isItalic) {
        boolean z;
        int i = STYLE_NORMAL;
        if (this.inheritableStyle == null) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        if (isItalic) {
            i = STYLE_ITALIC;
        }
        this.italic = i;
        return this;
    }

    public TtmlStyle inherit(TtmlStyle ancestor) {
        return inherit(ancestor, false);
    }

    public TtmlStyle chain(TtmlStyle ancestor) {
        return inherit(ancestor, true);
    }

    private TtmlStyle inherit(TtmlStyle ancestor, boolean chaining) {
        if (ancestor != null) {
            if (!this.hasFontColor && ancestor.hasFontColor) {
                setFontColor(ancestor.fontColor);
            }
            if (this.bold == UNSPECIFIED) {
                this.bold = ancestor.bold;
            }
            if (this.italic == UNSPECIFIED) {
                this.italic = ancestor.italic;
            }
            if (this.fontFamily == null) {
                this.fontFamily = ancestor.fontFamily;
            }
            if (this.linethrough == UNSPECIFIED) {
                this.linethrough = ancestor.linethrough;
            }
            if (this.underline == UNSPECIFIED) {
                this.underline = ancestor.underline;
            }
            if (this.textAlign == null) {
                this.textAlign = ancestor.textAlign;
            }
            if (this.fontSizeUnit == UNSPECIFIED) {
                this.fontSizeUnit = ancestor.fontSizeUnit;
                this.fontSize = ancestor.fontSize;
            }
            if (chaining && !this.hasBackgroundColor && ancestor.hasBackgroundColor) {
                setBackgroundColor(ancestor.backgroundColor);
            }
        }
        return this;
    }

    public TtmlStyle setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public Alignment getTextAlign() {
        return this.textAlign;
    }

    public TtmlStyle setTextAlign(Alignment textAlign) {
        this.textAlign = textAlign;
        return this;
    }

    public TtmlStyle setFontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public TtmlStyle setFontSizeUnit(int fontSizeUnit) {
        this.fontSizeUnit = fontSizeUnit;
        return this;
    }

    public int getFontSizeUnit() {
        return this.fontSizeUnit;
    }

    public float getFontSize() {
        return this.fontSize;
    }
}
