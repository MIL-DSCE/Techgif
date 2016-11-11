package org.telegram.messenger.exoplayer.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.ui.Components.VideoPlayer;

public final class WebvttCueParser {
    private static final char CHAR_AMPERSAND = '&';
    private static final char CHAR_GREATER_THAN = '>';
    private static final char CHAR_LESS_THAN = '<';
    private static final char CHAR_SEMI_COLON = ';';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_SPACE = ' ';
    private static final Pattern COMMENT;
    public static final Pattern CUE_HEADER_PATTERN;
    private static final Pattern CUE_SETTING_PATTERN;
    private static final String ENTITY_AMPERSAND = "amp";
    private static final String ENTITY_GREATER_THAN = "gt";
    private static final String ENTITY_LESS_THAN = "lt";
    private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
    private static final String SPACE = " ";
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;
    private static final String TAG = "WebvttCueParser";
    private static final String TAG_BOLD = "b";
    private static final String TAG_CLASS = "c";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_LANG = "lang";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_VOICE = "v";
    private final StringBuilder textBuilder;

    private static final class StartTag {
        public final String name;
        public final int position;

        public StartTag(String name, int position) {
            this.position = position;
            this.name = name;
        }
    }

    static {
        CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
        COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
        CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
    }

    public WebvttCueParser() {
        this.textBuilder = new StringBuilder();
    }

    boolean parseNextValidCue(ParsableByteArray webvttData, Builder builder) {
        Matcher cueHeaderMatcher;
        do {
            cueHeaderMatcher = findNextCueHeader(webvttData);
            if (cueHeaderMatcher == null) {
                return false;
            }
        } while (!parseCue(cueHeaderMatcher, webvttData, builder, this.textBuilder));
        return true;
    }

    static void parseCueSettingsList(String cueSettingsList, Builder builder) {
        Matcher cueSettingMatcher = CUE_SETTING_PATTERN.matcher(cueSettingsList);
        while (cueSettingMatcher.find()) {
            String name = cueSettingMatcher.group(STYLE_BOLD);
            String value = cueSettingMatcher.group(STYLE_ITALIC);
            try {
                if ("line".equals(name)) {
                    parseLineAttribute(value, builder);
                } else if ("align".equals(name)) {
                    builder.setTextAlignment(parseTextAlignment(value));
                } else if ("position".equals(name)) {
                    parsePositionAttribute(value, builder);
                } else if ("size".equals(name)) {
                    builder.setWidth(WebvttParserUtil.parsePercentage(value));
                } else {
                    Log.w(TAG, "Unknown cue setting " + name + ":" + value);
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Skipping bad cue setting: " + cueSettingMatcher.group());
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.regex.Matcher findNextCueHeader(org.telegram.messenger.exoplayer.util.ParsableByteArray r3) {
        /*
    L_0x0000:
        r1 = r3.readLine();
        if (r1 == 0) goto L_0x002c;
    L_0x0006:
        r2 = COMMENT;
        r2 = r2.matcher(r1);
        r2 = r2.matches();
        if (r2 == 0) goto L_0x001f;
    L_0x0012:
        r1 = r3.readLine();
        if (r1 == 0) goto L_0x0000;
    L_0x0018:
        r2 = r1.isEmpty();
        if (r2 != 0) goto L_0x0000;
    L_0x001e:
        goto L_0x0012;
    L_0x001f:
        r2 = CUE_HEADER_PATTERN;
        r0 = r2.matcher(r1);
        r2 = r0.matches();
        if (r2 == 0) goto L_0x0000;
    L_0x002b:
        return r0;
    L_0x002c:
        r0 = 0;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.text.webvtt.WebvttCueParser.findNextCueHeader(org.telegram.messenger.exoplayer.util.ParsableByteArray):java.util.regex.Matcher");
    }

    static void parseCueText(String markup, Builder builder) {
        SpannableStringBuilder spannedText = new SpannableStringBuilder();
        Stack<StartTag> startTagStack = new Stack();
        int pos = 0;
        while (pos < markup.length()) {
            char curr = markup.charAt(pos);
            switch (curr) {
                case NalUnitTypes.NAL_TYPE_FD_NUT /*38*/:
                    int semiColonEnd = markup.indexOf(59, pos + STYLE_BOLD);
                    int spaceEnd = markup.indexOf(32, pos + STYLE_BOLD);
                    int entityEnd = semiColonEnd == -1 ? spaceEnd : spaceEnd == -1 ? semiColonEnd : Math.min(semiColonEnd, spaceEnd);
                    if (entityEnd == -1) {
                        spannedText.append(curr);
                        pos += STYLE_BOLD;
                        break;
                    }
                    applyEntity(markup.substring(pos + STYLE_BOLD, entityEnd), spannedText);
                    if (entityEnd == spaceEnd) {
                        spannedText.append(SPACE);
                    }
                    pos = entityEnd + STYLE_BOLD;
                    break;
                case '<':
                    if (pos + STYLE_BOLD < markup.length()) {
                        int i;
                        int ltPos = pos;
                        boolean isClosingTag = markup.charAt(ltPos + STYLE_BOLD) == CHAR_SLASH;
                        pos = findEndOfTag(markup, ltPos + STYLE_BOLD);
                        boolean isVoidTag = markup.charAt(pos + -2) == CHAR_SLASH;
                        int i2 = ltPos + (isClosingTag ? STYLE_ITALIC : STYLE_BOLD);
                        if (isVoidTag) {
                            i = pos - 2;
                        } else {
                            i = pos - 1;
                        }
                        String[] tagTokens = tokenizeTag(markup.substring(i2, i));
                        if (tagTokens != null && isSupportedTag(tagTokens[0])) {
                            if (!isClosingTag) {
                                if (!isVoidTag) {
                                    startTagStack.push(new StartTag(tagTokens[0], spannedText.length()));
                                    break;
                                }
                                break;
                            }
                            while (!startTagStack.isEmpty()) {
                                StartTag startTag = (StartTag) startTagStack.pop();
                                applySpansForTag(startTag, spannedText);
                                if (startTag.name.equals(tagTokens[0])) {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    pos += STYLE_BOLD;
                    break;
                default:
                    spannedText.append(curr);
                    pos += STYLE_BOLD;
                    break;
            }
        }
        while (!startTagStack.isEmpty()) {
            applySpansForTag((StartTag) startTagStack.pop(), spannedText);
        }
        builder.setText(spannedText);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean parseCue(java.util.regex.Matcher r8, org.telegram.messenger.exoplayer.util.ParsableByteArray r9, org.telegram.messenger.exoplayer.text.webvtt.WebvttCue.Builder r10, java.lang.StringBuilder r11) {
        /*
        r2 = 1;
        r3 = 0;
        r4 = 1;
        r4 = r8.group(r4);	 Catch:{ NumberFormatException -> 0x0045 }
        r4 = org.telegram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r4);	 Catch:{ NumberFormatException -> 0x0045 }
        r4 = r10.setStartTime(r4);	 Catch:{ NumberFormatException -> 0x0045 }
        r5 = 2;
        r5 = r8.group(r5);	 Catch:{ NumberFormatException -> 0x0045 }
        r6 = org.telegram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r5);	 Catch:{ NumberFormatException -> 0x0045 }
        r4.setEndTime(r6);	 Catch:{ NumberFormatException -> 0x0045 }
        r4 = 3;
        r4 = r8.group(r4);
        parseCueSettingsList(r4, r10);
        r11.setLength(r3);
    L_0x0026:
        r1 = r9.readLine();
        if (r1 == 0) goto L_0x0064;
    L_0x002c:
        r3 = r1.isEmpty();
        if (r3 != 0) goto L_0x0064;
    L_0x0032:
        r3 = r11.length();
        if (r3 <= 0) goto L_0x003d;
    L_0x0038:
        r3 = "\n";
        r11.append(r3);
    L_0x003d:
        r3 = r1.trim();
        r11.append(r3);
        goto L_0x0026;
    L_0x0045:
        r0 = move-exception;
        r2 = "WebvttCueParser";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Skipping cue with bad header: ";
        r4 = r4.append(r5);
        r5 = r8.group();
        r4 = r4.append(r5);
        r4 = r4.toString();
        android.util.Log.w(r2, r4);
        r2 = r3;
    L_0x0063:
        return r2;
    L_0x0064:
        r3 = r11.toString();
        parseCueText(r3, r10);
        goto L_0x0063;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.text.webvtt.WebvttCueParser.parseCue(java.util.regex.Matcher, org.telegram.messenger.exoplayer.util.ParsableByteArray, org.telegram.messenger.exoplayer.text.webvtt.WebvttCue$Builder, java.lang.StringBuilder):boolean");
    }

    private static void parseLineAttribute(String s, Builder builder) throws NumberFormatException {
        int commaPosition = s.indexOf(44);
        if (commaPosition != -1) {
            builder.setLineAnchor(parsePositionAnchor(s.substring(commaPosition + STYLE_BOLD)));
            s = s.substring(0, commaPosition);
        } else {
            builder.setLineAnchor(LinearLayoutManager.INVALID_OFFSET);
        }
        if (s.endsWith("%")) {
            builder.setLine(WebvttParserUtil.parsePercentage(s)).setLineType(0);
        } else {
            builder.setLine((float) Integer.parseInt(s)).setLineType(STYLE_BOLD);
        }
    }

    private static void parsePositionAttribute(String s, Builder builder) throws NumberFormatException {
        int commaPosition = s.indexOf(44);
        if (commaPosition != -1) {
            builder.setPositionAnchor(parsePositionAnchor(s.substring(commaPosition + STYLE_BOLD)));
            s = s.substring(0, commaPosition);
        } else {
            builder.setPositionAnchor(LinearLayoutManager.INVALID_OFFSET);
        }
        builder.setPosition(WebvttParserUtil.parsePercentage(s));
    }

    private static int parsePositionAnchor(String s) {
        int i = -1;
        switch (s.hashCode()) {
            case -1364013995:
                if (s.equals(TtmlNode.CENTER)) {
                    i = STYLE_BOLD;
                    break;
                }
                break;
            case -1074341483:
                if (s.equals("middle")) {
                    i = STYLE_ITALIC;
                    break;
                }
                break;
            case 100571:
                if (s.equals(TtmlNode.END)) {
                    i = 3;
                    break;
                }
                break;
            case 109757538:
                if (s.equals(TtmlNode.START)) {
                    i = 0;
                    break;
                }
                break;
        }
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return 0;
            case STYLE_BOLD /*1*/:
            case STYLE_ITALIC /*2*/:
                return STYLE_BOLD;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return STYLE_ITALIC;
            default:
                Log.w(TAG, "Invalid anchor value: " + s);
                return LinearLayoutManager.INVALID_OFFSET;
        }
    }

    private static Alignment parseTextAlignment(String s) {
        Object obj = -1;
        switch (s.hashCode()) {
            case -1364013995:
                if (s.equals(TtmlNode.CENTER)) {
                    obj = STYLE_ITALIC;
                    break;
                }
                break;
            case -1074341483:
                if (s.equals("middle")) {
                    obj = 3;
                    break;
                }
                break;
            case 100571:
                if (s.equals(TtmlNode.END)) {
                    obj = 4;
                    break;
                }
                break;
            case 3317767:
                if (s.equals(TtmlNode.LEFT)) {
                    obj = STYLE_BOLD;
                    break;
                }
                break;
            case 108511772:
                if (s.equals(TtmlNode.RIGHT)) {
                    obj = 5;
                    break;
                }
                break;
            case 109757538:
                if (s.equals(TtmlNode.START)) {
                    obj = null;
                    break;
                }
                break;
        }
        switch (obj) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case STYLE_BOLD /*1*/:
                return Alignment.ALIGN_NORMAL;
            case STYLE_ITALIC /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return Alignment.ALIGN_CENTER;
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                return Alignment.ALIGN_OPPOSITE;
            default:
                Log.w(TAG, "Invalid alignment value: " + s);
                return null;
        }
    }

    private static int findEndOfTag(String markup, int startPos) {
        int idx = markup.indexOf(62, startPos);
        return idx == -1 ? markup.length() : idx + STYLE_BOLD;
    }

    private static void applyEntity(String entity, SpannableStringBuilder spannedText) {
        Object obj = -1;
        switch (entity.hashCode()) {
            case 3309:
                if (entity.equals(ENTITY_GREATER_THAN)) {
                    obj = STYLE_BOLD;
                    break;
                }
                break;
            case 3464:
                if (entity.equals(ENTITY_LESS_THAN)) {
                    obj = null;
                    break;
                }
                break;
            case 96708:
                if (entity.equals(ENTITY_AMPERSAND)) {
                    obj = 3;
                    break;
                }
                break;
            case 3374865:
                if (entity.equals(ENTITY_NON_BREAK_SPACE)) {
                    obj = STYLE_ITALIC;
                    break;
                }
                break;
        }
        switch (obj) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                spannedText.append(CHAR_LESS_THAN);
            case STYLE_BOLD /*1*/:
                spannedText.append(CHAR_GREATER_THAN);
            case STYLE_ITALIC /*2*/:
                spannedText.append(CHAR_SPACE);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                spannedText.append(CHAR_AMPERSAND);
            default:
                Log.w(TAG, "ignoring unsupported entity: '&" + entity + ";'");
        }
    }

    private static boolean isSupportedTag(String tagName) {
        boolean z = true;
        switch (tagName.hashCode()) {
            case 98:
                if (tagName.equals(TAG_BOLD)) {
                    z = false;
                    break;
                }
                break;
            case 99:
                if (tagName.equals(TAG_CLASS)) {
                    z = true;
                    break;
                }
                break;
            case 105:
                if (tagName.equals(TAG_ITALIC)) {
                    z = true;
                    break;
                }
                break;
            case 117:
                if (tagName.equals(TAG_UNDERLINE)) {
                    z = true;
                    break;
                }
                break;
            case 118:
                if (tagName.equals(TAG_VOICE)) {
                    z = true;
                    break;
                }
                break;
            case 3314158:
                if (tagName.equals(TAG_LANG)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case STYLE_BOLD /*1*/:
            case STYLE_ITALIC /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                return true;
            default:
                return false;
        }
    }

    private static void applySpansForTag(StartTag startTag, SpannableStringBuilder spannedText) {
        String str = startTag.name;
        int i = -1;
        switch (str.hashCode()) {
            case 98:
                if (str.equals(TAG_BOLD)) {
                    i = 0;
                    break;
                }
                break;
            case 105:
                if (str.equals(TAG_ITALIC)) {
                    i = STYLE_BOLD;
                    break;
                }
                break;
            case 117:
                if (str.equals(TAG_UNDERLINE)) {
                    i = STYLE_ITALIC;
                    break;
                }
                break;
        }
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                spannedText.setSpan(new StyleSpan(STYLE_BOLD), startTag.position, spannedText.length(), 33);
            case STYLE_BOLD /*1*/:
                spannedText.setSpan(new StyleSpan(STYLE_ITALIC), startTag.position, spannedText.length(), 33);
            case STYLE_ITALIC /*2*/:
                spannedText.setSpan(new UnderlineSpan(), startTag.position, spannedText.length(), 33);
            default:
        }
    }

    private static String[] tokenizeTag(String fullTagExpression) {
        fullTagExpression = fullTagExpression.replace("\\s+", SPACE).trim();
        if (fullTagExpression.length() == 0) {
            return null;
        }
        if (fullTagExpression.contains(SPACE)) {
            fullTagExpression = fullTagExpression.substring(0, fullTagExpression.indexOf(SPACE));
        }
        return fullTagExpression.split("\\.");
    }
}
