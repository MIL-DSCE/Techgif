package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build.VERSION;
import android.system.Os;
import android.system.StructStat;
import java.io.File;
import java.util.Map.Entry;

public class ClearCacheService extends IntentService {

    /* renamed from: org.telegram.messenger.ClearCacheService.1 */
    class C04311 implements Runnable {
        final /* synthetic */ int val$keepMedia;

        C04311(int i) {
            this.val$keepMedia = i;
        }

        public void run() {
            long currentTime = System.currentTimeMillis();
            long diff = (long) ((this.val$keepMedia == 0 ? 7 : 30) * 86400000);
            for (Entry<Integer, File> entry : ImageLoader.getInstance().createMediaPaths().entrySet()) {
                if (((Integer) entry.getKey()).intValue() != 4) {
                    File[] array = ((File) entry.getValue()).listFiles();
                    if (array != null) {
                        for (File f : array) {
                            if (f.isFile() && !f.getName().equals(".nomedia")) {
                                if (VERSION.SDK_INT >= 21) {
                                    try {
                                        StructStat stat = Os.stat(f.getPath());
                                        if (stat.st_atime != 0) {
                                            if (stat.st_atime + diff < currentTime) {
                                                f.delete();
                                            }
                                        } else if (stat.st_mtime + diff < currentTime) {
                                            f.delete();
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m13e("tmessages", e);
                                    } catch (Throwable e2) {
                                        FileLog.m13e("tmessages", e2);
                                    }
                                } else if (f.lastModified() + diff < currentTime) {
                                    f.delete();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ClearCacheService() {
        super("ClearCacheService");
    }

    protected void onHandleIntent(Intent intent) {
        ApplicationLoader.postInitApplication();
        int keepMedia = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
        if (keepMedia != 2) {
            Utilities.globalQueue.postRunnable(new C04311(keepMedia));
        }
    }
}
