package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.FileLoader.FileLoaderDelegate;
import org.telegram.messenger.exoplayer.ExoPlayer.Factory;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ImageLoader {
    private static volatile ImageLoader Instance;
    private static byte[] bytes;
    private static byte[] bytesThumb;
    private static byte[] header;
    private static byte[] headerThumb;
    private HashMap<String, Integer> bitmapUseCounts;
    private DispatchQueue cacheOutQueue;
    private DispatchQueue cacheThumbOutQueue;
    private int currentHttpFileLoadTasksCount;
    private int currentHttpTasksCount;
    private ConcurrentHashMap<String, Float> fileProgresses;
    private LinkedList<HttpFileTask> httpFileLoadTasks;
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys;
    private LinkedList<HttpImageTask> httpTasks;
    private String ignoreRemoval;
    private DispatchQueue imageLoadQueue;
    private HashMap<String, CacheImage> imageLoadingByKeys;
    private HashMap<Integer, CacheImage> imageLoadingByTag;
    private HashMap<String, CacheImage> imageLoadingByUrl;
    private volatile long lastCacheOutTime;
    private int lastImageNum;
    private long lastProgressUpdateTime;
    private LruCache memCache;
    private HashMap<String, Runnable> retryHttpsTasks;
    private File telegramPath;
    private HashMap<String, ThumbGenerateTask> thumbGenerateTasks;
    private DispatchQueue thumbGeneratingQueue;
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb;
    private HashMap<Integer, String> waitingForQualityThumbByTag;

    /* renamed from: org.telegram.messenger.ImageLoader.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ String val$location;

        AnonymousClass10(String str) {
            this.val$location = str;
        }

        public void run() {
            CacheImage img = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$location);
            if (img != null) {
                img.setImageAndClear(null);
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ HttpFileTask val$oldTask;
        final /* synthetic */ int val$reason;

        /* renamed from: org.telegram.messenger.ImageLoader.11.1 */
        class C04951 implements Runnable {
            final /* synthetic */ HttpFileTask val$newTask;

            C04951(HttpFileTask httpFileTask) {
                this.val$newTask = httpFileTask;
            }

            public void run() {
                ImageLoader.this.httpFileLoadTasks.add(this.val$newTask);
                ImageLoader.this.runHttpFileLoadTasks(null, 0);
            }
        }

        AnonymousClass11(HttpFileTask httpFileTask, int i) {
            this.val$oldTask = httpFileTask;
            this.val$reason = i;
        }

        public void run() {
            if (this.val$oldTask != null) {
                ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount - 1;
            }
            if (this.val$oldTask != null) {
                if (this.val$reason == 1) {
                    if (this.val$oldTask.canRetry) {
                        Runnable runnable = new C04951(new HttpFileTask(this.val$oldTask.url, this.val$oldTask.tempFile, this.val$oldTask.ext));
                        ImageLoader.this.retryHttpsTasks.put(this.val$oldTask.url, runnable);
                        AndroidUtilities.runOnUIThread(runnable, 1000);
                    } else {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidFailedLoad, this.val$oldTask.url);
                    }
                } else if (this.val$reason == 2) {
                    ImageLoader.this.httpFileLoadTasksByKeys.remove(this.val$oldTask.url);
                    File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$oldTask.url) + "." + this.val$oldTask.ext);
                    String result = this.val$oldTask.tempFile.renameTo(file) ? file.toString() : this.val$oldTask.tempFile.toString();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidLoaded, this.val$oldTask.url, result);
                }
            }
            while (ImageLoader.this.currentHttpFileLoadTasksCount < 2 && !ImageLoader.this.httpFileLoadTasks.isEmpty()) {
                ((HttpFileTask) ImageLoader.this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount + 1;
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.3 */
    class C05053 extends BroadcastReceiver {

        /* renamed from: org.telegram.messenger.ImageLoader.3.1 */
        class C05041 implements Runnable {
            C05041() {
            }

            public void run() {
                ImageLoader.this.checkMediaPaths();
            }
        }

        C05053() {
        }

        public void onReceive(Context arg0, Intent intent) {
            FileLog.m11e("tmessages", "file system changed");
            Runnable r = new C05041();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                AndroidUtilities.runOnUIThread(r, 1000);
            } else {
                r.run();
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.4 */
    class C05074 implements Runnable {

        /* renamed from: org.telegram.messenger.ImageLoader.4.1 */
        class C05061 implements Runnable {
            final /* synthetic */ HashMap val$paths;

            C05061(HashMap hashMap) {
                this.val$paths = hashMap;
            }

            public void run() {
                FileLoader.getInstance().setMediaDirs(this.val$paths);
            }
        }

        C05074() {
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05061(ImageLoader.this.createMediaPaths()));
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.5 */
    class C05085 implements Runnable {
        final /* synthetic */ ImageReceiver val$imageReceiver;
        final /* synthetic */ int val$type;

        C05085(int i, ImageReceiver imageReceiver) {
            this.val$type = i;
            this.val$imageReceiver = imageReceiver;
        }

        public void run() {
            int start = 0;
            int count = 2;
            if (this.val$type == 1) {
                count = 1;
            } else if (this.val$type == 2) {
                start = 1;
            }
            int a = start;
            while (a < count) {
                Integer TAG = this.val$imageReceiver.getTag(a == 0);
                if (a == 0) {
                    ImageLoader.this.removeFromWaitingForThumb(TAG);
                }
                if (TAG != null) {
                    CacheImage ei = (CacheImage) ImageLoader.this.imageLoadingByTag.get(TAG);
                    if (ei != null) {
                        ei.removeImageReceiver(this.val$imageReceiver);
                    }
                }
                a++;
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.6 */
    class C05096 implements Runnable {
        final /* synthetic */ String val$newKey;
        final /* synthetic */ FileLocation val$newLocation;
        final /* synthetic */ String val$oldKey;

        C05096(String str, String str2, FileLocation fileLocation) {
            this.val$oldKey = str;
            this.val$newKey = str2;
            this.val$newLocation = fileLocation;
        }

        public void run() {
            ImageLoader.this.replaceImageInCacheInternal(this.val$oldKey, this.val$newKey, this.val$newLocation);
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.7 */
    class C05107 implements Runnable {
        final /* synthetic */ boolean val$cacheOnly;
        final /* synthetic */ String val$ext;
        final /* synthetic */ String val$filter;
        final /* synthetic */ boolean val$finalIsNeedsQualityThumb;
        final /* synthetic */ Integer val$finalTag;
        final /* synthetic */ String val$httpLocation;
        final /* synthetic */ TLObject val$imageLocation;
        final /* synthetic */ ImageReceiver val$imageReceiver;
        final /* synthetic */ String val$key;
        final /* synthetic */ MessageObject val$parentMessageObject;
        final /* synthetic */ boolean val$shouldGenerateQualityThumb;
        final /* synthetic */ int val$size;
        final /* synthetic */ int val$thumb;
        final /* synthetic */ String val$url;

        C05107(int i, String str, String str2, Integer num, ImageReceiver imageReceiver, String str3, boolean z, MessageObject messageObject, TLObject tLObject, String str4, boolean z2, boolean z3, int i2, String str5) {
            this.val$thumb = i;
            this.val$url = str;
            this.val$key = str2;
            this.val$finalTag = num;
            this.val$imageReceiver = imageReceiver;
            this.val$httpLocation = str3;
            this.val$finalIsNeedsQualityThumb = z;
            this.val$parentMessageObject = messageObject;
            this.val$imageLocation = tLObject;
            this.val$filter = str4;
            this.val$shouldGenerateQualityThumb = z2;
            this.val$cacheOnly = z3;
            this.val$size = i2;
            this.val$ext = str5;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r23 = this;
            r5 = 0;
            r0 = r23;
            r0 = r0.val$thumb;
            r19 = r0;
            r20 = 2;
            r0 = r19;
            r1 = r20;
            if (r0 == r1) goto L_0x0078;
        L_0x000f:
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.imageLoadingByUrl;
            r0 = r23;
            r0 = r0.val$url;
            r20 = r0;
            r8 = r19.get(r20);
            r8 = (org.telegram.messenger.ImageLoader.CacheImage) r8;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.imageLoadingByKeys;
            r0 = r23;
            r0 = r0.val$key;
            r20 = r0;
            r6 = r19.get(r20);
            r6 = (org.telegram.messenger.ImageLoader.CacheImage) r6;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.imageLoadingByTag;
            r0 = r23;
            r0 = r0.val$finalTag;
            r20 = r0;
            r7 = r19.get(r20);
            r7 = (org.telegram.messenger.ImageLoader.CacheImage) r7;
            if (r7 == 0) goto L_0x0058;
        L_0x0053:
            if (r7 == r8) goto L_0x0057;
        L_0x0055:
            if (r7 != r6) goto L_0x020b;
        L_0x0057:
            r5 = 1;
        L_0x0058:
            if (r5 != 0) goto L_0x0068;
        L_0x005a:
            if (r6 == 0) goto L_0x0068;
        L_0x005c:
            r0 = r23;
            r0 = r0.val$imageReceiver;
            r19 = r0;
            r0 = r19;
            r6.addImageReceiver(r0);
            r5 = 1;
        L_0x0068:
            if (r5 != 0) goto L_0x0078;
        L_0x006a:
            if (r8 == 0) goto L_0x0078;
        L_0x006c:
            r0 = r23;
            r0 = r0.val$imageReceiver;
            r19 = r0;
            r0 = r19;
            r8.addImageReceiver(r0);
            r5 = 1;
        L_0x0078:
            if (r5 != 0) goto L_0x020a;
        L_0x007a:
            r18 = 0;
            r16 = 0;
            r11 = 0;
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            if (r19 == 0) goto L_0x025a;
        L_0x0087:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "http";
            r19 = r19.startsWith(r20);
            if (r19 != 0) goto L_0x00c8;
        L_0x0095:
            r18 = 1;
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "thumb://";
            r19 = r19.startsWith(r20);
            if (r19 == 0) goto L_0x0218;
        L_0x00a5:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = ":";
            r21 = 8;
            r13 = r19.indexOf(r20, r21);
            if (r13 < 0) goto L_0x00c8;
        L_0x00b5:
            r11 = new java.io.File;
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = r13 + 1;
            r19 = r19.substring(r20);
            r0 = r19;
            r11.<init>(r0);
        L_0x00c8:
            r0 = r23;
            r0 = r0.val$thumb;
            r19 = r0;
            r20 = 2;
            r0 = r19;
            r1 = r20;
            if (r0 == r1) goto L_0x020a;
        L_0x00d6:
            r14 = new org.telegram.messenger.ImageLoader$CacheImage;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r20 = 0;
            r0 = r19;
            r1 = r20;
            r14.<init>(r1);
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            if (r19 == 0) goto L_0x0127;
        L_0x00ef:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "vthumb";
            r19 = r19.startsWith(r20);
            if (r19 != 0) goto L_0x0127;
        L_0x00fd:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "thumb";
            r19 = r19.startsWith(r20);
            if (r19 != 0) goto L_0x0127;
        L_0x010b:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "mp4";
            r19 = r19.endsWith(r20);
            if (r19 != 0) goto L_0x0143;
        L_0x0119:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "gif";
            r19 = r19.endsWith(r20);
            if (r19 != 0) goto L_0x0143;
        L_0x0127:
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r0 = r19;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
            r19 = r0;
            if (r19 == 0) goto L_0x0149;
        L_0x0135:
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r19 = (org.telegram.tgnet.TLRPC.Document) r19;
            r19 = org.telegram.messenger.MessageObject.isGifDocument(r19);
            if (r19 == 0) goto L_0x0149;
        L_0x0143:
            r19 = 1;
            r0 = r19;
            r14.animatedFile = r0;
        L_0x0149:
            if (r11 != 0) goto L_0x017c;
        L_0x014b:
            r0 = r23;
            r0 = r0.val$cacheOnly;
            r19 = r0;
            if (r19 != 0) goto L_0x0163;
        L_0x0153:
            r0 = r23;
            r0 = r0.val$size;
            r19 = r0;
            if (r19 == 0) goto L_0x0163;
        L_0x015b:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            if (r19 == 0) goto L_0x03bd;
        L_0x0163:
            r11 = new java.io.File;
            r19 = org.telegram.messenger.FileLoader.getInstance();
            r20 = 4;
            r19 = r19.getDirectory(r20);
            r0 = r23;
            r0 = r0.val$url;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r11.<init>(r0, r1);
        L_0x017c:
            r0 = r23;
            r0 = r0.val$thumb;
            r19 = r0;
            if (r19 == 0) goto L_0x0401;
        L_0x0184:
            r19 = 1;
        L_0x0186:
            r0 = r19;
            r14.thumb = r0;
            r0 = r23;
            r0 = r0.val$key;
            r19 = r0;
            r0 = r19;
            r14.key = r0;
            r0 = r23;
            r0 = r0.val$filter;
            r19 = r0;
            r0 = r19;
            r14.filter = r0;
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r0 = r19;
            r14.httpUrl = r0;
            r0 = r23;
            r0 = r0.val$ext;
            r19 = r0;
            r0 = r19;
            r14.ext = r0;
            r0 = r23;
            r0 = r0.val$imageReceiver;
            r19 = r0;
            r0 = r19;
            r14.addImageReceiver(r0);
            if (r18 != 0) goto L_0x01c5;
        L_0x01bf:
            r19 = r11.exists();
            if (r19 == 0) goto L_0x0418;
        L_0x01c5:
            r14.finalFilePath = r11;
            r19 = new org.telegram.messenger.ImageLoader$CacheOutTask;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r0.<init>(r14);
            r0 = r19;
            r14.cacheTask = r0;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.imageLoadingByKeys;
            r0 = r23;
            r0 = r0.val$key;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r0.put(r1, r14);
            r0 = r23;
            r0 = r0.val$thumb;
            r19 = r0;
            if (r19 == 0) goto L_0x0405;
        L_0x01f9:
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.cacheThumbOutQueue;
            r0 = r14.cacheTask;
            r20 = r0;
            r19.postRunnable(r20);
        L_0x020a:
            return;
        L_0x020b:
            r0 = r23;
            r0 = r0.val$imageReceiver;
            r19 = r0;
            r0 = r19;
            r7.removeImageReceiver(r0);
            goto L_0x0058;
        L_0x0218:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = "vthumb://";
            r19 = r19.startsWith(r20);
            if (r19 == 0) goto L_0x024b;
        L_0x0226:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = ":";
            r21 = 9;
            r13 = r19.indexOf(r20, r21);
            if (r13 < 0) goto L_0x00c8;
        L_0x0236:
            r11 = new java.io.File;
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r20 = r13 + 1;
            r19 = r19.substring(r20);
            r0 = r19;
            r11.<init>(r0);
            goto L_0x00c8;
        L_0x024b:
            r11 = new java.io.File;
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r0 = r19;
            r11.<init>(r0);
            goto L_0x00c8;
        L_0x025a:
            r0 = r23;
            r0 = r0.val$thumb;
            r19 = r0;
            if (r19 == 0) goto L_0x00c8;
        L_0x0262:
            r0 = r23;
            r0 = r0.val$finalIsNeedsQualityThumb;
            r19 = r0;
            if (r19 == 0) goto L_0x029d;
        L_0x026a:
            r11 = new java.io.File;
            r19 = org.telegram.messenger.FileLoader.getInstance();
            r20 = 4;
            r19 = r19.getDirectory(r20);
            r20 = new java.lang.StringBuilder;
            r20.<init>();
            r21 = "q_";
            r20 = r20.append(r21);
            r0 = r23;
            r0 = r0.val$url;
            r21 = r0;
            r20 = r20.append(r21);
            r20 = r20.toString();
            r0 = r19;
            r1 = r20;
            r11.<init>(r0, r1);
            r19 = r11.exists();
            if (r19 != 0) goto L_0x029d;
        L_0x029c:
            r11 = 0;
        L_0x029d:
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            if (r19 == 0) goto L_0x00c8;
        L_0x02a5:
            r9 = 0;
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            r0 = r19;
            r0 = r0.messageOwner;
            r19 = r0;
            r0 = r19;
            r0 = r0.attachPath;
            r19 = r0;
            if (r19 == 0) goto L_0x02f2;
        L_0x02ba:
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            r0 = r19;
            r0 = r0.messageOwner;
            r19 = r0;
            r0 = r19;
            r0 = r0.attachPath;
            r19 = r0;
            r19 = r19.length();
            if (r19 <= 0) goto L_0x02f2;
        L_0x02d2:
            r9 = new java.io.File;
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            r0 = r19;
            r0 = r0.messageOwner;
            r19 = r0;
            r0 = r19;
            r0 = r0.attachPath;
            r19 = r0;
            r0 = r19;
            r9.<init>(r0);
            r19 = r9.exists();
            if (r19 != 0) goto L_0x02f2;
        L_0x02f1:
            r9 = 0;
        L_0x02f2:
            if (r9 != 0) goto L_0x0304;
        L_0x02f4:
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            r0 = r19;
            r0 = r0.messageOwner;
            r19 = r0;
            r9 = org.telegram.messenger.FileLoader.getPathToMessage(r19);
        L_0x0304:
            r0 = r23;
            r0 = r0.val$finalIsNeedsQualityThumb;
            r19 = r0;
            if (r19 == 0) goto L_0x0384;
        L_0x030c:
            if (r11 != 0) goto L_0x0384;
        L_0x030e:
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            r17 = r19.getFileName();
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.waitingForQualityThumb;
            r0 = r19;
            r1 = r17;
            r15 = r0.get(r1);
            r15 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r15;
            if (r15 != 0) goto L_0x0368;
        L_0x032e:
            r15 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r20 = 0;
            r0 = r19;
            r1 = r20;
            r15.<init>(r1);
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r19 = (org.telegram.tgnet.TLRPC.TL_fileLocation) r19;
            r0 = r19;
            r15.fileLocation = r0;
            r0 = r23;
            r0 = r0.val$filter;
            r19 = r0;
            r0 = r19;
            r15.filter = r0;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.waitingForQualityThumb;
            r0 = r19;
            r1 = r17;
            r0.put(r1, r15);
        L_0x0368:
            r15.count = r15.count + 1;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.waitingForQualityThumbByTag;
            r0 = r23;
            r0 = r0.val$finalTag;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r2 = r17;
            r0.put(r1, r2);
        L_0x0384:
            r19 = r9.exists();
            if (r19 == 0) goto L_0x00c8;
        L_0x038a:
            r0 = r23;
            r0 = r0.val$shouldGenerateQualityThumb;
            r19 = r0;
            if (r19 == 0) goto L_0x00c8;
        L_0x0392:
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r20 = r0;
            r0 = r23;
            r0 = r0.val$parentMessageObject;
            r19 = r0;
            r21 = r19.getFileType();
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r19 = (org.telegram.tgnet.TLRPC.TL_fileLocation) r19;
            r0 = r23;
            r0 = r0.val$filter;
            r22 = r0;
            r0 = r20;
            r1 = r21;
            r2 = r19;
            r3 = r22;
            r0.generateThumb(r1, r9, r2, r3);
            goto L_0x00c8;
        L_0x03bd:
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r0 = r19;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
            r19 = r0;
            if (r19 == 0) goto L_0x03e6;
        L_0x03cb:
            r11 = new java.io.File;
            r19 = org.telegram.messenger.FileLoader.getInstance();
            r20 = 3;
            r19 = r19.getDirectory(r20);
            r0 = r23;
            r0 = r0.val$url;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r11.<init>(r0, r1);
            goto L_0x017c;
        L_0x03e6:
            r11 = new java.io.File;
            r19 = org.telegram.messenger.FileLoader.getInstance();
            r20 = 0;
            r19 = r19.getDirectory(r20);
            r0 = r23;
            r0 = r0.val$url;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r11.<init>(r0, r1);
            goto L_0x017c;
        L_0x0401:
            r19 = 0;
            goto L_0x0186;
        L_0x0405:
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.cacheOutQueue;
            r0 = r14.cacheTask;
            r20 = r0;
            r19.postRunnable(r20);
            goto L_0x020a;
        L_0x0418:
            r0 = r23;
            r0 = r0.val$url;
            r19 = r0;
            r0 = r19;
            r14.url = r0;
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r0 = r19;
            r14.location = r0;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.imageLoadingByUrl;
            r0 = r23;
            r0 = r0.val$url;
            r20 = r0;
            r0 = r19;
            r1 = r20;
            r0.put(r1, r14);
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            if (r19 != 0) goto L_0x04cc;
        L_0x044b:
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r0 = r19;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.FileLocation;
            r19 = r0;
            if (r19 == 0) goto L_0x049d;
        L_0x0459:
            r0 = r23;
            r0 = r0.val$imageLocation;
            r17 = r0;
            r17 = (org.telegram.tgnet.TLRPC.FileLocation) r17;
            r20 = org.telegram.messenger.FileLoader.getInstance();
            r0 = r23;
            r0 = r0.val$ext;
            r21 = r0;
            r0 = r23;
            r0 = r0.val$size;
            r22 = r0;
            r0 = r23;
            r0 = r0.val$size;
            r19 = r0;
            if (r19 == 0) goto L_0x0489;
        L_0x0479:
            r0 = r17;
            r0 = r0.key;
            r19 = r0;
            if (r19 != 0) goto L_0x0489;
        L_0x0481:
            r0 = r23;
            r0 = r0.val$cacheOnly;
            r19 = r0;
            if (r19 == 0) goto L_0x049a;
        L_0x0489:
            r19 = 1;
        L_0x048b:
            r0 = r20;
            r1 = r17;
            r2 = r21;
            r3 = r22;
            r4 = r19;
            r0.loadFile(r1, r2, r3, r4);
            goto L_0x020a;
        L_0x049a:
            r19 = 0;
            goto L_0x048b;
        L_0x049d:
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r0 = r19;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
            r19 = r0;
            if (r19 == 0) goto L_0x020a;
        L_0x04ab:
            r20 = org.telegram.messenger.FileLoader.getInstance();
            r0 = r23;
            r0 = r0.val$imageLocation;
            r19 = r0;
            r19 = (org.telegram.tgnet.TLRPC.Document) r19;
            r21 = 1;
            r0 = r23;
            r0 = r0.val$cacheOnly;
            r22 = r0;
            r0 = r20;
            r1 = r19;
            r2 = r21;
            r3 = r22;
            r0.loadFile(r1, r2, r3);
            goto L_0x020a;
        L_0x04cc:
            r0 = r23;
            r0 = r0.val$httpLocation;
            r19 = r0;
            r12 = org.telegram.messenger.Utilities.MD5(r19);
            r19 = org.telegram.messenger.FileLoader.getInstance();
            r20 = 4;
            r10 = r19.getDirectory(r20);
            r19 = new java.io.File;
            r20 = new java.lang.StringBuilder;
            r20.<init>();
            r0 = r20;
            r20 = r0.append(r12);
            r21 = "_temp.jpg";
            r20 = r20.append(r21);
            r20 = r20.toString();
            r0 = r19;
            r1 = r20;
            r0.<init>(r10, r1);
            r0 = r19;
            r14.tempFilePath = r0;
            r14.finalFilePath = r11;
            r19 = new org.telegram.messenger.ImageLoader$HttpImageTask;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r20 = r0;
            r0 = r23;
            r0 = r0.val$size;
            r21 = r0;
            r0 = r19;
            r1 = r20;
            r2 = r21;
            r0.<init>(r14, r2);
            r0 = r19;
            r14.httpTask = r0;
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r19 = r19.httpTasks;
            r0 = r14.httpTask;
            r20 = r0;
            r19.add(r20);
            r0 = r23;
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0;
            r20 = 0;
            r19.runHttpTasks(r20);
            goto L_0x020a;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.7.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.8 */
    class C05118 implements Runnable {
        final /* synthetic */ String val$location;

        C05118(String str) {
            this.val$location = str;
        }

        public void run() {
            CacheImage img = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$location);
            if (img != null) {
                HttpImageTask oldTask = img.httpTask;
                img.httpTask = new HttpImageTask(oldTask.cacheImage, oldTask.imageSize);
                ImageLoader.this.httpTasks.add(img.httpTask);
                ImageLoader.this.runHttpTasks(false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.9 */
    class C05129 implements Runnable {
        final /* synthetic */ File val$finalFile;
        final /* synthetic */ String val$location;
        final /* synthetic */ int val$type;

        C05129(String str, int i, File file) {
            this.val$location = str;
            this.val$type = i;
            this.val$finalFile = file;
        }

        public void run() {
            ThumbGenerateInfo info = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(this.val$location);
            if (info != null) {
                ImageLoader.this.generateThumb(this.val$type, this.val$finalFile, info.fileLocation, info.filter);
                ImageLoader.this.waitingForQualityThumb.remove(this.val$location);
            }
            CacheImage img = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$location);
            if (img != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.val$location);
                CacheOutTask task = null;
                for (int a = 0; a < img.imageReceiverArray.size(); a++) {
                    ImageReceiver imageReceiver = (ImageReceiver) img.imageReceiverArray.get(a);
                    CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(img.key);
                    if (cacheImage == null) {
                        cacheImage = new CacheImage(null);
                        cacheImage.finalFilePath = this.val$finalFile;
                        cacheImage.key = img.key;
                        cacheImage.httpUrl = img.httpUrl;
                        cacheImage.thumb = img.thumb;
                        cacheImage.ext = img.ext;
                        task = new CacheOutTask(cacheImage);
                        cacheImage.cacheTask = task;
                        cacheImage.filter = img.filter;
                        cacheImage.animatedFile = img.animatedFile;
                        ImageLoader.this.imageLoadingByKeys.put(cacheImage.key, cacheImage);
                    }
                    cacheImage.addImageReceiver(imageReceiver);
                }
                if (task == null) {
                    return;
                }
                if (img.thumb) {
                    ImageLoader.this.cacheThumbOutQueue.postRunnable(task);
                } else {
                    ImageLoader.this.cacheOutQueue.postRunnable(task);
                }
            }
        }
    }

    private class CacheImage {
        protected boolean animatedFile;
        protected CacheOutTask cacheTask;
        protected String ext;
        protected String filter;
        protected File finalFilePath;
        protected HttpImageTask httpTask;
        protected String httpUrl;
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected String key;
        protected TLObject location;
        protected File tempFilePath;
        protected boolean thumb;
        protected String url;

        /* renamed from: org.telegram.messenger.ImageLoader.CacheImage.1 */
        class C05131 implements Runnable {
            final /* synthetic */ ArrayList val$finalImageReceiverArray;
            final /* synthetic */ BitmapDrawable val$image;

            C05131(BitmapDrawable bitmapDrawable, ArrayList arrayList) {
                this.val$image = bitmapDrawable;
                this.val$finalImageReceiverArray = arrayList;
            }

            public void run() {
                int a;
                if (this.val$image instanceof AnimatedFileDrawable) {
                    boolean imageSet = false;
                    BitmapDrawable fileDrawable = this.val$image;
                    a = 0;
                    while (a < this.val$finalImageReceiverArray.size()) {
                        if (((ImageReceiver) this.val$finalImageReceiverArray.get(a)).setImageBitmapByKey(a == 0 ? fileDrawable : fileDrawable.makeCopy(), CacheImage.this.key, CacheImage.this.thumb, false)) {
                            imageSet = true;
                        }
                        a++;
                    }
                    if (!imageSet) {
                        ((AnimatedFileDrawable) this.val$image).recycle();
                        return;
                    }
                    return;
                }
                for (a = 0; a < this.val$finalImageReceiverArray.size(); a++) {
                    ((ImageReceiver) this.val$finalImageReceiverArray.get(a)).setImageBitmapByKey(this.val$image, CacheImage.this.key, CacheImage.this.thumb, false);
                }
            }
        }

        private CacheImage() {
            this.imageReceiverArray = new ArrayList();
        }

        public void addImageReceiver(ImageReceiver imageReceiver) {
            boolean exist = false;
            Iterator i$ = this.imageReceiverArray.iterator();
            while (i$.hasNext()) {
                if (((ImageReceiver) i$.next()) == imageReceiver) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                this.imageReceiverArray.add(imageReceiver);
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(this.thumb), this);
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int a = 0;
            while (a < this.imageReceiverArray.size()) {
                ImageReceiver obj = (ImageReceiver) this.imageReceiverArray.get(a);
                if (obj == null || obj == imageReceiver) {
                    this.imageReceiverArray.remove(a);
                    if (obj != null) {
                        ImageLoader.this.imageLoadingByTag.remove(obj.getTag(this.thumb));
                    }
                    a--;
                }
                a++;
            }
            if (this.imageReceiverArray.size() == 0) {
                for (a = 0; a < this.imageReceiverArray.size(); a++) {
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a)).getTag(this.thumb));
                }
                this.imageReceiverArray.clear();
                if (this.location != null) {
                    if (this.location instanceof FileLocation) {
                        FileLoader.getInstance().cancelLoadFile((FileLocation) this.location, this.ext);
                    } else if (this.location instanceof Document) {
                        FileLoader.getInstance().cancelLoadFile((Document) this.location);
                    }
                }
                if (this.cacheTask != null) {
                    if (this.thumb) {
                        ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
                    } else {
                        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
                    }
                    this.cacheTask.cancel();
                    this.cacheTask = null;
                }
                if (this.httpTask != null) {
                    ImageLoader.this.httpTasks.remove(this.httpTask);
                    this.httpTask.cancel(true);
                    this.httpTask = null;
                }
                if (this.url != null) {
                    ImageLoader.this.imageLoadingByUrl.remove(this.url);
                }
                if (this.key != null) {
                    ImageLoader.this.imageLoadingByKeys.remove(this.key);
                }
            }
        }

        public void setImageAndClear(BitmapDrawable image) {
            if (image != null) {
                AndroidUtilities.runOnUIThread(new C05131(image, new ArrayList(this.imageReceiverArray)));
            }
            for (int a = 0; a < this.imageReceiverArray.size(); a++) {
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a)).getTag(this.thumb));
            }
            this.imageReceiverArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }
    }

    private class CacheOutTask implements Runnable {
        private CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync;

        /* renamed from: org.telegram.messenger.ImageLoader.CacheOutTask.1 */
        class C05151 implements Runnable {
            final /* synthetic */ BitmapDrawable val$bitmapDrawable;

            /* renamed from: org.telegram.messenger.ImageLoader.CacheOutTask.1.1 */
            class C05141 implements Runnable {
                final /* synthetic */ BitmapDrawable val$toSetFinal;

                C05141(BitmapDrawable bitmapDrawable) {
                    this.val$toSetFinal = bitmapDrawable;
                }

                public void run() {
                    CacheOutTask.this.cacheImage.setImageAndClear(this.val$toSetFinal);
                }
            }

            C05151(BitmapDrawable bitmapDrawable) {
                this.val$bitmapDrawable = bitmapDrawable;
            }

            public void run() {
                BitmapDrawable toSet = null;
                if (this.val$bitmapDrawable instanceof AnimatedFileDrawable) {
                    toSet = this.val$bitmapDrawable;
                } else if (this.val$bitmapDrawable != null) {
                    toSet = ImageLoader.this.memCache.get(CacheOutTask.this.cacheImage.key);
                    if (toSet == null) {
                        ImageLoader.this.memCache.put(CacheOutTask.this.cacheImage.key, this.val$bitmapDrawable);
                        toSet = this.val$bitmapDrawable;
                    } else {
                        this.val$bitmapDrawable.getBitmap().recycle();
                    }
                }
                ImageLoader.this.imageLoadQueue.postRunnable(new C05141(toSet));
            }
        }

        public CacheOutTask(CacheImage image) {
            this.sync = new Object();
            this.cacheImage = image;
        }

        public void run() {
            Throwable e;
            Options opts;
            float f;
            boolean z;
            String[] args;
            InputStream fileInputStream;
            Bitmap decodeStream;
            ByteBuffer buffer;
            Options bmOptions;
            int len;
            float bitmapW;
            float bitmapH;
            Bitmap scaledBitmap;
            BitmapDrawable bitmapDrawable;
            Throwable th;
            synchronized (this.sync) {
                this.runningThread = Thread.currentThread();
                Thread.interrupted();
                if (this.isCancelled) {
                    return;
                }
                if (this.cacheImage.animatedFile) {
                    synchronized (this.sync) {
                        if (this.isCancelled) {
                            return;
                        }
                        File file = this.cacheImage.finalFilePath;
                        boolean z2 = this.cacheImage.filter != null && this.cacheImage.filter.equals("d");
                        BitmapDrawable animatedFileDrawable = new AnimatedFileDrawable(file, z2);
                        Thread.interrupted();
                        onPostExecute(animatedFileDrawable);
                        return;
                    }
                }
                RandomAccessFile randomAccessFile;
                int idx;
                int delay;
                float h_filter;
                float scaleFactor;
                byte[] data;
                boolean blured;
                int blurType;
                Long mediaId = null;
                boolean mediaIsVideo = false;
                File cacheFileFinal = this.cacheImage.finalFilePath;
                boolean canDeleteFile = true;
                boolean useNativeWebpLoaded = false;
                if (VERSION.SDK_INT < 19) {
                    RandomAccessFile randomAccessFile2 = null;
                    try {
                        randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                        try {
                            byte[] bytes;
                            if (this.cacheImage.thumb) {
                                bytes = ImageLoader.headerThumb;
                            } else {
                                bytes = ImageLoader.header;
                            }
                            randomAccessFile.readFully(bytes, 0, bytes.length);
                            String str = new String(bytes).toLowerCase().toLowerCase();
                            if (str.startsWith("riff")) {
                                if (str.endsWith("webp")) {
                                    useNativeWebpLoaded = true;
                                }
                            }
                            randomAccessFile.close();
                            if (randomAccessFile != null) {
                                try {
                                    randomAccessFile.close();
                                } catch (Throwable e2) {
                                    FileLog.m13e("tmessages", e2);
                                }
                            }
                        } catch (Exception e3) {
                            e2 = e3;
                            randomAccessFile2 = randomAccessFile;
                            try {
                                FileLog.m13e("tmessages", e2);
                                if (randomAccessFile2 != null) {
                                    try {
                                        randomAccessFile2.close();
                                    } catch (Throwable e22) {
                                        FileLog.m13e("tmessages", e22);
                                    }
                                }
                                if (this.cacheImage.thumb) {
                                    try {
                                        if (this.cacheImage.httpUrl != null) {
                                            if (!this.cacheImage.httpUrl.startsWith("thumb://")) {
                                                idx = this.cacheImage.httpUrl.indexOf(":", 8);
                                                if (idx >= 0) {
                                                    mediaId = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(8, idx)));
                                                    mediaIsVideo = false;
                                                }
                                                canDeleteFile = false;
                                            } else if (!this.cacheImage.httpUrl.startsWith("vthumb://")) {
                                                idx = this.cacheImage.httpUrl.indexOf(":", 9);
                                                if (idx >= 0) {
                                                    mediaId = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(9, idx)));
                                                    mediaIsVideo = true;
                                                }
                                                canDeleteFile = false;
                                            } else if (!this.cacheImage.httpUrl.startsWith("http")) {
                                                canDeleteFile = false;
                                            }
                                        }
                                        delay = 20;
                                        if (mediaId != null) {
                                            delay = 0;
                                        }
                                        Thread.sleep((long) delay);
                                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                        synchronized (this.sync) {
                                            if (this.isCancelled) {
                                                opts = new Options();
                                                opts.inSampleSize = 1;
                                                f = 0.0f;
                                                h_filter = 0.0f;
                                                z = false;
                                                if (this.cacheImage.filter != null) {
                                                    args = this.cacheImage.filter.split("_");
                                                    if (args.length >= 2) {
                                                        f = Float.parseFloat(args[0]) * AndroidUtilities.density;
                                                        h_filter = Float.parseFloat(args[1]) * AndroidUtilities.density;
                                                    }
                                                    if (this.cacheImage.filter.contains("b")) {
                                                        z = true;
                                                    }
                                                    opts.inJustDecodeBounds = true;
                                                    if (mediaId != null) {
                                                        fileInputStream = new FileInputStream(cacheFileFinal);
                                                        decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                                        fileInputStream.close();
                                                    } else if (mediaIsVideo) {
                                                        Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                                        decodeStream = null;
                                                    } else {
                                                        Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                                        decodeStream = null;
                                                    }
                                                    try {
                                                        scaleFactor = Math.max(((float) opts.outWidth) / f, ((float) opts.outHeight) / h_filter);
                                                        if (scaleFactor < TouchHelperCallback.ALPHA_FULL) {
                                                            scaleFactor = TouchHelperCallback.ALPHA_FULL;
                                                        }
                                                        opts.inJustDecodeBounds = false;
                                                        opts.inSampleSize = (int) scaleFactor;
                                                        synchronized (this.sync) {
                                                            if (this.isCancelled) {
                                                                if (this.cacheImage.filter == null) {
                                                                }
                                                                opts.inPreferredConfig = Config.ARGB_8888;
                                                                if (VERSION.SDK_INT < 21) {
                                                                    opts.inPurgeable = true;
                                                                }
                                                                opts.inDither = false;
                                                                if (mediaId != null) {
                                                                    if (mediaIsVideo) {
                                                                        decodeStream = Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                                                    } else {
                                                                        decodeStream = Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                                                    }
                                                                }
                                                                if (decodeStream == null) {
                                                                    if (useNativeWebpLoaded) {
                                                                        randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                                        buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                                                        bmOptions = new Options();
                                                                        bmOptions.inJustDecodeBounds = true;
                                                                        Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                                                        decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                                                        Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                                                        randomAccessFile.close();
                                                                    } else if (opts.inPurgeable) {
                                                                        fileInputStream = new FileInputStream(cacheFileFinal);
                                                                        decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                                                        fileInputStream.close();
                                                                    } else {
                                                                        randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                                        len = (int) randomAccessFile.length();
                                                                        if (ImageLoader.bytes != null) {
                                                                        }
                                                                        if (data == null) {
                                                                            data = new byte[len];
                                                                            ImageLoader.bytes = data;
                                                                        }
                                                                        randomAccessFile.readFully(data, 0, len);
                                                                        decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                                                    }
                                                                }
                                                                if (decodeStream == null) {
                                                                    cacheFileFinal.delete();
                                                                } else {
                                                                    blured = false;
                                                                    if (this.cacheImage.filter != null) {
                                                                        bitmapW = (float) decodeStream.getWidth();
                                                                        bitmapH = (float) decodeStream.getHeight();
                                                                        scaledBitmap = Bitmaps.createScaledBitmap(decodeStream, (int) f, (int) (bitmapH / (bitmapW / f)), true);
                                                                        if (decodeStream != scaledBitmap) {
                                                                            decodeStream.recycle();
                                                                            decodeStream = scaledBitmap;
                                                                        }
                                                                        if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                                            Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 0 : 1, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                                        }
                                                                        blured = true;
                                                                    }
                                                                    Utilities.pinBitmap(decodeStream);
                                                                }
                                                            } else {
                                                                return;
                                                            }
                                                        }
                                                    } catch (Throwable th2) {
                                                    }
                                                }
                                                decodeStream = null;
                                                synchronized (this.sync) {
                                                    if (this.isCancelled) {
                                                        if (this.cacheImage.filter == null) {
                                                        }
                                                        opts.inPreferredConfig = Config.ARGB_8888;
                                                        if (VERSION.SDK_INT < 21) {
                                                            opts.inPurgeable = true;
                                                        }
                                                        opts.inDither = false;
                                                        if (mediaId != null) {
                                                            if (mediaIsVideo) {
                                                                decodeStream = Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                                            } else {
                                                                decodeStream = Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                                            }
                                                        }
                                                        if (decodeStream == null) {
                                                            if (useNativeWebpLoaded) {
                                                                randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                                buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                                                bmOptions = new Options();
                                                                bmOptions.inJustDecodeBounds = true;
                                                                Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                                                decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                                                if (opts.inPurgeable) {
                                                                }
                                                                Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                                                randomAccessFile.close();
                                                            } else if (opts.inPurgeable) {
                                                                fileInputStream = new FileInputStream(cacheFileFinal);
                                                                decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                                                fileInputStream.close();
                                                            } else {
                                                                randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                                len = (int) randomAccessFile.length();
                                                                if (ImageLoader.bytes != null) {
                                                                }
                                                                if (data == null) {
                                                                    data = new byte[len];
                                                                    ImageLoader.bytes = data;
                                                                }
                                                                randomAccessFile.readFully(data, 0, len);
                                                                decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                                            }
                                                        }
                                                        if (decodeStream == null) {
                                                            blured = false;
                                                            if (this.cacheImage.filter != null) {
                                                                bitmapW = (float) decodeStream.getWidth();
                                                                bitmapH = (float) decodeStream.getHeight();
                                                                scaledBitmap = Bitmaps.createScaledBitmap(decodeStream, (int) f, (int) (bitmapH / (bitmapW / f)), true);
                                                                if (decodeStream != scaledBitmap) {
                                                                    decodeStream.recycle();
                                                                    decodeStream = scaledBitmap;
                                                                }
                                                                if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                                    if (opts.inPurgeable) {
                                                                    }
                                                                    Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 0 : 1, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                                }
                                                                blured = true;
                                                            }
                                                            Utilities.pinBitmap(decodeStream);
                                                        } else {
                                                            cacheFileFinal.delete();
                                                        }
                                                    } else {
                                                        return;
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                    } catch (Throwable th3) {
                                        decodeStream = null;
                                    }
                                } else {
                                    blurType = 0;
                                    if (this.cacheImage.filter != null) {
                                        if (!this.cacheImage.filter.contains("b2")) {
                                            blurType = 3;
                                        } else if (!this.cacheImage.filter.contains("b1")) {
                                            blurType = 2;
                                        } else if (this.cacheImage.filter.contains("b")) {
                                            blurType = 1;
                                        }
                                    }
                                    try {
                                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                        synchronized (this.sync) {
                                            if (this.isCancelled) {
                                                opts = new Options();
                                                opts.inSampleSize = 1;
                                                if (VERSION.SDK_INT < 21) {
                                                    opts.inPurgeable = true;
                                                }
                                                if (!useNativeWebpLoaded) {
                                                    randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                    buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                                    bmOptions = new Options();
                                                    bmOptions.inJustDecodeBounds = true;
                                                    Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                                    decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                                    try {
                                                        Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                                        randomAccessFile.close();
                                                    } catch (Throwable th4) {
                                                        e22 = th4;
                                                        FileLog.m13e("tmessages", e22);
                                                        Thread.interrupted();
                                                        if (decodeStream == null) {
                                                            bitmapDrawable = new BitmapDrawable(decodeStream);
                                                        } else {
                                                            bitmapDrawable = null;
                                                        }
                                                        onPostExecute(bitmapDrawable);
                                                    }
                                                } else if (opts.inPurgeable) {
                                                    fileInputStream = new FileInputStream(cacheFileFinal);
                                                    decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                                    fileInputStream.close();
                                                } else {
                                                    randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                    len = (int) randomAccessFile.length();
                                                    if (ImageLoader.bytesThumb != null) {
                                                    }
                                                    if (data == null) {
                                                        data = new byte[len];
                                                        ImageLoader.bytesThumb = data;
                                                    }
                                                    randomAccessFile.readFully(data, 0, len);
                                                    decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                                }
                                                if (decodeStream != null) {
                                                    cacheFileFinal.delete();
                                                } else if (blurType == 1) {
                                                    if (blurType == 2) {
                                                        if (blurType != 3) {
                                                            Utilities.pinBitmap(decodeStream);
                                                        } else if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                            Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                            Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                            Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                        }
                                                    } else if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                        Utilities.blurBitmap(decodeStream, 1, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                    }
                                                } else if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                    Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                }
                                            } else {
                                                return;
                                            }
                                        }
                                    } catch (Throwable th5) {
                                        e22 = th5;
                                        decodeStream = null;
                                    }
                                }
                                Thread.interrupted();
                                if (decodeStream == null) {
                                    bitmapDrawable = null;
                                } else {
                                    bitmapDrawable = new BitmapDrawable(decodeStream);
                                }
                                onPostExecute(bitmapDrawable);
                            } catch (Throwable th6) {
                                th = th6;
                                if (randomAccessFile2 != null) {
                                    try {
                                        randomAccessFile2.close();
                                    } catch (Throwable e222) {
                                        FileLog.m13e("tmessages", e222);
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            randomAccessFile2 = randomAccessFile;
                            if (randomAccessFile2 != null) {
                                randomAccessFile2.close();
                            }
                            throw th;
                        }
                    } catch (Exception e4) {
                        e222 = e4;
                        FileLog.m13e("tmessages", e222);
                        if (randomAccessFile2 != null) {
                            randomAccessFile2.close();
                        }
                        if (this.cacheImage.thumb) {
                            blurType = 0;
                            if (this.cacheImage.filter != null) {
                                if (!this.cacheImage.filter.contains("b2")) {
                                    blurType = 3;
                                } else if (!this.cacheImage.filter.contains("b1")) {
                                    blurType = 2;
                                } else if (this.cacheImage.filter.contains("b")) {
                                    blurType = 1;
                                }
                            }
                            ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                            synchronized (this.sync) {
                                if (this.isCancelled) {
                                    return;
                                }
                                opts = new Options();
                                opts.inSampleSize = 1;
                                if (VERSION.SDK_INT < 21) {
                                    opts.inPurgeable = true;
                                }
                                if (!useNativeWebpLoaded) {
                                    randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                    buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                    bmOptions = new Options();
                                    bmOptions.inJustDecodeBounds = true;
                                    Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                    decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                    if (opts.inPurgeable) {
                                    }
                                    Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                    randomAccessFile.close();
                                } else if (opts.inPurgeable) {
                                    randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                    len = (int) randomAccessFile.length();
                                    if (ImageLoader.bytesThumb != null) {
                                    }
                                    if (data == null) {
                                        data = new byte[len];
                                        ImageLoader.bytesThumb = data;
                                    }
                                    randomAccessFile.readFully(data, 0, len);
                                    decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                } else {
                                    fileInputStream = new FileInputStream(cacheFileFinal);
                                    decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                    fileInputStream.close();
                                }
                                if (decodeStream != null) {
                                    cacheFileFinal.delete();
                                } else if (blurType == 1) {
                                    if (decodeStream.getConfig() == Config.ARGB_8888) {
                                        if (opts.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                    }
                                } else if (blurType == 2) {
                                    if (decodeStream.getConfig() == Config.ARGB_8888) {
                                        if (opts.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(decodeStream, 1, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                    }
                                } else if (blurType != 3) {
                                    Utilities.pinBitmap(decodeStream);
                                } else if (decodeStream.getConfig() == Config.ARGB_8888) {
                                    if (opts.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                    if (opts.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                    if (opts.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                }
                            }
                        } else {
                            if (this.cacheImage.httpUrl != null) {
                                if (!this.cacheImage.httpUrl.startsWith("thumb://")) {
                                    idx = this.cacheImage.httpUrl.indexOf(":", 8);
                                    if (idx >= 0) {
                                        mediaId = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(8, idx)));
                                        mediaIsVideo = false;
                                    }
                                    canDeleteFile = false;
                                } else if (!this.cacheImage.httpUrl.startsWith("vthumb://")) {
                                    idx = this.cacheImage.httpUrl.indexOf(":", 9);
                                    if (idx >= 0) {
                                        mediaId = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(9, idx)));
                                        mediaIsVideo = true;
                                    }
                                    canDeleteFile = false;
                                } else if (this.cacheImage.httpUrl.startsWith("http")) {
                                    canDeleteFile = false;
                                }
                            }
                            delay = 20;
                            if (mediaId != null) {
                                delay = 0;
                            }
                            Thread.sleep((long) delay);
                            ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                            synchronized (this.sync) {
                                if (this.isCancelled) {
                                    return;
                                }
                                opts = new Options();
                                opts.inSampleSize = 1;
                                f = 0.0f;
                                h_filter = 0.0f;
                                z = false;
                                if (this.cacheImage.filter != null) {
                                    args = this.cacheImage.filter.split("_");
                                    if (args.length >= 2) {
                                        f = Float.parseFloat(args[0]) * AndroidUtilities.density;
                                        h_filter = Float.parseFloat(args[1]) * AndroidUtilities.density;
                                    }
                                    if (this.cacheImage.filter.contains("b")) {
                                        z = true;
                                    }
                                    opts.inJustDecodeBounds = true;
                                    if (mediaId != null) {
                                        fileInputStream = new FileInputStream(cacheFileFinal);
                                        decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                        fileInputStream.close();
                                    } else if (mediaIsVideo) {
                                        Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                        decodeStream = null;
                                    } else {
                                        Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                        decodeStream = null;
                                    }
                                    scaleFactor = Math.max(((float) opts.outWidth) / f, ((float) opts.outHeight) / h_filter);
                                    if (scaleFactor < TouchHelperCallback.ALPHA_FULL) {
                                        scaleFactor = TouchHelperCallback.ALPHA_FULL;
                                    }
                                    opts.inJustDecodeBounds = false;
                                    opts.inSampleSize = (int) scaleFactor;
                                    synchronized (this.sync) {
                                        if (this.isCancelled) {
                                            return;
                                        }
                                        if (this.cacheImage.filter == null) {
                                        }
                                        opts.inPreferredConfig = Config.ARGB_8888;
                                        if (VERSION.SDK_INT < 21) {
                                            opts.inPurgeable = true;
                                        }
                                        opts.inDither = false;
                                        if (mediaId != null) {
                                            if (mediaIsVideo) {
                                                decodeStream = Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                            } else {
                                                decodeStream = Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                            }
                                        }
                                        if (decodeStream == null) {
                                            if (useNativeWebpLoaded) {
                                                randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                                bmOptions = new Options();
                                                bmOptions.inJustDecodeBounds = true;
                                                Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                                decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                                if (opts.inPurgeable) {
                                                }
                                                Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                                randomAccessFile.close();
                                            } else if (opts.inPurgeable) {
                                                randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                len = (int) randomAccessFile.length();
                                                if (ImageLoader.bytes != null) {
                                                }
                                                if (data == null) {
                                                    data = new byte[len];
                                                    ImageLoader.bytes = data;
                                                }
                                                randomAccessFile.readFully(data, 0, len);
                                                decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                            } else {
                                                fileInputStream = new FileInputStream(cacheFileFinal);
                                                decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                                fileInputStream.close();
                                            }
                                        }
                                        if (decodeStream == null) {
                                            cacheFileFinal.delete();
                                        } else {
                                            blured = false;
                                            if (this.cacheImage.filter != null) {
                                                bitmapW = (float) decodeStream.getWidth();
                                                bitmapH = (float) decodeStream.getHeight();
                                                scaledBitmap = Bitmaps.createScaledBitmap(decodeStream, (int) f, (int) (bitmapH / (bitmapW / f)), true);
                                                if (decodeStream != scaledBitmap) {
                                                    decodeStream.recycle();
                                                    decodeStream = scaledBitmap;
                                                }
                                                if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                    if (opts.inPurgeable) {
                                                    }
                                                    Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 0 : 1, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                }
                                                blured = true;
                                            }
                                            Utilities.pinBitmap(decodeStream);
                                        }
                                    }
                                }
                                decodeStream = null;
                                synchronized (this.sync) {
                                    if (this.isCancelled) {
                                        if (this.cacheImage.filter == null) {
                                        }
                                        opts.inPreferredConfig = Config.ARGB_8888;
                                        if (VERSION.SDK_INT < 21) {
                                            opts.inPurgeable = true;
                                        }
                                        opts.inDither = false;
                                        if (mediaId != null) {
                                            if (mediaIsVideo) {
                                                decodeStream = Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                            } else {
                                                decodeStream = Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                            }
                                        }
                                        if (decodeStream == null) {
                                            if (useNativeWebpLoaded) {
                                                randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                                bmOptions = new Options();
                                                bmOptions.inJustDecodeBounds = true;
                                                Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                                decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                                if (opts.inPurgeable) {
                                                }
                                                Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                                randomAccessFile.close();
                                            } else if (opts.inPurgeable) {
                                                fileInputStream = new FileInputStream(cacheFileFinal);
                                                decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                                fileInputStream.close();
                                            } else {
                                                randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                                len = (int) randomAccessFile.length();
                                                if (ImageLoader.bytes != null) {
                                                }
                                                if (data == null) {
                                                    data = new byte[len];
                                                    ImageLoader.bytes = data;
                                                }
                                                randomAccessFile.readFully(data, 0, len);
                                                decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                            }
                                        }
                                        if (decodeStream == null) {
                                            blured = false;
                                            if (this.cacheImage.filter != null) {
                                                bitmapW = (float) decodeStream.getWidth();
                                                bitmapH = (float) decodeStream.getHeight();
                                                scaledBitmap = Bitmaps.createScaledBitmap(decodeStream, (int) f, (int) (bitmapH / (bitmapW / f)), true);
                                                if (decodeStream != scaledBitmap) {
                                                    decodeStream.recycle();
                                                    decodeStream = scaledBitmap;
                                                }
                                                if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                    if (opts.inPurgeable) {
                                                    }
                                                    Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 0 : 1, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                }
                                                blured = true;
                                            }
                                            Utilities.pinBitmap(decodeStream);
                                        } else {
                                            cacheFileFinal.delete();
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                        }
                        Thread.interrupted();
                        if (decodeStream == null) {
                            bitmapDrawable = null;
                        } else {
                            bitmapDrawable = new BitmapDrawable(decodeStream);
                        }
                        onPostExecute(bitmapDrawable);
                    }
                }
                if (this.cacheImage.thumb) {
                    blurType = 0;
                    if (this.cacheImage.filter != null) {
                        if (!this.cacheImage.filter.contains("b2")) {
                            blurType = 3;
                        } else if (!this.cacheImage.filter.contains("b1")) {
                            blurType = 2;
                        } else if (this.cacheImage.filter.contains("b")) {
                            blurType = 1;
                        }
                    }
                    ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                    synchronized (this.sync) {
                        if (this.isCancelled) {
                            return;
                        }
                        opts = new Options();
                        opts.inSampleSize = 1;
                        if (VERSION.SDK_INT < 21) {
                            opts.inPurgeable = true;
                        }
                        if (!useNativeWebpLoaded) {
                            randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                            buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                            bmOptions = new Options();
                            bmOptions.inJustDecodeBounds = true;
                            Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                            decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                            if (opts.inPurgeable) {
                            }
                            Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                            randomAccessFile.close();
                        } else if (opts.inPurgeable) {
                            randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                            len = (int) randomAccessFile.length();
                            data = (ImageLoader.bytesThumb != null || ImageLoader.bytesThumb.length < len) ? null : ImageLoader.bytesThumb;
                            if (data == null) {
                                data = new byte[len];
                                ImageLoader.bytesThumb = data;
                            }
                            randomAccessFile.readFully(data, 0, len);
                            decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                        } else {
                            fileInputStream = new FileInputStream(cacheFileFinal);
                            decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                            fileInputStream.close();
                        }
                        if (decodeStream != null) {
                            if (cacheFileFinal.length() == 0 || this.cacheImage.filter == null) {
                                cacheFileFinal.delete();
                            }
                        } else if (blurType == 1) {
                            if (decodeStream.getConfig() == Config.ARGB_8888) {
                                if (opts.inPurgeable) {
                                }
                                Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                            }
                        } else if (blurType == 2) {
                            if (decodeStream.getConfig() == Config.ARGB_8888) {
                                if (opts.inPurgeable) {
                                }
                                Utilities.blurBitmap(decodeStream, 1, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                            }
                        } else if (blurType != 3) {
                            if (decodeStream.getConfig() == Config.ARGB_8888) {
                                if (opts.inPurgeable) {
                                }
                                Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                if (opts.inPurgeable) {
                                }
                                Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                if (opts.inPurgeable) {
                                }
                                Utilities.blurBitmap(decodeStream, 7, opts.inPurgeable ? 1 : 0, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                            }
                        } else if (blurType == 0 && opts.inPurgeable) {
                            Utilities.pinBitmap(decodeStream);
                        }
                    }
                } else {
                    if (this.cacheImage.httpUrl != null) {
                        if (!this.cacheImage.httpUrl.startsWith("thumb://")) {
                            idx = this.cacheImage.httpUrl.indexOf(":", 8);
                            if (idx >= 0) {
                                mediaId = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(8, idx)));
                                mediaIsVideo = false;
                            }
                            canDeleteFile = false;
                        } else if (!this.cacheImage.httpUrl.startsWith("vthumb://")) {
                            idx = this.cacheImage.httpUrl.indexOf(":", 9);
                            if (idx >= 0) {
                                mediaId = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(9, idx)));
                                mediaIsVideo = true;
                            }
                            canDeleteFile = false;
                        } else if (this.cacheImage.httpUrl.startsWith("http")) {
                            canDeleteFile = false;
                        }
                    }
                    delay = 20;
                    if (mediaId != null) {
                        delay = 0;
                    }
                    if (delay != 0 && ImageLoader.this.lastCacheOutTime != 0 && ImageLoader.this.lastCacheOutTime > System.currentTimeMillis() - ((long) delay) && VERSION.SDK_INT < 21) {
                        Thread.sleep((long) delay);
                    }
                    ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                    synchronized (this.sync) {
                        if (this.isCancelled) {
                            return;
                        }
                        opts = new Options();
                        opts.inSampleSize = 1;
                        f = 0.0f;
                        h_filter = 0.0f;
                        z = false;
                        if (this.cacheImage.filter != null) {
                            args = this.cacheImage.filter.split("_");
                            if (args.length >= 2) {
                                f = Float.parseFloat(args[0]) * AndroidUtilities.density;
                                h_filter = Float.parseFloat(args[1]) * AndroidUtilities.density;
                            }
                            if (this.cacheImage.filter.contains("b")) {
                                z = true;
                            }
                            if (!(f == 0.0f || h_filter == 0.0f)) {
                                opts.inJustDecodeBounds = true;
                                if (mediaId != null) {
                                    fileInputStream = new FileInputStream(cacheFileFinal);
                                    decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                    fileInputStream.close();
                                } else if (mediaIsVideo) {
                                    Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                    decodeStream = null;
                                } else {
                                    Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                    decodeStream = null;
                                }
                                scaleFactor = Math.max(((float) opts.outWidth) / f, ((float) opts.outHeight) / h_filter);
                                if (scaleFactor < TouchHelperCallback.ALPHA_FULL) {
                                    scaleFactor = TouchHelperCallback.ALPHA_FULL;
                                }
                                opts.inJustDecodeBounds = false;
                                opts.inSampleSize = (int) scaleFactor;
                                synchronized (this.sync) {
                                    if (this.isCancelled) {
                                        return;
                                    }
                                    if (this.cacheImage.filter == null && !blur && this.cacheImage.httpUrl == null) {
                                        opts.inPreferredConfig = Config.RGB_565;
                                    } else {
                                        opts.inPreferredConfig = Config.ARGB_8888;
                                    }
                                    if (VERSION.SDK_INT < 21) {
                                        opts.inPurgeable = true;
                                    }
                                    opts.inDither = false;
                                    if (mediaId != null) {
                                        if (mediaIsVideo) {
                                            decodeStream = Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                        } else {
                                            decodeStream = Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                        }
                                    }
                                    if (decodeStream == null) {
                                        if (useNativeWebpLoaded) {
                                            randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                            buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                            bmOptions = new Options();
                                            bmOptions.inJustDecodeBounds = true;
                                            Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                            decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                            if (opts.inPurgeable) {
                                            }
                                            Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                            randomAccessFile.close();
                                        } else if (opts.inPurgeable) {
                                            randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                            len = (int) randomAccessFile.length();
                                            data = (ImageLoader.bytes != null || ImageLoader.bytes.length < len) ? null : ImageLoader.bytes;
                                            if (data == null) {
                                                data = new byte[len];
                                                ImageLoader.bytes = data;
                                            }
                                            randomAccessFile.readFully(data, 0, len);
                                            decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                        } else {
                                            fileInputStream = new FileInputStream(cacheFileFinal);
                                            decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                            fileInputStream.close();
                                        }
                                    }
                                    if (decodeStream == null) {
                                        blured = false;
                                        if (this.cacheImage.filter != null) {
                                            bitmapW = (float) decodeStream.getWidth();
                                            bitmapH = (float) decodeStream.getHeight();
                                            if (!(opts.inPurgeable || f == 0.0f || bitmapW == f || bitmapW <= 20.0f + f)) {
                                                scaledBitmap = Bitmaps.createScaledBitmap(decodeStream, (int) f, (int) (bitmapH / (bitmapW / f)), true);
                                                if (decodeStream != scaledBitmap) {
                                                    decodeStream.recycle();
                                                    decodeStream = scaledBitmap;
                                                }
                                            }
                                            if (decodeStream != null && blur && bitmapH < 100.0f && bitmapW < 100.0f) {
                                                if (decodeStream.getConfig() == Config.ARGB_8888) {
                                                    if (opts.inPurgeable) {
                                                    }
                                                    Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 0 : 1, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                                }
                                                blured = true;
                                            }
                                        }
                                        if (!blured && opts.inPurgeable) {
                                            Utilities.pinBitmap(decodeStream);
                                        }
                                    } else if (canDeleteFile && (cacheFileFinal.length() == 0 || this.cacheImage.filter == null)) {
                                        cacheFileFinal.delete();
                                    }
                                }
                            }
                        }
                        decodeStream = null;
                        synchronized (this.sync) {
                            if (this.isCancelled) {
                                if (this.cacheImage.filter == null) {
                                }
                                opts.inPreferredConfig = Config.ARGB_8888;
                                if (VERSION.SDK_INT < 21) {
                                    opts.inPurgeable = true;
                                }
                                opts.inDither = false;
                                if (mediaId != null) {
                                    if (mediaIsVideo) {
                                        decodeStream = Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                    } else {
                                        decodeStream = Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), mediaId.longValue(), 1, opts);
                                    }
                                }
                                if (decodeStream == null) {
                                    if (useNativeWebpLoaded) {
                                        randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                        buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, cacheFileFinal.length());
                                        bmOptions = new Options();
                                        bmOptions.inJustDecodeBounds = true;
                                        Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                        decodeStream = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Config.ARGB_8888);
                                        if (opts.inPurgeable) {
                                        }
                                        Utilities.loadWebpImage(decodeStream, buffer, buffer.limit(), null, opts.inPurgeable);
                                        randomAccessFile.close();
                                    } else if (opts.inPurgeable) {
                                        fileInputStream = new FileInputStream(cacheFileFinal);
                                        decodeStream = BitmapFactory.decodeStream(fileInputStream, null, opts);
                                        fileInputStream.close();
                                    } else {
                                        randomAccessFile = new RandomAccessFile(cacheFileFinal, "r");
                                        len = (int) randomAccessFile.length();
                                        if (ImageLoader.bytes != null) {
                                        }
                                        if (data == null) {
                                            data = new byte[len];
                                            ImageLoader.bytes = data;
                                        }
                                        randomAccessFile.readFully(data, 0, len);
                                        decodeStream = BitmapFactory.decodeByteArray(data, 0, len, opts);
                                    }
                                }
                                if (decodeStream == null) {
                                    blured = false;
                                    if (this.cacheImage.filter != null) {
                                        bitmapW = (float) decodeStream.getWidth();
                                        bitmapH = (float) decodeStream.getHeight();
                                        scaledBitmap = Bitmaps.createScaledBitmap(decodeStream, (int) f, (int) (bitmapH / (bitmapW / f)), true);
                                        if (decodeStream != scaledBitmap) {
                                            decodeStream.recycle();
                                            decodeStream = scaledBitmap;
                                        }
                                        if (decodeStream.getConfig() == Config.ARGB_8888) {
                                            if (opts.inPurgeable) {
                                            }
                                            Utilities.blurBitmap(decodeStream, 3, opts.inPurgeable ? 0 : 1, decodeStream.getWidth(), decodeStream.getHeight(), decodeStream.getRowBytes());
                                        }
                                        blured = true;
                                    }
                                    Utilities.pinBitmap(decodeStream);
                                } else {
                                    cacheFileFinal.delete();
                                }
                            } else {
                                return;
                            }
                        }
                    }
                }
                Thread.interrupted();
                if (decodeStream == null) {
                    bitmapDrawable = new BitmapDrawable(decodeStream);
                } else {
                    bitmapDrawable = null;
                }
                onPostExecute(bitmapDrawable);
            }
        }

        private void onPostExecute(BitmapDrawable bitmapDrawable) {
            AndroidUtilities.runOnUIThread(new C05151(bitmapDrawable));
        }

        public void cancel() {
            synchronized (this.sync) {
                try {
                    this.isCancelled = true;
                    if (this.runningThread != null) {
                        this.runningThread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private class HttpFileTask extends AsyncTask<Void, Void, Boolean> {
        private boolean canRetry;
        private String ext;
        private RandomAccessFile fileOutputStream;
        private File tempFile;
        private String url;

        public HttpFileTask(String url, File tempFile, String ext) {
            this.fileOutputStream = null;
            this.canRetry = true;
            this.url = url;
            this.tempFile = tempFile;
            this.ext = ext;
        }

        protected Boolean doInBackground(Void... voids) {
            InputStream httpConnectionStream = null;
            boolean done = false;
            URLConnection httpConnection = null;
            try {
                httpConnection = new URL(this.url).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                httpConnection.addRequestProperty("Referer", "google.com");
                httpConnection.setConnectTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                httpConnection.setReadTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                if (httpConnection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                    httpURLConnection.setInstanceFollowRedirects(true);
                    int status = httpURLConnection.getResponseCode();
                    if (status == 302 || status == 301 || status == 303) {
                        String newUrl = httpURLConnection.getHeaderField("Location");
                        String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                        httpConnection = new URL(newUrl).openConnection();
                        httpConnection.setRequestProperty("Cookie", cookies);
                        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                        httpConnection.addRequestProperty("Referer", "google.com");
                    }
                }
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                this.fileOutputStream = new RandomAccessFile(this.tempFile, "rws");
            } catch (Throwable e) {
                if (e instanceof UnknownHostException) {
                    this.canRetry = false;
                }
                FileLog.m13e("tmessages", e);
            }
            if (this.canRetry) {
                if (httpConnection != null) {
                    try {
                        if (httpConnection instanceof HttpURLConnection) {
                            int code = ((HttpURLConnection) httpConnection).getResponseCode();
                            if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202 || code == 304)) {
                                this.canRetry = false;
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[MessagesController.UPDATE_MASK_SEND_STATE];
                        while (!isCancelled()) {
                            int read = httpConnectionStream.read(data);
                            if (read > 0) {
                                this.fileOutputStream.write(data, 0, read);
                            } else if (read == -1) {
                                done = true;
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m13e("tmessages", e22);
                    } catch (Throwable e222) {
                        FileLog.m13e("tmessages", e222);
                    }
                }
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e2222) {
                    FileLog.m13e("tmessages", e2222);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e22222) {
                        FileLog.m13e("tmessages", e22222);
                    }
                }
            }
            return Boolean.valueOf(done);
        }

        protected void onPostExecute(Boolean result) {
            ImageLoader.this.runHttpFileLoadTasks(this, result.booleanValue() ? 2 : 1);
        }

        protected void onCancelled() {
            ImageLoader.this.runHttpFileLoadTasks(this, 2);
        }
    }

    private class HttpImageTask extends AsyncTask<Void, Void, Boolean> {
        private CacheImage cacheImage;
        private boolean canRetry;
        private RandomAccessFile fileOutputStream;
        private URLConnection httpConnection;
        private int imageSize;
        private long lastProgressTime;

        /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.1 */
        class C05171 implements Runnable {
            final /* synthetic */ float val$progress;

            /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.1.1 */
            class C05161 implements Runnable {
                C05161() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpImageTask.this.cacheImage.url, Float.valueOf(C05171.this.val$progress));
                }
            }

            C05171(float f) {
                this.val$progress = f;
            }

            public void run() {
                ImageLoader.this.fileProgresses.put(HttpImageTask.this.cacheImage.url, Float.valueOf(this.val$progress));
                AndroidUtilities.runOnUIThread(new C05161());
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.2 */
        class C05192 implements Runnable {
            final /* synthetic */ Boolean val$result;

            /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.2.1 */
            class C05181 implements Runnable {
                C05181() {
                }

                public void run() {
                    if (C05192.this.val$result.booleanValue()) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, HttpImageTask.this.cacheImage.url);
                        return;
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(2));
                }
            }

            C05192(Boolean bool) {
                this.val$result = bool;
            }

            public void run() {
                ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                AndroidUtilities.runOnUIThread(new C05181());
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.3 */
        class C05203 implements Runnable {
            C05203() {
            }

            public void run() {
                ImageLoader.this.runHttpTasks(true);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.4 */
        class C05214 implements Runnable {
            C05214() {
            }

            public void run() {
                ImageLoader.this.runHttpTasks(true);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.5 */
        class C05235 implements Runnable {

            /* renamed from: org.telegram.messenger.ImageLoader.HttpImageTask.5.1 */
            class C05221 implements Runnable {
                C05221() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(1));
                }
            }

            C05235() {
            }

            public void run() {
                ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                AndroidUtilities.runOnUIThread(new C05221());
            }
        }

        public HttpImageTask(CacheImage cacheImage, int size) {
            this.cacheImage = null;
            this.fileOutputStream = null;
            this.canRetry = true;
            this.httpConnection = null;
            this.cacheImage = cacheImage;
            this.imageSize = size;
        }

        private void reportProgress(float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress == TouchHelperCallback.ALPHA_FULL || this.lastProgressTime == 0 || this.lastProgressTime < currentTime - 500) {
                this.lastProgressTime = currentTime;
                Utilities.stageQueue.postRunnable(new C05171(progress));
            }
        }

        protected Boolean doInBackground(Void... voids) {
            InputStream httpConnectionStream = null;
            boolean done = false;
            if (!isCancelled()) {
                try {
                    this.httpConnection = new URL(this.cacheImage.httpUrl).openConnection();
                    this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                    this.httpConnection.addRequestProperty("Referer", "google.com");
                    this.httpConnection.setConnectTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                    this.httpConnection.setReadTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                    if (this.httpConnection instanceof HttpURLConnection) {
                        ((HttpURLConnection) this.httpConnection).setInstanceFollowRedirects(true);
                    }
                    if (!isCancelled()) {
                        this.httpConnection.connect();
                        httpConnectionStream = this.httpConnection.getInputStream();
                        this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
                    }
                } catch (Throwable e) {
                    if (e instanceof SocketTimeoutException) {
                        if (ConnectionsManager.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (e instanceof UnknownHostException) {
                        this.canRetry = false;
                    } else if ((e instanceof SocketException) && e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                        this.canRetry = false;
                    }
                    FileLog.m13e("tmessages", e);
                }
            }
            if (!isCancelled()) {
                try {
                    if (this.httpConnection != null && (this.httpConnection instanceof HttpURLConnection)) {
                        int code = ((HttpURLConnection) this.httpConnection).getResponseCode();
                        if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202 || code == 304)) {
                            this.canRetry = false;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                if (this.imageSize == 0 && this.httpConnection != null) {
                    try {
                        Map<String, List<String>> headerFields = this.httpConnection.getHeaderFields();
                        if (headerFields != null) {
                            List values = (List) headerFields.get("content-Length");
                            if (!(values == null || values.isEmpty())) {
                                String length = (String) values.get(0);
                                if (length != null) {
                                    this.imageSize = Utilities.parseInt(length).intValue();
                                }
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m13e("tmessages", e22);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[MessagesController.UPDATE_MASK_CHANNEL];
                        int totalLoaded = 0;
                        while (!isCancelled()) {
                            int read = httpConnectionStream.read(data);
                            if (read > 0) {
                                totalLoaded += read;
                                this.fileOutputStream.write(data, 0, read);
                                if (this.imageSize != 0) {
                                    reportProgress(((float) totalLoaded) / ((float) this.imageSize));
                                }
                            } else if (read == -1) {
                                done = true;
                                if (this.imageSize != 0) {
                                    reportProgress(TouchHelperCallback.ALPHA_FULL);
                                }
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.m13e("tmessages", e222);
                    } catch (Throwable e2222) {
                        FileLog.m13e("tmessages", e2222);
                    }
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e22222) {
                FileLog.m13e("tmessages", e22222);
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e222222) {
                    FileLog.m13e("tmessages", e222222);
                }
            }
            if (!(!done || this.cacheImage.tempFilePath == null || this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath))) {
                this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
            }
            return Boolean.valueOf(done);
        }

        protected void onPostExecute(Boolean result) {
            if (result.booleanValue() || !this.canRetry) {
                ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new C05192(result));
            ImageLoader.this.imageLoadQueue.postRunnable(new C05203());
        }

        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new C05214());
            Utilities.stageQueue.postRunnable(new C05235());
        }
    }

    private class ThumbGenerateInfo {
        private int count;
        private FileLocation fileLocation;
        private String filter;

        private ThumbGenerateInfo() {
        }
    }

    private class ThumbGenerateTask implements Runnable {
        private String filter;
        private int mediaType;
        private File originalPath;
        private FileLocation thumbLocation;

        /* renamed from: org.telegram.messenger.ImageLoader.ThumbGenerateTask.1 */
        class C05241 implements Runnable {
            final /* synthetic */ String val$name;

            C05241(String str) {
                this.val$name = str;
            }

            public void run() {
                ImageLoader.this.thumbGenerateTasks.remove(this.val$name);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.ThumbGenerateTask.2 */
        class C05252 implements Runnable {
            final /* synthetic */ BitmapDrawable val$bitmapDrawable;
            final /* synthetic */ String val$key;

            C05252(String str, BitmapDrawable bitmapDrawable) {
                this.val$key = str;
                this.val$bitmapDrawable = bitmapDrawable;
            }

            public void run() {
                ThumbGenerateTask.this.removeTask();
                String kf = this.val$key;
                if (ThumbGenerateTask.this.filter != null) {
                    kf = kf + "@" + ThumbGenerateTask.this.filter;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageThumbGenerated, this.val$bitmapDrawable, kf);
                ImageLoader.this.memCache.put(kf, this.val$bitmapDrawable);
            }
        }

        public ThumbGenerateTask(int type, File path, FileLocation location, String f) {
            this.mediaType = type;
            this.originalPath = path;
            this.thumbLocation = location;
            this.filter = f;
        }

        private void removeTask() {
            if (this.thumbLocation != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new C05241(FileLoader.getAttachFileName(this.thumbLocation)));
            }
        }

        public void run() {
            try {
                if (this.thumbLocation == null) {
                    removeTask();
                    return;
                }
                String key = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
                File thumbFile = new File(FileLoader.getInstance().getDirectory(4), "q_" + key + ".jpg");
                if (thumbFile.exists() || !this.originalPath.exists()) {
                    removeTask();
                    return;
                }
                int size = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                Bitmap originalBitmap = null;
                if (this.mediaType == 0) {
                    originalBitmap = ImageLoader.loadBitmap(this.originalPath.toString(), null, (float) size, (float) size, false);
                } else if (this.mediaType == 2) {
                    originalBitmap = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
                } else if (this.mediaType == 3) {
                    String path = this.originalPath.toString().toLowerCase();
                    if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                        originalBitmap = ImageLoader.loadBitmap(path, null, (float) size, (float) size, false);
                    } else {
                        removeTask();
                        return;
                    }
                }
                if (originalBitmap == null) {
                    removeTask();
                    return;
                }
                int w = originalBitmap.getWidth();
                int h = originalBitmap.getHeight();
                if (w == 0 || h == 0) {
                    removeTask();
                    return;
                }
                float scaleFactor = Math.min(((float) w) / ((float) size), ((float) h) / ((float) size));
                Bitmap scaledBitmap = Bitmaps.createScaledBitmap(originalBitmap, (int) (((float) w) / scaleFactor), (int) (((float) h) / scaleFactor), true);
                if (scaledBitmap != originalBitmap) {
                    originalBitmap.recycle();
                }
                originalBitmap = scaledBitmap;
                FileOutputStream stream = new FileOutputStream(thumbFile);
                originalBitmap.compress(CompressFormat.JPEG, 60, stream);
                stream.close();
                AndroidUtilities.runOnUIThread(new C05252(key, new BitmapDrawable(originalBitmap)));
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
                removeTask();
            }
        }
    }

    public class VMRuntimeHack {
        private Object runtime;
        private Method trackAllocation;
        private Method trackFree;

        public boolean trackAlloc(long size) {
            if (this.runtime == null) {
                return false;
            }
            try {
                Object res = this.trackAllocation.invoke(this.runtime, new Object[]{Long.valueOf(size)});
                if (res instanceof Boolean) {
                    return ((Boolean) res).booleanValue();
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public boolean trackFree(long size) {
            if (this.runtime == null) {
                return false;
            }
            try {
                Object res = this.trackFree.invoke(this.runtime, new Object[]{Long.valueOf(size)});
                if (res instanceof Boolean) {
                    return ((Boolean) res).booleanValue();
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public VMRuntimeHack() {
            this.runtime = null;
            this.trackAllocation = null;
            this.trackFree = null;
            try {
                Class cl = Class.forName("dalvik.system.VMRuntime");
                this.runtime = cl.getMethod("getRuntime", new Class[0]).invoke(null, new Object[0]);
                this.trackAllocation = cl.getMethod("trackExternalAllocation", new Class[]{Long.TYPE});
                this.trackFree = cl.getMethod("trackExternalFree", new Class[]{Long.TYPE});
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                this.runtime = null;
                this.trackAllocation = null;
                this.trackFree = null;
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.1 */
    class C16821 extends LruCache {
        C16821(int x0) {
            super(x0);
        }

        protected int sizeOf(String key, BitmapDrawable value) {
            return value.getBitmap().getByteCount();
        }

        protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
            if (ImageLoader.this.ignoreRemoval == null || key == null || !ImageLoader.this.ignoreRemoval.equals(key)) {
                Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
                if (count == null || count.intValue() == 0) {
                    Bitmap b = oldValue.getBitmap();
                    if (!b.isRecycled()) {
                        b.recycle();
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader.2 */
    class C16832 implements FileLoaderDelegate {

        /* renamed from: org.telegram.messenger.ImageLoader.2.1 */
        class C04961 implements Runnable {
            final /* synthetic */ boolean val$isEncrypted;
            final /* synthetic */ String val$location;
            final /* synthetic */ float val$progress;

            C04961(String str, float f, boolean z) {
                this.val$location = str;
                this.val$progress = f;
                this.val$isEncrypted = z;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileUploadProgressChanged, this.val$location, Float.valueOf(this.val$progress), Boolean.valueOf(this.val$isEncrypted));
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.2.2 */
        class C04982 implements Runnable {
            final /* synthetic */ InputEncryptedFile val$inputEncryptedFile;
            final /* synthetic */ InputFile val$inputFile;
            final /* synthetic */ byte[] val$iv;
            final /* synthetic */ byte[] val$key;
            final /* synthetic */ String val$location;
            final /* synthetic */ long val$totalFileSize;

            /* renamed from: org.telegram.messenger.ImageLoader.2.2.1 */
            class C04971 implements Runnable {
                C04971() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidUpload, C04982.this.val$location, C04982.this.val$inputFile, C04982.this.val$inputEncryptedFile, C04982.this.val$key, C04982.this.val$iv, Long.valueOf(C04982.this.val$totalFileSize));
                }
            }

            C04982(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                this.val$location = str;
                this.val$inputFile = inputFile;
                this.val$inputEncryptedFile = inputEncryptedFile;
                this.val$key = bArr;
                this.val$iv = bArr2;
                this.val$totalFileSize = j;
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C04971());
                ImageLoader.this.fileProgresses.remove(this.val$location);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.2.3 */
        class C05003 implements Runnable {
            final /* synthetic */ boolean val$isEncrypted;
            final /* synthetic */ String val$location;

            /* renamed from: org.telegram.messenger.ImageLoader.2.3.1 */
            class C04991 implements Runnable {
                C04991() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailUpload, C05003.this.val$location, Boolean.valueOf(C05003.this.val$isEncrypted));
                }
            }

            C05003(String str, boolean z) {
                this.val$location = str;
                this.val$isEncrypted = z;
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C04991());
                ImageLoader.this.fileProgresses.remove(this.val$location);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.2.4 */
        class C05014 implements Runnable {
            final /* synthetic */ File val$finalFile;
            final /* synthetic */ String val$location;
            final /* synthetic */ int val$type;

            C05014(File file, String str, int i) {
                this.val$finalFile = file;
                this.val$location = str;
                this.val$type = i;
            }

            public void run() {
                if (MediaController.getInstance().canSaveToGallery() && ImageLoader.this.telegramPath != null && this.val$finalFile != null && ((this.val$location.endsWith(".mp4") || this.val$location.endsWith(".jpg")) && this.val$finalFile.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                    AndroidUtilities.addMediaToGallery(this.val$finalFile.toString());
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, this.val$location);
                ImageLoader.this.fileDidLoaded(this.val$location, this.val$finalFile, this.val$type);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.2.5 */
        class C05025 implements Runnable {
            final /* synthetic */ int val$canceled;
            final /* synthetic */ String val$location;

            C05025(String str, int i) {
                this.val$location = str;
                this.val$canceled = i;
            }

            public void run() {
                ImageLoader.this.fileDidFailedLoad(this.val$location, this.val$canceled);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, this.val$location, Integer.valueOf(this.val$canceled));
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader.2.6 */
        class C05036 implements Runnable {
            final /* synthetic */ String val$location;
            final /* synthetic */ float val$progress;

            C05036(String str, float f) {
                this.val$location = str;
                this.val$progress = f;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, this.val$location, Float.valueOf(this.val$progress));
            }
        }

        C16832() {
        }

        public void fileUploadProgressChanged(String location, float progress, boolean isEncrypted) {
            ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
            long currentTime = System.currentTimeMillis();
            if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                ImageLoader.this.lastProgressUpdateTime = currentTime;
                AndroidUtilities.runOnUIThread(new C04961(location, progress, isEncrypted));
            }
        }

        public void fileDidUploaded(String location, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
            Utilities.stageQueue.postRunnable(new C04982(location, inputFile, inputEncryptedFile, key, iv, totalFileSize));
        }

        public void fileDidFailedUpload(String location, boolean isEncrypted) {
            Utilities.stageQueue.postRunnable(new C05003(location, isEncrypted));
        }

        public void fileDidLoaded(String location, File finalFile, int type) {
            ImageLoader.this.fileProgresses.remove(location);
            AndroidUtilities.runOnUIThread(new C05014(finalFile, location, type));
        }

        public void fileDidFailedLoad(String location, int canceled) {
            ImageLoader.this.fileProgresses.remove(location);
            AndroidUtilities.runOnUIThread(new C05025(location, canceled));
        }

        public void fileLoadProgressChanged(String location, float progress) {
            ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
            long currentTime = System.currentTimeMillis();
            if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                ImageLoader.this.lastProgressUpdateTime = currentTime;
                AndroidUtilities.runOnUIThread(new C05036(location, progress));
            }
        }
    }

    static {
        header = new byte[12];
        headerThumb = new byte[12];
        Instance = null;
    }

    public static ImageLoader getInstance() {
        ImageLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (ImageLoader.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ImageLoader localInstance2 = new ImageLoader();
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

    public ImageLoader() {
        this.bitmapUseCounts = new HashMap();
        this.imageLoadingByUrl = new HashMap();
        this.imageLoadingByKeys = new HashMap();
        this.imageLoadingByTag = new HashMap();
        this.waitingForQualityThumb = new HashMap();
        this.waitingForQualityThumbByTag = new HashMap();
        this.httpTasks = new LinkedList();
        this.cacheOutQueue = new DispatchQueue("cacheOutQueue");
        this.cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
        this.thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
        this.imageLoadQueue = new DispatchQueue("imageLoadQueue");
        this.fileProgresses = new ConcurrentHashMap();
        this.thumbGenerateTasks = new HashMap();
        this.currentHttpTasksCount = 0;
        this.httpFileLoadTasks = new LinkedList();
        this.httpFileLoadTasksByKeys = new HashMap();
        this.retryHttpsTasks = new HashMap();
        this.currentHttpFileLoadTasksCount = 0;
        this.ignoreRemoval = null;
        this.lastCacheOutTime = 0;
        this.lastImageNum = 0;
        this.lastProgressUpdateTime = 0;
        this.telegramPath = null;
        this.cacheOutQueue.setPriority(1);
        this.cacheThumbOutQueue.setPriority(1);
        this.thumbGeneratingQueue.setPriority(1);
        this.imageLoadQueue.setPriority(1);
        this.memCache = new C16821((Math.min(15, ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass() / 7) * MessagesController.UPDATE_MASK_PHONE) * MessagesController.UPDATE_MASK_PHONE);
        FileLoader.getInstance().setDelegate(new C16832());
        BroadcastReceiver receiver = new C05053();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        filter.addAction("android.intent.action.MEDIA_CHECKING");
        filter.addAction("android.intent.action.MEDIA_EJECT");
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_NOFS");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        filter.addAction("android.intent.action.MEDIA_SHARED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addDataScheme("file");
        ApplicationLoader.applicationContext.registerReceiver(receiver, filter);
        HashMap<Integer, File> mediaDirs = new HashMap();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        mediaDirs.put(Integer.valueOf(4), cachePath);
        FileLoader.getInstance().setMediaDirs(mediaDirs);
        checkMediaPaths();
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new C05074());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.HashMap<java.lang.Integer, java.io.File> createMediaPaths() {
        /*
        r11 = this;
        r5 = new java.util.HashMap;
        r5.<init>();
        r1 = org.telegram.messenger.AndroidUtilities.getCacheDir();
        r7 = r1.isDirectory();
        if (r7 != 0) goto L_0x0012;
    L_0x000f:
        r1.mkdirs();	 Catch:{ Exception -> 0x01d1 }
    L_0x0012:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x01d9 }
        r8 = ".nomedia";
        r7.<init>(r1, r8);	 Catch:{ Exception -> 0x01d9 }
        r7.createNewFile();	 Catch:{ Exception -> 0x01d9 }
    L_0x001c:
        r7 = 4;
        r7 = java.lang.Integer.valueOf(r7);
        r5.put(r7, r1);
        r7 = "tmessages";
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "cache path = ";
        r8 = r8.append(r9);
        r8 = r8.append(r1);
        r8 = r8.toString();
        org.telegram.messenger.FileLog.m11e(r7, r8);
        r7 = "mounted";
        r8 = android.os.Environment.getExternalStorageState();	 Catch:{ Exception -> 0x01e9 }
        r7 = r7.equals(r8);	 Catch:{ Exception -> 0x01e9 }
        if (r7 == 0) goto L_0x0207;
    L_0x0048:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x01e9 }
        r8 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x01e9 }
        r9 = "AppName";
        r10 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Exception -> 0x01e9 }
        r7.<init>(r8, r9);	 Catch:{ Exception -> 0x01e9 }
        r11.telegramPath = r7;	 Catch:{ Exception -> 0x01e9 }
        r7 = r11.telegramPath;	 Catch:{ Exception -> 0x01e9 }
        r7.mkdirs();	 Catch:{ Exception -> 0x01e9 }
        r7 = r11.telegramPath;	 Catch:{ Exception -> 0x01e9 }
        r7 = r7.isDirectory();	 Catch:{ Exception -> 0x01e9 }
        if (r7 == 0) goto L_0x01c9;
    L_0x0069:
        r4 = new java.io.File;	 Catch:{ Exception -> 0x01e1 }
        r7 = r11.telegramPath;	 Catch:{ Exception -> 0x01e1 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1 }
        r8.<init>();	 Catch:{ Exception -> 0x01e1 }
        r9 = "AppName";
        r10 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Exception -> 0x01e1 }
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01e1 }
        r9 = " Images";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01e1 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x01e1 }
        r4.<init>(r7, r8);	 Catch:{ Exception -> 0x01e1 }
        r4.mkdir();	 Catch:{ Exception -> 0x01e1 }
        r7 = r4.isDirectory();	 Catch:{ Exception -> 0x01e1 }
        if (r7 == 0) goto L_0x00bc;
    L_0x0095:
        r7 = 0;
        r7 = r11.canMoveFiles(r1, r4, r7);	 Catch:{ Exception -> 0x01e1 }
        if (r7 == 0) goto L_0x00bc;
    L_0x009c:
        r7 = 0;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x01e1 }
        r5.put(r7, r4);	 Catch:{ Exception -> 0x01e1 }
        r7 = "tmessages";
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e1 }
        r8.<init>();	 Catch:{ Exception -> 0x01e1 }
        r9 = "image path = ";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01e1 }
        r8 = r8.append(r4);	 Catch:{ Exception -> 0x01e1 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x01e1 }
        org.telegram.messenger.FileLog.m11e(r7, r8);	 Catch:{ Exception -> 0x01e1 }
    L_0x00bc:
        r6 = new java.io.File;	 Catch:{ Exception -> 0x01f0 }
        r7 = r11.telegramPath;	 Catch:{ Exception -> 0x01f0 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01f0 }
        r8.<init>();	 Catch:{ Exception -> 0x01f0 }
        r9 = "AppName";
        r10 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Exception -> 0x01f0 }
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01f0 }
        r9 = " Video";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01f0 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x01f0 }
        r6.<init>(r7, r8);	 Catch:{ Exception -> 0x01f0 }
        r6.mkdir();	 Catch:{ Exception -> 0x01f0 }
        r7 = r6.isDirectory();	 Catch:{ Exception -> 0x01f0 }
        if (r7 == 0) goto L_0x010f;
    L_0x00e8:
        r7 = 2;
        r7 = r11.canMoveFiles(r1, r6, r7);	 Catch:{ Exception -> 0x01f0 }
        if (r7 == 0) goto L_0x010f;
    L_0x00ef:
        r7 = 2;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x01f0 }
        r5.put(r7, r6);	 Catch:{ Exception -> 0x01f0 }
        r7 = "tmessages";
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01f0 }
        r8.<init>();	 Catch:{ Exception -> 0x01f0 }
        r9 = "video path = ";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01f0 }
        r8 = r8.append(r6);	 Catch:{ Exception -> 0x01f0 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x01f0 }
        org.telegram.messenger.FileLog.m11e(r7, r8);	 Catch:{ Exception -> 0x01f0 }
    L_0x010f:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x01f8 }
        r7 = r11.telegramPath;	 Catch:{ Exception -> 0x01f8 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01f8 }
        r8.<init>();	 Catch:{ Exception -> 0x01f8 }
        r9 = "AppName";
        r10 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Exception -> 0x01f8 }
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01f8 }
        r9 = " Audio";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01f8 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x01f8 }
        r0.<init>(r7, r8);	 Catch:{ Exception -> 0x01f8 }
        r0.mkdir();	 Catch:{ Exception -> 0x01f8 }
        r7 = r0.isDirectory();	 Catch:{ Exception -> 0x01f8 }
        if (r7 == 0) goto L_0x016c;
    L_0x013b:
        r7 = 1;
        r7 = r11.canMoveFiles(r1, r0, r7);	 Catch:{ Exception -> 0x01f8 }
        if (r7 == 0) goto L_0x016c;
    L_0x0142:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x01f8 }
        r8 = ".nomedia";
        r7.<init>(r0, r8);	 Catch:{ Exception -> 0x01f8 }
        r7.createNewFile();	 Catch:{ Exception -> 0x01f8 }
        r7 = 1;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x01f8 }
        r5.put(r7, r0);	 Catch:{ Exception -> 0x01f8 }
        r7 = "tmessages";
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01f8 }
        r8.<init>();	 Catch:{ Exception -> 0x01f8 }
        r9 = "audio path = ";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x01f8 }
        r8 = r8.append(r0);	 Catch:{ Exception -> 0x01f8 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x01f8 }
        org.telegram.messenger.FileLog.m11e(r7, r8);	 Catch:{ Exception -> 0x01f8 }
    L_0x016c:
        r2 = new java.io.File;	 Catch:{ Exception -> 0x0200 }
        r7 = r11.telegramPath;	 Catch:{ Exception -> 0x0200 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0200 }
        r8.<init>();	 Catch:{ Exception -> 0x0200 }
        r9 = "AppName";
        r10 = 2131165300; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Exception -> 0x0200 }
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x0200 }
        r9 = " Documents";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x0200 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0200 }
        r2.<init>(r7, r8);	 Catch:{ Exception -> 0x0200 }
        r2.mkdir();	 Catch:{ Exception -> 0x0200 }
        r7 = r2.isDirectory();	 Catch:{ Exception -> 0x0200 }
        if (r7 == 0) goto L_0x01c9;
    L_0x0198:
        r7 = 3;
        r7 = r11.canMoveFiles(r1, r2, r7);	 Catch:{ Exception -> 0x0200 }
        if (r7 == 0) goto L_0x01c9;
    L_0x019f:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0200 }
        r8 = ".nomedia";
        r7.<init>(r2, r8);	 Catch:{ Exception -> 0x0200 }
        r7.createNewFile();	 Catch:{ Exception -> 0x0200 }
        r7 = 3;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0200 }
        r5.put(r7, r2);	 Catch:{ Exception -> 0x0200 }
        r7 = "tmessages";
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0200 }
        r8.<init>();	 Catch:{ Exception -> 0x0200 }
        r9 = "documents path = ";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x0200 }
        r8 = r8.append(r2);	 Catch:{ Exception -> 0x0200 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0200 }
        org.telegram.messenger.FileLog.m11e(r7, r8);	 Catch:{ Exception -> 0x0200 }
    L_0x01c9:
        r7 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x01e9 }
        r7.checkSaveToGalleryFiles();	 Catch:{ Exception -> 0x01e9 }
    L_0x01d0:
        return r5;
    L_0x01d1:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);
        goto L_0x0012;
    L_0x01d9:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);
        goto L_0x001c;
    L_0x01e1:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);	 Catch:{ Exception -> 0x01e9 }
        goto L_0x00bc;
    L_0x01e9:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);
        goto L_0x01d0;
    L_0x01f0:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);	 Catch:{ Exception -> 0x01e9 }
        goto L_0x010f;
    L_0x01f8:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);	 Catch:{ Exception -> 0x01e9 }
        goto L_0x016c;
    L_0x0200:
        r3 = move-exception;
        r7 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r7, r3);	 Catch:{ Exception -> 0x01e9 }
        goto L_0x01c9;
    L_0x0207:
        r7 = "tmessages";
        r8 = "this Android can't rename files";
        org.telegram.messenger.FileLog.m11e(r7, r8);	 Catch:{ Exception -> 0x01e9 }
        goto L_0x01c9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.createMediaPaths():java.util.HashMap<java.lang.Integer, java.io.File>");
    }

    private boolean canMoveFiles(File from, File to, int type) {
        Throwable e;
        Throwable th;
        RandomAccessFile file = null;
        File srcFile = null;
        File dstFile = null;
        File srcFile2;
        if (type == 0) {
            try {
                srcFile2 = new File(from, "000000000_999999_temp.jpg");
                try {
                    dstFile = new File(to, "000000000_999999.jpg");
                    srcFile = srcFile2;
                } catch (Exception e2) {
                    e = e2;
                    srcFile = srcFile2;
                    try {
                        FileLog.m13e("tmessages", e);
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e3) {
                                FileLog.m13e("tmessages", e3);
                            }
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e32) {
                                FileLog.m13e("tmessages", e32);
                            }
                        }
                        throw th;
                    }
                }
            } catch (Exception e4) {
                e32 = e4;
                FileLog.m13e("tmessages", e32);
                if (file != null) {
                    file.close();
                }
                return false;
            }
        } else if (type == 3) {
            srcFile2 = new File(from, "000000000_999999_temp.doc");
            dstFile = new File(to, "000000000_999999.doc");
            srcFile = srcFile2;
        } else if (type == 1) {
            srcFile2 = new File(from, "000000000_999999_temp.ogg");
            dstFile = new File(to, "000000000_999999.ogg");
            srcFile = srcFile2;
        } else if (type == 2) {
            srcFile2 = new File(from, "000000000_999999_temp.mp4");
            dstFile = new File(to, "000000000_999999.mp4");
            srcFile = srcFile2;
        }
        byte[] buffer = new byte[MessagesController.UPDATE_MASK_PHONE];
        srcFile.createNewFile();
        RandomAccessFile file2 = new RandomAccessFile(srcFile, "rws");
        try {
            file2.write(buffer);
            file2.close();
            file = null;
            boolean canRename = srcFile.renameTo(dstFile);
            srcFile.delete();
            dstFile.delete();
            if (!canRename) {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Throwable e322) {
                        FileLog.m13e("tmessages", e322);
                    }
                }
                return false;
            } else if (file == null) {
                return true;
            } else {
                try {
                    file.close();
                    return true;
                } catch (Throwable e3222) {
                    FileLog.m13e("tmessages", e3222);
                    return true;
                }
            }
        } catch (Exception e5) {
            e3222 = e5;
            file = file2;
            FileLog.m13e("tmessages", e3222);
            if (file != null) {
                file.close();
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            file = file2;
            if (file != null) {
                file.close();
            }
            throw th;
        }
    }

    public Float getFileProgress(String location) {
        if (location == null) {
            return null;
        }
        return (Float) this.fileProgresses.get(location);
    }

    private void performReplace(String oldKey, String newKey) {
        BitmapDrawable b = this.memCache.get(oldKey);
        if (b != null) {
            this.ignoreRemoval = oldKey;
            this.memCache.remove(oldKey);
            this.memCache.put(newKey, b);
            this.ignoreRemoval = null;
        }
        Integer val = (Integer) this.bitmapUseCounts.get(oldKey);
        if (val != null) {
            this.bitmapUseCounts.put(newKey, val);
            this.bitmapUseCounts.remove(oldKey);
        }
    }

    public void incrementUseCount(String key) {
        Integer count = (Integer) this.bitmapUseCounts.get(key);
        if (count == null) {
            this.bitmapUseCounts.put(key, Integer.valueOf(1));
        } else {
            this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String key) {
        Integer count = (Integer) this.bitmapUseCounts.get(key);
        if (count == null) {
            return true;
        }
        if (count.intValue() == 1) {
            this.bitmapUseCounts.remove(key);
            return true;
        }
        this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() - 1));
        return false;
    }

    public void removeImage(String key) {
        this.bitmapUseCounts.remove(key);
        this.memCache.remove(key);
    }

    public boolean isInCache(String key) {
        return this.memCache.get(key) != null;
    }

    public void clearMemory() {
        this.memCache.evictAll();
    }

    private void removeFromWaitingForThumb(Integer TAG) {
        String location = (String) this.waitingForQualityThumbByTag.get(TAG);
        if (location != null) {
            ThumbGenerateInfo info = (ThumbGenerateInfo) this.waitingForQualityThumb.get(location);
            if (info != null) {
                info.count = info.count - 1;
                if (info.count == 0) {
                    this.waitingForQualityThumb.remove(location);
                }
            }
            this.waitingForQualityThumbByTag.remove(TAG);
        }
    }

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, int type) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new C05085(type, imageReceiver));
        }
    }

    public BitmapDrawable getImageFromMemory(String key) {
        return this.memCache.get(key);
    }

    public BitmapDrawable getImageFromMemory(TLObject fileLocation, String httpUrl, String filter) {
        if (fileLocation == null && httpUrl == null) {
            return null;
        }
        String key = null;
        if (httpUrl != null) {
            key = Utilities.MD5(httpUrl);
        } else if (fileLocation instanceof FileLocation) {
            FileLocation location = (FileLocation) fileLocation;
            key = location.volume_id + "_" + location.local_id;
        } else if (fileLocation instanceof Document) {
            Document location2 = (Document) fileLocation;
            key = location2.dc_id + "_" + location2.id;
        }
        if (filter != null) {
            key = key + "@" + filter;
        }
        return this.memCache.get(key);
    }

    private void replaceImageInCacheInternal(String oldKey, String newKey, FileLocation newLocation) {
        ArrayList<String> arr = this.memCache.getFilterKeys(oldKey);
        if (arr != null) {
            for (int a = 0; a < arr.size(); a++) {
                String filter = (String) arr.get(a);
                performReplace(oldKey + "@" + filter, newKey + "@" + filter);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldK, newK, newLocation);
            }
            return;
        }
        performReplace(oldKey, newKey);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldKey, newKey, newLocation);
    }

    public void replaceImageInCache(String oldKey, String newKey, FileLocation newLocation, boolean post) {
        if (post) {
            AndroidUtilities.runOnUIThread(new C05096(oldKey, newKey, newLocation));
        } else {
            replaceImageInCacheInternal(oldKey, newKey, newLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmap, String key) {
        this.memCache.put(key, bitmap);
    }

    private void generateThumb(int mediaType, File originalPath, FileLocation thumbLocation, String filter) {
        if ((mediaType == 0 || mediaType == 2 || mediaType == 3) && originalPath != null && thumbLocation != null) {
            if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(thumbLocation))) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(mediaType, originalPath, thumbLocation, filter));
            }
        }
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String key, String url, String ext, TLObject imageLocation, String httpLocation, String filter, int size, boolean cacheOnly, int thumb) {
        if (imageReceiver != null && url != null && key != null) {
            Integer TAG = imageReceiver.getTag(thumb != 0);
            if (TAG == null) {
                TAG = Integer.valueOf(this.lastImageNum);
                imageReceiver.setTag(TAG, thumb != 0);
                this.lastImageNum++;
                if (this.lastImageNum == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    this.lastImageNum = 0;
                }
            }
            this.imageLoadQueue.postRunnable(new C05107(thumb, url, key, TAG, imageReceiver, httpLocation, imageReceiver.isNeedsQualityThumb(), imageReceiver.getParentMessageObject(), imageLocation, filter, imageReceiver.isShouldGenerateQualityThumb(), cacheOnly, size, ext));
        }
    }

    public void loadImageForImageReceiver(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            BitmapDrawable bitmapDrawable;
            String key = imageReceiver.getKey();
            if (key != null) {
                bitmapDrawable = this.memCache.get(key);
                if (bitmapDrawable != null) {
                    cancelLoadingForImageReceiver(imageReceiver, 0);
                    if (!imageReceiver.isForcePreview()) {
                        imageReceiver.setImageBitmapByKey(bitmapDrawable, key, false, true);
                        return;
                    }
                }
            }
            boolean thumbSet = false;
            String thumbKey = imageReceiver.getThumbKey();
            if (thumbKey != null) {
                bitmapDrawable = this.memCache.get(thumbKey);
                if (bitmapDrawable != null) {
                    imageReceiver.setImageBitmapByKey(bitmapDrawable, thumbKey, true, true);
                    cancelLoadingForImageReceiver(imageReceiver, 1);
                    thumbSet = true;
                }
            }
            TLObject thumbLocation = imageReceiver.getThumbLocation();
            TLObject imageLocation = imageReceiver.getImageLocation();
            String httpLocation = imageReceiver.getHttpImageLocation();
            boolean saveImageToCache = false;
            String url = null;
            String thumbUrl = null;
            key = null;
            thumbKey = null;
            String ext = imageReceiver.getExt();
            if (ext == null) {
                ext = "jpg";
            }
            if (httpLocation != null) {
                key = Utilities.MD5(httpLocation);
                url = key + "." + getHttpUrlExtension(httpLocation, "jpg");
            } else if (imageLocation != null) {
                if (imageLocation instanceof FileLocation) {
                    FileLocation location = (FileLocation) imageLocation;
                    key = location.volume_id + "_" + location.local_id;
                    url = key + "." + ext;
                    if (!(imageReceiver.getExt() == null && location.key == null && (location.volume_id != -2147483648L || location.local_id >= 0))) {
                        saveImageToCache = true;
                    }
                } else if (imageLocation instanceof Document) {
                    Document document = (Document) imageLocation;
                    if (document.id != 0 && document.dc_id != 0) {
                        key = document.dc_id + "_" + document.id;
                        String docExt = FileLoader.getDocumentFileName(document);
                        if (docExt != null) {
                            int idx = docExt.lastIndexOf(46);
                            if (idx != -1) {
                                docExt = docExt.substring(idx);
                                if (docExt.length() <= 1) {
                                    if (document.mime_type == null && document.mime_type.equals(MimeTypes.VIDEO_MP4)) {
                                        docExt = ".mp4";
                                    } else {
                                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                }
                                url = key + docExt;
                                if (null != null) {
                                    thumbUrl = null + "." + ext;
                                }
                                if (MessageObject.isGifDocument(document)) {
                                    saveImageToCache = true;
                                } else {
                                    saveImageToCache = false;
                                }
                            }
                        }
                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                        if (docExt.length() <= 1) {
                            if (document.mime_type == null) {
                            }
                            docExt = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        url = key + docExt;
                        if (null != null) {
                            thumbUrl = null + "." + ext;
                        }
                        if (MessageObject.isGifDocument(document)) {
                            saveImageToCache = false;
                        } else {
                            saveImageToCache = true;
                        }
                    } else {
                        return;
                    }
                }
                if (imageLocation == thumbLocation) {
                    imageLocation = null;
                    key = null;
                    url = null;
                }
            }
            if (thumbLocation != null) {
                thumbKey = thumbLocation.volume_id + "_" + thumbLocation.local_id;
                thumbUrl = thumbKey + "." + ext;
            }
            String filter = imageReceiver.getFilter();
            String thumbFilter = imageReceiver.getThumbFilter();
            if (!(key == null || filter == null)) {
                key = key + "@" + filter;
            }
            if (!(thumbKey == null || thumbFilter == null)) {
                thumbKey = thumbKey + "@" + thumbFilter;
            }
            if (httpLocation != null) {
                createLoadOperationForImageReceiver(imageReceiver, thumbKey, thumbUrl, ext, thumbLocation, null, thumbFilter, 0, true, thumbSet ? 2 : 1);
                createLoadOperationForImageReceiver(imageReceiver, key, url, ext, null, httpLocation, filter, 0, true, 0);
                return;
            }
            createLoadOperationForImageReceiver(imageReceiver, thumbKey, thumbUrl, ext, thumbLocation, null, thumbFilter, 0, true, thumbSet ? 2 : 1);
            int size = imageReceiver.getSize();
            boolean z = saveImageToCache || imageReceiver.getCacheOnly();
            createLoadOperationForImageReceiver(imageReceiver, key, url, ext, imageLocation, null, filter, size, z, 0);
        }
    }

    private void httpFileLoadError(String location) {
        this.imageLoadQueue.postRunnable(new C05118(location));
    }

    private void fileDidLoaded(String location, File finalFile, int type) {
        this.imageLoadQueue.postRunnable(new C05129(location, type, finalFile));
    }

    private void fileDidFailedLoad(String location, int canceled) {
        if (canceled != 1) {
            this.imageLoadQueue.postRunnable(new AnonymousClass10(location));
        }
    }

    private void runHttpTasks(boolean complete) {
        if (complete) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            ((HttpImageTask) this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpTasksCount++;
        }
    }

    public void loadHttpFile(String url, String defaultExt) {
        if (url != null && url.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(url)) {
            String ext = getHttpUrlExtension(url, defaultExt);
            File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(url) + "_temp." + ext);
            file.delete();
            HttpFileTask task = new HttpFileTask(url, file, ext);
            this.httpFileLoadTasks.add(task);
            this.httpFileLoadTasksByKeys.put(url, task);
            runHttpFileLoadTasks(null, 0);
        }
    }

    public void cancelLoadHttpFile(String url) {
        HttpFileTask task = (HttpFileTask) this.httpFileLoadTasksByKeys.get(url);
        if (task != null) {
            task.cancel(true);
            this.httpFileLoadTasksByKeys.remove(url);
            this.httpFileLoadTasks.remove(task);
        }
        Runnable runnable = (Runnable) this.retryHttpsTasks.get(url);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks(null, 0);
    }

    private void runHttpFileLoadTasks(HttpFileTask oldTask, int reason) {
        AndroidUtilities.runOnUIThread(new AnonymousClass11(oldTask, reason));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r22, android.net.Uri r23, float r24, float r25, boolean r26) {
        /*
        r8 = new android.graphics.BitmapFactory$Options;
        r8.<init>();
        r2 = 1;
        r8.inJustDecodeBounds = r2;
        r15 = 0;
        if (r22 != 0) goto L_0x0024;
    L_0x000b:
        if (r23 == 0) goto L_0x0024;
    L_0x000d:
        r2 = r23.getScheme();
        if (r2 == 0) goto L_0x0024;
    L_0x0013:
        r14 = 0;
        r2 = r23.getScheme();
        r3 = "file";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x00a1;
    L_0x0020:
        r22 = r23.getPath();
    L_0x0024:
        if (r22 == 0) goto L_0x00af;
    L_0x0026:
        r0 = r22;
        android.graphics.BitmapFactory.decodeFile(r0, r8);
    L_0x002b:
        r2 = r8.outWidth;
        r0 = (float) r2;
        r20 = r0;
        r2 = r8.outHeight;
        r0 = (float) r2;
        r19 = r0;
        if (r26 == 0) goto L_0x00db;
    L_0x0037:
        r2 = r20 / r24;
        r3 = r19 / r25;
        r21 = java.lang.Math.max(r2, r3);
    L_0x003f:
        r2 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r2 = (r21 > r2 ? 1 : (r21 == r2 ? 0 : -1));
        if (r2 >= 0) goto L_0x0047;
    L_0x0045:
        r21 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
    L_0x0047:
        r2 = 0;
        r8.inJustDecodeBounds = r2;
        r0 = r21;
        r2 = (int) r0;
        r8.inSampleSize = r2;
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r2 >= r3) goto L_0x00e5;
    L_0x0055:
        r2 = 1;
    L_0x0056:
        r8.inPurgeable = r2;
        r13 = 0;
        if (r22 == 0) goto L_0x00e8;
    L_0x005b:
        r13 = r22;
    L_0x005d:
        r6 = 0;
        if (r13 == 0) goto L_0x0076;
    L_0x0060:
        r12 = new android.media.ExifInterface;	 Catch:{ Throwable -> 0x01ab }
        r12.<init>(r13);	 Catch:{ Throwable -> 0x01ab }
        r2 = "Orientation";
        r3 = 1;
        r18 = r12.getAttributeInt(r2, r3);	 Catch:{ Throwable -> 0x01ab }
        r16 = new android.graphics.Matrix;	 Catch:{ Throwable -> 0x01ab }
        r16.<init>();	 Catch:{ Throwable -> 0x01ab }
        switch(r18) {
            case 3: goto L_0x0103;
            case 4: goto L_0x0074;
            case 5: goto L_0x0074;
            case 6: goto L_0x00f0;
            case 7: goto L_0x0074;
            case 8: goto L_0x010c;
            default: goto L_0x0074;
        };
    L_0x0074:
        r6 = r16;
    L_0x0076:
        r1 = 0;
        if (r22 == 0) goto L_0x0157;
    L_0x0079:
        r0 = r22;
        r1 = android.graphics.BitmapFactory.decodeFile(r0, r8);	 Catch:{ Throwable -> 0x0115 }
        if (r1 == 0) goto L_0x00a0;
    L_0x0081:
        r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x0115 }
        if (r2 == 0) goto L_0x0088;
    L_0x0085:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x0115 }
    L_0x0088:
        r2 = 0;
        r3 = 0;
        r4 = r1.getWidth();	 Catch:{ Throwable -> 0x0115 }
        r5 = r1.getHeight();	 Catch:{ Throwable -> 0x0115 }
        r7 = 1;
        r17 = org.telegram.messenger.Bitmaps.createBitmap(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0115 }
        r0 = r17;
        if (r0 == r1) goto L_0x00a0;
    L_0x009b:
        r1.recycle();	 Catch:{ Throwable -> 0x0115 }
        r1 = r17;
    L_0x00a0:
        return r1;
    L_0x00a1:
        r22 = org.telegram.messenger.AndroidUtilities.getPath(r23);	 Catch:{ Throwable -> 0x00a7 }
        goto L_0x0024;
    L_0x00a7:
        r9 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);
        goto L_0x0024;
    L_0x00af:
        if (r23 == 0) goto L_0x002b;
    L_0x00b1:
        r11 = 0;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00d3 }
        r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x00d3 }
        r0 = r23;
        r15 = r2.openInputStream(r0);	 Catch:{ Throwable -> 0x00d3 }
        r2 = 0;
        android.graphics.BitmapFactory.decodeStream(r15, r2, r8);	 Catch:{ Throwable -> 0x00d3 }
        r15.close();	 Catch:{ Throwable -> 0x00d3 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00d3 }
        r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x00d3 }
        r0 = r23;
        r15 = r2.openInputStream(r0);	 Catch:{ Throwable -> 0x00d3 }
        goto L_0x002b;
    L_0x00d3:
        r9 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);
        r1 = 0;
        goto L_0x00a0;
    L_0x00db:
        r2 = r20 / r24;
        r3 = r19 / r25;
        r21 = java.lang.Math.min(r2, r3);
        goto L_0x003f;
    L_0x00e5:
        r2 = 0;
        goto L_0x0056;
    L_0x00e8:
        if (r23 == 0) goto L_0x005d;
    L_0x00ea:
        r13 = org.telegram.messenger.AndroidUtilities.getPath(r23);
        goto L_0x005d;
    L_0x00f0:
        r2 = 1119092736; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r0 = r16;
        r0.postRotate(r2);	 Catch:{ Throwable -> 0x00f9 }
        goto L_0x0074;
    L_0x00f9:
        r9 = move-exception;
        r6 = r16;
    L_0x00fc:
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);
        goto L_0x0076;
    L_0x0103:
        r2 = 1127481344; // 0x43340000 float:180.0 double:5.570497984E-315;
        r0 = r16;
        r0.postRotate(r2);	 Catch:{ Throwable -> 0x00f9 }
        goto L_0x0074;
    L_0x010c:
        r2 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r0 = r16;
        r0.postRotate(r2);	 Catch:{ Throwable -> 0x00f9 }
        goto L_0x0074;
    L_0x0115:
        r9 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);
        r2 = getInstance();
        r2.clearMemory();
        if (r1 != 0) goto L_0x0133;
    L_0x0124:
        r0 = r22;
        r1 = android.graphics.BitmapFactory.decodeFile(r0, r8);	 Catch:{ Throwable -> 0x014f }
        if (r1 == 0) goto L_0x0133;
    L_0x012c:
        r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x014f }
        if (r2 == 0) goto L_0x0133;
    L_0x0130:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x014f }
    L_0x0133:
        if (r1 == 0) goto L_0x00a0;
    L_0x0135:
        r2 = 0;
        r3 = 0;
        r4 = r1.getWidth();	 Catch:{ Throwable -> 0x014f }
        r5 = r1.getHeight();	 Catch:{ Throwable -> 0x014f }
        r7 = 1;
        r17 = org.telegram.messenger.Bitmaps.createBitmap(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x014f }
        r0 = r17;
        if (r0 == r1) goto L_0x00a0;
    L_0x0148:
        r1.recycle();	 Catch:{ Throwable -> 0x014f }
        r1 = r17;
        goto L_0x00a0;
    L_0x014f:
        r10 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r10);
        goto L_0x00a0;
    L_0x0157:
        if (r23 == 0) goto L_0x00a0;
    L_0x0159:
        r2 = 0;
        r1 = android.graphics.BitmapFactory.decodeStream(r15, r2, r8);	 Catch:{ Throwable -> 0x018c }
        if (r1 == 0) goto L_0x017f;
    L_0x0160:
        r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x018c }
        if (r2 == 0) goto L_0x0167;
    L_0x0164:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x018c }
    L_0x0167:
        r2 = 0;
        r3 = 0;
        r4 = r1.getWidth();	 Catch:{ Throwable -> 0x018c }
        r5 = r1.getHeight();	 Catch:{ Throwable -> 0x018c }
        r7 = 1;
        r17 = org.telegram.messenger.Bitmaps.createBitmap(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x018c }
        r0 = r17;
        if (r0 == r1) goto L_0x017f;
    L_0x017a:
        r1.recycle();	 Catch:{ Throwable -> 0x018c }
        r1 = r17;
    L_0x017f:
        r15.close();	 Catch:{ Throwable -> 0x0184 }
        goto L_0x00a0;
    L_0x0184:
        r9 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);
        goto L_0x00a0;
    L_0x018c:
        r9 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);	 Catch:{ all -> 0x019f }
        r15.close();	 Catch:{ Throwable -> 0x0197 }
        goto L_0x00a0;
    L_0x0197:
        r9 = move-exception;
        r2 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r2, r9);
        goto L_0x00a0;
    L_0x019f:
        r2 = move-exception;
        r15.close();	 Catch:{ Throwable -> 0x01a4 }
    L_0x01a3:
        throw r2;
    L_0x01a4:
        r9 = move-exception;
        r3 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r3, r9);
        goto L_0x01a3;
    L_0x01ab:
        r9 = move-exception;
        goto L_0x00fc;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
        if (photoSize != null && photoSize.bytes == null) {
            try {
                RandomAccessFile f = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
                if (((int) f.length()) < 20000) {
                    photoSize.bytes = new byte[((int) f.length())];
                    f.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    private static PhotoSize scaleAndSaveImageInternal(Bitmap bitmap, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway) throws Exception {
        Bitmap scaledBitmap;
        if (scaleFactor > TouchHelperCallback.ALPHA_FULL || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap, w, h, true);
        } else {
            scaledBitmap = bitmap;
        }
        TL_fileLocation location = new TL_fileLocation();
        location.volume_id = -2147483648L;
        location.dc_id = LinearLayoutManager.INVALID_OFFSET;
        location.local_id = UserConfig.lastLocalId;
        UserConfig.lastLocalId--;
        PhotoSize size = new TL_photoSize();
        size.location = location;
        size.f34w = scaledBitmap.getWidth();
        size.f33h = scaledBitmap.getHeight();
        if (size.f34w <= 100 && size.f33h <= 100) {
            size.type = "s";
        } else if (size.f34w <= 320 && size.f33h <= 320) {
            size.type = "m";
        } else if (size.f34w <= 800 && size.f33h <= 800) {
            size.type = "x";
        } else if (size.f34w > 1280 || size.f33h > 1280) {
            size.type = "w";
        } else {
            size.type = "y";
        }
        FileOutputStream stream = new FileOutputStream(new File(FileLoader.getInstance().getDirectory(4), location.volume_id + "_" + location.local_id + ".jpg"));
        scaledBitmap.compress(CompressFormat.JPEG, quality, stream);
        if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            scaledBitmap.compress(CompressFormat.JPEG, quality, stream2);
            size.bytes = stream2.toByteArray();
            size.size = size.bytes.length;
            stream2.close();
        } else {
            size.size = (int) stream.getChannel().size();
        }
        stream.close();
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle();
        }
        return size;
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage(bitmap, maxWidth, maxHeight, quality, cache, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        if (bitmap == null) {
            return null;
        }
        float photoW = (float) bitmap.getWidth();
        float photoH = (float) bitmap.getHeight();
        if (photoW == 0.0f || photoH == 0.0f) {
            return null;
        }
        boolean scaleAnyway = false;
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        if (!(minWidth == 0 || minHeight == 0 || (photoW >= ((float) minWidth) && photoH >= ((float) minHeight)))) {
            if (photoW < ((float) minWidth) && photoH > ((float) minHeight)) {
                scaleFactor = photoW / ((float) minWidth);
            } else if (photoW <= ((float) minWidth) || photoH >= ((float) minHeight)) {
                scaleFactor = Math.max(photoW / ((float) minWidth), photoH / ((float) minHeight));
            } else {
                scaleFactor = photoH / ((float) minHeight);
            }
            scaleAnyway = true;
        }
        int w = (int) (photoW / scaleFactor);
        int h = (int) (photoH / scaleFactor);
        if (h == 0 || w == 0) {
            return null;
        }
        try {
            return scaleAndSaveImageInternal(bitmap, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway);
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
            return null;
        }
    }

    public static String getHttpUrlExtension(String url, String defaultExt) {
        String ext = null;
        int idx = url.lastIndexOf(46);
        if (idx != -1) {
            ext = url.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0 || ext.length() > 4) {
            return defaultExt;
        }
        return ext;
    }

    public static void saveMessageThumbs(Message message) {
        PhotoSize photoSize = null;
        Iterator i$;
        PhotoSize size;
        if (!(message.media instanceof TL_messageMediaPhoto)) {
            if (!(message.media instanceof TL_messageMediaDocument)) {
                if ((message.media instanceof TL_messageMediaWebPage) && message.media.webpage.photo != null) {
                    i$ = message.media.webpage.photo.sizes.iterator();
                    while (i$.hasNext()) {
                        size = (PhotoSize) i$.next();
                        if (size instanceof TL_photoCachedSize) {
                            photoSize = size;
                            break;
                        }
                    }
                }
            } else if (message.media.document.thumb instanceof TL_photoCachedSize) {
                photoSize = message.media.document.thumb;
            }
        } else {
            i$ = message.media.photo.sizes.iterator();
            while (i$.hasNext()) {
                size = (PhotoSize) i$.next();
                if (size instanceof TL_photoCachedSize) {
                    photoSize = size;
                    break;
                }
            }
        }
        if (photoSize != null && photoSize.bytes != null && photoSize.bytes.length != 0) {
            if (photoSize.location instanceof TL_fileLocationUnavailable) {
                photoSize.location = new TL_fileLocation();
                photoSize.location.volume_id = -2147483648L;
                photoSize.location.dc_id = LinearLayoutManager.INVALID_OFFSET;
                photoSize.location.local_id = UserConfig.lastLocalId;
                UserConfig.lastLocalId--;
            }
            File file = FileLoader.getPathToAttach(photoSize, true);
            if (!file.exists()) {
                try {
                    RandomAccessFile writeFile = new RandomAccessFile(file, "rws");
                    writeFile.write(photoSize.bytes);
                    writeFile.close();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            TL_photoSize newPhotoSize = new TL_photoSize();
            newPhotoSize.w = photoSize.f34w;
            newPhotoSize.h = photoSize.f33h;
            newPhotoSize.location = photoSize.location;
            newPhotoSize.size = photoSize.size;
            newPhotoSize.type = photoSize.type;
            int a;
            if (message.media instanceof TL_messageMediaPhoto) {
                for (a = 0; a < message.media.photo.sizes.size(); a++) {
                    if (message.media.photo.sizes.get(a) instanceof TL_photoCachedSize) {
                        message.media.photo.sizes.set(a, newPhotoSize);
                        return;
                    }
                }
            } else if (message.media instanceof TL_messageMediaDocument) {
                message.media.document.thumb = newPhotoSize;
            } else if (message.media instanceof TL_messageMediaWebPage) {
                for (a = 0; a < message.media.webpage.photo.sizes.size(); a++) {
                    if (message.media.webpage.photo.sizes.get(a) instanceof TL_photoCachedSize) {
                        message.media.webpage.photo.sizes.set(a, newPhotoSize);
                        return;
                    }
                }
            }
        }
    }

    public static void saveMessagesThumbs(ArrayList<Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (int a = 0; a < messages.size(); a++) {
                saveMessageThumbs((Message) messages.get(a));
            }
        }
    }
}
