package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.text.format.DateFormat;
import android.util.Xml;
import com.appsgeyser.sdk.ads.FullScreenBanner;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.xmlpull.v1.XmlPullParser;

public class LocaleController {
    private static volatile LocaleController Instance = null;
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    private static boolean is24HourFormat;
    public static boolean isRTL;
    public static int nameDisplayOrder;
    private HashMap<String, PluralRules> allRules;
    private boolean changingConfiguration;
    public FastDateFormat chatDate;
    public FastDateFormat chatFullDate;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private LocaleInfo defaultLocalInfo;
    public FastDateFormat formatterDay;
    public FastDateFormat formatterMonth;
    public FastDateFormat formatterMonthYear;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    public HashMap<String, LocaleInfo> languagesDict;
    private HashMap<String, String> localeValues;
    private ArrayList<LocaleInfo> otherLanguages;
    public ArrayList<LocaleInfo> sortedLanguages;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;

    /* renamed from: org.telegram.messenger.LocaleController.1 */
    class C05271 implements Comparator<LocaleInfo> {
        C05271() {
        }

        public int compare(LocaleInfo o, LocaleInfo o2) {
            return o.name.compareTo(o2.name);
        }
    }

    /* renamed from: org.telegram.messenger.LocaleController.2 */
    class C05282 implements Comparator<LocaleInfo> {
        C05282() {
        }

        public int compare(LocaleInfo o, LocaleInfo o2) {
            if (o.shortName == null) {
                return -1;
            }
            if (o2.shortName == null) {
                return LocaleController.QUANTITY_ZERO;
            }
            return o.name.compareTo(o2.name);
        }
    }

    public static class LocaleInfo {
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String shortName;

        public String getSaveString() {
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile;
        }

        public static LocaleInfo createWithString(String string) {
            if (string == null || string.length() == 0) {
                return null;
            }
            String[] args = string.split("\\|");
            if (args.length != LocaleController.QUANTITY_TWO) {
                return null;
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.name = args[LocaleController.QUANTITY_OTHER];
            localeInfo.nameEnglish = args[LocaleController.QUANTITY_ZERO];
            localeInfo.shortName = args[LocaleController.QUANTITY_ONE];
            localeInfo.pathToFile = args[3];
            return localeInfo;
        }
    }

    public static abstract class PluralRules {
        abstract int quantityForNumber(int i);
    }

    private class TimeZoneChangedReceiver extends BroadcastReceiver {

        /* renamed from: org.telegram.messenger.LocaleController.TimeZoneChangedReceiver.1 */
        class C05291 implements Runnable {
            C05291() {
            }

            public void run() {
                if (!LocaleController.this.formatterMonth.getTimeZone().equals(TimeZone.getDefault())) {
                    LocaleController.getInstance().recreateFormatters();
                }
            }
        }

        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new C05291());
        }
    }

    public static class PluralRules_Arabic extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 0) {
                return LocaleController.QUANTITY_ZERO;
            }
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count == LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_TWO;
            }
            if (rem100 >= 3 && rem100 <= 10) {
                return LocaleController.QUANTITY_FEW;
            }
            if (rem100 < 11 || rem100 > 99) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_MANY;
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == LocaleController.QUANTITY_ZERO && rem100 != 11) {
                return LocaleController.QUANTITY_ONE;
            }
            if (rem10 >= LocaleController.QUANTITY_ONE && rem10 <= LocaleController.QUANTITY_TWO && (rem100 < 12 || rem100 > 14)) {
                return LocaleController.QUANTITY_FEW;
            }
            if (rem10 == 0 || ((rem10 >= 5 && rem10 <= 9) || (rem100 >= 11 && rem100 <= 14))) {
                return LocaleController.QUANTITY_MANY;
            }
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Breton extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return LocaleController.QUANTITY_ZERO;
            }
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count == LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_TWO;
            }
            if (count == 3) {
                return LocaleController.QUANTITY_FEW;
            }
            if (count == 6) {
                return LocaleController.QUANTITY_MANY;
            }
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count < LocaleController.QUANTITY_ONE || count > LocaleController.QUANTITY_TWO) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int count) {
            if (count < 0 || count >= LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return LocaleController.QUANTITY_ZERO;
            }
            if (count <= 0 || count >= LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return LocaleController.QUANTITY_ZERO;
            }
            if (count % 10 != LocaleController.QUANTITY_ZERO || count % 100 == 11) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == LocaleController.QUANTITY_ZERO && (rem100 < 11 || rem100 > 19)) {
                return LocaleController.QUANTITY_ONE;
            }
            if (rem10 < LocaleController.QUANTITY_ONE || rem10 > 9 || (rem100 >= 11 && rem100 <= 19)) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        public int quantityForNumber(int count) {
            if (count % 10 != LocaleController.QUANTITY_ZERO || count == 11) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count == 0 || (rem100 >= LocaleController.QUANTITY_ONE && rem100 <= 10)) {
                return LocaleController.QUANTITY_FEW;
            }
            if (rem100 < 11 || rem100 > 19) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_MANY;
        }
    }

    public static class PluralRules_None extends PluralRules {
        public int quantityForNumber(int count) {
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_One extends PluralRules {
        public int quantityForNumber(int count) {
            return count == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (rem10 < LocaleController.QUANTITY_ONE || rem10 > LocaleController.QUANTITY_TWO || ((rem100 >= 12 && rem100 <= 14) || (rem100 >= 22 && rem100 <= 24))) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count == 0 || (rem100 >= LocaleController.QUANTITY_ZERO && rem100 <= 19)) {
                return LocaleController.QUANTITY_FEW;
            }
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Slovenian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (rem100 == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (rem100 == LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_TWO;
            }
            if (rem100 < 3 || rem100 > LocaleController.QUANTITY_TWO) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        public int quantityForNumber(int count) {
            if (count >= 0 && count <= LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count < LocaleController.QUANTITY_ONE || count > 10) {
                return LocaleController.QUANTITY_OTHER;
            }
            return LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count == LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_TWO;
            }
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return LocaleController.QUANTITY_ZERO;
            }
            if (count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            if (count == LocaleController.QUANTITY_ONE) {
                return LocaleController.QUANTITY_TWO;
            }
            if (count == 3) {
                return LocaleController.QUANTITY_FEW;
            }
            if (count == 6) {
                return LocaleController.QUANTITY_MANY;
            }
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Zero extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0 || count == LocaleController.QUANTITY_ZERO) {
                return LocaleController.QUANTITY_ONE;
            }
            return LocaleController.QUANTITY_OTHER;
        }
    }

    static {
        isRTL = false;
        nameDisplayOrder = QUANTITY_ZERO;
        is24HourFormat = false;
        Instance = null;
    }

    public static LocaleController getInstance() {
        LocaleController localInstance = Instance;
        if (localInstance == null) {
            synchronized (LocaleController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        LocaleController localInstance2 = new LocaleController();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public LocaleController() {
        this.allRules = new HashMap();
        this.localeValues = new HashMap();
        this.changingConfiguration = false;
        this.sortedLanguages = new ArrayList();
        this.languagesDict = new HashMap();
        this.otherLanguages = new ArrayList();
        addRules(new String[]{"bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"}, new PluralRules_One());
        String[] strArr = new String[QUANTITY_ONE];
        strArr[QUANTITY_OTHER] = "cs";
        strArr[QUANTITY_ZERO] = "sk";
        addRules(strArr, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"hr", "ru", "sr", "uk", "be", "bs", "sh"}, new PluralRules_Balkan());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "lv";
        addRules(strArr, new PluralRules_Latvian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "lt";
        addRules(strArr, new PluralRules_Lithuanian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "pl";
        addRules(strArr, new PluralRules_Polish());
        strArr = new String[QUANTITY_ONE];
        strArr[QUANTITY_OTHER] = "ro";
        strArr[QUANTITY_ZERO] = "mo";
        addRules(strArr, new PluralRules_Romanian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "sl";
        addRules(strArr, new PluralRules_Slovenian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "ar";
        addRules(strArr, new PluralRules_Arabic());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "mk";
        addRules(strArr, new PluralRules_Macedonian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "cy";
        addRules(strArr, new PluralRules_Welsh());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = TtmlNode.TAG_BR;
        addRules(strArr, new PluralRules_Breton());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "lag";
        addRules(strArr, new PluralRules_Langi());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "shi";
        addRules(strArr, new PluralRules_Tachelhit());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "mt";
        addRules(strArr, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", TtmlNode.ATTR_ID, "jv", "ka", "km", "kn", "ms", "th"}, new PluralRules_None());
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.name = "English";
        localeInfo.nameEnglish = "English";
        localeInfo.shortName = "en";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Italiano";
        localeInfo.nameEnglish = "Italian";
        localeInfo.shortName = "it";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Espa\u00f1ol";
        localeInfo.nameEnglish = "Spanish";
        localeInfo.shortName = "es";
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Deutsch";
        localeInfo.nameEnglish = "German";
        localeInfo.shortName = "de";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Nederlands";
        localeInfo.nameEnglish = "Dutch";
        localeInfo.shortName = "nl";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u0627\u0644\u0639\u0631\u0628\u064a\u0629";
        localeInfo.nameEnglish = "Arabic";
        localeInfo.shortName = "ar";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Portugu\u00eas (Brasil)";
        localeInfo.nameEnglish = "Portuguese (Brazil)";
        localeInfo.shortName = "pt_BR";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Portugu\u00eas (Portugal)";
        localeInfo.nameEnglish = "Portuguese (Portugal)";
        localeInfo.shortName = "pt_PT";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\ud55c\uad6d\uc5b4";
        localeInfo.nameEnglish = "Korean";
        localeInfo.shortName = "ko";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u0420\u0443\u0441\u0441\u043a\u0438\u0439";
        localeInfo.nameEnglish = "Russian";
        localeInfo.shortName = "ru";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Catal\u00e0";
        localeInfo.nameEnglish = "Catalan";
        localeInfo.shortName = "ca";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u067e\u0627\u0631\u0633\u06cc";
        localeInfo.nameEnglish = "Parsi";
        localeInfo.shortName = "fa";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Suomi";
        localeInfo.nameEnglish = "Finnish";
        localeInfo.shortName = "fi";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Fran\u00e7ais";
        localeInfo.nameEnglish = "French";
        localeInfo.shortName = "fr";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Galego";
        localeInfo.nameEnglish = "Galician";
        localeInfo.shortName = "gl";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u65e5\u672c\u8a9e";
        localeInfo.nameEnglish = "Japanese";
        localeInfo.shortName = "ja";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Sloven\u010dina";
        localeInfo.nameEnglish = "Slovak";
        localeInfo.shortName = "sk";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u4e2d\u6587";
        localeInfo.nameEnglish = "Chinese";
        localeInfo.shortName = "zh";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u4e2d\u6587\uff08\u9999\u6e2f\uff09";
        localeInfo.nameEnglish = "Chinese (Hong Kong)";
        localeInfo.shortName = "zh_HK";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u6b63\u9ad4\u4e2d\u6587";
        localeInfo.nameEnglish = "Chinese Traditional (Taiwan)";
        localeInfo.shortName = "zh_TW";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "O\u2018zbek";
        localeInfo.nameEnglish = "Uzbek";
        localeInfo.shortName = "uz";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u0411\u0435\u043b\u0430\u0440\u0443\u0441\u043a\u0430\u044f";
        localeInfo.nameEnglish = "Belarusian";
        localeInfo.shortName = "be";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u010ce\u0161tina";
        localeInfo.nameEnglish = "Czech";
        localeInfo.shortName = "cs";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "T\u00fcrk\u00e7e";
        localeInfo.nameEnglish = "Turkish";
        localeInfo.shortName = "tr";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u0423\u043a\u0440\u0430\u0457\u043d\u0441\u044c\u043a\u0430";
        localeInfo.nameEnglish = "Ukrainian";
        localeInfo.shortName = "uk";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        loadOtherLanguages();
        Iterator i$ = this.otherLanguages.iterator();
        while (i$.hasNext()) {
            LocaleInfo locale = (LocaleInfo) i$.next();
            this.sortedLanguages.add(locale);
            this.languagesDict.put(locale.shortName, locale);
        }
        Collections.sort(this.sortedLanguages, new C05271());
        localeInfo = new LocaleInfo();
        this.defaultLocalInfo = localeInfo;
        localeInfo.name = "System default";
        localeInfo.nameEnglish = "System default";
        localeInfo.shortName = null;
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(QUANTITY_OTHER, localeInfo);
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        LocaleInfo currentInfo = null;
        boolean override = false;
        try {
            String lang = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", QUANTITY_OTHER).getString("language", null);
            if (lang != null) {
                currentInfo = (LocaleInfo) this.languagesDict.get(lang);
                if (currentInfo != null) {
                    override = true;
                }
            }
            if (currentInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                currentInfo = (LocaleInfo) this.languagesDict.get(this.systemDefaultLocale.getLanguage());
            }
            if (currentInfo == null) {
                currentInfo = (LocaleInfo) this.languagesDict.get(getLocaleString(this.systemDefaultLocale));
            }
            if (currentInfo == null) {
                currentInfo = (LocaleInfo) this.languagesDict.get("en");
            }
            applyLanguage(currentInfo, override);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
    }

    private void addRules(String[] languages, PluralRules rules) {
        String[] arr$ = languages;
        int len$ = arr$.length;
        for (int i$ = QUANTITY_OTHER; i$ < len$; i$ += QUANTITY_ZERO) {
            this.allRules.put(arr$[i$], rules);
        }
    }

    private String stringForQuantity(int quantity) {
        switch (quantity) {
            case QUANTITY_ZERO /*1*/:
                return "zero";
            case QUANTITY_ONE /*2*/:
                return FullScreenBanner.ONE;
            case QUANTITY_TWO /*4*/:
                return "two";
            case QUANTITY_FEW /*8*/:
                return "few";
            case QUANTITY_MANY /*16*/:
                return "many";
            default:
                return "other";
        }
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('_');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getLocaleStringIso639() {
        Locale locale = getInstance().getSystemDefaultLocale();
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public boolean applyLanguageFile(File file) {
        try {
            HashMap<String, String> stringMap = getLocaleFileStrings(file);
            String languageName = (String) stringMap.get("LanguageName");
            String languageNameInEnglish = (String) stringMap.get("LanguageNameInEnglish");
            String languageCode = (String) stringMap.get("LanguageCode");
            if (languageName == null || languageName.length() <= 0 || languageNameInEnglish == null || languageNameInEnglish.length() <= 0 || languageCode == null || languageCode.length() <= 0 || languageName.contains("&") || languageName.contains("|") || languageNameInEnglish.contains("&") || languageNameInEnglish.contains("|") || languageCode.contains("&") || languageCode.contains("|") || languageCode.contains("/") || languageCode.contains("\\")) {
                return false;
            }
            File finalFile = new File(ApplicationLoader.getFilesDirFixed(), languageCode + ".xml");
            if (!AndroidUtilities.copyFile(file, finalFile)) {
                return false;
            }
            LocaleInfo localeInfo = (LocaleInfo) this.languagesDict.get(languageCode);
            if (localeInfo == null) {
                localeInfo = new LocaleInfo();
                localeInfo.name = languageName;
                localeInfo.nameEnglish = languageNameInEnglish;
                localeInfo.shortName = languageCode;
                localeInfo.pathToFile = finalFile.getAbsolutePath();
                this.sortedLanguages.add(localeInfo);
                this.languagesDict.put(localeInfo.shortName, localeInfo);
                this.otherLanguages.add(localeInfo);
                Collections.sort(this.sortedLanguages, new C05282());
                saveOtherLanguages();
            }
            this.localeValues = stringMap;
            applyLanguage(localeInfo, true, true);
            return true;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return false;
        }
    }

    private void saveOtherLanguages() {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", QUANTITY_OTHER).edit();
        String locales = TtmlNode.ANONYMOUS_REGION_ID;
        Iterator i$ = this.otherLanguages.iterator();
        while (i$.hasNext()) {
            String loc = ((LocaleInfo) i$.next()).getSaveString();
            if (loc != null) {
                if (locales.length() != 0) {
                    locales = locales + "&";
                }
                locales = locales + loc;
            }
        }
        editor.putString("locales", locales);
        editor.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo) {
        if (localeInfo.pathToFile == null) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            applyLanguage(this.defaultLocalInfo, true);
        }
        this.otherLanguages.remove(localeInfo);
        this.sortedLanguages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.shortName);
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        String locales = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", QUANTITY_OTHER).getString("locales", null);
        if (locales != null && locales.length() != 0) {
            String[] arr$ = locales.split("&");
            int len$ = arr$.length;
            for (int i$ = QUANTITY_OTHER; i$ < len$; i$ += QUANTITY_ZERO) {
                LocaleInfo localeInfo = LocaleInfo.createWithString(arr$[i$]);
                if (localeInfo != null) {
                    this.otherLanguages.add(localeInfo);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        Throwable e;
        Throwable th;
        FileInputStream stream = null;
        try {
            HashMap<String, String> stringMap = new HashMap();
            XmlPullParser parser = Xml.newPullParser();
            FileInputStream stream2 = new FileInputStream(file);
            try {
                parser.setInput(stream2, C0747C.UTF8_NAME);
                String name = null;
                String value = null;
                String attrName = null;
                for (int eventType = parser.getEventType(); eventType != QUANTITY_ZERO; eventType = parser.next()) {
                    if (eventType == QUANTITY_ONE) {
                        name = parser.getName();
                        if (parser.getAttributeCount() > 0) {
                            attrName = parser.getAttributeValue(QUANTITY_OTHER);
                        }
                    } else if (eventType == QUANTITY_TWO) {
                        if (attrName != null) {
                            value = parser.getText();
                            if (value != null) {
                                value = value.trim().replace("\\n", "\n").replace("\\", TtmlNode.ANONYMOUS_REGION_ID);
                            }
                        }
                    } else if (eventType == 3) {
                        value = null;
                        attrName = null;
                        name = null;
                    }
                    if (!(name == null || !name.equals("string") || value == null || attrName == null || value.length() == 0 || attrName.length() == 0)) {
                        stringMap.put(attrName, value);
                        name = null;
                        value = null;
                        attrName = null;
                    }
                }
                if (stream2 != null) {
                    try {
                        stream2.close();
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
                stream = stream2;
                return stringMap;
            } catch (Exception e3) {
                e2 = e3;
                stream = stream2;
                try {
                    FileLog.m13e("tmessages", e2);
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e22) {
                            FileLog.m13e("tmessages", e22);
                        }
                    }
                    return new HashMap();
                } catch (Throwable th2) {
                    th = th2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e222) {
                            FileLog.m13e("tmessages", e222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                stream = stream2;
                if (stream != null) {
                    stream.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e222 = e4;
            FileLog.m13e("tmessages", e222);
            if (stream != null) {
                stream.close();
            }
            return new HashMap();
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override) {
        applyLanguage(localeInfo, override, false);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean fromFile) {
        if (localeInfo != null) {
            try {
                Locale newLocale;
                Editor editor;
                if (localeInfo.shortName != null) {
                    String[] args = localeInfo.shortName.split("_");
                    if (args.length == QUANTITY_ZERO) {
                        newLocale = new Locale(localeInfo.shortName);
                    } else {
                        newLocale = new Locale(args[QUANTITY_OTHER], args[QUANTITY_ZERO]);
                    }
                    if (newLocale != null && override) {
                        this.languageOverride = localeInfo.shortName;
                        editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", QUANTITY_OTHER).edit();
                        editor.putString("language", localeInfo.shortName);
                        editor.commit();
                    }
                } else {
                    newLocale = this.systemDefaultLocale;
                    this.languageOverride = null;
                    editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", QUANTITY_OTHER).edit();
                    editor.remove("language");
                    editor.commit();
                    if (newLocale != null) {
                        LocaleInfo localeInfo2 = null;
                        if (newLocale.getLanguage() != null) {
                            localeInfo2 = (LocaleInfo) this.languagesDict.get(newLocale.getLanguage());
                        }
                        if (localeInfo2 == null) {
                            localeInfo2 = (LocaleInfo) this.languagesDict.get(getLocaleString(newLocale));
                        }
                        if (localeInfo2 == null) {
                            newLocale = Locale.US;
                        }
                    }
                }
                if (newLocale != null) {
                    if (localeInfo.pathToFile == null) {
                        this.localeValues.clear();
                    } else if (!fromFile) {
                        this.localeValues = getLocaleFileStrings(new File(localeInfo.pathToFile));
                    }
                    this.currentLocale = newLocale;
                    this.currentLocaleInfo = localeInfo;
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = (PluralRules) this.allRules.get("en");
                    }
                    this.changingConfiguration = true;
                    Locale.setDefault(this.currentLocale);
                    Configuration config = new Configuration();
                    config.locale = this.currentLocale;
                    ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                    this.changingConfiguration = false;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    private void loadCurrentLocale() {
        this.localeValues.clear();
    }

    public static String getCurrentLanguageName() {
        return getString("LanguageName", C0691R.string.LanguageName);
    }

    private String getStringInternal(String key, int res) {
        String value = (String) this.localeValues.get(key);
        if (value == null) {
            try {
                value = ApplicationLoader.applicationContext.getString(res);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        if (value == null) {
            return "LOC_ERR:" + key;
        }
        return value;
    }

    public static String getString(String key, int res) {
        return getInstance().getStringInternal(key, res);
    }

    public static String formatPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        int resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName());
        Object[] objArr = new Object[QUANTITY_ZERO];
        objArr[QUANTITY_OTHER] = Integer.valueOf(plural);
        return formatString(param, resourceId, objArr);
    }

    public static String formatString(String key, int res, Object... args) {
        try {
            String value = (String) getInstance().localeValues.get(key);
            if (value == null) {
                value = ApplicationLoader.applicationContext.getString(res);
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, value, args);
            }
            return String.format(value, args);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR: " + key;
        }
    }

    public static String formatStringSimple(String string, Object... args) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, string, args);
            }
            return String.format(string, args);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR: " + string;
        }
    }

    public void onDeviceConfigurationChange(Configuration newConfig) {
        if (!this.changingConfiguration) {
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            this.systemDefaultLocale = newConfig.locale;
            if (this.languageOverride != null) {
                LocaleInfo toSet = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(toSet, false);
                return;
            }
            Locale newLocale = newConfig.locale;
            if (newLocale != null) {
                String d1 = newLocale.getDisplayName();
                String d2 = this.currentLocale.getDisplayName();
                if (!(d1 == null || d2 == null || d1.equals(d2))) {
                    recreateFormatters();
                }
                this.currentLocale = newLocale;
                this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get("en");
                }
            }
        }
    }

    public static String formatDateChat(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(QUANTITY_ZERO);
            rightNow.setTimeInMillis(date * 1000);
            if (year == rightNow.get(QUANTITY_ZERO)) {
                return getInstance().chatDate.format(1000 * date);
            }
            return getInstance().chatFullDate.format(1000 * date);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(QUANTITY_ZERO);
            rightNow.setTimeInMillis(date * 1000);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(QUANTITY_ZERO);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(1000 * date));
            }
            if (dateDay + QUANTITY_ZERO == day && year == dateYear) {
                return getString("Yesterday", C0691R.string.Yesterday);
            }
            if (year == dateYear) {
                return getInstance().formatterMonth.format(new Date(1000 * date));
            }
            return getInstance().formatterYear.format(new Date(1000 * date));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(QUANTITY_ZERO);
            rightNow.setTimeInMillis(1000 * date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(QUANTITY_ZERO);
            Object[] objArr;
            if (dateDay == day && year == dateYear) {
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getString("TodayAt", C0691R.string.TodayAt);
                objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(1000 * date));
                return String.format("%s %s", objArr);
            } else if (dateDay + QUANTITY_ZERO == day && year == dateYear) {
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getString("YesterdayAt", C0691R.string.YesterdayAt);
                objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(1000 * date));
                return String.format("%s %s", objArr);
            } else if (year == dateYear) {
                r8 = new Object[QUANTITY_ONE];
                r8[QUANTITY_OTHER] = getInstance().formatterMonth.format(new Date(1000 * date));
                r8[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(1000 * date));
                return formatString("formatDateAtTime", C0691R.string.formatDateAtTime, r8);
            } else {
                r8 = new Object[QUANTITY_ONE];
                r8[QUANTITY_OTHER] = getInstance().formatterYear.format(new Date(1000 * date));
                r8[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(1000 * date));
                return formatString("formatDateAtTime", C0691R.string.formatDateAtTime, r8);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR";
        }
    }

    public static String formatDateOnline(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(QUANTITY_ZERO);
            rightNow.setTimeInMillis(1000 * date);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(QUANTITY_ZERO);
            if (dateDay == day && year == dateYear) {
                return String.format("%s %s %s", new Object[]{getString("LastSeen", C0691R.string.LastSeen), getString("TodayAt", C0691R.string.TodayAt), getInstance().formatterDay.format(new Date(1000 * date))});
            } else if (dateDay + QUANTITY_ZERO == day && year == dateYear) {
                return String.format("%s %s %s", new Object[]{getString("LastSeen", C0691R.string.LastSeen), getString("YesterdayAt", C0691R.string.YesterdayAt), getInstance().formatterDay.format(new Date(1000 * date))});
            } else if (year == dateYear) {
                r9 = new Object[QUANTITY_ONE];
                r9[QUANTITY_OTHER] = getInstance().formatterMonth.format(new Date(1000 * date));
                r9[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(1000 * date));
                format = formatString("formatDateAtTime", C0691R.string.formatDateAtTime, r9);
                r8 = new Object[QUANTITY_ONE];
                r8[QUANTITY_OTHER] = getString("LastSeenDate", C0691R.string.LastSeenDate);
                r8[QUANTITY_ZERO] = format;
                return String.format("%s %s", r8);
            } else {
                r9 = new Object[QUANTITY_ONE];
                r9[QUANTITY_OTHER] = getInstance().formatterYear.format(new Date(1000 * date));
                r9[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(1000 * date));
                format = formatString("formatDateAtTime", C0691R.string.formatDateAtTime, r9);
                r8 = new Object[QUANTITY_ONE];
                r8[QUANTITY_OTHER] = getString("LastSeenDate", C0691R.string.LastSeenDate);
                r8[QUANTITY_ZERO] = format;
                return String.format("%s %s", r8);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String format, String defaultFormat) {
        if (format == null || format.length() == 0) {
            format = defaultFormat;
        }
        try {
            return FastDateFormat.getInstance(format, locale);
        } catch (Exception e) {
            return FastDateFormat.getInstance(defaultFormat, locale);
        }
    }

    public void recreateFormatters() {
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String lang = locale.getLanguage();
        if (lang == null) {
            lang = "en";
        }
        isRTL = lang.toLowerCase().equals("ar");
        nameDisplayOrder = lang.toLowerCase().equals("ko") ? QUANTITY_ONE : QUANTITY_ZERO;
        this.formatterMonth = createFormatter(locale, getStringInternal("formatterMonth", C0691R.string.formatterMonth), "dd MMM");
        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", C0691R.string.formatterYear), "dd.MM.yy");
        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", C0691R.string.formatterYearMax), "dd.MM.yyyy");
        this.chatDate = createFormatter(locale, getStringInternal("chatDate", C0691R.string.chatDate), "d MMMM");
        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", C0691R.string.chatFullDate), "d MMMM yyyy");
        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", C0691R.string.formatterWeek), "EEE");
        this.formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", C0691R.string.formatterMonthYear), "MMMM yyyy");
        if (!(lang.toLowerCase().equals("ar") || lang.toLowerCase().equals("ko"))) {
            locale = Locale.US;
        }
        this.formatterDay = createFormatter(locale, is24HourFormat ? getStringInternal("formatterDay24H", C0691R.string.formatterDay24H) : getStringInternal("formatterDay12H", C0691R.string.formatterDay12H), is24HourFormat ? "HH:mm" : "h:mm a");
    }

    public static String stringForMessageListDate(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(QUANTITY_ZERO);
            rightNow.setTimeInMillis(1000 * date);
            int dateDay = rightNow.get(6);
            if (year != rightNow.get(QUANTITY_ZERO)) {
                return getInstance().formatterYear.format(new Date(1000 * date));
            }
            int dayDiff = dateDay - day;
            if (dayDiff == 0 || (dayDiff == -1 && ((long) ((int) (System.currentTimeMillis() / 1000))) - date < 28800)) {
                return getInstance().formatterDay.format(new Date(1000 * date));
            }
            if (dayDiff <= -7 || dayDiff > -1) {
                return getInstance().formatterMonth.format(new Date(1000 * date));
            }
            return getInstance().formatterWeek.format(new Date(1000 * date));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int number, int[] rounded) {
        String K = TtmlNode.ANONYMOUS_REGION_ID;
        int lastDec = QUANTITY_OTHER;
        while (number / 1000 > 0) {
            K = K + "K";
            lastDec = (number % 1000) / 100;
            number /= 1000;
        }
        if (rounded != null) {
            double value = ((double) number) + (((double) lastDec) / 10.0d);
            for (int a = QUANTITY_OTHER; a < K.length(); a += QUANTITY_ZERO) {
                value *= 1000.0d;
            }
            rounded[QUANTITY_OTHER] = (int) value;
        }
        Object[] objArr;
        if (lastDec == 0 || K.length() <= 0) {
            if (K.length() == QUANTITY_ONE) {
                objArr = new Object[QUANTITY_ZERO];
                objArr[QUANTITY_OTHER] = Integer.valueOf(number);
                return String.format(Locale.US, "%dM", objArr);
            }
            objArr = new Object[QUANTITY_ONE];
            objArr[QUANTITY_OTHER] = Integer.valueOf(number);
            objArr[QUANTITY_ZERO] = K;
            return String.format(Locale.US, "%d%s", objArr);
        } else if (K.length() == QUANTITY_ONE) {
            objArr = new Object[QUANTITY_ONE];
            objArr[QUANTITY_OTHER] = Integer.valueOf(number);
            objArr[QUANTITY_ZERO] = Integer.valueOf(lastDec);
            return String.format(Locale.US, "%d.%dM", objArr);
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(number), Integer.valueOf(lastDec), K});
        }
    }

    public static String formatUserStatus(User user) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            return getString("Online", C0691R.string.Online);
        }
        if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TL_userEmpty)) {
            return getString("ALongTimeAgo", C0691R.string.ALongTimeAgo);
        }
        if (user.status.expires > ConnectionsManager.getInstance().getCurrentTime()) {
            return getString("Online", C0691R.string.Online);
        }
        if (user.status.expires == -1) {
            return getString("Invisible", C0691R.string.Invisible);
        }
        if (user.status.expires == -100) {
            return getString("Lately", C0691R.string.Lately);
        }
        if (user.status.expires == -101) {
            return getString("WithinAWeek", C0691R.string.WithinAWeek);
        }
        if (user.status.expires == -102) {
            return getString("WithinAMonth", C0691R.string.WithinAMonth);
        }
        return formatDateOnline((long) user.status.expires);
    }

    public String getTranslitString(String src) {
        if (this.translitChars == null) {
            this.translitChars = new HashMap(520);
            this.translitChars.put("\u023c", "c");
            this.translitChars.put("\u1d87", "n");
            this.translitChars.put("\u0256", "d");
            this.translitChars.put("\u1eff", "y");
            this.translitChars.put("\u1d13", "o");
            this.translitChars.put("\u00f8", "o");
            this.translitChars.put("\u1e01", "a");
            this.translitChars.put("\u02af", "h");
            this.translitChars.put("\u0177", "y");
            this.translitChars.put("\u029e", "k");
            this.translitChars.put("\u1eeb", "u");
            this.translitChars.put("\ua733", "aa");
            this.translitChars.put("\u0133", "ij");
            this.translitChars.put("\u1e3d", "l");
            this.translitChars.put("\u026a", "i");
            this.translitChars.put("\u1e07", "b");
            this.translitChars.put("\u0280", "r");
            this.translitChars.put("\u011b", "e");
            this.translitChars.put("\ufb03", "ffi");
            this.translitChars.put("\u01a1", "o");
            this.translitChars.put("\u2c79", "r");
            this.translitChars.put("\u1ed3", "o");
            this.translitChars.put("\u01d0", "i");
            this.translitChars.put("\ua755", TtmlNode.TAG_P);
            this.translitChars.put("\u00fd", "y");
            this.translitChars.put("\u1e1d", "e");
            this.translitChars.put("\u2092", "o");
            this.translitChars.put("\u2c65", "a");
            this.translitChars.put("\u0299", "b");
            this.translitChars.put("\u1e1b", "e");
            this.translitChars.put("\u0188", "c");
            this.translitChars.put("\u0266", "h");
            this.translitChars.put("\u1d6c", "b");
            this.translitChars.put("\u1e63", "s");
            this.translitChars.put("\u0111", "d");
            this.translitChars.put("\u1ed7", "o");
            this.translitChars.put("\u025f", "j");
            this.translitChars.put("\u1e9a", "a");
            this.translitChars.put("\u024f", "y");
            this.translitChars.put("\u043b", "l");
            this.translitChars.put("\u028c", "v");
            this.translitChars.put("\ua753", TtmlNode.TAG_P);
            this.translitChars.put("\ufb01", "fi");
            this.translitChars.put("\u1d84", "k");
            this.translitChars.put("\u1e0f", "d");
            this.translitChars.put("\u1d0c", "l");
            this.translitChars.put("\u0117", "e");
            this.translitChars.put("\u0451", "yo");
            this.translitChars.put("\u1d0b", "k");
            this.translitChars.put("\u010b", "c");
            this.translitChars.put("\u0281", "r");
            this.translitChars.put("\u0195", "hv");
            this.translitChars.put("\u0180", "b");
            this.translitChars.put("\u1e4d", "o");
            this.translitChars.put("\u0223", "ou");
            this.translitChars.put("\u01f0", "j");
            this.translitChars.put("\u1d83", "g");
            this.translitChars.put("\u1e4b", "n");
            this.translitChars.put("\u0249", "j");
            this.translitChars.put("\u01e7", "g");
            this.translitChars.put("\u01f3", "dz");
            this.translitChars.put("\u017a", "z");
            this.translitChars.put("\ua737", "au");
            this.translitChars.put("\u01d6", "u");
            this.translitChars.put("\u1d79", "g");
            this.translitChars.put("\u022f", "o");
            this.translitChars.put("\u0250", "a");
            this.translitChars.put("\u0105", "a");
            this.translitChars.put("\u00f5", "o");
            this.translitChars.put("\u027b", "r");
            this.translitChars.put("\ua74d", "o");
            this.translitChars.put("\u01df", "a");
            this.translitChars.put("\u0234", "l");
            this.translitChars.put("\u0282", "s");
            this.translitChars.put("\ufb02", "fl");
            this.translitChars.put("\u0209", "i");
            this.translitChars.put("\u2c7b", "e");
            this.translitChars.put("\u1e49", "n");
            this.translitChars.put("\u00ef", "i");
            this.translitChars.put("\u00f1", "n");
            this.translitChars.put("\u1d09", "i");
            this.translitChars.put("\u0287", "t");
            this.translitChars.put("\u1e93", "z");
            this.translitChars.put("\u1ef7", "y");
            this.translitChars.put("\u0233", "y");
            this.translitChars.put("\u1e69", "s");
            this.translitChars.put("\u027d", "r");
            this.translitChars.put("\u011d", "g");
            this.translitChars.put("\u0432", "v");
            this.translitChars.put("\u1d1d", "u");
            this.translitChars.put("\u1e33", "k");
            this.translitChars.put("\ua76b", "et");
            this.translitChars.put("\u012b", "i");
            this.translitChars.put("\u0165", "t");
            this.translitChars.put("\ua73f", "c");
            this.translitChars.put("\u029f", "l");
            this.translitChars.put("\ua739", "av");
            this.translitChars.put("\u00fb", "u");
            this.translitChars.put("\u00e6", "ae");
            this.translitChars.put("\u0438", "i");
            this.translitChars.put("\u0103", "a");
            this.translitChars.put("\u01d8", "u");
            this.translitChars.put("\ua785", "s");
            this.translitChars.put("\u1d63", "r");
            this.translitChars.put("\u1d00", "a");
            this.translitChars.put("\u0183", "b");
            this.translitChars.put("\u1e29", "h");
            this.translitChars.put("\u1e67", "s");
            this.translitChars.put("\u2091", "e");
            this.translitChars.put("\u029c", "h");
            this.translitChars.put("\u1e8b", "x");
            this.translitChars.put("\ua745", "k");
            this.translitChars.put("\u1e0b", "d");
            this.translitChars.put("\u01a3", "oi");
            this.translitChars.put("\ua751", TtmlNode.TAG_P);
            this.translitChars.put("\u0127", "h");
            this.translitChars.put("\u2c74", "v");
            this.translitChars.put("\u1e87", "w");
            this.translitChars.put("\u01f9", "n");
            this.translitChars.put("\u026f", "m");
            this.translitChars.put("\u0261", "g");
            this.translitChars.put("\u0274", "n");
            this.translitChars.put("\u1d18", TtmlNode.TAG_P);
            this.translitChars.put("\u1d65", "v");
            this.translitChars.put("\u016b", "u");
            this.translitChars.put("\u1e03", "b");
            this.translitChars.put("\u1e57", TtmlNode.TAG_P);
            this.translitChars.put("\u044c", TtmlNode.ANONYMOUS_REGION_ID);
            this.translitChars.put("\u00e5", "a");
            this.translitChars.put("\u0255", "c");
            this.translitChars.put("\u1ecd", "o");
            this.translitChars.put("\u1eaf", "a");
            this.translitChars.put("\u0192", "f");
            this.translitChars.put("\u01e3", "ae");
            this.translitChars.put("\ua761", "vy");
            this.translitChars.put("\ufb00", "ff");
            this.translitChars.put("\u1d89", "r");
            this.translitChars.put("\u00f4", "o");
            this.translitChars.put("\u01ff", "o");
            this.translitChars.put("\u1e73", "u");
            this.translitChars.put("\u0225", "z");
            this.translitChars.put("\u1e1f", "f");
            this.translitChars.put("\u1e13", "d");
            this.translitChars.put("\u0207", "e");
            this.translitChars.put("\u0215", "u");
            this.translitChars.put("\u043f", TtmlNode.TAG_P);
            this.translitChars.put("\u0235", "n");
            this.translitChars.put("\u02a0", "q");
            this.translitChars.put("\u1ea5", "a");
            this.translitChars.put("\u01e9", "k");
            this.translitChars.put("\u0129", "i");
            this.translitChars.put("\u1e75", "u");
            this.translitChars.put("\u0167", "t");
            this.translitChars.put("\u027e", "r");
            this.translitChars.put("\u0199", "k");
            this.translitChars.put("\u1e6b", "t");
            this.translitChars.put("\ua757", "q");
            this.translitChars.put("\u1ead", "a");
            this.translitChars.put("\u043d", "n");
            this.translitChars.put("\u0284", "j");
            this.translitChars.put("\u019a", "l");
            this.translitChars.put("\u1d82", "f");
            this.translitChars.put("\u0434", "d");
            this.translitChars.put("\u1d74", "s");
            this.translitChars.put("\ua783", "r");
            this.translitChars.put("\u1d8c", "v");
            this.translitChars.put("\u0275", "o");
            this.translitChars.put("\u1e09", "c");
            this.translitChars.put("\u1d64", "u");
            this.translitChars.put("\u1e91", "z");
            this.translitChars.put("\u1e79", "u");
            this.translitChars.put("\u0148", "n");
            this.translitChars.put("\u028d", "w");
            this.translitChars.put("\u1ea7", "a");
            this.translitChars.put("\u01c9", "lj");
            this.translitChars.put("\u0253", "b");
            this.translitChars.put("\u027c", "r");
            this.translitChars.put("\u00f2", "o");
            this.translitChars.put("\u1e98", "w");
            this.translitChars.put("\u0257", "d");
            this.translitChars.put("\ua73d", "ay");
            this.translitChars.put("\u01b0", "u");
            this.translitChars.put("\u1d80", "b");
            this.translitChars.put("\u01dc", "u");
            this.translitChars.put("\u1eb9", "e");
            this.translitChars.put("\u01e1", "a");
            this.translitChars.put("\u0265", "h");
            this.translitChars.put("\u1e4f", "o");
            this.translitChars.put("\u01d4", "u");
            this.translitChars.put("\u028e", "y");
            this.translitChars.put("\u0231", "o");
            this.translitChars.put("\u1ec7", "e");
            this.translitChars.put("\u1ebf", "e");
            this.translitChars.put("\u012d", "i");
            this.translitChars.put("\u2c78", "e");
            this.translitChars.put("\u1e6f", "t");
            this.translitChars.put("\u1d91", "d");
            this.translitChars.put("\u1e27", "h");
            this.translitChars.put("\u1e65", "s");
            this.translitChars.put("\u00eb", "e");
            this.translitChars.put("\u1d0d", "m");
            this.translitChars.put("\u00f6", "o");
            this.translitChars.put("\u00e9", "e");
            this.translitChars.put("\u0131", "i");
            this.translitChars.put("\u010f", "d");
            this.translitChars.put("\u1d6f", "m");
            this.translitChars.put("\u1ef5", "y");
            this.translitChars.put("\u044f", "ya");
            this.translitChars.put("\u0175", "w");
            this.translitChars.put("\u1ec1", "e");
            this.translitChars.put("\u1ee9", "u");
            this.translitChars.put("\u01b6", "z");
            this.translitChars.put("\u0135", "j");
            this.translitChars.put("\u1e0d", "d");
            this.translitChars.put("\u016d", "u");
            this.translitChars.put("\u029d", "j");
            this.translitChars.put("\u0436", "zh");
            this.translitChars.put("\u00ea", "e");
            this.translitChars.put("\u01da", "u");
            this.translitChars.put("\u0121", "g");
            this.translitChars.put("\u1e59", "r");
            this.translitChars.put("\u019e", "n");
            this.translitChars.put("\u044a", TtmlNode.ANONYMOUS_REGION_ID);
            this.translitChars.put("\u1e17", "e");
            this.translitChars.put("\u1e9d", "s");
            this.translitChars.put("\u1d81", "d");
            this.translitChars.put("\u0137", "k");
            this.translitChars.put("\u1d02", "ae");
            this.translitChars.put("\u0258", "e");
            this.translitChars.put("\u1ee3", "o");
            this.translitChars.put("\u1e3f", "m");
            this.translitChars.put("\ua730", "f");
            this.translitChars.put("\u0430", "a");
            this.translitChars.put("\u1eb5", "a");
            this.translitChars.put("\ua74f", "oo");
            this.translitChars.put("\u1d86", "m");
            this.translitChars.put("\u1d7d", TtmlNode.TAG_P);
            this.translitChars.put("\u0446", "ts");
            this.translitChars.put("\u1eef", "u");
            this.translitChars.put("\u2c6a", "k");
            this.translitChars.put("\u1e25", "h");
            this.translitChars.put("\u0163", "t");
            this.translitChars.put("\u1d71", TtmlNode.TAG_P);
            this.translitChars.put("\u1e41", "m");
            this.translitChars.put("\u00e1", "a");
            this.translitChars.put("\u1d0e", "n");
            this.translitChars.put("\ua75f", "v");
            this.translitChars.put("\u00e8", "e");
            this.translitChars.put("\u1d8e", "z");
            this.translitChars.put("\ua77a", "d");
            this.translitChars.put("\u1d88", TtmlNode.TAG_P);
            this.translitChars.put("\u043c", "m");
            this.translitChars.put("\u026b", "l");
            this.translitChars.put("\u1d22", "z");
            this.translitChars.put("\u0271", "m");
            this.translitChars.put("\u1e5d", "r");
            this.translitChars.put("\u1e7d", "v");
            this.translitChars.put("\u0169", "u");
            this.translitChars.put("\u00df", "ss");
            this.translitChars.put("\u0442", "t");
            this.translitChars.put("\u0125", "h");
            this.translitChars.put("\u1d75", "t");
            this.translitChars.put("\u0290", "z");
            this.translitChars.put("\u1e5f", "r");
            this.translitChars.put("\u0272", "n");
            this.translitChars.put("\u00e0", "a");
            this.translitChars.put("\u1e99", "y");
            this.translitChars.put("\u1ef3", "y");
            this.translitChars.put("\u1d14", "oe");
            this.translitChars.put("\u044b", "i");
            this.translitChars.put("\u2093", "x");
            this.translitChars.put("\u0217", "u");
            this.translitChars.put("\u2c7c", "j");
            this.translitChars.put("\u1eab", "a");
            this.translitChars.put("\u0291", "z");
            this.translitChars.put("\u1e9b", "s");
            this.translitChars.put("\u1e2d", "i");
            this.translitChars.put("\ua735", "ao");
            this.translitChars.put("\u0240", "z");
            this.translitChars.put("\u00ff", "y");
            this.translitChars.put("\u01dd", "e");
            this.translitChars.put("\u01ed", "o");
            this.translitChars.put("\u1d05", "d");
            this.translitChars.put("\u1d85", "l");
            this.translitChars.put("\u00f9", "u");
            this.translitChars.put("\u1ea1", "a");
            this.translitChars.put("\u1e05", "b");
            this.translitChars.put("\u1ee5", "u");
            this.translitChars.put("\u043a", "k");
            this.translitChars.put("\u1eb1", "a");
            this.translitChars.put("\u1d1b", "t");
            this.translitChars.put("\u01b4", "y");
            this.translitChars.put("\u2c66", "t");
            this.translitChars.put("\u0437", "z");
            this.translitChars.put("\u2c61", "l");
            this.translitChars.put("\u0237", "j");
            this.translitChars.put("\u1d76", "z");
            this.translitChars.put("\u1e2b", "h");
            this.translitChars.put("\u2c73", "w");
            this.translitChars.put("\u1e35", "k");
            this.translitChars.put("\u1edd", "o");
            this.translitChars.put("\u00ee", "i");
            this.translitChars.put("\u0123", "g");
            this.translitChars.put("\u0205", "e");
            this.translitChars.put("\u0227", "a");
            this.translitChars.put("\u1eb3", "a");
            this.translitChars.put("\u0449", "sch");
            this.translitChars.put("\u024b", "q");
            this.translitChars.put("\u1e6d", "t");
            this.translitChars.put("\ua778", "um");
            this.translitChars.put("\u1d04", "c");
            this.translitChars.put("\u1e8d", "x");
            this.translitChars.put("\u1ee7", "u");
            this.translitChars.put("\u1ec9", "i");
            this.translitChars.put("\u1d1a", "r");
            this.translitChars.put("\u015b", "s");
            this.translitChars.put("\ua74b", "o");
            this.translitChars.put("\u1ef9", "y");
            this.translitChars.put("\u1e61", "s");
            this.translitChars.put("\u01cc", "nj");
            this.translitChars.put("\u0201", "a");
            this.translitChars.put("\u1e97", "t");
            this.translitChars.put("\u013a", "l");
            this.translitChars.put("\u017e", "z");
            this.translitChars.put("\u1d7a", "th");
            this.translitChars.put("\u018c", "d");
            this.translitChars.put("\u0219", "s");
            this.translitChars.put("\u0161", "s");
            this.translitChars.put("\u1d99", "u");
            this.translitChars.put("\u1ebd", "e");
            this.translitChars.put("\u1e9c", "s");
            this.translitChars.put("\u0247", "e");
            this.translitChars.put("\u1e77", "u");
            this.translitChars.put("\u1ed1", "o");
            this.translitChars.put("\u023f", "s");
            this.translitChars.put("\u1d20", "v");
            this.translitChars.put("\ua76d", "is");
            this.translitChars.put("\u1d0f", "o");
            this.translitChars.put("\u025b", "e");
            this.translitChars.put("\u01fb", "a");
            this.translitChars.put("\ufb04", "ffl");
            this.translitChars.put("\u2c7a", "o");
            this.translitChars.put("\u020b", "i");
            this.translitChars.put("\u1d6b", "ue");
            this.translitChars.put("\u0221", "d");
            this.translitChars.put("\u2c6c", "z");
            this.translitChars.put("\u1e81", "w");
            this.translitChars.put("\u1d8f", "a");
            this.translitChars.put("\ua787", "t");
            this.translitChars.put("\u011f", "g");
            this.translitChars.put("\u0273", "n");
            this.translitChars.put("\u029b", "g");
            this.translitChars.put("\u1d1c", "u");
            this.translitChars.put("\u0444", "f");
            this.translitChars.put("\u1ea9", "a");
            this.translitChars.put("\u1e45", "n");
            this.translitChars.put("\u0268", "i");
            this.translitChars.put("\u1d19", "r");
            this.translitChars.put("\u01ce", "a");
            this.translitChars.put("\u017f", "s");
            this.translitChars.put("\u0443", "u");
            this.translitChars.put("\u022b", "o");
            this.translitChars.put("\u027f", "r");
            this.translitChars.put("\u01ad", "t");
            this.translitChars.put("\u1e2f", "i");
            this.translitChars.put("\u01fd", "ae");
            this.translitChars.put("\u2c71", "v");
            this.translitChars.put("\u0276", "oe");
            this.translitChars.put("\u1e43", "m");
            this.translitChars.put("\u017c", "z");
            this.translitChars.put("\u0115", "e");
            this.translitChars.put("\ua73b", "av");
            this.translitChars.put("\u1edf", "o");
            this.translitChars.put("\u1ec5", "e");
            this.translitChars.put("\u026c", "l");
            this.translitChars.put("\u1ecb", "i");
            this.translitChars.put("\u1d6d", "d");
            this.translitChars.put("\ufb06", "st");
            this.translitChars.put("\u1e37", "l");
            this.translitChars.put("\u0155", "r");
            this.translitChars.put("\u1d15", "ou");
            this.translitChars.put("\u0288", "t");
            this.translitChars.put("\u0101", "a");
            this.translitChars.put("\u044d", "e");
            this.translitChars.put("\u1e19", "e");
            this.translitChars.put("\u1d11", "o");
            this.translitChars.put("\u00e7", "c");
            this.translitChars.put("\u1d8a", "s");
            this.translitChars.put("\u1eb7", "a");
            this.translitChars.put("\u0173", "u");
            this.translitChars.put("\u1ea3", "a");
            this.translitChars.put("\u01e5", "g");
            this.translitChars.put("\u0440", "r");
            this.translitChars.put("\ua741", "k");
            this.translitChars.put("\u1e95", "z");
            this.translitChars.put("\u015d", "s");
            this.translitChars.put("\u1e15", "e");
            this.translitChars.put("\u0260", "g");
            this.translitChars.put("\ua749", "l");
            this.translitChars.put("\ua77c", "f");
            this.translitChars.put("\u1d8d", "x");
            this.translitChars.put("\u0445", "h");
            this.translitChars.put("\u01d2", "o");
            this.translitChars.put("\u0119", "e");
            this.translitChars.put("\u1ed5", "o");
            this.translitChars.put("\u01ab", "t");
            this.translitChars.put("\u01eb", "o");
            this.translitChars.put("i\u0307", "i");
            this.translitChars.put("\u1e47", "n");
            this.translitChars.put("\u0107", "c");
            this.translitChars.put("\u1d77", "g");
            this.translitChars.put("\u1e85", "w");
            this.translitChars.put("\u1e11", "d");
            this.translitChars.put("\u1e39", "l");
            this.translitChars.put("\u0447", "ch");
            this.translitChars.put("\u0153", "oe");
            this.translitChars.put("\u1d73", "r");
            this.translitChars.put("\u013c", "l");
            this.translitChars.put("\u0211", "r");
            this.translitChars.put("\u022d", "o");
            this.translitChars.put("\u1d70", "n");
            this.translitChars.put("\u1d01", "ae");
            this.translitChars.put("\u0140", "l");
            this.translitChars.put("\u00e4", "a");
            this.translitChars.put("\u01a5", TtmlNode.TAG_P);
            this.translitChars.put("\u1ecf", "o");
            this.translitChars.put("\u012f", "i");
            this.translitChars.put("\u0213", "r");
            this.translitChars.put("\u01c6", "dz");
            this.translitChars.put("\u1e21", "g");
            this.translitChars.put("\u1e7b", "u");
            this.translitChars.put("\u014d", "o");
            this.translitChars.put("\u013e", "l");
            this.translitChars.put("\u1e83", "w");
            this.translitChars.put("\u021b", "t");
            this.translitChars.put("\u0144", "n");
            this.translitChars.put("\u024d", "r");
            this.translitChars.put("\u0203", "a");
            this.translitChars.put("\u00fc", "u");
            this.translitChars.put("\ua781", "l");
            this.translitChars.put("\u1d10", "o");
            this.translitChars.put("\u1edb", "o");
            this.translitChars.put("\u1d03", "b");
            this.translitChars.put("\u0279", "r");
            this.translitChars.put("\u1d72", "r");
            this.translitChars.put("\u028f", "y");
            this.translitChars.put("\u1d6e", "f");
            this.translitChars.put("\u2c68", "h");
            this.translitChars.put("\u014f", "o");
            this.translitChars.put("\u00fa", "u");
            this.translitChars.put("\u1e5b", "r");
            this.translitChars.put("\u02ae", "h");
            this.translitChars.put("\u00f3", "o");
            this.translitChars.put("\u016f", "u");
            this.translitChars.put("\u1ee1", "o");
            this.translitChars.put("\u1e55", TtmlNode.TAG_P);
            this.translitChars.put("\u1d96", "i");
            this.translitChars.put("\u1ef1", "u");
            this.translitChars.put("\u00e3", "a");
            this.translitChars.put("\u1d62", "i");
            this.translitChars.put("\u1e71", "t");
            this.translitChars.put("\u1ec3", "e");
            this.translitChars.put("\u1eed", "u");
            this.translitChars.put("\u00ed", "i");
            this.translitChars.put("\u0254", "o");
            this.translitChars.put("\u0441", "s");
            this.translitChars.put("\u0439", "i");
            this.translitChars.put("\u027a", "r");
            this.translitChars.put("\u0262", "g");
            this.translitChars.put("\u0159", "r");
            this.translitChars.put("\u1e96", "h");
            this.translitChars.put("\u0171", "u");
            this.translitChars.put("\u020d", "o");
            this.translitChars.put("\u0448", "sh");
            this.translitChars.put("\u1e3b", "l");
            this.translitChars.put("\u1e23", "h");
            this.translitChars.put("\u0236", "t");
            this.translitChars.put("\u0146", "n");
            this.translitChars.put("\u1d92", "e");
            this.translitChars.put("\u00ec", "i");
            this.translitChars.put("\u1e89", "w");
            this.translitChars.put("\u0431", "b");
            this.translitChars.put("\u0113", "e");
            this.translitChars.put("\u1d07", "e");
            this.translitChars.put("\u0142", "l");
            this.translitChars.put("\u1ed9", "o");
            this.translitChars.put("\u026d", "l");
            this.translitChars.put("\u1e8f", "y");
            this.translitChars.put("\u1d0a", "j");
            this.translitChars.put("\u1e31", "k");
            this.translitChars.put("\u1e7f", "v");
            this.translitChars.put("\u0229", "e");
            this.translitChars.put("\u00e2", "a");
            this.translitChars.put("\u015f", "s");
            this.translitChars.put("\u0157", "r");
            this.translitChars.put("\u028b", "v");
            this.translitChars.put("\u2090", "a");
            this.translitChars.put("\u2184", "c");
            this.translitChars.put("\u1d93", "e");
            this.translitChars.put("\u0270", "m");
            this.translitChars.put("\u0435", "e");
            this.translitChars.put("\u1d21", "w");
            this.translitChars.put("\u020f", "o");
            this.translitChars.put("\u010d", "c");
            this.translitChars.put("\u01f5", "g");
            this.translitChars.put("\u0109", "c");
            this.translitChars.put("\u044e", "yu");
            this.translitChars.put("\u1d97", "o");
            this.translitChars.put("\ua743", "k");
            this.translitChars.put("\ua759", "q");
            this.translitChars.put("\u0433", "g");
            this.translitChars.put("\u1e51", "o");
            this.translitChars.put("\ua731", "s");
            this.translitChars.put("\u1e53", "o");
            this.translitChars.put("\u021f", "h");
            this.translitChars.put("\u0151", "o");
            this.translitChars.put("\ua729", "tz");
            this.translitChars.put("\u1ebb", "e");
            this.translitChars.put("\u043e", "o");
        }
        StringBuilder dst = new StringBuilder(src.length());
        int len = src.length();
        for (int a = QUANTITY_OTHER; a < len; a += QUANTITY_ZERO) {
            String ch = src.substring(a, a + QUANTITY_ZERO);
            String tch = (String) this.translitChars.get(ch);
            if (tch != null) {
                dst.append(tch);
            } else {
                dst.append(ch);
            }
        }
        return dst.toString();
    }
}
