package org.telegram.messenger;

import android.app.Activity;
import android.net.Uri;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.utils.SimpleMultipartEntity;

public class NativeCrashManager {

    /* renamed from: org.telegram.messenger.NativeCrashManager.1 */
    static class C06701 extends Thread {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ String val$dumpFilename;
        final /* synthetic */ String val$identifier;
        final /* synthetic */ String val$logFilename;

        C06701(String str, Activity activity, String str2, String str3) {
            this.val$dumpFilename = str;
            this.val$activity = activity;
            this.val$logFilename = str2;
            this.val$identifier = str3;
        }

        public void run() {
            try {
                SimpleMultipartEntity entity = new SimpleMultipartEntity();
                entity.writeFirstBoundaryIfNeeds();
                Uri attachmentUri = Uri.fromFile(new File(Constants.FILES_PATH, this.val$dumpFilename));
                entity.addPart("attachment0", attachmentUri.getLastPathSegment(), this.val$activity.getContentResolver().openInputStream(attachmentUri), false);
                attachmentUri = Uri.fromFile(new File(Constants.FILES_PATH, this.val$logFilename));
                entity.addPart("log", attachmentUri.getLastPathSegment(), this.val$activity.getContentResolver().openInputStream(attachmentUri), true);
                entity.writeLastBoundaryIfNeeds();
                HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://rink.hockeyapp.net/api/2/apps/" + this.val$identifier + "/crashes/upload").openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", entity.getContentType());
                urlConnection.setRequestProperty("Content-Length", String.valueOf(entity.getContentLength()));
                BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputStream.write(entity.getOutputStream().toByteArray());
                outputStream.flush();
                outputStream.close();
                urlConnection.connect();
                FileLog.m11e("tmessages", "response code = " + urlConnection.getResponseCode() + " message = " + urlConnection.getResponseMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.val$activity.deleteFile(this.val$logFilename);
                this.val$activity.deleteFile(this.val$dumpFilename);
            }
        }
    }

    /* renamed from: org.telegram.messenger.NativeCrashManager.2 */
    static class C06712 implements FilenameFilter {
        C06712() {
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(".dmp");
        }
    }

    public static void handleDumpFiles(Activity activity) {
        for (String dumpFilename : searchForDumpFiles()) {
            String logFilename = createLogFile();
            if (logFilename != null) {
                uploadDumpAndLog(activity, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, dumpFilename, logFilename);
            }
        }
    }

    public static String createLogFile() {
        Date now = new Date();
        try {
            String filename = UUID.randomUUID().toString();
            BufferedWriter write = new BufferedWriter(new FileWriter(Constants.FILES_PATH + "/" + filename + ".faketrace"));
            write.write("Package: " + Constants.APP_PACKAGE + "\n");
            write.write("Version Code: " + Constants.APP_VERSION + "\n");
            write.write("Version Name: " + Constants.APP_VERSION_NAME + "\n");
            write.write("Android: " + Constants.ANDROID_VERSION + "\n");
            write.write("Manufacturer: " + Constants.PHONE_MANUFACTURER + "\n");
            write.write("Model: " + Constants.PHONE_MODEL + "\n");
            write.write("Date: " + now + "\n");
            write.write("\n");
            write.write("MinidumpContainer");
            write.flush();
            write.close();
            return filename + ".faketrace";
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return null;
        }
    }

    public static void uploadDumpAndLog(Activity activity, String identifier, String dumpFilename, String logFilename) {
        new C06701(dumpFilename, activity, logFilename, identifier).start();
    }

    private static String[] searchForDumpFiles() {
        if (Constants.FILES_PATH == null) {
            return new String[0];
        }
        File dir = new File(Constants.FILES_PATH + "/");
        if (dir.mkdir() || dir.exists()) {
            return dir.list(new C06712());
        }
        return new String[0];
    }
}
