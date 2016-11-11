package org.telegram.messenger;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;

public class FileLog {
    private static volatile FileLog Instance;
    private File currentFile;
    private FastDateFormat dateFormat;
    private DispatchQueue logQueue;
    private File networkFile;
    private OutputStreamWriter streamWriter;

    /* renamed from: org.telegram.messenger.FileLog.1 */
    static class C04821 implements Runnable {
        final /* synthetic */ Throwable val$exception;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04821(String str, String str2, Throwable th) {
            this.val$tag = str;
            this.val$message = str2;
            this.val$exception = th;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.write(this.val$exception.toString());
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLog.2 */
    static class C04832 implements Runnable {
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04832(String str, String str2) {
            this.val$tag = str;
            this.val$message = str2;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLog.3 */
    static class C04843 implements Runnable {
        final /* synthetic */ Throwable val$e;
        final /* synthetic */ String val$tag;

        C04843(String str, Throwable th) {
            this.val$tag = str;
            this.val$e = th;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + this.val$e + "\n");
                StackTraceElement[] stack = this.val$e.getStackTrace();
                for (Object obj : stack) {
                    FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "\ufe55 " + obj + "\n");
                }
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLog.4 */
    static class C04854 implements Runnable {
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04854(String str, String str2) {
            this.val$tag = str;
            this.val$message = str2;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " D/" + this.val$tag + "\ufe55 " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLog.5 */
    static class C04865 implements Runnable {
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$tag;

        C04865(String str, String str2) {
            this.val$tag = str;
            this.val$message = str2;
        }

        public void run() {
            try {
                FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " W/" + this.val$tag + ": " + this.val$message + "\n");
                FileLog.getInstance().streamWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static {
        Instance = null;
    }

    public static FileLog getInstance() {
        FileLog localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        FileLog localInstance2 = new FileLog();
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

    public FileLog() {
        this.streamWriter = null;
        this.dateFormat = null;
        this.logQueue = null;
        this.currentFile = null;
        this.networkFile = null;
        if (BuildVars.DEBUG_VERSION) {
            this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
            try {
                File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
                if (sdCard != null) {
                    File dir = new File(sdCard.getAbsolutePath() + "/logs");
                    dir.mkdirs();
                    this.currentFile = new File(dir, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        this.streamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
                        this.streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static String getNetworkLogPath() {
        if (!BuildVars.DEBUG_VERSION) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (sdCard == null) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            getInstance().networkFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static void m12e(String tag, String message, Throwable exception) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e(tag, message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04821(tag, message, exception));
            }
        }
    }

    public static void m11e(String tag, String message) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04832(tag, message));
            }
        }
    }

    public static void m13e(String tag, Throwable e) {
        if (BuildVars.DEBUG_VERSION) {
            e.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04843(tag, e));
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void m10d(String tag, String message) {
        if (BuildVars.DEBUG_VERSION) {
            Log.d(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04854(tag, message));
            }
        }
    }

    public static void m14w(String tag, String message) {
        if (BuildVars.DEBUG_VERSION) {
            Log.w(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new C04865(tag, message));
            }
        }
    }

    public static void cleanupLogs() {
        File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (sdCard != null) {
            File[] files = new File(sdCard.getAbsolutePath() + "/logs").listFiles();
            if (files != null) {
                for (File file : files) {
                    if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && (getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath()))) {
                        file.delete();
                    }
                }
            }
        }
    }
}
