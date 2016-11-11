package org.telegram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hockeyapp.android.Constants;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.22";
    private static final String LIB_SO_NAME = "libtmessages.22.so";
    private static final int LIB_VERSION = 22;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.22loc.so";
    private static volatile boolean nativeLoaded;
    private String crashPath;

    private static native void init(String str, boolean z);

    public NativeLoader() {
        this.crashPath = TtmlNode.ANONYMOUS_REGION_ID;
    }

    static {
        nativeLoaded = false;
    }

    private static File getNativeLibraryDir(Context context) {
        File file = null;
        if (context != null) {
            try {
                file = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (file == null) {
            file = new File(context.getApplicationInfo().dataDir, "lib");
        }
        return file.isDirectory() ? file : null;
    }

    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        Throwable th;
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Throwable e) {
            Throwable e2;
            FileLog.m13e("tmessages", e2);
        }
        ZipFile zipFile = null;
        InputStream stream = null;
        try {
            ZipFile zipFile2 = new ZipFile(context.getApplicationInfo().sourceDir);
            try {
                ZipEntry entry = zipFile2.getEntry("lib/" + folder + "/" + LIB_SO_NAME);
                if (entry == null) {
                    throw new Exception("Unable to find file in apk:lib/" + folder + "/" + LIB_NAME);
                }
                stream = zipFile2.getInputStream(entry);
                OutputStream out = new FileOutputStream(destLocalFile);
                byte[] buf = new byte[MessagesController.UPDATE_MASK_SEND_STATE];
                while (true) {
                    int len = stream.read(buf);
                    if (len <= 0) {
                        break;
                    }
                    Thread.yield();
                    out.write(buf, 0, len);
                }
                out.close();
                destLocalFile.setReadable(true, false);
                destLocalFile.setExecutable(true, false);
                destLocalFile.setWritable(true);
                try {
                    System.load(destLocalFile.getAbsolutePath());
                    init(Constants.FILES_PATH, BuildVars.DEBUG_VERSION);
                    nativeLoaded = true;
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e222) {
                        FileLog.m13e("tmessages", e222);
                    }
                }
                if (zipFile2 != null) {
                    try {
                        zipFile2.close();
                    } catch (Throwable e2222) {
                        FileLog.m13e("tmessages", e2222);
                    }
                }
                zipFile = zipFile2;
                return true;
            } catch (Exception e3) {
                e2222 = e3;
                zipFile = zipFile2;
                try {
                    FileLog.m13e("tmessages", e2222);
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e22222) {
                            FileLog.m13e("tmessages", e22222);
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable e222222) {
                            FileLog.m13e("tmessages", e222222);
                        }
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e2222222) {
                            FileLog.m13e("tmessages", e2222222);
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable e22222222) {
                            FileLog.m13e("tmessages", e22222222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                zipFile = zipFile2;
                if (stream != null) {
                    stream.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e22222222 = e4;
            FileLog.m13e("tmessages", e22222222);
            if (stream != null) {
                stream.close();
            }
            if (zipFile != null) {
                zipFile.close();
            }
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(android.content.Context r11) {
        /*
        r8 = org.telegram.messenger.NativeLoader.class;
        monitor-enter(r8);
        r7 = nativeLoaded;	 Catch:{ all -> 0x00d0 }
        if (r7 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r8);
        return;
    L_0x0009:
        net.hockeyapp.android.Constants.loadFromContext(r11);	 Catch:{ all -> 0x00d0 }
        r7 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011b }
        r9 = "armeabi-v7a";
        r7 = r7.equalsIgnoreCase(r9);	 Catch:{ Exception -> 0x011b }
        if (r7 == 0) goto L_0x00d3;
    L_0x0016:
        r5 = "armeabi-v7a";
    L_0x0018:
        r7 = "os.arch";
        r6 = java.lang.System.getProperty(r7);	 Catch:{ Throwable -> 0x0125 }
        if (r6 == 0) goto L_0x002a;
    L_0x0020:
        r7 = "686";
        r7 = r6.contains(r7);	 Catch:{ Throwable -> 0x0125 }
        if (r7 == 0) goto L_0x002a;
    L_0x0028:
        r5 = "x86";
    L_0x002a:
        r1 = getNativeLibraryDir(r11);	 Catch:{ Throwable -> 0x0125 }
        if (r1 == 0) goto L_0x005b;
    L_0x0030:
        r2 = new java.io.File;	 Catch:{ Throwable -> 0x0125 }
        r7 = "libtmessages.22.so";
        r2.<init>(r1, r7);	 Catch:{ Throwable -> 0x0125 }
        r7 = r2.exists();	 Catch:{ Throwable -> 0x0125 }
        if (r7 == 0) goto L_0x005a;
    L_0x003d:
        r7 = "tmessages";
        r9 = "load normal lib";
        org.telegram.messenger.FileLog.m10d(r7, r9);	 Catch:{ Throwable -> 0x0125 }
        r7 = "tmessages.22";
        java.lang.System.loadLibrary(r7);	 Catch:{ Error -> 0x0054 }
        r7 = net.hockeyapp.android.Constants.FILES_PATH;	 Catch:{ Error -> 0x0054 }
        r9 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Error -> 0x0054 }
        init(r7, r9);	 Catch:{ Error -> 0x0054 }
        r7 = 1;
        nativeLoaded = r7;	 Catch:{ Error -> 0x0054 }
        goto L_0x0007;
    L_0x0054:
        r4 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r4);	 Catch:{ Throwable -> 0x0125 }
    L_0x005a:
        r1 = r2;
    L_0x005b:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x0125 }
        r7 = r11.getFilesDir();	 Catch:{ Throwable -> 0x0125 }
        r9 = "lib";
        r0.<init>(r7, r9);	 Catch:{ Throwable -> 0x0125 }
        r0.mkdirs();	 Catch:{ Throwable -> 0x0125 }
        r3 = new java.io.File;	 Catch:{ Throwable -> 0x0125 }
        r7 = "libtmessages.22loc.so";
        r3.<init>(r0, r7);	 Catch:{ Throwable -> 0x0125 }
        r7 = r3.exists();	 Catch:{ Throwable -> 0x0125 }
        if (r7 == 0) goto L_0x0099;
    L_0x0076:
        r7 = "tmessages";
        r9 = "Load local lib";
        org.telegram.messenger.FileLog.m10d(r7, r9);	 Catch:{ Error -> 0x0090 }
        r7 = r3.getAbsolutePath();	 Catch:{ Error -> 0x0090 }
        java.lang.System.load(r7);	 Catch:{ Error -> 0x0090 }
        r7 = net.hockeyapp.android.Constants.FILES_PATH;	 Catch:{ Error -> 0x0090 }
        r9 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Error -> 0x0090 }
        init(r7, r9);	 Catch:{ Error -> 0x0090 }
        r7 = 1;
        nativeLoaded = r7;	 Catch:{ Error -> 0x0090 }
        goto L_0x0007;
    L_0x0090:
        r4 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r4);	 Catch:{ Throwable -> 0x0125 }
        r3.delete();	 Catch:{ Throwable -> 0x0125 }
    L_0x0099:
        r7 = "tmessages";
        r9 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0125 }
        r9.<init>();	 Catch:{ Throwable -> 0x0125 }
        r10 = "Library not found, arch = ";
        r9 = r9.append(r10);	 Catch:{ Throwable -> 0x0125 }
        r9 = r9.append(r5);	 Catch:{ Throwable -> 0x0125 }
        r9 = r9.toString();	 Catch:{ Throwable -> 0x0125 }
        org.telegram.messenger.FileLog.m11e(r7, r9);	 Catch:{ Throwable -> 0x0125 }
        r7 = loadFromZip(r11, r0, r3, r5);	 Catch:{ Throwable -> 0x0125 }
        if (r7 != 0) goto L_0x0007;
    L_0x00b7:
        r7 = "tmessages.22";
        java.lang.System.loadLibrary(r7);	 Catch:{ Error -> 0x00c8 }
        r7 = net.hockeyapp.android.Constants.FILES_PATH;	 Catch:{ Error -> 0x00c8 }
        r9 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Error -> 0x00c8 }
        init(r7, r9);	 Catch:{ Error -> 0x00c8 }
        r7 = 1;
        nativeLoaded = r7;	 Catch:{ Error -> 0x00c8 }
        goto L_0x0007;
    L_0x00c8:
        r4 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r4);	 Catch:{ all -> 0x00d0 }
        goto L_0x0007;
    L_0x00d0:
        r7 = move-exception;
        monitor-exit(r8);
        throw r7;
    L_0x00d3:
        r7 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011b }
        r9 = "armeabi";
        r7 = r7.equalsIgnoreCase(r9);	 Catch:{ Exception -> 0x011b }
        if (r7 == 0) goto L_0x00e1;
    L_0x00dd:
        r5 = "armeabi";
        goto L_0x0018;
    L_0x00e1:
        r7 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011b }
        r9 = "x86";
        r7 = r7.equalsIgnoreCase(r9);	 Catch:{ Exception -> 0x011b }
        if (r7 == 0) goto L_0x00ef;
    L_0x00eb:
        r5 = "x86";
        goto L_0x0018;
    L_0x00ef:
        r7 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011b }
        r9 = "mips";
        r7 = r7.equalsIgnoreCase(r9);	 Catch:{ Exception -> 0x011b }
        if (r7 == 0) goto L_0x00fd;
    L_0x00f9:
        r5 = "mips";
        goto L_0x0018;
    L_0x00fd:
        r5 = "armeabi";
        r7 = "tmessages";
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011b }
        r9.<init>();	 Catch:{ Exception -> 0x011b }
        r10 = "Unsupported arch: ";
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x011b }
        r10 = android.os.Build.CPU_ABI;	 Catch:{ Exception -> 0x011b }
        r9 = r9.append(r10);	 Catch:{ Exception -> 0x011b }
        r9 = r9.toString();	 Catch:{ Exception -> 0x011b }
        org.telegram.messenger.FileLog.m11e(r7, r9);	 Catch:{ Exception -> 0x011b }
        goto L_0x0018;
    L_0x011b:
        r4 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r4);	 Catch:{ Throwable -> 0x0125 }
        r5 = "armeabi";
        goto L_0x0018;
    L_0x0125:
        r4 = move-exception;
        r4.printStackTrace();	 Catch:{ all -> 0x00d0 }
        goto L_0x00b7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}
