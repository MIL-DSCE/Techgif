package org.telegram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 2;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int datacenter_id;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile fiv;
    private boolean isForceRequest;
    private byte[] iv;
    private byte[] key;
    private InputFileLocation location;
    private int nextDownloadOffset;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestsCount;
    private volatile int state;
    private File storePath;
    private File tempPath;
    private int totalBytesCount;

    /* renamed from: org.telegram.messenger.FileLoadOperation.1 */
    class C04641 implements Runnable {
        C04641() {
        }

        public void run() {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.2 */
    class C04652 implements Runnable {
        C04652() {
        }

        public void run() {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.3 */
    class C04663 implements Runnable {
        C04663() {
        }

        public void run() {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.4 */
    class C04674 implements Runnable {
        C04674() {
        }

        public void run() {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.5 */
    class C04685 implements Runnable {
        C04685() {
        }

        public void run() {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.6 */
    class C04696 implements Runnable {
        C04696() {
        }

        public void run() {
            if (FileLoadOperation.this.totalBytesCount == 0 || FileLoadOperation.this.downloadedBytes != FileLoadOperation.this.totalBytesCount) {
                FileLoadOperation.this.startDownloadRequest();
                return;
            }
            try {
                FileLoadOperation.this.onFinishLoadingFile();
            } catch (Exception e) {
                FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.7 */
    class C04707 implements Runnable {
        C04707() {
        }

        public void run() {
            if (FileLoadOperation.this.state != FileLoadOperation.stateFinished && FileLoadOperation.this.state != FileLoadOperation.stateFailed) {
                FileLoadOperation.this.state = FileLoadOperation.stateFailed;
                FileLoadOperation.this.cleanup();
                if (FileLoadOperation.this.requestInfos != null) {
                    for (int a = 0; a < FileLoadOperation.this.requestInfos.size(); a += FileLoadOperation.stateDownloading) {
                        RequestInfo requestInfo = (RequestInfo) FileLoadOperation.this.requestInfos.get(a);
                        if (requestInfo.requestToken != 0) {
                            ConnectionsManager.getInstance().cancelRequest(requestInfo.requestToken, true);
                        }
                    }
                }
                FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, FileLoadOperation.stateDownloading);
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.8 */
    class C04718 implements Runnable {
        C04718() {
        }

        public void run() {
            try {
                FileLoadOperation.this.onFinishLoadingFile();
            } catch (Exception e) {
                FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
            }
        }
    }

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    private static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;

        private RequestInfo() {
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation.9 */
    class C16789 implements RequestDelegate {
        final /* synthetic */ RequestInfo val$requestInfo;

        C16789(RequestInfo requestInfo) {
            this.val$requestInfo = requestInfo;
        }

        public void run(TLObject response, TL_error error) {
            this.val$requestInfo.response = (TL_upload_file) response;
            FileLoadOperation.this.processRequestResult(this.val$requestInfo, error);
        }
    }

    public FileLoadOperation(FileLocation photoLocation, String extension, int size) {
        this.state = 0;
        if (photoLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.id = photoLocation.volume_id;
            this.location.volume_id = photoLocation.volume_id;
            this.location.access_hash = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.iv = new byte[32];
            System.arraycopy(photoLocation.iv, 0, this.iv, 0, this.iv.length);
            this.key = photoLocation.key;
            this.datacenter_id = photoLocation.dc_id;
        } else if (photoLocation instanceof TL_fileLocation) {
            this.location = new TL_inputFileLocation();
            this.location.volume_id = photoLocation.volume_id;
            this.location.secret = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.datacenter_id = photoLocation.dc_id;
        }
        this.totalBytesCount = size;
        if (extension == null) {
            extension = "jpg";
        }
        this.ext = extension;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r12) {
        /*
        r11 = this;
        r5 = 1;
        r4 = -1;
        r3 = 0;
        r11.<init>();
        r11.state = r3;
        r6 = r12 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;	 Catch:{ Exception -> 0x00a9 }
        if (r6 == 0) goto L_0x008d;
    L_0x000c:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation;	 Catch:{ Exception -> 0x00a9 }
        r6.<init>();	 Catch:{ Exception -> 0x00a9 }
        r11.location = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00a9 }
        r8 = r12.id;	 Catch:{ Exception -> 0x00a9 }
        r6.id = r8;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00a9 }
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x00a9 }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x00a9 }
        r6 = r12.dc_id;	 Catch:{ Exception -> 0x00a9 }
        r11.datacenter_id = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = 32;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x00a9 }
        r11.iv = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = r12.iv;	 Catch:{ Exception -> 0x00a9 }
        r7 = 0;
        r8 = r11.iv;	 Catch:{ Exception -> 0x00a9 }
        r9 = 0;
        r10 = r11.iv;	 Catch:{ Exception -> 0x00a9 }
        r10 = r10.length;	 Catch:{ Exception -> 0x00a9 }
        java.lang.System.arraycopy(r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x00a9 }
        r6 = r12.key;	 Catch:{ Exception -> 0x00a9 }
        r11.key = r6;	 Catch:{ Exception -> 0x00a9 }
    L_0x0039:
        r6 = r12.size;	 Catch:{ Exception -> 0x00a9 }
        r11.totalBytesCount = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.key;	 Catch:{ Exception -> 0x00a9 }
        if (r6 == 0) goto L_0x0057;
    L_0x0041:
        r2 = 0;
        r6 = r11.totalBytesCount;	 Catch:{ Exception -> 0x00a9 }
        r6 = r6 % 16;
        if (r6 == 0) goto L_0x0057;
    L_0x0048:
        r6 = r11.totalBytesCount;	 Catch:{ Exception -> 0x00a9 }
        r6 = r6 % 16;
        r6 = 16 - r6;
        r11.bytesCountPadding = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.totalBytesCount;	 Catch:{ Exception -> 0x00a9 }
        r7 = r11.bytesCountPadding;	 Catch:{ Exception -> 0x00a9 }
        r6 = r6 + r7;
        r11.totalBytesCount = r6;	 Catch:{ Exception -> 0x00a9 }
    L_0x0057:
        r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r12);	 Catch:{ Exception -> 0x00a9 }
        r11.ext = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.ext;	 Catch:{ Exception -> 0x00a9 }
        if (r6 == 0) goto L_0x006b;
    L_0x0061:
        r6 = r11.ext;	 Catch:{ Exception -> 0x00a9 }
        r7 = 46;
        r1 = r6.lastIndexOf(r7);	 Catch:{ Exception -> 0x00a9 }
        if (r1 != r4) goto L_0x00c0;
    L_0x006b:
        r6 = "";
        r11.ext = r6;	 Catch:{ Exception -> 0x00a9 }
    L_0x006f:
        r6 = r11.ext;	 Catch:{ Exception -> 0x00a9 }
        r6 = r6.length();	 Catch:{ Exception -> 0x00a9 }
        if (r6 > r5) goto L_0x008c;
    L_0x0077:
        r6 = r12.mime_type;	 Catch:{ Exception -> 0x00a9 }
        if (r6 == 0) goto L_0x00e6;
    L_0x007b:
        r6 = r12.mime_type;	 Catch:{ Exception -> 0x00a9 }
        r7 = r6.hashCode();	 Catch:{ Exception -> 0x00a9 }
        switch(r7) {
            case 187091926: goto L_0x00d2;
            case 1331848029: goto L_0x00c9;
            default: goto L_0x0084;
        };	 Catch:{ Exception -> 0x00a9 }
    L_0x0084:
        r3 = r4;
    L_0x0085:
        switch(r3) {
            case 0: goto L_0x00dc;
            case 1: goto L_0x00e1;
            default: goto L_0x0088;
        };	 Catch:{ Exception -> 0x00a9 }
    L_0x0088:
        r3 = "";
        r11.ext = r3;	 Catch:{ Exception -> 0x00a9 }
    L_0x008c:
        return;
    L_0x008d:
        r6 = r12 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Exception -> 0x00a9 }
        if (r6 == 0) goto L_0x0039;
    L_0x0091:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;	 Catch:{ Exception -> 0x00a9 }
        r6.<init>();	 Catch:{ Exception -> 0x00a9 }
        r11.location = r6;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00a9 }
        r8 = r12.id;	 Catch:{ Exception -> 0x00a9 }
        r6.id = r8;	 Catch:{ Exception -> 0x00a9 }
        r6 = r11.location;	 Catch:{ Exception -> 0x00a9 }
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x00a9 }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x00a9 }
        r6 = r12.dc_id;	 Catch:{ Exception -> 0x00a9 }
        r11.datacenter_id = r6;	 Catch:{ Exception -> 0x00a9 }
        goto L_0x0039;
    L_0x00a9:
        r0 = move-exception;
        r3 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r3, r0);
        r3 = 2;
        r11.state = r3;
        r11.cleanup();
        r3 = org.telegram.messenger.Utilities.stageQueue;
        r4 = new org.telegram.messenger.FileLoadOperation$1;
        r4.<init>();
        r3.postRunnable(r4);
        goto L_0x008c;
    L_0x00c0:
        r6 = r11.ext;	 Catch:{ Exception -> 0x00a9 }
        r6 = r6.substring(r1);	 Catch:{ Exception -> 0x00a9 }
        r11.ext = r6;	 Catch:{ Exception -> 0x00a9 }
        goto L_0x006f;
    L_0x00c9:
        r5 = "video/mp4";
        r5 = r6.equals(r5);	 Catch:{ Exception -> 0x00a9 }
        if (r5 == 0) goto L_0x0084;
    L_0x00d1:
        goto L_0x0085;
    L_0x00d2:
        r3 = "audio/ogg";
        r3 = r6.equals(r3);	 Catch:{ Exception -> 0x00a9 }
        if (r3 == 0) goto L_0x0084;
    L_0x00da:
        r3 = r5;
        goto L_0x0085;
    L_0x00dc:
        r3 = ".mp4";
        r11.ext = r3;	 Catch:{ Exception -> 0x00a9 }
        goto L_0x008c;
    L_0x00e1:
        r3 = ".ogg";
        r11.ext = r3;	 Catch:{ Exception -> 0x00a9 }
        goto L_0x008c;
    L_0x00e6:
        r3 = "";
        r11.ext = r3;	 Catch:{ Exception -> 0x00a9 }
        goto L_0x008c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document):void");
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPaths(File store, File temp) {
        this.storePath = store;
        this.tempPath = temp;
    }

    public void start() {
        if (this.state == 0) {
            this.currentDownloadChunkSize = this.totalBytesCount >= bigFileSizeFrom ? downloadChunkSizeBig : downloadChunkSize;
            this.currentMaxDownloadRequests = this.totalBytesCount >= bigFileSizeFrom ? stateFailed : maxDownloadRequests;
            this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
            this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
            this.state = stateDownloading;
            if (this.location == null) {
                cleanup();
                Utilities.stageQueue.postRunnable(new C04652());
                return;
            }
            String fileNameTemp;
            String fileNameFinal;
            String fileNameIv = null;
            if (this.location.volume_id == 0 || this.location.local_id == 0) {
                fileNameTemp = this.datacenter_id + "_" + this.location.id + ".temp";
                fileNameFinal = this.datacenter_id + "_" + this.location.id + this.ext;
                if (this.key != null) {
                    fileNameIv = this.datacenter_id + "_" + this.location.id + ".iv";
                }
                if (this.datacenter_id == 0 || this.location.id == 0) {
                    cleanup();
                    Utilities.stageQueue.postRunnable(new C04674());
                    return;
                }
            }
            fileNameTemp = this.location.volume_id + "_" + this.location.local_id + ".temp";
            fileNameFinal = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
            if (this.key != null) {
                fileNameIv = this.location.volume_id + "_" + this.location.local_id + ".iv";
            }
            if (this.datacenter_id == LinearLayoutManager.INVALID_OFFSET || this.location.volume_id == -2147483648L || this.datacenter_id == 0) {
                cleanup();
                Utilities.stageQueue.postRunnable(new C04663());
                return;
            }
            this.cacheFileFinal = new File(this.storePath, fileNameFinal);
            if (!(!this.cacheFileFinal.exists() || this.totalBytesCount == 0 || ((long) this.totalBytesCount) == this.cacheFileFinal.length())) {
                this.cacheFileFinal.delete();
            }
            if (this.cacheFileFinal.exists()) {
                try {
                    onFinishLoadingFile();
                    return;
                } catch (Exception e) {
                    this.delegate.didFailedLoadingFile(this, 0);
                    return;
                }
            }
            this.cacheFileTemp = new File(this.tempPath, fileNameTemp);
            if (this.cacheFileTemp.exists()) {
                this.downloadedBytes = (int) this.cacheFileTemp.length();
                int i = (this.downloadedBytes / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
                this.downloadedBytes = i;
                this.nextDownloadOffset = i;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.m10d("tmessages", "start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
            }
            if (fileNameIv != null) {
                this.cacheIvTemp = new File(this.tempPath, fileNameIv);
                try {
                    this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
                    long len = this.cacheIvTemp.length();
                    if (len <= 0 || len % 32 != 0) {
                        this.downloadedBytes = 0;
                    } else {
                        this.fiv.read(this.iv, 0, 32);
                    }
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                    this.downloadedBytes = 0;
                }
            }
            try {
                this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
                if (this.downloadedBytes != 0) {
                    this.fileOutputStream.seek((long) this.downloadedBytes);
                }
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
            if (this.fileOutputStream == null) {
                cleanup();
                Utilities.stageQueue.postRunnable(new C04685());
                return;
            }
            Utilities.stageQueue.postRunnable(new C04696());
        }
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new C04707());
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e22) {
            FileLog.m13e("tmessages", e22);
        }
        if (this.delayedRequestInfos != null) {
            for (int a = 0; a < this.delayedRequestInfos.size(); a += stateDownloading) {
                RequestInfo requestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFinishLoadingFile() throws Exception {
        if (this.state == stateDownloading) {
            this.state = stateFinished;
            cleanup();
            if (this.cacheIvTemp != null) {
                this.cacheIvTemp.delete();
                this.cacheIvTemp = null;
            }
            if (!(this.cacheFileTemp == null || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m11e("tmessages", "unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                }
                this.renameRetryCount += stateDownloading;
                if (this.renameRetryCount < stateFinished) {
                    this.state = stateDownloading;
                    Utilities.stageQueue.postRunnable(new C04718(), 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.m11e("tmessages", "finished downloading file to " + this.cacheFileFinal);
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        }
    }

    private void processRequestResult(RequestInfo requestInfo, TL_error error) {
        this.requestInfos.remove(requestInfo);
        if (error == null) {
            try {
                if (this.downloadedBytes != requestInfo.offset) {
                    if (this.state == stateDownloading) {
                        this.delayedRequestInfos.add(requestInfo);
                        requestInfo.response.disableFree = true;
                    }
                } else if (requestInfo.response.bytes == null || requestInfo.response.bytes.limit() == 0) {
                    onFinishLoadingFile();
                } else {
                    int currentBytesSize = requestInfo.response.bytes.limit();
                    this.downloadedBytes += currentBytesSize;
                    boolean finishedDownloading = currentBytesSize != this.currentDownloadChunkSize || ((this.totalBytesCount == this.downloadedBytes || this.downloadedBytes % this.currentDownloadChunkSize != 0) && (this.totalBytesCount <= 0 || this.totalBytesCount <= this.downloadedBytes));
                    if (this.key != null) {
                        Utilities.aesIgeEncryption(requestInfo.response.bytes.buffer, this.key, this.iv, false, true, 0, requestInfo.response.bytes.limit());
                        if (finishedDownloading && this.bytesCountPadding != 0) {
                            requestInfo.response.bytes.limit(requestInfo.response.bytes.limit() - this.bytesCountPadding);
                        }
                    }
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.getChannel().write(requestInfo.response.bytes.buffer);
                    }
                    if (this.fiv != null) {
                        this.fiv.seek(0);
                        this.fiv.write(this.iv);
                    }
                    if (this.totalBytesCount > 0 && this.state == stateDownloading) {
                        this.delegate.didChangedLoadProgress(this, Math.min(TouchHelperCallback.ALPHA_FULL, ((float) this.downloadedBytes) / ((float) this.totalBytesCount)));
                    }
                    for (int a = 0; a < this.delayedRequestInfos.size(); a += stateDownloading) {
                        RequestInfo delayedRequestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                        if (this.downloadedBytes == delayedRequestInfo.offset) {
                            this.delayedRequestInfos.remove(a);
                            processRequestResult(delayedRequestInfo, null);
                            delayedRequestInfo.response.disableFree = false;
                            delayedRequestInfo.response.freeResources();
                            break;
                        }
                    }
                    if (finishedDownloading) {
                        onFinishLoadingFile();
                    } else {
                        startDownloadRequest();
                    }
                }
            } catch (Throwable e) {
                cleanup();
                this.delegate.didFailedLoadingFile(this, 0);
                FileLog.m13e("tmessages", e);
            }
        } else if (error.text.contains("FILE_MIGRATE_")) {
            Integer val;
            Scanner scanner = new Scanner(error.text.replace("FILE_MIGRATE_", TtmlNode.ANONYMOUS_REGION_ID));
            scanner.useDelimiter(TtmlNode.ANONYMOUS_REGION_ID);
            try {
                val = Integer.valueOf(scanner.nextInt());
            } catch (Exception e2) {
                val = null;
            }
            if (val == null) {
                cleanup();
                this.delegate.didFailedLoadingFile(this, 0);
                return;
            }
            this.datacenter_id = val.intValue();
            this.nextDownloadOffset = 0;
            startDownloadRequest();
        } else if (error.text.contains("OFFSET_INVALID")) {
            if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
                try {
                    onFinishLoadingFile();
                    return;
                } catch (Throwable e3) {
                    FileLog.m13e("tmessages", e3);
                    cleanup();
                    this.delegate.didFailedLoadingFile(this, 0);
                    return;
                }
            }
            cleanup();
            this.delegate.didFailedLoadingFile(this, 0);
        } else if (error.text.contains("RETRY_LIMIT")) {
            cleanup();
            this.delegate.didFailedLoadingFile(this, stateFailed);
        } else {
            if (this.location != null) {
                FileLog.m11e("tmessages", TtmlNode.ANONYMOUS_REGION_ID + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
            }
            cleanup();
            this.delegate.didFailedLoadingFile(this, 0);
        }
    }

    private void startDownloadRequest() {
        if (this.state != stateDownloading) {
            return;
        }
        if ((this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) && this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) {
            int count = stateDownloading;
            if (this.totalBytesCount > 0) {
                count = Math.max(0, (this.currentMaxDownloadRequests - this.requestInfos.size()) - this.delayedRequestInfos.size());
            }
            int a = 0;
            while (a < count) {
                if (this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) {
                    boolean isLast;
                    int i;
                    if (this.totalBytesCount <= 0 || a == count - 1 || (this.totalBytesCount > 0 && this.nextDownloadOffset + this.currentDownloadChunkSize >= this.totalBytesCount)) {
                        isLast = true;
                    } else {
                        isLast = false;
                    }
                    TL_upload_getFile req = new TL_upload_getFile();
                    req.location = this.location;
                    req.offset = this.nextDownloadOffset;
                    req.limit = this.currentDownloadChunkSize;
                    this.nextDownloadOffset += this.currentDownloadChunkSize;
                    RequestInfo requestInfo = new RequestInfo();
                    this.requestInfos.add(requestInfo);
                    requestInfo.offset = req.offset;
                    ConnectionsManager instance = ConnectionsManager.getInstance();
                    RequestDelegate c16789 = new C16789(requestInfo);
                    if (this.isForceRequest) {
                        i = 32;
                    } else {
                        i = 0;
                    }
                    requestInfo.requestToken = instance.sendRequest(req, c16789, null, i | stateFailed, this.datacenter_id, this.requestsCount % stateFailed == 0 ? stateFailed : ConnectionsManager.ConnectionTypeDownload2, isLast);
                    this.requestsCount += stateDownloading;
                    a += stateDownloading;
                } else {
                    return;
                }
            }
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate) {
        this.delegate = delegate;
    }
}
