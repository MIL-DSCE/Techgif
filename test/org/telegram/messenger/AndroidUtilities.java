package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class AndroidUtilities {
    public static final int FLAG_TAG_ALL = 7;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static Pattern WEB_URL;
    private static int adjustOwnerClassGuid;
    private static RectF bitmapRect;
    private static final Object callLock;
    public static float density;
    public static DisplayMetrics displayMetrics;
    public static Point displaySize;
    private static Boolean isTablet;
    public static int leftBaseline;
    public static Integer photoSize;
    private static int prevOrientation;
    private static Paint roundPaint;
    private static final Object smsLock;
    public static int statusBarHeight;
    private static final Hashtable<String, Typeface> typefaceCache;
    public static boolean usingHardwareInput;
    private static boolean waitingForCall;
    private static boolean waitingForSms;

    /* renamed from: org.telegram.messenger.AndroidUtilities.1 */
    static class C04231 implements OnClickListener {
        final /* synthetic */ BaseFragment val$fragment;

        C04231(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                this.val$fragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.AndroidUtilities.3 */
    static class C04243 implements OnClickListener {
        final /* synthetic */ EncryptedChat val$encryptedChat;
        final /* synthetic */ NumberPicker val$numberPicker;

        C04243(EncryptedChat encryptedChat, NumberPicker numberPicker) {
            this.val$encryptedChat = encryptedChat;
            this.val$numberPicker = numberPicker;
        }

        public void onClick(DialogInterface dialog, int which) {
            int oldValue = this.val$encryptedChat.ttl;
            which = this.val$numberPicker.getValue();
            if (which >= 0 && which < 16) {
                this.val$encryptedChat.ttl = which;
            } else if (which == 16) {
                this.val$encryptedChat.ttl = 30;
            } else if (which == 17) {
                this.val$encryptedChat.ttl = 60;
            } else if (which == 18) {
                this.val$encryptedChat.ttl = 3600;
            } else if (which == 19) {
                this.val$encryptedChat.ttl = 86400;
            } else if (which == 20) {
                this.val$encryptedChat.ttl = 604800;
            }
            if (oldValue != this.val$encryptedChat.ttl) {
                SecretChatHelper.getInstance().sendTTLMessage(this.val$encryptedChat, null);
                MessagesStorage.getInstance().updateEncryptedChatTTL(this.val$encryptedChat);
            }
        }
    }

    /* renamed from: org.telegram.messenger.AndroidUtilities.2 */
    static class C16722 implements Formatter {
        C16722() {
        }

        public String format(int value) {
            if (value == 0) {
                return LocaleController.getString("ShortMessageLifetimeForever", C0691R.string.ShortMessageLifetimeForever);
            }
            if (value >= AndroidUtilities.FLAG_TAG_BR && value < 16) {
                return AndroidUtilities.formatTTLString(value);
            }
            if (value == 16) {
                return AndroidUtilities.formatTTLString(30);
            }
            if (value == 17) {
                return AndroidUtilities.formatTTLString(60);
            }
            if (value == 18) {
                return AndroidUtilities.formatTTLString(3600);
            }
            if (value == 19) {
                return AndroidUtilities.formatTTLString(86400);
            }
            if (value == 20) {
                return AndroidUtilities.formatTTLString(604800);
            }
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    /* renamed from: org.telegram.messenger.AndroidUtilities.4 */
    static class C16734 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$num;
        final /* synthetic */ View val$view;
        final /* synthetic */ float val$x;

        C16734(View view, int i, float f) {
            this.val$view = view;
            this.val$num = i;
            this.val$x = f;
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.shakeView(this.val$view, this.val$num == 5 ? 0.0f : -this.val$x, this.val$num + AndroidUtilities.FLAG_TAG_BR);
        }
    }

    /* renamed from: org.telegram.messenger.AndroidUtilities.5 */
    static class C16745 extends CrashManagerListener {
        C16745() {
        }

        public boolean includeDeviceData() {
            return true;
        }
    }

    static {
        typefaceCache = new Hashtable();
        prevOrientation = -10;
        waitingForSms = false;
        waitingForCall = false;
        smsLock = new Object();
        callLock = new Object();
        statusBarHeight = 0;
        density = TouchHelperCallback.ALPHA_FULL;
        displaySize = new Point();
        photoSize = null;
        displayMetrics = new DisplayMetrics();
        isTablet = null;
        adjustOwnerClassGuid = 0;
        WEB_URL = null;
        try {
            String GOOD_IRI_CHAR = "a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef";
            String IRI = "[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}";
            String GOOD_GTLD_CHAR = "a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef";
            String GTLD = "[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}";
            String HOST_NAME = "([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}\\.)+[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}";
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + Pattern.compile("(([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}\\.)+[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}|" + Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))") + ")") + ")" + "(?:\\:\\d{1,5})?)" + "(\\/(?:(?:[" + "a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~" + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?" + "(?:\\b|$)");
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        density = ApplicationLoader.applicationContext.getResources().getDisplayMetrics().density;
        leftBaseline = isTablet() ? 80 : 72;
        checkDisplaySize();
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int bitmapColor = ViewCompat.MEASURED_STATE_MASK;
        int[] result = new int[FLAG_TAG_BOLD];
        try {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    Bitmap b = Bitmaps.createScaledBitmap(bitmap, FLAG_TAG_BR, FLAG_TAG_BR, true);
                    if (b != null) {
                        bitmapColor = b.getPixel(0, 0);
                        b.recycle();
                    }
                }
            } else if (drawable instanceof ColorDrawable) {
                bitmapColor = ((ColorDrawable) drawable).getColor();
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        double[] hsv = rgbToHsv((bitmapColor >> 16) & NalUnitUtil.EXTENDED_SAR, (bitmapColor >> 8) & NalUnitUtil.EXTENDED_SAR, bitmapColor & NalUnitUtil.EXTENDED_SAR);
        hsv[FLAG_TAG_BR] = Math.min(1.0d, (hsv[FLAG_TAG_BR] + 0.05d) + (0.1d * (1.0d - hsv[FLAG_TAG_BR])));
        hsv[FLAG_TAG_BOLD] = Math.max(0.0d, hsv[FLAG_TAG_BOLD] * 0.65d);
        int[] rgb = hsvToRgb(hsv[0], hsv[FLAG_TAG_BR], hsv[FLAG_TAG_BOLD]);
        result[0] = Color.argb(102, rgb[0], rgb[FLAG_TAG_BR], rgb[FLAG_TAG_BOLD]);
        result[FLAG_TAG_BR] = Color.argb(136, rgb[0], rgb[FLAG_TAG_BR], rgb[FLAG_TAG_BOLD]);
        return result;
    }

    private static double[] rgbToHsv(int r, int g, int b) {
        double h;
        double rf = ((double) r) / 255.0d;
        double gf = ((double) g) / 255.0d;
        double bf = ((double) b) / 255.0d;
        double max = (rf <= gf || rf <= bf) ? gf > bf ? gf : bf : rf;
        double min = (rf >= gf || rf >= bf) ? gf < bf ? gf : bf : rf;
        double d = max - min;
        double s = max == 0.0d ? 0.0d : d / max;
        if (max == min) {
            h = 0.0d;
        } else {
            if (rf > gf && rf > bf) {
                h = ((gf - bf) / d) + ((double) (gf < bf ? 6 : 0));
            } else if (gf > bf) {
                h = ((bf - rf) / d) + 2.0d;
            } else {
                h = ((rf - gf) / d) + 4.0d;
            }
            h /= 6.0d;
        }
        return new double[]{h, s, max};
    }

    private static int[] hsvToRgb(double h, double s, double v) {
        double r = 0.0d;
        double g = 0.0d;
        double b = 0.0d;
        double i = (double) ((int) Math.floor(6.0d * h));
        double f = (6.0d * h) - i;
        double p = v * (1.0d - s);
        double q = v * (1.0d - (f * s));
        double t = v * (1.0d - ((1.0d - f) * s));
        switch (((int) i) % 6) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                r = v;
                g = t;
                b = p;
                break;
            case FLAG_TAG_BR /*1*/:
                r = q;
                g = v;
                b = p;
                break;
            case FLAG_TAG_BOLD /*2*/:
                r = p;
                g = v;
                b = t;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                r = p;
                g = q;
                b = v;
                break;
            case FLAG_TAG_COLOR /*4*/:
                r = t;
                g = p;
                b = v;
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                r = v;
                g = p;
                b = q;
                break;
        }
        r18 = new int[3];
        r18[0] = (int) (255.0d * r);
        r18[FLAG_TAG_BR] = (int) (255.0d * g);
        r18[FLAG_TAG_BOLD] = (int) (255.0d * b);
        return r18;
    }

    public static void requestAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(16);
            adjustOwnerClassGuid = classGuid;
        }
    }

    public static void removeAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == classGuid) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static boolean isGoogleMapsInstalled(BaseFragment fragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (NameNotFoundException e) {
            if (fragment.getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setMessage("Install Google Maps?");
            builder.setCancelable(true);
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C04231(fragment));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
            fragment.showDialog(builder.create());
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isInternalUri(android.net.Uri r6) {
        /*
        r2 = 0;
        r1 = r6.getPath();
        if (r1 != 0) goto L_0x0009;
    L_0x0007:
        return r2;
    L_0x0008:
        r1 = r0;
    L_0x0009:
        r0 = org.telegram.messenger.Utilities.readlink(r1);
        if (r0 == 0) goto L_0x0015;
    L_0x000f:
        r3 = r0.equals(r1);
        if (r3 == 0) goto L_0x0008;
    L_0x0015:
        if (r1 == 0) goto L_0x0007;
    L_0x0017:
        r3 = r1.toLowerCase();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "/data/data/";
        r4 = r4.append(r5);
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = r5.getPackageName();
        r4 = r4.append(r5);
        r5 = "/files";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3 = r3.contains(r4);
        if (r3 == 0) goto L_0x0007;
    L_0x0040:
        r2 = 1;
        goto L_0x0007;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isInternalUri(android.net.Uri):boolean");
    }

    public static void lockOrientation(Activity activity) {
        if (activity != null && prevOrientation == -10) {
            try {
                prevOrientation = activity.getRequestedOrientation();
                WindowManager manager = (WindowManager) activity.getSystemService("window");
                if (manager != null && manager.getDefaultDisplay() != null) {
                    int rotation = manager.getDefaultDisplay().getRotation();
                    int orientation = activity.getResources().getConfiguration().orientation;
                    if (rotation == 3) {
                        if (orientation == FLAG_TAG_BR) {
                            activity.setRequestedOrientation(FLAG_TAG_BR);
                        } else {
                            activity.setRequestedOrientation(8);
                        }
                    } else if (rotation == FLAG_TAG_BR) {
                        if (orientation == FLAG_TAG_BR) {
                            activity.setRequestedOrientation(9);
                        } else {
                            activity.setRequestedOrientation(0);
                        }
                    } else if (rotation == 0) {
                        if (orientation == FLAG_TAG_BOLD) {
                            activity.setRequestedOrientation(0);
                        } else {
                            activity.setRequestedOrientation(FLAG_TAG_BR);
                        }
                    } else if (orientation == FLAG_TAG_BOLD) {
                        activity.setRequestedOrientation(8);
                    } else {
                        activity.setRequestedOrientation(9);
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            try {
                if (prevOrientation != -10) {
                    activity.setRequestedOrientation(prevOrientation);
                    prevOrientation = -10;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static Typeface getTypeface(String assetPath) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    typefaceCache.put(assetPath, Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath));
                } catch (Exception e) {
                    FileLog.m11e("Typefaces", "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    typeface = null;
                }
            }
            typeface = (Typeface) typefaceCache.get(assetPath);
        }
        return typeface;
    }

    public static boolean isWaitingForSms() {
        boolean value;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
        }
    }

    public static boolean isWaitingForCall() {
        boolean value;
        synchronized (callLock) {
            value = waitingForCall;
        }
        return value;
    }

    public static void setWaitingForCall(boolean value) {
        synchronized (callLock) {
            waitingForCall = value;
        }
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            try {
                ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, FLAG_TAG_BR);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static boolean isKeyboardShowed(View view) {
        boolean z = false;
        if (view != null) {
            try {
                z = ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        return z;
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static File getCacheDir() {
        File file;
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        if (state == null || state.startsWith("mounted")) {
            try {
                file = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
        }
        try {
            file = ApplicationLoader.applicationContext.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Throwable e22) {
            FileLog.m13e("tmessages", e22);
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static int dp(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.ceil((double) (density * value));
    }

    public static int compare(int lhs, int rhs) {
        if (lhs == rhs) {
            return 0;
        }
        if (lhs > rhs) {
            return FLAG_TAG_BR;
        }
        return -1;
    }

    public static float dpf2(float value) {
        if (value == 0.0f) {
            return 0.0f;
        }
        return density * value;
    }

    public static void checkDisplaySize() {
        boolean z = true;
        try {
            Configuration configuration = ApplicationLoader.applicationContext.getResources().getConfiguration();
            if (configuration.keyboard == FLAG_TAG_BR || configuration.hardKeyboardHidden != FLAG_TAG_BR) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    FileLog.m11e("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        return (isX ? displayMetrics.xdpi : displayMetrics.ydpi) * (cm / 2.54f);
    }

    public static long makeBroadcastId(int id) {
        return 4294967296L | (((long) id) & 4294967295L);
    }

    public static int getMyLayerVersion(int layer) {
        return SupportMenu.USER_MASK & layer;
    }

    public static int getPeerLayerVersion(int layer) {
        return (layer >> 16) & SupportMenu.USER_MASK;
    }

    public static int setMyLayerVersion(int layer, int version) {
        return (SupportMenu.CATEGORY_MASK & layer) | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return (SupportMenu.USER_MASK & layer) | (version << 16);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(C0691R.bool.isTablet));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 700.0f;
    }

    public static int getMinTabletSide() {
        int leftSide;
        if (isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int maxSide = Math.max(displaySize.x, displaySize.y);
            leftSide = (maxSide * 35) / 100;
            if (leftSide < dp(320.0f)) {
                leftSide = dp(320.0f);
            }
            return Math.min(smallSide, maxSide - leftSide);
        }
        smallSide = Math.min(displaySize.x, displaySize.y);
        leftSide = (smallSide * 35) / 100;
        if (leftSide < dp(320.0f)) {
            leftSide = dp(320.0f);
        }
        return smallSide - leftSide;
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            if (VERSION.SDK_INT >= 16) {
                photoSize = Integer.valueOf(1280);
            } else {
                photoSize = Integer.valueOf(800);
            }
        }
        return photoSize.intValue();
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return LocaleController.formatPluralString("Seconds", ttl);
        }
        if (ttl < 3600) {
            return LocaleController.formatPluralString("Minutes", ttl / 60);
        }
        if (ttl < 86400) {
            return LocaleController.formatPluralString("Hours", (ttl / 60) / 60);
        }
        if (ttl < 604800) {
            return LocaleController.formatPluralString("Days", ((ttl / 60) / 60) / 24);
        }
        int days = ((ttl / 60) / 60) / 24;
        if (ttl % FLAG_TAG_ALL == 0) {
            return LocaleController.formatPluralString("Weeks", days / FLAG_TAG_ALL);
        }
        Object[] objArr = new Object[FLAG_TAG_BOLD];
        objArr[0] = LocaleController.formatPluralString("Weeks", days / FLAG_TAG_ALL);
        objArr[FLAG_TAG_BR] = LocaleController.formatPluralString("Days", days % FLAG_TAG_ALL);
        return String.format("%s %s", objArr);
    }

    public static Builder buildTTLAlert(Context context, EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", C0691R.string.MessageLifetime));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 3600) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 86400) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 604800) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(0);
        }
        numberPicker.setFormatter(new C16722());
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", C0691R.string.Done), new C04243(encryptedChat, numberPicker));
        return builder;
    }

    public static void clearCursorDrawable(EditText editText) {
        if (editText != null) {
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.setInt(editText, 0);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public static void setProgressBarAnimationDuration(ProgressBar progressBar, int duration) {
        if (progressBar != null) {
            try {
                Field mCursorDrawableRes = ProgressBar.class.getDeclaredField("mDuration");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.setInt(progressBar, duration);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    private static Intent createShortcutIntent(long did, boolean forDelete) {
        Intent intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        int lower_id = (int) did;
        int high_id = (int) (did >> 32);
        User user = null;
        Chat chat = null;
        if (lower_id == 0) {
            intent.putExtra("encId", high_id);
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
            if (encryptedChat == null) {
                return null;
            }
            user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
        } else if (lower_id > 0) {
            intent.putExtra("userId", lower_id);
            user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
        } else if (lower_id >= 0) {
            return null;
        } else {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
            String str = "chatId";
            intent.putExtra(r25, -lower_id);
        }
        if (user == null && chat == null) {
            return null;
        }
        String name;
        TLObject photo = null;
        if (user != null) {
            name = ContactsController.formatName(user.first_name, user.last_name);
            if (user.photo != null) {
                photo = user.photo.photo_small;
            }
        } else {
            name = chat.title;
            if (chat.photo != null) {
                photo = chat.photo.photo_small;
            }
        }
        intent.setAction("com.tmessages.openchat" + did);
        intent.addFlags(67108864);
        Intent addIntent = new Intent();
        addIntent.putExtra("android.intent.extra.shortcut.INTENT", intent);
        addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
        addIntent.putExtra("duplicate", false);
        if (forDelete) {
            return addIntent;
        }
        Bitmap bitmap = null;
        if (photo != null) {
            try {
                bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                if (bitmap != null) {
                    int size = dp(58.0f);
                    Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                    result.eraseColor(0);
                    Canvas canvas = new Canvas(result);
                    Shader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                    if (roundPaint == null) {
                        roundPaint = new Paint(FLAG_TAG_BR);
                        bitmapRect = new RectF();
                    }
                    float scale = ((float) size) / ((float) bitmap.getWidth());
                    canvas.save();
                    canvas.scale(scale, scale);
                    roundPaint.setShader(bitmapShader);
                    bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
                    canvas.drawRoundRect(bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), roundPaint);
                    canvas.restore();
                    Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0691R.drawable.book_logo);
                    int w = dp(15.0f);
                    int left = (size - w) - dp(2.0f);
                    int top = (size - w) - dp(2.0f);
                    drawable.setBounds(left, top, left + w, top + w);
                    drawable.draw(canvas);
                    try {
                        canvas.setBitmap(null);
                    } catch (Exception e) {
                    }
                    bitmap = result;
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
        }
        if (bitmap != null) {
            addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap);
            return addIntent;
        } else if (user != null) {
            if (user.bot) {
                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0691R.drawable.book_bot));
                return addIntent;
            }
            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0691R.drawable.book_user));
            return addIntent;
        } else if (chat == null) {
            return addIntent;
        } else {
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0691R.drawable.book_group));
                return addIntent;
            }
            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0691R.drawable.book_channel));
            return addIntent;
        }
    }

    public static void installShortcut(long did) {
        try {
            Intent addIntent = createShortcutIntent(did, false);
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static void uninstallShortcut(long did) {
        try {
            Intent addIntent = createShortcutIntent(did, true);
            addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static int getViewInset(View view) {
        int i = 0;
        if (!(view == null || VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight)) {
            try {
                Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField.setAccessible(true);
                Object mAttachInfo = mAttachInfoField.get(view);
                if (mAttachInfo != null) {
                    Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                    mStableInsetsField.setAccessible(true);
                    i = ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        return i;
    }

    public static Point getRealScreenSize() {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    size.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Throwable e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.m13e("tmessages", e);
                }
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        return size;
    }

    public static CharSequence getTrimmedString(CharSequence src) {
        if (!(src == null || src.length() == 0)) {
            while (src.length() > 0 && (src.charAt(0) == '\n' || src.charAt(0) == ' ')) {
                src = src.subSequence(FLAG_TAG_BR, src.length());
            }
            while (src.length() > 0 && (src.charAt(src.length() - 1) == '\n' || src.charAt(src.length() - 1) == ' ')) {
                src = src.subSequence(0, src.length() - 1);
            }
        }
        return src;
    }

    public static void setListViewEdgeEffectColor(AbsListView listView, int color) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field field = AbsListView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(listView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                field = AbsListView.class.getDeclaredField("mEdgeGlowBottom");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(listView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void clearDrawableAnimation(View view) {
        if (VERSION.SDK_INT >= 21 && view != null) {
            Drawable drawable;
            if (view instanceof ListView) {
                drawable = ((ListView) view).getSelector();
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            drawable = view.getBackground();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                drawable.jumpToCurrentState();
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, FLAG_TAG_ALL);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag) {
        try {
            int start;
            int end;
            int a;
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & FLAG_TAG_BR) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("<br>");
                    if (start != -1) {
                        stringBuilder.replace(start, start + FLAG_TAG_COLOR, "\n");
                    } else {
                        while (true) {
                            break;
                            stringBuilder.replace(start, start + 5, "\n");
                        }
                    }
                }
                start = stringBuilder.indexOf("<br/>");
                if (start == -1) {
                    break;
                }
                stringBuilder.replace(start, start + 5, "\n");
            }
            ArrayList<Integer> bolds = new ArrayList();
            if ((flag & FLAG_TAG_BOLD) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("<b>");
                    if (start == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 3, TtmlNode.ANONYMOUS_REGION_ID);
                    end = stringBuilder.indexOf("</b>");
                    if (end == -1) {
                        end = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(end, end + FLAG_TAG_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                    bolds.add(Integer.valueOf(start));
                    bolds.add(Integer.valueOf(end));
                }
            }
            ArrayList<Integer> colors = new ArrayList();
            if ((flag & FLAG_TAG_COLOR) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("<c#");
                    if (start == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + FLAG_TAG_BOLD, TtmlNode.ANONYMOUS_REGION_ID);
                    end = stringBuilder.indexOf(">", start);
                    int color = Color.parseColor(stringBuilder.substring(start, end));
                    stringBuilder.replace(start, end + FLAG_TAG_BR, TtmlNode.ANONYMOUS_REGION_ID);
                    end = stringBuilder.indexOf("</c>");
                    stringBuilder.replace(end, end + FLAG_TAG_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                    colors.add(Integer.valueOf(start));
                    colors.add(Integer.valueOf(end));
                    colors.add(Integer.valueOf(color));
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (a = 0; a < bolds.size() / FLAG_TAG_BOLD; a += FLAG_TAG_BR) {
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer) bolds.get(a * FLAG_TAG_BOLD)).intValue(), ((Integer) bolds.get((a * FLAG_TAG_BOLD) + FLAG_TAG_BR)).intValue(), 33);
            }
            for (a = 0; a < colors.size() / 3; a += FLAG_TAG_BR) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(((Integer) colors.get((a * 3) + FLAG_TAG_BOLD)).intValue()), ((Integer) colors.get(a * 3)).intValue(), ((Integer) colors.get((a * 3) + FLAG_TAG_BR)).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return new SpannableStringBuilder(str);
        }
    }

    public static boolean needShowPasscode(boolean reset) {
        boolean wasInBackground = ForegroundDetector.getInstance().isWasInBackground(reset);
        if (reset) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return UserConfig.passcodeHash.length() > 0 && wasInBackground && (UserConfig.appLocked || !(UserConfig.autoLockIn == 0 || UserConfig.lastPauseTime == 0 || UserConfig.appLocked || UserConfig.lastPauseTime + UserConfig.autoLockIn > ConnectionsManager.getInstance().getCurrentTime()));
    }

    public static void shakeView(View view, float x, int num) {
        if (num == 6) {
            view.setTranslationX(0.0f);
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[FLAG_TAG_BR];
        float[] fArr = new float[FLAG_TAG_BR];
        fArr[0] = (float) dp(x);
        animatorArr[0] = ObjectAnimator.ofFloat(view, "translationX", fArr);
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(50);
        animatorSet.addListener(new C16734(view, num, x));
        animatorSet.start();
    }

    public static void checkForCrashes(Activity context) {
        CrashManager.register(context, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, new C16745());
    }

    public static void checkForUpdates(Activity context) {
        if (BuildVars.DEBUG_VERSION) {
            UpdateManager.register(context, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH);
        }
    }

    public static void unregisterUpdates() {
        if (BuildVars.DEBUG_VERSION) {
            UpdateManager.unregister();
        }
    }

    public static void addToClipboard(CharSequence str) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static void addMediaToGallery(String fromPath) {
        if (fromPath != null) {
            addMediaToGallery(Uri.fromFile(new File(fromPath)));
        }
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri != null) {
            try {
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                mediaScanIntent.setData(uri);
                ApplicationLoader.applicationContext.sendBroadcast(mediaScanIntent);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    private static File getAlbumDir() {
        if (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return FileLoader.getInstance().getDirectory(FLAG_TAG_COLOR);
        }
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), LocaleController.getString("AppName", C0691R.string.AppName));
            if (storageDir.mkdirs() || storageDir.exists()) {
                return storageDir;
            }
            FileLog.m10d("tmessages", "failed to create directory");
            return null;
        }
        FileLog.m10d("tmessages", "External storage is not mounted READ/WRITE.");
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    public static java.lang.String getPath(android.net.Uri r14) {
        /*
        r9 = 0;
        r12 = 1;
        r10 = 0;
        r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00f7 }
        r13 = 19;
        if (r11 < r13) goto L_0x004e;
    L_0x0009:
        r4 = r12;
    L_0x000a:
        if (r4 == 0) goto L_0x00cf;
    L_0x000c:
        r11 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00f7 }
        r11 = android.provider.DocumentsContract.isDocumentUri(r11, r14);	 Catch:{ Exception -> 0x00f7 }
        if (r11 == 0) goto L_0x00cf;
    L_0x0014:
        r11 = isExternalStorageDocument(r14);	 Catch:{ Exception -> 0x00f7 }
        if (r11 == 0) goto L_0x0050;
    L_0x001a:
        r1 = android.provider.DocumentsContract.getDocumentId(r14);	 Catch:{ Exception -> 0x00f7 }
        r10 = ":";
        r7 = r1.split(r10);	 Catch:{ Exception -> 0x00f7 }
        r10 = 0;
        r8 = r7[r10];	 Catch:{ Exception -> 0x00f7 }
        r10 = "primary";
        r10 = r10.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x00f7 }
        if (r10 == 0) goto L_0x004d;
    L_0x002f:
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00f7 }
        r10.<init>();	 Catch:{ Exception -> 0x00f7 }
        r11 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x00f7 }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x00f7 }
        r11 = "/";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x00f7 }
        r11 = 1;
        r11 = r7[r11];	 Catch:{ Exception -> 0x00f7 }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x00f7 }
        r9 = r10.toString();	 Catch:{ Exception -> 0x00f7 }
    L_0x004d:
        return r9;
    L_0x004e:
        r4 = r10;
        goto L_0x000a;
    L_0x0050:
        r11 = isDownloadsDocument(r14);	 Catch:{ Exception -> 0x00f7 }
        if (r11 == 0) goto L_0x0075;
    L_0x0056:
        r3 = android.provider.DocumentsContract.getDocumentId(r14);	 Catch:{ Exception -> 0x00f7 }
        r10 = "content://downloads/public_downloads";
        r10 = android.net.Uri.parse(r10);	 Catch:{ Exception -> 0x00f7 }
        r11 = java.lang.Long.valueOf(r3);	 Catch:{ Exception -> 0x00f7 }
        r12 = r11.longValue();	 Catch:{ Exception -> 0x00f7 }
        r0 = android.content.ContentUris.withAppendedId(r10, r12);	 Catch:{ Exception -> 0x00f7 }
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00f7 }
        r11 = 0;
        r12 = 0;
        r9 = getDataColumn(r10, r0, r11, r12);	 Catch:{ Exception -> 0x00f7 }
        goto L_0x004d;
    L_0x0075:
        r11 = isMediaDocument(r14);	 Catch:{ Exception -> 0x00f7 }
        if (r11 == 0) goto L_0x004d;
    L_0x007b:
        r1 = android.provider.DocumentsContract.getDocumentId(r14);	 Catch:{ Exception -> 0x00f7 }
        r11 = ":";
        r7 = r1.split(r11);	 Catch:{ Exception -> 0x00f7 }
        r11 = 0;
        r8 = r7[r11];	 Catch:{ Exception -> 0x00f7 }
        r0 = 0;
        r11 = -1;
        r13 = r8.hashCode();	 Catch:{ Exception -> 0x00f7 }
        switch(r13) {
            case 93166550: goto L_0x00bc;
            case 100313435: goto L_0x00a9;
            case 112202875: goto L_0x00b2;
            default: goto L_0x0091;
        };	 Catch:{ Exception -> 0x00f7 }
    L_0x0091:
        r10 = r11;
    L_0x0092:
        switch(r10) {
            case 0: goto L_0x00c6;
            case 1: goto L_0x00c9;
            case 2: goto L_0x00cc;
            default: goto L_0x0095;
        };	 Catch:{ Exception -> 0x00f7 }
    L_0x0095:
        r5 = "_id=?";
        r10 = 1;
        r6 = new java.lang.String[r10];	 Catch:{ Exception -> 0x00f7 }
        r10 = 0;
        r11 = 1;
        r11 = r7[r11];	 Catch:{ Exception -> 0x00f7 }
        r6[r10] = r11;	 Catch:{ Exception -> 0x00f7 }
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00f7 }
        r11 = "_id=?";
        r9 = getDataColumn(r10, r0, r11, r6);	 Catch:{ Exception -> 0x00f7 }
        goto L_0x004d;
    L_0x00a9:
        r12 = "image";
        r12 = r8.equals(r12);	 Catch:{ Exception -> 0x00f7 }
        if (r12 == 0) goto L_0x0091;
    L_0x00b1:
        goto L_0x0092;
    L_0x00b2:
        r10 = "video";
        r10 = r8.equals(r10);	 Catch:{ Exception -> 0x00f7 }
        if (r10 == 0) goto L_0x0091;
    L_0x00ba:
        r10 = r12;
        goto L_0x0092;
    L_0x00bc:
        r10 = "audio";
        r10 = r8.equals(r10);	 Catch:{ Exception -> 0x00f7 }
        if (r10 == 0) goto L_0x0091;
    L_0x00c4:
        r10 = 2;
        goto L_0x0092;
    L_0x00c6:
        r0 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x00f7 }
        goto L_0x0095;
    L_0x00c9:
        r0 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x00f7 }
        goto L_0x0095;
    L_0x00cc:
        r0 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x00f7 }
        goto L_0x0095;
    L_0x00cf:
        r10 = "content";
        r11 = r14.getScheme();	 Catch:{ Exception -> 0x00f7 }
        r10 = r10.equalsIgnoreCase(r11);	 Catch:{ Exception -> 0x00f7 }
        if (r10 == 0) goto L_0x00e5;
    L_0x00db:
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00f7 }
        r11 = 0;
        r12 = 0;
        r9 = getDataColumn(r10, r14, r11, r12);	 Catch:{ Exception -> 0x00f7 }
        goto L_0x004d;
    L_0x00e5:
        r10 = "file";
        r11 = r14.getScheme();	 Catch:{ Exception -> 0x00f7 }
        r10 = r10.equalsIgnoreCase(r11);	 Catch:{ Exception -> 0x00f7 }
        if (r10 == 0) goto L_0x004d;
    L_0x00f1:
        r9 = r14.getPath();	 Catch:{ Exception -> 0x00f7 }
        goto L_0x004d;
    L_0x00f7:
        r2 = move-exception;
        r10 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r10, r2);
        goto L_0x004d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getPath(android.net.Uri):java.lang.String");
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[FLAG_TAG_BR];
        projection[0] = "_data";
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            String value = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            if (value.startsWith("content://") || !(value.startsWith("/") || value.startsWith("file://"))) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } else if (cursor == null) {
                return value;
            } else {
                cursor.close();
                return value;
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static File generatePicturePath() {
        try {
            return new File(getAlbumDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg");
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return null;
        }
    }

    public static CharSequence generateSearchName(String name, String name2, String q) {
        if (name == null && name2 == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        CharSequence builder = new SpannableStringBuilder();
        String wholeString = name;
        if (wholeString == null || wholeString.length() == 0) {
            wholeString = name2;
        } else if (!(name2 == null || name2.length() == 0)) {
            wholeString = wholeString + " " + name2;
        }
        wholeString = wholeString.trim();
        String lower = " " + wholeString.toLowerCase();
        int lastIndex = 0;
        while (true) {
            int index = lower.indexOf(" " + q, lastIndex);
            if (index == -1) {
                break;
            }
            int i;
            if (index == 0) {
                i = 0;
            } else {
                i = FLAG_TAG_BR;
            }
            int idx = index - i;
            int length = q.length();
            if (index == 0) {
                i = 0;
            } else {
                i = FLAG_TAG_BR;
            }
            int end = (i + length) + idx;
            if (lastIndex != 0 && lastIndex != idx + FLAG_TAG_BR) {
                builder.append(wholeString.substring(lastIndex, idx));
            } else if (lastIndex == 0 && idx != 0) {
                builder.append(wholeString.substring(0, idx));
            }
            String query = wholeString.substring(idx, end);
            if (query.startsWith(" ")) {
                builder.append(" ");
            }
            builder.append(replaceTags("<c#ff4d83b3>" + query.trim() + "</c>"));
            lastIndex = end;
        }
        if (lastIndex == -1 || lastIndex == wholeString.length()) {
            return builder;
        }
        builder.append(wholeString.substring(lastIndex, wholeString.length()));
        return builder;
    }

    public static File generateVideoPath() {
        try {
            return new File(getAlbumDir(), "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4");
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return null;
        }
    }

    public static String formatFileSize(long size) {
        Object[] objArr;
        if (size < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Long.valueOf(size);
            return String.format("%d B", objArr);
        } else if (size < 1048576) {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Float.valueOf(((float) size) / 1024.0f);
            return String.format("%.1f KB", objArr);
        } else if (size < 1073741824) {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Float.valueOf((((float) size) / 1024.0f) / 1024.0f);
            return String.format("%.1f MB", objArr);
        } else {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Float.valueOf(((((float) size) / 1024.0f) / 1024.0f) / 1024.0f);
            return String.format("%.1f GB", objArr);
        }
    }

    public static byte[] decodeQuotedPrintable(byte[] bytes) {
        byte[] bArr = null;
        if (bytes != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int i = 0;
            while (i < bytes.length) {
                int b = bytes[i];
                if (b == 61) {
                    i += FLAG_TAG_BR;
                    try {
                        int u = Character.digit((char) bytes[i], 16);
                        i += FLAG_TAG_BR;
                        buffer.write((char) ((u << FLAG_TAG_COLOR) + Character.digit((char) bytes[i], 16)));
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                } else {
                    buffer.write(b);
                }
                i += FLAG_TAG_BR;
            }
            bArr = buffer.toByteArray();
            try {
                buffer.close();
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
        }
        return bArr;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[MessagesController.UPDATE_MASK_SEND_STATE];
        while (true) {
            int len = sourceFile.read(buf);
            if (len > 0) {
                Thread.yield();
                out.write(buf, 0, len);
            } else {
                out.close();
                return true;
            }
        }
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        Throwable e;
        Throwable th;
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileInputStream source = null;
        FileOutputStream destination = null;
        try {
            FileInputStream source2 = new FileInputStream(sourceFile);
            try {
                FileOutputStream destination2 = new FileOutputStream(destFile);
                try {
                    destination2.getChannel().transferFrom(source2.getChannel(), 0, source2.getChannel().size());
                    if (source2 != null) {
                        source2.close();
                    }
                    if (destination2 != null) {
                        destination2.close();
                    }
                    destination = destination2;
                    source = source2;
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    destination = destination2;
                    source = source2;
                    try {
                        FileLog.m13e("tmessages", e);
                        if (source != null) {
                            source.close();
                        }
                        if (destination != null) {
                            return false;
                        }
                        destination.close();
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (source != null) {
                            source.close();
                        }
                        if (destination != null) {
                            destination.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    destination = destination2;
                    source = source2;
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                source = source2;
                FileLog.m13e("tmessages", e);
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    return false;
                }
                destination.close();
                return false;
            } catch (Throwable th4) {
                th = th4;
                source = source2;
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            FileLog.m13e("tmessages", e);
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                return false;
            }
            destination.close();
            return false;
        }
    }

    public static byte[] calcAuthKeyHash(byte[] auth_key) {
        byte[] key_hash = new byte[16];
        System.arraycopy(Utilities.computeSHA1(auth_key), 0, key_hash, 0, 16);
        return key_hash;
    }

    public static void openForView(MessageObject message, Activity activity) throws Exception {
        File f = null;
        String fileName = message.getFileName();
        if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
            f = new File(message.messageOwner.attachPath);
        }
        if (f == null || !f.exists()) {
            f = FileLoader.getPathToMessage(message.messageOwner);
        }
        if (f != null && f.exists()) {
            String realMimeType = null;
            Intent intent = new Intent("android.intent.action.VIEW");
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            int idx = fileName.lastIndexOf(46);
            if (idx != -1) {
                realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + FLAG_TAG_BR).toLowerCase());
                if (realMimeType == null) {
                    if (message.type == 9 || message.type == 0) {
                        realMimeType = message.getDocument().mime_type;
                    }
                    if (realMimeType == null || realMimeType.length() == 0) {
                        realMimeType = null;
                    }
                }
                if (realMimeType != null) {
                    intent.setDataAndType(Uri.fromFile(f), realMimeType);
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "text/plain");
                }
            } else {
                intent.setDataAndType(Uri.fromFile(f), "text/plain");
            }
            if (realMimeType != null) {
                try {
                    activity.startActivityForResult(intent, 500);
                    return;
                } catch (Exception e) {
                    intent.setDataAndType(Uri.fromFile(f), "text/plain");
                    activity.startActivityForResult(intent, 500);
                    return;
                }
            }
            activity.startActivityForResult(intent, 500);
        }
    }
}
