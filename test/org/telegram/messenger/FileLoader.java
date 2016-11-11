package org.telegram.messenger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.FileLoadOperation.FileLoadOperationDelegate;
import org.telegram.messenger.FileUploadOperation.FileUploadOperationDelegate;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;

public class FileLoader {
    private static volatile FileLoader Instance = null;
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_VIDEO = 2;
    private LinkedList<FileLoadOperation> audioLoadOperationQueue;
    private int currentAudioLoadOperationsCount;
    private int currentLoadOperationsCount;
    private int currentPhotoLoadOperationsCount;
    private int currentUploadOperationsCount;
    private int currentUploadSmallOperationsCount;
    private FileLoaderDelegate delegate;
    private volatile DispatchQueue fileLoaderQueue;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths;
    private LinkedList<FileLoadOperation> loadOperationQueue;
    private HashMap<Integer, File> mediaDirs;
    private LinkedList<FileLoadOperation> photoLoadOperationQueue;
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths;
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc;
    private LinkedList<FileUploadOperation> uploadOperationQueue;
    private HashMap<String, Long> uploadSizes;
    private LinkedList<FileUploadOperation> uploadSmallOperationQueue;

    /* renamed from: org.telegram.messenger.FileLoader.1 */
    class C04721 implements Runnable {
        final /* synthetic */ boolean val$enc;
        final /* synthetic */ String val$location;

        C04721(boolean z, String str) {
            this.val$enc = z;
            this.val$location = str;
        }

        public void run() {
            FileUploadOperation operation;
            if (this.val$enc) {
                operation = (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(this.val$location);
            } else {
                operation = (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(this.val$location);
            }
            FileLoader.this.uploadSizes.remove(this.val$location);
            if (operation != null) {
                FileLoader.this.uploadOperationPathsEnc.remove(this.val$location);
                FileLoader.this.uploadOperationQueue.remove(operation);
                FileLoader.this.uploadSmallOperationQueue.remove(operation);
                operation.cancel();
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.2 */
    class C04732 implements Runnable {
        final /* synthetic */ boolean val$encrypted;
        final /* synthetic */ long val$finalSize;
        final /* synthetic */ String val$location;

        C04732(boolean z, String str, long j) {
            this.val$encrypted = z;
            this.val$location = str;
            this.val$finalSize = j;
        }

        public void run() {
            FileUploadOperation operation;
            if (this.val$encrypted) {
                operation = (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(this.val$location);
            } else {
                operation = (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(this.val$location);
            }
            if (operation != null) {
                operation.checkNewDataAvailable(this.val$finalSize);
            } else if (this.val$finalSize != 0) {
                FileLoader.this.uploadSizes.put(this.val$location, Long.valueOf(this.val$finalSize));
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.3 */
    class C04763 implements Runnable {
        final /* synthetic */ boolean val$encrypted;
        final /* synthetic */ int val$estimatedSize;
        final /* synthetic */ String val$location;
        final /* synthetic */ boolean val$small;

        /* renamed from: org.telegram.messenger.FileLoader.3.1 */
        class C16791 implements FileUploadOperationDelegate {

            /* renamed from: org.telegram.messenger.FileLoader.3.1.1 */
            class C04741 implements Runnable {
                final /* synthetic */ InputEncryptedFile val$inputEncryptedFile;
                final /* synthetic */ InputFile val$inputFile;
                final /* synthetic */ byte[] val$iv;
                final /* synthetic */ byte[] val$key;
                final /* synthetic */ FileUploadOperation val$operation;

                C04741(InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
                    this.val$inputFile = inputFile;
                    this.val$inputEncryptedFile = inputEncryptedFile;
                    this.val$key = bArr;
                    this.val$iv = bArr2;
                    this.val$operation = fileUploadOperation;
                }

                public void run() {
                    if (C04763.this.val$encrypted) {
                        FileLoader.this.uploadOperationPathsEnc.remove(C04763.this.val$location);
                    } else {
                        FileLoader.this.uploadOperationPaths.remove(C04763.this.val$location);
                    }
                    FileUploadOperation operation;
                    if (C04763.this.val$small) {
                        FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                        if (FileLoader.this.currentUploadSmallOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                            operation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                            if (operation != null) {
                                FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                                operation.start();
                            }
                        }
                    } else {
                        FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                        if (FileLoader.this.currentUploadOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                            operation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                            if (operation != null) {
                                FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                                operation.start();
                            }
                        }
                    }
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidUploaded(C04763.this.val$location, this.val$inputFile, this.val$inputEncryptedFile, this.val$key, this.val$iv, this.val$operation.getTotalFileSize());
                    }
                }
            }

            /* renamed from: org.telegram.messenger.FileLoader.3.1.2 */
            class C04752 implements Runnable {
                C04752() {
                }

                public void run() {
                    if (C04763.this.val$encrypted) {
                        FileLoader.this.uploadOperationPathsEnc.remove(C04763.this.val$location);
                    } else {
                        FileLoader.this.uploadOperationPaths.remove(C04763.this.val$location);
                    }
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidFailedUpload(C04763.this.val$location, C04763.this.val$encrypted);
                    }
                    FileUploadOperation operation;
                    if (C04763.this.val$small) {
                        FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                        if (FileLoader.this.currentUploadSmallOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                            operation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                            if (operation != null) {
                                FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                                operation.start();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                    if (FileLoader.this.currentUploadOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                        operation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                        if (operation != null) {
                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            operation.start();
                        }
                    }
                }
            }

            C16791() {
            }

            public void didFinishUploadingFile(FileUploadOperation operation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv) {
                FileLoader.this.fileLoaderQueue.postRunnable(new C04741(inputFile, inputEncryptedFile, key, iv, operation));
            }

            public void didFailedUploadingFile(FileUploadOperation operation) {
                FileLoader.this.fileLoaderQueue.postRunnable(new C04752());
            }

            public void didChangedUploadProgress(FileUploadOperation operation, float progress) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(C04763.this.val$location, progress, C04763.this.val$encrypted);
                }
            }
        }

        C04763(boolean z, String str, int i, boolean z2) {
            this.val$encrypted = z;
            this.val$location = str;
            this.val$estimatedSize = i;
            this.val$small = z2;
        }

        public void run() {
            if (this.val$encrypted) {
                if (FileLoader.this.uploadOperationPathsEnc.containsKey(this.val$location)) {
                    return;
                }
            } else if (FileLoader.this.uploadOperationPaths.containsKey(this.val$location)) {
                return;
            }
            int esimated = this.val$estimatedSize;
            if (!(esimated == 0 || ((Long) FileLoader.this.uploadSizes.get(this.val$location)) == null)) {
                esimated = FileLoader.MEDIA_DIR_IMAGE;
                FileLoader.this.uploadSizes.remove(this.val$location);
            }
            FileUploadOperation operation = new FileUploadOperation(this.val$location, this.val$encrypted, esimated);
            if (this.val$encrypted) {
                FileLoader.this.uploadOperationPathsEnc.put(this.val$location, operation);
            } else {
                FileLoader.this.uploadOperationPaths.put(this.val$location, operation);
            }
            operation.delegate = new C16791();
            if (this.val$small) {
                if (FileLoader.this.currentUploadSmallOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                    operation.start();
                    return;
                }
                FileLoader.this.uploadSmallOperationQueue.add(operation);
            } else if (FileLoader.this.currentUploadOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                operation.start();
            } else {
                FileLoader.this.uploadOperationQueue.add(operation);
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.4 */
    class C04774 implements Runnable {
        final /* synthetic */ Document val$document;
        final /* synthetic */ FileLocation val$location;
        final /* synthetic */ String val$locationExt;

        C04774(FileLocation fileLocation, String str, Document document) {
            this.val$location = fileLocation;
            this.val$locationExt = str;
            this.val$document = document;
        }

        public void run() {
            String fileName = null;
            if (this.val$location != null) {
                fileName = FileLoader.getAttachFileName(this.val$location, this.val$locationExt);
            } else if (this.val$document != null) {
                fileName = FileLoader.getAttachFileName(this.val$document);
            }
            if (fileName != null) {
                FileLoadOperation operation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(fileName);
                if (operation != null) {
                    if (MessageObject.isVoiceDocument(this.val$document)) {
                        FileLoader.this.audioLoadOperationQueue.remove(operation);
                    } else if (this.val$location != null) {
                        FileLoader.this.photoLoadOperationQueue.remove(operation);
                    } else {
                        FileLoader.this.loadOperationQueue.remove(operation);
                    }
                    operation.cancel();
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.5 */
    class C04785 implements Runnable {
        final /* synthetic */ String val$fileName;
        final /* synthetic */ Boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        C04785(Boolean[] boolArr, String str, Semaphore semaphore) {
            this.val$result = boolArr;
            this.val$fileName = str;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$result[FileLoader.MEDIA_DIR_IMAGE] = Boolean.valueOf(FileLoader.this.loadOperationPaths.containsKey(this.val$fileName));
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.6 */
    class C04796 implements Runnable {
        final /* synthetic */ boolean val$cacheOnly;
        final /* synthetic */ Document val$document;
        final /* synthetic */ boolean val$force;
        final /* synthetic */ FileLocation val$location;
        final /* synthetic */ String val$locationExt;
        final /* synthetic */ int val$locationSize;

        /* renamed from: org.telegram.messenger.FileLoader.6.1 */
        class C16801 implements FileLoadOperationDelegate {
            final /* synthetic */ String val$finalFileName;
            final /* synthetic */ int val$finalType;

            C16801(String str, int i) {
                this.val$finalFileName = str;
                this.val$finalType = i;
            }

            public void didFinishLoadingFile(FileLoadOperation operation, File finalFile) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidLoaded(this.val$finalFileName, finalFile, this.val$finalType);
                }
                FileLoader.this.checkDownloadQueue(C04796.this.val$document, C04796.this.val$location, this.val$finalFileName);
            }

            public void didFailedLoadingFile(FileLoadOperation operation, int canceled) {
                FileLoader.this.checkDownloadQueue(C04796.this.val$document, C04796.this.val$location, this.val$finalFileName);
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedLoad(this.val$finalFileName, canceled);
                }
            }

            public void didChangedLoadProgress(FileLoadOperation operation, float progress) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileLoadProgressChanged(this.val$finalFileName, progress);
                }
            }
        }

        C04796(FileLocation fileLocation, String str, Document document, boolean z, int i, boolean z2) {
            this.val$location = fileLocation;
            this.val$locationExt = str;
            this.val$document = document;
            this.val$force = z;
            this.val$locationSize = i;
            this.val$cacheOnly = z2;
        }

        public void run() {
            String fileName = null;
            if (this.val$location != null) {
                fileName = FileLoader.getAttachFileName(this.val$location, this.val$locationExt);
            } else if (this.val$document != null) {
                fileName = FileLoader.getAttachFileName(this.val$document);
            }
            if (fileName != null && !fileName.contains("-2147483648")) {
                FileLoadOperation operation = (FileLoadOperation) FileLoader.this.loadOperationPaths.get(fileName);
                if (operation == null) {
                    int maxCount;
                    File tempDir = FileLoader.this.getDirectory(FileLoader.MEDIA_DIR_CACHE);
                    File storeDir = tempDir;
                    int type = FileLoader.MEDIA_DIR_CACHE;
                    if (this.val$location != null) {
                        operation = new FileLoadOperation(this.val$location, this.val$locationExt, this.val$locationSize);
                        type = FileLoader.MEDIA_DIR_IMAGE;
                    } else if (this.val$document != null) {
                        operation = new FileLoadOperation(this.val$document);
                        if (MessageObject.isVoiceDocument(this.val$document)) {
                            type = FileLoader.MEDIA_DIR_AUDIO;
                        } else if (MessageObject.isVideoDocument(this.val$document)) {
                            type = FileLoader.MEDIA_DIR_VIDEO;
                        } else {
                            type = FileLoader.MEDIA_DIR_DOCUMENT;
                        }
                    }
                    if (!this.val$cacheOnly) {
                        storeDir = FileLoader.this.getDirectory(type);
                    }
                    operation.setPaths(storeDir, tempDir);
                    String finalFileName = fileName;
                    int finalType = type;
                    FileLoader.this.loadOperationPaths.put(fileName, operation);
                    operation.setDelegate(new C16801(finalFileName, finalType));
                    if (this.val$force) {
                        maxCount = FileLoader.MEDIA_DIR_DOCUMENT;
                    } else {
                        maxCount = FileLoader.MEDIA_DIR_AUDIO;
                    }
                    if (type == FileLoader.MEDIA_DIR_AUDIO) {
                        if (FileLoader.this.currentAudioLoadOperationsCount < maxCount) {
                            FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            operation.start();
                        } else if (this.val$force) {
                            FileLoader.this.audioLoadOperationQueue.add(FileLoader.MEDIA_DIR_IMAGE, operation);
                        } else {
                            FileLoader.this.audioLoadOperationQueue.add(operation);
                        }
                    } else if (this.val$location != null) {
                        if (FileLoader.this.currentPhotoLoadOperationsCount < maxCount) {
                            FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            operation.start();
                        } else if (this.val$force) {
                            FileLoader.this.photoLoadOperationQueue.add(FileLoader.MEDIA_DIR_IMAGE, operation);
                        } else {
                            FileLoader.this.photoLoadOperationQueue.add(operation);
                        }
                    } else if (FileLoader.this.currentLoadOperationsCount < maxCount) {
                        FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                        operation.start();
                    } else if (this.val$force) {
                        FileLoader.this.loadOperationQueue.add(FileLoader.MEDIA_DIR_IMAGE, operation);
                    } else {
                        FileLoader.this.loadOperationQueue.add(operation);
                    }
                } else if (this.val$force) {
                    LinkedList<FileLoadOperation> downloadQueue;
                    if (MessageObject.isVoiceDocument(this.val$document)) {
                        downloadQueue = FileLoader.this.audioLoadOperationQueue;
                    } else if (this.val$location != null) {
                        downloadQueue = FileLoader.this.photoLoadOperationQueue;
                    } else {
                        downloadQueue = FileLoader.this.loadOperationQueue;
                    }
                    if (downloadQueue != null) {
                        int index = downloadQueue.indexOf(operation);
                        if (index != -1) {
                            downloadQueue.remove(index);
                            downloadQueue.add(FileLoader.MEDIA_DIR_IMAGE, operation);
                            operation.setForceRequest(true);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.7 */
    class C04807 implements Runnable {
        final /* synthetic */ String val$arg1;
        final /* synthetic */ Document val$document;
        final /* synthetic */ FileLocation val$location;

        C04807(String str, Document document, FileLocation fileLocation) {
            this.val$arg1 = str;
            this.val$document = document;
            this.val$location = fileLocation;
        }

        public void run() {
            int maxCount = FileLoader.MEDIA_DIR_DOCUMENT;
            FileLoader.this.loadOperationPaths.remove(this.val$arg1);
            FileLoadOperation operation;
            if (MessageObject.isVoiceDocument(this.val$document)) {
                FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                if (!FileLoader.this.audioLoadOperationQueue.isEmpty()) {
                    if (!((FileLoadOperation) FileLoader.this.audioLoadOperationQueue.get(FileLoader.MEDIA_DIR_IMAGE)).isForceRequest()) {
                        maxCount = FileLoader.MEDIA_DIR_AUDIO;
                    }
                    if (FileLoader.this.currentAudioLoadOperationsCount < maxCount) {
                        operation = (FileLoadOperation) FileLoader.this.audioLoadOperationQueue.poll();
                        if (operation != null) {
                            this.this$0.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            operation.start();
                        }
                    }
                }
            } else if (this.val$location != null) {
                FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                if (!FileLoader.this.photoLoadOperationQueue.isEmpty()) {
                    if (!((FileLoadOperation) FileLoader.this.photoLoadOperationQueue.get(FileLoader.MEDIA_DIR_IMAGE)).isForceRequest()) {
                        maxCount = FileLoader.MEDIA_DIR_AUDIO;
                    }
                    if (FileLoader.this.currentPhotoLoadOperationsCount < maxCount) {
                        operation = (FileLoadOperation) FileLoader.this.photoLoadOperationQueue.poll();
                        if (operation != null) {
                            this.this$0.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            operation.start();
                        }
                    }
                }
            } else {
                FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                if (!FileLoader.this.loadOperationQueue.isEmpty()) {
                    if (!((FileLoadOperation) FileLoader.this.loadOperationQueue.get(FileLoader.MEDIA_DIR_IMAGE)).isForceRequest()) {
                        maxCount = FileLoader.MEDIA_DIR_AUDIO;
                    }
                    if (FileLoader.this.currentLoadOperationsCount < maxCount) {
                        operation = (FileLoadOperation) FileLoader.this.loadOperationQueue.poll();
                        if (operation != null) {
                            this.this$0.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            operation.start();
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileLoader.8 */
    class C04818 implements Runnable {
        final /* synthetic */ ArrayList val$files;
        final /* synthetic */ int val$type;

        C04818(ArrayList arrayList, int i) {
            this.val$files = arrayList;
            this.val$type = i;
        }

        public void run() {
            for (int a = FileLoader.MEDIA_DIR_IMAGE; a < this.val$files.size(); a += FileLoader.MEDIA_DIR_AUDIO) {
                File file = (File) this.val$files.get(a);
                if (file.exists()) {
                    try {
                        if (!file.delete()) {
                            file.deleteOnExit();
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
                try {
                    File qFile = new File(file.getParentFile(), "q_" + file.getName());
                    if (qFile.exists() && !qFile.delete()) {
                        qFile.deleteOnExit();
                    }
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
            if (this.val$type == FileLoader.MEDIA_DIR_VIDEO) {
                ImageLoader.getInstance().clearMemory();
            }
        }
    }

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, int i);

        void fileDidUploaded(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(String str, float f);

        void fileUploadProgressChanged(String str, float f, boolean z);
    }

    public FileLoader() {
        this.mediaDirs = null;
        this.fileLoaderQueue = new DispatchQueue("fileUploadQueue");
        this.uploadOperationQueue = new LinkedList();
        this.uploadSmallOperationQueue = new LinkedList();
        this.loadOperationQueue = new LinkedList();
        this.audioLoadOperationQueue = new LinkedList();
        this.photoLoadOperationQueue = new LinkedList();
        this.uploadOperationPaths = new ConcurrentHashMap();
        this.uploadOperationPathsEnc = new ConcurrentHashMap();
        this.loadOperationPaths = new ConcurrentHashMap();
        this.uploadSizes = new HashMap();
        this.delegate = null;
        this.currentLoadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentAudioLoadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentPhotoLoadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentUploadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentUploadSmallOperationsCount = MEDIA_DIR_IMAGE;
    }

    static {
        Instance = null;
    }

    public static FileLoader getInstance() {
        FileLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        FileLoader localInstance2 = new FileLoader();
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

    public void setMediaDirs(HashMap<Integer, File> dirs) {
        this.mediaDirs = dirs;
    }

    public File checkDirectory(int type) {
        return (File) this.mediaDirs.get(Integer.valueOf(type));
    }

    public File getDirectory(int type) {
        File dir = (File) this.mediaDirs.get(Integer.valueOf(type));
        if (dir == null && type != MEDIA_DIR_CACHE) {
            dir = (File) this.mediaDirs.get(Integer.valueOf(MEDIA_DIR_CACHE));
        }
        try {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
        return dir;
    }

    public void cancelUploadFile(String location, boolean enc) {
        this.fileLoaderQueue.postRunnable(new C04721(enc, location));
    }

    public void checkUploadNewDataAvailable(String location, boolean encrypted, long finalSize) {
        this.fileLoaderQueue.postRunnable(new C04732(encrypted, location, finalSize));
    }

    public void uploadFile(String location, boolean encrypted, boolean small) {
        uploadFile(location, encrypted, small, MEDIA_DIR_IMAGE);
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int estimatedSize) {
        if (location != null) {
            this.fileLoaderQueue.postRunnable(new C04763(encrypted, location, estimatedSize, small));
        }
    }

    public void cancelLoadFile(Document document) {
        cancelLoadFile(document, null, null);
    }

    public void cancelLoadFile(PhotoSize photo) {
        cancelLoadFile(null, photo.location, null);
    }

    public void cancelLoadFile(FileLocation location, String ext) {
        cancelLoadFile(null, location, ext);
    }

    private void cancelLoadFile(Document document, FileLocation location, String locationExt) {
        if (location != null || document != null) {
            this.fileLoaderQueue.postRunnable(new C04774(location, locationExt, document));
        }
    }

    public boolean isLoadingFile(String fileName) {
        Semaphore semaphore = new Semaphore(MEDIA_DIR_IMAGE);
        Boolean[] result = new Boolean[MEDIA_DIR_AUDIO];
        this.fileLoaderQueue.postRunnable(new C04785(result, fileName, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        return result[MEDIA_DIR_IMAGE].booleanValue();
    }

    public void loadFile(PhotoSize photo, String ext, boolean cacheOnly) {
        boolean z;
        FileLocation fileLocation = photo.location;
        int i = photo.size;
        if (cacheOnly || ((photo != null && photo.size == 0) || photo.location.key != null)) {
            z = true;
        } else {
            z = false;
        }
        loadFile(null, fileLocation, ext, i, false, z);
    }

    public void loadFile(Document document, boolean force, boolean cacheOnly) {
        boolean z;
        if (cacheOnly || !(document == null || document.key == null)) {
            z = true;
        } else {
            z = false;
        }
        loadFile(document, null, null, MEDIA_DIR_IMAGE, force, z);
    }

    public void loadFile(FileLocation location, String ext, int size, boolean cacheOnly) {
        boolean z = cacheOnly || size == 0 || !(location == null || location.key == null);
        loadFile(null, location, ext, size, true, z);
    }

    private void loadFile(Document document, FileLocation location, String locationExt, int locationSize, boolean force, boolean cacheOnly) {
        this.fileLoaderQueue.postRunnable(new C04796(location, locationExt, document, force, locationSize, cacheOnly));
    }

    private void checkDownloadQueue(Document document, FileLocation location, String arg1) {
        this.fileLoaderQueue.postRunnable(new C04807(arg1, document, location));
    }

    public void setDelegate(FileLoaderDelegate delegate) {
        this.delegate = delegate;
    }

    public static String getMessageFileName(Message message) {
        if (message == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        ArrayList<PhotoSize> sizes;
        PhotoSize sizeFull;
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                sizes = message.action.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                sizes = message.media.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.photo != null) {
                    sizes = message.media.webpage.photo.sizes;
                    if (sizes.size() > 0) {
                        sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                        if (sizeFull != null) {
                            return getAttachFileName(sizeFull);
                        }
                    }
                } else if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static File getPathToMessage(Message message) {
        if (message == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        ArrayList<PhotoSize> sizes;
        PhotoSize sizeFull;
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                sizes = message.action.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getPathToAttach(sizeFull);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            return getPathToAttach(message.media.document);
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                sizes = message.media.photo.sizes;
                if (sizes.size() > 0) {
                    sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getPathToAttach(sizeFull);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getPathToAttach(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    sizes = message.media.webpage.photo.sizes;
                    if (sizes.size() > 0) {
                        sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                        if (sizeFull != null) {
                            return getPathToAttach(sizeFull);
                        }
                    }
                }
            }
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, null, false);
    }

    public static File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, null, forceCache);
    }

    public static File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        File dir = null;
        if (forceCache) {
            dir = getInstance().getDirectory(MEDIA_DIR_CACHE);
        } else if (attach instanceof Document) {
            Document document = (Document) attach;
            if (document.key != null) {
                dir = getInstance().getDirectory(MEDIA_DIR_CACHE);
            } else if (MessageObject.isVoiceDocument(document)) {
                dir = getInstance().getDirectory(MEDIA_DIR_AUDIO);
            } else if (MessageObject.isVideoDocument(document)) {
                dir = getInstance().getDirectory(MEDIA_DIR_VIDEO);
            } else {
                dir = getInstance().getDirectory(MEDIA_DIR_DOCUMENT);
            }
        } else if (attach instanceof PhotoSize) {
            PhotoSize photoSize = (PhotoSize) attach;
            if (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) {
                dir = getInstance().getDirectory(MEDIA_DIR_CACHE);
            } else {
                dir = getInstance().getDirectory(MEDIA_DIR_IMAGE);
            }
        } else if (attach instanceof FileLocation) {
            FileLocation fileLocation = (FileLocation) attach;
            if (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) {
                dir = getInstance().getDirectory(MEDIA_DIR_CACHE);
            } else {
                dir = getInstance().getDirectory(MEDIA_DIR_IMAGE);
            }
        }
        if (dir == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> sizes, int side) {
        return getClosestPhotoSizeWithSize(sizes, side, false);
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> sizes, int side, boolean byMinSide) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = MEDIA_DIR_IMAGE;
        PhotoSize closestObject = null;
        for (int a = MEDIA_DIR_IMAGE; a < sizes.size(); a += MEDIA_DIR_AUDIO) {
            PhotoSize obj = (PhotoSize) sizes.get(a);
            if (obj != null) {
                int currentSide;
                if (byMinSide) {
                    currentSide = obj.f33h >= obj.f34w ? obj.f34w : obj.f33h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == LinearLayoutManager.INVALID_OFFSET) || (obj instanceof TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                } else {
                    currentSide = obj.f34w >= obj.f33h ? obj.f34w : obj.f33h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == LinearLayoutManager.INVALID_OFFSET) || (obj instanceof TL_photoCachedSize) || (currentSide <= side && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                }
            }
        }
        return closestObject;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + MEDIA_DIR_AUDIO);
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static String getDocumentFileName(Document document) {
        if (document != null) {
            if (document.file_name != null) {
                return document.file_name;
            }
            for (int a = MEDIA_DIR_IMAGE; a < document.attributes.size(); a += MEDIA_DIR_AUDIO) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(a);
                if (documentAttribute instanceof TL_documentAttributeFilename) {
                    return documentAttribute.file_name;
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static String getDocumentExtension(Document document) {
        String fileName = getDocumentFileName(document);
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + MEDIA_DIR_AUDIO);
        }
        if (ext == null || ext.length() == 0) {
            ext = document.mime_type;
        }
        if (ext == null) {
            ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return ext.toUpperCase();
    }

    public static String getAttachFileName(TLObject attach) {
        return getAttachFileName(attach, null);
    }

    public static String getAttachFileName(TLObject attach, String ext) {
        Object obj = -1;
        if (attach instanceof Document) {
            Document document = (Document) attach;
            String docExt = null;
            if (MEDIA_DIR_IMAGE == null) {
                docExt = getDocumentFileName(document);
                if (docExt != null) {
                    int idx = docExt.lastIndexOf(46);
                    if (idx != -1) {
                        docExt = docExt.substring(idx);
                    }
                }
                docExt = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (docExt.length() <= MEDIA_DIR_AUDIO) {
                if (document.mime_type != null) {
                    String str = document.mime_type;
                    switch (str.hashCode()) {
                        case 187091926:
                            if (str.equals("audio/ogg")) {
                                obj = MEDIA_DIR_AUDIO;
                                break;
                            }
                            break;
                        case 1331848029:
                            if (str.equals(MimeTypes.VIDEO_MP4)) {
                                obj = null;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case MEDIA_DIR_IMAGE /*0*/:
                            docExt = ".mp4";
                            break;
                        case MEDIA_DIR_AUDIO /*1*/:
                            docExt = ".ogg";
                            break;
                        default:
                            docExt = TtmlNode.ANONYMOUS_REGION_ID;
                            break;
                    }
                }
                docExt = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (docExt.length() > MEDIA_DIR_AUDIO) {
                return document.dc_id + "_" + document.id + docExt;
            }
            return document.dc_id + "_" + document.id;
        } else if (attach instanceof PhotoSize) {
            PhotoSize photo = (PhotoSize) attach;
            if (photo.location == null || (photo.location instanceof TL_fileLocationUnavailable)) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            r5 = new StringBuilder().append(photo.location.volume_id).append("_").append(photo.location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return r5.append(ext).toString();
        } else if (!(attach instanceof FileLocation)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            if (attach instanceof TL_fileLocationUnavailable) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            FileLocation location = (FileLocation) attach;
            r5 = new StringBuilder().append(location.volume_id).append("_").append(location.local_id).append(".");
            if (ext == null) {
                ext = "jpg";
            }
            return r5.append(ext).toString();
        }
    }

    public void deleteFiles(ArrayList<File> files, int type) {
        if (files != null && !files.isEmpty()) {
            this.fileLoaderQueue.postRunnable(new C04818(files, type));
        }
    }
}
