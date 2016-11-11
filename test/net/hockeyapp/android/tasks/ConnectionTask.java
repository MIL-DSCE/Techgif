package net.hockeyapp.android.tasks;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public abstract class ConnectionTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected static String getStringFromConnection(HttpURLConnection connection) throws IOException {
        InputStream inputStream = new BufferedInputStream(connection.getInputStream());
        String jsonString = convertStreamToString(inputStream);
        inputStream.close();
        return jsonString;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String convertStreamToString(java.io.InputStream r6) {
        /*
        r2 = new java.io.BufferedReader;
        r4 = new java.io.InputStreamReader;
        r4.<init>(r6);
        r5 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r2.<init>(r4, r5);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r1 = 0;
    L_0x0012:
        r1 = r2.readLine();	 Catch:{ IOException -> 0x002f }
        if (r1 == 0) goto L_0x003b;
    L_0x0018:
        r4 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x002f }
        r4.<init>();	 Catch:{ IOException -> 0x002f }
        r4 = r4.append(r1);	 Catch:{ IOException -> 0x002f }
        r5 = "\n";
        r4 = r4.append(r5);	 Catch:{ IOException -> 0x002f }
        r4 = r4.toString();	 Catch:{ IOException -> 0x002f }
        r3.append(r4);	 Catch:{ IOException -> 0x002f }
        goto L_0x0012;
    L_0x002f:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ all -> 0x0049 }
        r6.close();	 Catch:{ IOException -> 0x0044 }
    L_0x0036:
        r4 = r3.toString();
        return r4;
    L_0x003b:
        r6.close();	 Catch:{ IOException -> 0x003f }
        goto L_0x0036;
    L_0x003f:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0036;
    L_0x0044:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0036;
    L_0x0049:
        r4 = move-exception;
        r6.close();	 Catch:{ IOException -> 0x004e }
    L_0x004d:
        throw r4;
    L_0x004e:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x004d;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.tasks.ConnectionTask.convertStreamToString(java.io.InputStream):java.lang.String");
    }
}
