package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;

public class CallingCodeInfo {
    public String callingCode;
    public ArrayList<String> countries;
    public ArrayList<String> intlPrefixes;
    public ArrayList<RuleSet> ruleSets;
    public ArrayList<String> trunkPrefixes;

    public CallingCodeInfo() {
        this.countries = new ArrayList();
        this.callingCode = TtmlNode.ANONYMOUS_REGION_ID;
        this.trunkPrefixes = new ArrayList();
        this.intlPrefixes = new ArrayList();
        this.ruleSets = new ArrayList();
    }

    String matchingAccessCode(String str) {
        Iterator i$ = this.intlPrefixes.iterator();
        while (i$.hasNext()) {
            String code = (String) i$.next();
            if (str.startsWith(code)) {
                return code;
            }
        }
        return null;
    }

    String matchingTrunkCode(String str) {
        Iterator i$ = this.trunkPrefixes.iterator();
        while (i$.hasNext()) {
            String code = (String) i$.next();
            if (str.startsWith(code)) {
                return code;
            }
        }
        return null;
    }

    String format(String orig) {
        String str = orig;
        String trunkPrefix = null;
        String intlPrefix = null;
        if (str.startsWith(this.callingCode)) {
            intlPrefix = this.callingCode;
            str = str.substring(intlPrefix.length());
        } else {
            String trunk = matchingTrunkCode(str);
            if (trunk != null) {
                trunkPrefix = trunk;
                str = str.substring(trunkPrefix.length());
            }
        }
        Iterator i$ = this.ruleSets.iterator();
        while (i$.hasNext()) {
            String phone = ((RuleSet) i$.next()).format(str, intlPrefix, trunkPrefix, true);
            if (phone != null) {
                return phone;
            }
        }
        i$ = this.ruleSets.iterator();
        while (i$.hasNext()) {
            phone = ((RuleSet) i$.next()).format(str, intlPrefix, trunkPrefix, false);
            if (phone != null) {
                return phone;
            }
        }
        if (intlPrefix == null || str.length() == 0) {
            return orig;
        }
        return String.format("%s %s", new Object[]{intlPrefix, str});
    }

    boolean isValidPhoneNumber(String orig) {
        String str = orig;
        String trunkPrefix = null;
        String intlPrefix = null;
        if (str.startsWith(this.callingCode)) {
            intlPrefix = this.callingCode;
            str = str.substring(intlPrefix.length());
        } else {
            String trunk = matchingTrunkCode(str);
            if (trunk != null) {
                trunkPrefix = trunk;
                str = str.substring(trunkPrefix.length());
            }
        }
        Iterator i$ = this.ruleSets.iterator();
        while (i$.hasNext()) {
            if (((RuleSet) i$.next()).isValid(str, intlPrefix, trunkPrefix, true)) {
                return true;
            }
        }
        i$ = this.ruleSets.iterator();
        while (i$.hasNext()) {
            if (((RuleSet) i$.next()).isValid(str, intlPrefix, trunkPrefix, false)) {
                return true;
            }
        }
        return false;
    }
}
