package org.telegram.messenger;

public class BuildVars {
    public static String APP_HASH;
    public static int APP_ID;
    public static String BING_SEARCH_KEY;
    public static int BUILD_VERSION;
    public static String BUILD_VERSION_STRING;
    public static boolean DEBUG_VERSION;
    public static String FOURSQUARE_API_ID;
    public static String FOURSQUARE_API_KEY;
    public static String FOURSQUARE_API_VERSION;
    public static String GCM_SENDER_ID;
    public static String HOCKEY_APP_HASH;
    public static String HOCKEY_APP_HASH_DEBUG;
    public static String SEND_LOGS_EMAIL;

    static {
        DEBUG_VERSION = false;
        BUILD_VERSION = 821;
        BUILD_VERSION_STRING = "3.10";
        APP_ID = 13746;
        APP_HASH = "6fcabf15d069f2377299eb1b395206e6";
        HOCKEY_APP_HASH = "12345678901234567890123456789012";
        HOCKEY_APP_HASH_DEBUG = "12345678901234567890123456789012";
        GCM_SENDER_ID = "760348033672";
        SEND_LOGS_EMAIL = "email@gmail.com";
        BING_SEARCH_KEY = TtmlNode.ANONYMOUS_REGION_ID;
        FOURSQUARE_API_KEY = TtmlNode.ANONYMOUS_REGION_ID;
        FOURSQUARE_API_ID = TtmlNode.ANONYMOUS_REGION_ID;
        FOURSQUARE_API_VERSION = "20150326";
    }
}
