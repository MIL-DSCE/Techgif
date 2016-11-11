package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.VideoSeekBarView;
import org.telegram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

@TargetApi(16)
public class VideoEditorActivity extends BaseFragment implements SurfaceTextureListener, NotificationCenterDelegate {
    private long audioFramesSize;
    private int bitrate;
    private CheckBox compressVideo;
    private View controlView;
    private boolean created;
    private VideoEditorActivityDelegate delegate;
    private TextView editedSizeTextView;
    private long endTime;
    private long esimatedDuration;
    private int estimatedSize;
    private float lastProgress;
    private boolean needSeek;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private TextView originalSizeTextView;
    private int originalWidth;
    private ImageView playButton;
    private boolean playerPrepared;
    private Runnable progressRunnable;
    private int resultHeight;
    private int resultWidth;
    private int rotationValue;
    private long startTime;
    private final Object sync;
    private View textContainerView;
    private TextureView textureView;
    private Thread thread;
    private View videoContainerView;
    private float videoDuration;
    private long videoFramesSize;
    private String videoPath;
    private MediaPlayer videoPlayer;
    private VideoSeekBarView videoSeekBarView;
    private VideoTimelineView videoTimelineView;

    /* renamed from: org.telegram.ui.VideoEditorActivity.1 */
    class C14771 implements Runnable {

        /* renamed from: org.telegram.ui.VideoEditorActivity.1.1 */
        class C14761 implements Runnable {
            C14761() {
            }

            public void run() {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.videoPlayer.isPlaying()) {
                    float startTime = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                    float endTime = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                    if (startTime == endTime) {
                        startTime = endTime - 0.01f;
                    }
                    float lrdiff = VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                    float progress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() + (lrdiff * ((((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - startTime) / (endTime - startTime)));
                    if (progress > VideoEditorActivity.this.lastProgress) {
                        VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
                        VideoEditorActivity.this.lastProgress = progress;
                    }
                    if (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) >= endTime) {
                        try {
                            VideoEditorActivity.this.videoPlayer.pause();
                            VideoEditorActivity.this.onPlayComplete();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                }
            }
        }

        C14771() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r5 = this;
        L_0x0000:
            r2 = org.telegram.ui.VideoEditorActivity.this;
            r3 = r2.sync;
            monitor-enter(r3);
            r2 = org.telegram.ui.VideoEditorActivity.this;	 Catch:{ Exception -> 0x0030 }
            r2 = r2.videoPlayer;	 Catch:{ Exception -> 0x0030 }
            if (r2 == 0) goto L_0x002e;
        L_0x000f:
            r2 = org.telegram.ui.VideoEditorActivity.this;	 Catch:{ Exception -> 0x0030 }
            r2 = r2.videoPlayer;	 Catch:{ Exception -> 0x0030 }
            r2 = r2.isPlaying();	 Catch:{ Exception -> 0x0030 }
            if (r2 == 0) goto L_0x002e;
        L_0x001b:
            r1 = 1;
        L_0x001c:
            monitor-exit(r3);	 Catch:{ all -> 0x0038 }
            if (r1 != 0) goto L_0x003b;
        L_0x001f:
            r2 = org.telegram.ui.VideoEditorActivity.this;
            r3 = r2.sync;
            monitor-enter(r3);
            r2 = org.telegram.ui.VideoEditorActivity.this;	 Catch:{ all -> 0x0050 }
            r4 = 0;
            r2.thread = r4;	 Catch:{ all -> 0x0050 }
            monitor-exit(r3);	 Catch:{ all -> 0x0050 }
            return;
        L_0x002e:
            r1 = 0;
            goto L_0x001c;
        L_0x0030:
            r0 = move-exception;
            r1 = 0;
            r2 = "tmessages";
            org.telegram.messenger.FileLog.m13e(r2, r0);	 Catch:{ all -> 0x0038 }
            goto L_0x001c;
        L_0x0038:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0038 }
            throw r2;
        L_0x003b:
            r2 = new org.telegram.ui.VideoEditorActivity$1$1;
            r2.<init>();
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
            r2 = 50;
            java.lang.Thread.sleep(r2);	 Catch:{ Exception -> 0x0049 }
            goto L_0x0000;
        L_0x0049:
            r0 = move-exception;
            r2 = "tmessages";
            org.telegram.messenger.FileLog.m13e(r2, r0);
            goto L_0x0000;
        L_0x0050:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0050 }
            throw r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VideoEditorActivity.1.run():void");
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.2 */
    class C14792 implements OnCompletionListener {

        /* renamed from: org.telegram.ui.VideoEditorActivity.2.1 */
        class C14781 implements Runnable {
            C14781() {
            }

            public void run() {
                VideoEditorActivity.this.onPlayComplete();
            }
        }

        C14792() {
        }

        public void onCompletion(MediaPlayer mp) {
            AndroidUtilities.runOnUIThread(new C14781());
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.3 */
    class C14803 implements OnPreparedListener {
        C14803() {
        }

        public void onPrepared(MediaPlayer mp) {
            VideoEditorActivity.this.playerPrepared = true;
            if (VideoEditorActivity.this.videoTimelineView != null && VideoEditorActivity.this.videoPlayer != null) {
                VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
            }
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.5 */
    class C14815 implements OnCheckedChangeListener {
        C14815() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            editor.putBoolean("compress_video", isChecked);
            editor.commit();
            VideoEditorActivity.this.updateVideoEditedInfo();
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.8 */
    class C14828 implements OnClickListener {
        C14828() {
        }

        public void onClick(View v) {
            VideoEditorActivity.this.play();
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.9 */
    class C14839 implements OnGlobalLayoutListener {
        C14839() {
        }

        public void onGlobalLayout() {
            VideoEditorActivity.this.fixLayoutInternal();
            if (VideoEditorActivity.this.fragmentView == null) {
                return;
            }
            if (VERSION.SDK_INT < 16) {
                VideoEditorActivity.this.fragmentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                VideoEditorActivity.this.fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    public interface VideoEditorActivityDelegate {
        void didFinishEditVideo(String str, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, long j3, long j4);
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.4 */
    class C19574 extends ActionBarMenuOnItemClick {
        C19574() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                VideoEditorActivity.this.finishFragment();
            } else if (id == 1) {
                synchronized (VideoEditorActivity.this.sync) {
                    if (VideoEditorActivity.this.videoPlayer != null) {
                        try {
                            VideoEditorActivity.this.videoPlayer.stop();
                            VideoEditorActivity.this.videoPlayer.release();
                            VideoEditorActivity.this.videoPlayer = null;
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                }
                if (VideoEditorActivity.this.delegate != null) {
                    if (VideoEditorActivity.this.compressVideo.getVisibility() == 8 || (VideoEditorActivity.this.compressVideo.getVisibility() == 0 && !VideoEditorActivity.this.compressVideo.isChecked())) {
                        VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.originalBitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration);
                    } else {
                        VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.resultWidth, VideoEditorActivity.this.resultHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.bitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration);
                    }
                }
                VideoEditorActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.6 */
    class C19586 implements VideoTimelineViewDelegate {
        C19586() {
        }

        public void onLeftProgressChanged(float progress) {
            if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                try {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        VideoEditorActivity.this.videoPlayer.pause();
                        VideoEditorActivity.this.playButton.setImageResource(C0691R.drawable.video_play);
                    }
                    VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                    VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                VideoEditorActivity.this.needSeek = true;
                VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
                VideoEditorActivity.this.updateVideoEditedInfo();
            }
        }

        public void onRifhtProgressChanged(float progress) {
            if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                try {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        VideoEditorActivity.this.videoPlayer.pause();
                        VideoEditorActivity.this.playButton.setImageResource(C0691R.drawable.video_play);
                    }
                    VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                    VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                VideoEditorActivity.this.needSeek = true;
                VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
                VideoEditorActivity.this.updateVideoEditedInfo();
            }
        }
    }

    /* renamed from: org.telegram.ui.VideoEditorActivity.7 */
    class C19597 implements SeekBarDelegate {
        C19597() {
        }

        public void onSeekBarDrag(float progress) {
            if (progress < VideoEditorActivity.this.videoTimelineView.getLeftProgress()) {
                progress = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
            } else if (progress > VideoEditorActivity.this.videoTimelineView.getRightProgress()) {
                progress = VideoEditorActivity.this.videoTimelineView.getRightProgress();
                VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
            }
            if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                    try {
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                        VideoEditorActivity.this.lastProgress = progress;
                        return;
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                        return;
                    }
                }
                VideoEditorActivity.this.lastProgress = progress;
                VideoEditorActivity.this.needSeek = true;
            }
        }
    }

    public VideoEditorActivity(Bundle args) {
        super(args);
        this.created = false;
        this.videoPlayer = null;
        this.videoTimelineView = null;
        this.videoContainerView = null;
        this.originalSizeTextView = null;
        this.editedSizeTextView = null;
        this.textContainerView = null;
        this.playButton = null;
        this.videoSeekBarView = null;
        this.textureView = null;
        this.controlView = null;
        this.compressVideo = null;
        this.playerPrepared = false;
        this.videoPath = null;
        this.lastProgress = 0.0f;
        this.needSeek = false;
        this.sync = new Object();
        this.thread = null;
        this.rotationValue = 0;
        this.originalWidth = 0;
        this.originalHeight = 0;
        this.resultWidth = 0;
        this.resultHeight = 0;
        this.bitrate = 0;
        this.originalBitrate = 0;
        this.videoDuration = 0.0f;
        this.startTime = 0;
        this.endTime = 0;
        this.audioFramesSize = 0;
        this.videoFramesSize = 0;
        this.estimatedSize = 0;
        this.esimatedDuration = 0;
        this.originalSize = 0;
        this.progressRunnable = new C14771();
        this.videoPath = args.getString("videoPath");
    }

    public boolean onFragmentCreate() {
        if (this.created) {
            return true;
        }
        if (this.videoPath == null || !processOpenVideo()) {
            return false;
        }
        this.videoPlayer = new MediaPlayer();
        this.videoPlayer.setOnCompletionListener(new C14792());
        this.videoPlayer.setOnPreparedListener(new C14803());
        try {
            this.videoPlayer.setDataSource(this.videoPath);
            this.videoPlayer.prepareAsync();
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
            this.created = true;
            return super.onFragmentCreate();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return false;
        }
    }

    public void onFragmentDestroy() {
        if (this.videoTimelineView != null) {
            this.videoTimelineView.destroy();
        }
        if (this.videoPlayer != null) {
            try {
                this.videoPlayer.stop();
                this.videoPlayer.release();
                this.videoPlayer = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        int i;
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("EditVideo", C0691R.string.EditVideo));
        this.actionBar.setActionBarMenuOnItemClick(new C19574());
        this.actionBar.createMenu().addItemWithWidth(1, C0691R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(C0691R.layout.video_editor_layout, null, false);
        this.originalSizeTextView = (TextView) this.fragmentView.findViewById(C0691R.id.original_size);
        this.editedSizeTextView = (TextView) this.fragmentView.findViewById(C0691R.id.edited_size);
        this.videoContainerView = this.fragmentView.findViewById(C0691R.id.video_container);
        this.textContainerView = this.fragmentView.findViewById(C0691R.id.info_container);
        this.controlView = this.fragmentView.findViewById(C0691R.id.control_layout);
        this.compressVideo = (CheckBox) this.fragmentView.findViewById(C0691R.id.compress_video);
        this.compressVideo.setText(LocaleController.getString("CompressVideo", C0691R.string.CompressVideo));
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        CheckBox checkBox = this.compressVideo;
        if (this.originalHeight == this.resultHeight && this.originalWidth == this.resultWidth) {
            i = 8;
        } else {
            i = 0;
        }
        checkBox.setVisibility(i);
        this.compressVideo.setChecked(preferences.getBoolean("compress_video", true));
        this.compressVideo.setOnCheckedChangeListener(new C14815());
        if (VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo codecInfo = MediaController.selectCodec(MediaController.MIME_TYPE);
                if (codecInfo == null) {
                    this.compressVideo.setVisibility(8);
                } else {
                    String name = codecInfo.getName();
                    if (name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc") || name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                        this.compressVideo.setVisibility(8);
                    } else if (MediaController.selectColorFormat(codecInfo, MediaController.MIME_TYPE) == 0) {
                        this.compressVideo.setVisibility(8);
                    }
                }
            } catch (Throwable e) {
                this.compressVideo.setVisibility(8);
                FileLog.m13e("tmessages", e);
            }
        }
        ((TextView) this.fragmentView.findViewById(C0691R.id.original_title)).setText(LocaleController.getString("OriginalVideo", C0691R.string.OriginalVideo));
        ((TextView) this.fragmentView.findViewById(C0691R.id.edited_title)).setText(LocaleController.getString("EditedVideo", C0691R.string.EditedVideo));
        this.videoTimelineView = (VideoTimelineView) this.fragmentView.findViewById(C0691R.id.video_timeline_view);
        this.videoTimelineView.setVideoPath(this.videoPath);
        this.videoTimelineView.setDelegate(new C19586());
        this.videoSeekBarView = (VideoSeekBarView) this.fragmentView.findViewById(C0691R.id.video_seekbar);
        this.videoSeekBarView.delegate = new C19597();
        this.playButton = (ImageView) this.fragmentView.findViewById(C0691R.id.play_button);
        this.playButton.setOnClickListener(new C14828());
        this.textureView = (TextureView) this.fragmentView.findViewById(C0691R.id.video_view);
        this.textureView.setSurfaceTextureListener(this);
        updateVideoOriginalInfo();
        updateVideoEditedInfo();
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    private void setPlayerSurface() {
        if (this.textureView != null && this.textureView.isAvailable() && this.videoPlayer != null) {
            try {
                this.videoPlayer.setSurface(new Surface(this.textureView.getSurfaceTexture()));
                if (this.playerPrepared) {
                    this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void onResume() {
        super.onResume();
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        setPlayerSurface();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (this.videoPlayer != null) {
            this.videoPlayer.setDisplay(null);
        }
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void onPlayComplete() {
        if (this.playButton != null) {
            this.playButton.setImageResource(C0691R.drawable.video_play);
        }
        if (!(this.videoSeekBarView == null || this.videoTimelineView == null)) {
            this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
        }
        try {
            if (this.videoPlayer != null && this.videoTimelineView != null) {
                this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private void updateVideoOriginalInfo() {
        if (this.originalSizeTextView != null) {
            int width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalHeight : this.originalWidth;
            int height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalWidth : this.originalHeight;
            String videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
            long duration = (long) Math.ceil((double) this.videoDuration);
            int seconds = ((int) Math.ceil((double) (duration / 1000))) - (((int) ((duration / 1000) / 60)) * 60);
            String videoTimeSize = String.format("%d:%02d, %s", new Object[]{Integer.valueOf((int) ((duration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize(this.originalSize)});
            this.originalSizeTextView.setText(String.format("%s, %s", new Object[]{videoDimension, videoTimeSize}));
        }
    }

    private void updateVideoEditedInfo() {
        if (this.editedSizeTextView != null) {
            int width;
            int height;
            this.esimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressVideo.getVisibility() == 8 || (this.compressVideo.getVisibility() == 0 && !this.compressVideo.isChecked())) {
                width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalHeight : this.originalWidth;
                height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.esimatedDuration) / this.videoDuration));
            } else {
                width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultHeight : this.resultWidth;
                height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultWidth : this.resultHeight;
                this.estimatedSize = calculateEstimatedSize(((float) this.esimatedDuration) / this.videoDuration);
            }
            if (this.videoTimelineView.getLeftProgress() == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
            }
            if (this.videoTimelineView.getRightProgress() == TouchHelperCallback.ALPHA_FULL) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
            }
            String videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
            int seconds = ((int) Math.ceil((double) (this.esimatedDuration / 1000))) - (((int) ((this.esimatedDuration / 1000) / 60)) * 60);
            String videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.esimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
            this.editedSizeTextView.setText(String.format("%s, %s", new Object[]{videoDimension, videoTimeSize}));
        }
    }

    private void fixVideoSize() {
        if (this.fragmentView != null && getParentActivity() != null) {
            int viewHeight;
            int width;
            int height;
            if (AndroidUtilities.isTablet()) {
                viewHeight = AndroidUtilities.dp(472.0f);
            } else {
                viewHeight = (AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight) - ActionBar.getCurrentActionBarHeight();
            }
            if (AndroidUtilities.isTablet()) {
                int i;
                width = AndroidUtilities.dp(490.0f);
                if (this.compressVideo.getVisibility() == 0) {
                    i = 20;
                } else {
                    i = 0;
                }
                height = viewHeight - AndroidUtilities.dp((float) (i + 276));
            } else if (getParentActivity().getResources().getConfiguration().orientation == 2) {
                width = (AndroidUtilities.displaySize.x / 3) - AndroidUtilities.dp(24.0f);
                height = viewHeight - AndroidUtilities.dp(32.0f);
            } else {
                width = AndroidUtilities.displaySize.x;
                height = viewHeight - AndroidUtilities.dp((float) ((this.compressVideo.getVisibility() == 0 ? 20 : 0) + 276));
            }
            int aWidth = width;
            int aHeight = height;
            int vwidth = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalHeight : this.originalWidth;
            int vheight = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalWidth : this.originalHeight;
            float ar = ((float) vwidth) / ((float) vheight);
            if (((float) width) / ((float) vwidth) > ((float) height) / ((float) vheight)) {
                width = (int) (((float) height) * ar);
            } else {
                height = (int) (((float) width) / ar);
            }
            if (this.textureView != null) {
                LayoutParams layoutParams = (LayoutParams) this.textureView.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
                this.textureView.setLayoutParams(layoutParams);
            }
        }
    }

    private void fixLayoutInternal() {
        int i = 20;
        if (getParentActivity() != null) {
            LayoutParams layoutParams;
            if (AndroidUtilities.isTablet() || getParentActivity().getResources().getConfiguration().orientation != 2) {
                layoutParams = (LayoutParams) this.videoContainerView.getLayoutParams();
                layoutParams.topMargin = AndroidUtilities.dp(16.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp((float) ((this.compressVideo.getVisibility() == 0 ? 20 : 0) + 260));
                layoutParams.width = -1;
                layoutParams.leftMargin = 0;
                this.videoContainerView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.controlView.getLayoutParams();
                layoutParams.topMargin = 0;
                layoutParams.leftMargin = 0;
                if (this.compressVideo.getVisibility() != 0) {
                    i = 0;
                }
                layoutParams.bottomMargin = AndroidUtilities.dp((float) (i + 150));
                layoutParams.width = -1;
                layoutParams.gravity = 80;
                this.controlView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.textContainerView.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.leftMargin = AndroidUtilities.dp(16.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(16.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(16.0f);
                this.textContainerView.setLayoutParams(layoutParams);
            } else {
                layoutParams = (LayoutParams) this.videoContainerView.getLayoutParams();
                layoutParams.topMargin = AndroidUtilities.dp(16.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(16.0f);
                layoutParams.width = (AndroidUtilities.displaySize.x / 3) - AndroidUtilities.dp(24.0f);
                layoutParams.leftMargin = AndroidUtilities.dp(16.0f);
                this.videoContainerView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.controlView.getLayoutParams();
                layoutParams.topMargin = AndroidUtilities.dp(16.0f);
                layoutParams.bottomMargin = 0;
                layoutParams.width = ((AndroidUtilities.displaySize.x / 3) * 2) - AndroidUtilities.dp(32.0f);
                layoutParams.leftMargin = (AndroidUtilities.displaySize.x / 3) + AndroidUtilities.dp(16.0f);
                layoutParams.gravity = 48;
                this.controlView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.textContainerView.getLayoutParams();
                layoutParams.width = ((AndroidUtilities.displaySize.x / 3) * 2) - AndroidUtilities.dp(32.0f);
                layoutParams.leftMargin = (AndroidUtilities.displaySize.x / 3) + AndroidUtilities.dp(16.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(16.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(16.0f);
                this.textContainerView.setLayoutParams(layoutParams);
            }
            fixVideoSize();
            this.videoTimelineView.clearFrames();
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(new C14839());
        }
    }

    private void play() {
        if (this.videoPlayer != null && this.playerPrepared) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                this.playButton.setImageResource(C0691R.drawable.video_play);
                return;
            }
            try {
                this.playButton.setImageDrawable(null);
                this.lastProgress = 0.0f;
                if (this.needSeek) {
                    this.videoPlayer.seekTo((int) (this.videoDuration * this.videoSeekBarView.getProgress()));
                    this.needSeek = false;
                }
                this.videoPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                    public void onSeekComplete(MediaPlayer mp) {
                        float startTime = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                        float endTime = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                        if (startTime == endTime) {
                            startTime = endTime - 0.01f;
                        }
                        VideoEditorActivity.this.lastProgress = (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - startTime) / (endTime - startTime);
                        VideoEditorActivity.this.lastProgress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() + (VideoEditorActivity.this.lastProgress * (VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress()));
                        VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.lastProgress);
                    }
                });
                this.videoPlayer.start();
                synchronized (this.sync) {
                    if (this.thread == null) {
                        this.thread = new Thread(this.progressRunnable);
                        this.thread.start();
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void setDelegate(VideoEditorActivityDelegate delegate) {
        this.delegate = delegate;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean processOpenVideo() {
        /*
        r34 = this;
        r9 = new java.io.File;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.videoPath;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r9.<init>(r0);	 Catch:{ Exception -> 0x011d }
        r30 = r9.length();	 Catch:{ Exception -> 0x011d }
        r0 = r30;
        r2 = r34;
        r2.originalSize = r0;	 Catch:{ Exception -> 0x011d }
        r15 = new com.coremedia.iso.IsoFile;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.videoPath;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r15.<init>(r0);	 Catch:{ Exception -> 0x011d }
        r30 = "/moov/trak/";
        r0 = r30;
        r7 = com.googlecode.mp4parser.util.Path.getPaths(r15, r0);	 Catch:{ Exception -> 0x011d }
        r29 = 0;
        r13 = 1;
        r14 = 1;
        r30 = "/moov/trak/mdia/minf/stbl/stsd/mp4a/";
        r0 = r30;
        r6 = com.googlecode.mp4parser.util.Path.getPath(r15, r0);	 Catch:{ Exception -> 0x011d }
        if (r6 != 0) goto L_0x003b;
    L_0x003a:
        r14 = 0;
    L_0x003b:
        if (r14 != 0) goto L_0x0040;
    L_0x003d:
        r30 = 0;
    L_0x003f:
        return r30;
    L_0x0040:
        r30 = "/moov/trak/mdia/minf/stbl/stsd/avc1/";
        r0 = r30;
        r6 = com.googlecode.mp4parser.util.Path.getPath(r15, r0);	 Catch:{ Exception -> 0x011d }
        if (r6 != 0) goto L_0x004b;
    L_0x004a:
        r13 = 0;
    L_0x004b:
        r11 = r7.iterator();	 Catch:{ Exception -> 0x011d }
    L_0x004f:
        r30 = r11.hasNext();	 Catch:{ Exception -> 0x011d }
        if (r30 == 0) goto L_0x0142;
    L_0x0055:
        r5 = r11.next();	 Catch:{ Exception -> 0x011d }
        r5 = (com.coremedia.iso.boxes.Box) r5;	 Catch:{ Exception -> 0x011d }
        r0 = r5;
        r0 = (com.coremedia.iso.boxes.TrackBox) r0;	 Catch:{ Exception -> 0x011d }
        r28 = r0;
        r22 = 0;
        r26 = 0;
        r18 = r28.getMediaBox();	 Catch:{ Exception -> 0x0129 }
        r19 = r18.getMediaHeaderBox();	 Catch:{ Exception -> 0x0129 }
        r30 = r18.getMediaInformationBox();	 Catch:{ Exception -> 0x0129 }
        r30 = r30.getSampleTableBox();	 Catch:{ Exception -> 0x0129 }
        r20 = r30.getSampleSizeBox();	 Catch:{ Exception -> 0x0129 }
        r4 = r20.getSampleSizes();	 Catch:{ Exception -> 0x0129 }
        r0 = r4.length;	 Catch:{ Exception -> 0x0129 }
        r16 = r0;
        r12 = 0;
    L_0x0080:
        r0 = r16;
        if (r12 >= r0) goto L_0x008b;
    L_0x0084:
        r24 = r4[r12];	 Catch:{ Exception -> 0x0129 }
        r22 = r22 + r24;
        r12 = r12 + 1;
        goto L_0x0080;
    L_0x008b:
        r30 = r19.getDuration();	 Catch:{ Exception -> 0x0129 }
        r0 = r30;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0129 }
        r30 = r0;
        r32 = r19.getTimescale();	 Catch:{ Exception -> 0x0129 }
        r0 = r32;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0129 }
        r31 = r0;
        r30 = r30 / r31;
        r0 = r30;
        r1 = r34;
        r1.videoDuration = r0;	 Catch:{ Exception -> 0x0129 }
        r30 = 8;
        r30 = r30 * r22;
        r0 = r30;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0129 }
        r30 = r0;
        r0 = r34;
        r0 = r0.videoDuration;	 Catch:{ Exception -> 0x0129 }
        r31 = r0;
        r30 = r30 / r31;
        r0 = r30;
        r0 = (int) r0;
        r30 = r0;
        r0 = r30;
        r0 = (long) r0;
        r26 = r0;
    L_0x00c0:
        r10 = r28.getTrackHeaderBox();	 Catch:{ Exception -> 0x011d }
        r30 = r10.getWidth();	 Catch:{ Exception -> 0x011d }
        r32 = 0;
        r30 = (r30 > r32 ? 1 : (r30 == r32 ? 0 : -1));
        if (r30 == 0) goto L_0x0132;
    L_0x00ce:
        r30 = r10.getHeight();	 Catch:{ Exception -> 0x011d }
        r32 = 0;
        r30 = (r30 > r32 ? 1 : (r30 == r32 ? 0 : -1));
        if (r30 == 0) goto L_0x0132;
    L_0x00d8:
        r29 = r10;
        r30 = 100000; // 0x186a0 float:1.4013E-40 double:4.94066E-319;
        r30 = r26 / r30;
        r32 = 100000; // 0x186a0 float:1.4013E-40 double:4.94066E-319;
        r30 = r30 * r32;
        r0 = r30;
        r0 = (int) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r1 = r34;
        r1.bitrate = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r30;
        r1 = r34;
        r1.originalBitrate = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.bitrate;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r31 = 900000; // 0xdbba0 float:1.261169E-39 double:4.44659E-318;
        r0 = r30;
        r1 = r31;
        if (r0 <= r1) goto L_0x010d;
    L_0x0104:
        r30 = 900000; // 0xdbba0 float:1.261169E-39 double:4.44659E-318;
        r0 = r30;
        r1 = r34;
        r1.bitrate = r0;	 Catch:{ Exception -> 0x011d }
    L_0x010d:
        r0 = r34;
        r0 = r0.videoFramesSize;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r30 = r30 + r22;
        r0 = r30;
        r2 = r34;
        r2.videoFramesSize = r0;	 Catch:{ Exception -> 0x011d }
        goto L_0x004f;
    L_0x011d:
        r8 = move-exception;
        r30 = "tmessages";
        r0 = r30;
        org.telegram.messenger.FileLog.m13e(r0, r8);
        r30 = 0;
        goto L_0x003f;
    L_0x0129:
        r8 = move-exception;
        r30 = "tmessages";
        r0 = r30;
        org.telegram.messenger.FileLog.m13e(r0, r8);	 Catch:{ Exception -> 0x011d }
        goto L_0x00c0;
    L_0x0132:
        r0 = r34;
        r0 = r0.audioFramesSize;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r30 = r30 + r22;
        r0 = r30;
        r2 = r34;
        r2.audioFramesSize = r0;	 Catch:{ Exception -> 0x011d }
        goto L_0x004f;
    L_0x0142:
        if (r29 != 0) goto L_0x0148;
    L_0x0144:
        r30 = 0;
        goto L_0x003f;
    L_0x0148:
        r17 = r29.getMatrix();	 Catch:{ Exception -> 0x011d }
        r30 = com.googlecode.mp4parser.util.Matrix.ROTATE_90;	 Catch:{ Exception -> 0x011d }
        r0 = r17;
        r1 = r30;
        r30 = r0.equals(r1);	 Catch:{ Exception -> 0x011d }
        if (r30 == 0) goto L_0x026b;
    L_0x0158:
        r30 = 90;
        r0 = r30;
        r1 = r34;
        r1.rotationValue = r0;	 Catch:{ Exception -> 0x011d }
    L_0x0160:
        r30 = r29.getWidth();	 Catch:{ Exception -> 0x011d }
        r0 = r30;
        r0 = (int) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r1 = r34;
        r1.originalWidth = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r30;
        r1 = r34;
        r1.resultWidth = r0;	 Catch:{ Exception -> 0x011d }
        r30 = r29.getHeight();	 Catch:{ Exception -> 0x011d }
        r0 = r30;
        r0 = (int) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r1 = r34;
        r1.originalHeight = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r30;
        r1 = r34;
        r1.resultHeight = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.resultWidth;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r31 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r0 = r30;
        r1 = r31;
        if (r0 > r1) goto L_0x01a6;
    L_0x0198:
        r0 = r34;
        r0 = r0.resultHeight;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r31 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r0 = r30;
        r1 = r31;
        if (r0 <= r1) goto L_0x0241;
    L_0x01a6:
        r0 = r34;
        r0 = r0.resultWidth;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r34;
        r0 = r0.resultHeight;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r0 = r30;
        r1 = r31;
        if (r0 <= r1) goto L_0x0297;
    L_0x01b8:
        r30 = 1142947840; // 0x44200000 float:640.0 double:5.646912627E-315;
        r0 = r34;
        r0 = r0.resultWidth;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r0 = r31;
        r0 = (float) r0;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r21 = r30 / r31;
    L_0x01c7:
        r0 = r34;
        r0 = r0.resultWidth;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r0 = (float) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r30 = r30 * r21;
        r0 = r30;
        r0 = (int) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r1 = r34;
        r1.resultWidth = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.resultHeight;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r0 = (float) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r30 = r30 * r21;
        r0 = r30;
        r0 = (int) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r1 = r34;
        r1.resultHeight = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.bitrate;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        if (r30 == 0) goto L_0x0241;
    L_0x01ff:
        r0 = r34;
        r0 = r0.bitrate;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r0 = (float) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r31 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r0 = r31;
        r1 = r21;
        r31 = java.lang.Math.max(r0, r1);	 Catch:{ Exception -> 0x011d }
        r30 = r30 * r31;
        r0 = r30;
        r0 = (int) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r1 = r34;
        r1.bitrate = r0;	 Catch:{ Exception -> 0x011d }
        r0 = r34;
        r0 = r0.bitrate;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r30 = r30 / 8;
        r0 = r30;
        r0 = (float) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r34;
        r0 = r0.videoDuration;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r30 = r30 * r31;
        r0 = r30;
        r0 = (long) r0;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r30;
        r2 = r34;
        r2.videoFramesSize = r0;	 Catch:{ Exception -> 0x011d }
    L_0x0241:
        if (r13 != 0) goto L_0x02a8;
    L_0x0243:
        r0 = r34;
        r0 = r0.resultWidth;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r34;
        r0 = r0.originalWidth;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r0 = r30;
        r1 = r31;
        if (r0 == r1) goto L_0x0267;
    L_0x0255:
        r0 = r34;
        r0 = r0.resultHeight;	 Catch:{ Exception -> 0x011d }
        r30 = r0;
        r0 = r34;
        r0 = r0.originalHeight;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r0 = r30;
        r1 = r31;
        if (r0 != r1) goto L_0x02a8;
    L_0x0267:
        r30 = 0;
        goto L_0x003f;
    L_0x026b:
        r30 = com.googlecode.mp4parser.util.Matrix.ROTATE_180;	 Catch:{ Exception -> 0x011d }
        r0 = r17;
        r1 = r30;
        r30 = r0.equals(r1);	 Catch:{ Exception -> 0x011d }
        if (r30 == 0) goto L_0x0281;
    L_0x0277:
        r30 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r30;
        r1 = r34;
        r1.rotationValue = r0;	 Catch:{ Exception -> 0x011d }
        goto L_0x0160;
    L_0x0281:
        r30 = com.googlecode.mp4parser.util.Matrix.ROTATE_270;	 Catch:{ Exception -> 0x011d }
        r0 = r17;
        r1 = r30;
        r30 = r0.equals(r1);	 Catch:{ Exception -> 0x011d }
        if (r30 == 0) goto L_0x0160;
    L_0x028d:
        r30 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r30;
        r1 = r34;
        r1.rotationValue = r0;	 Catch:{ Exception -> 0x011d }
        goto L_0x0160;
    L_0x0297:
        r30 = 1142947840; // 0x44200000 float:640.0 double:5.646912627E-315;
        r0 = r34;
        r0 = r0.resultHeight;	 Catch:{ Exception -> 0x011d }
        r31 = r0;
        r0 = r31;
        r0 = (float) r0;
        r31 = r0;
        r21 = r30 / r31;
        goto L_0x01c7;
    L_0x02a8:
        r0 = r34;
        r0 = r0.videoDuration;
        r30 = r0;
        r31 = 1148846080; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r30 = r30 * r31;
        r0 = r30;
        r1 = r34;
        r1.videoDuration = r0;
        r34.updateVideoOriginalInfo();
        r34.updateVideoEditedInfo();
        r30 = 1;
        goto L_0x003f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VideoEditorActivity.processOpenVideo():boolean");
    }

    private int calculateEstimatedSize(float timeDelta) {
        int size = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * timeDelta);
        return size + ((size / TLRPC.MESSAGE_FLAG_EDITED) * 16);
    }
}
