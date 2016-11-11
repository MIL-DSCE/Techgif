package org.telegram.messenger.exoplayer.text.ttml;

import android.util.Log;
import android.util.Pair;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParserUtil;
import org.telegram.ui.Components.VideoPlayer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlParser implements SubtitleParser {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CLOCK_TIME;
    private static final int DEFAULT_FRAMERATE = 30;
    private static final int DEFAULT_SUBFRAMERATE = 1;
    private static final int DEFAULT_TICKRATE = 1;
    private static final Pattern FONT_SIZE;
    private static final Pattern OFFSET_TIME;
    private static final Pattern PERCENTAGE_COORDINATES;
    private static final String TAG = "TtmlParser";
    private final XmlPullParserFactory xmlParserFactory;

    static {
        CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
        OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
        FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
        PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    }

    public TtmlParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public boolean canParse(String mimeType) {
        return MimeTypes.APPLICATION_TTML.equals(mimeType);
    }

    public TtmlSubtitle parse(byte[] bytes, int offset, int length) throws ParserException {
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            Map<String, TtmlStyle> globalStyles = new HashMap();
            Map<String, TtmlRegion> regionMap = new HashMap();
            regionMap.put(TtmlNode.ANONYMOUS_REGION_ID, new TtmlRegion());
            xmlParser.setInput(new ByteArrayInputStream(bytes, offset, length), null);
            TtmlSubtitle ttmlSubtitle = null;
            LinkedList<TtmlNode> nodeStack = new LinkedList();
            int unsupportedNodeDepth = 0;
            for (int eventType = xmlParser.getEventType(); eventType != DEFAULT_TICKRATE; eventType = xmlParser.getEventType()) {
                TtmlNode parent = (TtmlNode) nodeStack.peekLast();
                if (unsupportedNodeDepth == 0) {
                    String name = xmlParser.getName();
                    if (eventType == 2) {
                        if (isSupportedTag(name)) {
                            if (TtmlNode.TAG_HEAD.equals(name)) {
                                parseHeader(xmlParser, globalStyles, regionMap);
                            } else {
                                try {
                                    TtmlNode node = parseNode(xmlParser, parent, regionMap);
                                    nodeStack.addLast(node);
                                    if (parent != null) {
                                        parent.addChild(node);
                                    }
                                } catch (ParserException e) {
                                    Log.w(TAG, "Suppressing parser error", e);
                                    unsupportedNodeDepth += DEFAULT_TICKRATE;
                                }
                            }
                        } else {
                            Log.i(TAG, "Ignoring unsupported tag: " + xmlParser.getName());
                            unsupportedNodeDepth += DEFAULT_TICKRATE;
                        }
                    } else if (eventType == 4) {
                        parent.addChild(TtmlNode.buildTextNode(xmlParser.getText()));
                    } else if (eventType == 3) {
                        if (xmlParser.getName().equals(TtmlNode.TAG_TT)) {
                            ttmlSubtitle = new TtmlSubtitle((TtmlNode) nodeStack.getLast(), globalStyles, regionMap);
                        }
                        nodeStack.removeLast();
                    } else {
                        continue;
                    }
                } else if (eventType == 2) {
                    unsupportedNodeDepth += DEFAULT_TICKRATE;
                } else if (eventType == 3) {
                    unsupportedNodeDepth--;
                }
                xmlParser.next();
            }
            return ttmlSubtitle;
        } catch (XmlPullParserException xppe) {
            throw new ParserException("Unable to parse source", xppe);
        } catch (IOException e2) {
            throw new IllegalStateException("Unexpected error when reading input.", e2);
        }
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, Map<String, TtmlRegion> globalRegions) throws IOException, XmlPullParserException {
        do {
            xmlParser.next();
            if (ParserUtil.isStartTag(xmlParser, ATTR_STYLE)) {
                String parentStyleId = ParserUtil.getAttributeValue(xmlParser, ATTR_STYLE);
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    String[] ids = parseStyleIds(parentStyleId);
                    for (int i = 0; i < ids.length; i += DEFAULT_TICKRATE) {
                        style.chain((TtmlStyle) globalStyles.get(ids[i]));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (ParserUtil.isStartTag(xmlParser, ATTR_REGION)) {
                Pair<String, TtmlRegion> ttmlRegionInfo = parseRegionAttributes(xmlParser);
                if (ttmlRegionInfo != null) {
                    globalRegions.put(ttmlRegionInfo.first, ttmlRegionInfo.second);
                }
            }
        } while (!ParserUtil.isEndTag(xmlParser, TtmlNode.TAG_HEAD));
        return globalStyles;
    }

    private Pair<String, TtmlRegion> parseRegionAttributes(XmlPullParser xmlParser) {
        String regionId = ParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_ID);
        String regionOrigin = ParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_ORIGIN);
        String regionExtent = ParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_EXTENT);
        if (regionOrigin == null || regionId == null) {
            return null;
        }
        float position = Cue.DIMEN_UNSET;
        float line = Cue.DIMEN_UNSET;
        Matcher originMatcher = PERCENTAGE_COORDINATES.matcher(regionOrigin);
        if (originMatcher.matches()) {
            try {
                position = Float.parseFloat(originMatcher.group(DEFAULT_TICKRATE)) / 100.0f;
                line = Float.parseFloat(originMatcher.group(2)) / 100.0f;
            } catch (NumberFormatException e) {
                Log.w(TAG, "Ignoring region with malformed origin: '" + regionOrigin + "'", e);
                position = Cue.DIMEN_UNSET;
            }
        }
        float width = Cue.DIMEN_UNSET;
        if (regionExtent != null) {
            Matcher extentMatcher = PERCENTAGE_COORDINATES.matcher(regionExtent);
            if (extentMatcher.matches()) {
                try {
                    width = Float.parseFloat(extentMatcher.group(DEFAULT_TICKRATE)) / 100.0f;
                } catch (NumberFormatException e2) {
                    Log.w(TAG, "Ignoring malformed region extent: '" + regionExtent + "'", e2);
                }
            }
        }
        if (position != Cue.DIMEN_UNSET) {
            return new Pair(regionId, new TtmlRegion(position, line, width));
        }
        return null;
    }

    private String[] parseStyleIds(String parentStyleIds) {
        return parentStyleIds.split("\\s+");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.exoplayer.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r13, org.telegram.messenger.exoplayer.text.ttml.TtmlStyle r14) {
        /*
        r12 = this;
        r9 = 3;
        r8 = 2;
        r6 = -1;
        r7 = 1;
        r5 = 0;
        r0 = r13.getAttributeCount();
        r3 = 0;
    L_0x000a:
        if (r3 >= r0) goto L_0x0221;
    L_0x000c:
        r1 = r13.getAttributeValue(r3);
        r4 = r13.getAttributeName(r3);
        r10 = r4.hashCode();
        switch(r10) {
            case -1550943582: goto L_0x005e;
            case -1224696685: goto L_0x0040;
            case -1065511464: goto L_0x0068;
            case -879295043: goto L_0x0072;
            case -734428249: goto L_0x0054;
            case 3355: goto L_0x0022;
            case 94842723: goto L_0x0036;
            case 365601008: goto L_0x004a;
            case 1287124693: goto L_0x002c;
            default: goto L_0x001b;
        };
    L_0x001b:
        r4 = r6;
    L_0x001c:
        switch(r4) {
            case 0: goto L_0x007d;
            case 1: goto L_0x0092;
            case 2: goto L_0x00bf;
            case 3: goto L_0x00ed;
            case 4: goto L_0x00f7;
            case 5: goto L_0x0121;
            case 6: goto L_0x0131;
            case 7: goto L_0x0141;
            case 8: goto L_0x01c0;
            default: goto L_0x001f;
        };
    L_0x001f:
        r3 = r3 + 1;
        goto L_0x000a;
    L_0x0022:
        r10 = "id";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x002a:
        r4 = r5;
        goto L_0x001c;
    L_0x002c:
        r10 = "backgroundColor";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0034:
        r4 = r7;
        goto L_0x001c;
    L_0x0036:
        r10 = "color";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x003e:
        r4 = r8;
        goto L_0x001c;
    L_0x0040:
        r10 = "fontFamily";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0048:
        r4 = r9;
        goto L_0x001c;
    L_0x004a:
        r10 = "fontSize";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0052:
        r4 = 4;
        goto L_0x001c;
    L_0x0054:
        r10 = "fontWeight";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x005c:
        r4 = 5;
        goto L_0x001c;
    L_0x005e:
        r10 = "fontStyle";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0066:
        r4 = 6;
        goto L_0x001c;
    L_0x0068:
        r10 = "textAlign";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x0070:
        r4 = 7;
        goto L_0x001c;
    L_0x0072:
        r10 = "textDecoration";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001b;
    L_0x007a:
        r4 = 8;
        goto L_0x001c;
    L_0x007d:
        r4 = "style";
        r10 = r13.getName();
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x001f;
    L_0x0089:
        r4 = r12.createIfNull(r14);
        r14 = r4.setId(r1);
        goto L_0x001f;
    L_0x0092:
        r14 = r12.createIfNull(r14);
        r4 = org.telegram.messenger.exoplayer.text.ttml.TtmlColorParser.parseColor(r1);	 Catch:{ IllegalArgumentException -> 0x009e }
        r14.setBackgroundColor(r4);	 Catch:{ IllegalArgumentException -> 0x009e }
        goto L_0x001f;
    L_0x009e:
        r2 = move-exception;
        r4 = "TtmlParser";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing background value: '";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r11 = "'";
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.w(r4, r10);
        goto L_0x001f;
    L_0x00bf:
        r14 = r12.createIfNull(r14);
        r4 = org.telegram.messenger.exoplayer.text.ttml.TtmlColorParser.parseColor(r1);	 Catch:{ IllegalArgumentException -> 0x00cc }
        r14.setFontColor(r4);	 Catch:{ IllegalArgumentException -> 0x00cc }
        goto L_0x001f;
    L_0x00cc:
        r2 = move-exception;
        r4 = "TtmlParser";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing color value: '";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r11 = "'";
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.w(r4, r10);
        goto L_0x001f;
    L_0x00ed:
        r4 = r12.createIfNull(r14);
        r14 = r4.setFontFamily(r1);
        goto L_0x001f;
    L_0x00f7:
        r14 = r12.createIfNull(r14);	 Catch:{ ParserException -> 0x0100 }
        parseFontSize(r1, r14);	 Catch:{ ParserException -> 0x0100 }
        goto L_0x001f;
    L_0x0100:
        r2 = move-exception;
        r4 = "TtmlParser";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing fontSize value: '";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r11 = "'";
        r10 = r10.append(r11);
        r10 = r10.toString();
        android.util.Log.w(r4, r10);
        goto L_0x001f;
    L_0x0121:
        r4 = r12.createIfNull(r14);
        r10 = "bold";
        r10 = r10.equalsIgnoreCase(r1);
        r14 = r4.setBold(r10);
        goto L_0x001f;
    L_0x0131:
        r4 = r12.createIfNull(r14);
        r10 = "italic";
        r10 = r10.equalsIgnoreCase(r1);
        r14 = r4.setItalic(r10);
        goto L_0x001f;
    L_0x0141:
        r4 = org.telegram.messenger.exoplayer.util.Util.toLowerInvariant(r1);
        r10 = r4.hashCode();
        switch(r10) {
            case -1364013995: goto L_0x0186;
            case 100571: goto L_0x017c;
            case 3317767: goto L_0x015e;
            case 108511772: goto L_0x0172;
            case 109757538: goto L_0x0168;
            default: goto L_0x014c;
        };
    L_0x014c:
        r4 = r6;
    L_0x014d:
        switch(r4) {
            case 0: goto L_0x0152;
            case 1: goto L_0x0190;
            case 2: goto L_0x019c;
            case 3: goto L_0x01a8;
            case 4: goto L_0x01b4;
            default: goto L_0x0150;
        };
    L_0x0150:
        goto L_0x001f;
    L_0x0152:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x015e:
        r10 = "left";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x014c;
    L_0x0166:
        r4 = r5;
        goto L_0x014d;
    L_0x0168:
        r10 = "start";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x014c;
    L_0x0170:
        r4 = r7;
        goto L_0x014d;
    L_0x0172:
        r10 = "right";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x014c;
    L_0x017a:
        r4 = r8;
        goto L_0x014d;
    L_0x017c:
        r10 = "end";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x014c;
    L_0x0184:
        r4 = r9;
        goto L_0x014d;
    L_0x0186:
        r10 = "center";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x014c;
    L_0x018e:
        r4 = 4;
        goto L_0x014d;
    L_0x0190:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x019c:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01a8:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01b4:
        r4 = r12.createIfNull(r14);
        r10 = android.text.Layout.Alignment.ALIGN_CENTER;
        r14 = r4.setTextAlign(r10);
        goto L_0x001f;
    L_0x01c0:
        r4 = org.telegram.messenger.exoplayer.util.Util.toLowerInvariant(r1);
        r10 = r4.hashCode();
        switch(r10) {
            case -1461280213: goto L_0x01f9;
            case -1026963764: goto L_0x01ef;
            case 913457136: goto L_0x01e5;
            case 1679736913: goto L_0x01db;
            default: goto L_0x01cb;
        };
    L_0x01cb:
        r4 = r6;
    L_0x01cc:
        switch(r4) {
            case 0: goto L_0x01d1;
            case 1: goto L_0x0203;
            case 2: goto L_0x020d;
            case 3: goto L_0x0217;
            default: goto L_0x01cf;
        };
    L_0x01cf:
        goto L_0x001f;
    L_0x01d1:
        r4 = r12.createIfNull(r14);
        r14 = r4.setLinethrough(r7);
        goto L_0x001f;
    L_0x01db:
        r10 = "linethrough";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cb;
    L_0x01e3:
        r4 = r5;
        goto L_0x01cc;
    L_0x01e5:
        r10 = "nolinethrough";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cb;
    L_0x01ed:
        r4 = r7;
        goto L_0x01cc;
    L_0x01ef:
        r10 = "underline";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cb;
    L_0x01f7:
        r4 = r8;
        goto L_0x01cc;
    L_0x01f9:
        r10 = "nounderline";
        r4 = r4.equals(r10);
        if (r4 == 0) goto L_0x01cb;
    L_0x0201:
        r4 = r9;
        goto L_0x01cc;
    L_0x0203:
        r4 = r12.createIfNull(r14);
        r14 = r4.setLinethrough(r5);
        goto L_0x001f;
    L_0x020d:
        r4 = r12.createIfNull(r14);
        r14 = r4.setUnderline(r7);
        goto L_0x001f;
    L_0x0217:
        r4 = r12.createIfNull(r14);
        r14 = r4.setUnderline(r5);
        goto L_0x001f;
    L_0x0221:
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer.text.ttml.TtmlParser.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, org.telegram.messenger.exoplayer.text.ttml.TtmlStyle):org.telegram.messenger.exoplayer.text.ttml.TtmlStyle");
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    private TtmlNode parseNode(XmlPullParser parser, TtmlNode parent, Map<String, TtmlRegion> regionMap) throws ParserException {
        long duration = 0;
        long startTime = -1;
        long endTime = -1;
        String regionId = TtmlNode.ANONYMOUS_REGION_ID;
        String[] styleIds = null;
        int attributeCount = parser.getAttributeCount();
        TtmlStyle style = parseStyleAttributes(parser, null);
        for (int i = 0; i < attributeCount; i += DEFAULT_TICKRATE) {
            String attr = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (ATTR_BEGIN.equals(attr)) {
                startTime = parseTimeExpression(value, DEFAULT_FRAMERATE, DEFAULT_TICKRATE, DEFAULT_TICKRATE);
            } else if (ATTR_END.equals(attr)) {
                endTime = parseTimeExpression(value, DEFAULT_FRAMERATE, DEFAULT_TICKRATE, DEFAULT_TICKRATE);
            } else if (ATTR_DURATION.equals(attr)) {
                duration = parseTimeExpression(value, DEFAULT_FRAMERATE, DEFAULT_TICKRATE, DEFAULT_TICKRATE);
            } else if (ATTR_STYLE.equals(attr)) {
                String[] ids = parseStyleIds(value);
                if (ids.length > 0) {
                    styleIds = ids;
                }
            } else if (ATTR_REGION.equals(attr) && regionMap.containsKey(value)) {
                regionId = value;
            }
        }
        if (parent != null) {
            if (parent.startTimeUs != -1) {
                if (startTime != -1) {
                    startTime += parent.startTimeUs;
                }
                if (endTime != -1) {
                    endTime += parent.startTimeUs;
                }
            }
        }
        if (endTime == -1) {
            if (duration > 0) {
                endTime = startTime + duration;
            } else if (parent != null) {
                if (parent.endTimeUs != -1) {
                    endTime = parent.endTimeUs;
                }
            }
        }
        return TtmlNode.buildNode(parser.getName(), startTime, endTime, style, styleIds, regionId);
    }

    private static boolean isSupportedTag(String tag) {
        if (tag.equals(TtmlNode.TAG_TT) || tag.equals(TtmlNode.TAG_HEAD) || tag.equals(TtmlNode.TAG_BODY) || tag.equals(TtmlNode.TAG_DIV) || tag.equals(TtmlNode.TAG_P) || tag.equals(TtmlNode.TAG_SPAN) || tag.equals(TtmlNode.TAG_BR) || tag.equals(ATTR_STYLE) || tag.equals(TtmlNode.TAG_STYLING) || tag.equals(TtmlNode.TAG_LAYOUT) || tag.equals(ATTR_REGION) || tag.equals(TtmlNode.TAG_METADATA) || tag.equals(TtmlNode.TAG_SMPTE_IMAGE) || tag.equals(TtmlNode.TAG_SMPTE_DATA) || tag.equals(TtmlNode.TAG_SMPTE_INFORMATION)) {
            return true;
        }
        return false;
    }

    private static void parseFontSize(String expression, TtmlStyle out) throws ParserException {
        Matcher matcher;
        String[] expressions = expression.split("\\s+");
        if (expressions.length == DEFAULT_TICKRATE) {
            matcher = FONT_SIZE.matcher(expression);
        } else if (expressions.length == 2) {
            matcher = FONT_SIZE.matcher(expressions[DEFAULT_TICKRATE]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new ParserException("Invalid number of entries for fontSize: " + expressions.length + ".");
        }
        if (matcher.matches()) {
            String unit = matcher.group(3);
            int i = -1;
            switch (unit.hashCode()) {
                case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                    if (unit.equals("%")) {
                        i = 2;
                        break;
                    }
                    break;
                case 3240:
                    if (unit.equals("em")) {
                        i = DEFAULT_TICKRATE;
                        break;
                    }
                    break;
                case 3592:
                    if (unit.equals("px")) {
                        i = 0;
                        break;
                    }
                    break;
            }
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    out.setFontSizeUnit(DEFAULT_TICKRATE);
                    break;
                case DEFAULT_TICKRATE /*1*/:
                    out.setFontSizeUnit(2);
                    break;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    out.setFontSizeUnit(3);
                    break;
                default:
                    throw new ParserException("Invalid unit for fontSize: '" + unit + "'.");
            }
            out.setFontSize(Float.valueOf(matcher.group(DEFAULT_TICKRATE)).floatValue());
            return;
        }
        throw new ParserException("Invalid expression for fontSize: '" + expression + "'.");
    }

    private static long parseTimeExpression(String time, int frameRate, int subframeRate, int tickRate) throws ParserException {
        Matcher matcher = CLOCK_TIME.matcher(time);
        if (matcher.matches()) {
            double durationSeconds = (((double) (Long.parseLong(matcher.group(DEFAULT_TICKRATE)) * 3600)) + ((double) (Long.parseLong(matcher.group(2)) * 60))) + ((double) Long.parseLong(matcher.group(3)));
            String fraction = matcher.group(4);
            durationSeconds += fraction != null ? Double.parseDouble(fraction) : 0.0d;
            String frames = matcher.group(5);
            durationSeconds += frames != null ? ((double) Long.parseLong(frames)) / ((double) frameRate) : 0.0d;
            String subframes = matcher.group(6);
            return (long) (1000000.0d * (durationSeconds + (subframes != null ? (((double) Long.parseLong(subframes)) / ((double) subframeRate)) / ((double) frameRate) : 0.0d)));
        }
        matcher = OFFSET_TIME.matcher(time);
        if (matcher.matches()) {
            double offsetSeconds = Double.parseDouble(matcher.group(DEFAULT_TICKRATE));
            String unit = matcher.group(2);
            if (unit.equals("h")) {
                offsetSeconds *= 3600.0d;
            } else if (unit.equals("m")) {
                offsetSeconds *= 60.0d;
            } else if (!unit.equals("s")) {
                if (unit.equals("ms")) {
                    offsetSeconds /= 1000.0d;
                } else if (unit.equals("f")) {
                    offsetSeconds /= (double) frameRate;
                } else if (unit.equals("t")) {
                    offsetSeconds /= (double) tickRate;
                }
            }
            return (long) (1000000.0d * offsetSeconds);
        }
        throw new ParserException("Malformed time expression: " + time);
    }
}
