package net.hockeyapp.android.metrics;

import android.content.Context;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;
import net.hockeyapp.android.utils.HockeyLog;

class Persistence {
    private static final String BIT_TELEMETRY_DIRECTORY = "/net.hockeyapp.android/telemetry/";
    private static final Object LOCK;
    private static final Integer MAX_FILE_COUNT;
    private static final String TAG = "HA-MetricsPersistence";
    protected ArrayList<File> mServedFiles;
    protected final File mTelemetryDirectory;
    private final WeakReference<Context> mWeakContext;
    protected WeakReference<Sender> mWeakSender;

    static {
        LOCK = new Object();
        MAX_FILE_COUNT = Integer.valueOf(50);
    }

    protected Persistence(Context context, File telemetryDirectory, Sender sender) {
        this.mWeakContext = new WeakReference(context);
        this.mServedFiles = new ArrayList(51);
        this.mTelemetryDirectory = telemetryDirectory;
        this.mWeakSender = new WeakReference(sender);
        createDirectoriesIfNecessary();
    }

    protected Persistence(Context context, Sender sender) {
        this(context, new File(context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY), null);
        setSender(sender);
    }

    protected void persist(String[] data) {
        if (isFreeSpaceAvailable().booleanValue()) {
            StringBuilder buffer = new StringBuilder();
            for (String aData : data) {
                if (buffer.length() > 0) {
                    buffer.append('\n');
                }
                buffer.append(aData);
            }
            if (Boolean.valueOf(writeToDisk(buffer.toString())).booleanValue()) {
                getSender().triggerSending();
                return;
            }
            return;
        }
        HockeyLog.warn(TAG, "Failed to persist file: Too many files on disk.");
        getSender().triggerSending();
    }

    protected boolean writeToDisk(String data) {
        Exception e;
        Throwable th;
        String uuid = UUID.randomUUID().toString();
        Boolean isSuccess = Boolean.valueOf(false);
        FileOutputStream outputStream = null;
        try {
            synchronized (LOCK) {
                try {
                    File filesDir = new File(this.mTelemetryDirectory + "/" + uuid);
                    FileOutputStream outputStream2 = new FileOutputStream(filesDir, true);
                    try {
                        outputStream2.write(data.getBytes());
                        HockeyLog.warn(TAG, "Saving data to: " + filesDir.toString());
                        try {
                            isSuccess = Boolean.valueOf(true);
                            if (outputStream2 != null) {
                                try {
                                    outputStream2.close();
                                    outputStream = outputStream2;
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                    outputStream = outputStream2;
                                }
                            }
                        } catch (Exception e3) {
                            e = e3;
                            outputStream = outputStream2;
                            try {
                                HockeyLog.warn(TAG, "Failed to save data with exception: " + e.toString());
                                if (outputStream != null) {
                                    try {
                                        outputStream.close();
                                    } catch (IOException e22) {
                                        e22.printStackTrace();
                                    }
                                }
                                return isSuccess.booleanValue();
                            } catch (Throwable th2) {
                                th = th2;
                                if (outputStream != null) {
                                    try {
                                        outputStream.close();
                                    } catch (IOException e222) {
                                        e222.printStackTrace();
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            outputStream = outputStream2;
                            if (outputStream != null) {
                                outputStream.close();
                            }
                            throw th;
                        }
                        return isSuccess.booleanValue();
                    } catch (Throwable th4) {
                        th = th4;
                        outputStream = outputStream2;
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    throw th;
                }
            }
        } catch (Exception e4) {
            e = e4;
        }
    }

    protected String load(File file) {
        Throwable th;
        StringBuilder buffer = new StringBuilder();
        if (file != null) {
            BufferedReader reader = null;
            try {
                synchronized (LOCK) {
                    try {
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        while (true) {
                            try {
                                int c = reader2.read();
                                if (c == -1) {
                                    break;
                                }
                                buffer.append((char) c);
                            } catch (Throwable th2) {
                                th = th2;
                                reader = reader2;
                            }
                        }
                        if (reader2 != null) {
                            try {
                                reader2.close();
                            } catch (IOException e) {
                                HockeyLog.warn(TAG, "Error closing stream." + e.getMessage());
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th;
                    }
                }
            } catch (Exception e2) {
                HockeyLog.warn(TAG, "Error reading telemetry data from file with exception message " + e2.getMessage());
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        HockeyLog.warn(TAG, "Error closing stream." + e3.getMessage());
                    }
                }
            } catch (Throwable th4) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e32) {
                        HockeyLog.warn(TAG, "Error closing stream." + e32.getMessage());
                    }
                }
            }
        }
        return buffer.toString();
    }

    protected File nextAvailableFileInDirectory() {
        File file;
        synchronized (LOCK) {
            if (this.mTelemetryDirectory != null) {
                File[] files = this.mTelemetryDirectory.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i <= files.length - 1; i++) {
                        file = files[i];
                        if (!this.mServedFiles.contains(file)) {
                            HockeyLog.info(TAG, "The directory " + file.toString() + " (ADDING TO SERVED AND RETURN)");
                            this.mServedFiles.add(file);
                            break;
                        }
                        HockeyLog.info(TAG, "The directory " + file.toString() + " (WAS ALREADY SERVED)");
                    }
                }
            }
            if (this.mTelemetryDirectory != null) {
                HockeyLog.info(TAG, "The directory " + this.mTelemetryDirectory.toString() + " did not contain any " + "unserved files");
            }
            file = null;
        }
        return file;
    }

    protected void deleteFile(File file) {
        if (file != null) {
            synchronized (LOCK) {
                if (file.delete()) {
                    HockeyLog.warn(TAG, "Successfully deleted telemetry file at: " + file.toString());
                    this.mServedFiles.remove(file);
                } else {
                    HockeyLog.warn(TAG, "Error deleting telemetry file " + file.toString());
                }
            }
            return;
        }
        HockeyLog.warn(TAG, "Couldn't delete file, the reference to the file was null");
    }

    protected void makeAvailable(File file) {
        synchronized (LOCK) {
            if (file != null) {
                this.mServedFiles.remove(file);
            }
        }
    }

    protected Boolean isFreeSpaceAvailable() {
        Boolean valueOf;
        boolean z = false;
        synchronized (LOCK) {
            Context context = getContext();
            if (context.getFilesDir() != null) {
                String path = context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY;
                if (!TextUtils.isEmpty(path)) {
                    if (new File(path).listFiles().length < MAX_FILE_COUNT.intValue()) {
                        z = true;
                    }
                    valueOf = Boolean.valueOf(z);
                }
            }
            valueOf = Boolean.valueOf(false);
        }
        return valueOf;
    }

    protected void createDirectoriesIfNecessary() {
        String successMessage = "Successfully created directory";
        String errorMessage = "Error creating directory";
        if (this.mTelemetryDirectory != null && !this.mTelemetryDirectory.exists()) {
            if (this.mTelemetryDirectory.mkdirs()) {
                HockeyLog.info(TAG, successMessage);
            } else {
                HockeyLog.info(TAG, errorMessage);
            }
        }
    }

    private Context getContext() {
        if (this.mWeakContext != null) {
            return (Context) this.mWeakContext.get();
        }
        return null;
    }

    protected Sender getSender() {
        if (this.mWeakSender != null) {
            return (Sender) this.mWeakSender.get();
        }
        return null;
    }

    protected void setSender(Sender sender) {
        this.mWeakSender = new WeakReference(sender);
    }
}
