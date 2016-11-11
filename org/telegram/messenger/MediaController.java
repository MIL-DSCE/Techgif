package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.PhotoViewer;

public class MediaController implements OnAudioFocusChangeListener, NotificationCenterDelegate, SensorEventListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
    public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_MASK_GIF = 32;
    public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
    public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
    public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
    private static volatile MediaController Instance = null;
    public static final String MIME_TYPE = "video/avc";
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    private static final float VOLUME_DUCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    public static AlbumEntry allPhotosAlbumEntry;
    private static final String[] projectionPhotos;
    private static final String[] projectionVideo;
    public static int[] readArgs;
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private HashMap<String, FileDownloadProgressListener> addLaterArray;
    private boolean allowStartRecord;
    private ArrayList<DownloadObject> audioDownloadQueue;
    private int audioFocus;
    private AudioInfo audioInfo;
    private MediaPlayer audioPlayer;
    private AudioRecord audioRecorder;
    private AudioTrack audioTrackPlayer;
    private boolean autoplayGifs;
    private int buffersWrited;
    private boolean callInProgress;
    private boolean cancelCurrentVideoConversion;
    private int countLess;
    private int currentPlaylistNum;
    private long currentTotalPcmDuration;
    private boolean customTabs;
    private boolean decodingFinished;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray;
    private boolean directShare;
    private ArrayList<DownloadObject> documentDownloadQueue;
    private HashMap<String, DownloadObject> downloadQueueKeys;
    private boolean downloadingCurrentMessage;
    private ExternalObserver externalObserver;
    private ByteBuffer fileBuffer;
    private DispatchQueue fileDecodingQueue;
    private DispatchQueue fileEncodingQueue;
    private boolean forceLoopCurrentPlaylist;
    private ArrayList<AudioBuffer> freePlayerBuffers;
    private HashMap<String, MessageObject> generatingWaveform;
    private ArrayList<DownloadObject> gifDownloadQueue;
    private float[] gravity;
    private float[] gravityFast;
    private Sensor gravitySensor;
    private int hasAudioFocus;
    private int ignoreFirstProgress;
    private boolean ignoreOnPause;
    private boolean ignoreProximity;
    private boolean inputFieldHasText;
    private InternalObserver internalObserver;
    private boolean isPaused;
    private int lastCheckMask;
    private long lastMediaCheckTime;
    private long lastPlayPcm;
    private int lastProgress;
    private float lastProximityValue;
    private EncryptedChat lastSecretChat;
    private long lastSecretChatEnterTime;
    private long lastSecretChatLeaveTime;
    private ArrayList<Long> lastSecretChatVisibleMessages;
    private int lastTag;
    private long lastTimestamp;
    private float[] linearAcceleration;
    private Sensor linearSensor;
    private boolean listenerInProgress;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers;
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers;
    private String[] mediaProjections;
    public int mobileDataDownloadMask;
    private ArrayList<DownloadObject> musicDownloadQueue;
    private HashMap<Integer, String> observersByTag;
    private ArrayList<DownloadObject> photoDownloadQueue;
    private boolean playMusicAgain;
    private int playerBufferSize;
    private final Object playerObjectSync;
    private DispatchQueue playerQueue;
    private final Object playerSync;
    private MessageObject playingMessageObject;
    private ArrayList<MessageObject> playlist;
    private float previousAccValue;
    private Timer progressTimer;
    private final Object progressTimerSync;
    private boolean proximityHasDifferentValues;
    private Sensor proximitySensor;
    private boolean proximityTouched;
    private WakeLock proximityWakeLock;
    private ChatActivity raiseChat;
    private boolean raiseToEarRecord;
    private boolean raiseToSpeak;
    private int raisedToBack;
    private int raisedToTop;
    private int recordBufferSize;
    private ArrayList<ByteBuffer> recordBuffers;
    private long recordDialogId;
    private DispatchQueue recordQueue;
    private MessageObject recordReplyingMessageObject;
    private Runnable recordRunnable;
    private short[] recordSamples;
    private Runnable recordStartRunnable;
    private long recordStartTime;
    private long recordTimeCount;
    private TL_document recordingAudio;
    private File recordingAudioFile;
    private Runnable refreshGalleryRunnable;
    private int repeatMode;
    private boolean resumeAudioOnFocusGain;
    public int roamingDownloadMask;
    private long samplesCount;
    private boolean saveToGallery;
    private int sendAfterDone;
    private SensorManager sensorManager;
    private boolean sensorsStarted;
    private boolean shuffleMusic;
    private ArrayList<MessageObject> shuffledPlaylist;
    private int startObserverToken;
    private StopMediaObserverRunnable stopMediaObserverRunnable;
    private final Object sync;
    private long timeSinceRaise;
    private HashMap<Long, Long> typingTimes;
    private boolean useFrontSpeaker;
    private ArrayList<AudioBuffer> usedPlayerBuffers;
    private boolean videoConvertFirstWrite;
    private ArrayList<MessageObject> videoConvertQueue;
    private final Object videoConvertSync;
    private ArrayList<DownloadObject> videoDownloadQueue;
    private final Object videoQueueSync;
    private ArrayList<MessageObject> voiceMessagesPlaylist;
    private HashMap<Integer, MessageObject> voiceMessagesPlaylistMap;
    private boolean voiceMessagesPlaylistUnread;
    public int wifiDownloadMask;

    /* renamed from: org.telegram.messenger.MediaController.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ float val$progress;

        /* renamed from: org.telegram.messenger.MediaController.12.1 */
        class C05331 implements Runnable {
            C05331() {
            }

            public void run() {
                if (!MediaController.this.isPaused) {
                    MediaController.this.ignoreFirstProgress = MediaController.PROCESSOR_TYPE_MTK;
                    MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * AnonymousClass12.this.val$progress);
                    if (MediaController.this.audioTrackPlayer != null) {
                        MediaController.this.audioTrackPlayer.play();
                    }
                    MediaController.this.lastProgress = (int) ((((float) MediaController.this.currentTotalPcmDuration) / 48.0f) * AnonymousClass12.this.val$progress);
                    MediaController.this.checkPlayerQueue();
                }
            }
        }

        AnonymousClass12(float f) {
            this.val$progress = f;
        }

        public void run() {
            MediaController.this.seekOpusFile(this.val$progress);
            synchronized (MediaController.this.playerSync) {
                MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
                MediaController.this.usedPlayerBuffers.clear();
            }
            AndroidUtilities.runOnUIThread(new C05331());
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ Boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass13(Boolean[] boolArr, File file, Semaphore semaphore) {
            this.val$result = boolArr;
            this.val$cacheFile = file;
            this.val$semaphore = semaphore;
        }

        public void run() {
            boolean z;
            Boolean[] boolArr = this.val$result;
            if (MediaController.this.openOpusFile(this.val$cacheFile.getAbsolutePath()) != 0) {
                z = true;
            } else {
                z = false;
            }
            boolArr[MediaController.PROCESSOR_TYPE_OTHER] = Boolean.valueOf(z);
            this.val$semaphore.release();
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.15 */
    class AnonymousClass15 implements OnCompletionListener {
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass15(MessageObject messageObject) {
            this.val$messageObject = messageObject;
        }

        public void onCompletion(MediaPlayer mediaPlayer) {
            if (MediaController.this.playlist.isEmpty() || MediaController.this.playlist.size() <= MediaController.PROCESSOR_TYPE_QCOM) {
                MediaController mediaController = MediaController.this;
                boolean z = this.val$messageObject != null && this.val$messageObject.isVoice();
                mediaController.cleanupPlayer(true, true, z);
                return;
            }
            MediaController.this.playNextMessage(true);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ MessageObject val$reply_to_msg;

        /* renamed from: org.telegram.messenger.MediaController.17.1 */
        class C05341 implements Runnable {
            C05341() {
            }

            public void run() {
                MediaController.this.recordStartRunnable = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[MediaController.PROCESSOR_TYPE_OTHER]);
            }
        }

        /* renamed from: org.telegram.messenger.MediaController.17.2 */
        class C05352 implements Runnable {
            C05352() {
            }

            public void run() {
                MediaController.this.recordStartRunnable = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[MediaController.PROCESSOR_TYPE_OTHER]);
            }
        }

        /* renamed from: org.telegram.messenger.MediaController.17.3 */
        class C05363 implements Runnable {
            C05363() {
            }

            public void run() {
                MediaController.this.recordStartRunnable = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[MediaController.PROCESSOR_TYPE_OTHER]);
            }
        }

        /* renamed from: org.telegram.messenger.MediaController.17.4 */
        class C05374 implements Runnable {
            C05374() {
            }

            public void run() {
                MediaController.this.recordStartRunnable = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[MediaController.PROCESSOR_TYPE_OTHER]);
            }
        }

        AnonymousClass17(long j, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            if (MediaController.this.audioRecorder != null) {
                AndroidUtilities.runOnUIThread(new C05341());
                return;
            }
            MediaController.this.recordingAudio = new TL_document();
            MediaController.this.recordingAudio.dc_id = LinearLayoutManager.INVALID_OFFSET;
            MediaController.this.recordingAudio.id = (long) UserConfig.lastLocalId;
            MediaController.this.recordingAudio.user_id = UserConfig.getClientUserId();
            MediaController.this.recordingAudio.mime_type = "audio/ogg";
            MediaController.this.recordingAudio.thumb = new TL_photoSizeEmpty();
            MediaController.this.recordingAudio.thumb.type = "s";
            UserConfig.lastLocalId--;
            UserConfig.saveConfig(false);
            MediaController.this.recordingAudioFile = new File(FileLoader.getInstance().getDirectory(MediaController.PROCESSOR_TYPE_SEC), FileLoader.getAttachFileName(MediaController.this.recordingAudio));
            try {
                if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0) {
                    AndroidUtilities.runOnUIThread(new C05352());
                    return;
                }
                MediaController.this.audioRecorder = new AudioRecord(MediaController.PROCESSOR_TYPE_QCOM, 16000, MediaController.AUTODOWNLOAD_MASK_MUSIC, MediaController.PROCESSOR_TYPE_INTEL, MediaController.this.recordBufferSize * 10);
                MediaController.this.recordStartTime = System.currentTimeMillis();
                MediaController.this.recordTimeCount = 0;
                MediaController.this.samplesCount = 0;
                MediaController.this.recordDialogId = this.val$dialog_id;
                MediaController.this.recordReplyingMessageObject = this.val$reply_to_msg;
                MediaController.this.fileBuffer.rewind();
                MediaController.this.audioRecorder.startRecording();
                MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                AndroidUtilities.runOnUIThread(new C05374());
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                MediaController.this.recordingAudio = null;
                MediaController.this.stopRecord();
                MediaController.this.recordingAudioFile.delete();
                MediaController.this.recordingAudioFile = null;
                try {
                    MediaController.this.audioRecorder.release();
                    MediaController.this.audioRecorder = null;
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                AndroidUtilities.runOnUIThread(new C05363());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ String val$id;
        final /* synthetic */ String val$path;

        /* renamed from: org.telegram.messenger.MediaController.18.1 */
        class C05381 implements Runnable {
            final /* synthetic */ byte[] val$waveform;

            C05381(byte[] bArr) {
                this.val$waveform = bArr;
            }

            public void run() {
                MessageObject messageObject = (MessageObject) MediaController.this.generatingWaveform.remove(AnonymousClass18.this.val$id);
                if (messageObject != null && this.val$waveform != null) {
                    for (int a = MediaController.PROCESSOR_TYPE_OTHER; a < messageObject.getDocument().attributes.size(); a += MediaController.PROCESSOR_TYPE_QCOM) {
                        DocumentAttribute attribute = (DocumentAttribute) messageObject.getDocument().attributes.get(a);
                        if (attribute instanceof TL_documentAttributeAudio) {
                            attribute.waveform = this.val$waveform;
                            attribute.flags |= MediaController.PROCESSOR_TYPE_SEC;
                            break;
                        }
                    }
                    messages_Messages messagesRes = new TL_messages_messages();
                    messagesRes.messages.add(messageObject.messageOwner);
                    MessagesStorage.getInstance().putMessages(messagesRes, messageObject.getDialogId(), -1, (int) MediaController.PROCESSOR_TYPE_OTHER, false);
                    ArrayList<MessageObject> arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.replaceMessagesObjects;
                    Object[] objArr = new Object[MediaController.PROCESSOR_TYPE_INTEL];
                    objArr[MediaController.PROCESSOR_TYPE_OTHER] = Long.valueOf(messageObject.getDialogId());
                    objArr[MediaController.PROCESSOR_TYPE_QCOM] = arrayList;
                    instance.postNotificationName(i, objArr);
                }
            }
        }

        AnonymousClass18(String str, String str2) {
            this.val$path = str;
            this.val$id = str2;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05381(MediaController.getInstance().getWaveform(this.val$path)));
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ TL_document val$audioToSend;
        final /* synthetic */ File val$recordingAudioFileToSend;
        final /* synthetic */ int val$send;

        /* renamed from: org.telegram.messenger.MediaController.19.1 */
        class C05391 implements Runnable {
            C05391() {
            }

            public void run() {
                VideoEditedInfo videoEditedInfo = null;
                AnonymousClass19.this.val$audioToSend.date = ConnectionsManager.getInstance().getCurrentTime();
                AnonymousClass19.this.val$audioToSend.size = (int) AnonymousClass19.this.val$recordingAudioFileToSend.length();
                TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.voice = true;
                attributeAudio.waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
                if (attributeAudio.waveform != null) {
                    attributeAudio.flags |= MediaController.PROCESSOR_TYPE_SEC;
                }
                long duration = MediaController.this.recordTimeCount;
                attributeAudio.duration = (int) (MediaController.this.recordTimeCount / 1000);
                AnonymousClass19.this.val$audioToSend.attributes.add(attributeAudio);
                if (duration > 700) {
                    TL_document tL_document;
                    if (AnonymousClass19.this.val$send == MediaController.PROCESSOR_TYPE_QCOM) {
                        SendMessagesHelper.getInstance().sendMessage(AnonymousClass19.this.val$audioToSend, null, AnonymousClass19.this.val$recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null);
                    }
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.audioDidSent;
                    Object[] objArr = new Object[MediaController.PROCESSOR_TYPE_INTEL];
                    if (AnonymousClass19.this.val$send == MediaController.PROCESSOR_TYPE_INTEL) {
                        tL_document = AnonymousClass19.this.val$audioToSend;
                    } else {
                        tL_document = null;
                    }
                    objArr[MediaController.PROCESSOR_TYPE_OTHER] = tL_document;
                    if (AnonymousClass19.this.val$send == MediaController.PROCESSOR_TYPE_INTEL) {
                        videoEditedInfo = AnonymousClass19.this.val$recordingAudioFileToSend.getAbsolutePath();
                    }
                    objArr[MediaController.PROCESSOR_TYPE_QCOM] = videoEditedInfo;
                    instance.postNotificationName(i, objArr);
                    return;
                }
                AnonymousClass19.this.val$recordingAudioFileToSend.delete();
            }
        }

        AnonymousClass19(TL_document tL_document, File file, int i) {
            this.val$audioToSend = tL_document;
            this.val$recordingAudioFileToSend = file;
            this.val$send = i;
        }

        public void run() {
            MediaController.this.stopRecord();
            AndroidUtilities.runOnUIThread(new C05391());
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.1 */
    class C05401 implements Runnable {

        /* renamed from: org.telegram.messenger.MediaController.1.1 */
        class C05311 implements Runnable {
            final /* synthetic */ ByteBuffer val$finalBuffer;
            final /* synthetic */ boolean val$flush;

            /* renamed from: org.telegram.messenger.MediaController.1.1.1 */
            class C05301 implements Runnable {
                C05301() {
                }

                public void run() {
                    MediaController.this.recordBuffers.add(C05311.this.val$finalBuffer);
                }
            }

            C05311(ByteBuffer byteBuffer, boolean z) {
                this.val$finalBuffer = byteBuffer;
                this.val$flush = z;
            }

            public void run() {
                while (this.val$finalBuffer.hasRemaining()) {
                    int oldLimit = -1;
                    if (this.val$finalBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                        oldLimit = this.val$finalBuffer.limit();
                        this.val$finalBuffer.limit(MediaController.this.fileBuffer.remaining() + this.val$finalBuffer.position());
                    }
                    MediaController.this.fileBuffer.put(this.val$finalBuffer);
                    if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || this.val$flush) {
                        if (MediaController.this.writeFrame(MediaController.this.fileBuffer, !this.val$flush ? MediaController.this.fileBuffer.limit() : this.val$finalBuffer.position()) != 0) {
                            MediaController.this.fileBuffer.rewind();
                            MediaController.access$714(MediaController.this, (long) ((MediaController.this.fileBuffer.limit() / MediaController.PROCESSOR_TYPE_INTEL) / MediaController.AUTODOWNLOAD_MASK_MUSIC));
                        }
                    }
                    if (oldLimit != -1) {
                        this.val$finalBuffer.limit(oldLimit);
                    }
                }
                MediaController.this.recordQueue.postRunnable(new C05301());
            }
        }

        /* renamed from: org.telegram.messenger.MediaController.1.2 */
        class C05322 implements Runnable {
            final /* synthetic */ double val$amplitude;

            C05322(double d) {
                this.val$amplitude = d;
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.recordProgressChanged;
                Object[] objArr = new Object[MediaController.PROCESSOR_TYPE_INTEL];
                objArr[MediaController.PROCESSOR_TYPE_OTHER] = Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime);
                objArr[MediaController.PROCESSOR_TYPE_QCOM] = Double.valueOf(this.val$amplitude);
                instance.postNotificationName(i, objArr);
            }
        }

        C05401() {
        }

        public void run() {
            if (MediaController.this.audioRecorder != null) {
                ByteBuffer buffer;
                if (MediaController.this.recordBuffers.isEmpty()) {
                    buffer = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
                    buffer.order(ByteOrder.nativeOrder());
                } else {
                    buffer = (ByteBuffer) MediaController.this.recordBuffers.get(MediaController.PROCESSOR_TYPE_OTHER);
                    MediaController.this.recordBuffers.remove(MediaController.PROCESSOR_TYPE_OTHER);
                }
                buffer.rewind();
                int len = MediaController.this.audioRecorder.read(buffer, buffer.capacity());
                if (len > 0) {
                    buffer.limit(len);
                    double d = 0.0d;
                    try {
                        float sampleStep;
                        long newSamplesCount = MediaController.this.samplesCount + ((long) (len / MediaController.PROCESSOR_TYPE_INTEL));
                        int currentPart = (int) ((((double) MediaController.this.samplesCount) / ((double) newSamplesCount)) * ((double) MediaController.this.recordSamples.length));
                        int newPart = MediaController.this.recordSamples.length - currentPart;
                        if (currentPart != 0) {
                            sampleStep = ((float) MediaController.this.recordSamples.length) / ((float) currentPart);
                            float currentNum = 0.0f;
                            for (int a = MediaController.PROCESSOR_TYPE_OTHER; a < currentPart; a += MediaController.PROCESSOR_TYPE_QCOM) {
                                MediaController.this.recordSamples[a] = MediaController.this.recordSamples[(int) currentNum];
                                currentNum += sampleStep;
                            }
                        }
                        int currentNum2 = currentPart;
                        float nextNum = 0.0f;
                        sampleStep = (((float) len) / 2.0f) / ((float) newPart);
                        for (int i = MediaController.PROCESSOR_TYPE_OTHER; i < len / MediaController.PROCESSOR_TYPE_INTEL; i += MediaController.PROCESSOR_TYPE_QCOM) {
                            short peak = buffer.getShort();
                            if (peak > (short) 2500) {
                                d += (double) (peak * peak);
                            }
                            int i2 = (int) nextNum;
                            if (i == r0) {
                                i2 = MediaController.this.recordSamples.length;
                                if (currentNum2 < r0) {
                                    MediaController.this.recordSamples[currentNum2] = peak;
                                    nextNum += sampleStep;
                                    currentNum2 += MediaController.PROCESSOR_TYPE_QCOM;
                                }
                            }
                        }
                        MediaController.this.samplesCount = newSamplesCount;
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    buffer.position(MediaController.PROCESSOR_TYPE_OTHER);
                    double amplitude = Math.sqrt((d / ((double) len)) / 2.0d);
                    ByteBuffer finalBuffer = buffer;
                    boolean flush = len != buffer.capacity();
                    if (len != 0) {
                        MediaController.this.fileEncodingQueue.postRunnable(new C05311(finalBuffer, flush));
                    }
                    MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
                    AndroidUtilities.runOnUIThread(new C05322(amplitude));
                    return;
                }
                MediaController.this.recordBuffers.add(buffer);
                MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.20 */
    class AnonymousClass20 implements Runnable {
        final /* synthetic */ int val$send;

        /* renamed from: org.telegram.messenger.MediaController.20.1 */
        class C05411 implements Runnable {
            C05411() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[MediaController.PROCESSOR_TYPE_OTHER]);
            }
        }

        AnonymousClass20(int i) {
            this.val$send = i;
        }

        public void run() {
            if (MediaController.this.audioRecorder != null) {
                try {
                    MediaController.this.sendAfterDone = this.val$send;
                    MediaController.this.audioRecorder.stop();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                    if (MediaController.this.recordingAudioFile != null) {
                        MediaController.this.recordingAudioFile.delete();
                    }
                }
                if (this.val$send == 0) {
                    MediaController.this.stopRecordingInternal(MediaController.PROCESSOR_TYPE_OTHER);
                }
                try {
                    ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
                AndroidUtilities.runOnUIThread(new C05411());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.21 */
    static class AnonymousClass21 implements Runnable {
        final /* synthetic */ ProgressDialog val$finalProgress;
        final /* synthetic */ String val$mime;
        final /* synthetic */ String val$name;
        final /* synthetic */ File val$sourceFile;
        final /* synthetic */ int val$type;

        /* renamed from: org.telegram.messenger.MediaController.21.1 */
        class C05421 implements Runnable {
            final /* synthetic */ int val$progress;

            C05421(int i) {
                this.val$progress = i;
            }

            public void run() {
                try {
                    AnonymousClass21.this.val$finalProgress.setProgress(this.val$progress);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        /* renamed from: org.telegram.messenger.MediaController.21.2 */
        class C05432 implements Runnable {
            C05432() {
            }

            public void run() {
                try {
                    AnonymousClass21.this.val$finalProgress.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        AnonymousClass21(int i, String str, File file, ProgressDialog progressDialog, String str2) {
            this.val$type = i;
            this.val$name = str;
            this.val$sourceFile = file;
            this.val$finalProgress = progressDialog;
            this.val$mime = str2;
        }

        public void run() {
            File destFile = null;
            try {
                if (this.val$type == 0) {
                    destFile = AndroidUtilities.generatePicturePath();
                } else if (this.val$type == MediaController.PROCESSOR_TYPE_QCOM) {
                    destFile = AndroidUtilities.generateVideoPath();
                } else if (this.val$type == MediaController.PROCESSOR_TYPE_INTEL) {
                    f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    f.mkdir();
                    destFile = new File(f, this.val$name);
                } else if (this.val$type == MediaController.PROCESSOR_TYPE_MTK) {
                    f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    f.mkdirs();
                    destFile = new File(f, this.val$name);
                }
                if (!destFile.exists()) {
                    destFile.createNewFile();
                }
                FileChannel source = null;
                FileChannel destination = null;
                boolean result = true;
                long lastProgress = System.currentTimeMillis() - 500;
                try {
                    source = new FileInputStream(this.val$sourceFile).getChannel();
                    destination = new FileOutputStream(destFile).getChannel();
                    long size = source.size();
                    for (long a = 0; a < size; a += PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM) {
                        destination.transferFrom(source, a, Math.min(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, size - a));
                        if (this.val$finalProgress != null && lastProgress <= System.currentTimeMillis() - 500) {
                            lastProgress = System.currentTimeMillis();
                            AndroidUtilities.runOnUIThread(new C05421((int) ((((float) a) / ((float) size)) * 100.0f)));
                        }
                    }
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                    result = false;
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                } catch (Throwable th) {
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                }
                if (result) {
                    if (this.val$type == MediaController.PROCESSOR_TYPE_INTEL) {
                        ((DownloadManager) ApplicationLoader.applicationContext.getSystemService("download")).addCompletedDownload(destFile.getName(), destFile.getName(), false, this.val$mime, destFile.getAbsolutePath(), destFile.length(), true);
                    } else {
                        AndroidUtilities.addMediaToGallery(Uri.fromFile(destFile));
                    }
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            if (this.val$finalProgress != null) {
                AndroidUtilities.runOnUIThread(new C05432());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.22 */
    static class AnonymousClass22 implements Runnable {
        final /* synthetic */ int val$guid;

        /* renamed from: org.telegram.messenger.MediaController.22.1 */
        class C05441 implements Runnable {
            final /* synthetic */ ArrayList val$albumsSorted;
            final /* synthetic */ AlbumEntry val$allPhotosAlbumFinal;
            final /* synthetic */ Integer val$cameraAlbumIdFinal;
            final /* synthetic */ Integer val$cameraAlbumVideoIdFinal;
            final /* synthetic */ ArrayList val$videoAlbumsSorted;

            C05441(AlbumEntry albumEntry, ArrayList arrayList, Integer num, ArrayList arrayList2, Integer num2) {
                this.val$allPhotosAlbumFinal = albumEntry;
                this.val$albumsSorted = arrayList;
                this.val$cameraAlbumIdFinal = num;
                this.val$videoAlbumsSorted = arrayList2;
                this.val$cameraAlbumVideoIdFinal = num2;
            }

            public void run() {
                MediaController.allPhotosAlbumEntry = this.val$allPhotosAlbumFinal;
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.albumsDidLoaded;
                Object[] objArr = new Object[MediaController.PROCESSOR_TYPE_TI];
                objArr[MediaController.PROCESSOR_TYPE_OTHER] = Integer.valueOf(AnonymousClass22.this.val$guid);
                objArr[MediaController.PROCESSOR_TYPE_QCOM] = this.val$albumsSorted;
                objArr[MediaController.PROCESSOR_TYPE_INTEL] = this.val$cameraAlbumIdFinal;
                objArr[MediaController.PROCESSOR_TYPE_MTK] = this.val$videoAlbumsSorted;
                objArr[MediaController.PROCESSOR_TYPE_SEC] = this.val$cameraAlbumVideoIdFinal;
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass22(int i) {
            this.val$guid = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r38 = this;
            r21 = new java.util.ArrayList;
            r21.<init>();
            r37 = new java.util.ArrayList;
            r37.<init>();
            r20 = new java.util.HashMap;
            r20.<init>();
            r22 = 0;
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r10 = android.os.Environment.DIRECTORY_DCIM;
            r10 = android.os.Environment.getExternalStoragePublicDirectory(r10);
            r10 = r10.getAbsolutePath();
            r2 = r2.append(r10);
            r10 = "/";
            r2 = r2.append(r10);
            r10 = "Camera/";
            r2 = r2.append(r10);
            r30 = r2.toString();
            r28 = 0;
            r29 = 0;
            r31 = 0;
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0149 }
            r10 = 23;
            if (r2 < r10) goto L_0x0050;
        L_0x0040:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0149 }
            r10 = 23;
            if (r2 < r10) goto L_0x027b;
        L_0x0046:
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0149 }
            r10 = "android.permission.READ_EXTERNAL_STORAGE";
            r2 = r2.checkSelfPermission(r10);	 Catch:{ Throwable -> 0x0149 }
            if (r2 != 0) goto L_0x027b;
        L_0x0050:
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0149 }
            r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x0149 }
            r3 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0149 }
            r4 = org.telegram.messenger.MediaController.projectionPhotos;	 Catch:{ Throwable -> 0x0149 }
            r5 = 0;
            r6 = 0;
            r7 = "datetaken DESC";
            r31 = android.provider.MediaStore.Images.Media.query(r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0149 }
            if (r31 == 0) goto L_0x027b;
        L_0x0066:
            r2 = "_id";
            r0 = r31;
            r35 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0149 }
            r2 = "bucket_id";
            r0 = r31;
            r25 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0149 }
            r2 = "bucket_display_name";
            r0 = r31;
            r27 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0149 }
            r2 = "_data";
            r0 = r31;
            r32 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0149 }
            r2 = "datetaken";
            r0 = r31;
            r33 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0149 }
            r2 = "orientation";
            r0 = r31;
            r36 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0149 }
            r23 = r22;
        L_0x0098:
            r2 = r31.moveToNext();	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            if (r2 == 0) goto L_0x0279;
        L_0x009e:
            r0 = r31;
            r1 = r35;
            r5 = r0.getInt(r1);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r0 = r31;
            r1 = r25;
            r4 = r0.getInt(r1);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r0 = r31;
            r1 = r27;
            r26 = r0.getString(r1);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r8 = r31.getString(r32);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r0 = r31;
            r1 = r33;
            r6 = r0.getLong(r1);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r0 = r31;
            r1 = r36;
            r9 = r0.getInt(r1);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            if (r8 == 0) goto L_0x0098;
        L_0x00cc:
            r2 = r8.length();	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            if (r2 == 0) goto L_0x0098;
        L_0x00d2:
            r3 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r10 = 0;
            r3.<init>(r4, r5, r6, r8, r9, r10);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            if (r23 != 0) goto L_0x02df;
        L_0x00da:
            r22 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r2 = 0;
            r10 = "AllPhotos";
            r11 = 2131165277; // 0x7f07005d float:1.7944767E38 double:1.052935549E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r11);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r11 = 0;
            r0 = r22;
            r0.<init>(r2, r10, r3, r11);	 Catch:{ Throwable -> 0x02da, all -> 0x02d6 }
            r2 = 0;
            r0 = r21;
            r1 = r22;
            r0.add(r2, r1);	 Catch:{ Throwable -> 0x0149 }
        L_0x00f4:
            if (r22 == 0) goto L_0x00fb;
        L_0x00f6:
            r0 = r22;
            r0.addPhoto(r3);	 Catch:{ Throwable -> 0x0149 }
        L_0x00fb:
            r2 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0149 }
            r0 = r20;
            r19 = r0.get(r2);	 Catch:{ Throwable -> 0x0149 }
            r19 = (org.telegram.messenger.MediaController.AlbumEntry) r19;	 Catch:{ Throwable -> 0x0149 }
            if (r19 != 0) goto L_0x0138;
        L_0x0109:
            r19 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0149 }
            r2 = 0;
            r0 = r19;
            r1 = r26;
            r0.<init>(r4, r1, r3, r2);	 Catch:{ Throwable -> 0x0149 }
            r2 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0149 }
            r0 = r20;
            r1 = r19;
            r0.put(r2, r1);	 Catch:{ Throwable -> 0x0149 }
            if (r28 != 0) goto L_0x0141;
        L_0x0120:
            if (r30 == 0) goto L_0x0141;
        L_0x0122:
            if (r8 == 0) goto L_0x0141;
        L_0x0124:
            r0 = r30;
            r2 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x0149 }
            if (r2 == 0) goto L_0x0141;
        L_0x012c:
            r2 = 0;
            r0 = r21;
            r1 = r19;
            r0.add(r2, r1);	 Catch:{ Throwable -> 0x0149 }
            r28 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0149 }
        L_0x0138:
            r0 = r19;
            r0.addPhoto(r3);	 Catch:{ Throwable -> 0x0149 }
            r23 = r22;
            goto L_0x0098;
        L_0x0141:
            r0 = r21;
            r1 = r19;
            r0.add(r1);	 Catch:{ Throwable -> 0x0149 }
            goto L_0x0138;
        L_0x0149:
            r34 = move-exception;
        L_0x014a:
            r2 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r2, r0);	 Catch:{ all -> 0x0296 }
            if (r31 == 0) goto L_0x0156;
        L_0x0153:
            r31.close();	 Catch:{ Exception -> 0x028c }
        L_0x0156:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0257 }
            r10 = 23;
            if (r2 < r10) goto L_0x016c;
        L_0x015c:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0257 }
            r10 = 23;
            if (r2 < r10) goto L_0x02b5;
        L_0x0162:
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0257 }
            r10 = "android.permission.READ_EXTERNAL_STORAGE";
            r2 = r2.checkSelfPermission(r10);	 Catch:{ Throwable -> 0x0257 }
            if (r2 != 0) goto L_0x02b5;
        L_0x016c:
            r20.clear();	 Catch:{ Throwable -> 0x0257 }
            r24 = 0;
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0257 }
            r10 = r2.getContentResolver();	 Catch:{ Throwable -> 0x0257 }
            r11 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0257 }
            r12 = org.telegram.messenger.MediaController.projectionVideo;	 Catch:{ Throwable -> 0x0257 }
            r13 = 0;
            r14 = 0;
            r15 = "datetaken DESC";
            r31 = android.provider.MediaStore.Images.Media.query(r10, r11, r12, r13, r14, r15);	 Catch:{ Throwable -> 0x0257 }
            if (r31 == 0) goto L_0x02b5;
        L_0x0187:
            r2 = "_id";
            r0 = r31;
            r35 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0257 }
            r2 = "bucket_id";
            r0 = r31;
            r25 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0257 }
            r2 = "bucket_display_name";
            r0 = r31;
            r27 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0257 }
            r2 = "_data";
            r0 = r31;
            r32 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0257 }
            r2 = "datetaken";
            r0 = r31;
            r33 = r0.getColumnIndex(r2);	 Catch:{ Throwable -> 0x0257 }
        L_0x01af:
            r2 = r31.moveToNext();	 Catch:{ Throwable -> 0x0257 }
            if (r2 == 0) goto L_0x02b5;
        L_0x01b5:
            r0 = r31;
            r1 = r35;
            r5 = r0.getInt(r1);	 Catch:{ Throwable -> 0x0257 }
            r0 = r31;
            r1 = r25;
            r4 = r0.getInt(r1);	 Catch:{ Throwable -> 0x0257 }
            r0 = r31;
            r1 = r27;
            r26 = r0.getString(r1);	 Catch:{ Throwable -> 0x0257 }
            r8 = r31.getString(r32);	 Catch:{ Throwable -> 0x0257 }
            r0 = r31;
            r1 = r33;
            r6 = r0.getLong(r1);	 Catch:{ Throwable -> 0x0257 }
            if (r8 == 0) goto L_0x01af;
        L_0x01db:
            r2 = r8.length();	 Catch:{ Throwable -> 0x0257 }
            if (r2 == 0) goto L_0x01af;
        L_0x01e1:
            r3 = new org.telegram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x0257 }
            r17 = 0;
            r18 = 1;
            r11 = r3;
            r12 = r4;
            r13 = r5;
            r14 = r6;
            r16 = r8;
            r11.<init>(r12, r13, r14, r16, r17, r18);	 Catch:{ Throwable -> 0x0257 }
            if (r24 != 0) goto L_0x020c;
        L_0x01f2:
            r24 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0257 }
            r2 = 0;
            r10 = "AllVideo";
            r11 = 2131165278; // 0x7f07005e float:1.7944769E38 double:1.0529355495E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r11);	 Catch:{ Throwable -> 0x0257 }
            r11 = 1;
            r0 = r24;
            r0.<init>(r2, r10, r3, r11);	 Catch:{ Throwable -> 0x0257 }
            r2 = 0;
            r0 = r37;
            r1 = r24;
            r0.add(r2, r1);	 Catch:{ Throwable -> 0x0257 }
        L_0x020c:
            if (r24 == 0) goto L_0x0213;
        L_0x020e:
            r0 = r24;
            r0.addPhoto(r3);	 Catch:{ Throwable -> 0x0257 }
        L_0x0213:
            r2 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0257 }
            r0 = r20;
            r19 = r0.get(r2);	 Catch:{ Throwable -> 0x0257 }
            r19 = (org.telegram.messenger.MediaController.AlbumEntry) r19;	 Catch:{ Throwable -> 0x0257 }
            if (r19 != 0) goto L_0x0250;
        L_0x0221:
            r19 = new org.telegram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x0257 }
            r2 = 1;
            r0 = r19;
            r1 = r26;
            r0.<init>(r4, r1, r3, r2);	 Catch:{ Throwable -> 0x0257 }
            r2 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0257 }
            r0 = r20;
            r1 = r19;
            r0.put(r2, r1);	 Catch:{ Throwable -> 0x0257 }
            if (r29 != 0) goto L_0x02a6;
        L_0x0238:
            if (r30 == 0) goto L_0x02a6;
        L_0x023a:
            if (r8 == 0) goto L_0x02a6;
        L_0x023c:
            r0 = r30;
            r2 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x0257 }
            if (r2 == 0) goto L_0x02a6;
        L_0x0244:
            r2 = 0;
            r0 = r37;
            r1 = r19;
            r0.add(r2, r1);	 Catch:{ Throwable -> 0x0257 }
            r29 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0257 }
        L_0x0250:
            r0 = r19;
            r0.addPhoto(r3);	 Catch:{ Throwable -> 0x0257 }
            goto L_0x01af;
        L_0x0257:
            r34 = move-exception;
            r2 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r2, r0);	 Catch:{ all -> 0x02ae }
            if (r31 == 0) goto L_0x0264;
        L_0x0261:
            r31.close();	 Catch:{ Exception -> 0x02c4 }
        L_0x0264:
            r14 = r28;
            r16 = r29;
            r12 = r22;
            r10 = new org.telegram.messenger.MediaController$22$1;
            r11 = r38;
            r13 = r21;
            r15 = r37;
            r10.<init>(r12, r13, r14, r15, r16);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10);
            return;
        L_0x0279:
            r22 = r23;
        L_0x027b:
            if (r31 == 0) goto L_0x0156;
        L_0x027d:
            r31.close();	 Catch:{ Exception -> 0x0282 }
            goto L_0x0156;
        L_0x0282:
            r34 = move-exception;
            r2 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r2, r0);
            goto L_0x0156;
        L_0x028c:
            r34 = move-exception;
            r2 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r2, r0);
            goto L_0x0156;
        L_0x0296:
            r2 = move-exception;
        L_0x0297:
            if (r31 == 0) goto L_0x029c;
        L_0x0299:
            r31.close();	 Catch:{ Exception -> 0x029d }
        L_0x029c:
            throw r2;
        L_0x029d:
            r34 = move-exception;
            r10 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r10, r0);
            goto L_0x029c;
        L_0x02a6:
            r0 = r37;
            r1 = r19;
            r0.add(r1);	 Catch:{ Throwable -> 0x0257 }
            goto L_0x0250;
        L_0x02ae:
            r2 = move-exception;
            if (r31 == 0) goto L_0x02b4;
        L_0x02b1:
            r31.close();	 Catch:{ Exception -> 0x02cd }
        L_0x02b4:
            throw r2;
        L_0x02b5:
            if (r31 == 0) goto L_0x0264;
        L_0x02b7:
            r31.close();	 Catch:{ Exception -> 0x02bb }
            goto L_0x0264;
        L_0x02bb:
            r34 = move-exception;
            r2 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r2, r0);
            goto L_0x0264;
        L_0x02c4:
            r34 = move-exception;
            r2 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r2, r0);
            goto L_0x0264;
        L_0x02cd:
            r34 = move-exception;
            r10 = "tmessages";
            r0 = r34;
            org.telegram.messenger.FileLog.m13e(r10, r0);
            goto L_0x02b4;
        L_0x02d6:
            r2 = move-exception;
            r22 = r23;
            goto L_0x0297;
        L_0x02da:
            r34 = move-exception;
            r22 = r23;
            goto L_0x014a;
        L_0x02df:
            r22 = r23;
            goto L_0x00f4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.22.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.23 */
    class AnonymousClass23 implements Runnable {
        final /* synthetic */ boolean val$error;
        final /* synthetic */ File val$file;
        final /* synthetic */ boolean val$firstWrite;
        final /* synthetic */ boolean val$last;
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass23(boolean z, MessageObject messageObject, File file, boolean z2, boolean z3) {
            this.val$error = z;
            this.val$messageObject = messageObject;
            this.val$file = file;
            this.val$firstWrite = z2;
            this.val$last = z3;
        }

        public void run() {
            NotificationCenter instance;
            int i;
            Object[] objArr;
            if (this.val$error) {
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.FilePreparingFailed;
                objArr = new Object[MediaController.PROCESSOR_TYPE_INTEL];
                objArr[MediaController.PROCESSOR_TYPE_OTHER] = this.val$messageObject;
                objArr[MediaController.PROCESSOR_TYPE_QCOM] = this.val$file.toString();
                instance.postNotificationName(i, objArr);
            } else {
                if (this.val$firstWrite) {
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.FilePreparingStarted;
                    objArr = new Object[MediaController.PROCESSOR_TYPE_INTEL];
                    objArr[MediaController.PROCESSOR_TYPE_OTHER] = this.val$messageObject;
                    objArr[MediaController.PROCESSOR_TYPE_QCOM] = this.val$file.toString();
                    instance.postNotificationName(i, objArr);
                }
                NotificationCenter instance2 = NotificationCenter.getInstance();
                int i2 = NotificationCenter.FileNewChunkAvailable;
                Object[] objArr2 = new Object[MediaController.PROCESSOR_TYPE_MTK];
                objArr2[MediaController.PROCESSOR_TYPE_OTHER] = this.val$messageObject;
                objArr2[MediaController.PROCESSOR_TYPE_QCOM] = this.val$file.toString();
                objArr2[MediaController.PROCESSOR_TYPE_INTEL] = Long.valueOf(this.val$last ? this.val$file.length() : 0);
                instance2.postNotificationName(i2, objArr2);
            }
            if (this.val$error || this.val$last) {
                synchronized (MediaController.this.videoConvertSync) {
                    MediaController.this.cancelCurrentVideoConversion = false;
                }
                MediaController.this.videoConvertQueue.remove(this.val$messageObject);
                MediaController.this.startVideoConvertFromQueue();
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.2 */
    class C05452 implements Runnable {
        C05452() {
        }

        public void run() {
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.3 */
    class C05463 extends BroadcastReceiver {
        C05463() {
        }

        public void onReceive(Context context, Intent intent) {
            MediaController.this.checkAutodownloadSettings();
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.4 */
    class C05474 extends PhoneStateListener {
        C05474() {
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == MediaController.PROCESSOR_TYPE_QCOM) {
                if (MediaController.this.isPlayingAudio(MediaController.this.getPlayingMessageObject()) && !MediaController.this.isAudioPaused()) {
                    MediaController.this.pauseAudio(MediaController.this.getPlayingMessageObject());
                } else if (!(MediaController.this.recordStartRunnable == null && MediaController.this.recordingAudio == null)) {
                    MediaController.this.stopRecording(MediaController.PROCESSOR_TYPE_INTEL);
                }
                MediaController.this.callInProgress = true;
            } else if (state == 0) {
                MediaController.this.callInProgress = false;
            } else if (state == MediaController.PROCESSOR_TYPE_INTEL) {
                MediaController.this.callInProgress = true;
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.5 */
    class C05495 extends TimerTask {
        final /* synthetic */ MessageObject val$currentPlayingMessageObject;

        /* renamed from: org.telegram.messenger.MediaController.5.1 */
        class C05481 implements Runnable {
            C05481() {
            }

            public void run() {
                if (C05495.this.val$currentPlayingMessageObject == null) {
                    return;
                }
                if ((MediaController.this.audioPlayer != null || MediaController.this.audioTrackPlayer != null) && !MediaController.this.isPaused) {
                    try {
                        if (MediaController.this.ignoreFirstProgress != 0) {
                            MediaController.this.ignoreFirstProgress = MediaController.this.ignoreFirstProgress - 1;
                            return;
                        }
                        int progress;
                        float value;
                        if (MediaController.this.audioPlayer != null) {
                            progress = MediaController.this.audioPlayer.getCurrentPosition();
                            value = ((float) MediaController.this.lastProgress) / ((float) MediaController.this.audioPlayer.getDuration());
                            if (progress <= MediaController.this.lastProgress) {
                                return;
                            }
                        }
                        progress = (int) (((float) MediaController.this.lastPlayPcm) / 48.0f);
                        value = ((float) MediaController.this.lastPlayPcm) / ((float) MediaController.this.currentTotalPcmDuration);
                        if (progress == MediaController.this.lastProgress) {
                            return;
                        }
                        MediaController.this.lastProgress = progress;
                        C05495.this.val$currentPlayingMessageObject.audioProgress = value;
                        C05495.this.val$currentPlayingMessageObject.audioProgressSec = MediaController.this.lastProgress / 1000;
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.audioProgressDidChanged;
                        Object[] objArr = new Object[MediaController.PROCESSOR_TYPE_INTEL];
                        objArr[MediaController.PROCESSOR_TYPE_OTHER] = Integer.valueOf(C05495.this.val$currentPlayingMessageObject.getId());
                        objArr[MediaController.PROCESSOR_TYPE_QCOM] = Float.valueOf(value);
                        instance.postNotificationName(i, objArr);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
            }
        }

        C05495(MessageObject messageObject) {
            this.val$currentPlayingMessageObject = messageObject;
        }

        public void run() {
            synchronized (MediaController.this.sync) {
                AndroidUtilities.runOnUIThread(new C05481());
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.6 */
    class C05506 implements Runnable {
        final /* synthetic */ ArrayList val$screenshotDates;

        C05506(ArrayList arrayList) {
            this.val$screenshotDates = arrayList;
        }

        public void run() {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenshotTook, new Object[MediaController.PROCESSOR_TYPE_OTHER]);
            MediaController.this.checkScreenshots(this.val$screenshotDates);
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.7 */
    class C05517 implements Runnable {
        C05517() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r10 = this;
            r9 = 1;
            r8 = 0;
            r4 = org.telegram.messenger.MediaController.this;
            r4 = r4.decodingFinished;
            if (r4 == 0) goto L_0x0010;
        L_0x000a:
            r4 = org.telegram.messenger.MediaController.this;
            r4.checkPlayerQueue();
        L_0x000f:
            return;
        L_0x0010:
            r3 = 0;
        L_0x0011:
            r2 = 0;
            r4 = org.telegram.messenger.MediaController.this;
            r5 = r4.playerSync;
            monitor-enter(r5);
            r4 = org.telegram.messenger.MediaController.this;	 Catch:{ all -> 0x009e }
            r4 = r4.freePlayerBuffers;	 Catch:{ all -> 0x009e }
            r4 = r4.isEmpty();	 Catch:{ all -> 0x009e }
            if (r4 != 0) goto L_0x003e;
        L_0x0025:
            r4 = org.telegram.messenger.MediaController.this;	 Catch:{ all -> 0x009e }
            r4 = r4.freePlayerBuffers;	 Catch:{ all -> 0x009e }
            r6 = 0;
            r4 = r4.get(r6);	 Catch:{ all -> 0x009e }
            r0 = r4;
            r0 = (org.telegram.messenger.MediaController.AudioBuffer) r0;	 Catch:{ all -> 0x009e }
            r2 = r0;
            r4 = org.telegram.messenger.MediaController.this;	 Catch:{ all -> 0x009e }
            r4 = r4.freePlayerBuffers;	 Catch:{ all -> 0x009e }
            r6 = 0;
            r4.remove(r6);	 Catch:{ all -> 0x009e }
        L_0x003e:
            r4 = org.telegram.messenger.MediaController.this;	 Catch:{ all -> 0x009e }
            r4 = r4.usedPlayerBuffers;	 Catch:{ all -> 0x009e }
            r4 = r4.isEmpty();	 Catch:{ all -> 0x009e }
            if (r4 != 0) goto L_0x004b;
        L_0x004a:
            r3 = 1;
        L_0x004b:
            monitor-exit(r5);	 Catch:{ all -> 0x009e }
            if (r2 == 0) goto L_0x00b5;
        L_0x004e:
            r4 = org.telegram.messenger.MediaController.this;
            r5 = r2.buffer;
            r6 = org.telegram.messenger.MediaController.this;
            r6 = r6.playerBufferSize;
            r7 = org.telegram.messenger.MediaController.readArgs;
            r4.readOpusFile(r5, r6, r7);
            r4 = org.telegram.messenger.MediaController.readArgs;
            r4 = r4[r8];
            r2.size = r4;
            r4 = org.telegram.messenger.MediaController.readArgs;
            r4 = r4[r9];
            r4 = (long) r4;
            r2.pcmOffset = r4;
            r4 = org.telegram.messenger.MediaController.readArgs;
            r5 = 2;
            r4 = r4[r5];
            r2.finished = r4;
            r4 = r2.finished;
            if (r4 != r9) goto L_0x007a;
        L_0x0075:
            r4 = org.telegram.messenger.MediaController.this;
            r4.decodingFinished = r9;
        L_0x007a:
            r4 = r2.size;
            if (r4 == 0) goto L_0x00a4;
        L_0x007e:
            r4 = r2.buffer;
            r4.rewind();
            r4 = r2.buffer;
            r5 = r2.bufferBytes;
            r4.get(r5);
            r4 = org.telegram.messenger.MediaController.this;
            r5 = r4.playerSync;
            monitor-enter(r5);
            r4 = org.telegram.messenger.MediaController.this;	 Catch:{ all -> 0x00a1 }
            r4 = r4.usedPlayerBuffers;	 Catch:{ all -> 0x00a1 }
            r4.add(r2);	 Catch:{ all -> 0x00a1 }
            monitor-exit(r5);	 Catch:{ all -> 0x00a1 }
            r3 = 1;
            goto L_0x0011;
        L_0x009e:
            r4 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x009e }
            throw r4;
        L_0x00a1:
            r4 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x00a1 }
            throw r4;
        L_0x00a4:
            r4 = org.telegram.messenger.MediaController.this;
            r5 = r4.playerSync;
            monitor-enter(r5);
            r4 = org.telegram.messenger.MediaController.this;	 Catch:{ all -> 0x00be }
            r4 = r4.freePlayerBuffers;	 Catch:{ all -> 0x00be }
            r4.add(r2);	 Catch:{ all -> 0x00be }
            monitor-exit(r5);	 Catch:{ all -> 0x00be }
        L_0x00b5:
            if (r3 == 0) goto L_0x000f;
        L_0x00b7:
            r4 = org.telegram.messenger.MediaController.this;
            r4.checkPlayerQueue();
            goto L_0x000f;
        L_0x00be:
            r4 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x00be }
            throw r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.7.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.8 */
    class C05538 implements Runnable {

        /* renamed from: org.telegram.messenger.MediaController.8.1 */
        class C05521 implements Runnable {
            final /* synthetic */ int val$finalBuffersWrited;
            final /* synthetic */ int val$marker;
            final /* synthetic */ long val$pcm;

            C05521(long j, int i, int i2) {
                this.val$pcm = j;
                this.val$marker = i;
                this.val$finalBuffersWrited = i2;
            }

            public void run() {
                MediaController.this.lastPlayPcm = this.val$pcm;
                if (this.val$marker != -1) {
                    if (MediaController.this.audioTrackPlayer != null) {
                        MediaController.this.audioTrackPlayer.setNotificationMarkerPosition(MediaController.PROCESSOR_TYPE_QCOM);
                    }
                    if (this.val$finalBuffersWrited == MediaController.PROCESSOR_TYPE_QCOM) {
                        MediaController.this.cleanupPlayer(true, true, true);
                    }
                }
            }
        }

        C05538() {
        }

        public void run() {
            synchronized (MediaController.this.playerObjectSync) {
                if (MediaController.this.audioTrackPlayer == null || MediaController.this.audioTrackPlayer.getPlayState() != MediaController.PROCESSOR_TYPE_MTK) {
                    return;
                }
                AudioBuffer buffer = null;
                synchronized (MediaController.this.playerSync) {
                    if (!MediaController.this.usedPlayerBuffers.isEmpty()) {
                        buffer = (AudioBuffer) MediaController.this.usedPlayerBuffers.get(MediaController.PROCESSOR_TYPE_OTHER);
                        MediaController.this.usedPlayerBuffers.remove(MediaController.PROCESSOR_TYPE_OTHER);
                    }
                }
                if (buffer != null) {
                    int count = MediaController.PROCESSOR_TYPE_OTHER;
                    try {
                        count = MediaController.this.audioTrackPlayer.write(buffer.bufferBytes, MediaController.PROCESSOR_TYPE_OTHER, buffer.size);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    MediaController.this.buffersWrited = MediaController.this.buffersWrited + MediaController.PROCESSOR_TYPE_QCOM;
                    if (count > 0) {
                        AndroidUtilities.runOnUIThread(new C05521(buffer.pcmOffset, buffer.finished == MediaController.PROCESSOR_TYPE_QCOM ? count : -1, MediaController.this.buffersWrited));
                    }
                    if (buffer.finished != MediaController.PROCESSOR_TYPE_QCOM) {
                        MediaController.this.checkPlayerQueue();
                    }
                }
                if (buffer == null || !(buffer == null || buffer.finished == MediaController.PROCESSOR_TYPE_QCOM)) {
                    MediaController.this.checkDecoderQueue();
                }
                if (buffer != null) {
                    synchronized (MediaController.this.playerSync) {
                        MediaController.this.freePlayerBuffers.add(buffer);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaController.9 */
    class C05549 implements Runnable {
        final /* synthetic */ MessageObject val$currentMessageObject;

        C05549(MessageObject messageObject) {
            this.val$currentMessageObject = messageObject;
        }

        public void run() {
            MediaController.this.pauseAudio(this.val$currentMessageObject);
        }
    }

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public boolean isVideo;
        public ArrayList<PhotoEntry> photos;
        public HashMap<Integer, PhotoEntry> photosByIds;

        public AlbumEntry(int bucketId, String bucketName, PhotoEntry coverPhoto, boolean isVideo) {
            this.photos = new ArrayList();
            this.photosByIds = new HashMap();
            this.bucketId = bucketId;
            this.bucketName = bucketName;
            this.coverPhoto = coverPhoto;
            this.isVideo = isVideo;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            this.photos.add(photoEntry);
            this.photosByIds.put(Integer.valueOf(photoEntry.imageId), photoEntry);
        }
    }

    private class AudioBuffer {
        ByteBuffer buffer;
        byte[] bufferBytes;
        int finished;
        long pcmOffset;
        int size;

        public AudioBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
            this.bufferBytes = new byte[capacity];
        }
    }

    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        public long id;
        public MessageObject messageObject;
        public String path;
        public String title;
    }

    private class ExternalObserver extends ContentObserver {
        public ExternalObserver() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(Media.EXTERNAL_CONTENT_URI);
        }
    }

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str);

        void onProgressDownload(String str, float f);

        void onProgressUpload(String str, float f, boolean z);

        void onSuccessDownload(String str);
    }

    private class GalleryObserverExternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController.GalleryObserverExternal.1 */
        class C05551 implements Runnable {
            C05551() {
            }

            public void run() {
                MediaController.this.refreshGalleryRunnable = null;
                MediaController.loadGalleryPhotosAlbums(MediaController.PROCESSOR_TYPE_OTHER);
            }
        }

        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.this.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.this.refreshGalleryRunnable = new C05551(), 2000);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {

        /* renamed from: org.telegram.messenger.MediaController.GalleryObserverInternal.1 */
        class C05561 implements Runnable {
            C05561() {
            }

            public void run() {
                MediaController.this.refreshGalleryRunnable = null;
                MediaController.loadGalleryPhotosAlbums(MediaController.PROCESSOR_TYPE_OTHER);
            }
        }

        public GalleryObserverInternal() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.this.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.this.refreshGalleryRunnable = new C05561(), 2000);
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super(null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(Media.INTERNAL_CONTENT_URI);
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public CharSequence caption;
        public long dateTaken;
        public int imageId;
        public String imagePath;
        public boolean isVideo;
        public int orientation;
        public String path;
        public String thumbPath;

        public PhotoEntry(int bucketId, int imageId, long dateTaken, String path, int orientation, boolean isVideo) {
            this.bucketId = bucketId;
            this.imageId = imageId;
            this.dateTaken = dateTaken;
            this.path = path;
            this.orientation = orientation;
            this.isVideo = isVideo;
        }
    }

    public static class SearchImage {
        public CharSequence caption;
        public int date;
        public Document document;
        public int height;
        public String id;
        public String imagePath;
        public String imageUrl;
        public String localUrl;
        public int size;
        public String thumbPath;
        public String thumbUrl;
        public int type;
        public int width;
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = MediaController.PROCESSOR_TYPE_OTHER;
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        MediaController.this.internalObserver = null;
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        MediaController.this.externalObserver = null;
                    }
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private MessageObject messageObject;

        /* renamed from: org.telegram.messenger.MediaController.VideoConvertRunnable.1 */
        static class C05571 implements Runnable {
            final /* synthetic */ MessageObject val$obj;

            C05571(MessageObject messageObject) {
                this.val$obj = messageObject;
            }

            public void run() {
                try {
                    Thread th = new Thread(new VideoConvertRunnable(null), "VideoConvertRunnable");
                    th.start();
                    th.join();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        private VideoConvertRunnable(MessageObject message) {
            this.messageObject = message;
        }

        public void run() {
            MediaController.getInstance().convertVideo(this.messageObject);
        }

        public static void runConversion(MessageObject obj) {
            new Thread(new C05571(obj)).start();
        }
    }

    private native void closeOpusFile();

    private native long getTotalPcmDuration();

    private native int isOpusFile(String str);

    private native int openOpusFile(String str);

    private native void readOpusFile(ByteBuffer byteBuffer, int i, int[] iArr);

    private native int seekOpusFile(float f);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    static /* synthetic */ long access$714(MediaController x0, long x1) {
        long j = x0.recordTimeCount + x1;
        x0.recordTimeCount = j;
        return j;
    }

    static {
        readArgs = new int[PROCESSOR_TYPE_MTK];
        projectionPhotos = new String[]{"_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation"};
        String[] strArr = new String[PROCESSOR_TYPE_TI];
        strArr[PROCESSOR_TYPE_OTHER] = "_id";
        strArr[PROCESSOR_TYPE_QCOM] = "bucket_id";
        strArr[PROCESSOR_TYPE_INTEL] = "bucket_display_name";
        strArr[PROCESSOR_TYPE_MTK] = "_data";
        strArr[PROCESSOR_TYPE_SEC] = "datetaken";
        projectionVideo = strArr;
        Instance = null;
    }

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        MediaController localInstance2 = new MediaController();
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

    public MediaController() {
        this.videoConvertSync = new Object();
        this.typingTimes = new HashMap();
        this.lastTimestamp = 0;
        this.lastProximityValue = -100.0f;
        this.gravity = new float[PROCESSOR_TYPE_MTK];
        this.gravityFast = new float[PROCESSOR_TYPE_MTK];
        this.linearAcceleration = new float[PROCESSOR_TYPE_MTK];
        this.audioFocus = PROCESSOR_TYPE_OTHER;
        this.videoConvertQueue = new ArrayList();
        this.videoQueueSync = new Object();
        this.cancelCurrentVideoConversion = false;
        this.videoConvertFirstWrite = true;
        this.generatingWaveform = new HashMap();
        this.mobileDataDownloadMask = PROCESSOR_TYPE_OTHER;
        this.wifiDownloadMask = PROCESSOR_TYPE_OTHER;
        this.roamingDownloadMask = PROCESSOR_TYPE_OTHER;
        this.lastCheckMask = PROCESSOR_TYPE_OTHER;
        this.photoDownloadQueue = new ArrayList();
        this.audioDownloadQueue = new ArrayList();
        this.documentDownloadQueue = new ArrayList();
        this.musicDownloadQueue = new ArrayList();
        this.gifDownloadQueue = new ArrayList();
        this.videoDownloadQueue = new ArrayList();
        this.downloadQueueKeys = new HashMap();
        this.saveToGallery = true;
        this.autoplayGifs = true;
        this.raiseToSpeak = true;
        this.customTabs = true;
        this.directShare = true;
        this.loadingFileObservers = new HashMap();
        this.loadingFileMessagesObservers = new HashMap();
        this.observersByTag = new HashMap();
        this.listenerInProgress = false;
        this.addLaterArray = new HashMap();
        this.deleteLaterArray = new ArrayList();
        this.lastTag = PROCESSOR_TYPE_OTHER;
        this.isPaused = false;
        this.audioPlayer = null;
        this.audioTrackPlayer = null;
        this.lastProgress = PROCESSOR_TYPE_OTHER;
        this.playerBufferSize = PROCESSOR_TYPE_OTHER;
        this.decodingFinished = false;
        this.ignoreFirstProgress = PROCESSOR_TYPE_OTHER;
        this.progressTimer = null;
        this.progressTimerSync = new Object();
        this.playlist = new ArrayList();
        this.shuffledPlaylist = new ArrayList();
        this.audioRecorder = null;
        this.recordingAudio = null;
        this.recordingAudioFile = null;
        this.usedPlayerBuffers = new ArrayList();
        this.freePlayerBuffers = new ArrayList();
        this.playerSync = new Object();
        this.playerObjectSync = new Object();
        this.recordSamples = new short[MessagesController.UPDATE_MASK_PHONE];
        this.sync = new Object();
        this.recordBuffers = new ArrayList();
        this.recordRunnable = new C05401();
        this.externalObserver = null;
        this.internalObserver = null;
        this.lastSecretChatEnterTime = 0;
        this.lastSecretChatLeaveTime = 0;
        this.lastMediaCheckTime = 0;
        this.lastSecretChat = null;
        this.lastSecretChatVisibleMessages = null;
        this.startObserverToken = PROCESSOR_TYPE_OTHER;
        this.stopMediaObserverRunnable = null;
        this.mediaProjections = null;
        try {
            int a;
            this.recordBufferSize = AudioRecord.getMinBufferSize(16000, AUTODOWNLOAD_MASK_MUSIC, PROCESSOR_TYPE_INTEL);
            if (this.recordBufferSize <= 0) {
                this.recordBufferSize = 1280;
            }
            this.playerBufferSize = AudioTrack.getMinBufferSize(48000, PROCESSOR_TYPE_SEC, PROCESSOR_TYPE_INTEL);
            if (this.playerBufferSize <= 0) {
                this.playerBufferSize = 3840;
            }
            for (a = PROCESSOR_TYPE_OTHER; a < PROCESSOR_TYPE_TI; a += PROCESSOR_TYPE_QCOM) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(MessagesController.UPDATE_MASK_SEND_STATE);
                buffer.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(buffer);
            }
            for (a = PROCESSOR_TYPE_OTHER; a < PROCESSOR_TYPE_MTK; a += PROCESSOR_TYPE_QCOM) {
                this.freePlayerBuffers.add(new AudioBuffer(this.playerBufferSize));
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            this.sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.linearSensor = this.sensorManager.getDefaultSensor(10);
            this.gravitySensor = this.sensorManager.getDefaultSensor(9);
            if (this.linearSensor == null || this.gravitySensor == null) {
                FileLog.m11e("tmessages", "gravity or linear sensor not found");
                this.accelerometerSensor = this.sensorManager.getDefaultSensor(PROCESSOR_TYPE_QCOM);
                this.linearSensor = null;
                this.gravitySensor = null;
            }
            this.proximitySensor = this.sensorManager.getDefaultSensor(AUTODOWNLOAD_MASK_DOCUMENT);
            this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(AUTODOWNLOAD_MASK_GIF, "proximity");
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        this.recordQueue = new DispatchQueue("recordQueue");
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.playerQueue = new DispatchQueue("playerQueue");
        this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER);
        this.mobileDataDownloadMask = preferences.getInt("mobileDataDownloadMask", 51);
        this.wifiDownloadMask = preferences.getInt("wifiDownloadMask", 51);
        this.roamingDownloadMask = preferences.getInt("roamingDownloadMask", PROCESSOR_TYPE_OTHER);
        this.saveToGallery = preferences.getBoolean("save_gallery", false);
        this.autoplayGifs = preferences.getBoolean("autoplay_gif", true);
        this.raiseToSpeak = preferences.getBoolean("raise_to_speak", true);
        this.customTabs = preferences.getBoolean("custom_tabs", true);
        this.directShare = preferences.getBoolean("direct_share", true);
        this.shuffleMusic = preferences.getBoolean("shuffleMusic", false);
        this.repeatMode = preferences.getInt("repeatMode", PROCESSOR_TYPE_OTHER);
        AndroidUtilities.runOnUIThread(new C05452());
        ApplicationLoader.applicationContext.registerReceiver(new C05463(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (UserConfig.isClientActivated()) {
            checkAutodownloadSettings();
        }
        if (VERSION.SDK_INT >= AUTODOWNLOAD_MASK_MUSIC) {
            this.mediaProjections = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height"};
        } else {
            String[] strArr = new String[PROCESSOR_TYPE_TI];
            strArr[PROCESSOR_TYPE_OTHER] = "_data";
            strArr[PROCESSOR_TYPE_QCOM] = "_display_name";
            strArr[PROCESSOR_TYPE_INTEL] = "bucket_display_name";
            strArr[PROCESSOR_TYPE_MTK] = "datetaken";
            strArr[PROCESSOR_TYPE_SEC] = "title";
            this.mediaProjections = strArr;
        }
        try {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, false, new GalleryObserverExternal());
        } catch (Throwable e22) {
            FileLog.m13e("tmessages", e22);
        }
        try {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Media.INTERNAL_CONTENT_URI, false, new GalleryObserverInternal());
        } catch (Throwable e222) {
            FileLog.m13e("tmessages", e222);
        }
        try {
            PhoneStateListener phoneStateListener = new C05474();
            TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (mgr != null) {
                mgr.listen(phoneStateListener, AUTODOWNLOAD_MASK_GIF);
            }
        } catch (Throwable e2222) {
            FileLog.m13e("tmessages", e2222);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingAudio(getPlayingMessageObject()) && !isAudioPaused()) {
                pauseAudio(getPlayingMessageObject());
            }
            this.hasAudioFocus = PROCESSOR_TYPE_OTHER;
            this.audioFocus = PROCESSOR_TYPE_OTHER;
        } else if (focusChange == PROCESSOR_TYPE_QCOM) {
            this.audioFocus = PROCESSOR_TYPE_INTEL;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                if (isPlayingAudio(getPlayingMessageObject()) && isAudioPaused()) {
                    playAudio(getPlayingMessageObject());
                }
            }
        } else if (focusChange == -3) {
            this.audioFocus = PROCESSOR_TYPE_QCOM;
        } else if (focusChange == -2) {
            this.audioFocus = PROCESSOR_TYPE_OTHER;
            if (isPlayingAudio(getPlayingMessageObject()) && !isAudioPaused()) {
                pauseAudio(getPlayingMessageObject());
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    private void setPlayerVolume() {
        try {
            float volume;
            if (this.audioFocus != PROCESSOR_TYPE_QCOM) {
                volume = VOLUME_NORMAL;
            } else {
                volume = VOLUME_DUCK;
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(volume, volume);
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.setStereoVolume(volume, volume);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private void startProgressTimer(MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            this.progressTimer = new Timer();
            this.progressTimer.schedule(new C05495(currentPlayingMessageObject), 0, 17);
        }
    }

    private void stopProgressTimer() {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.documentDownloadQueue.clear();
        this.videoDownloadQueue.clear();
        this.musicDownloadQueue.clear();
        this.gifDownloadQueue.clear();
        this.downloadQueueKeys.clear();
        this.videoConvertQueue.clear();
        this.playlist.clear();
        this.shuffledPlaylist.clear();
        this.generatingWaveform.clear();
        this.typingTimes.clear();
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        cancelVideoConvert(null);
    }

    protected int getAutodownloadMask() {
        int mask = PROCESSOR_TYPE_OTHER;
        if (!((this.mobileDataDownloadMask & PROCESSOR_TYPE_QCOM) == 0 && (this.wifiDownloadMask & PROCESSOR_TYPE_QCOM) == 0 && (this.roamingDownloadMask & PROCESSOR_TYPE_QCOM) == 0)) {
            mask = PROCESSOR_TYPE_OTHER | PROCESSOR_TYPE_QCOM;
        }
        if (!((this.mobileDataDownloadMask & PROCESSOR_TYPE_INTEL) == 0 && (this.wifiDownloadMask & PROCESSOR_TYPE_INTEL) == 0 && (this.roamingDownloadMask & PROCESSOR_TYPE_INTEL) == 0)) {
            mask |= PROCESSOR_TYPE_INTEL;
        }
        if (!((this.mobileDataDownloadMask & PROCESSOR_TYPE_SEC) == 0 && (this.wifiDownloadMask & PROCESSOR_TYPE_SEC) == 0 && (this.roamingDownloadMask & PROCESSOR_TYPE_SEC) == 0)) {
            mask |= PROCESSOR_TYPE_SEC;
        }
        if (!((this.mobileDataDownloadMask & AUTODOWNLOAD_MASK_DOCUMENT) == 0 && (this.wifiDownloadMask & AUTODOWNLOAD_MASK_DOCUMENT) == 0 && (this.roamingDownloadMask & AUTODOWNLOAD_MASK_DOCUMENT) == 0)) {
            mask |= AUTODOWNLOAD_MASK_DOCUMENT;
        }
        if (!((this.mobileDataDownloadMask & AUTODOWNLOAD_MASK_MUSIC) == 0 && (this.wifiDownloadMask & AUTODOWNLOAD_MASK_MUSIC) == 0 && (this.roamingDownloadMask & AUTODOWNLOAD_MASK_MUSIC) == 0)) {
            mask |= AUTODOWNLOAD_MASK_MUSIC;
        }
        if ((this.mobileDataDownloadMask & AUTODOWNLOAD_MASK_GIF) == 0 && (this.wifiDownloadMask & AUTODOWNLOAD_MASK_GIF) == 0 && (this.roamingDownloadMask & AUTODOWNLOAD_MASK_GIF) == 0) {
            return mask;
        }
        return mask | AUTODOWNLOAD_MASK_GIF;
    }

    public void checkAutodownloadSettings() {
        int currentMask = getCurrentDownloadMask();
        if (currentMask != this.lastCheckMask) {
            int a;
            this.lastCheckMask = currentMask;
            if ((currentMask & PROCESSOR_TYPE_QCOM) == 0) {
                for (a = PROCESSOR_TYPE_OTHER; a < this.photoDownloadQueue.size(); a += PROCESSOR_TYPE_QCOM) {
                    FileLoader.getInstance().cancelLoadFile((PhotoSize) ((DownloadObject) this.photoDownloadQueue.get(a)).object);
                }
                this.photoDownloadQueue.clear();
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(PROCESSOR_TYPE_QCOM);
            }
            if ((currentMask & PROCESSOR_TYPE_INTEL) == 0) {
                for (a = PROCESSOR_TYPE_OTHER; a < this.audioDownloadQueue.size(); a += PROCESSOR_TYPE_QCOM) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.audioDownloadQueue.get(a)).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(PROCESSOR_TYPE_INTEL);
            }
            if ((currentMask & AUTODOWNLOAD_MASK_DOCUMENT) == 0) {
                for (a = PROCESSOR_TYPE_OTHER; a < this.documentDownloadQueue.size(); a += PROCESSOR_TYPE_QCOM) {
                    FileLoader.getInstance().cancelLoadFile(((DownloadObject) this.documentDownloadQueue.get(a)).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(AUTODOWNLOAD_MASK_DOCUMENT);
            }
            if ((currentMask & PROCESSOR_TYPE_SEC) == 0) {
                for (a = PROCESSOR_TYPE_OTHER; a < this.videoDownloadQueue.size(); a += PROCESSOR_TYPE_QCOM) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.videoDownloadQueue.get(a)).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(PROCESSOR_TYPE_SEC);
            }
            if ((currentMask & AUTODOWNLOAD_MASK_MUSIC) == 0) {
                for (a = PROCESSOR_TYPE_OTHER; a < this.musicDownloadQueue.size(); a += PROCESSOR_TYPE_QCOM) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.musicDownloadQueue.get(a)).object);
                }
                this.musicDownloadQueue.clear();
            } else if (this.musicDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(AUTODOWNLOAD_MASK_MUSIC);
            }
            if ((currentMask & AUTODOWNLOAD_MASK_GIF) == 0) {
                for (a = PROCESSOR_TYPE_OTHER; a < this.gifDownloadQueue.size(); a += PROCESSOR_TYPE_QCOM) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.gifDownloadQueue.get(a)).object);
                }
                this.gifDownloadQueue.clear();
            } else if (this.gifDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(AUTODOWNLOAD_MASK_GIF);
            }
            int mask = getAutodownloadMask();
            if (mask == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(PROCESSOR_TYPE_OTHER);
                return;
            }
            if ((mask & PROCESSOR_TYPE_QCOM) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(PROCESSOR_TYPE_QCOM);
            }
            if ((mask & PROCESSOR_TYPE_INTEL) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(PROCESSOR_TYPE_INTEL);
            }
            if ((mask & PROCESSOR_TYPE_SEC) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(PROCESSOR_TYPE_SEC);
            }
            if ((mask & AUTODOWNLOAD_MASK_DOCUMENT) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(AUTODOWNLOAD_MASK_DOCUMENT);
            }
            if ((mask & AUTODOWNLOAD_MASK_MUSIC) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(AUTODOWNLOAD_MASK_MUSIC);
            }
            if ((mask & AUTODOWNLOAD_MASK_GIF) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(AUTODOWNLOAD_MASK_GIF);
            }
        }
    }

    public boolean canDownloadMedia(int type) {
        return (getCurrentDownloadMask() & type) != 0;
    }

    private int getCurrentDownloadMask() {
        if (ConnectionsManager.isConnectedToWiFi()) {
            return this.wifiDownloadMask;
        }
        if (ConnectionsManager.isRoaming()) {
            return this.roamingDownloadMask;
        }
        return this.mobileDataDownloadMask;
    }

    protected void processDownloadObjects(int type, ArrayList<DownloadObject> objects) {
        if (!objects.isEmpty()) {
            ArrayList<DownloadObject> queue = null;
            if (type == PROCESSOR_TYPE_QCOM) {
                queue = this.photoDownloadQueue;
            } else if (type == PROCESSOR_TYPE_INTEL) {
                queue = this.audioDownloadQueue;
            } else if (type == PROCESSOR_TYPE_SEC) {
                queue = this.videoDownloadQueue;
            } else if (type == AUTODOWNLOAD_MASK_DOCUMENT) {
                queue = this.documentDownloadQueue;
            } else if (type == AUTODOWNLOAD_MASK_MUSIC) {
                queue = this.musicDownloadQueue;
            } else if (type == AUTODOWNLOAD_MASK_GIF) {
                queue = this.gifDownloadQueue;
            }
            for (int a = PROCESSOR_TYPE_OTHER; a < objects.size(); a += PROCESSOR_TYPE_QCOM) {
                String path;
                DownloadObject downloadObject = (DownloadObject) objects.get(a);
                if (downloadObject.object instanceof Document) {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                } else {
                    path = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.downloadQueueKeys.containsKey(path)) {
                    boolean added = true;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance().loadFile((PhotoSize) downloadObject.object, null, false);
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance().loadFile((Document) downloadObject.object, false, false);
                    } else {
                        added = false;
                    }
                    if (added) {
                        queue.add(downloadObject);
                        this.downloadQueueKeys.put(path, downloadObject);
                    }
                }
            }
        }
    }

    protected void newDownloadObjectsAvailable(int downloadMask) {
        int mask = getCurrentDownloadMask();
        if (!((mask & PROCESSOR_TYPE_QCOM) == 0 || (downloadMask & PROCESSOR_TYPE_QCOM) == 0 || !this.photoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(PROCESSOR_TYPE_QCOM);
        }
        if (!((mask & PROCESSOR_TYPE_INTEL) == 0 || (downloadMask & PROCESSOR_TYPE_INTEL) == 0 || !this.audioDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(PROCESSOR_TYPE_INTEL);
        }
        if (!((mask & PROCESSOR_TYPE_SEC) == 0 || (downloadMask & PROCESSOR_TYPE_SEC) == 0 || !this.videoDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(PROCESSOR_TYPE_SEC);
        }
        if (!((mask & AUTODOWNLOAD_MASK_DOCUMENT) == 0 || (downloadMask & AUTODOWNLOAD_MASK_DOCUMENT) == 0 || !this.documentDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(AUTODOWNLOAD_MASK_DOCUMENT);
        }
        if (!((mask & AUTODOWNLOAD_MASK_MUSIC) == 0 || (downloadMask & AUTODOWNLOAD_MASK_MUSIC) == 0 || !this.musicDownloadQueue.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(AUTODOWNLOAD_MASK_MUSIC);
        }
        if ((mask & AUTODOWNLOAD_MASK_GIF) != 0 && (downloadMask & AUTODOWNLOAD_MASK_GIF) != 0 && this.gifDownloadQueue.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(AUTODOWNLOAD_MASK_GIF);
        }
    }

    private void checkDownloadFinished(String fileName, int state) {
        DownloadObject downloadObject = (DownloadObject) this.downloadQueueKeys.get(fileName);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(fileName);
            if (state == 0 || state == PROCESSOR_TYPE_INTEL) {
                MessagesStorage.getInstance().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type == PROCESSOR_TYPE_QCOM) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(PROCESSOR_TYPE_QCOM);
                }
            } else if (downloadObject.type == PROCESSOR_TYPE_INTEL) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(PROCESSOR_TYPE_INTEL);
                }
            } else if (downloadObject.type == PROCESSOR_TYPE_SEC) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(PROCESSOR_TYPE_SEC);
                }
            } else if (downloadObject.type == AUTODOWNLOAD_MASK_DOCUMENT) {
                this.documentDownloadQueue.remove(downloadObject);
                if (this.documentDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_MASK_DOCUMENT);
                }
            } else if (downloadObject.type == AUTODOWNLOAD_MASK_MUSIC) {
                this.musicDownloadQueue.remove(downloadObject);
                if (this.musicDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_MASK_MUSIC);
                }
            } else if (downloadObject.type == AUTODOWNLOAD_MASK_GIF) {
                this.gifDownloadQueue.remove(downloadObject);
                if (this.gifDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_MASK_GIF);
                }
            }
        }
    }

    public void startMediaObserver() {
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken += PROCESSOR_TYPE_QCOM;
        try {
            if (this.internalObserver == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = Media.EXTERNAL_CONTENT_URI;
                ContentObserver externalObserver = new ExternalObserver();
                this.externalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        try {
            if (this.externalObserver == null) {
                contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                uri = Media.INTERNAL_CONTENT_URI;
                externalObserver = new InternalObserver();
                this.internalObserver = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable();
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    public void processMediaObserver(Uri uri) {
        try {
            Point size = AndroidUtilities.getRealScreenSize();
            Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
            ArrayList<Long> screenshotDates = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String val = TtmlNode.ANONYMOUS_REGION_ID;
                    String data = cursor.getString(PROCESSOR_TYPE_OTHER);
                    String display_name = cursor.getString(PROCESSOR_TYPE_QCOM);
                    String album_name = cursor.getString(PROCESSOR_TYPE_INTEL);
                    long date = cursor.getLong(PROCESSOR_TYPE_MTK);
                    String title = cursor.getString(PROCESSOR_TYPE_SEC);
                    int photoW = PROCESSOR_TYPE_OTHER;
                    int photoH = PROCESSOR_TYPE_OTHER;
                    if (VERSION.SDK_INT >= AUTODOWNLOAD_MASK_MUSIC) {
                        photoW = cursor.getInt(PROCESSOR_TYPE_TI);
                        photoH = cursor.getInt(6);
                    }
                    if ((data != null && data.toLowerCase().contains("screenshot")) || ((display_name != null && display_name.toLowerCase().contains("screenshot")) || ((album_name != null && album_name.toLowerCase().contains("screenshot")) || (title != null && title.toLowerCase().contains("screenshot"))))) {
                        if (photoW == 0 || photoH == 0) {
                            try {
                                Options bmOptions = new Options();
                                bmOptions.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(data, bmOptions);
                                photoW = bmOptions.outWidth;
                                photoH = bmOptions.outHeight;
                            } catch (Exception e) {
                                screenshotDates.add(Long.valueOf(date));
                            }
                        }
                        if (photoW <= 0 || photoH <= 0 || ((photoW == size.x && photoH == size.y) || (photoH == size.x && photoW == size.y))) {
                            screenshotDates.add(Long.valueOf(date));
                        }
                    }
                }
                cursor.close();
            }
            if (!screenshotDates.isEmpty()) {
                AndroidUtilities.runOnUIThread(new C05506(screenshotDates));
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates != null && !dates.isEmpty() && this.lastSecretChatEnterTime != 0 && this.lastSecretChat != null && (this.lastSecretChat instanceof TL_encryptedChat)) {
            boolean send = false;
            Iterator i$ = dates.iterator();
            while (i$.hasNext()) {
                Long date = (Long) i$.next();
                if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastSecretChatEnterTime) {
                    if (this.lastSecretChatLeaveTime == 0 || date.longValue() <= this.lastSecretChatLeaveTime + 2000) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                        send = true;
                    }
                }
            }
            if (send) {
                SecretChatHelper.getInstance().sendScreenshotMessage(this.lastSecretChat, this.lastSecretChatVisibleMessages, null);
            }
        }
    }

    public void setLastEncryptedChatParams(long enterTime, long leaveTime, EncryptedChat encryptedChat, ArrayList<Long> visibleMessages) {
        this.lastSecretChatEnterTime = enterTime;
        this.lastSecretChatLeaveTime = leaveTime;
        this.lastSecretChat = encryptedChat;
        this.lastSecretChatVisibleMessages = visibleMessages;
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + PROCESSOR_TYPE_QCOM;
        return i;
    }

    public void addLoadingFileObserver(String fileName, FileDownloadProgressListener observer) {
        addLoadingFileObserver(fileName, null, observer);
    }

    public void addLoadingFileObserver(String fileName, MessageObject messageObject, FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(fileName, observer);
            return;
        }
        removeLoadingFileObserver(observer);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.loadingFileObservers.put(fileName, arrayList);
        }
        arrayList.add(new WeakReference(observer));
        if (messageObject != null) {
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects == null) {
                messageObjects = new ArrayList();
                this.loadingFileMessagesObservers.put(fileName, messageObjects);
            }
            messageObjects.add(messageObject);
        }
        this.observersByTag.put(Integer.valueOf(observer.getObserverTag()), fileName);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(observer);
            return;
        }
        String fileName = (String) this.observersByTag.get(Integer.valueOf(observer.getObserverTag()));
        if (fileName != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int a = PROCESSOR_TYPE_OTHER;
                while (a < arrayList.size()) {
                    WeakReference<FileDownloadProgressListener> reference = (WeakReference) arrayList.get(a);
                    if (reference.get() == null || reference.get() == observer) {
                        arrayList.remove(a);
                        a--;
                    }
                    a += PROCESSOR_TYPE_QCOM;
                }
                if (arrayList.isEmpty()) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.observersByTag.remove(Integer.valueOf(observer.getObserverTag()));
        }
    }

    private void processLaterArrays() {
        for (Entry<String, FileDownloadProgressListener> listener : this.addLaterArray.entrySet()) {
            addLoadingFileObserver((String) listener.getKey(), (FileDownloadProgressListener) listener.getValue());
        }
        this.addLaterArray.clear();
        Iterator i$ = this.deleteLaterArray.iterator();
        while (i$.hasNext()) {
            removeLoadingFileObserver((FileDownloadProgressListener) i$.next());
        }
        this.deleteLaterArray.clear();
    }

    public void didReceivedNotification(int id, Object... args) {
        String fileName;
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList;
        int a;
        WeakReference<FileDownloadProgressListener> reference;
        if (id == NotificationCenter.FileDidFailedLoad) {
            this.listenerInProgress = true;
            fileName = args[PROCESSOR_TYPE_OTHER];
            this.loadingFileMessagesObservers.get(fileName);
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (a = PROCESSOR_TYPE_OTHER; a < arrayList.size(); a += PROCESSOR_TYPE_QCOM) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onFailedDownload(fileName);
                        this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener) reference.get()).getObserverTag()));
                    }
                }
                this.loadingFileObservers.remove(fileName);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, ((Integer) args[PROCESSOR_TYPE_QCOM]).intValue());
        } else if (id == NotificationCenter.FileDidLoaded) {
            this.listenerInProgress = true;
            fileName = (String) args[PROCESSOR_TYPE_OTHER];
            if (this.downloadingCurrentMessage && this.playingMessageObject != null) {
                if (FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(fileName)) {
                    this.playMusicAgain = true;
                    playAudio(this.playingMessageObject);
                }
            }
            ArrayList<MessageObject> messageObjects = (ArrayList) this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects != null) {
                for (a = PROCESSOR_TYPE_OTHER; a < messageObjects.size(); a += PROCESSOR_TYPE_QCOM) {
                    ((MessageObject) messageObjects.get(a)).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(fileName);
            }
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (a = PROCESSOR_TYPE_OTHER; a < arrayList.size(); a += PROCESSOR_TYPE_QCOM) {
                    reference = (WeakReference) arrayList.get(a);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onSuccessDownload(fileName);
                        this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener) reference.get()).getObserverTag()));
                    }
                }
                this.loadingFileObservers.remove(fileName);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, PROCESSOR_TYPE_OTHER);
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            this.listenerInProgress = true;
            fileName = (String) args[PROCESSOR_TYPE_OTHER];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                progress = args[PROCESSOR_TYPE_QCOM];
                i$ = arrayList.iterator();
                while (i$.hasNext()) {
                    reference = (WeakReference) i$.next();
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onProgressDownload(fileName, progress.floatValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (id == NotificationCenter.FileUploadProgressChanged) {
            this.listenerInProgress = true;
            fileName = (String) args[PROCESSOR_TYPE_OTHER];
            arrayList = (ArrayList) this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                progress = (Float) args[PROCESSOR_TYPE_QCOM];
                Boolean enc = args[PROCESSOR_TYPE_INTEL];
                i$ = arrayList.iterator();
                while (i$.hasNext()) {
                    reference = (WeakReference) i$.next();
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onProgressUpload(fileName, progress.floatValue(), enc.booleanValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<DelayedMessage> delayedMessages = SendMessagesHelper.getInstance().getDelayedMessages(fileName);
                if (delayedMessages != null) {
                    for (a = PROCESSOR_TYPE_OTHER; a < delayedMessages.size(); a += PROCESSOR_TYPE_QCOM) {
                        DelayedMessage delayedMessage = (DelayedMessage) delayedMessages.get(a);
                        if (delayedMessage.encryptedChat == null) {
                            long dialog_id = delayedMessage.obj.getDialogId();
                            Long lastTime = (Long) this.typingTimes.get(Long.valueOf(dialog_id));
                            if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                if (MessageObject.isVideoDocument(delayedMessage.documentLocation)) {
                                    MessagesController.getInstance().sendTyping(dialog_id, PROCESSOR_TYPE_TI, PROCESSOR_TYPE_OTHER);
                                } else if (delayedMessage.documentLocation != null) {
                                    MessagesController.getInstance().sendTyping(dialog_id, PROCESSOR_TYPE_MTK, PROCESSOR_TYPE_OTHER);
                                } else if (delayedMessage.location != null) {
                                    MessagesController.getInstance().sendTyping(dialog_id, PROCESSOR_TYPE_SEC, PROCESSOR_TYPE_OTHER);
                                }
                                this.typingTimes.put(Long.valueOf(dialog_id), Long.valueOf(System.currentTimeMillis()));
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            int i;
            int channelId = ((Integer) args[PROCESSOR_TYPE_QCOM]).intValue();
            ArrayList<Integer> markAsDeletedMessages = args[PROCESSOR_TYPE_OTHER];
            if (this.playingMessageObject != null) {
                i = this.playingMessageObject.messageOwner.to_id.channel_id;
                if (channelId == r0) {
                    if (markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                        cleanupPlayer(true, true);
                    }
                }
            }
            if (this.voiceMessagesPlaylist != null) {
                if (!this.voiceMessagesPlaylist.isEmpty()) {
                    i = ((MessageObject) this.voiceMessagesPlaylist.get(PROCESSOR_TYPE_OTHER)).messageOwner.to_id.channel_id;
                    if (channelId == r0) {
                        for (a = PROCESSOR_TYPE_OTHER; a < markAsDeletedMessages.size(); a += PROCESSOR_TYPE_QCOM) {
                            messageObject = (MessageObject) this.voiceMessagesPlaylistMap.remove(markAsDeletedMessages.get(a));
                            if (messageObject != null) {
                                this.voiceMessagesPlaylist.remove(messageObject);
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            did = ((Long) args[PROCESSOR_TYPE_OTHER]).longValue();
            if (this.playingMessageObject != null) {
                if (this.playingMessageObject.getDialogId() == did) {
                    cleanupPlayer(false, true);
                }
            }
        } else if (id == NotificationCenter.musicDidLoaded) {
            did = ((Long) args[PROCESSOR_TYPE_OTHER]).longValue();
            if (this.playingMessageObject != null) {
                if (this.playingMessageObject.isMusic()) {
                    if (this.playingMessageObject.getDialogId() == did) {
                        ArrayList<MessageObject> arrayList2 = args[PROCESSOR_TYPE_QCOM];
                        this.playlist.addAll(PROCESSOR_TYPE_OTHER, arrayList2);
                        if (this.shuffleMusic) {
                            buildShuffledPlayList();
                            this.currentPlaylistNum = PROCESSOR_TYPE_OTHER;
                            return;
                        }
                        this.currentPlaylistNum += arrayList2.size();
                    }
                }
            }
        } else if (id == NotificationCenter.didReceivedNewMessages && this.voiceMessagesPlaylist != null) {
            if (!this.voiceMessagesPlaylist.isEmpty()) {
                if (((Long) args[PROCESSOR_TYPE_OTHER]).longValue() == ((MessageObject) this.voiceMessagesPlaylist.get(PROCESSOR_TYPE_OTHER)).getDialogId()) {
                    ArrayList<MessageObject> arr = args[PROCESSOR_TYPE_QCOM];
                    for (a = PROCESSOR_TYPE_OTHER; a < arr.size(); a += PROCESSOR_TYPE_QCOM) {
                        messageObject = (MessageObject) arr.get(a);
                        if (messageObject.isVoice() && (!this.voiceMessagesPlaylistUnread || (messageObject.isContentUnread() && !messageObject.isOut()))) {
                            this.voiceMessagesPlaylist.add(messageObject);
                            this.voiceMessagesPlaylistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                        }
                    }
                }
            }
        }
    }

    private void checkDecoderQueue() {
        this.fileDecodingQueue.postRunnable(new C05517());
    }

    private void checkPlayerQueue() {
        this.playerQueue.postRunnable(new C05538());
    }

    protected boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
    }

    public void onSensorChanged(SensorEvent event) {
        if (this.sensorsStarted) {
            if (event.sensor == this.proximitySensor) {
                FileLog.m11e("tmessages", "proximity changed to " + event.values[PROCESSOR_TYPE_OTHER]);
                if (this.lastProximityValue == -100.0f) {
                    this.lastProximityValue = event.values[PROCESSOR_TYPE_OTHER];
                } else if (this.lastProximityValue != event.values[PROCESSOR_TYPE_OTHER]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(event.values[PROCESSOR_TYPE_OTHER]);
                }
            } else if (event.sensor == this.accelerometerSensor) {
                double alpha;
                if (this.lastTimestamp == 0) {
                    alpha = 0.9800000190734863d;
                } else {
                    alpha = 1.0d / (1.0d + (((double) (event.timestamp - this.lastTimestamp)) / 1.0E9d));
                }
                this.lastTimestamp = event.timestamp;
                this.gravity[PROCESSOR_TYPE_OTHER] = (float) ((((double) this.gravity[PROCESSOR_TYPE_OTHER]) * alpha) + ((1.0d - alpha) * ((double) event.values[PROCESSOR_TYPE_OTHER])));
                this.gravity[PROCESSOR_TYPE_QCOM] = (float) ((((double) this.gravity[PROCESSOR_TYPE_QCOM]) * alpha) + ((1.0d - alpha) * ((double) event.values[PROCESSOR_TYPE_QCOM])));
                this.gravity[PROCESSOR_TYPE_INTEL] = (float) ((((double) this.gravity[PROCESSOR_TYPE_INTEL]) * alpha) + ((1.0d - alpha) * ((double) event.values[PROCESSOR_TYPE_INTEL])));
                this.gravityFast[PROCESSOR_TYPE_OTHER] = (DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.gravity[PROCESSOR_TYPE_OTHER]) + (0.19999999f * event.values[PROCESSOR_TYPE_OTHER]);
                this.gravityFast[PROCESSOR_TYPE_QCOM] = (DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.gravity[PROCESSOR_TYPE_QCOM]) + (0.19999999f * event.values[PROCESSOR_TYPE_QCOM]);
                this.gravityFast[PROCESSOR_TYPE_INTEL] = (DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.gravity[PROCESSOR_TYPE_INTEL]) + (0.19999999f * event.values[PROCESSOR_TYPE_INTEL]);
                this.linearAcceleration[PROCESSOR_TYPE_OTHER] = event.values[PROCESSOR_TYPE_OTHER] - this.gravity[PROCESSOR_TYPE_OTHER];
                this.linearAcceleration[PROCESSOR_TYPE_QCOM] = event.values[PROCESSOR_TYPE_QCOM] - this.gravity[PROCESSOR_TYPE_QCOM];
                this.linearAcceleration[PROCESSOR_TYPE_INTEL] = event.values[PROCESSOR_TYPE_INTEL] - this.gravity[PROCESSOR_TYPE_INTEL];
            } else if (event.sensor == this.linearSensor) {
                this.linearAcceleration[PROCESSOR_TYPE_OTHER] = event.values[PROCESSOR_TYPE_OTHER];
                this.linearAcceleration[PROCESSOR_TYPE_QCOM] = event.values[PROCESSOR_TYPE_QCOM];
                this.linearAcceleration[PROCESSOR_TYPE_INTEL] = event.values[PROCESSOR_TYPE_INTEL];
            } else if (event.sensor == this.gravitySensor) {
                float[] fArr = this.gravityFast;
                float[] fArr2 = this.gravity;
                float f = event.values[PROCESSOR_TYPE_OTHER];
                fArr2[PROCESSOR_TYPE_OTHER] = f;
                fArr[PROCESSOR_TYPE_OTHER] = f;
                fArr = this.gravityFast;
                fArr2 = this.gravity;
                f = event.values[PROCESSOR_TYPE_QCOM];
                fArr2[PROCESSOR_TYPE_QCOM] = f;
                fArr[PROCESSOR_TYPE_QCOM] = f;
                fArr = this.gravityFast;
                fArr2 = this.gravity;
                f = event.values[PROCESSOR_TYPE_INTEL];
                fArr2[PROCESSOR_TYPE_INTEL] = f;
                fArr[PROCESSOR_TYPE_INTEL] = f;
            }
            if (event.sensor == this.linearSensor || event.sensor == this.gravitySensor || event.sensor == this.accelerometerSensor) {
                float val = ((this.gravity[PROCESSOR_TYPE_OTHER] * this.linearAcceleration[PROCESSOR_TYPE_OTHER]) + (this.gravity[PROCESSOR_TYPE_QCOM] * this.linearAcceleration[PROCESSOR_TYPE_QCOM])) + (this.gravity[PROCESSOR_TYPE_INTEL] * this.linearAcceleration[PROCESSOR_TYPE_INTEL]);
                if (this.raisedToBack != 6) {
                    if (val <= 0.0f || this.previousAccValue <= 0.0f) {
                        if (val < 0.0f && this.previousAccValue < 0.0f) {
                            if (this.raisedToTop != 6 || val >= -15.0f) {
                                if (val > -15.0f) {
                                    this.countLess += PROCESSOR_TYPE_QCOM;
                                }
                                if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                    this.raisedToTop = PROCESSOR_TYPE_OTHER;
                                    this.raisedToBack = PROCESSOR_TYPE_OTHER;
                                    this.countLess = PROCESSOR_TYPE_OTHER;
                                }
                            } else if (this.raisedToBack < 6) {
                                this.raisedToBack += PROCESSOR_TYPE_QCOM;
                                if (this.raisedToBack == 6) {
                                    this.raisedToTop = PROCESSOR_TYPE_OTHER;
                                    this.countLess = PROCESSOR_TYPE_OTHER;
                                    this.timeSinceRaise = System.currentTimeMillis();
                                }
                            }
                        }
                    } else if (val <= 15.0f || this.raisedToBack != 0) {
                        if (val < 15.0f) {
                            this.countLess += PROCESSOR_TYPE_QCOM;
                        }
                        if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                            this.raisedToBack = PROCESSOR_TYPE_OTHER;
                            this.raisedToTop = PROCESSOR_TYPE_OTHER;
                            this.countLess = PROCESSOR_TYPE_OTHER;
                        }
                    } else if (this.raisedToTop < 6 && !this.proximityTouched) {
                        this.raisedToTop += PROCESSOR_TYPE_QCOM;
                        if (this.raisedToTop == 6) {
                            this.countLess = PROCESSOR_TYPE_OTHER;
                        }
                    }
                }
                this.previousAccValue = val;
                boolean z = this.gravityFast[PROCESSOR_TYPE_QCOM] > 2.5f && Math.abs(this.gravityFast[PROCESSOR_TYPE_INTEL]) < 4.0f && Math.abs(this.gravityFast[PROCESSOR_TYPE_OTHER]) > 1.5f;
                this.accelerometerVertical = z;
            }
            if (this.raisedToBack == 6 && this.accelerometerVertical && this.proximityTouched && !NotificationsController.getInstance().audioManager.isWiredHeadsetOn()) {
                FileLog.m11e("tmessages", "sensor values reached");
                if (this.playingMessageObject == null && this.recordStartRunnable == null && this.recordingAudio == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.inputFieldHasText && this.allowStartRecord && this.raiseChat != null && !this.callInProgress) {
                    if (!this.raiseToEarRecord) {
                        FileLog.m11e("tmessages", "start record");
                        this.useFrontSpeaker = true;
                        if (!this.raiseChat.playFirstUnreadVoiceMessage()) {
                            this.raiseToEarRecord = true;
                            this.useFrontSpeaker = false;
                            startRecording(this.raiseChat.getDialogId(), null);
                        }
                        this.ignoreOnPause = true;
                        if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                            this.proximityWakeLock.acquire();
                        }
                    }
                } else if (!(this.playingMessageObject == null || !this.playingMessageObject.isVoice() || this.useFrontSpeaker)) {
                    FileLog.m11e("tmessages", "start listen");
                    if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                        this.proximityWakeLock.acquire();
                    }
                    this.useFrontSpeaker = true;
                    startAudioAgain(false);
                    this.ignoreOnPause = true;
                }
                this.raisedToBack = PROCESSOR_TYPE_OTHER;
                this.raisedToTop = PROCESSOR_TYPE_OTHER;
                this.countLess = PROCESSOR_TYPE_OTHER;
            } else if (this.proximityTouched) {
                if (!(this.playingMessageObject == null || !this.playingMessageObject.isVoice() || this.useFrontSpeaker)) {
                    FileLog.m11e("tmessages", "start listen by proximity only");
                    if (!(!this.proximityHasDifferentValues || this.proximityWakeLock == null || this.proximityWakeLock.isHeld())) {
                        this.proximityWakeLock.acquire();
                    }
                    this.useFrontSpeaker = true;
                    startAudioAgain(false);
                    this.ignoreOnPause = true;
                }
            } else if (!this.proximityTouched) {
                if (this.raiseToEarRecord) {
                    FileLog.m11e("tmessages", "stop record");
                    stopRecording(PROCESSOR_TYPE_INTEL);
                    this.raiseToEarRecord = false;
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                } else if (this.useFrontSpeaker) {
                    FileLog.m11e("tmessages", "stop listen");
                    this.useFrontSpeaker = false;
                    startAudioAgain(true);
                    this.ignoreOnPause = false;
                    if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                        this.proximityWakeLock.release();
                    }
                }
            }
            if (this.timeSinceRaise != 0 && this.raisedToBack == 6 && Math.abs(System.currentTimeMillis() - this.timeSinceRaise) > 1000) {
                this.raisedToBack = PROCESSOR_TYPE_OTHER;
                this.raisedToTop = PROCESSOR_TYPE_OTHER;
                this.countLess = PROCESSOR_TYPE_OTHER;
                this.timeSinceRaise = 0;
            }
        }
    }

    public void startRecordingIfFromSpeaker() {
        if (this.useFrontSpeaker && this.raiseChat != null && this.allowStartRecord) {
            this.raiseToEarRecord = true;
            startRecording(this.raiseChat.getDialogId(), null);
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean paused) {
        if (this.playingMessageObject != null) {
            boolean post;
            if (this.audioPlayer != null) {
                post = true;
            } else {
                post = false;
            }
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.audioRouteChanged;
            Object[] objArr = new Object[PROCESSOR_TYPE_QCOM];
            objArr[PROCESSOR_TYPE_OTHER] = Boolean.valueOf(this.useFrontSpeaker);
            instance.postNotificationName(i, objArr);
            MessageObject currentMessageObject = this.playingMessageObject;
            float progress = this.playingMessageObject.audioProgress;
            cleanupPlayer(false, true);
            currentMessageObject.audioProgress = progress;
            playAudio(currentMessageObject);
            if (!paused) {
                return;
            }
            if (post) {
                AndroidUtilities.runOnUIThread(new C05549(currentMessageObject), 100);
            } else {
                pauseAudio(currentMessageObject);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setInputFieldHasText(boolean value) {
        this.inputFieldHasText = value;
    }

    public void setAllowStartRecord(boolean value) {
        this.allowStartRecord = value;
    }

    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (chatActivity == null) {
            return;
        }
        if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null) {
            this.raiseChat = chatActivity;
            if ((this.raiseToSpeak || (this.playingMessageObject != null && this.playingMessageObject.isVoice())) && !this.sensorsStarted) {
                float[] fArr = this.gravity;
                float[] fArr2 = this.gravity;
                this.gravity[PROCESSOR_TYPE_INTEL] = 0.0f;
                fArr2[PROCESSOR_TYPE_QCOM] = 0.0f;
                fArr[PROCESSOR_TYPE_OTHER] = 0.0f;
                fArr = this.linearAcceleration;
                fArr2 = this.linearAcceleration;
                this.linearAcceleration[PROCESSOR_TYPE_INTEL] = 0.0f;
                fArr2[PROCESSOR_TYPE_QCOM] = 0.0f;
                fArr[PROCESSOR_TYPE_OTHER] = 0.0f;
                fArr = this.gravityFast;
                fArr2 = this.gravityFast;
                this.gravityFast[PROCESSOR_TYPE_INTEL] = 0.0f;
                fArr2[PROCESSOR_TYPE_QCOM] = 0.0f;
                fArr[PROCESSOR_TYPE_OTHER] = 0.0f;
                this.lastTimestamp = 0;
                this.previousAccValue = 0.0f;
                this.raisedToTop = PROCESSOR_TYPE_OTHER;
                this.countLess = PROCESSOR_TYPE_OTHER;
                this.raisedToBack = PROCESSOR_TYPE_OTHER;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
                        if (MediaController.this.gravitySensor != null) {
                            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.gravitySensor, DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
                        }
                        if (MediaController.this.linearSensor != null) {
                            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.linearSensor, DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
                        }
                        if (MediaController.this.accelerometerSensor != null) {
                            MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.accelerometerSensor, DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
                        }
                        MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.proximitySensor, MediaController.PROCESSOR_TYPE_MTK);
                    }
                });
                this.sensorsStarted = true;
            }
        }
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity) {
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
        } else if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
                this.raiseChat = null;
                stopRecording(PROCESSOR_TYPE_OTHER);
                this.sensorsStarted = false;
                this.accelerometerVertical = false;
                this.proximityTouched = false;
                this.raiseToEarRecord = false;
                this.useFrontSpeaker = false;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
                        if (MediaController.this.linearSensor != null) {
                            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.linearSensor);
                        }
                        if (MediaController.this.gravitySensor != null) {
                            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.gravitySensor);
                        }
                        if (MediaController.this.accelerometerSensor != null) {
                            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.accelerometerSensor);
                        }
                        MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.proximitySensor);
                    }
                });
                if (this.proximityHasDifferentValues && this.proximityWakeLock != null && this.proximityWakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd) {
        if (this.audioPlayer != null) {
            try {
                this.audioPlayer.reset();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            try {
                this.audioPlayer.stop();
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            try {
                this.audioPlayer.release();
                this.audioPlayer = null;
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
        } else if (this.audioTrackPlayer != null) {
            synchronized (this.playerObjectSync) {
                try {
                    this.audioTrackPlayer.pause();
                    this.audioTrackPlayer.flush();
                } catch (Throwable e222) {
                    FileLog.m13e("tmessages", e222);
                }
                try {
                    this.audioTrackPlayer.release();
                    this.audioTrackPlayer = null;
                } catch (Throwable e2222) {
                    FileLog.m13e("tmessages", e2222);
                }
            }
        }
        stopProgressTimer();
        this.lastProgress = PROCESSOR_TYPE_OTHER;
        this.buffersWrited = PROCESSOR_TYPE_OTHER;
        this.isPaused = false;
        if (this.playingMessageObject != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance().cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject lastFile = this.playingMessageObject;
            this.playingMessageObject.audioProgress = 0.0f;
            this.playingMessageObject.audioProgressSec = PROCESSOR_TYPE_OTHER;
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.audioProgressDidChanged;
            Object[] objArr = new Object[PROCESSOR_TYPE_INTEL];
            objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
            objArr[PROCESSOR_TYPE_QCOM] = Integer.valueOf(PROCESSOR_TYPE_OTHER);
            instance.postNotificationName(i, objArr);
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (notify) {
                NotificationsController.getInstance().audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = PROCESSOR_TYPE_OTHER;
                if (this.voiceMessagesPlaylist != null) {
                    if (byVoiceEnd && this.voiceMessagesPlaylist.get(PROCESSOR_TYPE_OTHER) == lastFile) {
                        this.voiceMessagesPlaylist.remove(PROCESSOR_TYPE_OTHER);
                        this.voiceMessagesPlaylistMap.remove(Integer.valueOf(lastFile.getId()));
                        if (this.voiceMessagesPlaylist.isEmpty()) {
                            this.voiceMessagesPlaylist = null;
                            this.voiceMessagesPlaylistMap = null;
                        }
                    } else {
                        this.voiceMessagesPlaylist = null;
                        this.voiceMessagesPlaylistMap = null;
                    }
                }
                if (this.voiceMessagesPlaylist != null) {
                    playAudio((MessageObject) this.voiceMessagesPlaylist.get(PROCESSOR_TYPE_OTHER));
                } else {
                    if (lastFile.isVoice() && lastFile.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.audioDidReset;
                    objArr = new Object[PROCESSOR_TYPE_INTEL];
                    objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(lastFile.getId());
                    objArr[PROCESSOR_TYPE_QCOM] = Boolean.valueOf(stopService);
                    instance.postNotificationName(i, objArr);
                }
            }
            if (stopService) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
        if (!this.useFrontSpeaker && !this.raiseToSpeak) {
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat);
            this.raiseChat = chat;
        }
    }

    private void seekOpusPlayer(float progress) {
        if (progress != VOLUME_NORMAL) {
            if (!this.isPaused) {
                this.audioTrackPlayer.pause();
            }
            this.audioTrackPlayer.flush();
            this.fileDecodingQueue.postRunnable(new AnonymousClass12(progress));
        }
    }

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject != null && this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        try {
            if (this.audioPlayer != null) {
                int seekTo = (int) (((float) this.audioPlayer.getDuration()) * progress);
                this.audioPlayer.seekTo(seekTo);
                this.lastProgress = seekTo;
            } else if (this.audioTrackPlayer != null) {
                seekOpusPlayer(progress);
            }
            return true;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return false;
        }
    }

    public MessageObject getPlayingMessageObject() {
        return this.playingMessageObject;
    }

    public int getPlayingMessageObjectNum() {
        return this.currentPlaylistNum;
    }

    private void buildShuffledPlayList() {
        if (!this.playlist.isEmpty()) {
            ArrayList<MessageObject> all = new ArrayList(this.playlist);
            this.shuffledPlaylist.clear();
            MessageObject messageObject = (MessageObject) this.playlist.get(this.currentPlaylistNum);
            all.remove(this.currentPlaylistNum);
            this.shuffledPlaylist.add(messageObject);
            int count = all.size();
            for (int a = PROCESSOR_TYPE_OTHER; a < count; a += PROCESSOR_TYPE_QCOM) {
                int index = Utilities.random.nextInt(all.size());
                this.shuffledPlaylist.add(all.get(index));
                all.remove(index);
            }
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current) {
        return setPlaylist(messageObjects, current, true);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, boolean loadMusic) {
        boolean z = true;
        if (this.playingMessageObject == current) {
            return playAudio(current);
        }
        boolean z2;
        if (loadMusic) {
            z2 = false;
        } else {
            z2 = true;
        }
        this.forceLoopCurrentPlaylist = z2;
        if (this.playlist.isEmpty()) {
            z = false;
        }
        this.playMusicAgain = z;
        this.playlist.clear();
        for (int a = messageObjects.size() - 1; a >= 0; a--) {
            MessageObject messageObject = (MessageObject) messageObjects.get(a);
            if (messageObject.isMusic()) {
                this.playlist.add(messageObject);
            }
        }
        this.currentPlaylistNum = this.playlist.indexOf(current);
        if (this.currentPlaylistNum == -1) {
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.currentPlaylistNum = this.playlist.size();
            this.playlist.add(current);
        }
        if (current.isMusic()) {
            if (this.shuffleMusic) {
                buildShuffledPlayList();
                this.currentPlaylistNum = PROCESSOR_TYPE_OTHER;
            }
            if (loadMusic) {
                SharedMediaQuery.loadMusic(current.getDialogId(), ((MessageObject) this.playlist.get(PROCESSOR_TYPE_OTHER)).getId());
            }
        }
        return playAudio(current);
    }

    public void playNextMessage() {
        playNextMessage(false);
    }

    public void playMessageAtIndex(int index) {
        if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < this.playlist.size()) {
            this.currentPlaylistNum = index;
            this.playMusicAgain = true;
            playAudio((MessageObject) this.playlist.get(this.currentPlaylistNum));
        }
    }

    private void playNextMessage(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = this.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (byStop && this.repeatMode == PROCESSOR_TYPE_INTEL && !this.forceLoopCurrentPlaylist) {
            cleanupPlayer(false, false);
            playAudio((MessageObject) currentPlayList.get(this.currentPlaylistNum));
            return;
        }
        this.currentPlaylistNum += PROCESSOR_TYPE_QCOM;
        if (this.currentPlaylistNum >= currentPlayList.size()) {
            this.currentPlaylistNum = PROCESSOR_TYPE_OTHER;
            if (byStop && this.repeatMode == 0 && !this.forceLoopCurrentPlaylist) {
                if (this.audioPlayer != null || this.audioTrackPlayer != null) {
                    if (this.audioPlayer != null) {
                        try {
                            this.audioPlayer.reset();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                        try {
                            this.audioPlayer.stop();
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                        }
                        try {
                            this.audioPlayer.release();
                            this.audioPlayer = null;
                        } catch (Throwable e22) {
                            FileLog.m13e("tmessages", e22);
                        }
                    } else if (this.audioTrackPlayer != null) {
                        synchronized (this.playerObjectSync) {
                            try {
                                this.audioTrackPlayer.pause();
                                this.audioTrackPlayer.flush();
                            } catch (Throwable e222) {
                                FileLog.m13e("tmessages", e222);
                            }
                            try {
                                this.audioTrackPlayer.release();
                                this.audioTrackPlayer = null;
                            } catch (Throwable e2222) {
                                FileLog.m13e("tmessages", e2222);
                            }
                        }
                    }
                    stopProgressTimer();
                    this.lastProgress = PROCESSOR_TYPE_OTHER;
                    this.buffersWrited = PROCESSOR_TYPE_OTHER;
                    this.isPaused = true;
                    this.playingMessageObject.audioProgress = 0.0f;
                    this.playingMessageObject.audioProgressSec = PROCESSOR_TYPE_OTHER;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.audioProgressDidChanged;
                    Object[] objArr = new Object[PROCESSOR_TYPE_INTEL];
                    objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
                    objArr[PROCESSOR_TYPE_QCOM] = Integer.valueOf(PROCESSOR_TYPE_OTHER);
                    instance.postNotificationName(i, objArr);
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.audioPlayStateChanged;
                    objArr = new Object[PROCESSOR_TYPE_QCOM];
                    objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
                    instance.postNotificationName(i, objArr);
                    return;
                }
                return;
            }
        }
        if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
            this.playMusicAgain = true;
            playAudio((MessageObject) currentPlayList.get(this.currentPlaylistNum));
        }
    }

    public void playPreviousMessage() {
        ArrayList<MessageObject> currentPlayList = this.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        this.currentPlaylistNum--;
        if (this.currentPlaylistNum < 0) {
            this.currentPlaylistNum = currentPlayList.size() - 1;
        }
        if (this.currentPlaylistNum >= 0 && this.currentPlaylistNum < currentPlayList.size()) {
            this.playMusicAgain = true;
            playAudio((MessageObject) currentPlayList.get(this.currentPlaylistNum));
        }
    }

    private void checkIsNextMusicFileDownloaded() {
        if ((getCurrentDownloadMask() & AUTODOWNLOAD_MASK_MUSIC) != 0) {
            ArrayList<MessageObject> currentPlayList = this.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (currentPlayList != null && currentPlayList.size() >= PROCESSOR_TYPE_INTEL) {
                int nextIndex = this.currentPlaylistNum + PROCESSOR_TYPE_QCOM;
                if (nextIndex >= currentPlayList.size()) {
                    nextIndex = PROCESSOR_TYPE_OTHER;
                }
                MessageObject nextAudio = (MessageObject) currentPlayList.get(nextIndex);
                File file = null;
                if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
                    file = new File(nextAudio.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
                if (cacheFile == null || !cacheFile.exists()) {
                    boolean z = false;
                }
                if (cacheFile != null && cacheFile != file && !cacheFile.exists() && nextAudio.isMusic()) {
                    FileLoader.getInstance().loadFile(nextAudio.getDocument(), false, false);
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> playlist, boolean unread) {
        this.voiceMessagesPlaylist = playlist;
        if (this.voiceMessagesPlaylist != null) {
            this.voiceMessagesPlaylistUnread = unread;
            this.voiceMessagesPlaylistMap = new HashMap();
            for (int a = PROCESSOR_TYPE_OTHER; a < this.voiceMessagesPlaylist.size(); a += PROCESSOR_TYPE_QCOM) {
                MessageObject messageObject = (MessageObject) this.voiceMessagesPlaylist.get(a);
                this.voiceMessagesPlaylistMap.put(Integer.valueOf(messageObject.getId()), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int neededAudioFocus;
        if (!messageObject.isVoice()) {
            neededAudioFocus = PROCESSOR_TYPE_QCOM;
        } else if (this.useFrontSpeaker) {
            neededAudioFocus = PROCESSOR_TYPE_MTK;
        } else {
            neededAudioFocus = PROCESSOR_TYPE_INTEL;
        }
        if (this.hasAudioFocus != neededAudioFocus) {
            int result;
            this.hasAudioFocus = neededAudioFocus;
            if (neededAudioFocus == PROCESSOR_TYPE_MTK) {
                result = NotificationsController.getInstance().audioManager.requestAudioFocus(this, PROCESSOR_TYPE_OTHER, PROCESSOR_TYPE_QCOM);
            } else {
                result = NotificationsController.getInstance().audioManager.requestAudioFocus(this, PROCESSOR_TYPE_MTK, neededAudioFocus == PROCESSOR_TYPE_INTEL ? PROCESSOR_TYPE_MTK : PROCESSOR_TYPE_QCOM);
            }
            if (result == PROCESSOR_TYPE_QCOM) {
                this.audioFocus = PROCESSOR_TYPE_INTEL;
            }
        }
    }

    public boolean playAudio(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        if ((this.audioTrackPlayer == null && this.audioPlayer == null) || this.playingMessageObject == null || messageObject.getId() != this.playingMessageObject.getId()) {
            File cacheFile;
            if (!messageObject.isOut() && messageObject.isContentUnread() && messageObject.messageOwner.to_id.channel_id == 0) {
                MessagesController.getInstance().markMessageContentAsRead(messageObject);
            }
            boolean notify = !this.playMusicAgain;
            if (this.playingMessageObject != null) {
                notify = false;
            }
            cleanupPlayer(notify, false);
            this.playMusicAgain = false;
            File file = null;
            if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
                file = new File(messageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file != null) {
                cacheFile = file;
            } else {
                cacheFile = FileLoader.getPathToMessage(messageObject.messageOwner);
            }
            NotificationCenter instance;
            int i;
            Object[] objArr;
            if (cacheFile == null || cacheFile == file || cacheFile.exists() || !messageObject.isMusic()) {
                this.downloadingCurrentMessage = false;
                if (messageObject.isMusic()) {
                    checkIsNextMusicFileDownloaded();
                }
                if (isOpusFile(cacheFile.getAbsolutePath()) == PROCESSOR_TYPE_QCOM) {
                    this.playlist.clear();
                    this.shuffledPlaylist.clear();
                    synchronized (this.playerObjectSync) {
                        try {
                            this.ignoreFirstProgress = PROCESSOR_TYPE_MTK;
                            Semaphore semaphore = new Semaphore(PROCESSOR_TYPE_OTHER);
                            Boolean[] result = new Boolean[PROCESSOR_TYPE_QCOM];
                            this.fileDecodingQueue.postRunnable(new AnonymousClass13(result, cacheFile, semaphore));
                            semaphore.acquire();
                            if (result[PROCESSOR_TYPE_OTHER].booleanValue()) {
                                this.currentTotalPcmDuration = getTotalPcmDuration();
                                this.audioTrackPlayer = new AudioTrack(this.useFrontSpeaker ? PROCESSOR_TYPE_OTHER : PROCESSOR_TYPE_MTK, 48000, PROCESSOR_TYPE_SEC, PROCESSOR_TYPE_INTEL, this.playerBufferSize, PROCESSOR_TYPE_QCOM);
                                this.audioTrackPlayer.setStereoVolume(VOLUME_NORMAL, VOLUME_NORMAL);
                                this.audioTrackPlayer.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
                                    public void onMarkerReached(AudioTrack audioTrack) {
                                        MediaController.this.cleanupPlayer(true, true, true);
                                    }

                                    public void onPeriodicNotification(AudioTrack audioTrack) {
                                    }
                                });
                                this.audioTrackPlayer.play();
                            } else {
                                return false;
                            }
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                            if (this.audioTrackPlayer != null) {
                                this.audioTrackPlayer.release();
                                this.audioTrackPlayer = null;
                                this.isPaused = false;
                                this.playingMessageObject = null;
                                this.downloadingCurrentMessage = false;
                            }
                            return false;
                        }
                    }
                } else {
                    try {
                        this.audioPlayer = new MediaPlayer();
                        this.audioPlayer.setAudioStreamType(this.useFrontSpeaker ? PROCESSOR_TYPE_OTHER : PROCESSOR_TYPE_MTK);
                        this.audioPlayer.setDataSource(cacheFile.getAbsolutePath());
                        this.audioPlayer.setOnCompletionListener(new AnonymousClass15(messageObject));
                        this.audioPlayer.prepare();
                        this.audioPlayer.start();
                        if (messageObject.isVoice()) {
                            this.audioInfo = null;
                            this.playlist.clear();
                            this.shuffledPlaylist.clear();
                        } else {
                            try {
                                this.audioInfo = AudioInfo.getAudioInfo(cacheFile);
                            } catch (Throwable e2) {
                                FileLog.m13e("tmessages", e2);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m13e("tmessages", e22);
                        NotificationCenter instance2 = NotificationCenter.getInstance();
                        int i2 = NotificationCenter.audioPlayStateChanged;
                        Object[] objArr2 = new Object[PROCESSOR_TYPE_QCOM];
                        objArr2[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject != null ? this.playingMessageObject.getId() : PROCESSOR_TYPE_OTHER);
                        instance2.postNotificationName(i2, objArr2);
                        if (this.audioPlayer != null) {
                            this.audioPlayer.release();
                            this.audioPlayer = null;
                            this.isPaused = false;
                            this.playingMessageObject = null;
                            this.downloadingCurrentMessage = false;
                        }
                        return false;
                    }
                }
                checkAudioFocus(messageObject);
                setPlayerVolume();
                this.isPaused = false;
                this.lastProgress = PROCESSOR_TYPE_OTHER;
                this.lastPlayPcm = 0;
                this.playingMessageObject = messageObject;
                if (!this.raiseToSpeak) {
                    startRaiseToEarSensors(this.raiseChat);
                }
                startProgressTimer(this.playingMessageObject);
                instance = NotificationCenter.getInstance();
                i = NotificationCenter.audioDidStarted;
                objArr = new Object[PROCESSOR_TYPE_QCOM];
                objArr[PROCESSOR_TYPE_OTHER] = messageObject;
                instance.postNotificationName(i, objArr);
                if (this.audioPlayer != null) {
                    try {
                        if (this.playingMessageObject.audioProgress != 0.0f) {
                            this.audioPlayer.seekTo((int) (((float) this.audioPlayer.getDuration()) * this.playingMessageObject.audioProgress));
                        }
                    } catch (Throwable e23) {
                        this.playingMessageObject.audioProgress = 0.0f;
                        this.playingMessageObject.audioProgressSec = PROCESSOR_TYPE_OTHER;
                        instance = NotificationCenter.getInstance();
                        i = NotificationCenter.audioProgressDidChanged;
                        objArr = new Object[PROCESSOR_TYPE_INTEL];
                        objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
                        objArr[PROCESSOR_TYPE_QCOM] = Integer.valueOf(PROCESSOR_TYPE_OTHER);
                        instance.postNotificationName(i, objArr);
                        FileLog.m13e("tmessages", e23);
                    }
                } else if (this.audioTrackPlayer != null) {
                    if (this.playingMessageObject.audioProgress == VOLUME_NORMAL) {
                        this.playingMessageObject.audioProgress = 0.0f;
                    }
                    this.fileDecodingQueue.postRunnable(new Runnable() {
                        public void run() {
                            try {
                                if (!(MediaController.this.playingMessageObject == null || MediaController.this.playingMessageObject.audioProgress == 0.0f)) {
                                    MediaController.this.lastPlayPcm = (long) (((float) MediaController.this.currentTotalPcmDuration) * MediaController.this.playingMessageObject.audioProgress);
                                    MediaController.this.seekOpusFile(MediaController.this.playingMessageObject.audioProgress);
                                }
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                            synchronized (MediaController.this.playerSync) {
                                MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
                                MediaController.this.usedPlayerBuffers.clear();
                            }
                            MediaController.this.decodingFinished = false;
                            MediaController.this.checkPlayerQueue();
                        }
                    });
                }
                if (this.playingMessageObject.isMusic()) {
                    ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                } else {
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                return true;
            }
            FileLoader.getInstance().loadFile(messageObject.getDocument(), false, false);
            this.downloadingCurrentMessage = true;
            this.isPaused = false;
            this.lastProgress = PROCESSOR_TYPE_OTHER;
            this.lastPlayPcm = 0;
            this.audioInfo = null;
            this.playingMessageObject = messageObject;
            if (this.playingMessageObject.getDocument() != null) {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } else {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
            instance = NotificationCenter.getInstance();
            i = NotificationCenter.audioPlayStateChanged;
            objArr = new Object[PROCESSOR_TYPE_QCOM];
            objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
            instance.postNotificationName(i, objArr);
            return true;
        }
        if (this.isPaused) {
            resumeAudio(messageObject);
        }
        if (!this.raiseToSpeak) {
            startRaiseToEarSensors(this.raiseChat);
        }
        return true;
    }

    public void stopAudio() {
        if ((this.audioTrackPlayer != null || this.audioPlayer != null) && this.playingMessageObject != null) {
            try {
                if (this.audioPlayer != null) {
                    try {
                        this.audioPlayer.reset();
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    this.audioPlayer.stop();
                    try {
                        if (this.audioPlayer != null) {
                            this.audioPlayer.release();
                            this.audioPlayer = null;
                        } else if (this.audioTrackPlayer != null) {
                            synchronized (this.playerObjectSync) {
                                this.audioTrackPlayer.release();
                                this.audioTrackPlayer = null;
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                    stopProgressTimer();
                    this.playingMessageObject = null;
                    this.downloadingCurrentMessage = false;
                    this.isPaused = false;
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                if (this.audioTrackPlayer != null) {
                    this.audioTrackPlayer.pause();
                    this.audioTrackPlayer.flush();
                }
                if (this.audioPlayer != null) {
                    this.audioPlayer.release();
                    this.audioPlayer = null;
                } else if (this.audioTrackPlayer != null) {
                    synchronized (this.playerObjectSync) {
                        this.audioTrackPlayer.release();
                        this.audioTrackPlayer = null;
                    }
                }
                stopProgressTimer();
                this.playingMessageObject = null;
                this.downloadingCurrentMessage = false;
                this.isPaused = false;
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
        }
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public boolean isShuffleMusic() {
        return this.shuffleMusic;
    }

    public int getRepeatMode() {
        return this.repeatMode;
    }

    public void toggleShuffleMusic() {
        boolean z;
        if (this.shuffleMusic) {
            z = false;
        } else {
            z = true;
        }
        this.shuffleMusic = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putBoolean("shuffleMusic", this.shuffleMusic);
        editor.commit();
        if (this.shuffleMusic) {
            buildShuffledPlayList();
            this.currentPlaylistNum = PROCESSOR_TYPE_OTHER;
        } else if (this.playingMessageObject != null) {
            this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
            if (this.currentPlaylistNum == -1) {
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                cleanupPlayer(true, true);
            }
        }
    }

    public void toggleRepeatMode() {
        this.repeatMode += PROCESSOR_TYPE_QCOM;
        if (this.repeatMode > PROCESSOR_TYPE_INTEL) {
            this.repeatMode = PROCESSOR_TYPE_OTHER;
        }
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putInt("repeatMode", this.repeatMode);
        editor.commit();
    }

    public boolean pauseAudio(MessageObject messageObject) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject != null && this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        stopProgressTimer();
        try {
            if (this.audioPlayer != null) {
                this.audioPlayer.pause();
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.pause();
            }
            this.isPaused = true;
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.audioPlayStateChanged;
            Object[] objArr = new Object[PROCESSOR_TYPE_QCOM];
            objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
            instance.postNotificationName(i, objArr);
            return true;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            this.isPaused = false;
            return false;
        }
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioTrackPlayer == null && this.audioPlayer == null) || messageObject == null || this.playingMessageObject == null) {
            return false;
        }
        if (this.playingMessageObject != null && this.playingMessageObject.getId() != messageObject.getId()) {
            return false;
        }
        try {
            startProgressTimer(messageObject);
            if (this.audioPlayer != null) {
                this.audioPlayer.start();
            } else if (this.audioTrackPlayer != null) {
                this.audioTrackPlayer.play();
                checkPlayerQueue();
            }
            checkAudioFocus(messageObject);
            this.isPaused = false;
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.audioPlayStateChanged;
            Object[] objArr = new Object[PROCESSOR_TYPE_QCOM];
            objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(this.playingMessageObject.getId());
            instance.postNotificationName(i, objArr);
            return true;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return false;
        }
    }

    public boolean isPlayingAudio(MessageObject messageObject) {
        return ((this.audioTrackPlayer == null && this.audioPlayer == null) || messageObject == null || this.playingMessageObject == null || (this.playingMessageObject != null && (this.playingMessageObject.getId() != messageObject.getId() || this.downloadingCurrentMessage))) ? false : true;
    }

    public boolean isAudioPaused() {
        return this.isPaused || this.downloadingCurrentMessage;
    }

    public boolean isDownloadingCurrentMessage() {
        return this.downloadingCurrentMessage;
    }

    public void startRecording(long dialog_id, MessageObject reply_to_msg) {
        long j = 50;
        boolean paused = false;
        if (!(this.playingMessageObject == null || !isPlayingAudio(this.playingMessageObject) || isAudioPaused())) {
            paused = true;
            pauseAudio(this.playingMessageObject);
        }
        try {
            ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        Runnable anonymousClass17 = new AnonymousClass17(dialog_id, reply_to_msg);
        this.recordStartRunnable = anonymousClass17;
        if (paused) {
            j = 500;
        }
        dispatchQueue.postRunnable(anonymousClass17, j);
    }

    public void generateWaveform(MessageObject messageObject) {
        String id = messageObject.getId() + "_" + messageObject.getDialogId();
        String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new AnonymousClass18(path, id));
        }
    }

    private void stopRecordingInternal(int send) {
        if (send != 0) {
            this.fileEncodingQueue.postRunnable(new AnonymousClass19(this.recordingAudio, this.recordingAudioFile, send));
        }
        try {
            if (this.audioRecorder != null) {
                this.audioRecorder.release();
                this.audioRecorder = null;
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    public void stopRecording(int send) {
        if (this.recordStartRunnable != null) {
            this.recordQueue.cancelRunnable(this.recordStartRunnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new AnonymousClass20(send));
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        Throwable e;
        if (fullPath != null) {
            File file = null;
            if (!(fullPath == null || fullPath.length() == 0)) {
                file = new File(fullPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file != null) {
                File sourceFile = file;
                if (sourceFile.exists()) {
                    ProgressDialog progressDialog = null;
                    if (context != null) {
                        try {
                            ProgressDialog progressDialog2 = new ProgressDialog(context);
                            try {
                                progressDialog2.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
                                progressDialog2.setCanceledOnTouchOutside(false);
                                progressDialog2.setCancelable(false);
                                progressDialog2.setProgressStyle(PROCESSOR_TYPE_QCOM);
                                progressDialog2.setMax(100);
                                progressDialog2.show();
                                progressDialog = progressDialog2;
                            } catch (Exception e2) {
                                e = e2;
                                progressDialog = progressDialog2;
                                FileLog.m13e("tmessages", e);
                                new Thread(new AnonymousClass21(type, name, sourceFile, progressDialog, mime)).start();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.m13e("tmessages", e);
                            new Thread(new AnonymousClass21(type, name, sourceFile, progressDialog, mime)).start();
                        }
                    }
                    new Thread(new AnonymousClass21(type, name, sourceFile, progressDialog, mime)).start();
                }
            }
        }
    }

    public static boolean isWebp(Uri uri) {
        boolean z = false;
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[12];
            if (inputStream.read(header, PROCESSOR_TYPE_OTHER, 12) == 12) {
                String str = new String(header);
                if (str != null) {
                    str = str.toLowerCase();
                    if (str.startsWith("riff") && str.endsWith("webp")) {
                        z = true;
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable e2) {
                                FileLog.m13e("tmessages", e2);
                            }
                        }
                        return z;
                    }
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m13e("tmessages", e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m13e("tmessages", e2222);
                }
            }
        }
        return z;
    }

    public static boolean isGif(Uri uri) {
        boolean z = false;
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[PROCESSOR_TYPE_MTK];
            if (inputStream.read(header, PROCESSOR_TYPE_OTHER, PROCESSOR_TYPE_MTK) == PROCESSOR_TYPE_MTK) {
                String str = new String(header);
                if (str != null && str.equalsIgnoreCase("gif")) {
                    z = true;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                        }
                    }
                    return z;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m13e("tmessages", e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m13e("tmessages", e2222);
                }
            }
        }
        return z;
    }

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                String[] strArr = new String[PROCESSOR_TYPE_QCOM];
                strArr[PROCESSOR_TYPE_OTHER] = "_display_name";
                cursor = contentResolver.query(uri, strArr, null, null, null);
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex("_display_name"));
                }
                if (cursor != null) {
                    cursor.close();
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
        if (result != null) {
            return result;
        }
        result = uri.getPath();
        int cut = result.lastIndexOf(47);
        if (cut != -1) {
            return result.substring(cut + PROCESSOR_TYPE_QCOM);
        }
        return result;
    }

    public static String copyFileToCache(Uri uri, String ext) {
        Throwable e;
        Throwable th;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            String name = getFileName(uri);
            if (name == null) {
                int id = UserConfig.lastLocalId;
                UserConfig.lastLocalId--;
                UserConfig.saveConfig(false);
                Object[] objArr = new Object[PROCESSOR_TYPE_INTEL];
                objArr[PROCESSOR_TYPE_OTHER] = Integer.valueOf(id);
                objArr[PROCESSOR_TYPE_QCOM] = ext;
                name = String.format(Locale.US, "%d.%s", objArr);
            }
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            File f = new File(FileLoader.getInstance().getDirectory(PROCESSOR_TYPE_SEC), name);
            FileOutputStream output = new FileOutputStream(f);
            try {
                byte[] buffer = new byte[20480];
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    output.write(buffer, PROCESSOR_TYPE_OTHER, len);
                }
                String absolutePath = f.getAbsolutePath();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable e22) {
                        FileLog.m13e("tmessages", e22);
                    }
                }
                fileOutputStream = output;
                return absolutePath;
            } catch (Exception e3) {
                e = e3;
                fileOutputStream = output;
                try {
                    FileLog.m13e("tmessages", e);
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e222) {
                            FileLog.m13e("tmessages", e222);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e2222) {
                            FileLog.m13e("tmessages", e2222);
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e22222) {
                            FileLog.m13e("tmessages", e22222);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e222222) {
                            FileLog.m13e("tmessages", e222222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = output;
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            FileLog.m13e("tmessages", e);
            if (inputStream != null) {
                inputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return null;
        }
    }

    public void toggleSaveToGallery() {
        boolean z;
        if (this.saveToGallery) {
            z = false;
        } else {
            z = true;
        }
        this.saveToGallery = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putBoolean("save_gallery", this.saveToGallery);
        editor.commit();
        checkSaveToGalleryFiles();
    }

    public void toggleAutoplayGifs() {
        boolean z;
        if (this.autoplayGifs) {
            z = false;
        } else {
            z = true;
        }
        this.autoplayGifs = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putBoolean("autoplay_gif", this.autoplayGifs);
        editor.commit();
    }

    public void toogleRaiseToSpeak() {
        boolean z;
        if (this.raiseToSpeak) {
            z = false;
        } else {
            z = true;
        }
        this.raiseToSpeak = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putBoolean("raise_to_speak", this.raiseToSpeak);
        editor.commit();
    }

    public void toggleCustomTabs() {
        boolean z;
        if (this.customTabs) {
            z = false;
        } else {
            z = true;
        }
        this.customTabs = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putBoolean("custom_tabs", this.customTabs);
        editor.commit();
    }

    public void toggleDirectShare() {
        boolean z;
        if (this.directShare) {
            z = false;
        } else {
            z = true;
        }
        this.directShare = z;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", PROCESSOR_TYPE_OTHER).edit();
        editor.putBoolean("direct_share", this.directShare);
        editor.commit();
    }

    public void checkSaveToGalleryFiles() {
        try {
            File telegramPath = new File(Environment.getExternalStorageDirectory(), LocaleController.getString("AppName", C0691R.string.AppName));
            File imagePath = new File(telegramPath, LocaleController.getString("AppName", C0691R.string.AppName) + " Images");
            imagePath.mkdir();
            File videoPath = new File(telegramPath, LocaleController.getString("AppName", C0691R.string.AppName) + " Video");
            videoPath.mkdir();
            if (this.saveToGallery) {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").delete();
                }
                if (videoPath.isDirectory()) {
                    new File(videoPath, ".nomedia").delete();
                    return;
                }
                return;
            }
            if (imagePath.isDirectory()) {
                new File(imagePath, ".nomedia").createNewFile();
            }
            if (videoPath.isDirectory()) {
                new File(videoPath, ".nomedia").createNewFile();
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public boolean canSaveToGallery() {
        return this.saveToGallery;
    }

    public boolean canAutoplayGifs() {
        return this.autoplayGifs;
    }

    public boolean canRaiseToSpeak() {
        return this.raiseToSpeak;
    }

    public boolean canCustomTabs() {
        return this.customTabs;
    }

    public boolean canDirectShare() {
        return this.directShare;
    }

    public static void loadGalleryPhotosAlbums(int guid) {
        Thread thread = new Thread(new AnonymousClass22(guid));
        thread.setPriority(PROCESSOR_TYPE_QCOM);
        thread.start();
    }

    public void scheduleVideoConvert(MessageObject messageObject) {
        this.videoConvertQueue.add(messageObject);
        if (this.videoConvertQueue.size() == PROCESSOR_TYPE_QCOM) {
            startVideoConvertFromQueue();
        }
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject == null) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = true;
            }
        } else if (!this.videoConvertQueue.isEmpty()) {
            if (this.videoConvertQueue.get(PROCESSOR_TYPE_OTHER) == messageObject) {
                synchronized (this.videoConvertSync) {
                    this.cancelCurrentVideoConversion = true;
                }
            }
            this.videoConvertQueue.remove(messageObject);
        }
    }

    private void startVideoConvertFromQueue() {
        if (!this.videoConvertQueue.isEmpty()) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = false;
            }
            MessageObject messageObject = (MessageObject) this.videoConvertQueue.get(PROCESSOR_TYPE_OTHER);
            Intent intent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
            intent.putExtra("path", messageObject.messageOwner.attachPath);
            ApplicationLoader.applicationContext.startService(intent);
            VideoConvertRunnable.runConversion(messageObject);
        }
    }

    @SuppressLint({"NewApi"})
    public static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo lastCodecInfo = null;
        for (int i = PROCESSOR_TYPE_OTHER; i < numCodecs; i += PROCESSOR_TYPE_QCOM) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                String[] arr$ = codecInfo.getSupportedTypes();
                int len$ = arr$.length;
                for (int i$ = PROCESSOR_TYPE_OTHER; i$ < len$; i$ += PROCESSOR_TYPE_QCOM) {
                    if (arr$[i$].equalsIgnoreCase(mimeType)) {
                        lastCodecInfo = codecInfo;
                        if (!lastCodecInfo.getName().equals("OMX.SEC.avc.enc")) {
                            return lastCodecInfo;
                        }
                        if (lastCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) {
                            return lastCodecInfo;
                        }
                    }
                }
                continue;
            }
        }
        return lastCodecInfo;
    }

    private static boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            case XtraBox.MP4_XTRA_BT_INT64 /*19*/:
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
            case XtraBox.MP4_XTRA_BT_FILETIME /*21*/:
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
            case 2130706688:
                return true;
            default:
                return false;
        }
    }

    @SuppressLint({"NewApi"})
    public static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        int lastColorFormat = PROCESSOR_TYPE_OTHER;
        for (int i = PROCESSOR_TYPE_OTHER; i < capabilities.colorFormats.length; i += PROCESSOR_TYPE_QCOM) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                lastColorFormat = colorFormat;
                if (!codecInfo.getName().equals("OMX.SEC.AVC.Encoder") || colorFormat != 19) {
                    return colorFormat;
                }
            }
        }
        return lastColorFormat;
    }

    @TargetApi(16)
    private int selectTrack(MediaExtractor extractor, boolean audio) {
        int numTracks = extractor.getTrackCount();
        for (int i = PROCESSOR_TYPE_OTHER; i < numTracks; i += PROCESSOR_TYPE_QCOM) {
            String mime = extractor.getTrackFormat(i).getString("mime");
            if (audio) {
                if (mime.startsWith("audio/")) {
                    return i;
                }
            } else if (mime.startsWith("video/")) {
                return i;
            }
        }
        return -5;
    }

    private void didWriteData(MessageObject messageObject, File file, boolean last, boolean error) {
        boolean firstWrite = this.videoConvertFirstWrite;
        if (firstWrite) {
            this.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass23(error, messageObject, file, firstWrite, last));
    }

    @TargetApi(16)
    private long readAndWriteTrack(MessageObject messageObject, MediaExtractor extractor, MP4Builder mediaMuxer, BufferInfo info, long start, long end, File file, boolean isAudio) throws Exception {
        int trackIndex = selectTrack(extractor, isAudio);
        if (trackIndex < 0) {
            return -1;
        }
        extractor.selectTrack(trackIndex);
        MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);
        int muxerTrackIndex = mediaMuxer.addTrack(trackFormat, isAudio);
        int maxBufferSize = trackFormat.getInteger("max-input-size");
        boolean inputDone = false;
        if (start > 0) {
            extractor.seekTo(start, PROCESSOR_TYPE_OTHER);
        } else {
            extractor.seekTo(0, PROCESSOR_TYPE_OTHER);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        long startTime = -1;
        checkConversionCanceled();
        long lastTimestamp = -100;
        while (!inputDone) {
            checkConversionCanceled();
            boolean eof = false;
            int index = extractor.getSampleTrackIndex();
            if (index == trackIndex) {
                info.size = extractor.readSampleData(buffer, PROCESSOR_TYPE_OTHER);
                if (info.size >= 0) {
                    info.presentationTimeUs = extractor.getSampleTime();
                } else {
                    info.size = PROCESSOR_TYPE_OTHER;
                    eof = true;
                }
                if (info.size > 0 && !eof) {
                    if (start > 0 && startTime == -1) {
                        startTime = info.presentationTimeUs;
                    }
                    if (end >= 0) {
                        if (info.presentationTimeUs >= end) {
                            eof = true;
                        }
                    }
                    if (info.presentationTimeUs > lastTimestamp) {
                        info.offset = PROCESSOR_TYPE_OTHER;
                        info.flags = extractor.getSampleFlags();
                        if (mediaMuxer.writeSampleData(muxerTrackIndex, buffer, info, isAudio)) {
                            didWriteData(messageObject, file, false, false);
                        }
                    }
                    lastTimestamp = info.presentationTimeUs;
                }
                if (!eof) {
                    extractor.advance();
                }
            } else if (index == -1) {
                eof = true;
            } else {
                extractor.advance();
            }
            if (eof) {
                inputDone = true;
            }
        }
        extractor.unselectTrack(trackIndex);
        return startTime;
    }

    private void checkConversionCanceled() throws Exception {
        synchronized (this.videoConvertSync) {
            boolean cancelConversion = this.cancelCurrentVideoConversion;
        }
        if (cancelConversion) {
            throw new RuntimeException("canceled conversion");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.TargetApi(16)
    private boolean convertVideo(org.telegram.messenger.MessageObject r91) {
        /*
        r90 = this;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.originalPath;
        r84 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.startTime;
        r76 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.endTime;
        r18 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.resultWidth;
        r72 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.resultHeight;
        r70 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.rotationValue;
        r74 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.originalWidth;
        r61 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.originalHeight;
        r60 = r0;
        r0 = r91;
        r6 = r0.videoEditedInfo;
        r0 = r6.bitrate;
        r24 = r0;
        r73 = 0;
        r20 = new java.io.File;
        r0 = r91;
        r6 = r0.messageOwner;
        r6 = r6.attachPath;
        r0 = r20;
        r0.<init>(r6);
        r6 = android.os.Build.VERSION.SDK_INT;
        r10 = 18;
        if (r6 >= r10) goto L_0x00c5;
    L_0x005d:
        r0 = r70;
        r1 = r72;
        if (r0 <= r1) goto L_0x00c5;
    L_0x0063:
        r0 = r72;
        r1 = r61;
        if (r0 == r1) goto L_0x00c5;
    L_0x0069:
        r0 = r70;
        r1 = r60;
        if (r0 == r1) goto L_0x00c5;
    L_0x006f:
        r79 = r70;
        r70 = r72;
        r72 = r79;
        r74 = 90;
        r73 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
    L_0x0079:
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r10 = "videoconvert";
        r11 = 0;
        r68 = r6.getSharedPreferences(r10, r11);
        r6 = "isPreviousOk";
        r10 = 1;
        r0 = r68;
        r55 = r0.getBoolean(r6, r10);
        r6 = r68.edit();
        r10 = "isPreviousOk";
        r11 = 0;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r51 = new java.io.File;
        r0 = r51;
        r1 = r84;
        r0.<init>(r1);
        r6 = r51.canRead();
        if (r6 == 0) goto L_0x00aa;
    L_0x00a8:
        if (r55 != 0) goto L_0x00f8;
    L_0x00aa:
        r6 = 1;
        r10 = 1;
        r0 = r90;
        r1 = r91;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);
        r6 = r68.edit();
        r10 = "isPreviousOk";
        r11 = 1;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = 0;
    L_0x00c4:
        return r6;
    L_0x00c5:
        r6 = android.os.Build.VERSION.SDK_INT;
        r10 = 20;
        if (r6 <= r10) goto L_0x0079;
    L_0x00cb:
        r6 = 90;
        r0 = r74;
        if (r0 != r6) goto L_0x00dc;
    L_0x00d1:
        r79 = r70;
        r70 = r72;
        r72 = r79;
        r74 = 0;
        r73 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0079;
    L_0x00dc:
        r6 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r74;
        if (r0 != r6) goto L_0x00e7;
    L_0x00e2:
        r73 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r74 = 0;
        goto L_0x0079;
    L_0x00e7:
        r6 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r74;
        if (r0 != r6) goto L_0x0079;
    L_0x00ed:
        r79 = r70;
        r70 = r72;
        r72 = r79;
        r74 = 0;
        r73 = 90;
        goto L_0x0079;
    L_0x00f8:
        r6 = 1;
        r0 = r90;
        r0.videoConvertFirstWrite = r6;
        r43 = 0;
        r86 = r76;
        r80 = java.lang.System.currentTimeMillis();
        if (r72 == 0) goto L_0x087e;
    L_0x0107:
        if (r70 == 0) goto L_0x087e;
    L_0x0109:
        r57 = 0;
        r45 = 0;
        r48 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r48.<init>();	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r58 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r58.<init>();	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r0 = r58;
        r1 = r20;
        r0.setCacheFile(r1);	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r0 = r58;
        r1 = r74;
        r0.setRotation(r1);	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r0 = r58;
        r1 = r72;
        r2 = r70;
        r0.setSize(r1, r2);	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r6 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r6.<init>();	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r0 = r58;
        r57 = r6.createMovie(r0);	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r46 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r46.<init>();	 Catch:{ Exception -> 0x0832, all -> 0x089a }
        r6 = r51.toString();	 Catch:{ Exception -> 0x08a7, all -> 0x04aa }
        r0 = r46;
        r0.setDataSource(r6);	 Catch:{ Exception -> 0x08a7, all -> 0x04aa }
        r90.checkConversionCanceled();	 Catch:{ Exception -> 0x08a7, all -> 0x04aa }
        r0 = r72;
        r1 = r61;
        if (r0 != r1) goto L_0x0156;
    L_0x0150:
        r0 = r70;
        r1 = r60;
        if (r0 == r1) goto L_0x080c;
    L_0x0156:
        r6 = 0;
        r0 = r90;
        r1 = r46;
        r83 = r0.selectTrack(r1, r6);	 Catch:{ Exception -> 0x08a7, all -> 0x04aa }
        if (r83 < 0) goto L_0x08b6;
    L_0x0161:
        r4 = 0;
        r37 = 0;
        r53 = 0;
        r64 = 0;
        r88 = -1;
        r62 = 0;
        r50 = 0;
        r30 = 0;
        r78 = 0;
        r85 = -5;
        r69 = 0;
        r6 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r56 = r6.toLowerCase();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 >= r10) goto L_0x0454;
    L_0x0182:
        r6 = "video/avc";
        r26 = selectCodec(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "video/avc";
        r0 = r26;
        r28 = selectColorFormat(r0, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r28 != 0) goto L_0x0225;
    L_0x0192:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = "no supported color format";
        r6.<init>(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        throw r6;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x019a:
        r35 = move-exception;
    L_0x019b:
        r6 = "tmessages";
        r0 = r35;
        org.telegram.messenger.FileLog.m13e(r6, r0);	 Catch:{ Exception -> 0x08a7, all -> 0x04aa }
        r43 = 1;
        r16 = r86;
    L_0x01a6:
        r0 = r46;
        r1 = r83;
        r0.unselectTrack(r1);	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
        if (r64 == 0) goto L_0x01b2;
    L_0x01af:
        r64.release();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
    L_0x01b2:
        if (r53 == 0) goto L_0x01b7;
    L_0x01b4:
        r53.release();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
    L_0x01b7:
        if (r4 == 0) goto L_0x01bf;
    L_0x01b9:
        r4.stop();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
        r4.release();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
    L_0x01bf:
        if (r37 == 0) goto L_0x01c7;
    L_0x01c1:
        r37.stop();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
        r37.release();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
    L_0x01c7:
        r90.checkConversionCanceled();	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
    L_0x01ca:
        if (r43 != 0) goto L_0x01db;
    L_0x01cc:
        r21 = 1;
        r11 = r90;
        r12 = r91;
        r13 = r46;
        r14 = r57;
        r15 = r48;
        r11.readAndWriteTrack(r12, r13, r14, r15, r16, r18, r20, r21);	 Catch:{ Exception -> 0x08ad, all -> 0x089f }
    L_0x01db:
        if (r46 == 0) goto L_0x01e0;
    L_0x01dd:
        r46.release();
    L_0x01e0:
        if (r57 == 0) goto L_0x01e8;
    L_0x01e2:
        r6 = 0;
        r0 = r57;
        r0.finishMovie(r6);	 Catch:{ Exception -> 0x0828 }
    L_0x01e8:
        r6 = "tmessages";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "time = ";
        r10 = r10.append(r11);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r80;
        r10 = r10.append(r12);
        r10 = r10.toString();
        org.telegram.messenger.FileLog.m11e(r6, r10);
        r45 = r46;
    L_0x0208:
        r6 = r68.edit();
        r10 = "isPreviousOk";
        r11 = 1;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = 1;
        r0 = r90;
        r1 = r91;
        r2 = r20;
        r3 = r43;
        r0.didWriteData(r1, r2, r6, r3);
        r6 = 1;
        goto L_0x00c4;
    L_0x0225:
        r27 = r26.getName();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "OMX.qcom.";
        r0 = r27;
        r6 = r0.contains(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x041a;
    L_0x0233:
        r69 = 1;
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 16;
        if (r6 != r10) goto L_0x0251;
    L_0x023b:
        r6 = "lge";
        r0 = r56;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 != 0) goto L_0x024f;
    L_0x0245:
        r6 = "nokia";
        r0 = r56;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x0251;
    L_0x024f:
        r78 = 1;
    L_0x0251:
        r6 = "tmessages";
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "codec = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = r26.getName();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = " manufacturer = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r56;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "device = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        org.telegram.messenger.FileLog.m11e(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0285:
        r6 = "tmessages";
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "colorFormat = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r28;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        org.telegram.messenger.FileLog.m11e(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r71 = r70;
        r66 = 0;
        r6 = r72 * r70;
        r6 = r6 * 3;
        r25 = r6 / 2;
        if (r69 != 0) goto L_0x0459;
    L_0x02ab:
        r6 = r70 % 16;
        if (r6 == 0) goto L_0x02bf;
    L_0x02af:
        r6 = r70 % 16;
        r6 = 16 - r6;
        r71 = r71 + r6;
        r6 = r71 - r70;
        r66 = r72 * r6;
        r6 = r66 * 5;
        r6 = r6 / 4;
        r25 = r25 + r6;
    L_0x02bf:
        r0 = r46;
        r1 = r83;
        r0.selectTrack(r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 0;
        r6 = (r76 > r10 ? 1 : (r76 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x04a0;
    L_0x02cc:
        r6 = 0;
        r0 = r46;
        r1 = r76;
        r0.seekTo(r1, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x02d4:
        r0 = r46;
        r1 = r83;
        r52 = r0.getTrackFormat(r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "video/avc";
        r0 = r72;
        r1 = r70;
        r63 = android.media.MediaFormat.createVideoFormat(r6, r0, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "color-format";
        r0 = r63;
        r1 = r28;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "bitrate";
        if (r24 == 0) goto L_0x04db;
    L_0x02f3:
        r0 = r63;
        r1 = r24;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "frame-rate";
        r10 = 25;
        r0 = r63;
        r0.setInteger(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "i-frame-interval";
        r10 = 10;
        r0 = r63;
        r0.setInteger(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 >= r10) goto L_0x0324;
    L_0x0312:
        r6 = "stride";
        r10 = r72 + 32;
        r0 = r63;
        r0.setInteger(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "slice-height";
        r0 = r63;
        r1 = r70;
        r0.setInteger(r6, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0324:
        r6 = "video/avc";
        r37 = android.media.MediaCodec.createEncoderByType(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = 0;
        r10 = 0;
        r11 = 1;
        r0 = r37;
        r1 = r63;
        r0.configure(r1, r6, r10, r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 < r10) goto L_0x034a;
    L_0x033a:
        r54 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r37.createInputSurface();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r54;
        r0.<init>(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r54.makeCurrent();	 Catch:{ Exception -> 0x08b1, all -> 0x04aa }
        r53 = r54;
    L_0x034a:
        r37.start();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "mime";
        r0 = r52;
        r6 = r0.getString(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r4 = android.media.MediaCodec.createDecoderByType(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 < r10) goto L_0x04e0;
    L_0x035f:
        r65 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r65.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r64 = r65;
    L_0x0366:
        r6 = r64.getSurface();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 0;
        r11 = 0;
        r0 = r52;
        r4.configure(r0, r6, r10, r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r4.start();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r22 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r31 = 0;
        r40 = 0;
        r38 = 0;
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 21;
        if (r6 >= r10) goto L_0x0394;
    L_0x0382:
        r31 = r4.getInputBuffers();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r40 = r37.getOutputBuffers();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 >= r10) goto L_0x0394;
    L_0x0390:
        r38 = r37.getInputBuffers();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0394:
        r90.checkConversionCanceled();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0397:
        if (r62 != 0) goto L_0x0802;
    L_0x0399:
        r90.checkConversionCanceled();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r50 != 0) goto L_0x03e5;
    L_0x039e:
        r42 = 0;
        r47 = r46.getSampleTrackIndex();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r47;
        r1 = r83;
        if (r0 != r1) goto L_0x0505;
    L_0x03aa:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r5 < 0) goto L_0x03cf;
    L_0x03b2:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 21;
        if (r6 >= r10) goto L_0x04f1;
    L_0x03b8:
        r49 = r31[r5];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x03ba:
        r6 = 0;
        r0 = r46;
        r1 = r49;
        r7 = r0.readSampleData(r1, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r7 >= 0) goto L_0x04f7;
    L_0x03c5:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r10 = 4;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r50 = 1;
    L_0x03cf:
        if (r42 == 0) goto L_0x03e5;
    L_0x03d1:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r5 < 0) goto L_0x03e5;
    L_0x03d9:
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r14 = 4;
        r8 = r4;
        r9 = r5;
        r8.queueInputBuffer(r9, r10, r11, r12, r14);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r50 = 1;
    L_0x03e5:
        if (r30 != 0) goto L_0x050e;
    L_0x03e7:
        r32 = 1;
    L_0x03e9:
        r39 = 1;
    L_0x03eb:
        if (r32 != 0) goto L_0x03ef;
    L_0x03ed:
        if (r39 == 0) goto L_0x0397;
    L_0x03ef:
        r90.checkConversionCanceled();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r37;
        r1 = r48;
        r41 = r0.dequeueOutputBuffer(r1, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = -1;
        r0 = r41;
        if (r0 != r6) goto L_0x0512;
    L_0x0401:
        r39 = 0;
    L_0x0403:
        r6 = -1;
        r0 = r41;
        if (r0 != r6) goto L_0x03eb;
    L_0x0408:
        if (r30 != 0) goto L_0x03eb;
    L_0x040a:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r48;
        r33 = r4.dequeueOutputBuffer(r0, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = -1;
        r0 = r33;
        if (r0 != r6) goto L_0x0684;
    L_0x0417:
        r32 = 0;
        goto L_0x03eb;
    L_0x041a:
        r6 = "OMX.Intel.";
        r0 = r27;
        r6 = r0.contains(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x0428;
    L_0x0424:
        r69 = 2;
        goto L_0x0251;
    L_0x0428:
        r6 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r0 = r27;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x0436;
    L_0x0432:
        r69 = 3;
        goto L_0x0251;
    L_0x0436:
        r6 = "OMX.SEC.AVC.Encoder";
        r0 = r27;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x0446;
    L_0x0440:
        r69 = 4;
        r78 = 1;
        goto L_0x0251;
    L_0x0446:
        r6 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r0 = r27;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x0251;
    L_0x0450:
        r69 = 5;
        goto L_0x0251;
    L_0x0454:
        r28 = 2130708361; // 0x7f000789 float:1.701803E38 double:1.0527098025E-314;
        goto L_0x0285;
    L_0x0459:
        r6 = 1;
        r0 = r69;
        if (r0 != r6) goto L_0x047a;
    L_0x045e:
        r6 = r56.toLowerCase();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = "lge";
        r6 = r6.equals(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 != 0) goto L_0x02bf;
    L_0x046a:
        r6 = r72 * r70;
        r6 = r6 + 2047;
        r0 = r6 & -2048;
        r82 = r0;
        r6 = r72 * r70;
        r66 = r82 - r6;
        r25 = r25 + r66;
        goto L_0x02bf;
    L_0x047a:
        r6 = 5;
        r0 = r69;
        if (r0 == r6) goto L_0x02bf;
    L_0x047f:
        r6 = 3;
        r0 = r69;
        if (r0 != r6) goto L_0x02bf;
    L_0x0484:
        r6 = "baidu";
        r0 = r56;
        r6 = r0.equals(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x02bf;
    L_0x048e:
        r6 = r70 % 16;
        r6 = 16 - r6;
        r71 = r71 + r6;
        r6 = r71 - r70;
        r66 = r72 * r6;
        r6 = r66 * 5;
        r6 = r6 / 4;
        r25 = r25 + r6;
        goto L_0x02bf;
    L_0x04a0:
        r10 = 0;
        r6 = 0;
        r0 = r46;
        r0.seekTo(r10, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x02d4;
    L_0x04aa:
        r6 = move-exception;
        r45 = r46;
        r16 = r86;
    L_0x04af:
        if (r45 == 0) goto L_0x04b4;
    L_0x04b1:
        r45.release();
    L_0x04b4:
        if (r57 == 0) goto L_0x04bc;
    L_0x04b6:
        r10 = 0;
        r0 = r57;
        r0.finishMovie(r10);	 Catch:{ Exception -> 0x0874 }
    L_0x04bc:
        r10 = "tmessages";
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r12 = "time = ";
        r11 = r11.append(r12);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r80;
        r11 = r11.append(r12);
        r11 = r11.toString();
        org.telegram.messenger.FileLog.m11e(r10, r11);
        throw r6;
    L_0x04db:
        r24 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x02f3;
    L_0x04e0:
        r65 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r65;
        r1 = r72;
        r2 = r70;
        r3 = r73;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r64 = r65;
        goto L_0x0366;
    L_0x04f1:
        r49 = r4.getInputBuffer(r5);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x03ba;
    L_0x04f7:
        r6 = 0;
        r8 = r46.getSampleTime();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 0;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r46.advance();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x03cf;
    L_0x0505:
        r6 = -1;
        r0 = r47;
        if (r0 != r6) goto L_0x03cf;
    L_0x050a:
        r42 = 1;
        goto L_0x03cf;
    L_0x050e:
        r32 = 0;
        goto L_0x03e9;
    L_0x0512:
        r6 = -3;
        r0 = r41;
        if (r0 != r6) goto L_0x0523;
    L_0x0517:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 21;
        if (r6 >= r10) goto L_0x0403;
    L_0x051d:
        r40 = r37.getOutputBuffers();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0403;
    L_0x0523:
        r6 = -2;
        r0 = r41;
        if (r0 != r6) goto L_0x053c;
    L_0x0528:
        r59 = r37.getOutputFormat();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = -5;
        r0 = r85;
        if (r0 != r6) goto L_0x0403;
    L_0x0531:
        r6 = 0;
        r0 = r57;
        r1 = r59;
        r85 = r0.addTrack(r1, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0403;
    L_0x053c:
        if (r41 >= 0) goto L_0x0559;
    L_0x053e:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r41;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6.<init>(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        throw r6;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0559:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 21;
        if (r6 >= r10) goto L_0x0584;
    L_0x055f:
        r36 = r40[r41];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0561:
        if (r36 != 0) goto L_0x058d;
    L_0x0563:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "encoderOutputBuffer ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r41;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = " was null";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6.<init>(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        throw r6;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0584:
        r0 = r37;
        r1 = r41;
        r36 = r0.getOutputBuffer(r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0561;
    L_0x058d:
        r0 = r48;
        r6 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 1;
        if (r6 <= r10) goto L_0x05b6;
    L_0x0594:
        r0 = r48;
        r6 = r0.flags;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r6 & 2;
        if (r6 != 0) goto L_0x05ca;
    L_0x059c:
        r6 = 0;
        r0 = r57;
        r1 = r85;
        r2 = r36;
        r3 = r48;
        r6 = r0.writeSampleData(r1, r2, r3, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x05b6;
    L_0x05ab:
        r6 = 0;
        r10 = 0;
        r0 = r90;
        r1 = r91;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x05b6:
        r0 = r48;
        r6 = r0.flags;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r6 & 4;
        if (r6 == 0) goto L_0x0680;
    L_0x05be:
        r62 = 1;
    L_0x05c0:
        r6 = 0;
        r0 = r37;
        r1 = r41;
        r0.releaseOutputBuffer(r1, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0403;
    L_0x05ca:
        r6 = -5;
        r0 = r85;
        if (r0 != r6) goto L_0x05b6;
    L_0x05cf:
        r0 = r48;
        r6 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = new byte[r6];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r29 = r0;
        r0 = r48;
        r6 = r0.offset;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r48;
        r10 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r6 + r10;
        r0 = r36;
        r0.limit(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r48;
        r6 = r0.offset;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r36;
        r0.position(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r36;
        r1 = r29;
        r0.get(r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r75 = 0;
        r67 = 0;
        r0 = r48;
        r6 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r23 = r6 + -1;
    L_0x05ff:
        if (r23 < 0) goto L_0x0652;
    L_0x0601:
        r6 = 3;
        r0 = r23;
        if (r0 <= r6) goto L_0x0652;
    L_0x0606:
        r6 = r29[r23];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 1;
        if (r6 != r10) goto L_0x067d;
    L_0x060b:
        r6 = r23 + -1;
        r6 = r29[r6];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 != 0) goto L_0x067d;
    L_0x0611:
        r6 = r23 + -2;
        r6 = r29[r6];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 != 0) goto L_0x067d;
    L_0x0617:
        r6 = r23 + -3;
        r6 = r29[r6];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 != 0) goto L_0x067d;
    L_0x061d:
        r6 = r23 + -3;
        r75 = java.nio.ByteBuffer.allocate(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r48;
        r6 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r23 + -3;
        r6 = r6 - r10;
        r67 = java.nio.ByteBuffer.allocate(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = 0;
        r10 = r23 + -3;
        r0 = r75;
        r1 = r29;
        r6 = r0.put(r1, r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 0;
        r6.position(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r23 + -3;
        r0 = r48;
        r10 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = r23 + -3;
        r10 = r10 - r11;
        r0 = r67;
        r1 = r29;
        r6 = r0.put(r1, r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 0;
        r6.position(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0652:
        r6 = "video/avc";
        r0 = r72;
        r1 = r70;
        r59 = android.media.MediaFormat.createVideoFormat(r6, r0, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r75 == 0) goto L_0x0672;
    L_0x065e:
        if (r67 == 0) goto L_0x0672;
    L_0x0660:
        r6 = "csd-0";
        r0 = r59;
        r1 = r75;
        r0.setByteBuffer(r6, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "csd-1";
        r0 = r59;
        r1 = r67;
        r0.setByteBuffer(r6, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0672:
        r6 = 0;
        r0 = r57;
        r1 = r59;
        r85 = r0.addTrack(r1, r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x05b6;
    L_0x067d:
        r23 = r23 + -1;
        goto L_0x05ff;
    L_0x0680:
        r62 = 0;
        goto L_0x05c0;
    L_0x0684:
        r6 = -3;
        r0 = r33;
        if (r0 == r6) goto L_0x03eb;
    L_0x0689:
        r6 = -2;
        r0 = r33;
        if (r0 != r6) goto L_0x06ae;
    L_0x068e:
        r59 = r4.getOutputFormat();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = "tmessages";
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "newFormat = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r59;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        org.telegram.messenger.FileLog.m11e(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x03eb;
    L_0x06ae:
        if (r33 >= 0) goto L_0x06cb;
    L_0x06b0:
        r6 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r33;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6.<init>(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        throw r6;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x06cb:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 < r10) goto L_0x0780;
    L_0x06d1:
        r0 = r48;
        r6 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 == 0) goto L_0x077c;
    L_0x06d7:
        r34 = 1;
    L_0x06d9:
        r10 = 0;
        r6 = (r18 > r10 ? 1 : (r18 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x06f7;
    L_0x06df:
        r0 = r48;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r6 < 0) goto L_0x06f7;
    L_0x06e7:
        r50 = 1;
        r30 = 1;
        r34 = 0;
        r0 = r48;
        r6 = r0.flags;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r6 | 4;
        r0 = r48;
        r0.flags = r6;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x06f7:
        r10 = 0;
        r6 = (r76 > r10 ? 1 : (r76 == r10 ? 0 : -1));
        if (r6 <= 0) goto L_0x0735;
    L_0x06fd:
        r10 = -1;
        r6 = (r88 > r10 ? 1 : (r88 == r10 ? 0 : -1));
        if (r6 != 0) goto L_0x0735;
    L_0x0703:
        r0 = r48;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = (r10 > r76 ? 1 : (r10 == r76 ? 0 : -1));
        if (r6 >= 0) goto L_0x0797;
    L_0x070b:
        r34 = 0;
        r6 = "tmessages";
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10.<init>();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = "drop frame startTime = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r76;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r11 = " present time = ";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r48;
        r12 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.append(r12);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r10.toString();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        org.telegram.messenger.FileLog.m11e(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0735:
        r0 = r33;
        r1 = r34;
        r4.releaseOutputBuffer(r0, r1);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r34 == 0) goto L_0x0760;
    L_0x073e:
        r44 = 0;
        r64.awaitNewImage();	 Catch:{ Exception -> 0x079e, all -> 0x04aa }
    L_0x0743:
        if (r44 != 0) goto L_0x0760;
    L_0x0745:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 < r10) goto L_0x07a9;
    L_0x074b:
        r6 = 0;
        r0 = r64;
        r0.drawImage(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r0 = r48;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r12 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r10 = r10 * r12;
        r0 = r53;
        r0.setPresentationTime(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r53.swapBuffers();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
    L_0x0760:
        r0 = r48;
        r6 = r0.flags;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = r6 & 4;
        if (r6 == 0) goto L_0x03eb;
    L_0x0768:
        r32 = 0;
        r6 = "tmessages";
        r10 = "decoder stream end";
        org.telegram.messenger.FileLog.m11e(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = 18;
        if (r6 < r10) goto L_0x07e8;
    L_0x0777:
        r37.signalEndOfInputStream();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x03eb;
    L_0x077c:
        r34 = 0;
        goto L_0x06d9;
    L_0x0780:
        r0 = r48;
        r6 = r0.size;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r6 != 0) goto L_0x0790;
    L_0x0786:
        r0 = r48;
        r10 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r12 = 0;
        r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x0794;
    L_0x0790:
        r34 = 1;
    L_0x0792:
        goto L_0x06d9;
    L_0x0794:
        r34 = 0;
        goto L_0x0792;
    L_0x0797:
        r0 = r48;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r88 = r0;
        goto L_0x0735;
    L_0x079e:
        r35 = move-exception;
        r44 = 1;
        r6 = "tmessages";
        r0 = r35;
        org.telegram.messenger.FileLog.m13e(r6, r0);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0743;
    L_0x07a9:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r37;
        r5 = r0.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r5 < 0) goto L_0x07df;
    L_0x07b3:
        r6 = 1;
        r0 = r64;
        r0.drawImage(r6);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r8 = r64.getFrame();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r9 = r38[r5];	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r9.clear();	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r10 = r28;
        r11 = r72;
        r12 = r70;
        r13 = r66;
        r14 = r78;
        org.telegram.messenger.Utilities.convertVideoFrame(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r12 = 0;
        r0 = r48;
        r14 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r16 = 0;
        r10 = r37;
        r11 = r5;
        r13 = r25;
        r10.queueInputBuffer(r11, r12, r13, r14, r16);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0760;
    L_0x07df:
        r6 = "tmessages";
        r10 = "input buffer not available";
        org.telegram.messenger.FileLog.m11e(r6, r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x0760;
    L_0x07e8:
        r10 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r37;
        r5 = r0.dequeueInputBuffer(r10);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        if (r5 < 0) goto L_0x03eb;
    L_0x07f2:
        r12 = 0;
        r13 = 1;
        r0 = r48;
        r14 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        r16 = 4;
        r10 = r37;
        r11 = r5;
        r10.queueInputBuffer(r11, r12, r13, r14, r16);	 Catch:{ Exception -> 0x019a, all -> 0x04aa }
        goto L_0x03eb;
    L_0x0802:
        r10 = -1;
        r6 = (r88 > r10 ? 1 : (r88 == r10 ? 0 : -1));
        if (r6 == 0) goto L_0x08ba;
    L_0x0808:
        r16 = r88;
        goto L_0x01a6;
    L_0x080c:
        r21 = 0;
        r11 = r90;
        r12 = r91;
        r13 = r46;
        r14 = r57;
        r15 = r48;
        r16 = r76;
        r88 = r11.readAndWriteTrack(r12, r13, r14, r15, r16, r18, r20, r21);	 Catch:{ Exception -> 0x08a7, all -> 0x04aa }
        r10 = -1;
        r6 = (r88 > r10 ? 1 : (r88 == r10 ? 0 : -1));
        if (r6 == 0) goto L_0x08b6;
    L_0x0824:
        r16 = r88;
        goto L_0x01ca;
    L_0x0828:
        r35 = move-exception;
        r6 = "tmessages";
        r0 = r35;
        org.telegram.messenger.FileLog.m13e(r6, r0);
        goto L_0x01e8;
    L_0x0832:
        r35 = move-exception;
        r16 = r86;
    L_0x0835:
        r43 = 1;
        r6 = "tmessages";
        r0 = r35;
        org.telegram.messenger.FileLog.m13e(r6, r0);	 Catch:{ all -> 0x08a4 }
        if (r45 == 0) goto L_0x0843;
    L_0x0840:
        r45.release();
    L_0x0843:
        if (r57 == 0) goto L_0x084b;
    L_0x0845:
        r6 = 0;
        r0 = r57;
        r0.finishMovie(r6);	 Catch:{ Exception -> 0x086b }
    L_0x084b:
        r6 = "tmessages";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "time = ";
        r10 = r10.append(r11);
        r12 = java.lang.System.currentTimeMillis();
        r12 = r12 - r80;
        r10 = r10.append(r12);
        r10 = r10.toString();
        org.telegram.messenger.FileLog.m11e(r6, r10);
        goto L_0x0208;
    L_0x086b:
        r35 = move-exception;
        r6 = "tmessages";
        r0 = r35;
        org.telegram.messenger.FileLog.m13e(r6, r0);
        goto L_0x084b;
    L_0x0874:
        r35 = move-exception;
        r10 = "tmessages";
        r0 = r35;
        org.telegram.messenger.FileLog.m13e(r10, r0);
        goto L_0x04bc;
    L_0x087e:
        r6 = r68.edit();
        r10 = "isPreviousOk";
        r11 = 1;
        r6 = r6.putBoolean(r10, r11);
        r6.commit();
        r6 = 1;
        r10 = 1;
        r0 = r90;
        r1 = r91;
        r2 = r20;
        r0.didWriteData(r1, r2, r6, r10);
        r6 = 0;
        goto L_0x00c4;
    L_0x089a:
        r6 = move-exception;
        r16 = r86;
        goto L_0x04af;
    L_0x089f:
        r6 = move-exception;
        r45 = r46;
        goto L_0x04af;
    L_0x08a4:
        r6 = move-exception;
        goto L_0x04af;
    L_0x08a7:
        r35 = move-exception;
        r45 = r46;
        r16 = r86;
        goto L_0x0835;
    L_0x08ad:
        r35 = move-exception;
        r45 = r46;
        goto L_0x0835;
    L_0x08b1:
        r35 = move-exception;
        r53 = r54;
        goto L_0x019b;
    L_0x08b6:
        r16 = r86;
        goto L_0x01ca;
    L_0x08ba:
        r16 = r86;
        goto L_0x01a6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaController.convertVideo(org.telegram.messenger.MessageObject):boolean");
    }
}
